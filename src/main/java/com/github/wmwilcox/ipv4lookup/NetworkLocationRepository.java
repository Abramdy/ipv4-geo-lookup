package com.github.wmwilcox.ipv4lookup;

import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * Responsible for all database interaction in regards to
 * {@link NetworkLocation}
 */
@Repository
@RequiredArgsConstructor
public class NetworkLocationRepository {
	private final JdbcTemplate jdbcTemplate;

	/**
	 * Gets total number of rows.
	 */
	public long count() {
		return jdbcTemplate.queryForObject("select count(*) from network_location", Long.class);
	}

	/**
	 * Inserts all the given collection of {@link NetworkLocation}s using a batch
	 * update.
	 */
	public void batchInsert(List<NetworkLocation> locations) {
		String sql = "INSERT INTO network_location "
				+ "(network, longitude, latitude, accuracy_radius) VALUES (?::cidr, ?, ?, ?)";

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			@SneakyThrows
			public void setValues(PreparedStatement ps, int i) {
				NetworkLocation location = locations.get(i);
				ps.setString(1, location.getNetwork());
				ps.setFloat(2, location.getLongitude());
				ps.setFloat(3, location.getLatitude());
				ps.setInt(4, location.getAccuracyRadius());
			}

			@Override
			public int getBatchSize() {
				return locations.size();
			}
		});
	}

	/**
	 * Searches for any entries where the given ipv4 address is contained by the
	 * network.
	 * 
	 * @param ipv4Address
	 * @return The associated {@link NetworkLocation} that contains the given
	 *         address. null if none are found, error if multiple found.
	 * @see <a href=
	 *      "URL#https://www.postgresql.org/docs/11/functions-net.html/">postgres
	 *      network functions</a>
	 */
	public NetworkLocation findLocation(String ipv4Address) {
		List<NetworkLocation> locations = jdbcTemplate.query("select * from network_location where network >> ?::cidr",
				new Object[] { ipv4Address }, new BeanPropertyRowMapper<>(NetworkLocation.class));
		int resultSize = locations.size();
		if (resultSize == 0) {
			return null;
		} else {
			if (resultSize == 1) {
				return locations.get(0);
			} else {
				throw new IllegalStateException(
						String.format("Search for address %s resulted in %d results", ipv4Address, resultSize));
			}
		}
	}
}

package com.github.wmwilcox.ipv4lookup;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

import javax.annotation.PostConstruct;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Responsible for parsing and loading GeoLite2 CSV inputs.
 * 
 * Parameters are found in {@link DataLoaderProperties}
 * 
 * @see <a href= "URL#https://dev.maxmind.com/geoip/geoip2/geolite2/">geolite2
 *      csv format</a>
 * @see <a href=
 *      "URL#https://dev.maxmind.com/geoip/geoip2/geoip2-city-country-csv-databases/">geoip
 *      csv format</a>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader {

	private final DataLoaderProperties properties;
	private final ResourceLoader resourceLoader;
	private final NetworkLocationRepository networkLocationRepository;

	@PostConstruct
	public void onStartup() {
		if (networkLocationRepository.count() > 0) {
			log.info("data already loaded. skipping.");
		} else {
			load();
		}
	}

	@SneakyThrows
	private void load() {
		InputStream inputStream = getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line = null;
		int count = 0;

		int batchSize = properties.getBatchSize();

		List<NetworkLocation> batch = new ArrayList<>(batchSize);

		while ((line = reader.readLine()) != null) {
			if (count == 0) {
				count++;
				continue; // skip header line
			}

			if (count % batchSize == 0) {
				log.debug("loading records: {} completed", count);
				flushBatch(batch);
			}

			try {
				NetworkLocation location = extractNetworkLocation(line);
				if (location != null) {
					batch.add(location);
					count++;
				}
			} catch (Exception t) {
				log.error("error on line " + count);
				log.error("\tcontent: {}", line);
				throw t;
			}
		}

		inputStream.close();

		flushBatch(batch);
		log.debug("complete. final count is {}", count);
	}

	private void flushBatch(List<NetworkLocation> batch) {
		networkLocationRepository.batchInsert(batch);
		batch.clear();
	}

	@SneakyThrows
	private InputStream getInputStream() {
		Resource resource = resourceLoader.getResource(properties.getSourceLocation());
		if (properties.isSourceCompressed()) {
			ZipInputStream zis = new ZipInputStream(resource.getInputStream());
			zis.getNextEntry(); // advance to first entry
			return zis;
		} else {
			return resource.getInputStream();
		}
	}

	private NetworkLocation extractNetworkLocation(String line) {
		// positional information found at
		// https://dev.maxmind.com/geoip/geoip2/geoip2-city-country-csv-databases/
		String[] row = line.split(",");

		// java string.split bizarre in that trailing empty spaces are trimmed
		// making this check necessary.
		if (row.length <= 7) {
			return null;
		}

		String network = row[0];
		Float latitude = Float.parseFloat(row[7]);
		Float longitude = Float.parseFloat(row[8]);
		;
		Integer accuracyRadius = Integer.parseInt(row[9]);

		return new NetworkLocation(network, latitude, longitude, accuracyRadius);
	}
}

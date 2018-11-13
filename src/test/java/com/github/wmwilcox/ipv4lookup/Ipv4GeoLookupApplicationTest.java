package com.github.wmwilcox.ipv4lookup;

import static org.junit.Assert.assertEquals;

import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import lombok.SneakyThrows;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
		"spring.datasource.url=jdbc:tc:postgresql:11.1://localhost/postgres",
		"spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver",
		"ipv4-geo-lookup.loader.source-compressed=false",
		"ipv4-geo-lookup.loader.source-location=classpath:data/geo-test-data.csv",
		"ipv4-geo-lookup.loaderbatch-size=2" })
public class Ipv4GeoLookupApplicationTest {

	@LocalServerPort
	private int randomServerPort;

	private String searchUrl = null;

	@Before
	public void setup() {
		searchUrl = String.format("http://localhost:%d/search", randomServerPort);
		Unirest.setObjectMapper(new UnirestObjectMapper());
	}

	@Test
	public void contextLoads() {
		// empty test -- failure indicates issue w/ loading application container.
	}

	@Test
	public void testSearchForGeoLookup_returnsExpectedSearchResults() throws UnirestException {
		Consumer<Integer> test8Block = (n) -> {
			NetworkLocation expected = new NetworkLocation(n + ".0.0.0/8", -1 * n, n, n);
			assertEquals(expected, runQuery(n + ".0.0.0"));
			assertEquals(expected, runQuery(n + ".0.0.22"));
			assertEquals(expected, runQuery(n + ".99.22.11"));
		};

		for (int i = 1; i <= 10; i++) {
			test8Block.accept(i);
		}
	}

	@Test(expected = UnirestException.class)
	public void testSearchForGeoLookup_validatesInput() {
		runQuery("not.an.ip.address");
	}

	@Test(expected = UnirestException.class)
	public void testSearchForGeoLookup_errorOnNotFound() {
		runQuery("99.0.0.1");
	}

	@SneakyThrows
	private NetworkLocation runQuery(String ipv4Address) {
		String query = searchUrl + "?ipv4Address=" + ipv4Address;
		HttpResponse<NetworkLocation> response = Unirest.get(query).asObject(NetworkLocation.class);
		return response.getBody();
	}

}

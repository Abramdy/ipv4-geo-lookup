package com.github.wmwilcox.ipv4lookup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication
public class Ipv4GeoLookupApplication {

	public static void main(String[] args) {
		SpringApplication.run(Ipv4GeoLookupApplication.class, args);
	}
}

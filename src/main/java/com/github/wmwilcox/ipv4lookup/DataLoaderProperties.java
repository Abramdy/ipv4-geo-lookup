package com.github.wmwilcox.ipv4lookup;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Encapsulates configurable properties of the loading process.
 * 
 * @see DataLoaderProperties#sourceLocation
 * @see DataLoaderProperties#batchSize
 */
@Configuration
@ConfigurationProperties(prefix = "ipv4-geo-lookup.loader", ignoreUnknownFields = false)
@Getter
@Setter
@ToString
public class DataLoaderProperties {
	/**
	 * The location of the CSV data source.
	 * 
	 * @see ResourceLoader#getResource(String)
	 */
	private String sourceLocation;
	/**
	 * Number of records used for batch inserts
	 */
	private int batchSize;
	/**
	 * Whether or not the source file is compressed. If set, the archive is expected
	 * to have only a single entry.
	 */
	private boolean sourceCompressed;
}

package com.github.wmwilcox.ipv4lookup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Encapsulates a network and its approximate geolocation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NetworkLocation {
	private String network;
	private float latitude;
	private float longitude;
	private int accuracyRadius;
}

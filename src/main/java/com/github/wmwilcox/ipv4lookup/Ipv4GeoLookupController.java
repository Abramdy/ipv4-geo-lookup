package com.github.wmwilcox.ipv4lookup;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class Ipv4GeoLookupController {

	private static final String IP_V4_REGEX = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

	private final NetworkLocationRepository networkLocationRepository;

	@GetMapping("search")
	public NetworkLocation search(
			@Valid @Pattern(regexp = IP_V4_REGEX, message = "Must be a valid ipv4 address.") @RequestParam String ipv4Address) {
		log.debug("querying for address {}", ipv4Address);
		return networkLocationRepository.findLocation(ipv4Address);
	}

}

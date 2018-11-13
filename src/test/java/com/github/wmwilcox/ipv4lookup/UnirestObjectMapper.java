package com.github.wmwilcox.ipv4lookup;

import com.mashape.unirest.http.ObjectMapper;

import lombok.SneakyThrows;

/**
 * Mapper implementaion for Unirest - useful for converting deserializing json
 * response.
 */
public class UnirestObjectMapper implements ObjectMapper {
	private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

	@SneakyThrows
	@Override
	public <T> T readValue(String value, Class<T> valueType) {
		return jacksonObjectMapper.readValue(value, valueType);
	}

	@SneakyThrows
	@Override
	public String writeValue(Object value) {
		return jacksonObjectMapper.writeValueAsString(value);
	}

}

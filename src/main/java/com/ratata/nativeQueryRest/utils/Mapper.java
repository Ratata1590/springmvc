package com.ratata.nativeQueryRest.utils;

import java.util.HashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Mapper {

	public static final ObjectMapper mapper = new ObjectMapper();
	public static final TypeReference<HashMap<String, JsonNode>> typeRef = new TypeReference<HashMap<String, JsonNode>>() {
	};

}

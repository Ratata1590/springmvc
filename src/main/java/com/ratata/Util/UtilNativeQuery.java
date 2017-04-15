package com.ratata.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class UtilNativeQuery {

	public static final ObjectMapper mapper = new ObjectMapper();
	public static final TypeReference<HashMap<String, JsonNode>> typeRef = new TypeReference<HashMap<String, JsonNode>>() {
	};

	public static List<String> arrayNodeToListString(ArrayNode arrayNode) {
		List<String> options = new ArrayList<String>();
		if (arrayNode != null) {
			for (int i = 0; i < arrayNode.size(); i++) {
				options.add(i, arrayNode.get(i).asText());
			}
		}
		return options;
	}
}

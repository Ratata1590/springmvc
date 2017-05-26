package com.ratata.nativeQueryRest.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

@RestController
public class GenTool {
	@RequestMapping(value = "/genEntityFromString", method = RequestMethod.POST)
	public String genEntityUpdateFromJsonNode(@RequestBody JsonNode data) {
		data = data.get("data");
		StringBuilder sb = new StringBuilder();
		for (JsonNode node : data) {
			node = (ArrayNode) node;
			if (node.get(0).asText().equals("String")) {
				String varName = node.get(1).asText();
				String upperName = varName.substring(0, 1).toUpperCase() + varName.substring(1);
				sb.append(String.format("if(node.has(\"%1$s\")){set%2$s(node.get(\"%1$s\").asText());}", varName,
						upperName));
			}
		}
		return sb.toString();
	}
}

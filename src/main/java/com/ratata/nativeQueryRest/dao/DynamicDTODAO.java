package com.ratata.nativeQueryRest.dao;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public interface DynamicDTODAO {
	public Object nativeWithDynamicPojo(JsonNode pojo);

	public Object nativeWithDynamicPojo(JsonNode node, JsonNode param);

	public List<Object> transationNativeQuery(String query) throws Exception;

	public List<Object> transationNativeQuery(ArrayNode query);

}

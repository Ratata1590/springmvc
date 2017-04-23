package com.ratata.NativeQuery.dao;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public interface NativeQueryDynamicPojoDAO {
	public Object nativeWithDynamicPojo(JsonNode pojo) throws Exception;

	public Object nativeWithDynamicPojo(JsonNode node, ArrayNode param) throws Exception;

	public List<Object> transationNativeQuery(String query) throws Exception;

	public List<Object> transationNativeQuery(ArrayNode query) throws Exception;

	public void processDynamicPojo(JsonNode node, ArrayNode param) throws Exception;

	public void checkNode(JsonNode node, ArrayNode paramArray, Integer objectNodeNumber) throws Exception;

}

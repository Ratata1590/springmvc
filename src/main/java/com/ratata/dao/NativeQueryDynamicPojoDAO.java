package com.ratata.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public interface NativeQueryDynamicPojoDAO {
	public Object nativeWithDynamicPojo(JsonNode pojo) throws Exception;

	public Object nativeWithDynamicPojo(JsonNode node, ArrayNode param) throws Exception;

	public void processDynamicPojo(JsonNode node, ArrayNode param) throws Exception;

	public void checkNode(JsonNode node, ArrayNode paramArray, int objectNodeNumber) throws Exception;
}

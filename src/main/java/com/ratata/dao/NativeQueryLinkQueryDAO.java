package com.ratata.dao;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface NativeQueryLinkQueryDAO {
	public Map<String, JsonNode> getQueryList();

	public void setQueryList(Map<String, JsonNode> queryList);

	public void saveQueryListFromFile() throws Exception;

	public void saveQueryList(Object query);

	public void saveQueryListToDB() throws Exception;
	
	public void updateQueryListToDB() throws Exception;

	public void persistQueryListToDB() throws Exception;

	public void syncQueryListfromDB();

	public void updateQueryList(ObjectNode query);

	public Object processCustomQuery(String queryName, String param) throws Exception;

	public Object processCustomQuery(String queryName, ArrayNode param) throws Exception;
}

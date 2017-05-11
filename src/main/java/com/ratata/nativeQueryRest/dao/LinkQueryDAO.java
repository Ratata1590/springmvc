package com.ratata.nativeQueryRest.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface LinkQueryDAO {
  public void saveQueryList(Object query);

  public void saveQueryListFromFile() throws Exception;

  public void persistQueryListToDB() throws Exception;

  public void saveQueryListToDB() throws Exception;

  public void updateQueryListToDB() throws Exception;

  public void syncQueryListfromDB();

  public void updateQueryList(ObjectNode query);

  public Object processLinkQuery(String queryName, String param) throws Exception;

  public Object processLinkQuery(String queryName, JsonNode param) throws Exception;

  public boolean isUpdateFromDB();

  public void setUpdateFromDB(boolean updateFromDB);
}

package com.ratata.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface RatataDBService {
  public Long saveNode(JsonNode object) throws Exception;

  public Object getNode(Long id, Integer type, Boolean showId, Boolean showData) throws Exception;
}

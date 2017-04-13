package com.ratata.dao;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class UtilNativeQuery {

  public static final ObjectMapper mapper = new ObjectMapper();

  public static List<String> arrayNodeToListString(ArrayNode arrayNode) {
    List<String> options = new ArrayList<String>();
    for (int i = 0; i < arrayNode.size(); i++) {
      options.add(i, arrayNode.get(i).asText());
    }
    return options;
  }
}

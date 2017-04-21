package com.ratata.controller;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ratata.Util.UtilNativeQuery;

@RestController
public class ExportDBController {

  @PersistenceContext
  private EntityManager em;
  
  public static final String[] entityList = {"Item","User","InnerItem"};

  @RequestMapping(value = "/exportTable", method = RequestMethod.GET)
  public JsonNode exportTable() {
    ObjectNode resultAndMap = UtilNativeQuery.mapper.createObjectNode();
    ObjectNode result = UtilNativeQuery.mapper.createObjectNode();
    for (String className : entityList) {
      JsonNode nodes = UtilNativeQuery.mapper.convertValue(
          em.createQuery("Select t from " + className + " t").getResultList(), JsonNode.class);
      result.set(className, nodes);
    }
    resultAndMap.set("data", result);
    return result;
  }

  @RequestMapping(value = "/importTable", method = RequestMethod.POST)
  public void importTable(@RequestParam("data") MultipartFile data,
      @RequestParam("map") MultipartFile map) throws Exception {
    JsonNode jdata = UtilNativeQuery.mapper.readTree(data.getInputStream());
    JsonNode jmap = UtilNativeQuery.mapper.readTree(map.getInputStream());
  }
}

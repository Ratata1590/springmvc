package com.ratata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.ratata.service.RatataDBService;

@RestController
public class RatataDBController {

  @Autowired
  private RatataDBService ratataDBService;

  @RequestMapping(value = "/Rsave", method = RequestMethod.POST)
  public Object saveNode(@RequestBody JsonNode object) throws Exception {
    return ratataDBService.saveNode(object);
  }

  @RequestMapping(value = "/Rget", method = RequestMethod.GET)
  public Object getNode(@RequestParam Long id, @RequestParam Integer type) throws Exception {
    return ratataDBService.getNode(id, type);
  }


}

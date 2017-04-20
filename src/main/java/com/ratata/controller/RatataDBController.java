package com.ratata.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

  @RequestMapping(value = "/RsaveFile", method = RequestMethod.POST)
  public Object saveFile(@RequestParam("file") MultipartFile[] files,
      @RequestParam("names") String[] names) throws Exception {
    return ratataDBService.uploadFile(files, names);
  }

  @RequestMapping(value = "/Rget", method = RequestMethod.GET)
  public Object getNode(@RequestParam Long id, @RequestParam Integer type,
      @RequestParam(required = false, defaultValue = "false") Boolean showId,
      @RequestParam(required = false, defaultValue = "true") Boolean showData,
      @RequestParam(required = false, defaultValue = "false") Boolean showBinary,
      @RequestParam(required = false, defaultValue = "10") Long limit) throws Exception {
    return ratataDBService.getNode(id, type, showId, showData, showBinary, limit);
  }

  @RequestMapping(value = "/RSearchObject", method = RequestMethod.POST)
  public Object searchObjectNode(@RequestBody JsonNode node,
      @RequestParam(required = false, defaultValue = "false") Boolean showId,
      @RequestParam(required = false, defaultValue = "true") Boolean showData,
      @RequestParam(required = false, defaultValue = "false") Boolean showBinary,
      @RequestParam(required = false, defaultValue = "10") Long limit) {
    return ratataDBService.searchObjectNode(node, showId, showData, showBinary, limit);
  }

  @RequestMapping(value = "/RgetFileById", method = RequestMethod.GET)
  public void getNode(@RequestParam Long id, HttpServletResponse response) throws Exception {
    ratataDBService.downloadFileById(response, id);
  }

  @RequestMapping(value = "/RgetFileByHash", method = RequestMethod.GET)
  public void getNode(@RequestParam String hash, HttpServletResponse response) throws Exception {
    ratataDBService.downloadFileByHash(response, hash);
  }

  @RequestMapping(value = "/RgetParent", method = RequestMethod.GET)
  public Object getNodeParent(@RequestParam Long id, @RequestParam Integer type,
      @RequestParam(required = false, defaultValue = "false") Boolean showId,
      @RequestParam(required = false, defaultValue = "true") Boolean showData,
      @RequestParam(required = false, defaultValue = "false") Boolean showBinary,
      @RequestParam(required = false, defaultValue = "10") Long limit) throws Exception {
    return ratataDBService.getParent(id, type, showId, showData, showBinary, limit);
  }
}

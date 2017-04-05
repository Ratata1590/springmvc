package com.ratata.controller;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ratata.LockUtil.LockUtil;
import com.ratata.dao.CustomQueryListDAO;
import com.ratata.dao.NativeQueryDAO;
import com.ratata.dao.NativeQueryDynamicPojoDAO;
import com.ratata.dao.UtilNativeQuery;

@RestController
public class DemoController {
  @Autowired
  private NativeQueryDAO nativeQueryDAO;

  @Autowired
  private NativeQueryDynamicPojoDAO nativeQueryDynamicPojoDAO;

  @Autowired
  private CustomQueryListDAO customQueryListDAO;

  @RequestMapping(value = "/nativequery", method = RequestMethod.GET)
  public Object nativeQuery(String query,
      @RequestParam(required = false, defaultValue = "") String className,
      @RequestParam(required = false, defaultValue = "") String[] resultSet,
      @RequestParam(required = false, defaultValue = "L") String queryMode,
      @RequestParam(required = false, defaultValue = "[]") String param)
      throws ClassNotFoundException, JsonProcessingException, IOException {
    ArrayNode paramNode = ((ArrayNode) UtilNativeQuery.mapper.readTree(param));
    return nativeQueryDAO.nativeQuery(query, className,
        resultSet != null ? Arrays.asList(resultSet) : null, queryMode, paramNode);
  }

  @RequestMapping(value = "/nativequery", method = RequestMethod.POST)
  public Object nativeQueryWithDynamicPoJo(@RequestBody JsonNode pojo)
      throws ClassNotFoundException, JsonProcessingException, IOException {
    return nativeQueryDynamicPojoDAO.nativeWithDynamicPojo(
        pojo.get(NativeQueryDynamicPojoDAO.PARAM_SINGLEREQUEST_DATA),
        (ArrayNode) pojo.get(NativeQueryDynamicPojoDAO.PARAM_SINGLEREQUEST_PARAM));
  }

  // ------------------------------
  @RequestMapping(value = "/SaveQueryList", method = RequestMethod.POST)
  public Object SaveQueryList(@RequestBody ObjectNode queryList) {
    customQueryListDAO.saveQueryList(queryList);
    return CustomQueryListDAO.queryList;
  }

  @RequestMapping(value = "/UpdateQueryList", method = RequestMethod.POST)
  public Object UpdateQueryList(@RequestBody ObjectNode queryList) {
    customQueryListDAO.updateQueryList(queryList);
    return CustomQueryListDAO.queryList;
  }

  @RequestMapping(value = "/GetQueryList", method = RequestMethod.GET)
  public Object GetQueryList() {
    return CustomQueryListDAO.queryList;
  }

  @RequestMapping(value = "/CustomQuery", method = RequestMethod.GET)
  public Object queryWithParam(@RequestParam String queryName,
      @RequestParam(required = false, defaultValue = "[]") String param)
      throws ClassNotFoundException, JsonProcessingException, IOException {
    if (CustomQueryListDAO.queryList == null) {
      return "please insert query list first";
    }
    ArrayNode paramNode = ((ArrayNode) UtilNativeQuery.mapper.readTree(param));
    return customQueryListDAO.processCustomQuery(queryName, paramNode);

  }

  // ------------------------
  @RequestMapping(value = "/SaveObject", method = RequestMethod.POST)
  public void saveData(@RequestBody Object obj, @RequestParam String className)
      throws IllegalArgumentException, ClassNotFoundException {
    if (LockUtil.isLockFlag()) {
      return;
    }
    nativeQueryDAO.saveObject(obj, className);
  }

  @RequestMapping(value = "/lock", method = RequestMethod.GET)
  public String lockOption(@RequestParam String password, String hint)
      throws NoSuchAlgorithmException {
    return LockUtil.lock(password, hint);
  }

  @RequestMapping(value = "/unlock", method = RequestMethod.GET)
  public String unlockOption(@RequestParam String key) {
    return LockUtil.unlock(key);
  }

  @PostConstruct
  public void InitQueryList() throws JsonProcessingException, IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource("initQueryList.txt").getFile());
    customQueryListDAO.saveQueryList(UtilNativeQuery.mapper.readTree(file));
  }
}

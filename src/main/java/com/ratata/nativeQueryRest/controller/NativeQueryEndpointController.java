package com.ratata.nativeQueryRest.controller;

import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ratata.nativeQueryRest.dao.CoreDAO;
import com.ratata.nativeQueryRest.dao.DynamicDTODAO;
import com.ratata.nativeQueryRest.dao.LinkQueryDAO;
import com.ratata.nativeQueryRest.pojo.NativeQueryParam;
import com.ratata.nativeQueryRest.pojo.QueryListHolder;
import com.ratata.nativeQueryRest.utils.Const;
import com.ratata.nativeQueryRest.utils.LockUtil;
import com.ratata.nativeQueryRest.utils.Mapper;

@RestController
public class NativeQueryEndpointController {
  @Autowired
  private CoreDAO coreDAO;

  @Autowired
  private DynamicDTODAO dynamicDTODAO;

  @Autowired
  private LinkQueryDAO linkQueryDAO;

  // ------------------------------NativeQueryDAO
  @RequestMapping(value = "/directQuery", method = RequestMethod.GET)
  public Object nativeQuery(@RequestParam String query,
      @RequestHeader(required = false, defaultValue = "") String className,
      @RequestHeader(required = false, defaultValue = "") String[] resultSet,
      @RequestHeader(required = false, defaultValue = "L") String queryMode,
      @RequestHeader(required = false, defaultValue = "{}") String param,
      @RequestHeader(required = false, defaultValue = "true") Boolean isNative,
      @RequestHeader(required = false, defaultValue = "0") Integer lockMode,
      @RequestHeader(required = false, defaultValue = "0") Integer offset,
      @RequestHeader(required = false, defaultValue = "0") Integer limit) throws Exception {
    if (LockUtil.isLockFlag() && LockUtil.lockList.get("/directQuery")) {
      return Const.LOCK_MESSAGE;
    }
    return coreDAO.nativeQuery(new NativeQueryParam(query, className, resultSet, queryMode, param,
        isNative, lockMode, offset, limit));
  }

  @RequestMapping(value = "/directQueryToQueryObject", method = RequestMethod.GET)
  public Object directQueryToQueryObject(@RequestParam String query,
      @RequestHeader(required = false, defaultValue = "") String className,
      @RequestHeader(required = false, defaultValue = "") String[] resultSet,
      @RequestHeader(required = false, defaultValue = "L") String queryMode,
      @RequestHeader(required = false, defaultValue = "{}") String param,
      @RequestHeader(required = false, defaultValue = "true") Boolean isNative,
      @RequestHeader(required = false, defaultValue = "0") Integer lockMode,
      @RequestHeader(required = false, defaultValue = "0") Integer offset,
      @RequestHeader(required = false, defaultValue = "0") Integer limit) throws Exception {
    if (LockUtil.isLockFlag() && LockUtil.lockList.get("/directQueryToQueryObject")) {
      return Const.LOCK_MESSAGE;
    }
    return new NativeQueryParam(query, className, resultSet, queryMode, param, isNative, lockMode,
        offset, limit);
  }

  // entity map
  @RequestMapping(value = "/getEntityMapDetail", method = RequestMethod.GET)
  public Object getEntityMapDetail(@RequestParam String className) throws Exception {
    if (LockUtil.isLockFlag() && LockUtil.lockList.get("/getEntityMapDetail")) {
      return Const.LOCK_MESSAGE;
    }
    return Class.forName(className).getDeclaredFields();
  }

  @RequestMapping(value = "/getEntityMap", method = RequestMethod.GET)
  public Object getEntityMap(@RequestParam String className) throws Exception {
    if (LockUtil.isLockFlag() && LockUtil.lockList.get("/getEntityMap")) {
      return Const.LOCK_MESSAGE;
    }
    ObjectNode node = Mapper.mapper.createObjectNode();
    Field[] fields = Class.forName(className).getDeclaredFields();
    for (Field field : fields) {
      node.put(field.getName(), field.getAnnotatedType().getType().getTypeName());
    }
    return node;
  }

  @RequestMapping(value = "/initObject", method = RequestMethod.GET)
  public Object initData(@RequestHeader String className) throws Exception {
    if (LockUtil.isLockFlag() && LockUtil.lockList.get("/initObject")) {
      return Const.LOCK_MESSAGE;
    }
    ObjectNode node = Mapper.mapper.valueToTree(Class.forName(className).newInstance());
    node.remove("id");
    return node;
  }

  @RequestMapping(value = "/saveObject", method = RequestMethod.POST)
  public String saveData(@RequestBody JsonNode obj, @RequestHeader String className)
      throws Exception {
    if (LockUtil.isLockFlag() && LockUtil.lockList.get("/saveObject")) {
      return Const.LOCK_MESSAGE;
    }
    coreDAO.saveObject(obj, className);
    return null;
  }

  @RequestMapping(value = "/linkObject", method = RequestMethod.POST)
  public Object saveLinkedData(@RequestBody JsonNode obj) throws Exception {
    if (LockUtil.isLockFlag() && LockUtil.lockList.get("/linkObject")) {
      return Const.LOCK_MESSAGE;
    }
    return coreDAO.linkObject(obj);
  }

  // ------------------------------NativeQueryDynamicPojoDAO
  @RequestMapping(value = "/nativeQueryJson", method = RequestMethod.POST)
  public Object nativeQueryWithDynamicPoJoPost(@RequestBody JsonNode pojo) throws Exception {
    if (LockUtil.isLockFlag() && LockUtil.lockList.get("/nativeQueryJson_POST")) {
      return Const.LOCK_MESSAGE;
    }
    return dynamicDTODAO.nativeWithDynamicPojo(pojo);
  }

  @RequestMapping(value = "/nativeQueryJson", method = RequestMethod.GET)
  public Object nativeQueryWithDynamicPoJoGet(@RequestHeader String query) throws Exception {
    if (LockUtil.isLockFlag() && LockUtil.lockList.get("/nativeQueryJson_GET")) {
      return Const.LOCK_MESSAGE;
    }
    return dynamicDTODAO.nativeWithDynamicPojo(Mapper.mapper.readTree(query));
  }

  // ------------------------------NativeQueryTransaction
  @RequestMapping(value = "/nativeQueryTransaction", method = RequestMethod.GET)
  public Object nativeQueryTransactionGet(@RequestHeader String query) throws Exception {
    if (LockUtil.isLockFlag() && LockUtil.lockList.get("/nativeQueryTransaction_GET")) {
      return Const.LOCK_MESSAGE;
    }
    return dynamicDTODAO.transationNativeQuery(query);
  }

  @RequestMapping(value = "/nativeQueryTransaction", method = RequestMethod.POST)
  public Object nativeQueryTransactionPost(@RequestBody ArrayNode query) throws Exception {
    if (LockUtil.isLockFlag() && LockUtil.lockList.get("/nativeQueryTransaction_POST")) {
      return Const.LOCK_MESSAGE;
    }
    return dynamicDTODAO.transationNativeQuery(query);
  }

  // ------------------------------CustomQueryListDAO
  @RequestMapping(value = "/saveQueryList", method = RequestMethod.POST)
  public Object SaveQueryList(@RequestBody ObjectNode queryList) throws Exception {
    if (LockUtil.isLockFlag() && LockUtil.lockList.get("/saveQueryList")) {
      return Const.LOCK_MESSAGE;
    }
    linkQueryDAO.saveQueryList(queryList);
    linkQueryDAO.saveQueryListToDB();
    return QueryListHolder.queryList;
  }

  @RequestMapping(value = "/updateQueryList", method = RequestMethod.POST)
  public Object UpdateQueryList(@RequestBody ObjectNode queryList) throws Exception {
    if (LockUtil.isLockFlag() && LockUtil.lockList.get("/updateQueryList")) {
      return Const.LOCK_MESSAGE;
    }
    linkQueryDAO.updateQueryList(queryList);
    linkQueryDAO.updateQueryListToDB();
    return QueryListHolder.queryList;
  }

  @RequestMapping(value = "/getQueryList", method = RequestMethod.GET)
  public Object GetQueryList() {
    if (LockUtil.isLockFlag() && LockUtil.lockList.get("/getQueryList")) {
      return Const.LOCK_MESSAGE;
    }
    return QueryListHolder.queryList;
  }

  @RequestMapping(value = "/customQuery/{queryName}", method = RequestMethod.GET)
  public Object queryWithParam(@PathVariable("queryName") String queryName,
      @RequestHeader(defaultValue = "{}") String param) throws Exception {
    if (LockUtil.isLockFlag() && LockUtil.lockList.get("/customQuery")) {
      return Const.LOCK_MESSAGE;
    }
    return linkQueryDAO.processLinkQuery(queryName, param);
  }

  @RequestMapping(value = "/updateFromDb", method = RequestMethod.GET)
  public boolean configFromDB(@RequestParam(required = false) Boolean option) {
    if (option != null) {
      linkQueryDAO.setUpdateFromDB(option);
    }
    return linkQueryDAO.isUpdateFromDB();
  }

  @Scheduled(fixedRate = 10000)
  public void SyncDbQueryList() {
    if (linkQueryDAO.isUpdateFromDB()) {
      linkQueryDAO.syncQueryListfromDB();
    }
  }

  @PostConstruct
  public void InitQueryList() throws Exception {
    linkQueryDAO.saveQueryListFromFile();
    linkQueryDAO.persistQueryListToDB();
    LockUtil.initLockList();
  }

  // ------------------------------LockUtil
  @RequestMapping(value = "/lock", method = RequestMethod.POST)
  public String lockOption(@RequestHeader String password, @RequestHeader String hint,
      @RequestBody Map<String, Boolean> lockOption) throws NoSuchAlgorithmException {
    LockUtil.lockList = lockOption;
    return LockUtil.lock(password, hint);
  }

  @RequestMapping(value = "/unlock", method = RequestMethod.GET)
  public String unlockOption(@RequestHeader String key) {
    return LockUtil.unlock(key);
  }

  @RequestMapping(value = "/lockStatus", method = RequestMethod.GET)
  public Object getLockStatus() throws NoSuchAlgorithmException {
    return LockUtil.lockList;
  }

  // other util
  @SuppressWarnings("unchecked")
  @RequestMapping(value = "/tableFieldsToResultset", method = RequestMethod.GET)
  public Object getColumnsAsResultset(@RequestParam String queryShow,
      @RequestParam(required = false, defaultValue = "0") Integer fieldNumber) throws Exception {
    if (LockUtil.isLockFlag() && LockUtil.lockList.get("/tableFieldsToResultset")) {
      return Const.LOCK_MESSAGE;
    }
    NativeQueryParam query = new NativeQueryParam();
    query.setQuery(queryShow);
    List<Object[]> result = (List<Object[]>) coreDAO.nativeQuery(query);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < result.size(); i++) {
      sb.append((String) result.get(i)[fieldNumber]);
      if (i == result.size() - 1) {
        break;
      }
      sb.append(",");
    }
    return sb.toString();
  }
}

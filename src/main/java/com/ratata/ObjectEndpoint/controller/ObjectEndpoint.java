package com.ratata.ObjectEndpoint.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ratata.ObjectEndpoint.pojo.ActionObjectType;
import com.ratata.ObjectEndpoint.pojo.ObjectContainer;
import com.ratata.ObjectEndpoint.utils.ObjectContainerUtil;
import com.ratata.nativeQueryRest.utils.Mapper;

@RestController
public class ObjectEndpoint {
  private static Map<Integer, ObjectContainer> listObject = new HashMap<Integer, ObjectContainer>();

  @RequestMapping(value = "/listObject", method = RequestMethod.GET)
  public Object listObject() {
    return listObject;
  }

  @RequestMapping(value = "/checkDataType", method = RequestMethod.POST)
  public Object checkDataType(@RequestBody JsonNode dataObj) {
    return new ObjectContainer(Mapper.mapper.convertValue(dataObj, Object.class));
  }

  @RequestMapping(value = "/createDataObject", method = RequestMethod.POST)
  public Object createDataObject(@RequestBody JsonNode dataObj) {
    ObjectContainer data = Mapper.mapper.convertValue(dataObj, ObjectContainer.class);
    listObject.put(data.hashCode(), data);
    return data.hashCode();
  }

  @RequestMapping(value = "/convertObject", method = RequestMethod.GET)
  public Object convertObject(@RequestHeader(required = true) Integer objectId,
      @RequestHeader(required = true) String typeName, @RequestHeader String methodName)
      throws Exception {
    ObjectContainer obj = listObject.get(objectId);
    obj.setObj(ObjectContainerUtil.convert(typeName, methodName, obj.getObj()));
    return obj.hashCode();
  }

  @SuppressWarnings("unchecked")
  @RequestMapping(value = "/convertObjectParam", method = RequestMethod.POST)
  public Object arrayParam(@RequestBody JsonNode param) throws Exception {
    List<ObjectContainer> templistObject = new ArrayList<ObjectContainer>();
    ArrayList<Object> list = (ArrayList<Object>) Mapper.mapper.convertValue(param, ArrayList.class);
    for (Object obj : list) {
      ObjectContainer objc;
      if (obj instanceof LinkedHashMap) {
        objc = ObjectContainerUtil.convertFromLinkedHashMap((Map<String, Object>) obj);
      } else {
        objc = new ObjectContainer(obj);
      }
      templistObject.add(objc);
    }
    return templistObject;
  }

  @RequestMapping(value = "/resolveAction", method = RequestMethod.POST)
  public Object resolveAction(@RequestBody JsonNode actionNode) {
    if (actionNode.isArray()) {
      LinkedList<Object> result = new LinkedList<Object>();
      for (JsonNode node : actionNode) {
        result.add(resolveActionNode(node));
      }
      return result;
    }
    return resolveActionNode(actionNode);
  }

  private Object resolveActionNode(JsonNode node) {
    switch (node.get(ActionObjectType.ACTIONTYPE).asInt()) {
      case ActionObjectType.CONSTRUCTOR:
        node = ((ObjectNode) node);
        node.
        return null;
      case ActionObjectType.METHOD:
        return null;
      case ActionObjectType.PROPERTY:
        return null;
      case ActionObjectType.LINK:
        return null;
      case ActionObjectType.CONVERT:
        return null;
    }
    return Mapper.mapper.convertValue(node, Object.class);
  }

  // @RequestMapping(value = "/createObject", method = RequestMethod.POST)
  // public Object createObject(@RequestHeader String className, @RequestBody JsonNode param)
  // throws Exception {
  // List<Class<?>> paramTypeList = new ArrayList<Class<?>>();
  // List<Object> paramObjectList = new ArrayList<Object>();
  // if (paramType != null) {
  // for (int i = 0; i < paramType.length; i++) {
  // paramTypeList.add(Class.forName(paramType[i]));
  // paramObjectList.add(listObject.get(paramObject[i]).getObj());
  // }
  // }
  // Object obj = Class.forName(className)
  // .getConstructor(paramTypeList.toArray(new Class<?>[paramTypeList.size()]))
  // .newInstance(paramObjectList.toArray(new Object[paramObjectList.size()]));
  // String objectId = genObjectId(obj);
  // listObject.put(objectId, new ObjectContainer(obj));
  // return objectId;
  // }
  //
  // @RequestMapping(value = "/callMethodObject", method = RequestMethod.POST)
  // public Object callMethodObject(@RequestBody JsonNode dataObj,
  // @RequestHeader(defaultValue = "false") Boolean storeReturn,
  // @RequestHeader(required=true) String objectId,
  // @RequestHeader(required=true) String methodName,
  // @RequestHeader(defaultValue="false") Boolean isStaticMethod){
  // List<Class<?>> paramTypeList = new ArrayList<Class<?>>();
  // Object obj = listObject.get(objectId);
  // Object result;
  // if(isStaticMethod){
  // result = obj.getClass().getMethod(methodName, new
  // Class<?>[paramTypeList.size()]).invoke(null,);
  // }else{
  //
  // }
  // if(storeReturn){
  // return
  // }
  // return objectId;
  // }
  //
  //
  // private List<Class<?>> jsonNodeToparamTypeList(ArrayNode param) {
  // for (JsonNode node : param) {
  // node.getNodeType().toString();
  // }
  // }
}

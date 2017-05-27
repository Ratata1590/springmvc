package com.ratata.ObjectEndpoint.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.mdkt.compiler.InMemoryJavaCompiler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.ratata.ObjectEndpoint.pojo.ObjectContainer;
import com.ratata.nativeQueryRest.utils.Mapper;

@RestController
public class InMemoryClass {
  public static Map<String, Class<?>> classList = new ConcurrentHashMap<String, Class<?>>();

  public static Map<String, Object> objList = new ConcurrentHashMap<String, Object>();

  @RequestMapping(value = "/checkDataType", method = RequestMethod.POST)
  public Object checkDataType(@RequestBody JsonNode dataObj) {
    return new ObjectContainer(Mapper.mapper.convertValue(dataObj, Object.class));
  }

  @RequestMapping(value = "/newClass", method = RequestMethod.POST)
  public void newClass(@RequestBody String classbody, @RequestHeader String className)
      throws Exception {
    Class<?> theClass = InMemoryJavaCompiler.compile(className, classbody);
    theClass.getMethod("loadAllClass").invoke(null);
    if (classList.containsKey(className)) {
      removeClass(className);
    }
    classList.put(className, theClass);
    System.gc();
  }

  @RequestMapping(value = "/removeClass", method = RequestMethod.GET)
  public void removeClass(@RequestHeader String className) throws Exception {
    classList.remove(className);
    for (String obj : objList.keySet()) {
      if (obj.startsWith(className.concat(":"))) {
        objList.remove(obj);
      }
    }
    System.gc();
  }

  @RequestMapping(value = "/callClassMethod", method = RequestMethod.POST)
  public Object callClassMethod(@RequestHeader(required = true) String methodName,
      @RequestHeader(required = true) String className, @RequestBody Object... param)
      throws Exception {
    return classList.get(className).getMethod(methodName, revolseObjectParamType(param))
        .invoke(null, param);
  }

  @RequestMapping(value = "/classList", method = RequestMethod.GET)
  public Object classList() throws Exception {
    return classList.keySet();
  }

  @RequestMapping(value = "/newObj", method = RequestMethod.GET)
  public String newObj(@RequestHeader String className) throws Exception {
    Object ob = classList.get(className).getConstructor(ClassLoader.class, Map.class, Map.class)
        .newInstance(Thread.currentThread().getContextClassLoader(), classList, objList);
    String instanceId = className + ":" + ob.hashCode();
    objList.put(instanceId, ob);
    System.gc();
    return instanceId;
  }

  @RequestMapping(value = "/removeObj", method = RequestMethod.GET)
  public void removeObj(@RequestHeader(required = true) String instanceId) throws Exception {
    objList.remove(instanceId);
    System.gc();
  }

  @RequestMapping(value = "/objList", method = RequestMethod.GET)
  public Object objList() throws Exception {
    return objList.keySet();
  }

  @RequestMapping(value = "/callObjMethod", method = RequestMethod.POST)
  public Object callObjMethod(@RequestHeader(required = true) String instanceId,
      @RequestHeader(required = true) String methodName, @RequestBody Object... param)
      throws Exception {
    Object obj = objList.get(instanceId);
    return obj.getClass().getMethod(methodName, revolseObjectParamType(param)).invoke(obj, param);
  }

  private Class<?>[] revolseObjectParamType(Object... param) {
    Class<?>[] classTypeList = new Class<?>[param.length];
    for (int i = 0; i < param.length; i++) {
      classTypeList[i] = param.getClass();
    }
    return classTypeList;
  }
}

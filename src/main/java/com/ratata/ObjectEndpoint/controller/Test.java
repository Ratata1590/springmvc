package com.ratata.ObjectEndpoint.controller;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Test {
  private static ClassLoader classLoader;
  private static Map<String, Class<?>> classList;
  private static Map<String, Object> objList;

  // logging
  private ByteArrayOutputStream baos = new ByteArrayOutputStream();
  private PrintStream log = new PrintStream(baos);

  public String getLog() {
    return new String(baos.toByteArray(), StandardCharsets.UTF_8);
  }

  public void clearLog() {
    baos.reset();
  }

  public static final int className = 0;
  public static final int getClass = 1;

  public static void loadAllClass() throws Exception {
    for (Object[] obj : classNameList) {
      obj[getClass] = classLoader.loadClass((String) obj[className]);
    }
  }

  public static Class<?>[] revolseObjectParamType(Object... param) {
    Class<?>[] classTypeList = new Class<?>[param.length];
    for (int i = 0; i < param.length; i++) {
      classTypeList[i] = param.getClass();
    }
    return classTypeList;
  }

  public static Object newObj(Object[] theClass, Object... param) throws Exception {
    return ((Class<?>) theClass[getClass]).getConstructor(revolseObjectParamType(param))
        .newInstance(param);
  }

  public static Object getStaticProp(Object[] theClass, String propName) throws Exception {
    return ((Class<?>) theClass[getClass]).getDeclaredField(propName).get(null);
  }

  public static void setStaticProp(Object[] theClass, String propName, Object value)
      throws Exception {
    ((Class<?>) theClass[getClass]).getDeclaredField(propName).set(null, value);
  }

  public static Object getObjProp(Object obj, String propName) throws Exception {
    return obj.getClass().getDeclaredField(propName).get(obj);
  }

  public static void setObjProp(Object obj, String propName, Object value) throws Exception {
    obj.getClass().getDeclaredField(propName).set(obj, value);
  }

  public static Object callStaticMethod(Object[] theClass, String methodName, Object... param)
      throws Exception {
    return ((Class<?>) theClass[getClass]).getMethod(methodName, revolseObjectParamType(param))
        .invoke(null, param);
  }

  public static Object callObjMethod(Object obj, String methodName, Object... param)
      throws Exception {
    return obj.getClass().getMethod(methodName, revolseObjectParamType(param)).invoke(obj, param);
  }

  // import class
  public static final Object[] ObjectMapper = {"com.fasterxml.jackson.databind.ObjectMapper", null};
  public static final Object[] JsonNode = {"com.fasterxml.jackson.databind.JsonNode", null};
  public static final Object[] ArrayNode = {"com.fasterxml.jackson.databind.node.ArrayNode", null};
  public static final Object[] Mapper = {"com.ratata.nativeQueryRest.utils.Mapper", null};
  public static final Object[] ShareResourceFromSpring =
      {"com.ratata.ObjectEndpoint.controller.ShareResourceFromSpring", null};
  public final static Object[][] classNameList =
      {ObjectMapper, JsonNode, ArrayNode, Mapper, ShareResourceFromSpring};

  // main code

  public void hello() throws Exception {
    Object om = getStaticProp(Mapper, "mapper");
    Object jsonNode = newObj(ObjectMapper);
    log.println(jsonNode.getClass().getTypeName());
  }

  public static void haha() throws Exception {
    Object om = getStaticProp(ShareResourceFromSpring, "mapper");
  }
}

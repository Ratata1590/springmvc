package com.ratata.ObjectEndpoint.utils;

import java.util.Map;

import com.ratata.ObjectEndpoint.pojo.ObjectContainer;

public class ObjectContainerUtil {
  public static Object convert(String type, String method, Object obj) throws Exception {
    method = method == null ? "valueOf" : method;
    return Class.forName(type).getMethod(method, String.class).invoke(null, obj.toString());
  }

  public static String OBJECT_TYPE_FORMAT_OBJ = "obj";
  public static String OBJECT_TYPE_FORMAT_OBJTYPE = "objType";
  public static String OBJECT_TYPE_FORMAT_METHOD = "method";

  public static ObjectContainer convertFromLinkedHashMap(Map<String, Object> obj) throws Exception {
    ObjectContainer objc =
        new ObjectContainer(((Map<String, Object>) obj).get(OBJECT_TYPE_FORMAT_OBJ));
    objc.setObj(ObjectContainerUtil.convert(
        (String) ((Map<String, Object>) obj).get(OBJECT_TYPE_FORMAT_OBJTYPE),
        (String) ((Map<String, Object>) obj).get(OBJECT_TYPE_FORMAT_METHOD), objc.getObj()));
    return objc;
  }
}

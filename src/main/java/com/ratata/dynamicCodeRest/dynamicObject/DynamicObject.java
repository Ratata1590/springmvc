package com.ratata.dynamicCodeRest.dynamicObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class DynamicObject {

  public final Map<String, Field> fields = new HashMap<String, Field>();
  public final Map<String, Method> methods = new HashMap<String, Method>();

  public DynamicObject(String centralObjectName, ClassLoader classLoader) throws Exception {
    Class<?> theClass = classLoader.loadClass(centralObjectName);
    for (Method method : theClass.getMethods()) {
      methods.put(method.getName(), method);
    }
    for (Field field : theClass.getFields()) {
      fields.put(field.getName(), field.);
    }
  }

}

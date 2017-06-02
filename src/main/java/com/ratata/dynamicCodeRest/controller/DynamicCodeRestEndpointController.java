package com.ratata.dynamicCodeRest.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.mdkt.compiler.InMemoryJavaCompiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ratata.dynamicCodeRest.dynamicObject.DynamicObject;
import com.ratata.dynamicCodeRest.utils.DynamicCodeUtil;
import com.ratata.dynamicCodeRest.utils.ShareResourceFromSpringInterface;

@RestController
public class DynamicCodeRestEndpointController {
  @Autowired
  private ShareResourceFromSpringInterface shareResourceFromSpring;

  @PostConstruct
  private void initResource() {
    shareResourceFromSpring.loadAllSharedObj();
  }

  public static Map<String, String> sourceCodeList = new ConcurrentHashMap<String, String>();

  public static Map<String, Class<?>> classList = new ConcurrentHashMap<String, Class<?>>();

  public static Map<String, DynamicObject> objList = new ConcurrentHashMap<String, DynamicObject>();

  @RequestMapping(value = "/newClass", method = RequestMethod.POST)
  public void newClass(@RequestBody String classbody, @RequestHeader String className,
      @RequestHeader(required = false, defaultValue = "true") Boolean saveSource) throws Exception {
    Class<?> theClass = InMemoryJavaCompiler.compile(className, classbody);
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    DynamicCodeUtil.loadAllClass(theClass, classLoader);
    if (classList.containsKey(className)) {
      removeClass(className);
    }
    classList.put(className, theClass);
    if (saveSource) {
      sourceCodeList.put(className, classbody);
    }
    System.gc();
  }

  @RequestMapping(value = "/removeClass", method = RequestMethod.GET)
  public void removeClass(@RequestHeader String className) throws Exception {
    classList.remove(className);
    sourceCodeList.remove(className);
    for (String obj : objList.keySet()) {
      if (obj.startsWith(className.concat(DynamicObject.SEPARATOR))) {
        objList.remove(obj);
      }
      if (obj.startsWith(className.concat(DynamicObject.CLASS_SEPARATOR))) {
        objList.remove(obj);
      }
    }
    System.gc();
  }

  @RequestMapping(value = "/callClassMethod", method = RequestMethod.POST)
  public Object callClassMethod(@RequestHeader(required = true) String className,
      @RequestHeader(required = true) String methodName,
      @RequestHeader(required = false, defaultValue = "false") Boolean download,
      HttpServletResponse response, @RequestBody Object... param) throws Exception {
    Object result = DynamicObject.callClassMethod(classList.get(className), methodName, param);
    if (download) {
      serveDownload(result, response);
      return null;
    }
    return result;
  }

  @RequestMapping(value = "/classList", method = RequestMethod.GET)
  public Object classList(
      @RequestHeader(required = false, defaultValue = "false") Boolean showSource)
      throws Exception {
    ArrayList<Object> result = new ArrayList<Object>();
    if (showSource) {
      result.add(sourceCodeList);
    }
    String[] clazzList = classList.keySet().toArray(new String[classList.keySet().size()]);
    Arrays.sort(clazzList);
    result.add(clazzList);
    return result;
  }

  @RequestMapping(value = "/newObj", method = RequestMethod.GET)
  public String newObj(@RequestHeader String className) throws Exception {
    Object ob = classList.get(className).getConstructor().newInstance();
    String instanceId = className + DynamicObject.SEPARATOR + ob.hashCode();
    objList.put(instanceId, new DynamicObject(ob, instanceId));
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
    String[] result = objList.keySet().toArray(new String[objList.keySet().size()]);
    Arrays.sort(result);
    return result;
  }

  @RequestMapping(value = "/callObjMethod", method = RequestMethod.POST)
  public Object callObjMethod(@RequestHeader(required = true) String instanceId,
      @RequestHeader(required = true) String methodName,
      @RequestHeader(required = false, defaultValue = "false") Boolean download,
      HttpServletResponse response, @RequestBody Object... param) throws Exception {
    DynamicObject obj = objList.get(instanceId);
    Object result = obj.callObjMethod(methodName, param);
    if (download) {
      serveDownload(result, response);
      return null;
    }
    return result;
  }

  private void serveDownload(Object obj, HttpServletResponse response) throws Exception {
    response.setContentType("application/x-msdownload");
    response.setHeader("Content-disposition", "attachment; filename=result");
    IOUtils.copy((InputStream) obj, response.getOutputStream());
  }

}

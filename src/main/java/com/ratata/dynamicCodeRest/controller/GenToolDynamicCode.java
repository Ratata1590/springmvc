package com.ratata.dynamicCodeRest.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.ratata.dynamicCodeRest.dynamicObject.ObjectContainer;
import com.ratata.dynamicCodeRest.utils.ThreadUtils;

@RestController
public class GenToolDynamicCode {
  public static final String importList = "importList";
  public static final String autowireList = "autowireList";
  public static final String importString = "import ";
  public static final ObjectMapper mapper = new ObjectMapper();

  @RequestMapping(value = "/checkInputType", method = RequestMethod.POST)
  public Object checkInputType(@RequestBody Object data) {
    return new ObjectContainer(data);
  }

  @RequestMapping(value = "/genDynamicCodeTemplate", method = RequestMethod.POST)
  public String genDynamicCodeTemplate(@RequestBody JsonNode data) {
    StringBuilder result = new StringBuilder();
    if (data.get(importList) != null) {
      String impList = data.get(importList).textValue();
      for (String imp : impList.split(";")) {
        String classNameFull = imp.substring(importString.length(), imp.length());
        String classNameShort =
            new String(classNameFull).substring(classNameFull.lastIndexOf(".") + 1);
        result.append(String.format("public static Object $C_%1$s = \"%2$s\";\n", classNameShort,
            classNameFull));
      }
      result.append("\n");
    }
    if (data.get(autowireList) != null) {
      String auList = data.get(autowireList).textValue();
      for (String imp : auList.split(";")) {
        String classNameFull = imp.substring(importString.length(), imp.length());
        String classNameShort =
            new String(classNameFull).substring(classNameFull.lastIndexOf(".") + 1);
        result.append(String.format("public static Object $Sp_%1$s = \"%2$s\";\n", classNameShort,
            classNameFull));
      }
    }
    return result.toString();
  }

  private final XmlMapper xmlMapper = new XmlMapper();

  @RequestMapping(value = "/convertMavenToJson", method = RequestMethod.POST)
  public Object convertMavenToJson(@RequestBody String mavenDependencies) throws Exception {
    return xmlMapper.readTree(mavenDependencies);
  }

  /**
   * junit,junit,3.8.1,junit-3.8.1.jar
   * org,springframework,spring-webmvc,4.0.1.RELEASE,spring-webmvc-4.0.1.RELEASE.jar
   * org,springframework,spring-beans,4.0.1.RELEASE,spring-beans-4.0.1.RELEASE.jar
   * org,springframework,spring-context,4.0.1.RELEASE,spring-context-4.0.1.RELEASE.jar
   * org,springframework,spring-core,4.0.1.RELEASE,spring-core-4.0.1.RELEASE.jar
   **/
  @RequestMapping(value = "/convertJarListToJson", method = RequestMethod.POST)
  public Object convertJarListToJson(@RequestBody String jarList) throws Exception {
    ArrayNode result = mapper.createArrayNode();
    String[] lines = jarList.split("\r\n");
    for (String line : lines) {
      ObjectNode item = mapper.createObjectNode();
      String[] lineItems = line.split(",");
      List<String> groupId = Arrays.asList(Arrays.copyOfRange(lineItems, 0, lineItems.length - 3));
      item.put(MavenRepoEndpointController.keyGroupId, String.join(".", groupId));
      item.put(MavenRepoEndpointController.keyArtifactId, lineItems[lineItems.length - 3]);
      item.put(MavenRepoEndpointController.keyVersion, lineItems[lineItems.length - 2]);
      result.add(item);
    }
    return result;
  }

  @RequestMapping(value = "/getAllThead", method = RequestMethod.GET)
  public Object getAllThead() throws Exception {
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("getAllDaemonThreads", ThreadUtils.getAllThreadInfos());
    return result;
  }

  @RequestMapping(value = "/freeMem", method = RequestMethod.GET)
  public Object freeMem() throws Exception {
    ObjectNode node = mapper.createObjectNode();
    node.put("total", String.valueOf(Runtime.getRuntime().totalMemory()) + "/"
        + String.valueOf(Runtime.getRuntime().maxMemory()));
    node.put("freed", String.valueOf(Runtime.getRuntime().freeMemory()));
    node.put("processor", Runtime.getRuntime().availableProcessors());
    return node;
  }
}

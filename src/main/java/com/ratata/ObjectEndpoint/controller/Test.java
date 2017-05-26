package com.ratata.ObjectEndpoint.controller;

import java.util.HashMap;
import java.util.Map;

public class Test {
	public ClassLoader classLoader;
	public final String ObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper";
	public final String JsonNode = "com.fasterxml.jackson.databind.JsonNode";
	public final String ArrayNode = "com.fasterxml.jackson.databind.node.ArrayNode";
	public final String Mapper = "com.ratata.nativeQueryRest.utils.Mapper";
	public final String[] classNameList = { ObjectMapper, JsonNode, ArrayNode, Mapper };
	public Map<String, Class<?>> classList = new HashMap<String, Class<?>>();

	public void loadAll() throws Exception {
		for (String str : classNameList) {
			classList.put(str, classLoader.loadClass(str));
		}
	}

	public Object newClass(String className, Object... param) throws Exception {
		Class<?>[] classTypeList = new Class<?>[param.length];
		for (int i = 0; i < param.length; i++) {
			classTypeList[i] = param.getClass();
		}
		return classList.get(className).getConstructor(classTypeList).newInstance(param);
	}

	public Object getStaticProp(String className, String propName) throws Exception {
		return classList.get(className).getDeclaredField(propName).get(null);
	}

	public Test(ClassLoader classLoader) throws Exception {
		super();
		this.classLoader = classLoader;
		loadAll();
	}

	public void hello() throws Exception {
		Object om = getStaticProp(Mapper, "mapper");
		Object jsonNode = newClass(ObjectMapper);
		System.out.println(jsonNode.getClass().getTypeName());
	}
}

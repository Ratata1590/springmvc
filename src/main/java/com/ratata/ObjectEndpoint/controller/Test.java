package com.ratata.ObjectEndpoint.controller;

public class Test {
	public ClassLoader classLoader;
	public final int className = 0;
	public final int getClass = 1;
	public final Object[] ObjectMapper = { "com.fasterxml.jackson.databind.ObjectMapper", null };
	public final Object[] JsonNode = { "com.fasterxml.jackson.databind.JsonNode", null };
	public final Object[] ArrayNode = { "com.fasterxml.jackson.databind.node.ArrayNode", null };
	public final Object[] Mapper = { "com.ratata.nativeQueryRest.utils.Mapper", null };
	public final Object[][] classNameList = { ObjectMapper, JsonNode, ArrayNode, Mapper };

	public void loadAll() throws Exception {
		for (Object[] obj : classNameList) {
			obj[getClass] = classLoader.loadClass((String) obj[className]);
		}
	}

	public Object newClass(Object[] theClass, Object... param) throws Exception {
		Class<?>[] classTypeList = new Class<?>[param.length];
		for (int i = 0; i < param.length; i++) {
			classTypeList[i] = param.getClass();
		}
		return ((Class<?>) theClass[getClass]).getConstructor(classTypeList).newInstance(param);
	}

	public Object getStaticProp(Object[] theClass, String propName) throws Exception {
		return ((Class<?>) theClass[getClass]).getDeclaredField(propName).get(null);
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

package com.ratata.ObjectEndpoint.controller;

import java.util.HashMap;
import java.util.Map;

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
	public static Map<String, Class<?>> classList = new HashMap<String, Class<?>>();

	public static Map<String, Object> instanceList = new HashMap<String, Object>();

	@RequestMapping(value = "/checkDataType", method = RequestMethod.POST)
	public Object checkDataType(@RequestBody JsonNode dataObj) {
		return new ObjectContainer(Mapper.mapper.convertValue(dataObj, Object.class));
	}

	@RequestMapping(value = "/createClass", method = RequestMethod.POST)
	public void createClass(@RequestBody String classbody, @RequestHeader String className) throws Exception {
		classList.put(className, InMemoryJavaCompiler.compile(className, classbody));
	}

	@RequestMapping(value = "/callStaticMethod", method = RequestMethod.POST)
	public Object callMethod(@RequestHeader(required = true) String methodName,
			@RequestHeader(required = true) String className, @RequestBody JsonNode param) throws Exception {
		return classList.get(className).getMethod(methodName).invoke(null, param);
	}

	@RequestMapping(value = "/classList", method = RequestMethod.GET)
	public Object callMethod() throws Exception {
		return classList;
	}

	@RequestMapping(value = "/newInstance", method = RequestMethod.GET)
	public String newClass(@RequestHeader String className) throws Exception {
		Object ob = classList.get(className).getConstructor(ClassLoader.class)
				.newInstance(Thread.currentThread().getContextClassLoader());
		String instanceId = className + ":" + ob.hashCode();
		instanceList.put(instanceId, ob);
		return instanceId;
	}

	@RequestMapping(value = "/instanceList", method = RequestMethod.GET)
	public Object instanceList() throws Exception {
		return instanceList.keySet();
	}

	@RequestMapping(value = "/callInstanceMethod", method = RequestMethod.POST)
	public Object callInstanceMethod(@RequestHeader(required = true) String instanceId,
			@RequestHeader(required = true) String methodName, @RequestBody JsonNode param) throws Exception {
		Object ob = instanceList.get(instanceId);
		return ob.getClass().getMethod(methodName).invoke(ob);
	}
}

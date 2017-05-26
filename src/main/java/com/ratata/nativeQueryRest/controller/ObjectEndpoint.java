package com.ratata.nativeQueryRest.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.ratata.nativeQueryRest.utils.Mapper;

@RestController
public class ObjectEndpoint {
	private static Map<String, ObjectContainer> listObject = new HashMap<String, ObjectContainer>();

	@RequestMapping(value = "/listObject", method = RequestMethod.GET)
	public Object listObject() {
		return listObject.keySet();
	}

	@RequestMapping(value = "/createDataObject", method = RequestMethod.POST)
	public Object createDataObject(@RequestBody JsonNode dataObj) {
		ObjectContainer data = Mapper.mapper.convertValue(dataObj, ObjectContainer.class);
		String objectId = genObjectId(data);
		listObject.put(objectId, data);
		return objectId;
	}

	@RequestMapping(value = "/createObject", method = RequestMethod.GET)
	public Object createObject(@RequestHeader String className, @RequestHeader String[] paramType,
			@RequestHeader String[] paramObject) throws Exception {
		List<Class<?>> paramTypeList = new ArrayList<Class<?>>();
		List<Object> paramObjectList = new ArrayList<Object>();
		if (paramType != null) {
			for (int i = 0; i < paramType.length; i++) {
				paramTypeList.add(Class.forName(paramType[i]));
				paramObjectList.add(listObject.get(paramObject[i]).getObj());
			}
		}
		Object obj = Class.forName(className).getConstructor(paramTypeList.toArray(new Class<?>[paramTypeList.size()]))
				.newInstance(paramObjectList.toArray(new Object[paramObjectList.size()]));
		String objectId = genObjectId(obj);
		listObject.put(objectId, new ObjectContainer(obj));
		return objectId;
	}

	@RequestMapping(value = "/callMethodObject", method = RequestMethod.POST)
	public Object callMethodObject(@RequestBody JsonNode dataObj,
			@RequestHeader(defaultValue = "false") Boolean storeReturn,
			@RequestHeader(required=true) String objectId,
			@RequestHeader(required=true) String methodName){
		List<Class<?>> paramTypeList = new ArrayList<Class<?>>();
		Object obj = listObject.get(objectId);
		obj.getClass().getMethod(methodName, new Class<?>[paramTypeList.size()]).invoke(obj,);
		
		if(storeReturn){
			return 
		}
		return objectId;
	}

	private String genObjectId(Object obj) {
		return obj.getClass().getTypeName() + ":" + obj.hashCode();
	}
}

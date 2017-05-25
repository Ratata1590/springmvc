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
		String objectId = data.getObjType() + ":" + data.hashCode();
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
		Object obj = Class.forName(className).getConstructor(paramTypeList.get(0)).newInstance(paramObjectList.get(0));
		String objectId = obj.getClass().getTypeName() + ":" + obj.hashCode();

		ObjectContainer objc = new ObjectContainer();
		objc.setObj(obj);
		listObject.put(objectId, objc);
		return objectId;
	}
}

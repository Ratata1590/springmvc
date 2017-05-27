package com.ratata.dynamicCodeRest.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

@RestController
public class GenToolDynamicCode {
	public static final String importList = "importList";
	public static final String autowireList = "autowireList";
	public static final String importString = "import ";

	@RequestMapping(value = "/genDynamicCodeTemplate", method = RequestMethod.POST)
	public String genDynamicCodeTemplate(@RequestBody JsonNode data) {
		StringBuilder result = new StringBuilder();
		if (data.get(importList) != null) {
			String impList = data.get(importList).textValue();
			for (String imp : impList.split(";")) {
				String classNameFull = imp.substring(importString.length(), imp.length());
				String classNameShort = new String(classNameFull).substring(classNameFull.lastIndexOf(".") + 1);
				result.append(
						String.format("public static Object $C_%1$s = \"%2$s\";\n", classNameShort, classNameFull));
			}
			result.append("\n");
		}
		if (data.get(autowireList) != null) {
			String auList = data.get(autowireList).textValue();
			for (String imp : auList.split(";")) {
				String classNameFull = imp.substring(importString.length(), imp.length());
				String classNameShort = new String(classNameFull).substring(classNameFull.lastIndexOf(".") + 1);
				result.append(
						String.format("public static Object $Sp_%1$s = \"%2$s\";\n", classNameShort, classNameFull));
			}
		}
		return result.toString();
	}
}

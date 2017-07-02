package com.ratata.nativeQueryRest.controller;

import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

@RestController
public class ResourceController {

	public static ConcurrentHashMap<String, Object> resources = new ConcurrentHashMap<String, Object>();

	// @RequestMapping(value = "/dynamicFronEnd/{resourcePath:**}", method =
	// RequestMethod.POST)
	// public static void postResource(@PathVariable String resourcePath,
	// @RequestBody String body) {
	// resources.put(resourcePath, body);
	// }

	@RequestMapping(value = "/dynamicFronEnd/**", method = RequestMethod.POST)
	public static void postResource(HttpServletRequest request, @RequestBody byte[] body) throws Exception {
		resources.put(getResourcePathFromRequest(request), body);
	}

	@RequestMapping(value = "/dynamicFronEnd/**", method = RequestMethod.GET)
	public static Object getResource(HttpServletRequest request) {
		return resources.get(getResourcePathFromRequest(request));
	}

	@RequestMapping(value = "/dynamicFronEndFileTree", method = RequestMethod.GET)
	public static Object dynamicFronEndFileTree() {
		return resources.keys();
	}

	private static String getResourcePathFromRequest(HttpServletRequest request) {
		String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		AntPathMatcher apm = new AntPathMatcher();
		String finalPath = apm.extractPathWithinPattern(bestMatchPattern, path);
		return finalPath;
	}
}

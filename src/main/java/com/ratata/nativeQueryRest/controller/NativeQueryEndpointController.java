package com.ratata.nativeQueryRest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ratata.nativeQueryRest.dao.CoreDAO;
import com.ratata.nativeQueryRest.pojo.NativeQueryParam;

@RestController
public class NativeQueryEndpointController {
	@Autowired
	private CoreDAO coreDAO;

	// ------------------------------NativeQueryDAO
	@RequestMapping(value = "/directQuery", method = RequestMethod.GET)
	public Object nativeQuery(@RequestParam String query,
			@RequestHeader(required = false, defaultValue = "") String className,
			@RequestHeader(required = false, defaultValue = "") String[] resultSet,
			@RequestHeader(required = false, defaultValue = "L") String queryMode,
			@RequestHeader(required = false, defaultValue = "{}") String param,
			@RequestHeader(required = false, defaultValue = "true") Boolean isNative,
			@RequestHeader(required = false, defaultValue = "0") Integer lockMode,
			@RequestHeader(required = false, defaultValue = "0") Integer offset,
			@RequestHeader(required = false, defaultValue = "0") Integer limit) throws Exception {
		return coreDAO.nativeQuery(
				new NativeQueryParam(query, className, resultSet, queryMode, param, isNative, lockMode, offset, limit));
	}

	@RequestMapping(value = "/directQuery", method = RequestMethod.POST)
	public Object nativeQuery(@RequestBody NativeQueryParam nativeQueryParam) throws Exception {
		return coreDAO.nativeQuery(nativeQueryParam);
	}

	@RequestMapping(value = "/directQueryToQueryObject", method = RequestMethod.GET)
	public Object directQueryToQueryObject(@RequestParam String query,
			@RequestHeader(required = false, defaultValue = "") String className,
			@RequestHeader(required = false, defaultValue = "") String[] resultSet,
			@RequestHeader(required = false, defaultValue = "L") String queryMode,
			@RequestHeader(required = false, defaultValue = "{}") String param,
			@RequestHeader(required = false, defaultValue = "true") Boolean isNative,
			@RequestHeader(required = false, defaultValue = "0") Integer lockMode,
			@RequestHeader(required = false, defaultValue = "0") Integer offset,
			@RequestHeader(required = false, defaultValue = "0") Integer limit) throws Exception {
		return new NativeQueryParam(query, className, resultSet, queryMode, param, isNative, lockMode, offset, limit);
	}
}

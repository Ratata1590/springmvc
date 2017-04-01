package com.ratata.controller;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ratata.dao.CustomQueryListDAO;
import com.ratata.dao.NativeQueryDAO;
import com.ratata.dao.NativeQueryDynamicPojoDAO;

@RestController
public class DemoController {
	@Autowired
	private NativeQueryDAO userDAOCustom;

	@Autowired
	NativeQueryDynamicPojoDAO nativeQueryDynamicPojoDAO;

	@Autowired
	CustomQueryListDAO customQueryListDAO;

	@RequestMapping(value = "/nativequery", method = RequestMethod.GET)
	public Object nativeQuery(@RequestParam("query") String query,
			@RequestParam(value = "className", required = false, defaultValue = "") String className,
			@RequestParam(value = "resultSet", required = false) String[] resultSet,
			@RequestParam(value = "singleReturn", required = false, defaultValue = "false") boolean singleReturn)
			throws ClassNotFoundException, JsonProcessingException, IOException {
		return userDAOCustom.nativeQuery(query, className, Arrays.asList(resultSet), singleReturn, null);
	}

	@RequestMapping(value = "/nativequery", method = RequestMethod.POST)
	public Object nativeQueryWithDynamicPoJo(@RequestBody ObjectNode pojo)
			throws ClassNotFoundException, JsonProcessingException, IOException {
		nativeQueryDynamicPojoDAO.nativeWithDynamicPojo(pojo);
		return pojo;
	}

	// ------------------------------
	@RequestMapping(value = "/SaveQueryList", method = RequestMethod.POST)
	public Object SaveQueryList(@RequestBody ObjectNode queryList) {
		customQueryListDAO.saveQueryList(queryList);
		return CustomQueryListDAO.queryList;
	}

	@RequestMapping(value = "/UpdateQueryList", method = RequestMethod.POST)
	public Object UpdateQueryList(@RequestBody ObjectNode queryList) {
		customQueryListDAO.updateQueryList(queryList);
		return CustomQueryListDAO.queryList;
	}

	@RequestMapping(value = "/GetQueryList", method = RequestMethod.GET)
	public Object GetQueryList() {
		return CustomQueryListDAO.queryList;
	}

	@RequestMapping(value = "/CustomQuery", method = RequestMethod.GET)
	public Object queryWithParam(@RequestParam String queryName, @RequestParam String[] param)
			throws ClassNotFoundException, JsonProcessingException, IOException {
		if (CustomQueryListDAO.queryList == null) {
			return "please insert query list first";
		}
		return customQueryListDAO.processCustomQuery(queryName, Arrays.asList(param));

	}
}

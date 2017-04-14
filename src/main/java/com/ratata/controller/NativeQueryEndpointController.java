package com.ratata.controller;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ratata.Util.LockUtil;
import com.ratata.Util.UtilNativeQuery;
import com.ratata.dao.NativeQueryDAO;
import com.ratata.dao.NativeQueryDynamicPojoDAO;
import com.ratata.dao.NativeQueryLinkQueryDAO;

@RestController
public class NativeQueryEndpointController {
	@Autowired
	private NativeQueryDAO nativeQueryDAO;

	@Autowired
	private NativeQueryDynamicPojoDAO nativeQueryDynamicPojoDAO;

	@Autowired
	private NativeQueryLinkQueryDAO nativeQueryLinkQueryDAO;

	// ------------------------------NativeQueryDAO
	@RequestMapping(value = "/nativequery", method = RequestMethod.GET)
	public Object nativeQuery(String query, @RequestParam(required = false, defaultValue = "") String className,
			@RequestParam(required = false, defaultValue = "") String[] resultSet,
			@RequestParam(required = false, defaultValue = "L") String queryMode,
			@RequestParam(required = false, defaultValue = "[]") String param,
			@RequestParam(required = false, defaultValue = "0") int offset,
			@RequestParam(required = false, defaultValue = "0") int limit) throws Exception {
		ArrayNode paramNode = ((ArrayNode) UtilNativeQuery.mapper.readTree(param));
		return nativeQueryDAO.nativeQuery(query, className, Arrays.asList(resultSet), queryMode, paramNode, offset,
				limit);
	}

	@RequestMapping(value = "/SaveObject", method = RequestMethod.POST)
	public void saveData(@RequestBody Object obj, @RequestParam String className) throws Exception {
		if (LockUtil.isLockFlag()) {
			return;
		}
		nativeQueryDAO.saveObject(obj, className);
	}

	// ------------------------------NativeQueryDynamicPojoDAO
	@RequestMapping(value = "/nativequery", method = RequestMethod.POST)
	public Object nativeQueryWithDynamicPoJo(@RequestBody JsonNode pojo) throws Exception {
		return nativeQueryDynamicPojoDAO.nativeWithDynamicPojo(pojo);
	}

	// ------------------------------CustomQueryListDAO
	@RequestMapping(value = "/SaveQueryList", method = RequestMethod.POST)
	public Object SaveQueryList(@RequestBody ObjectNode queryList) {
		nativeQueryLinkQueryDAO.saveQueryList(queryList);
		return nativeQueryLinkQueryDAO.getQueryList();
	}

	@RequestMapping(value = "/UpdateQueryList", method = RequestMethod.POST)
	public Object UpdateQueryList(@RequestBody ObjectNode queryList) {
		nativeQueryLinkQueryDAO.updateQueryList(queryList);
		return nativeQueryLinkQueryDAO.getQueryList();
	}

	@RequestMapping(value = "/GetQueryList", method = RequestMethod.GET)
	public Object GetQueryList() {
		return nativeQueryLinkQueryDAO.getQueryList();
	}

	@RequestMapping(value = "/CustomQuery", method = RequestMethod.GET)
	public Object queryWithParam(@RequestParam String queryName,
			@RequestParam(required = false, defaultValue = "[]") String param) throws Exception {
		return nativeQueryLinkQueryDAO.processCustomQuery(queryName, param);
	}

	@PostConstruct
	public void InitQueryList() throws Exception {
		nativeQueryLinkQueryDAO.saveQueryListFromFile();
	}

	// ------------------------------LockUtil
	@RequestMapping(value = "/lock", method = RequestMethod.GET)
	public String lockOption(@RequestParam String password, String hint) throws NoSuchAlgorithmException {
		return LockUtil.lock(password, hint);
	}

	@RequestMapping(value = "/unlock", method = RequestMethod.GET)
	public String unlockOption(@RequestParam String key) {
		return LockUtil.unlock(key);
	}

}

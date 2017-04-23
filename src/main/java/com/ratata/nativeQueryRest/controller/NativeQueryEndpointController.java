package com.ratata.nativeQueryRest.controller;

import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ratata.nativeQueryRest.dao.CoreDAO;
import com.ratata.nativeQueryRest.dao.DynamicDTODAO;
import com.ratata.nativeQueryRest.dao.LinkQueryDAO;
import com.ratata.nativeQueryRest.pojo.NativeQueryParam;
import com.ratata.nativeQueryRest.pojo.QueryListHolder;
import com.ratata.nativeQueryRest.utils.LockUtil;
import com.ratata.nativeQueryRest.utils.Mapper;

@RestController
public class NativeQueryEndpointController {
	@Autowired
	private CoreDAO nativeQueryDAO;

	@Autowired
	private DynamicDTODAO nativeQueryDynamicPojoDAO;

	@Autowired
	private LinkQueryDAO nativeQueryLinkQueryDAO;

	// ------------------------------NativeQueryDAO
	@RequestMapping(value = "/nativequery", method = RequestMethod.GET)
	public Object nativeQuery(@RequestParam String query,
			@RequestHeader(required = false, defaultValue = "") String className,
			@RequestHeader(required = false, defaultValue = "") String[] resultSet,
			@RequestHeader(required = false, defaultValue = "L") String queryMode,
			@RequestHeader(required = false, defaultValue = "[]") String param,
			@RequestHeader(required = false, defaultValue = "true") Boolean isNative,
			@RequestHeader(required = false, defaultValue = "0") Integer lockMode,
			@RequestHeader(required = false, defaultValue = "0") Integer offset,
			@RequestHeader(required = false, defaultValue = "0") Integer limit) throws Exception {
		return nativeQueryDAO.nativeQuery(
				new NativeQueryParam(query, className, resultSet, queryMode, param, isNative, lockMode, offset, limit));
	}

	@RequestMapping(value = "/SaveObject", method = RequestMethod.POST)
	public void saveData(@RequestBody JsonNode obj, @RequestHeader String className) throws Exception {
		if (LockUtil.isLockFlag()) {
			return;
		}
		nativeQueryDAO.saveObject(obj, className);
	}

	@RequestMapping(value = "/SaveNestedObject", method = RequestMethod.POST)
	public void saveLinkedData(@RequestBody JsonNode obj) throws Exception {
		nativeQueryDAO.saveLinkedObject(obj);
	}

	// ------------------------------NativeQueryDynamicPojoDAO
	@RequestMapping(value = "/nativequeryjson", method = RequestMethod.POST)
	public Object nativeQueryWithDynamicPoJoPost(@RequestBody JsonNode pojo) throws Exception {
		return nativeQueryDynamicPojoDAO.nativeWithDynamicPojo(pojo);
	}

	@RequestMapping(value = "/nativequeryjson", method = RequestMethod.GET)
	public Object nativeQueryWithDynamicPoJoGet(@RequestHeader String query) throws Exception {
		return nativeQueryDynamicPojoDAO.nativeWithDynamicPojo(Mapper.mapper.readTree(query));
	}

	// ------------------------------NativeQueryTransaction
	@RequestMapping(value = "/nativequerytransaction", method = RequestMethod.GET)
	public Object nativeQueryTransactionGet(@RequestHeader String query) throws Exception {
		return nativeQueryDynamicPojoDAO.transationNativeQuery(query);
	}

	@RequestMapping(value = "/nativequerytransaction", method = RequestMethod.POST)
	public Object nativeQueryTransactionPost(@RequestBody ArrayNode query) throws Exception {
		return nativeQueryDynamicPojoDAO.transationNativeQuery(query);
	}

	// ------------------------------CustomQueryListDAO
	@RequestMapping(value = "/SaveQueryList", method = RequestMethod.POST)
	public Object SaveQueryList(@RequestBody ObjectNode queryList) throws Exception {
		nativeQueryLinkQueryDAO.saveQueryList(queryList);
		nativeQueryLinkQueryDAO.saveQueryListToDB();
		return QueryListHolder.queryList;
	}

	@RequestMapping(value = "/UpdateQueryList", method = RequestMethod.POST)
	public Object UpdateQueryList(@RequestBody ObjectNode queryList) throws Exception {
		nativeQueryLinkQueryDAO.updateQueryList(queryList);
		nativeQueryLinkQueryDAO.updateQueryListToDB();
		return QueryListHolder.queryList;
	}

	@RequestMapping(value = "/GetQueryList", method = RequestMethod.GET)
	public Object GetQueryList() {
		return QueryListHolder.queryList;
	}

	@RequestMapping(value = "/CustomQuery", method = RequestMethod.GET)
	public Object queryWithParam(@RequestParam String queryName, @RequestHeader(defaultValue = "[]") String param)
			throws Exception {
		return nativeQueryLinkQueryDAO.processLinkQuery(queryName, param);
	}

	// @Scheduled(fixedRate = 10000)
	public void SyncDbQueryList() {
		nativeQueryLinkQueryDAO.syncQueryListfromDB();
	}

	@PostConstruct
	public void InitQueryList() throws Exception {
		nativeQueryLinkQueryDAO.saveQueryListFromFile();
		nativeQueryLinkQueryDAO.persistQueryListToDB();
	}

	// ------------------------------LockUtil
	@RequestMapping(value = "/lock", method = RequestMethod.GET)
	public String lockOption(@RequestHeader String password, @RequestHeader String hint)
			throws NoSuchAlgorithmException {
		return LockUtil.lock(password, hint);
	}

	@RequestMapping(value = "/unlock", method = RequestMethod.GET)
	public String unlockOption(@RequestHeader String key) {
		return LockUtil.unlock(key);
	}

}

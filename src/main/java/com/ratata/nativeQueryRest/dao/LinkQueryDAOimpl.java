package com.ratata.nativeQueryRest.dao;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ratata.nativeQueryRest.pojo.QueryListHolder;
import com.ratata.nativeQueryRest.saveDB.entity.QueryList;
import com.ratata.nativeQueryRest.saveDB.repo.QueryListRepo;
import com.ratata.nativeQueryRest.utils.Const;
import com.ratata.nativeQueryRest.utils.Mapper;

@Component
public class LinkQueryDAOimpl implements LinkQueryDAO {
	@Autowired
	private CoreDAO coreDAO;

	@Autowired
	private QueryListRepo queryListRepo;

	@Autowired
	private DynamicDTODAO dynamicDTODAO;

	public void saveQueryList(Object query) {
		QueryListHolder.queryList = Mapper.mapper.convertValue(query, Mapper.typeRef);
	}

	public void saveQueryListFromFile() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(Const.LINK_QUERY_INITFILENAME).getFile());
		saveQueryList(Mapper.mapper.readTree(file));
	}

	public void persistQueryListToDB() throws Exception {
		Iterator<String> iter = QueryListHolder.queryList.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			QueryList query = queryListRepo.findQueryByKey(key);
			if (query != null) {
				continue;
			}
			query = new QueryList();
			query.setQueryName(key);
			query.setQueryData(Mapper.mapper.writeValueAsString(QueryListHolder.queryList.get(key)));
			queryListRepo.save(query);
		}
	}

	public void saveQueryListToDB() throws Exception {
		Iterator<String> iter = QueryListHolder.queryList.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			QueryList query = queryListRepo.findQueryByKey(key);
			if (query == null) {
				query = new QueryList();
			}
			query.setQueryName(key);
			query.setQueryData(Mapper.mapper.writeValueAsString(QueryListHolder.queryList.get(key)));
			queryListRepo.save(query);
		}
	}

	@Transactional
	public void updateQueryListToDB() throws Exception {
		Iterator<String> iter = QueryListHolder.queryList.keySet().iterator();
		List<String> listName = queryListRepo.getAllqueryName();
		for (String name : listName) {
			if (!QueryListHolder.queryList.containsKey(name)) {
				queryListRepo.deleteQueryByqueryName(name);
			}
		}
		while (iter.hasNext()) {
			String key = iter.next();
			QueryList query = queryListRepo.findQueryByKey(key);
			if (query == null) {
				query = new QueryList();
			}
			query.setQueryName(key);
			query.setQueryData(Mapper.mapper.writeValueAsString(QueryListHolder.queryList.get(key)));
			queryListRepo.save(query);
		}
	}

	public void syncQueryListfromDB() {
		List<QueryList> queryListDB = queryListRepo.findAll();
		for (QueryList query : queryListDB) {
			try {
				QueryListHolder.queryList.put(query.getQueryName(), Mapper.mapper.readTree(query.getQueryData()));
			} catch (Exception e) {
				QueryListHolder.queryList.put(query.getQueryName(),
						Mapper.mapper.createArrayNode().add("Invalid query from DB"));
			}
		}
	}

	public void updateQueryList(ObjectNode query) {
		Iterator<Entry<String, JsonNode>> nodeEntry = query.fields();
		while (nodeEntry.hasNext()) {
			Entry<String, JsonNode> entry = (Entry<String, JsonNode>) nodeEntry.next();
			if (entry.getValue().isNull() && QueryListHolder.queryList.containsKey(entry.getKey())) {
				QueryListHolder.queryList.remove(entry.getKey());
				continue;
			}
			QueryListHolder.queryList.put(entry.getKey(), entry.getValue());
		}
	}

	public Object processLinkQuery(String queryName, String param) throws Exception {
		if (QueryListHolder.queryList.isEmpty()) {
			return Const.LINK_QUERY_QUERYLISTEMPTY;
		}
		return processLinkQuery(queryName, Mapper.mapper.readTree(param));
	}

	public Object processLinkQuery(String queryName, JsonNode param) throws Exception {
		JsonNode queryObject = QueryListHolder.queryList.get(queryName);
		if (queryObject == null) {
			return Const.LINK_QUERY_QUERYNOTEXIST;
		}
		if (queryObject.has(Const.PARAM_QUERY)) {
			return coreDAO.processQueryObject(queryObject, param);
		}
		return dynamicDTODAO.nativeWithDynamicPojo((ObjectNode) queryObject.deepCopy(), param);
	}

}

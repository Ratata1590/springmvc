package com.ratata.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

@Component
public class UserDAOCustom {

	@PersistenceContext
	private EntityManager em;

	@SuppressWarnings("unchecked")
	public Object nativeQuery(String query, String className, String[] resultSet)
			throws ClassNotFoundException, JsonProcessingException, IOException {
		if (resultSet != null && resultSet.length != 0) {
			List<Object[]> result = em.createNativeQuery(query).getResultList();
			List<Object> resultReturn = new ArrayList<>();
			for (Object[] record : result) {
				int i = 0;
				Map<String, Object> resultMap = new HashMap<String, Object>();
				while (i < record.length) {
					resultMap.put(resultSet[i], record[i]);
					i++;
				}
				resultReturn.add(resultMap);
			}
			return resultReturn;
		}

		if (className.isEmpty())

		{
			return em.createNativeQuery(query).getResultList();
		}

		return em.createNativeQuery(query, Class.forName(className)).getResultList();
	}

}
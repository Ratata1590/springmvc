package com.ratata.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;

@Component
public class UserDAOCustom {

	@PersistenceContext
	private EntityManager em;

	public Object nativeQery(String query, String className) throws ClassNotFoundException {
		if (className.isEmpty()) {
			return em.createNativeQuery(query).getResultList();
		} else {
			return em.createNativeQuery(query, Class.forName(className)).getResultList();
		}
	}
}

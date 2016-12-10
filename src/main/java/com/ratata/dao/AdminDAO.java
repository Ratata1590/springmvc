package com.ratata.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ratata.model.Admin;
import com.ratata.model.User;

@Component
public class AdminDAO {

	@PersistenceContext
	private EntityManager em;

	public Admin getAdminByUsername(String username) {
		Admin admin=null;
		try {
			admin = (Admin) em.createQuery("SELECT t FROM Admin t where t.username = :value1")
					.setParameter("value1", username).getSingleResult();
		} catch (Exception NoResultException) {
		}
		return admin;
	}
}

package com.ratata.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ratata.model.User;

@Component
public class UserDAO {

	@PersistenceContext
	private EntityManager em;

	public List<User> getAllUsers() {
		TypedQuery<User> query = em.createQuery("SELECT u FROM User u ORDER BY u.id", User.class);
		return query.getResultList();
	}

	@Transactional
	public void delete(int userId) {
		User user = em.getReference(User.class, userId);
		em.remove(user);
	}

	@Transactional
	public void save(User aUser) {
		em.persist(aUser);
	}
}

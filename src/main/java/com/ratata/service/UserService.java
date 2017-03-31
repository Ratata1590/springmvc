package com.ratata.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ratata.dao.UserDAO;
import com.ratata.model.User;

@Service
public class UserService {

	@Autowired
	private UserDAO userDAO;

	public List<User> getAllUsers() {
		return userDAO.getAllUsers();
	}

	public void save(User aUser) {
		userDAO.save(aUser);
	}

	public void delete(int userId) {
		userDAO.delete(userId);
	}

}

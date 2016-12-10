package com.ratata.service;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ratata.common.Constants;
import com.ratata.common.MD5Hash;
import com.ratata.dao.AdminDAO;
import com.ratata.model.Admin;

@Service
public class AuthenticationService {

	@Autowired
	private AdminDAO adminDAO;

	public boolean login(String username, String password) {
		Admin admin = adminDAO.getAdminByUsername(username);
		System.out.println(admin);
		if (admin == null) {
			return Constants.AuthenticationStatus_LoginFail;
		}
		if (admin.getPassword().equals(MD5Hash.MD5HashPassword(username, password))) {
			return Constants.AuthenticationStatus_LoginSuccess;
		}
		return Constants.AuthenticationStatus_LoginFail;
	}

}

package com.ratata.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ratata.model.User;
import com.ratata.service.AsyncService;
import com.ratata.service.UserService;

@RestController
public class DemoController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/user/", method = RequestMethod.GET)
	public ResponseEntity<List<User>> home(Model model) {
		List<User> users = userService.getAllUsers();
		if (users.isEmpty()) {
			return new ResponseEntity<List<User>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<User>>(users, HttpStatus.OK);
	}

	@RequestMapping(value = "/user/add", method = RequestMethod.POST)
	public ResponseEntity<Void> addUser(@RequestBody User user) {
		userService.save(user);
		return new ResponseEntity<Void>(HttpStatus.CREATED);
	}

	@RequestMapping(value = "/user/delete/{userId}", method = RequestMethod.DELETE)
	public ResponseEntity<User> removeUser(@PathVariable("userId") int userId) {

		userService.delete(userId);

		return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
	}

	@Autowired
	private AsyncService asyncService;

	@RequestMapping(value = "/thread", method = RequestMethod.GET)
	public String threadController(@RequestParam String data) {
		asyncService.setData(data);
		return asyncService.getAsyncThread().getStatus();
	}

	@RequestMapping(value = "/threadStart", method = RequestMethod.GET)
	public int threadStartController() {
		return asyncService.startService();
	}

	@RequestMapping(value = "/threadStop", method = RequestMethod.GET)
	public void threadStopController() {
		asyncService.stopService();
	}
}

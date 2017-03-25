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
import com.ratata.service.MyThreadService;
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
	private MyThreadService asyncService;

	@RequestMapping(value = "/threadSetData", method = RequestMethod.GET)
	public Object threadController(@RequestParam String data, @RequestParam int id) {
		asyncService.setData(data, id);
		return asyncService.getstatus(id);
	}

	@RequestMapping(value = "/threadStart", method = RequestMethod.GET)
	public Object threadStartController(@RequestParam int id) {
		if (id == -1) {
			asyncService.startAll();
			return asyncService.getstatusAll();
		} else {
			asyncService.startService(id);
			return asyncService.getstatus(id);
		}
	}

	@RequestMapping(value = "/threadStop", method = RequestMethod.GET)
	public Object threadStopController(@RequestParam int id) {
		if (id == -1) {
			asyncService.stopAll();
			return asyncService.getstatusAll();
		} else {
			asyncService.stopService(id);
			return asyncService.getstatus(id);
		}
	}

	@RequestMapping(value = "/threadStatus", method = RequestMethod.GET)
	public Object threadGetStatusController() {
		return asyncService.getstatusAll();
	}

	@RequestMapping(value = "/threadInit", method = RequestMethod.GET)
	public Object threadInitController() {
		asyncService.initPool();
		return asyncService.getstatusAll();
	}
}

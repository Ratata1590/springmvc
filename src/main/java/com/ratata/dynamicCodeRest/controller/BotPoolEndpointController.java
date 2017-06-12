package com.ratata.dynamicCodeRest.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BotPoolEndpointController {
	private static Map<String, LinkedList<String>> botConnectionPool = new ConcurrentHashMap<String, LinkedList<String>>();

	@RequestMapping(value = "/botInit")
	public static List<String> botInit(@RequestHeader String botName, @RequestHeader int poolSize) {
		LinkedList<String> thePool = new LinkedList<String>();
		for (int i = 0; i < poolSize; i++) {
			thePool.push(MirrorForceEndpointController.mirrorControlCreate());
		}
		if (botConnectionPool.containsKey(botName)) {
			botCleanPool(botName);
		}
		botConnectionPool.put(botName, thePool);
		return thePool;
	}

	@RequestMapping(value = "/botCleanPool")
	public static void botCleanPool(@RequestHeader String botName) {
		if (botConnectionPool.containsKey(botName)) {
			for (String conn : botConnectionPool.get(botName)) {
				botConnectionPool.remove(conn);
				MirrorForceEndpointController.mirrorControlDisconnect(conn);
			}
		}
	}

	@RequestMapping(value = "/botAddConnection")
	public static String botAddConnection(@RequestHeader String botName) {
		String conn = MirrorForceEndpointController.mirrorControlCreate();
		botConnectionPool.get(botName).push(conn);
		return conn;
	}

	@RequestMapping(value = "/botGetConnection")
	public static String botGetConnection(@RequestHeader String botName) throws Exception {
		String conn = botConnectionPool.get(botName).pop();
		String client = MirrorForceEndpointController.mirrorControlCreate();
		MirrorForceEndpointController.mirrorControlConnectTwoWay(conn, client);
		return client;
	}
}

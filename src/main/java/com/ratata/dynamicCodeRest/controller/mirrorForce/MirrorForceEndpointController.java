package com.ratata.dynamicCodeRest.controller.mirrorForce;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

@RequestMapping("/mirror")
@RestController
public class MirrorForceEndpointController {
	private static Map<String, PipedInputStream> inputStreamList = new ConcurrentHashMap<String, PipedInputStream>();
	private static Map<String, PipedOutputStream> outputStreamList = new ConcurrentHashMap<String, PipedOutputStream>();

	@RequestMapping(value = "/socketHandler", method = RequestMethod.POST)
	public static void socketHandler(@RequestBody byte[] data, @RequestHeader(required = true) String sockRestId)
			throws Exception {
		PipedOutputStream out = outputStreamList.get(sockRestId);
		if (data.length != 0) {
			try {
				out.write(data);
				out.flush();
			} catch (Exception e) {
				try {
					out.close();
				} catch (Exception e2) {
				}
				outputStreamList.remove(sockRestId);
				throw new Exception("socket closed");
			}
		}
	}

	@RequestMapping(value = "/socketHandler", method = RequestMethod.GET)
	public static Object socketHandler(@RequestHeader(required = true) String sockRestId) throws Exception {
		PipedInputStream in = inputStreamList.get(sockRestId);
		int byteRead = in.available();
		byte[] resultBuff = new byte[byteRead];
		try {
			in.read(resultBuff, 0, byteRead);
		} catch (Exception e) {
			try {
				in.close();
			} catch (Exception e2) {
			}
			inputStreamList.remove(sockRestId);
			throw new Exception("socket closed");
		}
		return resultBuff;
	}

	@RequestMapping(value = "/socketControl/create", method = RequestMethod.POST)
	public static String mirrorControlCreate() {
		String tmpsessionId = getSaltString();
		while (inputStreamList.containsKey(tmpsessionId) || outputStreamList.containsKey(tmpsessionId)) {
			tmpsessionId = getSaltString();
		}
		PipedInputStream in = new PipedInputStream();
		PipedOutputStream out = new PipedOutputStream();
		inputStreamList.put(tmpsessionId, in);
		outputStreamList.put(tmpsessionId, out);
		return tmpsessionId;
	}

	@RequestMapping(value = "/socketControl/connectTwoWay", method = RequestMethod.POST)
	public static void mirrorControlConnectTwoWay(@RequestHeader String in, @RequestHeader String out)
			throws Exception {
		inputStreamList.get(in).connect(outputStreamList.get(out));
		outputStreamList.get(in).connect(inputStreamList.get(out));
	}

	@RequestMapping(value = "/socketControl/connectByJson", method = RequestMethod.POST)
	public static void mirrorControlConnectByJson(@RequestBody JsonNode config) throws Exception {
		if (config.isArray()) {
			for (JsonNode node : (ArrayNode) config) {
				processConnect(node);
			}
		}
		if (config.isObject()) {
			processConnect(config);
		}
	}

	private final static String type = "type";
	private final static String oneWay = "oneWay";
	private final static String twoWay = "twoWay";
	private final static String in = "in";
	private final static String out = "out";

	private static void processConnect(JsonNode config) throws Exception {
		if (!config.has(type) || !config.has(in) || !config.has(out)) {
			return;
		}
		switch (config.get(type).asText()) {
		case oneWay:
			inputStreamList.get(config.get(in).asText()).connect(outputStreamList.get(config.get(out).asText()));
			break;
		case twoWay:
			inputStreamList.get(config.get(in).asText()).connect(outputStreamList.get(config.get(out).asText()));
			outputStreamList.get(config.get(in).asText()).connect(inputStreamList.get(config.get(out).asText()));
			break;
		}
	}

	@RequestMapping(value = "/socketControl/disconnect", method = RequestMethod.POST)
	public static void mirrorControlDisconnect(@RequestHeader String sessionId) {
		try {
			outputStreamList.get(sessionId).close();
		} catch (Exception e) {
		}
		outputStreamList.remove(sessionId);
		try {
			inputStreamList.get(sessionId).close();
		} catch (Exception e) {
		}
		inputStreamList.remove(sessionId);
	}

	@RequestMapping(value = "/socketList", method = RequestMethod.GET)
	public static Object socketList() {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("inputStreamList", inputStreamList.keySet());
		result.put("outputStreamList", outputStreamList.keySet());
		return result;
	}

	@RequestMapping(value = "/socketClear", method = RequestMethod.GET)
	public static void socketClear() {
		for (String id : inputStreamList.keySet()) {
			try {
				inputStreamList.get(id).close();
			} catch (Exception e) {
			}
			inputStreamList.remove(id);
		}
		for (String id : outputStreamList.keySet()) {
			try {
				outputStreamList.get(id).close();
			} catch (Exception e) {
			}
			outputStreamList.remove(id);
		}
	}

	public static String getSaltString() {
		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < 18) { // length of the random string.
			int index = (int) (rnd.nextFloat() * SALTCHARS.length());
			salt.append(SALTCHARS.charAt(index));
		}
		String saltStr = salt.toString();
		return saltStr;
	}
}

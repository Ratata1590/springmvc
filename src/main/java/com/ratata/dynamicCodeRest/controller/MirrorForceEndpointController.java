package com.ratata.dynamicCodeRest.controller;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

	@RequestMapping(value = "/socketControl", method = RequestMethod.POST)
	public static void socketControl(@RequestHeader(required = true) String sessionId,
			@RequestHeader(required = true) String action) throws Exception {
		switch (action) {
		case "connect":
			if (inputStreamList.containsKey(sessionId) || outputStreamList.containsKey(sessionId)) {
				throw new Exception("sessionId is being used!");
			}
			PipedInputStream in = new PipedInputStream();
			PipedOutputStream out = new PipedOutputStream(in);
			inputStreamList.put(sessionId, in);
			outputStreamList.put(sessionId, out);
			break;
		case "diconnect":
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
			break;
		}
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
}

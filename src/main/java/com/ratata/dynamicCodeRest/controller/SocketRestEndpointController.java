package com.ratata.dynamicCodeRest.controller;

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SocketRestEndpointController {

	private static Map<String, Socket> sessionList = new ConcurrentHashMap<String, Socket>();
	private static Map<String, String> configList = new ConcurrentHashMap<String, String>();

	@RequestMapping(value = "/socketHandler", method = RequestMethod.POST)
	public static void socketHandler(@RequestBody byte[] data, @RequestHeader(required = true) String sockRestId)
			throws Exception {
		if (data.length != 0) {
			try {
				OutputStream out = sessionList.get(sockRestId).getOutputStream();
				out.write(data);
				out.flush();
			} catch (Exception e) {
				try {
					sessionList.get(sockRestId).close();
				} catch (Exception e2) {
				}
				sessionList.remove(sockRestId);
				throw new Exception("socket closed");
			}
		}
	}

	@RequestMapping(value = "/socketHandler", method = RequestMethod.GET)
	public static Object socketHandler(@RequestHeader(required = true) String sockRestId) throws Exception {
		Socket socket = sessionList.get(sockRestId);
		byte[] resultBuff;
		byte[] tmpbuff = null;
		try {
			resultBuff = new byte[socket.getReceiveBufferSize()];
			tmpbuff = new byte[socket.getInputStream().read(resultBuff, 0, resultBuff.length)];
			System.arraycopy(resultBuff, 0, tmpbuff, 0, tmpbuff.length);
		} catch (Exception e) {
			try {
				socket.close();
			} catch (Exception e2) {
			}
			sessionList.remove(sockRestId);
			throw new Exception("socket closed");
		}
		return tmpbuff;
	}

	@RequestMapping(value = "/socketControl", method = RequestMethod.POST)
	public static void socketConnect(@RequestHeader(required = true) String sessionId,
			@RequestHeader(required = true) String action, @RequestHeader(required = false) String host,
			@RequestHeader(required = false) Integer port) throws Exception {
		switch (action) {
		case "connect":
			if (sessionList.containsKey(sessionId)) {
				throw new Exception("sessionId is being used!");
			}
			Socket sock = openSocket(host, port);
			sessionList.put(sessionId, sock);
			configList.put(sessionId, host + ":" + port);
			break;
		case "diconnect":
			try {
				sessionList.get(sessionId).close();
			} catch (Exception e) {
			}
			sessionList.remove(sessionId);
			configList.remove(sessionId);
			break;
		}
	}

	@RequestMapping(value = "/socketList", method = RequestMethod.GET)
	public static Object socketList() {
		return configList;
	}

	@RequestMapping(value = "/socketClear", method = RequestMethod.GET)
	public static void socketClear() {
		for (String id : sessionList.keySet()) {
			try {
				sessionList.get(id).close();
			} catch (Exception e) {
			}
			sessionList.remove(id);
			configList.remove(id);
		}
	}

	private static Socket openSocket(String server, int port) throws Exception {
		Socket socket;
		try {
			InetAddress inteAddress = InetAddress.getByName(server);
			SocketAddress socketAddress = new InetSocketAddress(inteAddress, port);
			socket = new Socket();
			int timeoutInMs = 10 * 1000;
			socket.connect(socketAddress, timeoutInMs);
			return socket;
		} catch (SocketTimeoutException ste) {
			ste.printStackTrace();
			throw ste;
		}
	}
}

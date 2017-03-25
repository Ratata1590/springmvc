package com.ratata.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ratata.async.AsyncThread;

@Service
public class AsyncService {
	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;

	private int poolsize = 5;

	private static List<AsyncThread> threadPool = new ArrayList<AsyncThread>();

	public void initPool() {
		while (threadPool.size() < poolsize) {
			AsyncThread asyncThread = new AsyncThread();
			taskExecutor.execute(asyncThread);
			threadPool.add(asyncThread);
		}
	}

	public void startService(int id) {
		threadPool.get(id).start();
	}

	public void stopService(int id) {
		threadPool.get(id).stop();
	}

	public void setData(String data, int id) {
		threadPool.get(id).data = data;
	}

	public Object getstatus() {
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode status = mapper.createArrayNode();
		for (int i = 0; i < threadPool.size(); i++) {
			ObjectNode thread = mapper.createObjectNode();
			thread.put("id", i);
			thread.put("status", threadPool.get(i).getStatus());
			thread.put("data", threadPool.get(i).data);
			thread.put("error", threadPool.get(i).errorStackTrace);
			status.add(thread);
		}
		return status;
	}
}

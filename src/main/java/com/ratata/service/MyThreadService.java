package com.ratata.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ratata.asyncEngine.AbstractAsyncService;
import com.ratata.asyncEngine.AsyncThread;
import com.ratata.asyncEngine.AsyncThreadConfig;
import com.ratata.asyncImpl.MyThread;

@Service
public class MyThreadService extends AbstractAsyncService {

	@Override
	public ObjectNode getstatus(int id) {
		ObjectNode objectNode = super.getstatus(id);
		MyThread myThread = ((MyThread) threadPool.get(id));
		objectNode.put("dataUrl", myThread.dataUrl);
		objectNode.put("currentItemName", myThread.currentItemName);
		objectNode.put("currentItemId", myThread.currentItemId);
		objectNode.put("Info", "[" + myThread.startId + "-" + myThread.stopId + "]["
				+ percentJob(myThread.currentItemId, myThread.startId, myThread.stopId) + "%]");
		return objectNode;
	}

	private int percentJob(float current, float startId, float stopId) {
		return (int) ((current - startId + 1) / (stopId - startId + 1) * 100f);
	}

	public void setData(int id, String dataUrl, JsonNode data, int startId, int stopId) {
		MyThread myThread = ((MyThread) threadPool.get(id));
		myThread.dataUrl = dataUrl;
		myThread.data = data;
		myThread.startId = startId;
		myThread.stopId = stopId;
	}

	public void createSingleThread(String dataUrl, JsonNode data, int startId, int stopId) {
		if (threadPool.size() < AsyncThreadConfig.poolsize) {
			AsyncThread asyncThread = new MyThread(dataUrl, data, startId, stopId);
			taskExecutor.execute(asyncThread);
			threadPool.add(asyncThread);
		}
	}

	@Override
	public void initPool() {

	}
}

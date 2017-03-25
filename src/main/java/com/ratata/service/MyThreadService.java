package com.ratata.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ratata.asyncEngine.AbstractAsyncService;
import com.ratata.asyncEngine.AsyncThread;
import com.ratata.asyncImpl.MyThread;

@Service
public class MyThreadService extends AbstractAsyncService {

	@Override
	public ObjectNode getstatus(int id) {
		ObjectNode objectNode = super.getstatus(id);
		objectNode.put("data", ((MyThread) threadPool.get(id)).data);
		return objectNode;
	}

	@Override
	public AsyncThread createThread() {
		return new MyThread();
	}

	public void setData(String data, int id) {
		((MyThread) threadPool.get(id)).data = data;
	}
}

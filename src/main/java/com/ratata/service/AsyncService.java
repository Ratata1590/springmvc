package com.ratata.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class AsyncService {

	@Autowired
	private AsyncThread asyncThread;

	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;

	public AsyncThread getAsyncThread() {
		return asyncThread;
	}

	public void setAsyncThread(AsyncThread asyncThread) {
		this.asyncThread = asyncThread;
	}

	public int startService() {
		System.out.println(asyncThread.getStatus());
		if (asyncThread.getStatus().equals("START")) {
			taskExecutor.execute(asyncThread);
		}
		if (asyncThread.getStatus().equals("STOP")) {
			asyncThread.start();
		}
		return taskExecutor.getActiveCount();
	}

	public void stopService() {
		asyncThread.stop();
	}

	public void setData(String data) {
		asyncThread.data = data;
	}
}

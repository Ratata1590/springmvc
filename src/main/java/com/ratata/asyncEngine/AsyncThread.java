package com.ratata.asyncEngine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public abstract class AsyncThread implements Runnable {

	public String errorStackTrace = "";

	private Semaphore semaphore = new Semaphore(0);

	public AsyncThreadConfig.AsyncThreadStatus status = AsyncThreadConfig.AsyncThreadStatus.INIT;

	@Override
	public void run() {
		try {
			process();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void process() throws InterruptedException {
		while (!status.equals(AsyncThreadConfig.AsyncThreadStatus.DESTROY)) {
			while (semaphore.tryAcquire(1, TimeUnit.SECONDS)) {
				status = AsyncThreadConfig.AsyncThreadStatus.RUNNING;
				try {
					while (status.equals(AsyncThreadConfig.AsyncThreadStatus.RUNNING)) {
						job();
					}
				} catch (Exception e) {
					status = AsyncThreadConfig.AsyncThreadStatus.ERROR;
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					errorStackTrace = errors.toString();
				}
			}
		}
	}

	public void job() throws Exception {
		Thread.sleep(1000);
	}

	public void start() {
		semaphore.release();
	}

	public void stop() {
		status = AsyncThreadConfig.AsyncThreadStatus.STOP;
	}

	public String getStatus() {
		return status.toString();
	}

	public void destroy() {
		stop();
		status = AsyncThreadConfig.AsyncThreadStatus.DESTROY;
	}
}

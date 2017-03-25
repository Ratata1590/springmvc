package com.ratata.async;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class AsyncThread implements Runnable {

	public String data = "updating";

	public String errorStackTrace = "";

	private Semaphore semaphore = new Semaphore(0);

	public AsyncThreadStatus status = AsyncThreadStatus.INIT;

	@Override
	public void run() {
		try {
			process();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void process() throws InterruptedException {
		while (true) {
			while (semaphore.tryAcquire(1, TimeUnit.SECONDS)) {
				status = AsyncThreadStatus.RUNNING;
				try {
					while (status.equals(AsyncThreadStatus.RUNNING)) {
						// running job
						Thread.sleep(1000);
						System.out.println(data);
					}
				} catch (InterruptedException e) {
					throw e;
				} catch (Exception e) {
					status = AsyncThreadStatus.ERROR;
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					errorStackTrace = errors.toString();
				}
			}
		}
	}

	public void start() {
		semaphore.release();
	}

	public void stop() {
		status = AsyncThreadStatus.STOP;
	}

	public String getStatus() {
		return status.toString();
	}
}

package com.ratata.async;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class AsyncThread implements Runnable {

	public String data = "updating";

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

				while (!status.equals(AsyncThreadStatus.STOP)) {
					// running job
					Thread.sleep(1000);
					System.out.println(data);
				}
			}
			status = AsyncThreadStatus.STOP;
		}
	}

	public String getStatus() {
		return status.toString();
	}

	public void stop() {
		status = AsyncThreadStatus.STOP;
	}

	public void start() {
		semaphore.release();
	}
}

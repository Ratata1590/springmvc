package com.ratata.dynamicCodeRest.dynamicObject;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CleanUpThread extends Thread implements CleanUp {
	private Thread thread;
	private Integer timeOut;

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		try {
			TimeUnit.SECONDS.sleep(timeOut);
			thread.interrupt();
			TimeUnit.SECONDS.sleep(10);
			thread.stop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void setThreadInfo(Thread thread, Integer timeOut) {
		this.thread = thread;
		this.timeOut = timeOut;
		start();
	}
}

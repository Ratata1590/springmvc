package com.ratata.service;

import org.springframework.stereotype.Service;

@Service
public class AsyncThread implements Runnable {

	public String data = "updating";

	private boolean running = true;

	public enum Status {
		START, RUNNING, STOP
	}

	public Status status = Status.START;

	@Override
	public void run() {
		while (true) {
			while (running) {
				status = Status.RUNNING;
				try {
					Thread.sleep(1000);
					System.out.println(data);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			status = Status.STOP;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public String getStatus() {
		return status.toString();
	}

	public void stop() {
		running = false;
	}

	public void start() {
		running = true;
	}
}

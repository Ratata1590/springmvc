package com.ratata.asyncImpl;

import com.ratata.asyncEngine.AsyncThread;

public class MyThread extends AsyncThread {
	
	public String data = "updating"; 
	@Override
	public void job() throws Exception {
		Thread.sleep(1000);
		System.out.println(data);
	}
}

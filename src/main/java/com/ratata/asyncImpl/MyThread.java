package com.ratata.asyncImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.ratata.asyncEngine.AsyncThread;

public class MyThread extends AsyncThread {

	public String dataUrl = "None";
	public JsonNode data;
	public String currentItemName = "";
	public int currentItemId = 10;
	public int startId;
	public int stopId;

	public MyThread(String dataUrl, JsonNode data, int startId, int stopId) {
		this.dataUrl = dataUrl;
		this.data = data;
		this.startId = startId;
		this.stopId = stopId;
	}

	@Override
	public void job() throws Exception {
		Thread.sleep(1000);
		System.out.println(dataUrl);
	}
}

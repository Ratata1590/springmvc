package com.ratata.asyncImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.ratata.asyncEngine.AsyncThread;

public abstract class InsertThread extends AsyncThread {

	public String dataUrl = "None";
	public JsonNode data;
	public String currentItemName;
	public int currentItemId;
	public int startId;
	public int stopId;

	public InsertThread(String dataUrl, JsonNode data, int startId, int stopId) {
		this.dataUrl = dataUrl;
		this.data = data;
		this.startId = startId;
		this.stopId = stopId;
	}

	@Override
	public void job() throws Exception {
	}
}

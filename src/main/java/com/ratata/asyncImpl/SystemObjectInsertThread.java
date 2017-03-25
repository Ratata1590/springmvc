package com.ratata.asyncImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.ratata.asyncEngine.AsyncThreadConfig.AsyncThreadStatus;

public class SystemObjectInsertThread extends InsertThread {

	public SystemObjectInsertThread(String dataUrl, JsonNode data, int startId, int stopId) {
		super(dataUrl, data, startId, stopId);
	}

	@Override
	public void job() throws Exception {
		for (currentItemId = startId; currentItemId < stopId - 1; currentItemId++) {
			Thread.sleep(2000);
		}
		status = AsyncThreadStatus.STOP;
	}
}

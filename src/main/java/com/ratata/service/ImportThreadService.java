package com.ratata.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ratata.asyncEngine.AbstractAsyncService;
import com.ratata.asyncImpl.InsertThread;
import com.ratata.asyncImpl.IviewInsertThread;
import com.ratata.asyncImpl.SystemObjectInsertThread;

@Service
public class ImportThreadService extends AbstractAsyncService {

  private Map<String, JsonNode> dataIviewFromRemoteEndpoint = new HashMap<String, JsonNode>();
  private Map<String, JsonNode> dataSystemObjectFromRemoteEndpoint =
      new HashMap<String, JsonNode>();

  @Override
  public ObjectNode getStatus(int id) {
    ObjectNode objectNode = super.getStatus(id);
    InsertThread myThread = ((InsertThread) threadPool.get(id));
    objectNode.put("dataUrl", myThread.dataUrl);
    objectNode.put("currentItemName", myThread.currentItemName);
    objectNode.put("currentItemId", myThread.currentItemId);
    objectNode.put("Info", "[" + myThread.startId + "-" + myThread.stopId + "]["
        + percentJob(myThread.currentItemId, myThread.startId, myThread.stopId) + "%]");
    return objectNode;
  }

  private int percentJob(float current, float startId, float stopId) {
    return (int) ((current - startId + 1) / (stopId - startId + 1) * 100f);
  }

  public void setData(int id, int startId, int stopId, int currentItemId) {
    InsertThread myThread = ((InsertThread) threadPool.get(id));
    myThread.startId = startId;
    myThread.stopId = stopId;
    myThread.currentItemId = currentItemId;
  }

  public void insertThreadInit() {
    for (String url : dataIviewFromRemoteEndpoint.keySet()) {
      ArrayNode data = (ArrayNode) dataIviewFromRemoteEndpoint.get(url);
      createThread(new IviewInsertThread(url, data, 0, data.size()));
    }
    for (String url : dataSystemObjectFromRemoteEndpoint.keySet()) {
      ArrayNode data = (ArrayNode) dataSystemObjectFromRemoteEndpoint.get(url);
      createThread(new SystemObjectInsertThread(url, data, 0, data.size()));
    }
  }

  public void splitThread(int id) {
    InsertThread myThread = ((InsertThread) threadPool.get(id));
    if (myThread instanceof IviewInsertThread) {
      int end = myThread.stopId;
      myThread.stopId = (myThread.stopId - myThread.currentItemId - 2) / 2;
      createThread(new IviewInsertThread(myThread.dataUrl, myThread.data, myThread.stopId, end));
      startAll();
    }
    if (myThread instanceof SystemObjectInsertThread) {
      int end = myThread.stopId;
      myThread.stopId = (myThread.stopId - myThread.currentItemId - 2) / 2;
      createThread(
          new SystemObjectInsertThread(myThread.dataUrl, myThread.data, myThread.stopId, end));
      startAll();
    }
  }

  public Map<String, JsonNode> getDataIviewFromRemoteEndpoint() {
    return dataIviewFromRemoteEndpoint;
  }

  public Map<String, JsonNode> getDataSystemObjectFromRemoteEndpoint() {
    return dataSystemObjectFromRemoteEndpoint;
  }

  public ThreadPoolTaskExecutor getTaskExecutor() {
    return AbstractAsyncService.taskExecutor;
  }

  @Autowired
  public void setTaskExecutor(ThreadPoolTaskExecutor taskExecutor) {
    AbstractAsyncService.taskExecutor = taskExecutor;
  }

}

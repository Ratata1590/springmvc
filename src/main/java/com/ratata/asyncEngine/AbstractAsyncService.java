package com.ratata.asyncEngine;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public abstract class AbstractAsyncService {

  @Autowired
  protected static ThreadPoolTaskExecutor taskExecutor;

  protected static List<AsyncThread> threadPool = new ArrayList<AsyncThread>();

  ObjectMapper mapper = new ObjectMapper();

  public void createThread(AsyncThread asyncThread) {
    taskExecutor.execute(asyncThread);
    threadPool.add(asyncThread);
  }

  public void startService(int id) {
    threadPool.get(id).start();
  }

  public void startAll() {
    for (AsyncThread asyncThread : threadPool) {
      asyncThread.start();
    }
  }

  public void stopService(int id) {
    threadPool.get(id).stop();
  }

  public void stopAll() {
    for (AsyncThread asyncThread : threadPool) {
      asyncThread.stop();
    }
  }

  public ObjectNode getStatus(int id) {
    ObjectNode thread = mapper.createObjectNode();
    thread.put("id", id);
    thread.put("status", threadPool.get(id).getStatus());
    thread.put("error", threadPool.get(id).errorStackTrace);
    return thread;
  }

  public ArrayNode getStatusAll() {
    ArrayNode status = mapper.createArrayNode();
    for (int i = 0; i < threadPool.size(); i++) {
      status.add(getStatus(i));
    }
    return status;
  }

  public void destroy(int id) {
    threadPool.get(id).destroy();
    threadPool.remove(id);
  }

  public void destroyAll() {
    List<AsyncThread> toRemove = new ArrayList<>();
    for (AsyncThread asyncThread : threadPool) {
      toRemove.add(asyncThread);
    }
    threadPool.removeAll(toRemove);
  }
}

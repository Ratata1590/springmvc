package com.ratata.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.ratata.pojo.NodeInfo;

public interface RatataDBService {
  public Long saveNode(JsonNode object) throws Exception;

  public JsonNode getNode(Long id, Integer type, Boolean showId, Boolean showData,
      Boolean showBinary) throws Exception;

  public List<NodeInfo> getParent(Long id, Integer type, Boolean showId, Boolean showData,
      Boolean showBinary);

  public void downloadFileById(HttpServletResponse response, Long id) throws Exception;

  public void downloadFileByHash(HttpServletResponse response, String hash) throws Exception;

  public Object uploadFile(MultipartFile[] files, String[] names) throws Exception;
}

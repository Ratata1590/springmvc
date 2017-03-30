package com.ratata.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class UserDAOCustom {

  @PersistenceContext
  private EntityManager em;

  ObjectMapper mapper = new ObjectMapper();

  public static final String PARAM_QUERY = "query";
  public static final String PARAM_CLASSNAME = "className";
  public static final String PARAM_RESULTSET = "resultSet";
  public static final String PARAM_SINGLERETURN = "singleReturn";

  @SuppressWarnings("unchecked")
  public Object nativeQuery(String query, String className, List<String> resultSet,
      boolean singleReturn) throws ClassNotFoundException, JsonProcessingException, IOException {
    Object result = returnResult(singleReturn, returnQuery(query, className));

    if (resultSet != null && resultSet.size() != 0) {
      if (singleReturn) {
        Object[] record = (Object[]) result;
        Map<String, Object> resultMap = new HashMap<String, Object>();
        for (int i = 0; i < record.length; i++) {
          resultMap.put(resultSet.get(i), record[i]);
        }
        return resultMap;
      } else {
        List<Object> resultReturn = new ArrayList<>();
        for (Object[] record : (List<Object[]>) result) {
          int i = 0;
          Map<String, Object> resultMap = new HashMap<String, Object>();
          while (i < record.length) {
            resultMap.put(resultSet.get(i), record[i]);
            i++;
          }
          resultReturn.add(resultMap);
        }
        return resultReturn;
      }
    } else {
      return result;
    }
  }

  private Object returnResult(boolean singleReturn, Query queryOjb) {
    if (singleReturn) {
      return queryOjb.getSingleResult();
    } else {
      return queryOjb.getResultList();
    }
  }

  private Query returnQuery(String query, String className) throws ClassNotFoundException {
    if (className != null && !className.isEmpty()) {
      return em.createNativeQuery(query, Class.forName(className));
    } else {
      return em.createNativeQuery(query);
    }
  }

  // ------------------------------------------
  public Object nativeWithDynamicPojo(ObjectNode node)
      throws ClassNotFoundException, JsonProcessingException, IOException {
    if (node.has(PARAM_QUERY)) {
      return processQuery(node);
    }
    checkNode(node);
    return node;
  }

  public void checkNode(JsonNode node)
      throws ClassNotFoundException, JsonProcessingException, IOException {
    Iterator<Entry<String, JsonNode>> nodeEntry = node.fields();
    while (nodeEntry.hasNext()) {
      Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) nodeEntry.next();
      switch (entry.getValue().getNodeType()) {
        case ARRAY:
          for (int i = 0; i < entry.getValue().size(); i++) {
            JsonNode tnode = entry.getValue().get(i);
            if (tnode.has(PARAM_QUERY)) {
              ((ArrayNode) entry.getValue()).set(i, mapper.valueToTree(processQuery(tnode)));
            } else {
              checkNode(tnode);
            }
          }
          break;
        case OBJECT:
          if (entry.getValue().has(PARAM_QUERY)) {
            ((ObjectNode) node).replace(entry.getKey(),
                mapper.valueToTree(processQuery(entry.getValue())));
          } else {
            checkNode(entry.getValue());
          }
        default:
          break;
      }
    }
  }

  private Object processQuery(JsonNode node)
      throws ClassNotFoundException, JsonProcessingException, IOException {
    boolean singleReturn = false;

    if (node.has(PARAM_SINGLERETURN)) {
      singleReturn = node.get(PARAM_SINGLERETURN).asBoolean();
    }
    if (node.has(PARAM_RESULTSET)) {
      ArrayNode resultSetData = (ArrayNode) node.get(PARAM_RESULTSET);
      List<String> options = new ArrayList<String>();
      for (int i = 0; i < resultSetData.size(); i++) {
        options.add(i, resultSetData.get(i).asText());
      }
      return nativeQuery(node.get(PARAM_QUERY).asText(), null, options, singleReturn);
    }
    if (node.has(PARAM_CLASSNAME)) {
      return nativeQuery(node.get(PARAM_QUERY).asText(), node.get(PARAM_CLASSNAME).asText(), null,
          singleReturn);
    }
    return nativeQuery(node.get(PARAM_QUERY).asText(), null, null, singleReturn);
  }

}

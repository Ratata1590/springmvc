package com.ratata.dao;

import java.util.Iterator;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ratata.Util.Const;
import com.ratata.Util.UtilNativeQuery;

@Component
public class NativeQueryDynamicPojoDAOImpl implements NativeQueryDynamicPojoDAO {

	@Autowired
	private NativeQueryDAO nativeQueryDAO;

	@Autowired
	NativeQueryLinkQueryDAO nativeQueryLinkQueryDAO;

	public Object nativeWithDynamicPojo(JsonNode pojo) throws Exception {
		return nativeWithDynamicPojo(pojo.get(Const.PARAM_SINGLEREQUEST_DATA),
				(ArrayNode) pojo.get(Const.PARAM_SINGLEREQUEST_PARAM));
	}

	public Object nativeWithDynamicPojo(JsonNode node, ArrayNode param) throws Exception {
		if (node.has(Const.PARAM_QUERY)) {
			return nativeQueryDAO.processQueryObject(node, param);
		}
		processDynamicPojo(node, param);
		return node;
	}

	public void processDynamicPojo(JsonNode node, ArrayNode param) throws Exception {
		int objectNodeNumber = 0;
		checkNode(node, param, objectNodeNumber);
	}

	public void checkNode(JsonNode node, ArrayNode paramArray, Integer objectNodeNumber) throws Exception {
		Iterator<Entry<String, JsonNode>> nodeIter = node.fields();
		while (nodeIter.hasNext()) {
			Entry<String, JsonNode> entry = (Entry<String, JsonNode>) nodeIter.next();
			JsonNode innerNode = entry.getValue();
			switch (innerNode.getNodeType()) {
			case ARRAY:
				for (int i = 0; i < innerNode.size(); i++) {
					JsonNode tnode = innerNode.get(i);
					if (tnode.has(Const.LINK_QUERY)) {
						processMergeArray(tnode, innerNode, i, processQueryLink(tnode, paramArray, objectNodeNumber));
						objectNodeNumber++;
						continue;
					}
					if (tnode.has(Const.PARAM_QUERY)) {
						processMergeArray(tnode, innerNode, i, processQueryNode(tnode, paramArray, objectNodeNumber));
						objectNodeNumber++;
						continue;
					}
					checkNode(tnode, paramArray, objectNodeNumber);
				}
				break;
			case OBJECT:
				if (innerNode.has(Const.LINK_QUERY)) {
					((ObjectNode) node).replace(entry.getKey(),
							processQueryLink(innerNode, paramArray, objectNodeNumber));
					objectNodeNumber++;
					break;
				}
				if (innerNode.has(Const.PARAM_QUERY)) {
					((ObjectNode) node).replace(entry.getKey(),
							processQueryNode(innerNode, paramArray, objectNodeNumber));
					objectNodeNumber++;
					break;
				}
				checkNode(innerNode, paramArray, objectNodeNumber);
			default:
				break;
			}
		}
	}

	private void processMergeArray(JsonNode tnode, JsonNode innerNode, Integer i, JsonNode data) {
		if (tnode.has(Const.PARAM_MERGEARRAY) && tnode.get(Const.PARAM_MERGEARRAY).asBoolean()) {
			((ArrayNode) innerNode).remove(i);
			((ArrayNode) innerNode).addAll((ArrayNode) data);
		} else {
			((ArrayNode) innerNode).set(i, data);
		}
	}

	private JsonNode processQueryLink(JsonNode node, ArrayNode paramArray, Integer objectNodeNumber) throws Exception {
		return UtilNativeQuery.mapper.valueToTree(nativeQueryDAO.processQueryObject(
				nativeQueryLinkQueryDAO.getQueryList().get(node.get(Const.LINK_QUERY).asText()),
				(ArrayNode) paramArray.get(objectNodeNumber)));
	}

	private JsonNode processQueryNode(JsonNode node, ArrayNode paramArray, Integer objectNodeNumber) throws Exception {
		return UtilNativeQuery.mapper
				.valueToTree(nativeQueryDAO.processQueryObject(node, (ArrayNode) paramArray.get(objectNodeNumber)));
	}
}

package com.ratata.poi.controller;


import java.util.Iterator;
import java.util.LinkedList;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.ratata.nativeQueryRest.dao.LinkQueryDAO;

@RestController
public class PoiController {

  private static final ObjectMapper SORTED_MAPPER = new ObjectMapper();
  static {
    SORTED_MAPPER.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
  }

  private LinkedList<String> headers = new LinkedList<String>();

  @Autowired
  private LinkQueryDAO linkQueryDAO;

  @RequestMapping(value = "/jsonToExcel/{queryName}/{fileName}", method = RequestMethod.GET)
  public void downloadExcel(@PathVariable("queryName") String queryName,
      @PathVariable("fileName") String fileName, @RequestHeader(defaultValue = "{}") String param,
      HttpServletResponse response) throws Exception {
    JsonNode data = SORTED_MAPPER.valueToTree(linkQueryDAO.processLinkQuery(queryName, param));
    if (data.isArray()) {
      data = (ArrayNode) data;
    } else {
      data = SORTED_MAPPER.createArrayNode().add(data);
    }

    ArrayNode newData = SORTED_MAPPER.createArrayNode();
    for (JsonNode node : data) {
      String value = JsonFlattener.flatten(SORTED_MAPPER.writeValueAsString(node));
      newData.add(SORTED_MAPPER.readTree(value));
    }
    XSSFWorkbook workbook = new XSSFWorkbook();
    XSSFSheet sheet = workbook.createSheet("ExportData");
    resolveJsonToExcelData(newData, sheet);
    response.setContentType("application/x-msdownload");
    response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xlsx");
    workbook.write(response.getOutputStream());
    workbook.close();
    return;
  }

  private void resolveJsonToExcelData(ArrayNode data, Sheet dataSheet) {
    // fill header
    headers = resolveKeys(data);
    Row headerRow = dataSheet.createRow(0);
    fillHeaders(headers, headerRow);

    // fill data
    for (int indexRow = 0; indexRow < data.size(); indexRow++) {
      Row dataRow = dataSheet.createRow(indexRow + 1);
      fillData(dataRow, indexRow, data.get(indexRow));
    }
  }

  private void fillHeaders(LinkedList<String> headerList, Row row) {
    for (int index = 0; index < headerList.size(); index++) {
      Cell cell = row.createCell(index);
      cell.setCellValue(headerList.get(index));
    }
  }

  private LinkedList<String> resolveKeys(ArrayNode data) {
    LinkedList<String> keyList = new LinkedList<String>();
    for (JsonNode node : data) {
      Iterator<String> names = node.fieldNames();
      while (names.hasNext()) {
        String key = names.next();
        if (!keyList.contains(key)) {
          keyList.add(key);
        }
      }
    }
    return keyList;
  }

  private void fillData(Row dataRow, int indexRow, JsonNode node) {
    for (int colIndex = 0; colIndex < headers.size(); colIndex++) {
      Cell cell = dataRow.createCell(colIndex);
      String key = headers.get(colIndex);
      if (node.has(key)) {
        JsonNode value = node.get(key);
        switch (value.getNodeType()) {
          case BOOLEAN:
            cell.setCellValue(value.asBoolean());
            break;
          case NUMBER:
            cell.setCellValue(value.asDouble());
            break;
          case STRING:
            cell.setCellValue(value.asText());
            break;
          default:
            break;
        }
      }
    }
  }
}

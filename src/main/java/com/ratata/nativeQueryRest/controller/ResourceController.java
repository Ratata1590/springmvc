package com.ratata.nativeQueryRest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

@RestController
public class ResourceController {

	public static ConcurrentHashMap<String, byte[]> resources = new ConcurrentHashMap<String, byte[]>();
	public static TreeMap<String, String> resourcesList = new TreeMap<String, String>();

	@RequestMapping(value = "/dynamicFrontEnd/**", method = RequestMethod.POST)
	public static void postResource(HttpServletRequest request, @RequestBody byte[] body) throws Exception {
		String key = getResourcePathFromRequest(request);
		resources.put(key, body);
		resourcesList.put(key, String.valueOf(body.length));
	}

	@RequestMapping(value = "/dynamicFrontEndUploadFile", method = RequestMethod.POST)
	public static void uploadResource(@RequestParam("file") MultipartFile[] file, @RequestParam("path") String[] path)
			throws Exception {
		if (file.length != path.length) {
			throw new Exception("invalid request");
		}
		for (int i = 0; i < path.length; i++) {
			resources.put(path[i], file[i].getBytes());
			resourcesList.put(path[i], String.valueOf(file[i].getBytes().length));
		}
	}

	@RequestMapping(value = "/dynamicFrontEnd/**", method = RequestMethod.GET)
	public static Object getResource(HttpServletRequest request) throws Exception {
		String key = getResourcePathFromRequest(request);
		checkResource(key);
		return resources.get(key);
	}

	@RequestMapping(value = "/dynamicFrontEnd/**", method = RequestMethod.DELETE)
	public static void deleteResource(HttpServletRequest request) throws Exception {
		String key = getResourcePathFromRequest(request);
		checkResource(key);
		resources.remove(key);
		resourcesList.remove(key);
	}

	@RequestMapping(value = "/dynamicFrontEndDeleteFile", method = RequestMethod.DELETE)
	public static void deleteResources(@RequestBody(required = true) List<String> fileList) throws Exception {
		for (String file : fileList) {
			resources.remove(file);
			resourcesList.remove(file);
		}
	}

	@RequestMapping(value = "/dynamicFrontEndFileTree", method = RequestMethod.GET)
	public static Object dynamicFronEndFileTree() {
		return resourcesList;
	}

	@RequestMapping(value = "/dynamicFrontEndGetInfo", method = RequestMethod.GET)
	public static Object dynamicFronEndFileTree(@RequestHeader String resource) throws Exception {
		checkResource(resource);
		return resourcesList.get(resource);
	}

	@RequestMapping(value = "dynamicFrontEndUploadZip", method = RequestMethod.POST)
	public static void uploadZipFrontEnd(@RequestParam("file") MultipartFile[] file) throws Exception {
		for (MultipartFile zipFile : file) {
			processUnzipSingleFile(zipFile);
		}
	}

	private static void processUnzipSingleFile(MultipartFile zipFile) throws Exception {
		ZipInputStream zipInputStream = new ZipInputStream(zipFile.getInputStream());
		ZipEntry zipEntry = zipInputStream.getNextEntry();
		while (zipEntry != null) {
			String fileName = zipEntry.getName();
			byte[] file = IOUtils.toByteArray(zipInputStream);
			resources.put(fileName, file);
			resourcesList.put(fileName, String.valueOf(file.length));
			zipEntry = zipInputStream.getNextEntry();
		}
	}

	@RequestMapping(value = "dynamicFrontEndDownloadZip/**", method = RequestMethod.GET)
	public static void downloadZipFrontEnd(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String key = getResourcePathFromRequest(request);
		List<String> fileList = new ArrayList<String>();
		for (String keyItem : resources.keySet()) {
			if (keyItem.startsWith(key)) {
				fileList.add(keyItem);
			}
		}
		zipFiles(fileList, response);
	}

	private static void zipFiles(List<String> fileList, HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment; filename=\"" + "download.zip" + "\"");
		response.setHeader("Content-Type", "application/zip");
		ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
		for (String file : fileList) {
			ZipEntry ze = new ZipEntry(file);
			zos.putNextEntry(ze);
			zos.write(resources.get(file));
		}
		zos.closeEntry();
		zos.close();
	}

	private static String getResourcePathFromRequest(HttpServletRequest request) {
		String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		AntPathMatcher apm = new AntPathMatcher();
		String finalPath = apm.extractPathWithinPattern(bestMatchPattern, path);
		return finalPath;
	}

	private static void checkResource(String key) throws Exception {
		if (!resources.containsKey(key)) {
			throw new Exception("resource not found");
		}
	}
}

package com.ratata.dynamicCodeRest.controller;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.artifact.SubArtifact;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@RestController
public class MavenRepoEndpointController {
	public static final String ratataRepo = "/ratataRepo";

	public static final String keyName = "name";
	public static final String keyData = "data";

	public static final String keyId = "id";
	public static final String keyType = "type";
	public static final String keyUrl = "url";
	public static final String keyDependencies = "dependencies";

	public static final String keyGroupId = "groupId";
	public static final String keyArtifactId = "artifactId";
	public static final String keyVersion = "version";

	public static final String defaultId = "central";
	public static final String defaultType = "default";
	public static final String defaultUrl = "http://repo1.maven.org/maven2/";

	private final XmlMapper xmlMapper = new XmlMapper();

	private RepositorySystem repositorySystem;
	private DefaultRepositorySystemSession systemSession;

	public static final Map<String, JsonNode> configList = new HashMap<String, JsonNode>();
	public static final Map<String, ClassLoader> classLoaderList = new HashMap<String, ClassLoader>();

	@RequestMapping(value = "/convertMavenToJson", method = RequestMethod.POST)
	public Object convertMavenToJson(@RequestBody String mavenDependencies) throws Exception {
		return xmlMapper.readTree(mavenDependencies);
	}

	@RequestMapping(value = "/uploadJarToRepo", method = RequestMethod.POST)
	public void uploadJarToRepo(@RequestBody(required = true) MultipartFile file,
			@RequestHeader(required = true) String groupId, @RequestHeader(required = true) String artifactId,
			@RequestHeader(required = true) String version) throws Exception {
		File tempFile = File.createTempFile(file.getOriginalFilename(), ".jar");
		file.transferTo(tempFile);
		Artifact jarArtifact = new DefaultArtifact(groupId, artifactId, "jar", version);
		jarArtifact.setFile(tempFile);
		Artifact pomArtifact = new SubArtifact( jarArtifact, "", "pom" );
	    pomArtifact = pomArtifact.setFile( new File( "pom.xml" ) );
		InstallRequest install = new InstallRequest();
		install.addArtifact(jarArtifact).addArtifact(pomArtifact);
		repositorySystem.install(systemSession, install);
	}

	@RequestMapping(value = "/createClassLoader", method = RequestMethod.POST)
	public void createClassLoader(@RequestBody JsonNode mavenDependencies) throws Exception {
		String classLoaderName = mavenDependencies.get(keyName).asText();
		URL[] urlList = mavenDependenciesToArtifactRequest(mavenDependencies.get(keyData));
		ClassLoader classLoader = new URLClassLoader(urlList);
		if (classLoaderList.containsKey(classLoaderName)) {
			removeClassLoader(classLoaderName);
		}
		classLoaderList.put(classLoaderName, classLoader);
		configList.put(classLoaderName, mavenDependencies);
	}

	@RequestMapping(value = "/removeClassLoader", method = RequestMethod.GET)
	public void removeClassLoader(@RequestHeader String classLoaderName) throws Exception {
		classLoaderList.remove(classLoaderName);
		configList.remove(classLoaderName);
	}

	@RequestMapping(value = "/getClassLoaderList", method = RequestMethod.GET)
	public Object getClassLoaderList() throws Exception {
		return configList;
	}

	@PostConstruct
	private void initRepositorySession() {
		DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
		locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
		locator.addService(TransporterFactory.class, FileTransporterFactory.class);
		locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
		repositorySystem = locator.getService(RepositorySystem.class);
		systemSession = MavenRepositorySystemUtils.newSession();
		LocalRepository localRepo = new LocalRepository(ratataRepo);
		systemSession.setLocalRepositoryManager(repositorySystem.newLocalRepositoryManager(systemSession, localRepo));
	}

	private URL[] mavenDependenciesToArtifactRequest(JsonNode data) throws Exception {
		List<URL> result = new ArrayList<URL>();
		if (data.isArray()) {
			for (JsonNode node : data) {
				result.addAll(resolveSingleRepo(node));
			}

		} else {
			result = resolveSingleRepo(data);
		}

		return result.toArray(new URL[result.size()]);
	}

	private List<URL> resolveSingleRepo(JsonNode node) throws Exception {
		List<URL> result = new ArrayList<URL>();
		String id = defaultId, type = defaultType, url = defaultUrl;
		id = node.get(keyId) != null ? node.get(keyId).asText() : id;
		type = node.get(keyType) != null ? node.get(keyType).asText() : type;
		url = node.get(keyUrl) != null ? node.get(keyUrl).asText() : url;

		RemoteRepository remoteRepo = new RemoteRepository.Builder(id, type, url).build();

		for (JsonNode dependency : node.get(keyDependencies)) {
			ArtifactRequest artifactRequest = new ArtifactRequest();
			artifactRequest.setArtifact(new DefaultArtifact(dependency.get(keyGroupId).asText(),
					dependency.get(keyArtifactId).asText(), "jar", dependency.get(keyVersion).asText()));
			artifactRequest.addRepository(remoteRepo);
			result.add(repositorySystem.resolveArtifact(systemSession, artifactRequest).getArtifact().getFile().toURI()
					.toURL());
		}
		return result;
	}
}

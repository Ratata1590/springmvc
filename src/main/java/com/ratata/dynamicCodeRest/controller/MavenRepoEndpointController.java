package com.ratata.dynamicCodeRest.controller;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.Proxy;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.repository.AuthenticationBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;

@RestController
public class MavenRepoEndpointController {
  public static final String ratataRepo = "/ratataRepo";

  public static final String keyName = "name";
  public static final String keyParent = "parent";
  public static final String parent_SEP = "/";
  public static final String keyData = "data";

  public static final String keyId = "id";
  public static final String keyType = "type";
  public static final String keyUrl = "url";
  public static final String keyProxyURL = "proxyUrl";
  public static final String keyProxyProtocol = "protocol";
  public static final String keyProxyPort = "proxyPort";
  public static final String keyUsername = "username";
  public static final String keyPassword = "password";
  public static final String keyDependencies = "dependencies";

  public static final String keyGroupId = "groupId";
  public static final String keyArtifactId = "artifactId";
  public static final String keyVersion = "version";

  public static final String defaultSystemName = "system";

  public static final String defaultId = "central";
  public static final String defaultIdLocal = "local";
  public static final String defaultType = "default";
  public static final String defaultUrl = "http://repo1.maven.org/maven2/";
  public static final String defaultProxyProtocol = "http";

  private RepositorySystem repositorySystem;
  private LocalRepository localRepo;
  private DefaultRepositorySystemSession systemSession;

  public static final Map<String, JsonNode> configList = new HashMap<String, JsonNode>();
  public static final Map<String, ClassLoader> classLoaderList = new HashMap<String, ClassLoader>();

  @RequestMapping(value = "/lookUpClass", method = RequestMethod.POST)
  public Object lookUpClass(@RequestBody(required = true) JsonNode classNameList,
      @RequestHeader String classLoaderName) throws Exception {
    Map<String, Object> result = new HashMap<String, Object>();
    ClassLoader theClassLoader = classLoaderList.get(classLoaderName);
    for (JsonNode cln : classNameList) {
      try {
        Class<?> theClass = theClassLoader.loadClass(cln.asText());
        result.put(cln.asText(), listClassItems(theClass));
      } catch (Exception e) {
        result.put(cln.asText(), false);
      }
    }
    System.gc();
    return result;
  }

  @RequestMapping(value = "/uploadJarToRepo", method = RequestMethod.POST)
  public void uploadJarToRepo(@RequestBody(required = true) MultipartFile file,
      @RequestHeader(required = true) String groupId,
      @RequestHeader(required = true) String artifactId,
      @RequestHeader(required = true) String version) throws Exception {
    FileUtils.deleteDirectory(
        new File(localRepo.getBasedir().getPath().concat(File.separator).concat(groupId)
            .concat(File.separator).concat(artifactId).concat(File.separator).concat(version)));
    File tempFile = File.createTempFile(file.getOriginalFilename(), "");
    file.transferTo(tempFile);
    Artifact jarArtifact =
        new DefaultArtifact(groupId, artifactId, null, "jar", version, null, tempFile);
    InstallRequest install = new InstallRequest();
    install.addArtifact(jarArtifact);
    repositorySystem.install(systemSession, install);
    tempFile.delete();
  }

  @RequestMapping(value = "/getLocalMetadata", method = RequestMethod.GET)
  public Object getMetadata(@RequestHeader(required = true) String groupId,
      @RequestHeader(required = true) String artifactId) throws Exception {
    File metaFile = new File(localRepo.getBasedir().getPath().concat(File.separator).concat(groupId)
        .concat(File.separator).concat(artifactId).concat(File.separator)
        .concat("maven-metadata-local.xml"));
    return new String(Files.readAllBytes(Paths.get(metaFile.getPath())));
  }

  @RequestMapping(value = "/createClassLoader", method = RequestMethod.POST)
  public void createClassLoader(@RequestHeader(required = true) String classLoaderName,
      @RequestBody JsonNode mavenDependencies) throws Exception {
    if (classLoaderName.contains(parent_SEP)) {
      throw new Exception("invalid classLoaderName");
    }
    ClassLoader parent = null;
    if (mavenDependencies.has(keyParent)) {
      parent = resolveParentClassloader(mavenDependencies.get(keyParent).asText());
    }
    URL[] urlList = mavenDependenciesToArtifactRequest(mavenDependencies.get(keyData));
    URLClassLoader classLoader;
    if (parent != null) {
      classLoader = new URLClassLoader(urlList, parent);
      classLoaderName =
          mavenDependencies.get(keyParent).asText().concat(parent_SEP).concat(classLoaderName);
    } else {
      classLoader = new URLClassLoader(urlList);
    }
    if (classLoaderList.containsKey(classLoaderName)) {
      removeClassLoader(classLoaderName);
    }
    classLoaderList.put(classLoaderName, classLoader);
    configList.put(classLoaderName, mavenDependencies);
    System.gc();
  }

  private ClassLoader resolveParentClassloader(String parentName) {
    if (parentName.equals(defaultSystemName)) {
      return Thread.currentThread().getContextClassLoader();
    }
    return classLoaderList.get(parentName);
  }

  @RequestMapping(value = "/removeClassLoader", method = RequestMethod.GET)
  public void removeClassLoader(@RequestHeader String classLoaderName) throws Exception {
    for (String cl : classLoaderList.keySet()) {
      if (cl.startsWith(classLoaderName)) {
        classLoaderList.remove(cl);
        configList.remove(cl);
      }
    }
    System.gc();
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
    localRepo = new LocalRepository(ratataRepo);
    System.out.println(localRepo.getBasedir().getAbsolutePath());
    systemSession.setLocalRepositoryManager(
        repositorySystem.newLocalRepositoryManager(systemSession, localRepo));
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

    RemoteRepository remoteRepo;
    if (node.get(keyProxyURL) != null && node.get(keyProxyPort) != null
        && node.get(keyUsername) != null && node.get(keyPassword) != null) {
      String protocol = node.get(keyProxyProtocol) != null ? node.get(keyProxyProtocol).asText()
          : defaultProxyProtocol;
      Authentication auth = new AuthenticationBuilder().addUsername(node.get(keyUsername).asText())
          .addPassword(node.get(keyPassword).asText()).build();
      Proxy proxy = new Proxy(protocol, node.get(keyProxyURL).asText(),
          node.get(keyProxyPort).asInt(), (org.eclipse.aether.repository.Authentication) auth);
      remoteRepo = new RemoteRepository.Builder(id, type, url).setProxy(proxy).build();
    } else {
      remoteRepo = new RemoteRepository.Builder(id, type, url).build();
    }

    for (JsonNode dependency : node.get(keyDependencies)) {
      ArtifactRequest artifactRequest = new ArtifactRequest();
      artifactRequest.setArtifact(new DefaultArtifact(dependency.get(keyGroupId).asText(),
          dependency.get(keyArtifactId).asText(), "jar", dependency.get(keyVersion).asText()));
      artifactRequest.addRepository(remoteRepo);
      result.add(repositorySystem.resolveArtifact(systemSession, artifactRequest).getArtifact()
          .getFile().toURI().toURL());
    }
    return result;
  }

  private Object listClassItems(Class<?> theClass) {
    Map<String, Object> result = new HashMap<String, Object>();
    List<Object> cnstList = new ArrayList<Object>();
    for (Constructor<?> cnst : theClass.getDeclaredConstructors()) {
      Map<String, Object> cnstDetail = new HashMap<String, Object>();
      List<String> listParam = new ArrayList<String>();
      for (Class<?> param : cnst.getParameterTypes()) {
        listParam.add(param.getName());
      }
      cnstDetail.put(cnst.getName(), listParam);
      cnstList.add(cnstDetail);
    }
    result.put("constructors", cnstList);

    List<Object> fieldList = new ArrayList<Object>();
    for (Field field : theClass.getDeclaredFields()) {
      Map<String, Object> fieldDetail = new HashMap<String, Object>();
      fieldDetail.put(field.getName(), field.getAnnotatedType().getType().getTypeName());
      fieldList.add(fieldDetail);
    }
    result.put("fields", fieldList);

    List<Object> methodList = new ArrayList<Object>();
    for (Method method : theClass.getDeclaredMethods()) {
      Map<String, Object> methodDetail = new HashMap<String, Object>();
      List<String> listParam = new ArrayList<String>();
      for (Class<?> param : method.getParameterTypes()) {
        listParam.add(param.getName());
      }
      methodDetail.put(method.getName(), listParam);
      methodList.add(methodDetail);
    }
    result.put("methods", methodList);
    return result;
  }
}

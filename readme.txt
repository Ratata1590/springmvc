After import maven project into Eclipse, remember to set server runtime to avoid
-----------------------------------------------------------------------------------
Description	Resource	Path	Location	Type
The superclass "javax.servlet.http.HttpServlet" was not found on the Java Build Path	index.jsp	/demoMvc/src/main/webapp	line 1	JSP Problem

===================================================================================
update .gitignore like this to ignore eclipse generated folder
/target/
.classpath
.settings
.gitignore
.project
.springBeans
===================================================================================
change version of Dynamic Web Module by config schema of web.xml. EX v3.0
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://java.sun.com/xml/ns/javaee" xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd" id="WebApp_ID" version="3.0">
===================================================================================
add ContextLoaderListener
	_To tie the lifecycle of the ApplicationContext to the lifecycle of the ServletContext
	_to automate the creation of the ApplicationContext, so you don't have to write explicit code to do create it - it's a convenience function

add DispatcherServlet
	_Handle request mapping

more:
https://schoudari.wordpress.com/2012/07/23/purpose-of-contextloaderlistener-spring-mvc/

add basic controller,view(helloworld.jsp)
===================================================================================
also can be deployed by mvn tomcat7:run
===================================================================================
convert to rest service
POST
http://localhost:8080/demoSpringMvc4/user/add
Content-Type application/json
{
    "username": "ratata",
    "version": 0
}
GET
http://localhost:8080/demoSpringMvc4/user/
DELETE
http://localhost:8080/demoSpringMvc4/user/delete/1
===================================================================================
add log4j pom,log4j.properties,<mvc:resources mapping="/resources/**" location="/resources/" />
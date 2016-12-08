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
===================================================================================
change version of Dynamic Web Module by config schema of web.xml. EX v3.0
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://java.sun.com/xml/ns/javaee" xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd" id="WebApp_ID" version="3.0">
 
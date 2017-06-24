package test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

public class Test {

	// logging
	private ByteArrayOutputStream baos = new ByteArrayOutputStream();
	private PrintStream log = new PrintStream(baos);

	public String getLog() {
		return new String(baos.toByteArray(), StandardCharsets.UTF_8);
	}

	public void clearLog() {
		baos.reset();
	}

	// util method
	public static Method $M_getClass;

	public static Object getClass(Object theClass) throws Exception {
		return $M_getClass.invoke(null, theClass);
	}

	public static Method $M_newDynObj;

	public static Object newDynObj(Object theClass) throws Exception {
		return $M_newDynObj.invoke(null, theClass);
	}

	public static Method $M_getDynObj;

	public static Object getDynObj(Object theClass) throws Exception {
		return $M_getDynObj.invoke(null, theClass);
	}

	public static Method $M_clearDynObj;

	public static Object clearDynObj(Object theClass) throws Exception {
		return $M_clearDynObj.invoke(null, theClass);
	}

	public static Method $M_getBean;

	public static Object getBean(Object theClass) throws Exception {
		return $M_getBean.invoke(null, theClass);
	}

	public static Method $M_newObj;

	public static Object newObj(Object theClass, Class<?>[] paramType, Object[] paramData) throws Exception {
		return $M_newObj.invoke(null, theClass, paramType, paramData);
	}

	public static Method $M_getStaticProp;

	public static Object getStaticProp(Object theClass, String propName) throws Exception {
		return $M_getStaticProp.invoke(null, theClass, propName);
	}

	public static Method $M_setStaticProp;

	public static Object setStaticProp(Object theClass, String propName, Object value) throws Exception {
		return $M_setStaticProp.invoke(null, theClass, propName, value);
	}

	public static Method $M_getObjProp;

	public static Object getObjProp(Object obj, String propName) throws Exception {
		return $M_getObjProp.invoke(null, obj, propName);
	}

	public static Method $M_setObjProp;

	public static Object setObjProp(Object obj, String propName, Object value) throws Exception {
		return $M_setObjProp.invoke(null, obj, propName, value);
	}

	public static Method $M_callStaticMethod;

	public static Object callStaticMethod(Object theClass, String methodName, Class<?>[] paramType, Object[] paramData)
			throws Exception {
		return $M_callStaticMethod.invoke(null, theClass, methodName, paramType, paramData);
	}

	public static Method $M_callObjMethod;

	public static Object callObjMethod(Object obj, String methodName, Class<?>[] paramType, Object[] paramData)
			throws Exception {
		return $M_callObjMethod.invoke(null, obj, methodName, paramType, paramData);
	}

	// import class
	// public static Object $C_ObjectMapper =
	// "com.fasterxml.jackson.databind.ObjectMapper";
	// public static Object $C_JsonNode =
	// "com.fasterxml.jackson.databind.JsonNode";
	// public static Object $C_ArrayNode =
	// "com.fasterxml.jackson.databind.node.ArrayNode";
	// public static Object $C_Mapper =
	// "com.ratata.nativeQueryRest.utils.Mapper";
	// public static Object $C_NativeQueryParam =
	// "com.ratata.nativeQueryRest.pojo.NativeQueryParam";
	// public static Object $C_CoreDAO =
	// "com.ratata.nativeQueryRest.dao.CoreDAO";

	public static Object $C_Hello = "hello:testProject.Hello";

	// public static Object $Sp_CoreDAO =
	// "com.ratata.nativeQueryRest.dao.CoreDAO";
	// main code

	public static Object hello(String ss) throws Exception {
		return callStaticMethod($C_Hello, "hello", null, null);
	}
}

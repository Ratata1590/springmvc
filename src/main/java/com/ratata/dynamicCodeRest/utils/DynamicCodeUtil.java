package com.ratata.dynamicCodeRest.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class DynamicCodeUtil {
	public static final String SPECIAL_CHAR = "$";
	public static final String SPECIAL_METHOD_CHAR = "$M_";
	public static final String SPECIAL_IMPORT_CHAR = "$C_";
	public static final String SPECIAL_AUTOWIRE_CHAR = "$Sp_";

	public static final List<String> UTILNAME_LIST = Arrays.asList(new String[] { 
			"getBean", 
			"newObj", 
			"getStaticProp",
			"setStaticProp", 
			"callStaticMethod", 
			"getObjProp", 
			"setObjProp", 
			"callObjMethod" });

	public static Class<?>[] revolseObjectParamType(Object... param) {
		Class<?>[] classTypeList = new Class<?>[param.length];
		for (int i = 0; i < param.length; i++) {
			classTypeList[i] = param[i].getClass();
		}
		return classTypeList;
	}

	public static void loadAllClass(Class<?> theClass, ClassLoader classLoader) throws Exception {
		Field[] fields = theClass.getDeclaredFields();
		Method[] thisMethods = DynamicCodeUtil.class.getMethods();
		for (Field field : fields) {
			if (!field.getName().startsWith(SPECIAL_CHAR)) {
				continue;
			}
			if (field.getName().startsWith(SPECIAL_METHOD_CHAR)) {
				String theName = field.getName().substring(SPECIAL_METHOD_CHAR.length());
				if (UTILNAME_LIST.contains(theName)) {
					for (Method mth : thisMethods) {
						if (mth.getName().equals(theName)) {
							field.set(null, mth);
							break;
						}
					}
				}
			}
			if (field.getName().startsWith(SPECIAL_IMPORT_CHAR)) {
				field.set(null, classLoader.loadClass((String) field.get(null)));
			}
			if (field.getName().startsWith(SPECIAL_AUTOWIRE_CHAR)) {
				field.set(null, ShareResourceFromSpring.shareAppContext
						.getBean(classLoader.loadClass((String) field.get(null))));
			}
		}
	}

	public static Object getBean(Object theClass) {
		return ShareResourceFromSpring.shareAppContext.getBean((Class<?>) theClass);
	}

	public static Object newObj(Object theClass, Object... param) throws Exception {
		return ((Class<?>) theClass).getConstructor(revolseObjectParamType(param)).newInstance(param);
	}

	public static Object getStaticProp(Object theClass, String propName) throws Exception {
		return ((Class<?>) theClass).getDeclaredField(propName).get(null);
	}

	public static void setStaticProp(Object theClass, String propName, Object value) throws Exception {
		((Class<?>) theClass).getDeclaredField(propName).set(null, value);
	}

	public static Object getObjProp(Object obj, String propName) throws Exception {
		return obj.getClass().getDeclaredField(propName).get(obj);
	}

	public static void setObjProp(Object obj, String propName, Object value) throws Exception {
		obj.getClass().getDeclaredField(propName).set(obj, value);
	}

	public static Object callStaticMethod(Object theClass, String methodName, Object... param) throws Exception {
		return ((Class<?>) theClass).getMethod(methodName, revolseObjectParamType(param)).invoke(null, param);
	}

	public static Object callObjMethod(Object obj, String methodName, Object... param) throws Exception {
		return obj.getClass().getMethod(methodName, revolseObjectParamType(param)).invoke(obj, param);
	}

}

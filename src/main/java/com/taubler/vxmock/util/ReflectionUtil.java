package com.taubler.vxmock.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taubler.vxmock.io.RuntimeMessager;


public class ReflectionUtil {
	
	public static final String DATE_TIME_FORMAT = DateUtil.ANSI_DATE_TIME_FORMAT;
	private static Logger log = LoggerFactory.getLogger (ReflectionUtil.class);
	
	public static String getUnqualifiedClassName(Object o) {
		String name = o.getClass().getName();
	    if (name.lastIndexOf('.') > 0) {
	    	name = name.substring(name.lastIndexOf('.') + 1); // Map$Entry
	    	name = name.replace('$', '.');      // Map.Entry
	    }
	    return name;
	}

	public static <T> T create(final Class<T> classToCreate) {
	    final Constructor<T> constructor;
	    try {
	        constructor = classToCreate.getDeclaredConstructor();
	        final T result = constructor.newInstance();
	        return result;
	    } catch (Exception e) {
	        throw new RuntimeException("Error trying to construct class of type " + 
	        		classToCreate.getCanonicalName() + "; " + e.getMessage(), e);
	    }
	}

	public static <T extends Annotation> List<Pair<Field, T>> 
			findFieldsWithAnnotation(Class<?> clazz, Class<T> annotationClass) {
		
		List<Pair<Field, T>> retVal = new ArrayList<Pair<Field, T>>();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(annotationClass)) {
				T annotation = field.getAnnotation(annotationClass);
				retVal.add( new Pair<Field, T>(field, annotation) );
			}
		}
		return retVal;
		
	}
	
	public static void merge(Object o, Map<String, String> values) {
		Class<?> clazz = o.getClass();

		for (String key : values.keySet()) {
			try {
				String value = values.get(key);
				Method setter = findSetter(clazz, key);
				if (setter != null) {
					//TODO below, we should be sure that 'value' is of the proper data type
					Class<?> type = setter.getParameterTypes()[0];
					if (type == String.class) { // for UsNeed.transportation, type = java.util.Set; for paymentMethod it is UsNeed$PaymentMethod
						setter.invoke(o, value);
					} else if (type == Long.class || type == long.class) {
						// Sometimes we provide datetime Strings as input for Longs
						// and expect the timestamp to be extracted and set.
						// So if parseLong fails, then try parsing a data string.
						try {
							setter.invoke(o, Long.parseLong(value));
						} catch (NumberFormatException nfe) {
							Date date = new SimpleDateFormat(DATE_TIME_FORMAT).parse(value);
							setter.invoke(o, DateUtil.getUnixTimestamp( date ));
						}
					} else if (type == Integer.class || type == int.class) {
						setter.invoke(o, Integer.parseInt(value));
					} else if (type == Boolean.class || type == boolean.class) {
						setter.invoke(o, StringUtil.boolVal(value));
					} else if (type == Float.class || type == float.class) {
						setter.invoke(o, Float.parseFloat(value));
					} else if (type == Double.class || type == double.class) {
						setter.invoke(o, Double.parseDouble(value));
					} else if (type == Short.class || type == short.class) {
						setter.invoke(o, Short.parseShort(value));
					} else if (type == Byte.class || type == byte.class) {
						setter.invoke(o, Byte.parseByte(value));
					} else if (type == Date.class) {
						boolean done = false;
						if (isProbablyTimestamp(value)) {
							try {
								Long l = Long.parseLong(value);
								Date d = new Date(l);
								setter.invoke(o, d);
								done = true;
							} catch (Exception e) { }
						}
						if (!done) {
							Date date = new SimpleDateFormat(DATE_TIME_FORMAT).parse(value);
							setter.invoke(o, date);
						}
					} else if (type.isEnum()) {
						// by convention, we've been putting byDesc() methods in our custom enums
						try {
							Method method = type.getMethod("byDesc", String.class);
							Object foundEnumObj = method.invoke(o, value);
							setter.invoke(o, foundEnumObj);
						} catch (Exception e) {
							RuntimeMessager.output(String.format("Found a key/value (%s/%s) that should be assigned as enum type %s, but could not find a byDesc() method on that enum", 
									key, value, type.getName()));
						}
					}
				}
			} catch (Exception e) {
				throw new RuntimeException("Error setting object value " + key + " = " + values.get(key), e);
			}
		}
	}

	public static Method findSetter(Class<?> clazz, String name) throws IntrospectionException {
		BeanInfo info = Introspector.getBeanInfo(clazz);
		Method setter = null;
		for ( PropertyDescriptor pd : info.getPropertyDescriptors() ) {
			if (name.equals(pd.getName())) {
				setter = pd.getWriteMethod();
				break;
			}
		}
		if (setter == null) {
			// sometimes this happens even when there is a setter. Don't know why.
			String expectedSetterName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
			for (MethodDescriptor md : info.getMethodDescriptors()) {
				if (md.getName().equals(expectedSetterName)) {
					setter = md.getMethod();
					break;
				}
			}
		}
		return setter;
	}

	public static Method findGetter(Class<?> clazz, String name) throws IntrospectionException {
		BeanInfo info = Introspector.getBeanInfo(clazz);
		for ( PropertyDescriptor pd : info.getPropertyDescriptors() )
			if (name.equals(pd.getName())) return pd.getReadMethod();
		return null;
	}
	
	public static boolean copyField(Object fromObj, Object toObj, String fieldName) {
		Class<?> fromClazz = fromObj.getClass();
		Class<?> toClazz = toObj.getClass();
		try {
			Method setter = findSetter(toClazz, fieldName);
			Method getter = findGetter(fromClazz, fieldName);
			if (setter != null && getter != null) {
				Object val = getter.invoke(fromObj);
				setter.invoke(toObj, val);
				return true;
			}
		} catch (Exception e) {
			log.error("Error copying field " + fieldName + " from " + fromObj + " to " + toObj, e);
		}
		return false;
	}
	
	public static Map<String, Object> copyPropertyValues(Object fromObj, Set<String> excludeProperties) {
	    excludeProperties = (excludeProperties == null) ? new HashSet<String>() : excludeProperties;
	    excludeProperties.add("class"); // be sure we don't include 'class', which appears in every object
	    Map<String, Object> retVal = new HashMap<String, Object>();
	    
        Class<?> fromClazz = fromObj.getClass();
        PropertyDescriptor[] propDescs;
        try {
            propDescs = Introspector.getBeanInfo(fromClazz).getPropertyDescriptors();
            for (PropertyDescriptor propDesc : propDescs) {
                String propName = propDesc.getName();
                if (excludeProperties.contains(propName))
                    continue;
                Method getter = propDesc.getReadMethod();
                try {
                    Object val = getter.invoke(fromObj);
                    retVal.put(propName, val);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    log.error("Error reading property " + propName, e);
                }
            }
        } catch (IntrospectionException e) {
            log.error("Error copying property values.");
        }
	    
	    return retVal;
	}
	
	public static boolean isProbablyTimestamp(String str) {
	    if (str == null) {
	        return false;
	    }
	    int sz = str.length();
	    if (sz > 10 || sz < 8) {
	    	return false;
	    }
	    
	    for (int i = 0; i < sz; i++) {
	        if (Character.isDigit(str.charAt(i)) == false) {
	            return false;
	        }
	    }
	    return true;
	}
}

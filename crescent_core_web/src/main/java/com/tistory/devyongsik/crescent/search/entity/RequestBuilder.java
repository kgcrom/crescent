package com.tistory.devyongsik.crescent.search.entity;

import java.lang.reflect.Field;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;

@Slf4j
public class RequestBuilder<T> {
	
	public T mappingRequestParam(HttpServletRequest request, Class<T> clazz) throws Exception {
		
		T returnObject = clazz.newInstance();
		
		Field[] fields = clazz.getDeclaredFields();
		
		BeanWrapperImpl beanWrapperImpl = new BeanWrapperImpl(returnObject);
		
		for(Field field : fields) {
			
			RequestParamName requestParamName = field.getAnnotation(RequestParamName.class);
			
			String paramName = null;
			String paramValue = null;
			
			if(requestParamName != null) {
				paramName = requestParamName.name();
				
				paramValue = request.getParameter(paramName);
				
				if(paramValue == null) {
					paramValue = requestParamName.defaultValue();
				}
				
				paramValue = URLDecoder.decode(paramValue, "utf-8");
			}
			
			log.debug("field name : {}, paramName : {}, defaultValue : {}", new Object[]{field.getName(), paramName, paramValue});
			
			beanWrapperImpl.setPropertyValue(field.getName(), paramValue);
		}
		
		beanWrapperImpl.setPropertyValue("userIp", request.getRemoteAddr());
		
		return returnObject;
	}
}

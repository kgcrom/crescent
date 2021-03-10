package com.tistory.devyongsik.crescent.search;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

@Slf4j
public class JsonFormConverter {

	public String convert(Object targetObject) {
		
		try {
			
			ObjectMapper mapper = new ObjectMapper();	
			String json = mapper.writeValueAsString(targetObject);
			return json;
			
		} catch (IOException e) {
			log.error("Exception while make json form string.", e);
			throw new IllegalStateException("Exception while make json form string.", e);
		}
	}
}

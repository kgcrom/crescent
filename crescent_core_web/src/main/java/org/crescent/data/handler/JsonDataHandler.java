package org.crescent.data.handler;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.crescent.index.entity.IndexingRequestForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class JsonDataHandler implements Handler {
	private static final Logger logger = LoggerFactory.getLogger(JsonDataHandler.class);
	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public IndexingRequestForm handledData(String jsonFormStr) {
		
		try {
			IndexingRequestForm indexingRequestForm = objectMapper.readValue(jsonFormStr, IndexingRequestForm.class);
			
			return indexingRequestForm;
			
		} catch (IOException e) {
			logger.error("error : ", e);
			throw new IllegalStateException("색인 대상 문서를 변환 중 에러가 발생하였습니다. [" + jsonFormStr +"]");
		}
	}
}

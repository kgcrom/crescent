package com.tistory.devyongsik.crescent.data.handler;

import com.tistory.devyongsik.crescent.index.entity.IndexingRequestForm;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * author : need4spd, need4spd@naver.com, 2012. 3. 17.
 * 
 * 파일로부터 Json 포맷의 데이터를 읽어와서 Gson을 사용 Document 타입으로 변환한다.
 */
@Slf4j
public class JsonDataHandler implements Handler {

	@Override
	public IndexingRequestForm handledData(String jsonFormStr) {
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			
			IndexingRequestForm indexingRequestForm = mapper.readValue(jsonFormStr, IndexingRequestForm.class);
			
			return indexingRequestForm;
			
		} catch (IOException e) {
			log.error("error : ", e);
			throw new IllegalStateException("색인 대상 문서를 변환 중 에러가 발생하였습니다. [" + jsonFormStr +"]");
		}
	}
}

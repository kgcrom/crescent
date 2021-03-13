package com.tistory.devyongsik.crescent.search.entity;

import java.util.Map;

import com.tistory.devyongsik.crescent.collection.entity.CrescentCollection;
import com.tistory.devyongsik.crescent.collection.entity.CrescentCollectionField;
import com.tistory.devyongsik.crescent.config.CrescentCollectionHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

@Slf4j
public class SearchRequestValidator {
	
	public boolean isValid(SearchRequest searchRequest, CrescentCollectionHandler collectionHandler) throws Exception {
		
		CrescentCollection collection = collectionHandler.getCrescentCollections()
													.getCrescentCollection(searchRequest.getCollectionName());
		
		if(collection == null) {
			log.error("invalid collection name: {}", searchRequest.getCollectionName());
			throw new Exception();
		}
		
		
		//request search field
		if(searchRequest.getSearchField() != null) {
			String[] requestSearchFieldNames = searchRequest.getSearchField().split(",");
			
			if(requestSearchFieldNames != null && requestSearchFieldNames[0].length() > 0) {
				for(String requestFieldName : requestSearchFieldNames) {
					if(!collection.getCrescentFieldByName().containsKey(requestFieldName)) {
						log.error("invalid search field: ", searchRequest.getSearchField());
						throw new Exception();
					}
				}
			}
		}
		
		//page num
		if(searchRequest.getPageNum() != null && !StringUtils.isNumeric(searchRequest.getPageNum())) {
			log.error("invalid page number: {}", searchRequest.getPageNum());
			throw new Exception();
		}
		
		//request sort field
		String sortQueryString = searchRequest.getSort();
		if(sortQueryString == null || "".equals(sortQueryString) || "null".equals(sortQueryString)) {
			//nothing
		} else {
	
			String[] parts = sortQueryString.split(",");
			if(parts.length == 0) {
				log.error("invalid sortquery syntax: {}", sortQueryString);
				throw new Exception();
			}
	
			for(int i = 0; i < parts.length; i++) {
				String part = parts[i].trim(); //part = field desc
				
				int idx = part.indexOf( ' ' );
				
				if(idx <= 0) {
					log.error("has no order condition: {}", sortQueryString);
					throw new Exception();
				}
				
				part = part.substring( 0, idx ).trim(); //part = field
				Map<String, CrescentCollectionField> collectionFields = collection.getCrescentFieldByName();
				
				CrescentCollectionField f = collectionFields.get(part);
				
				if(f == null) {
					log.error("don't exist field: {}", String.join(", ", parts));
					throw new Exception();
				}
				if(f.isAnalyze()) {
					log.error("analyzed filed are not sortable, {}", String.join(", ", parts));
					throw new Exception();
				}
			}
		}
		
		return true;
	}
}

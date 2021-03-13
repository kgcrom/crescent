package com.tistory.devyongsik.crescent.search.service;

import com.tistory.devyongsik.crescent.config.CrescentCollectionHandler;
import com.tistory.devyongsik.crescent.query.CrescentSearchRequestWrapper;
import com.tistory.devyongsik.crescent.search.entity.SearchRequest;
import com.tistory.devyongsik.crescent.search.entity.SearchRequestValidator;
import com.tistory.devyongsik.crescent.search.entity.SearchResult;
import com.tistory.devyongsik.crescent.search.searcher.CrescentDocSearcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.Query;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

	private final CrescentDocSearcher crescentDocSearcher;
	private final CrescentCollectionHandler collectionHandler;

	public SearchServiceImpl(CrescentDocSearcher crescentDocSearcher, CrescentCollectionHandler collectionHandler) {
		this.crescentDocSearcher = crescentDocSearcher;
		this.collectionHandler = collectionHandler;
	}

	@Override
	public SearchResult search(SearchRequest searchRequest) throws IOException {
		
		Query query = null;
		CrescentSearchRequestWrapper csrw 
				= new CrescentSearchRequestWrapper(searchRequest, collectionHandler);
	
		try {
			
			SearchRequestValidator validator = new SearchRequestValidator();
			validator.isValid(searchRequest, collectionHandler);
			
			
			query = csrw.getQuery();
		
		} catch (Exception e) {
			SearchResult searchResult = new SearchResult();
			
			Map<String, Object> result = new HashMap<String, Object>();
			List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
			
			result.put("total_count", 0);
			result.put("result_list", resultList);
			result.put("error_code", -1);
			result.put("error_msg", e.getMessage());
			
			log.error("검색 중 에러 발생함." , e);
			
			searchResult.setErrorCode(-1);
			searchResult.setErrorMsg(e.getMessage());
			searchResult.setSearchResult(result);
			searchResult.setResultList(resultList);
			
			return searchResult;
		}
		
		log.debug("query : {}" , query);
		
		SearchResult searchResult = crescentDocSearcher.search(csrw);
		
		return searchResult;
	}

}

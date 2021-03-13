package com.tistory.devyongsik.crescent.search.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tistory.devyongsik.crescent.search.entity.SearchRequest;
import com.tistory.devyongsik.crescent.search.entity.SearchResult;
import com.tistory.devyongsik.utils.CrescentTestCaseUtil;
import java.io.IOException;
import javax.annotation.PostConstruct;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class SearchServiceImplTest extends CrescentTestCaseUtil {

	@PostConstruct
	public void init() throws Exception {
		super.init();
	}

	@Disabled
	@Test
	public void search() throws IOException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setCollectionName("sample");
		searchRequest.setKeyword("1");
		
		SearchResult searchResult = searchService.search(searchRequest);
		
		assertTrue(searchResult.getTotalHitsCount() > 0);
	}
}

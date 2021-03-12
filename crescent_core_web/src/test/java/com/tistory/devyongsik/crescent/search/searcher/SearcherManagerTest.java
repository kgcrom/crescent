package com.tistory.devyongsik.crescent.search.searcher;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import javax.annotation.PostConstruct;


import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherManager;

import com.tistory.devyongsik.utils.CrescentTestCaseUtil;
import org.junit.jupiter.api.Test;

public class SearcherManagerTest extends CrescentTestCaseUtil {

	@PostConstruct
	public void init() throws Exception {
		super.init();
	}
	
	@Test
	public void initSearcherManager() {
		
		assertNotNull(crescentSearcherManager);
	}
	
	@Test
	public void getSearcher() throws IOException {
		IndexSearcher indexSearcher = null;
		SearcherManager searcherManager = crescentSearcherManager.getSearcherManager("sample");
		
		indexSearcher = searcherManager.acquire();
		
		assertNotNull(indexSearcher);
	}
}

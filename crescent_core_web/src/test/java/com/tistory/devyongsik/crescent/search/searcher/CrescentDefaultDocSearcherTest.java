package com.tistory.devyongsik.crescent.search.searcher;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tistory.devyongsik.crescent.query.CrescentSearchRequestWrapper;
import com.tistory.devyongsik.crescent.search.entity.SearchRequest;
import com.tistory.devyongsik.crescent.search.entity.SearchResult;
import com.tistory.devyongsik.utils.CrescentTestCaseUtil;
import java.io.IOException;
import javax.annotation.PostConstruct;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class CrescentDefaultDocSearcherTest extends CrescentTestCaseUtil {

	@PostConstruct
	public void init() throws Exception {
		super.init();
	}

	@Disabled
	@Test
	public void search() throws IOException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeyword("1");
		searchRequest.setCollectionName("sample");
		
		CrescentSearchRequestWrapper csrw = new CrescentSearchRequestWrapper(searchRequest, this.collectionHandler);
		
		SearchResult searchResult = crescentDocSearcher.search(csrw);
		
		assertTrue(searchResult.getResultList().size() > 0);
	}
}

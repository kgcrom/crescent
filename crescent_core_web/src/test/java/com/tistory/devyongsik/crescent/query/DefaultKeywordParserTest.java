package com.tistory.devyongsik.crescent.query;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tistory.devyongsik.crescent.search.entity.SearchRequest;
import com.tistory.devyongsik.crescent.search.exception.CrescentInvalidRequestException;
import com.tistory.devyongsik.utils.CrescentTestCaseUtil;
import javax.annotation.PostConstruct;
import org.apache.lucene.search.Query;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class DefaultKeywordParserTest extends CrescentTestCaseUtil {

	@PostConstruct
	public void init() {
		super.init();
	}

	@Disabled
	@Test
	public void keywordParse() throws CrescentInvalidRequestException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setCollectionName("sample");
		searchRequest.setKeyword("나이키청바지");
		
		CrescentSearchRequestWrapper csrw 
			= new CrescentSearchRequestWrapper(searchRequest, this.collectionHandler);
		
		Query query = csrw.getQuery();
		
		System.out.println(query);
		
		assertEquals("title:나이키청바지^2.0 title:청바지^2.0 title:나이키^2.0 title:나이키청바^2.0 +dscr:나이키청바지 +dscr:청바지 +dscr:나이키 +dscr:나이키청바",
				query.toString());
	}
}

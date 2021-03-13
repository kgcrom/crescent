package com.tistory.devyongsik.crescent.search.searcher;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import com.tistory.devyongsik.utils.CrescentTestCaseUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class IndexSearcherTest extends CrescentTestCaseUtil {

	@PostConstruct
	public void init() throws Exception {
		super.init();
	}

	@Disabled
	@Test
	public void defaultSearch() throws IOException {
		SearcherManager searcherManager = crescentSearcherManager.getSearcherManager("sample");
		IndexSearcher indexSearcher = searcherManager.acquire();
		
		Term t = new Term("board_id", "2");
		Query q = new TermQuery(t);
		
		TopDocs topDocs = indexSearcher.search(q, 5);
		
		int totalCount = topDocs.totalHits;
		
		System.out.print("total count : " + totalCount);
		
		assertTrue(totalCount > 0);
	}
}

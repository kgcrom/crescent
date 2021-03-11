package com.tistory.devyongsik.crescent.search.highlight;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.vectorhighlight.FastVectorHighlighter;

@Slf4j
public class CrescentFastVectorHighlighter {

	private FastVectorHighlighter highlighter = new FastVectorHighlighter();
	
	public String getBestFragment(IndexReader indexReader, int docId, Query query, String fieldName) {
		
		log.debug("get highlight... {}, {}", docId, query);
		
		try {
			
			String fragment = highlighter.getBestFragment(highlighter.getFieldQuery(query),
					indexReader, docId, fieldName, 200);
			
			return fragment;
			
		} catch (Exception e) {
			log.error("highlighter error : ", e);
			
			return "";
		}
		
	}

}

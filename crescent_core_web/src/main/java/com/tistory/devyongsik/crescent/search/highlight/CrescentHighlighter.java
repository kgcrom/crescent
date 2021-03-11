package com.tistory.devyongsik.crescent.search.highlight;

import com.tistory.devyongsik.crescent.collection.entity.CrescentCollectionField;
import com.tistory.devyongsik.crescent.search.exception.CrescentInvalidRequestException;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;

import java.io.IOException;
import java.io.StringReader;

@Slf4j
@Deprecated
public class CrescentHighlighter {

	private SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<b>","</b>");

	public String getBestFragment(CrescentCollectionField field, String value, Query query, Analyzer analyzer) throws CrescentInvalidRequestException {
		String fragment = "";

		log.debug("fieldName : {}", field.getName());

		try {
			
			log.debug("query for highlighter : {}" , query);

			QueryScorer scorer = new QueryScorer(query);

			Highlighter highlighter = new Highlighter(formatter, scorer);
			highlighter.setTextFragmenter(new SimpleFragmenter(50));


			TokenStream stream = analyzer.tokenStream(field.getName(), new StringReader(value));
			fragment = highlighter.getBestFragments(stream, value, 1, "...");

			if(fragment == null || "".equals(fragment)) {
				if(value.length() > 100) {
					fragment = value.substring(0,100);
				} else {
					fragment = value;
				}
			}

			return fragment;
			
		} catch (IOException e) {

			log.error("error in crescent highlighter", e);

			return value;
			
		} catch (InvalidTokenOffsetsException e) {

			log.error("error in crescent highlighter", e);

			return value;
		}

	}
}

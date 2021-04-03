package com.tistory.devyongsik.crescent.query;

import com.tistory.devyongsik.crescent.collection.entity.CollectionField;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DefaultKeywordParser {

	protected Query parse(List<CollectionField> searchFields, String keyword, Analyzer analyzer) throws IOException {
	
		log.debug("search fields : {}", searchFields);
		
		BooleanQuery resultQuery = new BooleanQuery();

		//검색어를 split
		String[] keywords = keyword.split( " " );

		for(int i = 0; i < keywords.length; i++) {
			ArrayList<String> analyzedTokenList = analyzedTokenList(analyzer, keywords[i]);

			//필드만큼 돌아간다..
			for(CollectionField field : searchFields) {
				if(analyzedTokenList.size() == 0) { //색인되어 나온 것이 없으면
					Term t = new Term(field.getName(), keywords[i]);
					Query query = new TermQuery(t);
					if(field.getBoost() > 1F) {
						query.setBoost(field.getBoost());
					}
					resultQuery.add(query, Occur.SHOULD);
				} else {
					for(String str : analyzedTokenList) {
						
						Term t = new Term(field.getName(), str);
						Query query = new TermQuery(t);
						if(field.getBoost() > 1F) {
							query.setBoost(field.getBoost());
						}
						
						resultQuery.add(query, field.getOccur());
					}
				}
			}
		}

		log.debug("검색 쿼리 : {}", resultQuery);
		return resultQuery;
	}

	private ArrayList<String> analyzedTokenList(Analyzer analyzer, String splitedKeyword) throws IOException {

		ArrayList<String> rst = new ArrayList<String>();
		//split된 검색어를 Analyze..
		TokenStream stream = null;
		
		try {
			stream = analyzer.tokenStream("", new StringReader(splitedKeyword));
		} catch (IOException e1) {
			log.error("Error in analyzed Token List", e1);
			throw new IllegalStateException("키워드 분석 중 에러가 발생하였습니다. [" + splitedKeyword + "]");	
		}
		
		CharTermAttribute charTerm = stream.getAttribute(CharTermAttribute.class);
		

		try {
			stream.reset();
			
			while(stream.incrementToken()) {
				rst.add(charTerm.toString());
			}
			
			stream.close();
			
		} catch (IOException e) {
			log.error("error in DefaultKeywordParser : ", e);
			throw e;
		}

		log.debug("[{}] 에서 추출된 명사 : [{}]", new Object[]{splitedKeyword, rst.toString()});
			

		return rst;
	}

	final protected Query getWildcardQuery(String field, String term) throws ParseException {
		throw new ParseException("no use wild card");
	}

	final protected Query getFuzzyQuery(String field, String term) throws ParseException {
		throw new ParseException("no use fuzzy");
	}
}

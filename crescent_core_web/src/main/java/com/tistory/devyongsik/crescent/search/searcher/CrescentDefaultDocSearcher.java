package com.tistory.devyongsik.crescent.search.searcher;

import com.tistory.devyongsik.crescent.collection.entity.Collection;
import com.tistory.devyongsik.crescent.collection.entity.CollectionField;
import com.tistory.devyongsik.crescent.config.CrescentCollectionHandler;
import com.tistory.devyongsik.crescent.query.CrescentSearchRequestWrapper;
import com.tistory.devyongsik.crescent.search.entity.SearchResult;
import com.tistory.devyongsik.crescent.search.highlight.CrescentFastVectorHighlighter;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CrescentDefaultDocSearcher implements CrescentDocSearcher {

	private final CrescentSearcherManager crescentSearcherManager;
	private final CrescentCollectionHandler collectionHandler;

	public CrescentDefaultDocSearcher(CrescentSearcherManager crescentSearcherManager, CrescentCollectionHandler collectionHandler) {
		this.crescentSearcherManager = crescentSearcherManager;
		this.collectionHandler = collectionHandler;
	}

	@Override
	public SearchResult search(CrescentSearchRequestWrapper csrw) throws IOException {
		
		SearchResult searchResult = new SearchResult();
		int totalHitsCount = 0;
		String errorMessage = "SUCCESS";
		int errorCode = 0;
		
		//5page * 50
		int numOfHits = csrw.getDefaultHitsPage() * csrw.getHitsForPage();
		IndexSearcher indexSearcher = null;
		SearcherManager searcherManager = crescentSearcherManager.getSearcherManager(csrw.getCollectionName());
		
		try {
			indexSearcher = searcherManager.acquire();
			
			Query query = csrw.getQuery();
			Filter filter = csrw.getFilter();
			Sort sort = csrw.getSort();
			
			log.debug("query : {}" , query);
			log.debug("filter : {}" , filter);
			log.debug("sort : {}" , sort);
			
			TopDocs topDocs = null;
			
			if(sort == null) {
				topDocs = indexSearcher.search(query, filter, numOfHits);
			} else {
				topDocs = indexSearcher.search(query, filter, numOfHits, sort);
			}
			
			//전체 검색 건수
			// TODO logging search result
			// csrw에 정보와 elapsed time, query, total hit
			totalHitsCount = topDocs.totalHits;
			log.debug("Total Hits Count : {} ", totalHitsCount);
			
			ScoreDoc[] hits = topDocs.scoreDocs;
			
			//총 검색건수와 실제 보여줄 document의 offset (min ~ max)를 비교해서 작은 것을 가져옴
			int endOffset = Math.min(totalHitsCount, csrw.getStartOffSet() + csrw.getHitsForPage());
			
			if(endOffset > hits.length) {
				log.debug("기본 설정된 검색건수보다 더 검색을 원하므로, 전체를 대상으로 검색합니다.");
				
				if(sort == null) {
					topDocs = indexSearcher.search(query, filter, totalHitsCount);
				} else {
					topDocs = indexSearcher.search(query, filter, totalHitsCount, sort);
				}
				
		        hits = topDocs.scoreDocs;
			}
	
			int startOffset = csrw.getStartOffSet();
			endOffset = Math.min(hits.length, startOffset + csrw.getHitsForPage());
									
			log.debug("start offset : [{}], end offset : [{}], total : [{}], numOfHits :[{}]"
							,new Object[]{csrw.getStartOffSet(), endOffset, totalHitsCount, numOfHits});
			log.debug("hits count : [{}]", hits.length);
			log.debug("startOffset + hitsPerPage : [{}]", csrw.getStartOffSet() + csrw.getHitsForPage());
			
			
			if(totalHitsCount > 0) { 
				List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
				Map<String, Object> result = new HashMap<String, Object>();
				
				CrescentFastVectorHighlighter highlighter = new CrescentFastVectorHighlighter();
				
				Collection collection = collectionHandler.getCrescentCollections().getCrescentCollection(csrw.getCollectionName());
				
				//int docnum = 0;
				for(int i = startOffset; i < endOffset; i++) {
					
					Map<String,String> resultMap = new HashMap<String, String>();
					
					for(CollectionField field : collection.getFields()) {
						String value = null;
								
						if(field.isStore() && !field.isNumeric()) {
							
							//필드별 결과를 가져온다.
							value = highlighter.getBestFragment(indexSearcher.getIndexReader(), hits[i].doc, query, field.getName());
								
						}
						
						if(value == null || value.length() == 0) {
							Document doc = indexSearcher.doc(hits[i].doc);
							value = doc.get(field.getName());		
						}
						
						resultMap.put(field.getName(), value);
					}
					
					resultList.add(resultMap);
				}
				
				result.put("total_count", totalHitsCount);
				result.put("result_list", resultList);
				result.put("error_code", errorCode);
				result.put("error_msg", errorMessage);
				
				log.debug("result list {}", resultList);
				
				searchResult.setResultList(resultList);
				searchResult.setTotalHitsCount(totalHitsCount);
				searchResult.setSearchResult(result);
				
			} else {
				
				//결과없음
				Map<String, Object> result = new HashMap<String, Object>();
				List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
				
				result.put("total_count", totalHitsCount);
				result.put("result_list", resultList);
				result.put("error_code", errorCode);
				result.put("error_msg", errorMessage);
				
				
				log.debug("result list {}", resultList);
				
				searchResult.setResultList(resultList);
				searchResult.setTotalHitsCount(0);
				searchResult.setSearchResult(result);
			
			}
			
			
		} catch (Exception e) {
			
			log.error("error in CrescentDefaultDocSearcher : ", e);
			
			Map<String, Object> result = new HashMap<String, Object>();
			List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
			
			result.put("total_count", totalHitsCount);
			result.put("result_list", resultList);
			result.put("error_code", errorCode);
			result.put("error_msg", errorMessage);
			
			log.error("검색 중 에러 발생함. {}", e);
			
			searchResult.setErrorCode(errorCode);
			searchResult.setErrorMsg(errorMessage);
			searchResult.setSearchResult(result);
			searchResult.setResultList(resultList);
			
			return searchResult;
		} finally {
			searcherManager.release(indexSearcher);
		}
		
		return searchResult;
	}
}

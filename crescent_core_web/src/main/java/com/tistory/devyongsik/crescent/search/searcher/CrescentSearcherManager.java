package com.tistory.devyongsik.crescent.search.searcher;

import com.tistory.devyongsik.crescent.IndexWriterManager;
import com.tistory.devyongsik.crescent.collection.entity.CrescentCollection;
import com.tistory.devyongsik.crescent.collection.entity.CrescentCollections;
import com.tistory.devyongsik.crescent.config.CrescentCollectionHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component("crescentSearcherManager")
public class CrescentSearcherManager {

	private Map<String, SearcherManager> searcherManagerByCollection = new ConcurrentHashMap<String, SearcherManager>();

	@Autowired
	@Qualifier("crescentCollectionHandler")
	private CrescentCollectionHandler collectionHandler;
	
	@Autowired
	@Qualifier("indexWriterManager")
	private IndexWriterManager indexWriterManager;

	@PostConstruct
	private void indexSearcherInit() {
		
		log.info("indexSearcherManager init start.....");
		
		CrescentCollections collections = collectionHandler.getCrescentCollections();
		
		Map<String, CrescentCollection> collectionsMap = collections.getCrescentCollectionsMap();
		
		Set<String> collectionNames = collectionsMap.keySet();
		
		for(String collectionName : collectionNames) {
			
			log.info("collection name {}", collectionName);
			
			SearcherFactory searcherFactory = new SearcherFactory();
			IndexWriter indexWriter = indexWriterManager.getIndexWriter(collectionName);
			SearcherManager searcherManager = null;
			
			try {
			
				searcherManager = new SearcherManager(indexWriter, true, searcherFactory);
			
			} catch (IOException e) {
				log.error("index searcher init error ", e);
				throw new RuntimeException("index searcher init error ", e);
			}
			
			searcherManagerByCollection.put(collectionName, searcherManager);
			
			log.info("searcher manager created....");
		}
	}
	
	public SearcherManager getSearcherManager(String collectionName) {
		SearcherManager searcherManager = searcherManagerByCollection.get(collectionName);
		
		try {
			
			searcherManager.maybeRefresh();
		
		} catch (IOException e) {
		
			log.error("exception in CrescentSearcherManager : {}", e);
			new IllegalStateException("SearcherManager maybeRefresh Exception in " + collectionName + ".");
		
		}
		
		return searcherManager;
	}
}

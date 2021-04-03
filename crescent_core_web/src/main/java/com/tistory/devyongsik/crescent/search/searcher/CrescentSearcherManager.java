package com.tistory.devyongsik.crescent.search.searcher;

import com.tistory.devyongsik.crescent.IndexWriterManager;
import com.tistory.devyongsik.crescent.collection.entity.Collection;
import com.tistory.devyongsik.crescent.collection.entity.Collections;
import com.tistory.devyongsik.crescent.config.CrescentCollectionHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class CrescentSearcherManager {

	private Map<String, SearcherManager> searcherManagerByCollection = new ConcurrentHashMap<String, SearcherManager>();

	private final CrescentCollectionHandler collectionHandler;
	private final IndexWriterManager indexWriterManager;

	public CrescentSearcherManager(CrescentCollectionHandler collectionHandler, IndexWriterManager indexWriterManager) {
		this.collectionHandler = collectionHandler;
		this.indexWriterManager = indexWriterManager;
	}

	@PostConstruct
	private void indexSearcherInit() throws IOException {
		log.info("indexSearcherManager init start.....");
		
		Collections collections = collectionHandler.getCrescentCollections();
		Map<String, Collection> collectionsMap = collections.getCrescentCollectionsMap();
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
				throw e;
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

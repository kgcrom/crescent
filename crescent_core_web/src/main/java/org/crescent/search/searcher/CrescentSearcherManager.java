package org.crescent.search.searcher;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.crescent.IndexWriterManager;
import org.crescent.config.CollectionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CrescentSearcherManager {

	private static final Logger logger = LoggerFactory.getLogger(CrescentSearcherManager.class);

	private Map<String, SearcherManager> searcherManagerByCollection = new ConcurrentHashMap<String, SearcherManager>();

	private final CollectionHandler collectionHandler;
	private final IndexWriterManager indexWriterManager;

	public CrescentSearcherManager(CollectionHandler collectionHandler, IndexWriterManager indexWriterManager) throws IOException{
		this.collectionHandler = collectionHandler;
		this.indexWriterManager = indexWriterManager;

		init();
	}

	private void init() throws IOException {
		for(String collectionName : collectionHandler.getCollectionMap().keySet()) {
			SearcherFactory searcherFactory = new SearcherFactory();
			IndexWriter indexWriter = indexWriterManager.getIndexWriter(collectionName);
			SearcherManager searcherManager;
			
			searcherManager = new SearcherManager(indexWriter, searcherFactory);
			searcherManagerByCollection.put(collectionName, searcherManager);
		}
	}
	
	public SearcherManager getSearcherManager(String collectionName) {
		SearcherManager searcherManager = searcherManagerByCollection.get(collectionName);
		
		try {
			searcherManager.maybeRefresh();
		} catch (IOException e) {
			logger.error("exception in CrescentSearcherManager : {}", collectionName, e);
			throw new IllegalStateException("SearcherManager maybeRefresh Exception in " + collectionName + ".");
		}
		return searcherManager;
	}
}

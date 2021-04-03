package com.tistory.devyongsik.crescent;

import com.tistory.devyongsik.crescent.collection.entity.Collection;
import com.tistory.devyongsik.crescent.config.CrescentCollectionHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class IndexWriterManager {
	private Map<String, IndexWriter> indexWritersByCollectionName = new ConcurrentHashMap<String, IndexWriter>();

	private final CrescentCollectionHandler collectionHandler;

	public IndexWriterManager(CrescentCollectionHandler collectionHandler) {
		this.collectionHandler = collectionHandler;
	}

	@PostConstruct
	private void initIndexWriter() throws IOException {
		
		for(Collection collection : collectionHandler.getCrescentCollections().getCrescentCollections()) {
			log.info("collection name {}", collection.getName());
			String indexDir = collection.getIndexingDirectory();
			log.info("index file dir ; {}", indexDir);
			
			Directory dir;
			if(indexDir.equals("memory")) {
				dir = new RAMDirectory();
			} else {
				dir = FSDirectory.open(new File(indexDir));
			}

			IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_44, collection.getIndexingModeAnalyzer());
			conf.setOpenMode(OpenMode.CREATE_OR_APPEND);

			IndexWriter indexWriter = new IndexWriter(dir, conf);
			indexWritersByCollectionName.put(collection.getName(), indexWriter);

			log.info("index writer for collection {} is initialized...", collection.getName());
		}
	}
	
	public IndexWriter getIndexWriter(String collectionName) {
		return indexWritersByCollectionName.get(collectionName);
	}
}

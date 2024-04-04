package org.crescent;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;
import org.crescent.collection.entity.Collection;
import org.crescent.config.CollectionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IndexWriterManager {

	private static final Logger logger = LoggerFactory.getLogger(IndexWriterManager.class);
	private Map<String, IndexWriter> indexWritersByCollectionName = new ConcurrentHashMap<>();

	private final CollectionHandler collectionHandler;

	public IndexWriterManager(CollectionHandler collectionHandler) throws IOException{
		this.collectionHandler = collectionHandler;
		init();
	}

	private void init() throws IOException {
		for(Collection collection : collectionHandler.getCollections()) {
			String indexDir = collection.getIndexingDirectory();

			Directory dir;
			if(indexDir.equals("memory")) {
				Path tempPath = Files.createTempDirectory(Paths.get(System.getProperty("java.io.tmpdir")), "memory");
				dir = new MMapDirectory(tempPath);
			} else {
				dir = FSDirectory.open(Paths.get(indexDir));
			}

			IndexWriterConfig conf = new IndexWriterConfig(collection.getIndexingModeAnalyzer());
			conf.setOpenMode(OpenMode.CREATE_OR_APPEND);

			IndexWriter indexWriter = new IndexWriter(dir, conf);
			indexWritersByCollectionName.put(collection.getName(), indexWriter);

			logger.info("finish that initialized index writer. Collection: {}, Index Dir: {}", collection.getName(), indexDir);
		}
	}
	
	public IndexWriter getIndexWriter(String collectionName) {
		return indexWritersByCollectionName.get(collectionName);
	}
}

package org.crescent.index.indexer;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.crescent.IndexWriterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CrescentIndexer {

	private static final Logger logger = LoggerFactory.getLogger(CrescentIndexer.class);
	private final IndexWriterManager indexWriterManager;

	public CrescentIndexer(IndexWriterManager indexWriterManager) {
		this.indexWriterManager = indexWriterManager;
	}

	// TODO execute parallel index
	public void addDocument(List<Document> documentList, String collectionName) throws IOException, IllegalArgumentException {
		IndexWriter indexWriter = indexWriterManager.getIndexWriter(collectionName);

		if (indexWriter == null) {
			throw new IllegalArgumentException("doesn't exist collection: " + collectionName);
		}

		indexWriter.addDocuments(documentList);
		logger.info("total indexed document count {}", documentList.size());
	}
	
	public void updateDocuments(Term term, List<Document> documents, String collectionName) throws IOException, IllegalArgumentException {
		IndexWriter indexWriter = indexWriterManager.getIndexWriter(collectionName);

		if (indexWriter == null) {
			throw new IllegalArgumentException("doesn't exist collection: " + collectionName);
		}
		indexWriter.updateDocuments(term, documents);
	}
	
	public void updateDocument(Term term, Document document, String collectionName) throws IOException, IllegalArgumentException {
		IndexWriter indexWriter = indexWriterManager.getIndexWriter(collectionName);

		if (indexWriter == null) {
			throw new IllegalArgumentException("doesn't exist collection: " + collectionName);
		}
		indexWriter.updateDocument(term, document);
	}
	
	public void deleteDocument(Query query, String collectionName) throws IOException, IllegalArgumentException {
		IndexWriter indexWriter = indexWriterManager.getIndexWriter(collectionName);
		if (indexWriter == null) {
			throw new IllegalArgumentException("doesn't exist collection: " + collectionName);
		}
		indexWriter.deleteDocuments(query);
	}
	
	public void commit(String collectionName) throws IOException, IllegalArgumentException {
		
		IndexWriter indexWriter = indexWriterManager.getIndexWriter(collectionName);

		if (indexWriter == null) {
			throw new IllegalArgumentException("doesn't exist collection: " + collectionName);
		}

		logger.info("Commit {}", collectionName);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Map<String, String> indexUserData = new HashMap<>();

		indexUserData.put("lastModified", dateFormat.format(OffsetDateTime.now(ZoneId.of("Asia/Seoul"))));
		indexWriter.setLiveCommitData(indexUserData.entrySet());
		indexWriter.commit();
	}
}

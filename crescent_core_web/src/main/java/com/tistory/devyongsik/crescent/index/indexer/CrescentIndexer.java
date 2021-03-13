package com.tistory.devyongsik.crescent.index.indexer;

import com.tistory.devyongsik.crescent.IndexWriterManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
public class CrescentIndexer {

	private final IndexWriterManager indexWriterManager;

	public CrescentIndexer(IndexWriterManager indexWriterManager) {
		this.indexWriterManager = indexWriterManager;
	}

	public void addDocument(List<Document> documentList, String collectionName) throws IOException {
		
		IndexWriter indexWriter = indexWriterManager.getIndexWriter(collectionName);
		
		try {
			log.info("collectionName : {}", collectionName);
			log.info("add indexing start................");
			
			int indexingDocumentCount = 0;
			for(Document doc : documentList) {
				indexingDocumentCount++;
				if((indexingDocumentCount%50000) == 0) {
					log.info("{} indexed...", indexingDocumentCount);
				}
				
				indexWriter.addDocument(doc);
			}
			
			log.info("total indexed document count {}", indexingDocumentCount);
			log.info("end");
		} catch (IOException e) {
			log.error("indexer ioexception", e);
			throw e;
			
		}
	}
	
	public void updateDocuments(Term term, List<Document> documents, String collectionName) throws Exception {
		
		IndexWriter indexWriter = indexWriterManager.getIndexWriter(collectionName);
		
		try {
			
			log.info("collectionName : {}", collectionName);
			log.info("update indexing start................{}, size : {}", term, documents.size());
			
			indexWriter.updateDocuments(term, documents);
					
			log.info("end");
			
		} catch (IOException e) {
			
			log.error("update document ioexception", e);
			throw new Exception();
			
		}
	}
	
	public void updateDocument(Term term, Document document, String collectionName) throws Exception {
		
		IndexWriter indexWriter = indexWriterManager.getIndexWriter(collectionName);
		
		try {
			
			log.info("collectionName : {}", collectionName);
			log.info("update indexing start................{}", term);
			
			indexWriter.updateDocument(term, document);
					
			log.info("end");
			
		} catch (IOException e) {
			
			log.error("update document ioexception", e);
			throw new Exception();
			
		}
	}
	
	public void deleteDocument(Query query, String collectionName) throws Exception {
		
		IndexWriter indexWriter = indexWriterManager.getIndexWriter(collectionName);
		
		try {
			
			log.info("collectionName : {}", collectionName);
			log.info("delete indexing start................ {}", query);
			
			indexWriter.deleteDocuments(query);
					
			log.info("end");
			
		} catch (IOException e) {
			
			log.error("delete document ioexception", e);
			throw new Exception();
			
		}
	}
	
	public void commit(String collectionName) throws Exception {
		
		IndexWriter indexWriter = indexWriterManager.getIndexWriter(collectionName);
		
		try {
			
			log.info("Commit {}", collectionName);
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Map<String, String> indexUserData = new HashMap<String, String>();
			indexUserData.put("lastModified", dateFormat.format(new Date()));
			indexWriter.setCommitData(indexUserData);
			
			indexWriter.commit();
		} catch (CorruptIndexException e) {
			log.error("commit corruptindex exception", e);
			throw new Exception();
		} catch (IOException e) {
			log.error("commit ioexception", e);
			throw new Exception();
		
		}
	}
}

package org.crescent.index.indexer;

import com.google.common.base.Splitter;
import org.crescent.collection.entity.Collection;
import org.crescent.index.LuceneDocumentBuilder;
import org.crescent.index.entity.IndexingCommand;
import org.crescent.index.entity.IndexingRequestForm;
import org.crescent.index.entity.IndexingType;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CrescentIndexerExecutor {

	private static final Logger logger = LoggerFactory.getLogger(CrescentIndexerExecutor.class);
	private final CrescentIndexer crescentIndexer;

	public CrescentIndexerExecutor(CrescentIndexer crescentIndexer) {
		this.crescentIndexer = crescentIndexer;
	}

	public String execute(Collection collection, IndexingRequestForm indexingRequestForm) throws Exception {
		
		logger.info("indexingRequestForm : {}", indexingRequestForm);
		
		IndexingType indexingType = IndexingType.valueOf(indexingRequestForm.getIndexingType().toUpperCase());
		IndexingCommand indexingCommand = IndexingCommand.valueOf(indexingRequestForm.getCommand().toUpperCase());
		String query = indexingRequestForm.getQuery();
		List<String> splitQuery = Splitter.on(":").limit(2).splitToList(query);
		
		logger.info("Indexing type : {} , Indexing command : {} ", indexingType, indexingCommand);
		logger.info("Query : {}", query);
		
		String resultMessage = "Nothing To Execute...";
		
		if(IndexingCommand.ADD == indexingCommand) {
			List<Document> documentList = indexingRequestForm.getDocumentList().stream()
					.map(v -> LuceneDocumentBuilder.buildDocumentList(v, collection.getCrescentFieldByName()))
					.collect(Collectors.toList());
			crescentIndexer.addDocument(documentList, collection.getName());
		
			resultMessage = documentList.size() + "건의 색인이 완료되었습니다.";
			
		} else if (IndexingCommand.UPDATE == indexingCommand) {
			
			List<Document> documentList = indexingRequestForm.getDocumentList().stream()
					.map(v -> LuceneDocumentBuilder.buildDocumentList(v, collection.getCrescentFieldByName()))
					.collect(Collectors.toList());
			
			if(documentList.size() == 0) {
				logger.error("업데이트 할 document가 없습니다.");
				throw new IllegalStateException("업데이트 할 document가 없습니다.");
			}

			if(splitQuery.size() != 2) {
				logger.error("Update 대상 문서를 찾을 Query가 잘못되었습니다. [{}]", query);
				throw new IllegalStateException("Update 대상 문서를 찾을 Query가 잘못되었습니다. ["+query+"]");
			}
			String field = splitQuery.get(0);
			String value = splitQuery.get(1);
			
			logger.info("field : {}, value : {}", field, value);
			
			Term updateTerm = new Term(field, value);
			
			crescentIndexer.updateDocuments(updateTerm, documentList, collection.getName());
			
			resultMessage = updateTerm.toString() + "에 대한 update가 완료되었습니다.";
			
		} else if (IndexingCommand.UPDATE_BY_FIELD_VALUE == indexingCommand) {
			
			List<Document> documentList = indexingRequestForm.getDocumentList().stream()
					.map(v -> LuceneDocumentBuilder.buildDocumentList(v, collection.getCrescentFieldByName()))
					.collect(Collectors.toList());
			
			if(documentList.size() == 0) {
				logger.error("업데이트 할 document가 없습니다.");
				throw new IllegalStateException("업데이트 할 document가 없습니다.");
			}

			if(splitQuery.size() != 2) {
				logger.error("Update 대상 문서를 찾을 Query가 잘못되었습니다. [{}]", query);
				throw new IllegalStateException("Delete 대상 문서를 찾을 Query가 잘못되었습니다. ["+query+"]");
			}
			
			String field = splitQuery.get(0);
			String value = splitQuery.get(1);
			
			logger.info("field : {}, value : {}", field, value);
			
			for(Document document : documentList) {
				value = document.get(field);
				
				if(value == null || value.length() == 0) {
					logger.error("Update 대상 문서를 찾을 field지이 잘못되었거나 field : [{}], value가 없습니다. value : [{}]", field, value);
					throw new IllegalStateException("pdate 대상 문서를 찾을 field지이 잘못되었거나 field : ["+field+"], value가 없습니다. value : ["+value+"]");
				}
				
				Term updateTerm = new Term(field, value);
				crescentIndexer.updateDocument(updateTerm, document, collection.getName());
			}
			
			resultMessage = query + "에 대한 update가 완료되었습니다.";
			
		} else if (IndexingCommand.DELETE == indexingCommand) {
			if(splitQuery.size() != 2) {
				logger.error("Delete 대상 문서를 찾을 Query가 잘못되었습니다. [{}]", query);
				throw new IllegalStateException("Delete 대상 문서를 찾을 Query가 잘못되었습니다. ["+query+"]");
			}
			String field = splitQuery.get(0);
			String value = splitQuery.get(1);
			
			logger.info("field : {}, value : {}", field, value);
			
			Term deleteTerm = new Term(field, value);
			Query deleteTermQuery = new TermQuery(deleteTerm);
			
			crescentIndexer.deleteDocument(deleteTermQuery, collection.getName());
			
			resultMessage = deleteTerm.toString() + "에 대한 delete가 완료되었습니다.";
		}
		
		if(IndexingType.BULK == indexingType) {
			crescentIndexer.commit(collection.getName());
		}
		
		return resultMessage;
	}
}

package com.tistory.devyongsik.crescent.index.indexer;

import com.tistory.devyongsik.crescent.collection.entity.Collection;
import com.tistory.devyongsik.crescent.index.LuceneDocumentBuilder;
import com.tistory.devyongsik.crescent.index.entity.IndexingCommand;
import com.tistory.devyongsik.crescent.index.entity.IndexingRequestForm;
import com.tistory.devyongsik.crescent.index.entity.IndexingType;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class CrescentIndexerExecutor {

	private final CrescentIndexer crescentIndexer;

	public CrescentIndexerExecutor(CrescentIndexer crescentIndexer) {
		this.crescentIndexer = crescentIndexer;
	}

	public String indexing(Collection collection, IndexingRequestForm indexingRequestForm) throws Exception {
		
		log.info("indexingRequestForm : {}", indexingRequestForm);
		
		IndexingType indexingType = IndexingType.valueOf(indexingRequestForm.getIndexingType().toUpperCase());
		IndexingCommand indexingCommand = IndexingCommand.valueOf(indexingRequestForm.getCommand().toUpperCase());
		String query = indexingRequestForm.getQuery();
		
		log.info("Indexing type : {} , Indexing command : {} ", indexingType, indexingCommand);
		log.info("Query : {}", query);
		
		String resultMessage = "Nothing To Execute...";
		
		if(IndexingCommand.ADD == indexingCommand) {
			List<Document> documentList = LuceneDocumentBuilder.buildDocumentList(indexingRequestForm.getDocumentList(), collection.getCrescentFieldByName());
			crescentIndexer.addDocument(documentList, collection.getName());
		
			resultMessage = documentList.size() + "건의 색인이 완료되었습니다.";
			
		} else if (IndexingCommand.UPDATE == indexingCommand) {
			
			List<Document> documentList = LuceneDocumentBuilder.buildDocumentList(indexingRequestForm.getDocumentList(), collection.getCrescentFieldByName());
			
			if(documentList.size() == 0) {
				log.error("업데이트 할 document가 없습니다.");
				throw new IllegalStateException("업데이트 할 document가 없습니다.");
			}
			
			String[] splitQuery = query.split(":");
			if(splitQuery.length != 2) {
				log.error("Update 대상 문서를 찾을 Query식이 잘못되었습니다. [{}]", query);
				throw new IllegalStateException("Update 대상 문서를 찾을 Query식이 잘못되었습니다. ["+query+"]");
			}
			String field = query.split(":")[0];
			String value = query.split(":")[1];
			
			log.info("field : {}, value : {}", field, value);
			
			Term updateTerm = new Term(field, value);
			
			crescentIndexer.updateDocuments(updateTerm, documentList, collection.getName());
			
			resultMessage = updateTerm.toString() + "에 대한 update가 완료되었습니다.";
			
		} else if (IndexingCommand.UPDATE_BY_FIELD_VALUE == indexingCommand) {
			
			List<Document> documentList = LuceneDocumentBuilder.buildDocumentList(indexingRequestForm.getDocumentList(), collection.getCrescentFieldByName());
			
			if(documentList.size() == 0) {
				log.error("업데이트 할 document가 없습니다.");
				throw new IllegalStateException("업데이트 할 document가 없습니다.");
			}
			
			String field = query.split(":")[0];
			String value = query.split(":")[1];
			
			log.info("field : {}, value : {}", field, value);
			
			for(Document document : documentList) {
				value = document.get(field);
				
				if(value == null || value.length() == 0) {
					log.error("Update 대상 문서를 찾을 field지이 잘못되었거나 field : [{}], value가 없습니다. value : [{}]", field, value);
					throw new IllegalStateException("pdate 대상 문서를 찾을 field지이 잘못되었거나 field : ["+field+"], value가 없습니다. value : ["+value+"]");
				}
				
				Term updateTerm = new Term(field, value);
				crescentIndexer.updateDocument(updateTerm, document, collection.getName());
			}
			
			resultMessage = query + "에 대한 update가 완료되었습니다.";
			
		} else if (IndexingCommand.DELETE == indexingCommand) {
			
			String[] splitQuery = query.split(":");
			if(splitQuery.length != 2) {
				log.error("Delete 대상 문서를 찾을 Query식이 잘못되었습니다. [{}]", query);
				throw new IllegalStateException("Delete 대상 문서를 찾을 Query식이 잘못되었습니다. ["+query+"]");
			}
			String field = query.split(":")[0];
			String value = query.split(":")[1];
			
			log.info("field : {}, value : {}", field, value);
			
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

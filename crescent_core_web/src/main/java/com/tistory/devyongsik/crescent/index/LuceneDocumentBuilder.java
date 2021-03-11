package com.tistory.devyongsik.crescent.index;

import com.tistory.devyongsik.crescent.collection.entity.CrescentCollectionField;
import lombok.extern.slf4j.Slf4j;
import net.htmlparser.jericho.Source;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class LuceneDocumentBuilder {

	public static List<Document> buildDocumentList(List<Map<String, String>> docList, 
												   Map<String, CrescentCollectionField> fieldsByName) {

		List<Document> documentList = new ArrayList<Document>();

		LuceneFieldBuilder luceneFieldBuilder = new LuceneFieldBuilder();

		for(Map<String, String> doc : docList) {
			//data filed에 있는 필드들..
			Set<String> fieldNamesFromDataFile = doc.keySet();
			
			Document document = new Document();
			
			for(String fieldName : fieldNamesFromDataFile) {
				String value = doc.get(fieldName);
				
				CrescentCollectionField crescentCollectionField = fieldsByName.get(fieldName);
				
				if(crescentCollectionField == null) {
					log.error("해당 collection에 존재하지 않는 필드입니다. [{}]", fieldName);
					throw new IllegalStateException("해당 collection에 존재하지 않는 필드입니다. ["+fieldName+"]");
				}
				
				if (crescentCollectionField.isRemoveHtmlTag()) {
					Source source = new Source(value);
					value = source.getTextExtractor().toString();
				}
				
				IndexableField indexableField = luceneFieldBuilder.create(fieldsByName.get(fieldName), value);
				document.add(indexableField);
				
				CrescentCollectionField crescentSortField = fieldsByName.get(fieldName+"_sort");
				if(crescentSortField != null) {
					IndexableField sortFieldAble = luceneFieldBuilder.create(fieldsByName.get(fieldName+"_sort"), value);
					document.add(sortFieldAble);
				}
			}
			
			documentList.add(document);
		}
		
		return documentList;
	}
}

package com.tistory.devyongsik.crescent.collection.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.tistory.devyongsik.crescent.index.LuceneDocumentBuilder;
import com.tistory.devyongsik.utils.CrescentTestCaseUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.lucene.document.Document;
import org.junit.jupiter.api.Test;

public class LuceneDocumentBuilderTest extends CrescentTestCaseUtil {

	@PostConstruct
	public void init() throws Exception {
		super.init();
	}

	@Test
	public void create() {
		List<Map<String, String>> docList = new ArrayList<Map<String, String>>();
		Map<String, String> doc = new HashMap<String, String>();
		
		doc.put("dscr", "상세 내용");
		doc.put("creuser", "작성자");
		doc.put("board_id", "1");
		doc.put("title", "제목");
		
		docList.add(doc);
		
		CrescentCollections crescentCollections = this.collectionHandler.getCrescentCollections();

		Map<String, CrescentCollection> collections = crescentCollections.getCrescentCollectionsMap();

		CrescentCollection sampleCollection = collections.get("sample");
		
		
		Map<String, CrescentCollectionField> fieldsByName = sampleCollection.getCrescentFieldByName();
		
		List<Document> luceneDocuments = LuceneDocumentBuilder.buildDocumentList(docList, fieldsByName);
		
		assertNotNull(luceneDocuments);
		
		for(Document luceneDocument :luceneDocuments){ 
			System.out.println(luceneDocument);
		}
	}
}

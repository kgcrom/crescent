package com.tistory.devyongsik.crescent.collection.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.tistory.devyongsik.crescent.index.LuceneFieldBuilder;
import com.tistory.devyongsik.utils.CrescentTestCaseUtil;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.apache.lucene.index.IndexableField;
import org.junit.jupiter.api.Test;

public class LuceneFieldBuilderTest extends CrescentTestCaseUtil {

	@PostConstruct
	public void init() throws Exception {
		super.init();
	}

	@Test
	public void create() {
		Collections crescentCollections = this.collectionHandler.getCrescentCollections();

		Map<String, Collection> collections = crescentCollections.getCrescentCollectionsMap();

		Collection sampleCollection = collections.get("sample");

		
		Map<String, CollectionField> fieldsByName = sampleCollection.getCrescentFieldByName();
		
		Set<String> fieldNames = fieldsByName.keySet();
		LuceneFieldBuilder luceneFieldBuilder = new LuceneFieldBuilder();
		
		for(String fieldName : fieldNames) {
			CollectionField field = fieldsByName.get(fieldName);
			IndexableField luceneField = luceneFieldBuilder.create(field, "30");
			
			System.out.println(luceneField);
			
			assertNotNull(luceneField);
		}
	}
}

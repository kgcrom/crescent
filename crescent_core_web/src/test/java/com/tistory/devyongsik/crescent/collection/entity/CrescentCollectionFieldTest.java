package com.tistory.devyongsik.crescent.collection.entity;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.tistory.devyongsik.utils.CrescentTestCaseUtil;
import org.junit.jupiter.api.Test;


public class CrescentCollectionFieldTest extends CrescentTestCaseUtil {

	@PostConstruct
	public void init() throws Exception {
		super.init();
	}

	@Test
	public void collectionFieldTest() {

		CrescentCollections crescentCollections = this.collectionHandler.getCrescentCollections();

		Map<String, CrescentCollection> collections = crescentCollections.getCrescentCollectionsMap();

		CrescentCollection sampleCollection = collections.get("sample");


		Map<String, CrescentCollectionField> fieldsByName = sampleCollection.getCrescentFieldByName();

		Set<String> fieldNames = fieldsByName.keySet();

		for(String fieldName : fieldNames) {
			CrescentCollectionField field = fieldsByName.get(fieldName);
			assertNotNull(field);
		}
	}
}

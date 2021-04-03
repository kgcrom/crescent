package com.tistory.devyongsik.crescent.collection.entity;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.tistory.devyongsik.utils.CrescentTestCaseUtil;
import org.junit.jupiter.api.Test;


public class CollectionFieldTest extends CrescentTestCaseUtil {

	@PostConstruct
	public void init() throws Exception {
		super.init();
	}

	@Test
	public void collectionFieldTest() {

		Collections crescentCollections = this.collectionHandler.getCrescentCollections();

		Map<String, Collection> collections = crescentCollections.getCrescentCollectionsMap();

		Collection sampleCollection = collections.get("sample");


		Map<String, CollectionField> fieldsByName = sampleCollection.getCrescentFieldByName();

		Set<String> fieldNames = fieldsByName.keySet();

		for(String fieldName : fieldNames) {
			CollectionField field = fieldsByName.get(fieldName);
			assertNotNull(field);
		}
	}
}

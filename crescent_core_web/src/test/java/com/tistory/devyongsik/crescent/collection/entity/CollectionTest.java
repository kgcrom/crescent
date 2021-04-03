package com.tistory.devyongsik.crescent.collection.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import javax.annotation.PostConstruct;


import com.tistory.devyongsik.utils.CrescentTestCaseUtil;
import org.junit.jupiter.api.Test;

public class CollectionTest extends CrescentTestCaseUtil {

	@PostConstruct
	public void init() throws Exception {
		super.init();
	}

	@Test
	public void collectionTest() {
		Collections crescentCollections = this.collectionHandler.getCrescentCollections();

		Map<String, Collection> collections = crescentCollections.getCrescentCollectionsMap();

		Collection sampleCollection = collections.get("sample");
		
		assertNotNull(sampleCollection);
		
		
	}
}

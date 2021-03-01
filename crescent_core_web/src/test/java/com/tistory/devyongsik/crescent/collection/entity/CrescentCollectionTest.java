package com.tistory.devyongsik.crescent.collection.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import javax.annotation.PostConstruct;


import com.tistory.devyongsik.crescent.config.CrescentCollectionHandler;
import com.tistory.devyongsik.crescent.config.SpringApplicationContext;
import com.tistory.devyongsik.utils.CrescentTestCaseUtil;
import org.junit.jupiter.api.Test;

public class CrescentCollectionTest extends CrescentTestCaseUtil {

	@PostConstruct
	public void init() {
		super.init();
	}

	@Test
	public void collectionTest() {
		CrescentCollectionHandler collectionHandler 
		= SpringApplicationContext.getBean("crescentCollectionHandler", CrescentCollectionHandler.class);
		
		CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();

		Map<String, CrescentCollection> collections = crescentCollections.getCrescentCollectionsMap();

		CrescentCollection sampleCollection = collections.get("sample");
		
		assertNotNull(sampleCollection);
		
		
	}
}

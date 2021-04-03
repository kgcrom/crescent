package com.tistory.devyongsik.utils;

import com.tistory.devyongsik.crescent.collection.entity.Collection;
import com.tistory.devyongsik.crescent.collection.entity.Collections;
import com.tistory.devyongsik.crescent.config.CrescentCollectionHandler;
import com.tistory.devyongsik.crescent.data.handler.Handler;
import com.tistory.devyongsik.crescent.data.handler.JsonDataHandler;
import com.tistory.devyongsik.crescent.index.entity.IndexingRequestForm;
import com.tistory.devyongsik.crescent.index.indexer.CrescentIndexerExecutor;
import com.tistory.devyongsik.crescent.search.searcher.CrescentDocSearcher;
import com.tistory.devyongsik.crescent.search.searcher.CrescentSearcherManager;
import com.tistory.devyongsik.crescent.search.service.SearchService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles(value = "test")
public class CrescentTestCaseUtil {

	@Autowired
	protected CrescentSearcherManager crescentSearcherManager;
	
	@Autowired
	protected CrescentCollectionHandler collectionHandler;
	
	@Autowired
	protected CrescentIndexerExecutor executor;
	
	@Autowired
	protected CrescentDocSearcher crescentDocSearcher;
	
	@Autowired
	protected SearchService searchService;
	
	private static String bulkIndexingTestText = "{\"command\":\"add\", \"indexingType\":\"bulk\",\"documentList\"" +
			":[{\"title\":\"제목 입니다0\",\"dscr\":\"텍스트 입니다0\",\"creuser\":\"creuser0\",\"board_id\":\"0\"}" +
			",{\"title\":\"제목 입니다1\",\"dscr\":\"텍스트 입니다1\",\"creuser\":\"creuser1\",\"board_id\":\"1\"}" +
			",{\"title\":\"제목 입니다2\",\"dscr\":\"텍스트 입니다2\",\"creuser\":\"creuser2\",\"board_id\":\"2\"}]}";

	public void init() throws Exception {
		initIndexFile();
	}
	
	private void initIndexFile() throws Exception {
		indexingTestData();
	}

	private void indexingTestData() throws Exception {
		
		Handler handler = new JsonDataHandler();
		IndexingRequestForm indexingRequestForm = handler.handledData(bulkIndexingTestText);
		Collections collections = collectionHandler.getCrescentCollections();
		Collection collection = collections.getCrescentCollection("sample");
		
		String message = executor.indexing(collection, indexingRequestForm);

		System.out.println("indexing result message : " + message);
	}
	
}

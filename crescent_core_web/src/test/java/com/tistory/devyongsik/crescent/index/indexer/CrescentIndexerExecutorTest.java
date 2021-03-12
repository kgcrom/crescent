package com.tistory.devyongsik.crescent.index.indexer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tistory.devyongsik.crescent.collection.entity.CrescentCollection;
import com.tistory.devyongsik.crescent.collection.entity.CrescentCollections;
import com.tistory.devyongsik.crescent.data.handler.Handler;
import com.tistory.devyongsik.crescent.data.handler.JsonDataHandler;
import com.tistory.devyongsik.crescent.index.entity.IndexingRequestForm;
import com.tistory.devyongsik.crescent.query.CrescentSearchRequestWrapper;
import com.tistory.devyongsik.crescent.search.entity.SearchRequest;
import com.tistory.devyongsik.crescent.search.entity.SearchResult;
import com.tistory.devyongsik.utils.CrescentTestCaseUtil;
import com.tistory.devyongsik.utils.FormattedTextBuilder;
import java.io.IOException;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.lucene.index.CorruptIndexException;
import org.junit.jupiter.api.Test;

public class CrescentIndexerExecutorTest extends CrescentTestCaseUtil {

  @PostConstruct
  public void init() throws Exception{
    super.init();
  }

  @Test
  public void addDocument() throws Exception {

    CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();
    Map<String, CrescentCollection> collections = crescentCollections.getCrescentCollectionsMap();

    CrescentCollection sampleCollection = collections.get("sample");

    Handler handler = new JsonDataHandler();
    IndexingRequestForm indexingRequestForm = handler
        .handledData(FormattedTextBuilder.getAddDocBulkJsonForm());

    String returnMessage = executor.indexing(sampleCollection, indexingRequestForm);

    assertEquals("1건의 색인이 완료되었습니다.", returnMessage);
  }

  @Test
  public void deleteDocument() throws Exception {

    CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();
    Map<String, CrescentCollection> collections = crescentCollections.getCrescentCollectionsMap();

    CrescentCollection sampleCollection = collections.get("sample");

    Handler handler = new JsonDataHandler();
    IndexingRequestForm indexingRequestForm = handler
        .handledData(FormattedTextBuilder.getDeleteDocBulkJsonForm());

    String returnMessage = executor.indexing(sampleCollection, indexingRequestForm);

    assertEquals("creuser:test에 대한 delete가 완료되었습니다.", returnMessage);
  }

  @Test
  public void updateDocument() throws Exception {

    CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();
    Map<String, CrescentCollection> collections = crescentCollections.getCrescentCollectionsMap();

    CrescentCollection sampleCollection = collections.get("sample");

    Handler handler = new JsonDataHandler();
    IndexingRequestForm indexingRequestForm = handler
        .handledData(FormattedTextBuilder.getUpdateDocBulkJsonForm());

    String returnMessage = executor.indexing(sampleCollection, indexingRequestForm);

    assertEquals("creuser:test에 대한 update가 완료되었습니다.", returnMessage);
  }

  @Test
  public void updateNewDocument() throws Exception {

    CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();
    Map<String, CrescentCollection> collections = crescentCollections.getCrescentCollectionsMap();

    CrescentCollection sampleCollection = collections.get("sample");

    Handler handler = new JsonDataHandler();
    IndexingRequestForm indexingRequestForm = handler
        .handledData(FormattedTextBuilder.getUpdateNewDocBulkJsonForm());

    String returnMessage = executor.indexing(sampleCollection, indexingRequestForm);

    assertEquals("creuser:testnew에 대한 update가 완료되었습니다.", returnMessage);

    SearchRequest searchRequest = new SearchRequest();
    searchRequest.setKeyword("testnew");
    searchRequest.setSearchField("creuser");
    searchRequest.setCollectionName("sample");

    CrescentSearchRequestWrapper csrw = new CrescentSearchRequestWrapper(searchRequest, this.collectionHandler);

    SearchResult searchResult = crescentDocSearcher.search(csrw);

    assertTrue(searchResult.getResultList().size() == 1);
  }

  @Test
  public void updateNewDocuments() throws Exception {

    CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();
    Map<String, CrescentCollection> collections = crescentCollections.getCrescentCollectionsMap();

    CrescentCollection sampleCollection = collections.get("sample");

    Handler handler = new JsonDataHandler();
    IndexingRequestForm indexingRequestForm = handler
        .handledData(FormattedTextBuilder.getUpdateNewDocListBulkJsonForm());

    String returnMessage = executor.indexing(sampleCollection, indexingRequestForm);

    assertEquals("creuser:testnew에 대한 update가 완료되었습니다.", returnMessage);

    SearchRequest searchRequest = new SearchRequest();
    searchRequest.setKeyword("testnew");
    searchRequest.setSearchField("creuser");
    searchRequest.setCollectionName("sample");

    CrescentSearchRequestWrapper csrw = new CrescentSearchRequestWrapper(searchRequest, this.collectionHandler);

    SearchResult searchResult = crescentDocSearcher.search(csrw);

    assertTrue(searchResult.getResultList().size() == 2);
  }

  @Test
  public void updateByFieldValueDocument() throws Exception {

    CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();
    Map<String, CrescentCollection> collections = crescentCollections.getCrescentCollectionsMap();

    CrescentCollection sampleCollection = collections.get("sample");

    Handler handler = new JsonDataHandler();
    IndexingRequestForm indexingRequestForm = handler
        .handledData(FormattedTextBuilder.getUpdateByFieldValueDocBulkJsonForm());

    String returnMessage = executor.indexing(sampleCollection, indexingRequestForm);

    assertEquals("creuser:*에 대한 update가 완료되었습니다.", returnMessage);

    SearchRequest searchRequest = new SearchRequest();
    searchRequest.setKeyword("test");
    searchRequest.setSearchField("creuser");
    searchRequest.setCollectionName("sample");

    CrescentSearchRequestWrapper csrw = new CrescentSearchRequestWrapper(searchRequest, this.collectionHandler);

    SearchResult searchResult = crescentDocSearcher.search(csrw);

    assertTrue(searchResult.getResultList().size() == 1);
    assertEquals("제목 입니다0 업데이트...", searchResult.getResultList().get(0).get("title"));
  }

  @Test
  public void updateByFieldValueNewDocumentList() throws Exception {

    CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();
    Map<String, CrescentCollection> collections = crescentCollections.getCrescentCollectionsMap();

    CrescentCollection sampleCollection = collections.get("sample");

    Handler handler = new JsonDataHandler();
    IndexingRequestForm indexingRequestForm = handler
        .handledData(FormattedTextBuilder.getUpdateByFieldValueNewDocListBulkJsonForm());

    String returnMessage = executor.indexing(sampleCollection, indexingRequestForm);

    assertEquals("creuser:*에 대한 update가 완료되었습니다.", returnMessage);

    SearchRequest searchRequest = new SearchRequest();
    searchRequest.setKeyword("testnew");
    searchRequest.setSearchField("creuser");
    searchRequest.setCollectionName("sample");

    CrescentSearchRequestWrapper csrw = new CrescentSearchRequestWrapper(searchRequest, this.collectionHandler);

    SearchResult searchResult = crescentDocSearcher.search(csrw);

    assertTrue(searchResult.getResultList().size() == 1);
    assertEquals("제목 입니다1 업데이트...", searchResult.getResultList().get(0).get("title"));
  }
}

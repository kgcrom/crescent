package org.crescent.search.searcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.crescent.search.searcher.CrescentSearcherManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.crescent.IndexWriterManager;
import org.crescent.collection.entity.Collection;
import org.crescent.config.CollectionHandler;
import org.crescent.index.LuceneDocumentBuilder;
import org.crescent.index.indexer.CrescentIndexer;
import org.crescent.search.entity.SearchRequest;
import org.crescent.search.entity.SearchResult;
import org.crescent.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@ActiveProfiles(value = "test")
@SpringBootTest(classes = {
    CrescentIndexer.class,
    SearchService.class,
    CollectionHandler.class,
    CrescentSearcherManager.class,
    IndexWriterManager.class
})
class SearchServiceTest {

  @Autowired
  private SearchService searchService;

  @Autowired
  private CrescentIndexer crescentIndexer;

  @Autowired
  private CollectionHandler collectionHandler;

  @BeforeEach
  void setUp() {
    Collection sampleCollection = collectionHandler.getCollection("sample");
    // TODO string type만 색인된다, integer, long 형 색인되도록 수정
    List<Document> documents = generateSampleDocument(5).stream()
        .map(v -> LuceneDocumentBuilder
            .buildDocumentList(v, sampleCollection.getCrescentFieldByName()))
        .collect(Collectors.toList());
    try {
      crescentIndexer.addDocument(documents, "sample");
    } catch (IOException e) {
      fail("expected collection info is correct");
    }
  }

  @AfterEach
  void tearDown() throws IOException {
    crescentIndexer.deleteDocument(new MatchAllDocsQuery(), "sample");
  }

  @Test
  void test_SearchMatchAllQuery() throws IOException {
    SearchRequest request = SearchRequest.builder()
        .collectionName("sample")
        .query("*:*")
        .build();
    SearchResult result = searchService.search(request);

    assertEquals(5, result.getTotalHists());
    List<Map<String, Object>> docs = result.getDocs();
    assertTrue(docs.get(0).containsKey("board_id"));
    assertFalse(docs.get(0).containsKey("comment_count"));
  }

  @Test
  void test_SearchSimpleQuery() throws IOException {
    SearchRequest request = SearchRequest.builder()
        .collectionName("sample")
        .query("title:sample title 3")
        .build();
    SearchResult result = searchService.search(request);

    assertEquals(1, result.getTotalHists());
  }

  @Test
  void test_SearchSimpleFilterQuery() throws IOException {
    SearchRequest request = SearchRequest.builder()
        .collectionName("sample")
        .filterQuery("comment_count:3")
        .build();
    SearchResult result = searchService.search(request);

    assertEquals(1, result.getTotalHists());
  }

  // test 용 색인하는 class 만들어서 활용하기
  private List<Map<String, String>> generateSampleDocument(int size) {
    List<Map<String, String>> documents = new ArrayList<>(size);

    for (int i = 0; i < size; i++) {
      Map<String, String> doc = new HashMap<>();
      doc.put("board_id", String.valueOf(i));
      doc.put("comment_count", String.valueOf(i));
      doc.put("title", "sample title " + (i + 1));
      doc.put("dscr", "sample description " + (i + 1));
      doc.put("creuser", "kgcrom");
      documents.add(doc);
    }
    return documents;
  }
}

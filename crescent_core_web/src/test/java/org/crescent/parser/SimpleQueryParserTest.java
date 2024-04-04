package org.crescent.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.MatchNoDocsQuery;
import org.apache.lucene.search.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.crescent.collection.entity.Collection;
import org.crescent.config.CollectionHandler;
import org.crescent.search.entity.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(value = "local")
@SpringBootTest(classes = {
    CollectionHandler.class,
})
class SimpleQueryParserTest {

  @Autowired
  private CollectionHandler collectionHandler;

  private SimpleQueryParser simpleQueryParser;
  private Collection sampleCollection;

  @BeforeEach
  void setUp() {
    this.simpleQueryParser = new SimpleQueryParser();
    this.sampleCollection = this.collectionHandler.getCollection("sample");
  }

  @Test
  void test_MatchNoQuery() {
    SearchRequest request = SearchRequest.builder()
        .collectionName("sample")
        .build();
    Query query = simpleQueryParser.parse(this.sampleCollection, request, sampleCollection.getDefaultSearchFields());

    assertTrue(query instanceof MatchNoDocsQuery);
  }

  @Test
  void test_MatchAllQuery() {
    SearchRequest request = SearchRequest.builder()
        .collectionName("sample")
        .query("*:*")
        .build();
    Query query = simpleQueryParser.parse(this.sampleCollection, request, sampleCollection.getDefaultSearchFields());

    assertTrue(query instanceof MatchAllDocsQuery);
  }

  @Test
  void test_SimpleQuery() {
    SearchRequest request = SearchRequest.builder()
        .query("sample")
        .collectionName("sample")
        .build();
    Query query = simpleQueryParser.parse(this.sampleCollection, request, sampleCollection.getDefaultSearchFields());

    // TODO EnglishAnalyzer 적용하니 sampl이 되는거 수정
    assertEquals("title:sampl dscr:sampl", query.toString());
  }

  @Test
  void test_SimpleFilterQuery1() {
    SearchRequest request = SearchRequest.builder()
        .filterQuery("sample")
        .collectionName("sample")
        .build();
    Query query = simpleQueryParser.parse(this.sampleCollection, request, sampleCollection.getDefaultSearchFields());

    assertTrue(query instanceof MatchNoDocsQuery);
  }

  @Test
  void test_SimpleFilterQuery2() {
    SearchRequest request = SearchRequest.builder()
        .filterQuery("comment_count:3")
        .collectionName("sample")
        .build();
    Query query = simpleQueryParser.parse(this.sampleCollection, request, sampleCollection.getDefaultSearchFields());

    assertEquals("+comment_count:[3 TO 3]", query.toString());
  }

  @Test
  void test_SimpleFilterQuery3() {
    SearchRequest request = SearchRequest.builder()
        .filterQuery("board_id:3")
        .collectionName("sample")
        .build();
    Query query = simpleQueryParser.parse(this.sampleCollection, request, sampleCollection.getDefaultSearchFields());

    assertEquals("+board_id:[3 TO 3]", query.toString());
  }

  @Test
  void test_SimpleFilterQuery4() {
    SearchRequest request = SearchRequest.builder()
        .filterQuery("title:sample3")
        .collectionName("sample")
        .build();
    Query query = simpleQueryParser.parse(this.sampleCollection, request, sampleCollection.getDefaultSearchFields());

    assertEquals("+title:sample3", query.toString());
  }

  @Test
  void test_Simple_QueryAndFilterQuery() {
    SearchRequest request = SearchRequest.builder()
        .query("title:sample1")
        .filterQuery("board_id:3")
        .collectionName("sample")
        .build();
    Query query = simpleQueryParser.parse(this.sampleCollection, request, sampleCollection.getDefaultSearchFields());

    assertEquals("title:sample1 +board_id:[3 TO 3]", query.toString());
  }

}

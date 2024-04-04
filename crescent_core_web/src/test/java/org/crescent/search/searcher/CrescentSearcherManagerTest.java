package org.crescent.search.searcher;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.crescent.search.searcher.CrescentSearcherManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.crescent.IndexWriterManager;
import org.crescent.collection.entity.Collection;
import org.crescent.config.CollectionHandler;
import org.crescent.index.LuceneDocumentBuilder;
import org.crescent.index.indexer.CrescentIndexer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(value = "test")
@SpringBootTest(classes = {
    CollectionHandler.class,
    IndexWriterManager.class,
    CrescentIndexer.class,
    CrescentSearcherManager.class,
})
class CrescentSearcherManagerTest {

  @Autowired
  private CrescentSearcherManager crescentSearcherManager;

  @Autowired
  private CrescentIndexer crescentIndexer;

  @Autowired
  private CollectionHandler collectionHandler;

  @Test
  public void testGetSearchManager() {
    SearcherManager sampleSearchManager = crescentSearcherManager.getSearcherManager("sample");
    SearcherManager sampleWikiSearchManager = crescentSearcherManager.getSearcherManager("sample_wiki");

    try {
      assertTrue(sampleSearchManager.isSearcherCurrent());
      assertTrue(sampleWikiSearchManager.isSearcherCurrent());
    } catch (IOException e) {
      fail("search manager has changed");
    }
  }

  @Test
  public void testIndexSampleCollection() throws IOException{
    Collection sampleCollection = collectionHandler.getCollection("sample");
    List<Document> documents = generateSampleDocument(5).stream()
        .map(v -> LuceneDocumentBuilder.buildDocumentList(v, sampleCollection.getCrescentFieldByName()))
        .collect(Collectors.toList());

    try {
      crescentIndexer.addDocument(documents, "sample");
    } catch (IOException e) {
      fail("expected collection info is correct");
    }

    IndexSearcher indexSearcher = null;
    SearcherManager searcherManager = crescentSearcherManager.getSearcherManager("sample");
    try {
      indexSearcher = searcherManager.acquire();
      Term term = new Term("creuser", "kgcrom");
      TermQuery query = new TermQuery(term);
      TopDocs docs = indexSearcher.search(query, 5);
      assertEquals(5, docs.totalHits.value);

      Document doc = indexSearcher.doc(docs.scoreDocs[0].doc);
      assertEquals("sample title 1", doc.get("title"));
    } catch (IOException e) {
      fail("expected to acquire searcher");
    } finally {
      searcherManager.release(indexSearcher);
    }
  }

  private List<Map<String, String>> generateSampleDocument(int size) {
    List<Map<String, String>> documents = new ArrayList<>(size);

    for (int i = 0; i < size; i++) {
      Map<String, String> doc = new HashMap<>();
      doc.put("board_id", String.valueOf(i));
      doc.put("comment_count", "3");
      doc.put("title", "sample title " + (i + 1));
      doc.put("dscr", "sample description " + (i + 1));
      doc.put("creuser", "kgcrom");
      documents.add(doc);
    }
    return documents;
  }

}

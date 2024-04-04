package org.crescent.admin.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.lucene.document.Document;
import org.crescent.collection.entity.Collection;
import org.crescent.config.CollectionHandler;
import org.crescent.index.LuceneDocumentBuilder;
import org.crescent.index.indexer.CrescentIndexer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class InternalController {

  private CrescentIndexer indexer;
  private CollectionHandler collectionHandler;

  public InternalController(CrescentIndexer indexer,
      CollectionHandler collectionHandler) {
    this.indexer = indexer;
    this.collectionHandler = collectionHandler;
  }

  @RequestMapping("/v1/internal/index_mock")
  public void indexMockSampleCollection() throws IOException {
    Collection sampleCollection = collectionHandler.getCollection("sample");
    List<Document> documents = generateSampleDocument(10).stream()
        .map(v -> LuceneDocumentBuilder
            .buildDocumentList(v, sampleCollection.getCrescentFieldByName()))
        .collect(Collectors.toList());

    indexer.addDocument(documents, "sample");
  }

  // TODO internal API와 test에서도 사용할수 있도록 만들기
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

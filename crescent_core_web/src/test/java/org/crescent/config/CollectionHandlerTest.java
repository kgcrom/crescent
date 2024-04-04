package org.crescent.config;

import org.crescent.config.CollectionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.crescent.index.analysis.AnalyzerHolder;
import org.crescent.collection.entity.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(value = "test")
@SpringBootTest(classes = {
    CollectionHandler.class
})
public class CollectionHandlerTest {

  @Autowired
  private CollectionHandler handler;

  @BeforeEach
  public void setUp() {
    handler.loadCollection();
  }

  @Test
  public void testCollectionLoad() {
    List<Collection> collections = handler.getCollections();
    Map<String, Collection> collectionMap = handler.getCollectionMap();

    assertEquals(2, collections.size());

    assertTrue(collectionMap.containsKey("sample"));
    assertTrue(collectionMap.containsKey("sample_wiki"));
  }

  @Test
  public void testCollectionAnalyzer() {
    Map<String, Collection> collectionMap = handler.getCollectionMap();
    Collection sampleCollection = collectionMap.get("sample");

    List<AnalyzerHolder> analyzerHolders = sampleCollection.getAnalyzers();
    for (AnalyzerHolder analyzerHolder : analyzerHolders) {
      if ("indexing".equals(analyzerHolder.getType())) {
        assertEquals(analyzerHolder.getClassName(), sampleCollection.getIndexingModeAnalyzer().getClass().getName());
      } else {
        assertEquals(analyzerHolder.getClassName(), sampleCollection.getSearchModeAnalyzer().getClass().getName());
      }
    }
  }

  // TODO test collection field

}

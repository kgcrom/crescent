package com.tistory.devyongsik.crescent.config;

import com.tistory.devyongsik.crescent.collection.entity.AnalyzerHolder;
import com.tistory.devyongsik.crescent.collection.entity.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(value = "test")
@SpringBootTest
public class CollectionHandlerTest {

  @Autowired
  private CrescentCollectionHandler handler;

  @Test
  public void testCollectionLoad() {
    handler.loadCollection();
    Collections collections = handler.getCrescentCollections();

    assertNotNull(collections);
    assertEquals(2, collections.getCrescentCollections().size());
    assertEquals("sample", collections.getCrescentCollection("sample").getName());

    List<AnalyzerHolder> analyzers = collections.getCrescentCollections().get(0)
        .getAnalyzers();
    assertEquals(2, analyzers.size());
    assertEquals("org.apache.lucene.analysis.en.EnglishAnalyzer", analyzers.get(0).getClassName());
  }

}

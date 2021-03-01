package com.tistory.devyongsik.crescent.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.tistory.devyongsik.crescent.collection.entity.CrescentAnalyzerHolder;
import com.tistory.devyongsik.crescent.collection.entity.CrescentCollections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(value = "test")
public class CrescentCollectionHandlerTest {

  @Autowired
  private CrescentCollectionHandler handler;

  @Test
  public void test_XML로_Collection정보로드() {
    CrescentCollections collections = handler.getCrescentCollections();

    assertNotNull(collections);
    assertEquals(1, collections.getCrescentCollections().size());

    List<CrescentAnalyzerHolder> analyzers = collections.getCrescentCollections().get(0)
        .getAnalyzers();
    assertEquals(2, analyzers.size());
    assertEquals("org.apache.lucene.analysis.en.EnglishAnalyzer", analyzers.get(0).getClassName());
  }

}

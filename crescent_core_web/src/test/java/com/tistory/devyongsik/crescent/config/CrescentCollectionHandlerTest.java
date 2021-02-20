package com.tistory.devyongsik.crescent.config;

import com.tistory.devyongsik.crescent.collection.entity.CrescentAnalyzerHolder;
import com.tistory.devyongsik.crescent.collection.entity.CrescentCollections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(value = "test")
@ContextConfiguration(locations = {
    "classpath:spring/applicationContext.xml",
    "classpath:spring/action-config.xml"})
public class CrescentCollectionHandlerTest {

  @Autowired
  private CrescentCollectionHandler handler;

  @Test
  public void test_XML로_Collection정보로드() {
    CrescentCollections collections = handler.getCrescentCollections();

    Assert.assertNotNull(collections);
    Assert.assertEquals(1, collections.getCrescentCollections().size());

    List<CrescentAnalyzerHolder> analyzers = collections.getCrescentCollections().get(0).getAnalyzers();
    Assert.assertEquals(2, analyzers.size());
    Assert.assertEquals("org.apache.lucene.analysis.en.EnglishAnalyzer", analyzers.get(0).getClassName());
  }

}

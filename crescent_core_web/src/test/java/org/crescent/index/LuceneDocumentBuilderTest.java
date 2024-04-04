package org.crescent.index;

import org.apache.lucene.document.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.crescent.collection.entity.CollectionField;
import org.crescent.config.CollectionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(value = "test")
@SpringBootTest(classes = {
    CollectionHandler.class
})
class LuceneDocumentBuilderTest {

  @Autowired
  private CollectionHandler collectionHandler;

  // TODO collection handler가 아니라 Test 전용 Collection 생성 코드 사용하도록 수정
  @Test
  void testExistFieldDocument() {
    Map<String, CollectionField> fieldsByName = collectionHandler.getCollection("sample").getCrescentFieldByName();
    Map<String, String> doc = new HashMap<>();
    doc.put("board_id", "1");
    doc.put("comment_count", "5");
    doc.put("title", "sample title1");
    doc.put("dscr", "sample description 1");
    doc.put("creuser", "kgcrom");

    Document document = LuceneDocumentBuilder.buildDocumentList(doc, fieldsByName);
    assertEquals(6, document.getFields().size());
  }

  @Test
  void testNotExistFieldDocument() {
    Map<String, CollectionField> fieldsByName = collectionHandler.getCollection("sample").getCrescentFieldByName();
    Map<String, String> doc = new HashMap<>();
    doc.put("board_id", "1");
    doc.put("title_not_exist", "sample title1");
    doc.put("dscr", "sample description 1");
    doc.put("creuser", "kgcrom");

    assertThrows(IllegalStateException.class, () -> LuceneDocumentBuilder.buildDocumentList(doc, fieldsByName));
  }

  @Test
  void testParseHtmlField() {
    Map<String, CollectionField> fieldsByName = collectionHandler.getCollection("sample").getCrescentFieldByName();
    Map<String, String> doc = new HashMap<>();
    fieldsByName.get("dscr").setRemoveHtmlTag(true);

    doc.put("board_id", "1");
    doc.put("comment_count", "5");
    doc.put("title", "sample title1");
    doc.put("dscr", "<p>sample <br/>description 1</p>");
    doc.put("creuser", "kgcrom");

    Document document = LuceneDocumentBuilder.buildDocumentList(doc, fieldsByName);
    assertEquals(6, document.getFields().size());
    assertEquals("sample description 1", document.get("dscr"));
  }
}

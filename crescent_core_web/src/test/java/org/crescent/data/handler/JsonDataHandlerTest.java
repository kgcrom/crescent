package org.crescent.data.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.crescent.index.entity.IndexingRequestForm;
import org.crescent.util.FormattedTextBuilder;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class JsonDataHandlerTest {

  @Test
  public void indexingAddDocumentBulk() {
    String inputText = FormattedTextBuilder.getAddDocBulkJsonForm();

    ObjectMapper mapper = new ObjectMapper();
    IndexingRequestForm indexingRequest = null;
    try {
      indexingRequest = mapper.readValue(inputText, IndexingRequestForm.class);
    } catch (IOException e) {
      fail(e.getMessage());
    }

    assertEquals("add", indexingRequest.getCommand());
    assertEquals(null, indexingRequest.getQuery());
    assertEquals("bulk", indexingRequest.getIndexingType());
    assertEquals("[{board_id=0, title=제목 입니다0, dscr=본문 입니다.0, creuser=test}]",
        indexingRequest.getDocumentList().toString());
  }

  @Test
  public void indexingAddDocumentIncrement() {
    String inputText = FormattedTextBuilder.getAddDocIncJsonForm();

    ObjectMapper mapper = new ObjectMapper();
    IndexingRequestForm indexingRequest = null;
    try {
      indexingRequest = mapper.readValue(inputText, IndexingRequestForm.class);
    } catch (IOException e) {
      fail(e.getMessage());
    }

    assertEquals("add", indexingRequest.getCommand());
    assertEquals(null, indexingRequest.getQuery());
    assertEquals("incremental", indexingRequest.getIndexingType());
    assertEquals("[{board_id=0, title=제목 입니다0, dscr=본문 입니다.0, creuser=test}]",
        indexingRequest.getDocumentList().toString());
  }

  @Test
  public void indexingUpdateDocumentBulk() {
    String inputText = FormattedTextBuilder.getUpdateDocBulkJsonForm();

    ObjectMapper mapper = new ObjectMapper();
    IndexingRequestForm indexingRequest = null;
    try {
      indexingRequest = mapper.readValue(inputText, IndexingRequestForm.class);
    } catch (IOException e) {
      fail(e.getMessage());
    }

    assertEquals("update", indexingRequest.getCommand());
    assertEquals("creuser:test", indexingRequest.getQuery());
    assertEquals("bulk", indexingRequest.getIndexingType());
    assertEquals("[{board_id=0, title=제목 입니다0 업데이트..., dscr=본문 입니다.0, creuser=test}]",
        indexingRequest.getDocumentList().toString());
  }

  @Test
  public void indexingUpdateDocumentInc() {
    String inputText = FormattedTextBuilder.getUpdateDocIncJsonForm();

    ObjectMapper mapper = new ObjectMapper();
    IndexingRequestForm indexingRequest = null;
    try {
      indexingRequest = mapper.readValue(inputText, IndexingRequestForm.class);
    } catch (IOException e) {
      fail(e.getMessage());
    }

    assertEquals("update", indexingRequest.getCommand());
    assertEquals("creuser:test", indexingRequest.getQuery());
    assertEquals("incremental", indexingRequest.getIndexingType());
    assertEquals("[{board_id=0, title=제목 입니다0 업데이트..., dscr=본문 입니다.0, creuser=test}]",
        indexingRequest.getDocumentList().toString());
  }

  @Test
  public void indexingDeleteDocumentBulk() {
    String inputText = FormattedTextBuilder.getDeleteDocBulkJsonForm();

    ObjectMapper mapper = new ObjectMapper();
    IndexingRequestForm indexingRequest = null;
    try {
      indexingRequest = mapper.readValue(inputText, IndexingRequestForm.class);
    } catch (IOException e) {
      fail(e.getMessage());
    }

    assertEquals("delete", indexingRequest.getCommand());
    assertEquals("creuser:test", indexingRequest.getQuery());
    assertEquals("bulk", indexingRequest.getIndexingType());
    assertEquals(null, indexingRequest.getDocumentList());
  }

  @Test
  public void indexingDeleteDocumentInc() {
    String inputText = FormattedTextBuilder.getDeleteDocIncJsonForm();

    ObjectMapper mapper = new ObjectMapper();
    IndexingRequestForm indexingRequest = null;
    try {
      indexingRequest = mapper.readValue(inputText, IndexingRequestForm.class);
    } catch (IOException e) {
      fail(e.getMessage());
    }

    assertEquals("delete", indexingRequest.getCommand());
    assertEquals("creuser:test", indexingRequest.getQuery());
    assertEquals("incremental", indexingRequest.getIndexingType());
    assertEquals(null, indexingRequest.getDocumentList());
  }
}

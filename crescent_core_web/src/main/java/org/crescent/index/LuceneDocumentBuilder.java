package org.crescent.index;

import net.htmlparser.jericho.Source;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;
import org.crescent.collection.entity.CollectionField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LuceneDocumentBuilder {

  private static final Logger logger = LoggerFactory.getLogger(LuceneDocumentBuilder.class);

  public static Document buildDocumentList(
      Map<String, String> doc, Map<String, CollectionField> fieldsByName) {
    Set<String> fieldNamesFromDataFile = doc.keySet();
    Document document = new Document();

    for (String fieldName : fieldNamesFromDataFile) {
      String value = doc.get(fieldName);
      CollectionField collectionField = fieldsByName.get(fieldName);

      if (collectionField == null) {
        logger.error("doesn't exist {} field in this collection", fieldName);
        throw new IllegalStateException("해당 collection에 존재하지 않는 필드입니다. [" + fieldName + "]");
      }

      if (collectionField.isRemoveHtmlTag()) {
        Source source = new Source(value);
        value = source.getTextExtractor().toString();
      }

      List<IndexableField> fields = createField(fieldsByName.get(fieldName), value);
      for (IndexableField field : fields) {
        document.add(field);
      }
    }

    return document;
  }

  private static List<IndexableField> createField(CollectionField collectionField, String value) {
    // add docvalue
    List<IndexableField> fields = new ArrayList<>(2);
    if ("string".equals(collectionField.getType())) {
      fields.add(new StringField(collectionField.getName(), value, Field.Store.YES));
    } else if ("text".equals(collectionField.getType())) {
      fields.add(new TextField(collectionField.getName(), value, Field.Store.YES));
    } else if ("long".equals(collectionField.getType())) {
      if (collectionField.isIndexed()) {
        fields.add(new LongPoint(collectionField.getName(), Long.parseLong(value)));
      }
      if (collectionField.isStored()) {
        fields.add(new StoredField(collectionField.getName(), Long.parseLong(value)));
      }
    } else if ("integer".equals(collectionField.getType())) {
      if (collectionField.isIndexed()) {
        fields.add(new IntPoint(collectionField.getName(), Integer.parseInt(value)));
      }
      if (collectionField.isStored()) {
        fields.add(new StoredField(collectionField.getName(), Integer.parseInt(value)));
      }
    }

    return fields;
  }
}

package com.tistory.devyongsik.crescent.index;

import com.tistory.devyongsik.crescent.collection.entity.CollectionField;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.FieldType.NumericType;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.IndexableField;

@Slf4j
public class LuceneFieldBuilder {

	public IndexableField create(CollectionField collectionField, String value) {

		FieldType fieldType = new FieldType();
		fieldType.setIndexed(collectionField.isIndex());
		fieldType.setStored(collectionField.isStore());
		fieldType.setTokenized(collectionField.isAnalyze());
		fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
		fieldType.setStoreTermVectors(collectionField.isTermvector());

		if("STRING".equalsIgnoreCase(collectionField.getType())) {
			Field f = new Field(collectionField.getName(),
					StringUtils.defaultString(value, ""),
					fieldType);

			f.setBoost(collectionField.getBoost());

			log.debug("Field : {}", f);


			return f;

		} else if("LONG".equalsIgnoreCase(collectionField.getType())) {
			fieldType.setNumericType(NumericType.LONG);

			Field f = new LongField(collectionField.getName(),
					Long.parseLong(value),
					fieldType);

			log.debug("Field : {}", f);

			return f;

		} else if ("INTEGER".equalsIgnoreCase(collectionField.getType())){
			fieldType.setNumericType(NumericType.INT);

			Field f = new IntField(collectionField.getName(),
					Integer.parseInt(value),
					fieldType);

			log.debug("Field : {}", f);

			return f;
		}else {
			return null;
		}
	}
}

package com.tistory.devyongsik.crescent.config;

import com.thoughtworks.xstream.XStream;
import com.tistory.devyongsik.crescent.collection.entity.Collection;
import com.tistory.devyongsik.crescent.collection.entity.AnalyzerHolder;
import com.tistory.devyongsik.crescent.collection.entity.CollectionField;
import com.tistory.devyongsik.crescent.collection.entity.Collections;
import com.tistory.devyongsik.crescent.collection.entity.SortField;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CrescentCollectionHandler {

	private String collectionPath;
	private Collections collections;

	public CrescentCollectionHandler(@Value("${crescent.collection-path}") String collectionPath) {
		this.collectionPath = collectionPath;
		loadCollection();
	}

	public void loadCollection() {
		loadFromXML();
		makeAnalyzer();
		makeFieldsMap();
		makeAddtionalFields();
	}

	private void loadFromXML() {
		XStream stream = new XStream();
		stream.processAnnotations(Collections.class);
		stream.alias("collections", Collections.class);
		stream.addImplicitCollection(Collections.class, "collections");

		ResourceLoader resourceLoader = new ResourceLoader(collectionPath);
		InputStream inputStream = resourceLoader.getInputStream();

		collections = (Collections) stream.fromXML(inputStream);

		if (collections == null) {
			String errorMsg = "Crescent Collections is not loaded from xml : [" + collectionPath + "]";
			log.error(errorMsg);

			throw new IllegalStateException(errorMsg);
		}

		// indexingDirectory가 절대경로가 아닌경우 임의로 경로 수정,  maven local profile에서 사용
		List<Collection> list = collections.getCrescentCollections();
		for (Collection collection : list) {
			String path = collection.getIndexingDirectory();
			File file = new File(path);
			if (!file.isAbsolute()) {
				String webRoot = System.getProperty("webapp.root");
				if (webRoot != null)
					collection.setIndexingDirectory(webRoot + path);
			}
		}

		try {
			inputStream.close();
		} catch (Exception e) {
			log.error("stream close error ; ", e);
		}
	}

	private void makeAnalyzer() {
		for (Collection collection : collections.getCrescentCollections()) {
			List<AnalyzerHolder> analyzerHolders = collection.getAnalyzers();

			for (AnalyzerHolder analyzerHolder : analyzerHolders) {
				String type = analyzerHolder.getType();
				String className = analyzerHolder.getClassName();
				String constructorArgs = analyzerHolder.getConstructorArgs();

				Analyzer analyzer = null;

				try {
					@SuppressWarnings("unchecked")
					Class<Analyzer> analyzerClass = (Class<Analyzer>) Class.forName(className);

					if (constructorArgs == null || constructorArgs.trim().length() == 0) {
						analyzer = analyzerClass.newInstance();
					} else {
						// TODO version 하드코딩이 아니라 다양한 analyzer에 맞도록 로직 수정
						Version version = Version.parseLeniently(constructorArgs);
						Class<?>[] intArgsClass = new Class<?>[]{Version.class};
						Object[] initArgs = new Object[]{version};

						Constructor<Analyzer> intArgsConstructor = analyzerClass.getConstructor(intArgsClass);
						analyzer = intArgsConstructor.newInstance(initArgs);
					}

					if("indexing".equals(type)) {
						collection.setIndexingModeAnalyzer(analyzer);
					} else if("search".equals(type)) {
						collection.setSearchModeAnalyzer(analyzer);
					} else {
						throw new IllegalStateException("정의되지 않은 Analyzer type 입니다. ["+type+"]");
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Collections getCrescentCollections() {
		return this.collections;
	}
	
	public void writeToXML() {

		ResourceLoader resourceLoader = new ResourceLoader(collectionPath);
		URL collectionXmlUrl = resourceLoader.getURL();

		XStream stream = new XStream();
		stream.processAnnotations(Collections.class);

		log.debug("collectionXmlUrl : {}", collectionXmlUrl);

		try {

			File collectionsXmlFile = new File(collectionXmlUrl.toURI());

			log.debug("collectionXmlUrl to URI: {}", collectionXmlUrl.toURI());
			log.debug("collectionsXmlFile : {}", collectionsXmlFile);

			FileOutputStream fos = new FileOutputStream(collectionsXmlFile, false);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, Charset.forName("utf-8")));

			stream.toXML(this.collections, bw);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			log.error("error : ", e);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.error("error : ", e);
		} catch (IOException e) {
			e.printStackTrace();
			log.error("error : ", e);
		}
	}

	private void makeFieldsMap() {
		List<Collection> collections = this.collections.getCrescentCollections();
		
		if(collections.size() == 0) {
			String errorMsg = "There are no Crescent collections!!";
			log.error(errorMsg);
			
			throw new IllegalStateException(errorMsg);
		}
		
		for(Collection collection : collections) {
			Map<String, CollectionField> fieldMap = collection.getCrescentFieldByName();
			if(fieldMap == null) {
				fieldMap = new HashMap<>();
			}
			
			List<CollectionField> fieldList = collection.getFields();
			for(CollectionField field : fieldList) {
				fieldMap.put(field.getName(), field);
			}
			
			collection.setCrescentFieldByName(fieldMap);
		}
	}
	
	private void makeAddtionalFields() {
		List<Collection> collections = this.collections.getCrescentCollections();
		
		for(Collection collection : collections) {
			List<SortField> sortFieldList = collection.getSortFields();
			Map<String, CollectionField> fieldMap = collection.getCrescentFieldByName();
			
			for(SortField sortField : sortFieldList) {
				CollectionField field = fieldMap.get(sortField.getSource());
				
				if(field == null) {
					throw new IllegalStateException("정렬 필드 설정에 필요한 원본(source) 필드가 없습니다.");
				}
				
				try {
					CollectionField newSortField = (CollectionField)field.clone();
					
					newSortField.setAnalyze(false);
					newSortField.setIndex(true);
					newSortField.setName(sortField.getDest());
					
					fieldMap.put(sortField.getDest(), newSortField);
					
				} catch (CloneNotSupportedException e1) {
					log.error("error : ", e1);
				}
			}
		}
	}
}

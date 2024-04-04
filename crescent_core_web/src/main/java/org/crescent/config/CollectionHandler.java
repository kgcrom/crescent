package org.crescent.config;

import com.thoughtworks.xstream.XStream;
import org.apache.lucene.analysis.Analyzer;
import org.crescent.index.analysis.AnalyzerHolder;
import org.crescent.collection.entity.Collection;
import org.crescent.collection.entity.CollectionField;
import org.crescent.collection.entity.Collections;
import org.crescent.collection.entity.SortField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CollectionHandler {

	private static final Logger logger = LoggerFactory.getLogger(CollectionHandler.class);
	private String path;
	private Collections collections;
	private Map<String, Collection> collectionMap;

	public CollectionHandler(@Value("${crescent.collection-path}") String path) {
		this.path = path;
		loadCollection();
	}

	public void loadCollection() {
		loadFromXML();
		makeAnalyzer();
		makeFieldsMap();
		makeAdditionalFields();
	}

	private void loadFromXML() {
		XStream stream = new XStream();
		stream.allowTypesByWildcard(new String[] { "org.crescent.**"});
		stream.processAnnotations(Collections.class);
		stream.alias("collections", Collections.class);
		stream.addImplicitCollection(Collections.class, "collections");

		ResourceLoader resourceLoader = new ResourceLoader(path);
		InputStream inputStream = resourceLoader.getInputStream();

		collections = (Collections) stream.fromXML(inputStream);

		if (collections == null) {
			String errorMsg = "Crescent Collections is not loaded from xml : [" + path + "]";
			logger.error(errorMsg);

			throw new IllegalStateException(errorMsg);
		}

		// indexingDirectory가 절대경로가 아닌경우 임의로 경로 수정,  maven local profile에서 사용
		List<Collection> list = collections.getCollections();
		for (Collection collection : list) {
			String path = collection.getIndexingDirectory();
			File file = new File(path);
			if (!file.isAbsolute()) {
				String webRoot = System.getProperty("webapp.root");
				if (webRoot != null)
					collection.setIndexingDirectory(webRoot + path);
			}
		}

		collectionMap = new HashMap<>();
		for (Collection collection: collections.getCollections()) {
			collectionMap.put(collection.getName(), collection);
		}

		try {
			inputStream.close();
		} catch (Exception e) {
			logger.error("stream close error ; ", e);
		}
	}

	private void makeAnalyzer() {
		for (Collection collection : collections.getCollections()) {
			List<AnalyzerHolder> analyzerHolders = collection.getAnalyzers();

			for (AnalyzerHolder analyzerHolder : analyzerHolders) {
				String type = analyzerHolder.getType();
				String className = analyzerHolder.getClassName();
				Analyzer analyzer;

				try {
					@SuppressWarnings("unchecked")
					Class<Analyzer> analyzerClass = (Class<Analyzer>) Class.forName(className);

					analyzer = analyzerClass.getDeclaredConstructor().newInstance();

					if("indexing".equals(type)) {
						collection.setIndexingModeAnalyzer(analyzer);
					} else if("search".equals(type)) {
						collection.setSearchModeAnalyzer(analyzer);
					} else {
						throw new IllegalStateException("정의되지 않은 Analyzer type 입니다. ["+type+"]");
					}
				} catch (ClassNotFoundException e) {
					logger.error("class not found, class name: {}", className, e);
				} catch (InstantiationException e) {
					logger.error("failed to new instance, class name: {}", className, e);
				} catch (IllegalAccessException e) {
					logger.error("failed to new instance, class name: {}", className, e);
				} catch (InvocationTargetException e) {
					logger.error("failed to new instance, class name: {}", className, e);
				} catch (NoSuchMethodException e) {
					logger.error("initialize failed, class name: {}", className, e);
				}
			}
		}
	}

	public Collections getCrescentCollections() {
		return this.collections;
	}
	
	public void writeToXML() {

		ResourceLoader resourceLoader = new ResourceLoader(path);
		URL collectionXmlUrl = resourceLoader.getURL();

		XStream stream = new XStream();
		stream.processAnnotations(Collections.class);

		logger.debug("collectionXmlUrl : {}", collectionXmlUrl);

		try {

			File collectionsXmlFile = new File(collectionXmlUrl.toURI());

			logger.debug("collectionXmlUrl to URI: {}", collectionXmlUrl.toURI());
			logger.debug("collectionsXmlFile : {}", collectionsXmlFile);

			FileOutputStream fos = new FileOutputStream(collectionsXmlFile, false);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, Charset.forName("utf-8")));

			stream.toXML(this.collections, bw);
		} catch (URISyntaxException e) {
			logger.error("error : ", e);
		} catch (FileNotFoundException e) {
			logger.error("error : ", e);
		}
	}

	private void makeFieldsMap() {
		List<Collection> collections = this.collections.getCollections();
		
		if(collections.size() == 0) {
			String errorMsg = "There are no Crescent collections!!";
			logger.error(errorMsg);
			
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
	
	private void makeAdditionalFields() {
		List<Collection> collections = this.collections.getCollections();
		
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
					newSortField.setIndexed(true);
					newSortField.setName(sortField.getDest());
					
					fieldMap.put(sortField.getDest(), newSortField);
					
				} catch (CloneNotSupportedException e1) {
					logger.error("error : ", e1);
				}
			}
		}
	}

	public List<Collection> getCollections() {
		return collections.getCollections();
	}

	public Map<String, Collection> getCollectionMap() {
		return collectionMap;
	}

	public Collection getCollection(String name) {
		return collectionMap.get(name);
	}
}

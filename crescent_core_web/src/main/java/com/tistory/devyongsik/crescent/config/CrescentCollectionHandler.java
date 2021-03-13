package com.tistory.devyongsik.crescent.config;

import com.thoughtworks.xstream.XStream;
import com.tistory.devyongsik.crescent.collection.entity.CrescentAnalyzerHolder;
import com.tistory.devyongsik.crescent.collection.entity.CrescentCollection;
import com.tistory.devyongsik.crescent.collection.entity.CrescentCollectionField;
import com.tistory.devyongsik.crescent.collection.entity.CrescentCollections;
import com.tistory.devyongsik.crescent.collection.entity.CrescentSortField;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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

	@Value("#{systemProperties['crescentHome'] == null ? 'default' : systemProperties['crescentHome']}")
	private String crescentHomeLocation = null;
	private Environment environment;
	private CrescentCollections crescentCollections = null;
	private String collectionsXmlLocation = null;

	public CrescentCollectionHandler(Environment environment) {
		this.environment = environment;
	}

	@PostConstruct
	private void init() {
		String activeProfile = environment.getActiveProfiles().length < 1 ? "local" : environment.getActiveProfiles()[0];
		
		log.info("init crescent collection handler....");
		log.info("running mode : {}, crescentHomeLocation : {}", activeProfile, crescentHomeLocation);
		
		String xmlFileName = "collections.xml";
		
		if("test".equals(activeProfile)) {
			xmlFileName = "test-collections.xml";
		}
		
		if("default".equals(crescentHomeLocation)) {
			collectionsXmlLocation = "collection"+"/"+xmlFileName;
		} else {
			collectionsXmlLocation = crescentHomeLocation+"/"+xmlFileName;
		}
		
		log.info("collectionsXmlLocation : {}", collectionsXmlLocation);
		
		
		loadFromXML();
		makeFieldsMap();
		makeAddtionalFields();
	}
	
	private void loadFromXML() {
		
		
		XStream stream = new XStream();
		stream.processAnnotations(CrescentCollections.class);
		stream.alias( "collections", CrescentCollections.class );
		stream.addImplicitCollection( CrescentCollections.class, "crescentCollections" );
		
		log.info("collectionsXmlLocation : {}", collectionsXmlLocation);
		
		ResourceLoader resourceLoader = new ResourceLoader(collectionsXmlLocation);
		InputStream inputStream = resourceLoader.getInputStream();
		
		crescentCollections = (CrescentCollections)stream.fromXML(inputStream);
		
		if(crescentCollections == null) {
			String errorMsg = "Crescent Collections is not loaded from xml : ["+collectionsXmlLocation+"]";
			log.error(errorMsg);
			
			throw new IllegalStateException(errorMsg);
		}
		
		// indexingDirectory가 절대경로가 아닌경우 임의로 경로 수정,  maven local profile에서 사용
		List<CrescentCollection> list = crescentCollections.getCrescentCollections();
		for (CrescentCollection collection : list) {
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
		
		//Analyzer 생성
		for (CrescentCollection collection : list) {
			List<CrescentAnalyzerHolder> analyzerHolders = collection.getAnalyzers();
			
			for(CrescentAnalyzerHolder analyzerHolder : analyzerHolders) {
				String type = analyzerHolder.getType();
				String className = analyzerHolder.getClassName();
				String constructorArgs = analyzerHolder.getConstructorArgs();
				
				Analyzer analyzer = null;
				
				try {
					@SuppressWarnings("unchecked")
					Class<Analyzer> analyzerClass = (Class<Analyzer>) Class.forName(className);	
					
					if(constructorArgs == null || constructorArgs.trim().length() == 0) {
						analyzer = analyzerClass.newInstance();
					} else {
						// TODO version 하드코딩이 아니라 다양한 analyzer에 맞도록 로직 수정
						Version version = Version.parseLeniently(constructorArgs);
						Class<?>[] intArgsClass = new Class<?>[] {Version.class};
						Object[] initArgs = new Object[] {version};
						
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public CrescentCollections getCrescentCollections() {
		return this.crescentCollections;
	}
	
	public void writeToXML() {
		
		ResourceLoader resourceLoader = new ResourceLoader(collectionsXmlLocation);
		URL collectionXmlUrl = resourceLoader.getURL();
		
		XStream stream = new XStream();
		stream.processAnnotations(CrescentCollections.class);
		
		log.debug("collectionXmlUrl : {}", collectionXmlUrl);
		
		try {
			
			File collectionsXmlFile = new File(collectionXmlUrl.toURI());
			
			log.debug("collectionXmlUrl to URI: {}", collectionXmlUrl.toURI());
			log.debug("collectionsXmlFile : {}", collectionsXmlFile);
			
			FileOutputStream fos = new FileOutputStream(collectionsXmlFile, false);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, Charset.forName("utf-8")));
			
			stream.toXML(this.crescentCollections, bw);
			
			bw.close();
			fos.close();
		
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
		List<CrescentCollection> crescentCollectionList = crescentCollections.getCrescentCollections();
		
		if(crescentCollectionList.size() == 0) {
			String errorMsg = "There are no Crescent collections!!";
			log.error(errorMsg);
			
			throw new IllegalStateException(errorMsg);
		}
		
		for(CrescentCollection crescentCollection : crescentCollectionList) {
			Map<String, CrescentCollectionField> fieldMap = crescentCollection.getCrescentFieldByName();
			if(fieldMap == null) {
				fieldMap = new HashMap<>();
			}
			
			List<CrescentCollectionField> fieldList = crescentCollection.getFields();
			for(CrescentCollectionField field : fieldList) {
				fieldMap.put(field.getName(), field);
			}
			
			crescentCollection.setCrescentFieldByName(fieldMap);
		}
	}
	
	private void makeAddtionalFields() {
		List<CrescentCollection> crescentCollectionList = crescentCollections.getCrescentCollections();
		
		for(CrescentCollection crescentCollection : crescentCollectionList) {
			List<CrescentSortField> crescentSortFieldList = crescentCollection.getSortFields();
			Map<String, CrescentCollectionField> fieldMap = crescentCollection.getCrescentFieldByName();
			
			for(CrescentSortField sortField : crescentSortFieldList) {
				CrescentCollectionField field = fieldMap.get(sortField.getSource());
				
				if(field == null) {
					throw new IllegalStateException("정렬 필드 설정에 필요한 원본(source) 필드가 없습니다.");
				}
				
				try {
					CrescentCollectionField newSortField = (CrescentCollectionField)field.clone();
					
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
	
	public void reloadCollectionsXML() {
		loadFromXML();
		makeFieldsMap();
		makeAddtionalFields();
	}
	
}

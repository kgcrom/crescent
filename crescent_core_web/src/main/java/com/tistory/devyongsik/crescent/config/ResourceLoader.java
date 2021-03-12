package com.tistory.devyongsik.crescent.config;

import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;


@Slf4j
public class ResourceLoader {

	private ClassLoader classLoader;
	private Document document = null;
	private String name;
	private Properties properties = null;
	private InputStream inputStream = null;
	private URL url = null;
	
	public ResourceLoader(String name) {
		
		log.info("ResourceLoader init..");
		
		this.name = name;
		this.classLoader = Thread.currentThread().getContextClassLoader();
		initInputStream();
	}
	
	private void initInputStream() {
		
		try {
			inputStream = this.classLoader.getResourceAsStream(name);
			
			if(inputStream == null) {
				inputStream = new FileInputStream(new File(name));
			}
			
			if(inputStream == null) {
				log.error("inputStream {} 를 지정된 경로에서 찾을 수 없습니다.", name);
			}
			
			url = this.classLoader.getResource(name);
			
			if(url == null) {
				url = new File(name).toURI().toURL();
			}
			
			if(url == null) {
				log.error("url {} 를 지정된 경로에서 찾을 수 없습니다.", name);
			}
			
		} catch (Exception e) {
			log.error("{}에 대한 resource를 찾지 못 했습니다.", name);
			throw new IllegalStateException(name+" 에 대한 resource를 찾지 못 했습니다.");
		}
	}
	
	protected InputStream getInputStream() {
		return inputStream;
	}
	
	protected URL getURL() {
		
		return url;
	}
	
	private void buildDocument(InputStream is) throws Exception {
		SAXReader saxReader = new SAXReader();
    	
    	try {
			document = saxReader.read(is);
		} catch (DocumentException e) {
			log.error("build document {}.xml", name, e);
			throw new Exception();
		}
	}
	
	private void buildProperties(InputStream is) throws Exception {
		properties = new Properties();
		try {
			properties.load(is);
		} catch (IOException e) {
			log.error("build properties {}.properties ", name, e);
			throw new Exception();
		}
	}
	
	public Document getDocument() throws Exception {
		if(document == null) {
			buildDocument(inputStream);
		}
		
		return document;
	}
	
	public Properties getProperties() throws Exception {
		if(properties == null) {
			buildProperties(inputStream);
		}
		
		return properties;
	}
}

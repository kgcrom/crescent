package com.tistory.devyongsik.crescent.admin.service;

import com.tistory.devyongsik.crescent.collection.entity.Collection;
import com.tistory.devyongsik.crescent.collection.entity.AnalyzerHolder;
import com.tistory.devyongsik.crescent.collection.entity.CollectionField;
import com.tistory.devyongsik.crescent.collection.entity.Collections;
import com.tistory.devyongsik.crescent.collection.entity.DefaultSearchField;
import com.tistory.devyongsik.crescent.collection.entity.SortField;
import com.tistory.devyongsik.crescent.config.CrescentCollectionHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Slf4j
@Service
public class CollectionManageServiceImpl implements CollectionManageService {

	private final CrescentCollectionHandler collectionHandler;

	public CollectionManageServiceImpl(CrescentCollectionHandler collectionHandler) {
		this.collectionHandler = collectionHandler;
	}

	@Override
	public Collection updateCollectionInfo(HttpServletRequest request) {
		
		Collections collections = collectionHandler.getCrescentCollections();
		
		String selectedCollectionName = request.getParameter("collectionName");

		log.debug("selectedCollectionName : " + selectedCollectionName);

		String indexingModeAnalyzer = request.getParameter("indexingModeAnalyzer");
		String searchModeAnalyzer = request.getParameter("searchModeAnalyzer");
		
		String indexingModeAnalyzerType = request.getParameter("indexingModeAnalyzerType");
		String searchModeAnalyzerType = request.getParameter("searchModeAnalyzerType");
		
		String indexingModeAnalyzerConstArgs = request.getParameter("indexingModeAnalyzerConstArgs");
		String searchModeAnalyzerConstArgs = request.getParameter("searchModeAnalyzerConstArgs");
		
		Collection selectedCollection = collections.getCrescentCollection(selectedCollectionName);
		
		List<AnalyzerHolder> analyzerHolderList = new ArrayList<AnalyzerHolder>();
		AnalyzerHolder indexingModeAnalyzerHolder = new AnalyzerHolder();
		indexingModeAnalyzerHolder.setClassName(indexingModeAnalyzer);
		indexingModeAnalyzerHolder.setConstructorArgs(indexingModeAnalyzerConstArgs);
		indexingModeAnalyzerHolder.setType(indexingModeAnalyzerType);
		analyzerHolderList.add(indexingModeAnalyzerHolder);
		
		AnalyzerHolder searchModeAnalyzerHolder = new AnalyzerHolder();
		searchModeAnalyzerHolder.setClassName(searchModeAnalyzer);
		searchModeAnalyzerHolder.setConstructorArgs(searchModeAnalyzerConstArgs);
		searchModeAnalyzerHolder.setType(searchModeAnalyzerType);
		analyzerHolderList.add(searchModeAnalyzerHolder);
		
		selectedCollection.setAnalyzers(analyzerHolderList);
		
		selectedCollection.setSearcherReloadScheduleMin(StringUtils.defaultIfEmpty(request.getParameter("searcherReloadScheduleMin"), "10"));

		if(log.isDebugEnabled()) {
			log.debug("analyzer : {} ", request.getParameter("analyzer"));
			log.debug("collection Name : {} ", request.getParameter("collectionName"));
			log.debug("indexing Directory : {} ", request.getParameter("indexingDirectory"));
			log.debug("searcher reload schedule min : {} ", request.getParameter("searcherReloadScheduleMin"));
			log.debug("indexingModeAnalyzer : {} ", request.getParameter("indexingModeAnalyzer"));
			log.debug("searchModeAnalyzer : {} ", request.getParameter("searchModeAnalyzer"));
			log.debug("indexingModeAnalyzerType : {} ", request.getParameter("indexingModeAnalyzerType"));
			log.debug("searchModeAnalyzerType : {} ", request.getParameter("searchModeAnalyzerType"));
			log.debug("indexingModeAnalyzerConstArgs : {} ", request.getParameter("indexingModeAnalyzerConstArgs"));
			log.debug("searchModeAnalyzerConstArgs : {} ", request.getParameter("searchModeAnalyzerConstArgs"));
		}

		List<CollectionField> collectionFields = selectedCollection.getFields();

		//추가되는 필드명을 모은다.
		@SuppressWarnings("unchecked")
		Enumeration<String> enumeration = request.getParameterNames();
		List<String> addFieldNameList = new ArrayList<String>();
		while(enumeration.hasMoreElements()) {
			String paramValue = enumeration.nextElement();
			if(paramValue.endsWith("fieldName")) {
				addFieldNameList.add(paramValue.substring(0, paramValue.lastIndexOf("-")));
			}
		}

		log.debug("add field name list : {}", addFieldNameList);

		for(String fieldName :addFieldNameList) {
			CollectionField crescentField = new CollectionField();
			crescentField.setName(fieldName);
			
			if(!collectionFields.contains(crescentField)) {
				collectionFields.add(crescentField);
			}
		}
				
		for(CollectionField crescentField : collectionFields) {

			crescentField.setAnalyze("on".equals(request.getParameter(crescentField.getName()+"-analyze")) ? true : false);
			crescentField.setIndex("on".equals(request.getParameter(crescentField.getName()+"-index")) ? true : false);
			crescentField.setMust("on".equals(request.getParameter(crescentField.getName()+"-must")) ? true : false);
			crescentField.setStore("on".equals(request.getParameter(crescentField.getName()+"-store")) ? true : false);
			crescentField.setTermoffset("on".equals(request.getParameter(crescentField.getName()+"-termoffset")) ? true : false);
			crescentField.setTermposition("on".equals(request.getParameter(crescentField.getName()+"-termposition")) ? true : false);
			crescentField.setTermvector("on".equals(request.getParameter(crescentField.getName()+"-termvector")) ? true : false);
			crescentField.setRemoveHtmlTag("on".equals(request.getParameter(crescentField.getName()+"-removeHtmlTag")) ? true : false);

			crescentField.setBoost(Float.parseFloat(StringUtils.defaultString(request.getParameter(crescentField.getName()+"-boost"), "0")));
			crescentField.setType(StringUtils.defaultString(request.getParameter(crescentField.getName()+"-type"), "STRING"));


			//sort field 처리
			if("on".equals(request.getParameter(crescentField.getName()+"-sortField"))) {
				SortField sortField = new SortField();
				sortField.setSource(crescentField.getName());
				sortField.setDest(crescentField.getName()+"_sort");

				if(!selectedCollection.getSortFields().contains(sortField)) {
					selectedCollection.getSortFields().add(sortField);
				}
			}

			//default search field 처리
			if("on".equals(request.getParameter(crescentField.getName()+"-defaultSearchField"))) {
				DefaultSearchField defaultSearchField = new DefaultSearchField();
				defaultSearchField.setName(crescentField.getName());

				if(!selectedCollection.getDefaultSearchFields().contains(defaultSearchField)) {
					selectedCollection.getDefaultSearchFields().add(defaultSearchField);
				}
			}

			if(log.isDebugEnabled()) {
				log.debug("crescentField Name {} = {}", crescentField.getName(), "sortField : " + request.getParameter(crescentField.getName()+"-sortField"));
				log.debug("crescentField Name {} = {}", crescentField.getName(), "defaultSearchField : "+ request.getParameter(crescentField.getName()+"-defaultSearchField"));
			}

			if(log.isDebugEnabled()) {
				log.debug("crescentField Name {} = {}", crescentField.getName(), "analyze : " + request.getParameter(crescentField.getName()+"-analyze"));
				log.debug("crescentField Name {} = {}", crescentField.getName(), "index : " + request.getParameter(crescentField.getName()+"-index"));
				log.debug("crescentField Name {} = {}", crescentField.getName(), "must : " + request.getParameter(crescentField.getName()+"-must"));
				log.debug("crescentField Name {} = {}", crescentField.getName(), "store : " + request.getParameter(crescentField.getName()+"-store"));
				log.debug("crescentField Name {} = {}", crescentField.getName(), "termoffset : " + request.getParameter(crescentField.getName()+"-termoffset"));
				log.debug("crescentField Name {} = {}", crescentField.getName(), "termposition : " + request.getParameter(crescentField.getName()+"-termposition"));
				log.debug("crescentField Name {} = {}", crescentField.getName(), "termvector : " + request.getParameter(crescentField.getName()+"-termvector"));
				log.debug("crescentField Name {} = {}", crescentField.getName(), "boost : " + request.getParameter(crescentField.getName()+"-boost"));
				log.debug("crescentField Name {} = {}", crescentField.getName(), "type : " + request.getParameter(crescentField.getName()+"-type"));
			}
		}
			
		collectionHandler.writeToXML();
		collectionHandler.loadCollection();
		
		collections = collectionHandler.getCrescentCollections();
		selectedCollection = collections.getCrescentCollection(selectedCollectionName);
		
		return selectedCollection;
	}
	
	
	@Override
	public Collection addCollectionInfo(HttpServletRequest request) {
		String selectedCollectionName = request.getParameter("collectionName");

		log.debug("selectedCollectionName : " + selectedCollectionName);
		
		Collection newCollection = new Collection();
		newCollection.setName(selectedCollectionName);
		newCollection.setIndexingDirectory(request.getParameter("indexingDirectory"));
		
		newCollection.setSearcherReloadScheduleMin(StringUtils.defaultIfEmpty(request.getParameter("searcherReloadScheduleMin"), "10"));

		String indexingModeAnalyzer = request.getParameter("indexingModeAnalyzer");
		String searchModeAnalyzer = request.getParameter("searchModeAnalyzer");
		
		String indexingModeAnalyzerType = request.getParameter("indexingModeAnalyzerType");
		String searchModelAnalyzerType = request.getParameter("searchModeAnalyzerType");
		
		String indexingModeAnalyzerConstArgs = request.getParameter("indexingModeAnalyzerConstArgs");
		String searchModeAnalyzerConstArgs = request.getParameter("searchModeAnalyzerConstArgs");
		
		List<AnalyzerHolder> analyzerHolderList = new ArrayList<AnalyzerHolder>();
		AnalyzerHolder indexingModeAnalyzerHolder = new AnalyzerHolder();
		indexingModeAnalyzerHolder.setClassName(indexingModeAnalyzer);
		indexingModeAnalyzerHolder.setConstructorArgs(indexingModeAnalyzerConstArgs);
		indexingModeAnalyzerHolder.setType(indexingModeAnalyzerType);
		analyzerHolderList.add(indexingModeAnalyzerHolder);
		
		AnalyzerHolder searchModeAnalyzerHolder = new AnalyzerHolder();
		searchModeAnalyzerHolder.setClassName(searchModeAnalyzer);
		searchModeAnalyzerHolder.setConstructorArgs(searchModeAnalyzerConstArgs);
		searchModeAnalyzerHolder.setType(searchModelAnalyzerType);
		analyzerHolderList.add(searchModeAnalyzerHolder);
		
		if(log.isDebugEnabled()) {
			log.debug("analyzer : {} ", request.getParameter("analyzer"));
			log.debug("collection Name : {} ", request.getParameter("collectionName"));
			log.debug("indexing Directory : {} ", request.getParameter("indexingDirectory"));
			log.debug("searcher reload schedule min : {} ", request.getParameter("searcherReloadScheduleMin"));
			log.debug("indexingModeAnalyzer : {} ", request.getParameter("indexingModeAnalyzer"));
			log.debug("searchModeAnalyzer : {} ", request.getParameter("searchModeAnalyzer"));
			log.debug("indexingModeAnalyzerType : {} ", request.getParameter("indexingModeAnalyzerType"));
			log.debug("searchModelAnalyzerType : {} ", request.getParameter("searchModelAnalyzerType"));
			log.debug("indexingModeAnalyzerConstArgs : {} ", request.getParameter("indexingModeAnalyzerConstArgs"));
			log.debug("searchModeAnalyzerConstArgs : {} ", request.getParameter("searchModeAnalyzerConstArgs"));
		}

		//필드들을 걸러낸다.
		@SuppressWarnings("unchecked")
		Enumeration<String> enumeration = (Enumeration<String>)request.getParameterNames();
		List<String> fieldNameList = new ArrayList<String>();
		while(enumeration.hasMoreElements()) {
			String paramName = enumeration.nextElement();

			if(paramName.endsWith("-fieldName")) { //필수값
				String fieldName = paramName.split("-")[0];
				fieldNameList.add(fieldName);
			}
		}
		
		List<CollectionField> newCollectionFieldList = new ArrayList<CollectionField>();
		List<SortField> sortFieldList = new ArrayList<SortField>();
		List<DefaultSearchField> defaultSearchFieldList = new ArrayList<DefaultSearchField>();
		
		for(String fieldName : fieldNameList) {
			CollectionField newCollectionField = new CollectionField();
			
			newCollectionField.setName(fieldName);
			newCollectionField.setAnalyze("on".equals(request.getParameter(fieldName+"-analyze")) ? true : false);
			newCollectionField.setIndex("on".equals(request.getParameter(fieldName+"-index")) ? true : false);
			newCollectionField.setMust("on".equals(request.getParameter(fieldName+"-must")) ? true : false);
			newCollectionField.setStore("on".equals(request.getParameter(fieldName+"-store")) ? true : false);
			newCollectionField.setTermoffset("on".equals(request.getParameter(fieldName+"-termoffset")) ? true : false);
			newCollectionField.setTermposition("on".equals(request.getParameter(fieldName+"-termposition")) ? true : false);
			newCollectionField.setTermvector("on".equals(request.getParameter(fieldName+"-termvector")) ? true : false);

			newCollectionField.setBoost(Float.parseFloat(StringUtils.defaultIfEmpty(request.getParameter(fieldName+"-boost"), "0")));
			newCollectionField.setType(StringUtils.defaultString(request.getParameter(fieldName+"-type"), "STRING"));

			newCollectionFieldList.add(newCollectionField);
			
			//sort field 처리			
			if("on".equals(request.getParameter(fieldName+"-sortField"))) {
				SortField sortField = new SortField();
				sortField.setSource(fieldName);
				sortField.setDest(fieldName+"_sort");

				sortFieldList.add(sortField);
			}
			
			//default search field 처리
			if("on".equals(request.getParameter(fieldName+"-defaultSearchField"))) {
				DefaultSearchField defaultSearchField = new DefaultSearchField();
				defaultSearchField.setName(fieldName);
				defaultSearchFieldList.add(defaultSearchField);
			}
			

			if(log.isDebugEnabled()) {
				log.debug("crescentField Name {} = {}", fieldName, "sortField : " + request.getParameter(fieldName+"-sortField"));
				log.debug("crescentField Name {} = {}", fieldName, "defaultSearchField : "+ request.getParameter(fieldName+"-defaultSearchField"));
			}

			if(log.isDebugEnabled()) {
				log.debug("crescentField Name {} = {}", fieldName, "analyze : " + request.getParameter(fieldName+"-analyze"));
				log.debug("crescentField Name {} = {}", fieldName, "index : " + request.getParameter(fieldName+"-index"));
				log.debug("crescentField Name {} = {}", fieldName, "must : " + request.getParameter(fieldName+"-must"));
				log.debug("crescentField Name {} = {}", fieldName, "store : " + request.getParameter(fieldName+"-store"));
				log.debug("crescentField Name {} = {}", fieldName, "termoffset : " + request.getParameter(fieldName+"-termoffset"));
				log.debug("crescentField Name {} = {}", fieldName, "termposition : " + request.getParameter(fieldName+"-termposition"));
				log.debug("crescentField Name {} = {}", fieldName, "termvector : " + request.getParameter(fieldName+"-termvector"));
				log.debug("crescentField Name {} = {}", fieldName, "boost : " + request.getParameter(fieldName+"-boost"));
				log.debug("crescentField Name {} = {}", fieldName, "type : " + request.getParameter(fieldName+"-type"));
			}
		}
		
		newCollection.setSortFields(sortFieldList);
		newCollection.setDefaultSearchFields(defaultSearchFieldList);
		newCollection.setFields(newCollectionFieldList);
		newCollection.setAnalyzers(analyzerHolderList);
		
		collectionHandler.getCrescentCollections().getCrescentCollections().add(newCollection);
		collectionHandler.writeToXML();
		collectionHandler.loadCollection();
		
		return newCollection;
	}
	
	@Override
	public void deleteCollectionInfo(String collectionName) {

		Collections collections = collectionHandler.getCrescentCollections();
		List<Collection> collectionList = collections.getCrescentCollections();
		
		int targetIndex = -1;
		
		for(int index = 0; index < collectionList.size(); index++) {
			if(collectionName.equals(collectionList.get(index).getName())) {
				targetIndex = index;
				break;
			}
		}
		
		if(targetIndex > 0) {
			collectionList.remove(targetIndex);
		} else {
			log.error("삭제하려는 컬렉션이 존재하지 않습니다. [{}]", collectionName);
			throw new IllegalArgumentException("삭제하려는 컬렉션이 존재하지 않습니다.");
		}
		
		collectionHandler.writeToXML();
		collectionHandler.loadCollection();
	}
}

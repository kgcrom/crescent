package com.tistory.devyongsik.crescent.collection.entity;

import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("collection")
public class Collection {
	
	@XStreamAsAttribute
	private String name;
	
	private String indexingDirectory;
	
	private String searcherReloadScheduleMin;
	
	@XStreamOmitField
	private Map<String, CollectionField> crescentFieldByName;
	
	private List<CollectionField> fields;
	private List<DefaultSearchField> defaultSearchFields;
	private List<SortField> sortFields;
	
	private List<AnalyzerHolder> analyzers;
	
	@XStreamOmitField
	private Analyzer indexingModeAnalyzer;
	
	@XStreamOmitField
	private Analyzer searchModeAnalyzer;
	
	public Analyzer getIndexingModeAnalyzer() {
		return indexingModeAnalyzer;
	}
	
	public Analyzer getSearchModeAnalyzer() {
		return searchModeAnalyzer;
	}
	
	public void setIndexingModeAnalyzer(Analyzer analyzer) {
		this.indexingModeAnalyzer = analyzer;
	}
	
	public void setSearchModeAnalyzer(Analyzer analyzer) {
		this.searchModeAnalyzer = analyzer;
	}
	
	
	public List<AnalyzerHolder> getAnalyzers() {
		return analyzers;
	}
	public void setAnalyzers(List<AnalyzerHolder> analyzers) {
		this.analyzers = analyzers;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<CollectionField> getFields() {
		return fields;
	}
	public void setFields(List<CollectionField> fields) {
		this.fields = fields;
	}
	public List<DefaultSearchField> getDefaultSearchFields() {
		return defaultSearchFields;
	}
	public void setDefaultSearchFields(
			List<DefaultSearchField> defaultSearchFields) {
		this.defaultSearchFields = defaultSearchFields;
	}
	public List<SortField> getSortFields() {
		return sortFields;
	}
	public void setSortFields(List<SortField> sortFields) {
		this.sortFields = sortFields;
	}
	
	public String getIndexingDirectory() {
		return indexingDirectory;
	}
	public void setIndexingDirectory(String indexingDirectory) {
		this.indexingDirectory = indexingDirectory;
	}
	
	public Map<String, CollectionField> getCrescentFieldByName() {
		return crescentFieldByName;
	}
	public void setCrescentFieldByName(
			Map<String, CollectionField> crescentFieldByName) {
		this.crescentFieldByName = crescentFieldByName;
	}
	public String getSearcherReloadScheduleMin() {
		return searcherReloadScheduleMin;
	}
	public void setSearcherReloadScheduleMin(String searcherReloadScheduleMin) {
		this.searcherReloadScheduleMin = searcherReloadScheduleMin;
	}
	
	@Override
	public String toString() {
		return "CrescentCollection [name=" + name + ", indexingDirectory="
				+ indexingDirectory + ", searcherReloadScheduleMin="
				+ searcherReloadScheduleMin + ", crescentFieldByName="
				+ crescentFieldByName + ", fields=" + fields
				+ ", defaultSearchFields=" + defaultSearchFields
				+ ", sortFields=" + sortFields + ", analyzers=" + analyzers
				+ "]";
	}
}

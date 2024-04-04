package org.crescent.collection.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.apache.lucene.analysis.Analyzer;

import java.util.List;
import java.util.Map;

import org.crescent.index.analysis.AnalyzerHolder;

@XStreamAlias("collection")
public class Collection {

  @XStreamAsAttribute
  private String name;
  private String indexingDirectory;
  private String flushInterval;
  private List<CollectionField> fields;
  private List<DefaultSearchField> defaultSearchFields;
  private List<SortField> sortFields;
  private List<AnalyzerHolder> analyzers;

  @JsonIgnore
  @XStreamOmitField
  private Map<String, CollectionField> crescentFieldByName;
  @JsonIgnore
  @XStreamOmitField
  private Analyzer indexingModeAnalyzer;
  @JsonIgnore
  @XStreamOmitField
  private Analyzer searchModeAnalyzer;

  public String getName() {
    return name;
  }

  public String getIndexingDirectory() {
    return indexingDirectory;
  }

  public String getFlushInterval() {
    return flushInterval;
  }

  public List<CollectionField> getFields() {
    return fields;
  }

  public List<DefaultSearchField> getDefaultSearchFields() {
    return defaultSearchFields;
  }

  public List<SortField> getSortFields() {
    return sortFields;
  }

  public List<AnalyzerHolder> getAnalyzers() {
    return analyzers;
  }

  public Map<String, CollectionField> getCrescentFieldByName() {
    return crescentFieldByName;
  }

  public Analyzer getIndexingModeAnalyzer() {
    return indexingModeAnalyzer;
  }

  public Analyzer getSearchModeAnalyzer() {
    return searchModeAnalyzer;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setIndexingDirectory(String indexingDirectory) {
    this.indexingDirectory = indexingDirectory;
  }

  public void setFlushInterval(String flushInterval) {
    this.flushInterval = flushInterval;
  }

  public void setFields(List<CollectionField> fields) {
    this.fields = fields;
  }

  public void setDefaultSearchFields(List<DefaultSearchField> defaultSearchFields) {
    this.defaultSearchFields = defaultSearchFields;
  }

  public void setSortFields(List<SortField> sortFields) {
    this.sortFields = sortFields;
  }

  public void setAnalyzers(List<AnalyzerHolder> analyzers) {
    this.analyzers = analyzers;
  }

  public void setCrescentFieldByName(Map<String, CollectionField> crescentFieldByName) {
    this.crescentFieldByName = crescentFieldByName;
  }

  public void setIndexingModeAnalyzer(Analyzer indexingModeAnalyzer) {
    this.indexingModeAnalyzer = indexingModeAnalyzer;
  }

  public void setSearchModeAnalyzer(Analyzer searchModeAnalyzer) {
    this.searchModeAnalyzer = searchModeAnalyzer;
  }
}

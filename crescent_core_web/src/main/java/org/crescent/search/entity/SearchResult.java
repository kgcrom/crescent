package org.crescent.search.entity;

import java.util.List;
import java.util.Map;

public class SearchResult {

  private long totalHists;
  private List<Map<String, Object>> docs = null;

  public long getTotalHists() {
    return totalHists;
  }

  public List<Map<String, Object>> getDocs() {
    return docs;
  }

  public void setTotalHists(long totalHists) {
    this.totalHists = totalHists;
  }

  public void setDocs(List<Map<String, Object>> docs) {
    this.docs = docs;
  }
}

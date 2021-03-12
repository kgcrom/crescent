package com.tistory.devyongsik.crescent.search.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class SearchResult {

  private int totalHitsCount = 0;
  private List<Map<String, String>> resultList = null;
  private int errorCode = 0;
  private String errorMsg = "";
  private Map<String, Object> searchResult = null;
}

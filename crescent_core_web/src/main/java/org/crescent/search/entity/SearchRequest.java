package org.crescent.search.entity;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SearchRequest {

  private String collectionName;
  private String query;
  private String filterQuery;
  private String pageNum;
  private String pageSize;
  private String sort;
  private String searchField;

  public String getCollectionName() {
    return collectionName;
  }

  public String getQuery() {
    return query;
  }

  public String getFilterQuery() {
    return filterQuery;
  }

  public String getPageNum() {
    return pageNum;
  }

  public String getPageSize() {
    return pageSize;
  }

  public String getSort() {
    return sort;
  }

  public String getSearchField() {
    return searchField;
  }

  public SearchRequest(Builder builder) {
    this.collectionName = builder.collectionName;
    this.query = builder.query;
    this.filterQuery = builder.filterQuery;
    this.pageNum = builder.pageNum;
    this.pageSize = builder.pageSize;
    this.sort = builder.sort;
    this.searchField = builder.searchField;
  }

  public static Builder builder() {
    return new Builder();
  }

  // required filed
//  public static Builder builder(String collectionName) {
//    return new Builder(collectionName);
//  }

  public static class Builder {
    private String collectionName;
    private String query;
    private String filterQuery;
    private String pageNum;
    private String pageSize;
    private String sort;
    private String searchField;

    public Builder() {}

    public Builder collectionName(String collectionName) {
      this.collectionName = collectionName;
      return this;
    }

    public Builder query(String query) {
      this.query = query;
      return this;
    }

    public Builder filterQuery(String filterQuery) {
      this.filterQuery = filterQuery;
      return this;
    }

    public Builder pageNum(String pageNum) {
      this.pageNum = pageNum;
      return this;
    }

    public Builder pageSize(String pageSize) {
      this.pageSize = pageSize;
      return this;
    }

    public Builder sort(String sort) {
      this.sort = sort;
      return this;
    }

    public Builder searchField(String searchField) {
      this.searchField = searchField;
      return this;
    }

    public SearchRequest build() {
      return new SearchRequest(this);
    }
  }

}

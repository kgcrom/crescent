package org.crescent.search;

import java.io.IOException;
import org.crescent.search.entity.SearchRequest;
import org.crescent.search.entity.SearchResult;
import org.crescent.search.service.SearchService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SearchController {

  private final SearchService searchService;

  public SearchController(SearchService searchService) {
    this.searchService = searchService;
  }

  @RequestMapping("/v1/search")
  public SearchResult searchDocument(
      @RequestParam String collectionName,
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String fq) throws IOException {
    SearchRequest.Builder builder = SearchRequest.builder().collectionName(collectionName);
    if (q != null && !q.isEmpty()) {
      builder.query(q);
    }
    if (fq != null && !fq.isEmpty()) {
      builder.filterQuery(fq);
    }
    return searchService.search(builder.build());
  }
}

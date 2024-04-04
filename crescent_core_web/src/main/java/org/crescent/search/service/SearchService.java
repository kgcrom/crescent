package org.crescent.search.service;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TopDocs;
import org.crescent.search.entity.SearchRequestValidator;
import org.crescent.collection.entity.Collection;
import org.crescent.collection.entity.CollectionField;
import org.crescent.collection.entity.DefaultSearchField;
import org.crescent.config.CollectionHandler;
import org.crescent.parser.SimpleQueryParser;
import org.crescent.search.entity.SearchRequest;
import org.crescent.search.entity.SearchResult;
import org.crescent.search.searcher.CrescentSearcherManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SearchService {

  private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

  private SimpleQueryParser simpleQueryParser;
  private SearchRequestValidator searchRequestValidator;

  private final CrescentSearcherManager crescentSearcherManager;
  private final CollectionHandler collectionHandler;

  public SearchService(CrescentSearcherManager crescentSearcherManager,
                       CollectionHandler collectionHandler) {
    this.crescentSearcherManager = crescentSearcherManager;
    this.collectionHandler = collectionHandler;
    this.simpleQueryParser = new SimpleQueryParser();
    this.searchRequestValidator = new SearchRequestValidator();
  }

  public SearchResult search(SearchRequest request) throws IOException {
    if (request.getCollectionName() == null) {
      throw new InvalidParameterException("search request must have collection name");
    }
    searchRequestValidator.valid(request, collectionHandler.getCollection(request.getCollectionName()));

    SearchResult result = new SearchResult();
    IndexSearcher searcher = null;
    SearcherManager searcherManager = crescentSearcherManager.getSearcherManager(
            request.getCollectionName());
    Collection collection = collectionHandler.getCollection(request.getCollectionName());

    int offset = 0;
    int nHits = 10;

    List<DefaultSearchField> defaultSearchFields = collectionHandler.getCollection(request.getCollectionName()).getDefaultSearchFields();
    Query query = simpleQueryParser.parse(collection, request, defaultSearchFields);

    // TODO implement sort, docvalue, aggregation
    try {
      // TODO: Solr, ES에서는 lucene search 어떻게 하는지 살펴보고 적용하기, (offset,total)
      searcher = searcherManager.acquire();
      TopDocs topdocs = searcher.search(query, nHits);

      result.setTotalHists(topdocs.totalHits.value);

      List<Map<String, Object>> docs = new ArrayList<>(nHits);
      for (int i = offset; i < topdocs.totalHits.value; i++) {
        int docId = topdocs.scoreDocs[i].doc;

        Document document = searcher.doc(docId);
        // TODO highlighter 적용하기 FastVectorHighlighter

        Map<String, Object> doc = new HashMap<>();
        for (CollectionField field : collection.getFields()) {
          if (document.get(field.getName()) != null) {
            if ("long".equals(field.getType())) {
              doc.put(field.getName(), Long.parseLong(document.get(field.getName())));
            } else if ("integer".equals(field.getType())) {
              doc.put(field.getName(), Integer.parseInt(document.get(field.getName())));
            } else {
              doc.put(field.getName(), document.get(field.getName()));
            }
          }
        }

        docs.add(doc);
      }

      result.setDocs(docs);
    } catch (IOException e) {
      logger.error("failed to acquire IndexSearcher, collection name: {}",
              request.getCollectionName(), e);
      throw e;
    } finally {
      searcherManager.release(searcher);
    }

    return result;
  }
}

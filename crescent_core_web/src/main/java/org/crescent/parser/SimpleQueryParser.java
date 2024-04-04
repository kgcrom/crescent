package org.crescent.parser;

import java.util.List;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.MatchNoDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.QueryBuilder;
import org.crescent.collection.entity.Collection;
import org.crescent.collection.entity.CollectionField;
import org.crescent.collection.entity.DefaultSearchField;
import org.crescent.search.entity.SearchRequest;

public class SimpleQueryParser {

  public Query parse(Collection collection, SearchRequest request,
      List<DefaultSearchField> defaultSearchFields) {
    if (request.getQuery() == null && request.getFilterQuery() == null) {
      return new MatchNoDocsQuery("user query has no query and filter query");
    }

    // TODO analyzer 적용해서 token으로 쿼리 만들도록 구현
    BooleanQuery.Builder executeQuery = new BooleanQuery.Builder();
    if (request.getQuery() != null) {
      QueryBuilder builder = new QueryBuilder(collection.getSearchModeAnalyzer());
      if ("*:*".equals(request.getQuery())) {
        return new MatchAllDocsQuery();
      }
      if (request.getQuery().indexOf(":") != -1) {
        String[] tq = request.getQuery().split(":", 2);
        Query q = builder.createPhraseQuery(tq[0], tq[1]);
        executeQuery.add(q, Occur.SHOULD);
      } else {
        for (DefaultSearchField defaultField : defaultSearchFields) {
          Query q = builder.createPhraseQuery(defaultField.getName(), request.getQuery());
          executeQuery.add(q, Occur.SHOULD);
        }
      }
    }

    // TODO cache filter query result
    if (request.getFilterQuery() != null) {
      if (request.getFilterQuery().indexOf(":") == -1) {
        return new MatchNoDocsQuery("filter query must have field and text, ex: board_id:0");
      }
      // TODO support range query
      String[] tq = request.getFilterQuery().split(":", 2);

      for (CollectionField field : collection.getFields()) {
        if (field.getName().equals(tq[0])) {
          if ("integer".equals(field.getType())) {
            executeQuery.add(IntPoint.newExactQuery(tq[0], Integer.parseInt(tq[1])), Occur.MUST);
          } else if ("long".equals(field.getType())) {
            executeQuery.add(LongPoint.newExactQuery(tq[0], Long.parseLong(tq[1])), Occur.MUST);
          } else {
            executeQuery.add(new TermQuery(new Term(tq[0], tq[1])), Occur.MUST);
          }
        }
      }
    }
    return executeQuery.build();
  }

  // TDOO implement to generate token list using analyzer.
}

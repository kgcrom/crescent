package com.tistory.devyongsik.crescent.query;

import com.tistory.devyongsik.crescent.collection.entity.CrescentCollectionField;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.sandbox.queries.regex.RegexQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.io.StringReader;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class CustomQueryStringParser {

  private static final Pattern pattern = Pattern.compile("(.*?)(:)(\".*?\")");
  private Query resultQuery = null;

  protected Query getQuery(List<CrescentCollectionField> indexedFields, String customQueryString, Analyzer analyzer, String regexQueryString) throws InvalidParameterException, IOException {
    if (resultQuery != null) {
      return this.resultQuery;
    } else {
      return getQueryFromCustomQuery(indexedFields, customQueryString, analyzer, regexQueryString);
    }
  }

  private Query getQueryFromCustomQuery(List<CrescentCollectionField> indexedFields, String customQueryString, Analyzer analyzer, String regexQueryString)
      throws InvalidParameterException, IOException {

    List<QueryAnalysisResult> queryAnalysisResultList = getQueryAnalysisResults(customQueryString);

    BooleanQuery resultQuery = new BooleanQuery();

    CrescentCollectionField searchTargetField = null;

    String fieldName = "";
    Occur occur = Occur.SHOULD;
    String userRequestQuery = "";
    float boost = 0F;

    boolean isRangeQuery = false;

    boolean any = true;
    boolean isLongField = false;
    boolean isAnalyzed = false;

    for (QueryAnalysisResult queryAnalysisResult : queryAnalysisResultList) {

      fieldName = queryAnalysisResult.getFieldName();
      occur = queryAnalysisResult.getOccur();
      userRequestQuery = queryAnalysisResult.getUserQuery();
      boost = queryAnalysisResult.getBoost();
      isRangeQuery = queryAnalysisResult.isRangeQuery();

      //field가 검색 대상에 있는지 확인..
      for (CrescentCollectionField crescentField : indexedFields) {
        if (fieldName.equals(crescentField.getName())) {
          any = false;
          searchTargetField = crescentField;

          isLongField = "LONG".equals(crescentField.getType());
          isAnalyzed = crescentField.isAnalyze();

          log.debug("selected searchTargetField : {} ", searchTargetField);
          break;
        }
      }

      if (any) {
        log.error("검색 할 수 없는 필드입니다. {} ", fieldName);
        throw new InvalidParameterException();
      }

      //range쿼리인 경우에는 RangeQuery 생성
      if (isRangeQuery) {

        //QueryParser qp = new QueryParser(Version.LUCENE_36, fieldName, analyzer);
        String minValue = "";
        String maxValue = "";
        boolean isIncludeMin = false;
        boolean isIncludeMax = false;

        String[] splitQuery = userRequestQuery.split("TO");
        log.info("splitQuery : {}", Arrays.toString(splitQuery));

        if (splitQuery.length != 2) {
          log.error("문법 오류 확인바랍니다. {} ", userRequestQuery);
          throw new InvalidParameterException();
        }

        if (splitQuery[0].trim().startsWith("[")) {
          isIncludeMin = true;
        }

        if (splitQuery[1].trim().endsWith("]")) {
          isIncludeMax = true;
        }

        log.debug("minInclude : {}, maxInclude : {}", isIncludeMin, isIncludeMax);

        minValue = splitQuery[0].trim().substring(1);
        maxValue = splitQuery[1].trim().substring(0, splitQuery[1].trim().length() - 1);

        log.debug("minValue : {}, maxValue : {}", minValue, maxValue);

        boolean isNumeric = false;
        isNumeric = StringUtils.isNumeric(minValue) && StringUtils.isNumeric(maxValue);

        log.debug("isLongField : {}", isLongField);
        log.debug("is numeric : {}", isNumeric);

        Query query = null;

        if (isAnalyzed) {
          log.error("범위검색 대상 field는 analyzed값이 false이어야 합니다. {} ", userRequestQuery);
          throw new InvalidParameterException();
        }

        if (isLongField && isNumeric) {

          query = NumericRangeQuery.newLongRange(fieldName, Long.parseLong(minValue), Long.parseLong(maxValue), isIncludeMin, isIncludeMax);

        } else if (!(isLongField && isNumeric)) {

          BytesRef minValBytes = new BytesRef(minValue);
          BytesRef maxValBytes = new BytesRef(maxValue);

          query = new TermRangeQuery(fieldName, minValBytes, maxValBytes, isIncludeMin, isIncludeMax);

        } else {
          log.error("범위검색은 필드의 타입과 쿼리의 타입이 맞아야 합니다. {} ", userRequestQuery);
          throw new InvalidParameterException();
        }

        resultQuery.add(query, occur);

      } else {
        //쿼리 생성..
        String[] keywords = userRequestQuery.split(" ");

        if (log.isDebugEnabled()) {
          log.debug("split keyword : {}", Arrays.toString(keywords));
        }

        for (int i = 0; i < keywords.length; i++) {
          ArrayList<String> analyzedTokenList = analyzedTokenList(analyzer, keywords[i]);

          if (!isAnalyzed || analyzedTokenList.size() == 0) {

            Term t = new Term(fieldName, keywords[i]);
            Query query = new TermQuery(t);

            if (searchTargetField.getBoost() > 1F && boost > 1F) {
              query.setBoost(searchTargetField.getBoost() + boost);
            } else if (boost > 1F) {
              query.setBoost(boost);
            } else if (searchTargetField.getBoost() > 1F) {
              query.setBoost(searchTargetField.getBoost());
            }

            resultQuery.add(query, occur);

            log.debug("query : {} ", query.toString());
            log.debug("result query : {} ", resultQuery.toString());

          } else {

            for (String str : analyzedTokenList) {

              Term t = new Term(fieldName, str);
              Query query = new TermQuery(t);

              if (searchTargetField.getBoost() > 1F && boost > 1F) {
                query.setBoost(searchTargetField.getBoost() + boost);
              } else if (boost > 1F) {
                query.setBoost(boost);
              } else if (searchTargetField.getBoost() > 1F) {
                query.setBoost(searchTargetField.getBoost());
              }

              resultQuery.add(query, occur);

              log.debug("query : {} ", query.toString());
              log.debug("result query : {} ", resultQuery.toString());
            }
          }
        }
      }
    }

    if (regexQueryString != null && regexQueryString.length() > 0) {
      List<QueryAnalysisResult> regexQueryAnalysisResultList = getQueryAnalysisResults(regexQueryString);

      for (QueryAnalysisResult queryAnalysisResult : regexQueryAnalysisResultList) {
        Term term = new Term(queryAnalysisResult.getFieldName(), queryAnalysisResult.getUserQuery());
        Query regexQuery = new RegexQuery(term);

        log.info("Regex Query : {}", regexQuery);

        resultQuery.add(regexQuery, queryAnalysisResult.getOccur());
      }
    }

    log.info("result query : {} ", resultQuery.toString());

    this.resultQuery = resultQuery;

    return resultQuery;
  }

  private ArrayList<String> analyzedTokenList(Analyzer analyzer, String splitedKeyword) throws IOException {

    ArrayList<String> rst = new ArrayList<String>();
    //split된 검색어를 Analyze..
    TokenStream stream = null;
    try {
      stream = analyzer.tokenStream("", new StringReader(splitedKeyword));
      CharTermAttribute charTerm = stream.getAttribute(CharTermAttribute.class);

      stream.reset();

      while (stream.incrementToken()) {
        rst.add(charTerm.toString());
      }

      stream.close();

    } catch (IOException e) {
      log.error("error in DefaultKeywordParser : ", e);
      throw e;
    }

    log.debug("[{}] 에서 추출된 명사 : [{}]", splitedKeyword, rst.toString());


    return rst;
  }

  private List<QueryAnalysisResult> getQueryAnalysisResults(String analysisTargetString) throws InvalidParameterException {
    List<QueryAnalysisResult> queryAnalysisResultList = new ArrayList<QueryAnalysisResult>();

    Matcher m = pattern.matcher(analysisTargetString);

    while (m.find()) {
      if (m.groupCount() != 3) {
        log.error("query syntax error: {}", analysisTargetString);
        throw new InvalidParameterException();
      }

      QueryAnalysisResult anaysisResult = new QueryAnalysisResult();

      Occur occur = Occur.SHOULD;
      String userRequestQuery = "";
      float boost = 0F;
      boolean isRangeQuery = false;

      String fieldName = m.group(1).trim();
      if (fieldName.startsWith("-")) {
        occur = Occur.MUST_NOT;
        fieldName = fieldName.substring(1);
      } else if (fieldName.startsWith("+")) {
        occur = Occur.MUST;
        fieldName = fieldName.substring(1);
      }

      userRequestQuery = m.group(3).trim().replaceAll("\"", "");
      if ((userRequestQuery.startsWith("[") && userRequestQuery.endsWith("]"))
          || (userRequestQuery.startsWith("{") && userRequestQuery.endsWith("}"))) {

        isRangeQuery = true;

      }

      //boost 정보 추출
      int indexOfBoostSign = userRequestQuery.indexOf("^");
      if (indexOfBoostSign >= 0) {
        boost = Float.parseFloat(userRequestQuery.substring(indexOfBoostSign + 1));
        userRequestQuery = userRequestQuery.substring(0, indexOfBoostSign);
      }

      log.debug("user Request Query : {} ", userRequestQuery);
      log.debug("boost : {} ", boost);

      anaysisResult.setFieldName(fieldName);
      anaysisResult.setBoost(boost);
      anaysisResult.setOccur(occur);
      anaysisResult.setRangeQuery(isRangeQuery);
      anaysisResult.setUserQuery(userRequestQuery);

      queryAnalysisResultList.add(anaysisResult);
    }

    return queryAnalysisResultList;
  }

  private class QueryAnalysisResult {

    private String fieldName;
    private String userQuery;
    private Occur occur = Occur.SHOULD;
    private float boost = 0F;
    private boolean isRangeQuery = false;

    public String getFieldName() {
      return fieldName;
    }

    public void setFieldName(String fieldName) {
      this.fieldName = fieldName;
    }

    public String getUserQuery() {
      return userQuery;
    }

    public void setUserQuery(String userQuery) {
      this.userQuery = userQuery;
    }

    public Occur getOccur() {
      return occur;
    }

    public void setOccur(Occur occur) {
      this.occur = occur;
    }

    public float getBoost() {
      return boost;
    }

    public void setBoost(float boost) {
      this.boost = boost;
    }

    public boolean isRangeQuery() {
      return isRangeQuery;
    }

    public void setRangeQuery(boolean isRangeQuery) {
      this.isRangeQuery = isRangeQuery;
    }
  }
}

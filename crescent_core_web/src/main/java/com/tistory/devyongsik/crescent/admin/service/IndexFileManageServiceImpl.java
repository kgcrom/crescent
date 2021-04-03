package com.tistory.devyongsik.crescent.admin.service;

import com.tistory.devyongsik.crescent.admin.entity.CrescentTermStats;
import com.tistory.devyongsik.crescent.admin.entity.HighFreqTermResult;
import com.tistory.devyongsik.crescent.admin.entity.HighFreqTermResult.TermStatsQueue;
import com.tistory.devyongsik.crescent.admin.entity.IndexInfo;
import com.tistory.devyongsik.crescent.collection.entity.Collection;
import com.tistory.devyongsik.crescent.collection.entity.CollectionField;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.*;

@Slf4j
@Service
public class IndexFileManageServiceImpl implements IndexFileManageService {
  //TODO 호출할때마다 계산하는게 아니라, 색인시간 체크해서 보여주도록

  @Override
  public IndexInfo getIndexInfo(Collection selectCollection, String selectTopField) throws IOException {
    IndexInfo indexInfo = new IndexInfo();

    Directory directory = FSDirectory.open(new File(selectCollection.getIndexingDirectory()));
    DirectoryReader directoryReader;
    if (DirectoryReader.indexExists(directory)) {
      directoryReader = DirectoryReader.open(directory);
    } else {
      return null;
    }
    indexInfo.setNumOfDoc(directoryReader.numDocs());
    indexInfo.setHasDel(directoryReader.hasDeletions());
    indexInfo.setIndexVersion(directoryReader.getVersion());
    indexInfo.setSelectCollectionName(selectCollection.getName());
    indexInfo.setIndexName(selectCollection.getIndexingDirectory());

    Map<String, Long> termCountByFieldNameMap = new HashMap<String, Long>();

    long totalTermCount = 0L;
    long totalTermCountByField = 0L;

    List<String> fieldNames = new ArrayList<String>();
    for (CollectionField field : selectCollection.getFields()) {
      fieldNames.add(field.getName());

      totalTermCountByField = directoryReader.getSumTotalTermFreq(field.getName());
      totalTermCount += totalTermCountByField;

      termCountByFieldNameMap.put(field.getName(), totalTermCountByField);
    }

    indexInfo.setFieldNames(fieldNames);
    indexInfo.setNumOfField(fieldNames.size());
    indexInfo.setTermCountByFieldNameMap(termCountByFieldNameMap);
    indexInfo.setTotalTermCount(totalTermCount);

    try {
      HighFreqTermResult highFreqTermResult = getHighFreqTerms(selectCollection, selectTopField);
      TermStatsQueue q = highFreqTermResult.getTermStatsQueue();

      List<CrescentTermStats> crescentTermStatsList = new ArrayList<CrescentTermStats>();

      while (q.size() > 0) {
        CrescentTermStats stats = q.pop();

        crescentTermStatsList.add(stats);
      }

      Collections.sort(crescentTermStatsList, new Comparator<CrescentTermStats>() {

        @Override
        public int compare(CrescentTermStats o1, CrescentTermStats o2) {
          if (o2.getTotalTermFreq() > o1.getTotalTermFreq()) {
            return 1;
          } else if (o2.getTotalTermFreq() < o1.getTotalTermFreq()) {
            return -1;
          } else {
            return 0;
          }
        }
      });

      indexInfo.setCrescentTermStatsList(crescentTermStatsList);

    } catch (Exception e) {
      log.error("Exception in getIndexInfo : ", e);
    }

    return indexInfo;
  }

  private HighFreqTermResult getHighFreqTerms(Collection selectCollection, String selectTopField) throws IOException {

    HighFreqTermResult highFreqTermResult = new HighFreqTermResult();

    HighFreqTermResult.TermStatsQueue termStatsQueue = highFreqTermResult.getTermStatsQueue();
    int i = 0;
    Directory directory = null;
    DirectoryReader directoryReader = null;

    try {
      directory = FSDirectory.open(new File(selectCollection.getIndexingDirectory()));
      directoryReader = DirectoryReader.open(directory);
    } catch (IOException e) {
      throw e;
    } finally {
      directory.close();
      directoryReader.close();
    }

    if (selectTopField != null) {
      Fields fields = MultiFields.getFields(directoryReader);
      if (fields == null) {
        log.error("Index has no fields.....!");
        throw new InvalidParameterException();
      }
      Terms terms = MultiFields.getTerms(directoryReader, selectTopField);
      TermsEnum termsEnum = terms.iterator(TermsEnum.EMPTY);
      String termText;
      CollectionField crescentField = selectCollection.getCrescentFieldByName().get(selectTopField);

      BytesRef ref;
      while ((ref = termsEnum.next()) != null) {
        if ("LONG".equalsIgnoreCase(crescentField.getType())) {
          termText = String.valueOf(NumericUtils.prefixCodedToLong(ref));
        } else if ("INTEGER".equalsIgnoreCase(crescentField.getType())) {
          termText = String.valueOf(NumericUtils.prefixCodedToInt(ref));
        } else {
          termText = ref.utf8ToString();
        }

        termStatsQueue.insertWithOverflow(new CrescentTermStats(selectTopField, termText, termsEnum.docFreq(), termsEnum.totalTermFreq()));
        i++;
      }
    }


    log.info("highFreqTermResult count : {}", i);
    return highFreqTermResult;
  }

}
package org.crescent.admin.service;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.NumericUtils;
import org.crescent.admin.entity.HighFreqTermResult;
import org.crescent.admin.entity.IndexInfo;
import org.crescent.admin.entity.CrescentTermStats;
import org.crescent.collection.entity.Collection;
import org.crescent.collection.entity.CollectionField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IndexFileManageService {
  // TODO 호출할때마다 계산하는게 아니라, 색인시간 체크해서 보여주도록
  private static final Logger logger = LoggerFactory.getLogger(IndexFileManageService.class);

  public IndexInfo getIndexInfo(Collection selectCollection, String selectTopField) throws IOException {
    IndexInfo indexInfo = new IndexInfo();

    Directory directory = FSDirectory.open(Paths.get(selectCollection.getIndexingDirectory()));
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

    Map<String, Long> termCountByFieldNameMap = new HashMap<>();

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
      HighFreqTermResult.TermStatsQueue q = highFreqTermResult.getTermStatsQueue();

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
      logger.error("Exception in getIndexInfo : ", e);
    }

    return indexInfo;
  }

  private HighFreqTermResult getHighFreqTerms(Collection selectCollection, String selectTopField) throws Exception {

    HighFreqTermResult highFreqTermResult = new HighFreqTermResult();

    HighFreqTermResult.TermStatsQueue termStatsQueue = highFreqTermResult.getTermStatsQueue();
    Directory directory = null;
    DirectoryReader directoryReader = null;

    try {
      directory = FSDirectory.open(Paths.get(selectCollection.getIndexingDirectory()));
      directoryReader = DirectoryReader.open(directory);
    } catch (IOException e) {
      throw e;
    } finally {
      directory.close();
      directoryReader.close();
    }

    if (selectTopField != null) {
      try {
        TermStats[] stats = HighFreqTerms.getHighFreqTerms(directoryReader, 10, selectTopField, new HighFreqTerms.DocFreqComparator());
        CollectionField crescentField = selectCollection.getCrescentFieldByName().get(selectTopField);
        String termText;

        for (int i = 0; i < stats.length; i++) {
          if ("LONG".equalsIgnoreCase(crescentField.getType())) {
            termText = String.valueOf(NumericUtils.sortableBytesToInt(stats[i].termtext.bytes, 0));
          } else if ("INTEGER".equalsIgnoreCase(crescentField.getType())) {
            termText = String.valueOf(NumericUtils.sortableBytesToLong(stats[i].termtext.bytes, 0));
          } else {
            termText = stats[i].termtext.utf8ToString();
          }
          termStatsQueue.insertWithOverflow(new CrescentTermStats(selectTopField, termText, stats[0].docFreq, stats[0].totalTermFreq));

        }
      } catch (Exception e) {
        throw e;
      }
    }

    logger.info("highFreqTermResult count : {}", highFreqTermResult.getTermStatsQueue().size());
    return highFreqTermResult;
  }

}

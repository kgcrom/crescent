package org.crescent.admin.service;

import com.google.common.base.Splitter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import org.crescent.collection.entity.Collection;
import org.crescent.collection.entity.CollectionField;
import org.crescent.collection.entity.DefaultSearchField;
import org.crescent.collection.entity.SortField;
import org.crescent.config.CollectionHandler;
import org.crescent.index.analysis.AnalyzerHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CollectionManageService {

  private static final Logger logger = LoggerFactory.getLogger(CollectionManageService.class);
  private final CollectionHandler collectionHandler;

  public CollectionManageService(CollectionHandler collectionHandler) {
    this.collectionHandler = collectionHandler;
  }

  // TODO 기존 코드 참고해서 colleciton 추가/수정/삭제 기능 개발
  // 파일로 저장되어있는 xml file을 수정하는게 좋을지 다른 프로젝트 살펴본 후 진행
  public Collection updateCollectionInfo() {
    String selectedCollectionName = "";
    String indexingModeAnalyzer = "";
    String searchModeAnalyzer = "";
    String indexingModeAnalyzerType = "";
    String searchModeAnalyzerType = "";
    String flushInterval = "";

    Collection selectedCollection = collectionHandler.getCollection(selectedCollectionName);

    List<AnalyzerHolder> analyzerHolderList = new ArrayList<>();
    AnalyzerHolder indexingModeAnalyzerHolder = new AnalyzerHolder();
    indexingModeAnalyzerHolder.setClassName(indexingModeAnalyzer);
    indexingModeAnalyzerHolder.setType(indexingModeAnalyzerType);
    analyzerHolderList.add(indexingModeAnalyzerHolder);

    AnalyzerHolder searchModeAnalyzerHolder = new AnalyzerHolder();
    searchModeAnalyzerHolder.setClassName(searchModeAnalyzer);
    searchModeAnalyzerHolder.setType(searchModeAnalyzerType);
    analyzerHolderList.add(searchModeAnalyzerHolder);

    selectedCollection.setAnalyzers(analyzerHolderList);

    selectedCollection.setFlushInterval(flushInterval);

    List<CollectionField> collectionFields = selectedCollection.getFields();

    //추가되는 필드명을 모은다.
    Enumeration<String> enumeration = null;
    List<String> addFieldNameList = new ArrayList<>();
    while (enumeration.hasMoreElements()) {
      String paramValue = enumeration.nextElement();
      if (paramValue.endsWith("fieldName")) {
        addFieldNameList.add(paramValue.substring(0, paramValue.lastIndexOf("-")));
      }
    }

    logger.debug("add field name list : {}", addFieldNameList);

    for (String fieldName : addFieldNameList) {
      CollectionField crescentField = new CollectionField();
      crescentField.setName(fieldName);

      if (!collectionFields.contains(crescentField)) {
        collectionFields.add(crescentField);
      }
    }

    // TODO 각 필드마다 analyze, index, store, removeHtmlTag Option 존재
    for (CollectionField crescentField : collectionFields) {

      crescentField.setAnalyze(true);
      crescentField.setIndexed(true);
      crescentField.setStored(true);
      crescentField.setRemoveHtmlTag(true);

      // field type과 sort도 존재
      crescentField.setType("STRING");

      //sort field 처리
      if ("on".equals("-sortField")) {
        SortField sortField = new SortField();
        sortField.setSource(crescentField.getName());
        sortField.setDest(crescentField.getName() + "_sort");

        if (!selectedCollection.getSortFields().contains(sortField)) {
          selectedCollection.getSortFields().add(sortField);
        }
      }

      // TODO 해당 collection의 default search field 처리
      if ("on".equals("-defaultSearchField")) {
        DefaultSearchField defaultSearchField = new DefaultSearchField();
        defaultSearchField.setName(crescentField.getName());

        if (!selectedCollection.getDefaultSearchFields().contains(defaultSearchField)) {
          selectedCollection.getDefaultSearchFields().add(defaultSearchField);
        }
      }
    }

    collectionHandler.writeToXML();
    collectionHandler.loadCollection();

    selectedCollection = collectionHandler.getCollection(selectedCollectionName);

    return selectedCollection;
  }

  public Collection addCollectionInfo() {
    String selectedCollectionName = "";

    Collection newCollection = new Collection();
    newCollection.setName(selectedCollectionName);
    newCollection.setIndexingDirectory("indexingDirectory");

//    ("searcherReloadScheduleMin")
    newCollection.setFlushInterval("10");

    String indexingModeAnalyzer = "indexingModeAnalyzer";
    String searchModeAnalyzer = "searchModeAnalyzer";

    String indexingModeAnalyzerType = "indexingModeAnalyzerType";
    String searchModelAnalyzerType = "searchModeAnalyzerType";

    List<AnalyzerHolder> analyzerHolderList = new ArrayList<>();
    AnalyzerHolder indexingModeAnalyzerHolder = new AnalyzerHolder();
    indexingModeAnalyzerHolder.setClassName(indexingModeAnalyzer);
    indexingModeAnalyzerHolder.setType(indexingModeAnalyzerType);
    analyzerHolderList.add(indexingModeAnalyzerHolder);

    AnalyzerHolder searchModeAnalyzerHolder = new AnalyzerHolder();
    searchModeAnalyzerHolder.setClassName(searchModeAnalyzer);
    searchModeAnalyzerHolder.setType(searchModelAnalyzerType);
    analyzerHolderList.add(searchModeAnalyzerHolder);

    //필드들을 걸러낸다.
    @SuppressWarnings("unchecked")
    Enumeration<String> enumeration = null;
    List<String> fieldNameList = new ArrayList<>();
    while (enumeration.hasMoreElements()) {
      String paramName = enumeration.nextElement();

      if (paramName.endsWith("-fieldName")) { //필수값
        String fieldName = Splitter.on("-").split(paramName).iterator().next();
        fieldNameList.add(fieldName);
      }
    }

    List<CollectionField> newCollectionFieldList = new ArrayList<>();
    List<SortField> sortFieldList = new ArrayList<>();
    List<DefaultSearchField> defaultSearchFieldList = new ArrayList<>();

    for (String fieldName : fieldNameList) {
      CollectionField newCollectionField = new CollectionField();

      newCollectionField.setName(fieldName);
      newCollectionField.setAnalyze(
          "on".equals("-analyze") ? true : false);
      newCollectionField.setIndexed(
          "on".equals("-index") ? true : false);
      newCollectionField.setStored(
          "on".equals("-store") ? true : false);

      newCollectionField.setType(
          Optional.ofNullable("-type").orElse("STRING"));

      newCollectionFieldList.add(newCollectionField);

      //sort field 처리
      if ("on".equals("-sortField")) {
        SortField sortField = new SortField();
        sortField.setSource(fieldName);
        sortField.setDest(fieldName + "_sort");

        sortFieldList.add(sortField);
      }

      //default search field 처리
      if ("on".equals("-defaultSearchField")) {
        DefaultSearchField defaultSearchField = new DefaultSearchField();
        defaultSearchField.setName(fieldName);
        defaultSearchFieldList.add(defaultSearchField);
      }
    }

    newCollection.setSortFields(sortFieldList);
    newCollection.setDefaultSearchFields(defaultSearchFieldList);
    newCollection.setFields(newCollectionFieldList);
    newCollection.setAnalyzers(analyzerHolderList);

    collectionHandler.getCollections().add(newCollection);
    collectionHandler.writeToXML();
    collectionHandler.loadCollection();

    return newCollection;
  }

  public void deleteCollectionInfo(String collectionName) {
    List<Collection> collections = collectionHandler.getCollections();

    int targetIndex = -1;

    for (int index = 0; index < collections.size(); index++) {
      if (collectionName.equals(collections.get(index).getName())) {
        targetIndex = index;
        break;
      }
    }

    if (targetIndex > 0) {
      collections.remove(targetIndex);
    } else {
      logger.error("삭제하려는 컬렉션이 존재하지 않습니다. [{}]", collectionName);
      throw new IllegalArgumentException("삭제하려는 컬렉션이 존재하지 않습니다.");
    }

    collectionHandler.writeToXML();
    collectionHandler.loadCollection();
  }
}

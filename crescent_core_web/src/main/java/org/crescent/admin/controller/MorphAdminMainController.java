package org.crescent.admin.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.crescent.admin.entity.MorphResult;
import org.crescent.admin.entity.MorphToken;
import org.crescent.admin.service.MorphService;
import org.crescent.collection.entity.Collection;
import org.crescent.config.CollectionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class MorphAdminMainController {

  private static final Logger logger = LoggerFactory.getLogger(MorphAdminMainController.class);

  private MorphService morphService;
  private CollectionHandler collectionHandler;

  public MorphAdminMainController(MorphService morphService, CollectionHandler collectionHandler) {
    this.morphService = morphService;
    this.collectionHandler = collectionHandler;
  }

  @RequestMapping("/morphMain")
  public ModelAndView morphMain(
      @RequestParam(value = "col_name", required = false) String selectedCollectionName) {

    List<Collection> collections = collectionHandler.getCollections();

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.addObject("selectedCollectionName",
        selectedCollectionName != null ? selectedCollectionName : collections.get(0).getName());

    modelAndView.addObject("collectionList", collections);
    modelAndView.addObject("selectedCollection",
        collectionHandler.getCollection(selectedCollectionName));

    modelAndView.setViewName("/admin/morphMain");

    return modelAndView;
  }

  @GetMapping("/doMorphTestAjax")
  public Map<String, List<MorphResult>> morphTestAjax(
      @RequestParam(value = "keyword") String keyword,
      @RequestParam(value = "collectionName") String collectionName

  ) throws Exception {
    logger.debug("keyword : {}, collectionName : {}", keyword, collectionName);

    List<MorphToken> resultTokenListIndexingMode = morphService.getTokens(keyword, true,
        collectionName);
    List<MorphToken> resultTokenListQueryMode = morphService.getTokens(keyword, false,
        collectionName);

    List<MorphResult> morphIndexingTestResult = new ArrayList<MorphResult>();
    List<MorphResult> morphQueryTestResult = new ArrayList<MorphResult>();

    Map<String, List<MorphResult>> morphTestResultSet = new HashMap<>();

    for (MorphToken token : resultTokenListIndexingMode) {
      MorphResult morphResult = new MorphResult();
      morphResult.setWord(token.getTerm());
      morphResult.setType(token.getType());
      morphResult.setStartOffset(token.getStartOffset());
      morphResult.setEndOffset(token.getEndOffset());

      morphIndexingTestResult.add(morphResult);
    }

    for (MorphToken token : resultTokenListQueryMode) {
      MorphResult morphResult = new MorphResult();
      morphResult.setWord(token.getTerm());
      morphResult.setType(token.getType());
      morphResult.setStartOffset(token.getStartOffset());
      morphResult.setEndOffset(token.getEndOffset());

      morphQueryTestResult.add(morphResult);
    }

    morphTestResultSet.put("indexResult", morphIndexingTestResult);
    morphTestResultSet.put("queryResult", morphQueryTestResult);

    return morphTestResultSet;
  }

  // TODO 사전 생성, 삭제, 변경 기능 구현
}

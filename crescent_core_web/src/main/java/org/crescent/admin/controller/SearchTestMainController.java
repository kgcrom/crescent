package org.crescent.admin.controller;

import java.util.List;
import org.crescent.collection.entity.Collection;
import org.crescent.config.CollectionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class SearchTestMainController {

  private static final Logger logger = LoggerFactory.getLogger(SearchTestMainController.class);
  private final CollectionHandler collectionHandler;

  public SearchTestMainController(CollectionHandler collectionHandler) {
    this.collectionHandler = collectionHandler;
  }

  @RequestMapping("/searchTestMain")
  public ModelAndView searchTestMain(
      @RequestParam(value = "col_name", required = false) String selectedCollectionName) {
    List<Collection> collections = collectionHandler.getCollections();
    String collectionName = selectedCollectionName;
    if (collectionName == null) {
      collectionName = collections.get(0).getName();
    }

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.addObject("selectedCollectionName", collectionName);
    modelAndView.addObject("collectionList", collections);
    modelAndView.addObject("selectedCollection",
        collectionHandler.getCollection(selectedCollectionName));
    modelAndView.setViewName("/admin/searchTestMain");

    logger.debug("search Test main");

    return modelAndView;
  }

}

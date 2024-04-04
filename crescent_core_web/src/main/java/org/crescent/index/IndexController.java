package org.crescent.index;

import org.crescent.config.CollectionHandler;
import org.crescent.index.indexer.CrescentIndexerExecutor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class IndexController {

  public IndexController(CollectionHandler collectionHandler,
      CrescentIndexerExecutor crescentIndexerExecutor) {
  }

  @PutMapping("/update")
  public String updateDocument(@RequestParam(value = "collection_name") String collectionName) {
    // IndexingRequestForm parameter로 받아서 처리
    return "Impletemnt Not yet";
  }
}

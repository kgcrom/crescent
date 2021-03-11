package com.tistory.devyongsik.crescent;

import com.tistory.devyongsik.crescent.collection.entity.CrescentCollection;
import com.tistory.devyongsik.crescent.config.CrescentCollectionHandler;
import com.tistory.devyongsik.crescent.search.searcher.CrescentSearcherManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.SearcherManager;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class SearcherManagerReloader implements SmartLifecycle {

  private List<ScheduledThreadPoolExecutor> execList = new ArrayList<ScheduledThreadPoolExecutor>();
  private boolean isRunning = false;

  private final CrescentSearcherManager crescentSearcherManager;
  private final CrescentCollectionHandler crescentCollectionHandler;
  private final IndexWriterManager indexWriterManager;

  public SearcherManagerReloader(CrescentSearcherManager crescentSearcherManager, CrescentCollectionHandler crescentCollectionHandler, IndexWriterManager indexWriterManager) {
    this.crescentSearcherManager = crescentSearcherManager;
    this.crescentCollectionHandler = crescentCollectionHandler;
    this.indexWriterManager = indexWriterManager;
  }

  private void reloadStart() {

    List<CrescentCollection> crescentCollectionList = crescentCollectionHandler.getCrescentCollections().getCrescentCollections();

    log.info("reloader start.....[{}]", crescentCollectionList);

    for (CrescentCollection collection : crescentCollectionList) {

      ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
      execList.add(exec);

      exec.scheduleWithFixedDelay(new Reloader(collection.getName()), 0, Integer.parseInt(collection.getSearcherReloadScheduleMin()), TimeUnit.MINUTES);
    }
  }

  private void shutdown() {
    for (ScheduledThreadPoolExecutor exec : execList) {
      List<Runnable> rList = exec.shutdownNow();

      log.info("Reloader Shutdown.. {}", rList.toString());
    }

    List<CrescentCollection> crescentCollectionList = crescentCollectionHandler.getCrescentCollections().getCrescentCollections();

    for (CrescentCollection collection : crescentCollectionList) {
      IndexWriter indexWriter = indexWriterManager.getIndexWriter(collection.getName());

      try {
        indexWriter.close();
      } catch (CorruptIndexException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }

      log.error("IndexWriter close....{}", collection.getName());
    }
  }

  @Override
  public void start() {
    reloadStart();

    isRunning = true;
  }

  @Override
  public void stop() {
    shutdown();

    isRunning = false;
  }

  @Override
  public boolean isRunning() {

    return isRunning;
  }

  @Override
  public int getPhase() {
    return 0;
  }

  @Override
  public boolean isAutoStartup() {
    return true;
  }

  @Override
  public void stop(Runnable callback) {
    stop();
    callback.run();
  }

  private class Reloader implements Runnable {

    private String collectionName = null;

    public Reloader(String collectionName) {
      this.collectionName = collectionName;

      log.info("Reloader Start {} ", collectionName);
    }

    @Override
    public void run() {
      SearcherManager searcherManager = crescentSearcherManager.getSearcherManager(collectionName);
      boolean refreshed = false;

      try {

        refreshed = searcherManager.maybeRefresh();

      } catch (IOException e) {
        log.error("Searcher Manager Reloader Error!", e);
      }

      log.info("Searcher Manager Reloaded..{}, {}", collectionName, refreshed);
    }
  }
}

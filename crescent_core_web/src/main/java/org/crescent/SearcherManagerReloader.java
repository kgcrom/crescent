package org.crescent;

import org.crescent.collection.entity.Collection;
import org.crescent.config.CollectionHandler;
import org.crescent.search.searcher.CrescentSearcherManager;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.SearcherManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class SearcherManagerReloader implements SmartLifecycle {

  private static final Logger logger = LoggerFactory.getLogger(SearcherManagerReloader.class);
  private List<ScheduledThreadPoolExecutor> execList = new ArrayList<ScheduledThreadPoolExecutor>();
  private boolean isRunning = false;

  private final CrescentSearcherManager crescentSearcherManager;
  private final CollectionHandler collectionHandler;
  private final IndexWriterManager indexWriterManager;

  public SearcherManagerReloader(CrescentSearcherManager crescentSearcherManager, CollectionHandler collectionHandler, IndexWriterManager indexWriterManager) {
    this.crescentSearcherManager = crescentSearcherManager;
    this.collectionHandler = collectionHandler;
    this.indexWriterManager = indexWriterManager;
  }

  @SuppressWarnings("FutureReturnValueIgnored")
  private void reloadStart() {
    List<Collection> collections = collectionHandler.getCollections();

    logger.info("reloader start.....[{}]", collections);

    for (Collection collection : collections) {
      ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
      execList.add(exec);
      exec.scheduleWithFixedDelay(new Reloader(collection.getName()), 0, Integer.parseInt(collection.getFlushInterval()), TimeUnit.MINUTES);
    }
  }

  private void shutdown() {
    for (ScheduledThreadPoolExecutor exec : execList) {
      List<Runnable> rList = exec.shutdownNow();

      logger.info("Reloader Shutdown.. {}", rList.toString());
    }
    for (Collection collection : collectionHandler.getCollections()) {
      IndexWriter indexWriter = indexWriterManager.getIndexWriter(collection.getName());

      try {
        indexWriter.close();
      } catch (IOException e) {
        logger.error("failed to close index writer: {}", collection.getName());
      }

      logger.info("{} IndexWriter close", collection.getName());
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

    private String collectionName;

    public Reloader(String collectionName) {
      this.collectionName = collectionName;

      logger.info("Reloader Start {} ", collectionName);
    }

    @Override
    public void run() {
      SearcherManager searcherManager = crescentSearcherManager.getSearcherManager(collectionName);
      boolean refreshed = false;

      try {
        refreshed = searcherManager.maybeRefresh();
      } catch (IOException e) {
        logger.error("occur error to reload Searcher Manager", e);
        return;
      }

      logger.info("Searcher Manager Reloaded..{}, {}", collectionName, refreshed);
    }
  }
}

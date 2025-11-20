package edu.internship.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

public class JsonFileProcessor {
  private final ExecutorService executor;
  private final JsonFileParser parser = new JsonFileParser();

  public JsonFileProcessor(int threads) {
    this.executor = Executors.newFixedThreadPool(threads);
  }

  /**
   * Processes a list of JSON files concurrently and aggregates statistics for the specified attribute (e.g., "status").
   *
   * @param files list of JSON file paths
   * @param attribute attribute name to extract and count
   * @return a combined map of attribute values and their total occurrences
   */
  public Map<String, Integer> processFiles(List<Path> files, String attribute) throws InterruptedException {
    List<Future<Map<String, Integer>>> processingFutures = new ArrayList<>(files.size());

    // Submit tasks for parallel processing
    for (Path file : files) {
      processingFutures.add(
          executor.submit(
              () -> {
                try {
                  return parser.processFile(file, attribute);
                } catch (IOException e) {
                  return Collections.emptyMap();
                }
              }));
    }

    Map<String, Integer> global = new HashMap<>();

    // Merge statistics returned by each task
    for (Future<Map<String, Integer>> future : processingFutures) {
      try {
        Map<String, Integer> local = future.get();
        for (Map.Entry<String, Integer> e : local.entrySet()) {
          global.merge(e.getKey(), e.getValue(), Integer::sum);
        }

      } catch (ExecutionException e) {
        System.err.println("File was not processed: " + e.getCause().getMessage());
      }
    }

    shutdownExecutor();
    return global;
  }

  /**
   * Shuts down the executor service.
   *
   * <p>Attempts a normal shutdown and waits up to 1 minute. If threads are still running, forces shutdown.
   */
  private void shutdownExecutor() {
    executor.shutdown();
    try {
      boolean finished = executor.awaitTermination(1, TimeUnit.MINUTES);
      if (!finished) {
        executor.shutdownNow();
        if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
          System.err.println("Executor did not terminate.");
        }
      }
    } catch (InterruptedException e) {
      executor.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}

package com.stc12.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class OccurrenceServiceImpl implements OccurrenceService {

  public static final int N_THREADS = 10;

  @Override public void getOccurrences(String[] sources, String[] words, String res) {
    createDirectoryIfNotExists(res);
    persistOccurrencesIntoResource(sources, words, res);
  }

  private void createDirectoryIfNotExists(String res) {
    try {
      Path path = Paths.get(res);
      if (!Files.exists(path)) {
        Files.createDirectories(path.getParent());
        Files.createFile(path);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void persistOccurrencesIntoResource(String[] sources, String[] words, String res) {
    SentenceApplyService sentenceApplyService = new WordFinderServiceImpl();
    ExecutorService threadPool = Executors.newFixedThreadPool(N_THREADS);
    List<Future<String>> futures = new ArrayList<>();

    long start = System.currentTimeMillis();
    for (String uri : sources) {
      if (uri == null) {
        continue;
      }
      futures.add(CompletableFuture.supplyAsync(() -> sentenceApplyService.apply(uri, words), threadPool));
    }
    try (OutputStream outputStream = new FileOutputStream(res)) {
      for (Future<String> line : futures) {
        outputStream.write(line.get().getBytes());
      }
    } catch (IOException | InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
    threadPool.shutdown();
    long timeExecution = System.currentTimeMillis() - start;
    System.out.println("\rВремя " + timeExecution + " ms (" + readableTime(timeExecution) + ")");
  }


  private String readableTime(long millis) {
    long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
    long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
    long secondPart = seconds - TimeUnit.MINUTES.toSeconds(minutes);
    return String.format("%d min, %d sec",
        minutes,
        secondPart
    );
  }
}

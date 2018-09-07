package com.stc12;

import com.stc12.service.OccurrenceServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

  public static final String RESULT_RESOURCE = "test/result/result.txt";

  public static void main(String[] args) {
    AtomicBoolean isInProgress = new AtomicBoolean(true);
    new Thread(() -> {
      new OccurrenceServiceImpl()
          .getOccurrences(
              getSources(),
              new String[]{"main"},
              RESULT_RESOURCE);
      isInProgress.set(false);
    }).start();
    new ProgressPrinterThread(isInProgress).start();
  }

  private static String[] getSources() {
    List<String> sourceList = new ArrayList<>();
    sourceList.add("test/Tolkien2.txt");
    sourceList.add("http://www.gutenberg.org/files/2600/2600-0.txt");
    sourceList.add("http://www.gutenberg.org/ebooks/817.txt.utf-8");
    sourceList.add("http://www.gutenberg.org/ebooks/10.txt.utf-8");
    for (int i = 0; i < 100; i++) {
      sourceList.add("test/WarAndPeace_23Mb.txt");
    }
    final String[] sources = new String[sourceList.size()];
    sourceList.toArray(sources);
    return sources;
  }

}

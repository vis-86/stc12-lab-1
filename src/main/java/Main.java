import org.apache.log4j.Logger;
import services.OccurrenceServiceException;
import services.OccurrenceServiceImpl;
import services.WordFinderServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

  final static Logger LOGGER = Logger.getLogger(WordFinderServiceImpl.class);

  public static final int N_THREADS = 10;
  public static final String RESULT_RESOURCE = "test/result/result.txt";

  public static void main(String[] args) {
    LOGGER.info("App is started");
    AtomicBoolean isInProgress = new AtomicBoolean(true);
    new Thread(() -> {
      try {
        new OccurrenceServiceImpl(new WordFinderServiceImpl(), Executors.newFixedThreadPool(N_THREADS))
            .getOccurrences(
                getSources(),
                new String[]{"main", "then", "ok"},
                RESULT_RESOURCE);
      } catch (OccurrenceServiceException e) {
        e.printStackTrace();
      }
      isInProgress.set(false);
    }).start();

    if (!LOGGER.isDebugEnabled()) {
      new ProgressPrinterThread(isInProgress).start();
    }
  }

  private static String[] getSources() {
    List<String> sourceList = new ArrayList<>();
    sourceList.add("http://www.gutenberg.org/files/2600/2600-0.txt");
    /*sourceList.add("http://www.gutenberg.org/ebooks/817.txt.utf-8");
    sourceList.add("http://www.gutenberg.org/ebooks/10.txt.utf-8");
    for (int i = 0; i < 100; i++) {
      sourceList.add("test/WarAndPeace_23Mb.txt");
    }*/
    final String[] sources = new String[sourceList.size()];
    sourceList.toArray(sources);
    return sources;
  }

}

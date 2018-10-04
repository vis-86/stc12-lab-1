package services;

import org.apache.log4j.Logger;

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
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class OccurrenceServiceImpl implements OccurrenceService {

  final static Logger LOGGER = Logger.getLogger(OccurrenceServiceImpl.class);
  private SentenceApplyService sentenceApplyService;
  private ExecutorService threadPool;

  public OccurrenceServiceImpl(SentenceApplyService sentenceApplyService, ExecutorService threadPool) {
    this.sentenceApplyService = sentenceApplyService;
    this.threadPool = threadPool;
  }

  /**
   * Get occurrences from source by words, and store founded in resource
   * @param sources Places where we will search words (sources may be path to file or url)
   * @param words Words
   * @param res Resource in which we save the sentences found
   * @throws OccurrenceServiceException
   */
  @Override public void getOccurrences(String[] sources, String[] words, String res) throws OccurrenceServiceException {
    if (sources == null || sources.length == 0) {
      throw new OccurrenceServiceException("Sources can not be empty");
    }
    if (words == null || words.length == 0) {
      throw new OccurrenceServiceException("Searched words can not be empty");
    }
    if (res == null || res.isEmpty()) {
      throw new OccurrenceServiceException("Resource can not be empty");
    }
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
      LOGGER.error(e.getMessage());
    }
  }

  private void persistOccurrencesIntoResource(String[] sources, String[] words, String res) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("\n\n==== Params ==== \nSource size: " + sources.length
          + "\nResult file path: " + res
          + "\nSearching words: " + String.join(",", words) + "\n"
          + "==== End Params ====\n"
      );
    }
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
        outputStream.write((line.get() + "\n").getBytes());
      }
    } catch (IOException | InterruptedException | ExecutionException e) {
      LOGGER.error(e.getMessage());
    }
    threadPool.shutdown();
    long timeExecution = System.currentTimeMillis() - start;
    LOGGER.info("\rВремя " + timeExecution + " ms (" + readableTime(timeExecution) + ")");
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

package services;

import exceptions.OccurrenceServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class OccurrenceServiceImplTest {

  private OccurrenceService occurrenceService;
  private SentenceApplyService applyServiceMock;
  private ExecutorService executorServiceMock;

  private String res;

  @BeforeEach
  void setUp() {
    applyServiceMock = Mockito.mock(SentenceApplyService.class);
    executorServiceMock = Executors.newFixedThreadPool(1);
    occurrenceService = new OccurrenceServiceImpl(applyServiceMock, executorServiceMock);
    res = "test/result/result.txt";
  }

  @Test
  void testCorrectParamsAndResult() throws IOException {
    File file = new File(res);
    String text = "text";
    Mockito.when(applyServiceMock.apply(Mockito.any(), Mockito.any())).thenReturn(text);
    Assertions.assertDoesNotThrow(() -> {
      occurrenceService.getOccurrences(
          new String[]{"source1", "source2"},
          new String[]{"word1", "word2"},
          res
      );
    });

    Assertions.assertTrue(file.exists(), res + " not found");

    BufferedReader reader = new BufferedReader(new FileReader(file));
    StringBuilder content = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      content.append(line);
    }
    Assertions.assertEquals(text + text, content.toString(), "incorrect result");
  }

  @Test
  void testWrongParams() {
    Assertions.assertThrows(OccurrenceServiceException.class, () -> {
      occurrenceService.getOccurrences(
          null,
          null,
          res
      );
    });
    Assertions.assertThrows(OccurrenceServiceException.class, () -> {
      occurrenceService.getOccurrences(
          new String[]{},
          null,
          res
      );
    });
  }
}
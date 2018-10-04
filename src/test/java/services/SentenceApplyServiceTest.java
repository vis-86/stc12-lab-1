package services;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;

class SentenceApplyServiceTest {
  private static URL resource;
  private WordFinderServiceImpl sentenceApplyService;

  @BeforeAll
  static void setUpResources() {
    resource = SentenceApplyServiceTest.class.getClassLoader().getResource("text-source.txt");
  }

  @BeforeEach
  void setUp() {
    sentenceApplyService = new WordFinderServiceImpl();
  }

  @Test
  void testCorrectResult() throws Exception {
    if (resource == null) {
      throw new Exception("Can not find resource");
    }
    String result = sentenceApplyService.apply(resource.getPath(), new String[]{"malorum", "sit"});
    Assertions.assertNotNull(result, "result is null");
    Assertions.assertTrue(result.length() > 0, "empty result");
  }

  @Test
  void testEmptyResult() throws Exception {
    if (resource == null) {
      throw new Exception("Can not find resource");
    }
    String empty = sentenceApplyService.apply(resource.getPath(), new String[]{});
    Assertions.assertEquals(0, empty.length());
  }

  @Test
  void testNullParams() throws Exception {
    if (resource == null) {
      throw new Exception("Can not find resource");
    }
    Assertions.assertThrows(IllegalArgumentException.class,
        () -> sentenceApplyService.apply(null, new String[]{}));
  }
}
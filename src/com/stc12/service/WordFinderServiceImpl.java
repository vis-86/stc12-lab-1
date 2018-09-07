package com.stc12.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WordFinderServiceImpl implements SentenceApplyService {

  @Override public String apply(String uri, String[] searchWord) {
    return getFoundSentence(uri, searchWord);
  }

  private String getFoundSentence(String uri, String[] searchWord) {
    StringBuilder result = new StringBuilder();
    StringBuffer buffer = new StringBuffer();
    boolean isUrl = uri.trim().indexOf("http") == 0 || uri.trim().indexOf("ftp") == 0;
    try (InputStream fis = isUrl ? new URL(uri).openStream() : new FileInputStream(uri);
        InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
        Reader in = new BufferedReader(isr)) {
      int ch;
      while ((ch = in.read()) > -1) {
        String ch1 = String.valueOf((char) ch);
        buffer.append(ch1);
        if (containEndOfSentence(ch1)) {
          if (searchWord(buffer, searchWord)) {
            result.append(normalize(buffer.toString()));
            result.append("\n");
          }
          clearBuffer(buffer);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return result.toString();
  }

  private String normalize(String toString) {
    return toString.trim().replaceAll("\n\r|\n|\t|\r", " ");
  }

  private void clearBuffer(StringBuffer stringBuffer) {
    stringBuffer.delete(0, stringBuffer.length());
  }

  private boolean containEndOfSentence(String source) {
    return source.contains(".") || source.contains("!") || source.contains("?");
  }

  private boolean searchWord(StringBuffer text, String[] words) {
    for (String word : words) {
      if (text.indexOf(" " + word.trim() + " ") > -1) {
        return true;
      }
    }
    return false;
  }
}

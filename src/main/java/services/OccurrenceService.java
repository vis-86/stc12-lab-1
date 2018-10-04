package services;

import exceptions.OccurrenceServiceException;

public interface OccurrenceService {
  void getOccurrences(String[] sources, String[] words, String res) throws OccurrenceServiceException;
}

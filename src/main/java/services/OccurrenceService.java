package services;

public interface OccurrenceService {
  void getOccurrences(String[] sources, String[] words, String res) throws OccurrenceServiceException;
}

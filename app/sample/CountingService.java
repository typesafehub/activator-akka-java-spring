package sample;

import javax.inject.Named;

/**
 * A simple service that can increment a number.
 */
@Named("CountingService")
public class CountingService {
  /**
   * Increment the given number by one.
   */
  public int increment(int count) {
    return count + 1;
  }
}

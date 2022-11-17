package org.rle.ucms.utility;

public class RandomNumbers {

  private RandomNumbers() {}

  // returns a random integer number between min (inclusive) and max (inclusive)
  public static int randomNumber(int min, int max) {
    return (int) (Math.random() * ((max - min) + 1)) + min;
  }

  // returns a random integer number between min (inclusive) and max (inclusive) which is not the excludedValue
  public static int randomNumberExcludingOne(
    int min,
    int max,
    int excludedValue
  ) {
    int randomNumber;
    do {
      randomNumber = (int) (Math.random() * ((max - min) + 1)) + min;
    } while (randomNumber == excludedValue);

    return randomNumber;
  }
}

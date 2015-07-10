package org.apache.geode;

import com.gemstone.gemfire.CancelCriterion;
import com.gemstone.gemfire.internal.LocalStatisticsFactory;

public class GeodeStatisticsExample {

  public static void main(String[] args) {
    startStatisticsArchiver();
    waitForProgramToBeTerminated();
  }

  private static void waitForProgramToBeTerminated() {
    Object lock = new Object();
    try {
      synchronized (lock) {
        lock.wait();
      }

    } catch (InterruptedException e) {
      System.err.println("We were interrupted");
    }
  }

  private static void startStatisticsArchiver() {
    LocalStatisticsFactory factory = new LocalStatisticsFactory(createStopper());

  }

  private static CancelCriterion createStopper() {
    return new CancelCriterion() {
      @Override
      public String cancelInProgress() {
        String weNeverCancelStatisticsHarvesting = null;
        return weNeverCancelStatisticsHarvesting;
      }

      @Override
      public RuntimeException generateCancelledException(Throwable throwable) {
        return noSpecialExceptionTypeIsNecessary(throwable);
      }

      private RuntimeException noSpecialExceptionTypeIsNecessary(Throwable throwable) {
        return new RuntimeException(throwable);
      }
    };
  }
}

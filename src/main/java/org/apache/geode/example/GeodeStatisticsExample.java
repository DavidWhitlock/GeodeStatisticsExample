package org.apache.geode.example;

import org.apache.geode.*;
import org.apache.geode.internal.statistics.LocalStatisticsFactory;
import org.apache.geode.internal.statistics.StatisticsTypeFactoryImpl;

import java.util.Random;

public class GeodeStatisticsExample {

  public static void main(String[] args) {
    StatisticsFactory statsFactory = startStatisticsArchiver();
    createExampleStatistics(statsFactory);
    waitForProgramToBeTerminated();
  }

  private static void createExampleStatistics(StatisticsFactory statsFactory) {
    StatisticsTypeFactory typeFactory = StatisticsTypeFactoryImpl.singleton();
    StatisticsType type = typeFactory.createType("ExampleStatistics", "Example Statistics",
      new StatisticDescriptor[]{
        typeFactory.createIntCounter("sheepIveCounted", "How many sheep have I counted?", "sheep")
      });
    int sheepIveCountedId = type.nameToId("sheepIveCounted");

    Statistics stats = statsFactory.createAtomicStatistics(type, "MyFirstStatistics");
    updateExampleStatisticsInBackgroundThread(sheepIveCountedId, stats);
  }

  private static void updateExampleStatisticsInBackgroundThread(int sheepIveCountedId, Statistics stats) {
    new Thread(() -> {
      Random random = new Random();
      while (true) {
        int durationInMillis = random.nextInt(1000);
        try {
          Thread.sleep(durationInMillis);

        } catch (InterruptedException e) {
          return;
        }

        stats.incInt(sheepIveCountedId, 1);
      }
    }).start();
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

  private static LocalStatisticsFactory startStatisticsArchiver() {
    LocalStatisticsFactory factory = new LocalStatisticsFactory(createStopper());
    return factory;

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

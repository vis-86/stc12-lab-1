package com.stc12;

import java.util.concurrent.atomic.AtomicBoolean;

class ProgressPrinterThread extends Thread {
  private final AtomicBoolean atomicBoolean;

  public ProgressPrinterThread(AtomicBoolean atomicBoolean) {
    this.atomicBoolean = atomicBoolean;
  }

  @Override public void run() {
    char[] animationChars = new char[]{'|', '/', '-', '\\'};
    int i = 1;
    long start = System.currentTimeMillis();
    while (atomicBoolean.get()) {
      try {
        System.out.print(("\rSearching: " + animationChars[i % 4]));
        i++;
        Thread.sleep(300);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    System.out.println("\nDone!"); ;
  }
}

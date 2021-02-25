package art.ch8;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchTest {
    private static CountDownLatch c = new CountDownLatch(5);
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(finalI);
                    try {
                        Thread.sleep(20000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        c.countDown();
                    }
                }
            }).start();
        }

        c.await();
        System.out.println("main ending");
    }
}

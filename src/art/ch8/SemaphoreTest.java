package art.ch8;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class SemaphoreTest {
    private static final int THREAD_COUNT = 30;
    private static Semaphore semaphore = new Semaphore(10);
    private static ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);

    public static void main(String[] args){
        for (int i = 0; i < THREAD_COUNT; i++) {
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        semaphore.acquire();
                        System.out.println(Thread.currentThread().getName()+"get connection! saving data");
                        Thread.sleep(1000);
//                        semaphore.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    finally {
                        semaphore.release();
                    }
                    System.out.println(Thread.currentThread().getName()+"exit!");
                }
            });
        }
        pool.shutdown();
        System.out.println("main end");
    }
}

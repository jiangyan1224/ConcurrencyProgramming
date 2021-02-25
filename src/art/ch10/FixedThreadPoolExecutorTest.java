package art.ch10;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class FixedThreadPoolExecutorTest {
    private static final ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
    public static void main(String[] args) throws InterruptedException {
        pool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return null;
            }
        });
        pool.prestartAllCoreThreads();
        Thread.currentThread().sleep(3000);
//        System.out.println(pool.getActiveCount());//返回正在执行任务的线程的大概数目
        System.out.println(pool.getQueue().size());
        pool.execute(new Runnable() {
            @Override
            public void run() {

            }
        });
        System.out.println(pool.getQueue().size());
        Thread.currentThread().sleep(3000);
        pool.shutdown();
    }
}

package art.ch8;

import java.util.concurrent.*;

public class ExchangerTest {
    private static final Exchanger<String> exchanger = new Exchanger<>();
    private static final ExecutorService pool = Executors.newFixedThreadPool(2);

    private static Object object = new Object();
    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor pool1 = new ThreadPoolExecutor(2,4,1000,
                TimeUnit.SECONDS,new ArrayBlockingQueue<>(10));
        pool1.submit(new Runnable() {
            @Override
            public void run() {
                synchronized (object){
                    String A = "money A";
                    try {
//                    exchanger.exchange(A);
                        object.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        System.out.println("Thread1 been interrupted");
                    }
                }

            }
        });
//        pool.submit(new Runnable() {
//            @Override
//            public void run() {
//                String B = "money B";
//                try {
//                    String A = exchanger.exchange(B);
//                    System.out.println("A input : "+ A + ", B input : "+ B + "," + A.equals(B));
//                    //A input : money A, B input : money B,false
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
        pool1.prestartCoreThread();
        System.out.println(pool1.getQueue().size());
        Thread.currentThread().sleep(4000);
//        pool1.shutdown();//interrupt的是所有正在等待任务的线程
//        System.out.println(pool1.getActiveCount());
        System.out.println(pool1.getQueue().size());
        pool1.submit(new Runnable() {//这个任务提交之后，应该会把正在等待任务的那个线程也算进运行的线程，所以会进入到阻塞队列
            @Override
            public void run() {
                String B = "money B";
                System.out.println(Thread.currentThread().getName());

            }
        });
        System.out.println(pool1.getQueue().size());
//        pool1.shutdownNow();//interrupt的是所有线程
        pool1.shutdown();
        System.out.println(pool1.getQueue().size());
        //线程池中运行的线程，是包括正在等待任务的线程的
    }


}

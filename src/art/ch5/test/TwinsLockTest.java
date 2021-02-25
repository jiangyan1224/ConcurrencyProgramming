package art.ch5.test;

import art.ch5.TwinsLock;

public class TwinsLockTest {
    public static void test() throws InterruptedException {
        TwinsLock lock = new TwinsLock();
        class Worker extends Thread{
            @Override
            public void run() {
                while (true){
                    lock.lock();
                    try{
                        Thread.sleep(1000);
                        System.out.println(Thread.currentThread().getName());
                        Thread.sleep(1000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        lock.unlock();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        for (int i = 0; i < 10; i++) {
            Worker worker = new Worker();
//            worker.setDaemon(true);//设置为守护线程
            worker.start();
        }
//        for (int i = 0; i < 10; i++) {
//            Thread.sleep(3000);
//            System.out.println();
//        }
    }

    //如果当前JVM中只剩下守护线程，JVM可以直接退出
    public static void main(String[] args) throws InterruptedException {
        test();
    }
}

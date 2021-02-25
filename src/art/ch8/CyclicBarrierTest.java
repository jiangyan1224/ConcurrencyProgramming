package art.ch8;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierTest {
    private static CyclicBarrier cyclicBarrier = new CyclicBarrier(2, new A());
    static class A implements Runnable{

        @Override
        public void run() {
            System.out.println("A run()");
        }
    }
    public static void main(String[] args){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    cyclicBarrier.reset();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
                System.out.println("thread1 ending");
            }
        });
        thread.start();
        thread.interrupt();
        try {
            cyclicBarrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    cyclicBarrier.await();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                    cyclicBarrier.reset();
//                } catch (BrokenBarrierException e) {
//                    e.printStackTrace();
//                    cyclicBarrier.reset();
//                }
//                System.out.println("thread2 ending");
//            }
//        }).start();

//        System.out.println("main ending");
        System.out.println(cyclicBarrier.isBroken());
    }

}

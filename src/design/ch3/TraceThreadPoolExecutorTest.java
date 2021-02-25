package design.ch3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class TraceThreadPoolExecutorTest {
    static class DivTask implements Runnable{
        private int a,b;
        public DivTask(int a, int b){this.a=a;this.b=b;}

        @Override
        public void run() {
            System.out.println(a/b);
        }
    }
    public static void main(String[] args){
        ExecutorService es = new TraceThreadPoolExecutor(0, Integer.MAX_VALUE, 0L,
                TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>());

        for (int i = 0; i < 5; i++) {
            es.submit(new DivTask(100, i));
        }
    }
}

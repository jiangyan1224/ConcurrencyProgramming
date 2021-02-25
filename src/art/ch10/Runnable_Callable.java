package art.ch10;

import java.util.concurrent.*;

public class Runnable_Callable {
    private static final ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String restr = "23";
        Runnable r = new Runnable() {
            @Override
            public void run() {
//                int res = 3/0;
            }
        };
        Callable c = new Callable() {
            @Override
            public String call() throws Exception {
                return "callable";
            }
        };
        Callable r2c = Executors.callable(r,"r2c");
        Future future = pool.submit(r,"123");
        System.out.println(future.get());
        pool.shutdown();
    }
}

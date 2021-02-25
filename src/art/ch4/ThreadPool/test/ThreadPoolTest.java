package art.ch4.ThreadPool.test;


import art.ch4.ThreadPool.DefaultThreadPool;
import art.ch4.ThreadPool.Job;
import art.ch4.ThreadPool.ThreadPool;

import java.util.concurrent.TimeUnit;

    public class ThreadPoolTest {
        public static void main(String[] args) {
            ThreadPool pool = new DefaultThreadPool();
            pool.execute(new NewJob());
            pool.execute(new NewJob());
            pool.execute(new NewJob());
            pool.execute(new NewJob());
            pool.execute(new NewJob());
            pool.execute(new NewJob());
            pool.execute(new NewJob());
            pool.execute(new NewJob());
        }
    }
    class NewJob implements Job {

        @Override
        public void run() {
            try {
                System.out.println("sleeping");
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

package art.ch4.connPool;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionPoolTest {
    static ConnectionPool connectionPool = new ConnectionPool(10);
    static CountDownLatch start = new CountDownLatch(1);//让所有线程一起启动
    static CountDownLatch end;

    public static void main(String[] args) throws InterruptedException {
        int threadCount = 50;
        end = new CountDownLatch(threadCount);
        int count = 10;//每个线程获取和释放连接count次
        AtomicInteger got = new AtomicInteger(), notGot = new AtomicInteger();//got和notGot是所有线程一起用的
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(new ConnectionRunner(count, got, notGot), "ConnectionRunnerThread");
            thread.start();
        }
        start.countDown();
        end.await();
        System.out.println("total invoke:"+threadCount * count);
        System.out.println("total got:"+got);
        System.out.println("total notGot:"+notGot);
        System.out.println("ratio:"+((float)got.intValue()/(threadCount * count)));
    }
    static class ConnectionRunner implements Runnable{
        private int count;
        private AtomicInteger got;
        private AtomicInteger notGot;
        ConnectionRunner(int count, AtomicInteger got, AtomicInteger notGot){
            this.count = count;
            this.got = got;
            this.notGot = notGot;
        }
        @Override
        public void run(){//进行count次获取和释放连接
            try {
                start.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Connection conn = null;
            while(count > 0){
                try {
                    conn = connectionPool.getConnection(1000);
                    if (conn != null){
                        try{
                            conn.createStatement();
                            conn.commit();
                        } finally {
                            connectionPool.releaseConnection(conn);
                            got.incrementAndGet();
                        }
                    } else{
                        notGot.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    count--;
                }
            }
            end.countDown();
        }
    }


}

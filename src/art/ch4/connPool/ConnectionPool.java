package art.ch4.connPool;

import java.sql.Connection;
import java.util.LinkedList;

/**数据库连接池实现*/
public class ConnectionPool {
    private final LinkedList<Connection> pool = new LinkedList<>();

    public ConnectionPool(int initialSize){
        if(initialSize > 0 ){
            for (int i = 0; i < initialSize; i++) {
                pool.add(ConnectionDriver.createConnection());
            }
        }
    }

    /**等待mills毫秒超时，获取连接*/
    public Connection getConnection(long mills) throws InterruptedException {
        synchronized (pool){
            if(mills <= 0){
                if(pool.isEmpty()){
                    return null;
                }
                else return pool.removeFirst();
            }else{
                Connection conn = null;
                long end = System.currentTimeMillis() + mills;
                long remaining = mills;
                while(pool.isEmpty() && remaining > 0){
                    pool.wait(remaining);
                    remaining = end - System.currentTimeMillis();
                }
                if(!pool.isEmpty()) conn = pool.removeFirst();
                return conn;
            }
        }
    }
    public void releaseConnection(Connection connection){
        if(connection == null) return;
        synchronized (pool){
            pool.addLast(connection);
            pool.notifyAll();//释放连接之后通知所有等待中的线程
        }
    }
}

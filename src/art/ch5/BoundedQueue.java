package art.ch5;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用Lock + Condition + 循环队列 实现一个线程安全的阻塞队列
 * */
public class BoundedQueue<T> {
    private Object[] items;
    private ReentrantLock lock = new ReentrantLock();
    private Condition notFull  =lock.newCondition();
    private Condition notEmpty = lock.newCondition();
    private int start,end;//标志序列的首尾，数组实现循环队列

    BoundedQueue(int initialSize){
        items = new Object[initialSize + 1];
        start = 0;
        end = 0;
    }
    public int getSize(){
        try{
            lock.lock();
            return (end - start + items.length) % items.length;
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }finally {
            lock.unlock();
        }
    }
    public boolean put(T object){
        try {
            lock.lock();
            while (items.length == (getSize() + 1)){
                notEmpty.await();
            }
            end = (end + 1) % items.length;
            items[end] = object;
            notFull.signal();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            lock.unlock();
        }
    }
    public T take(){
        try {
            lock.lock();
            T res = null;
            while (0 == getSize()){
                notFull.await();
            }
            res = (T) items[start];
            start = (start + 1) % items.length;
            notEmpty.signal();
            return res;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            lock.unlock();
        }
    }
}

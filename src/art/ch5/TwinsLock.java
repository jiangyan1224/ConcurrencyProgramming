package art.ch5;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class TwinsLock implements Lock {
    static class Sync extends AbstractQueuedSynchronizer{
        Sync(int count){
            if (count <= 0) throw new IllegalArgumentException("count must >=0");
            setState(count);
        }
        protected int tryAcquireShared(int acquires) {
            for (;;){
//                if (hasQueuedPredecessors()){
//                    return -1;
//                }
                int available = getState();
                int remaining = available - acquires;
                if (remaining < 0 || compareAndSetState(available, remaining)){
                    return remaining;
                }
            }
        }
        protected final boolean tryReleaseShared(int releases) {
            for (;;){
                int current = getState();
                int next = current +releases;
                if (next < current)
                    throw new Error("overflow");
                if(compareAndSetState(current, next))
                    return true;
            }
        }

    }
    private static Sync sync = new Sync(2);
    @Override
    public void lock() {
        sync.acquireShared(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        sync.releaseShared(1);
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}

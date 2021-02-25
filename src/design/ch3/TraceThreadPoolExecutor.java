package design.ch3;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TraceThreadPoolExecutor extends ThreadPoolExecutor {
    public TraceThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                   TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    //因为传入的参数可能要用在new Runnable匿名内部类中，匿名内部类如果要用局部变量，必须是final变量
    //只要匿名内部类用了局部变量，就会在自己内部存一份副本，相当于多一个属性（拷贝引用/基本类型）
    private Runnable wrap(final Runnable runnable, final Exception clientStack, final String clientName){
//        runnable = "";//报错
        return new Runnable() {
            @Override
            public void run() {
                try{
                    runnable.run();
                }catch (Exception e){
                    clientStack.printStackTrace();
                    throw e;
                }
            }
        };
    }

    @Override
    public void execute(Runnable command) {
        super.execute(wrap(command, new Exception("Client stack here!!!"), Thread.currentThread().getName()));
    }

    public Future<?> submit(Runnable command) {
        return super.submit(wrap(command, new Exception("Client stack here!!!"), Thread.currentThread().getName()));
    }
}

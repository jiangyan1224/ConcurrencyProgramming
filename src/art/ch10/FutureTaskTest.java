package art.ch10;

import java.util.concurrent.*;

public class FutureTaskTest {
    private ConcurrentHashMap<String, Future<String>> taskCache
            = new ConcurrentHashMap<>();
    public String executeFuture(String taskName){
        if (taskName == null) return null;
        while(true){//这个死循环，可以让在只有一个线程a成功执行任务，其他线程都在get处阻塞时，如果任务的执行抛出了异常，
            //a可以删除对应键值对，新建Callable，再次尝试执行
            Future future = taskCache.get(taskName);
            if (future == null){
                Callable c = new Callable() {
                    @Override
                    public String call() throws Exception {
                        return taskName;
                    }
                };
                FutureTask<String> task = new FutureTask<String>(c);
                future = taskCache.putIfAbsent(taskName, task);
                if (future == null){
                    future = task;//防止从这个if出去之后，到future.get出现空指针异常
                    task.run();
                }
            }
            try {
                return (String) future.get();
            } catch (InterruptedException | ExecutionException e) {
                //remove(key)和remove(key,value)区别在于，一个是只要匹配key就删除，另一个是key和value都匹配才删除
                //在这里，我觉得两个remove都可以
                taskCache.remove(taskName, future);
                e.printStackTrace();
            }
        }
    }
}

package art.ch4.ThreadPool;

public interface ThreadPool<Job extends Runnable> {
    //执行一个Job，这个Job需要实现Runnable
    void execute(Job job);
    void shutdown();
    //增加工作线程
    void addWorkers(int num);
    //减少工作线程
    void removeWorker(int num);
    int getJobSize();
}

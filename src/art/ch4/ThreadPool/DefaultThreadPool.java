package art.ch4.ThreadPool;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultThreadPool<Job extends Runnable> implements ThreadPool<Job>{
    //线程池最大工作线程数
    private static final int MAX_WORKER_NUMBERS = 10;
    //线程池最小工作线程数
    private static final int MIN_WORKER_NUMBERS = 10;
    //线程池默认初始大小
    private static final int DEFAULT_WORKER_NUMBERS = 5;
    //线程池的工作线程列表
    private final List<Worker> workerList = Collections.synchronizedList(new LinkedList<>());
    //线程池的工作/任务列表
    private final LinkedList<Job> jobList = new LinkedList<Job>();
    //线程池还活着的工作线程数量
    private AtomicInteger workerNum = new AtomicInteger(DEFAULT_WORKER_NUMBERS);
    //线程池等待队列允许的最大等待任务数
    private static final int MAX_JOB_NUMBERS = Integer.MAX_VALUE;
    //线程池线程编号
    private static volatile AtomicInteger threadNum = new AtomicInteger();

    public DefaultThreadPool(){
        workerNum.set(DEFAULT_WORKER_NUMBERS);
        initializeWorkers(DEFAULT_WORKER_NUMBERS);
    }
    public DefaultThreadPool(int initialSize){
        int num = initialSize < MIN_WORKER_NUMBERS ? MIN_WORKER_NUMBERS :
                Math.min(initialSize, MAX_WORKER_NUMBERS);
        workerNum.set(num);
        initializeWorkers(num);
    }
    class Worker implements Runnable{
        //方便让线程退出run方法，按理说应该只有线程池本身可以设置，而且每个线程内部都有一个，不知道为啥用private volatile
        private volatile boolean running = true;
        @Override
        public void run() {
            while (running){
                Job job = null;
                synchronized (jobList){
                    while(jobList.isEmpty()){
                        try {
                            jobList.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    job = jobList.pollFirst();
                }
                if(job != null){
                    try {
                        job.run();
                    }catch (Exception e){
                        System.out.println("the job submitted Exception");
                        e.printStackTrace();
                    }
                }
            }
        }
        public void shutdown(){
            running = false;
        }
    }
    private void initializeWorkers(int num){
        for (int i = 0; i < num; i++) {
            Worker worker = new Worker();
            workerList.add(worker);
            Thread thread = new Thread(worker, "ThreadPool-Thread"+threadNum.getAndIncrement());
            thread.start();
        }
    }


    @Override
    /**
     *
     * */
    public void execute(Job job) {
        if (job == null) return;
        synchronized (jobList){
            jobList.addLast(job);
            jobList.notify();
        }

    }

    @Override
    //停止所有worker
    public void shutdown() {
        synchronized (jobList){//唤醒那些因为jobList为空wait的线程
            jobList.notifyAll();
        }
        for (Worker worker : workerList) {
            worker.shutdown();
        }
    }

    @Override
    //新建worker，放在最后
    public void addWorkers(int num) {
        synchronized (workerList){
            num = Math.min(MAX_WORKER_NUMBERS - workerNum.get() , num);
            initializeWorkers(num);
            this.workerNum.addAndGet(num);
        }
    }

    @Override
    /**将指定数量的worker remove并shutdown
     * 这里实际上只需要从workerList中remove一定数量的worker，具体是不是0~num号元素无所谓
     * */
    public void removeWorker(int num) {
        synchronized (workerList){
//            for (int i = 0; i < num; i++) {
//                workerList.remove(i);//因为每次读取i都是从当前list的最左边开始数，有可能下标溢出
//            }
            int count = 0;
            while(count < num) {
                Worker worker = workerList.get(count);
                if (workerList.remove(worker)){
                    worker.shutdown();
                    count++;
                }
            }
            this.workerNum.addAndGet(-count);
        }
    }

    @Override
    public int getJobSize() {
        synchronized (jobList){
            return jobList.size();
        }
    }
}

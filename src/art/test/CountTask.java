package art.test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class CountTask extends RecursiveTask<Long> {
    private long start;
    private long end;
    private static final int THRESHOLD = 10;
    CountTask(long start, long end){
        this.start = start;
        this.end = end;
    }

    @Override
    //编写主要执行逻辑
    protected Long compute() {
        long sum = 0;
        boolean canCompute = (start - end) <= THRESHOLD;
        if (canCompute){
            for (long i = start; i < end; i++) {
                sum += i;
            }
            return sum;
        }
        //分割任务,分成2个小任务
        else {
            long middle = (start + end)/2;
            CountTask leftTask = new CountTask(start, middle);
            CountTask rightTask = new CountTask(middle + 1, end);
            invokeAll(leftTask, rightTask);
            sum = leftTask.join() + rightTask.join();
            return sum;

        }
    }

    public static void main(String[] args){
        ForkJoinPool pool = new ForkJoinPool();
        ForkJoinTask<Long> res = pool.submit(new CountTask(0, 102));//[start,end)
        Long sum = 0L;
        try {
            sum = res.get();
            System.out.println(sum);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }
}

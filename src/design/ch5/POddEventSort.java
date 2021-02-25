package design.ch5;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//并行奇偶阶段排序
public class POddEventSort {
    private static ExecutorService pool = Executors.newCachedThreadPool();
    private static int exchFlag = 1;

    static class OddEventSortTask implements Runnable{
        private CountDownLatch latch;
        private int i;
        private int[] arr;
        OddEventSortTask(CountDownLatch latch, int i, int[] arr){this.latch = latch;this.i = i;this.arr = arr;}
        @Override
        public void run() {
            if (arr[i] > arr[i + 1]){
                int tmp = arr[i];
                arr[i] = arr[i + 1];
                arr[i + 1] = tmp;
                exchFlag = 1;
            }
            latch.countDown();
        }
    }
    public static void pOddEventSort(int[] arr) throws InterruptedException{
        int start = 0;
        //直到某一次奇阶段完成之后，exchFlag==0，说明连续的偶 奇阶段都没有发生元素交换，说明已经排序完成
        while(exchFlag == 1 || start ==1){
            exchFlag = 0;
            CountDownLatch latch = new CountDownLatch((arr.length - start) / 2);
            //一整个循环完成，就是完成一次阶段的排序（偶阶段/奇阶段）
            for (int i = start; i < arr.length - 1; i += 2) {
                pool.submit(new OddEventSortTask(latch, i, arr));
            }
            latch.await();
            if (start == 1) start = 0;
            else start = 1;
        }
        pool.shutdown();
        System.out.println(Arrays.toString(arr));
    }
}

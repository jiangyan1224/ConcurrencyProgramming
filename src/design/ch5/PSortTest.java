package design.ch5;

import java.util.Arrays;

public class PSortTest {
    public static void main(String[] args) throws InterruptedException {
        int[] arr = new int[]{2,4,21,56,3,42,9,89,51};
        System.out.println(Arrays.toString(arr));
        System.out.print("pOddEventSort：");POddEventSort.pOddEventSort(arr);
    }
}

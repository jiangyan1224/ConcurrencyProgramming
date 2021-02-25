package design.ch5;

import java.util.Arrays;

public class ShellSort {
    public static void shellSort(int[] arr){
        int h = 1;
        while (h <= arr.length / 3) h = h * 3 + 1;
        while (h > 0){
            for (int i = h; i < arr.length; i++) {
                if (arr[i] < arr[i - h]){
                    int j = i - h;
                    int tmp = arr[i];
                    while (j >= 0 && arr[j] > tmp){
                        arr[j + h] = arr[j];
                        j = j - h;
                    }
                    arr[j + h] = tmp;
                }
            }
            h = (h - 1) / 3;
        }
        System.out.println(Arrays.toString(arr));
    }
}

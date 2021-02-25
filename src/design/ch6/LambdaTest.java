package design.ch6;

import java.util.Arrays;
import java.util.function.Function;

public class LambdaTest {
    static int[] arr = {1,2,3,4,5,6,7,8,9,0};
    public static void testFinal(final int x){
//        System.out.println(x++);//报错
    }
    public static void main(String[] args){
//        new Thread(() -> {
//            System.out.println();
//            System.out.println("chu");
//            System.out.println("io");
//        }).start();
//        new Thread(() -> System.out.println());

//        int num = 2;
//        Function<Integer, Integer> intConverter = (x) -> x * num;
//        System.out.println(intConverter.apply(3));

        Arrays.stream(arr).map((x) -> x = x * 2).forEach((x) -> System.out.print(x+"-"));
        System.out.println();
        Arrays.stream(arr).forEach((x) -> System.out.print(x+"-"));
        System.out.println();
        Arrays.stream(arr).forEach((x) -> {x++;
            System.out.print(x+"-");});
    }
}

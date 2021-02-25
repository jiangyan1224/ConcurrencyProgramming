package design.ch2;

import java.util.concurrent.TimeUnit;

public class Test {
    public static int[] ints = new int[5];
    public static Object o = new Object();
    public static Object o1 = new Object();
    public static void main(String[] args) throws Exception {
        new Thread(() -> {            //线程B
            while (true) {
                synchronized (o1){
                    if (ints[0] == 2) {
                        System.out.println("结束");
                        break;
                    }
                }

            }

        }).start();

        new Thread(() -> {
            synchronized (o){
                //线程A
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
//                e.printStackTrace();
                }
                ints[0] = 2;
            }


        }).start();

    }
}

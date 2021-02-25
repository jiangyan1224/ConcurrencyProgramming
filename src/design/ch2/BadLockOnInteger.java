package design.ch2;

public class BadLockOnInteger implements Runnable{
    public static volatile Integer i = 0;
    static BadLockOnInteger instance = new BadLockOnInteger();
    @Override
    public void run() {
        for (int j = 0; j < 1000; j++) {
            /**
             i++;

            情况1：volatile i++，（实际上有没有volatile都一样）
            i++并不是一个原子操作，所以在多个线程都对这个i++的时候就有可能出现丢失修改：
             * 9：getstatic     #2                  // Field i:I获取静态字段，压入栈顶
             * 12: iconst_1                         //int型常量1，压入栈顶
             * 13: iadd                             //栈顶的两个弹出并相加，结果入栈
             * 14: putstatic     #2                  // Field i:I
             * 假设线程a执行到了13行之后得到相加结果1，被挂起；
             * 然后线程b从头执行，在13行得到的也是1，并执行14行完毕，使得i=1；
             * 然后a再继续赋值，最终i=1。两个线程都做了修改，但是其中一个修改被丢失了，volatile在这里也无法发挥作用
             * */

            /**
             * 情况2：volatile Integer i++：
             * 首先，Integer是不可变对象，对Integer的++相当于 i = Integer.valueOf(i)，返回了另一个Integer对象
             * 就导致，线程a b可能并不是在同一个对象上加锁，比如：
             * 线程a先拿到integer1的锁，integer1.owner=a；线程b处于integer1的entrylist上；
             * a执行一次循环，让i指向了integer2，并让出integer1的owner；下一次a再进入syn就要拿到integer2的锁；
             * 而对于b，一旦a退出syn，b被integer1置为owner，去修改integer1/integer2（这里到底是哪个我不确定，可能是取决于CPU能不能在b读取i之前
             * 刷新i到主存，如果是volatile Integer，应该可以即时刷新，b读的应该就是integer2）
             * 如果是volatile，b读的是integer2，b此时拿的integer1的锁，去更新integer2
             * a此时也完全可以拿到integer2的锁，要对integer2修改，但是这个时候，b可以不拿到integer2的锁，就对integer2修改；
             * 这就可能会破坏syn块的原子性和排外性（就是a修改的时候，b可以进去干扰），破坏加锁的语义。
             * */
            synchronized (i){
                i++;
            }
        }
    }
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(instance);
        Thread t2 = new Thread(instance);
        t1.start();t2.start();
        t1.join();t2.join();
        System.out.println(i);
    }
}

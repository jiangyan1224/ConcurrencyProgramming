package design.echoServer.BIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

public class EchoClientBIO {
    //用来执行Runnable任务的线程池
    private static ExecutorService es = Executors.newCachedThreadPool();
    private static final int SLEEP_TIME = 1000*1000*1000;
    //Runnable任务，构造一个Socket和BIOServer连接，并发送信息
    public static class BIOClient implements Runnable{
        @Override
        public void run() {
            //Socket不能在被close之后，再更新引用new Socket，会报错java.net.SocketException: Socket closed
            Socket clientSocket = null;
            BufferedReader br = null;
            PrintWriter pw = null;
            try {
                clientSocket = new Socket();
                clientSocket.connect(new InetSocketAddress("localhost",8000));
                pw = new PrintWriter(clientSocket.getOutputStream(), true);
                System.out.println("bio-client-print-start"+System.currentTimeMillis());
                pw.print("Hello ");
                LockSupport.parkNanos(SLEEP_TIME);
                pw.print("-");
                LockSupport.parkNanos(SLEEP_TIME);
                pw.println("BIOEchoServer");
//                pw.flush();
                System.out.println("bio-client-print-end"+System.currentTimeMillis());
                br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                System.out.println("from echoSever: "+br.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try{
                    if (br != null) br.close();
                    if (pw != null) pw.close();
                    if (clientSocket != null) clientSocket.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
    public static void main(String[] args){
        BIOClient client = new BIOClient();
        for (int i = 0; i < 1; i++) {
            es.execute(client);
        }

    }
}

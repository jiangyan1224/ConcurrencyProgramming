package design.echoServer.BIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoServerBIO {
    //线程池，用来执行客户端提交的任务
    private static ExecutorService es = Executors.newCachedThreadPool();

    //要提交给线程池执行的Runnable任务
    static class HandleMsg implements Runnable{
        Socket clientSocket;//客户端传来的Socket
        HandleMsg(Socket clientSocket){this.clientSocket = clientSocket;}

        @Override
        public void run() {
            BufferedReader br = null;
            PrintWriter pw = null;
            try {
//                pw = new PrintWriter(clientSocket.getOutputStream(), true);
//                pw.println("BIO server发送信息");
                //br读取clientSocket发送来的数据
                br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//                System.out.println("BIO server收到：" + br.readLine());//readLine必须要读到一行的结尾才返回(\r\n)
                //pw写入回应数据（这里就是把客户端发来的数据原样返回）
                pw = new PrintWriter(clientSocket.getOutputStream(), true);
                String input = null;
                long start = System.currentTimeMillis();
                while ((input = br.readLine()) != null){
                    pw.println(input);
                }
                System.out.println("spend" + System.currentTimeMillis() + "-" + start + "=" +
                        (System.currentTimeMillis() - start) + "ms");
            } catch (IOException e) {
                e.printStackTrace();
            }finally {//关闭流和socket
                try{
                    if (br != null) br.close();
                    if (pw != null) pw.close();
                    clientSocket.close();
                }catch (IOException e){
                    e.printStackTrace();
                }

            }
        }
    }

    public static void main(String[] args){
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        try{
            serverSocket = new ServerSocket(8000);
            while (true){
                clientSocket = serverSocket.accept();
                System.out.println(clientSocket.getRemoteSocketAddress() + "accept!");
                es.execute(new HandleMsg(clientSocket));
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

package art.ch4.ThreadPool;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleHttpServer {
    /**处理接收来的socket*/
    static class HTTPRequestHandler implements Runnable{
        private Socket socket;
        public HTTPRequestHandler(Socket socket){this.socket = socket;}

        @Override
        public void run() {
            BufferedReader br = null;
            BufferedReader reader = null;
            PrintWriter out = null;
            InputStream in = null;
            String line = null;
            OutputStream os = null;
            try{
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String header = br.readLine();
                System.out.println("socket header: "+ header);
                //由相对路径计算得出获取绝对路径
                String filePath = basePath + header.split(" ")[1];
                os = socket.getOutputStream();
                out = new PrintWriter(os);//读出流
                System.out.println("full path: "+filePath);
                if (filePath.endsWith("jpg") || filePath.endsWith("ico")){
                    in = new FileInputStream(filePath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int i = 0;
                    while ((i = in.read()) != -1){
                        baos.write(i);
                    }
                    byte[] array = baos.toByteArray();
                    out.println("HTTP/1.1 200 OK");
                    out.println("Server: Molly");
                    out.println("Content-Type: image/jpeg");
                    out.println("");
                    os.write(array, 0, array.length);
                }else{
                    reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
                    out.println("HTTP/1.1 200 OK");
                    out.println("Server: Molly");
                    out.println("Content-Type: text/html; charset=UTF-8");
                    out.println("");
                    while ((line = reader.readLine()) != null){
                        out.println(line);
                    }
                }
                out.flush();
            } catch (IOException e) {
                out.println("HTTP/1.1 500");
                out.println("");
                out.flush();
                e.printStackTrace();
            }finally {
                close(br, in, out, reader, br, socket, os);
            }
        }
        private static void close(Closeable... closeables){
            if (closeables != null){
                for (Closeable closeable: closeables) {
                    try {
                        if (closeable != null) closeable.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    private static ThreadPool<HTTPRequestHandler> pool = new DefaultThreadPool<HTTPRequestHandler>();
    private static String basePath;
    private static ServerSocket serverSocket;
    private static int port = 8080;
    public static void setBasePath(String path){
        if (path != null && new File(path).exists() && new File(path).isDirectory()){
            SimpleHttpServer.basePath = path;
            System.out.println("basePath done");
        }

    }
    public static void setPort(int port){
        if (port > 0) SimpleHttpServer.port = port;
    }
    public static void start() throws IOException {
        serverSocket = new ServerSocket(port);
        Socket socket = null;
        while((socket = serverSocket.accept()) != null){
            pool.execute(new HTTPRequestHandler(socket));
        }
        pool.shutdown();
        serverSocket.close();
    }

}

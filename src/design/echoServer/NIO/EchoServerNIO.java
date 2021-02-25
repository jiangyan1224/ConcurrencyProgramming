package design.echoServer.NIO;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoServerNIO {
    private Selector selector;
    private ExecutorService pool = Executors.newCachedThreadPool();
    //统计在某一个Socket上花费时间的map
    public static Map<Socket, Long> time_stat = new HashMap<Socket, Long>(32);

    class EchoClient{
        private LinkedList<ByteBuffer> outq;
        EchoClient(){
            outq = new LinkedList<ByteBuffer>();
        }
        public LinkedList<ByteBuffer> getOutq(){
            return outq;
        }
        public void enQueue(ByteBuffer bb){
            outq.addFirst(bb);
        }
    }

    private void startServer() throws IOException {
        selector = SelectorProvider.provider().openSelector();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);//这是为了后面accept时，不让主线程阻塞在accept上
        InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 8000);
//        serverSocketChannel.bind(inetSocketAddress);///////////
        serverSocketChannel.socket().bind(inetSocketAddress);

        //ssc在选择器上注册，表示自己对ACCEPT事件感兴趣
        SelectionKey acceptKey =
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //开始轮询，获取感兴趣并且已经就绪的SelectionKey，从对应的channel获取数据并处理
        for(;;){
            int num = selector.select();
            /**
             * 获取已就绪的SelectionKey集合，一定是channel【感兴趣的事件们中至少有一个】就绪了
             * readyOps集合是interestOps的子集；
             * isWritable返回true，必须满足：该channel对写事件感兴趣，并且对写事件已经就绪，即write是属于readyOps的。
             * 但是对SocketChannel随时都可以调用write方法
             * （channel的读写都是通过sk获取到对方的SocketChannel，再read/write，没有对ServerSocketChannel读写的）
             * https://stackoverflow.com/questions/3745413/can-selectionkey-iswritable-be-true-without-op-write-in-interestops
             * */
            Set readyKeys = selector.selectedKeys();
            Iterator iterator = readyKeys.iterator();
            long end = 0;
            while (iterator.hasNext()){
                SelectionKey sk = (SelectionKey) iterator.next();
                iterator.remove();
                if (sk.isAcceptable()){//如果当前SelectionKey是准备好接收客户端连接
                    doAccept(sk);
                }
                else if (sk.isValid() && sk.isReadable()){//如果当前SelectionKey是准备好读数据
                    if (!time_stat.containsKey(((SocketChannel) sk.channel()).socket()))
                        time_stat.put(((SocketChannel) sk.channel()).socket(), System.currentTimeMillis());
                    doRead(sk);
//                    System.out.println("read");
                }
                else if (sk.isValid() && sk.isWritable()){//如果当前SelectionKey是准备好写数据
                    doWrite(sk);
                    end = System.currentTimeMillis();
                    Socket socket = ((SocketChannel)sk.channel()).socket();
                    System.out.println(socket.toString());
                    long start = time_stat.remove(socket);
                    System.out.println("spend:"+end + "-" + start + "=" + (end - start) + "ms");
                }
            }
        }
    }

    //对不同就绪事件的不同处理：doxxx方法：
    private void doAccept(SelectionKey acceptKey){
        //获取当前accept就绪的服务端的ServerSocketChannel
        ServerSocketChannel server = (ServerSocketChannel) acceptKey.channel();
        SocketChannel clientChannel = null;
        try{
            /**
             * 通过服务端的就绪channel，接收客户端的channel
             * 因为之前已经把对accept感兴趣的channel设置为非阻塞，
             * 所以在这里的accept，如果当前没有客户端的连接，会立即返回null，不会一直等待
             * 但是实验发现，这里的返回的clientChannel不会为null，ACCEPT就绪一定发生在真的有客户端来链接的时候
             * */
            clientChannel = server.accept();
            clientChannel.configureBlocking(false);
            //把客户端来的SocketChannel注册到服务端的selector，对读感兴趣，server就可以对这个channel读
            SelectionKey clientKey = clientChannel.register(this.selector, SelectionKey.OP_READ);
            EchoClient echoClient = new EchoClient();
            clientKey.attach(echoClient);

            SocketAddress sa = clientChannel.socket().getRemoteSocketAddress();
            System.out.println("accepted from "+ sa);
            clientChannel.write(ByteBuffer.wrap(new String("server在accept之后用channel向客户端write了一条信息").getBytes()));
            System.out.println("server在accept之后用channel向客户端write done");
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private void doRead(SelectionKey readKey){
        SocketChannel clientChannel = (SocketChannel) readKey.channel();
        ByteBuffer bb = ByteBuffer.allocate(16384);
        int len;
        try{
            len = clientChannel.read(bb);
            bb.flip();
            if (len > 0) {
                byte[] arr = bb.array();
                System.out.println("server在doRead收到client消息：" + new String(arr).trim());
                //附加在SocketChannel上，这里的echoClient只封装了一个队列，用于暂时存储server要发给client的信息
                EchoClient echoClient = (EchoClient) readKey.attachment();
                echoClient.enQueue(bb);
//                echoClient.enQueue(ByteBuffer.wrap(new String("server给echoClient写消息").getBytes()));
                //给SocketChannel增加对Write感兴趣，方便后面通过echoClient写信息给client
                readKey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                selector.wakeup();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void doWrite(SelectionKey writeKey){
        SocketChannel clientChannel = (SocketChannel) writeKey.channel();
        EchoClient client = (EchoClient) writeKey.attachment();
        //这里代码中，因为只有给echoClient写入信息之后，才会增加对写事件的兴趣，所以第一次进入doWrite，队列肯定有消息存储
        ByteBuffer bb = client.getOutq().getLast();
        try{
            int len = clientChannel.write(bb);
            if (bb.remaining() == 0) client.getOutq().removeLast();
        }catch (IOException e){
            e.printStackTrace();
        }
        //如果消息传完了，必须取消channel对写事件的兴趣，否则，key.isWritable会一直为true，
        // 再次进入就有可能导致getLast方法因为队列为空而抛出异常
        if (client.getOutq().size() == 0){
            writeKey.interestOps(SelectionKey.OP_READ);
        }
    }
    public static void main(String[] args) throws IOException {
        EchoServerNIO server = new EchoServerNIO();
        server.startServer();
    }
}

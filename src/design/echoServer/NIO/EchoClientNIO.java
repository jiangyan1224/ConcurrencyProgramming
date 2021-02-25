package design.echoServer.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

public class EchoClientNIO {
    private Selector selector;
    public void init(String ip, int port) throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        this.selector = SelectorProvider.provider().openSelector();
        channel.connect(new InetSocketAddress(ip, port));
        channel.register(selector, SelectionKey.OP_CONNECT);
    }
    public void working() throws IOException {
        while (true){
            if (!selector.isOpen()){
                break;
            }
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isConnectable()){
                    doConnect(key);
                }else if (key.isReadable()){
                    doRead(key);
                }
            }
        }
    }

    public void doConnect(SelectionKey key) throws IOException{
        SocketChannel channel = (SocketChannel) key.channel();
        if (channel.isConnectionPending()){
            channel.finishConnect();
        }
        channel.configureBlocking(false);
        //这里如果没有换行符\r\n，如果server端用的BufferedReader.readLine读取，会因为读不到一行的结尾而陷入IO阻塞
        channel.write(ByteBuffer.wrap(new String("hello from nio-client\r\n").getBytes()));
        channel.register(selector, SelectionKey.OP_READ);
    }
    public void doRead(SelectionKey key) throws IOException{
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer bb = ByteBuffer.allocate(1024);
        channel.read(bb);
        byte[] arr = bb.array();
        System.out.println("从server收到信息："+new String(arr).trim());

        channel.close();
        key.selector().close();
    }

    public static void main(String[] args) throws IOException {
        EchoClientNIO clientNIO = new EchoClientNIO();
        clientNIO.init("localhost", 8000);
        clientNIO.working();
    }
}

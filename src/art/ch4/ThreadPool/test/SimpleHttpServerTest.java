package art.ch4.ThreadPool.test;

import art.ch4.ThreadPool.SimpleHttpServer;

import java.io.IOException;

public class SimpleHttpServerTest {
    public static void main(String[] args) throws IOException {
        SimpleHttpServer.setBasePath("D:/Proj/ConcurrencyProgramming/src/art.ch4/ThreadPool/test");
        SimpleHttpServer.start();
    }
}

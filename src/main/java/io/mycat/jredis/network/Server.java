package io.mycat.jredis.network;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Desc:
 * <p/>Date: 2017/3/28
 * <br/>Time: 21:34
 * <br/>User: gaozhiwen
 */
public class Server {
    private static final int PORT = 6379;

    public static void main(String[] args) throws IOException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        int processNum = Runtime.getRuntime().availableProcessors();
        Reactor[] reactors = new Reactor[processNum];
        for (int i = 0; i < processNum; i++) {
            Reactor reactor = new Reactor(executorService);
            reactor.setName("ReactorThread" + i);
            reactors[i] = reactor;
            reactor.start();
        }
        Acceptor acceptor = new Acceptor(PORT, reactors);
        acceptor.setName("AcceptorThread");
        acceptor.start();
    }
}

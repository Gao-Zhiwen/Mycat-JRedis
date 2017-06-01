package io.mycat.jredis.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Acceptor extends Thread {
    private Reactor[] reactors;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    public Acceptor(int port, Reactor[] reactors) throws IOException {
        this.reactors = reactors;
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override public void run() {
        Set<SelectionKey> selectionKeys = null;
        while (true) {
            try {
                selector.select();
                selectionKeys = selector.selectedKeys();
                for (SelectionKey selectionKey : selectionKeys) {
                    if (selectionKey.isValid() && selectionKey.isAcceptable()) {
                        doConnect();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (selectionKeys != null) {
                    selectionKeys.clear();
                }
            }
        }
    }

    private void doConnect() throws IOException {
        System.out.println("new connection request");
        SocketChannel socketChannel = serverSocketChannel.accept();
        int randomReactor = ThreadLocalRandom.current().nextInt(reactors.length);
        reactors[randomReactor].registClient(socketChannel);
    }
}

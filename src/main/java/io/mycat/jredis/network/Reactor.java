package io.mycat.jredis.network;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class Reactor extends Thread {
    private final ExecutorService executorService;
    private final Selector selector;

    public Reactor(ExecutorService executorService) throws IOException {
        this.executorService = executorService;
        this.selector = Selector.open();
    }

    public void registClient(SocketChannel socketChannel) {
        try {
            new RedisHandler(selector, socketChannel, executorService);
        } catch (IOException e) {
            doClose(socketChannel);
        }
    }

    @Override public void run() {
        Set<SelectionKey> selectionKeys = null;
        while (true) {
            try {
                selector.select(500L);
                selectionKeys = selector.selectedKeys();
                for (SelectionKey selectionKey : selectionKeys) {
                    if (selectionKey.isValid()) {
                        try {
                            RedisHandler redisHandler = (RedisHandler) selectionKey.attachment();
                            if (selectionKey.isReadable()) {
                                redisHandler.doRead();
                            } else if (selectionKey.isWritable()) {
                                redisHandler.doWrite();
                            }
                        } catch (IOException e) {
                            doClose((SocketChannel) selectionKey.channel());
                        }
                    } else {
                        doClose((SocketChannel) selectionKey.channel());
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

    private void doClose(SocketChannel socketChannel) {
        if (socketChannel != null) {
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

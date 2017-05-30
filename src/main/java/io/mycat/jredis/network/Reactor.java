package io.mycat.jredis.network;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * Desc:
 * <p/>Date: 2017/3/28
 * <br/>Time: 21:37
 * <br/>User: gaozhiwen
 */
public class Reactor extends Thread {
    private final ExecutorService executorService;
    private final Selector selector;

    public Reactor(ExecutorService executorService) throws IOException {
        this.executorService = executorService;
        this.selector = Selector.open();
    }

    public void registClient(SocketChannel socketChannel) {
        try {
            new IOHandler(selector, socketChannel, executorService);
        } catch (IOException e) {
            doClose(socketChannel);
        }
    }

    @Override
    public void run() {
        Set<SelectionKey> selectionKeys = null;
        while (true) {
            try {
                selector.select(500L);
                selectionKeys = selector.selectedKeys();
                for (SelectionKey selectionKey : selectionKeys) {
                    if (selectionKey.isValid()) {
                        try {
                            IOHandler ioHandler = (IOHandler) selectionKey.attachment();
                            if (selectionKey.isReadable()) {
                                ioHandler.doRead();
                            } else if (selectionKey.isWritable()) {
                                ioHandler.doWrite();
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

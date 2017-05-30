package io.mycat.jredis.network;

import io.mycat.jredis.command.RedisParser;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;

/**
 * Desc:
 * <p/>Date: 2017/3/29
 * <br/>Time: 21:02
 * <br/>User: gaozhiwen
 */
public class IOHandler {
    private ExecutorService executorService;
    private SelectionKey selectionKey;
    private SocketChannel socketChannel;
    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer;
    private LinkedList<ByteBuffer> writeBufferList = new LinkedList<>();
    private int lastPosition;

    public IOHandler(final Selector selector, SocketChannel socketChannel,
            ExecutorService executorService) throws IOException {
        this.executorService = executorService;
        this.socketChannel = socketChannel;
        this.socketChannel.configureBlocking(false);
        this.selectionKey = this.socketChannel.register(selector, SelectionKey.OP_READ);
        this.selectionKey.attach(this);
        readBuffer = ByteBuffer.allocateDirect(1024);
        onConnected();
    }

    private void onConnected() throws IOException {
        System.out.println("onconnected");
        socketChannel.write(ByteBuffer.wrap("+OK\r\n".getBytes()));
    }

    private void onClosed() throws IOException {
        socketChannel.close();
    }

    public void doRead() throws IOException {
        int num = socketChannel.read(readBuffer);
        if (num == -1) {
            onClosed();
            return;
        }
        if (num == 0) {
            //error
            return;
        }

        RedisParser.parse(readBuffer, lastPosition);
        lastPosition += num;

        if (lastPosition > readBuffer.capacity() / 2) {
            readBuffer.position(lastPosition + 2);
            readBuffer.limit(lastPosition);
            readBuffer.compact();
            lastPosition = 0;
        }
    }

    public void doWrite() throws IOException {
        if (writeBuffer != null && writeBuffer.hasRemaining()) {
            writeToChannel(writeBuffer);
        } else if (!writeBufferList.isEmpty()) {
            writeToChannel(writeBufferList.pollFirst());
        }
    }

    public void write(String data) throws IOException {
        if (data == null || data.length() <= 0) {
            return;
        }
        writeToChannel(ByteBuffer.wrap(data.getBytes()));
    }

    private void writeToChannel(ByteBuffer byteBuffer) throws IOException {
        socketChannel.write(byteBuffer);
        if (byteBuffer.hasRemaining()) {
            selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE);
        } else {
            if (writeBufferList.isEmpty()) {
                selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
            } else {
                selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE);
                writeToChannel(writeBufferList.pollFirst());
            }
        }
    }

    public void closeChannel() {
        try {
            if (socketChannel.isConnected()) {
                socketChannel.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

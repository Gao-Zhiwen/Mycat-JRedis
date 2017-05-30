package io.mycat.jredis.command;

import java.nio.ByteBuffer;

/**
 * Desc:
 * <p/>Date: 30/05/2017
 * <br/>Time: 18:00
 * <br/>User: gaozhiwen
 */
public class RedisParser {
    public static void parse(ByteBuffer buffer, int lastPosition) {
        String line = readLine(buffer, lastPosition);
        if (line == null || line.length() <= 0) {
            return;
        }
        System.out.println("read line: " + line);
        handleLine(line);
        handleLine(readLine(buffer, lastPosition + line.length() + 2));
    }

    private static void handleLine(String line) {
        char c = line.charAt(0);
        switch (c) {
            case '*':
                System.out.println("*****: " + line.substring(1));
                break;
            case '$':
                System.out.println("$$$$$: " + line.substring(1));
                break;
            default:
                System.out.println("default: " + line);
                break;
        }
    }

    private static String readLine(ByteBuffer buffer, int lastPosition) {
        String readLine = null;
        int newPosition = buffer.position();
        for (int i = lastPosition; i < newPosition; i++) {
            if (buffer.get(i) == 13) {
                //读取到换行
                byte[] bytes = new byte[i - lastPosition];
                buffer.get(bytes);
                readLine = new String(bytes);
                break;
            }
        }

        return readLine;
    }
}

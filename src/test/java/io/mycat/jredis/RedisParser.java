package io.mycat.jredis;

/**
 * Desc:
 * <p/>Date: 30/05/2017
 * <br/>Time: 16:49
 * <br/>User: gaozhiwen
 */
public class RedisParser {
    public static void main(String[] args) {
        String str = "$123\r\n";
        int length = 0;

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != '\r') {
                length = (length * 10) + (str.charAt(i) - '0');
            }
        }
        System.out.println(length);
    }
}

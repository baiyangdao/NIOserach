package nio.buffers;

import java.nio.CharBuffer;

/**
 * Created by yang on 2017/11/8.
 * 用CharBuffer测试填充和释放缓存区
 */
public class BufferFillDrain {


    public static void main(String[] args) {
        CharBuffer charBuffer = CharBuffer.allocate(100);
        while (fillBuffer(charBuffer)){
            charBuffer.flip();
            drainBuffer(charBuffer);
            charBuffer.clear();
        }
    }

    /**
     * 向缓冲区写入数据
     * @param charBuffer
     * @return
     */
    private static boolean fillBuffer(CharBuffer charBuffer){
        if (index >= strings.length){
            return false;
        }
        String string = strings[index++];
        for (int i = 0; i < string.length(); i++) {
            charBuffer.put(string.charAt(i));
        }
        return true;
    }

    /**
     * 从缓冲区读数据
     * @param charBuffer
     */
    private static void drainBuffer(CharBuffer charBuffer){
        while (charBuffer.hasRemaining()){
            System.out.print(charBuffer.get());
        }
        System.out.println("");
    }

    private static int index = 0;
    private static String[] strings = {
            "A random string value",
            "The product of an infinite number of monkeys",
            "Hey hey we're the Monkees",
            "Opening act for the Monkees: Jimi Hendrix",
            "'Scuse me while I kiss this fly",
            "Help Me! Help Me!",
    };
}

package nio.channels;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.util.*;

/**
 * gathering 写的测试用例
 * Created by Administrator on 2017/11/10.
 */
public class Marketing {

    private static final String DEMOGRAPHIC = "blashblash.txt";

    public static void main(String[] args) throws IOException {
        int res = 10;
        if (args.length>10){
            res = Integer.parseInt(args[0]);
        }
        FileOutputStream fos = new FileOutputStream(DEMOGRAPHIC);
        GatheringByteChannel gatheringByteChannel = fos.getChannel();
        ByteBuffer[] buffers = utterBS(res);
        while (gatheringByteChannel.write(buffers) > 0){

        }
        System.out.println("写出数据完成"+DEMOGRAPHIC);
        fos.close();
    }

    private static String[] col1 = {
            "Aggregate", "Enable", "Leverage",
            "Facilitate", "Synergize", "Repurpose",
            "Strategize", "Reinvent", "Harness"
    };

    private static String[]col2 = {
            "cross-platform", "best-of-breed", "frictionless",
            "ubiquitous", "extensible", "compelling",
            "mission-critical", "collaborative", "integrated"
    };

    private static String[]col3 = {
            "methodologies", "infomediaries", "platforms",
            "schemas", "mindshare", "paradigms",
            "functionalities", "web services", "infrastructures"
    };

    private static String newLine = System.getProperty("line.separator");
    private static Random rand = new Random();

    /**
     * 随机生成缓冲区，装入数据，并转成读状态
     * @param strings
     * @param suffix
     * @return
     * @throws UnsupportedEncodingException
     */
    private static ByteBuffer  pickRandom (String[]strings,String suffix) throws UnsupportedEncodingException {
        String string = strings[rand.nextInt(strings.length)];
        int total = string.length() + suffix.length();
        ByteBuffer buf = ByteBuffer.allocate(total);
        buf.put(string.getBytes("US-ASCII"));
        buf.put(suffix.getBytes("US-ASCII"));
        buf.flip();
        return buf;
    }

    /**
     * 生成缓冲区数组
     * @param hosMany
     * @return
     * @throws UnsupportedEncodingException
     */
    private static ByteBuffer[] utterBS (int hosMany) throws UnsupportedEncodingException {
        List list = new LinkedList();
        for (int i = 0; i < hosMany; i++) {
            list.add(pickRandom(col1," "));
            list.add(pickRandom(col2," "));
            list.add(pickRandom(col3,newLine));
        }
        ByteBuffer[] bufs = new ByteBuffer[list.size()];
        list.toArray(bufs);
        return bufs;
    }

}

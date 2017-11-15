package nio.channels;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 测试不同模式内存映射的行为方式
 * Created by Administrator on 2017/11/14.
 */
public class MapFile {

    public static void main(String[] args) throws IOException {
        //创建一个文件，连接到一个channel上
        File tempFile = File.createTempFile("mmaptest",null);
        RandomAccessFile file = new RandomAccessFile(tempFile,"rw");
        FileChannel channel = file.getChannel();
        ByteBuffer temp = ByteBuffer.allocate(100);
        //放入一些数据到这个文件位置从0开始
        temp.put("This is the file content".getBytes());
        temp.flip();
        channel.write(temp,0);
        //放入另外一些数据，位置从8192开始
        temp.clear();
        temp.put("This is more file content".getBytes());
        temp.flip();
        channel.write(temp,8192);
        //对这个文件做三种不同的映射
        MappedByteBuffer ro = channel.map(FileChannel.MapMode.READ_ONLY,0,channel.size());
        MappedByteBuffer rw = channel.map(FileChannel.MapMode.READ_WRITE,0,channel.size());
        MappedByteBuffer cow = channel.map(FileChannel.MapMode.PRIVATE,0,channel.size());
        //查看更改之前文件的内容
        System.out.println("Begin");
        showBuffers(ro,rw,cow);
        //更改copy-on-write 映射buffer
        cow.position(8);
        cow.put("COW".getBytes());
        System.out.println("Change to COW Buffer");
        showBuffers(ro,rw,cow);
        //更改read-write 映射buffer
        rw.position(9);
        rw.put("R/W".getBytes());
        rw.position(8194);
        rw.put("R/W".getBytes());
        rw.force();
        System.out.println("change to R/W buffer");
        showBuffers(ro,rw,cow);
        //通过channel向文件写入数据，查看对三种映射的影响
        temp.clear();
        temp.put("Channge Write".getBytes());
        temp.flip();
        channel.write(temp,0);
        temp.rewind();
        channel.write(temp,8202);
        System.out.println("Write On Channel");
        showBuffers(ro,rw,cow);
        //再次更改copy-on-write 映射buffer
        cow.position(8207);
        cow.put("COW2".getBytes());
        System.out.println("Second change to COW Buffer");
        showBuffers(ro,rw,cow);
        //再次更改read/write映射buffer
        rw.position(0);
        rw.put("R/W2".getBytes());
        rw.position(8210);
        rw.put("R/W2".getBytes());
        rw.force();
        System.out.println("Second change to R/W buffer");
        showBuffers(ro,rw,cow);
        channel.close();
        file.close();
        tempFile.delete();

    }

    /**
     * 输出现在映射buffer的内容
     * @param ro
     * @param rw
     * @param cow
     */
    private static void showBuffers(ByteBuffer ro , ByteBuffer rw,ByteBuffer cow){
        dumpBuffers("R/O",ro);
        dumpBuffers("R/W",rw);
        dumpBuffers("COW",cow);
        System.out.println("");
    }

    /**
     * 打印出buffer内容，并且跳过nulls
     * @param prefix
     * @param buffer
     */
    private static void dumpBuffers(String prefix,ByteBuffer buffer){
        System.out.print(prefix+": '");
        int nulls = 0;
        int limit = buffer.limit();
        for (int i = 0 ; i<limit;i++){
            char c = (char)buffer.get(i);
            if (c == '\u0000'){
                nulls++;
                continue;
            }
            if (nulls!=0){
                System.out.print("|["+nulls+" nulls]|");
                nulls = 0;
            }
            System.out.print(c);
        }
        System.out.println("'");
    }

}

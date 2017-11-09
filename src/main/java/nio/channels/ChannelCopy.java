package nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Created by yang on 2017/11/9.
 * 测试channel间的copy
 */
public class ChannelCopy {

    public static void main(String[] args) throws IOException {
        ReadableByteChannel source = Channels.newChannel(System.in);
        WritableByteChannel dest = Channels.newChannel(System.out);
        channelCopy1(source,dest);
        channelCopy2(source,dest);
    }

    /**
     *
     * @param src
     * @param des
     */
    private static void channelCopy1(ReadableByteChannel src, WritableByteChannel des) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16*1024);
        while (src.read(byteBuffer)!=-1){
            //把缓冲区设置为只读状态，此时position为0，limit是缓冲区数据量大小
            byteBuffer.flip();
            //把缓冲区数据，写入通道。此地容易误解，以为是把数据写入了缓存区
            //其实是写入了通道。此时缓冲去的position应该为limit-1
            des.write(byteBuffer);
            //压缩缓冲区，如果缓冲区此时数据为空，相当于清空缓冲区
            byteBuffer.compact();
        }
        //保证缓冲区数据全部被写出
        byteBuffer.flip();
        while (byteBuffer.hasRemaining()){
            des.write(byteBuffer);
        }
    }

    private static void channelCopy2(ReadableByteChannel src , WritableByteChannel des) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16*1024);
        while (src.read(byteBuffer) != -1){
            byteBuffer.flip();
            //每次保证缓冲区的数据被写出
            while (byteBuffer.hasRemaining()){
                des.write(byteBuffer);
            }
            byteBuffer.clear();
        }
    }

}

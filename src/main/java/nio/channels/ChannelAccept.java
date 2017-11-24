package nio.channels;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by Administrator on 2017/11/23.
 */
public class ChannelAccept {
    public static final String GREETING = "Hello I must be going .\r\n";

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 1234;
        ByteBuffer buffer = ByteBuffer.wrap(GREETING.getBytes());
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress(port));
        ssc.configureBlocking(false);
        while (true){
            System.out.println("Watting for connections");
            SocketChannel sc = ssc.accept();
            if (sc == null){
                Thread.sleep(200);
            }else {
                System.out.println("Incoming connection from : " + sc.socket().getRemoteSocketAddress());
                buffer.rewind();
                sc.write(buffer);
                sc.close();
            }
        }
    }
}

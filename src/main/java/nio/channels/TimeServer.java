package nio.channels;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;

/**
 * Created by Administrator on 2017/11/24.
 */
public class TimeServer {
    private static final int DEFAULT_TIME_PORT = 37;
    private static final long DIFF_1990 = 2208988800L;
    private DatagramChannel channel;

    public TimeServer(int port) throws IOException {
        this.channel = DatagramChannel.open();
        this.channel.socket().bind(new InetSocketAddress(port));
        System.out.println("Listening on port " + port +" for time requests");
    }

    public void listen() throws IOException {
        ByteBuffer longByteBuffer = ByteBuffer.allocate(8);
        longByteBuffer.order(ByteOrder.BIG_ENDIAN);
        longByteBuffer.putLong(0,0);
        longByteBuffer.position(4);
        ByteBuffer buffer = longByteBuffer.slice();
        while (true){
            buffer.slice();
            SocketAddress sa = this.channel.receive(buffer);
            if (sa ==  null){
                continue;
            }
            System.out.println("Time request from " + sa);
            buffer.clear();
            longByteBuffer.putLong(0,(System.currentTimeMillis()/1000)+DIFF_1990);
            this.channel.send(longByteBuffer,sa);
        }
    }

    public static void main(String[] args) {
        int port = DEFAULT_TIME_PORT;
        try {
            TimeServer timeServer = new TimeServer(port);
            timeServer.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

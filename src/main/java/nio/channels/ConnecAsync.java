package nio.channels;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * Created by Administrator on 2017/11/24.
 */
public class ConnecAsync {

    public static void main(String[] args) throws IOException {
        String host = "localhost";
        int port = 80;
        InetSocketAddress address = new InetSocketAddress(host,port);
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        sc.connect(address);
        while (! sc.finishConnect()){
            doSomething();
        }
        sc.close();

    }

    private static void doSomething(){
        System.out.println("do something useful");
    }

}

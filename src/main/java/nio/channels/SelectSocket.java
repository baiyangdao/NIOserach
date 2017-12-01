package nio.channels;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * Created by Administrator on 2017/11/29.
 */
public class SelectSocket {

    public static int PORT_NUMBER = 1234;

    public static void main(String[] args) throws IOException {
            new SelectSocket().go(args);
    }

    public void go(String[] argv) throws IOException {
        int port = PORT_NUMBER;
        System.out.println("Listening on port "+port );
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        ServerSocket serverSocket = serverSocketChannel.socket();
        Selector selector = Selector.open();
        serverSocket.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);
        while (true){
            int n = selector.select();
            if (n == 0){
                continue;
            }
            Iterator it = selector.selectedKeys().iterator();
            while (it.hasNext()){
                SelectionKey key = (SelectionKey)it.next();
                if (key.isAcceptable()){
                    ServerSocketChannel server = (ServerSocketChannel)key.channel();
                    SocketChannel channel = server.accept();
                    registerChannel(selector,channel,SelectionKey.OP_READ);
                    sayHello(channel);
                }
                if (key.isReadable()){
                    readDataFromSocket(key);
                }
                it.remove();
            }
        }
    }

    private void registerChannel(Selector selector, SelectableChannel channel,int ops) throws IOException {
        if (channel == null){
            return;
        }
        channel.configureBlocking(false);
        channel.register(selector,ops);
    }

    private ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

    private void readDataFromSocket(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel)key.channel();
        int count;
        buffer.clear();
        while ((count = socketChannel.read(buffer))>0){
            buffer.flip();
            while (buffer.hasRemaining()){
                socketChannel.write(buffer);
            }
            buffer.clear();
        }
        if (count <0){
            socketChannel.close();
        }
    }

    private void sayHello(SocketChannel channel) throws IOException {
        buffer.clear();
        buffer.put("Hi there!\r\n".getBytes());
        buffer.flip();
        channel.write(buffer);
    }


}

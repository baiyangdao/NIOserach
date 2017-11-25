package nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.Pipe;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Random;

/**
 * Created by Administrator on 2017/11/24.
 */
public class PipeTest {

    public static void main(String[] args) throws IOException {
        WritableByteChannel out = Channels.newChannel(System.out);
        ReadableByteChannel workChannel = startWork(10);
        ByteBuffer buffer = ByteBuffer.allocate(100);
        while (workChannel.read(buffer)>=0){
            buffer.flip();
            out.write(buffer);
            buffer.clear();
        }
    }

    private static ReadableByteChannel startWork(int reps) throws IOException {
        Pipe pipe = Pipe.open();
        Worker worker = new Worker(pipe.sink(),reps);
        worker.start();
        return (pipe.source());
    }

    private static class Worker extends Thread{
        WritableByteChannel channel;
        private int reps;
        Worker(WritableByteChannel channel,int reps){
            this.channel = channel;
            this.reps = reps;
        }
        @Override
        public void run() {
            ByteBuffer buffer = ByteBuffer.allocate(100);
            try {
                for (int i = 0; i < this.reps; i++) {
                    doSomeWork(buffer);
                    while (channel.write(buffer)>0){

                    }
                }
            }catch (Exception e){

            }
        }
    }

    private static String[]products = {
            "No good deed goes unpunished",
            "To be, or what?",
            "No matter where you go, there you are",
            "Just say \"Yo\"",
            "My karma ran over my dogma"
    };

    private static Random rand = new Random();
    private static void doSomeWork(ByteBuffer buffer){
        int product = rand.nextInt(products.length);
        buffer.clear();
        buffer.put(products[product].getBytes());
        buffer.put("\r\n".getBytes());
        buffer.flip();
    }
}

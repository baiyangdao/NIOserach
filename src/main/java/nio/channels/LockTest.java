package nio.channels;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Random;

/**
 * Created by yang on 2017/11/10.
 * 测试channel的文件锁定
 */
public class LockTest {

    private static final int SIZEOF_INT = 4 ;
    private static final int INDEX_START = 0;
    private static final int INDEX_COUNT = 10;
    private static final int INDEX_SIZE = INDEX_COUNT*SIZEOF_INT;
    private ByteBuffer buffer = ByteBuffer.allocate(INDEX_SIZE);
    private IntBuffer indexBuffer = buffer.asIntBuffer();
    private Random rand = new Random();

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        boolean writer = false;
        String fileName;
        if (args.length !=2){
            System.out.println("Usage:[ -r | -w ] filename");
            return;
        }
        writer = args[0].equals("-w");
        fileName = args[1];
        RandomAccessFile raf = new RandomAccessFile(fileName , (writer)? "rw":"r");
        FileChannel fc = raf.getChannel();
        LockTest lockTest = new LockTest();
        if (writer){
            lockTest.doUpdates(fc);
        }else {
            lockTest.doQueries(fc);
        }
    }

    /**
     * 测试共享锁
     * @param fc
     * @throws InterruptedException
     */
    void doQueries(FileChannel fc) throws InterruptedException {
        while (true){
            println("trying for shared lock...");
            FileLock lock = null;
            try {
               lock = fc.lock(INDEX_START,INDEX_SIZE,true);
               int reps = rand.nextInt(60)+20;
                for (int i = 0; i < reps; i++) {
                    int n = rand.nextInt(INDEX_COUNT);
                    int positon = INDEX_START + (n*SIZEOF_INT);
                    buffer.clear();
                    fc.read(buffer,positon);
                    int value = indexBuffer.get(n);
                    println("Index entry "+ n + "=" + value);
                    Thread.sleep(100);
                }
                println("<sleeping>");
                Thread.sleep(rand.nextInt(3000)+500);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    lock.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 测试独占锁
     * @param fc
     * @throws InterruptedException
     */
    void doUpdates(FileChannel fc) throws InterruptedException {
        while (true){
            println("trying for exclusive lock...");
            FileLock fileLock = null;
             try {
                    fileLock = fc.lock(INDEX_START,INDEX_SIZE,false);
                    updateIndex(fc);
                    println("<sleping>");
                    Thread.sleep(rand.nextInt(2000)+500);
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                 try {
                     fileLock.release();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
        }
    }

    private int idxval = 1;
    void updateIndex(FileChannel fc) throws InterruptedException, IOException {
        indexBuffer.clear();
        for (int i = 0; i < INDEX_COUNT; i++) {
            idxval++;
            println("Updating index " + i + "=" + idxval);
            indexBuffer.put(idxval);
            Thread.sleep(500);
        }
        buffer.clear();
        fc.write(buffer,INDEX_START);
    }

    private int lastLineLen = 0;
    private void  println(String msg){
        System.out.print("\r");
        System.out.print(msg);
        for (int i = msg.length(); i < lastLineLen; i++) {
            System.out.print(" ");
        }
        System.out.print("\r");
        System.out.flush();
        lastLineLen = msg.length();
    }
}

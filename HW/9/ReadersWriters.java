// Writers and readers

// A database is written and read in parallel by different threads. For simplicity, we now assume that the database can only be locked in one.

// The implementation of the simulation is in ReadersWriters.java. Surround the read and write with synchronous blocks.

// Read and write locks

// Using synchronization blocks degrades performance significantly. However, competing writes are never a problem. We only need to block concurrent writes and reads that are concurrent with writes. Replace the use of synchronization programs with read and write locks using the ReadWriteLock (Links to an external site.) objects.

// If there are too many readers, they can starve the writers. This can be prevented by providing fair ordering in read and write locks: ReentrantReadWriteLock(boolean fair) (Links to an external site.). Compare the results with the previous ones.

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadersWriters {
    private static final int NUMBER_OF_PROCESSES = 100;
    private static final int DATABASE_SIZE = 100;
    private static final int WRITER_RATIO = 5;
    private static final int MAX_VALUE = 1000;
    private static final int DELAY = 100;

    // The read-write lock for the database.
    private static final ReadWriteLock lock = new ReentrantReadWriteLock(true);
    private static int[] dataBase = new int[DATABASE_SIZE];

    public static void main(String[] args) throws InterruptedException {
        ExecutorService pool = Executors.newCachedThreadPool();

        for (int i = 0; i < NUMBER_OF_PROCESSES; ++i) {
            pool.submit(() -> {
                ThreadLocalRandom rnd = ThreadLocalRandom.current();
                int index = rnd.nextInt(DATABASE_SIZE);

                if (rnd.nextInt(WRITER_RATIO) == 0) {
                    int value = rnd.nextInt(MAX_VALUE);
                    lock.writeLock().lock();
                    try{
                        sleep(DELAY);
                        dataBase[index] = value;
                        System.err.println("Written value " + value + " to index " + index);
                    }finally{
                        lock.writeLock().unlock();
                    }
                    // synchronized(dataBase){
                    // }
                } else {
                    int value;
                    lock.readLock().lock();
                    try{
                        sleep(DELAY);
                        value = dataBase[index];
                        System.err.println("Read value " + value + " from index " + index);
                    }finally{
                        lock.readLock().unlock();
                    }
                    // synchronized(dataBase){
                    // }
                }
            });
        }

        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);
    }

    static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
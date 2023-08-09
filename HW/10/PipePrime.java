import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.SynchronousQueue;

public class PipePrime {

    private static final ConcurrentSkipListSet<Integer> primes = new ConcurrentSkipListSet<>();

    private static final SynchronousQueue<Object> allDone = new SynchronousQueue<>();

    static class Node implements Runnable {

        private final DataInputStream in;
        private final CountDownLatch latch;

        public Node(InputStream in, CountDownLatch latch) {
            this.in = new DataInputStream(in);
            this.latch = latch;
        }

        @Override
        public void run() {
            DataOutputStream nextOut = null;
            PipedInputStream nextIn = new PipedInputStream();
            CountDownLatch latch = new CountDownLatch(1);
            int first = -1;
            int next = -1;
            try (nextOut = new DataOutputStream(new PipedOutputStream(nextIn));
                 var prevIn = in) {


                first = prevIn.readInt();
                primes.add(first);

                new Thread(new Node(nextIn, latch)).start();

                while (true) {

                    next = prevIn.readInt();

                    if (next % first == 0) {
                        continue;
                    }
                    nextOut.writeInt(next);
                    nextOut.flush();
                }

            } catch (IOException e) {
                System.out.printf("Thread %s finished.\n", Thread.currentThread().getName());
                if (first == -1 && next == -1) {
                    try {
                        allDone.put(new Object());
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
            }finally{
                nextOut.write(-1);
                nextOut.flush();

                latch.await();
            }

        }
    }

    public static List<Integer> doIt(int n) {
        PipedInputStream in = new PipedInputStream();
        try (DataOutputStream out = new DataOutputStream(new PipedOutputStream(in))) {
            CountDownLatch latch = new CountDownLatch(1);
            new Thread(new Node(in, latch)).start();

            for (int i = 2; i <= n; i++) {
                out.writeInt(i);
                out.flush();
            }


            out.write(-1);
            out.flush();

            latch.wait();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return new ArrayList<>(primes);
    }

    public static void main(String[] args) {
        var primes = doIt(1000);

        for (int p : primes) {
            System.out.println(p);
        }

        if (primes.equals(SingleThreadPrime.primeFinder(1000))) {
            System.out.println("OK");
        } else {
            throw new RuntimeException("NOK");
        }
    }

}

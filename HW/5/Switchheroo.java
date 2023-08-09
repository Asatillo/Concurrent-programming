import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Switchheroo {

    public static void main(String[] args){
        int[] elements = new int[100];
        Arrays.fill(elements, 10000);
        Object[] locks = new Object[100];
        Arrays.fill(locks, new Object());
        ExecutorService es = Executors.newFixedThreadPool(10);
        System.out.println(Arrays.stream(elements).sum());
        for(int i=0;i<10;i++){
            final int a = ThreadLocalRandom.current().nextInt(100);
            final int b = ThreadLocalRandom.current().nextInt(100);
            es.submit(() ->{
                for(int j=0; j<10000; j++){
                    final int value = ThreadLocalRandom.current().nextInt(500);
                    synchronized(locks[a]){
                        synchronized(locks[b]){
                            elements[a] -= value;
                            elements[b] += value;
                        }
                    }

                }
            });
        }
        es.shutdown();
        try {
            while (!es.awaitTermination(60, TimeUnit.SECONDS)) {}
        } catch (InterruptedException e) {}
        System.out.println(Arrays.stream(elements).sum());
    }
}

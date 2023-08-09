import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class InefficientSorter {

    public static void main(String[] args){
        int[] elements = new int[100];
        Random rClass = new Random();
        for (int i = 0; i< elements.length; i++) {
            elements[i] = rClass.nextInt(0, 1000);
        }
        System.out.println(Arrays.toString(elements));
        Object[] locks = new Object[100];
        Arrays.fill(locks, new Object());
        ExecutorService es = Executors.newFixedThreadPool(10);
        System.out.println(Arrays.stream(elements).sum());
        for(int i=0;i<10;i++){
            final int a = ThreadLocalRandom.current().nextInt(100);
            final int b = ThreadLocalRandom.current().nextInt(100);
            es.submit(() ->{
                for(int j=0; j<100000; j++){
                    synchronized(locks[a]){
                        synchronized(locks[b]){
                            int max = Math.max(a, b), min = Math.min(a,b);
                            if(elements[max] < elements[min]){
                                elements[max] = (elements[max] + elements[min]) - (elements[min] = elements[max]);
                            }
                        }
                    }

                }
            });
        }
        es.shutdown();
        try {
            while (!es.awaitTermination(60, TimeUnit.SECONDS)) {}
        } catch (InterruptedException e) {}
        System.out.println(Arrays.toString(elements));
    }
}

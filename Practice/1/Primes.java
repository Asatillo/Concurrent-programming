import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.Arrays;
import java.util.List;

public class Primes{
    static class SieveThread extends Thread{
        private int n;
        private boolean[] isPrime;
        private int init;
        private int upper;

        public SieveThread(int n, boolean[] isPrime, int init, int upper){
            this.n = n;
            this.isPrime = isPrime;
            this.init = init;
            this.upper = upper;
        };

        public void run(){
            sieve(n, isPrime, init, upper);
        }
    }
    public static void sieve(int n, boolean[] isPrime, int init, int upper){
        
        for (int i=init;i<upper;i++){
            if (isPrime[i]){
                for(int j= i*i;j<n; j += i){
                    isPrime[j] = false;
                }
            }
        }
    }
    
    public static void main(String[] args){
        int n = Integer.parseInt(args[0]);
        long start = System.nanoTime(); 
        System.out.println("Number of processors: " + Runtime.getRuntime().availableProcessors());

        boolean[] isPrime = new boolean[n];
        Arrays.fill(isPrime, true);
        int upper = (int)Math.sqrt(n);
        // sieve(n, isPrime, 2, upper);
        Thread[] threads = new Thread[]{
            new SieveThread(n, isPrime, 2, upper/3),
            new SieveThread(n, isPrime, upper/3, upper*2/3),
            new SieveThread(n, isPrime, upper*2/3, upper)
        };
        threads[0].start();
        threads[1].start();
        threads[2].start();
        try{
            threads[0].join();
            threads[1].join();
            threads[2].join();
        }catch(InterruptedException e){};
        List<Integer> primes = IntStream.range(2, n).filter(x -> isPrime[x]).boxed().collect(Collectors.toList());
        System.out.println("Number of primes: " + primes.size() + " Time:" + ((System.nanoTime() - start)/1000000) + "ms");
        // System.out.println(primes);
    }
}
import java.util.ArrayList;

class ChildThread extends Thread{
    static long sum = 0;
    public int init;
    public int end;

    public ChildThread(int init, int end){
        this.init = init;
        this.end = end;
    }

    public void run(){
        long local_sum = 0;
        for(int i=init; i<=end; i++){
            local_sum += i;
        }
        sum += local_sum;
    }
}

public class AddNumbers{
    
    public static void main(String[] args) {

        // Without threading calculation
        long sum = 0;
        long start = System.nanoTime();
        for(int i=1;i<=1_000_000_000;i++){
            sum += i;
        }
        long time = System.nanoTime() - start;
        System.out.println("Sum is " + sum + ". Without threading it took " + time + " nanoseconds.");

        // Calculation with threading
        ArrayList <Thread> threads = new ArrayList<>();
        for(int i=0;i<10;i++){
            threads.add(new ChildThread(i*100_000_000+1, i*100_000_000+100_000_000));
        }

        start = System.nanoTime();
        for (Thread thread : threads) {
            thread.start();
        }

        try{
            for (Thread thread : threads) {
                thread.join();
            }
        }catch(InterruptedException e){}
        time = System.nanoTime() - start;
        System.out.println("Sum is " + ChildThread.sum + ". With threading it took " + time + " nanoseconds.");
    }
}
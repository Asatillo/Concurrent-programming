public class ThreadSafeMutableIntArray {
    static class ThreadProcess implements Runnable{
        ThreadSafeMutableIntArray data = new ThreadSafeMutableIntArray(2);
        public ThreadProcess(){
        }
        public void run(){
            for(int i=0; i<10_000_000; i++){
                data.set(data.elements[i%2], (data.get(data.elements[i%2]) + 2));
            }
        }
    }
    private final int[] elements;
    private final Object[] locks;


    public ThreadSafeMutableIntArray(int capacity) {
        elements = new int[capacity];
        locks = new Object[capacity];
        for (int i = 0; i < elements.length; i++) {
            
            locks[i] = new Object();
        }
    }

    public final int get(int idx) {
        synchronized (locks[idx]) {
            return elements[idx];
        }
    }

    public final void set(int idx, int newValue) {
        synchronized (locks[idx]) {
            elements[idx] = newValue;
        }
    }

    public static void main(String[] args){
        ThreadSafeMutableIntArray safe = new ThreadSafeMutableIntArray(2);
        Thread[] threads = new Thread[10];
        for(int i=0; i<threads.length; i++){
            threads[i] = new Thread();
        }
        for (Thread thread : threads) {
            thread.start();
        }
        try{
            for (Thread thread : threads) {
                thread.join();
            }
        }catch(InterruptedException e){}
    }
}

class ChildThread extends Thread{
    private String message;
    private int count;

    public ChildThread(String message, int count){
        this.message = message;
        this.count = count;
    }

    public void run(){
        for(int i = 0; i<count; i++){
            // System.out.println(message);
            message.chars().forEach(c -> {System.out.print((char)c);});
            System.out.println("\n");
        }
    }
}

public class CreateThreads {
    public static void main(String[] args){
        Thread thread2 = new ChildThread("World", 1000);
        Thread thread1 = new ChildThread("Hello", 1000);
        thread1.start();
        thread2.start();
        try{
            thread1.join();
            thread2.join();
        }catch(InterruptedException e){}
    }
}

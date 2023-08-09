import java.io.PrintWriter;
import java.io.IOException;

class ChildRunnable implements Runnable{
    private String message;
    private int count;
    private PrintWriter pw;
    public ChildRunnable(String message, int count, PrintWriter pw){
        this.message = message;
        this.count = count;
        this.pw = pw;
    }
    @Override 
    public void run(){
        for(int i = 0; i<count; i++){
            // System.out.println(message);
            message.chars().forEach(c -> {pw.print((char)c);});
            pw.println("\n");
        }
    }
}

class ChildThread extends Thread{
    private String message;
    private int count;
    private PrintWriter pw;
    public ChildThread(String message, int count, PrintWriter pw){
        this.message = message;
        this.count = count;
        this.pw = pw;
    }

    public void run(){
        for(int i = 0; i<count; i++){
            // System.out.println(message);
            message.chars().forEach(c -> {pw.print((char)c);});
            pw.println("\n");
        }
    }
}

public class CreateThreads {
    public static void main(String[] args){
        try(PrintWriter pw = new PrintWriter("threads2.txt")){
            for(int i=0; i<5;i++){
                Thread thread1 = null, thread2=null;
                
                switch(i){
                case 0:
                    thread1 = new ChildThread("hello", 10000, pw);
                    thread2 = new ChildThread("world", 10000, pw);
                    break;
                case 1:
                    thread1 = new Thread(new ChildRunnable("hello", 10000, pw));
                    thread2 = new Thread(new ChildRunnable("world", 10000, pw));
                    break;
                case 2:
                    final String message = "hello";
                    final int  count = 10000;
                    thread1 = new Thread(){
                        public void run(){
                            for(int j = 0; j<count; j++){
                                message.chars().forEach(c -> {pw.print((char)c);});
                                pw.println("\n");
                            }
                        }
                    };
                    final String message2 = "world";
                    thread2 = new Thread(){
                        public void run(){
                            for(int j = 0; j<count; j++){
                                message2.chars().forEach(c -> {pw.print((char)c);});
                                pw.println("\n");
                            }
                        }
                    };
                    break;

                    case 3:
                        final String message = "hello";
                        final int  count = 10000;
                        thread1 = new Thread(new Runnable()){
                            public void run(){
                                for(int j = 0; j<count; j++){
                                    message.chars().forEach(c -> {pw.print((char)c);});
                                    pw.println("\n");
                                }
                            }
                        };
                        final String message2 = "world";
                        thread2 = new Thread(new Runnable()){
                            public void run(){
                                for(int j = 0; j<count; j++){
                                    message2.chars().forEach(c -> {pw.print((char)c);});
                                    pw.println("\n");
                                }
                            }
                        };
                        break;
                    
                    case 4:
                    
                }
            }
            // thread1.run();
            // thread2.run();
            thread1.start();
            thread2.start();
            try{
                thread1.join();
                thread2.join();
            }catch(InterruptedException e){}
        }catch(IOException e){}

    }
}

import java.io.PrintWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

class SleepThread extends Thread{
    public SleepThread(){

    }

    public void run(){
        try (PrintWriter pw = new PrintWriter(getName())){
            for(int i=0;i<1000000;i++){
                pw.println(i);
            }
        }catch (IOException e){}
    }
}

public class JoinSleepTwo {
    public static void printLastLine(Thread[] threads){
        for(int i=0;i<threads.length;i++){
            try(BufferedReader br = new BufferedReader( new FileReader(threads[i].getName()))){
                String lastLine = null, line;
                while((line = br.readLine()) != null){
                    lastLine = line;
                }
                System.out.println(i + " " + threads[i].getName() + ' ' + lastLine);
            } catch (IOException e){};
        }

    }

    public static void main(String[] args) {
        Thread[] threads = new Thread[] {
            new SleepThread(), new SleepThread(), new SleepThread(), new SleepThread(), new SleepThread(), new SleepThread(),
            new SleepThread(), new SleepThread(), new SleepThread(), new SleepThread(), new SleepThread(), new SleepThread()
        };
        for(int i=0; i < threads.length; i++){
            threads[i].start();
        };
        try{
            printLastLine(threads);
            for(int i=0; i < threads.length; i++){
                threads[i].join();
            };
        } catch (InterruptedException e) {};
    }
}

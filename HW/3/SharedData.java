import java.util.List;
import java.util.ArrayList;

class AccessListThread extends Thread
{
    private boolean isSyncronized, isInOrder;
    private List<Integer> list;
    private int range, numOfThreads;

    public AccessListThread(boolean isSyncronized, boolean isInOrder, List<Integer> list, int range, int numOfThreads){
        this.isSyncronized = isSyncronized; 
        this.isInOrder = isInOrder;
        this.list = list;
        this.range = range;
        this.numOfThreads = numOfThreads;
    }

    @Override
    public void run(){
        for (int i = range+1; i <= 50; i+=numOfThreads){
            if (isSyncronized){
                synchronized (list) {
                    list.add(i);
                }
            }else{
                list.add(i);
            }
            System.out.println(i);
        }
    }
} 
public class SharedData
{
    public static void main(String[] args){
        useThreads(5, false, false);
        useThreads(10, true, true);

    }

    public static void useThreads(int countThreads, boolean isSyncronized, boolean isInOrder){
        List<Integer> list = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();
        
        for (int i=0; i<countThreads; i++){
            threads.add(new AccessListThread(true, false, list, i, countThreads));
            threads.get(i).start();
        }

        try{
            for (int i=0; i<countThreads; i++){
                threads.get(i).join();
            }
        }catch (InterruptedException e){}
        System.out.println("____------____");
    }

}
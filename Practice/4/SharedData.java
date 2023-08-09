import java.util.ArrayList;
import java.util.List;

class AccessListThread extends Thread{
    boolean isOdd;
    List<Integer> list;
    public AccessListThread(boolean isOdd, List<Integer> list){
        this.isOdd = isOdd;
        this.list = list;
    }

    public void run(){
        for(int i=1;i<1_000_000; i++){
            synchronized(list){
                if(isOdd && (i%2)==1 || !isOdd && (i%2)==0){
                    if(!isOdd && list.size() == 0 || 
                        list.size() != 0 && list.get(list.size() - 1) != i-1){i--; continue;}
                    list.add(i);
                }
            }
        }
    }
} 

public class SharedData {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        Thread thread1 = new AccessListThread(true, list), thread2 = new AccessListThread(false, list);
        thread1.start();
        thread2.start();
        try{
            thread1.join();
            thread2.join();
            System.out.println(list.size());
            int outOfOrder = 0;
            for(int i=0; i<list.size()-1; i++){
                if (list.get(i) == null) continue;
                if (list.get(i+1) == null) continue;
                if(list.get(i) > list.get(i+1)) outOfOrder++;
            }
            System.out.println(list.size() + " out of order " + outOfOrder);
        } catch (InterruptedException e){};
    }
}

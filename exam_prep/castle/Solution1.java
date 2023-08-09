import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

class Solution1{

    static class CyclicBarrier{
        int parties;
        Runnable barrierAction;
        AtomicInteger val = new AtomicInteger(0);
        CyclicBarrier(int parties){
            this.parties = parties;
        }

        CyclicBarrier(int parties, Runnable barrierAction){
            this.parties = parties;
            this.barrierAction = barrierAction;
        }

        boolean shootArrow(int curEnemy){
            synchronized(Solution1.class){
                if(enemy <= 0) return false;
                if(val.incrementAndGet() == parties){
                    if(barrierAction != null){
                        barrierAction.run();
                    }else{
                        for(int i=0;i<parties;i++){
                            b.add(String.format("Enemy shoot by arrow %d\n", --enemy));
                        }
                    }
                    val.set(0);
                }
            }
            Solution1.shootArrow();
            return true;
        }
    }
    private static int enemy = 200;
    private static int tickRate = 100;

    private static void shootArrow(){
        delay(1);
        // enemy--;
        // System.out.printf("Enemy hit by arrow %d\n", enemy);
    }

    public static void delay(int ticks) {
        try{
            Thread.sleep(ticks*tickRate);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        Thread threads[] = new Thread[10+1+2]; 
        BlockingQueue<String> b = new ArrayBlockingQueue<String>(10);
        CyclicBarrier cb = new CyclicBarrier(10, (() -> {
            enemy -= 10;
            b.add(String.format("Enemies hit by arrow %d-%d\n", --enemy, enemy+9));
        }));
        for(int i=0; i<10;i++){
            threads[i] = new Thread(() -> {
                while(true){
                    // synchronized(Solution1.class){
                    //     if(enemy <= 0) break;
                    //     b.add(String.format("Enemy hit by arrow %d\n", --enemy));
                    // }
                    if(!cb.shootArrow()) break;
                    // cb.shootArrow();
                    delay(5);
                }
            });
        }
        threads[10] = new Thread(() -> {
            while(true){
                synchronized(Solution1.class){
                    if(enemy<=0) break;
                }
                try{
                    String message = b.poll(tickRate, TimeUnit.MILLISECONDS);
                    if(message == null){
                        if(enemy <= 0) break;
                        continue;
                    }
                    System.out.println(message);
                }catch(InterruptedException e){}
            }
        });
        AtomicBoolean haveCannobal = new AtomicBoolean(false);
        threads[11] = new Thread(() -> {
            while(true){
                if(haveCannobal.get()){
                    if(enemy <= 0) break;
                    delay(1);
                    continue;
                } 
                delay(10);
                haveCannobal.set(true);
                synchronized(haveCannobal){
                    haveCannobal.notify();
                }
            }
        }); // smuggler
        threads[12] = new Thread(() -> {
            while(true){
                if(enemy <= 0) break;
                synchronized(haveCannobal){
                    try{
                        haveCannobal.wait(tickRate);
                    }catch(InterruptedException e){}
                    if(!haveCannobal.get()) continue;
                }
                haveCannobal.set(false);
            
                synchronized(Solution1.class){
                    if(enemy < 10){
                        enemy = 0;
                    }else{
                        enemy -= 10;
                    }
                }
            }
        }); // cannon
        for(int i=0; i<threads.length;i++){
            threads[i].start();;
        }

        
        try{
            for(int i=0; i<threads.length;i++){
                threads[i].join();
            }
        }catch(InterruptedException e){}
    }
}
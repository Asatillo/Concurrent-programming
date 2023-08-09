import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ThreadLocalRandom;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

public class FieldRace{
    static final int PLAYER_COUNT = 10;
    static final int CHECKPOINT_COUNT = 10;
    static AtomicBoolean isOn = new AtomicBoolean(true);
    static ConcurrentHashMap<Integer, Integer> scores = new ConcurrentHashMap<>(); 
    static AtomicInteger[] checkpointScores = new AtomicInteger[PLAYER_COUNT];  
    
    private static <T> List<T> nCopyList(int count, IntFunction<T> makeElem) {
        return IntStream.range(0, count).mapToObj(i -> makeElem.apply(i)).toList();
    }

    private static void printScores(){
        StringBuilder str = new StringBuilder("Scores: [");
        for(ConcurrentHashMap.Entry<Integer, Integer> entry: scores.entrySet()){
            str.append(entry.getKey() + "=" + entry.getValue() + " ");
        }
        str.append("]");
        System.out.println(str);
        try{
            Thread.sleep(1000);
        }catch(InterruptedException e){}
    }
    
    public static void main(String[] args){
        List<BlockingQueue<AtomicInteger>> checkpointQueues = 
        nCopyList(CHECKPOINT_COUNT, n -> new ArrayBlockingQueue<AtomicInteger>(32));

        for(int i=0; i<PLAYER_COUNT;i++){
            scores.put(i, 0);
            checkpointScores[i] = new AtomicInteger();
        }
        
        ExecutorService ex = Executors.newFixedThreadPool(PLAYER_COUNT + CHECKPOINT_COUNT + 1);
        ex.execute((() -> {
            while(isOn.get()){
                printScores();
            }
        }));

        for(int j=0;j<CHECKPOINT_COUNT;j++){
            final int finalj = j;
            ex.execute((() -> {
                while(isOn.get()){
                    try{
                        AtomicInteger val = checkpointQueues.get(finalj).poll(2, TimeUnit.SECONDS);
                        if(val == null)continue;
                        val.set(ThreadLocalRandom.current().nextInt(10, 100));
                        synchronized(val){val.notify();}
                    }catch(InterruptedException e){}
                }
            }));
        }

        for(int k=0;k<PLAYER_COUNT;k++){
            final int finalk = k;
            ex.execute((() -> {
                while(isOn.get()){
                    Integer rand_chp = ThreadLocalRandom.current().nextInt(0, CHECKPOINT_COUNT);
                    int rand_time = ThreadLocalRandom.current().nextInt(500, 2000);
                    try{
                        Thread.sleep(rand_time);
                    }catch(InterruptedException e){}
                    AtomicInteger checkpointScore = checkpointScores[finalk];
                    try{
                        checkpointQueues.get(rand_chp.intValue()).put(checkpointScore);
                    }catch(InterruptedException e){}
                    synchronized(checkpointScore){
                        try{
                            long start = System.currentTimeMillis();
                            while(checkpointScore.get() == 0 && System.currentTimeMillis() - start <= 3000){
                                checkpointScore.wait();
                            }
                        }catch(InterruptedException e){}
                        int val = checkpointScore.get();
                        checkpointScore.set(0);
                        System.out.println("Player " + finalk + " got " + val + " at checkpoint " + rand_chp);
                        Integer new_val = scores.get(rand_chp) + val;
                        scores.put(rand_chp, new_val);
                    }
                }
            }));
        }// player == producer
        
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run(){
                isOn.set(false);
                ex.shutdown();
                // try{
                //     ex.awaitTermination(3, TimeUnit.SECONDS);
                // }catch(InterruptedException e){}
                try{
                    ex.awaitTermination(4, TimeUnit.SECONDS);
                }catch(InterruptedException e){}
                ex.shutdownNow();
                while(!ex.isTerminated()){
                    try{
                        ex.awaitTermination(1, TimeUnit.SECONDS);
                    }catch(InterruptedException e){}    
                }
                printScores();
            }
        }, 10*1000);
    }
}
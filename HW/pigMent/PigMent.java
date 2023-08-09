import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PigMent {
  static final int TIC_MIN   = 50;  // Tickrate minimum time (ms)
  static final int TIC_MAX   = 200; // Tickrate maximum time (ms)
  static final int FEED      = 20;  // Mass gained through photosynthesis
  static final int BMR       = 10;  // Mass lost due to basal metabolic rate
  static final int MAX_POP   = 10;  // Maximum number of concurent pigs
  static final int INIT_POP  = 3;   // size of initial pig population
  static final int INIT_MASS = 20;  // starting mass of initial pigs

  // TODO: don't forget to make the pigs scream edgy stuff:
  // 
  
  private static PriorityBlockingQueue<PhotoPig> openArea = new PriorityBlockingQueue<>(MAX_POP, (a, b) -> a.mass - b.mass);
  private static AtomicInteger id_counter = new AtomicInteger(0);
  private static ExecutorService pigPool = Executors.newFixedThreadPool(MAX_POP);
  private static AtomicInteger population = new AtomicInteger(0);
  private static boolean signal = false; 
  private static final ReentrantLock lock = new ReentrantLock();
  private static final Condition condition = lock.newCondition();
  
  static class PhotoPig implements Runnable {
    
    final int id;
    public int mass;
    
    public PhotoPig(int mass) {
      this.mass = mass;
      this.id   = id_counter.getAndIncrement();
  
    }
    
    void pigSleep() {
      try{
        int tic_time = ThreadLocalRandom.current().nextInt(TIC_MIN, TIC_MAX);
        Thread.sleep(tic_time);
      }catch(InterruptedException e){};
    }

    @Override public void run() {
      population.incrementAndGet();
      if(population.get() >= MAX_POP){
        lock.lock();
        try{
          signal = true;
          condition.signal();
        }finally{
          lock.unlock();
        }
      }
      this.pigSay("Beware world, for I'm here now!");
      while(this.eatLight() && this.aTerribleThingToDo()){
        if(pigPool.isShutdown()){
          break;
        }
      }
      if(pigPool.isShutdown()){
        pigSay("Look on my works, ye Mighty, and despair!");
      }else{
        this.pigSay("I have endured unspeakable horrors. Farewell, world!");
        population.decrementAndGet();
      }
    }


    public synchronized void pigSay(String msg) {
      System.out.println("<Pig#" + this.id + "," + this.mass + "kg> " + msg);
    }

    boolean eatLight() {
      openArea.offer(this);
      this.mass += FEED;
      pigSay("Holy crap, I just ate light!");
      this.mass -= this.mass/BMR;
      if(this.mass/BMR > FEED / 2){
        if(population.get() >= MAX_POP){
          lock.lock();
          try{
            signal = true;
            condition.signal();
          }finally{
            lock.unlock();
          }
          return false;
        }
        this.mass /= 2;
        pigPool.submit(new PhotoPig(this.mass));
        pigSay("This vessel can no longer hold the both of us!");
      }
      
      return openArea.remove(this);
    }

    boolean aTerribleThingToDo() {
      openArea.offer(this);
      if(openArea.size() <= 1){
        return true; 
      } 
      PhotoPig smallerPig = openArea.stream().filter(pig -> pig.mass < this.mass).findFirst().orElse(null);
      if(smallerPig == null){
        return true;
      }
      if(openArea.remove(this)){
        openArea.remove(smallerPig);
        pigSay("Bless me, Father, for I have sinned.");
        this.mass += smallerPig.mass/2;
        this.pigSleep();
        return true;
      }else{
        return false;
      }
    }
  }

  public static void main(String[] args) {
    System.out.println("================\n Hello PigMent! \n================"); 
    for(int i=0; i<INIT_POP; i++){
      pigPool.submit(new PhotoPig(INIT_MASS));
    }
    
    lock.lock();
    try{
      while(!signal){
        condition.await();
      }
      pigPool.shutdownNow();
      System.out.println("Signal received, population is equal to " + population.get());
    }catch(InterruptedException e){
      e.printStackTrace();
    }finally{
      lock.unlock();
    }
  }
}
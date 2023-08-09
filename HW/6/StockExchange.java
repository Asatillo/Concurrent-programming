import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.TimerTask;
import java.util.Timer;
import java.util.ArrayList;

public class StockExchange {

    private static final Map<String, Double> stocks = Collections.synchronizedMap(new HashMap<>());

    public static void buildStock(){
        for(int i=0; i<10; i++){
            char c1 = (char)(int)Math.floor(Math.random()*26+65);
            char c2 = (char)(int)Math.floor(Math.random()*26+65);
            char c3 = (char)(int)Math.floor(Math.random()*26+65);
            char[] arr = {c1,c2,c3};
            String stockName = new String(arr);
            stocks.put(stockName, 100.0);
            System.out.println(stocks.get(stockName));
        }
    }

    public static class Broker extends Thread{
        @Override
        public void run(){
            for(int i=0; i<1000; i++){
                Boolean buy = ThreadLocalRandom.current().nextBoolean();
                synchronized(stocks){
                    Object[] keys = stocks.keySet().toArray();
                    Random rand = new Random();
                    String randKey = keys[rand.nextInt(keys.length)].toString();
                    synchronized(buy){
                        if(buy){
                            stocks.put(randKey, stocks.get(randKey)*1.01);
                        }
                        else{
                            stocks.put(randKey, stocks.get(randKey)*0.99);
                        }
                    }
                }
            }
        }
    }

    public static class Print extends TimerTask {
        boolean running = true;
        
        public void terminate(){
            running=false;
        }
        @Override
        public void run() {
            while(running){
                synchronized(stocks){
                    for (Map.Entry<String, Double> entry : stocks.entrySet()) {
                        System.out.println(entry.getKey()+" : "+ Double.parseDouble(String.format("%.2f", entry.getValue())));
                    }
                }
                System.out.println("=======================");
            }
           
        }
    }
    


    public static void main(String[] args){
        buildStock();
        int numberOfBrokers = 100;
        ArrayList<Broker> brokers = new ArrayList<>();
        for(int j=0; j<numberOfBrokers; j++){
            brokers.add(new Broker());
        }

        for (Broker broker : brokers) {
            broker.start();
        }

        Timer timer = new Timer();
        Print print = new Print();
        timer.schedule(print, 0, 1000);

        for (Broker broker : brokers) {
            try{
                broker.join();
            }catch(InterruptedException e){
                System.err.println("Broker was interrupted");
            }
        }
        print.terminate();
    }
    
}

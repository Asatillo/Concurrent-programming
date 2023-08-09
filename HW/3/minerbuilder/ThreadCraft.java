public class ThreadCraft {
    private static Resources resources = new Resources(); 
    public static void main(String[] args) {
        Thread miner = new Thread(){
            @Override
            public void run(){
                mineAction();
            }
        };
        miner.start();

        Thread builder = new Thread(){
            @Override
            public void run(){
                buildAction();
            }
        };
        builder.start();

        Thread logger = new Thread(){
            @Override
            public void run(){
                loggingAction();
            }
        };
        logger.start();

        try{
            miner.join();
            builder.join();
            logger.join();
        }catch(InterruptedException e){
            e.printStackTrace();
        }

        System.out.println("Simulation is over");
        logStatus();
    }

    private static void mineAction(){
        while(resources.getGoldmineCapacity() > 0){
            resources.tryToMineGold();
            System.out.println("Miner mines gold");
            sleepForMsec(Configuration.MINING_FREQUENCY);
        }
        System.out.println("Miner finished mining");
    }

    private static void buildAction(){
        while(!isOver()){
            if(resources.tryToBuildHouse()){
                System.out.println("Builder builds a house");
                sleepForMsec(Configuration.BUILD_TIME);
            }else{
                System.out.println("Builder waits for gold");
                sleepForMsec(Configuration.SLEEP_TIME);
            }
        }
        System.out.println("Builder finished building");
    }

    private static void loggingAction(){
        while(!isOver()){
            logStatus();
            sleepForMsec(Configuration.LOGGING_FREQUENCY);
        }
    }

    private static void logStatus(){
        System.out.println(
            "Gold: " + resources.getGold() +
            "\nHouses: " + resources.getHouses() +
            "\nGoldmine: " + resources.getGoldmineCapacity() +
            "\n"
        );
    }
    public static boolean isOver(){
        return resources.getHouses() == Configuration.HOUSE_LIMIT;
    }

    public static void sleepForMsec(int msec){
        try{
            Thread.sleep(Configuration.SLEEP_TIME);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}

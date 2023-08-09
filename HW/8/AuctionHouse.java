import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class AuctionHouse {
    static class NFT {
        public final int artistIdx;
        public final int price;

        public NFT(int artistIdx, int price) {
            this.artistIdx = artistIdx;
            this.price = price;
        }
    }

    static class AuctionOffer {
        public int offeredSum;
        public String collectorName;

        public AuctionOffer(int offeredSum, String collectorName) {
            this.offeredSum = offeredSum;
            this.collectorName = collectorName;
        }
    }

    static int failCount = 0;

    static final int MAX_NFT_PRICE = 100;
    static final int MAX_NFT_IDX = 100_000;
    static final int MAX_COLLECTOR_OFFER = MAX_NFT_IDX / 100;

    private static final int COLLECTOR_MIN_SLEEP = 10;
    private static final int COLLECTOR_MAX_SLEEP = 20;
    private static final int MAX_AUCTION_OFFERS = 10;

    static final int ARTIST_COUNT = 10;
    static final int COLLECTOR_COUNT = 5;

    static final int INIT_ASSETS = MAX_NFT_IDX / 10 * MAX_NFT_PRICE;

    static int nftIdx = 0;
    static int remainingNftPrice = INIT_ASSETS;
    static NFT[] nfts = new NFT[MAX_NFT_IDX];

    static int totalCommission = 0;
    static int noAuctionAvailableCount = 0;
    static int soldItemCount = 0;

    static BlockingQueue<AuctionOffer> auctionQueue;

    public static void main(String[] args) throws InterruptedException {
        // Task 1
        List<Thread> artists = makeArtists();

        // Task 2
        Thread auctioneer = makeAuctioneer(artists);

        // Task 3
        List<Thread> collectors = makeCollectors(auctioneer);

        for (int i = 0; i < ARTIST_COUNT; i++) {
            artists.get(i).start();
        }
        try {
            for (int i = 0; i < ARTIST_COUNT; i++) {
                artists.get(i).join();
            }
        } catch (InterruptedException e) {}

        runChecks();
    }

    // ------------------------------------------------------------------------
    // Task 1

    private static List<Thread> makeArtists() {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < ARTIST_COUNT; i++) {
            final int finali = i;
            threads.add(new Thread(() -> {
                while (true) {
                    sleepForMsec(20);
                    int price = getRandomBetween(100, 1000);
                    int curIdx;
                    synchronized (nfts) {
                        if (nftIdx >= MAX_NFT_IDX) break;
                        if (price > remainingNftPrice) break;
                        curIdx = nftIdx++;
                        remainingNftPrice -= price;
                        nfts[curIdx] = new NFT(finali, price);
                    }
                }
            }));
        }
        return threads;
    }

    // ------------------------------------------------------------------------
    // Task 2

    private static Thread makeAuctioneer(List<Thread> artists) {
        Thread auctioneer = new Thread(() -> {
            int remAuctionsCnt = 100;
            while(true){
                boolean artistsWorking = false;
                for(int i=0; i<artists.size();i++){
                    artistsWorking = artistsWorking & artists.get(i).isAlive();
                }
                if(!artistsWorking && remAuctionsCnt <= 0) break;

                int availableNftCount;
                synchronized(AuctionHouse.class){
                    availableNftCount = nftIdx;
                }

                if(availableNftCount == 0) continue;

                var chosenNftIdx = getRandomBetween(0, availableNftCount-1);

                synchronized(AuctionHouse.class){
                    auctionQueue = new ArrayBlockingQueue<>(100);
                }

                int bestPrice = 0;

                for(int i=0; i<MAX_AUCTION_OFFERS;i++){
                    try{
                        AuctionOffer offer = auctionQueue.poll(1, TimeUnit.SECONDS);
                        if(offer != null && offer.offeredSum > bestPrice){
                            bestPrice = offer.offeredSum;
                        }
                    }catch(InterruptedException e){
                        break;
                    }
                }

                if(bestPrice != 0) soldItemCount += 1;

                totalCommission += (nfts[chosenNftIdx].price + bestPrice)/10;

                synchronized(AuctionHouse.class){
                    auctionQueue = null;
                }

                sleepForMsec(3);
                remAuctionsCnt -= 1;
            }
        });
        return auctioneer;
    }
    // ------------------------------------------------------------------------
    // Task 3

    private static List<Thread> makeCollectors(Thread auctioneer) {
        List<Thread> collectors = new ArrayList<>();
        for(int i=0;i<COLLECTOR_COUNT;i++){
            int finali = i;
            collectors.add(new Thread(() ->{
                AuctionOffer lastBid = null;
                while (auctioneer.isAlive()) {
                    if (auctionQueue != null) {
                        AuctionOffer a = auctionQueue.peek();
                        if (a == null) {
                            noAuctionAvailableCount++;
                            sleepForMsec(ThreadLocalRandom.current().nextInt(COLLECTOR_MIN_SLEEP,COLLECTOR_MAX_SLEEP));
                        } else {
                            if (lastBid != a) {
                                lastBid = a;
                                String name = "Collector" + (finali + 1);
                                if (!a.collectorName.equals(name)) {
                                    int price = ThreadLocalRandom.current().nextInt(0, MAX_COLLECTOR_OFFER);
                                    auctionQueue.add(new AuctionOffer(price, name));
                                }
                            }
                        }
                    }
                }
            }));
        }
        return collectors;
    }

    // ------------------------------------------------------------------------
    // Tester

    private static String isOK(boolean condition) {
        if (!condition)   ++failCount;
        return isOkTxt(condition);
    }

    private static String isOkTxt(boolean condition) {
        return condition ? "GOOD" : "BAD ";
    }

    private static void runChecks() {
        if (Thread.activeCount() == 1) {
            System.out.printf("%s Only the current thread is running%n", isOK(true));
        } else {
            System.out.printf("%s %d threads are active, there should be only one%n", isOK(Thread.activeCount() == 1), Thread.activeCount());
        }

        System.out.printf("%s nftIdx > 0%n", isOK(nftIdx > 0));

        int soldPrice = IntStream.range(0, nftIdx).map(idx-> nfts[idx].price).sum();
        System.out.printf("%s Money is not lost: %d + %d = %d%n", isOK(soldPrice + remainingNftPrice == INIT_ASSETS), soldPrice, remainingNftPrice, INIT_ASSETS);

        System.out.printf("%s [Only Task 2] Total commission not zero: %d > 0%n", isOK(totalCommission > 0), totalCommission, INIT_ASSETS);

        System.out.printf("%s [Only Task 3] Sold item count not zero: %d > 0%n", isOK(soldItemCount > 0), soldItemCount, INIT_ASSETS);
        // System.out.printf("%s [Only Task 3] Some collectors have become owners of NFTs: %d > 0%n", isOK(owners.size() > 0), owners.size(), INIT_ASSETS);
        System.out.printf("%s [Only Task 3] Sometimes, collectors found no auction: %d > 0%n", isOK(noAuctionAvailableCount > 0), noAuctionAvailableCount, INIT_ASSETS);

        System.out.printf("%s Altogether %d condition%s failed%n", isOkTxt(failCount == 0), failCount, failCount == 1 ? "" : "s");

        // forcibly shutting down the program (don't YOU ever do this)
        System.exit(42);
    }

    // ------------------------------------------------------------------------
    // Utilities

    private static int getRandomBetween(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max+1);
    }

    private static void sleepForMsec(int msec) {
        try {
            Thread.sleep(msec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
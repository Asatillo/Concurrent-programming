import java.util.concurrent.Semaphore;

public class Philosophers {
    private static final int NUMBER_OF_PHILOSOPHERS = 5;
    private static final int THINK_TIME = 100;
    private static final int EAT_TIME = 50;

    private static Semaphore[] forks = new Semaphore[NUMBER_OF_PHILOSOPHERS];
    enum State {
        THINKING, HUNGRY, EATING;
    }
    private static State[] state = new State[NUMBER_OF_PHILOSOPHERS];
    static {
        for (int i = 0; i < NUMBER_OF_PHILOSOPHERS; ++i) {
            forks[i] = new Semaphore(1);
        }
    }

    private static class Philosopher extends Thread {
        private int id;

        Philosopher(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            while (true) {
                think();
                eat();
            }
        }

        private void think() {
            System.err.println("#" + id + " Thinking...");
            try {
                Thread.sleep(THINK_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void test(int id)
        {
            if (state[id] == Philosophers.State.HUNGRY &&
                state[(id + NUMBER_OF_PHILOSOPHERS - 1) % NUMBER_OF_PHILOSOPHERS] != Philosophers.State.EATING &&
                state[(id + 1) % NUMBER_OF_PHILOSOPHERS] != Philosophers.State.EATING) {
                state[id] = Philosophers.State.EATING;
                forks[id].release();
            }
        }

        private void eat() {
            System.err.println("#" + id + " Taking left fork.");
            int leftFork = id;
            int rightFork = (id + 1) % NUMBER_OF_PHILOSOPHERS;
            //if (rightFork == 0) {
            //    rightFork = leftFork; leftFork = 0;
            //}
            synchronized (Philosopher.class) {
                state[id] = Philosophers.State.HUNGRY;
                test(id);
            }
            try {
                forks[id].acquire();
            } catch (InterruptedException e) {}
            //synchronized (forks[leftFork]) {
                System.err.println("#" + id + " Taking right fork.");
                //synchronized (forks[rightFork]) {
                    System.err.println("#" + id + " Eating...");
                    try {
                        Thread.sleep(EAT_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                //}
            //}
            synchronized (Philosopher.class) {
                state[id] = Philosophers.State.THINKING;
                test((id + NUMBER_OF_PHILOSOPHERS - 1) % NUMBER_OF_PHILOSOPHERS);
                test((id + 1) % NUMBER_OF_PHILOSOPHERS);
            }
        }
    }

    public static void main(String[] args) {
        Philosopher[] philosophers = new Philosopher[NUMBER_OF_PHILOSOPHERS];

        for (int i = 0; i < NUMBER_OF_PHILOSOPHERS; ++i) {
            philosophers[i] = new Philosopher(i);
            philosophers[i].start();
        }
    }
}
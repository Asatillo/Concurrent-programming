public class JoinSleep{
    public static void main(String[] args){
        Thread thread1 = new Thread(()-> {
            for(int i=0;i<10900;i++){
                System.out.println("hello");
                if (i != 9999){
                    try {
                        Thread.sleep(5);
                    }catch(InterruptedException e){
                        System.out.println("Thread1 was interupted");
                    }
                } 
            }
        }), thread2 = new Thread(() -> {
            for(int i=0;i<10000;i++){
                System.out.println("world");
                if (i != 9999){
                    try {
                        Thread.sleep(5);
                    }catch(InterruptedException e){
                        System.out.println("Thread2 was interupted");
                    }
                } 
            }
        }), thread3 = new Thread(() -> {
            while (Thread.currentThread().getThreadGroup().activeCount() > 2){
                try {
                    Thread.sleep(1000);
                }catch(InterruptedException e){
                    System.out.println("Thread3 was interupted");
                }
            }
            System.out.println("Programm finished!");
        });
        thread1.start();
        thread2.start();
        thread3.start();
        try{
            Thread.sleep(1000);
            thread1.interrupt();thread2.interrupt();thread3.interrupt();
            thread1.join();thread2.join();thread3.join();
        } catch (InterruptedException e) {}
    }
}
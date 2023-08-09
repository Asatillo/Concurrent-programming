public class JoinSleep{
    public static void main(String[] args){
        Thread thread1 = new Thread(()-> {
            for(int i=0;i<10000;i++){
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
        });
        thread1.start();
        thread2.start();
        try{
            Thread.sleep(1000);
            thread1.interrupt();thread2.interrupt();
            thread1.join();thread2.join();
        } catch (InterruptedException e) {}
    }
}
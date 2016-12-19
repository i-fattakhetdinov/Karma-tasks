package ThreadPool;

public class Main {
    public static void main(String[] args) {
        ThreadPool myPool = new ThreadPool(4);
        Runnable run = () -> System.out.println("In Runnable");
        Runnable hello = () -> System.out.println("hello");
        for (int i = 0; i < 10000; i++) {
            myPool.execute(run);
            myPool.execute(hello);
        }
        myPool.shutdown();
    }
}

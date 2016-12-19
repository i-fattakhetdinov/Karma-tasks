package ThreadPool;

import java.util.Vector;

public class ThreadPool {
    private Vector<Thread> workers;
    private ThreadSafeQueue<Runnable> tasks;

    ThreadPool(int num_workers) {
        tasks = new ThreadSafeQueue<Runnable>();
        workers = new Vector<>();
        for (int i = 0; i < num_workers; i++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            Runnable task = tasks.pop();
                            task.run();
                        }
                    } catch (Exception e) {
                    }
                }
            };
            thread.start();
            workers.add(thread);
        }
    }

    void execute(Runnable function) {
        tasks.push(function);
    }

    void shutdown() {
        tasks.shutdown();
        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }
        }
    }
}

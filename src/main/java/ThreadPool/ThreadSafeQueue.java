package ThreadPool;

import java.util.LinkedList;
import java.util.Queue;

public class ThreadSafeQueue<Type> {
    private static final Object monitor = new Object();
    private boolean isEnable;
    private Queue<Type> myQueue = new LinkedList<>();

    ThreadSafeQueue() {
        isEnable = true;
    }

    public void push(Type v) {
        synchronized (monitor) {
            isEnable();
            myQueue.add(v);
            monitor.notify();
        }
    }

    public Type pop() {
        synchronized (monitor) {
            while (myQueue.isEmpty()) {
                isEnable();
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException();
                }
            }
            Type res = myQueue.poll();
            return res;
        }
    }

    public void shutdown() {
        synchronized (monitor) {
            isEnable = false;
            monitor.notifyAll();
        }
    }

    private void isEnable() {
        if (!isEnable) {
            throw new RuntimeException();
        }
    }
}

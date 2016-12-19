package ThreadPool;

import java.util.Vector;

public class ThreadSafeQueue<Type> {
    private static final Object monitor = new Object();
    private boolean isEnable;
    private Vector<Type> myQueue = new Vector<>();

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
            Type res = myQueue.lastElement();
            myQueue.removeElementAt(myQueue.size() - 1);
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

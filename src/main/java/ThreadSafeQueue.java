import java.util.Vector;

public class ThreadSafeQueue{
    private static final Object monitor = new Object();
    private boolean isEnable;
    Vector<Runnable> myQueue = new Vector<>();

    ThreadSafeQueue(){
        isEnable = true;
    }

    public void push(Runnable v){
        synchronized (monitor){
            isEnable();
            myQueue.add(v);
            monitor.notify();
        }
    }

    public Runnable pop(){
        synchronized (monitor){
            while (myQueue.isEmpty()){
                isEnable();
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException();
                }
            }
            Runnable res = myQueue.lastElement();
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
    private void isEnable(){
        if(!isEnable){
            throw new RuntimeException();
        }
    }
}

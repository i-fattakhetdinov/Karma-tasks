package Cache;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map;
import javafx.util.Pair;


public class Cache<K, V> {
    private final ReferenceQueue<Pair<K, V>> clearedReferences = new ReferenceQueue<>();
    private int maxCapacity = 10000;
    private final Map<K, SoftReference<Pair<K, V>>> storage =
            new LinkedHashMap<K, SoftReference<Pair<K, V>>>(maxCapacity, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(
                        Map.Entry<K, SoftReference<Pair<K, V>>> eldest) {
                    return size() > maxCapacity;
                }
            };

    public Cache() {
        Thread remover = new Remover(clearedReferences, storage);
        remover.start();
    }

    public Cache(int capacity) {
        maxCapacity = capacity;
        Thread remover = new Remover(clearedReferences, storage);
        remover.start();
    }

    public void put(K key, V value) {
        synchronized (storage) {
            storage.put(key, new SoftReference<>(new Pair<>(key, value), clearedReferences));
        }
    }

    public V getIfPresent(K key) {
        synchronized (storage) {
            SoftReference<Pair<K, V>> softRef = storage.get(key);
            if (softRef != null) {
                Pair<K, V> pair = softRef.get();
                assert pair != null;
                V value = pair.getValue();
                if (value == null) {
                    storage.remove(key);
                }
                return value;
            }
        }
        return null;
    }

    public void remove(K key) {
        synchronized (storage) {
            storage.remove(key);
        }
    }

    public void clear() {
        synchronized (storage) {
            storage.clear();
        }
    }

    private class Remover extends Thread {
        private final ReferenceQueue<Pair<K, V>> refQueue;
        private final Map<K, SoftReference<Pair<K, V>>> cache;

        public Remover(ReferenceQueue<Pair<K, V>> refQueue,
                Map<K, SoftReference<Pair<K, V>>> cache) {
            this.refQueue = refQueue;
            this.cache = cache;
            setDaemon(true);
        }

        public void run() {
            try {
                while (true) {
                    Reference<? extends Pair<K, V>> softRef = refQueue.remove();
                    if (softRef != null) {
                        Pair<K, V> pair = softRef.get();
                        K key = pair.getKey();
                        synchronized (cache) {
                            cache.remove(key);
                        }
                    }
                }
            } catch (InterruptedException e) {
            }
        }
    }
}
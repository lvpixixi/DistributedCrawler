import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class UniqueQueue <T> implements Iterable<T>{
    private BlockingQueue<T>        queue;
    private Set<T>                  set;
    private ReentrantLock           lock;

    public UniqueQueue() {
        this.set = new HashSet<>();
        this.queue = new ArrayBlockingQueue<>(1);
        this.lock = new ReentrantLock();
    }

    /**
     * 新元素来了后，先在set中判断以下
     * 1.   直接add该元素, 如果返回false直接跳过该队列操作
     * 2    如果无, 调用queue的offer操作
     * 2.1  如果返回false意味着队列已满.
     *      此时poll掉元素,set中也干掉queue poll掉的那个元素
     * 3.   重新赋值 offer的结果 (为true), 并返回
     * 
     */
    public boolean offer(T t) {
        lock.lock();
        boolean isOffer = false;
        try {
            if (!this.set.add(t)) // set中已有该元素，此次操作无效。
                return isOffer;
            isOffer = queue.offer(t);
            if (!isOffer) { //满了
                set.remove(queue.poll()); // 队列poll掉头元素，同时set remove掉存储的队列头元素
                isOffer = queue.offer(t);  //set已经在最初的判断中加入该新元素了
            }
        } finally {
            lock.unlock();
        }
        return isOffer;
    }
    
    public T poll() {
        lock.lock();
        T t = queue.poll();
        try {
        	set.remove(t); // 队列poll掉头元素，同时set remove掉存储的队列头元素
        } finally {
            lock.unlock();
        }
        return t;
    }

    /**
     * 清空UniqueQueue的方法
     */
    public void clear() {
        lock.lock();
        try {
            this.queue.clear();
            this.set.clear();
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        return this.queue.size();
    }

    /**
     * UniqueQueue的iterator方法
     */
    @Override
    public Iterator<T> iterator() {
        return this.queue.iterator();
    }
}
package cz.cvut.fit.smejkdo1.bak.acpf.astar;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * PriorityQueue sorted by external int.
 */
public class AStarQueue<T> {
    private final Map<T, Integer> priority;
    private final PriorityQueue<T> queue;

    public AStarQueue() {
        priority = new HashMap<>();
        queue = new PriorityQueue<>(1, (t1, t2) -> {
            int res = priority.get(t1) - priority.get(t2);
            return Integer.compare(res, 0);
        });
    }

    public boolean empty() {
        return queue.isEmpty();
    }

    public void add(T t, Integer d) {
        priority.put(t, d);
        queue.add(t);
    }

    public T pop() {
        priority.remove(queue.peek());
        return queue.poll();
    }

    public boolean contains(T t) {
        return priority.containsKey(t);
    }

    public void update(T t, Integer d) {
        priority.remove(t);
        queue.remove(t);
        this.add(t, d);
    }

}

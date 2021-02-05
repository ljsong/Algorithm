import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private static final int QUEUE_CAPACITY = 32;

    private Item[] queue;
    private int count = 0;
    // construct an empty randomized queue
    public RandomizedQueue() {
        queue = cast(new Object[QUEUE_CAPACITY]);
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return count == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return count;
    }

    private void resize(int capacity) {
        Item[] copy = cast(new Object[capacity]);
        for (int ix = 0; ix < count; ++ix) {
            copy[ix] = queue[ix];
        }

        queue = copy;
    }

    private <T> T cast(Object obj) {
        return (T) obj;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item enqueued can't be null item");
        }

        if (count == queue.length) {
            resize(queue.length * 2);
        }
        queue[count] = item;
        ++count;
    }

    // remove and return a random item
    public Item dequeue() {
        if (count == 0) {
            throw new NoSuchElementException("Empty queue");
        }

        int index = StdRandom.uniform(count);
        Item ret = queue[index];
        queue[index] = queue[count - 1];
        --count;

        if (count < queue.length / 4) {
            resize(queue.length / 2);
        }

        return ret;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (count == 0) {
            throw new NoSuchElementException("Empty queue");
        }

        int index = StdRandom.uniform(count);
        Item ret = queue[index];

        return ret;
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new QueueIterator();
    }

    private class QueueIterator implements Iterator<Item> {
        private final Item[] copy;
        private int idx = 0;

        public QueueIterator() {
            copy = cast(new Object[count]);
            for (int ix = 0; ix < count; ++ix) {
                copy[ix] = queue[ix];
            }

            StdRandom.shuffle(copy);
        }

        @Override
        public boolean hasNext() {
            return idx < copy.length;
        }

        @Override
        public Item next() {
            if (idx >= copy.length) {
                throw new NoSuchElementException("No more elements to iterate");
            }

            Item ret = copy[idx];
            ++idx;
            return ret;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Currently we don't support remove operation");
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<Integer> queue = new RandomizedQueue<>();
        queue.enqueue(5);
        queue.enqueue(7);
        queue.enqueue(8);
        StdOut.println("Size of current queue is: " + queue.size());
        StdOut.println("Deque an item, value is: " + queue.dequeue());
        StdOut.println("Sample an item, value is: " + queue.sample());
        StdOut.println("Size of current queue is: " + queue.size());

        Iterator<Integer> itr = queue.iterator();
        while (itr.hasNext()) {
            StdOut.print(itr.next() + " ");
        }
        StdOut.println();
    }
}


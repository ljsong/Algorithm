import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {

    private Node head, tail;
    private int count;
    // construct an empty deque
    public Deque() {
        head = null;
        tail = null;
        count = 0;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return count == 0;
    }

    // return the number of items on the deque
    public int size() {
        return count;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item added into queue can't be null item");
        }

        if (head == null) {
            head = new Node(item);
            tail = head;
        } else {
            head.prev = new Node(item, null, head);
            head = head.prev;
        }
        ++count;
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item added into queue can't be null item");
        }

        if (head == null) {
            head = new Node(item);
            tail = head;
        } else {
            tail.next = new Node(item, tail, null);
            tail = tail.next;
        }
        ++count;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (count == 0) {
            throw new NoSuchElementException("Empty deque");
        }

        Node ret = head;
        head = head.next;
        ret.next = null;
        if (head != null) {
            head.prev = null;
        } else {
            tail = null;
        }
        --count;

        return ret.val;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (count == 0) {
            throw new NoSuchElementException("Empty deque");
        }

        Node ret = tail;
        tail = tail.prev;
        ret.prev = null;
        if (tail != null) {
            tail.next = null;
        } else {
            head = null;
        }
        --count;

        return ret.val;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Node current = head;
        while (current != null) {
            sb.append(current.val.toString());
            if (current.next != null) {
                sb.append(", ");
            }
            current = current.next;
        }
        sb.append("]");

        return sb.toString();
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    private class Node {
        Item val;
        Node prev;
        Node next;

        public Node(Item v, Node p, Node n) {
            val = v;
            prev = p;
            next = n;
        }

        public Node(Item v) {
            this(v, null, null);
        }
    }

    private class DequeIterator implements Iterator<Item> {
        Node current = head;
        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public Item next() {
            if (current == null) {
                throw new NoSuchElementException("No more elements to iterate");
            }

            Node ret = current;
            current = current.next;

            return ret.val;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Currently we don't support remove operation");
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<Integer> deque = new Deque<>();
        deque.addFirst(5);
        deque.addFirst(7);
        StdOut.println(deque.removeLast());
        StdOut.println(deque.removeLast());
        StdOut.println(deque.size());
        StdOut.println(deque.toString());
    }

}

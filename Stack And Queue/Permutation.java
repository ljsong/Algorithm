import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.NoSuchElementException;

public class Permutation {
    public static void main(String[] args) {
        if (args.length < 1) {
            StdOut.println("Too few arguments!");
        }

        RandomizedQueue<String> queue = new RandomizedQueue<>();
        int count = Integer.parseInt(args[0]);
        String item = null;
        while (!StdIn.isEmpty()) {
            item = StdIn.readString();
            queue.enqueue(item);
        }

        while (count > 0) {
            StdOut.println(queue.dequeue());
            --count;
        }
    }
}

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.Queue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BurrowsWheeler {
    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    private static final int MAX_CHARS_PER_LINE = 4096;
    public static void transform() {
        StringBuilder sb = new StringBuilder();
        while (!BinaryStdIn.isEmpty()) {
            String line = BinaryStdIn.readString();
            sb.append(line);
        }

        String content = sb.toString();
        CircularSuffixArray cs = new CircularSuffixArray(content);
        for (int i = 0; i < cs.length(); ++i) {
            if (cs.index(i) == 0) {
                BinaryStdOut.write(i);
            }
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < cs.length(); ++i) {
            int pos = cs.index(i) == 0 ? cs.length() - 1 : (cs.index(i) - 1);
            result.append(content.charAt(pos));
            if (result.length() >= MAX_CHARS_PER_LINE) {
                BinaryStdOut.write(result.toString());
                result.delete(0, result.length());
            }
        }
        BinaryStdOut.write(result.toString());
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        StringBuilder sb = new StringBuilder();
        int first = 0;

        if (!BinaryStdIn.isEmpty()) {
            first = BinaryStdIn.readInt();
        }

        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            sb.append(c);
        }

        char[] chars = new char[sb.length()];
        sb.getChars(0, sb.length(), chars, 0);
        int[] next = new int[chars.length];
        Map<Character, Queue<Integer>> pos = new HashMap<>();
        Arrays.sort(chars);

        for (int i = 0; i < chars.length; ++i) {
            char c = sb.charAt(i);
            Queue<Integer> queue;
            if (pos.containsKey(c)) {
                queue = pos.get(c);
            } else {
                queue = new Queue<>();
                pos.put(c, queue);
            }

            queue.enqueue(i);
        }

        for (int i = 0; i < chars.length; ++i) {
            next[i] = pos.get(chars[i]).dequeue();
        }

        int count = 0;
        StringBuilder result = new StringBuilder();
        while (count < next.length) {
            result.append(chars[first]);
            if (result.length() >= MAX_CHARS_PER_LINE) {
                BinaryStdOut.write(result.toString());
                result.delete(0, result.length());
            }
            first = next[first];
            ++count;
        }
        BinaryStdOut.write(result.toString());
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            BurrowsWheeler.transform();
        } else if (args[0].equals("+")) {
            BurrowsWheeler.inverseTransform();
        }
    }
}

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.Queue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BurrowsWheeler {
    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        StringBuilder sb = new StringBuilder();
        while(!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            if (c == 0x0D || c == 0x0A) {
                break;
            }
            sb.append(c);
        }

        String content = sb.toString();
        CircularSuffixArray cs = new CircularSuffixArray(content);
        for (int i = 0; i < cs.length(); ++i) {
            if (cs.index(i) == 0) {
                BinaryStdOut.write(i);
            }
        }
        for (int i = 0; i < cs.length(); ++i) {
            int pos = cs.index(i) == 0 ? cs.length() - 1 : (cs.index(i) - 1);
            BinaryStdOut.write(content.charAt(pos));
        }
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
        while(!BinaryStdIn.isEmpty()) {
            char c= BinaryStdIn.readChar();
            if (c == 0x0A || c == 0x0D) {
                break;
            }
            sb.append(c);
        }

        char[] chars = sb.toString().toCharArray();
        int[] next = new int[chars.length];
        Map<Character, Queue<Character>> pos = new HashMap<>();
        Arrays.sort(chars);

        for (int i = 0; i < chars.length; ++i) {
            char c = sb.charAt(i);
            Queue<Character> queue;
            if (pos.containsKey(c)) {
                queue = pos.get(c);
            } else {
                queue = new Queue<>();
                pos.put(c, queue);
            }

            queue.enqueue((char)i);
        }

        for (int i = 0; i < chars.length; ++i) {
            next[i] = pos.get(chars[i]).dequeue();
        }

        int i = first;
        while(next[i] != first) {
            BinaryStdOut.write(chars[i]);
            i = next[i];
        }
        BinaryStdOut.write(sb.charAt(first));
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        BurrowsWheeler.inverseTransform();
    }
}

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.HashMap;
import java.util.Map;

public class MoveToFront {
    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        char[] dict = new char[256];
        Map<Character, Character> posMap = new HashMap<>();
        for (char i = 0; i < 256; ++i) {
            dict[i] = i;
            posMap.put((char)i, i);
        }

        while(!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            char pos = posMap.get(c);
            for (char i = pos; i > 0; --i) {
                dict[i] = dict[i - 1];
                posMap.put(dict[i], i);
            }
            dict[0] = c;
            posMap.put(c, (char)0);
            BinaryStdOut.write(pos);
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        char[] dict = new char[256];
        Map<Character, Character> posMap = new HashMap<>();
        for (char i = 0; i < 256; ++i) {
            dict[i] = i;
            posMap.put(i, i);
        }

        while(!BinaryStdIn.isEmpty()) {
            char pos = BinaryStdIn.readChar();
            char c = dict[pos];
            for (char i = pos; i > 0; --i) {
                dict[i] = dict[i - 1];
                posMap.put(dict[i], i);
            }
            dict[0] = c;
            posMap.put(c, (char)0);
            BinaryStdOut.write(c);
        }

        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        MoveToFront.encode();
        MoveToFront.decode();
    }
}

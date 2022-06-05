import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    private static final int MAX_CHARS_PER_LINE = 4096;

    private static final int R = 256;
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
        // pos is used to record the positions for character occurred in original string
        // the point should be noticed is that for specific character, if it appears more
        // than one time in original string, the positions will be stored in the second
        // dimensional array as they appear in the result of `transform`
        int[][] pos = new int[R][];
        sort(sb, chars, pos);

        for (int i = 0; i < chars.length; ++i) {
            int len = pos[chars[i]].length;
            int idx = pos[chars[i]][0];
            next[i] = pos[chars[i]][len - idx];
            pos[chars[i]][0]--;
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

    private static void sort(StringBuilder sb, char[] chars, int[][] pos) {
        int[] count = new int[R + 1];

        for (int i = 0; i < sb.length(); ++i) {
            count[sb.charAt(i) + 1]++;
        }

        for (int r = 0; r < R; ++r) {
            if (count[r + 1] != 0) {
                // the first element is used to indicate where we should put the incoming number
                pos[r] = new int[count[r + 1] + 1];
            }
        }

        for (int r = 0; r < R; ++r) {
            count[r + 1] += count[r];
        }

        for (int i = 0; i < sb.length(); ++i) {
            chars[count[sb.charAt(i)]++] = sb.charAt(i);
            pos[sb.charAt(i)][0]++;
            int idx = pos[sb.charAt(i)][0];
            pos[sb.charAt(i)][idx] = i;
        }
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

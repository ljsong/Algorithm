import edu.princeton.cs.algs4.Quick3string;
import edu.princeton.cs.algs4.Quick3way;

public class CircularSuffixArray {
    private String str;

    private String[] suffixes;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException("Argument can not be null");
        }

        str = s;
        char pivot = str.charAt(s.length() - 1);
        suffixes = new String[length()];

        for (int i = 0; i < length(); ++i) {
            suffixes[i] = str.substring(i);     // notice the implementation of substring!!
        }
        Quick3string.sort(suffixes);
    }


    // length of s
    public int length() {
        return str.length();
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= length()) {
            throw new IllegalArgumentException("index is out of range");
        }

        return length() - suffixes[i].length();
    }

    // unit testing (required)
    public static void main(String[] args) {
        CircularSuffixArray csa = new CircularSuffixArray("ABRACADABRA!");
        System.out.println(csa.index(4));
    }
}

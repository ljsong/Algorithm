public class CircularSuffixArray {
    private static final int CUTOFF        =  15;   // cutoff to insertion sort
    private static final int R             = 256;   // extended ASCII alphabet size
    private final String str;

    private final int[] suffixes;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException("Argument can not be null");
        }

        str = s;

        suffixes = new int[str.length()];
        if (str.length() > 0) {
            for (int i = 0; i < str.length(); ++i) {
                // here we only record the start index of this suffix string
                // eg. for original string ABRACADABRA! and suffix string BRACADABRA!A
                // we only record the start index 1 to represent this string
                suffixes[i] = i;
            }

            // here we use MSD to sort these circular suffix, LSD will slower than MSD
            // for amendments.txt, LSD costs more than 2000ms and MSD uses about 19ms
            // based on i7-9750 CPU
            sort(suffixes);
        }
    }

    /**
     * return the dth character of the ith suffix string in the original string
     * @param idx the sequence of the suffix string
     * @param d the index of the character in the suffix string
     * @return
     */
    private char charAt(int idx, int d) {
        d = (d + idx) % str.length();
        return str.charAt(d);
    }

    private void sort(int[] a) {
        int n = a.length;
        int[] aux = new int[n];
        sort(a, 0, n-1, 0, aux);
    }

    // sort from a[lo] to a[hi], starting at the dth character
    private void sort(int[] a, int lo, int hi, int d, int[] aux) {

        // cutoff to insertion sort for small subarrays
        if (hi <= lo + CUTOFF) {
            insertion(a, lo, hi, d);
            return;
        }

        // compute frequency counts
        int[] count = new int[R+2];
        for (int i = lo; i <= hi; i++) {
            int c = charAt(a[i], d);
            count[c+2]++;
        }

        // transform counts to indicies
        for (int r = 0; r < R+1; r++)
            count[r+1] += count[r];

        // distribute
        for (int i = lo; i <= hi; i++) {
            int c = charAt(a[i], d);
            aux[count[c+1]++] = a[i];
        }

        // copy back
        for (int i = lo; i <= hi; i++)
            a[i] = aux[i - lo];


        // recursively sort for each character (excludes sentinel -1)
        for (int r = 0; r < R; r++)
            sort(a, lo + count[r], lo + count[r+1] - 1, d+1, aux);
    }


    // insertion sort a[lo..hi], starting at dth character
    private void insertion(int[] a, int lo, int hi, int d) {
        for (int i = lo; i <= hi; i++)
            for (int j = i; j > lo && less(a[j], a[j-1], d); j--)
                exch(a, j, j-1);
    }

    // exchange a[i] and a[j]
    private void exch(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    // is v less than w, starting at character d
    private boolean less(int v, int w, int d) {
        // assert v.substring(0, d).equals(w.substring(0, d));
        for (int i = d; i < str.length(); i++) {
            if (charAt(v, i) < charAt(w, i)) return true;
            if (charAt(v, i) > charAt(w, i)) return false;
        }

        return false;
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

        return suffixes[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        CircularSuffixArray csa = new CircularSuffixArray("ABRACADABRA!");
        System.out.println(csa.index(1));
    }
}

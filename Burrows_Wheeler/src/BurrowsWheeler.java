import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        while(!BinaryStdIn.isEmpty()) {
            String content = BinaryStdIn.readString();
            content = content.substring(0, content.length() - 1);       // remove the CR chracter
            CircularSuffixArray cs = new CircularSuffixArray(content);
            for (int i = 0; i < cs.length(); ++i) {
                if (cs.index(i) == 0) {
                    BinaryStdOut.write(i);
                }
            }
            for (int i = 0; i < cs.length(); ++i) {
                System.out.println(cs.index(i));
                int pos = cs.index(i) == 0 ? cs.length() - 1 : (cs.index(i) - 1);
                BinaryStdOut.write(content.charAt(pos));
            }
        }
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        while(!BinaryStdIn.isEmpty()) {
            int first = BinaryStdIn.readInt();
        }
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        BurrowsWheeler.transform();
    }
}

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wordNet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        wordNet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        String outlier = null;
        int maxDistance = Integer.MIN_VALUE;

        for (var first : nouns) {
            var distance = 0;
            for (var second : nouns) {
                distance += wordNet.distance(first, second);
            }
            StdOut.println("The distance of " + first + " is: " + distance);
            if (distance > maxDistance) {
                maxDistance = distance;
                outlier = first;
            }
        }

        return outlier;
    }

    public static void main(String[] args) {
        // unit test code
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}

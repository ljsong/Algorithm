import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;

public class WordNet {
    private final ST<String, Integer> dict;
    private final Digraph wordGraph;
    private int cntOfNodes = 0;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException("" +
                    "Arguments for constructor can not be null");
        }

        dict = new ST<>();
        In in = new In(synsets);
        while (in.hasNextLine()) {
            String line = in.readLine();
            // separate id, synsets, definition
            String[] items = line.split(",");
            // separate synonyms
            String[] synonyms = items[1].split(" ");

            int nodeId = Integer.parseInt(items[0]);
            for (var syn : synonyms) {
                dict.put(syn, nodeId);
            }
            ++cntOfNodes;
        }
        in.close();

        wordGraph = new Digraph(cntOfNodes);

        in = new In(hypernyms);
        while(in.hasNextLine()) {
            String line = in.readLine();
            String[] items = line.split(",");
            int v = Integer.parseInt(items[0]);
            for (int ix = 1; ix < items.length; ++ix) {
                wordGraph.addEdge(v, Integer.parseInt(items[ix]));
            }
        }

        //TODO(ljsong): handle the exception that word net is not a rooted DAG
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return dict.keys();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException("" +
                    "Arguments for isNoun method can not be null");
        }

        return dict.contains(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("Either " + nounA +
                    " or " + nounB + " is not in current word net.");
        }

        return 0;
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("Either " + nounA +
                    " or " + nounB + " is not in current word net.");
        }

        return null;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        // unit testing code
    }
}

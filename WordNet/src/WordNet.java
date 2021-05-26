import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;

public class WordNet {
    private static final String FIELD_SEP = ",";
    private static final String WORD_SEP = " ";

    private final ST<String, Bag<Integer>> dict;
    private final String[] keys;
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException("" +
                    "Arguments for constructor can not be null");
        }

        int cntOfNodes = 0;
        boolean isRooted = false;
        dict = new ST<>();
        In in = new In(synsets);
        while (in.hasNextLine()) {
            String line = in.readLine();
            // separate id, synsets, definition
            String[] items = line.split(FIELD_SEP);
            // separate synonyms
            String[] synonyms = items[1].split(WORD_SEP);

            /* for some nouns, they may have different meanings
             * for example, peach can represent a fruit or a kind of color
             */
            int nodeId = Integer.parseInt(items[0]);
            for (var syn : synonyms) {
                Bag<Integer> item = dict.contains(syn) ? dict.get(syn) : new Bag<>();
                item.add(nodeId);
                dict.put(syn, item);
            }
            ++cntOfNodes;
        }
        in.close();

        keys = new String[cntOfNodes];      // construct the reverse lookup
        for (var name: dict.keys()) {
            for (var nodeId : dict.get(name)) {
                String val = keys[nodeId] == null ? "" : keys[nodeId];
                String delim = keys[nodeId] == null ? "" : " ";

                StringBuilder sb = new StringBuilder(val);
                sb.append(delim);
                sb.append(name);
                keys[nodeId] = sb.toString();
            }
        }

        Digraph wordGraph = new Digraph(cntOfNodes);
        in = new In(hypernyms);
        while (in.hasNextLine()) {
            String line = in.readLine();
            String[] items = line.split(FIELD_SEP);

            // ^\d+$ represents the root
            if (items.length == 1) {
                isRooted = true;
                continue;
            }

            int v = Integer.parseInt(items[0]);
            for (int ix = 1; ix < items.length; ++ix) {
                wordGraph.addEdge(v, Integer.parseInt(items[ix]));
            }
        }
        in.close();

        if (!isRooted) {
            throw new IllegalArgumentException("Current word net is not a rooted graph");
        }

        sap = new SAP(wordGraph);
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

        Bag<Integer> v = dict.get(nounA);
        Bag<Integer> w = dict.get(nounB);

        return sap.length(v, w);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("Either " + nounA +
                    " or " + nounB + " is not in current word net.");
        }

        Bag<Integer> v = dict.get(nounA);
        Bag<Integer> w = dict.get(nounB);

        int x = sap.ancestor(v, w);

        return keys[x];
    }

    // do unit testing of this class
    public static void main(String[] args) {
        // unit testing code
    }
}

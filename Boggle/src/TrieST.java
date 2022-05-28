import edu.princeton.cs.algs4.Queue;

import java.util.HashMap;
import java.util.Map;

public class TrieST<Value>{
    private static final int R = 26;        // extended ASCII

    private Node root;      // root of trie
    private int n;          // number of keys in trie

    private final Map<String, Node> prefixNodes;

    // R-way trie node
    private static class Node {
        private Object val;
        private Node[] next = new Node[R];
    }

    /**
     * Initializes an empty string symbol table.
     */
    public TrieST() {
        prefixNodes = new HashMap<>();
    }

    private int charAt(String key, int d) {
        char c = key.charAt(d);
        return c - 'A';
    }

    /**
     * Inserts the key-value pair into the symbol table, overwriting the old value
     * with the new value if the key is already in the symbol table.
     * If the value is {@code null}, this effectively deletes the key from the symbol table.
     * @param key the key
     * @param val the value
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public void put(String key, Value val) {
        if (key == null) throw new IllegalArgumentException("first argument to put() is null");
        else root = put(root, key, val, 0);
    }

    private Node put(Node x, String key, Value val, int d) {
        if (x == null) x = new Node();
        if (d == key.length()) {
            if (x.val == null) n++;
            x.val = val;
            return x;
        }
        int c = charAt(key, d);
        x.next[c] = put(x.next[c], key, val, d+1);
        return x;
    }

    /**
     * Returns the number of key-value pairs in this symbol table.
     * @return the number of key-value pairs in this symbol table
     */
    public int size() {
        return n;
    }

    /**
     * Is this symbol table empty?
     * @return {@code true} if this symbol table is empty and {@code false} otherwise
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    // Do not use the version implemented by textbook, it will
    // cost too much memory and time
    public boolean keysWithPrefix(String pre) {
        Node x = prefixNodes.get(pre);
        if (x != null) return true;
        if (pre.length() > 0) {
            x = prefixNodes.get(pre.substring(0, pre.length() - 1));
            if (x != null) {
                return existPrefix(x, pre, pre.length() - 1);
            }
        }
        return existPrefix(root, pre, 0);
    }

    private boolean existPrefix(Node x, String pre, int d) {
        if (x == null) return false;
        int i;
        for (i = d; i < pre.length(); i++) {
            x = x.next[pre.charAt(i) - 'A'];
            if (x == null) return false;
        }
        if (x.val != null) {
            prefixNodes.put(pre, x);
            return true;
        }
        for (char c = 0; c < R; c++) {
            if (x.next[c] != null) {
                prefixNodes.put(pre, x);
                return true;
            }
        }
        return false;
    }
}

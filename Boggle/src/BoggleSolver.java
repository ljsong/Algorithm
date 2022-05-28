import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class BoggleSolver {
    private final TrieST<Boolean> dictTrie = new TrieST<>();
    // score table to get the score quickly
    private final Map<Integer, Integer> scoreTable = new HashMap<>();

    // there will be a compilation error when using Trie's contains function
    private final Set<String> dictSet = new HashSet<>();

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        for (String word : dictionary) {
            dictTrie.put(word, Boolean.TRUE);
            dictSet.add(word);
        }

        scoreTable.put(3, 1);
        scoreTable.put(4, 1);
        scoreTable.put(5, 2);
        scoreTable.put(6, 3);
        scoreTable.put(7, 5);
        scoreTable.put(8, 11);
    }

    private Iterable<Position> getAdjacent(BoggleBoard board, int i, int j) {
        Bag<Position> positions = new Bag<>();
        if (i - 1 >= 0) {
            if (j - 1 >= 0) {
                positions.add(new Position(i - 1, j - 1));
            }
            if (j + 1 < board.cols()) {
                positions.add(new Position(i - 1, j + 1));
            }

            positions.add(new Position(i - 1, j));
        }

        if (j - 1 >= 0) {
            positions.add(new Position(i, j - 1));
        }
        if (j + 1 < board.cols()) {
            positions.add(new Position(i, j + 1));
        }

        if (i + 1 < board.rows()) {
            if (j - 1 >= 0) {
                positions.add(new Position(i + 1, j - 1));
            }
            if (j + 1 < board.cols()) {
                positions.add(new Position(i + 1, j + 1));
            }
            positions.add(new Position(i + 1, j));
        }

        return positions;
    }

    private void traverseBoard(BoggleBoard board, Set<String> validWords) {
        boolean visited[][] = new boolean[board.rows()][board.cols()];

        for (int row = 0; row < board.rows(); ++row) {
            for (int col = 0; col < board.cols(); ++col) {
                StringBuilder prefix = new StringBuilder();
                collect(board, row, col, visited, validWords, prefix);
            }
        }
    }

    private void collect(BoggleBoard board, int row, int col, boolean[][] visited,
                         Set<String> validWords, StringBuilder prefix) {
        if (visited[row][col]) return;
        visited[row][col] = true;

        char c = board.getLetter(row, col);
        prefix.append(c);
        String str = prefix.toString().replace("Q", "QU");

        boolean hasPrefix = dictTrie.keysWithPrefix(str);

        if (hasPrefix) {
            for (Position pos : getAdjacent(board, row, col)) {
                if (str.length() >= 3 && dictSet.contains(str)) {
                    validWords.add(str);
                }
                collect(board, pos.x(), pos.y(), visited, validWords, prefix);
            }
        }

        visited[row][col] = false;
        prefix.deleteCharAt(prefix.length() - 1);
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        Set<String> validWords = new HashSet<>();
        traverseBoard(board, validWords);

        return validWords;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (!dictSet.contains(word)) {
            return 0;
        }

        int len = word.length();
        if (len < 3) {
            return 0;
        } else if (len > 8) {
            return 11;
        } else {
            return scoreTable.get(len);
        }
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }

    private final class Position {
        private final int x;
        private final int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int x() {
            return x;
        }

        public int y() {
            return y;
        }

        @Override
        public String toString() {
            return String.format("(%d, %d)", x, y);
        }
    }
}

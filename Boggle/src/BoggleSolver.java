import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class BoggleSolver {
    // with this map, we can easily check some words with characters not appeared on board
    private final Map<String, Set<Character>> dictMap = new HashMap<>();
    private final Map<Character, Bag<Position>> position = new HashMap<>();
    // characters appeared on board
    private final Set<Character> validc = new HashSet<>();

    private final Set<String> allWords = new HashSet<>();
    // score table to get the score quickly
    private final Map<Integer, Integer> scoreTable = new HashMap<>();
    // characters and its neighbors on board
    private final Map<Position, Map<Character, Bag<Position>>> neighbors = new HashMap<>();
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        for (String word : dictionary) {
            Set<Character> set = new HashSet<>();
            for (int i = 0; i < word.length(); ++i) {
                char c = word.charAt(i);
                if (c != 'Q') {
                    set.add(c);
                } else {
                    // it is only valid when Q and U appear continuously
                    if (i + 1 < word.length() && word.charAt(i + 1) == 'U') {
                        set.add(c);
                        ++i;        // skip U behind Q
                    } else {
                        set.clear();    // treat single Q as illegal character
                        break;
                    }
                }
            }
            dictMap.put(word, set);
            allWords.add(word);
        }

        scoreTable.put(3, 1);
        scoreTable.put(4, 1);
        scoreTable.put(5, 2);
        scoreTable.put(6, 3);
        scoreTable.put(7, 5);
        scoreTable.put(8, 11);
    }

    private boolean findCharacter(int idx, Bag<Position> positions, String word, Set<Position> parents) {
        if (positions == null) {
            return false;
        }

        if (idx == word.length() - 1) {
            for (Position lastPosition : positions) {
                if (!parents.contains(lastPosition)) {
                    return true;
                }
            }
            return false;
        }

        char next = word.charAt(idx + 1);
        boolean ret = false;

        for (Position pos : positions) {
            Map<Character, Bag<Position>> chars = neighbors.get(pos);
            if (!chars.containsKey(next)) {
                // next character is not a neighbor of current character
                continue;
            }
            if (parents.contains(pos)) {
                // this position has been used
                continue;
            }

            parents.add(pos);
            Bag<Position> nextPositions = chars.get(next);
            ret |= findCharacter(idx + 1, nextPositions, word, parents);
            parents.remove(pos);
        }

        return ret;
    }

    private boolean findWord(String word, BoggleBoard board) {
        if (word.length() <= 2) {
            return false;
        }

        Set<Character> chars = dictMap.get(word);
        // empty chars represents there are some illegal character in this word
        // such as Q due to Q must bind with U when it appears on board
        if (chars.isEmpty() || !validc.containsAll(chars)) {
            return false;
        }

        int totalChars = board.rows() * board.cols();
        if (word.length() / 2 > totalChars) {
            return false;
        }

        Set<Position> parents = new HashSet<>();
        Bag<Position> positions = position.get(word.charAt(0));
        // here, we can guarantee that all Q has a U behind it
        // so we replace QU with Q to make it conveniently process
        String replacement = word.replaceAll("QU", "Q");
        return findCharacter(0, positions, replacement, parents);
    }

    private void putCharPosition(BoggleBoard board, Map<Character, Bag<Position>> chars, int x, int y) {
        char c = board.getLetter(x, y);
        Bag<Position> positions = null;

        if (chars.containsKey(c)) {
            positions = chars.get(c);
        } else {
            positions = new Bag<>();
            chars.put(c, positions);
        }

        positions.add(new Position(x, y));
    }

    private void constructNeighbors(BoggleBoard board) {
        for (int i = 0; i < board.rows(); ++i) {
            for (int j = 0; j < board.cols(); ++j) {
                Position p = new Position(i, j);
                Map<Character, Bag<Position>> chars = new HashMap<>();

                if (i - 1 >= 0) {
                    if (j - 1 >= 0) {
                        putCharPosition(board, chars, i - 1, j - 1);
                    }
                    if (j + 1 < board.cols()) {
                        putCharPosition(board, chars, i - 1, j + 1);
                    }
                    putCharPosition(board, chars, i - 1, j);
                }

                if (j - 1 >= 0) {
                    putCharPosition(board, chars, i, j - 1);
                }
                if (j + 1 < board.cols()) {
                    putCharPosition(board, chars, i, j + 1);
                }

                if (i + 1 < board.rows()) {
                    if (j - 1 >= 0) {
                        putCharPosition(board, chars, i + 1, j - 1);
                    }
                    if (j + 1 < board.cols()) {
                        putCharPosition(board, chars, i + 1, j + 1);
                    }
                    putCharPosition(board, chars, i + 1, j);
                }

                neighbors.put(p, chars);
            }
        }
    }

    private void preprocess(BoggleBoard board) {
        neighbors.clear();
        validc.clear();
        position.clear();

        for (int i = 0; i < board.rows(); ++i) {
            for (int j = 0; j < board.cols(); ++j) {
                char c = board.getLetter(i, j);
                Bag<Position> v;
                if (position.containsKey(c)) {
                    v = position.get(c);
                } else {
                    v = new Bag<>();
                }
                Position p = new Position(i, j);
                v.add(p);
                position.put(c, v);
                validc.add(c);
            }
        }

        constructNeighbors(board);
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        Bag<String> validWords = new Bag<>();

        preprocess(board);
        for (String word : allWords) {
            if (findWord(word, board)) {
                validWords.add(word);
            }
        }

        return validWords;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (!allWords.contains(word)) {
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

    private class Pair<U, V> {
        private final U first;
        private final V second;

        public Pair(U x, V y) {
            first = x;
            second = y;
        }

        public final U first() {
            return this.first;
        }

        public final V second() {
            return this.second;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            Pair p = (Pair) obj;
            if (first == p.first && second == p.second) {
                return true;
            }

            return false;
        }

        @Override
        public int hashCode() {
            return 31 * first.hashCode() + second.hashCode();
        }

        @Override
        public String toString() {
            return String.format("(%d, %d)", first, second);
        }
    }

    private final class Position {
        private final Pair<Integer, Integer> pair;

        public Position(int x, int y) {
            pair = new Pair<>(x, y);
        }

        public int x() {
            return pair.first();
        }

        public int y() {
            return pair.second();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            Position p = (Position) obj;
            return p.pair.equals(this.pair);
        }

        @Override
        public int hashCode() {
            return pair.hashCode();
        }

        @Override
        public String toString() {
            return String.format("%s", pair);
        }
    }
}

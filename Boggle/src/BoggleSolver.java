import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class BoggleSolver {
    private final String[] dict;

    // with this map, we can easily check some words with characters not appeared on board
    private Map<String, Set<Character>> dictMap = new HashMap<>();
    private Bag<String> words = new Bag<>();
    private Map<Character, Bag<Position>> position = new HashMap<>();
    // characters appeared on board
    private Set<Character> validc = new HashSet<>();
    //score table to get the score quickly
    private Map<Integer, Integer> scoreTable = new HashMap<>();
    // characters and its neighbors on board
    private Map<Position, Map<Character, Position>> neighbors = new HashMap<>();
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        dict = dictionary;
        for (String word : dict) {
            Set<Character> set = new HashSet<>();
            for (int i = 0; i < word.length(); ++i) {
                set.add(word.charAt(i));
            }
            dictMap.put(word, set);
        }

        scoreTable.put(3, 1);
        scoreTable.put(4, 1);
        scoreTable.put(5, 2);
        scoreTable.put(6, 3);
        scoreTable.put(7, 5);
        scoreTable.put(8, 11);
    }

    private boolean isNeighbor(char c, Bag<Character> neighbors) {
        for (char item : neighbors) {
            if (c == item) {
                return true;
            }
        }

        return false;
    }

    private boolean findPath(Position p, String word) {
        int i = 0;
        Position parent = null;
        System.out.println(String.format("word = %s", word));
        while(i < word.length() - 1) {
            char next = word.charAt(i + 1);
            System.out.println(p);
            Map<Character, Position> chars = neighbors.get(p);
            System.out.println(chars);
            System.out.println(String.format("parent = %s, chars.get(next) = %s", parent, chars.get(next)));
            System.out.println(String.format("%b", (parent == chars.get(next))));
            if (chars.containsKey(next) && parent != chars.get(next)) {
                parent = p;
                p = chars.get(next);
            } else {
                return false;
            }
            ++i;
        }

        return true;
    }
    private boolean simulate(String word, BoggleBoard board) {
        if (word.length() <= 2) {
            return false;
        }

        if (!validc.containsAll(dictMap.get(word))) {
            return false;
        }

        Bag<Position> positions = position.get(word.charAt(0));
        for (Position p : positions) {
            if (findPath(p, word)) {
                return true;
            }
        }

        return false;
    }

    private void constructNeighbors(BoggleBoard board) {
        for (int i = 0; i < board.rows(); ++i) {
            for (int j = 0; j < board.cols(); ++j) {
                Position p = new Position(i, j);
                Map<Character, Position> chars = new HashMap<>();

                if (i - 1 >= 0) {
                    if (j - 1 >= 0) {
                        chars.put(board.getLetter(i - 1, j - 1), new Position(i - 1, j - 1));
                    }
                    if (j + 1 < board.cols()) {
                        chars.put(board.getLetter(i - 1, j + 1), new Position(i - 1, j + 1));
                    }
                    chars.put(board.getLetter(i - 1, j), new Position(i - 1, j));
                }

                if (j - 1 >= 0) {
                    chars.put(board.getLetter(i, j - 1), new Position(i , j - 1));
                }
                if (j + 1 < board.cols()) {
                    chars.put(board.getLetter(i, j + 1), new Position(i, j + 1));
                }

                if (i + 1 < board.rows()) {
                    if (j - 1 >= 0) {
                        chars.put(board.getLetter(i + 1, j - 1), new Position(i + 1, j - 1));
                    }
                    if (j + 1 < board.cols()) {
                        chars.put(board.getLetter(i + 1, j + 1), new Position(i + 1, j + 1));
                    }
                    chars.put(board.getLetter(i + 1, j), new Position(i + 1, j));
                }

                neighbors.put(p, chars);
            }
        }
        System.out.println(neighbors);
    }

    private void preprocess(BoggleBoard board) {
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
        long start = System.currentTimeMillis();
        preprocess(board);
        for (String word : dict) {
            if (simulate(word, board)) {
                words.add(word);
            }
        }

        long end = System.currentTimeMillis();
        System.out.println("Cost time: " + (end - start) + "ms.");

        return words;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
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

    private class Pair<U, V>{
        private U first;
        private V second;

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
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Pair p = (Pair) o;
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
        private Pair<Integer, Integer> pair;

        public Position(int x, int y) {
            pair = new Pair(x, y);
        }

        public int x() {
            return pair.first();
        }

        public int y() {
            return pair.second();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Position p = (Position) o;
            return p.pair == this.pair;
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

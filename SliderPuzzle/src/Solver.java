import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.LinkedList;

public class Solver {
    // define the search node by its definition
    private class SearchNode implements Comparable<SearchNode> {
        Board       board;
        int         moves;
        int         priority;
        SearchNode  previous;

        public SearchNode(Board b, int moves, SearchNode prev) {
            board = b;
            this.moves = moves;
            priority = moves + board.manhattan();
            previous = prev;
        }

        @Override
        public int compareTo(SearchNode that) {
            if (that == null) {
                return 1;
            }

            return priority - that.priority;
        }
    }

    private final Board board;
    private final LinkedList<Board> solutions = new LinkedList<>();
    private final boolean solved;
    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException("board can not be null");
        }

        board = initial;
        solved = search();
    }

    private boolean search() {
        var ret = false;
        MinPQ<SearchNode> pq = new MinPQ<>(), twinPq = new MinPQ<>();
        SearchNode current;
        var node = new SearchNode(board, 0, null);
        var twinNode = new SearchNode(board.twin(), 0, null);
        pq.insert(node);
        twinPq.insert(twinNode);

        do {
            current = pq.delMin();
            SearchNode twinCurrent = twinPq.delMin();

            ret = twinCurrent.board.isGoal() || current.board.isGoal();
            if (ret) {
                ret = !twinCurrent.board.isGoal() && current.board.isGoal();
                break;
            }

            extend(current, pq);
            extend(twinCurrent, twinPq);
        } while (!pq.isEmpty() && !twinPq.isEmpty());

        while (ret && current != null) {
            solutions.addFirst(current.board);
            current = current.previous;
        }

        return ret;
    }

    private void extend(SearchNode current, MinPQ<SearchNode> queue) {
        var b = current.board;
        var prevB = current.previous == null ? null : current.previous.board;

        for (var item: b.neighbors()) {
            if (item.equals(prevB)) {
                continue;
            }

            queue.insert(new SearchNode(item, current.moves + 1, current));
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return solved;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return solved ? solutions.size() - 1 : -1;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (solved) {
            return solutions;
        }

        return null;
    }

    // test client (see below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}

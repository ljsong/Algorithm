import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class Board {
    private int n = 0, blank_pos;
    private int tiles[][];

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        n = tiles.length;
        for (int ix = 0; ix < tiles.length; ++ix) {
            this.tiles[ix] = new int[tiles[ix].length];
            for (int jx = 0; jx < tiles[ix].length; ++jx) {
                this.tiles[ix][jx] = tiles[ix][jx];
                if(tiles[ix][jx] == 0) {
                    blank_pos = ix * n + jx + 1;
                }
            }
        }
    }

    // string representation of this board
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(n + "\n");
        for (int ix = 0; ix < n; ++ix) {
            for (int jx = 0; jx < n; ++ix) {
                String last = jx == n - 1 ? "" : " ";
                sb.append(tiles[ix][jx] + last);
            }
        }

        return  sb.toString();
    }

    // board dimension n
    public int dimension() {
        return n;
    }

    // number of tiles out of place
    public int hamming() {
        int cnt_of_miss = 0;
        for (int ix = 0; ix < n; ++ix) {
            for(int jx = 0; jx < n; ++jx) {
                if (tiles[ix][jx] != 0 && tiles[ix][jx] != ix * n + jx + 1) {
                    ++cnt_of_miss;
                }
            }
        }

        return cnt_of_miss;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int ham_distance = 0;
        for (int ix = 0; ix < n; ++ix) {
            for (int jx = 0; jx < n; ++jx) {
                if (tiles[ix][jx] == 0) {
                    continue;
                }

                int num = tiles[ix][jx] - 1;
                int x = num / n;
                int y = num % n;
                ham_distance += (x - ix + y - jx);
            }
        }

        return ham_distance;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hamming() == 0;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (!(y instanceof Board)) {
            return false;
        }

        Board that = (Board) y;
        if (that.n != n) {
            return false;
        }

        for (int ix = 0; ix < n; ++ix) {
            for(int jx = 0; jx < n; ++jx) {
                if (tiles[ix][jx] != that.tiles[ix][jx]) {
                    return false;
                }
            }
        }

        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        return () -> new BoardIterator();
    }

    private class BoardIterator implements Iterator<Board> {
        // We will use the sequence: UP, LEFT, BOTTOM, RIGHT
        private int blank_row;
        private int blank_col;
        private int max_steps = 4;

        public BoardIterator() {
            blank_row = (blank_pos - 1) / n;
            blank_col = (blank_pos - 1) % n;
        }

        @Override
        public boolean hasNext() {
            return max_steps == 0;
        }

        @Override
        public Board next() {
            Board l = leftBoard();
            Board b = bottomBoard();
            Board r = rightBoard();
            Board u = upBoard();

            return l == null ? (b == null ? (r == null ? u : r) : b) : l;
        }

        private Board leftBoard() {
            Board b = null;
            if (max_steps < 4) {
                return b;
            }

            if (blank_col > 0) {
                swap(blank_row, blank_col - 1);
                b = new Board(tiles);
                swap(blank_row, blank_col - 1);
            }
            --max_steps;

            return b;
        }

        private Board bottomBoard() {
            Board b = null;
            if (max_steps < 3) {
                return b;
            }

            if (blank_row < n - 1) {
                swap(blank_row + 1, blank_col);
                b = new Board(tiles);
                swap(blank_row + 1, blank_col);
            }
            --max_steps;

            return b;
        }

        private Board rightBoard() {
            Board b = null;

            if (max_steps < 2) {
                return b;
            }

            if (blank_col < n - 1) {
                swap(blank_row, blank_col + 1);
                b = new Board(tiles);
                swap(blank_row, blank_col + 1);
            }
            --max_steps;

            return b;

        }

        private Board upBoard() {
            Board b = null;
            if (max_steps < 1) {
                return b;
            }

            if (blank_row > 0) {
                swap(blank_row - 1, blank_col);
                b = new Board(tiles);
                swap(blank_row - 1, blank_col);
            }
            --max_steps;

            return b;
        }

        private void swap(int row, int col) {
            int tmp = tiles[blank_row][blank_col];
            tiles[blank_row][blank_col] = tiles[row][col];
            tiles[row][col] = tmp;
        }
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        return null;
    }

    // unit testing (not graded)
    public static void main(String[] args) {

    }

}

import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class Board {
    private int n = 0, blank_pos;
    private int tiles[][];

    private enum DIRECTION {
        INVALID(0),
        LEFT(1),
        BOTTOM(2),
        RIGHT(3),
        UP(4);

        private int index;
        DIRECTION(int ind) {
            this.index = ind;
        }

        public int getIndex() {
            return index;
        }

        public static DIRECTION getDirection(int ind) {
            for (DIRECTION direction: DIRECTION.values()) {
                if (direction.getIndex() == ind) {
                    return direction;
                }
            }

            return INVALID;
        }
    };

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        n = tiles.length;
        this.tiles = new int[tiles.length][];

        for (int ix = 0; ix < tiles.length; ++ix) {
            this.tiles[ix] = new int[tiles[ix].length];
        }
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
            for (int jx = 0; jx < n; ++jx) {
                String last = jx == n - 1 ? "\n" : " ";
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
        int distance = 0;
        for (int ix = 0; ix < n; ++ix) {
            for (int jx = 0; jx < n; ++jx) {
                if (tiles[ix][jx] == 0) {
                    continue;
                }

                int num = tiles[ix][jx] - 1;
                int x = num / n;
                int y = num % n;
                distance += Math.abs(x - ix) + Math.abs(y - jx);
            }
        }

        return distance;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hamming() == 0;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == this) {
            return true;
        }

        if (y == null || !(y instanceof Board)) {
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
        return () -> new NeighborIterator();
    }

    private class NeighborIterator implements Iterator<Board> {
        // We will use the sequence: UP, LEFT, BOTTOM, RIGHT
        private int ind = 0;
        private int blank_row, blank_col;
        private ArrayList<Board> neighbors = new ArrayList<>(4);
        private static final int MAX_STEPS = 4;

        public NeighborIterator() {
            blank_row = (blank_pos - 1) / n;
            blank_col = (blank_pos - 1) % n;

            for (int steps = 1; steps <= MAX_STEPS; ++steps) {
                Board b = twin(blank_row, blank_col, DIRECTION.getDirection(steps));
                if (b == null) {
                    continue;
                }

                neighbors.add(b);
            }
        }

        @Override
        public boolean hasNext() {
            return ind < neighbors.size();
        }

        @Override
        public Board next() {
            return neighbors.get(ind++);
        }
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        if (tiles[0][0] != 0) {
            if (tiles[0][1] != 0) {
                return twin(0, 0, DIRECTION.RIGHT);
            } else if (tiles[1][0] != 0) {
                return twin(0, 0, DIRECTION.BOTTOM);
            }
        }

        return twin(1, 1, DIRECTION.UP);
    }

    /*
     * @row: row index
     * @col: col index
     * @direction: which neighbor we wanna exchange, below are acceptable value
     *             1, left
     *             2, bottom
     *             3, right
     *             4, up
     */
    private Board twin(int row, int col, DIRECTION direction) {
        return switch(direction) {
            case LEFT ->  col <= 0 ? null : nextBoard(row, col, direction);
            case BOTTOM -> row >= n - 1 ? null : nextBoard(row, col, direction);
            case RIGHT -> col >= n - 1 ? null : nextBoard(row, col, direction);
            case UP -> row <= 0 ? null : nextBoard(row, col, direction);
            case INVALID -> null;
        };
    }

    private Board nextBoard(int row, int col, DIRECTION direction) {
        swap(row, col, direction);
        var ret = new Board(tiles);
        swap(row, col, direction);

        return ret;
    }

    private void swap(int row, int col, DIRECTION direction) {
        var new_row = switch(direction) {
            case BOTTOM -> row + 1;
            case UP -> row - 1;
            default -> row;
        };
        var new_col = switch(direction) {
            case LEFT -> col - 1;
            case RIGHT -> col + 1;
            default -> col;
        };

        var tmp = tiles[row][col];
        tiles[row][col] = tiles[new_row][new_col];
        tiles[new_row][new_col] = tmp;
    }

    // unit testing (not graded)
    public static void main(String[] args) {

    }

}

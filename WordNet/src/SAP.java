import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.ST;

public class SAP {
    private final Digraph graph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException("Graph can't be null reference");
        }

        graph = G;
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (v == w) {
            return 0;
        }

        ST<Integer, Integer> firstPaths = new ST<>();
        ST<Integer, Integer> secondPaths = new ST<>();

        shortestPath(v, firstPaths);
        shortestPath(w, secondPaths);

        int minDistance = Integer.MAX_VALUE;
        for (var u : secondPaths.keys()) {
            if (!firstPaths.contains(u)) {
                continue;
            }

            var length = firstPaths.get(u) + secondPaths.get(u);
            minDistance = length < minDistance ? length : minDistance;
        }

        return minDistance == Integer.MAX_VALUE ? -1 : minDistance;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (v == w) {
            return v;
        }

        ST<Integer, Integer> firstPaths = new ST<>();
        ST<Integer, Integer> secondPaths = new ST<>();

        shortestPath(v, firstPaths);
        shortestPath(w, secondPaths);

        int commonNode = -1, minDistance = Integer.MAX_VALUE;
        for (var u : secondPaths.keys()) {
            if (!firstPaths.contains(u)) {
                continue;
            }

            var length = firstPaths.get(u) + secondPaths.get(u);
            if (length < minDistance) {
                minDistance = length;
                commonNode = u;
            }
        }

        return commonNode;
    }

    private void shortestPath(int v, ST<Integer, Integer> paths) {
        Queue<Integer> queue = new Queue<>();
        boolean[] marked = new boolean[graph.V()];

        queue.enqueue(v);
        paths.put(v, 0);    // the length of path to itself is 0
        while(!queue.isEmpty()) {
            var u = queue.dequeue();
            for (var w : graph.adj(u)) {
                if (marked[w]) {
                    continue;
                }

                marked[w] = true;
                paths.put(w, paths.get(u) + 1);
                queue.enqueue(w);
            }
        }
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        var minDistance = Integer.MAX_VALUE;
        for (var firstNode : v) {
            for (var secondNode : w) {
                var pathLength = length(firstNode, secondNode);
                minDistance = (pathLength != -1 && pathLength < minDistance)
                        ? pathLength : minDistance;
            }
        }

        return minDistance == Integer.MAX_VALUE ? -1 : minDistance;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        var minDistance = Integer.MAX_VALUE;
        int minFirst = -1, minSecond = -1;

        for (var firstNode : v) {
            for (var secondNode : w) {
                var pathLength = length(firstNode, secondNode);
                if (pathLength == -1) {
                    continue;
                }

                if (pathLength < minDistance) {
                    minFirst = firstNode;
                    minSecond = secondNode;
                    minDistance = pathLength;
                }
            }
        }

        return minDistance == Integer.MAX_VALUE ? -1 : ancestor(minFirst, minSecond);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        // unit test code
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}

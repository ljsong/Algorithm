import edu.princeton.cs.algs4.*;

import java.util.HashMap;

public class BaseballElimination {
    private static final int SOURCE_VERTEX = 0;
    private final int cntOfTeams;
    private HashMap<String, Integer> teamIndexes;
    private int[] w;
    private int[] l;
    private int[] r;
    private int[][] g;

    public BaseballElimination(String filename) {
        In fileIn = new In(filename);
        int index = 0;      // 0 is used to represent source vertex

        if (fileIn.hasNextLine()) {
            cntOfTeams = fileIn.readInt();
            w = new int[cntOfTeams];
            l = new int[cntOfTeams];
            r = new int[cntOfTeams];
            g = new int[cntOfTeams][cntOfTeams];
        } else {
            cntOfTeams = 0;
        }

        while(fileIn.hasNextLine()) {
            String line = fileIn.readLine();
            String[] contents = line.split(" ");
            teamIndexes.put(contents[0], index);
            w[index] = Integer.parseInt(contents[1]);
            l[index] = Integer.parseInt(contents[2]);
            r[index] = Integer.parseInt(contents[3]);
            for (int ix = 0; ix < cntOfTeams; ++ix) {
                g[index][ix] = Integer.parseInt(contents[4 + ix]);
            }
            ++index;
        }
    }

    public int numberOfTeams() {
        return cntOfTeams;
    }

    public Iterable<String> teams() {
        return teamIndexes.keySet();
    }

    public int wins(String team) {
        validateTeam(team);

        return w[teamIndexes.get(team)];
    }

    public int losses(String team) {
        validateTeam(team);

        return l[teamIndexes.get(team)];
    }

    public int remaining(String team) {
        validateTeam(team);

        return r[teamIndexes.get(team)];
    }

    public int against(String team1, String team2) {
        validateTeam(team1, team2);

        int row = teamIndexes.get(team1);
        int col = teamIndexes.get(team2);
        return g[row][col];
    }

    public boolean isEliminated(String team) {
        validateTeam(team);

        // trivial elimination
        int index = teamIndexes.get(team);
        for (String t : teamIndexes.keySet()) {
            int rivalIndex = teamIndexes.get(t);
            if (index != rivalIndex && w[index] + r[index] < w[rivalIndex]) {
                return true;
            }
        }

        // nontrivial elimination
        runFlowNetwork(index);

        return false;
    }

    public Iterable<String> certificateOfElimination(String team) {
        validateTeam(team);
    }

    private void validateTeam(String... teams) {
        for (String team : teams) {
            if (!teamIndexes.containsKey(team)) {
                throw new IllegalArgumentException(String.format("Team %s is not existed!", team));
            }
        }
    }

    private void runFlowNetwork(int v) {
        // cnt of vertices: C(cntOfTeams - 1, 2) + cntOfTeams - 1 + 2
        int cntOfVertices = (factorial(cntOfTeams - 1) << 1) + cntOfTeams + 1;
        HashMap<Integer, Integer> verticeMap = new HashMap<>();

        // In flow network, we use 0 to represent source point
        int n = 1;
        for (int ix = 0; ix < cntOfTeams; ++ix) {
            if (ix != v) {
                verticeMap.put(ix, n++);
            }
        }

        // use `cntOfVertices` to represent the sink point
        for (int ix = 0; ix < cntOfTeams; ++ix) {
            if (ix == v) {
                continue;
            }
            for (int jx = ix + 1; jx < cntOfTeams; ++jx) {
                int dest = (ix + 1) * cntOfTeams + jx - 1;
                verticeMap.put(dest, n++);
            }
        }

        FlowNetwork network = buildNetwork(v, verticeMap, cntOfVertices);
        FordFulkerson ff = new FordFulkerson(network, 0, cntOfVertices - 1);
    }

    private FlowNetwork buildNetwork(int v, HashMap<Integer, Integer> verticeMap, int cntOfVertices) {
        FlowNetwork network = new FlowNetwork(cntOfVertices);

        for (int ix = 0; ix < cntOfTeams; ++ix) {
            if (v == ix) {
                continue;
            }

            FlowEdge e1 = new FlowEdge(verticeMap.get(ix), cntOfVertices - 1, w[v] + r[v] - w[ix]);
            network.addEdge(e1);
            for (int jx = ix + 1; jx < cntOfTeams; ++jx) {
                int indx = (ix + 1) * cntOfTeams + jx - 1;
                int dest = verticeMap.get(indx);
                FlowEdge e2 = new FlowEdge(SOURCE_VERTEX, dest, g[ix][jx]);
                network.addEdge(e2);
                FlowEdge e3 = new FlowEdge(dest, ix, Double.POSITIVE_INFINITY);
                network.addEdge(e3);
                FlowEdge e4 = new FlowEdge(dest, jx, Double.POSITIVE_INFINITY);
                network.addEdge(e4);
            }
        }

        return network;
    }

    private int factorial(int n) {
        if (n < 0) {
            return 0;
        }

        if (n == 0 || n == 1) {
            return 1;
        }

        int product = 1;
        while(n != 0) {
            product *= n;
            --n;
        }

        return product;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}

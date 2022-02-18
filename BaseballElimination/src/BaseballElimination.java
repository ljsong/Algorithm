import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BaseballElimination {
    private static final int SOURCE_VERTEX = 0;
    private final int cntOfTeams;
    private final HashMap<String, Integer> teamIndexes = new HashMap<>();
    private final List<String> certOfElimination;
    private int[] wins;
    private int[] losses;
    private int[] remains;
    private int[][] games;

    public BaseballElimination(String filename) {
        In fileIn = new In(filename);
        int index = 0;      // 0 is used to represent source vertex

        if (fileIn.hasNextLine()) {
            String line = fileIn.readLine();
            cntOfTeams = Integer.parseInt(line);
            wins = new int[cntOfTeams];
            losses = new int[cntOfTeams];
            remains = new int[cntOfTeams];
            games = new int[cntOfTeams][cntOfTeams];
        } else {
            cntOfTeams = 0;
        }

        while (fileIn.hasNextLine()) {
            String line = fileIn.readLine().strip();
            String[] contents = line.split("\\s+");
            teamIndexes.put(contents[0], index);
            wins[index] = Integer.parseInt(contents[1]);
            losses[index] = Integer.parseInt(contents[2]);
            remains[index] = Integer.parseInt(contents[3]);
            for (int ix = 0; ix < cntOfTeams; ++ix) {
                games[index][ix] = Integer.parseInt(contents[4 + ix]);
            }
            ++index;
        }

        certOfElimination = new ArrayList<>(cntOfTeams);
    }

    public int numberOfTeams() {
        return cntOfTeams;
    }

    public Iterable<String> teams() {
        return teamIndexes.keySet();
    }

    public int wins(String team) {
        validateTeam(team);

        return wins[teamIndexes.get(team)];
    }

    public int losses(String team) {
        validateTeam(team);

        return losses[teamIndexes.get(team)];
    }

    public int remaining(String team) {
        validateTeam(team);

        return remains[teamIndexes.get(team)];
    }

    public int against(String team1, String team2) {
        validateTeam(team1, team2);

        int row = teamIndexes.get(team1);
        int col = teamIndexes.get(team2);
        return games[row][col];
    }

    public boolean isEliminated(String team) {
        validateTeam(team);
        certOfElimination.clear();

        // trivial elimination
        int index = teamIndexes.get(team);
        for (String t : teamIndexes.keySet()) {
            int rivalIndex = teamIndexes.get(t);
            if (index != rivalIndex && wins[index] + remains[index] < wins[rivalIndex]) {
                certOfElimination.add(t);
                return true;
            }
        }

        // nontrivial elimination
        return isNonTrivialEliminated(index);
    }

    public Iterable<String> certificateOfElimination(String team) {
        validateTeam(team);
        isEliminated(team);
        return certOfElimination.isEmpty() ? null : certOfElimination;
    }

    private void validateTeam(String... teams) {
        for (String team : teams) {
            if (!teamIndexes.containsKey(team)) {
                throw new IllegalArgumentException(String.format("Team %s is not existed!", team));
            }
        }
    }

    private boolean isNonTrivialEliminated(int v) {
        // cnt of vertices: C(cntOfTeams - 1, 2) + cntOfTeams - 1 + 2
        int cntOfVertices = (cntOfTeams - 1) * (cntOfTeams - 2);
        cntOfVertices <<= 1;
        cntOfVertices += cntOfTeams + 1;
        HashMap<Integer, Integer> verticeMap = new HashMap<>();

        // In flow network, we use 0 to represent source point
        int n = 1;
        // key: team index
        // value: index of node in graph
        for (int ix = 0; ix < cntOfTeams; ++ix) {
            if (ix != v) {
                verticeMap.put(ix, n++);        // team vertices
            }
        }

        // use `cntOfVertices` to represent the sink point
        for (int ix = 0; ix < cntOfTeams; ++ix) {
            if (ix == v) {
                continue;
            }
            for (int jx = ix + 1; jx < cntOfTeams; ++jx) {
                if (jx == v) {
                    continue;
                }
                int dest = (ix + 1) * cntOfTeams + jx - 1;
                verticeMap.put(dest, n++);      // game vertices
            }
        }

        FlowNetwork network = buildNetwork(v, verticeMap, cntOfVertices);
        FordFulkerson ff = new FordFulkerson(network, 0, cntOfVertices - 1);

        for (int ix = 0; ix < cntOfTeams; ++ix) {
            if (ix != v && ff.inCut(verticeMap.get(ix))) {
                for (String team : teamIndexes.keySet()) {
                    if (teamIndexes.get(team) == ix) {
                        certOfElimination.add(team);
                    }
                }
            }
        }

        return !certOfElimination.isEmpty();
    }

    private FlowNetwork buildNetwork(int v, HashMap<Integer, Integer> verticeMap, int cntOfVertices) {
        FlowNetwork network = new FlowNetwork(cntOfVertices);

        for (int ix = 0; ix < cntOfTeams; ++ix) {
            if (v == ix) {
                continue;
            }

            FlowEdge e1 = new FlowEdge(verticeMap.get(ix), cntOfVertices - 1, wins[v] + remains[v] - wins[ix]);
            network.addEdge(e1);
            for (int jx = ix + 1; jx < cntOfTeams; ++jx) {
                if (jx == v)  {
                    continue;
                }
                int indx = (ix + 1) * cntOfTeams + jx - 1;
                int dest = verticeMap.get(indx);
                FlowEdge e2 = new FlowEdge(SOURCE_VERTEX, dest, games[ix][jx]);
                network.addEdge(e2);
                FlowEdge e3 = new FlowEdge(dest, verticeMap.get(ix), Double.POSITIVE_INFINITY);
                network.addEdge(e3);
                FlowEdge e4 = new FlowEdge(dest, verticeMap.get(jx), Double.POSITIVE_INFINITY);
                network.addEdge(e4);
            }
        }

        return network;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        division.numberOfTeams();
        Iterable<String> list = division.certificateOfElimination("Detroit");
        division.isEliminated("Detroit");
        Iterable<String> list1 = division.certificateOfElimination("Detroit");
        System.out.println(list);
        System.out.println(list1);
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

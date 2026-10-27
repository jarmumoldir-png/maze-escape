import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Missions 1 and 2. Building the maze out of the walls.
 * A wall k joins the cells w.a()[k] and w.b()[k] and costs w.cost()[k]. Indexes start from 0,
 * the line number in walls.txt is the index + 1. The cells are connected by DisjointSet.
 */
public class Maze {

    private static int getCellCount(Input.Walls w) {
        int max = 0;
        for (int i = 0; i < w.count(); i++) {
            if (w.a()[i] > max) max = w.a()[i];
            if (w.b()[i] > max) max = w.b()[i];
        }
        return max + 1;
    }

    /**
     * Mission 1. Take the walls in the order of the file. Break a wall when its two cells are in different sets.
     * Returns the indexes of the broken walls, in the order of breaking.
     */
    public static int[] breakWalls(Input.Walls w) {
        int nCells = getCellCount(w);
        DisjointSet dsu = new DisjointSet(nCells);
        List<Integer> broken = new ArrayList<>();

        for (int i = 0; i < w.count(); i++) {
            int cellA = w.a()[i];
            int cellB = w.b()[i];
            if (dsu.union(cellA, cellB)) {
                broken.add(i);
            }
        }

        return broken.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Mission 2. Kruskal: the same, but take the walls from the cheapest to the most expensive.
     * Returns the indexes of the broken walls.
     */
    public static int[] cheapestMaze(Input.Walls w) {
        int nCells = getCellCount(w);
        int wallCount = w.count();
        
        Integer[] indices = new Integer[wallCount];
        for (int i = 0; i < wallCount; i++) {
            indices[i] = i;
        }
        
        Arrays.sort(indices, Comparator.comparingInt(i -> w.cost()[i]));

        DisjointSet dsu = new DisjointSet(nCells);
        List<Integer> broken = new ArrayList<>();

        for (int i : indices) {
            int cellA = w.a()[i];
            int cellB = w.b()[i];
            if (dsu.union(cellA, cellB)) {
                broken.add(i);
            }
        }

        return broken.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Mission 2. Prim with a priority queue. Returns the total cost of the cheapest maze.
     * It must be equal to the total cost of cheapestMaze.
     */
    public static long primCost(Input.Walls w) {
        int nCells = getCellCount(w);
        
        @SuppressWarnings("unchecked")
        List<int[]>[] adj = new ArrayList[nCells];
        for (int i = 0; i < nCells; i++) {
            adj[i] = new ArrayList<>();
        }
        
        for (int i = 0; i < w.count(); i++) {
            int u = w.a()[i];
            int v = w.b()[i];
            int cost = w.cost()[i];
            adj[u].add(new int[]{v, cost});
            adj[v].add(new int[]{u, cost});
        }

        boolean[] visited = new boolean[nCells];
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));

        long totalCost = 0;
        int visitedCount = 0;

        visited[0] = true;
        visitedCount++;
        for (int[] edge : adj[0]) {
            pq.offer(new int[]{edge[1], edge[0]});
        }

        while (!pq.isEmpty() && visitedCount < nCells) {
            int[] edge = pq.poll();
            int cost = edge[0];
            int u = edge[1];

            if (visited[u]) continue;

            visited[u] = true;
            visitedCount++;
            totalCost += cost;

            for (int[] nextEdge : adj[u]) {
                int v = nextEdge[0];
                int nextCost = nextEdge[1];
                if (!visited[v]) {
                    pq.offer(new int[]{nextCost, v});
                }
            }
        }

        return totalCost;
    }
}
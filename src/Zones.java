import java.util.*;
/**
 * Missions 4 and 5. The slides: a directed graph. Cells are 0 .. n-1, slide i goes from s.from()[i] to s.to()[i]
 * and takes s.time()[i]. There are no slides from a cell to itself, and the exit can always be reached from the start.
 *
 * Ready for you: Graphs.slidesFrom(s)[u] is the list of the slides that leave cell u, and Graphs.slidesInto(s)[v] is the list
 * of the slides that enter cell v (reversed graph). The lists hold slide numbers i; the cell at the other end is s.to()[i]
 * or s.from()[i].
 */
public class Zones {

    /**
     * Mission 4. Kosaraju. Returns zoneOf: zoneOf[cell] = number of the zone, numbers 0 .. zones-1.
     * Number the zones in the order in which the second pass finds them. That order is a topological order
     * of the zone graph: every slide between two zones goes from a smaller number to a larger number.
     *
     * Plan: 1) DFS on the slides, write the cells in the order in which they are finished (use a stack, not recursion);
     * 2) go through the cells from the last finished to the first; if a cell has no zone yet, DFS from it on the reversed
     * slides (slidesInto) and give every cell it reaches the next zone number.
     * Is DAG in Main is 1 when the number of zones equals the number of cells (no cycle), so you do not need a separate check.
     */
    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();
        int[][] slidesFrom = Graphs.slidesFrom(s);
        int[][] slidesInto = Graphs.slidesInto(s);

        boolean[] visited = new boolean[n];
        List<Integer> finishOrder = new ArrayList<>();

        // Pass 1: Iterative DFS for finish order
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                Stack<int[]> stack = new Stack<>();
                stack.push(new int[]{i, 0});
                visited[i] = true;

                while (!stack.isEmpty()) {
                    int[] curr = stack.peek();
                    int u = curr[0];
                    int edgeIdx = curr[1];

                    int[] outgoing = slidesFrom[u];
                    boolean pushedNew = false;

                    while (edgeIdx < outgoing.length) {
                        int slideId = outgoing[edgeIdx];
                        curr[1] = edgeIdx + 1;
                        int v = s.to()[slideId];
                        if (!visited[v]) {
                            visited[v] = true;
                            stack.push(new int[]{v, 0});
                            pushedNew = true;
                            break;
                        }
                        edgeIdx++;
                    }

                    if (!pushedNew) {
                        stack.pop();
                        finishOrder.add(u);
                    }
                }
            }
        }

        // Pass 2: DFS on reversed graph (slidesInto) in reverse finish order
        int[] zoneOf = new int[n];
        Arrays.fill(zoneOf, -1);
        int zoneCount = 0;

        for (int i = finishOrder.size() - 1; i >= 0; i--) {
            int root = finishOrder.get(i);
            if (zoneOf[root] == -1) {
                Stack<Integer> revStack = new Stack<>();
                revStack.push(root);
                zoneOf[root] = zoneCount;

                while (!revStack.isEmpty()) {
                    int u = revStack.pop();
                    for (int slideId : slidesInto[u]) {
                        int v = s.from()[slideId];
                        if (zoneOf[v] == -1) {
                            zoneOf[v] = zoneCount;
                            revStack.push(v);
                        }
                    }
                }
                zoneCount++;
            }
        }

        return zoneOf;
    }

    /**
     * Mission 5. The graph of zones: the edge X to Y costs the cheapest slide from X to Y, walking inside a zone is free.
     * Go through the zones in the order of their numbers (no priority queue). Returns two numbers:
     * [0] flow stages: the number of zones on the longest chain of zones;
     * [1] the cost of the cheapest route from the zone of cell 0 to the zone of the last cell.
     */
    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        int n = s.n();
        int startZone = zoneOf[0];
        int exitZone = zoneOf[n - 1];

        // Build minimum slide cost between zones
        Map<Integer, Integer>[] zoneAdj = new HashMap[zoneCount];
        for (int i = 0; i < zoneCount; i++) {
            zoneAdj[i] = new HashMap<>();
        }

        for (int i = 0; i < s.from().length; i++) {
            int uZone = zoneOf[s.from()[i]];
            int vZone = zoneOf[s.to()[i]];
            if (uZone != vZone) {
                int cost = s.time()[i];
                zoneAdj[uZone].put(vZone, Math.min(zoneAdj[uZone].getOrDefault(vZone, Integer.MAX_VALUE), cost));
            }
        }

        long[] maxStages = new long[zoneCount];
        long[] minCost = new long[zoneCount];
        Arrays.fill(minCost, Long.MAX_VALUE);

        maxStages[startZone] = 1;
        minCost[startZone] = 0;

        for (int u = 0; u < zoneCount; u++) {
            if (minCost[u] == Long.MAX_VALUE) continue;

            for (Map.Entry<Integer, Integer> entry : zoneAdj[u].entrySet()) {
                int v = entry.getKey();
                int cost = entry.getValue();

                if (maxStages[u] + 1 > maxStages[v]) {
                    maxStages[v] = maxStages[u] + 1;
                }
                if (minCost[u] + cost < minCost[v]) {
                    minCost[v] = minCost[u] + cost;
                }
            }
        }

        long flowStages = maxStages[exitZone];
        if (flowStages == 0) flowStages = 1; // At least start zone
        long cheapestCost = minCost[exitZone] == Long.MAX_VALUE ? 0 : minCost[exitZone];

        return new long[]{flowStages, cheapestCost};
    }
}
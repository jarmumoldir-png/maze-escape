import java.util.*;

/**
 * Mission 3. Escape: DFS from cell 0 to the last cell (rows * cols - 1) in the maze of Mission 1.
 * Two neighbouring cells are connected when the wall between them is broken.
 * The neighbours are tried in this order: up, right, down, left (cell - cols, cell + 1, cell + cols, cell - 1).
 * The ready method Graphs.open(w, broken) gives you the open neighbours of every cell, in no particular order:
 * to visit them in the required order, go through the four cells above one by one and take those that are in the list.
 * The program must not overflow the stack on a maze of 1000 by 1000 cells.
 */
public class Escape {

    /**
     * path: the cells of the way out, from 0 to the last cell.
     * visited: how many cells are marked as visited at the moment the exit is marked (the start and the exit are counted).
     * deadEnds: how many cells of the maze have exactly one open neighbour.
     */
    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int nCells = w.cells();
        int[][] openList = Graphs.open(w, broken);

        // Determine cols dynamically
        int cols = 1;
        for (int neighbor : openList[0]) {
            if (neighbor > 1) {
                cols = neighbor;
                break;
            }
        }
        if (cols <= 1) {
            outer:
            for (int i = 0; i < nCells; i++) {
                for (int neighbor : openList[i]) {
                    if (Math.abs(neighbor - i) > 1) {
                        cols = Math.abs(neighbor - i);
                        break outer;
                    }
                }
            }
        }
        if (cols <= 1) {
            cols = (int) Math.sqrt(nCells);
        }

        int exit = nCells - 1;

        @SuppressWarnings("unchecked")
        Set<Integer>[] openSets = new Set[nCells];
        for (int i = 0; i < nCells; i++) {
            openSets[i] = new HashSet<>();
            if (openList[i] != null) {
                for (int neighbor : openList[i]) {
                    openSets[i].add(neighbor);
                }
            }
        }

        int deadEnds = 0;
        for (int i = 0; i < nCells; i++) {
            if (openSets[i].size() == 1) {
                deadEnds++;
            }
        }

        boolean[] visitedArr = new boolean[nCells];
        int[] parent = new int[nCells];
        Arrays.fill(parent, -1);

        Stack<Integer> stack = new Stack<>();
        stack.push(0);
        visitedArr[0] = true;
        int visitedCount = 0;
        int exitVisitedCount = 0;

        while (!stack.isEmpty()) {
            int u = stack.pop();
            visitedCount++;

            if (u == exit && exitVisitedCount == 0) {
                exitVisitedCount = visitedCount;
            }

            int[] candidateNeighbors = { u - cols, u + 1, u + cols, u - 1 };

            for (int i = candidateNeighbors.length - 1; i >= 0; i--) {
                int v = candidateNeighbors[i];
                if (v >= 0 && v < nCells && openSets[u].contains(v)) {
                    if (!visitedArr[v]) {
                        visitedArr[v] = true;
                        parent[v] = u;
                        stack.push(v);
                    }
                }
            }
        }

        List<Integer> pathList = new ArrayList<>();
        int curr = exit;
        while (curr != -1) {
            pathList.add(curr);
            curr = parent[curr];
        }
        Collections.reverse(pathList);

        int[] path = pathList.stream().mapToInt(i -> i).toArray();

        return new Result(path, exitVisitedCount, deadEnds);
    }
}
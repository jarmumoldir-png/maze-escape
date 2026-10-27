/**
 * Checks of Mission 0. Run:  java -cp out DisjointSetTest
 * Add your own checks below: a large chain, a union of a set with itself, count after many unions.
 */
public class DisjointSetTest {

    static int failed = 0;

    static void check(boolean ok, String what) {
        System.out.println((ok ? "ok    " : "FAIL  ") + what);
        if (!ok) failed++;
    }

    public static void main(String[] args) {
        DisjointSet d = new DisjointSet(6);
        check(d.count() == 6, "6 sets at the start");
        check(!d.connected(0, 1), "0 and 1 are apart at the start");
        check(d.union(0, 1), "union(0, 1) joins two sets");
        check(d.connected(0, 1), "0 and 1 are together");
        check(!d.union(1, 0), "union(1, 0) again returns false");
        check(d.count() == 5, "5 sets after one union");
        d.union(2, 3); d.union(1, 3);
        check(d.connected(0, 2), "0 and 2 are together through 1 and 3");
        check(d.count() == 3, "3 sets: {0,1,2,3} {4} {5}");
        check(d.find(4) == 4 && d.find(5) == 5, "a single cell is its own root");

        // TODO: add your own checks here
        // 1. Union of a set with itself
        check(!d.union(4, 4), "union of an element with itself returns false");
        check(d.count() == 3, "count remains unchanged after self-union");

        // 2. A large chain of unions (testing path compression and union by size across many elements)
        int largeSize = 1000;
        DisjointSet largeD = new DisjointSet(largeSize);
        for (int i = 0; i < largeSize - 1; i++) {
            largeD.union(i, i + 1);
        }
        check(largeD.count() == 1, "large chain of size " + largeSize + " results in exactly 1 set");
        check(largeD.connected(0, largeSize - 1), "endpoints of the large chain are connected");
        check(largeD.find(0) == largeD.find(largeSize - 1), "all elements share the same root in a chain");

        System.out.println(failed == 0 ? "All checks passed." : failed + " checks failed.");
    }
}

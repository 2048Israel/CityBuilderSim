package ham.citybuildersim;

/**
 * Scratch (not in the tree; docs/studies/households): inclusive wall time per named method, filled by the calls HhAgent
 * puts at the entry and exits of the methods it is told to time. One thread (the one set as main) is counted; a method
 * that calls itself or one of its own overloads is counted once, from the outermost entry.
 */
public final class HhTimer {
    public static final int MAX = 64;
    public static final String[] NAMES = new String[MAX];
    static int count;
    static final long[] total = new long[MAX], calls = new long[MAX], start = new long[MAX];
    static final int[] depth = new int[MAX];
    public static volatile Thread main;

    public static synchronized int register(String name) {
        for (int i = 0; i < count; i++) if (NAMES[i].equals(name)) return i;
        NAMES[count] = name;
        return count++;
    }

    public static void enter(int id) {
        if (Thread.currentThread() != main) return;
        calls[id]++;
        if (depth[id]++ == 0) start[id] = System.nanoTime();
    }

    public static void exit(int id) {
        if (Thread.currentThread() != main) return;
        if (--depth[id] == 0) total[id] += System.nanoTime() - start[id];
    }

    public static void reset() {
        for (int i = 0; i < MAX; i++) { total[i] = 0; calls[i] = 0; depth[i] = 0; }
    }

    public static int count() { return count; }
    public static double ms(int id) { return total[id] / 1e6; }
    public static long calls(int id) { return calls[id]; }
    public static int idOf(String name) {
        for (int i = 0; i < count; i++) if (NAMES[i].equals(name)) return i;
        return -1;
    }
}

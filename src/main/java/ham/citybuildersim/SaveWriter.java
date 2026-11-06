package ham.citybuildersim;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The one thread saves are written on, for the whole JVM: it takes a
 * finished snapshot of a city and turns it into the save, the map and the
 * history files, one file at a time, off the game's clock. No JavaFX.
 *
 * WHY IT EXISTS (UP1, 2026-10-22)
 *
 * The autosave ran inside the month, on the FX thread, and turning DataSave
 * and HistorySave into JSON and writing three files was most of what a long
 * game's frames waited on: at month 4,000 it held the thread 170 ms every
 * twelve months, 31% of the FX thread at 50x speed, the longest frame on every
 * screen, and it grew with every month played (the history is 13 MB there,
 * two thirds of the cost). Only filling the save has to happen where the model
 * is safe to read; Game does that on the FX thread, hands the writer a
 * snapshot() of the save and a snapshot of the history that is owed (GT1:
 * the writer makes its copy as its first act, and the history makes it
 * itself before it next changes), and goes on with the month. The writer
 * never sees a live object.
 *
 * ONE WRITER, ONE FILE AT A TIME. A job for a file that already has one
 * waiting replaces it - the newer city is the one worth keeping, and two
 * writes to one file never run beside each other. The thread is started when
 * there is something to write and ends when there is not; it is not a daemon,
 * so a JVM that ends by itself waits for a write in flight (System.exit does
 * not, which is why Game.saveForQuit() waits for it).
 */
public final class SaveWriter {

    /** The most the month that autosaves may hold the clock, in milliseconds: the fill of the save and the snapshot of the history. */
    public static final long CLOCK_MS = 25;

    /** The least real time, in seconds, between two autosaves (Jerus, 2026-10-22: "At most every 30 s"); a month due inside it waits for the next due month past it. */
    public static final double FLOOR_SECONDS = 30;

    /**
     * Anything the one writer writes (TR1, 2026-10-31): the autosave's Job, and a recording's month or history (Recording). Two
     * tasks for one target never run beside each other, and a newer one replaces one still waiting - the recording rides the
     * thread the autosave does, so a month of it never holds the clock and never runs beside a save.
     */
    public interface Task {

        /** The file this task is for: tasks with the same target replace one another while they wait. */
        Path target();

        /** Writes it, on the writer's thread; the outcome is returned, not thrown. */
        GameFiles.Result write();
    }

    /**
     * One city to put on disk: the three things a save is, already taken off
     * the live model on the FX thread. It writes in the order Game.save()
     * always has: the save, its map, then the history.
     */
    public static final class Job implements Task {

        private final GameFiles files;
        private final int slot;
        private final DataSave data;
        private final HistorySave history;
        private final HistorySave.Later laterHistory;
        private final byte[] map;

        /** {@code map} is the city map's sidecar bytes, or null when the city has none. */
        public Job(GameFiles files, int slot, DataSave data, HistorySave history, byte[] map) {
            this.files = files;
            this.slot = slot;
            this.data = data;
            this.history = history;
            this.laterHistory = null;
            this.map = map;
        }

        /**
         * An autosave's job (GT1): the history is a snapshot still owed, copied by the first of this job's write() and
         * the history's next change to ask for it - see HistorySave.snapshotLater().
         */
        public Job(GameFiles files, int slot, DataSave data, HistorySave.Later history, byte[] map) {
            this.files = files;
            this.slot = slot;
            this.data = data;
            this.history = null;
            this.laterHistory = history;
            this.map = map;
        }

        /** For SaveSlotCheck: the save this job writes. */
        DataSave data() { return data; }

        /** For SaveSlotCheck: the history this job writes (copied now, on the calling thread, if the writer has not yet). */
        HistorySave history() { return laterHistory != null ? laterHistory.get() : history; }

        /** For SaveSlotCheck: the map's bytes, or null when the city has none. */
        byte[] map() { return map; }

        /** The file this job is for: the save's; two jobs with the same target never run beside each other. */
        @Override
        public Path target() {
            return files.saveFile(slot);
        }

        /**
         * Writes all three files and returns the save's outcome with the history's beside it (UP6, LB3-S): a history that could
         * not be written is printed and carried in the result's historyError, so a new save beside an old history is never told
         * "Saved" alone. The map's failure is printed only.
         */
        @Override
        public GameFiles.Result write() {

            // The history's copy first (GT1): the clock waits for it, if at all, only until the next month is recorded.
            HistorySave toWrite = history();

            GameFiles.Result result = data.saveGame(files, slot);

            if (map != null && result.ok) {
                GameFiles.Result mapResult = files.write(files.mapFile(slot), map);
                if (!mapResult.ok) System.out.println(mapResult.message());
            }

            // The history is written even when the save failed, on purpose: the two
            // files are independent, and one of them landing is strictly better
            // than neither. The outcome is the save's with the history's beside it
            // (UP6, LB3-S): a slot can hold a new save beside an old history, and the
            // game must not say "Saved" alone when it does.
            GameFiles.Result historyResult = toWrite.saveHistory(files, slot);
            if (!historyResult.ok) {
                System.out.println(historyResult.message());
            }

            return result.withHistory(historyResult);
        }
    }

    /** A task waiting its turn, and who to tell when it has been written. */
    private static final class Waiting {
        final Task job;
        final Consumer<GameFiles.Result> done;

        Waiting(Task job, Consumer<GameFiles.Result> done) {
            this.job = job;
            this.done = done;
        }
    }

    /** The one writer every city in the JVM shares: what shared() hands out. */
    private static final SaveWriter SHARED = new SaveWriter();

    /** The writer every save in this JVM goes through. */
    static SaveWriter shared() {
        return SHARED;
    }

    /* Everything below is guarded by this object's monitor. A job leaves the
       map under it and is written outside it. */

    /** Waiting jobs by file, in the order they were first asked for; a newer job for a file replaces the older in its place. */
    private final Map<Path, Waiting> waiting = new LinkedHashMap<>();

    /** The thread writing, or null when none is running. */
    private Thread worker;

    /** How many jobs are out of the map and being written right now: 1 while the writer writes, never more. */
    private int beingWritten;

    private boolean held;
    private int writes;
    private int mostAtOnce;

    SaveWriter() { }

    /**
     * Hands a job to the writer and returns at once. It replaces any job still
     * waiting for the same file. {@code done} is called on the writer's thread
     * with the job's result - or with a failure naming the throwable, if
     * writing it threw anything.
     */
    void submit(Task job, Consumer<GameFiles.Result> done) {
        synchronized (this) {
            waiting.put(job.target(), new Waiting(job, done));
            startWorker();
            notifyAll();
        }
    }

    /** Blocks until nothing is waiting and nothing is being written. If interrupted it keeps the flag and returns. */
    void awaitIdle() {
        synchronized (this) {
            try {
                while (!waiting.isEmpty() || beingWritten > 0) {
                    // A writer that died with jobs left is started again here, not waited for.
                    if (!waiting.isEmpty()) startWorker();
                    wait();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /** For checks: while held the writer starts no new job; one already writing finishes. */
    synchronized void hold() {
        held = true;
    }

    /** For checks: lets a held writer go on. */
    synchronized void release() {
        held = false;
        notifyAll();
    }

    /** For checks: how many jobs have been written since the JVM started. */
    synchronized int writes() {
        return writes;
    }

    /** For checks: the most jobs ever being written at the same moment; it must stay 1. */
    synchronized int mostAtOnce() {
        return mostAtOnce;
    }

    /** Starts the writer if none is running. Called with the monitor held. */
    private void startWorker() {
        if (worker != null) return;
        Thread thread = new Thread(this::run, "save-writer");
        thread.setDaemon(false);
        worker = thread;
        thread.start();
    }

    private void run() {
        try {
            while (true) {
                Waiting next;
                synchronized (this) {
                    while (held && !waiting.isEmpty()) {
                        wait();
                    }
                    if (waiting.isEmpty()) {
                        // Reset in the same breath as the emptiness check, so a submit
                        // after this sees no worker and starts the next one.
                        worker = null;
                        notifyAll();
                        return;
                    }
                    Iterator<Waiting> first = waiting.values().iterator();
                    next = first.next();
                    first.remove();
                    beingWritten++;
                    mostAtOnce = Math.max(mostAtOnce, beingWritten);
                }

                GameFiles.Result result;
                try {
                    result = next.job.write();
                } catch (Throwable t) {
                    System.out.println("A save could not be written: " + t);
                    result = GameFiles.Result.failed(next.job.target(), t);
                }

                try {
                    next.done.accept(result);
                } catch (Throwable t) {
                    System.out.println("A save's report failed: " + t);
                }

                synchronized (this) {
                    beingWritten--;
                    writes++;
                    notifyAll();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            synchronized (this) {
                // A crash must not leave submits that never start a worker. (On the
                // ordinary way out the worker was reset already, and a newer one may
                // be running: it is none of this thread's business.)
                if (worker == Thread.currentThread()) {
                    worker = null;
                    beingWritten = 0;
                }
                notifyAll();
            }
        }
    }
}

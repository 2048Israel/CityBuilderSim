# SaveWriter.java - 269 lines · 19 methods · 3 constants · model

`ham/citybuildersim/SaveWriter.java` - generated 2026-11-02 by CodeMap; line numbers are as of that run.

> The one thread saves are written on, for the whole JVM: it takes a
> finished snapshot of a city and turns it into the save, the map and the
> history files, one file at a time, off the game's clock. No JavaFX.
> 
> WHY IT EXISTS (UP1, 2026-10-22)
> 
> The autosave ran inside the month, on the FX thread, and turning DataSave
> and HistorySave into JSON and writing three files was most of what a long
> game's frames waited on: at month 4,000 it held the thread 170 ms every
> twelve months, 31% of the FX thread at 50x speed, the longest frame on every
> screen, and it grew with every month played (the history is 13 MB there,
> two thirds of the cost). Only filling the save has to happen where the model
> is safe to read; Game does that on the FX thread, hands the writer a
> snapshot() of the save and of the history, and goes on with the month. The
> writer never sees a live object.
> 
> ONE WRITER, ONE FILE AT A TIME. A job for a file that already has one
> waiting replaces it - the newer city is the one worth keeping, and two
> writes to one file never run beside each other. The thread is started when
> there is something to write and ends when there is not; it is not a daemon,
> so a JVM that ends by itself waits for a write in flight (System.exit does
> not, which is why Game.saveForQuit() waits for it).

**Uses:** [GameFiles](GameFiles.md) (13), [DataSave](DataSave.md) (4), [HistorySave](HistorySave.md) (4)

**Used by (5):** [Game](Game.md), [Recording](Recording.md), [ReplayCheck](ReplayCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [UiProbe](UiProbe.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 36 | `SaveWriter.CLOCK_MS` | `25` | The most the month that autosaves may hold the clock, in milliseconds: the fill of the save and the snapshot of the history. |
| 39 | `SaveWriter.FLOOR_SECONDS` | `30` | The least real time, in seconds, between two autosaves (Jerus, 2026-10-22: "At most every 30 s"); a month due inside it waits for the next due month past it. |
| 128 | `SaveWriter.SHARED` | `new SaveWriter()` | The one writer every city in the JVM shares: what shared() hands out. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 62 | `private final GameFiles files` |  |
| 63 | `private final int slot` |  |
| 64 | `private final DataSave data` |  |
| 65 | `private final HistorySave history` |  |
| 66 | `private final byte[] map` |  |
| 118 | `final Task job` |  |
| 119 | `final Consumer<GameFiles.Result> done` |  |
| 139 | `private final Map<Path, Waiting> waiting` | Waiting jobs by file, in the order they were first asked for; a newer job for a file replaces the older in its place. |
| 142 | `private Thread worker` | The thread writing, or null when none is running. |
| 145 | `private int beingWritten` | How many jobs are out of the map and being written right now: 1 while the writer writes, never more. |
| 147 | `private boolean held` |  |
| 148 | `private int writes` |  |
| 149 | `private int mostAtOnce` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 33 | 237 | **type** `public final class SaveWriter` | The one thread saves are written on, for the whole JVM: it takes a finished snapshot of a city and turns it into the save, the map and the history files, one file at a time, off the game's clock. |
| 46 | 8 | **type** `public interface Task` | Anything the one writer writes (TR1, 2026-10-31): the autosave's Job, and a recording's month or history (Recording). |
| 49 | 1 | `Path target()` _(in SaveWriter.Task)_ | The file this task is for: tasks with the same target replace one another while they wait. |
| 52 | 1 | `GameFiles.Result write()` _(in SaveWriter.Task)_ | Writes it, on the writer's thread; the outcome is returned, not thrown. |
| 60 | 55 | **type** `public static final class Job implements Task` | One city to put on disk: the three things a save is, already taken off the live model on the FX thread. |
| 69 | 7 | `public Job(GameFiles files, int slot, DataSave data, HistorySave history, byte[] map)` _(in SaveWriter.Job)_ | {@code map} is the city map's sidecar bytes, or null when the city has none. |
| 78 | 1 | `DataSave data()` _(in SaveWriter.Job)_ | For SaveSlotCheck: the save this job writes. |
| 81 | 1 | `HistorySave history()` _(in SaveWriter.Job)_ | For SaveSlotCheck: the history this job writes. |
| 84 | 1 | `byte[] map()` _(in SaveWriter.Job)_ | For SaveSlotCheck: the map's bytes, or null when the city has none. |
| 88 | 3 | `public Path target()` _(in SaveWriter.Job)_ | The file this job is for: the save's; two jobs with the same target never run beside each other. |
| 94 | 20 | `public GameFiles.Result write()` _(in SaveWriter.Job)_ | Writes all three files and returns the save's outcome; the map's and the history's failures are printed. |
| 117 | 9 | **type** `private static final class Waiting` | A task waiting its turn, and who to tell when it has been written. |
| 121 | 4 | `Waiting(Task job, Consumer<GameFiles.Result> done)` _(in SaveWriter.Waiting)_ |  |
| 131 | 3 | `static SaveWriter shared()` | The writer every save in this JVM goes through. |
| 151 | 1 | `SaveWriter()` |  |
| 159 | 7 | `void submit(Task job, Consumer<GameFiles.Result> done)` | Hands a job to the writer and returns at once. |
| 168 | 13 | `void awaitIdle()` | Blocks until nothing is waiting and nothing is being written. |
| 183 | 3 | `synchronized void hold()` | For checks: while held the writer starts no new job; one already writing finishes. |
| 188 | 4 | `synchronized void release()` | For checks: lets a held writer go on. |
| 194 | 3 | `synchronized int writes()` | For checks: how many jobs have been written since the JVM started. |
| 199 | 3 | `synchronized int mostAtOnce()` | For checks: the most jobs ever being written at the same moment; it must stay 1. |
| 204 | 7 | `private void startWorker()` | Starts the writer if none is running. |
| 212 | 57 | `private void run()` |  |


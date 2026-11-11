# InputTimeline.java - 378 lines · 32 methods · 3 constants · model

`ham/citybuildersim/InputTimeline.java` - generated 2026-11-11 by CodeMap; line numbers are as of that run.

> A recording's input logs read as one timeline (TR2, 2026-11-09): which sitting holds each recorded month, the segments those
> make, where Follow can start for a month, and the mouse, the screen, the window and the map the player had at any moment of
> it. Read once (InputLog) and never changed; no JavaFX, the interface only turns its answers into pictures.
> 
> <p>WHY IT EXISTS (TR2): a recording keeps the timeline last played, and the input logs keep every sitting - and a rewind makes
> sittings overlap: the player loads an earlier save and plays on, the recording drops the months past it, and the old sitting's
> log still reaches them. Follow (FollowPlayer) must play the player's input for the months the recording holds, so the rule is
> the brief's: a sitting covers a month when its first MONTH marker is at or below it and its last at or above, the
> latest-started covering sitting holds it, and a month the recording does not hold is nobody's. (A sitting that itself loads an
> earlier save, its markers going back, is cut there into runs, each one a sitting of its own in this rule.) Time is the log's
> own: milliseconds of the sitting, a segment being a stretch of one sitting from the marker that shows its first month to the
> marker that shows the first month past its last. A moment is a place in a segment, by the index of the next event to deliver
> and the millisecond, so that the events a marker shares its millisecond with are not lost.
> 
> <p>IDLE: the input events (MOVE, DRAG, PRESS, RELEASE, WHEEL, KEY_DOWN, KEY_UP; the markers are the game's, not the player's)
> and the ends of a segment cut it into gaps, and a gap longer than {@link #IDLE_GAP_MS} is an idle stretch, which FollowPlayer
> plays in {@link #IDLE_KEEP_MS} when it skips idle.

**Uses:** [InputLog](InputLog.md) (12), [MapView](MapView.md) (5), [Recording](Recording.md) (1)

**Used by (4):** [FollowBench](FollowBench.md), [FollowPlayer](FollowPlayer.md), [ReplayCheck](ReplayCheck.md), [ReplayMode](ReplayMode.md)

## Sections

| line | section |
|---:|---|
| 82 | · what it is made of |
| 218 | · building |
| 311 | · answering |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 31 | `InputTimeline.IDLE_GAP_MS` | `5_000` | The longest a gap between two input events plays as it was, in milliseconds of the sitting: a longer gap is an idle stretch. |
| 34 | `InputTimeline.IDLE_KEEP_MS` | `1_000` | How long an idle stretch plays for when idle skipping is on, in milliseconds at speed 1. |
| 37 | `InputTimeline.CHECKPOINT_EVERY` | `256` | How many events apart stateAt's saved states are, so that it folds this many at most. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 86 | `final long startedAt` |  |
| 87 | `final List<InputLog.Event> events` |  |
| 88 | `final State[] checkpoints` |  |
| 113 | `boolean mouse` |  |
| 114 | `double x, y, w, h` |  |
| 115 | `String screen` |  |
| 116 | `MapView view` |  |
| 117 | `int buttons` |  |
| 149 | `final int sitting` |  |
| 150 | `final Log log` |  |
| 151 | `final int start, end` |  |
| 152 | `final int[] markers, months` |  |
| 182 | `final Run run` |  |
| 183 | `final List<InputLog.Event> events` |  |
| 184 | `final int from, to` |  |
| 185 | `final int firstMonth, lastMonth` |  |
| 186 | `final long msFrom, msTo` |  |
| 187 | `final long[] idleFrom, idleTo` |  |
| 220 | `private final List<Log> logs` |  |
| 221 | `private final List<Seg> segs` |  |
| 222 | `private final List<Segment> views` |  |
| 223 | `private final int[] recorded` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 28 | 351 | **type** `public final class InputTimeline` | A recording's input logs read as one timeline (TR2, 2026-11-09): which sitting holds each recorded month, the segments those make, where Follow can start for a month, and the mouse, the screen, the window and the map ... |
| 40 | 1 | **type** `public record Sitting(long startedAt, List<InputLog.Event> events)` | A sitting as the harness or the folder gives it: when it began (epoch milliseconds) and its events, in the order they were logged. |
| 46 | 1 | **type** `public record Segment(int sitting, long startedAt, int firstMonth, int lastMonth, long msFrom, long msTo)` | A stretch of one sitting the timeline plays: months {@code firstMonth} to {@code lastMonth} of the recording, held by the sitting (the n-th, oldest first, that began at {@code startedAt}), from {@code msFrom} to {@cod... |
| 52 | 1 | **type** `public record Moment(int segment, int index, long ms)` | A place in the timeline: the segment, the index of the next event of its sitting to deliver (the events before it are delivered, the MONTH markers before it taken) and the sitting's millisecond. |
| 55 | 1 | **type** `public record MapView(boolean globe, double a, double b, double scale, double roll)` | The expanded map's view as a MAP_VIEW logs it; {@code globe} for the globe, else the plane. |
| 62 | 9 | **type** `public record State(boolean mouseKnown, double x, double y, String screen, double windowWidth, double windo...` | What the player had at a moment: the last mouse position logged ({@code mouseKnown} false before any), the screen open (null before any), the window's size (0 before any), the last map view logged (null before any; op... |
| 66 | 1 | `public boolean mapOpen()` _(in InputTimeline.State)_ | Whether the expanded map was open: the last MAP_VIEW logged has a scale above 0. |
| 69 | 1 | `public boolean down(int button)` _(in InputTimeline.State)_ | Whether the mouse button (the code a PRESS logs) was held. |
| 72 | 1 | `private static int bit(int button)` |  |
| 75 | 6 | `public static boolean isInput(InputLog.Kind kind)` | Whether an event is something the player did with the mouse or the keyboard: not a marker. |

### what it is made of (lines 82-217)

| line | len | member | says |
|---:|---:|---|---|
| 85 | 25 | **type** `static final class Log` | A sitting's events with the states saved every CHECKPOINT_EVERY of them. |
| 90 | 11 | `Log(Sitting sitting)` _(in InputTimeline.Log)_ |  |
| 102 | 7 | `State stateBefore(int index)` _(in InputTimeline.Log)_ |  |
| 112 | 34 | **type** `private static final class Acc` | The state folded event by event. |
| 119 | 1 | `Acc()` _(in InputTimeline.Acc)_ |  |
| 121 | 10 | `Acc(State s)` _(in InputTimeline.Acc)_ |  |
| 132 | 11 | `void fold(InputLog.Event e)` _(in InputTimeline.Acc)_ |  |
| 144 | 1 | `State state()` _(in InputTimeline.Acc)_ |  |
| 148 | 31 | **type** `private static final class Run` | One run of a sitting: its MONTH markers go up and up; a marker that does not go up begins the next run. |
| 154 | 8 | `Run(int sitting, Log log, int start, int end, List<Integer> markerIndexes)` _(in InputTimeline.Run)_ |  |
| 163 | 1 | `int first()` _(in InputTimeline.Run)_ |  |
| 165 | 1 | `int last()` _(in InputTimeline.Run)_ |  |
| 168 | 4 | `int atOrAbove(int month)` _(in InputTimeline.Run)_ | The position of the first marker showing {@code month} or a later one, or -1. |
| 174 | 4 | `int above(int month)` _(in InputTimeline.Run)_ | The position of the first marker showing a month past {@code month}, or -1. |
| 181 | 36 | **type** `static final class Seg` | A segment with what playing it needs: the event indexes it spans and its idle stretches. |
| 189 | 23 | `Seg(Run run, int from, int to, int firstMonth, int lastMonth)` _(in InputTimeline.Seg)_ |  |
| 213 | 3 | `Segment view(int logIndex)` _(in InputTimeline.Seg)_ |  |

### building (lines 218-310)

| line | len | member | says |
|---:|---:|---|---|
| 225 | 39 | `private InputTimeline(List<Sitting> sittings, int[] recordedMonths)` |  |
| 265 | 18 | `private static void cutRuns(int sitting, Log log, List<Run> out)` |  |
| 285 | 10 | `private static void flush(Run run, int a, int b, List<Seg> out)` | The segment of {@code run} for months {@code a} to {@code b}, if the run has a marker in them to play. |
| 297 | 8 | `public static InputTimeline of(Path recordingFolder)` | The timeline of a recording's folder: its sittings' logs read once, and the months the recording holds. |
| 307 | 3 | `public static InputTimeline of(List<Sitting> sittings, int[] recordedMonths)` | The timeline of sittings in memory (a harness's), and the months the recording holds: a month not in it is nobody's. |

### answering (lines 311-378)

| line | len | member | says |
|---:|---:|---|---|
| 314 | 1 | `public List<Segment> segments()` | The segments in the order they play, which is the order of their months. |
| 317 | 1 | `public boolean isEmpty()` | Whether no log reaches any recorded month. |
| 319 | 1 | `int size()` |  |
| 321 | 1 | `Seg seg(int i)` |  |
| 324 | 1 | `public Moment first()` | The start of the first segment, or null when there is none. |
| 327 | 4 | `public Moment startOfSegment(int segment)` | The start of the segment: its first marker taken. |
| 336 | 14 | `public Moment startOf(int month)` | Where Follow starts for a recorded month: the segment that holds it and the millisecond of its marker, or of the first marker past it when a skip jumped over it; null when the recording lacks the month or no log reach... |
| 352 | 6 | `public Moment momentAt(int segment, long ms)` | The place in a segment where its sitting's clock says {@code ms}: the events at or before it delivered (its first marker at least). |
| 360 | 11 | `public int monthAt(Moment at)` | The month the player's screen showed at the moment: that of the last MONTH marker before it, or -1 for a moment that is not in the timeline. |
| 373 | 5 | `public State stateAt(Moment at)` | What the player had at the moment: everything logged in the sitting before it, folded. |


package ham.citybuildersim;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * A recording's input logs read as one timeline (TR2, 2026-11-09): which sitting holds each recorded month, the segments those
 * make, where Follow can start for a month, and the mouse, the screen, the window and the map the player had at any moment of
 * it. Read once (InputLog) and never changed; no JavaFX, the interface only turns its answers into pictures.
 *
 * <p>WHY IT EXISTS (TR2): a recording keeps the timeline last played, and the input logs keep every sitting - and a rewind makes
 * sittings overlap: the player loads an earlier save and plays on, the recording drops the months past it, and the old sitting's
 * log still reaches them. Follow (FollowPlayer) must play the player's input for the months the recording holds, so the rule is
 * the brief's: a sitting covers a month when its first MONTH marker is at or below it and its last at or above, the
 * latest-started covering sitting holds it, and a month the recording does not hold is nobody's. (A sitting that itself loads an
 * earlier save, its markers going back, is cut there into runs, each one a sitting of its own in this rule.) Time is the log's
 * own: milliseconds of the sitting, a segment being a stretch of one sitting from the marker that shows its first month to the
 * marker that shows the first month past its last. A moment is a place in a segment, by the index of the next event to deliver
 * and the millisecond, so that the events a marker shares its millisecond with are not lost.
 *
 * <p>IDLE: the input events (MOVE, DRAG, PRESS, RELEASE, WHEEL, KEY_DOWN, KEY_UP; the markers are the game's, not the player's)
 * and the ends of a segment cut it into gaps, and a gap longer than {@link #IDLE_GAP_MS} is an idle stretch, which FollowPlayer
 * plays in {@link #IDLE_KEEP_MS} when it skips idle.
 */
public final class InputTimeline {

    /** The longest a gap between two input events plays as it was, in milliseconds of the sitting: a longer gap is an idle stretch. */
    public static final long IDLE_GAP_MS = 5_000;

    /** How long an idle stretch plays for when idle skipping is on, in milliseconds at speed 1. */
    public static final long IDLE_KEEP_MS = 1_000;

    /** How many events apart stateAt's saved states are, so that it folds this many at most. */
    static final int CHECKPOINT_EVERY = 256;

    /** A sitting as the harness or the folder gives it: when it began (epoch milliseconds) and its events, in the order they were logged. */
    public record Sitting(long startedAt, List<InputLog.Event> events) { }

    /**
     * A stretch of one sitting the timeline plays: months {@code firstMonth} to {@code lastMonth} of the recording, held by the
     * sitting (the n-th, oldest first, that began at {@code startedAt}), from {@code msFrom} to {@code msTo} of its clock.
     */
    public record Segment(int sitting, long startedAt, int firstMonth, int lastMonth, long msFrom, long msTo) { }

    /**
     * A place in the timeline: the segment, the index of the next event of its sitting to deliver (the events before it are
     * delivered, the MONTH markers before it taken) and the sitting's millisecond.
     */
    public record Moment(int segment, int index, long ms) { }

    /** The expanded map's view as a MAP_VIEW logs it; {@code globe} for the globe, else the plane. */
    public record MapView(boolean globe, double a, double b, double scale, double roll) { }

    /**
     * What the player had at a moment: the last mouse position logged ({@code mouseKnown} false before any), the screen open
     * (null before any), the window's size (0 before any), the last map view logged (null before any; open only while its scale is
     * above 0) and the mouse buttons held, bit {@code 1 << button}.
     */
    public record State(boolean mouseKnown, double x, double y, String screen, double windowWidth, double windowHeight,
                        MapView mapView, int buttons) {

        /** Whether the expanded map was open: the last MAP_VIEW logged has a scale above 0. */
        public boolean mapOpen() { return mapView != null && mapView.scale() > 0; }

        /** Whether the mouse button (the code a PRESS logs) was held. */
        public boolean down(int button) { return (buttons & bit(button)) != 0; }
    }

    private static int bit(int button) { return button >= 0 && button < 31 ? 1 << button : 0; }

    /** Whether an event is something the player did with the mouse or the keyboard: not a marker. */
    public static boolean isInput(InputLog.Kind kind) {
        return switch (kind) {
            case MOVE, DRAG, PRESS, RELEASE, WHEEL, KEY_DOWN, KEY_UP -> true;
            default -> false;
        };
    }

    /* ------------------------------ what it is made of ------------------------------ */

    /** A sitting's events with the states saved every CHECKPOINT_EVERY of them. */
    static final class Log {
        final long startedAt;
        final List<InputLog.Event> events;
        final State[] checkpoints;

        Log(Sitting sitting) {
            this.startedAt = sitting.startedAt();
            this.events = List.copyOf(sitting.events());
            this.checkpoints = new State[events.size() / CHECKPOINT_EVERY + 1];
            Acc acc = new Acc();
            for (int i = 0; i < events.size(); i++) {
                if (i % CHECKPOINT_EVERY == 0) checkpoints[i / CHECKPOINT_EVERY] = acc.state();
                acc.fold(events.get(i));
            }
            if (events.size() % CHECKPOINT_EVERY == 0) checkpoints[events.size() / CHECKPOINT_EVERY] = acc.state();
        }

        State stateBefore(int index) {
            int at = Math.max(0, Math.min(index, events.size()));
            int k = at / CHECKPOINT_EVERY;
            Acc acc = new Acc(checkpoints[k]);
            for (int i = k * CHECKPOINT_EVERY; i < at; i++) acc.fold(events.get(i));
            return acc.state();
        }
    }

    /** The state folded event by event. */
    private static final class Acc {
        boolean mouse;
        double x, y, w, h;
        String screen;
        MapView view;
        int buttons;

        Acc() { }

        Acc(State s) {
            mouse = s.mouseKnown();
            x = s.x();
            y = s.y();
            w = s.windowWidth();
            h = s.windowHeight();
            screen = s.screen();
            view = s.mapView();
            buttons = s.buttons();
        }

        void fold(InputLog.Event e) {
            switch (e.kind()) {
                case MOVE, WHEEL -> { mouse = true; x = e.x(); y = e.y(); }
                case DRAG, PRESS -> { mouse = true; x = e.x(); y = e.y(); buttons |= bit(e.code()); }
                case RELEASE -> { mouse = true; x = e.x(); y = e.y(); buttons &= ~bit(e.code()); }
                case WINDOW -> { w = e.a(); h = e.b(); }
                case SCREEN -> screen = e.words();
                case MAP_VIEW -> view = new MapView(e.code() == 1, e.a(), e.b(), e.c(), e.d());
                default -> { }
            }
        }

        State state() { return new State(mouse, x, y, screen, w, h, view, buttons); }
    }

    /** One run of a sitting: its MONTH markers go up and up; a marker that does not go up begins the next run. */
    private static final class Run {
        final int sitting;
        final Log log;
        final int start, end;
        final int[] markers, months;

        Run(int sitting, Log log, int start, int end, List<Integer> markerIndexes) {
            this.sitting = sitting;
            this.log = log;
            this.start = start;
            this.end = end;
            this.markers = markerIndexes.stream().mapToInt(Integer::intValue).toArray();
            this.months = Arrays.stream(markers).map(i -> log.events.get(i).code()).toArray();
        }

        int first() { return months[0]; }

        int last() { return months[months.length - 1]; }

        /** The position of the first marker showing {@code month} or a later one, or -1. */
        int atOrAbove(int month) {
            for (int k = 0; k < months.length; k++) if (months[k] >= month) return k;
            return -1;
        }

        /** The position of the first marker showing a month past {@code month}, or -1. */
        int above(int month) {
            for (int k = 0; k < months.length; k++) if (months[k] > month) return k;
            return -1;
        }
    }

    /** A segment with what playing it needs: the event indexes it spans and its idle stretches. */
    static final class Seg {
        final Run run;
        final List<InputLog.Event> events;
        final int from, to;
        final int firstMonth, lastMonth;
        final long msFrom, msTo;
        final long[] idleFrom, idleTo;

        Seg(Run run, int from, int to, int firstMonth, int lastMonth) {
            this.run = run;
            this.events = run.log.events;
            this.from = from;
            this.to = to;
            this.firstMonth = firstMonth;
            this.lastMonth = lastMonth;
            this.msFrom = events.get(from).ms();
            this.msTo = to < events.size() ? events.get(to).ms() : events.get(to - 1).ms();
            List<long[]> idle = new ArrayList<>();
            long before = msFrom;
            for (int i = from; i <= to; i++) {
                long at;
                if (i == to) at = msTo;
                else if (isInput(events.get(i).kind())) at = events.get(i).ms();
                else continue;
                if (at < before) continue;
                if (at - before > IDLE_GAP_MS) idle.add(new long[] {before, at});
                before = at;
            }
            this.idleFrom = idle.stream().mapToLong(a -> a[0]).toArray();
            this.idleTo = idle.stream().mapToLong(a -> a[1]).toArray();
        }

        Segment view(int logIndex) {
            return new Segment(logIndex, run.log.startedAt, firstMonth, lastMonth, msFrom, msTo);
        }
    }

    /* --------------------------------- building ---------------------------------- */

    private final List<Log> logs = new ArrayList<>();
    private final List<Seg> segs = new ArrayList<>();
    private final List<Segment> views = new ArrayList<>();
    private final int[] recorded;

    private InputTimeline(List<Sitting> sittings, int[] recordedMonths) {
        List<Sitting> ordered = new ArrayList<>(sittings);
        ordered.sort(Comparator.comparingLong(Sitting::startedAt));
        for (Sitting s : ordered) logs.add(new Log(s));
        this.recorded = Arrays.stream(recordedMonths == null ? new int[0] : recordedMonths).distinct().sorted().toArray();

        List<Run> runs = new ArrayList<>();
        for (int n = 0; n < logs.size(); n++) cutRuns(n, logs.get(n), runs);

        Run holder = null;
        int from = 0, last = 0;
        List<Seg> built = new ArrayList<>();
        for (int m : recorded) {
            Run held = null;
            for (int r = runs.size() - 1; r >= 0 && held == null; r--) {
                Run run = runs.get(r);
                if (run.first() <= m && m <= run.last()) held = run;
            }
            boolean continues = held != null && held == holder && m == last + 1;
            if (!continues) {
                flush(holder, from, last, built);
                from = m;
            }
            holder = held;
            last = m;
        }
        flush(holder, from, last, built);

        // Two pieces of one run that meet at one marker - a month between them the run jumped over and the recording lacks - are one.
        for (Seg s : built) {
            Seg before = segs.isEmpty() ? null : segs.get(segs.size() - 1);
            if (before != null && before.run == s.run && before.to == s.from) {
                segs.set(segs.size() - 1, new Seg(s.run, before.from, s.to, before.firstMonth, s.lastMonth));
            } else {
                segs.add(s);
            }
        }
        for (Seg s : segs) views.add(s.view(s.run.sitting));
    }

    private static void cutRuns(int sitting, Log log, List<Run> out) {
        int start = -1;
        int lastMonth = Integer.MIN_VALUE;
        List<Integer> markers = new ArrayList<>();
        for (int i = 0; i < log.events.size(); i++) {
            InputLog.Event e = log.events.get(i);
            if (e.kind() != InputLog.Kind.MONTH) continue;
            if (start >= 0 && e.code() <= lastMonth) {
                out.add(new Run(sitting, log, start, i, markers));
                start = -1;
                markers = new ArrayList<>();
            }
            if (start < 0) start = i;
            markers.add(i);
            lastMonth = e.code();
        }
        if (start >= 0) out.add(new Run(sitting, log, start, log.events.size(), markers));
    }

    /** The segment of {@code run} for months {@code a} to {@code b}, if the run has a marker in them to play. */
    private static void flush(Run run, int a, int b, List<Seg> out) {
        if (run == null) return;
        int pa = run.atOrAbove(a);
        if (pa < 0) return;
        int pb = run.above(b);
        int from = run.markers[pa];
        int to = pb < 0 ? run.end : run.markers[pb];
        if (from >= to) return;
        out.add(new Seg(run, from, to, a, b));
    }

    /** The timeline of a recording's folder: its sittings' logs read once, and the months the recording holds. */
    public static InputTimeline of(Path recordingFolder) {
        List<Sitting> read = new ArrayList<>();
        for (Path file : InputLog.sittings(recordingFolder)) {
            long at = InputLog.startOf(file);
            if (at >= 0) read.add(new Sitting(at, InputLog.read(file)));
        }
        return new InputTimeline(read, Recording.at(recordingFolder).months());
    }

    /** The timeline of sittings in memory (a harness's), and the months the recording holds: a month not in it is nobody's. */
    public static InputTimeline of(List<Sitting> sittings, int[] recordedMonths) {
        return new InputTimeline(sittings, recordedMonths);
    }

    /* --------------------------------- answering --------------------------------- */

    /** The segments in the order they play, which is the order of their months. */
    public List<Segment> segments() { return List.copyOf(views); }

    /** Whether no log reaches any recorded month. */
    public boolean isEmpty() { return segs.isEmpty(); }

    int size() { return segs.size(); }

    Seg seg(int i) { return segs.get(i); }

    /** The start of the first segment, or null when there is none. */
    public Moment first() { return segs.isEmpty() ? null : startOfSegment(0); }

    /** The start of the segment: its first marker taken. */
    public Moment startOfSegment(int segment) {
        Seg s = segs.get(segment);
        return new Moment(segment, s.from + 1, s.msFrom);
    }

    /**
     * Where Follow starts for a recorded month: the segment that holds it and the millisecond of its marker, or of the first marker
     * past it when a skip jumped over it; null when the recording lacks the month or no log reaches it.
     */
    public Moment startOf(int month) {
        if (Arrays.binarySearch(recorded, month) < 0) return null;
        for (int si = 0; si < segs.size(); si++) {
            Seg s = segs.get(si);
            if (month < s.firstMonth || month > s.lastMonth) continue;
            for (int k = 0; k < s.run.months.length; k++) {
                int at = s.run.markers[k];
                if (at < s.from || at >= s.to || s.run.months[k] < month) continue;
                return new Moment(si, at + 1, s.events.get(at).ms());
            }
            return null;
        }
        return null;
    }

    /** The place in a segment where its sitting's clock says {@code ms}: the events at or before it delivered (its first marker at least). */
    public Moment momentAt(int segment, long ms) {
        Seg s = segs.get(segment);
        int index = s.from + 1;
        while (index < s.to && s.events.get(index).ms() <= ms) index++;
        return new Moment(segment, index, ms);
    }

    /** The month the player's screen showed at the moment: that of the last MONTH marker before it, or -1 for a moment that is not in the timeline. */
    public int monthAt(Moment at) {
        if (at == null || at.segment() < 0 || at.segment() >= segs.size()) return -1;
        Seg s = segs.get(at.segment());
        int limit = Math.max(at.index(), s.from + 1);
        int month = -1;
        for (int i = s.from; i < Math.min(limit, s.to); i++) {
            InputLog.Event e = s.events.get(i);
            if (e.kind() == InputLog.Kind.MONTH) month = e.code();
        }
        return month;
    }

    /** What the player had at the moment: everything logged in the sitting before it, folded. */
    public State stateAt(Moment at) {
        if (at == null || at.segment() < 0 || at.segment() >= segs.size()) return new Acc().state();
        Seg s = segs.get(at.segment());
        return s.run.log.stateBefore(at.index());
    }
}

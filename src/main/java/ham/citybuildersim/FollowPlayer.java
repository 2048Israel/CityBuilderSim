package ham.citybuildersim;

/**
 * Plays an InputTimeline in real time (TR2, 2026-11-09): the cursor of Follow. It moves along the log by the time it is given,
 * at a chosen speed, with idle stretches shortened when asked, and hands every event that falls due to a {@link Target} in the
 * order it was logged; the month markers it turns into {@link Target#loadMonth} itself, before the events after them. No
 * JavaFX, no clock of its own: the interface calls {@link #advance} with the real milliseconds of each frame, so a harness plays
 * a sitting in no time and the same way.
 *
 * <p>WHY IT EXISTS (TR2): Follow feeds the player's logged input into the replay's copy - tabs switch, pages scroll, the map pans,
 * clicks land - and months turn when the log says they turned, each by loading that month whole. Which events, which month and
 * when is arithmetic that must be right to the frame whatever the window does, so it lives here, away from the scene. Joining
 * mid-month loads the month and plays its input from its start in one go, flagged as catching up so that the interface changes
 * state without drawing the way there; the state the month's marker found (the screen open, the window, the map) is
 * {@link InputTimeline#stateAt} of the segment's start.
 */
public final class FollowPlayer {

    /** What plays the log: the interface (or a harness), on the thread that calls start() and advance(). */
    public interface Target {

        /** The month the log shows now: load it whole. Called for each MONTH marker, before the events after it, and once by start(). */
        void loadMonth(int month);

        /** A logged event to re-enact (a marker too: the target decides what a SCREEN, a WINDOW, a MAP_VIEW or a CLOCK means); {@code catchingUp} while start() plays a month's input up to the moment joined, which is not drawn. */
        void event(InputLog.Event e, boolean catchingUp);
    }

    /** The speeds Follow offers, as multiples of the player's own time. */
    public static final double[] SPEEDS = {0.5, 1, 2, 4};

    /** The speed a player starts at. */
    public static final double DEFAULT_SPEED = 1;

    /** How close to the cursor an event's millisecond may be to count as due, in milliseconds: floating-point slack. */
    private static final double SLACK_MS = 1e-6;

    private final InputTimeline timeline;
    private final Target target;
    private double speed = DEFAULT_SPEED;
    private boolean skipIdle = true;
    private boolean started, stopped, done;
    private int segment, next, month = -1;
    private double cursor;

    public FollowPlayer(InputTimeline timeline, Target target) {
        this.timeline = timeline;
        this.target = target;
    }

    /**
     * Joins the timeline at {@code at}: loads the month in effect there and plays that month's input from just after its marker
     * up to the moment, all at once, flagged as catching up; then it is live. A moment right after a marker (InputTimeline.startOf)
     * has nothing to catch up. A moment that is not in the timeline ends the player.
     */
    public void start(InputTimeline.Moment at) {
        started = false;
        stopped = false;
        done = false;
        if (at == null || at.segment() < 0 || at.segment() >= timeline.size()) {
            done = true;
            return;
        }
        InputTimeline.Seg s = timeline.seg(at.segment());
        int index = Math.max(s.from + 1, Math.min(at.index(), s.to));
        int marker = s.from;
        for (int i = s.from; i < index; i++) if (s.events.get(i).kind() == InputLog.Kind.MONTH) marker = i;
        segment = at.segment();
        next = index;
        cursor = Math.max(Math.max((double) at.ms(), s.msFrom), s.events.get(index - 1).ms());
        started = true;
        month = s.events.get(marker).code();
        target.loadMonth(month);
        for (int i = marker + 1; i < index && !stopped; i++) target.event(s.events.get(i), true);
    }

    /**
     * Moves the log's cursor by {@code realMs} of the viewer's time times the speed - a stretch of idle compressed to
     * {@link InputTimeline#IDLE_KEEP_MS} while skipping - and delivers the events that fall due, in order, not catching up. A
     * MONTH marker is a loadMonth before the events after it; when a segment ends the next begins at its first marker; at the end
     * of the timeline it stops ({@link #isDone}). Before start() it starts at the beginning of the timeline.
     */
    public void advance(double realMs) {
        if (stopped || done) return;
        if (!started) start(timeline.first());
        if (stopped || done || !(realMs > 0)) return;
        double budget = realMs * speed;
        while (!done && !stopped) {
            InputTimeline.Seg s = timeline.seg(segment);
            deliverDue(s);
            if (stopped) return;
            if (cursor + SLACK_MS >= s.msTo) {
                if (segment + 1 >= timeline.size()) {
                    done = true;
                    return;
                }
                segment++;
                InputTimeline.Seg following = timeline.seg(segment);
                next = following.from;
                cursor = following.msFrom;
                continue;
            }
            if (budget <= 0) return;
            double boundary = s.msTo;
            double rate = 1;
            if (skipIdle) {
                for (int k = 0; k < s.idleTo.length; k++) {
                    if (s.idleTo[k] <= cursor) continue;
                    if (s.idleFrom[k] <= cursor) {
                        rate = (double) (s.idleTo[k] - s.idleFrom[k]) / InputTimeline.IDLE_KEEP_MS;
                        boundary = Math.min(boundary, s.idleTo[k]);
                    } else {
                        boundary = Math.min(boundary, s.idleFrom[k]);
                    }
                    break;
                }
            }
            double room = boundary - cursor;
            double reach = budget * rate;
            if (reach >= room) {
                budget -= room / rate;
                cursor = boundary;
            } else {
                cursor += reach;
                budget = 0;
            }
        }
    }

    /** Hands over the events at or before the cursor, and every one left when the cursor has reached the segment's end. */
    private void deliverDue(InputTimeline.Seg s) {
        boolean ended = cursor + SLACK_MS >= s.msTo;
        while (next < s.to && !stopped) {
            InputLog.Event e = s.events.get(next);
            if (!ended && e.ms() > cursor + SLACK_MS) break;
            next++;
            if (e.kind() == InputLog.Kind.MONTH) {
                month = e.code();
                target.loadMonth(month);
            } else {
                target.event(e, false);
            }
        }
    }

    /** Sets the speed, a multiple of the player's own time (see {@link #SPEEDS}); a value that is not above 0 is ignored. */
    public void setSpeed(double speed) {
        if (speed > 0 && !Double.isInfinite(speed)) this.speed = speed;
    }

    public double speed() { return speed; }

    /** Whether idle stretches play shortened (on at the start). */
    public void setSkipIdle(boolean skip) { this.skipIdle = skip; }

    public boolean skipIdle() { return skipIdle; }

    /** The place the cursor is at, to stop and rejoin later with start(); null before the player has started. */
    public InputTimeline.Moment now() {
        return started ? new InputTimeline.Moment(segment, next, (long) Math.floor(cursor)) : null;
    }

    /** The month the log shows now, or -1 before the player has started. */
    public int month() { return month; }

    /** Ends the playing: nothing more is delivered until start() is called again. */
    public void stop() { stopped = true; }

    /** Whether the player is started and neither stopped nor at the end. */
    public boolean isRunning() { return started && !stopped && !done; }

    /** Whether the cursor has reached the end of the timeline. */
    public boolean isDone() { return done; }
}

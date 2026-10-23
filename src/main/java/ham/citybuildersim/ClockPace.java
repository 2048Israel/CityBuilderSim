package ham.citybuildersim;

import java.util.ArrayDeque;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;

/**
 * The clock's pace (UP2): how much of each frame the months may take, how often the screen is
 * redrawn and what the redraw cost, and what speed the city is really running at.
 *
 * It holds the arithmetic of ui.UserInterface.startClock() and nothing else: pure Java, no JavaFX,
 * with its time source injected so a harness can drive it with a clock it advances by hand
 * (ClockCheck). The window's timer calls frame() once a frame, redrawStarted() and redrawEnded()
 * around each redraw it makes, and asks redrawDue(), progress() and reading().
 *
 * WHY THIS EXISTS. The timer ran every month that was due in a frame, however long they took (up
 * to two and a half of them after a stalled frame at 50x), and redrew the open screen at a fixed
 * 120 ms spacing that never looked at what a redraw cost. Neither cost mattered while a month and
 * a page were cheap. At month 4,000 a People page took 55 ms to build and lay out, so at 50x
 * redraws took nearly half of the FX thread, and a slow stretch stacked: the owed time was banked
 * and paid back in a burst the frame after. So the redraw now waits three times what it cost (it
 * takes at most a third of the thread), a frame stops running months when it has spent
 * MONTH_FRAME_MS on them and drops the time it could not run instead of banking it, and the
 * speed the header reads says when the city is running slower than the rung asked for.
 */
public final class ClockPace {

    /**
     * The least time between one redraw of the open screen and the next while the clock runs, in
     * seconds (moved here from ui.UserInterface by UP2, its comment with it).
     *
     * HOW OFTEN THE SCREEN MAY BE REBUILT WHILE TIME RUNS. The clock redraws the open screen at
     * most this often, which was free at 10x: a month
     * arrives every 500ms there, so the limit never bound. At 50x one lands every 100ms, and a
     * heavy page - the property tax screen is a ten-row grid and ten dials - rebuilt ten times a
     * second is a page that stutters and fights the pointer. So the SIMULATION runs free and the
     * REDRAW is capped. 120ms is under the gap at 20x, so nothing below the top rung behaves any
     * differently, and at 50x the screen lags the city by at most a tenth of a second - one
     * frame's worth of figures on a screen advancing ten months a second. A redraw that costs
     * more waits longer still (REDRAW_SPACING).
     */
    public static final double REDRAW_EVERY = .12;

    /** How many times a redraw's cost the next one waits (UP2): 3, so redraws take at most a third of the thread. */
    public static final double REDRAW_SPACING = 3;

    /** The most a redraw is counted to have cost, in seconds (UP2): the pulse that ends the measure can be late (a minimised window), and a gap of minutes is a screen that never moves. */
    public static final double REDRAW_COST_MAX = .5;

    /** The time one frame may spend running months, in milliseconds (UP2): the loop stops when the months run in it pass this, but the first month always runs. */
    public static final double MONTH_FRAME_MS = 40;

    /**
     * The most real time one frame is worth to the game's clock, in seconds.
     *
     * CLAMPED, because a stalled frame is not elapsed game time. Dragging the window, opening a
     * menu or a long garbage collection can hand the clock a dt of several seconds, and at 10x
     * that would silently run a year while the player was not looking at the screen. A quarter of
     * a second is the most any single frame is allowed to be worth.
     *
     * AND THE CLAMP EARNS ITS KEEP AT THE TOP OF THE LADDER. A quarter of a second is 2.5
     * months at 50x - so a dragged window or a long collection costs a couple of months rather
     * than a couple of years. It was written for 10x and it is what makes 20x and 50x safe to offer.
     */
    public static final double FRAME_CLAMP = .25;

    /** The running time the speed achieved is read over, in seconds (UP2); until a full window has run the header reads the rung alone. */
    public static final double SHORT_WINDOW = 2;

    /** How far under the rung the speed achieved must fall, as a share of the rung, before the header says so (UP2): more than a tenth. */
    public static final double SHORT_BY = .1;

    private final LongSupplier nanoTime;

    /** The months run in the month now under way and owed to the next, as a fraction; a month is due at 1. */
    private double progress;
    private double dropped;

    private double sinceRedraw;
    private double redrawCost;
    private double redrawGap = REDRAW_EVERY;
    private long redrawBegan;
    private boolean redrawing;

    /** The frames of the window: each one's real seconds and the months it advanced. */
    private final ArrayDeque<double[]> window = new ArrayDeque<>();
    private double windowSeconds, windowMonths;
    private double windowRung = Double.NaN, secondsPerMonth;

    public ClockPace() {
        this(System::nanoTime);
    }

    /** @param nanoTime the time source, in nanoseconds, as System.nanoTime(); a harness passes a clock it moves itself */
    public ClockPace(LongSupplier nanoTime) {
        this.nanoTime = nanoTime;
    }

    /* =====================================================================
       THE MONTHS A FRAME RUNS
       ===================================================================== */

    /**
     * One frame of the clock while it runs: adds the share of a month the frame is worth (clamped
     * to FRAME_CLAMP), then runs {@code month} while a month is due and the months run in this
     * frame have taken under MONTH_FRAME_MS - the first always runs. What is still owed past one
     * month when the loop stops is dropped (droppedMonths()), not banked, so a slow stretch slows
     * the city instead of bursting afterwards.
     *
     * @param dt              real seconds since the last frame
     * @param speed           the rung, in times real speed
     * @param secondsPerMonth real seconds a month takes at 1x
     * @param month           runs one month and returns true when the clock stopped itself (the
     *                        city has something to say): the loop ends and the progress is zeroed
     * @return the months run
     */
    public int frame(double dt, double speed, double secondsPerMonth, BooleanSupplier month) {
        sinceRedraw += dt;
        // A new rung is a new window: months at 5x say nothing of months at 50x.
        if (speed != windowRung) {
            restart();
            windowRung = speed;
        }
        this.secondsPerMonth = secondsPerMonth;

        double share = Math.min(dt, FRAME_CLAMP);
        if (dt > share) dropped += (dt - share) * speed / secondsPerMonth;
        double before = progress;
        progress += share * speed / secondsPerMonth;

        int ran = 0;
        long began = nanoTime.getAsLong();
        while (progress >= 1) {
            progress -= 1;
            boolean stopped = month.getAsBoolean();
            ran++;
            if (stopped) {
                stopped();
                return ran;
            }
            if (progress >= 1 && (nanoTime.getAsLong() - began) / 1e6 >= MONTH_FRAME_MS) {
                dropped += progress - 1;
                progress = 1;
                break;
            }
        }

        // What the frame advanced the city: the months it ran and the fraction of the next it
        // gained. Counted so, not in months landed alone, a rung of 0.1x (a month in fifty
        // seconds) has a speed to read over two seconds as well as a rung of 50x.
        windowAdd(dt, ran + progress - before);
        return ran;
    }

    /** A frame of a clock that is not running (paused, or a menu or dialog up): only the redraw's time moves. */
    public void idle(double dt) {
        sinceRedraw += dt;
    }

    /** The share of the next month the clock has run, 0 up to 1 (1 while a month is owed that a slow frame could not run): what the date's day is read from. */
    public double progress() {
        return progress;
    }

    /** The months of owed time that were dropped, not run, since the pace was made: a stalled frame past FRAME_CLAMP, what a slow frame could not fit, what a stop zeroed. */
    public double droppedMonths() {
        return dropped;
    }

    /** The clock stopped itself or the city was closed: nothing is owed, and the speed's window starts again. */
    public void stopped() {
        dropped += progress;
        progress = 0;
        restart();
    }

    /** The speed's window starts again (the clock starts, the speed changes, another city opens); the progress stands. */
    public void restart() {
        window.clear();
        windowSeconds = 0;
        windowMonths = 0;
        windowRung = Double.NaN;
    }

    /* =====================================================================
       HOW OFTEN THE SCREEN IS REDRAWN
       ===================================================================== */

    /** Whether the gap since the last redraw began has passed: a redraw that is pending may be drawn. */
    public boolean redrawDue() {
        return sinceRedraw >= redrawGap;
    }

    /** A redraw of the open screen begins: the time since the last starts again, and its cost is measured from here. */
    public void redrawStarted() {
        sinceRedraw = 0;
        redrawBegan = nanoTime.getAsLong();
        redrawing = true;
    }

    /** Whether a redraw began and its cost has not been taken (redrawEnded()). */
    public boolean redrawing() {
        return redrawing;
    }

    /** The redraw that began is over - drawn, and CSS and layout done: its cost sets the gap before the next. A call with no redraw begun does nothing. */
    public void redrawEnded() {
        if (!redrawing) return;
        redrawing = false;
        redrawCost = Math.min(REDRAW_COST_MAX, (nanoTime.getAsLong() - redrawBegan) / 1e9);
        redrawGap = Math.max(REDRAW_EVERY, redrawCost * REDRAW_SPACING);
    }

    /** The seconds the next redraw waits after the last one began: the larger of REDRAW_EVERY and what the last cost times REDRAW_SPACING. */
    public double redrawGap() {
        return redrawGap;
    }

    /** What the last redraw cost, in seconds (capped at REDRAW_COST_MAX); 0 before the first. */
    public double redrawCost() {
        return redrawCost;
    }

    /* =====================================================================
       THE SPEED THE CITY IS REALLY RUNNING AT
       ===================================================================== */

    /**
     * What the header reads between the speed's arrows: the rung alone - {@code rungLabel}, as
     * the header writes it - while the city keeps up or until a full SHORT_WINDOW of running time
     * has passed since the window began; and the rung, then the speed achieved, once the months
     * have fallen more than SHORT_BY under it: "50&times; &middot; 31&times;". The speed achieved
     * is written whole from 1 up and with one decimal under 1.
     *
     * @param rungLabel the rung as the header writes it ("50&times;")
     * @param rung      the rung, in times real speed
     */
    public String reading(String rungLabel, double rung) {
        if (windowSeconds < SHORT_WINDOW || secondsPerMonth <= 0) return rungLabel;
        double achieved = windowMonths / windowSeconds * secondsPerMonth;
        if (!(achieved < rung * (1 - SHORT_BY))) return rungLabel;
        return rungLabel + " · " + (achieved >= 1
                ? Long.toString(Math.round(achieved))
                : String.format(Locale.ROOT, "%.1f", achieved)) + "×";
    }

    /** One frame into the window; the oldest frames go while what is left still spans SHORT_WINDOW. */
    private void windowAdd(double seconds, double months) {
        window.addLast(new double[] {seconds, months});
        windowSeconds += seconds;
        windowMonths += months;
        while (window.size() > 1 && windowSeconds - window.peekFirst()[0] >= SHORT_WINDOW) {
            double[] old = window.removeFirst();
            windowSeconds -= old[0];
            windowMonths -= old[1];
        }
    }
}

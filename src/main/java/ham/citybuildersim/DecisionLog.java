package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.IntSupplier;

/**
 * What the player decided, and when: every change of a policy and every
 * spend at scale, one short line each, at the month it was made (0.7.23).
 *
 * WHY THIS EXISTS
 *
 * Jerus, on the charts: "event markers on the timeline, including the
 * player's own decisions ('taxes to 17%', 'central bank cut to 0%', 'bank
 * rescued')". Nothing in the game kept them. A tax rate is saved as what it
 * IS, not as when it moved, and the history keeps the rate a month at a
 * time - so the month a player cut it could be guessed from a step in a
 * line, and the reason a line stepped could not be told from the line. A
 * decision is a flow, and a flow cannot be reconstructed from the state a
 * month ended in, so it is recorded as it happens and saved.
 *
 * RECORDED WHERE IT IS APPLIED, NEVER IN THE INTERFACE. Each policy has one
 * place that applies it - TaxPolicy's setters, LabourMarket's floor, the
 * central bank's dials, Education's share of tuition, and Game's methods
 * for the standing subsidies, the bank, the fund, the paper, the currency
 * and the queue - and that is where the line is written, so a decision made
 * from any screen, from the playtest's advisor or from a harness is
 * recorded the same way, and one that changes nothing (a value set to what
 * it already was) is not recorded at all. Ordinary build orders, and the
 * plots the land office buys, are not decisions: a city places them every
 * month, and the big ones the treasury cannot pay for arrive here as the
 * borrowing that paid for them.
 *
 * HELD WHILE A CITY IS BUILT. A new city, a load and the constructor set
 * their dials through the same setters a player does; none of that is a
 * decision, so the log is held from buildWorld() until the door into the
 * city is through, and a load holds it for itself (Game.loadGame()). It is
 * held too where the city acts by itself through a player's own method: the
 * rollover's issues (Game.issueForRollover()), a foreign default the
 * treasury could not avoid (checkForeignSolvency()), and the reserves a
 * dollar issue buys as part of itself (handleForeignLogic(), whose own line
 * says "held as reserves").
 *
 * NOTHING HERE PRINTS. Every println is the game's log and the playtest's
 * report reads the log; a decision is in this list and in the save, on
 * the charts (ChartModel.flags()) and in Policy's RECENT DECISIONS and City
 * History's lists, and nowhere else.
 */
public final class DecisionLog {

    /* ----- the kinds a decision comes in: what its flag is coloured by ----- */

    /** A tax rate, an offset or the farmland relief. */
    public static final String TAX = "tax";

    /** A promise: the wage floor, pensions, EI, schools, health, the fare, a standing subsidy. */
    public static final String PROMISE = "promise";

    /** The central bank's dials: the rate, the rule, the target, its holdings, its advances. */
    public static final String CENTRAL_BANK = "central bank";

    /** The money itself: a reform, the vault bought or sold, how land is paid for. */
    public static final String CURRENCY = "currency";

    /** The city's paper: an issue, a buyback, the rollover's setting, a default abroad. */
    public static final String BORROWING = "borrowing";

    /** The commercial bank: a rescue, the preferred offer, the rescue setting. */
    public static final String BANK = "bank";

    /** The city's fund: its dial, and the hand on it. */
    public static final String FUND = "fund";

    /** The construction queue (0.7.22): the order, rushes, cancels, restarts, demolitions, buy-outs. */
    public static final String CONSTRUCTION = "construction";

    /** The city's strategic reserve (0.7.85): a fill ordered, a release set or stopped. */
    public static final String RESERVE = "reserve";

    /** One decision: when, what kind, and what it was, in a few words. Public fields, for the save. */
    public static final class Entry {
        public int month;
        public String kind;
        public String label;

        /** Gson needs it. */
        public Entry() { }

        public Entry(int month, String kind, String label) {
            this.month = month;
            this.kind = kind;
            this.label = label;
        }

        public int month()    { return month; }
        public String kind()  { return kind == null ? "" : kind; }
        public String label() { return label == null ? "" : label; }
    }

    private final List<Entry> entries = new ArrayList<>();

    /** The month a decision is made in: the city's own counter, read when it is recorded. */
    private final IntSupplier clock;

    /** How many doors are open that set dials without deciding anything; nothing is recorded while any is. */
    private int held;

    /** A log on no clock - every decision at month 0. For a fixture. */
    public DecisionLog() { this(() -> 0); }

    public DecisionLog(IntSupplier clock) {
        this.clock = clock == null ? () -> 0 : clock;
    }

    /**
     * Records one decision at this month. Nothing while held, and nothing
     * empty. The caller decides whether anything changed - it alone knows
     * what the value was.
     */
    public void record(String kind, String label) {
        if (held > 0 || label == null || label.isEmpty()) return;
        entries.add(new Entry(clock.getAsInt(), kind, label));
    }

    /** Stops recording until the matching release(): a city being built or loaded. Nests. */
    public void hold() { held++; }

    /** Ends one hold(). */
    public void release() { if (held > 0) held--; }

    /** Whether anything is being recorded now. */
    public boolean isHeld() { return held > 0; }

    /** Every decision, oldest first. Read-only. */
    public List<Entry> entries() { return Collections.unmodifiableList(entries); }

    public int size() { return entries.size(); }

    /** The decisions of one month, in the order they were made. */
    public List<Entry> inMonth(int month) {
        List<Entry> out = new ArrayList<>();
        for (Entry e : entries) if (e.month == month) out.add(e);
        return out;
    }

    /** The newest decision, or null. */
    public Entry last() { return entries.isEmpty() ? null : entries.get(entries.size() - 1); }

    /* ----- the save (DataSave.decisionLog) ----- */

    /** For the save: a copy, so the save never holds the live list. */
    public List<Entry> toState() {
        List<Entry> out = new ArrayList<>();
        for (Entry e : entries) out.add(new Entry(e.month, e.kind, e.label));
        return out;
    }

    /** ...and back; null - a save from before 0.7.23 (format 28 and older) - is an empty log. */
    public void restore(List<Entry> saved) {
        entries.clear();
        if (saved == null) return;
        for (Entry e : saved) {
            if (e == null || e.label == null || e.label.isEmpty()) continue;
            entries.add(new Entry(e.month, e.kind == null ? "" : e.kind, e.label));
        }
    }

    /* ----- the words a line is written in ----- */

    /** A share as a percentage, as few places as it needs and never more than two: "17%", "17.5%", "0.25%". */
    public static String pct(double share) {
        return trim(String.format(Locale.ROOT, "%.2f", share * 100)) + "%";
    }

    /** A rate to two places, as the central bank's dial reads: "0.00%", "3.25%". */
    public static String pct2(double share) {
        return String.format(Locale.ROOT, "%.2f%%", share * 100);
    }

    /** Points over or under a base: "+2 pts", "-1.5 pts", "0 pts". */
    public static String points(double share) {
        double p = share * 100;
        String s = trim(String.format(Locale.ROOT, "%.2f", Math.abs(p)));
        return (p > 1e-9 ? "+" : p < -1e-9 ? "-" : "") + s + " pts";
    }

    /** A multiplier: "x1.2", "x0". */
    public static String times(double scale) {
        return "x" + trim(String.format(Locale.ROOT, "%.2f", scale));
    }

    /** Money, in the model's thousands, as the screens print it: "$58.7M". */
    public static String money(double thousands) {
        return Formats.INSTANCE.amount(thousands);
    }

    /** Whether two dial readings differ by more than a rounding hair. */
    public static boolean moved(double was, double now) {
        return Math.abs(was - now) > 1e-12;
    }

    private static String trim(String s) {
        if (s.indexOf('.') < 0) return s;
        s = s.replaceAll("0+$", "");
        return s.endsWith(".") ? s.substring(0, s.length() - 1) : s;
    }
}

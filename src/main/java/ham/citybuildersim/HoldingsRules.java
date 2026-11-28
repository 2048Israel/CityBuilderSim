package ham.citybuildersim;

/**
 * The central bank's saved rules for its holdings of the city's paper: how
 * fast the book moves toward the holdings dial, how it shrinks, its share of
 * each auction, and the law on buying from the treasury at all.
 * WHY (CBK2): Jerus, 2026-11-26: "setting it at 10% doesnt mean it goes to 10%
 * right away, just means thats teh cap", and "when you issue debt the ability
 * to just issue directly to the central bank? if real life is not like that
 * then no". Until CBK2 the pace was QE_SPEED's, a quarter of the larger
 * setting a month, never the player's; QT was whatever ran off past the dial
 * plus sales at that pace; and the bank bought at issue only its own maturing
 * par. BankRules' shape (CBK3): an enum of dials, a saved array, appended in
 * order, an old save's missing slots reading the defaults, which are main's.
 */
public final class HoldingsRules {
    /** The pace dial's setting that keeps the old pace: QE_SPEED of the larger of the dial, the setting before it and the share held, a month. */
    public static final double PACE_AUTO = 0;
    /** QT_MODE's setting for runoff and sales at market, main's (the default). */
    public static final double QT_SELL = 0;
    /** ...and for runoff only, capped at the pace (the Fed's 2022 QT). */
    public static final double QT_RUNOFF = 1;
    /** The law's setting that bars buying at issue beyond the bank's own rollover (the Federal Reserve Act, s.14(b), since 1935). */
    public static final double LAW_OFF = 0;
    /** ...and the one that allows it (the Bank of Canada's practice). */
    public static final double LAW_ON = 1;

    /** Saved order: append new rules, never reorder existing ones. */
    public enum Dial {
        PACE("Pace", PACE_AUTO, 0, .25, .0025,
                "The most the book moves in a month by the open market and by runoff, as a share of the term paper. "
                + "At Auto it moves a quarter of the larger of the dial and the setting before it a month."),
        QT_MODE("Shrinking the book", QT_SELL, 0, 1, 1,
                "Over its dial it lets maturing paper run off and sells the rest at market, the gain or loss in its "
                + "income; or it only lets maturing paper run off, up to the pace each month, as the Fed did from 2022."),
        AUCTION_SHARE("Auction share", 0, 0, 1, .01,
                "With direct purchases allowed: its non-competitive bid at each new term issue, as a share of the face "
                + "the market buys, at the auction's price, never past the dial."),
        DIRECT_PURCHASES("Direct purchases", LAW_OFF, 0, 1, 1,
                "The law. Off, it buys at an auction only to replace its own maturing paper, as the Fed must. "
                + "On, the auction share is open, and leaning on it costs credibility.");

        public final String label, explanation;
        public final double initial, min, max, step;
        Dial(String label, double initial, double min, double max, double step, String explanation) {
            this.label = label; this.initial = initial; this.min = min;
            this.max = max; this.step = step; this.explanation = explanation;
        }
    }

    private final double[] values = new double[Dial.values().length];
    private transient DecisionLog decisions;
    public HoldingsRules() { reset(); }
    public void recordTo(DecisionLog log) { decisions = log; }
    public double get(Dial dial) { return values[dial.ordinal()]; }
    public void set(Dial dial, double value) {
        double before = get(dial);
        values[dial.ordinal()] = bounded(dial, value);
        if (decisions != null && before != get(dial))
            decisions.record(DecisionLog.CENTRAL_BANK, dial.label + " to " + reading(dial, get(dial)));
    }

    /** The pace a month as a share of the term paper, or PACE_AUTO for the old pace. */
    public double pace() { return get(Dial.PACE); }
    /** True when the book shrinks by runoff only. */
    public boolean runoffOnly() { return get(Dial.QT_MODE) >= QT_RUNOFF; }
    /** True when the law allows buying at issue beyond the bank's own rollover. */
    public boolean directPurchasesAllowed() { return get(Dial.DIRECT_PURCHASES) >= LAW_ON; }
    /** The auction share in force: the dial's under the law, nothing without it. */
    public double auctionShare() { return directPurchasesAllowed() ? get(Dial.AUCTION_SHARE) : 0; }

    public static String reading(Dial dial, double value) {
        return switch (dial) {
            case PACE -> value == PACE_AUTO ? "Auto (a quarter of the way a month)"
                    : String.format(java.util.Locale.ROOT, "%.2f%% of the term paper a month", value * 100);
            case QT_MODE -> value >= QT_RUNOFF ? "Runoff only" : "Runoff and sales at market";
            case AUCTION_SHARE -> value == 0 ? "None"
                    : String.format(java.util.Locale.ROOT, "%.0f%% of each term issue", value * 100);
            case DIRECT_PURCHASES -> value >= LAW_ON ? "Allowed" : "Not allowed";
        };
    }

    private static double bounded(Dial dial, double value) {
        if (!Double.isFinite(value)) return dial.initial;
        double v = Math.max(dial.min, Math.min(dial.max, value));
        // The two switches are one or the other, never between.
        return dial == Dial.QT_MODE || dial == Dial.DIRECT_PURCHASES ? (v >= .5 ? dial.max : dial.min) : v;
    }
    public void reset() { for (Dial dial : Dial.values()) values[dial.ordinal()] = dial.initial; }
    public double[] toSave() { return values.clone(); }
    /** A save's rules; a save from before CBK2 (null) or a shorter array reads the defaults for what it lacks, which move the city as before. */
    public void restore(double[] saved) {
        reset();
        if (saved != null) for (Dial dial : Dial.values())
            if (dial.ordinal() < saved.length) values[dial.ordinal()] = bounded(dial, saved[dial.ordinal()]);
    }
}

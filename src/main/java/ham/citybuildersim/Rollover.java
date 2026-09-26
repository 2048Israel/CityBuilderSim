package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;

/**
 * What falls due next month, refinanced: the treasury's rollover setting and the ledger of the surplus it has netted (0.7.13).
 *
 * WHY. Until 0.7.13 every piece of the city's paper was paid off out of the
 * treasury's cash the month it fell due, and a treasury that could not meet
 * one went to the central bank's advances for it - a term bond's whole face
 * in a month. Jerus: "you should have an option, where it defualt toggles
 * on, so basically the game checks whats going to mature next month, and
 * issues what the treasury is lacking ... you can modify the toggle, so its
 * either manual, aka you do it yourself, or that it defualts to same
 * structure, or that it defualts to 12 month tbill. this doesnt take care of
 * the treasury short of cash, thats the central bank thing, this only takes
 * care of upcoming maturities."
 *
 * THE RULE, EACH MONTH (Game, ROLLING WHAT FALLS DUE, between two presses):
 *
 *   1. WHAT FALLS DUE: the principal every piece of the city's own paper
 *      repays in the month about to run (Debt.principalDueNextMonth()), at
 *      home and abroad, piece by piece.
 *   2. THE NETTING, Jerus's "net of last year's surplus": the budget surplus
 *      the city ran over the last NETTING_MONTHS (NationalAccounts
 *      .getBalance(), a month at a time, as HistorySave keeps it) less what
 *      earlier rollovers netted in the same months - never below nothing,
 *      never more than the treasury holds, never more than falls due. That is
 *      S (netting()). A deficit year nets nothing. What each rollover netted
 *      is kept here (the ledger), so two months in a row cannot net the same
 *      surplus twice.
 *   3. THE ISSUE: what falls due less S, each piece its share pro rata,
 *      sized so the cash it brings covers that share - what the treasury is
 *      lacking. SAME_STRUCTURE rolls each piece into its own instrument, term
 *      and currency (Jerus: "dollars into dollars"; with the window abroad
 *      shut, dollar paper rolls at home, and the log says so);
 *      TWELVE_MONTH_BILL rolls every piece into a local note of BILL_MONTHS.
 *      Pieces rolling into the same paper are one issue, and an issue under
 *      Game.minimumIssueSize() is not arranged: its share is paid out of cash.
 *
 * WHAT ROLLING FOR CASH DOES. Paper sells under its face - a note by its
 * discount, a term loan by its redemption premium (its coupon is a third of
 * its yield), any issue by the underwriting spread and the fixed fee - so
 * the face whose cash covers the maturity is more than the face falling
 * due. Rolling for cash capitalises the interest of the paper it replaces
 * into the new principal, at every roll, at whatever rate the city then
 * pays. Nothing in the model caps what a city may sell of its own paper at
 * home - the rate rises with the debt to its ceiling and the bank buys the
 * rest (0.7.13's notes) - so in a city that runs deficits the principal
 * compounds. Measured on a Lean founding (8 seeds, 4,005 months; 0.7.13's
 * notes, round 2): in seed 0, a city in deficit, every twenty-year loan
 * rolled into one of 1.7 to 2.9 times its face, the city's rate climbing to
 * its ceiling (a twenty-year yield of up to 26.8%), and the city owed $476.6
 * trillion of its own paper by month 3,948 - $760.6 trillion of face issued
 * for $284.1 trillion of cash. The central bank advanced $652.6 trillion
 * for the coupons, 3,633 months with its advances at their ceiling (a
 * coupon is a promise, paid past it), and the city ended at
 * 2,423 people (145,800 by hand). The other seven seeds rolled $1.68 billion
 * of face for $1.26 billion, and repaid all of it. Jerus chose this sizing
 * over rolling face for face (round 1), knowing it.
 *
 * WHAT IT IS NOT. It does not cover a treasury short of cash for its
 * spending - the central bank's advances do that, unchanged - and it only
 * refinances what falls due. The proceeds sit in the treasury for a month
 * before the maturity pays them out, which is what pre-funding a redemption
 * costs, and it is a real practice: Canada's Department of Finance holds
 * liquidity for at least one month of net projected cash flows, including
 * its debt refinancing needs (Debt Management Report 2017-18, Part 1).
 *
 * @author Jerus
 */
public final class Rollover {

    /** The three settings, saved by name. */
    public enum Mode {
        /** Nothing automatic: every maturity is paid out of cash, as before 0.7.13. */
        MANUAL,
        /** Each piece rolls into its own instrument, term and currency - dollar paper at home while the window abroad is shut. The default for a new game. */
        SAME_STRUCTURE,
        /** Every piece rolls into a local note of BILL_MONTHS. */
        TWELVE_MONTH_BILL
    }

    /** How far back the surplus that nets a maturity is read, in months: Jerus's "net of last year's surplus" - a year. */
    public static final int NETTING_MONTHS = 12;

    /** The note TWELVE_MONTH_BILL rolls into, in months: twelve, the longest note the treasury sells (the Finances tab's notes run 3 to 12 months). */
    public static final int BILL_MONTHS = 12;

    /**
     * One issue the rollover makes: the instrument by the quote functions'
     * names ("Note", "Serial", "Term"), its term in their units (months for a
     * note, years for the rest), whether it is sold abroad in dollars, the
     * cash it must bring in local money, the face its quote gives for that
     * cash as the books stand (in local money at the day's rate, for dollar
     * paper), how many pieces falling due roll into it, and how many of those
     * were dollar paper rolled at home because the window abroad was shut.
     */
    public record Issue(String type, int term, boolean foreign, double cash, double face,
                        int pieces, int atHomeForDollars) {

        /** The paper, in words: "a 20-year term loan in dollars", "a 12-month note". */
        public String paper() {
            String what = switch (type) {
                case "Note"   -> term + "-month note";
                case "Serial" -> term + "-year serial bond";
                default       -> term + "-year term loan";
            };
            return "a " + what + (foreign ? " in dollars" : "");
        }

        /** Why it is this paper, in words. */
        public String why(Mode mode) {
            if (mode == Mode.TWELVE_MONTH_BILL) return "every piece into a note at home";
            if (atHomeForDollars <= 0) return "as the paper falling due";
            return atHomeForDollars == pieces
                    ? "at home in place of dollars: the window abroad is shut"
                    : "as the paper falling due, and " + atHomeForDollars
                            + " dollar piece(s) at home: the window abroad is shut";
        }
    }

    /**
     * What the rollover does this month, worked out before it does it: the
     * setting; what falls due, and how much of it abroad; the surplus over
     * the last year and what earlier rollovers netted of it; the treasury's
     * cash; S, what is netted; and the issues. Every figure in local money.
     */
    public record Plan(Mode mode, double due, double dueAbroad, double surplus, double used,
                       double cash, double netted, List<Issue> issues) {
        /** What falls due less what is netted: the cash the issues must bring between them, before any too small to be worth arranging (Game.minimumIssueSize()). */
        public double toRoll() { return Math.max(0, due - netted); }
        /** The cash the issues are sized to bring between them. */
        public double toRaise() {
            double sum = 0;
            for (Issue i : issues) sum += i.cash();
            return sum;
        }
        /** The face their quotes give for it, each quoted on the books as they stand: more than the cash, by the paper's discount, premium and costs. */
        public double issued() {
            double sum = 0;
            for (Issue i : issues) sum += i.face();
            return sum;
        }
    }

    /** The setting in words, for the log and the Finances tab: "by hand", "in the same structure", "into 12-month notes". */
    public static String words(Mode mode) {
        return switch (mode == null ? Mode.MANUAL : mode) {
            case MANUAL            -> "by hand";
            case SAME_STRUCTURE    -> "in the same structure";
            case TWELVE_MONTH_BILL -> "into " + BILL_MONTHS + "-month notes";
        };
    }

    private Mode mode = Mode.MANUAL;

    /** What each rollover netted, and the month it ran in: {month, netted}, oldest first, only the last NETTING_MONTHS kept. */
    private final List<double[]> ledger = new ArrayList<>();

    /* The last rollover, and the run's: what the Finances page and the playtest read, saved (recordToSave()). */
    private int lastMonth = -1;
    private double lastDue, lastNetted, lastIssued, lastRaised;
    private double issuedLifetime, raisedLifetime, nettedLifetime;
    private int issuesLifetime, atHomeForDollarsLifetime;

    public Mode getMode()           { return mode; }
    public void setMode(Mode mode)  { this.mode = mode == null ? Mode.MANUAL : mode; }

    /**
     * S: the surplus over the last year less what earlier rollovers netted in
     * it, never below nothing, never more than the treasury's cash, never
     * more than falls due. A deficit year nets nothing.
     */
    public static double netting(double surplusYear, double usedInYear, double cash, double due) {
        double unused = Math.max(0, surplusYear - Math.max(0, usedInYear));
        return Math.max(0, Math.min(unused, Math.min(Math.max(0, cash), Math.max(0, due))));
    }

    /** What the rollovers run in the NETTING_MONTHS ending with this month netted between them. */
    public double usedInYear(int month) {
        double sum = 0;
        for (double[] entry : ledger) {
            if (entry[0] > month - NETTING_MONTHS && entry[0] <= month) sum += entry[1];
        }
        return sum;
    }

    /** A rollover ran: what fell due, what it netted, the face its issues came to and the cash they raised, in the month it ran in. */
    void record(int month, double due, double netted, double issued, double raised, int issues, int atHomeForDollars) {
        if (netted > 0) ledger.add(new double[] { month, netted });
        ledger.removeIf(e -> e[0] <= month - NETTING_MONTHS);
        lastMonth = month;
        lastDue = due;
        lastNetted = netted;
        lastIssued = issued;
        lastRaised = raised;
        issuedLifetime += issued;
        raisedLifetime += raised;
        nettedLifetime += netted;
        issuesLifetime += issues;
        atHomeForDollarsLifetime += atHomeForDollars;
    }

    /** The month the last rollover ran in, or -1 if none has. */
    public int getLastMonth()          { return lastMonth; }
    /** ...what fell due then, in local money. */
    public double getLastDue()         { return lastDue; }
    /** ...what it netted from the year's surplus. */
    public double getLastNetted()      { return lastNetted; }
    /** ...the face its issues came to, in local money. */
    public double getLastIssued()      { return lastIssued; }
    /** ...and the cash they brought: less than their face by their discount and costs. */
    public double getLastRaised()      { return lastRaised; }
    /** The face the rollover has issued since the setting was first on, in local money. */
    public double getIssuedLifetime()  { return issuedLifetime; }
    /** ...and the cash it raised. */
    public double getRaisedLifetime()  { return raisedLifetime; }
    /** ...and netted from surplus. */
    public double getNettedLifetime()  { return nettedLifetime; }
    /** How many issues it has made. */
    public int getIssuesLifetime()     { return issuesLifetime; }
    /** How many of them rolled dollar paper at home because the window abroad was shut. */
    public int getAtHomeForDollarsLifetime() { return atHomeForDollarsLifetime; }

    /** The ledger, for the save: {month, netted} pairs, oldest first. */
    public double[] ledgerToSave() {
        double[] out = new double[ledger.size() * 2];
        for (int i = 0; i < ledger.size(); i++) {
            out[2 * i] = ledger.get(i)[0];
            out[2 * i + 1] = ledger.get(i)[1];
        }
        return out;
    }

    /** ...and back. Null or an odd length is an older save's, which netted nothing. */
    public void restoreLedger(double[] saved) {
        ledger.clear();
        if (saved == null || saved.length % 2 != 0) return;
        for (int i = 0; i < saved.length; i += 2) ledger.add(new double[] { saved[i], saved[i + 1] });
    }

    /** The last rollover and the run's, for the save. */
    public double[] recordToSave() {
        return new double[] { lastMonth, lastDue, lastNetted, lastIssued, lastRaised,
                issuedLifetime, raisedLifetime, nettedLifetime, issuesLifetime, atHomeForDollarsLifetime };
    }

    /** ...and back. Null or short is an older save's, which never rolled anything. */
    public void restoreRecord(double[] saved) {
        clearRecord();
        if (saved == null || saved.length < 10) return;
        lastMonth = (int) Math.round(saved[0]);
        lastDue = saved[1];
        lastNetted = saved[2];
        lastIssued = saved[3];
        lastRaised = saved[4];
        issuedLifetime = saved[5];
        raisedLifetime = saved[6];
        nettedLifetime = saved[7];
        issuesLifetime = (int) Math.round(saved[8]);
        atHomeForDollarsLifetime = (int) Math.round(saved[9]);
    }

    private void clearRecord() {
        lastMonth = -1;
        lastDue = lastNetted = lastIssued = lastRaised = 0;
        issuedLifetime = raisedLifetime = nettedLifetime = 0;
        issuesLifetime = atHomeForDollarsLifetime = 0;
    }

    /** A city founded from nothing: nothing automatic, nothing netted. Game.newGame() then turns it on. */
    public void reset() {
        mode = Mode.MANUAL;
        ledger.clear();
        clearRecord();
    }

    /** Every figure it keeps in money, in the new unit (Game, THE CURRENCY REFORM). */
    public void redenominate(double scale) {
        for (double[] entry : ledger) entry[1] *= scale;
        lastDue *= scale;
        lastNetted *= scale;
        lastIssued *= scale;
        lastRaised *= scale;
        issuedLifetime *= scale;
        raisedLifetime *= scale;
        nettedLifetime *= scale;
    }
}

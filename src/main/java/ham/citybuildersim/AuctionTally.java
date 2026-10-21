package ham.citybuildersim;

/**
 * The city's auctions counted: how many sales the treasury has booked, what
 * they sold and what they asked, how often the stop sat more than a point over
 * fair, how often the bids fell short, and the sale last booked.
 *
 * WHY (CD2, D33): the cap defaults to one point over fair (Auction.MAX_TAIL)
 * on a judgement that the bids almost always cover well inside it; whether
 * that is so is a count, not an opinion, and the count is only worth anything
 * over a long run. So every auction that BOOKS adds itself here, the harness
 * reads it at the end of a playtest (AuctionCheck 11, LongPlaytest's line),
 * and the Borrow screen shows the last sale's figures from the same record.
 *
 * Only a booking counts. A quote is the same arithmetic run for a screen that
 * redraws every second and for the solvers that try a dozen faces to find one;
 * counted there the tally would measure the screen. Game.bookProceeds() is the
 * one place a domestic sale reaches the treasury, so it is the one place that
 * calls record(). Dollar paper is not sold at auction and is not counted.
 *
 * It is saved (DataSave.getAuctionTally(), toSave() and restore()): a city
 * reloaded in the middle of a run keeps its count, or the count at the end of
 * a long run would depend on how many times the player saved.
 */
public final class AuctionTally {

    /** How far over fair a tail must be to count as over a point: the stop is found to Auction.STOP_TOLERANCE, so a tail that is the cap's own one point, to the arithmetic's last digits, is not over it. */
    private static final double EPSILON = Auction.STOP_TOLERANCE;

    /**
     * The sale last booked, as the screen shows it.
     *
     * @param kind   bill, bond or amortizing
     * @param months the term the paper was sold at, in months
     * @param stop   the clearing yield
     * @param tail   the stop over fair
     * @param cover  what was bid at the ceiling over the issue
     * @param sold   the face sold
     * @param asked  the face asked
     */
    public record Last(Auction.Kind kind, int months, double stop, double tail, double cover,
                       double sold, double asked) { }

    private int sales;
    private double faceSold;
    private double faceAsked;
    private int tailsOverPoint;
    private int shortSales;
    private double largestTail;
    private Last last;

    /** A harness's look at each sale as it is counted (AuctionCheck 11 checks every booked sale, not only the sums). Null in play. */
    java.util.function.Consumer<Auction.Result> probeForTest;

    /** Counts a sale that has been booked. A sale of nothing is not a sale. */
    public void record(Auction.Result r) {
        if (r == null || r.isEmpty() || !(r.sold() > 0)) return;
        sales++;
        faceSold += r.sold();
        faceAsked += r.ask().face();
        if (r.tail() > Auction.MAX_TAIL + EPSILON) tailsOverPoint++;
        if (r.shortSale()) shortSales++;
        largestTail = Math.max(largestTail, r.tail());
        last = new Last(r.ask().kind(), r.ask().months(), r.stop(), r.tail(), r.cover(), r.sold(), r.ask().face());
        if (probeForTest != null) probeForTest.accept(r);
    }

    /** Sales booked. */
    public int auctionSales() { return sales; }

    /** The face those sales sold, money. */
    public double faceSold() { return faceSold; }

    /** The face those sales asked, money: what the city wanted, not what it got. */
    public double faceAsked() { return faceAsked; }

    /** Sales whose tail was strictly more than Auction.MAX_TAIL. */
    public int auctionTailsOverPoint() { return tailsOverPoint; }

    /** Sales whose bids did not reach the issue at the highest yield the city took. */
    public int auctionShortSales() { return shortSales; }

    /** The largest tail any sale cleared at, in yield (.01 is a point); nothing before the first sale. */
    public double largestTail() { return largestTail; }

    /** The sale last booked, or null for a city that has booked none. */
    public Last lastAuction() { return last; }

    /** Nothing counted: a new game, and a save from before the tally. */
    public void clear() {
        sales = 0;
        faceSold = 0;
        faceAsked = 0;
        tailsOverPoint = 0;
        shortSales = 0;
        largestTail = 0;
        last = null;
    }

    /**
     * The tally as the save holds it: sales, face sold, face asked, tails over
     * a point, short sales, the largest tail, then the last sale - whether
     * there is one, its kind (the ordinal), months, stop, tail, cover, sold and
     * asked. Always the same fourteen slots, so a reader never has to ask.
     */
    public double[] toSave() {
        double[] out = new double[14];
        out[0] = sales;
        out[1] = faceSold;
        out[2] = faceAsked;
        out[3] = tailsOverPoint;
        out[4] = shortSales;
        out[5] = largestTail;
        if (last != null) {
            out[6] = 1;
            out[7] = last.kind().ordinal();
            out[8] = last.months();
            out[9] = last.stop();
            out[10] = last.tail();
            out[11] = last.cover();
            out[12] = last.sold();
            out[13] = last.asked();
        }
        return out;
    }

    /** ...and back. A save from before the tally has none and loads an empty one; a shorter array than today's reads what it holds and leaves the rest at nothing. */
    public void restore(double[] saved) {
        clear();
        if (saved == null) return;
        if (saved.length > 0) sales = (int) Math.round(saved[0]);
        if (saved.length > 1) faceSold = saved[1];
        if (saved.length > 2) faceAsked = saved[2];
        if (saved.length > 3) tailsOverPoint = (int) Math.round(saved[3]);
        if (saved.length > 4) shortSales = (int) Math.round(saved[4]);
        if (saved.length > 5) largestTail = saved[5];
        if (saved.length >= 14 && saved[6] > 0) {
            Auction.Kind[] kinds = Auction.Kind.values();
            int k = (int) Math.round(saved[7]);
            if (k >= 0 && k < kinds.length) {
                last = new Last(kinds[k], (int) Math.round(saved[8]), saved[9], saved[10], saved[11], saved[12], saved[13]);
            }
        }
    }
}

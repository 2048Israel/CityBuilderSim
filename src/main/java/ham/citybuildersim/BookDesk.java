package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The debt book as a desk: every figure the book page and a line's page draw (CD6,
 * spec-city-debt.md section 7, boards 1 and 2), a pure model of what the screen shows,
 * so the screen computes nothing.
 *
 * WHY THIS EXISTS. The debt tab worked out its own figures from the pieces - the face,
 * the worth, the carrying value, the premium or discount of each - and a figure the
 * screen works out is one the model never checks (CLAUDE.md: every model figure through
 * a public getter). The desk is those figures, one line a piece by when it falls due,
 * the totals the ledger already gives, and the totals by kind, each read from the
 * ledger's own getters.
 *
 * A pure read: {@link #of(DebtManager)} strikes nothing. It prices the market as it was
 * last struck, so the screen strikes it ONCE for the whole desk (Game.bookDesk()), not
 * once a line, and the month's recorder (DebtManager.recordTheBook()) never does.
 *
 * @author Jerus
 */
public final class BookDesk {

    /**
     * One line of the desk: a piece of paper and the figures the screen prints, in local
     * money. {@code face} is what is still owed at face, {@code coupon} the cash a month
     * (without a linker's last-month uplift), {@code couponRate} the annual figure printed
     * on the paper (a bond's or an amortizing bond's coupon rate, a linker's real coupon,
     * a floater's margin over the dial, nothing for a bill), {@code bookedRate} the issue
     * yield: the effective rate the books charge it at (a linker's is real),
     * {@code pricePer100} where it trades against par, {@code yield} what a buyer earns,
     * {@code realYield} a linker's real yield (NaN for any other piece), {@code worth}
     * what it would cost to buy back today, {@code carried} what the books carry it at,
     * {@code ifClosedNow} the carrying value less the worth (positive: a gain buying it
     * back at fair; its share of the carrying value is {@link Line#ifClosedNowShare()}), {@code vsFace} the face less the worth,
     * {@code exchangeSinceSold} (CD7) what the D$'s move since the sale has done to what is still carried (a gain positive; 0 for
     * domestic paper; its share of the carrying value is {@link Line#exchangeShare()}), {@code duration} in years and
     * {@code valueOfAPoint} what a point on the curve takes off its worth
     * (DebtManager.valueOfAPoint(), which says what it is for a callable line and a
     * floater), and the holders as shares of the face, 0 to 1 (dollar paper is the
     * world's).
     */
    public record Line(Debt debt, double face, double coupon, double couponRate, double bookedRate,
                       double pricePer100, double yield, double realYield, double worth, double carried,
                       double ifClosedNow, double vsFace, double exchangeSinceSold, double duration, double valueOfAPoint, int monthsLeft,
                       double households, double bank, double centralBank, double firms, double world) {

        /** What it is carried at for each 100 of what is still owed on it (CD6): the carrying value over the face, times 100; 0 for a line that owes nothing. */
        public double carriedPer100() {
            return face > 0 ? carried / face * 100 : 0;
        }

        /**
         * What closing the line now would book as a share of what the books carry it at (CD6, Jerus 2026-10-23): the gain
         * {@link #ifClosedNow()} over the carrying value, a fraction (0.338 is a gain of 33.8 per cent of what is carried,
         * below nothing a loss); 0 for a line that is carried at nothing. The screen prints it in green or red beside the
         * D$ and divides nothing itself.
         */
        public double ifClosedNowShare() {
            return carried > 0 ? ifClosedNow / carried : 0;
        }

        /**
         * What the exchange has done to what is carried since the sale as a share of what the books carry it at (CD7): the
         * {@link #exchangeSinceSold()} over the carrying value, a fraction (-0.10 is a D$ that has lost 10 per cent against
         * the money the piece is written in since it was sold: the exchange has cost a tenth of what is carried now; a gain
         * positive); 0 for a line carried at nothing, for domestic paper and for a piece whose sale kept no rate. The screen
         * prints it in green or red and divides nothing itself.
         */
        public double exchangeShare() {
            return carried > 0 ? exchangeSinceSold / carried : 0;
        }
    }

    private final List<Line> lines;
    private final double face, carried, worth, coupon, valueOfAPoint;
    /** CD7: the lines' exchange since the sale added up, and the carrying value of the foreign ones, which the share is over. */
    private final double exchangeSinceSold, foreignCarried;
    private final double[] faceByKind = new double[DebtManager.BOOK_KINDS.length];
    private final double[] carriedByKind = new double[DebtManager.BOOK_KINDS.length];
    private final double[] worthByKind = new double[DebtManager.BOOK_KINDS.length];

    private BookDesk(DebtManager ledger) {
        List<Line> built = new ArrayList<>();
        double point = 0, exchange = 0, abroad = 0;
        for (Debt d : ledger.getDebt()) {
            Line line = line(ledger, d);
            built.add(line);
            point += line.valueOfAPoint();
            exchange += line.exchangeSinceSold();
            if (d.isForeign()) abroad += line.carried();
            int kind = DebtManager.bookKind(d.getType());
            faceByKind[kind] += line.face();
            carriedByKind[kind] += line.carried();
            worthByKind[kind] += line.worth();
        }
        // By when it falls due; a stable sort keeps the ledger's order within a date.
        built.sort(Comparator.comparingInt(Line::monthsLeft));
        this.lines = List.copyOf(built);
        this.face = ledger.getAllPrincipal();
        this.carried = ledger.getCarryingValue();
        this.worth = ledger.getTotalMarketValue();
        this.coupon = ledger.getMonthlyCoupon();
        this.valueOfAPoint = point;
        this.exchangeSinceSold = exchange;
        this.foreignCarried = abroad;
    }

    /** The desk on the ledger's market as it stands: every piece a line, sorted by when it falls due. */
    public static BookDesk of(DebtManager ledger) {
        return new BookDesk(ledger);
    }

    /** One piece's line, for its own page. */
    public static Line line(DebtManager ledger, Debt debt) {
        double face = debt.getOustandingPrincipal();
        double worth = ledger.marketValue(debt);
        double carried = debt.getCarryingValue();
        double couponRate = switch (debt) {
            case LongTermBond bond -> bond.couponRate();
            case MediumTermBond bond -> bond.couponRate();
            case InflationLinkedBond linked -> linked.getRealCoupon();
            case FloatingRateNote note -> note.getMargin();
            default -> 0;
        };
        double realYield = debt instanceof InflationLinkedBond linked ? ledger.realYieldOf(linked) : Double.NaN;
        double share = face > 0 ? 1 / face : 0;
        boolean abroad = debt.isForeign();
        return new Line(debt, face, debt.getMonthlyCoupon(), couponRate, debt.getEffectiveRate(),
                ledger.priceOfPar(debt), ledger.yieldOf(debt), realYield, worth, carried,
                carried - worth, face - worth, debt.exchangeSinceSold(), ledger.durationOf(debt), ledger.valueOfAPoint(debt),
                debt.getRemainingMonths(),
                abroad ? 0 : debt.householdPrincipal() * share,
                abroad ? 0 : debt.bankPrincipal() * share,
                abroad ? 0 : debt.centralBankPrincipal() * share,
                abroad ? 0 : debt.firmPrincipal() * share,
                abroad ? 1 : debt.worldPrincipal() * share);
    }

    /** Every line, by when it falls due (the fewest months left first). */
    public List<Line> lines() { return lines; }

    /** What the city owes at face: DebtManager.getAllPrincipal(). */
    public double face() { return face; }

    /** ...what the books carry it at: DebtManager.getCarryingValue(). */
    public double carried() { return carried; }

    /** ...what it would cost to buy back today: DebtManager.getTotalMarketValue(). */
    public double worth() { return worth; }

    /** ...the cash it pays a month in coupon: DebtManager.getMonthlyCoupon(). */
    public double coupon() { return coupon; }

    /** ...what a point on the curve takes off its worth: the lines' summed. */
    public double valueOfAPoint() { return valueOfAPoint; }

    /** ...the carrying value less the worth: what closing the whole book now would book as a gain on the statement (below nothing, a loss). */
    public double ifClosedNow() { return carried - worth; }

    /** ...that gain as a share of the carrying value, a fraction (CD6, Jerus 2026-10-23): the book's {@link #ifClosedNow()} over {@link #carried()}; 0 for a book carried at nothing. The same figure as a line's, for the whole book. */
    public double ifClosedNowShare() { return carried > 0 ? (carried - worth) / carried : 0; }

    /** ...the face less the worth: what the market's price saves against what is owed. */
    public double vsFace() { return face - worth; }

    /** ...what the D$'s move since the sales has done to what the dollar lines carry (CD7): the lines' {@link Line#exchangeSinceSold()} added up, a gain positive; 0 with no dollar paper. */
    public double exchangeSinceSold() { return exchangeSinceSold; }

    /** ...that as a share of the dollar lines' carrying value, a fraction (CD7): {@link #exchangeSinceSold()} over what the foreign lines are carried at; 0 with none. A line's own is {@link Line#exchangeShare()}. */
    public double exchangeShare() { return foreignCarried > 0 ? exchangeSinceSold / foreignCarried : 0; }

    /** The face of one kind of paper (a DebtManager.BOOK_KINDS entry): the bars by kind. A type that is none of them reads as a term loan's, as the manager's books do. */
    public double faceOf(String kind) { return faceByKind[DebtManager.bookKind(kind)]; }

    /** ...its carrying value. */
    public double carriedOf(String kind) { return carriedByKind[DebtManager.bookKind(kind)]; }

    /** ...and its worth. */
    public double worthOf(String kind) { return worthByKind[DebtManager.bookKind(kind)]; }
}

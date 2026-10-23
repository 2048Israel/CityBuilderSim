package ham.citybuildersim;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * What a loan would cost, worked out BEFORE the player agrees to it.
 *
 * WHY THIS EXISTS
 *
 * The market prices a loan with the loan itself on the books - ask for more and
 * you are quoted more, because you are a worse credit the moment the money
 * lands. That is the right behaviour and it was invisible: the player picked an
 * amount, hit Confirm, and only then found out what it cost. You cannot make a
 * borrowing decision after you have borrowed.
 *
 * THE RULE THIS TYPE ENFORCES
 *
 * A quote that is computed separately from the deal is a second definition of
 * the deal, and second definitions drift. So the quote is not a preview of the
 * arithmetic - it IS the arithmetic. Game.quoteTBill()/quoteMediumBond()/
 * quoteLongBond() - and quoteLongBondForCash(), the build screen's bond
 * (0.7.10) - work the terms out and return one of these; the matching
 * handle*() then books exactly the numbers in the record it was handed and
 * computes nothing of its own. There is no path by which the screen can promise
 * one thing and the ledger record another.
 *
 * AND SINCE CD2 THE DEAL IS AN AUCTION. The city's own paper is sold at
 * auction (Auction): the quote carries the auction's result - the stop, the
 * tail over fair, the cover, who took it - and a sale whose bids did not reach
 * the issue at the highest yield the city took quotes what it SOLD, not what
 * it asked (isShort()). Dollar paper is not sold at auction, so its quote
 * carries none.
 *
 * Everything here is a term of the loan, not a description of it - the wording
 * lives in summary(), so the console and the JavaFX screens say the same thing.
 *
 * @param instrument      "Note" (a bill), "Serial" (the amortizing bond) or "Term" (a bond)
 * @param duration        months for a bill, years for either bond
 * @param requested       what the player asked for: the cash for a bill and
 *                        for the ForCash quotes, the face for a bond sold by face
 * @param marketRate      the yield struck, WITH this loan priced in: the
 *                        auction's stop, or the world's rate for dollar paper
 * @param rateBefore      the standing rate before it - the two differ by
 *                        exactly what this borrowing costs the city's credit
 * @param faceValue       what goes on the books; what must be repaid. A sale
 *                        that came up short quotes what it sold
 * @param cashReceived    what actually reaches the treasury
 * @param monthlyInterest the recurring bill
 * @param totalCost       every dollar of credit cost over the full term:
 *                        discount or premium, plus all the coupons
 * @param auction         the auction this was sold at; null for dollar paper
 * @param coupon          the annual coupon the sale sets: the auction's (a
 *                        bond's, rounded down to an eighth of a point; the
 *                        stop itself for an amortizing bond) or the world's
 *                        for a dollar bond; 0 for a bill
 * @param callable        true for a bond sold callable (CD4): the coupon
 *                        carries the city's right to call it whole at 100
 * @param firstCallMonth  the game month from which it may be called (a line
 *                        reopened keeps its own); -1 when not callable
 */
public record DebtQuote(
        String instrument,
        int duration,
        double requested,
        double marketRate,
        double rateBefore,
        double faceValue,
        double cashReceived,
        double monthlyInterest,
        double totalCost,
        Auction.Result auction,
        double coupon,
        boolean callable,
        int firstCallMonth) {

    /** A quote of paper that cannot be called: every sale before CD4, and every one that does not ask for the option. */
    public DebtQuote(String instrument, int duration, double requested, double marketRate, double rateBefore,
                     double faceValue, double cashReceived, double monthlyInterest, double totalCost,
                     Auction.Result auction, double coupon) {
        this(instrument, duration, requested, marketRate, rateBefore, faceValue, cashReceived,
                monthlyInterest, totalCost, auction, coupon, false, -1);
    }

    /** Years for a bond, months for a bill - the unit the duration is in. */
    public String timeUnit() {
        return "Note".equals(instrument) ? "months" : "years";
    }

    /**
     * What the borrowing does to the city's credit, as a sentence.
     *
     * The point of showing both rates is that the gap between them is not a
     * quirk of the display - it is the price of the size of the ask, and it is
     * the number a player should be looking at before deciding to ask for less.
     */
    public String creditImpact() {
        double moved = (marketRate - rateBefore) * 100;
        if (moved < 0.005) {
            return String.format("Market rate: %.2f%% (unchanged by this)", marketRate * 100);
        }
        // An arrow, not "->" (0.7.32, the Finances spec's B11): the line is
        // printed on the offer cards of the land office, Build and Finances.
        return String.format("Market rate: %.2f%% → %.2f%%  (+%.2f pts for asking this much)",
                rateBefore * 100, marketRate * 100, moved);
    }

    /**
     * The terms, in the vocabulary a bond is actually described in.
     *
     * PAR, DISCOUNT AND PREMIUM ARE STATED, not left for the player to work out
     * from two dollar figures. "Received $198,488 against $200,000 of par" is
     * arithmetic; "issued at 99.2 - a discount" is what a bond desk would say,
     * and it is the phrasing that makes the three instruments comparable at all,
     * since they differ mainly in where they sit against par and why.
     */
    public String summary() {
        // A tiny sale (Game.isTinySale()): no face, but the auction's result, which says why it is not done.
        if (isEmpty() && auction != null && auction.sold() > 0) {
            return String.format("Nothing sold: the bids reached only $%s of the $%s asked, at the highest yield the city took"
                    + " (%.3f%%), and a sale of $%s raising $%s is under the smallest deal worth arranging.",
                    f(auction.sold()), f(auction.ask().face()), auction.ceiling() * 100,
                    f(auction.sold()), f(Math.max(0, auction.proceeds())));
        }
        String sale = saleLines();
        return switch (instrument) {
            case "Note" -> String.format(
                    "Par value (repay):    $%s%n"
                    + "Cash received:        $%s%n"
                    + "Issued at:            %.2f of par  (a discount - the bill pays no coupon)%n"
                    + "Cost of the credit:   $%s%n"
                    + "Term:                 %d months, sold at a yield of %.2f%%/yr%s",
                    f(faceValue), f(cashReceived), pricePerPar(),
                    f(totalCost), duration, marketRate * 100, sale);

            case "Serial" -> String.format(
                    "Par value:            $%s%n"
                    + "Cash received:        $%s   (issued at %.2f of par)%n"
                    + "Coupon:               %.3f%%, paid monthly on the balance outstanding%n"
                    + "First payment:        $%s   (it falls as principal amortises)%n"
                    + "Principal:            repaid in %d annual slices - no lump at the end%n"
                    + "Total cost of credit: $%s over %d years%s",
                    f(faceValue), f(cashReceived), pricePerPar(),
                    couponRate() * 100, f(monthlyInterest),
                    Math.max(1, duration), f(totalCost), duration, sale);

            default -> String.format(
                    "Par value (repay):    $%s%n"
                    + "Cash received:        $%s   (issued at %.2f of par)%n"
                    + "Coupon:               %.3f%%, paid monthly on the full par value%n"
                    + "Monthly payment:      $%s%n"
                    + "Principal:            the WHOLE par value falls due in %d years%n"
                    + "Total cost of credit: $%s%s",
                    f(faceValue), f(cashReceived), pricePerPar(),
                    couponRate() * 100, f(monthlyInterest), duration, f(totalCost), sale);
        };
    }

    /**
     * What the auction did, for a sale at home: the stop and the tail over
     * fair, the cover, the coupon and the price per 100, and "sold X of Y" when
     * the bids did not reach the issue at the highest yield the city took.
     * Nothing for dollar paper, which is not sold at auction.
     */
    private String saleLines() {
        if (auction == null || auction.isEmpty()) return "";
        StringBuilder out = new StringBuilder();
        out.append(String.format("%nSold at auction:       stop %.3f%%, %.0f bp over fair, covered %.2fx",
                auction.stop() * 100, auction.tail() * 10_000, auction.cover()));
        if (auction.coupon() > 0) {
            out.append(String.format("%nCoupon and price:     %.3f%%, %.3f per 100", auction.coupon() * 100, auction.pricePer100()));
        } else {
            out.append(String.format("%nPrice:                %.3f per 100", auction.pricePer100()));
        }
        if (callable) {
            out.append(String.format("%nCallable:              at 100, from month %d - the coupon carries the option", firstCallMonth));
        }
        if (auction.shortSale()) {
            out.append(String.format("%nSOLD SHORT:           sold $%s of $%s - the bids did not reach the issue at %.3f%%, the highest yield the city took",
                    f(auction.sold()), f(auction.ask().face()), auction.ceiling() * 100));
        }
        return out.toString();
    }

    /** Where it was issued against par, the way bonds are quoted. */
    public double pricePerPar() {
        return faceValue > 0 ? cashReceived / faceValue * 100 : 0;
    }

    /** True when the city receives less than it will repay. */
    public boolean isDiscount() {
        return cashReceived < faceValue - 1e-9;
    }

    /**
     * The annual coupon the paper carries: the one the sale set, no longer
     * backed out of the monthly bill (CD2). Backed out only for a quote that
     * set none - a bill's is nothing either way.
     */
    public double couponRate() {
        if (coupon > 0) return coupon;
        return faceValue > 0 ? (monthlyInterest * 12) / faceValue : 0;
    }

    /** True when this was sold at auction and the bids did not reach the issue at the highest yield the city took: the quote is what it sold. */
    public boolean isShort() {
        return auction != null && auction.shortSale();
    }

    /** True if the city is receiving nothing worth booking. */
    public boolean isEmpty() {
        return requested <= 0 || faceValue <= 0;
    }

    /**
     * Every payment this paper would ask, month by month, index i paid i + 1
     * months after it is issued, times `toLocal` (1 for the city's own paper;
     * the exchange rate, for a dollar quote's figures into local money):
     * a bill's face at its end; an amortizing bond's coupon on what is still
     * out and a slice of the face each anniversary; a bond's coupon every
     * month and its face at the end - for the months its line has left when
     * it reopens one. The Finances tab's own (its proposedSchedule) until
     * 0.7.32, moved here so the ladder it draws with the issue in it is the
     * model's: built from the quote rather than from an instrument, because
     * the instrument does not exist yet and constructing one to draw a
     * picture is how a reporting layer ends up changing the thing it
     * reports.
     */
    public double[] schedule(double toLocal) {
        int months = auction != null && !auction.isEmpty() ? auction.ask().months()
                : "Note".equals(instrument) ? duration : duration * 12;
        if (months <= 0 || isEmpty()) return new double[0];
        double[] flows = new double[months];
        switch (instrument) {
            case "Note" -> flows[months - 1] = faceValue;
            case "Serial" -> {
                // A slice of principal on each anniversary, and a coupon on the
                // declining balance - which is why the coupon falls rather
                // than sitting flat.
                int slices = Math.max(1, duration);
                double perSlice = faceValue / slices;
                double outstanding = faceValue;
                double monthlyRate = couponRate() / 12;
                for (int m = 0; m < months; m++) {
                    flows[m] = outstanding * monthlyRate;
                    if ((m + 1) % 12 == 0) {
                        double due = Math.min(perSlice, outstanding);
                        flows[m] += due;
                        outstanding -= due;
                    }
                }
            }
            default -> {
                java.util.Arrays.fill(flows, monthlyInterest);
                flows[months - 1] += faceValue;
            }
        }
        if (toLocal != 1) for (int m = 0; m < months; m++) flows[m] *= toLocal;
        return flows;
    }

    /**
     * A figure on this quote, in dollars.
     *
     * THE MODEL COUNTS IN THOUSANDS and this class was printing the raw number
     * with a dollar sign in front of it, so a $2.3B bond read as "$2,300,000"
     * on the confirmation screen and as "$2.3B" on the debt vital a moment
     * later. Multiplied out here, once, because every figure on this record is
     * money in the same unit.
     */
    private static String f(double v) {
        return FORMAT.format(v * 1000);
    }

    private static final NumberFormat FORMAT = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        FORMAT.setMaximumFractionDigits(2);
        FORMAT.setMinimumFractionDigits(0);
    }
}

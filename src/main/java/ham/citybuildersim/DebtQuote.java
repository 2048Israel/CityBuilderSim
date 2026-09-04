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
 * quoteLongBond() work the terms out and return one of these; the matching
 * handle*Logic() then books exactly the numbers in the record it was handed and
 * computes nothing of its own. There is no path by which the screen can promise
 * one thing and the ledger record another.
 *
 * Everything here is a term of the loan, not a description of it - the wording
 * lives in summary(), so the console and the JavaFX screens say the same thing.
 *
 * @param instrument      "Note", "Serial" or "Term"
 * @param duration        months for a T-Bill, years for either bond
 * @param requested       what the player asked to receive
 * @param marketRate      the rate struck, WITH this loan priced in
 * @param rateBefore      the standing rate before it - the two differ by
 *                        exactly what this borrowing costs the city's credit
 * @param faceValue       what goes on the books; what must be repaid
 * @param cashReceived    what actually reaches the treasury
 * @param monthlyInterest the recurring bill
 * @param totalCost       every dollar of credit cost over the full term:
 *                        discount or premium, plus all the coupons
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
        double totalCost) {

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
        return String.format("Market rate: %.2f%% -> %.2f%%  (+%.2f pts for asking this much)",
                rateBefore * 100, marketRate * 100, moved);
    }

    /** The terms, as the player should see them before confirming. */
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
        return switch (instrument) {
            case "Note" -> String.format(
                    "Par value (repay):    $%s%n"
                    + "Cash received:        $%s%n"
                    + "Issued at:            %.2f of par  (a discount - the note pays no coupon)%n"
                    + "Cost of the credit:   $%s%n"
                    + "Term:                 %d months, discounted at %.2f%%/yr",
                    f(faceValue), f(cashReceived), pricePerPar(),
                    f(totalCost), duration, marketRate * 100);

            case "Serial" -> String.format(
                    "Par value:            $%s%n"
                    + "Cash received:        $%s   (issued at %.2f of par)%n"
                    + "Coupon:               %.2f%%, paid monthly on the balance outstanding%n"
                    + "First payment:        $%s   (it falls as principal amortises)%n"
                    + "Principal:            repaid in %d annual slices - no lump at the end%n"
                    + "Total cost of credit: $%s over %d years",
                    f(faceValue), f(cashReceived), pricePerPar(),
                    marketRate * 100, f(monthlyInterest),
                    Math.max(1, duration), f(totalCost), duration);

            default -> String.format(
                    "Par value (repay):    $%s%n"
                    + "Cash received:        $%s   (issued at %.2f of par - a deep discount)%n"
                    + "Coupon:               %.2f%%, paid monthly on the full par value%n"
                    + "Monthly payment:      $%s%n"
                    + "Principal:            the WHOLE par value falls due in %d years%n"
                    + "Total cost of credit: $%s",
                    f(faceValue), f(cashReceived), pricePerPar(),
                    couponRate() * 100, f(monthlyInterest), duration, f(totalCost));
        };
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
     * The coupon actually charged monthly, backed out of the monthly bill.
     *
     * A long bond's headline market rate is NOT what it pays monthly - the whole
     * instrument is a low coupon bought with a redemption premium, and printing
     * the market rate next to "monthly interest" would misdescribe it.
     */
    public double couponRate() {
        return faceValue > 0 ? (monthlyInterest * 12) / faceValue : 0;
    }

    /** True if the city is receiving nothing worth booking. */
    public boolean isEmpty() {
        return requested <= 0 || faceValue <= 0;
    }

    private static String f(double v) {
        return FORMAT.format(v);
    }

    private static final NumberFormat FORMAT = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        FORMAT.setMaximumFractionDigits(2);
        FORMAT.setMinimumFractionDigits(0);
    }
}

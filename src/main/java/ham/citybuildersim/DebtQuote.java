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
 * @param instrument      "T-Bill", "Medium-Term" or "Long-Term"
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
        return "T-Bill".equals(instrument) ? "months" : "years";
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
    public String summary() {
        return switch (instrument) {
            case "T-Bill" -> String.format(
                    "Face Value (repay):   $%s%n"
                    + "Cash Received:        $%s%n"
                    + "Discount Cost:        $%s%n"
                    + "Term:                 %d months @ %.2f%%",
                    f(faceValue), f(cashReceived), f(totalCost), duration, marketRate * 100);

            case "Medium-Term" -> String.format(
                    "Principal:            $%s%n"
                    + "Cash Received:        $%s%n"
                    + "Monthly Interest:     $%s%n"
                    + "Total Cost of Credit: $%s%n"
                    + "Term:                 %d years @ %.2f%%",
                    f(faceValue), f(cashReceived), f(monthlyInterest),
                    f(totalCost), duration, marketRate * 100);

            default -> String.format(
                    "Cash Received:        $%s%n"
                    + "Repay at Maturity:    $%s%n"
                    + "Monthly Interest:     $%s%n"
                    + "Total Cost of Credit: $%s%n"
                    + "Term:                 %d years @ %.2f%% coupon",
                    f(cashReceived), f(faceValue), f(monthlyInterest),
                    f(totalCost), duration, couponRate() * 100);
        };
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

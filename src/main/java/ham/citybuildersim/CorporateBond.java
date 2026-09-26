package ham.citybuildersim;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A corporate bond: a sector's debt to investors, issued at par through
 * bookbuilding, paying a fixed coupon every month and its whole face at the
 * end.
 *
 * ==================== WHY THIS EXISTS ====================
 *
 * Jerus, 2026-09-24: "notes are the loans businesses make to the banks, and
 * bonds are debts that they issue to investors ... bonds will have a slightly
 * higher rate than notes, and households can buy them, but banks also buy
 * them as, only if it wants, and yes they are tradeable so they have par
 * value and all." Until 0.7.12 a business could borrow only from the one
 * bank, so a whole industry's debt sat on one balance sheet: with the
 * landlords on insured mortgages (0.7.11) the sector that failed the bank
 * owed it five to six times its equity (the-landlords-take-a-mortgage.md,
 * section 4). A bond is the second place to go.
 *
 * WHY "BANK LOANS" AND NOT "NOTES" ON THE SCREENS. Jerus calls a business's
 * bank loan a note. The city's own short paper (ShortTermTBill) and the build
 * screen's six-month offer (Game.BUILD_NOTE_MONTHS) are already called notes,
 * so a screen that said "notes" would name three different things; the
 * screens say "bank loans" for the business's borrowing from the bank, and
 * "bonds" for this.
 *
 * ==================== THE TERMS ====================
 *
 *   ISSUED AT PAR by a sector, the coupon set by bookbuilding - the yield
 *   that fills the issue (BondMarket, THE BOOK IS BUILT), so the bond prices
 *   at exactly its face the day it is sold.
 *
 *   TEN YEARS, bullet: TERM_MONTHS. Ten years is the benchmark tenor of
 *   investment-grade corporate issuance - the maturity a corporate spread is
 *   quoted against ("the spread to the 10-year Treasury") and the brief's own
 *   anchor - and it is the term the model already prices at: the insured
 *   mortgage's (Mortgage.MORTGAGE_TERM_MONTHS) and the ten-year point of the
 *   city's curve. One standard term rather than a choice among 5, 7, 10 and
 *   30: a choice by cost would trade the curve's slope against spreading the
 *   issuing costs over more years, and every term chosen is one more order
 *   book. 0.7.12 shipped the one term; a choice among them was not built.
 *
 *   COUPONS MONTHLY, a twelfth of the annual coupon on the face outstanding
 *   (COUPONS_A_YEAR). Real bonds pay twice a year; the model's clock is the
 *   month, and a coupon paid monthly is the same money sooner by a few
 *   weeks. The coupon is an interest expense on the issuer's income
 *   statement, like a loan's (BusinessDebtManager.getMonthlyInterest()).
 *
 *   THE WHOLE FACE AT MATURITY, paid from the issuer's till; what it cannot
 *   pay is a short month like any other - it sells what it holds, then the
 *   shortfall desk lends or a new bond raises the rest, the choice again,
 *   and what nobody lends defaults that month (BondMarket.redeemMaturing();
 *   BusinessDebtManager, CAN'T PAY MEANS DEFAULT, since round 4).
 *
 * ==================== WHO HOLDS IT ====================
 *
 * Four holder classes, in face: the households (their face together - since
 * round 2 each cell holds its own face of the bond, Household.bondFace, and
 * this is the cells' sum; round 1 held one pool with a claim per cell), the
 * bank (with what it paid, its cost), each company that bought it by sector,
 * and the world. The four always add up to the face outstanding; a default
 * writes every one of them down by the same share (writeDown()).
 *
 * WHAT IT IS WORTH is priceAtYield() at the city's curve for the months left
 * plus the issuer's expected loss for a bondholder - BondMarket.modelYield().
 * What it trades at is the order book's.
 */
public class CorporateBond {

    /** The term every bond is issued at: ten years, the benchmark tenor of investment-grade corporate issuance, and the term the model already prices at (the insured mortgage's, Mortgage.MORTGAGE_TERM_MONTHS). */
    public static final int TERM_MONTHS = Mortgage.MORTGAGE_TERM_MONTHS;

    /** Coupons a year: one a month, the model's month; real bonds pay twice a year. */
    public static final int COUPONS_A_YEAR = 12;

    int id;
    String issuer;
    /** The face it was issued at. */
    double issued;
    /** The face outstanding: the issue less what defaults wrote off. */
    double face;
    /** The annual coupon, fixed at issue. */
    double coupon;
    int issueMonth;
    int maturityMonth;

    /* ---- who holds it, in face ---- */
    double households;
    double bank;
    /** What the bank paid for what it holds - its carrying value, written down with the face. */
    double bankCost;
    double world;
    Map<String, Double> companies = new LinkedHashMap<>();

    /** Face written off by defaults over its life. */
    double writtenOff;

    CorporateBond() { }

    CorporateBond(int id, String issuer, double face, double coupon, int issueMonth, int termMonths) {
        this.id = id;
        this.issuer = issuer;
        this.issued = face;
        this.face = face;
        this.coupon = coupon;
        this.issueMonth = issueMonth;
        this.maturityMonth = issueMonth + termMonths;
    }

    public int id()             { return id; }
    public String issuer()      { return issuer; }
    public double issued()      { return issued; }
    public double face()        { return face; }
    public double coupon()      { return coupon; }
    public int issueMonth()     { return issueMonth; }
    public int maturityMonth()  { return maturityMonth; }
    public double households()  { return households; }
    public double bank()        { return bank; }
    public double bankCost()    { return bankCost; }
    public double world()       { return world; }
    public double writtenOff()  { return writtenOff; }
    public double company(String sector) { return companies.getOrDefault(sector, 0.0); }
    public Map<String, Double> companies() { return java.util.Collections.unmodifiableMap(companies); }
    /** What every company holds together. */
    public double companiesTotal() {
        double t = 0;
        for (double v : companies.values()) t += v;
        return t;
    }

    /** The name its order book trades under. */
    public String instrument()  { return "bond-" + id; }

    /** Months to maturity at this month, never below nothing. */
    public int remainingMonths(int month) { return Math.max(0, maturityMonth - month); }

    /** The coupon due this month on the face outstanding: a twelfth of the annual coupon. */
    public double monthlyCoupon() { return face * coupon / COUPONS_A_YEAR; }

    /** True once its maturity month has come. */
    public boolean isMatured(int month) { return month >= maturityMonth; }

    /** What the four holder classes hold, added up: the face outstanding, to the dust. */
    public double held() { return households + bank + world + companiesTotal(); }

    /* ============================== its value ============================== */

    /**
     * WHAT A UNIT OF FACE IS WORTH at an annual yield: the coupons left and
     * the face, each discounted monthly at yield / 12 -
     *
     *     price = (c / 12) x (1 - (1 + r)^-n) / r + (1 + r)^-n,   r = y / 12
     *
     * so a bond priced at its own coupon is worth exactly par, whatever is
     * left of it; at a higher yield less, at a lower more. With nothing left,
     * the face.
     */
    public static double priceAtYield(double coupon, int months, double yield) {
        if (months <= 0) return 1;
        double r = yield / COUPONS_A_YEAR;
        double c = coupon / COUPONS_A_YEAR;
        if (Math.abs(r) < 1e-12) return c * months + 1;
        double discount = Math.exp(-months * Math.log1p(r));
        return c * (1 - discount) / r + discount;
    }

    /**
     * ...and the yield a price implies: the one annual yield at which
     * priceAtYield() gives it, found by bisection - the price falls as the
     * yield rises, so there is exactly one - between -50% and 1,000% a year.
     * NaN for a price that is not a price.
     */
    public static double yieldAtPrice(double coupon, int months, double price) {
        if (!(price > 0) || !Double.isFinite(price)) return Double.NaN;
        if (months <= 0) return coupon;
        double lo = -.5, hi = 10;
        for (int i = 0; i < 200; i++) {
            double mid = (lo + hi) / 2;
            if (priceAtYield(coupon, months, mid) > price) lo = mid; else hi = mid;
        }
        return (lo + hi) / 2;
    }

    /** What a unit of face of this bond is worth at this yield, this month. */
    public double priceAt(double yield, int month) {
        return priceAtYield(coupon, remainingMonths(month), yield);
    }

    /* ========================= a default, and a reform ========================= */

    /**
     * Writes the bond down to this share of its face: every holder's face by
     * the same share, and the bank's cost with its face.
     *
     * @return the face written off, all holders together
     */
    double writeDown(double scale) {
        double keep = Math.max(0, Math.min(1, scale));
        double before = face;
        face *= keep;
        households *= keep;
        bank *= keep;
        bankCost *= keep;
        world *= keep;
        companies.replaceAll((k, v) -> v * keep);
        writtenOff += before - face;
        return before - face;
    }

    /** Every money figure in the new unit; the coupon, the months and the ratios do not move. */
    void redenominate(double scale) {
        issued *= scale;
        face *= scale;
        households *= scale;
        bank *= scale;
        bankCost *= scale;
        world *= scale;
        writtenOff *= scale;
        companies.replaceAll((k, v) -> v * scale);
    }
}

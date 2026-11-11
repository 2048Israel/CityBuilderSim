package ham.citybuildersim;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * The test player's annual response to the government's wealth, shared by the playtest and its measuring drivers.
 *
 * WHY (GV1/GV2, spec-wealth.md 3.2). Scripted income and property tax changes ignored the government's accumulating wealth and fought the
 * study's policy rule. This is a player's move, called by LongPlaytest after a successful January; Game never calls it for a live city.
 * January has finished before this runs, so this January's newly opened policy levers accept their setters. The prototype acted after
 * December instead, before those gates opened. The first eligible January is month 13, with a full year's revenue history.
 */
public final class GovernmentPlayer {

    /** The prototype's annual profit-tax move: five percentage points, small enough to give each year's response a chance to work. */
    public static final double PROFIT_STEP = .05;
    /** The prototype's annual sales-tax move: two percentage points, since it taxes turnover rather than profit. */
    public static final double SALES_STEP = .02;
    /** The annual pension and EI move: five percentage points, the prototype's transfer step extended to both promised benefits. */
    public static final double TRANSFER_STEP = .05;
    /** The prototype's estimated poor-side profit-tax ceiling, to rebuild the buffer without an ever-rising tax. */
    public static final double POOR_PROFIT_MAX = .30;
    /** The prototype's poor-side sales-tax ceiling: stop at the tax's founding rate rather than raising the whole turnover bill further. */
    public static final double POOR_SALES_MAX = TaxPolicy.DEFAULT_INCOME_TAX;
    /** The prototype's estimated pension floor: rebuilding the buffer can trim a promise, but does not remove it. */
    public static final double POOR_PENSION_MIN = .30;
    /** EI's poor-side floor is its existing default replacement, so the rule unwinds its increases rather than cutting the founding benefit. */
    public static final double POOR_EI_MIN = Unemployment.DEFAULT_BENEFIT_RATE;

    /** The January already considered for each city; weak keys let finished scratch cities go, without sharing a player's year across cities. */
    private static final Map<Game, Integer> considered = new WeakHashMap<>();

    private GovernmentPlayer() { }

    /**
     * Consider this completed January once, returning true only if at least one dial actually moved. Call only after a successful month:
     * a long skip reaches every January, a refused month never moves a dial, and loading a January does not replay a move already made.
     * An in-band or unmeasurable January also counts as considered: retrying it after another move cannot trigger a second annual review.
     * State survives separate run() calls on this Game; a newly loaded Game is a new player session and starts with its next completed month.
     */
    public static synchronized boolean step(Game g) {
        int month = g.getMonth();
        if (month <= 1 || CityCalendar.monthOfYear(month) != 1
                || considered.getOrDefault(g, -1) >= month) return false;
        considered.put(g, month);
        double years = GovernmentView.of(g).wealthYears();
        if (!Double.isFinite(years)) return false;

        TaxPolicy tax = g.getEconomyManager().getTaxPolicy();
        TreasuryFund fund = g.getFund();
        double profit = tax.getProfitTaxRate(), sales = tax.getSalesTaxRate();
        double pension = tax.getPensionReplacement(), ei = tax.getEiBenefitRate();
        int withdrawal = fund.getWithdrawalSteps();
        if (years > GovernmentView.RICH_MIN) {
            tax.setProfitTaxRate(Math.max(0, profit - PROFIT_STEP));
            tax.setSalesTaxRate(Math.max(0, sales - SALES_STEP));
            tax.setPensionReplacement(Math.min(TaxPolicy.MAX_REPLACEMENT, pension + TRANSFER_STEP));
            tax.setEiBenefitRate(Math.min(TaxPolicy.MAX_EI_BENEFIT, ei + TRANSFER_STEP));
            g.setFundWithdrawal(TreasuryFund.MAX_WITHDRAWAL_STEPS * TreasuryFund.WITHDRAWAL_STEP);
        } else if (years < GovernmentView.SOUND_MIN) {
            // A newly opened promise can still be zero, or a manually set tax already above this player's ceiling. Never reverse the move.
            tax.setProfitTaxRate(Math.max(profit, Math.min(POOR_PROFIT_MAX, profit + PROFIT_STEP)));
            tax.setSalesTaxRate(Math.max(sales, Math.min(POOR_SALES_MAX, sales + SALES_STEP)));
            tax.setPensionReplacement(Math.min(pension, Math.max(POOR_PENSION_MIN, pension - TRANSFER_STEP)));
            tax.setEiBenefitRate(Math.min(ei, Math.max(POOR_EI_MIN, ei - TRANSFER_STEP)));
            g.setFundWithdrawal(Math.max(0, withdrawal - 1) * TreasuryFund.WITHDRAWAL_STEP);
        }
        return tax.getProfitTaxRate() != profit || tax.getSalesTaxRate() != sales
                || tax.getPensionReplacement() != pension || tax.getEiBenefitRate() != ei
                || fund.getWithdrawalSteps() != withdrawal;
    }
}

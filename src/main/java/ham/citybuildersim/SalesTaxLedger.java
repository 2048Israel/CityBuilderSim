package ham.citybuildersim;

/**
 * The month's sales tax, as tax payable less input tax credits.
 *
 * WHY THIS REPLACED A ONE-LINE SALES TAX
 *
 * calculateSalesTax() was three lines: food-plant revenue, store revenue, and
 * the retail import tax, each times the one city rate. Two things were wrong
 * with it, and they pulled in opposite directions.
 *
 * It taxed the same food TWICE - once when the plant sold it and again when the
 * store did - so a longer supply chain cost more tax for no more consumption.
 * And it never touched Heavy Industry or Mining at all: their gross revenue was
 * simply absent from the sum, so steel and ore moved through the economy
 * untaxed while a loaf of bread was charged at every step.
 *
 * Jerus's fix, in his words: "just like real life... it's taxed, all of it, just
 * that there is tax credits - if you bought stuff with 3k tax then what you sell
 * has a 3k tax credit, basically the HST receivable and payable thing."
 *
 * So: every sector charges tax on what it sells and claims back the tax embedded
 * in what it bought. The city collects the difference, which is the tax on the
 * VALUE THE SECTOR ADDED. Add the chain up and the total is the tax on final
 * consumption, however many hands the goods passed through.
 *
 * ONE DELIBERATE DEPARTURE FROM REAL HST
 *
 * Under a real VAT the rate follows the PRODUCT, so intermediate rates cancel
 * exactly and only the last sale before a final consumer sets what the city
 * collects. That would make five of the six per-sector dials cosmetic. Jerus
 * chose the other way: the rate follows the PRODUCER, so a sector's own rate
 * applies to the margin it adds and every dial is a real lever. Cutting Mining's
 * rate genuinely makes ore cheaper to produce.
 *
 * The consequence to know: because payable and credit can be struck at different
 * rates, a sector buying at a high rate and selling at a low one can show a
 * NEGATIVE net remittance. That is a refund, it is correct, and the ledger does
 * not floor it - flooring would quietly turn a rate cut into a partial one.
 *
 * EXPORTS ARE ZERO-RATED. Nothing is charged on ore leaving the city, and the
 * credits behind it are still claimable, so an exporter can end the month owed
 * money. That is what zero-rating means and it is why it is a genuine incentive
 * to export rather than a bookkeeping nicety.
 *
 * IMPORTS ARE TAXED AND CREDITABLE, so a sector cannot undercut a local supplier
 * simply by buying from outside the city.
 *
 * THE CITY IS EXEMPT. It charges nothing on what it supplies and claims nothing
 * on what it buys. Real governments do pay VAT; this one does not, because the
 * alternative is the treasury paying itself and both halves appearing in the
 * books - and money-from-nowhere through exactly that kind of round trip is the
 * bug this codebase keeps rediscovering.
 */
public class SalesTaxLedger {

    private final int sectors = PolicySector.values().length;

    private final double[] taxableSales  = new double[sectors];
    private final double[] zeroRated     = new double[sectors];
    private final double[] creditedInput = new double[sectors];

    private final double[] payable = new double[sectors];
    private final double[] credit  = new double[sectors];

    private double totalRemitted;

    /* ==================================================================
       WHAT HAPPENED THIS MONTH
       ================================================================== */

    /** Sales to anyone inside the city. Charged at the seller's own rate. */
    public void recordSales(PolicySector sector, double revenue) {
        if (sector == null || revenue <= 0) return;
        taxableSales[sector.ordinal()] += revenue;
    }

    /**
     * Sales out of the city. Charged nothing, and they do not cost the seller
     * its credits - that is the whole of what zero-rating means.
     *
     * Tracked separately rather than just ignored, because a screen that cannot
     * say "this much of your revenue was zero-rated" makes a refund look like a
     * bug.
     */
    public void recordExport(PolicySector sector, double revenue) {
        if (sector == null || revenue <= 0) return;
        zeroRated[sector.ordinal()] += revenue;
    }

    /**
     * Tax the sector actually PAID on its inputs, recoverable in full.
     *
     * Takes the tax, not the purchase. The credit has to be what the supplier
     * charged - at the SUPPLIER's rate - or the chain stops adding up: a buyer
     * claiming back more than the seller remitted is the city refunding tax it
     * never collected.
     */
    public void recordInputTax(PolicySector sector, double taxPaid) {
        if (sector == null || taxPaid <= 0) return;
        creditedInput[sector.ordinal()] += taxPaid;
    }

    /**
     * Tax on goods bought from outside the city, at the BUYER's rate.
     *
     * An import has no local supplier to have charged anything, so the buyer is
     * charged on the way in and credits the same amount. That nets to zero for a
     * sector that resells locally, which is the point - the tax lands on the
     * final sale either way, and importing carries no advantage over buying from
     * a local supplier.
     */
    public double chargeImport(PolicySector sector, double landedCost, TaxPolicy policy) {
        if (sector == null || landedCost <= 0) return 0;
        double tax = landedCost * policy.effectiveSalesRate(sector);
        taxableSales[sector.ordinal()] += 0;      // an import is not a sale
        creditedInput[sector.ordinal()] += tax;   // ...but the tax on it is creditable
        return tax;
    }

    /* ==================================================================
       WHAT IT COMES TO
       ================================================================== */

    /**
     * Strikes the month's tax. Call once, after every sector has reported.
     *
     * @return what the city collects in total, which may be less than any one
     *         sector's payable if another is in a refund position
     */
    public double settle(TaxPolicy policy) {

        totalRemitted = 0;

        for (PolicySector s : PolicySector.values()) {
            int i = s.ordinal();

            // Zero-rated sales are charged nothing. They are still SALES, so
            // they do not reduce the credit behind them.
            payable[i] = taxableSales[i] * policy.effectiveSalesRate(s);
            credit[i]  = creditedInput[i];

            totalRemitted += payable[i] - credit[i];
        }
        return totalRemitted;
    }

    public double getTotalRemitted()               { return totalRemitted; }
    public double getPayable(PolicySector s)       { return payable[s.ordinal()]; }
    public double getCredit(PolicySector s)        { return credit[s.ordinal()]; }
    public double getNet(PolicySector s)           { return payable[s.ordinal()] - credit[s.ordinal()]; }
    public double getTaxableSales(PolicySector s)  { return taxableSales[s.ordinal()]; }
    public double getZeroRated(PolicySector s)     { return zeroRated[s.ordinal()]; }

    /** True when the city owes this sector rather than the other way round. */
    public boolean isInRefund(PolicySector s) {
        return getNet(s) < 0;
    }

    /**
     * Clears the month.
     *
     * Everything here is a FLOW - it describes a period, not a balance - so it
     * has to be zeroed at the start of each month and carried in the save rather
     * than recomputed on load. Nothing about the state a month ended in can tell
     * you what was bought and sold during it.
     */
    public void startMonth() {
        java.util.Arrays.fill(taxableSales, 0);
        java.util.Arrays.fill(zeroRated, 0);
        java.util.Arrays.fill(creditedInput, 0);
        java.util.Arrays.fill(payable, 0);
        java.util.Arrays.fill(credit, 0);
        totalRemitted = 0;
    }

    /* ------------------------- save and restore ------------------------- */

    public double[] getLedgerState() {
        double[] state = new double[1 + sectors * 5];
        int i = 0;
        state[i++] = totalRemitted;
        for (int s = 0; s < sectors; s++) state[i++] = taxableSales[s];
        for (int s = 0; s < sectors; s++) state[i++] = zeroRated[s];
        for (int s = 0; s < sectors; s++) state[i++] = creditedInput[s];
        for (int s = 0; s < sectors; s++) state[i++] = payable[s];
        for (int s = 0; s < sectors; s++) state[i++] = credit[s];
        return state;
    }

    /** @return false if the array is not this build's shape; nothing is changed */
    public boolean restoreLedgerState(double[] state) {

        if (state == null || state.length != 1 + sectors * 5) return false;

        int i = 0;
        totalRemitted = state[i++];
        for (int s = 0; s < sectors; s++) taxableSales[s]  = state[i++];
        for (int s = 0; s < sectors; s++) zeroRated[s]     = state[i++];
        for (int s = 0; s < sectors; s++) creditedInput[s] = state[i++];
        for (int s = 0; s < sectors; s++) payable[s]       = state[i++];
        for (int s = 0; s < sectors; s++) credit[s]        = state[i++];
        return true;
    }

    public void reset() {
        startMonth();
    }
}

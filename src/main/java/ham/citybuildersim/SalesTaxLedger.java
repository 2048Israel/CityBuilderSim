package ham.citybuildersim;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The month's sales tax, as tax payable less input tax credits.
 *
 * WHY THIS REPLACED A ONE-LINE SALES TAX
 *
 * calculateSalesTax() was three lines: food-plant revenue, store revenue, and
 * the retail import tax, each times the one city rate. It taxed the same food
 * TWICE - once when the plant sold it and again when the store did - and it
 * never touched Heavy Industry or Mining at all, so steel and ore moved
 * through the economy untaxed while a loaf of bread was charged at every
 * step.
 *
 * Jerus's fix, in his words: "just like real life... it's taxed, all of it,
 * just that there is tax credits - if you bought stuff with 3k tax then what
 * you sell has a 3k tax credit, basically the HST receivable and payable
 * thing." So every sector charges tax on what it sells and claims back the
 * tax embedded in what it bought. The city collects the difference, which is
 * the tax on the VALUE THE SECTOR ADDED.
 *
 * ONE DELIBERATE DEPARTURE FROM REAL HST: the rate follows the PRODUCER, not
 * the product, so every per-sector dial is a real lever. A sector buying at a
 * high rate and selling at a low one can show a NEGATIVE net remittance;
 * that is a refund, it is correct, and the ledger does not floor it.
 *
 * EXPORTS ARE ZERO-RATED, and the credits behind them stay claimable. IMPORTS
 * ARE TAXED AND CREDITABLE, so a sector cannot undercut a local supplier by
 * buying from outside. THE CITY IS EXEMPT, and so is residential rent (see
 * Good.taxExempt()).
 *
 * SINCE THE SECTOR TEMPLATE (2026-09-11) the ledger is STRUCK FROM THE
 * TRADES rather than written by hand per sector - see
 * EconomyManager.settleSalesTax(): a sale to a local buyer at the seller's
 * rate, an export zero-rated, a purchase credited at the supplier's rate, an
 * import charged at the buyer's. Keyed by the sector's name, so a seventh
 * sector is a seventh row and nothing here changes.
 */
public class SalesTaxLedger {

    private final Map<String, double[]> rows = new LinkedHashMap<>();

    /* slots in a row */
    private static final int TAXABLE_SALES = 0, IMPORT_TAX = 1, ZERO_RATED = 2,
            CREDITED_INPUT = 3, PAYABLE = 4, CREDIT = 5, SLOTS = 6;

    private double totalRemitted;

    private double[] row(String sector) {
        return rows.computeIfAbsent(sector, k -> new double[SLOTS]);
    }

    /* ==================================================================
       WHAT HAPPENED THIS MONTH
       ================================================================== */

    /** Sales to anyone inside the city. Charged at the seller's own rate. */
    public void recordSales(String sector, double revenue) {
        if (sector == null || revenue <= 0) return;
        row(sector)[TAXABLE_SALES] += revenue;
    }

    /**
     * Sales out of the city. Charged nothing, and they do not cost the seller
     * its credits. Tracked so a screen can say "this much of your revenue was
     * zero-rated" and a refund does not look like a bug.
     */
    public void recordExport(String sector, double revenue) {
        if (sector == null || revenue <= 0) return;
        row(sector)[ZERO_RATED] += revenue;
    }

    /**
     * Tax the sector actually PAID on its inputs, recoverable in full. Takes
     * the tax, not the purchase: the credit has to be what the supplier
     * charged - at the SUPPLIER's rate - or the city refunds tax it never
     * collected.
     */
    public void recordInputTax(String sector, double taxPaid) {
        if (sector == null || taxPaid <= 0) return;
        row(sector)[CREDITED_INPUT] += taxPaid;
    }

    /**
     * Tax on goods bought from outside the city, at the BUYER's rate: charged
     * on the way in and credited, which nets to zero for a sector that
     * resells locally - the tax lands on the final sale either way.
     */
    public double chargeImport(String sector, double landedCost, TaxPolicy policy) {
        if (sector == null || landedCost <= 0) return 0;
        double tax = landedCost * policy.effectiveSalesRate(sector);
        row(sector)[IMPORT_TAX] += tax;
        row(sector)[CREDITED_INPUT] += tax;
        return tax;
    }

    /** The sector in the biggest refund position this month, or null if none is. */
    public String deepestRefund() {
        String worst = null;
        double deepest = 0;
        for (String sector : rows.keySet()) {
            double net = getNet(sector);
            if (net < deepest) {
                deepest = net;
                worst = sector;
            }
        }
        return worst;
    }

    /* ==================================================================
       WHAT IT COMES TO
       ================================================================== */

    /** Strikes the month's tax. Call once, after every sector has reported. */
    public double settle(TaxPolicy policy) {
        totalRemitted = 0;
        for (Map.Entry<String, double[]> e : rows.entrySet()) {
            double[] r = e.getValue();
            // Zero-rated sales are charged nothing; they are still sales, so
            // they do not reduce the credit behind them.
            r[PAYABLE] = r[TAXABLE_SALES] * policy.effectiveSalesRate(e.getKey()) + r[IMPORT_TAX];
            r[CREDIT] = r[CREDITED_INPUT];
            totalRemitted += r[PAYABLE] - r[CREDIT];
        }
        return totalRemitted;
    }

    public double getTotalRemitted()             { return totalRemitted; }
    public double getPayable(String s)           { return has(s) ? rows.get(s)[PAYABLE] : 0; }
    public double getCredit(String s)            { return has(s) ? rows.get(s)[CREDIT] : 0; }
    public double getNet(String s)               { return getPayable(s) - getCredit(s); }
    public double getTaxableSales(String s)      { return has(s) ? rows.get(s)[TAXABLE_SALES] : 0; }
    public double getZeroRated(String s)         { return has(s) ? rows.get(s)[ZERO_RATED] : 0; }
    public double getImportTax(String s)         { return has(s) ? rows.get(s)[IMPORT_TAX] : 0; }

    private boolean has(String s) { return s != null && rows.containsKey(s); }

    /** True when the city owes this sector rather than the other way round. */
    public boolean isInRefund(String s) { return getNet(s) < 0; }

    /**
     * Clears the month. Everything here is a FLOW - it describes a period,
     * not a balance - so it is zeroed at the start of each month and carried
     * in the save rather than recomputed on load.
     */
    public void startMonth() {
        rows.clear();
        totalRemitted = 0;
    }

    /* ------------------------- save and restore ------------------------- */

    /** One sector's row, as the save carries it. */
    public static final class Row {
        public String sector;
        public double taxableSales, importTax, zeroRated, creditedInput, payable, credit;
    }

    public static final class State {
        public double totalRemitted;
        public List<Row> rows = new ArrayList<>();
    }

    public State toState() {
        State s = new State();
        s.totalRemitted = totalRemitted;
        for (Map.Entry<String, double[]> e : rows.entrySet()) {
            double[] r = e.getValue();
            Row row = new Row();
            row.sector = e.getKey();
            row.taxableSales = r[TAXABLE_SALES];
            row.importTax = r[IMPORT_TAX];
            row.zeroRated = r[ZERO_RATED];
            row.creditedInput = r[CREDITED_INPUT];
            row.payable = r[PAYABLE];
            row.credit = r[CREDIT];
            s.rows.add(row);
        }
        return s;
    }

    /** @return false when there is nothing to restore; nothing is changed. */
    public boolean restore(State s) {
        if (s == null) return false;
        rows.clear();
        totalRemitted = s.totalRemitted;
        if (s.rows != null) for (Row row : s.rows) {
            if (row == null || row.sector == null) continue;
            double[] r = row(row.sector);
            r[TAXABLE_SALES] = row.taxableSales;
            r[IMPORT_TAX] = row.importTax;
            r[ZERO_RATED] = row.zeroRated;
            r[CREDITED_INPUT] = row.creditedInput;
            r[PAYABLE] = row.payable;
            r[CREDIT] = row.credit;
        }
        return true;
    }

    public void reset() { startMonth(); }

    /** The month's VAT working, in the new unit. */
    public void redenominate(double scale) {
        for (double[] r : rows.values()) {
            for (int i = 0; i < SLOTS; i++) r[i] *= scale;
        }
        totalRemitted *= scale;
    }
}

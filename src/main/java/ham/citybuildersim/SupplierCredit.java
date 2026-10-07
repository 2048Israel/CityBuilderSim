package ham.citybuildersim;

import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * What a buyer owes its suppliers for stock they let it have on credit (0.7.44): the grocers' trade credit, struck and repaid a month at a time.
 *
 * WHY. Since 0.7.12 a firm restocks only as far as its cash and the credit it
 * can get reach (Sector, BUY ONLY WHAT IT CAN PAY FOR), and since 0.7.43 the
 * shops sell the baskets on their shelf and no more (Retail.sellOwnPriced()).
 * Together they made a trap the ensemble found in 11.9% of the autopilot's
 * months after month 240 (runs/diag-0743.md, section 5): a grocer whose till
 * ran dry bought nothing, so its shelf emptied, so it sold nothing, so its till
 * stayed dry - delivering under 5% of what was wanted for two or three months
 * until a working-capital loan refilled the shelf, and again ten months later.
 * In 94% of those months the city had the shops; they did not have the cash.
 * The stock bought this month is sold next month (Markets.clearMonth(): the
 * sale, then the restock), so a till paid out of last month's sale cannot buy
 * the stock for a sale that never happened. A real grocer does not pay for its
 * stock on delivery: its suppliers wait for the money until the stock has sold
 * (trade credit - in the US and Canada about a month's terms, "net 30").
 *
 * THE RULE, EACH MONTH:
 *
 *   1. AT THE CLEARING, what the suppliers will wait for - the LIMIT, the
 *      buyer's own (Retail.supplierCreditLimit(): the stock for a month of
 *      the sales it expects, at what that stock costs to bring in) - is
 *      opened beside the till the buyer will have at the settle (open(), from
 *      EconomyManager.purchaseBudget(), which adds the limit to what its till
 *      and its lender cover). So the restock is still bounded by its cash and
 *      its credit: the credit now includes its suppliers'.
 *   2. AS IT IS FILLED, every order for stock is noted against the supplier
 *      that filled it - the world for an import - and the goods the credit
 *      covers (the shelf) apart from the rest (the fleet) (note(), from
 *      Sector.bookPurchase()).
 *   3. AT THE CLOSE, what it bought on credit is the part of the stock bill
 *      its till cannot cover, never more than the limit nor than the covered
 *      goods' bill: min(limit, covered, max(0, stock bill - max(0, till)))
 *      (close()). A grocer whose till covers its stock takes none. It is
 *      shared over the suppliers of the covered goods in proportion to what
 *      each filled.
 *   4. AT THE NEXT STRIKE (EconomyManager.settleSupplierCredit()), the
 *      statement charges the whole bill as it always has; the till pays all of
 *      it but the credit, and repays what the strike before left owed - the
 *      stock that has now been sold, paid out of the takings of its sale. Each
 *      local supplier is paid less by its share now and is paid what it was
 *      owed; the world's share crosses the money audit as a financial flow in
 *      and, a month later, out (MoneyAudit, "SupplierCredit").
 *
 * THE CREDIT IS THE STOCK'S. The limit is added to what the till and the
 * lender cover after that is floored at nothing, not netted against the
 * bills they could not: a grocer whose wages and interest outrun its till
 * still gets its month's stock on its suppliers' credit, and still defaults
 * on the wages and the interest, as the 0.7.12 rule says it must. The stock
 * pays for itself - its sale brings in its cost at least RETAIL_MARKUP over,
 * and repays the suppliers at the next strike - and suppliers in fact lend
 * on those terms because the goods are theirs until paid for (retention of
 * title; a purchase-money security interest in inventory ranks first on it
 * and its proceeds, US UCC 9-324(b)). Measured the other way first: with the
 * limit netted against the bills (and then struck at the shelf price, the
 * takings, rather than at cost), a grocer that could not pay its staff out
 * of its margin - over-built for a city whose roads and power hand over 40%
 * of its coverage - still emptied its shelf, 2.4% of the months of the worst
 * seed and a hunger rate of .106, against none and .052 this way (seed 1 of
 * the ensemble's autopilot, month 240 on; 0.7.43: 35% and .409).
 *
 * ON THE BOOKS: the buyer's balance sheet carries what it owes (a current
 * liability, BalanceSheet.getTradePayables()), its suppliers' what they are
 * owed (a current asset), and the cash-flow statement the month's net
 * (SectorBooks.SectorMonth's tradeCredit). The lender reads each of them as
 * if the credit were not there: the buyer's assets net of what it owes its
 * suppliers, theirs with what they are owed (EconomyManager
 * .refreshCreditAssets()). The cash-flow test reads what the month asked the
 * buyer to pay: the stock it paid for and the credit it repaid, not the stock
 * its suppliers are waiting for (EconomyManager.monthObligations()).
 *
 * WHAT IT IS NOT. Not a loan from the bank: no fee, no interest - the goods'
 * price is the supplier's whether paid now or at the next strike, as on net-30
 * terms - and nothing written in BusinessDebtManager. Not a way past a bill it
 * cannot avoid: the till still pays the wages, the interest, the tax and the
 * principal that falls due first, and a grocer that cannot pay those still
 * defaults on them; what it owes its suppliers is one more bill the next
 * strike takes. And only the grocers have one (Retail.supplierCredit()):
 * the kitchens and the boutiques were measured and do not starve this way -
 * on the autopilot, sixteen seeds from month 240, neither served under 5% of
 * what was wanted in any month, and neither went short of stock it could
 * not pay for in more than 0.6% of months.
 *
 * @author Jerus
 */
public final class SupplierCredit {

    /** The goods the credit covers: an order for anything else the buyer keeps (its fleet) is paid from its till as before. */
    private final Set<Good> covers;

    /** What it owes each supplier - a sector's key, or Trade.WORLD - for the stock it bought on credit at the clearing the last strike banked; the next strike repays it. */
    private final Map<String, Double> owed = new LinkedHashMap<>();
    /** ...what the last strike repaid each, the strike before's credit. */
    private final Map<String, Double> repaid = new LinkedHashMap<>();
    /** ...and what it bought on credit at this month's clearing, by supplier: owed from the next strike. */
    private final Map<String, Double> bought = new LinkedHashMap<>();

    /* The clearing's, opened and closed inside it - never saved. */
    private boolean open;
    private double limit, till, stockBill;
    private final Map<String, Double> coveredBill = new LinkedHashMap<>();
    /** The last clearing's reading, for the screens: what the suppliers would have waited for. */
    private double rLimit;

    public SupplierCredit(Set<Good> covers) {
        this.covers = covers == null || covers.isEmpty() ? EnumSet.noneOf(Good.class) : EnumSet.copyOf(covers);
    }

    /** Whether the credit covers this good. */
    public boolean covers(Good g) { return g != null && covers.contains(g); }

    /* ---------------------------- the clearing ---------------------------- */

    /**
     * Opens the clearing's credit: what the suppliers will wait for, and the
     * till the buyer will have at the settle before it borrows. Returns the
     * limit, which the purchase budget adds to what it can pay for; nothing
     * when it is not a finite positive sum.
     */
    public double open(double limit, double tillAtSettle) {
        this.limit = limit > 0 && Double.isFinite(limit) ? limit : 0;
        this.till = Double.isFinite(tillAtSettle) ? tillAtSettle : 0;
        this.stockBill = 0;
        coveredBill.clear();
        rLimit = this.limit;
        open = true;
        return this.limit;
    }

    /** An order for stock, filled: by whom, of what, for how much. Ignored outside a clearing. */
    public void note(String supplier, Good g, double value) {
        if (!open || supplier == null || !(value > 0) || !Double.isFinite(value)) return;
        stockBill += value;
        if (covers(g)) coveredBill.merge(supplier, value, Double::sum);
    }

    /**
     * Closes the clearing: what it bought on credit, the part of the stock
     * bill its till cannot cover, at most the limit and the covered goods'
     * bill, shared over their suppliers by what each filled.
     */
    public void close() {
        if (!open) return;
        open = false;
        double covered = 0;
        for (double v : coveredBill.values()) covered += v;
        double credit = Math.min(limit, Math.min(covered, Math.max(0, stockBill - Math.max(0, till))));
        if (!(credit > 0) || !(covered > 0)) return;
        for (Map.Entry<String, Double> e : coveredBill.entrySet()) {
            double share = credit * (e.getValue() / covered);
            if (share > 0) bought.merge(e.getKey(), share, Double::sum);
        }
    }

    /* ------------------------------ the strike ------------------------------ */

    /**
     * The strike: what was owed is repaid, and what the last clearing bought
     * on credit is owed. No money moves here - EconomyManager
     * .settleSupplierCredit() moves it, both sides, off what this leaves.
     */
    public void strike() {
        repaid.clear();
        repaid.putAll(owed);
        owed.clear();
        owed.putAll(bought);
        bought.clear();
    }

    /* ------------------------------ readings ------------------------------ */

    /** What it owes all its suppliers now: the last strike's credit, which the next strike repays. */
    public double owedTotal() { return sum(owed); }
    /** ...one of them. */
    public double owedTo(String supplier) { return owed.getOrDefault(supplier, 0.0); }
    /** What the last strike repaid, all its suppliers. */
    public double repaidTotal() { return sum(repaid); }
    /** ...one of them. */
    public double repaidTo(String supplier) { return repaid.getOrDefault(supplier, 0.0); }
    /** What the last strike took on credit - what it now owes, since the strike moved it there. */
    public double takenTotal() { return owedTotal(); }
    /** What this month's clearing bought on credit, owed from the next strike. */
    public double boughtTotal() { return sum(bought); }
    /** ...from one supplier. */
    public double boughtFrom(String supplier) { return bought.getOrDefault(supplier, 0.0); }
    /** The suppliers it owes, or was repaid at the last strike, or bought from on credit since. */
    public Set<String> suppliers() {
        java.util.LinkedHashSet<String> all = new java.util.LinkedHashSet<>(owed.keySet());
        all.addAll(repaid.keySet());
        all.addAll(bought.keySet());
        return Collections.unmodifiableSet(all);
    }
    /** What the last clearing's suppliers would have waited for. */
    public double getLimit() { return rLimit; }
    /** ...and what they will wait for in the clearing open now; nothing outside one. */
    public double openLimit() { return open ? limit : 0; }

    private static double sum(Map<String, Double> m) {
        double t = 0;
        for (double v : m.values()) t += v;
        return t;
    }

    /* --------------------------- save, reset, reform --------------------------- */

    /** Into the owner's extras, by name: each map under its own prefix, keyed by supplier. */
    public void save(Map<String, Double> extras, String prefix) {
        for (Map.Entry<String, Double> e : owed.entrySet())   extras.put(prefix + "owed." + e.getKey(), e.getValue());
        for (Map.Entry<String, Double> e : repaid.entrySet()) extras.put(prefix + "repaid." + e.getKey(), e.getValue());
        for (Map.Entry<String, Double> e : bought.entrySet()) extras.put(prefix + "bought." + e.getKey(), e.getValue());
        extras.put(prefix + "limit", rLimit);
    }

    /** ...and back. A save from before 0.7.44 has none of these names, and owes nothing, which is what that city owed. */
    public void restore(Map<String, Double> extras, String prefix) {
        clear();
        for (Map.Entry<String, Double> e : extras.entrySet()) {
            String k = e.getKey();
            Double v = e.getValue();
            if (k == null || v == null || !k.startsWith(prefix)) continue;
            String rest = k.substring(prefix.length());
            if (rest.startsWith("owed."))        owed.put(rest.substring(5), v);
            else if (rest.startsWith("repaid.")) repaid.put(rest.substring(7), v);
            else if (rest.startsWith("bought.")) bought.put(rest.substring(7), v);
            else if (rest.equals("limit"))      rLimit = v;
        }
    }

    /** Nothing owed, nothing bought. */
    public void clear() {
        owed.clear();
        repaid.clear();
        bought.clear();
        coveredBill.clear();
        open = false;
        limit = till = stockBill = rLimit = 0;
    }

    /** Every sum in the new unit. */
    public void redenominate(double scale) {
        owed.replaceAll((k, v) -> v * scale);
        repaid.replaceAll((k, v) -> v * scale);
        bought.replaceAll((k, v) -> v * scale);
        rLimit *= scale;
    }
}

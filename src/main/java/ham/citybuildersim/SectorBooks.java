package ham.citybuildersim;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A month of books for every business in the city, and last month's too.
 *
 * WHY THIS EXISTS AT ALL. Six sectors used to keep their own figures in five
 * different handlers, in five different shapes, and none of them could show
 * LAST month at all. So: one shape, read once a month, kept for two months.
 * What the sector screens draw is this. Since the sector template
 * (2026-09-11) every sector strikes its statement in this shape itself - see
 * Sector.Statement - and this class adds the flows the city recorded
 * against its name (credit, subsidy, the owners, the money abroad) and keeps
 * the comparative column.
 *
 * NOTHING HERE FEEDS BACK. This class reads the model and is read by screens.
 * No handler asks it a question, no decision depends on it, and deleting it
 * would change nothing about how the city runs. That is the whole design
 * constraint: a reporting layer that can affect the thing it reports is not a
 * reporting layer, it is a bug waiting for a save/load to expose it.
 *
 * IT IS SAVED, and that is not decoration. A statement whose comparative column
 * is blank until you have played a month is a statement that is blank exactly
 * when a returning player opens it - which is the same trap the schools fell
 * into. Two months of six small records is a few hundred bytes.
 *
 * @author Jerus
 */
public final class SectorBooks {

    /**
     * One sector's month.
     *
     * A record rather than a class because it is a value: it is written once,
     * when the month closes, and never edited. Field order is statement order,
     * top to bottom, so reading the declaration reads the income statement.
     */
    public record SectorMonth(

            String sector,
            int month,

            /* ---------------------- income statement ---------------------- */
            double revenue,
            double inputs,          // ore, scrap, materials - cost of sales
            double payroll,
            double electricity,
            double water,
            double operatingIncome,
            double interest,
            double propertyTax,
            double preTaxIncome,
            double tax,
            /** AFTER tax. The handlers all call their pre-tax figure
             *  "net income"; this one is what the sector actually keeps, and
             *  it is the figure its cash reserve moves by. */
            double netIncome,

            /* ------------------------ balance sheet ------------------------ */
            double cash,
            double inventory,
            double land,
            double buildings,
            double bondsPayable,

            /* -------------------------- cash flow -------------------------- */
            double openingCash,
            double borrowed,
            double repaid,
            double fromTheCity,     // subsidy paid into the sector's own books
            /**
             * What its creditors wrote off this month: the overdraft a
             * restructure forgave. Cash that arrived from nobody inside the
             * city, so it has to be its own line or unexplained() holds it -
             * which it did, for one run, the first time a sector went bankrupt.
             * See BusinessDebtManager.restructure().
             */
            double forgiven,
            /**
             * What the bank paid this sector for the money in its till.
             *
             * A CASH-FLOW LINE AND NOT AN INCOME ONE, which is a compromise and
             * is written down as such. The bank strikes its deposit interest at
             * the bottom of the month, long after every sector's income
             * statement has been struck, and credits it straight to the tills -
             * so it reaches the cash without passing through any statement.
             *
             * It used to reach the cash without passing through anything at
             * all, and `unexplained()` was left holding it. That was invisible
             * for as long as a fixture city never got as far as building a
             * bank; the founding bank of 2026-09-09 made every city have one
             * from month one and `SectorBooksCheck` found it in every sector
             * from month 26. Naming it is the fix that was available; putting
             * it on the income statement where it belongs needs the interest
             * to be struck a month earlier, and that is its own change.
             */
            double depositInterest,
            double spentOnBuildings,// its own premises, less anything sold back
            /**
             * Sales tax remitted. ON THE INCOME STATEMENT since 2026-09-09, as
             * a deduction between operating income and profit before tax - so
             * it is NOT a separate cash-flow line any more. It reaches the cash
             * through netIncome() like every other cost, and subtracting it
             * here as well would take it twice.
             *
             * Kept in this position in the record only so nothing that reads it
             * by name has to move. See preTaxIncome().
             */
            double salesTaxPaid,

            /* ------------------------- and its credit ------------------------- */
            double rate,
            double leverage,
            double writtenOff,
            boolean blocked,

            /* ------------------------ and its money abroad ------------------------ */
            /**
             * What it holds abroad, in the city's money at the rate it was
             * last valued at - a balance-sheet line beside cash, since
             * 2026-09-10. See OutwardInvestment. At the END of the record
             * rather than beside cash so nothing that reads the record by
             * position has to move; Gson matches a save by name, so an older
             * save reads zero here, which is what that city held abroad.
             */
            double foreignAssets,
            /** Sent abroad this month, net: positive went, negative came home. A cash-flow line. */
            double investedAbroad,
            /** What the world paid it this month, rolled into what it holds there. NOT a cash-flow line: it never reaches the till. */
            double foreignInterest,

            /* ------------------------ and its owners ------------------------ */
            /** Sold in shares this month, to the households and the world: a cash-flow line in. Since 2026-09-10 (evening); see Equity. */
            double equityRaised,
            /** Paid to its shareholders this month: a cash-flow line out. */
            double dividendsPaid,
            /** Spent buying back and cancelling its own shares this month: a cash-flow line out. Since the exchange. */
            double sharesBoughtBack,

            /* ------------------------ and what it cost to stand ------------------------ */
            /**
             * Repairs on its own buildings - the order it placed with the
             * builders. ITS OWN LINE since the sector template (2026-09-11):
             * every handler used to fold it into `inputs`, and Retail folded
             * property tax in as well, so no two sectors' operating-income
             * lines meant the same thing. `inputs` is goods bought now, for
             * everyone, and this is the repairs, for everyone.
             */
            double maintenance,

            /* ------------------------ and what was stolen from it ------------------------ */
            /**
             * Taken from its till by thieves this month: a cash-flow line out,
             * since the police (2026-09-11). It reaches the cash through no
             * statement - a theft is not a cost of doing business anybody
             * booked - so without its own line unexplained() would hold it. At
             * the end of the record for the reason foreignAssets is: an older
             * save reads zero here, which is what was stolen from that city.
             */
            double stolen) {

        /** What the sheet says the owners have. */
        public double equity() {
            return totalAssets() - bondsPayable;
        }

        public double totalAssets() {
            return cash + inventory + land + buildings + foreignAssets;
        }

        /** Everything above operating income: goods bought, payroll, utilities, repairs. */
        public double operatingCost() {
            return inputs + payroll + electricity + water + maintenance;
        }

        /**
         * What the cash flow statement adds up to, against what the cash
         * actually is.
         *
         * Zero when the four flows explain the month completely. Anything else
         * is money this class cannot account for, and the screen prints it as
         * its own line rather than hiding it in a total - see the sick rate on
         * the Services screen for the same rule.
         */
        public double unexplained() {
            return cash - (openingCash + netIncome
                    + borrowed - repaid + fromTheCity + forgiven + depositInterest
                    - investedAbroad
                    + equityRaised - dividendsPaid - sharesBoughtBack
                    - spentOnBuildings - stolen);
        }

        public double margin() {
            return revenue > 0 ? netIncome / revenue : 0;
        }

        /** An empty month, for a sector that has not been recorded yet. */
        public static SectorMonth none(String sector) {
            return new SectorMonth(sector, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, false,
                    0, 0, 0,
                    0, 0, 0,
                    0, 0);
        }

        public boolean isEmpty() {
            return month == 0;
        }
    }

    /* ===================================================================
       THE TWO MONTHS
       =================================================================== */

    private final Map<String, SectorMonth> now = new LinkedHashMap<>();
    private final Map<String, SectorMonth> before = new LinkedHashMap<>();

    /** Closing cash from the month just recorded, which is next month's opening. */
    private final Map<String, Double> lastCash = new LinkedHashMap<>();

    public SectorMonth get(Sector sector)      { return get(sector.key()); }
    public SectorMonth previous(Sector sector) { return previous(sector.key()); }

    public SectorMonth get(String key) {
        return now.getOrDefault(key, SectorMonth.none(key));
    }

    public SectorMonth previous(String key) {
        return before.getOrDefault(key, SectorMonth.none(key));
    }

    public boolean hasComparatives() {
        for (SectorMonth m : before.values()) if (!m.isEmpty()) return true;
        return false;
    }

    public boolean isEmpty() {
        return now.isEmpty();
    }

    /* ===================================================================
       THE MONTH

       Called once from Game.recordMonth, beside the history and the inbox, and
       for the same reason all three are there: a month that goes by during a
       fifty-month skip has to be recorded by the city rather than by whichever
       screen the player happens to open afterwards.
       =================================================================== */

    public void takeMonth(Game game) {

        if (game == null) return;

        before.clear();
        before.putAll(now);
        now.clear();

        for (Sector sector : game.getSectors().all()) {
            SectorMonth month = read(game, sector);
            now.put(sector.key(), month);
            lastCash.put(sector.key(), month.cash());
        }
    }

    /**
     * One sector's figures, off the statement it struck this month and the
     * flows the city recorded against its name.
     *
     * THE SIX-WAY SWITCH IS GONE (2026-09-11). It existed so that no screen
     * ever had to know that Retail and Real Estate came out of the same
     * object or that construction kept its wage bill under a different
     * name; the template struck every sector's statement in one shape, so
     * there is nothing left to normalise.
     */
    private SectorMonth read(Game game, Sector sector) {

        EconomyManager economy = game.getEconomyManager();
        BusinessDebtManager credit = economy.getBusinessDebtManager();
        String key = sector.key();
        Sector.Statement st = sector.statement();
        BalanceSheet sheet = sector.getBalanceSheet();

        double opening = lastCash.getOrDefault(key, 0.0);

        return new SectorMonth(key, game.getMonth(),
                st.revenue, st.inputs, st.payroll, st.electricity, st.water,
                st.operatingIncome, st.interest, st.propertyTax, st.preTaxIncome, st.profitTax, st.netIncome,
                sheet.getCash(),
                sheet.getInventory(),
                sheet.getLand(),
                sheet.getBuildings(),
                sheet.getBondsPayable(),
                opening,
                credit.getLentThisMonth(key),
                credit.getRepaidThisMonth(key),
                game.getSubsidyPaid(sector),
                economy.getOverdraftForgivenThisMonth(key),
                economy.getDepositInterestPaid(key),
                game.getInvestedThisMonth(key),
                st.salesTax,
                credit.getRate(key), credit.getLeverage(key),
                credit.getWrittenOffThisMonth(key),
                credit.isBorrowingBlocked(key),
                economy.getForeignAssets(key),
                economy.getOutwardInvestment() == null ? 0
                        : economy.getOutwardInvestment().getMovedThisMonth(key),
                economy.getOutwardInvestment() == null ? 0
                        : economy.getOutwardInvestment().getInterestThisMonth(key),
                economy.getEquityRaised(key),
                economy.getDividendsPaid(key),
                economy.getSharesBoughtBack(key),
                st.maintenance,
                economy.getStolen(key));
    }

    /* ===================================================================
       SAVE AND RESTORE

       Both months and the closing cash, because all three are needed for the
       first month back to look like the month before it rather than like the
       beginning of time.

       Refused whole on anything unexpected, the standing rule in this codebase:
       a half-restored set of books would show a comparative column from one
       city against a current column from another, which is worse than showing
       no comparative at all.
       =================================================================== */

    public java.util.List<SectorMonth> thisMonth()  { return list(now); }
    public java.util.List<SectorMonth> lastMonth()  { return list(before); }

    private static java.util.List<SectorMonth> list(Map<String, SectorMonth> from) {
        return new java.util.ArrayList<>(from.values());
    }

    public void restoreFrom(java.util.List<SectorMonth> saved,
                            java.util.List<SectorMonth> savedBefore) {
        now.clear();
        before.clear();
        lastCash.clear();
        if (saved != null) {
            for (SectorMonth m : saved) {
                if (m == null || m.sector() == null) continue;
                now.put(m.sector(), m);
                lastCash.put(m.sector(), m.cash());
            }
        }
        if (savedBefore != null) {
            for (SectorMonth m : savedBefore) {
                if (m == null || m.sector() == null) continue;
                before.put(m.sector(), m);
            }
        }
    }

    /* ===================================================================
       A REFORM

       The two months are in the money they were struck in, and until the
       register read them that was a screen's problem for one month. The
       dividend is paid on the last closed month's net income, so a record
       left in the old money paid a hundred times the dividend the morning
       after a reform - DenominationCheck read the shops' till at zero. Every
       money field moves; the rate, the leverage and the flag do not.
       =================================================================== */

    public void redenominate(double scale) {
        for (Map.Entry<String, SectorMonth> e : now.entrySet()) e.setValue(scaled(e.getValue(), scale));
        for (Map.Entry<String, SectorMonth> e : before.entrySet()) e.setValue(scaled(e.getValue(), scale));
        for (Map.Entry<String, Double> e : lastCash.entrySet()) e.setValue(e.getValue() * scale);
    }

    private static SectorMonth scaled(SectorMonth m, double s) {
        if (m == null) return null;
        return new SectorMonth(m.sector(), m.month(),
                m.revenue() * s, m.inputs() * s, m.payroll() * s, m.electricity() * s, m.water() * s,
                m.operatingIncome() * s, m.interest() * s, m.propertyTax() * s, m.preTaxIncome() * s,
                m.tax() * s, m.netIncome() * s,
                m.cash() * s, m.inventory() * s, m.land() * s, m.buildings() * s, m.bondsPayable() * s,
                m.openingCash() * s, m.borrowed() * s, m.repaid() * s, m.fromTheCity() * s,
                m.forgiven() * s, m.depositInterest() * s, m.spentOnBuildings() * s, m.salesTaxPaid() * s,
                m.rate(), m.leverage(), m.writtenOff() * s, m.blocked(),
                m.foreignAssets() * s, m.investedAbroad() * s, m.foreignInterest() * s,
                m.equityRaised() * s, m.dividendsPaid() * s, m.sharesBoughtBack() * s,
                m.maintenance() * s, m.stolen() * s);
    }
}

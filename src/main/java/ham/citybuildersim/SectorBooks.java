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
            /** What its loans handed it this month: the principal less the fee the bank kept back (0.7.7) and a mortgage's premium, which went to the treasury (0.7.11). */
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
            double stolen,

            /* ------------------------ and its scrapped plant ------------------------ */
            /**
             * The PART of spentOnBuildings that was scrapped plant's material
             * (0.7.8): what the builders paid for it, positive, and what the
             * seller was paid, negative - Game, THE PLANT'S MATERIAL, TO THE
             * BUILDERS. Already inside spentOnBuildings, so unexplained() does
             * not take it again; named so the screen can say which it was. At
             * the end of the record: an older save reads zero here.
             */
            double salvage,

            /* ------------------------ and stock paid for earlier ------------------------ */
            /**
             * The part of this month's inputs drawn from stock it paid cash
             * for in an earlier month (0.7.8, round 3; Sector.Ledger
             * .paidEarlier): the builders' material from scrapped plant, at
             * cost. A cost in netIncome and no cash this month, so the cash
             * flow adds it back - the working-capital line a real statement
             * carries. At the end of the record: an older save reads zero.
             */
            double paidEarlier,

            /* ------------------------ and its bonds (0.7.12) ------------------------ */
            /**
             * The other sectors' bonds it holds, at face: an asset beside its
             * cash and what it holds abroad (BondMarket, THE PARTICIPANTS).
             * The bonds it issued are in bondsPayable with its loans. At the
             * end of the record: an older save reads zero here, as it held none.
             */
            double bondAssets,
            /** What its bonds handed it this month: the face sold less the issuing costs, which the bank was paid. A cash-flow line in, beside what its loans handed it. */
            double bondsIssued,
            /** The face of its own bonds it repaid at maturity this month: a cash-flow line out, beside its loans' principal. */
            double bondsRepaid,
            /** What it spent this month on other sectors' bonds, less what it sold and what their principal paid it back: a cash-flow line out. */
            double bondsBought,
            /** The coupons it was paid on the bonds it holds: a cash-flow line in, reaching its till at the market's step without passing through its statement - the deposit interest's shape. */
            double bondCoupons,

            /* ------------------------ and its trade credit (0.7.44) ------------------------ */
            /**
             * What the buyers it supplied owe it for stock it let them have on
             * credit - a current asset, which the next strike collects - and
             * what it owes its suppliers the same way, a current liability
             * (SupplierCredit). At the end of the record: an older save reads
             * zero here, as nobody was owed.
             */
            double tradeReceivables,
            double tradePayables,
            /**
             * What trade credit moved its till by at the strike, net, in: the
             * credit it took less what it repaid, as a buyer; what it was
             * repaid less what it let its buyers have, as a supplier
             * (Sector.getTradeCreditCash()). A cash-flow line: the statement
             * charged the whole bill and booked the whole sale, and the cash
             * did not move by all of either.
             */
            double tradeCredit,

            /* ------------------------ and the city's arrears (0.7.55) ------------------------ */
            /**
             * What the treasury paid it this month of what it owed it - a
             * subsidy, a repair bill, an escalation or overtime it refused
             * while the central bank's ceiling bound (Game.payDownArrears()).
             * A cash-flow line in, the subsidy's shape: the refused part never
             * reached its statement (the builders recognise what was paid), so
             * the payment arrives as cash and nothing else. Until 0.7.55 it
             * reached the till and no line, and unexplained() held it. At the
             * end of the record: an older save reads zero here.
             */
            double arrearsPaid,

            /* ------------------------ and its share capital (0.7.75, R3) ------------------------ */
            /**
             * Its share capital as the month closed (Equity.getPaidIn()): its
             * founders' book, what it raised at home and abroad, less what its
             * buybacks paid - the sheet's share capital, the rest of its
             * equity retained and revalued. At the end of the record: a save
             * from before 0.7.75 reads nothing here, and its load derives it
             * (derivePaidIn()).
             */
            double paidIn,
            /** The founders' shares issued against its book this month: share capital out of what it had kept, no cash moving. */
            double founded,
            /** True when paidIn was derived at the load of a save from before it was kept: what it raised since founding, its founders' book and earlier buybacks not known. */
            boolean paidInDerived,

            /* -------------- and what its borrowing cost it up front (0.7.75, F2 and R5) -------------- */
            /** The fees the bank kept out of what it lent it this month (0.7.7): what it owes is the whole principal and its cash the rest, so its equity falls by them - on no statement until 0.7.75's outside lines. */
            double loanFees,
            /** ...and its mortgages' insurance premiums, paid to the treasury out of the principal (0.7.11). */
            double premiums,
            /** ...and its bonds' issuing costs (R5): the face it sold less what the bonds handed it, the bank's underwriting. */
            double bondCosts,
            /** The face its bondholders wrote off in this month's defaults (BusinessDebtManager.getBondWrittenOffThisMonth()): what it owes falls and no cash moves, so its equity rises. */
            double bondsWrittenOff,

            /* ------------------------ and what the sheet priced (0.7.75, R6) ------------------------ */
            /** Its land, in square feet, and the price a square foot the sheet valued it at: the sheet's land is their product. */
            double landSqFt,
            double landPrice,
            /** The construction materials in its buildings, finished and on site, in units, and the price a unit the sheet valued them at: the materials part of its buildings. */
            double buildingMaterials,
            double materialsPrice,
            /**
             * Last month's stock at this month's prices, less at last month's:
             * what the prices did to the stock it began the month with. Counted
             * when last month's stock was in memory (stockCounted) - not the
             * month after a load, nor in a save from before 0.7.75, which reads
             * false there.
             */
            double stockRevalued,
            boolean stockCounted,

            /* -------------- and what its borrowing cost it, expensed (0.7.102, A16) -------------- */
            /** The month's sixtieth of what its borrowing cost it up front (Sector.Statement.borrowingCosts): a cost before the profit tax, inside netIncome, paid when it borrowed - so the cash flow adds it back. */
            double borrowingCosts,
            /** ...what it has paid and is still to expense, as the month closed (Sector.getBorrowingCostsToExpense()): an asset on its sheet. */
            double borrowingCostsToExpense,
            /**
             * True for a month struck since 0.7.102, whose fees, premiums and
             * bonds' issuing costs went to borrowingCostsToExpense; false in an
             * older save's months, where they moved its equity the month they
             * were paid and are its outside lines (SectorStatements.outside()).
             */
            boolean borrowingCostsDeferred) {

        /** What the sheet says the owners have. */
        public double equity() {
            return totalAssets() - totalLiabilities();
        }

        /** What it owes: its loans and bonds, and since 0.7.44 its suppliers. */
        public double totalLiabilities() {
            return bondsPayable + tradePayables;
        }

        public double totalAssets() {
            return cash + inventory + land + buildings + foreignAssets + bondAssets + tradeReceivables
                    // ...and what its borrowing cost it up front and it is still to expense (0.7.102).
                    + borrowingCostsToExpense;
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
            return cash - (openingCash + netIncome + paidEarlier
                    + borrowed - repaid + fromTheCity + forgiven + depositInterest
                    - investedAbroad
                    + equityRaised - dividendsPaid - sharesBoughtBack
                    - spentOnBuildings - stolen
                    // ...and its bonds (0.7.12): what they handed it, what it
                    // repaid, what it spent on others', and their coupons.
                    + bondsIssued - bondsRepaid - bondsBought + bondCoupons
                    // ...and its trade credit, net (0.7.44).
                    + tradeCredit
                    // ...and what the city owed it and paid (0.7.55).
                    + arrearsPaid
                    // ...and its borrowing's costs, expensed now and paid when it borrowed (0.7.102).
                    + borrowingCosts);
        }

        /** The size of the figures unexplained() is made of, every one of them as a magnitude: what MoneyAudit.tolerance() reads it against (0.7.54). */
        public double unexplainedScale() {
            double[] terms = { cash, openingCash, netIncome, paidEarlier, borrowed, repaid, fromTheCity, forgiven,
                    depositInterest, investedAbroad, equityRaised, dividendsPaid, sharesBoughtBack, spentOnBuildings,
                    stolen, bondsIssued, bondsRepaid, bondsBought, bondCoupons, tradeCredit, arrearsPaid, borrowingCosts };
            double size = 0;
            for (double t : terms) size += Math.abs(t);
            return size;
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
                    0, 0, 0, 0,
                    0, 0, 0, 0, 0,
                    0, 0, 0,
                    0,
                    0, 0, false,
                    0, 0, 0, 0,
                    0, 0, 0, 0, 0, false,
                    0, 0, true);
        }

        /** Its equity less its share capital: what it kept and what the model revalued (R3). */
        public double retained() {
            return equity() - paidIn;
        }

        /** The same month with its share capital put: a save from before 0.7.75, derived at the load (derivePaidIn()). */
        SectorMonth withPaidIn(double capital, boolean derived) {
            return new SectorMonth(sector, month, revenue, inputs, payroll, electricity, water, operatingIncome,
                    interest, propertyTax, preTaxIncome, tax, netIncome, cash, inventory, land, buildings, bondsPayable,
                    openingCash, borrowed, repaid, fromTheCity, forgiven, depositInterest, spentOnBuildings, salesTaxPaid,
                    rate, leverage, writtenOff, blocked, foreignAssets, investedAbroad, foreignInterest, equityRaised,
                    dividendsPaid, sharesBoughtBack, maintenance, stolen, salvage, paidEarlier, bondAssets, bondsIssued,
                    bondsRepaid, bondsBought, bondCoupons, tradeReceivables, tradePayables, tradeCredit, arrearsPaid,
                    capital, founded, derived, loanFees, premiums, bondCosts, bondsWrittenOff,
                    landSqFt, landPrice, buildingMaterials, materialsPrice, stockRevalued, stockCounted,
                    borrowingCosts, borrowingCostsToExpense, borrowingCostsDeferred);
        }

        public boolean isEmpty() {
            return month == 0;
        }
    }

    /**
     * R1 AND R2 (0.7.74, the sector statements): one business's interest by
     * instrument as its statement was struck, and what it owes by kind with
     * what of it falls due within a year and within five, as its sheet was
     * read - each in KINDS' order. The interest is EconomyManager's split of
     * the bill it handed the sector at the top of the month, so it adds to
     * `interest`; the debt its split of the principal the sheet was last
     * pushed with, so it adds to `bondsPayable`. Either is null when it was
     * not read. Kept for this month and last in memory and NOT SAVED: after
     * a load both months read as not counted until a month has run - the
     * rule the flows' units keep (B14) - which is what the spec asks of R1,
     * R2 and R7 alike.
     *
     * ...AND SINCE 0.7.75 (R2's rate and "runs to", R7): the rate each kind
     * pays a year, weighted by what is owed on it, and the month the last of
     * it falls due (BusinessDebtManager.debtByKind()'s RATE and RUNS_TO); and
     * what had moved its principal so far, {borrowed, repaid, written off}
     * by kind, as the sheet was read (`moved`) and as the month closed
     * (`movedAtClose`) - running totals in memory, which the debt schedule
     * differences a month apart (SectorStatements.schedule()): from sheet to
     * sheet, and, for the harness, from close to close - one and the same
     * since 0.7.102, when the sheet is pushed at the month's close (A16,
     * Game.recordMonth()).
     */
    public record Debt(double[] interest, double[] owed, double[] withinYear, double[] withinFive, double[] rate,
                       double[] runsTo, double[][] moved, double[][] movedAtClose) {

        /** The four kinds, as the statements name them: BusinessDebtManager.DEBT_KINDS. */
        public static final String[] KINDS = BusinessDebtManager.DEBT_KINDS;

        public double interestTotal()   { return sum(interest); }
        public double owedTotal()       { return sum(owed); }
        public double withinYearTotal() { return sum(withinYear); }
        public double withinFiveTotal() { return sum(withinFive); }

        private static double sum(double[] a) {
            if (a == null) return Double.NaN;
            double s = 0;
            for (double v : a) s += v;
            return s;
        }

        Debt scaled(double s) {
            return new Debt(times(interest, s), times(owed, s), times(withinYear, s), times(withinFive, s), rate, runsTo,
                    times(moved, s), times(movedAtClose, s));
        }

        private static double[][] times(double[][] a, double s) {
            if (a == null) return null;
            double[][] out = new double[a.length][];
            for (int i = 0; i < a.length; i++) out[i] = times(a[i], s);
            return out;
        }

        private static double[] times(double[] a, double s) {
            if (a == null) return null;
            double[] out = a.clone();
            for (int i = 0; i < out.length; i++) out[i] *= s;
            return out;
        }
    }

    /**
     * One company's share as its month closed (0.7.74, the investor report):
     * its shares, those outside the bank's desk, the last trade or fair
     * value with whether it has traded, the register's fair value, the
     * month's dividend and a year's of it a share - Equity's and Exchange's
     * reads. In memory for this month and last, NOT SAVED, as Debt.
     */
    public record Shares(double shares, double outstanding, double price, double fair, boolean traded,
                         double dividend, double dividendYear) {

        Shares scaled(double s) {
            return new Shares(shares, outstanding, price * s, fair * s, traded, dividend * s, dividendYear * s);
        }
    }

    /* ===================================================================
       THE TWO MONTHS
       =================================================================== */

    private final Map<String, SectorMonth> now = new LinkedHashMap<>();
    private final Map<String, SectorMonth> before = new LinkedHashMap<>();

    /** Closing cash from the month just recorded, which is next month's opening. */
    private final Map<String, Double> lastCash = new LinkedHashMap<>();

    /** R1 and R2 for this month and last, and each company's share (0.7.74): in memory only - see Debt and Shares. */
    private final Map<String, Debt> debtNow = new LinkedHashMap<>(), debtBefore = new LinkedHashMap<>();
    private final Map<String, Shares> sharesNow = new LinkedHashMap<>(), sharesBefore = new LinkedHashMap<>();

    /** Each business's stock by good as the month closed, {units, price} (0.7.75, R6): next month's revaluation is last month's units at its prices. In memory only. */
    private final Map<String, Map<Good, double[]>> stockAtClose = new LinkedHashMap<>();

    /** This month's R1 and R2 for one business, or null while not counted (a load, a founding). */
    public Debt debt(String key)         { return debtNow.get(key); }
    /** ...and last month's. */
    public Debt debtBefore(String key)   { return debtBefore.get(key); }
    /** One company's share as this month closed, or null while not counted. */
    public Shares shares(String key)       { return sharesNow.get(key); }
    /** ...and as last month closed. */
    public Shares sharesBefore(String key) { return sharesBefore.get(key); }

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
        debtBefore.clear();
        debtBefore.putAll(debtNow);
        debtNow.clear();
        sharesBefore.clear();
        sharesBefore.putAll(sharesNow);
        sharesNow.clear();

        for (Sector sector : game.getSectors().all()) {
            SectorMonth month = read(game, sector);
            now.put(sector.key(), month);
            lastCash.put(sector.key(), month.cash());
            Debt debt = readDebt(game, sector.key());
            if (debt != null) debtNow.put(sector.key(), debt);
            Shares share = readShares(game, sector.key());
            if (share != null) sharesNow.put(sector.key(), share);
        }
    }

    /** R1 and R2 as the month's books read them (0.7.74): EconomyManager's splits, copied - null when it kept neither. */
    private static Debt readDebt(Game game, String key) {
        EconomyManager economy = game.getEconomyManager();
        double[] interest = economy.getInterestByKind(key);
        double[][] owed = economy.getDebtByKind(key);
        if (interest == null && owed == null) return null;
        double[][] moved = economy.getDebtMovedAtSheet(key);
        return new Debt(interest == null ? null : interest.clone(),
                owed == null ? null : owed[BusinessDebtManager.OWED].clone(),
                owed == null ? null : owed[BusinessDebtManager.WITHIN_YEAR].clone(),
                owed == null ? null : owed[BusinessDebtManager.WITHIN_FIVE].clone(),
                owed == null ? null : owed[BusinessDebtManager.RATE].clone(),
                owed == null ? null : owed[BusinessDebtManager.RUNS_TO].clone(),
                owed == null || moved == null ? null : copy(moved),
                owed == null || moved == null ? null : economy.getBusinessDebtManager().debtMovedByKind(key));
    }

    private static double[][] copy(double[][] a) {
        double[][] out = new double[a.length][];
        for (int i = 0; i < a.length; i++) out[i] = a[i].clone();
        return out;
    }

    /** A company's share as the month closes (0.7.74): null for one that is not listed or has no shares. */
    private static Shares readShares(Game game, String key) {
        Equity register = game.getEquity();
        Exchange market = game.getExchange();
        int c = Equity.indexOf(key);
        if (register == null || market == null || c < 0 || !(register.getShares(c) > 0)) return null;
        return new Shares(register.getShares(c), register.getOutstanding(c), market.price(c), market.fair(c),
                market.hasTraded(c), register.getDividendThisMonth(c), register.dividendPerShareAnnual(c));
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

        // Its share capital (0.7.75, R3): the register's, a sector's own.
        Equity register = game.getEquity();
        int company = Equity.indexOf(key);
        boolean listed = register != null && company >= 0 && company != Equity.BANK;
        // ...the sheet's prices and quantities, as it was pushed (R6) - at the month's close since 0.7.102 (A16)...
        double[] valued = economy.getValuedAt(key);
        if (valued == null) valued = new double[4];
        // ...and last month's stock at this month's prices.
        Map<Good, double[]> stock = sector.stockAtPrices();
        Map<Good, double[]> stockBefore = stockAtClose.get(key);
        double stockRevalued = 0;
        if (stockBefore != null) {
            for (Map.Entry<Good, double[]> e : stockBefore.entrySet()) {
                stockRevalued += e.getValue()[0] * (sector.stockPrice(e.getKey()) - e.getValue()[1]);
            }
        }
        stockAtClose.put(key, stock);

        return new SectorMonth(key, game.getMonth(),
                st.revenue, st.inputs, st.payroll, st.electricity, st.water,
                st.operatingIncome, st.interest, st.propertyTax, st.preTaxIncome, st.profitTax, st.netIncome,
                sheet.getCash(),
                sheet.getInventory(),
                sheet.getLand(),
                sheet.getBuildings(),
                sheet.getBondsPayable(),
                opening,
                // What it was HANDED: the principal less the loan's fee,
                // which the bank kept back (0.7.7) - the cash that arrived -
                // and less a mortgage's premium, which went to the treasury
                // out of the principal (0.7.11).
                credit.getLentThisMonth(key) - credit.getFeesThisMonth(key)
                        - credit.getPremiumsThisMonth(key),
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
                economy.getStolen(key),
                game.getSalvageThisMonth(key),
                st.paidEarlier,
                economy.getBondAssets(key),
                game.getBondMarket().getProceeds(key),
                game.getBondMarket().getRepaid(key),
                game.getBondMarket().getBoughtNet(key),
                game.getBondMarket().getCouponsTo(key),
                sheet.getTradeReceivables(),
                sheet.getTradePayables(),
                sector.getTradeCreditCash(),
                game.getArrearsPaidTo(key),
                listed ? register.getPaidIn(company) : 0,
                listed ? register.getFoundedThisMonth(company) : 0,
                listed && register.isPaidInDerived(company),
                credit.getFeesThisMonth(key),
                credit.getPremiumsThisMonth(key),
                game.getBondMarket().getIssued(key) - game.getBondMarket().getProceeds(key),
                credit.getBondWrittenOffThisMonth(key),
                valued[0], valued[1], valued[2], valued[3],
                stockRevalued, stockBefore != null,
                // ...and what its borrowing cost it up front: the month's sixtieth, and what is still to expense (0.7.102).
                st.borrowingCosts, sector.getBorrowingCostsToExpense(), true);
    }

    /**
     * A SAVE FROM BEFORE 0.7.75 KEPT NO SHARE CAPITAL (R3): its two months
     * read nothing there. Put on the register's derived figure (Equity's
     * restore()) - this month's as it stands, last month's the same less
     * what this month's shares, buybacks and founders moved it by, so the
     * equity statement's share capital closes - and marked derived, which
     * the statements say. Called by the load path after restoreFrom() for
     * such a save only.
     */
    public void derivePaidIn(Equity register) {
        if (register == null) return;
        for (Map.Entry<String, SectorMonth> e : now.entrySet()) {
            int c = Equity.indexOf(e.getKey());
            if (c < 0 || c == Equity.BANK) continue;
            SectorMonth m = e.getValue();
            double capital = register.getPaidIn(c);
            boolean derived = register.isPaidInDerived(c);
            e.setValue(m.withPaidIn(capital, derived));
            SectorMonth b = before.get(e.getKey());
            if (b != null) {
                before.put(e.getKey(), b.withPaidIn(capital - m.equityRaised() + m.sharesBoughtBack() - m.founded(), derived));
            }
        }
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
        // ...and R1, R2 and the shares are not saved (0.7.74): not counted until a month runs.
        debtNow.clear();
        debtBefore.clear();
        sharesNow.clear();
        sharesBefore.clear();
        // ...nor last month's stock by good (0.7.75, R6): the month after a load does not count its revaluation.
        stockAtClose.clear();
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
        for (Map.Entry<String, Debt> e : debtNow.entrySet()) e.setValue(e.getValue().scaled(scale));
        for (Map.Entry<String, Debt> e : debtBefore.entrySet()) e.setValue(e.getValue().scaled(scale));
        for (Map.Entry<String, Shares> e : sharesNow.entrySet()) e.setValue(e.getValue().scaled(scale));
        for (Map.Entry<String, Shares> e : sharesBefore.entrySet()) e.setValue(e.getValue().scaled(scale));
        for (Map<Good, double[]> m : stockAtClose.values()) for (double[] v : m.values()) v[1] *= scale;
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
                m.maintenance() * s, m.stolen() * s, m.salvage() * s, m.paidEarlier() * s,
                m.bondAssets() * s, m.bondsIssued() * s, m.bondsRepaid() * s, m.bondsBought() * s,
                m.bondCoupons() * s,
                m.tradeReceivables() * s, m.tradePayables() * s, m.tradeCredit() * s,
                m.arrearsPaid() * s,
                m.paidIn() * s, m.founded() * s, m.paidInDerived(),
                m.loanFees() * s, m.premiums() * s, m.bondCosts() * s, m.bondsWrittenOff() * s,
                m.landSqFt(), m.landPrice() * s, m.buildingMaterials(), m.materialsPrice() * s,
                m.stockRevalued() * s, m.stockCounted(),
                m.borrowingCosts() * s, m.borrowingCostsToExpense() * s, m.borrowingCostsDeferred());
    }
}

package ham.citybuildersim;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A month of books for every business in the city, and last month's too.
 *
 * WHY THIS EXISTS AT ALL. Six sectors keep their own figures in five different
 * handlers, in five different shapes: the mines report a revenue and a payroll,
 * commercial reports two businesses out of one object, construction reports
 * unearned revenue nobody else has. Every screen that wanted to compare them
 * had to know all five, and none of them could show LAST month at all, because
 * a handler holds this month and nothing else.
 *
 * So: one shape, read once a month, kept for two months. What the sector
 * screens draw is this, not the handlers - which means every sector's income
 * statement has the same lines in the same order whether or not the handler
 * behind it happens to have a getter for them.
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
            double foreignInterest) {

        /** What the sheet says the owners have. */
        public double equity() {
            return totalAssets() - bondsPayable;
        }

        public double totalAssets() {
            return cash + inventory + land + buildings + foreignAssets;
        }

        /** Everything that is not payroll, inputs or utilities. */
        public double operatingCost() {
            return inputs + payroll + electricity + water;
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
                    - spentOnBuildings);
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
                    0, 0, 0);
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

    public SectorMonth get(PolicySector sector) {
        return now.getOrDefault(sector.creditName(),
                SectorMonth.none(sector.creditName()));
    }

    public SectorMonth previous(PolicySector sector) {
        return before.getOrDefault(sector.creditName(),
                SectorMonth.none(sector.creditName()));
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

        for (PolicySector sector : PolicySector.values()) {
            SectorMonth month = read(game, sector);
            now.put(sector.creditName(), month);
            lastCash.put(sector.creditName(), month.cash());
        }
    }

    /**
     * One sector's figures, gathered from whichever handler holds them.
     *
     * THE FIVE-WAY SWITCH IS THE POINT. It exists once, here, so that no screen
     * ever has to know that Retail and Real Estate come out of the same object
     * or that construction keeps its wage bill under a different name.
     */
    private SectorMonth read(Game game, PolicySector sector) {

        EconomyManager economy = game.getEconomyManager();
        BusinessDebtManager credit = economy.getBusinessDebtManager();
        String key = sector.creditName();

        double opening = lastCash.getOrDefault(key, 0.0);

        BalanceSheet sheet;
        double revenue, inputs, payroll, power, water;
        double operating, interest, propertyTax, preTax, tax;

        switch (sector) {

            /*
             * TWO BUSINESSES OUT OF ONE OBJECT. CommercialHandler runs the
             * shops and the landlords side by side and keeps two of everything
             * - two cash reserves, two interest bills, two tax figures - which
             * is why they are two sectors here and one row on the old screen.
             */
            case RETAIL -> {
                CommercialHandler h = economy.getCommercialHandler();
                revenue = h.getGrossRevenue();
                inputs = h.getReportInventoryCost() + h.getReportRetailMaintenance();
                payroll = h.getReportPayroll();
                power = h.getReportElectricityCost();
                water = h.getReportWaterCost();
                interest = h.getReportRetailInterest();
                propertyTax = h.getReportRetailPropertyTax();
                operating = revenue - inputs - payroll - power - water;
                preTax = h.getReportRetailNetIncome();
                tax = h.getReportRetailTax();
                sheet = h.getRetailBalanceSheet();
            }

            case REAL_ESTATE -> {
                CommercialHandler h = economy.getCommercialHandler();
                revenue = h.getReportRentIncome();
                inputs = h.getReportPropertyMaintenance();
                payroll = 0;
                power = 0;
                water = 0;
                interest = h.getReportRealEstateInterest();
                propertyTax = h.getReportPropertyTaxExpense();
                operating = revenue - inputs;
                preTax = h.getReportRealEstateNetIncome();
                tax = h.getReportRealEstateTax();
                sheet = h.getRealEstateBalanceSheet();
            }

            case INDUSTRY -> {
                IndustrialHandler h = economy.getIndustrialHandler();
                revenue = h.getGrossRevenue();
                inputs = h.getReportMaintenanceExpense();
                payroll = h.getReportPayroll();
                power = h.getReportElectricityCost();
                water = h.getReportWaterCost();
                interest = h.getReportInterestExpense();
                propertyTax = h.getReportPropertyTaxExpense();
                operating = h.getReportOperatingIncome();
                // The handler's own figure, not a re-derivation of it. This was
                // `operating - interest - propertyTax`, which was the same
                // number until the statement grew a sales tax line and then
                // silently was not. See MiningHandler.getReportPropertyTaxExpense().
                preTax = h.getNetIncome();
                tax = h.getReportTaxIncome();
                sheet = h.getBalanceSheet();
            }

            case HEAVY_INDUSTRY -> {
                HeavyIndustryHandler h = economy.getHeavyIndustryHandler();
                revenue = h.getReportRevenue();
                inputs = h.getReportInputCost() + h.getReportMaintenanceExpense();
                payroll = h.getReportPayroll();
                power = h.getReportElectricityCost();
                water = h.getReportWaterCost();
                interest = h.getReportInterestExpense();
                propertyTax = h.getReportPropertyTaxExpense();
                operating = h.getReportOperatingIncome();
                preTax = h.getReportNetIncome();
                tax = h.getTaxIncome(h.getTaxRate());
                sheet = h.getBalanceSheet();
            }

            case MINING -> {
                MiningHandler h = economy.getMiningHandler();
                revenue = h.getReportRevenue();
                inputs = h.getReportMaintenanceExpense();
                payroll = h.getReportPayroll();
                power = h.getReportElectricityCost();
                water = h.getReportWaterCost();
                interest = h.getReportInterestExpense();
                operating = revenue - h.getReportOperatingCost();
                preTax = h.getReportNetIncome();
                // It has a getter now. This used to come out of the identity
                // net = operating - interest - property tax, which stopped being
                // true the moment the sales tax joined the statement - and a
                // derived line does not fail when that happens, it just quietly
                // becomes the sum of everything nobody named.
                propertyTax = h.getReportPropertyTaxExpense();
                tax = h.getTaxIncome(h.getTaxRate());
                sheet = h.getBalanceSheet();
            }

            default -> {   // CONSTRUCTION
                ConstructionHandler h = game.getServicesManager().getConstructionHandler();
                revenue = h.getReportRevenue();
                inputs = h.getReportMaterialsExpense() + h.getReportMaintenanceExpense();
                payroll = h.getReportWageExpense();
                power = 0;
                water = 0;
                interest = h.getReportInterestExpense();
                propertyTax = h.getReportPropertyTaxExpense();
                operating = revenue - inputs - payroll;
                preTax = h.getNetIncome();
                // Since 2026-09-10 the builders pay profit tax like the other
                // five - the Policy screen had been offering a lever for it
                // that collected nothing. See ConstructionHandler.getTaxIncome().
                tax = h.getReportProfitTax();
                sheet = h.getBalanceSheet();
            }
        }

        return new SectorMonth(key, game.getMonth(),
                revenue, inputs, payroll, power, water,
                operating, interest, propertyTax, preTax, tax, preTax - tax,
                sheet == null ? 0 : sheet.getCash(),
                sheet == null ? 0 : sheet.getInventory(),
                sheet == null ? 0 : sheet.getLand(),
                sheet == null ? 0 : sheet.getBuildings(),
                sheet == null ? 0 : sheet.getBondsPayable(),
                opening,
                credit.getLentThisMonth(key),
                credit.getRepaidThisMonth(key),
                game.getSubsidyPaid(sector),
                economy.getOverdraftForgivenThisMonth(key),
                economy.getDepositInterestPaid(key),
                game.getInvestedThisMonth(key),
                economy.getSectorSalesTax(sector),
                credit.getRate(key), credit.getLeverage(key),
                credit.getWrittenOffThisMonth(key),
                credit.isBorrowingBlocked(key),
                economy.getForeignAssets(key),
                economy.getOutwardInvestment() == null ? 0
                        : economy.getOutwardInvestment().getMovedThisMonth(key),
                economy.getOutwardInvestment() == null ? 0
                        : economy.getOutwardInvestment().getInterestThisMonth(key));
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
}

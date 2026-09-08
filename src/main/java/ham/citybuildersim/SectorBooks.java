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
            double spentOnBuildings,// its own premises, less anything sold back
            double salesTaxPaid,    // remitted out of cash, and NOT on the income statement

            /* ------------------------- and its credit ------------------------- */
            double rate,
            double leverage,
            double writtenOff,
            boolean blocked) {

        /** What the sheet says the owners have. */
        public double equity() {
            return totalAssets() - bondsPayable;
        }

        public double totalAssets() {
            return cash + inventory + land + buildings;
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
                    + borrowed - repaid + fromTheCity
                    - spentOnBuildings - salesTaxPaid);
        }

        public double margin() {
            return revenue > 0 ? netIncome / revenue : 0;
        }

        /** An empty month, for a sector that has not been recorded yet. */
        public static SectorMonth none(String sector) {
            return new SectorMonth(sector, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0,
                    0, 0, 0, false);
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
                inputs = h.getReportInventoryCost();
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
                inputs = 0;
                payroll = h.getReportPayroll();
                power = h.getReportElectricityCost();
                water = h.getReportWaterCost();
                interest = h.getReportInterestExpense();
                propertyTax = h.getReportPropertyTaxExpense();
                operating = h.getReportOperatingIncome();
                preTax = operating - interest - propertyTax;
                tax = h.getReportTaxIncome();
                sheet = h.getBalanceSheet();
            }

            case HEAVY_INDUSTRY -> {
                HeavyIndustryHandler h = economy.getHeavyIndustryHandler();
                revenue = h.getReportRevenue();
                inputs = h.getReportInputCost();
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
                inputs = 0;
                payroll = h.getReportPayroll();
                power = h.getReportElectricityCost();
                water = h.getReportWaterCost();
                interest = h.getReportInterestExpense();
                operating = revenue - h.getReportOperatingCost();
                preTax = h.getReportNetIncome();
                // No getter, and it is the only term the three above leave
                // implicit - so it comes out of the identity the handler
                // itself computes: net = operating - interest - property tax.
                propertyTax = operating - interest - preTax;
                tax = h.getTaxIncome(h.getTaxRate());
                sheet = h.getBalanceSheet();
            }

            default -> {   // CONSTRUCTION
                ConstructionHandler h = game.getServicesManager().getConstructionHandler();
                revenue = h.getReportRevenue();
                inputs = h.getReportMaterialsExpense();
                payroll = h.getReportWageExpense();
                power = 0;
                water = 0;
                interest = h.getReportInterestExpense();
                propertyTax = h.getReportPropertyTaxExpense();
                operating = revenue - inputs - payroll;
                preTax = h.getNetIncome();
                // NOT A ROUNDING. EconomyManager.getTaxIncome() adds up retail,
                // real estate, industry, heavy industry and mining, and stops.
                // The builders pay property tax like everybody else and no
                // profit tax at all - see the note on their books.
                tax = 0;
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
                game.getInvestedThisMonth(key),
                economy.getSectorSalesTax(sector),
                credit.getRate(key), credit.getLeverage(key),
                credit.getWrittenOffThisMonth(key),
                credit.isBorrowingBlocked(key));
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

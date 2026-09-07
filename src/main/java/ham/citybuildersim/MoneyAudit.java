package ham.citybuildersim;

/**
 * Where the money went this month, and whether it all went somewhere.
 *
 * THE IDENTITY
 *
 * Every dollar in the game sits in one of a handful of pools: the treasury,
 * the six private sectors' cash, and the construction sector's order book (a
 * build is paid for up front and earned as the work is done, so the unearned
 * part is money the builder holds and has not yet booked). Everything else -
 * households, the lender, other cities, the world - is OUTSIDE, and money only
 * ever crosses that boundary in a known set of ways: wages out, shopping and
 * rent in, imports out, exports in, loans in, repayments and interest out,
 * pensions and subsidies out, fees and wage tax in.
 *
 * So for one month tick:
 *
 *     pooled(after) - pooled(before)  ==  inflows - outflows
 *
 * and the difference between the two sides is the RESIDUAL. A residual is a
 * dollar that appeared from nowhere or vanished into nothing, and every one of
 * this codebase's money bugs - the quote that charged the city for a private
 * investor's materials, the tax collected on money the sector kept, the VAT
 * struck on sales nobody paid for - was a residual that nothing was measuring.
 *
 * WHAT IT IS NOT
 *
 * Not a second set of books. Every figure here is read off the statement the
 * sector already wrote for the month, so this cannot disagree with the screen.
 * What it CAN disagree with is the cash, and that is the point: a statement
 * that says one thing while the bank balance moves by another is exactly what
 * this catches. Internal flows - a shop paying a mill, a mill paying a mine,
 * the city collecting a tax - are deliberately not listed. They cancel if both
 * sides booked the same number, and show up as residual if they did not.
 *
 * Struck by Game.nextMonth() every month, always, because it costs forty
 * getter reads; asserted by LongPlaytest and MoneyCheck.
 */
public final class MoneyAudit {

    /** One month's strike. Immutable, so a harness can keep the worst one. */
    public static final class Result {
        public static final Result NONE = new Result(0, 0, 0, 0, 0, 0);

        public final int month;
        public final double before;
        public final double after;
        public final double inflows;
        public final double outflows;
        /** (after - before) - (inflows - outflows). Zero when money is conserved. */
        public final double residual;

        /** Every pool and flow by name, for chasing a residual. */
        public final String detail;

        Result(int month, double before, double after, double inflows, double outflows, double residual) {
            this(month, before, after, inflows, outflows, residual, "");
        }

        Result(int month, double before, double after, double inflows, double outflows, double residual, String detail) {
            this.month = month;
            this.before = before;
            this.after = after;
            this.inflows = inflows;
            this.outflows = outflows;
            this.residual = residual;
            this.detail = detail;
        }

        /** Residual as a share of what moved, so a $3 leak in a $3B city reads as 0. */
        public double relative() {
            double moved = Math.max(1, Math.abs(inflows) + Math.abs(outflows));
            return Math.abs(residual) / moved;
        }

        @Override public String toString() {
            return String.format("m%d pooled %,.2f -> %,.2f  in %,.2f  out %,.2f  residual %,.2f (%.4f%%)",
                    month, before, after, inflows, outflows, residual, relative() * 100);
        }
    }

    private MoneyAudit() {}

    static final String[] POOL_NAMES = {
        "city", "retail", "real estate", "industry", "heavy industry", "mining",
        "construction", "order book", "cheque in the post"
    };

    /** The pools, in POOL_NAMES order. */
    public static double[] pools(Game g) {
        EconomyManager e = g.getEconomyManager();
        ConstructionHandler c = g.getServicesManager().getConstructionHandler();
        return new double[] {
            g.getCash(),
            e.getSectorCash(BusinessDebtManager.RETAIL),
            e.getSectorCash(BusinessDebtManager.REAL_ESTATE),
            e.getSectorCash(BusinessDebtManager.INDUSTRY),
            e.getSectorCash(BusinessDebtManager.HEAVY_INDUSTRY),
            e.getSectorCash(BusinessDebtManager.MINING),
            c.getCash(),
            c.getUnearnedRevenue(),
            /*
             * The stores' payment for local food. Retail's statement charges it
             * this month; the mills' statement books it NEXT month (the demand
             * signal is a month behind by design - see
             * EconomyManager.startOfMontEconUpdate). Between the two it is a
             * cheque in the post, and it belongs to the mills.
             */
            e.getCommercialHandler().getReportLocalPurchaseValue()
        };
    }

    /** Every dollar the city and its businesses hold, plus the builder's order book. */
    public static double pooled(Game g) {
        double total = 0;
        for (double p : pools(g)) total += p;
        return total;
    }

    /**
     * Strikes the month. Called at the bottom of nextMonth(), after every
     * statement has run and every payment has moved.
     *
     * @param before      pooled() as it stood at the top of the month
     * @param interestDue the city's interest accrued going in, which is what
     *                    finalUpdateEconomy() pays this month and then clears
     */
    static Result strike(Game g, double before, double interestDue) {
        return strike(g, before, null, interestDue);
    }

    /** As above, and with the opening pools the result can say which pool moved unexplained. */
    static Result strike(Game g, double before, double[] poolsBefore, double interestDue) {
        StringBuilder detail = new StringBuilder();
        java.util.function.BiConsumer<String, Double> note = (label, amount) -> {
            if (amount != 0) detail.append(String.format("      %-36s %,14.2f%n", label, amount));
        };
        java.util.function.BiFunction<String, Double, Double> flow = (label, amount) -> {
            note.accept(label, amount);
            return amount;
        };
        EconomyManager e = g.getEconomyManager();
        CommercialHandler retail = e.getCommercialHandler();
        IndustrialHandler food = e.getIndustrialHandler();
        HeavyIndustryHandler mills = e.getHeavyIndustryHandler();
        MiningHandler mines = e.getMiningHandler();
        ConstructionHandler builders = g.getServicesManager().getConstructionHandler();
        UtilitiesHandler utilities = g.getServicesManager().getUtilitiesHandler();
        BusinessDebtManager lender = e.getBusinessDebtManager();
        Healthcare care = g.getHealthcare();
        Education schools = g.getEducation();

        double in = 0;
        // Households: what they spend in the shops and on rent, and what the
        // city takes off their pay and charges them at the door.
        in += flow.apply("+ retail GrossRevenue", retail.getGrossRevenue());
        in += flow.apply("+ retail RentIncome", retail.getReportRentIncome());
        in += flow.apply("+ e WageTax", e.getWageTax());
        in += flow.apply("+ e Contributions", e.getContributions());
        in += flow.apply("+ care Fees", care.getFees());
        in += flow.apply("+ schools Fees", schools.getFees());
        // The world: exports, at the price the statement sold them for.
        in += flow.apply("+ food FoodExportRevenue", food.getFoodExportRevenue());
        in += flow.apply("+ mills Revenue", mills.getReportRevenue());
        in += flow.apply("+ mines OreExported * mines ExportPr", mines.getReportOreExported() * mines.getReportExportPrice());
        // The lender.
        in += flow.apply("+ lender LentThisMonth", lender.getLentThisMonth());
        in += flow.apply("+ city CityDebtRaisedThisMonth", g.getCityDebtRaisedThisMonth());

        double out = 0;
        // Payrolls, as each statement charged them.
        out += flow.apply("- retail Payroll", retail.getReportPayroll());
        out += flow.apply("- food Payroll", food.getReportPayroll());
        out += flow.apply("- mills Payroll", mills.getReportPayroll());
        out += flow.apply("- mines Payroll", mines.getReportPayroll());
        out += flow.apply("- builders WageExpense", builders.getReportWageExpense());
        out += flow.apply("- utilities UtilityPayroll", utilities.getUtilityPayroll());
        out += flow.apply("- care Payroll", care.getPayroll());
        out += flow.apply("- schools Payroll", schools.getPayroll());
        // Imports.
        out += flow.apply("- retail ImportPurchaseValue", retail.getReportImportPurchaseValue());
        // The mill's input bill less the ore it bought locally, at the price
        // the mine's statement sold it for. If the two statements disagree on
        // that price the difference lands in the residual, which is right.
        out += Math.max(0, mills.getReportInputCost()
                - mills.getReportLocalOreUsed() * mines.getReportLocalPrice());
        out += flow.apply("- builders MaterialsExpense", builders.getReportMaterialsExpense());
        // Interest and principal to the lender.
        out += flow.apply("- retail RetailInterest", retail.getReportRetailInterest());
        out += flow.apply("- retail RealEstateInterest", retail.getReportRealEstateInterest());
        out += flow.apply("- food InterestExpense", food.getReportInterestExpense());
        out += flow.apply("- mills InterestExpense", mills.getReportInterestExpense());
        out += flow.apply("- mines InterestExpense", mines.getReportInterestExpense());
        out += flow.apply("- builders InterestExpense", builders.getReportInterestExpense());
        out += flow.apply("- lender RepaidThisMonth", lender.getRepaidThisMonth());
        out += flow.apply("- interestDue", interestDue);
        out += flow.apply("- city CityPrincipalRepaidThisMonth", g.getCityPrincipalRepaidThisMonth());
        // What the city pays the outside world: pensions, the schools' tuition
        // subsidy (paid to households), and the upkeep of its own services.
        out += flow.apply("- e PensionsPaid", e.getPensionsPaid());
        out += flow.apply("- care Upkeep", care.getUpkeep());
        out += flow.apply("- schools Upkeep", schools.getUpkeep());
        out += flow.apply("- schools Subsidy", schools.getSubsidy());

        // Suspect internal pairs, for the detail only. A flow that should
        // cancel and does not is where a residual lives.
        note.accept("chk profit tax collected (city)", e.getBusinessTax() + e.getIndustrialTax() + e.getHeavyIndustryTax());
        note.accept("chk VAT collected (city)", e.getSalesTax());
        note.accept("chk property tax collected", e.getTotalPropertyTax());
        note.accept("chk retail local purchase (net)", retail.getReportLocalPurchaseValue());
        note.accept("chk retail inventory cost paid", retail.getReportInventoryCost());
        note.accept("chk industry local revenue", food.getGrossRevenue() - food.getFoodExportRevenue());
        note.accept("chk utility revenue", utilities.getUtilityRevenue());
        note.accept("chk sector utility bills", retail.getReportElectricityCost() + retail.getReportWaterCost()
                + food.getReportElectricityCost() + food.getReportWaterCost()
                + mills.getReportElectricityCost() + mills.getReportWaterCost()
                + mines.getReportElectricityCost() + mines.getReportWaterCost());
        note.accept("chk mills ore bought locally", mills.getReportLocalOreUsed() * mines.getReportLocalPrice());
        note.accept("chk mines ore sold locally", mines.getReportOreSoldLocally() * mines.getReportLocalPrice());
        note.accept("chk construction revenue banked", builders.getReportRevenue());
        note.accept("chk construction subsidy", builders.getSubsidyThisMonth());
        note.accept("chk land sold to sectors", g.getLandManager().getLandSalesThisMonth());

        double[] poolsAfter = pools(g);
        double after = 0;
        for (double p : poolsAfter) after += p;
        if (poolsBefore != null) {
            for (int i = 0; i < POOL_NAMES.length; i++) {
                note.accept("pool " + POOL_NAMES[i] + " moved", poolsAfter[i] - poolsBefore[i]);
            }
        }
        double residual = (after - before) - (in - out);
        return new Result(g.getMonth(), before, after, in, out, residual, detail.toString());
    }
}

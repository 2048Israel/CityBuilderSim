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
        "construction", "order book", "cheque in the post", "bank"
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
            e.getCommercialHandler().getReportLocalPurchaseValue(),
            /*
             * THE BANK, since 2026-09-07. It holds the city's lending, so a
             * loan to a sector or to the treasury is now an internal transfer
             * that cancels rather than money arriving from outside and interest
             * vanishing into it - a strictly better identity than the one it
             * replaces, where both ends were nowhere.
             *
             * Households are still OUTSIDE, as they have always been, so what
             * the bank lends a family is a real outflow and what it gets back
             * is a real inflow. Those two lines are declared below.
             */
            g.getBank().getCash()
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
        /*
         * THE LENDER IS INSIDE THE CITY NOW.
         *
         * A business loan and a city bond used to arrive from outside and their
         * interest used to leave, so the audit saw money created at one end and
         * destroyed at the other and balanced only because both were declared.
         * The bank funds them out of its own cash, so all four of those lines -
         * lent, repaid, interest, principal - are transfers between two pools
         * and cancel. What is left is what actually crosses the city's edge.
         */
        in += flow.apply("+ bank RepaidByHouseholds", g.getBank().getRepaidByHouseholds());
        /*
         * The shareholders' capital when a branch opens - money from outside the
         * city, and the only reason a bank can begin lending at all. A bailout
         * paid by the treasury is NOT here: that is the city's own money moving
         * to the bank's pool, and it cancels.
         */
        in += flow.apply("+ bank CapitalInjected", g.getBank().getCapitalInjected());
        // The first branch settling with the lenders it replaces - see
        // Bank.openBranches(). Signed, because it goes either way.
        in += flow.apply("+ bank FoundingSettlement", g.getBank().getFoundingSettlement());
        /*
         * A failed bank's creditors absorbing the shortfall. They are the
         * wholesale funders, who are outside the city, so the money the city
         * keeps and will not repay arrives here. See Bank.resolveIfFailed().
         */
        in += flow.apply("+ bank ResolutionLoss", g.getBank().getResolutionLoss());

        in += flow.apply("+ bank InterestEarned", g.getBank().getInterestEarned()
                - g.getBank().getInternalInterest());

        double out = 0;
        // Payrolls, as each statement charged them.
        out += flow.apply("- retail Payroll", retail.getReportPayroll());
        /*
         * The bank's tellers, which used to be inside the line above.
         *
         * Wages leave the audited system whoever pays them, so moving them from
         * the shops' payroll to the bank's changes which pool they come out of
         * and nothing else about the identity - which is exactly why it was safe
         * to move. Declared separately so the two can be read apart.
         */
        out += flow.apply("- bank Payroll", g.getBank().getPayroll());
        /*
         * Interest paid to SAVERS, since the bank started paying for its
         * deposits. The households' share leaves the audited system, because
         * households have always been outside it; the sectors' share does not,
         * because it moves from the bank's pool to theirs and cancels.
         */
        out += flow.apply("- bank DepositInterest (households)",
                g.getBank().getDepositInterestToHouseholds());
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
        // Lending to a family crosses the city's edge; lending to a sector or
        // to the treasury no longer does - see the pools above.
        out += flow.apply("- bank LentToHouseholds", g.getBank().getLentToHouseholds());
        /*
         * WHAT THE BANK PAYS FOR MONEY IT DID NOT HAVE, since 2026-09-07.
         *
         * A negative cash position IS borrowing, and the part of it the city's
         * own deposits do not cover is funded abroad. That coupon leaves the
         * city, so it is declared here. The principal is not: the bank's cash
         * going further negative is already the loan arriving, and the pool
         * moves with it - declaring it as well would count it twice.
         */
        out += flow.apply("- bank FundingCost", g.getBank().getFundingCost());
        /*
         * The bank's OWN staff and upkeep are not here, and that is deliberate
         * rather than missing. It is a COMMERCIAL building, so its jobs land in
         * the commercial sector's payroll and its upkeep in the commercial
         * sector's costs, exactly like a grocery store's - the shops are paying
         * the tellers. Wrong in an org chart, right in the books, and giving
         * the bank its own building category to fix it is a bigger change than
         * it earns today.
         */
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

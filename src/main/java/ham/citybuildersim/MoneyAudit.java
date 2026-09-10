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

        /* ---- the same month, seen as a balance of payments ---- */

        /** Goods and services sold abroad. */
        public final double tradeIn;
        /** ...and bought abroad. */
        public final double tradeOut;
        /** Interest received from foreign borrowers. */
        public final double incomeIn;
        /** ...and paid to foreign lenders. */
        public final double incomeOut;
        /** Capital arriving from abroad. */
        public final double financialIn;
        /** ...and leaving. */
        public final double financialOut;
        /** Foreign claims written off or settled, in the city's favour. */
        public final double valuationIn;
        /** ...and against it. */
        public final double valuationOut;
        /** Reserves sold for local money - the financing item, below the line. */
        public final double reserveIn;
        /** ...and bought with it. */
        public final double reserveOut;

        /** Exports less imports. The visible balance. */
        public double tradeBalance()     { return tradeIn - tradeOut; }

        /** What the city earns on foreign assets, less what it pays on foreign debts. */
        public double incomeBalance()    { return incomeIn - incomeOut; }

        /** The two together. */
        public double currentAccount()   { return tradeBalance() + incomeBalance(); }

        /** Capital in less capital out. Real money, so it moves the reserve. */
        public double financialAccount() { return financialIn - financialOut; }

        /**
         * Claims forgiven or settled. Improves what the city owes the world; is
         * NOT a dollar earned, so it stays out of the reserve.
         */
        public double valuationChange() { return valuationIn - valuationOut; }

        /**
         * What the city's foreign position moved by this month.
         *
         * The two accounts together, which is the whole of the balance of
         * payments: every dollar that crossed the city's edge in either
         * direction, and nothing that merely crossed to a household.
         */
        public double foreignBalance()   { return currentAccount() + financialAccount(); }

        /** Everything crossing the edge that is foreign, in each direction. */
        public double foreignIn()  {
            return tradeIn + incomeIn + financialIn + valuationIn + reserveIn;
        }
        public double foreignOut() {
            return tradeOut + incomeOut + financialOut + valuationOut + reserveOut;
        }

        /** What the treasury did to its own reserve stock this month. */
        public double reserveChange() { return reserveOut - reserveIn; }

        /** ...and everything crossing it that is not. Households, in other words. */
        public double domesticIn()  { return inflows - foreignIn(); }
        public double domesticOut() { return outflows - foreignOut(); }

        Result(int month, double before, double after, double inflows, double outflows, double residual) {
            this(month, before, after, inflows, outflows, residual, "", new double[8]);
        }

        Result(int month, double before, double after, double inflows, double outflows,
               double residual, String detail, double[] foreign) {
            this.month = month;
            this.before = before;
            this.after = after;
            this.inflows = inflows;
            this.outflows = outflows;
            this.residual = residual;
            this.detail = detail;
            this.tradeIn = foreign[0];
            this.tradeOut = foreign[1];
            this.incomeIn = foreign[2];
            this.incomeOut = foreign[3];
            this.financialIn = foreign[4];
            this.financialOut = foreign[5];
            this.reserveIn = foreign.length > 8 ? foreign[8] : 0;
            this.reserveOut = foreign.length > 9 ? foreign[9] : 0;
            this.valuationIn = foreign[6];
            this.valuationOut = foreign[7];
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

    /**
     * WHICH SIDE OF WHICH BOUNDARY A FLOW CROSSES.
     *
     * "Outside the pools" has always meant two completely different things, and
     * until 2026-09-07 nothing here told them apart:
     *
     *   DOMESTIC  - households. They are outside the audited pools because they
     *               are not modelled as one, not because they are abroad. Wages,
     *               rent, shopping, the tax taken at the door, what the bank
     *               lends a family and what it gets back.
     *
     *   TRADE     - the rest of the world, goods and services: sold abroad,
     *               bought abroad.
     *
     *   INCOME     - the rest of the world, PRIMARY INCOME: interest paid to
     *               foreign creditors, and one day received from them. Kept
     *               apart from TRADE because "imports" ought to mean goods -
     *               and because a city whose current account is in deficit
     *               entirely on its interest bill is in a different kind of
     *               trouble from one that simply buys more than it sells.
     *
     *   FINANCIAL - the rest of the world, on the FINANCIAL ACCOUNT: capital
     *               actually moving. Shareholders putting money into the bank
     *               today; foreign borrowing and the carry trade later.
     *
     *   VALUATION - the rest of the world, but NOT money moving: the shortfall a
     *               failed bank's foreign creditors absorb, and the settlement
     *               when a city's first branch takes over from the lenders it
     *               replaces. These improve what the city owes the world without
     *               a single dollar being earned, so they belong in the audit -
     *               the pools really do move - and NOT in the reserve.
     *
     *               Measured before this was split out: a city's reserve swung
     *               from -$88M to +$50M and back on bank resolutions alone,
     *               which told the player nothing whatever about whether they
     *               were selling more than they were buying.
     *
     * Splitting them turns this class into a BALANCE OF PAYMENTS at no extra
     * cost. The foreign subset of a list that already reconciles to the cent IS
     * the balance of payments, and the running total of it is the city's
     * reserve position - see ForeignAccounts.
     *
     * The tag is on the flow itself rather than in a second list somewhere,
     * which is the whole point: a line added here cannot be forgotten there,
     * and ForeignCheck asserts that domestic and foreign together come to
     * exactly what this class already said crossed the edge.
     */
    /**
     * Which line of the balance of payments a flow belongs on.
     *
     * RESERVE is the odd one, and it is odd for the reason the textbooks make
     * it odd. A balance of payments reads:
     *
     *     current account + capital account + financial account
     *                                            = change in reserve assets
     *
     * Reserve transactions are the FINANCING item - the thing that settles the
     * balance - not part of the balance being settled. Putting them inside the
     * financial account makes an intervention finance itself, and it did:
     * ForeignAccounts moved the stock directly AND takeMonth() moved it again
     * off the financial account, so buying reserves cost cash and built
     * nothing, and SELLING them raised cash and left the stock where it was.
     * A player could sell $1,000 of reserves every month for ever. Jerus found
     * it by playing: "i can go to the tab and sell my reserves... and it builds
     * back up?"
     *
     * Still FOREIGN - the money genuinely crosses the city's edge and the
     * domestic/foreign split has to stay exhaustive - just not financial.
     */
    public enum Scope { DOMESTIC, TRADE, INCOME, FINANCIAL, VALUATION, RESERVE }

    /** Label, amount and scope, for one line of the month. */
    private interface Tagged {
        double apply(String label, double amount, Scope scope);
    }

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
        /*
         * Two recorders rather than one, so the DIRECTION is stated at the call
         * site instead of inferred. `in += credit.apply(...)` cannot quietly
         * become an outflow the way a single tagged helper could.
         */
        double[] foreign = new double[10];
        Tagged credit = (label, amount, scope) -> {
            note.accept(label, amount);
            switch (scope) {
                case TRADE     -> foreign[0] += amount;
                case INCOME    -> foreign[2] += amount;
                case FINANCIAL -> foreign[4] += amount;
                case VALUATION -> foreign[6] += amount;
                case RESERVE   -> foreign[8] += amount;
                default        -> { }
            }
            return amount;
        };
        Tagged debit = (label, amount, scope) -> {
            note.accept(label, amount);
            switch (scope) {
                case TRADE     -> foreign[1] += amount;
                case INCOME    -> foreign[3] += amount;
                case FINANCIAL -> foreign[5] += amount;
                case VALUATION -> foreign[7] += amount;
                case RESERVE   -> foreign[9] += amount;
                default        -> { }
            }
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
        in += credit.apply("+ retail GrossRevenue", retail.getGrossRevenue(), Scope.DOMESTIC);
        in += credit.apply("+ retail RentIncome", retail.getReportRentIncome(), Scope.DOMESTIC);
        in += credit.apply("+ e WageTax", e.getWageTax(), Scope.DOMESTIC);
        in += credit.apply("+ e Contributions", e.getContributions(), Scope.DOMESTIC);
        in += credit.apply("+ care Fees", care.getFees(), Scope.DOMESTIC);
        in += credit.apply("+ schools Fees", schools.getFees(), Scope.DOMESTIC);
        // The world: exports, at the price the statement sold them for.
        in += credit.apply("+ food FoodExportRevenue", food.getFoodExportRevenue(), Scope.TRADE);
        in += credit.apply("+ mills Revenue", mills.getReportRevenue(), Scope.TRADE);
        in += credit.apply("+ mines OreExported * mines ExportPr", mines.getReportOreExported() * mines.getReportExportPrice(), Scope.TRADE);
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
        in += credit.apply("+ bank RepaidByHouseholds", g.getBank().getRepaidByHouseholds(), Scope.DOMESTIC);
        /*
         * The shareholders' capital when a branch opens - money from outside the
         * city, and the only reason a bank can begin lending at all. A bailout
         * paid by the treasury is NOT here: that is the city's own money moving
         * to the bank's pool, and it cancels.
         */
        in += credit.apply("+ bank CapitalInjected", g.getBank().getCapitalInjected(), Scope.FINANCIAL);
        /*
         * ...and the half of it the city's own savers put up, which is money
         * arriving from outside the audited POOLS but not from outside the
         * COUNTRY. See Bank.injectCapital(): this line is why the balance of
         * payments is no longer dominated by a number nobody chose.
         */
        in += credit.apply("+ bank CapitalFromHome",
                g.getBank().getCapitalFromHome(), Scope.DOMESTIC);

        /*
         * HOT MONEY, arriving to chase the city's rate. A financial inflow with
         * no maturity and nothing owed - which is exactly what makes it
         * dangerous, and why the outflow line below is the one that ends
         * credit booms. See CapitalFlows.
         */
        in += credit.apply("+ bank HotMoneyIn", g.getBank().getHotMoneyIn(), Scope.FINANCIAL);
        /*
         * THE SECTORS' OWN MONEY, ABROAD AND BACK. The outflow is a financial
         * debit like a stranger's money leaving; the coupon it earns is
         * income, like the coupon the city pays. Both move inside this
         * window, which hot money does not - see OutwardInvestment.takeMonth().
         */
        in += credit.apply("+ sectors BroughtHome", g.getOutwardInvestment().getBroughtHomeThisMonth(), Scope.FINANCIAL);
        // Earned abroad and rolled there: an income credit and a financial
        // debit of the same size, and no cash in any pool. Declared both ways
        // so the balance of payments reads what a rolled coupon is.
        in += credit.apply("+ sectors ForeignInterest", g.getOutwardInvestment().getInterestThisMonth(), Scope.INCOME);
        /*
         * THE HOUSEHOLDS' PAPER ABROAD, since 2026-09-11. The households are
         * outside the pools, so every one of their foreign flows is a pair
         * that cancels here and reads on the balance of payments: what came
         * home is a financial inflow into their savings (domestic, out of
         * nobody's pool); what went abroad is a financial outflow out of
         * their savings; the coupon is income rolled abroad, like the
         * sectors'. See HouseholdBalance.investAbroad().
         */
        in += credit.apply("+ households BroughtHome", g.getHouseholdBalance().getBroughtHome(), Scope.FINANCIAL);
        in += credit.apply("+ households SavedFromAbroad", g.getHouseholdBalance().getSentAbroad(), Scope.DOMESTIC);
        in += credit.apply("+ households ForeignInterest", g.getHouseholdBalance().getForeignInterest(), Scope.INCOME);
        /*
         * THE OWNERS' MONEY, COMING IN. Shares sold to the city's households
         * are money arriving from outside the audited pools but inside the
         * country - like the shop's takings, and declared the same way; shares
         * sold to the world are a financial inflow with nothing owed on them
         * but a dividend, which is what equity is. See Equity.offer(). The
         * bank's own capital is on the two lines above it; these are the
         * sectors'.
         */
        in += credit.apply("+ equity SubscribedByHouseholds", g.getEquity().getRaisedHomeThisMonth() - g.getEquity().getRaisedHomeThisMonth(Equity.BANK), Scope.DOMESTIC);
        in += credit.apply("+ equity SubscribedAbroad", g.getEquity().getRaisedAbroadThisMonth() - g.getEquity().getRaisedAbroadThisMonth(Equity.BANK), Scope.FINANCIAL);
        /*
         * THE TRADING DESK. Every trade on the exchange is with the bank, so
         * every one is cash into or out of the bank's pool: a household
         * buying is money arriving from outside the pools, the world buying
         * is a financial inflow. See Exchange.
         */
        in += credit.apply("+ desk SharesSoldToHouseholds", g.getExchange().getSoldToHouseholds(), Scope.DOMESTIC);
        in += credit.apply("+ desk SharesSoldAbroad", g.getExchange().getSoldAbroad(), Scope.FINANCIAL);
        /*
         * The treasury selling reserves. Foreign money out, local money in - the
         * cash arrives in the city's pool from outside it, so it is declared.
         *
         * SCOPE.RESERVE, NOT FINANCIAL, and the difference is the whole of a
         * bug Jerus found by playing. See the Scope enum: this is the financing
         * item that settles the balance of payments, not part of the balance.
         * Tagged FINANCIAL, ForeignAccounts.takeMonth() added it straight back
         * to the stock the sale had just taken it out of, and a player could
         * sell the same reserves every month for ever.
         */
        in += credit.apply("+ treasury SoldReserves",
                g.getForeignAccounts().getSoldThisMonth(), Scope.RESERVE);
        // The first branch settling with the lenders it replaces - see
        // Bank.openBranches(). Signed, because it goes either way.
        in += credit.apply("+ bank FoundingSettlement", g.getBank().getFoundingSettlement(), Scope.VALUATION);
        /*
         * A failed bank's creditors absorbing the shortfall. They are the
         * wholesale funders, who are outside the city, so the money the city
         * keeps and will not repay arrives here. See Bank.resolveIfFailed().
         */
        in += credit.apply("+ bank ResolutionLoss", g.getBank().getResolutionLossThisMonth(), Scope.VALUATION);
        /*
         * A bankrupt sector's creditors absorbing its overdraft, the same
         * way. The bills were paid with money the sector did not have; the
         * restructure admits it and somebody outside eats it. See
         * EconomyManager.settleInsolvency().
         */
        in += credit.apply("+ sectors OverdraftForgiven", g.getEconomyManager().getOverdraftForgiven(), Scope.VALUATION);

        in += credit.apply("+ bank InterestEarned", g.getBank().getInterestEarned()
                - g.getBank().getInternalInterest(), Scope.DOMESTIC);

        /*
         * DOLLARS BORROWED ABROAD, which is the one kind of city borrowing that
         * crosses this boundary.
         *
         * The city's own bonds do not appear here at all, and should not: the
         * bank buys them, so cityDebtRaisedThisMonth goes to bank.lend() and
         * the money never leaves the audited pools. Foreign paper is bought by
         * somebody the city has no other relationship with, so the cash really
         * does arrive from outside - and it is a FINANCIAL flow, not a trade
         * one, because nothing was sold to earn it.
         */
        in += credit.apply("+ city ForeignDebtRaised",
                g.getForeignDebtRaisedThisMonth(), Scope.FINANCIAL);

        double out = 0;
        // Payrolls, as each statement charged them.
        out += debit.apply("- retail Payroll", retail.getReportPayroll(), Scope.DOMESTIC);
        /*
         * The bank's tellers, which used to be inside the line above.
         *
         * Wages leave the audited system whoever pays them, so moving them from
         * the shops' payroll to the bank's changes which pool they come out of
         * and nothing else about the identity - which is exactly why it was safe
         * to move. Declared separately so the two can be read apart.
         */
        out += debit.apply("- bank Payroll", g.getBank().getPayroll(), Scope.DOMESTIC);
        /*
         * Interest paid to SAVERS, since the bank started paying for its
         * deposits, and it goes to three different places.
         *
         *   HOUSEHOLDS leave the audited system, because households have always
         *   been outside it.
         *
         *   THE SECTORS do not: that money moves from the bank's pool to theirs
         *   and cancels, so it is not declared here at all.
         *
         *   THE HOT MONEY leaves the COUNTRY, so it is an income outflow on the
         *   balance of payments, beside the wholesale funding the bank raises
         *   abroad. Declared here since 2026-09-07; before that it was folded
         *   into the sectors' share, which paid foreign savers' interest to
         *   domestic businesses and - in any month where no business had a
         *   positive balance - paid it to nobody at all and destroyed it. See
         *   Bank.fundToCover().
         */
        out += debit.apply("- bank DepositInterest (households)", g.getBank().getDepositInterestToHouseholds(), Scope.DOMESTIC);
        out += debit.apply("- bank DepositInterest (abroad)", g.getBank().getDepositInterestToForeign(), Scope.INCOME);
        out += debit.apply("- food Payroll", food.getReportPayroll(), Scope.DOMESTIC);
        out += debit.apply("- mills Payroll", mills.getReportPayroll(), Scope.DOMESTIC);
        out += debit.apply("- mines Payroll", mines.getReportPayroll(), Scope.DOMESTIC);
        out += debit.apply("- builders WageExpense", builders.getReportWageExpense(), Scope.DOMESTIC);
        out += debit.apply("- utilities UtilityPayroll", utilities.getUtilityPayroll(), Scope.DOMESTIC);
        out += debit.apply("- care Payroll", care.getPayroll(), Scope.DOMESTIC);
        out += debit.apply("- schools Payroll", schools.getPayroll(), Scope.DOMESTIC);
        // Imports.
        out += debit.apply("- retail ImportPurchaseValue", retail.getReportImportPurchaseValue(), Scope.TRADE);
        // The mill's input bill less the ore it bought locally, at the price
        // the mine's statement sold it for. If the two statements disagree on
        // that price the difference lands in the residual, which is right.
        out += debit.apply("- mills ScrapImported", Math.max(0, mills.getReportInputCost()
                - mills.getReportLocalOreUsed() * mines.getReportLocalPrice()), Scope.TRADE);
        out += debit.apply("- builders MaterialsExpense", builders.getReportMaterialsExpense(), Scope.TRADE);
        // Lending to a family crosses the city's edge; lending to a sector or
        // to the treasury no longer does - see the pools above.
        out += debit.apply("- bank LentToHouseholds", g.getBank().getLentToHouseholds(), Scope.DOMESTIC);
        /*
         * WHAT THE BANK PAYS FOR MONEY IT DID NOT HAVE, since 2026-09-07.
         *
         * A negative cash position IS borrowing, and the part of it the city's
         * own deposits do not cover is funded abroad. That coupon leaves the
         * city, so it is declared here. The principal is not: the bank's cash
         * going further negative is already the loan arriving, and the pool
         * moves with it - declaring it as well would count it twice.
         */
        /*
         * THE BANK'S WHOLESALE FUNDING, SPLIT BY WHOSE MONEY IT IS.
         *
         * All of it used to be declared INCOME - interest paid to foreign
         * lenders - and at a book of $156B that single line was $592,109 a
         * month against $234,348 of exports. The city's whole balance of
         * payments was one number nobody had chosen, the current account was
         * negative however well it traded, and the exchange rate ran to its
         * ceiling. See Bank.fundingCostAbroad().
         */
        out += debit.apply("- bank FundingCost (abroad)",
                g.getBank().fundingCostAbroad(), Scope.INCOME);
        out += debit.apply("- bank FundingCost (at home)",
                g.getBank().fundingCostAtHome(), Scope.DOMESTIC);

        /*
         * ...AND WHAT IT COSTS TO OWE THEM.
         *
         * The coupon is INCOME - a payment to a foreign factor of production,
         * which is what the current account's income line is for - and the
         * principal is FINANCIAL, because repaying a debt is not consumption of
         * anything. That distinction is not decoration: interest is part of the
         * current account and therefore drives the exchange rate, while
         * principal is not and does not. Getting them the same way round would
         * make a city that merely rolls its debt over look like one running a
         * permanent deficit.
         *
         * The REVALUATION is deliberately absent. See
         * ForeignAccounts.takeForeignDebt(): a currency move makes the debt
         * dearer without a cent changing hands, so putting it here would break
         * the identity this whole class exists to prove.
         */
        out += debit.apply("- city ForeignInterestPaid",
                g.getForeignInterestPaidThisMonth(), Scope.INCOME);
        out += debit.apply("- city ForeignPrincipalRepaid",
                g.getForeignPrincipalRepaidThisMonth(), Scope.FINANCIAL);
        // ...and buying them, which is local money leaving to pay for foreign.
        // RESERVE for the same reason: it built nothing, because takeMonth()
        // took it off the stock again the moment the month closed.
        out += debit.apply("- bank HotMoneyOut",
                g.getBank().getHotMoneyOut(), Scope.FINANCIAL);
        out += debit.apply("- sectors InvestedAbroad", g.getOutwardInvestment().getInvestedAbroadThisMonth(), Scope.FINANCIAL);
        out += debit.apply("- sectors ForeignInterestReinvested", g.getOutwardInvestment().getInterestThisMonth(), Scope.FINANCIAL);
        // The households' three, the other way round. See the credits.
        out += debit.apply("- households InvestedAbroad", g.getHouseholdBalance().getSentAbroad(), Scope.FINANCIAL);
        out += debit.apply("- households BroughtHomeSaved", g.getHouseholdBalance().getBroughtHome(), Scope.DOMESTIC);
        out += debit.apply("- households ForeignInterestReinvested", g.getHouseholdBalance().getForeignInterest(), Scope.FINANCIAL);
        /*
         * ...AND GOING OUT. A dividend to a household leaves the pools the
         * way a wage does; a dividend to a shareholder abroad is income paid
         * to the world, like the coupon on a foreign bond, and lands on the
         * income account where the currency can feel it. Both companies'
         * kinds - the sectors' and the bank's - because both tills are pools.
         */
        out += debit.apply("- equity DividendsToHouseholds", g.getEquity().getDividendHomeThisMonth(), Scope.DOMESTIC);
        out += debit.apply("- equity DividendsAbroad", g.getEquity().getDividendAbroadThisMonth(), Scope.INCOME);
        // ...and the desk buying: from a household, cash out of the pools;
        // from the world (an emigrant on the way out included), a financial
        // outflow. A company's tender pays its holders the same two ways; the
        // desk's own tendered shares are a pool paying a pool.
        out += debit.apply("- desk SharesBoughtFromHouseholds", g.getExchange().getBoughtFromHouseholds(), Scope.DOMESTIC);
        out += debit.apply("- desk SharesBoughtFromAbroad", g.getExchange().getBoughtFromAbroad(), Scope.FINANCIAL);
        out += debit.apply("- equity BuybackToHouseholds", g.getExchange().getBuybackToHouseholds(), Scope.DOMESTIC);
        out += debit.apply("- equity BuybackAbroad", g.getExchange().getBuybackAbroad(), Scope.FINANCIAL);
        out += debit.apply("- treasury BoughtReserves",
                g.getForeignAccounts().getBoughtThisMonth(), Scope.RESERVE);
        /*
         * The bank's OWN staff and upkeep are not here, and that is deliberate
         * rather than missing. It is a COMMERCIAL building, so its jobs land in
         * the commercial sector's payroll and its upkeep in the commercial
         * sector's costs, exactly like a grocery store's - the shops are paying
         * the tellers. Wrong in an org chart, right in the books, and giving
         * the bank its own building category to fix it is a bigger change than
         * it earns today.
         */
        // What the city pays the outside world: pensions, and the upkeep of its
        // own services.
        //
        // The schools' tuition subsidy WAS a line here, described as "paid to
        // households". It is not paid to anybody: households are billed tuition
        // net of it and credited nothing, so the debit was the treasury losing
        // money to no counterparty. This audit could not catch it - households
        // are outside the pool, so an outflow to a household and an outflow to
        // nowhere look identical from in here - and it balanced only because
        // Education.getGrossCost() was charging the same phantom on the other
        // side. Both are gone. See Education.getGrossCost() for the whole of it.
        out += debit.apply("- e PensionsPaid", e.getPensionsPaid(), Scope.DOMESTIC);
        out += debit.apply("- care Upkeep", care.getUpkeep(), Scope.DOMESTIC);
        out += debit.apply("- schools Upkeep", schools.getUpkeep(), Scope.DOMESTIC);

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
        return new Result(g.getMonth(), before, after, in, out, residual, detail.toString(), foreign);
    }
}

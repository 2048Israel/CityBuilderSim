package ham.citybuildersim;

/**
 * Where the money went this month, and whether it all went somewhere.
 *
 * THE IDENTITY
 *
 * Every dollar in the game sits in one of a handful of pools, as pools()
 * reads them: the treasury (plus what a buyback between two presses has paid
 * its holders outside the pools and the next month has not yet declared,
 * since 0.7.1); every sector's cash, in Sectors.KEYS order; the
 * construction sector's order book (a build is paid for up front and earned
 * as the work is done, so the unearned part is money the builder holds and
 * has not yet booked); and the bank's cash, plus what it owes the central
 * bank's window and less what it still owes the treasury for the city's paper
 * (sold between two presses, paid for at the next settle). Everything else -
 * households, other cities, the world and, since 0.7.0, the central bank - is
 * OUTSIDE, and money only ever crosses that boundary in a known set of ways:
 * wages out, shopping and rent in, imports out, exports in, loans in,
 * repayments and interest out, pensions and subsidies out, fees and wage tax
 * in, the city's paper bought by households in and its coupons, principal and
 * buybacks paid to them out (0.7.1) - and money the central bank makes in,
 * and money paid back to it, which it destroys, out (Scope.MONEY).
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
 * Struck by Game.nextMonth() every month, always, because it costs a few
 * hundred getter reads; asserted by LongPlaytest and MoneyCheck, and its
 * central bank lines by CentralBankCheck.
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

        /**
         * The pools as this month's strike found them, and as it left them.
         *
         * Kept so that the bottom of the month can check the pools against
         * this month's close. The residual above reconciles within a month and
         * therefore cannot see money that moves in the GAP between two strikes:
         * such money is missing from the flows and already in the pools, the
         * two errors cancel, and the residual stays at $0.00. That is where
         * hot money sat for the whole life of the mechanic. See NOTHING AFTER
         * THE AUDIT MAY MOVE A POOL, the last thing Game.nextMonth() does, and
         * its assertions in LongPlaytest.audit() and MoneyCheck.
         */
        public final double[] poolsAtOpen;
        public final double[] poolsAtClose;

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

        /* ---- and the part of it the central bank made or destroyed (0.7.0) ---- */

        /** Money the central bank made and paid into the pools: advances, interest on reserves, the remittance. */
        public final double moneyIn;
        /** ...and money paid back to it, which it destroyed: repayments, and the window's and the advances' interest. */
        public final double moneyOut;

        /** What the month did to M0, as the audit saw it cross the edge. CentralBankCheck holds it to the bank's own ledger. */
        public double moneyMade()        { return moneyIn - moneyOut; }

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

        /**
         * ...and everything crossing it that is not. Households, in other
         * words - and, since 0.7.0, the central bank, which is at home: its
         * flows are domestic, and moneyIn()/moneyOut() are the part of these
         * two it made or destroyed.
         */
        public double domesticIn()  { return inflows - foreignIn(); }
        public double domesticOut() { return outflows - foreignOut(); }

        Result(int month, double before, double after, double inflows, double outflows, double residual) {
            this(month, before, after, inflows, outflows, residual, "", new double[12], null, null);
        }

        Result(int month, double before, double after, double inflows, double outflows,
               double residual, String detail, double[] foreign) {
            this(month, before, after, inflows, outflows, residual, detail, foreign, null, null);
        }

        Result(int month, double before, double after, double inflows, double outflows,
               double residual, String detail, double[] foreign,
               double[] poolsAtOpen, double[] poolsAtClose) {
            this.poolsAtOpen  = poolsAtOpen  == null ? null : poolsAtOpen.clone();
            this.poolsAtClose = poolsAtClose == null ? null : poolsAtClose.clone();
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
            this.moneyIn  = foreign.length > 10 ? foreign[10] : 0;
            this.moneyOut = foreign.length > 11 ? foreign[11] : 0;
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
     *
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
     *
     *   MONEY     - the central bank (0.7.0): money MADE and paid into the
     *               pools - an advance, interest on reserves, the remittance -
     *               and money paid back to it and DESTROYED - a repayment, the
     *               window's interest, the advances'. A boundary participant
     *               like the households, and at home like them, so it is
     *               domestic on the balance of payments; it has a scope of its
     *               own because the month's MONEY in less out is the change in
     *               M0, and CentralBankCheck holds the audit to that. See
     *               CentralBank.
     */
    public enum Scope { DOMESTIC, TRADE, INCOME, FINANCIAL, VALUATION, RESERVE, MONEY }

    /** Label, amount and scope, for one line of the month. */
    private interface Tagged {
        double apply(String label, double amount, Scope scope);
    }

    /**
     * The pools, by name: the city, every sector in the registry's order, the
     * builders' order book, the bank. SINCE THE SECTOR TEMPLATE (2026-09-11)
     * the sectors come from Sectors.KEYS rather than a literal, and the
     * "cheque in the post" pool is gone with the lag that needed it - a
     * trade is booked on both sides in the same strike now.
     */
    static final String[] POOL_NAMES = poolNames();

    private static String[] poolNames() {
        String[] names = new String[Sectors.KEYS.length + 3];
        names[0] = "city";
        for (int i = 0; i < Sectors.KEYS.length; i++) names[1 + i] = Sectors.KEYS[i].toLowerCase();
        names[Sectors.KEYS.length + 1] = "order book";
        names[Sectors.KEYS.length + 2] = "bank";
        return names;
    }

    /** The pools, in POOL_NAMES order. */
    public static double[] pools(Game g) {
        Sectors sectors = g.getSectors();
        double[] pools = new double[POOL_NAMES.length];
        int i = 0;
        /*
         * THE TREASURY, PLUS WHAT A BUYBACK HAS PAID OUT OF THE POOLS AND THE
         * MONTH HAS NOT YET DECLARED (0.7.1): the households' share, the
         * dollar holders' and the central bank's, paid between two presses
         * and seen leaving in the next month's window - the treasury's end of
         * the shape the bank's unsettled paper has below. See
         * Game.repurchaseDebt().
         */
        pools[i++] = g.getCash() + g.getBuybackUnsettled();
        for (Sector s : sectors.all()) pools[i++] = s.getCash();
        pools[i++] = sectors.construction().getOrderBookForAudit();
        /*
         * THE BANK, since 2026-09-07. It holds the city's lending, so a loan
         * to a sector or to the treasury is an internal transfer that cancels
         * rather than money arriving from outside. Households are still
         * OUTSIDE, so what the bank lends a family is a real outflow.
         *
         * LESS WHAT IT OWES FOR THE CITY'S PAPER, since 2026-09-21, when it
         * started paying for it. The city sells its bonds between two
         * presses and the treasury has the cash at once; the bank pays at the
         * settle at the bottom of the next month. In between, the money is
         * the treasury's and the bank owes it - so its pool is its cash less
         * getCityPaperUnsettled(), the same timing difference the builders'
         * order book is carried for above. Counted this way an issue moves
         * nothing between the pools and the settle moves nothing either;
         * counted as bare cash, the settle would read as the bank's money
         * vanishing inside a month whose window opened after the treasury
         * had already been paid.
         */
        /*
         * ...AND PLUS WHAT IT OWES THE CENTRAL BANK'S WINDOW, since 0.7.0. The
         * bank's cash is its NET position - a negative balance is borrowing,
         * see Bank's FUNDING SIDE - so the window's loan is already inside it
         * the day the bank lends past its deposits. The central bank books
         * the advance once a month at the settle, and it is money made: the
         * pool is the cash the bank would hold had the advance been paid in,
         * so the advance moves the pool by exactly what the audit declares as
         * "+ centralbank AdvancedToBank", and a repayment the other way.
         */
        pools[i++] = g.getBank().getCash() + g.getCentralBank().getAdvancesToBank()
                - g.getCityPaperUnsettled();
        return pools;
    }

    /**
     * Every dollar in the pools: the city's, its businesses', the builders'
     * order book, and the bank's - plus what it owes the window, less what it
     * owes for the city's paper.
     */
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
        double[] foreign = new double[12];
        Tagged credit = (label, amount, scope) -> {
            note.accept(label, amount);
            switch (scope) {
                case TRADE     -> foreign[0] += amount;
                case INCOME    -> foreign[2] += amount;
                case FINANCIAL -> foreign[4] += amount;
                case VALUATION -> foreign[6] += amount;
                case RESERVE   -> foreign[8] += amount;
                case MONEY     -> foreign[10] += amount;
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
                case MONEY     -> foreign[11] += amount;
                default        -> { }
            }
            return amount;
        };
        EconomyManager e = g.getEconomyManager();
        Sectors sectors = g.getSectors();
        UtilitiesHandler utilities = g.getServicesManager().getUtilitiesHandler();
        Healthcare care = g.getHealthcare();
        Education schools = g.getEducation();

        double in = 0;
        // Households: what they spend in the shops and on rent, and what the
        // city takes off their pay and charges them at the door. Every
        // sector's sales to households, as its statement booked them.
        for (Sector s : sectors.all()) {
            in += credit.apply("+ " + s.key() + " SalesToHouseholds", s.statement().salesToHouseholds, Scope.DOMESTIC);
        }
        in += credit.apply("+ e WageTax", e.getWageTax(), Scope.DOMESTIC);
        in += credit.apply("+ e Contributions", e.getContributions(), Scope.DOMESTIC);
        // The EI premium, off the same payslips, and the student loans the
        // graduates repaid - households are outside the pools (2026-09-11).
        in += credit.apply("+ e EiPremiums", e.getEiPremiums(), Scope.DOMESTIC);
        // ...and the health premium off the same payslips (2026-09-19): a
        // household -> treasury transfer, like the EI premium beside it.
        in += credit.apply("+ e HealthPremiums", e.getHealthPremiums(), Scope.DOMESTIC);
        in += credit.apply("+ treasury StudentLoansRepaid", g.getStudentLoansRepaid(), Scope.DOMESTIC);
        // ...and the interest the graduates paid on them (2026-09-21): a
        // household -> treasury flow like the principal beside it, and a
        // budget line where the principal is not.
        in += credit.apply("+ treasury StudentLoanInterest", g.getStudentLoanInterest(), Scope.DOMESTIC);
        in += credit.apply("+ care Fees", care.getFees(), Scope.DOMESTIC);
        in += credit.apply("+ schools Fees", schools.getFees(), Scope.DOMESTIC);
        /*
         * ...AND THE FARE (2026-09-16), which belongs beside those two and was
         * missing from this list since the day transit was built. The city
         * banked it inside getTotalIncome() and no household was ever debited
         * for it, so the pools gained money the flows could not explain - and
         * nothing caught it for the plainest possible reason: until the advisor
         * learned to buy a bus, no city in any harness ever had a rider. See
         * HouseholdAccounts.fares.
         */
        in += credit.apply("+ e TransitFares",
                e.getTransitFares(), Scope.DOMESTIC);
        // The world: every sector's exports, at the price the statement sold them for.
        for (Sector s : sectors.all()) {
            in += credit.apply("+ " + s.key() + " Exports", s.statement().exports, Scope.TRADE);
        }
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
         * THE HOUSEHOLDS BUY THE CITY'S PAPER (0.7.1), at the settle of an
         * issue: their cash is what the bank did not have to pay the treasury
         * for, so it arrives in the bank's pool from outside the pools, like a
         * repayment. See Game's THE HOUSEHOLDS TAKE THEIR SHARE.
         */
        in += credit.apply("+ households BoughtCityPaper", g.getHouseholdsBoughtPaper(), Scope.DOMESTIC);
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
         * THE CARRY TRADE COMING HOME. A foreigner who borrowed local and took
         * it abroad has bought the currency back to repay - money arriving, and
         * the squeeze that makes an unwind hurt. Financial, like hot money: it
         * is a claim being settled, not a good being sold.
         */
        in += credit.apply("+ bank CarryRepaid", g.getBank().getCarryRepaid(), Scope.FINANCIAL);

        /*
         * ...and what they pay for the privilege, which is earned abroad and so
         * is INCOME rather than domestic interest. It is inside interestEarned
         * as well, because it is interest - so the domestic interest line below
         * nets it out, exactly as it already nets out the bank's interest on
         * reserves (placed abroad, and netted out the same way, before 0.7.0).
         */
        in += credit.apply("+ bank CarryInterest", g.getBank().getCarryInterest(), Scope.INCOME);
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
        /*
         * THE CARS THE HOUSEHOLDS BOUGHT FROM ABROAD (2026-09-16).
         *
         * The first thing a household has ever imported. The domestic half of
         * the same purchase needs no line here - it is already in some
         * sector's SalesToHouseholds above - but this half crosses the
         * country's edge and is booked against no sector's statement, because
         * Markets.draw() has nobody to book it to when the buyer is not a
         * sector.
         *
         * THE PAIR IS THE ONE `SavedFromAbroad` USES, two lines up, and for
         * the identical reason: households are outside the audited POOLS, so
         * money going from a household to the world moves nothing the pool
         * identity can see and has to be declared on both sides to keep it. Of
         * the two only the debit carries a scope the balance of payments
         * reads, and TRADE is the right one - it is a good, arriving on a
         * ship. See getHouseholdCarImports().
         */
        in += credit.apply("+ households CarImportsFunded",
                g.getHouseholdCarImports(), Scope.DOMESTIC);
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
         * A failed bank's creditors absorbing the shortfall. They were the
         * wholesale funders abroad until 0.7.0, so the money the city keeps
         * and will not repay arrives here. It still does, though the bank's
         * wholesale lender is the central bank's window now and the window is
         * repaid out of this at the next settle - who absorbs a failed bank
         * is Jerus's open question. See Bank.resolveIfFailed().
         */
        in += credit.apply("+ bank ResolutionLoss", g.getBank().getResolutionLossThisMonth(), Scope.VALUATION);
        /*
         * A bankrupt sector's creditors absorbing its overdraft, the same
         * way. The bills were paid with money the sector did not have; the
         * restructure admits it and somebody outside eats it. See
         * EconomyManager.settleInsolvency().
         */
        in += credit.apply("+ sectors OverdraftForgiven", g.getEconomyManager().getOverdraftForgiven(), Scope.VALUATION);

        // What the households paid it: everything earned less the internal
        // transfers and less its interest on reserves, which is declared on
        // its own line below as money the central bank made.
        in += credit.apply("+ bank InterestEarned", g.getBank().getInterestEarned()
                - g.getBank().getInternalInterest() - g.getBank().getPlacementIncome()
                - g.getBank().getCarryInterest(), Scope.DOMESTIC);

        /*
         * DOLLARS BORROWED ABROAD, which is the one kind of city borrowing that
         * crosses this boundary.
         *
         * The city's own bonds do not appear here at all, and should not: the
         * bank buys them, so what the treasury raised goes to bank.lend() at
         * the next settle - carried against the bank's pool until then, see
         * pools() - and the money never leaves the audited pools. Foreign
         * paper is bought by somebody the city has no other relationship
         * with, so the cash really does arrive from outside - and it is a
         * FINANCIAL flow, not a trade one, because nothing was sold to earn it.
         */
        in += credit.apply("+ city ForeignDebtRaised",
                g.getForeignDebtRaisedThisMonth(), Scope.FINANCIAL);

        /*
         * THE CENTRAL BANK, since 0.7.0: every dollar it made this month and
         * paid into a pool. Read off its own counters, which move only in its
         * own methods, so this list and its ledger are one set of figures and
         * the audit is what holds the other end - the bank's pool and the
         * treasury's cash - to them.
         */
        CentralBank cb = g.getCentralBank();
        in += credit.apply("+ centralbank AdvancedToBank", cb.getAdvancedToBank(), Scope.MONEY);
        in += credit.apply("+ centralbank AdvancedToTreasury", cb.getAdvancedToTreasury(), Scope.MONEY);
        in += credit.apply("+ centralbank Remittance", cb.getRemitted(), Scope.MONEY);
        // ...and what it paid the bank for the city's paper it bought, in
        // money made for it: the holdings dial (0.7.1).
        in += credit.apply("+ centralbank BoughtPaper", cb.getBoughtPaper(), Scope.MONEY);

        double out = 0;
        // Payrolls, as each statement charged them.
        for (Sector s : sectors.all()) {
            out += debit.apply("- " + s.key() + " Payroll", s.statement().payroll, Scope.DOMESTIC);
        }
        /*
         * The bank's tellers. Wages leave the audited system whoever pays
         * them; declared separately so the two can be read apart.
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
         *   balance of payments, where the wholesale funding the bank raised
         *   abroad also sat until the central bank's window replaced it in
         *   0.7.0. Declared here since 2026-09-07; before that it was folded
         *   into the sectors' share, which paid foreign savers' interest to
         *   domestic businesses and - in any month where no business had a
         *   positive balance - paid it to nobody at all and destroyed it. See
         *   Bank.fundToCover().
         */
        out += debit.apply("- bank DepositInterest (households)", g.getBank().getDepositInterestToHouseholds(), Scope.DOMESTIC);
        out += debit.apply("- bank DepositInterest (abroad)", g.getBank().getDepositInterestToForeign(), Scope.INCOME);
        out += debit.apply("- utilities UtilityPayroll", utilities.getUtilityPayroll(), Scope.DOMESTIC);
        out += debit.apply("- care Payroll", care.getPayroll(), Scope.DOMESTIC);
        out += debit.apply("- schools Payroll", schools.getPayroll(), Scope.DOMESTIC);
        out += debit.apply("- safety Payroll", g.getCrime().getPayroll(), Scope.DOMESTIC);
        // Imports: every sector's purchases from the world, as its statement booked them.
        for (Sector s : sectors.all()) {
            out += debit.apply("- " + s.key() + " Imports", s.statement().imports, Scope.TRADE);
        }
        // Lending to a family crosses the city's edge; lending to a sector or
        // to the treasury no longer does - see the pools above.
        out += debit.apply("- bank LentToHouseholds", g.getBank().getLentToHouseholds(), Scope.DOMESTIC);
        /*
         * WHAT THE HOUSEHOLDS WERE PAID FOR AND ON THE CITY'S PAPER (0.7.1).
         * Every one of these leaves the pools for a household: the desk
         * buying their paper (the waterfall, the spread gone, a household
         * leaving the city), the coupon and the principal on their share, and
         * their share of a bond bought back between the presses, declared the
         * month after the button was pressed. See Game's THE HOLDERS ARE PAID.
         */
        out += debit.apply("- desk PaperBoughtFromHouseholds", g.getBank().getPaperBoughtFromHouseholds(), Scope.DOMESTIC);
        out += debit.apply("- city CouponsToHouseholds", g.getCouponsToHouseholds(), Scope.DOMESTIC);
        out += debit.apply("- city PrincipalToHouseholds", g.getPrincipalToHouseholds(), Scope.DOMESTIC);
        out += debit.apply("- city BuybackToHouseholds", g.getBuybackToHouseholds(), Scope.DOMESTIC);
        /*
         * WHAT THE BANK PAYS FOR MONEY IT DID NOT HAVE, since 2026-09-07.
         *
         * A negative cash position IS borrowing, and the part of it the city's
         * own deposits do not cover was funded abroad until 0.7.0 - declared
         * as foreign income paid, and from 2026-09-07 split by whose money it
         * was, because at a book of $156B that one line was $592,109 a month
         * against $234,348 of exports and ran the currency to its ceiling.
         *
         * IT IS THE CENTRAL BANK'S WINDOW NOW, so the coupon goes to the
         * central bank and is money destroyed; the advance itself moves the
         * bank's pool (see pools()) and is declared with the central bank's
         * other lines above. Read off the central bank's counter, which Game
         * books from the bank's own figure in the same breath.
         */
        out += debit.apply("- centralbank WindowInterest",
                cb.getWindowInterest(), Scope.MONEY);
        // ...and the mirror: what its reserves earned at the central bank, at
        // the policy rate, in money made to pay it. See Bank.placementIncome.
        // Placed abroad at the world's rate, and declared as foreign income,
        // until 0.7.0.
        in += credit.apply("+ centralbank InterestOnReserves",
                cb.getInterestOnReserves(), Scope.MONEY);

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
        // ...and a dollar bond bought back between the presses (0.7.1): the
        // price leaves the country, a financial outflow like the principal.
        // It used to leave the treasury for nowhere.
        out += debit.apply("- city BuybackAbroad", g.getBuybackAbroad(), Scope.FINANCIAL);
        // ...and buying them, which is local money leaving to pay for foreign.
        // RESERVE for the same reason: it built nothing, because takeMonth()
        // took it off the stock again the moment the month closed.
        out += debit.apply("- bank HotMoneyOut",
                g.getBank().getHotMoneyOut(), Scope.FINANCIAL);

        /*
         * ...and the carry trade going out. The bank hands over local currency
         * and the borrower sells it for dollars, so the money genuinely leaves.
         * This is the door the surplus has never had.
         */
        out += debit.apply("- bank CarryLent",
                g.getBank().getCarryLent(), Scope.FINANCIAL);
        out += debit.apply("- sectors InvestedAbroad", g.getOutwardInvestment().getInvestedAbroadThisMonth(), Scope.FINANCIAL);
        out += debit.apply("- sectors ForeignInterestReinvested", g.getOutwardInvestment().getInterestThisMonth(), Scope.FINANCIAL);
        // The households' three, the other way round. See the credits.
        out += debit.apply("- households InvestedAbroad", g.getHouseholdBalance().getSentAbroad(), Scope.FINANCIAL);
        // ...and the cars they bought from the world. See the credit's note.
        out += debit.apply("- households CarImports",
                g.getHouseholdCarImports(), Scope.TRADE);
        out += debit.apply("- households BroughtHomeSaved", g.getHouseholdBalance().getBroughtHome(), Scope.DOMESTIC);
        /*
         * THE COUPON IS BANKED AT HOME NOW, AND STILL NEEDS ITS OTHER LEG
         * (2026-09-17). It used to be reinvested abroad; it is paid home - see
         * HouseholdBalance.investAbroad() - and the first attempt at this
         * DELETED the debit, on the reasoning that the money no longer leaves.
         * Thirteen harnesses went red, five of them on the conservation
         * identity itself.
         *
         * WHY THAT WAS WRONG, and it is the thing to remember about this file:
         * HOUSEHOLD SAVINGS ARE NOT ONE OF THE AUDITED POOLS. Money moving
         * from abroad into a household's bank balance moves nothing the pool
         * identity can see, so it has to be declared on BOTH sides or the
         * credit stands alone and the audit reports money appearing. Every
         * household foreign flow above is a pair for exactly this reason.
         *
         * WHAT CHANGED IS THE SCOPE, not the existence. The balance of payments
         * reads one side of each pair: the credit is INCOME, because a coupon
         * earned abroad is a current-account receipt either way. The debit used
         * to be FINANCIAL - money earned abroad and left there is an outward
         * investment - and is DOMESTIC now, which is the same treatment
         * "- households BroughtHomeSaved" gets two lines up, and for the same
         * reason: the money is at home.
         *
         * The SECTORS' coupon is untouched and still rolls, so its own pair
         * stands above.
         */
        out += debit.apply("- households ForeignInterestBanked",
                g.getHouseholdBalance().getForeignInterest(), Scope.DOMESTIC);
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
        // ...EI to the out of work, grants and loans to the students.
        out += debit.apply("- e EiBenefits", e.getEiBenefits(), Scope.DOMESTIC);
        out += debit.apply("- e StudentGrants", e.getStudentGrants(), Scope.DOMESTIC);
        out += debit.apply("- treasury StudentLoansLent", g.getStudentLoansLent(), Scope.DOMESTIC);
        out += debit.apply("- care Upkeep", care.getUpkeep(), Scope.DOMESTIC);
        out += debit.apply("- schools Upkeep", schools.getUpkeep(), Scope.DOMESTIC);
        out += debit.apply("- safety Upkeep", g.getCrime().getUpkeep(), Scope.DOMESTIC);
        // What thieves took from the tills, to the offenders' households -
        // out of the pools like a wage. What they took from households never
        // entered them. See Crime.
        out += debit.apply("- crime StolenFromBusinesses", g.getCrime().getStolenFromBusinesses(), Scope.DOMESTIC);

        // ...and what went back to the central bank this month, which it
        // destroyed. See the credits.
        out += debit.apply("- centralbank RepaidByBank", cb.getRepaidByBank(), Scope.MONEY);
        out += debit.apply("- centralbank RepaidByTreasury", cb.getRepaidByTreasury(), Scope.MONEY);
        out += debit.apply("- centralbank AdvancesInterest", cb.getAdvancesInterest(), Scope.MONEY);
        // The holdings (0.7.1): paper it sold the bank, the coupons and
        // principal the treasury paid it on its share, and its share of a
        // buyback, settled this month - all destroyed.
        out += debit.apply("- centralbank SoldPaper", cb.getSoldPaper(), Scope.MONEY);
        out += debit.apply("- centralbank PaperCoupons", cb.getPaperCoupons(), Scope.MONEY);
        out += debit.apply("- centralbank PaperRedeemed", cb.getPaperRedeemed(), Scope.MONEY);
        out += debit.apply("- centralbank BoughtBack", cb.getBoughtBack(), Scope.MONEY);

        // Suspect internal pairs, for the detail only. A flow that should
        // cancel and does not is where a residual lives.
        note.accept("chk profit tax collected (city)", e.getBusinessTax() + e.getIndustrialTax());
        note.accept("chk VAT collected (city)", e.getSalesTax());
        note.accept("chk property tax collected", e.getTotalPropertyTax());
        double localSales = 0, localPurchases = 0, bills = 0;
        for (Sector s : sectors.all()) {
            localSales += s.statement().localSales - s.statement().salesToHouseholds;
            localPurchases += s.statement().localPurchases;
            bills += s.statement().electricity + s.statement().water;
        }
        note.accept("chk sectors sold to sectors", localSales);
        note.accept("chk sectors bought from sectors", localPurchases);
        note.accept("chk utility revenue", utilities.getUtilityRevenue());
        note.accept("chk sector utility bills", bills);
        note.accept("chk construction revenue banked", sectors.construction().statement().revenue);
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
        return new Result(g.getMonth(), before, after, in, out, residual, detail.toString(), foreign,
                poolsBefore, poolsAfter);
    }
}

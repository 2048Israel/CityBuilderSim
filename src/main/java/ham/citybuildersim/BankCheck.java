package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The commercial bank, and the families it discharges.
 *
 * WHAT IS ACTUALLY BEING ASKED
 *
 * Before this went in there were three lenders in the game and none of them was
 * anybody: a sector borrowed from BusinessDebtManager, the city sold bonds to a
 * market, and a family short of the shop borrowed from nothing at all. Money
 * arrived from outside the city and interest disappeared out of it.
 *
 * So the questions here are not "does the class compile". They are:
 *
 *   1. Does the price of credit actually depend on the bank? A premium that no
 *      borrower pays is a number on a screen.
 *   2. Does the money still add up, now that four flows that used to run to and
 *      from nowhere run between two pools inside the city?
 *   3. Does a family that cannot carry its debt get discharged - and does the
 *      bank, not thin air, eat the loss?
 *
 * Each of those is measured by CAUSING the condition, never by finding a city
 * that happens to be in it.
 */
public class BankCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-58s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) {
            fails++;
            out.printf("%-58s FAIL  %,.4f != %,.4f%n", label, actual, expected);
        } else {
            out.printf("%-58s OK%n", label);
        }
    }

    static BuildingsTemplate template(Game game, String name) {
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException("no template named " + name);
    }

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        /* ================= 1. the two limits ================= */
        out.println("--- what the bank can carry ---");

        /*
         * THE TWO LIMITS ARE CAPITAL AND FUNDING, since Jerus asked for this to
         * be "what banks do". The invented BOOK_PER_BRANCH ceiling is gone.
         */

        Bank rich = new Bank();
        // Plenty of capital, one branch: what it can GATHER binds.
        rich.refresh(1, 10_000_000, 0, 0, 0, 0);
        rich.injectCapital(10_000_000);
        close("a well capitalised bank is limited by what it can gather",
                rich.capacity(), Bank.DEPOSITS_PER_BRANCH * Bank.LEVERAGE, 1e-9);

        Bank thin = new Bank();
        // Branches everywhere, a city with savings, and no capital at all.
        thin.refresh(50, 10_000_000, 0, 0, 0, 0);
        close("a bank with no capital can lend nothing, however many branches",
                thin.capacity(), 0, 1e-9);

        thin.injectCapital(8_000);
        close("...and capital is what lets it lend, at the regulator's ratio",
                thin.capacity(), 8_000 / Bank.CAPITAL_RATIO, 1e-9);

        assertTrue("...so the two limits are genuinely different questions",
                rich.capacity() != thin.capacity());

        assertTrue("a bank that has lost more than it owns is insolvent",
                new Bank() {{ refresh(1, 100, 0, 0, 0, 0); lend(500); }}.isInsolvent());

        /* ============ 1b. WHAT TO PAY SAVERS IS A DECISION ============

           Jerus, 2026-09-09: "make it variable, the bank will not give out more
           than it needs to stay net 0, but if its already making profit it will
           do some forecast to see what new deposits would come in if it
           increased the deposit interest rate."

           Three rules, and each is CAUSED here rather than hoped for in a city.
           A run-based fixture cannot reach the third one at all: this economy's
           bank is almost always either marginGone (capital binds, so more deposits
           are worthless) or under-lent (nothing to fund, so more deposits are
           worthless), and a 600-month probe fired it zero times for exactly
           those two reasons. That is the model being right and a whole-city
           fixture being the wrong instrument - so the instrument is this.
           ================================================================= */
        System.out.println("\n--- what to pay savers is a decision ---");

        /* ---- rule 2 first, because it bounds the other two ---- */
        Bank marginGone = new Bank();
        marginGone.refresh(1, 1_000_000, 0, 0, 0, 0);
        marginGone.injectCapital(1_000_000);
        marginGone.startMonth();
        marginGone.lend(500_000);            // borrowed to lend: wholesale funding to pay for
        marginGone.fundToCover(.10);
        assertTrue("a bank whose margin is gone pays its savers nothing",
                marginGone.depositInterest() <= 1e-9);

        /* ---- rule 1: a share of the income, and only of the income ---- */
        Bank earner = new Bank();
        earner.refresh(1, 1_000_000, 0, 0, 0, 0);
        earner.injectCapital(1_000_000);
        earner.startMonth();
        earner.takeInterest(1_000);
        earner.fundToCover(.10);
        close("...and one that earned pays the baseline share of what it earned",
                earner.depositInterest(), 1_000 * Bank.DEPOSIT_PASS_THROUGH, 1e-9);
        assertTrue("...which is a rate on the whole deposit book, derived not set",
                earner.depositRate() > 0);
        close("...and that rate is the payout over the deposits",
                earner.depositRate(),
                earner.depositInterest() / earner.getDeposits() * 12, 1e-9);

        /* ---- rule 3: and higher, when buying deposits actually pays ---- */
        /*
         * CAUSED, which takes all three conditions at once: in profit, LENT OUT
         * so another dollar would be used, and DEPOSITS binding rather than
         * capital. Capital is made abundant and the book is pushed past the
         * comfortable strain, so the only thing between this bank and more
         * lending is funding.
         */
        double savings = 10_000_000;
        double branchesEnough = savings / Bank.DEPOSITS_PER_BRANCH;   // it can reach all of it
        double wayPastFunding = savings * Bank.LEVERAGE * 2;          // and has lent twice that
        double earned = 5_000;
        double baselinePay = earned * Bank.DEPOSIT_PASS_THROUGH;

        Bank hungry = new Bank();
        // The BOOK goes in through refresh - lend() only moves cash, and the
        // weighted book is what strain() is measured on.
        hungry.refresh(branchesEnough, savings, 0, wayPastFunding, 0, 0);
        hungry.injectCapital(500_000_000);        // capital is not the problem
        hungry.startMonth();
        hungry.takeInterest(earned);              // and it is in profit
        hungry.lend(wayPastFunding);              // and the cash really went out
        hungry.setDepositMarket(rate -> rate * 20_000_000);          // money answers the price
        hungry.fundToCover(.10);

        System.out.printf("   lent out and short of funding: pays %,.1f against a baseline of %,.1f"
                + " (strain %.1f)%n",
                hungry.depositInterest(), baselinePay, hungry.strain());
        assertTrue("fixture: it really is lent out", hungry.strain() > 1);
        assertTrue("a bank that is lent out bids above its baseline for deposits",
                hungry.depositInterest() > baselinePay + 1e-9);
        assertTrue("...and never past the interest it earned",
                hungry.depositInterest() <= earned + 1e-9);

        /*
         * ...AND IT DOES NOT BID WHEN THE MONEY WOULD BE NO USE. Same bank,
         * same offer, but capital is the binding limit now - so another dollar
         * of deposits cannot be lent and is not worth paying for. This is the
         * same question wantsBranch() asks before putting up a counter.
         */
        /*
         * Sized so that CAPITAL is the only thing wrong with it. The cash has
         * to actually go out - a book with no borrowing behind it counts as
         * equity and this bank would look better capitalised than the one above
         * - but it must stay inside what the deposits can fund, or the wholesale
         * bill eats the margin and rule 2 answers before rule 3 is ever asked.
         */
        double insideItsDeposits = savings / 2;
        Bank capped = new Bank();
        capped.refresh(branchesEnough, savings, 0, insideItsDeposits, 0, 0);
        capped.injectCapital(1_000);              // almost none
        capped.startMonth();
        capped.takeInterest(earned);
        capped.lend(insideItsDeposits);
        capped.setDepositMarket(rate -> rate * 20_000_000);
        capped.fundToCover(.10);
        System.out.printf("   capital-bound: capacity %,.0f against a book of %,.0f%n",
                capped.capacity(), capped.getWeightedBook());
        assertTrue("fixture: capital really is what binds it", capped.capacity()
                < capped.depositsGathered() * Bank.LEVERAGE);
        close("a bank short of CAPITAL does not bid for deposits it may not lend",
                capped.depositInterest(), baselinePay, 1e-9);

        /* ---------------- ...and it does not open counters either ----------------
         *
         * WHY THIS IS ASSERTED ON A CLOSED MONTH AND NOT A LIVE ONE, which is
         * the whole finding and cost two silent iterations to learn.
         *
         * Opening a branch is an equity injection - see capacityWith() - so
         * while CAPITAL binds, one more branch always raises capacity and
         * bookAnotherBranchWouldCarry() is always positive. A bank losing money
         * is short of capital by definition, so it always wanted another
         * branch, and every branch brought a wage bill it already could not
         * cover: 1,049 counters by month 4,000, carrying $10,553 of book apiece
         * where one branch's capital supports $400,000 of it.
         *
         * branchWouldPayForItself() closes that, and it has to read LAST
         * month's figures, because startMonth() zeroes every flow and the
         * advisor asks between the two. Written against the live fields it is
         * inert - a four-thousand-month playtest came back byte-identical
         * twice, which is what a test that never fires looks like. So this
         * fixture runs a whole month and CLOSES it, exactly as the game does,
         * or it would assert nothing at all.
         */
        Bank overbuilt = new Bank();
        double manyBranches = branchesEnough * 40;      // far more counters than book
        overbuilt.refresh(manyBranches, savings, 0, insideItsDeposits / 50, 0, 0);
        overbuilt.injectCapital(200_000);
        overbuilt.startMonth();
        overbuilt.takeInterest(earned / 50);
        overbuilt.payRunning(earned, 0);                // a wage bill it cannot cover
        overbuilt.closeMonth();

        System.out.printf("   %,.0f branches carrying %,.0f of book"
                + " against %,.0f of wages%n",
                overbuilt.getBranches(), overbuilt.getBook(), earned);

        assertTrue("fixture: the month really did close on a loss",
                overbuilt.getProfitLastMonth() < 0);
        assertTrue("a bank whose counters do not pay for themselves wants no more",
                !overbuilt.branchWouldPayForItself());
        assertTrue("...so it does not ask for one", !overbuilt.wantsBranch());

        /*
         * ...and the same bank with a book worth banking says yes. Nothing
         * changes but how much is lent through the same counters, which is what
         * the test is supposed to be about.
         */
        Bank worthIt = new Bank();
        worthIt.refresh(branchesEnough, savings, 0, insideItsDeposits, 0, 0);
        worthIt.injectCapital(200_000);
        worthIt.startMonth();
        worthIt.takeInterest(earned);
        worthIt.payRunning(earned / 50, 0);
        worthIt.closeMonth();

        assertTrue("...where a counter carrying a real book is worth opening",
                worthIt.branchWouldPayForItself());

        /* ================= 2. the price of strain ================= */
        out.println("\n--- and what it charges for being past it ---");

        /*
         * Capitalised well past what it has gathered, so the FUNDING limit binds
         * and the strain below is a fact about the book rather than about the
         * capital - which moves when the book moves and would make this circular.
         */
        Bank easy = new Bank();
        int branches = 4;
        double deposits = branches * Bank.DEPOSITS_PER_BRANCH;
        double room = deposits * Bank.LEVERAGE;
        easy.injectCapital(room * Bank.CAPITAL_RATIO * 20);

        easy.refresh(branches, deposits, 0, room * .5, 0, 0);
        close("a bank lending half of what it can charges nothing",
                easy.ratePremium(), 0, 1e-12);

        easy.refresh(branches, deposits, 0, room * (Bank.EASY_STRAIN + .0001), 0, 0);
        assertTrue("...and starts charging the moment it is past EASY_STRAIN",
                easy.ratePremium() > 0 && easy.ratePremium() < Bank.MAX_STRAIN_PREMIUM);

        easy.refresh(branches, deposits, 0, room * Bank.HARD_STRAIN * 1.5, 0, 0);
        close("a bank lending well past itself charges the cap",
                easy.ratePremium(), Bank.MAX_STRAIN_PREMIUM, 1e-12);

        // The premium is monotone across the whole ramp, checked by walking it
        // rather than by sampling the two ends - a ramp that dips in the middle
        // passes an ends-only test.
        boolean rising = true;
        double previous = -1;
        for (double s = 0; s <= 2.0; s += .05) {
            easy.refresh(branches, deposits, 0, room * s, 0, 0);
            double p = easy.ratePremium();
            if (p < previous - 1e-12) rising = false;
            previous = p;
        }
        assertTrue("the premium never falls as the book grows", rising);

        Bank none = new Bank();
        none.injectCapital(5_000_000);
        none.refresh(0, 5_000_000, 0, 0, 0, 0);
        close("a city with no bank and no debt is charged nothing",
                none.ratePremium(), 0, 1e-12);
        none.refresh(0, 5_000_000, 0, 100, 0, 0);
        close("...but the moment it borrows, it pays the full premium",
                none.ratePremium(), Bank.MAX_STRAIN_PREMIUM, 1e-12);

        /* ================= 3. the premium reaches the borrower ================= */
        out.println("\n--- and the borrower actually pays it ---");

        DebtManager unbanked = new DebtManager();
        DebtManager banked = new DebtManager();
        double asked = 40_000;
        double clean = banked.quoteRate(asked);
        unbanked.setBankPremium(Bank.MAX_STRAIN_PREMIUM);
        double dear = unbanked.quoteRate(asked);
        out.printf("   the same $%,.0fk quotes %.2f%% with a bank, %.2f%% without%n",
                asked, clean * 100, dear * 100);
        close("the premium lands on the quote, point for point",
                dear - clean, Bank.MAX_STRAIN_PREMIUM, 1e-9);

        /*
         * THE QUOTE AND THE BOOKING ARE THE SAME CALCULATION.
         *
         * This is the bug this file exists to keep out. The premium was first
         * added to getRate() and not to quoteRate(), so the city was quoted the
         * clean rate, booked at it, and then serviced the debt at the dear one -
         * RestructureCheck found $567,131 of printed money over eight round
         * trips before it was moved inside priceAt().
         */
        unbanked.setBankPremium(.07);
        unbanked.updateInterest();     // the standing rate is a stored field
        double quoted = unbanked.quoteRate(0);
        close("the standing rate is the quote for nothing more",
                unbanked.getRate(), quoted, 1e-12);

        /* ================= 4. a real city, and its money ================= */
        out.println("\n--- a bank in a city that has one ---");

        Path root = Files.createTempDirectory("bankcheck");
        /*
         * THE CURRENCY IS HELD STILL THROUGHOUT THIS FILE.
         *
         * Every fixture here asks a question about banking - what capital
         * supports, what a branch is worth, whether the books balance. None of
         * them is a question about the exchange rate, and a drifting rate moves
         * every world price in the city underneath them. Pinned after each
         * run(), so what is measured is the bank.
         */
        Game city = new Game(new GameFiles(root.resolve("data"), root.resolve("no-legacy")));
        System.setOut(quiet);
        try {
            city.run();
            city.getForeignAccounts().pinRate(1.0);
            city.buildStack(template(city, "House"), 300, false);
            city.buildStack(template(city, "Convenience Store"), 6, false);
            city.buildStack(template(city, "Textile Mill"), 2, false);
            city.buildStack(template(city, "Construction Depot"), 4, false);
            city.buildStack(template(city, "Coal Power Plant"), 1, false);
            city.buildStack(template(city, "Commercial Bank"), 1, false);
            /*
             * SEVENTY-TWO MONTHS, NOT FORTY-EIGHT, since 2026-09-07. The order
             * always went through; what changed is how long it waits. Rent
             * became a market, housing became worth supplying, and
             * BusinessInvestment now puts far more into the construction queue -
             * so the branch that used to be finished by month 48 comes out
             * between 48 and 60. Nothing about the bank moved; the queue in
             * front of it got longer.
             */
            city.simulateMonths(72);
        } finally {
            System.setOut(out);
        }

        Bank live = city.getBank();
        out.printf("   %,.0f branch(es), deposits $%,.0fk, book $%,.0fk, cash $%,.0fk%n",
                live.getBranches(), live.getDeposits(), live.getBook(), live.getCash());
        out.printf("   sectors owe $%,.0fk, the city owes $%,.0fk, families owe $%,.0fk%n",
                live.getSectorBook(), live.getCityBook(), live.getHouseholdBook());

        assertTrue("the fixture actually built a bank", live.getBranches() >= 1);
        assertTrue("...and somebody in it has actually borrowed", live.getBook() > 0);

        close("the bank's book is every loan in the city, and nothing else",
                live.getBook(),
                live.getSectorBook() + live.getCityBook() + live.getHouseholdBook(), 1e-9);

        close("the sector book IS the business lender's principal",
                live.getSectorBook(),
                city.getEconomyManager().getBusinessDebtManager().getAllPrincipal(), 1e-6);
        close("the city book IS the treasury's principal",
                live.getCityBook(), city.getDebtManager().getAllPrincipal(), 1e-6);
        close("the household book IS what the families owe",
                live.getHouseholdBook(), city.getHouseholdBalance().bookOwed(), 1e-6);

        /*
         * MONEY. The audit is struck every month by the game itself, so this is
         * not a second calculation - it is a check that the pool this feature
         * added did not open a hole in the identity that already existed.
         */
        MoneyAudit.Result audit = city.getLastMoneyAudit();
        out.printf("   %s%n", audit);
        assertTrue("the bank is one of the audited pools",
                java.util.Arrays.asList(MoneyAudit.POOL_NAMES).contains("bank"));
        assertTrue("...and the month still balances to the cent",
                Math.abs(audit.residual) < .01);

        /* ================= 5. the save carries the bank's cash ================= */
        out.println("\n--- and it survives a reload ---");

        double cashBefore = live.getCash();
        double pooledBefore = MoneyAudit.pooled(city);
        assertTrue("the fixture's bank is actually holding something",
                Math.abs(cashBefore) > 1);

        System.setOut(quiet);
        city.saveGame(1, "the bank city");
        Game reloaded = new Game(new GameFiles(root.resolve("data"), root.resolve("no-legacy")));
        try {
            reloaded.run();
            reloaded.getForeignAccounts().pinRate(1.0);
            reloaded.loadGameSave(1);
        } finally {
            System.setOut(out);
        }

        close("the bank's cash reloads exactly", reloaded.getBank().getCash(), cashBefore, .005);
        close("...so the city's whole money supply does too",
                MoneyAudit.pooled(reloaded), pooledBefore, .05);

        /*
         * AND THE PRICE OF CREDIT RELOADS WITH IT.
         *
         * The live path sets the premium at the top of every month, which the
         * load path never reaches - so a player could save at a strained bank,
         * reload, and be quoted the clean curve for the loan the game had just
         * priced dearly.
         */
        out.printf("   the bank reloads with %,.0f branch(es), $%,.0fk deposited, $%,.0fk lent%n",
                reloaded.getBank().getBranches(), reloaded.getBank().getDeposits(),
                reloaded.getBank().getBook());

        /*
         * WHAT THE FAMILIES HAVE SAVED, ROW FOR ROW.
         *
         * Exact, because it is a carried stock. Their HOUSEHOLD COUNTS are not
         * exact across a load - the tier split is re-derived and comes back
         * within a couple of percent - which is a pre-existing gap in the
         * household split and has nothing to do with the bank. Asserted per row
         * rather than on the city total, so the carried half cannot start
         * drifting behind the re-derived half without this failing.
         */
        boolean savingsExact = true;
        for (int q = 0; q < HouseholdBalance.ROWS; q++) {
            if (Math.abs(city.getHouseholdBalance().getSavings(q)
                    - reloaded.getHouseholdBalance().getSavings(q)) > 1e-9) savingsExact = false;
        }
        assertTrue("every tier's savings reload to the cent", savingsExact);

        close("...and so does what the bank is charging for money",
                reloaded.getDebtManager().quoteRate(10_000),
                city.getDebtManager().quoteRate(10_000), 1e-9);
        close("...and the standing rate the debt screen shows",
                reloaded.getDebtManager().getRate(),
                city.getDebtManager().getRate(), 1e-9);

        /* ================= 6. a family that cannot carry it ================= */
        out.println("\n--- and the families it discharges ---");

        /*
         * CAUSED, NOT WAITED FOR.
         *
         * Driven directly through HouseholdBalance with a month a household
         * cannot survive: a wage that does not cover the rent, so the gap is
         * borrowed every month and the debt climbs until it is past the ceiling
         * while there is still nothing left to eat with. A fixture that ran a
         * city and hoped somebody went broke would pass or fail on the tuning.
         */
        HouseholdBalance broke = new HouseholdBalance();
        int rows = HouseholdBalance.ROWS;
        /*
         * PER CELL, since 2026-09-10: the balance is handed a census rather
         * than row counts. A thousand unskilled households of two and a half
         * people - five hundred couples and five hundred couples with a teen -
         * with the row's take-home between their two thousand earners.
         */
        double[][] mix = new double[FamilyStructure.values().length][PayTier.values().length];
        java.util.function.ToDoubleBiFunction<FamilyStructure, PayTier> census =
                (s, t) -> mix[s.ordinal()][t.ordinal()];
        int r = 0;
        mix[FamilyStructure.COUPLE.ordinal()][r] = 500;
        mix[FamilyStructure.COUPLE_TEEN.ordinal()][r] = 500;
        double[] disposable = new double[rows];
        double[] fees = new double[rows];
        double[] spent = new double[rows];
        double people = 2500;
        disposable[r] = 1000 * 1.2;      // $1.2k a household a month
        fees[r] = 0;
        double rent = 1.0;               // most of it goes on rent
        double foodPerHead = .25;        // ...and food costs more than what is left

        double firstBankruptMonth = -1;
        double writtenOff = 0;
        double discharged = 0;
        double left = 0;
        for (int m = 1; m <= 120; m++) {
            // Spends what a family must to eat, whether it has it or not.
            spent[r] = people * foodPerHead;
            broke.advanceMonth(census, disposable, rent, fees, spent,
                    foodPerHead, .05, 1);
            writtenOff += broke.getWrittenOff();
            discharged += broke.getBankrupt(r);
            left += broke.getLeavingCity();
            if (broke.getBankrupt(r) > 0 && firstBankruptMonth < 0) firstBankruptMonth = m;
        }

        out.printf("   after 120 months: debt $%,.2fk/household, first discharge in month %.0f%n",
                broke.getDebt(r), firstBankruptMonth);
        out.printf("   $%,.0fk written off, %,.0f households discharged, %,.0f people gone%n",
                writtenOff, discharged, left);

        assertTrue("a household that cannot pay its rent does go broke",
                firstBankruptMonth > 0);
        assertTrue("...and it takes months, not one bad month",
                firstBankruptMonth > 3);
        assertTrue("the write-off is real money, not a rounding",
                writtenOff > 0);
        assertTrue("...and the discharged tier is locked out afterwards",
                broke.isLockedOut(r));
        assertTrue("...for the full term", broke.getLockout(r) > 0
                && broke.getLockout(r) <= HouseholdBalance.LOCKOUT_MONTHS);
        assertTrue("some of them leave the city", left > 0);
        assertTrue("...but not all of them", left < discharged);

        /*
         * THE DEBT DOES NOT CLIMB FOREVER.
         *
         * The point of a bankruptcy is that it ENDS a position. Without it this
         * row's debt compounds at 36% until it is larger than every dollar in
         * the game, which is what it did before this went in.
         */
        double ceiling = HouseholdBalance.CREDIT_LIMIT_MONTHS * disposable[r] / 1000;
        out.printf("   the credit ceiling is $%,.2fk; the row is sitting at $%,.2fk%n",
                ceiling, broke.getDebt(r));
        assertTrue("debt is bounded by the ceiling, not compounding past it",
                broke.getDebt(r) <= ceiling * 1.05);

        /*
         * A LOCKED-OUT ROW CANNOT BORROW.
         *
         * The lockout is the whole consequence: wiping the debt and handing the
         * same family a fresh line of credit the next month is not a bankruptcy,
         * it is a subsidy.
         */
        HouseholdBalance locked = new HouseholdBalance();
        double[][] few = new double[FamilyStructure.values().length][PayTier.values().length];
        java.util.function.ToDoubleBiFunction<FamilyStructure, PayTier> fewCensus =
                (s, t) -> few[s.ordinal()][t.ordinal()];
        few[FamilyStructure.COUPLE.ordinal()][r] = 50;        // 100 people
        few[FamilyStructure.COUPLE_TEEN.ordinal()][r] = 50;   // 150 people
        double[] pay = new double[rows];
        pay[r] = 100 * 1.2;
        double[] noFees = new double[rows];
        double[] mustEat = new double[rows];
        mustEat[r] = 250 * foodPerHead;
        double borrowedWhileOpen = 0;
        for (int m = 1; m <= 60; m++) {
            locked.advanceMonth(fewCensus, pay, rent, noFees, mustEat, foodPerHead, .05, 1);
            if (!locked.isLockedOut(r)) borrowedWhileOpen += locked.getBorrowed(r);
        }
        double borrowedWhileShut = 0;
        for (int m = 1; m <= 6; m++) {
            if (locked.isLockedOut(r)) {
                locked.advanceMonth(fewCensus, pay, rent, noFees, mustEat, foodPerHead, .05, 1);
                borrowedWhileShut += locked.getBorrowed(r);
            }
        }
        assertTrue("the fixture did reach a lockout - or this proves nothing",
                borrowedWhileOpen > 0);
        close("a locked-out household is lent nothing at all", borrowedWhileShut, 0, 1e-9);

        /* ================= 7. and the city opens its own ================= */
        out.println("\n--- and a city with nobody watching opens one itself ---");

        /*
         * Jerus: "add the bank to investor so that banks expand themselves."
         *
         * CAUSED, as ever. This city is never told to build a bank; it is given
         * a reason to borrow and left alone. If the advisor cannot find its way
         * to a branch on its own then no unattended city ever gets one, and the
         * whole feature is a punishment for not knowing about it.
         */
        Path lonely = Files.createTempDirectory("bankcheck-advisor");
        Game unattended = new Game(new GameFiles(lonely.resolve("data"), lonely.resolve("no-legacy")));
        System.setOut(quiet);
        int branchesBuilt;
        try {
            unattended.run();
            /*
             * ...AND THEN THE CITY'S OWN BANK IS PULLED DOWN.
             *
             * Every city has founded with one since 2026-09-09, which is right
             * for the game and fatal for this fixture: the question below is
             * what a city with NO bank does, and a fixture that stands next to
             * its condition instead of causing it proves nothing. Scrapped
             * rather than the founding suppressed, because scrapping is
             * something the model can actually do and the state it leaves is a
             * state the game can reach.
             */
            unattended.getBuildingManager().retire(
                    template(unattended, "Commercial Bank"), 1);
            unattended.getLandManager().release(
                    template(unattended, "Commercial Bank").getLandSqFt());
            unattended.getForeignAccounts().pinRate(1.0);
            unattended.buildStack(template(unattended, "House"), 400, false);
            unattended.buildStack(template(unattended, "Convenience Store"), 8, false);
            unattended.buildStack(template(unattended, "Construction Depot"), 4, false);
            unattended.buildStack(template(unattended, "Coal Power Plant"), 1, false);
            unattended.buildStack(template(unattended, "Water Treatment Plant"), 1, false);

            /*
             * AND SOMEBODY HAS TO BE BORROWING, or the question does not arise.
             *
             * The assertion below is that a city with no bank WANTS one, and a
             * bank is wanted because there is a loan book to carry. This fixture
             * used to get its book from a retail sector that was quietly broke;
             * once the shops were paid for what they sold on 2026-09-07 they
             * stopped needing credit, the city had no debt at all, and the
             * advisor answered "nobody is borrowing yet" - which was correct,
             * and meant the fixture was no longer asking its own question.
             *
             * So the city borrows, on its own account, deliberately.
             */
            unattended.handleMediumBondLogic(20_000, 10, 1000);

            /*
             * ...AND KEEPS BORROWING, because a ten-year serial bond is repaid
             * by month 120 and this fixture reads its answer at month 180.
             *
             * Second time this fixture has stopped asking its own question the
             * same way. The comment above records the first: the shops were
             * fixed, stopped needing credit, and the city had no book. This
             * time the city's own deliberate bond simply matured before anybody
             * looked, and the advisor again answered "nobody is borrowing yet"
             * - correct, and about a city the fixture had not meant to build.
             *
             * A city that borrows once is not a city with a loan book; it is a
             * city that borrowed once. Rolled over halfway, which is what a
             * treasury actually does and what keeps a book on the books.
             */
            unattended.simulateMonths(180);

            /*
             * ...AND IS STILL BORROWING WHEN THE QUESTION IS ASKED.
             *
             * The bond above is a ten-year serial and this reads its answer at
             * month 180, so the whole thing had amortised away and the city had
             * no book at all - the advisor said "nobody is borrowing yet",
             * which was true about a city the fixture did not mean to build.
             * Second time this fixture has stopped asking its own question that
             * way; the comment above records the first.
             *
             * Issued at the end rather than rolled mid-run, because what is
             * being tested is whether an unbanked city with a loan book wants a
             * bank - so the book needs to exist at the moment of asking, and
             * nothing else about the run depends on when it was raised.
             */
            unattended.handleMediumBondLogic(20_000, 10, 1000);
            unattended.simulateMonths(1);
            branchesBuilt = unattended.getBuildingManager().countByName("Commercial Bank");
        } finally {
            System.setOut(out);
        }

        out.printf("   a small unbanked city: %d branch(es), premium %.1f points%n",
                branchesBuilt, unattended.getBank().ratePremium() * 100);
        BusinessInvestment.Decision wanted = unattended.getBusinessInvestment().planBank();
        out.printf("   the advisor wants one: %s - \"%s\"%n", wanted.build, wanted.reason);
        out.printf("   ...and could not have it: %s%n", unattended.getLastInvestment("Bank"));

        /*
         * THE ADVISOR ASKS EVEN WHEN IT CANNOT PAY, and the two halves are
         * asserted separately on purpose.
         *
         * A town of four hundred houses has a loan book of about $2M and a
         * branch costs $9M, so a branch genuinely does not pay for itself there
         * - the eighteen points it would save are eighteen points of nearly
         * nothing. That is the right answer and this fixture does not argue with
         * it. What WOULD be wrong is the advisor never noticing, so what is
         * asserted here is that it asks; whether it can afford the answer is the
         * next fixture's question.
         */
        /*
         * WANTS ONE, OR IS ALREADY PUTTING ONE UP - and the second is not a
         * weaker claim, it is the same claim one month later.
         *
         * This asserted `wanted.build` alone and broke when the founding
         * endowment went to $3.5B with rebalance stage two: the city could now
         * afford the branch, so it ORDERED one, so by the time the decision was
         * read the advisor was correctly holding with "a branch is already
         * going up". The fixture was asking a city that had already solved the
         * problem whether it still had it.
         *
         * What must never happen is the advisor not noticing, and that is what
         * both branches below rule out. The reason string still has to be about
         * credit either way, which is the half that catches a bank being
         * ordered as though it were a shop.
         */
        /*
         * ...OR HAS ONE STANDING. Third time (2026-09-10): the city now puts
         * the branch up inside the run, so the decision read afterwards is
         * "room enough" - which is the advisor having noticed, acted, and
         * finished. The claim is unchanged: a city with no bank does
         * something about it.
         */
        boolean noticed = wanted.build
                || wanted.reason.toLowerCase().contains("already going up")
                || branchesBuilt >= 1;
        assertTrue("a city with no bank at all does something about it", noticed);
        assertTrue("...and says so in words about credit, not about shops",
                wanted.reason.toLowerCase().contains("bank")
                        || wanted.reason.toLowerCase().contains("credit")
                        || wanted.reason.toLowerCase().contains("branch"));

        /* ---- and a city that can pay for one, does ---- */

        Path solvent = Files.createTempDirectory("bankcheck-solvent");
        Game trading = new Game(new GameFiles(solvent.resolve("data"), solvent.resolve("no-legacy")));
        System.setOut(quiet);
        int builtWhenAffordable;
        try {
            trading.run();
            trading.getForeignAccounts().pinRate(1.0);
            /*
             * Standing on month one, with roads and food - the same reason
             * as the books fixture below: queued behind its own power plant
             * this city starved, its landlord defaulted, and the branch the
             * shops opened had FAILED by the time it was counted, still
             * paying the punitive premium with a bank standing in the city.
             */
            trading.getLandManager().setOwnedSqFt(30_000_000);
            trading.buildStack(template(trading, "House"), 400, true);
            trading.buildStack(template(trading, "Convenience Store"), 8, true);
            trading.buildStack(template(trading, "Small Grocery Store"), 2, true);
            trading.buildStack(template(trading, "Food Processing Plant"), 1, true);
            trading.buildStack(template(trading, "Paved Road"), 20, true);
            trading.buildStack(template(trading, "Construction Depot"), 4, true);
            trading.buildStack(template(trading, "Coal Power Plant"), 1, true);
            trading.buildStack(template(trading, "Water Treatment Plant"), 1, true);
            trading.simulateMonths(24);
            /*
             * CAUSED, not waited for. Two things stand between the advisor's
             * decision and the building, and both are handed over here so that
             * what is left being measured is the decision: money in the shops'
             * tills, and somewhere to put a 45,000 sq ft building. The first
             * version gave it the money and not the land, and the advisor
             * decided correctly every month and was refused by the ground.
             */
            trading.getEconomyManager().setSectorCash(BusinessDebtManager.RETAIL, 250_000);
            trading.getLandManager().setOwnedSqFt(8_000_000);
            trading.simulateMonths(60);
            builtWhenAffordable = trading.getBuildingManager().countByName("Commercial Bank");
        } finally {
            System.setOut(out);
        }

        out.printf("   a city whose shops can pay for one: %d branch(es), premium %.1f points%n",
                builtWhenAffordable, trading.getBank().ratePremium() * 100);
        out.printf("   the advisor's last word on it: %s%n", trading.getLastInvestment("Bank"));

        assertTrue("a city that can afford a branch opens one, unprompted",
                builtWhenAffordable >= 1);
        assertTrue("...and stops paying the punitive premium once it has",
                trading.getBank().ratePremium() < Bank.MAX_STRAIN_PREMIUM);

        /*
         * ...AND STOPS. An advisor that builds a branch because it wants one and
         * then still wants one has built a factory for bank branches.
         */
        Bank sated = new Bank();
        sated.injectCapital(10_000_000);
        sated.refresh(1, 10_000_000, 0, Bank.DEPOSITS_PER_BRANCH * Bank.LEVERAGE * .10, 0, 0);
        assertTrue("a bank with room to spare does not ask for another counter",
                !sated.wantsBranch());
        close("...and prices one at nothing, so nobody would build it",
                sated.bookAnotherBranchWouldCarry(), 0, 1e-9);

        sated.refresh(1, 10_000_000, 0, Bank.DEPOSITS_PER_BRANCH * Bank.LEVERAGE * 1.4, 0, 0);
        assertTrue("a bank lent out past itself does ask", sated.wantsBranch());
        assertTrue("...and a branch is worth real money to it",
                sated.bookAnotherBranchWouldCarry() > 0);

        /* ============ 8. the accounting identities, on a played city ============ */
        out.println("\n--- and its books actually balance ---");

        Path booksRoot = Files.createTempDirectory("bankcheck-books");
        double profitTaxedNextMonth = 0;
        Game books = new Game(new GameFiles(booksRoot.resolve("data"), booksRoot.resolve("no-legacy")));
        System.setOut(quiet);
        double worstBalance = 0, worstArticulation = 0;
        int worstMonth = 0;
        try {
            books.run();
            books.getForeignAccounts().pinRate(1.0);
            /*
             * A CITY THAT WORKS, STANDING ON MONTH ONE (2026-09-10).
             *
             * This fixture used to queue the same list behind a 500-month
             * construction backlog, with no roads and no food: a city of
             * 215 people in 400 houses, half of them hungry, whose landlord
             * borrowed to build Low-Rise blocks for nobody and defaulted on
             * them. The bank's books balanced on top of that, which is what
             * was being asserted - right up until the day the material
             * import bill started being charged and the landlord's second
             * default took the whole of the bank's equity in month 70. A
             * failed bank articulates through a resolution loss, which this
             * fixture's explanation of equity has never included, and a
             * failed bank has no book, so two assertions fell over for a
             * reason about starvation.
             *
             * So the city is put up whole - roads, food, shops - on ground it
             * owns, and the book is CAUSED rather than hoped for: Industry
             * starts $20M short with two mills of collateral, borrows it at
             * the counter and pays it back out of its own income over the
             * next four years. What is left being measured is the books.
             */
            books.getLandManager().setOwnedSqFt(30_000_000);
            books.buildStack(template(books, "House"), 400, true);
            books.buildStack(template(books, "Convenience Store"), 8, true);
            books.buildStack(template(books, "Small Grocery Store"), 2, true);
            books.buildStack(template(books, "Food Processing Plant"), 1, true);
            books.buildStack(template(books, "Paved Road"), 20, true);
            books.buildStack(template(books, "Textile Mill"), 2, true);
            books.buildStack(template(books, "Construction Depot"), 4, true);
            books.buildStack(template(books, "Coal Power Plant"), 1, true);
            books.buildStack(template(books, "Water Treatment Plant"), 1, true);
            books.simulateMonths(24);
            books.getEconomyManager().setSectorCash(BusinessDebtManager.RETAIL, 250_000);
            books.getEconomyManager().setSectorCash(BusinessDebtManager.INDUSTRY, -20_000);

            Bank kept = books.getBank();
            for (int m = 0; m < 120; m++) {
                // The profit the NEXT month's tax will be charged on - struck
                // at closeMonth(), charged at the top of the month after.
                profitTaxedNextMonth = kept.getProfitLastMonth();
                books.simulateMonths(1);

                // A = L + E, every month, no exceptions.
                double balance = kept.totalAssets() - kept.totalLiabilities() - kept.equity();
                if (Math.abs(balance) > Math.abs(worstBalance)) worstBalance = balance;

                /*
                 * ...AND EQUITY MOVES BY THE MONTH'S PROFIT AND BY NOTHING ELSE,
                 * bar capital actually put in. This is the assertion that earns
                 * its keep: it is what found the restructures being written off
                 * against nobody, the treasury's paper being bought at a
                 * discount that went nowhere, and new households arriving
                 * already owing the average family's debt.
                 */
                double moved = kept.equity() - kept.getOpeningEquity();
                /*
                 * BOTH HALVES OF THE PAID-IN CAPITAL. getCapitalInjected() is
                 * the foreign share only since the two were split - see
                 * Bank.injectCapital() - and this identity does not care where
                 * a shareholder lives, only that the money arrived.
                 */
                double explained = kept.getNetIncome()
                        + kept.getCapitalInjected() + kept.getCapitalFromHome()
                        + kept.getBailoutReceived() + kept.getFoundingSettlement();
                if (Math.abs(moved - explained) > Math.abs(worstArticulation)) {
                    worstArticulation = moved - explained;
                    worstMonth = books.getMonth();
                }
            }
        } finally {
            System.setOut(out);
        }

        Bank shown = books.getBank();
        out.printf("   after 12 years: assets $%,.0fk, liabilities $%,.0fk, equity $%,.0fk%n",
                shown.totalAssets(), shown.totalLiabilities(), shown.equity());
        out.printf("   it charges %.2f%% and pays savers %.2f%%; profit $%,.2fk this month%n",
                books.getDebtManager().getRate() * 100, shown.depositRate() * 100,
                shown.getNetIncome());
        out.printf("   worst balance-sheet error $%.6fk; worst unexplained equity move"
                + " $%.6fk (month %d)%n", worstBalance, worstArticulation, worstMonth);

        assertTrue("the fixture's bank is actually running a book",
                shown.getBook() > 0 && shown.getBranches() >= 1);
        close("ASSETS = LIABILITIES + EQUITY, every month", worstBalance, 0, .005);
        close("...and equity moves by net income and capital, and nothing else",
                worstArticulation, 0, .005);

        /* ---- and the city takes its share ---- */

        /*
         * Jerus: "quick question banks are taxed right?"
         *
         * Its staff always were. Its profits were not by anybody, which took a
         * bank with a full set of financial statements and left it the only
         * business in the city paying no profit tax.
         */
        double bankRate = books.getEconomyManager().getTaxPolicy()
                .effectiveProfitRate(PolicySector.RETAIL);
        out.printf("   taxed at %.1f%% on last month's $%,.2fk of profit: $%,.2fk%n",
                bankRate * 100, shown.getProfitLastMonth(), shown.getTaxPaid());

        /*
         * THE LAW, on the played city: whatever it made, the city took that
         * share of it and no other. Asserted this way rather than "the tax is
         * positive" because a small city's bank is genuinely marginal - this
         * one loses money most months - and a fixture that needed it to be
         * profitable would be testing the tuning rather than the tax.
         */
        /*
         * ...OF WHAT IT MADE LAST MONTH. chargeTax() runs at the top of the
         * month on the profit closeMonth() struck at the bottom of the one
         * before; getProfitLastMonth() after the month has ended is already
         * the NEXT bill's base. This compared the two and passed for as long
         * as the fixture's bank made the same profit every month - the moment
         * an interest reserve let a sector borrow a little each month, the
         * profit moved 3% and the assertion failed with the tax exactly right.
         */
        close("the city takes exactly its share of what the bank made",
                shown.getTaxPaid(), Math.max(0, profitTaxedNextMonth) * bankRate, 1e-9);
        close("...and the figure the treasury books is the figure the bank paid",
                books.getEconomyManager().getBankTax(), shown.getTaxPaid(), 1e-9);

        /* ---- and the mechanism itself, where the profit is not up for debate ---- */

        Bank earning = new Bank();
        earning.injectCapital(100_000);
        earning.setProfitLastMonth(10_000);
        double took = earning.chargeTax(.25);
        close("a profitable bank hands over its share", took, 2_500, 1e-9);
        close("...out of its own cash", earning.equity(), 97_500, 1e-9);
        close("...and reports it", earning.getTaxPaid(), 2_500, 1e-9);
        close("...which comes off the month's net income",
                earning.getNetIncome(), earning.profitBeforeTax() - 2_500, 1e-9);

        // A loss is not a refund - the same treatment every other sector gets.
        Bank losing = new Bank();
        losing.setProfitLastMonth(-5_000);
        close("a bank that lost money is not paid a refund", losing.chargeTax(.25), 0, 1e-12);

        /* ---- what a bank is: it earns the difference between two rates ---- */

        assertTrue("it pays its savers something", shown.depositRate() > 0);
        assertTrue("...and less than it charges its borrowers",
                shown.depositRate() < books.getDebtManager().getRate());
        assertTrue("...and its staff are on its own books, not the shops'",
                shown.operatingExpenses() > 0);

        /* ============ 9. capital is the constraint, and it can run out ============ */
        out.println("\n--- and capital is what lets it lend ---");

        Bank solid = new Bank();
        solid.injectCapital(80_000);
        solid.refresh(100, 10_000_000, 0, 0, 0, 0);
        close("a bank can lend its capital over the regulator's ratio",
                solid.capitalLimit(), 80_000 / Bank.CAPITAL_RATIO, 1e-9);

        // Lend it out, then lose most of it.
        solid.refresh(100, 10_000_000, 0, 900_000, 0, 0);
        double beforeLosses = solid.capacity();
        assertTrue("...and does", beforeLosses > 0);

        Bank bust = new Bank();
        bust.injectCapital(80_000);
        bust.refresh(100, 10_000_000, 0, 0, 0, 0);
        bust.lend(200_000);            // out the door
        bust.refresh(100, 10_000_000, 0, 100_000, 0, 0);   // half of it never coming back
        assertTrue("a bank that loses more than it owns is insolvent", bust.isInsolvent());
        close("...and can lend nothing at all", bust.capacity(), 0, 1e-9);
        close("...so every borrower in the city pays the full premium",
                bust.ratePremium(), Bank.MAX_STRAIN_PREMIUM, 1e-12);
        assertTrue("...and it says what it would take to fix",
                bust.recapitalisationNeeded() > 0);

        bust.receiveBailout(bust.recapitalisationNeeded());
        assertTrue("recapitalised, it is solvent again", !bust.isInsolvent());
        assertTrue("...and lending again", bust.capacity() > 0);
        assertTrue("...and above the ratio it is required to hold, by at least the exit buffer",
                bust.capitalRatio() >= Bank.CAPITAL_RATIO * Bank.RESOLUTION_EXIT_BUFFER - 1e-9);
        assertTrue("...and holding at least what one branch is capitalised with",
                bust.equity() >= Bank.PAID_IN_PER_BRANCH - 1e-9);
        close("...which is exactly what it was asked for, and not a dollar more",
                bust.recapitalisationNeeded(), 0, 1e-9);

        /*
         * A BANK WITH NO BOOK ASKED FOR NOTHING, AND SO GOT NOTHING. The ratio
         * is struck on the book, so a bank that lost its book with its equity
         * needed - by the ratio - zero, lent nothing because it had nothing,
         * never got a book, and never needed anything. That was the end state
         * of every long run ever recorded. The floor is one branch's capital.
         */
        Bank hollow = new Bank();
        hollow.injectCapital(50_000);
        hollow.refresh(3, 10_000_000, 0, 0, 0, 0);
        hollow.lend(100_000);                             // half its own, half borrowed
        hollow.refresh(3, 10_000_000, 0, 0, 0, 0);        // and none of it coming back
        hollow.resolveIfFailed();
        assertTrue("fixture: a bank that lost its whole book has failed", hollow.getFailures() == 1);
        close("fixture: ...and has no book left to strike a ratio on", hollow.getBook(), 0, 1e-9);
        assertTrue("a failed bank with no book is still asked for one branch's capital",
                hollow.recapitalisationNeeded() >= Bank.PAID_IN_PER_BRANCH - 1e-9);
        hollow.receiveBailout(hollow.recapitalisationNeeded());
        assertTrue("...and can lend again once it has it", hollow.capacity() > 0);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}

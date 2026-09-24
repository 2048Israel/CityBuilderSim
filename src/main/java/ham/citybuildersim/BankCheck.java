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
 *   1. Does the price of credit actually depend on the bank - since 0.7.7 on
 *      what lending costs it, not on how lent out it is? A price that no
 *      borrower pays is a number on a screen.
 *   2. Does the money still add up, now that four flows that used to run to and
 *      from nowhere run between two pools inside the city?
 *   3. Does a family that cannot carry its debt get discharged - and does the
 *      bank, not thin air, eat the loss?
 *   4. Does the trading desk's statement add up? Jerus: "the bank, just
 *      explain to me the trading desk, cause a bunch of times it's losing
 *      billions of dollars due to the trading desk." The opened lines have to
 *      sum to the figure above them, and the term that makes them - the
 *      re-mark of what the desk holds - is measured on a fixture that trades
 *      and counted on a played city.
 *   5. Does the bank PAY for the city's paper? It held every bond the city
 *      sold and, until 2026-09-21, had never handed over a dollar for one -
 *      section 10. Since 0.7.1 it pays for what the households did not take,
 *      and earns the discount as it accretes rather than the month it settles.
 *   6. Does what the Bank tab prints add up (0.7.9, section 13)? The ladder's
 *      parts are its prime, the weight table foots to the weighted book, and
 *      the equity's movement leaves nothing unexplained - three figures the
 *      screen used to work out for itself, two of them wrongly.
 *   7. Does the desk keep to the bank's capital (0.7.8, section 17)? It buys
 *      the bank's own shares back only with what the bank holds over its
 *      target, and other companies' only while the bank would still hold its
 *      target with them on its books, at the weight the weighted book gives
 *      the desk; what it will not buy stays with the seller.
 *   8. Does the bank keep its capital like a business (0.7.8, sections 7-12)?
 *      It sets aside for the loans that will not come back and draws a
 *      write-off against that first, chooses its target from the worst year
 *      it has lived through, keeps its profit and lends slower under it,
 *      pays a share inside its band and returns the excess over it - and a
 *      reload reads the same month.
 *   9. Does a sector default a slice at a time (0.7.8, sections 14-16)? The
 *      month's slice is the curve's and the allowance reads the same curve,
 *      firm by firm; the backstop takes only a sector with nothing left; a
 *      failing sector's plant is sold to the builders for its material; and
 *      the bank reads a borrower from its last quarter.
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

    /**
     * The trading desk's statement as the bank screen opens it, less the
     * re-mark: sold to households and abroad, bought from both, dividends on
     * the inventory, tendered into buybacks - the same getters BankScreen
     * reads, with the same signs.
     */
    static double deskParts(Exchange exchange, Equity register, Bank bank) {
        // Not the bank's own shares, which are capital since 0.7.8 and not
        // the desk's trading (Exchange.payForShares()): its lines, less them
        // - Exchange.deskSoldToHouseholds() and the three beside it since
        // 0.7.9, which the screen reads.
        return exchange.deskSoldToHouseholds() + exchange.deskSoldAbroad()
                - exchange.deskBoughtFromHouseholds() - exchange.deskBoughtFromAbroad()
                + register.getDividendDeskThisMonth() + exchange.getBuybackToDesk();
    }

    /**
     * Everything that moves the bank's equity that is not its net income:
     * capital put in (both halves), the treasury's, the founding settlement,
     * less its dividend - and since 0.7.8 its own shares, issued or bought
     * back, which moved it through the desk's "trading income" before.
     */
    static double capitalMoved(Bank b) {
        return b.getCapitalInjected() + b.getCapitalFromHome()
                + b.getBailoutReceived() + b.getFoundingSettlement()
                - b.getDividendsPaid()
                + b.getSharesIssued() - b.getSharesBoughtBack();
    }

    static double deskParts(Game game) {
        return deskParts(game.getExchange(), game.getEquity(), game.getBank());
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

           Jerus, 2026-09-23: "the bank chooses, but it can pass on less or
           more if it wants." Since 0.7.7 the bank sets a RATE, a share of the
           policy rate its funding asks for - see section (3) below for the
           two ends - moved a step a month; and rule 2 stands: never more
           interest out than interest in. This section asserted the old rules
           until 0.7.7 - a share of the month's interest INCOME
           (DEPOSIT_PASS_THROUGH), a bid for hot money when lent out and short
           of deposits (rule 3, which a 600-month probe once fired zero times),
           and the quote capped at the lending rate (0.7.3) - and asserts what
           replaced them now, each caused here rather than hoped for in a city.
           ================================================================= */
        System.out.println("\n--- what to pay savers is a decision ---");

        /* ---- rule 2 first, because it bounds the other ---- */
        Bank marginGone = new Bank();
        marginGone.refresh(1, 1_000_000, 0, 0, 0, 0);
        marginGone.injectCapital(1_000_000);
        marginGone.startMonth();
        // Lent past its capital: window funding to pay for, and no idle
        // reserves earning a placement (see Bank.placementIncome) - the
        // margin is gone in both directions.
        marginGone.lend(1_500_000);
        marginGone.fundToCover(.10);
        assertTrue("a bank whose margin is gone pays its savers nothing",
                marginGone.depositInterest() <= 1e-9);
        assertTrue("...and says its margin held them under the rate it chose",
                marginGone.isDepositPayoutHeld() && marginGone.getChosenDepositRate() > 0);

        /*
         * ...AND A MARGIN THAT IS THERE IS NOT ALL THE SAVERS'. The gate's
         * held-10% city, in miniature: a flush bank whose only income is its
         * capital's placement, on ten times that capital in deposits, which
         * it counts but does not hold - the rate it chooses on all of them is
         * more than it earns. Once with no running costs, where the savers'
         * share of the margin binds; once with a payroll that leaves less
         * than that share, where the running costs do.
         */
        double heldDial = .10;
        Bank shareHeld = new Bank(), costHeld = new Bank();
        for (Bank b : new Bank[] { shareHeld, costHeld }) {
            b.injectCapital(1_000_000);
            b.refresh(1, 10_000_000, 0, 0, 0, 0);
            b.setDepositRate(Bank.DEPOSIT_SHARE_FLUSH * heldDial);
            b.startMonth();
        }
        costHeld.payRunning(6_000, 0);
        for (Bank b : new Bank[] { shareHeld, costHeld }) b.fundToCover(heldDial);
        assertTrue("fixture: the rate it chose on all its deposits is more than its margin",
                shareHeld.getChosenDepositRate() * shareHeld.getDeposits() / 12
                        > shareHeld.getPlacementIncome() && shareHeld.isDepositPayoutHeld());
        close("a bank whose deposits earn it nothing pays its savers their share of what it did earn",
                shareHeld.depositInterest(), shareHeld.depositShare() * shareHeld.getPlacementIncome(), 1e-9);
        assertTrue("fixture: the second bank's payroll leaves less than that share",
                costHeld.getPlacementIncome() - 6_000 < costHeld.depositShare() * costHeld.getPlacementIncome());
        close("...and never so much that its margin no longer pays its staff and buildings",
                costHeld.depositInterest(), costHeld.getPlacementIncome() - 6_000, 1e-9);
        assertTrue("...so paying its savers never costs it money",
                costHeld.getInterestEarned() - costHeld.depositInterest() - costHeld.getFundingCost()
                        - 6_000 >= -1e-9 && costHeld.isDepositPayoutHeld());

        /* ---- rule 1: a rate it chooses, paid on the deposits ---- */
        Bank earner = new Bank();
        earner.refresh(1, 1_000_000, 0, 0, 0, 0);
        earner.injectCapital(1_000_000);
        earner.startMonth();
        earner.takeInterest(1_000);
        earner.fundToCover(.10);
        assertTrue("fixture: the idle capital earned a placement",
                earner.getPlacementIncome() > 0);
        close("a new bank moves a step toward the share of the dial its funding asks for",
                earner.depositRate(), Bank.DEPOSIT_RATE_SPEED * earner.depositShare() * .10, 1e-12);
        close("...and that rate is what it paid, over the deposits",
                earner.depositRate(),
                earner.depositInterest() / earner.getDeposits() * 12, 1e-12);
        assertTrue("...with its margin to spare, so nothing held it",
                !earner.isDepositPayoutHeld());

        /*
         * ...AND THE RATE IT QUOTES IS NEVER MORE THAN IT CHARGES - the 0.7.3
         * cap's premise, which the chosen rate keeps by construction. A
         * founding bank's shape: its own capital at the central bank earning
         * the policy rate, and a deposit book that has barely opened - $5.7k,
         * seed 0's first month, which the income share read as 7,567% a year.
         */
        Bank opening = new Bank();
        opening.refresh(1, 5.7, 0, 0, 0, 0);
        opening.injectCapital(1_000_000);
        opening.startMonth();
        opening.fundToCover(.03);
        out.printf("   a bank's first month: paid %,.4f on %,.1f of deposits - %.3f%% a year;"
                + " it charges %.2f%%%n", opening.depositInterest(), opening.getDeposits(),
                opening.depositRate() * 100, opening.lendingRate(.03) * 100);
        assertTrue("a bank's first month pays its savers less than it charges",
                opening.depositRate() < opening.lendingRate(.03));
        assertTrue("...and no more than the window charges it",
                opening.depositRate() <= .03 + CentralBank.WINDOW_PENALTY + 1e-12);

        // What the branch fixtures below lend and earn.
        double savings = 10_000_000;
        double branchesEnough = savings / Bank.DEPOSITS_PER_BRANCH;   // it can reach all of it
        double earned = 5_000;
        double insideItsDeposits = savings / 2;

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

        /* ================= 2. STRAIN IS NOT A PRICE (0.7.7) =================

           Until 0.7.7 this section asserted the strain premium: nothing to 80%
           of capacity, rising to eighteen points at 150%, monotone on the way,
           and the full eighteen for a city with no bank that borrowed - and
           the next that it landed on the city's quote point for point. The
           premium is gone, and these two assert what replaced it: a loan is
           priced from what it costs to make, so two banks with the same costs
           quote the same rates however lent out either is; the city's paper
           has nothing of the bank's strain in it; and a new loan's rate is its
           four parts, fixed for its term.
           ================================================================= */
        out.println("\n--- (1) a strained bank quotes what an unstrained one with the same costs does ---");

        /*
         * CAUSED: two banks with the same capital, the same deposits, the same
         * reserves and the same closed month - no payroll, no write-offs - and
         * a book the refresh puts at half of capacity in one and 140% in the
         * other. Capitalised well past what they gathered, so the FUNDING limit
         * binds and the strain is a fact about the book.
         */
        int branches = 4;
        double deposits = branches * Bank.DEPOSITS_PER_BRANCH;
        double room = deposits * Bank.LEVERAGE;
        Bank calm = new Bank(), stretched = new Bank();
        for (Bank b : new Bank[] { calm, stretched }) {
            b.injectCapital(room * Bank.CAPITAL_RATIO * 20);
            b.refresh(branches, deposits, 0, 0, 0, 0);
            b.startMonth();
            b.fundToCover(.04);
            b.closeMonth();
        }
        calm.refresh(branches, deposits, 0, room * .5, 0, 0);
        stretched.refresh(branches, deposits, 0, room * 1.4, 0, 0);
        out.printf("   strain %.2f against %.2f: prime %.3f%% against %.3f%% at a 4%% dial%n",
                calm.strain(), stretched.strain(), calm.prime(.04) * 100, stretched.prime(.04) * 100);
        assertTrue("fixture: one is comfortable and the other is lent out past itself",
                calm.strain() < Bank.EASY_STRAIN && stretched.strain() > 1);
        boolean samePrice = true;
        for (double dial = 0; dial <= .20 + 1e-9; dial += .01) {
            if (Math.abs(calm.prime(dial) - stretched.prime(dial)) > 1e-15
                    || Math.abs(calm.householdRate(dial) - stretched.householdRate(dial)) > 1e-15
                    || Math.abs(calm.lendingRate(dial) - stretched.lendingRate(dial)) > 1e-15) {
                samePrice = false;
            }
        }
        assertTrue("the same prime, household rate and lending rate at every dial from 0 to 20%", samePrice);

        /*
         * ...AND THE CITY'S PAPER HAS NOTHING OF IT IN IT. What reaches the
         * debt market from the bank since 0.7.7 is what the bank's money costs
         * it (marginalCostOfFunds(), the floor) - the same for the two banks,
         * because their funding is - and the market quotes the same note,
         * bond and curve off either.
         */
        DebtManager offCalm = new DebtManager(), offStretched = new DebtManager();
        for (DebtManager dm : new DebtManager[] { offCalm, offStretched }) {
            dm.setPolicyRate(.04);
            dm.setGDP(9_068);
            dm.setTaxRevenue(1_916);
            dm.setCashPosition(0);
            dm.addLongTermBond(300_000, 240, 1, .04);
        }
        offCalm.setCostOfFunds(calm.marginalCostOfFunds());
        offStretched.setCostOfFunds(stretched.marginalCostOfFunds());
        offCalm.updateInterest();
        offStretched.updateInterest();
        close("the city's note is the same off the strained bank as off the comfortable one",
                offStretched.getRate(), offCalm.getRate(), 1e-15);
        close("...and so is its twenty-year paper",
                offStretched.curveRate(240), offCalm.curveRate(240), 1e-15);
        close("...which is the dial, its floor and its own spreads, and nothing else",
                offStretched.getRate(), offStretched.rateAtPolicy(.04), 1e-15);

        out.println("\n--- (2) a new loan's rate is its parts, and is fixed for its term ---");

        /*
         * A bank with a history to price from: a payroll, a write-off, and a
         * book lent past its deposits so part of its money is the window's.
         * Closed, so its parts are struck, and read at a dial of 3%.
         */
        Bank priced = new Bank();
        priced.injectCapital(50_000);
        priced.refresh(1, 100_000, 0, 700_000, 0, 0);
        priced.startMonth();
        priced.lend(700_000);                    // past the 100,000 its one branch gathered
        priced.takeInterest(3_000);
        priced.payRunning(400, 100);
        priced.writeOff(700);
        priced.fundToCover(.03);
        priced.closeMonth();
        double dial = .03;
        int term = Bank.PRIME_TERM_MONTHS;
        double ftp = priced.fundsTransferPrice(dial, term);
        out.printf("   at a 3%% dial: ftp %.3f + running %.3f + loss %.3f + capital %.3f = prime %.3f%%"
                + " (window share %.2f)%n", ftp * 100, priced.runningCostRate() * 100,
                priced.expectedLossRate() * 100,
                priced.capitalCharge(dial, term, Bank.RISK_BUSINESS) * 100, priced.prime(dial) * 100,
                priced.windowShare());
        assertTrue("fixture: part of its money is the window's", priced.windowShare() > 0 && priced.windowShare() < 1);
        close("the funds-transfer price: the dial, the window's penalty on its share, the curve's term premium",
                ftp, dial + CentralBank.WINDOW_PENALTY * priced.windowShare() + DebtManager.termPremium(term), 1e-15);
        close("...the window's share is what it owes the window over all it borrowed",
                priced.windowShare(), priced.wholesaleFunding() / priced.borrowings(), 1e-12);
        double carried = Math.max(priced.getBook(),
                Math.max(priced.equity(), Bank.PAID_IN_PER_BRANCH) / priced.capitalTarget());
        close("the running costs: a year of payroll and upkeep over the book, or over what its capital carries",
                priced.runningCostRate(), (400 + 100) * 12 / carried, 1e-12);
        /*
         * THE EXPECTED LOSS IS THE THROUGH-THE-CYCLE BASE, and this bank's own
         * write-off does not reach into it: a borrower's risk is its spread
         * (the leverage, below), not the bank's last loss. The fixture wrote
         * off a month that runs at three times the base a year, so a price
         * struck from the record would read differently here.
         */
        assertTrue("fixture: this month's write-off runs past the base rate a year",
                12 * priced.getWriteOffs() / priced.getSectorBook() > 2 * Bank.BASE_LOSS_RATE);
        close("the expected loss: the through-the-cycle base, whatever the bank has just written off",
                priced.expectedLossRate(), Bank.BASE_LOSS_RATE, 0);
        close("the owners' return is the one the market prices its shares on",
                Bank.requiredReturn(), (DebtManager.WORLD_BASE_RATE + Equity.FOREIGN_PREMIUM) / Equity.PAYOUT, 1e-15);
        /*
         * AT THE BANK'S OWN TARGET since 0.7.8 - which replaces the fixed
         * three points over the minimum 0.7.7 priced at (CAPITAL_BUFFER, gone):
         * a bank this young has no year of losses on record, so it holds the
         * conservation buffer over the minimum. Section (8) asserts how the
         * target moves.
         */
        close("the capital charge: the weight, the bank's own target ratio, the owners' return over the ftp",
                priced.capitalCharge(dial, term, Bank.RISK_BUSINESS),
                Bank.RISK_BUSINESS * priced.capitalTarget() * (Bank.requiredReturn() - ftp), 1e-15);
        close("...and a bank with no year on record targets the minimum and the conservation buffer",
                priced.capitalTarget(), Bank.CAPITAL_RATIO + Bank.CONSERVATION_BUFFER, 1e-15);
        close("prime is the four added up",
                priced.prime(dial), ftp + priced.runningCostRate() + priced.expectedLossRate()
                        + priced.capitalCharge(dial, term, Bank.RISK_BUSINESS), 1e-15);
        close("...a household's line the same four at its own weight, with no term",
                priced.householdRate(dial), priced.fundsTransferPrice(dial, 0) + priced.runningCostRate()
                        + priced.expectedLossRate() + priced.capitalCharge(dial, 0, Bank.RISK_HOUSEHOLD), 1e-15);
        assertTrue("...and the capital charge never goes negative, whatever the dial",
                priced.capitalCharge(.40, term, Bank.RISK_BUSINESS) == 0);

        // A city with no bank is priced the same way, its marginal money the window's.
        Bank noBank = new Bank();
        close("a city with no bank yet: its money is the window's",
                noBank.windowShare(), 1, 0);
        close("...and it is quoted the same four parts, not a punitive rate",
                noBank.prime(dial), dial + CentralBank.WINDOW_PENALTY + DebtManager.termPremium(term)
                        + 0 + Bank.BASE_LOSS_RATE + noBank.capitalCharge(dial, term, Bank.RISK_BUSINESS), 1e-15);

        /*
         * ...A SOUND BUSINESS PAYS PRIME, and keeps the rate for the loan's
         * term. The lender is handed prime, lends to a sector at a tenth of
         * its assets, and then the dial doubles.
         */
        BusinessDebtManager lender = new BusinessDebtManager();
        String sound = Sectors.INDUSTRY;
        lender.setPrimeRate(priced.prime(dial));
        lender.setAssets(sound, 1_000_000);
        lender.updateRates();
        BusinessLoan written = lender.issueLoan(sound, 100_000, 1);
        // Its own risk is its expected loss off the curve over the book's
        // since 0.7.8 - nothing at a tenth of its assets.
        close("a lightly leveraged business pays prime plus its own expected loss and nothing else",
                written.getAnnualRate(), priced.prime(dial) + BusinessDebtManager.expectedLossSpread(.1), 1e-15);
        close("...which at a tenth of its assets is nothing: it pays prime",
                written.getAnnualRate(), priced.prime(dial), 1e-15);
        lender.setPrimeRate(priced.prime(.06));
        lender.updateRates();
        for (int m = 2; m < BusinessDebtManager.LOAN_TERM_MONTHS; m++) lender.processMonth();
        assertTrue("fixture: new borrowing reprices with the dial",
                lender.getRate(sound) > written.getAnnualRate());
        close("...and the loan written before it keeps its rate to the last month of its term",
                written.getAnnualRate(), priced.prime(dial) + BusinessDebtManager.expectedLossSpread(.1), 1e-15);
        assertTrue("...which is still running", lender.getPrincipal(sound) > 0);

        /* ============ 3. WHAT IT PAYS, WHAT IT TAKES, WHAT IT KEEPS (0.7.7) ============

           The rest of the batch that took the premium out: savers are paid a
           share of the dial the bank's funding sets (3); its fees reach it
           from the households to the cent, and a loan's is its share of the
           lending (4); the window it funds at charges the dial plus a quarter
           of a point (5); and its owners are paid on its profit after tax,
           with what landed after a close carried into the next month once (6).
           ================================================================= */
        out.println("\n--- (3) a flush bank passes on less of the dial than one borrowing at the window ---");

        /*
         * CAUSED BOTH WAYS: one bank sitting on its capital as reserves, one
         * lent ten times past what its branch gathered - and each given the
         * interest to pay any rate it chooses, so rule 2 never answers for
         * rule 1. A hundred and fifty months at a 5% dial, which the step
         * closes to within a part in a trillion.
         */
        double p = .05;
        Bank flush = new Bank(), windowed = new Bank();
        flush.injectCapital(1_000_000);
        flush.refresh(1, 200_000, 0, 0, 0, 0);
        windowed.injectCapital(100_000);
        windowed.refresh(1, 200_000, 0, 10_000_000, 0, 0);
        windowed.startMonth();
        windowed.lend(10_000_000);
        for (int m = 0; m < 150; m++) {
            for (Bank b : new Bank[] { flush, windowed }) {
                b.startMonth();
                b.takeInterest(50_000);
                b.fundToCover(p);
            }
        }
        out.printf("   at a 5%% dial: flush pays %.3f%% (position %.2f), at the window %.3f%% (position %.2f)%n",
                flush.depositRate() * 100, flush.fundingPosition(),
                windowed.depositRate() * 100, windowed.fundingPosition());
        assertTrue("fixture: one is flush and the other at the window",
                flush.fundingPosition() == 0 && windowed.fundingPosition() == 1
                        && windowed.wholesaleFunding() > 0);
        assertTrue("fixture: neither was held by its margin", !flush.isDepositPayoutHeld()
                && !windowed.isDepositPayoutHeld());
        close("a bank flush with reserves settles at DEPOSIT_SHARE_FLUSH of the dial",
                flush.depositRate(), Bank.DEPOSIT_SHARE_FLUSH * p, 1e-9);
        close("...one borrowing at the window at DEPOSIT_SHARE_AT_WINDOW of it",
                windowed.depositRate(), Bank.DEPOSIT_SHARE_AT_WINDOW * p, 1e-9);
        assertTrue("...so the flush bank passes on less", flush.depositRate() < windowed.depositRate());

        double settled = flush.depositRate();
        flush.startMonth();
        flush.takeInterest(50_000);
        flush.fundToCover(.08);
        close("a new dial reaches savers a step a month, not at once",
                flush.depositRate() - settled,
                Bank.DEPOSIT_RATE_SPEED * (Bank.DEPOSIT_SHARE_FLUSH * .08 - settled), 1e-12);

        // ...never above the window, however high it was paying ...
        Bank eager = new Bank();
        eager.injectCapital(1_000_000);
        eager.refresh(1, 200_000, 0, 0, 0, 0);
        eager.setDepositRate(.50);
        eager.startMonth();
        eager.takeInterest(1_000_000);
        eager.fundToCover(p);
        close("a bank paying far over the window comes down to the window's rate, no higher",
                eager.depositRate(), p + CentralBank.WINDOW_PENALTY, 1e-12);
        // ...and never below nothing.
        Bank dry = new Bank();
        dry.injectCapital(1_000_000);
        dry.refresh(1, 200_000, 0, 0, 0, 0);
        dry.setDepositRate(.02);
        for (int m = 0; m < 150; m++) {
            dry.startMonth();
            dry.takeInterest(1_000);
            dry.fundToCover(0);
        }
        assertTrue("...and at a dial of nothing it pays nothing, and never less",
                dry.depositRate() >= 0 && dry.depositRate() < 1e-12);

        out.println("\n--- (4) the fees: the households' reach the bank to the cent, a loan's is its share ---");

        /*
         * THE ACCOUNT FEE COMES OUT OF THE HOUSEHOLDS' MONEY. Two balances
         * alike in everything - a thousand working households with the money
         * for their month - one charged the bank's fee and one not: the
         * charged one ends the month poorer by the fee on every household,
         * and that is the figure the bank takes and the books show.
         */
        double[][] town = new double[FamilyStructure.values().length][PayTier.values().length];
        town[FamilyStructure.COUPLE.ordinal()][0] = 1_000;
        java.util.function.ToDoubleBiFunction<FamilyStructure, PayTier> townCensus =
                (s, t) -> town[s.ordinal()][t.ordinal()];
        double[] wages = new double[HouseholdBalance.ROWS];
        double[] noBills = new double[HouseholdBalance.ROWS];
        double[] shop = new double[HouseholdBalance.ROWS];
        wages[0] = 1_000 * 3.0;
        shop[0] = 1_000 * .5;
        double fee = new Bank() {{ refresh(1, 0, 0, 0, 0, 0); }}.accountFee(1.2);
        HouseholdBalance feeless = new HouseholdBalance(), charged = new HouseholdBalance();
        charged.setAccountFee(fee);
        charged.setHousedShares(c -> 1);
        feeless.setHousedShares(c -> 1);
        for (HouseholdBalance hb : new HouseholdBalance[] { feeless, charged }) {
            hb.advanceMonth(townCensus, wages, 1.0, noBills, shop, .2, .05, 1);
        }
        close("a month's fee is ACCOUNT_FEE at the price index, in today's money",
                fee, Bank.ACCOUNT_FEE * 1.2, 1e-15);
        close("the households charged it end the month poorer by exactly the fee on every household",
                feeless.totalSavings() - charged.totalSavings(), fee * 1_000, 1e-9);
        close("...which is what the balance says they paid", charged.totalAccountFees(), fee * 1_000, 1e-9);
        double byRow = 0;
        for (double v : charged.accountFeesByRow(townCensus)) byRow += v;
        close("...and what the books were told they would pay, row by row, before they settled",
                byRow, charged.totalAccountFees(), 1e-9);
        HouseholdAccounts books0 = new HouseholdAccounts();
        books0.setAccountFees(charged.accountFeesByRow(townCensus));
        close("...on its own line of the household books", books0.getAccountFees(), charged.totalAccountFees(), 1e-9);
        Bank feeBank = new Bank();
        feeBank.startMonth();
        double feeCashBefore = feeBank.getCash();
        feeBank.takeAccountFees(charged.totalAccountFees());
        close("...and the bank's cash takes it to the cent", feeBank.getCash() - feeCashBefore, charged.totalAccountFees(), 1e-12);
        close("...as income on its Fees line", feeBank.feeIncome(), charged.totalAccountFees(), 1e-12);
        HouseholdBalance homeless = new HouseholdBalance();
        homeless.setAccountFee(fee);
        homeless.setHousedShares(c -> 0);
        homeless.advanceMonth(townCensus, wages, 1.0, noBills, shop, .2, .05, 1);
        close("a household with no home has no account to be charged for", homeless.totalAccountFees(), 0, 0);

        /*
         * ...AND A LOAN'S FEE IS ITS SHARE OF THE LENDING. A household's is
         * added to what it owes - caused with a row whose wage cannot cover
         * its rent, so it borrows every month once its savings are gone.
         */
        double[][] poor = new double[FamilyStructure.values().length][PayTier.values().length];
        poor[FamilyStructure.COUPLE.ordinal()][0] = 500;
        java.util.function.ToDoubleBiFunction<FamilyStructure, PayTier> poorCensus =
                (s, t) -> poor[s.ordinal()][t.ordinal()];
        double[] lean = new double[HouseholdBalance.ROWS];
        double[] eat = new double[HouseholdBalance.ROWS];
        lean[0] = 500 * 1.2;
        eat[0] = 1_000 * .25;
        HouseholdBalance borrowing = new HouseholdBalance();
        double drawn = 0, feesOwed = 0, worstFee = 0;
        for (int m = 0; m < 60; m++) {
            double owedBefore = borrowing.totalDebt();
            borrowing.advanceMonth(poorCensus, lean, 1.0, noBills, eat, .25, .05, 1);
            drawn += borrowing.totalBorrowed();
            feesOwed += borrowing.totalLoanFees();
            worstFee = Math.max(worstFee, Math.abs(borrowing.totalLoanFees() - Bank.LOAN_FEE * borrowing.totalBorrowed()));
        }
        out.printf("   a poor row drew $%,.1fk of credit and was charged $%,.2fk of fees on it%n", drawn, feesOwed);
        assertTrue("fixture: the households really did borrow", drawn > 0);
        close("a household's loan fee is LOAN_FEE of what it drew, every month", worstFee, 0, 1e-9);

        out.println("\n--- (5) the window charges the policy rate plus a quarter of a point ---");

        Bank atWindow = new Bank();
        atWindow.injectCapital(10_000);
        atWindow.refresh(1, 100_000, 0, 500_000, 0, 0);
        atWindow.startMonth();
        atWindow.lend(500_000);
        double owedTheWindow = atWindow.wholesaleFunding();
        atWindow.fundToCover(.04);
        assertTrue("fixture: it really is borrowing at the window", owedTheWindow > 0);
        close("the window's rate is the dial plus its penalty",
                atWindow.fundingRate(), .04 + CentralBank.WINDOW_PENALTY, 1e-15);
        close("...charged on what the bank owes the window, a month of it",
                atWindow.getFundingCost(), owedTheWindow * (.04 + CentralBank.WINDOW_PENALTY) / 12, 1e-9);
        close("...and the penalty is a quarter of a point: the Bank of Canada's Bank Rate is its target plus 25 basis points",
                CentralBank.WINDOW_PENALTY, .0025, 1e-15);

        out.println("\n--- (6) the bank's owners are paid on what the tax leaves, and on all of it ---");

        Bank taxed = new Bank();
        taxed.setProfitLastMonth(10_000);
        close("the profit the owners are paid a share of is after the tax", taxed.getProfitAfterTaxLastMonth(.25),
                10_000 - taxed.chargeTax(.25), 1e-9);
        close("...the same tax the city takes", taxed.getTaxPaid(), 2_500, 1e-9);
        Bank lossMaker = new Bank();
        lossMaker.setProfitLastMonth(-5_000);
        close("...and a loss is a loss, with no tax to take off it", lossMaker.getProfitAfterTaxLastMonth(.25), -5_000, 1e-12);

        /*
         * WHAT LANDS AFTER THE CLOSE. The desk's re-mark and the dividends on
         * what it holds move this bank's equity after closeMonth() - the
         * owners are paid and the shares trade below it in the month - and
         * until 0.7.7 they were never taxed or paid out. Caused here: a month
         * closed on its interest, then the desk re-marked and paid.
         */
        Bank late = new Bank();
        late.injectCapital(100_000);
        late.refresh(1, 1_000, 0, 0, 0, 0);
        late.startMonth();
        late.takeInterest(1_000);
        late.closeMonth();
        double struck = late.getProfitLastMonth();
        late.markSecurities(250);
        late.receiveDividend(50);
        close("what lands after the close is counted", late.lateProfit(), 300, 1e-9);
        close("...while the month's own statement shows it where it happened",
                late.profitBeforeTax(), struck + 300, 1e-9);
        late.startMonth();
        late.takeInterest(100);
        late.closeMonth();
        close("...and next month's taxed profit carries it", late.getProfitLastMonth(), 100 + 300, 1e-9);
        close("...once", late.getCarriedLate(), 300, 1e-9);
        late.startMonth();
        late.closeMonth();
        close("...and not again", late.getProfitLastMonth(), 0, 1e-9);

        theBankAsABusinessWithItsCapital();

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
            /*
             * THE DIAL UNDER THE WORLD'S RATE, since 0.7.0, so the carry
             * section below has a funding currency to find. It used to get one
             * for free: the city's paper was quoted CITY_DISCOUNT under the
             * dial, 1% against the world's 2%, and the carry trade was priced
             * off that. The floor is the policy rate itself now, so the
             * fixture causes the condition rather than inheriting it.
             */
            city.getDebtManager().setPolicyRate(DebtManager.MIN_POLICY_RATE);
            city.buildStack(template(city, "House"), 300, false);
            city.buildStack(template(city, "Convenience Store"), 6, false);
            city.buildStack(template(city, "Industrial Bakery"), 2, false);
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

        /*
         * FOUR BOOKS SINCE THE CARRY TRADE (2026-09-12), and the fourth is the
         * only one with no debt object behind it: what foreigners have borrowed
         * to take abroad. It has to be IN getBook(), because equity is cash plus
         * book, and a loan that took cash out and credited nothing would have
         * cut the bank's capital by the whole principal and resolved it on the
         * first foreign borrower.
         *
         * This fixture causes one rather than hoping for it - the dial is set
         * to its floor above, which puts the city under the world's rate and
         * makes it the funding currency the trade needs.
         */
        assertTrue("fixture: somebody abroad has actually borrowed",
                live.getCarryBook() > 0);
        close("the bank's book is every loan in the city, plus what left it",
                live.getBook(),
                live.getSectorBook() + live.getCityBook() + live.getHouseholdBook()
                        + live.getCarryBook(), 1e-9);
        close("...and the carry book IS the stock that owns it",
                live.getCarryBook(), city.getCapitalFlows().getCarryStock(), 1e-9);

        close("the sector book IS the business lender's principal",
                live.getSectorBook(),
                city.getEconomyManager().getBusinessDebtManager().getAllPrincipal(), 1e-6);
        // The DOMESTIC principal since 0.7.0: dollar paper is held abroad, and
        // this line used to assert the bank held it too - see Game.refreshBank().
        close("the city book IS the treasury's principal at home",
                live.getCityBook(), city.getDebtManager().getDomesticPrincipal(), 1e-6);
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
         * The live path sets the bank's prices at the top of every month - the
         * premium until 0.7.7, the floor under the city's paper and prime
         * since - which the load path never reaches, so it sets them itself:
         * before it did, a player could save at a strained bank, reload, and
         * be quoted the clean curve for the loan the game had just priced
         * dearly.
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

        out.printf("   a small unbanked city: %d branch(es), prime %.2f%% on a dial of %.2f%%%n",
                branchesBuilt,
                unattended.getBank().prime(unattended.getDebtManager().getPolicyRate()) * 100,
                unattended.getDebtManager().getPolicyRate() * 100);
        BusinessInvestment.Decision wanted = unattended.getBusinessInvestment().planBank();
        out.printf("   the advisor wants one: %s - \"%s\"%n", wanted.build, wanted.reason);
        out.printf("   ...and could not have it: %s%n", unattended.getLastInvestment("Bank"));

        /*
         * THE ADVISOR ASKS EVEN WHEN IT CANNOT PAY, and the two halves are
         * asserted separately on purpose.
         *
         * A town of four hundred houses has a loan book of about $2M and a
         * branch costs $9M, so a branch genuinely does not pay for itself there
         * - the eighteen points it would have saved until 0.7.7 were eighteen
         * points of nearly nothing. That is the right answer and this fixture
         * does not argue with it. What WOULD be wrong is the advisor never noticing, so what is
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
        /*
         * ...AND WHAT IT SAYS ABOUT THE CREDIT IS TRUE (0.7.8). With no bank
         * the reason said credit was "at the punitive rate", which it has
         * not been since the premium went (0.7.7): a city with no branch is
         * lent the window's money at its price, and the words say so. Asked
         * only while the advisor is giving the no-bank reason - the states
         * above where it has already acted read differently, as they should.
         */
        if (wanted.build && unattended.getBank().getBranches() <= 0) {
            assertTrue("...and a city with no bank is told its credit is the window's, not punitive",
                    wanted.reason.toLowerCase().contains("credit")
                            && wanted.reason.toLowerCase().contains("window")
                            && !wanted.reason.toLowerCase().contains("punitive"));
        }

        /* ---- and a city that can pay for one, does ---- */

        Path solvent = Files.createTempDirectory("bankcheck-solvent");
        Game trading = new Game(new GameFiles(solvent.resolve("data"), solvent.resolve("no-legacy")));
        System.setOut(quiet);
        int builtWhenAffordable;
        double windowShareWhenNew;
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
            trading.buildStack(template(trading, "Bakery"), 1, true);
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
            trading.getEconomyManager().setSectorCash(Sectors.RETAIL, 250_000);
            trading.getLandManager().setOwnedSqFt(8_000_000);
            /*
             * STEPPED, AND READ WHEN THE BRANCH IS NEW (2026-09-17).
             *
             * This ran sixty months in one call and read the premium at the
             * end of them, which is four years after the question it is
             * asking. The question was "does opening a branch stop the
             * punitive premium" - since 0.7.7, when there is no premium, "does
             * the city's money stop being the window's"; what the last month
             * actually reports is whether the bank is still standing in year
             * seven, and in a city this deliberately marginal that is a
             * different and much harder claim.
             *
             * It came apart on the van batch and the cause is worth keeping:
             * MATERIALS built its first plant, had to buy a FLEET as well as
             * the plant, borrowed $57,939 from a bank with $50,292 of equity -
             * thirty-eight per cent of the whole book in one name - and
             * defaulted in month 70. The bank's equity went to minus $2,628
             * and it paid the punitive premium from then on.
             *
             * NEITHER HALF OF THAT IS A BUG IN THE BANK. A sector that has to
             * buy lorries as well as machines is a riskier borrower, which is
             * the mechanic; and a lender with no concentration limit will one
             * day put a third of its book in one name, which is a real hole
             * and its own batch. What is wrong is a fixture that asserts the
             * first thing and measures the second.
             *
             * So it is read six months after the branch opens - long enough
             * for the capital to be raised and the funding to settle, short
             * enough to still be about the branch.
             */
            int opened = -1;
            double share = Double.NaN;
            for (int m = 0; m < 60; m++) {
                trading.simulateMonths(1);
                if (opened < 0 && trading.getBuildingManager().countByName("Commercial Bank") >= 1) {
                    opened = m;
                }
                if (opened >= 0 && m == opened + 6) share = trading.getBank().windowShare();
            }
            windowShareWhenNew = Double.isNaN(share) ? trading.getBank().windowShare() : share;
            builtWhenAffordable = trading.getBuildingManager().countByName("Commercial Bank");
        } finally {
            System.setOut(out);
        }

        out.printf("   a city whose shops can pay for one: %d branch(es), %.0f%% of its money the window's"
                + " six months in (%.0f%% at the end of the run)%n",
                builtWhenAffordable, windowShareWhenNew * 100,
                trading.getBank().windowShare() * 100);
        out.printf("   the advisor's last word on it: %s%n", trading.getLastInvestment("Bank"));

        assertTrue("a city that can afford a branch opens one, unprompted",
                builtWhenAffordable >= 1);
        // It asserted "...and stops paying the punitive premium once it has"
        // until 0.7.7; a city with no bank is priced as though its money were
        // the window's, and one with a bank prices off its own funding.
        assertTrue("...and once it has, its money is its own and not all the window's",
                windowShareWhenNew < 1);

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
        double worstBalance = 0, worstArticulation = 0, worstDesk = 0;
        int worstMonth = 0, deskMonths = 0, reMarkLosses = 0;
        // ...and 0.7.7's, on the same played city: the fees, the audit they
        // cross, the owners' dividend and the profit that lands after a close.
        double worstAccountFee = 0, worstLoanFee = 0, worstAudit = 0, worstDividend = 0;
        int feeMonths = 0, loanFeeMonths = 0, dividendMonths = 0, lateMonths = 0;
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
            books.buildStack(template(books, "Bakery"), 1, true);
            books.buildStack(template(books, "Paved Road"), 20, true);
            books.buildStack(template(books, "Industrial Bakery"), 2, true);
            books.buildStack(template(books, "Construction Depot"), 4, true);
            books.buildStack(template(books, "Coal Power Plant"), 1, true);
            books.buildStack(template(books, "Water Treatment Plant"), 1, true);
            books.simulateMonths(24);
            books.getEconomyManager().setSectorCash(Sectors.RETAIL, 250_000);
            books.getEconomyManager().setSectorCash(Sectors.INDUSTRY, -20_000);

            Bank kept = books.getBank();
            /*
             * ...AND RESERVES, so it can pay its owners (0.7.7). A bank's
             * dividend is paid out of its cash (Game.payDividends()), and its
             * cash is its net funding position - this one's ran $40-280k
             * short for all 120 months, so it never paid one and (6) below
             * asserted nothing. A million of shareholders' capital, put in
             * between two months where the articulation is not looking,
             * causes the condition.
             */
            kept.injectCapital(1_000_000);
            for (int m = 0; m < 120; m++) {
                // The profit the NEXT month's tax will be charged on - struck
                // at closeMonth(), charged at the top of the month after.
                profitTaxedNextMonth = kept.getProfitLastMonth();
                books.simulateMonths(1);

                // A = L + E, every month, no exceptions.
                double balance = kept.totalAssets() - kept.totalLiabilities() - kept.equity();
                if (Math.abs(balance) > Math.abs(worstBalance)) worstBalance = balance;

                // ...and the desk's statement, line by line, every month it
                // did anything - see "the desk foots" below for the identity.
                double deskGap = kept.getTradingIncome() - deskParts(books) - kept.getMarkChange();
                if (Math.abs(deskGap) > Math.abs(worstDesk)) worstDesk = deskGap;
                if (Math.abs(kept.getTradingIncome()) > 1e-9 || Math.abs(kept.getMarkChange()) > 1e-9) {
                    deskMonths++;
                    if (kept.getTradingIncome() < 0
                            && kept.getMarkChange() <= kept.getTradingIncome() / 2) reMarkLosses++;
                }

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
                // ...and by the dividend it paid its owners, since 2026-09-10
                // (evening): cash out, equity down, and its own line - see
                // Bank.payDividend(). And since 0.7.8 by its own shares, bought
                // back or issued: capital, not the desk's trading - see
                // capitalMoved().
                double explained = kept.getNetIncome() + capitalMoved(kept);
                if (Math.abs(moved - explained) > Math.abs(worstArticulation)) {
                    worstArticulation = moved - explained;
                    worstMonth = books.getMonth();
                }

                /*
                 * (4) THE ACCOUNT FEES ARRIVE TO THE CENT, and the loan fees
                 * are their share: what the households' books show they paid
                 * is what the bank took; a business's fee is LOAN_FEE of what
                 * it was lent; and the month's money audit closes with both
                 * crossing it.
                 */
                double shownPaid = books.getHouseholds().getAccountFees();
                if (kept.getAccountFees() > 0) feeMonths++;
                worstAccountFee = Math.max(worstAccountFee, Math.abs(kept.getAccountFees() - shownPaid));
                double lentNow = books.getEconomyManager().getBusinessDebtManager().getLentThisMonth();
                if (lentNow > 0) {
                    loanFeeMonths++;
                    worstLoanFee = Math.max(worstLoanFee, Math.abs(kept.getLoanFeesPaid() - lentNow * Bank.LOAN_FEE));
                }
                MoneyAudit.Result month = books.getLastMoneyAudit();
                if (month != null) worstAudit = Math.max(worstAudit, month.relative());

                /*
                 * (6) THE OWNERS ARE PAID ON WHAT THE TAX LEAVES: in a month
                 * the bank paid a dividend, it was paid on last month's
                 * profit after the city's share of it - never the profit
                 * before tax, which is what it was until 0.7.7 - and by the
                 * bank's own rule since 0.7.8 (section 10): PAYOUT_IN_BAND of
                 * that profit and a twelfth of what it held over the top of
                 * its band, never more than its room over its target, to the
                 * cent and not capped at its cash. Until 0.7.8 this asserted
                 * Equity.PAYOUT (40%) or less when the cash was short, the
                 * rule this batch replaces for the bank.
                 */
                double taxRate = books.getEconomyManager().getTaxPolicy().effectiveProfitRate(Sectors.RETAIL);
                if (kept.getDividendsPaid() > 0) {
                    dividendMonths++;
                    worstDividend = Math.max(worstDividend,
                            Math.abs(kept.getPayoutProfit() - kept.getProfitAfterTaxLastMonth(taxRate)));
                    double due = Math.min(kept.getPayoutOverTarget(),
                            Bank.PAYOUT_IN_BAND * Math.max(0, kept.getPayoutProfit())
                                    + kept.getPayoutExcess() / Bank.EXCESS_PAYOUT_MONTHS);
                    worstDividend = Math.max(worstDividend, Math.abs(kept.getDividendsPaid() - due));
                }
                if (Math.abs(kept.getCarriedLate()) > 1e-9) lateMonths++;
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

        out.printf("   fees in %d of 120 months, new business loans in %d, a dividend in %d,"
                + " profit carried past a close in %d; worst audit residual %.2e%n",
                feeMonths, loanFeeMonths, dividendMonths, lateMonths, worstAudit);
        assertTrue("fixture: the played city's households paid account fees", feeMonths > 0);
        close("(4) what the households' books show they paid in account fees is what the bank took, to the cent",
                worstAccountFee, 0, .005);
        assertTrue("fixture: the played city's businesses borrowed", loanFeeMonths > 0);
        close("...a business loan's fee is LOAN_FEE of what was lent, every month it lent",
                worstLoanFee, 0, 1e-9);
        assertTrue("...and the money audit closes with the fees crossing it", worstAudit < 1e-4);
        assertTrue("fixture: the bank paid its owners", dividendMonths > 0);
        close("(6) the bank's dividend is its payout rule on its profit after tax, every month it paid",
                worstDividend, 0, 1e-6);
        assertTrue("...and what lands after a close reaches the next month's taxed profit",
                deskMonths == 0 || lateMonths > 0);

        /* ---- and the city takes its share ---- */

        /*
         * Jerus: "quick question banks are taxed right?"
         *
         * Its staff always were. Its profits were not by anybody, which took a
         * bank with a full set of financial statements and left it the only
         * business in the city paying no profit tax.
         */
        double bankRate = books.getEconomyManager().getTaxPolicy()
                .effectiveProfitRate(Sectors.RETAIL);
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
        /*
         * OUT OF WHAT IT EARNS - which is the rule chooseDepositRate() actually
         * enforces, and the one this line was a proxy for.
         *
         * It read `depositRate() < debtManager.getRate()`: pays savers less
         * than the SOVEREIGN rate. That is not a rate any of its borrowers pay,
         * and it held only for as long as the fixture's bank was losing money
         * and paying savers almost nothing. Give the same bank a healthier
         * world and it turns a profit, pays 1.18% against a 1.00% sovereign,
         * and this failed on a bank doing nothing wrong.
         *
         * A bank whose reserves earn a rate of their own - the policy rate at
         * the central bank since 0.7.0, the world's rate before it - can pay its
         * savers more than it charges its own borrowers and still make money,
         * because lending is not where its income is coming from. That is a
         * real bank and there are several. What it can NEVER do is pay out more
         * than it took in, and that is what chooseDepositRate() is written to
         * guarantee - "never more than leaves the bank at net zero". So that is
         * what this asks.
         */
        assertTrue("...and never more than it earned",
                shown.depositInterest() <= shown.interestIncome() + 1e-9);
        assertTrue("...and its staff are on its own books, not the shops'",
                shown.operatingExpenses() > 0);

        /* ============ 8b. the trading desk's statement foots ============ */
        out.println("\n--- and the desk's statement adds up to its total ---");

        /*
         * Jerus: "the bank, just explain to me the trading desk, cause a bunch
         * of times it's losing billions of dollars due to the trading desk."
         *
         * The bank statement opens "The trading desk" into what it sold, what
         * it bought, the dividends on what it holds and what it tendered into
         * buybacks - and until 2026-09-18 those did not add up to the figure
         * above them, because the biggest term was missing: the change in the
         * mark, which Bank.markSecurities() adds to tradingIncome and nothing
         * showed. Bank.getMarkChange() is that term, kept by the model rather
         * than backed out on the screen, and this is the identity:
         *
         *   tradingIncome = sold to households + sold abroad
         *                 - bought from households - bought from abroad
         *                 + dividends on the inventory + tendered into buybacks
         *                 + re-marked what it holds
         *
         * On a fixture that trades - ExchangeCheck's leavers, borrowed whole:
         * twenty households emigrate and the desk buys their two thousand
         * shares at the bid, then re-marks them at the closing quote. Every
         * term but two is zero there, so the re-mark is read against the
         * model's own quote and not just against a subtraction.
         */
        HouseholdBalance leavers = ExchangeCheck.savers(20.0, 4.0);
        int RETAIL = Equity.indexOf(Sectors.RETAIL);
        Equity register = new Equity();
        register.listIfUnlisted(RETAIL, 10_000, leavers);
        double[][] fewer = new double[FamilyStructure.values().length][PayTier.values().length];
        fewer[FamilyStructure.COUPLE.ordinal()][PayTier.UNSKILLED.ordinal()] = 80;
        double[] income = new double[HouseholdBalance.ROWS];
        income[PayTier.UNSKILLED.ordinal()] = 80 * 4.0;
        double[] nothing = new double[HouseholdBalance.ROWS];
        leavers.advanceMonth((s, t) -> fewer[s.ordinal()][t.ordinal()], income, 0, nothing, nothing, .25, .05, 1);
        register.followEmigrants(leavers);

        Exchange desk = new Exchange();
        Bank dealer = ExchangeCheck.bankWith(10_000);
        double[] bookValue = new double[Equity.COMPANIES.length];
        bookValue[RETAIL] = 10_000;                          // $1 a share
        desk.startMonth();
        desk.takeMonth(register, leavers, dealer, new ExchangeCheck.Firms(), bookValue,
                DebtManager.WORLD_BASE_RATE, 0);

        double bid = 1.0 * (1 - Exchange.SPREAD / 2);
        double closingMid = 1.0 * (1 - Exchange.PRESSURE * 2_000 / desk.limit(RETAIL));
        assertTrue("fixture: the desk traded", desk.getBoughtFromAbroad(RETAIL) > 0);
        close("the re-mark is the inventory at the closing quote, from nothing",
                dealer.getMarkChange(), 2_000 * closingMid, 1e-9);
        close("...and the desk's total is what it paid against that re-mark",
                dealer.getTradingIncome(), -2_000 * bid + dealer.getMarkChange(), 1e-9);
        close("the opened lines and the re-mark sum to the total, exactly",
                deskParts(desk, register, dealer) + dealer.getMarkChange(),
                dealer.getTradingIncome(), 1e-9);

        // And the field is a flow: the next month opens with it at nothing.
        dealer.startMonth();
        close("the re-mark is cleared with the trading result at the top of a month",
                dealer.getMarkChange(), 0, 1e-12);

        /*
         * ON THE PLAYED CITY, every month of the twelve years above - a real
         * desk with real trades, dividends and buybacks in the same month.
         * Counted, because a mechanic that fires on a fixture and never in a
         * run is the classic way this suite stops testing anything.
         */
        out.printf("   the played city's desk did something in %d of 120 months; in %d of them"
                + " it lost money and the re-mark was at least half the loss%n",
                deskMonths, reMarkLosses);
        assertTrue("fixture: the played city's desk actually traded", deskMonths > 0);
        close("...and its opened lines and re-mark summed to its total every month",
                worstDesk, 0, 1e-6);

        /* ============ 12. a reload reads the same month (0.7.8) ============

           The bank's month is flows, and until 0.7.8 none of its statement
           lines was saved: a reloaded city's Income page read zeroes until a
           month was played. With them saved - and the allowance, and the
           record the capital target is struck from - the played city of
           section 8, saved between two months and loaded again, reads the
           same statement, the same allowance and the same target. And a save
           from before 0.7.8, which carries none of the three, loads with an
           allowance set up from its borrowers and no provision booked for it:
           its equity is lower by what it set aside, and the next month's
           statement and money audit are an ordinary month's.
           ================================================================= */
        out.println("\n--- (12) a reload between two months reads the same income lines, allowance and target ---");

        GameFiles booksFiles = new GameFiles(booksRoot.resolve("data"), booksRoot.resolve("no-legacy"));
        Game booksBack, old077;
        System.setOut(quiet);
        try {
            books.saveGame(4, "the books city");
            booksBack = new Game(booksFiles);
            booksBack.loadGameSave(4);
            // ...and the same save as a 0.7.7 one: the three keys taken out.
            Path saved = booksFiles.saveFile(4);
            com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(Files.readString(saved)).getAsJsonObject();
            // (and 0.7.9's year of statements, which no 0.7.7 save carried either,
            // nor 0.7.8's quarter of readings the bank rates a borrower on)
            for (String key : new String[]{ "bankAllowance", "bankCapitalRecord", "bankMonthLines",
                    "bankStatementYear", "creditStatements" }) json.remove(key);
            Files.writeString(saved, new com.google.gson.Gson().toJson(json));
            old077 = new Game(booksFiles);
            old077.loadGameSave(4);
        } finally {
            System.setOut(out);
        }
        Bank again = booksBack.getBank(), opened = old077.getBank(), livedBank = books.getBank();
        out.printf("   saved with $%,.2fk set aside, a target of %.2f%%, a provision of $%,.2fk and $%,.2fk kept this month%n",
                livedBank.getAllowance(), livedBank.capitalTarget() * 100, livedBank.provisions(), livedBank.getNetIncome());
        assertTrue("fixture: the played bank had set something aside", livedBank.getAllowance() > 0);
        close("the allowance reloads to the cent", again.getAllowance(), livedBank.getAllowance(), 1e-9);
        close("...the capital target it chose", again.capitalTarget(), livedBank.capitalTarget(), 1e-15);
        close("...the month's provision", again.provisions(), livedBank.provisions(), 1e-9);
        close("...its interest, its fees and its profit, as the Profit page reads them",
                again.interestIncome() + again.feeIncome() + again.profitBeforeTax(),
                livedBank.interestIncome() + livedBank.feeIncome() + livedBank.profitBeforeTax(), 1e-9);
        close("...what it kept, and what it paid its owners", again.getNetIncome() - again.getDividendsPaid(),
                livedBank.getNetIncome() - livedBank.getDividendsPaid(), 1e-9);
        boolean linesSame = java.util.Arrays.equals(again.monthLinesToSave(), livedBank.monthLinesToSave())
                && java.util.Arrays.equals(again.capitalRecordToSave(), livedBank.capitalRecordToSave());
        java.util.Map<String, double[]> keptBooks = livedBank.allowanceToSave(), againBooks = again.allowanceToSave();
        boolean booksSame = keptBooks.keySet().equals(againBooks.keySet());
        for (String k : keptBooks.keySet()) if (booksSame) booksSame = java.util.Arrays.equals(keptBooks.get(k), againBooks.get(k));
        assertTrue("...every statement line and the target's record, exactly", linesSame);
        assertTrue("...and the allowance, book by book", booksSame);

        out.printf("   the same save as a 0.7.7 one: $%,.2fk set up on load, a provision of $%,.2fk%n",
                opened.getAllowance(), opened.provisions());
        assertTrue("a save from before 0.7.8 loads with an allowance set up from its borrowers",
                opened.getAllowance() > 0);
        /*
         * THE SAME RULE ON THE SAME BORROWERS, as the load reads them (0.7.8).
         * This compared the whole allowance with the saved bank's, within a
         * hundredth - which held while a sound book's allowance was a flat
         * BASE_LOSS_RATE and never read the borrower's assets. On the curve
         * (Bank.sectorAllowance()) a book near or past the watch line reads
         * them, and the load reads them at the month's end (Game's load path
         * strikes them again), not at the month's check: this city's
         * Materials sits at about 1.48 times its assets - where until 0.7.8
         * it would have been written down whole - and its assets moved 1.2%
         * between the check and the save. So the two halves are asserted as
         * what they are: the rule, exactly, on the borrowers as the load
         * reads them; and, book by book, the saved bank's own allowance
         * wherever the curve does not read the borrower's assets.
         */
        BusinessDebtManager creditAtLoad = old077.getEconomyManager().getBusinessDebtManager();
        double byRule = old077.getHouseholdBalance().lossAllowance();
        java.util.Map<String, double[]> livedBooks = livedBank.allowanceToSave(), openedBooks = opened.allowanceToSave();
        double worstFlat = 0;
        int flatBooks = 0;
        for (String k : creditAtLoad.sectors()) {
            double owedNow = creditAtLoad.getPrincipal(k), assetsNow = creditAtLoad.getAssets(k);
            byRule += Bank.sectorAllowance(owedNow, assetsNow);
            // A book the curve does not read the assets of, by the load's own
            // reading: even a loan's term of its defaults under BASE_LOSS_RATE,
            // so a year and a lifetime are both BASE_LOSS_RATE, whatever the
            // share past the watch line (staged, round 2).
            boolean flat = owedNow > 0 && assetsNow > 0
                    && BusinessDebtManager.LOSS_GIVEN_DEFAULT * BusinessDebtManager.defaultProbability(
                            owedNow / assetsNow, BusinessDebtManager.LOAN_TERM_MONTHS) < Bank.BASE_LOSS_RATE;
            if (flat) {
                flatBooks++;
                double openedBook = openedBooks.containsKey(k) ? openedBooks.get(k)[0] : 0;
                double livedBook = livedBooks.containsKey(k) ? livedBooks.get(k)[0] : 0;
                worstFlat = Math.max(worstFlat, Math.abs(openedBook - livedBook));
            }
        }
        close("...by the same rule on the same borrowers, as the load reads them",
                opened.getAllowance(), byRule, 1e-9);
        assertTrue("fixture: some of its books are sound ones, held at BASE_LOSS_RATE", flatBooks > 0);
        close("...which, wherever the curve does not read the borrower's assets, is the saved bank's to the cent",
                worstFlat, 0, 1e-9);
        close("...booked as no month's provision", opened.provisions(), 0, 1e-12);
        close("...so its equity is lower by what it set aside, and by nothing else",
                opened.equity(), livedBank.equity() + livedBank.getAllowance() - opened.getAllowance(), 1e-6);
        double articulatedOld, provisionOld, provisionNew, allowanceAtLoad = opened.getAllowance();
        System.setOut(quiet);
        try {
            old077.getForeignAccounts().pinRate(1.0);
            booksBack.getForeignAccounts().pinRate(1.0);
            old077.simulateMonths(1);
            booksBack.simulateMonths(1);
        } finally {
            System.setOut(out);
        }
        provisionOld = opened.provisions();
        provisionNew = again.provisions();
        articulatedOld = opened.equity() - opened.getOpeningEquity() - opened.getNetIncome() - capitalMoved(opened);
        out.printf("   its next month's provision $%,.2fk against $%,.2fk for the city that saved one%n",
                provisionOld, provisionNew);
        close("...and its next month articulates like any other", articulatedOld, 0, .005);
        close("...with the provision the saved city made, within a hundredth of the allowance - not the allowance again",
                provisionOld, provisionNew, Math.abs(allowanceAtLoad - livedBank.getAllowance()) + .01 * allowanceAtLoad);
        assertTrue("...and a money audit that closes", Math.abs(old077.getLastMoneyAudit().residual) < .01);

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
        assertTrue("...and it says what it would take to fix",
                bust.recapitalisationNeeded() > 0);
        /*
         * A FAILED BANK'S MONEY IS THE WINDOW'S - which its loans are priced
         * off, and nothing more. It asserted "...so every borrower in the city
         * pays the full premium" until 0.7.7. The same bank, failed and closed.
         */
        Bank failed = new Bank();
        failed.injectCapital(80_000);
        failed.refresh(100, 10_000_000, 0, 0, 0, 0);
        failed.lend(200_000);
        failed.refresh(100, 10_000_000, 0, 100_000, 0, 0);
        failed.startMonth();
        failed.resolveIfFailed();
        failed.closeMonth();
        close("a failed bank prices off the window: its money is all the window's",
                failed.windowShare(), 1, 0);
        close("...and its prime is the four parts on it, not a premium on every rate",
                failed.prime(.03), failed.fundsTransferPrice(.03, Bank.PRIME_TERM_MONTHS)
                        + failed.runningCostRate() + failed.expectedLossRate()
                        + failed.capitalCharge(.03, Bank.PRIME_TERM_MONTHS, Bank.RISK_BUSINESS), 1e-15);

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

        theBankPaysForTheCitysPaper();

        whatTheBankTabReads();

        theSectorDefaultsASliceAtATime();

        aFailingSectorsPlantIsSoldToTheBuilders();
        theBankReadsTheQuarter();
        theDeskIsHeldToTheBanksCapital();

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ============ 14. A SECTOR DEFAULTS A SLICE AT A TIME (0.7.8) ============

       Jerus, 2026-09-23: "go for option C". A sector stands for many firms:
       each month the share of its debt whose firms fell through the default
       point defaults - PD(L) = N(ln(L / INSOLVENCY_TRIGGER) / ASSET_VOLATILITY)
       a year, its monthly hazard a month - and the bank loses
       LOSS_GIVEN_DEFAULT of it. The whole-sector restructure is the backstop
       for a sector with nothing left. Every fixture below causes its
       condition through BusinessDebtManager or a played city, and every
       expectation is written from the constants, with the curve's
       arithmetic done here independently of the model's (Math.pow, not its
       log1p), so the two have to agree.
       ======================================================================= */

    /**
     * The allowance the round-2 brief writes down, staged firm by firm,
     * computed here rather than by the model: principal x ((1 - s2) x EL12 +
     * s2 x ELlife), s2 = N(ln(L / SECTOR_WATCH_LEVERAGE) / ASSET_VOLATILITY),
     * EL12 = max(BASE_LOSS_RATE, LGD x PD), ELlife = max(EL12, LGD x (1 - (1 -
     * PD)^(term / 12))) - the lifetime with Math.pow, not the model's log1p.
     */
    static double staged(double principal, double leverage) {
        double sig = BusinessDebtManager.ASSET_VOLATILITY, lgd = BusinessDebtManager.LOSS_GIVEN_DEFAULT;
        double pd = BusinessDebtManager.normalCdf(Math.log(leverage / BusinessDebtManager.INSOLVENCY_TRIGGER) / sig);
        double year = Math.max(Bank.BASE_LOSS_RATE, lgd * pd);
        double life = Math.max(year, lgd * (1 - Math.pow(1 - pd,
                BusinessDebtManager.LOAN_TERM_MONTHS / (double) BusinessDebtManager.DEFAULT_HORIZON_MONTHS)));
        double s2 = BusinessDebtManager.normalCdf(Math.log(leverage / Bank.SECTOR_WATCH_LEVERAGE) / sig);
        return principal * ((1 - s2) * year + s2 * life);
    }

    /** The monthly hazard the brief writes down, 1 - (1 - PD)^(1/12), computed here rather than by the model. */
    static double hazard(double leverage) {
        double pd = BusinessDebtManager.normalCdf(Math.log(leverage / BusinessDebtManager.INSOLVENCY_TRIGGER)
                / BusinessDebtManager.ASSET_VOLATILITY);
        return 1 - Math.pow(1 - pd, 1.0 / BusinessDebtManager.DEFAULT_HORIZON_MONTHS);
    }

    /** A lender with one sector owing these loans against these assets. */
    static BusinessDebtManager lender(String sector, double assets, double... loans) {
        BusinessDebtManager m = new BusinessDebtManager();
        m.setPrimeRate(.05);
        for (double l : loans) m.issueLoan(sector, l, 1);
        m.setAssets(sector, assets);
        m.updateRates();
        m.startAuditMonth();
        return m;
    }

    static void theSectorDefaultsASliceAtATime() throws Exception {

        String IND = Sectors.INDUSTRY;
        double T = BusinessDebtManager.INSOLVENCY_TRIGGER, LGD = BusinessDebtManager.LOSS_GIVEN_DEFAULT;
        double watch = Bank.SECTOR_WATCH_LEVERAGE;

        out.println("\n--- (14) the normal curve, and what it gives ---");

        // The exact values, from erfc to seventeen figures.
        close("N(0) is a half", BusinessDebtManager.normalCdf(0), .5, 0);
        close("N(1.96) is 0.975", BusinessDebtManager.normalCdf(1.96), 0.9750021048517795, 1e-14);
        close("N(-1) is 0.1587", BusinessDebtManager.normalCdf(-1), 0.15865525393145707, 1e-14);
        close("N(-3) is 0.00135, to the tail's own figures", BusinessDebtManager.normalCdf(-3),
                0.0013498980316300957, 1e-16);
        double worstSym = 0;
        for (double x = 0; x <= 12; x += .01) {
            worstSym = Math.max(worstSym, Math.abs(BusinessDebtManager.normalCdf(x)
                    + BusinessDebtManager.normalCdf(-x) - 1));
        }
        close("...and N(x) + N(-x) is one", worstSym, 0, 1e-15);
        close("the loss given default is the restructure rule's: 1 - RESTRUCTURE_TARGET / INSOLVENCY_TRIGGER",
                LGD, 1 - BusinessDebtManager.RESTRUCTURE_TARGET / T, 0);
        close("at the default point half its firms default in a year",
                BusinessDebtManager.defaultProbability(T), .5, 1e-15);

        out.printf("   at ASSET_VOLATILITY %.2f:%n", BusinessDebtManager.ASSET_VOLATILITY);
        out.println("     leverage   default a year   a month   lost a year, of its debt");
        for (double L : new double[] {.3, .5, .7, .9, 1.1, 1.3, 1.5, 2.0, 3.0}) {
            double pd = BusinessDebtManager.defaultProbability(L);
            out.printf("       %.2f        %6.2f%%        %6.2f%%       %6.2f%%%n", L, pd * 100,
                    BusinessDebtManager.monthlyDefaultShare(L) * 100, LGD * pd * 100);
        }

        out.println("\n--- (14) a sector held at a leverage loses its defaulted firms' slice, pro rata ---");

        double L = 1.2;
        BusinessDebtManager held = lender(IND, 40_000 / L, 30_000, 10_000);
        double h = hazard(L);
        close("fixture: the lender's hazard is the brief's, 1 - (1 - PD)^(1/12)",
                BusinessDebtManager.monthlyDefaultShare(L), h, 1e-15);
        double off = held.restructureInsolventSectors();
        out.printf("   at %.2f times its assets: %.3f%% of its debt defaults this month, %,.2f written off%n",
                L, h * 100, off);
        close("a sector at leverage L writes off principal x h(L) x LOSS_GIVEN_DEFAULT in the month",
                held.getWrittenOffThisMonth(IND), 40_000 * h * LGD, 1e-9);
        close("...which is what the month's sweep returns", off, 40_000 * h * LGD, 1e-9);
        close("...and the debt that defaulted is h of it", held.getDefaultedThisMonth(IND), 40_000 * h, 1e-9);
        java.util.List<BusinessDebt> loans = held.getLoans(IND);
        close("...every loan falls pro rata: the first", loans.get(0).getOutstandingPrincipal(),
                30_000 * (1 - h * LGD), 1e-9);
        close("...and the second", loans.get(1).getOutstandingPrincipal(), 10_000 * (1 - h * LGD), 1e-9);
        close("...its assets are untouched - the firms keep their plant", held.getAssets(IND), 40_000 / L, 0);
        assertTrue("...it is not restructured, not on its record and not shut out",
                !held.wasRestructuredThisMonth(IND) && held.getRestructureCount(IND) == 0
                        && !held.isBorrowingBlocked(IND));
        // (Here at 1.0, as round 1 had it: at 1.2 the old leverage spread sat at
        // its MAX_SPREAD cap, which the curve no longer has.)
        BusinessDebtManager quoted = lender(IND, 10_000, 10_000);
        double rateBefore = quoted.getRate(IND);
        quoted.restructureInsolventSectors();
        assertTrue("...and owing less against the same assets, its next loan is quoted less",
                quoted.getWrittenOffThisMonth(IND) > 0 && quoted.getRate(IND) < rateBefore);
        // ...and held there a year: the share of its debt that defaulted is PD(L).
        double survived = 1 - held.getDefaultShareThisMonth(IND);
        for (int m = 2; m <= BusinessDebtManager.DEFAULT_HORIZON_MONTHS; m++) {
            held.setAssets(IND, held.getPrincipal(IND) / L);
            held.restructureInsolventSectors();
            survived *= 1 - held.getDefaultShareThisMonth(IND);
        }
        close("held a year at a leverage, the share of its debt that defaulted is PD(L)",
                1 - survived, BusinessDebtManager.defaultProbability(L), 1e-12);

        out.println("\n--- (14) more defaults the deeper in debt, and nothing past dust under the watch line ---");

        boolean monotone = true;
        double lastPd = -1, lastH = -1, worstUnderWatch = 0, newsFrom = Double.NaN;
        double hAtT = BusinessDebtManager.monthlyDefaultShare(T);
        for (int i = 1; i <= 600; i++) {
            double lev = i / 100.0;
            double pd = BusinessDebtManager.defaultProbability(lev), hm = BusinessDebtManager.monthlyDefaultShare(lev);
            if (pd < lastPd || hm < lastH) monotone = false;
            lastPd = pd; lastH = hm;
            if (lev <= watch) worstUnderWatch = Math.max(worstUnderWatch, hm * LGD);
            if (Double.isNaN(newsFrom) && hm >= hAtT) newsFrom = lev;
        }
        out.printf("   under the watch line a month writes off at most %.3f%% of the debt; the month is news from %.2f%n",
                worstUnderWatch * 100, newsFrom);
        assertTrue("the default rate and the monthly slice rise with leverage, and never fall", monotone);
        assertTrue("...from nothing with no debt to all of it against no assets",
                BusinessDebtManager.defaultProbability(0) == 0
                        && BusinessDebtManager.defaultProbability(Double.POSITIVE_INFINITY) == 1);
        assertTrue("under the watch line no month's slice costs more than a sound book's year, BASE_LOSS_RATE",
                worstUnderWatch < Bank.BASE_LOSS_RATE);
        BusinessDebtManager inTrouble = lender(IND, 10_000, 12_000);
        inTrouble.restructureInsolventSectors();
        BusinessDebtManager atPoint = lender(IND, 10_000, 10_000 * T);
        atPoint.restructureInsolventSectors();
        assertTrue("a sector at 1.2 times its assets loses a slice and is not news; one at the default point is",
                inTrouble.getWrittenOffThisMonth(IND) > 0 && !inTrouble.defaultsAreNews(IND)
                        && atPoint.defaultsAreNews(IND));
        BusinessDebtManager sound = lender(IND, 100_000, 30_000);
        sound.restructureInsolventSectors();
        assertTrue("...and at 0.3 a month's slice of $30M of debt is under a cent",
                sound.getWrittenOffThisMonth(IND) < 1e-5);

        out.println("\n--- (14) past the default point with its plant standing, it is not written down whole ---");

        for (double lev : new double[] {2.0, 5.0}) {
            BusinessDebtManager past = lender(IND, 10_000, 10_000 * lev);
            assertTrue("fixture: " + lev + " times its assets is past INSOLVENCY_TRIGGER, against positive assets",
                    past.getPrincipal(IND) > past.getAssets(IND) * T && past.getAssets(IND) > 0);
            past.restructureInsolventSectors();
            close("at " + lev + " times its assets the month writes off its slice, principal x h x LOSS_GIVEN_DEFAULT",
                    past.getWrittenOffThisMonth(IND), 10_000 * lev * hazard(lev) * LGD, 1e-9);
            assertTrue("...not down to RESTRUCTURE_TARGET of its assets in one month",
                    past.getPrincipal(IND) > past.getAssets(IND) * BusinessDebtManager.RESTRUCTURE_TARGET * 1.5);
            assertTrue("...and not banned, and not on its record",
                    !past.isBorrowingBlocked(IND) && past.getRestructureCount(IND) == 0
                            && !past.wasRestructuredThisMonth(IND) && !past.isInsolvent(IND));
        }

        out.println("\n--- (14) the backstop: a sector with nothing left is written down whole, and banned ---");

        BusinessDebtManager sunk = lender(IND, -100, 900);
        sunk.setCash(IND, -1_500);
        assertTrue("fixture: owing against assets below nothing is insolvent", sunk.isInsolvent(IND));
        close("the whole of its debt is written off - RESTRUCTURE_TARGET of nothing",
                sunk.restructureInsolventSectors(), 900, 1e-9);
        close("...its overdraft is forgiven", sunk.takeOverdraftForgiven(IND), 1_500, 1e-9);
        assertTrue("...it is restructured, not sliced", sunk.wasRestructuredThisMonth(IND)
                && sunk.getDefaultedThisMonth(IND) == 0);
        close("...one default on its record", sunk.getRestructureCount(IND), 1, 0);
        close("...and shut out for exclusionFor() of it", sunk.getBlockedMonths(IND),
                BusinessDebtManager.exclusionFor(1), 0);
        assertTrue("...which is news", sunk.defaultsAreNews(IND));

        out.println("\n--- (14) the allowance reads the same curve, staged firm by firm: a year on its sound firms, a loan's term on those past the watch line ---");

        /*
         * STAGED FIRM BY FIRM since round 2 (Jerus: "Smooth the loss
         * reserve"). These asserted a book whole in stage 1 up to the watch
         * line and whole in stage 2 past it; they assert the staged formula
         * now, computed here (staged()), at the same leverages - and that the
         * cliff at the line is gone.
         */
        double P = 10_000;
        close("far under the watch line its firms are sound: a year's BASE_LOSS_RATE, never less",
                Bank.sectorAllowance(P, P / .5), P * Bank.BASE_LOSS_RATE, 1e-12);
        assertTrue("fixture: at 0.5 even a loan's term of the curve is under BASE_LOSS_RATE",
                LGD * BusinessDebtManager.defaultProbability(.5, BusinessDebtManager.LOAN_TERM_MONTHS) < Bank.BASE_LOSS_RATE);
        double pd85 = BusinessDebtManager.normalCdf(Math.log(.85 / T) / BusinessDebtManager.ASSET_VOLATILITY);
        assertTrue("fixture: at 0.85 the curve's year is over BASE_LOSS_RATE", LGD * pd85 > Bank.BASE_LOSS_RATE);
        close("...nearer it, a year on its sound firms and a loan's term on the share past the line",
                Bank.sectorAllowance(P, P / .85), staged(P, .85), 1e-9);
        close("...the share past it being N(ln(L / SECTOR_WATCH_LEVERAGE) / ASSET_VOLATILITY)",
                Bank.stageTwoShare(P, P / .85), BusinessDebtManager.normalCdf(Math.log(.85 / watch)
                        / BusinessDebtManager.ASSET_VOLATILITY), 1e-15);
        close("...half of them at the watch line itself", Bank.stageTwoShare(P, P / watch), .5, 1e-15);
        close("...so there, half a year's loss and half a lifetime's",
                Bank.sectorAllowance(P, P / watch), staged(P, watch), 1e-9);
        double pd12 = BusinessDebtManager.normalCdf(Math.log(1.2 / T) / BusinessDebtManager.ASSET_VOLATILITY);
        double life12 = P * LGD * (1 - Math.pow(1 - pd12,
                BusinessDebtManager.LOAN_TERM_MONTHS / (double) BusinessDebtManager.DEFAULT_HORIZON_MONTHS));
        close("past it, most of the lifetime loss over a loan's term, staged",
                Bank.sectorAllowance(P, P / 1.2), staged(P, 1.2), 1e-9);
        assertTrue("...between a year's loss and the whole lifetime's",
                Bank.sectorAllowance(P, P / 1.2) >= P * Math.max(Bank.BASE_LOSS_RATE, LGD * pd12)
                        && Bank.sectorAllowance(P, P / 1.2) <= life12 + 1e-9);
        close("...and a sector with nothing left holds all it owes, which the backstop writes off",
                Bank.sectorAllowance(P, 0), P, 0);
        double worstStep = 0, lastHeld = -1;
        boolean rises = true;
        for (int i = 1; i <= 3000; i++) {
            double lev = i / 1000.0;
            double perDollar = Bank.sectorAllowance(P, P / lev) / P;
            if (perDollar < lastHeld - 1e-15) rises = false;
            if (lastHeld >= 0) worstStep = Math.max(worstStep, perDollar - lastHeld);
            lastHeld = perDollar;
        }
        double firstCut = P * (LGD * BusinessDebtManager.defaultProbability(watch, BusinessDebtManager.LOAN_TERM_MONTHS)
                - Math.max(Bank.BASE_LOSS_RATE, LGD * BusinessDebtManager.defaultProbability(watch)));
        double across = Bank.sectorAllowance(P, P / (watch + 1e-4)) - Bank.sectorAllowance(P, P / (watch - 1e-4));
        double acrossTight = Bank.sectorAllowance(P, P / (watch + 1e-6)) - Bank.sectorAllowance(P, P / (watch - 1e-6));
        out.printf("   steepest: %.4f%% of the debt a thousandth of leverage; across the watch line %.6f%% (the first cut jumped %.2f%% there)%n",
                worstStep * 100, acrossTight / P * 100, firstCut / P * 100);
        assertTrue("the allowance rises with leverage and never falls", rises);
        assertTrue("...and has no cliff at the watch line: the step across it shrinks with the step, as a continuous one does",
                acrossTight >= 0 && acrossTight < across / 50);

        // Held past the watch line: its lifetime loss is set aside the month it
        // crosses, and every slice after is drawn from it, not from capital.
        Bank provider = new Bank();
        provider.injectCapital(100_000);
        provider.refresh(1, 1_000_000, 0, 0, 0, 0);
        BusinessDebtManager desk = lender(IND, 80_000, 40_000);
        provider.startMonth();
        provider.lend(40_000);
        provider.refresh(1, 1_000_000, 0, 40_000, 0, 0);
        provider.provide(owing(IND, 40_000, 80_000), 0, 0);
        provider.closeMonth();
        double stage1At = provider.getSectorAllowance(IND);
        // It weakens past the line: its assets fall to leave it at 1.2.
        provider.startMonth();
        desk.setAssets(IND, 40_000 / 1.2);
        provider.provide(owing(IND, 40_000, 40_000 / 1.2), 0, 0);
        double crossing = provider.provisions();
        double setAside = provider.getSectorAllowance(IND);
        provider.closeMonth();
        double slices = 0, providedAfter = 0, beyond = 0, worstDraw = 0, worstMove = 0;
        for (int m = 0; m < BusinessDebtManager.DEFAULT_HORIZON_MONTHS; m++) {
            provider.startMonth();
            desk.setAssets(IND, desk.getPrincipal(IND) / 1.2);
            double slice = desk.restructureInsolventSectors();
            provider.writeOffSector(IND, desk.getWrittenOffThisMonth(IND));
            provider.refresh(1, 1_000_000, 0, desk.getPrincipal(IND), 0, 0);
            provider.provide(owing(IND, desk.getPrincipal(IND), desk.getAssets(IND)), 0, 0);
            slices += slice;
            providedAfter += provider.provisions();
            beyond += provider.getWriteOffsBeyondAllowance();
            worstDraw = Math.max(worstDraw, Math.abs(provider.getAllowanceUsed() - slice));
            worstMove = Math.max(worstMove, Math.abs(provider.equity() - provider.getOpeningEquity() - provider.getNetIncome()));
            provider.closeMonth();
        }
        out.printf("   crossing the line provided %,.1f (stage 1 held %,.1f); a year's slices %,.1f, provided after %,.1f%n",
                crossing, stage1At, slices, providedAfter);
        close("moving past the watch line provides the rise in its staged loss, less the year it held",
                crossing, setAside - stage1At, 1e-9);
        close("...which is the staged loss at its new leverage", setAside, staged(40_000, 1.2), 1e-9);
        assertTrue("...which is more than the year of slices that follow", setAside > slices);
        close("every slice after is drawn from the allowance", worstDraw, 0, 1e-9);
        close("...and none of it reaches the month past it", beyond, 0, 1e-9);
        assertTrue("...so the year's provisions are less than its write-offs: the loss was taken when it crossed",
                providedAfter < slices);
        close("...and every month its equity moved by its net income and nothing else", worstMove, 0, 1e-9);

        out.println("\n--- (14) a played city through months of slices: the money audit closes ---");

        Path root = Files.createTempDirectory("bankcheck-slices");
        Game city = new Game(new GameFiles(root.resolve("data"), root.resolve("no-legacy")));
        System.setOut(quiet);
        try {
            city.run();
            city.getForeignAccounts().pinRate(1.0);
            city.buildStack(template(city, "House"), 300, false);
            city.buildStack(template(city, "Convenience Store"), 6, false);
            city.buildStack(template(city, "Industrial Bakery"), 2, false);
            city.buildStack(template(city, "Construction Depot"), 4, false);
            city.buildStack(template(city, "Coal Power Plant"), 1, false);
            city.buildStack(template(city, "Commercial Bank"), 1, false);
            /*
             * LUXURY RETAIL SITS THIS ONE OUT (0.7.8, since round 3). The
             * question is retail's slices, and the quiet months below need no
             * other sector past the default point. That held by luck: on
             * round 3's build and on this one, the city's boutiques had borrowed $7.7M against $4.7M by month 74
             * (the round-2 build: $2.8M against $2.2M) and were past the
             * default point, news, in every one of the twelve months - "0 of
             * the 12 months" quiet, measured on both. Held from the founding
             * they never build, and the fixture causes its own condition. See
             * BusinessInvestment.holdSector().
             */
            city.getBusinessInvestment().holdSector(Sectors.LUXURY_RETAIL);
            city.simulateMonths(72);
        } finally {
            System.setOut(out);
        }
        BusinessDebtManager credit = city.getEconomyManager().getBusinessDebtManager();
        String RET = Sectors.RETAIL;
        double retailAssets = credit.getAssets(RET);
        assertTrue("fixture: the city has a standing bank, and retail has plant to lend against",
                city.getBank().getBranches() >= 1 && !city.getBank().isInsolvent() && retailAssets > 0);
        // It owes 1.2 times what it owns - taken on between months, as the
        // money check's own stressed sector is: a claim, no money moved.
        credit.issueLoan(RET, Math.max(0, 1.2 * retailAssets - credit.getPrincipal(RET)), city.getMonth());
        int sliceMonths = 0, cleanMonths = 0, bankAgrees = 0, quietMonths = 0, noticeRight = 0;
        double worstResidual = 0, worstRelative = 0;
        System.setOut(quiet);
        try {
            for (int m = 0; m < 12; m++) {
                city.simulateMonths(1);
                MoneyAudit.Result audit = city.getLastMoneyAudit();
                worstResidual = Math.max(worstResidual, Math.abs(audit.residual));
                worstRelative = Math.max(worstRelative, audit.relative());
                if (credit.getDefaultedThisMonth(RET) > 0 && !credit.wasRestructuredThisMonth(RET)) sliceMonths++;
                if (Math.abs(audit.residual) < .01) cleanMonths++;
                if (Math.abs(city.getBank().getWrittenOff(RET) - credit.getWrittenOffThisMonth(RET)) < 1e-9) bankAgrees++;
                // The notice (Inbox, "defaults") is live exactly when some
                // sector's month was news - never on retail's slices alone.
                boolean anyNews = false;
                for (String s : credit.sectors()) anyNews |= credit.defaultsAreNews(s);
                if (!anyNews) quietMonths++;
                if ((city.getInbox().live("defaults") != null) == anyNews) noticeRight++;
            }
        } finally {
            System.setOut(out);
        }
        out.printf("   retail sliced in %d of 12 months, written off $%,.0fk in all; worst residual $%.4fk%n",
                sliceMonths, credit.getWrittenOffTotal(RET), worstResidual);
        assertTrue("fixture: retail's firms really did default, a slice a month, for months", sliceMonths >= 6);
        close("every month of it closes to the cent", cleanMonths, 12, 0);
        assertTrue("...and within 0.01% of what moved", worstRelative < 1e-4);
        close("...and the bank wrote off what the lender did, every month", bankAgrees, 12, 0);
        out.printf("   %d of the 12 months had slices and nothing past the default point%n", quietMonths);
        assertTrue("fixture: months went by with retail's firms defaulting and nothing past the line",
                quietMonths >= 3);
        close("the defaults notice is live exactly in the months that were news, and in no other", noticeRight, 12, 0);

        // ...and past the default point it is news: twice what it owns.
        credit.issueLoan(RET, Math.max(0, 2 * credit.getAssets(RET) - credit.getPrincipal(RET)), city.getMonth());
        System.setOut(quiet);
        try {
            city.simulateMonths(1);
        } finally {
            System.setOut(out);
        }
        Notice raised = city.getInbox().live("defaults");
        out.printf("   pushed past the default point: %.1f%% of retail's debt defaulted, notice %s%n",
                credit.getDefaultShareThisMonth(RET) * 100, raised == null ? "none" : "\"" + raised.getTitle() + "\"");
        assertTrue("fixture: retail's month was past the default point", credit.defaultsAreNews(RET));
        assertTrue("...and the inbox says so, naming it",
                raised != null && String.join("\n", raised.getBody()).contains(RET));
        assertTrue("...and the month still closes", Math.abs(city.getLastMoneyAudit().residual) < .01);
    }

    /* ============ 15. A FAILING SECTOR'S PLANT, SOLD TO THE BUILDERS (0.7.8) ============

       Jerus, 2026-09-23: "Sell a failing sector's plant ... to construction
       company just in materials, and land to treasury". A played city whose
       retail sector is held in distress - owing past what it owns, shut out
       of credit, overdrawn, two years of losses on its record - so the
       distress rule retires a shop inside a month the audit strikes. What it
       has to prove: the builders pay for the shop's material at the day's
       price and take it into their stock, the seller's till takes the same
       sum, both cash-flow statements close, and the month's audit closes;
       and a builder that cannot pay buys nothing, the building scrapped for
       nothing as it always was.
       ======================================================================= */

    static void aFailingSectorsPlantIsSoldToTheBuilders() throws Exception {

        out.println("\n--- (15) a failing sector's plant is sold to the builders, as its materials ---");

        Path root = Files.createTempDirectory("bankcheck-salvage");
        Game city = new Game(new GameFiles(root.resolve("data"), root.resolve("no-legacy")));
        System.setOut(quiet);
        try {
            city.run();
            city.getForeignAccounts().pinRate(1.0);
            city.buildStack(template(city, "House"), 300, false);
            city.buildStack(template(city, "Convenience Store"), 6, false);
            city.buildStack(template(city, "Industrial Bakery"), 2, false);
            city.buildStack(template(city, "Construction Depot"), 4, false);
            city.buildStack(template(city, "Coal Power Plant"), 1, false);
            city.buildStack(template(city, "Commercial Bank"), 1, false);
            city.simulateMonths(72);
        } finally {
            System.setOut(out);
        }
        String RET = Sectors.RETAIL;
        BusinessDebtManager credit = city.getEconomyManager().getBusinessDebtManager();
        EconomyManager econ = city.getEconomyManager();
        ham.citybuildersim.sectors.Construction builders = city.getSectors().construction();
        BuildingsTemplate shop = template(city, "Convenience Store");
        credit.issueLoan(RET, Math.max(0, 1.3 * credit.getAssets(RET) - credit.getPrincipal(RET)), city.getMonth());

        // Held in distress until the rule retires a shop: shut out, overdrawn,
        // and the losing streak the rule reads - renewed every month.
        Game.Salvage sale = null;
        MoneyAudit.Result audit = null;
        double stockBefore = 0, shopsBefore = 0, priceBefore = 0, overdrawnBy = 0, costBefore = 0;
        int tried = 0;
        System.setOut(quiet);
        try {
            while (sale == null && tried < 36) {
                java.util.Map<String, Integer> blocked = new java.util.LinkedHashMap<>(credit.getBlockedMonthsAll());
                blocked.put(RET, 12);
                credit.restoreCreditRecord(credit.getRestructureCounts(), blocked);
                java.util.Map<String, Integer> streaks = new java.util.LinkedHashMap<>(
                        city.getBusinessInvestment().getLossMonthsState());
                streaks.put(RET, BusinessInvestment.DISTRESS_LOSS_MONTHS);
                city.getBusinessInvestment().restoreLossMonths(streaks);
                double tillWas = econ.getSectorCash(RET);
                econ.setSectorCash(RET, Math.min(tillWas, -2 * shop.getCashCost()));
                overdrawnBy = econ.getSectorCash(RET) - tillWas;
                stockBefore = builders.getSalvage();
                costBefore = builders.getSalvageCost();
                shopsBefore = city.getBuildingManager().getQuantity(shop.getId());
                // The day's price: the materials market strikes later in the
                // month than the retirement, so the price it sold at is the
                // one standing when the month opened.
                priceBefore = city.getMarkets().get(Good.MATERIALS).getLocalPrice();
                city.simulateMonths(1);
                tried++;
                for (Game.Salvage s : city.getSalvageThisMonth()) {
                    if (s.seller().equals(RET) && s.distress()) sale = s;
                }
                audit = city.getLastMoneyAudit();
            }
        } finally {
            System.setOut(out);
        }
        assertTrue("fixture: retail in distress retired plant, and the rule that did it was the distress rule",
                sale != null);
        if (sale == null) return;
        double shopsGone = shopsBefore - city.getBuildingManager().getQuantity(shop.getId());
        out.printf("   month %d: %d %s retired, %.0f units of material at $%,.3fk - the builders paid $%,.1fk%n",
                city.getMonth(), sale.buildings(), sale.building(), sale.units(), sale.price(), sale.paid());
        close("the material sold is the template's own, times the buildings retired",
                sale.units(), template(city, sale.building()).getConstructionMaterials() * (double) sale.buildings(), 1e-9);
        close("...a shop's material, the shops the city lost", sale.buildings(), shopsGone, 0);
        assertTrue("fixture: the builders could pay for all of it", sale.unitsBought() == sale.units() && sale.paid() > 0);
        close("the builders paid the units at the day's price for material", sale.paid(), sale.units() * sale.price(), 1e-9);
        close("...the materials market's own price, as it stood that day", sale.price(), priceBefore, 1e-9);
        close("their stock rose by the units, less what the month's sites drew from it",
                builders.getSalvage(), stockBefore + sale.unitsBought() - city.getSalvageUsedThisMonth(), 1e-9);
        close("...and it is on their balance sheet at the day's price", builders.getInventoryValue(),
                builders.getSalvage() * city.getMarkets().get(Good.MATERIALS).getLocalPrice(), 1e-9);
        close("the builders' cash moved out by what they paid", city.getSalvageThisMonth(builders.key()), sale.paid(), 1e-9);
        close("...and the seller's in by the same", city.getSalvageThisMonth(RET), -sale.paid(), 1e-9);
        SectorBooks.SectorMonth sellerBooks = city.getSectorBooks().get(RET);
        SectorBooks.SectorMonth builderBooks = city.getSectorBooks().get(builders.key());
        close("...both on their cash-flow statements", sellerBooks.salvage() + builderBooks.salvage(), 0, 1e-9);
        close("...which close for the seller, but for the overdraft the fixture handed it",
                sellerBooks.unexplained(), overdrawnBy, 1e-6);
        close("...and for the builders", builderBooks.unexplained(), 0, 1e-6);
        assertTrue("and the month's money audit closes, to the cent", audit != null && Math.abs(audit.residual) < .01);

        /*
         * ...AND WHAT THEY BUILD WITH FROM IT IS A COST (0.7.8, round 3): at
         * what they paid for it, as the stock is drawn, and no cash - the
         * cash left at the sale. The sites draw early in the month, before
         * the retirements, so they build with it from the month after; and a
         * month's draw is in the ledger struck and banked at the top of the
         * month after that, with the rest of it.
         */
        double drawnUnits = 0, drawnCost = 0;
        int waited = 0;
        Game.BuildResult ordered = null;
        MoneyAudit.Result nextAudit;
        System.setOut(quiet);
        try {
            // Work for the crews that the city's free yard does not cover:
            // the yard emptied, land to put it on, and four paved roads ordered.
            city.getBuildingManager().takeFromYard(city.getBuildingManager().getConstructionMaterials());
            city.getLandManager().setOwnedSqFt(city.getLandManager().getOwnedSqFt() + 10_000_000L);
            ordered = city.buildStack(template(city, "Paved Road"), 4, false);
            while (drawnUnits <= 0 && waited < 12) {
                double costAtOpen = builders.getSalvageCost();
                city.simulateMonths(1);
                waited++;
                drawnUnits = city.getSalvageUsedThisMonth();
                drawnCost = costAtOpen + city.getSalvageThisMonth(builders.key()) - builders.getSalvageCost();
            }
            city.simulateMonths(1);
            nextAudit = city.getLastMoneyAudit();
        } finally {
            System.setOut(out);
        }
        assertTrue("fixture: the city ordered work its yard does not cover", ordered == Game.BuildResult.SUCCESS);
        assertTrue("fixture: the builders built with it in a later month", drawnUnits > 0 && drawnCost > 0);
        Sector.Statement built = builders.statement();
        out.printf("   drew %,.0f units, $%,.1fk of what they paid - booked as the next month's input%n", drawnUnits, drawnCost);
        close("what they built with from it is on their income statement, at what they paid for it",
                built.paidEarlier, drawnCost, 1e-6);
        close("...as a named input", built.otherInputs.getOrDefault("Material from scrapped plant", 0.0),
                built.paidEarlier, 1e-9);
        SectorBooks.SectorMonth builtBooks = city.getSectorBooks().get(builders.key());
        close("...and no cash: the cash flow adds it back", builtBooks.paidEarlier(), built.paidEarlier, 1e-9);
        close("...and closes", builtBooks.unexplained(), 0, 1e-6);
        assertTrue("...and that month's money audit closes, to the cent", Math.abs(nextAudit.residual) < .01);

        // A builder that cannot pay buys nothing: the building is scrapped
        // for nothing, as it always was.
        Game.Salvage broke = null;
        tried = 0;
        System.setOut(quiet);
        try {
            while (broke == null && tried < 36) {
                java.util.Map<String, Integer> blocked = new java.util.LinkedHashMap<>(credit.getBlockedMonthsAll());
                blocked.put(RET, 12);
                credit.restoreCreditRecord(credit.getRestructureCounts(), blocked);
                java.util.Map<String, Integer> streaks = new java.util.LinkedHashMap<>(
                        city.getBusinessInvestment().getLossMonthsState());
                streaks.put(RET, BusinessInvestment.DISTRESS_LOSS_MONTHS);
                city.getBusinessInvestment().restoreLossMonths(streaks);
                econ.setSectorCash(RET, Math.min(econ.getSectorCash(RET), -2 * shop.getCashCost()));
                econ.setSectorCash(builders.key(), -1e12);
                city.simulateMonths(1);
                tried++;
                for (Game.Salvage s : city.getSalvageThisMonth()) {
                    if (s.seller().equals(RET)) broke = s;
                }
            }
        } finally {
            System.setOut(out);
        }
        assertTrue("fixture: retail retired plant again, with the builders overdrawn", broke != null);
        if (broke == null) return;
        assertTrue("builders who cannot pay buy nothing, and pay nothing",
                broke.unitsBought() == 0 && broke.paid() == 0 && broke.units() > 0);
        close("...so the seller is paid nothing for it", city.getSalvageThisMonth(RET), 0, 0);
    }

    /* ============ 16. THE BANK READS A BORROWER FROM ITS LAST QUARTER (0.7.8) ============

       Jerus, 2026-09-23: "the bank reads a borrower's debt level from its last
       quarter's average, like real statements, not one month's stock swing";
       and 2026-09-24, "keep quarterly", restarted after a whole-sector
       restructure. What it has to prove, on a lender alone: with no readings
       it reads the month; the price and the allowance read the average debt
       over the average assets of the last STATEMENT_MONTHS readings; a fourth
       drops the oldest; the readings save and restore by the sector's name;
       the default hazard stays on the month's own leverage; and after the
       backstop the next loan is priced on the restructured books, not on the
       months before it - while a slice, continuous and small, restarts
       nothing.
       ======================================================================= */

    static void theBankReadsTheQuarter() {

        out.println("\n--- (16) the bank reads a borrower from its last quarter ---");

        String IND = Sectors.INDUSTRY;
        BusinessDebtManager q = new BusinessDebtManager();
        q.setPrimeRate(.05);
        q.setAssets(IND, 50_000);
        close("with no statements yet, the bank reads a borrower's month",
                q.getQuarterLeverage(IND), q.getLeverage(IND), 0);
        q.issueLoan(IND, 100_000, 1);
        q.recordStatement(IND, 100_000, 100_000);
        q.recordStatement(IND, 100_000, 125_000);
        q.recordStatement(IND, 100_000, 75_000);
        q.updateRates();
        out.printf("   owing 100,000 against 50,000 this month (%.2fx), against 100,000 over the quarter (%.2fx)%n",
                q.getLeverage(IND), q.getQuarterLeverage(IND));
        close("the price reads the last quarter's statements: their average debt over their average assets",
                q.getRate(IND), .05 + BusinessDebtManager.expectedLossSpread(1.0), 1e-15);
        assertTrue("...not the month's own", q.getRate(IND) < .05 + BusinessDebtManager.expectedLossSpread(2.0));
        close("...and so does the allowance: its staged loss at the quarter's leverage",
                Bank.sectorAllowance(q.quarterPrincipal(IND), q.quarterAssets(IND)),
                Bank.sectorAllowance(100_000, 100_000), 1e-9);
        java.util.Map<String, double[]> saved = q.getStatementsToSave();
        BusinessDebtManager again = new BusinessDebtManager();
        again.restoreStatements(saved);
        close("the quarter is saved and restored by the sector's name",
                again.quarterAssets(IND), q.quarterAssets(IND), 0);
        q.recordStatement(IND, 100_000, 200_000);
        close("a fourth reading drops the oldest: STATEMENT_MONTHS of them",
                q.quarterAssets(IND), (125_000 + 75_000 + 200_000) / 3.0, 1e-9);
        close("...and the count stops there", q.getStatementCount(IND), BusinessDebtManager.STATEMENT_MONTHS, 0);
        double h = BusinessDebtManager.monthlyDefaultShare(q.getLeverage(IND));
        double cut = q.defaultSlice(IND);
        close("the hazard stays on the month's own leverage: firms fail on what they owe against what they have",
                cut, 100_000 * h * BusinessDebtManager.LOSS_GIVEN_DEFAULT, 1e-9);
        assertTrue("...and a slice, continuous and small, does not restart the quarter",
                q.getStatementCount(IND) == BusinessDebtManager.STATEMENT_MONTHS);

        /*
         * ...AND A BACKSTOP RESTARTS IT (round 4). The bank re-rates a
         * restructured borrower on its restructured books, as a lender does
         * after a credit event: before this the quarter still read the debt
         * it had just written off, for two months, and priced the sector's
         * next loans as though it were past the default point. Here a sector
         * with a quarter of heavy debt behind it loses everything, is written
         * down whole, gets its plant back and is quoted again.
         */
        BusinessDebtManager r = new BusinessDebtManager();
        r.setPrimeRate(.05);
        r.setAssets(IND, 100_000);
        r.issueLoan(IND, 180_000, 1);
        r.recordStatement(IND, 180_000, 100_000);
        r.recordStatement(IND, 180_000, 100_000);
        r.recordStatement(IND, 180_000, 100_000);
        r.setAssets(IND, -1);
        r.restructureInsolventSectors();
        assertTrue("fixture: the sector with nothing left was written down whole", r.wasRestructuredThisMonth(IND));
        close("after the backstop its quarter restarts: no reading from before it counts",
                r.getStatementCount(IND), 0, 0);
        r.setAssets(IND, 100_000);
        r.updateRates();
        double owedAfter = r.getPrincipal(IND);
        close("...so its next loan's price reads the restructured books, what it owes now over what it owns now",
                r.getRate(IND), .05 + BusinessDebtManager.expectedLossSpread(owedAfter / 100_000)
                        + r.getRecordSurcharge(IND), 1e-15);
        assertTrue("...which is far under what the quarter before the backstop would have charged",
                r.getRate(IND) < .05 + BusinessDebtManager.expectedLossSpread(1.8));
    }

    /* ============ 17. THE DESK HELD TO THE BANK'S CAPITAL (0.7.8) ============

       Jerus, 2026-09-24: "Buybacks only from spare capital" and "Trading
       desk held to its capital." Each on a bank built by hand a little over
       its target, offered more than that capital carries by a household
       short of money (Exchange.sellForHousehold()) - the one sale that is
       neither re-quoted nor re-marked before it can be read: its own shares,
       where it buys back exactly what it holds over its target and ends at
       it; and another company's, where it buys what the spare carries at the
       weighted book's own RISK_EQUITY, and marked it holds its target. Then
       a bank under its target, which buys nothing: the household raises
       nothing from its shares, and the leavers' shares stay abroad with them,
       as with no market at all. Round 3 measured why: 68 of the default
       run's 101 failures were months of buybacks a median 131% of the
       equity, and 28 more (47 of the autopilot's, 124 of the held city's)
       were the desk's re-mark.
       ================================================================= */
    static void theDeskIsHeldToTheBanksCapital() {

        out.println("\n--- (17) the desk buys the bank's own shares back only with what it holds over its target ---");

        int BANK = Equity.BANK;
        int IND = Equity.indexOf(Sectors.INDUSTRY);
        double W = DebtManager.WORLD_BASE_RATE;
        double B = 1_000_000;
        double target = Bank.CAPITAL_RATIO + Bank.CONSERVATION_BUFFER;
        double spare = 50;

        Bank own = lentOut(target * B + spare, B);
        close("fixture: a bank a little over its target", own.spareCapital(), spare, 1e-6);
        assertTrue("fixture: ...which by its own rule buys its shares back", own.buysBackOwnShares());
        HouseholdBalance town = ExchangeCheck.savers(0, 4.0);
        Equity reg = ExchangeCheck.withRecord(BANK, ExchangeCheck.months(100));
        reg.listIfUnlisted(BANK, own.equity(), town);
        assertTrue("fixture: its shares have a record neither new nor bad",
                reg.getRegime(BANK) != Equity.Regime.NEW && reg.getRegime(BANK) != Equity.Regime.BAD);
        Exchange ex = new Exchange();
        double[] book = new double[Equity.COMPANIES.length];
        book[BANK] = own.equity();
        ex.startMonth();
        ex.quote(reg, book, own.equity(), W);
        double pace = Exchange.BUYBACK_PACE / 12 * reg.getShares(BANK);
        Household cell = ExchangeCheck.couple(town);
        double offered = cell.shares[BANK] * cell.households();
        double needPer = cell.shares[BANK] * ex.bid(BANK) / 2;       // half of what it holds
        assertTrue("fixture: the household offers more than the pace, and the pace more than the spare",
                offered / 2 > pace && pace * ex.bid(BANK) > spare);
        double raised = ex.sellForHousehold(reg, own, cell, needPer) * cell.households();
        out.printf("   offered %,.0f of its shares ($%,.0f) with $%,.0f to spare; bought back $%,.4f%n",
                offered / 2, offered / 2 * ex.bid(BANK), spare, own.getSharesBoughtBack());
        close("a bank a little over its target, offered more than its spare capital, buys back exactly the spare",
                own.getSharesBoughtBack(), spare, 1e-6);
        close("...and ends at its target", own.spareCapital(), 0, 1e-6);
        assertTrue("...not under it", own.equity() >= own.targetEquity() - 1e-6);
        close("...so the household raised what the desk took, and no more - the rest of its need goes on to credit",
                raised, spare, 1e-6);
        close("what the pace allowed and its capital did not is counted against the household",
                ex.getOwnRefused(Exchange.Seller.HOUSEHOLD), (pace - spare / ex.bid(BANK)) * ex.bid(BANK), 1e-6);
        double again = ex.sellForHousehold(reg, own, cell, needPer);
        close("a second offer the same month, at its target, is bought with nothing", again, 0, 1e-9);

        out.println("\n--- (17) ...and other companies' shares only on the capital it has to spare, at the desk's weight ---");

        double deskSpare = 1_000;
        Bank desk = lentOut(target * B + deskSpare, B);
        HouseholdBalance holders = ExchangeCheck.savers(0, 4.0);
        Equity reg2 = new Equity();
        reg2.listIfUnlisted(IND, 100_000, holders);
        Exchange ex2 = new Exchange();
        double[] book2 = new double[Equity.COMPANIES.length];
        book2[IND] = 100_000;                                 // $1 a share on the books
        ex2.startMonth();
        ex2.quote(reg2, book2, desk.equity(), W);
        double bid = ex2.bid(IND), mark = ex2.mark(IND);
        double carries = deskSpare / (bid - mark + desk.capitalTarget() * Bank.RISK_EQUITY * mark);
        double dealing = Math.min(Exchange.CAPACITY * ex2.limit(IND), Exchange.BOOK_LIMIT * desk.equity() / ex2.fair(IND));
        Household holder = ExchangeCheck.couple(holders);
        double offer = holder.shares[IND] * holder.households();
        assertTrue("fixture: the household offers more than the desk's dealing limits take, and those more than its capital carries",
                offer > dealing && dealing > carries);
        double weightedBefore = desk.getWeightedBook();
        double raised2 = ex2.sellForHousehold(reg2, desk, holder, holder.shares[IND] * bid) * holder.households();
        out.printf("   offered %,.0f shares; its dealing limits take %,.0f, its $%,.0f of spare capital carries %,.0f; it bought %,.2f%n",
                offer, dealing, deskSpare, carries, reg2.getDealerShares(IND));
        close("a bank a little over its target buys only what its spare capital carries at the weight",
                reg2.getDealerShares(IND), carries, 1e-6);
        desk.markSecurities(ex2.markToMarket(reg2));          // what the close does, at the same quote
        close("...the weight the weighted book already gives the desk, RISK_EQUITY",
                desk.getWeightedBook() - weightedBefore, carries * mark * Bank.RISK_EQUITY, 1e-6);
        close("...and marked, it holds its target", desk.spareCapital(), 0, 1e-6);
        assertTrue("...its ratio at or over the target", desk.capitalRatio() >= desk.capitalTarget() - 1e-12);
        close("what its dealing limits allowed and its capital did not is counted against the household",
                ex2.getDeskRefused(Exchange.Seller.HOUSEHOLD), (dealing - carries) * bid, 1e-6);
        close("...and the household raised what the desk paid", raised2, carries * bid, 1e-6);
        double heldBefore = reg2.getDealerShares(IND);
        double sold = ex2.deskSellsToHouseholds(reg2, desk, IND, 100);
        assertTrue("its selling is unchanged: at its target it still sells what it holds",
                sold > 0 && reg2.getDealerShares(IND) < heldBefore);

        Bank short_ = lentOut(target * B - 1_000, B);
        HouseholdBalance holders3 = ExchangeCheck.savers(0, 4.0);
        Equity reg3 = new Equity();
        reg3.listIfUnlisted(IND, 100_000, holders3);
        Exchange ex3 = new Exchange();
        ex3.startMonth();
        ex3.quote(reg3, book2, short_.equity(), W);
        assertTrue("fixture: a bank under its target, still making a market", ex3.isOpen() && short_.spareCapital() < 0);
        Household holder3 = ExchangeCheck.couple(holders3);
        double raised3 = ex3.sellForHousehold(reg3, short_, holder3, holder3.shares[IND] * ex3.bid(IND) / 2);
        close("a bank under its target buys none: the household raises nothing from its shares", raised3, 0, 0);
        close("...and the desk holds none", reg3.getDealerShares(IND), 0, 0);

        HouseholdBalance leaving = ExchangeCheck.savers(20.0, 4.0);
        Equity reg4 = new Equity();
        reg4.listIfUnlisted(IND, 10_000, leaving);
        double[][] fewer = new double[FamilyStructure.values().length][PayTier.values().length];
        fewer[FamilyStructure.COUPLE.ordinal()][PayTier.UNSKILLED.ordinal()] = 80;
        double[] income = new double[HouseholdBalance.ROWS];
        income[PayTier.UNSKILLED.ordinal()] = 80 * 4.0;
        double[] none = new double[HouseholdBalance.ROWS];
        leaving.advanceMonth((s, t) -> fewer[s.ordinal()][t.ordinal()], income, 0, none, none, .25, .05, 1);
        reg4.followEmigrants(leaving);
        double abroad = reg4.getForeignShares(IND);
        assertTrue("fixture: twenty households leave with their shares", abroad > 0);
        Exchange ex4 = new Exchange();
        double[] book4 = new double[Equity.COMPANIES.length];
        book4[IND] = 10_000;
        Bank short4 = lentOut(target * B - 1_000, B);
        ex4.startMonth();
        ex4.takeMonth(reg4, leaving, short4, new ExchangeCheck.Firms(), book4, W, 0);
        assertTrue("fixture: the exchange was open", ex4.isOpen());
        close("the leavers' shares a bank under its target will not buy stay abroad with them, as with no market",
                reg4.getForeignShares(IND), abroad, 0);
        assertTrue("...counted against the emigrants", ex4.getDeskRefused(Exchange.Seller.EMIGRANT) > 0
                && ex4.getEmigrantsPaid() == 0);
    }

    /* ============ 13. WHAT THE BANK TAB READS (0.7.9) ============

       The Bank tab's figures are model getters now (Bank, WHAT THE BANK TAB
       READS), and three of them carry arithmetic the screen used to do for
       itself - two of the three wrongly. Each is caused here: the ladder's
       four parts are its prime, at every dial; the weight table foots to the
       weighted book, term and desk included; and the equity's movement
       leaves nothing unexplained, every month of a played city from its
       founding month, with a treasury buyback and a rescue between two
       presses in it. And the figures nothing kept before: the interest by who paid
       it, last month's statement and the year's, a branch's reach in
       today's money after a reform, the bank's state in a sentence, and the
       rescue's guard on the treasury's cash.
       ================================================================= */
    static void whatTheBankTabReads() throws Exception {

        out.println("\n--- (13) the ladder's four parts are its prime, at every dial ---");

        Bank priced = lentOut(50_000, 400_000);
        priced.payRunning(300, 100);
        priced.closeMonth();
        double worstParts = 0, worstRungs = 0;
        for (double dial : new double[] {0, .01, .03, .06, .10, .15}) {
            Bank.Ladder l = priced.ladder(dial);
            worstParts = Math.max(worstParts, Math.abs(l.parts() - l.prime()));
            worstParts = Math.max(worstParts, Math.abs(l.prime() - priced.prime(dial)));
            worstRungs = Math.max(worstRungs, Math.abs(l.household() - priced.householdRate(dial))
                    + Math.abs(l.carry() - priced.carryRate(dial))
                    + Math.abs(l.transfer() - priced.fundsTransferPrice(dial, Bank.PRIME_TERM_MONTHS))
                    + Math.abs(l.window() - (dial + CentralBank.WINDOW_PENALTY)));
        }
        Bank.Ladder at3 = priced.ladder(.03);
        out.printf("   at a 3%% dial: %.2f%% + %.2f + %.2f + %.2f = prime %.2f%%%n", at3.transfer() * 100,
                at3.running() * 100, at3.loss() * 100, at3.capital() * 100, at3.prime() * 100);
        close("the funds-transfer price, running costs, expected loss and capital charge add up to prime, at six dials",
                worstParts, 0, 1e-12);
        close("...and every other rung is the bank's own rate for it", worstRungs, 0, 1e-12);
        assertTrue("fixture: at 3% every part is priced",
                at3.running() > 0 && at3.loss() > 0 && at3.capital() > 0);
        close("...and past the owners' return the capital part is nothing", priced.ladder(.15).capital(), 0, 0);
        close("the step from the policy rate is the window's penalty on its share and the term premium",
                at3.transferOverPolicy(), CentralBank.WINDOW_PENALTY * priced.windowShare()
                        + DebtManager.termPremium(Bank.PRIME_TERM_MONTHS), 1e-12);
        close("...and the step to prime is the three costs over it",
                at3.primeOverTransfer(), at3.running() + at3.loss() + at3.capital(), 1e-12);

        out.println("\n--- (13) the weight table foots to the weighted book, term and desk included ---");

        Bank weighed = new Bank();
        weighed.injectCapital(100_000);
        weighed.refresh(2, 5_000_000, 0, 300_000, 200_000, 50_000);
        weighed.setWeightedBook(300_000 * Bank.RISK_BUSINESS * .7, 200_000 * Bank.RISK_CITY * .5,
                50_000 * Bank.RISK_HOUSEHOLD);
        weighed.setCarryBook(40_000);
        weighed.markSecurities(25_000);
        double footed = 0, desk = 0, businessTerm = 0;
        boolean rowsRight = true;
        for (Bank.WeightRow row : weighed.weightTable()) {
            footed += row.weighted();
            rowsRight &= Math.abs(row.face() * row.term() * row.risk() - row.weighted()) < 1e-6;
            if (row.book() == Bank.Book.DESK) desk = row.weighted();
            if (row.book() == Bank.Book.BUSINESSES) businessTerm = row.term();
        }
        double oldTable = 300_000 * Bank.RISK_BUSINESS + 200_000 * Bank.RISK_CITY
                + 50_000 * Bank.RISK_HOUSEHOLD + 40_000 * Bank.RISK_CARRY;
        out.printf("   the table foots to $%,.0fk; face times risk, the old table, read $%,.0fk%n",
                footed, oldTable);
        close("the weight table's weighed column foots to the weighted book capacity reads",
                footed, weighed.getWeightedBook(), 1e-9);
        close("fixture: the desk's shares are on it, at RISK_EQUITY", desk, 25_000 * Bank.RISK_EQUITY, 1e-9);
        close("...a book halfway through its loans' terms shows the share its term counts for",
                businessTerm, .7, 1e-12);
        assertTrue("...and every row is its face, term and risk weight multiplied", rowsRight);
        assertTrue("...where face times risk, with no term and no desk, did not foot",
                Math.abs(oldTable - weighed.getWeightedBook()) > 1);

        out.println("\n--- (13) a branch's reach is in today's money after a reform ---");

        Bank reformed = new Bank();
        reformed.refresh(3, 1_000_000, 0, 0, 0, 0);
        close("three branches reach three times DEPOSITS_PER_BRANCH", reformed.branchReach(),
                3 * Bank.DEPOSITS_PER_BRANCH, 1e-9);
        reformed.redenominate(.01);
        close("...and after a hundred-for-one reform, a hundredth of it", reformed.getDepositsPerBranch(),
                Bank.DEPOSITS_PER_BRANCH * .01, 1e-12);
        close("...so what they reach of the city's savings is too - not the founding figure the screen used",
                reformed.localDepositsReached(), Math.min(1_000_000 * .01, 3 * Bank.DEPOSITS_PER_BRANCH * .01), 1e-9);
        close("...and their owners' capital for a branch", reformed.getPaidInPerBranch(),
                Bank.PAID_IN_PER_BRANCH * .01, 1e-12);
        close("...and reached and beyond reach add up to the city's own savings",
                reformed.localDepositsReached() + reformed.localDepositsBeyondReach(), reformed.localDeposits(), 1e-9);

        out.println("\n--- (13) the bank's state in a sentence, with the figure that decides it ---");

        Bank none = new Bank();
        assertTrue("with no branch: there is no bank", none.status().startsWith("There is no bank"));
        Bank sound = lentOut(100_000, 400_000);
        Bank thin = lentOut(40_000, 400_000);
        Bank under = lentOut(20_000, 400_000);
        Bank broke = lentOut(20_000, 400_000);
        broke.refresh(1, 100 * 400_000 + 1_000_000, 0, 370_000, 0, 0);    // a 30,000 loss on 20,000 of capital
        for (Bank b : new Bank[] { sound, thin, under, broke, none }) out.println("   " + b.status());
        assertTrue("fixture: the four banks are healthy, rebuilding, under the minimum and failed",
                (sound.payoutStance() == Bank.Payout.PAYING || sound.payoutStance() == Bank.Payout.RETURNING)
                && thin.payoutStance() == Bank.Payout.REBUILDING
                && under.payoutStance() == Bank.Payout.UNDER_MINIMUM
                && broke.payoutStance() == Bank.Payout.FAILED);
        assertTrue("a bank at or over its target is healthy and lending freely, at its ratio and its target",
                sound.status().startsWith("Healthy")
                && sound.status().contains(String.format("%.1f%%", sound.capitalRatio() * 100))
                && sound.status().contains(String.format("%.1f%%", sound.capitalTarget() * 100)));
        assertTrue("...one under its target is rebuilding, at the growth its rule allows",
                thin.status().startsWith("Rebuilding")
                && thin.status().contains(String.format("%.2f%%", thin.lendingGrowthLimit() * 100)));
        assertTrue("...one under the minimum lends only to keep its borrowers going, at its ratio",
                under.status().startsWith("Under the minimum")
                && under.status().contains(String.format("%.1f%%", under.capitalRatio() * 100)));
        assertTrue("...and a failed one says what it must hold again",
                broke.status().startsWith("Failed")
                && broke.status().contains(String.format("%.1f%%",
                        Bank.CAPITAL_RATIO * Bank.RESOLUTION_EXIT_BUFFER * 100)));

        out.println("\n--- (13) on a played city: the interest by who paid it, last month, the year, and the equity's movement ---");

        // Each payer on its own line, caused one at a time: the played city
        // below may go years with no family owing anything.
        Bank paid = lentOut(50_000, 400_000);
        paid.takeInterest(1_000, 2_000);
        paid.takeFromHouseholds(500, 300);
        paid.takeDiscount(40);
        close("the city's coupons, the businesses', the families' and the discount each on its own line",
                Math.abs(paid.getInterestFromCity() - 1_000) + Math.abs(paid.getInterestFromBusinesses() - 2_000)
                        + Math.abs(paid.getInterestFromHouseholds() - 300) + Math.abs(paid.getDiscountAccreted() - 40),
                0, 1e-12);
        close("...and together they are its interest", paid.interestIncome(), 3_340, 1e-12);

        Path root = Files.createTempDirectory("bankcheck-tab");
        Game city = new Game(new GameFiles(root.resolve("data"), root.resolve("no-legacy")));
        Bank bank = city.getBank();
        double worstResidual = 0, worstBetween = 0, worstByWho = 0, worstLast = 0, worstYear = 0, worstFoot = 0;
        int foundingMonths = 0, dividendMonths = 0, familyMonths = 0, cityMonths = 0, businessMonths = 0;
        int lastChecked = 0, yearChecked = 0, played = 72;
        double buybackGain = 0, rescued = 0;
        java.util.List<Double> nets = new java.util.ArrayList<>(), fees = new java.util.ArrayList<>();
        Debt bond = null;
        System.setOut(quiet);
        try {
            city.run();
            city.getLandManager().setOwnedSqFt(30_000_000);
            city.buildStack(template(city, "House"), 400, true);
            city.buildStack(template(city, "Convenience Store"), 8, true);
            city.buildStack(template(city, "Small Grocery Store"), 2, true);
            city.buildStack(template(city, "Bakery"), 1, true);
            city.buildStack(template(city, "Paved Road"), 20, true);
            city.buildStack(template(city, "Industrial Bakery"), 2, true);
            city.buildStack(template(city, "Construction Depot"), 4, true);
            city.buildStack(template(city, "Coal Power Plant"), 1, true);
            city.buildStack(template(city, "Water Treatment Plant"), 1, true);
            city.getEconomyManager().setSectorCash(Sectors.INDUSTRY, -20_000);
            // From the first month: a city opens with its founding branch
            // standing (Game.foundingBank()), so the month it is capitalised
            // and takes the standing book over is the first played.

            for (int m = 0; m < played; m++) {
                city.simulateMonths(1);

                Bank.EquityMovement moved = bank.equityMovement();
                worstResidual = Math.max(worstResidual, Math.abs(moved.residual()));
                if (Math.abs(moved.founding()) > 1e-9) foundingMonths++;
                if (moved.dividends() > 0) dividendMonths++;

                double byWho = bank.getInterestFromBusinesses() + bank.getInterestFromHouseholds()
                        + bank.getInterestFromCity() + bank.getDiscountAccreted()
                        + bank.getCarryInterest() + bank.getPlacementIncome();
                worstByWho = Math.max(worstByWho, Math.abs(byWho - bank.interestIncome()));
                if (bank.getInterestFromHouseholds() > 0) familyMonths++;
                if (bank.getInterestFromCity() + bank.getDiscountAccreted() > 0) cityMonths++;
                if (bank.getInterestFromBusinesses() > 0) businessMonths++;

                // Last month on file is what the month before read, whole.
                if (m > 0) {
                    worstLast = Math.max(worstLast, Math.abs(bank.lastMonth(Bank.Line.NET) - nets.get(m - 1))
                            + Math.abs(bank.lastMonth(Bank.Line.FEES) - fees.get(m - 1)));
                    lastChecked++;
                }
                nets.add(bank.getNetIncome());
                fees.add(bank.feeIncome());
                // ...and the year is the last twelve read, this one among them.
                if (m >= Bank.YEAR_MONTHS - 1) {
                    double year = 0;
                    for (int k = m - Bank.YEAR_MONTHS + 1; k <= m; k++) year += nets.get(k);
                    worstYear = Math.max(worstYear, Math.abs(bank.overYear(Bank.Line.NET) - year));
                    yearChecked++;
                }

                double footing = 0;
                for (Bank.WeightRow row : bank.weightTable()) footing += row.weighted();
                worstFoot = Math.max(worstFoot, Math.abs(footing - bank.getWeightedBook()));

                // The city borrows; the bank buys the paper at the next settle.
                if (m == 12) {
                    city.setCashForTest(city.getCash() + 20_000);
                    city.handleLongBondLogic(5_000, LongTermBond.MATURITIES[0], 100);
                    bond = city.getDebtManager().getDebt().get(city.getDebtManager().getDebt().size() - 1);
                }
                // ...and buys it back between two presses, which is in no income statement.
                if (m == 30 && bond != null && bond.bankPrincipal() > 0) {
                    city.setCashForTest(city.getCash() + 2 * bond.getOustandingPrincipal());
                    city.repurchaseDebt(bond);
                    buybackGain = bank.getTreasuryBuybackGain();
                    worstBetween = Math.max(worstBetween, Math.abs(bank.equityMovement().residual()));
                }
                // ...and the city puts capital into a bank that stands, between two presses.
                if (m == 45) {
                    rescued = city.recapitaliseBank(500);
                    worstBetween = Math.max(worstBetween, Math.abs(bank.equityMovement().residual()));
                }
            }
        } finally {
            System.setOut(out);
        }
        out.printf("   %d months: the founding in %d, dividends in %d; a treasury buyback moved its equity by $%,.2fk"
                + " and a rescue by $%,.2fk between two presses%n", played, foundingMonths, dividendMonths,
                buybackGain, rescued);
        out.printf("   interest from the families in %d months, the city in %d, the businesses in %d%n",
                familyMonths, cityMonths, businessMonths);
        assertTrue("fixture: the played city's first branch opened in it", foundingMonths == 1);
        assertTrue("fixture: the bank paid its owners in it", dividendMonths > 0);
        assertTrue("fixture: the treasury bought paper back from it, at a gain or a loss", Math.abs(buybackGain) > 1e-9);
        assertTrue("fixture: the city put capital into it", rescued > 0);
        close("its equity's movement left nothing unexplained, every month", worstResidual, 0, 1e-6);
        close("...nor between two presses, after a buyback and after a rescue", worstBetween, 0, 1e-6);
        assertTrue("fixture: the city and the businesses paid it interest", cityMonths > 0 && businessMonths > 0);
        close("its interest by who paid it is the whole of its interest, every month", worstByWho, 0, 1e-6);
        assertTrue("fixture: last month and the year were read in most months", lastChecked > 60 && yearChecked > 50);
        close("last month's column is what that month read, whole", worstLast, 0, 1e-9);
        close("...and the last twelve months add up to the last twelve read", worstYear, 0, 1e-6);
        close("the played bank's weight table foots every month", worstFoot, 0, 1e-6);

        out.println("\n--- (13) the rescue's guard is the model's ---");
        assertTrue("fixture: the bank stands and needs nothing", !bank.isInsolvent() && bank.recapitalisationNeeded() == 0);
        assertTrue("a bank that needs nothing is not offered a rescue", !city.canRecapitaliseBank());
        bank.setCash(bank.getCash() - bank.equity() - 1_000);          // a thousand under water
        double needed = bank.recapitalisationNeeded();
        city.setCashForTest(needed / 2);
        assertTrue("fixture: the bank is under water", bank.isInsolvent() && needed > 0);
        assertTrue("...a treasury holding half of what it needs cannot rescue it", !city.canRecapitaliseBank());
        city.setCashForTest(needed * 2);
        assertTrue("...one holding all of it can", city.canRecapitaliseBank());
        city.recapitaliseBank(needed);
        assertTrue("...and doing it stands the bank back up", !bank.isInsolvent());
    }

    /* ============ 7-11. THE BANK AS A BUSINESS WITH ITS CAPITAL (0.7.8) ============

       What 0.7.8 gave the bank to do with its capital, each caused on a bank
       built by hand: it sets aside for the loans that will not come back and
       draws a write-off against that first (7); it chooses the capital it
       holds from the worst year it has seen (8); under that it keeps its
       profit and lends slower, not not at all (9); inside its band it pays a
       share, over it the excess (10); and while it stands there is always
       something it will lend (11). Section (12), the reload, is on the
       played city in section 8.
       ================================================================= */

    /** A bank with one branch, this much capital and this much lent - equity is the capital, its cash what it lent past it. */
    static Bank lentOut(double capital, double book) {
        Bank b = new Bank();
        b.injectCapital(capital);
        b.lend(book);
        b.refresh(1, 100 * book + 1_000_000, 0, book, 0, 0);
        b.startMonth();
        return b;
    }

    /** The sectors' positions as the bank's provide() reads them: one sector, owing this against these assets. */
    static java.util.Map<String, double[]> owing(String sector, double principal, double assets) {
        java.util.Map<String, double[]> m = new java.util.LinkedHashMap<>();
        m.put(sector, new double[]{ principal, assets });
        return m;
    }

    static void theBankAsABusinessWithItsCapital() {

        out.println("\n--- (7) a loan is met by its year's allowance, a weakening borrower by its lifetime loss, a write-off by the allowance first ---");

        String IND = Sectors.INDUSTRY;
        Bank provider = new Bank();
        provider.injectCapital(100_000);
        provider.refresh(1, 1_000_000, 0, 0, 0, 0);
        double provided = 0, writtenOff = 0, worstMove = 0;

        // Month 1: a sound sector borrows 40,000 against 100,000 of assets.
        provider.startMonth();
        provider.lend(40_000);
        provider.refresh(1, 1_000_000, 0, 40_000, 0, 0);
        provider.provide(owing(IND, 40_000, 100_000), 0, 0);
        close("a new loan to a sound borrower is met by its year's expected loss, BASE_LOSS_RATE of it",
                provider.getSectorAllowance(IND), 40_000 * Bank.BASE_LOSS_RATE, 1e-9);
        assertTrue("...a stage-1 book", provider.getStage(IND) == 1);
        close("...and that is the month's provision", provider.provisions(), 40_000 * Bank.BASE_LOSS_RATE, 1e-9);
        worstMove = Math.max(worstMove, Math.abs(provider.equity() - provider.getOpeningEquity() - provider.getNetIncome()));
        provided += provider.provisions(); writtenOff += provider.getWriteOffs();
        provider.closeMonth();

        // Month 2: its assets fall to 30,000 - leverage 1.33, past the watch line.
        // Since 0.7.8 stage 2 is the lifetime expected loss off the curve the
        // month's slices are written off by: a loan's term of defaults at
        // this leverage, at LOSS_GIVEN_DEFAULT (see BankCheck (14)).
        double stage1 = 40_000 * Bank.BASE_LOSS_RATE;
        provider.startMonth();
        provider.provide(owing(IND, 40_000, 30_000), 0, 0);
        double lev = 40_000 / 30_000.0;
        // ...staged firm by firm since round 2: most of its firms are past
        // the line at 1.33, and the lifetime is on them (BankCheck (14)).
        double lifetime = staged(40_000, lev);
        out.printf("   weakened to %.2f times its assets: allowance %,.1f against a year's %,.1f%n",
                lev, provider.getSectorAllowance(IND), stage1);
        assertTrue("a borrower past SECTOR_WATCH_LEVERAGE moves to stage 2", provider.getStage(IND) == 2);
        close("...and its allowance rises toward its lifetime loss: a loan's term of defaults on the share of its firms past the line",
                provider.getSectorAllowance(IND), lifetime, 1e-9);
        assertTrue("...which is more than a year's loss", provider.getSectorAllowance(IND) > stage1);
        close("...and the rise is the month's provision, before anything is written off",
                provider.provisions(), lifetime - stage1, 1e-9);
        worstMove = Math.max(worstMove, Math.abs(provider.equity() - provider.getOpeningEquity() - provider.getNetIncome()));
        provided += provider.provisions(); writtenOff += provider.getWriteOffs();
        provider.closeMonth();

        // Month 3: it stays at 1.33 and its firms past the default point go -
        // the month's slice, written off through the lender the game uses.
        // (Until 0.7.8 its assets fell to 25,000, past the line, and the whole
        // sector was written down to RESTRUCTURE_TARGET of them.)
        provider.startMonth();
        double setAside = provider.getSectorAllowance(IND);
        BusinessDebtManager sliced = new BusinessDebtManager();
        sliced.issueLoan(IND, 40_000, 1);
        sliced.setAssets(IND, 30_000);
        double loss = sliced.restructureInsolventSectors();
        assertTrue("fixture: the month's slice is written off, and the sector is not restructured",
                loss > 0 && !sliced.wasRestructuredThisMonth(IND));
        provider.writeOffSector(IND, sliced.getWrittenOffThisMonth(IND));
        double left = sliced.getPrincipal(IND);
        provider.refresh(1, 1_000_000, 0, left, 0, 0);
        provider.provide(owing(IND, left, 30_000), 0, 0);
        out.printf("   a slice of %,.1f written off with %,.1f set aside: provision %,.1f%n", loss, setAside, provider.provisions());
        close("a write-off is drawn against the allowance its book held first",
                provider.getAllowanceUsed(), Math.min(loss, setAside), 1e-9);
        close("...and only what that did not cover reaches the month", provider.getWriteOffsBeyondAllowance(),
                Math.max(0, loss - setAside), 1e-9);
        close("...so the provision is the allowance's move plus the write-off",
                provider.provisions(), provider.getAllowance() - setAside + loss, 1e-9);
        assertTrue("...and the month's hit to equity is less than the write-off, because most was taken already",
                provider.provisions() < loss);
        double levAfter = left / 30_000;
        assertTrue("...and a borrower still past the watch line stays in stage 2, its staged loss struck on what it owes now",
                provider.getStage(IND) == 2 && levAfter > Bank.SECTOR_WATCH_LEVERAGE
                        && Math.abs(provider.getSectorAllowance(IND) - staged(left, levAfter)) < 1e-9);
        worstMove = Math.max(worstMove, Math.abs(provider.equity() - provider.getOpeningEquity() - provider.getNetIncome()));
        provided += provider.provisions(); writtenOff += provider.getWriteOffs();
        provider.closeMonth();

        // Month 4: what is left is repaid, and its allowance released.
        provider.startMonth();
        provider.takeRepayment(left);
        provider.refresh(1, 1_000_000, 0, 0, 0, 0);
        provider.provide(owing(IND, 0, 25_000), 0, 0);
        assertTrue("a repaid book holds nothing, and its allowance comes back as a release", provider.provisions() < 0
                && provider.getAllowance() == 0);
        worstMove = Math.max(worstMove, Math.abs(provider.equity() - provider.getOpeningEquity() - provider.getNetIncome()));
        provided += provider.provisions(); writtenOff += provider.getWriteOffs();
        provider.closeMonth();
        close("over the episode the provisions add up to the write-offs, to the cent", provided, writtenOff, 1e-9);
        close("...and every month its equity moved by its net income and nothing else", worstMove, 0, 1e-9);

        // The families: a cell past HOUSEHOLD_WATCH_MONTHS, scaled to its whole debt at the discharge line.
        close("a family owing under the watch line holds a year's loss",
                Bank.householdAllowance(1_000, Bank.HOUSEHOLD_WATCH_MONTHS), 1_000 * Bank.BASE_LOSS_RATE, 1e-12);
        close("...halfway from there to the discharge line, half its debt",
                Bank.householdAllowance(1_000, (Bank.HOUSEHOLD_WATCH_MONTHS + HouseholdBalance.BANKRUPT_AT_MONTHS) / 2),
                500, 1e-9);
        close("...and at the line, all of it - a discharge writes the whole debt off",
                Bank.householdAllowance(1_000, HouseholdBalance.BANKRUPT_AT_MONTHS), 1_000, 1e-9);
        double[][] poor = new double[FamilyStructure.values().length][PayTier.values().length];
        poor[FamilyStructure.COUPLE.ordinal()][0] = 500;
        java.util.function.ToDoubleBiFunction<FamilyStructure, PayTier> poorCensus =
                (s, t) -> poor[s.ordinal()][t.ordinal()];
        double[] lean = new double[HouseholdBalance.ROWS], eat = new double[HouseholdBalance.ROWS];
        double[] noBills = new double[HouseholdBalance.ROWS];
        lean[0] = 500 * 1.2;
        eat[0] = 1_000 * .25;
        HouseholdBalance borrowing = new HouseholdBalance();
        double firstWatched = -1;
        for (int m = 0; m < 60 && firstWatched < 0; m++) {
            borrowing.advanceMonth(poorCensus, lean, 1.0, noBills, eat, .25, .05, 1);
            if (borrowing.debtInTrouble() > 0) firstWatched = m;
        }
        double byCell = 0;
        for (Household c : borrowing.cells()) byCell += c.lossAllowance();
        out.printf("   a poor row went past the watch line in month %.0f: owes %,.1f, %,.1f of it in trouble, allowance %,.1f%n",
                firstWatched, borrowing.totalDebt(), borrowing.debtInTrouble(), borrowing.lossAllowance());
        assertTrue("fixture: a family borrowing to eat does pass the watch line", firstWatched >= 0);
        assertTrue("...and its book then holds more than a year's loss",
                borrowing.lossAllowance() > borrowing.totalDebt() * Bank.BASE_LOSS_RATE);
        close("...which is the rule cell by cell", borrowing.lossAllowance(), byCell, 1e-9);

        out.println("\n--- (8) a bank that has lived through a bad year chooses more capital than a young one ---");

        Bank young = new Bank(), scarred = new Bank(), ruined = new Bank(), small = new Bank();
        double book = 1_000_000;
        for (Bank b : new Bank[] { young, scarred, ruined }) {
            b.injectCapital(200_000);
            b.lend(book);
        }
        small.injectCapital(200_000);
        small.lend(50_000);
        for (int m = 1; m <= 24; m++) {
            for (Bank b : new Bank[] { young, scarred, ruined, small }) {
                b.startMonth();
                double lent = b == small ? 50_000 : book;
                // One month of the second year writes a sector down: 1% of the
                // book in the young bank's year, 6% in the scarred one's, 90%
                // in the ruined one's, and 40% of the small bank's small book.
                double off = m != 18 ? 0 : b == young ? .01 * lent : b == scarred ? .06 * lent
                        : b == ruined ? .90 * lent : .40 * lent;
                if (off > 0) b.writeOffSector(IND, off);
                b.refresh(1, 10_000_000, 0, lent, 0, 0);
                b.provide(owing(IND, lent, lent * 4), 0, 0);
                b.closeMonth();
            }
        }
        out.printf("   worst year: young %.2f%%, scarred %.2f%%, ruined %.2f%%, small %.2f%%;"
                + " targets %.2f%% / %.2f%% / %.2f%% / %.2f%%%n",
                young.getWorstLossRate() * 100, scarred.getWorstLossRate() * 100, ruined.getWorstLossRate() * 100,
                small.getWorstLossRate() * 100, young.capitalTarget() * 100, scarred.capitalTarget() * 100,
                ruined.capitalTarget() * 100, small.capitalTarget() * 100);
        close("a bank whose worst year was under the conservation buffer targets the minimum and the buffer",
                young.capitalTarget(), Bank.CAPITAL_RATIO + Bank.CONSERVATION_BUFFER, 1e-12);
        assertTrue("fixture: the scarred bank's worst year was past the conservation buffer",
                scarred.getWorstLossRate() > Bank.CONSERVATION_BUFFER);
        close("...one that has lived through a worse year holds a buffer that would take it and leave the minimum",
                scarred.capitalTarget(), Bank.CAPITAL_RATIO + scarred.getWorstLossRate(), 1e-12);
        assertTrue("...which is more than the young one holds", scarred.capitalTarget() > young.capitalTarget());
        close("...its worst year is its year's provisions over its weighted book",
                scarred.getWorstLossRate(), .06 + Bank.BASE_LOSS_RATE * 0, .002);
        close("...and never more than the whole Basel stack, MAX_BUFFER, however bad the year",
                ruined.capitalTarget(), Bank.CAPITAL_RATIO + Bank.MAX_BUFFER, 1e-12);
        double capacityOfOne = Bank.PAID_IN_PER_BRANCH / Bank.CAPITAL_RATIO;
        close("...and a loss on a book smaller than its founding capital carries is read over that capacity",
                small.getWorstLossRate(), .40 * 50_000 / capacityOfOne, .002);
        boolean floor = true;
        for (Bank b : new Bank[] { young, scarred, ruined, small, new Bank() }) {
            if (b.capitalTarget() < Bank.CAPITAL_RATIO + Bank.CONSERVATION_BUFFER - 1e-15) floor = false;
        }
        assertTrue("no bank ever targets less than the minimum and the conservation buffer", floor);
        close("...and it prices a loan's capital at the target it chose",
                scarred.capitalCharge(.03, Bank.PRIME_TERM_MONTHS, Bank.RISK_BUSINESS),
                Bank.RISK_BUSINESS * scarred.capitalTarget()
                        * (Bank.requiredReturn() - scarred.fundsTransferPrice(.03, Bank.PRIME_TERM_MONTHS)), 1e-15);

        out.println("\n--- (9) under its target it keeps its profit and lends slower, and under its minimum only to keep borrowers going ---");

        double B = 1_000_000;
        double target = Bank.CAPITAL_RATIO + Bank.CONSERVATION_BUFFER;
        Bank rebuilding = lentOut((Bank.CAPITAL_RATIO + target) / 2 * B, B);
        close("fixture: halfway between its minimum and its target", rebuilding.capitalRatio(),
                (Bank.CAPITAL_RATIO + target) / 2, 1e-12);
        assertTrue("it keeps every dollar of a profit", rebuilding.dividendDue(10_000) == 0);
        assertTrue("...and says so", rebuilding.payoutStance() == Bank.Payout.REBUILDING);
        close("...and a borrower's debt may grow RATIONED_GROWTH a month halfway there",
                rebuilding.lendingGrowthLimit(), Bank.RATIONED_GROWTH, 1e-12);
        double lastGrowth = -1;
        boolean rises = true, finite = true;
        for (int i = 0; i < 100; i++) {
            double ratio = Bank.CAPITAL_RATIO + (target - Bank.CAPITAL_RATIO) * i / 100.0;
            double g = lentOut(ratio * B, B).lendingGrowthLimit();
            double x = i / 100.0;
            if (Math.abs(g - Bank.RATIONED_GROWTH * x / (1 - x)) > 1e-9) finite = false;
            if (g < lastGrowth) rises = false;
            lastGrowth = g;
        }
        assertTrue("the limit is RATIONED_GROWTH x x/(1-x) from the minimum to the target", finite);
        assertTrue("...rising smoothly, never falling, as the ratio rises", rises);
        close("...from nothing at the minimum", lentOut(Bank.CAPITAL_RATIO * B, B).lendingGrowthLimit(), 0, 1e-12);
        assertTrue("...to no limit at all at the target",
                Double.isInfinite(lentOut(target * B + 1, B).lendingGrowthLimit()));

        // The desks that lend read it: a sector's debt grows at most that much this month.
        BusinessDebtManager desk = new BusinessDebtManager();
        desk.setPrimeRate(.05);
        desk.setAssets(IND, 1_000_000);
        desk.issueLoan(IND, 100_000, 1);
        desk.updateRates();
        double g = rebuilding.lendingGrowthLimit();
        desk.setCapitalRule(g, rebuilding.lendsOnlyToKeepBorrowersGoing());
        close("a sector owing 100,000 may borrow that share of it more this month", desk.capitalRoom(IND), g * 100_000, 1e-9);
        assertTrue("...a project inside it is funded", desk.canFundProject(IND, g * 100_000 * .9));
        assertTrue("...and one past it is not", !desk.canFundProject(IND, g * 100_000 * 1.1));
        assertTrue("...and the investor can say it was the bank's capital", desk.wasRefusedForCapital(IND));
        double lentShort = desk.coverShortfall(IND, -50_000, 0, 2);
        close("...and its losses are lent to that limit, however big the hole",
                lentShort, Math.max(g * 100_000, desk.getMonthlyInterest(IND) / (1 - Bank.LOAN_FEE) - 0), 1e-6);

        Bank underMinimum = lentOut(.05 * B, B);
        assertTrue("under its minimum it lends only to keep its borrowers going",
                underMinimum.lendsOnlyToKeepBorrowersGoing() && underMinimum.lendingGrowthLimit() == 0);
        BusinessDebtManager held = new BusinessDebtManager();
        held.setPrimeRate(.05);
        held.setAssets(IND, 1_000_000);
        held.issueLoan(IND, 100_000, 1);
        held.updateRates();
        held.setCapitalRule(underMinimum.lendingGrowthLimit(), underMinimum.lendsOnlyToKeepBorrowersGoing());
        assertTrue("...no project, however small", !held.canFundProject(IND, 1));
        double interestReserve = held.getMonthlyInterest(IND) / (1 - Bank.LOAN_FEE);
        close("...but a borrower short of cash is still lent its interest", held.coverShortfall(IND, -50_000, 0, 2),
                interestReserve, 1e-9);
        // ...and a loan that falls due is refinanced: the book does not grow.
        BusinessDebtManager maturing = new BusinessDebtManager();
        maturing.setPrimeRate(.05);
        maturing.setAssets(IND, 1_000_000);
        maturing.issueLoan(IND, 100_000, 1);
        maturing.updateRates();
        maturing.setCapitalRule(0, true);
        for (int m = 0; m < BusinessDebtManager.LOAN_TERM_MONTHS; m++) maturing.processMonth();
        double due = maturing.takeMaturedPrincipal(IND);
        assertTrue("fixture: its loan fell due", due > 0 && maturing.getPrincipal(IND) == 0);
        close("...and what fell due may be lent again under the minimum", maturing.capitalRoom(IND), due, 1e-9);
        // ...and the month's defaults still run: a borrower far past the line
        // loses its defaulted firms' slice (0.7.8), and one with nothing left
        // is still restructured whole.
        held.setAssets(IND, 10_000);
        assertTrue("...and a borrower past the line still has its defaulted firms written off",
                held.restructureInsolventSectors() > 0 && held.getDefaultedThisMonth(IND) > 0);
        held.setAssets(IND, -1);
        assertTrue("...and one with nothing left is still restructured whole",
                held.restructureInsolventSectors() > 0 && held.wasRestructuredThisMonth(IND));

        // The families: a poor row borrowing to eat, free, rationed and held.
        HouseholdBalance free = new HouseholdBalance(), rationed = new HouseholdBalance(), kept = new HouseholdBalance();
        for (HouseholdBalance hb : new HouseholdBalance[] { free, rationed, kept }) {
            for (int m = 0; m < 12; m++) hb.advanceMonth(poorCensus, lean, 1.0, noBills, eat, .25, .05, 1);
        }
        double owedBefore = rationed.totalDebt();
        rationed.setCapitalRule(.005, false);
        kept.setCapitalRule(0, true);
        free.setCapitalRule(Double.POSITIVE_INFINITY, false);
        for (HouseholdBalance hb : new HouseholdBalance[] { free, rationed, kept }) {
            hb.advanceMonth(poorCensus, lean, 1.0, noBills, eat, .25, .05, 1);
        }
        out.printf("   a poor row's month: free it drew %,.2f, rationed at 0.5%% %,.2f, held %,.2f (its interest %,.2f)%n",
                free.totalBorrowed(), rationed.totalBorrowed(), kept.totalBorrowed(), kept.totalInterest());
        assertTrue("fixture: free, it borrows more than its interest", free.totalBorrowed() > free.totalInterest() * 1.5);
        assertTrue("a family's debt may grow the month's share and its interest, and no more",
                rationed.totalBorrowed() * (1 + Bank.LOAN_FEE) <= owedBefore * .005 + rationed.totalInterest() * (1 + Bank.LOAN_FEE) + 1e-6
                        && rationed.totalBorrowed() < free.totalBorrowed());
        assertTrue("...and under the minimum it may draw its interest and nothing else",
                kept.totalBorrowed() <= kept.totalInterest() + 1e-9 && kept.totalBorrowed() > 0);

        out.println("\n--- (10) inside its band it pays a share of its profit, over the top it returns the excess ---");

        double top = Bank.CAPITAL_RATIO + Bank.CONSERVATION_BUFFER + Bank.MANAGEMENT_CUSHION;
        Bank inBand = lentOut((target + top) / 2 * B, B);
        assertTrue("fixture: inside its band, with no reserves at all", inBand.payoutStance() == Bank.Payout.PAYING
                && inBand.getCash() < 0);
        close("inside its band it pays PAYOUT_IN_BAND of its profit after tax", inBand.dividendDue(1_000),
                Bank.PAYOUT_IN_BAND * 1_000, 1e-9);
        double cashBefore = inBand.getCash();
        double paid = inBand.payOwners(1_000);
        close("...and pays it though it holds no reserves - out of what it borrows, like any bank",
                cashBefore - inBand.getCash(), paid, 1e-9);
        close("...nothing on a loss", inBand.dividendDue(-1_000), 0, 0);
        Bank atTarget = lentOut(target * B + 100, B);
        close("...and never so much that it would fall under its target", atTarget.dividendDue(10_000), 100, 1e-6);

        Bank over = lentOut(.40 * B, B);
        double excess = over.equity() - top * B;
        assertTrue("fixture: far over the top of its band", over.payoutStance() == Bank.Payout.RETURNING);
        close("over the top it pays its share and a twelfth of the excess", over.dividendDue(1_000),
                Bank.PAYOUT_IN_BAND * 1_000 + excess / Bank.EXCESS_PAYOUT_MONTHS, 1e-6);
        double excessBefore = over.excessCapital();
        for (int m = 0; m < 12; m++) {
            over.startMonth();
            over.payOwners(0);
        }
        double returned = 1 - over.excessCapital() / excessBefore;
        out.printf("   a year of it returned %.1f%% of the excess, on no profit%n", returned * 100);
        close("...so a year returns all but (11/12)^12 of it, on no profit at all",
                returned, 1 - Math.pow(1 - 1 / Bank.EXCESS_PAYOUT_MONTHS, 12), 1e-9);
        close("...and it counts what it paid over the year", over.dividendsOverYear(),
                excessBefore * returned, 1e-6);
        Bank noBranch = new Bank();
        noBranch.injectCapital(1_000);
        assertTrue("under its target, under its minimum, failed or with no bank it pays nothing",
                rebuilding.dividendDue(1_000) == 0 && underMinimum.dividendDue(1_000) == 0
                        && noBranch.dividendDue(1_000) == 0);

        out.println("\n--- (11) while the bank stands, there is always something it will lend ---");

        boolean alwaysCapacity = true, alwaysKeepsGoing = true, growsAboveMinimum = true;
        for (double ratio : new double[]{ .001, .01, .05, .079, .08, .081, .09, .1, .104, .105, .12, .2, .5 }) {
            Bank standing = lentOut(ratio * B, B);
            if (!(standing.capacity() > 0)) alwaysCapacity = false;
            if (ratio > Bank.CAPITAL_RATIO + 1e-9 && !(standing.lendingGrowthLimit() > 0)) growsAboveMinimum = false;
            BusinessDebtManager lender = new BusinessDebtManager();
            lender.setPrimeRate(.05);
            lender.setAssets(IND, 1_000_000);
            lender.issueLoan(IND, 100_000, 1);
            lender.updateRates();
            lender.setLendingOpen(!standing.isInsolvent());
            lender.setCapitalRule(standing.lendingGrowthLimit(), standing.lendsOnlyToKeepBorrowersGoing());
            if (!(lender.coverShortfall(IND, -50_000, 0, 2) > 0)) alwaysKeepsGoing = false;
        }
        assertTrue("a standing bank at any capital has capacity", alwaysCapacity);
        assertTrue("...lends more than nothing a month whenever it is over its minimum", growsAboveMinimum);
        assertTrue("...and at any capital lends a borrower short of cash its interest", alwaysKeepsGoing);
        Bank failed = lentOut(.05 * B, B);
        failed.refresh(1, 100 * B, 0, B * .5, 0, 0);
        failed.resolveIfFailed();
        close("...where a failed one, in resolution, has none - resolution is as it was", failed.capacity(), 0, 0);
    }

    /* ============ 10. the bank pays for the city's paper (2026-09-21) ============

       Jerus, on the central bank that is coming: "the central bank would buy
       gbonds or sell gbonds from thin air". It can only trade paper with a
       commercial bank that owns the paper - and this one never paid for it.
       Every issue lands between two presses; the settlement snapshot was
       taken after the top-of-month clear, so bank.lend() was handed zero for
       every bond the city ever sold. The book still rose by the face (it is
       read off the treasury's principal), the coupons and the principal still
       came in, and the cash never went out: equity from nothing, and money
       for the treasury from nowhere. See Game, THE BANK PAYS FOR THE CITY'S
       PAPER.

       CAUSED: a working city with a bank and no debt, which then issues a
       note and a serial bond between two presses and turns one month.

       "LESS THE ORDINARY MONTH", STRUCK THE WAY SECTION 8 STRIKES IT. The
       bank's cash moves for a dozen reasons in any month. Its equity is its
       cash plus its books plus the desk, less the hot money it owes - so the
       cash moves by the equity's move, less what went into each book. And
       section 8 asserts, every month, that the equity moves by net income and
       capital and nothing else. Put the two together, take out the discount
       (which is in the net income and is the paper's), and what is left is
       the month the bank would have had anyway; the paper is the difference.
       On the old order the bank paid nothing, its equity rose by the face
       with no income behind it, and the four assertions below that read the
       settle fail: the cash, the figure the settle reports, the equity, and
       the reloaded city's settle.

       SINCE 0.7.1 THE BANK IS THE RESIDUAL BUYER: the households take their
       share at the settle first (Game, THE HOUSEHOLDS TAKE THEIR SHARE), so
       the bank pays what the treasury received less what they paid, and its
       book rises by its own face. And the discount ACCRETES: what is taken
       out of the net income above is the month's accretion on the bank's
       share, not the whole discount - and the part that pays for the rest of
       the paper's life sits against the book as unearned, so the equity does
       not move for it. Carried on through the note's six months, the
       accretion adds up to the paper's face less what it raised.

       AND THE HOUSEHOLDS HOLD THEIR PAPER THROUGH THE SETTLE, which the
       arithmetic above needs and which was never asserted until 0.7.2. If
       the paper yields no more than the bank pays savers, the households
       sell a tenth of it back to the bank's desk that month
       (HouseholdBalance.sellPaperForSpread()) - bank cash for paper, in the
       month under test, and in neither "the ordinary month" nor the settle.
       The bank raises its deposit rate in the settle month to fund the
       settle, and with a $10,000k note and a $20,000k serial it raised it to
       within a fifth of a point of the paper's yield (6.383% against 6.560%)
       - and past it once the household cells under half a household were
       emptied (0.7.2): 6.825% against 6.726%, and the desk bought $1,080k
       back. Half the issue leaves the bank less to fund and
       the households a quarter of a point (6.359% against 6.615%), and the
       fixture line after the month says the desk bought nothing.
       ============================================================ */
    static void theBankPaysForTheCitysPaper() throws Exception {
        out.println("\n--- and the bank pays for the city's paper ---");

        Path paperRoot = Files.createTempDirectory("bankcheck-paper");
        GameFiles paperFiles = new GameFiles(paperRoot.resolve("data"), paperRoot.resolve("no-legacy"));
        Game city = new Game(paperFiles);
        System.setOut(quiet);
        try {
            city.run();
            city.getForeignAccounts().pinRate(1.0);
            // The city section 8 puts up, standing on month one, for its reason:
            // a city that works, so what is measured is the bank.
            city.getLandManager().setOwnedSqFt(30_000_000);
            city.buildStack(template(city, "House"), 400, true);
            city.buildStack(template(city, "Convenience Store"), 8, true);
            city.buildStack(template(city, "Small Grocery Store"), 2, true);
            city.buildStack(template(city, "Bakery"), 1, true);
            city.buildStack(template(city, "Paved Road"), 20, true);
            city.buildStack(template(city, "Industrial Bakery"), 2, true);
            city.buildStack(template(city, "Construction Depot"), 4, true);
            city.buildStack(template(city, "Coal Power Plant"), 1, true);
            city.buildStack(template(city, "Water Treatment Plant"), 1, true);
            city.simulateMonths(24);
        } finally {
            System.setOut(out);
        }
        Bank bank = city.getBank();
        assertTrue("fixture: the city has a working bank and owes nothing yet",
                bank.getBranches() >= 1 && city.getDebtManager().getAllPrincipal() == 0);

        /* ---- between the presses: a note and a serial bond ---- */
        double treasuryBefore = city.getCash();
        double faceBefore = city.getDebtManager().getDomesticPrincipal();
        double cashBefore = bank.getCash();
        double sectorBefore = bank.getSectorBook(), cityBefore = bank.getCityBook();
        double householdBefore = bank.getHouseholdBook(), carryBefore = bank.getCarryBook();
        double securitiesBefore = bank.getSecurities();
        double hotBefore = Math.max(0, bank.getForeignDeposits());
        double equityBefore = bank.equity();
        // ...and what it has set aside against its books (0.7.8): a provision
        // is in the net income and moves no cash, so the cash's move adds it
        // back, the way it takes the books' moves out.
        double allowanceBefore = bank.getAllowance();
        System.setOut(quiet);
        try {
            city.handleTBillLogic(5_000, 6, 1000);
            city.handleMediumBondLogic(10_000, 10, 1000);
        } finally {
            System.setOut(out);
        }
        double received = city.getCash() - treasuryBefore;
        double face = city.getDebtManager().getDomesticPrincipal() - faceBefore;
        double discount = face - received;
        out.printf("   the treasury sold $%,.2fk of paper for $%,.2fk (a $%,.2fk discount)%n",
                face, received, discount);
        assertTrue("fixture: the city issued a note and a serial bond, below par",
                received > 0 && discount > 0);
        close("between the presses the bank has not paid yet", bank.getCash(), cashBefore, 0);
        close("...and owes the treasury exactly what it received",
                city.getCityPaperUnsettled(), received, 1e-9);

        /* ---- saved in between, and the reloaded city's bank still pays ---- */
        Game twin;
        System.setOut(quiet);
        try {
            city.saveGame(3, "paper, unpaid");
            twin = new Game(paperFiles);
            twin.loadGameSave(3);
        } finally {
            System.setOut(out);
        }
        close("a city saved between the issue and the settle still owes its bank's payment",
                twin.getCityPaperUnsettled(), received, 1e-9);

        /* ---- the month turns ---- */
        System.setOut(quiet);
        try {
            city.simulateMonths(1);
            twin.getForeignAccounts().pinRate(1.0);
            twin.simulateMonths(1);
        } finally {
            System.setOut(out);
        }

        close("fixture: the households held their paper through the settle - the desk bought none",
                bank.getPaperBoughtFromHouseholds(), 0, 0);

        // The bank's share of the principal - what reaches it at the settle.
        double repaid = city.getBankPrincipalRepaidThisMonth();
        double explained = bank.getNetIncome() + capitalMoved(bank);
        // The month's accretion on the bank's share (0.7.1): the part of the
        // discount in this month's net income, which is the paper's.
        double accreted = city.getDebtManager().getAccretedForBank();
        double householdsPaid = city.getHouseholdsBoughtPaper();
        double bankPaid = received - householdsPaid;
        double ordinary = (explained - accreted)
                - (bank.getSectorBook() - sectorBefore)
                - (bank.getHouseholdBook() - householdBefore)
                - (bank.getCarryBook() - carryBefore)
                - (bank.getSecurities() - securitiesBefore)
                + (Math.max(0, bank.getForeignDeposits()) - hotBefore)
                + (bank.getAllowance() - allowanceBefore)
                + repaid;
        double cashMoved = bank.getCash() - cashBefore;
        out.printf("   the bank's cash moved $%,.2fk: $%,.2fk the ordinary month, $%,.2fk the paper%n",
                cashMoved, ordinary, cashMoved - ordinary);

        out.printf("   the households paid $%,.2fk of it at the settle, the bank $%,.2fk%n",
                householdsPaid, bankPaid);
        close("the bank's cash fell by exactly what the treasury received less what the households"
                + " paid, less the ordinary month", cashMoved - ordinary, -bankPaid, 1e-6);
        close("...which is the figure the settle reports", city.getCityPaperSettled(), bankPaid, 1e-9);
        close("...and between them the holders paid every dollar the treasury received",
                city.getCityPaperSettled() + householdsPaid, received, 1e-9);
        double bankFace = city.getDebtManager().bankPrincipal();
        close("...its book rose by its own face, less what the city repaid it this month",
                bank.getCityBook() - cityBefore, bankFace, 1e-6);
        close("...and its equity by net income and capital, and nothing else - the discount, not the face",
                bank.equity() - equityBefore - explained, 0, 1e-6);

        close("...and once it has paid it owes nothing for the paper",
                city.getCityPaperUnsettled(), 0, 0);
        MoneyAudit.Result audit = city.getLastMoneyAudit();
        out.printf("   %s%n", audit);
        assertTrue("MoneyAudit still closes, on the month the bank paid",
                Math.abs(audit.residual) < .01);
        close("the reloaded city's bank paid the same, at its settle",
                twin.getCityPaperSettled(), bankPaid, 1e-9);
        close("...and its households the same", twin.getHouseholdsBoughtPaper(), householdsPaid, 1e-9);
        assertTrue("...and its month closes too", Math.abs(twin.getLastMoneyAudit().residual) < .01);

        /* ---- the discount accretes (0.7.1) ---- */
        Debt note = null, serialBond = null;
        for (Debt d : city.getDebtManager().getDebt()) {
            if (d instanceof ShortTermTBill) note = d;
            if (d instanceof MediumTermBond) serialBond = d;
        }
        assertTrue("fixture: the note and the serial are on the books", note != null && serialBond != null);
        double noteDiscount = note.getIssueDiscount(), serialDiscount = serialBond.getIssueDiscount();
        close("each piece carries its own discount, which together is face less what it raised",
                noteDiscount + serialDiscount, discount, 1e-6);
        double noteShare = note.bankPrincipal() / note.getOustandingPrincipal();
        double serialShare = serialBond.bankPrincipal() / serialBond.getOustandingPrincipal();
        double oneMonth = noteShare * noteDiscount / note.getDuration()
                + serialShare * serialDiscount / serialBond.getDuration();
        close("the bank's interest in the settle month carries one month of its share of the discount,"
                + " not the whole", accreted, oneMonth, 1e-6);
        assertTrue("...which is well short of the whole (the old rule booked all of it)",
                accreted < .5 * (noteShare * noteDiscount + serialShare * serialDiscount));
        close("the rest sits against its book, unearned, so its equity did not move for the discount",
                bank.getUnearnedDiscount(),
                city.getDebtManager().bankUnearnedDiscount(), 1e-9);
        close("...and it is exactly the bank's share less what accreted",
                bank.getUnearnedDiscount(),
                noteShare * noteDiscount + serialShare * serialDiscount - oneMonth, 1e-6);

        // Through the note's life: the discount runs down a month at a time and
        // is gone the month the note is repaid.
        double noteLeftBefore = note.getDiscountLeft();
        double runsDown = noteDiscount - noteLeftBefore;
        for (int m = 1; m < note.getDuration(); m++) {
            double left = note.getDiscountLeft();
            System.setOut(quiet);
            try { city.simulateMonths(1); } finally { System.setOut(out); }
            runsDown += left - note.getDiscountLeft();
        }
        close("over the note's life its accretion adds up to its face less what it raised",
                runsDown, noteDiscount, 1e-6);
        close("...and nothing of it is left unearned once it is repaid", note.getDiscountLeft(), 0, 1e-9);
        assertTrue("...and it is off the books", !city.getDebtManager().getDebt().contains(note));
    }
}

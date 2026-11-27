package ham.citybuildersim;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * The landlords' insured mortgages (0.7.11): the instrument, the lender's
 * tests, the city's insurance and the bank's book.
 *
 * WHAT THIS HAS TO PROVE (Jerus, 2026-09-24: "Mortgages", "CMHC (Canada)",
 * "Keep it", "Insured by the city"):
 *
 *   1. THE ANNUITY: the payment is the level annuity; the balance after k
 *      payments is the closed form; interest and principal add up to the
 *      payment every month; nothing is owed after
 *      Mortgage.MORTGAGE_AMORTIZATION_MONTHS.
 *   2. ORIGINATION: the loan is the cost less what the landlord put in, and it
 *      put in at least 1 - MORTGAGE_MAX_LOAN_TO_COST; the premium is CMHC's
 *      5.00% and the forty-year surcharge on the loan, added to the principal
 *      and received by the treasury; a played month that writes one closes.
 *   3. THE DOWN PAYMENT: a landlord short of it on one holds, in words.
 *   4. THE LENDER'S TEST AT ITS LINE: MORTGAGE_DEBT_COVERAGE is financed, a
 *      hair under is declined, in words; each size the halving tries is read
 *      at its own rent roll (RES3c).
 *   5. FIXED FOR THE TERM: the dial moving does not move the payment; at
 *      MORTGAGE_TERM_MONTHS it renews at the day's rate over what is left.
 *      And the rent floor reads its interest and not its principal.
 *   6. INSURANCE: a landlord written down - a slice, and the backstop - owes
 *      less; the bank loses nothing on the insured part; the treasury pays
 *      the bank exactly that part, on its claims line; the month closes.
 *   7. THE BANK: an insured mortgage weighs nothing and carries no
 *      allowance. It is priced with no loss and with the capital its
 *      leverage requirement ties up (round 2). It is not rationed by
 *      capital while the risk-based requirement binds, and is rationed
 *      with the rest when the leverage ratio does.
 *   8. SAVE AND LOAD mid-term, every field; an older save's loans unchanged.
 *   9. WHERE THE OLD RULE SAID NO: a landlord the 1.25x interest test refused
 *      on 100% of the cost, at its own risk's rate, that the lender's test on
 *      85% passes - and it builds.
 *
 * ROUND 2 (Jerus, 2026-09-24: "Basel leverage ratio", "Close losing
 * branches", "Pay out after principal"):
 *
 *  10. THE LEVERAGE RATIO: a bank whose book is mostly insured mortgages is
 *      held to Bank.LEVERAGE_RATIO_MIN of everything it has lent. Its target
 *      scales with its risk-based one. Its payout, its buybacks and its desk
 *      stop at the leverage requirement when that is the larger, and its
 *      lending tightens on it. A bank under it opens no branch for the
 *      capital, and its weight table still foots.
 *  11. A BRANCH THAT DOES NOT PAY IS CLOSED (rewritten for 0.7.19): every
 *      branch past what the month's fees cover closes that month, with no
 *      fuse to wait out; a bank whose fees cover its branches closes
 *      nothing; the last branch stays; in a played city the closure is a
 *      retired building, and the bank's equity and the money audit close
 *      through it; and what the rule reads survives a save. (Until 0.7.19:
 *      after Bank.BRANCH_CLOSE_MONTHS of a book that did not keep its
 *      branches' staff, and not before.)
 *  12. PAYOUTS AFTER PRINCIPAL: a landlord with a mortgage pays Equity.PAYOUT
 *      of its income less the principal it repaid, and nothing when the
 *      principal is the larger (the cushion that counts the payments is
 *      ExchangeCheck's).
 *
 * Every fixture causes its condition. FS9: the played landlords' town moved
 * with HN1-A (they order at the door's price): section 12's lean month no
 * longer came in the sixty months it searched, and the others waited on what
 * the played town happened to do. Each is set, not waited for:
 *   2 and 3 play one month of the founded town (short of doors from the
 *     first, so its landlords order in it): 2 reads the premium, the rate, the
 *     loan and the audit in it; 3 seeds the landlords' book at half what the
 *     shares are worth, which is what makes them hold, and reads that month.
 *   5 (the rent floor), 6 (insurance) and 8 (save and load) stand
 *     HOMES_STANDING homes for the landlords in a town whose works stand,
 *     hold them from ordering, and write their mortgages and loans by hand
 *     (issueMortgage, issueLoan): 5 plays the month that reads them, 6 owes
 *     1.2 times what it owns for a slice to default, 8 plays the months a
 *     term needs to be mid-way.
 *   9 sets the tax policy, the builders' offset and the landlords' till.
 *   12 writes a small and a large mortgage by hand, at PAYOUT_FIXTURE_RATE,
 *     on homes that earn, and reads the month after each.
 */
public class MortgageCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-78s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= Math.max(tol, Math.abs(expected) * tol);
        if (!ok) fails++;
        out.printf("%-78s %s  %,.9f against %,.9f%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    static void same(String label, String actual, String expected) {
        boolean ok = actual != null && actual.equals(expected);
        if (!ok) fails++;
        out.printf("%-78s %s%n      \"%s\"%s%n", label, ok ? "OK" : "FAIL", actual,
                ok ? "" : "\n      expected \"" + expected + "\"");
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    static BuildingsTemplate template(Game g, String name) {
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException("no template named " + name);
    }

    static final String RE = Sectors.REAL_ESTATE;
    static final double FEE = Bank.LOAN_FEE;

    /** The premium's rate from the schedule's own constants: the base and a surcharge for each step past the base years. */
    static double scheduleRate() {
        int years = Mortgage.MORTGAGE_AMORTIZATION_MONTHS / 12;
        int steps = (int) Math.ceil((years - Mortgage.PREMIUM_BASE_YEARS) / (double) Mortgage.PREMIUM_SURCHARGE_STEP_YEARS);
        return Mortgage.PREMIUM_RATE + Mortgage.PREMIUM_SURCHARGE * Math.max(0, steps);
    }

    /** The level payment, written out here rather than asked of the class under test. */
    static double level(double balance, double annual, int months) {
        double r = annual / 12;
        return balance * r / (1 - Math.pow(1 + r, -months));
    }

    /**
     * A city whose landlords buy on mortgages: founded, funded, given ground
     * and jobs, with a bank, and its housing left to the landlords - who are
     * short of doors from the first month, so they order at once.
     */
    static Game landlordCity(Path root, String name) {
        return landlordCity(root, name, false);
    }

    /** ...with its works standing from the start when asked: see payoutsAfterPrincipal(). */
    static Game landlordCity(Path root, String name, boolean worksStanding) {
        return landlordCity(root, name, worksStanding, 0);
    }

    /**
     * ...and, when its works stand, with that many more depots standing beside the founding yard (PT1-M4). The works
     * department that built for a city nobody lived in is retired: this town's builders are the Builders' Yard the city
     * founds with and what depots stand, staffed from its hundred people at the per-type fill, and its repairs (149
     * points a month for the works standing) were more than they could do - 13 homes, then none for sixty months.
     * Standing depots (buildStack(t, n, true), as foundingYard() stands its one) are builders enough to out-work them.
     */
    static Game landlordCity(Path root, String name, boolean worksStanding, int depotsStanding) {
        GameFiles files = new GameFiles(root.resolve(name), root.resolve(name + "-no-legacy"));
        Game g = new Game(files);
        quietly(() -> {
            g.run();
            // THE TREASURY THIS FIXTURE IS WRITTEN AGAINST: its works are
            // bought out of cash, the Wealthy preset's, explicitly.
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 50_000_000L);
            if (worksStanding) {
                // ...all but the depots, which a city with nobody living in it
                // yet could not staff: they are built, by the city's own works.
                for (String[] w : new String[][] { { "Industrial Bakery", "3" }, { "Coal Power Plant", "1" },
                        { "Water Treatment Plant", "1" }, { "Commercial Bank", "1" }, { "Convenience Store", "4" } }) {
                    g.buildStack(template(g, w[0]), Integer.parseInt(w[1]), true);
                }
                if (depotsStanding > 0) g.buildStack(template(g, "Construction Depot"), depotsStanding, true);
                LongPlaytest.build(g, "Construction Depot", 4);
                return;
            }
            LongPlaytest.build(g, "Industrial Bakery", 3);
            LongPlaytest.build(g, "Construction Depot", 4);
            LongPlaytest.build(g, "Coal Power Plant", 1);
            LongPlaytest.build(g, "Water Treatment Plant", 1);
            LongPlaytest.build(g, "Commercial Bank", 1);
            LongPlaytest.build(g, "Convenience Store", 4);
        });
        return g;
    }

    public static void main(String[] args) throws Exception {
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        Path root = TempDirs.create("mortgagecheck");

        annuity();
        origination(root);
        downPayment(root);
        lendersTest();
        fixedForTheTerm(root);
        insurance(root);
        theBank(root);
        saveAndLoad(root);
        whereTheOldRuleSaidNo(root);
        leverageRatio();
        branchesClose(root);
        payoutsAfterPrincipal(root);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ============================== 1. THE ANNUITY ============================== */

    static void annuity() {
        out.println("--- 1. the annuity: a level payment, the closed form, and nothing left at the end ---");

        double rate = .05, loan = 100_000;
        Mortgage m = new Mortgage(RE, loan, 0, rate, true);
        double b0 = m.getOutstandingPrincipal();
        int n = Mortgage.MORTGAGE_AMORTIZATION_MONTHS;
        double a = level(b0, rate, n);
        close("the payment is B r / (1 - (1 + r)^-n), r a twelfth of the rate", m.getMonthlyPayment(), a, 1e-12);
        close("...which Mortgage.payment() gives for any balance, rate and term",
                Mortgage.payment(12_345, .0731, 360), level(12_345, .0731, 360), 1e-12);

        // Through the lender, so a renewal at the same rate is part of the walk.
        BusinessDebtManager credit = new BusinessDebtManager();
        credit.setInsuredMortgageRate(rate);
        credit.getLoans().add(m);
        double worstSplit = 0, worstLevel = 0, worstClosed = 0, repaid = 0, interestPaid = 0;
        int[] probes = { 1, 12, Mortgage.MORTGAGE_TERM_MONTHS - 1, Mortgage.MORTGAGE_TERM_MONTHS, 240, n - 1 };
        double r = rate / 12;
        // The first three payments line by line...
        for (int k = 1; k <= 3; k++) {
            double before = m.getOutstandingPrincipal();
            double interest = m.getMonthlyInterestExpense();
            double pay = m.getMonthlyPayment();
            credit.processMonth();
            double principal = credit.takeMaturedPrincipal(RE);
            repaid += principal;
            interestPaid += interest;
            worstSplit = Math.max(worstSplit, Math.abs(interest + principal - pay) / a);
            worstLevel = Math.max(worstLevel, Math.abs(pay - a) / a);
            close("...interest is the balance at a twelfth of the rate, month " + k, interest, before * r, 1e-12);
            double closed = b0 * Math.pow(1 + r, k) - a * (Math.pow(1 + r, k) - 1) / r;
            worstClosed = Math.max(worstClosed, Math.abs(m.getOutstandingPrincipal() - closed) / b0);
        }
        // The rest of the walk, without a line a month.
        for (int k = 4; k <= n; k++) {
            double interest = m.getMonthlyInterestExpense();
            double pay = m.getMonthlyPayment();
            credit.processMonth();
            double principal = credit.takeMaturedPrincipal(RE);
            repaid += principal;
            interestPaid += interest;
            worstSplit = Math.max(worstSplit, Math.abs(interest + principal - pay) / a);
            worstLevel = Math.max(worstLevel, Math.abs(pay - a) / a);
            for (int p : probes) {
                if (k == p && k < n) {
                    double closed = b0 * Math.pow(1 + r, k) - a * (Math.pow(1 + r, k) - 1) / r;
                    worstClosed = Math.max(worstClosed, Math.abs(m.getOutstandingPrincipal() - closed) / b0);
                }
            }
        }
        out.printf("   %d payments of $%,.4fk: $%,.4fk of principal and $%,.4fk of interest%n", n, a, repaid, interestPaid);
        close("interest plus principal is the payment, every month", worstSplit, 0, 1e-9);
        close("...and the payment is level, a renewal at the same rate included", worstLevel, 0, 1e-9);
        close("the balance after k payments is the closed form, B(1+r)^k - A((1+r)^k - 1)/r", worstClosed, 0, 1e-9);
        close("...which Mortgage.balanceAfter() gives", Mortgage.balanceAfter(b0, rate, n, 240),
                b0 * Math.pow(1 + r, 240) - a * (Math.pow(1 + r, 240) - 1) / r, 1e-9);
        close("after MORTGAGE_AMORTIZATION_MONTHS payments nothing is owed", m.getOutstandingPrincipal(), 0, 0);
        close("...every dollar of principal came back through the settle", repaid, b0, 1e-9);
        assertTrue("...and the mortgage is closed and off the books", credit.getMortgageCount(RE) == 0 && credit.getPrincipal(RE) == 0);
        close("...the payments were the annuity n times over", repaid + interestPaid, a * n, 1e-9);
    }

    /* ============================== 2. ORIGINATION ============================== */

    static void origination(Path root) throws Exception {
        out.println("\n--- 2. origination: the loan is the cost less the landlord's funds, the premium on top ---");

        double rate = .045, cost = 1_000_000;
        BusinessDebtManager credit = new BusinessDebtManager();
        credit.setInsuredMortgageRate(rate);
        credit.setAssets(RE, 5_000_000);
        credit.startAuditMonth();
        double till = Mortgage.ownFundsFor(cost);
        double shortfall = cost - till;
        assertTrue("the lender writes it with the least till the down payment allows",
                credit.canFundMortgage(RE, shortfall, cost));
        Mortgage m = credit.issueMortgage(RE, shortfall, 7);
        double principal = m.getOutstandingPrincipal();
        double fee = FEE * principal;
        double handed = m.getLoan() - fee;
        double put = cost - m.getLoan();
        close("the landlord is handed the loan less its fee - the shortfall, to the cent", handed, shortfall, 1e-9);
        close("...so its till pays the building and the fee and ends at nothing", till + handed - cost, 0, 1e-6);
        close("the loan is the cost less what the landlord put in", m.getLoan(), cost - put, 1e-12);
        close("...and it put in 1 - MORTGAGE_MAX_LOAN_TO_COST of the cost, the least it may", put / cost,
                1 - Mortgage.MORTGAGE_MAX_LOAN_TO_COST, 1e-9);
        close("the premium is the schedule's rate: 5.00% and 0.25% for each five years past 25",
                Mortgage.premiumRate(), scheduleRate(), 1e-15);
        close("...which is PREMIUM_RATE and three surcharges at forty years", Mortgage.premiumRate(),
                Mortgage.PREMIUM_RATE + 3 * Mortgage.PREMIUM_SURCHARGE, 1e-15);
        close("...of the loan", m.getPremium(), scheduleRate() * m.getLoan(), 1e-12);
        close("...added to the principal", principal, m.getLoan() + m.getPremium(), 1e-12);
        close("the bank lends the principal", credit.getLentThisMonth(), principal, 1e-12);
        close("...keeps Bank.LOAN_FEE of it", credit.getFeesThisMonth(), fee, 1e-12);
        close("...and the premium is the month's, for the treasury", credit.getPremiumsThisMonth(), m.getPremium(), 1e-12);
        close("it is written at the insured rate", m.getAnnualRate(), rate, 0);
        assertTrue("...insured, for MORTGAGE_TERM_MONTHS, over MORTGAGE_AMORTIZATION_MONTHS",
                m.isInsured() && m.getRemainingMonths() == Mortgage.MORTGAGE_TERM_MONTHS
                        && m.getAmortizationLeft() == Mortgage.MORTGAGE_AMORTIZATION_MONTHS);
        assertTrue("a cent less of its own and the lender refuses it: the down payment",
                !credit.canFundMortgage(RE, shortfall + .01, cost)
                        && BusinessDebtManager.MORTGAGE_DOWN_PAYMENT.equals(credit.getMortgageRefusal()));

        out.println("\n--- 2. ...and a played month that writes one: the treasury takes the premium, and it closes ---");
        Game g = landlordCity(root, "origination");
        BusinessDebtManager lender = g.getEconomyManager().getBusinessDebtManager();
        // FS9: ONE MONTH, NOT A WAIT. The town is founded short of doors, nothing on site and nothing owed, so its landlords order a
        // House at the door in the first month it plays and the lender writes it then: that month is played, once, and read. (A mortgage
        // written by hand between months would move no money, and what this section asks is the month's: the premium taken by the
        // treasury, the loan handed over less its fee, the audit closed.)
        assertTrue("fixture: the landlords have nothing on site and owe nothing before the month",
                g.getBuildingManager().getUnderConstructionBySector(RE) == 0 && lender.getMortgages(RE).isEmpty());
        quietly(() -> g.simulateMonths(1));
        int month = g.getMonth();
        assertTrue("fixture: the landlords wrote a mortgage in the one month played", lender.getMortgagesWrittenThisMonth() > 0);
        if (lender.getMortgagesWrittenThisMonth() == 0) return;
        double premiums = 0, loans = 0;
        boolean atTheRate = true;
        for (Mortgage one : lender.getMortgages(RE)) {
            if (one.getMonthStarted() == month) {
                premiums += one.getPremium();
                loans += one.getLoan();
                atTheRate &= one.getAnnualRate() == lender.getInsuredMortgageRate();
            }
        }
        assertTrue("it was written at the month's insured rate, the bank's pushed in with prime", atTheRate);
        double spent = g.getInvestedThisMonth(RE);
        out.printf("   month %d: %s%n   spent $%,.0fk on the building, borrowed $%,.0fk, premium $%,.0fk%n",
                month, g.getLastInvestment(RE), spent, loans, premiums);
        close("the treasury's revenue line is the premiums written that month",
                g.getEconomyManager().getNationalAccounts().getMortgagePremiums(), premiums, 1e-9);
        assertTrue("...and the budget's revenue carries it",
                g.getEconomyManager().getNationalAccounts().getTotalRevenue()
                        >= g.getEconomyManager().getNationalAccounts().getMortgagePremiums());
        assertTrue("the loan was no more than MORTGAGE_MAX_LOAN_TO_COST of what the building cost",
                loans <= Mortgage.MORTGAGE_MAX_LOAN_TO_COST * spent * (1 + 1e-9) && spent > 0);
        close("the month's money audit closes", g.getLastMoneyAudit().residual, 0,
                MoneyAudit.tolerance(g.getLastMoneyAudit().moved()));
        close("...and the landlords' cash-flow statement, handed the loan less its fee and not the premium",
                g.getSectorBooks().get(RE).unexplained(), 0,
                MoneyAudit.tolerance(g.getSectorBooks().get(RE).unexplainedScale()));
    }

    /* ============================ 3. THE DOWN PAYMENT ============================ */

    static void downPayment(Path root) {
        out.println("\n--- 3. the down payment: short of it on one, the landlord holds, and says so ---");

        double one = 300;
        java.util.function.LongToDoubleFunction costOf = n -> one * n;
        double need = Mortgage.ownFundsFor(one);
        Mortgage.Decision shortOne = Mortgage.decide(5, costOf, need - .01, 1_000, .04);
        assertTrue("a till a cent short of the down payment on one buys none", shortOne.quantity() == 0 && shortOne.shortOfDown());
        same("...and says what it needs",
                shortOne.refusal("House"),
                String.format("Holding: needs %s of its own for the %.0f%% down payment on a House",
                        Formats.INSTANCE.cash(need), (1 - Mortgage.MORTGAGE_MAX_LOAN_TO_COST) * 100));
        Mortgage.Decision overdrawn = Mortgage.decide(5, costOf, -50, 1_000, .04);
        assertTrue("a till in overdraft covers nothing", overdrawn.quantity() == 0 && overdrawn.shortOfDown());
        Mortgage.Decision three = Mortgage.decide(5, costOf, Mortgage.ownFundsFor(3 * one), 1_000, .04);
        assertTrue("a till that puts the down payment on three of five orders three",
                three.quantity() == 3 && Mortgage.Decision.DOWN_PAYMENT.equals(three.trimmedBy()));
        same("...and says why it was trimmed", three.trimmed(5),
                String.format(" (trimmed from 5 - its own funds put the %.0f%% down on 3)",
                        (1 - Mortgage.MORTGAGE_MAX_LOAN_TO_COST) * 100));
        Mortgage.Decision outright = Mortgage.decide(5, costOf, 5 * one, -1, .04);
        assertTrue("a till that covers the order buys it outright, with no lender to ask", outright.quantity() == 5);

        out.println("\n--- 3. ...and in a played city, a landlord short of it holds, in those words ---");
        Game g = landlordCity(root, "downpayment");
        BusinessDebtManager lender = g.getEconomyManager().getBusinessDebtManager();
        /*
         * ...WITH ITS SHARES CHEAP ON THE BOOK, so its owners are not asked
         * (0.7.12 round 2). A landlord short of the down payment asks its
         * owners for it first, unless the market has its shares under what
         * they are worth by more than the tolerance (Game, the down payment;
         * Exchange.quoteSupportsIssue()). Until round 2 the dealer's own
         * inventory held its quote there for most of this fixture's months;
         * on the book the price is the last trade, which here sits at the
         * desk's bid, and the owners paid every down payment - the landlord
         * never held. So the fixture causes it: between months, the landlords'
         * book is given a last trade at half their fair value - a market that
         * has their shares cheap - and nothing else moves.
         */
        int re = Equity.indexOf(RE);
        // FS9: THE SEEDING IS THE CAUSE, SO ONE MONTH IS READ, NOT WAITED FOR: with the book's last trade at half what the shares are worth
        // the owners are not asked (Exchange.quoteSupportsIssue()), the founding till is short of the down payment on a House, and the
        // landlords hold in the first month they order - the month played here, once.
        g.getExchange().bookOf(re).seedLastPrice(.5 * g.getExchange().fair(re));
        quietly(() -> g.simulateMonths(1));
        String said = g.getLastInvestment(RE);
        out.printf("   month %d, held for the down payment: %s%n", g.getMonth(), said);
        assertTrue("fixture: the landlords did hold for the down payment", g.getHeldForDownPayment().contains(RE));
        assertTrue("...no mortgage is written in a month it holds for the down payment, month " + g.getMonth(),
                lender.getMortgagesWrittenThisMonth() == 0);
        assertTrue("...and the advisor said what it needed for the down payment",
                said != null && said.startsWith("Holding: needs ")
                        && said.contains(String.format("%.0f%% down payment on a ",
                        (1 - Mortgage.MORTGAGE_MAX_LOAN_TO_COST) * 100)));
    }

    /* ========================== 4. THE LENDER'S TEST ========================== */

    static void lendersTest() {
        out.println("\n--- 4. the lender's test at its line: MORTGAGE_DEBT_COVERAGE passes, a hair under does not ---");

        double one = 300, rate = .05;
        java.util.function.LongToDoubleFunction costOf = n -> one * n;
        double till = Mortgage.ownFundsFor(one);
        double payment = level(Mortgage.loanFor(one - till) * (1 + scheduleRate()), rate,
                Mortgage.MORTGAGE_AMORTIZATION_MONTHS);
        close("the payment the test reads is on the principal with the premium, over the amortization",
                Mortgage.coverage(payment, one - till, rate), 1, 1e-12);
        double line = Mortgage.MORTGAGE_DEBT_COVERAGE * payment;
        Mortgage.Decision at = Mortgage.decide(1, costOf, till, line * (1 + 1e-12), rate);
        Mortgage.Decision under = Mortgage.decide(1, costOf, till, line * (1 - 1e-9), rate);
        assertTrue("a building whose income covers the payment MORTGAGE_DEBT_COVERAGE times is financed", at.quantity() == 1);
        assertTrue("...and one a hair under is declined, not for its down payment",
                under.quantity() == 0 && !under.shortOfDown());
        Mortgage.Decision clear = Mortgage.decide(1, costOf, till, 1.19 * payment, rate);
        same("...in the lender's words", clear.refusal("House"),
                String.format("Declined House - its rent would cover the mortgage 1.19×; the lender asks %.2f×",
                        Mortgage.MORTGAGE_DEBT_COVERAGE));
        Mortgage.Decision five = Mortgage.decide(5, costOf, 5 * till, line * (1 - 1e-6), rate);
        assertTrue("the test is on the order: five that fail it are trimmed until they pass, or dropped",
                five.quantity() < 5);

        // RES3c: a lender reads the rent roll of the size it is shown. Demand for three: past it the order's rent is
        // three units' spread over more (the typed market's HousingMarket.candidateIncome()); the till is the down
        // payment on three.
        java.util.function.LongToDoubleFunction roll = n -> 1.5 * line * Math.min(n, 3) / n;
        assertTrue("fixture: six read at their average fail the line, three at their own pass it",
                roll.applyAsDouble(6) < line && roll.applyAsDouble(3) >= line);
        Mortgage.Decision averaged = Mortgage.decide(6, costOf, Mortgage.ownFundsFor(3 * one), roll.applyAsDouble(6), rate);
        Mortgage.Decision ownRoll = Mortgage.decide(6, costOf, Mortgage.ownFundsFor(3 * one), roll, rate, n -> 1);
        assertTrue("each size the halving tries is read at its own rent: six trimmed to the three the till puts down on",
                ownRoll.quantity() == 3 && Mortgage.Decision.DOWN_PAYMENT.equals(ownRoll.trimmedBy()));
        assertTrue("...where the six-order's average rent read for every size passed fewer", averaged.quantity() < 3);
        Mortgage.Decision flat = Mortgage.decide(5, costOf, 5 * till, n -> line * (1 - 1e-6), rate, n -> 1);
        assertTrue("...and a flat rent read either way decides the same", flat.quantity() == five.quantity()
                && java.util.Objects.equals(flat.trimmedBy(), five.trimmedBy()));
    }

    /* ========================= 5. FIXED FOR THE TERM ========================= */

    static void fixedForTheTerm(Path root) {
        out.println("\n--- 5. fixed for the term: the dial moves nothing until the term ends ---");

        BusinessDebtManager credit = new BusinessDebtManager();
        credit.setInsuredMortgageRate(.04);
        credit.startAuditMonth();
        Mortgage m = credit.issueMortgage(RE, 200_000, 0);
        double a = m.getMonthlyPayment();
        for (int k = 0; k < 60; k++) { credit.processMonth(); credit.takeMaturedPrincipal(RE); }
        credit.setInsuredMortgageRate(.12);
        close("the dial went up eight points and the payment did not move", m.getMonthlyPayment(), a, 1e-9);
        close("...nor the rate it pays", m.getAnnualRate(), .04, 0);
        for (int k = 60; k < Mortgage.MORTGAGE_TERM_MONTHS - 1; k++) { credit.processMonth(); credit.takeMaturedPrincipal(RE); }
        close("...to the last month of the term", m.getMonthlyPayment(), a, 1e-9);
        credit.processMonth();
        credit.takeMaturedPrincipal(RE);
        close("at MORTGAGE_TERM_MONTHS it renews at the day's rate", m.getAnnualRate(), .12, 0);
        assertTrue("...for another term, once", m.getRemainingMonths() == Mortgage.MORTGAGE_TERM_MONTHS
                && m.getRenewals() == 1 && credit.getRenewedThisMonth() == 1);
        assertTrue("...with what is left of the amortization, 360 months",
                m.getAmortizationLeft() == Mortgage.MORTGAGE_AMORTIZATION_MONTHS - Mortgage.MORTGAGE_TERM_MONTHS);
        close("...and the payment recomputed on the balance over those months", m.getMonthlyPayment(),
                level(m.getOutstandingPrincipal(), .12, Mortgage.MORTGAGE_AMORTIZATION_MONTHS - Mortgage.MORTGAGE_TERM_MONTHS), 1e-12);
        assertTrue("...which is dearer", m.getMonthlyPayment() > a);

        out.println("\n--- 5. ...and a renewal the lender cannot write falls due, as a loan would ---");
        BusinessDebtManager shut = new BusinessDebtManager();
        shut.setInsuredMortgageRate(.04);
        shut.startAuditMonth();
        Mortgage n = shut.issueMortgage(RE, 200_000, 0);
        for (int k = 0; k < Mortgage.MORTGAGE_TERM_MONTHS - 1; k++) { shut.processMonth(); shut.takeMaturedPrincipal(RE); }
        double owedAtTheEnd = n.getOutstandingPrincipal();
        shut.setLendingOpen(false);
        shut.processMonth();
        close("a failed bank renews nothing: what it owed falls due with the term's last payment",
                shut.takeMaturedPrincipal(RE), owedAtTheEnd, 1e-9);
        assertTrue("...and the mortgage is gone", shut.getMortgageCount(RE) == 0 && shut.getFallenDueThisMonth() == 1);

        out.println("\n--- 5. ...and the rent floor reads a mortgage's interest, and not its principal ---");
        BusinessDebtManager both = new BusinessDebtManager();
        both.setInsuredMortgageRate(.05);
        both.setPrimeRate(.06);
        both.startAuditMonth();
        Mortgage paying = both.issueMortgage(RE, 150_000, 0);
        BusinessLoan bullet = both.issueLoan(RE, 20_000, 0);
        for (int k = 0; k < 30; k++) { both.processMonth(); both.takeMaturedPrincipal(RE); }
        close("a landlord's interest line is its mortgage's on the balance and its loan's on the face",
                both.getMonthlyInterest(RE), paying.getOutstandingPrincipal() * .05 / 12
                        + bullet.getFaceValue() * bullet.getAnnualRate() / 12, 1e-12);
        assertTrue("...which leaves out the principal each payment repays",
                both.getMonthlyInterest(RE) < paying.getMonthlyPayment() + bullet.getMonthlyInterestExpense() - 1e-9);

        Game g = landlordCity(root, "floor", true);
        BusinessDebtManager lender = g.getEconomyManager().getBusinessDebtManager();
        // FS9: THE MORTGAGE IS WRITTEN BY HAND, not waited for: HOMES_STANDING homes stand for the floor to be over (it is spread over what
        // the landlords own), the landlords are held from ordering, and a mortgage is issued on them between months - so the one month
        // played reads it as the month's interest line and the month's payment, and the floor is read in that month.
        standLandlords(g);
        lender.issueMortgage(RE, 500, g.getMonth());
        quietly(() -> g.simulateMonths(1));
        ham.citybuildersim.sectors.RealEstate re = g.getSectors().realEstate();
        double repaid = lender.getMortgageRepaidThisMonth(RE);
        assertTrue("fixture: the landlords owe a mortgage and are paying it down",
                lender.getMortgageCount(RE) > 0 && repaid > 0 && re.getOwnedHousingCapacity() > 0);
        double carry = re.getMaintenanceExpense() + re.getPropertyTaxExpense() + re.getInterestExpense();
        close("the break-even is maintenance, property tax and the interest line over what it owns",
                re.rentBreakEven() * re.getOwnedHousingCapacity(), carry, 1e-9);
        assertTrue("...and the principal it repaid this month is not in it",
                Math.abs(re.rentBreakEven() * re.getOwnedHousingCapacity() - (carry + repaid)) > .5 * repaid);
    }

    /* ============================== 6. INSURANCE ============================== */

    static void insurance(Path root) {
        out.println("\n--- 6. insurance: a landlord's slice - its debt falls, the city pays the bank the insured part ---");

        Game g = landlordCity(root, "insurance", true);
        EconomyManager econ = g.getEconomyManager();
        BusinessDebtManager lender = econ.getBusinessDebtManager();
        /*
         * FS9: THE LANDLORDS' DEBT IS WRITTEN BY HAND, not waited for. HOMES_STANDING homes stand for the landlords to own, they are held
         * from ordering (nothing new is written while the write-downs are measured: the debt after the month is then what the write-down
         * left), their sheet is valued as the month will read it, and between months an insured mortgage is issued on half of what they
         * own, then a claim that takes the debt to 1.2 times it (BondCheck's 5b fixture, the same way): a claim, no money moved. Past
         * what it owns and under the default point, a month's share of its debt falls through the default point (monthlyDefaultShare of
         * any positive leverage) - the slice, with the insured mortgage among what it takes.
         */
        standLandlords(g);
        econ.pushBalanceSheetInputs();
        econ.refreshCreditAssets();
        Mortgage insuredByHand = lender.issueMortgage(RE, .5 * lender.getAssets(RE), g.getMonth());
        double byHand = insuredByHand.getOutstandingPrincipal();
        assertTrue("fixture: the landlords owe an insured mortgage, written by hand", lender.getInsuredPrincipal(RE) > 0);
        double claim = Math.max(0, 1.2 * lender.getAssets(RE) - lender.getPrincipal(RE));
        lender.issueLoan(RE, claim, g.getMonth());
        int recordBefore = lender.getRestructureCount(RE);
        double claimsBefore = lender.getInsuredWrittenOffTotal(RE);
        quietly(() -> g.simulateMonths(1));
        double written = lender.getWrittenOffThisMonth(RE), insured = lender.getInsuredWrittenOffThisMonth(RE);
        // What it owes the BANK: since 0.7.12 its shortfall desk can issue a
        // bond, which loses a bond's share of a slice, not a loan's
        // (BusinessDebtManager, RECOVERIES BY INSTRUMENT, round 2), and so
        // does not fall with the loans pro rata.
        // ...nor does interim financing, which ranks ahead of all of it and a
        // slice leaves whole (0.7.12, round 5, INTERIM FINANCING).
        double owed = lender.getLoanPrincipal(RE) - lender.getInterimPrincipal(RE), owedInsured = lender.getInsuredPrincipal(RE);
        Bank bank = g.getBank();
        NationalAccounts na = g.getEconomyManager().getNationalAccounts();
        out.printf("   written off $%,.2fk, of it $%,.2fk off insured mortgages; the bank took $%,.2fk%n",
                written, insured, bank.getWrittenOff(RE));
        assertTrue("fixture: its firms defaulted a slice, insured mortgages among them", written > 0 && insured > 0
                && !lender.wasRestructuredThisMonth(RE));
        out.printf("   of what it owes the bank, $%,.2fk is interim financing, ranked first%n", lender.getInterimPrincipal(RE));
        close("every bank loan fell pro rata: the insured share of the write-off is their share of the loans a slice takes",
                insured / written, owedInsured / owed, 1e-9);
        // A mortgage is a first-lien loan (0.7.12, round 2): the claim is the
        // slice's share of the insured balance at a loan's loss given default.
        double hRE = lender.getDefaultShareThisMonth(RE), lossRE = 1 - BusinessDebtManager.LOAN_RECOVERY;
        close("the insurer's claim is h x the insured balance x (1 - LOAN_RECOVERY), a first-lien loan's loss",
                insured, owedInsured / (1 - hRE * lossRE) * hRE * lossRE, 1e-9);
        /*
         * ...WITH ITS LOSS ON THE SECTOR'S BONDS BESIDE THE LOANS' (XR4): a bond is no mortgage and nobody insured it, so the slice's share of
         * the bonds the bank holds (at a bond's own recovery, and at what the bank paid) is booked with the loans' uninsured part
         * (Game: restructured.getWrittenOffThisMonth(s) - insured + bondMarket.takeBankLoss(s)). Under the Fed's rate as the world's price of
         * money (XR4) the bank holds a little of the landlords' bonds in this city; under the old flat 2% it held none (measured with the world
         * quieted, as BondCheck's town is), and the identity read the loans alone. It reads the bond loss from the market now, so what is insured is
         * still exactly what the bank did not book, bonds or no bonds.
         */
        BondMarket bonds = g.getBondMarket();
        close("the bank books only what nobody insured as its loss, its bonds' beside the loans'",
                bank.getWrittenOff(RE), written - insured + bonds.getBankLossThisMonth(RE), 1e-9);
        double uninsuredAll = 0, claimsAll = 0;
        for (String s : lender.sectors()) {
            uninsuredAll += lender.getWrittenOffThisMonth(s) - lender.getInsuredWrittenOffThisMonth(s) + bonds.getBankLossThisMonth(s);
            claimsAll += lender.getInsuredWrittenOffThisMonth(s);
        }
        close("...so its month's write-offs carry none of the insured part", bank.getWriteOffs(), uninsuredAll, 1e-9);
        close("the treasury paid the bank exactly the written-down insured balance", bank.getInsuranceClaims(), claimsAll, 1e-12);
        close("...on its claims line", na.getMortgageClaims(), claimsAll, 1e-12);
        close("the bank's equity moved by its income, its named causes and the fixture's own loans, and nothing else",
                bank.equityMovement().residual(), claim + byHand,
                MoneyAudit.tolerance(1e-6, bank.equityMovement().scale()));
        close("the month's money audit closes", g.getLastMoneyAudit().residual, 0,
                MoneyAudit.tolerance(g.getLastMoneyAudit().moved()));
        assertTrue("a slice is not on the landlord's record", lender.getRestructureCount(RE) == recordBefore);

        out.println("\n--- 6. ...and the backstop: a landlord with nothing left is written down whole ---");
        double ownsBesidesItsTill = lender.getAssets(RE) - lender.getCash(RE);
        g.getEconomyManager().setSectorCash(RE, -Math.max(0, ownsBesidesItsTill) - 100_000);
        double insuredBefore = lender.getInsuredPrincipal(RE);
        quietly(() -> g.simulateMonths(1));
        double wholeWritten = lender.getWrittenOffThisMonth(RE), wholeInsured = lender.getInsuredWrittenOffThisMonth(RE);
        out.printf("   written off $%,.2fk whole, $%,.2fk of it insured (it owed $%,.2fk on its mortgages)%n",
                wholeWritten, wholeInsured, insuredBefore);
        assertTrue("fixture: the landlord went under whole", lender.wasRestructuredThisMonth(RE));
        close("its debt is gone", lender.getPrincipal(RE), 0, 1e-9);
        close("...every insured balance claimed - what was left after the month's payments",
                wholeInsured, insuredBefore - lender.getMortgageRepaidThisMonth(RE), 1e-9);
        close("the bank booked what nobody insured, its bonds' loss with the loans'", g.getBank().getWrittenOff(RE),
                wholeWritten - wholeInsured + g.getBondMarket().getBankLossThisMonth(RE), 1e-9);
        close("the treasury paid the bank the insured balance, on its claims line",
                g.getEconomyManager().getNationalAccounts().getMortgageClaims(), lender.getInsuredWrittenOffThisMonth(), 1e-12);
        close("...cash the bank received", g.getBank().getInsuranceClaims(), lender.getInsuredWrittenOffThisMonth(), 1e-12);
        assertTrue("the default is still the landlord's: on its record, and banned",
                lender.getRestructureCount(RE) == recordBefore + 1 && lender.isBorrowingBlocked(RE));
        close("the bank's equity moved by its income and its named causes", g.getBank().equityMovement().residual(), 0,
                MoneyAudit.tolerance(1e-6, g.getBank().equityMovement().scale()));
        close("the month's money audit closes", g.getLastMoneyAudit().residual, 0,
                MoneyAudit.tolerance(g.getLastMoneyAudit().moved()));
        close("over the city's life the claims grew by what the two write-downs took off insured mortgages",
                lender.getInsuredWrittenOffTotal(RE) - claimsBefore, insured + wholeInsured, 1e-9);
    }

    /* ============================== 7. THE BANK ============================== */

    static void theBank(Path root) {
        out.println("\n--- 7. the bank: an insured mortgage weighs nothing and carries no allowance ---");

        Bank weighed = new Bank();
        weighed.injectCapital(100_000);
        weighed.refresh(2, 5_000_000, 0, 300_000, 200_000, 50_000);
        weighed.setWeightedBook(200_000 * Bank.RISK_BUSINESS * .7 + 100_000 * Bank.RISK_INSURED_MORTGAGE,
                200_000 * Bank.RISK_CITY * .5, 50_000 * Bank.RISK_HOUSEHOLD);
        weighed.setMortgageBook(100_000, 100_000 * Bank.RISK_INSURED_MORTGAGE);
        double footed = 0, mortgageWeighs = -1, mortgageFace = -1, mortgageRisk = -1, businessFace = -1;
        for (Bank.WeightRow row : weighed.weightTable()) {
            footed += row.weighted();
            if (row.book() == Bank.Book.MORTGAGES) { mortgageWeighs = row.weighted(); mortgageFace = row.face(); mortgageRisk = row.risk(); }
            if (row.book() == Bank.Book.BUSINESSES) businessFace = row.face();
        }
        close("the weight table has the insured mortgages as their own row, at face", mortgageFace, 100_000, 0);
        close("...at RISK_INSURED_MORTGAGE", mortgageRisk, Bank.RISK_INSURED_MORTGAGE, 0);
        close("...which is the sovereign's nothing (Basel III, CRE20)", Bank.RISK_INSURED_MORTGAGE, 0, 0);
        close("...so they weigh nothing", mortgageWeighs, 0, 0);
        close("...the businesses' row is the rest of what they owe", businessFace, 200_000, 0);
        close("...and the table still foots to the weighted book", footed, weighed.getWeightedBook(), 1e-9);

        double owedInsured = 80_000, assets = 70_000;
        close("an all-insured book holds no allowance while sound",
                Bank.sectorAllowance(0, owedInsured, 200_000), 0, 0);
        close("...nor in stage 2, past the watch line", Bank.sectorAllowance(0, owedInsured, assets), 0, 0);
        assertTrue("fixture: that borrower really is in stage 2", Bank.sectorWatched(owedInsured, assets));
        close("...and a mixed book holds the uninsured part's loss, at the leverage of the whole",
                Bank.sectorAllowance(20_000, owedInsured, assets),
                Bank.sectorAllowance(owedInsured, owedInsured, assets) * 20_000 / owedInsured, 1e-9);

        /*
         * ROUND 2 CHANGED THIS RULE (Jerus: "Basel leverage ratio" - the bank
         * "prices that capital into the mortgage rate"). Until then the charge
         * at RISK_INSURED_MORTGAGE was nothing, and these assertions read "no
         * capital". Now a dollar of it ties up the leverage requirement,
         * Bank.leverageTarget(), at the owners' return over the money's cost -
         * prime's own capital charge with the weight times the target floored
         * at the leverage requirement per dollar (Bank.capitalPerDollar()).
         */
        out.println("\n--- 7. ...priced with no loss and with the capital its leverage requirement ties up, the ladder's rung ---");
        Game g = landlordCity(root, "bank");
        quietly(() -> g.simulateMonths(3));
        Bank bank = g.getBank();
        BusinessDebtManager lender = g.getEconomyManager().getBusinessDebtManager();
        close("a dollar of it ties up the leverage minimum scaled by the bank's own buffer",
                bank.capitalPerDollar(Bank.RISK_INSURED_MORTGAGE),
                Bank.LEVERAGE_RATIO_MIN * bank.capitalTarget() / Bank.CAPITAL_RATIO, 1e-15);
        close("...and a business loan what it always did: the target on its whole weight",
                bank.capitalPerDollar(Bank.RISK_BUSINESS), Bank.RISK_BUSINESS * bank.capitalTarget(), 0);
        for (double dial : new double[] { 0, .03, .10, .25 }) {
            double rate = bank.insuredMortgageRate(dial);
            double capital = bank.leverageTarget()
                    * Math.max(0, Bank.requiredReturn() - bank.fundsTransferPrice(dial, Mortgage.MORTGAGE_TERM_MONTHS));
            close(String.format("at %.0f%% the capital part is the leverage target at the owners' return over the money", dial * 100),
                    bank.capitalCharge(dial, Mortgage.MORTGAGE_TERM_MONTHS, Bank.RISK_INSURED_MORTGAGE), capital, 1e-15);
            close("...and the rate is the ten-year funds-transfer price, running the bank and that",
                    rate, bank.fundsTransferPrice(dial, Mortgage.MORTGAGE_TERM_MONTHS) + bank.runningCostRate() + capital, 1e-15);
            Bank.Ladder l = bank.ladder(dial);
            close("...the ladder's rung is it", l.mortgage(), rate, 1e-15);
            close("...its money is the policy rate, the window's share and the ten-year term premium",
                    l.mortgageTransferOverPolicy(), CentralBank.WINDOW_PENALTY * bank.windowShare()
                            + DebtManager.termPremium(Mortgage.MORTGAGE_TERM_MONTHS), 1e-12);
            close("...the capital its own part of the rung", l.mortgageCapital(), capital, 1e-15);
            close("...and over its money the running costs and the capital, no loss", l.mortgageOverTransfer(),
                    l.running() + l.mortgageCapital(), 1e-12);
        }
        assertTrue("fixture: at the dials under the owners' return the part is a few tenths of a point",
                bank.capitalCharge(.03, Mortgage.MORTGAGE_TERM_MONTHS, Bank.RISK_INSURED_MORTGAGE) > .001
                        && bank.capitalCharge(.03, Mortgage.MORTGAGE_TERM_MONTHS, Bank.RISK_INSURED_MORTGAGE) < .01);
        close("...and past it nothing: the owners' money would be the cheaper money",
                bank.capitalCharge(.25, Mortgage.MORTGAGE_TERM_MONTHS, Bank.RISK_INSURED_MORTGAGE), 0, 0);
        close("the played bank carries the insured mortgages as its mortgage book",
                bank.getMortgageBook(), lender.getInsuredPrincipal(), 1e-9);
        double played = 0;
        for (Bank.WeightRow row : bank.weightTable()) played += row.weighted();
        close("...and its weight table foots", played, bank.getWeightedBook(), 1e-9);
        double q = lender.quarterPrincipal(RE), qa = lender.quarterAssets(RE);
        close("the landlords' allowance is struck on what nobody insures, at the curve of all they owe"
                        + " and each instrument's own loss given default",
                bank.getSectorAllowance(RE),
                Bank.sectorAllowance(lender.getUninsuredPrincipal(RE), q, qa, 1 - BusinessDebtManager.LOAN_RECOVERY)
                        + Bank.sectorAllowance(g.getBondMarket().bankCost(RE), q, qa,
                                1 - BusinessDebtManager.BOND_RECOVERY), 1e-9);

        out.println("\n--- 7. ...and the capital rule rations it only when the leverage ratio binds ---");
        BusinessDebtManager rationed = new BusinessDebtManager();
        rationed.setInsuredMortgageRate(.04);
        rationed.setAssets(RE, 10_000_000);
        rationed.issueLoan(RE, 1_000_000, 0);
        rationed.setCapitalRule(0, true);
        rationed.startAuditMonth();
        assertTrue("fixture: a bank under its minimum refuses a project loan", !rationed.canFundProject(RE, 100_000));
        assertTrue("...and writes the insured mortgage", rationed.canFundMortgage(RE, 100_000, 1_000_000));
        double roomBefore = rationed.capitalRoom(RE);
        rationed.issueMortgage(RE, 100_000, 1);
        close("...which leaves the room its uninsured borrowing has where it was", rationed.capitalRoom(RE), roomBefore, 1e-6);
        // ...but when the leverage requirement is the larger (round 2), a
        // mortgage uses the capital the bank is short of like any loan.
        rationed.setCapitalRule(0, true, true);
        assertTrue("a bank under its leverage minimum refuses the insured mortgage too",
                !rationed.canFundMortgage(RE, 100_000, 1_000_000));
        same("...for its capital", rationed.getMortgageRefusal(), BusinessDebtManager.MORTGAGE_CAPITAL);
        rationed.setCapitalRule(.01, false, true);
        close("...and short of its target it lets the whole debt grow at the rule's rate, the mortgages counted",
                rationed.capitalRoom(RE), rationed.getPrincipal(RE) * .01, 1e-9);
        rationed.setCapitalRule(.01, false, false);
        close("...where on the risk weights it counts only what nobody insures",
                rationed.capitalRoom(RE), rationed.getUninsuredPrincipal(RE) * .01, 1e-9);
        BusinessDebtManager leveraged = new BusinessDebtManager();
        leveraged.setInsuredMortgageRate(.04);
        leveraged.setPrimeRate(.05);
        leveraged.setAssets(RE, 1_000_000);
        leveraged.issueLoan(RE, 1_300_000, 0);
        leveraged.updateRates();
        assertTrue("fixture: that borrower's own risk costs it points over prime", leveraged.getRate(RE) > .05 + .05);
        close("...and its insured mortgage is written at the insured rate all the same",
                leveraged.issueMortgage(RE, 100_000, 1).getAnnualRate(), .04, 0);
    }

    /* ============================ 8. SAVE AND LOAD ============================ */

    static void saveAndLoad(Path root) throws Exception {
        out.println("\n--- 8. save and load in the middle of a term: every field round-trips ---");

        Game g = landlordCity(root, "save", true);
        BusinessDebtManager lender = g.getEconomyManager().getBusinessDebtManager();
        /*
         * FS9: THE MORTGAGES ARE WRITTEN BY HAND, and only the months a term needs to be mid-way are played. HOMES_STANDING homes stand
         * for the landlords to owe against and they are held from ordering (nothing else is written); between months, a mortgage and a
         * business loan are issued on them, one more mortgage two months on, so the two stand at different points of their terms when
         * they are saved - the first three months into its term, the second two.
         */
        standLandlords(g);
        EconomyManager econ = g.getEconomyManager();
        econ.pushBalanceSheetInputs();
        econ.refreshCreditAssets();
        double owns = lender.getAssets(RE);
        lender.issueMortgage(RE, .2 * owns, g.getMonth());
        BusinessLoan byHandLoan = lender.issueLoan(RE, .1 * owns, g.getMonth());
        quietly(() -> g.simulateMonths(1));
        lender.issueMortgage(RE, .2 * owns, g.getMonth());
        quietly(() -> g.simulateMonths(2));
        List<Mortgage> before = lender.getMortgages(RE);
        assertTrue("fixture: the landlords owe two mortgages, written by hand, each mid-term",
                before.size() == 2 && before.get(0).getRemainingMonths() < before.get(1).getRemainingMonths()
                        && before.get(1).getRemainingMonths() < Mortgage.MORTGAGE_TERM_MONTHS
                        && before.get(0).getRemainingMonths() > 0);
        // The business loans beside them, as they stood when saved.
        java.util.List<String> bulletsSaved = new java.util.ArrayList<>();
        for (BusinessDebt loan : lender.getLoans()) {
            if (!(loan instanceof Mortgage)) bulletsSaved.add(describe(loan));
        }
        boolean loanAmongThem = bulletsSaved.contains(describe(byHandLoan));
        final boolean[] saved = { false };
        quietly(() -> saved[0] = g.saveGame(1, "mortgage city").ok);
        assertTrue("saved", saved[0]);
        GameFiles files = new GameFiles(root.resolve("save"), root.resolve("save-no-legacy"));
        Game back = new Game(files);
        quietly(() -> back.loadGameSave(1));
        back.getBusinessInvestment().holdSector(RE);   // ...held as the city it was saved from is: a hold is a harness's, and not saved
        BusinessDebtManager again = back.getEconomyManager().getBusinessDebtManager();
        List<Mortgage> after = again.getMortgages(RE);
        boolean fields = after.size() == before.size();
        for (int i = 0; fields && i < before.size(); i++) {
            Mortgage a = before.get(i), b = after.get(i);
            fields = a.getLoan() == b.getLoan() && a.getPremium() == b.getPremium()
                    && a.getFaceValue() == b.getFaceValue()
                    && a.getOutstandingPrincipal() == b.getOutstandingPrincipal()
                    && a.getAnnualRate() == b.getAnnualRate() && a.getRemainingMonths() == b.getRemainingMonths()
                    && a.getAmortizationLeft() == b.getAmortizationLeft()
                    && a.getAmortizationMonths() == b.getAmortizationMonths()
                    && a.getTermMonths() == b.getTermMonths() && a.getRenewals() == b.getRenewals()
                    && a.isInsured() == b.isInsured() && a.getMonthStarted() == b.getMonthStarted()
                    && a.getSector().equals(b.getSector()) && "MORTGAGE".equals(b.getType());
        }
        assertTrue("every mortgage, every field, to the bit", fields);
        close("...the principal the month's payments took", again.getMortgageRepaidThisMonth(RE),
                lender.getMortgageRepaidThisMonth(RE), 0);
        close("...the premiums over the city's life", again.getPremiumsTotal(), lender.getPremiumsTotal(), 0);
        close("...the claims over it", again.getInsuredWrittenOffTotal(), lender.getInsuredWrittenOffTotal(), 0);
        close("...the budget's premium line", back.getEconomyManager().getNationalAccounts().getMortgagePremiums(),
                g.getEconomyManager().getNationalAccounts().getMortgagePremiums(), 0);
        close("...and its claims line", back.getEconomyManager().getNationalAccounts().getMortgageClaims(),
                g.getEconomyManager().getNationalAccounts().getMortgageClaims(), 0);
        close("the next payment is the same payment", again.getMortgagePayment(RE), lender.getMortgagePayment(RE), 0);
        close("the insured rate is struck again on load from the bank's saved prices, as prime is",
                again.getInsuredMortgageRate(), g.getBank().insuredMortgageRate(g.getDebtManager().getPolicyRate()), 1e-12);
        quietly(() -> { g.simulateMonths(1); back.simulateMonths(1); });
        close("...and a month on, the two cities write at the same rate", again.getInsuredMortgageRate(),
                lender.getInsuredMortgageRate(), 1e-12);
        close("...and owe the same on their mortgages", again.getMortgagePrincipal(RE), lender.getMortgagePrincipal(RE), 1e-12);

        out.println("\n--- 8. ...and a save from before mortgages loads its loans unchanged ---");
        Path file = files.saveFile(1);
        JsonObject json = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
        JsonArray kept = new JsonArray();
        int loans = 0;
        for (JsonElement e : json.getAsJsonArray("businessDebts")) {
            if ("MORTGAGE".equals(e.getAsJsonObject().get("type").getAsString())) continue;
            kept.add(e);
            loans++;
        }
        json.add("businessDebts", kept);
        json.remove("mortgageRepaid");
        json.remove("insuranceClaims");
        json.remove("insurancePremiums");
        JsonArray block = json.getAsJsonArray("governmentMonth");
        if (block != null && block.size() == 27) {
            JsonArray older = new JsonArray();
            for (int i = 0; i < 25; i++) older.add(block.get(i));
            json.add("governmentMonth", older);
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.writeString(files.saveFile(2), gson.toJson(json), StandardCharsets.UTF_8);
        Game old = new Game(files);
        quietly(() -> old.loadGameSave(2));
        BusinessDebtManager oldLender = old.getEconomyManager().getBusinessDebtManager();
        java.util.List<String> bulletsLoaded = new java.util.ArrayList<>();
        for (BusinessDebt loan : oldLender.getLoans()) bulletsLoaded.add(describe(loan));
        out.printf("   the older save carries %d loan(s) and no mortgage%n", loans);
        assertTrue("fixture: the city owes business loans beside its mortgages, the one written by hand among them", loanAmongThem);
        assertTrue("its loans load as they were, field by field", bulletsLoaded.equals(bulletsSaved));
        assertTrue("...with no mortgage and an empty insurance book",
                oldLender.getMortgages().isEmpty() && oldLender.getPremiumsTotal() == 0
                        && oldLender.getInsuredWrittenOffTotal() == 0);
    }

    /** A loan's fields, to the bit, as one string. */
    static String describe(BusinessDebt loan) {
        return loan.getClass().getSimpleName() + "|" + loan.getSector() + "|" + loan.getType()
                + "|" + Double.doubleToLongBits(loan.getFaceValue())
                + "|" + Double.doubleToLongBits(loan.getOutstandingPrincipal())
                + "|" + Double.doubleToLongBits(loan.getAnnualRate())
                + "|" + loan.getRemainingMonths() + "|" + loan.getMaturityMonth();
    }

    /* ======================= 9. WHERE THE OLD RULE SAID NO ======================= */

    static void whereTheOldRuleSaidNo(Path root) {
        out.println("\n--- 9. where the old rule said no: a new landlord's own risk refused a House the lender's test passes ---");

        // FC1: three depots stand beside the founding yard (as payoutsAfterPrincipal()'s town has them), so the House is priced
        // with the builders under their knee (BuildersShort.KNEE_MONTHS): the ordered works' founding queue at one yard's
        // pace is hundreds of months, a premium near the ceiling, and a House at 1.5 times what this section was written against
        // is a question the lender's test answers otherwise (0.87x against 1.29x) - not the one asked here.
        Game g = landlordCity(root, "oldrule", true, 3);
        // Nothing ordered yet: the city is short of doors from its first month
        // and its landlords are about to order their first.
        assertTrue("fixture: the landlords have nothing on site and want to build",
                g.getBuildingManager().getUnderConstructionBySector(RE) == 0);
        BusinessDebtManager lender = g.getEconomyManager().getBusinessDebtManager();
        EconomyManager econ = g.getEconomyManager();
        BuildingsTemplate house = template(g, "House");
        /*
         * ...AT THE PRICE THIS SECTION WAS WRITTEN AGAINST, RE-CAUSED (0.7.19).
         * The builders' sales tax is in their quote since 0.7.19 (Game, THE
         * BUILDERS' PRICE), and a landlord, whose rent is an exempt supply,
         * cannot claim it back as a credit - nor, on a House, by a rebate:
         * the purpose-built rental rebate needs four homes, and the new-home
         * rebate is gone at a House's founding value (revised 0.7.19;
         * EconomyManager, THE REBATES ON A NEW HOME, AND THE CITY'S): the
         * House went from $446k to $524.7k, and at the founding city's rent
         * the lender's test read 1.06x against its 1.20 - a true answer about
         * a question this section is not asking.
         * It asks whether the old rule refused a House the lender's test
         * passes, so the builders' tax is dialled to nothing in this city (a
         * lever the player holds, TaxPolicy's sector offset) and the House
         * costs what it did.
         */
        // FS9: SET, NOT FOUND - the policy is put back to its defaults and the builders' offset set against their sales tax, so the quote
        // is the House's without it whatever the founding town taxed.
        econ.getTaxPolicy().reset();
        econ.getTaxPolicy().setSalesOffset(g.getSectors().construction(), -TaxPolicy.DEFAULT_INCOME_TAX);
        assertTrue("fixture: the builders charge no sales tax in this city",
                econ.buildersSalesRate() == 0);
        /*
         * ITS OWN RISK, WHICH A NEW LANDLORD CANNOT AVOID. Owning nothing yet
         * and borrowing all of its first building, it is left at exactly 1.0
         * times its assets - the building is all it owns and all it owes -
         * and the old rule priced the loan at the curve there
         * (projectRate(): prime and its expected loss over the book's). The
         * insured rate does not read the borrower at all. Priced as the
         * month will price it, on the balance sheets as they stand.
         * FS9: THEIR HOLDINGS ARE SET, not found: the till is put at nothing (the founding town gives them none), they have no building
         * standing or on site, and they owe nothing.
         */
        econ.setSectorCash(RE, 0);
        econ.pushBalanceSheetInputs();
        econ.refreshCreditAssets();
        assertTrue("fixture: the landlords own nothing and owe nothing yet",
                lender.getAssets(RE) == 0 && lender.getPrincipal(RE) == 0);
        double dial = g.getDebtManager().getPolicyRate();
        econ.updateBusinessCredit(g.getBank().prime(dial), g.getBank().insuredMortgageRate(dial));
        BusinessInvestment plans = g.getBusinessInvestment();
        double cost = plans.getCostOf(house, 1);
        double rent = plans.estimatedMonthlyProfit(RE, house);
        double oldRate = lender.projectRate(RE, cost);
        boolean oldSays = plans.servicesItsOwnDebt(rent, cost, oldRate);
        double till = Mortgage.ownFundsFor(cost);
        double noi = rent - econ.housingCarry(house);
        double insuredRate = lender.getInsuredMortgageRate();
        double coverage = Mortgage.coverage(noi, cost - till, insuredRate);
        Mortgage.Decision newSays = Mortgage.decide(1, n -> plans.getCostOf(house, n), till, noi, insuredRate);
        out.printf("   a House: $%,.2fk, lets for $%,.4fk a month, holds for $%,.4fk%n", cost, rent, econ.housingCarry(house));
        out.printf("   the old test: %.4f of rent against %.2f x %.4f of interest on all of it at %.2f%% - %s%n",
                rent, BusinessInvestment.PROFIT_OVER_INTEREST, cost * oldRate / 12, oldRate * 100, oldSays ? "passes" : "refused");
        out.printf("   the lender's: %.4f of income over a payment of %.4f at %.2f%% on %.0f%% of it - %.2fx%n",
                noi, Mortgage.payment(Mortgage.loanFor(cost - till) * (1 + Mortgage.premiumRate()), insuredRate,
                        Mortgage.MORTGAGE_AMORTIZATION_MONTHS), insuredRate * 100,
                Mortgage.MORTGAGE_MAX_LOAN_TO_COST * 100, coverage);
        /*
         * AT THE LOSS 0.7.11 READ (0.7.12, round 2). This section is why the
         * landlords were moved onto insured mortgages, and it was measured
         * when every defaulted dollar lost 1 - RESTRUCTURE_TARGET /
         * INSOLVENCY_TRIGGER, 60%. A loan recovers LOAN_RECOVERY now
         * (BusinessDebtManager, RECOVERIES BY INSTRUMENT), its own risk at
         * 1.0 is under a point, and the old rule would pass this House: the
         * refusal is asserted at the loss it was written against, and what
         * the rule says today is printed beside it.
         */
        double lossThen = 1 - BusinessDebtManager.RESTRUCTURE_TARGET / BusinessDebtManager.INSOLVENCY_TRIGGER;
        double rateThen = lender.getPrimeRate() + BusinessDebtManager.expectedLossSpread(1.0, lossThen)
                + (oldRate - lender.getPrimeRate() - BusinessDebtManager.expectedLossSpread(1.0));
        boolean thenSays = plans.servicesItsOwnDebt(rent, cost, rateThen);
        out.printf("   at 0.7.11's 60%% loss the old rate was %.2f%% - %s; at a loan's %.0f%% since round 2 it is %.2f%% - %s%n",
                rateThen * 100, thenSays ? "passes" : "refused", (1 - BusinessDebtManager.LOAN_RECOVERY) * 100,
                oldRate * 100, oldSays ? "passes" : "refused");
        assertTrue("the old rule refused it at 0.7.11's loss: gross rent under 1.25 times the interest on its whole cost at its own risk",
                !thenSays && rent < BusinessInvestment.PROFIT_OVER_INTEREST * cost * rateThen / 12);
        close("...borrowing all of it leaves the landlord at 1.0 times what it owns",
                lender.leverageAfterProject(RE, cost), 1, 1e-12);
        close("...where the curve charges its own expected loss over prime, at a loan's loss today", oldRate,
                lender.getPrimeRate() + BusinessDebtManager.expectedLossSpread(1.0), 1e-12);
        assertTrue("...which was points of it at 0.7.11's loss", rateThen - lender.getPrimeRate() > .01);
        assertTrue("the lender's test passes: its income covers the payment on 85% MORTGAGE_DEBT_COVERAGE times",
                coverage >= Mortgage.MORTGAGE_DEBT_COVERAGE);
        assertTrue("...so the landlord builds it", newSays.quantity() == 1);

        // ...and in the city, the month it orders: the one month the fixture above causes, played once (FS9: no waiting on the advisor).
        int writtenBefore = lender.getMortgages(RE).size();
        quietly(() -> g.simulateMonths(1));
        String said = g.getLastInvestment(RE);
        out.println("   the landlords' advisor: " + said);
        assertTrue("...and in a played month it does, on an insured mortgage",
                said != null && said.startsWith("Built") && said.contains("on an insured mortgage")
                        && lender.getMortgages(RE).size() > writtenBefore);
    }

    /* ======================== 10. THE LEVERAGE RATIO (round 2) ======================== */

    /**
     * A bank by hand whose business book is $1M, $900k of it insured
     * mortgages, and whose equity is `equity`: net borrowed, so the book is
     * everything on its sheet and the exposure is the book.
     */
    static Bank mortgageBank(double equity) {
        Bank b = new Bank();
        b.refresh(2, 5_000_000, 0, 1_000_000, 0, 0);
        b.setWeightedBook(100_000 * Bank.RISK_BUSINESS, 0, 0);
        b.setMortgageBook(900_000, 900_000 * Bank.RISK_INSURED_MORTGAGE);
        b.setBranchesCapitalised(2);
        b.setCash(equity - 1_000_000);
        return b;
    }

    static void leverageRatio() {
        out.println("\n--- 10. the leverage ratio: a book of insured mortgages holds capital against its face ---");

        Bank rebuilding = mortgageBank(35_000);
        close("fixture: the bank's equity is what the fixture set", rebuilding.equity(), 35_000, 1e-9);
        close("...its exposure is everything it has lent, the mortgages at face", rebuilding.exposure(), 1_000_000, 1e-9);
        assertTrue("fixture: on its risk-weighted book it is far past its target",
                rebuilding.capitalRatio() > 3 * rebuilding.capitalTarget());
        assertTrue("...but the leverage requirement is the larger", rebuilding.leverageBinds());
        close("so its minimum is LEVERAGE_RATIO_MIN of the exposure", rebuilding.minimumEquity(),
                Bank.LEVERAGE_RATIO_MIN * rebuilding.exposure(), 1e-12);
        close("...its leverage target LEVERAGE_RATIO_MIN scaled by the buffer it chose on the risk side",
                rebuilding.leverageTarget(), Bank.LEVERAGE_RATIO_MIN * rebuilding.capitalTarget() / Bank.CAPITAL_RATIO, 1e-15);
        close("...and its target equity that on the exposure", rebuilding.targetEquity(),
                rebuilding.leverageTarget() * rebuilding.exposure(), 1e-12);
        assertTrue("fixture: 3.5% of what it has lent is between the minimum and that target",
                rebuilding.leverageRatio() > Bank.LEVERAGE_RATIO_MIN && rebuilding.leverageRatio() < rebuilding.leverageTarget());
        assertTrue("so it is rebuilding, whatever its risk-weighted ratio says",
                rebuilding.payoutStance() == Bank.Payout.REBUILDING);
        close("...pays its owners nothing", rebuilding.dividendDue(10_000), 0, 0);
        assertTrue("...buys none of its shares back, and issues them",
                !rebuilding.buysBackOwnShares() && rebuilding.buybackRoom(0) == 0 && rebuilding.issuesOwnShares());
        close("...lets the desk carry nothing new", rebuilding.deskCanCarry(0, 1, 1), 0, 0);
        double x = (rebuilding.leverageRatio() - Bank.LEVERAGE_RATIO_MIN)
                / (rebuilding.leverageTarget() - Bank.LEVERAGE_RATIO_MIN);
        close("...and lets a borrower's debt grow by the rule, on the leverage ratio", rebuilding.lendingGrowthLimit(),
                Bank.RATIONED_GROWTH * x / (1 - x), 1e-12);
        close("...nothing asked of the city while it is over the minimum", rebuilding.recapitalisationNeeded(), 0, 0);

        Bank under = mortgageBank(25_000);
        // A month that kept its staff, so a branch would pay for itself.
        // RE-CAUSED FOR 0.7.19: a branch answers to its customers' fees since
        // then (Bank, THE BRANCHES, BY THEIR CUSTOMERS), so the month also
        // has the customers for another branch and the fees to pay for it,
        // where it had a book spilling past its capital and the strain it
        // built at. The assertion below is unchanged: a bank under its
        // minimum opens no branch for the capital it would bring.
        under.startMonth();
        under.takeInterest(50_000);
        under.payRunning(1_000, 100);
        under.closeMonth();
        under.setCustomers((under.getBranches() + 2) * Bank.CUSTOMERS_PER_BRANCH);
        under.takeAccountFees(10 * (under.getBranches() + 1) * under.runningCostPerBranch());
        under.setCash(25_000 - 1_000_000);
        assertTrue("fixture: at 2.5% of what it has lent it is under its minimum",
                under.payoutStance() == Bank.Payout.UNDER_MINIMUM && under.lendsOnlyToKeepBorrowersGoing());
        close("...and the city is asked for what takes it back to its target", under.recapitalisationNeeded(),
                under.targetEquity() - under.equity(), 1e-9);
        assertTrue("fixture: every other part of the branch test says open one - there are customers for "
                        + "another, and their fees would cover it",
                under.customersForAnother() && under.feesWouldCoverAnother());
        assertTrue("...but a bank under its minimum opens no branch for the capital it would bring",
                !under.wantsBranch());

        Bank paying = mortgageBank(45_000);
        assertTrue("fixture: at 4.5% it is over its leverage target and under the top of its band",
                paying.payoutStance() == Bank.Payout.PAYING);
        double spare = paying.equity() - paying.leverageTarget() * paying.exposure();
        close("its payout is its share of a profit", paying.dividendDue(1_000), Bank.PAYOUT_IN_BAND * 1_000, 1e-12);
        close("...never past what it holds over its leverage target", paying.dividendDue(100_000), spare, 1e-9);
        close("...its buybacks too", paying.buybackRoom(0), spare, 1e-9);
        assertTrue("...which is less than the risk weights alone would have let it spend",
                spare < paying.equity() - paying.capitalTarget() * paying.getWeightedBook());
        close("...and its desk carries only what leaves it at the leverage target", paying.deskCanCarry(0, 1, 1),
                spare / paying.leverageTarget(), 1e-9);
        paying.payOwners(100_000);
        close("paid out, it holds its leverage target", paying.equity(), paying.targetEquity(), 1e-9);
        assertTrue("...which is at least LEVERAGE_RATIO_MIN of everything it has lent",
                paying.equity() >= Bank.LEVERAGE_RATIO_MIN * paying.exposure());
        double footed = 0;
        for (Bank.WeightRow row : paying.weightTable()) footed += row.weighted();
        close("its weight table still foots", footed, paying.getWeightedBook(), 1e-9);
        assertTrue("...and the Bank tab's words read the leverage ratio",
                paying.status().contains("everything it has lent"));
    }

    /* ================== 11. A BRANCH THAT DOES NOT PAY IS CLOSED (round 2; 0.7.19) ==================

       REWRITTEN FOR 0.7.19, because Jerus replaced the rule it encoded. Old
       (0.7.11 round 2): a branch closed after BRANCH_CLOSE_MONTHS - the
       city's two-year distress fuse - of closed months in which the BOOK had
       not kept its branches' staff, one a month, never the last; the played
       city's six extra branches closed one at a time, the first only once the
       streak reached the fuse, and the streak survived a save. New (Bank,
       THE BRANCHES, BY THEIR CUSTOMERS): every branch past the first whose
       customers' fees do not cover what a branch costs closes at once, the
       month the fees are taken, as many as it takes, never the first; the
       played city's six close the month after they stand, all together, by
       the same retired-building path, with the same capital and audit
       assertions; and what the rule reads - the customers and the month's
       running costs - survives a save.
       ========================================================================================== */

    /** One closed month of a hand-built bank: this interest, these costs. */
    static void closeAMonth(Bank b, double interest, double payroll) {
        b.startMonth();
        b.takeInterest(interest);
        b.payRunning(payroll, payroll / 10);
        b.closeMonth();
    }

    static void branchesClose(Path root) {
        out.println("\n--- 11. a branch whose fees do not cover it is closed: at once, and never the last ---");

        Bank three = new Bank();
        three.refresh(3, 5_000_000, 0, 100_000, 0, 0);
        closeAMonth(three, 10, 300);
        double cost = three.runningCostPerBranch();
        three.startMonth();
        three.takeAccountFees(2.2 * cost);
        assertTrue("fixture: the month's fees cover two of its three branches",
                cost > 0 && three.branchesTheFeesCover() == 2);
        assertTrue("...and it closes the third that month, with no fuse to wait out",
                three.closesBranch() && three.branchesToClose() == 1);
        three.startMonth();
        three.takeAccountFees(3.1 * cost);
        assertTrue("a month whose fees cover all three closes nothing", !three.closesBranch());

        Bank one = new Bank();
        one.refresh(1, 5_000_000, 0, 100_000, 0, 0);
        for (int m = 0; m < 48; m++) closeAMonth(one, 10, 300);
        assertTrue("the last branch stays, however long its fees have not covered it",
                one.getUncoveredMonths() == 0 && !one.closesBranch() && one.getAccountFees() == 0);

        /*
         * ...AND IN A PLAYED CITY: the landlords' city with six more branches
         * standing from the founding than its fees can cover. The month after
         * they stand, the rule closes all six at once.
         */
        // Cause paying, housed customers before the first closure, rather
        // than hoping homes and their supporting works finish in this short window.
        Game g = landlordCity(root, "branches", true, 3);
        standLandlords(g);
        BuildingsTemplate branch = template(g, "Commercial Bank");
        long extraBranches = 7 - g.getBuildingManager().countByName("Commercial Bank");
        assertTrue("fixture: the standing town can be given exactly seven bank branches", extraBranches >= 0);
        quietly(() -> {
            g.getBuildingManager().addStack(branch, extraBranches, true);
            g.getLandManager().allocate(branch.getLandSqFt() * extraBranches);
        });
        assertTrue("fixture: exactly seven branches stand before the six close",
                g.getBuildingManager().countByName("Commercial Bank") == 7);
        Bank bank = g.getBank();
        int closedAt = -1, firstCostKnown = -1, closedIn = -1; long before = -1, after = -1;
        double equityResidual = Double.NaN, audit = Double.NaN, capitalisedBefore = 0, capitalisedAfter = 0;
        String said = null;
        for (int m = 0; m < 90 && closedAt < 0; m++) {
            long standing = g.getBuildingManager().countByName("Commercial Bank");
            double capitalised = bank.getBranchesCapitalised();
            // The rule reads what a branch cost the month before; a bank that
            // has closed no month with its branches standing has none to read.
            if (firstCostKnown < 0 && bank.runningCostPerBranch() > 0) firstCostKnown = m;
            quietly(() -> g.simulateMonths(1));
            long now = g.getBuildingManager().countByName("Commercial Bank");
            if (now < standing) {
                closedIn = m;
                closedAt = g.getMonth();
                before = standing;
                after = now;
                equityResidual = bank.equityMovement().residual();
                audit = g.getLastMoneyAudit().residual;
                for (DemolitionLog.Entry e : g.getDemolitionLog().recent(g.getMonth())) {
                    if (e.month == g.getMonth() && e.building.equals("Commercial Bank")) {
                        said = e.sector + " sold " + e.quantity;
                    }
                }
                capitalisedBefore = capitalised;
                capitalisedAfter = bank.getBranchesCapitalised();
            }
        }
        assertTrue("fixture: the played bank closed its branches", closedAt > 0);
        assertTrue("...in the first month its rule had a branch's cost to read", closedIn >= 0 && closedIn == firstCostKnown);
        close("...all six past the first, at once - the town's fees cover none of them", before - after, 6, 0);
        same("...sold as retired buildings, by their owner - on the city's list of what came down", said,
                Sectors.RETAIL + " sold 6");
        close("...its founding capital left where it was", capitalisedAfter, capitalisedBefore, 0);
        close("the bank's equity moved by its income and its named causes that month", equityResidual, 0, 1e-6);
        close("...and the month's money audit closes through the closure", audit, 0, MoneyAudit.CENT);

        // ...and what the rule reads is carried in the save: the customers and
        // the month's running costs (Bank.monthLinesToSave(), 0.7.19).
        // REWRITTEN (revised 0.7.19, Jerus: "Exempt first branch"): it read
        // the month's operating cost, which a bank standing on its charter
        // alone no longer pays, so its reload compared nothing with nothing.
        // The cost the rule reads - what a branch past the charter carries,
        // its operating cost in it, from last month's figures - is what the
        // save must carry now.
        quietly(() -> g.simulateMonths(1));
        assertTrue("fixture: the bank has customers, and a branch past the charter would carry an operating cost the charter does not",
                bank.getCustomers() > 0 && bank.laterBranchCost() > bank.runningCostPerBranch() && bank.getOperatingCost() == 0);
        final boolean[] saved = { false };
        quietly(() -> saved[0] = g.saveGame(1, "branch city").ok);
        GameFiles files = new GameFiles(root.resolve("branches"), root.resolve("branches-no-legacy"));
        Game back = new Game(files);
        quietly(() -> back.loadGameSave(1));
        close("a reloaded bank has the same customers", back.getBank().getCustomers(), bank.getCustomers(), 0);
        close("...the same cost a branch past the charter carries, the operating cost in it",
                back.getBank().laterBranchCost(), bank.laterBranchCost(), 0);
        close("...and stands the same branches", back.getBank().getBranches(), bank.getBranches(), 0);
    }

    /* ==================== 12. PAYOUTS AFTER PRINCIPAL (round 2) ==================== */

    /** The insured rate section 12's by-hand mortgages are written at: low enough that a mortgage's interest leaves the landlords' income standing while its principal can still pass it. */
    static final double PAYOUT_FIXTURE_RATE = .005;

    /** The homes section 12 stands for the landlords to let (buildStack, noConstruction): enough that the month's rent is more than its costs once their tenants have moved in. */
    static final int HOMES_STANDING = 50;

    /** HOMES_STANDING homes stand for the landlords to own (in a town whose works stand: the founding town builds none at once), and they are held from ordering: what a by-hand fixture owes against, and what keeps nothing but its own mortgages on their books. */
    static void standLandlords(Game g) {
        quietly(() -> g.buildStack(template(g, "House"), HOMES_STANDING, true));
        g.getBusinessInvestment().holdSector(RE);
    }

    /** The most months section 12 plays for those homes to earn, the first month of a founded town being its start-up: a fixture bound, and the fixture fails past it. */
    static final int START_UP_MONTHS = 6;

    /** One month of the landlords' books as the dividend step reads them: what it earned and repaid, what it paid its owners, what the desk lent it, and the till as the dividend found it. */
    static final class LandlordMonth {
        double income, principal, paid, shortfall, till = -1;
    }

    /** One month played, the till read where the dividend step finds it (the settle probe, after the month's investment and before the owners are paid): nothing in the month moves. */
    static LandlordMonth playLandlordMonth(Game g) {
        EconomyManager econ = g.getEconomyManager();
        LandlordMonth month = new LandlordMonth();
        g.settleProbeForTest = after -> { if (after) month.till = econ.getSectorCash(RE); };
        quietly(() -> g.simulateMonths(1));
        g.settleProbeForTest = null;
        SectorBooks.SectorMonth now = g.getSectorBooks().get(RE);
        month.income = now.netIncome();
        month.principal = Math.max(0, now.repaid()) + Math.max(0, now.bondsRepaid());
        month.paid = now.dividendsPaid();
        month.shortfall = econ.getBusinessDebtManager().getShortfallLentThisMonth(RE);
        return month;
    }

    /** What the shortfall desk rolled of a month's principal, in the month that pays on it (Game.dividendDue(): never more than what fell due). */
    static double rolled(LandlordMonth books, LandlordMonth paying) {
        return Math.min(books.principal, paying.shortfall);
    }

    /**
     * A mortgage written by hand on the landlords whose first month repays `firstMonthPrincipal` of principal: the annuity less
     * its interest at PAYOUT_FIXTURE_RATE, per dollar of shortfall, from a bare lender's own mortgage (the model's arithmetic, no
     * played month's). Its loan, less the fee, goes to the till as the door hands it (Game.mortgageInvestor()), so the landlords
     * own what they owe and the lender's default point is not what the month reads.
     */
    static Mortgage borrowByHand(Game g, double firstMonthPrincipal) {
        BusinessDebtManager bare = new BusinessDebtManager();
        bare.setInsuredMortgageRate(PAYOUT_FIXTURE_RATE);
        bare.startAuditMonth();
        Mortgage probe = bare.issueMortgage(RE, 1_000, 0);
        double perDollar = (probe.getMonthlyPayment() - probe.getOutstandingPrincipal() * PAYOUT_FIXTURE_RATE / 12) / 1_000;
        EconomyManager econ = g.getEconomyManager();
        BusinessDebtManager lender = econ.getBusinessDebtManager();
        lender.setInsuredMortgageRate(PAYOUT_FIXTURE_RATE);
        Mortgage m = lender.issueMortgage(RE, firstMonthPrincipal / perDollar, g.getMonth());
        econ.setSectorCash(RE, econ.getSectorCash(RE) + m.getLoan() - BusinessDebtManager.feeOn(m.getOutstandingPrincipal()));
        return m;
    }

    static void payoutsAfterPrincipal(Path root) {
        out.println("\n--- 12. payouts after principal: a share of what the month leaves once the lender is paid ---");

        /*
         * BOTH MONTHS ARE CAUSED (FS9, 2026-11-21). The fixture needs landlords who earn more than their mortgages
         * take in one month and less in another. The played town found them by searching sixty months, and since HN1-A
         * (the landlords order at the door's price) its lean month, the principal past the income, no longer comes in
         * them. So the town is built for it: the
         * works stand from the start (0.7.17), HOMES_STANDING homes stand for the landlords to let, the landlords are
         * held from ordering, and the mortgages are written by hand on them (borrowByHand()) at a rate that leaves
         * their income standing - the first small, its principal a tenth of the income the month before, the second
         * large, its principal twice the income the month before it. Each month's books are what the next month's
         * dividend is struck on (Game.dividendDue()), so each is played and then the month after it, which pays.
         * Their till is the mortgages' proceeds, so what is paid is the rule's and not the till's.
         */
        Game g = landlordCity(root, "payout", true, 3);
        // THE DEFAULTS PUT BACK IN THE FIXTURE (BA1 on R0, 2026-11-05; OP1): the town this was written against taxes from month 1,
        // as main's R0 fixtures had it (RailDepotCheck, HaulageCheck): put back before a month has run (every lever is open since OP1).
        EconomyManager econ = g.getEconomyManager();
        econ.getTaxPolicy().reset();
        Equity register = g.getEquity();
        int landlords = Equity.indexOf(RE);
        standLandlords(g);

        java.util.List<LandlordMonth> months = new java.util.ArrayList<>();
        LandlordMonth start = null;
        for (int i = 0; i < START_UP_MONTHS && (start == null || !(start.income > 0)); i++) {
            start = playLandlordMonth(g);
            months.add(start);
        }
        assertTrue(String.format("fixture: the landlords' standing homes earned before the mortgages, %.4f a month", start.income),
                start.income > 0 && g.getBuildingManager().getTotalHomes() >= HOMES_STANDING);
        if (!(start.income > 0)) return;

        // ...the month that earns more than its principal: a mortgage whose first month repays a tenth of the income before it.
        borrowByHand(g, start.income / 10);
        LandlordMonth beat = playLandlordMonth(g), pays = playLandlordMonth(g);
        months.add(beat);
        months.add(pays);
        // ...and the one that earns less: a second, whose first month repays twice the income the month before it.
        borrowByHand(g, 2 * pays.income);
        LandlordMonth lean = playLandlordMonth(g), leanPays = playLandlordMonth(g);
        months.add(lean);
        months.add(leanPays);

        double rolledOnBeat = rolled(beat, pays), rolledOnLean = rolled(lean, leanPays);
        double paidOn = beat.income - beat.principal, paidDue = Equity.PAYOUT * paidOn;
        double nothingIncome = lean.income, nothingPrincipal = lean.principal - rolledOnLean;
        out.printf("   the small mortgage: $%,.4fk a month of income against $%,.4fk of principal; the large: $%,.4fk against $%,.4fk%n",
                beat.income, beat.principal, lean.income, lean.principal);
        assertTrue("fixture: a month whose income beat the principal its mortgages took, and one it did not",
                beat.income > beat.principal && beat.principal > 0 && lean.income > 0 && nothingPrincipal > nothingIncome);
        assertTrue(String.format("fixture: the shortfall desk rolled none of either month's principal (%.4f and %.4f)", rolledOnBeat, rolledOnLean),
                rolledOnBeat == 0 && rolledOnLean == 0);
        assertTrue(String.format("fixture: the landlords' till covers the dividend due (%.4f against %.4f)", pays.till, paidDue),
                paidDue > 0 && pays.till >= paidDue);
        assertTrue(String.format("fixture: ...and the dividend the old rule would pay on the lean month (%.4f against %.4f)",
                leanPays.till, Equity.PAYOUT * nothingIncome), leanPays.till >= Equity.PAYOUT * nothingIncome);
        close("a landlord with a mortgage pays PAYOUT of its income less the principal it repaid", pays.paid, paidDue, 1e-9);
        assertTrue(String.format("...and a month that earned %.2f against %.2f of principal pays nothing,"
                        + " where the old rule paid %.2f", nothingIncome, nothingPrincipal, Equity.PAYOUT * nothingIncome),
                leanPays.paid == 0 && register.dividendDue(landlords, nothingIncome, nothingPrincipal) == 0
                        && register.dividendDue(landlords, nothingIncome) > 0);
        boolean everPastIt = false;
        for (int k = 1; k < months.size(); k++) {
            LandlordMonth books = months.get(k - 1), paying = months.get(k);
            double due = register.dividendDue(landlords, books.income, books.principal - rolled(books, paying));
            everPastIt |= paying.paid > due + 1e-9;
        }
        assertTrue("no month paid the landlords past what their income left after the principal, net of what the desk rolled", !everPastIt);
        close("with nothing repaid the rule is the old one",
                register.dividendDue(landlords, 100, 0), register.dividendDue(landlords, 100), 0);
    }
}

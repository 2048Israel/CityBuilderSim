package ham.citybuildersim;

/** Verifies private-sector credit: pricing, origination, rollover, cash conservation. */
public class CreditCheck {

    static int fails = 0;

    static void check(String label, double actual, double expected) {
        boolean ok = Math.abs(actual - expected) < 1e-6;
        if (!ok) fails++;
        System.out.printf("%-50s %14.4f  expected %14.4f  %s%n",
                label, actual, expected, ok ? "OK" : "FAIL");
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-50s %s%n", label, ok ? "OK" : "FAIL");
    }

    static final String IND = Sectors.INDUSTRY;

    /** The standing rate at a given cash position, leaving the market as it found it. */
    static double priced(DebtManager m, double cash) {
        double before = m.getOverdraft();
        m.setCashPosition(cash);
        m.updateInterest();
        double r = m.getRate();
        m.setCashPosition(-before);
        m.updateInterest();
        return r;
    }

    public static void main(String[] args) throws Exception {

        /* ==================== 1. pricing ==================== */
        /*
         * PRIME PLUS THE BORROWER'S OWN SPREAD, since 0.7.7. The spread was
         * struck over the city's rate with a one-point floor - "nobody borrows
         * at sovereign" - and prime carries what that point stood for (the
         * bank's costs and the base expected loss), so it comes off: a
         * debt-free business pays prime.
         *
         * THE SPREAD IS THE CURVE'S SINCE 0.7.8 (Jerus: "Price risk from the
         * curve"): the borrower's own expected loss, a loan's loss given
         * default (LOAN_LOSS_GIVEN_DEFAULT since 0.7.12 round 2) x
         * PD(L), over the BASE_LOSS_RATE prime already carries, at the
         * leverage the loan leaves it at - no longer a line in leverage
         * capped at MAX_SPREAD - MIN_SPREAD. This section asserted the line
         * and its cap; it asserts the curve now, against the class's own
         * functions, and the points where the two meant the same thing (no
         * debt pays prime; a loan prices itself in; a borrower with nothing
         * is not a good credit) are asserted as they were.
         */
        System.out.println("--- pricing: prime + the borrower's own expected loss, off the curve ---");

        double lgd = BusinessDebtManager.LOAN_LOSS_GIVEN_DEFAULT, base = Bank.BASE_LOSS_RATE;
        BusinessDebtManager m = new BusinessDebtManager();
        m.setPrimeRate(.01);
        m.setAssets(IND, 100000);
        m.updateRates();

        // No debt: the best credit a sector can have pays prime and nothing over it.
        check("no debt -> no spread over prime", m.getSpread(IND), 0);
        check("no debt -> rate is prime", m.getRate(IND), .01);

        // debt/assets = 0.5 -> its expected loss there, over prime: nothing,
        // because half its assets is far under the curve's knee.
        m.issueLoan(IND, 50000, 1);
        m.setAssets(IND, 100000);
        m.updateRates();
        check("leverage 0.5", m.getLeverage(IND), .5);
        check("leverage 0.5 -> spread, its expected loss over the book's",
                m.getSpread(IND), Math.max(0, lgd * BusinessDebtManager.defaultProbability(.5) - base));
        check("...which at half its assets is nothing", m.getSpread(IND), 0);
        check("leverage 0.5 -> rate", m.getRate(IND), .01);

        // Push leverage past the old cap: the spread is the curve's, uncapped.
        m.issueLoan(IND, 150000, 1);
        m.setAssets(IND, 100000);
        m.updateRates();
        check("leverage now 2.0", m.getLeverage(IND), 2.0);
        double at2 = lgd * BusinessDebtManager.defaultProbability(2.0) - base;
        check("at 2.0 the spread is LGD x PD(2.0) less BASE_LOSS_RATE, uncapped", m.getSpread(IND), at2);
        check("rate is prime + that", m.getRate(IND), .01 + at2);

        // The spread is the borrower's risk, so a higher prime carries through
        // and the risk part does not move with it.
        m.setPrimeRate(.20);
        m.updateRates();
        check("prime 20% -> business 20% + the same spread", m.getRate(IND), .20 + at2);

        // Insolvent: owes money against non-positive assets. Worst case.
        m.setPrimeRate(.01);
        m.setAssets(IND, -5000);
        m.updateRates();
        check("negative assets -> the whole curve, LGD less BASE_LOSS_RATE", m.getSpread(IND), lgd - base);

        // An empty or insolvent business with NO debt is not a good credit
        // either. The first version returned the minimum spread here, which is
        // how the insolvent food industry came to borrow $49,611 at 2%.
        BusinessDebtManager broke = new BusinessDebtManager();
        broke.setPrimeRate(.01);
        broke.setAssets(IND, -22815);
        broke.updateRates();
        check("no debt but insolvent -> the whole curve, not prime", broke.getSpread(IND), lgd - base);
        check("...and the loan is written at that rate",
                broke.issueLoan(IND, 49611, 1).getAnnualRate(), .01 + lgd - base);

        // A loan must be priced including itself, not off the balance sheet from
        // before it existed - otherwise every sector's first loan is the cheapest
        // one it will ever get, however large.
        BusinessDebtManager fresh = new BusinessDebtManager();
        fresh.setPrimeRate(.01);
        fresh.setAssets(IND, 100000);
        fresh.updateRates();
        check("quoted rate before borrowing", fresh.getRate(IND), .01);
        // borrowing 100,000 against 100,000 of assets is leverage 1.0
        check("but a big loan prices itself in",
                fresh.issueLoan(IND, 100000, 1).getAnnualRate(),
                .01 + lgd * BusinessDebtManager.defaultProbability(1.0) - base);

        /*
         * ...AND A PROJECT'S LOAN COUNTS ITS BUILDING (0.7.8), at the loan's
         * value, the way canFundProject() reads the deal: the same 100,000
         * borrowed for a building leaves the sector at 100,000 over 200,000.
         * A shortfall loan's proceeds cover losses already on its books, so
         * it counts none. And the rate a project is JUDGED at is the one it
         * would be written at, not today's quote - Game.consider() reads
         * projectRate(), which is the brake on building up the curve.
         */
        BusinessDebtManager builder = new BusinessDebtManager();
        builder.setPrimeRate(.01);
        builder.setAssets(IND, 100000);
        builder.issueLoan(IND, 30000, 1);
        builder.updateRates();
        double quoted = builder.getRate(IND);
        double project = 500000;
        double leverageAfter = (30000 + project) / (100000 + project);
        check("a project's loan is priced at the leverage it leaves the sector at, its building counted",
                builder.projectRate(IND, project),
                .01 + Math.max(0, lgd * BusinessDebtManager.defaultProbability(leverageAfter) - base));
        check("...which is the leverage canFundProject() reads after the deal",
                builder.leverageAfterProject(IND, project), leverageAfter);
        assertTrue("...dearer than today's quote when it takes the sector up the curve",
                builder.projectRate(IND, project) > quoted);
        double judged = builder.projectRate(IND, project);
        check("...and the loan is written at exactly the rate it was judged at",
                builder.issueProjectLoan(IND, project, 2).getAnnualRate(), judged);
        BusinessDebtManager shortOne = new BusinessDebtManager();
        shortOne.setPrimeRate(.01);
        shortOne.setAssets(IND, 100000);
        check("a shortfall loan counts no building: what it will owe over what it owns",
                shortOne.issueLoan(IND, 80000, 1).getAnnualRate(),
                .01 + Math.max(0, lgd * BusinessDebtManager.defaultProbability(.8) - base));

        /* ---------- ...and it pays its fee out of the proceeds (0.7.7) ---------- */
        check("a loan's fee is Bank.LOAN_FEE of its principal",
                BusinessDebtManager.feeOn(100000), 100000 * Bank.LOAN_FEE);
        check("...counted for the month, for the bank to collect at the settle",
                fresh.getFeesThisMonth(), BusinessDebtManager.feeOn(100000));
        check("...and against the sector that borrowed",
                fresh.getFeesThisMonth(IND), BusinessDebtManager.feeOn(100000));
        fresh.startAuditMonth();
        check("...and cleared with the month", fresh.getFeesThisMonth(), 0);

        /* ==================== 2. rate is fixed at issue ==================== */
        System.out.println("\n--- a loan keeps the rate it was written at ---");

        BusinessDebtManager m2 = new BusinessDebtManager();
        m2.setPrimeRate(.01);
        m2.setAssets(IND, 1000000);
        m2.updateRates();
        BusinessLoan cheap = m2.issueLoan(IND, 10000, 1);
        double cheapRate = cheap.getAnnualRate();

        // Credit deteriorates badly...
        m2.setAssets(IND, 1000);
        m2.updateRates();
        assertTrue("new borrowing got dearer", m2.getRate(IND) > cheapRate);
        check("but the old loan's rate is unchanged", cheap.getAnnualRate(), cheapRate);
        check("interest still priced off the old rate",
                cheap.getMonthlyInterestExpense(), 10000 * cheapRate / 12);

        /* ==================== 3. origination ==================== */
        System.out.println("\n--- shortfall borrowing: hole + 3 months of the loss ---");

        BusinessDebtManager m3 = new BusinessDebtManager();
        m3.setPrimeRate(.01);
        m3.setAssets(IND, 50000);
        m3.updateRates();

        check("solvent sector borrows nothing", m3.coverShortfall(IND, 5000, 0, 1), 0);
        check("...and has no debt", m3.getPrincipal(IND), 0);

        // $1,000 overdrawn, losing $200/month -> 1000 + 3*200 = 1600 in hand,
        // borrowed grossed up for the loan's fee, which comes out of it (0.7.7)
        double lent = m3.coverShortfall(IND, -1000, 200, 1);
        check("handed hole + buffer, the fee kept back", lent - BusinessDebtManager.feeOn(lent), 1600);
        check("principal on the books: that, grossed up for the fee", m3.getPrincipal(IND),
                1600 / (1 - Bank.LOAN_FEE));
        check("one loan, not many", m3.getLoanCount(IND), 1);

        /* ---------- ...and it stops at the borrower's own insolvency line ----------
         *
         * THE TEST THAT WAS NOT THERE. Until 2026-09-10 coverShortfall() had no
         * underwriting of any kind: a negative balance got a loan, any size, at
         * any leverage, every month. Meanwhile restructure() in the same class
         * calls a sector insolvent the moment its principal passes assets x
         * INSOLVENCY_TRIGGER. One half of a rule enforced, the other half not:
         * the lender wrote the loan that bankrupted its borrower and then ate
         * the write-down, on a loop, once every forty-five months for the food
         * industry across a four-thousand-month run.
         *
         * The fixture is built to CAUSE the condition rather than stand near
         * it: a sector with known assets is walked up to the line by borrowing,
         * and then asked for more.
         */
        BusinessDebtManager m3b = new BusinessDebtManager();
        m3b.setPrimeRate(.01);
        m3b.setAssets(IND, 1000);
        m3b.updateRates();

        double ceiling = 1000 * BusinessDebtManager.MAX_LOAN_TO_ASSETS;

        /* ---------- ...and the ceiling is BELOW the line, by a real gap ----------
         *
         * The first version of this fixture asserted the loan was capped "at
         * the insolvency line", and it was - the ceiling was DEFINED as the
         * trigger. So the underwriter parked every borrower exactly where a
         * one-dollar fall in assets made it insolvent and cost the lender 60%.
         * Twenty-four of thirty-two restructures in a 4,000-month run fired
         * between 1.50 and 1.74 times assets. The assertion below is the one
         * that would have caught it: a borrower lent to the ceiling has to be
         * able to lose a real share of its assets and still be solvent.
         */
        assertTrue("fixture: the ceiling is strictly below the write-down line",
                BusinessDebtManager.MAX_LOAN_TO_ASSETS < BusinessDebtManager.INSOLVENCY_TRIGGER);

        // Ask for far more than the ceiling allows, in one go.
        double capped = m3b.coverShortfall(IND, -100000, 0, 1);
        check("a loan is capped at the ceiling, not at what was asked", capped, ceiling);
        check("...so the principal sits exactly on it", m3b.getPrincipal(IND), ceiling);
        /*
         * "SOLVENT" SINCE 0.7.8 is short of the default point: a sector past
         * INSOLVENCY_TRIGGER is no longer written down whole (isInsolvent() is
         * the backstop's, a sector with nothing left), it has half its firms
         * default within a year. The gap below is what keeps a borrower at the
         * ceiling short of that line - the premise, asserted against the line
         * itself rather than against a predicate that no longer reads it.
         */
        assertTrue("...and the borrower is short of the default point there",
                m3b.getLeverage(IND) < BusinessDebtManager.INSOLVENCY_TRIGGER
                        && m3b.getDefaultRate(IND) < .5);

        // The gap: lose a third of the assets at the ceiling and still be short of the line.
        m3b.setAssets(IND, 1000 * (2.0 / 3));
        assertTrue("a borrower at the ceiling survives a one-third fall in assets, short of the default point",
                m3b.getLeverage(IND) < BusinessDebtManager.INSOLVENCY_TRIGGER
                        && m3b.getDefaultRate(IND) < .5);
        m3b.setAssets(IND, 1000);

        /*
         * At the ceiling the desk lends ONE more thing: this month's interest,
         * as a construction lender carries an interest reserve, and only up to
         * the write-down line. A borrower that has just built on credit sits
         * above the ceiling for a year or two and could not otherwise borrow
         * the interest on the loan that built it. Losses beyond it are still
         * refused - the second call, with the interest already advanced, gets
         * nothing at all.
         */
        double interestDue = m3b.getMonthlyInterest(IND);
        assertTrue("fixture: the borrower at the ceiling owes interest this month", interestDue > 0);
        double advanced = m3b.coverShortfall(IND, -5000, 0, 2);
        check("at the ceiling, the desk hands over this month's interest and no more, its fee on top",
                advanced - BusinessDebtManager.feeOn(advanced), interestDue);
        check("...so the books moved by exactly that", m3b.getPrincipal(IND), ceiling + advanced);
        check("...and there is nothing left to lend for losses", m3b.borrowingRoom(IND), 0);
        double atCeiling = m3b.getPrincipal(IND);

        // Assets grow: the room comes back, because the ceiling moved.
        m3b.setAssets(IND, 2000);
        check("the room the shortfall desk has grows with the assets",
                m3b.borrowingRoom(IND), 2000 * BusinessDebtManager.MAX_LOAN_TO_ASSETS - atCeiling);
        double more = m3b.coverShortfall(IND, -100000, 0, 3);
        check("a borrower whose assets grew can borrow again",
                more, 2000 * BusinessDebtManager.MAX_LOAN_TO_ASSETS - atCeiling);

        /* ---------- ...and a shut lender lends nothing, from either desk ----------
         *
         * Bank.capacity() is zero in resolution and nothing here ever asked.
         * Measured over 4,000 months: 326 months began with the bank frozen
         * and $5.05bn of business credit was written inside them.
         */
        BusinessDebtManager m3s = new BusinessDebtManager();
        m3s.setPrimeRate(.01);
        m3s.setAssets(IND, 1000);
        m3s.updateRates();
        m3s.setLendingOpen(false);
        check("a frozen bank lends nothing to a short sector", m3s.coverShortfall(IND, -100, 0, 1), 0);
        check("...and offers the investment desk no room", m3s.borrowingRoom(IND), 0);
        m3s.setLendingOpen(true);
        assertTrue("...until it is standing again", m3s.borrowingRoom(IND) > 0);

        // ...and a sector with nothing behind it gets nothing.
        BusinessDebtManager m3c = new BusinessDebtManager();
        m3c.setPrimeRate(.01);
        m3c.setAssets(IND, 0);
        m3c.updateRates();
        check("a sector with no assets at all cannot borrow a penny",
                m3c.coverShortfall(IND, -5000, 100, 1), 0);

        /* ---------- ...and a repeat defaulter is shut out for longer ----------
         *
         * A flat twelve months was the same answer for a first default and a
         * twentieth, so a sector with no viable business defaulted, sat out its
         * year, re-levered against the same assets and defaulted again - 163
         * restructures across a four-thousand-month run, one somewhere every
         * 25 months. The exclusion is the borrower's record and now grows with
         * it. See BusinessDebtManager.exclusionFor().
         *
         * Driven through restructure() rather than by reading the helper, so
         * the fixture tests the path the game takes - and, since 0.7.8, with
         * the assets gone below nothing, because only the backstop counts a
         * default on the record: a sector past the line with assets standing
         * loses a slice a month and keeps its record (BankCheck (14)).
         */
        BusinessDebtManager m3d = new BusinessDebtManager();
        m3d.setPrimeRate(.01);
        m3d.setAssets(IND, 1000);
        m3d.updateRates();

        int[] shutOutFor = new int[3];
        for (int attempt = 0; attempt < 3; attempt++) {
            // Borrow to the line, then let the assets fall so the same debt is
            // past it - which is what actually makes a sector insolvent here.
            m3d.setAssets(IND, 1000);
            for (int wait = 0; wait < 40; wait++) m3d.advanceBlocks();  // clear the ban
            m3d.coverShortfall(IND, -100000, 0, attempt * 100 + 1);
            m3d.setAssets(IND, -100);
            assertTrue("fixture: attempt " + (attempt + 1)
                    + " really did leave it insolvent", m3d.isInsolvent(IND));
            m3d.restructure(IND);
            shutOutFor[attempt] = m3d.getBlockedMonths(IND);
        }

        System.out.printf("   shut out for %d, then %d, then %d months%n",
                shutOutFor[0], shutOutFor[1], shutOutFor[2]);

        check("a first default costs a year", shutOutFor[0], 12);
        assertTrue("...a second costs longer than the first",
                shutOutFor[1] > shutOutFor[0]);
        assertTrue("...and a third longer than the second",
                shutOutFor[2] > shutOutFor[1]);

        /* ---------- ...and the record is priced, not only banned on ----------
         *
         * Defaulting used to CUT the rate: restructure() repriced off the
         * post-write-down leverage and the record never entered the price, so
         * a sector quoted 8.5% at the ceiling was quoted 5.1% the month after
         * it defaulted. Same assets, same debt, one clean borrower and one with
         * three write-downs behind it: the second has to pay more.
         */
        BusinessDebtManager spotless = new BusinessDebtManager();
        spotless.setPrimeRate(.01);
        spotless.setAssets(IND, 1000);
        spotless.updateRates();
        // The backstop leaves nothing owing (RESTRUCTURE_TARGET of no assets),
        // so both are handed the same loan - the 60 a write-down to
        // RESTRUCTURE_TARGET of 100 used to leave - to be priced at a real
        // leverage rather than at none.
        m3d.issueLoan(IND, 60, 301);
        spotless.issueLoan(IND, m3d.getPrincipal(IND), 1);
        m3d.setAssets(IND, 1000);
        m3d.updateRates();
        spotless.updateRates();
        System.out.printf("   same leverage: spotless %.2f%%, three defaults %.2f%%%n",
                spotless.getRate(IND) * 100, m3d.getRate(IND) * 100);
        assertTrue("fixture: the two borrowers really are at the same leverage",
                Math.abs(spotless.getPrincipal(IND) - m3d.getPrincipal(IND)) < 1e-9);
        assertTrue("a serial defaulter is quoted more than a spotless borrower at the same leverage",
                m3d.getRate(IND) > spotless.getRate(IND) + .02);

        /* ---------- ...and a bankruptcy forgives the overdraft, once ----------
         *
         * A restructure wrote the loan down and left the overdraft standing,
         * so the sector was as insolvent as it found it and was judged so
         * again every month of its ban - 46 write-downs on Retail in one run
         * and a 525-month exclusion. Jerus's call: the overdraft is written
         * off and declared, the record grows by one, and a sector inside its
         * ban is one episode, not a default a month.
         */
        BusinessDebtManager m3e = new BusinessDebtManager();
        m3e.setPrimeRate(.01);
        m3e.setAssets(IND, 1000);
        m3e.updateRates();
        m3e.coverShortfall(IND, -100000, 0, 1);             // to the ceiling: 900
        m3e.setAssets(IND, -500);                            // ...and then under water
        m3e.setCash(IND, -1500);
        assertTrue("fixture: overdrawn past everything it owns is insolvent", m3e.isInsolvent(IND));
        double loanLoss = m3e.restructure(IND);
        check("the loan is written off in full against nothing", loanLoss, 900);
        check("...and the overdraft with it", m3e.takeOverdraftForgiven(IND), 1500);
        check("...once", m3e.takeOverdraftForgiven(IND), 0);
        check("one default on the record", m3e.getRestructureCount(IND), 1);
        assertTrue("...and a ban", m3e.isBorrowingBlocked(IND));
        m3e.setCash(IND, -200);                              // still bleeding, still banned
        m3e.setAssets(IND, -200);
        assertTrue("fixture: it is insolvent again inside its ban", m3e.isInsolvent(IND));
        check("...but a sector inside its ban is not a new default", m3e.restructure(IND), 0);
        check("...so the record does not grow", m3e.getRestructureCount(IND), 1);
        for (int wait = 0; wait < 40; wait++) m3e.advanceBlocks();
        assertTrue("fixture: the ban has lifted", !m3e.isBorrowingBlocked(IND));
        m3e.restructure(IND);
        check("still under water when the ban lifts IS a new default", m3e.getRestructureCount(IND), 2);
        check("...and the overdraft it ran up meanwhile is forgiven too", m3e.takeOverdraftForgiven(IND), 200);

        // No loan at all, only an overdraft bigger than the plant: still bust.
        BusinessDebtManager m3f = new BusinessDebtManager();
        m3f.setAssets(IND, -50);
        m3f.setCash(IND, -1050);
        assertTrue("a sector with no loans and an overdraft past its plant is insolvent", m3f.isInsolvent(IND));
        m3f.setAssets(IND, 500);
        assertTrue("...and one whose plant still outweighs the overdraft is not", !m3f.isInsolvent(IND));

        /* ==================== 4. maturity and rollover ==================== */
        System.out.println("\n--- 36-month term, then rollover ---");

        BusinessDebtManager m4 = new BusinessDebtManager();
        m4.setPrimeRate(.01);
        m4.setAssets(IND, 50000);
        m4.updateRates();
        m4.issueLoan(IND, 12000, 1);

        for (int month = 1; month <= 35; month++) {
            m4.processMonth();
        }
        check("still outstanding at month 35", m4.getPrincipal(IND), 12000);
        check("nothing matured yet", m4.takeMaturedPrincipal(IND), 0);

        m4.processMonth();   // 36th
        check("loan retired", m4.getPrincipal(IND), 0);
        double due = m4.takeMaturedPrincipal(IND);
        check("principal fell due", due, 12000);
        check("and is only handed over once", m4.takeMaturedPrincipal(IND), 0);

        // A sector with no cash rolls it: the balloon takes it negative, the
        // shortfall check writes a replacement.
        double cash = 500 - due;
        assertTrue("balloon took cash negative", cash < 0);
        // The sector is handed the loan less its fee, as the game hands it
        // (EconomyManager.settleBusinessCredit(), 0.7.7).
        double rolled = m4.coverShortfall(IND, cash, 0, 37);
        cash += rolled - BusinessDebtManager.feeOn(rolled);
        check("refinanced back to zero", cash, 0);
        check("new loan on the books, grossed up for its fee", m4.getPrincipal(IND),
                11500 / (1 - Bank.LOAN_FEE));
        check("matures 36 months later", m4.getLoans(IND).get(0).getMaturityMonth(), 73);

        /* ==================== 5. cash conservation ==================== */
        System.out.println("\n--- interest must be charged exactly once ---");

        // The trap this design exists to avoid: interest is an income-statement
        // expense, so cash moves by net income. If the debt manager ALSO took it
        // out of cash, the sector would pay twice.
        BusinessDebtManager m5 = new BusinessDebtManager();
        m5.setPrimeRate(.01);
        m5.setAssets(IND, 100000);
        m5.updateRates();
        m5.issueLoan(IND, 24000, 1);

        double interest = m5.getMonthlyInterest(IND);
        assertTrue("there is interest to pay", interest > 0);

        // A bare sector off the template - no buildings, no markets - with one
        // sale of the month booked into its ledger by hand.
        Sector ih = new ham.citybuildersim.sectors.FoodIndustry();
        ih.setCash(10000);
        ih.bookSale(new Trade(Good.BREAD, ih.key(), Sectors.RETAIL, 1000, .50));   // revenue 500
        ih.setEnergyRatio(1);
        ih.setWaterRatio(1);
        ih.updateJobFillRate(new double[11]);
        ih.updateWages(new double[11], new int[11]);
        ih.setInterestExpense(interest);
        // Struck, then banked - see BooksCheck for why they are two calls now.
        ih.strike();
        ih.bank(0);

        check("operating income excludes interest", ih.statement().operatingIncome, 500);
        check("interest expensed", ih.statement().interest, interest);
        check("pre-tax income is net of interest", ih.getNetIncome(), 500 - interest);
        check("cash moved by exactly that", ih.getCash(), 10000 + 500 - interest);

        // processMonth must not touch anyone's cash
        double before = ih.getCash();
        m5.processMonth();
        check("processMonth moved no cash", ih.getCash(), before);

        /* ==================== 6. balance sheet integration ==================== */
        System.out.println("\n--- the loan shows up as a liability ---");

        ih.setBalanceSheetInputs(0, 20000, m5.getPrincipal(IND));
        BalanceSheet bs = ih.getBalanceSheet();

        check("loans payable", bs.getTotalLiabilities(), 24000);
        check("still balances", bs.getTotalAssets(), bs.getTotalLiabilitiesAndEquity());
        check("equity is now assets less debt", bs.getEquity(), bs.getTotalAssets() - 24000);

        /* ==================== 7. the spiral guard ==================== */
        System.out.println("\n--- a chronically loss-making sector, 60 months ---");

        // This is the case the 3-month buffer exists for. Without it the sector
        // writes a new loan every single month.
        BusinessDebtManager m6 = new BusinessDebtManager();
        m6.setPrimeRate(.01);
        double sectorCash = 0;
        double monthlyLoss = 300;

        for (int month = 1; month <= 60; month++) {
            m6.setAssets(IND, 40000);
            m6.updateRates();
            sectorCash -= monthlyLoss;                       // the operating loss
            sectorCash -= m6.getMonthlyInterest(IND);        // plus debt service
            m6.processMonth();
            sectorCash -= m6.takeMaturedPrincipal(IND);
            sectorCash += m6.coverShortfall(IND, sectorCash, monthlyLoss, month);
        }

        System.out.printf("   after 60 months: %d loans, $%.0f principal, rate %.2f%%%n",
                m6.getLoanCount(IND), m6.getPrincipal(IND), m6.getRate(IND) * 100);
        assertTrue("cash never left negative", sectorCash >= -1e-9);
        assertTrue("loan count stayed readable (<20, not 60)", m6.getLoanCount(IND) < 20);
        // No cap since 0.7.8: under the shortfall desk's ceiling the curve
        // charges at most its expected loss there, and that is the guard.
        check("its rate is the curve's at its leverage", m6.getRate(IND),
                .01 + BusinessDebtManager.expectedLossSpread(m6.getLeverage(IND)));
        assertTrue("...under what the curve charges at the shortfall ceiling",
                m6.getRate(IND) <= .01 + BusinessDebtManager.expectedLossSpread(
                        BusinessDebtManager.MAX_LOAN_TO_ASSETS) + 1e-9);

        /* ============ the CITY's debt market, repriced ============ */
        System.out.println("\n--- there is no free money ---");

        /*
         * Two rules, both of which the old model broke.
         *
         *   1. An overdraft is borrowing. A city $1.1M in the red with no bonds
         *      outstanding used to be quoted the 1% floor, because the only
         *      input was getAllPrincipal(). That happened in a real run.
         *   2. A loan is priced WITH ITSELF on the books. Pricing off the
         *      balance sheet as it stands before the money arrives is what let a
         *      debt-free city borrow ten million at one percent.
         */
        DebtManager market = new DebtManager();
        market.setGDP(9068);            // a mid-size city from the calibration run
        market.setTaxRevenue(1916);
        market.setCashPosition(0);
        market.updateInterest();

        double clean = market.getRate();
        // Against the model's floor rather than the 1% it used to be: the
        // floor is the policy rate since 0.7.0, and was the dial less two
        // points before it (DebtManager's THE FLOOR IS REAL NOW).
        assertTrue("a debt-free city with no overdraft prices at the floor",
                Math.abs(clean - market.floorRate()) < 1e-9);

        // 1. the overdraft
        market.setCashPosition(-500_000);
        market.updateInterest();
        double overdrawn = market.getRate();
        System.out.printf("   no debt, no overdraft:      %.2f%%%n", clean * 100);
        System.out.printf("   no debt, $500k overdrawn:   %.2f%%%n", overdrawn * 100);
        assertTrue("being overdrawn costs more than being clean", overdrawn > clean);
        assertTrue("...and a positive balance is not credit",
                priced(market, 250_000) >= clean);

        market.setCashPosition(0);
        market.updateInterest();

        // 2. the loan prices itself in
        double tiny = market.quoteRate(1_000);
        double large = market.quoteRate(5_000_000);
        System.out.printf("   quote for $1,000:           %.2f%%%n", tiny * 100);
        System.out.printf("   quote for $5,000,000:       %.2f%%%n", large * 100);
        assertTrue("a big loan is quoted dearer than a small one, on the same books",
                large > tiny);
        /*
         * Measured against the BAND, not against a number.
         *
         * This used to read "> .10", which was half the band when the ceiling
         * was 20% and is most of it now that the curve has been made gentler
         * twice. An assertion pinned to a constant that the thing under test is
         * allowed to move is an assertion that fails for being right.
         */
        double halfwayUp = market.floorRate()
                + (market.ceilingRate() - market.floorRate()) / 2;
        assertTrue("...and ten million is well up the band, not near the floor",
                market.quoteRate(10_000_000) > halfwayUp);
        assertTrue("the standing rate is unmoved by merely asking",
                Math.abs(market.getRate() - clean) < 1e-9);

        // 3. monotonic, and always inside the band
        double previous = -1;
        boolean rising = true, inBand = true;
        for (double loan = 0; loan <= 20_000_000; loan += 100_000) {
            double r = market.quoteRate(loan);
            if (r < previous - 1e-12) rising = false;
            if (r < market.floorRate() - 1e-9 || r > market.ceilingRate() + 1e-9) inBand = false;
            previous = r;
        }
        assertTrue("more borrowing never gets cheaper", rising);
        assertTrue("the quote never leaves the band", inBand);

        // 4. the fixed point converges and agrees with itself
        // Sized to sit ON the curve, not against the cap: this city prices to
        // the ceiling at about $360k, and a test run in the capped region would
        // "pass" by comparing 20% with 20% and prove nothing.
        double rounding = 1000;
        double asked = 150_000;
        double fixed = market.quoteRate(asked,
                r -> Math.ceil((asked / (1 - r)) / rounding) * rounding);
        double faceAtFixed = Math.ceil((asked / (1 - fixed)) / rounding) * rounding;
        double repriced = market.quoteRate(faceAtFixed);
        System.out.printf("   $150k bill: rate %.2f%%, face $%,.0f, reprices to %.2f%%%n",
                fixed * 100, faceAtFixed, repriced * 100);
        assertTrue("the quoted rate is a fixed point of its own face value",
                Math.abs(fixed - repriced) < 5e-4);
        assertTrue("...and a discounted bill costs more than its cash value implies",
                fixed >= market.quoteRate(asked) - 1e-9);

        // 5. an overdrawn city borrowing its way out is not charged for both
        market.setCashPosition(-300_000);
        market.updateInterest();
        double coveringTheHole = market.quoteRate(300_000);
        market.setCashPosition(0);
        market.updateInterest();
        double sameLoanClean = market.quoteRate(300_000);
        System.out.printf("   $300k to close a $300k hole: %.2f%%  (same loan, no hole: %.2f%%)%n",
                coveringTheHole * 100, sameLoanClean * 100);
        assertTrue("proceeds that close the overdraft are not counted twice",
                Math.abs(coveringTheHole - sameLoanClean) < 1e-9);

        // 6. the denominator really is half and half
        DebtManager taxPoor = new DebtManager();
        taxPoor.setGDP(9068);
        taxPoor.setTaxRevenue(200);          // same output, collects far less
        taxPoor.setCashPosition(0);
        assertTrue("a city that cannot tax its economy is the worse credit",
                taxPoor.quoteRate(100_000) > market.quoteRate(100_000));
        System.out.printf("   $100k costs %.2f%% here, %.2f%% to a city collecting a tenth as much%n",
                market.quoteRate(100_000) * 100, taxPoor.quoteRate(100_000) * 100);

        /* ============ 7. the quote IS the deal ============ */
        /*
         * The screens now show the player what a loan will cost before they
         * agree to it. That is only worth anything if the quote and the booking
         * are the same calculation - a preview computed separately is a second
         * definition of the deal, and second definitions drift.
         *
         * So: quote it, book it, and check the ledger against the piece of paper.
         *
         * SIZING. Money is in thousands, so a "250_000" here is a quarter of a
         * BILLION dollars and every quote for it comes back at the 20% ceiling.
         * A test run up there passes by comparing 20% with 20% - the same way an
         * earlier version of section 6 did - so onCurve() below refuses to let
         * that happen silently.
         */
        System.out.println("\n--- the quote is the deal ---");

        java.nio.file.Path root = java.nio.file.Files.createTempDirectory("creditcheck");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));

        Game city = new Game(files);
        city.run();
        // THE TREASURY THIS FIXTURE WAS WRITTEN AGAINST (0.7.10): its build list
        // is bought out of cash, and a city founds on D$100M since 0.7.10, not the
        // D$2.5B it assumed - so it is given that, the Wealthy preset's, explicitly.
        city.setCashForTest(Founding.WEALTHY_CASH);
        city.buildStack(template(city, "House"), 200, false);
        city.buildStack(template(city, "Convenience Store"), 5, false);
        city.buildStack(template(city, "Industrial Bakery"), 2, false);
        city.buildStack(template(city, "Construction Depot"), 4, false);
        /*
         * And a bank, because this section is about the CURVE.
         *
         * Until 0.7.7 the rate a borrower was quoted was the curve plus
         * whatever the bank's strain added, and a city with no branches had no
         * capacity and sat at the full premium - which pushed every quote here
         * through the ceiling and turned onCurve() below into a permanent
         * failure. The premium is gone (Bank, WHAT A LOAN COSTS) and the bank
         * stays: what is being measured here is what the city's own debt load
         * does to its price, so the bank is built and left unstrained.
         */
        city.buildStack(template(city, "Commercial Bank"), 1, false);
        city.simulateMonths(60);

        System.out.printf("   a city of %d at month %d: GDP $%,.0fk/mo, tax $%,.0fk/mo%n",
                city.getPopulationManager().getPopulation(), city.getMonth(),
                city.getEconomyManager().getMonthGdp(),
                city.getEconomyManager().getTaxIncome());

        // Quoting must not touch anything. Ask three hundred times, loudly.
        double principalBeforeAsking = city.getDebtManager().getAllPrincipal();
        double cashBeforeAsking = city.getCash();
        double rateBeforeAsking = city.getDebtManager().getRate();
        for (int i = 1; i <= 100; i++) {
            city.quoteTBill(i * 500, 3, 1000);
            city.quoteMediumBond(i * 500, 5, 10000);
            city.quoteLongBond(i * 500, 20, 100000);
        }
        assertTrue("three hundred quotes book no debt",
                city.getDebtManager().getAllPrincipal() == principalBeforeAsking);
        assertTrue("three hundred quotes move no cash",
                city.getCash() == cashBeforeAsking);
        assertTrue("three hundred quotes leave the standing rate alone",
                Math.abs(city.getDebtManager().getRate() - rateBeforeAsking) < 1e-12);

        /*
         * The thing the player is actually being shown: asking for more costs
         * more. Checked BEFORE anything is booked, because once the city is
         * carrying debt every further quote sits at the cap and the comparison
         * stops meaning anything.
         */
        DebtQuote modest = city.quoteMediumBond(5_000, 5, 10000);
        DebtQuote greedy = city.quoteMediumBond(40_000, 5, 10000);
        System.out.printf("   $5M quotes %.2f%%; $40M quotes %.2f%%%n",
                modest.marketRate() * 100, greedy.marketRate() * 100);
        assertTrue("asking for eight times as much is priced dearer",
                greedy.marketRate() > modest.marketRate());
        assertTrue("...and the small one is a real quote, not the cap",
                onCurve(modest));
        assertTrue("the quote reports the rate it is moving from",
                Math.abs(modest.rateBefore() - city.getDebtManager().getRate()) < 1e-12);

        // Now book each instrument and hold the ledger against the quote.
        bookAndCompare(city, "Note",      5_000,  3,  1000,   true);
        bookAndCompare(city, "Serial", 20_000, 5,  10000,  true);

        /*
         * The long bond twice. Once at a fine rounding so the rate lands on the
         * curve and a mismatch between quote and booking would actually show;
         * once at the 100,000 the issuance screen really passes, which for a
         * city this size is far past the cap - that is the path players use, so
         * it is checked too, just with no illusion about what its rate proves.
         */
        bookAndCompare(city, "Term",   10_000, 20, 1000,   true);
        bookAndCompare(city, "Term",   100_000, 20, 100000, false);

        /* ============ 9. A NOTE DELIVERS WHAT IT WAS ASKED FOR ============

           Found in play, 2026-09-06. Jerus: "when roads are issues with tbill,
           the tbill is inacted but the roads are not built and you are just
           left with the cash unspent."

           quoteTBill() takes a CASH amount - "how much paper do I need to raise
           this" - and it was inverting the discount only, so the underwriter's
           fee came out of the proceeds and the city banked less than it asked
           for. Rounding the face up to the next $1,000 covered the fee often
           enough that the failure looked random: the slack from rounding is at
           most one granule and the fee grows with the face, so the shortfall
           came and went with the size of the ask and became permanent above
           about $130,000 of face.

           Then the emergency path borrowed the exact shortfall, came up short,
           and buildStack() re-tested the price and refused - and the UI threw
           the refusal away. Debt on the books, nothing built.

           A sweep, because a single amount would have passed: $5k, $20k, $40k
           and $72k all worked BEFORE the fix.
           ================================================================== */
        System.out.println("\n--- a note raises what it was asked to raise ---");

        Game notes = new Game(GameFiles.scratch("creditcheck"));
        notes.getBuildingManager().initializeTemplates();

        int shortfalls = 0;
        for (int months : new int[]{3, 6, 12}) {
            for (double ask : new double[]{1_000, 5_000, 20_000, 40_000, 60_000,
                                           72_000, 100_000, 205_000, 500_000}) {
                DebtQuote q = notes.quoteTBill(ask, months, 1000.0);
                if (q.cashReceived() < ask) {
                    shortfalls++;
                    System.out.printf("   SHORT  %2dmo  asked $%,.0f  got $%,.2f%n",
                            months, ask, q.cashReceived());
                }
                assertTrue(String.format("a %dmo note for $%,.0f is not free money",
                                months, ask),
                        q.cashReceived() <= q.faceValue() + 1e-6);
            }
        }
        assertTrue("every note covers the cash it was quoted for", shortfalls == 0);

        /*
         * AND THE BONDS ARE DIFFERENT ON PURPOSE.
         *
         * A serial bond is issued at PAR: the face is what the player asked for
         * and the city receives par less fees, which is what issuing at par
         * means. A term bond is issued at a discount, its face solved so that
         * what it is WORTH at issue is what the player asked for, and the city
         * receives that less fees. Neither is sized to the cash that arrives.
         * Asserted here, for the serial, so nobody later "fixes" it to match
         * the note and quietly changes what the instrument is. (The build
         * screen's bond, 0.7.10, does ask the note's question of a term bond,
         * through a quote of its own - Game.quoteLongBondForCash(), held by
         * NewGameCheck section 11 - and quoteLongBond() is unchanged.)
         */
        DebtQuote serial = notes.quoteMediumBond(100_000, 10, 10000.0);
        assertTrue("a serial bond's face IS the request",
                serial.faceValue() >= 100_000 - 1e-6);
        assertTrue("...and the city receives par less fees, by design",
                serial.cashReceived() < serial.faceValue());

        /* ============ 10. THE STORY THAT BROKE, END TO END ============

           The unit test above would have caught the arithmetic. This catches
           the thing the player actually experienced, which is one step further
           on: borrow for a building you cannot afford, and END UP WITH THE
           BUILDING.
           ============================================================== */
        System.out.println("\n--- borrow for a road, and get the road ---");

        Game roadCity = new Game(GameFiles.scratch("creditcheck"));
        roadCity.newGame();
        BuildingManager yard = roadCity.getBuildingManager();

        BuildingsTemplate road = null;
        for (BuildingsTemplate t : yard.getTemplates()) {
            if (t.getName().equals("Paved Road")) road = t;
        }
        assertTrue("the Paved Road template exists", road != null);

        // Land, so the refusal under test is about MONEY and nothing else - a
        // road is a quarter of a million square feet and would otherwise fail
        // the land check first and prove nothing about funding.
        roadCity.getLandManager().setOwnedSqFt(
                roadCity.getLandManager().getOwnedSqFt() + road.getLandSqFt() * 100_000L);

        /*
         * BIG ENOUGH TO ACTUALLY BE SHORT.
         *
         * The first version of this ordered five, which a founding city can
         * simply afford out of its $300k - so it built them, the funding path
         * was never entered, and two of the assertions below were measuring a
         * city that had already built the roads twice. A fixture has to CAUSE
         * the condition it is testing, and the condition here is "cannot pay".
         *
         * ...AND THEN COUNTED AGAINST THE ENDOWMENT, WHICH IS NOT A CONSTANT.
         *
         * "Forty roads is about $540k against $300k of cash" was true when it
         * was written and stopped being true twice in one day: the founding
         * endowment went to $500M that afternoon and to $3.5B that night, when
         * rebalance stage two put every building on its real capital cost. Both
         * times this fixture went quietly back to being a city that could
         * simply afford the roads - the exact failure the paragraph above is
         * about, reintroduced by a number somewhere else.
         *
         * So the order is SIZED FROM THE CITY'S OWN CASH rather than written
         * down. Whatever the endowment is, this asks for half again as much
         * road as the city can pay for, and the assertion below still refuses
         * to go on if that somehow stops being true.
         */
        double unitPrice = roadCity.calculateTotalCost(road, 1);
        int howMany = unitPrice > 0
                ? (int) Math.ceil(roadCity.getCash() / unitPrice * 1.5) + 1
                : 40;
        double price = roadCity.calculateTotalCost(road, howMany);
        double cashBefore = roadCity.getCash();

        System.out.printf("   %d x Paved Road costs $%,.2f against $%,.2f of cash%n",
                howMany, price, cashBefore);
        System.out.printf("   short by $%,.2f%n", price - cashBefore);

        assertTrue("the fixture is actually short of the money - or this proves nothing",
                price > cashBefore);
        assertTrue("...and the city is told so rather than refused outright",
                roadCity.buildStack(road, howMany, false) == Game.BuildResult.NEEDS_FUNDING);
        assertTrue("nothing was built by the refusal", yard.getQuantity(road.getId()) == 0);

        double gap = price - roadCity.getCash();
        // The build screen's note, as its button books it (since 0.7.0 the
        // emergency path is the central bank's advance, and the note the
        // screen offers is an ordinary bill on the screen's own term).
        roadCity.handleTBillLogic(gap, Game.BUILD_NOTE_MONTHS, 1000.0);

        System.out.printf("   borrowed the $%,.2f gap; cash is now $%,.2f%n",
                gap, roadCity.getCash());

        assertTrue("the note actually covered the gap", roadCity.getCash() >= price - 1e-6);

        Game.BuildResult after = roadCity.buildStack(road, howMany, false);
        System.out.println("   second attempt: " + after);

        assertTrue("THE ROADS ARE BUILT", after == Game.BuildResult.SUCCESS);
        assertTrue("...all forty of them",
                yard.getQuantity(road.getId()) + yard.getUnderConstructionById()[road.getId()]
                        == howMany);
        assertTrue("...and the cash was actually spent, not left sitting",
                roadCity.getCash() < cashBefore + gap);
        assertTrue("...and there is a receipt for it", roadCity.hasNewReceipt());

        /* ============ 10. THE BAN IS ONE BAN ============

           Found by reading, 2026-09-06. A sector written down in a restructure
           is barred from shortfall loans for twelve months - and until today
           could still borrow to EXPAND, because the investment advisor's
           Investor.canBorrow() said yes to any positive amount. A sector that
           cannot borrow to keep the lights on could borrow to build a mall.

           The fixture: a city whose retail sector has a healthy appetite (jobs
           and people, few shops), then that sector is bankrupted by hand and
           restructured so the ban is live, with cash short of one store.
           ================================================================ */
        System.out.println("\n--- a borrowing ban also stops the investment advisor ---");

        Game banned = new Game(GameFiles.scratch("creditcheck"));
        banned.run();
        /* -------------------------------------------------------------------
           MORE PEOPLE THAN THE SHOPS CAN COVER, AND IT HAS TO STAY THAT WAY.

           Being broke and banned only bites if the advisor WANTS to build, and
           this fixture has now been patched three times to keep it wanting:
           300 houses became 800, $1,000 of cash became $10, and on 2026-09-07
           it went red again when rent became a market. Each patch bought a
           fixture that worked at one run length. Measured that day: twelve
           months printed "coverage ahead of demand", eighteen the same,
           twenty-four passed, thirty-six failed. A fixture whose verdict
           depends on how long you run it is not causing the condition under
           test, it is standing next to it.

           The reason is simple once seen: retail BUILDS. Give it thirty-six
           months of freedom and it puts up exactly the shops its city needs, so
           by the time the ban lands there is nothing it wants. Adding houses
           does not help, because retail adds shops to match; planRetail()
           forecasts on population plus growth, so doors alone cannot outrun it.

           So the fixture stops giving retail those months. It is funded and
           given ground, it grows into two and a half thousand houses, and after
           a short spell on the level it is banned and held broke for thirty
           months with the ban RENEWED EVERY MONTH - a lockout is finite and
           expires halfway through otherwise. Coverage then stands still while
           the city keeps growing, so demand is ahead of coverage by
           construction. It passes at a twelve, eighteen and twenty-four month
           opening spell rather than at exactly one of them, which is the
           difference that matters.

           The opening spell cannot be much shorter than twelve: the starting
           city already has a shop in the construction queue, and a retailer
           starved from month one leaves it half-built for ever - at six months
           the refusal that prints is "already building", which is a third
           reason and not the one under test.
           ------------------------------------------------------------------- */
        banned.getGovernmentInvestor().spend(-5_000_000);
        banned.getLandManager().setOwnedSqFt(200_000_000);
        banned.buildStack(template(banned, "House"), 2500, false);
        banned.buildStack(template(banned, "Industrial Bakery"), 3, false);
        banned.buildStack(template(banned, "Construction Depot"), 4, false);
        banned.buildStack(template(banned, "Coal Power Plant"), 1, false);
        // ...and somewhere to put a shop, or the refusal is about land and the
        // ban is still never reached.
        BusinessDebtManager ledger = banned.getEconomyManager().getBusinessDebtManager();
        EconomyManager econ = banned.getEconomyManager();
        String sector = Sectors.RETAIL;

        // Six months on the level, so any shop the starting city already had in
        // the queue actually opens. A retailer held broke from month one leaves
        // it half-built for ever and the refusal that prints is "already
        // building" rather than the ban.
        quietly(() -> banned.simulateMonths(12));

        // Owe a lot against nothing, and let the restructure fire.
        ledger.issueLoan(sector, 5_000_000, banned.getMonth());
        ledger.setAssets(sector, -1);
        ledger.restructure(sector);

        // ...and then held broke and banned for thirty months while the city
        // grows into its housing, so coverage cannot follow demand.
        quietly(() -> {
            for (int mm = 0; mm < 30; mm++) {
                econ.setSectorCash(sector, 10);
                ledger.setAssets(sector, -1);
                ledger.restructure(sector);
                banned.simulateMonths(1);
                econ.setSectorCash(sector, 10);
            }
        });
        ledger.setAssets(sector, -1);
        ledger.restructure(sector);
        assertTrue("fixture: retail is under a borrowing ban", ledger.isBorrowingBlocked(sector));

        /*
         * BROKE, and held broke, which is what the fixture is FOR.
         *
         * This was $1,000 and passed by luck: the advisor wanted a Convenience
         * Store, which costs $120, so a "broke" retailer could pay cash and the
         * ban never came up - the refusal that got printed was whatever branch
         * fired next, and on 2026-09-07 that became "coverage ahead of demand"
         * and the assertion failed. A fixture has to CAUSE the condition under
         * test, not stand next to it: retail is now poorer than the cheapest
         * thing it could want, every month, so the only way it builds is credit
         * and the only reason it cannot is the ban.
         */
        /*
         * ...AND SHORT OF SHOPS, caused the same way. Between the bans the
         * retailer builds whatever the city is short of - it did so in
         * this fixture the month a ban lapsed, three shops at once - and a
         * retailer that has just caught up with its customers holds for
         * "coverage ahead of demand", which is a third reason and not the
         * one under test. So its shops are sold down to one, and what it
         * wants is credit for the rest. (Until the sector template this was
         * accidental: the bank under construction counted as retail's order
         * in flight, and retail sat on "already building" for years.)
         */
        BuildingsTemplate shop = template(banned, "Convenience Store");
        int standing = banned.getBuildingManager().getQuantity(shop.getId());
        if (standing > 1) banned.getBuildingManager().retire(shop, standing - 1);

        int loansBefore = ledger.getLoanCount(sector), interimCountBefore = ledger.getInterimCount(sector);
        double principalBefore = ledger.getPrincipal(sector), interimBefore = ledger.getInterimPrincipal(sector);
        int shopsBefore = banned.getBuildingManager().getTotalStoreCoverage();
        int company = Equity.indexOf(sector);
        double raisedBefore = banned.getEquity().getLifetimeRaisedHome(company)
                + banned.getEquity().getLifetimeRaisedAbroad(company);

        /*
         * THE OWNERS ARE NOT THE LENDER. Since the share register (2026-09-10)
         * a sector with a plan asks its shareholders before its bank, and a
         * banned retailer that sells shares for its shops has broken no rule
         * - the ban is on CREDIT. So the fixture records what was raised and
         * lets the three passes go either way: shops paid for by an offering,
         * or a refusal in the ban's own words. What it can never be is a loan.
         */
        boolean refusedForCredit = false;
        for (int pass = 0; pass < 3; pass++) {
            econ.setSectorCash(sector, 10);
            quietly(() -> banned.simulateMonths(1));
            if (banned.getLastInvestment(sector).contains("borrowing ban")) refusedForCredit = true;
        }
        double raised = banned.getEquity().getLifetimeRaisedHome(company)
                + banned.getEquity().getLifetimeRaisedAbroad(company) - raisedBefore;

        System.out.println("   retail's advisor says: " + banned.getLastInvestment(sector));
        System.out.printf("   ...having raised $%,.0fk from its owners over the three passes%n", raised);
        // Fewer loans is fine - the written-down ones mature and settle. More
        // is the bug.
        // ...TO EXPAND: since 0.7.12 round 5 a banned sector whose month
        // leaves it short defaults and is lent the unpaid rest as interim
        // financing, which the ban does not bind (BusinessDebtManager,
        // INTERIM FINANCING) - a loan that pays bills it already owed, never
        // one that builds. What the ban forbids is the rest.
        System.out.printf("   ...and was lent $%,.1fk in the interim over the three passes%n",
                ledger.getInterimPrincipal(sector) - interimBefore);
        assertTrue("a banned sector takes no new loan to expand",
                ledger.getLoanCount(sector) - ledger.getInterimCount(sector) <= loansBefore - interimCountBefore
                        && ledger.getPrincipal(sector) - ledger.getInterimPrincipal(sector) <= principalBefore - interimBefore + 1e-6);
        assertTrue("...and either its owners paid for the shops, or the refusal says why in the ban's own words",
                raised > 0 || refusedForCredit);
        // Retiring idle shops is allowed (that is what a broke sector does);
        // opening new ones on credit is not.
        assertTrue("...and nothing was built on credit it did not have",
                raised > 0 || banned.getBuildingManager().getTotalStoreCoverage() <= shopsBefore);

        theCurve(city);
        nothingPastTheDefaultPoint();
        cantPayMeansDefault();
        creditLinesStayOpen();
        firstReading();
        shortfallBondGrossedUp();

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ============ 13. CAN'T PAY MEANS DEFAULT (0.7.12, round 4) ============

       Jerus: "Can't pay means default." The cash-flow test of the Bankruptcy
       and Insolvency Act, s. 2 (a) and (b), beside the balance-sheet test the
       model had: a till short with no lender behind it defaults that month.
       The fixtures cause it - a sector the default point refuses, whose till
       the month leaves short - and one the desk lends to, which does not. The
       share that defaults is the part that cannot pay, the shortfall over
       what the month asked the sector to pay, or the curve's own share at its
       leverage where that is larger - one slice, never both - each
       instrument at its own recovery; the overdraft is closed as the backstop
       closes one, and no ban or record goes with a slice. The same in play,
       with a sector that sells its bonds first and the audit closing, is
       BondCheck's section 5c.
       ============================================================ */
    static BusinessDebtManager.BondBook bondsOf(String s, double[] face) {
        return new BusinessDebtManager.BondBook() {
            @Override public double principal(String sector) { return sector.equals(s) ? face[0] : 0; }
            @Override public double monthlyCoupon(String sector) { return 0; }
            @Override public double writeDown(String sector, double scale) {
                if (!sector.equals(s)) return 0;
                double gone = face[0] * (1 - scale);
                face[0] *= scale;
                return gone;
            }
        };
    }

    static void cantPayMeansDefault() {
        System.out.println("\n--- 13. can't pay means default, and what is still unpaid is lent as interim financing, ranked first ---");
        double T = BusinessDebtManager.INSOLVENCY_TRIGGER;
        double loanLoss = BusinessDebtManager.LOAN_LOSS_GIVEN_DEFAULT, bondLoss = BusinessDebtManager.BOND_LOSS_GIVEN_DEFAULT;
        double gross = 1 / (1 - Bank.LOAN_FEE);

        // $1,200 of loans and $400 of bonds on $1,000 of assets, its quarter
        // there too: 1.6 times what it owns, past the line. The month asked it
        // for $400 and left its till $100 short.
        BusinessDebtManager m = new BusinessDebtManager();
        m.setPrimeRate(.05);
        double[] bonds = { 400 };
        m.setBondMarket(bondsOf(IND, bonds), null);
        m.issueLoan(IND, 1_200, 1);
        m.setAssets(IND, 1_000);
        m.setCash(IND, 0);
        for (int q = 0; q < BusinessDebtManager.STATEMENT_MONTHS; q++) m.recordStatement(IND, 1_600, 1_000);
        m.updateRates();
        m.processMonth();
        double lent = m.coverShortfall(IND, -100, 0, 2);
        m.setCash(IND, -100);
        m.setMonthObligations(IND, 400);
        double curve = BusinessDebtManager.monthlyDefaultShare(1.6);
        double after = 1_200 * (1 - .25 * loanLoss) + 400 * (1 - .25 * bondLoss);
        assertTrue("fixture: the sector is past the line, and the desk lends it nothing", m.getQuarterLeverage(IND) > T && lent == 0);
        assertTrue("fixture: the part that cannot pay is more than the curve's share at its leverage", .25 > curve);
        assertTrue("fixture: once that part of its debt is written off it reads under the line on its quarter, the loan counted",
                after / 1_000 < T && after + 100 * gross < (1_000 + 100) * T);
        m.restructureInsolventSectors();
        check("a sector refused credit whose till is short defaults that month, $100 of its bills unpaid",
                m.getCannotPayShort(IND), 100);
        check("...the part that cannot pay: what it was short over what the month asked of it",
                m.getCannotPayShare(IND), 100.0 / 400);
        check("...and its debt is sliced at that share, the larger reading", m.getDefaultShareThisMonth(IND), .25);
        check("recoveries by instrument: the loans lose a loan's loss on the share", m.getWrittenOffThisMonth(IND),
                1_200 * .25 * loanLoss);
        check("...the bonds a bond's", m.getBondWrittenOffThisMonth(IND), 400 * .25 * bondLoss);
        check("the unpaid rest is lent as an interim loan, grossed up for its fee", m.getInterimLentThisMonth(IND), 100 * gross);
        check("...an interim loan, which a save will carry by its own type", m.getInterimCount(IND), 1);
        check("...owed beside what is left of the rest", m.getPrincipal(IND), after + 100 * gross);
        check("...its till ends the month at nothing, not short", m.getCash(IND), 0);
        check("...handed exactly what was unpaid", m.takeInterimHanded(IND), 100);
        check("...and nothing is forgiven", m.takeOverdraftForgiven(IND), 0);
        double interimRate = 0;
        for (BusinessDebt d : m.getLoans()) if (d instanceof InterimLoan) interimRate = d.getAnnualRate();
        assertTrue("...priced by the bank's own rule at its rank: cheaper than the sector's loans, nothing ranking ahead of it",
                interimRate > 0 && interimRate < m.getRate(IND));
        assertTrue("...counted as the default point's refusal", m.getCannotPayReason(IND) == BusinessDebtManager.ShortReason.PAST_DEFAULT_POINT);
        assertTrue("...and a slice is not a restructure: no ban, no record",
                !m.isBorrowingBlocked(IND) && m.getRestructureCount(IND) == 0 && !m.wasRestructuredThisMonth(IND));

        // ITS RANK: the next month a slice takes the other debt's share and
        // leaves the interim loan whole.
        double interimOwed = m.getInterimPrincipal(IND);
        double loansBefore = m.getLoanPrincipal(IND) - interimOwed;
        m.setCash(IND, 0);
        m.setAssets(IND, 900);
        m.restructureInsolventSectors();
        double share = m.getDefaultShareThisMonth(IND);
        assertTrue("fixture: a later month's slice", share > 0 && !m.wasRestructuredThisMonth(IND));
        check("in a later slice the interim loan loses nothing: it ranks ahead of all the sector's other debt",
                m.getInterimPrincipal(IND), interimOwed);
        check("...while the loans lose their own share", m.getLoanPrincipal(IND) - m.getInterimPrincipal(IND),
                loansBefore * (1 - share * loanLoss));

        // The same sector a dollar short: the curve's share is the larger -
        // and after it the sector still reads past the line, so nobody lends.
        BusinessDebtManager n = new BusinessDebtManager();
        n.setPrimeRate(.05);
        n.issueLoan(IND, 1_600, 1);
        n.setAssets(IND, 1_000);
        n.setCash(IND, 0);
        for (int q = 0; q < BusinessDebtManager.STATEMENT_MONTHS; q++) n.recordStatement(IND, 1_600, 1_000);
        n.updateRates();
        n.processMonth();
        n.coverShortfall(IND, -1, 0, 2);
        n.setCash(IND, -1);
        n.setMonthObligations(IND, 400);
        n.restructureInsolventSectors();
        check("a sector a dollar short is sliced at the curve's share, the larger: one slice, never the two added",
                n.getDefaultShareThisMonth(IND), curve);
        assertTrue("fixture: after the curve's slice it still reads past the line on its quarter",
                1_600 * (1 - curve * loanLoss) > 1_000 * T);
        assertTrue("a sector still past the line after the write-down is lent nothing more",
                BusinessDebtManager.INTERIM_PAST_LINE.equals(n.getInterimRefusal(IND)) && n.getInterimCount(IND) == 0);
        assertTrue("...and goes to the backstop, the whole sector: banned, and on its record",
                n.wasRestructuredThisMonth(IND) && n.isBorrowingBlocked(IND) && n.getRestructureCount(IND) == 1);
        check("...written down to RESTRUCTURE_TARGET of what it owns", n.getPrincipal(IND),
                1_000 * BusinessDebtManager.RESTRUCTURE_TARGET);
        check("...its overdraft closed as the backstop closes one: forgiven", n.takeOverdraftForgiven(IND), 1);
        check("...its till at nothing", n.getCash(IND), 0);

        // ITS RANK IN THE BACKSTOP: kept first out of what the backstop keeps.
        BusinessDebtManager k = new BusinessDebtManager();
        k.setPrimeRate(.05);
        k.issueLoan(IND, 1_500, 1);
        k.getLoans().add(new InterimLoan(IND, 200, BusinessDebtManager.LOAN_TERM_MONTHS, 1, .05));
        k.setAssets(IND, 1_000);
        k.setCash(IND, 0);
        for (int q = 0; q < BusinessDebtManager.STATEMENT_MONTHS; q++) k.recordStatement(IND, 1_700, 1_000);
        k.updateRates();
        k.processMonth();
        k.coverShortfall(IND, -100, 0, 2);
        k.setCash(IND, -100);
        k.setMonthObligations(IND, 1_000);
        k.restructureInsolventSectors();
        assertTrue("fixture: it owes an interim loan, and still past the line after the month's slice it goes to the backstop",
                k.wasRestructuredThisMonth(IND) && k.getInterimRefusal(IND) != null);
        double kept = 1_000 * BusinessDebtManager.RESTRUCTURE_TARGET;
        check("in the backstop the interim loan is kept first, whole: written down only after the sector's other debt",
                k.getInterimPrincipal(IND), 200);
        check("...the loans keep what is left of the target", k.getLoanPrincipal(IND) - k.getInterimPrincipal(IND), kept - 200);
        check("...and the backstop of a sector with nothing left keeps nothing: the interim loan goes last, with the rest",
                nothingLeft(), 0);

        // A sector the desk lends to: $100 on $1,000, short $100.
        BusinessDebtManager ok = new BusinessDebtManager();
        ok.setPrimeRate(.05);
        ok.issueLoan(IND, 100, 1);
        ok.setAssets(IND, 1_000);
        ok.setCash(IND, 0);
        ok.updateRates();
        ok.processMonth();
        double lentOk = ok.coverShortfall(IND, -100, 0, 2);
        double tillOk = -100 + lentOk - BusinessDebtManager.feeOn(lentOk);
        ok.setCash(IND, tillOk);
        ok.setMonthObligations(IND, 400);
        ok.restructureInsolventSectors();
        assertTrue("a sector that can borrow is lent what it is short, and more", lentOk > 0 && tillOk >= 0);
        check("...and does not default for want of cash", ok.getCannotPayShort(IND), 0);
        check("...nor is lent anything in the interim", ok.getInterimLentThisMonth(IND), 0);
        check("...its slice only the curve's at its leverage", ok.getDefaultShareThisMonth(IND),
                BusinessDebtManager.monthlyDefaultShare((100 + lentOk) / 1_000));

        // Banned after a restructure, owing nothing, short $50: it defaults,
        // counted as the ban's - and the interim lender, whom the ban does
        // not bind, lends it the $50.
        BusinessDebtManager b = new BusinessDebtManager();
        b.setPrimeRate(.05);
        java.util.Map<String, Integer> record = new java.util.HashMap<>(), blocked = new java.util.HashMap<>();
        record.put(IND, 1);
        blocked.put(IND, 12);
        b.restoreCreditRecord(record, blocked);
        b.setAssets(IND, 500);
        b.setCash(IND, 0);
        b.processMonth();
        b.coverShortfall(IND, -50, 0, 2);
        b.setCash(IND, -50);
        b.setMonthObligations(IND, 200);
        b.restructureInsolventSectors();
        assertTrue("fixture: banned", b.isBorrowingBlocked(IND));
        check("a banned sector short of cash defaults too", b.getCannotPayShort(IND), 50);
        assertTrue("...counted as the ban's", b.getCannotPayReason(IND) == BusinessDebtManager.ShortReason.BANNED);
        check("...owing nothing, nothing is sliced", b.getWrittenOffThisMonth(IND), 0);
        check("...and the unpaid $50 is lent in the interim: the ban binds the desks that lend to grow, not the interim lender",
                b.getInterimLentThisMonth(IND), 50 * gross);
        check("...nothing forgiven", b.takeOverdraftForgiven(IND), 0);
        assertTrue("...nothing new on its record, still banned", b.getRestructureCount(IND) == 1 && b.isBorrowingBlocked(IND));

        // ...and a banned sector nobody will lend to - its bank shut - is the
        // backstop's inside the episode its ban began: the overdraft closed,
        // nothing new written off or counted.
        BusinessDebtManager bs = new BusinessDebtManager();
        bs.setPrimeRate(.05);
        bs.restoreCreditRecord(record, blocked);
        bs.setAssets(IND, 500);
        bs.setCash(IND, 0);
        bs.setLendingOpen(false);
        bs.processMonth();
        bs.coverShortfall(IND, -50, 0, 2);
        bs.setCash(IND, -50);
        bs.setMonthObligations(IND, 200);
        bs.restructureInsolventSectors();
        check("a banned sector refused the interim loan has its overdraft closed by the backstop", bs.getBackstopInBanThisMonth(IND), 50);
        assertTrue("...inside its episode: nothing new on its record", bs.getRestructureCount(IND) == 1 && !bs.wasRestructuredThisMonth(IND));

        // A bank shut: nobody lends, so a short sector goes to the backstop.
        BusinessDebtManager shut = new BusinessDebtManager();
        shut.setPrimeRate(.05);
        shut.issueLoan(IND, 400, 1);
        shut.setAssets(IND, 1_000);
        shut.setCash(IND, 0);
        shut.updateRates();
        shut.setLendingOpen(false);
        shut.processMonth();
        shut.coverShortfall(IND, -100, 0, 2);
        shut.setCash(IND, -100);
        shut.setMonthObligations(IND, 400);
        shut.restructureInsolventSectors();
        assertTrue("a sector whose bank is shut is refused, counted as the bank's",
                shut.getCannotPayReason(IND) == BusinessDebtManager.ShortReason.BANK_SHUT
                        && BusinessDebtManager.INTERIM_BANK_SHUT.equals(shut.getInterimRefusal(IND)));
        assertTrue("...and nobody lends in the interim either: the whole sector to the backstop",
                shut.wasRestructuredThisMonth(IND) && shut.getInterimCount(IND) == 0);
        check("...which closes its overdraft", shut.takeOverdraftForgiven(IND), 100);
    }

    /* ============ 15. A NEW SECTOR'S FIRST READING (0.7.12, round 7) ============

       A month-end at which a sector owned nothing and owed nothing is not a
       reading of a business: its quarter starts with its first month-end
       holding anything, and until then it reads as it stands. So a sector
       whose first plant arrives after months of nothing borrows its first
       bill rather than being read past the line on a quarter of nothing;
       and a sector that owned plant and crashed still reads the quarter it
       had. See BusinessDebtManager.recordStatement().

       ...AND IN A PLAYED CITY (round 8): plant handed over between months is
       on the sheet the lender reads at the next settle, because the sheets
       are valued before the lender reads them every month
       (EconomyManager.updateBusinessCredit()). So a sector whose first plant
       arrives that way pays its first bill with no default and no interim
       loan - round 7's two cases, RailCheck's railway ($930) and
       RestaurantsCheck's kitchens ($8.70).
       ============================================================ */
    static void firstReading() throws Exception {
        System.out.println("\n--- 15. a new sector's first reading: month-ends of nothing are not a quarter ---");
        double T = BusinessDebtManager.INSOLVENCY_TRIGGER;
        double gross = 1 / (1 - Bank.LOAN_FEE);

        // Three month-ends of a sector with nothing, then its first plant,
        // handed over between months, and its first bill: $50 short.
        BusinessDebtManager m = new BusinessDebtManager();
        m.setPrimeRate(.05);
        m.setAssets(IND, 0);
        m.setCash(IND, 0);
        for (int q = 0; q < BusinessDebtManager.STATEMENT_MONTHS; q++) m.recordStatement(IND, 0, 0);
        check("month-ends at which a sector owned and owed nothing file no reading", m.getStatementCount(IND), 0);
        m.setAssets(IND, 1_000);
        m.updateRates();
        m.processMonth();
        assertTrue("...so with its first plant it reads as it stands, under the line", !m.pastDefaultPoint(IND, -50));
        double lent = m.coverShortfall(IND, -50, 0, 2);
        m.setCash(IND, -50 + lent - BusinessDebtManager.feeOn(lent));
        m.setMonthObligations(IND, 50);
        m.restructureInsolventSectors();
        check("a sector whose first plant arrived after months of nothing borrows its first bill", lent, 50 * gross);
        check("...and does not default on it", m.getCannotPayShort(IND), 0);
        assertTrue("...nor is it banned", !m.isBorrowingBlocked(IND) && !m.wasRestructuredThisMonth(IND));
        m.recordStatement(IND, m.getPrincipal(IND), 1_000 + 50);
        check("its first month-end holding plant is the first reading of its quarter", m.getStatementCount(IND), 1);

        // A sector that owned plant and crashed: its readings are what it owed
        // and owned, and a crash is a reading of something.
        BusinessDebtManager c = new BusinessDebtManager();
        c.setPrimeRate(.05);
        c.issueLoan(IND, 1_200, 1);
        c.setAssets(IND, 100);
        c.setCash(IND, 0);
        c.recordStatement(IND, 1_200, 1_000);
        c.recordStatement(IND, 1_200, 1_000);
        c.recordStatement(IND, 1_200, 100);
        check("a sector that owned plant and crashed still reads its real quarter", c.quarterAssets(IND), 2_100.0 / 3);
        assertTrue("...past the line on it", c.pastDefaultPoint(IND, -10) && 1_200 > 2_100.0 / 3 * T);
        c.recordStatement(IND, 0, 0);
        check("...and a month-end of nothing after it leaves that quarter as it was", c.quarterAssets(IND), 2_100.0 / 3);
        check("...its readings unchanged", c.getStatementCount(IND), 3);

        /*
         * THE PLANT HANDED OVER BETWEEN MONTHS, in the town RestaurantsCheck
         * builds its kitchens in, three months played: a Diner bought for the
         * kitchens (a site, on the treasury, as that harness buys it) and two
         * Rail Spurs laid for the railway (RailCheck's lay()), neither sector
         * having owned or owed anything. Each one's first bill - the site's
         * property tax, the railway's first payroll - finds its till empty,
         * and is read at the next settle against the sheet the lender reads.
         */
        Game town = new Game(GameFiles.scratch("creditcheck"));
        quietly(() -> {
            town.run();
            town.getGovernmentInvestor().spend(-4_000_000);
            town.getLandManager().setOwnedSqFt(town.getLandManager().getOwnedSqFt() + 20_000_000L);
            LongPlaytest.build(town, "House", 60);
            LongPlaytest.build(town, "Convenience Store", 4);
            LongPlaytest.build(town, "Mixed Farm", 3);
            LongPlaytest.build(town, "Coal Power Plant", 1);
            LongPlaytest.build(town, "Water Treatment Plant", 1);
            LongPlaytest.build(town, "Paved Road", 6);
            town.simulateMonths(3);
        });
        BusinessDebtManager lender = town.getEconomyManager().getBusinessDebtManager();
        BuildingManager plant = town.getBuildingManager();
        String[] firsts = { Sectors.RESTAURANTS, Sectors.RAIL };
        for (String k : firsts) {
            assertTrue("fixture: " + k + " has owned nothing, owed nothing and filed no reading",
                    lender.getStatementCount(k) == 0 && lender.getPrincipal(k) == 0
                            && !(plant.getBuildingsValueBySector(k) > 0)
                            && !(town.getEconomyManager().getSectorCash(k) > 0));
        }
        quietly(() -> {
            LongPlaytest.build(town, "Diner", 1);
            plant.addStack(template(town, "Rail Spur"), 2, true);
            town.simulateMonths(1);
        });
        for (String k : firsts) {
            assertTrue("fixture: " + k + "'s first plant arrived between months, and its first month billed its empty till",
                    plant.getBuildingsValueBySector(k) > 0 && lender.getMonthObligations(k) > 0);
            assertTrue("a sector whose first plant was handed over between months borrows its first bill (" + k + ")",
                    lender.getShortfallLentThisMonth(k) > 0);
            assertTrue("...with no default on it (" + k + ")",
                    lender.getCannotPayShort(k) == 0 && lender.getCannotPayReason(k) == null);
            assertTrue("...and with no interim loan (" + k + ")", lender.getInterimCount(k) == 0);
            assertTrue("...nor a ban (" + k + ")", !lender.isBorrowingBlocked(k) && lender.getRestructureCount(k) == 0);
        }
    }

    /* ============ 16. THE SHORTFALL DESK'S BOND, GROSSED UP (0.7.12, round 7) ============

       A bond hands the till its face less its costs. The shortfall desk now
       lends what the bond's proceeds left of the shortfall, grossed for the
       loan's fee, as a building's loan has since round 5: the till is handed
       what it was short, and the bond's costs ride in the loan's face. A
       desk that answers on its own terms: a bond of up to `bondPart` at 4%,
       costing COST to issue, the bank for the rest.
       ============================================================ */
    static BusinessDebtManager.BondDesk deskOf(double bondPart, double cost) {
        return new BusinessDebtManager.BondDesk() {
            @Override public BusinessDebtManager.Plan plan(String sector, double amount, double loanRate, double loanRoom,
                                                           double bondRoom, double extraAssets, int month) {
                double bond = Math.min(Math.min(amount, loanRoom), bondPart);
                double loan = Math.max(0, Math.min(amount, loanRoom) - bond);
                return new BusinessDebtManager.Plan(sector, amount, bond, .04, .045, loan, loanRate, loanRate,
                        cost, .04, extraAssets, bond + loan, bond);
            }
            @Override public double issue(BusinessDebtManager.Plan plan, int month) {
                return plan.bondFace() - plan.costs();
            }
        };
    }

    static void shortfallBondGrossedUp() {
        System.out.println("\n--- 16. the shortfall desk's bond, grossed up: the till is handed what it was short ---");
        double fee = Bank.LOAN_FEE, cost = 12;
        for (double part : new double[] { 300, Double.POSITIVE_INFINITY }) {
            BusinessDebtManager m = new BusinessDebtManager();
            m.setPrimeRate(.05);
            m.setBondMarket(null, deskOf(part, cost));
            m.setAssets(IND, 10_000);
            m.setCash(IND, 0);
            m.updateRates();
            m.processMonth();
            double lent = m.coverShortfall(IND, -500, 0, 2);
            double bond = m.getShortfallPlan(IND).bondFace();
            double handed = lent - BusinessDebtManager.feeOn(lent) + m.takeBondProceeds(IND);
            String which = Double.isInfinite(part) ? "a bond that raised it all" : "a bond and a loan";
            assertTrue("fixture (" + which + "): the desk sold a bond costing " + cost, bond > 0);
            check("a shortfall covered by " + which + " leaves the till at what it was short", handed, 500);
            check("...the bond's costs carried in the loan's face", lent, (500 - (bond - cost)) / (1 - fee));
        }
    }

    /** A sector owing $1,000 of loans and a $200 interim loan with nothing left: what of the interim loan the backstop keeps. */
    static double nothingLeft() {
        BusinessDebtManager z = new BusinessDebtManager();
        z.setPrimeRate(.05);
        z.issueLoan(IND, 1_000, 1);
        z.getLoans().add(new InterimLoan(IND, 200, BusinessDebtManager.LOAN_TERM_MONTHS, 1, .05));
        z.setAssets(IND, -1);
        z.setCash(IND, 0);
        z.updateRates();
        z.restructureInsolventSectors();
        return z.wasRestructuredThisMonth(IND) ? z.getInterimPrincipal(IND) : Double.NaN;
    }

    static void creditLinesStayOpen() {
        System.out.println("\n--- 14. credit lines stay open: a rationing bank covers a short month and refuses growth ---");
        String OWES = Sectors.MINING, NOTHING = Sectors.RETAIL, OVER = Sectors.MATERIALS, PAST = Sectors.AUTOMOTIVE;
        double gross = 1 / (1 - Bank.LOAN_FEE);
        BusinessDebtManager r = new BusinessDebtManager();
        r.setPrimeRate(.05);
        r.issueLoan(OWES, 100_000, 1);
        r.setAssets(OWES, 1_000_000);
        r.setAssets(NOTHING, 500_000);
        r.issueLoan(OVER, 950_000, 1);
        r.setAssets(OVER, 1_000_000);
        r.issueLoan(PAST, 1_600, 1);
        r.setAssets(PAST, 1_000);
        for (String s : new String[] { OWES, NOTHING, OVER, PAST }) r.setCash(s, 0);
        for (int q = 0; q < BusinessDebtManager.STATEMENT_MONTHS; q++) r.recordStatement(PAST, 1_600, 1_000);
        r.updateRates();
        double g = .001;
        r.setCapitalRule(g, false);
        r.processMonth();
        assertTrue("fixture: the bank is short of capital - between its minimum and its target, rationing",
                r.capitalRuleOn() && !r.isKeepGoingOnly() && r.getCapitalGrowth() == g);
        check("fixture: the rule would let the sector owing $100,000 borrow $100 more", r.capitalRoom(OWES), g * 100_000);
        check("...and the sector owing nothing, nothing", r.capitalRoom(NOTHING), 0);
        double a = r.coverShortfall(OWES, -50_000, 0, 2);
        check("a rationing bank covers a healthy sector's short month whole, its fee on top: a working-capital line",
                a, 50_000 * gross);
        check("...counted as the line lent while the bank rationed", r.getLineLentRationed(OWES), 50_000 * gross);
        check("...all but $100 of it past what the 0.7.8 rule would have lent", r.getLineLentPastOldRule(OWES), 50_000 * gross - g * 100_000);
        double z = r.coverShortfall(NOTHING, -10_000, 0, 2);
        check("...a sector that owes nothing too, whose room under the rule is none", z, 10_000 * gross);
        assertTrue("...and refuses its building's loan: that is growth", !r.canFundProject(NOTHING, 1_000));
        assertTrue("...counted at that door", r.wasProjectRefusedForCapital(NOTHING) && r.wasRefusedForCapital(NOTHING));
        double reserve = r.getMonthlyInterest(OVER) / (1 - Bank.LOAN_FEE);
        double o = r.coverShortfall(OVER, -50_000, 0, 2);
        assertTrue("fixture: a sector over the ceiling, under the line", 950_000.0 / 1_000_000 > BusinessDebtManager.MAX_LOAN_TO_ASSETS
                && 950_000.0 / 1_000_000 < BusinessDebtManager.INSOLVENCY_TRIGGER);
        check("a sector over the ceiling is refused all but its interest reserve", o, reserve);
        double p = r.coverShortfall(PAST, -100, 0, 2);
        check("...and one past the line is refused everything", p, 0);
        r.setCash(OWES, 50_000 * gross - BusinessDebtManager.feeOn(50_000 * gross) - 50_000);
        r.setCash(NOTHING, 0);
        r.setCash(OVER, -50_000 + o - BusinessDebtManager.feeOn(o));
        r.setCash(PAST, -100);
        r.setMonthObligations(OVER, 200_000);
        r.setMonthObligations(PAST, 400);
        r.restructureInsolventSectors();
        check("the lines the bank honoured leave nobody to default: the sector that owed", r.getCannotPayShort(OWES), 0);
        check("...and the one that owed nothing", r.getCannotPayShort(NOTHING), 0);
        assertTrue("the sector over the ceiling defaults for want of cash, on its own credit",
                r.getCannotPayShort(OVER) > 0 && r.getCannotPayReason(OVER) == BusinessDebtManager.ShortReason.CEILING);
        assertTrue("...and so does the one past the line",
                r.getCannotPayShort(PAST) > 0 && r.getCannotPayReason(PAST) == BusinessDebtManager.ShortReason.PAST_DEFAULT_POINT);

        // Under its minimum: the line is still honoured.
        BusinessDebtManager u = new BusinessDebtManager();
        u.setPrimeRate(.05);
        u.issueLoan(OWES, 100_000, 1);
        u.setAssets(OWES, 1_000_000);
        u.setCash(OWES, 0);
        u.updateRates();
        u.setCapitalRule(0, true);
        u.processMonth();
        check("a bank under its minimum honours the line too", u.coverShortfall(OWES, -50_000, 0, 2), 50_000 * gross);
        assertTrue("...and funds no building, however small", !u.canFundProject(OWES, 1));
    }

    /* ============ 12. NOTHING PAST THE DEFAULT POINT (0.7.12, round 2) ============

       Jerus: "Stop at the default point." No shortfall loan, no interest
       reserve, no rollover of what fell due, no renewal and no project loan
       to a sector past INSOLVENCY_TRIGGER as its sheet stands when the loan is
       written, or once lent. The fixtures cause the condition the rule is
       for: a sector the month's opening refresh reads UNDER the line, which a
       loan falling due takes past it - where the old ceilings, reading the
       refresh, still lent it its interest - and one the same loan leaves
       under it. Then a played city, to follow what the firm does instead:
       its till stays short, its debt is sliced at the curve's rate, and when
       the overdraft outweighs its plant the backstop forgives it and bans it,
       every month's money audit closing.
       ============================================================ */
    static void nothingPastTheDefaultPoint() throws Exception {
        System.out.println("\n--- 12. nothing past the default point: the desks lend nothing past INSOLVENCY_TRIGGER ---");
        double T = BusinessDebtManager.INSOLVENCY_TRIGGER;

        // A sector with no quarter yet - a new one, or one a backstop has just
        // restarted - reads as it stands (round 3): these first fixtures file
        // no reading.
        // A sector owing 1.4 times what the month's refresh read, 0.4 of it
        // falling due now; its till empty, so paying it leaves it 0.4 short.
        double[] maturing = { 400, 150 };
        String[] name = { "past", "under" };
        double[] refusedFor = new double[2], lentTo = new double[2];
        for (int k = 0; k < 2; k++) {
            BusinessDebtManager m = new BusinessDebtManager();
            m.setPrimeRate(.05);
            double longLoan = 1_400 - maturing[k] - (k == 1 ? 350 : 0);
            m.issueLoan(IND, longLoan, 1);
            m.getLoans().add(new BusinessLoan(IND, maturing[k], 1, 1, .06));
            m.setAssets(IND, 1_000);
            m.setCash(IND, 0);                       // the economy reports its sheet: the till is in it
            m.updateRates();
            assertTrue("fixture (" + name[k] + "): the month's refresh reads it under the line",
                    m.getLeverage(IND) <= T);
            m.processMonth();
            double due = m.takeMaturedPrincipal(IND);
            double till = 0 - due;
            double owes = m.getPrincipal(IND), assetsNow = m.assetsNow(IND, till);
            System.out.printf("   %s: owes %,.0f against %,.0f once the %,.0f fell due - %.2f times%n",
                    name[k], owes, assetsNow, due, owes / assetsNow);
            double oldRoom = m.borrowingRoom(IND);
            double interest = m.getMonthlyInterest(IND);
            lentTo[k] = m.coverShortfall(IND, till, 0, 2);
            refusedFor[k] = m.getRefusedAtDefaultPoint(IND);
            if (k == 0) {
                assertTrue("fixture: paying what fell due takes it past INSOLVENCY_TRIGGER",
                        owes > assetsNow * T && m.pastDefaultPoint(IND, till));
                assertTrue("...where the old ceilings, reading the refresh, had room: the interest reserve",
                        oldRoom <= 0 && interest > 0 && refusedFor[k] > 0);
                check("a sector past the line is refused the shortfall loan", lentTo[k], 0);
                check("...so nothing is written", m.getPrincipal(IND), owes);
                /*
                 * "AFTER THE LOAN" ALONE WOULD HAVE LENT IT: the loan's cash
                 * lands on its sheet too, and the whole hole filled leaves it
                 * under the line on paper. The rule reads the sheet before
                 * the loan as well - see BusinessDebtManager, NOTHING PAST THE
                 * DEFAULT POINT.
                 */
                double whole = -till / (1 - Bank.LOAN_FEE);
                assertTrue("...though filling the whole hole would leave it under the line on paper",
                        (owes + whole) <= (assetsNow + whole - BusinessDebtManager.feeOn(whole)) * T);
                // The investment desk: a project's loan covers the till's
                // overdraft first, so it is a shortfall loan in substance.
                m.setAssets(IND, assetsNow);
                m.setCash(IND, till);
                assertTrue("the investment desk refuses it too, whatever the building would add",
                        !m.canFundProject(IND, 5_000) && m.wasRefusedAtDefaultPoint(IND)
                                && m.projectLoanRoom(IND, 5_000) == 0 && m.projectBondRoom(IND, 5_000) == 0);
                assertTrue("...where the test after the deal alone would have funded it",
                        owes + 5_000 <= (assetsNow + 5_000) * T);
            } else {
                assertTrue("fixture: one whose maturity leaves it under the line", owes <= assetsNow * T);
                assertTrue("a sector under the line is lent", lentTo[k] > 0);
                check("...nothing refused", refusedFor[k], 0);
                double handed = lentTo[k] - BusinessDebtManager.feeOn(lentTo[k]);
                assertTrue("...and the loan leaves it under the line",
                        m.getPrincipal(IND) <= (assetsNow + handed) * T);
            }
        }

        // A landlord's mortgage at the end of its term: renewed under the
        // line, fallen due whole past it.
        String RE = Sectors.REAL_ESTATE;
        for (boolean past : new boolean[] { false, true }) {
            BusinessDebtManager m = new BusinessDebtManager();
            m.setInsuredMortgageRate(.04);
            Mortgage mort = m.issueMortgage(RE, 200_000, 0);
            for (int k = 0; k < Mortgage.MORTGAGE_TERM_MONTHS - 1; k++) { m.processMonth(); m.takeMaturedPrincipal(RE); }
            double owedAtTheEnd = mort.getOutstandingPrincipal();
            m.setAssets(RE, past ? owedAtTheEnd / 1.6 : owedAtTheEnd / 1.2);
            m.setCash(RE, 0);
            m.processMonth();
            double due = m.takeMaturedPrincipal(RE);
            if (!past) {
                assertTrue("a landlord at 1.2 times what it owns renews its mortgage at the term's end",
                        m.getMortgageCount(RE) == 1 && m.getNotRenewedAtDefaultPoint(RE) == 0);
            } else {
                check("...one at 1.6 does not: the balance falls due whole with the term's last payment",
                        due, owedAtTheEnd);
                assertTrue("...and the mortgage is gone, counted against the default point",
                        m.getMortgageCount(RE) == 0 && m.getNotRenewedAtDefaultPoint(RE) > 0
                                && m.getNotRenewedAtDefaultPoint(RE) <= owedAtTheEnd);
            }
        }

        /*
         * ON THE QUARTER (round 3, Jerus: "Read the quarter"): a sector with
         * readings is read over its last STATEMENT_MONTHS of them, as its
         * price and its allowance are. Two sectors, the same loans falling due
         * the same way: one whose month, once it has paid, reads under the
         * line but whose quarter read it over; and one the other way round.
         */
        System.out.println("\n--- 12. ...on the quarter: the month under and the quarter over is refused, the other way round is lent ---");
        double[][] cases = { { 1_080, 1.6 }, { 1_440, 1.2 } };   // {the long loan, the quarter's leverage}
        String[] caseName = { "month under, quarter over", "month over, quarter under" };
        for (int k = 0; k < 2; k++) {
            BusinessDebtManager m = new BusinessDebtManager();
            m.setPrimeRate(.05);
            m.issueLoan(IND, cases[k][0], 1);
            m.getLoans().add(new BusinessLoan(IND, 100, 1, 1, .06));
            m.setAssets(IND, 1_000);
            m.setCash(IND, 0);
            for (int q = 0; q < BusinessDebtManager.STATEMENT_MONTHS; q++) m.recordStatement(IND, cases[k][1] * 1_000, 1_000);
            m.updateRates();
            m.processMonth();
            double due = m.takeMaturedPrincipal(IND);
            double till = -due;
            double month = m.getPrincipal(IND) / m.assetsNow(IND, till), quarter = m.getQuarterLeverage(IND);
            System.out.printf("   %s: %.2f times as it stands once the %,.0f fell due, %.2f over its quarter%n",
                    caseName[k], month, due, quarter);
            double lent = m.coverShortfall(IND, till, 0, 2);
            if (k == 0) {
                assertTrue("fixture: paying what fell due leaves it under the line as it stands, and its quarter reads it over",
                        month <= T && quarter > T);
                check("a sector whose month reads under the line but whose quarter reads it over is refused", lent, 0);
                assertTrue("...counted against the default point", m.getRefusedAtDefaultPoint(IND) > 0);
            } else {
                assertTrue("fixture: paying what fell due takes it past the line as it stands, and its quarter reads it under",
                        month > T && quarter <= T);
                assertTrue("a sector whose month reads over the line but whose quarter reads it under is lent, as its price reads it",
                        lent > 0 && m.getRefusedAtDefaultPoint(IND) == 0);
            }
        }

        /* ---- ...and in a played city: what the firm does instead ---- */
        System.out.println("\n--- 12. ...what a sector past the line does instead, in a played city ---");
        Game g = new Game(GameFiles.scratch("creditcheck"));
        quietly(() -> {
            g.run();
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.buildStack(template(g, "House"), 300, true);
            g.buildStack(template(g, "Convenience Store"), 12, true);
            g.buildStack(template(g, "Bakery"), 3, true);
            g.buildStack(template(g, "Construction Depot"), 3, true);
            g.buildStack(template(g, "Coal Power Plant"), 2, true);
            g.buildStack(template(g, "Water Treatment Plant"), 1, true);
            g.buildStack(template(g, "Commercial Bank"), 1, true);
            g.setAutoSubsidised(Sectors.INDUSTRY, true);
            g.simulateMonths(24);
        });
        BusinessDebtManager credit = g.getEconomyManager().getBusinessDebtManager();
        String X = Sectors.CONSTRUCTION;
        double till0 = g.getEconomyManager().getSectorCash(X);
        double plant = credit.getAssets(X) - credit.getCash(X);
        assertTrue("fixture: the city's builders have plant and a till, and a standing bank",
                plant > 0 && till0 > 0 && !g.getBank().isInsolvent() && g.getBank().getBranches() > 0);
        // Taken on between months, a claim, no money moved: a long loan of
        // 1.25 times its plant, and one of its till and a fifth of its plant
        // more that falls due at the next settle. The refresh reads it under
        // the line; paying what fell due takes it past. And its quarter
        // (round 3), filed between months as the bank's month-end reading
        // files it: three month-ends at what it owes now, 1.6 times what it
        // owned - past the line.
        double longClaim = 1.25 * plant, dueClaim = till0 + .2 * plant;
        credit.issueLoan(X, longClaim, g.getMonth());
        credit.getLoans().add(new BusinessLoan(X, dueClaim, 1, g.getMonth(), .06));
        for (int q = 0; q < BusinessDebtManager.STATEMENT_MONTHS; q++) {
            credit.recordStatement(X, credit.getPrincipal(X), credit.getPrincipal(X) / 1.6);
        }
        assertTrue("fixture: the bank's quarter reads the builders past the line", credit.getQuarterLeverage(X) > T);
        quietly(g::toggleNextMonth);
        MoneyAudit.Result r1 = g.getLastMoneyAudit();
        SectorBooks.SectorMonth s1 = g.getSectorBooks().get(X);
        System.out.printf("   the builders: %,.0fk fell due, %,.0fk refused, borrowed %,.0fk; %,.0fk of their debt defaulted"
                        + " this month; interim %,.0fk, refused: %s, backstop %b; till %,.0fk%n", s1.repaid(),
                credit.getRefusedAtDefaultPoint(X), s1.borrowed(), credit.getDefaultedThisMonth(X),
                credit.getInterimLentThisMonth(X), credit.getInterimRefusal(X), credit.wasRestructuredThisMonth(X), s1.cash());
        assertTrue("the shortfall desk refused the builders once paying what fell due took them past the line",
                credit.getRefusedAtDefaultPoint(X) > 0 && s1.borrowed() == 0 && s1.repaid() >= dueClaim - 1e-6);
        assertTrue("...so their till went short: the overdraft", s1.openingCash() - s1.repaid() < 0);
        assertTrue("...and the month's slice wrote their debt down: at the curve's rate for its leverage, or since round 4 the part that could not pay, whichever is larger",
                credit.getDefaultedThisMonth(X) > 0);
        assertTrue("...what it could not pay defaulted that month, and the till ends it at nothing (round 4)",
                credit.getCannotPayShort(X) > 0 && s1.cash() >= 0);
        // Round 5 (Jerus: "If nobody will lend even then, the whole industry
        // goes to the existing full write-off"): read after the write-down
        // the builders are still past the line, so nobody lends them the
        // rest in the interim.
        assertTrue("...and still past the line after it, nobody lent them the rest: the whole sector went to the backstop (round 5)",
                BusinessDebtManager.INTERIM_PAST_LINE.equals(credit.getInterimRefusal(X)) && credit.wasRestructuredThisMonth(X)
                        && credit.getInterimLentThisMonth(X) == 0);
        assertTrue("...and the month's money audit closes", r1 != null && Math.abs(r1.residual) < .01);

        // Under water: an overdraft past everything it owns - twice its plant
        // since round 4. Once its assets were a thousand under nothing at the
        // month's top they stayed there to its defaults; with the month
        // before's overdraft closed and its debt sliced by the part that could
        // not pay, the month's own flows lifted them $2.2M, back over nothing
        // by its defaults, and the cash-flow test closed the overdraft before
        // the backstop could read it. The cause is asserted below. Since round
        // 5 the builders are the backstop's already, and inside its ban, so
        // the sector under water is the next with plant and no ban.
        String V = null;
        for (String s : Sectors.KEYS) {
            if (s.equals(X) || credit.isBorrowingBlocked(s)) continue;
            if (credit.getAssets(s) - credit.getCash(s) > 0 && (V == null
                    || credit.getAssets(s) - credit.getCash(s) > credit.getAssets(V) - credit.getCash(V))) V = s;
        }
        final String UW = V;
        assertTrue("fixture: a sector with plant, outside any ban: " + UW, UW != null);
        g.getEconomyManager().setSectorCash(UW, -2 * (credit.getAssets(UW) - credit.getCash(UW)) - 1_000);
        quietly(g::toggleNextMonth);
        assertTrue("fixture: the month's defaults read its assets at or below nothing", credit.getAssets(UW) <= 0);
        MoneyAudit.Result r2 = g.getLastMoneyAudit();
        System.out.printf("   under water (%s): written down whole %s, overdraft forgiven %,.0fk, banned %d months%n", UW,
                credit.wasRestructuredThisMonth(UW), g.getEconomyManager().getOverdraftForgivenThisMonth(UW),
                credit.getBlockedMonths(UW));
        assertTrue("with its overdraft past its plant the backstop writes it down whole",
                credit.wasRestructuredThisMonth(UW) && credit.getPrincipal(UW) < 1e-6);
        assertTrue("...forgives the overdraft and bans it",
                g.getEconomyManager().getOverdraftForgivenThisMonth(UW) > 0 && credit.isBorrowingBlocked(UW));
        assertTrue("...and that month's audit closes too", r2 != null && Math.abs(r2.residual) < .01);

        /*
         * THE PROJECT LOAN'S FEE (0.7.12, round 5; Jerus: "gross a project
         * loan up for its fee, as the shortfall desk's loan already is
         * (0.7.7). Every loan is then handed its full purpose"). A sector
         * outside any ban, its till at nothing, borrows for a building
         * through the investor the month's investment uses; small enough that
         * the bank lends it all, no bond.
         */
        String P = null;
        for (String s : Sectors.KEYS) {
            if (s.equals(X) || s.equals(UW) || credit.isBorrowingBlocked(s) || !(credit.getAssets(s) > 0)) continue;
            if (P == null || credit.getAssets(s) > credit.getAssets(P)) P = s;
        }
        final String PS = P;
        Investor builder = g.getSectorInvestor(PS);
        g.getEconomyManager().setSectorCash(PS, 0);
        double purpose = 50, owedBefore = credit.getPrincipal(PS), lentBefore = credit.getLentThisMonth();
        assertTrue("fixture: " + PS + " may borrow for a building", builder.canBorrow(purpose));
        builder.borrow(purpose, g.getMonth());
        double written = credit.getLentThisMonth() - lentBefore;
        assertTrue("fixture: the bank lent it all, no bond: what it owes more is what the bank wrote",
                written > 0 && Math.abs(written - (credit.getPrincipal(PS) - owedBefore)) < 1e-9);
        check("a project loan hands its full purpose to the till", g.getEconomyManager().getSectorCash(PS), purpose);
        check("...its fee carried in the principal: the loan is the purpose grossed up for LOAN_FEE",
                credit.getPrincipal(PS) - owedBefore, purpose / (1 - Bank.LOAN_FEE));
        check("...and the fee the bank keeps is LOAN_FEE of that principal", written * Bank.LOAN_FEE,
                purpose / (1 - Bank.LOAN_FEE) - purpose);
    }

    /* ============ 11. THE CURVE (0.7.1) ============

       Jerus: "the short term rates, aka the one you choose, those should be
       basically the tbill rate, the others change just as in real life." A
       debt-free market quotes the note at the dial, and every term above a
       year at the dial plus the table's premium for it - interpolated
       linearly from nothing at a year to the ten-year point, and between the
       table's points. A hike moves every maturity by the hike; debt moves
       every maturity by the same credit spread. A term loan is issued at
       10, 20, 30, 40 or 50 years and a 25-year ask is refused; and a bond
       issued at twenty years is bought back the same month for what it
       raised, bar the issuance cost - the issue and the buyback price off one
       curve. Asserted against the constants, not the numbers.
       ================================================ */
    static void theCurve(Game city) {
        System.out.println("\n--- 11. the curve: the dial at the short end, a premium by maturity ---");

        DebtManager bare = new DebtManager();
        bare.setPolicyRate(.05);
        bare.setGDP(9_068);
        bare.setTaxRevenue(1_916);
        bare.setCashPosition(0);
        bare.setCostOfFunds(0);
        bare.updateInterest();
        int[] rows = {6, 60, 120, 240, 360, 480, 600};
        double[] clean = new double[rows.length];
        for (int i = 0; i < rows.length; i++) clean[i] = bare.curveRate(rows[i]);

        check("debt-free, the note quotes exactly the dial", bare.curveRate(6), .05);
        check("...and getRate() is the note's rate, the short end", bare.getRate(), bare.curveRate(6));
        check("the 10-year term loan: the dial plus TERM_PREMIUM_10Y", bare.curveRate(120),
                .05 + DebtManager.TERM_PREMIUM_10Y);
        check("the 20-year: the dial plus TERM_PREMIUM_20Y", bare.curveRate(240),
                .05 + DebtManager.TERM_PREMIUM_20Y);
        check("the 50-year: the dial plus TERM_PREMIUM_50Y", bare.curveRate(600),
                .05 + DebtManager.TERM_PREMIUM_50Y);
        check("...and past fifty years the curve holds", bare.curveRate(720), bare.curveRate(600));
        check("a 5-year serial: the dial plus the 10-year entry, 48 of the 108 months from a year to ten",
                bare.curveRate(60), .05 + DebtManager.TERM_PREMIUM_10Y * (60 - 12) / (120.0 - 12));
        check("...a 35-year point halfway between 30 and 40", bare.curveRate(420),
                .05 + (DebtManager.TERM_PREMIUM_30Y + DebtManager.TERM_PREMIUM_40Y) / 2);
        check("...and a year-long note carries none", DebtManager.termPremium(12), 0);

        bare.setPolicyRate(.07);
        bare.updateInterest();
        boolean parallel = true;
        for (int i = 0; i < rows.length; i++) {
            if (Math.abs(bare.curveRate(rows[i]) - clean[i] - .02) > 1e-12) parallel = false;
        }
        assertTrue("a hike of two points moves every row by two: the whole curve in parallel", parallel);
        bare.setPolicyRate(.05);

        bare.addLongTermBond(300_000, 240, 1, .05);
        bare.updateInterest();
        double spread = bare.gdpSpread() + bare.revenueSpread();
        assertTrue("fixture: the debt adds a real credit spread", spread > 1e-4);
        boolean sameSpread = true;
        for (int i = 0; i < rows.length; i++) {
            if (Math.abs(bare.curveRate(rows[i]) - clean[i] - spread) > 1e-12) sameSpread = false;
        }
        assertTrue("debt moves every row by the same credit spread", sameSpread);

        // A term the treasury does not issue.
        int debts = city.getDebtManager().getDebt().size();
        double cash = city.getCash();
        String said = city.handleLongBondLogic(5_000, 25, 1000);
        assertTrue("a 25-year request is refused, in words", LongTermBond.REFUSAL.equals(said));
        assertTrue("...quoted nothing", city.quoteLongBond(5_000, 25, 1000).isEmpty());
        assertTrue("...and books nothing", city.getDebtManager().getDebt().size() == debts
                && city.getCash() == cash);
        boolean five = true;
        for (int years : LongTermBond.MATURITIES) {
            if (city.quoteLongBond(5_000, years, 1000).isEmpty()) five = false;
        }
        assertTrue("...while each of the five maturities is quoted", five);

        // Issued at twenty and bought straight back: the same curve both ways.
        DebtQuote quote = city.quoteLongBond(5_000, 20, 1000);
        quietly(() -> city.handleLongBondLogic(5_000, 20, 1000));
        Debt twenty = city.getDebtManager().getDebt().get(city.getDebtManager().getDebt().size() - 1);
        double price = city.quoteRepurchase(twenty);
        System.out.printf("   20-year: raised $%,.2fk on $%,.0fk of face at %.3f%%; bought back for $%,.2fk%n",
                quote.cashReceived(), quote.faceValue(), quote.marketRate() * 100, price);
        assertTrue("a bond issued at 20 years is bought back the same month for what it raised,"
                + " bar the issuance cost",
                Math.abs(price - (quote.cashReceived() + city.costOfIssuance(quote.faceValue()))) <= .01);
        double paid = city.repurchaseDebt(twenty);
        assertTrue("...and the buyback takes it off the books at that price",
                Math.abs(paid - price) < 1e-9 && !city.getDebtManager().getDebt().contains(twenty));
    }

    /**
     * True if this quote came off the sloped part of the curve.
     *
     * The rate is clamped into [0.5%, 20%]. Both clamps are places where two
     * different calculations produce the same number, so an assertion made
     * there proves nothing about either. Any check that turns on the RATE has
     * to establish it is not sitting on a clamp first.
     */
    static boolean onCurve(DebtQuote q) {
        return onCurve(q.marketRate());
    }

    static boolean onCurve(double rate) {
        // Bounds read off the market rather than typed in, so a change to the
        // band cannot quietly turn these assertions into "the cap equals the cap".
        DebtManager shape = new DebtManager();
        return rate > shape.floorRate() + 1e-4 && rate < shape.ceilingRate() - 1e-4;
    }

    /** Runs a stretch of the game without its per-month console output. */
    static void quietly(Runnable work) {
        java.io.PrintStream out = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        try { work.run(); } finally { System.setOut(out); }
    }

    static BuildingsTemplate template(Game game, String name) {
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException(name);
    }

    /**
     * Takes a quote, books it, and checks the books say what the quote said.
     *
     * Deliberately checks the DEBT OBJECT's own monthly interest rather than
     * re-deriving it from the rate: the long bond is the case that matters, and
     * it is booked at its coupon, not at the headline market rate. Comparing
     * against a number this method computed itself would let the two definitions
     * drift apart in exactly the place they are most likely to.
     */
    static void bookAndCompare(Game g, String type, double amount, int duration,
                               double rounding, boolean mustBeOnCurve) {

        DebtQuote quote = g.quoteDebt(type, amount, duration, rounding);

        // A quote pinned to the ceiling agrees with a booking pinned to the
        // ceiling no matter how badly the two calculations differ underneath.
        if (mustBeOnCurve) {
            assertTrue(type + ": priced on the curve, not against the cap", onCurve(quote));
        }

        double principalBefore = g.getDebtManager().getAllPrincipal();
        double cashBefore = g.getCash();
        int debtsBefore = g.getDebtManager().getDebt().size();

        switch (type) {
            case "Note"      -> g.handleTBillLogic(amount, duration, rounding);
            case "Serial" -> g.handleMediumBondLogic(amount, duration, rounding);
            default            -> g.handleLongBondLogic(amount, duration, rounding);
        }

        System.out.printf("   %-12s asked $%,.0fk -> quoted %.2f%%, face $%,.0fk, cash $%,.0fk%n",
                type, amount, quote.marketRate() * 100, quote.faceValue(), quote.cashReceived());

        check(type + ": principal rose by the quoted face value",
                g.getDebtManager().getAllPrincipal() - principalBefore, quote.faceValue());
        check(type + ": cash rose by the quoted proceeds",
                g.getCash() - cashBefore, quote.cashReceived());
        assertTrue(type + ": exactly one instrument was booked",
                g.getDebtManager().getDebt().size() == debtsBefore + 1);

        // The rate the paper was actually written at.
        Debt booked = g.getDebtManager().getDebt().get(g.getDebtManager().getDebt().size() - 1);
        check(type + ": booked at the quoted monthly interest",
                booked.getMonthlyInterestExpense(), quote.monthlyInterest());

        assertTrue(type + ": the city never receives more than it owes",
                quote.cashReceived() <= quote.faceValue() + 1e-6);
    }
}

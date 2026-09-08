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

    static final String IND = BusinessDebtManager.INDUSTRY;

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
        System.out.println("--- pricing: govt rate + spread, spread capped at 8pts ---");

        BusinessDebtManager m = new BusinessDebtManager();
        m.setRiskFreeRate(.01);
        m.setAssets(IND, 100000);
        m.updateRates();

        // No debt: the best credit a sector can have, still 1pt over government.
        check("no debt -> min spread", m.getSpread(IND), .01);
        check("no debt -> rate", m.getRate(IND), .02);

        // debt/assets = 0.5 -> spread = 1% + 6%*0.5 = 4%
        m.issueLoan(IND, 50000, 1);
        m.setAssets(IND, 100000);
        m.updateRates();
        check("leverage 0.5", m.getLeverage(IND), .5);
        check("leverage 0.5 -> spread", m.getSpread(IND), .04);
        check("leverage 0.5 -> rate", m.getRate(IND), .05);

        // Push leverage past the cap: spread must stop at 8pts, not keep climbing.
        m.issueLoan(IND, 150000, 1);
        m.setAssets(IND, 100000);
        m.updateRates();
        check("leverage now 2.0", m.getLeverage(IND), 2.0);
        check("spread capped", m.getSpread(IND), .08);
        check("rate capped at govt + 8", m.getRate(IND), .09);

        // The cap is on the SPREAD, so a higher government rate carries through.
        m.setRiskFreeRate(.20);
        m.updateRates();
        check("govt 20% -> business 28%", m.getRate(IND), .28);

        // Insolvent: owes money against non-positive assets. Worst case.
        m.setRiskFreeRate(.01);
        m.setAssets(IND, -5000);
        m.updateRates();
        check("negative assets -> spread at ceiling", m.getSpread(IND), .08);

        // An empty or insolvent business with NO debt is not a good credit
        // either. The first version returned the minimum spread here, which is
        // how the insolvent food industry came to borrow $49,611 at 2%.
        BusinessDebtManager broke = new BusinessDebtManager();
        broke.setRiskFreeRate(.01);
        broke.setAssets(IND, -22815);
        broke.updateRates();
        check("no debt but insolvent -> ceiling", broke.getSpread(IND), .08);
        check("...and the loan is written at that rate",
                broke.issueLoan(IND, 49611, 1).getAnnualRate(), .09);

        // A loan must be priced including itself, not off the balance sheet from
        // before it existed - otherwise every sector's first loan is the cheapest
        // one it will ever get, however large.
        BusinessDebtManager fresh = new BusinessDebtManager();
        fresh.setRiskFreeRate(.01);
        fresh.setAssets(IND, 100000);
        fresh.updateRates();
        check("quoted rate before borrowing", fresh.getRate(IND), .02);
        // borrowing 100,000 against 100,000 of assets is leverage 1.0 -> 7%
        check("but a big loan prices itself in",
                fresh.issueLoan(IND, 100000, 1).getAnnualRate(), .08);

        /* ==================== 2. rate is fixed at issue ==================== */
        System.out.println("\n--- a loan keeps the rate it was written at ---");

        BusinessDebtManager m2 = new BusinessDebtManager();
        m2.setRiskFreeRate(.01);
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
        m3.setRiskFreeRate(.01);
        m3.setAssets(IND, 50000);
        m3.updateRates();

        check("solvent sector borrows nothing", m3.coverShortfall(IND, 5000, 0, 1), 0);
        check("...and has no debt", m3.getPrincipal(IND), 0);

        // $1,000 overdrawn, losing $200/month -> 1000 + 3*200 = 1600
        double lent = m3.coverShortfall(IND, -1000, 200, 1);
        check("borrowed hole + buffer", lent, 1600);
        check("principal on the books", m3.getPrincipal(IND), 1600);
        check("one loan, not many", m3.getLoanCount(IND), 1);

        /* ==================== 4. maturity and rollover ==================== */
        System.out.println("\n--- 36-month term, then rollover ---");

        BusinessDebtManager m4 = new BusinessDebtManager();
        m4.setRiskFreeRate(.01);
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
        cash += m4.coverShortfall(IND, cash, 0, 37);
        check("refinanced back to zero", cash, 0);
        check("new loan on the books", m4.getPrincipal(IND), 11500);
        check("matures 36 months later", m4.getLoans(IND).get(0).getMaturityMonth(), 73);

        /* ==================== 5. cash conservation ==================== */
        System.out.println("\n--- interest must be charged exactly once ---");

        // The trap this design exists to avoid: interest is an income-statement
        // expense, so cash moves by net income. If the debt manager ALSO took it
        // out of cash, the sector would pay twice.
        BusinessDebtManager m5 = new BusinessDebtManager();
        m5.setRiskFreeRate(.01);
        m5.setAssets(IND, 100000);
        m5.updateRates();
        m5.issueLoan(IND, 24000, 1);

        double interest = m5.getMonthlyInterest(IND);
        assertTrue("there is interest to pay", interest > 0);

        IndustrialHandler ih = new IndustrialHandler();
        ih.setIndustrialCash(10000);
        ih.setFoodInventory(1000);
        ih.setFoodDemand(1000);
        ih.setFoodPrice(.50);          // revenue 500
        ih.setEnergyRatio(1);
        ih.setWaterRatio(1);
        ih.updateJobFillRate(new double[11]);
        ih.updateIndustrialWages(new double[11], new int[11]);
        ih.setInterestExpense(interest);
        ih.calculateIndustrialResults();

        check("operating income excludes interest", ih.getReportOperatingIncome(), 500);
        check("interest expensed", ih.getReportInterestExpense(), interest);
        check("pre-tax income is net of interest", ih.getNetIncome(), 500 - interest);
        check("cash moved by exactly that", ih.getIndustrialCash(), 10000 + 500 - interest);

        // processMonth must not touch anyone's cash
        double before = ih.getIndustrialCash();
        m5.processMonth();
        check("processMonth moved no cash", ih.getIndustrialCash(), before);

        /* ==================== 6. balance sheet integration ==================== */
        System.out.println("\n--- the loan shows up as a liability ---");

        ih.setBuildingsValue(20000);
        ih.setLandValue(0);
        ih.setBondsPayable(m5.getPrincipal(IND));
        BalanceSheet bs = ih.getBalanceSheet();

        check("loans payable", bs.getTotalLiabilities(), 24000);
        check("still balances", bs.getTotalAssets(), bs.getTotalLiabilitiesAndEquity());
        check("equity is now assets less debt", bs.getEquity(), bs.getTotalAssets() - 24000);

        /* ==================== 7. the spiral guard ==================== */
        System.out.println("\n--- a chronically loss-making sector, 60 months ---");

        // This is the case the 3-month buffer exists for. Without it the sector
        // writes a new loan every single month.
        BusinessDebtManager m6 = new BusinessDebtManager();
        m6.setRiskFreeRate(.01);
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
        assertTrue("rate stayed inside the cap", m6.getRate(IND) <= .01 + .08 + 1e-9);

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
        assertTrue("a debt-free city with no overdraft prices at the floor",
                Math.abs(clean - .01) < 1e-9);

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
        city.buildStack(template(city, "House"), 200, false);
        city.buildStack(template(city, "Convenience Store"), 5, false);
        city.buildStack(template(city, "Textile Mill"), 2, false);
        city.buildStack(template(city, "Construction Depot"), 4, false);
        /*
         * And a bank, because this section is about the CURVE.
         *
         * The rate a borrower is quoted is the curve plus whatever the bank's
         * strain adds, and a city with no branches has no capacity and sits at
         * the full premium - which pushes every quote here through the ceiling
         * and turns onCurve() below into a permanent failure. The premium is
         * real and is tested elsewhere; what is being measured here is what the
         * city's own debt load does to its price, so the bank is built and left
         * unstrained.
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
         * A serial or term bond is issued at PAR: the face is what the player
         * asked for and the city receives par less fees, which is what issuing
         * at par means. Asserted here so nobody later "fixes" them to match the
         * note and quietly changes what those two instruments are.
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
                roadCity.getLandManager().getOwnedSqFt() + road.getLandSqFt() * 60);

        /*
         * BIG ENOUGH TO ACTUALLY BE SHORT.
         *
         * The first version of this ordered five, which a founding city can
         * simply afford out of its $300k - so it built them, the funding path
         * was never entered, and two of the assertions below were measuring a
         * city that had already built the roads twice. A fixture has to CAUSE
         * the condition it is testing, and the condition here is "cannot pay".
         *
         * Forty roads is about $540k against $300k of cash, and the assertion
         * right below refuses to go on if that ever stops being true.
         */
        int howMany = 40;
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
        roadCity.issueEmergencyDebt(gap, Game.EMERGENCY_NOTE_MONTHS);

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
        banned.buildStack(template(banned, "Textile Mill"), 3, false);
        banned.buildStack(template(banned, "Construction Depot"), 4, false);
        banned.buildStack(template(banned, "Coal Power Plant"), 1, false);
        // ...and somewhere to put a shop, or the refusal is about land and the
        // ban is still never reached.
        BusinessDebtManager ledger = banned.getEconomyManager().getBusinessDebtManager();
        EconomyManager econ = banned.getEconomyManager();
        String sector = BusinessDebtManager.RETAIL;

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
        int loansBefore = ledger.getLoanCount(sector);
        double principalBefore = ledger.getPrincipal(sector);
        int shopsBefore = banned.getBuildingManager().getTotalStoreCoverage();

        quietly(() -> {
            for (int pass = 0; pass < 3; pass++) {
                econ.setSectorCash(sector, 10);
                banned.simulateMonths(1);
            }
        });

        System.out.println("   retail's advisor says: " + banned.getLastInvestment(sector));
        // Fewer loans is fine - the written-down ones mature and settle. More
        // is the bug.
        assertTrue("a banned sector takes no new loan to expand",
                ledger.getLoanCount(sector) <= loansBefore
                        && ledger.getPrincipal(sector) <= principalBefore + 1e-6);
        assertTrue("...and the refusal says why, in the ban's own words",
                banned.getLastInvestment(sector).contains("borrowing ban"));
        // Retiring idle shops is allowed (that is what a broke sector does);
        // opening new ones on credit is not.
        assertTrue("...and nothing was built on credit it did not have",
                banned.getBuildingManager().getTotalStoreCoverage() <= shopsBefore);

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
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

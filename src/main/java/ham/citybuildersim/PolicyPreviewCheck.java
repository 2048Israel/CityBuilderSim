package ham.citybuildersim;

import ham.citybuildersim.sectors.HeavyIndustry;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;

/**
 * The Policy tab's previews (0.7.36): PolicyPreview and the reads it is made
 * of, held to the month's own books in a played city.
 *
 * WHY. Every dial on the Policy tab shows what it would do before it is
 * applied, and since 0.7.36 every "after" is a model read on the class that
 * owns the arithmetic, asked of a detached copy of the policy the staged set
 * was put through (the project's spec-policy-0735.md, D2). A read that did
 * not reproduce the month's own figure at the city's own dials, a copy that
 * wrote into the city's decision log, or a preview that differed from what
 * the model computes once the same dials are applied would be a confident
 * wrong answer on the screen where the player decides the budget. It is
 * advice, not a model change, so nothing in the default playtest reaches it:
 * this is the only place it is played.
 *
 * What this has to prove:
 *   1. THE COPY is every dial, detached: its own setters clamp as the city's
 *      do, a move on it moves nothing in the city, and it records no decision.
 *   2. AT THE CITY'S OWN POLICY every line is the month's: each sector's
 *      profit tax its booked tax, its sales tax its net, the wage tax by
 *      band summed to getWageTax(), property each sector's charge at the roll
 *      as it stands, the contribution and both premiums off the wage bill,
 *      the pensions, the EI bill on the pool, the pensioner household, the
 *      central bank's compression and ceiling, the floor in today's money -
 *      and THE BUDGET moves by nothing.
 *   3. THE BUDGET is last month's balance plus each line's move: a tax-only
 *      set moves it by the take's move, the pension by the pensions', the
 *      clinic's fee by the fees at one - and since 0.7.103 (Jerus's A7) the
 *      fuel duty by the month's litres at the pump at the duty charged, at
 *      the city's own dial the month's duty.
 *   4. THE OTHER READS: the savers' rate the bank would choose, between where
 *      it is and where it heads; the compression and the ceiling at another
 *      setting; the floor's cash and founding figures one way and back; the
 *      floor lifting only the wages under it (WG1: the posts filled in the jobs
 *      it reaches, none when it stays under every target, the city's own payroll
 *      job by job, the job cover).
 *   5. AFTER A LOAD the take at the city's own policy is the saved month's.
 *   6. APPLIED, THE PREVIEW IS THE MODEL: the same dials set on the city
 *      read as the copy did; the take struck on the copy is the take struck
 *      on the city after, on the same books; a month on, the EI bill the
 *      treasury paid at the top of it and the bank's tax charged there are
 *      the previews struck before it, to the cent, and each payroll line is
 *      the month's own at the new dials (6b: section 2 again, there).
 *   7. A DIAL AT ZERO previews something (the spec's B9): the old screen
 *      scaled today's figure by the ratio of two rates and read nothing from
 *      a contribution, a pension, a premium or a benefit at zero.
 *   8. THE FUND'S WITHDRAWAL (0.7.48, C2): at the dial in force the
 *      preview's due is next month's, struck when it comes; and a year on,
 *      earning nothing, the fund is its value times (1 - the rate) to the
 *      twelfth, which is what twelve of the model's own withdrawals leave.
 *   9. ZONING'S BEFORE AND AFTER (0.7.107, batch Z1): a zone and a forest
 *      law staged on a copy move nothing in the city, and applied read as
 *      the copy did - what the zone holds, what that ground would fetch, the
 *      price given up, the forest's three kinds and the ground free for
 *      anyone; and a zone's tax relief is the property tax un-zoning it
 *      would raise.
 *  10. THE PORT DUES (FR4; the dial is FR3's): in a city whose berth has
 *      worked, the dues previewed at the city's own dial are the month's,
 *      Freight's and the treasury's, to the bit; at 2 twice that, which is
 *      what a twin that ran the month at 2 paid, to the cent; at 0 none, and
 *      the budget moves by the dues' move alone; a tonne's handling at a dial
 *      is the division's quote at it; and the copy's dial clamps as the
 *      city's does.
 *
 * Every fixture causes its condition.
 */
public class PolicyPreviewCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-100s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** Money is in thousands, so this is a cent. */
    static final double CENT = 1e-5;

    static boolean near(double a, double b) { return Math.abs(a - b) <= CENT; }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    /**
     * A played city with every line of the Policy tab in it: homes, shops,
     * a diner and builders; a mine, a foundry and a fabrication shop; power,
     * water and roads; a commercial bank (its profit tax), clinics (their
     * fees) and schools; put up finished out of a Wealthy treasury and played
     * five years - long enough for pensioners, claimants and students.
     *
     * 580 HOMES SINCE 0.7.55, where it was 500. The ground is priced by how
     * crowded the city is now (LandMarket, THE CROWDING PREMIUM): 4,300 people
     * on its 18.9 square kilometres pay the base, where the size premium had
     * charged about 17 times it, and its businesses hired the pool empty -
     * none on EI in month 62, the month section 6 reads. With 580 homes the
     * pool holds claimants in the months sections 6 and 7 read (222 and 115).
     *
     * 700 HOMES SINCE FR2, where it was 580. Haulage carries the city leg of
     * every bulk tonne at one quote, and a works no longer keeps its own vans
     * for its bulk (I4's weighing), so the fixture's businesses grow
     * differently: by month 61 the city stands a Construction Materials Plant
     * (250 posts; none stood before) and offers 2,474 posts where it offered
     * 2,125, and the 209 claimants of month 61 were hired - none on EI in the
     * months sections 6 and 7 read (4,106 people, 0 claimants). 700 homes put
     * the pool back (204 claimants at month 61, 254 in section 7), the same
     * remedy as 0.7.55's, for the same reason: the condition is a pool that
     * the city's businesses have not hired empty.
     *
     * THE D$ HELD SINCE AN1, where the float moved it. With the goods struck
     * at a D$ that follows the D$ slowly the city's rate ends the five years
     * at 0.87 D$ a US$ (1.34 on main, where the prices chased it up), imports are
     * cheaper, and the city hires its pool empty again: 4,990 people, 0 on EI
     * and 46 openings at month 61 (4,634, 208 on EI and 1 opening on main).
     * This fixture measures the Policy tab's previews, not the currency, so
     * the rate is held where the founding leaves it: 4,622 people and 197 on
     * EI at month 61.
     *
     * 800 HOMES SINCE PT1-M4, where it was 700. The works department that
     * built 400 points for nobody is retired and the city founds with a
     * Builders' Yard that builds 160, so the fixture's businesses grow
     * differently again and hire the pool empty (4,906 people, 0 on EI at
     * month 61: sections 6 and 7 had no claimants to price the benefit on).
     * 800 homes put the pool back, the same remedy as 0.7.55's and FR2's:
     * 5,075 people and 117 on EI at month 61.
     */
    static Game city(Path root) {
        GameFiles files = new GameFiles(root.resolve("city"), root.resolve("city-no-legacy"));
        Game g = new Game(files);
        // XR4: the world's price of money is held too, at the constant: under the US's rate (5.5% at the founding) the town's bank
        // makes no profit by month 61 and its pool has no claimant, so sections 6 and 7 had nothing to preview. The world quieted
        // for the D$, as BondCheck's and MapCheck's played cities are; the previews are the fixture's subject, not the world.
        g.quietTheWorldForTheD();
        quietly(() -> {
            g.run();
            // THE LEVERS OPENED IN THE FIXTURE (R0): the previews are of dials that exist, and on history's dates the taxes, the pension, EI
            // and the rest are shut until 1916-1966. Every lever opened and the defaults put back, before a month has run.
            g.getEconomyManager().getTaxPolicy().reset();
            g.getEconomyManager().getTaxPolicy().openAll();
            // AN1 (2026-10-22): the D$ is held (see above); the reload twin of section 5 runs no month, so no side of it floats.
            g.getForeignAccounts().pinRate(g.getForeignAccounts().getRate());
            // WN3: the cement sector builds nothing here. On the world's band it borrows $53M by month 13 for a Lime Kiln the town's
            // crews take 45 months to open (its plan reads "already building" the while), pays $343k a month of interest on it with
            // nothing coming in, defaults in months 48 to 56 ($9.7M of write-offs in month 53 alone) and the bank, at $35M of equity,
            // makes a loss through month 63: sections 6 and 7 are about the bank's profit tax, and there is none to preview. The
            // fixture measures the Policy tab's previews, not the credit cycle, so no sector borrows a works the town cannot build.
            g.getBusinessInvestment().holdSector(Sectors.CEMENT_CONCRETE);
            // LD1 (2026-10-31): nor does Freight build a Rail Depot. The staffing gate held it on main ("the city could staff 26% of a
            // Rail Depot; it wants 80%") and the refused posts priced into the band's wage now let it through: it borrows $185M from the
            // bank by month 61, loses $1.3M a month on it (net income -544k, -1,280k, -1,382k in months 61 to 63, its cash nil) and the
            // loan is written off at $740k a month from month 62, so the bank's profit is a loss in months 61 to 64 (-2,113k, -5,363k,
            // -1,693k, -4,546k) and again from month 73: bankTaxUnder() is nothing in the month section 6 reads. Same cause as the Lime
            // Kiln above and the same remedy; the previews are the fixture's subject, not the credit cycle.
            g.getBusinessInvestment().holdSector(Sectors.FREIGHT);
            // GW1: the gate counts a year's draw (Sector.GATE_DRAW_MONTHS), so the Blast Furnace Heavy Industry was held for want of staff ("could staff 35%") is ordered in month 60 ($44.7M: $8.4M of bonds, $37.0M from the bank) and the bank's profit is a loss in months 60 and 61 (-576k, -986k) where it was +59k: bankTaxUnder() is nothing in the month section 6 reads. Same cause as the Lime Kiln and the Rail Depot above and the same remedy: Heavy Industry builds nothing here.
            g.getBusinessInvestment().holdSector(Sectors.HEAVY_INDUSTRY);
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.getGovernmentInvestor().spend(-2_000_000 * PriceLevel.FACTOR);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 200_000_000L);
            for (String[] w : ORDERS) {
                BuildingsTemplate t = LongPlaytest.template(g, w[0]);
                if (t != null) g.buildStack(t, Integer.parseInt(w[1]), true);
            }
            g.simulateMonths(60);
        });
        return g;
    }

    /** The fixture's orders (the houses 800 since PT1-M4: see city()). */
    static final String[][] ORDERS = {
            { "House", "800" }, { "Convenience Store", "12" }, { "Diner", "2" }, { "Construction Depot", "6" },
            { "Coal Power Plant", "2" }, { "Water Treatment Plant", "2" }, { "Industrial Bakery", "3" },
            { LongPlaytest.byId(HeavyIndustry.ARC_FURNACE), "2" }, { "Fabrication Shop", "2" }, { "Commercial Bank", "1" },
            { "Elementary School", "3" }, { "Walk-in Clinic", "3" }, { "Paved Road", "20" } };

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        Path root = TempDirs.create("policypreviewcheck");

        Game g = city(root);
        printCity(g);
        theCopy(g);
        atTheCitysOwn(g, "2");
        theBudget(g);
        theOtherReads(g);
        theMinimumWagePage(root, g);
        afterALoad(root, g);
        anEiPool(g);
        appliedIsTheModel(g);
        aDialAtZero(g);
        theWithdrawal(g);
        zoning(g);
        portDues();

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /**
     * An EI pool that stays, caused. WN3: on the world's band the fixture's city is job-rich, and the 125 on EI it opens month 61 with
     * (the month's separations) are hired again the month after, so by the sections that play a month (1, then 6 and 7 here) nobody is
     * on EI and the bill the dials are previewed on is nothing. Sections 6 and 7 are about the EI bill, so as OutsideCheck does it,
     * half of the largest employer's buildings close and a month passes: those out of work are on EI, and they stay (the posts are gone).
     */
    static void anEiPool(Game g) {
        /*
         * ...AND AGAIN WHILE THE CITY STILL HAS A POST FOR EVERYONE (IC1, 2026-11-01). An idle builder's laid-off crews are spare for
         * the staffing gate since IC1, so the planners order the buildings the gate used to refuse and the city this fixture plays has
         * 473 posts with no worker in them at month 62 (3,260 posts, 2,787 filled), not a lull's 366 out of work: the first closing (six depots, 300 posts) takes
         * workers who are hired into the vacancies the same month, and nobody is on EI after it. The next largest employer is closed the
         * same way, a month passing each time, until some are on EI (the second closing, a Fabrication Shop, leaves 240 on EI and the
         * pool grows with the third): it is the posts being gone that keeps them out. On main's rule the first closing does it
         * (766 out of work), so it is the only one.
         */
        for (int round = 0; round < EI_POOL_ROUNDS; round++) {
            BuildingsTemplate biggest = null;
            int biggestJobs = 0;
            for (BuildingsTemplate b : g.getBuildingManager().getTemplates()) {
                if (b.getSector() == null || b.getTotalJobs() <= 0) continue;
                int jobs = b.getTotalJobs() * g.getBuildingManager().getQuantity(b.getId());
                if (jobs > biggestJobs) { biggestJobs = jobs; biggest = b; }
            }
            final BuildingsTemplate closing = biggest;
            if (closing == null) return;
            quietly(() -> {
                g.getBuildingManager().retire(closing, Math.max(1, g.getBuildingManager().getQuantity(closing.getId()) / 2));
                g.simulateMonths(1);
            });
            if (g.getUnemployment().onEi() > 0) return;
        }
    }

    /** The most employers anEiPool() closes before it gives up on a pool (IC1): the city with a post for everyone is closed into one in two or three. */
    static final int EI_POOL_ROUNDS = 6;

    /** The city as the previews read it, for the record. */
    static void printCity(Game g) {
        EconomyManager em = g.getEconomyManager();
        out.printf("%n      the city: month %d, %,d people; tax a month %s, balance %s; %,.0f seniors, %,.0f on EI; bank profit tax next month %s%n",
                g.getMonth(), g.getPopulationManager().getPopulation(),
                Formats.INSTANCE.amount(CityNeeds.taxRaised(g)),
                Formats.INSTANCE.amount(em.getNationalAccounts().getBalance()),
                em.getSeniors(), g.getUnemployment().onEi(),
                Formats.INSTANCE.amount(g.bankTaxUnder(em.getTaxPolicy())));
    }

    /* ============================ 1. THE COPY ============================ */

    static void theCopy(Game g) {
        out.println("\n--- 1. the copy is every dial, detached: it clamps as the city's does and records nothing ---");
        TaxPolicy live = g.getEconomyManager().getTaxPolicy();
        // Part the three income bases and set an offset of each kind, so the copy has something to carry.
        quietly(() -> {
            live.setSalesTaxRate(live.getSalesTaxRate() + .0125);
            live.setProfitOffset(Sectors.RETAIL, .02);
            live.setSalesOffset(Sectors.CONSTRUCTION, -.01);
            live.setPropertyOffset(Sectors.REAL_ESTATE, .002);
            live.setWageOffset(WageBand.values()[0], .01);
        });
        assertTrue("fixture: the three income bases have parted and an offset of every kind is set",
                live.incomeRatesSplit() && live.getProfitOffset(Sectors.RETAIL) != 0
                && live.getSalesOffset(Sectors.CONSTRUCTION) != 0 && live.getPropertyOffset(Sectors.REAL_ESTATE) != 0
                && live.getWageOffset(WageBand.values()[0]) != 0);

        TaxPolicy copy = live.copy();
        assertTrue("the copy's every dial is the city's: the policy array, slot for slot",
                Arrays.equals(copy.getPolicyState(), live.getPolicyState()));
        boolean offsets = true;
        for (Sector s : g.getSectors().all()) {
            offsets &= copy.getProfitOffset(s) == live.getProfitOffset(s) && copy.getSalesOffset(s) == live.getSalesOffset(s)
                    && copy.getPropertyOffset(s) == live.getPropertyOffset(s);
        }
        assertTrue("...every sector's three offsets", offsets);
        assertTrue("...and the wage a pension is struck on", copy.pensionPerSenior() == live.pensionPerSenior()
                && copy.pensionPerSeniorAt(.5) == live.pensionPerSeniorAt(.5));

        int decided = g.getDecisions().size();
        double[] before = live.getPolicyState();
        copy.setProfitTaxRate(5);
        copy.setPropertyTaxRate(-1);
        copy.setPensionReplacement(.9);
        copy.setProfitOffset(Sectors.RETAIL, -.9);
        copy.setTuitionScale(2);
        assertTrue("a profit rate past the stop is held at MAX_INCOME_TAX on the copy, as the city's setter holds it",
                copy.getProfitTaxRate() == TaxPolicy.MAX_INCOME_TAX);
        assertTrue("...a property rate under nothing at nothing, an offset at MAX_OFFSET",
                copy.getPropertyTaxRate() == 0 && copy.getProfitOffset(Sectors.RETAIL) == -TaxPolicy.MAX_OFFSET);
        assertTrue("moving the copy's dials moved none of the city's", Arrays.equals(before, live.getPolicyState())
                && live.getProfitOffset(Sectors.RETAIL) == .02);
        assertTrue("...and wrote nothing in the city's decision log (" + decided + " entries)",
                g.getDecisions().size() == decided);
        assertTrue("a pension at another rate is that rate of the same wage base, held to MAX_REPLACEMENT",
                near(live.pensionPerSeniorAt(.30), .30 * live.pensionPerSenior() / Math.max(1e-12, live.getPensionReplacement()))
                && live.pensionPerSeniorAt(2) == live.pensionPerSeniorAt(TaxPolicy.MAX_REPLACEMENT));

        // The parted city, played a month, so the books are struck at the dials section 2 reads them under.
        quietly(() -> g.simulateMonths(1));
    }

    /* ============================ 2. AT THE CITY'S OWN POLICY ============================ */

    static void atTheCitysOwn(Game g, String section) {
        out.println("\n--- " + section + ". at the city's own policy every line is the month's, and the budget moves by nothing ---");
        EconomyManager em = g.getEconomyManager();
        TaxPolicy live = em.getTaxPolicy();
        PolicyPreview.TaxTake take = PolicyPreview.taxTake(g, live);

        int paying = 0, selling = 0, refund = 0;
        boolean profit = true, sales = true, property = true;
        double moved = 0;
        int movedSectors = 0;
        for (Sector s : g.getSectors().all()) {
            SectorBooks.SectorMonth m = g.getSectorBooks().get(s);
            profit &= near(take.profit(s.key()), m.tax()) && near(take.profit(s.key()), s.getProfitTax());
            sales &= near(take.sales(s.key()), em.getSalesTaxLedger().getNet(s.key()));
            property &= near(take.property(s.key()), em.getPropertyTaxFor(s));
            double gap = Math.abs(take.property(s.key()) - em.getPropertyTaxCharged(s.key()));
            if (gap > CENT) { moved += gap; movedSectors++; }
            if (m.tax() > 0) paying++;
            if (em.getSalesTaxLedger().getTaxableSales(s.key()) > 0) selling++;
            if (em.getSalesTaxLedger().isInRefund(s.key())) refund++;
        }
        assertTrue("fixture: sectors paid profit tax (" + paying + ") and sold taxable goods (" + selling + "; " + refund
                + " in refund)", paying > 2 && selling > 2);
        assertTrue("each sector's profit tax is its booked tax - the SectorBooks month and its statement's", profit);
        assertTrue("...its sales tax its net remittance (a refund negative)", sales);
        assertTrue("...its property tax the month's own charge on its roll as it stands (getPropertyTaxFor()); "
                + movedSectors + " sector(s) moved " + Formats.INSTANCE.amount(moved) + " since the charge", property);
        double bands = 0;
        boolean banded = true;
        for (WageBand b : WageBand.values()) {
            bands += take.wage(b);
            banded &= near(take.wage(b), em.payrollIn(b) * live.effectiveWageRate(b));
        }
        assertTrue("each band's wage tax is its staffed payroll at its rate", banded);
        assertTrue("...and the bands sum to the month's wage tax", Math.abs(bands - em.getWageTax()) <= 1e-6 * Math.max(1, em.getWageTax()));
        assertTrue("the take's profit and sales lines sum to the sectors' and the bank's",
                near(take.profitTotal(), sum(take.profit()) + take.bank()) && near(take.salesTotal(), em.getSalesTax()));

        assertTrue("fixture: the wage bill carries a contribution and an EI premium",
                em.getWageBill() > 0 && em.getContributions() > 0 && em.getEiPremiums() > 0);
        assertTrue("the contribution at the city's rate off the wage bill is the month's", near(em.contributionsAt(live.getContributionRate()), em.getContributions()));
        assertTrue("...the EI premium", near(em.premiumAt(live.getEiPremiumRate()), em.getEiPremiums()));
        assertTrue("...and the health premium", near(em.premiumAt(live.getHealthPremiumRate()), em.getHealthPremiums()));
        assertTrue("fixture: the city pays pensions (" + String.format("%,.0f", em.getSeniors()) + " seniors)", em.getPensionsPaid() > 0);
        assertTrue("the pensions at the city's pension are the month's", near(em.pensionsPaidUnder(live), em.getPensionsPaid()));
        Unemployment u = g.getUnemployment();
        assertTrue("the EI bill at the city's benefit rate is the pool's (getBenefitsPaid())",
                near(u.benefitsAt(live.getEiBenefitRate()), u.getBenefitsPaid()));
        HouseholdBalance hb = g.getHouseholdBalance();
        assertTrue("fixture: a pensioner household has something to spend",
                hb.getDisposable(HouseholdAccounts.RETIRED) > 0);
        assertTrue("a pensioner household at the city's pension has what its ledger says (M11 at no move)",
                hb.disposableWithRowMoved(HouseholdAccounts.RETIRED, 0) == hb.getDisposable(HouseholdAccounts.RETIRED)
                && near(PolicyPreview.pensionerHasUnder(g, live), hb.getDisposable(HouseholdAccounts.RETIRED)));
        DebtManager dm = g.getDebtManager();
        CentralBank cb = g.getCentralBank();
        assertTrue("the compression at the share the central bank holds is the curve's own, at every term",
                dm.compressionAt(dm.centralBankShareOfTerm(), 360) == dm.compression(360)
                && dm.compressionAt(dm.centralBankShareOfTerm(), 60) == dm.compression(60));
        assertTrue("the ceiling at the dial's months is the ceiling", cb.ceilingAt(cb.getAdvancesCeilingMonths()) == cb.ceiling());
        LabourMarket lm = g.getLabourMarket();
        assertTrue("the floor at today's setting, in today's money, is cashMinimumWage()",
                lm.cashAt(lm.getMinimumWage()) == lm.cashMinimumWage());
        double share = g.getEducation().getTuitionSubsidy();
        assertTrue("the budget under a copy of the city's own policy moves by nothing",
                PolicyPreview.change(g, live, live.copy(), share, share) == 0);
        assertTrue("...and THE BUDGET's before is last month's balance",
                PolicyPreview.budget(g, live.copy(), share).before() == em.getNationalAccounts().getBalance());
    }

    static double sum(java.util.Map<String, Double> m) {
        double t = 0;
        for (double v : m.values()) t += v;
        return t;
    }

    /* ============================ 7. A DIAL AT ZERO ============================ */

    static void aDialAtZero(Game g) {
        out.println("\n--- 7. a dial at zero previews something: the contribution, the pension, the premium, the benefit (B9) ---");
        EconomyManager em = g.getEconomyManager();
        TaxPolicy live = em.getTaxPolicy();
        double contribution = live.getContributionRate(), pension = live.getPensionReplacement(),
                premium = live.getEiPremiumRate(), benefit = live.getEiBenefitRate();
        quietly(() -> {
            live.setContributionRate(0);
            live.setPensionReplacement(0);
            live.setEiPremiumRate(0);
            live.setEiBenefitRate(0);
            g.simulateMonths(1);
        });
        Unemployment u = g.getUnemployment();
        assertTrue("fixture: the four dials at zero, and the month collected and paid nothing on them ("
                + String.format("%,.0f", u.onEi()) + " claimants)",
                em.getContributions() == 0 && em.getEiPremiums() == 0 && em.getPensionsPaid() == 0
                && u.getBenefitsPaid() == 0 && u.onEi() > 0 && em.getSeniors() > 0);
        TaxPolicy back = live.copy();
        back.setContributionRate(contribution);
        back.setPensionReplacement(pension);
        back.setEiPremiumRate(premium);
        back.setEiBenefitRate(benefit);
        assertTrue("the contribution put back previews the wage bill at its rate",
                em.contributionsAt(back.getContributionRate()) > 0
                && near(em.contributionsAt(back.getContributionRate()), SocialSecurity.contributionsOn(em.getWageBill(), contribution)));
        assertTrue("...the pension put back, today's seniors at it",
                em.pensionsPaidUnder(back) > 0 && near(em.pensionsPaidUnder(back), em.getSeniors() * back.pensionPerSenior()));
        assertTrue("...the EI premium put back", em.premiumAt(back.getEiPremiumRate()) > 0);
        assertTrue("...and the benefit put back, the pool at it", u.benefitsAt(back.getEiBenefitRate()) > 0);
        assertTrue("...and a pensioner household would have more than it has", PolicyPreview.pensionerHasUnder(g, back)
                > g.getHouseholdBalance().getDisposable(HouseholdAccounts.RETIRED));
        quietly(() -> {
            live.setContributionRate(contribution);
            live.setPensionReplacement(pension);
            live.setEiPremiumRate(premium);
            live.setEiBenefitRate(benefit);
        });
    }

    /* ============================ 6. APPLIED, THE PREVIEW IS THE MODEL ============================ */

    static void appliedIsTheModel(Game g) {
        out.println("\n--- 6. applied, the preview is the model: the same dials, the same take, and a month on the bills it struck ---");
        EconomyManager em = g.getEconomyManager();
        TaxPolicy live = em.getTaxPolicy();
        TaxPolicy copy = live.copy();
        copy.setWageTaxRate(live.getWageTaxRate() + .01);
        copy.setPropertyTaxRate(live.getPropertyTaxRate() + .001);
        copy.setProfitTaxRate(live.getProfitTaxRate() + .01);
        copy.setProfitOffset(Sectors.RETAIL, live.getProfitOffset(Sectors.RETAIL) + .0025);
        copy.setContributionRate(live.getContributionRate() + .01);
        copy.setPensionReplacement(live.getPensionReplacement() + .05);
        copy.setEiPremiumRate(live.getEiPremiumRate() + .005);
        copy.setHealthPremiumRate(live.getHealthPremiumRate() + .01);
        copy.setEiBenefitRate(live.getEiBenefitRate() + .05);

        PolicyPreview.TaxTake preview = PolicyPreview.taxTake(g, copy);
        double eiBill = g.getUnemployment().benefitsAt(copy.getEiBenefitRate());
        double bankTax = g.bankTaxUnder(copy);
        assertTrue("fixture: the bank made a profit to be taxed on, and the pool has claimants",
                bankTax > 0 && eiBill > 0);

        int decided = g.getDecisions().size();
        quietly(() -> {
            live.setWageTaxRate(copy.getWageTaxRate());
            live.setPropertyTaxRate(copy.getPropertyTaxRate());
            live.setProfitTaxRate(copy.getProfitTaxRate());
            live.setProfitOffset(Sectors.RETAIL, copy.getProfitOffset(Sectors.RETAIL));
            live.setContributionRate(copy.getContributionRate());
            live.setPensionReplacement(copy.getPensionReplacement());
            live.setEiPremiumRate(copy.getEiPremiumRate());
            live.setHealthPremiumRate(copy.getHealthPremiumRate());
            live.setEiBenefitRate(copy.getEiBenefitRate());
        });
        assertTrue("the same dials set on the city read as the copy did, slot for slot",
                Arrays.equals(live.getPolicyState(), copy.getPolicyState())
                && live.getProfitOffset(Sectors.RETAIL) == copy.getProfitOffset(Sectors.RETAIL));
        assertTrue("...and the city's setters wrote the decisions the copy's did not (" + (g.getDecisions().size() - decided) + ")",
                g.getDecisions().size() > decided);
        PolicyPreview.TaxTake after = PolicyPreview.taxTake(g, live);
        assertTrue("the take struck on the copy is the take struck on the city after, on the same books, to the cent",
                near(preview.total(), after.total()) && near(preview.wageTotal(), after.wageTotal())
                && near(preview.propertyTotal(), after.propertyTotal()) && near(preview.profitTotal(), after.profitTotal()));

        quietly(() -> g.simulateMonths(1));
        assertTrue("a month on, the EI bill the treasury paid at the top of it is the one benefitsAt() previewed (B11)",
                near(em.getEiBenefits(), eiBill));
        assertTrue("...and the bank's profit tax charged there is the one bankTaxUnder() previewed, at retail's new rate (B5)",
                near(em.getBankTax(), bankTax));
        assertTrue("...and each payroll line is struck on the month's own bill at the new dials: the contribution",
                near(em.contributionsAt(live.getContributionRate()), em.getContributions()));
        assertTrue("...the two premiums", near(em.premiumAt(live.getEiPremiumRate()), em.getEiPremiums())
                && near(em.premiumAt(live.getHealthPremiumRate()), em.getHealthPremiums()));
        assertTrue("...and the pensions", near(em.pensionsPaidUnder(live), em.getPensionsPaid()));
        atTheCitysOwn(g, "6b");
    }

    /* ============================ 3. THE BUDGET ============================ */

    static void theBudget(Game g) {
        out.println("\n--- 3. the budget is last month's balance plus each line's move ---");
        EconomyManager em = g.getEconomyManager();
        TaxPolicy live = em.getTaxPolicy();
        double share = g.getEducation().getTuitionSubsidy();

        TaxPolicy taxes = live.copy();
        taxes.setIncomeTaxRate(live.getIncomeTaxRate() + .01);
        taxes.setPropertyTaxRate(live.getPropertyTaxRate() + .001);
        double takeMove = PolicyPreview.taxTake(g, taxes).total() - PolicyPreview.taxTake(g, live).total();
        assertTrue("fixture: every tax a point up moves the take (" + Formats.INSTANCE.amount(takeMove) + ")", takeMove > 0);
        assertTrue("a tax-only set moves the budget by the take's move, exactly",
                PolicyPreview.change(g, live, taxes, share, share) == takeMove);
        PolicyPreview.Budget b = PolicyPreview.budget(g, taxes, share);
        assertTrue("...and THE BUDGET after is the balance plus it", b.after() == em.getNationalAccounts().getBalance() + takeMove);

        TaxPolicy pension = live.copy();
        pension.setPensionReplacement(live.getPensionReplacement() + .05);
        double paid = em.pensionsPaidUnder(pension) - em.getPensionsPaid();
        assertTrue("a pension five points up moves it down by the pensions' move (" + Formats.INSTANCE.amount(paid) + ")",
                paid > 0 && near(PolicyPreview.change(g, live, pension, share, share), -paid));

        TaxPolicy fee = live.copy();
        fee.setHealthFeeScale(live.getHealthFeeScale() + 1);
        assertTrue("fixture: the clinics bill treatment (" + Formats.INSTANCE.amount(g.getHealthcare().treatmentFeesAtOne())
                + " a month at the founding fee)", g.getHealthcare().treatmentFeesAtOne() > 0);
        assertTrue("the clinic's fee a founding fee up moves it by the fees at one, at full service on both sides (B13)",
                near(PolicyPreview.change(g, live, fee, share, share), g.getHealthcare().treatmentFeesAtOne()));

        TaxPolicy premium = live.copy();
        premium.setEiPremiumRate(live.getEiPremiumRate() + .005);
        assertTrue("an EI premium half a point up moves it by half a point of the wage bill",
                near(PolicyPreview.change(g, live, premium, share, share), em.getWageBill() * .005));
        assertTrue("the city's share of tuition moved alone moves it by the tuition the city would waive",
                near(PolicyPreview.change(g, live, live.copy(), share, Math.min(1, share + .1)),
                        -(PolicyPreview.tuitionWaived(g, live, Math.min(1, share + .1)) - PolicyPreview.tuitionWaived(g, live, share))));
        // (The food vouchers' line, 0.7.45's B19, is GroceryCheck's: this
        // Wealthy city has nobody a voucher would go to, so it cannot cause it.)

        // THE FUEL DUTY (0.7.103, batch P4; Jerus's A7): the month's litres at the pump at the duty as charged.
        double litres = PolicyPreview.fuelLitres(g);
        assertTrue("fixture: the drivers bought petrol at the pump last month (" + Formats.INSTANCE.count(litres) + " L)", litres > 0);
        TaxPolicy duty = live.copy();
        duty.setFuelDuty(.000215 * PriceLevel.FACTOR);
        assertTrue("at the city's own dial the duty previewed is the month's, to the bit (none at the default)",
                PolicyPreview.fuelDutyAt(g, live) == em.getFuelDuty());
        double moved = PolicyPreview.change(g, live, duty, share, share), byHand = litres * duty.chargedFuelDuty() - PolicyPreview.fuelDutyAt(g, live);
        System.out.printf("   moved %.6f, by hand %.6f; at the duty %.9f against %.9f%n", moved, byHand, PolicyPreview.fuelDutyAt(g, duty),
                litres * .000215 * PriceLevel.FACTOR * live.getExpectedLevel());
        assertTrue("a duty of 21.5 cents a litre moves the budget by the litres at it as charged, the expected price level in it",
                near(moved, byHand) && PolicyPreview.fuelDutyAt(g, duty) == litres * duty.chargedFuelDuty()
                        && duty.chargedFuelDuty() == .000215 * PriceLevel.FACTOR * live.getExpectedLevel());
        assertTrue("...and a litre at the pump by exactly the duty",
                near(PolicyPreview.pumpPriceAt(g, duty) - PolicyPreview.pumpPriceAt(g, live),
                        duty.chargedFuelDuty() - live.chargedFuelDuty()));
    }

    /* ============================ 4. THE OTHER READS ============================ */

    static void theOtherReads(Game g) {
        out.println("\n--- 4. the savers' rate, the compression, the ceiling, the floor ---");
        Bank bank = g.getBank();
        DebtManager dm = g.getDebtManager();
        double policy = dm.getPolicyRate();
        double low = bank.depositRateAt(0), here = bank.depositRateAt(policy), high = bank.depositRateAt(policy + .05);
        assertTrue("fixture: the city has a bank with depositors", bank.getBranches() > 0 && bank.getDeposits() > 0);
        assertTrue("the savers' rate the bank would choose rises with the dial", low <= here && here <= high && low < high);
        double target = Math.min(policy + .05 + CentralBank.WINDOW_PENALTY, bank.depositShare() * (policy + .05));
        assertTrue("...a step of the way from what it pays toward what its funding asks, never past it",
                high >= Math.min(bank.depositRate(), target) - 1e-12 && high <= Math.max(bank.depositRate(), target) + 1e-12);
        double expected = g.getExpectations().getExpectedInflation();
        assertTrue("at the dial in force the preview's real rate subtracts what savers expect, as the month does",
                PolicyPreview.realDepositRateAt(g, policy) == here - expected
                && Math.abs((PolicyPreview.realDepositRateAt(g, policy) - g.realDepositRate()) - (here - bank.depositRate())) <= 1e-12);

        CentralBank cb = g.getCentralBank();
        assertTrue("fixture: the central bank has a trailing revenue to cap the advances on", cb.trailingRevenue() > 0);
        assertTrue("the ceiling at twelve months is twelve months of it",
                near(cb.ceilingAt(12), 12 * cb.trailingRevenue()));
        assertTrue("...held to MAX_ADVANCES_CEILING, and nothing under none",
                cb.ceilingAt(1e6) == cb.ceilingAt(CentralBank.MAX_ADVANCES_CEILING) && cb.ceilingAt(-3) == 0);
        assertTrue("the compression holding nothing is nothing; at FULL_COMPRESSION_SHARE and past it, the whole premium at QE_COMPRESSION",
                dm.compressionAt(0, 360) == 0
                && dm.compressionAt(CentralBank.FULL_COMPRESSION_SHARE, 360) == DebtManager.termPremium(360) * CentralBank.QE_COMPRESSION
                && dm.compressionAt(1, 360) == dm.compressionAt(CentralBank.FULL_COMPRESSION_SHARE, 360));

        LabourMarket lm = g.getLabourMarket();
        double floor = lm.getMinimumWage();
        assertTrue("the floor in founding money comes back from its cash figure", Math.abs(lm.floorForCash(lm.cashAt(floor * 1.3)) - floor * 1.3) <= 1e-12);
        theFloorsMove(g);
    }

    /**
     * THE FLOOR LIFTS ONLY THE WAGES UNDER IT (WG1; it moved every wage by its own ratio until then): a job's target at a floor is its own, or the floor in
     * today's money where that is more (LabourMarket.targetWageAt() has the floor only in its max(), and targetWageAt(job, 0) is the ladder's own). So a
     * floor raised past the lowest rungs and under college's moves the posts filled in those rungs alone, one under every target moves nobody and the
     * wage bill not at all, the city's own payroll moves job by job through the three sums the month makes, and the floor takes home what a full-time job
     * at it takes home.
     */
    static void theFloorsMove(Game g) {
        LabourMarket lm = g.getLabourMarket();
        PopulationManager pm = g.getPopulationManager();
        double floor = lm.getMinimumWage();
        JobType[] jobTypes = JobType.values();
        long[] posts = pm.getJobs();
        double[] fill = pm.getJobFillRate();
        double[] own = new double[jobTypes.length], inForce = new double[jobTypes.length];
        double dearest = 0, cheapestCollege = Double.MAX_VALUE;
        for (JobType job : jobTypes) {
            int i = job.ordinal();
            own[i] = lm.targetWageAt(job, 0);
            inForce[i] = lm.targetWageAt(job, floor);
            dearest = Math.max(dearest, own[i]);
            if (WageBand.of(job) == WageBand.COLLEGE || WageBand.of(job) == WageBand.UNIVERSITY) cheapestCollege = Math.min(cheapestCollege, own[i]);
        }
        boolean targets = true;
        for (JobType job : jobTypes) {
            int i = job.ordinal();
            double staged = lm.cashAt(floor * 1.1);
            targets &= Math.abs(lm.targetWageAt(job, floor * 1.1) - Math.max(own[i], staged)) <= 1e-9
                    && lm.targetWageAt(job, floor * 1.1) >= staged - 1e-12 && inForce[i] >= lm.cashMinimumWage() - 1e-12;
        }
        assertTrue("every job's target is its own or the floor in today's money, whichever is more - the floor is in the max and nowhere else - and none is under the floor", targets);

        // (a) a floor raised past the unskilled and diploma wages and under college's
        double lowRungs = Math.max(own[JobType.NO_DIPLOMA.ordinal()], own[JobType.DIPLOMA.ordinal()]);
        double stagedA = lm.floorForCash((lowRungs + cheapestCollege) / 2);
        out.println("   the ladder's own wages: unskilled " + own[JobType.NO_DIPLOMA.ordinal()] + ", diploma " + own[JobType.DIPLOMA.ordinal()] + ", the cheapest college and up "
                + cheapestCollege + "; the floor in force " + lm.cashMinimumWage() + ", staged " + lm.cashAt(stagedA));
        assertTrue("fixture: the lowest rungs sit under college and up on the ladder, and the staged floor is past the floor in force", lowRungs < cheapestCollege && lm.cashAt(stagedA) > lm.cashMinimumWage());
        PolicyPreview.FloorMove move = PolicyPreview.floorMoveAt(g, stagedA);
        double expectedWorkers = 0, payNow = 0, payThen = 0, billNow = 0, billThen = 0;
        boolean collegeStill = true;
        for (JobType job : jobTypes) {
            int i = job.ordinal();
            double filled = posts[i] * fill[i], then = Math.max(own[i], lm.cashAt(stagedA));
            billNow += filled * inForce[i];
            billThen += filled * then;
            if (own[i] < lm.cashAt(stagedA)) {
                expectedWorkers += filled;
                payNow += filled * inForce[i];
                payThen += filled * then;
            } else {
                collegeStill &= lm.targetWageAt(job, stagedA) == inForce[i];
            }
        }
        assertTrue("fixture: there are people in the lowest rungs, and in college and up", expectedWorkers > 0 && collegeStill);
        assertTrue("the floor moves the posts filled in the jobs whose wage is under it (" + move.workers() + " of them), and no one in college and up",
                Math.abs(move.workers() - expectedWorkers) <= 1e-9 * expectedWorkers);
        assertTrue("...their pay a month at the floor in force and at the staged one are the hand sums, and it rises",
                Math.abs(move.payNow() - payNow) <= 1e-9 * payNow && Math.abs(move.payThen() - payThen) <= 1e-9 * payThen && move.payThen() > move.payNow());
        assertTrue("...and the city's whole wage bill moves by the same money, no one else's pay moving: billThen - billNow = payThen - payNow",
                Math.abs(move.billNow() - billNow) <= 1e-9 * billNow && Math.abs(move.billThen() - billThen) <= 1e-9 * billThen
                && Math.abs((move.billThen() - move.billNow()) - (move.payThen() - move.payNow())) <= 1e-9 * move.billNow()
                && Math.abs(PolicyPreview.wageBillMoveAt(g, stagedA) - billThen / billNow) <= 1e-12);
        PolicyPreview.FloorMove everyone = PolicyPreview.floorMoveAt(g, lm.floorForCash(1.2 * dearest));
        assertTrue("a floor past the dearest job moves every post filled, as PopulationManager.getJobsFilled() counts them",
                Math.abs(everyone.workers() - pm.getJobsFilled()) <= .5 && Math.abs(everyone.payThen() - everyone.billThen()) <= 1e-9 * everyone.billThen());

        // (b) a floor moved under every target: the floor in force binds no job, so none is moved by a floor that stays under them all
        double cheapest = Double.MAX_VALUE;
        for (JobType job : jobTypes) if (posts[job.ordinal()] > 0) cheapest = Math.min(cheapest, own[job.ordinal()]);
        double stagedB = floor * .9;
        assertTrue("fixture: the floor in force and the one staged under it are both under every target of a job with posts (the cheapest is " + cheapest + ", the floor " + lm.cashMinimumWage() + ")",
                lm.cashMinimumWage() < cheapest && lm.cashAt(stagedB) < lm.cashMinimumWage());
        PolicyPreview.FloorMove none = PolicyPreview.floorMoveAt(g, stagedB);
        assertTrue("a floor moved under every target moves no one, and the wage bill not at all",
                none.workers() == 0 && none.payNow() == none.payThen() && none.billNow() == none.billThen() && PolicyPreview.wageBillMoveAt(g, stagedB) == 1);

        // (c) the city's own payroll, job by job
        assertTrue("the city's own payroll at the floor in force is the payroll the city pays", PolicyPreview.cityPayrollAt(g, floor) == PolicyPreview.cityPayroll(g));
        assertTrue("...and a floor that reaches none of the staff's jobs moves it by nothing", PolicyPreview.cityPayrollAt(g, stagedB) == PolicyPreview.cityPayroll(g));
        double raised = lm.floorForCash(1.2 * dearest);
        double[] now = new double[jobTypes.length], then = new double[jobTypes.length];
        for (JobType job : jobTypes) {
            now[job.ordinal()] = lm.targetWageAt(job, floor);
            then[job.ordinal()] = lm.targetWageAt(job, raised);
        }
        BuildingManager bm = g.getBuildingManager();
        double staffMove = 0, teachers = bm.getCategoryPayroll(BuildingType.EDUCATION, then, fill) - bm.getCategoryPayroll(BuildingType.EDUCATION, now, fill),
                nurses = bm.getCategoryPayroll(BuildingType.HEALTHCARE, then, fill) - bm.getCategoryPayroll(BuildingType.HEALTHCARE, now, fill);
        double[] researchFill = g.getResearchFill() != null ? g.getResearchFill() : fill;
        for (ResearchCatalog.Field f : ResearchCatalog.Field.values()) {
            int held = g.getResearch().posts(f);
            if (held > 0) staffMove += Research.payroll(f, held, then, researchFill) - Research.payroll(f, held, now, researchFill);
        }
        staffMove += teachers + nurses;
        assertTrue("fixture: a floor past the dearest job lifts the teachers' and the nurses' pay (by " + teachers + " and " + nurses + ")", teachers > 0 && nurses > 0);
        assertTrue("...and the city's own payroll rises by those staff's posts filled times their move, the hand sum through getCategoryPayroll at the two target arrays",
                Math.abs(PolicyPreview.cityPayrollAt(g, raised) - (PolicyPreview.cityPayroll(g) + staffMove)) <= 1e-9 * (PolicyPreview.cityPayroll(g) + staffMove)
                && PolicyPreview.cityPayrollAt(g, raised) > PolicyPreview.cityPayroll(g));

        // (d) what the floor takes home against what living alone costs
        HouseholdAccounts hh = g.getHouseholds();
        double alone = hh.rentPerHousehold() + hh.shoppingPerHead() + hh.feesPerHead();
        double takeHome = lm.cashMinimumWage() * (1 - g.getEconomyManager().getTaxPolicy().payslipShare(WageBand.NONE));
        assertTrue("fixture: living alone costs something in this city (" + alone + ") and the payslip takes a share of the floor",
                alone > 0 && takeHome < lm.cashMinimumWage() && takeHome > 0);
        assertTrue("the floor's job cover is what a full-time job at it takes home over what living alone costs (" + PolicyPreview.floorJobCover(g) + ")",
                hh.costOfLivingAlone() == alone && Math.abs(PolicyPreview.floorJobCover(g) - takeHome / alone) <= 1e-12);
    }

    /* ============================ 11. THE MINIMUM-WAGE PAGE (WS1) ============================ */

    /** Equal to a part in a billion, or to a billionth outright: the shares and sums of a played city. */
    static boolean same(double a, double b) {
        return Math.abs(a - b) <= 1e-9 * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }

    /** The posts filled in the jobs that pass `which`, as PopulationManager.getJobsFilled() counts them (posts x fill rate, unrounded): the hand sum. */
    static double filledWhere(Game g, java.util.function.Predicate<JobType> which) {
        long[] posts = g.getPopulationManager().getJobs();
        double[] fill = g.getPopulationManager().getJobFillRate();
        double sum = 0;
        for (JobType job : JobType.values()) {
            if (job.ordinal() < posts.length && which.test(job)) sum += posts[job.ordinal()] * fill[job.ordinal()];
        }
        return sum;
    }

    /**
     * THE MINIMUM-WAGE PAGE'S FIGURES (WS1): FloorView is what the page draws and the screen recomputes nothing, so every figure is held to a hand
     * sum of the model's own getters in the played city, and each state the page's words name is caused: the default floor paying nobody, a floor
     * staged 20% over the going rate (lifting workers, leaving every job over it alone) and the same floor applied for a month in a twin loaded from
     * a save, a job with no posts, the joined market (the unskilled taken out of the supply, so their posts are filled by diploma holders), a city
     * where nobody is employed, and the world's nations.
     */
    static void theMinimumWagePage(Path root, Game g) {
        out.println("\n--- the minimum-wage page (WS1) ---");
        LabourMarket lm = g.getLabourMarket();
        PopulationManager pm = g.getPopulationManager();
        HouseholdAccounts hh = g.getHouseholds();
        JobType[] jobTypes = JobType.values();
        WageBand lowest = WageBand.values()[0];
        java.util.function.DoubleFunction<String> money = x -> String.format("<%.4f>", x);   // the screen's money writer, stood in for: the model writes no "$" of its own

        // ---- the default floor, nothing staged ----
        assertTrue("fixture: the floor in force is the default share of the going rate, and no tie or nudge has moved it",
                same(lm.getMinimumWage() / lm.getGoingRate(), LabourMarket.DEFAULT_FLOOR_SHARE) && lm.getFloorTie() == LabourMarket.FloorTie.PRICES
                && lm.getMinimumWageAdjustment() == 0);
        FloorView v = FloorView.of(g, lm.getMinimumWage());
        assertTrue("against the going rate: the going rate, the floor, the staged floor and the unskilled wage are the market's own, in today's money",
                v.goingRate() == lm.cashGoingRate() && v.floor() == lm.cashMinimumWage() && v.staged() == lm.cashAt(lm.getMinimumWage())
                && v.unskilledWage() == lm.getWage(JobType.NO_DIPLOMA) && v.stagedFloor() == lm.getMinimumWage());
        assertTrue("...and each over the going rate (the default floor is " + LabourMarket.DEFAULT_FLOOR_SHARE + " of it)",
                same(v.floorOverGoing(), v.floor() / v.goingRate()) && same(v.floorOverGoing(), LabourMarket.DEFAULT_FLOOR_SHARE)
                && same(v.stagedOverGoing(), v.staged() / v.goingRate()) && same(v.unskilledOverGoing(), v.unskilledWage() / v.goingRate()));
        assertTrue("the zones' edges are the market's: under MIN_MULTIPLE the floor never binds, over 1.0 it always does, and the gauge reaches 1.5",
                FloorView.NEVER_UNDER == LabourMarket.MIN_MULTIPLE && FloorView.ALWAYS_OVER == 1.0 && FloorView.GAUGE_TOP == 1.5
                && FloorView.zoneOf(LabourMarket.MIN_MULTIPLE - .01) == FloorView.Zone.NEVER && FloorView.zoneOf(LabourMarket.MIN_MULTIPLE + .01) == FloorView.Zone.GLUT
                && FloorView.zoneOf(1.0) == FloorView.Zone.GLUT && FloorView.zoneOf(1.01) == FloorView.Zone.ALWAYS && FloorView.zoneOf(Double.NaN) == null);
        assertTrue("the default floor is in the glut zone, and so is the floor staged at it", v.floorZone() == FloorView.Zone.GLUT && v.stagedZone() == FloorView.Zone.GLUT);

        // The zones are true of the market: the unskilled wage in a glut of hands and at par, against the market alone (the law shut), for a floor in each zone.
        double[] glutPosts = new double[WageBand.values().length], glutHands = new double[glutPosts.length], atPar = new double[glutPosts.length];
        Arrays.fill(glutPosts, 1);
        Arrays.fill(glutHands, 1000);
        Arrays.fill(atPar, 1000);
        boolean neverBinds = true, alwaysBinds = true, glutBinds = false, glutNotAtPar = false;
        for (boolean inGlut : new boolean[] { true, false }) {
            double[] marketPosts = inGlut ? glutPosts : atPar, marketHands = inGlut ? glutHands : atPar;
            LabourMarket alone = new LabourMarket(), under = new LabourMarket(), between = new LabourMarket(), over = new LabourMarket();
            alone.setFloorBinding(false);
            under.setMinimumWage(under.getGoingRate() * (FloorView.NEVER_UNDER - .01));
            between.setMinimumWage(between.getGoingRate() * (FloorView.NEVER_UNDER + FloorView.ALWAYS_OVER) / 2);
            over.setMinimumWage(over.getGoingRate() * (FloorView.ALWAYS_OVER + .01));
            for (int m = 0; m < 200; m++) {
                for (LabourMarket each : new LabourMarket[] { alone, under, between, over }) each.advanceMonth(marketPosts, marketHands);
            }
            double marketAlone = alone.getWage(JobType.NO_DIPLOMA);
            neverBinds &= under.getWage(JobType.NO_DIPLOMA) == marketAlone;
            alwaysBinds &= over.getWage(JobType.NO_DIPLOMA) > marketAlone;
            if (inGlut) glutBinds = between.getWage(JobType.NO_DIPLOMA) > marketAlone;
            else glutNotAtPar = between.getWage(JobType.NO_DIPLOMA) == marketAlone;
        }
        assertTrue("a floor under NEVER_UNDER changes no wage, in a glut of hands or at par; one over ALWAYS_OVER lifts the unskilled wage in both",
                neverBinds && alwaysBinds);
        assertTrue("...and one between them lifts the unskilled wage in a glut of hands and not at par: the glut zone", glutBinds && glutNotAtPar);

        double averageWage = pm.getAverageWage();
        double filledAll = filledWhere(g, job -> true);
        assertTrue("against the average wage: it is the Rankings' wage bill over the posts filled, the floor's and the staged floor's share of it",
                v.averageWage() == averageWage && averageWage > 0 && same(v.floorOverAverage(), lm.cashMinimumWage() / averageWage)
                && same(v.stagedOverAverage(), lm.cashAt(lm.getMinimumWage()) / averageWage));
        assertTrue("...and the low bands' share is the posts filled in the two lowest wage bands over the posts filled",
                same(v.lowBandsShare(), filledWhere(g, job -> WageBand.of(job).ordinal() < 2) / filledAll) && v.lowBandsShare() > 0 && v.lowBandsShare() < 1);

        // ---- the nations ----
        java.util.List<WorldMeasures.Place> withRatio = new java.util.ArrayList<>();
        for (WorldMeasures.Place nation : WorldMeasures.load().nations()) if (Double.isFinite(nation.value("kaitz"))) withRatio.add(nation);
        withRatio.sort(java.util.Comparator.comparingDouble((WorldMeasures.Place p) -> p.value("kaitz")).thenComparing(WorldMeasures.Place::id));
        int n = withRatio.size();
        assertTrue("the nations with a minimum-wage-to-average-wage figure are the world file's 33, lowest first: the hand count and the view's list agree",
                n == 33 && v.nations().size() == n);
        boolean sorted = true;
        for (int i = 0; i < n; i++) {
            FloorView.Nation shown = v.nations().get(i);
            sorted &= shown.id().equals(withRatio.get(i).id()) && shown.name().equals(withRatio.get(i).name()) && shown.kaitz() == withRatio.get(i).value("kaitz")
                    && (i == 0 || shown.kaitz() >= v.nations().get(i - 1).kaitz());
        }
        assertTrue("...each with its id, name and ratio from the file", sorted);
        assertTrue("the lowest is the United States and the highest Costa Rica, and Canada is named too",
                v.lowestNation().id().equals("USA") && v.highestNation().id().equals("CRI") && v.canada() != null && v.canada().id().equals(FloorView.CANADA)
                && v.canada().kaitz() == WorldMeasures.load().place(FloorView.CANADA).value("kaitz"));
        int lowAt = (int) Math.ceil(FloorView.BAND_LOW_PERCENTILE * n), highAt = (int) Math.ceil(FloorView.BAND_HIGH_PERCENTILE * n);   // nearest rank, 1-based
        int inside = 0;
        for (WorldMeasures.Place p : withRatio) {
            if (p.value("kaitz") >= withRatio.get(lowAt - 1).value("kaitz") && p.value("kaitz") <= withRatio.get(highAt - 1).value("kaitz")) inside++;
        }
        out.println("   the middle band of nations: " + v.bandLow().name() + " " + v.bandLow().kaitz() + " to " + v.bandHigh().name() + " " + v.bandHigh().kaitz() + ", " + inside + " of " + n);
        assertTrue("the middle band runs from the nation at the 10th percentile to the one at the 90th (nearest rank " + lowAt + " and " + highAt + " of " + n + "), and counts the nations inside it",
                FloorView.BAND_LOW_PERCENTILE == .10 && FloorView.BAND_HIGH_PERCENTILE == .90 && v.bandLow().id().equals(withRatio.get(lowAt - 1).id())
                && v.bandHigh().id().equals(withRatio.get(highAt - 1).id()) && v.nationsInBand() == inside && inside == highAt - lowAt + 1);

        // ---- what a full-time floor job pays for ----
        double takeHome = lm.cashMinimumWage() * (1 - g.getEconomyManager().getTaxPolicy().payslipShare(lowest));
        assertTrue("a full-time floor job: the take-home is the floor less the lowest band's payslip share, and rent, food and fees are the households' own",
                v.payslipShare() == g.getEconomyManager().getTaxPolicy().payslipShare(lowest) && same(v.takeHome(), takeHome)
                && v.rent() == hh.rentPerHousehold() && v.food() == hh.shoppingPerHead() && v.fees() == hh.feesPerHead());
        assertTrue("...their sum is what living alone costs, what is left is the take-home less it, and the cover is the floor job cover",
                same(v.costSum(), hh.costOfLivingAlone()) && same(v.costSum(), v.rent() + v.food() + v.fees()) && same(v.left(), takeHome - hh.costOfLivingAlone())
                && v.cover() == PolicyPreview.floorJobCover(g) && same(v.cover(), takeHome / hh.costOfLivingAlone()) && v.costSum() > 0);

        // ---- who is near it ----
        double[] paid = pm.workersPaidTheFloor(lm), near = pm.workersNearTheFloor(lm);
        boolean byBand = true;
        double paidSum = 0, nearSum = 0;
        for (WageBand band : WageBand.values()) {
            byBand &= v.paid(band) == paid[band.ordinal()] && v.near(band) == near[band.ordinal()];
            paidSum += paid[band.ordinal()];
            nearSum += near[band.ordinal()];
        }
        assertTrue("who is near it: the workers paid the floor and near it are the population's own, by band, and their totals are the sums",
                byBand && same(v.paidTotal(), paidSum) && same(v.nearTotal(), nearSum));
        assertTrue("...the posts filled are posts x fill rate summed over the jobs (within a person of getJobsFilled()), and paid + near + everyone else is all of them",
                same(v.postsFilled(), filledAll) && Math.abs(v.postsFilled() - pm.getJobsFilled()) <= .5
                && same(v.everyoneElse(), filledAll - paidSum - nearSum) && same(v.paidTotal() + v.nearTotal() + v.everyoneElse(), v.postsFilled()));
        assertTrue("the default floor pays nobody, so the words name the unskilled wage and write the money with the screen's writer, not their own",
                Math.round(v.paidTotal()) == 0 && v.holdsWords(money).equals("Nobody here is paid it: unskilled work goes for " + money.apply(lm.getWage(JobType.NO_DIPLOMA)) + ".")
                && !v.holdsWords(money).contains("$"));

        // ---- the tie ----
        assertTrue("the tie is the market's own, and entering the average wage now would hold the floor's share of it, held to the tie's range",
                v.tie() == lm.getFloorTie() && v.tieShare() == lm.getTieShare()
                && same(v.enteringShare(), Math.max(LabourMarket.MIN_TIE_SHARE, Math.min(LabourMarket.MAX_TIE_SHARE, lm.cashMinimumWage() / averageWage))));

        // ---- the preview with nothing staged, then the floor moved under every target ----
        PolicyPreview.FloorMove stay = PolicyPreview.floorMoveAt(g, lm.getMinimumWage());
        assertTrue("with nothing staged the floor moves no one: no job is marked, nobody lifted, no pay to average, the payroll unmoved",
                v.lifted() == 0 && stay.workers() == 0 && Double.isNaN(v.liftedPayNow()) && Double.isNaN(v.liftedPayThen()) && v.liftedShare() == 0
                && v.payrollMove() == 0 && v.jobs().stream().noneMatch(FloorView.JobRow::moved));
        FloorView lower = FloorView.of(g, lm.getMinimumWage() * .9);
        assertTrue("a floor staged under the one in force moves no one either, and its share is its own",
                lower.lifted() == 0 && lower.payrollMove() == 0 && lower.stagedOverGoing() < lower.floorOverGoing() && same(lower.stagedOverGoing(), lower.staged() / lower.goingRate()));

        // ---- the jobs, the floor staged 20% over the going rate ----
        double stagedUp = lm.floorForCash(1.2 * lm.cashGoingRate());
        FloorView up = FloorView.of(g, stagedUp);
        assertTrue("fixture: a floor staged at 1.2 of the going rate reads 1.2 of it, in the always zone, with the floor in force still in the glut zone",
                same(up.stagedOverGoing(), 1.2) && up.stagedZone() == FloorView.Zone.ALWAYS && up.floorZone() == FloorView.Zone.GLUT && up.staged() > up.floor());
        PolicyPreview.FloorMove move = PolicyPreview.floorMoveAt(g, stagedUp);
        long[] posts = pm.getJobs();
        double[] fill = pm.getJobFillRate();
        boolean rowsAreTheModels = up.jobs().size() == jobTypes.length, movedIsDiffer = true, aboveUnchanged = true, underLifted = true;
        double movedWorkers = 0;
        int above = 0, lifted = 0, noPosts = 0;
        for (JobType job : jobTypes) {
            int i = job.ordinal();
            FloorView.JobRow row = up.job(job);
            double now = lm.targetWageAt(job, lm.getMinimumWage()), then = lm.targetWageAt(job, stagedUp);
            rowsAreTheModels &= row.job() == job && row.posts() == posts[i] && same(row.filled(), posts[i] * fill[i]) && row.wage() == lm.getWage(job)
                    && row.targetNow() == now && row.targetStaged() == then && row.noPosts() == (posts[i] == 0);
            movedIsDiffer &= row.moved() == (now != then);
            if (row.moved()) movedWorkers += row.filled();
            if (now >= up.staged()) { aboveUnchanged &= !row.moved() && then == now; above++; }
            else { underLifted &= row.moved() && then == up.staged(); lifted++; }
            if (row.noPosts()) noPosts++;
            out.printf("   %-20s posts %,6d filled %,9.1f  wage %.5f  target %.5f -> %.5f %s%s%n", job, posts[i], row.filled(), row.wage(), now, then,
                    row.moved() ? "moved" : "", row.noPosts() ? "  (" + FloorView.NO_POSTS_WORDS + ")" : "");
        }
        assertTrue("per job: the posts, the posts filled, the wage and both targets are the model's own, in JobType order", rowsAreTheModels);
        assertTrue("fixture: the floor staged 20% over the going rate lifts some jobs and leaves some over it (" + lifted + " lifted, " + above + " over it)", lifted > 0 && above > 0);
        assertTrue("a job is marked moved exactly when its two targets differ", movedIsDiffer);
        assertTrue("every job under the staged floor is lifted to it, and every job over it is unchanged", underLifted && aboveUnchanged);
        assertTrue("the preview is floorMoveAt()'s: the workers lifted are the posts filled in the moved jobs, and their share, their average pay at the two targets and the payroll's move follow",
                up.lifted() == move.workers() && same(up.lifted(), movedWorkers) && up.lifted() > 0
                && same(up.liftedShare(), move.workers() / filledAll) && same(up.liftedPayNow(), move.payNow() / move.workers())
                && same(up.liftedPayThen(), move.payThen() / move.workers()) && up.liftedPayThen() > up.liftedPayNow()
                && same(up.payrollMove(), move.billThen() - move.billNow()) && up.payrollMove() > 0);
        assertTrue("...the payroll the page reads before and after is floorMoveAt()'s two bills, and the move is their difference",
                up.payrollNow() == move.billNow() && up.payrollThen() == move.billThen() && same(up.payrollMove(), up.payrollThen() - up.payrollNow()));

        // ---- a job with no posts ----
        assertTrue("fixture: the played city has a job with no posts, and one with posts", noPosts > 0 && noPosts < jobTypes.length);
        boolean noPostsRows = true;
        for (FloorView.JobRow row : up.jobs()) noPostsRows &= !row.noPosts() || (row.posts() == 0 && row.filled() == 0);
        assertTrue("a job with no posts reads noPosts with nothing filled, and its words are \"" + FloorView.NO_POSTS_WORDS + "\"",
                noPostsRows && FloorView.NO_POSTS_WORDS.equals("no posts here"));

        // ---- the joined market ----
        // GW1: the gate counts a year's draw (Sector.GATE_DRAW_MONTHS), so the played city's works stand sooner and its unskilled posts outrun its unskilled workers: diploma holders fill them and the lowest band is joined; the not-joined state is caused by handing the city unskilled workers (2% of the workforce a step), put back after.
        long workforceWas = pm.getWorkforceForSave(), handed = 0;
        try {
            for (int step = 0; step < 45 && pm.fillByBand().joinedAbove(lowest); step++) {
                handed += Math.max(1, Math.round(workforceWas * .02));
                pm.restoreWorkforce(workforceWas + handed);
            }
            FloorView own = FloorView.of(g, lm.getMinimumWage());
            assertTrue("fixture: with " + handed + " unskilled workers handed to the played city (GW1: its tighter market fills the unskilled posts with diploma holders at month "
                    + g.getMonth() + "), its lowest band is its own market", !pm.fillByBand().joinedAbove(lowest) && !own.joined());
            assertTrue("the joined market: the played city's lowest band is its own market, so there are no joined words and no shared wage",
                    !own.joined() && !pm.fillByBand().joinedAbove(lowest) && own.joinedWords(money) == null && Double.isNaN(own.joinedWage()));
        } finally {
            pm.restoreWorkforce(workforceWas);
        }
        double[] ownWorkers = pm.workforceByBand();
        double imprisonedWas = pm.getImprisoned();
        // The unskilled are away, so their posts have no one of their own to fill them; then, in steps, as many of the rest as it takes for the spare the bands
        // above carry down to fall short of those posts too (the fill joins the lowest band to the one above only while it is still hiring and out-pays it).
        double workforce = 0;
        for (double w : ownWorkers) workforce += w;
        double away = ownWorkers[lowest.ordinal()];
        pm.setImprisoned(imprisonedWas + away);
        for (int step = 0; step < 45 && !pm.fillByBand().joinedAbove(lowest) && away + workforce * .02 < workforce * .95; step++) {
            away += workforce * .02;
            pm.setImprisoned(imprisonedWas + away);
        }
        final double awayNow = away;
        try {
            FloorView joined = FloorView.of(g, lm.getMinimumWage());
            assertTrue("fixture: with the unskilled out of the supply and the spare above too few for their posts (" + Math.round(awayNow) + " of "
                            + Math.round(workforce) + " away), the lowest band is filled as one market with the diploma band above it",
                    pm.workforceByBand()[lowest.ordinal()] == 0 && pm.fillByBand().joinedAbove(lowest) && joined.joined());
            assertTrue("...so the words name the diploma holders in the unskilled posts and the one wage both pay, which is the unskilled wage",
                    joined.joinedWage() == lm.getWage(JobType.NO_DIPLOMA) && joined.joinedWords(money).equals(
                            "One market: " + WageBand.values()[lowest.ordinal() + 1].label().toLowerCase(Locale.ROOT) + " holders fill the unskilled posts, so both pay "
                                    + money.apply(lm.getWage(JobType.NO_DIPLOMA)) + "."));
        } finally {
            pm.setImprisoned(imprisonedWas);
        }

        // ---- the same floor applied: a twin loaded from a save, the floor set, a month on ----
        quietly(() -> g.saveGame(11, "policypreviewcheck-floor"));
        Game twin = new Game(new GameFiles(root.resolve("city"), root.resolve("city-no-legacy")));
        quietly(() -> twin.loadGameSave(11));
        LabourMarket twinLm = twin.getLabourMarket();
        twin.getEconomyManager().getTaxPolicy().openAll();
        twinLm.setMinimumWage(stagedUp);
        quietly(() -> twin.simulateMonths(1));
        FloorView applied = FloorView.of(twin, twinLm.getMinimumWage());
        double heldByHand = filledWhere(twin, twinLm::isPaidTheFloor);
        assertTrue("fixture: applied, the floor is paid to workers in the twin once a month has run (" + Math.round(heldByHand) + " of them), and the twin's real floor is still the one staged",
                Math.round(heldByHand) >= 1 && same(twinLm.getMinimumWage(), stagedUp));
        assertTrue("applied, the words count the workers paid the floor: " + Formats.INSTANCE.count(Math.round(heldByHand)),
                same(applied.paidTotal(), heldByHand) && applied.holdsWords(money).equals(
                        "Holds " + Formats.INSTANCE.count(Math.round(heldByHand)) + " " + (Math.round(heldByHand) == 1 ? "worker" : "workers") + ": they are paid the floor itself."));
        assertTrue("...and staging that floor again moves no one: it is the floor in force, so no job is marked",
                applied.lifted() == 0 && applied.jobs().stream().noneMatch(FloorView.JobRow::moved) && applied.floorZone() == FloorView.Zone.ALWAYS);

        // Who is near it: the floor set just under the cheapest college or university job that has posts, a month on, so that job is paid a little over it.
        JobType cheapestSkilled = null;
        for (JobType job : jobTypes) {
            if (WageBand.of(job).ordinal() < 2 || twin.getPopulationManager().getJobs()[job.ordinal()] == 0) continue;
            if (cheapestSkilled == null || twinLm.getWage(job) < twinLm.getWage(cheapestSkilled)) cheapestSkilled = job;
        }
        twinLm.setMinimumWage(twinLm.floorForCash(twinLm.getWage(cheapestSkilled) / (1 + LabourMarket.NEAR_FLOOR / 2)));
        quietly(() -> twin.simulateMonths(1));
        FloorView nearBy = FloorView.of(twin, twinLm.getMinimumWage());
        double nearByHand = filledWhere(twin, twinLm::isNearTheFloor), paidByHand = filledWhere(twin, twinLm::isPaidTheFloor);
        assertTrue("fixture: with the floor just under the cheapest skilled job's wage, some are paid it and some are a little over it (" + Math.round(paidByHand) + " and "
                        + Math.round(nearByHand) + ")", paidByHand > 0 && nearByHand > 0);
        assertTrue("...and paid + near + everyone else is still all of the posts filled",
                same(nearBy.nearTotal(), nearByHand) && same(nearBy.paidTotal(), paidByHand)
                && same(nearBy.paidTotal() + nearBy.nearTotal() + nearBy.everyoneElse(), nearBy.postsFilled()) && nearBy.everyoneElse() >= 0);
        boolean bandsAdd = true;
        for (WageBand band : WageBand.values()) bandsAdd &= same(nearBy.atOrNear(band), nearBy.paid(band) + nearBy.near(band));
        assertTrue("...the page's shares are each count over the posts filled, and a band's line is its paid and near together",
                bandsAdd && same(nearBy.paidShare(), nearBy.paidTotal() / nearBy.postsFilled()) && same(nearBy.nearShare(), nearBy.nearTotal() / nearBy.postsFilled())
                && same(nearBy.everyoneElseShare(), nearBy.everyoneElse() / nearBy.postsFilled()));

        // The floor over the average wage: the entering share is held to the tie's range, the words say why the city reads high, and a low floor reads nothing.
        double twinAverage = twin.getPopulationManager().getAverageWage();
        twinLm.setMinimumWage(twinLm.floorForCash(2 * twinAverage));
        FloorView high = FloorView.of(twin, twinLm.getMinimumWage());
        assertTrue("fixture: a floor at twice the average wage reads over it, and entering the average wage would hold its share at the top of the tie's range",
                high.floorOverAverage() > 1 && high.enteringShare() == LabourMarket.MAX_TIE_SHARE);
        assertTrue("a floor over the middle band's top reads high, and the words give the low bands' share of the posts and send the reader to the gauge against unskilled work",
                high.floorOverAverage() > high.bandHigh().kaitz() && high.readsHighWords() != null
                && high.readsHighWords().equals("This city reads high because " + Formats.INSTANCE.pct(high.lowBandsShare())
                        + " of its posts are unskilled or diploma posts, paid near the floor; the gauge against unskilled work is the one that says whether the floor bites."));
        twinLm.setMinimumWage(twinLm.getMinSettable());
        FloorView bottom = FloorView.of(twin, twinLm.getMinimumWage());
        assertTrue("fixture: the dial's bottom is under the tie's lowest share of the average wage, which is what entering the average wage would then hold",
                bottom.floorOverAverage() < LabourMarket.MIN_TIE_SHARE && bottom.enteringShare() == LabourMarket.MIN_TIE_SHARE);
        assertTrue("a floor under the middle band's top reads nothing extra",
                bottom.floorOverAverage() <= bottom.bandHigh().kaitz() && bottom.readsHighWords() == null);

        // ---- a city where nobody is employed ----
        Game empty = new Game(GameFiles.scratch("floorview"));
        quietly(empty::newGame);
        FloorView nobody = FloorView.of(empty, empty.getLabourMarket().getMinimumWage());
        assertTrue("fixture: a new city has nobody employed", empty.getPopulationManager().getJobsFilled() == 0 && empty.getPopulationManager().getAverageWage() == 0);
        assertTrue("with nobody employed the shares of the average wage and of the posts are NaN, the entering share is the default, and nobody is lifted or averaged",
                Double.isNaN(nobody.floorOverAverage()) && Double.isNaN(nobody.stagedOverAverage()) && Double.isNaN(nobody.lowBandsShare()) && Double.isNaN(nobody.liftedShare())
                && Double.isNaN(nobody.liftedPayNow()) && nobody.enteringShare() == LabourMarket.DEFAULT_TIE_SHARE && nobody.postsFilled() == 0
                && nobody.readsHighWords() == null);
    }

    /* ============================ 8. THE FUND'S WITHDRAWAL (0.7.48) ============================ */

    static void theWithdrawal(Game g) {
        out.println("\n--- 8. the fund's withdrawal: next month's due, and a year on earning nothing ---");
        double payIn = 100_000;
        g.setCashForTest(g.getCash() + payIn);
        quietly(() -> g.fundPayIn(payIn));
        quietly(() -> g.setFundWithdrawal(3 * TreasuryFund.WITHDRAWAL_STEP));
        TreasuryFund fund = g.getFund();
        assertTrue("fixture: a fund of cash alone, three steps a month, its year-end dial at nothing",
                fund.getCash() > 0 && g.fundSharesValue() == 0 && g.fundBondsValue() == 0 && g.fundPreferredValue() == 0
                && g.fundWarrantsValue() == 0 && fund.getWithdrawalSteps() == 3 && fund.getDial() == 0);
        PolicyPreview.Withdrawal w = PolicyPreview.fundWithdrawalAt(g, g.getFundWithdrawal());
        boolean asTheModel = w.due() == g.fundTransferDue();
        quietly(g::toggleNextMonth);
        assertTrue("at the withdrawal in force the preview's due is next month's", asTheModel
                && fund.getTransferDue() == w.due() && fund.getTransferPaid() == w.fromCash() && w.toSell() == 0 && w.unpaid() == 0);
        boolean yearOn = true;
        for (int steps : new int[] { 0, TreasuryFund.DEFAULT_WITHDRAWAL_STEPS, 3, TreasuryFund.MAX_WITHDRAWAL_STEPS }) {
            double rate = steps * TreasuryFund.WITHDRAWAL_STEP, value = g.fundValue();
            PolicyPreview.Withdrawal at = PolicyPreview.fundWithdrawalAt(g, rate);
            TreasuryFund bare = new TreasuryFund();
            bare.receive(value);
            bare.setWithdrawal(rate);
            for (int m = 0; m < TreasuryFund.YEAR_MONTHS; m++) bare.payTransfer(bare.getCash(), 2000);
            yearOn &= at.yearOn() == value * Math.pow(1 - rate, TreasuryFund.YEAR_MONTHS)
                    && Math.abs(at.yearOn() - bare.getCash()) <= 1e-12 * value;
        }
        assertTrue("a year on, earning nothing, is the value x (1 - rate)^12 - what twelve of its withdrawals leave", yearOn);
    }

    /* ============================ 5. AFTER A LOAD ============================ */

    static void afterALoad(Path root, Game g) {
        out.println("\n--- 5. after a load the take at the city's own policy is the saved month's ---");
        PolicyPreview.TaxTake was = PolicyPreview.taxTake(g, g.getEconomyManager().getTaxPolicy());
        double contributions = g.getEconomyManager().contributionsAt(g.getEconomyManager().getTaxPolicy().getContributionRate());
        double pool = g.getUnemployment().benefitsAt(g.getEconomyManager().getTaxPolicy().getEiBenefitRate());
        quietly(() -> g.saveGame(10, "policypreviewcheck"));
        Game twin = new Game(new GameFiles(root.resolve("city"), root.resolve("city-no-legacy")));
        quietly(() -> twin.loadGameSave(10));
        EconomyManager em = twin.getEconomyManager();
        TaxPolicy live = em.getTaxPolicy();
        PolicyPreview.TaxTake take = PolicyPreview.taxTake(twin, live);
        assertTrue("loaded, the take is the one struck before the save: profit, sales, wage and property",
                near(take.profitTotal() - take.bank(), was.profitTotal() - was.bank()) && near(take.salesTotal(), was.salesTotal())
                && near(take.wageTotal(), was.wageTotal()) && near(take.propertyTotal(), was.propertyTotal()));
        assertTrue("...the bank's next bill", near(take.bank(), was.bank()));
        assertTrue("...the contribution off the wage bill", near(em.contributionsAt(live.getContributionRate()), contributions));
        assertTrue("...and the EI bill on the pool", near(twin.getUnemployment().benefitsAt(live.getEiBenefitRate()), pool));
    }
    /* ============================ 9. ZONING'S BEFORE AND AFTER (0.7.107) ============================ */

    static void zoning(Game g) {
        out.println("\n--- 9. zoning's before and after: staged on a copy, applied, the preview is the model ---");
        LandManager land = g.getLandManager();
        // Ground with forest in it, so the law has something to keep: the city's ground drawn to 80 km2.
        quietly(() -> land.setOwnedSqFt(LandManager.sqFt(80)));
        Zoning live = g.getZoning();
        assertTrue("fixture: the city's ground holds forest, and it has no zone and no law",
                land.getForestSqFt() > 0 && live.isEmpty());
        PolicyPreview.ZoneAt none = PolicyPreview.zoneAt(g, live, Sectors.RESTAURANTS);
        assertTrue("at the city's own zoning a sector's zone is nothing, and the tax moves by nothing",
                none.heldSqFt() == 0 && none.taxRelief() == 0 && PolicyPreview.zoningTaxChange(g, live.copy()) == 0);
        Zoning staged = live.copy();
        staged.set(Sectors.RESTAURANTS, Zoning.Size.KM2, 1, .25);
        staged.setForestLaw(.2, .5);
        PolicyPreview.ZoneAt then = PolicyPreview.zoneAt(g, staged, Sectors.RESTAURANTS);
        PolicyPreview.ForestAt forestThen = PolicyPreview.forestAt(g, staged);
        PolicyPreview.ForestAt forestNow = PolicyPreview.forestAt(g, live);
        assertTrue("the copy is detached: the city holds no zone and keeps no forest, and its decisions are untouched",
                g.getZoning().isEmpty() && land.heldByZones() == 0 && forestNow.protectedSqFt() == 0
                && forestNow.freeForAnyone() == land.getAvailableSqFt());
        int decided = g.getDecisions().entries().size();
        g.setZone(Sectors.RESTAURANTS, Zoning.Size.KM2, 1, .25);
        g.setForestLaw(.2, .5);
        double going = land.getPricePerSqFt(), held = land.heldBy(Sectors.RESTAURANTS);
        assertTrue("applied, the zone holds what the preview said, at the size it said",
                then.heldSqFt() == held && then.sizeSqFt() == land.zoneSizeOf(Sectors.RESTAURANTS) && held > 0
                && g.getDecisions().entries().size() == decided + 2);
        assertTrue("...what that ground would fetch at the going price, and the price given up selling it at the zone's",
                near(then.heldAtGoingPrice(), held * going)
                && near(then.priceGivenUp(), land.priceFor(held) - land.priceFor(held, Sectors.RESTAURANTS)));
        assertTrue("...the forest's three kinds, the wood kept and the wood that may be cut, and the ground free for anyone",
                forestThen.protectedSqFt() == land.getProtectedForestSqFt() && forestThen.workingSqFt() == land.getWorkingForestSqFt()
                && forestThen.openSqFt() == land.getOpenForestSqFt() && forestThen.freeForAnyone() == land.getAvailableSqFt()
                && forestThen.protectedWood() == land.getProtectedWood() && forestThen.cuttableWood() == land.cuttableForest()
                && Math.abs(forestThen.standsForEver() - .2) <= 1e-12);

        // A zone with ground built in it: its relief is the tax un-zoning would raise.
        Sector restaurants = g.getSectors().byKey(Sectors.RESTAURANTS);
        restaurants.addCash(1_000_000);
        g.buildFor(g.getSectorInvestor(Sectors.RESTAURANTS), LongPlaytest.template(g, "Diner"), 3);
        PolicyPreview.ZoneAt built = PolicyPreview.zoneAt(g, g.getZoning(), Sectors.RESTAURANTS);
        Zoning off = g.getZoning().copy();
        off.remove(Sectors.RESTAURANTS);
        double withZone = g.getEconomyManager().getPropertyTaxFor(restaurants);
        double moved = PolicyPreview.zoningTaxChange(g, off);
        g.removeZone(Sectors.RESTAURANTS);
        double without = g.getEconomyManager().getPropertyTaxFor(restaurants);
        out.printf("      the zone's relief a month %s; the preview's move %s, the move applied %s%n",
                Formats.INSTANCE.amount(built.taxRelief()), Formats.INSTANCE.amount(moved), Formats.INSTANCE.amount(without - withZone));
        assertTrue("fixture: three diners stand in the zone, so the zone gives relief", built.usedSqFt() > 0 && built.taxRelief() > 0);
        assertTrue("un-zoning previews the property tax rising by the relief, and applied it does",
                near(moved, built.taxRelief()) && near(without - withZone, built.taxRelief()));
    }

    /* ============================ 10. THE PORT DUES (FR4) ============================ */

    /** A city with a Bulk Terminal handed to it and three months played on, so a berth has worked and Freight has billed its tonnes (LandlordPortCheck.seaBase()'s recipe). */
    static Game portCity(String label) {
        Game g = HaulageCheck.played(GameFiles.scratch(label), 14);
        quietly(() -> {
            HaulageCheck.lay(g, "Bulk Terminal", 1);
            for (int i = 0; i < 3; i++) g.toggleNextMonth();
        });
        return g;
    }

    static void portDues() {
        out.println("\n--- 10. the port dues: last month's tonnes at each kind's due, times the dial (FR4) ---");
        Game one = portCity("policypreview-ports-1"), two = portCity("policypreview-ports-2");
        quietly(() -> {
            two.getEconomyManager().getTaxPolicy().setPortDues(2);
            one.toggleNextMonth();
            two.toggleNextMonth();
        });
        EconomyManager em = one.getEconomyManager();
        TaxPolicy live = em.getTaxPolicy();
        ham.citybuildersim.sectors.PortDivision pd = one.getSectors().freight().portDivision();
        ham.citybuildersim.sectors.PortDivision pd2 = two.getSectors().freight().portDivision();
        double share = one.getEducation().getTuitionSubsidy(), dues = pd.getDuesPaid();
        Ports.Cargo bulk = Ports.Cargo.DRY_BULK;
        double[] berths = Ports.berths(one.getBuildingManager());

        boolean berthsMatch = true;
        for (Ports.Cargo kind : Ports.Cargo.values()) berthsMatch &= PolicyPreview.hasBerth(one, kind) == (berths[kind.ordinal()] > 0);
        out.printf("      the city: %,.0f t across the berths last month, D$%,.4fk of dues at the dial %.2f; the twin at %.2f paid %,.4fk%n",
                PolicyPreview.portTonnes(one), dues, live.getPortDues(), two.getEconomyManager().getTaxPolicy().getPortDues(), pd2.getDuesPaid());
        assertTrue("fixture: a berth stands and tonnes crossed it, the treasury took dues at the default dial, and a kind has a berth to charge on only if one stands",
                PolicyPreview.portTonnes(one) > 0 && dues > 0 && em.getPortDues() > 0 && live.getPortDues() == TaxPolicy.DEFAULT_PORT_DUES
                        && berths[bulk.ordinal()] > 0 && berthsMatch);
        boolean alike = pd.isQuoteKnown(bulk) && Math.abs(pd2.getCrewQuote(bulk) - pd.getCrewQuote(bulk)) <= 1e-9;
        for (Ports.Cargo kind : Ports.Cargo.values()) {
            alike &= pd2.getTonnes(kind) == pd.getTonnes(kind)
                    && Math.abs(pd2.getDuesRate(kind) - pd.getDuesRate(kind)) <= 1e-12 * Math.max(1e-9, pd.getDuesRate(kind));
        }
        assertTrue("fixture: the twin ran the same month at the dial 2 and is alike but for it - the same tonnes, the same default due, the same crew quote - and paid dues",
                alike && two.getEconomyManager().getTaxPolicy().getPortDues() == 2 && pd2.getDuesPaid() > 0);

        assertTrue("at the city's own dial the dues previewed are the month's, to the bit: Freight's, the treasury's and the accounts'",
                PolicyPreview.portDuesAt(one, live) == dues && dues == em.getPortDues()
                        && dues == em.getNationalAccounts().getPortDues());
        TaxPolicy double2 = live.copy(), none = live.copy();
        double2.setPortDues(2);
        none.setPortDues(0);
        assertTrue("at the dial 2 the dues previewed are twice the month's, and the twin that ran the month at 2 paid them, to the cent",
                PolicyPreview.portDuesAt(one, double2) == 2 * dues && near(PolicyPreview.portDuesAt(one, double2), pd2.getDuesPaid())
                        && near(PolicyPreview.portDuesAt(one, double2), two.getEconomyManager().getPortDues()));
        assertTrue("at the dial 0 they are none, and the budget falls by the month's dues",
                PolicyPreview.portDuesAt(one, none) == 0 && near(PolicyPreview.change(one, live, none, share, share), -dues));
        assertTrue("at 2 the budget rises by them, the dues' move alone, and a set that moves no dial moves it by nothing",
                near(PolicyPreview.change(one, live, double2, share, share), dues)
                        && PolicyPreview.change(one, live, live.copy(), share, share) == 0);

        double rate = pd.getDuesRate(bulk);
        assertTrue("a tonne's handling at the city's dial is the division's quote, to the bit; at 2 it is dearer by the kind's default due, and the twin billed at 2 quotes it",
                PolicyPreview.handlingQuoteAt(one, bulk, live) == pd.getQuote(bulk) && rate > 0
                        && Math.abs(PolicyPreview.handlingQuoteAt(one, bulk, double2) - PolicyPreview.handlingQuoteAt(one, bulk, live) - rate) <= 1e-9
                        && Math.abs(PolicyPreview.handlingQuoteAt(one, bulk, double2) - pd2.getQuote(bulk)) <= 1e-9
                        && Math.abs(PolicyPreview.handlingQuoteAt(one, bulk, none) - pd.getCrewQuote(bulk)) <= 1e-9);
        TaxPolicy wild = live.copy();
        wild.setPortDues(99);
        boolean top = wild.getPortDues() == TaxPolicy.MAX_PORT_DUES;
        wild.setPortDues(-1);
        assertTrue("the copy's dial clamps as the city's: past the top is the top, below none is none, and the city's own dial stays where it was",
                top && wild.getPortDues() == 0 && live.getPortDues() == TaxPolicy.DEFAULT_PORT_DUES);
    }
}

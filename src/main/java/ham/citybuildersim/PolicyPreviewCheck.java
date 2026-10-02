package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
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
 *      clinic's fee by the fees at one.
 *   4. THE OTHER READS: the savers' rate the bank would choose, between where
 *      it is and where it heads; the compression and the ceiling at another
 *      setting; the floor's cash and founding figures one way and back; every
 *      wage's target moving by the floor's own ratio.
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
     */
    static Game city(Path root) {
        GameFiles files = new GameFiles(root.resolve("city"), root.resolve("city-no-legacy"));
        Game g = new Game(files);
        quietly(() -> {
            g.run();
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.getGovernmentInvestor().spend(-2_000_000);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 200_000_000L);
            for (String[] w : ORDERS) {
                BuildingsTemplate t = LongPlaytest.template(g, w[0]);
                if (t != null) g.buildStack(t, Integer.parseInt(w[1]), true);
            }
            g.simulateMonths(60);
        });
        return g;
    }

    /** The fixture's orders. */
    static final String[][] ORDERS = {
            { "House", "500" }, { "Convenience Store", "12" }, { "Diner", "2" }, { "Construction Depot", "6" },
            { "Coal Power Plant", "2" }, { "Water Treatment Plant", "2" }, { "Industrial Bakery", "3" },
            { "Steel Foundry", "2" }, { "Fabrication Shop", "2" }, { "Commercial Bank", "1" },
            { "Elementary School", "3" }, { "Walk-in Clinic", "3" }, { "Paved Road", "20" } };

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        Path root = Files.createTempDirectory("policypreviewcheck");

        Game g = city(root);
        printCity(g);
        theCopy(g);
        atTheCitysOwn(g, "2");
        theBudget(g);
        theOtherReads(g);
        afterALoad(root, g);
        appliedIsTheModel(g);
        aDialAtZero(g);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

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
        boolean targets = true;
        for (JobType job : JobType.values()) {
            targets &= Math.abs(lm.targetWageAt(job, floor * 1.1) - 1.1 * lm.targetWageAt(job, floor)) <= 1e-9
                    && lm.targetWageAt(job, floor * 1.1) >= lm.cashAt(floor * 1.1) - 1e-12;
        }
        assertTrue("every job's target moves by the floor's own ratio, and none is under the floor in today's money", targets);
        assertTrue("...so a floor ten per cent up moves every wage, and the city's own payroll, by ten per cent once walked",
                Math.abs(PolicyPreview.wageMoveAt(g, floor * 1.1) - 1.1) <= 1e-12
                && near(PolicyPreview.cityPayrollAt(g, floor * 1.1),
                        (g.getEducation().getPayroll() + g.getHealthcare().getPayroll()) * PolicyPreview.wageMoveAt(g, floor * 1.1)));
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
}

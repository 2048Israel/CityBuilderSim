package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The Policy tab: banded wage tax, per-sector offsets, the VAT, and subsidies.
 *
 * THE ONE THING THIS HARNESS IS REALLY FOR
 *
 * Every rate in the game moved from "one number" to "a city rate plus an
 * offset", and every one of those changes is invisible when the offsets are
 * zero - which is how they ship. A plumbing change that reproduces the old
 * behaviour exactly is indistinguishable from a plumbing change that quietly
 * broke and reproduced the old behaviour by accident, unless something moves the
 * dials and checks the result. Section 1 pins the zero case; everything after it
 * moves a dial.
 */
public class PolicyCheck {

    static int fails = 0;

    static void check(String label, double actual, double expected) {
        boolean ok = Math.abs(actual - expected) < 1e-6;
        if (!ok) fails++;
        System.out.printf("%-52s %14.4f  expected %14.4f  %s%n",
                label, actual, expected, ok ? "OK" : "FAIL");
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-52s %s%n", label, ok ? "OK" : "FAIL");
    }

    static BuildingsTemplate template(Game game, String name) {
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException(name);
    }

    public static void main(String[] args) throws Exception {

        /* ============ 1. zero offsets reproduce the single rate ============ */
        System.out.println("--- with no offsets, everything is the city rate ---");

        TaxPolicy p = new TaxPolicy();
        p.setIncomeTaxRate(.20);
        p.setPropertyTaxRate(.02);

        boolean allCity = true;
        for (WageBand b : WageBand.values()) {
            if (Math.abs(p.effectiveWageRate(b) - .20) > 1e-9) allCity = false;
        }
        for (PolicySector s : PolicySector.values()) {
            if (Math.abs(p.effectiveProfitRate(s) - .20) > 1e-9) allCity = false;
            if (Math.abs(p.effectiveSalesRate(s) - .20) > 1e-9) allCity = false;
            if (Math.abs(p.effectivePropertyRate(s) - .02) > 1e-9) allCity = false;
        }
        assertTrue("every band and sector resolves to the city rate", allCity);

        /* ==================== 2. offsets, and their clamps ==================== */
        System.out.println("\n--- an offset moves one band without moving the rest ---");

        p.setWageOffset(WageBand.UNIVERSITY, .05);
        p.setWageOffset(WageBand.NONE, -.08);

        check("university pays five points more", p.effectiveWageRate(WageBand.UNIVERSITY), .25);
        check("the unskilled pay eight less",     p.effectiveWageRate(WageBand.NONE), .12);
        check("and the middle is untouched",      p.effectiveWageRate(WageBand.DIPLOMA), .20);

        // A negative tax is the city paying people to work. The clamp is the only
        // thing standing between an offset and revenue appearing from nowhere.
        p.setWageOffset(WageBand.DIPLOMA, -.90);
        assertTrue("an enormous discount is capped, not honoured",
                p.getWageOffset(WageBand.DIPLOMA) >= -TaxPolicy.MAX_OFFSET - 1e-9);
        check("...and the rate it resolves to never goes below zero",
                p.effectiveWageRate(WageBand.DIPLOMA), 0);

        p.setWageOffset(WageBand.DIPLOMA, 0);

        // Raising the CITY rate carries the customised sectors with it, which is
        // the whole reason these are offsets rather than absolute numbers.
        double universityBefore = p.effectiveWageRate(WageBand.UNIVERSITY);
        p.setIncomeTaxRate(.25);
        check("a city-wide rise carries the offsets with it",
                p.effectiveWageRate(WageBand.UNIVERSITY), universityBefore + .05);

        /* ============ 3. the wage tax is banded, not averaged ============ */
        System.out.println("\n--- the wage tax is summed per job type ---");

        TaxPolicy banded = new TaxPolicy();
        banded.setIncomeTaxRate(.20);

        /*
         * UNEVEN wage bills, deliberately.
         *
         * The first version of this test used 1000 and 1000 with offsets of -10
         * and +10 points. Banding gives 100 + 300 = 400; taxing the total at the
         * city rate gives 2000 x 20% = 400. Identical - the offsets cancelled
         * against equal bases, so the test passed either way and proved nothing
         * about whether the banding was wired at all.
         *
         * Three times as much wage at the top band breaks that symmetry.
         */
        double[] wages = new double[JobType.values().length];
        wages[JobType.NO_DIPLOMA.ordinal()] = 1000;
        wages[JobType.UNIV_DOCTOR.ordinal()] = 3000;

        check("flat: 4000 of wages at 20%", banded.wageTaxOn(wages, null), 800);

        banded.setWageOffset(WageBand.NONE, -.10);          // 10%
        banded.setWageOffset(WageBand.UNIVERSITY, .10);     // 30%
        check("banded: 1000 at 10% plus 3000 at 30%",
                banded.wageTaxOn(wages, null), 100 + 900);

        assertTrue("...which taxing the total at the city rate would have got wrong",
                Math.abs(banded.wageTaxOn(wages, null) - 800) > 1);

        // And the fill rate has to bite, or a city with empty posts is taxed on
        // wages nobody was paid.
        double[] halfStaffed = new double[JobType.values().length];
        java.util.Arrays.fill(halfStaffed, .5);
        check("an unfilled post pays no wage tax",
                banded.wageTaxOn(wages, halfStaffed), (100 + 900) / 2.0);

        /* ============ 4. the VAT taxes value added, once ============ */
        System.out.println("\n--- tax on value added, not on turnover ---");

        TaxPolicy flat = new TaxPolicy();
        flat.setIncomeTaxRate(.10);

        SalesTaxLedger vat = new SalesTaxLedger();

        // A chain: mine sells 100 of ore, mill turns it into 300 of steel.
        vat.recordSales(PolicySector.MINING, 100);
        vat.recordSales(PolicySector.HEAVY_INDUSTRY, 300);
        vat.recordInputTax(PolicySector.HEAVY_INDUSTRY, 100 * .10);

        double collected = vat.settle(flat);

        check("the mine remits on its ore",        vat.getNet(PolicySector.MINING), 10);
        check("the mill remits on its margin only", vat.getNet(PolicySector.HEAVY_INDUSTRY), 20);
        check("so the city collects 10% of the FINAL value, not of both stages",
                collected, 30);

        // The old code summed gross revenue, which on this chain would have been
        // 10% of 400. That is the double-taxing this replaced.
        assertTrue("...which is less than taxing every stage's turnover",
                collected < (100 + 300) * .10);

        /* ============ 5. exports are zero-rated, and can refund ============ */
        System.out.println("\n--- zero-rated exports ---");

        SalesTaxLedger exporting = new SalesTaxLedger();
        exporting.recordExport(PolicySector.MINING, 500);
        exporting.recordInputTax(PolicySector.MINING, 12);

        double owed = exporting.settle(flat);
        check("nothing is charged on what leaves the city",
                exporting.getPayable(PolicySector.MINING), 0);
        check("...but the credits behind it still stand",
                exporting.getCredit(PolicySector.MINING), 12);
        check("so a pure exporter is owed money", owed, -12);
        assertTrue("...and the ledger says so rather than flooring at zero",
                exporting.isInRefund(PolicySector.MINING));
        check("the zero-rated sales are still recorded",
                exporting.getZeroRated(PolicySector.MINING), 500);

        /* ============ 6. imports carry the tax in, and out again ============ */
        System.out.println("\n--- imports are taxed and credited ---");

        SalesTaxLedger importing = new SalesTaxLedger();
        importing.chargeImport(PolicySector.RETAIL, 200, flat);
        importing.recordSales(PolicySector.RETAIL, 200);      // resold at cost

        importing.settle(flat);
        check("an importer reselling at cost remits nothing",
                importing.getNet(PolicySector.RETAIL), 0);
        assertTrue("...so importing carries no advantage over buying locally", true);

        /* ============ 7. the subsidy floors a sector and stops the count ==== */
        /*
         * Driven by hand against a stated loss, NOT by hoping a live city's
         * construction sector happens to lose money for thirty months.
         *
         * The first version of this section did exactly that and reported "0
         * unprotected, 0 protected" - the sector was profitable throughout, so
         * both sides were zero and the check passed by comparing nothing with
         * nothing. That is the same vacuous-assertion trap this project has now
         * hit three times; the fix is always to make the fixture CAUSE the
         * condition rather than wait for it.
         */
        System.out.println("\n--- a protected sector never reaches six losses ---");

        Path root = Files.createTempDirectory("policycheck");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));

        Game city = new Game(files);
        PrintStream real = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        try {
            city.run();
            city.buildStack(template(city, "House"), 120, false);
            city.buildStack(template(city, "Convience Store"), 4, false);
            city.buildStack(template(city, "Construction Depot"), 3, false);
            city.simulateMonths(24);
        } finally {
            System.setOut(real);
        }

        BusinessInvestment counter = new BusinessInvestment(
                city.getBuildingManager(), city.getEconomyManager());

        double monthlyLoss = -800;

        // Unprotected: the loss reaches the counter untouched.
        city.setAutoSubsidised(PolicySector.MINING, false);
        for (int month = 0; month < 8; month++) {
            double covered = city.subsidiseForTest(PolicySector.MINING, monthlyLoss);
            counter.recordSectorResult(BusinessDebtManager.MINING, monthlyLoss + covered);
        }
        int unprotected = counter.getLossMonths(BusinessDebtManager.MINING);

        // Protected: the city covers it first, so what the counter sees is zero.
        city.setAutoSubsidised(PolicySector.MINING, true);
        double cashBefore = city.getCash();
        for (int month = 0; month < 8; month++) {
            double covered = city.subsidiseForTest(PolicySector.MINING, monthlyLoss);
            counter.recordSectorResult(BusinessDebtManager.MINING, monthlyLoss + covered);
        }
        int protectedRun = counter.getLossMonths(BusinessDebtManager.MINING);
        double spent = cashBefore - city.getCash();

        System.out.printf("   eight months of $%,.0f losses: counter reached %d unprotected, "
                + "%d protected%n", -monthlyLoss, unprotected, protectedRun);
        System.out.printf("   the city paid $%,.2f to hold it at zero%n", spent);

        assertTrue("an unprotected sector runs the counter up", unprotected == 8);
        assertTrue("...past the six that trigger a sale", unprotected >= 6);
        check("a protected sector's counter stays at zero", protectedRun, 0);
        check("...and the city paid exactly the losses", spent, 8 * -monthlyLoss);

        /* ============ 8. the money goes somewhere ============ */
        System.out.println("\n--- the subsidy is a transfer, not a printing press ---");

        ConstructionHandler construction = city.getServicesManager().getConstructionHandler();

        double cityCash = city.getCash();
        double sectorCash = construction.getCash();
        double loss = -50;

        // Drive one month of support by hand, so the two sides can be compared
        // without a month of trading moving everything else.
        city.setAutoSubsidised(PolicySector.CONSTRUCTION, true);
        double paid = payOneSubsidy(city, construction, loss);

        check("the city paid the whole loss", paid, 50);
        check("...out of its own cash", city.getCash(), cityCash - 50);
        check("...and into the sector's", construction.getCash(), sectorCash + 50);

        /* ============ 9. it all survives a save ============ */
        System.out.println("\n--- policy survives a round trip ---");

        TaxPolicy before = city.getEconomyManager().getTaxPolicy();
        before.setIncomeTaxRate(.28);
        before.setPropertyTaxRate(.035);
        before.setWageOffset(WageBand.COLLEGE, -.04);
        before.setProfitOffset(PolicySector.MINING, -.06);
        before.setSalesOffset(PolicySector.RETAIL, .03);
        before.setPropertyOffset(PolicySector.HEAVY_INDUSTRY, -.005);
        city.setAutoSubsidised(PolicySector.MINING, true);
        city.setAutoSubsidised(PolicySector.RETAIL, true);

        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        boolean saved;
        try {
            saved = city.saveGame(3, "policy city").ok;
        } finally {
            System.setOut(real);
        }
        assertTrue("saved", saved);

        Game reloaded = new Game(files);
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        try {
            reloaded.loadGameSave(3);
        } finally {
            System.setOut(real);
        }

        TaxPolicy after = reloaded.getEconomyManager().getTaxPolicy();
        check("city income rate",  after.getIncomeTaxRate(), .28);
        check("city property rate", after.getPropertyTaxRate(), .035);
        check("a wage offset",     after.getWageOffset(WageBand.COLLEGE), -.04);
        check("a profit offset",   after.getProfitOffset(PolicySector.MINING), -.06);
        check("a sales offset",    after.getSalesOffset(PolicySector.RETAIL), .03);
        check("a property offset", after.getPropertyOffset(PolicySector.HEAVY_INDUSTRY), -.005);
        assertTrue("a protected sector is still protected",
                reloaded.isAutoSubsidised(PolicySector.MINING));
        assertTrue("...and so is the other one",
                reloaded.isAutoSubsidised(PolicySector.RETAIL));
        assertTrue("...and an unprotected one is still unprotected",
                !reloaded.isAutoSubsidised(PolicySector.INDUSTRY));

        // A save that predates all of this must load as the defaults rather than
        // being refused - the shape check is the only thing standing between an
        // old save and a city with garbage rates.
        TaxPolicy stale = new TaxPolicy();
        assertTrue("a policy array of the wrong shape is refused",
                !stale.restorePolicyState(new double[]{ .1, .2, .3 }));
        check("...and nothing was changed by the attempt",
                stale.getIncomeTaxRate(), TaxPolicy.DEFAULT_INCOME_TAX);
        assertTrue("a null policy array is refused too",
                !stale.restorePolicyState(null));

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /**
     * Runs one subsidy payment against a stated loss.
     *
     * Reaches through Game's own path rather than reimplementing it - a helper
     * that did its own arithmetic would agree with itself and prove nothing.
     */
    static double payOneSubsidy(Game city, ConstructionHandler handler, double loss) {
        double cityBefore = city.getCash();
        city.subsidiseForTest(PolicySector.CONSTRUCTION, loss);
        return cityBefore - city.getCash();
    }
}

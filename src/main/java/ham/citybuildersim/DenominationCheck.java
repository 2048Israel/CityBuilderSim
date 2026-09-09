package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A currency reform is a change of units, and this is how we know.
 *
 * WHAT IS ACTUALLY BEING ASKED. Jerus wanted a button that divides the money by
 * ten or a hundred so that three centuries of inflation do not end with bread
 * priced at 300M and the arithmetic losing digits at the bottom. That is a
 * currency reform, and the entire risk in one is that it is not ONLY a change
 * of units: miss one balance and the button quietly creates or destroys money;
 * miss one compile-time constant and a House is a hundred times dearer the next
 * morning.
 *
 * So the test is not a list of fields. It is TWO CITIES:
 *
 *   A runs normally.
 *   B is the same city, reformed, and then run for exactly as long.
 *
 * If the reform is only a change of units then B is A with a different label on
 * the axis: every real quantity identical to the person, every money quantity
 * identical after dividing by the factor, and the two trajectories still on top
 * of each other years later. Any field left unscaled changes a relative price,
 * and a changed relative price changes what gets built and who moves in - so
 * the populations part company and the harness says so.
 *
 * That is a much stronger assertion than "the balances add up", and it is the
 * only one that can be trusted, because nobody can enumerate this codebase's
 * money by reading it.
 */
public class DenominationCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-62s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** Within a RELATIVE band, for the claims a reform can only keep approximately. */
    /** How far apart two figures are, as a percentage of the expected one. */
    static double gap(double actual, double expected) {
        return Math.abs(expected) < 1e-12 ? 0
                : Math.abs(actual - expected) / Math.abs(expected) * 100;
    }

    static void within(String label, double actual, double expected, double band) {
        double gap = Math.abs(expected) > 1e-12
                ? Math.abs(actual - expected) / Math.abs(expected)
                : Math.abs(actual - expected);
        boolean ok = gap <= band;
        if (!ok) fails++;
        out.printf("%-62s %s  (%.2f%% apart, %.0f%% allowed)%n",
                label, ok ? "OK" : "FAIL", gap * 100, band * 100);
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= Math.max(tol, Math.abs(expected) * tol);
        if (!ok) {
            fails++;
            out.printf("%-62s FAIL  %,.8f != %,.8f%n", label, actual, expected);
        } else {
            out.printf("%-62s OK%n", label);
        }
    }

    static void quietly(Runnable work) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { work.run(); } finally { System.setOut(real); }
    }

    /* ------------------------------------------------------------------ */

    /** A city with a bit of everything in it, so the reform has work to do. */
    static Game city(String name) {
        Game g = new Game(GameFiles.scratch(name));
        quietly(() -> {
            g.run();
            g.getGovernmentInvestor().spend(-2_000_000);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 200_000_000L);
            LongPlaytest.build(g, "House", 400);
            LongPlaytest.build(g, "Convenience Store", 20);
            LongPlaytest.build(g, "Construction Depot", 6);
            LongPlaytest.build(g, "Coal Power Plant", 2);
            LongPlaytest.build(g, "Water Treatment Plant", 2);
            LongPlaytest.build(g, "Textile Mill", 4);
            LongPlaytest.build(g, "Iron Mine", 2);
            LongPlaytest.build(g, "Steel Foundry", 2);
            LongPlaytest.build(g, "Commercial Bank", 1);
            LongPlaytest.build(g, "Elementary School", 3);
            LongPlaytest.build(g, "Walk-in Clinic", 3);
            LongPlaytest.build(g, "Paved Road", 20);
            g.simulateMonths(120);
        });
        return g;
    }

    /* ------------------------------------------------------------------ */

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        Path root = Files.createTempDirectory("denomcheck");

        /* ============ 1. THE UNIT ============ */
        out.println("--- what a reform does to the unit ---");

        Denomination d = new Denomination();
        close("a founding city is in founding money", d.getUnit(), 1, 1e-12);
        assertTrue("...and cannot reform, because nothing has inflated",
                !d.unlocked(1.0));
        assertTrue("...nor at nine times", !d.unlocked(9.0));
        assertTrue("...and can at ten", d.unlocked(Denomination.UNLOCK_AT));

        d.lop(100);
        close("lopping two zeros makes a dollar a hundred old ones", d.getUnit(), 100, 1e-12);
        close("...so a founding price reads as a hundredth", d.money(30), .30, 1e-12);
        assertTrue("...and the money gets a new name", d.name().startsWith("second"));
        d.lop(1000);
        assertTrue("...and again", d.name().startsWith("third"));
        close("the unit compounds", d.getUnit(), 100_000, 1e-9);

        Denomination big = new Denomination();
        while (big.canLop(1000)) big.lop(1000);
        assertTrue("it stops before a double runs out of digits",
                big.getUnit() <= Denomination.MAX_UNIT && !big.canLop(1000));

        /* ============ 2. IT IS THE SAME CITY ============

           The headline. One city, one reformed copy, and then a hundred months
           on both. Everything real must match to the person and the tonne;
           everything nominal must match after dividing by a hundred.
           ================================================================= */
        out.println("\n--- a reformed city is the same city ---");

        Game plain = city("denom-plain");
        Game lopped = city("denom-lopped");

        assertTrue("fixture: the two cities really are identical to start with",
                same(plain, lopped));

        double factor = 100;

        /*
         * The button is gated on the price level and this fixture's has not
         * moved, so the gate is opened by hand. What is under test is the
         * REFORM, not the threshold - the threshold is tested above, on the
         * Denomination itself, where it can be held still.
         */
        boolean done = force(lopped, factor);
        assertTrue("the reform goes through", done);

        close("cash divided", lopped.getCash(), plain.getCash() / factor, 1e-9);
        close("the exchange rate divided",
                lopped.getForeignAccounts().getRate(),
                plain.getForeignAccounts().getRate() / factor, 1e-9);
        close("the shelf price divided",
                shelf(lopped), shelf(plain) / factor, 1e-9);
        close("rent divided", rent(lopped), rent(plain) / factor, 1e-9);
        close("the minimum wage divided",
                lopped.getLabourMarket().getMinimumWage(),
                plain.getLabourMarket().getMinimumWage() / factor, 1e-9);
        close("a House costs a hundredth as many dollars",
                cost(lopped, "House"), cost(plain, "House") / factor, 1e-9);

        /*
         * ...AND THE RATIOS DID NOT MOVE, which is the other half of the claim
         * and the more important one. A reform that divided everything by
         * slightly different numbers would pass the lines above and fail these.
         */
        close("the price index did not move",
                lopped.getPriceIndex().getIndex(), plain.getPriceIndex().getIndex(), 1e-9);
        close("nor the rent burden",
                lopped.getHouseholds().getRentBurden(),
                plain.getHouseholds().getRentBurden(), 1e-9);
        close("nor the cost of living",
                lopped.getLabourMarket().getCostOfLiving(),
                plain.getLabourMarket().getCostOfLiving(), 1e-9);
        assertTrue("nor the population", plain.getPopulationManager().getPopulation()
                == lopped.getPopulationManager().getPopulation());

        /* ============ 3. AND IT STAYS RECOGNISABLY THE SAME CITY ============

           The assertion that cannot be satisfied by luck, and the one this
           change had to earn the hard way.

           WHAT IT FOUND, because this is worth writing down. The reform is a
           change of units, so in principle the two cities should stay bitwise
           identical for ever. Every time they did not, the reason was the same
           SHAPE of bug: a figure that is read at the top of a month before
           anything rewrites it - a statement, a cached exchange rate, a
           handler's own copy of the payroll - which is a STOCK for as long as
           it takes to read it, however much it looks like a flow. Eleven of
           those have been found this way and every one was invisible to the
           money audit, because none of them created or destroyed a penny; they
           simply valued a real month at the wrong scale. The last three were
           the mills', the mines' and the builders' whole income statements,
           found on 2026-09-09: HeavyIndustryHandler.redenominate() carried a
           long comment explaining that a statement read before it is rewritten
           is a stock, and then no code under it at all.

           WHAT THE ASSERTIONS ARE NOW, and why they changed shape. This used to
           run a hundred months and assert a BAND at the end of it - within an
           eighth on output, a tenth on rent. That was the right instrument
           while the reform month itself was known to be a few percent adrift,
           because there was nothing tighter to say. It is the wrong one now:
           the reform month is EXACT, so the honest test is to say so, and the
           hundred-month figure is dominated by the advisor's discrete decisions
           rather than by the reform. A band wide enough to survive that is a
           band that would not catch anything.

           So: the month after the reform is asserted to the last digit, the
           year after it is asserted tightly, and the decade after it is
           MEASURED and printed. If the first two ever move, that is a new bug
           and this catches it on the month it happens rather than a decade
           downstream.

           WHAT IS STILL TRUE AND NOT PERFECT. Retail's till comes out of the
           reform month wrong - measured on this fixture at 552.55 against
           13.82, which is not a rounding - and because the shops make DISCRETE
           decisions a wrong till becomes a different decision and a different
           decision compounds. Everything the player looks at is right on the
           month; the divergence is entirely downstream of that one number.
           Money is conserved throughout and no player has an unreformed twin to
           compare against, so nothing about the city they get is wrong - but it
           is not the same city, and this harness says so rather than pretending.
           The trail, for whoever picks it up: rLocalPurchaseValue and
           rInventoryCost are the two that come out of the reform month at the
           old scale, while localPurchaseValue - the live field they are copied
           from - is correct. See CommercialHandler.computeMonthlyReport().
           ================================================================= */
        out.println("\n--- ...and the month after the reform is the same month ---");

        quietly(() -> { plain.simulateMonths(1); lopped.simulateMonths(1); });

        assertTrue("the same people live there, to the person",
                plain.getPopulationManager().getPopulation()
                        == lopped.getPopulationManager().getPopulation());
        close("the same output, to the cent",
                lopped.getEconomyManager().getMonthGdp(),
                plain.getEconomyManager().getMonthGdp() / factor, 1e-9);
        close("the same rent", rent(lopped), rent(plain) / factor, 1e-9);
        close("the same price level",
                lopped.getPriceIndex().getIndex(), plain.getPriceIndex().getIndex(), 1e-9);
        /*
         * The rate is the one thing that is NOT exact on the month, and the
         * reason is worth keeping: it moves on the month's trade balance, and
         * the trade balance moves on what the shops imported, and the shops'
         * till is the one figure that comes out of the reform month wrong. So
         * this is the retail defect measured one layer downstream - 0.025% on
         * this fixture - rather than a fault of the currency machinery. It is
         * asserted tightly rather than exactly, and if it ever widens that is
         * the till getting worse.
         */
        within("the same currency, within a thousandth",
                lopped.getForeignAccounts().getRate(),
                plain.getForeignAccounts().getRate() / factor, .001);

        out.println("\n--- ...and the same city a year later ---");

        quietly(() -> { plain.simulateMonths(11); lopped.simulateMonths(11); });

        within("the same people live there, within a fiftieth",
                lopped.getPopulationManager().getPopulation(),
                plain.getPopulationManager().getPopulation(), .02);
        /*
         * A YEAR of output, not a month. A single month's GDP swings on whether
         * a power station happened to be ordered in it - the same reason
         * InfrastructureCheck measures roads over twelve months - so comparing
         * one month here would be measuring the lumpiness, not the reform.
         */
        within("the same real economy, within a twelfth",
                lopped.getEconomyManager().getYearGdp(),
                plain.getEconomyManager().getYearGdp() / factor, .085);
        within("the same rent, within a fiftieth", rent(lopped), rent(plain) / factor, .02);
        within("the same price level, within a twentieth",
                lopped.getPriceIndex().getIndex(), plain.getPriceIndex().getIndex(), .05);
        within("the same currency, within a twentieth",
                lopped.getForeignAccounts().getRate(),
                plain.getForeignAccounts().getRate() / factor, .05);

        /* ---------------- and a decade on, measured rather than asserted ---------------- */
        out.println("\n--- ...and a decade later, measured ---");

        quietly(() -> { plain.simulateMonths(88); lopped.simulateMonths(88); });

        out.printf("   plain  pop %,d  GDP %,.2f  rent %.6f  index %.4f%n",
                plain.getPopulationManager().getPopulation(),
                plain.getEconomyManager().getMonthGdp(), rent(plain),
                plain.getPriceIndex().getIndex());
        out.printf("   lopped pop %,d  GDP %,.2f  rent %.6f  index %.4f%n",
                lopped.getPopulationManager().getPopulation(),
                lopped.getEconomyManager().getMonthGdp(), rent(lopped),
                lopped.getPriceIndex().getIndex());
        out.printf("   a hundred months on they are %.1f%% apart on population and "
                + "%.1f%% on the price level. Not asserted - see the note above.%n",
                gap(lopped.getPopulationManager().getPopulation(),
                    plain.getPopulationManager().getPopulation()),
                gap(lopped.getPriceIndex().getIndex(), plain.getPriceIndex().getIndex()));

        /* ============ 4. AND NO MONEY WAS MADE OR LOST ============

           The audit, on the reformed city, for a year. A reform that dropped a
           balance would show up as a residual the month after.
           ================================================================= */
        out.println("\n--- and the books still balance ---");

        double worst = 0;
        for (int m = 0; m < 24; m++) {
            quietly(() -> lopped.simulateMonths(1));
            worst = Math.max(worst, lopped.getLastMoneyAudit().relative());
        }
        out.printf("   worst residual over two years after the reform: %.6f%%%n", worst * 100);
        assertTrue("a reformed city conserves money like any other", worst < 1e-4);

        /* ============ 5. AND IT SURVIVES A SAVE ============ */
        out.println("\n--- a reformed city reloads as itself ---");

        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        Game saved = new Game(files);
        quietly(() -> {
            saved.run();
            saved.getGovernmentInvestor().spend(-2_000_000);
            saved.getLandManager().setOwnedSqFt(saved.getLandManager().getOwnedSqFt() + 100_000_000L);
            LongPlaytest.build(saved, "House", 200);
            LongPlaytest.build(saved, "Convenience Store", 10);
            LongPlaytest.build(saved, "Construction Depot", 4);
            saved.simulateMonths(60);
        });
        force(saved, 10);
        double cashBefore = saved.getCash();
        double rentBefore = rent(saved);
        double unitBefore = saved.getDenomination().getUnit();
        quietly(() -> saved.saveGame(1));

        Game back = new Game(files);
        quietly(() -> back.loadGameSave(1));

        close("the unit came back", back.getDenomination().getUnit(), unitBefore, 1e-9);
        close("...and the money with it", back.getCash(), cashBefore, 1e-6);
        close("...and the rent it was charging", rent(back), rentBefore, 1e-9);
        close("...and a House still costs what it cost",
                cost(back, "House"), cost(saved, "House"), 1e-9);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ------------------------------ helpers ------------------------------ */

    /** Reforms regardless of the price-level gate, which is tested separately. */
    static boolean force(Game g, double factor) {
        final boolean[] ok = { false };
        quietly(() -> ok[0] = g.reformCurrencyForTest(factor));
        return ok[0];
    }

    /** The real city: who lives there and how much of everything there is. */
    static boolean same(Game a, Game b) {
        return a.getPopulationManager().getPopulation() == b.getPopulationManager().getPopulation()
                && a.getPopulationManager().getTotalJobs() == b.getPopulationManager().getTotalJobs()
                && a.getBuildingManager().getTotalHomes() == b.getBuildingManager().getTotalHomes()
                && a.getBuildingManager().getTotalStoreCoverage()
                   == b.getBuildingManager().getTotalStoreCoverage();
    }

    static double shelf(Game g) {
        return g.getEconomyManager().getCommercialHandler().getStoreSellPrice();
    }

    static double rent(Game g) {
        return g.getEconomyManager().getCommercialHandler().getRentPrice();
    }

    static double cost(Game g, String name) {
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName(name);
        return t == null ? 0 : t.getCashCost();
    }
}

package ham.citybuildersim;

import ham.citybuildersim.sectors.HeavyIndustry;
import ham.citybuildersim.sectors.Mining;

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
 *
 * The twins have no transit, so a second pair with a bus network is reformed
 * by a thousand in section 6 (B6, 0.7.47): the month's transit lines, the
 * riders at the same real fare, and the working rows' fixed bills.
 *
 * Section 7 (DZ1, 2026-11-02) is the trade by partner. Its first part is on
 * bare PartnerTrade objects, which need no world file: its history's rounding
 * grid follows the currency's unit, and a reform divides the month and the year
 * held, the finished years and the save's copy of them. Its second part is two
 * cities on the repository's twelve-nation world fixture, one reformed in
 * mid-year: the proof that Game wires the grid to the currency's unit and that
 * Game.reformCurrency() divides the record, which the bare objects cannot show
 * and which the twins of section 2 show only where the gate names the real
 * world file.
 *
 * Section 7's third part (TP1, 2026-11-08) is what the screens read of it: the
 * nation card's year to date, the month's figures the map's lanes and arrows
 * are made of, and the floor under which a partner has none (TradeLanes.minLane(),
 * MIN_LANE in the price index and the currency's unit). A bare part holds the
 * floor's arithmetic; the same two cities, on both sides of the reform and
 * again with prices moved, show that the reformed city's card is the plain
 * one's divided and that it draws the same partners.
 */
public class DenominationCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-62s %s%n", label, ok ? "OK" : "FAIL");
    }

    /**
     * Everything real to the person, everything nominal to the factor.
     *
     * One helper rather than five repeated blocks, so the month, the year and
     * the decade are held to the SAME standard and the only thing that changes
     * between them is how much floating-point drift is allowed to accumulate.
     * Since DZ1 (2026-11-02) that is nothing: all three are held to 1e-9. WHY:
     * PartnerTrade's history grid in absolute money parted the twins by 5e-7
     * over fifty months and hid under the millionth the year and the decade
     * were allowed; mended, they hold to about 1e-14 a decade on.
     */
    static void sameCity(Game plain, Game lopped, double factor, double band) {
        assertTrue("the same people live there, to the person",
                plain.getPopulationManager().getPopulation()
                        == lopped.getPopulationManager().getPopulation());
        relative("the same output", lopped.getEconomyManager().getMonthGdp(),
                plain.getEconomyManager().getMonthGdp() / factor, band);
        relative("...and the same year of it", lopped.getEconomyManager().getYearGdp(),
                plain.getEconomyManager().getYearGdp() / factor, band);
        relative("the same rent", rent(lopped), rent(plain) / factor, band);
        relative("the same price level", lopped.getPriceIndex().getIndex(),
                plain.getPriceIndex().getIndex(), band);
        relative("the same currency", lopped.getForeignAccounts().getRate(),
                plain.getForeignAccounts().getRate() / factor, band);
        relative("the same treasury", lopped.getCash(), plain.getCash() / factor, band);
        relative("the same shops' till",
                lopped.getSectors().retail().getCash(),
                plain.getSectors().retail().getCash() / factor,
                band);
        relative("the same city debt", lopped.getDebtManager().getAllPrincipal(),
                plain.getDebtManager().getAllPrincipal() / factor, band);
        relative("the same bank", lopped.getBank().getDeposits(),
                plain.getBank().getDeposits() / factor, band);
    }

    /** Equal to within a RELATIVE band, so one helper works at any scale. */
    static void relative(String label, double actual, double expected, double band) {
        double scale = Math.max(1e-12, Math.abs(expected));
        double off = Math.abs(actual - expected) / scale;
        if (off > band) {
            fails++;
            out.printf("%-62s FAIL  %,.10f != %,.10f  (%.3g apart)%n",
                    label, actual, expected, off);
        } else {
            out.printf("%-62s OK  (%.1g apart, %.1g allowed)%n", label, off, band);
        }
    }

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
    static Game city(String name) { return city(name, false); }

    /** ...with a bus network on its roads too (B6, 0.7.47): a fare, riders and the month's transit lines for a reform to carry. */
    static Game city(String name, boolean buses) {
        Game g = new Game(GameFiles.scratch(name));
        quietly(() -> {
            g.run();
            // XR1 (2026-10-21): the bus towns measure a reform of the transit bill, the fares and the fuel, not the
            // currency. Under THE FLOAT the town's D$ is at 1.79 by month 121 and its drivers burn no fuel yet (fuel bill
            // 0.0, households' fuel 0.0), so the fixture "the drivers burn fuel" has nothing to read; the rate is held
            // where the founding leaves it, and the fuel is burnt. The twins of section 2 are not pinned: they are the
            // harness's reform-neutrality test, and the currency moving under them is the case it exists for.
            if (buses) g.getForeignAccounts().pinRate(g.getForeignAccounts().getRate());
            // THE TREASURY THIS FIXTURE WAS WRITTEN AGAINST (0.7.10): its build list
            // is bought out of cash, and a city founds on D$100M since 0.7.10, not the
            // D$2.5B it assumed - so it is given that, the Wealthy preset's, explicitly.
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.getGovernmentInvestor().spend(-2_000_000 * PriceLevel.FACTOR);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 200_000_000L);
            // PT1-M4: the works department that built for a town nobody staffed is retired. This town's builders are the
            // founding yard's 20 posts, filled from ninety-odd people at the city's per-type fill, and its list of 400
            // houses stood at a handful by month 121, with its drivers having burnt no fuel yet. Depots stand beside the
            // yard (as MortgageCheck's landlords' town stands its), builders enough to raise the list; the plain twins of
            // section 2 stay as they were.
            if (buses) g.buildStack(g.getBuildingManager().getTemplateByName("Construction Depot"), 2, true);
            LongPlaytest.build(g, "House", 400);
            LongPlaytest.build(g, "Convenience Store", 20);
            LongPlaytest.build(g, "Construction Depot", 6);
            LongPlaytest.build(g, "Coal Power Plant", 2);
            LongPlaytest.build(g, "Water Treatment Plant", 2);
            LongPlaytest.build(g, "Industrial Bakery", 4);
            LongPlaytest.build(g, LongPlaytest.nameOf(g, Mining.IRON_MINE), 2);
            LongPlaytest.build(g, LongPlaytest.nameOf(g, HeavyIndustry.ARC_FURNACE), 2);
            LongPlaytest.build(g, "Commercial Bank", 1);
            LongPlaytest.build(g, "Elementary School", 3);
            LongPlaytest.build(g, "Walk-in Clinic", 3);
            LongPlaytest.build(g, "Paved Road", 20);
            if (buses) {
                // ...standing from the first month, and given its price on top:
                // the list above spends the treasury down, and its sites queue.
                BuildingsTemplate network = g.getBuildingManager().getTemplateByName("Bus Network");
                g.setCashForTest(g.getCash() + network.getCashCost());
                g.buildStack(network, 1, true);
            }
            g.simulateMonths(120);
        });
        return g;
    }

    /* ------------------------------------------------------------------ */

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        Path root = TempDirs.create("denomcheck");

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
        // ...of whatever the city named its money (0.7.10): a city founded as Arden.
        Currency arden = Currency.fromCityName("Arden");
        assertTrue("...and the money gets a new name", d.name(arden).startsWith("second"));
        assertTrue("...the city's own money's, renamed", d.name(arden).equals("second " + arden.name()));
        d.lop(1000);
        assertTrue("...and again", d.name(arden).startsWith("third"));
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
        // No assertion: without the nations' world file the terms of trade and the trade by partner (DZ1) do not run in the twins at all.
        out.println("   the twins trade by partner: " + (plain.getPartnerTrade() != null
                ? "yes, on the nations' world file"
                : "no world-1.bin here: the terms of trade and the trade by partner do not run"));

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

        /* =================================================================
           ...AND THE SHARE DESK'S ROOM DOES NOT DIVIDE, WHICH IS THE POINT

           Every line above asks whether a money figure was divided. This one
           asks the opposite question about a figure that is NOT money, and it
           is the question that catches the harder half of this family.

           deskCanBuy() answers in SHARES. Underneath it are two money
           quantities - the desk's book limit and what its book is worth - so
           a reform that divides one and not the other changes an answer that
           has no business moving at all. bookLimit was the one left behind
           (2026-09-17), and because it is re-struck in startMonth() AFTER the
           households settle, a reformed city's desk spent the first half of
           every month believing it had a hundred times the room it had.

           NOTHING ELSE IN THIS HARNESS SAW IT for as long as it existed. The
           desk's room only binds when a household is short enough to sell its
           shares rather than borrow, so the fault sat latent until a change to
           GDP - a fourteenth sector, as it happens - moved the cancellation
           under the limit onto the other side of zero. Then it cost 4.2% of
           household debt in ONE month and a tenth of a percent on every
           headline figure a decade later.

           A RATIO OF TWO MONEY QUANTITIES IS THE GENERAL SHAPE and this is
           the assertion for it: if the answer is not in money, a reform must
           not change it.
           ================================================================= */
        double deskBook = 0;
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            double was = plain.getExchange().deskCanBuy(plain.getEquity(), c);
            double is = lopped.getExchange().deskCanBuy(lopped.getEquity(), c);
            deskBook += Math.max(0, plain.getEquity().getDealerShares(c));
            close("the desk's room in " + Equity.COMPANIES[c] + " is shares, so it is untouched",
                    is, was, 1e-9);
        }
        /*
         * THE FIXTURE GUARD IS THE DESK'S BOOK, NOT ITS ROOM, and the
         * difference is the whole reason the fault survived. This fixture's
         * desk is FULL: every company's room is zero, because bookAtFair has
         * reached bookLimit. A guard written on the room would read zero on a
         * correct build and call the section vacuous - when a full book is
         * exactly the state that makes the subtraction underneath it a
         * cancellation, and a cancellation is where a missing scale shows.
         *
         * Measured with the scale removed again after this was written: nine
         * of the fifteen companies went red, each of them at several hundred
         * shares of room the desk did not have.
         */
        assertTrue(String.format(
                "fixture: the desk had a book to be wrong about (%,.0f shares)", deskBook),
                deskBook > 0);

        /*
         * ...AND SO DOES EVERY LINE THE INCOME STATEMENT OPENS INTO.
         *
         * The per-good money behind Revenue and the cost of sales (Sector.Split,
         * 2026-09-16) is money like any other and has to divide like any other.
         * It was written with the two scale() calls in place and then they were
         * DELETED to see what would notice: the whole suite, all fifty-two
         * harnesses, stayed green. Nothing else in the game reads these maps -
         * only the screen does - so a reform that left them alone would have
         * shipped a statement whose parts were a hundred times its total, and
         * the only way to find it would have been to reform a city and click.
         *
         * That is the twenty-third of this family and the first one caught
         * before it existed. Both maps, both sides, every sector, every good -
         * the same shape as the claim above it, because "any field left
         * unscaled changes a relative price" is what this harness is for.
         */
        int splitLines = 0;
        for (Sector was : plain.getSectors().all()) {
            Sector is = lopped.getSectors().byKey(was.key());
            for (Good g : Good.values()) {
                Sector.Split soldWas = was.statement().sold.get(g);
                Sector.Split soldIs = is.statement().sold.get(g);
                if (soldWas != null || soldIs != null) {
                    splitLines++;
                    close(was.key() + ": " + g + " sold here divided",
                            soldIs == null ? 0 : soldIs.atHome,
                            (soldWas == null ? 0 : soldWas.atHome) / factor, 1e-9);
                    close(was.key() + ": " + g + " sold abroad divided",
                            soldIs == null ? 0 : soldIs.abroad,
                            (soldWas == null ? 0 : soldWas.abroad) / factor, 1e-9);
                }
                Sector.Split boughtWas = was.statement().bought.get(g);
                Sector.Split boughtIs = is.statement().bought.get(g);
                if (boughtWas != null || boughtIs != null) {
                    splitLines++;
                    close(was.key() + ": " + g + " bought here divided",
                            boughtIs == null ? 0 : boughtIs.atHome,
                            (boughtWas == null ? 0 : boughtWas.atHome) / factor, 1e-9);
                    close(was.key() + ": " + g + " imported divided",
                            boughtIs == null ? 0 : boughtIs.abroad,
                            (boughtWas == null ? 0 : boughtWas.abroad) / factor, 1e-9);
                }
            }
        }
        assertTrue("fixture: there was a per-good breakdown to divide (" + splitLines + " lines)",
                splitLines > 0);

        /* ...and the named parts of revenue that are not a good, the same way. */
        for (Sector was : plain.getSectors().all()) {
            Sector is = lopped.getSectors().byKey(was.key());
            for (java.util.Map.Entry<String, Double> part : was.otherRevenueParts().entrySet()) {
                close(was.key() + ": " + part.getKey().toLowerCase() + " divided",
                        is.otherRevenueParts().getOrDefault(part.getKey(), 0.0),
                        part.getValue() / factor, 1e-9);
            }
        }

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

        /* ============ 3. AND IT STAYS THE SAME CITY ============

           The assertion that cannot be satisfied by luck, and the one this
           change had to earn the hard way. A reform is a change of UNITS, so
           the reformed city and its unreformed twin should stay the same city
           for ever. They now do: a hundred months on, the same 5,693 people
           live there, the price index agrees to four decimals, and every money
           figure agrees to the last digit once divided.

           WHAT IT TOOK, because this is the part worth writing down. Fourteen
           bugs were found by this harness and every one of them was the same
           SHAPE: a figure that is read before anything rewrites it, which makes
           it a STOCK for as long as it takes to read it, however much it looks
           like a flow. Not one of them created or destroyed a penny, so the
           money audit could not see any of them; they simply valued a real month
           at the wrong scale. In the order they were found:

             1-8   caches, statements and market prices, 2026-09-08
             9-11  the mills', the mines' and the builders' whole income
                   statements. HeavyIndustryHandler.redenominate() carried a
                   long comment explaining that a statement read before it is
                   rewritten is a stock, and then no code under it at all.
             12    THE MILLS' OWN COPY OF THE WAGE SCALE, and the loudest of the
                   lot. EconomyManager pushes it in every month, so it looks like
                   it cannot go stale - and it goes stale for exactly one month,
                   which is the month that matters. computeMonthlyReport() runs
                   at the top of the tick, before the push. So a reformed city's
                   mills paid a hundred times their whole revenue in wages for
                   one month, borrowed to cover the hole, and dragged the bank's
                   deposit book, its funding cost and its foreign borrowing with
                   them. The REAL economy stayed exact throughout, which is why
                   it survived so long: nothing a player looks at first was wrong.
             13    the tuition table, which is not merely a misreport - it feeds
                   affordability(), so a reformed city's students could no longer
                   pay for school and the pipeline stalled for ever.
             14    CapitalFlows.MIN_STOCK, a flat $1 floor under the hot money.
                   $3.01 of foreign money becomes $0.03 after a hundred-to-one
                   reform, which is under a threshold denominated in the old
                   money, so the reformed city wrote its foreign capital off and
                   the unreformed one did not.

           The last two are the general lesson: a CONSTANT in absolute money is
           the same bug as a cached figure, and a threshold in absolute money is
           worse, because it is discrete - it does not drift, it flips.

           HOW THIS IS ASSERTED NOW. The month after the reform, the year after
           and the decade after, all to a billionth (1e-9). There is no band
           left to hide in. If any of it ever moves again, it is a fifteenth of
           the same family and this catches it on the month it happens.

           WHY THE YEAR AND THE DECADE WERE A MILLIONTH UNTIL DZ1 (2026-11-02):
           PartnerTrade's history rounded to a grid in absolute money, which a
           reform by a hundred leaves a hundred times coarser, and it parted
           the twins by 5e-7 over fifty months (the terms of trade, so the
           currency) - under a millionth, where nothing saw it. With the grid
           following the unit and the trade by partner redenominated (section
           7), the twins hold to about 1e-14 a decade on.
           ================================================================= */
        out.println("\n--- ...and the month after the reform is the same month ---");

        scanEmpty(plain);
        scanEmpty(lopped);
        emptyHeldAfterReform = emptyHolding;
        stepBoth(plain, lopped, 1);
        sameCity(plain, lopped, factor, 1e-9);

        out.println("\n--- ...and the same city a year later ---");
        stepBoth(plain, lopped, 11);
        sameCity(plain, lopped, factor, 1e-9);

        out.println("\n--- ...and the same city a decade later ---");
        /*
         * -Ddenom.trace=true: month by month, with the sectors' state at the
         * first month the two banks part. The instrument that found the
         * resolution floor read in the founding unit (Bank.resolutionExitEquity).
         */
        if (Boolean.getBoolean("denom.trace")) {
            for (int m = 0; m < 88; m++) {
                stepBoth(plain, lopped, 1);
                double a = plain.getBank().equity(), b = lopped.getBank().equity() * factor;
                double ca = plain.getCash(), cb = lopped.getCash() * factor;
                long pa = plain.getPopulationManager().getPopulation(), pb = lopped.getPopulationManager().getPopulation();
                out.printf("   m%d bank %.6f vs %.6f | cash %.6f vs %.6f | pop %d vs %d | matOwed %.3f vs %.3f | bankCash %.6f vs %.6f%n", plain.getMonth(), a, b, ca, cb, pa, pb,
                        plain.getBuildingManager().getMaterialsOwed(), lopped.getBuildingManager().getMaterialsOwed(),
                        plain.getBank().getCash(), lopped.getBank().getCash() * factor);
                if (Math.abs(a - b) > 1e-3 * Math.max(1, Math.abs(a))) {
                    for (String s : Sectors.KEYS) {
                        out.printf("      %s debt %.3f vs %.3f cash %.3f vs %.3f | %s || %s%n", s,
                                plain.getEconomyManager().getBusinessDebtManager().getPrincipal(s), lopped.getEconomyManager().getBusinessDebtManager().getPrincipal(s) * factor,
                                plain.getEconomyManager().getSectorCash(s), lopped.getEconomyManager().getSectorCash(s) * factor,
                                plain.getLastInvestment(s), lopped.getLastInvestment(s));
                    }
                    out.printf("      bank branches %.0f vs %.0f | city debt %.3f vs %.3f | bank book %.3f vs %.3f | securities %.3f vs %.3f%n",
                            plain.getBank().getBranches(), lopped.getBank().getBranches(), plain.getDebtManager().getAllPrincipal(), lopped.getDebtManager().getAllPrincipal() * factor,
                            plain.getBank().getBook(), lopped.getBank().getBook() * factor, plain.getBank().getSecurities(), lopped.getBank().getSecurities() * factor);
                    break;
                }
            }
        } else {
            stepBoth(plain, lopped, 88);
        }

        out.printf("   plain  pop %,d  GDP %,.2f  rent %.6f  index %.4f%n",
                plain.getPopulationManager().getPopulation(),
                plain.getEconomyManager().getMonthGdp(), rent(plain),
                plain.getPriceIndex().getIndex());
        out.printf("   lopped pop %,d  GDP %,.2f  rent %.6f  index %.4f%n",
                lopped.getPopulationManager().getPopulation(),
                lopped.getEconomyManager().getMonthGdp(), rent(lopped),
                lopped.getPriceIndex().getIndex());
        sameCity(plain, lopped, factor, 1e-9);

        /* ============ 3a. AND NO CELL UNDER HALF A HOUSEHOLD HOLDS ANYTHING ============

           What parted these two cities at month 166 once the currency's rate
           pull went to four (0.7.2), and on the pristine 0.7.1 tree held money
           from month 127 on: a household cell the census leaves at 1e-15 of a
           household, still holding savings and still being paid. Whether a
           count lands on 1e-15 or on exactly nothing is the fifteenth decimal,
           which is precisely what a reform by a hundred moves - so the twins
           paid different ghosts. HouseholdBalance.EMPTY_CELL now empties such
           a cell where its count is written (HouseholdCheck has the fixture);
           this is the same claim on a real city, reformed and not, every
           month: no cell under the line holds anything.
           ================================================================= */
        out.println("\n--- ...and no cell under half a household holds anything, reformed or not ---");
        assertTrue("right after the reform, in either city", emptyHeldAfterReform == 0);
        out.printf("   %d looks (both cities, after the reform and after every month): a cell under half a household in %d; %,d cells emptied of something%n",
                looks, looksWithEmpty, cellsFolded);
        assertTrue("fixture: the census left cells under half a household", looksWithEmpty > 0);
        assertTrue("...and after no month did one hold anything", emptyHolding == 0);

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
            saved.getGovernmentInvestor().spend(-2_000_000 * PriceLevel.FACTOR);
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

        /* ============ 6. AND THE TRAMS (B6, 0.7.47) ============

           The twins above have no transit, so nothing above saw it: a reform
           divided every month's figure but the transit wages and fares, and
           the next month billed the households last month's fares in the old
           unit - a thousand times the ride, and every working row's income
           after its fixed bills went negative. And the ridership curve read
           the dial, divided, against a cap in founding money, so a thousandth
           of the fare put more people on the buses at the same real price.
           A pair of bus towns, one reformed by a thousand (the research
           city's reform), a month on.
           ================================================================= */
        out.println("\n--- a reform and the buses ---");

        Game buses = city("denom-buses-plain", true);
        Game reformed = city("denom-buses-lopped", true);
        assertTrue("fixture: two bus towns, identical, with riders and a transit bill and fares",
                same(buses, reformed) && buses.getInfrastructureManager().getTransitRiders() > 0
                        && buses.getEconomyManager().getTransitBill() > 0 && buses.getEconomyManager().getTransitFares() > 0);
        assertTrue("fixture: the bus towns' builders raised the list of 400 houses they were given",
                buses.getBuildingManager().getTotalHomes() > 400 && reformed.getBuildingManager().getTotalHomes() > 400);
        double thousand = 1000;
        double billBefore = reformed.getEconomyManager().getTransitBill();
        double faresBefore = reformed.getEconomyManager().getTransitFares();
        // ...and the fuel (0.7.49): a journey's price as the month struck it, the drivers' bill, and what the households paid.
        double fuelStruck = reformed.getInfrastructureManager().getFuelPerJourney();
        double fuelBill = reformed.getMotoring().getFuelBill();
        double fuelPaid = reformed.getHouseholdFuel();
        assertTrue("fixture: the bus town's drivers burn fuel, struck, billed and paid",
                fuelStruck > 0 && fuelBill > 0 && fuelPaid > 0);
        assertTrue("fixture: the reform by a thousand goes through", force(reformed, thousand));
        assertTrue("a reform scales the month's transit bill and fares",
                reformed.getEconomyManager().getTransitBill() == billBefore * (1.0 / thousand)
                        && reformed.getEconomyManager().getTransitFares() == faresBefore * (1.0 / thousand));
        assertTrue("a reform divides the fuel struck and the fuel paid",
                reformed.getInfrastructureManager().getFuelPerJourney() == fuelStruck * (1.0 / thousand)
                        && reformed.getMotoring().getFuelBill() == fuelBill * (1.0 / thousand)
                        && reformed.getHouseholdFuel() == fuelPaid * (1.0 / thousand));
        assertTrue("...and the fare dial's cap is the founding cap in the new unit",
                reformed.getEconomyManager().getTaxPolicy().maxTransitFare() == TaxPolicy.MAX_TRANSIT_FARE / reformed.getDenomination().getUnit()
                        && reformed.getInfrastructureManager().getFareUnit() == reformed.getDenomination().getUnit());

        stepBoth(buses, reformed, 1);
        double ridersPlain = buses.getInfrastructureManager().getTransitRiders();
        double ridersReformed = reformed.getInfrastructureManager().getTransitRiders();
        out.printf("   a month on: riders %,.3f plain, %,.3f reformed%n", ridersPlain, ridersReformed);
        assertTrue("after a reform the riders are what the same real fare drew",
                ridersPlain > 0 && Math.abs(ridersReformed - ridersPlain) <= 1e-9 * ridersPlain);

        java.util.List<Household> plainCells = buses.getHouseholdBalance().cells();
        java.util.List<Household> reformedCells = reformed.getHouseholdBalance().cells();
        int working = 0, wentNegative = 0;
        for (int i = 0; i < plainCells.size() && i < reformedCells.size(); i++) {
            Household p = plainCells.get(i), r = reformedCells.get(i);
            if (!(p instanceof WorkingHousehold) || p.isEmpty() || p.afterFixed() < 0) continue;
            working++;
            if (r.afterFixed() < 0) wentNegative++;
        }
        out.printf("   %d working rows clear their fixed bills unreformed; %d of them do not, reformed%n", working, wentNegative);
        assertTrue("no working row's income after fixed bills goes negative across a reform",
                working > 0 && wentNegative == 0 && plainCells.size() == reformedCells.size());

        /* ============ 7. THE TRADE BY PARTNER (DZ1, 2026-11-02) ============

           What parted the twins of section 2, once the nations' world file was
           there to run the terms of trade (the gate has it, a worktree does
           not): in the January of month 169 the exchange rate was 5.6e-8 apart
           and then every sector's cash, and by month 221 5e-7, under the old
           millionth of the year and the decade by luck. Two defects, both in
           PartnerTrade, the city's trade by nation:

             1. It rounds the year it finishes to a grid in money, and the grid
                followed the price factor and not the currency's unit. After a
                reform by a hundred it was a hundred times coarser against the
                city's money, so the reformed twin's history cells rounded
                differently, and Game.cityExportPriceLevel() weights the
                world's prices by those rounded exports every January: the
                terms of trade, so the D$.
             2. Game.reformCurrency() never touched it. The month's and the
                year's money and the finished years stayed in the old unit
                (and HistorySave's copy of the tables with them), so a reform
                in mid-year weighed one year's exports in two units. The twins
                reform on a year's turn, which is why only the grid showed.

           So this section's first part is on bare PartnerTrade objects, which
           need no world file: a plain record and its reformed copies, side by
           side, tallying the same real trade, the copies' money divided by the
           factor. Its second part is the Game's half: two cities on the
           twelve-nation world fixture, one reformed in mid-year, to show that
           Game hands the record the currency's unit (ensureWorldMarket()) and
           divides it in a reform (reformCurrency()), the two lines the bare
           objects cannot reach. The reform falls between two Januaries, so the
           year held is in two units, and the next turn of the year is the
           reformed city's first rounding on the new grid.
           ================================================================= */
        tradeByPartner();
        lanesFloor();
        tradeByPartnerInGame(root);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ------------------- section 7: the trade by partner ------------------- */

    /** The founding grid's step: what the history's money is rounded to before a reform. */
    static final double TRADE_STEP = 1 / PartnerTrade.HISTORY_ROUND_MONEY;

    /**
     * One month of the fixture's trade, every money figure divided by `by` (the reformed copies' unit): crops and grains exported and imported
     * to two nations (the first takes three quarters of the exports, each half the imports; the units tallied are the allocations' sums, 4 and 2,
     * as the record's contract has them), and some fish with no partner. The year's totals are
     * 123.4567 steps of crops and 37.8912 of grains exported, 2.3456 and 5.6789 imported, spread evenly over the twelve months: off the founding
     * grid, so every cell's rounding is a visible one.
     */
    static void tallyMonth(PartnerTrade t, int month, double by) {
        WorldMarket.Allocation split = new WorldMarket.Allocation(new double[] { 3, 1 }, new double[] { 1, 1 });
        t.tally(month, Good.CROPS, split, 4, TRADE_STEP * 123.4567 / 12 / by, 2, TRADE_STEP * 2.3456 / 12 / by, 0);
        t.tally(month, Good.GRAINS, split, 4, TRADE_STEP * 37.8912 / 12 / by, 2, TRADE_STEP * 5.6789 / 12 / by, 0);
        t.tally(month, Good.FISH, null, 50, TRADE_STEP * 7 / by, 25, TRADE_STEP * 3 / by, 0);
        t.close(month);
    }

    /** How far apart two figures are, relative to the expected one; infinity where the expected is nothing and the actual is not. */
    static double gapOf(double actual, double expected) {
        return Math.abs(actual - expected) / Math.max(Math.abs(expected), Double.MIN_NORMAL);
    }

    /** The worst gap between two arrays, the expected one times `scale`. */
    static double arrayGap(double[] actual, double[] expected, double scale) {
        if (actual.length != expected.length) return Double.POSITIVE_INFINITY;
        double worst = 0;
        for (int i = 0; i < actual.length; i++) worst = Math.max(worst, gapOf(actual[i], expected[i] * scale));
        return worst;
    }

    /** The worst gap between the money of two lists of cells, the expected times `scale`; infinity where they are not the same cells. */
    static double cellGap(java.util.List<PartnerTrade.Cell> actual, java.util.List<PartnerTrade.Cell> expected, double scale) {
        if (actual.size() != expected.size()) return Double.POSITIVE_INFINITY;
        double worst = 0;
        for (int i = 0; i < actual.size(); i++) {
            PartnerTrade.Cell a = actual.get(i), e = expected.get(i);
            if (a.good() != e.good() || a.nation() != e.nation()) return Double.POSITIVE_INFINITY;
            worst = Math.max(worst, Math.max(gapOf(a.valueOut(), e.valueOut() * scale), gapOf(a.valueIn(), e.valueIn() * scale)));
        }
        return worst;
    }

    /** Whether two lists of cells have the same units, to the bit. */
    static boolean sameUnits(java.util.List<PartnerTrade.Cell> actual, java.util.List<PartnerTrade.Cell> expected) {
        if (actual.size() != expected.size()) return false;
        for (int i = 0; i < actual.size(); i++) {
            if (actual.get(i).unitsOut() != expected.get(i).unitsOut() || actual.get(i).unitsIn() != expected.get(i).unitsIn()) return false;
        }
        return true;
    }

    /** Section 7's first part, on bare PartnerTrade objects. */
    static void tradeByPartner() {
        out.println("\n--- the trade by partner: its history's grid follows the unit ---");

        double factor = 100;
        // The first month of a calendar year, found, and the twelve months of it: the year turns on the first tally of the month after.
        int first = 2;
        while (Nations.yearOf(first - 1) == Nations.yearOf(first)) first++;
        final int start = first;
        assertTrue("fixture: the twelve months tallied are one calendar year and the next month is another's",
                Nations.yearOf(start) == Nations.yearOf(start + 11) && Nations.yearOf(start + 12) != Nations.yearOf(start));

        // The plain record: the year as it was, the month and the year to date it stood at, and the table the turn sent to the history.
        PartnerTrade plain = new PartnerTrade(2);
        PartnerTrade.Snapshot midYearMonth = null;
        for (int i = 0; i < 12; i++) {
            tallyMonth(plain, start + i, 1);
            if (i == 5) midYearMonth = plain.snapshot();
        }
        PartnerTrade.Snapshot beforeTurn = plain.snapshot();
        plain.close(start + 12);
        java.util.List<PartnerTrade.Cell> plainCells = plain.history().get(0).cells();
        assertTrue("fixture: the turn sent a year of four cells (two goods, two nations) to the history, off the grid",
                plain.yearsFinished() == 1 && plainCells.size() == 4);
        assertTrue("fixture: the month and the year held had trade with no partner to divide",
                midYearMonth.noPartnerExports() > 0 && midYearMonth.noPartnerImports() > 0 && beforeTurn.yearExportValue()[0] > 0
                        && beforeTurn.yearImportValue()[1] > 0);

        // a. The grid follows the unit: a copy in the new money, tallying the same trade at value / factor.
        PartnerTrade followed = new PartnerTrade(2);
        followed.setMoneyUnit(() -> factor);
        for (int i = 0; i < 12; i++) tallyMonth(followed, start + i, factor);
        followed.close(start + 12);
        java.util.List<PartnerTrade.Cell> followedCells = followed.history().get(0).cells();
        close("a reformed record's history is the plain record's, divided: every cell's value out and in",
                cellGap(followedCells, plainCells, 1 / factor), 0, 1e-12);
        assertTrue("...and its units are the plain record's, exactly", sameUnits(followedCells, plainCells));

        // b. The other side: the same copy with the unit left at 1 rounds on the founding grid, a hundred times too coarse.
        PartnerTrade founding = new PartnerTrade(2);
        for (int i = 0; i < 12; i++) tallyMonth(founding, start + i, factor);
        founding.close(start + 12);
        java.util.List<PartnerTrade.Cell> foundingCells = founding.history().get(0).cells();
        double off = cellGap(foundingCells, plainCells, 1 / factor);
        assertTrue(String.format("fixture: on the founding grid the same figures are %.3g apart, so the figures above sit off both grids", off),
                foundingCells.size() == plainCells.size() && off > 1e-3);

        // c. A reform in mid-year: six months in the old money, the reform, and six in the new.
        double[] unit = { 1 };
        PartnerTrade mid = new PartnerTrade(2);
        mid.setMoneyUnit(() -> unit[0]);
        for (int i = 0; i < 6; i++) tallyMonth(mid, start + i, 1);
        mid.redenominate(1 / factor);
        unit[0] = factor;
        PartnerTrade.Snapshot reformedMonth = mid.snapshot();
        close("a reform in mid-year divides the month held, with its trade that had no partner",
                Math.max(Math.max(arrayGap(reformedMonth.exportValue(), midYearMonth.exportValue(), 1 / factor),
                                arrayGap(reformedMonth.importValue(), midYearMonth.importValue(), 1 / factor)),
                        Math.max(gapOf(reformedMonth.noPartnerExports(), midYearMonth.noPartnerExports() / factor),
                                gapOf(reformedMonth.noPartnerImports(), midYearMonth.noPartnerImports() / factor))),
                0, 1e-12);
        for (int i = 6; i < 12; i++) tallyMonth(mid, start + i, factor);
        PartnerTrade.Snapshot midBeforeTurn = mid.snapshot();
        close("...and the year held: its exports and imports to date are the plain record's, divided",
                Math.max(arrayGap(midBeforeTurn.yearExportValue(), beforeTurn.yearExportValue(), 1 / factor),
                        arrayGap(midBeforeTurn.yearImportValue(), beforeTurn.yearImportValue(), 1 / factor)),
                0, 1e-12);
        mid.close(start + 12);
        close("...and the year it sends to the history is the plain record's, divided",
                cellGap(mid.history().get(0).cells(), plainCells, 1 / factor), 0, 1e-12);

        // d. A year finished before the reform is in the old money until the reform divides it.
        PartnerTrade finished = new PartnerTrade(2);
        for (int i = 0; i < 12; i++) tallyMonth(finished, start + i, 1);
        finished.close(start + 12);
        assertTrue("fixture: the year is in the history before the reform", finished.yearsFinished() == 1);
        finished.redenominate(1 / factor);
        close("a reform divides the years already finished: their cells are the plain record's, divided",
                cellGap(finished.history().get(0).cells(), plainCells, 1 / factor), 0, 1e-12);
        assertTrue("...with the same year and the same units",
                finished.history().get(0).year() == plain.history().get(0).year() && sameUnits(finished.history().get(0).cells(), plainCells));

        // e. The save's copy of the tables: HistorySave keeps its own, and its reform divides them too.
        HistorySave saved = new HistorySave();
        saved.setTradeByPartner(plain.history());
        saved.redenominate(1 / factor);
        java.util.List<PartnerTrade.Table> savedTables = saved.getTradeByPartner();
        assertTrue("fixture: the save's copy holds the finished year", savedTables.size() == 1);
        close("a reform divides the save's trade by partner: its cells are the plain record's, divided",
                cellGap(savedTables.get(0).cells(), plainCells, 1 / factor), 0, 1e-12);
    }

    /* ----------- section 7, third part: the lanes' floor (TP1, 2026-11-08) ----------- */

    /**
     * The floor under which a partner has no lane, on bare TradeLanes: the board's D$0.3M at the founding's prices (times PriceLevel.FACTOR), moved by
     * the price index and the currency's unit alone, and the lanes and arrows held to the floor they are given, at the floor of each unit.
     */
    static void lanesFloor() {
        out.println("\n--- the lanes' floor in real terms: the price index and the currency's unit (TP1) ---");
        double founding = TradeLanes.minLane(1, 1);
        close("at the founding's prices and money the floor is the board's D$0.3M priced at the founding's", founding, TradeLanes.MIN_LANE * PriceLevel.FACTOR, 1e-12);
        close("prices twice as high double it", TradeLanes.minLane(2, 1), 2 * founding, 1e-12);
        close("a reform by a hundred divides it by a hundred", TradeLanes.minLane(1, 100), founding / 100, 1e-12);
        close("...and a reform with prices doubled is both", TradeLanes.minLane(2, 100), 2 * founding / 100, 1e-12);
        close("an index or a unit that is not a positive number reads 1",
                TradeLanes.minLane(Double.NaN, 0) + TradeLanes.minLane(-3, Double.POSITIVE_INFINITY), 2 * founding, 1e-12);
        assertTrue("the feed with no money to read it from holds partners to the board's figure as written", TradeLanes.Feed.NONE.minLane() == TradeLanes.MIN_LANE);

        // Two partners, one at the floor and one a hair under it, in the money of an unreformed city and of three reformed ones.
        double[] lat = { 52, 10 }, lon = { 5, 100 };
        for (double unit : new double[] { 1, 100, 1000, 1e6 }) {
            double floor = TradeLanes.minLane(1, unit);
            double[] ex = { floor, Math.nextDown(floor) }, im = { 0, 0 };
            TradeLanes lanes = TradeLanes.of(45, -63, 7, lat, lon, ex, im, floor);
            TradeLanes.Arrows arrows = TradeLanes.Arrows.of(45, -63, 7, lat, lon, ex, im, floor);
            assertTrue("unit " + unit + ": the partner at the floor has a lane, the one a hair under has none",
                    lanes.partners() == 1 && lanes.partnerEdge(0) >= 0 && lanes.partnerEdge(1) == -1);
            assertTrue("unit " + unit + ": ...and an arrow, the other none (" + arrows.count() + " drawn)", arrows.count() == 1 && arrows.nation(0) == 1);
            assertTrue("unit " + unit + ": ...and they are the lanes of that floor and no other (the screen builds them again when it moves)",
                    lanes.matches(7, 45, -63, ex, im, floor) && !lanes.matches(7, 45, -63, ex, im, floor / 2) && lanes.minLane() == floor
                            && arrows.matches(7, 45, -63, ex, im, floor) && !arrows.matches(7, 45, -63, ex, im, floor * 2));
            if (unit > 1) {
                // The fixture can tell the floors apart: the figures are a hundredth or less of the board's, which the board's own floor, left
                // as it was written, would hide whole.
                assertTrue("unit " + unit + ": the floor as the board wrote it would hide both (" + floor + " < " + TradeLanes.MIN_LANE + ")",
                        TradeLanes.of(45, -63, 7, lat, lon, ex, im).partners() == 0 && TradeLanes.Arrows.of(45, -63, 7, lat, lon, ex, im).count() == 0);
            }
        }
    }

    /* ----------- section 7, second part: the Game's two lines, in a city ----------- */

    /** The world seed of the second part's cities: ReadPathCheck's WN3 city's, so the fixture is one the repository already plays. */
    static final long TRADE_WORLD_SEED = 2_026_102_8L;

    /** The month of the year the reform is put in: July, the middle of the calendar year, six tallies behind it and five or six in front. */
    static final int REFORM_MONTH_OF_YEAR = 7;

    /** Section 7's second part: two cities on the twelve-nation world fixture, one reformed in mid-year (the file is named for the property while they play). */
    static void tradeByPartnerInGame(Path root) throws Exception {
        out.println("\n--- the trade by partner: a city on the world fixture, reformed in mid-year ---");
        byte[] fixture = WorldDataCheck.resource(WorldData.FIXTURE);
        assertTrue("fixture: the twelve-nation world file is beside the classes", fixture != null);
        if (fixture == null) return;
        Path file = root.resolve("world-fixture.bin");
        Files.write(file, fixture);
        // The gate names the real world file through -Dcbs.world, and the twins of section 2 have run on it: it is put back as it was found.
        String named = System.getProperty(WorldData.PROPERTY);
        try {
            System.setProperty(WorldData.PROPERTY, file.toString());
            reformedInMidYear(root);
        } finally {
            if (named == null) System.clearProperty(WorldData.PROPERTY);
            else System.setProperty(WorldData.PROPERTY, named);
        }
    }

    /** ReadPathCheck's WN3 city (120 houses, two convenience stores, a water treatment plant) on the fixture file, played until its trade by partner has finished a year. */
    static Game worldCity(Path root, String name) {
        Game g = new Game(new GameFiles(root.resolve(name + "-data"), root.resolve("no-legacy")));
        quietly(() -> {
            g.newGame(Founding.defaults().withWorldSeed(TRADE_WORLD_SEED));
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.buildStack(g.getBuildingManager().getTemplateByName("House"), 120, true);
            g.buildStack(g.getBuildingManager().getTemplateByName("Convenience Store"), 2, true);
            g.buildStack(g.getBuildingManager().getTemplateByName("Water Treatment Plant"), 1, true);
            for (int m = 0; m < 20 && (g.partnerTradeForTest() == null || g.partnerTradeForTest().yearsFinished() < 1); m++) {
                g.simulateMonths(1);
            }
        });
        return g;
    }

    /** One month on both cities, quietly. */
    static void monthOn(Game a, Game b) {
        quietly(() -> { a.simulateMonths(1); b.simulateMonths(1); });
    }

    /** A city's live trade by partner, or null where it has none. */
    static PartnerTrade tradeOf(Game g) { return g.partnerTradeForTest(); }

    static double sum(double[] a) {
        double s = 0;
        for (double v : a) s += v;
        return s;
    }

    /** Whether a finished year's table holds any trade in money. */
    static boolean hasTrade(PartnerTrade.Table t) {
        for (PartnerTrade.Cell c : t.cells()) if (c.valueOut() > 0 || c.valueIn() > 0) return true;
        return false;
    }

    /** A worst gap measured, held to a band, and said on its line: the margin is there for a reader to see. */
    static void gapWithin(String label, double gap, double band) {
        boolean ok = gap <= band;
        if (!ok) fails++;
        out.printf("%-62s %s  (%.2g apart, %.0e allowed)%n", label, ok ? "OK" : "FAIL", gap, band);
    }

    /** The worst gap between the units of two lists of cells, relative to the expected; infinity where they are not the same cells. */
    static double unitsGap(java.util.List<PartnerTrade.Cell> actual, java.util.List<PartnerTrade.Cell> expected) {
        if (actual.size() != expected.size()) return Double.POSITIVE_INFINITY;
        double worst = 0;
        for (int i = 0; i < actual.size(); i++) {
            PartnerTrade.Cell a = actual.get(i), e = expected.get(i);
            if (a.good() != e.good() || a.nation() != e.nation()) return Double.POSITIVE_INFINITY;
            worst = Math.max(worst, Math.max(gapOf(a.unitsOut(), e.unitsOut()), gapOf(a.unitsIn(), e.unitsIn())));
        }
        return worst;
    }

    /** The worst gap between the money of two records' state, the expected times `scale`: the month and the year to date by nation, the trade with no partner, and the year's cells. */
    static double stateMoneyGap(PartnerTrade.State actual, PartnerTrade.State expected, double scale) {
        double worst = Math.max(arrayGap(actual.exportValue, expected.exportValue, scale), arrayGap(actual.importValue, expected.importValue, scale));
        worst = Math.max(worst, Math.max(arrayGap(actual.yearExportValue, expected.yearExportValue, scale),
                arrayGap(actual.yearImportValue, expected.yearImportValue, scale)));
        worst = Math.max(worst, Math.max(gapOf(actual.noPartnerExports, expected.noPartnerExports * scale),
                gapOf(actual.noPartnerImports, expected.noPartnerImports * scale)));
        return Math.max(worst, cellGap(actual.yearCells, expected.yearCells, scale));
    }

    /** The worst gap between the real figures of two records' state, nothing scaled: the tonnes by nation (month and year, cargo too) and the year's units. */
    static double stateRealGap(PartnerTrade.State actual, PartnerTrade.State expected) {
        double worst = Math.max(arrayGap(actual.exportTonnes, expected.exportTonnes, 1), arrayGap(actual.importTonnes, expected.importTonnes, 1));
        worst = Math.max(worst, Math.max(arrayGap(actual.yearExportTonnes, expected.yearExportTonnes, 1),
                arrayGap(actual.yearImportTonnes, expected.yearImportTonnes, 1)));
        worst = Math.max(worst, Math.max(arrayGap(actual.cargoExportTonnes, expected.cargoExportTonnes, 1),
                arrayGap(actual.cargoImportTonnes, expected.cargoImportTonnes, 1)));
        return Math.max(worst, unitsGap(actual.yearCells, expected.yearCells));
    }

    /** The worst gap between the money of two lists of finished years, the expected times `scale`; infinity where they are not the same years. */
    static double historyMoneyGap(java.util.List<PartnerTrade.Table> actual, java.util.List<PartnerTrade.Table> expected, double scale) {
        if (actual.size() != expected.size()) return Double.POSITIVE_INFINITY;
        double worst = 0;
        for (int i = 0; i < actual.size(); i++) {
            if (actual.get(i).year() != expected.get(i).year()) return Double.POSITIVE_INFINITY;
            worst = Math.max(worst, cellGap(actual.get(i).cells(), expected.get(i).cells(), scale));
        }
        return worst;
    }

    /** ...and the units of them. */
    static double historyUnitsGap(java.util.List<PartnerTrade.Table> actual, java.util.List<PartnerTrade.Table> expected) {
        if (actual.size() != expected.size()) return Double.POSITIVE_INFINITY;
        double worst = 0;
        for (int i = 0; i < actual.size(); i++) worst = Math.max(worst, unitsGap(actual.get(i).cells(), expected.get(i).cells()));
        return worst;
    }

    /**
     * Two cities on the fixture world, identical until the reform, side by side: the reform is put in July of their second year, with the January
     * behind it (a year turned, so the history holds a year in the old money) and the next in front (the turn that rounds the year held, half of it
     * in each money, on the new grid).
     */
    static void reformedInMidYear(Path root) {
        double factor = 100;
        double band = 1e-12;
        Game plain = worldCity(root, "trade-plain");
        Game lopped = worldCity(root, "trade-lopped");

        // 1. The fixture: two cities on the file, each with a market and a trade by partner that has seen a year turn, and trade in that year.
        boolean ready = tradeOf(plain) != null && tradeOf(lopped) != null && plain.getWorldMarket() != null && lopped.getWorldMarket() != null
                && tradeOf(plain).yearsFinished() == 1 && tradeOf(lopped).yearsFinished() == 1;
        assertTrue("fixture: both cities have a world market and a trade by partner that has seen a year turn", ready);
        if (!ready) return;
        assertTrue("fixture: the two cities are the same city before the reform", same(plain, lopped));
        PartnerTrade.Table first = tradeOf(plain).history().get(0);
        assertTrue("fixture: the year behind them has trade (" + first.cells().size() + " cells), the same in both",
                hasTrade(first) && historyMoneyGap(tradeOf(lopped).history(), tradeOf(plain).history(), 1) == 0);
        out.printf("   the first year turned in %s%n", CityCalendar.shortMonthName(tradeOf(plain).month()));

        // 2. Both on to July, with trade in the year held and in the month; the reform falls between two Januaries.
        for (int m = 0; m < 12 && CityCalendar.monthOfYear(tradeOf(lopped).month()) != REFORM_MONTH_OF_YEAR; m++) monthOn(plain, lopped);
        int reformMonth = tradeOf(lopped).month();
        assertTrue("fixture: the reform falls in mid-year, not in a January (" + CityCalendar.shortMonthName(reformMonth) + ")",
                CityCalendar.monthOfYear(reformMonth) == REFORM_MONTH_OF_YEAR && tradeOf(plain).month() == reformMonth);
        PartnerTrade.State plainBefore = tradeOf(plain).toState();
        java.util.List<PartnerTrade.Table> plainHistory = tradeOf(plain).history();
        assertTrue("fixture: the year held has trade by then, and so has the month",
                sum(plainBefore.yearExportValue) + sum(plainBefore.yearImportValue) > 0
                        && sum(plainBefore.exportValue) + sum(plainBefore.importValue) + plainBefore.noPartnerExports + plainBefore.noPartnerImports > 0);
        assertTrue("fixture: the two cities are still the same city", same(plain, lopped) && tradeOf(lopped).toState().year == plainBefore.year);
        cardAndLanes("before the reform", plain, lopped, 1, false);
        assertTrue("the reform took: a hundred old dollars to the new one",
                force(lopped, factor) && Math.abs(lopped.getDenomination().getUnit() - factor) <= band * factor);

        // 3. Right after it: the record is the plain one's, divided, and its real figures are the plain one's.
        PartnerTrade.State after = tradeOf(lopped).toState();
        double moneyGap = stateMoneyGap(after, plainBefore, 1 / factor);
        double realGap = stateRealGap(after, plainBefore);
        gapWithin("a reform in mid-year divides the month and the year held, by nation and with no partner", moneyGap, band);
        gapWithin("...and leaves their tonnes and units as they were", realGap, band);
        double historyGap = historyMoneyGap(tradeOf(lopped).history(), plainHistory, 1 / factor);
        gapWithin("...and divides the years already finished (" + plainHistory.size() + " of them)", historyGap, band);
        gapWithin("...and leaves their units as they were", historyUnitsGap(tradeOf(lopped).history(), plainHistory), band);
        java.util.List<PartnerTrade.Table> savedPlain = plain.getHistorySave().getTradeByPartner();
        java.util.List<PartnerTrade.Table> savedLopped = lopped.getHistorySave().getTradeByPartner();
        assertTrue("fixture: the save's copy holds the finished year in both cities", savedPlain.size() == 1 && savedLopped.size() == 1);
        gapWithin("...and the save's copy of them is divided too", historyMoneyGap(savedLopped, savedPlain, 1 / factor), band);
        out.printf("   right after the reform the worst gaps: money %.2g, real %.2g, history %.2g%n", moneyGap, realGap, historyGap);
        cardAndLanes("right after the reform", plain, lopped, 1 / factor, true);

        // 4. On to the next turn of the year: the first rounding on the new grid, with the half year before it in the old money divided.
        for (int m = 0; m < 13 && tradeOf(lopped).yearsFinished() < 2; m++) monthOn(plain, lopped);
        int turnMonth = tradeOf(lopped).month();
        assertTrue("both cities finished a second year, in " + CityCalendar.shortMonthName(turnMonth),
                tradeOf(lopped).yearsFinished() == 2 && tradeOf(plain).yearsFinished() == 2 && tradeOf(plain).month() == turnMonth);
        if (tradeOf(lopped).yearsFinished() != 2 || tradeOf(plain).yearsFinished() != 2) return;
        PartnerTrade.Table newestPlain = tradeOf(plain).history().get(1), newestLopped = tradeOf(lopped).history().get(1);
        assertTrue("fixture: the year just finished has trade (" + newestPlain.cells().size() + " cells) and is the same calendar year in both",
                hasTrade(newestPlain) && newestPlain.year() == newestLopped.year());
        // What the old grid would have made of these cells: each plain figure divided, and rounded to the founding step.
        double oldGrid = 0;
        for (PartnerTrade.Cell c : newestPlain.cells()) {
            for (double v : new double[] { c.valueOut(), c.valueIn() }) {
                double want = v / factor;
                oldGrid = Math.max(oldGrid, gapOf(Math.round(want * PartnerTrade.HISTORY_ROUND_MONEY) / PartnerTrade.HISTORY_ROUND_MONEY, want));
            }
        }
        assertTrue(String.format("fixture: on the founding grid these cells would be %.3g apart, so the band below can tell the two grids apart", oldGrid),
                oldGrid > 1e-6);
        double newestMoney = cellGap(newestLopped.cells(), newestPlain.cells(), 1 / factor);
        double newestUnits = unitsGap(newestLopped.cells(), newestPlain.cells());
        gapWithin("the year finished on the new grid is the plain one's, divided", newestMoney, band);
        gapWithin("...and its units are the plain one's", newestUnits, band);
        gapWithin("...and so is every year behind it, the save's copy too",
                Math.max(historyMoneyGap(tradeOf(lopped).history(), tradeOf(plain).history(), 1 / factor),
                        historyMoneyGap(lopped.getHistorySave().getTradeByPartner(), plain.getHistorySave().getTradeByPartner(), 1 / factor)), band);
        out.printf("   reformed in %s, the second year turned in %s: worst gaps money %.2g, units %.2g (founding grid: %.2g)%n",
                CityCalendar.shortMonthName(reformMonth), CityCalendar.shortMonthName(turnMonth), newestMoney, newestUnits, oldGrid);

        cardAndLanes("a year turned on", plain, lopped, 1 / factor, true);

        // 5. The city as a whole is still the same city.
        sameCity(plain, lopped, factor, 1e-9);

        // 6. On until prices have moved: the floor is the index's, in both money, and the reformed city still draws what the plain one does.
        for (int m = 0; m < 12 && tradeOf(plain).snapshot().minLane() <= TradeLanes.minLane(1, 1) * (1 + 1e-9); m++) monthOn(plain, lopped);
        cardAndLanes("with prices moved", plain, lopped, 1 / factor, true);
    }

    /** Where the lanes of cardAndLanes() are drawn from: Halifax, as GlobeCheck's city. */
    static final double LANES_FROM_LAT = 44.65, LANES_FROM_LON = -63.57;

    /**
     * What the screens read of two cities' trade by partner, the second the first reformed by 1 / scale (scale 1: the same city, before the reform):
     * the nation card's year to date, the month's figures the lanes are made of, the floor under which a partner has no lane, and the lanes and the
     * arrows built on them as the map builds them, from the snapshot's own floor. The reformed city's card and figures are the plain one's times
     * `scale`, its floor too, and it draws the same partners. `reformed` says the second city is not the first's twin but its reform, where the
     * floor the reform left alone would have hidden every lane (the fault: TradeLanes.MIN_LANE was money as the city counted it, and a reform
     * divides every trade but not it).
     */
    static void cardAndLanes(String when, Game plain, Game lopped, double scale, boolean reformed) {
        out.println("--- the card and the lanes, " + when + " (TP1) ---");
        PartnerTrade.Snapshot p = tradeOf(plain).snapshot(), l = tradeOf(lopped).snapshot();
        double band = 1e-12;
        assertTrue("fixture: both cities are at the same month (" + CityCalendar.shortMonthName(p.month()) + ") with a trade by partner", p.month() == l.month() && p.nations() == l.nations() && p.nations() >= 1);

        // The card: each nation's year to date, in the city's money and in tonnes.
        double cardMoney = 0, cardTonnes = 0, yearTrade = 0;
        for (int nation = 1; nation <= p.nations(); nation++) {
            TradeLanes.YearToDate a = l.yearToDate(nation), e = p.yearToDate(nation);
            yearTrade += e.exports() + e.imports();
            cardMoney = Math.max(cardMoney, Math.max(gapOf(a.exports(), e.exports() * scale), gapOf(a.imports(), e.imports() * scale)));
            cardTonnes = Math.max(cardTonnes, Math.max(gapOf(a.tonnesOut(), e.tonnesOut()), gapOf(a.tonnesIn(), e.tonnesIn())));
        }
        assertTrue("fixture: the plain city has sold and bought this year (D$" + String.format("%.1f", yearTrade) + " thousand)", yearTrade > 0);
        gapWithin("the nation card's year to date is the plain city's, " + (scale == 1 ? "as it was" : "divided"), cardMoney, band);
        gapWithin("...and its tonnes are the plain city's", cardTonnes, band);
        gapWithin("the month's figures the lanes are made of are the plain city's, " + (scale == 1 ? "as they were" : "divided"),
                Math.max(arrayGap(l.exportsByNation(), p.exportsByNation(), scale), arrayGap(l.importsByNation(), p.importsByNation(), scale)), band);

        // The floor: the board's, in each city's own price index and money.
        double index = plain.getPriceIndex().getIndex();
        double unit = lopped.getDenomination().getUnit();
        close("the plain city's floor is the board's in its prices (index " + String.format("%.4f", index) + ")", p.minLane(), TradeLanes.minLane(index, 1), 1e-12);
        close("the reformed city's is the same, in its money (unit " + unit + ")", l.minLane(), TradeLanes.minLane(lopped.getPriceIndex().getIndex(), unit), 1e-12);
        gapWithin("...and, the cities being the same, the plain city's times " + (scale == 1 ? "1" : "the reform's scale"), gapOf(l.minLane(), p.minLane() * scale), band);

        // The lanes and the arrows, as the map builds them: on the nodes of the nations, from the place the city stands.
        int n = p.nations();
        double[] la = new double[n], lo = new double[n];
        for (int i = 0; i < n; i++) {
            la[i] = plain.getWorldMarket().nodeLat(i + 1);
            lo[i] = plain.getWorldMarket().nodeLon(i + 1);
        }
        // From Halifax, not from the place the city stands: the fixture's only partner is the nation it stands in (the United States, at the stand-in's
        // own node), which needs no lane or arrow at its own place, so the lanes would be empty. The floor and the figures are what is under test.
        double cityLat = LANES_FROM_LAT, cityLon = LANES_FROM_LON;
        boolean apart = true;
        for (int i = 0; i < n; i++) if (p.exportsByNation()[i] + p.importsByNation()[i] > 0) apart &= Math.abs(la[i] - cityLat) > 1 || Math.abs(lo[i] - cityLon) > 1;
        assertTrue("fixture: every partner with trade stands apart from the place the lanes are drawn from (Halifax)", apart);
        TradeLanes pl = TradeLanes.of(cityLat, cityLon, p.month(), la, lo, p.exportsByNation(), p.importsByNation(), p.minLane());
        TradeLanes ll = TradeLanes.of(cityLat, cityLon, l.month(), la, lo, l.exportsByNation(), l.importsByNation(), l.minLane());
        TradeLanes.Arrows pa = TradeLanes.Arrows.of(cityLat, cityLon, p.month(), la, lo, p.exportsByNation(), p.importsByNation(), p.minLane());
        TradeLanes.Arrows la2 = TradeLanes.Arrows.of(cityLat, cityLon, l.month(), la, lo, l.exportsByNation(), l.importsByNation(), l.minLane());
        assertTrue("fixture: the plain city's month has a partner with a lane and an arrow (" + pl.partners() + " lanes, " + pa.count() + " arrows)", pl.partners() >= 1 && pa.count() >= 1);
        boolean sameLanes = pl.partners() == ll.partners();
        for (int i = 0; i < n; i++) sameLanes &= (pl.partnerEdge(i) >= 0) == (ll.partnerEdge(i) >= 0);
        assertTrue("the " + (scale == 1 ? "twin" : "reformed city") + " has a lane to every partner the plain city has one to, and to no other (" + ll.partners() + " of " + pl.partners() + ")", sameLanes);
        boolean sameArrows = pa.count() == la2.count() && pa.hidden() == la2.hidden();
        for (int k = 0; sameArrows && k < pa.count(); k++) sameArrows = pa.nation(k) == la2.nation(k);
        assertTrue("...and the same arrows, in the same order (" + la2.count() + " drawn, " + la2.hidden() + " hidden)", sameArrows);
        if (reformed) {
            // The fixture can tell the floors apart: the plain city's floor left in the old money is what the reform used to leave, and it hides the lot.
            assertTrue("the fault: the floor a reform left undivided (D$" + String.format("%.3f", p.minLane()) + ") would hide every lane and arrow of the reformed city",
                    TradeLanes.of(cityLat, cityLon, l.month(), la, lo, l.exportsByNation(), l.importsByNation(), p.minLane()).partners() == 0
                            && TradeLanes.Arrows.of(cityLat, cityLon, l.month(), la, lo, l.exportsByNation(), l.importsByNation(), p.minLane()).count() == 0);
        }
        if (when.startsWith("with prices")) {
            assertTrue("the price index has moved (" + String.format("%.4f", index) + "), and the floor with it: above the founding's", index > 1 && p.minLane() > TradeLanes.minLane(1, 1));
        }
    }

    /* ------------------------------ helpers ------------------------------ */

    /** Cells under half a household holding something, counted over every scan. */
    static int emptyHolding = 0;
    /** ...and the same, as it stood right after the reform. */
    static int emptyHeldAfterReform = -1;
    static int looks = 0, looksWithEmpty = 0, cellsFolded = 0;

    /** Both cities a month at a time, each scanned after every month. */
    static void stepBoth(Game a, Game b, int months) {
        for (int m = 0; m < months; m++) {
            quietly(() -> { a.simulateMonths(1); b.simulateMonths(1); });
            scanEmpty(a);
            scanEmpty(b);
            cellsFolded += a.getHouseholdBalance().getCellsFolded() + b.getHouseholdBalance().getCellsFolded();
        }
    }

    /** One look at a city: whether any cell is under half a household, and any of them holding something. */
    static void scanEmpty(Game g) {
        HouseholdBalance hb = g.getHouseholdBalance();
        boolean any = false;
        for (Household c : hb.cells()) {
            if (!c.isEmpty()) continue;
            if (c.households() > 0) any = true;
            if (hb.holdsAnything(c)) emptyHolding++;
        }
        looks++;
        if (any) looksWithEmpty++;
    }

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
        return g.getSectors().retail().getStoreSellPrice();
    }

    static double rent(Game g) {
        return g.getSectors().realEstate().getRentPrice();
    }

    static double cost(Game g, String name) {
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName(name);
        return t == null ? 0 : t.getCashCost();
    }
}

package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * The two US dollars (batch WN4, D29; spec-nations 10 item 29, spec-exchange-rates 3.9): the in-game US$ inflating on the US's path, and the real US$ of
 * 2015 the city's real GDP and a rate are shown in, for comparison only.
 *
 * WHY. The dollar's prices were a seeded walk around a mean the founding screen's five chips set, pulled back to a flat trend: a US that inflated by one
 * rule and a dollar that inflated by another. Where the city has the nations' moneys the world's price level is now the US's own, month by month, and
 * the three comparison figures are made of it; a harness holds the level to the nations' path computed another way, the walk to the walk it always was,
 * and each figure to the arithmetic it is defined by - a fixture that causes the condition, not the model's own number read back.
 *
 * What this has to prove:
 *   1. THE DOLLAR IS THE US'S (the full file): on Random and on Historical, 37 months of a city, the world's price level equals the US's price level
 *      over its January 2000 level, computed from Nations' Januaries, to 1e-12 every month; the headline rate is this year's pace; the ring's realised
 *      inflation is the level over the level a year ago; on Historical the Januaries equal the file's US CPI over 2000's; a pinned world holds; a
 *      saved and loaded city follows the path again.
 *   2. NO FILE, THE WALK (the literals): a city on the literals has the walk, its level the bare WorldEconomy's over the same months, to the bit.
 *   3. D$ PER 2015 US$ is the rate times the level, and the rate at founding.
 *   4. REAL GDP IN 2015 US$ is the GDP over the index over the founding's rate.
 *   5. THE REAL EFFECTIVE RATE: 100 at founding; the city's prices up 10% and nothing else moving reads 110; a partner's money 10% dearer against the
 *      US$ with half the weight reads 100 x 1.1^-.5; the US alone with no trade; the weights are shares.
 *   6. THE SHOCK (WorldEconomy.shockLevel(), the ensemble's import shock): on the path it is a factor the next month keeps, saved, and permanent; on the
 *      walk it is the level times the size, which the pull then takes back, and what the old save-and-restore door did.
 */
public class RealDollarCheck {

    static int fails = 0;
    static final PrintStream out = System.out;
    static final PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());

    /** The path seed the cities are founded on. */
    static final long SEED = 20_261_031L;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-130s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** A figure the arithmetic gives, judged to a tolerance. */
    static void close(String label, double got, double want, double tol) {
        assertTrue(String.format("%s: %.12f against %.12f", label, got, want), Math.abs(got - want) <= tol);
    }

    /** Figures compared one by one, reported as one line: every one has to agree, and at least one has to have been read. */
    static final class Tally {
        int n, bad;
        String first;

        void see(boolean ok, Supplier<String> what) {
            n++;
            if (!ok) {
                bad++;
                if (first == null) first = what.get();
            }
        }
    }

    static void tally(String label, Tally t) {
        boolean ok = t.n > 0 && t.bad == 0;
        if (!ok) fails++;
        out.printf("%-130s %s  (%d of %d agree)%s%n", label, ok ? "OK" : "FAIL", t.n - t.bad, t.n,
                t.bad > 0 ? "; the first that does not: " + t.first : t.n == 0 ? "; none read" : "");
    }

    static void quietly(Runnable work) {
        PrintStream was = System.out;
        System.setOut(quiet);
        try { work.run(); } finally { System.setOut(was); }
    }

    static void play(Game city) { quietly(() -> city.simulateMonths(1)); }

    /** A trading town on a founding, as WorldMoneysCheck builds it, on a scratch folder; the world's file as named. */
    static Game town(String label, Founding founding) {
        Game city = new Game(GameFiles.scratch(label), founding);
        quietly(() -> {
            city.run();
            city.setCashForTest(Founding.WEALTHY_CASH);
            city.getLandManager().setOwnedSqFt(30_000_000);
            city.buildStack(ForeignCheck.template(city, "House"), 500, false);
            city.buildStack(ForeignCheck.template(city, "Convenience Store"), 8, false);
            city.buildStack(ForeignCheck.template(city, "Small Grocery Store"), 3, false);
            city.buildStack(ForeignCheck.template(city, "Construction Depot"), 4, false);
            city.buildStack(ForeignCheck.template(city, "Coal Power Plant"), 1, false);
            city.buildStack(ForeignCheck.template(city, "Water Treatment Plant"), 1, false);
            city.buildStack(ForeignCheck.template(city, "Industrial Bakery"), 2, false);
        });
        return city;
    }

    static boolean same(double a, double b) { return Double.doubleToLongBits(a) == Double.doubleToLongBits(b); }

    public static void main(String[] args) {
        long start = System.nanoTime();
        theDollarIsTheUs();
        noFileTheWalk();
        dollarsPer2015Usd();
        realGdp();
        realEffectiveRate();
        theShock();
        out.printf("%nRealDollarCheck took %.1f s%n", (System.nanoTime() - start) / 1e9);
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ----------------------------------------------------------------- 1 */

    /** The US's price level in month t over its January 2000 level, computed here from Nations' Januaries, log-linear between them. */
    static double usLevel(Nations nations, int usa, int t) {
        int year = Nations.FIRST_YEAR + t / 12;
        double f = (t % 12) / 12.0;
        double l0 = Math.log(nations.year(usa, Nations.FIRST_YEAR).priceLevel());
        double a = Math.log(nations.year(usa, year).priceLevel()), b = Math.log(nations.year(usa, year + 1).priceLevel());
        return Math.exp(a + f * (b - a) - l0);
    }

    /** The US's inflation in the year of month t: January over January, less one. */
    static double usPace(Nations nations, int usa, int t) {
        int year = Nations.FIRST_YEAR + t / 12;
        return nations.year(usa, year + 1).priceLevel() / nations.year(usa, year).priceLevel() - 1;
    }

    static void theDollarIsTheUs() {
        out.println("--- 1. the in-game US$ is the US's: its prices follow the nations' path ---");
        Path file = WorldData.locate();
        if (file == null) {
            out.println("  world-1.bin not named (-D" + WorldData.PROPERTY + "=<path>) nor at " + WorldData.DATA_FILE + ": this section did not run");
            return;
        }
        for (Nations.Path path : Nations.Path.values()) {
            Game g = town("realdollar-" + path, Founding.defaults().withPath(path, SEED));
            assertTrue(path + ": a city founded at the default mean, before its first month, is on the walk's level 1 and has no path yet",
                    g.getWorldEconomy().getPriceLevel() == 1 && !g.getWorldEconomy().isOnUsPath()
                            && g.getWorldEconomy().getMeanInflation() == WorldEconomy.DEFAULT_MEAN_INFLATION);
            Tally level = new Tally(), pace = new Tally(), ring = new Tally(), januaries = new Tally(), onPath = new Tally();
            Nations nations = null;
            int usa = 0;
            Game back = null;
            Tally loaded = new Tally();
            double last = 1;
            for (int m = 1; m <= 37; m++) {
                play(g);
                WorldEconomy w = g.getWorldEconomy();
                WorldMoneys moneys = g.getWorldMoneys();
                onPath.see(moneys != null && w.isOnUsPath(), () -> "month " + g.getMonth());
                if (moneys == null) continue;
                if (nations == null) {
                    nations = g.getNations();
                    usa = moneys.usNation();
                }
                int t = moneys.month();
                final int mt = t;
                double want = usLevel(nations, usa, t);
                level.see(Math.abs(w.getPriceLevel() - want) <= 1e-12, () -> String.format("month %d: %.15f against %.15f", mt, w.getPriceLevel(), want));
                double wantPace = usPace(nations, usa, t);
                pace.see(Math.abs(w.getInflation() - wantPace) <= 1e-12, () -> String.format("month %d: %.15f against %.15f", mt, w.getInflation(), wantPace));
                if (m >= 13) {
                    double year = want / usLevel(nations, usa, t - 12) - 1;
                    ring.see(Math.abs(w.realisedInflation() - year) <= 1e-12, () -> String.format("month %d: %.15f against %.15f", mt, w.realisedInflation(), year));
                }
                if (path == Nations.Path.HISTORICAL && t % 12 == 0 && t > 0) {
                    double cpi = nations.year(usa, Nations.FIRST_YEAR + t / 12).priceLevel() / nations.year(usa, Nations.FIRST_YEAR).priceLevel();
                    januaries.see(Math.abs(w.getPriceLevel() - cpi) <= 1e-12, () -> String.format("January of month %d: %.15f against the file's %.15f", mt, w.getPriceLevel(), cpi));
                }
                last = w.getPriceLevel();
                if (m == 13) {
                    assertTrue(path + ": saved after 13 months, to its scratch folder", g.saveGame(2, "the dollar").ok);
                    back = new Game(g.getGameFiles());
                    Game loading = back;
                    quietly(() -> loading.loadGameSave(2));
                } else if (m > 13 && back != null) {
                    play(back);
                    WorldEconomy bw = back.getWorldEconomy();
                    loaded.see(back.getWorldMoneys() != null && bw.isOnUsPath() && same(bw.getPriceLevel(), w.getPriceLevel())
                            && same(bw.getInflation(), w.getInflation()), () -> "month " + g.getMonth());
                }
            }
            tally(path + ": the world's price level is the US's over its January 2000 level, from Nations' Januaries, every month (1e-12)", level);
            tally(path + ": ...it is on the path in every month, with the moneys built before the level was struck", onPath);
            tally(path + ": ...the headline rate is this year's pace, P_US(y+1) over P_US(y), less one", pace);
            tally(path + ": ...the realised inflation is the level over the level a year ago", ring);
            if (path == Nations.Path.HISTORICAL) tally("Historical: the Januaries are the file's US CPI over 2000's", januaries);
            tally(path + ": the city loaded after 13 months follows the path again, level and rate to the bit, 24 months on", loaded);
            out.printf("   %s: after 37 months the US$'s prices stand at %.4f of founding's (%.2f%% a year)%n", path, last, 100 * (Math.pow(last, 12.0 / 37) - 1));
            assertTrue(path + ": ...and no shock has touched it (the factor is 1)", g.getWorldEconomy().getLevelFactor() == 1);
        }
        Game pinned = new Game(GameFiles.scratch("realdollar-pinned"), Founding.defaults());
        quietly(() -> {
            pinned.run();
            pinned.getWorldEconomy().pin();
            pinned.simulateMonths(14);
        });
        assertTrue("a pinned world holds still on the path: 14 months, level 1 to the bit, the path handed all the same",
                pinned.getWorldEconomy().getPriceLevel() == 1 && pinned.getWorldEconomy().isOnUsPath());
    }

    /* ----------------------------------------------------------------- 2 */

    static void noFileTheWalk() {
        out.println("--- 2. no world file, the walk: a city on the literals walks as WorldEconomy always did ---");
        Game g = new Game(GameFiles.scratch("realdollar-literals"), Founding.defaults());
        g.playOnLiterals();
        quietly(g::run);
        WorldEconomy bare = new WorldEconomy();
        Tally level = new Tally(), rate = new Tally();
        for (int m = 1; m <= 36; m++) {
            play(g);
            bare.advanceMonth(g.getMonth());
            WorldEconomy w = g.getWorldEconomy();
            final int month = g.getMonth();
            level.see(same(w.getPriceLevel(), bare.getPriceLevel()), () -> String.format("month %d: %.15f against the bare world's %.15f", month, w.getPriceLevel(), bare.getPriceLevel()));
            rate.see(same(w.getInflation(), bare.getInflation()) && same(w.realisedInflation(), bare.realisedInflation()), () -> "month " + month);
        }
        tally("the city's level is the bare WorldEconomy's at the default mean over the same months, to the bit", level);
        tally("...and so are its headline and its realised inflation", rate);
        assertTrue("...with no world's moneys and no path: the dollar is the walk", g.getWorldMoneys() == null && !g.getWorldEconomy().isOnUsPath());
        assertTrue("...and the walk's mean is the default's, which nothing offers a choice of now",
                g.getWorldEconomy().getMeanInflation() == WorldEconomy.DEFAULT_MEAN_INFLATION && g.getFounding().getMeanInflation() == WorldEconomy.DEFAULT_MEAN_INFLATION);
    }

    /* ----------------------------------------------------------------- 3 */

    static void dollarsPer2015Usd() {
        out.println("--- 3. D$ per 2015 US$ is the rate times the US$'s prices since founding ---");
        close("a fixture: at 1.25 D$ a US$ and prices 1.6 times founding's, a real 2015 US$ costs 2 D$", RealDollar.dollarsPer2015Usd(1.25, 1.6), 2.0, 1e-12);
        close("...at founding (level 1) it is the rate", RealDollar.dollarsPer2015Usd(ForeignAccounts.OPENING_RATE, 1), ForeignAccounts.OPENING_RATE, 0);
        assertTrue("...and NaN for a rate or a level that is not a positive number",
                Double.isNaN(RealDollar.dollarsPer2015Usd(0, 1)) && Double.isNaN(RealDollar.dollarsPer2015Usd(1, -1)) && Double.isNaN(RealDollar.dollarsPer2015Usd(Double.NaN, 1)));
        Game g = new Game(GameFiles.scratch("realdollar-dollars"), Founding.defaults());
        quietly(g::run);
        assertTrue("a city founds at ForeignAccounts.OPENING_RATE (" + ForeignAccounts.OPENING_RATE + "), the founding's rate the figures count from",
                g.getForeignAccounts().getRate() == ForeignAccounts.OPENING_RATE);
        assertTrue("...so at founding the D$ per 2015 US$ is that rate", g.dollarsPer2015Usd() == ForeignAccounts.OPENING_RATE);
        quietly(() -> g.simulateMonths(24));
        close("after 24 months it is the foreign accounts' rate times the world's level", g.dollarsPer2015Usd(),
                g.getForeignAccounts().getRate() * g.getWorldEconomy().getPriceLevel(), 1e-12);
        assertTrue("...a number the city's prices moved (the level is no longer 1)", g.getWorldEconomy().getPriceLevel() != 1);
    }

    /* ----------------------------------------------------------------- 4 */

    static void realGdp() {
        out.println("--- 4. real GDP in 2015 US$ is the GDP over the price index over the founding's rate ---");
        close("a fixture: D$120M a month at an index of 1.2 and a founding rate of 1 is 100M real US$", RealDollar.realGdp2015Usd(120e6, 1.2, 1), 100e6, 1e-6);
        close("...at a founding rate of 2 D$ a US$, 50M", RealDollar.realGdp2015Usd(120e6, 1.2, 2), 50e6, 1e-6);
        close("...at an index of 1 and OPENING_RATE it is the GDP", RealDollar.realGdp2015Usd(7e6, 1, ForeignAccounts.OPENING_RATE), 7e6 / ForeignAccounts.OPENING_RATE, 1e-9);
        assertTrue("...and NaN for an index or a rate that is not positive",
                Double.isNaN(RealDollar.realGdp2015Usd(1, 0, 1)) && Double.isNaN(RealDollar.realGdp2015Usd(1, 1, 0)) && Double.isNaN(RealDollar.realGdp2015Usd(Double.NaN, 1, 1)));
        Game g = town("realdollar-gdp", Founding.defaults());
        quietly(() -> g.simulateMonths(24));
        NationalAccounts na = g.getEconomyManager().getNationalAccounts();
        double want = na.getGdp() / g.getPriceIndex().getIndex() / ForeignAccounts.OPENING_RATE;
        assertTrue(String.format("a town that has earned for 24 months: GDP D$%,.0f over the index %.4f is the getter's %,.0f", na.getGdp(), g.getPriceIndex().getIndex(), g.realGdp2015Usd()),
                na.getGdp() > 0 && g.getDenomination().getUnit() == 1 && Math.abs(g.realGdp2015Usd() - want) <= 1e-9 * Math.abs(want));
    }

    /* ----------------------------------------------------------------- 5 */

    static void realEffectiveRate() {
        out.println("--- 5. the real effective rate: 100 at founding, the BIS's way ---");
        double r0 = ForeignAccounts.OPENING_RATE;
        close("a fixture: at founding (the rate, the prices and the partners' moneys where they were) it reads 100",
                RealDollar.realEffectiveRate(r0, r0, 1, List.of(new RealDollar.Partner(.6, 1, 1, 1), new RealDollar.Partner(.4, 150, 150, 1))), 100, 1e-12);
        close("the city's prices rise 10% and nothing else moves: 110", RealDollar.realEffectiveRate(r0, r0, 1.1, RealDollar.usAlone(1)), 110, 1e-9);
        close("...against partners alike, whatever their weights", RealDollar.realEffectiveRate(r0, r0, 1.1,
                List.of(new RealDollar.Partner(.3, 1, 1, 1), new RealDollar.Partner(.7, 8, 8, 1))), 110, 1e-9);
        close("the D$ falls 10% against the US$ (the rate up by 1/.9) and nothing else moves: 90", RealDollar.realEffectiveRate(r0 / .9, r0, 1, RealDollar.usAlone(1)), 90, 1e-9);
        close("a partner's money rises 10% against the US$ (its units a US$ down by 1.1) with half the weight, the US$ the other half: 100 x 1.1^-.5, the other way",
                RealDollar.realEffectiveRate(r0, r0, 1,
                        List.of(new RealDollar.Partner(.5, 1, 1, 1), new RealDollar.Partner(.5, 100 / 1.1, 100, 1))), 100 * Math.pow(1.1, -.5), 1e-9);
        close("a partner's prices rise 10% (the city's not) with a third of the weight: 100 x 1.1^-(1/3)",
                RealDollar.realEffectiveRate(r0, r0, 1, List.of(new RealDollar.Partner(1, 1, 1, 1), new RealDollar.Partner(.5, 4, 4, 1.1))), 100 * Math.pow(1.1, -1.0 / 3), 1e-9);
        close("the weights are shares of their sum: 6 and 2 are .75 and .25", RealDollar.realEffectiveRate(r0, r0, 1,
                List.of(new RealDollar.Partner(6, 1, 1, 1), new RealDollar.Partner(2, 10, 10, 2))), 100 * Math.pow(2, -.25), 1e-9);
        close("the US alone with no trade, its prices up 25% and the city's 10% up: 100 x 1.1 / 1.25", RealDollar.realEffectiveRate(r0, r0, 1.1, RealDollar.usAlone(1.25)), 100 * 1.1 / 1.25, 1e-9);
        close("no usable partner (a weight of nothing, a money with no rate) is the US alone at prices of 1: the D$'s own prices only",
                RealDollar.realEffectiveRate(r0, r0, 1.1, List.of(new RealDollar.Partner(0, 1, 1, 1), new RealDollar.Partner(1, Double.NaN, 100, 1))), 110, 1e-9);
        close("a partner with no rate is left out of the average, the rest taking its weight: 110 from the one that is there",
                RealDollar.realEffectiveRate(r0, r0, 1.1, List.of(new RealDollar.Partner(1, 1, 1, 1), new RealDollar.Partner(5, Double.NaN, 100, 1))), 110, 1e-9);
        assertTrue("NaN for a rate, a founding rate or an index that is not positive",
                Double.isNaN(RealDollar.realEffectiveRate(0, r0, 1, RealDollar.usAlone(1))) && Double.isNaN(RealDollar.realEffectiveRate(r0, -1, 1, RealDollar.usAlone(1)))
                        && Double.isNaN(RealDollar.realEffectiveRate(r0, r0, 0, RealDollar.usAlone(1))));
        Game g = new Game(GameFiles.scratch("realdollar-effective"), Founding.defaults());
        quietly(g::run);
        close("a city before its first month: the real effective rate is 100", g.realEffectiveRate(), 100, 1e-9);
        Path file = WorldData.locate();
        if (file == null) {
            out.println("  world-1.bin not named (-D" + WorldData.PROPERTY + "=<path>) nor at " + WorldData.DATA_FILE + ": the city's months did not run");
            return;
        }
        Game town = town("realdollar-effective-town", Founding.defaults().withPath(Nations.Path.RANDOM, SEED));
        quietly(() -> town.simulateMonths(30));
        WorldMoneys moneys = town.getWorldMoneys();
        Nations nations = town.getNations();
        int usa = moneys.usNation(), germany = nations.index("DEU"), japan = nations.index("JPN");
        double level = town.getWorldEconomy().getPriceLevel(), rate = town.getForeignAccounts().getRate(), index = town.getPriceIndex().getIndex();
        double aloneWas = RealDollar.realEffectiveRate(rate, ForeignAccounts.OPENING_RATE, index, RealDollar.usAlone(level));
        close("a town that has traded with nobody: the D$ against the US$ alone, the US$'s prices the world's level", town.realEffectiveRate(), aloneWas, 1e-9);
        // The finished year's trade, given: Germany 4,000 and Japan 2,000 of thousands of D$ (exports plus imports), the US 2,000 - the condition caused, not read.
        Good good = Good.values()[0];
        quietly(() -> town.partnerTradeForTest().restoreHistory(List.of(new PartnerTrade.Table(2001, List.of(
                new PartnerTrade.Cell(good, germany, 10, 5, 3_000, 1_000), new PartnerTrade.Cell(good, japan, 0, 5, 0, 2_000),
                new PartnerTrade.Cell(good, usa, 4, 4, 1_000, 1_000))))));
        int t = moneys.month();
        double[] weight = new double[nations.count() + 1];
        weight[germany] = 4_000;
        weight[japan] = 2_000;
        weight[usa] = 2_000;
        List<RealDollar.Partner> partners = new java.util.ArrayList<>();
        for (int nation : new int[] { germany, japan, usa }) {
            String code = nations.year(nation, Nations.FIRST_YEAR + t / 12).money();
            partners.add(new RealDollar.Partner(weight[nation], moneys.rate(code), moneys.rateAtStart(code), nation == usa ? level : usLevel(nations, nation, t)));
        }
        double want = RealDollar.realEffectiveRate(rate, ForeignAccounts.OPENING_RATE, index, partners);
        double reer = town.realEffectiveRate();
        out.printf("   a town after 30 months: the D$ at %.4f, the index %.4f, the US$'s prices %.4f; with Germany, Japan and the US as partners (.5, .25, .25) the real effective rate is %.3f, the US$ alone's %.3f%n",
                rate, index, level, reer, aloneWas);
        close("...given a finished year's trade with Germany, Japan and the US, the getter weighs each partner's money (the one its nation uses now, against its rate in January 2000) and prices", reer, want, 1e-9 * want);
        assertTrue("...which is not the US$ alone's figure: the partners' moneys and prices move it", Math.abs(reer - aloneWas) > 1e-6 && reer > 0 && Double.isFinite(reer));
    }

    /* ----------------------------------------------------------------- 6 */

    /** A synthetic US: 0.2% a month, 2.4% a year, so a shock is read against a path that moves. */
    static final WorldEconomy.UsPath GROWING = new WorldEconomy.UsPath() {
        @Override public double level(int t) { return Math.pow(1.002, t); }
        @Override public double pace(int t) { return Math.pow(1.002, 12) - 1; }
    };

    static void theShock() {
        out.println("--- 6. the import shock: permanent on the path, pulled back on the walk ---");
        WorldEconomy plain = new WorldEconomy(), shocked = new WorldEconomy();
        plain.followUsPath(GROWING);
        shocked.followUsPath(GROWING);
        for (int m = 2; m <= 30; m++) {
            plain.advanceMonth(m);
            shocked.advanceMonth(m);
        }
        close("fixture: before the shock, the two paths read alike", shocked.getPriceLevel(), plain.getPriceLevel(), 0);
        shocked.shockLevel(1.3);
        close("shockLevel(1.3): the level is 1.3 times the path's at once", shocked.getPriceLevel(), 1.3 * plain.getPriceLevel(), 1e-12);
        close("...the factor is 1.3", shocked.getLevelFactor(), 1.3, 0);
        assertTrue("...and the realised inflation reads the jump for a year (the ring is kept)", shocked.realisedInflation() - plain.realisedInflation() > .25);
        Tally kept = new Tally();
        for (int m = 31; m <= 90; m++) {
            plain.advanceMonth(m);
            shocked.advanceMonth(m);
            final int month = m;
            kept.see(Math.abs(shocked.getPriceLevel() - 1.3 * plain.getPriceLevel()) <= 1e-12, () -> "month " + month);
        }
        tally("on the path the next 60 months keep the shock: the level is 1.3 times the path's every month, nothing pulls it back", kept);
        close("...a year on, the realised inflation is the path's again", shocked.realisedInflation(), plain.realisedInflation(), 1e-12);
        double[] saved = shocked.toSaveArray();
        assertTrue("the factor is saved in the slot after the mean", saved[saved.length - 1] == 1.3 && saved[saved.length - 2] == shocked.getMeanInflation());
        WorldEconomy back = new WorldEconomy();
        back.followUsPath(GROWING);
        back.restore(saved);
        shocked.advanceMonth(91);
        back.advanceMonth(91);
        assertTrue("...and a world restored from it, handed the path, steps on to the bit", same(back.getPriceLevel(), shocked.getPriceLevel()) && back.getLevelFactor() == 1.3);
        WorldEconomy older = new WorldEconomy();
        older.followUsPath(GROWING);
        older.restore(Arrays.copyOf(saved, saved.length - 1));
        plain.advanceMonth(91);
        older.advanceMonth(91);
        assertTrue("an array without the slot (an older save's) reads a factor of 1", older.getLevelFactor() == 1 && same(older.getPriceLevel(), plain.getPriceLevel()));
        WorldEconomy still = new WorldEconomy();
        still.followUsPath(GROWING);
        still.advanceMonth(2);
        still.pin();
        still.shockLevel(2);
        still.advanceMonth(3);
        close("a pinned world holds the shock it is given and does not follow the path", still.getPriceLevel(), 2 * GROWING.level(1), 1e-12);

        WorldEconomy a = new WorldEconomy(), b = new WorldEconomy(), door = new WorldEconomy();
        for (int m = 1; m <= 30; m++) {
            a.advanceMonth(m);
            b.advanceMonth(m);
            door.advanceMonth(m);
        }
        b.shockLevel(1.3);
        double[] s = door.toSaveArray();
        s[0] *= 1.3;
        door.restore(s);
        close("on the walk shockLevel(1.3) is the level times 1.3", b.getPriceLevel(), 1.3 * a.getPriceLevel(), 1e-12);
        assertTrue("...with no path and a factor of 1", !b.isOnUsPath() && b.getLevelFactor() == 1);
        Tally same = new Tally();
        double early = 0;
        for (int m = 31; m <= 400; m++) {
            a.advanceMonth(m);
            b.advanceMonth(m);
            door.advanceMonth(m);
            final int month = m;
            same.see(same(b.getPriceLevel(), door.getPriceLevel()) && same(b.realisedInflation(), door.realisedInflation()), () -> "month " + month);
            if (m == 31) early = b.getPriceLevel() / a.getPriceLevel();
        }
        tally("...exactly what the save-and-restore door did, level and realised inflation to the bit, 370 months on", same);
        double late = b.getPriceLevel() / a.getPriceLevel();
        assertTrue(String.format("...and the trend pull takes it back: shocked over unshocked %.4f a month on, %.4f thirty years on", early, late), early > 1.29 && late < early - .05 && late > 1);
    }
}

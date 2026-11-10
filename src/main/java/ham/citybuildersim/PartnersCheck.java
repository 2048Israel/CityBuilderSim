package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * The model the Trade screen's Partners board, a nation's page and The reserves read (batch WN6, "Nations: the screens"; docs/studies/spec-nations.md 8,
 * boards 2 and 3, 9, D29): each good's curve at the city's site (WorldMarket.Curve), the partners table and a nation's page (PartnersView), and the
 * vault in eight (VaultView). No screen is drawn here and nothing in the month reads any of it: this holds the figures a screen will print, to the
 * model's own getters, so that a screen never has to recompute one.
 *
 * WHY. The band was two numbers and the allocation an array; the sinks, where each buyer joins, who sets the level and the world's line were in the
 * market's private fills, read only by a harness on the model's thread. A screen that rebuilt them would carry its own idea of the sale side; one that
 * read the live fills from its own thread would read half a rebuild. So the curve is a snapshot, published whole, whose floors and ceilings and
 * allocations are the band's and allocate()'s own code, and every other board figure here is held to the getter it comes from.
 *
 * What this has to prove:
 *   1. THE CURVE IS THE BAND (a world-file city, Historical, a fixed seed, run to trade): for every good with a curve, at last month's exports and
 *      imports and at volumes up the world's use, floorAt/ceilingAt equal band() to the bit and buyersAt/sellersAt equal allocate()'s arrays to the
 *      bit; the buyers taking at a volume are the sinks above the level and each nets at least it; the joins ascend; the next buyers all join beyond the
 *      volume and none is taking; the month's line never rises with the volume and the world's line is never above it; the grid ends where the level does.
 *   2. A LOADED CITY HAS ITS CURVES: saved to a scratch folder and loaded, before any month, the curves are the original's to the bit (sinks, sellers,
 *      floors, grid).
 *   3. THE PARTNERS TABLE FOOTS, both spans: the rows plus the no-partner remainder are the snapshot's month (or year); the order is by what was sold
 *      and bought, ranks 1..n; each row's money is its nation's this year and its mark the eight's or its code; a policy rate exactly where the money
 *      is one of the eight; D$ a unit times units a US$ is D$ a US$; its distance is the great circle to its node.
 *   4. A NATION'S PAGE: its goods foot to its year's totals; its years are the history's; its ranks are the map card's (a NationsMap built on the file)
 *      and the rule's; its land is Game's tallies and the land factors'; the vault's holding of its money and what the city owes in it (CD8) are the
 *      getters'; the city's GDP in real 2015 US$ stands beside its output.
 *   5. THE VAULT IN EIGHT: the worths add to getReservesUsd() to the bit and the shares to 1; a vault caused to hold two moneys shows both; the month's
 *      move is the history's, and a loaded city shows it; the policy rates and what is owed are the getters'.
 *   6. A PUBLISHED CURVE NEVER CHANGES: a curve read before a solve is the same after it, the market publishes a new one, and a thread that reads while
 *      the model solves sees only whole curves.
 *   7. AN EARTH CITY (where the game's Earth is here): Halifax's distances are from its own spot, and its land factors, marks and prices are the land
 *      sellers' - the branch a generated world's city (no land sellers, a stand-in's node) leaves out.
 *   8. A GOOD'S BOARD (WN6, the screens' second slice): the volumes it is asked at are the tally's month (the snapshot's, which is the live record's),
 *      its floor and ceiling the curve's at them, its conversion into the city's money the market's own rate, the goods it offers exactly those with
 *      a curve (the sold ones, by the books, largest first), the parts of a sale or a purchase the curve's allocation, adding to 1, and what moves it the
 *      curve's three differences; and Game's year of real GDP is the page's.
 */
public class PartnersCheck {

    static int fails = 0;
    static final PrintStream out = System.out;
    static final PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());

    /** The months the city plays before the sections look: past a January, so a year has finished and the history holds it. */
    static final int MONTHS = 14;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-130s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** A figure the arithmetic gives, judged to a tolerance (relative to the larger, with an absolute floor of the tolerance). */
    static void close(String label, double got, double want, double tol) {
        assertTrue(String.format("%s: %.12g against %.12g", label, got, want), Math.abs(got - want) <= tol * Math.max(1, Math.max(Math.abs(got), Math.abs(want))));
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

    /** Two figures the same to the bit (NaN is NaN). */
    static boolean same(double a, double b) { return Double.compare(a, b) == 0; }

    public static void main(String[] args) {
        long start = System.nanoTime();
        Path file = WorldData.locate();
        if (file == null) {
            out.println("  world-1.bin not named (-D" + WorldData.PROPERTY + "=<path>) nor at " + WorldData.DATA_FILE + ": no section ran");
            out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
            System.exit(fails == 0 ? 0 : 1);
        }
        Game city = RealDollarCheck.town("partners-city", Founding.defaults());
        for (int m = 1; m <= MONTHS; m++) play(city);
        int widened = widenTheTrade(city);
        out.println("   the city's month is widened: " + widened + " goods sold and bought at a share of the world's use, split by the market's own allocation");
        theCurveIsTheBand(city);
        Game back = aLoadedCityHasItsCurves(city);
        theTableFoots(city);
        aNationsPage(city, file);
        theVaultInEight(city);
        aPublishedCurveNeverChanges(city, back);
        anEarthCity();
        aGoodsBoard(city);
        out.printf("%nPartnersCheck took %.1f s%n", (System.nanoTime() - start) / 1e9);
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ----------------------------------------------------------------- 1 */

    /** The volumes a good is asked at: last month's, none, one side alone, and shares of the world's use up to all of it (game units a month). */
    static double[][] volumes(double lastOut, double lastIn, double use) {
        return new double[][] { { lastOut, lastIn }, { 0, 0 }, { lastOut, 0 }, { 0, lastIn }, { 1e-5 * use, 1e-5 * use }, { 1e-4 * use, 0 }, { 1e-3 * use, 1e-4 * use },
                { 3e-3 * use, 0 }, { 1e-2 * use, 1e-3 * use }, { 3e-2 * use, 3e-3 * use }, { .1 * use, .05 * use }, { .3 * use, .2 * use }, { use, use } };
    }

    /**
     * Gives the city's tally of the month a trade with many nations, the condition the board needs: a town of five hundred houses sells and buys at the
     * nation next door and nowhere else (the first sink takes it all), so a few goods are sold and bought at half the world's use, split by the market's own
     * allocation at those volumes (PartnerTrade.tally, the call the month makes) into the month it has tallied. The tally is the town's, so the band after it
     * is struck from these volumes too, which only this harness's town sees. Returns how many goods were given trade.
     */
    static int widenTheTrade(Game city) {
        WorldMarket market = city.getWorldMarket();
        PartnerTrade trade = city.partnerTradeForTest();
        int month = city.getPartnerTrade().month(), goods = 0;
        for (Good g : Good.values()) {
            WorldMarket.Curve c = market.curve(g);
            if (c == null) continue;
            boolean sells = g.exportable() && c.sinks().size() >= 10, buys = g.importable() && c.sellers().size() >= 10;
            if (!sells && !buys) continue;
            // the nation next door can use a third of the world's whole use; the rest join past a twentieth, so half of it is sold and half of it bought
            double use = c.worldUseUnitsMonth(), x = sells ? .5 * use : 0, m = buys ? .5 * use : 0;
            double[] band = market.band(g, 0, 0);
            trade.tally(month, g, market.allocate(g, x, m), x, sells ? x * band[0] : 0, m, buys ? m * band[1] : 0, c.unitsPerTonne());
            if (++goods == 8) break;
        }
        trade.close(month);
        return goods;
    }

    static void theCurveIsTheBand(Game city) {
        out.println("--- 1. the curve is the band: floors and ceilings and allocations to the bit, the joins in order ---");
        WorldMarket market = city.getWorldMarket();
        PartnerTrade trade = city.partnerTradeForTest();
        assertTrue("fixture: a city of " + MONTHS + " months has a world market, solved, and a record of its trade by partner", market != null && trade != null && market.solvedYear() > 0);
        if (market == null || trade == null) return;
        Tally floors = new Tally(), ceilings = new Tally(), buyers = new Tally(), sellers = new Tally(), taking = new Tally(), joins = new Tally(), next = new Tally(),
                net = new Tally(), world = new Tally(), grid = new Tally(), summed = new Tally(), bands = new Tally();
        int goods = 0, tradedGoods = 0, several = 0, withGrid = 0, gridPoints = 0, noCurve = 0;
        for (Good g : Good.values()) {
            WorldMarket.Curve c = market.curve(g);
            if (c == null) {
                noCurve++;
                double[] band = market.band(g, 1, 1);
                bands.see(Double.isNaN(band[0]) && Double.isNaN(band[1]), () -> g + " has no curve and a band of " + Arrays.toString(band));
                continue;
            }
            goods++;
            double lastOut = trade.monthExportUnits(g), lastIn = trade.monthImportUnits(g), use = c.worldUseUnitsMonth();
            if (lastOut > 0 || lastIn > 0) tradedGoods++;
            double[][] asked = volumes(lastOut, lastIn, use);
            for (double[] v : asked) {
                double x = v[0], m = v[1];
                double[] band = market.band(g, x, m);
                floors.see(same(c.floorAt(x, m), band[0]), () -> g + " floor at " + x + "/" + m + ": " + c.floorAt(x, m) + " against the band's " + band[0]);
                ceilings.see(same(c.ceilingAt(x, m), band[1]), () -> g + " ceiling at " + x + "/" + m + ": " + c.ceilingAt(x, m) + " against the band's " + band[1]);
                WorldMarket.Allocation a = market.allocate(g, x, m);
                buyers.see(Arrays.equals(c.buyersAt(x), a.exportUnitsTo()), () -> g + " buyers at " + x);
                sellers.see(Arrays.equals(c.sellersAt(m), a.importUnitsFrom()), () -> g + " sellers at " + m);
                // who takes at this volume
                WorldMarket.Curve.Level level = c.levelAt(x);
                int takes = c.buyersTaking(x);
                if (g.exportable() && !c.sinks().isEmpty()) {
                    boolean ok = takes == level.taking() && takes <= c.sinks().size();
                    for (int k = 0; k < c.sinks().size() && ok; k++) {
                        double netback = c.sinks().get(k).netback();
                        double slack = 1e-12 * Math.max(1, Math.abs(netback));
                        // a sink taking nets at least the level; one not taking is not above it (at 0 nobody is taking and the level is the first's)
                        ok = k < takes ? netback - level.level() >= -slack : netback <= level.level() + slack;
                    }
                    int tk = takes;
                    taking.see(ok, () -> g + " at " + x + ": " + tk + " taking at level " + level.level());
                    if (takes >= 2) several++;
                    if (x > 0 && !(level.level() == Double.NEGATIVE_INFINITY)) {
                        double sum = 0;
                        for (double u : c.buyersAt(x)) sum += u;
                        double total = sum;
                        summed.see(Math.abs(total - x) <= 1e-9 * x, () -> g + " buyers add to " + total + " for " + x);
                    }
                    // the buyer that sets the floor is a sink of the curve, the last one taking
                    if (takes > 0) {
                        WorldMarket.Curve.Sink last = c.sinks().get(takes - 1);
                        taking.see(level.nation() == last.buyer() && level.supplier() == last.supplier(), () -> g + " the level's nation " + level.nation() + " against the last sink's " + last.buyer());
                    }
                    // the next buyers all join beyond this volume and none is taking
                    List<WorldMarket.Curve.Sink> upcoming = c.nextBuyers(x, 3);
                    boolean after = upcoming.size() == Math.min(3, c.sinks().size() - takes);
                    double[] by = c.buyersAt(x);
                    for (WorldMarket.Curve.Sink s : upcoming) after &= s.joinsAtUnits() >= x * (1 - 1e-9) && by[s.buyer() - 1] == 0;
                    boolean fine = after;
                    next.see(fine, () -> g + " the next buyers after " + x + ": " + upcoming);
                    // the month's line never rises with the volume (the volumes asked of are not in order: take the sale side's own, ascending)
                }
                if (g.importable() && !c.sellers().isEmpty()) {
                    WorldMarket.Curve.Level cost = c.costLevelAt(m);
                    boolean ok = cost.taking() <= c.sellers().size();
                    for (int k = 0; k < c.sellers().size() && ok; k++) {
                        double landed = c.sellers().get(k).landed();
                        double slack = 1e-12 * Math.max(1, Math.abs(landed));
                        ok = k < cost.taking() ? landed <= cost.level() + slack : landed >= cost.level() - slack;
                    }
                    taking.see(ok, () -> g + " the sellers at " + m + ": " + cost.taking() + " taking at cost " + cost.level());
                }
            }
            // the joins ascend, both sides
            boolean ascend = true;
            for (int k = 1; k < c.sinks().size(); k++) ascend &= c.sinks().get(k).joinsAtUnits() >= c.sinks().get(k - 1).joinsAtUnits() && c.sinks().get(k).netback() <= c.sinks().get(k - 1).netback();
            for (int k = 1; k < c.sellers().size(); k++) ascend &= c.sellers().get(k).joinsAtUnits() >= c.sellers().get(k - 1).joinsAtUnits() && c.sellers().get(k).landed() >= c.sellers().get(k - 1).landed();
            joins.see(ascend, () -> g + " the joins do not ascend");
            // the month's line never rises with the volume, up the grid
            double prevNet = Double.NaN;
            boolean falls = true;
            for (int k = 0; k < 24; k++) {
                double nowNet = c.netAt(use * 1e-6 * Math.pow(10, 6.5 * k / 23));
                if (Double.isNaN(nowNet)) continue;
                if (!Double.isNaN(prevNet) && nowNet > prevNet) falls = false;
                prevNet = nowNet;
            }
            boolean fallsNow = falls;
            net.see(fallsNow, () -> g + " the month's line rises with the volume");
            // the grid: ascending shares in range, the lines are the curve's own and the world's is never above the month's
            List<WorldMarket.Curve.Point> points = c.points();
            if (!points.isEmpty()) {
                withGrid++;
                gridPoints += points.size();
                boolean fine = points.size() <= WorldMarket.Curve.POINTS && points.get(points.size() - 1).share() <= WorldMarket.Curve.POINTS_TO_SHARE * (1 + 1e-12)
                        && points.get(points.size() - 1).units() <= c.saleCapacityUnits() * (1 + 1e-9);
                double before = 0;
                for (WorldMarket.Curve.Point p : points) {
                    fine &= p.share() > before && same(p.net(), c.netAt(p.units())) && same(p.world(), c.floorAt(p.units(), 0)) && Double.isFinite(p.net())
                            && Math.abs(c.shareOfWorldUse(p.units()) - p.share()) <= 1e-9 * p.share();
                    world.see(p.world() <= Math.max(0, p.net()) * (1 + 1e-12), () -> g + " the world's line " + p.world() + " is above the month's " + p.net() + " at " + p.share());
                    before = p.share();
                }
                boolean gridFine = fine;
                grid.see(gridFine, () -> g + " the grid " + points.get(0) + " ... " + points.get(points.size() - 1));
            }
        }
        out.printf("   %d goods have a curve (%d traded last month, %d have none), %d grids of %d points in all, %d volumes with several buyers taking%n",
                goods, tradedGoods, noCurve, withGrid, gridPoints, several);
        assertTrue("goods were tested: " + goods + " have a curve, " + tradedGoods + " of them traded last month", goods > 0 && tradedGoods > 0);
        tally("a good with no curve has a band of nothing", bands);
        tally("floorAt equals band()'s floor to the bit, at last month's volumes and at shares of the world's use up to all of it", floors);
        tally("ceilingAt equals band()'s ceiling to the bit, likewise", ceilings);
        tally("buyersAt equals allocate()'s exports by nation to the bit", buyers);
        tally("sellersAt equals allocate()'s imports by nation to the bit", sellers);
        tally("the buyers taking are the sinks above the level, each netting at least it, and the sellers likewise; the level's nation is the last one taking", taking);
        assertTrue("...with several buyers taking at some volume, so the order is tested", several > 0);
        tally("the buyers added up are the volume sold (past the sinks' caps they are scaled up to it)", summed);
        tally("the joins ascend as the sinks' netbacks fall and the sellers' landed costs rise", joins);
        tally("the next buyers all join beyond the volume, none is taking, and there are as many as are left up to three", next);
        tally("the month's line never rises with the volume", net);
        tally("the grid is ascending shares up to the level's end, its lines the curve's own at the same volume", grid);
        assertTrue("grids were drawn: " + withGrid, withGrid > 0);
        tally("the world's line is never above the month's (nothing below nothing)", world);
        // the curve is the market's: none before a year is solved, none for a good it does not model
        WorldMarket fresh = new WorldMarket(market.base(), city.getNations());
        boolean none = true;
        for (Good g : Good.values()) none &= fresh.curve(g) == null;
        assertTrue("a market that has solved no year has no curve for any good", none);
        int unmodelled = 0;
        boolean noneForThem = true;
        for (Good g : Good.values()) {
            if (market.indexOf(g) >= 0) continue;
            unmodelled++;
            noneForThem &= market.curve(g) == null;
        }
        assertTrue("...and a good the market does not model has none (" + unmodelled + " such goods)", unmodelled > 0 && noneForThem);
    }

    /* ----------------------------------------------------------------- 2 */

    static Game aLoadedCityHasItsCurves(Game city) {
        out.println("--- 2. a loaded city has its curves before its first month, the original's to the bit ---");
        WorldMarket market = city.getWorldMarket();
        PartnerTrade trade = city.partnerTradeForTest();
        assertTrue("saved to its scratch folder", city.saveGame(2, "partners").ok);
        Game back = new Game(city.getGameFiles());
        quietly(() -> back.loadGameSave(2));
        WorldMarket loaded = back.getWorldMarket();
        assertTrue("the loaded city has its market, solved in the year it was saved in, and no month played since",
                back.getLoadFailure() == null && loaded != null && loaded.solvedYear() == market.solvedYear() && back.getMonth() == city.getMonth());
        if (loaded == null) return back;
        Tally curves = new Tally(), floors = new Tally(), sides = new Tally(), grids = new Tally();
        int made = 0;
        for (Good g : Good.values()) {
            WorldMarket.Curve a = market.curve(g), b = loaded.curve(g);
            curves.see((a == null) == (b == null), () -> g + " has a curve in one and not the other");
            if (a == null || b == null) continue;
            made++;
            sides.see(a.sinks().equals(b.sinks()) && a.sellers().equals(b.sellers()) && same(a.worldUseTonnesYear(), b.worldUseTonnesYear()) && same(a.worldSlope(), b.worldSlope()),
                    () -> g + " sinks or sellers differ");
            grids.see(a.points().equals(b.points()), () -> g + " grid differs");
            double use = a.worldUseUnitsMonth();
            for (double[] v : volumes(trade.monthExportUnits(g), trade.monthImportUnits(g), use)) {
                double[] was = market.band(g, v[0], v[1]), now = loaded.band(g, v[0], v[1]);
                floors.see(same(a.floorAt(v[0], v[1]), b.floorAt(v[0], v[1])) && same(a.ceilingAt(v[0], v[1]), b.ceilingAt(v[0], v[1])) && same(b.floorAt(v[0], v[1]), now[0])
                        && same(b.ceilingAt(v[0], v[1]), now[1]) && same(was[0], now[0]) && same(was[1], now[1]), () -> g + " at " + Arrays.toString(v));
            }
        }
        assertTrue("curves were compared: " + made, made > 0);
        tally("every good has a curve in the loaded city exactly where it has one in the original", curves);
        tally("...with the same sinks and sellers, world use and slope, to the bit", sides);
        tally("...the same grid", grids);
        tally("...and the same floor and ceiling at every volume asked, which are the loaded band's", floors);
        return back;
    }

    /* ----------------------------------------------------------------- 3 */

    static void theTableFoots(Game city) {
        out.println("--- 3. the partners table foots, both spans ---");
        Nations nations = city.getNations();
        WorldMarket market = city.getWorldMarket();
        PartnerTrade.Snapshot snap = city.getPartnerTrade();
        PolicyRates policy = city.getPolicyRates();
        ForeignAccounts fx = city.getForeignAccounts();
        int year = Nations.yearOf(city.getMonth());
        assertTrue("fixture: the city has traded this month (" + String.format("%,.1f", snap.monthTotal()) + " thousand with the nations) and this year", snap.monthTotal() > 0 && !snap.yearCells().isEmpty());
        // the city's spot: the Earth's where it has one, else the stand-in nation's node the generated world's ground seed draws (the snapshot keeps it)
        double lat = city.getFounding().getEarthLat(), lon = city.getFounding().getEarthLon();
        if (!(Double.isFinite(lat) && Double.isFinite(lon))) {
            lat = snap.standInLat();
            lon = snap.standInLon();
        }
        assertTrue("fixture: the city stands somewhere (" + lat + ", " + lon + "), the stand-in's node on a generated world, so distances are from its own spot", Double.isFinite(lat) && Double.isFinite(lon));
        final double siteLat = lat, siteLon = lon;
        Game empty = new Game(GameFiles.scratch("partners-no-market"), Founding.defaults());
        assertTrue("a city with no market built has no table and no page", empty.getPartnerTrade() == null && PartnersView.table(empty, PartnersView.Span.MONTH) == null
                && PartnersView.nation(empty, 1) == null);
        PartnersView.Table month = PartnersView.table(city, PartnersView.Span.MONTH), yearly = PartnersView.table(city, PartnersView.Span.YEAR);
        for (PartnersView.Table table : List.of(month, yearly)) {
            boolean isMonth = table.span() == PartnersView.Span.MONTH;
            String span = isMonth ? "month" : "year";
            double[] sold = isMonth ? snap.exportValue() : snap.yearExportValue(), bought = isMonth ? snap.importValue() : snap.yearImportValue();
            double[] tonnesOut = isMonth ? snap.exportTonnes() : snap.yearExportTonnes(), tonnesIn = isMonth ? snap.importTonnes() : snap.yearImportTonnes();
            assertTrue(span + ": the table is of the snapshot's month " + snap.month() + " and year " + snap.year() + ", with " + table.rows().size() + " nations",
                    table.month() == snap.month() && table.year() == snap.year() && !table.rows().isEmpty());
            double rowSold = 0, rowBought = 0, rowOut = 0, rowIn = 0, wantSold = 0, wantBought = 0, wantOut = 0, wantIn = 0;
            int soldTo = 0, boughtFrom = 0, soldWant = 0, boughtWant = 0;
            for (PartnersView.Row r : table.rows()) {
                rowSold += r.sold();
                rowBought += r.bought();
                rowOut += r.tonnesOut();
                rowIn += r.tonnesIn();
                if (r.sold() > 0) soldTo++;
                if (r.bought() > 0) boughtFrom++;
            }
            for (int i = 0; i < sold.length; i++) {
                wantSold += sold[i];
                wantBought += bought[i];
                wantOut += tonnesOut[i];
                wantIn += tonnesIn[i];
                if (sold[i] > 0) soldWant++;
                if (bought[i] > 0) boughtWant++;
            }
            close(span + ": the rows' sold add to the snapshot's, thousands", rowSold, wantSold, 1e-9);
            close(span + ": ...and bought", rowBought, wantBought, 1e-9);
            close(span + ": ...and the tonnes out", rowOut, wantOut, 1e-9);
            close(span + ": ...and the tonnes in", rowIn, wantIn, 1e-9);
            PartnersView.Totals totals = table.totals();
            assertTrue(span + ": the totals count the nations sold to (" + soldTo + ") and bought of (" + boughtFrom + "), the snapshot's own counts",
                    totals.nationsSoldTo() == soldTo && totals.nationsBoughtFrom() == boughtFrom && soldTo == soldWant && boughtFrom == boughtWant);
            close(span + ": ...and sum the rows", totals.sold() + totals.bought(), rowSold + rowBought, 1e-12);
            if (isMonth) {
                assertTrue("month: the no-partner remainders are the snapshot's (" + String.format("%,.2f", snap.noPartnerExports()) + " out, " + String.format("%,.2f", snap.noPartnerImports()) + " in)",
                        same(totals.noPartnerSold(), snap.noPartnerExports()) && same(totals.noPartnerBought(), snap.noPartnerImports()));
                close("month: the rows plus the remainders are the month's trade with the nations plus what no partner took", rowSold + rowBought + totals.noPartnerSold() + totals.noPartnerBought(),
                        snap.monthTotal() + snap.noPartnerExports() + snap.noPartnerImports(), 1e-9);
            } else {
                assertTrue("year: the tally keeps no remainder for the year, and the totals say so (NaN)", Double.isNaN(totals.noPartnerSold()) && Double.isNaN(totals.noPartnerBought()));
            }
            // the order, the ranks, the money, the rates, the distance
            Tally order = new Tally(), money = new Tally(), policyRows = new Tally(), perUnit = new Tally(), distance = new Tally(), identity = new Tally();
            int inEight = 0, outsideEight = 0, withRank = 0;
            PartnersView.Row before = null;
            for (PartnersView.Row r : table.rows()) {
                withRank++;
                order.see(r.rank() == withRank && (before == null || before.total() > r.total() || before.total() == r.total() && before.nation() < r.nation()), () -> span + " row " + r.rank() + " " + r.name());
                Nations.Year y = nations.year(r.nation(), year);
                int k = ForeignAccounts.moneyIndex(y.money());
                money.see(r.money().equals(y.money()) && r.mark().equals(k >= 0 ? ForeignAccounts.MONEY_MARK[k] : y.money()), () -> r.name() + " money " + r.money() + " mark " + r.mark() + " against " + y.money());
                double want = policy == null || k < 0 ? Double.NaN : policy.rate(y.money());
                policyRows.see(same(r.policyRate(), want) && Double.isFinite(r.policyRate()) == (k >= 0 && Double.isFinite(want)), () -> r.name() + " policy rate " + r.policyRate() + " for " + y.money());
                if (k >= 0) inEight++; else outsideEight++;
                perUnit.see(r.unitsPerUsd() > 0 && Math.abs(r.dollarsPerUnit() * r.unitsPerUsd() - fx.getRate()) <= 1e-12 * fx.getRate(),
                        () -> r.name() + ": " + r.dollarsPerUnit() + " x " + r.unitsPerUsd() + " against " + fx.getRate());
                double km = EarthProj.distanceM(siteLat, siteLon, market.nodeLat(r.nation()), market.nodeLon(r.nation())) / 1000;
                distance.see(same(r.distanceKm(), km) && r.distanceKm() >= 0 && r.distanceKm() <= 20_040, () -> r.name() + " is " + r.distanceKm() + " km, not " + km);
                identity.see(r.name().equals(nations.name(r.nation())) && r.iso3().equals(nations.iso3(r.nation())) && r.landKm2() == city.getLandBoughtKm2(r.nation())
                        && r.landUsd() == city.getLandBoughtUsd(r.nation()) * 1000, () -> r.name() + " identity or land");
                before = r;
            }
            out.printf("   %s: %d nations, %d on one of the eight moneys and %d on another, the first %s (%,.1f thousand)%n", span, table.rows().size(), inEight, outsideEight,
                    table.rows().get(0).name(), table.rows().get(0).total());
            tally(span + ": the rows run largest first by what was sold and bought together, ties by nation number, ranks 1 to n", order);
            tally(span + ": each row's money is its nation's this year and its mark the eight's mark or its code", money);
            tally(span + ": a policy rate exactly where the money is one of the eight (and the bank has one), the policy rates' own", policyRows);
            assertTrue(span + ": the rows were both on the eight and on other moneys, so the rate is tested on both sides (" + inEight + " and " + outsideEight + ")", inEight > 0 && outsideEight > 0);
            tally(span + ": D$ a unit times units a US$ is D$ a US$", perUnit);
            tally(span + ": its distance is the great circle from the city to the nation's node, in km", distance);
            tally(span + ": its name, code and land are the nations' and Game's tallies", identity);
        }
        Tally grows = new Tally();
        for (PartnersView.Row m : month.rows()) {
            for (PartnersView.Row y : yearly.rows()) {
                if (y.nation() == m.nation()) grows.see(y.sold() >= m.sold() * (1 - 1e-9) && y.bought() >= m.bought() * (1 - 1e-9), () -> m.name() + " the year to date is less than the month");
            }
        }
        tally("the year to date is at least the month, nation by nation", grows);
    }

    /* ----------------------------------------------------------------- 4 */

    static void aNationsPage(Game city, Path file) {
        out.println("--- 4. a nation's page: its goods foot, its years are the history's, its ranks the card's, its land Game's ---");
        Nations nations = city.getNations();
        ForeignAccounts fx = city.getForeignAccounts();
        PartnerTrade.Snapshot snap = city.getPartnerTrade();
        PartnersView.Table table = PartnersView.table(city, PartnersView.Span.YEAR);
        int year = Nations.yearOf(city.getMonth());
        // THE LAND, caused: the city has bought from the biggest partner (the tallies, set by hand as WN5's harness does)
        int top = table.rows().get(0).nation();
        int sellersCount = city.landSellerCount();
        assertTrue("fixture: the world file has " + sellersCount + " nations to buy land from", sellersCount == nations.count());
        double[] km2 = new double[sellersCount + 1], usd = new double[sellersCount + 1];
        km2[top] = 12.5;
        usd[top] = 3_400;
        city.setLandBoughtForTest(km2, usd);
        PartnersView.Page page = PartnersView.nation(city, top);
        assertTrue("the page of the biggest partner, " + page.name() + " (" + page.iso3() + "), is of this year " + year + " among " + page.count() + " nations",
                page.nation() == top && page.year() == year && page.count() == nations.count() && page.name().equals(nations.name(top)));
        // its goods foot to the year's totals
        double goodsOut = 0, goodsIn = 0;
        boolean ordered = true;
        double previous = Double.POSITIVE_INFINITY;
        for (PartnerTrade.Cell cell : page.goods()) {
            goodsOut += cell.valueOut();
            goodsIn += cell.valueIn();
            ordered &= cell.nation() == top && cell.valueOut() + cell.valueIn() <= previous;
            previous = cell.valueOut() + cell.valueIn();
        }
        close("its goods' value out adds to the year's sold to it, thousands", goodsOut, snap.yearExportValue()[top - 1], 1e-9);
        close("...and value in to the year's bought of it", goodsIn, snap.yearImportValue()[top - 1], 1e-9);
        assertTrue("...with " + page.goods().size() + " goods, all of this nation, largest first", !page.goods().isEmpty() && ordered);
        assertTrue("its month and year figures are the snapshot's", same(page.monthSold(), snap.exportValue()[top - 1]) && same(page.monthBought(), snap.importValue()[top - 1])
                && same(page.yearSold(), snap.yearExportValue()[top - 1]) && same(page.yearBought(), snap.yearImportValue()[top - 1])
                && same(page.yearTonnesOut(), snap.yearExportTonnes()[top - 1]) && same(page.yearTonnesIn(), snap.yearImportTonnes()[top - 1]));
        // its years are the history's, from the saved list
        List<PartnerTrade.Table> history = city.getHistorySave().getTradeByPartner();
        assertTrue("fixture: the history holds " + history.size() + " finished year(s)", !history.isEmpty());
        boolean yearsFine = page.years().size() == history.size();
        for (int i = 0; i < history.size() && yearsFine; i++) {
            double out = 0, in = 0;
            for (PartnerTrade.Cell cell : history.get(i).cells()) {
                if (cell.nation() != top) continue;
                out += cell.valueOut();
                in += cell.valueIn();
            }
            yearsFine = page.years().get(i).year() == history.get(i).year() && Math.abs(page.years().get(i).valueOut() - out) <= 1e-9 * Math.max(1, out)
                    && Math.abs(page.years().get(i).valueIn() - in) <= 1e-9 * Math.max(1, in);
        }
        assertTrue("its finished years are the history's, oldest first (" + page.years() + ")", yearsFine);
        // its ranks are the rule's: the nations strictly larger, plus one
        int largerOutput = 0, largerPeople = 0, largerHead = 0;
        Nations.Year y = nations.year(top, year);
        for (int n = 1; n <= nations.count(); n++) {
            Nations.Year o = nations.year(n, year);
            if (o.output() > y.output()) largerOutput++;
            if (o.people() > y.people()) largerPeople++;
            if (o.outputPerHead() > y.outputPerHead()) largerHead++;
        }
        assertTrue("its ranks (" + page.rankOutput() + ", " + page.rankPeople() + ", " + page.rankHead() + ") are one more than the nations with more output, people and output a head",
                page.rankOutput() == largerOutput + 1 && page.rankPeople() == largerPeople + 1 && page.rankHead() == largerHead + 1);
        assertTrue("its figures are the nations' for the year", same(page.output(), y.output()) && same(page.people(), y.people()) && same(page.outputPerHead(), y.outputPerHead())
                && page.outputMark() == y.outputMark() && page.peopleMark() == y.peopleMark() && same(page.inflation(), y.inflation())
                && same(page.growth(), Math.expm1(nations.growth(top, year))) && page.money().equals(y.money()));
        // ...and the map card's: a NationsMap built on the file writes the same rows with the same ranks
        try (WorldData data = WorldData.open(file)) {
            NationsMap map = NationsMap.of(data);
            double monthRate = city.getWorldMoneys() == null ? Double.NaN : city.getWorldMoneys().rateOfNation(top);
            NationsMap.Card card = map.card(nations, top, year, city.getFounding().getEarthLat(), city.getFounding().getEarthLon(), fx.getRate(), null, monthRate, city.getPolicyRates());
            assertTrue("the page's rows are the map card's rows, word for word, ranks and all (" + page.figureRows().size() + " rows: " + page.figureRows().get(0) + ")",
                    card.rows().equals(page.figureRows()) && !page.figureRows().get(0).rank().isEmpty());
            assertTrue("...so the page's ranks are the card's: " + card.rows().get(0).rank() + ", " + card.rows().get(1).rank() + ", " + card.rows().get(2).rank(),
                    card.rows().get(0).rank().equals(NationsMap.ordinal(page.rankOutput()) + " of " + nations.count())
                            && card.rows().get(1).rank().equals(NationsMap.ordinal(page.rankPeople()) + " of " + nations.count())
                            && card.rows().get(2).rank().equals(NationsMap.ordinal(page.rankHead()) + " of " + nations.count()));
            boolean everyone = true;
            for (int n = 1; n <= nations.count() && everyone; n += 7) {
                PartnersView.Page other = PartnersView.nation(city, n);
                NationsMap.Card c = map.card(nations, n, year, city.getFounding().getEarthLat(), city.getFounding().getEarthLon(), fx.getRate(), null,
                        city.getWorldMoneys() == null ? Double.NaN : city.getWorldMoneys().rateOfNation(n), city.getPolicyRates());
                everyone = c.rows().equals(other.figureRows());
            }
            assertTrue("...for every seventh nation of the " + nations.count() + " too", everyone);
        } catch (java.io.IOException e) {
            assertTrue("the world file would not open for the map: " + e, false);
        }
        // its land is Game's tallies and the land factors'
        assertTrue("its land is what Game tallied: 12.5 km2 for US$3.4M", page.landKm2() == 12.5 && page.landUsd() == 3_400_000 && page.landKm2() == city.getLandBoughtKm2(top)
                && page.landUsd() == city.getLandBoughtUsd(top) * 1000);
        LandSellers sellers = city.getLandSellers();
        if (sellers != null) {
            LandFactors factors = sellers.factors();
            assertTrue("its land factor now (" + page.landFactor() + "), mark (" + page.landMark() + ") and a hectare's price are the land factors'",
                    same(page.landFactor(), factors.factorAt(top, city.getMonth())) && page.landMark() == factors.mark(top) && same(page.landUsdPerHectare(), factors.usdPerHectare(top)));
        } else {
            assertTrue("the city has no land sellers, and the page says so (NaN, no mark)", Double.isNaN(page.landFactor()) && page.landMark() == WorldData.MARK_NONE);
        }
        PartnersView.Row row = PartnersView.table(city, PartnersView.Span.YEAR).rows().get(0);
        assertTrue("...and the table's row of it says the same", row.nation() == top && row.landKm2() == 12.5 && row.landUsd() == 3_400_000 && same(row.landFactor(), page.landFactor()));
        // the city's GDP beside its output
        double gdp = city.realGdp2015Usd() * 12 * 1000;
        assertTrue("the city's GDP in real 2015 US$ a year (the last month's, times 12 and the thousands) is " + String.format("%,.0f", page.cityGdpUsd()) + " beside its output "
                + String.format("%,.0f", page.output()) + ": the share " + String.format("%.3g", page.gdpShare()),
                same(page.cityGdpUsd(), gdp) && same(page.gdpShare(), gdp / y.output()) && gdp > 0);
        // the vault's holding of its money and what the city owes in it (CD8), caused
        int germany = nations.index("DEU");
        Nations.Year g = nations.year(germany, year);
        assertTrue("fixture: Germany is on the euro, the vault's second money", "EUR".equals(g.money()) && ForeignAccounts.moneyIndex(g.money()) == 1);
        double spend = Math.min(60_000, city.getCash() / 10);
        city.buyForeignCurrency(spend, "EUR");
        String said = DebtMoneysCheck.sell(city, "Term", "EUR", DebtMoneysCheck.askIn(city, "EUR"), 10, false);
        DebtManager dm = city.getDebtManager();
        assertTrue("fixture: the city holds euros (" + String.format("%,.2f", fx.getHolding(1)) + ") and owes some (" + String.format("%,.2f", dm.getForeignPrincipalIn("EUR")) + " euros; it said \"" + said + "\")",
                fx.getHolding(1) > 0 && dm.getForeignPrincipalIn("EUR") > 0);
        PartnersView.Page de = PartnersView.nation(city, germany);
        assertTrue("Germany's page holds the vault's euros, in euros and in US$, as the vault has them", de.vaultIndex() == 1 && same(de.vaultHeld(), fx.getHolding(1)) && same(de.vaultHeldUsd(), fx.getHoldingUsd(1)));
        assertTrue("...and what the city owes in euros (CD8), in euros and in US$ at the vault's rate", same(de.owedOwn(), dm.getForeignPrincipalIn("EUR"))
                && same(de.owedUsd(), dm.getForeignPrincipalIn("EUR") * fx.getMoneyUsd(1)));
        int other = nations.index("BRA");
        PartnersView.Page br = PartnersView.nation(city, other);
        assertTrue("a nation on a money that is none of the eight (" + br.money() + ") has no place in the vault and nothing owed in it",
                br.vaultIndex() == -1 && Double.isNaN(br.vaultHeld()) && Double.isNaN(br.owedOwn()) && ForeignAccounts.moneyIndex(br.money()) == -1);
        assertTrue("a number that is no nation has no page", PartnersView.nation(city, 0) == null && PartnersView.nation(city, nations.count() + 1) == null);
    }

    /* ----------------------------------------------------------------- 5 */

    static void theVaultInEight(Game city) {
        out.println("--- 5. the vault in eight: the worths add to the vault, the move is the history's ---");
        ForeignAccounts fx = city.getForeignAccounts();
        PolicyRates policy = city.getPolicyRates();
        DebtManager dm = city.getDebtManager();
        VaultView v = VaultView.of(city);
        assertTrue("nine rows (ForeignAccounts.MONEYS): the eight in the eight's order, then gold, named and marked as the vault names them", v.rows().size() == ForeignAccounts.MONEYS);
        boolean named = true;
        for (VaultView.Row r : v.rows())
            named &= r.index() == v.rows().indexOf(r) && r.code().equals(ForeignAccounts.codeOf(r.index())) && r.name().equals(ForeignAccounts.MONEY_NAME[r.index()]) && r.mark().equals(ForeignAccounts.MONEY_MARK[r.index()]);
        assertTrue("...each row's code, name and mark the vault's, and gold's row is the last, XAU", named && v.rows().get(ForeignAccounts.GOLD).code().equals("XAU"));
        assertTrue("the worths add to getReservesUsd() to the bit, and the D$ worth is getReserves()", same(v.worthUsd(), fx.getReservesUsd()) && same(v.worthD(), fx.getReserves()) && v.worthUsd() > 0);
        // a vault of dollars and euros (the euros bought by the last section), and the yen caused now: three moneys
        double spend = Math.min(60_000, city.getCash() / 10);
        city.buyForeignCurrency(spend, "JPY");
        v = VaultView.of(city);
        assertTrue("fixture: the vault holds US$, euros and yen now", fx.getHolding(0) > 0 && fx.getHolding(1) > 0 && fx.getHolding(2) > 0);
        double worth = 0, share = 0, worthD = 0;
        boolean held = true;
        for (VaultView.Row r : v.rows()) {
            worth += r.worthUsd();
            share += r.share();
            worthD += r.worthD();
            held &= same(r.held(), fx.getHolding(r.index())) && same(r.worthUsd(), fx.getHoldingUsd(r.index())) && same(r.worthD(), fx.getHoldingUsd(r.index()) * fx.getRate())
                    && r.rated() == fx.hasRate(r.index()) && same(r.usdPerUnit(), fx.getMoneyUsd(r.index()));
        }
        assertTrue("each row holds what the vault holds of the money, in its own units, in US$ and in D$ at the rate", held);
        close("the worths add to the vault's US$ worth", worth, fx.getReservesUsd(), 1e-12);
        assertTrue("...to the bit, the total being the vault's", same(v.worthUsd(), fx.getReservesUsd()));
        close("the D$ worths add to the vault's D$ worth", worthD, fx.getReserves(), 1e-12);
        close("the shares add to 1", share, 1, 1e-12);
        VaultView.Row usdRow = v.rows().get(0), eur = v.rows().get(1), jpy = v.rows().get(2);
        assertTrue("a vault of three moneys shows all three, their shares as their worths over the vault's (" + String.format("%.3f", usdRow.share()) + ", " + String.format("%.3f", eur.share()) + ", "
                + String.format("%.3f", jpy.share()) + ") and the other five, and gold, nothing",
                usdRow.share() > 0 && eur.share() > 0 && jpy.share() > 0 && Math.abs(eur.share() - eur.worthUsd() / v.worthUsd()) < 1e-12
                        && v.rows().subList(3, ForeignAccounts.MONEYS).stream().allMatch(r -> r.held() == 0 && r.share() == 0 && r.worthUsd() == 0));
        // what is owed, and the policy rates
        boolean owes = true, policies = true;
        for (VaultView.Row r : v.rows()) {
            owes &= same(r.owedOwn(), dm.getForeignPrincipalIn(r.code())) && (r.owedOwn() == 0 ? r.owedUsd() == 0 : same(r.owedUsd(), r.owedOwn() * fx.getMoneyUsd(r.index())));
            policies &= r.index() == ForeignAccounts.GOLD ? Double.isNaN(r.policyRate()) : same(r.policyRate(), policy.rate(r.code())) && Double.isFinite(r.policyRate());
        }
        assertTrue("what the city owes in each is DebtManager's (CD8), in its own units and in US$ (the euro bond: " + String.format("%,.2f", eur.owedOwn()) + " euros)", owes && eur.owedOwn() > 0);
        assertTrue("each row's policy rate is its central bank's, and gold's, which no bank pays, is no number", policies);
        // THE MONTH'S MOVE: a month on, the history's two last figures
        play(city);
        v = VaultView.of(city);
        HistorySave history = city.getHistorySave();
        Tally moves = new Tally(), finite = new Tally();
        for (VaultView.Row r : v.rows()) {
            if (r.code().equals(Nations.DOLLAR)) {
                moves.see(r.move() == 0, () -> "the US$ moved " + r.move());
                continue;
            }
            if (r.index() == ForeignAccounts.GOLD) {
                // gold's move is the price's own, not the history's (which holds no gold series): this month's price over last month's, less one
                int at = city.getWorldMoneys().month();
                double goldWas = city.goldUsdPerOunce(at - 1), goldNow = city.goldUsdPerOunce(at);
                moves.see(same(r.move(), goldNow / goldWas - 1) && same(r.move(), city.getGoldMove()), () -> "gold moved " + r.move() + ", the price's " + (goldNow / goldWas - 1));
                finite.see(Double.isFinite(r.move()), () -> "gold has no move");
                continue;
            }
            double[] series = history.aligned(HistorySave.worldRateKey(r.code()));
            double was = series[series.length - 2], now = series[series.length - 1];
            moves.see(same(r.move(), was / now - 1), () -> r.code() + " moved " + r.move() + ", the history's " + (was / now - 1));
            finite.see(Double.isFinite(r.move()), () -> r.code() + " has no move");
        }
        tally("this month's move is the history's units a US$ of last month over this month's, less one (the US$ itself 0; gold's the price's, this month's over last month's, less one)", moves);
        tally("...and every one of the seven, and gold, has one", finite);
        ForeignAccounts.CrossDriver driver = v.crossDriver();
        assertTrue("the month's cross revaluation (" + String.format("%+,.3f", v.crossUsd()) + " thousand) and its driver (" + (driver == null ? "none" : driver.code()) + ") are the vault's, and its D$ revaluation is too",
                same(v.crossUsd(), fx.getLastVaultCrossUsd()) && driver != null && driver.equals(fx.getLastVaultCrossDriver()) && same(v.revaluationD(), fx.getLastVaultRevaluation()) && v.crossUsd() != 0);
        if (driver != null) {
            double saved = v.rows().get(driver.index()).move();
            assertTrue("the driver's move against the US$ (" + String.format("%+.4f", driver.move()) + ") is the row's (" + String.format("%+.4f", saved) + ") to the history's rounding",
                    Math.abs(driver.move() - saved) < 5e-4);
        }
        // A LOADED CITY SHOWS IT: the history is saved
        assertTrue("saved after the month", city.saveGame(3, "the vault").ok);
        Game back = new Game(city.getGameFiles());
        quietly(() -> back.loadGameSave(3));
        VaultView b = VaultView.of(back);
        boolean same = back.getLoadFailure() == null;
        for (int k = 0; k < ForeignAccounts.MONEYS && same; k++) same = same(b.rows().get(k).move(), v.rows().get(k).move()) && same(b.rows().get(k).held(), v.rows().get(k).held());
        assertTrue("a loaded city shows the same month's move for each money, and holds the same balances", same);
    }

    /* ----------------------------------------------------------------- 6 */

    static void aPublishedCurveNeverChanges(Game city, Game back) {
        out.println("--- 6. a published curve never changes: a curve read before a solve is the same after, and a reader sees only whole curves ---");
        WorldMarket market = back.getWorldMarket();
        Good watched = null;
        for (Good g : Good.values()) {
            WorldMarket.Curve c = market.curve(g);
            if (c != null && g.exportable() && c.sinks().size() >= 3 && c.points().size() >= 8) {
                watched = g;
                break;
            }
        }
        assertTrue("fixture: a good with a curve of several buyers to watch (" + watched + ")", watched != null);
        if (watched == null) return;
        final Good good = watched;
        WorldMarket.Curve first = market.curve(good);
        final double ask = first.worldUseUnitsMonth() * 1e-3;
        double a = first.floorAt(ask, 0);
        double[] then = { a, first.netAt(ask), first.ceilingAt(ask, ask), first.floorAt(30 * ask, ask), first.worldAnswerAt(ask, 0) };
        double[] buyersThen = first.buyersAt(30 * ask), sellersThen = first.sellersAt(30 * ask);
        List<WorldMarket.Curve.Sink> sinksThen = new ArrayList<>(first.sinks());
        int year = market.solvedYear();
        // a thread reads the curve and its floor again and again while the model solves the next years
        Set<Long> seen = ConcurrentHashMap.newKeySet();
        AtomicBoolean stop = new AtomicBoolean();
        AtomicReference<Throwable> failed = new AtomicReference<>();
        AtomicReference<Integer> reads = new AtomicReference<>(0);
        Thread reader = new Thread(() -> {
            try {
                int n = 0;
                while (!stop.get()) {
                    WorldMarket.Curve c = market.curve(good);
                    seen.add(Double.doubleToLongBits(c.floorAt(ask, 0)));
                    n++;
                }
                reads.set(n);
            } catch (Throwable e) {
                failed.set(e);
            }
        }, "partners-reader");
        reader.start();
        List<Double> floors = new ArrayList<>(List.of(a));
        WorldMarket.Curve published = first;
        boolean fresh = true;
        for (int step = 1; step <= 3; step++) {
            quietly(() -> market.solveYear(market.solvedYear() + 1));
            WorldMarket.Curve now = market.curve(good);
            fresh &= now != published;
            published = now;
            floors.add(now.floorAt(ask, 0));
        }
        stop.set(true);
        try {
            reader.join(20_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        assertTrue("the market solved " + (market.solvedYear() - year) + " years on and published a new curve each time", market.solvedYear() == year + 3 && fresh);
        double[] now = { first.floorAt(ask, 0), first.netAt(ask), first.ceilingAt(ask, ask), first.floorAt(30 * ask, ask), first.worldAnswerAt(ask, 0) };
        assertTrue("the curve read before the solves is the same after them: its floor " + a + ", net, ceiling and world's answer at several volumes, the buyers and sellers and the sinks, to the bit",
                Arrays.equals(then, now) && Arrays.equals(buyersThen, first.buyersAt(30 * ask)) && Arrays.equals(sellersThen, first.sellersAt(30 * ask))
                        && first.sinks().equals(sinksThen) && market.curve(good) != first);
        Set<Long> expected = new HashSet<>();
        for (double f : floors) expected.add(Double.doubleToLongBits(f));
        assertTrue("the years' floors differ (" + floors + "), so a torn curve could be told", expected.size() >= 2);
        assertTrue("a thread reading all the while (" + reads.get() + " reads) saw no error, and only floors of the four whole curves (" + seen.size() + " of them)",
                failed.get() == null && !seen.isEmpty() && expected.containsAll(seen));
    }

    /* ----------------------------------------------------------------- 8 */

    static void aGoodsBoard(Game city) {
        out.println("--- 8. a good's board: the volumes are the tally's month, the band the curve's at them, the conversion the market's own, the parts the allocation's ---");
        WorldMarket market = city.getWorldMarket();
        PartnerTrade trade = city.partnerTradeForTest();
        PartnerTrade.Snapshot snap = city.getPartnerTrade();
        Tally units = new Tally(), band = new Tally(), parts = new Tally(), moves = new Tally();
        int boards = 0, withCurve = 0;
        for (Good g : Good.values()) {
            WorldMarket.Curve c = market.curve(g);
            if (c == null) {
                assertTrue("a good with no curve (" + g + ") has no board", PartnersView.good(city, g) == null);
                continue;
            }
            withCurve++;
            PartnersView.GoodBoard b = PartnersView.good(city, g);
            boards++;
            double x = trade.monthExportUnits(g), m = trade.monthImportUnits(g);
            units.see(same(b.exportUnits(), x) && same(b.importUnits(), m) && same(snap.monthExportUnits(g), x) && same(snap.monthImportUnits(g), m),
                    () -> g + ": the board's volumes " + b.exportUnits() + " and " + b.importUnits() + " are not the tally's " + x + " and " + m);
            GoodsMarket gm = city.getMarkets().get(g);
            band.see(same(b.floor(), c.floorAt(x, m)) && same(b.ceiling(), c.ceilingAt(x, m)) && same(b.perUnit(), gm.getExchangeRate())
                            && same(b.inCityMoney(1), gm.getExchangeRate()) && same(b.sellsAt(), gm.exportPrice()) && same(b.buysAt(), gm.importPrice())
                            && b.floorNation() == c.levelAt(x).nation() && b.ceilingNation() == c.costLevelAt(m).nation(),
                    () -> g + ": the board's band is not the curve's at the month's volumes, at the market's rate");
            double own = c.netAt(x) - c.netAt(0);
            PartnersView.Moves mv = b.moves();
            moves.see(same(mv.own(), Double.isFinite(own) ? own : Double.NaN) && same(mv.world(), -c.worldAnswerAt(x, m))
                            && same(mv.freight(), c.sinks().isEmpty() || !g.exportable() ? Double.NaN : -c.sinks().get(0).freight()),
                    () -> g + ": what moves it is not the curve's own selling, world's answer and first sink's freight");
            if (x > 0 && g.exportable()) {
                List<PartnersView.Part> buyers = PartnersView.buyers(c, x);
                double[] to = c.buyersAt(x);
                double share = 0, units2 = 0;
                boolean ordered = true;
                for (int i = 0; i < buyers.size(); i++) {
                    share += buyers.get(i).share();
                    units2 += buyers.get(i).units();
                    ordered &= same(buyers.get(i).units(), to[buyers.get(i).nation() - 1]) && (i == 0 || buyers.get(i - 1).units() >= buyers.get(i).units());
                }
                final double shareSum = share;
                parts.see(!buyers.isEmpty() && ordered && Math.abs(shareSum - 1) < 1e-9 && Math.abs(units2 - x) <= 1e-9 * Math.max(1, x),
                        () -> g + ": the buyers' parts add to " + shareSum + " of what was sold");
            }
            if (m > 0 && g.importable()) {
                List<PartnersView.Part> sellers = PartnersView.sellers(c, m);
                double share = 0;
                for (PartnersView.Part p : sellers) share += p.share();
                final double shareSum = share;
                parts.see(!sellers.isEmpty() && Math.abs(shareSum - 1) < 1e-9, () -> g + ": the sellers' parts add to " + shareSum + " of what was bought");
            }
        }
        out.printf("   %d goods have a curve and a board%n", boards);
        tally("each board's volumes are the tally's month (the snapshot's and the live record's agree)", units);
        tally("...its floor and ceiling the curve's at them, the rate and the prices the market's own, the nations the curve's", band);
        tally("...what moves it the curve's own selling, the world's answer and the first sink's freight", moves);
        tally("...the parts of a sale or a purchase the allocation's, largest first, adding to what was sold or bought", parts);
        PartnersView.Goods goods = PartnersView.goods(city);
        java.util.Map<Good, Sectors.GoodTrade> books = city.getTradeByGood().goods();
        boolean ordered = true, sells = true;
        for (int i = 0; i < goods.sold().size(); i++) {
            Good g = goods.sold().get(i);
            sells &= books.get(g) != null && books.get(g).sold() > 0 && !market.curve(g).sinks().isEmpty();
            ordered &= i == 0 || books.get(goods.sold().get(i - 1)).sold() >= books.get(g).sold();
        }
        assertTrue("the board offers every good with a curve once (" + goods.sold().size() + " sold, " + goods.others().size() + " others of " + withCurve + ")",
                goods.sold().size() + goods.others().size() == withCurve && new HashSet<>(goods.sold()).stream().noneMatch(goods.others()::contains));
        assertTrue("...the sold ones those the books sold abroad, largest first", sells && ordered);
        PartnersView.Page page = PartnersView.nation(city, snap.exportValue().length > 1 ? 2 : 1);
        assertTrue("Game's year of real GDP is the month's times twelve in whole US$, and the nation's page sets that beside its output",
                same(city.realGdp2015UsdYear(), city.realGdp2015Usd() * 12 * 1000) && same(page.cityGdpUsd(), city.realGdp2015UsdYear()));
    }

    /* ----------------------------------------------------------------- 7 */

    static void anEarthCity() {
        out.println("--- 7. an Earth city: distances from its own spot, land factors from the land sellers ---");
        if (!EarthSite.gameData().here()) {
            out.println("  the game's Earth is not here (" + EarthSite.gameData().words() + "): this section did not run");
            return;
        }
        LandNationsCheck.City halifax = new LandNationsCheck.City("partners-halifax", LandNationsCheck.onEarth(LandNationsCheck.HALIFAX_LAT, LandNationsCheck.HALIFAX_LON));
        halifax.months(2);
        Game g = halifax.g;
        widenTheTrade(g);
        LandSellers sellers = g.getLandSellers();
        assertTrue("fixture: the city stands on the real Earth at Halifax and its ground is sold by nations",
                g.getFounding().getEarthLat() == LandNationsCheck.HALIFAX_LAT && g.getFounding().getEarthLon() == LandNationsCheck.HALIFAX_LON && sellers != null && g.getWorldMarket() != null);
        if (sellers == null || g.getWorldMarket() == null) return;
        LandFactors factors = sellers.factors();
        WorldMarket market = g.getWorldMarket();
        PartnersView.Table table = PartnersView.table(g, PartnersView.Span.MONTH);
        Tally distance = new Tally(), land = new Tally();
        for (PartnersView.Row r : table.rows()) {
            double km = EarthProj.distanceM(LandNationsCheck.HALIFAX_LAT, LandNationsCheck.HALIFAX_LON, market.nodeLat(r.nation()), market.nodeLon(r.nation())) / 1000;
            distance.see(same(r.distanceKm(), km), () -> r.name() + " is " + r.distanceKm() + " km from Halifax, not " + km);
            land.see(same(r.landFactor(), factors.factorAt(r.nation(), g.getMonth())) && r.landMark() == factors.mark(r.nation()) && r.landFactor() > 0, () -> r.name() + " land factor " + r.landFactor());
        }
        out.printf("   Halifax trades with %d nations; the first, %s, is %,.0f km away, its land factor %.3f%n", table.rows().size(), table.rows().get(0).name(),
                table.rows().get(0).distanceKm(), table.rows().get(0).landFactor());
        tally("each row's distance is the great circle from Halifax's own spot to the nation's node", distance);
        tally("...and its land factor and mark are the land sellers' for the month", land);
        int host = market.host();
        PartnersView.Page page = PartnersView.nation(g, host);
        assertTrue("the host nation's (" + page.name() + ") page has the land factors' factor, mark and price a hectare (" + String.format("%.2f", page.landUsdPerHectare()) + ")",
                same(page.landFactor(), factors.factorAt(host, g.getMonth())) && page.landMark() == factors.mark(host) && same(page.landUsdPerHectare(), factors.usdPerHectare(host))
                        && page.landUsdPerHectare() > 0);
        assertTrue("...and a distance of its node from the spot that is not nothing and not the world's width (" + String.format("%,.0f", page.distanceKm()) + " km)",
                page.distanceKm() > 0 && page.distanceKm() < 6_000);
    }
}

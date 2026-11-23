package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * What a large city's month reads again, it keeps - and the figure it gives is the one it gave before.
 *
 * AP2 made slot 9's months faster by keeping four things the month read over and over: the tiles a link's search reads
 * (CityMap's RunGround), the stamp of the centre's blocks (CityLand.centreStamp()), the landmass ids near a tile
 * (EarthTerrain.idsNear()) and what each sector owes (BusinessDebtManager.getPrincipalsByKey()). A thing kept is a thing that
 * can go stale, and a speed-up that moves a figure by a bit is a bug no other harness would name: the months would still
 * run, the books would still balance, and a save would quietly not match the one before. So each is read both ways here,
 * kept and drawn afresh, and the two must be the same.
 *
 * WHAT THIS HAS TO PROVE:
 *
 *   1. A LINK'S SEARCH KEEPS EVERY TILE IT READS, AND READS THE SAME GROUND. A city that owns more tiles than the usual keep
 *      (CityMap.RUN_TILES_KEPT) draws every one again on each pass round them; between searching(true) and searching(false)
 *      (CityRuns.Ground) a second pass draws none, every plot reads as it did, and when the search ends the keep goes back
 *      to its usual size and the ground still reads the same.
 *
 *   2. THE CENTRE'S STAMP IS KEPT BETWEEN ITS CHANGES AND MOVES WITH THEM. It is the hash the map's ground asks at every
 *      plot a search reads; kept past a restatement it would tell the map nothing had changed. A restored copy carries
 *      the original's.
 *
 *   3. A TILE'S LANDMASS IDS ARE KEPT, THE SAME AS READ FROM THE CELLS. The same array on a second ask, the same ids as
 *      the cells give, and the same again after the keep has filled and been cleared.
 *
 *   4. WHAT EACH SECTOR OWES, FROM ONE PASS OVER THE BOOK, IS getPrincipal() TO THE BIT. The auction's bidders only
 *      lend or hold on `owed > 0` (OutwardInvestment.mayHold()), so a figure off by a bit would tip a sector from one side
 *      to the other.
 *
 * Every fixture CAUSES its condition: a city of more tiles than the keep, a restatement of the centre, tiles by the
 * thousand against the keep's size, a bank that has lent to two sectors.
 */
public class KeptReadsCheck {

    static int fails = 0;
    static final PrintStream out = System.out;
    static final PrintStream QUIET = new PrintStream(OutputStream.nullOutputStream());

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-112s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-112s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(QUIET);
        try { r.run(); } finally { System.setOut(real); }
    }

    /** The default world's seed: the generated world every fixture but section 3's is drawn on. */
    static final long SEED = Founding.DEFAULT_WORLD_SEED;

    /** Highways and railway lines the section 1 city is laid with, as GatewayCheck's large fixtures (gatewayCity(types, 100, 10)). */
    static final long HIGHWAYS = 100, TRACKS = 10;

    /** How many tiles of the section 1 city are read plot by plot, both ways: a handful, spread over the city. */
    static final int HANDFUL = 4;

    /** The dry ground a section 2 city is founded on, in km2, and what a restatement draws it again to: four times as much, so more blocks. */
    static final double DRY_KM2 = 1.0, MORE_KM2 = 4 * DRY_KM2;

    /** Tiles either side of the founding tile of the window section 3 reads: a window of (2 * 48 + 1) tiles a side. */
    static final int WINDOW = 48;

    /** The months the section 4 city runs before the bank's book is looked at, and the most it may run on for the condition (two sectors in debt). */
    static final int MONTHS_FIRST = 24, MONTHS_MOST = 120;

    /** The months after, one at a time, in each of which the figures are read again: a year, the book changing under them. */
    static final int MONTHS_LOOKED = 12;

    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();
        theLinksTiles();
        theCentresStamp();
        theLandmassIds();
        theBook();
        out.printf("%nran %.1f s%n", (System.nanoTime() - start) / 1e9);
        out.println(fails == 0 ? "All checks passed." : fails + " FAILED");
        System.exit(fails > 0 ? 1 : 0);
    }

    /* =====================================================================
       1. A LINK'S SEARCH KEEPS EVERY TILE IT READS
       ===================================================================== */

    /** One owned plot of every owned tile of the ground's box, in the order the tiles lie (rows from the north-west). */
    static long[][] onePlotATile(CityRuns.Ground g) {
        long[] box = g.box();
        List<long[]> plots = new ArrayList<>();
        for (long ty = Math.floorDiv(box[1], World.TILE); ty <= Math.floorDiv(box[3], World.TILE); ty++) {
            for (long tx = Math.floorDiv(box[0], World.TILE); tx <= Math.floorDiv(box[2], World.TILE); tx++) {
                long[] one = null;
                for (int iy = 0; iy < World.TILE && one == null; iy++) {
                    for (int ix = 0; ix < World.TILE && one == null; ix++) {
                        long x = tx * World.TILE + ix, y = ty * World.TILE + iy;
                        if (g.at(x, y, -1) >= 0) one = new long[] { x, y };
                    }
                }
                if (one != null) plots.add(one);
            }
        }
        return plots.toArray(new long[0][]);
    }

    /** What the ground reads at each plot, in order. */
    static int[] pass(CityRuns.Ground g, long[][] plots) {
        int[] v = new int[plots.length];
        for (int i = 0; i < plots.length; i++) v[i] = g.at(plots[i][0], plots[i][1], -1);
        return v;
    }

    /** What the ground reads at every plot of tile (tx, ty), row by row. */
    static int[] wholeTile(CityRuns.Ground g, long tx, long ty) {
        int[] v = new int[World.TILE * World.TILE];
        for (int iy = 0; iy < World.TILE; iy++) {
            for (int ix = 0; ix < World.TILE; ix++) v[iy * World.TILE + ix] = g.at(tx * World.TILE + ix, ty * World.TILE + iy, -1);
        }
        return v;
    }

    static void theLinksTiles() {
        out.println("--- 1. a link's search keeps every tile it reads, and reads the same ground ---");
        World world = World.of(SEED);
        long[] place = MapCheck.dryPlace(world);
        BuildingVisual.Type[] types = GatewayCheck.gatewayTypes();
        long[] counts = new long[types.length];
        int highway = GatewayCheck.highwayType(types), track = GatewayCheck.trackType(types);
        if (highway >= 0) counts[highway] = HIGHWAYS;
        if (track >= 0) counts[track] = TRACKS;
        // The smallest square of districts whose tiles are more than the usual keep.
        int tilesADistrict = CityMap.TILES_A_SIDE * CityMap.TILES_A_SIDE;
        int side = (int) Math.ceil(Math.sqrt((CityMap.RUN_TILES_KEPT + 1.0) / tilesADistrict));
        CityMap[] made = new CityMap[1];
        quietly(() -> made[0] = CityMap.square(SEED, place[0], place[1], types, side, CityMap.DISTRICT * CityMap.DISTRICT, counts));
        CityMap map = made[0];
        CityRuns.Ground ground = map.runGround();

        long[][] plots = onePlotATile(ground);
        int tiles = plots.length;
        report("fixture: the city owns more tiles than the usual keep, and fewer than the search's",
                tiles > CityMap.RUN_TILES_KEPT && tiles <= CityMap.RUN_SEARCH_TILES_KEPT,
                tiles + " tiles owned on a " + side + " x " + side + " districts square (keep " + CityMap.RUN_TILES_KEPT
                        + ", search " + CityMap.RUN_SEARCH_TILES_KEPT + ")");
        report("fixture: the keep in a search is above the usual keep, as the model's two constants stand",
                CityMap.RUN_SEARCH_TILES_KEPT > CityMap.RUN_TILES_KEPT, CityMap.RUN_TILES_KEPT + " then " + CityMap.RUN_SEARCH_TILES_KEPT);

        // A plot the city does not own reads -1 and draws no tile.
        long[] box = ground.box();
        long drawnBefore = map.runTilesDrawn();
        check("a plot the city does not own reads -1 and draws no tile",
                ground.at(box[0] - 1, box[1], -1) == -1 && ground.at(box[2] + 1, box[3], -1) == -1 && map.runTilesDrawn() == drawnBefore);

        /* (a) the usual keep: a pass round more tiles than it holds draws every tile again. */
        int[] a1 = pass(ground, plots);
        long d0 = map.runTilesDrawn();
        int[] a2 = pass(ground, plots);
        long drawnAgain = map.runTilesDrawn() - d0;
        report("with the usual keep a second pass round every tile draws them again (the keep thrashes on this many)",
                drawnAgain > CityMap.RUN_TILES_KEPT, drawnAgain + " tiles drawn on the second pass of " + tiles);
        check("...and reads what the first pass read", Arrays.equals(a1, a2));
        check("...every plot read is ground (not -1) on a city that owns it all", allOwned(a1));
        check("...and the keep held no more than the usual size", map.runTilesHeld() <= CityMap.RUN_TILES_KEPT);

        /* (b) a search: every tile kept. */
        ground.searching(true);
        long drawnFirst = map.runTilesDrawn();
        int[] b1 = pass(ground, plots);
        drawnFirst = map.runTilesDrawn() - drawnFirst;
        long d1 = map.runTilesDrawn();
        int[] b2 = pass(ground, plots);
        long drawnSecond = map.runTilesDrawn() - d1;
        int held = map.runTilesHeld();
        report("in a search the second pass draws no tile", drawnSecond == 0,
                drawnFirst + " tiles drawn on its first pass, " + drawnSecond + " on its second");
        check("...every value equals the usual keep's", Arrays.equals(a1, b1) && Arrays.equals(a1, b2));
        report("...and the keep holds more than the usual size, all the tiles read", held > CityMap.RUN_TILES_KEPT && held == tiles,
                held + " tiles held of " + tiles);

        // Every plot of a handful of tiles, in the search and after it, against the world's own terrain.
        int[][] inSearch = new int[HANDFUL][];
        long[][] handful = new long[HANDFUL][];
        for (int i = 0; i < HANDFUL; i++) {
            long[] p = plots[(int) ((long) i * (tiles - 1) / (HANDFUL - 1))];
            handful[i] = new long[] { Math.floorDiv(p[0], World.TILE), Math.floorDiv(p[1], World.TILE) };
            inSearch[i] = wholeTile(ground, handful[i][0], handful[i][1]);
        }

        /* (c) the search ends: the keep goes back to its usual size and the ground reads the same. */
        ground.searching(false);
        report("when the search ends the keep goes back to the usual size", map.runTilesHeld() <= CityMap.RUN_TILES_KEPT,
                map.runTilesHeld() + " tiles held after, of " + held + " in the search");
        int[] c1 = pass(ground, plots);
        check("...and a pass reads the same values again", Arrays.equals(a1, c1));

        boolean whole = true, world1 = true;
        byte[] terrain = new byte[World.TILE * World.TILE];
        for (int i = 0; i < HANDFUL; i++) {
            int[] after = wholeTile(ground, handful[i][0], handful[i][1]);
            world.tileTerrain(handful[i][0], handful[i][1], terrain);
            whole &= Arrays.equals(inSearch[i], after);
            for (int k = 0; k < terrain.length; k++) world1 &= after[k] == terrain[k] && inSearch[i][k] == terrain[k];
        }
        check("every plot of " + HANDFUL + " tiles reads the same in a search as out of one", whole);
        check("...and is the world's own terrain for it (World.tileTerrain())", world1);

        /* (d) linkWay() itself opens the search on the ground it is given and closes it, and finds the way a ground that keeps no more finds. */
        int[] calls = new int[2];
        CityRuns.Ground counted = new CityRuns.Ground() {
            @Override public int at(long x, long y, int dir) { return ground.at(x, y, dir); }
            @Override public long version() { return ground.version(); }
            @Override public long[] box() { return ground.box(); }
            @Override public void searching(boolean on) { calls[on ? 0 : 1]++; ground.searching(on); }
        };
        CityRuns.Ground plain = new CityRuns.Ground() {
            @Override public int at(long x, long y, int dir) { return ground.at(x, y, dir); }
            @Override public long version() { return ground.version(); }
            @Override public long[] box() { return ground.box(); }
        };
        CityRuns.Net hw = map.runs().highways();
        long drawnSearch = map.runTilesDrawn();
        CityRuns.LinkWay kept = CityRuns.linkWay(hw, map.runs().rail(), counted, 0, CityRuns.plotsOf(hw, 0), -1, 0, CityRuns.hubOf(hw, 0), LINK_DISTRICTS);
        drawnSearch = map.runTilesDrawn() - drawnSearch;
        report("linkWay() opens a search on its ground and closes it, once each", calls[0] == 1 && calls[1] == 1,
                calls[0] + " opened, " + calls[1] + " closed; " + drawnSearch + " tiles drawn");
        check("...and the ground keeps its usual size after it", map.runTilesHeld() <= CityMap.RUN_TILES_KEPT);
        CityRuns.LinkWay bare = CityRuns.linkWay(hw, map.runs().rail(), plain, 0, CityRuns.plotsOf(hw, 0), -1, 0, CityRuns.hubOf(hw, 0), LINK_DISTRICTS);
        report("...and the way it finds is the way found on a ground that keeps no more (searching() not passed on)", sameWay(kept, bare),
                kept == null ? "no way, both" : "from (" + kept.sx() + ", " + kept.sy() + ") to " + (kept.kind() == 0 ? "row " : "column ") + kept.v()
                        + ", " + kept.legs().length + " legs");
    }

    /** How many districts out from the hub section 1's link looks for a line (linkWay()'s `most`): one, the nearest ring of candidates. */
    static final long LINK_DISTRICTS = 1;

    /** Two planned ways are one way: the same start, line and legs (null, no way, is one only with null). */
    static boolean sameWay(CityRuns.LinkWay a, CityRuns.LinkWay b) {
        if (a == null || b == null) return a == b;
        return a.sx() == b.sx() && a.sy() == b.sy() && a.kind() == b.kind() && a.v() == b.v() && Arrays.deepEquals(a.legs(), b.legs());
    }

    static boolean allOwned(int[] v) {
        for (int x : v) if (x < 0) return false;
        return true;
    }

    /* =====================================================================
       2. THE CENTRE'S STAMP
       ===================================================================== */

    static void theCentresStamp() {
        out.println("\n--- 2. the centre's stamp is kept between its changes and moves with them ---");
        World world = World.of(SEED);
        long[] place = MapCheck.dryPlace(world);
        CityLand land = CityLand.found(world, place[0], place[1], DRY_KM2);
        int blocks = land.centreRects().size();
        long first = land.centreStamp();
        long drawn = land.centreStampDrawn();
        report("a founded centre's stamp is the hash drawn from its blocks", first == drawn, blocks + " blocks");
        check("...and asked again, the same", land.centreStamp() == drawn && land.centreStamp() == first);

        CityLand before = land.copy();
        check("a restored copy has the original's stamp, kept and drawn", before.centreStamp() == first && before.centreStampDrawn() == first);

        // A restatement with more ground, round the same site (LandDrawing.restate()).
        LandDrawing.Result more = LandDrawing.draw(world, land.siteX(), land.siteY(), MORE_KM2, 0, 0, 0, false);
        land.redraw(more);
        long after = land.centreStamp();
        report("after a restatement with more ground the stamp is the hash of the new blocks", after == land.centreStampDrawn(),
                land.centreRects().size() + " blocks now");
        check("...and is not the stamp it was", after != first && land.centreRects().size() != blocks);
        check("...asked again, the same", land.centreStamp() == after);
        check("a copy taken before it keeps the stamp it had", before.centreStamp() == first && before.centreStamp() != after);

        CityLand later = land.copy();
        check("a copy taken after it has the new stamp", later.centreStamp() == after && later.centreStampDrawn() == after);

        // A second restatement on the heels of the first, the stamp kept from it: it moves again.
        LandDrawing.Result less = LandDrawing.draw(world, land.siteX(), land.siteY(), DRY_KM2, 0, 0, 0, false);
        land.redraw(less);
        long third = land.centreStamp();
        report("a second restatement, to less ground, moves the kept stamp again to the hash of its blocks",
                third == land.centreStampDrawn() && third != after, land.centreRects().size() + " blocks now");
        check("...asked again, the same", land.centreStamp() == third);
    }

    /* =====================================================================
       3. A TILE'S LANDMASS IDS
       ===================================================================== */

    static void theLandmassIds() throws Exception {
        out.println("\n--- 3. a tile's landmass ids are kept, the same as read from the cells ---");
        byte[] fixture = EarthCheck.resource(EarthCheck.FIXTURE);
        check("EarthCheck's fixture is beside the classes (" + EarthCheck.FIXTURE + ")", fixture != null);
        if (fixture == null) return;
        EarthData data = EarthData.of(fixture);
        String id = EarthTerrain.use(data);
        World world = World.of(World.Key.earth(LandmassCheck.SEED, LandmassCheck.RLAT, LandmassCheck.RLON, id));
        EarthTerrain e = world.earthTerrain();
        check("the Earth fixture's world has landmasses to read", world.hasLandmasses() && world.isEarth() && data.hasRelief());

        long fx = world.foundingX() / World.TILE, fy = world.foundingY() / World.TILE;
        int tiles = 0, sameIds = 0, sameArray = 0, one = 0, two = 0;
        for (long ty = fy - WINDOW; ty <= fy + WINDOW; ty++) {
            for (long tx = fx - WINDOW; tx <= fx + WINDOW; tx++) {
                int[] kept = e.idsNear(tx, ty);
                tiles++;
                if (Arrays.equals(kept, e.idsNearDrawn(tx, ty))) sameIds++;
                if (e.idsNear(tx, ty) == kept) sameArray++;
                if (kept.length >= 1) one++;
                if (kept.length >= 2) two++;
            }
        }
        report("fixture: tiles in the window hold one landmass id or more, and two or more", one > 0 && two > 0,
                tiles + " tiles, " + one + " with an id, " + two + " with two or more");
        report("every tile's ids, kept, are the ids read from the cells", sameIds == tiles, sameIds + " of " + tiles);
        report("...and a second ask is the same array, not a read again", sameArray == tiles, sameArray + " of " + tiles);
        check("a tile off the world holds no ids", e.idsNear(-1, 0).length == 0 && e.idsNear(0, -1).length == 0);

        // The keep fills and is cleared: tiles by the thousand against IDS_KEPT, the first read of them given up and read again.
        int across = (int) Math.ceil(Math.sqrt(EarthTerrain.IDS_KEPT)) + 4;
        long tx0 = fx - across / 2, ty0 = fy - across / 2;
        int[] firstKept = e.idsNear(tx0, ty0);
        for (long ty = ty0; ty < ty0 + across; ty++) {
            for (long tx = tx0; tx < tx0 + across; tx++) e.idsNear(tx, ty);
        }
        int[] again = e.idsNear(tx0, ty0);
        report("a keep that has filled to IDS_KEPT and been cleared reads the first tile's ids again, the same", Arrays.equals(again, firstKept)
                && Arrays.equals(again, e.idsNearDrawn(tx0, ty0)), (long) across * across + " tiles read against IDS_KEPT " + EarthTerrain.IDS_KEPT);
        check("...as a new array, the old one let go with the rest, and then kept again", again != firstKept && e.idsNear(tx0, ty0) == again);
        boolean allSame = true;
        for (long ty = fy - WINDOW; ty <= fy + WINDOW; ty++) {
            for (long tx = fx - WINDOW; tx <= fx + WINDOW; tx++) allSame &= Arrays.equals(e.idsNear(tx, ty), e.idsNearDrawn(tx, ty));
        }
        check("...and every tile of the first window still reads as the cells give it", allSame);
    }

    /* =====================================================================
       4. WHAT EACH SECTOR OWES
       ===================================================================== */

    /** How many sectors the bank's book holds a loan of. */
    static int sectorsLent(BusinessDebtManager bank) {
        Set<String> s = new HashSet<>();
        for (BusinessDebt loan : bank.getLoans()) s.add(loan.getSector());
        return s.size();
    }

    /** The most loans the bank's book holds of any one sector: more than one, and the sum's order is a thing to keep. */
    static int mostLoansOfASector(BusinessDebtManager bank) {
        java.util.Map<String, Integer> n = new java.util.HashMap<>();
        int most = 0;
        for (BusinessDebt loan : bank.getLoans()) most = Math.max(most, n.merge(loan.getSector(), 1, Integer::sum));
        return most;
    }

    static void theBook() {
        out.println("\n--- 4. what each sector owes, from one pass over the book, is getPrincipal() to the bit ---");
        Set<String> keys = new HashSet<>(Arrays.asList(Sectors.KEYS));
        check("fixture: Sectors.KEYS holds no key twice (the one pass puts a sector at its first place)", keys.size() == Sectors.KEYS.length);

        GameFiles files = GameFiles.scratch("keptreadscheck");
        Game city = ShortSellingCheck.lineOff(new Game(files));
        city.quietTheWorldForTheD();
        quietly(() -> {
            city.run();
            city.getEconomyManager().getTaxPolicy().reset();
            city.setCashForTest(Founding.WEALTHY_CASH);
            city.getLandManager().setOwnedSqFt(city.getLandManager().getOwnedSqFt() + 50_000_000L);
            city.getForeignAccounts().pinRate(1.0);
            city.getBusinessInvestment().holdSector(Sectors.CONSTRUCTION);
            city.getBusinessInvestment().holdSector(Sectors.HEAVY_INDUSTRY);
            city.buildStack(HoldersCheck.template(city, "Gravel Road"), 6, true);
            city.buildStack(HoldersCheck.template(city, "House"), 300, false);
            city.buildStack(HoldersCheck.template(city, "Convenience Store"), 6, false);
            city.buildStack(HoldersCheck.template(city, "Industrial Bakery"), 2, false);
            city.buildStack(HoldersCheck.template(city, "Construction Depot"), 4, false);
            city.buildStack(HoldersCheck.template(city, "Coal Power Plant"), 1, false);
            city.buildStack(HoldersCheck.template(city, "Commercial Bank"), 1, false);
            city.simulateMonths(MONTHS_FIRST);
        });
        BusinessDebtManager bank = city.getEconomyManager().getBusinessDebtManager();
        int months = MONTHS_FIRST;
        for (; months < MONTHS_MOST && sectorsLent(bank) < 2; months++) quietly(() -> city.simulateMonths(1));
        int lent = sectorsLent(bank);
        report("fixture: the bank's book holds loans in at least two sectors", lent >= 2,
                bank.getLoans().size() + " loans in " + lent + " sectors after " + months + " months");
        int most = mostLoansOfASector(bank);
        report("fixture: a sector holds several loans, so the order they are summed in is in the figure", most >= 2, most + " loans in the most indebted sector");
        int owing = 0, clear = 0, bonds = 0;
        for (String k : Sectors.KEYS) {
            if (bank.getPrincipal(k) > 0) owing++;
            else if (bank.getPrincipal(k) == 0) clear++;
            if (bank.getBondPrincipal(k) > 0) bonds++;
        }
        out.println("   the book's bonds are held in " + bonds + " sectors");
        report("fixture: one sector owes more than nothing and one owes nothing (both sides of `owed > 0`)", owing >= 1 && clear >= 1,
                owing + " sectors owe, " + clear + " owe nothing, of " + Sectors.KEYS.length);

        double[] owed = bank.getPrincipalsByKey();
        check("one figure for every sector of Sectors.KEYS", owed.length == Sectors.KEYS.length);
        String[] first = new String[1];
        int apart = sectorsApart(bank, first);
        report("every sector's figure is getPrincipal() to the bit", apart == 0,
                (Sectors.KEYS.length - apart) + " of " + Sectors.KEYS.length + (first[0] == null ? "" : ", first apart: " + first[0]));
        double[] again = bank.getPrincipalsByKey();
        check("...and a second pass over the book gives the same figures", Arrays.equals(owed, again) && again != owed);

        // The book as it moves: a month at a time, the figures drawn afresh each time (a sum of several loans is not exact, so its order is in its bits).
        int monthsLooked = 0, monthsApart = 0, sectorsLooked = 0;
        for (int m = 0; m < MONTHS_LOOKED; m++) {
            quietly(() -> city.simulateMonths(1));
            int a = sectorsApart(bank, first);
            monthsLooked++;
            sectorsLooked += Sectors.KEYS.length;
            if (a > 0) monthsApart++;
        }
        report("...and in each of the months after, every sector's figure is getPrincipal() to the bit", monthsApart == 0,
                monthsLooked + " months, " + sectorsLooked + " figures, " + bank.getLoans().size() + " loans in " + sectorsLent(bank)
                        + " sectors at the last" + (first[0] == null ? "" : ", first apart: " + first[0]));
    }

    /** How many sectors' getPrincipalsByKey() figures are not getPrincipal() to the bit; `first` takes a line on the first that is not. */
    static int sectorsApart(BusinessDebtManager bank, String[] first) {
        double[] owed = bank.getPrincipalsByKey();
        int apart = 0;
        first[0] = null;
        for (int i = 0; i < Sectors.KEYS.length && i < owed.length; i++) {
            double each = bank.getPrincipal(Sectors.KEYS[i]);
            if (Double.doubleToRawLongBits(owed[i]) != Double.doubleToRawLongBits(each)) {
                apart++;
                if (first[0] == null) first[0] = Sectors.KEYS[i] + " " + owed[i] + " against " + each;
            }
        }
        return apart;
    }
}

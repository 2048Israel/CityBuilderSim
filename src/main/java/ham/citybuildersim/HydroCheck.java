package ham.citybuildersim;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * The Hydro Station and the river it needs (HY1, 2026-10-26; docs/queue/HY1-notes.md): the physics of a drop against
 * its own constants, the sites on synthetic ground, a generated world's city that has none, a city given sites by fiat
 * that orders up to its slots, and the real Earth's rivers when earth-2.bin is named. Not part of the game.
 *
 * WHY. The game had only coal and wind, and Canada's power was mostly hydro from 1900. The Earth has no heights on land
 * (spec-earth E8), so a river's drop is read from its banks (HydroSites): the lower bank's rise over REACH plots, each
 * plot at its relief class's least grade, with the flow from the river's width. Every rule of that reading is caused
 * here on ground a fixture lays out, from the model's own constants and from both sides of each threshold, and the
 * Earth is asked only whether a river of the real world reads as one: a hydro town does, a flat one does not.
 *
 * What this has to prove:
 *   1. THE PHYSICS: the flow is (width / HG_COEFF) squared and holds at WIDTH_CAP_M; the power is EFFICIENCY x G x Q x H;
 *      a bank's rise is REACH plots of PLOT_M x the class's least grade, for each class; a head under HEAD_MIN_M is none.
 *   2. SITES ON SYNTHETIC GROUND (the core, a relief lambda and segments): a river with foothill on both banks gives the
 *      head and the power the model says; one bank only gives nothing; the head just under HEAD_MIN_M gives nothing and
 *      just over gives a site; a river across three cells gives three; the width is read at each point of a segment; a
 *      cell's site is the best of every sample in it, so a best point on ground the city does not own is not the city's;
 *      a tie goes to the lower y and then the lower x, the same in whatever order the segments are listed.
 *   3. A GENERATED WORLD HAS NO RIVER: no site, and no river read at all; an order for a Hydro Station is refused
 *      NO_RIVER, the card's verdict and a run's stop say so, the advice values it at nothing, and the automatic builder
 *      run some months orders none.
 *   4. SITES BY FIAT: orders up to the slots go ahead and the next is NO_RIVER (a run counts the orders ahead of it);
 *      each station standing adds its production1 to the grid; it stands on steep ground a coal plant cannot; the advice
 *      offers it while a slot is free, holds its count to the slots free, and offers none past them.
 *   5. THE EARTH, when earth-2.bin is named: 20 km squares round hydro towns find sites and round flat towns none, and
 *      a city founded at Shawinigan has the sites its ground owns, as the world reads them; ground bought round the
 *      nearest site that holds a station makes it the city's, linked, and an order for its stations goes ahead up to
 *      the slots and no further.
 */
public class HydroCheck {

    static int fails = 0;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-110s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** A check against a figure, to a part in a billion of it. */
    static void near(String label, double actual, double expected) {
        boolean ok = Math.abs(actual - expected) <= 1e-9 * Math.max(1, Math.abs(expected));
        if (!ok) fails++;
        System.out.printf("%-86s %16.4f  expected %16.4f  %s%n", label, actual, expected, ok ? "OK" : "FAIL");
    }

    static void quietly(Runnable work) {
        java.io.PrintStream out = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        try { work.run(); } finally { System.setOut(out); }
    }

    static <T> T quiet(java.util.function.Supplier<T> work) {
        java.io.PrintStream out = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        try { return work.get(); } finally { System.setOut(out); }
    }

    /** The seed every world here is drawn with: the default world's. */
    static final long SEED = Founding.DEFAULT_WORLD_SEED;

    public static void main(String[] args) throws Exception {
        java.util.Locale.setDefault(java.util.Locale.CANADA);
        // ChildcareCheck's town is the fixture of section 3's months: its streams too.
        java.io.PrintStream quiet = new java.io.PrintStream(java.io.OutputStream.nullOutputStream());
        ChildcareCheck.out = quiet;
        ChildcareCheck.quiet = quiet;
        LandClassCheck.out = quiet;
        LandClassCheck.quiet = quiet;
        thePhysics();
        syntheticGround();
        Game plain = generatedWorld();
        fiatSites(plain);
        theEarth();
        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* =====================================================================
       1. THE PHYSICS
       ===================================================================== */

    /** The rise of a bank of this relief class all the way out, metres: REACH plots at PLOT_M x the class's least grade. */
    static double riseOf(byte relief) {
        return HydroSites.REACH * World.PLOT_M * HydroSites.leastGrade(relief);
    }

    static void thePhysics() {
        System.out.println("--- 1. the physics: the flow, the power and a bank's rise ---");
        double w = 60;
        near("a 60 m river's mean flow is (width / HG_COEFF) squared", HydroSites.dischargeM3s(w), (w / HydroSites.HG_COEFF) * (w / HydroSites.HG_COEFF));
        near("...a river twice as wide carries four times the flow, under the cap", HydroSites.dischargeM3s(2 * w), 4 * HydroSites.dischargeM3s(w));
        double under = HydroSites.WIDTH_CAP_M / 2, over = HydroSites.WIDTH_CAP_M * 3;
        near("...a river under WIDTH_CAP_M is read at its own width", HydroSites.dischargeM3s(under), Math.pow(under / HydroSites.HG_COEFF, 2));
        near("...a river over WIDTH_CAP_M is read at the cap's flow, not its own", HydroSites.dischargeM3s(over), HydroSites.dischargeM3s(HydroSites.WIDTH_CAP_M));
        assertTrue("...so a river just over the cap carries what the cap does and a hair under it carries less",
                HydroSites.dischargeM3s(HydroSites.WIDTH_CAP_M + 1) == HydroSites.dischargeM3s(HydroSites.WIDTH_CAP_M)
                        && HydroSites.dischargeM3s(HydroSites.WIDTH_CAP_M - 1) < HydroSites.dischargeM3s(HydroSites.WIDTH_CAP_M));
        double head = riseOf(World.FOOTHILL);
        near("a river's mean power is EFFICIENCY x G x Q x H, in kW", HydroSites.meanKw(w, head),
                HydroSites.EFFICIENCY * HydroSites.G * HydroSites.dischargeM3s(w) * head);
        near("...twice the head is twice the power", HydroSites.meanKw(w, 2 * head), 2 * HydroSites.meanKw(w, head));
        near("...no head is none", HydroSites.meanKw(w, 0), 0);
        near("...the units a month a power makes are its kW x HOURS_A_MONTH over KWH_PER_UNIT",
                HydroSites.unitsOfKw(1000), 1000 * HydroSites.HOURS_A_MONTH / HydroSites.KWH_PER_UNIT);
        System.out.printf("   for the notes' figures: a 30 m river under foothill banks gives %,.0f kW, a 60 m one %,.0f kW, a 200 m one %,.0f kW (head %.0f m)%n",
                HydroSites.meanKw(30, head), HydroSites.meanKw(60, head), HydroSites.meanKw(200, head), head);

        // A bank's rise, for each class: the class everywhere reads REACH plots of PLOT_M x its least grade.
        String[] names = { "flat", "foothill", "steep", "very steep" };
        for (byte c = 0; c < World.RELIEFS; c++) {
            final byte cls = c;
            double rise = HydroSites.bankRiseM((x, y) -> cls, 100.5, 100.5, 0, 1, 1);
            near("a " + names[c] + " bank's rise is REACH plots at PLOT_M x its least grade (" + HydroSites.leastGrade(c) + ")", rise, riseOf(c));
        }
        boolean up = true;
        for (byte c = 1; c < World.RELIEFS; c++) up &= HydroSites.leastGrade(c) > HydroSites.leastGrade((byte) (c - 1));
        assertTrue("...each class's least grade is more than the one before's, flat's none", up && HydroSites.leastGrade(World.FLAT) == 0);
        // The grades are the edges the classes are cut at, as the land office's words say them (LandMap.GRADE_WORDS).
        boolean words = true;
        for (byte c = 1; c < World.RELIEFS; c++) words &= LandMap.GRADE_WORDS[c].startsWith(String.valueOf(Math.round(100 * HydroSites.leastGrade(c))));
        assertTrue("...and each is the lower edge of the grade the land office says its class has (LandMap.GRADE_WORDS)", words);
        assertTrue("a byte that is no class reads flat: no rise", HydroSites.leastGrade((byte) 9) == 0 && HydroSites.leastGrade((byte) -1) == 0);
        // Only the plots out to REACH count: a class past the reach is not read.
        double reachOnly = HydroSites.bankRiseM((x, y) -> y > 100 + HydroSites.REACH ? World.VERY_STEEP : World.FOOTHILL, 100.5, 100.5, 0, 1, 0.0);
        near("a bank is read REACH plots out and no further (a plot past the reach is not asked)", reachOnly, riseOf(World.FOOTHILL));
        // The head: the lower bank's, and none under HEAD_MIN_M.
        near("the head at a point is the lower bank's rise", HydroSites.headM(riseOf(World.STEEP), riseOf(World.FOOTHILL)), riseOf(World.FOOTHILL));
        near("...and under HEAD_MIN_M there is none", HydroSites.headM(HydroSites.HEAD_MIN_M - 0.001, 1e6), 0);
        near("...at HEAD_MIN_M there is", HydroSites.headM(HydroSites.HEAD_MIN_M, 1e6), HydroSites.HEAD_MIN_M);
    }

    /* =====================================================================
       2. SITES ON SYNTHETIC GROUND
       ===================================================================== */

    /** The plot row of the water of every river here: the river is two plots (60 m) wide, rows Y0 and Y0 + 1, its centre the line y = Y0 + 1. */
    static final long Y0 = 60;

    /** The width of the fixtures' rivers, metres: two plots. */
    static final double W = 2 * World.PLOT_M;

    /** A river along the line y = Y0 + 1 from x0 to x1, W wide. */
    static HydroSites.Segment river(double x0, double x1) {
        return new HydroSites.Segment(x0, Y0 + 1, x1, Y0 + 1, W, W);
    }

    /** Ground with the banks' relief given: the water's rows flat; the plots north of it (k = Y0 - y, 1 and up) and those south (k = y - Y0 - 1) read by their number out from the water. */
    static HydroSites.Relief banks(java.util.function.IntFunction<Byte> north, java.util.function.IntFunction<Byte> south) {
        return (x, y) -> {
            if (y < Y0) return north.apply((int) (Y0 - y));
            if (y > Y0 + 1) return south.apply((int) (y - Y0 - 1));
            return World.FLAT;
        };
    }

    static HydroSites.Relief valley(byte both) { return banks(k -> both, k -> both); }

    static final HydroSites.Cells ALL = (cx, cy) -> true;

    static void syntheticGround() {
        System.out.println("\n--- 2. sites on synthetic ground ---");
        double foothill = riseOf(World.FOOTHILL);

        // A valley: foothill on both banks.
        List<HydroSites.Site> one = HydroSites.scan(valley(World.FOOTHILL), List.of(river(1, 100)), ALL);
        assertTrue("a straight river 60 m wide with foothill on both banks, inside one cell, gives one site", one.size() == 1);
        HydroSites.Site s = one.get(0);
        near("...its head is REACH plots of PLOT_M x the foothill grade", s.headM(), foothill);
        near("...its width is the river's", s.widthM(), W);
        near("...its mean power is the model's for that width and head", s.meanKw(), HydroSites.meanKw(W, foothill));
        assertTrue("...every sample of it ties, so the site is the lowest y then the lowest x of the river's samples: the river's own row and its first plot",
                s.y() == Y0 + 1 && s.x() == 1);
        near("...and what it makes a month is its kW x HOURS_A_MONTH over KWH_PER_UNIT", s.units(), HydroSites.unitsOfKw(s.meanKw()));
        near("...it holds floor(units over a station's) stations: 2.5 stations' worth holds 2", new HydroSites.Site(0, 0, W, foothill, 2.5 * 1000 * HydroSites.KWH_PER_UNIT / HydroSites.HOURS_A_MONTH, 0).slots(1000), 2);

        // One bank only: foothill on one side of the river, flat on the other - the head is the lower bank's, none.
        assertTrue("foothill on the north bank only gives no site: the head is the lower bank's",
                HydroSites.scan(banks(k -> World.FOOTHILL, k -> World.FLAT), List.of(river(1, 100)), ALL).isEmpty());
        assertTrue("...nor on the south bank only",
                HydroSites.scan(banks(k -> World.FLAT, k -> World.FOOTHILL), List.of(river(1, 100)), ALL).isEmpty());
        // ...and the lower of two different banks is the head: steep on one side, foothill on the other.
        List<HydroSites.Site> mixed = HydroSites.scan(banks(k -> World.STEEP, k -> World.FOOTHILL), List.of(river(1, 100)), ALL);
        assertTrue("steep on one bank and foothill on the other gives the foothill's head, the lower",
                mixed.size() == 1 && Math.abs(mixed.get(0).headM() - foothill) < 1e-9);
        List<HydroSites.Site> steep = HydroSites.scan(valley(World.STEEP), List.of(river(1, 100)), ALL);
        assertTrue("steep banks give the steep head, more than the foothill's, and so more power",
                steep.size() == 1 && Math.abs(steep.get(0).headM() - riseOf(World.STEEP)) < 1e-9 && steep.get(0).meanKw() > one.get(0).meanKw());
        // Water on a bank reads flat (the lambda gives the plots beside a lake FLAT): a lake on one side cuts that bank's rise.
        assertTrue("a bank of which the near plots are water (flat) rises only by the rest: the rise is less and no more than the plots beyond",
                Math.abs(HydroSites.bankRiseM(banks(k -> k <= 3 ? World.FLAT : World.FOOTHILL, k -> World.FOOTHILL), 100.5, Y0 + 1.0, 0, -1, 1)
                        - (HydroSites.REACH - 3) * World.PLOT_M * HydroSites.leastGrade(World.FOOTHILL)) < 1e-9);

        // Just under and just over HEAD_MIN_M: the lower bank has m foothill plots out from the water and flat beyond.
        double perPlot = World.PLOT_M * HydroSites.leastGrade(World.FOOTHILL);
        int mUnder = (int) Math.ceil(HydroSites.HEAD_MIN_M / perPlot) - 1, mOver = mUnder + 1;
        assertTrue("fixture: " + mUnder + " foothill plots rise " + mUnder * perPlot + " m, under HEAD_MIN_M, and " + mOver + " rise "
                + mOver * perPlot + " m, at or over it", mUnder * perPlot < HydroSites.HEAD_MIN_M && mOver * perPlot >= HydroSites.HEAD_MIN_M && mOver <= HydroSites.REACH);
        assertTrue("a head just under HEAD_MIN_M gives no site",
                HydroSites.scan(banks(k -> World.FOOTHILL, k -> k <= mUnder ? World.FOOTHILL : World.FLAT), List.of(river(1, 100)), ALL).isEmpty());
        List<HydroSites.Site> justOver = HydroSites.scan(banks(k -> World.FOOTHILL, k -> k <= mOver ? World.FOOTHILL : World.FLAT), List.of(river(1, 100)), ALL);
        assertTrue("...and just over it gives one, at that head",
                justOver.size() == 1 && Math.abs(justOver.get(0).headM() - mOver * perPlot) < 1e-9);

        // A river across three cells: one site each.
        long cell = HydroSites.CELL;
        List<HydroSites.Site> three = HydroSites.scan(valley(World.FOOTHILL), List.of(river(10, 3 * cell - 10)), ALL);
        assertTrue("a river across three cells gives three sites, a cell each, the same ground giving the same figures",
                three.size() == 3 && Math.floorDiv(three.get(0).x(), cell) == 0 && Math.floorDiv(three.get(1).x(), cell) == 1
                        && Math.floorDiv(three.get(2).x(), cell) == 2 && three.get(0).meanKw() == three.get(2).meanKw());
        assertTrue("...each at the lowest x of its cell the river reaches (the ties go to the lower x)",
                three.get(0).x() == 10 && three.get(1).x() == cell && three.get(2).x() == 2 * cell);
        // A site is the world's: asking for one cell gives that cell's site, whatever the others.
        List<HydroSites.Site> middle = HydroSites.scan(valley(World.FOOTHILL), List.of(river(10, 3 * cell - 10)), (cx, cy) -> cx == 1);
        assertTrue("a cell's site is the same asked alone as with its neighbours (defined on the world, not on what is read with it)",
                middle.size() == 1 && middle.get(0).equals(three.get(1)));
        // A segment's width is read along it: a river widening east has its site at the wide end.
        HydroSites.Segment widening = new HydroSites.Segment(1, Y0 + 1, 100, Y0 + 1, W, 3 * W);
        List<HydroSites.Site> wide = HydroSites.scan(valley(World.FOOTHILL), List.of(widening), ALL);
        double len = 99, tLast = (99 - 0.5) / 99;
        assertTrue("a river that widens along its segment has its site where it is widest, its last sample, the width between its ends there",
                wide.size() == 1 && wide.get(0).x() == (long) Math.floor(1 + len * tLast)
                        && Math.abs(wide.get(0).widthM() - (W + (3 * W - W) * tLast)) < 1e-9);

        // A cell's best sample on ground the city does not own is not the city's, though the owned part of the river is as good.
        HydroSites.Segment widensWest = new HydroSites.Segment(10, Y0 + 1, 100, Y0 + 1, 3 * W, W);
        HydroSites.Segment widensEast = new HydroSites.Segment(10, Y0 + 1, 100, Y0 + 1, W, 3 * W);
        HydroSites.Owns eastHalf = (x, y) -> x >= 64;
        List<HydroSites.Site> westBest = HydroSites.scan(valley(World.FOOTHILL), List.of(widensWest), ALL);
        List<HydroSites.Site> eastBest = HydroSites.scan(valley(World.FOOTHILL), List.of(widensEast), ALL);
        assertTrue("fixture: the river's best point is its wide end - the west end in one, the east end in the other",
                westBest.size() == 1 && westBest.get(0).x() < 64 && eastBest.size() == 1 && eastBest.get(0).x() >= 64);
        assertTrue("a cell whose best sample lies on unowned ground is not the city's, though the owned part of the river has a drop too",
                HydroSites.keepOwned(westBest, eastHalf).isEmpty());
        assertTrue("...and one whose best sample is owned is", HydroSites.keepOwned(eastBest, eastHalf).equals(eastBest));
        // ...and a tie across the owned line is the lower x, unowned here: the owned part's equal samples do not take it.
        List<HydroSites.Site> flatTie = HydroSites.scan(valley(World.FOOTHILL), List.of(river(10, 100)), ALL);
        assertTrue("a tie across the line of ownership goes to the lower x as ever, so the site is on the unowned side and not the city's",
                flatTie.size() == 1 && flatTie.get(0).x() == 10 && HydroSites.keepOwned(flatTie, eastHalf).isEmpty());

        // A tie goes to the lower y, then the lower x, whatever the order the segments are listed in.
        long rowB = Y0 + 30;
        HydroSites.Relief twoRivers = (x, y) -> y == Y0 || y == Y0 + 1 || y == rowB || y == rowB + 1 ? World.FLAT : World.FOOTHILL;
        HydroSites.Segment a = new HydroSites.Segment(20, Y0 + 1, 60, Y0 + 1, W, W);
        HydroSites.Segment b = new HydroSites.Segment(20, rowB + 1, 60, rowB + 1, W, W);
        List<HydroSites.Site> ab = HydroSites.scan(twoRivers, List.of(a, b), ALL), ba = HydroSites.scan(twoRivers, List.of(b, a), ALL);
        assertTrue("two rivers of the same flow and drop in one cell: the site is the one of the lower y",
                ab.size() == 1 && ab.get(0).y() == Y0 + 1 && Math.abs(ab.get(0).meanKw() - HydroSites.meanKw(W, foothill)) < 1e-6);
        assertTrue("...the same whichever order the segments are listed in", ab.equals(ba));
        HydroSites.Segment c = new HydroSites.Segment(70, Y0 + 1, 110, Y0 + 1, W, W);
        List<HydroSites.Site> ac = HydroSites.scan(twoRivers, List.of(c, a), ALL), ca = HydroSites.scan(twoRivers, List.of(a, c), ALL);
        assertTrue("...and at the same y, the lower x, in either order", ac.size() == 1 && ac.get(0).x() == 20 && ac.equals(ca));
        List<HydroSites.Site> reversed = HydroSites.scan(valley(World.FOOTHILL), List.of(river(3 * cell - 10, 10 + 2 * cell), river(10 + cell, 10), river(10 + cell, 3 * cell - 10)), ALL);
        List<HydroSites.Site> inOrder = HydroSites.scan(valley(World.FOOTHILL), List.of(river(10 + cell, 10), river(10 + cell, 3 * cell - 10), river(3 * cell - 10, 10 + 2 * cell)), ALL);
        assertTrue("...a river given as segments in any order and either direction gives the same sites", reversed.equals(inOrder));
        // The better sample wins before any tie is looked at: a narrow river at the lower y loses to a wide one at the higher.
        HydroSites.Segment narrow = new HydroSites.Segment(20, Y0 + 1, 60, Y0 + 1, W, W);
        HydroSites.Segment broad = new HydroSites.Segment(20, rowB + 1, 60, rowB + 1, 2 * W, 2 * W);
        List<HydroSites.Site> better = HydroSites.scan(twoRivers, List.of(narrow, broad), ALL);
        assertTrue("the best sample wins before any tie is looked at: a wider river at the higher y beats a narrower one at the lower",
                better.size() == 1 && better.get(0).y() == rowB + 1 && better.get(0).widthM() == 2 * W);
        assertTrue("a zero-length segment reads nothing, and no river at all gives no site",
                HydroSites.scan(valley(World.FOOTHILL), List.of(new HydroSites.Segment(50, 50, 50, 50, W, W)), ALL).isEmpty()
                        && HydroSites.scan(valley(World.FOOTHILL), List.of(), ALL).isEmpty());
    }

    /* =====================================================================
       3. A GENERATED WORLD HAS NO RIVER
       ===================================================================== */

    static BuildingsTemplate hydroOf(Game g) {
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName("Hydro Station");
        if (t == null) throw new IllegalStateException("no Hydro Station");
        return t;
    }

    /** The station's own mean power, kW: the units it makes a month, back through the game's hours and unit. */
    static double stationKw(BuildingsTemplate t) {
        return t.getProduction1() * HydroSites.KWH_PER_UNIT / HydroSites.HOURS_A_MONTH;
    }

    static Game generatedWorld() throws Exception {
        System.out.println("\n--- 3. a generated world has no river ---");
        Game g = new Game(GameFiles.scratch("hydrocheck-generated"));
        quietly(() -> { g.newGame(); g.toggleNextMonth(); });
        BuildingsTemplate hydro = hydroOf(g);
        BuildingsTemplate coal = g.getBuildingManager().getTemplateByName("Coal Power Plant");
        HydroSites sites = g.getLandManager().getHydroSites();
        assertTrue("fixture: the city's world is a generated one, not the Earth", !g.getWorld().isEarth());
        assertTrue("the Hydro Station is a power station that needs a river, 10 MW at a 45% capacity factor (production1 4,560), and stands on any ground",
                hydro.getCategory() == BuildingType.ELECTRICITY && hydro.isHydro() && !coal.isHydro() && Game.standsAnywhere(hydro) && !Game.standsAnywhere(coal));
        assertTrue("...its own station's power is its production1's kW: 4,500 kW a month at 730 hours over 720 kWh a unit, the Wind Farm's arithmetic",
                Math.abs(stationKw(hydro) - 4500) < 50);
        assertTrue("a generated world has no site and reads no river at all", sites.sites().isEmpty() && sites.linked().isEmpty() && sites.segmentsRead() == 0);
        assertTrue("...so it holds no station: no slots, none free", g.hydroSlots(hydro) == 0 && g.hydroSlotsFree(hydro) == 0);
        assertTrue("...and no building but a hydro station is held to a river", g.hasRiverFor(coal, 1) && !g.hasRiverFor(hydro, 1) && g.hasRiverFor(hydro, 0));
        g.setCashForTest(Founding.WEALTHY_CASH);
        assertTrue("AN ORDER FOR A HYDRO STATION IS REFUSED NO_RIVER, before land and money", quiet(() -> g.buildStack(hydro, 1, false)) == Game.BuildResult.NO_RIVER);
        assertTrue("...and it takes nothing: none standing, none on site", g.hydroCommitted() == 0);
        assertTrue("...the card's verdict says so before the click", BuildCard.verdict(g, hydro, 1).kind() == BuildCard.VerdictKind.NO_RIVER);
        java.util.LinkedHashMap<BuildingsTemplate, Integer> run = new java.util.LinkedHashMap<>();
        run.put(hydro, 1);
        assertTrue("...and a run stops at it", g.buildRunAhead(run) == 0 && g.buildRunStop(run) == Game.BuildResult.NO_RIVER);
        assertTrue("...the card carries no river: no sites, no slots", BuildCard.hydro(g, hydro).free() == 0 && BuildCard.hydro(g, hydro).slots() == 0
                && BuildCard.hydro(g, hydro).sites() == 0 && BuildCard.hydro(g, coal) == null);
        BuildAdvice.Measure power = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);
        assertTrue("...the advice values it at nothing: unit() is 0 where there is no river", BuildAdvice.unit(g, power, hydro) == 0);

        // The automatic builder, some months on a city that orders power: it names no Hydro Station.
        Path root = TempDirs.create("hydrocheck");
        Game town = ChildcareCheck.town(root, "auto");
        AutoBuilder ab = town.getAutoBuilder();
        int hydrosBefore = town.hydroCommitted();
        ab.setOn(true, null);
        int months = 24;
        quietly(() -> town.simulateMonths(months));
        assertTrue("fixture: the town's automatic builder ran " + months + " months and ordered something (" + ab.getOrders() + " orders)", ab.getOrders() > 0);
        assertTrue("THE AUTOMATIC BUILDER NEVER ORDERS A HYDRO STATION ON A CITY WITH NO RIVER: none standing, none on site after " + months + " months",
                hydrosBefore == 0 && town.hydroCommitted() == 0);
        return g;
    }

    /* =====================================================================
       4. SITES BY FIAT
       ===================================================================== */

    static void fiatSites(Game g) {
        System.out.println("\n--- 4. sites by fiat: the orders go ahead up to the slots, and the next is NO_RIVER ---");
        BuildingsTemplate hydro = hydroOf(g);
        BuildingsTemplate coal = g.getBuildingManager().getTemplateByName("Coal Power Plant");
        HydroSites sites = g.getLandManager().getHydroSites();
        double kw = stationKw(hydro);
        double head = riseOf(World.FOOTHILL);
        // Three sites: one holds 3 stations (3.5 stations' mean power), one 1 (1.5) and one none (0.5) - 4 slots in all.
        HydroSites.Site big = new HydroSites.Site(1000, 1000, 200, head, 3.5 * kw, 0);
        HydroSites.Site mid = new HydroSites.Site(2000, 2000, 60, head, 1.5 * kw, 0);
        HydroSites.Site thin = new HydroSites.Site(3000, 3000, 30, head, 0.5 * kw, 0);
        // The ground first: a land change works the sites out again (LandManager.landChanged()), so the sites go in after it.
        g.setCashForTest(Founding.WEALTHY_CASH);
        g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 80_000_000L);
        sites.setForTest(List.of(big, mid, thin));
        assertTrue("fixture: three sites, holding 3, 1 and 0 stations: " + big.slots(hydro.getProduction1()) + ", " + mid.slots(hydro.getProduction1()) + ", "
                + thin.slots(hydro.getProduction1()), big.slots(hydro.getProduction1()) == 3 && mid.slots(hydro.getProduction1()) == 1 && thin.slots(hydro.getProduction1()) == 0);
        assertTrue("the city's slots are the sum of each site's floor(units over a station's): 4", sites.slots(hydro.getProduction1()) == 4
                && g.hydroSlots(hydro) == 4 && g.hydroSlotsFree(hydro) == 4 && sites.sites().size() == 3 && sites.linked().size() == 3);
        assertTrue("...the card says so: 4 of 4 free on 3 sites, the best the widest river",
                BuildCard.hydro(g, hydro).free() == 4 && BuildCard.hydro(g, hydro).slots() == 4 && BuildCard.hydro(g, hydro).sites() == 3
                        && BuildCard.hydro(g, hydro).widthM() == big.widthM() && BuildCard.hydro(g, hydro).headM() == head);
        near("...and the best site is the one of the most mean power", sites.best().meanKw(), big.meanKw());
        BuildAdvice.Measure power = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);
        assertTrue("with a slot free the advice values it at what it makes: unit() is more than nothing",
                BuildAdvice.unit(g, power, hydro) > 0);

        int free = g.hydroSlotsFree(hydro);
        assertTrue("an order of one more than the slots free is refused NO_RIVER, before land and money, and takes none",
                quiet(() -> g.buildStack(hydro, free + 1, false)) == Game.BuildResult.NO_RIVER && g.hydroCommitted() == 0
                        && BuildCard.verdict(g, hydro, free + 1).kind() == BuildCard.VerdictKind.NO_RIVER);
        assertTrue("...an order of the slots free is not: its verdict is not NO_RIVER", BuildCard.verdict(g, hydro, free).kind() != BuildCard.VerdictKind.NO_RIVER);
        // A run: its orders count against the slots in turn - a second hydro station in the run has the first's ahead of it.
        BuildingsTemplate twin = new BuildingsTemplate("Hydro Station (a second kind, for the run)", BuildingType.ELECTRICITY).setHydro(true)
                .setCashCost(hydro.getCashCost()).setProduction1(hydro.getProduction1()).setLandSqFt(hydro.getLandSqFt());
        java.util.LinkedHashMap<BuildingsTemplate, Integer> fits = new java.util.LinkedHashMap<>();
        fits.put(hydro, free - 1);
        fits.put(twin, 1);
        assertTrue("a run of " + (free - 1) + " and then 1 more hydro station is the slots free: both orders pass",
                g.buildRunAhead(fits) == 2 && g.buildRunStop(fits) == Game.BuildResult.SUCCESS);
        java.util.LinkedHashMap<BuildingsTemplate, Integer> over = new java.util.LinkedHashMap<>();
        over.put(hydro, free - 1);
        over.put(twin, 2);
        assertTrue("...a run of " + (free - 1) + " and then 2 more is one over: the first passes, the second is NO_RIVER with the first ahead of it",
                g.buildRunAhead(over) == 1 && g.buildRunStop(over) == Game.BuildResult.NO_RIVER);
        java.util.LinkedHashMap<BuildingsTemplate, Integer> alone = new java.util.LinkedHashMap<>();
        alone.put(hydro, free + 1);
        assertTrue("...and a single order of one more than the slots stops the run at once, NO_RIVER", g.buildRunAhead(alone) == 0 && g.buildRunStop(alone) == Game.BuildResult.NO_RIVER);

        // The orders: 3 of the 4, then 2 more refused (one free), then 1, then none.
        assertTrue("orders up to the slots go ahead: 3 of 4", quiet(() -> g.buildStack(hydro, 3, false)) == Game.BuildResult.SUCCESS && g.hydroCommitted() == 3);
        assertTrue("...one free now", g.hydroSlotsFree(hydro) == 1 && g.hasRiverFor(hydro, 1) && !g.hasRiverFor(hydro, 2));
        assertTrue("...2 more is NO_RIVER, the stations on site counted", quiet(() -> g.buildStack(hydro, 2, false)) == Game.BuildResult.NO_RIVER && g.hydroCommitted() == 3);
        java.util.LinkedHashMap<BuildingsTemplate, Integer> next = new java.util.LinkedHashMap<>();
        next.put(hydro, 1);
        assertTrue("...a run of one passes, the card's verdict is not NO_RIVER", g.buildRunStop(next) == Game.BuildResult.SUCCESS
                && BuildCard.verdict(g, hydro, 1).kind() != BuildCard.VerdictKind.NO_RIVER);
        assertTrue("...the last one goes ahead", quiet(() -> g.buildStack(hydro, 1, false)) == Game.BuildResult.SUCCESS && g.hydroCommitted() == 4);
        assertTrue("...and the next is NO_RIVER: the slots are spent", quiet(() -> g.buildStack(hydro, 1, false)) == Game.BuildResult.NO_RIVER
                && g.hydroSlotsFree(hydro) == 0 && BuildCard.verdict(g, hydro, 1).kind() == BuildCard.VerdictKind.NO_RIVER);
        assertTrue("...the card's line says none free of the 4", BuildCard.hydro(g, hydro).free() == 0 && BuildCard.hydro(g, hydro).slots() == 4);
        assertTrue("...and the advice values it at nothing once no slot is free", BuildAdvice.unit(g, power, hydro) == 0);

        // A station standing is never stopped by a site lost: only new orders are held.
        sites.setForTest(List.of(thin));
        assertTrue("a site lost (the ground sold back) leaves the stations standing and on site: committed 4 of 0 slots, none free",
                g.hydroCommitted() == 4 && g.hydroSlots(hydro) == 0 && g.hydroSlotsFree(hydro) == 0);
        sites.setForTest(List.of(big, mid, thin));

        // The grid: each station standing adds its production1 to the generation.
        Game stand = LandClassCheck.city("hydrocheck-fiat", 1, 0, 0, 0);
        stand.getLandManager().getHydroSites().setForTest(List.of(big, mid, thin));
        stand.setCashForTest(Founding.WEALTHY_CASH);
        quietly(() -> stand.simulateMonths(1));
        UtilitiesHandler grid = stand.getServicesManager().getUtilitiesHandler();
        double before = grid.getBaseProduction();
        assertTrue("fixture: two stations stood at once on a scratch city with a river by fiat",
                quiet(() -> stand.buildStack(hydroOf(stand), 2, true)) == Game.BuildResult.SUCCESS && stand.getBuildingManager().getQuantity(hydroOf(stand).getId()) == 2);
        quietly(() -> stand.simulateMonths(1));
        near("each station standing adds its production1 to the grid's generation: two add twice", grid.getBaseProduction() - before, 2 * hydroOf(stand).getProduction1());

        // It stands on steep ground, where a coal plant is refused for want of ground it can stand on.
        Game steepCity = LandClassCheck.city("hydrocheck-steep", 0, 0, 1, 0);
        steepCity.getLandManager().getHydroSites().setForTest(List.of(big, mid, thin));
        steepCity.setCashForTest(Founding.WEALTHY_CASH);
        BuildingsTemplate steepHydro = hydroOf(steepCity), steepCoal = steepCity.getBuildingManager().getTemplateByName("Coal Power Plant");
        assertTrue("a city with only steep ground refuses a coal plant for the ground (it stands on flat and foothill)",
                quiet(() -> steepCity.buildStack(steepCoal, 1, false)) == Game.BuildResult.NO_LAND);
        assertTrue("...and takes a Hydro Station: a works that stands on any ground, steep too (Game.standsAnywhere())",
                quiet(() -> steepCity.buildStack(steepHydro, 1, false)) == Game.BuildResult.SUCCESS && steepCity.hydroCommitted() == 1
                        && Game.groundUse(steepHydro) == LandManager.Use.WORKS);

        // The advice: offered while a slot is free and its count held to the slots free - alone in the ranking, past them it offers none.
        adviceHeld();
    }

    /** The advice's count for a hydro station is held to the slots free, and it is a candidate only while one is free. */
    static void adviceHeld() {
        Game g = LandClassCheck.city("hydrocheck-advice", 1, 0, 0, 0);
        g.setCashForTest(Founding.WEALTHY_CASH);
        BuildingsTemplate hydro = hydroOf(g);
        double kw = stationKw(hydro);
        HydroSites sites = g.getLandManager().getHydroSites();
        HydroSites.Site river = new HydroSites.Site(1000, 1000, 200, riseOf(World.FOOTHILL), 2.5 * kw, 0);
        sites.setForTest(List.of(river));
        // A town that wants more power than two stations make: houses by the thousand (no ground asked), every other power plant out of the ranking.
        BuildingsTemplate house = g.getBuildingManager().getTemplateByName("House");
        quietly(() -> {
            g.getBuildingManager().addStack(house, 20_000, true);
            g.simulateMonths(2);
        });
        BuildAdvice.Measure power = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);
        UtilitiesHandler uh = g.getServicesManager().getUtilitiesHandler();
        java.util.Set<BuildingsTemplate> skip = new java.util.HashSet<>();
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) if (t.getCategory() == BuildingType.ELECTRICITY && !t.isHydro()) skip.add(t);
        int free = g.hydroSlotsFree(hydro);
        assertTrue("fixture: the town wants more power than it makes, by more than " + free + " stations' worth (" + (long) uh.getConsumption() + " against " + (long) uh.getBaseProduction() + ")",
                uh.getConsumption() - uh.getBaseProduction() > 0 && free == 2);
        BuildAdvice.Suggestion s = BuildAdvice.suggestFor(g, null, power, Founding.WEALTHY_CASH, 1e12, BuildAdvice.SLACK, skip);
        assertTrue("the advice offers a hydro station, the only power it may name, while a slot is free (" + free + " of the river's)",
                s != null && s.template() == hydro);
        assertTrue("...its count is held to the slots free: " + free + ", though the city needs more than that", s != null && s.count() == free);
        assertTrue("...and it does not say it keeps the need ahead, which the slots cannot", s != null && !s.closes());
        // The same town on a river that holds more: the count is what the need asks, and it closes the gap.
        int ample = 100;
        sites.setForTest(List.of(new HydroSites.Site(1000, 1000, 200, riseOf(World.FOOTHILL), (ample + 0.5) * kw, 0)));
        BuildAdvice.Suggestion plenty = BuildAdvice.suggestFor(g, null, power, Founding.WEALTHY_CASH, 1e12, BuildAdvice.SLACK, skip);
        assertTrue("on a river with " + ample + " slots the count is what the need asks, more than the " + free + " and no more than the slots (" + (plenty == null ? 0 : plenty.count()) + "), and it keeps the need ahead",
                plenty != null && plenty.template() == hydro && plenty.count() > free && plenty.count() <= g.hydroSlotsFree(hydro) && g.hydroSlotsFree(hydro) == ample && plenty.closes());
        // The notes' star 4, measured: with every power plant in the ranking and the river's slots ample, what the advice names.
        BuildAdvice.Suggestion open = BuildAdvice.suggestFor(g, null, power, Founding.WEALTHY_CASH, 1e12, BuildAdvice.SLACK, new java.util.HashSet<>());
        System.out.println("   with every power plant in the ranking and " + ample + " slots free, the advice names "
                + (open == null ? "nothing" : open.count() + " x " + open.template().getName()) + " (HY1 notes, star 4)");
        assertTrue("...with every power plant in the ranking it names one, hydro or not (the measure's premise)", open != null);
        // Past the slots: the same ranking has no hydro station to offer.
        sites.setForTest(List.of(river));
        quietly(() -> g.buildStack(hydro, free, true));
        BuildAdvice.Suggestion none = BuildAdvice.suggestFor(g, null, power, Founding.WEALTHY_CASH, 1e12, BuildAdvice.SLACK, skip);
        assertTrue("past the slots the advice offers no hydro station: none free, none named", g.hydroSlotsFree(hydro) == 0 && (none == null || none.template() != hydro));
    }

    /* =====================================================================
       5. THE EARTH
       ===================================================================== */

    record Place(String name, double lat, double lon, boolean hydro) { }

    static final Place[] PLACES = {
            new Place("Shawinigan QC", 46.556, -72.748, true),
            new Place("Niagara Falls ON", 43.08, -79.07, true),
            new Place("Revelstoke BC", 51.00, -118.19, true),
            new Place("Grand Coulee WA", 47.956, -118.98, true),
            new Place("Rotterdam", 51.92, 4.48, false),
            new Place("Winnipeg", 49.90, -97.14, false),
    };

    /** The half-side of the square a place is scanned in, in plots: 333 (10 km), a square of world 20 km a side. */
    static final long HALF = 333;

    static void theEarth() throws Exception {
        System.out.println("\n--- 5. the Earth: hydro towns and flat ones ---");
        Path p = EarthData.locate();
        String named = System.getProperty(EarthData.PROPERTY);
        if (named != null && !named.isBlank() && (p == null || !Files.isRegularFile(p))) {
            assertTrue("the file -D" + EarthData.PROPERTY + " names is there: " + named, false);
            return;
        }
        if (p == null) {
            System.out.println("earth-2.bin not named (-D" + EarthData.PROPERTY + "=<path>) nor at " + EarthData.DATA_FILE + ": the full file's sections were not run");
            return;
        }
        EarthSite.Data game = EarthSite.gameData();
        assertTrue("the game's Earth is here, the file named, EARTH_2: " + p, game.here() && game.reader().identity().equals(EarthData.EARTH_2) && game.reader().hasRelief());

        BuildingsTemplate hydro = null;
        {
            BuildingManager bm = new BuildingManager();
            bm.initializeTemplates();
            hydro = bm.getTemplateByName("Hydro Station");
        }
        double station = hydro.getProduction1();
        int hydroFound = 0, hydroTowns = 0;
        boolean flatsClear = true;
        for (Place pl : PLACES) {
            World.Key key = World.Key.earth(SEED, pl.lat(), pl.lon(), EarthData.EARTH_2);
            World w = World.of(key);
            double[] at = new double[2];
            if (!w.earthTerrain().projection().plotOf(pl.lat(), pl.lon(), at)) {
                assertTrue(pl.name() + ": the place projects onto the world", false);
                continue;
            }
            long cx = (long) Math.floor(at[0]), cy = (long) Math.floor(at[1]);
            long t0 = System.nanoTime();
            List<HydroSites.Site> found = HydroSites.sitesOnWorld(w, cx - HALF, cy - HALF, cx + HALF, cy + HALF);
            long ms = (System.nanoTime() - t0) / 1_000_000;
            int slots = 0;
            HydroSites.Site best = null;
            for (HydroSites.Site s : found) {
                slots += s.slots(station);
                if (best == null || s.meanKw() > best.meanKw()) best = s;
            }
            System.out.printf("   %-18s %s: %2d site(s), %2d slot(s) of %,.0f units a month%s  (%d ms)%n", pl.name(), pl.hydro() ? "hydro" : "flat ", found.size(), slots, station,
                    best == null ? "" : String.format("; the best %.0f m wide, %.0f m of head, %,.0f kW, landmass %d", best.widthM(), best.headM(), best.meanKw(), best.landmass()), ms);
            if (pl.hydro()) {
                hydroTowns++;
                if (!found.isEmpty()) hydroFound++;
            } else if (!found.isEmpty()) {
                flatsClear = false;
            }
            assertTrue(pl.name() + ": the same square read again gives the same sites",
                    found.equals(HydroSites.sitesOnWorld(w, cx - HALF, cy - HALF, cx + HALF, cy + HALF)));
        }
        assertTrue("the flat places, Rotterdam and Winnipeg, have no site in the 20 km round them", flatsClear);
        assertTrue("at least half of the hydro places have a site in the 20 km round them: " + hydroFound + " of " + hydroTowns, 2 * hydroFound >= hydroTowns);

        // A city founded at Shawinigan: the sites its founding ground owns are the world's, those whose dam it owns.
        Place shaw = PLACES[0];
        World.Key key = World.Key.earth(SEED, shaw.lat(), shaw.lon(), EarthData.EARTH_2);
        Founding f = Founding.defaults().withWorldKey(key);
        Game city = new Game(GameFiles.scratch("hydrocheck-shawinigan"));
        String[] threw = new String[1];
        long t0 = System.nanoTime();
        quietly(() -> {
            try {
                city.newGame(f);
            } catch (RuntimeException e) {
                threw[0] = e.toString();
            }
        });
        long ms = (System.nanoTime() - t0) / 1_000_000;
        assertTrue("a city founded at Shawinigan: " + (threw[0] == null ? "founded on the Earth (" + ms + " ms with the recount)" : threw[0]), threw[0] == null && city.getWorld().isEarth());
        if (threw[0] != null) return;
        HydroSites mine = city.getLandManager().getHydroSites();
        CityLand land = city.getCityLand();
        World w = city.getWorld();
        System.out.printf("   Shawinigan's founding ground owns %d hydro site(s), %d linked, %d slot(s); %d river segments read%n", mine.sites().size(), mine.linked().size(),
                mine.slots(station), mine.segmentsRead());
        for (HydroSites.Site s : mine.sites()) System.out.printf("      site at plot (%d, %d): %.0f m wide, %.0f m of head, %,.0f kW, landmass %d%n", s.x(), s.y(), s.widthM(), s.headM(), s.meanKw(), s.landmass());
        LandGrid grid = land.grid();
        List<HydroSites.Site> world = HydroSites.sitesOnWorld(w, grid.minX(), grid.minY(), grid.maxX(), grid.maxY());
        List<HydroSites.Site> owned = new ArrayList<>();
        for (HydroSites.Site s : world) if (land.ownsPlot(s.x(), s.y())) owned.add(s);
        assertTrue("the city's sites are the world's sites over its ground whose dams it owns, exactly (" + owned.size() + " of the world's " + world.size() + " in its bounds)",
                owned.equals(mine.sites()));
        assertTrue("...and every one is on a landmass it has linked, the founding one", mine.linked().equals(mine.sites()));

        // The ground bought round the nearest world site that holds a station: the city then owns the site, on its home landmass,
        // and an order for a Hydro Station goes ahead up to its slots and not past them.
        long cx = land.siteX(), cy = land.siteY();
        HydroSites.Site target = null;
        for (HydroSites.Site s : HydroSites.sitesOnWorld(w, cx - 1500, cy - 1500, cx + 1500, cy + 1500)) {
            if (s.slots(station) < 1) continue;
            if (target == null || Math.hypot(s.x() - cx, s.y() - cy) < Math.hypot(target.x() - cx, target.y() - cy)) target = s;
        }
        if (target == null) {
            System.out.println("   no world site that holds a station within 45 km of Shawinigan's centre: the purchase was not tried");
            return;
        }
        long half = HydroSites.CELL / 4;
        final HydroSites.Site site = target;
        long t1 = System.nanoTime();
        quietly(() -> city.getLandManager().buyRectForTest(site.x() - half, site.y() - half, site.x() + half, site.y() + half, city.getMonth()));
        long ms2 = (System.nanoTime() - t1) / 1_000_000;
        System.out.printf("   bought %d x %d plots round the site at (%d, %d), %.1f km from the centre: %d owned site(s), %d slot(s), the recount in %d ms%n", 2 * half, 2 * half,
                site.x(), site.y(), Math.hypot(site.x() - cx, site.y() - cy) * World.PLOT_M / 1000, mine.sites().size(), mine.slots(station), ms2);
        assertTrue("the ground bought round a world site makes it the city's, with the figures the world gives it", mine.sites().contains(site));
        assertTrue("...on the city's own landmass, so linked: the stations of its slots can be ordered", mine.linked().contains(site) && mine.slots(station) >= site.slots(station)
                && city.getLandManager().getLandmasses().linked(site.landmass()));
        BuildingsTemplate shawHydro = hydroOf(city);
        city.setCashForTest(Founding.WEALTHY_CASH);
        int free = city.hydroSlotsFree(shawHydro);
        assertTrue("...an order for one more station than the slots free is refused NO_RIVER; one for the slots free is not (" + free + " free)",
                free >= 1 && quiet(() -> city.buildStack(shawHydro, free + 1, false)) == Game.BuildResult.NO_RIVER
                        && BuildCard.verdict(city, shawHydro, free).kind() != BuildCard.VerdictKind.NO_RIVER);
        assertTrue("...and one station goes ahead on the Earth's ground", quiet(() -> city.buildStack(shawHydro, 1, false)) == Game.BuildResult.SUCCESS
                && city.hydroCommitted() == 1 && city.hydroSlotsFree(shawHydro) == free - 1);
    }
}

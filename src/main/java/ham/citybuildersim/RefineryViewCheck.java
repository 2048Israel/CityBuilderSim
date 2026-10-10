package ham.citybuildersim;

import ham.citybuildersim.sectors.Oil;
import ham.citybuildersim.sectors.Rail;
import ham.citybuildersim.sectors.RefineryFlow;
import ham.citybuildersim.sectors.RefineryFlow.Kind;
import ham.citybuildersim.sectors.RefineryFlow.Stream;
import ham.citybuildersim.sectors.Refining;
import ham.citybuildersim.sectors.Retail;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The refinery's pictogram (0.7.95, batch O11; runs/spec-oil.md 2.12):
 * RefineryView's figures and its picture, held to the model's own reads in
 * played towns.
 *
 * WHY. Refining's Operations page draws the refinery as a picture since
 * 0.7.95 - crude in, the column's cuts, the units, the tank by product, who
 * took each - every ribbon to scale. A ribbon that did not foot to the flow,
 * a product band that was not what the month made, a taker the market never
 * sold to, or a ribbon drawn out of scale would be a confident wrong picture.
 * The screen is checked by eye on the PC; this holds what it draws.
 *
 * What this has to prove:
 *   1. THE FLOW, TRACED, FOOTS: on every grade and on mixes, with no unit and
 *      with a whole campus, the links into each unit are its run, into each
 *      product the flow's make (bitumen and coke at the residue's litres a
 *      tonne), out of each cut the cut, and to the furnaces the residue
 *      burned - each within a billionth; and a unit feeds another (cracked
 *      gas to alkylation, naphtha to the reformers) only where the flow's
 *      pools carry it.
 *   2. THE MONTH IS THE ONE THE PRODUCTS WERE MADE ON: the view's flow is the
 *      one the production rows read (their capacity, to the bit), and each
 *      product's run less what idled is its row's made.
 *   3. THE CRUDE: the run is the crude units' nameplate at the month's rate;
 *      its sources' tonnes are the run; what each was bought is crude's
 *      clearing's, and they come to the refiners' row; the wells standing are
 *      counted; the column's cuts are the run's litres.
 *   4. THE TAKERS ARE THE BUYERS' OWN ROWS: what the refinery sold at home is
 *      every buyer's purchase from it, the railway's haul apart; the cars are
 *      the forecourts' month; the railway's diesel is its haul's tonnes at
 *      FUEL_LITRES_PER_TONNE; and the tanks take what was made and not taken,
 *      which is what the refiners' stock moved by over the month.
 *   5. THE PICTURE IS TO SCALE: every ribbon leaves one node's right edge and
 *      reaches another's left, inside each; every node's ribbons foot to its
 *      litres times the scale - a unit's in its run, a product's band its
 *      make, a taker's bar what it took; everything lies in the picture, and
 *      its tallest column fills it; the takers' labels stand LABEL_SPACING
 *      apart where there is room.
 *   6. THE FURNACES: on heavy crude with no unit, the residue the diesel
 *      cannot cut is burned and drawn to the furnaces; on medium none is.
 *   7. NOTHING TO DRAW: a town with no crude unit, and a city just loaded,
 *      say why and draw nothing; a month on, the loaded city's view is its
 *      twin's.
 *   8. PURE: read twice, the same view, and reading it makes no production
 *      row.
 *
 * Every fixture causes its condition.
 */
public class RefineryViewCheck {

    static int fails = 0;
    static PrintStream out = System.out;
    static PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-100s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-100s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    /** Within a billionth of the larger, or of one. */
    static boolean near(double a, double b) {
        return Math.abs(a - b) <= 1e-9 * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }

    /** ...and a picture's pixels: within a millionth of one. */
    static final double PX = 1e-6;

    static final Map<Game, GameFiles> FILES = new java.util.IdentityHashMap<>();

    /** The units of the campus town: one small of each kind but the asphalt unit (heavy crude's), so the cracking unit and the coker feed alkylation and the hydrocracker and the coker the reformer. */
    static final String[] CAMPUS = { "Small Reformer", "Small Cracking Unit", "Small Hydrocracker", "Small Alkylation Unit",
            "Small Lube Plant", "Small Coker" };

    /** Land wells on fiat sites in the campus town - fewer than its refinery runs on, so it imports the rest - and the oil each site holds. */
    static final int WELLS = 2;
    static final double SITE_TONNES = 5_000_000;

    /** Months the towns play before they are read: two years, so the households own cars and drive (OilCheck 4's). */
    static final int MONTHS = 24;

    /**
     * OilCheck's town (houses, shops, bakeries, power, water, roads and builders),
     * with a rail spur and two filling stations, an Oil Refinery and `units`,
     * and `wells` land wells on fiat sites; the refiners held, so what stands
     * is the fixture's. Put up finished out of a Wealthy treasury.
     */
    static Game town(String label, int wells, String... units) {
        GameFiles files = GameFiles.scratch(label);
        Game g = new Game(files);
        FILES.put(g, files);
        quietly(() -> {
            g.newGame();
            g.setCashForTest(Founding.WEALTHY_CASH);
            BuildingManager b = g.getBuildingManager();
            String[] names = { "House", "Convenience Store", "Small Grocery Store", "Industrial Bakery", "Paved Road",
                    "Coal Power Plant", "Water Treatment Plant", "Construction Depot", "Rail Spur", "Filling Station",
                    "Oil Refinery" };
            int[] counts = { 600, 13, 4, 4, 40, 2, 1, 4, 1, 2, 1 };
            double ground = 0;
            for (int i = 0; i < names.length; i++) ground += b.getTemplateByName(names[i]).getLandSqFt() * counts[i];
            for (String u : units) ground += b.getTemplateByName(u).getLandSqFt();
            BuildingsTemplate well = b.getTemplateByName("Oil Well");
            ground += wells * well.getLandSqFt();
            g.getLandManager().setOwnedSqFt(LandManager.STARTING_SQ_FT + ground * 1.25);
            g.getBusinessInvestment().holdSector(Sectors.REFINING);
            for (int i = 0; i < names.length; i++) g.buildStack(b.getTemplateByName(names[i]), counts[i], true);
            for (String u : units) g.buildStack(b.getTemplateByName(u), 1, true);
            if (wells > 0) {
                g.getLandManager().restoreSites(Resource.OIL, wells, SITE_TONNES);
                g.buildStack(well, wells, true);
            }
        });
        return g;
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);

        theTrace();

        Game g = town("refineryviewcheck", WELLS, CAMPUS);
        quietly(() -> g.simulateMonths(MONTHS - 1));
        Refining r = g.getSectors().refining();
        double[] stockBefore = new double[Good.values().length];
        for (Good p : RefineryView.TANK_ORDER) stockBefore[p.ordinal()] = r.getStock(p);
        quietly(() -> g.simulateMonths(1));
        RefineryView.View v = RefineryView.of(g);
        printTown(g, v);
        theMonth(g, v);
        theCrude(g, v);
        theTakers(g, v, stockBefore);
        theScale(v, v.picture(1200, RefineryView.HEIGHT), "the campus town at the 1,389 window's 1,200 px");
        theScale(v, v.picture(RefineryView.LEAST_WIDTH, RefineryView.HEIGHT), "...and at the least it is laid out at");
        theFurnaces(v);
        nothingToDraw(g, v);
        pure(g);

        for (GameFiles f : FILES.values()) LongPlaytest.cleanUp(f.getDirectory().getParent());
        out.println();
        if (fails == 0) {
            out.println("All checks passed.");
        } else {
            out.println(fails + " FAILED");
            System.exit(fails);
        }
    }

    static void printTown(Game g, RefineryView.View v) {
        out.printf("%nThe campus town at month %,d: %,d people; %d crude unit(s) at %.1f%%, %s of %s; %d kinds of unit,"
                        + " %d products, %d takers, %d links%n", g.getMonth(), g.getPopulationManager().getPopulation(),
                v.crudeUnits(), 100 * v.rate(), RefineryView.litres(v.runLitres()), RefineryView.gradeWords(v.mix()),
                v.units().size(), v.products().size(), v.buyers().size(), v.links().size());
    }

    /* ============================ 1. THE TRACE ============================ */

    /** Each kind's feed: its small and its large unit, `n` of each, from the catalogue (RefineryCheck's campus). */
    static double[] campus(BuildingManager b, int n) {
        double[] feed = new double[Kind.values().length];
        for (BuildingsTemplate t : b.getTemplates()) if (t.refineryUnit() != null) feed[t.refineryUnit().ordinal()] += n * t.feedPerMonth();
        return feed;
    }

    /** Whether the links of `f` traced at `rate` foot to it: into each unit its run, into each product its make, out of each cut the cut, to the furnaces the burned. */
    static String foots(RefineryFlow.Flow f, double rate) {
        List<RefineryView.Link> links = RefineryView.trace(f, rate);
        double[] into = new double[Kind.values().length], outOf = new double[Stream.values().length];
        Map<Good, Double> made = new EnumMap<>(Good.class);
        double burned = 0;
        for (RefineryView.Link l : links) {
            if (l.to().unit() != null) into[l.to().unit().ordinal()] += l.litres();
            if (l.to().product() != null) made.merge(l.to().product(), l.litres(), Double::sum);
            if (l.to().furnaces()) burned += l.litres();
            if (l.from().cut() != null) outOf[l.from().cut().ordinal()] += l.litres();
        }
        for (Kind k : Kind.values()) if (!near(into[k.ordinal()], f.run(k) * rate)) return "into " + k + " " + into[k.ordinal()] + " against " + f.run(k) * rate;
        for (Good g : RefineryView.TANK_ORDER) {
            double want = RefineryView.litresOf(g, f.of(g)) * rate;
            if (!near(made.getOrDefault(g, 0.0), want)) return "into " + g + " " + made.getOrDefault(g, 0.0) + " against " + want;
        }
        for (Stream s : RefineryView.COLUMN) {
            double cut = (f.cut(s) + (s == Stream.RESIDUE ? f.cut(Stream.HEAVY_RESIDUE) : 0)) * rate;
            if (!near(outOf[s.ordinal()], cut)) return "out of " + s + " " + outOf[s.ordinal()] + " against " + cut;
        }
        if (!near(burned, f.burned() * rate)) return "burned " + burned + " against " + f.burned() * rate;
        return null;
    }

    static void theTrace() {
        out.println("--- 1. the flow, traced, foots to the flow ---");
        BuildingManager catalogue = new BuildingManager();
        catalogue.initializeBuiltInTemplates();
        double[] values = RefineryFlow.values(g -> (g.worldImportPrice() + g.worldExportPrice()) / 2);
        double[][] mixes = { Refining.mixOf(Deposit.Grade.LIGHT), Refining.MEDIUM_MIX, Refining.mixOf(Deposit.Grade.HEAVY),
                { .2, .5, .3 }, { .6, 0, .4 } };
        String[] names = { "light", "medium", "heavy", "a blend 20/50/30", "light and heavy 60/40" };
        double[][] feeds = { new double[Kind.values().length], campus(catalogue, 1), campus(catalogue, 3) };
        String[] campuses = { "no unit", "a small and a large of each kind", "three of each" };
        int cases = 0;
        String first = null;
        for (int m = 0; m < mixes.length; m++) {
            for (int c = 0; c < feeds.length; c++) {
                for (double crude : new double[] { 8_300, 415_000 }) {
                    for (double rate : new double[] { 1, .37 }) {
                        RefineryFlow.Flow f = RefineryFlow.solve(feeds[c], crude, mixes[m], values);
                        String why = foots(f, rate);
                        cases++;
                        if (why != null && first == null) first = names[m] + ", " + campuses[c] + ", " + crude + " t at " + rate + ": " + why;
                    }
                }
            }
        }
        RefineryFlow.Flow all = RefineryFlow.solve(feeds[1], 415_000, Refining.mixOf(Deposit.Grade.HEAVY), values);
        int ran = 0;
        for (Kind k : Kind.values()) if (all.run(k) > 0) ran++;
        report("fixture: a large crude unit's heavy crude through a small and a large of each kind, its units taking feed",
                ran >= 6 && all.burned() >= 0, ran + " of 7 kinds take feed");
        report("into each unit its run, into each product its make, out of each cut the cut, the furnaces the burned"
                + " (a billionth)", first == null, first == null ? cases + " flows: 5 mixes x 3 campuses x 2 crudes x 2 rates" : first);

        // A campus on medium crude: the cracking unit's cracked gas reaches alkylation, the hydrocracker's naphtha the reformers.
        RefineryFlow.Flow f = RefineryFlow.solve(campus(catalogue, 1), 415_000, Refining.MEDIUM_MIX, values);
        List<RefineryView.Link> links = RefineryView.trace(f, 1);
        double crackToAlky = between(links, Kind.CRACKER, Kind.ALKYLATION), hydroToReformer = between(links, Kind.HYDROCRACKER, Kind.REFORMER);
        report("a cracking unit's cracked gas feeds alkylation, a hydrocracker's naphtha the reformers - links from unit to unit",
                crackToAlky > 0 && hydroToReformer > 0 && f.run(Kind.ALKYLATION) > 0 && f.run(Kind.REFORMER) > 0,
                String.format("%s and %s", RefineryView.litres(crackToAlky), RefineryView.litres(hydroToReformer)));
        // ...and only there: no link from unit to unit with no unit downstream.
        boolean none = true;
        for (RefineryView.Link l : RefineryView.trace(RefineryFlow.solve(only(Kind.CRACKER, 2e6), 8_300, Refining.MEDIUM_MIX, values), 1)) {
            none &= l.to().unit() == null || l.from().unit() == null;
        }
        assertTrue("...and a cracking unit alone links to no unit: its cracked gas goes to petroleum gas", none);
    }

    static double between(List<RefineryView.Link> links, Kind from, Kind to) {
        double t = 0;
        for (RefineryView.Link l : links) if (l.from().unit() == from && l.to().unit() == to) t += l.litres();
        return t;
    }

    static double[] only(Kind k, double litres) {
        double[] feed = new double[Kind.values().length];
        feed[k.ordinal()] = litres;
        return feed;
    }

    /* ============================ 2. THE MONTH ============================ */

    static void theMonth(Game g, RefineryView.View v) {
        out.println("\n--- 2. the month is the one the products were made on ---");
        Refining r = g.getSectors().refining();
        StringBuilder running = new StringBuilder();
        for (RefineryView.Unit u : v.units()) if (u.run() > 0) running.append(running.length() == 0 ? "" : ", ").append(u.kind().unitName());
        report("fixture: the campus town's refinery ran this month, units with it", v.drawn() && running.length() > 0,
                running + " took feed");
        assertTrue("the view's flow is the one the month's products were made on (Refining.monthsFlow())", v.flow() == r.monthsFlow());
        boolean capacity = true, made = true;
        String worst = "";
        for (Good p : RefineryView.TANK_ORDER) {
            Sector.Output o = r.outputRow(p);
            if (o == null) continue;
            capacity &= o.capacity == v.flow().of(p);
            RefineryView.Product vp = v.product(p);
            double runs = v.flow().of(p) * v.rate();
            // A product the month neither made nor moved has a row of nothing, and no place in the view.
            boolean ok = vp == null ? o.produced + o.exportBound == 0 && !(runs > 0)
                    : vp.made() == o.produced + o.exportBound && near(vp.run(), runs) && near(vp.made() + vp.idled(), runs);
            if (!ok && worst.isEmpty()) worst = p.label();
            made &= ok;
        }
        assertTrue("each product's nameplate row is the view's flow, to the bit", capacity);
        report("each product's made is its row's, and its run - the flow at the month's rate - is made and idled", made,
                worst.isEmpty() ? String.format("petrol %s made of %s", RefineryView.figure(Good.PETROL, v.product(Good.PETROL).made()),
                        RefineryView.figure(Good.PETROL, v.product(Good.PETROL).run())) : worst);
        boolean spreads = true;
        for (RefineryView.Unit u : v.units()) {
            spreads &= u.spread() == v.flow().spread(u.kind()) && u.citySpread() == RefineryFlow.spread(u.kind(), r.cityValues())
                    && u.run() == v.flow().run(u.kind()) * v.rate();
        }
        assertTrue("each unit's run and spread are the flow's, its city spread the planner's (cityValues())", spreads);
        boolean gates = true;
        for (RefineryView.Unit u : v.units()) {
            gates &= u.gate() != null && u.gate().template().refineryUnit() == u.kind();
            if (u.gate() != null) gates &= same(r.appraise(u.gate().template(), g.getBusinessInvestment()), u.gate());
        }
        assertTrue("...and its gate is the planner's own appraisal of one more of its kind", gates);
    }

    /** Whether two appraisals of a building are the same: its template, earnings, cost and first refusal. */
    static boolean same(ham.citybuildersim.sectors.SpreadPlanner.Candidate a, ham.citybuildersim.sectors.SpreadPlanner.Candidate b) {
        return a.template() == b.template() && a.earns() == b.earns() && a.cost() == b.cost() && a.failed() == b.failed()
                && java.util.Objects.equals(a.why(), b.why());
    }

    /* ============================ 3. THE CRUDE ============================ */

    static void theCrude(Game g, RefineryView.View v) {
        out.println("\n--- 3. the crude: the run, its sources, the column ---");
        Refining r = g.getSectors().refining();
        Oil oil = g.getSectors().oil();
        double nameplate = r.getInputAtCapacity(Good.CRUDE);
        report("the run is the crude units' nameplate at the month's rate", near(v.runTonnes(), nameplate * r.monthsRate())
                        && v.rate() == r.monthsRate(),
                String.format("%s of %s, %.1f%%", RefineryView.tonnes(v.runTonnes()), RefineryView.tonnes(nameplate), 100 * v.rate()));
        double credited = 0, bought = 0;
        RefineryView.Source wells = null, world = null;
        for (RefineryView.Source s : v.sources()) {
            credited += s.tonnes();
            bought += s.bought();
            if (s.kind() == RefineryView.SourceKind.LAND_WELLS) wells = s;
            if (s.kind() == RefineryView.SourceKind.IMPORTED) world = s;
        }
        report("fixture: the town's wells lift less than the refinery runs, so it imports the rest", wells != null && world != null,
                String.format("%s from the wells, %s imported", wells == null ? "none" : RefineryView.tonnes(wells.bought()),
                        world == null ? "none" : RefineryView.tonnes(world.bought())));
        assertTrue("the sources' tonnes are the run", near(credited, v.runTonnes()));
        Sector.Input in = r.inputRow(Good.CRUDE);
        double clearing = 0;
        for (Trade t : g.getMarkets().get(Good.CRUDE).getTrades()) if (Sectors.REFINING.equals(t.buyer())) clearing += t.units();
        report("what each was bought is crude's clearing's, and they come to the refiners' row, home and imported",
                near(bought, clearing) && near(bought, in.boughtLocal + in.imported) && wells != null && near(wells.bought(), in.boughtLocal)
                        && world != null && near(world.bought(), in.imported),
                String.format("%s bought", RefineryView.tonnes(bought)));
        assertTrue("the land wells standing are counted", wells != null && wells.count() == oil.landWellsStanding() && wells.count() == WELLS);
        double cuts = 0;
        for (Stream s : RefineryView.COLUMN) cuts += v.cut(s);
        report("the column's cuts are the run's litres", near(cuts, v.runLitres()), RefineryView.litres(cuts));
        assertTrue("...cut at the mix the month ran on (Refining.monthsMix())", java.util.Arrays.equals(v.mix(), r.monthsMix()));
    }

    /* ============================ 4. THE TAKERS ============================ */

    static void theTakers(Game g, RefineryView.View v, double[] stockBefore) {
        out.println("\n--- 4. the takers are the buyers' own rows ---");
        Refining r = g.getSectors().refining();
        Rail rail = g.getSectors().rail();
        Retail shops = g.getSectors().retail();
        double railLocal = rail.getFuelLitres() - rail.getFuelLitresImported();
        boolean sold = true;
        String detail = "";
        for (Good p : RefineryView.TANK_ORDER) {
            Sector.Output o = r.outputRow(p);
            double rowSold = o == null ? 0 : o.soldLocal;
            double viewLocal = 0;
            for (RefineryView.Buyer b : v.buyers()) {
                if (b.kind() == RefineryView.BuyerKind.TANKS || b.kind() == RefineryView.BuyerKind.IDLED
                        || b.kind() == RefineryView.BuyerKind.ABROAD) continue;
                viewLocal += b.local().getOrDefault(p, 0.0);
            }
            double want = rowSold + (p == Good.DIESEL ? railLocal : 0);
            sold &= near(viewLocal, want);
            if (p == Good.DIESEL) detail = String.format("diesel: %s to the vans and the railway, %s of it the railway's haul",
                    RefineryView.litres(viewLocal), RefineryView.litres(railLocal));
        }
        report("what the refinery sold at home, product by product, is its takers' - its own vans' among them, the railway's"
                + " haul apart", sold, detail);
        RefineryView.Buyer cars = buyer(v, RefineryView.BuyerKind.CARS), railway = buyer(v, RefineryView.BuyerKind.RAIL);
        report("fixture: the households drive and the railway hauls", cars != null && railway != null,
                String.format("cars %s, railway %s", cars == null ? "none" : RefineryView.buyerFigure(cars),
                        railway == null ? "none" : RefineryView.buyerFigure(railway)));
        assertTrue("the cars' petrol is the forecourts' month (Retail's pump and queue)", cars != null
                && near(cars.of(Good.PETROL), shops.getPumpLitres() + shops.getQueueLitres()));
        double haul = rail.getHauledTonnes() * Rail.FUEL_LITRES_PER_TONNE;
        Sector.Input fleet = rail.inputRow(Good.DIESEL);
        double railFleet = fleet == null ? 0 : fleet.boughtLocal + fleet.imported;
        report("the railway's diesel is its haul's tonnes at FUEL_LITRES_PER_TONNE (and its own vans')", railway != null && haul > 0
                        && near(rail.getFuelLitres(), haul) && near(railway.of(Good.DIESEL), haul + railFleet),
                String.format("%,.0f t hauled, %s", rail.getHauledTonnes(), RefineryView.litres(haul)));
        boolean tanks = true;
        String moved = "";
        double most = -1;
        for (RefineryView.Product p : v.products()) {
            Sector.Output o = r.outputRow(p.good());
            double lost = o == null ? 0 : o.writtenOff;
            double delta = r.getStock(p.good()) - stockBefore[p.good().ordinal()];
            tanks &= Math.abs(delta - (p.tanks() - lost)) <= 1e-6 * Math.max(1, Math.abs(p.made()) + Math.abs(p.local()) + Math.abs(p.exported()));
            if (Math.abs(delta) > most) {
                most = Math.abs(delta);
                moved = String.format("%s %s, the stock %s", p.good().label().toLowerCase(), RefineryView.figure(p.good(), p.tanks()),
                        RefineryView.figure(p.good(), delta));
            }
        }
        report("the tanks take what was made and not taken: what the refiners' stock moved by over the month", tanks, moved);
        double takers = 0, products = 0;
        for (RefineryView.Buyer b : v.buyers()) takers += b.litres();
        for (RefineryView.Product p : v.products()) {
            products += RefineryView.litresOf(p.good(), p.local() + p.imported() + p.exported() + p.intoTanks() + p.idled());
        }
        assertTrue("every litre a taker took is a product's: taken here, imported, shipped, into the tanks or idled", near(takers, products));
    }

    static RefineryView.Buyer buyer(RefineryView.View v, RefineryView.BuyerKind k) {
        for (RefineryView.Buyer b : v.buyers()) if (b.kind() == k) return b;
        return null;
    }

    /* ============================ 5. THE SCALE ============================ */

    static void theScale(RefineryView.View v, RefineryView.Picture p, String which) {
        out.println("\n--- 5. the picture is to scale: " + which + " ---");
        double k = p.scale();
        // Every ribbon from one node's right edge to another's left, inside each.
        Map<String, double[]> sides = new java.util.LinkedHashMap<>();
        int attached = 0;
        String loose = null;
        for (RefineryView.Ribbon r : p.ribbons()) {
            if (!(r.height() > PX)) { attached++; continue; }
            RefineryView.Place from = null, to = null;
            for (RefineryView.Place q : p.places()) {
                if (Math.abs(q.right() - r.x1()) < PX && r.a0() >= q.y() - PX && r.a1() <= q.y() + q.h() + PX) from = q;
                if (Math.abs(q.x() - r.x2()) < PX && r.b0() >= q.y() - PX && r.b1() <= q.y() + q.h() + PX) to = q;
            }
            // The crude reaches the column whole, across its cuts.
            if (from != null && from.key().startsWith("source:")) to = new RefineryView.Place("column", r.x2(), 0, 0, 0);
            if (from == null || to == null) {
                if (loose == null) loose = String.format("%.1f,%.1f-%.1f to %.1f,%.1f-%.1f", r.x1(), r.a0(), r.a1(), r.x2(), r.b0(), r.b1());
                continue;
            }
            attached++;
            sides.computeIfAbsent(from.key(), x -> new double[2])[1] += r.height();
            sides.computeIfAbsent(to.key(), x -> new double[2])[0] += r.height();
        }
        report("every ribbon leaves one node's right edge and reaches another's left, inside each", loose == null,
                loose == null ? attached + " ribbons" : loose);

        // Each node's ribbons foot to its litres times the scale.
        List<String> wrong = new ArrayList<>();
        double run = v.runLitres();
        check(wrong, sides, "column", 0, run * k);
        for (RefineryView.Source s : v.sources()) check(wrong, sides, "source:" + s.kind(), 1, s.litres() * k);
        for (Stream s : RefineryView.COLUMN) check(wrong, sides, "cut:" + s, 1, v.cut(s) * k);
        for (RefineryView.Unit u : v.units()) if (u.standing() > 0) check(wrong, sides, "unit:" + u.kind(), 0, u.run() * k);
        for (RefineryView.Product q : v.products()) {
            double make = RefineryView.litresOf(q.good(), q.run()) * k;
            check(wrong, sides, "made:" + q.good(), 0, make);
            check(wrong, sides, "made:" + q.good(), 1, make);
            check(wrong, sides, "stored:" + q.good(), 1, RefineryView.litresOf(q.good(), q.fromTanks()) * k);
            check(wrong, sides, "landed:" + q.good(), 1, RefineryView.litresOf(q.good(), q.imported()) * k);
        }
        for (int i = 0; i < v.buyers().size(); i++) check(wrong, sides, "taker:" + i, 0, v.buyers().get(i).litres() * k);
        check(wrong, sides, "furnaces", 0, v.burned() * k);
        for (RefineryView.Place q : p.places()) {
            if (q.key().startsWith("band:")) {
                double[] s = sides.getOrDefault(q.key(), new double[2]);
                if (Math.abs(s[0] - q.h()) > PX || Math.abs(s[1] - q.h()) > PX) wrong.add(q.key());
            }
            if (q.key().startsWith("unit:") || q.key().startsWith("made:") || q.key().startsWith("taker:")
                    || q.key().startsWith("source:") || q.key().startsWith("cut:")) {
                double[] s = sides.getOrDefault(q.key(), new double[2]);
                if (Math.max(s[0], s[1]) > q.h() + PX) wrong.add(q.key() + " taller than its node");
            }
        }
        report("each node's ribbons foot to its litres times the scale: a unit's in its run, a product's band its make,"
                + " a taker's bar what it took", wrong.isEmpty(), wrong.isEmpty() ? RefineryView.scaleWords(k) : wrong.toString());

        // Everything inside the picture, and the tallest column fills it.
        double lo = Double.MAX_VALUE, hi = -Double.MAX_VALUE, left = Double.MAX_VALUE, right = -Double.MAX_VALUE;
        for (RefineryView.Ribbon r : p.ribbons()) {
            lo = Math.min(lo, Math.min(r.a0(), r.b0()));
            hi = Math.max(hi, Math.max(r.a1(), r.b1()));
            left = Math.min(left, r.x1());
            right = Math.max(right, r.x2());
        }
        for (RefineryView.Box b : p.boxes()) {
            lo = Math.min(lo, b.y());
            hi = Math.max(hi, b.y() + b.h());
            left = Math.min(left, b.x());
            right = Math.max(right, b.x() + b.w());
        }
        for (RefineryView.Words w : p.words()) {
            lo = Math.min(lo, w.y() - w.size());
            hi = Math.max(hi, w.y() + 3);
        }
        report("every box, ribbon and line of words lies in the picture", lo >= -PX && hi <= p.height() + PX && left >= -PX
                && right <= p.width() + PX, String.format("y %.1f to %.1f of %.0f, x %.1f to %.1f of %.0f", lo, hi, p.height(), left,
                right, p.width()));
        double usable = p.height() - RefineryView.TOP - 2 * RefineryView.PAD, tallest = 0;
        for (String prefix : new String[] { "source:", "cut:", "unit:", "band:", "furnaces", "made:", "stored:", "landed:", "taker:" }) {
            double top = Double.MAX_VALUE, bottom = -Double.MAX_VALUE;
            for (RefineryView.Place q : p.places()) {
                if (!q.key().startsWith(prefix)) continue;
                top = Math.min(top, q.y());
                bottom = Math.max(bottom, q.y() + q.h());
            }
            if (bottom > top) tallest = Math.max(tallest, bottom - top);
        }
        report("...and the tallest column fills most of it: the scale is the largest at which every column fits",
                tallest >= .8 * usable && tallest <= usable + PX, String.format("%.1f of %.1f px", tallest, usable));

        // The takers' labels spread apart.
        List<Double> centres = new ArrayList<>();
        for (RefineryView.Words w : p.words()) {
            if (w.face() == RefineryView.Face.SANS_MEDIUM && w.size() == 11.5 && w.x() > p.width() / 2) centres.add(w.y() + 3);
        }
        boolean apart = true;
        boolean room = centres.size() * RefineryView.LABEL_SPACING <= p.height() - 42;
        for (int i = 1; i < centres.size(); i++) apart &= centres.get(i) - centres.get(i - 1) >= RefineryView.LABEL_SPACING - 1e-6;
        report("the takers' labels stand LABEL_SPACING apart, a line from each bar to its moved label", !room || apart,
                centres.size() + " labels");
    }

    static void check(List<String> wrong, Map<String, double[]> sides, String key, int side, double want) {
        double[] s = sides.getOrDefault(key, new double[2]);
        if (Math.abs(s[side] - want) > 1e-6 * Math.max(1, want)) wrong.add(String.format("%s %s %.4f px, wants %.4f", key,
                side == 0 ? "in" : "out", s[side], want));
    }

    /* ============================ 6. THE FURNACES ============================ */

    static void theFurnaces(RefineryView.View medium) {
        out.println("\n--- 6. the furnaces: residue the diesel cannot cut ---");
        assertTrue("on medium crude with a coker, none is burned and no furnaces are drawn", medium.burned() == 0
                && medium.picture(1200, RefineryView.HEIGHT).place("furnaces") == null);
        Game g = town("refineryviewcheck-heavy", 0);
        quietly(() -> {
            g.simulateMonths(2);
            g.getSectors().refining().setCrudeMixForTest(Refining.mixOf(Deposit.Grade.HEAVY));
            g.simulateMonths(1);
        });
        RefineryView.View v = RefineryView.of(g);
        RefineryView.Picture p = v.picture(1200, RefineryView.HEIGHT);
        RefineryView.Place furnaces = p.place("furnaces");
        report("on heavy crude with no unit the residue the diesel cannot cut is burned, the flow's to the litre, and drawn"
                        + " to the furnaces", v.burned() > 0 && near(v.burned(), v.flow().burned() * v.rate()) && furnaces != null
                        && Math.abs(furnaces.h() - v.burned() * p.scale()) < PX,
                String.format("%s burned of %s run, %s", RefineryView.litres(v.burned()), RefineryView.litres(v.runLitres()),
                        RefineryView.gradeWords(v.mix())));
        assertTrue("...and the column's diesel is all spent cutting it: none is left as diesel (to a billionth of the run)",
                v.product(Good.DIESEL) == null || v.product(Good.DIESEL).run() <= 1e-9 * v.runLitres());
        theScale(v, p, "the heavy town");
    }

    /* ============================ 7. NOTHING TO DRAW ============================ */

    static void nothingToDraw(Game g, RefineryView.View v) {
        out.println("\n--- 7. nothing to draw: no crude unit, and a city just loaded ---");
        GameFiles files = GameFiles.scratch("refineryviewcheck-bare");
        Game bare = new Game(files);
        FILES.put(bare, files);
        quietly(() -> {
            bare.newGame();
            bare.simulateMonths(1);
        });
        RefineryView.View none = RefineryView.of(bare);
        assertTrue("a town with no crude unit has nothing standing, draws nothing and says so", !none.standing() && !none.drawn()
                && none.picture(1200, RefineryView.HEIGHT).empty() && RefineryView.emptyWords(none).startsWith("No crude unit"));

        quietly(() -> g.saveGame(10, "refineryview"));
        Game twin = new Game(FILES.get(g));
        quietly(() -> twin.loadGameSave(10));
        RefineryView.View loaded = RefineryView.of(twin);
        assertTrue("a city just loaded is not counted, draws nothing and says a month has to run", loaded.standing() && !loaded.counted()
                && !loaded.drawn() && loaded.picture(1200, RefineryView.HEIGHT).empty()
                && RefineryView.emptyWords(loaded).startsWith("Not drawn until a month runs"));
        quietly(() -> {
            twin.getBusinessInvestment().holdSector(Sectors.REFINING);
            g.simulateMonths(1);
            twin.simulateMonths(1);
        });
        RefineryView.View a = RefineryView.of(g), b = RefineryView.of(twin);
        boolean same = a.counted() && b.counted() && a.rate() == b.rate() && a.flow().slate().equals(b.flow().slate())
                && a.products().size() == b.products().size();
        for (int i = 0; same && i < a.products().size(); i++) {
            same = a.products().get(i).made() == b.products().get(i).made() && a.products().get(i).run() == b.products().get(i).run();
        }
        for (Kind k : Kind.values()) same &= a.flow().run(k) == b.flow().run(k);
        assertTrue("...and a month on, the loaded city's month is its twin's: the flow, the rate, each product made", same);
    }

    /* ============================ 8. PURE ============================ */

    static void pure(Game g) {
        out.println("\n--- 8. pure ---");
        String a = words(RefineryView.of(g)), b = words(RefineryView.of(g));
        assertTrue("read twice, the same view, figure for figure and word for word", a.equals(b) && !a.isEmpty());
        Sector homes = g.getSectors().realEstate();
        boolean before = homes.inputRow(Good.PETROL) == null && homes.outputRow(Good.DIESEL) == null;
        RefineryView.of(g).picture(1200, RefineryView.HEIGHT);
        assertTrue("...and reading it makes no production row (asked of a sector that buys no product)", before
                && homes.inputRow(Good.PETROL) == null && homes.outputRow(Good.DIESEL) == null);
    }

    /** A view and its picture as text: every figure and every word. */
    static String words(RefineryView.View v) {
        StringBuilder s = new StringBuilder();
        s.append(v.rate()).append(v.runLitres()).append(java.util.Arrays.toString(v.mix())).append(v.burned());
        for (RefineryView.Source x : v.sources()) s.append(x.kind()).append(x.tonnes()).append(x.bought()).append(java.util.Arrays.toString(x.grades()));
        for (RefineryView.Unit u : v.units()) s.append(u.kind()).append(u.run()).append(u.spread()).append(u.citySpread())
                .append(u.gate() == null ? "" : u.gate().earns() + "" + u.gate().failed());
        for (RefineryView.Product p : v.products()) s.append(p);
        for (RefineryView.Buyer b : v.buyers()) s.append(b);
        for (RefineryView.Link l : v.links()) s.append(l);
        for (RefineryView.Words w : v.picture(1200, RefineryView.HEIGHT).words()) s.append(w.line()).append(w.x()).append(w.y());
        return s.toString();
    }
}

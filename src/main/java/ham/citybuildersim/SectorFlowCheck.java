package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

/**
 * The flow (0.7.30): SectorFlow's figures - what went into each business,
 * what its plant made of it and what held it back, and what came out - held
 * to the model's own reads in a played city.
 *
 * WHY. The Sectors screen's Operations page draws a business as inputs →
 * the plant → outputs since 0.7.30, with the money on every row and the
 * plant's six throttles as a cascade down to the rate it runs at. A row whose
 * money did not add up to the income statement's line, a cascade that ended
 * somewhere other than the operating rate, or a rate drawn over a sector
 * with nothing standing (the old page's "Running at 43%" in red over three
 * empty sectors) would be a confident wrong picture. The screen is checked
 * by eye; this holds what it is drawn from.
 *
 * What this has to prove:
 *   1. THE MONEY ADDS UP: each sector's inputs - goods and services - come to
 *      the statement's inputs line, and its outputs - goods and work billed -
 *      to its revenue, to the cent; and both are the SectorBooks month's.
 *   2. THE CASCADE IS THE RATE: the six throttles are the sector's own, each
 *      step is the product of those before it, and the last is
 *      getOperatingRate() to the bit.
 *   3. NOTHING STANDING HAS NO RATE: a sector with no building has a plant
 *      of none, no rate and no lowest throttle, and its operations page says
 *      "no plant standing" where it read a rate; the page's note names all
 *      six throttles and says they multiply.
 *   4. THE UNITS ARE THE PRODUCTION ROWS, read without creating any; a
 *      seller-priced good's capacity is the Build group's note.
 *   5. AFTER A LOAD the units are not counted until a month runs (the rows
 *      are not saved), and the money, which is, is the same money.
 *   6. THE PAGE IS THE BLOCK AND THE SECTOR'S OWN LINES: operations() is
 *      plantLines() then ownLines() for the sectors that keep the block, and
 *      ownLines() alone for the seven that replace it.
 *
 * Every fixture causes its condition.
 */
public class SectorFlowCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-100s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** Money is in thousands, so this is a cent. */
    static final double CENT = 1e-5;

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    /**
     * A played city with a bit of every chain in it - homes, shops and a
     * diner; bakeries; a mine, a foundry and a fabrication shop; builders;
     * power, water, roads and a rail spur, so the shippers pay a railway for
     * haulage, a service with no units - and NO office, so Business Services
     * has nothing standing. Put up finished, out of a Wealthy treasury, and
     * played five years.
     */
    static Game city(Path root) {
        // (no Iron Mine: a fresh city owns no deposit, and the foundry buys its ore)
        GameFiles files = new GameFiles(root.resolve("city"), root.resolve("city-no-legacy"));
        Game g = new Game(files);
        quietly(() -> {
            g.run();
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.getGovernmentInvestor().spend(-2_000_000);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 200_000_000L);
            for (String[] w : ORDERS) {
                BuildingsTemplate t = LongPlaytest.template(g, w[0]);
                built.put(w[0], t != null && g.buildStack(t, Integer.parseInt(w[1]), true) == Game.BuildResult.SUCCESS);
            }
            /*
             * ...AND NONE OF ITS OWN, EITHER (0.7.43): this city's offices'
             * planner left it officeless for five years until groceries were
             * sold at a price; since then it opens one in them. The section
             * needs a sector with nothing standing, so it is held.
             */
            g.getBusinessInvestment().holdSector(Sectors.BUSINESS_SERVICES);
            g.simulateMonths(60);
        });
        return g;
    }

    /** The fixture's orders, and whether each went on site. */
    static final String[][] ORDERS = {
            { "House", "400" }, { "Convenience Store", "12" }, { "Diner", "2" }, { "Construction Depot", "6" },
            { "Coal Power Plant", "2" }, { "Water Treatment Plant", "2" }, { "Industrial Bakery", "3" },
            { "Steel Foundry", "2" }, { "Fabrication Shop", "2" }, { "Commercial Bank", "1" },
            { "Elementary School", "3" }, { "Walk-in Clinic", "3" }, { "Paved Road", "20" }, { "Rail Spur", "1" } };
    /** ...whether each went on site, as city() found it. */
    static final java.util.Map<String, Boolean> built = new java.util.LinkedHashMap<>();

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        Path root = Files.createTempDirectory("sectorflowcheck");

        Game g = city(root);
        printCity(g);
        theMoney(g);
        theCascade(g);
        nothingStanding(g);
        theUnits(g);
        afterALoad(root, g);
        thePage(g);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** The city as the flow reads it, for the record. */
    static void printCity(Game g) {
        out.printf("%n      the city: month %d, %,d people; ordered %s%n", g.getMonth(),
                g.getPopulationManager().getPopulation(), built);
        for (Sector s : g.getSectors().all()) {
            SectorFlow.Flow f = SectorFlow.of(g, s);
            out.printf("      %-18s standing %3d · rate %s · in %d rows %s · out %d rows %s%n", s.label(),
                    f.plant().standing(), Double.isNaN(f.plant().rate()) ? "none" : String.format("%.3f", f.plant().rate()),
                    f.inputs().size(), Formats.INSTANCE.amount(f.inputsMoney()),
                    f.outputs().size(), Formats.INSTANCE.amount(f.revenueMoney()));
        }
    }

    /* ============================ 1. THE MONEY ============================ */

    static void theMoney(Game g) {
        out.println("\n--- 1. the money adds up: inputs to the inputs line, outputs to revenue, to the cent ---");
        boolean in = true, outs = true, books = true;
        int withInputs = 0, withServices = 0, withWork = 0;
        for (Sector s : g.getSectors().all()) {
            SectorFlow.Flow f = SectorFlow.of(g, s);
            SectorBooks.SectorMonth m = g.getSectorBooks().get(s);
            in &= Math.abs(f.inputsMoney() - s.statement().inputs) <= CENT;
            outs &= Math.abs(f.revenueMoney() - s.statement().revenue) <= CENT;
            books &= Math.abs(f.inputsMoney() - m.inputs()) <= CENT && Math.abs(f.revenueMoney() - m.revenue()) <= CENT;
            if (f.inputsMoney() > 0) withInputs++;
            for (SectorFlow.In i : f.inputs()) if (i.service()) { withServices++; break; }
            for (SectorFlow.Out o : f.outputs()) if (o.work()) { withWork++; break; }
        }
        assertTrue("fixture: the city's businesses bought something (" + withInputs + " sectors), a service among it ("
                + withServices + "), and work was billed (" + withWork + ")", withInputs > 3 && withServices > 0 && withWork > 0);
        assertTrue("every sector's inputs, goods and services, come to its statement's inputs line", in);
        assertTrue("...and its outputs, goods and work billed, to its revenue", outs);
        assertTrue("...and both are the SectorBooks month the Income page draws", books);
    }

    /* ============================ 2. THE CASCADE ============================ */

    static void theCascade(Game g) {
        out.println("\n--- 2. the cascade is the rate: the six throttles, multiplied in order, end at getOperatingRate() ---");
        boolean own = true, steps = true, rate = true;
        int standing = 0, below = 0;
        for (Sector s : g.getSectors().all()) {
            SectorFlow.Plant p = SectorFlow.of(g, s).plant();
            double[] r = p.ratios();
            own &= r.length == SectorFlow.THROTTLES.length && r[0] == s.getAverageFill() && r[1] == s.getEnergyRatio()
                    && r[2] == s.getWaterRatio() && r[3] == s.getRoadRatio() && r[4] == s.getHealthRatio()
                    && r[5] == s.getVanRatio();
            double run = r[0];
            steps &= p.cascade()[0] == run;
            for (int i = 1; i < r.length; i++) {
                run = run * r[i];
                steps &= p.cascade()[i] == run;
            }
            if (p.none()) continue;
            standing++;
            rate &= Double.doubleToLongBits(p.cascade()[r.length - 1]) == Double.doubleToLongBits(s.getOperatingRate())
                    && Double.doubleToLongBits(p.rate()) == Double.doubleToLongBits(s.getOperatingRate());
            if (s.getOperatingRate() < 1) below++;
            int low = p.lowest();
            for (double x : r) rate &= r[low] <= x;
        }
        assertTrue("fixture: " + standing + " sectors have something standing, " + below + " of them running below nameplate",
                standing > 5 && below > 0);
        assertTrue("the six throttles are the sector's own: the fill, power, water, the road, health and its vans", own);
        assertTrue("each step of the cascade is the product of the throttles up to it", steps);
        assertTrue("...the last is getOperatingRate() to the bit, the plant's rate is too, and the lowest is the lowest", rate);
    }

    /* ============================ 3. NOTHING STANDING ============================ */

    static void nothingStanding(Game g) {
        out.println("\n--- 3. nothing standing has no rate, and the page says so (B3, B4) ---");
        Sector offices = g.getSectors().businessServices();
        assertTrue("fixture: Business Services has nothing standing (no office was built)", offices.buildingsStanding() == 0
                && g.getBuildingManager().totalBySector(offices.key(), t -> 1) == 0);
        SectorFlow.Plant p = SectorFlow.of(g, offices).plant();
        assertTrue("its plant is none, with no rate and no lowest throttle", p.none() && Double.isNaN(p.rate()) && p.lowest() == -1);
        boolean words = false, noRate = true;
        for (Sector.Line l : offices.operations(g)) {
            if (l.label().equals("Running at")) words = l.value().equals("no plant standing");
            if (l.kind() == Sector.Line.Kind.LINE && l.value().endsWith("%") && l.label().equals("Staffed")) noRate = false;
        }
        assertTrue("...and its operations page reads \"Running at: no plant standing\", not a rate, and no \"Staffed 100%\"",
                words && noRate);
        Sector bakers = g.getSectors().industry();
        String note = null;
        for (Sector.Line l : bakers.operations(g)) if (l.kind() == Sector.Line.Kind.NOTE && note == null) note = l.label();
        assertTrue("a standing plant's note names all six throttles and says they multiply: " + note,
                note != null && note.contains("vans") && note.contains("multiply") && !note.contains("whichever is thinnest"));
    }

    /* ============================ 4. THE UNITS ============================ */

    static void theUnits(Game g) {
        out.println("\n--- 4. the units are the production rows, read without making any ---");
        boolean rows = true;
        int read = 0;
        for (Sector s : g.getSectors().all()) {
            SectorFlow.Flow f = SectorFlow.of(g, s);
            for (SectorFlow.In i : f.inputs()) {
                if (i.service()) continue;
                Sector.Input row = s.inputRow(i.good());
                double bid = row == null ? 0 : row.bid, local = row == null ? 0 : row.boughtLocal, landed = row == null ? 0 : row.imported;
                rows &= i.wanted() == bid && i.fromCity() == local && i.imported() == landed;
                read++;
            }
            for (SectorFlow.Out o : f.outputs()) {
                if (o.work()) continue;
                Sector.Output row = s.outputRow(o.good());
                double made = row == null ? 0 : row.produced + row.exportBound;
                rows &= o.made() == made && o.soldHome() == (row == null ? 0 : row.soldLocal)
                        && o.exported() == (row == null ? 0 : row.exported);
                if (o.good().traded()) rows &= o.capacity() == s.getCapacity(o.good());
                read++;
            }
        }
        assertTrue("every row's units are its good's production row (" + read + " rows)", rows && read > 0);
        Sector agri = g.getSectors().agriculture();
        boolean none = true;
        for (Good x : Good.values()) none &= agri.inputRow(x) == null || agri.goodsUsed().contains(x) || agri.statement().bought.containsKey(x);
        SectorFlow.of(g, agri);
        boolean still = true;
        for (Good x : Good.values()) still &= agri.inputRow(x) == null || agri.goodsUsed().contains(x) || agri.statement().bought.containsKey(x);
        assertTrue("...and reading a flow makes no row for a good the sector does not use", none && still);

        SectorFlow.Flow shops = SectorFlow.of(g, g.getSectors().retail());
        SectorFlow.Out groceries = null;
        for (SectorFlow.Out o : shops.outputs()) if (o.good() == Good.GROCERIES) groceries = o;
        BuildCard.Note note = BuildCard.groups(g, BuildAdvice.SHOPS, null).get(0).note();
        assertTrue("fixture: the shops sold groceries this month", groceries != null && groceries.soldHome() > 0);
        assertTrue("the groceries' capacity is the Build group's note, the shops' coverage: "
                + Formats.INSTANCE.count(groceries == null ? 0 : groceries.capacity()),
                groceries != null && note.kind() == BuildCard.NoteKind.SHOPS && groceries.capacity() == note.a()
                && shops.note().equals(note));
    }

    /* ============================ 5. AFTER A LOAD ============================ */

    static void afterALoad(Path root, Game g) {
        out.println("\n--- 5. after a load the units are not counted until a month runs; the money is the same ---");
        quietly(() -> g.saveGame(10, "sectorflowcheck"));
        Game twin = new Game(new GameFiles(root.resolve("city"), root.resolve("city-no-legacy")));
        quietly(() -> twin.loadGameSave(10));
        boolean uncounted = true, money = true;
        int rows = 0;
        for (Sector s : twin.getSectors().all()) {
            SectorFlow.Flow f = SectorFlow.of(twin, s), was = SectorFlow.of(g, g.getSectors().byKey(s.key()));
            uncounted &= !f.counted();
            for (SectorFlow.In i : f.inputs()) if (!i.service()) { uncounted &= Double.isNaN(i.fromCity()); rows++; }
            for (SectorFlow.Out o : f.outputs()) if (!o.work()) { uncounted &= Double.isNaN(o.made()); rows++; }
            money &= Math.abs(f.inputsMoney() - was.inputsMoney()) <= CENT && Math.abs(f.revenueMoney() - was.revenueMoney()) <= CENT;
        }
        assertTrue("loaded, no row's units are counted (" + rows + " rows): the production rows are not saved", uncounted && rows > 0);
        assertTrue("...and every sector's money in and out is what it was before the save", money);
        quietly(() -> twin.simulateMonths(1));
        boolean counted = true;
        for (Sector s : twin.getSectors().all()) {
            SectorFlow.Flow f = SectorFlow.of(twin, s);
            counted &= f.counted();
            for (SectorFlow.Out o : f.outputs()) if (!o.work()) counted &= Double.isFinite(o.made());
        }
        assertTrue("...and a month later they are counted", counted);
    }

    /* ============================ 6. THE PAGE ============================ */

    static void thePage(Game g) {
        out.println("\n--- 6. the page is the block and the sector's own lines (D3) ---");
        boolean same = true;
        int keep = 0, replace = 0;
        for (Sector s : g.getSectors().all()) {
            List<Sector.Line> page = s.operations(g);
            List<Sector.Line> parts = new java.util.ArrayList<>();
            if (s.hasPlantBlock()) { parts.addAll(s.plantLines(g)); keep++; } else replace++;
            parts.addAll(s.ownLines(g));
            same &= page.equals(parts);
        }
        assertTrue("operations() is plantLines() then ownLines() for the " + keep + " that keep the block, ownLines() for the "
                + replace + " that replace it", same && keep > 0 && replace == 7);
    }
}

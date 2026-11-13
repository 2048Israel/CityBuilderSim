package ham.citybuildersim;

import ham.citybuildersim.sectors.HeavyIndustry;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The city's ports and their boats (0.7.86, batch O9; docs/studies/spec-oil.md
 * 2.9, 2.10 and 4's PortCheck; runs/research-freight.md 5).
 *
 * WHAT THIS HAS TO PROVE:
 *
 *   1. THE FREIGHT RULE: each kind's SEA_FREIGHT_SHARE is the research's rule
 *      - F + c x d, sea against a lorry door to door, a 50 km lorry leg at
 *      each end - at 5,000 km, from its own rows, to its two places; and every
 *      kind's is under the railway's floor, so the berths go before it.
 *
 *   2. THE CARGO, THE SHIPS AND THE TERMINALS: Good.cargo() as spec-oil 2.1
 *      writes it; the ships' cargoes [P30][P40][P42][P46]; a tanker the
 *      largest the free room takes, none under an MR; the four terminals'
 *      berths, the city's, on the coast. And (FR3, docs/queue/FR3.md 2) the
 *      Bulk Berth, id 142, and the Handysize and Supramax it takes: the
 *      month's dry bulk calls in the classes of the largest dry bulk berth
 *      standing, the Bulk Terminal's Panamax and Capesize as before. And
 *      (SL1, 2026-10-28, section 2c) the four terminals as large modern real
 *      ones, of several berths, their cost lines moved by their tonnes'
 *      ratio; Ports.payingCount(), the terminals the uncovered tonnes pay
 *      back, each counted after the ones before it, at its edges and the
 *      dial, agreeing with berthPaysBack(); and main's 35 General Cargo
 *      Terminals' trade (m3996) carried by the catalogue's 8.
 *
 *   3. THE FACTOR equals its formula exactly, and with nothing at sea it is
 *      the railway's own 1 - rail, the railway's share its own, to the bit.
 *
 *   4. THE SHARES: a kind's berths are shared among its goods by their
 *      tonnes; a kind under the quote goes before the railway and takes its
 *      tonnes off what the railway is offered; one not under it takes only
 *      what the railway leaves; crude with no tank room is held back, and
 *      with room goes in the largest class it takes.
 *
 *   5. NO PORT GIVES A BIT-IDENTICAL BAND: a town with a railway and no
 *      terminal quotes 0.7.85's band, 1 - carried and carried x quote, every
 *      good, to the bit, and nothing goes by sea.
 *
 *   6. A PORT ON A TOWN: refused NO_COAST without owned sea, allowed with it;
 *      its kind's goods then go by sea in its share, each good's band its
 *      formula, narrower than the lorries'; the road told the ships' share;
 *      its crews paid by Freight (FR3) and its upkeep with transit's; the calls tonnes over the ship's cargo;
 *      the audit closing.
 *
 *   7. CRUDE NEEDS ROOM: a refinery's crude with a tanker berth and no Tank
 *      Farm is held back for room, and its page says so; with a farm it goes
 *      by sea in the largest class the free room takes. AND THE NEXT CARGO'S
 *      ROOM (0.7.105, Jerus's R2): a Crude Unit's month beside one farm left
 *      14,185 t free, under an MR's cargo, so no tanker ever landed; the
 *      refiners now keep an MR's cargo free, and its crude goes by sea.
 *
 *   8. THE BOATS: whole calls, exact on average; inside their month; pure in
 *      (call, t), loaded the right way each leg; a lane straight out away
 *      from the site; and a frame touches only the routes on screen, and
 *      finds every boat on them.
 *
 *   9. THE SAVE: the shares in force and the month cross a save, the band and
 *      the road with them, and a month on the city and its reload agree to
 *      the bit; a save from before them loads with nothing at sea.
 *
 *  10. THE HARBOUR'S QUAY (HB1, 2026-11-08; Jerus: "and that ships dock
 *      properly"): from the zoom where the scale bar first reads 500 m
 *      (MapFrame.DETAIL_FROM, strictly above) a docked boat lies alongside
 *      its quay - heading along the face, its side on it, none of it in the
 *      work's box or quay - on each of the four sides, and is the boat itself
 *      at DETAIL_FROM and below; a berth carries its work's side; and the
 *      harbour terminals' quays are walls, side by side within the gap one
 *      wall over both sea sides and the gap, farther apart two, another way
 *      its own, no plot of any box in one. 10b (slice E, same day): boats do
 *      not overlap alongside - BoatSchedule.berthed() lays a docked boat
 *      alongside, in the order they arrived, only where its hull crosses no
 *      box, wall or other docked boat (each as it lies); three Capesizes at
 *      Bulk Berths 5 apart stay as placed, two 8 apart give the first to
 *      arrive its place, far apart every one has it, a lone berth is
 *      alongside() itself, a corner overlaps nothing, a boat's place is the
 *      same whatever window it is drawn in (BoatSchedule.drawn()), within
 *      a frame's boat budget, and below DETAIL_FROM nothing moves.
 *
 *  11. THE YARD'S PICTURE (HB2, 2026-11-09; Jerus picked the near look from
 *      renders: 1a the yard fills, 2a tracks on the quay): HarbourYard paints
 *      a work's box as w*PX by h*PX opaque pixels, the same each call and
 *      another at another place, on each of the four sides; each port kind's
 *      stock grows with the step (each step holds the one before's, step
 *      LEVELS has more than step 1) and a tank farm's does not move; step()
 *      shows any cargo at all; the quay track runs to a box's edge where it
 *      is open and stops short at a buffer where it is not, the lead comes up
 *      its end's column from the land edge to the track, a train stands on
 *      the track and nowhere else; gapCell() and leadCell() are the track and
 *      the lead at the offsets the picture has them; kindOf() reads a type
 *      by its berth's cargo or its shore; Ports.busy() is the month's sea
 *      tonnes over the berths'; and a large box's picture is painted within
 *      HarbourYard.PICTURE_MS.
 *
 * Timing harness (Timing.HARNESSES, since HB1): 10b judges a frame's boats drawn() and 11 a picture's painting through Timing, so the lines are soft under
 * -Dcbs.timing=soft and hard at the gate.
 *
 * Every fixture CAUSES its condition: the coast is bought (the offer nearest
 * the sea, as WaterCheck buys it), the terminals and the plants stood, and
 * the pure sections hand Ports its month's tonnes and berths.
 */
public class PortCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-100s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-100s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    /** A timing assertion's line (HB1 slice E, 10b's cost), as report() prints one, its verdict Timing's: soft under -Dcbs.timing=soft, hard at the gate. */
    static void timed(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-100s %s  %s%n", label, Timing.verdict(ok, label), detail);
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    static <T> T quietlyGet(java.util.function.Supplier<T> s) {
        Object[] r = new Object[1];
        quietly(() -> r[0] = s.get());
        @SuppressWarnings("unchecked") T t = (T) r[0];
        return t;
    }

    static BuildingsTemplate template(Game g, String name) {
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName(name);
        if (t == null) throw new IllegalStateException("no template named " + name);
        return t;
    }

    /** The month's audit residual as a share of the house's money tolerance at what moved (MoneyAudit.tolerance()): at most 1 closes. */
    static double auditMiss(Game g) {
        MoneyAudit.Result r = g.getLastMoneyAudit();
        return Math.abs(r.residual) / MoneyAudit.tolerance(r.moved());
    }

    /** Each fixture town's save folder. */
    static final java.util.Map<Game, GameFiles> FILES = new java.util.IdentityHashMap<>();

    /* ----- the research's rule (runs/research-freight.md 5), its rows as the research writes them ----- */

    /** The distance the shares are struck at, km: the research's headline. */
    static final double KM = 5000;

    /** A lorry: F US$2.0 a tonne, c US$0.080 a tonne-km; and the leg to a quay at each end, 50 km. */
    static final double LORRY_F = 2.0, LORRY_C = .080, ACCESS_KM = 50;

    /** The sea rows, {F, c}: liquid VLCC, Suezmax, Aframax-LR, MR... */
    static final double[][] LIQUID_ROWS = { { 4 + 1, .0012 }, { 4 + 2, .0022 }, { 4 + 5, .0035 }, { 4 + 6, .0035 } };
    /** ...dry Capesize, Panamax... */
    static final double[][] DRY_ROWS = { { 6 + 1.2, .00075 }, { 6 + 5, .0022 } };
    /** ...deep-sea boxes... */
    static final double[][] BOX_ROWS = { { 24, .0055 } };
    /** ...and general cargo. */
    static final double[][] GENERAL_ROWS = { { 40, .0060 } };

    /** Sea against a lorry, door to door, at KM: each row's, averaged over the kind's rows. */
    static double ruleShare(double[][] rows) {
        double lorry = LORRY_F + LORRY_C * KM, access = 2 * (LORRY_F + LORRY_C * ACCESS_KM), sum = 0;
        for (double[] r : rows) sum += (r[0] + r[1] * KM + access) / lorry;
        return sum / rows.length;
    }

    /**
     * A town that pays its people: houses, shops, bakeries to work in, power,
     * water, roads and builders, standing, on ground a quarter more than they
     * take and `spare` more (OilCheck's town).
     */
    static Game town(String label, int houses, double spare) {
        GameFiles files = GameFiles.scratch(label);
        Game g = new Game(files);
        FILES.put(g, files);
        quietly(() -> {
            g.newGame();
            g.setCashForTest(Founding.WEALTHY_CASH);
            BuildingManager b = g.getBuildingManager();
            String[] names = { "House", "Convenience Store", "Small Grocery Store", "Industrial Bakery", "Paved Road",
                    "Coal Power Plant", "Water Treatment Plant", "Construction Depot" };
            int[] counts = { houses, Math.max(1, houses / 45), Math.max(1, houses / 150), Math.max(1, houses / 150),
                    Math.max(4, houses / 15), 2, 1, 4 };
            double ground = 0;
            for (int i = 0; i < names.length; i++) ground += b.getTemplateByName(names[i]).getLandSqFt() * counts[i];
            g.getLandManager().setOwnedSqFt(LandManager.STARTING_SQ_FT + ground * 1.25 + spare);
            for (int i = 0; i < names.length; i++) g.buildStack(b.getTemplateByName(names[i]), counts[i], true);
        });
        return g;
    }

    /** Buys toward the sea nearest the site, the offer nearest it each time, until one standing runs out to it, and buys that (WaterCheck's way). */
    static boolean buyCoast(Game g) {
        LandManager land = g.getLandManager();
        CityLand ground = g.getCityLand();
        World world = g.getWorld();
        long[] sea = null;
        for (int r = 1; r <= 120 && sea == null; r++) {
            for (int dx = -r; dx <= r && sea == null; dx++) {
                for (int dy = -r; dy <= r && sea == null; dy++) {
                    if (Math.max(Math.abs(dx), Math.abs(dy)) != r) continue;
                    if (world.terrainAt(ground.siteX() + dx, ground.siteY() + dy) == World.SALT) sea = new long[] { ground.siteX() + dx, ground.siteY() + dy };
                }
            }
        }
        if (sea == null) return false;
        double cash = g.getCash();
        g.setCashForTest(cash + 10_000_000);
        int pushed = 0;
        LandParcel next = MiningCheck.nearestOffer(land.getMarket(), sea[0], sea[1]);
        while (next != null && land.getMarket().cheapestWithSea() == null && pushed < 60) {
            int id = next.getId();
            if (!quietlyGet(() -> g.buyLandParcel(id))) break;
            pushed++;
            next = MiningCheck.nearestOffer(land.getMarket(), sea[0], sea[1]);
        }
        LandParcel coast = g.bestOffer(Game.LandNeed.coast());
        boolean bought = coast != null && quietlyGet(() -> g.buyLandParcel(coast.getId()));
        g.setCashForTest(cash);
        return bought && land.getSeaKm2() > 0;
    }

    public static void main(String[] args) throws Exception {
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        theRule();
        cargoShipsAndTerminals();
        theBulkBerth();
        terminalsOfSeveralBerths();
        theFactor();
        theShares();
        noPortTheSameBand();
        Game port = aPortOnATown();
        crudeNeedsRoom();
        theBoats();
        acrossASave(port);
        theHarbourQuay();
        theYardsPicture();

        for (GameFiles f : FILES.values()) LongPlaytest.cleanUp(f.getDirectory().getParent());
        out.println();
        if (fails == 0) {
            out.println("All checks passed.");
        } else {
            out.println(fails + " FAILED");
            System.exit(fails);
        }
    }

    /* ============================ 1. THE RULE ============================ */

    static void theRule() {
        out.println("--- 1. the freight rule: each kind's sea freight against a lorry's, at 5,000 km, from the research's rows ---");
        Object[][] kinds = { { Ports.Cargo.LIQUID, LIQUID_ROWS }, { Ports.Cargo.DRY_BULK, DRY_ROWS },
                { Ports.Cargo.CONTAINER, BOX_ROWS }, { Ports.Cargo.GENERAL, GENERAL_ROWS } };
        for (Object[] k : kinds) {
            Ports.Cargo c = (Ports.Cargo) k[0];
            double rule = ruleShare((double[][]) k[1]);
            report(String.format("%s by sea is SEA_FREIGHT_SHARE %.2f of a lorry's: the rule's %s, to its two places",
                            c.label(), c.seaFreightShare(), ((double[][]) k[1]).length > 1 ? "rows' mean" : "row"),
                    Math.abs(rule - c.seaFreightShare()) <= .005, String.format("%.4f", rule));
        }
        assertTrue("each kind's constant is its own: liquid .08, dry bulk .07, containers .16, general cargo .20",
                Ports.Cargo.LIQUID.seaFreightShare() == Ports.SEA_FREIGHT_SHARE_LIQUID && Ports.SEA_FREIGHT_SHARE_LIQUID == .08
                        && Ports.Cargo.DRY_BULK.seaFreightShare() == Ports.SEA_FREIGHT_SHARE_DRY_BULK && Ports.SEA_FREIGHT_SHARE_DRY_BULK == .07
                        && Ports.Cargo.CONTAINER.seaFreightShare() == Ports.SEA_FREIGHT_SHARE_CONTAINER && Ports.SEA_FREIGHT_SHARE_CONTAINER == .16
                        && Ports.Cargo.GENERAL.seaFreightShare() == Ports.SEA_FREIGHT_SHARE_GENERAL && Ports.SEA_FREIGHT_SHARE_GENERAL == .20);
        boolean under = true;
        for (Ports.Cargo c : Ports.Cargo.values()) under &= c.seaFreightShare() < ham.citybuildersim.sectors.Freight.RAIL_FLOOR;
        assertTrue("SEA IS USED ONLY WHERE IT IS CHEAPER: every kind's sea freight is under the railway's floor, so the"
                + " berths go before it at any quote", under);
    }

    /* ============================ 2. THE CARGO, THE SHIPS AND THE TERMINALS ============================ */

    static void cargoShipsAndTerminals() {
        out.println("\n--- 2. the cargo, the ships and the terminals ---");
        java.util.EnumSet<Good> liquid = java.util.EnumSet.of(Good.CRUDE, Good.LPG, Good.NAPHTHA, Good.PETROL, Good.JET,
                Good.DIESEL, Good.LUBRICANTS, Good.FUEL_OIL, Good.BITUMEN);
        java.util.EnumSet<Good> dry = java.util.EnumSet.of(Good.IRON, Good.CROPS, Good.GRAINS, Good.MATERIALS, Good.COKE);
        java.util.EnumSet<Good> general = java.util.EnumSet.of(Good.STEEL, Good.FABRICATED_STEEL, Good.MACHINERY,
                Good.ROLLING_STOCK, Good.CARS, Good.VANS, Good.TRUCKS);
        boolean kinds = true, boxes = true, none = true;
        int boxed = 0;
        for (Good g : Good.values()) {
            Ports.Cargo c = g.cargo();
            if (liquid.contains(g)) kinds &= c == Ports.Cargo.LIQUID;
            else if (dry.contains(g)) kinds &= c == Ports.Cargo.DRY_BULK;
            else if (general.contains(g)) kinds &= c == Ports.Cargo.GENERAL;
            else if (g.traffic() == Traffic.GOODS) { boxes &= c == Ports.Cargo.CONTAINER; boxed++; }
            none &= (c == null) == (g.traffic() == null || !g.traffic().isFreight());
        }
        assertTrue("liquid is crude, the eight litre goods and bitumen; dry bulk iron, crops, grains, materials and coke;"
                + " general steel, fabricated steel, machinery, wagon sets, cars, vans and trucks (FR2)", kinds);
        report("...containers the rest of what the goods stream carries", boxes && boxed > 0, boxed + " goods");
        assertTrue("...and no kind for a good that never crosses the boundary as freight", none);
        assertTrue("the ships carry the research's cargoes: MR 37,500 t, LR1 60,000, Aframax 75,000, LR2 82,500, Suezmax"
                        + " 137,500, VLCC 265,000; Panamax 66,000, Capesize 165,000; a feeder 3,000 TEU at 9 t; general cargo 5,500",
                Ports.Ship.MR.tonnes() == 37_500 && Ports.Ship.LR1.tonnes() == 60_000 && Ports.Ship.AFRAMAX.tonnes() == 75_000
                        && Ports.Ship.LR2.tonnes() == 82_500 && Ports.Ship.SUEZMAX.tonnes() == 137_500 && Ports.Ship.VLCC.tonnes() == 265_000
                        && Ports.Ship.PANAMAX.tonnes() == 66_000 && Ports.Ship.CAPESIZE.tonnes() == 165_000
                        && Ports.Ship.FEEDER.tonnes() == Ports.FEEDER_TEU * Ports.TONNES_A_TEU && Ports.FEEDER_TEU == 3_000
                        && Ports.TONNES_A_TEU == 9 && Ports.Ship.GENERAL_CARGO.tonnes() == 5_500);
        boolean fits = true;
        for (double room = 0; room <= 400_000; room += 250) {
            Ports.Ship s = Ports.crudeShipFor(room);
            if (room < Ports.Ship.MR.tonnes()) { fits &= s == null; continue; }
            fits &= s != null && s.cargo() == Ports.Cargo.LIQUID && s.tonnes() <= room;
            for (Ports.Ship t : Ports.Ship.values()) fits &= !(t.cargo() == Ports.Cargo.LIQUID && t.tonnes() <= room && t.tonnes() > s.tonnes());
        }
        assertTrue("THE CLASS FITS THE FREE ROOM: a tanker is the largest whose cargo the room takes, none under an MR's",
                fits && Ports.crudeShipFor(37_499) == null && Ports.crudeShipFor(37_500) == Ports.Ship.MR
                        && Ports.crudeShipFor(100_000) == Ports.Ship.LR2 && Ports.crudeShipFor(1e9) == Ports.Ship.VLCC);
        assertTrue("...and a bulk carrier a Capesize when the month's tonnes fill one, a Panamax when not (the Bulk Terminal's classes)",
                Ports.bulkShipFor(164_999) == Ports.Ship.PANAMAX && Ports.bulkShipFor(165_000) == Ports.Ship.CAPESIZE);

        // FR3 (docs/queue/FR3.md 2, call 6): the Bulk Berth's two classes, and which classes the month's dry bulk calls in.
        java.util.List<Ports.Ship> berthClasses = java.util.List.of(Ports.Ship.HANDYSIZE, Ports.Ship.SUPRAMAX);
        assertTrue("the Bulk Berth's ships are dry bulk carriers [P37]: a Handysize 32,500 t and a Supramax 57,500, the middles of"
                        + " 30-35,000 and 55-60,000, each below a Panamax and saved by name",
                Ports.Ship.HANDYSIZE.cargo() == Ports.Cargo.DRY_BULK && Ports.Ship.HANDYSIZE.tonnes() == 32_500
                        && Ports.Ship.SUPRAMAX.cargo() == Ports.Cargo.DRY_BULK && Ports.Ship.SUPRAMAX.tonnes() == 57_500
                        && Ports.Ship.SUPRAMAX.tonnes() < Ports.Ship.PANAMAX.tonnes()
                        && Ports.Ship.byName("HANDYSIZE") == Ports.Ship.HANDYSIZE && Ports.Ship.byName("SUPRAMAX") == Ports.Ship.SUPRAMAX);
        assertTrue("...with the Bulk Berth's classes a month's tonnes one way go in a Supramax when they fill one, else a Handysize",
                Ports.bulkShipFor(Ports.Ship.SUPRAMAX.tonnes() - 1, berthClasses) == Ports.Ship.HANDYSIZE
                        && Ports.bulkShipFor(Ports.Ship.SUPRAMAX.tonnes(), berthClasses) == Ports.Ship.SUPRAMAX
                        && Ports.bulkShipFor(Ports.Ship.HANDYSIZE.tonnes() - 1, berthClasses) == Ports.Ship.HANDYSIZE
                        && Ports.bulkShipFor(Ports.Ship.CAPESIZE.tonnes() * 10, berthClasses) == Ports.Ship.SUPRAMAX);
        assertTrue("...with the Bulk Terminal's, a Capesize at its cargo and a Panamax under it, as before FR3",
                Ports.bulkShipFor(Ports.Ship.CAPESIZE.tonnes() - 1, Ports.BULK_TERMINAL_SHIPS) == Ports.Ship.PANAMAX
                        && Ports.bulkShipFor(Ports.Ship.CAPESIZE.tonnes(), Ports.BULK_TERMINAL_SHIPS) == Ports.Ship.CAPESIZE
                        && Ports.bulkShipFor(1, Ports.BULK_TERMINAL_SHIPS) == Ports.Ship.PANAMAX);
        boolean old = true;
        double[] grid = { Double.NaN, -5, 0, 1, 32_500, 57_500, 65_999, 66_000, 100_000, 164_999.5, 165_000, 165_001, 1e7 };
        for (double t : grid) old &= Ports.bulkShipFor(t, Ports.BULK_TERMINAL_SHIPS) == (t >= Ports.Ship.CAPESIZE.tonnes() ? Ports.Ship.CAPESIZE : Ports.Ship.PANAMAX)
                && Ports.bulkShipFor(t) == Ports.bulkShipFor(t, Ports.BULK_TERMINAL_SHIPS) && Ports.bulkShipFor(t, null) == Ports.bulkShipFor(t);
        assertTrue("THE BULK TERMINAL'S CLASSES ARE TODAY'S RULE EXACTLY: tonnes >= a Capesize's cargo is a Capesize, else a Panamax,"
                + " on a grid with nothing, a negative and a NaN", old);

        BuildingManager fleet = new BuildingManager();
        fleet.initializeBuiltInTemplates();
        assertTrue("THE LARGEST DRY BULK BERTH STANDING DECIDES: with none, the Bulk Terminal's classes (today's rule)",
                Ports.bulkShipsStanding(fleet).equals(Ports.BULK_TERMINAL_SHIPS) && Ports.bulkShipsStanding(null).equals(Ports.BULK_TERMINAL_SHIPS));
        fleet.addStack(fleet.getTemplateByName("Tanker Terminal"), 1, true);
        fleet.addStack(fleet.getTemplateByName("General Cargo Terminal"), 3, true);
        assertTrue("...a tanker or a general cargo berth is not a dry bulk berth, and decides nothing",
                Ports.bulkShipsStanding(fleet).equals(Ports.BULK_TERMINAL_SHIPS));
        fleet.addStack(fleet.getTemplateByName("Bulk Berth"), 2, true);
        assertTrue("...a Bulk Berth alone: Handysize and Supramax", Ports.bulkShipsStanding(fleet).equals(berthClasses));
        fleet.addStack(fleet.getTemplateByName("Bulk Terminal"), 1, true);
        assertTrue("...a Bulk Terminal beside it, the larger berth: Panamax and Capesize",
                Ports.bulkShipsStanding(fleet).equals(Ports.BULK_TERMINAL_SHIPS)
                        && fleet.getTemplateByName("Bulk Terminal").berthTonnesAYear() > fleet.getTemplateByName("Bulk Berth").berthTonnesAYear());
        BuildingManager terminalAlone = new BuildingManager();
        terminalAlone.initializeBuiltInTemplates();
        terminalAlone.addStack(terminalAlone.getTemplateByName("Bulk Terminal"), 1, true);
        assertTrue("...and a Bulk Terminal alone, the same", Ports.bulkShipsStanding(terminalAlone).equals(Ports.BULK_TERMINAL_SHIPS));

        BuildingManager bm = new BuildingManager();
        bm.initializeBuiltInTemplates();
        List<BuildingsTemplate> file = new BuildingCatalog().load();
        String[] names = { "Tanker Terminal", "Bulk Terminal", "Container Terminal", "General Cargo Terminal" };
        Ports.Cargo[] cargo = { Ports.Cargo.LIQUID, Ports.Cargo.DRY_BULK, Ports.Cargo.CONTAINER, Ports.Cargo.GENERAL };
        // SL1 (2026-10-28): each terminal one large modern real one, of several berths - see the PORTS block of BuildingManager for the cited terminals.
        double[] tonnes = { 30_000_000, 50_000_000, 2_700_000 * Ports.TONNES_A_TEU, 2_000_000 };
        double[] capital = { 886_154, 4_200_000, 1_659_600, 311_112 };
        int[] posts = { 46 + 18, 293 + 113 + 20, 1451 + 554 + 101, 36 + 13 };
        boolean terminals = true;
        for (int i = 0; i < names.length; i++) {
            BuildingsTemplate t = bm.getTemplateByName(names[i]), d = null;
            for (BuildingsTemplate x : file) if (x.getName().equals(names[i])) d = x;
            for (BuildingsTemplate x : new BuildingsTemplate[] { t, d }) {
                terminals &= x != null && x.getId() == 94 + i && x.getCategory() == BuildingType.PORTS && !x.isOwnedBySector()
                        && x.isPort() && x.berthCargo() == cargo[i] && x.berthTonnesAYear() == tonnes[i]
                        && x.getCashCost() == capital[i] * PriceLevel.FACTOR && x.getTotalJobs() == posts[i] && x.needsCoast();
            }
        }
        assertTrue("four terminals, ids 94-97, the city's, large and modern (SL1) - liquid 30 Mt, dry bulk 50 Mt, boxes 2.7M TEU,"
                + " general 2 Mt a year - at D$886M, 4,200M, 1,660M and 311M, 64, 426, 2,106 and 49 posts, on the coast", terminals);
        assertTrue("...a terminal's month is its year over twelve", Ports.berthMonth(bm.getTemplateByName("Tanker Terminal")) == 30_000_000 / 12.0);
        assertTrue("WORTH_A_BERTH, the test player's line, is the railway's own MIN_LINE_UTILISATION",
                Ports.WORTH_A_BERTH == ham.citybuildersim.sectors.Freight.MIN_LINE_UTILISATION);
    }

    /* ============================ 2b. THE BULK BERTH (FR3) ============================ */

    static void theBulkBerth() {
        out.println("\n--- the Bulk Berth (FR3) ---");
        BuildingManager bm = new BuildingManager();
        bm.initializeBuiltInTemplates();
        List<BuildingsTemplate> file = new BuildingCatalog().load();
        BuildingsTemplate fileBerth = null, fileTerminal = null;
        for (BuildingsTemplate x : file) {
            if (x.getName().equals("Bulk Berth")) fileBerth = x;
            if (x.getName().equals("Bulk Terminal")) fileTerminal = x;
        }
        java.util.List<Ports.Ship> berthClasses = java.util.List.of(Ports.Ship.HANDYSIZE, Ports.Ship.SUPRAMAX);
        boolean berth = true;
        for (BuildingsTemplate x : new BuildingsTemplate[] { bm.getTemplateByName("Bulk Berth"), fileBerth }) {
            berth &= x != null && x.getId() == 142 && x.getCategory() == BuildingType.PORTS && !x.isOwnedBySector()
                    && x.isPort() && x.needsCoast() && x.berthCargo() == Ports.Cargo.DRY_BULK && x.berthTonnesAYear() == 2_500_000
                    && x.getCashCost() == 60_000 * PriceLevel.FACTOR && x.getConstructionPoints() == 30_000 && x.getConstructionMaterials() == 1_000
                    && x.getElectricityConsumption() == 0 && x.getWaterConsumption() == 0 && x.getLandSqFt() == 215_000
                    && x.getJobs(JobType.NO_DIPLOMA) == 19 && x.getJobs(JobType.DIPLOMA) == 7 && x.getJobs(JobType.COLLEGE_ENGINEERING) == 1
                    && x.getTotalJobs() == 27 && x.berthShips().equals(berthClasses) && Ports.shipsOf(x).equals(berthClasses);
        }
        assertTrue("id 142 is the Bulk Berth, in the code and in the file alike: the city's, PORTS, a DRY_BULK berth of 2.5 Mt a year,"
                + " D$60M, 27 posts (19, 7, 1), 2 ha, no power or water, taking Handysize and Supramax", berth);
        BuildingsTemplate terminal = bm.getTemplateByName("Bulk Terminal");
        assertTrue("...the Bulk Terminal's \"ships\" line is Panamax and Capesize, in the code and in the file",
                terminal.berthShips().equals(Ports.BULK_TERMINAL_SHIPS) && fileTerminal != null
                        && fileTerminal.berthShips().equals(Ports.BULK_TERMINAL_SHIPS) && Ports.shipsOf(terminal).equals(Ports.BULK_TERMINAL_SHIPS));
        boolean open = true;
        for (String name : new String[] { "Tanker Terminal", "Container Terminal", "General Cargo Terminal" }) {
            BuildingsTemplate t = bm.getTemplateByName(name);
            java.util.List<Ports.Ship> kind = new ArrayList<>();
            for (Ports.Ship s : Ports.Ship.values()) if (s.cargo() == t.berthCargo()) kind.add(s);
            open &= t.berthShips().isEmpty() && Ports.shipsOf(t).equals(kind) && !kind.isEmpty();
        }
        assertTrue("...a terminal with no list takes every class of its kind: the tanker, container and general terminals, unchanged", open);
        assertTrue("...and a building that is not a berth takes none", Ports.shipsOf(bm.getTemplateByName("House")).isEmpty()
                && Ports.shipsOf(null).isEmpty());

        BuildingManager one = new BuildingManager(), two = new BuildingManager(), none = new BuildingManager(), both = new BuildingManager();
        for (BuildingManager b : new BuildingManager[] { one, two, none, both }) b.initializeBuiltInTemplates();
        one.addStack(one.getTemplateByName("Bulk Berth"), 1, true);
        two.addStack(two.getTemplateByName("Bulk Berth"), 2, true);
        both.addStack(both.getTemplateByName("Bulk Berth"), 1, true);
        both.addStack(both.getTemplateByName("Bulk Terminal"), 1, true);
        double[] standing = Ports.berths(one);
        boolean others = true;
        for (Ports.Cargo c : Ports.Cargo.values()) if (c != Ports.Cargo.DRY_BULK) others &= standing[c.ordinal()] == 0;
        assertTrue("ONE BULK BERTH STANDING ADDS 2,500,000 / 12 TONNES A MONTH to the dry bulk berths and nothing to any other kind",
                standing[Ports.Cargo.DRY_BULK.ordinal()] == 2_500_000 / 12.0 && Ports.berthMonth(one.getTemplateByName("Bulk Berth")) == 2_500_000 / 12.0
                        && others && Ports.berths(none)[Ports.Cargo.DRY_BULK.ordinal()] == 0);
        assertTrue("...two add twice that, and the same beside a Bulk Terminal's 50,000,000 / 12",
                Ports.berths(two)[Ports.Cargo.DRY_BULK.ordinal()] == 2 * (2_500_000 / 12.0)
                        && Ports.berths(both)[Ports.Cargo.DRY_BULK.ordinal()] == 50_000_000 / 12.0 + 2_500_000 / 12.0);

        // The month's boats in those classes: 3 Supramaxes' tonnes call in three Supramaxes, and in a Bulk Terminal's classes in Capesizes.
        Ports month = new Ports();
        month.tally(Good.IRON, 3 * Ports.Ship.SUPRAMAX.tonnes(), 0, 1);
        List<BoatSchedule.Berth> quay = List.of(new BoatSchedule.Berth(Ports.Cargo.DRY_BULK, 0, 0));
        BoatSchedule inBerth = BoatSchedule.of(4000, month, quay, 0, 0, Ports.bulkShipsStanding(one));
        BoatSchedule inTerminal = BoatSchedule.of(4000, month, quay, 0, 0, Ports.BULK_TERMINAL_SHIPS);
        BoatSchedule before = BoatSchedule.of(4000, month, quay, 0, 0);
        boolean supramaxes = inBerth.callCount() == 3, capes = inTerminal.callCount() > 0;
        for (BoatSchedule.Call c : inBerth.calls()) supramaxes &= c.ship() == Ports.Ship.SUPRAMAX && c.cargo() == Ports.Cargo.DRY_BULK;
        for (BoatSchedule.Call c : inTerminal.calls()) capes &= c.ship() == Ports.Ship.CAPESIZE;
        assertTrue("THE SCHEDULE CALLS IN THE CLASSES IT IS GIVEN: three Supramaxes' tonnes in with a Bulk Berth standing are three calls"
                + " in a Supramax each, and in the Bulk Terminal's classes Capesizes", supramaxes && capes);
        assertTrue("...a schedule given no classes is the Bulk Terminal's, as before FR3", before.calls().equals(inTerminal.calls())
                && BoatSchedule.of(4000, month, quay.stream().map(q -> BoatSchedule.lane(q, 0, 0)).toList()).calls().equals(inTerminal.calls()));
    }

    /* ============================ 2c. A TERMINAL OF SEVERAL BERTHS, ORDERED BY THE COUNT (SL1) ============================ */

    /** Tonnes a year of a General Cargo Terminal on main at 0.8.66, before SL1: the berth the default playtest stood full 35 of at m3996 (544,000 people). */
    static final double MAIN_GENERAL_BERTH_TONNES_A_YEAR = 450_000;
    /** ...and how many it stood, all full, at m3996. */
    static final int MAIN_GENERAL_BERTHS_AT_M3996 = 35;
    /**
     * How far a terminal's cash, points or materials may sit from its ratio's rounding: 2. The O9 split (BuildingDataCheck: material
     * round(cash / 60), points cash / 2) is kept exactly, so an odd cash is made even (the General Cargo Terminal's 311,111 is 311,112)
     * and a sixtieth is taken of the new cash, not scaled from the old rounded one (the Container Terminal's 27,659 is 27,660).
     */
    static final double SPLIT_ROUNDING = 2;

    static void terminalsOfSeveralBerths() {
        out.println("\n--- 2c. a terminal of several berths (SL1): its cost moved with its tonnes, and the count the tonnes pay back ---");
        BuildingManager bm = new BuildingManager();
        bm.initializeBuiltInTemplates();
        List<BuildingsTemplate> file = new BuildingCatalog().load();
        String[] names = { "Tanker Terminal", "Bulk Terminal", "Container Terminal", "General Cargo Terminal" };
        // Main's 0.8.66 terminals: tonnes a year; cash (thousands), construction points, materials, ground; the three job lines.
        double[] was = { 3_250_000, 27_500_000, 750_000 * Ports.TONNES_A_TEU, MAIN_GENERAL_BERTH_TONNES_A_YEAR };
        double[][] lines = { { 96_000, 48_000, 1_600, 430_556, 5, 2, 0 }, { 2_310_000, 1_155_000, 38_500, 2_368_060, 161, 62, 11 },
                { 461_000, 230_500, 7_683, 2_152_782, 403, 154, 28 }, { 70_000, 35_000, 1_167, 107_639, 8, 3, 0 } };
        boolean scaled = true, dearer = true;
        StringBuilder bad = new StringBuilder();
        for (int i = 0; i < names.length; i++) {
            BuildingsTemplate t = bm.getTemplateByName(names[i]), d = null;
            for (BuildingsTemplate x : file) if (x.getName().equals(names[i])) d = x;
            for (BuildingsTemplate x : new BuildingsTemplate[] { t, d }) {
                if (x == null) { scaled = false; continue; }
                double r = x.berthTonnesAYear() / was[i];
                // the lines are main's, today's thousands: the cash is read back from the founding's level (EP1)
                double[] now = { x.getCashCost() / PriceLevel.FACTOR, x.getConstructionPoints(), x.getConstructionMaterials(), x.getLandSqFt(),
                        x.getJobs(JobType.NO_DIPLOMA), x.getJobs(JobType.DIPLOMA), x.getJobs(JobType.COLLEGE_ENGINEERING) };
                for (int k = 0; k < now.length; k++) {
                    double want = Math.round(lines[i][k] * r);
                    if (k >= 4 && lines[i][k] > 0) want = Math.max(1, want);
                    if (Math.abs(now[k] - want) > (k < 3 ? SPLIT_ROUNDING : 0)) { scaled = false; bad.append(names[i]).append(" line ").append(k).append(": ").append(now[k]).append(" want ").append(want).append("; "); }
                }
                dearer &= Math.abs(x.getCashCost() / PriceLevel.FACTOR / x.berthTonnesAYear() - lines[i][0] / was[i]) <= 1e-5 * lines[i][0] / was[i];
            }
        }
        report("EACH TERMINAL'S COST LINES MOVED BY ITS TONNES' RATIO, in the code and in the file: the ground and every job line are main's times"
                + " new tonnes over old, to the whole number, the cash, the points and the materials within SPLIT_ROUNDING of it", scaled, bad.toString());
        assertTrue("...so a tonne a year costs what it did, to the rounding (the port dues a tonne are the price over the tonnes)", dearer);
        BuildingsTemplate berth = bm.getTemplateByName("Bulk Berth");
        assertTrue("...and the Bulk Berth, dry bulk's small terminal for the young city, is as it was: 2.5 Mt a year, D$60M", berth.berthTonnesAYear() == 2_500_000
                && berth.getCashCost() == 60_000 * PriceLevel.FACTOR);

        /* --- payingCount() at its edges: a round berth month, the fixtures' arithmetic proved before it is used --- */
        double month = 100_000, w = Ports.WORTH_A_BERTH;
        double line = w * month, line2 = w / 2 * month;
        assertTrue("fixture: the default dial is 1, so the break-even is WORTH_A_BERTH of a month; 2.6 months leave the line after two"
                        + " and fall under it after three, 3.3 months at the dial 2 leave its line after three",
                TaxPolicy.DEFAULT_PORT_DUES == 1 && w / TaxPolicy.DEFAULT_PORT_DUES * month == line && w == .60
                        && 2.6 * month - 2 * month >= line && 2.6 * month - 3 * month < line
                        && 3.3 * month - 3 * month >= line2 && 3.3 * month - 4 * month < line2);
        assertTrue("JUST UNDER THE BREAK-EVEN: no terminal pays back", Ports.payingCount(Math.nextDown(line), month, 1) == 0);
        assertTrue("AT THE BREAK-EVEN: one", Ports.payingCount(line, month, 1) == 1);
        assertTrue("...one still a hair under the second's break-even (a month more), two at it",
                Ports.payingCount(Math.nextDown(line + month), month, 1) == 1 && Ports.payingCount(line + month, month, 1) == 2);
        assertTrue("2.6 MONTHS OF A TERMINAL'S TONNES: three, each counted after the ones before it (the third faces .60 of a month left)",
                Ports.payingCount(2.6 * month, month, 1) == 3 && Ports.payingCount(Math.nextUp(2.6 * month), month, 1) == 3
                        && Ports.payingCount(3.5 * month, month, 1) == 3 && Ports.payingCount(3.7 * month, month, 1) == 4);
        assertTrue("THE DIAL AT 2 HALVES THE THRESHOLD: none a hair under half a month's .60, one at it, where the default dial pays none",
                Ports.payingCount(Math.nextDown(line2), month, 2) == 0 && Ports.payingCount(line2, month, 2) == 1
                        && Ports.payingCount(line2, month, 1) == 0);
        assertTrue("...2.6 months at the dial 2 are three and 3.3 months four, the dial 3 a third of the line",
                Ports.payingCount(2.6 * month, month, 2) == 3 && Ports.payingCount(3.3 * month, month, 2) == 4
                        && Ports.payingCount(w / 3 * month, month, 3) == 1 && Ports.payingCount(Math.nextDown(w / 3 * month), month, 3) == 0);
        assertTrue("NO DUES, NO BERTH, NO TONNES: none - the dial at 0 or under it or NaN, a terminal of no month or NaN, the tonnes NaN or negative",
                Ports.payingCount(1e12, month, 0) == 0 && Ports.payingCount(1e12, month, -1) == 0 && Ports.payingCount(1e12, month, Double.NaN) == 0
                        && Ports.payingCount(1e12, 0, 1) == 0 && Ports.payingCount(1e12, Double.NaN, 1) == 0
                        && Ports.payingCount(Double.NaN, month, 1) == 0 && Ports.payingCount(-5, month, 1) == 0 && Ports.payingCount(0, month, 1) == 0);

        /* --- agreeing with berthPaysBack() on a sweep, and with the inequality it is defined by --- */
        double[] dials = { .5, 1, 1.7, 2, 3, 0, -1 };
        double[] months = { 1, 37.5, 2_000_000 / 12.0, 50_000_000 / 12.0, 2_500_000 / 12.0 };
        int cases = 0;
        boolean agrees = true, definition = true, monotone = true;
        for (double dial : dials) {
            for (double m : months) {
                java.util.TreeSet<Double> tonnes = new java.util.TreeSet<>();
                for (int k = 0; k <= 400; k++) tonnes.add(k * .05 * m);
                if (dial > 0) for (int j = 0; j <= 6; j++) {
                    double edge = (w / dial + j) * m;
                    tonnes.add(Math.nextDown(edge));
                    tonnes.add(edge);
                    tonnes.add(Math.nextUp(edge));
                }
                int last = 0;
                for (double u : tonnes) {
                    int n = Ports.payingCount(u, m, dial);
                    boolean old = dial > 0 && m > 0 && u >= w / dial * m;
                    double lineHere = w / dial * m;
                    agrees &= Ports.berthPaysBack(u, m, dial) == old && (n >= 1) == old;
                    definition &= n == 0 ? !(old) : (u - (n - 1) * m >= lineHere && !(u - n * m >= lineHere));
                    monotone &= n >= last;
                    last = n;
                    cases++;
                }
            }
        }
        report("berthPaysBack() IS payingCount() >= 1 and is the old line, to the bit, on a sweep of dials, terminals and tonnes, edges a hair either side",
                agrees, cases + " cases");
        report("...and payingCount() is the largest n with the tonnes less the n - 1 before it at the line - n pays, n + 1 does not - and never falls as the tonnes rise",
                definition && monotone, cases + " cases");

        /* --- the same trade carried by fewer terminals: main's playtest's general cargo at m3996 --- */
        BuildingsTemplate general = bm.getTemplateByName("General Cargo Terminal");
        double mainMonth = MAIN_GENERAL_BERTH_TONNES_A_YEAR / 12, trade = MAIN_GENERAL_BERTHS_AT_M3996 * mainMonth;
        double terminalMonth = Ports.berthMonth(general);
        assertTrue("fixture: main's 35 berths' worth of general cargo, 35 x 450,000 / 12 t a month, is between 7 and 8 of the catalogue's General Cargo Terminal's month,"
                        + " the seven whole ones and .875 of an eighth, over the line",
                trade == MAIN_GENERAL_BERTHS_AT_M3996 * MAIN_GENERAL_BERTH_TONNES_A_YEAR / 12 && trade / terminalMonth > 7 && trade / terminalMonth < 8
                        && trade / terminalMonth - 7 >= w);
        int count = Ports.payingCount(trade, terminalMonth, TaxPolicy.DEFAULT_PORT_DUES);
        assertTrue("THE SAME TRADE TAKES 8 GENERAL CARGO TERMINALS, not main's 35: under a quarter of them, enough to carry it, and not one more than the tonnes pay for",
                count == 8 && count * 4 < MAIN_GENERAL_BERTHS_AT_M3996 && count * terminalMonth >= trade && (count - 1) * terminalMonth < trade);
        assertTrue("...payingTerminal() picks the General Cargo Terminal for it, the kind's only terminal, and orderCount() orders those 8",
                Ports.payingTerminal(Ports.Cargo.GENERAL, bm.getTemplates(), trade, trade, TaxPolicy.DEFAULT_PORT_DUES, null) == general
                        && Ports.orderCount(general, trade, TaxPolicy.DEFAULT_PORT_DUES) == count);
        assertTrue("...and the first waits for .60 of its month, 100,000 t a month: two of main's berths' worth (75,000) is none yet",
                Ports.payingCount(w * terminalMonth, terminalMonth, 1) == 1 && Ports.payingCount(trade / 35 * 2, terminalMonth, 1) == 0
                        && Ports.payingTerminal(Ports.Cargo.GENERAL, bm.getTemplates(), trade / 35 * 2, trade / 35 * 2, 1, null) == null);

        /* --- the size by the kind's whole trade (SL1): the Bulk Terminal for a trade past its break-even, Bulk Berths under it --- */
        BuildingsTemplate bulkBerth = bm.getTemplateByName("Bulk Berth"), bulkTerminal = bm.getTemplateByName("Bulk Terminal");
        double berthMonth = Ports.berthMonth(bulkBerth), bigMonth = Ports.berthMonth(bulkTerminal);
        double bigLine = w * bigMonth;                        // the Bulk Terminal's break-even, t a month
        double fewBerths = 3.5 * berthMonth;                  // a few Bulk Berths' month uncovered
        double pastIt = bigLine + berthMonth;                 // a trade past the Bulk Terminal's break-even
        Ports.Cargo dry = Ports.Cargo.DRY_BULK;
        java.util.List<BuildingsTemplate> catalogue = bm.getTemplates();
        assertTrue("fixture: 3.5 Bulk Berths' month uncovered pays a Bulk Berth back (three, each after the ones before it) and not a Bulk Terminal;"
                        + " a trade of the Bulk Terminal's break-even and a Bulk Berth's month is past the Terminal's break-even and more than the uncovered",
                Ports.berthPaysBack(fewBerths, berthMonth, 1) && Ports.payingCount(fewBerths, berthMonth, 1) == 3
                        && !Ports.berthPaysBack(fewBerths, bigMonth, 1) && Ports.berthPaysBack(pastIt, bigMonth, 1) && pastIt > fewBerths
                        && bulkBerth.berthCargo() == dry && bulkTerminal.berthCargo() == dry && bigMonth > berthMonth);
        BuildingsTemplate pick = Ports.payingTerminal(dry, catalogue, fewBerths, pastIt, 1, null);
        assertTrue("A TRADE PAST THE BULK TERMINAL'S BREAK-EVEN WITH A FEW BULK BERTHS' MONTH UNCOVERED: the Bulk Terminal, and ONE of it - the uncovered"
                        + " tonnes do not reach its own break-even, but the whole trade does",
                pick == bulkTerminal && Ports.orderCount(pick, fewBerths, 1) == 1 && Ports.payingCount(fewBerths, bigMonth, 1) == 0);
        pick = Ports.payingTerminal(dry, catalogue, fewBerths, fewBerths, 1, null);
        assertTrue("THE SAME UNCOVERED WITH THE TRADE UNDER IT (all of the trade uncovered): the Bulk Berth, and as many of it as Ports.payingCount()"
                        + " says, three",
                pick == bulkBerth && Ports.orderCount(pick, fewBerths, 1) == Ports.payingCount(fewBerths, berthMonth, 1) && Ports.orderCount(pick, fewBerths, 1) == 3);
        assertTrue("...the trade a hair under the Bulk Terminal's break-even is Bulk Berths still, at it the Bulk Terminal",
                Ports.payingTerminal(dry, catalogue, fewBerths, Math.nextDown(bigLine), 1, null) == bulkBerth
                        && Ports.payingTerminal(dry, catalogue, fewBerths, bigLine, 1, null) == bulkTerminal);
        assertTrue("NO NEED, NOTHING: the uncovered a hair under a Bulk Berth's break-even is none, however large the trade (its berths stand), and no tonnes or no dues the same",
                Ports.payingTerminal(dry, catalogue, Math.nextDown(w * berthMonth), 100 * bigMonth, 1, null) == null
                        && Ports.payingTerminal(dry, catalogue, 0, 100 * bigMonth, 1, null) == null
                        && Ports.payingTerminal(dry, catalogue, fewBerths, pastIt, 0, null) == null
                        && Ports.payingTerminal(dry, catalogue, Double.NaN, pastIt, 1, null) == null);
        assertTrue("...at a Bulk Berth's break-even exactly there is a need, and a trade past the Terminal's has it a Bulk Terminal",
                Ports.payingTerminal(dry, catalogue, w * berthMonth, pastIt, 1, null) == bulkTerminal
                        && Ports.payingTerminal(dry, catalogue, w * berthMonth, w * berthMonth, 1, null) == bulkBerth);
        assertTrue("A TERMINAL PASSED OVER (skip) LEAVES THE OTHER: the Bulk Terminal skipped is the Bulk Berth, both skipped none, and a Bulk Berth skipped has the need"
                        + " met by the Bulk Terminal alone only where the trade reaches it",
                Ports.payingTerminal(dry, catalogue, fewBerths, pastIt, 1, java.util.Set.of(bulkTerminal)) == bulkBerth
                        && Ports.payingTerminal(dry, catalogue, fewBerths, pastIt, 1, java.util.Set.of(bulkTerminal, bulkBerth)) == null
                        && Ports.payingTerminal(dry, catalogue, bigLine, pastIt, 1, java.util.Set.of(bulkBerth)) == bulkTerminal
                        && Ports.payingTerminal(dry, catalogue, fewBerths, fewBerths, 1, java.util.Set.of(bulkBerth)) == null);
        assertTrue("THE DIAL AT 2 HALVES BOTH LINES: the trade at half the Bulk Terminal's break-even is Bulk Berths at the default dial, the Bulk Terminal at 2",
                Ports.payingTerminal(dry, catalogue, fewBerths, bigLine / 2, 1, null) == bulkBerth
                        && Ports.payingTerminal(dry, catalogue, fewBerths, bigLine / 2, 2, null) == bulkTerminal);
        assertTrue("...a trade is never taken for less than what is left uncovered of it (a caller's lower figure is held to it), and the order is one at least",
                Ports.payingTerminal(dry, catalogue, pastIt, 0, 1, null) == bulkTerminal && Ports.orderCount(bulkTerminal, 0, 1) == 1
                        && Ports.orderCount(bulkTerminal, 2.7 * bigMonth, 1) == 3);
    }

    /* ============================ 3. THE FACTOR ============================ */

    static void theFactor() {
        out.println("\n--- 3. the factor equals its formula, and nothing at sea is the railway's band, to the bit ---");
        double[] grid = { 0, 1e-9, .1, .25, .3333333333333333, .5, .6, .9, 1 };
        boolean formula = true, bare = true;
        for (double r : grid) {
            for (double s : grid) {
                if (r + s > 1) continue;
                for (Ports.Cargo c : Ports.Cargo.values()) {
                    double x = c.seaFreightShare();
                    if (s > 0) formula &= Ports.factor(r, s, x) == 1 - r - s + s * x;
                    else bare &= Ports.factor(r, s, x) == 1 - r;
                }
            }
        }
        assertTrue("THE FACTOR EQUALS ITS FORMULA EXACTLY: 1 - rail - sea + sea x SEA_FREIGHT_SHARE, every share on a grid", formula);
        assertTrue("...and with nothing at sea it is the railway's own 1 - rail, to the bit", bare);
        Ports nothing = new Ports();
        boolean rail = true, sea = true;
        for (Good g : Good.values()) {
            for (double c : grid) {
                rail &= nothing.railShareOf(g, c) == c;
                sea &= nothing.seaShareOf(g, c) == 0;
            }
        }
        double[] carried = { 0, .37, .9 };
        double[] stream = nothing.railOfStream(carried);
        assertTrue("WITH NO PORT the railway's share of every good is its carried share and the ships' nothing, to the bit",
                rail && sea && !nothing.anyAtSea());
        assertTrue("...and the road's rail share is its carried share, the ships' nothing",
                stream[0] == 0 && stream[1] == .37 && stream[2] == .9 && java.util.Arrays.equals(nothing.seaOfStream(), new double[3]));
    }

    /* ============================ 4. THE SHARES ============================ */

    static double[] tonnes(Object... pairs) {
        double[] t = new double[Good.values().length];
        for (int i = 0; i < pairs.length; i += 2) t[((Good) pairs[i]).ordinal()] = ((Number) pairs[i + 1]).doubleValue();
        return t;
    }

    static void theShares() {
        out.println("\n--- 4. the shares: berths shared by tonnes; before the railway, or after it; crude and its room ---");
        // A month's trade: general cargo, dry bulk, liquids (crude and petrol) and boxes.
        double[] t = tonnes(Good.STEEL, 60_000, Good.MACHINERY, 15_000, Good.IRON, 100_000, Good.CRUDE, 50_000,
                Good.PETROL, 10_000, Good.BREAD, 4_000);
        double[] lorry = tonnes(Good.STEEL, 9_000, Good.MACHINERY, 3_000, Good.IRON, 10_000, Good.CRUDE, 2_000,
                Good.PETROL, 400, Good.BREAD, 500);
        double[] berths = new double[Ports.Cargo.values().length];
        berths[Ports.Cargo.GENERAL.ordinal()] = 37_500;
        berths[Ports.Cargo.LIQUID.ordinal()] = 3_250_000 / 12.0;
        double[] stream = new double[Traffic.values().length];
        for (Good g : Good.values()) if (g.traffic() != null && t[g.ordinal()] > 0) stream[g.traffic().ordinal()] += t[g.ordinal()];

        Ports p = new Ports();
        double quote = .6;
        double[][] first = p.planFirst(t, lorry, quote, berths, 0);
        double[] next = { 0, .5, .25 };
        p.planAfter(t, next);
        p.commit(t, stream, next);
        double s = 37_500 / 75_000.0;
        assertTrue("A KIND'S BERTHS ARE SHARED AMONG ITS GOODS BY THEIR TONNES: steel and machinery each go by sea in the"
                        + " general cargo berth over their tonnes, a half",
                p.share(Ports.Cargo.GENERAL) == s && p.seaShareOf(Good.STEEL, .25) == s && p.seaShareOf(Good.MACHINERY, .25) == s);
        assertTrue("...under the quote, before the railway: the railway hauls its carried share of what the berths leave",
                p.seaFirst(Ports.Cargo.GENERAL) && p.railShareOf(Good.STEEL, .25) == .25 * (1 - s));
        assertTrue("...and the tonnes they take come off what the railway is offered, by stream, with their lorry freight",
                first[0][Traffic.BULK.ordinal()] == 60_000 * s + 15_000 * s && first[1][Traffic.BULK.ordinal()] == 9_000 * s + 3_000 * s);
        assertTrue("a kind with no berth goes nowhere by sea, and the railway's share of it is its carried share",
                p.share(Ports.Cargo.DRY_BULK) == 0 && p.seaShareOf(Good.IRON, .25) == 0 && p.railShareOf(Good.IRON, .25) == .25
                        && p.share(Ports.Cargo.CONTAINER) == 0);
        double liquidBerth = 3_250_000 / 12.0;
        report("CRUDE WITH NO TANK ROOM IS HELD BACK: the tanker berth takes the other liquids, all of the petrol, and the"
                        + " crude it would take is held back for room",
                p.crudeShip() == null && p.seaShareOf(Good.CRUDE, 0) == 0 && p.railShareOf(Good.CRUDE, .25) == .25
                        && p.share(Ports.Cargo.LIQUID) == 1 && p.seaShareOf(Good.PETROL, .5) == 1
                        && p.getHeldBack() == 50_000 * Math.min(1, liquidBerth / 60_000),
                String.format("%,.0f t held back", p.getHeldBack()));
        double[] sea = p.seaOfStream();
        assertTrue("...and the road's two shares are what the berths take of each stream, before the railway and in all",
                Math.abs(sea[Traffic.BULK.ordinal()] - (75_000 * s) / stream[Traffic.BULK.ordinal()]) < 1e-15
                        && Math.abs(sea[Traffic.GOODS.ordinal()] - 10_000 / stream[Traffic.GOODS.ordinal()]) < 1e-15
                        && p.railOfStream(next)[Traffic.BULK.ordinal()] == .25 * (1 - (75_000 * s) / stream[Traffic.BULK.ordinal()]));

        Ports roomy = new Ports();
        roomy.planFirst(t, lorry, quote, berths, 100_000);
        roomy.planAfter(t, next);
        roomy.commit(t, stream, next);
        assertTrue("WITH ROOM FOR A CARGO crude goes by sea in the largest class the room takes, an LR2 in 100,000 t, the"
                        + " berth shared over crude and the rest",
                roomy.crudeShip() == Ports.Ship.LR2 && roomy.getHeldBack() == 0
                        && roomy.share(Ports.Cargo.LIQUID) == Math.min(1, liquidBerth / 60_000)
                        && roomy.seaShareOf(Good.CRUDE, .25) == roomy.share(Ports.Cargo.LIQUID));

        Ports after = new Ports();
        double low = .05;
        double[][] none = after.planFirst(t, lorry, low, berths, 0);
        after.planAfter(t, next);
        after.commit(t, stream, next);
        double rest = (60_000 + 15_000) * (1 - .25);
        assertTrue("A KIND NOT UNDER THE QUOTE GOES AFTER THE RAILWAY: it takes nothing off what the railway is offered",
                !after.seaFirst(Ports.Cargo.GENERAL) && none[0][Traffic.BULK.ordinal()] == 0 && none[1][Traffic.BULK.ordinal()] == 0);
        assertTrue("...its berths shared over what the railway leaves, and the railway's share its carried share, whole",
                after.share(Ports.Cargo.GENERAL) == Math.min(1, 37_500 / rest)
                        && after.seaShareOf(Good.STEEL, .25) == (1 - .25) * after.share(Ports.Cargo.GENERAL)
                        && after.railShareOf(Good.STEEL, .25) == .25);
    }

    /* ============================ 5. NO PORT, THE SAME BAND ============================ */

    static void noPortTheSameBand() {
        out.println("\n--- 5. no port gives a bit-identical band: a town with a railway and no terminal ---");
        Game g = town("portcheck-rail", 300, 2_000_000);
        Game.BuildResult[] stood = new Game.BuildResult[2];
        // ...on the literals, steel's world prices doubled, as 6 (FL1): on the world file's band under Random an Arc Furnace on imported scrap shipped
        // steel that netted a hair under its scrap's cost in the draws of 1900 (EP1: 0.0058 against 0.0059 by the third month, the slope sinking each
        // month's band with the last month's exports), THE ORDER does not buy scrap for an export at a loss, and the premise here is foundries that
        // ship (the general cargo out by rail and road) - so their steel pays.
        g.playOnLiterals();
        g.getMarkets().get(Good.STEEL).setWorldPrices(Good.STEEL.worldImportPrice() * 2, Good.STEEL.worldExportPrice() * 2);
        quietly(() -> {
            g.setCashForTest(Founding.WEALTHY_CASH * 100);
            stood[0] = g.buildStack(g.getBuildingManager().getTemplate(HeavyIndustry.ARC_FURNACE), 2, true);
            stood[1] = g.buildStack(template(g, "Rail Spur"), 1, true);
            g.simulateMonths(4);
        });
        assertTrue("fixture: two steel foundries and a rail spur stood", stood[0] == Game.BuildResult.SUCCESS
                && stood[1] == Game.BuildResult.SUCCESS);
        ham.citybuildersim.sectors.Freight rail = g.getSectors().freight();
        double[] carried = rail.getCarried();
        boolean band = true;
        int railed = 0;
        for (Good good : Good.values()) {
            GoodsMarket m = g.getMarkets().get(good);
            if (m == null) continue;
            Traffic s = good.traffic();
            double share = s == null || !s.isFreight() ? 0 : carried[s.ordinal()];
            band &= m.getFreightFactor() == Math.min(1, 1 - share) && m.getRailCharge() == (share * rail.getQuote() > 0
                    ? Math.min(1, share * rail.getQuote()) : 0);
            if (share > 0) railed++;
        }
        report("fixture: the railway carries the town's freight, and no terminal stands", railed > 0
                && Ports.berths(g.getBuildingManager())[Ports.Cargo.GENERAL.ordinal()] == 0,
                String.format("bulk %.3f, goods %.3f", carried[Traffic.BULK.ordinal()], carried[Traffic.GOODS.ordinal()]));
        assertTrue("EVERY GOOD'S BAND IS 0.7.85'S: 1 - carried and carried x the quote, to the bit", band);
        InfrastructureManager road = g.getInfrastructureManager();
        boolean roadSame = true;
        for (Traffic s : Traffic.values()) roadSame &= road.getRailShare(s) == carried[s.ordinal()] && road.getSeaShare(s) == 0;
        assertTrue("...the road's rail share the railway's carried share and the ships' nothing", roadSame);
        Ports ports = g.getPorts();
        report("...and nothing went by sea, and no boat called",
                !ports.anyAtSea() && ports.seaTonnes() == 0 && g.boats(List.of()).callCount() == 0
                        && ports.tradeOut(Ports.Cargo.GENERAL) > 0,
                String.format("%,.0f t of general cargo out by rail and road", ports.tradeOut(Ports.Cargo.GENERAL)));
    }

    /* ============================ 6. A PORT ON A TOWN ============================ */

    static Game aPortOnATown() {
        out.println("\n--- 6. a port on a town: the coast, the band, the road, the crews, the calls, the audit ---");
        Game g = town("portcheck-port", 300, 2_000_000);
        BuildingsTemplate terminal = template(g, "General Cargo Terminal");
        Game.BuildResult[] stood = new Game.BuildResult[2];
        // ...on the literals, steel's world prices doubled (FL1): an Arc Furnace on imported scrap ships steel that nets under its scrap's cost,
        // and THE ORDER no longer buys scrap for an export at a loss; the premise is foundries that ship, so their steel pays.
        g.playOnLiterals();
        g.getMarkets().get(Good.STEEL).setWorldPrices(Good.STEEL.worldImportPrice() * 2, Good.STEEL.worldExportPrice() * 2);
        quietly(() -> {
            g.setCashForTest(Founding.WEALTHY_CASH * 100);
            stood[0] = g.buildStack(g.getBuildingManager().getTemplate(HeavyIndustry.ARC_FURNACE), 2, true);
            g.simulateMonths(3);
        });
        Ports ports = g.getPorts();
        report("fixture: the town's two foundries ship steel and land iron, and it owns no sea", stood[0] == Game.BuildResult.SUCCESS
                        && ports.tradeOut(Ports.Cargo.GENERAL) > 0
                        && g.getLandManager().getSeaKm2() == 0,
                String.format("%,.0f t of general cargo out", ports.tradeOut(Ports.Cargo.GENERAL)));
        assertTrue("A TERMINAL WITH NO SEA IS REFUSED NO_COAST, before land and money",
                quietlyGet(() -> g.buildStack(terminal, 1, false)) == Game.BuildResult.NO_COAST && !g.hasCoastFor(terminal, 1));
        assertTrue("...the card's verdict says so before the click",
                BuildCard.verdict(g, terminal, 1).kind() == BuildCard.VerdictKind.NO_COAST);
        java.util.LinkedHashMap<BuildingsTemplate, Integer> run = new java.util.LinkedHashMap<>();
        run.put(terminal, 1);
        assertTrue("...and a run stops at it", g.buildRunAhead(run) == 0 && g.buildRunStop(run) == Game.BuildResult.NO_COAST);
        boolean coast = buyCoast(g);
        report("fixture: the city buys an offer that runs out to the sea", coast,
                String.format("%.5f km2 of sea", g.getLandManager().getSeaKm2()));
        assertTrue("WITH SEA THE ORDER PASSES THE COAST", g.hasCoastFor(terminal, 1)
                && BuildCard.verdict(g, terminal, 1).kind() != BuildCard.VerdictKind.NO_COAST);
        quietly(() -> {
            g.setCashForTest(Founding.WEALTHY_CASH * 100);
            stood[1] = g.buildStack(terminal, 1, true);
        });
        assertTrue("fixture: the terminal stood", stood[1] == Game.BuildResult.SUCCESS
                && g.getBuildingManager().getQuantity(terminal.getId()) == 1);

        Game twin = town("portcheck-port-twin", 300, 2_000_000);
        quietly(() -> {
            twin.setCashForTest(Founding.WEALTHY_CASH * 100);
            twin.buildStack(twin.getBuildingManager().getTemplate(HeavyIndustry.ARC_FURNACE), 2, true);
            twin.simulateMonths(3);
        });

        double worstAudit = 0;
        quietly(() -> { g.simulateMonths(1); twin.simulateMonths(1); });
        worstAudit = Math.max(worstAudit, auditMiss(g));
        double berth = Ports.berthMonth(terminal);
        double carriable = ports.carriable(Ports.Cargo.GENERAL);
        report("ITS KIND GOES BY SEA IN ITS SHARE: the berth's month over the general cargo that crossed the boundary",
                Math.abs(ports.share(Ports.Cargo.GENERAL) - Math.min(1, berth / carriable)) <= 1e-12 && ports.share(Ports.Cargo.GENERAL) > 0
                        && ports.seaFirst(Ports.Cargo.GENERAL),
                String.format("%.4f of %,.0f t", ports.share(Ports.Cargo.GENERAL), carriable));
        double[] carried = g.getSectors().freight().getCarried();
        boolean formula = true, narrower = true;
        for (Good good : Good.values()) {
            if (good.cargo() != Ports.Cargo.GENERAL) continue;
            GoodsMarket m = g.getMarkets().get(good);
            double c = carried[good.traffic().ordinal()];
            double r = ports.railShareOf(good, c), s = ports.seaShareOf(good, c);
            formula &= m.getFreightFactor() == Ports.factor(r, s, Ports.SEA_FREIGHT_SHARE_GENERAL)
                    && m.getFreightFactor() == 1 - r - s + s * Ports.SEA_FREIGHT_SHARE_GENERAL;
            if (good.importable() && good.exportable() && good.baseFreight() > 0) {
                // ...the world's delivered prices in force (WN3: where the world market has struck the band they are its two prices, else the
                // good's literals), which the lorries' band stands on before freight's change.
                double worldIn = Double.isNaN(m.getWorldImportSet()) ? good.worldImportPrice() : m.getWorldImportSet();
                double worldOut = Double.isNaN(m.getWorldExportSet()) ? good.worldExportPrice() : m.getWorldExportSet();
                narrower &= m.getFreightFactor() < 1 && m.importPrice() < worldIn * m.getExchangeRate()
                        && m.exportPrice() > worldOut * m.getExchangeRate();
            }
        }
        assertTrue("EACH GOOD'S BAND IS ITS FORMULA: Ports.factor() of its rail and sea shares, to the bit", formula);
        assertTrue("...narrower than the lorries': it lands cheaper and ships dearer than the world's delivered prices", narrower);
        GoodsMarket bread = g.getMarkets().get(Good.BREAD);
        assertTrue("...and a kind with no berth keeps the lorries' band", bread.getFreightFactor() == 1 && bread.getRailCharge() == 0);
        InfrastructureManager road = g.getInfrastructureManager();
        double[] sea = ports.seaOfStream();
        report("THE ROAD IS TOLD THE SHIPS' SHARE of each stream, relieved at the railway's RAIL_ROAD_RELIEF",
                road.getSeaShare(Traffic.BULK) == sea[Traffic.BULK.ordinal()] && sea[Traffic.BULK.ordinal()] > 0
                        && road.roadCostOf(Traffic.BULK) < 1
                        && Math.abs(road.roadCostOf(Traffic.BULK) - (1 - InfrastructureManager.BULK_HIGHWAY_RELIEF * road.getHighwayShare())
                        * (1 - InfrastructureManager.RAIL_ROAD_RELIEF * (road.getRailShare(Traffic.BULK) + road.getSeaShare(Traffic.BULK)))) <= 1e-15,
                String.format("%.4f of the bulk stream", sea[Traffic.BULK.ordinal()]));
        double posts = 0;
        double[] wages = g.getPopulationManager().getWagesPerType();
        for (JobType j : JobType.values()) posts += terminal.getJobs(j) * wages[j.ordinal()];
        double bill = g.getEconomyManager().getTransitBill(), twinBill = twin.getEconomyManager().getTransitBill();
        // ...THE LANDLORD PORT (FR3, D8; docs/queue/FR3.md 3): the crews left transit's bill for Freight's payroll,
        // and the city keeps the berths' upkeep. Its fixture is unchanged: a town with no line, so transit's bill
        // is the ports' part alone, and its twin with no terminal pays nothing and has no crew.
        ham.citybuildersim.sectors.Freight freight = g.getSectors().freight();
        double crew = g.getBuildingManager().getCategoryPayroll(BuildingType.PORTS, wages, g.getPopulationManager().getJobFillRate());
        report("ITS CREWS ARE PAID BY FREIGHT: a town with no line pays only its terminal's upkeep with transit's, Freight's wage bill"
                        + " carries the crew at the same fill, and its twin with none pays nothing",
                Math.abs(bill - g.getBuildingManager().getUpkeepByCategory(BuildingType.PORTS)) <= 1e-9 && bill < crew
                        && crew > 0 && crew <= posts * (1 + 1e-12) && Math.abs(freight.portsPayroll() - crew) <= 1e-9 * Math.max(1, crew)
                        && twinBill == 0 && twin.getSectors().freight().portsPayroll() == 0,
                String.format("$%,.2fk of upkeep with transit's; $%,.2fk of crew in Freight's payroll, of $%,.2fk fully staffed", bill, freight.portsPayroll(), posts));

        double inForce = ports.share(Ports.Cargo.GENERAL);
        quietly(() -> g.simulateMonths(1));
        worstAudit = Math.max(worstAudit, auditMiss(g));
        BoatSchedule boats = g.boats(List.of());
        double in = ports.seaIn(Ports.Cargo.GENERAL), outT = ports.seaOut(Ports.Cargo.GENERAL);
        int calls = boats.callsOf(Ports.Cargo.GENERAL);
        double cargo = Ports.Ship.GENERAL_CARGO.tonnes();
        report("CALLS = TONNES ÷ CARGO: the month's general cargo by sea, each way, over a 5,500 t ship, whole",
                outT > 0 && calls >= Math.floor(in / cargo) + Math.floor(outT / cargo) && calls <= Math.ceil(in / cargo) + Math.ceil(outT / cargo),
                String.format("%,.0f t in, %,.0f t out: %d call(s)", in, outT, calls));
        double crossed = ports.tradeIn(Ports.Cargo.GENERAL) + ports.tradeOut(Ports.Cargo.GENERAL);
        report("...the month's sea tonnes are the share that was in force while it moved, of what crossed",
                Math.abs(in + outT - inForce * crossed) <= 1e-12 * crossed && in + outT > 0,
                String.format("%,.0f of %,.0f t at %.4f", in + outT, crossed, inForce));
        for (int m = 0; m < 10; m++) {
            quietly(() -> g.simulateMonths(1));
            worstAudit = Math.max(worstAudit, auditMiss(g));
        }
        report("...and the audit closes every month of a year with the port: its residual within MoneyAudit.tolerance() of"
                + " what moved", worstAudit <= 1, String.format("worst %.2e of the tolerance", worstAudit));
        return g;
    }

    /* ============================ 7. CRUDE NEEDS ROOM ============================ */

    static void crudeNeedsRoom() {
        out.println("\n--- 7. crude needs room: held back without a Tank Farm, in the largest class the room takes with one ---");
        Game g = town("portcheck-crude", 600, 870_000 + 1_076_391 + 700_000);
        ham.citybuildersim.sectors.Refining refiners = g.getSectors().refining();
        boolean coast = buyCoast(g);
        Game.BuildResult[] stood = new Game.BuildResult[3];
        quietly(() -> {
            g.setCashForTest(Founding.WEALTHY_CASH * 100);
            g.getBusinessInvestment().holdSector(Sectors.REFINING);
            stood[0] = g.buildStack(template(g, "Oil Refinery"), 1, true);
            stood[1] = g.buildStack(template(g, "Tanker Terminal"), 1, true);
            g.simulateMonths(3);
        });
        Ports ports = g.getPorts();
        report("fixture: a refinery on imported crude, a tanker berth, and no Tank Farm", coast && ports.crudeTrade() > 0
                        && stood[0] == Game.BuildResult.SUCCESS && stood[1] == Game.BuildResult.SUCCESS
                        && refiners.tankFarmRoom() == 0 && Ports.berths(g.getBuildingManager())[Ports.Cargo.LIQUID.ordinal()] > 0,
                String.format("%,.0f t of crude across the boundary", ports.crudeTrade()));
        double liquid = ports.tradeIn(Ports.Cargo.LIQUID) + ports.tradeOut(Ports.Cargo.LIQUID);
        report("ITS CRUDE IS HELD BACK FOR ROOM: none by sea, what the berth would take held back",
                ports.crudeShip() == null && ports.crudeSeaIn() + ports.crudeSeaOut() == 0
                        && Math.abs(ports.getHeldBack() - ports.crudeTrade() * Math.min(1, Ports.berthMonth(template(g, "Tanker Terminal")) / liquid)) <= 1e-9
                        && ports.getHeldBack() > 0,
                String.format("%,.0f t a month", ports.getHeldBack()));
        boolean said = false;
        for (Sector.Line l : refiners.ownLines(g)) said |= l.label() != null && l.label().startsWith("Crude held back for room");
        assertTrue("...and the refiners' page says so", said);

        // The farm's order (spec-oil 2.8): weighed on the freight the ship would save on the refiners' part of it.
        BusinessInvestment plans = g.getBusinessInvestment();
        BuildingsTemplate farmT = template(g, "Tank Farm");
        ham.citybuildersim.sectors.SpreadPlanner.Candidate farm = refiners.farmCandidate(plans, g);
        ham.citybuildersim.sectors.SpreadPlanner.City city = refiners.planner().city(refiners, plans, g);
        GoodsMarket crude = g.getMarkets().get(Good.CRUDE);
        Sector.Input bought = refiners.inputRow(Good.CRUDE);
        double theirs = refiners.crudeHeldBackForThem(ports), saved = refiners.crudeFreightSavedByShip();
        report("A TANK FARM IS WEIGHED while a port holds the refiners' crude back for room: their part of it, at the freight a"
                        + " ship would save a tonne, less its running and standing",
                farm != null && farm.template() == farmT && bought != null
                        && theirs == ports.getHeldBack() * Math.min(1, bought.imported / ports.crudeTrade())
                        && saved == Good.CRUDE.baseFreight() * crude.getExchangeRate()
                        * (crude.getFreightFactor() + crude.getRailCharge() - Ports.SEA_FREIGHT_SHARE_LIQUID)
                        && farm.earns() == theirs * saved - city.running(farmT) - city.standing(farmT) && farm.cost() == city.cost(farmT),
                farm == null ? "none" : String.format("$%,.1fk a month on $%,.0fk; %s", farm.earns(), farm.cost(),
                        farm.passes() ? "passes" : "refused: " + farm.why()));
        BusinessInvestment.Decision plan = refiners.plan(plans, g);
        assertTrue("...and the refiners order it only when it passes every gate and earns more on its cost than any unit",
                farm != null && (farm.passes() && farm.earns() > 0) == (plan.template == farmT));
        ham.citybuildersim.sectors.SpreadPlanner.City frame = new ham.citybuildersim.sectors.SpreadPlanner.City() {
            @Override public double cost(BuildingsTemplate t) { return 250_000 * PriceLevel.FACTOR; }
            @Override public double running(BuildingsTemplate t) { return 60 * PriceLevel.FACTOR; }
            @Override public double standing(BuildingsTemplate t) { return 200 * PriceLevel.FACTOR; }
            @Override public double rate() { return 1; }
            @Override public String land(BuildingsTemplate t) { return null; }
            @Override public String staff(BuildingsTemplate t) { return null; }
            @Override public String money(double earns, double cost) { return earns >= cost * .005 ? null : "under its hurdle"; }
        };
        ham.citybuildersim.sectors.SpreadPlanner.Candidate big = ham.citybuildersim.sectors.Refining.farmEstimate(farmT, 415_000, saved, frame),
                small = ham.citybuildersim.sectors.Refining.farmEstimate(farmT, 8_300, saved, frame),
                none = ham.citybuildersim.sectors.Refining.farmEstimate(farmT, 0, saved, frame);
        report("...a Crude Unit's crude held back pays for one where an Oil Refinery's does not (a fixture's gates); nothing"
                        + " held back is refused at its feed",
                big.passes() && big.earns() == 415_000 * saved - 60 * PriceLevel.FACTOR - 200 * PriceLevel.FACTOR && !small.passes() && small.failed()
                        == ham.citybuildersim.sectors.SpreadPlanner.Gate.MONEY && none.failed() == ham.citybuildersim.sectors.SpreadPlanner.Gate.FEED,
                String.format("%.2f%% and %.2f%% a month", 100 * big.score(), 100 * small.score()));
        quietly(() -> {
            g.setCashForTest(Founding.WEALTHY_CASH * 100);
            stood[2] = g.buildStack(template(g, "Tank Farm"), 1, true);
            g.simulateMonths(2);
        });
        assertTrue("fixture: a Tank Farm stood beside it", stood[2] == Game.BuildResult.SUCCESS && refiners.tankFarmRoom() > 0);
        double room = Ports.freeCrudeRoom(refiners);
        Ports.Ship ship = ports.crudeShip();
        report("WITH A TANK FARM ITS CRUDE GOES BY SEA, in the largest class the free room takes",
                ship != null && ship == Ports.crudeShipFor(room) && ports.getHeldBack() == 0
                        && ports.crudeSeaIn() > 0 && ports.seaShareOf(Good.CRUDE, 0) > 0,
                String.format("a %s, %,.0f t of room; %,.0f t landed", ship == null ? "none" : ship.label(), room, ports.crudeSeaIn()));
        assertTrue("...its calls in that class", g.boats(List.of()).calls().stream().filter(c -> c.cargo() == Ports.Cargo.LIQUID
                && c.inbound()).allMatch(c -> c.ship() == ports.billedCrudeShip() || c.ship() == Ports.Ship.MR));

        /*
         * THE NEXT CARGO'S ROOM (0.7.105, batch P6; Jerus's R2): a Crude Unit
         * in the Oil Refinery's place. Its month is more than a farm holds
         * less an MR's cargo, so the old rule - a month as far as the room
         * held it - filled the room and left no tanker a place to land.
         */
        BuildingsTemplate crudeUnit = template(g, "Crude Unit");
        double farmRoom = refiners.tankFarmRoom() / ham.citybuildersim.sectors.Refining.CRUDE_LITRES_PER_TONNE;
        double month = ham.citybuildersim.sectors.Refining.CRUDE_COVER_MONTHS * crudeUnit.uses(Good.CRUDE);
        double oldFree = farmRoom - Math.min(month, farmRoom);
        report("fixture: one Crude Unit's month beside one Tank Farm's room: kept as far as the room held it (until 0.7.105) it"
                        + " left under an MR's cargo free, and no tanker landed", oldFree < Ports.Ship.MR.tonnes()
                        && Ports.crudeShipFor(oldFree) == null && month > farmRoom - Ports.Ship.MR.tonnes(),
                String.format("%,.0f t a month, %,.0f t of room, %,.0f t free", month, farmRoom, oldFree));
        quietly(() -> {
            g.setCashForTest(Founding.WEALTHY_CASH * 100);
            stood[0] = g.buildStack(crudeUnit, 1, true);
        });
        double kept = refiners.crudeKept();
        report("THE ROOM AN MR'S CARGO NEEDS IS KEPT FREE (Refining.CARGO_KEPT_FREE): the refiners keep the rest of the room,"
                        + " and the next cargo fits", stood[0] == Game.BuildResult.SUCCESS
                        && ham.citybuildersim.sectors.Refining.CARGO_KEPT_FREE == Ports.Ship.MR.tonnes()
                        && kept == farmRoom - Ports.Ship.MR.tonnes() && Ports.crudeShipFor(farmRoom - kept) == Ports.Ship.MR,
                String.format("%,.0f t kept, %,.0f t free", kept, farmRoom - kept));
        quietly(() -> {
            refiners.setCash(Math.max(refiners.getCash(), 1e12));
            g.simulateMonths(2);
        });
        report("...and a month on, the store kept, its crude goes by sea in an MR, none held back for room",
                ports.crudeShip() == Ports.Ship.MR && ports.getHeldBack() == 0 && ports.crudeSeaIn() > 0
                        && Math.abs(refiners.getPantry(Good.CRUDE) - refiners.crudeKept()) <= 1e-9 * refiners.crudeKept(),
                String.format("a %s; %,.0f t on hand, %,.0f t free; %,.0f t landed", ports.crudeShip() == null ? "none"
                        : ports.crudeShip().label(), refiners.getPantry(Good.CRUDE), Ports.freeCrudeRoom(refiners), ports.crudeSeaIn()));
    }

    /* ============================ 8. THE BOATS ============================ */

    static void theBoats() {
        out.println("\n--- 8. the boats: whole calls, inside their month, pure in (call, t), and a frame that touches only the"
                + " routes on screen ---");
        boolean whole = true;
        double sum = 0;
        int n = 20_000;
        for (int k = 0; k < n; k++) {
            int c = BoatSchedule.callsFor(12_345, 5_500, World.mix(k + 77L));
            whole &= c == 2 || c == 3;
            sum += c;
        }
        report("A FRACTION OF A CALL IS A WHOLE ONE in the share of months it says: 12,345 t in 5,500 t ships is 2 or 3"
                        + " calls, 2.245 on average",
                whole && Math.abs(sum / n - 12_345 / 5_500.0) < .01, String.format("%.4f", sum / n));
        assertTrue("...no tonnes, no call", BoatSchedule.callsFor(0, 5_500, 1) == 0 && BoatSchedule.callsFor(5_500, 5_500, 1) == 1);

        // A coast of 64 quays along a line, as the prototype lays them, and a month's trade by sea.
        List<BoatSchedule.Berth> quays = new ArrayList<>();
        Ports.Cargo[] kinds = Ports.Cargo.values();
        for (int i = 0; i < 64; i++) quays.add(new BoatSchedule.Berth(kinds[i % kinds.length], 10_000 + i * 97L, 5_000));
        Ports p = monthAtSea();
        BoatSchedule a = BoatSchedule.of(4000, p, quays, 10_000 + 32 * 97L, 7_000), b = BoatSchedule.of(4000, p, quays, 10_000 + 32 * 97L, 7_000);
        double leg = BoatSchedule.LEG_SECONDS / BoatSchedule.MONTH_SECONDS, berth = BoatSchedule.BERTH_SECONDS / BoatSchedule.MONTH_SECONDS;
        boolean inside = true;
        for (BoatSchedule.Call c : a.calls()) inside &= c.arrives() - leg >= 0 && c.arrives() + berth + leg <= 1 && c.route() >= 0
                && a.routes().get(c.route()).berth().cargo() == c.cargo();
        report("the schedule is pure in the month and the trade: built twice, the same calls", a.calls().equals(b.calls())
                && a.callCount() > 0, a.callCount() + " calls");
        assertTrue("...each call's whole visit inside its month, on a quay of its own kind", inside);
        BoatSchedule other = BoatSchedule.of(4001, p, quays, 10_000 + 32 * 97L, 7_000);
        assertTrue("...and another month hashes its arrivals afresh", !other.calls().equals(a.calls()));
        BoatSchedule.Route lane = BoatSchedule.lane(new BoatSchedule.Berth(Ports.Cargo.LIQUID, 1_000, 1_000), 1_000, 4_000);
        assertTrue("A LANE RUNS STRAIGHT OUT TO SEA AWAY FROM THE SITE, LANE_PLOTS long",
                lane.x1() == 1_000 && lane.y1() == 1_000 - BoatSchedule.LANE_PLOTS);

        BoatSchedule.Call call = a.calls().get(0);
        BoatSchedule.Route rt = a.routes().get(call.route());
        double t0 = call.arrives();
        BoatSchedule.Boat in = BoatSchedule.position(call, rt, t0 - leg / 2), docked = BoatSchedule.position(call, rt, t0 + berth / 2),
                outLeg = BoatSchedule.position(call, rt, t0 + berth + leg / 2);
        boolean pure = true;
        for (BoatSchedule.Call c : a.calls()) {
            BoatSchedule.Route r = a.routes().get(c.route());
            for (double t = 0; t <= 1; t += 1 / 97.0) {
                BoatSchedule.Boat x = BoatSchedule.position(c, r, t), y = BoatSchedule.position(c, r, t);
                pure &= java.util.Objects.equals(x, y)
                        && (x != null) == (t >= c.arrives() - leg && t <= c.arrives() + berth + leg);
            }
        }
        assertTrue("A BOAT IS PURE IN (CALL, t): asked twice, the same place; on the water only from a leg before it berths to"
                + " a leg after it leaves", pure);
        assertTrue("...halfway in, halfway along its lane; at the quay while it berths; halfway out on the way back",
                in != null && !in.docked() && Math.abs(in.x() - (rt.berth().x() + (rt.x1() - rt.berth().x()) * .5)) < 1e-6
                        && docked != null && docked.docked() && docked.x() == rt.berth().x() && docked.y() == rt.berth().y()
                        && outLeg != null && !outLeg.docked() && Math.abs(outLeg.y() - (rt.berth().y() + (rt.y1() - rt.berth().y()) * .5)) < 1e-6);
        boolean loads = true;
        for (BoatSchedule.Call c : a.calls()) {
            BoatSchedule.Route r = a.routes().get(c.route());
            BoatSchedule.Boat i = BoatSchedule.position(c, r, c.arrives() - leg / 2), o = BoatSchedule.position(c, r, c.arrives() + berth + leg / 2);
            boolean boxes = c.cargo() == Ports.Cargo.CONTAINER;
            loads &= i.loaded() == (boxes || c.inbound()) && o.loaded() == (boxes || !c.inbound());
        }
        assertTrue("...an import comes in loaded and leaves empty, an export the other way, a box ship loaded both ways", loads);

        // The frame: one district, and eight by eight, against every route and call by brute force.
        long[][] views = { { 10_000 + 30 * 97L, 4_000, 10_000 + 30 * 97L + World.DISTRICT, 4_000 + World.DISTRICT },
                { 9_000, 2_000, 9_000 + 8L * World.DISTRICT, 2_000 + 8L * World.DISTRICT } };
        boolean frames = true;
        int found = 0;
        StringBuilder seen = new StringBuilder();
        for (long[] v : views) {
            for (double t : new double[] { .1, .37, .5, .83 }) {
                List<BoatSchedule.Boat> got = a.frame(v[0], v[1], v[2], v[3], t);
                int crossing = 0, expect = 0;
                for (int r = 0; r < a.routes().size(); r++) {
                    if (!BoatSchedule.crosses(a.routes().get(r), v[0], v[1], v[2], v[3])) continue;
                    crossing++;
                    for (BoatSchedule.Call c : a.calls()) if (c.route() == r && BoatSchedule.position(c, a.routes().get(r), t) != null) expect++;
                }
                frames &= a.touched() == crossing && got.size() == expect && crossing < a.routes().size();
                found += got.size();
                if (t == .5) seen.append(String.format("%d of %d routes, %d boats; ", a.touched(), a.routes().size(), got.size()));
            }
        }
        report("A FRAME TOUCHES ONLY THE ROUTES ON SCREEN, and finds every boat on them, a district and eight by eight",
                frames && found > 0, seen.toString());

        // Spec-oil 2.10's scale: the 10B city's trade (BoatProto's), the schedule and a frame, measured - shown, not held.
        Ports big = new Ports();
        double[] t = new double[Good.values().length];
        t[Good.STEEL.ordinal()] = 896_308.0 * 1e10 / 469_092;
        double[] berths = new double[Ports.Cargo.values().length];
        berths[Ports.Cargo.GENERAL.ordinal()] = t[Good.STEEL.ordinal()];
        double[] stream = new double[Traffic.values().length];
        stream[Traffic.BULK.ordinal()] = t[Good.STEEL.ordinal()];
        big.planFirst(t, new double[t.length], .6, berths, 0);
        big.planAfter(t, new double[3]);
        big.commit(t, stream, new double[3]);
        big.beginMonth();
        big.tally(Good.STEEL, 0, t[Good.STEEL.ordinal()], 1);
        long start = System.nanoTime();
        BoatSchedule huge = BoatSchedule.of(4000, big, quays, 10_000 + 32 * 97L, 7_000);
        long counted = System.nanoTime() - start;
        start = System.nanoTime();
        huge.frame(10_000 + 30 * 97L, 4_000, 10_000 + 30 * 97L + World.DISTRICT, 4_000 + World.DISTRICT, .5);
        long first = System.nanoTime() - start;
        start = System.nanoTime();
        int boatsSeen = huge.frame(9_000, 2_000, 9_000 + 8L * World.DISTRICT, 2_000 + 8L * World.DISTRICT, .5).size();
        long next = System.nanoTime() - start;
        out.printf("   at 10B (spec-oil 2.10's trade, all general cargo, on these 64 quays): %,d calls counted in %.2f ms; the"
                + " first frame builds them, %.0f ms; a frame of 8 x 8 districts after, %.2f ms (%,d boats) - shown, not held%n",
                huge.callCount(), counted / 1e6, first / 1e6, next / 1e6, boatsSeen);
    }

    /** A month's trade by sea on every kind: Ports handed a month with each kind's berths taking its share. */
    static Ports monthAtSea() {
        double[] t = tonnes(Good.STEEL, 1_100_000, Good.IRON, 400_000, Good.PETROL, 80_000, Good.BREAD, 60_000);
        double[] berths = { 3_250_000 / 12.0, 27_500_000 / 12.0, 6_750_000 / 12.0, 1_100_000 };
        double[] stream = new double[Traffic.values().length];
        for (Good g : Good.values()) if (g.traffic() != null && t[g.ordinal()] > 0) stream[g.traffic().ordinal()] += t[g.ordinal()];
        Ports p = new Ports();
        p.planFirst(t, new double[t.length], .6, berths, 0);
        p.planAfter(t, new double[3]);
        p.commit(t, stream, new double[3]);
        p.beginMonth();
        p.tally(Good.STEEL, 400_000, 700_000, p.seaShareOf(Good.STEEL, 0));
        p.tally(Good.IRON, 400_000, 0, p.seaShareOf(Good.IRON, 0));
        p.tally(Good.PETROL, 80_000, 0, p.seaShareOf(Good.PETROL, 0));
        p.tally(Good.BREAD, 20_000, 40_000, p.seaShareOf(Good.BREAD, 0));
        return p;
    }

    /* ============================ 9. THE SAVE ============================ */

    static void acrossASave(Game g) throws Exception {
        out.println("\n--- 9. the shares and the month cross a save; a save from before them has nothing at sea ---");
        Ports ports = g.getPorts();
        GameFiles files = FILES.get(g);
        quietly(() -> g.saveGame(10, "ports"));
        Game twin = new Game(files);
        quietly(() -> twin.loadGameSave(10));
        Ports back = twin.getPorts();
        boolean state = twin.getLoadFailure() == null;
        for (Ports.Cargo c : Ports.Cargo.values()) {
            state &= back.share(c) == ports.share(c) && back.seaFirst(c) == ports.seaFirst(c) && back.seaIn(c) == ports.seaIn(c)
                    && back.seaOut(c) == ports.seaOut(c) && back.tradeIn(c) == ports.tradeIn(c) && back.tradeOut(c) == ports.tradeOut(c);
        }
        state &= java.util.Arrays.equals(back.seaOfStream(), ports.seaOfStream()) && back.crudeShip() == ports.crudeShip()
                && back.getHeldBack() == ports.getHeldBack() && ports.share(Ports.Cargo.GENERAL) > 0;
        report("the shares in force and the month's tonnes cross a save, to the bit", state,
                String.format("general cargo %.4f at sea", back.share(Ports.Cargo.GENERAL)));
        boolean band = true;
        for (Good good : Good.values()) {
            GoodsMarket a = g.getMarkets().get(good), b = twin.getMarkets().get(good);
            if (a == null || b == null) continue;
            band &= a.getFreightFactor() == b.getFreightFactor() && a.getRailCharge() == b.getRailCharge();
        }
        boolean road = true;
        for (Traffic s : Traffic.values()) road &= twin.getInfrastructureManager().getSeaShare(s) == g.getInfrastructureManager().getSeaShare(s)
                && twin.getInfrastructureManager().getRailShare(s) == g.getInfrastructureManager().getRailShare(s);
        assertTrue("...the band put back on every market, and the road's two shares, to the bit", band && road);
        assertTrue("...the month's boats the same", twin.boats(List.of()).calls().equals(g.boats(List.of()).calls()));
        quietly(() -> { g.simulateMonths(1); twin.simulateMonths(1); });
        boolean month = true;
        for (Ports.Cargo c : Ports.Cargo.values()) month &= back.share(c) == ports.share(c) && back.seaIn(c) == ports.seaIn(c) && back.seaOut(c) == ports.seaOut(c);
        assertTrue("...and a month on the city and its reload ship the same tonnes by sea and strike the same shares, to the bit", month);

        // A save from before 0.7.86: no portMonth.
        quietly(() -> twin.saveGame(10, "ports"));
        Path file = files.saveFile(10);
        com.google.gson.JsonObject o = com.google.gson.JsonParser.parseString(Files.readString(file)).getAsJsonObject();
        boolean had = o.has("portMonth");
        o.remove("portMonth");
        Files.writeString(file, new com.google.gson.GsonBuilder().serializeSpecialFloatingPointValues().create().toJson(o));
        Game old = new Game(files);
        quietly(() -> old.loadGameSave(10));
        Ports none = old.getPorts();
        boolean nothing = old.getLoadFailure() == null && !none.anyAtSea() && none.seaTonnes() == 0 && none.crudeShip() == null;
        for (Traffic s : Traffic.values()) nothing &= old.getInfrastructureManager().getSeaShare(s) == 0;
        report("A SAVE FROM BEFORE 0.7.86 (no portMonth) loads with nothing at sea", had && nothing,
                "the key was " + (had ? "there" : "missing"));
        quietly(() -> old.simulateMonths(1));
        report("...and its terminal takes its share the first month it plays, the audit closing",
                none.share(Ports.Cargo.GENERAL) > 0 && auditMiss(old) <= 1,
                String.format("%.4f", none.share(Ports.Cargo.GENERAL)));
    }

    /* ============================ 10. THE HARBOUR'S QUAY ============================ */

    /** The fixture terminal's box, in plots: 6 across and 5 deep (the fixture's own, not the model's). */
    static final int BOX_W = 6, BOX_H = 5;

    /** A harbour terminal of the fixture: its box at (x0, y0), the sea on `side`, the harbour's quay. */
    static CityShore.Work harbourWork(long x0, long y0, int side) {
        return new CityShore.Work(x0, y0, BOX_W, BOX_H, 0, side, CityShore.HARBOUR_QUAY_PLOTS);
    }

    /** Two harbour terminals facing `side` at (2000, 2000), the second `gap` plots past the first along the shore, on the same line. */
    static CityShore pairOnShore(int side, int gap) {
        boolean along = side % 2 == 0;
        int len = along ? BOX_W : BOX_H;
        CityShore s = new CityShore();
        s.add(harbourWork(2000, 2000, side));
        s.add(harbourWork(2000 + (along ? len + gap : 0), 2000 + (along ? 0 : len + gap), side));
        return s;
    }

    /** The wall the pair should make, worked from the geometry alone: the strip before both sea sides and the gap between, HARBOUR_QUAY_PLOTS deep. */
    static CityShore.Wall pairsWall(int side, int gap) {
        int q = CityShore.HARBOUR_QUAY_PLOTS, len = (side % 2 == 0 ? BOX_W : BOX_H) * 2 + gap;
        switch (side) {
            case 0:  return new CityShore.Wall(2000, 2000 - q, len, q, 0);
            case 1:  return new CityShore.Wall(2000 + BOX_W, 2000, q, len, 1);
            case 2:  return new CityShore.Wall(2000, 2000 + BOX_H, len, q, 2);
            default: return new CityShore.Wall(2000 - q, 2000, q, len, 3);
        }
    }

    /** Whether some wall of `walls` holds world plot (x, y). */
    static boolean walled(List<CityShore.Wall> walls, long x, long y) {
        for (CityShore.Wall w : walls) if (w.holds(x, y)) return true;
        return false;
    }

    /** Whether every plot of the rectangle (x0, y0, w, h) is in a wall (all = true), or none of them is (all = false). */
    static boolean walledAll(List<CityShore.Wall> walls, long x0, long y0, long w, long h, boolean all) {
        for (long y = y0; y < y0 + h; y++) for (long x = x0; x < x0 + w; x++) if (walled(walls, x, y) != all) return false;
        return true;
    }

    /** Whether any plot of any wall of `shore` is in a work's box. */
    static boolean wallInABox(CityShore shore) {
        for (CityShore.Wall w : shore.walls()) {
            for (long y = w.y0(); y < w.y0() + w.h(); y++) for (long x = w.x0(); x < w.x0() + w.w(); x++) if (shore.at(x, y) != null) return true;
        }
        return false;
    }

    static void theHarbourQuay() {
        out.println("\n--- 10. the harbour's quay (HB1): from the detail zoom in a boat at its berth lies alongside its quay and the terminals' quays are one wall ---");
        double eps = 1e-9;

        // THE DIAL: the zoom where the scale bar first reads 500 m, strictly above DETAIL_FROM.
        MapFrame f = new MapFrame(1000, 800);
        double mid = World.SIDE / 2.0;
        f.set(mid, mid, MapFrame.DETAIL_FROM);
        double atFrom = f.scaleBar(MapFrame.SCALE_BAR_PX)[0];
        f.set(mid, mid, MapFrame.DETAIL_FROM * 1.01);
        double above = f.scaleBar(MapFrame.SCALE_BAR_PX)[0];
        f.set(mid, mid, Math.nextDown(MapFrame.DETAIL_FROM));
        double below = f.scaleBar(MapFrame.SCALE_BAR_PX)[0];
        report("the dial: the bar reads 1,000 m at DETAIL_FROM, 500 m at 1% past it, 1,000 m just under it",
                atFrom == 1000 && above == 500 && below == 1000 && MapFrame.DETAIL_FROM == MapFrame.SCALE_BAR_PX * World.PLOT_M / 1000
                        && MapFrame.DETAIL_FROM > MapFrame.L0_FROM,
                String.format("DETAIL_FROM %.3f px a plot, bar %.0f / %.0f / %.0f m", MapFrame.DETAIL_FROM, atFrom, above, below));

        // ALONGSIDE: a docked boat at a harbour berth on each side, from above DETAIL_FROM.
        Ports.Ship[] ships = { Ports.Ship.GENERAL_CARGO, Ports.Ship.MR, Ports.Ship.VLCC };
        double[] scales = { MapFrame.DETAIL_FROM * 1.01, MapFrame.BIG_TILES_FROM, MapFrame.MOST_PX_A_PLOT };
        int cases = 0;
        boolean parallel = true, turned = true, seaward = true, onFace = true, beam = true, clear = true, lateral = true, kept = true;
        boolean narrow = false, wide = false;
        for (int side = 0; side < 4; side++) {
            CityShore.Work w = harbourWork(1000, 1000, side);
            long[] sp = w.shorePlot();
            double mx = sp[0] + 0.5, my = sp[1] + 0.5, dx = CityShore.DX[side], dy = CityShore.DY[side];
            double[] bp = w.berth();
            double face = w.quay() + 0.5;
            for (Ports.Ship ship : ships) {
                for (double scale : scales) {
                    BoatSchedule.Call call = new BoatSchedule.Call(ship.cargo(), ship, true, 0, 0.5);
                    BoatSchedule.Boat b = new BoatSchedule.Boat(call, bp[0], bp[1], true, true, 0.3, 1);
                    BoatSchedule.Boat a = BoatSchedule.alongside(b, side, scale);
                    cases++;
                    double hx = Math.cos(a.heading()), hy = Math.sin(a.heading());
                    parallel &= Math.abs(hx * dx + hy * dy) < eps;
                    turned &= Math.abs((dx * hy - dy * hx) - 1) < eps;
                    kept &= a.call().equals(b.call()) && a.docked() && a.loaded() == b.loaded() && a.alpha() == b.alpha();
                    lateral &= Math.abs((a.x() - bp[0]) * dy - (a.y() - bp[1]) * dx) < eps;
                    double halfBeam = ShipShapes.drawnLength(a, scale) * ShipShapes.BEAM / 2;
                    narrow |= halfBeam < 0.5;
                    wide |= halfBeam > 0.5;
                    double[][] hull = ShipShapes.hull(a, scale);
                    double least = Double.MAX_VALUE, most = -Double.MAX_VALUE;
                    for (int i = 0; i < hull[0].length; i++) {
                        double px = hull[0][i], py = hull[1][i], depth = (px - mx) * dx + (py - my) * dy;
                        least = Math.min(least, depth);
                        most = Math.max(most, depth);
                        clear &= !(px > w.x0() + eps && px < w.x0() + w.w() - eps && py > w.y0() + eps && py < w.y0() + w.h() - eps);
                        for (int k = 1; k <= w.quay(); k++) {
                            long[] qp = w.quayPlot(k);
                            clear &= !(px > qp[0] + eps && px < qp[0] + 1 - eps && py > qp[1] + eps && py < qp[1] + 1 - eps);
                        }
                    }
                    seaward &= least >= face - eps;
                    onFace &= Math.abs(least - face) < eps;
                    beam &= Math.abs(most - least - 2 * halfBeam) < eps;
                }
            }
        }
        String of = cases + " boats on four sides";
        report("alongside: the heading is along the quay face, the sea's direction turned a quarter the same way every time", parallel && turned && cases == 4 * ships.length * scales.length, of);
        report("...every hull point seaward of the face, none in the work's box or its quay's plots", seaward && clear, of);
        report("...the hull's side on the face, its far side a beam out (hulls narrower and wider than a plot)", onFace && beam && narrow && wide, of);
        report("...the boat on the berth's line out from the work, its call, load and docking as they were", lateral && kept, of);

        // ...at DETAIL_FROM and below, a boat not docked, a side not known: the boat itself.
        boolean same = true;
        BoatSchedule.Call call = new BoatSchedule.Call(Ports.Cargo.LIQUID, Ports.Ship.MR, true, 0, 0.5);
        BoatSchedule.Boat docked = new BoatSchedule.Boat(call, 1000.5, 990.5, true, true, 0.3, 1);
        BoatSchedule.Boat afloat = new BoatSchedule.Boat(call, 1000.5, 990.5, true, false, 0.3, 1);
        for (int side = 0; side < 4; side++) {
            for (double scale : new double[] { MapFrame.DETAIL_FROM, MapFrame.L0_FROM, MapFrame.L1_FROM, f.leastScale() }) {
                same &= BoatSchedule.alongside(docked, side, scale).equals(docked);
            }
            same &= BoatSchedule.alongside(afloat, side, MapFrame.MOST_PX_A_PLOT).equals(afloat);
        }
        same &= BoatSchedule.alongside(docked, -1, MapFrame.MOST_PX_A_PLOT).equals(docked)
                && BoatSchedule.alongside(docked, CityShore.DX.length, MapFrame.MOST_PX_A_PLOT).equals(docked)
                && !BoatSchedule.alongside(docked, 0, MapFrame.MOST_PX_A_PLOT).equals(docked);
        report("...at DETAIL_FROM and below, for a boat afloat, and for a berth whose side is not known: the boat itself", same, "");

        // THE BERTH SAYS ITS SIDE: CityShore.berths() hands the work's side on; a berth made by hand has none.
        BuildingVisual.Type terminal = new BuildingVisual.Type(0, null, 0, false, BuildingVisual.NOT_A_ROAD, null, false, 1, false, 0, 0, 0,
                false, true, false, false, false, Ports.Cargo.LIQUID.ordinal(), 0, null, BuildingVisual.PLAIN, BuildingVisual.NO_GROUP, false, "", false, false);
        boolean sides = new BoatSchedule.Berth(Ports.Cargo.LIQUID, 1, 2).side() == -1;
        for (int side = 0; side < 4; side++) {
            CityShore one = new CityShore();
            CityShore.Work w = harbourWork(1000, 1000, side);
            one.add(w);
            List<BoatSchedule.Berth> berths = one.berths(new BuildingVisual.Type[] { terminal });
            double[] bp = w.berth();
            sides &= berths.size() == 1 && berths.get(0).side() == side && berths.get(0).x() == (long) Math.floor(bp[0]) && berths.get(0).y() == (long) Math.floor(bp[1])
                    && berths.get(0).cargo() == Ports.Cargo.LIQUID;
        }
        assertTrue("the berth carries its work's side from CityShore.berths(), -1 for a berth made by hand", sides);

        // THE WALLS: side by side within the gap, one wall; farther apart, two; another way, its own; none in a box.
        int q = CityShore.HARBOUR_QUAY_PLOTS, join = CityShore.WALL_JOIN_GAP;
        boolean one = true, two = true, noBox = true, fromGeometry = join == 2 * CityShore.CLEAR + 1;
        for (int side = 0; side < 4; side++) {
            for (int gap = CityShore.CLEAR; gap <= join; gap++) {
                CityShore s = pairOnShore(side, gap);
                one &= s.walls().equals(List.of(pairsWall(side, gap)));
                noBox &= !wallInABox(s);
                // the sea sides and the gap between all walled, and not a plot inland of the line
                CityShore.Wall e = pairsWall(side, gap);
                one &= walledAll(s.walls(), e.x0(), e.y0(), e.w(), e.h(), true);
            }
            CityShore apart = pairOnShore(side, join + 1);
            CityShore.Wall e = pairsWall(side, join + 1);      // the span of both strips and the gap between
            boolean along = side % 2 == 0;
            int len = along ? BOX_W : BOX_H;
            long gx = e.x0() + (along ? len : 0), gy = e.y0() + (along ? 0 : len);      // the gap's strip
            two &= apart.walls().size() == 2 && walledAll(apart.walls(), gx, gy, along ? join + 1 : q, along ? q : join + 1, false)
                    && walledAll(apart.walls(), e.x0(), e.y0(), along ? len : q, along ? q : len, true)
                    && walledAll(apart.walls(), gx + (along ? join + 1 : 0), gy + (along ? 0 : join + 1), along ? len : q, along ? q : len, true);
            noBox &= !wallInABox(apart);
        }
        assertTrue("walls: two works side by side within the gap (WALL_JOIN_GAP, from CLEAR), on each of the four sides: one wall over both sea sides and the gap", one && fromGeometry);
        assertTrue("...the same two farther apart than the gap: two walls, the gap not covered", two);

        CityShore mixed = pairOnShore(0, CityShore.CLEAR);
        mixed.add(harbourWork(2100, 2000, 1));                        // another way: east
        mixed.add(harbourWork(2000 + 2 * BOX_W + 2 * CityShore.CLEAR, 2001, 0));   // the same side a plot inland, a gap on: another line
        mixed.add(new CityShore.Work(2200, 2000, BOX_W, BOX_H, 0, 0, CityShore.QUAY_PLOTS[0]));   // a pier of its own: no wall
        mixed.add(new CityShore.Work(2300, 2000, BOX_W, BOX_H, 0, 0, 0));                         // a tank farm: no quay
        List<CityShore.Wall> mw = mixed.walls();
        boolean own = mw.size() == 3 && mw.contains(new CityShore.Wall(2100 + BOX_W, 2000, q, BOX_H, 1))
                && mw.contains(new CityShore.Wall(2000 + 2 * BOX_W + 2 * CityShore.CLEAR, 2001 - q, BOX_W, q, 0))
                && mw.contains(new CityShore.Wall(2000, 2000 - q, 2 * BOX_W + CityShore.CLEAR, q, 0))
                && mw.stream().noneMatch(w -> w.x0() >= 2200);
        noBox &= !wallInABox(mixed);
        assertTrue("...a third facing another way has its own, the same side on another line has its own, a pier of its own and a tank farm have none", own);

        // A box in a strip cuts the wall round it.
        CityShore cut = new CityShore();
        cut.add(harbourWork(2000, 2000, 0));
        CityShore.Work in = new CityShore.Work(2003, 2000 - q + 1, 2, 1, 0, 1, 0);   // a small box one row into the strip, two plots across
        cut.add(in);
        List<CityShore.Wall> cw = cut.walls();
        int plots = 0;
        for (CityShore.Wall w : cw) plots += w.w() * w.h();
        boolean cutOk = plots == BOX_W * q - in.w() * in.h() && walledAll(cw, 2003, 2000 - q + 1, in.w(), in.h(), false) && !wallInABox(cut);
        noBox &= !wallInABox(cut);
        assertTrue("...no plot of any work's box is in a wall: a box in a strip cuts the wall round it, the rest of the strip still walled", noBox && cutOk);

        // The walls are made once a version and asked by the window; the order the works were laid in does not matter.
        CityShore laid = pairOnShore(2, CityShore.CLEAR), backwards = new CityShore();
        for (int i = laid.works().size() - 1; i >= 0; i--) backwards.add(laid.works().get(i));
        boolean order = laid.walls().equals(backwards.walls());
        long before = laid.version();
        laid.add(harbourWork(2000 + 2 * (BOX_W + CityShore.CLEAR), 2000, 2));
        boolean grows = laid.version() > before && laid.walls().size() == 1 && laid.walls().get(0).w() == 3 * BOX_W + 2 * CityShore.CLEAR;
        CityShore.Wall one0 = new CityShore.Wall(2000, 2000 - q, 2 * BOX_W + CityShore.CLEAR, q, 0);
        boolean window = mixed.wallsIn(one0.x0(), one0.y0(), one0.x0() + one0.w(), one0.y0() + one0.h()).contains(one0)
                && mixed.wallsIn(one0.x0() + 5000, one0.y0(), one0.x0() + 6000, one0.y0() + 100).isEmpty()
                && mixed.wallsIn(Long.MIN_VALUE / 2, Long.MIN_VALUE / 2, Long.MAX_VALUE / 2, Long.MAX_VALUE / 2).equals(mw)
                && mixed.wallsIn(one0.x0() + one0.w(), one0.y0(), one0.x0() + one0.w() + 1, one0.y0() + 1).stream().noneMatch(one0::equals);
        assertTrue("...made again when a work is laid (a third joins the wall), the same in whatever order they were laid, asked by the window", order && grows && window);

        berthsShareTheQuay();
    }

    /* ----- 10b. THE BOATS ALONGSIDE OVERLAP NOTHING (HB1 slice E, 2026-11-08) ----- */

    /** A Bulk Berth's box of the fixture, in plots: 5 along the shore and 4 deep (the fixture's own; the berth's land is 215,000 m2). */
    static final int BULK_ALONG = 5, BULK_DEEP = 4;

    /** The scales the berths are laid at, px a plot: 4, just past DETAIL_FROM (where boats were drawn 1.6 times true before slice E), and 8 (the renders' other). */
    static final double[] BERTH_SCALES = { 4, 8 };

    /** A Bulk Berth of the fixture with its box at (x0, y0), the sea on `side`, the harbour's quay. */
    static CityShore.Work bulkWork(long x0, long y0, int side) {
        boolean along = side % 2 == 0;
        return new CityShore.Work(x0, y0, along ? BULK_ALONG : BULK_DEEP, along ? BULK_DEEP : BULK_ALONG, 0, side, CityShore.HARBOUR_QUAY_PLOTS);
    }

    /** `count` Bulk Berths facing `side`, `pitch` plots from one box to the next along the shore, from (3000, 3000). */
    static CityShore berthRow(int count, int side, int pitch) {
        CityShore s = new CityShore();
        boolean along = side % 2 == 0;
        for (int k = 0; k < count; k++) s.add(bulkWork(3000 + (along ? (long) k * pitch : 0), 3000 + (along ? 0 : (long) k * pitch), side));
        return s;
    }

    /** The routes of a shore's berthed works in the order laid, one each, the berth's cargo cycling through `cargos`: a straight lane 60 plots out to sea from the work's berth point, the berth carrying the work's side. */
    static List<BoatSchedule.Route> routesOf(CityShore shore, Ports.Cargo... cargos) {
        List<BoatSchedule.Route> routes = new ArrayList<>();
        for (CityShore.Work w : shore.works()) {
            if (!w.berthed()) continue;
            double[] bp = w.berth();
            BoatSchedule.Berth berth = new BoatSchedule.Berth(cargos[routes.size() % cargos.length], (long) Math.floor(bp[0]), (long) Math.floor(bp[1]), w.side());
            routes.add(BoatSchedule.Route.through(berth, new double[] { bp[0], bp[0] + 60.0 * CityShore.DX[w.side()] },
                    new double[] { bp[1], bp[1] + 60.0 * CityShore.DY[w.side()] }, 1, 0, 0));
        }
        return routes;
    }

    /** A call of `ship` on route r arriving at `arrives`, and its boat at the quay the instant it does, as the schedule places it. */
    static BoatSchedule.Boat dockedOn(List<BoatSchedule.Route> routes, int r, Ports.Ship ship, double arrives) {
        return BoatSchedule.position(new BoatSchedule.Call(ship.cargo(), ship, true, r, arrives), routes.get(r), arrives);
    }

    /** A rectangle's outline {xs, ys}. */
    static double[][] box(double x, double y, double w, double h) {
        return new double[][] { { x, x + w, x + w, x }, { y, y, y + h, y + h } };
    }

    /**
     * How thick the part two convex outlines share is: the narrowest width, across the edges of the part itself, of what the second clips of the
     * first (Sutherland-Hodgman) - 0 when they share nothing or only a line. Worked from the outlines alone, not from the model's separating-axis test.
     */
    static double thickness(double[][] a, double[][] b) {
        List<double[]> poly = new ArrayList<>();
        for (int i = 0; i < a[0].length; i++) poly.add(new double[] { a[0][i], a[1][i] });
        int m = b[0].length;
        double wind = 0;
        for (int i = 0; i < m; i++) wind += b[0][i] * b[1][(i + 1) % m] - b[0][(i + 1) % m] * b[1][i];
        double sign = Math.signum(wind);
        for (int e = 0; e < m && poly.size() > 2; e++) {
            double ex = b[0][e], ey = b[1][e], fx = b[0][(e + 1) % m] - ex, fy = b[1][(e + 1) % m] - ey;
            List<double[]> next = new ArrayList<>();
            for (int i = 0; i < poly.size(); i++) {
                double[] p = poly.get(i), q = poly.get((i + 1) % poly.size());
                double sp = sign * (fx * (p[1] - ey) - fy * (p[0] - ex)), sq = sign * (fx * (q[1] - ey) - fy * (q[0] - ex));
                if (sp >= 0) next.add(p);
                if ((sp >= 0) != (sq >= 0)) {
                    double u = sp / (sp - sq);
                    next.add(new double[] { p[0] + (q[0] - p[0]) * u, p[1] + (q[1] - p[1]) * u });
                }
            }
            poly = next;
        }
        if (poly.size() < 3) return 0;
        double least = Double.MAX_VALUE;
        for (int i = 0; i < poly.size(); i++) {
            double[] p = poly.get(i), q = poly.get((i + 1) % poly.size());
            double nx = q[1] - p[1], ny = p[0] - q[0], len = Math.hypot(nx, ny);
            if (len < 1e-12) continue;
            double lo = Double.MAX_VALUE, hi = -Double.MAX_VALUE;
            for (double[] r : poly) {
                double d = (r[0] * nx + r[1] * ny) / len;
                lo = Math.min(lo, d);
                hi = Math.max(hi, d);
            }
            least = Math.min(least, hi - lo);
        }
        return least == Double.MAX_VALUE ? 0 : least;
    }

    /**
     * What a layout of docked boats comes to, counted from the outlines (thickness()) and not the model's test: {how many lie in a place of their own, alongside or stern-to (are not
     * the boat given), how many pairs of drawn hulls share a part thicker than BoatSchedule.CROSS_TOLERANCE, how many boats alongside have a part
     * of that thickness in a work's box or a wall of `shore`}.
     */
    static int[] audit(CityShore shore, List<BoatSchedule.Boat> given, List<BoatSchedule.Boat> drawn, double scale) {
        double deep = BoatSchedule.CROSS_TOLERANCE + 1e-9;
        int alongside = 0, overlaps = 0, crossings = 0;
        for (int i = 0; i < drawn.size(); i++) {
            BoatSchedule.Boat a = drawn.get(i);
            if (!a.docked()) continue;
            double[][] ha = ShipShapes.hull(a, scale);
            if (!a.equals(given.get(i))) {
                alongside++;
                for (CityShore.Work w : shore.works()) if (thickness(ha, box(w.x0(), w.y0(), w.w(), w.h())) > deep) crossings++;
                for (CityShore.Wall w : shore.walls()) if (thickness(ha, box(w.x0(), w.y0(), w.w(), w.h())) > deep) crossings++;
            }
            for (int j = i + 1; j < drawn.size(); j++) {
                if (drawn.get(j).docked() && thickness(ha, ShipShapes.hull(drawn.get(j), scale)) > deep) overlaps++;
            }
        }
        return new int[] { alongside, overlaps, crossings };
    }

    /** `n` copies of a side, for kinds(): a row of boats facing one way. */
    static int[] same(int side, int n) {
        int[] s = new int[n];
        java.util.Arrays.fill(s, side);
        return s;
    }

    /** How a layout's docked boats lie, by comparing each with the places BoatSchedule gives it on its side (sides[i], the side of the berth of boat i): {alongside, stern-to, as placed}. */
    static int[] kinds(List<BoatSchedule.Boat> given, List<BoatSchedule.Boat> drawn, int[] sides, double scale) {
        int[] k = new int[3];
        for (int i = 0; i < drawn.size(); i++) {
            if (!drawn.get(i).docked()) continue;
            if (drawn.get(i).equals(BoatSchedule.alongside(given.get(i), sides[i], scale))) k[0]++;
            else if (drawn.get(i).equals(BoatSchedule.sternTo(given.get(i), sides[i], scale))) k[1]++;
            else if (drawn.get(i).equals(given.get(i))) k[2]++;
            else throw new IllegalStateException("a boat in none of the three places: " + drawn.get(i));
        }
        return k;
    }

    /** The sum of a kinds() count into another. */
    static void add(int[] to, int[] k) {
        for (int i = 0; i < to.length; i++) to[i] += k[i];
    }

    static void berthsShareTheQuay() {
        out.println("\n--- 10b. (HB1 slice E) the boats lie alongside, else stern-to, else as placed, only where their hulls cross no box, wall or other docked boat ---");
        double tol = BoatSchedule.CROSS_TOLERANCE;
        Ports.Ship cape = Ports.Ship.CAPESIZE;
        List<BoatSchedule.Route> one = routesOf(berthRow(1, 0, 0), cape.cargo());
        double length = ShipShapes.drawnLength(dockedOn(one, 0, cape, .5), BERTH_SCALES[0]), beam = length * ShipShapes.BEAM;
        boolean trueSize = true;
        for (double scale : BERTH_SCALES) trueSize &= scale > MapFrame.DETAIL_FROM && ShipShapes.boost(scale) == 1
                && ShipShapes.drawnLength(dockedOn(one, 0, cape, .5), scale) == length;
        report("slice E: from the detail zoom in boats are drawn at true size (ShipShapes.TRUE_FROM is DETAIL_FROM), so the fixtures' scales draw a Capesize one length",
                trueSize && ShipShapes.TRUE_FROM == MapFrame.DETAIL_FROM, String.format("a Capesize %.2f plots long, %.2f across", length, beam));

        // THREE CAPESIZES AT BERTHS TOUCHING (5 plots apart, the box's width) and at the harbour's own spacing (a box and CLEAR).
        int cases = 0, lie = 0, overlaps = 0, crossings = 0;
        int[] k5 = new int[3], k6 = new int[3];
        int rows = 4 * BERTH_SCALES.length;
        for (int pitch : new int[] { BULK_ALONG, BULK_ALONG + CityShore.CLEAR }) {
            for (int side = 0; side < 4; side++) {
                for (double scale : BERTH_SCALES) {
                    CityShore shore = berthRow(3, side, pitch);
                    List<BoatSchedule.Route> routes = routesOf(shore, cape.cargo());
                    List<BoatSchedule.Boat> given = List.of(dockedOn(routes, 0, cape, .5), dockedOn(routes, 1, cape, .5001), dockedOn(routes, 2, cape, .5002));
                    List<BoatSchedule.Boat> drawn = BoatSchedule.berthed(given, routes, scale, shore);
                    int[] au = audit(shore, given, drawn, scale);
                    cases++;
                    lie += au[0];
                    overlaps += au[1];
                    crossings += au[2];
                    add(pitch == BULK_ALONG ? k5 : k6, kinds(given, drawn, same(side, 3), scale));
                }
            }
        }
        report("three Capesizes at Bulk Berths side by side facing one way, 5 plots apart and 6, at 4 px and 8: no two drawn hulls overlap, none alongside or stern-to crosses a box or a wall",
                cases == 2 * rows && overlaps == 0 && crossings == 0, cases + " rows on four sides, " + lie + " boats alongside or stern-to");
        report("...5 apart a neighbour's hull as placed lies across every one's place alongside (a Capesize is more than 5 plots long), but each is clear stern-to: all three of every row, none alongside, none as placed",
                java.util.Arrays.equals(k5, new int[] { 0, 3 * rows, 0 }), k5[0] + " alongside, " + k5[1] + " stern-to, " + k5[2] + " as placed");
        report("...6 apart, a box and CLEAR: the first and third of a row lie alongside (a neighbour's hull as placed clears them by less than the tolerance) and the middle one stern-to, none as placed",
                java.util.Arrays.equals(k6, new int[] { 2 * rows, rows, 0 }), k6[0] + " alongside, " + k6[1] + " stern-to, " + k6[2] + " as placed");

        // THE FIRST TO ARRIVE LIES ALONGSIDE where only the second's place is crossed: 8 plots apart, between where its bow-out hull is clear and where its hull alongside is.
        int apart = 8;
        double from = (length + beam) / 2 - tol, to = length - tol;
        boolean firstLies = true, secondStern = true, clean = true, anyOrder = true;
        for (int side = 0; side < 4; side++) {
            for (double scale : BERTH_SCALES) {
                CityShore shore = berthRow(2, side, apart);
                List<BoatSchedule.Route> routes = routesOf(shore, cape.cargo());
                for (int first = 0; first < 2; first++) {
                    BoatSchedule.Boat[] g = new BoatSchedule.Boat[2];
                    g[first] = dockedOn(routes, first, cape, .5);
                    g[1 - first] = dockedOn(routes, 1 - first, cape, .6);
                    List<BoatSchedule.Boat> given = List.of(g[0], g[1]), drawn = BoatSchedule.berthed(given, routes, scale, shore);
                    firstLies &= drawn.get(first).equals(BoatSchedule.alongside(g[first], side, scale));
                    secondStern &= drawn.get(1 - first).equals(BoatSchedule.sternTo(g[1 - first], side, scale));
                    int[] au = audit(shore, given, drawn, scale);
                    clean &= au[0] == 2 && au[1] == 0 && au[2] == 0;
                    anyOrder &= BoatSchedule.berthed(List.of(g[1], g[0]), routes, scale, shore).equals(List.of(drawn.get(1), drawn.get(0)));
                }
            }
        }
        report("two Capesizes 8 plots apart, a pitch between where a bow-out hull is clear of the other's place and where two lie nose to tail: the first to arrive lies alongside, the second stern-to (its place alongside crossed), either way round",
                from < apart && apart < to && firstLies && secondStern && clean, String.format("clear from %.2f plots, crossed under %.2f", from, to));
        assertTrue("...the answer is the same whatever order the boats are handed in (the order they arrived decides, not the list's)", anyOrder);

        // THE SAME WORKS FAR APART: every one alongside.
        int far = (int) Math.ceil(length + beam) + CityShore.CLEAR;
        boolean everyOne = true, farClean = true;
        for (int side = 0; side < 4; side++) {
            for (double scale : BERTH_SCALES) {
                CityShore shore = berthRow(3, side, far);
                List<BoatSchedule.Route> routes = routesOf(shore, cape.cargo());
                List<BoatSchedule.Boat> given = List.of(dockedOn(routes, 0, cape, .5), dockedOn(routes, 1, cape, .5001), dockedOn(routes, 2, cape, .5002));
                List<BoatSchedule.Boat> drawn = BoatSchedule.berthed(given, routes, scale, shore);
                for (int i = 0; i < 3; i++) everyOne &= drawn.get(i).equals(BoatSchedule.alongside(given.get(i), side, scale));
                int[] au = audit(shore, given, drawn, scale);
                farClean &= au[0] == 3 && au[1] == 0 && au[2] == 0;
            }
        }
        report("the same three Bulk Berths spaced far apart (a Capesize's length and beam and a plot): every boat alongside, as alongside() gives it, none crossing anything",
                everyOne && farClean, "pitch " + far + " plots");

        // A LONE BERTH: alongside() itself, on each side, for each class, at three scales (slice C's case).
        Ports.Ship[] classes = { Ports.Ship.GENERAL_CARGO, Ports.Ship.MR, Ports.Ship.PANAMAX, Ports.Ship.CAPESIZE, Ports.Ship.VLCC };
        double[] lone = { BERTH_SCALES[0], BERTH_SCALES[1], MapFrame.MOST_PX_A_PLOT };
        int alone = 0;
        boolean same = true, loneClean = true;
        for (int side = 0; side < 4; side++) {
            for (Ports.Ship ship : classes) {
                for (double scale : lone) {
                    CityShore shore = berthRow(1, side, 0);
                    List<BoatSchedule.Route> routes = routesOf(shore, ship.cargo());
                    BoatSchedule.Boat b = dockedOn(routes, 0, ship, .5);
                    List<BoatSchedule.Boat> drawn = BoatSchedule.berthed(List.of(b), routes, scale, shore);
                    same &= drawn.size() == 1 && drawn.get(0).equals(BoatSchedule.alongside(b, side, scale)) && !drawn.get(0).equals(b);
                    int[] au = audit(shore, List.of(b), drawn, scale);
                    loneClean &= au[0] == 1 && au[1] == 0 && au[2] == 0;
                    alone++;
                }
            }
        }
        report("a lone berth: the boat lies alongside exactly as alongside() gives it, on each side, for five classes of ship, at three scales (slice C's case unchanged)",
                same && loneClean, alone + " boats");

        // STERN-TO: the place itself, worked from the geometry: heading the sea's direction, the stern on the face, on the berth's line out from the work, none of it in the box or the quay.
        double eps = 1e-9;
        int sternCases = 0;
        boolean sternHeading = true, sternFace = true, sternLine = true, sternKept = true, sternAshore = true, sternSame = true;
        double legShare = BoatSchedule.LEG_SECONDS / BoatSchedule.MONTH_SECONDS;
        for (int side = 0; side < 4; side++) {
            CityShore.Work w = harbourWork(1000, 1000, side);
            long[] sp = w.shorePlot();
            double mx = sp[0] + 0.5, my = sp[1] + 0.5, dx = CityShore.DX[side], dy = CityShore.DY[side], face = w.quay() + 0.5;
            double[] bp = w.berth();
            for (Ports.Ship ship : classes) {
                for (double scale : lone) {
                    BoatSchedule.Call call = new BoatSchedule.Call(ship.cargo(), ship, true, 0, 0.5);
                    BoatSchedule.Boat b = new BoatSchedule.Boat(call, bp[0], bp[1], true, true, 0.3, 1), s = BoatSchedule.sternTo(b, side, scale);
                    sternCases++;
                    sternHeading &= Math.abs(Math.cos(s.heading()) - dx) < eps && Math.abs(Math.sin(s.heading()) - dy) < eps;
                    double[][] hull = ShipShapes.hull(s, scale);
                    double least = Double.MAX_VALUE, most = -Double.MAX_VALUE;
                    for (int i = 0; i < hull[0].length; i++) {
                        double depth = (hull[0][i] - mx) * dx + (hull[1][i] - my) * dy;
                        least = Math.min(least, depth);
                        most = Math.max(most, depth);
                        sternAshore &= !(hull[0][i] > w.x0() + eps && hull[0][i] < w.x0() + w.w() - eps && hull[1][i] > w.y0() + eps && hull[1][i] < w.y0() + w.h() - eps);
                    }
                    sternFace &= Math.abs(least - face) < eps && Math.abs(most - least - ShipShapes.drawnLength(s, scale)) < eps;
                    sternLine &= Math.abs((s.x() - bp[0]) * dy - (s.y() - bp[1]) * dx) < eps;
                    sternKept &= s.call().equals(b.call()) && s.docked() && s.loaded() == b.loaded() && s.alpha() == b.alpha();
                    BoatSchedule.Boat afloat = BoatSchedule.position(call, routesOf(berthRow(1, side, 0), ship.cargo()).get(0), 0.5 - legShare / 2);
                    sternSame &= BoatSchedule.sternTo(afloat, side, scale).equals(afloat) && BoatSchedule.sternTo(b, -1, scale).equals(b)
                            && BoatSchedule.sternTo(b, CityShore.DX.length, scale).equals(b) && BoatSchedule.sternTo(b, side, MapFrame.DETAIL_FROM).equals(b)
                            && BoatSchedule.sternTo(b, side, MapFrame.L1_FROM).equals(b);
                }
            }
        }
        String stern = sternCases + " boats on four sides";
        report("stern-to: the heading is the sea's direction, the hull's stern on the quay's face (least depth the face, its length the drawn length), on the berth's line out from the work", sternHeading && sternFace && sternLine, stern);
        report("...none of the hull in the work's box, the call, load and docking as they were; the boat itself at DETAIL_FROM and below, afloat, and for a side not known", sternAshore && sternKept && sternSame, stern);

        // A BOX OR A WALL IN THE WAY: a Capesize's tip would go deeper than the tolerance into a neighbour's box, or its wall, and a smaller ship clears them.
        CityShore bare = berthRow(1, 0, 0);
        List<BoatSchedule.Route> bareRoutes = routesOf(bare, cape.cargo());
        BoatSchedule.Boat cb = dockedOn(bareRoutes, 0, cape, .5);
        double[] bp = bare.works().get(0).berth();
        long reach = (long) Math.floor(bp[0] + length / 2 - 0.8);
        CityShore boxed = berthRow(1, 0, 0), walled = berthRow(1, 0, 0);
        boxed.add(new CityShore.Work(reach, 2995, BULK_ALONG, 3, 0, 1, 0));                                    // a tank farm's box, in the ship's band
        walled.add(new CityShore.Work(reach, 2992, BULK_ALONG, BULK_DEEP, 0, 2, CityShore.HARBOUR_QUAY_PLOTS)); // a work facing the other way: its wall in the band, its box clear of it
        Ports.Ship pana = Ports.Ship.PANAMAX;
        BoatSchedule.Boat pb = dockedOn(bareRoutes, 0, pana, .5);
        boolean fallsBack = true, smallerClears = true, wallClear = true, sternClean = true;
        for (double scale : BERTH_SCALES) {
            fallsBack &= BoatSchedule.berthed(List.of(cb), bareRoutes, scale, bare).get(0).equals(BoatSchedule.alongside(cb, 0, scale));    // nothing in the way: alongside
            fallsBack &= BoatSchedule.berthed(List.of(cb), bareRoutes, scale, boxed).get(0).equals(BoatSchedule.sternTo(cb, 0, scale));
            fallsBack &= BoatSchedule.berthed(List.of(cb), bareRoutes, scale, walled).get(0).equals(BoatSchedule.sternTo(cb, 0, scale));
            sternClean &= java.util.Arrays.equals(audit(boxed, List.of(cb), BoatSchedule.berthed(List.of(cb), bareRoutes, scale, boxed), scale), new int[] { 1, 0, 0 })
                    && java.util.Arrays.equals(audit(walled, List.of(cb), BoatSchedule.berthed(List.of(cb), bareRoutes, scale, walled), scale), new int[] { 1, 0, 0 });
            smallerClears &= BoatSchedule.berthed(List.of(pb), bareRoutes, scale, boxed).get(0).equals(BoatSchedule.alongside(pb, 0, scale))
                    && BoatSchedule.berthed(List.of(pb), bareRoutes, scale, walled).get(0).equals(BoatSchedule.alongside(pb, 0, scale));
            wallClear &= walled.walls().stream().anyMatch(w -> thickness(ShipShapes.hull(BoatSchedule.alongside(cb, 0, scale), scale), box(w.x0(), w.y0(), w.w(), w.h())) > tol)
                    && thickness(ShipShapes.hull(BoatSchedule.alongside(cb, 0, scale), scale), box(reach, 2992, BULK_ALONG, BULK_DEEP)) <= tol;
        }
        report("a box in the way, or a neighbour's wall (its box clear): a Capesize lies stern-to, crossing nothing, though alone it lies alongside; a Panamax, which clears them, lies alongside",
                fallsBack && smallerClears && wallClear && sternClean, String.format("Capesize %.2f plots, Panamax %.2f", length, ShipShapes.drawnLength(pb, BERTH_SCALES[0])));

        // BOTH BLOCKED: a box on the water straight out from the berth, across the Capesize's stern-to place and its place alongside, and a Panamax's: as placed.
        CityShore blocked = berthRow(1, 0, 0);
        blocked.add(new CityShore.Work(3001, 2994, 3, 4, 0, 1, 0));                                       // a tank farm's box at the face, out from the berth's middle
        boolean asPlaced = true, causes = true;
        for (double scale : BERTH_SCALES) {
            for (BoatSchedule.Boat ship : new BoatSchedule.Boat[] { cb, pb }) {
                asPlaced &= BoatSchedule.berthed(List.of(ship), bareRoutes, scale, bare).get(0).equals(BoatSchedule.alongside(ship, 0, scale))
                        && BoatSchedule.berthed(List.of(ship), bareRoutes, scale, blocked).get(0).equals(ship);
                double[][] in = box(3001, 2994, 3, 4);
                causes &= thickness(ShipShapes.hull(BoatSchedule.alongside(ship, 0, scale), scale), in) > tol
                        && thickness(ShipShapes.hull(BoatSchedule.sternTo(ship, 0, scale), scale), in) > tol;
            }
        }
        report("a box on the water straight out from the berth, across both its place alongside and its stern-to place: a Capesize and a Panamax stay as the schedule placed them, though without it each lies alongside",
                asPlaced && causes, "both places crossed deeper than the tolerance, by the oracle");

        // A CORNER: a work facing east and one facing south, 3 plots from its wall, their ships' places crossing where the two shores meet.
        boolean cornerClean = true, eastLies = true, southAs = true;
        int[] cornerKinds = new int[3];
        for (double scale : BERTH_SCALES) {
            CityShore corner = new CityShore();
            corner.add(new CityShore.Work(3000, 3010, BULK_DEEP, BULK_ALONG, 0, 1, CityShore.HARBOUR_QUAY_PLOTS));
            corner.add(new CityShore.Work(3000 + BULK_DEEP + CityShore.HARBOUR_QUAY_PLOTS + 3, 3006, BULK_ALONG, BULK_DEEP, 0, 2, CityShore.HARBOUR_QUAY_PLOTS));
            List<BoatSchedule.Route> routes = routesOf(corner, cape.cargo());
            for (int firstEast = 0; firstEast < 2; firstEast++) {
                BoatSchedule.Boat east = dockedOn(routes, 0, cape, firstEast == 1 ? .5 : .6), south = dockedOn(routes, 1, cape, firstEast == 1 ? .6 : .5);
                List<BoatSchedule.Boat> given = List.of(east, south), drawn = BoatSchedule.berthed(given, routes, scale, corner);
                boolean cross = thickness(ShipShapes.hull(BoatSchedule.alongside(east, 1, scale), scale), ShipShapes.hull(BoatSchedule.alongside(south, 2, scale), scale)) > tol;
                int[] au = audit(corner, given, drawn, scale);
                cornerClean &= cross && au[1] == 0 && au[2] == 0;
                eastLies &= drawn.get(0).equals(BoatSchedule.alongside(east, 1, scale));
                southAs &= drawn.get(1).equals(firstEast == 1 ? BoatSchedule.sternTo(south, 2, scale) : south);
                add(cornerKinds, kinds(given, drawn, new int[] { 1, 2 }, scale));
            }
        }
        report("a corner, two works at right angles with 3 plots between a wall and a box, where the ships lying alongside would cross: no two drawn hulls overlap, none laid alongside or stern-to crosses a box or a wall",
                cornerClean, "either ship first, at 4 px and 8");
        report("...the east ship, clear of the south's place, lies alongside; the south ship lies stern-to when the east ship came first, and as placed when it came second (its stern-to place crossed by the east ship's hull as placed)",
                eastLies && southAs && java.util.Arrays.equals(cornerKinds, new int[] { 4, 2, 2 }),
                cornerKinds[0] + " alongside, " + cornerKinds[1] + " stern-to, " + cornerKinds[2] + " as placed in 4 corners");

        // BELOW THE DETAIL ZOOM, A BOAT AFLOAT, A BERTH OF NO SIDE: as they were.
        CityShore row = berthRow(2, 0, apart);
        List<BoatSchedule.Route> routes = routesOf(row, cape.cargo());
        double leg = BoatSchedule.LEG_SECONDS / BoatSchedule.MONTH_SECONDS;
        BoatSchedule.Boat docked = dockedOn(routes, 0, cape, .5);
        BoatSchedule.Call coming = new BoatSchedule.Call(cape.cargo(), cape, true, 1, .5);
        BoatSchedule.Boat afloat = BoatSchedule.position(coming, routes.get(1), .5 - leg / 2);
        List<BoatSchedule.Boat> mixed = List.of(docked, afloat);
        MapFrame mf = new MapFrame(1000, 800);
        boolean below = true;
        for (double scale : new double[] { MapFrame.DETAIL_FROM, MapFrame.L0_FROM, MapFrame.L1_FROM, mf.leastScale() }) below &= BoatSchedule.berthed(mixed, routes, scale, row) == mixed;
        List<BoatSchedule.Boat> above = BoatSchedule.berthed(mixed, routes, BERTH_SCALES[0], row);
        boolean afloatKept = !afloat.docked() && above.get(1).equals(afloat) && above.get(0).equals(BoatSchedule.alongside(docked, 0, BERTH_SCALES[0]));
        BoatSchedule.Route blind = BoatSchedule.Route.through(new BoatSchedule.Berth(cape.cargo(), routes.get(0).berth().x(), routes.get(0).berth().y()),
                routes.get(0).xs(), routes.get(0).ys(), 1, 0, 0);
        boolean noSide = BoatSchedule.berthed(List.of(docked), List.of(blind), BERTH_SCALES[0], row).get(0).equals(docked);
        report("at DETAIL_FROM and below every boat comes back as given (the list itself); above it a boat afloat is untouched, and a boat whose berth's side is not known stays as it is",
                below && afloatKept && noSide, "");

        windowFixture();
    }

    /**
     * A boat's place does not depend on the window: BoatSchedule.drawn() weighs the boats of a frame against every boat docked at that moment (docked()),
     * on a bay of berths in a row, liquid and dry bulk by turns (MR tankers beside Capesizes, so that neighbours' places cross), and a month of
     * the trade at sea, 250,000 t out a tanker berth and 750,000 t a bulk one, at every call's arrival - on a bay of 8 berths and one of 64 - and
     * its cost on the bay of 64.
     */
    static void windowFixture() {
        int pitch = BULK_ALONG + CityShore.CLEAR;
        double scale = BERTH_SCALES[1];
        for (int berths : new int[] { 8, 64 }) {
            CityShore bay = berthRow(berths, 0, pitch);
            List<BoatSchedule.Route> routes = routesOf(bay, Ports.Cargo.LIQUID, Ports.Cargo.DRY_BULK);
            BoatSchedule month = BoatSchedule.of(4000, MapCheck.tradeAtSea(berths * 125_000.0, berths * 375_000.0, 0, 0), routes, Ports.BULK_TERMINAL_SHIPS);
            long x0 = 3000 - 10, x1 = 3000 + (long) berths * pitch + 10, y0 = 2900, y1 = 3010;
            List<long[]> windows = new ArrayList<>();
            windows.add(new long[] { x0, y0, x1, y1 });                                           // the whole bay
            windows.add(new long[] { x0, y0, (x0 + x1) / 2, y1 });                                // its west half
            windows.add(new long[] { (x0 + x1) / 2, y0, x1, y1 });                                // its east half
            for (int k = 0; k < berths; k += 3) windows.add(new long[] { 3000 + (long) k * pitch, 2930, 3000 + (long) (k + 1) * pitch, y1 });   // one berth's width
            for (int k = 0; k < berths; k += 5) windows.add(new long[] { 3000 + (long) k * pitch, 2930, 3000 + (long) (k + 4) * pitch, y1 });   // four berths'
            int together = 0, differs = 0, boatsSeen = 0;
            boolean sameSet = true, stable = true, belowSame = true;
            List<BoatSchedule.Call> calls = month.calls();
            for (BoatSchedule.Call c : calls) {
                double t = c.arrives();
                List<BoatSchedule.Boat> all = month.docked(t);
                java.util.Set<BoatSchedule.Boat> expect = new java.util.HashSet<>();
                for (BoatSchedule.Call k : calls) {
                    BoatSchedule.Boat b = BoatSchedule.position(k, routes.get(k.route()), t);
                    if (b != null && b.docked()) expect.add(b);
                }
                sameSet &= all.size() == expect.size() && expect.containsAll(all) && all.contains(BoatSchedule.position(c, routes.get(c.route()), t));
                java.util.Map<BoatSchedule.Call, BoatSchedule.Boat> laid = new java.util.HashMap<>();
                List<BoatSchedule.Boat> placed = BoatSchedule.berthed(all, routes, scale, bay);
                for (int i = 0; i < all.size(); i++) laid.put(all.get(i).call(), placed.get(i));
                if (all.size() > 1) together++;
                for (long[] w : windows) {
                    List<BoatSchedule.Boat> frame = month.frame(w[0], w[1], w[2], w[3], t), drawn = month.drawn(frame, t, scale, bay);
                    belowSame &= month.drawn(frame, t, MapFrame.DETAIL_FROM, bay) == frame;
                    List<BoatSchedule.Boat> alone = BoatSchedule.berthed(frame, routes, scale, bay);
                    stable &= drawn.size() == frame.size();
                    for (int i = 0; i < frame.size() && i < drawn.size(); i++) {
                        BoatSchedule.Boat b = frame.get(i);
                        if (!b.docked()) {
                            stable &= drawn.get(i).equals(b);
                            continue;
                        }
                        boatsSeen++;
                        stable &= drawn.get(i).equals(laid.get(b.call()));
                        if (!alone.get(i).equals(drawn.get(i))) differs++;
                    }
                }
            }
            report("the window, a bay of " + berths + " berths: docked(t) is every boat at a quay at t, and a frame's boats drawn() are the same boats whichever part of the bay is on screen",
                    sameSet && stable && belowSame && boatsSeen > 0,
                    calls.size() + " arrivals, " + together + " with two boats or more at a quay, " + boatsSeen + " docked boats seen through " + windows.size() + " windows each");
            assertTrue("...berthed() over a window's boats alone would have turned some of them (the window matters, so drawn() weighs every boat docked): " + differs + " boats", differs > 0);

            if (berths < 64) continue;
            // THE COST: a frame's boats drawn over the bay of 64, the frame's own search included, within a frame's boat budget.
            int frames = Math.min(calls.size(), 400);
            double best = Double.MAX_VALUE;
            long shown = 0;
            for (int round = 0; round < MapCheck.WARM_ROUNDS + MapCheck.TIMED_ROUNDS; round++) {
                long t0 = System.nanoTime();
                shown = 0;
                for (int i = 0; i < frames; i++) {
                    double t = calls.get(i * calls.size() / frames).arrives();
                    shown += month.drawn(month.frame(windows.get(0)[0], windows.get(0)[1], windows.get(0)[2], windows.get(0)[3], t), t, scale, bay).size();
                }
                double ms = (System.nanoTime() - t0) / 1e6 / frames;
                if (round >= MapCheck.WARM_ROUNDS) best = Math.min(best, ms);
            }
            timed("a frame's boats drawn() over the whole bay of 64 berths take no more than MapCheck.BOAT_FRAME_MS (0.5 ms)",
                    Timing.within(best, MapCheck.BOAT_FRAME_MS) && shown > 0, String.format("%.3f ms a frame, %,d boats over %d frames", best, shown, frames));
        }
    }

    /* ----- 11. THE YARD'S PICTURE (HB2, 2026-11-09) ----- */

    /** The fixture's boxes, {along the shore, deep} in plots: a Bulk Berth's (BULK_ALONG by BULK_DEEP) and a large terminal's, deep enough for a loop. */
    static final int[][] YARD_BOXES = { { BULK_ALONG, BULK_DEEP }, { 16, 15 } };

    /** What a box of YARD_BOXES is called in a line. */
    static final String[] YARD_BOX_NAMES = { "small", "large" };

    /** The kinds of a terminal, whose yards fill with stock. */
    static final HarbourYard.Kind[] PORT_KINDS = { HarbourYard.Kind.CONTAINER, HarbourYard.Kind.DRY_BULK, HarbourYard.Kind.LIQUID, HarbourYard.Kind.GENERAL };

    /** The rail bits of a picture whose quay track runs on past both ends of its box. */
    static final int RAIL_OPEN = HarbourYard.RAIL | HarbourYard.OPEN_LOW | HarbourYard.OPEN_HIGH;

    /** A work of the fixture: box `box` of YARD_BOXES at (x0, y0), of type `type`, the harbour's quay, the sea on `side`. */
    static CityShore.Work yardWork(long x0, long y0, int box, int side, int type) {
        boolean ns = side % 2 == 0;
        int along = YARD_BOXES[box][0], deep = YARD_BOXES[box][1];
        return new CityShore.Work(x0, y0, ns ? along : deep, ns ? deep : along, type, side, CityShore.HARBOUR_QUAY_PLOTS);
    }

    /**
     * A picture's geometry for a sea side: its pixels `w` by `h`, read in the shore's own axes - `a` along the shore (x for a sea side of
     * north or south, else y, so a small `a` is the LOW end) and `c` across it (the other).
     */
    record YardGrid(int side, int w, int h) {
        static YardGrid of(CityShore.Work k) { return new YardGrid(k.side(), k.w() * HarbourYard.PX, k.h() * HarbourYard.PX); }

        int along() { return side % 2 == 0 ? w : h; }

        int across() { return side % 2 == 0 ? h : w; }

        int at(int[] pic, int a, int c) { return side % 2 == 0 ? pic[c * w + a] : pic[a * w + c]; }

        /** The first c of the `rows` sea-most plot rows. */
        int seaFrom(int rows) { return side == 0 || side == 3 ? 0 : across() - rows * HarbourYard.PX; }

        /** The c of the land edge line. */
        int land() { return side == 0 || side == 3 ? across() - 1 : 0; }

        /** The c of the pixel row v out from the land side. */
        int cOfV(int v) { return side == 0 || side == 3 ? across() - 1 - v : v; }

        /** The a of pixel i of a picture. */
        int aOf(int i) { return side % 2 == 0 ? i % w : i / w; }

        /** The c of pixel i of a picture. */
        int cOf(int i) { return side % 2 == 0 ? i / w : i % w; }
    }

    /** Whether a pixel is a track's: its ballast, a sleeper or a steel rail. */
    static boolean onTrack(int argb) { return argb == TileRaster.BALLAST || argb == TileRaster.SLEEPER || argb == TileRaster.STEEL; }

    /** The offsets from `from` of the pixels of a line of a picture that are steel (`onlySteel`) or any track's: the line across at c = `fixed` (`acrossLine`, p running along the shore), else along at a = `fixed`. */
    static List<Integer> yardLine(YardGrid g, int[] pic, boolean acrossLine, int fixed, int from, int to, boolean onlySteel) {
        List<Integer> at = new ArrayList<>();
        for (int p = from; p < to; p++) {
            int argb = acrossLine ? g.at(pic, p, fixed) : g.at(pic, fixed, p);
            if (onlySteel ? argb == TileRaster.STEEL : onTrack(argb)) at.add(p - from);
        }
        return at;
    }

    /** Whether two pictures agree on every pixel of the part a in [a0, a1), c in [c0, c1). */
    static boolean yardSame(YardGrid g, int[] x, int[] y, int a0, int a1, int c0, int c1) {
        for (int a = a0; a < a1; a++) for (int c = c0; c < c1; c++) if (g.at(x, a, c) != g.at(y, a, c)) return false;
        return true;
    }

    /** Which pixels of two pictures differ. */
    static boolean[] yardDiff(int[] a, int[] b) {
        boolean[] d = new boolean[a.length];
        for (int i = 0; i < a.length; i++) d[i] = a[i] != b[i];
        return d;
    }

    /** How far a picture is from a base, in all: the channels' differences summed. */
    static long yardFar(int[] a, int[] base) {
        long sum = 0;
        for (int i = 0; i < a.length; i++) {
            for (int sh = 0; sh < 24; sh += 8) sum += Math.abs(((a[i] >> sh) & 255) - ((base[i] >> sh) & 255));
        }
        return sum;
    }

    /** The first case of a loop that fails, kept: `was` if there is one, else `what` when `ok` is not. */
    static String yardWhy(String was, boolean ok, String what) { return was != null || ok ? was : what; }

    /** A case's words: the box, the sea side, and the kind. */
    static String yardCase(int box, int side, Object kind) { return YARD_BOX_NAMES[box] + " box, sea side " + side + ", " + kind; }

    static void theYardsPicture() {
        out.println("\n--- 11. the yard's picture (HB2): a work's box painted, its stock by the level, the quay track, lead and train by the rail bits, the cells between, a type's kind, a kind's busy ---");
        yardShape();
        yardFills();
        yardSteps();
        yardQuayTrack();
        yardLead();
        yardTrain();
        yardCells();
        yardKinds();
        yardBusy();
        yardCost();
    }

    /** The picture is the box's pixels, opaque, the same each call, and the work's own: another place or another type paints another. */
    static void yardShape() {
        int cases = 0;
        String why = null, seed = null;
        for (int box = 0; box < YARD_BOXES.length; box++) {
            for (int side = 0; side < 4; side++) {
                CityShore.Work k = yardWork(3000, 3000, box, side, 7);
                for (HarbourYard.Kind kind : HarbourYard.Kind.values()) {
                    int bits = RAIL_OPEN | HarbourYard.TRAIN;
                    int[] p = HarbourYard.picture(k, kind, HarbourYard.LEVELS / 2, bits);
                    boolean opaque = true;
                    for (int px : p) opaque &= (px >>> 24) == 0xff;
                    String at = yardCase(box, side, kind);
                    why = yardWhy(why, p.length == k.w() * HarbourYard.PX * k.h() * HarbourYard.PX, at + ": " + p.length + " pixels for a box of " + k.w() + " by " + k.h() + " plots");
                    why = yardWhy(why, opaque, at + ": a pixel is not opaque");
                    why = yardWhy(why, Arrays.equals(p, HarbourYard.picture(k, kind, HarbourYard.LEVELS / 2, bits)), at + ": not the same on a second call");
                    cases++;
                }
                for (HarbourYard.Kind kind : new HarbourYard.Kind[] { HarbourYard.Kind.CONTAINER, HarbourYard.Kind.DRY_BULK, HarbourYard.Kind.GENERAL }) {         // the kinds that scatter: tanks stand on a plain grid
                    int[] here = HarbourYard.picture(k, kind, HarbourYard.LEVELS, 0);
                    seed = yardWhy(seed, !Arrays.equals(here, HarbourYard.picture(yardWork(3037, 3011, box, side, 7), kind, HarbourYard.LEVELS, 0)), yardCase(box, side, kind) + ": the same box 37 plots east and 11 south is the same picture");
                    seed = yardWhy(seed, !Arrays.equals(here, HarbourYard.picture(yardWork(3000, 3000, box, side, 8), kind, HarbourYard.LEVELS, 0)), yardCase(box, side, kind) + ": the same box of another type is the same picture");
                }
            }
        }
        report("a picture is w*PX by h*PX, opaque and the same on a second call, for each of the five kinds, a small box and a large, on all four sea sides", why == null, why == null ? cases + " pictures" : why);
        report("...and the work's own: the same box at another place, or of another type, is another picture (the seed), for the yards that scatter: boxes, heaps and crates", seed == null, seed == null ? "" : seed);
    }

    /** The yard fills: each step's stock holds the step before's, bare and with the tracks and a train; a tank farm shows no level and no rail; below nothing is bare, above LEVELS is full. */
    static void yardFills() {
        int cases = 0;
        String why = null, farm = null, clampWhy = null;
        for (int bits : new int[] { 0, RAIL_OPEN | HarbourYard.LEAD_HIGH | HarbourYard.TRAIN }) {
            for (int box = 0; box < YARD_BOXES.length; box++) {
                for (int side = 0; side < 4; side++) {
                    CityShore.Work k = yardWork(3000, 3000, box, side, 7);
                    for (HarbourYard.Kind kind : PORT_KINDS) {
                        int[][] pics = new int[HarbourYard.LEVELS + 1][];
                        for (int lv = 0; lv <= HarbourYard.LEVELS; lv++) pics[lv] = HarbourYard.picture(k, kind, lv, bits);
                        String at = yardCase(box, side, kind) + (bits == 0 ? ", bare" : ", with its tracks");
                        int first = -1;
                        for (int lv = 1; lv <= HarbourYard.LEVELS && first < 0; lv++) {
                            for (int i = 0; i < pics[0].length; i++) {
                                if (pics[lv - 1][i] != pics[0][i] && pics[lv][i] == pics[0][i]) {
                                    first = lv;
                                    break;
                                }
                            }
                        }
                        int some = 0;
                        for (boolean d : yardDiff(pics[1], pics[0])) if (d) some++;
                        why = yardWhy(why, first < 0, at + ": step " + first + " has bare ground where step " + (first - 1) + " had stock");
                        why = yardWhy(why, some > 0, at + ": step 1 shows no stock");
                        why = yardWhy(why, yardFar(pics[HarbourYard.LEVELS], pics[0]) > yardFar(pics[1], pics[0]), at + ": step LEVELS shows no more stock than step 1");
                        clampWhy = yardWhy(clampWhy, Arrays.equals(HarbourYard.picture(k, kind, -3, bits), pics[0]) && Arrays.equals(HarbourYard.picture(k, kind, HarbourYard.LEVELS + 5, bits), pics[HarbourYard.LEVELS]),
                                at + ": a level below nothing is not step 0, or one above LEVELS is not step LEVELS");
                        cases++;
                    }
                    int[] bare = HarbourYard.picture(k, HarbourYard.Kind.TANKS, 0, 0);
                    boolean still = true;
                    for (int lv = 0; lv <= HarbourYard.LEVELS; lv++) still &= Arrays.equals(HarbourYard.picture(k, HarbourYard.Kind.TANKS, lv, bits), bare);
                    farm = yardWhy(farm, still, yardCase(box, side, HarbourYard.Kind.TANKS) + ": the picture moves with the level or the rail bits");
                    int tanks = 0;
                    for (boolean d : yardDiff(bare, HarbourYard.picture(k, null, 0, 0))) if (d) tanks++;
                    farm = yardWhy(farm, tanks > 0, yardCase(box, side, HarbourYard.Kind.TANKS) + ": no tank on the ground");
                }
            }
        }
        report("the yard fills: for each port kind, stock at step k (the pixels that differ from step 0's) holds step k-1's, step 1 has some and step LEVELS shows more, bare and with the tracks, a small box and a large, four sides",
                why == null, why == null ? cases + " yards over " + HarbourYard.LEVELS + " steps" : why);
        report("...a level below nothing is step 0 and one above LEVELS is step LEVELS", clampWhy == null, clampWhy == null ? "" : clampWhy);
        report("...a tank farm (TANKS) has its tanks on the ground and shows the same picture at every step and with every rail bit", farm == null, farm == null ? "" : farm);
    }

    /** step() shows any cargo at all and never falls as busy rises. */
    static void yardSteps() {
        int top = HarbourYard.LEVELS;
        boolean ends = HarbourYard.step(0) == 0 && HarbourYard.step(-0.5) == 0 && HarbourYard.step(Double.NaN) == 0;
        boolean any = true;
        for (double busy = 1e-12; busy < 0.1; busy *= 3) any &= HarbourYard.step(busy) >= 1;
        boolean full = HarbourYard.step(1) == top && HarbourYard.step(1.7) == top && HarbourYard.step(1e9) == top && HarbourYard.step(Double.POSITIVE_INFINITY) == top;
        boolean rises = true;
        boolean[] seen = new boolean[top + 1];
        int last = 0;
        for (int i = 0; i <= 2000; i++) {
            int s = HarbourYard.step(i / 1000.0);
            rises &= s >= last && s >= 0 && s <= top;
            seen[Math.max(0, Math.min(top, s))] = true;
            last = s;
        }
        boolean all = true;
        for (boolean s : seen) all &= s;
        report("step(): 0 at no busy, at least 1 for any busy above 0 however small, LEVELS at 1 and beyond", ends && any && full, "step(0.01) = " + HarbourYard.step(0.01) + ", step(1) = " + HarbourYard.step(1));
        report("...never falling as busy rises from 0 to 2, and every step from 0 to LEVELS is the step of some busy", rises && all, "");
    }

    /** The quay track runs to a box's edge where it is open and stops short at a buffer where it is not; OPEN_x paints only in its end's plot; no RAIL, no track; no stock on it. */
    static void yardQuayTrack() {
        int cases = 0;
        String edges = null, stops = null, ends = null, bare = null, whole = null, stock = null;
        int px = HarbourYard.PX, lv = HarbourYard.LEVELS;
        for (int box = 0; box < YARD_BOXES.length; box++) {
            for (int side = 0; side < 4; side++) {
                CityShore.Work k = yardWork(3000, 3000, box, side, 7);
                YardGrid g = YardGrid.of(k);
                int s0 = g.seaFrom(1), last = g.along() - 1, rows = YARD_BOXES[box][1] >= HarbourYard.LOOP_PLOTS ? 2 : 1;
                for (HarbourYard.Kind kind : PORT_KINDS) {
                    String at = yardCase(box, side, kind);
                    int[] open = HarbourYard.picture(k, kind, lv, RAIL_OPEN), shut = HarbourYard.picture(k, kind, lv, HarbourYard.RAIL);
                    int[] lowOnly = HarbourYard.picture(k, kind, lv, HarbourYard.RAIL | HarbourYard.OPEN_LOW), highOnly = HarbourYard.picture(k, kind, lv, HarbourYard.RAIL | HarbourYard.OPEN_HIGH);
                    List<Integer> lowRails = yardLine(g, open, false, 0, s0, s0 + px, true), highRails = yardLine(g, open, false, last, s0, s0 + px, true);
                    edges = yardWhy(edges, !lowRails.isEmpty() && !highRails.isEmpty(), at + ": an open box's LOW or HIGH edge line holds no steel in the sea-most row");
                    edges = yardWhy(edges, lowRails.equals(highRails), at + ": the rails are at " + lowRails + " on the LOW edge and " + highRails + " on the HIGH");
                    boolean through = true;
                    for (int a = 0; a <= last; a++) for (int r : lowRails) through &= g.at(open, a, s0 + r) == TileRaster.STEEL;
                    whole = yardWhy(whole, through, at + ": a rail of the quay track breaks between the box's two edges");
                    stops = yardWhy(stops, yardLine(g, shut, false, 0, s0, s0 + px, true).isEmpty() && yardLine(g, shut, false, last, s0, s0 + px, true).isEmpty(), at + ": a track that is not open reaches the edge of the box");
                    stops = yardWhy(stops, yardLine(g, lowOnly, false, last, s0, s0 + px, true).isEmpty() && yardLine(g, highOnly, false, 0, s0, s0 + px, true).isEmpty(), at + ": OPEN_LOW opens the HIGH end or OPEN_HIGH the LOW");
                    stops = yardWhy(stops, !yardLine(g, lowOnly, false, 0, s0, s0 + px, true).isEmpty() && !yardLine(g, highOnly, false, last, s0, s0 + px, true).isEmpty(), at + ": OPEN_LOW or OPEN_HIGH leaves its end shut");
                    boolean lowIn = false, highIn = false, lowOut = true, highOut = true;
                    boolean[] dl = yardDiff(lowOnly, shut), dh = yardDiff(highOnly, shut);
                    for (int i = 0; i < dl.length; i++) {
                        if (dl[i]) {
                            lowIn = true;
                            lowOut &= g.aOf(i) < px;
                        }
                        if (dh[i]) {
                            highIn = true;
                            highOut &= g.aOf(i) >= g.along() - px;
                        }
                    }
                    ends = yardWhy(ends, lowIn && highIn && lowOut && highOut, at + ": OPEN_LOW or OPEN_HIGH changes nothing, or pixels beyond the first plot along the shore (the last)");
                    int[] plain = HarbourYard.picture(k, kind, lv, 0);
                    int[] inert = HarbourYard.picture(k, kind, lv, HarbourYard.OPEN_LOW | HarbourYard.OPEN_HIGH | HarbourYard.LEAD_LOW | HarbourYard.LEAD_HIGH | HarbourYard.TRAIN);
                    bare = yardWhy(bare, Arrays.equals(plain, inert), at + ": rail bits without RAIL change the picture");
                    bare = yardWhy(bare, yardLine(g, plain, false, 0, s0, s0 + px, true).isEmpty() && yardLine(g, plain, false, last, s0, s0 + px, true).isEmpty(), at + ": steel at the edges of the sea-most row with no RAIL");
                    int[] empty = HarbourYard.picture(k, kind, 0, RAIL_OPEN);
                    int rowsFrom = g.seaFrom(rows);
                    stock = yardWhy(stock, yardSame(g, empty, open, 0, g.along(), rowsFrom, rowsFrom + rows * px), at + ": stock stands in the rows of the track");
                    cases++;
                }
            }
        }
        report("the quay track (RAIL | OPEN_LOW | OPEN_HIGH): the box's LOW and HIGH edge lines hold steel in the sea-most plot row, at the same offsets, and the rails run unbroken between them",
                edges == null && whole == null, edges != null ? edges : whole != null ? whole : cases + " pictures");
        report("...with RAIL alone neither edge holds any (the buffer stops); OPEN_LOW opens the LOW end only, OPEN_HIGH the HIGH", stops == null, stops == null ? "" : stops);
        report("...OPEN_LOW changes pixels only within the first plot along the shore and OPEN_HIGH only within the last", ends == null, ends == null ? "" : ends);
        report("...without RAIL no rail bit changes a picture and the sea-most row holds no track at the edges", bare == null, bare == null ? "" : bare);
        report("...no stock stands in the track's rows: the sea-most plot row, and at a box 10 plots deep the two, are the same at step 0 and at LEVELS", stock == null, stock == null ? "" : stock);
    }

    /** The lead comes up the first or last plot column from the land edge to the quay track, and no stock stands in its column. */
    static void yardLead() {
        int cases = 0;
        String why = null, joined = null, neither = null, stock = null;
        int px = HarbourYard.PX, lv = HarbourYard.LEVELS;
        for (int box = 0; box < YARD_BOXES.length; box++) {
            for (int side = 0; side < 4; side++) {
                CityShore.Work k = yardWork(3000, 3000, box, side, 7);
                YardGrid g = YardGrid.of(k);
                int lowFrom = 0, highFrom = g.along() - px, v1 = g.across() - 5;        // the quay track's middle lies 5 px from the sea
                for (HarbourYard.Kind kind : PORT_KINDS) {
                    String at = yardCase(box, side, kind);
                    int[] none = HarbourYard.picture(k, kind, lv, HarbourYard.RAIL), low = HarbourYard.picture(k, kind, lv, HarbourYard.RAIL | HarbourYard.LEAD_LOW);
                    int[] high = HarbourYard.picture(k, kind, lv, HarbourYard.RAIL | HarbourYard.LEAD_HIGH);
                    neither = yardWhy(neither, yardLine(g, none, true, g.land(), 0, g.along(), true).isEmpty(), at + ": steel on the land edge line with no lead");
                    List<Integer> inLow = yardLine(g, low, true, g.land(), lowFrom, lowFrom + px, true), inHigh = yardLine(g, high, true, g.land(), highFrom, highFrom + px, true);
                    why = yardWhy(why, !inLow.isEmpty() && yardLine(g, low, true, g.land(), lowFrom + px, g.along(), true).isEmpty(), at + ": LEAD_LOW's steel is not in the LOW column's plot alone");
                    why = yardWhy(why, !inHigh.isEmpty() && yardLine(g, high, true, g.land(), 0, highFrom, true).isEmpty(), at + ": LEAD_HIGH's steel is not in the HIGH column's plot alone");
                    boolean up = true;
                    for (int r : inLow) for (int v = 0; v < v1; v++) up &= g.at(low, lowFrom + r, g.cOfV(v)) == TileRaster.STEEL;
                    for (int r : inHigh) for (int v = 0; v < v1; v++) up &= g.at(high, highFrom + r, g.cOfV(v)) == TileRaster.STEEL;
                    joined = yardWhy(joined, up, at + ": a lead's rail breaks before it reaches the quay track");
                    int[] emptyLow = HarbourYard.picture(k, kind, 0, HarbourYard.RAIL | HarbourYard.LEAD_LOW), emptyHigh = HarbourYard.picture(k, kind, 0, HarbourYard.RAIL | HarbourYard.LEAD_HIGH);
                    stock = yardWhy(stock, yardSame(g, emptyLow, low, lowFrom, lowFrom + px, 0, g.across()) && yardSame(g, emptyHigh, high, highFrom, highFrom + px, 0, g.across()), at + ": stock stands in a lead's column");
                    cases++;
                }
            }
        }
        report("the lead (RAIL | LEAD_LOW, LEAD_HIGH): the land edge line holds steel inside the LOW column's plot (the HIGH's) and nowhere else, and none with no lead",
                why == null && neither == null, why != null ? why : neither != null ? neither : cases + " pictures");
        report("...its rails run unbroken from the land edge to the quay track's land-side rail, which they join", joined == null, joined == null ? "" : joined);
        report("...and the yard gives its column up: the lead's plot column is the same at step 0 and at LEVELS", stock == null, stock == null ? "" : stock);
    }

    /** A train changes pixels only on the track's rows, and only with RAIL. */
    static void yardTrain() {
        int cases = 0;
        String why = null, none = null;
        int px = HarbourYard.PX, lv = HarbourYard.LEVELS;
        for (int box = 0; box < YARD_BOXES.length; box++) {
            for (int side = 0; side < 4; side++) {
                CityShore.Work k = yardWork(3000, 3000, box, side, 7);
                YardGrid g = YardGrid.of(k);
                int rows = YARD_BOXES[box][1] >= HarbourYard.LOOP_PLOTS ? 2 : 1, from = g.seaFrom(rows);
                for (HarbourYard.Kind kind : PORT_KINDS) {
                    String at = yardCase(box, side, kind);
                    for (int bits : new int[] { RAIL_OPEN, HarbourYard.RAIL | HarbourYard.OPEN_LOW | HarbourYard.LEAD_HIGH }) {
                        boolean[] d = yardDiff(HarbourYard.picture(k, kind, lv, bits | HarbourYard.TRAIN), HarbourYard.picture(k, kind, lv, bits));
                        boolean any = false, inside = true;
                        for (int i = 0; i < d.length; i++) {
                            if (!d[i]) continue;
                            any = true;
                            inside &= g.cOf(i) >= from && g.cOf(i) < from + rows * px;
                        }
                        why = yardWhy(why, any && inside, at + ": the train " + (any ? "paints outside the " + rows + " sea-most plot rows" : "paints nothing"));
                    }
                    none = yardWhy(none, Arrays.equals(HarbourYard.picture(k, kind, lv, HarbourYard.TRAIN), HarbourYard.picture(k, kind, lv, 0)), at + ": TRAIN without RAIL changes the picture");
                    cases++;
                }
            }
        }
        report("a train (TRAIN with RAIL) changes pixels, and only inside the track rows: the sea-most plot row, and at a box 10 plots deep the two", why == null, why == null ? cases + " boxes" : why);
        report("...and without RAIL TRAIN changes nothing", none == null, none == null ? "" : none);
    }

    /** gapCell() and leadCell() are the track and the lead at the offsets across the plot that the picture has them. */
    static void yardCells() {
        String shape = null, gap = null, lead = null;
        int px = HarbourYard.PX, lv = HarbourYard.LEVELS;
        for (int side = 0; side < 4; side++) {
            YardGrid cell = new YardGrid(side, px, px);
            int[] gapCell = HarbourYard.gapCell(side);
            int[][] leads = { HarbourYard.leadCell(side, false), HarbourYard.leadCell(side, true) };
            for (int[] c : new int[][] { gapCell, leads[0], leads[1] }) {
                boolean clear = false, track = false, opaque = true;
                for (int argb : c) {
                    clear |= argb == 0;
                    track |= argb != 0;
                    opaque &= argb == 0 || (argb >>> 24) == 0xff;
                }
                shape = yardWhy(shape, c.length == px * px && clear && track && opaque, "sea side " + side + ": a cell is not PX by PX, transparent off the track and opaque on it");
            }
            for (int box = 0; box < YARD_BOXES.length; box++) {
                CityShore.Work k = yardWork(3000, 3000, box, side, 7);
                YardGrid g = YardGrid.of(k);
                int s0 = g.seaFrom(1), last = g.along() - 1;
                String at = yardCase(box, side, "its cells");
                int[] open = HarbourYard.picture(k, HarbourYard.Kind.CONTAINER, lv, RAIL_OPEN);
                for (int[] line : new int[][] { { 0, 0 }, { last, px - 1 } }) {
                    List<Integer> steel = yardLine(g, open, false, line[0], s0, s0 + px, true), bed = yardLine(g, open, false, line[0], s0, s0 + px, false);
                    List<Integer> cellSteel = yardLine(cell, gapCell, false, line[1], 0, px, true), cellBed = new ArrayList<>();
                    for (int c = 0; c < px; c++) if (cell.at(gapCell, line[1], c) != 0) cellBed.add(c);
                    gap = yardWhy(gap, !steel.isEmpty() && steel.equals(cellSteel), at + ": the gap cell's rails at " + cellSteel + " on its edge line " + line[1] + ", the picture's at " + steel);
                    gap = yardWhy(gap, bed.equals(cellBed), at + ": the gap cell's track at " + cellBed + " on its edge line " + line[1] + ", the picture's at " + bed);
                }
                for (int h = 0; h < 2; h++) {
                    boolean high = h == 1;
                    int bits = HarbourYard.RAIL | (high ? HarbourYard.LEAD_HIGH : HarbourYard.LEAD_LOW), plot = high ? g.along() - px : 0;
                    int[] pic = HarbourYard.picture(k, HarbourYard.Kind.CONTAINER, lv, bits);
                    List<Integer> steel = yardLine(g, pic, true, g.land(), plot, plot + px, true), bed = yardLine(g, pic, true, g.land(), plot, plot + px, false);
                    for (int cLine : new int[] { cell.land(), cell.cOfV(px - 1) }) {
                        List<Integer> cellSteel = yardLine(cell, leads[h], true, cLine, 0, px, true), cellBed = new ArrayList<>();
                        for (int a = 0; a < px; a++) if (cell.at(leads[h], a, cLine) != 0) cellBed.add(a);
                        lead = yardWhy(lead, !steel.isEmpty() && steel.equals(cellSteel), at + ": the " + (high ? "HIGH" : "LOW") + " lead cell's rails at " + cellSteel + " on the line across " + cLine + ", the picture's at " + steel);
                        lead = yardWhy(lead, bed.equals(cellBed), at + ": the " + (high ? "HIGH" : "LOW") + " lead cell's track at " + cellBed + " on the line across " + cLine + ", the picture's at " + bed);
                    }
                }
            }
        }
        report("gapCell() and leadCell() are PX by PX, transparent (0) off the track and opaque on it, on each side", shape == null, shape == null ? "" : shape);
        report("a gap cell's two shore-wise edge lines hold steel at the offsets of an open picture's edge line in its sea-most row, and its track where the picture's is", gap == null, gap == null ? "" : gap);
        report("a lead cell's land-side and sea-side edge lines hold steel at the offsets of the picture's land edge line in the LOW (HIGH) lead's column, and its track where the picture's is", lead == null, lead == null ? "" : lead);
    }

    /** kindOf() reads a type by its berth's cargo, a tank farm by its shore, and nothing else. */
    static void yardKinds() {
        BuildingVisual.Type[] types = BuildingVisual.table(new BuildingCatalog().load());
        int terminals = 0, farms = 0, others = 0;
        boolean[] kinds = new boolean[PORT_KINDS.length];
        String why = null;
        for (BuildingVisual.Type t : types) {
            if (t == null) continue;
            HarbourYard.Kind kind = HarbourYard.kindOf(t);
            if (t.berth() >= 0) {
                terminals++;
                HarbourYard.Kind expect = HarbourYard.Kind.valueOf(Ports.Cargo.values()[t.berth()].name());
                why = yardWhy(why, kind == expect, "type " + t.id() + " (berth of " + Ports.Cargo.values()[t.berth()] + ") is " + kind);
                for (int i = 0; i < PORT_KINDS.length; i++) kinds[i] |= kind == PORT_KINDS[i];
            } else if (t.shore()) {
                farms++;
                why = yardWhy(why, kind == HarbourYard.Kind.TANKS, "type " + t.id() + " (a tank farm) is " + kind);
            } else {
                others++;
                why = yardWhy(why, kind == null, "type " + t.id() + " (neither) is " + kind);
            }
        }
        boolean every = true;
        for (boolean s : kinds) every &= s;
        report("kindOf(): each terminal of the catalogue is the kind of its berth's cargo, a tank farm (shore()) is TANKS, any other type is null, and null is null",
                why == null && every && farms > 0 && HarbourYard.kindOf(null) == null, why != null ? why : terminals + " terminals of all four kinds, " + farms + " tank farms, " + others + " other types");
    }

    /** The first good of a kind that is not crude, as MapCheck.tradeAtSea() takes it. */
    static Good yardGood(Ports.Cargo c) {
        for (Good g : Good.values()) if (g != Good.CRUDE && g.cargo() == c) return g;
        return null;
    }

    /** Ports.busy() is the month's sea tonnes both ways over the berths', 1 when over, 0 with no berth. */
    static void yardBusy() {
        double[] berths = new double[Ports.Cargo.values().length];
        berths[Ports.Cargo.LIQUID.ordinal()] = 300;
        berths[Ports.Cargo.DRY_BULK.ordinal()] = 400;
        berths[Ports.Cargo.GENERAL.ordinal()] = 100;
        Ports p = MapCheck.tradeAtSea(100, 400, 300, 50);              // liquid 100 out, dry bulk 400 out (what its berths handle), general 300 out (three times), containers 50 out with no berth
        p.tally(yardGood(Ports.Cargo.LIQUID), 50, 0, 1);               // and liquid 50 in: both ways, 150 of 300
        double eps = 1e-9;
        boolean half = Math.abs(p.busy(Ports.Cargo.LIQUID, berths) - 0.5) < eps;
        boolean exact = Math.abs(p.busy(Ports.Cargo.DRY_BULK, berths) - 1) < eps;
        boolean over = Math.abs(p.busy(Ports.Cargo.GENERAL, berths) - 1) < eps;
        boolean none = p.busy(Ports.Cargo.CONTAINER, berths) == 0 && p.busy(Ports.Cargo.CONTAINER, null) == 0 && p.busy(Ports.Cargo.LIQUID, new double[0]) == 0;
        boolean quiet = MapCheck.tradeAtSea(0, 0, 0, 0).busy(Ports.Cargo.LIQUID, berths) == 0;
        report("Ports.busy(): a kind's sea tonnes both ways over its berths' month - 150 of 300 t is 0.5, exactly the berths' is 1, three times is 1, no berth standing is 0, a month with no trade is 0",
                half && exact && over && none && quiet,
                String.format("liquid %.3f, dry bulk %.3f, general %.3f", p.busy(Ports.Cargo.LIQUID, berths), p.busy(Ports.Cargo.DRY_BULK, berths), p.busy(Ports.Cargo.GENERAL, berths)));
        boolean never = true;
        double before = 0;
        for (int i = 0; i < 8; i++) {
            double b = MapCheck.tradeAtSea(0, 80.0 * i, 0, 0).busy(Ports.Cargo.DRY_BULK, berths);
            never &= b >= before && b >= 0 && b <= 1;
            before = b;
        }
        report("...and never falls as the tonnes rise nor leaves 0 to 1; with step() the kind at its berths' month shows full", never && HarbourYard.step(p.busy(Ports.Cargo.DRY_BULK, berths)) == HarbourYard.LEVELS, "");
    }

    /** Painting the large box's picture (a container yard, full, its track and train) costs no more than HarbourYard.PICTURE_MS. */
    static void yardCost() {
        if (Timing.skip("PortCheck.yardCost")) return;
        CityShore.Work big = yardWork(3000, 3000, 1, 0, 7);
        int bits = HarbourYard.RAIL | HarbourYard.OPEN_LOW | HarbourYard.TRAIN, reps = 20;
        double best = Double.MAX_VALUE;
        long sink = 0;
        for (int round = 0; round < MapCheck.WARM_ROUNDS + MapCheck.TIMED_ROUNDS; round++) {
            long t0 = System.nanoTime();
            for (int i = 0; i < reps; i++) sink += HarbourYard.picture(big, HarbourYard.Kind.CONTAINER, HarbourYard.LEVELS, bits)[i];
            double ms = (System.nanoTime() - t0) / 1e6 / reps;
            if (round >= MapCheck.WARM_ROUNDS) best = Math.min(best, ms);
        }
        timed("painting the large box's picture (a full container yard, RAIL | OPEN_LOW | TRAIN) takes no more than HarbourYard.PICTURE_MS (0.5 ms)",
                Timing.within(best, HarbourYard.PICTURE_MS) && sink != 0, String.format("%.3f ms a picture", best));
    }
}

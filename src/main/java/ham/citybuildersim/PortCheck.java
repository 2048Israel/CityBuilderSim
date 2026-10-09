package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * The city's ports and their boats (0.7.86, batch O9; runs/spec-oil.md 2.9,
 * 2.10 and 4's PortCheck; runs/research-freight.md 5).
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
 *      berths, the city's, on the coast.
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
 *      its crews paid with transit's; the calls tonnes over the ship's cargo;
 *      the audit closing.
 *
 *   7. CRUDE NEEDS ROOM: a refinery's crude with a tanker berth and no Tank
 *      Farm is held back for room, and its page says so; with a farm it goes
 *      by sea in the largest class the free room takes.
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

    /** The sea rows, {F, c}: liquid VLCC, Suezmax, Aframax-LR, MR; dry Capesize, Panamax; deep-sea boxes; general cargo. */
    static final double[][] LIQUID_ROWS = { { 4 + 1, .0012 }, { 4 + 2, .0022 }, { 4 + 5, .0035 }, { 4 + 6, .0035 } };
    static final double[][] DRY_ROWS = { { 6 + 1.2, .00075 }, { 6 + 5, .0022 } };
    static final double[][] BOX_ROWS = { { 24, .0055 } };
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
        theFactor();
        theShares();
        noPortTheSameBand();
        Game port = aPortOnATown();
        crudeNeedsRoom();
        theBoats();
        acrossASave(port);

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
        for (Ports.Cargo c : Ports.Cargo.values()) under &= c.seaFreightShare() < ham.citybuildersim.sectors.Rail.RAIL_FLOOR;
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
                Good.ROLLING_STOCK, Good.CARS, Good.VANS);
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
                + " general steel, fabricated steel, machinery, wagon sets, cars and vans", kinds);
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
        assertTrue("...and a bulk carrier a Capesize when the month's tonnes fill one, a Panamax when not",
                Ports.bulkShipFor(164_999) == Ports.Ship.PANAMAX && Ports.bulkShipFor(165_000) == Ports.Ship.CAPESIZE);

        BuildingManager bm = new BuildingManager();
        bm.initializeBuiltInTemplates();
        List<BuildingsTemplate> file = new BuildingCatalog().load();
        String[] names = { "Tanker Terminal", "Bulk Terminal", "Container Terminal", "General Cargo Terminal" };
        Ports.Cargo[] cargo = { Ports.Cargo.LIQUID, Ports.Cargo.DRY_BULK, Ports.Cargo.CONTAINER, Ports.Cargo.GENERAL };
        double[] tonnes = { 3_250_000, 27_500_000, 750_000 * Ports.TONNES_A_TEU, 450_000 };
        double[] capital = { 96_000, 2_310_000, 461_000, 70_000 };
        int[] posts = { 7, 234, 585, 11 };
        boolean terminals = true;
        for (int i = 0; i < names.length; i++) {
            BuildingsTemplate t = bm.getTemplateByName(names[i]), d = null;
            for (BuildingsTemplate x : file) if (x.getName().equals(names[i])) d = x;
            for (BuildingsTemplate x : new BuildingsTemplate[] { t, d }) {
                terminals &= x != null && x.getId() == 94 + i && x.getCategory() == BuildingType.PORTS && !x.isOwnedBySector()
                        && x.isPort() && x.berthCargo() == cargo[i] && x.berthTonnesAYear() == tonnes[i]
                        && x.getCashCost() == capital[i] && x.getTotalJobs() == posts[i] && x.needsCoast();
            }
        }
        assertTrue("four terminals, ids 94-97, the city's, a berth each - liquid 3.25 Mt, dry bulk 27.5 Mt, boxes 0.75M TEU,"
                + " general 0.45 Mt a year - at D$96M, 2,310M, 461M and 70M, 7, 234, 585 and 11 posts, on the coast", terminals);
        assertTrue("...a berth's month is its year over twelve", Ports.berthMonth(bm.getTemplateByName("Tanker Terminal")) == 3_250_000 / 12.0);
        assertTrue("WORTH_A_BERTH, the test player's line, is the railway's own MIN_LINE_UTILISATION",
                Ports.WORTH_A_BERTH == ham.citybuildersim.sectors.Rail.MIN_LINE_UTILISATION);
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
        quietly(() -> {
            g.setCashForTest(Founding.WEALTHY_CASH * 100);
            stood[0] = g.buildStack(template(g, "Steel Foundry"), 2, true);
            stood[1] = g.buildStack(template(g, "Rail Spur"), 1, true);
            g.simulateMonths(4);
        });
        assertTrue("fixture: two steel foundries and a rail spur stood", stood[0] == Game.BuildResult.SUCCESS
                && stood[1] == Game.BuildResult.SUCCESS);
        ham.citybuildersim.sectors.Rail rail = g.getSectors().rail();
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
        quietly(() -> {
            g.setCashForTest(Founding.WEALTHY_CASH * 100);
            stood[0] = g.buildStack(template(g, "Steel Foundry"), 2, true);
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
            twin.buildStack(template(twin, "Steel Foundry"), 2, true);
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
        double[] carried = g.getSectors().rail().getCarried();
        boolean formula = true, narrower = true;
        for (Good good : Good.values()) {
            if (good.cargo() != Ports.Cargo.GENERAL) continue;
            GoodsMarket m = g.getMarkets().get(good);
            double c = carried[good.traffic().ordinal()];
            double r = ports.railShareOf(good, c), s = ports.seaShareOf(good, c);
            formula &= m.getFreightFactor() == Ports.factor(r, s, Ports.SEA_FREIGHT_SHARE_GENERAL)
                    && m.getFreightFactor() == 1 - r - s + s * Ports.SEA_FREIGHT_SHARE_GENERAL;
            if (good.importable() && good.exportable() && good.baseFreight() > 0) {
                narrower &= m.getFreightFactor() < 1 && m.importPrice() < good.worldImportPrice() * m.getExchangeRate()
                        && m.exportPrice() > good.worldExportPrice() * m.getExchangeRate();
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
        report("ITS CREWS ARE PAID WITH TRANSIT'S: a town with no line pays its terminal's posts at their fill, and its twin"
                        + " with none pays nothing",
                bill > 0 && bill <= posts * (1 + 1e-12) && twinBill == 0,
                String.format("$%,.2fk of $%,.2fk fully staffed", bill, posts));

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
            @Override public double cost(BuildingsTemplate t) { return 250_000; }
            @Override public double running(BuildingsTemplate t) { return 60; }
            @Override public double standing(BuildingsTemplate t) { return 200; }
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
                big.passes() && big.earns() == 415_000 * saved - 260 && !small.passes() && small.failed()
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
}

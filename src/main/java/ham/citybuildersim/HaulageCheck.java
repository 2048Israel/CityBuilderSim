package ham.citybuildersim;

import ham.citybuildersim.sectors.Freight;
import ham.citybuildersim.sectors.Haulage;
import ham.citybuildersim.sectors.HeavyIndustry;
import ham.citybuildersim.sectors.Mining;
import ham.citybuildersim.sectors.ProcessingSector;
import ham.citybuildersim.sectors.SpreadPlanner;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Freight's trucks, held to FR2's brief: the city leg of every bulk tonne is
 * billed once, by Haulage, at a quote the trucks' own books set, and the vans
 * carry goods and nothing else.
 *
 * WHY THIS HARNESS EXISTS. Until FR2 every sector moved its own bulk in its own
 * vans, both sides of every door: an imported tonne paid its city leg twice, a
 * tonne between two works paid nobody and a siding saved nothing. FR2 (docs/queue/
 * FR2.md) moved the city leg to Freight's Haulage division. It was written by
 * one agent from the brief and is held here by another, from the brief's rules
 * and the model's own getters and constants: no figure below is typed in that a
 * getter or a constant could give. Ten claims, numbered as FR2.md section 6
 * numbers them (its 11, a real run, is LongPlaytest's):
 *
 *   1. THE SLOT. The twelfth key, Sectors.KEYS[11] (the brief's "position 12"),
 *      is "Freight", the registry has as many sectors as keys, and the twelfth
 *      is a Freight.
 *   2. TRUCKS. The world sells them and never buys them, one truck weighs 40 t
 *      gross less a load, and the Heavy Truck Plant alone makes them.
 *   3. EACH LEG ONCE. In a played city with a mine selling ore at home to a
 *      pelletising plant that lands its coal and ships its pellets, every
 *      sector's "Haulage" line is its legs' tonnes at the share in force and the
 *      quote, a tonne sold at home is its buyer's and nobody else's, a goods
 *      tonne has no truck leg, and Freight's revenue is the shippers' lines to
 *      the cent. And a reload bills the next month to the bit, with the same
 *      shares behind its need and the same fleet after it.
 *   4. THE SIDING. A railway carrying a share r of the imports takes
 *      RAIL_ROAD_RELIEF x r of their truck tonnes off the city leg; a share
 *      carried by sea alone relieves nothing (since FR6 a station beside a
 *      sea terminal is a port yard that does: HomeRailCheck 4).
 *   5. VANS FOR GOODS. A works whose tonnes are all bulk keeps no vans; the
 *      vans of a sector with goods are its goods' tonnes over TONNES_PER_VAN.
 *   6. THE FREIGHT FACTOR. A bulk sector runs at MIN_VAN_RATE + (1 - MIN_VAN_RATE)
 *      x the trucks' share, a literal 1 when the trucks suffice, and the
 *      fifth factor is the lesser of that and its vans'.
 *   7. THE FLEET. The need (the plant standing, at nameplate, struck at each
 *      haul), the bid, the wear (a tenth faster past the depots' room), the
 *      seeding once and never again, and the bill never past what the trucks
 *      could carry.
 *   8. THE PRICE. The quote walks REPRICE_SPEED toward the rule's target, over
 *      what the trucks COULD carry, at the scarcity the tightness sets.
 *   9. THE DEPOT. Ordered once the fleet passes the room by DEPOT_ORDER_MARGIN of
 *      a depot, not before; and the drivers follow the trucks: a depot keeping
 *      fewer trucks than its room offers fewer driver posts, and the city counts
 *      and pays the same number.
 *  10. THE WEIGHING. A works is weighed with its legs at the quote, the vans
 *      only for its goods, the railed part of an import relieved by the siding.
 *
 * It tests the model through its public getters and constants and changes
 * nothing in it; where the code and the brief disagree it says FAIL and the
 * line says where. The sections run in the order 1, 2, 3 (with the reload), 5, 6, 7, 8, 4, 9, 10, because 5 and 6 read section 3's city
 * (6 changes it with hauls run by hand) and every later section plays a city of its own.
 */
public class HaulageCheck {

    static int fails = 0;

    static void quietly(Runnable r) {
        java.io.PrintStream real = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        try { r.run(); } finally { System.setOut(real); }
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-68s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        System.out.printf("%-68s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    /** Bitwise, not near. See TradeCostCheck for why this harness family compares this way. */
    static boolean same(double a, double b) {
        return Double.compare(a, b) == 0 || (Double.isNaN(a) && Double.isNaN(b));
    }

    static boolean near(double a, double b, double tol) {
        return Math.abs(a - b) <= tol * Math.max(1, Math.abs(b));
    }

    /** A truck's gross weight, FR2.md section 2 (an EU 40 t articulated truck): the model states its tare, 40 t less a load. */
    static final double GROSS_TONNES = 40;

    /** The relief a siding gives a railed tonne's city leg (InfrastructureManager). */
    static final double RELIEF = InfrastructureManager.RAIL_ROAD_RELIEF;

    static final double T = Haulage.TONNES_A_TRUCK_MONTH;

    /**
     * Freight's place in Sectors.KEYS: FR2.md calls it "position 12" and "KEYS[12]", counting from one - the twelfth sector, Rail's
     * old place, index 11 (retail is the first). Index 12 is Automotive, as it always was.
     */
    static final int SLOT = 11;

    /* ================================================================
       THE FIXTURES
       ================================================================ */

    /**
     * A city with a bulk chain at home and trade both ways: three iron mines,
     * and a pelletising plant that buys their ore (and lands the rest, which
     * the mines cannot make), lands its coal and ships its pellets. Houses,
     * shops, roads, power and water around them. Not yet played.
     *
     * THE SECTORS THAT WOULD CHANGE THE PREMISE ARE HELD: the planner would
     * build a depot or a rail line under the trucks, or shed the plant. An
     * assertion whose subject is decided by a planner is an assertion about the
     * planner. AND FREIGHT IS GIVEN CASH: a founded Freight has none and buys
     * its trucks out of what it earns, which with a fleet of fifty-odd at
     * D$215k each and a bill of a few hundred dollars a month is never; in a
     * played city the investor funds it with its first depot.
     */
    static Game base(GameFiles files) { return base(files, true); }

    /** ...with or without the plant: mines and no works are a city whose ore all goes abroad. */
    static Game base(GameFiles files, boolean plant) {
        Game g = new Game(files);
        quietly(() -> {
            g.newGame();
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.getForeignAccounts().pinRate(1.0);
            g.getWorldEconomy().pin();
            g.getLandManager().setOwnedSqFt(400_000_000L);
            BuildingManager b = g.getBuildingManager();
            g.buildStack(b.getTemplateByName("House"), 900, true);
            g.buildStack(b.getTemplateByName("Convenience Store"), 20, true);
            g.buildStack(b.getTemplateByName("Small Grocery Store"), 6, true);
            g.buildStack(b.getTemplateByName("Paved Road"), 60, true);
            g.buildStack(b.getTemplateByName("Coal Power Plant"), 1, true);
            g.buildStack(b.getTemplateByName("Water Treatment Plant"), 1, true);
            g.buildStack(b.getTemplateByName("Construction Depot"), 4, true);
            g.getLandManager().restoreSites(Resource.IRON, 3, 50_000_000);
            g.buildStack(b.getTemplate(Mining.IRON_MINE), 3, true);
            if (plant) g.buildStack(b.getTemplate(HeavyIndustry.PELLETISING_PLANT), 1, true);
            g.getBusinessInvestment().holdSector(Sectors.FREIGHT);
            g.getBusinessInvestment().holdSector(Sectors.HEAVY_INDUSTRY);
            g.getBusinessInvestment().holdSector(Sectors.MINING);
            // FIXTURE: D$1B for Freight to buy its trucks with. A founded Freight earns almost nothing, and its fleet grows only
            // out of its own revenue and its lender's credit, so without it these sections would read a fleet that never grew
            // (FR2-notes, the financing finding; Pending).
            g.getSectors().freight().setCash(1_000_000);
        });
        return g;
    }

    /** ...played for `months`, which is long enough for the trucks to be seeded (at the first haul, at the plant's nameplate need) and the books settled. */
    static Game played(GameFiles files, int months) { return played(files, months, true); }

    static Game played(GameFiles files, int months, boolean plant) {
        Game g = base(files, plant);
        quietly(() -> { for (int i = 0; i < months; i++) g.toggleNextMonth(); });
        return g;
    }

    static Game played(String label, int months) { return played(GameFiles.scratch(label), months); }

    /** Track or depots, handed to the city rather than waited for, on their ground (RailCheck.lay()). */
    static void lay(Game game, String name, int count) {
        BuildingsTemplate t = game.getBuildingManager().getTemplateByName(name);
        game.getBuildingManager().addStack(t, count, true);
        game.getLandManager().allocate(t.getLandSqFt() * count);
    }

    /** The month about to be billed, worked out from the model's own getters before the haul runs. */
    static final class Pred {
        final Map<Sector, Double> legs = new LinkedHashMap<>();
        double tonnes, capacity, quote, share, f;

        double bill(Sector s) { return legs.getOrDefault(s, 0.0) * f * quote; }
    }

    /**
     * What the next haul bills, from the rows the month just run left: each
     * sector's legs (Haulage.legTonnes at the railway's share of each good in
     * force), at the share Haulage says it carries and never more than the
     * trucks can, at the quote in force. The brief's rule, not the code's loop.
     */
    static Pred predict(Game game) {
        Freight fr = game.getSectors().freight();
        Haulage h = fr.haulage();
        Pred p = new Pred();
        for (Sector s : game.getSectors().all()) {
            if (s == fr) continue;
            double t = 0;
            for (Good g : Good.values()) t += Haulage.legTonnes(s, g, fr.railShareInForce(g), fr.seaShareInForce(g), fr.getHomeShare(), fr.getQuayShare());
            if (t > 0) {
                p.legs.put(s, t);
                p.tonnes += t;
            }
        }
        p.capacity = h.trucks() * T;
        p.quote = h.getQuote();
        p.share = h.getShare();
        double f = p.share;
        if (p.tonnes * f > p.capacity) f = p.capacity / p.tonnes;
        p.f = p.tonnes > 0 && f > 0 ? f : 0;
        return p;
    }

    static double line(Sector s) { return s.otherInputParts().getOrDefault(Haulage.BILLED_AS, 0.0); }

    /** Where the last compare() found its worst miss: the sector, what it was billed, what the brief says. */
    static String worstWhere = "";

    /** What the builders' repair imports added to their line over the rows the prediction saw (positive), over every compare() so far. */
    static double topOfMonth = 0;

    /**
     * Every sector's "Haulage" line against the prediction. The brief's rule is that each sector pays its legs x ONE share x the quote,
     * the share in force and never more than the trucks could carry. So the share every sector with legs was billed at must be the same
     * (the worst relative spread is the miss returned), and it must be the share in force (nothing capped) or less than it with the
     * trucks' whole capacity billed (the cap). The prediction's rows are all the harness can see between two months, and the builders'
     * repair imports at the top of the month are not among them (see below), so the share it predicts is the most it can have been.
     * Returns {worst miss, sectors billed, the share billed at}.
     */
    static double[] compare(Game game, Pred p) {
        Freight fr = game.getSectors().freight();
        double worst = 0, billed = 0, fStar = Double.NaN, lines = 0;
        for (Sector s : game.getSectors().all()) {
            if (s == fr) continue;
            double got = line(s), legs = p.legs.getOrDefault(s, 0.0);
            lines += got;
            if (got > 0) billed++;
            // THE BUILDERS BUY THEIR REPAIR MATERIALS AT THE TOP OF THE MONTH (Game.chargeBuildingMaintenance(), just before chargeFreight()):
            // those imports are in no row between two months, so a prediction cannot see them, and the haul bills them in the month they
            // were bought. Construction's line is therefore not predicted: it counts in the total the cap is held to.
            if (Sectors.CONSTRUCTION.equals(s.key())) {
                topOfMonth += Math.max(0, got - legs * p.f * p.quote);
                continue;
            }
            if (legs > 0 && p.quote > 0) {
                double f = got / (legs * p.quote);
                if (Double.isNaN(fStar)) fStar = f;
                double miss = Math.abs(f - fStar) / Math.max(1, Math.abs(fStar));
                if (miss > worst) worstWhere = String.format("%s billed at share %.9f against %.9f (%.6f for %,.0f t of legs at %.8f)", s.key(), f, fStar, got, legs, p.quote);
                worst = Math.max(worst, miss);
            } else if (got > 1e-12) {
                worstWhere = String.format("%s billed %.6f with no legs in its rows", s.key(), got);
                worst = Math.max(worst, got);
            }
        }
        if (!Double.isNaN(fStar)) {
            boolean uncapped = near(fStar, p.share, 1e-9);
            boolean capped = fStar < p.share && near(lines, p.capacity * p.quote, 1e-9);
            boolean within = fStar <= p.f * (1 + 1e-9) + 1e-12;
            if (!(uncapped || capped) || !within) {
                worstWhere = String.format("billed at share %.9f; the share in force %.9f, the cap %.9f; %,.4fk billed against capacity x quote %,.4fk",
                        fStar, p.share, p.f, lines, p.capacity * p.quote);
                worst = Math.max(worst, 1);
            }
        }
        return new double[] { worst, billed, fStar };
    }

    static double sum(Game game) {
        double t = 0;
        for (Sector s : game.getSectors().all()) if (!(s instanceof Freight)) t += line(s);
        return t;
    }

    /** The GOODS tonnes a sector's standing plant moves, in and out, worked out from its capacities (no captive good in these cities). */
    static double goodsTonnes(Sector s) {
        double t = 0;
        for (Good g : s.goodsMade()) if (g.traffic() == Traffic.GOODS) t += s.getCapacity(g) * g.tonnesPerUnit();
        for (Good g : s.goodsUsed()) {
            if (g == Good.VANS || g == Good.TRUCKS || g.traffic() != Traffic.GOODS) continue;
            t += s.getInputAtCapacity(g) * g.tonnesPerUnit();
        }
        return t;
    }

    /** The BULK tonnes a sector's plant moves at nameplate. */
    static double bulkTonnes(Sector s) {
        double t = 0;
        for (Good g : s.goodsMade()) if (g.traffic() == Traffic.BULK) t += s.getCapacity(g) * g.tonnesPerUnit();
        for (Good g : s.goodsUsed()) {
            if (g == Good.VANS || g == Good.TRUCKS || g.traffic() != Traffic.BULK) continue;
            t += s.getInputAtCapacity(g) * g.tonnesPerUnit();
        }
        return t;
    }

    /** One haul, run by hand: the month's rows billed at the state in force, as Game.chargeFreight() runs it. */
    static void haul(Game game) { game.getSectors().freight().haul(game.getSectors(), game.getPorts()); }

    /** Haulage's need and share put where a fixture wants them, through its own save and restore (the share is struck at a haul, so a fixture cannot reach it by moving the pantry). */
    static void setState(Haulage h, double need, double share) {
        Map<String, Double> ex = new HashMap<>();
        h.save(ex);
        ex.put("haul.need", need);
        ex.put("haul.share", share);
        h.restore(ex);
    }

    /** Trucks a fixture hands over (setPantry) were bought by nobody: Haulage's fleet account (★10) kept at nought for them, or their wear comes back to the shippers as a refund. */
    static void offTheBooks(Haulage h) {
        Map<String, Double> ex = new HashMap<>();
        h.save(ex);
        ex.put("haul.fleetOwed", 0.0);
        h.restore(ex);
    }

    /**
     * A good's shares of the city's trade, from the sectors' own month: the units shipped abroad over those and the units bought at
     * home, and the units landed over those and the units bought at home - or, with nothing traded, 1 for a good the world buys (sells)
     * and 0 for one it does not. {import share, export share}. Haulage strikes them at each haul from these rows (FR2 review), so they
     * are the ones in force right after a haul run by hand, and the ones the next haul will strike between two months.
     */
    static double[] tradeShares(Game game, Good g) {
        double ex = 0, im = 0, home = 0;
        for (Sector s : game.getSectors().all()) {
            ex += Math.max(0, s.unitsExported(g));
            im += Math.max(0, s.unitsImported(g));
            home += Math.max(0, s.unitsBoughtLocal(g));
        }
        double exp = ex + home > 0 ? Math.max(0, Math.min(1, ex / (ex + home))) : g.exportable() ? 1 : 0;
        double imp = im + home > 0 ? Math.max(0, Math.min(1, im / (im + home))) : g.importable() ? 1 : 0;
        return new double[] { imp, exp };
    }

    /**
     * The city-leg tonnes the plant standing asks for at nameplate, from the brief's weights (the FR2 review): every sector's but
     * Freight's BULK inputs at capacity x tpu x (1 - RELIEF x rail x import share), plus its BULK outputs at capacity x tpu x export
     * share x (1 - RELIEF x rail). The cities here make and use no captive good, so nothing is taken off for one.
     */
    static double nameplate(Game game, boolean railed) {
        Freight fr = game.getSectors().freight();
        double t = 0;
        for (Sector s : game.getSectors().all()) {
            if (s == fr) continue;
            for (Good g : s.goodsUsed()) {
                if (g == Good.VANS || g == Good.TRUCKS || g.traffic() != Traffic.BULK) continue;
                t += s.getInputAtCapacity(g) * g.tonnesPerUnit() * (1 - RELIEF * (railed ? fr.railShareInForce(g) : 0) * tradeShares(game, g)[0]);
            }
            for (Good g : s.goodsMade()) {
                if (g == Good.VANS || g == Good.TRUCKS || g.traffic() != Traffic.BULK) continue;
                t += s.getCapacity(g) * g.tonnesPerUnit() * tradeShares(game, g)[1] * (1 - RELIEF * (railed ? fr.railShareInForce(g) : 0));
            }
        }
        return t;
    }

    /** The trucks set to `fleet` and a haul run by hand: the share is struck then, over the fleet held at its start (Haulage.getShare()). */
    static void strike(Game game, double fleet) {
        game.getSectors().freight().setPantry(Good.TRUCKS, fleet);
        haul(game);
    }

    public static void main(String[] args) {

        slot();
        trucks();

        /* ================================================================
           3. EACH LEG ONCE - and A RELOAD, on the same city
           ================================================================ */

        System.out.println("\n--- 3. each leg once: the city leg is billed to the buyer and the exporter, at the share in force and the quote ---");

        GameFiles filesA = GameFiles.scratch("haulagecheck-a");
        Game a = base(filesA);
        Freight fr = a.getSectors().freight();
        Haulage h = fr.haulage();
        Sector plant = a.getSectors().heavyIndustry(), mine = a.getSectors().mining();
        // The ramp first: until the trucks are seeded and bought there is nothing billed.
        int guard = 0;
        while (!(h.getBill() > 0) && guard++ < 12) quietly(a::toggleNextMonth);
        assertTrue("fixture: the first bill has been raised, so Haulage has a fleet, a quote and legs to bill",
                h.getBill() > 0 && h.isQuoteKnown() && h.trucks() > 0 && h.getNeed() > 0);

        int months = 0, short_ = 0, enough = 0, capped = 0, sectorsBilled = 0;
        double worst = 0;
        boolean everyMonth = true, sumsToTheCent = true, goodsFree = true;
        long goodsCrossed = 0;
        double homeTonnes = 0, coalTonnes = 0, pelletTonnes = 0, oreImpTonnes = 0, fBefore = 0, quoteBefore = 0, soldLocalTonnes = 0;
        double mineBill = Double.NaN, mineExports = Double.NaN, plantWant = 0, plantGot = 0;
        StringBuilder misses = new StringBuilder();
        for (int m = 0; m < 16; m++) {
            // THE SHORTAGE IS CAUSED: the fleet is seeded at the plant's nameplate need, so left alone the trucks suffice every
            // month. Month 2 is run with half of them (Freight buys them back at an eighth of the need a month, so a few
            // months are short and the rest are not): both sides of the share are billed.
            if (m == 2) fr.setPantry(Good.TRUCKS, h.getNeed() / T * .5);
            Pred p = predict(a);
            // A GOODS tonne has no truck leg, whatever crossed: counted, so the premise that goods crossed is caused and not assumed.
            for (Sector s : a.getSectors().all()) {
                for (Good g : Good.values()) {
                    if (g.traffic() != Traffic.GOODS) continue;
                    if (s.unitsImported(g) + s.unitsExported(g) + s.unitsBoughtLocal(g) > 0) {
                        goodsCrossed++;
                        goodsFree &= same(Haulage.legTonnes(s, g, 0), 0) && same(Haulage.legTonnes(s, g, 1), 0);
                    }
                }
            }
            if (m == 15) {
                double r = fr.railShareInForce(Good.IRON);
                homeTonnes = Good.IRON.tonnesPerUnit() * plant.unitsBoughtLocal(Good.IRON);
                oreImpTonnes = Good.IRON.tonnesPerUnit() * plant.unitsImported(Good.IRON) * (1 - RELIEF * r);
                coalTonnes = Good.THERMAL_COAL.tonnesPerUnit() * (plant.unitsBoughtLocal(Good.THERMAL_COAL)
                        + (plant.unitsImported(Good.THERMAL_COAL) + plant.unitsExported(Good.THERMAL_COAL)) * (1 - RELIEF * fr.railShareInForce(Good.THERMAL_COAL)));
                pelletTonnes = Good.PELLETS.tonnesPerUnit() * (plant.unitsBoughtLocal(Good.PELLETS)
                        + (plant.unitsImported(Good.PELLETS) + plant.unitsExported(Good.PELLETS)) * (1 - RELIEF * fr.railShareInForce(Good.PELLETS)));
                fBefore = p.f;
                quoteBefore = p.quote;
                plantWant = (homeTonnes + oreImpTonnes + coalTonnes + pelletTonnes) * p.f * p.quote;
                Sector.Output ore = mine.outputRow(Good.IRON);
                soldLocalTonnes = ore == null ? 0 : Good.IRON.tonnesPerUnit() * ore.soldLocal;
                mineExports = mine.unitsExported(Good.IRON);
            }
            quietly(a::toggleNextMonth);
            months++;
            double[] c = compare(a, p);
            if (c[0] > 1e-9 && misses.length() < 600) misses.append("month " + m + ": " + worstWhere + "; ");
            worst = Math.max(worst, c[0]);
            sectorsBilled += (int) c[1];
            everyMonth &= c[0] <= 1e-9;
            if (p.f < 1) short_++; else enough++;
            if (p.tonnes * p.share > p.capacity) capped++;
            sumsToTheCent &= near(sum(a), h.getBill(), 1e-9)
                    && near(h.getBill(), fr.otherRevenueParts().getOrDefault("Haulage billed", 0.0), 1e-9);
            if (m == 15) {
                plantGot = line(plant);
                mineBill = line(mine);
            }
        }
        report("every sector's \"Haulage\" line is its legs x the share in force x the quote, every month",
                everyMonth && sectorsBilled > 0,
                String.format("%d months, %d sector-bills compared, worst miss %.2e; the builders' repair imports added %,.4fk at the top of the months %s",
                        months, sectorsBilled, worst, topOfMonth, misses));
        report("...and the months cover the trucks short and the trucks enough, so neither side is vacuous",
                short_ > 0 && enough > 0, String.format("%d months short of trucks, %d enough, %d with the cap binding", short_, enough, capped));
        report("the plant pays the ore it bought at home, the ore and coal it landed and the pellets it shipped, each a leg",
                homeTonnes > 0 && oreImpTonnes > 0 && coalTonnes > 0 && pelletTonnes > 0 && near(plantGot, plantWant, 1e-9),
                String.format("%,.0f t ore at home + %,.0f t ore landed + %,.0f t coal + %,.0f t pellets, x%.4f x D$%.5fk = %,.4fk against %,.4fk billed",
                        homeTonnes, oreImpTonnes, coalTonnes, pelletTonnes, fBefore, quoteBefore, plantWant, plantGot));
        // "Nothing" is read within 1e-9 of what hauling the ore it sold at home would have cost it (PT1-M4): the city's numbers moved
        // with the works department retired, and the trade books left float noise (~1e-14 of a bill of 150k) where they left an exact 0.
        double soldAtHomeBill = soldLocalTonnes * fBefore * quoteBefore;
        report("the mine that sold its ore at home, and shipped none, was billed nothing for it",
                soldLocalTonnes > 0 && Good.IRON.tonnesPerUnit() * Math.abs(mineExports) <= 1e-9 * soldLocalTonnes
                        && Math.abs(mineBill) <= 1e-9 * soldAtHomeBill && near(soldLocalTonnes, homeTonnes, 1e-9),
                String.format("sold %,.0f t at home (the plant bought %,.0f t), exported %.0f (%.3g), billed %s of the %,.2fk hauling the ore it sold would have been",
                        soldLocalTonnes, homeTonnes, mineExports, mineExports, mineBill, soldAtHomeBill));
        report("...and a goods tonne that crossed, or was bought at home, has no truck leg, whatever the railway carries",
                goodsCrossed > 0 && goodsFree, goodsCrossed + " sector-goods rows crossed");
        report("Freight's \"Haulage billed\" is the shippers' lines to the cent, and the bill is in its other revenue",
                sumsToTheCent && h.getBill() > 0 && near(fr.statement().otherRevenue, h.getBill() + fr.otherRevenueParts().getOrDefault("Rail freight billed", 0.0), 1e-9),
                String.format("billed %,.6fk, shippers' lines %,.6fk, %s", h.getBill(), sum(a), fr.otherRevenueParts()));
        report("...and the bill is what the trucks carried: the carried share of the legs, within the trucks' capacity",
                h.getTonnes() > 0 && h.getCarried() > 0 && h.getCarried() <= h.getCapacityInForce() * (1 + 1e-12)
                        && h.getCarried() <= h.getTonnes() * (1 + 1e-12),
                String.format("%,.0f t of legs, %,.0f carried, %,.0f t capacity", h.getTonnes(), h.getCarried(), h.getCapacityInForce()));

        /* ---------------- the reload ---------------- */

        System.out.println("\n--- ...and a reload bills the next month as the live city does, to the bit ---");

        quietly(() -> a.saveGame(10));
        Game back = new Game(filesA);
        quietly(() -> back.loadGameSave(10));
        // The live city's exchange rate and the world's price level are pinned by the fixture (base()), and a pin is the harness's, not the save's:
        // the loaded city is pinned the same, or its markets would drift from the live city's and the two would price a month differently.
        back.getForeignAccounts().pinRate(1.0);
        back.getWorldEconomy().pin();
        Haulage hBack = back.getSectors().freight().haulage();
        Pred pLive = predict(a), pBack = predict(back);
        boolean legsBack = pLive.legs.size() == pBack.legs.size() && pLive.legs.size() > 0;
        for (Sector s : a.getSectors().all()) {
            legsBack &= same(pLive.legs.getOrDefault(s, 0.0), pBack.legs.getOrDefault(back.getSectors().byKey(s.key()), 0.0));
        }
        report("the loaded city carries the month's units bought at home, landed and shipped: its legs are the live city's",
                legsBack && same(pLive.tonnes, pBack.tonnes) && same(pLive.quote, pBack.quote) && same(pLive.share, pBack.share)
                        && same(h.trucks(), hBack.trucks()) && same(h.getNeed(), hBack.getNeed()),
                String.format("%,.0f t of legs, quote %.6f, share %.6f, %,.2f trucks", pLive.tonnes, pLive.quote, pLive.share, hBack.trucks()));
        // WHAT THE NEED AND THE LEG PRICE ARE WEIGHED WITH: the shares of each good the city imported and exported, struck at the
        // last haul from the sectors' month and saved with Haulage; read through the leg price they weight.
        StringBuilder shares = new StringBuilder();
        boolean sharesBack = true;
        Haulage.LegPrice priceLive = h.legPrice(), priceBack = hBack.legPrice();
        for (Good g : new Good[] { Good.IRON, Good.THERMAL_COAL, Good.PELLETS }) {
            sharesBack &= same(priceLive.in(g), priceBack.in(g)) && same(priceLive.out(g), priceBack.out(g));
            shares.append(String.format("%s in %.8f v %.8f out %.8f v %.8f; ", g.name(), priceLive.in(g), priceBack.in(g), priceLive.out(g), priceBack.out(g)));
        }
        report("the loaded city has the live city's leg price, which the shares the city struck at its last haul weigh",
                sharesBack, shares.toString());
        double needBefore = h.nameplateNeed(a.getSectors()), needBack = hBack.nameplateNeed(back.getSectors());
        report("...so the plant standing asks the same city leg of both before the first haul after the load",
                needBefore > 0 && same(needBefore, needBack) && same(nameplate(a, true), nameplate(back, true)),
                String.format("%,.0f t live against %,.0f t loaded", needBefore, needBack));
        quietly(() -> { a.toggleNextMonth(); back.toggleNextMonth(); });
        boolean linesSame = true;
        StringBuilder diff = new StringBuilder();
        for (Sector s : a.getSectors().all()) {
            double live = line(s), loaded = line(back.getSectors().byKey(s.key()));
            if (!same(live, loaded)) {
                linesSame = false;
                diff.append(String.format("%s %s v %s; ", s.key(), live, loaded));
            }
        }
        report("the next haul bills every sector the same \"Haulage\" line as the live city's, to the bit",
                linesSame && h.getBill() > 0 && same(h.getBill(), hBack.getBill()),
                diff.length() > 0 ? diff.toString() : String.format("%,.6fk billed by both", h.getBill()));
        StringBuilder unlike = new StringBuilder();
        if (!same(h.getQuote(), hBack.getQuote())) unlike.append(String.format("quote %.8f v %.8f; ", h.getQuote(), hBack.getQuote()));
        if (!same(h.getTonnes(), hBack.getTonnes())) unlike.append(String.format("tonnes %,.1f v %,.1f; ", h.getTonnes(), hBack.getTonnes()));
        if (!same(h.getCarried(), hBack.getCarried())) unlike.append(String.format("carried %,.1f v %,.1f; ", h.getCarried(), hBack.getCarried()));
        if (!same(h.getCapacityInForce(), hBack.getCapacityInForce())) unlike.append(String.format("capacity %,.1f v %,.1f; ", h.getCapacityInForce(), hBack.getCapacityInForce()));
        if (!same(h.getAllowed(), hBack.getAllowed())) unlike.append(String.format("allowed %.6f v %.6f; ", h.getAllowed(), hBack.getAllowed()));
        if (!same(h.getNeed(), hBack.getNeed())) unlike.append(String.format("need %,.1f v %,.1f; ", h.getNeed(), hBack.getNeed()));
        if (!same(h.getShare(), hBack.getShare())) unlike.append(String.format("share %.8f v %.8f; ", h.getShare(), hBack.getShare()));
        if (!same(h.trucks(), hBack.trucks())) unlike.append(String.format("trucks %.6f v %.6f; ", h.trucks(), hBack.trucks()));
        if (unlike.length() > 0) {
            unlike.append(String.format("[diesel %.8f v %.8f, worn %.8f v %.8f, truck price %.6f v %.6f, diesel price %.8f v %.8f]", h.getDiesel(), hBack.getDiesel(),
                    h.getWorn(), hBack.getWorn(), a.getMarkets().get(Good.TRUCKS).landedPrice(), back.getMarkets().get(Good.TRUCKS).landedPrice(),
                    a.getMarkets().get(Good.DIESEL).landedPrice(), back.getMarkets().get(Good.DIESEL).landedPrice()));
        }
        report("...and prices, carries and fleets alike: quote, tonnes, carried, capacity, allowed, need, share, trucks",
                unlike.length() == 0, unlike.length() == 0 ? String.format("quote %.8f, %.6f trucks, need %,.0f t", h.getQuote(), h.trucks(), h.getNeed()) : unlike.toString());

        vans(a);
        factor(a);
        fleetAndPrice();
        siding();
        depot();
        weighing();

        System.out.println();
        if (fails == 0) {
            System.out.println("Haulage works: each leg is billed once, at the share in force and the quote; the siding"
                    + " relieves and the sea does not; the vans carry goods; the fleet, the price and the depot follow the brief.");
        } else {
            System.out.println(fails + " FAILED");
        }
        System.exit(fails);
    }

    /* ================================================================
       1. THE SLOT
       ================================================================ */

    static void slot() {
        System.out.println("--- 1. the slot: Freight is key 12, and the registry is the keys ---");
        Game g = new Game(GameFiles.scratch("haulagecheck-slot"));
        quietly(g::newGame);
        Sectors all = g.getSectors();
        report("the twelfth key (position 12, index " + SLOT + ") is \"Freight\", and Sectors.FREIGHT says the same",
                Sectors.KEYS.length > SLOT && "Freight".equals(Sectors.KEYS[SLOT]) && "Freight".equals(Sectors.FREIGHT),
                "KEYS[" + SLOT + "] = " + (Sectors.KEYS.length > SLOT ? Sectors.KEYS[SLOT] : "none"));
        boolean noRail = true, unique = true;
        java.util.Set<String> seen = new java.util.HashSet<>();
        for (String k : Sectors.KEYS) {
            noRail &= !"Rail".equals(k);
            unique &= seen.add(k);
        }
        report("...no key is called \"Rail\" any more, and none is twice in the list",
                noRail && unique, Sectors.KEYS.length + " keys");
        boolean inStep = all.size() == Sectors.KEYS.length;
        for (int i = 0; i < Sectors.KEYS.length && i < all.size(); i++) inStep &= all.get(i).key().equals(Sectors.KEYS[i]);
        report("the registry has as many sectors as there are keys, each in its key's place",
                inStep, all.size() + " sectors against " + Sectors.KEYS.length + " keys");
        report("the twelfth sector is the Freight, found by index, by key and by accessor",
                all.get(SLOT) instanceof Freight && all.get(SLOT) == all.freight() && all.byKey("Freight") == all.freight()
                        && all.indexOf("Freight") == SLOT,
                all.get(SLOT).getClass().getSimpleName());
        report("...a Freight with a Haulage division of its own, and no fleet in a city that has not yet moved a tonne",
                all.freight().haulage() != null && same(all.freight().haulage().trucks(), 0),
                String.format("%.0f trucks", all.freight().haulage().trucks()));
    }

    /* ================================================================
       2. TRUCKS
       ================================================================ */

    static void trucks() {
        System.out.println("\n--- 2. TRUCKS: the world sells them and never buys, 15 t of tare, one plant makes them ---");
        report("the world sells a truck at a price: it is importable, at a finite world import price",
                Good.TRUCKS.importable() && Double.isFinite(Good.TRUCKS.worldImportPrice()) && Good.TRUCKS.worldImportPrice() > 0,
                String.format("D$%.1fk", Good.TRUCKS.worldImportPrice()));
        report("...and never buys one: no world export price, and not exportable",
                Double.isNaN(Good.TRUCKS.worldExportPrice()) && !Good.TRUCKS.exportable(),
                "export price " + Good.TRUCKS.worldExportPrice());
        report("a truck weighs 40 t gross less a full load: tonnesPerUnit() is 15",
                same(Good.TRUCKS.tonnesPerUnit(), GROSS_TONNES - Haulage.TONNES_A_LOAD) && same(Good.TRUCKS.tonnesPerUnit(), 15),
                String.format("%.1f t (%.0f gross less a %.0f t load)", Good.TRUCKS.tonnesPerUnit(), GROSS_TONNES, Haulage.TONNES_A_LOAD));
        report("...it drives in on its own wheels: the goods stream, a general cargo, a freight of 6.25% of its price",
                Good.TRUCKS.traffic() == Traffic.GOODS && Good.TRUCKS.cargo() == Ports.Cargo.GENERAL
                        && near(Good.TRUCKS.baseFreight(), .0625 * Good.TRUCKS.worldImportPrice(), 1e-12),
                String.format("freight %.4f of %.1f", Good.TRUCKS.baseFreight(), Good.TRUCKS.worldImportPrice()));
        BuildingManager bm = new BuildingManager();
        quietly(bm::initializeTemplates);
        int makers = 0, depots = 0;
        BuildingsTemplate plant = null, depot = null;
        for (BuildingsTemplate t : bm.getTemplates()) {
            if (t.makes(Good.TRUCKS) > 0) {
                makers++;
                plant = t;
            }
            if (t.getTruckRoom() > 0) {
                depots++;
                depot = t;
            }
        }
        report("exactly one building in the catalogue makes trucks: the Heavy Truck Plant, Automotive's",
                makers == 1 && plant != null && "Heavy Truck Plant".equals(plant.getName()) && plant.getId() == 140
                        && Sectors.AUTOMOTIVE.equals(plant.getSector()),
                plant == null ? "none" : String.format("%s (%d), %.0f a month", plant.getName(), plant.getId(), plant.makes(Good.TRUCKS)));
        report("...and exactly one keeps them: the Truck Depot, Freight's, with room for trucks and posts for their drivers",
                depots == 1 && depot != null && "Truck Depot".equals(depot.getName()) && depot.getId() == 139
                        && Sectors.FREIGHT.equals(depot.getSector()) && depot.getCategory() == BuildingType.HAULAGE
                        && depot.getTruckRoom() > 0 && depot.getJobs(JobType.DIPLOMA) > depot.getTruckRoom() * Haulage.DRIVERS_A_TRUCK,
                depot == null ? "none" : String.format("room %.0f trucks, %d DIPLOMA posts", depot.getTruckRoom(), depot.getJobs(JobType.DIPLOMA)));
        boolean noneUses = true;
        for (BuildingsTemplate t : bm.getTemplates()) noneUses &= t.uses(Good.TRUCKS) == 0;
        assertTrue("...and no building burns trucks as a feed: they are a fleet, bought by Freight alone", noneUses);
    }

    /* ================================================================
       5. VANS FOR GOODS
       ================================================================ */

    static void vans(Game a) {
        System.out.println("\n--- 5. vans for goods: bulk keeps none, goods keep TONNES_PER_VAN ---");
        Sector plant = a.getSectors().heavyIndustry(), mine = a.getSectors().mining(), retail = a.getSectors().retail();
        Freight fr = a.getSectors().freight();
        double bulk = bulkTonnes(plant);
        report("a works whose tonnes are all bulk (Iron & Steel's pelletising plant) keeps no vans, to the bit",
                bulk > 0 && same(goodsTonnes(plant), 0) && plant.hasBulkLegs() && same(plant.tonnesMoved(), 0)
                        && same(plant.vansNeeded(), 0) && same(plant.getVanRatio(), 1),
                String.format("%,.0f t of bulk, %.0f of goods, %.0f vans", bulk, plant.tonnesMoved(), plant.vansNeeded()));
        double mineGoods = goodsTonnes(mine), mineBulk = bulkTonnes(mine);
        report("a mine's ore is bulk and its diesel is goods: it keeps vans for the diesel alone",
                mineBulk > 100 * mineGoods && mineGoods > 0 && near(mine.tonnesMoved(), mineGoods, 1e-12)
                        && near(mine.vansNeeded(), mineGoods / Sector.TONNES_PER_VAN, 1e-12),
                String.format("%,.0f t of ore, %,.1f t of diesel -> %.3f vans", mineBulk, mineGoods, mine.vansNeeded()));
        double retailGoods = goodsTonnes(retail);
        report("a sector with goods keeps its goods' tonnes over TONNES_PER_VAN, and its bulk (the grain) is not among them",
                retailGoods > 0 && bulkTonnes(retail) > 0 && near(retail.tonnesMoved(), retailGoods, 1e-12)
                        && near(retail.vansNeeded(), retailGoods / Sector.TONNES_PER_VAN, 1e-12),
                String.format("%,.1f t of goods + %,.1f t of bulk -> %.3f vans", retailGoods, bulkTonnes(retail), retail.vansNeeded()));
        report("...and Freight itself, the fleet's owner, moves no goods and keeps no vans: the trucks do not haul themselves",
                same(fr.tonnesMoved(), 0) && same(fr.vansNeeded(), 0) && same(fr.getVanRatio(), 1),
                String.format("%.0f t", fr.tonnesMoved()));
    }

    /* ================================================================
       6. THE FREIGHT FACTOR
       ================================================================ */

    static void factor(Game a) {
        System.out.println("\n--- 6. the freight factor: MIN_VAN_RATE + (1 - MIN_VAN_RATE) x the trucks' share, a literal 1 when they suffice ---");
        Freight fr = a.getSectors().freight();
        Haulage h = fr.haulage();
        Sector plant = a.getSectors().heavyIndustry(), retail = a.getSectors().retail(), services = a.getSectors().businessServices();
        assertTrue("fixture: the plant and the shop have bulk to move, the office has none",
                plant.hasBulkLegs() && retail.hasBulkLegs() && !services.hasBulkLegs());

        // THE SHARE IS STRUCK AT A HAUL (FR2 review), over the fleet held when it starts: a fixture sets the fleet and runs one by hand.
        // A first haul with trucks to spare strikes the need the rest are read against (the rows do not move between hauls by hand).
        strike(a, h.getNeed() / T * 3);
        double need = h.getNeed();
        assertTrue("fixture: Haulage has a nameplate need to read the share against, and strikes the same one at every haul by hand",
                need > 0 && near(need, nameplate(a, true), 1e-9) && same(h.getShare(), 1));
        double enoughTrucks = need / T;
        while (enoughTrucks * T < need) enoughTrucks = Math.nextUp(enoughTrucks);

        strike(a, need / T * .5);
        double shareHalf = h.getShare();
        report("the share is the fleet held at the haul x TONNES_A_TRUCK_MONTH over the need (capacity / need), struck at that haul",
                near(shareHalf, .5, 1e-9) && near(shareHalf, h.getCapacityInForce() / h.getNeed(), 1e-12) && near(h.getNeed(), need, 1e-12),
                String.format("%.4f trucks held of %.4f needed -> share %.6f (capacity %,.0f t of %,.0f t)", need / T * .5, h.trucksNeeded(), shareHalf,
                        h.getCapacityInForce(), h.getNeed()));
        double ratio = Sector.MIN_VAN_RATE + (1 - Sector.MIN_VAN_RATE) * shareHalf;
        report("a bulk sector's haulage ratio is MIN_VAN_RATE + (1 - MIN_VAN_RATE) x that share",
                near(plant.getHaulageRatio(), ratio, 1e-12) && plant.getHaulageRatio() < 1 && plant.getHaulageRatio() > Sector.MIN_VAN_RATE,
                String.format("%.6f (floor %.1f, share %.3f)", plant.getHaulageRatio(), Sector.MIN_VAN_RATE, shareHalf));
        report("...and its freight ratio is the lesser of its vans' ratio and that: the plant keeps no vans, so it is the trucks'",
                same(plant.getVanRatio(), 1) && same(plant.getFreightRatio(), Math.min(plant.getVanRatio(), plant.getHaulageRatio()))
                        && same(plant.getFreightRatio(), plant.getHaulageRatio()),
                String.format("vans %.3f, trucks %.3f -> %.6f", plant.getVanRatio(), plant.getHaulageRatio(), plant.getFreightRatio()));
        // The fifth factor of the operating rate, in the order Sector.getOperatingRate() takes them.
        report("...and it is the fifth factor of the operating rate",
                same(plant.getOperatingRate(), plant.getAverageFill() * plant.getEnergyRatio() * plant.getWaterRatio()
                        * plant.getRoadRatio() * plant.getHealthRatio() * plant.getFreightRatio()),
                String.format("rate %.6f", plant.getOperatingRate()));
        // ...AND IT HOLDS FOR THE MONTH: the pantry moving between hauls moves nothing (the sectors that produced before the trucks were
        // bought ran at one rate and the ones after at another, when it was read live).
        fr.setPantry(Good.TRUCKS, 0);
        report("...and holds for the month: the trucks moving between two hauls do not move the share or the sectors' ratio",
                same(h.getShare(), shareHalf) && near(plant.getHaulageRatio(), ratio, 1e-12), String.format("share %.6f with %.0f trucks held", h.getShare(), h.trucks()));

        // BOTH SIDES OF THE THRESHOLD: no trucks at all, a tenth short, exactly enough, plenty.
        strike(a, 0);
        report("with no trucks at all a bulk sector is slowed to the floor, not stopped",
                same(h.getShare(), 0) && same(plant.getHaulageRatio(), Sector.MIN_VAN_RATE) && plant.getOperatingRate() > 0,
                String.format("ratio %.1f%%, operating rate %.4f", plant.getHaulageRatio() * 100, plant.getOperatingRate()));
        strike(a, enoughTrucks * .9);
        report("...a tenth short of the need it runs just below one",
                near(h.getShare(), .9, 1e-6) && plant.getHaulageRatio() < 1
                        && near(plant.getHaulageRatio(), Sector.MIN_VAN_RATE + (1 - Sector.MIN_VAN_RATE) * h.getShare(), 1e-12),
                String.format("share %.6f -> %.6f", h.getShare(), plant.getHaulageRatio()));
        strike(a, enoughTrucks);
        report("...trucks exactly at the need: the share is a literal 1 and so is the ratio, and the freight ratio",
                same(h.getShare(), 1) && same(plant.getHaulageRatio(), 1) && same(plant.getFreightRatio(), 1) && same(retail.getHaulageRatio(), 1),
                String.format("share %s, ratio %s", h.getShare(), plant.getHaulageRatio()));
        report("...and the operating rate is then the other four factors times a literal one, to the bit",
                same(plant.getOperatingRate(), plant.getAverageFill() * plant.getEnergyRatio() * plant.getWaterRatio()
                        * plant.getRoadRatio() * plant.getHealthRatio() * 1),
                String.format("rate %.6f", plant.getOperatingRate()));
        strike(a, enoughTrucks * 3);
        report("...and trucks past the need earn no bonus: still a literal 1",
                same(h.getShare(), 1) && same(plant.getHaulageRatio(), 1), String.format("share %s", h.getShare()));

        // A SECTOR WITH NO BULK LEGS reads one whatever the share; Freight itself too.
        strike(a, 0);
        report("a sector with no bulk legs reads a literal 1 whatever the share, and so does Freight itself",
                !services.hasBulkLegs() && same(services.getHaulageRatio(), 1) && same(services.getFreightRatio(), services.getVanRatio())
                        && same(fr.getHaulageRatio(), 1) && same(fr.getFreightRatio(), 1) && same(h.getShare(), 0),
                String.format("business services %s, Freight %s, at a share of %s", services.getHaulageRatio(), fr.getHaulageRatio(), h.getShare()));

        // THE LESSER OF THE TWO, from both sides, on a shop that keeps vans for its goods and has grain to haul.
        double vansNeeded = retail.vansNeeded();
        assertTrue("fixture: the shop keeps vans for its goods and its grain goes by truck", vansNeeded > 0 && retail.isFleetKnown());
        retail.setPantry(Good.VANS, vansNeeded * .25);
        strike(a, enoughTrucks * .9);
        double vanRatio = Sector.MIN_VAN_RATE + (1 - Sector.MIN_VAN_RATE) * .25, haulRatio = Sector.MIN_VAN_RATE + (1 - Sector.MIN_VAN_RATE) * h.getShare();
        report("a shop short of vans and a little short of trucks runs at its vans' ratio, the lesser",
                near(retail.getVanRatio(), vanRatio, 1e-9) && near(retail.getHaulageRatio(), haulRatio, 1e-12)
                        && same(retail.getFreightRatio(), retail.getVanRatio()) && retail.getVanRatio() < retail.getHaulageRatio(),
                String.format("vans %.4f, trucks %.4f -> %.4f", retail.getVanRatio(), retail.getHaulageRatio(), retail.getFreightRatio()));
        retail.setPantry(Good.VANS, vansNeeded * 2);
        strike(a, enoughTrucks * .5);
        haulRatio = Sector.MIN_VAN_RATE + (1 - Sector.MIN_VAN_RATE) * h.getShare();
        report("...with vans to spare and the trucks half short it runs at the trucks' ratio, the lesser",
                same(retail.getVanRatio(), 1) && near(h.getShare(), .5, 1e-6) && near(retail.getHaulageRatio(), haulRatio, 1e-12)
                        && same(retail.getFreightRatio(), retail.getHaulageRatio()),
                String.format("vans %.4f, trucks %.4f -> %.4f", retail.getVanRatio(), retail.getHaulageRatio(), retail.getFreightRatio()));
    }

    /* ================================================================
       7. THE FLEET and 8. THE PRICE
       ================================================================ */

    static void fleetAndPrice() {
        System.out.println("\n--- 7. the fleet: the bid, the wear, the seeding once, the bill never past the trucks ---");
        Game g = played("haulagecheck-p", 16);
        Freight fr = g.getSectors().freight();
        Haulage h = fr.haulage();
        BuildingsTemplate depot = g.getBuildingManager().getTemplateByName("Truck Depot");
        double room = depot.getTruckRoom();

        // ---- the need: the plant standing at nameplate, struck at each haul, not last month's legs ----
        // (The shares that weight it are struck at the haul from the sectors' rows, which a haul run by hand leaves as they are:
        // the brief's sum over the rows now is the need the haul strikes, and nameplateNeed() afterwards agrees.)
        double indep = nameplate(g, true);
        haul(g);
        double np = h.nameplateNeed(g.getSectors());
        report("the need a haul strikes is the plant standing at nameplate: getNeed() is nameplateNeed(), the brief's weighted sum",
                np > 0 && near(indep, np, 1e-9) && near(h.getNeed(), np, 1e-9) && same(h.trucksNeeded(), h.getNeed() / T),
                String.format("need %,.0f t (nameplateNeed %,.0f, the brief's sum %,.0f) = %.2f trucks", h.getNeed(), np, indep, h.trucksNeeded()));
        report("...and not last month's legs: the legs billed are what the plant ran, the need what it stands for",
                h.getTonnes() > 0 && Math.abs(h.getNeed() - h.getTonnes()) > 1e-6 * np,
                String.format("%,.0f t of legs billed against a need of %,.0f t", h.getTonnes(), h.getNeed()));

        // ---- the bid ----
        double need = 30 * T;
        setState(h, need, 1);
        // The order is toward the plant standing AND the plant on site (Haulage.trucksAsked(), the review after this harness
        // was written): this city's works on site add their legs to the need it is bought toward.
        double asked = h.trucksAsked();
        boolean bids = true;
        StringBuilder bidLine = new StringBuilder();
        double eighth = asked / Sector.FLEET_DELIVERY_MONTHS;
        for (double have : new double[] { 0, 10, asked - eighth - 1, asked - eighth + 1, asked * .99, asked, asked + 5 }) {
            fr.setPantry(Good.TRUCKS, have);
            double want = Math.max(0, Math.min(asked - have, eighth));
            bids &= same(h.bid(), want) && same(fr.bid(Good.TRUCKS), want);
            bidLine.append(String.format("%.2f->%.3f ", have, h.bid()));
        }
        report("the bid is min(asked - have, asked / FLEET_DELIVERY_MONTHS) trucks, asked the need and the works on site's,"
                        + " and nothing at or past it",
                bids && asked > 3 * Sector.FLEET_DELIVERY_MONTHS && same(asked, (need + h.getNeedOnSite()) / T),
                String.format("asked %.2f (on site %,.0f t); have->bid: ", asked, h.getNeedOnSite()) + bidLine);
        fr.setPantry(Good.TRUCKS, 0);
        report("...a fleet of none asks for an eighth of what it is asked, not all of it (a fleet is built up, not conjured)",
                same(h.bid(), eighth) && h.bid() < asked, String.format("%.3f of %.1f", h.bid(), asked));
        fr.setPantry(Good.TRUCKS, asked);
        report("...and a fleet of exactly what it is asked asks for nothing, a literal zero",
                same(h.bid(), 0) && same(fr.bid(Good.TRUCKS), 0), String.format("%s", h.bid()));

        // ---- the wear ----
        Pred ready = predict(g);
        assertTrue("fixture: the city has legs to haul, so a haul runs its whole month", ready.tonnes > 0 && h.isQuoteKnown());
        double[][] cases = { { 60, 0 }, { 60, 1 }, { 150, 1 }, { 101, 1 }, { 100, 1 } };
        StringBuilder wearLine = new StringBuilder();
        boolean wears = true;
        int depots = 0;
        for (double[] c : cases) {
            while (depots < (int) c[1]) {
                lay(g, "Truck Depot", 1);
                depots++;
            }
            double have = c[0], standing = depots * room, street = Math.max(0, have - standing);
            fr.setPantry(Good.TRUCKS, have);
            haul(g);
            double wore = have - fr.getPantry(Good.TRUCKS);
            double want = (have - street) / Sector.VAN_LIFE_MONTHS + street * Haulage.STREET_WEAR / Sector.VAN_LIFE_MONTHS;
            wears &= near(wore, want, 1e-12) && near(h.getWorn(), want, 1e-12);
            wearLine.append(String.format("%.0f trucks/%.0f room: %.4f (want %.4f); ", have, standing, wore, want));
        }
        report("a month's wear is a truck's life's share of the trucks in the depots' room, and STREET_WEAR times that past it",
                wears, wearLine.toString());
        fr.setPantry(Good.TRUCKS, 150);
        double paid = 150.0 / Sector.VAN_LIFE_MONTHS;
        haul(g);
        double wore = 150 - fr.getPantry(Good.TRUCKS);
        report("...past the room the wear exceeds a depot-kept fleet's by exactly the tenth on the trucks past it",
                wore > paid && near(wore - paid, (150 - room) * (Haulage.STREET_WEAR - 1) / Sector.VAN_LIFE_MONTHS, 1e-9),
                String.format("%.5f against %.5f at %.0f room", wore, paid, room));

        // ---- the bill, never past what the trucks could carry ----
        Pred p = predict(g);
        setState(h, p.tonnes / 2, 1);
        fr.setPantry(Good.TRUCKS, p.tonnes / 2 / T);
        Pred q = predict(g);
        haul(g);
        double capped = h.getCapacityInForce() * q.quote;
        report("when the legs exceed the trucks the bill is the trucks' capacity at the quote, not the legs'",
                q.tonnes * q.share > q.capacity && near(h.getBill(), capped, 1e-9) && h.getBill() < q.tonnes * q.quote * .6
                        && h.getCarried() <= h.getCapacityInForce() * (1 + 1e-12),
                String.format("legs %,.0f t, trucks carry %,.0f t: billed %,.4fk (capacity x quote %,.4fk, legs x quote %,.4fk)",
                        q.tonnes, h.getCapacityInForce(), h.getBill(), capped, q.tonnes * q.quote));
        Pred r = predict(g);
        fr.setPantry(Good.TRUCKS, r.tonnes * 2 / T);
        setState(h, r.tonnes, 1);
        double quote2 = h.getQuote();
        haul(g);
        report("...and when the trucks have room to spare the bill is the legs' alone, below the capacity's",
                near(h.getBill(), r.tonnes * quote2, 1e-9) && h.getBill() < h.getCapacityInForce() * quote2,
                String.format("billed %,.4fk of a possible %,.4fk", h.getBill(), h.getCapacityInForce() * quote2));
        Pred s = predict(g);
        fr.setPantry(Good.TRUCKS, s.tonnes / T * .5);
        setState(h, s.tonnes, .5);
        double quote3 = h.getQuote();
        haul(g);
        report("...and with the trucks half the need, never past what they carry",
                h.getBill() <= h.getCapacityInForce() * quote3 * (1 + 1e-9) && h.getBill() > 0,
                String.format("billed %,.4fk, capacity x quote %,.4fk", h.getBill(), h.getCapacityInForce() * quote3));

        seeding();

        /* ================================================================
           8. THE PRICE
           ================================================================ */

        System.out.println("\n--- 8. the price: the quote walks REPRICE_SPEED toward allowed over what the trucks could carry, at the scarcity ---");
        Game pg = played("haulagecheck-price", 16);
        Freight pf = pg.getSectors().freight();
        Haulage ph = pf.haulage();
        assertTrue("fixture: a quote is known and no depot stands, so every truck is on the street", ph.isQuoteKnown() && same(ph.depotRoom(), 0));

        // Half the need: the capacity is the fleet held when the haul starts (before the month's wear, which the order replaces),
        // so half the nameplate need in trucks makes the tightness .5. The need a haul strikes is the brief's sum over the rows now.
        double nameplate = nameplate(pg, true);
        pf.setPantry(Good.TRUCKS, nameplate / 2 / T);
        double old = ph.getQuote();
        haul(pg);
        double target = ph.getAllowed() / ph.getCapacityInForce() * (1 + ph.getTightness() * (Freight.MAX_SCARCITY_MULTIPLE - 1));
        report("with the trucks at half the need the tightness is .5 and the scarcity is 1 + .5 x (MAX_SCARCITY_MULTIPLE - 1)",
                nameplate > 0 && near(ph.getNeed(), nameplate, 1e-9) && near(ph.getTightness(), .5, 1e-9)
                        && near(1 + ph.getTightness() * (Freight.MAX_SCARCITY_MULTIPLE - 1), 1 + .5 * (Freight.MAX_SCARCITY_MULTIPLE - 1), 1e-9),
                String.format("tightness %.9f, scarcity %.6f (MAX %.1f)", ph.getTightness(),
                        1 + ph.getTightness() * (Freight.MAX_SCARCITY_MULTIPLE - 1), Freight.MAX_SCARCITY_MULTIPLE));
        report("...and the quote is the old one plus REPRICE_SPEED of the way to allowed / capacity x scarcity",
                near(ph.getQuote(), old + (target - old) * Freight.REPRICE_SPEED, 1e-12) && ph.getAllowed() > 0 && target > old,
                String.format("%.8f -> %.8f (target %.8f, allowed %,.4fk over %,.0f t capacity)", old, ph.getQuote(), target, ph.getAllowed(), ph.getCapacityInForce()));

        // Plenty of trucks: no scarcity, and the divisor is the capacity, not the tonnes carried.
        pf.setPantry(Good.TRUCKS, nameplate * 3 / T);
        offTheBooks(ph);
        old = ph.getQuote();
        haul(pg);
        target = ph.getAllowed() / ph.getCapacityInForce() * (1 + ph.getTightness() * (Freight.MAX_SCARCITY_MULTIPLE - 1));
        double ifCarried = ph.getAllowed() / ph.getCarried();
        double stepOverCapacity = old + (target - old) * Freight.REPRICE_SPEED, stepOverCarried = old + (ifCarried - old) * Freight.REPRICE_SPEED;
        report("with trucks to spare the tightness is nought and the scarcity a plain one",
                same(ph.getTightness(), 0) && ph.getCarried() < ph.getCapacityInForce() * .6,
                String.format("tightness %s, %,.0f t carried of %,.0f", ph.getTightness(), ph.getCarried(), ph.getCapacityInForce()));
        report("...the quote walks toward allowed over what the trucks COULD carry, not what they did",
                near(ph.getQuote(), stepOverCapacity, 1e-12) && Math.abs(ph.getQuote() - stepOverCarried) > 1e-6 * Math.max(1, old),
                String.format("%.8f -> %.8f; over the capacity %.6f a tonne, over the carried %.6f", old, ph.getQuote(), target, ifCarried));
        report("...a fleet with idle trucks does not price its few tonnes at its whole cost",
                ph.getQuote() < stepOverCarried, String.format("%.8f against %.8f", ph.getQuote(), stepOverCarried));
        // Almost no trucks: the scarcity is at its multiple.
        pf.setPantry(Good.TRUCKS, nameplate / T * .0001);
        offTheBooks(ph);
        old = ph.getQuote();
        haul(pg);
        double scarce = 1 + ph.getTightness() * (Freight.MAX_SCARCITY_MULTIPLE - 1);
        double stepScarce = old + (ph.getAllowed() / ph.getCapacityInForce() * scarce - old) * Freight.REPRICE_SPEED;
        report("...and trucks almost none are the dearest: tightness near one, the scarcity near MAX_SCARCITY_MULTIPLE",
                ph.getTightness() > .99 && scarce > Freight.MAX_SCARCITY_MULTIPLE - .01 && ph.getCarried() > 0 && near(ph.getQuote(), stepScarce, 1e-12),
                String.format("tightness %.6f, scarcity %.4f, quote %.8f", ph.getTightness(), scarce, ph.getQuote()));
    }

    /** The first fleet is given once, at the nameplate need, free; a city that is known to have run out is not given another. */
    static void seeding() {
        Game fresh = base(GameFiles.scratch("haulagecheck-seed"));
        Freight fr = fresh.getSectors().freight();
        Haulage h = fr.haulage();
        report("before any month there is no fleet and nothing to bid for",
                same(h.trucks(), 0) && same(h.bid(), 0) && same(h.getNeed(), 0), "");
        int guard = 0;
        while (!(h.getNeed() > 0) && guard++ < 6) quietly(fresh::toggleNextMonth);
        Sector.Split bought = fr.statement().bought.getOrDefault(Good.TRUCKS, new Sector.Split());
        report("the first haul seeds the fleet at the need, for nothing (a city with a plant standing has a need from its first month)",
                h.getNeed() > 0 && same(h.trucks(), h.trucksNeeded()) && h.trucks() > 0 && same(bought.total(), 0),
                String.format("%.4f trucks for a need of %,.0f t (%.4f trucks); bought %s", h.trucks(), h.getNeed(), h.trucksNeeded(), bought.total()));

        // A city that has run its fleet down is not given another: known, with no trucks, and no cash to buy any.
        Game spent = base(GameFiles.scratch("haulagecheck-unseeded"));
        Freight fs = spent.getSectors().freight();
        Haulage hs = fs.haulage();
        Map<String, Double> ex = new HashMap<>();
        hs.save(ex);
        ex.put("haul.trucksKnown", 1.0);
        hs.restore(ex);
        fs.setCash(0);
        quietly(() -> { for (int i = 0; i < 4; i++) spent.toggleNextMonth(); });
        report("a fleet already known, with no trucks and no money, is not handed one: the seeding happens once",
                hs.getNeed() > 0 && hs.trucks() < hs.trucksNeeded() * .01 && hs.bid() > 0,
                String.format("%.5f trucks of %.2f needed after four months, asking for %.3f", hs.trucks(), hs.trucksNeeded(), hs.bid()));
    }

    /* ================================================================
       4. THE SIDING
       ================================================================ */

    static void siding() {
        System.out.println("\n--- 4. the siding: a railed share of the imports relieves RAIL_ROAD_RELIEF of their truck tonnes; the sea relieves none ---");
        Game b = played("haulagecheck-rail", 14);
        quietly(() -> lay(b, "Rail Spur", 1));
        quietly(() -> { for (int i = 0; i < 4; i++) b.toggleNextMonth(); });
        Freight fr = b.getSectors().freight();
        Haulage h = fr.haulage();
        Sector plant = b.getSectors().heavyIndustry();
        double r = fr.railShareInForce(Good.IRON);
        assertTrue("fixture: the railway carries a share of the bulk stream, strictly between nothing and all",
                r > 0 && r < 1 && fr.trackTonnes() > 0 && fr.getHauledTonnes() > 0);
        double imp = plant.unitsImported(Good.IRON), home = plant.unitsBoughtLocal(Good.IRON);
        double tpu = Good.IRON.tonnesPerUnit();
        double got = Haulage.legTonnes(plant, Good.IRON, r), none = Haulage.legTonnes(plant, Good.IRON, 0), all = Haulage.legTonnes(plant, Good.IRON, 1);
        report("a railed share r of the landed ore takes RAIL_ROAD_RELIEF x r off its truck tonnes: they are (1 - RELIEF x r) of the tonnes",
                imp > 0 && near(got, tpu * (home + imp * (1 - RELIEF * r)), 1e-12)
                        && near(none - got, tpu * imp * RELIEF * r, 1e-9) && got < none,
                String.format("r=%.4f: %,.0f t landed, %,.0f t by truck against %,.0f t unrailed (relief %.4f)", r, tpu * imp,
                        got - tpu * home, none - tpu * home, RELIEF * r));
        report("...a tonne bought at home is not railed at all: a full truck tonne whatever r is",
                home > 0 && near(none - tpu * imp, tpu * home, 1e-9) && near(all - tpu * imp * (1 - RELIEF), tpu * home, 1e-9),
                String.format("%,.0f t at home, in every leg", tpu * home));
        report("...and a share of 1 relieves at most RELIEF: a quarter of a truck tonne is left",
                near(all, tpu * (home + imp * (1 - RELIEF)), 1e-12) && RELIEF < 1,
                String.format("%,.0f t of %,.0f", all, none));
        Pred p = predict(b);
        double unrailed = 0;
        for (Good g : Good.values()) unrailed += Haulage.legTonnes(plant, g, 0);
        quietly(b::toggleNextMonth);
        double[] c = compare(b, p);
        report("the month's bill is at the railed share: the plant's \"Haulage\" line is its legs after the siding, at the share and the quote",
                c[0] <= 1e-9 && line(plant) > 0 && near(line(plant), p.bill(plant), 1e-9) && p.legs.get(plant) < unrailed,
                String.format("worst miss %.2e (%s), %,.0f t of legs against %,.0f unrailed, %,.4fk billed", c[0], worstWhere, p.legs.get(plant), unrailed, line(plant)));
        double railBilled = 0;
        for (Sector s : b.getSectors().all()) railBilled += s.otherInputParts().getOrDefault("Rail freight", 0.0);
        report("...and the railway's own bill is a separate line, \"Rail freight\", so the two are not mistaken for each other",
                railBilled > 0 && near(railBilled, fr.getHaulageBilled(), 1e-9) && h.getBill() > 0 && near(sum(b), h.getBill(), 1e-9)
                        && near(fr.statement().otherRevenue, railBilled + h.getBill(), 1e-9),
                String.format("rail %,.4fk, trucks %,.4fk", railBilled, h.getBill()));

        // ---- the sea ----
        Game c2 = played("haulagecheck-sea", 14);
        quietly(() -> lay(c2, "Bulk Terminal", 1));
        quietly(() -> { for (int i = 0; i < 3; i++) c2.toggleNextMonth(); });
        Freight fs = c2.getSectors().freight();
        Sector plantSea = c2.getSectors().heavyIndustry();
        double sea = c2.getPorts().seaShareOf(Good.IRON, fs.getCarried()[Traffic.BULK.ordinal()]);
        double railed = fs.railShareInForce(Good.IRON);
        assertTrue("fixture: the ore goes by sea and not by rail", sea > 0 && same(railed, 0) && c2.getPorts().anyAtSea());
        double ti = Good.IRON.tonnesPerUnit();
        double fullTruck = ti * (plantSea.unitsBoughtLocal(Good.IRON) + plantSea.unitsImported(Good.IRON) + plantSea.unitsExported(Good.IRON));
        report("a share carried by sea alone relieves nothing: the landed ore is a full truck tonne from the quay to the door",
                plantSea.unitsImported(Good.IRON) > 0 && near(Haulage.legTonnes(plantSea, Good.IRON, railed), fullTruck, 1e-12),
                String.format("%.0f%% by sea, %,.0f t of truck tonnes for the ore", sea * 100, fullTruck));
        Pred ps = predict(c2);
        double others = 0;
        for (Good g : new Good[] { Good.THERMAL_COAL, Good.PELLETS }) {
            others += g.tonnesPerUnit() * (plantSea.unitsBoughtLocal(g) + (plantSea.unitsImported(g) + plantSea.unitsExported(g)) * (1 - RELIEF * fs.railShareInForce(g)));
        }
        double wantSea = (fullTruck + others) * ps.f * ps.quote;
        quietly(c2::toggleNextMonth);
        report("...and the bill agrees: the plant pays its whole tonnage at the quote, the sea having shortened nothing inside the city",
                near(line(plantSea), wantSea, 1e-9) && near(line(plantSea), ps.bill(plantSea), 1e-9) && line(plantSea) > 0,
                String.format("%,.0f t of legs, x%.4f x D$%.5fk = %,.4fk against %,.4fk billed", fullTruck + others, ps.f, ps.quote, wantSea, line(plantSea)));
    }

    /* ================================================================
       9. THE DEPOT
       ================================================================ */

    static void depot() {
        System.out.println("\n--- 9. the depot: ordered past the room by DEPOT_ORDER_MARGIN of one, not before; the drivers follow the trucks ---");
        GameFiles files = GameFiles.scratch("haulagecheck-depot");
        Game g = played(files, 16);
        Freight fr = g.getSectors().freight();
        Haulage h = fr.haulage();
        BuildingManager bm = g.getBuildingManager();
        BusinessInvestment plans = g.getBusinessInvestment();
        BuildingsTemplate depot = bm.getTemplateByName("Truck Depot");
        double room = depot.getTruckRoom(), margin = Haulage.DEPOT_ORDER_MARGIN * room;
        assertTrue("fixture: the fleet is known, no depot stands or is being built, and the margin is a fraction of a depot's room",
                same(h.depotRoom(), 0) && bm.countByName("Truck Depot") == 0 && h.trucks() > 0 && margin > 0 && margin < room);

        // ---- no depot stands: the threshold is the margin itself ----
        fr.setPantry(Good.TRUCKS, margin - 1);
        BusinessInvestment.Decision within = fr.plan(plans, g);
        report("trucks inside the margin of a depot's room: none is ordered",
                h.depotToOrder() == null && !(within.build && within.template != null && within.template.getTruckRoom() > 0),
                String.format("%.0f trucks, room 0, margin %.0f: %s", margin - 1, margin, within.build ? within.template.getName() : within.reason));
        fr.setPantry(Good.TRUCKS, margin);
        report("...and exactly at the margin still none (it has to be passed)",
                h.depotToOrder() == null, String.format("%.0f trucks", margin));
        fr.setPantry(Good.TRUCKS, margin + 1);
        BusinessInvestment.Decision past = fr.plan(plans, g);
        report("trucks past it: the plan orders a Truck Depot - or says the staff or the ground are why not",
                h.depotToOrder() == depot
                        && (past.build ? past.template == depot && past.quantity == 1
                            : past.reason != null && (past.reason.contains(depot.getName()) || past.landBlocked)),
                String.format("%.0f trucks: %s", margin + 1, past.build ? "orders a " + past.template.getName() : past.reason));

        // ---- a depot stands: the margin is past its room ----
        quietly(() -> lay(g, "Truck Depot", 1));
        fr.setPantry(Good.TRUCKS, room + margin - 1);
        BusinessInvestment.Decision within2 = fr.plan(plans, g);
        report("with a depot standing the margin is counted from its room: trucks short of room + margin order nothing",
                same(h.depotRoom(), room) && h.depotToOrder() == null && !(within2.build && within2.template != null && within2.template.getTruckRoom() > 0),
                String.format("%.0f trucks, room %.0f", room + margin - 1, h.depotRoom()));
        fr.setPantry(Good.TRUCKS, room + margin + 1);
        BusinessInvestment.Decision past2 = fr.plan(plans, g);
        report("...and past it another is ordered, or the reason is the staff or the ground",
                h.depotToOrder() == depot
                        && (past2.build ? past2.template == depot : past2.reason != null && (past2.reason.contains(depot.getName()) || past2.landBlocked)),
                String.format("%.0f trucks: %s", room + margin + 1, past2.build ? "orders a " + past2.template.getName() : past2.reason));

        // ---- the drivers follow the trucks ----
        System.out.println("\n--- ...and the drivers follow the trucks: a depot keeping fewer than its room offers fewer posts ---");
        int di = JobType.DIPLOMA.ordinal();
        double[] rates = g.getEconomyManager().getWageRates();
        long raw = bm.getJobArrayBySector(fr.key())[di];
        assertTrue("fixture: a depot stands, with driver posts at a wage, in a city that counts posts", raw >= depot.getJobs(JobType.DIPLOMA) && rates[di] > 0);

        // The room full: every post is offered.
        fr.setPantry(Good.TRUCKS, room * 1.5);
        h.strikeDrivers();
        fr.updateWages(rates, fr.postsPerTier());
        long[] fullOffered = bm.getPostsOfferedBySector(fr.key());
        long withheldFull = bm.getTotalJobsAtEveryPost(JobType.DIPLOMA) - bm.getTotalJobs(JobType.DIPLOMA);
        double payFull = fr.getPayrollAtFullStaffing();
        boolean everyTier = true;
        for (JobType t : JobType.values()) everyTier &= fullOffered[t.ordinal()] == bm.getJobArrayBySector(fr.key())[t.ordinal()];
        report("with the room full, every post is offered, in every tier",
                everyTier && same(h.offeredShare(JobType.DIPLOMA), 1) && fullOffered[di] == raw,
                String.format("%d of %d DIPLOMA posts", fullOffered[di], raw));

        double trucks = 30.7;
        fr.setPantry(Good.TRUCKS, trucks);
        h.strikeDrivers();
        double idle = Math.floor(room - trucks) * Haulage.DRIVERS_A_TRUCK;
        fr.updateWages(rates, fr.postsPerTier());
        long[] offered = bm.getPostsOfferedBySector(fr.key());
        long withheld = bm.getTotalJobsAtEveryPost(JobType.DIPLOMA) - bm.getTotalJobs(JobType.DIPLOMA);
        double pay = fr.getPayrollAtFullStaffing();
        report("a depot keeping fewer trucks than its room: the DIPLOMA posts offered are its posts less a driver for each empty place",
                idle > 0 && offered[di] == raw - (long) idle && offered[di] < raw,
                String.format("%.1f trucks in a room of %.0f: %d of %d posts offered (%.0f drivers laid off)", trucks, room, offered[di], raw, idle));
        boolean others = true;
        for (JobType t : JobType.values()) if (t != JobType.DIPLOMA) others &= offered[t.ordinal()] == bm.getJobArrayBySector(fr.key())[t.ordinal()];
        report("...the mechanics and every other tier are offered whole",
                others && same(h.offeredShare(JobType.COLLEGE_ENGINEERING), 1) && h.offeredShare(JobType.DIPLOMA) < 1,
                String.format("DIPLOMA share %.4f", h.offeredShare(JobType.DIPLOMA)));
        report("...the city's job count withholds the same number the sector does not offer",
                withheld - withheldFull == (long) idle,
                String.format("%d withheld against %d with the room full: %.0f", withheld, withheldFull, idle));
        double want = 0;
        for (JobType t : JobType.values()) want += rates[t.ordinal()] * offered[t.ordinal()];
        report("...and Freight's wage bill pays the posts offered and no more: its DIPLOMA wages are the wage a post times the posts offered",
                near(pay, want, 1e-9) && near(payFull - pay, rates[di] * idle, 1e-9) && pay < payFull,
                String.format("%,.4fk against %,.4fk with the room full (a post is D$%.4fk x %.0f)", pay, payFull, rates[di], idle));

        // Nothing to drive: all the depot's places empty, and no more posts laid off than there are.
        fr.setPantry(Good.TRUCKS, 0);
        h.strikeDrivers();
        long[] none = bm.getPostsOfferedBySector(fr.key());
        int mech = JobType.COLLEGE_ENGINEERING.ordinal();
        report("...and with no trucks at all the drivers are all laid off and not a post more: the mechanics' posts stand",
                none[di] == raw - (long) (Math.floor(room) * Haulage.DRIVERS_A_TRUCK) && none[di] >= 0
                        && none[mech] == bm.getJobArrayBySector(fr.key())[mech],
                String.format("%d of %d posts", none[di], raw));

        // ---- and the share crosses a save ----
        fr.setPantry(Good.TRUCKS, trucks);
        h.strikeDrivers();
        quietly(() -> g.saveGame(10));
        Game twin = new Game(files);
        quietly(() -> twin.loadGameSave(10));
        Freight frTwin = twin.getSectors().freight();
        long[] twinOffered = twin.getBuildingManager().getPostsOfferedBySector(frTwin.key());
        report("a reloaded city offers the posts the live one did (the drivers' share is saved)",
                same(frTwin.haulage().offeredShare(JobType.DIPLOMA), h.offeredShare(JobType.DIPLOMA))
                        && twinOffered[di] == bm.getPostsOfferedBySector(fr.key())[di] && twinOffered[di] < raw,
                String.format("%d v %d posts", twinOffered[di], bm.getPostsOfferedBySector(fr.key())[di]));
    }

    /* ================================================================
       10. THE WEIGHING
       ================================================================ */

    static void weighing() {
        System.out.println("\n--- 10. the weighing: a works is weighed with its legs at the quote and vans for its goods only ---");
        Game b = played("haulagecheck-weigh", 14);
        quietly(() -> lay(b, "Rail Spur", 1));
        quietly(() -> { for (int i = 0; i < 4; i++) b.toggleNextMonth(); });
        Freight fr = b.getSectors().freight();
        Haulage h = fr.haulage();
        Markets markets = b.getMarkets();
        BuildingManager bm = b.getBuildingManager();
        BuildingsTemplate pt = bm.getTemplate(HeavyIndustry.PELLETISING_PLANT), im = bm.getTemplate(Mining.IRON_MINE);

        // THE NEED, WITH A RAILWAY: struck at a haul, the siding's relief is in it (the railed part of an import is a quarter truck).
        haul(b);
        double railedNeed = nameplate(b, true), unrailedNeed = nameplate(b, false);
        report("with a railway carrying, the need struck at a haul is the nameplate plant's legs less the siding's relief: nameplateNeed() = the brief's sum",
                fr.railShareInForce(Good.IRON) > 0 && near(h.getNeed(), h.nameplateNeed(b.getSectors()), 1e-12)
                        && near(h.getNeed(), railedNeed, 1e-9) && railedNeed < unrailedNeed,
                String.format("need %,.0f t railed against %,.0f t on roads alone (r %.3f)", h.getNeed(), unrailedNeed, fr.railShareInForce(Good.IRON)));
        SpreadPlanner.Values v = SpreadPlanner.values(markets);
        Haulage.LegPrice p = h.legPrice();
        // The steady quote (the rule on the trucks worn), which a planner weighs a works at since the review: the quote charged
        // carries the fleet's growth (Haulage.getSteadyQuote()).
        double q = h.getSteadyQuote(), rate = .8;

        report("the Pelletising Plant keeps no vans and buys none: all its tonnes are bulk, vansFor() and fleetCapital() are zero",
                same(ProcessingSector.vansFor(pt), 0) && same(ProcessingSector.fleetCapital(pt, v), 0)
                        && same(ProcessingSector.fleetRunning(pt, v, rate), 0),
                String.format("%.0f vans, D$%.0fk of them", ProcessingSector.vansFor(pt), ProcessingSector.fleetCapital(pt, v)));

        // The legs' price of a good, from the shares struck at the haul run just above (the sectors' rows) and the railway's.
        boolean pricesRight = true;
        StringBuilder shown = new StringBuilder();
        for (Good g : new Good[] { Good.IRON, Good.THERMAL_COAL, Good.PELLETS }) {
            double r = fr.railShareInForce(g);
            double impShare = tradeShares(b, g)[0];
            double expShare = tradeShares(b, g)[1];
            double wantIn = q * (1 - RELIEF * r * impShare);
            double wantOut = q * expShare * (1 - RELIEF * r);
            pricesRight &= near(p.in(g), wantIn, 1e-12) && near(p.out(g), wantOut, 1e-12) && p.in(g) <= q && p.out(g) <= q;
            shown.append(String.format("%s in %.6f out %.6f (r %.3f, imp %.3f, exp %.3f); ", g.name(), p.in(g), p.out(g), r, impShare, expShare));
        }
        report("legPrice(): a received tonne costs the quote less the siding's RELIEF x r x the import share; a made one the export share of it",
                pricesRight && q > 0, String.format("quote %.6f: %s", q, shown));
        double r = fr.railShareInForce(Good.IRON);
        double ironExported = 0;
        for (Sector s : b.getSectors().all()) ironExported += s.unitsExported(Good.IRON);
        double impShare = tradeShares(b, Good.IRON)[0];
        report("a railed share lowers p.in() for an imported good by exactly RAIL_ROAD_RELIEF x r x the import share (of the quote)",
                r > 0 && impShare > 0 && impShare < 1 && near(q - p.in(Good.IRON), q * RELIEF * r * impShare, 1e-9) && p.in(Good.IRON) < q,
                String.format("r %.4f, %.1f%% imported: %.6f off %.6f", r, impShare * 100, q - p.in(Good.IRON), q));
        report("...and a GOODS tonne has no leg price at all",
                same(p.in(Good.CARS), 0) && same(p.out(Good.CARS), 0) && same(p.in(Good.VANS), 0) && same(p.out(Good.TRUCKS), 0), "");

        double legs = 0;
        for (Good g : pt.goodsUsed().keySet()) if (g.traffic() == Traffic.BULK) legs += pt.uses(g) * g.tonnesPerUnit() * p.in(g);
        for (Good g : pt.goodsMade().keySet()) if (g.traffic() == Traffic.BULK) legs += pt.makes(g) * g.tonnesPerUnit() * p.out(g);
        report("cityLegs() is its bulk inputs' tonnes x p.in() plus its outputs' x p.out(), at the rate",
                legs > 0 && near(ProcessingSector.cityLegs(pt, rate, p), legs * rate, 1e-12) && same(ProcessingSector.cityLegs(pt, 0, p), 0)
                        && same(ProcessingSector.cityLegs(null, rate, p), 0),
                String.format("D$%,.1fk a month at a rate of %.1f", ProcessingSector.cityLegs(pt, rate, p), rate));
        report("...and the plant's freight is its legs and nothing else: freightRunning() = fleetRunning() + cityLegs(), vans being nought",
                near(ProcessingSector.freightRunning(pt, v, rate, p), ProcessingSector.cityLegs(pt, rate, p), 1e-12),
                String.format("D$%,.1fk", ProcessingSector.freightRunning(pt, v, rate, p)));

        // An Iron Mine, weighed by the same freightRunning: its ore shipped is its leg, its diesel its vans'.
        Mining mining = b.getSectors().mining();
        double mineVans = 0;
        for (Good g : im.goodsUsed().keySet()) if (g.traffic() == Traffic.GOODS && g != Good.VANS && g != Good.TRUCKS) mineVans += im.uses(g) * g.tonnesPerUnit() / Sector.TONNES_PER_VAN;
        double mineLegs = 0;
        for (Good g : im.goodsMade().keySet()) if (g.traffic() == Traffic.BULK) mineLegs += im.makes(g) * g.tonnesPerUnit() * p.out(g);
        report("an Iron Mine whose ore all sells at home is weighed with vans for its diesel alone and no leg for its ore: its buyer pays it",
                near(ProcessingSector.vansFor(im), mineVans, 1e-12) && mineVans > 0 && same(ironExported, 0) && same(p.out(Good.IRON), 0)
                        && same(mineLegs, 0) && same(ProcessingSector.cityLegs(im, rate, p), 0),
                String.format("%.3f vans; %,.0f t of ore exported, ore leg D$%.4fk", ProcessingSector.vansFor(im), ironExported, mineLegs * rate));

        // ...and one that ships its ore abroad pays the leg of every tonne: a city of mines and no works.
        Game mg = played(GameFiles.scratch("haulagecheck-mines"), 10, false);
        Freight mf = mg.getSectors().freight();
        Haulage mh = mf.haulage();
        Mining miners = mg.getSectors().mining();
        Pred shipped = predict(mg);
        double shippedTonnes = Good.IRON.tonnesPerUnit() * miners.unitsExported(Good.IRON);
        quietly(mg::toggleNextMonth);
        report("a mine that ships its ore abroad is billed for the tonnes it shipped, at the share in force and the quote: the exporter pays",
                shippedTonnes > 0 && near(shipped.legs.getOrDefault(miners, 0.0), shippedTonnes, 1e-12) && line(miners) > 0
                        && near(line(miners), shippedTonnes * shipped.f * shipped.quote, 1e-9),
                String.format("%,.0f t shipped x %.4f x D$%.5fk = %,.4fk against %,.4fk billed", shippedTonnes, shipped.f, shipped.quote,
                        shippedTonnes * shipped.f * shipped.quote, line(miners)));
        haul(mg);   // strikes the shares from the rows the toggled month left
        Haulage.LegPrice pm = mh.legPrice();
        double qm = mh.getSteadyQuote();   // the steady quote a planner weighs at (Haulage.getSteadyQuote())
        double exportedShare = tradeShares(mg, Good.IRON)[1];
        double oreLeg = im.makes(Good.IRON) * Good.IRON.tonnesPerUnit() * pm.out(Good.IRON);
        SpreadPlanner.Values vm = SpreadPlanner.values(mg.getMarkets());
        Mining mm = mg.getSectors().mining();
        report("...so an Iron Mine there is weighed with its ore's leg at the share the city exports, and its diesel's vans",
                exportedShare > .99 && near(pm.out(Good.IRON), qm * exportedShare, 1e-12) && oreLeg > 0
                        && near(ProcessingSector.cityLegs(im, rate, pm), oreLeg * rate, 1e-12)
                        && near(ProcessingSector.vansFor(im), mineVans, 1e-12),
                String.format("%.1f%% of ore exported: ore leg D$%,.2fk a month at a rate of %.1f, %.3f vans", exportedShare * 100,
                        oreLeg * rate, rate, ProcessingSector.vansFor(im)));
        report("...and the mining sector's own weighing agrees with the pure one, the leg in it and no truck in its capital",
                near(mm.freightRunning(im, rate), ProcessingSector.freightRunning(im, vm, rate, pm), 1e-12)
                        && near(mm.freightRunning(im, rate), ProcessingSector.fleetRunning(im, vm, rate) + ProcessingSector.cityLegs(im, rate, pm), 1e-12)
                        && mm.freightRunning(im, rate) > ProcessingSector.fleetRunning(im, vm, rate)
                        && near(mm.fleetCapital(im), ProcessingSector.fleetCapital(im, vm), 1e-12),
                String.format("D$%,.2fk a month (vans' running D$%,.2fk), D$%,.2fk of vans", mm.freightRunning(im, rate),
                        ProcessingSector.fleetRunning(im, vm, rate), mm.fleetCapital(im)));
    }
}

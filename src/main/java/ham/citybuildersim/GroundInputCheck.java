package ham.citybuildersim;

import ham.citybuildersim.sectors.CoalMining;
import ham.citybuildersim.sectors.CopperMining;
import ham.citybuildersim.sectors.GoldMining;
import ham.citybuildersim.sectors.Mining;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * A works the ground limits buys the inputs of what its ground gives it, not of its plant (NG1).
 *
 * WHY. HL1's row asked why the works imported 2.6 times a month's inputs the
 * month their exports went, and for imported inputs not used to be booked as
 * stock. Its m902 was GDP1's January stale price (0.9.97), and seed 12 no
 * longer reaches it on 0.9.170; the class still live was the ground: an Iron
 * Mine whose field was worked out bid the diesel of a whole month's ore after
 * groundLimit() had dug it none, and a surface coal mine's diesel, a leach
 * plant's acid and a gold mill's acid and diesel went the same way, their
 * empty lift reading order share 1. What nothing made was bought abroad and
 * became nothing - no output, no stock - and GDP fell by its value. Since
 * NG1 the order follows the ground (Sector.groundShare()), so no input is
 * bought that the month's run does not use; what a works does hold over the
 * month's end, its pantry and its stock, is the fifth term's.
 *
 * What this has to prove:
 *   1. an Iron Mine on a field shorter than its month clears the diesel of
 *      the ore it dug, and on a worked-out field none but its fleet's; the
 *      accounts add up each month;
 *   2. a surface coal mine on a short thermal seam bids the diesel of its
 *      lift, and once the seam is out clears none for its run;
 *   3. a leach plant on short oxide ground bids the acid of its cathode, and
 *      once the oxide is out clears none;
 *   4. a gold mill on a worked-out field bids and clears no acid or diesel,
 *      and on a falling grade still bids its whole throughput's;
 *   5. kept: on ample ground each one's bid is its recipe at its order share
 *      and rate, to the bit, as before NG1;
 *   6. an input bought and held over the month's end is stock: the vans a
 *      works buys for a new mine and has not worn are in HELD, the fifth
 *      term's goods, as the accounts count them.
 */
public class GroundInputCheck {
    static int fails;
    static final PrintStream out = System.out;
    static final PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-118s %s%n", label, ok ? "OK" : "FAIL");
    }

    static boolean near(double a, double b) {
        return Math.abs(a - b) <= 1e-9 * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }

    static void quietly(Runnable work) {
        System.setOut(quiet);
        try { work.run(); }
        finally { System.setOut(out); }
    }

    static void clear(Game g) {
        quietly(() -> g.getMarkets().clearMonth(g.getSectors(), g));
    }

    static NationalAccounts accounts(Game g) {
        quietly(() -> {
            EconomyManager e = g.getEconomyManager();
            e.strikeSectors();
            e.updateNationalAccounts(0, 0, 0, 0, 0, 0, 0);
        });
        return g.getEconomyManager().getNationalAccounts();
    }

    static boolean identity(NationalAccounts na) {
        return near(na.getGdp(), na.getConsumption() + na.getInvestment() + na.getGovernment() + na.getNetExports());
    }

    /** Units of an input a sector's row took in this pass, at home and abroad. */
    static double bought(Sector s, Good g) {
        Sector.Input in = s.inputRow(g);
        return in == null ? 0 : in.boughtLocal + in.imported;
    }

    /** ...the diesel its run took: the row's, less its fleet's (Sector.runFleet() books through the same row). */
    static double runDiesel(Sector s) {
        return bought(s, Good.DIESEL) - s.getFleetDieselLitres();
    }

    /** A stockable good's order share and an input's bid, as the market pass sees them before it clears (THE ORDER). */
    static double[] order(Sector s, Good made, Good input) {
        double[] r = new double[2];
        quietly(() -> {
            s.planOrders();
            r[0] = s.orderShare(made);
            r[1] = s.bid(input);
            s.endOrders();
        });
        return r;
    }

    /* ----- 1: the Iron Mine ----- */

    /** A scratch town with one Iron Mine on a hematite field of `tonnes` by fiat, its power, water and hands. */
    static Game ironTown(String label, double tonnes) {
        Game g = new Game(GameFiles.scratch("groundinputcheck-" + label));
        quietly(() -> {
            g.newGame();
            LandManager land = g.getLandManager();
            land.setOwnedSqFt(land.getOwnedSqFt() + 400_000_000L);
            land.restoreSites(Resource.IRON, 1, tonnes);
            g.setCashForTest(Founding.WEALTHY_CASH);
            BuildingManager b = g.getBuildingManager();
            b.addStack(b.getTemplate(Mining.IRON_MINE), 1, true);
            b.addStack(b.getTemplateByName("Coal Power Plant"), 1, true);
            b.addStack(b.getTemplateByName("Water Treatment Plant"), 1, true);
        });
        MaterialsCheck.handWorkers(g);
        return g;
    }

    static void iron() {
        out.println("--- 1. an Iron Mine on a short field clears the diesel of the ore it dug, on a worked-out one none but its fleet's ---");
        double left = 2_000;
        Game g = ironTown("iron-short", left);
        Mining mine = g.getSectors().mining();
        BuildingsTemplate t = g.getBuildingManager().getTemplate(Mining.IRON_MINE);
        double ratio = t.uses(Good.DIESEL) / t.makes(Good.IRON);
        LandManager land = g.getLandManager();
        double field = land.getRemaining(Resource.IRON);
        clear(g);
        Sector.Output ore = mine.outputRow(Good.IRON);
        double asked = ore.planned, dug = ore.produced, diesel = runDiesel(mine);
        out.printf("field %,.0f t; asked %,.1f t of ore, dug %,.1f t; run diesel %,.1f L (the ore's %,.1f; the ask's %,.1f); fleet %,.1f L%n",
                field, asked, dug, diesel, dug * ratio, asked * ratio, mine.getFleetDieselLitres());
        check("premise: one Iron Mine, its month's ask past the field, which gives up all it holds",
                mine.getCapacity(Good.IRON) == t.makes(Good.IRON) && near(field, left) && asked > dug && near(dug, left));
        check("the run's diesel is the ore dug at the recipe, not the ask's: none bought for ore the field did not hold",
                near(diesel, dug * ratio));
        check("...and the accounts add up", identity(accounts(g)));

        clear(g);
        double second = runDiesel(mine), fleet = mine.getFleetDieselLitres();
        out.printf("worked out: dug %,.1f t; run diesel %,.3f L; fleet %,.1f L%n", mine.outputRow(Good.IRON).produced, second, fleet);
        check("premise: the field worked out, the mine still standing and its fleet still running",
                land.getRemaining(Resource.IRON) == 0 && mine.outputRow(Good.IRON).produced == 0 && fleet > 0);
        check("on the worked-out field the mine clears no diesel for its run: what the row took is its fleet's alone",
                Math.abs(second) <= 1e-9 * fleet);
        double fleetAbroad = mine.getFleetDieselImported();
        NationalAccounts na = accounts(g);
        Sector.Split split = mine.statement().bought.get(Good.DIESEL);
        double abroad = split == null ? 0 : split.abroad;
        out.printf("struck: diesel from abroad %,.2f, the fleet's %,.2f%n", abroad, fleetAbroad);
        check("...so the struck statement's diesel from abroad, in GDP's imports, is the fleet's: no import nothing was made with",
                near(abroad, fleetAbroad));
        check("...and the accounts add up", identity(na));
    }

    /* ----- 2: the surface coal mine ----- */

    static void coal() {
        out.println("\n--- 2. a surface coal mine on a short thermal seam bids the diesel of its lift, once the seam is out none ---");
        Game g = CoalMiningCheck.coalTown("groundinputcheck-coal", 1);
        CoalMining coal = g.getSectors().coalMining();
        BuildingManager b = g.getBuildingManager();
        BuildingsTemplate t = b.getTemplate(CoalMining.SURFACE_MINE);
        quietly(() -> b.addStack(t, 1, true));
        LandManager land = g.getLandManager();
        Resource r = Resource.COAL;
        Deposit.FieldType th = Deposit.FieldType.THERMAL;
        double ratio = t.uses(Good.DIESEL) / t.makes(Good.THERMAL_COAL), rate = coal.getOperatingRate();
        land.extract(r, th, land.getRemaining(r, th) - 30_000);
        double lift = coal.lift().surface();
        double[] o = order(coal, Good.THERMAL_COAL, Good.DIESEL);
        out.printf("rate %.4f: ask %,.0f t, lift %,.1f t; order share %.6f; diesel bid %,.1f L (the lift's %,.1f; the plant's %,.1f)%n",
                rate, t.makes(Good.THERMAL_COAL) * rate, lift, o[0], o[1], ratio * o[0] * lift, t.uses(Good.DIESEL) * o[0] * rate);
        check("premise: one surface mine, its thermal seam short of the month's ask and not out",
                coal.getCapacity(Good.THERMAL_COAL) == t.makes(Good.THERMAL_COAL) && rate > 0
                        && lift > 0 && lift < t.makes(Good.THERMAL_COAL) * rate && o[0] > 0);
        check("the diesel bid is the lift's at the recipe and the order share, not the plant's", near(o[1], ratio * o[0] * lift));

        land.extract(r, th, land.getRemaining(r, th));
        double[] none = order(coal, Good.THERMAL_COAL, Good.DIESEL);
        clear(g);
        double diesel = runDiesel(coal);
        out.printf("seam out: lift %,.1f t; order share %.6f; diesel bid %,.3f L; run diesel cleared %,.3f L%n",
                coal.lift().surface(), none[0], none[1], diesel);
        check("premise: the thermal seam out, the mine standing", land.getRemaining(r, th) == 0 && coal.lift().surface() == 0);
        check("once the seam is out the mine bids no diesel for its run, and the clearing buys it none",
                none[1] == 0 && Math.abs(diesel) <= 1e-9 * Math.max(1, coal.getFleetDieselLitres()));
        check("...and the accounts add up", identity(accounts(g)));
    }

    /* ----- 3: the leach plant ----- */

    static void leach() {
        out.println("\n--- 3. a leach plant on short oxide ground bids the acid of its cathode, once the oxide is out none ---");
        Game g = CopperMiningCheck.copperTown("groundinputcheck-leach", 0);
        CopperMining cu = g.getSectors().copperMining();
        BuildingManager b = g.getBuildingManager();
        BuildingsTemplate t = b.getTemplate(CopperMining.LEACH_PLANT);
        quietly(() -> b.addStack(t, 1, true));
        LandManager land = g.getLandManager();
        Resource r = Resource.COPPER;
        Deposit.FieldType ox = Deposit.FieldType.OXIDE;
        double ratio = t.uses(Good.ACID) / t.makes(Good.COPPER), rate = cu.getOperatingRate();
        land.extract(r, ox, land.getRemaining(r, ox) - 140);
        double lift = cu.lift();
        double[] o = order(cu, Good.COPPER, Good.ACID);
        out.printf("rate %.4f: ask %,.1f t, lift %,.2f t; order share %.6f; acid bid %,.2f t (the cathode's %,.2f; the plant's %,.2f)%n",
                rate, t.makes(Good.COPPER) * rate, lift, o[0], o[1], ratio * o[0] * lift, t.uses(Good.ACID) * o[0] * rate);
        check("premise: one leach plant, its oxide short of the month's ask and not out",
                cu.getCapacity(Good.COPPER) == t.makes(Good.COPPER) && rate > 0
                        && lift > 0 && lift < t.makes(Good.COPPER) * rate && o[0] > 0);
        check("the acid bid is the cathode's at the recipe and the order share, not the plant's", near(o[1], ratio * o[0] * lift));

        land.extract(r, ox, land.getRemaining(r, ox));
        double[] none = order(cu, Good.COPPER, Good.ACID);
        clear(g);
        double acid = bought(cu, Good.ACID);
        out.printf("oxide out: lift %,.2f t; order share %.6f; acid bid %,.3f t; acid cleared %,.3f t%n", cu.lift(), none[0], none[1], acid);
        check("premise: the oxide out, the plant standing", land.getRemaining(r, ox) == 0 && cu.lift() == 0);
        check("once the oxide is out the plant bids no acid, and the clearing buys it none", none[1] == 0 && acid == 0);
        check("...and the accounts add up", identity(accounts(g)));
    }

    /* ----- 4: the gold mill ----- */

    static void gold() {
        out.println("\n--- 4. a gold mill on a worked-out field buys no acid or diesel; on a falling grade its whole throughput's ---");
        Game g = GoldMiningCheck.town("groundinputcheck-gold");
        GoldMining mill = g.getSectors().goldMining();
        BuildingsTemplate t = g.getBuildingManager().getTemplate(GoldMining.MINE_AND_MILL);
        LandManager land = g.getLandManager();
        double rate = mill.getOperatingRate(), throughput = mill.getCapacity(Good.GOLD) * rate;
        double rich = land.goldOutput(throughput);
        land.extract(Resource.GOLD, land.getRemaining(Resource.GOLD) * .5);
        double poor = land.goldOutput(throughput);
        double[] acid = order(mill, Good.GOLD, Good.ACID), diesel = order(mill, Good.GOLD, Good.DIESEL);
        out.printf("rate %.4f; ounces at the throughput %,.2f rich, %,.2f half worked; order share %.6f; acid bid %,.3f t, diesel %,.1f L%n",
                rate, rich, poor, acid[0], acid[1], diesel[1]);
        check("premise: one mill, its grade falling as its field is worked, its ore still there",
                mill.getCapacity(Good.GOLD) == t.makes(Good.GOLD) && rate > 0 && poor > 0 && poor < rich);
        check("kept: on a falling grade the mill bids the acid and diesel of its whole throughput at its order share",
                acid[1] == t.uses(Good.ACID) * acid[0] * rate && diesel[1] == t.uses(Good.DIESEL) * diesel[0] * rate);

        land.extract(Resource.GOLD, land.getRemaining(Resource.GOLD));
        double[] noAcid = order(mill, Good.GOLD, Good.ACID), noDiesel = order(mill, Good.GOLD, Good.DIESEL);
        clear(g);
        double cleared = bought(mill, Good.ACID), run = runDiesel(mill);
        out.printf("worked out: ounces %,.3f; order share %.6f; acid bid %,.3f t, diesel bid %,.3f L; cleared acid %,.3f t, run diesel %,.3f L%n",
                land.goldOutput(throughput), noAcid[0], noAcid[1], noDiesel[1], cleared, run);
        check("premise: the field worked out, the mill standing", land.getRemaining(Resource.GOLD) == 0 && land.goldOutput(throughput) == 0);
        check("on the worked-out field the mill bids no acid and no diesel for its run",
                noAcid[1] == 0 && noDiesel[1] == 0);
        check("...and the clearing buys it none: no acid, and no diesel past its fleet's",
                cleared == 0 && Math.abs(run) <= 1e-9 * Math.max(1, mill.getFleetDieselLitres()));
        check("...and the accounts add up", identity(accounts(g)));
    }

    /* ----- 5: ample ground ----- */

    static void ample() {
        out.println("\n--- 5. kept: on ample ground each one's bid is its recipe at its order share and rate, to the bit ---");
        Game iron = ironTown("iron-ample", 1e9);
        Mining mine = iron.getSectors().mining();
        BuildingsTemplate it = iron.getBuildingManager().getTemplate(Mining.IRON_MINE);
        clear(iron);
        double dug = mine.outputRow(Good.IRON).produced, diesel = runDiesel(mine);
        out.printf("iron: dug %,.1f t of %,.1f asked; run diesel %,.1f L%n", dug, mine.outputRow(Good.IRON).planned, diesel);
        check("the Iron Mine digs its whole ask and clears that ore's diesel",
                dug > 0 && dug == mine.outputRow(Good.IRON).planned && near(diesel, dug * it.uses(Good.DIESEL) / it.makes(Good.IRON)));
        check("...and the accounts add up", identity(accounts(iron)));

        Game c = CoalMiningCheck.coalTown("groundinputcheck-coal-ample", 3);
        CoalMining coal = c.getSectors().coalMining();
        BuildingsTemplate ct = c.getBuildingManager().getTemplate(CoalMining.SURFACE_MINE);
        quietly(() -> c.getBuildingManager().addStack(ct, 1, true));
        double cr = coal.getOperatingRate();
        double[] co = order(coal, Good.THERMAL_COAL, Good.DIESEL);
        out.printf("coal: lift %,.1f t at rate %.4f; order share %.6f; diesel bid %,.3f L%n", coal.lift().surface(), cr, co[0], co[1]);
        check("the surface mine on a whole seam bids its plant's diesel at its share and rate, to the bit",
                coal.lift().surface() == ct.makes(Good.THERMAL_COAL) * cr && co[0] > 0 && co[1] == ct.uses(Good.DIESEL) * co[0] * cr);

        Game k = CopperMiningCheck.copperTown("groundinputcheck-leach-ample", 0);
        CopperMining cu = k.getSectors().copperMining();
        BuildingsTemplate kt = k.getBuildingManager().getTemplate(CopperMining.LEACH_PLANT);
        quietly(() -> k.getBuildingManager().addStack(kt, 1, true));
        double kr = cu.getOperatingRate();
        double[] ko = order(cu, Good.COPPER, Good.ACID);
        out.printf("leach: lift %,.2f t at rate %.4f; order share %.6f; acid bid %,.4f t%n", cu.lift(), kr, ko[0], ko[1]);
        check("the leach plant on whole oxide bids its plant's acid at its share and rate, to the bit",
                cu.lift() == kt.makes(Good.COPPER) * kr && ko[0] > 0 && ko[1] == kt.uses(Good.ACID) * ko[0] * kr);

        Game gd = GoldMiningCheck.town("groundinputcheck-gold-ample");
        GoldMining mill = gd.getSectors().goldMining();
        BuildingsTemplate gt = gd.getBuildingManager().getTemplate(GoldMining.MINE_AND_MILL);
        double gr = mill.getOperatingRate();
        double[] ga = order(mill, Good.GOLD, Good.ACID);
        out.printf("gold: rate %.4f; order share %.6f; acid bid %,.4f t%n", gr, ga[0], ga[1]);
        check("the gold mill on its rich field bids its plant's acid at its share and rate, to the bit",
                ga[1] == gt.uses(Good.ACID) * ga[0] * gr);
    }

    /* ----- 6: what is held over ----- */

    static void heldOver() {
        out.println("\n--- 6. an input bought and held over the month's end is stock: a new mine's vans, in the fifth term's HELD ---");
        Game g = ironTown("held", 1e9);
        Mining mine = g.getSectors().mining();
        clear(g);
        accounts(g);
        BuildingManager b = g.getBuildingManager();
        quietly(() -> b.addStack(b.getTemplate(Mining.IRON_MINE), 1, true));
        double before = mine.getPantry(Good.VANS), need = mine.vansNeeded();
        clear(g);
        double vans = bought(mine, Good.VANS);
        NationalAccounts na = accounts(g);
        double after = mine.getPantry(Good.VANS), all = 0;
        for (Sector s : g.getSectors().all()) all += s.getStock(Good.VANS) + s.getPantry(Good.VANS);
        int at = java.util.Arrays.asList(NationalAccounts.HELD).indexOf(Good.VANS);
        double held = na.getLastHeldUnits()[at];
        out.printf("vans: %.3f held, %.3f needed; %.3f bought this month; %.3f held at its end; HELD's vans %.3f (every sector's %.3f)%n",
                before, need, vans, after, held, all);
        check("premise: the second mine's fleet is short, and its vans are bought this month", need > before && vans > 0);
        check("the vans bought and not worn are held at the month's end: the pantry rose by them, less the old fleet's wear",
                near(after, before - before / Sector.VAN_LIFE_MONTHS + vans));
        check("...and they are in HELD as the accounts count it, so their rise is the fifth term's investment",
                at >= 0 && near(held, all));
        check("...and the accounts add up", identity(na));
    }

    public static void main(String[] args) {
        out.println("=== GroundInputCheck: inputs bought for what the ground gives (NG1) ===\n");
        iron();
        coal();
        leach();
        gold();
        ample();
        heldOver();
        out.println();
        out.println(fails == 0 ? "ALL PASSED" : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}

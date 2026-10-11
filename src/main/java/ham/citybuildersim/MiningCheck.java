package ham.citybuildersim;

import ham.citybuildersim.sectors.HeavyIndustry;
import ham.citybuildersim.sectors.Mining;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Ore and scrap, from the bands they clear in to what an arc furnace makes of scrap and a mine of ore.
 *
 * Until 0.7.119 the point of this harness was one number: what a Steel
 * Foundry earned on imported scrap against local ore - IRON was both, its
 * import price the mills' scrap and its export price the mines' ore - and
 * whether a mine next door made steel "a different business, not a better
 * month". Since 0.7.120 (batch I3; Jerus's Q4, 2026-10-08: "split") ore and
 * scrap are two goods, the foundry is the Electric Arc Furnace on SCRAP, and
 * steel is at the world's price, .650 / .470 (Q6). So the number is the
 * furnace's margin on its sales at home - steel to the city's fabricators at
 * what imported steel would cost - on imported scrap against the city's own
 * (the study's 2.5: about 7% and 25% for the furnace, 13% and 31% for the
 * Mini-Mill; the spec's 4: 5-15% and 20-35% of its sales). Section 3
 * measures both furnaces directly, on a month walked by hand; section 4 asks
 * whether a mine pays with every tonne shipped at the floor.
 */
public class MiningCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    /**
     * A world whose founding site has an iron field near it, as the default
     * world's had until 0.7.98 (35 sites, 449 Mt, 0.87 km out): 35, whose
     * nearest is 36 sites, 471 Mt, 0.97 km out on dry ground, none in a new
     * city's centre or first offers (fixW1-notes.md, the seed scan). Since
     * 0.7.99 (batch W1) the deposits are fewer and bigger and a founding asks
     * no iron: the default world's nearest field lies twelve kilometres out,
     * in a lake. What the fixtures that want a young city to buy a deposit
     * found their city on - this one's, MapCheck's and PlanCheck's played city.
     */
    static final long IRON_SEED = 35;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-58s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected) {
        boolean ok = Math.abs(actual - expected) < 1e-6;
        if (!ok) {
            fails++;
            out.printf("%-58s FAIL  %.6f != %.6f%n", label, actual, expected);
        } else {
            out.printf("%-58s OK%n", label);
        }
    }

    static BuildingsTemplate template(Game game, String name) {
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException("no template named " + name);
    }

    /**
     * Buys land until the city can fit what is coming, deposits included.
     *
     * A DEPOSIT COMES FROM A BOUGHT OFFER WITH IRON (0.7.57). The parcels drew
     * ore at random, a fifth of them or so; the offers are the world's ground,
     * and a new city's are a few hundred metres out, which seldom hold a
     * field's centre. So a city wanting a deposit buys the richest offer in
     * iron when one stands, and otherwise buys toward the nearest iron field
     * it does not own - on IRON_SEED's world one lies a kilometre out (until
     * 0.7.98 the founding site had one within World.SITE_IRON_KM) - the
     * offer nearest it each time, until an offer holds it (0.7.67; that
     * field's lane pushed out before).
     */
    static void makeRoom(Game game, double sqFt, boolean wantDeposit) {
        for (int i = 0; i < 200; i++) {
            if (game.getLandManager().getAvailableSqFt() >= sqFt
                    && (!wantDeposit || game.getLandManager().getIronDeposits() > 0)) {
                return;
            }
            boolean ore = wantDeposit && game.getLandManager().getIronDeposits() == 0;
            LandParcel target = ore ? game.getLandManager().getMarket().richest(Resource.IRON)
                    : game.getLandManager().getMarket().bestValue();
            if (ore && target == null) target = towardIron(game);
            // The ore is priced in the ground at US$0.40 a tonne (1/212.5 of its world price since 0.7.120, 1/350 until then). The
            // world's fields come whole - the field the default world put by
            // its founding site was 35 sites, about US$180M, and IRON_SEED's
            // is 36 (0.7.99), more than a new city's treasury (0.7.57, and
            // again since 0.7.64; from 0.7.58 to
            // 0.7.63 they were shared site by site, a site about US$5.3M).
            // Either way the fixture is given the price of a deposit it cannot
            // pay for, so it is the deposit that is tested and not the
            // treasury.
            if (ore && target != null && target.hasIron() && !game.canAffordParcel(target)) {
                game.setCashForTest(game.getCash() + target.localPrice(game.getForeignAccounts().getRate()));
            }
            if (target == null || !game.buyLandParcel(target.getId())) {
                if (!game.buyLandBlock()) return;
            }
        }
    }

    /** The offer standing nearest the iron field nearest the city's site that it does not own: buying toward it is the shortest way to a deposit (0.7.67; that field's lane pushed out until 0.7.66). Null when no field lies in the nine cells round the site. */
    static LandParcel towardIron(Game game) {
        CityLand land = game.getCityLand();
        World world = game.getWorld();
        long cx = land.siteX() / World.CELL, cy = land.siteY() / World.CELL;
        Deposit nearest = null;
        double best = Double.MAX_VALUE;
        for (long y = cy - 1; y <= cy + 1; y++) {
            for (long x = cx - 1; x <= cx + 1; x++) {
                if (x < 0 || y < 0 || x >= World.CELLS || y >= World.CELLS) continue;
                for (Deposit d : world.fieldsInCell((int) (y * World.CELLS + x), Resource.IRON)) {
                    double r = LegacyLand.radius(d.x() - land.siteX(), d.y() - land.siteY());
                    if (land.ownsPlot(d.x(), d.y()) || r >= best) continue;
                    best = r;
                    nearest = d;
                }
            }
        }
        if (nearest == null) return null;
        return nearestOffer(game.getLandManager().getMarket(), nearest.x(), nearest.y());
    }

    /**
     * The offer standing nearest plot (x, y), L-infinity from its rectangle,
     * the lower id on a tie; null with nothing listed. On the block grid
     * (0.7.67) buying it, and then the next nearest, grows the city toward
     * the plot until an offer holds it: what the harnesses that want a field
     * or the sea do where they pushed out a lane before.
     */
    static LandParcel nearestOffer(LandMarket market, long x, long y) {
        LandParcel best = null;
        long far = Long.MAX_VALUE;
        for (LandParcel p : market.getListing()) {
            long dx = x < p.getX0() ? p.getX0() - x : x >= p.getX1() ? x - (p.getX1() - 1) : 0;
            long dy = y < p.getY0() ? p.getY0() - y : y >= p.getY1() ? y - (p.getY1() - 1) : 0;
            long d = Math.max(dx, dy);
            if (best == null || d < far || (d == far && p.getId() < best.getId())) {
                best = p;
                far = d;
            }
        }
        return best;
    }

    /** The share of its sales an arc furnace keeps on imported scrap, selling its steel at home, at least and at most (runs/spec-materials.md 4, from the study's 2.5: the furnace about 7%, the Mini-Mill 13%). */
    static final double IMPORTED_SCRAP_LOW = .05, IMPORTED_SCRAP_HIGH = .15;

    /** ...and on the city's own scrap, at its export price (the study's 25% and 31%). */
    static final double LOCAL_SCRAP_LOW = .20, LOCAL_SCRAP_HIGH = .35;

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        /* ================= 1. the band ================= */
        out.println("--- the price band ---");

        // The ore market is one GoodsMarket now (2026-09-11, the sector
        // template): the same band IronMarket struck, generalised to every
        // good. The ceiling is the import price - what a tonne of ore costs
        // landed - and the floor is the export price a mine gets shipping
        // out. Until 0.7.120 the ceiling was the mills' scrap: IRON was both.
        GoodsMarket market = new GoodsMarket(Good.IRON);

        assertTrue("a landed tonne of ore is the ceiling", market.ceiling() > 0);
        assertTrue("the mines' export price is the floor",
                market.floor() > 0 && market.floor() < market.ceiling());
        close("...and they are the world's two prices for ore",
                market.ceiling() - market.floor(),
                Good.IRON.worldImportPrice() - Good.IRON.worldExportPrice());
        out.printf("   the band runs $%.0f to $%.0f%n",
                market.floor() * 1000, market.ceiling() * 1000);

        // Nobody mining: whoever wants ore lands it.
        market.strike(0, 0, 5000);
        close("no mines - price sits at the import ceiling",
                market.getLocalPrice(), market.ceiling());

        // Mining hard, nobody buying: the mines export, and will not go below it.
        market.strike(5000, 0, 0);
        close("no buyers - price sits at the export floor",
                market.getLocalPrice(), market.floor());

        // The one that caught a real design error: demand/supply clamped to 1
        // put a matched pair at the ceiling, so the mine took everything and
        // the mill gained nothing at all.
        market.strike(5000, 0, 5000);
        close("supply meets demand - the middle of the band",
                market.getLocalPrice(),
                (market.ceiling() + market.floor()) / 2);

        market.strike(15000, 0, 5000);
        assertTrue("three times the ore that is wanted - down near the floor",
                market.getPriceIndex() < .3);

        market.strike(5000, 0, 15000);
        assertTrue("three times the demand - up near the ceiling",
                market.getPriceIndex() > .7);

        // Monotonic: more ore never makes ore dearer.
        double previous = 1;
        boolean falling = true;
        for (int supply = 500; supply <= 40000; supply += 500) {
            market.strike(supply, 0, 5000);
            if (market.getPriceIndex() > previous + 1e-12) falling = false;
            previous = market.getPriceIndex();
        }
        assertTrue("more ore never makes ore dearer", falling);

        // The band is the whole mechanic: outside it, one side would rather
        // trade with the rest of the world.
        boolean insideBand = true;
        for (int supply = 0; supply <= 20000; supply += 250) {
            market.strike(supply, 0, 5000);
            if (market.getLocalPrice() < market.floor() - 1e-9
                    || market.getLocalPrice() > market.ceiling() + 1e-9) {
                insideBand = false;
            }
        }
        assertTrue("the price never leaves the band", insideBand);

        // ...and a stockpile is supply too, a sixth of it a month.
        market.strike(0, 30000, 5000);
        close("a warehouse counts as a sixth of itself a month",
                market.getLocalPrice(), (market.ceiling() + market.floor()) / 2);

        /*
         * ...AND SCRAP IS ITS OWN GOOD SINCE 0.7.120 (batch I3; Jerus's Q4,
         * "split"): IRON's old import price moved to it, and the arc furnaces
         * buy it. Ore and scrap are different things at different prices -
         * ore at about a quarter of scrap's - and a furnace that took one for
         * the other was the old row's whole confusion.
         */
        assertTrue("scrap is a good of its own: what IRON's import price was (.41), a flow good the world sells and buys",
                Good.SCRAP.worldImportPrice() == .41 && Good.SCRAP.importable() && Good.SCRAP.exportable()
                        && !Good.SCRAP.stockable() && Good.SCRAP.traded());
        BuildingManager catalogue = new BuildingManager();
        catalogue.initializeTemplates();
        assertTrue("...dearer than ore at both ends, and the furnaces' feed, not the mines' output",
                Good.SCRAP.worldExportPrice() > Good.IRON.worldImportPrice()
                        && catalogue.getTemplate(HeavyIndustry.ARC_FURNACE).uses(Good.SCRAP) > 0
                        && catalogue.getTemplate(HeavyIndustry.MINI_MILL).uses(Good.SCRAP) > 0
                        && catalogue.getTemplate(Mining.IRON_MINE).makes(Good.SCRAP) == 0);

        /* ================= 2. a mine needs ground with ore in it ================= */
        out.println("\n--- a mine needs a deposit ---");

        Path root = Files.createTempDirectory("mining");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));

        // On IRON_SEED's world, whose founding site has an iron field a kilometre out (0.7.99: the default world's is twelve).
        Game city = new Game(files, Founding.defaults().withWorldSeed(IRON_SEED));
        System.setOut(quiet);
        city.run();
        System.setOut(out);

        BuildingsTemplate mine = city.getBuildingManager().getTemplate(Mining.IRON_MINE);

        /*
         * The band's two ends are WORLD PRICES, in the city's money, and
         * this is where that gets checked, because it is not obvious and it
         * wastes an afternoon when forgotten. They used to be building data
         * - the mine's productionModifier1 and the foundry's
         * productionModifier2 - and setting either on the market looked like
         * it worked and was silently overwritten the next month. Since the
         * sector template they live on Good.IRON, once, and the market
         * multiplies by the exchange rate it was last handed.
         */
        GoodsMarket live = city.getMarkets().get(Good.IRON);
        close("the floor IS the world's price for the city's ore",
                live.floor(), Good.IRON.worldExportPrice() * live.getExchangeRate());
        close("the ceiling IS what a landed tonne of ore costs",
                live.ceiling(), Good.IRON.worldImportPrice() * live.getExchangeRate());
        assertTrue("...and the mine's own template no longer carries a price of its own",
                mine.makes(Good.IRON) > 0);

        assertTrue("the Iron Mine is its own category",
                mine.getCategory() == BuildingType.MINING);
        /* ===================================================================
           THE MINE IS NOT THE BIGGEST EMPLOYER IN THE GAME ANY MORE, AND IT
           SHOULD NEVER HAVE BEEN (2026-09-09, rebalance stage two).

           It employed 376 people to lift 2,500 tonnes a month - 30,000 tonnes a
           YEAR, which is a quarry, not a mine. That is about 80 tonnes per
           worker per year against a real open-pit figure in the thousands, and
           the whole of the old iron price band was built on top of it:
           IronMarket's note recorded that the mine's cost was $159 a tonne and
           worked backwards from there to a $200 export floor, which is double
           the real seaborne price.

           Raising wages to real ones made that unpayable - 376 people at real
           wages is $554 a tonne of payroll alone - and the honest fix was the
           crew rather than the price. Sixty people lifting 30,000 t/yr costs
           about $103 a tonne and lives comfortably on $140 ore, which is a real
           domestic pellet price. Three hundred and sixteen jobs left the city
           with it; they were jobs the ore price was paying for and the ore
           price could not pay for.

           (SINCE 0.7.120 the same sixty lift 20,000 t a month, 4,000 t a post a
           year - the small end of a real mine - for ore at its world price, $85
           out and $115 in: Jerus's Q5.)

           So the assertion is now that a mine is a SERIOUS employer, not the
           largest one. What it is measured against is deliberately the smallest
           thing that makes the claim mean anything - a Small Grocery Store -
           rather than a Food Processing Plant, which is a genuinely large
           industrial employer and should out-employ a small mine.
           =================================================================== */
        assertTrue("...and is still a serious employer",
                mine.getTotalJobs() > template(city, "Small Grocery Store").getTotalJobs());
        out.printf("   a mine employs %d people; a grocery %d; a food plant %d%n",
                mine.getTotalJobs(),
                template(city, "Small Grocery Store").getTotalJobs(),
                template(city, "Bakery").getTotalJobs());
        /*
         * EIGHT TIMES THE ORE ON THE SAME SIXTY POSTS (0.7.120, batch I3; the
         * study's 2.4): 20,000 t a month on 60,000 L of diesel, 600 kW and 20
         * units of water, for its old cost's cash, points and material x 4.632.
         */
        assertTrue("the Iron Mine lifts 20,000 t a month with the same 60 posts (45/12/3), on 60,000 L of diesel, 600 kW and 20 units"
                        + " of water", mine.makes(Good.IRON) == 20_000 && mine.getTotalJobs() == 60
                        && mine.getJobs(JobType.NO_DIPLOMA) == 45 && mine.getJobs(JobType.DIPLOMA) == 12
                        && mine.getJobs(JobType.COLLEGE_ENGINEERING) == 3 && mine.uses(Good.DIESEL) == 60_000
                        && mine.getElectricityConsumption() == 600 && mine.getWaterConsumption() == 20);
        double allIn = mine.getCashCost() + BuildingManager.MATERIALS_WORLD_PRICE * mine.getConstructionMaterials();
        report("...for D$45M all in: its cash, points and material each x 4.632 (7,286, 1,600 and 135 until 0.7.120)",
                mine.getCashCost() == Math.round(7286 * 4.632) && mine.getConstructionPoints() == Math.round(1600 * 4.632)
                        && mine.getConstructionMaterials() == Math.round(135 * 4.632) && Math.abs(allIn - 45_000) < 10,
                String.format("D$%,.0fk", allIn));
        assertTrue("...carrying the old one's road load a tonne: 420 for 2,500 t, 3,360 for 20,000",
                mine.getRoadLoad() * 2_500 == 420 * mine.makes(Good.IRON));

        System.setOut(quiet);
        Game.BuildResult refused = city.buildStack(mine, 1, false);
        System.setOut(out);

        assertTrue("a city with no deposit cannot build one",
                refused == Game.BuildResult.NO_DEPOSIT);
        assertTrue("...and it is refused for the RIGHT reason, not for money",
                refused != Game.BuildResult.NEEDS_FUNDING);

        System.setOut(quiet);
        makeRoom(city, mine.getLandSqFt() * 2, true);
        System.setOut(out);

        assertTrue("buying a parcel with iron gives the city a deposit",
                city.getLandManager().getIronDeposits() > 0);

        // A D$45M mine is more than a young city's treasury once it has bought its field: the fixture is given the price, so it is the deposit that is tested.
        city.setCashForTest(city.getCash() + 2 * allIn);
        System.setOut(quiet);
        Game.BuildResult allowed = city.buildStack(mine, 1, false);
        System.setOut(out);
        assertTrue("...and now the mine can be ordered",
                allowed == Game.BuildResult.SUCCESS);

        /*
         * ONE SITE, ONE MINE (0.7.57). This was "but one deposit only supports
         * one mine", ordering a second and seeing it refused: a parcel's ore
         * was usually one site. A deposit is a field of the world now, and the
         * one by IRON_SEED's founding site holds many; what stands is that each site
         * takes one mine - as many more as the sites left, and not one more.
         */
        int sites = city.getLandManager().getIronDeposits();
        assertTrue("but each site supports only one mine: as many more as the sites left, and not one more",
                city.minesCommitted() == 1 && city.hasDepositFor(mine, sites - 1) && !city.hasDepositFor(mine, sites));

        /* ================= 3. what an arc furnace makes of scrap ================= */
        out.println("\n--- and now the only question that matters: an arc furnace's margin, on imported scrap and on the city's own ---");

        /*
         * MEASURED AS A MARGIN, NOT AS A DOLLAR FIGURE, and the design sentence
         * moved with steel's price (0.7.120, batch I3).
         *
         * To 0.7.119 this section ran two cities for three years - a foundry on
         * imported scrap, and the same foundry with a mine next door - and
         * asserted 20-36% of its sales on scrap and 48-62% on local ore: "a
         * different business, not a better month". Both halves of that are
         * gone. A furnace melts scrap, not ore; and at steel's world price
         * (.650 landed, .470 shipped) a furnace on imported scrap barely pays
         * selling at home and loses money exporting, which is what a real arc
         * furnace in a scrap-poor town does. What still makes it a business is
         * the city's own scrap at its export price: about 7% of its sales on
         * imported scrap and 25% on the city's own for the furnace, 13% and
         * 31% for the Mini-Mill (the study's 2.5).
         *
         * WALKED BY HAND, as section 4 walks a mine: one furnace standing, a
         * market at parity, the game's founding wages and tariffs; its steel
         * sold to the city's fabricators at the landed price (the STEEL
         * market struck with buyers and no mill, at its ceiling), its scrap
         * bought from the world at the SCRAP ceiling or from the city at its
         * floor (the market struck with sellers and no furnace: the export
         * price, the study's "valued at its export price"). Nothing else in a
         * city moves the number; there is no city.
         */
        for (int id : new int[] { HeavyIndustry.ARC_FURNACE, HeavyIndustry.MINI_MILL }) {
            double[] imported = furnaceMonth(city, id, false);
            double[] own = furnaceMonth(city, id, true);
            String name = city.getBuildingManager().getTemplate(id).getName();
            BuildingsTemplate t = city.getBuildingManager().getTemplate(id);
            double onImported = imported[0] / imported[1], onOwn = own[0] / own[1];
            out.printf("   %-21s importing scrap at $%3.0f/t: $%,9.2fk on $%,.0fk = %5.1f%%   own scrap at $%3.0f/t: $%,9.2fk = %5.1f%%%n",
                    name, imported[2] * 1000, imported[0], imported[1], onImported * 100,
                    own[2] * 1000, own[0], onOwn * 100);
            assertTrue("fixture: " + name + "'s steel sold at home at the landed price, its scrap at the world's two prices",
                    imported[3] == Good.STEEL.worldImportPrice() && own[3] == imported[3]
                            && imported[2] == Good.SCRAP.worldImportPrice() && own[2] == Good.SCRAP.worldExportPrice()
                            && imported[1] == t.makes(Good.STEEL) * Good.STEEL.worldImportPrice());
            report("on imported scrap the " + name + " keeps an arc furnace's thin margin: "
                            + Math.round(IMPORTED_SCRAP_LOW * 100) + "-" + Math.round(IMPORTED_SCRAP_HIGH * 100) + "% of its sales",
                    onImported > IMPORTED_SCRAP_LOW && onImported < IMPORTED_SCRAP_HIGH,
                    String.format("%.1f%%", onImported * 100));
            report("...and on the city's own scrap a living one: " + Math.round(LOCAL_SCRAP_LOW * 100) + "-"
                            + Math.round(LOCAL_SCRAP_HIGH * 100) + "%",
                    onOwn > LOCAL_SCRAP_LOW && onOwn < LOCAL_SCRAP_HIGH, String.format("%.1f%%", onOwn * 100));
            assertTrue("...the difference is the scrap's band and nothing else: its tonnes at the import less the export price",
                    Math.abs((own[0] - imported[0]) - t.uses(Good.SCRAP)
                            * (Good.SCRAP.worldImportPrice() - Good.SCRAP.worldExportPrice())) < 1e-6);
        }

        /* ============ 4. and is the mine worth sinking? ============ */
        out.println("\n--- a mine has to pay even with nobody to sell to ---");

        /*
         * ASKED OF THE HANDLER, NOT OF A CITY
         *
         * A mine with no mill cannot be staged through Game any more. Steel is
         * profitable enough now that the investment engine builds a foundry
         * within six months, so a city started with a mine and no mill finishes
         * the run with both - which is the mechanic working exactly as intended
         * and completely useless as a control.
         *
         * So the worst case is put to the books directly: every tonne exported,
         * at the floor, with no local buyer at any price.
         *
         * (FROM 0.7.120 nothing in the city bought ore - the furnaces melt
         * scrap - so this was the mine's month until the integrated route's
         * Pelletising Plant and blast furnaces came, at 0.7.123 (batch I4), and
         * it still is wherever none of them stands. It pays for its
         * diesel and its consumables now, at the world's prices, and its crew
         * at the game's founding wages, PayTier's - .8, 1.5 and 4.0 until
         * then, the wages of 2026-09-09.)
         */
        // A bare mining sector off the template, with one mine standing and
        // a market to ship into. The month is walked by hand the way
        // Markets.clearMonth walks it: lift, offer, nobody buys, ship the
        // rest abroad, strike.
        double[] rates = new double[JobType.values().length];
        long[] crew = new long[JobType.values().length];
        for (JobType j : JobType.values()) {
            rates[j.ordinal()] = PayTier.wageOf(j);
            crew[j.ordinal()] = mine.getJobs(j);
        }

        double[] fullFill = new double[JobType.values().length];
        java.util.Arrays.fill(fullFill, 1.0);

        BuildingManager oneMine = new BuildingManager();
        oneMine.initializeTemplates();
        oneMine.addStack(oneMine.getTemplate(Mining.IRON_MINE), 1, true);
        Markets bareMarkets = new Markets();
        bareMarkets.setExchangeRate(1);
        GoodsMarket ore = bareMarkets.get(Good.IRON);
        GoodsMarket diesel = bareMarkets.get(Good.DIESEL);
        Sector worstCase = new ham.citybuildersim.sectors.Mining();
        worstCase.attach(oneMine, bareMarkets);

        double floor = ore.floor();
        double tonnes = mine.makes(Good.IRON);
        worstCase.setElectricityConsumption(mine.getElectricityConsumption());
        worstCase.setWaterConsumption(mine.getWaterConsumption());
        worstCase.setPricePerWatt(city.getEconomyManager().getPricePerWatt());
        worstCase.setPricePerWaterUnit(city.getEconomyManager().getPricePerWaterUnit());
        worstCase.updateJobFillRate(fullFill);
        worstCase.updateWages(rates, crew);

        ore.strike(tonnes, 0, 0);                        // nobody local wants any
        worstCase.produceFlow(Good.IRON);
        worstCase.offer(Good.IRON, ore.getLocalPrice());
        worstCase.shipUnsoldFlow(Good.IRON, ore);        // every tonne abroad, at the floor
        // ...its diesel landed from the world, and its consumables bought abroad (0.7.120).
        worstCase.bookPurchase(diesel.record(Trade.WORLD, worstCase.key(), worstCase.bid(Good.DIESEL), diesel.importPrice()));
        worstCase.buyConsumables();
        worstCase.strike();

        Sector.Statement w = worstCase.statement();
        double exportOnly = w.preTaxIncome;
        double perTonne = (w.payroll + w.electricity + w.water + w.maintenance + w.inputs) / tonnes;
        close("fixture: every tonne left at the floor", w.exports, tonnes * floor);
        close("fixture: ...its 60,000 L of diesel landed, and its consumables bought abroad at parity",
                w.inputs, mine.uses(Good.DIESEL) * diesel.importPrice() + mine.getConsumables());

        out.printf("   %,.0f t/month at the $%.0f floor%n",
                tonnes, floor * 1000);
        out.printf("   costs $%.2f a tonne to lift, sells for $%.0f%n",
                perTonne * 1000, floor * 1000);
        out.printf("   net $%,.2fk a month on a $%,.0fk building, all in%n",
                exportOnly, allIn);

        assertTrue("a mine lifts ore for less than the export floor",
                perTonne < floor);
        assertTrue("...so exporting alone is profitable, with no mill anywhere",
                exportOnly > 0);
        assertTrue("...comfortably, not marginally: over 2% of its cost all in a month (the study's 2.7%)",
                exportOnly / allIn > .02);

        /*
         * AND WHO IN THE CITY BUYS IT: since the integrated route (0.7.123,
         * batch I4) the Pelletising Plant and the two blast furnaces, Iron &
         * Steel's. To 0.7.119 this section's last line was "a mill next door
         * is worth more to a mine than exporting": the foundry bought IRON, ore
         * or scrap; from 0.7.120 a furnace bought SCRAP and nothing used ore.
         * Ore's co-location reward - replacing imported ore at the landed
         * price, 4.1% a month against 2.7% (the study's case C) - is a works
         * of the route's, held in MaterialsCheck 11's cases.
         */
        java.util.Set<Integer> oreBuyers = new java.util.TreeSet<>();
        for (BuildingsTemplate t : oneMine.getTemplates()) if (t.uses(Good.IRON) > 0) oreBuyers.add(t.getId());
        assertTrue("since batch I4 the catalogue's ore buyers are Iron & Steel's Pelletising Plant and its two blast furnaces: "
                        + oreBuyers, oreBuyers.equals(new java.util.TreeSet<>(java.util.List.of(
                        ham.citybuildersim.sectors.HeavyIndustry.PELLETISING_PLANT, ham.citybuildersim.sectors.HeavyIndustry.MINI_BLAST_FURNACE,
                        ham.citybuildersim.sectors.HeavyIndustry.BLAST_FURNACE))));

        cleanUp(root);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-58s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    /**
     * One arc furnace's month, walked by hand (section 3): one standing, a
     * market at parity, the game's founding wages (PayTier) and the city's
     * tariffs; its steel sold at home at the landed price, its scrap bought
     * from the world at the SCRAP ceiling or, `ownScrap`, from the city at
     * its floor; its consumables bought abroad.
     *
     * @return {its income before tax, its sales, what its scrap cost a tonne, what its steel fetched a tonne}
     */
    static double[] furnaceMonth(Game city, int id, boolean ownScrap) {
        BuildingManager b = new BuildingManager();
        b.initializeTemplates();
        BuildingsTemplate t = b.getTemplate(id);
        b.addStack(t, 1, true);
        Markets markets = new Markets();
        markets.setExchangeRate(1);
        Sector mills = new HeavyIndustry();
        mills.attach(b, markets);
        double[] rates = new double[JobType.values().length], fill = new double[JobType.values().length];
        long[] crew = new long[JobType.values().length];
        for (JobType j : JobType.values()) {
            rates[j.ordinal()] = PayTier.wageOf(j);
            crew[j.ordinal()] = t.getJobs(j);
            fill[j.ordinal()] = 1;
        }
        mills.setElectricityConsumption(t.getElectricityConsumption());
        mills.setWaterConsumption(t.getWaterConsumption());
        mills.setPricePerWatt(city.getEconomyManager().getPricePerWatt());
        mills.setPricePerWaterUnit(city.getEconomyManager().getPricePerWaterUnit());
        mills.updateJobFillRate(fill);
        mills.updateWages(rates, crew);

        GoodsMarket scrap = markets.get(Good.SCRAP), steel = markets.get(Good.STEEL);
        // THE FIXTURE CAUSES IT: steel wanted and no mill (the ceiling); scrap offered and no furnace (the floor), or wanted (the ceiling).
        steel.strike(0, 0, t.makes(Good.STEEL));
        if (ownScrap) scrap.strike(t.uses(Good.SCRAP), 0, 0); else scrap.strike(0, 0, t.uses(Good.SCRAP));
        mills.produceFlow(Good.STEEL);
        double made = mills.outputRow(Good.STEEL).produced;
        mills.bookPurchase(scrap.record(ownScrap ? Trade.HOUSEHOLDS : Trade.WORLD, mills.key(), mills.bid(Good.SCRAP),
                scrap.getLocalPrice()));
        mills.offer(Good.STEEL, steel.getLocalPrice());
        mills.bookSale(steel.record(mills.key(), Sectors.MANUFACTURING, made, steel.getLocalPrice()));
        mills.shipUnsoldFlow(Good.STEEL, steel);
        mills.buyConsumables();
        mills.strike();
        Sector.Statement s = mills.statement();
        return new double[] { s.preTaxIncome, s.revenue, scrap.getLocalPrice(), steel.getLocalPrice() };
    }

    static void cleanUp(Path root) {
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }
    }
}

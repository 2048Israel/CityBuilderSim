package ham.citybuildersim;

import ham.citybuildersim.sectors.Agriculture;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The ground under the loaf.
 *
 * WHAT THIS HAS TO PROVE. Not that the city got bigger - it did not, and the
 * measurement is in claude/farms.md where a number that will drift belongs.
 * What is checkable is whether the tenth sector does the four things it was
 * built to do:
 *
 *   1. the mills BUY their raw material now, and the chain is four links deep
 *   2. crops clear in a band with a real import ceiling, so a city with no
 *      fields eats anyway and pays for the privilege
 *   3. a farm is ground and almost nothing else - and the clock that runs on
 *      that ground is what decides fields against glass
 *   4. the farmland dial actually moves the bill, and the planner reads it
 *
 * Every fixture CAUSES its condition. The clock test moves the land price
 * rather than waiting three centuries for it; the relief test sets the dial
 * rather than hoping a run finds it.
 */
public class AgricultureCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-64s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void check(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) {
            fails++;
            out.printf("%-64s FAIL  %.6f != %.6f%n", label, actual, expected);
        } else {
            out.printf("%-64s OK%n", label);
        }
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    static final String MIXED = "Mixed Farm";
    static final String GRAIN = "Grain Farm";
    static final String GLASS = "Greenhouse Complex";

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        /* ============ 1. the good, and the chain it closes ============ */
        out.println("--- a crop is a thing the world will sell you ---");

        assertTrue("crops can be imported - a city with no fields still eats",
                Good.CROPS.importable());
        assertTrue("...and exported, so a farming city can ship its surplus",
                Good.CROPS.exportable());
        assertTrue("...and stored, because a harvest keeps and that is what a silo is",
                Good.CROPS.stockable());
        assertTrue("crops clear on the band", Good.CROPS.traded());
        assertTrue("a tonne costs more to bring in than a farm gets shipping one out",
                Good.CROPS.worldImportPrice() > Good.CROPS.worldExportPrice());
        assertTrue("crops are sold by the tonne", "tonne".equals(Good.CROPS.unit()));

        Path root = Files.createTempDirectory("agcheck");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        Game plain = new Game(files);
        quietly(plain::run);
        BuildingManager bm = plain.getBuildingManager();

        /* ---- the chain: ground -> crops -> food -> groceries ---- */
        BuildingsTemplate mill = bm.getTemplateByName("Textile Mill");
        BuildingsTemplate plant = bm.getTemplateByName("Food Processing Plant");
        assertTrue("the Textile Mill buys crops, which it never did before",
                mill.uses(Good.CROPS) > 0);
        assertTrue("...and so does the Food Processing Plant",
                plant.uses(Good.CROPS) > 0);
        check("a tonne of crops is 9.5 units of food at the mill",
                mill.makes(Good.FOOD) / mill.uses(Good.CROPS), Agriculture.FOOD_PER_TONNE, .05);
        check("...and at the plant",
                plant.makes(Good.FOOD) / plant.uses(Good.CROPS), Agriculture.FOOD_PER_TONNE, .05);
        assertTrue("the mills declare crops as an input on the sector, not just the template",
                plain.getSectors().industry().isUser(Good.CROPS));
        assertTrue("...and the fields declare them as an output",
                plain.getSectors().agriculture().isMaker(Good.CROPS));

        /*
         * THE MILLS HAVE TO SURVIVE THE CEILING, which is what a city with no
         * fields of its own pays, because that is every city on its founding
         * day. Struck from the templates at the founding rate rather than
         * measured off a run, so it is a statement about the numbers.
         */
        out.println("\n   what a mill clears, at the world's crop price:");
        for (BuildingsTemplate t : new BuildingsTemplate[] { mill, plant }) {
            double revenue = t.makes(Good.FOOD)
                    * (Good.FOOD.worldImportPrice() + Good.FOOD.worldExportPrice()) / 2;
            double crops = t.uses(Good.CROPS) * Good.CROPS.worldImportPrice();
            double payroll = payrollOf(t);
            double utilities = t.getElectricityConsumption() * plain.getEconomyManager().getPricePerWatt()
                    + t.getWaterConsumption() * plain.getEconomyManager().getPricePerWaterUnit();
            double op = revenue - crops - payroll - utilities;
            out.printf("   %-22s $%,8.0fk   crops %4.1f%%  wages %4.1f%%  ->  %4.1f%% left%n",
                    t.getName(), revenue, crops / revenue * 100,
                    payroll / revenue * 100, op / revenue * 100);
            assertTrue(t.getName() + ": the crop bill is a real processor's, not a token",
                    crops / revenue > .20 && crops / revenue < .40);
            assertTrue(t.getName() + ": ...and it still clears the band every plant clears",
                    op / revenue > .25 && op / revenue < .55);
        }

        /* ============ 2. the band, and the city with no fields ============ */
        out.println("\n--- a city with no fields eats anyway, and pays for it ---");

        GoodsMarket crops = new GoodsMarket(Good.CROPS);
        crops.strike(0, 0, 5000);
        check("nobody growing any - the mills pay what the world charges",
                crops.getLocalPrice(), crops.ceiling(), 1e-12);
        crops.strike(5000, 0, 0);
        check("nobody buying any - the fields get what the ship pays",
                crops.getLocalPrice(), crops.floor(), 1e-12);
        crops.strike(5000, 0, 5000);
        check("...and a matched pair splits the difference",
                crops.getLocalPrice(), (crops.floor() + crops.ceiling()) / 2, 1e-9);
        out.printf("   floor $%.0f, ceiling $%.0f a tonne%n",
                crops.floor() * 1000, crops.ceiling() * 1000);

        /* ============ 3. a farm is ground and almost nothing else ============ */
        out.println("\n--- three farms, and the clock that runs on them ---");

        String[] names = { MIXED, GRAIN, GLASS };
        // tonnes a month, posts, land sq ft, all-in build cost
        double[][] want = {
            { 125,  3,  600000,   312 },
            { 500,  4, 2400000,  1506 },
            { 900, 17,  150000,  7472 },
        };
        for (int i = 0; i < names.length; i++) {
            BuildingsTemplate t = bm.getTemplateByName(names[i]);
            assertTrue(names[i] + " exists", t != null);
            if (t == null) continue;
            check(names[i] + ": what it grows a month", t.makes(Good.CROPS), want[i][0], 0);
            check(names[i] + ": posts", jobsOf(t), want[i][1], 0);
            check(names[i] + ": the ground it stands on", t.getLandSqFt(), want[i][2], 1);
            check(names[i] + ": all-in build cost",
                    t.getCashCost() + BuildingManager.MATERIALS_WORLD_PRICE * t.getConstructionMaterials(),
                    want[i][3], 2);
            assertTrue(names[i] + " is owned by the sector, not the city",
                    Sectors.AGRICULTURE.equals(t.getSector()));
            assertTrue(names[i] + " has a silo - a harvest keeps", t.stocks(Good.CROPS) > 0);
            assertTrue(names[i] + " needs no licence to grow anything",
                    t.getRequiresLicence() == null);
        }

        BuildingsTemplate mixed = bm.getTemplateByName(MIXED);
        BuildingsTemplate grain = bm.getTemplateByName(GRAIN);
        BuildingsTemplate glass = bm.getTemplateByName(GLASS);
        BuildingsTemplate foundry = bm.getTemplateByName("Steel Foundry");

        /*
         * LABOUR AND ENERGY CHEAP, GROUND EXPENSIVE - Jerus's brief, asserted
         * rather than described. A field is five people on fifty-five acres and
         * the glass is forty-five on nine, which is the sector in one line and
         * is what both of those really are.
         */
        out.printf("   ground a post: mixed %,.0f, grain %,.0f, glass %,.0f,"
                + " a steel foundry %,.0f sq ft%n",
                mixed.getLandSqFt() / jobsOf(mixed), grain.getLandSqFt() / jobsOf(grain),
                glass.getLandSqFt() / jobsOf(glass), foundry.getLandSqFt() / jobsOf(foundry));
        assertTrue("a field takes more ground a post than any factory in the game",
                mixed.getLandSqFt() / jobsOf(mixed) > foundry.getLandSqFt() / jobsOf(foundry)
                        && grain.getLandSqFt() / jobsOf(grain) > foundry.getLandSqFt() / jobsOf(foundry));
        assertTrue("...and far more ground a tonne than glass does",
                grain.getLandSqFt() / grain.makes(Good.CROPS)
                        > 20 * glass.getLandSqFt() / glass.makes(Good.CROPS));
        assertTrue("glass pays for its density in power",
                glass.getElectricityConsumption() / glass.makes(Good.CROPS)
                        > 5 * grain.getElectricityConsumption() / grain.makes(Good.CROPS));
        assertTrue("a field's wage bill is small against what it sells",
                payrollOf(grain) < .25 * grain.makes(Good.CROPS) * Good.CROPS.worldExportPrice());

        /*
         * THE CLOCK, CAUSED. Move the land price - nothing else - and watch
         * which farm still pays for the ground it needs. This is the whole
         * design of the sector and it is one arithmetic.
         */
        out.println("\n   what the ground costs, and what still pays for it:");
        double[] prices = { .0013, .006, .020, .060 };
        boolean fieldsStopped = false, glassHeld = true;
        for (double px : prices) {
            double mixedAll = mixed.getLandSqFt() * px + want[0][3];
            double grainAll = grain.getLandSqFt() * px + want[1][3];
            double glassAll = glass.getLandSqFt() * px + want[2][3];
            out.printf("   $%5.2f/sqft:  mixed %,9.0fk  grain %,9.0fk  glass %,9.0fk%n",
                    px * 1000, mixedAll, grainAll, glassAll);
            if (grainAll > 8 * glassAll) fieldsStopped = true;
            if (glassAll > 30 * want[2][3]) glassHeld = false;
        }
        assertTrue("dear ground makes a field cost many times what glass costs",
                fieldsStopped);
        assertTrue("...while the glass is still mostly the glass", glassHeld);

        /* ============ 4. the dial, and what reads it ============ */
        out.println("\n--- farmland is assessed at what the player says ---");

        check("full relief by default, which is what real jurisdictions do",
                TaxPolicy.DEFAULT_FARMLAND_RELIEF, 1.0, 0);

        Game city = new Game(files);
        quietly(() -> {
            city.run();
            BuildingManager b = city.getBuildingManager();
            city.getLandManager().setOwnedSqFt(city.getLandManager().getOwnedSqFt() + 200_000_000L);
            b.addStack(b.getTemplateByName("House"), 600, true);
            b.addStack(b.getTemplateByName("Convenience Store"), 12, true);
            b.addStack(b.getTemplateByName("Coal Power Plant"), 1, true);
            b.addStack(b.getTemplateByName("Water Treatment Plant"), 1, true);
            b.addStack(b.getTemplateByName("Paved Road"), 20, true);
            b.addStack(b.getTemplateByName("Textile Mill"), 2, true);
            b.addStack(b.getTemplateByName(GRAIN), 6, true);
            for (int i = 0; i < 18; i++) city.simulateMonths(1);
        });

        TaxPolicy policy = city.getEconomyManager().getTaxPolicy();
        EconomyManager em = city.getEconomyManager();
        Agriculture fields = city.getSectors().agriculture();
        Sector mills = city.getSectors().industry();

        check("six grain farms is 3,000 tonnes of crop a month",
                fields.getCapacity(Good.CROPS), 3000, 0);
        assertTrue("the fields sold something", fields.statement().revenue > 0);
        assertTrue("the mills bought crops", mills.statement().inputs > 0);
        assertTrue("...from the fields, at home", fields.statement().localSales > 0);
        assertTrue("...and the fields' own crop line shows it",
                mills.input(Good.CROPS).boughtLocal > 0);
        out.printf("   crops clear at $%.4f (floor %.4f, ceiling %.4f); the mills' bill is %.0f%%"
                + " of what they sold%n",
                city.getMarkets().get(Good.CROPS).getLocalPrice(),
                city.getMarkets().get(Good.CROPS).floor(),
                city.getMarkets().get(Good.CROPS).ceiling(),
                city.getSectors().industry().statement().inputs
                        / Math.max(1, mills.statement().revenue) * 100);

        /* ---- the dial moves the bill, and only the fields' ---- */
        double ground = em.landValueOf(fields);
        assertTrue("the fields are standing on real money", ground > 0);

        policy.setFarmlandRelief(1.0);
        double relieved = em.getAssessedValue(fields);
        double millsRelieved = em.getAssessedValue(mills);
        policy.setFarmlandRelief(0.0);
        double taxed = em.getAssessedValue(fields);
        double millsTaxed = em.getAssessedValue(mills);

        out.printf("   the ground under the fields is %s; on the roll at %s relieved, %s not%n",
                Formats.INSTANCE.cash(ground), Formats.INSTANCE.cash(relieved),
                Formats.INSTANCE.cash(taxed));
        check("full relief takes the ground off the roll and leaves the barns",
                relieved, taxed - ground, Math.max(1e-6, ground * 1e-9));
        assertTrue("no relief puts all of it back", taxed > relieved);
        check("and it touches nobody else's assessment", millsRelieved, millsTaxed, 1e-9);

        check("the share on the roll is one for everybody else",
                policy.assessedLandShare(Sectors.INDUSTRY), 1, 0);
        policy.setFarmlandRelief(.4);
        check("...and one less the relief for the fields",
                policy.assessedLandShare(Sectors.AGRICULTURE), .6, 1e-12);
        policy.setFarmlandRelief(2);
        check("the dial cannot be set past full relief", policy.getFarmlandRelief(), 1, 0);
        policy.setFarmlandRelief(-1);
        check("...nor below none", policy.getFarmlandRelief(), 0, 0);

        /* ---- and the bill the sector is actually charged follows it ---- */
        policy.setFarmlandRelief(0);
        quietly(() -> city.simulateMonths(1));
        double billTaxed = city.getEconomyManager().getPropertyTaxCharged(Sectors.AGRICULTURE);
        policy.setFarmlandRelief(1);
        quietly(() -> city.simulateMonths(1));
        double billRelieved = city.getEconomyManager().getPropertyTaxCharged(Sectors.AGRICULTURE);
        out.printf("   the fields' land tax: %s taxed, %s relieved%n",
                Formats.INSTANCE.cash(billTaxed), Formats.INSTANCE.cash(billRelieved));
        assertTrue("the bill the fields are actually charged follows the dial",
                billRelieved < billTaxed);

        /* ---- the planner reads the same dial, or it would sink a farm it could not stand on ---- */
        double reliefOn = city.getBusinessInvestment().standingCostOf(fields, grain);
        policy.setFarmlandRelief(0);
        double reliefOff = city.getBusinessInvestment().standingCostOf(fields, grain);
        policy.setFarmlandRelief(TaxPolicy.DEFAULT_FARMLAND_RELIEF);
        assertTrue("the investment desk prices a prospective farm at the relieved rate too",
                reliefOn < reliefOff);

        /* ---- and the fields yield to housing, which is the rule that saved the city ---- */
        out.println("\n--- and nobody breaks ground while somebody sleeps outside ---");
        BusinessInvestment.Decision asked = fields.plan(city.getBusinessInvestment(), city);
        out.println("   \"" + (asked == null ? "-" : asked.reason) + "\"");
        assertTrue("the sector answers with a reason either way",
                asked != null && asked.reason != null && !asked.reason.isEmpty());

        /* ---- the registry, and a reload ---- */
        assertTrue("Agriculture is the tenth sector",
                Sectors.KEYS.length > 9 && Sectors.KEYS[9].equals(Sectors.AGRICULTURE));
        assertTrue("...and the share register is the sectors and the bank",
                Equity.COMPANIES.length == Sectors.KEYS.length + 1);

        policy.setFarmlandRelief(.35);
        quietly(() -> city.saveGame(10));
        Game back = new Game(files);
        quietly(() -> { back.run(); back.loadGame(10); });
        check("a reloaded city has the same fields",
                back.getSectors().agriculture().getCapacity(Good.CROPS),
                fields.getCapacity(Good.CROPS), 0);
        check("...the same cash",
                back.getEconomyManager().getSectorCash(Sectors.AGRICULTURE),
                city.getEconomyManager().getSectorCash(Sectors.AGRICULTURE), 1e-9);
        check("...and the same farmland dial, which is a policy and has to survive",
                back.getEconomyManager().getTaxPolicy().getFarmlandRelief(), .35, 1e-12);

        out.println();
        if (fails == 0) {
            out.println("Agriculture works: the mills buy what they used to conjure, the world "
                    + "sells crops to a city with no fields, and the ground is the clock.");
        } else {
            out.println(fails + " check(s) failed.");
        }
        System.exit(fails);
    }

    static double jobsOf(BuildingsTemplate t) {
        double n = 0;
        for (JobType job : JobType.values()) n += t.getJobs(job);
        return n;
    }

    static double payrollOf(BuildingsTemplate t) {
        double n = 0;
        for (JobType job : JobType.values()) n += t.getJobs(job) * PayTier.of(job).getMonthlyWage();
        return n;
    }
}

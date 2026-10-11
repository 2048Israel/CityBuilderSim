package ham.citybuildersim;

import ham.citybuildersim.sectors.CoalMining;
import ham.citybuildersim.sectors.ExtractionSector;
import ham.citybuildersim.sectors.SpreadPlanner;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

/**
 * Coal Mining, checked (0.7.126, batch I6; runs/spec-materials.md 2.1, 2.4,
 * 2.10, 4 and 5's I6 row; the study's - materials-chains-research.md - 1.3,
 * 1.4 and 3): the second materials chain's digging industry, on the end of
 * Sectors.KEYS.
 *
 * WHY A HARNESS OF ITS OWN. A new sector gets one (CLAUDE.md, "adding
 * things": a <Name>Check modelled on RestaurantsCheck), and coal brings the
 * one rule MaterialsCheck's shared machinery does not have: a coking mine's
 * coal is raw until a preparation plant washes it, a stream inside the
 * sector and not a good, so its two coals come off one flow. MaterialsCheck
 * keeps the machinery every chain shares; this keeps coal's own.
 *
 * WHAT THIS HAS TO PROVE:
 *
 *   1. THE FLOW (CoalMining.flow(), pure): the study's 3.3 - 500,000 t of
 *      raw coal through a plant washing 500,000 is 275,000 t of coking coal,
 *      100,000 of thermal and 125,000 of spoil, to the tonne; every tonne in
 *      comes out; raw past the plants' wash sells as thermal tonne for tonne,
 *      and a wash past the raw washes only the raw.
 *
 *   2. THE SECTOR ON THE END OF THE REGISTRY: Sectors.KEYS ends with Coal
 *      Mining, after the refinery (Jerus's Q21: iron, coal, stone, copper,
 *      wood); an ExtractionSector on coal making both coals on diesel; and
 *      what is as long as the keys grew with them - Equity's companies (the
 *      keys and the bank), the households' share block, the audit's pools,
 *      the bank's year of sheets. Resource.COAL is priced: THERMAL_COAL for
 *      both its types, 1/200 of thermal coal's export price in the ground.
 *
 *   3. THE FOUR ROWS ARE THE STUDY'S (ids 111 to 114, at parity): their
 *      cost, posts, draw, coal, wash, sites, ground and consumables; and
 *      their four cases, A to D (SpreadPlanner.Case). The study gives coal's
 *      returns in words, not a table (its 3.4), so the comparison is worked
 *      from its own figures: each case's coal at the study's 3.2 prices, less
 *      the study's 3.3 running cost (its diesel in it at D$0.60), over the
 *      study's cost - and the model's four cases (caseEarns() for a mine; a
 *      plant's on the flow's increment) are those to its rounding. Then the
 *      returns the study states: a surface mine 3.3% and a large one 3.6%
 *      exporting, a longwall alone 0.8%, the pair 2.4% (two longwalls to the
 *      plant's 500,000 t), the plant alone 19%.
 *
 *   4. A LONGWALL ALONE SELLS ITS RAW COAL AS THERMAL, TONNE FOR TONNE: the
 *      sector's capacity is the flow's - an Underground Coal Mine standing
 *      with no plant makes 250,000 t of thermal coal and no coking; with a
 *      plant 137,500 t of coking and 50,000 of thermal; a plant on site is
 *      the pipeline's difference.
 *
 *   5. THE TYPES AND THE FIT: coal's fields are thermal or coking at I1's
 *      shares (85% and 15%, within two points of the fields on the default
 *      world), on Earth leaning to coking three to one in fold belts and one
 *      to two on shields (E3); a surface mine stands only on thermal, the
 *      large one on four free sites of one thermal field, the longwall only
 *      on coking (fits()).
 *
 *   6. THE DIG BY TYPE: the month's coal is the lift - each kind's nameplate
 *      at the rate, held to its type's ground - what the market is priced on
 *      and the month makes; at the month's end each type's ground gives up
 *      that coal at its dig (1/.95 at the surface, 1/.80 underground), E by
 *      type rising by it and W exact; a type running short holds its mines
 *      to what is left, and a mine over a worked-out type is spare.
 *
 *   7. THE PLAN: with raw coal unwashed the sector orders a preparation
 *      plant first, its earnings the flow's increment - the coking coal it
 *      makes, less the thermal coal the raw no longer sells as; with none
 *      unwashed, ExtractionSector's rule, the best of the mines that fit.
 *
 *   8. A PLAYED MONTH: with a longwall, a surface mine and a plant standing,
 *      the month's coal is the lift's, the ground gives it up and the audit
 *      closes.
 *
 * Every fixture causes its condition: a town founded on a coking field, its
 * thermal ground by fiat (the first type), the mines placed by hand.
 */
public class CoalMiningCheck {

    static int fails = 0;
    static final PrintStream out = System.out;
    static final PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());

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

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        flow();
        sector();
        rows();
        rawAsThermal();
        typesAndFit();
        dig();
        plan();
        playedMonth();
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ============================ 1. the flow ============================ */

    /** The study's 3.3: a plant's 500,000 t of raw coal, and what comes of it. */
    static final double STUDY_RAW = 500_000, STUDY_COKING = 275_000, STUDY_THERMAL = 100_000, STUDY_SPOIL = 125_000;

    static void flow() {
        out.println("--- 1. the flow: raw coal washed into coking and thermal coal and spoil, the rest sold raw as thermal ---");
        CoalMining.Flow f = CoalMining.flow(0, STUDY_RAW, STUDY_RAW);
        report("500,000 t of raw coal through a plant washing 500,000: 275,000 t coking, 100,000 thermal, 125,000 spoil (the study's 3.3)",
                f.washed() == STUDY_RAW && f.coking() == STUDY_COKING && f.thermal() == STUDY_THERMAL && f.spoil() == STUDY_SPOIL,
                String.format("%,.0f + %,.0f + %,.0f", f.coking(), f.thermal(), f.spoil()));
        assertTrue("...the yields add to one, so every tonne in comes out",
                CoalMining.COKING_YIELD + CoalMining.THERMAL_YIELD + CoalMining.SPOIL_YIELD == 1
                        && f.coking() + f.thermal() + f.spoil() == f.raw() + f.surface());
        CoalMining.Flow alone = CoalMining.flow(0, 250_000, 0);
        assertTrue("raw coal no plant washes sells as thermal coal, tonne for tonne, and makes no coking coal",
                alone.thermal() == 250_000 && alone.coking() == 0 && alone.spoil() == 0 && alone.of(Good.THERMAL_COAL) == 250_000);
        CoalMining.Flow past = CoalMining.flow(100_000, 750_000, 500_000);
        report("past the wash: the surface mines' coal, the washed coal's thermal and the raw past the wash, all thermal",
                past.washed() == 500_000 && past.coking() == STUDY_COKING
                        && past.thermal() == 100_000 + STUDY_THERMAL + 250_000 && past.spoil() == STUDY_SPOIL,
                String.format("%,.0f t thermal, %,.0f coking", past.thermal(), past.coking()));
        CoalMining.Flow short_ = CoalMining.flow(0, 250_000, 500_000);
        assertTrue("...and a wash past the raw washes only the raw: 250,000 t into 137,500 coking and 50,000 thermal",
                short_.washed() == 250_000 && short_.coking() == 137_500 && short_.thermal() == 50_000);
    }

    /* ============================ 2. the sector on the end of the registry ============================ */

    static void sector() {
        out.println("\n--- 2. the sector on the end of the registry, and what grew with it ---");
        String[] keys = Sectors.KEYS;
        assertTrue("Sectors.KEYS ends with Coal Mining, after the refinery (Jerus's Q21: coal the materials chains' first new sector)",
                keys[keys.length - 1].equals(Sectors.COAL_MINING) && keys[keys.length - 2].equals(Sectors.REFINING)
                        && keys.length == 18);
        Sectors s = new Sectors(new BuildingManager(), new Markets());
        CoalMining coal = s.coalMining();
        assertTrue("...an extraction sector on coal, found by its saved name, making both coals and using diesel; Mining's group",
                s.byKey(Sectors.COAL_MINING) == coal && coal instanceof ExtractionSector && coal.resource() == Resource.COAL
                        && coal.isMaker(Good.THERMAL_COAL) && coal.isMaker(Good.COKING_COAL) && coal.isUser(Good.DIESEL)
                        && coal.goodsMade().size() == 2 && coal.group() == BuildingType.MINING);
        assertTrue("Equity's companies are the keys and then the bank, the bank's index the keys' count",
                Equity.COMPANIES.length == keys.length + 1 && Equity.COMPANIES[keys.length - 1].equals(Sectors.COAL_MINING)
                        && Equity.BANK == keys.length && Equity.COMPANIES[Equity.BANK].equals("Bank"));
        assertTrue("...the households' share block a slot a company",
                HouseholdBalance.CELL_SLOTS_BEFORE_ABROAD - HouseholdBalance.CELL_SLOTS_BEFORE_SHARES == Equity.COMPANIES.length);
        assertTrue("...the audit's pools the city, a pool a sector in the keys' order - coal mining's the last - and the three after",
                MoneyAudit.POOL_NAMES.length == keys.length + 4 && MoneyAudit.POOL_NAMES[keys.length].equals("coal mining")
                        && MoneyAudit.POOL_NAMES[keys.length + 1].equals("order book"));
        double[] sheets = new Bank().sheetYearToSave();
        assertTrue("...and the bank's year of sheets files a column of loans and of interim financing a sector",
                (int) sheets[2] == keys.length);
        assertTrue("Resource.COAL is sold as thermal coal - both its types, washing being the sector's flow - at 1/200 of its export"
                        + " price in the ground (the study's 8)",
                Resource.COAL.good() == Good.THERMAL_COAL && Resource.COAL.goodOf(Deposit.FieldType.THERMAL) == Good.THERMAL_COAL
                        && Resource.COAL.goodOf(Deposit.FieldType.COKING) == Good.THERMAL_COAL && Resource.COAL.inGroundShare() == 1.0 / 200
                        && Resource.COAL.goodOf(Deposit.FieldType.HEMATITE) == null);
        double[] km2 = new double[CityLand.AREAS];
        km2[CityLand.TOTAL] = .1;
        km2[CityLand.DRY] = .1;
        double[] none = new double[Resource.values().length], coalIn = none.clone();
        coalIn[Resource.COAL.ordinal()] = Resource.COAL.amountPerSite();
        double ground = .0007, level = 1;
        double with = LandMarket.price(km2, coalIn, ground, level), without = LandMarket.price(km2, none, ground, level);
        report("...so an offer's coal is in its price: a site's 6.5 Mt at US$0.25 a tonne, about US$1.6M (to the US$5k rounding)",
                Math.abs((with - without) - Resource.COAL.amountPerSite() * Good.THERMAL_COAL.worldExportPrice() / 200) <= 5,
                String.format("US$%,.0fk", (with - without)));
        assertTrue("the two coals are the goods batch I4 brought, at the study's 3.2 prices (thermal 75/50, coking 225/180), stockable",
                Good.THERMAL_COAL.worldImportPrice() == .075 && Good.THERMAL_COAL.worldExportPrice() == .050
                        && Good.COKING_COAL.worldImportPrice() == .225 && Good.COKING_COAL.worldExportPrice() == .180
                        && Good.THERMAL_COAL.stockable() && Good.COKING_COAL.stockable());
    }

    /* ============================ 3. the four rows against the study ============================ */

    /** Coal Mining's rows, by their permanent ids, in the study's 3.3 order. */
    static final int[] ROWS = { CoalMining.SURFACE_MINE, CoalMining.LARGE_SURFACE_MINE, CoalMining.UNDERGROUND_MINE,
            CoalMining.PREPARATION_PLANT };

    /** The study's 3.3: each one's cost, D$ thousands all in. */
    static final double[] STUDY_COSTS = { 120_000, 950_000, 800_000, 150_000 };

    /** ...its posts. */
    static final int[] STUDY_POSTS = { 80, 350, 400, 50 };

    /** ...its draw, kW. */
    static final int[] STUDY_KW = { 800, 6_000, 29_000, 4_000 };

    /** ...its running cost a month, D$ thousands (the surface mine's "with 230,000 L diesel" in it, at the study's D$0.60). */
    static final double[] STUDY_RUNS = { 1_070, 5_570, 5_770, 774 };

    /** ...the coal each mine makes a month, t (the longwall's raw), and the plant's wash. */
    static final double[] STUDY_MAKES = { 100_000, 800_000, 250_000, 500_000 };

    /** ...the sites each stands on, and of which type (the study's 1.3 and 3.3; the plant none). */
    static final int[] STUDY_SITES = { 1, 4, 1, 0 };
    static final Deposit.FieldType[] STUDY_TYPES = { Deposit.FieldType.THERMAL, Deposit.FieldType.THERMAL, Deposit.FieldType.COKING, null };

    /** ...the tonnes of seam a tonne of coal takes: 95% of the seam recovered at the surface, 80% by a longwall (the study's 3.1; spec 2.4). */
    static final double SURFACE_DIG = 1 / .95, UNDERGROUND_DIG = 1 / .80;

    /** ...the surface mine's diesel, litres a month (the study's 3.3), and its price in the study's runs, D$ thousands a litre (its 1.5). */
    static final double STUDY_DIESEL_LITRES = 230_000, STUDY_DIESEL = .0006;

    /** ...the plant's ground, hectares (the study's 3.3). */
    static final double STUDY_PLANT_HECTARES = 19;

    /** The study's rounding: its returns to a tenth of a point, the plant's 19% to the point. */
    static final double STUDY_ROUNDING = .05, STUDY_ROUNDING_POINT = .5;

    /** The returns the study's 3.4 states, % of the cost a month: a surface mine and a large one exporting, a longwall alone, the pair, the plant alone. */
    static final double STUDY_SURFACE = 3.3, STUDY_LARGE = 3.6, STUDY_ALONE = .8, STUDY_PAIR = 2.4, STUDY_PLANT = 19;

    static void rows() {
        out.println("\n--- 3. the four rows at parity: the study's 3.3, and its four cases worked from its own figures, to its rounding ---");
        BuildingManager b = new BuildingManager();
        b.initializeTemplates();
        UtilitiesHandler founding = new UtilitiesHandler();
        double tariff = founding.getPricerPerWatt(), water = founding.getPricePerWaterUnit();
        java.util.function.ToDoubleFunction<Good> imp = Good::worldImportPrice, exp = Good::worldExportPrice;
        String[] cases = { "A", "B", "C", "D" };
        for (int r = 0; r < ROWS.length; r++) {
            BuildingsTemplate t = b.getTemplate(ROWS[r]);
            boolean plant = r == 3;
            double cost = t.getCashCost() + BuildingManager.MATERIALS_WORLD_PRICE * t.getConstructionMaterials();
            BuildingsTemplate.Site site = t.site();
            boolean siteOk = plant ? site == null && t.getWash() == STUDY_MAKES[r] && t.goodsMade().isEmpty() && t.goodsUsed().isEmpty()
                    : site != null && site.resource() == Resource.COAL && site.type() == STUDY_TYPES[r] && site.sites() == STUDY_SITES[r]
                    && site.dig() == (r == 2 ? UNDERGROUND_DIG : SURFACE_DIG) && t.makes(Good.THERMAL_COAL) == STUDY_MAKES[r]
                    && t.getWash() == 0;
            report("the " + t.getName() + " (" + t.getId() + ") is the study's: D$" + String.format("%,.0f", STUDY_COSTS[r]) + "k all in, "
                            + STUDY_POSTS[r] + " posts, " + String.format("%,d", STUDY_KW[r]) + " kW, " + String.format("%,.0f", STUDY_MAKES[r])
                            + (plant ? " t of raw coal washed a month, on no site" : " t of " + (r == 2 ? "raw" : "thermal") + " coal a month on "
                            + STUDY_SITES[r] + " " + STUDY_TYPES[r] + " site(s)") + "; Coal Mining's, MINING, dated 1900",
                    cost == STUDY_COSTS[r] && t.getTotalJobs() == STUDY_POSTS[r] && t.getElectricityConsumption() == STUDY_KW[r] && siteOk
                            && Sectors.COAL_MINING.equals(t.getSector()) && t.getCategory() == BuildingType.MINING && t.getSince() == 1900
                            && t.getStock() > 0,
                    String.format("cash %,.0f + 18 x %,d material", t.getCashCost(), t.getConstructionMaterials()));
            double sqFt = plant ? STUDY_PLANT_HECTARES * 10_000 / LandManager.SQ_M_PER_SQ_FT
                    : STUDY_SITES[r] * Resource.COAL.siteKm2() * LandManager.SQ_FT_PER_KM2;
            report("...its ground " + (plant ? "the study's 19 ha" : "the coal sites it takes, a coal site's 1 km2 each") + ", to the square foot",
                    Math.abs(t.getLandSqFt() - sqFt) <= .5, String.format("%,.0f sq ft", t.getLandSqFt()));
            double wages = 0;
            for (JobType j : JobType.values()) wages += t.getJobs(j) * PayTier.wageOf(j);
            double running = wages + t.getElectricityConsumption() * tariff + t.getWaterConsumption() * water;
            double diesel = t.uses(Good.DIESEL) * STUDY_DIESEL;
            report("its consumables are the study's running cost less its wages, power, water and diesel (at the study's D$0.60 a litre)",
                    Math.abs(t.getConsumables() - (STUDY_RUNS[r] - running - diesel)) < 1e-9,
                    String.format("%,.2f = %,.0f - %,.2f - %,.1f", t.getConsumables(), STUDY_RUNS[r], running, diesel));

            // The four cases: the model's, and the same four worked from the study's own figures.
            boolean all = true;
            StringBuilder got = new StringBuilder(), study = new StringBuilder();
            for (SpreadPlanner.Case c : SpreadPlanner.Case.values()) {
                java.util.function.ToDoubleFunction<Good> price = c.outputAtImport() ? imp : exp;
                double model, theirs;
                if (plant) {
                    // A plant buys and makes no good: what it adds to the flow at the case's price, its running cost and consumables.
                    Map<Good, Double> adds = flowIncrement(0, STUDY_RAW, 0, STUDY_MAKES[r]);
                    double value = 0;
                    for (Map.Entry<Good, Double> e : adds.entrySet()) value += e.getValue() * price.applyAsDouble(e.getKey());
                    model = value - running - SpreadPlanner.ownCosts(t, 1, 1, tariff);
                    theirs = (STUDY_COKING * price.applyAsDouble(Good.COKING_COAL) + (STUDY_THERMAL - STUDY_RAW) * price.applyAsDouble(Good.THERMAL_COAL))
                            - STUDY_RUNS[r];
                } else {
                    model = SpreadPlanner.caseEarns(t, c, imp, exp, running, 0, 1, tariff);
                    theirs = STUDY_MAKES[r] * price.applyAsDouble(Good.THERMAL_COAL) - STUDY_RUNS[r];
                }
                double pct = 100 * model / cost, want = 100 * theirs / STUDY_COSTS[r];
                all &= Math.abs(pct - want) <= STUDY_ROUNDING + 1e-9;
                got.append(String.format("%s %.3f%%  ", cases[c.ordinal()], pct));
                study.append(c.ordinal() == 0 ? "" : "/").append(String.format("%.3f", want));
            }
            report("...its four cases are the study's arithmetic to its rounding: " + study + "%", all, got.toString().trim());
        }
        /*
         * WHERE THE DIESEL IS (spec 2.2: "DIESEL where the study names it,
         * scaled by tonnes to the large sibling"): the study names 230,000 L
         * for the Surface Coal Mine's 100,000 t, so the Large Surface Mine
         * burns the same a tonne; it names none for the longwall, whose
         * running cost is its 29,000 kW and its consumables, nor the plant.
         */
        BuildingsTemplate small = b.getTemplate(CoalMining.SURFACE_MINE), large = b.getTemplate(CoalMining.LARGE_SURFACE_MINE);
        assertTrue("the surface mine burns the study's 230,000 L, the large one the same a tonne, the longwall and the plant none",
                small.uses(Good.DIESEL) == STUDY_DIESEL_LITRES
                        && large.uses(Good.DIESEL) / large.makes(Good.THERMAL_COAL) == small.uses(Good.DIESEL) / small.makes(Good.THERMAL_COAL)
                        && b.getTemplate(CoalMining.UNDERGROUND_MINE).goodsUsed().isEmpty());

        // The returns the study states (its 3.4), each from its own figures.
        double surface = 100 * (STUDY_MAKES[0] * Good.THERMAL_COAL.worldExportPrice() - STUDY_RUNS[0]) / STUDY_COSTS[0];
        double largeR = 100 * (STUDY_MAKES[1] * Good.THERMAL_COAL.worldExportPrice() - STUDY_RUNS[1]) / STUDY_COSTS[1];
        double alone = 100 * (STUDY_MAKES[2] * Good.THERMAL_COAL.worldExportPrice() - STUDY_RUNS[2]) / STUDY_COSTS[2];
        report("the study's 3.4: a surface mine returns 3.3% a month exporting, a large one 3.6% (its \"3.3-3.6%\")",
                Math.abs(surface - STUDY_SURFACE) <= STUDY_ROUNDING && Math.abs(largeR - STUDY_LARGE) <= STUDY_ROUNDING,
                String.format("%.3f%%, %.3f%%", surface, largeR));
        report("...a longwall alone 0.8%, its raw coal sold as thermal", Math.abs(alone - STUDY_ALONE) <= STUDY_ROUNDING,
                String.format("%.3f%%", alone));
        CoalMining.Flow two = CoalMining.flow(0, 2 * STUDY_MAKES[2], STUDY_MAKES[3]);
        double pairEarns = two.coking() * Good.COKING_COAL.worldExportPrice() + two.thermal() * Good.THERMAL_COAL.worldExportPrice()
                - 2 * STUDY_RUNS[2] - STUDY_RUNS[3];
        double pair = 100 * pairEarns / (2 * STUDY_COSTS[2] + STUDY_COSTS[3]);
        report("...with the plant the pair 2.4% - the plant fed whole, two longwalls' 500,000 t (one longwall's 250,000 reads 2.2%)",
                Math.abs(pair - STUDY_PAIR) <= STUDY_ROUNDING, String.format("%.3f%%", pair));
        double plantAlone = 100 * (STUDY_COKING * Good.COKING_COAL.worldExportPrice() + (STUDY_THERMAL - STUDY_RAW) * Good.THERMAL_COAL.worldExportPrice()
                - STUDY_RUNS[3]) / STUDY_COSTS[3];
        report("...and the plant alone 19%, because washing is where coking coal is made", Math.abs(plantAlone - STUDY_PLANT) <= STUDY_ROUNDING_POINT,
                String.format("%.2f%%", plantAlone));
    }

    /** What `wash` t of plants add to a flow of `surface` thermal and `raw` raw coal already washing `washing`: {good: tonnes}. */
    static Map<Good, Double> flowIncrement(double surface, double raw, double washing, double wash) {
        CoalMining.Flow before = CoalMining.flow(surface, raw, washing), after = CoalMining.flow(surface, raw, washing + wash);
        Map<Good, Double> out = new EnumMap<>(Good.class);
        for (Good g : new Good[] { Good.THERMAL_COAL, Good.COKING_COAL }) {
            double d = after.of(g) - before.of(g);
            if (d != 0) out.put(g, d);
        }
        return out;
    }

    /* ============================ 4. a longwall alone sells its raw coal as thermal ============================ */

    static void rawAsThermal() {
        out.println("\n--- 4. a longwall alone sells its raw coal as thermal, tonne for tonne; with a plant, coking coal ---");
        BuildingManager b = new BuildingManager();
        b.initializeTemplates();
        Sectors s = new Sectors(b, new Markets());
        CoalMining coal = s.coalMining();
        b.addStack(b.getTemplate(CoalMining.UNDERGROUND_MINE), 1, true);
        report("an Underground Coal Mine standing alone: the sector's nameplate 250,000 t of thermal coal and no coking (getCapacity())",
                coal.getCapacity(Good.THERMAL_COAL) == 250_000 && coal.getCapacity(Good.COKING_COAL) == 0
                        && coal.flow().raw() == 250_000 && coal.flow().washed() == 0,
                String.format("%,.0f t thermal", coal.getCapacity(Good.THERMAL_COAL)));
        b.addStack(b.getTemplate(CoalMining.PREPARATION_PLANT), 1, false);
        report("...a plant on site counts as supply the planner sees: the flow with it less the flow without, never below nothing",
                coal.getPipeline(Good.COKING_COAL) == 137_500 && coal.getPipeline(Good.THERMAL_COAL) == 0
                        && coal.getCapacity(Good.COKING_COAL) == 0,
                String.format("%,.0f t coking coming", coal.getPipeline(Good.COKING_COAL)));
        b.addStack(b.getTemplate(CoalMining.PREPARATION_PLANT), 1, true);
        report("...and standing, it washes the longwall's 250,000 t: 137,500 t of coking coal and 50,000 of thermal",
                coal.getCapacity(Good.COKING_COAL) == 137_500 && coal.getCapacity(Good.THERMAL_COAL) == 50_000,
                String.format("%,.0f coking, %,.0f thermal", coal.getCapacity(Good.COKING_COAL), coal.getCapacity(Good.THERMAL_COAL)));
        b.addStack(b.getTemplate(CoalMining.SURFACE_MINE), 1, true);
        assertTrue("...a surface mine's coal is thermal whatever stands beside it, and no plant washes it",
                coal.getCapacity(Good.THERMAL_COAL) == 150_000 && coal.getCapacity(Good.COKING_COAL) == 137_500 && coal.unwashed() == 0);
    }

    /* ============================ 5. the types and the fit ============================ */

    /** How far coal's thermal share of the fields may sit from Deposit.TYPE_SHARES (spec-materials 4: two points, WorldCheck 8's). */
    static final double TYPE_WITHIN = .02;

    static void typesAndFit() throws Exception {
        out.println("\n--- 5. coal's types at I1's shares and E3's odds, and each mine on the field that fits it ---");
        World w = World.of(Founding.DEFAULT_WORLD_SEED);
        long[] n = new long[2];
        for (int py = 0; py < World.poolsOnSide(Resource.COAL); py++) {
            for (int px = 0; px < World.poolsOnSide(Resource.COAL); px++) {
                for (Deposit d : w.poolFields(Resource.COAL, px, py)) n[Resource.COAL.typesOf().indexOf(d.type())]++;
            }
        }
        double thermal = (double) n[0] / (n[0] + n[1]);
        report("coal's fields on the default world are thermal at TYPE_SHARES' 85% and coking 15%, within two points (I1's draw)",
                Deposit.TYPE_SHARES[Deposit.FieldType.THERMAL.ordinal()] == .85 && Deposit.TYPE_SHARES[Deposit.FieldType.COKING.ordinal()] == .15
                        && Math.abs(thermal - .85) <= TYPE_WITHIN,
                String.format("%,d fields, %.2f%% thermal", n[0] + n[1], 100 * thermal));
        double flat = 1 - Geology.lean(Geology.COKING_ODDS, 1);
        assertTrue("on Earth coking leans on the ground (E3): odds three times as long in a fold belt, half on a shield, the rest at"
                        + " the odds that keep the Earth-wide share 15% (EarthTerrainCheck 14)",
                Geology.firstShare(Resource.COAL, Geology.Kind.FOLD_BELT) == 1 - Geology.lean(Geology.COKING_ODDS, 3)
                        && Geology.firstShare(Resource.COAL, Geology.Kind.SHIELD) == 1 - Geology.lean(Geology.COKING_ODDS, .5)
                        && Geology.firstShare(Resource.COAL, Geology.Kind.BASIN) == flat
                        && Geology.firstShare(Resource.COAL, Geology.Kind.FOLD_BELT) < flat);

        Game g = coalTown("coalminingcheck-fit", 3);
        CoalMining coal = g.getSectors().coalMining();
        BuildingManager b = g.getBuildingManager();
        LandManager land = g.getLandManager();
        BuildingsTemplate small = b.getTemplate(CoalMining.SURFACE_MINE), large = b.getTemplate(CoalMining.LARGE_SURFACE_MINE),
                longwall = b.getTemplate(CoalMining.UNDERGROUND_MINE), plant = b.getTemplate(CoalMining.PREPARATION_PLANT);
        int coking = land.getSites(Resource.COAL, Deposit.FieldType.COKING), therm = land.getSites(Resource.COAL, Deposit.FieldType.THERMAL);
        report("fixture: a town founded on a coking field, three thermal sites by fiat (the first type)", coking > 0 && therm == 3,
                String.format("%d coking site(s), %,.0f Mt; %d thermal", coking, land.getRemaining(Resource.COAL, Deposit.FieldType.COKING) / 1e6, therm));
        assertTrue("a surface mine fits a thermal site, and three of them the three; a large one, four sites of one field, does not",
                coal.fits(small) && g.hasDepositFor(small, 3) && !g.hasDepositFor(small, 4) && !coal.fits(large) && !g.hasDepositFor(large, 1));
        assertTrue("...a longwall fits only the coking sites, as many as there are, and the plant needs no site but coal in the ground",
                coal.fits(longwall) && g.hasDepositFor(longwall, coking) && !g.hasDepositFor(longwall, coking + 1) && coal.fits(plant)
                        && Game.siteOf(plant) == null);
        quietly(() -> land.restoreSites(Resource.COAL, coking + 4, land.getOwnedAmount(Resource.COAL) + Resource.COAL.amountPerSite()));
        assertTrue("on four thermal sites of one piece the large mine fits, one and not two", coal.fits(large) && g.hasDepositFor(large, 1)
                && !g.hasDepositFor(large, 2) && land.getSites(Resource.COAL, Deposit.FieldType.THERMAL) == 4);
    }

    /**
     * A town to dig coal in: MaterialsCheck's (the Wealthy preset's cash and
     * ground, homes, shops, power and water), its land founded instead on the
     * coking coal field nearest the default founding site on dry ground - so
     * it holds a coking field whole (CityLand.found(): the field its centre is
     * on goes with that ground) - and `thermalSites` thermal sites by fiat,
     * ground listed by fiat being the first type.
     */
    static Game coalTown(String label, int thermalSites) {
        Game g = new Game(GameFiles.scratch(label));
        quietly(() -> {
            g.newGame();
            World w = World.of(g.getCityLand().seed());
            Deposit field = nearestCoking(w);
            LandManager land = g.getLandManager();
            land.install(CityLand.found(w, field.x(), field.y(), LandManager.km2(land.getOwnedSqFt())), new double[CityLand.KINDS],
                    w.totals(), w.seaTheta());
            land.restoreOwnedSqFt(land.getLandDrySqFt());
            g.setCashForTest(Founding.WEALTHY_CASH);
            // ...the ground restated to hold the town (LandConversion.restate()), then the thermal by fiat on it.
            land.setOwnedSqFt(land.getOwnedSqFt() + 400_000_000L);
            int held = land.getSites(Resource.COAL);
            land.restoreSites(Resource.COAL, held + thermalSites,
                    land.getOwnedAmount(Resource.COAL) + thermalSites * Resource.COAL.amountPerSite());
            BuildingManager b = g.getBuildingManager();
            g.buildStack(b.getTemplateByName("House"), 400, true);
            g.buildStack(b.getTemplateByName("Small Grocery Store"), 4, true);
            g.buildStack(b.getTemplateByName("Coal Power Plant"), 1, true);
            g.buildStack(b.getTemplateByName("Water Treatment Plant"), 1, true);
            g.buildStack(b.getTemplateByName("Paved Road"), 10, true);
        });
        return g;
    }

    /** The coking coal field nearest a world's founding site whose centre plot is dry ground, cell ring by cell ring. */
    static Deposit nearestCoking(World w) {
        long fx = w.foundingX(), fy = w.foundingY();
        int cx = (int) (fx / World.CELL), cy = (int) (fy / World.CELL);
        for (int ring = 0; ring < World.CELLS; ring++) {
            Deposit best = null;
            double far = Double.MAX_VALUE;
            for (int y = cy - ring; y <= cy + ring; y++) {
                for (int x = cx - ring; x <= cx + ring; x++) {
                    if (Math.max(Math.abs(x - cx), Math.abs(y - cy)) != ring) continue;
                    if (x < 0 || y < 0 || x >= World.CELLS || y >= World.CELLS) continue;
                    for (Deposit d : w.fieldsInCell(y * World.CELLS + x, Resource.COAL)) {
                        if (d.type() != Deposit.FieldType.COKING || !World.dry(w.terrainAt(d.x(), d.y()))) continue;
                        double r = Math.hypot(d.x() - fx, d.y() - fy);
                        if (r < far) { far = r; best = d; }
                    }
                }
            }
            if (best != null) return best;
        }
        throw new IllegalStateException("no coking field on dry ground on world " + w);
    }

    /* ============================ 6. the dig by type ============================ */

    static void dig() {
        out.println("\n--- 6. the dig by type: the month's coal is the lift, each type's ground gives it up at its dig, W exact ---");
        Game g = coalTown("coalminingcheck-dig", 3);
        CoalMining coal = g.getSectors().coalMining();
        BuildingManager b = g.getBuildingManager();
        LandManager land = g.getLandManager();
        final Resource r = Resource.COAL;
        final Deposit.FieldType th = Deposit.FieldType.THERMAL, ck = Deposit.FieldType.COKING;
        quietly(() -> {
            b.addStack(b.getTemplate(CoalMining.SURFACE_MINE), 1, true);
            b.addStack(b.getTemplate(CoalMining.UNDERGROUND_MINE), 1, true);
        });
        double rate = coal.getOperatingRate();
        CoalMining.Flow lift = coal.lift();
        report("fixture: a surface mine and a longwall standing, no plant; the month's lift each one's nameplate at the sector's rate",
                rate > 0 && lift.surface() == 100_000 * rate && lift.raw() == 250_000 * rate && lift.washed() == 0,
                String.format("rate %.4f: %,.0f t thermal dug, %,.0f t raw", rate, lift.surface(), lift.raw()));
        assertTrue("...what the market is priced on and the month makes: the lift's flow, the raw sold as thermal",
                coal.getPlannedOutput(Good.THERMAL_COAL) + coal.getExportBoundOutput(Good.THERMAL_COAL) <= lift.thermal() + 1e-6
                        && lift.thermal() == lift.surface() + lift.raw() && lift.coking() == 0);
        double[] before = land.getExtractedByType(r);
        double eBefore = land.getExtracted(r);
        quietly(() -> coal.endOfMonth(g));
        double[] after = land.getExtractedByType(r);
        int t = r.typesOf().indexOf(th), c = r.typesOf().indexOf(ck);
        report("at the month's end each type's ground gives up its coal at its dig: the thermal 1/.95 t a tonne, the coking 1/.80",
                Math.abs(after[t] - before[t] - lift.surface() * SURFACE_DIG) < 1e-6 && Math.abs(after[c] - before[c] - lift.raw() * UNDERGROUND_DIG) < 1e-6,
                String.format("thermal %,.0f t of seam, coking %,.0f", after[t] - before[t], after[c] - before[c]));
        assertTrue("...E rising by the seam taken out, and W by type to the tonne",
                Math.abs(land.getExtracted(r) - eBefore - lift.surface() * SURFACE_DIG - lift.raw() * UNDERGROUND_DIG) < 1e-6
                        && LandCheck.wByType(land));

        // A type running short holds its mines to what is left.
        double left = 30_000;
        land.extract(r, th, land.getRemaining(r, th) - left);
        CoalMining.Flow short_ = coal.lift();
        report("with 30,000 t of thermal seam left the surface mine digs what it holds, 30,000 x .95 t of coal; the longwall its whole ask",
                Math.abs(short_.surface() - left / SURFACE_DIG) < 1e-6 && short_.raw() == 250_000 * rate,
                String.format("%,.0f t thermal", short_.surface()));
        land.extract(r, th, land.getRemaining(r, th));
        double[] spare = coal.retirementDemandAndCapacity(g);
        assertTrue("...and once the thermal is out the surface mine digs nothing and is spare, and only it may be sold",
                coal.lift().surface() == 0 && spare != null && spare[0] == 0 && spare[1] == 100_000
                        && coal.mayRetire(b.getTemplate(CoalMining.SURFACE_MINE)) && !coal.mayRetire(b.getTemplate(CoalMining.UNDERGROUND_MINE))
                        && LandCheck.wByType(land));
    }

    /* ============================ 7. the plan ============================ */

    static void plan() {
        out.println("\n--- 7. the plan: a preparation plant beside the coking mines first, then the best mine that fits ---");
        Game g = coalTown("coalminingcheck-plan", 3);
        CoalMining coal = g.getSectors().coalMining();
        BuildingManager b = g.getBuildingManager();
        final BuildingManager first = b;
        BuildingsTemplate plant = b.getTemplate(CoalMining.PREPARATION_PLANT);
        quietly(() -> first.addStack(first.getTemplate(CoalMining.UNDERGROUND_MINE), 1, true));
        MaterialsCheck.handWorkers(g);
        BusinessInvestment plans = g.getBusinessInvestment();
        Map<Good, Double> adds = coal.madeBy(plant);
        report("fixture: a longwall standing, its 250,000 t of raw coal unwashed: a plant would add 137,500 t of coking coal and take"
                        + " 200,000 t off the thermal",
                coal.unwashed() == 250_000 && adds.get(Good.COKING_COAL) == 137_500 && adds.get(Good.THERMAL_COAL) == -200_000,
                adds.toString());
        double rate = BusinessInvestment.operatingRateOf(coal.getOperatingRate());
        double expect = plans.estimatedMakerProfit(coal, plant, Map.of(Good.COKING_COAL, 137_500.0))
                - coal.lostValue(Good.THERMAL_COAL, 200_000, plans) * rate
                - SpreadPlanner.ownCosts(plant, rate, coal.exchangeFactor(), g.getEconomyManager().getPricePerWatt()) - coal.fleetRunning(plant, rate);
        double earns = coal.estimatedMonthlyProfit(plant, plans);
        report("...its estimate the flow's increment: the coking coal at the makers' rule, less the thermal at what it was selling for,"
                        + " less its costs, to the bit",
                earns == expect && earns > 0, String.format("D$%,.1fk a month on D$%,.0fk", earns, plans.getCostOf(plant, 1)));
        BusinessInvestment.Decision d = coal.plan(plans, g);
        report("...and the plan orders the plant, before any mine", d.build && d.template == plant && d.reason.contains("unwashed"), d.reason);

        quietly(() -> first.addStack(plant, 1, false));
        BusinessInvestment.Decision busy = coal.plan(plans, g);
        assertTrue("with the plant on site nothing is unwashed and one order is in flight: the sector waits",
                coal.unwashed() == 0 && !busy.build && busy.reason.contains("already building"));
        // A second town, its longwall and plant both standing and nothing on site.
        Game g2 = coalTown("coalminingcheck-plan2", 3);
        CoalMining coal2 = g2.getSectors().coalMining();
        BuildingManager b2 = g2.getBuildingManager();
        quietly(() -> {
            b2.addStack(b2.getTemplate(CoalMining.UNDERGROUND_MINE), 1, true);
            b2.addStack(b2.getTemplate(CoalMining.PREPARATION_PLANT), 1, true);
        });
        MaterialsCheck.handWorkers(g2);
        plans = g2.getBusinessInvestment();
        coal = coal2;
        b = b2;
        g = g2;
        BusinessInvestment.Decision mine = coal.plan(plans, g);
        BuildingsTemplate expected = null;
        double best = 0;
        for (BuildingsTemplate t : b.getTemplatesBySector(Sectors.COAL_MINING)) {
            if (!(t.makes(Good.THERMAL_COAL) > 0) || !coal.fits(t) || !coal.staffing(t).passes()) continue;
            double e = coal.estimatedMonthlyProfit(t, plans), cost = plans.getCostOf(t, 1) + coal.fleetCapital(t);
            if (!coal.planner().passesMoney(coal, plans, g, e, cost)) continue;
            if (e / cost > best) { best = e / cost; expected = t; }
        }
        report("with the plant standing and room in its wash, the mines' rule: the best earnings on the whole cost of those that fit",
                coal.unwashed() == 0 && (expected == null ? !mine.build : mine.build && mine.template == expected),
                mine.reason + (expected == null ? "" : String.format("; %s at %.3f%% a month", expected.getName(), 100 * best)));
        Map<Good, Double> second = coal.madeBy(b.getTemplate(CoalMining.UNDERGROUND_MINE));
        assertTrue("...a second longwall's raw coal is washed in the plant's spare 250,000 t: it adds coking coal, not thermal",
                second.get(Good.COKING_COAL) == 137_500 && second.get(Good.THERMAL_COAL) == 50_000);
    }

    /* ============================ 8. a played month ============================ */

    static void playedMonth() {
        out.println("\n--- 8. a played month: the coal is the lift's, the ground gives it up, the audit closes ---");
        Game g = coalTown("coalminingcheck-month", 3);
        CoalMining coal = g.getSectors().coalMining();
        BuildingManager b = g.getBuildingManager();
        LandManager land = g.getLandManager();
        quietly(() -> {
            b.addStack(b.getTemplate(CoalMining.SURFACE_MINE), 1, true);
            b.addStack(b.getTemplate(CoalMining.UNDERGROUND_MINE), 1, true);
            b.addStack(b.getTemplate(CoalMining.PREPARATION_PLANT), 1, true);
            g.simulateMonths(1);
        });
        double eBefore = land.getExtracted(Resource.COAL);
        quietly(() -> g.simulateMonths(1));
        Sector.Output th = coal.outputRow(Good.THERMAL_COAL), ck = coal.outputRow(Good.COKING_COAL);
        double dug = land.getExtracted(Resource.COAL) - eBefore;
        CoalMining.Flow lift = coal.lift();
        report("fixture: a surface mine, a longwall and a plant standing, a month on: the sector made both coals",
                th != null && ck != null && ck.capacity == 137_500 && th.capacity == 150_000 && th.planned + th.exportBound > 0
                        && ck.planned + ck.exportBound > 0,
                th == null || ck == null ? "no rows" : String.format("thermal %,.0f made, coking %,.0f; %,.0f t of seam dug",
                        th.planned + th.exportBound, ck.planned + ck.exportBound, dug));
        report("...each coal's month - made for home, from the line abroad, and idled - the lift's flow at the month's rate",
                Math.abs(th.planned + th.exportBound + th.idled - lift.thermal()) < 1e-6 && Math.abs(ck.planned + ck.exportBound + ck.idled - lift.coking()) < 1e-6,
                String.format("rate %.4f: %,.0f t raw washed, %,.0f t thermal dug", coal.getOperatingRate(), lift.washed(), lift.surface()));
        report("...and the ground gave up the seam for it, the thermal at 1/.95 a tonne and the raw at 1/.80; W by type exact",
                Math.abs(dug - lift.surface() * SURFACE_DIG - lift.raw() * UNDERGROUND_DIG) < 1e-6 && LandCheck.wByType(land),
                String.format("%,.0f t of seam", dug));
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        report("the month's money audit closes", audit != null && audit.relative() < 1e-4,
                audit == null ? "none" : String.format("residual %.3g of what moved", audit.relative()));
    }
}

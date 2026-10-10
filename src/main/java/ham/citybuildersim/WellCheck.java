package ham.citybuildersim;

import ham.citybuildersim.sectors.Oil;
import ham.citybuildersim.sectors.Oil.Vintage;
import ham.citybuildersim.sectors.Oil.WellKind;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * The oil wells' lives (0.7.84, batch O7; runs/spec-oil.md 2.6 and 4's
 * WellCheck, the land half) - and since 0.7.91 the sea's (batch O10; spec-oil
 * 2.7, 2.11): the platforms, their wells, the shuttle tankers and the pipes;
 * and since 0.7.93 the two pools they lift (batch O10b).
 *
 * WHAT THIS HAS TO PROVE:
 *
 *   1. THE PROFILES, TO THE BIT: a land well lifts LAND_KEEPS_A_YEAR (nine
 *      tenths) of its nameplate to the power of its years - 10% less a year -
 *      and a platform well all of it through PLATFORM_PLATEAU_MONTHS, then
 *      PLATFORM_KEEPS_A_YEAR to the power of its years past them.
 *
 *   2. RETIRED AT 263 MONTHS ON LAND AND 348 ON A PLATFORM: the first age at
 *      which a well lifts under ten barrels a day, 41.5 t of its 415.
 *
 *   3. A TOWN'S LIFT IS ITS VINTAGES': each batch of wells, from the month it
 *      opened, at its profile, summed oldest first - the nameplate the month
 *      is struck on, to the bit; a new well lifts its whole nameplate the
 *      month it opens; a read moves nothing; and the world's oil is
 *      conserved to the tonne.
 *
 *   4. A WORN-OUT WELL IS RETIRED, OLDEST FIRST, AND ITS SITE REFILLED: at 263
 *      months, by the month's retirements, and the sector's one-well-a-month
 *      rule drills its site again.
 *
 *   5. DRY AND SEA SITES ARE KEPT APART: a land well stands only on a dry site
 *      - the order, the card and the investors all refuse a sea one - and
 *      wells already standing past the dry sites, as a save from before 0.7.84
 *      carries them on the sea's, are kept, lifting the ground pool.
 *
 *   6. THE VINTAGES CROSS A SAVE, to the bit, as the Oil sector's extras; and a
 *      save from before them reads every well new, opened the first month the
 *      city plays.
 *
 *   7. A WELL HAS ONE POST, a diploma's (the research's Q13: 0.2-0.4 workers a
 *      well [W12]; three until 0.7.84).
 *
 *   8. A PLATFORM STANDS ON A SHALLOW SEA FIELD, its wells in its slots: a
 *      field in the sea PLATFORM_MAX_DEPTH_M deep or less; slots at most 12
 *      and at most the field's sea sites; a platform's well only in a free
 *      slot, a land well only on a dry site - the two kept apart; the
 *      platform profile's lift, to the bit; W conserved; a platform well
 *      retired at 348 months and its slot refilled.
 *
 *   9. THE SHUTTLE TANKERS: platform crude sold at home pays CRUDE's band
 *      freight a tonne, the offshore pool's share of the month's lift, to the
 *      bit, booked as the Oil sector's imported "Shuttle tankers"; crude
 *      shipped abroad pays none; the audit closes.
 *
 *  10. THE PIPELINE: the rule passes on a big field and fails on a small one,
 *      and on land; a field's whole pipe stands for its crude and the shuttle
 *      tankers carry none of it.
 *
 *  11. THE PLATFORMS AND PIPELINES CROSS A SAVE, to the bit; a save from
 *      before them reads the platforms standing anew on their field.
 *
 *  12. THE PLANNER WEIGHS THE THREE KINDS AT SEA after the land well: a
 *      platform's jacket as a package with its wells, by the interest test; a
 *      platform well into a free slot; and once the oil is worked out the
 *      empty jackets and the pipes are decommissioned.
 *
 *  13. THE CITY'S CRUDE IS TWO POOLS (0.7.93; Jerus: "two pools, offshore
 *      oil and ground oil"): the ground pool its dry fields' and the oil by
 *      fiat, the offshore pool its sea fields'; the land wells lift the
 *      ground's only and the platform wells the sea's only, each its own
 *      pool's E, every month; one pool worked out, its wells lift nothing
 *      and are the spare while the other's lift on, its orders refused for
 *      their deposit and the other's let through; the Oil page shows both;
 *      the two E's cross a save, and a save from before them charges its one
 *      pool to the ground, floored at the ground's tonnes, none to the sea.
 *
 * Every fixture CAUSES its condition: the town is handed its oil by fiat
 * (LandManager.restoreSites(), as MiningCheck hands a city its ore), its
 * wells' ages by their vintages (Oil.setVintagesForTest()), and section 5's
 * sea field is bought.
 */
public class WellCheck {

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

    /** Each fixture town's save folder (section 6). */
    static final java.util.Map<Game, GameFiles> FILES = new java.util.IdentityHashMap<>();

    /** A well's nameplate a month, the template's: 415 t, a hundred barrels a day. */
    static final double WELL_TONNES = 415;

    /** The Strategic Reserve's fill a month in the sea sections (OilCheck 16's order): more than the wells lift, so the city buys every tonne of theirs at home. */
    static final double FILL = 50_000;

    /** The oil a town is handed: far more than its wells lift in the months a section runs, so the ground never limits them. */
    static final double PLENTY = 50_000_000;

    /**
     * OilCheck's town: houses, shops, bakeries to work in, power, water, roads
     * and builders, standing, on ground a quarter more than they take and
     * `spare` square feet more, on the world `seed` (0 the default).
     */
    static Game town(String label, int houses, double spare, long seed) {
        GameFiles files = GameFiles.scratch(label);
        Game g = new Game(files);
        FILES.put(g, files);
        quietly(() -> {
            if (seed == 0) g.newGame(); else g.newGame(Founding.defaults().withWorldSeed(seed));
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

    /** The lift the vintages say, as the spec writes it: each vintage's wells x a well's nameplate x its profile at its age, oldest first. */
    static double liftOf(List<Vintage> vintages, double each, int month) {
        double lift = 0;
        for (Vintage v : vintages) lift += v.count() * each * Oil.profile(v.kind(), month - v.month());
        return lift;
    }

    /** Whether the world's oil is the town's unowned + remaining + extracted, to the tonne. */
    static boolean conserved(LandManager land, double world) {
        return Math.abs(land.getUnowned(Resource.OIL) + land.getRemaining(Resource.OIL) + land.getExtracted(Resource.OIL) - world) <= 1;
    }

    public static void main(String[] args) throws Exception {
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        theProfiles();
        theirLife();
        Game lifted = aTownsLift();
        Game worn = wornOutWellsRetire();
        drySitesAndSea();
        acrossASave(worn);
        onePost();
        Game sea = aPlatform();
        if (sea != null) {
            theShuttleTankers(sea);
            thePipeline(sea);
            seaAcrossASave(sea);
        }
        thePlanner();
        theTwoPools();

        for (GameFiles f : FILES.values()) LongPlaytest.cleanUp(f.getDirectory().getParent());
        out.println();
        if (fails == 0) {
            out.println("All checks passed.");
        } else {
            out.println(fails + " FAILED");
            System.exit(fails);
        }
    }

    /* ============================ 1. THE PROFILES ============================ */

    static void theProfiles() {
        out.println("--- 1. the profiles, to the bit ---");
        assertTrue("a land well loses 10% of its lift a year, a platform well 8.5% after a plateau of three years (the research's"
                        + " 3.1 [W6][W16], est.)",
                Oil.LAND_KEEPS_A_YEAR == 1 - .10 && Oil.PLATFORM_KEEPS_A_YEAR == 1 - .085 && Oil.PLATFORM_PLATEAU_MONTHS == 36);
        boolean land = true, platform = true, yearly = true;
        for (int age = 0; age <= 600; age++) {
            land &= Oil.profile(WellKind.LAND, age) == StrictMath.pow(Oil.LAND_KEEPS_A_YEAR, age / 12.0);
            platform &= Oil.profile(WellKind.PLATFORM, age) == (age < Oil.PLATFORM_PLATEAU_MONTHS ? 1
                    : StrictMath.pow(Oil.PLATFORM_KEEPS_A_YEAR, (age - Oil.PLATFORM_PLATEAU_MONTHS) / 12.0));
            yearly &= Math.abs(Oil.profile(WellKind.LAND, age + 12) / Oil.profile(WellKind.LAND, age) - Oil.LAND_KEEPS_A_YEAR) < 1e-12;
        }
        assertTrue("a land well's profile is LAND_KEEPS_A_YEAR to the power of its age in years, every month of fifty years, to"
                + " the bit", land);
        assertTrue("...all of its nameplate the month it opens, nine tenths a year on, and nine tenths of the year before every year",
                Oil.profile(WellKind.LAND, 0) == 1 && Oil.profile(WellKind.LAND, 12) == Oil.LAND_KEEPS_A_YEAR && yearly);
        assertTrue("a platform well's is 1 through PLATFORM_PLATEAU_MONTHS, then PLATFORM_KEEPS_A_YEAR to the power of its years"
                + " past them, to the bit", platform && Oil.profile(WellKind.PLATFORM, 35) == 1
                && Oil.profile(WellKind.PLATFORM, 36) == 1 && Oil.profile(WellKind.PLATFORM, 48) == Oil.PLATFORM_KEEPS_A_YEAR);
        assertTrue("...and an age under nothing reads as nothing", Oil.profile(WellKind.LAND, -5) == 1
                && Oil.profile(WellKind.PLATFORM, -5) == 1);
    }

    /* ============================ 2. THEIR LIFE ============================ */

    static void theirLife() {
        out.println("\n--- 2. a well is worn out under ten barrels a day: at 263 months on land, 348 on a platform ---");
        double floor = WELL_TONNES * Oil.WORN_OUT_SHARE;
        assertTrue("worn out is WORN_OUT_BARRELS_A_DAY of a well's NAMEPLATE_BARRELS_A_DAY, ten of a hundred: 41.5 t of 415",
                Oil.WORN_OUT_SHARE == Oil.WORN_OUT_BARRELS_A_DAY / Oil.NAMEPLATE_BARRELS_A_DAY && Oil.WORN_OUT_BARRELS_A_DAY == 10
                        && Oil.NAMEPLATE_BARRELS_A_DAY == 100 && floor == 41.5);
        int land = Oil.lifeMonths(WellKind.LAND), platform = Oil.lifeMonths(WellKind.PLATFORM);
        report("a land well's life is 263 months: at 262 it lifts ten barrels a day or more, at 263 less",
                land == 263 && WELL_TONNES * Oil.profile(WellKind.LAND, 262) >= floor
                        && WELL_TONNES * Oil.profile(WellKind.LAND, 263) < floor,
                String.format("%,.2f t at 262, %,.2f t at 263", WELL_TONNES * Oil.profile(WellKind.LAND, 262),
                        WELL_TONNES * Oil.profile(WellKind.LAND, 263)));
        report("...a platform well's 348: its plateau and 26 years past it",
                platform == 348 && WELL_TONNES * Oil.profile(WellKind.PLATFORM, 347) >= floor
                        && WELL_TONNES * Oil.profile(WellKind.PLATFORM, 348) < floor,
                String.format("%,.2f t at 347, %,.2f t at 348", WELL_TONNES * Oil.profile(WellKind.PLATFORM, 347),
                        WELL_TONNES * Oil.profile(WellKind.PLATFORM, 348)));
    }

    /* ============================ 3. A TOWN'S LIFT ============================ */

    static Game aTownsLift() {
        out.println("\n--- 3. a town's lift is its vintages', to the bit ---");
        Game g = town("wellcheck-lift", 300, 8 * 87_120, 0);
        BuildingsTemplate well = template(g, "Oil Well");
        LandManager land = g.getLandManager();
        Oil wells = g.getSectors().oil();
        int[] stood = new int[2];
        quietly(() -> {
            // The wells' own orders held: this section's wells are the ones it stands.
            g.getBusinessInvestment().holdSector(Sectors.OIL);
            land.restoreSites(Resource.OIL, 6, PLENTY);
            stood[0] = g.getMonth();
            g.buildStack(well, 2, true);
        });
        double world = land.getWorldTotal(Resource.OIL);
        report("fixture: the town is handed six dry oil sites and plenty of oil, and stands two wells",
                land.getSites(Resource.OIL, true) == 6 && wells.landWellsStanding() == 2, String.format("%,.0f t", land.getOilReserveTonnes()));
        List<Vintage> unstruck = wells.vintagesNow();
        assertTrue("...read before a month is played: two wells new this month, the vintages struck holding none (a read moves"
                        + " nothing)",
                unstruck.equals(List.of(new Vintage(stood[0], 2, WellKind.LAND))) && wells.vintages().isEmpty()
                        && wells.getCapacity(Good.CRUDE) == 2 * WELL_TONNES);
        boolean struck = true, kept = true;
        for (int m = 0; m < 12; m++) {
            quietly(() -> g.simulateMonths(1));
            Sector.Output o = wells.output(Good.CRUDE);
            struck &= o.capacity == liftOf(wells.vintages(), WELL_TONNES, g.getMonth());
            kept &= conserved(land, world);
        }
        int firstOpened = stood[0] + 1;
        report("wells stood between two months open the first month played, and are struck so once a month",
                wells.vintages().equals(List.of(new Vintage(firstOpened, 2, WellKind.LAND))), wells.vintages().toString());
        quietly(() -> {
            stood[1] = g.getMonth();
            g.buildStack(well, 2, true);
        });
        double before = liftOf(wells.vintages(), WELL_TONNES, g.getMonth());
        report("a new well lifts all of its nameplate the month it opens: two more add 830 t to the lift, to the bit",
                wells.getCapacity(Good.CRUDE) == before + 2 * WELL_TONNES, String.format("%,.4f t on %,.4f", wells.getCapacity(Good.CRUDE), before));
        for (int m = 0; m < 6; m++) {
            quietly(() -> g.simulateMonths(1));
            Sector.Output o = wells.output(Good.CRUDE);
            struck &= o.capacity == liftOf(wells.vintages(), WELL_TONNES, g.getMonth());
            kept &= conserved(land, world);
        }
        List<Vintage> two = List.of(new Vintage(firstOpened, 2, WellKind.LAND), new Vintage(stood[1] + 1, 2, WellKind.LAND));
        report("two batches, two vintages, oldest first", wells.vintages().equals(two), wells.vintages().toString());
        double expected = liftOf(two, WELL_TONNES, g.getMonth());
        report("the nameplate is each vintage's wells x 415 t x its profile at its age, summed oldest first, to the bit",
                wells.getCapacity(Good.CRUDE) == expected && expected < 4 * WELL_TONNES,
                String.format("%,.6f t at ages %d and %d (new: %,.0f)", expected, g.getMonth() - firstOpened,
                        g.getMonth() - stood[1] - 1, wells.newWellsCapacity()));
        assertTrue("...the nameplate each month was struck on, every month of the eighteen", struck);
        double lifted = wells.output(Good.CRUDE).produced;
        report("...and the wells lift it at their operating rate, the ground permitting",
                lifted > 0 && Math.abs(lifted - wells.output(Good.CRUDE).capacity * wells.getOperatingRate()) <= 1e-9 * lifted,
                String.format("%,.3f t at %.4f", lifted, wells.getOperatingRate()));
        double first = wells.getCapacity(Good.CRUDE);
        assertTrue("the lift is cached on the month: read twice, the same, and the vintages unmoved",
                wells.getCapacity(Good.CRUDE) == first && wells.vintages().equals(two));
        assertTrue("...a well's part of the shrinking rules' measure is the average well's lift",
                Math.abs(wells.unitsOf(well) * 4 - first) <= 1e-9 * first);
        assertTrue("the world's oil is unowned + the town's remaining + its extracted, to the tonne, every month", kept);
        return g;
    }

    /* ============================ 4. WORN OUT, RETIRED, REFILLED ============================ */

    static Game wornOutWellsRetire() {
        out.println("\n--- 4. a worn-out well is retired, oldest first, and its site refilled ---");
        // Three hundred houses more than its jobs, and a year for their people to come, so the town has the hands to staff
        // a new well.
        Game g = town("wellcheck-worn", 600, 12 * 87_120 + 300 * 10_000, 0);
        BuildingsTemplate well = template(g, "Oil Well");
        LandManager land = g.getLandManager();
        Oil wells = g.getSectors().oil();
        int[] at = new int[1];
        quietly(() -> {
            g.buildStack(template(g, "House"), 300, true);
            land.restoreSites(Resource.OIL, 4, PLENTY);
            g.buildStack(well, 4, true);
            g.simulateMonths(12);
            at[0] = g.getMonth();
            // Two wells a month short of their life, two a hundred months old.
            wells.setVintagesForTest(List.of(new Vintage(at[0] - (Oil.lifeMonths(WellKind.LAND) - 1), 2, WellKind.LAND),
                    new Vintage(at[0] - 100, 2, WellKind.LAND)));
        });
        int month = at[0];
        report("fixture: four wells on four dry sites, two of them 262 months old: none worn out yet - and hands to staff another",
                wells.landWellsStanding() == 4 && wells.wornOut() == 0 && land.getSites(Resource.OIL, true) == 4
                        && wells.staffing(well).passes(),
                wells.vintages().toString());
        double world = land.getWorldTotal(Resource.OIL);
        int retiredBefore = g.getWellsWornOut();
        quietly(() -> g.simulateMonths(1));
        List<Vintage> after = wells.vintages();
        boolean younger = !after.isEmpty() && after.get(0).equals(new Vintage(month - 100, 2, WellKind.LAND));
        for (int i = 1; i < after.size(); i++) younger &= after.get(i).month() == month + 1;
        report("a month on, the two at 263 months are retired by the month's retirements, oldest first: the two younger stand",
                g.getWellsWornOut() - retiredBefore == 2 && younger
                        && wells.landWellsStanding() == 2 + (after.size() - 1 == 0 ? 0 : after.get(after.size() - 1).count()),
                String.format("%d retired; %s", g.getWellsWornOut() - retiredBefore, after));
        int[] committed = new int[8];
        boolean oneAMonth = true, kept = true;
        committed[0] = g.wellsCommitted();
        for (int m = 1; m < committed.length; m++) {
            quietly(() -> g.simulateMonths(1));
            committed[m] = g.wellsCommitted();
            oneAMonth &= committed[m] - committed[m - 1] <= 1 && committed[m] <= land.getSites(Resource.OIL, true);
            kept &= conserved(land, world);
        }
        report("the sector's one-well-a-month rule drills the freed sites again, one at a time",
                wells.landWellsStanding() == 4 && oneAMonth, java.util.Arrays.toString(committed) + " wells committed by month");
        List<Vintage> now = wells.vintages();
        boolean refilled = now.size() >= 2 && now.get(0).equals(new Vintage(month - 100, 2, WellKind.LAND));
        int fresh = 0;
        for (int i = 1; i < now.size(); i++) {
            refilled &= now.get(i).month() > month;
            fresh += now.get(i).count();
        }
        report("...each new well a vintage of its own month, lifting its whole nameplate when it opened",
                refilled && fresh == 2, now.toString());
        assertTrue("...and the world's oil is conserved, to the tonne", kept);
        return g;
    }

    /* ============================ 5. DRY AND SEA SITES ============================ */

    static void drySitesAndSea() {
        out.println("\n--- 5. dry and sea sites are kept apart: a land well stands only on a dry one ---");
        World w = World.of(OilCheck.SEA_OIL_SEED);
        Deposit sea = OilCheck.nearestSeaOil(w);
        report("fixture: the world's nearest oil field to its site is in the sea", sea != null,
                sea == null ? "none" : String.format("%d sites, %,.0f t, %.0f m deep", sea.sites(), sea.amount(), w.depthAt(sea.x(), sea.y())));
        if (sea == null) return;
        Game g = town("wellcheck-sea", 300, 4 * 87_120, OilCheck.SEA_OIL_SEED);
        BuildingsTemplate well = template(g, "Oil Well"), mine = template(g, "Iron Mine");
        LandManager land = g.getLandManager();
        Oil wells = g.getSectors().oil();
        int pushes = 0;
        while (!g.getCityLand().ownsPlot(sea.x(), sea.y()) && pushes < 200) {
            LandParcel p = MiningCheck.nearestOffer(land.getMarket(), sea.x(), sea.y());
            if (p == null) break;
            g.setCashForTest(g.getCash() + p.localPrice(g.getForeignAccounts().getRate()));
            if (!quietlyGet(() -> g.buyLandParcel(p.getId()))) break;
            pushes++;
        }
        int all = land.getOilSites(), seaSites = land.getSites(Resource.OIL, false), dry = land.getSites(Resource.OIL, true);
        report("fixture: the town buys the ground over it, and all its oil sites are the sea's",
                g.getCityLand().ownsPlot(sea.x(), sea.y()) && all > 0 && seaSites == all && dry == 0,
                String.format("%d purchase(s): %d site(s), %d at sea", pushes, all, seaSites));
        assertTrue("a land well's sites are the dry ones (Game.sitesFor()); an Iron Mine's all its iron's",
                g.sitesFor(well) == dry && g.sitesFor(mine) == land.getSites(Resource.IRON) && g.sitesFor(template(g, "House")) == 0);
        Game.BuildResult refused = quietlyGet(() -> g.buildStack(well, 1, false));
        BuildCard.Verdict v = BuildCard.verdict(g, well, 1);
        report("an Oil Well on a sea site is refused for its deposit, on the card as at the order",
                refused == Game.BuildResult.NO_DEPOSIT && v.kind() == BuildCard.VerdictKind.NO_DEPOSIT, refused + ", " + v.kind());
        // ...the land's own decision (0.7.91): the sea's field is a platform's, weighed after it (section 12).
        BusinessInvestment.Decision d = wells.planOnLand(g.getBusinessInvestment(), g);
        report("...and the investors drill no land well: the word names the deposit and the dry ground", !d.build
                && d.reason.contains("deposit") && d.reason.contains("dry ground"), "\"" + d.reason + "\"");

        // Two dry sites handed to the centre with oil of their own by fiat, the ground pool's - since 0.7.93 a land well lifts
        // no other (section 13); until then with none of it, the wells lifting the town's one pool, the sea field's.
        double owned = land.getOwnedAmount(Resource.OIL);
        quietly(() -> land.restoreSites(Resource.OIL, seaSites + 2, owned + PLENTY));
        Game.BuildResult two = quietlyGet(() -> g.buildStack(well, 2, true));
        Game.BuildResult third = quietlyGet(() -> g.buildStack(well, 1, false));
        report("with two dry sites beside the sea's, two wells stand and a third is refused",
                land.getSites(Resource.OIL, true) == 2 && land.getSites(Resource.OIL, false) == seaSites
                        && two == Game.BuildResult.SUCCESS && third == Game.BuildResult.NO_DEPOSIT && g.wellsCommitted() == 2,
                two + ", " + third);

        // ...and those two dry sites taken away again, their oil left in the ground pool: the wells now stand past the dry
        // sites, as a save from before 0.7.84 carries wells on the sea's.
        quietly(() -> land.restoreSites(Resource.OIL, seaSites, owned + PLENTY));
        double world = land.getWorldTotal(Resource.OIL);
        boolean kept = true, standing = true;
        for (int m = 0; m < 6; m++) {
            quietly(() -> g.simulateMonths(1));
            // ...land wells (0.7.91): the sea's sites are a platform's, which the investors may order meanwhile (section 12).
            standing &= wells.landWellsStanding() == 2 && g.landWellsCommitted() == 2;
            kept &= conserved(land, world);
        }
        report("wells standing past the dry sites, on the sea's, are kept: six months on, both stand and lift, and no land well"
                        + " is added",
                standing && land.getSites(Resource.OIL, true) == 0 && wells.output(Good.CRUDE).produced > 0,
                String.format("%,.0f t lifted the last month", wells.output(Good.CRUDE).produced));
        Game.BuildResult again = quietlyGet(() -> g.buildStack(well, 1, false));
        assertTrue("...an order is still refused for its deposit", again == Game.BuildResult.NO_DEPOSIT);
        assertTrue("...and the world's oil is conserved, to the tonne", kept);
    }

    /* ============================ 6. ACROSS A SAVE ============================ */

    static void acrossASave(Game g) throws Exception {
        out.println("\n--- 6. the vintages cross a save, and a save from before them reads every well new ---");
        Oil wells = g.getSectors().oil();
        List<Vintage> before = wells.vintages();
        double lift = wells.getCapacity(Good.CRUDE);
        GameFiles files = FILES.get(g);
        quietly(() -> g.saveGame(10, "wells"));
        Game twin = new Game(files);
        quietly(() -> twin.loadGameSave(10));
        Oil back = twin.getSectors().oil();
        report("the vintages and the lift cross a save, to the bit",
                twin.getLoadFailure() == null && back.vintages().equals(before) && back.getCapacity(Good.CRUDE) == lift
                        && before.size() >= 2,
                before.toString());
        quietly(() -> {
            g.simulateMonths(1);
            twin.simulateMonths(1);
        });
        assertTrue("...and a month on, the city and its reload strike the same vintages and lift the same crude, to the bit",
                back.vintages().equals(wells.vintages()) && back.getCapacity(Good.CRUDE) == wells.getCapacity(Good.CRUDE)
                        && back.output(Good.CRUDE).produced == wells.output(Good.CRUDE).produced);

        // The keys, and a save without them.
        quietly(() -> twin.saveGame(10, "wells"));
        Path file = files.saveFile(10);
        com.google.gson.JsonObject o = com.google.gson.JsonParser.parseString(Files.readString(file)).getAsJsonObject();
        com.google.gson.JsonObject oilState = OilCheck.sectorOf(o, Sectors.OIL);
        com.google.gson.JsonObject extras = oilState == null ? null : oilState.getAsJsonObject("extras");
        java.util.List<String> keys = new java.util.ArrayList<>();
        if (extras != null) for (String k : extras.keySet()) if (k.startsWith(Oil.VINTAGE_KEY)) keys.add(k);
        Vintage oldest = back.vintages().isEmpty() ? null : back.vintages().get(0);
        assertTrue("...saved as the Oil sector's extras, vintages.<month opened>.<kind>, the count the value",
                oldest != null && keys.size() == back.vintages().size()
                        && extras.get(Oil.VINTAGE_KEY + oldest.month() + "." + oldest.kind().name()).getAsDouble() == oldest.count());
        for (String k : keys) extras.remove(k);
        Files.writeString(file, new com.google.gson.GsonBuilder().serializeSpecialFloatingPointValues().create().toJson(o));
        Game old = new Game(files);
        quietly(() -> old.loadGameSave(10));
        Oil oldWells = old.getSectors().oil();
        int standing = oldWells.landWellsStanding();
        report("a save from before 0.7.84 has none: every well it stands is read new, its whole nameplate",
                old.getLoadFailure() == null && oldWells.vintages().isEmpty() && standing > 0
                        && oldWells.getCapacity(Good.CRUDE) == standing * WELL_TONNES && oldWells.wornOut() == 0,
                String.format("%d well(s), %,.0f t", standing, oldWells.getCapacity(Good.CRUDE)));
        int loaded = old.getMonth();
        quietly(() -> old.simulateMonths(1));
        report("...and struck as opened the first month the city plays",
                oldWells.vintages().equals(List.of(new Vintage(loaded + 1, oldWells.landWellsStanding(), WellKind.LAND))),
                oldWells.vintages().toString());
        assertTrue("...the audit closing", old.getLastMoneyAudit().relative() < 1e-10);
    }

    /* ============================ 7. ONE POST ============================ */

    static void onePost() {
        out.println("\n--- 7. a well has one post ---");
        BuildingManager bm = new BuildingManager();
        bm.initializeBuiltInTemplates();
        BuildingsTemplate twin = bm.getTemplateByName("Oil Well");
        BuildingsTemplate data = null;
        List<BuildingsTemplate> file = new BuildingCatalog().load();
        if (file != null) for (BuildingsTemplate t : file) if (t.getId() == 74) data = t;
        boolean one = true;
        for (BuildingsTemplate t : new BuildingsTemplate[] { twin, data }) {
            one &= t != null && t.getTotalJobs() == 1 && t.getJobs(JobType.DIPLOMA) == 1;
        }
        assertTrue("the Oil Well has one post, a diploma's, in buildings.json and its built-in twin (the research's Q13; three"
                + " until 0.7.84)", one);
    }

    /* ============================ THE OIL AT SEA (0.7.91) ============================ */

    /** The sea field the sea sections stand on: section 5's, the nearest oil field to SEA_OIL_SEED's site, in the sea. */
    static Deposit seaField() {
        return OilCheck.nearestSeaOil(World.of(OilCheck.SEA_OIL_SEED));
    }

    /**
     * Section 4's staffed town on SEA_OIL_SEED's world, owning the ground over
     * the sea field (bought, as section 5 buys it) and handed plenty of oil;
     * every sector's own orders held, so the ground and the hands the sections
     * use are theirs (a section releases the wells' to watch their planner).
     * Null when the field cannot be bought.
     */
    static Game seaTown(String label) {
        Deposit sea = seaField();
        if (sea == null) return null;
        // ...its ground with room for section 9's two land wells and Strategic Reserve: the land is never restated after the
        // sea field is bought (LandManager.setOwnedSqFt() folds the purchases into the centre).
        Game g = town(label, 600, 14 * 87_120 + 300 * 10_000 + 1_076_391, OilCheck.SEA_OIL_SEED);
        LandManager land = g.getLandManager();
        int[] pushes = { 0 };
        quietly(() -> {
            g.buildStack(template(g, "House"), 300, true);
            for (Sector s : g.getSectors().all()) g.getBusinessInvestment().holdSector(s.key());
            while (!g.getCityLand().ownsPlot(sea.x(), sea.y()) && pushes[0] < 200) {
                LandParcel p = MiningCheck.nearestOffer(land.getMarket(), sea.x(), sea.y());
                if (p == null) break;
                g.setCashForTest(g.getCash() + p.localPrice(g.getForeignAccounts().getRate()));
                if (!g.buyLandParcel(p.getId())) break;
                pushes[0]++;
            }
            land.restoreSites(Resource.OIL, land.getOilSites(), PLENTY);
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.simulateMonths(12);
        });
        return g.getCityLand().ownsPlot(sea.x(), sea.y()) ? g : null;
    }

    /* ============================ 8. A PLATFORM ============================ */

    static Game aPlatform() {
        out.println("\n--- 8. a platform stands on a shallow sea field, its wells in its slots; the land's on dry ground ---");
        assertTrue("a platform stands in sea PLATFORM_MAX_DEPTH_M deep or less: 150 m, the fixed platform's limit (the research's 3.2)",
                Oil.PLATFORM_MAX_DEPTH_M == 150);
        Deposit d = seaField();
        if (d == null) {
            report("fixture: SEA_OIL_SEED's nearest oil field is in the sea", false, "none");
            return null;
        }
        assertTrue("...the rule, pure: a field in the sea at 150 m takes one, at 150.5 m or on land or with no site owned none",
                Oil.isShallow(new LandManager.SeaField(d, 8, 150)) && !Oil.isShallow(new LandManager.SeaField(d, 8, 150.5))
                        && !Oil.isShallow(new LandManager.SeaField(d, 8, 0)) && !Oil.isShallow(new LandManager.SeaField(d, 0, 40)));
        // The slots, pure: a 16-site field takes a jacket of 12 and one of 4, a third none; a field the city does not own none.
        LandManager.SeaField big = new LandManager.SeaField(d, 16, 60);
        List<Oil.Platform> three = List.of(new Oil.Platform(d.cell(), d.index(), 0, 1), new Oil.Platform(d.cell(), d.index(), 0, 2),
                new Oil.Platform(d.cell(), d.index(), 0, 3), new Oil.Platform(d.cell() + 1, d.index(), 0, 4));
        int[] slots = Oil.slotsOf(three, List.of(big), 12);
        report("slots are at most 12 a jacket and at most the field's sea sites: a 16-site field holds 12 and 4, then none",
                java.util.Arrays.equals(slots, new int[] { 12, 4, 0, 0 })
                        && java.util.Arrays.equals(Oil.freeSites(three.subList(0, 1), List.of(big), 12), new int[] { 4 }),
                java.util.Arrays.toString(slots));
        List<Oil.Platform> viewed = Oil.platformView(List.of(), 2, 15, 7, List.of(big), 12, d.x(), d.y());
        report("...two jackets read anew stand on it, its wells dealt oldest first: 15 wells, 12 and 3",
                viewed.equals(List.of(new Oil.Platform(d.cell(), d.index(), 12, 7), new Oil.Platform(d.cell(), d.index(), 3, 7))),
                viewed.toString());

        Game g = seaTown("wellcheck-platform");
        if (g == null) {
            report("fixture: the town buys the ground over the sea field", false, "could not");
            return null;
        }
        LandManager land = g.getLandManager();
        Oil wells = g.getSectors().oil();
        BuildingsTemplate jacket = template(g, "Offshore Platform"), seaWell = template(g, "Platform Well"),
                landWell = template(g, "Oil Well");
        List<LandManager.SeaField> shallow = wells.shallowFields();
        World w = World.of(OilCheck.SEA_OIL_SEED);
        report("fixture: the town owns the sea field, a shallow one - its sites all the sea's, its depth the world's",
                shallow.size() == 1 && shallow.get(0).field().equals(d) && shallow.get(0).sites() == d.sites()
                        && shallow.get(0).depth() == w.depthAt(d.x(), d.y()) && shallow.get(0).depth() <= Oil.PLATFORM_MAX_DEPTH_M
                        && land.getSites(Resource.OIL, true) == 0 && land.getSites(Resource.OIL, false) == d.sites(),
                String.format("%d sites, %.1f m deep, %.2f km out", d.sites(), w.depthAt(d.x(), d.y()),
                        Math.hypot(d.x() - g.getCityLand().siteX(), d.y() - g.getCityLand().siteY()) * World.PLOT_M / 1000));
        report("with no platform: room for one jacket (its " + d.sites() + " sites, a jacket to every 12 or part), no slot for a well",
                g.sitesFor(jacket) == 1 && g.sitesFor(seaWell) == 0 && wells.platformRoom() == 1,
                g.sitesFor(jacket) + " jacket(s), " + g.sitesFor(seaWell) + " slot(s)");
        Game.BuildResult noSlot = quietlyGet(() -> g.buildStack(seaWell, 1, false));
        BuildCard.Verdict noSlotCard = BuildCard.verdict(g, seaWell, 1);
        report("...a platform well is refused for its deposit, on the order and the card", noSlot == Game.BuildResult.NO_DEPOSIT
                && noSlotCard.kind() == BuildCard.VerdictKind.NO_DEPOSIT, noSlot + ", " + noSlotCard.kind());
        Game.BuildResult stood = quietlyGet(() -> g.buildStack(jacket, 1, true));
        Game.BuildResult second = quietlyGet(() -> g.buildStack(jacket, 1, false));
        int opened = g.getMonth();
        quietly(() -> g.simulateMonths(1));
        report("a jacket stands on it; a second is refused for its deposit - the field's " + d.sites() + " sites are its slots - and the record"
                        + " is struck the first month played",
                stood == Game.BuildResult.SUCCESS && second == Game.BuildResult.NO_DEPOSIT
                        && wells.platforms().equals(List.of(new Oil.Platform(d.cell(), d.index(), 0, opened + 1)))
                        && wells.slotsStanding() == Math.min(12, d.sites()) && wells.platformRoom() == 0,
                stood + ", " + second + "; " + wells.platforms() + ", " + wells.slotsStanding() + " slots");
        Game.BuildResult eight = quietlyGet(() -> g.buildStack(seaWell, d.sites(), true));
        Game.BuildResult ninth = quietlyGet(() -> g.buildStack(seaWell, 1, false));
        Game.BuildResult onLand = quietlyGet(() -> g.buildStack(landWell, 1, false));
        report("a well to every slot stands, the next is refused; a land well is refused too - dry and sea sites kept apart",
                eight == Game.BuildResult.SUCCESS && ninth == Game.BuildResult.NO_DEPOSIT && onLand == Game.BuildResult.NO_DEPOSIT
                        && g.sitesFor(landWell) == 0 && g.sitesFor(seaWell) == d.sites() && g.platformWellsCommitted() == d.sites()
                        && g.landWellsCommitted() == 0 && g.wellsCommitted() == d.sites(),
                eight + ", " + ninth + ", " + onLand);
        double world = land.getWorldTotal(Resource.OIL);
        boolean kept = true, struck = true;
        int wellsOpened = g.getMonth() + 1;
        for (int m = 0; m < 6; m++) {
            quietly(() -> g.simulateMonths(1));
            struck &= wells.output(Good.CRUDE).capacity == liftOf(wells.vintages(), WELL_TONNES, g.getMonth());
            kept &= conserved(land, world);
        }
        report("the wells in its slots lift on the platform's profile, struck as a vintage of their own, its nameplate to the bit",
                wells.platformWellsInSlots() == d.sites()
                        && wells.vintages().equals(List.of(new Vintage(wellsOpened, d.sites(), WellKind.PLATFORM)))
                        && wells.getCapacity(Good.CRUDE) == d.sites() * WELL_TONNES && wells.getCapacityAtSea() == wells.getCapacity(Good.CRUDE)
                        && struck && wells.platforms().get(0).wells() == d.sites(),
                wells.vintages().toString());
        int month = g.getMonth();
        quietly(() -> wells.setVintagesForTest(List.of(new Vintage(month - 40, d.sites(), WellKind.PLATFORM))));
        double expected = d.sites() * WELL_TONNES * Oil.profile(WellKind.PLATFORM, 40);
        report("...forty months old, four months past its plateau: " + d.sites() + " x 415 t x 0.915^(4/12), to the bit",
                wells.getCapacity(Good.CRUDE) == expected && expected < d.sites() * WELL_TONNES,
                String.format("%,.6f t", wells.getCapacity(Good.CRUDE)));
        assertTrue("...and the world's oil is conserved, to the tonne", kept);

        // Worn out at 348 months, oldest first, and the slots refilled by the planner one a month.
        quietly(() -> wells.setVintagesForTest(List.of(new Vintage(month - (Oil.lifeMonths(WellKind.PLATFORM) - 1), 2, WellKind.PLATFORM),
                new Vintage(month - 100, d.sites() - 2, WellKind.PLATFORM))));
        int retiredBefore = g.getWellsWornOut();
        boolean none = wells.wornOut(WellKind.PLATFORM) == 0;
        quietly(() -> {
            g.getBusinessInvestment().releaseSector(Sectors.OIL);
            g.simulateMonths(1);
        });
        report("two platform wells at 348 months are retired by the month's retirements, the younger kept",
                none && g.getWellsWornOut() - retiredBefore == 2 && wells.vintages().get(0).equals(new Vintage(month - 100, d.sites() - 2, WellKind.PLATFORM)),
                String.format("%d retired; %s", g.getWellsWornOut() - retiredBefore, wells.vintages()));
        // A platform well is 2,800 points of the town's builders' work, some months each.
        int[] committed = new int[25];
        boolean oneAMonth = true;
        committed[0] = g.platformWellsCommitted();
        for (int m = 1; m < committed.length; m++) {
            quietly(() -> g.simulateMonths(1));
            committed[m] = g.platformWellsCommitted();
            oneAMonth &= committed[m] - committed[m - 1] <= 1 && committed[m] <= wells.slotsStanding();
        }
        quietly(() -> g.getBusinessInvestment().holdSector(Sectors.OIL));
        report("...and the planner drills the freed slots again, one at a time", wells.platformWellsStanding() == d.sites() && oneAMonth,
                java.util.Arrays.toString(committed) + " platform wells committed by month");
        return g;
    }

    /* ============================ 9. THE SHUTTLE TANKERS ============================ */

    static void theShuttleTankers(Game g) {
        out.println("\n--- 9. the shuttle tankers: platform crude sold at home pays the boundary's freight; abroad, none ---");
        Oil wells = g.getSectors().oil();
        LandManager land = g.getLandManager();
        quietly(() -> g.simulateMonths(1));
        Sector.Output o = wells.output(Good.CRUDE);
        report("fixture: no buyer at home - every tonne the platform lifts is shipped abroad, and the shuttle tankers are paid"
                        + " nothing",
                o.produced > 0 && o.soldLocal == 0 && wells.getShuttleTonnes() == 0 && wells.getShuttleBill() == 0,
                String.format("%,.0f t lifted, %,.0f shipped", o.produced, o.exported));
        // Two dry sites handed to the centre with two land wells on them, and the city's Strategic Reserve filling - more than
        // the wells lift - to buy their crude at home (OilCheck 16's fill: the wells' crude first, the world's for the rest).
        int seaSites = land.getSites(Resource.OIL, false);
        BuildingsTemplate reserveT = template(g, "Strategic Reserve");
        Game.BuildResult[] stood = new Game.BuildResult[2];
        quietly(() -> {
            land.restoreSites(Resource.OIL, seaSites + 2, PLENTY);
            stood[0] = g.buildStack(template(g, "Oil Well"), 2, true);
            stood[1] = g.buildStack(reserveT, 1, true);
            g.simulateMonths(1);
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.fillReserve(FILL);
        });
        double groundBefore = land.getOilExtractedOnGround(), seaBefore = land.getOilExtractedAtSea();
        quietly(() -> g.simulateMonths(1));
        o = wells.output(Good.CRUDE);
        // ...the month's crude by pool (0.7.93): the platforms' from the offshore pool, the land wells' from the ground's.
        double sea = wells.getLiftedAtSea(), onGround = wells.getLiftedOnGround();
        GoodsMarket crude = g.getMarkets().get(Good.CRUDE);
        double freight = Good.CRUDE.baseFreight() * crude.getFreightFactor() * crude.getExchangeRate();
        double home = Math.min(o.soldLocal, o.produced), tonnes = home * (sea / (onGround + sea)), bill = tonnes * freight;
        report("with the city's reserve filling, the crude ashore is the offshore pool's share of the month's lift sold at home -"
                        + " the platforms' and two land wells' (0.7.93; the platforms' share of the nameplate until then)",
                stood[0] == Game.BuildResult.SUCCESS && stood[1] == Game.BuildResult.SUCCESS
                        && home > 0 && sea > 0 && onGround > 0 && wells.getShuttleTonnes() == tonnes,
                String.format("%,.3f t of %,.3f sold at home, %.4f at sea", tonnes, home, sea / (onGround + sea)));
        report("...each part what left its pool: the platforms' the offshore pool's E, the land wells' the ground's, the two the"
                        + " month's crude",
                close(sea, land.getOilExtractedAtSea() - seaBefore) && close(onGround, land.getOilExtractedOnGround() - groundBefore)
                        && onGround + sea == o.produced,
                String.format("%,.6f t at sea, %,.6f on the ground", sea, onGround));
        report("...at CRUDE's band freight a tonne, baseFreight x freightFactor x the rate, to the bit",
                wells.shuttleFreightPerTonne() == freight && wells.getShuttleBill() == bill,
                String.format("%,.4f a tonne, %,.4f", freight, bill));
        double paid = bill;
        quietly(() -> {
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.fillReserve(FILL);
            g.simulateMonths(1);
        });
        Sector.Statement st = wells.statement();
        Double named = st.otherInputs.get(Oil.SHUTTLE_TANKERS);
        report("...booked as a service bought from the world: the Oil sector's \"Shuttle tankers\" on its input line, in its imports",
                named != null && named == paid && st.imports >= paid && st.inputs >= paid,
                String.format("%,.4f named, %,.4f imports", named == null ? 0 : named, st.imports));
        report("...and the audit closes", g.getLastMoneyAudit().relative() < 1e-10,
                String.format("%.2e", g.getLastMoneyAudit().relative()));
    }

    /* ============================ 10. THE PIPELINE ============================ */

    static void thePipeline(Game g) {
        out.println("\n--- 10. the pipeline: it pays on a big field and not on a small one; a whole pipe ends the shuttle ---");
        Oil wells = g.getSectors().oil();
        BuildingsTemplate pipe = template(g, "Crude Pipeline");
        Deposit d = seaField();
        long sx = g.getCityLand().siteX(), sy = g.getCityLand().siteY();
        int km = Oil.lengthKm(d, sx, sy);
        report("a field's pipe is the straight line from it to the founding site, in whole kilometres rounded up",
                km == (int) Math.ceil(Math.hypot(d.x() - sx, d.y() - sy) * World.PLOT_M / 1000) && km >= 1, km + " km");
        BusinessInvestment plans = g.getBusinessInvestment();
        double cost = plans.getCostOf(pipe, km), standing = plans.standingCostOf(wells, pipe) * km, freight = wells.shuttleFreightPerTonne();
        double bigField = 12 * WELL_TONNES, smallField = WELL_TONNES;
        report("the rule (freight less repairs and tax a tonne) x tonnes x min(months, 480) >= 1.25 x cost: a full platform's"
                        + " crude pays for it, one well's does not",
                Oil.pipelinePays(freight, standing, bigField, Oil.PIPE_LIFE_MONTHS, cost)
                        && !Oil.pipelinePays(freight, standing, smallField, Oil.PIPE_LIFE_MONTHS, cost),
                String.format("D$%,.0fk for %d km; %,.2f a month big, %,.2f small, against %,.2f", cost, km,
                        (freight * bigField - standing), (freight * smallField - standing), cost * Oil.PIPE_PAYBACK / Oil.PIPE_LIFE_MONTHS));
        double edge = cost * Oil.PIPE_PAYBACK / Oil.PIPE_LIFE_MONTHS;
        assertTrue("...its months capped at the pipe's 480, and on land - no freight saved - never",
                Oil.pipelinePays(freight, standing, bigField, 10_000, cost) == Oil.pipelinePays(freight, standing, bigField, Oil.PIPE_LIFE_MONTHS, cost)
                        && !Oil.pipelinePays(0, standing, bigField, Oil.PIPE_LIFE_MONTHS, cost)
                        && Oil.pipelinePays(edge / bigField + standing / bigField, standing, bigField, Oil.PIPE_LIFE_MONTHS, cost)
                                == (((edge / bigField + standing / bigField) - standing / bigField) * bigField * Oil.PIPE_LIFE_MONTHS >= cost * Oil.PIPE_PAYBACK));
        Oil.PipeCase c = null;
        for (LandManager.SeaField f : wells.shallowFields()) c = wells.pipeCase(f, plans);
        Oil.PipeCase cc = c;
        report("...weighed on the town's field: its cost, freight and tonnes the rule's, and the provisos - the refiners not losing"
                        + " money, its first platform on its plateau",
                c != null && c.km() == km && c.cost() == cost && c.freight() == freight
                        && c.pays() == (c.refinersPay() && c.onPlateau() && Oil.pipelinePays(c.freight(), c.standing(), c.tonnes(), c.months(), c.cost())),
                cc == null ? "none" : String.format("%,.0f t a month, %,.0f months, refiners %s, plateau %s: %s", cc.tonnes(), cc.months(),
                        cc.refinersPay(), cc.onPlateau(), cc.pays() ? "pays" : "does not pay"));
        // The player lays the field's pipe: the kilometres go to the platform's field, and its crude comes ashore by pipe.
        Game.BuildResult laid = quietlyGet(() -> g.buildStack(pipe, km, true));
        int at = g.getMonth();
        quietly(() -> {
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.fillReserve(FILL);
            g.simulateMonths(1);
        });
        report("a pipe the player lays goes to the platform's field with none, its kilometres the record's",
                laid == Game.BuildResult.SUCCESS && wells.pipelines().equals(List.of(new Oil.Pipeline(d.cell(), d.index(), km, at + 1))),
                laid + ", " + wells.pipelines());
        quietly(() -> {
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.fillReserve(FILL);
            g.simulateMonths(1);
        });
        Sector.Output o = wells.output(Good.CRUDE);
        report("...whole, it carries the field's crude: the shuttle tankers carry none of it while the crude is sold at home",
                wells.pipedShare() == 1 && o.soldLocal > 0 && wells.getShuttleTonnes() == 0 && wells.getShuttleBill() == 0,
                String.format("piped %.2f; %,.0f t sold at home", wells.pipedShare(), o.soldLocal));
    }

    /* ============================ 11. ACROSS A SAVE ============================ */

    static void seaAcrossASave(Game g) throws Exception {
        out.println("\n--- 11. the platforms and pipelines cross a save; a save from before them reads them anew ---");
        Oil wells = g.getSectors().oil();
        List<Oil.Platform> platforms = wells.platforms();
        List<Oil.Pipeline> pipes = wells.pipelines();
        double lift = wells.getCapacity(Good.CRUDE);
        GameFiles files = FILES.get(g);
        quietly(() -> g.saveGame(10, "sea"));
        Game twin = new Game(files);
        quietly(() -> twin.loadGameSave(10));
        // ...the fixture's holds, which a save does not carry, held in the reload too: the same city, the same month.
        for (Sector s : twin.getSectors().all()) {
            if (g.getBusinessInvestment().isHeld(s.key())) twin.getBusinessInvestment().holdSector(s.key());
        }
        Oil back = twin.getSectors().oil();
        report("the platforms, the pipelines and the lift cross a save, to the bit",
                twin.getLoadFailure() == null && back.platforms().equals(platforms) && back.pipelines().equals(pipes)
                        && back.getCapacity(Good.CRUDE) == lift && back.pipedShare() == wells.pipedShare() && !platforms.isEmpty(),
                platforms + " " + pipes);
        quietly(() -> {
            g.simulateMonths(1);
            twin.simulateMonths(1);
        });
        report("...and a month on, the city and its reload strike the same and lift the same, to the bit",
                back.platforms().equals(wells.platforms()) && back.pipelines().equals(wells.pipelines())
                        && back.output(Good.CRUDE).produced == wells.output(Good.CRUDE).produced
                        && back.getShuttleBill() == wells.getShuttleBill(),
                back.platforms() + " / " + wells.platforms() + "; " + back.pipelines() + " / " + wells.pipelines() + "; "
                        + back.output(Good.CRUDE).produced + " / " + wells.output(Good.CRUDE).produced + "; "
                        + back.getShuttleBill() + " / " + wells.getShuttleBill());
        quietly(() -> twin.saveGame(10, "sea"));
        Path file = files.saveFile(10);
        com.google.gson.JsonObject o = com.google.gson.JsonParser.parseString(Files.readString(file)).getAsJsonObject();
        com.google.gson.JsonObject extras = OilCheck.sectorOf(o, Sectors.OIL).getAsJsonObject("extras");
        java.util.List<String> keys = new java.util.ArrayList<>();
        for (String k : extras.keySet()) if (k.startsWith(Oil.PLATFORM_KEY) || k.startsWith(Oil.PIPELINE_KEY)) keys.add(k);
        Oil.Platform p0 = back.platforms().get(0);
        String key = Oil.PLATFORM_KEY + "0." + p0.cell() + "." + p0.index() + "." + p0.month();
        assertTrue("...saved as the Oil sector's extras, platforms.<place>.<cell>.<index>.<month> its wells, pipelines.<...> its km",
                keys.size() == back.platforms().size() + back.pipelines().size() && extras.has(key)
                        && extras.get(key).getAsDouble() == p0.wells());
        for (String k : keys) extras.remove(k);
        Files.writeString(file, new com.google.gson.GsonBuilder().serializeSpecialFloatingPointValues().create().toJson(o));
        Game old = new Game(files);
        quietly(() -> old.loadGameSave(10));
        Oil oldWells = old.getSectors().oil();
        int loaded = old.getMonth();
        quietly(() -> old.simulateMonths(1));
        report("a save without them reads the jacket standing anew on its field, its wells in its slots, the pipe on its field -"
                        + " struck the first month played - and the audit closes",
                old.getLoadFailure() == null && oldWells.platforms().size() == 1
                        && oldWells.platforms().get(0).cell() == p0.cell() && oldWells.platforms().get(0).index() == p0.index()
                        && oldWells.platforms().get(0).month() == loaded + 1 && oldWells.platforms().get(0).wells() == p0.wells()
                        && oldWells.pipelines().size() == 1 && oldWells.pipelines().get(0).cell() == p0.cell()
                        && old.getLastMoneyAudit().relative() < 1e-10,
                oldWells.platforms() + " " + oldWells.pipelines());
    }

    /* ============================ 12. THE PLANNER ============================ */

    static void thePlanner() {
        out.println("\n--- 12. the planner weighs the sea after the land: a jacket as a package, then its wells ---");
        Game g = seaTown("wellcheck-planner");
        if (g == null) {
            report("fixture: the town buys the ground over the sea field", false, "could not");
            return;
        }
        Oil wells = g.getSectors().oil();
        BusinessInvestment plans = g.getBusinessInvestment();
        BuildingsTemplate jacket = template(g, "Offshore Platform"), seaWell = template(g, "Platform Well");
        int jacketsBefore = wells.jacketsStanding();
        Oil.JacketCase c = wells.jacketCase(plans);
        double share = c == null ? Double.NaN : wells.estimatedMonthlyProfit(jacket, plans) * c.cost() / plans.getCostOf(jacket, 1);
        report("the investors' test on a jacket is its package's: its share by cost of the jacket and min(12, sites) wells' earnings",
                c != null && c.wells() == Math.min(12, seaField().sites()) && Math.abs(share - c.earns()) <= 1e-9 * Math.max(1, Math.abs(c.earns()))
                        && c.cost() == plans.getCostOf(jacket, 1) + plans.getCostOf(seaWell, c.wells()),
                c == null ? "none" : String.format("%d wells: earns %,.2f a month on %,.0f", c.wells(), c.earns(), c.cost()));
        BusinessInvestment.Decision onLand = wells.planOnLand(plans, g);
        BusinessInvestment.Decision d = quietlyGet(() -> wells.plan(plans, g));
        boolean staffs = wells.staffing(jacket).passes();
        report("with no dry site and the sea field free, the land says no and the planner weighs a jacket on it: ordered while"
                        + " the package earns and the town can staff it",
                !onLand.build && jacketsBefore == 0 && (d.build ? d.template == jacket && c != null && c.earns() > 0 && staffs
                        : !(c != null && c.earns() > 0 && staffs) && wells.atSeaWord() != null),
                (d.build ? "ordered " + d.template.getName() + ": " : "held: " + wells.atSeaWord() + "; land: ") + d.reason);
        // A jacket standing: its slots free, a platform well next.
        quietly(() -> {
            plans.holdSector(Sectors.OIL);
            g.buildStack(jacket, 1, true);
            g.simulateMonths(1);
        });
        BusinessInvestment.Decision next = quietlyGet(() -> wells.plan(plans, g));
        double each = wells.estimatedMonthlyProfit(seaWell, plans);
        report("...with a jacket standing and its slots free, a platform well into one, while one earns",
                wells.slotsStanding() > 0 && (next.build ? next.template == seaWell && each > 0 : !(each > 0) || !wells.staffing(seaWell).passes()),
                (next.build ? next.template.getName() + ": " : "held: ") + next.reason);
        // Once the oil is worked out, the empty jacket is decommissioned at the top of the month - and then the pipe.
        quietly(() -> {
            g.buildStack(template(g, "Crude Pipeline"), 2, true);
            g.getLandManager().restoreSites(Resource.OIL, g.getLandManager().getOilSites(), 0);
            plans.releaseSector(Sectors.OIL);
            g.simulateMonths(2);
            plans.holdSector(Sectors.OIL);
        });
        report("once the oil is worked out, the jacket with no well is decommissioned, and with none left the pipe",
                wells.jacketsStanding() == 0 && wells.pipeKmStanding() == 0 && wells.platformWellsStanding() == 0,
                wells.jacketsStanding() + " jacket(s), " + wells.pipeKmStanding() + " km");
    }

    /* ============================ 13. THE TWO POOLS (0.7.93) ============================ */

    /** Section 13's ground pool: two months of a land well's nameplate, so it is worked out inside the months the section runs. */
    static final double GROUND = 2 * WELL_TONNES;

    /** Section 13's offshore pool left for its other half: less than a month of the platform wells' lift, so it is worked out in one. */
    static final double LAST_AT_SEA = 100;

    /** Whether two figures agree but for a rounding: within a billionth of the larger, or of a tonne. */
    static boolean close(double a, double b) {
        return Math.abs(a - b) <= 1e-9 * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }

    /** The value of the line of `lines` with this label, or null. */
    static String lineValue(List<Sector.Line> lines, String label) {
        for (Sector.Line l : lines) if (label.equals(l.label())) return l.value();
        return null;
    }

    /** Months played until a pool is worked out to the tonne's last bit (left == 0), at most `most`; the months played. */
    static int untilWorkedOut(Game g, java.util.function.DoubleSupplier left, int most) {
        int m = 0;
        while (left.getAsDouble() > 0 && m < most) {
            quietly(() -> g.simulateMonths(1));
            m++;
        }
        return m;
    }

    static void theTwoPools() throws Exception {
        out.println("\n--- 13. the city's crude is two pools: the land wells lift the ground's, the platform wells the sea's (0.7.93) ---");
        Game g = seaTown("wellcheck-pools");
        if (g == null) {
            report("fixture: the town buys the ground over the sea field", false, "could not");
            return;
        }
        Deposit d = seaField();
        LandManager land = g.getLandManager();
        Oil wells = g.getSectors().oil();
        BuildingsTemplate jacket = template(g, "Offshore Platform"), seaWell = template(g, "Platform Well"), landWell = template(g, "Oil Well");
        int seaSites = land.getSites(Resource.OIL, false);
        double bought = g.getCityLand().purchasedAmount(Resource.OIL);
        // Two dry sites handed to the centre with GROUND by fiat - the ground pool - beside the sea field the town bought.
        quietly(() -> land.restoreSites(Resource.OIL, seaSites + 2, bought + GROUND));
        double ground = land.getOilOwnedOnGround(), sea = land.getOilOwnedAtSea();
        report("the offshore pool is the sea field's tonnes, the ground pool the rest - the centre's by fiat - and the two the city's"
                        + " oil",
                sea == d.amount() && close(ground, GROUND) && close(ground + sea, land.getOwnedAmount(Resource.OIL))
                        && land.getSites(Resource.OIL, true) == 2 && seaSites == d.sites() && land.getOilExtractedOnGround() == 0
                        && land.getOilExtractedAtSea() == 0,
                String.format("%,.3f t at sea, %,.3f on the ground", sea, ground));

        // A jacket on the field, a platform well in all but one of its slots, a land well on one of the two dry sites.
        Game.BuildResult[] stood = new Game.BuildResult[3];
        quietly(() -> {
            stood[0] = g.buildStack(jacket, 1, true);
            g.simulateMonths(1);
            stood[1] = g.buildStack(seaWell, d.sites() - 1, true);
            stood[2] = g.buildStack(landWell, 1, true);
        });
        report("fixture: a jacket on the field with a well in all but one slot, a land well on one of the two dry sites",
                stood[0] == Game.BuildResult.SUCCESS && stood[1] == Game.BuildResult.SUCCESS && stood[2] == Game.BuildResult.SUCCESS
                        && wells.platformWellsStanding() == d.sites() - 1 && wells.landWellsStanding() == 1,
                stood[0] + ", " + stood[1] + ", " + stood[2]);
        double world = land.getWorldTotal(Resource.OIL);
        boolean split = true, kept = true, apart = true;
        int months = 0, workedOutAt = -1;
        while (months < 12 && (workedOutAt < 0 || months < workedOutAt + 2)) {
            double eg = land.getOilExtractedOnGround(), es = land.getOilExtractedAtSea();
            quietly(() -> g.simulateMonths(1));
            months++;
            double dg = land.getOilExtractedOnGround() - eg, ds = land.getOilExtractedAtSea() - es;
            split &= close(dg, wells.getLiftedOnGround()) && close(ds, wells.getLiftedAtSea()) && ds > 0
                    && wells.getLiftedOnGround() + wells.getLiftedAtSea() == wells.output(Good.CRUDE).produced;
            kept &= conserved(land, world);
            if (workedOutAt >= 0) apart &= dg == 0 && wells.getLiftedOnGround() == 0 && ds > 0;
            if (workedOutAt < 0 && land.getOilLeftOnGround() == 0) workedOutAt = months;
        }
        report("every month each pool gives its own wells' crude: the land well's what left the ground pool, the platforms' what"
                        + " left the sea's, the two the month's crude",
                split && months > 0, months + " month(s)");
        report("the ground pool worked out, the land well lifts nothing while the platform wells lift on from the sea's",
                workedOutAt > 0 && apart && land.getOilExtractedOnGround() == ground && land.getOilLeftAtSea() > 0,
                String.format("worked out in month %d; %,.3f t lifted of the ground's %,.3f; %,.0f t left at sea", workedOutAt,
                        land.getOilExtractedOnGround(), ground, land.getOilLeftAtSea()));
        assertTrue("...and the world's oil is conserved, to the tonne, every month", kept);

        // The gates, each kind on its own pool.
        Game.BuildResult landRefused = quietlyGet(() -> g.buildStack(landWell, 1, false));
        BuildCard.Verdict landCard = BuildCard.verdict(g, landWell, 1);
        report("a land well is refused for its deposit with a dry site free - its pool worked out - on the order and the card",
                g.sitesFor(landWell) - g.landWellsCommitted() == 1 && landRefused == Game.BuildResult.NO_DEPOSIT
                        && landCard.kind() == BuildCard.VerdictKind.NO_DEPOSIT && g.remainingFor(landWell) == 0,
                landRefused + ", " + landCard.kind());
        report("...while a platform well into the free slot is let through, the offshore pool its own: the card's tonnes the sea's",
                g.sitesFor(seaWell) - g.platformWellsCommitted() == 1 && g.hasDepositFor(seaWell, 1)
                        && g.remainingFor(seaWell) == land.getOilLeftAtSea() && g.remainingFor(jacket) == land.getOilLeftAtSea(),
                String.format("%,.0f t under the sea", g.remainingFor(seaWell)));
        BusinessInvestment.Decision word = wells.planOnLand(g.getBusinessInvestment(), g);
        report("...and the land's word says the oil on dry ground is worked out, a deposit's word (ORE)",
                !word.build && word.reason.contains("dry ground") && word.reason.contains("worked out")
                        && BuildCard.wordKind(word.reason) == BuildCard.WordKind.ORE,
                "\"" + word.reason + "\"");

        // The spare: the well over the worked-out pool.
        double[] measure = wells.retirementDemandAndCapacity(g);
        double capacity = wells.getCapacity(Good.CRUDE);
        report("the land well over the worked-out pool is the spare - the platforms' lift the demand, the whole the capacity - and"
                        + " the shrinking rules may sell it and not a platform's",
                measure != null && measure[1] == capacity && close(measure[0], wells.getCapacityAtSea()) && measure[0] < capacity
                        && wells.mayRetire(landWell) && !wells.mayRetire(seaWell),
                measure == null ? "none" : String.format("%,.1f against %,.1f", measure[0], measure[1]));

        // The Oil page: each pool its line.
        List<Sector.Line> lines = wells.ownLines(g);
        String onGroundLine = lineValue(lines, "Crude in the ground"), atSeaLine = lineValue(lines, "Crude under the sea");
        report("the Oil page shows both pools: the ground's in \"Crude in the ground\", the sea's in the At sea lines'"
                        + " \"Crude under the sea\"",
                (Formats.INSTANCE.count(0) + " t").equals(onGroundLine)
                        && (Formats.INSTANCE.count(land.getOilLeftAtSea()) + " t").equals(atSeaLine),
                onGroundLine + " / " + atSeaLine);

        // The map greys each pool apart.
        double[] byHolding = land.remainingByHolding(Resource.OIL);
        double sum = 0, most = 0;
        for (double v : byHolding) {
            sum += v;
            most = Math.max(most, v);
        }
        report("the map greys each pool apart: the centre's ground oil worked out, the sea field's holding its pool's left, the"
                        + " holdings' the two pools'",
                byHolding[0] == 0 && close(most, land.getOilLeftAtSea()) && close(sum, land.getRemaining(Resource.OIL)),
                String.format("centre %,.0f, the field's %,.0f, all %,.0f", byHolding[0], most, sum));

        // Across a save, and a save from before the pools.
        GameFiles files = FILES.get(g);
        quietly(() -> g.saveGame(10, "pools"));
        Game twin = new Game(files);
        quietly(() -> twin.loadGameSave(10));
        LandManager tl = twin.getLandManager();
        report("the two pools' E cross a save, to the bit",
                twin.getLoadFailure() == null && tl.getOilExtractedOnGround() == land.getOilExtractedOnGround()
                        && tl.getOilExtractedAtSea() == land.getOilExtractedAtSea() && land.getOilExtractedAtSea() > 0
                        && tl.getOilLeftAtSea() == land.getOilLeftAtSea(),
                String.format("%,.6f / %,.6f t", tl.getOilExtractedOnGround(), tl.getOilExtractedAtSea()));
        Path file = files.saveFile(10);
        com.google.gson.JsonObject o = com.google.gson.JsonParser.parseString(Files.readString(file)).getAsJsonObject();
        int oil = Resource.OIL.ordinal();
        assertTrue("...saved in SAVE_FORMAT 35 and since: the depletion's oil the ground pool's E, oilDepletionAtSea the sea's",
                o.get("saveFormat").getAsInt() == GameVersion.SAVE_FORMAT && GameVersion.SAVE_FORMAT >= LandManager.TWO_POOLS_FORMAT
                        && o.getAsJsonArray("depletion").get(oil).getAsDouble() == land.getOilExtractedOnGround()
                        && o.get("oilDepletionAtSea").getAsDouble() == land.getOilExtractedAtSea());
        // As a format-34 save carries it: one pool's E - past the dry fields' tonnes by half the sea's - and none at sea.
        double onePool = ground + sea / 2;
        o.addProperty("saveFormat", LandManager.TWO_POOLS_FORMAT - 1);
        o.remove("oilDepletionAtSea");
        o.getAsJsonArray("depletion").set(oil, new com.google.gson.JsonPrimitive(onePool));
        Files.writeString(file, new com.google.gson.GsonBuilder().serializeSpecialFloatingPointValues().create().toJson(o));
        Game old = new Game(files);
        quietly(() -> old.loadGameSave(10));
        LandManager ol = old.getLandManager();
        report("a save from before the pools: its one pool's E charged to the ground, floored at the ground's tonnes, none to the"
                        + " sea - the sea's oil whole",
                old.getLoadFailure() == null && ol.getOilExtractedOnGround() == ol.getOilOwnedOnGround() && ol.getOilExtractedAtSea() == 0
                        && ol.getOilLeftAtSea() == ol.getOilOwnedAtSea() && ol.getOilLeftOnGround() == 0 && conserved(ol, world),
                String.format("%,.0f t lifted as one pool: %,.0f on the ground, %,.0f at sea", onePool, ol.getOilExtractedOnGround(),
                        ol.getOilExtractedAtSea()));
        quietly(() -> old.simulateMonths(1));
        assertTrue("...a month on, the world's oil conserved and the audit closing", conserved(ol, world)
                && old.getLastMoneyAudit().relative() < 1e-10);
        o.getAsJsonArray("depletion").set(oil, new com.google.gson.JsonPrimitive(ground / 2));
        Files.writeString(file, new com.google.gson.GsonBuilder().serializeSpecialFloatingPointValues().create().toJson(o));
        Game half = new Game(files);
        quietly(() -> half.loadGameSave(10));
        report("...and one that lifted less than the dry fields hold charges it all to the ground",
                half.getLoadFailure() == null && half.getLandManager().getOilExtractedOnGround() == ground / 2
                        && half.getLandManager().getOilExtractedAtSea() == 0,
                String.format("%,.3f t on the ground", half.getLandManager().getOilExtractedOnGround()));

        // The other way: the ground refilled, and the offshore pool left LAST_AT_SEA (the load's setter).
        quietly(() -> {
            land.restoreSites(Resource.OIL, seaSites + 2, bought + PLENTY);
            land.restoreOilPools(sea - LAST_AT_SEA);
        });
        double groundStart = land.getOilExtractedOnGround();
        int played = untilWorkedOut(g, land::getOilLeftAtSea, 3);
        report("the offshore pool's last 100 t: the platforms lift those and no more, the land well its month from the ground's",
                played == 1 && land.getOilLeftAtSea() == 0 && wells.getLiftedAtSea() == LAST_AT_SEA && wells.getLiftedOnGround() > 0
                        && land.getOilExtractedOnGround() - groundStart == wells.getLiftedOnGround(),
                String.format("%d month(s): %,.3f t at sea, %,.3f on the ground", played, wells.getLiftedAtSea(), wells.getLiftedOnGround()));
        double[] seaSpare = wells.retirementDemandAndCapacity(g);
        Game.BuildResult seaRefused = quietlyGet(() -> g.buildStack(seaWell, 1, false));
        report("...the sea worked out: the platform wells the spare and the land well kept, a platform well refused for its deposit"
                        + " with its slot free, a jacket too",
                seaSpare != null && close(seaSpare[0], seaSpare[1] - wells.getCapacityAtSea()) && wells.mayRetire(seaWell)
                        && !wells.mayRetire(landWell) && seaRefused == Game.BuildResult.NO_DEPOSIT && !g.hasDepositFor(jacket, 1)
                        && g.remainingFor(landWell) == land.getOilLeftOnGround() && land.getOilLeftOnGround() > 0,
                seaRefused + (seaSpare == null ? "; none" : String.format("; %,.1f against %,.1f", seaSpare[0], seaSpare[1])));
    }
}

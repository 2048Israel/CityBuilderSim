package ham.citybuildersim;

import ham.citybuildersim.sectors.RealEstate;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Predicate;

/**
 * The harness for GW1's staffing gate that counts the draw: a business orders a building only when the city could staff
 * Sector.MIN_STAFFABLE_TO_ORDER of its posts, and since GW1 that count is the spare workers and the workers migration would bring for
 * the order's own posts within Sector.GATE_DRAW_MONTHS (Migration.drawFor()). Not part of the game.
 *
 * WHY. GW0 (docs/studies/spec-staffing-gate.md) found about 15 refused orders a month in a young city while a fifth of its workers
 * were jobless: the gate asked what the city could staff today, and the workers a new works would draw were not there until it opened.
 * The gate now counts a year's draw. Each fixture below causes its case: a village with room whose spare workers are few, a city
 * whose crowding reads 0, a month struck on a pull, a town without its graduate bands, a refusal worded, a saved city and its twin, and
 * a played city counted ask by ask. Nothing here moves a premise to get green.
 *
 * What this has to prove:
 *   1. In a village with room the draw passes a works the spare workers alone refuse, and the drawn workers are the formula's.
 *   2. In a city whose crowding reads 0 the draw is nothing, the share is the spare's, and what the spare refuses is refused.
 *   3. The pulls turn the draw down, by the ratio of their products (WG2, queued next, adds a migration brake and will add its section here).
 *   4. The Shared Services Centre in a town without its graduate bands is refused by the share, not the band test; a band held closed
 *      draws nobody and is the band nobody can fill, as before.
 *   5. The hold's words name both counts.
 *   6. A loaded city's gate is its twin's, to the bit, in a city founded the default way and one founded the 1900 way.
 *   7. How often the draw opens an order in a played city, and that it never does in a month of no room.
 */
public class StaffingDrawCheck {

    static int fails = 0;
    static PrintStream out, quiet;

    /** The share of the works the village's spare workers alone fill: well under MIN_STAFFABLE_TO_ORDER, so only the draw can take it over the line. */
    static final double SPARE_SHARE = .40;

    /** The workforce as a share of the people: what Migration.drawFor() turns a draw of residents into workers by (workforce over population). */
    static final double WORKFORCE_SHARE = .5;

    /** The most workers a fixture searches for a share. */
    static final long MOST_WORKERS = 5_000;

    /** The workers of the town whose graduate bands are empty, half of them with a diploma: few enough that the draw, not the spare, fills the centre's diploma posts. */
    static final long TOWN_WORKERS = 100;

    /** The people Migration's month is struck on in the fixtures that put its crowding at 1 or at 0 (ArrivalMixCheck.draw()'s figures). */
    static final long HOUSEHOLD_PEOPLE = 4_000, MONTH_JOBS = 6_000, MONTH_CAPACITY = 12_000, ROOMY_DOORS = 9_000;

    /** The adult share Migration's month is struck on: 0 reads the equilibrium share, the one the fresh city's residents a job already use. */
    static final double EQUILIBRIUM_ADULTS = 0;

    /** Rent as a multiple of the target burden in section 3's dear month, and crime as a multiple of Canada's: affordabilityPull() 1/2, crimePull() 1/1.2. */
    static final double DEAR_RENT = 2, DEAR_CRIME = 3;

    /** Rent at ten times the target: the affordability pull is a tenth, and the draw with it. */
    static final double RUINOUS_RENT = 10;

    /** The months the twin cities are played before they are saved, and the months the played city is asked about. */
    static final int MONTHS_BEFORE_SAVE = 24, MONTHS_PLAYED = 120;

    static final int NONE = WageBand.NONE.ordinal();
    static final int DIPLOMA = WageBand.DIPLOMA.ordinal();
    static final int COLLEGE = WageBand.COLLEGE.ordinal();
    static final int UNIVERSITY = WageBand.UNIVERSITY.ordinal();

    /** Carried from section 1 to section 2: whether the village's order passed. */
    static boolean villagePassed;

    /** Carried from section 2 to section 5: the order the city with no room refused, and the works it was. */
    static Sector.Staffing noRoomRefusal;
    static String noRoomName;

    /* ------------------------------- the helpers ------------------------------- */

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-110s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** Equal within a part in a billion of the expected (exactly, where it is nothing). */
    static void near(String label, double actual, double expected) {
        double tolerance = Math.abs(expected) > 0 ? 1e-9 * Math.abs(expected) : 0;
        boolean ok = Math.abs(actual - expected) <= tolerance;
        if (!ok) fails++;
        System.out.printf("%-110s %s  (%.6f, expected %.6f)%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    static void quietly(Runnable r) {
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(out); }
    }

    static BuildingsTemplate template(Game g, String name) {
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName(name);
        if (t == null) throw new IllegalStateException("no building named " + name);
        return t;
    }

    /** A fresh city, currency and world pinned, land by fiat; no month played. */
    static Game city(Path root, String name) {
        Game g = new Game(new GameFiles(root.resolve(name), root.resolve(name + "-no-legacy")));
        quietly(() -> {
            g.run();
            g.getForeignAccounts().pinRate(1.0);
            g.getWorldEconomy().pin();
            g.getLandManager().setOwnedSqFt(50_000_000);
        });
        return g;
    }

    /** A village founded as given: roads, homes, shops, farms, bakeries, power and water (FittingCheck.village()), in a folder of the harness's own. */
    static Game town(Path root, String name, Founding founding) {
        Game g = new Game(new GameFiles(root.resolve(name), root.resolve(name + "-no-legacy")));
        quietly(() -> {
            g.newGame(founding);
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 400_000_000L);
            BuildingManager b = g.getBuildingManager();
            g.buildStack(b.getTemplateByName("Gravel Road"), 6, true);
            g.buildStack(b.getTemplateByName("House"), 400, true);
            g.buildStack(b.getTemplateByName("Small Grocery Store"), 4, true);
            g.buildStack(b.getTemplateByName("Mixed Farm"), 2, true);
            g.buildStack(b.getTemplateByName("Grain Farm"), 1, true);
            g.buildStack(b.getTemplateByName("Industrial Bakery"), 2, true);
            g.buildStack(b.getTemplateByName("Bakery"), 2, true);
            g.buildStack(b.getTemplateByName("Coal Power Plant"), 1, true);
            g.buildStack(b.getTemplateByName("Water Treatment Plant"), 1, true);
            MaterialsCheck.handWorkers(g);
        });
        return g;
    }

    /** One month, quietly. */
    static void month(Game g) {
        quietly(() -> g.simulateMonths(1));
    }

    /** One press of the clock, as the player's button makes it, quietly. */
    static void step(Game g) {
        quietly(g::toggleNextMonth);
    }

    /** The posts of a building, all bands. */
    static double posts(BuildingsTemplate t) {
        double p = 0;
        for (JobType job : JobType.values()) p += t.getJobs(job);
        return p;
    }

    /** The posts of a building in one wage band. */
    static double posts(BuildingsTemplate t, WageBand band) {
        double p = 0;
        for (JobType job : JobType.values()) if (WageBand.of(job) == band) p += t.getJobs(job);
        return p;
    }

    static double sum(double[] a) {
        double s = 0;
        for (double v : a) s += v;
        return s;
    }

    static boolean allZero(double[] a) {
        for (double v : a) if (v != 0) return false;
        return true;
    }

    static String pct(double share) {
        return String.format("%.0f%%", share * 100);
    }

    static String bands(double[] a) {
        return a.length < 4 ? "none" : String.format("none %.1f, diploma %.1f, college %.1f, university %.1f", a[NONE], a[DIPLOMA], a[COLLEGE], a[UNIVERSITY]);
    }

    /** The village's people and its workforce: this many workers, half of them with a diploma, WORKFORCE_SHARE of the people. */
    static void seat(PopulationManager people, long workers) {
        people.setPopulation(Math.round(workers / WORKFORCE_SHARE));
        people.restoreWorkforce(workers);
        people.restoreSkilledHeads(new double[] { 0, workers / 2.0, 0, 0 });
    }

    /** The fewest workers, seated, at which the works' staffing is as wanted; -1 where none up to MOST_WORKERS is (the village is left at MOST_WORKERS). */
    static long seatFor(PopulationManager people, Sector owner, BuildingsTemplate t, Predicate<Sector.Staffing> wanted) {
        for (long workers = 1; workers <= MOST_WORKERS; workers++) {
            seat(people, workers);
            if (wanted.test(owner.staffing(t))) return workers;
        }
        return -1;
    }

    /** The workers Migration.drawFor() is to bring for one of this works: the formula in the model's own constants and last-month scalars. */
    static double expectedDraw(Game g, BuildingsTemplate t) {
        Migration m = g.getMigration();
        PopulationManager people = g.getPopulationManager();
        double monthly = Math.max(0, Math.min(1, Migration.ARRIVAL_RATE * m.getLastCrowding()));
        double close = 1 - Math.pow(1 - monthly, Sector.GATE_DRAW_MONTHS);
        return Migration.JOB_WEIGHT * posts(t) * m.getLastResidentsPerJob() * m.getLastSeniorPull() * m.getLastAffordabilityPull()
                * m.getLastCrimePull() * close * people.getWorkforceForSave() / people.getPopulation();
    }

    static double pulls(Migration m) {
        return m.getLastSeniorPull() * m.getLastAffordabilityPull() * m.getLastCrimePull();
    }

    /** Migration's month struck on households with doors enough: its crowding reads 1, and the pulls are the ones set. */
    static void roomMonth(Migration mig) {
        FamilyModel households = new FamilyModel();
        PopulationCohorts some = new PopulationCohorts();
        some.migrate(HOUSEHOLD_PEOPLE);
        households.rebuild(some, new double[PayTier.values().length]);
        mig.monthlyNet(HOUSEHOLD_PEOPLE, MONTH_JOBS, MONTH_CAPACITY, ROOMY_DOORS, households, EQUILIBRIUM_ADULTS);
    }

    /** Migration's month struck on the same households and no door at all: the crowding floor, and its crowding reads 0. */
    static void noRoomMonth(Migration mig) {
        FamilyModel households = new FamilyModel();
        PopulationCohorts some = new PopulationCohorts();
        some.migrate(HOUSEHOLD_PEOPLE);
        households.rebuild(some, new double[PayTier.values().length]);
        mig.monthlyNet(HOUSEHOLD_PEOPLE, MONTH_JOBS, MONTH_CAPACITY, 0, households, EQUILIBRIUM_ADULTS);
    }

    public static void main(String[] args) throws Exception {
        out = System.out;
        quiet = new PrintStream(OutputStream.nullOutputStream());
        Path root = TempDirs.create("staffingdrawcheck");

        long workers = sectionOne(root);
        sectionTwo(root);
        sectionThree(root, workers);
        sectionFour(root);
        sectionFive(root);
        sectionSix(root);
        sectionSeven(root);

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails > 0 ? 1 : 0);
    }

    /* ============ 1. a village with room: the draw passes what the spare does not ============ */

    static long sectionOne(Path root) {
        System.out.println("--- 1. a village with room: the draw passes what the spare does not ---");
        Game g = city(root, "village");
        Migration mig = g.getMigration();
        PopulationManager people = g.getPopulationManager();
        BuildingsTemplate sawmill = template(g, "Sawmill");
        Sector owner = BuildCard.ownerOf(g, sawmill);

        assertTrue("fixture: the sawmill's posts are mostly unskilled and diploma",
                posts(sawmill, WageBand.NONE) + posts(sawmill, WageBand.DIPLOMA) > posts(sawmill) / 2);
        assertTrue("fixture: no month played, so crowding and the senior, rent and crime pulls all read 1",
                mig.getLastCrowding() == 1 && mig.getLastSeniorPull() == 1 && mig.getLastAffordabilityPull() == 1 && mig.getLastCrimePull() == 1);
        System.out.printf("   last month's residents a job %.3f, crowding %.2f, pulls %.2f%n",
                mig.getLastResidentsPerJob(), mig.getLastCrowding(), pulls(mig));

        long workers = seatFor(people, owner, sawmill, ask -> ask.shareNow >= SPARE_SHARE);
        assertTrue("fixture: a workforce was found whose spare workers fill " + pct(SPARE_SHARE) + " of the sawmill", workers > 0);
        Sector.Staffing st = owner.staffing(sawmill);
        double[] drawn = st.drawn();
        System.out.printf("   %d workers of %,d people; the sawmill's %.0f posts: %s staffable now, %s with %s; %.1f workers drawn (%s)%n",
                workers, people.getPopulation(), posts(sawmill), pct(st.shareNow), pct(st.share), Sector.Staffing.arrivalsWords(),
                sum(drawn), bands(drawn));
        assertTrue("fixture: the spare workers alone fall short of " + pct(Sector.MIN_STAFFABLE_TO_ORDER), st.shareNow < Sector.MIN_STAFFABLE_TO_ORDER);
        assertTrue("fixture: there are spare workers, so the order is not the draw's alone", st.shareNow > 0);
        assertTrue("with the draw the share reaches " + pct(Sector.MIN_STAFFABLE_TO_ORDER), st.share >= Sector.MIN_STAFFABLE_TO_ORDER);
        assertTrue("...so the order passes", st.passes());
        assertTrue("...with no band nobody can fill", st.unfillable == null);
        assertTrue("the draw brings workers", sum(drawn) > 0);
        near("the drawn workers are the formula's from the getters", sum(drawn), expectedDraw(g, sawmill));
        villagePassed = st.passes();
        return workers;
    }

    /* ============ 2. a city with no room: the draw refuses what the spare refuses ============ */

    static void sectionTwo(Path root) {
        System.out.println("\n--- 2. a city with no room: the draw refuses what the spare refuses ---");
        Game g = city(root, "crowded");
        Migration mig = g.getMigration();
        PopulationManager people = g.getPopulationManager();
        BuildingsTemplate sawmill = template(g, "Sawmill");
        Sector owner = BuildCard.ownerOf(g, sawmill);

        /*
         * HOW NO ROOM IS CAUSED: (a), the city's own state. The households of HOUSEHOLD_PEOPLE people are put into a city with no
         * home standing and two months are played, so the placement leaves them nowhere and Migration's crowdingFactor() reads 0 (the
         * first month's migration is struck before the households are rebuilt, the second's on them). monthlyNet() struck by hand on
         * the same households (b, noRoomMonth()) is used only in section 7, for a played city that has no month of no room of its own.
         */
        g.getCohorts().migrate(HOUSEHOLD_PEOPLE);
        month(g);
        month(g);
        // The spare as in the village (SPARE_SHARE of the sawmill), in this city's own posts: only the draw differs between the two.
        long workers = seatFor(people, owner, sawmill, ask -> ask.shareNow >= SPARE_SHARE);

        Sector.Staffing st = owner.staffing(sawmill);
        System.out.printf("   crowding caused by the city's own state: households with no door, two months played%n"
                        + "   %d workers of %,d people; crowding %.2f; the sawmill: %s staffable now, %s with %s; %.1f workers drawn%n",
                workers, people.getPopulation(), mig.getLastCrowding(), pct(st.shareNow), pct(st.share), Sector.Staffing.arrivalsWords(),
                sum(st.drawn()));
        assertTrue("fixture: Migration's crowding reads 0, from the households and the doors", mig.getLastCrowding() == 0);
        assertTrue("fixture: a workforce was found whose spare workers fill " + pct(SPARE_SHARE) + " of the sawmill, as in the village", workers > 0);
        assertTrue("fixture: the spare workers alone fall short of " + pct(Sector.MIN_STAFFABLE_TO_ORDER), st.shareNow < Sector.MIN_STAFFABLE_TO_ORDER);
        assertTrue("fixture: the same works passed in the village with room", villagePassed);
        assertTrue("every band's drawn workers are 0", allZero(st.drawn()));
        near("...as the formula has it", expectedDraw(g, sawmill), 0);
        assertTrue("the share is the spare's alone, exactly", Double.doubleToLongBits(st.share) == Double.doubleToLongBits(st.shareNow));
        assertTrue("a works the spare refuses is refused", !st.passes());
        noRoomRefusal = st;
        noRoomName = sawmill.getName();
    }

    /* ============ 3. the pulls turn the draw down ============ */

    /*
     * Main has no migration brake yet (a pull is the only thing that turns arrivals down): WG2, queued next, adds one, cutting the draw
     * as it cuts arrivals, and will add its section here beside the rent and crime of this one.
     */
    static void sectionThree(Path root, long workers) {
        System.out.println("\n--- 3. the pulls turn the draw down ---");
        Game g = city(root, "pulls");
        Migration mig = g.getMigration();
        PopulationManager people = g.getPopulationManager();
        BuildingsTemplate sawmill = template(g, "Sawmill");
        Sector owner = BuildCard.ownerOf(g, sawmill);
        seat(people, workers);

        roomMonth(mig);
        Sector.Staffing level = owner.staffing(sawmill);
        double drawnLevel = sum(level.drawn()), pullsLevel = pulls(mig), crowding = mig.getLastCrowding(), perJob = mig.getLastResidentsPerJob();
        System.out.printf("   a month with room, no pull: pulls %.3f, %.1f workers drawn, %s with the draw%n", pullsLevel, drawnLevel, pct(level.share));
        assertTrue("fixture: a month with room and no pull: crowding 1 and the pulls' product 1", crowding == 1 && pullsLevel == 1);
        assertTrue("fixture: the order passes on the draw, as in section 1", level.passes() && level.shareNow < Sector.MIN_STAFFABLE_TO_ORDER);
        near("fixture: the draw is the formula's", drawnLevel, expectedDraw(g, sawmill));

        // Dear rent: the affordability pull falls.
        mig.setRentBurden(RealEstate.TARGET_RENT_BURDEN * DEAR_RENT);
        roomMonth(mig);
        Sector.Staffing dear = owner.staffing(sawmill);
        double drawnDear = sum(dear.drawn()), dearPull = mig.getLastAffordabilityPull();
        System.out.printf("   rent at %.0fx the target: affordability pull %.3f, %.1f workers drawn, %s%n",
                DEAR_RENT, dearPull, drawnDear, pct(dear.share));
        assertTrue("fixture: dear rent cut the affordability pull", mig.getLastAffordabilityPull() < 1);
        assertTrue("fixture: nothing else moved: crowding, residents a job, the senior and crime pulls",
                mig.getLastCrowding() == crowding && mig.getLastResidentsPerJob() == perJob && mig.getLastSeniorPull() == 1 && mig.getLastCrimePull() == 1);
        near("the drawn total moved by exactly the ratio of the pull products (rent)", drawnDear / drawnLevel, pulls(mig) / pullsLevel);
        near("...and is the formula's", drawnDear, expectedDraw(g, sawmill));
        assertTrue("...and the share fell with it, or stayed", dear.share <= level.share);

        // Crime: rent back to the target, and the crime pull falls.
        mig.setRentBurden(0);
        mig.setCrimeVsCanada(DEAR_CRIME);
        roomMonth(mig);
        Sector.Staffing unsafe = owner.staffing(sawmill);
        double drawnUnsafe = sum(unsafe.drawn());
        System.out.printf("   crime at %.0fx Canada's: crime pull %.3f, %.1f workers drawn, %s%n",
                DEAR_CRIME, mig.getLastCrimePull(), drawnUnsafe, pct(unsafe.share));
        assertTrue("fixture: crime cut the crime pull and rent's is back to 1", mig.getLastCrimePull() < 1 && mig.getLastAffordabilityPull() == 1);
        assertTrue("fixture: nothing else moved: crowding and residents a job",
                mig.getLastCrowding() == crowding && mig.getLastResidentsPerJob() == perJob);
        near("the drawn total moved by exactly the ratio of the pull products (crime)", drawnUnsafe / drawnLevel, pulls(mig) / pullsLevel);
        near("...and is the formula's", drawnUnsafe, expectedDraw(g, sawmill));

        // A pull hard enough turns the order away that passed.
        mig.setCrimeVsCanada(0);
        mig.setRentBurden(RealEstate.TARGET_RENT_BURDEN * RUINOUS_RENT);
        roomMonth(mig);
        Sector.Staffing ruinous = owner.staffing(sawmill);
        System.out.printf("   rent at %.0fx the target: affordability pull %.3f, %.1f workers drawn, %s staffable now, %s with the draw%n",
                RUINOUS_RENT, mig.getLastAffordabilityPull(), sum(ruinous.drawn()), pct(ruinous.shareNow), pct(ruinous.share));
        assertTrue("fixture: the affordability pull is below the dear month's", mig.getLastAffordabilityPull() < dearPull);
        near("the draw is the formula's at the ruinous rent", sum(ruinous.drawn()), expectedDraw(g, sawmill));
        assertTrue("the spare is the same, so the order that passed on the draw is refused when the pull turns it down",
                ruinous.shareNow == level.shareNow && !ruinous.passes());
    }

    /* ============ 4. the Shared Services Centre in a town without its bands is still refused ============ */

    static void sectionFour(Path root) {
        System.out.println("\n--- 4. the Shared Services Centre in a town without its bands is still refused ---");
        Game g = city(root, "town");
        PopulationManager people = g.getPopulationManager();
        LabourMarket market = g.getLabourMarket();
        BuildingsTemplate ssc = template(g, "Shared Services Centre");
        Sector owner = BuildCard.ownerOf(g, ssc);
        seat(people, TOWN_WORKERS);

        Sector.Staffing st = owner.staffing(ssc);
        double[] drawn = st.drawn();
        double diplomaShare = posts(ssc, WageBand.DIPLOMA) / posts(ssc);
        System.out.printf("   %d workers, none with a college or university degree; the centre's %.0f posts: %s staffable now, %s with %s; "
                        + "%.1f workers drawn (%s); diploma posts are %s of it%n",
                TOWN_WORKERS, posts(ssc), pct(st.shareNow), pct(st.share), Sector.Staffing.arrivalsWords(), sum(drawn), bands(drawn), pct(diplomaShare));
        assertTrue("fixture: the town has no college and no university workers",
                people.workforceByBand()[COLLEGE] == 0 && people.workforceByBand()[UNIVERSITY] == 0);
        assertTrue("fixture: the graduate bands are at the going rate: the pull of neither is anything",
                Migration.reach(market.bandPremium(WageBand.COLLEGE)) == 0 && Migration.reach(market.bandPremium(WageBand.UNIVERSITY)) == 0);
        assertTrue("fixture: nobody, or under one worker, is drawn to the college or university band", drawn[COLLEGE] < 1 && drawn[UNIVERSITY] < 1);
        assertTrue("fixture: the draw is workers enough for the centre's diploma posts", sum(drawn) >= posts(ssc, WageBand.DIPLOMA));
        assertTrue("the draw raises the centre's share above the spare's alone", st.share > st.shareNow);
        assertTrue("the centre's share is under " + pct(Sector.MIN_STAFFABLE_TO_ORDER), st.share < Sector.MIN_STAFFABLE_TO_ORDER);
        assertTrue("...no more than its diploma posts', for the graduate posts get nobody", st.share <= diplomaShare + 1e-9);
        assertTrue("...so the centre is refused", !st.passes());
        assertTrue("...by the share, not the band test: migrants are admitted to every band, so no band is one nobody can fill", st.unfillable == null);

        // The band test is unchanged: a band held closed draws nobody and, with nobody here to fill it, is the band nobody can fill.
        Game held = city(root, "held");
        PopulationManager heldPeople = held.getPopulationManager();
        Migration heldMig = held.getMigration();
        LabourMarket heldMarket = held.getLabourMarket();
        BuildingsTemplate sawmill = template(held, "Sawmill");
        Sector heldOwner = BuildCard.ownerOf(held, sawmill);
        seat(heldPeople, TOWN_WORKERS);
        // Every band's wage bid up over 200 months (ArrivalMixCheck.bidUp()), so the graduate bands have a premium to draw by.
        int bandCount = WageBand.values().length, jobCount = JobType.values().length;
        double[] bandPosts = new double[bandCount], bandSupply = new double[bandCount], licensed = new double[jobCount];
        long[] jobPosts = new long[jobCount];
        Arrays.fill(bandPosts, 1000);
        Arrays.fill(bandSupply, 250);
        Arrays.fill(jobPosts, 1000);
        for (int m = 0; m < 200; m++) heldMarket.advanceMonth(bandPosts, bandSupply, jobPosts, licensed);

        Sector.Staffing open = heldOwner.staffing(sawmill);
        System.out.printf("   a sawmill, university arrivals open and bid for (reach %.2f): %.2f university workers drawn, nobody can fill: %s%n",
                Migration.reach(heldMarket.bandPremium(WageBand.UNIVERSITY)), open.drawn()[UNIVERSITY], open.unfillable);
        assertTrue("fixture: the sawmill has university posts and nobody here holds a degree",
                posts(sawmill, WageBand.UNIVERSITY) > 0 && heldPeople.workforceByBand()[UNIVERSITY] == 0);
        assertTrue("fixture: the university band has a premium to draw by", Migration.reach(heldMarket.bandPremium(WageBand.UNIVERSITY)) > 0);
        assertTrue("fixture: with university arrivals open the draw brings university workers and no band is one nobody can fill",
                open.drawn()[UNIVERSITY] > 0 && open.unfillable == null);
        heldMig.holdArrivals(WageBand.UNIVERSITY);
        Sector.Staffing shut = heldOwner.staffing(sawmill);
        System.out.printf("   ...and held closed: %.2f university workers drawn, nobody can fill: %s - \"%s\"%n",
                shut.drawn()[UNIVERSITY], shut.unfillable, shut.why(sawmill.getName()));
        assertTrue("held closed, the band draws nobody", shut.drawn()[UNIVERSITY] == 0);
        assertTrue("...and with no spare, nobody above and no migrants, the university band is the one nobody can fill", shut.unfillable == WageBand.UNIVERSITY);
        assertTrue("...and the order is refused for it", !shut.passes());
    }

    /* ============ 5. the hold's words name both counts ============ */

    static void sectionFive(Path root) {
        System.out.println("\n--- 5. the hold's words name both counts ---");
        Game g = city(root, "words");
        PopulationManager people = g.getPopulationManager();
        BuildingsTemplate sawmill = template(g, "Sawmill");
        Sector owner = BuildCard.ownerOf(g, sawmill);
        long workers = seatFor(people, owner, sawmill, ask -> ask.shareNow >= SPARE_SHARE / 4 && !ask.passes()
                && Math.round(ask.shareNow * 100) < Math.round(ask.share * 100));
        Sector.Staffing st = owner.staffing(sawmill);
        String why = st.why(sawmill.getName());
        System.out.println("   a workforce of " + workers + ": \"" + why + "\"");
        assertTrue("fixture: a refused order whose two counts round apart",
                workers > 0 && !st.passes() && Math.round(st.shareNow * 100) < Math.round(st.share * 100));
        String wants = "it wants " + pct(Sector.MIN_STAFFABLE_TO_ORDER);
        assertTrue("the words say what the city could staff now: \"could staff " + pct(st.shareNow) + " of a Sawmill now\"",
                why.contains("could staff " + pct(st.shareNow) + " of a Sawmill now"));
        assertTrue("...and with the draw: \"and " + pct(st.share) + " with " + Sector.Staffing.arrivalsWords() + "\"",
                why.contains(" and " + pct(st.share) + " with " + Sector.Staffing.arrivalsWords()));
        assertTrue("...and what the gate wants", why.contains(wants));

        Sector.Staffing none = noRoomRefusal;
        String noRoomWhy = none.why(noRoomName);
        System.out.println("   the city with no room: \"" + noRoomWhy + "\"");
        assertTrue("fixture: the order the city with no room refused rounds to one count", Math.round(none.shareNow * 100) == Math.round(none.share * 100));
        assertTrue("where the draw is nothing the words say \"now or with " + Sector.Staffing.arrivalsWords() + "\"",
                noRoomWhy.contains("now or with " + Sector.Staffing.arrivalsWords()));
        assertTrue("...with the one count: \"could staff " + pct(none.share) + " of a Sawmill\"", noRoomWhy.contains("could staff " + pct(none.share) + " of a Sawmill"));
        assertTrue("...and not the two", !noRoomWhy.contains(" now and "));
    }

    /* ============ 6. a loaded city's gate is its twin's ============ */

    static void sectionSix(Path root) {
        System.out.println("\n--- 6. a loaded city's gate is its twin's ---");
        for (boolean the1900Way : new boolean[] { false, true }) {
            String name = the1900Way ? "twin-1900" : "twin-default";
            Game g = town(root, name, the1900Way ? Founding.defaults().withUnfitted(true) : Founding.defaults());
            assertTrue("fixture (" + name + "): the city is founded the " + (the1900Way ? "1900" : "default") + " way", g.foundedUnfitted() == the1900Way);
            for (int i = 0; i < MONTHS_BEFORE_SAVE; i++) step(g);
            Migration mig = g.getMigration();
            System.out.printf("   %s after %d months: crowding %.3f, pulls %.3f, residents a job %.3f (the start's %.3f)%n",
                    name, MONTHS_BEFORE_SAVE, mig.getLastCrowding(), pulls(mig), mig.getLastResidentsPerJob(), Migration.residentsPerJob(0));
            assertTrue("fixture (" + name + "): the last month's scalars are no longer their defaults",
                    mig.getLastCrowding() != 1 || pulls(mig) != 1 || mig.getLastResidentsPerJob() != Migration.residentsPerJob(0));

            GameFiles.Result[] saved = new GameFiles.Result[1];
            quietly(() -> saved[0] = g.saveGame(10, name));
            assertTrue("fixture (" + name + "): the city saved to slot 10 of its own folder", saved[0].ok);
            Game twin = new Game(new GameFiles(root.resolve(name), root.resolve(name + "-no-legacy")));
            quietly(() -> twin.loadGameSave(10));
            assertTrue("fixture (" + name + "): the twin loaded, in the same month and founded the same way",
                    twin.getLoadFailure() == null && twin.getMonth() == g.getMonth() && twin.foundedUnfitted() == the1900Way);
            if (the1900Way) {
                assertTrue("fixture (" + name + "): the original holds the year's mix and the twin's Migration holds none (it is never saved)",
                        mig.getYearMix() != null && twin.getMigration().getYearMix() == null);
            }

            if (the1900Way) {
                // The comparison is sensitive to the mix: a gate that read Migration's own (null in the twin) would draw otherwise.
                BuildingsTemplate sawmill = template(g, "Sawmill");
                Sector.Staffing sm = BuildCard.ownerOf(g, sawmill).staffing(sawmill);
                double[] withMix = mig.drawFor(sm.wanted(), g.getLabourMarket(), g.getPopulationManager(), Sector.GATE_DRAW_MONTHS, ArrivalMix.at(g.getMonth()));
                double[] withNone = mig.drawFor(sm.wanted(), g.getLabourMarket(), g.getPopulationManager(), Sector.GATE_DRAW_MONTHS, null);
                System.out.printf("   a sawmill's draw with the year's mix: %s; with none: %s%n", bands(withMix), bands(withNone));
                assertTrue("fixture (" + name + "): the year's mix changes the draw of a sawmill", !Arrays.equals(withMix, withNone));
                assertTrue("...and the gate draws on the year's", Arrays.equals(withMix, sm.drawn()));
            }

            int asked = 0, drawing = 0, differ = 0;
            String first = null;
            for (int i = 0; i < g.getSectors().all().size(); i++) {
                Sector s = g.getSectors().all().get(i), twinSector = twin.getSectors().all().get(i);
                for (BuildingsTemplate t : g.getBuildingManager().getTemplatesBySector(s.key())) {
                    BuildingsTemplate twinTemplate = twin.getBuildingManager().getTemplateByName(t.getName());
                    Sector.Staffing a = s.staffing(t), b = twinSector.staffing(twinTemplate);
                    asked++;
                    if (sum(a.drawn()) > 0) drawing++;
                    boolean same = Double.doubleToLongBits(a.share) == Double.doubleToLongBits(b.share)
                            && Double.doubleToLongBits(a.shareNow) == Double.doubleToLongBits(b.shareNow)
                            && Arrays.equals(a.drawn(), b.drawn());
                    if (!same) {
                        differ++;
                        if (first == null) first = t.getName() + ": share " + a.share + " / " + b.share + ", now " + a.shareNow + " / " + b.shareNow
                                + ", drawn " + sum(a.drawn()) + " / " + sum(b.drawn());
                    }
                }
            }
            System.out.printf("   %s: %,d questions asked, %,d with workers drawn, %,d differ from the twin's%s%n",
                    name, asked, drawing, differ, first == null ? "" : " - first " + first);
            assertTrue("questions were asked (" + name + ")", asked > 0);
            assertTrue("fixture (" + name + "): some of the questions draw workers, so the comparison is not of zeros", drawing > 0);
            assertTrue("the twin's share, share now and drawn workers are the original's, to the bit, for every sector and template (" + name + ")", differ == 0);
        }
    }

    /* ============ 7. how often the draw opens an order in a played city ============ */

    static void sectionSeven(Path root) {
        System.out.println("\n--- 7. how often the draw opens an order in a played city ---");
        Game g = town(root, "played", Founding.defaults());
        int asks = 0, opened = 0, spareAlone = 0, refused = 0, noRoomMonths = 0, openedWithNoRoom = 0;
        for (int m = 0; m < MONTHS_PLAYED; m++) {
            month(g);
            boolean noRoom = g.getMigration().getLastCrowding() == 0;
            if (noRoom) noRoomMonths++;
            for (Sector s : g.getSectors().all()) {
                for (BuildingsTemplate t : g.getBuildingManager().getTemplatesBySector(s.key())) {
                    Sector.Staffing st = s.staffing(t);
                    if (st.template() == null) continue;      // the always-staffable answer: no posts, nobody to ask
                    asks++;
                    boolean drawOpened = st.shareNow < Sector.MIN_STAFFABLE_TO_ORDER && st.passes();
                    if (drawOpened) opened++;
                    if (st.shareNow >= Sector.MIN_STAFFABLE_TO_ORDER) spareAlone++;
                    if (!st.passes()) refused++;
                    if (noRoom && drawOpened) openedWithNoRoom++;
                }
            }
        }
        System.out.printf("   %d months: %,d asks; %,d opened by the draw, %,d where the spare alone passes, %,d refused; "
                        + "%d months with crowding 0, %d asks the draw opened in them%n",
                MONTHS_PLAYED, asks, opened, spareAlone, refused, noRoomMonths, openedWithNoRoom);
        assertTrue("fixture: the played city was asked questions", asks > 0);
        assertTrue("the draw opened at least one ask", opened > 0);
        assertTrue("none opened in a month of no room", openedWithNoRoom == 0);

        // The months of the played city may have none with crowding 0, so one is struck here: every sector's and template's ask, in a month of no room.
        noRoomMonth(g.getMigration());
        int spareRefused = 0, openedNow = 0;
        for (Sector s : g.getSectors().all()) {
            for (BuildingsTemplate t : g.getBuildingManager().getTemplatesBySector(s.key())) {
                Sector.Staffing st = s.staffing(t);
                if (st.template() == null) continue;
                if (st.shareNow < Sector.MIN_STAFFABLE_TO_ORDER) spareRefused++;
                if (st.shareNow < Sector.MIN_STAFFABLE_TO_ORDER && st.passes()) openedNow++;
            }
        }
        System.out.printf("   one more month struck on crowding %.2f: %,d asks the spare refuses, %d of them opened by the draw%n",
                g.getMigration().getLastCrowding(), spareRefused, openedNow);
        assertTrue("fixture: the last month struck reads no room", g.getMigration().getLastCrowding() == 0);
        assertTrue("fixture: there are asks the spare refuses in it", spareRefused > 0);
        assertTrue("none of them is opened by the draw in a month of no room", openedNow == 0);
    }
}

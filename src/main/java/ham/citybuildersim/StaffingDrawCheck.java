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
 * WHAT THE DRAW COUNTS (Migration.drawFor(), after the gate's review): only the part of migration's gap that the order makes positive
 * (max(0, gap + the order's addition to the target) - max(0, gap), the gap being the target less the population), closed over the
 * window, and no more than the doors last month left free; at the going wage, so no band premium is read (the premium is priced on the
 * posts the gate refused, Game.repriceLabour(), and a gate that read it would let a refused order's own posts raise the wage that
 * draws the workers that pass it). It first counted the order's whole addition whatever the gap and the room, so it drew where
 * migration brings nobody (a target at or below the people; YearBookCheck's city had 7,738 posts against a labour force of 6,326 at month 60).
 *
 * What this has to prove:
 *   1. In a village with room (the gap positive, the doors many) the draw passes a works the spare workers alone refuse, and the drawn
 *      workers are the formula's.
 *   2. In a city whose crowding reads 0 the draw is nothing, the share is the spare's, and what the spare refuses is refused.
 *   3. The pulls turn the draw down, by the ratio of their products (the jobs' pulls: the rent's and the crime's; WG2's brake is section 10).
 *   4. The Shared Services Centre in a town without its graduate bands is refused by the share, not the band test; a band held closed
 *      draws nobody and is the band nobody can fill, as before; and a college wage bid up draws the gate nobody more, to the bit.
 *   5. The hold's words name both counts.
 *   6. A loaded city's gate is its twin's, to the bit, in a city founded the default way and one founded the 1900 way, and the last
 *      month's room it reads rides the save.
 *   7. How often the draw opens an order in a played city, and that it never does in a month of no room.
 *   8. The gap's positive part: where the population stands above migration's target even with the order, the draw is nothing, to the
 *      bit; where the order takes the gap from negative to positive, it is that positive part's, and less than the whole addition's.
 *   9. Within the room: where the doors last month left free are fewer than the draw would be, the draw is the room left.
 *  10. The brake and the pull rule of WG2 turn the draw down as they turn arrivals: full senior care draws the same workers as none, to
 *      the bit; the draw is the unbraked draw x Migration.arrivalBrake() of the village's out-of-work share, the brake acting from
 *      Migration.BRAKE_FROM_MONTH (the villages are that old); and past Migration.ARRIVALS_STOP_OUT_OF_WORK it is nothing, and the
 *      order the village passed is refused. A village younger than BRAKE_FROM_MONTH draws the unbraked draw whole, past the stop too.
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

    /** The spare workers of the city that has no census (section 9): few, so that with the workers it employs its people are fewer than the centre's addition. */
    static final long NO_CENSUS_SPARE = 4;

    /** The people Migration's month is struck on in the fixtures that put its crowding at 1 or at 0 (ArrivalMixCheck.draw()'s figures). */
    static final long HOUSEHOLD_PEOPLE = 4_000, MONTH_JOBS = 6_000, MONTH_CAPACITY = 12_000, ROOMY_DOORS = 9_000;

    /** The household sizes a month's door census holds in the fixtures that give it one (ROOMY_DOORS split evenly over 1 to this). */
    static final int CENSUS_SIZES = 6;

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

    /**
     * The workers the village already employs, in unskilled posts, beside {@code spare} workers it has no post for: so few of its labour
     * force are out of work that the share stays under Migration.HEALTHY_OUT_OF_WORK, where migration's brake (WG2) is whole. A village of
     * nothing but spare workers is all out of work and the brake stops its draw; the spare workers are the village's jobless, and a village
     * whose draw counts is one whose jobless are few.
     */
    static long employedBeside(long spare) {
        return (long) Math.ceil(spare * (1 - Migration.HEALTHY_OUT_OF_WORK) / Migration.HEALTHY_OUT_OF_WORK) + 1;
    }

    /** The village as seat() has it, but employing as many beside its spare workers as leave this share of its labour force out of work (to a person). */
    static void seatOutOfWork(PopulationManager people, long workers, double share) {
        seatWith(people, workers, Math.round(workers * (1 - share) / share));
    }

    /**
     * The village's people and its workforce: this many spare workers, half of them with a diploma, beside the workers it employs in posts
     * of its own (employedBeside()), the labour force WORKFORCE_SHARE of the people. The spare workers of each band, and so the share the
     * spare fills of a works, are the same as with no employed base; what the base adds is the village's out-of-work share, under the
     * healthy rate, so the brake reads 1.
     */
    static void seat(PopulationManager people, long workers) {
        seatWith(people, workers, employedBeside(workers));
    }

    /** As seat(), employing this many beside the spare workers: the village's out-of-work share is the spare over the two. */
    static void seatWith(PopulationManager people, long workers, long employed) {
        people.setPopulation(Math.round((workers + employed) / WORKFORCE_SHARE));
        people.restoreWorkforce(workers + employed);
        people.restoreSkilledHeads(new double[] { 0, workers / 2.0, 0, 0 });
        long[] posts = new long[JobType.values().length];
        posts[JobType.NO_DIPLOMA.ordinal()] = employed;
        people.updateJobs(posts);
    }

    /** The fewest workers, seated, at which the works' staffing is as wanted; -1 where none up to MOST_WORKERS is (the village is left at MOST_WORKERS). */
    static long seatFor(PopulationManager people, Sector owner, BuildingsTemplate t, Predicate<Sector.Staffing> wanted) {
        for (long workers = 1; workers <= MOST_WORKERS; workers++) {
            seat(people, workers);
            if (wanted.test(owner.staffing(t))) return workers;
        }
        return -1;
    }

    /** The order's addition to migration's target, in people: JOB_WEIGHT x posts x last month's residents a job x the pulls on the jobs' draw. */
    static double addition(Game g, double posts) {
        Migration m = g.getMigration();
        return Migration.JOB_WEIGHT * posts * m.getLastResidentsPerJob() * pulls(m);
    }

    /** The gap migration's target stands over the population now, in people (below 0 where the population is the larger). */
    static double gapNow(Game g) {
        return g.getMigration().getLastTarget() - g.getPopulationManager().getPopulation();
    }

    /** The share of a gap that GATE_DRAW_MONTHS months close, at last month's crowding. */
    static double closed(Migration m) {
        double monthly = Math.max(0, Math.min(1, Migration.ARRIVAL_RATE * m.getLastCrowding()));
        return 1 - Math.pow(1 - monthly, Sector.GATE_DRAW_MONTHS);
    }

    /** The people an order of this many posts would draw over the window before the room: the part of the gap the order makes positive, closed. */
    static double uncappedPeople(Game g, double posts) {
        double gap = gapNow(g);
        double orderGap = Math.max(0, gap + addition(g, posts)) - Math.max(0, gap);
        return orderGap * closed(g.getMigration());
    }

    /** The people the doors last month left free after its own arrivals; unbounded where the city has no census to place them against (lastRoom NaN). */
    static double roomLeft(Migration m) {
        return Double.isNaN(m.getLastRoom()) ? Double.POSITIVE_INFINITY : Math.max(0, m.getLastRoom() - m.getLastArrivals());
    }

    /** The people Migration.drawFor() is to count for an order of this many posts: the uncapped draw, within the room, cut by the brake (WG2). */
    static double expectedPeople(Game g, double posts) {
        return Math.min(uncappedPeople(g, posts), roomLeft(g.getMigration())) * brake(g);
    }

    /** The workers Migration.drawFor() is to bring for one of this works: the formula in the model's own constants and last-month scalars. */
    static double expectedDraw(Game g, BuildingsTemplate t) {
        PopulationManager people = g.getPopulationManager();
        return expectedPeople(g, posts(t)) * people.getWorkforceForSave() / people.getPopulation();
    }

    /** The workers a number of drawn people come to in this city: the people over the population, times the workforce. */
    static double workersOf(Game g, double people) {
        return people * g.getPopulationManager().getWorkforceForSave() / g.getPopulationManager().getPopulation();
    }

    /** The pulls on the jobs' draw: the rent's and the crime's, never above 1; senior care lifts the homes' draw only (WG2). */
    static double pulls(Migration m) {
        return m.getLastJobsPull();
    }

    /** The share of the arrivals migration's brake lets through in this city now: 1 at and under the healthy out-of-work rate, 0 at and past the stop (WG2). */
    static double brake(Game g) {
        return g.getMigration().arrivalBrakeNow(g.getPopulationManager().getUnemploymentRate());
    }

    /** Makes this Migration an old city's: Migration.BRAKE_FROM_MONTH months of the same steady wage recorded, so its brake acts and no tier declines (WG2). */
    static void makeOld(Migration mig) {
        double[] steady = new double[PayTier.values().length];
        steady[PayTier.UNSKILLED.ordinal()] = 1000;
        for (int m = 0; m < Migration.BRAKE_FROM_MONTH; m++) mig.recordWages(steady);
    }

    /**
     * A fixture of the sections that read the draw as the order's whole addition: the gap is positive without the order, the room does
     * not bind, and the village is out of work under the healthy rate so the brake is whole (WG2).
     */
    static void drawIsClear(String when, Game g, BuildingsTemplate t) {
        assertTrue("fixture (" + when + "): the gap is positive without the order, the room does not bind and the brake is whole, so the draw is the order's whole addition, closed",
                gapNow(g) > 0 && roomLeft(g.getMigration()) > uncappedPeople(g, posts(t))
                        && g.getPopulationManager().getUnemploymentRate() <= Migration.HEALTHY_OUT_OF_WORK && brake(g) == 1);
    }

    /** Whether every drawn figure is positive zero, to the bit. */
    static boolean zeroToTheBit(double[] a) {
        for (double v : a) if (Double.doubleToLongBits(v) != 0L) return false;
        return true;
    }

    /** One order a city could ask about: its sector, the building and its posts. */
    static final class Ask {
        final Sector sector;
        final BuildingsTemplate template;
        final double posts;

        Ask(Sector sector, BuildingsTemplate template, double posts) {
            this.sector = sector;
            this.template = template;
            this.posts = posts;
        }

        Sector.Staffing staffing() { return sector.staffing(template); }
    }

    /** Every building of the city that has posts, with its sector: the orders the gate is asked about. */
    static java.util.List<Ask> asksOf(Game g) {
        java.util.List<Ask> asks = new java.util.ArrayList<>();
        for (Sector s : g.getSectors().all()) {
            for (BuildingsTemplate t : g.getBuildingManager().getTemplatesBySector(s.key())) {
                double p = posts(t);
                if (p > 0) asks.add(new Ask(s, t, p));
            }
        }
        return asks;
    }

    /**
     * Migration's month struck on households with doors enough: its crowding reads 1, and the pulls are the ones set. The doors are a
     * census of ROOMY_DOORS homes spread over the sizes 1 to CENSUS_SIZES (Migration.setDoors(), as Game hands it), so the month has a
     * room to cut arrivals by and the room is a number, not the lack of a census.
     */
    static void roomMonth(Migration mig) {
        roomMonth(mig, 0);
    }

    /** ...with this share of the seniors who need a place in senior care placed (1 is full coverage: the senior care pull at its most). */
    static void roomMonth(Migration mig, double seniorCoverage) {
        FamilyModel households = new FamilyModel();
        PopulationCohorts some = new PopulationCohorts();
        some.migrate(HOUSEHOLD_PEOPLE);
        households.rebuild(some, new double[PayTier.values().length]);
        long[] census = new long[CENSUS_SIZES + 1];
        for (int size = 1; size <= CENSUS_SIZES; size++) census[size] = ROOMY_DOORS / CENSUS_SIZES;
        mig.setDoors(census);
        mig.monthlyNet(HOUSEHOLD_PEOPLE, MONTH_JOBS, MONTH_CAPACITY, ROOMY_DOORS, households, EQUILIBRIUM_ADULTS, seniorCoverage);
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
        sectionEight(root);
        sectionNine(root);
        sectionTen(root, workers);

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
        // A month with room struck by hand (roomMonth()): crowding 1, a target well over the village's few people so the gap is positive,
        // and doors enough that the room is no bound on what the order draws.
        roomMonth(mig);
        assertTrue("fixture: a month with room was struck: crowding and the senior, rent and crime pulls all read 1",
                mig.getLastCrowding() == 1 && mig.getLastSeniorPull() == 1 && mig.getLastAffordabilityPull() == 1 && mig.getLastCrimePull() == 1);
        System.out.printf("   last month's residents a job %.3f, crowding %.2f, pulls %.2f; target %,.0f, room %,.0f, arrivals %,.1f%n",
                mig.getLastResidentsPerJob(), mig.getLastCrowding(), pulls(mig), mig.getLastTarget(), mig.getLastRoom(), mig.getLastArrivals());

        long workers = seatFor(people, owner, sawmill, ask -> ask.shareNow >= SPARE_SHARE);
        assertTrue("fixture: a workforce was found whose spare workers fill " + pct(SPARE_SHARE) + " of the sawmill", workers > 0);
        Sector.Staffing st = owner.staffing(sawmill);
        double[] drawn = st.drawn();
        System.out.printf("   %d workers of %,d people; the sawmill's %.0f posts: %s staffable now, %s with %s; %.1f workers drawn (%s)%n",
                workers, people.getPopulation(), posts(sawmill), pct(st.shareNow), pct(st.share), Sector.Staffing.arrivalsWords(),
                sum(drawn), bands(drawn));
        assertTrue("fixture: the spare workers alone fall short of " + pct(Sector.MIN_STAFFABLE_TO_ORDER), st.shareNow < Sector.MIN_STAFFABLE_TO_ORDER);
        assertTrue("fixture: there are spare workers, so the order is not the draw's alone", st.shareNow > 0);
        System.out.printf("   the gap now %,.0f people, the sawmill's addition %,.1f, closed %s over the window: %,.1f people; the room left %,.0f%n",
                gapNow(g), addition(g, posts(sawmill)), pct(closed(mig)), uncappedPeople(g, posts(sawmill)), roomLeft(mig));
        assertTrue("fixture: the gap is positive without the order: migration's target stands over the village's people", gapNow(g) > 0);
        assertTrue("fixture: the room does not bind: the doors left after last month's arrivals take more than the order's draw",
                roomLeft(mig) > uncappedPeople(g, posts(sawmill)));
        assertTrue("with the draw the share reaches " + pct(Sector.MIN_STAFFABLE_TO_ORDER), st.share >= Sector.MIN_STAFFABLE_TO_ORDER);
        assertTrue("...so the order passes", st.passes());
        assertTrue("...with no band nobody can fill", st.unfillable == null);
        assertTrue("the draw brings workers", sum(drawn) > 0);
        near("the drawn workers are the formula's from the getters: the order's addition (the gap is positive, so all of it), closed, in workers",
                sum(drawn), workersOf(g, addition(g, posts(sawmill)) * closed(mig)));
        near("...and the same from the gap's positive part, within the room", sum(drawn), expectedDraw(g, sawmill));
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
     * The pulls on the jobs' draw are the rent's and the crime's; senior care lifts the homes' draw only, since WG2 (section 10). The brake
     * WG2 added turns the draw down as it turns arrivals down: it is whole in every village of these sections, whose out-of-work share is
     * kept under the healthy rate (seat()), and section 10 is where it is not.
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
        drawIsClear("no pull", g, sawmill);
        double drawnLevel = sum(level.drawn()), pullsLevel = pulls(mig), crowding = mig.getLastCrowding(), perJob = mig.getLastResidentsPerJob();
        System.out.printf("   a month with room, no pull: pulls %.3f, %.1f workers drawn, %s with the draw%n", pullsLevel, drawnLevel, pct(level.share));
        assertTrue("fixture: a month with room and no pull: crowding 1 and the pulls' product 1", crowding == 1 && pullsLevel == 1);
        assertTrue("fixture: the order passes on the draw, as in section 1", level.passes() && level.shareNow < Sector.MIN_STAFFABLE_TO_ORDER);
        near("fixture: the draw is the formula's", drawnLevel, expectedDraw(g, sawmill));

        // Dear rent: the affordability pull falls.
        mig.setRentBurden(RealEstate.TARGET_RENT_BURDEN * DEAR_RENT);
        roomMonth(mig);
        Sector.Staffing dear = owner.staffing(sawmill);
        drawIsClear("dear rent", g, sawmill);
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
        drawIsClear("crime", g, sawmill);
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
        drawIsClear("ruinous rent", g, sawmill);
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
        // A month with room, as in the village: the town employs most of its workers (seat()), so its people are many beside the centre's
        // addition and only a target over them leaves the gap positive for the draw to close.
        roomMonth(g.getMigration());

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
        // A month with room struck by hand (roomMonth()), so the gap is positive and the sawmill's draw is not nothing.
        roomMonth(heldMig);
        // Every band's wage bid up over 200 months (ArrivalMixCheck.bidUp()), and every profession's licence with it: the professions'
        // licence premiums are what the gate's draw still reads (WantedPosts does not price licences); the bands' are not read at all.
        int bandCount = WageBand.values().length, jobCount = JobType.values().length;
        double[] bandPosts = new double[bandCount], bandSupply = new double[bandCount], licensed = new double[jobCount];
        long[] jobPosts = new long[jobCount];
        Arrays.fill(bandPosts, 1000);
        Arrays.fill(bandSupply, 250);
        Arrays.fill(jobPosts, 1000);
        for (int m = 0; m < 200; m++) heldMarket.advanceMonth(bandPosts, bandSupply, jobPosts, licensed);

        double bestLicence = 0;
        for (EducationType type : EducationType.values()) {
            JobType job = type.isProfessional() ? type.licenses() : null;
            if (job != null) bestLicence = Math.max(bestLicence, Migration.reach(heldMarket.licencePremium(job)));
        }
        Sector.Staffing open = heldOwner.staffing(sawmill);
        System.out.printf("   a sawmill, university arrivals open and the professions' licences bid for (reach %.2f): %.2f university workers drawn, nobody can fill: %s%n",
                bestLicence, open.drawn()[UNIVERSITY], open.unfillable);
        assertTrue("fixture: the sawmill has university posts and nobody here holds a degree",
                posts(sawmill, WageBand.UNIVERSITY) > 0 && heldPeople.workforceByBand()[UNIVERSITY] == 0);
        assertTrue("fixture: a profession's licence has a premium to draw by", bestLicence > 0);
        drawIsClear("held", held, sawmill);
        assertTrue("fixture: with university arrivals open the draw brings university workers, through the licences, and no band is one nobody can fill",
                open.drawn()[UNIVERSITY] > 0 && open.unfillable == null);
        heldMig.holdArrivals(WageBand.UNIVERSITY);
        Sector.Staffing shut = heldOwner.staffing(sawmill);
        System.out.printf("   ...and held closed: %.2f university workers drawn, nobody can fill: %s - \"%s\"%n",
                shut.drawn()[UNIVERSITY], shut.unfillable, shut.why(sawmill.getName()));
        assertTrue("held closed, the band draws nobody", shut.drawn()[UNIVERSITY] == 0);
        assertTrue("...and with no spare, nobody above and no migrants, the university band is the one nobody can fill", shut.unfillable == WageBand.UNIVERSITY);
        assertTrue("...and the order is refused for it", !shut.passes());

        // THE GATE DOES NOT READ A BAND'S PREMIUM. The premium is priced on the posts the gate refused (Game.repriceLabour(), WantedPosts),
        // so a draw that read it would let a refused order's own posts raise the wage that draws the workers that pass it. The college
        // band alone is bid up here (the two-argument advanceMonth() prices the bands only, so no licence moves), and the draw is the same.
        Game dear = city(root, "dear-college");
        PopulationManager dearPeople = dear.getPopulationManager();
        Migration dearMig = dear.getMigration();
        LabourMarket dearMarket = dear.getLabourMarket();
        BuildingsTemplate works = template(dear, "Sawmill");
        Sector dearOwner = BuildCard.ownerOf(dear, works);
        seat(dearPeople, TOWN_WORKERS);
        roomMonth(dearMig);
        assertTrue("fixture: the works has college posts", posts(works, WageBand.COLLEGE) > 0);
        drawIsClear("a college wage at the going rate", dear, works);
        Sector.Staffing atGoing = dearOwner.staffing(works);
        double[] licenceBefore = new double[jobCount];
        for (JobType job : JobType.values()) licenceBefore[job.ordinal()] = dearMarket.licencePremium(job);
        double collegeBefore = Migration.reach(dearMarket.bandPremium(WageBand.COLLEGE));
        double[] collegePosts = new double[bandCount], collegeSupply = new double[bandCount];
        Arrays.fill(collegePosts, 250);
        Arrays.fill(collegeSupply, 250);
        collegePosts[COLLEGE] = 1000;
        for (int m = 0; m < 200; m++) dearMarket.advanceMonth(collegePosts, collegeSupply);
        Sector.Staffing atPremium = dearOwner.staffing(works);
        boolean licencesStood = true;
        for (JobType job : JobType.values()) licencesStood &= dearMarket.licencePremium(job) == licenceBefore[job.ordinal()];
        System.out.printf("   a sawmill, the college wage at the going rate (reach %.2f) and bid up (reach %.2f): %.2f workers drawn, then %.2f (%s)%n",
                collegeBefore, Migration.reach(dearMarket.bandPremium(WageBand.COLLEGE)), sum(atGoing.drawn()), sum(atPremium.drawn()), bands(atPremium.drawn()));
        assertTrue("fixture: the college wage was at the going rate and is now over it", collegeBefore == 0 && Migration.reach(dearMarket.bandPremium(WageBand.COLLEGE)) > 0);
        assertTrue("fixture: no other band's premium moved with it, and no profession's licence", licencesStood
                && Migration.reach(dearMarket.bandPremium(WageBand.NONE)) == 0 && Migration.reach(dearMarket.bandPremium(WageBand.DIPLOMA)) == 0
                && Migration.reach(dearMarket.bandPremium(WageBand.UNIVERSITY)) == 0);
        assertTrue("fixture: the draw is workers, so the comparison is not of zeros", sum(atGoing.drawn()) > 0);
        assertTrue("the college wage bid up draws the gate the same workers, band by band, to the bit: the gate does not read the premium",
                Arrays.equals(atGoing.drawn(), atPremium.drawn()));
        assertTrue("...and none of them to the college band, whose posts the premium would have drawn graduates to", atPremium.drawn()[COLLEGE] == 0);
        assertTrue("...and the share is the same, to the bit", Double.doubleToLongBits(atGoing.share) == Double.doubleToLongBits(atPremium.share));
    }

    /* ============ 5. the hold's words name both counts ============ */

    static void sectionFive(Path root) {
        System.out.println("\n--- 5. the hold's words name both counts ---");
        Game g = city(root, "words");
        PopulationManager people = g.getPopulationManager();
        BuildingsTemplate sawmill = template(g, "Sawmill");
        Sector owner = BuildCard.ownerOf(g, sawmill);
        roomMonth(g.getMigration());   // a month with room, as in the village: the draw is over nothing
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
                // The comparison is sensitive to the mix: a gate that read Migration's own (null in the twin) would draw otherwise. The order asked
                // is the one this city's gap draws most for: the gap is the population's to bridge now, and a small order may draw nothing.
                Ask biggest = null;
                double most = 0;
                for (Ask a : asksOf(g)) {
                    double drawn = sum(a.staffing().drawn());
                    if (drawn > most) { most = drawn; biggest = a; }
                }
                assertTrue("fixture (" + name + "): some order of the city draws workers", biggest != null);
                Sector.Staffing sm = biggest.staffing();
                double[] withMix = mig.drawFor(sm.wanted(), g.getLabourMarket(), g.getPopulationManager(), Sector.GATE_DRAW_MONTHS, ArrivalMix.at(g.getMonth()));
                double[] withNone = mig.drawFor(sm.wanted(), g.getLabourMarket(), g.getPopulationManager(), Sector.GATE_DRAW_MONTHS, null);
                System.out.printf("   the %s's draw with the year's mix: %s; with none: %s%n", biggest.template.getName(), bands(withMix), bands(withNone));
                assertTrue("fixture (" + name + "): the year's mix changes the draw of the order that draws most", !Arrays.equals(withMix, withNone));
                assertTrue("...and the gate draws on the year's", Arrays.equals(withMix, sm.drawn()));
            }

            // The room the draw is cut to is last month's and rides the save: the twin's gate would part from the original's where it binds.
            int roomBound = 0;
            for (Ask a : asksOf(g)) if (uncappedPeople(g, a.posts) > roomLeft(mig)) roomBound++;
            System.out.printf("   %s: last month's room %,.1f of which %,.1f taken by its arrivals; %d orders are cut to the room left%n",
                    name, mig.getLastRoom(), mig.getLastArrivals(), roomBound);
            assertTrue("fixture (" + name + "): the city has a census (last month's room is a number) and some orders are cut to the room left",
                    !Double.isNaN(mig.getLastRoom()) && roomBound > 0);
            assertTrue("the twin's last month's room is the original's, to the bit (" + name + ")",
                    Double.doubleToLongBits(twin.getMigration().getLastRoom()) == Double.doubleToLongBits(mig.getLastRoom()));

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

        // Last month's room in the array itself: a number is kept to the bit (0 too), and "no census" (NaN, which DataSave's Gson cannot write) is -1 in the array and NaN back.
        Migration struck = new Migration(), noCensus = new Migration(), reader = new Migration(), readerOfNone = new Migration(), readerOfZero = new Migration();
        roomMonth(struck);
        roomMonth(readerOfNone);   // a reader that holds a room now, to be given "no census" by the array
        assertTrue("fixture: the reader that is to be given \"no census\" holds a room now", !Double.isNaN(readerOfNone.getLastRoom()));
        reader.restore(struck.toSaveArray());
        boolean noNaN = true;
        for (double v : noCensus.toSaveArray()) noNaN &= !Double.isNaN(v) && !Double.isInfinite(v);
        readerOfNone.restore(noCensus.toSaveArray());
        // fixture: placed. A room of exactly nothing (a census that found no door) is written into the struck month's own array, in the
        // slot of the room: the last of the month's scalars, which are followed by the two band mixes and the licences by job.
        double[] zeroRoom = struck.toSaveArray();
        zeroRoom[zeroRoom.length - 2 * WageBand.values().length - JobType.values().length - 1] = 0;
        readerOfZero.restore(zeroRoom);
        System.out.printf("   last month's room: struck on doors %,.1f read back %,.1f; placed at 0 read back %.1f; with no census NaN: %s%n",
                struck.getLastRoom(), reader.getLastRoom(), readerOfZero.getLastRoom(), Double.isNaN(noCensus.getLastRoom()));
        assertTrue("fixture: a month with room has a room that is a number over 0", struck.getLastRoom() > 0);
        assertTrue("the room a month struck rides the array and is read back to the bit",
                Double.doubleToLongBits(reader.getLastRoom()) == Double.doubleToLongBits(struck.getLastRoom()));
        assertTrue("...and a room of nothing (fixture: placed) is read back as nothing, not as the lack of a census",
                Double.doubleToLongBits(readerOfZero.getLastRoom()) == Double.doubleToLongBits(0.0));
        assertTrue("fixture: a Migration with no census holds no room figure, and its array has no NaN in it for the save to refuse",
                Double.isNaN(noCensus.getLastRoom()) && noNaN);
        assertTrue("...and read back by a Migration that held a room it is no census again", Double.isNaN(readerOfNone.getLastRoom()));
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

    /* ============ 8. the gap's positive part: only what the order makes positive is drawn ============ */

    /** The most months a played city is run, one press of the clock at a time, to reach the month a section asks for. */
    static final int MOST_PLAYED_MONTHS = 60;

    static void sectionEight(Path root) {
        System.out.println("\n--- 8. the gap's positive part: only what the order makes positive is drawn ---");
        // A city founded the 1900 way, played: its people grow past what its jobs and homes hold (the month's target falls under them),
        // as a city that has outgrown its work does. The month is the first one with the population over the target, a census to
        // cut the room by, and orders on both sides of the gap: some that add less than the gap is short, and one that bridges it.
        Game g = town(root, "gap", Founding.defaults().withUnfitted(true));
        Migration mig = g.getMigration();
        PopulationManager people = g.getPopulationManager();
        java.util.List<Ask> under = new java.util.ArrayList<>();
        Ask bridge = null;
        int orders = 0;
        for (int m = 0; m < MOST_PLAYED_MONTHS; m++) {
            step(g);
            double gap = gapNow(g);
            if (gap >= 0 || Double.isNaN(mig.getLastRoom()) || brake(g) <= 0) continue;
            under.clear();
            bridge = null;
            java.util.List<Ask> asks = asksOf(g);
            orders = asks.size();
            for (Ask a : asks) {
                if (gap + addition(g, a.posts) < 0) under.add(a);
                else if (uncappedPeople(g, a.posts) <= roomLeft(mig) && (bridge == null || a.posts < bridge.posts)) bridge = a;   // the smallest that bridges: the positive part is least of its addition
            }
            if (!under.isEmpty() && bridge != null) break;
        }
        assertTrue("fixture: a played city reached a month its population stands over migration's target, with orders on both sides of the gap",
                gapNow(g) < 0 && !under.isEmpty() && bridge != null);
        assertTrue("fixture: the brake lets some through that month (the city is out of work under the stop), so the bridge's draw is not nothing for that",
                brake(g) > 0);
        double gap = gapNow(g);
        System.out.printf("   month %d: %,d people against a target of %,.0f (the gap %,.1f), a census with %,.0f of room left; "
                        + "%d of %d orders add less than the gap is short (the largest %,.1f), the %s adds %,.1f%n",
                g.getMonth(), people.getPopulation(), mig.getLastTarget(), gap, roomLeft(mig), under.size(), orders,
                addition(g, under.stream().mapToDouble(a -> a.posts).max().orElse(0)), bridge.template.getName(), addition(g, bridge.posts));

        // (a) The population stands over the target even with the order: migration brings nobody for it, and the draw is nothing.
        boolean nothing = true, shareIsSpares = true, countedBefore = true, formula = true;
        for (Ask a : under) {
            Sector.Staffing st = a.staffing();
            nothing &= zeroToTheBit(st.drawn());
            shareIsSpares &= Double.doubleToLongBits(st.share) == Double.doubleToLongBits(st.shareNow);
            countedBefore &= addition(g, a.posts) * closed(mig) > 0;
            formula &= expectedDraw(g, a.template) == 0;
        }
        assertTrue("fixture: the gap is under 0 and each of these orders adds less than it is short: the population stands over the target even with the order",
                gap < 0 && under.stream().allMatch(a -> gap + addition(g, a.posts) < 0));
        assertTrue("fixture: the crowding is over 0, so it is the gap and not the doors that leaves the draw nothing", mig.getLastCrowding() > 0);
        assertTrue("fixture: the count before the gap was read (the addition closed over the window) was over nothing for each of them", countedBefore);
        assertTrue("the draw is nothing, to the bit, in every band, for every one of them", nothing);
        assertTrue("...as the formula has it", formula);
        assertTrue("...so the share is the spare's alone, to the bit", shareIsSpares);

        // (b) The order's addition takes the gap from negative to positive: the draw is the positive part's alone.
        Sector.Staffing bridged = bridge.staffing();
        double addition = addition(g, bridge.posts), positivePart = gap + addition;
        double drawn = sum(bridged.drawn());
        System.out.printf("   the %s adds %,.1f against a gap of %,.1f: %,.1f people made positive, closed over the window %,.1f; "
                        + "%.2f workers drawn where its whole addition would have been %.2f%n",
                bridge.template.getName(), addition, gap, positivePart, positivePart * closed(mig), drawn, workersOf(g, addition * closed(mig)));
        assertTrue("fixture: the order's addition takes the gap from negative to positive", gap < 0 && positivePart > 0);
        assertTrue("fixture: the room does not bind: the doors left after last month's arrivals take more than the positive part's draw",
                roomLeft(mig) > uncappedPeople(g, bridge.posts));
        assertTrue("fixture: the crowding is over 0", mig.getLastCrowding() > 0);
        near("the draw is the positive part of the gap, closed over the window, cut by the brake, in workers",
                drawn, workersOf(g, positivePart * closed(mig)) * brake(g));
        near("...the formula's", drawn, expectedDraw(g, bridge.template));
        assertTrue("...and it is less than the whole addition's, which the count before drew whatever the gap",
                drawn > 0 && drawn < workersOf(g, addition * closed(mig)) * brake(g));
    }

    /* ============ 9. within the room: no more than the doors last month left free ============ */

    static void sectionNine(Path root) {
        System.out.println("\n--- 9. within the room: no more than the doors last month left free ---");
        // A city founded the default way, played: a young city whose homes are few and whose arrivals take most of them, so a large
        // order's draw is more than the doors left. The month is the first with a census, a room left over 0 and orders on both sides of it.
        Game g = town(root, "room", Founding.defaults());
        Migration mig = g.getMigration();
        PopulationManager people = g.getPopulationManager();
        Ask bound = null, free = null;
        for (int m = 0; m < MOST_PLAYED_MONTHS && bound == null; m++) {
            step(g);
            if (Double.isNaN(mig.getLastRoom()) || roomLeft(mig) <= 0 || brake(g) <= 0) continue;
            Ask big = null, small = null;
            for (Ask a : asksOf(g)) {
                double uncapped = uncappedPeople(g, a.posts);
                if (uncapped > roomLeft(mig) && (big == null || a.posts > big.posts)) big = a;
                if (uncapped > 0 && uncapped < roomLeft(mig) && (small == null || a.posts > small.posts)) small = a;
            }
            if (big != null && small != null) { bound = big; free = small; }
        }
        assertTrue("fixture: a played city reached a month with a census, doors left over 0, and orders on both sides of them", bound != null && free != null);
        double left = roomLeft(mig), uncapped = uncappedPeople(g, bound.posts);
        System.out.printf("   month %d: room %,.1f, its arrivals %,.1f, so %,.1f people left; the %s would draw %,.1f people uncapped, the %s %,.1f%n",
                g.getMonth(), mig.getLastRoom(), mig.getLastArrivals(), left, bound.template.getName(), uncapped, free.template.getName(),
                uncappedPeople(g, free.posts));
        assertTrue("fixture: last month's room is a number, and what its arrivals left of it is over 0 and fewer than the order's uncapped draw",
                !Double.isNaN(mig.getLastRoom()) && left > 0 && left < uncapped);
        assertTrue("fixture: the gap is positive, so it is the room and not the gap that is short", gapNow(g) > 0);
        assertTrue("fixture: the city has a workforce, so people turn to workers", people.getWorkforceForSave() > 0);
        Sector.Staffing cut = bound.staffing();
        double drawn = sum(cut.drawn());
        assertTrue("fixture: the order draws workers", drawn > 0);
        near("the draw, as people before the workforce share, is the room left, last month's room less its arrivals, cut by the brake",
                drawn * people.getPopulation() / people.getWorkforceForSave(), left * brake(g));
        near("...the formula's, in workers", drawn, expectedDraw(g, bound.template));
        assertTrue("...and less than the uncapped draw's", drawn < workersOf(g, uncapped) * brake(g));
        near("an order whose draw is under the room left is not cut by it: it draws the uncapped figure, cut by the brake",
                sum(free.staffing().drawn()), workersOf(g, uncappedPeople(g, free.posts)) * brake(g));

        // A city with no census (no month struck: lastRoom is NaN) has no room to cut by.
        Game fresh = city(root, "no-census");
        Migration freshMig = fresh.getMigration();
        BuildingsTemplate ssc = template(fresh, "Shared Services Centre");
        Sector sscOwner = BuildCard.ownerOf(fresh, ssc);
        seat(fresh.getPopulationManager(), NO_CENSUS_SPARE);
        double sscDrawn = sum(sscOwner.staffing(ssc).drawn());
        assertTrue("fixture: no month was struck, so last month's room is no number", Double.isNaN(freshMig.getLastRoom()));
        assertTrue("fixture: the order's addition takes the gap from negative to positive", gapNow(fresh) < 0 && gapNow(fresh) + addition(fresh, posts(ssc)) > 0);
        near("with no census the draw is uncut: the positive part's, closed, in workers",
                sscDrawn, workersOf(fresh, uncappedPeople(fresh, posts(ssc))));
    }

    /* ============ 10. the brake and the pull rule turn the draw down as they turn arrivals ============ */

    /** The workers the village's draw would bring before the brake: the order's uncapped people, within the room, in this village's workers. */
    static double unbraked(Game g, BuildingsTemplate t) {
        return workersOf(g, Math.min(uncappedPeople(g, posts(t)), roomLeft(g.getMigration())));
    }

    static void sectionTen(Path root, long workers) {
        System.out.println("\n--- 10. the brake and the pull rule turn the draw down as they turn arrivals ---");
        Game g = city(root, "brake");
        Migration mig = g.getMigration();
        makeOld(mig);
        assertTrue("fixture: the brake village has recorded BRAKE_FROM_MONTH months, so its brake acts", mig.brakeApplies());
        PopulationManager people = g.getPopulationManager();
        BuildingsTemplate sawmill = template(g, "Sawmill");
        Sector owner = BuildCard.ownerOf(g, sawmill);

        /*
         * (a) THE PULL RULE. Senior care lifts the homes' draw, so it lifts migration's target; the jobs' draw, which is all an order's
         * posts add to, is not lifted by it. The same village and order, a month struck without senior care and one with all of it: the
         * target rises by the homes' draw x SENIOR_CARE_PULL, and the workers drawn are the same, to the bit.
         */
        seat(people, workers);
        roomMonth(mig);
        drawIsClear("no senior care", g, sawmill);
        double[] without = owner.staffing(sawmill).drawn();
        double targetWithout = mig.getLastTarget(), gapWithout = gapNow(g);
        roomMonth(mig, 1);
        drawIsClear("full senior care", g, sawmill);
        double[] with = owner.staffing(sawmill).drawn();
        System.out.printf("   senior care pull %.2f against 1: target %,.1f against %,.1f (the gap %,.1f against %,.1f); the sawmill draws %.3f workers against %.3f%n",
                mig.getLastSeniorPull(), mig.getLastTarget(), targetWithout, gapNow(g), gapWithout, sum(with), sum(without));
        assertTrue("fixture: the second month has all the senior care pull and the first none", mig.getLastSeniorPull() == Migration.seniorCarePull(1));
        assertTrue("fixture: the gap is positive both ways, so the order's whole addition is drawn in each", gapWithout > 0 && gapNow(g) > 0);
        near("fixture: senior care lifted the target by the homes' draw x SENIOR_CARE_PULL, as the model has it",
                mig.getLastTarget() - targetWithout, mig.getLastHomeDraw() * Migration.SENIOR_CARE_PULL);
        assertTrue("fixture: the draw is workers, so the comparison is not of zeros", sum(without) > 0);
        assertTrue("full senior care draws the same workers as none, band by band, to the bit: it lifts the homes' draw and not the jobs'",
                Arrays.equals(with, without));

        /*
         * (b) THE BRAKE. The same order in a month with room struck once, and the village's out-of-work share caused three ways by the
         * workers it employs beside its spare ones: under the healthy rate, between it and the stop, and past the stop.
         */
        roomMonth(mig);
        double middle = (Migration.HEALTHY_OUT_OF_WORK + Migration.ARRIVALS_STOP_OUT_OF_WORK) / 2;
        seat(people, workers);
        double rateHealthy = people.getUnemploymentRate();
        Sector.Staffing healthy = owner.staffing(sawmill);
        double drawnHealthy = sum(healthy.drawn()), unbrakedHealthy = unbraked(g, sawmill);
        seatOutOfWork(people, workers, middle);
        double rateMiddle = people.getUnemploymentRate();
        Sector.Staffing half = owner.staffing(sawmill);
        double drawnMiddle = sum(half.drawn()), unbrakedMiddle = unbraked(g, sawmill);
        seatOutOfWork(people, workers, Migration.ARRIVALS_STOP_OUT_OF_WORK + .04);
        double ratePast = people.getUnemploymentRate();
        Sector.Staffing stopped = owner.staffing(sawmill);
        double unbrakedPast = unbraked(g, sawmill);
        System.out.printf("   out of work %.4f: brake %.3f, %.2f workers drawn of %.2f unbraked, %s with the draw%n",
                rateHealthy, Migration.arrivalBrake(rateHealthy), drawnHealthy, unbrakedHealthy, pct(healthy.share));
        System.out.printf("   out of work %.4f: brake %.3f, %.2f workers drawn of %.2f unbraked (%.3f of the healthy village's), %s with the draw%n",
                rateMiddle, Migration.arrivalBrake(rateMiddle), drawnMiddle, unbrakedMiddle, drawnMiddle / drawnHealthy, pct(half.share));
        System.out.printf("   out of work %.4f: brake %.3f, %.2f workers drawn of %.2f unbraked, %s with the draw, %s staffable now%n",
                ratePast, Migration.arrivalBrake(ratePast), sum(stopped.drawn()), unbrakedPast, pct(stopped.share), pct(stopped.shareNow));
        assertTrue("fixture: the first village is out of work under the healthy rate (" + pct(Migration.HEALTHY_OUT_OF_WORK) + ")",
                rateHealthy <= Migration.HEALTHY_OUT_OF_WORK);
        assertTrue("fixture: the second is between the healthy rate and the stop",
                rateMiddle > Migration.HEALTHY_OUT_OF_WORK && rateMiddle < Migration.ARRIVALS_STOP_OUT_OF_WORK);
        assertTrue("fixture: the third is past the stop (" + pct(Migration.ARRIVALS_STOP_OUT_OF_WORK) + ")", ratePast >= Migration.ARRIVALS_STOP_OUT_OF_WORK);
        assertTrue("fixture: the same spare workers in each, so the spare share is the same",
                healthy.shareNow == half.shareNow && half.shareNow == stopped.shareNow);
        assertTrue("fixture: the unbraked draw is workers in the third village (the gap is positive and the room does not bind), so the brake is what makes it nothing",
                unbrakedPast > 0 && gapNow(g) > 0 && roomLeft(mig) > uncappedPeople(g, posts(sawmill)));
        near("under the healthy rate the draw is the unbraked draw, whole", drawnHealthy, unbrakedHealthy * Migration.arrivalBrake(rateHealthy));
        near("between the dials the draw is the unbraked draw x the brake at the rate it reads", drawnMiddle, unbrakedMiddle * Migration.arrivalBrake(rateMiddle));
        assertTrue("...which is less than the healthy village's, and more than nothing", drawnMiddle > 0 && drawnMiddle < drawnHealthy);
        assertTrue("past the stop the draw is nothing, to the bit, in every band", zeroToTheBit(stopped.drawn()));
        assertTrue("...so the share is the spare's alone, to the bit", Double.doubleToLongBits(stopped.share) == Double.doubleToLongBits(stopped.shareNow));
        assertTrue("the order the healthy village passed on the draw", healthy.passes() && healthy.shareNow < Sector.MIN_STAFFABLE_TO_ORDER);
        assertTrue("...is refused past the stop, where the spare is what it was", !stopped.passes());

        /*
         * (c) A YOUNG VILLAGE IS NOT BRAKED. The same order, month and spare workers in a village with no months recorded, out of work past
         * the stop: the brake is not read (Migration.BRAKE_FROM_MONTH), so it draws the unbraked draw, whole, and passes on it.
         */
        Game young = city(root, "brake-young");
        Migration youngMig = young.getMigration();
        PopulationManager youngPeople = young.getPopulationManager();
        BuildingsTemplate youngSawmill = template(young, "Sawmill");
        Sector youngOwner = BuildCard.ownerOf(young, youngSawmill);
        roomMonth(youngMig);
        seatOutOfWork(youngPeople, workers, Migration.ARRIVALS_STOP_OUT_OF_WORK + .04);
        double rateYoung = youngPeople.getUnemploymentRate();
        Sector.Staffing youngStaffing = youngOwner.staffing(youngSawmill);
        double drawnYoung = sum(youngStaffing.drawn()), unbrakedYoung = unbraked(young, youngSawmill);
        System.out.printf("   out of work %.4f with no months recorded: brake now %.3f, %.2f workers drawn of %.2f unbraked, %s with the draw%n",
                rateYoung, youngMig.arrivalBrakeNow(rateYoung), drawnYoung, unbrakedYoung, pct(youngStaffing.share));
        assertTrue("fixture: the young village has no months recorded and is out of work past the stop, with the same spare as the old one's",
                !youngMig.brakeApplies() && rateYoung >= Migration.ARRIVALS_STOP_OUT_OF_WORK && youngStaffing.shareNow == stopped.shareNow);
        assertTrue("fixture: the unbraked draw is workers there (the gap is positive and the room does not bind)",
                unbrakedYoung > 0 && gapNow(young) > 0 && roomLeft(youngMig) > uncappedPeople(young, posts(youngSawmill)));
        near("a village younger than BRAKE_FROM_MONTH, past the stop, draws the unbraked draw, whole", drawnYoung, unbrakedYoung);
        assertTrue("...and the order it passes on that draw is not refused for want of the brake",
                youngStaffing.passes() && youngStaffing.shareNow < Sector.MIN_STAFFABLE_TO_ORDER);
    }
}

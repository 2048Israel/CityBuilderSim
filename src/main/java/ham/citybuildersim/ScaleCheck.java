package ham.citybuildersim;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * A city past 2^31 people (0.7.53): its counts read back whole, its posts,
 * doors and household places add up to the last one, and a save gives them
 * all back.
 *
 * WHY. Jerus plans cities of five to ten billion people, and until 0.7.53
 * every count of them was an int, which wraps silently at 2,147,483,647. The
 * scale study (the project's spec-scale.md, section 2) loaded a 5.09B copy of
 * his city: its population read back as 938,901,759, its workforce stuck at
 * 2,147,483,647, and unclamped the save would not parse at all, because the
 * slot header's population was an int. The population, the workforce, the
 * posts, the homes and the household capacity are longs now, through the
 * model, the save, the history and the screens.
 *
 * THE METHOD IS THE STUDY'S (its scale_save.py, ported as scale()). A founded
 * city is saved and its save multiplied by K: the population, the workforce,
 * the pyramid, the families, the households in each cell, the buildings and
 * their sites, every sector's and market's stocks and flows, the loans, the
 * treasury, the bank, the foreign accounts and the central bank. What is per
 * household stays; a household's shares and bonds are divided by K so the
 * registers still add up. K is a whole number, so every building count, and
 * so every post and door, is exactly K times the founded city's. Its land's
 * books - each holding's five areas and its forest's timber - are K times the
 * founded city's too, as its square feet are (0.7.67): the books are drawn
 * plots, counted exactly, and a copy's ground of millions of square
 * kilometres is not drawn and counted in a harness's time (until 0.7.66 the
 * load drew a centre to hold it from samples); its blocks, its fields and its
 * iron are the founded city's.
 *
 * EVERY SECTOR IS HELD in sections 2 to 5 (BusinessInvestment.holdSector()),
 * as 0.7.53 wrote them: what they count does not depend on what the sectors
 * build. Section 6 lets them build, at 5 and at 10 billion, now that the
 * three order loops search instead of counting (0.7.54; Game, THE LARGEST
 * SLICE, WITHOUT COUNTING TO IT): counting, a 10 billion copy of city2400
 * took up to 143 s a month.
 *
 * What this has to prove:
 *   1. the founded city, and its copy K times over past 2^31 people;
 *   2. the copy's counts read back whole - not wrapped, not stuck at the int
 *      ceiling - and the slot header's population with them;
 *   3. its posts, doors and household places are exactly K times the founded
 *      city's, and every way of adding them up agrees;
 *   4. three months at that size: still whole, still adding up, and the
 *      history records what the screens show;
 *   5. a save gives every one of those figures back, and the city it loads
 *      plays its next month as the one that was saved;
 *   6. a growing city - the playtest's at FREE_FIXTURE_MONTHS, after its
 *      player's look then (0.7.58), with ground free for a home - copied to 5
 *      and to 10 billion with its sectors free, so they order: every month
 *      whole and adding up, money conserved to RELATIVE_RESIDUAL of what
 *      moved from the first month, the city's books agreeing within
 *      MoneyAudit.tolerance() where a harness's absolute cent would not, no
 *      order reading more waits or asking the bond desk more often than its
 *      search allows, and the median month under MONTH_MEDIAN_MS; and since
 *      0.7.55, its ground priced as the city it was copied from is (the same
 *      crowding, the same premium), its rents in reach as that city's are,
 *      and homes built - where at 0.7.54 its land cost about two million
 *      times the city's and it built nothing;
 *   7. money at that size reads in its own unit: quadrillions, and never a
 *      whole-dollar figure past what a double holds (Formats);
 *   8. the audit's floor at size (0.7.63; 64 steps since 0.7.64): a floor of
 *      its own never under MoneyAudit.ULP_STEPS of a double's steps at the
 *      size of the figures - the 4.7e9-unit cash-flow statement batch K's
 *      copy at month 430 missed by 1.3e-6, rounding alone, now passes - and
 *      exactly the floor at every size the other harnesses read it at; a cent
 *      never moves.
 */
public class ScaleCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    /** Jerus's smaller plan, in people: what the copy is scaled to reach. */
    static final double TARGET_PEOPLE = 5e9;

    /** Months the copy plays before it is saved. */
    static final int MONTHS_AT_SIZE = 3;

    /** The founded city's age, in months, when it is saved and copied. */
    static final int FIXTURE_MONTHS = 120;

    /** Jerus's two plans, in people: the copies whose sectors are free (section 6). */
    static final double[] FREE_PEOPLE = { 5e9, 1e10 };

    /**
     * The month the playtest's city is copied at for section 6. The founded
     * city above is shrinking at month 120, and copied with its sectors free
     * none of them ordered anything; the playtest's city at 400 months is
     * growing, and its copies order: at 5 billion an order sized from
     * 2,147,483,647 needed and one of 280 million judged on its interest.
     *
     * ...AFTER ITS PLAYER'S LOOK AT THAT MONTH (0.7.58, lookAt()). Since
     * 0.7.58 the playtest's player buys a building's shortfall of ground and
     * no more (Game.bestOffer(), A SHORTFALL IS MET WITH GROUND), so between
     * its looks the city stands full: at month 400, mid-way through one of
     * the rhythm's long skips, it had 1,470 sq ft free, no room for a home at
     * any price (0.7.57's had 19,920), and the 10 billion copy built
     * nothing. "It builds" is a test of what ground costs a copy, so the
     * copy is of the city its player has just looked at - who buys room to
     * grow when the city is full, as at every stop of the run.
     *
     * ...AT 410 SINCE 0.7.62, WHERE ITS OWN RENTS PRICE NOBODY OUT. The rents'
     * pull is compared month for month, and a copy cannot follow the city
     * into a squeeze its own growth makes: the copies take no arrivals
     * (WHAT IT STILL LOSES, below - the founding care and police are one
     * city's), so when the city's homes fill faster than its landlords
     * build, its pull drops and theirs stays at 1. 0.7.55 to 0.7.61's town at
     * 400 never squeezed in the six months read; batch K's (the drivers'
     * fuel at the world's price level moved the run from month 26) is in its
     * take-off at 400, 955 people in 265 homes, and squeezed in three of the
     * six (the pull .921, .962, .989). Scanned (scratch-k scanF): the six
     * months after 410 and after 425 are clear and every assertion holds;
     * after 405, 415 and 420 the city squeezes. The fixture says it now
     * (Original.calm()): a city whose own rents bind is not one a copy that
     * cannot grow can be read against.
     *
     * ...AT 550 SINCE 0.7.64, BY THE SAME PREMISE. Batch L's player buys its
     * iron a whole field at a time and no longer buys a deposit as a move
     * (LongPlaytest.ironWhenNeeded()), and the first ring's offers hold no
     * ore, so the village buys other ground (the playtest moves from month
     * 32): its town takes off later - 846 people at 410 where batch K's had
     * 1,042, 1,734 at 500 where K's had 4,305 - and after every month
     * scanned from 400 to 530 its rents squeeze in the six months read, or
     * its copies order nothing judged on its interest (batch L's scratch,
     * scale/: 400 to 440 squeeze; 490 to 530 order nothing, 490 and 520
     * squeezing as well). After 535 to 570 the city is calm, growing, with
     * ground free for a home, and its copies order and build: the middle of
     * that, 550 (2,711 people at 555).
     *
     * ...AT 450 SINCE 0.7.67, BY THE SAME PREMISE. On the block grid (batch
     * M3) a new city owns 1.7% more ground and its first offers are 120 m
     * blocks, so the playtest moves from month 1 and its town grows sooner:
     * 4,629 people at 560 where batch L's had about 2,700 at 555. Scanned
     * (scratch-m3/scale, ScaleCheck with the month changed, every assertion
     * read): from 535 to 570 the copies order nothing sized from a million
     * needed; 490 to 530 the same; 400 to 440 one fixture or another fails
     * at most months (passing at 405, 410, 413 and 425); after 445 to 453
     * the city is calm, growing, with ground free for a home, and its copies
     * order - every assertion holds at 445, 446, 447, 449, 450, 451, 452
     * and 453, while at 448 the copies built no more homes than they began
     * with. 450, in that run: 4,370 people at 455.
     *
     * ...AT 545 SINCE 0.7.67'S BATCH M3b, BY THE SAME PREMISE. The player
     * prices its room to grow from the ground-ahead line, orders no more
     * transit than its riders fill and buys an iron field only when its
     * mines pay it back (LongPlaytest.roomToGrow(), fieldEarnings()), so its
     * town builds power and roads where it bought ground: 15,608 people at
     * 550. Scanned (scratch-m3b/scale, ScaleCheck with the month changed,
     * every assertion read): after 400 to 420 the copies built no more homes
     * than they began with; 445 had no ground free for a home; at 455 to
     * 460 and 480 to 525 the copies ordered nothing sized from a million
     * needed, and 570 to 600 the same; 430, 440 and 470 pass alone; every
     * assertion holds at each month scanned from 527 to 565 (527, 530, 533,
     * 535, 537, 540, 543, 545, 547, 550, 553, 555, 557, 560, 563, 565). 545
     * is the middle of that.
     */
    static final int FREE_FIXTURE_MONTHS = 545;

    /** Months each free copy plays, every one timed. */
    static final int FREE_MONTHS = 6;

    /** The most the audit's residual may be of what moved, at any size: five orders of magnitude inside MoneyCheck's 1e-4. */
    static final double RELATIVE_RESIDUAL = 1e-10;

    /**
     * The longest a free copy's median month may take, in milliseconds. On
     * the 2-core cloud machine 0.7.54 was built on, the median month was 8 ms
     * at 5 billion and 9 ms at 10 billion; 0.7.53's counting loops took a
     * median of 8,782 ms on the same 5 billion copy. The bound is about 170
     * times the first, for a slower machine and a busy one, and a sixth of
     * the second.
     */
    static final double MONTH_MEDIAN_MS = 1500;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-84s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void same(String label, long actual, long expected) {
        boolean ok = actual == expected;
        if (!ok) fails++;
        out.printf("%-84s %s  %,d against %,d%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    /** One look by the playtest's player, as at each stop of its rhythm (OrderSearchCheck.playtestRhythm()): the schools seen to, its moves one a month, and two months after (0.7.58; see FREE_FIXTURE_MONTHS). */
    static void lookAt(Game g) {
        PrintStream was = LongPlaytest.out, real = System.out;
        LongPlaytest.out = quiet;
        System.setOut(quiet);
        try {
            LongPlaytest.ensureSchools(g);
            for (int move = 0; move < LongPlaytest.movesPerLook(); move++) {
                if (LongPlaytest.advise(g) == null) break;
                LongPlaytest.run(g, 1);
            }
            LongPlaytest.run(g, 2);
        } finally {
            LongPlaytest.out = was;
            System.setOut(real);
        }
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        Path root = Files.createTempDirectory("scalecheck");
        try {
            run(root);
        } finally {
            cleanUp(root);
        }
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    static void run(Path root) throws Exception {

        /* ============== 1. the fixture, and its copy K times over ============== */
        out.println("--- the founded city, and its copy K times over past 2^31 people ---");

        GameFiles small = new GameFiles(root.resolve("small"), root.resolve("small-no-legacy"));
        Game founded = new Game(small, LongPlaytest.founding());
        PrintStream was = LongPlaytest.out;
        LongPlaytest.out = quiet;
        try {
            quietly(() -> {
                founded.run();
                LongPlaytest.build(founded, "House", 40);
                LongPlaytest.build(founded, "Convenience Store", 3);
                LongPlaytest.build(founded, "Mixed Farm", 2);
                founded.simulateMonths(3);
                LongPlaytest.build(founded, "House", 20);
                founded.simulateMonths(4);
                LongPlaytest.build(founded, "Convenience Store", 2);
                LongPlaytest.build(founded, "Construction Depot", 1);
                founded.simulateMonths(FIXTURE_MONTHS - founded.getMonth());
                founded.saveGame(10, "scalecheck");
            });
        } finally {
            LongPlaytest.out = was;
        }

        // The founded city as its own save loads it, so both sides of every
        // comparison below come through the same load.
        Game one = new Game(small);
        quietly(() -> one.loadGameSave(10));
        long pop1 = one.getPopulationManager().getPopulation();
        long workforce1 = one.getPopulationManager().getWorkforce();
        long posts1 = everyPost(one);
        long homes1 = one.getBuildingManager().getTotalHomes();
        long capacity1 = one.getHouseholdCapacity();
        long k = Math.round(TARGET_PEOPLE / Math.max(1, pop1));
        out.printf("      founded: month %d, %,d people, %,d workers, %,d posts, %,d homes, places for %,d; K = %,d%n",
                one.getMonth(), pop1, workforce1, posts1, homes1, capacity1, k);
        assertTrue("fixture: the founded city has people, posts and homes",
                pop1 > 0 && workforce1 > 0 && posts1 > 0 && homes1 > 0);
        assertTrue("fixture: K times its people passes 2^31", k * pop1 > Integer.MAX_VALUE);

        GameFiles big = new GameFiles(root.resolve("big"), root.resolve("big-no-legacy"));
        scale(small.saveFile(10), big.saveFile(10), k);
        Files.copy(small.historyFile(10), big.historyFile(10));

        /* ============== 2. the counts read back whole ============== */
        out.println("\n--- the copy's counts read back whole ---");

        SaveHeader header = big.readHeader(10);
        assertTrue("the slot header of a save past 2^31 people reads (an int could not parse it)", header != null);
        if (header != null) same("...and its population is K times the founded city's", header.getPopulation(), k * pop1);

        Game g = new Game(big);
        quietly(() -> g.loadGameSave(10));
        for (Sector s : g.getSectors().all()) g.getBusinessInvestment().holdSector(s.key());
        PopulationManager people = g.getPopulationManager();
        assertTrue("the copy loads", g.getLoadFailure() == null && g.getMonth() == one.getMonth());
        same("its population is K times the founded city's, past 2^31", people.getPopulation(), k * pop1);
        same("...its workforce too, not stuck at 2,147,483,647", people.getWorkforce(), k * workforce1);
        assertTrue("...both past the int ceiling",
                people.getPopulation() > Integer.MAX_VALUE && people.getWorkforce() > Integer.MAX_VALUE);
        /*
         * ITS GROUND IS K TIMES THE CITY'S, AND ITS LAND'S BOOKS SAY SO (0.7.57;
         * 0.7.67). The copy's square feet were multiplied and, since 0.7.67, its
         * land's books with them (scale()): the load reads the same ground as
         * the figure, to within a plot, and draws nothing again. Until 0.7.66
         * the books were not scaled and the load drew a centre to hold the
         * figure from samples; the books are counted plots now, and counting
         * millions of square kilometres plot by plot is minutes, not a
         * harness's seconds.
         */
        LandManager ground1 = one.getLandManager(), groundK = g.getLandManager();
        assertTrue("its ground is K times the founded city's", groundK.getOwnedSqFt() == ground1.getOwnedSqFt() * k);
        assertTrue("...and its land's books K times the founded city's (scaled with it, 0.7.67): its dry ground that figure"
                + " to within a plot, its blocks and its site the founded city's",
                LandConversion.sameGround(groundK.getOwnedSqFt(), groundK.getLandDrySqFt())
                        && g.getCityLand().centreKm2(CityLand.DRY) == one.getCityLand().centreKm2(CityLand.DRY) * k
                        && g.getCityLand().purchases().size() == one.getCityLand().purchases().size()
                        && g.getCityLand().stamp() == one.getCityLand().stamp()
                        && g.getCityLand().siteX() == one.getCityLand().siteX()
                        && g.getCityLand().siteY() == one.getCityLand().siteY());
        out.printf("      its land: %,.0f km2 dry in a centre of %,.0f km2 (the founded city's %.2f km2 dry)%n",
                g.getCityLand().centreKm2(CityLand.DRY), g.getCityLand().centreKm2(CityLand.TOTAL),
                LandManager.km2(ground1.getOwnedSqFt()));
        assertTrue("...its iron as the founded city's: sites and tonnes",
                groundK.getIronDeposits() == ground1.getIronDeposits()
                        && groundK.getIronReserveTonnes() == ground1.getIronReserveTonnes());

        /* ============== 3. posts, doors and household places ============== */
        out.println("\n--- posts, doors and household places add up exactly ---");
        same("every post is K times the founded city's", everyPost(g), k * posts1);
        same("every home is K times the founded city's", g.getBuildingManager().getTotalHomes(), k * homes1);
        same("the household places are K times the founded city's, past the city's own 100",
                g.getHouseholdCapacity() - 100, k * (capacity1 - 100));
        addsUp(g, "at load");

        /* ============== 4. three months at that size ============== */
        out.println("\n--- " + MONTHS_AT_SIZE + " months at that size ---");
        for (int i = 0; i < MONTHS_AT_SIZE; i++) {
            long t = System.nanoTime();
            quietly(() -> g.simulateMonths(1));
            out.printf("      month %d: %,d people, %,d workers, %,d posts, %,d out of work (%.0f ms)%n",
                    g.getMonth(), people.getPopulation(), people.getWorkforce(), people.getTotalJobs(),
                    people.getUnemployed(), (System.nanoTime() - t) / 1e6);
        }
        assertTrue("the months ran", g.getMonth() == one.getMonth() + MONTHS_AT_SIZE);
        assertTrue("the population is still past 2^31", people.getPopulation() > Integer.MAX_VALUE);
        same("...and is the pyramid's, rounded once", people.getPopulation(), Math.round(g.getCohorts().total()));
        assertTrue("the workforce is still past 2^31", people.getWorkforce() > Integer.MAX_VALUE);
        addsUp(g, "after " + MONTHS_AT_SIZE + " months");
        HistorySave h = g.getHistorySave();
        same("the history's last population is the city's", last(h.getPopulation()), people.getPopulation());
        same("...its workforce", last(h.getWorkforce()), people.getWorkforce());
        same("...its posts", last(h.getJobs()), people.getTotalJobs());
        same("...the people out of work", last(h.getOutOfWork()), people.getUnemployed());
        same("...and its homes", Math.round(h.aligned("homes")[h.months() - 1]), g.getBuildingManager().getTotalHomes());

        /* ============== 5. the save round-trips them ============== */
        out.println("\n--- a save gives every figure back ---");
        assertTrue("the city past 2^31 saves", quietlyGet(() -> g.saveGame(10, "scalecheck x" + k).ok));
        SaveHeader again = big.readHeader(10);
        assertTrue("its header reads", again != null);
        if (again != null) same("...with the population it was saved with", again.getPopulation(), people.getPopulation());

        Game back = new Game(big);
        quietly(() -> back.loadGameSave(10));
        for (Sector s : back.getSectors().all()) back.getBusinessInvestment().holdSector(s.key());
        PopulationManager backPeople = back.getPopulationManager();
        assertTrue("it loads at the month it was saved", back.getLoadFailure() == null && back.getMonth() == g.getMonth());
        same("the population came back", backPeople.getPopulation(), people.getPopulation());
        same("...the workforce", backPeople.getWorkforce(), people.getWorkforce());
        same("...the posts", backPeople.getTotalJobs(), people.getTotalJobs());
        same("...the posts filled", backPeople.getJobsFilled(), people.getJobsFilled());
        same("...the people out of work", backPeople.getUnemployed(), people.getUnemployed());
        same("...the homes", back.getBuildingManager().getTotalHomes(), g.getBuildingManager().getTotalHomes());
        same("...the household places", back.getHouseholdCapacity(), g.getHouseholdCapacity());
        HistorySave hb = back.getHistorySave();
        assertTrue("...and the history, month for month",
                hb.getPopulation().equals(h.getPopulation()) && hb.getWorkforce().equals(h.getWorkforce())
                && hb.getJobs().equals(h.getJobs()) && hb.getOutOfWork().equals(h.getOutOfWork())
                && java.util.Arrays.equals(hb.aligned("homes"), h.aligned("homes")));
        addsUp(back, "as loaded");

        quietly(() -> { g.simulateMonths(1); back.simulateMonths(1); });
        same("the loaded city plays its next month to the same population", backPeople.getPopulation(),
                people.getPopulation());
        same("...the same workforce", backPeople.getWorkforce(), people.getWorkforce());
        same("...and the same people out of work", backPeople.getUnemployed(), people.getUnemployed());

        /* ============== 6. the sectors free, at 5 and 10 billion ============== */
        out.println("\n--- a growing city to copy with its sectors free: the playtest's, at month " + FREE_FIXTURE_MONTHS
                + " and its player's look ---");
        GameFiles grown = new GameFiles(root.resolve("grown"), root.resolve("grown-no-legacy"));
        Game city = new Game(grown, LongPlaytest.founding());
        OrderSearchCheck.playtestCity(city, FREE_FIXTURE_MONTHS, quiet);
        lookAt(city);
        quietly(() -> city.saveGame(10, "scalecheck grown"));
        long popGrown = city.getPopulationManager().getPopulation();
        out.printf("      month %d, %,d people%n", city.getMonth(), popGrown);
        assertTrue("fixture: the city is growing - more people than a year before",
                popGrown > city.getHistorySave().getPopulation().get(city.getHistorySave().months() - 13));
        BuildingsTemplate home = city.getBuildingManager().getTemplateByName("House");
        out.printf("      %,.0f sq ft free after its player's look%n", city.getLandManager().getAvailableSqFt());
        assertTrue("fixture: ...with ground free for a home", home != null
                && city.getLandManager().getAvailableSqFt() >= home.getLandSqFt());
        // ...and its own next FREE_MONTHS, which the copies are read against (0.7.55).
        Original original = new Original(city);
        assertTrue("fixture: ...and its own rents price no migrant out in the months its copies are read against"
                + " (the affordability pull 1 in all " + FREE_MONTHS + ")", original.calm());
        for (double target : FREE_PEOPLE) free(root, grown, popGrown, target, original);

        /* ============== 7. money at that size reads ============== */
        reads();

        /* ============== 8. the audit's floor at size ============== */
        auditFloor();
    }

    /**
     * Section 8 (0.7.64, batch L; the floor itself since 0.7.63, at 8 steps;
     * fixK-notes 7 d): MoneyAudit.tolerance()'s floor at the size of the
     * figures. A floor of 1e-6 is four of a double's steps at 2^30 units and
     * about one at 4.7e9: K's copy at month 430 failed a Retail cash-flow
     * statement that missed by 1.3e-6 on figures of 4.7e9 (1.4 steps).
     */
    static void auditFloor() {
        out.println("\n--- the audit's floor is never under the figures' own step (0.7.63) ---");
        double floor = TreasuryCheck.TOLERANCE, k = MoneyAudit.ULP_STEPS;
        boolean atLeast = true, exact = true;
        for (double size = 1; size <= 1e16; size *= 1.7) {
            atLeast &= MoneyAudit.tolerance(floor, size) >= Math.max(floor, k * Math.ulp(size));
        }
        // What the floors under a cent read in the other harnesses, at most (batch L's scratch, tol/): 2.5e6 units.
        for (double size : new double[] { 0, 1, 1.2e3, 9.0e3, 1.1e5, 1.5e5, 2.5e6, Math.scalb(1.0, 27) - 1 }) {
            exact &= MoneyAudit.tolerance(floor, size) == floor && MoneyAudit.tolerance(.005 + 1e-9, size) == .005 + 1e-9;
        }
        assertTrue("a floor of its own is never under ULP_STEPS of a double's steps at the size, at any size", atLeast);
        assertTrue("...and is the floor exactly below 2^27 units, past every size the other harnesses read it at", exact);
        double kMiss = 1.3e-6, kSize = 4.7e9;
        out.printf("      batch K's miss: %.1e on %.1e, %.2f steps; the floor there %.2e (until 0.7.63 %.0e)%n",
                kMiss, kSize, kMiss / Math.ulp(kSize), MoneyAudit.tolerance(floor, kSize), floor);
        assertTrue("...so the cash-flow statement K's copy missed on rounding alone is within it", kMiss <= MoneyAudit.tolerance(floor, kSize));
        boolean cent = true;
        for (double size = 1; size <= 1e16; size *= 1.7) {
            double before = MoneyAudit.RELATIVE_TOLERANCE * size > MoneyAudit.CENT ? MoneyAudit.RELATIVE_TOLERANCE * size : MoneyAudit.CENT;
            cent &= MoneyAudit.tolerance(size) == before;
        }
        assertTrue("a cent never moves: a cent or a part in a trillion, at every size, as before", cent);
        assertTrue("...and a size that is not a number keeps the floor alone",
                MoneyAudit.tolerance(floor, Double.NaN) == floor && MoneyAudit.tolerance(floor, Double.POSITIVE_INFINITY) == floor);
    }

    /** Section 7: Formats at ten billion - the quadrillion step, and cash() past the whole dollars a double holds. */
    static void reads() {
        out.println("\n--- money at that size reads in its own unit ---");
        Formats f = Formats.INSTANCE;
        sameText("a 10 billion city's treasury, 2.6e13 units (the study's), reads in quadrillions", f.amount(2.6e13), "$26.0Q");
        sameText("...a thousand quadrillion and more is grouped", f.amount(1.317e15), "$1,317.0Q");
        sameText("...a negative one keeps its sign", f.amount(-2.6e13), "-$26.0Q");
        sameText("...and a trillion still reads as one", f.amount(5e9), "$5.0T");
        double lastWhole = Math.floor(Formats.WHOLE_DOLLARS_MOST / 1000 / 2);
        sameText("cash() prints every whole dollar a double holds, as before",
                f.cash(lastWhole), "$" + String.format("%,d", Math.round(lastWhole * 1000)));
        sameText("...and past Formats.WHOLE_DOLLARS_MOST it is amount()'s", f.cash(1.317e15), f.amount(1.317e15));
        assertTrue("...so a sum past a long's reach is not the saturated $9,223,372,036,854,775,807",
                !f.cash(1e17).contains("9,223,372,036,854,775,807") && f.cash(1e17).endsWith("Q"));
    }

    static void sameText(String label, String actual, String expected) {
        boolean ok = actual.equals(expected);
        if (!ok) fails++;
        out.printf("%-84s %s  \"%s\"%s%n", label, ok ? "OK" : "FAIL", actual, ok ? "" : " against \"" + expected + "\"");
    }

    /**
     * Section 6: a city's save (`small`, of `pop1` people) copied to `target`
     * people and played with every sector free to build.
     *
     * THE FIRST MONTH'S JUMP IS THE LAND, NOT THE COPY (0.7.54). With its
     * sectors free, 0.7.53's 5 billion copy of the founded city moved the
     * treasury from 8.4e11 to 3.3e13 in its first month, with an audit
     * residual of 2.7e9. They were two things. The residual was the copy's:
     * the builders' recognisedThisMonth left unscaled (THE NAMES THE PATTERN
     * READ WRONG, below), and it is gone. The cash is the model's, and it
     * comes every month: LandMarket.update() prices land at a premium linear
     * in the city's absolute size, so at 5 billion people a square foot cost
     * 2,490 against the founded city's 0.0013, and the property tax on the
     * businesses' land came to 3.2e13 in the month against 196 x K at the
     * founded city's price. They borrow it from the bank, and the bank from
     * the central bank's window. Read at the founded city's size (the study's
     * probe patch), the same copy's treasury moved 8.37e11 to 8.39e11. That
     * premium was Jerus's to decide (the project's spec-scale.md, section 8,
     * step 2), and he did:
     *
     * CROWDED, NOT BIG (0.7.55). The premium is the city's crowding now
     * (LandMarket, THE CROWDING PREMIUM), and a copy K times over is exactly
     * as crowded as the city it copies, so the copy is asserted against that
     * city's own FREE_MONTHS (Original): the same ground price in its first
     * month, the rents' pull on migrants (Migration's affordability pull) the
     * city's every month - at 0.7.54 it fell to 0.08 in the 5 billion copy's
     * sixth month, rents having followed the land - and homes built, where
     * 0.7.54's built none.
     *
     * WHAT IT STILL LOSES, AND WHY: NOT THE LAND. The copies lose about 0.85%
     * of their people a month, with no arrivals at all. The city's founding
     * endowments of care and of police (Healthcare.foundingCapacity(),
     * SafetyType.foundingCapacity()) are one founded city's, not a building,
     * so they do not scale: the playtest's town at month 400 has all its
     * senior care from that endowment, and its copy K times over has a K-th
     * of it - the senior-care pull on migrants (1.13 in the town) is gone,
     * and crime climbs. Measured with both endowments scaled by K (a scratch
     * probe, 2026-10-06), the 1,000-times copy grew 1.5% in six months
     * against the town's 2.5%. Printed, not asserted: a fixture's age, not a
     * fault at size - a real city at billions has built its care.
     */
    static void free(Path root, GameFiles small, long pop1, double target, Original original) throws Exception {
        long k = Math.round(target / Math.max(1, pop1));
        out.printf("%n--- the sectors free, %,.0f people (K = %,d): whole, adding up, conserved, in time ---%n", target, k);
        GameFiles files = new GameFiles(root.resolve("free" + k), root.resolve("free" + k + "-no-legacy"));
        scale(small.saveFile(10), files.saveFile(10), k);
        Files.copy(small.historyFile(10), files.historyFile(10));
        Game g = new Game(files);
        quietly(() -> g.loadGameSave(10));
        assertTrue("the copy loads", g.getLoadFailure() == null);
        Orders orders = new Orders();
        g.watchOrders(orders);
        PopulationManager people = g.getPopulationManager();
        Books books = new Books();
        double[] ms = new double[FREE_MONTHS];
        double worstRelative = 0, worstPull = 0;
        boolean whole = true, counted = true;
        long homesBefore = g.getBuildingManager().getTotalHomes(), peopleBefore = people.getPopulation();
        double firstCrowding = 0, firstGround = 0, firstPremium = 0, firstSale = 0;
        for (int i = 0; i < FREE_MONTHS; i++) {
            double opening = g.getCash();
            long t = System.nanoTime();
            quietly(() -> g.simulateMonths(1));
            ms[i] = (System.nanoTime() - t) / 1e6;
            MoneyAudit.Result r = g.getLastMoneyAudit();
            worstRelative = Math.max(worstRelative, r.relative());
            whole &= people.getPopulation() > Integer.MAX_VALUE && people.getWorkforce() > Integer.MAX_VALUE
                    && people.getPopulation() == Math.round(g.getCohorts().total());
            counted &= sumsAgree(g);
            books.month(g, opening);
            if (i == 0) {
                firstCrowding = g.getLandManager().getCrowding();
                firstGround = g.getLandManager().getGroundUsdPerSqFt();
                firstPremium = g.getLandManager().getCrowdingPremium();
                firstSale = g.getLandManager().getPricePerSqFt();
            }
            worstPull = Math.max(worstPull,
                    Math.abs(g.getMigration().getLastAffordabilityPull() - original.affordability[i]));
            out.printf("      month %d: %,d people, %,d posts; treasury %.3e -> %.3e; residual %.2e of %.3e moved (%.0f ms)%n",
                    g.getMonth(), people.getPopulation(), people.getTotalJobs(), opening, g.getCash(),
                    r.relative(), Math.abs(r.inflows) + Math.abs(r.outflows), ms[i]);
        }
        double[] sorted = ms.clone();
        java.util.Arrays.sort(sorted);
        double median = (sorted[(FREE_MONTHS - 1) / 2] + sorted[FREE_MONTHS / 2]) / 2;
        assertTrue("every month the population is past 2^31, whole, and the pyramid's rounded once", whole);
        assertTrue("...and the posts, the doors and the household places add up exactly, every month", counted);
        assertTrue(String.format("money is conserved to %.0e of what moved, the first month included (worst %.1e)",
                RELATIVE_RESIDUAL, worstRelative), worstRelative < RELATIVE_RESIDUAL);
        books.verdicts();
        orders.verdicts();
        // 0.7.55: the ground, the rents and the building, against the city it copies.
        out.printf("      the ground in the first month: %.1f people a km2 (the city's %.1f), premium %.4f (%.4f),"
                        + " US$%.6g a sq ft (US$%.6g)%n", firstCrowding, original.crowding,
                firstPremium, original.premium, firstGround, original.ground);
        assertTrue("the copy's ground is the city's in its first month: as crowded, the same premium and dollars",
                Math.abs(firstPremium - original.premium) <= 1e-9 * original.premium
                        && Math.abs(firstGround - original.ground) <= 1e-9 * original.ground);
        assertTrue("...and its businesses pay the city's price for it",
                Math.abs(firstSale - original.sale) <= 1e-9 * original.sale);
        assertTrue(String.format("its rents pull migrants as the city's do, every month (the affordability pull"
                + " within %.2f; worst %.2g)", AFFORDABILITY_WITHIN, worstPull), worstPull <= AFFORDABILITY_WITHIN);
        long homesAfter = g.getBuildingManager().getTotalHomes();
        out.printf("      homes %,d -> %,d (%.2f built per K; the city built %.0f); people %,d -> %,d (%.2f%% a month; the"
                        + " city %+.2f%%) - see WHAT IT STILL LOSES above%n", homesBefore, homesAfter,
                (homesAfter - homesBefore) / (double) k, original.homesBuilt, peopleBefore, people.getPopulation(),
                (Math.pow(people.getPopulation() / (double) peopleBefore, 1.0 / FREE_MONTHS) - 1) * 100,
                (Math.pow(original.peopleAfter / (double) original.peopleBefore, 1.0 / FREE_MONTHS) - 1) * 100);
        assertTrue("...and it builds: more homes at the end than at the start", homesAfter > homesBefore);
        assertTrue("fixture: the free copy ordered - an order sized from more than a million needed,"
                + " and one judged on its interest", orders.largestNeeded > 1_000_000 && orders.invested > 0);
        assertTrue(String.format("the median month is under %,.0f ms (%,.0f ms; the slowest %,.0f ms)",
                MONTH_MEDIAN_MS, median, sorted[FREE_MONTHS - 1]), median < MONTH_MEDIAN_MS);
    }

    /** addsUp()'s sums, as a verdict rather than a list of lines: section 6 asks it every month. */
    static boolean sumsAgree(Game g) {
        BuildingManager bm = g.getBuildingManager();
        PopulationManager people = g.getPopulationManager();
        long byStack = 0, homesByStack = 0, placesByStack = 0;
        for (BuildingsTemplate t : bm.getTemplates()) {
            int quantity = bm.getQuantity(t.getId());
            for (JobType j : JobType.values()) byStack += (long) quantity * t.getJobs(j);
            if (t.getCategory() != BuildingType.RESIDENTIAL) continue;
            int per = t.getDwellings() > 0 ? t.getDwellings() : Math.max(1, t.getCapacity() / 4);
            homesByStack += (long) quantity * per;
            placesByStack += (long) quantity * t.getCapacity();
        }
        long byType = 0;
        for (long n : people.getJobs()) byType += n;
        long bySize = 0;
        for (long n : bm.homesBySize()) bySize += n;
        var landlords = g.getSectors().realEstate();
        return everyPost(g) == byStack && byType == people.getTotalJobs()
                && people.getTotalJobs() == everyPost(g) - bm.getPostsWithheld()
                && bm.getTotalHomes() == homesByStack && bySize == bm.getTotalHomes()
                && landlords.getStudioHomes() + landlords.getFamilyHomes() == bm.getTotalHomes()
                && g.getHouseholdCapacity() == 100 + placesByStack;
    }

    /**
     * How far a free copy's affordability pull may stand from the city's in
     * any month (0.7.55): the rents' multiplier on the migrants' target
     * (Migration.affordabilityPull()). Both read 1.0000 in every month of
     * 0.7.55's copies; at 0.7.54 the 5 billion copy's fell to 0.48, 0.24,
     * 0.13 and 0.08 in months three to six while the city's stayed at 1.
     */
    static final double AFFORDABILITY_WITHIN = .01;

    /**
     * THE CITY THE COPIES ARE READ AGAINST (0.7.55): the playtest's town at
     * FREE_FIXTURE_MONTHS, played its own FREE_MONTHS after it was saved -
     * its ground as its first month struck it, its affordability pull each
     * month, the homes it built and its people.
     */
    static final class Original {
        final double crowding, premium, ground, sale, homesBuilt;
        final long peopleBefore, peopleAfter;
        final double[] affordability = new double[FREE_MONTHS];

        Original(Game city) {
            long homes = city.getBuildingManager().getTotalHomes();
            peopleBefore = city.getPopulationManager().getPopulation();
            double c = 0, p = 0, gr = 0, s = 0;
            for (int i = 0; i < FREE_MONTHS; i++) {
                quietly(() -> city.simulateMonths(1));
                if (i == 0) {
                    LandManager land = city.getLandManager();
                    c = land.getCrowding();
                    p = land.getCrowdingPremium();
                    gr = land.getGroundUsdPerSqFt();
                    s = land.getPricePerSqFt();
                }
                affordability[i] = city.getMigration().getLastAffordabilityPull();
            }
            crowding = c; premium = p; ground = gr; sale = s;
            homesBuilt = city.getBuildingManager().getTotalHomes() - homes;
            peopleAfter = city.getPopulationManager().getPopulation();
        }

        /** Whether the city's own rents priced nobody out in any of its months (0.7.62; FREE_FIXTURE_MONTHS). */
        boolean calm() {
            for (double pull : affordability) if (pull != 1) return false;
            return true;
        }
    }

    /**
     * THE CITY'S BOOKS AT TEN BILLION (0.7.54): the identities the harnesses
     * hold a played city to every month, read here at this size, each
     * against the absolute tolerance its harness held it to before 0.7.54 and
     * against MoneyAudit.tolerance() of the figures it is made of. Section 6
     * asserts the second, and prints how often the first would have failed:
     * those are the harness lines that moved to MoneyAudit.tolerance().
     */
    static final class Books {
        /** {worst miss, the largest figure, months the old tolerance missed, misses of the new} for each identity, by name. */
        final java.util.Map<String, double[]> worst = new java.util.LinkedHashMap<>();
        final java.util.Map<String, Double> old = new java.util.LinkedHashMap<>();
        final java.util.Set<String> missedThisMonth = new java.util.HashSet<>();
        double previousClosing = Double.NaN, previousM0 = Double.NaN, journalLeft;
        int arrearsMonths;

        /**
         * One reading of an identity: what it missed by, the size of the
         * figures it is made of, the tolerance its harness held it to before
         * 0.7.54 (oldTolerance; for one already relative, that at this size)
         * and the floor MoneyAudit.tolerance() keeps under it now.
         */
        void miss(String name, double oldTolerance, double newFloor, double miss, double size) {
            old.putIfAbsent(name, oldTolerance);
            double[] w = worst.computeIfAbsent(name, x -> new double[4]);
            double m = Math.abs(miss);
            if (m > oldTolerance) missedThisMonth.add(name);
            w[0] = Math.max(w[0], m);
            w[1] = Math.max(w[1], Math.abs(size));
            if (!(m <= MoneyAudit.tolerance(newFloor, size))) w[3]++;
        }

        void month(Game g, double opening) {
            readings(g);
            for (String name : missedThisMonth) worst.get(name)[2]++;
            missedThisMonth.clear();
        }

        void readings(Game g) {
            MoneyAudit.Result r = g.getLastMoneyAudit();
            CentralBank cb = g.getCentralBank();
            // BankCheck, CreditCheck, EducationCheck, HoldersCheck, MortgageCheck: the month's audit closes, to the cent.
            miss("the month's money audit closes, to the cent (BankCheck and four more)", MoneyAudit.CENT, MoneyAudit.CENT,
                    r.residual, r.moved());
            // MortgageCheck and BankCheck: each sector's cash-flow statement closes - every month
            // since 0.7.55, when the arrears the treasury pays down became a line of it
            // (SectorBooks, arrearsPaid); until then a month with arrears was not read.
            if (g.getArrearsRefusedThisMonth() > 0 || g.getArrearsPaidThisMonth() > 0) arrearsMonths++;
            for (Sector s : g.getSectors().all()) {
                SectorBooks.SectorMonth m = g.getSectorBooks().get(s);
                if (m == null) continue;
                miss("a sector's cash-flow statement closes (MortgageCheck, BankCheck)", 1e-6, 1e-6,
                        m.unexplained(), m.unexplainedScale());
            }
            // MortgageCheck and BankCheck: the bank's equity moved by its income and its named causes.
            if (g.getBank().getBranches() > 0) {
                Bank.EquityMovement em = g.getBank().equityMovement();
                miss("the bank's equity moved by its income and named causes (MortgageCheck, BankCheck)", 1e-6, 1e-6,
                        em.residual(), em.scale());
            }
            // LongPlaytest: the audit's MONEY lines are the central bank's issued less retired.
            double made = cb.getIssued() - cb.getRetired();
            miss("the money the audit saw made is the central bank's (LongPlaytest)", MoneyAudit.CENT, MoneyAudit.CENT,
                    r.moneyMade() - made, Math.abs(r.moneyIn) + Math.abs(r.moneyOut));
            // LongPlaytest: M0 moved by the money made - a cent, or a part in a billion of M0, since 0.7.0.
            if (!Double.isNaN(previousM0)) {
                miss("M0 moved by the money made (LongPlaytest, already relative)",
                        Math.max(MoneyAudit.CENT, 1e-9 * Math.abs(cb.m0())), MoneyAudit.CENT,
                        (cb.m0() - previousM0) - made, Math.max(cb.m0(), previousM0));
            }
            previousM0 = cb.m0();
            // LongPlaytest and MoneyCheck: nothing moved a pool after the strike.
            double pools = 0;
            for (double p : MoneyAudit.pools(g)) pools += Math.abs(p);
            miss("nothing moved after the audit struck (LongPlaytest, MoneyCheck)", MoneyAudit.CENT, MoneyAudit.CENT,
                    g.getPostAuditDrift(), pools);
            // TreasuryCheck: the window, the closing and the bridge.
            double cashSize = Math.max(Math.abs(g.getTreasuryOpening()), Math.abs(g.getTreasuryClosing()));
            if (!Double.isNaN(previousClosing)) {
                miss("the treasury's window opens where it closed (TreasuryCheck)", TreasuryCheck.TOLERANCE,
                        TreasuryCheck.TOLERANCE, g.getTreasuryOpening() - previousClosing, cashSize);
            }
            previousClosing = g.getTreasuryClosing();
            miss("the treasury's closing balance is its cash (TreasuryCheck)", TreasuryCheck.TOLERANCE,
                    TreasuryCheck.TOLERANCE, g.getTreasuryClosing() - g.getCash(), cashSize);
            double bridgeSize = cashSize + Math.abs(g.getTreasurySurplus()) + Math.abs(g.getTreasuryRaised())
                    + Math.abs(g.getTreasuryRepaid());
            miss("the treasury's bridge foots (TreasuryCheck)", TreasuryCheck.TOLERANCE, TreasuryCheck.TOLERANCE,
                    g.getTreasurySurplus() + g.getTreasuryRaised() - g.getTreasuryRepaid()
                            + g.getTreasuryUnexplained() - g.getTreasuryChange(), bridgeSize);
            // ...and what the journal leaves unnamed, read but not held: the copy's bank is resolved in
            // its first month at this size, because the registers the copy leaves unscaled hold the
            // bank's securities (the study's star 1), and the bridge carries that, not rounding.
            journalLeft = Math.max(journalLeft, Math.abs(g.getTreasuryResidual()));
            // HistoryCheck: the history's last month is the month's own, to the history's cent.
            HistorySave h = g.getHistorySave();
            NationalAccounts na = g.getEconomyManager().getNationalAccounts();
            int last = h.months() - 1;
            String[] parts = YearBook.GDP_PARTS;
            double[] live = { na.getConsumption(), na.getInvestment(), na.getGovernment(), na.getNetExports() };
            double sum = 0, size = 0;
            for (int p = 0; p < parts.length; p++) {
                double kept = h.aligned(parts[p])[last];
                sum += kept;
                size = Math.max(size, Math.abs(kept));
                miss("the history's GDP parts are the accounts' own, to the cent (HistoryCheck)", .005 + 1e-9,
                        .005 + 1e-9, kept - live[p], live[p]);
            }
            miss("the history's GDP parts add up to its GDP, to the cent (HistoryCheck)", .025 + 1e-9, .025 + 1e-9,
                    sum - h.aligned("gdp")[last], size);
            for (Sector s : g.getSectors().all()) {
                List<? extends Number> income = h.seriesByName().get(HistorySave.netIncomeKey(s.key()));
                if (income == null || income.isEmpty()) continue;
                double net = g.getSectorBooks().get(s).netIncome();
                miss("the history's net income is the sector books', to the cent (HistoryCheck)", .005 + 1e-9,
                        .005 + 1e-9, income.get(income.size() - 1).doubleValue() - net, net);
            }
            Bank lender = g.getBank();
            String[] bankKeys = { "bankFees", "bankAllowance", "bankProvisions", "bankDividends" };
            double[] bankLive = { lender.feeIncome(), lender.getAllowance(), lender.provisions(), lender.getDividendsPaid() };
            for (int b = 0; b < bankKeys.length; b++) {
                miss("the history's bank figures are the bank's own, to the cent (HistoryCheck)", .005 + 1e-9,
                        .005 + 1e-9, h.aligned(bankKeys[b])[last] - bankLive[b], bankLive[b]);
            }
        }

        void verdicts() {
            out.printf("      (the treasury refused or paid arrears in %d of %d months, its sectors' statements read in"
                    + " every one; the treasury's journal left at most %.3e unnamed, read and not held)%n",
                    arrearsMonths, FREE_MONTHS, journalLeft);
            for (var e : worst.entrySet()) {
                double[] w = e.getValue();
                out.printf("      %s: worst miss %.3e on figures to %.3e; the old tolerance (%.1e) missed in %d of %d months%n",
                        e.getKey(), w[0], w[1], old.get(e.getKey()), (int) w[2], FREE_MONTHS);
                assertTrue("..." + e.getKey().replaceAll(" \\(.*", "") + ", within MoneyAudit.tolerance()",
                        w[3] == 0);
            }
        }
    }

    /** Every order the free copy decides: how big, and how many times it asked the bond desk or read a wait. */
    static final class Orders implements BusinessInvestment.OrderWatch {
        int sized, invested, mortgaged, largestNeeded, largestInvested, largestMortgage, mostWaits, mostDesk;
        int overDesk, overWaits;

        @Override
        public void sized(BuildingsTemplate t, int needed, double siteOutput, int deliverable, int waitsRead) {
            sized++;
            largestNeeded = Math.max(largestNeeded, needed);
            mostWaits = Math.max(mostWaits, waitsRead);
            if (waitsRead > 32 - Integer.numberOfLeadingZeros(Math.max(1, needed)) + 1) overWaits++;
        }

        @Override
        public void invested(BusinessInvestment.Decision d, double cash, double perUnitProfit, Game.Afford found) {
            invested++;
            largestInvested = Math.max(largestInvested, d.quantity);
            mostDesk = Math.max(mostDesk, found.deskCalls());
            if (found.deskCalls() > Game.deskCallsMost(d.quantity)) overDesk++;
        }

        @Override
        public void mortgaged(int asked, java.util.function.IntToDoubleFunction costOf, double cash,
                              double noiPerUnit, double annualRate, Mortgage.Decision found) {
            mortgaged++;
            largestMortgage = Math.max(largestMortgage, asked);
        }

        void verdicts() {
            out.printf("      orders: %,d sized (the largest needed %,d, at most %d waits read), %,d judged on their"
                    + " interest (the largest %,d, at most %d asks of the bond desk), %,d on a mortgage (the largest %,d)%n",
                    sized, largestNeeded, mostWaits, invested, largestInvested, mostDesk, mortgaged, largestMortgage);
            same("no order sized read more waits than one a bit of what it needed, and one more", overWaits, 0);
            same("no order judged on its interest asked the bond desk more than Game.deskCallsMost() times", overDesk, 0);
        }
    }

    /**
     * Every way of adding up the posts, the doors and the household places
     * agrees, to the last one - a long sum of K-times counts is exact where an
     * int sum would have wrapped.
     */
    static void addsUp(Game g, String when) {
        BuildingManager bm = g.getBuildingManager();
        PopulationManager people = g.getPopulationManager();

        long byStack = 0, homesByStack = 0, placesByStack = 0;
        for (BuildingsTemplate t : bm.getTemplates()) {
            int quantity = bm.getQuantity(t.getId());
            for (JobType j : JobType.values()) byStack += (long) quantity * t.getJobs(j);
            if (t.getCategory() != BuildingType.RESIDENTIAL) continue;
            int per = t.getDwellings() > 0 ? t.getDwellings() : Math.max(1, t.getCapacity() / 4);
            homesByStack += (long) quantity * per;
            placesByStack += (long) quantity * t.getCapacity();
        }
        same("every post, " + when + ", is the stacks' quantity times their posts", everyPost(g), byStack);
        long byType = 0;
        for (long n : people.getJobs()) byType += n;
        same("...the posts on offer by job type add up to the total", byType, people.getTotalJobs());
        same("...and the total is every post less those withheld", people.getTotalJobs(),
                everyPost(g) - bm.getPostsWithheld());

        long bySize = 0;
        for (long n : bm.homesBySize()) bySize += n;
        same("every home is the stacks' quantity times their doors", bm.getTotalHomes(), homesByStack);
        same("...and the homes by size add up to it", bySize, bm.getTotalHomes());
        var landlords = g.getSectors().realEstate();
        same("...and the landlords' studio and family doors to it too",
                landlords.getStudioHomes() + landlords.getFamilyHomes(), bm.getTotalHomes());
        same("the household places are the city's 100 and the stacks' places", g.getHouseholdCapacity(),
                100 + placesByStack);
    }

    /** Every post the buildings have, offered or not. */
    static long everyPost(Game g) {
        long n = 0;
        for (JobType j : JobType.values()) n += g.getBuildingManager().getTotalJobsAtEveryPost(j);
        return n;
    }

    static long last(List<Long> series) {
        return series.isEmpty() ? -1 : series.get(series.size() - 1);
    }

    static boolean quietlyGet(java.util.function.BooleanSupplier s) {
        boolean[] r = new boolean[1];
        quietly(() -> r[0] = s.getAsBoolean());
        return r[0];
    }

    /* =====================================================================
       THE STUDY'S SCALING (scale_save.py, 2026-10-06), on a save's JSON

       Extensive figures times K, a whole number written back as a whole
       number; names that are intensive (prices, rates, months, ids, shares,
       ratios and targets) are left alone wherever they are met inside the
       sectors, markets, debts and books. The households' cells, the
       families, migration's wage bills, the foreign accounts and the central
       bank are arrays read by position, scaled at the positions the study
       named for 0.7.52's layouts.

       ONE KEY THE STUDY SCALED IS LEFT ALONE: the city's own works yard
       (constructionMaterials). It is not the people's; it fills at
       BuildingManager.BASE_MATERIALS a month whatever the city's size, and
       holds 36 units in Jerus's city of 509,455. A young fixture's yard is
       thousands of units, and a fixture of a few hundred people needs K in
       the millions, so scaled it passes 2^31 where no real city's would -
       and it is an int, as a count of materials may stay.
       ===================================================================== */

    /**
     * A name the scaler leaves alone wherever it meets one, unless EXTENSIVE
     * names it: a price, a rate, a month, an id, a share, a ratio or a
     * target, as scale_save.py's SKIP read them, and since 0.7.54 the seven
     * ratios at its end.
     */
    static final Pattern INTENSIVE = Pattern.compile("(price|Price|rate|Rate|month|Month|Months|share|Share|ratio|Ratio|target|Target|"
            + "renewals|duration|Left$|^id$|Id$|factor|Factor|insured|key|sector|good|type|building|known|Known|"
            + "^leverage$|Margin$|^quote$|^tightness$|^utilisation$|Multiple$|StruckOn$)");

    /*
     * THE NAMES THE PATTERN READ WRONG (0.7.54). The scaled copy's first month
     * with its sectors free showed an audit residual of 2.7e9 in 0.7.53, and
     * it was the scaler's: exactly 375.219355 x (K - 1) at every K tried, and
     * 375.219355 is the builders' recognisedThisMonth in the founded city's
     * save. "Month" in its name kept it the founded
     * city's while the order book it is recognised from was K times over,
     * and the first month booked the difference as money from nowhere. So
     * the money and the quantities whose names look intensive are scaled
     * (EXTENSIVE), and the ratios whose names did not look it are left alone
     * (the pattern's last seven: a book's leverage, a margin, the railway's
     * quote and tightness, the builders' utilisation and the fill they
     * struck on, the shops' scarcity multiple).
     */
    static final java.util.Set<String> EXTENSIVE = java.util.Set.of("recognisedThisMonth", "escalationThisMonth",
            "repairsThisMonth", "lastMonthSales", "demandAtPrice", "buildings", "sharesBoughtBack", "zeroRated");

    /** The save's top-level keys scaled: scale_save.py's list, less the works yard. */
    static final String[] TOP = { "cash", "householdSavings", "landOwned", "insurancePremiums", "propertyTaxCharged",
            "rentWeight", "rentWeightStudio", "bankCash", "cityMaintenancePaid", "bankProfitLastMonth",
            "bankTaxCharged", "population", "workforce", "landBlocksPurchased",
            "materialsConsumed", "buildings", "underConstructionById", "constructionProgressById",
            "materialsOwedById", "contractValueById", "cohorts", "skilledWorkforce", "housingOccupancy",
            "populationTrend", "treasuryJournalAmounts", "governmentMonth", "householdStatement",
            "sectors", "markets", "businessDebts", "sectorBooks", "sectorBooksBefore", "creditStatements",
            "salesTax", "mortgageRepaid", "insuranceClaims", "subsidyPaid", "writeOffTotals",
            // ...and the drivers' fuel as 6d drew it (0.7.62): the bill, its imported part and the litres, all
            // three the drivers' - until 0.7.62 the load derived it from the drivers, which the cells scale.
            "householdFuel",
            // ...and the graduates waiting for the next census and the last census's (0.7.63): people.
            "householdGraduates" };

    /** The five TOP keys scale_save.py handed sc() under a name of their own: the sectors, the markets, the business debts and the books, this month's and last's. */
    static final java.util.Set<String> NAMED = java.util.Set.of("sectors", "markets", "businessDebts", "sectorBooks",
            "sectorBooksBefore");

    static void scale(Path in, Path to, long k) throws Exception {
        JsonObject d = com.google.gson.JsonParser.parseString(Files.readString(in)).getAsJsonObject();
        for (String key : TOP) {
            if (d.has(key)) d.add(key, sc(d.get(key), NAMED.contains(key) ? "x" : "", k));
        }
        // The land's books (0.7.67): each holding's five areas and its forest's timber, K times over, as the
        // square feet are - its blocks, its fields, its iron and its offers the founded city's.
        int timber = CityLand.AREAS + CityLand.KINDS + Resource.FOREST.ordinal();
        if (d.has("landCentre")) {
            JsonArray a = d.getAsJsonArray("landCentre");
            for (int i = 0; i < CityLand.AREAS; i++) a.set(i, times(a.get(i), k));
            a.set(timber, times(a.get(timber), k));
        }
        if (d.has("landHoldings")) {
            int at = LandParcel.PURCHASE_FIELDS - 2 * CityLand.KINDS - CityLand.AREAS - 2;
            for (JsonElement row : d.getAsJsonArray("landHoldings")) {
                JsonArray a = row.getAsJsonArray();
                for (int i = 0; i < CityLand.AREAS; i++) a.set(at + i, times(a.get(at + i), k));
                a.set(at + timber, times(a.get(at + timber), k));
            }
        }
        // The treasury's month: five money figures and a flag.
        if (d.has("treasuryMonth")) {
            JsonArray a = d.getAsJsonArray("treasuryMonth");
            for (int i = 0; i < Math.min(5, a.size()); i++) a.set(i, times(a.get(i), k));
        }
        // The households' cells: per-household money stays; the households
        // (slot 3) scale; shares, the city's paper and the bonds are per
        // household and scale by 1/K; then the three city figures.
        JsonArray keys = d.getAsJsonArray("householdCellKeys");
        JsonArray cells = d.getAsJsonArray("householdCells");
        int n = keys.size();
        int slots = (cells.size() - 3) / n;
        int companies = d.has("equityKeys") ? d.getAsJsonArray("equityKeys").size() : 0;
        for (int c = 0; c < n; c++) {
            int b = c * slots;
            cells.set(b + 3, times(cells.get(b + 3), k));
            for (int j = 0; j < companies; j++) cells.set(b + 8 + j, over(cells.get(b + 8 + j), k));
            cells.set(b + 8 + companies + 6, over(cells.get(b + 8 + companies + 6), k));
            cells.set(b + 8 + companies + 7, over(cells.get(b + 8 + companies + 7), k));
        }
        for (int j = 0; j < 3; j++) cells.set(n * slots + j, times(cells.get(n * slots + j), k));
        // The families: the household matrix and its counters.
        if (d.has("families")) {
            JsonArray a = d.getAsJsonArray("families");
            for (int i = 0; i < a.size(); i++) a.set(i, times(a.get(i), k));
        }
        // Migration: a year of each of six tiers' real wage bill.
        if (d.has("migration")) {
            JsonArray a = d.getAsJsonArray("migration");
            for (int i = 0; i < Math.min(72, a.size()); i++) a.set(i, times(a.get(i), k));
        }
        // The foreign accounts' money slots, not the rate, the ratios or the months.
        if (d.has("foreignAccounts")) {
            JsonArray a = d.getAsJsonArray("foreignAccounts");
            java.util.Set<Integer> money = new java.util.HashSet<>(List.of(0, 1, 4, 5, 6, 7, 8, 9, 10, 15, 17, 19, 20,
                    21, 22, 23, 24, 25, 26, 27));
            for (int i = 31; i < a.size(); i++) money.add(i);
            for (int i = 0; i < a.size(); i++) if (money.contains(i)) a.set(i, times(a.get(i), k));
        }
        // The central bank's money, not its revenue cursor or its target.
        if (d.has("centralBank")) {
            JsonArray a = d.getAsJsonArray("centralBank");
            java.util.Set<Integer> skip = java.util.Set.of(11, 12, 13 + 12 + 4);
            for (int i = 0; i < a.size(); i++) if (!skip.contains(i)) a.set(i, times(a.get(i), k));
        }
        d.addProperty("slotName", "scaled x" + k);
        Files.createDirectories(to.getParent());
        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
        Files.writeString(to, gson.toJson(d));
    }

    static JsonElement sc(JsonElement v, String name, long k) {
        if (v == null || v.isJsonNull()) return v;
        if (v.isJsonPrimitive()) {
            JsonPrimitive p = v.getAsJsonPrimitive();
            if (!p.isNumber()) return v;
            if (!name.isEmpty() && !EXTENSIVE.contains(name) && INTENSIVE.matcher(name).find()) return v;
            return times(v, k);
        }
        if (v.isJsonArray()) {
            JsonArray a = v.getAsJsonArray(), r = new JsonArray();
            for (JsonElement e : a) r.add(sc(e, name, k));
            return r;
        }
        JsonObject o = v.getAsJsonObject(), r = new JsonObject();
        for (var e : o.entrySet()) r.add(e.getKey(), sc(e.getValue(), e.getKey(), k));
        return r;
    }

    /** A number times K: a whole number stays whole and exact, a fraction is a double. */
    static JsonElement times(JsonElement v, long k) {
        if (v == null || !v.isJsonPrimitive() || !v.getAsJsonPrimitive().isNumber()) return v;
        String s = v.getAsString();
        if (s.matches("-?\\d+")) return new JsonPrimitive(new BigDecimal(s).multiply(BigDecimal.valueOf(k)).longValueExact());
        return new JsonPrimitive(v.getAsDouble() * k);
    }

    static JsonElement over(JsonElement v, long k) {
        if (v == null || !v.isJsonPrimitive() || !v.getAsJsonPrimitive().isNumber()) return v;
        return new JsonPrimitive(v.getAsDouble() / k);
    }

    static void cleanUp(Path root) {
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }
    }
}

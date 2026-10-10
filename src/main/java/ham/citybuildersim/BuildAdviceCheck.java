package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * The Build overview's advice (0.7.24): BuildAdvice's rule held to NEEDS
 * YOU's list, to the quote, and to the model's own figures, in a played
 * city - and the categories it is filed under.
 *
 * WHY. WHAT WOULD HELP MOST tells a player what to build and how many, and
 * each card's Build button spends the treasury on that. A rule that read a
 * need NEEDS YOU does not list, counted past what closes it, quoted a price
 * the Build button would not charge, or promised a figure the month would
 * not reach would be a confident wrong answer on the front page of the game.
 * It is advice, not a model change, so nothing in the default playtest
 * reaches it: this is the only place it is played.
 *
 * What this has to prove:
 *   1. THE CATEGORIES: the strip's fourteen, each once, the five renamed by
 *      the old label still landing on the new; every building in exactly one.
 *   2. NEEDS YOU'S NEEDS, IN ITS ORDER: every suggestion is a need NEEDS YOU
 *      lists, one a city-built building answers, in the list's order, at
 *      most three and one per need - and a need it passed over before the
 *      last one it took is one what is on site already answers, or one no
 *      building can help.
 *   3. THE COUNT AND THE PRICE (0.7.51): the count is the least that keeps
 *      the need ahead at its projection after what is on site, at the
 *      staffed capacity the model counts - one fewer does not; the price is
 *      Game.quoteBuild() for it, to the bit; and the building fits the land
 *      the cards before it leave, or none that closes the need does.
 *      3b. THE CASH, NO CAP: every count is what keeps its need ahead
 *      whatever the cash, and a card is on credit exactly when the cash the
 *      ones before left is short of its quote - by that much; with no cash,
 *      every one, its count unchanged. (Until 0.7.51 the count was cut to
 *      what the cash left afforded.)
 *   4. BEFORE AND AFTER, BY THE MODEL: in a twin of the city, the order and
 *      what is on site built standing and the month's own services pass run
 *      on it, every suggestion's figure is the one the model reads - and the
 *      same for an order of every measure a city category opens on.
 *   5. THE STAFFING WEIGHTING: in a city short of one job type, of two
 *      buildings alike in everything but how many of those posts they have,
 *      the advice takes the one with fewer.
 *   6. NOTHING NEEDED, NOTHING SUGGESTED: a city NEEDS YOU lists no
 *      city-built need for gets no suggestion.
 *   7. LAND COUNTED (0.7.51): a building's price a unit is its quote for one
 *      and its ground at landValue(), over what it serves, to the bit, on
 *      the land the cards before leave - a road's with its running over its
 *      life since 0.7.70 (BuildAdvice.lifetime(); RoadCheck holds the rest);
 *      with no ground free, the land office's prices at nothing pick the
 *      road that is cheapest to build and keep, and at ten times the price
 *      past which the road that needs the least ground a trip is the
 *      cheapest with it, that road.
 *   8. SLACK BUILT: a served card is at 100% of its demand projected and
 *      SLACK past it, and its count is never fewer than the old rule's.
 *   9. NO HIGHER EDUCATION WITHOUT ITS PIPELINE: a college or university row
 *      wants no more than would come and no more than would be hired, and a
 *      first school half the smallest; a city with students and no posts
 *      for them gets no row and no card until the posts come; and the
 *      playtest's month-9 university is gone.
 *  10. SIZED TO THE PROJECTION: a card's growth is the businesses'
 *      growthFactor() over its wait and their horizon, to the bit, and a
 *      rising population never orders fewer.
 *  11. THE RUN: "Build all three" places the cards' orders in their order,
 *      goes all the way exactly when no card is short of land and otherwise
 *      stops at the first that is, for land; and what it charges is the
 *      header's total, Game.buildRunInvoice().
 *
 * Every fixture causes its condition.
 */
public class BuildAdviceCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-88s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= Math.max(tol, Math.abs(expected) * tol);
        if (!ok) fails++;
        out.printf("%-88s %s  %,.9f against %,.9f%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    static void bits(String label, double actual, double expected) {
        boolean ok = Double.doubleToLongBits(actual) == Double.doubleToLongBits(expected);
        if (!ok) fails++;
        out.printf("%-88s %s  %s against %s%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    static BuildingsTemplate template(Game g, String name) {
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException("no template named " + name);
    }

    /**
     * A played city short of the city's works: about four thousand people in
     * nine hundred houses on one paved road, no clinic or daycare (the
     * founding's own care only), a school of each basic stage, a college, a
     * university and four medical schools
     * (so doctors are short: the schools take more than the city has), two
     * police stations and home care - every sector held, so nothing is built
     * but by this fixture's hand - played three years; then on site a gravel
     * road and three small childcare centres of the city's own (three Home
     * Daycares until 0.7.71 resized them), and with onSiteClinic
     * a clinic too, so the advice has something on site to count. Measured
     * (the calibration probe): general care at 28% (red; amber with the
     * clinic on site), crime red at 2.7x Canada's, the roads near 400% of
     * capacity, childcare 26%, a person caught and not held - and until
     * 0.7.51 the law school red with no seats for 28, a row NEEDS YOU no
     * longer lists: it wants no more students than the city would hire.
     */
    static Game shortCity(Path root, String name, boolean onSiteClinic) {
        return shortCity(root, name, onSiteClinic, Set.of());
    }

    /** ...without the buildings named in `without` (section 9: the short city with no university). */
    static Game shortCity(Path root, String name, boolean onSiteClinic, Set<String> without) {
        GameFiles files = new GameFiles(root.resolve(name), root.resolve(name + "-no-legacy"));
        Game g = new Game(files);
        quietly(() -> {
            g.run();
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 80_000_000L);
            for (Sector s : g.getSectors().all()) g.getBusinessInvestment().holdSector(s.key());
            for (String[] w : new String[][] {
                    { "Industrial Bakery", "4" }, { "Coal Power Plant", "1" }, { "Water Treatment Plant", "1" },
                    { "Commercial Bank", "1" }, { "Convenience Store", "10" }, { "House", "900" },
                    { "Elementary School", "1" }, { "Middle School", "1" }, { "High School", "1" },
                    { "Community College", "1" }, { "University", "1" }, { "Medical School", "4" },
                    { "Construction Depot", "4" }, { "Paved Road", "1" }, { "Police Station", "2" },
                    { "Home Care Service", "2" } }) {
                if (without.contains(w[0])) continue;
                g.buildStack(template(g, w[0]), Integer.parseInt(w[1]), true);
            }
            g.simulateMonths(36);
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.buildStack(template(g, "Gravel Road"), 1, false);
            g.buildStack(template(g, "Small Childcare Centre"), 3, false);
            if (onSiteClinic) g.buildStack(template(g, "Walk-in Clinic"), 1, false);
        });
        return g;
    }

    /**
     * A smaller city well served: every measure NEEDS YOU watches for a
     * city-built building past its line - care of every kind, a school of
     * every basic stage, a college, a university and a medical school with
     * seats for who would come, police, highways - played two years.
     * Measured: nothing listed but the pensions and the budget.
     */
    static Game servedCity(Path root, String name) {
        GameFiles files = new GameFiles(root.resolve(name), root.resolve(name + "-no-legacy"));
        Game g = new Game(files);
        quietly(() -> {
            g.run();
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 200_000_000L);
            for (Sector s : g.getSectors().all()) g.getBusinessInvestment().holdSector(s.key());
            for (String[] w : new String[][] {
                    { "Industrial Bakery", "2" }, { "Coal Power Plant", "1" }, { "Water Treatment Plant", "1" },
                    { "Commercial Bank", "1" }, { "Convenience Store", "4" }, { "House", "300" },
                    { "Construction Depot", "2" }, { "Elevated Highway", "3" },
                    { "Community Health Centre", "1" }, { "Childcare Centre", "2" }, { "Home Care Service", "2" },
                    { "Elementary School", "1" }, { "Middle School", "1" }, { "High School", "1" },
                    { "Community College", "1" }, { "University", "1" }, { "Medical School", "1" },
                    { "Police Station", "1" } }) {
                g.buildStack(template(g, w[0]), Integer.parseInt(w[1]), true);
            }
            g.simulateMonths(24);
        });
        return g;
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        Path root = Files.createTempDirectory("buildadvicecheck");

        theCategories(root);
        Game g = shortCity(root, "short", true);
        quietly(() -> g.saveGame(10, "buildadvicecheck"));
        List<CityNeeds.Need> all = CityNeeds.measure(g, CityNeeds.PLAIN);
        List<BuildAdvice.Suggestion> advice = BuildAdvice.suggest(g, all);
        printCity(g, all, advice);
        needsYousOrder(g, all, advice);
        theCountAndThePrice(g, advice);
        theCash(root);
        beforeAndAfter(root, g, advice);
        everyMeasure(root, g);
        theStaffingWeighting(root);
        nothingNeeded(root);
        landCounted(root);
        slackBuilt(g, advice);
        thePipeline(root, g, all);
        sizedToTheProjection(root);
        theRun(root, g, advice);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** The city as the advice read it, for the record. */
    static void printCity(Game g, List<CityNeeds.Need> all, List<BuildAdvice.Suggestion> advice) {
        out.printf("%n      the fixture: month %d, %,d people, cash %s%n", g.getMonth(),
                g.getPopulationManager().getPopulation(), Formats.INSTANCE.amount(g.getCash()));
        for (CityNeeds.Need n : CityNeeds.biting(all)) {
            out.printf("      NEEDS YOU %s %-16s %s%n", n.level() == 2 ? "red  " : "amber", n.label(), n.reading());
        }
        for (BuildAdvice.Suggestion s : advice) {
            out.printf("      suggests %s: %,d x %s for %s, %.4f -> on site %.4f -> %.4f (%.4f at opening: %.1f mo, k %.4f)%n",
                    s.need().label(), s.count(), s.template().getName(),
                    Formats.INSTANCE.amount(s.price()), s.before(), s.whenOnSite(), s.after(), s.afterAtOpening(),
                    s.ahead().months(), s.ahead().k());
        }
    }

    /* ============================ 1. THE CATEGORIES ============================ */

    static void theCategories(Path root) {
        out.println("--- 1. the categories: fourteen, each once; every building in exactly one ---");
        List<BuildAdvice.Category> cats = BuildAdvice.categories();
        assertTrue("the strip has fourteen categories", cats.size() == 14);
        Set<String> names = new HashSet<>();
        for (BuildAdvice.Category c : cats) names.add(c.name());
        assertTrue("...each named once", names.size() == cats.size());
        int city = 0;
        for (BuildAdvice.Category c : cats) if (c.cityBuilds()) city++;
        assertTrue("five only the city builds, and they come first", city == 5
                && cats.get(0).cityBuilds() && cats.get(4).cityBuilds() && !cats.get(5).cityBuilds());
        String[][] renamed = { {"Residential", "Homes"}, {"Commercial", "Shops"}, {"Industrial", "Industry"},
                {"Infrastructure", "Roads & transit"}, {"Services", "Offices"} };
        for (String[] r : renamed) {
            BuildAdvice.Category c = BuildAdvice.category(r[0]);
            assertTrue("\"" + r[0] + "\" lands on \"" + r[1] + "\", and is no name of its own",
                    c != null && c.name().equals(r[1]) && !names.contains(r[0]));
        }
        assertTrue("the Overview is no category", BuildAdvice.category(BuildAdvice.OVERVIEW) == null);
        // Every kind of building in exactly one category.
        boolean once = true;
        for (BuildingType type : BuildingType.values()) {
            int in = 0;
            for (BuildAdvice.Category c : cats) if (c.types().contains(type)) in++;
            if (in != 1) { once = false; out.println("      " + type + " is in " + in); }
        }
        assertTrue("every BuildingType is in exactly one category", once);
        Game g = new Game(new GameFiles(root.resolve("catalogue"), root.resolve("catalogue-no-legacy")));
        BuildingManager bm = g.getBuildingManager();
        if (bm.getTemplates().isEmpty()) bm.initializeTemplates();
        boolean every = !bm.getTemplates().isEmpty();
        for (BuildingsTemplate t : bm.getTemplates()) {
            int in = 0;
            for (BuildAdvice.Category c : cats) if (c.types().contains(t.getCategory())) in++;
            every &= in == 1;
        }
        assertTrue("every building in the catalogue (" + bm.getTemplates().size() + ") sits in exactly one", every);
        assertTrue("investors build the market's nine, by BusinessInvestment's own list - the city's sea terminals (PORTS,"
                        + " 0.7.86) filed beside them in Industry - and none of the city's five",
                checkInvestors(cats));
    }

    static boolean checkInvestors(List<BuildAdvice.Category> cats) {
        EnumSet<BuildingType> theirs = EnumSet.of(BuildingType.RESIDENTIAL, BuildingType.COMMERCIAL,
                BuildingType.INDUSTRIAL, BuildingType.HEAVY_INDUSTRY, BuildingType.MINING, BuildingType.CONSTRUCTION,
                BuildingType.BUSINESS_SERVICES, BuildingType.AGRICULTURE, BuildingType.RAIL, BuildingType.AUTOMOTIVE,
                BuildingType.LUXURY, BuildingType.HOSPITALITY);
        for (BuildAdvice.Category c : cats) {
            // The city's sea terminals are filed in Industry as their own group (0.7.86, BuildCard's "Ports"), as
            // its Strategic Reserve is among the heavy industry: city buildings on a market page, which only the
            // city builds (BuildCard.citys()). Every other type on a market page is one investors build.
            EnumSet<BuildingType> types = c.types();
            types.remove(BuildingType.PORTS);
            boolean market = theirs.containsAll(types);
            if (market == c.cityBuilds()) return false;
            if (c.types().contains(BuildingType.PORTS) && !c.name().equals(BuildAdvice.INDUSTRY)) return false;
        }
        return true;
    }

    /* ============================ 2. NEEDS YOU'S NEEDS, IN ITS ORDER ============================ */

    static void needsYousOrder(Game g, List<CityNeeds.Need> all, List<BuildAdvice.Suggestion> advice) {
        out.println("\n--- 2. NEEDS YOU's needs, in its order: at most three, one per need ---");
        List<CityNeeds.Need> biting = CityNeeds.biting(all);
        int cityBuilt = 0;
        for (CityNeeds.Need n : biting) if (n.cityBuilds()) cityBuilt++;
        assertTrue("fixture: NEEDS YOU lists more than three needs a city-built building answers (" + cityBuilt + ")",
                cityBuilt > 3);
        assertTrue("fixture: and something on site answers one of them", onSiteAnswers(g, biting));
        assertTrue("the advice is three suggestions", advice.size() == BuildAdvice.MAX_SUGGESTIONS);
        int last = -1;
        boolean inOrder = true, listed = true, built = true;
        Set<CityNeeds.Need> seen = new HashSet<>();
        for (BuildAdvice.Suggestion s : advice) {
            int at = biting.indexOf(s.need());
            listed &= at >= 0;
            inOrder &= at > last;
            last = at;
            built &= s.need().cityBuilds();
            seen.add(s.need());
        }
        assertTrue("every suggestion is a need NEEDS YOU lists", listed);
        assertTrue("...one a city-built building answers", built);
        assertTrue("...in NEEDS YOU's order, reds first", inOrder);
        assertTrue("...one per need", seen.size() == advice.size());
        boolean passedFairly = true;
        for (int i = 0; i < last; i++) {
            CityNeeds.Need n = biting.get(i);
            if (!n.cityBuilds() || seen.contains(n)) continue;
            BuildAdvice.Measure m = BuildAdvice.measureOf(n);
            // Since 0.7.51 what is on site has to keep it ahead of an order placed now, not just off the list today.
            boolean onSiteCloses = BuildAdvice.ahead(g, m, BuildAdvice.onSite(g, m), BuildAdvice.opening(g, 0));
            boolean noneHelps = true;
            for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
                boolean cand = m.kind() == BuildAdvice.Kind.ROADS ? t.getCategory() == BuildingType.INFRASTRUCTURE : m.serves(t);
                if (cand && BuildAdvice.unit(g, m, t) > 0) noneHelps = false;
            }
            out.printf("      passed over %s: on site closes it %s, no building helps %s%n", n.label(), onSiteCloses, noneHelps);
            passedFairly &= onSiteCloses || noneHelps;
        }
        assertTrue("a need passed over before the last taken is one on site answers, or none can help", passedFairly);
    }

    /** Whether any biting city-built need has something on site that serves it. */
    static boolean onSiteAnswers(Game g, List<CityNeeds.Need> biting) {
        for (CityNeeds.Need n : biting) {
            BuildAdvice.Measure m = BuildAdvice.measureOf(n);
            if (m != null && BuildAdvice.units(BuildAdvice.onSite(g, m)) > 0) return true;
        }
        return false;
    }

    /* ============================ 3. THE COUNT AND THE PRICE ============================ */

    static void theCountAndThePrice(Game g, List<BuildAdvice.Suggestion> advice) {
        out.println("\n--- 3. the count keeps the need ahead at its projection; one fewer does not; the price is the quote;"
                + " it fits the land the cards before leave, or no building that closes it does ---");
        boolean anyOnSite = false;
        double landLeft = g.getLandManager().getAvailableSqFt();
        for (BuildAdvice.Suggestion s : advice) {
            BuildAdvice.Measure m = s.measure();
            Map<BuildingsTemplate, Integer> site = BuildAdvice.onSite(g, m);
            anyOnSite |= BuildAdvice.units(site) > 0;
            String what = s.need().label() + ", " + s.template().getName();
            assertTrue(what + ": not on credit - the Wealthy city's cash covers its quote", !s.needsCredit() && s.credit() == 0);
            assertTrue(what + ": the count keeps it ahead at its projection",
                    s.closes() && BuildAdvice.ahead(g, m, BuildAdvice.plus(site, s.template(), s.count()), s.ahead()));
            assertTrue(what + ": ...and one fewer does not",
                    !BuildAdvice.ahead(g, m, BuildAdvice.plus(site, s.template(), s.count() - 1), s.ahead()));
            bits(what + ": the price is Game.quoteBuild() for the count", s.price(), g.quoteBuild(s.template(), s.count()).total);
            bits(what + ": before is the figure NEEDS YOU read", s.before(), s.need().value());
            assertTrue(what + ": it fits the land the cards before leave, or no building that closes it does",
                    fitsOrNone(g, s, landLeft));
            // The staffed capacity it counted at is the model's: one more of it, in the model's own sum.
            if (m.kind() != BuildAdvice.Kind.ROADS) {
                close(what + ": a unit at today's staffing is the model's own sum, one more standing",
                        unitByTheModel(twin(g), m, s.template()), s.unit(), 1e-9);
            }
            landLeft = Math.max(0, landLeft - s.landSqFt());
        }
        assertTrue("fixture: at least one suggestion counted what is on site", anyOnSite);
        // BuildAdvice.quoteTotal() (0.7.38): the model adds the quotes up, not the screen.
        double each = 0;
        for (BuildAdvice.Suggestion s : advice) each += g.quoteBuild(s.template(), s.count()).total;
        assertTrue("fixture: more than one suggestion to add up", advice.size() > 1);
        bits("the suggestions' quotes, added, are each one's Game.quoteBuild() in their order",
                BuildAdvice.quoteTotal(advice), each);
    }

    /**
     * The land rule: the chosen building's whole count fits the ground the
     * cards before it leave, unless none that keeps the need ahead would -
     * each counted as the advice counts it, at today's demand with the slack
     * for its wait and then at the demand it opens to.
     */
    static boolean fitsOrNone(Game g, BuildAdvice.Suggestion s, double landLeft) {
        if (s.template().getLandSqFt() * (double) s.count() <= landLeft) return true;
        BuildAdvice.Measure m = s.measure();
        Map<BuildingsTemplate, Integer> site = BuildAdvice.onSite(g, m);
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            boolean cand = m.kind() == BuildAdvice.Kind.ROADS ? t.getCategory() == BuildingType.INFRASTRUCTURE : m.serves(t);
            if (!cand || !(BuildAdvice.unit(g, m, t) > 0)) continue;
            int[] today = BuildAdvice.count(g, m, site, t, new BuildAdvice.Ahead(0, 1, BuildAdvice.SLACK));
            if (today[0] <= 0) continue;
            int[] c = BuildAdvice.count(g, m, site, t, BuildAdvice.opening(g, g.quoteBuild(t, today[0]).months));
            if (c[1] == 1 && t.getLandSqFt() * (double) c[0] <= landLeft) return false;
        }
        return true;
    }

    /**
     * One building's staffed capacity as the model counts it: the model's
     * getter with one more of it standing in a twin, less the getter before.
     */
    static double unitByTheModel(Game twin, BuildAdvice.Measure m, BuildingsTemplate t) {
        BuildingManager bm = twin.getBuildingManager();
        double[] fill = twin.getPopulationManager().getJobFillRate();
        BuildingsTemplate mine = template(twin, t.getName());
        switch (m.kind()) {
            case CARE: {
                double before = bm.getStaffedCareCapacity(m.care(), fill);
                bm.addStack(mine, 1, true);
                return bm.getStaffedCareCapacity(m.care(), fill) - before;
            }
            case SCHOOL: {
                double before = bm.getStaffedEducationPlaces(fill)[m.school().ordinal()];
                bm.addStack(mine, 1, true);
                return bm.getStaffedEducationPlaces(fill)[m.school().ordinal()] - before;
            }
            case POLICE: case CELLS: {
                SafetyType kind = m.kind() == BuildAdvice.Kind.POLICE ? SafetyType.POLICE : SafetyType.PRISON;
                double before = bm.getStaffedSafetyCapacity(kind, fill);
                bm.addStack(mine, 1, true);
                return bm.getStaffedSafetyCapacity(kind, fill) - before;
            }
            case POWER: case WATER: {
                // The utilities' generation, one more standing, at that building's own staffing.
                return mine.getProduction1() * BuildAdvice.staffing(mine, fill);
            }
            default:
                return BuildAdvice.unit(twin, m, mine);
        }
    }

    /**
     * THE CASH, NO CAP (0.7.51): with the cash for the first card and half
     * the second, every card is the count that keeps its need ahead, as with
     * the Wealthy cash; the second and after are on credit for exactly what
     * the cash the ones before left does not cover; with none, every one.
     * Until 0.7.51 this section held the cap: the largest cut to what the
     * cash left afforded (BuildAdvice's THE SUGGESTIONS says why it went).
     */
    static void theCash(Path root) {
        out.println("\n--- 3b. the cash: no cap - every count is what keeps it ahead, and a card is on credit exactly"
                + " when the cash the ones before left is short of its quote ---");
        Game g = shortCity(root, "short-cash", true);
        List<BuildAdvice.Suggestion> rich = BuildAdvice.suggest(g);
        assertTrue("fixture: more than one suggestion (" + rich.size() + ")", rich.size() > 1);
        if (rich.size() < 2) return;
        double cash = rich.get(0).price() + rich.get(1).price() / 2;
        g.setCashForTest(cash);
        List<BuildAdvice.Suggestion> part = BuildAdvice.suggest(g);
        assertTrue("with the cash for the first and half the second, every card is the same building and count as with plenty",
                sameOrders(rich, part));
        assertTrue("fixture: so the first is not on credit and the second is",
                part.size() > 1 && !part.get(0).needsCredit() && part.get(1).needsCredit());
        double left = cash;
        boolean exactly = true, amounts = true;
        for (BuildAdvice.Suggestion s : part) {
            exactly &= s.needsCredit() == (s.price() > left);
            double owed = s.needsCredit() ? s.price() - Math.max(0, left) : 0;
            amounts &= Double.doubleToLongBits(s.credit()) == Double.doubleToLongBits(owed);
            left = Math.max(0, left - s.price());
        }
        assertTrue("...a card is on credit exactly when its quote is more than the cash the ones before left", exactly);
        assertTrue("...and what is on credit is its quote less that cash, to the bit", amounts);
        g.setCashForTest(0);
        List<BuildAdvice.Suggestion> none = BuildAdvice.suggest(g);
        boolean credit = sameOrders(rich, none);
        for (BuildAdvice.Suggestion b : none) {
            credit &= b.needsCredit() && Double.doubleToLongBits(b.credit()) == Double.doubleToLongBits(b.price());
        }
        assertTrue("with no cash, every card is on credit for its whole quote, its count unchanged", credit);
    }

    /** The same buildings, in the same counts, in the same order. */
    static boolean sameOrders(List<BuildAdvice.Suggestion> a, List<BuildAdvice.Suggestion> b) {
        if (a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i).template() != b.get(i).template() || a.get(i).count() != b.get(i).count()) return false;
        }
        return true;
    }

    /* ============================ 4. BEFORE AND AFTER, BY THE MODEL ============================ */

    /** A twin of the city: the same save, loaded. */
    static Game twin(Game g) {
        Game t = new Game(g.getGameFiles());
        quietly(() -> t.loadGameSave(10));
        return t;
    }

    /**
     * The order and what is on site built standing in a twin, the month's own
     * services pass run on it (SimulationEngine's last steps), and the figure
     * read the way the model reads it.
     *
     * A SCHOOL ABOVE THE LADDER IS READ AGAINST THE CITY'S OWN STUDENTS
     * (0.7.51): its seats are the twin's, and who it would get and hire is
     * CityNeeds.wanted() before the order stands. That counts the posts the
     * city has; a school's own posts - a university's professors are
     * university graduates - would let an order make its own demand. Until
     * 0.7.51 it was who would come, which no building moves.
     */
    static double builtAndRead(Game twin, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> add) {
        BuildingManager bm = twin.getBuildingManager();
        wantBefore = m.kind() == BuildAdvice.Kind.SCHOOL && !m.school().isBasic()
                ? CityNeeds.wanted(twin, m.school(), 0, 1) : Double.NaN;
        for (Map.Entry<BuildingsTemplate, Integer> e : add.entrySet()) {
            bm.addStack(template(twin, e.getKey().getName()), e.getValue(), true);
        }
        ServicesManager services = twin.getServicesManager();
        services.updateServiceWages(twin.getPopulationManager().getWagesPerType());
        services.updateJobFillRate(twin.getPopulationManager().getJobFillRate());
        services.updateServices();
        return readByTheModel(twin, m);
    }

    /** The students a school above the ladder would get and hire, read before the order stood (builtAndRead()). */
    static double wantBefore = Double.NaN;

    /** A measure's figure, read off the model: the handlers' own loads, the education month, the crime month. */
    static double readByTheModel(Game twin, BuildAdvice.Measure m) {
        double[] fill = twin.getPopulationManager().getJobFillRate();
        BuildingManager bm = twin.getBuildingManager();
        switch (m.kind()) {
            case POWER: {
                UtilitiesHandler u = twin.getServicesManager().getUtilitiesHandler();
                return u.getConsumption() / u.getProduction();
            }
            case WATER: {
                UtilitiesHandler u = twin.getServicesManager().getUtilitiesHandler();
                return u.getWaterConsumption() / u.getWaterProduction();
            }
            case ROADS:
                return twin.getInfrastructureManager().getUtilisation();
            case TRANSIT:
                return twin.getInfrastructureManager().getTransitCover();
            case CARE:
                return CityNeeds.careCover(twin, m.care(), twin.getCohorts(), fill);
            case SCHOOL: {
                if (!m.school().isBasic()) {
                    double seats = bm.getStaffedEducationPlaces(fill)[m.school().ordinal()];
                    return wantBefore / Math.max(seats, 1);
                }
                // The education month itself, on the twin's places: its coverage is the figure.
                quietly(() -> twin.getEducation().advanceMonth(bm.getStaffedEducationPlaces(fill), twin.getCohorts(),
                        twin.getPopulationManager(), twin.getLabourMarket(), 0, 0));
                return twin.getEducation().getCoverage(m.school());
            }
            case POLICE: {
                Crime month = crimeMonth(twin, bm.getStaffedSafetyCapacity(SafetyType.POLICE, fill),
                        bm.getStaffedSafetyCapacity(SafetyType.PRISON, fill), 1);
                return month.getRateVsCanada();
            }
            case CELLS: {
                // Sentences are six months, so the cells' figure is the long run:
                // 24 months of the same crime, the last six averaged.
                Crime month = crimeMonth(twin, bm.getStaffedSafetyCapacity(SafetyType.POLICE, fill),
                        bm.getStaffedSafetyCapacity(SafetyType.PRISON, fill), 0);
                double sum = 0;
                for (int i = 0; i < 24; i++) {
                    advance(twin, month, bm.getStaffedSafetyCapacity(SafetyType.POLICE, fill),
                            bm.getStaffedSafetyCapacity(SafetyType.PRISON, fill));
                    if (i >= 24 - Crime.SENTENCE_MONTHS) sum += month.getNotHeld();
                }
                return sum / Crime.SENTENCE_MONTHS;
            }
            case DEATH: {
                // The healthcare month's settle, on the twin's plots and ovens: the dead left waiting.
                Healthcare month = new Healthcare();
                month.restore(twin.getHealthcare().getState());
                month.advanceMonth(0, 0, null, null, twin.getHealthcare().getDeaths(), .5,
                        bm.getCareCapacity(CareType.BURIAL), bm.getStaffedCareCapacity(CareType.CREMATION, fill));
                return month.getUnburied();
            }
            case PLOTS:
                return twin.getHealthcare().monthsOfPlotsLeft(bm.getCareCapacity(CareType.BURIAL));
            default:
                return Double.NaN;
        }
    }

    /** The city's crime, restored into a fresh Crime and played `months` months on these officers and cells. */
    static Crime crimeMonth(Game twin, double officers, double cells, int months) {
        Crime month = new Crime();
        month.restore(twin.getCrime().getState());
        for (int i = 0; i < months; i++) advance(twin, month, officers, cells);
        return month;
    }

    /** One month of crime on the city's own causes - the adults in each, from the pressure the city's month left. */
    static void advance(Game twin, Crime month, double officers, double cells) {
        Crime city = twin.getCrime();
        Crime.Causes causes = new Crime.Causes();
        for (Crime.Cause c : Crime.Cause.values()) {
            if (c.isGroup()) causes.add(c, city.getPressure(c) / c.weight());
        }
        month.advanceMonth(causes, city.getPopulation(), officers, cells, 0, 0);
    }

    static void beforeAndAfter(Path root, Game g, List<BuildAdvice.Suggestion> advice) {
        out.println("\n--- 4. before and after: the order built in a twin, the figure the model reads ---");
        for (BuildAdvice.Suggestion s : advice) {
            BuildAdvice.Measure m = s.measure();
            Game t = twin(g);
            close("fixture: the twin stands where the city does for " + m.label(),
                    BuildAdvice.figure(t, m, Map.of()), BuildAdvice.figure(g, m, Map.of()), 1e-12);
            double read = builtAndRead(t, m, BuildAdvice.plus(BuildAdvice.onSite(g, m), s.template(), s.count()));
            close(s.need().label() + ": after the on site and " + s.count() + " " + s.template().getName()
                    + ", the figure the model reads", s.after(), read, 1e-9);
        }
    }

    /** Every measure a city category opens on: an order of the first building that serves it, built in a twin. */
    static void everyMeasure(Path root, Game g) {
        out.println("\n--- 4b. every measure: an order of its first building, the twin's figure ---");
        for (BuildAdvice.Category c : BuildAdvice.categories()) {
            for (BuildAdvice.Measure m : BuildAdvice.measuresOf(c.name())) {
                BuildingsTemplate first = null;
                for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
                    if (m.serves(t)) { first = t; break; }
                }
                if (first == null) continue;
                int n = m.kind() == BuildAdvice.Kind.CELLS ? 1 : 3;
                Map<BuildingsTemplate, Integer> add = BuildAdvice.plus(BuildAdvice.onSite(g, m), first, n);
                double advised = BuildAdvice.figure(g, m, add);
                Game t = twin(g);
                double read = builtAndRead(t, m, add);
                close(c.name() + ", " + m.label() + ": " + n + " " + first.getName() + " - the advice's figure is the model's",
                        advised, read, m.kind() == BuildAdvice.Kind.CELLS ? 1e-6 : 1e-9);
            }
        }
        // ...and the burial plots, which have a NEEDS YOU row and no ring of their own.
        BuildAdvice.Measure plots = BuildAdvice.Measure.of(BuildAdvice.Kind.PLOTS);
        BuildingsTemplate cemetery = template(g, "Memorial Cemetery");
        Map<BuildingsTemplate, Integer> add = BuildAdvice.plus(BuildAdvice.onSite(g, plots), cemetery, 3);
        close("Healthcare, burial plots: 3 Memorial Cemetery - the months of plots left are the model's",
                BuildAdvice.figure(g, plots, add), builtAndRead(twin(g), plots, add), 1e-9);
    }

    /* ============================ 5. THE STAFFING WEIGHTING ============================ */

    /**
     * Two clinics alike in everything - price, capacity, land, posts in all -
     * but how many of the scarcest of the clinic's own posts they need: the
     * advice takes the one with fewer.
     */
    static void theStaffingWeighting(Path root) {
        out.println("\n--- 5. the staffing weighting: short of a job, the building with fewer of those posts ---");
        Game g = shortCity(root, "short-staff", false);
        double[] fill = g.getPopulationManager().getJobFillRate();
        BuildingsTemplate clinic = template(g, "Walk-in Clinic");
        JobType scarce = null, full = null;
        for (JobType j : JobType.values()) {
            double f = fill[j.ordinal()];
            if (clinic.getJobs(j) > 0 && (scarce == null || f < fill[scarce.ordinal()])) scarce = j;
            if (f >= .999 && (full == null)) full = j;
        }
        out.printf("      the clinic's scarcest post: %s at %.3f filled; a job filled whole: %s%n",
                scarce, scarce == null ? 1 : fill[scarce.ordinal()], full);
        assertTrue("fixture: the city fills one of the clinic's job types short", scarce != null && fill[scarce.ordinal()] < .999);
        assertTrue("fixture: and one job type whole", full != null);
        if (scarce == null || full == null || fill[scarce.ordinal()] >= .999) return;
        int k = clinic.getJobs(scarce);
        BuildingsTemplate fewer = clone(clinic, "Clinic with fewer", scarce, 0, full, clinic.getJobs(full) + k);
        BuildingsTemplate more = clone(clinic, "Clinic with more", scarce, 2 * k, full, Math.max(0, clinic.getJobs(full) - k));
        assertTrue("fixture: the two have the same posts in all", posts(fewer) == posts(more));
        bits("fixture: and the same price", g.quoteBuild(fewer, 1).total, g.quoteBuild(more, 1).total);
        List<BuildingsTemplate> catalogue = g.getBuildingManager().getTemplates();
        // The original is set aside for the comparison; the two take its place.
        catalogue.remove(clinic);
        catalogue.add(more);
        catalogue.add(fewer);
        List<CityNeeds.Need> all = CityNeeds.measure(g, CityNeeds.PLAIN);
        BuildAdvice.Suggestion general = null;
        for (BuildAdvice.Suggestion s : BuildAdvice.suggest(g, all)) {
            if (s.measure().kind() == BuildAdvice.Kind.CARE && s.measure().care() == CareType.GENERAL) general = s;
        }
        BuildAdvice.Measure gm = BuildAdvice.Measure.care(CareType.GENERAL);
        out.printf("      fewer: staffing %.4f, a unit %.1f; more: staffing %.4f, a unit %.1f%n",
                BuildAdvice.staffing(fewer, fill), BuildAdvice.unit(g, gm, fewer),
                BuildAdvice.staffing(more, fill), BuildAdvice.unit(g, gm, more));
        assertTrue("the one with fewer of the short posts serves more at today's staffing",
                BuildAdvice.unit(g, gm, fewer) > BuildAdvice.unit(g, gm, more));
        assertTrue("general care is suggested", general != null);
        assertTrue("...and with the clinic needing fewer of them", general != null && general.template() == fewer);
        catalogue.remove(more);
        catalogue.remove(fewer);
        catalogue.add(clinic);
    }

    static BuildingsTemplate clone(BuildingsTemplate t, String name, JobType a, int na, JobType b, int nb) {
        BuildingsTemplate c = new BuildingsTemplate(name, t.getCategory())
                .setCare(t.getCare()).setCapacity(t.getCapacity()).setCashCost(t.getCashCost())
                .setConstructionPoints(t.getConstructionPoints()).setConstructionMaterials(t.getConstructionMaterials())
                .setUpkeep(t.getUpkeep()).setElectricityConsumption(t.getElectricityConsumption())
                .setWaterConsumption(t.getWaterConsumption()).setLandSqFt(t.getLandSqFt()).setRoadLoad(t.getRoadLoad())
                .setId(t.getId());
        for (JobType j : JobType.values()) c.setJobs(j, t.getJobs(j));
        c.setJobs(a, na);
        c.setJobs(b, nb);
        return c;
    }

    static int posts(BuildingsTemplate t) {
        int n = 0;
        for (JobType j : JobType.values()) n += t.getJobs(j);
        return n;
    }

    /* ============================ 6. NOTHING NEEDED ============================ */

    static void nothingNeeded(Path root) {
        out.println("\n--- 6. nothing needed, nothing suggested ---");
        Game g = servedCity(root, "served");
        List<CityNeeds.Need> all = CityNeeds.measure(g, CityNeeds.PLAIN);
        int cityBuilt = 0;
        for (CityNeeds.Need n : CityNeeds.biting(all)) {
            if (n.cityBuilds()) {
                cityBuilt++;
                out.printf("      still listed: %s · %s%n", n.label(), n.reading());
            }
        }
        out.printf("      the served city: month %d, %,d people%n", g.getMonth(), g.getPopulationManager().getPopulation());
        assertTrue("fixture: NEEDS YOU lists no need a city-built building answers", cityBuilt == 0);
        assertTrue("...and the advice suggests nothing", BuildAdvice.suggest(g, all).isEmpty());
    }

    static boolean bitsEqual(double a, double b) {
        return Double.doubleToLongBits(a) == Double.doubleToLongBits(b);
    }

    /* ============================ 7. LAND COUNTED ============================ */

    /**
     * LAND COUNTED (0.7.51): the short city's cards with the land office's
     * listing at nothing, as it stands, and at ten times its prices (each
     * parcel's dollar price in LandMarket's listing state, scaled): every
     * card's figure is, since 0.7.101 (Jerus's decisions A4 and A19), its
     * order over its life - its quote for the count, its ground
     * (BuildAdvice.landValue()) and lifeFactor() months of running() a
     * building - over what it will serve over that life, year by year as
     * the city grows into it, for power, water, the roads and care, and over
     * what its count adds for the rest (lifeFigure(), recomputed here); until
     * then its quote for one and its ground over what one serves, a road's
     * with its running over its life (0.7.70), a living care building's its
     * order over the places the need lacked (0.7.71) - and
     * its ground is valued on what the cards before it leave. Then with no
     * ground free, so every road's ground is bought at the office: with the
     * office's prices at nothing the road cheapest to build and keep a trip
     * wins, as it did before 0.7.51 - gravel; at ten times the price a
     * square foot past which the road that needs the least ground a trip is
     * the cheapest with its ground over its life, that one - the elevated
     * highway. (The spec's ten times the
     * listing is city2400's; the short city's ground is two hundred times
     * cheaper and its free ground is valued at the lower of the office's
     * price and what a business pays.)
     */
    static void landCounted(Path root) {
        out.println("\n--- 7. land counted: each building priced with its ground at the land office's,"
                + " on the land the cards before leave ---");
        Game g = shortCity(root, "short-land", true);
        LandMarket market = g.getLandManager().getMarket();
        double[][] base = market.getOffersState();
        int nextId = market.getNextOfferId();
        double[] factors = {0, 1, 10};
        String[] roads = new String[factors.length], power = new String[factors.length];
        boolean perBits = true, leftBits = true;
        int cards = 0;
        for (int f = 0; f < factors.length; f++) {
            // The offers' records, each one's dollar price scaled (since 0.7.57 the offers, forty until 0.7.66; the parcels' listing until then).
            market.restoreOffers(scaledOffers(base, factors[f]), nextId);
            double landLeft = g.getLandManager().getAvailableSqFt();
            StringBuilder said = new StringBuilder();
            for (BuildAdvice.Suggestion s : BuildAdvice.suggest(g)) {
                cards++;
                BuildingsTemplate t = s.template();
                // Every card's since 0.7.101: its order over its life for what it will serve, recomputed here.
                double per = lifeFigure(g, s, landLeft);
                perBits &= bitsEqual(s.pricePerUnit(), per);
                leftBits &= bitsEqual(s.landValue(), BuildAdvice.landValue(g, s.landSqFt(), landLeft))
                        && bitsEqual(s.landShort(), Math.max(0, s.landSqFt() - landLeft));
                if (s.measure().kind() == BuildAdvice.Kind.ROADS) roads[f] = t.getName();
                if (s.measure().kind() == BuildAdvice.Kind.POWER) power[f] = t.getName();
                said.append(String.format(" [%s: %,d x %s, %,.3f a unit it will serve over its life]", s.need().label(), s.count(),
                        t.getName(), s.pricePerUnit()));
                landLeft = Math.max(0, landLeft - s.landSqFt());
            }
            out.printf("      the land office at x%s: %s a sq ft;%s%n", factors[f],
                    Formats.INSTANCE.amount(g.getLandManager().getOfficePricePerSqFt()), said);
        }
        market.restoreOffers(base, nextId);
        assertTrue("fixture: cards at nothing, as the listing stands and at ten times", cards > 0);
        assertTrue("every card's figure is its order over its life - its quote for the count, its ground at landValue() and"
                + " lifeFactor() months of running() each - over what it will serve over that life (power, water, the roads and"
                + " care) or what its count adds (the rest) (0.7.101) - to the bit", perBits);
        assertTrue("...its ground valued on the land the cards before it leave, and short of that by landShort, to the bit",
                leftBits);

        // No ground free: every road's ground at the land office's price.
        LandManager land = g.getLandManager();
        double owned = land.getOwnedSqFt();
        land.setOwnedSqFt(land.getAllocatedSqFt());
        // Since 0.7.57 the ground set by hand draws the land again and lists
        // its offers afresh from it; the prices below are the listing above's.
        market.restoreOffers(base, nextId);
        BuildAdvice.Measure m = BuildAdvice.Measure.of(BuildAdvice.Kind.ROADS);
        Map<BuildingsTemplate, Integer> site = BuildAdvice.onSite(g, m);
        BuildingsTemplate highway = template(g, "Elevated Highway");
        // A road's price over its life since 0.7.70 (BuildAdvice.lifetime()): its quote and its running for its life.
        double life = BuildAdvice.lifeFactor(g);
        double hq = (g.quoteBuild(highway, 1).total + BuildAdvice.running(g, highway, site) * life) / BuildAdvice.unit(g, m, highway);
        double ha = highway.getLandSqFt() / BuildAdvice.unit(g, m, highway);
        double breakEven = 0;
        boolean thriftiest = true;
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            if (t.getCategory() != BuildingType.INFRASTRUCTURE || t == highway) continue;
            double u = BuildAdvice.unit(g, m, t);
            if (!(u > 0)) continue;
            int[] today = BuildAdvice.count(g, m, site, t, new BuildAdvice.Ahead(0, 1, BuildAdvice.SLACK));
            if (today[0] <= 0) continue;
            if (BuildAdvice.count(g, m, site, t, BuildAdvice.opening(g, g.quoteBuild(t, today[0]).months))[1] != 1) continue;
            double q = (g.quoteBuild(t, 1).total + BuildAdvice.running(g, t, site) * life) / u, ga = t.getLandSqFt() / u;
            thriftiest &= ga > ha;
            if (ga > ha) breakEven = Math.max(breakEven, (hq - q) / (ga - ha));
        }
        double office = land.getOfficePricePerSqFt();
        double factor = 10 * breakEven / office;
        String[] dear = new String[2];
        double[] at = {0, factor};
        for (int f = 0; f < 2; f++) {
            market.restoreOffers(scaledOffers(base, at[f]), nextId);
            for (BuildAdvice.Suggestion s : BuildAdvice.suggest(g)) if (s.measure().kind() == BuildAdvice.Kind.ROADS) dear[f] = s.template().getName();
        }
        market.restoreOffers(base, nextId);
        land.setOwnedSqFt(owned);
        out.printf("      no ground free: the highway is the cheapest road with its ground past %s a sq ft;"
                + " the office's prices at x0 and x%,.1f: %s, %s%n", Formats.INSTANCE.amount(breakEven), factor, dear[0], dear[1]);
        assertTrue("fixture: of the roads that keep it ahead, the Elevated Highway needs the least ground a trip", thriftiest
                && breakEven > 0);
        assertTrue("with the land office's prices at nothing, the roads card is the cheapest to build: Gravel Road (" + dear[0] + ")",
                "Gravel Road".equals(dear[0]));
        assertTrue("...and at ten times that price a square foot, the one that needs the least ground: Elevated Highway ("
                + dear[1] + ")", "Elevated Highway".equals(dear[1]));
    }

    /**
     * A card's figure, recomputed here from the model's own pieces (0.7.101,
     * BuildAdvice, A BUILDING OVER ITS LIFE): its order over its life - the
     * quote for its count (Game.quoteBuild(), or Game.quotePave() and the
     * ground a paving frees taken off), its ground at landValue() and
     * lifeFactor() months of running() each - over what it serves a month on
     * average over LIFE_YEARS years, each read at its middle month from its
     * opening at the demand today's grows to on lifeTrend() with the card's
     * slack, as the shortfall without it less the shortfall with it, the
     * year weighted by its twelve months at the life's rate; where the
     * measure has no shortfall (BuildAdvice.weighsServed()) or the order
     * serves none of it, over the count times what one serves.
     */
    static double lifeFigure(Game g, BuildAdvice.Suggestion s, double landLeft) {
        BuildAdvice.Measure m = s.measure();
        BuildingsTemplate t = s.template();
        Map<BuildingsTemplate, Integer> site = BuildAdvice.onSite(g, m);
        int n = s.count();
        double factor = BuildAdvice.lifeFactor(g);
        double life;
        Map<BuildingsTemplate, Integer> with = new java.util.LinkedHashMap<>(site);
        if (s.paving()) {
            BuildingsTemplate from = template(g, ConstructionControl.PAVE_FROM), to = template(g, ConstructionControl.PAVE_TO);
            life = g.quotePave(n).total - BuildAdvice.landValue(g, BuildAdvice.pavingFrees(g) * (double) n, landLeft)
                    + n * (BuildAdvice.running(g, to, site) - BuildAdvice.running(g, from, site)) * factor;
            with.merge(to, n, Integer::sum);
            with.merge(from, -n, Integer::sum);
        } else {
            life = g.quoteBuild(t, n).total + BuildAdvice.landValue(g, t.getLandSqFt() * (double) n, landLeft)
                    + n * BuildAdvice.running(g, t, site) * factor;
            with.merge(t, n, Integer::sum);
        }
        if (!BuildAdvice.weighsServed(m)) return life / (n * s.unit());
        double i = BuildAdvice.lifeRate(g) / 12, served = 0, weights = 0;
        for (int y = 0; y < BuildAdvice.LIFE_MONTHS / 12; y++) {
            double w = 0;
            for (int j = 12 * y + 1; j <= 12 * y + 12; j++) w += i > 0 ? Math.pow(1 + i, -j) : 1;
            double months = Math.max(0, s.ahead().months() - BuildAdvice.HORIZON) + 12 * y + 6;
            BuildAdvice.Ahead a = new BuildAdvice.Ahead(months, BuildAdvice.lifeTrend(g, months), s.ahead().slack());
            served += w * Math.max(0, Math.max(0, BuildAdvice.shortfall(g, m, site, a))
                    - Math.max(0, BuildAdvice.shortfall(g, m, with, a)));
            weights += w;
        }
        double avg = weights > 0 ? served / weights : 0;
        return avg > 0 ? life / avg : life / (n * s.unit());
    }

    /** The offers' records with each one's dollar price times f (LandParcel.offerRow(): the price second from the end). */
    static double[][] scaledOffers(double[][] base, double f) {
        double[][] st = new double[base.length][];
        for (int i = 0; i < base.length; i++) {
            st[i] = base[i].clone();
            st[i][LandParcel.OFFER_FIELDS - 2] *= f;
        }
        return st;
    }

    /* ============================ 8. SLACK BUILT ============================ */

    /**
     * SLACK BUILT (0.7.51): a served card - power, water, the road, care,
     * the schools - at 100% or more of its demand at its projection with
     * SLACK on top, not just off NEEDS YOU's list; care's and a basic
     * school's demand there exactly today's times k and 1 + SLACK; and the
     * count never fewer than the old rule's, the least off the list today.
     */
    static void slackBuilt(Game g, List<BuildAdvice.Suggestion> advice) {
        out.println("\n--- 8. slack built: a served card at 100% of its demand projected and SLACK past it;"
                + " never fewer than the old rule ---");
        boolean more = false;
        for (BuildAdvice.Suggestion s : advice) {
            BuildAdvice.Measure m = s.measure();
            Map<BuildingsTemplate, Integer> with = BuildAdvice.plus(BuildAdvice.onSite(g, m), s.template(), s.count());
            String what = s.need().label() + ", " + s.count() + " " + s.template().getName();
            bits(what + ": sized with the businesses' headroom as its slack", s.ahead().slack(), BusinessInvestment.TARGET_HEADROOM);
            if (BuildAdvice.isServed(m) && m.kind() != BuildAdvice.Kind.TRANSIT) {
                double[] there = BuildAdvice.supplyDemand(g, m, with, s.ahead());
                assertTrue(what + ": at 100% or more of its demand projected and SLACK past it ("
                        + String.format("%.4f", CityNeeds.servedShare(there[0], there[1])) + ")",
                        CityNeeds.servedShare(there[0], there[1]) >= 1);
                if (m.kind() == BuildAdvice.Kind.CARE || (m.kind() == BuildAdvice.Kind.SCHOOL && m.school().isBasic())) {
                    double[] now = BuildAdvice.supplyDemand(g, m, with);
                    bits(what + ": ...that demand is today's times k and 1 + SLACK", there[1], s.ahead().scale() * now[1]);
                }
            }
            int old = BuildAdvice.count(g, m, BuildAdvice.onSite(g, m), s.template())[0];
            assertTrue(what + ": never fewer than the old rule's count (" + old + ")", s.count() >= old);
            more |= s.count() > old;
        }
        assertTrue("fixture: the slack and the projection add buildings to at least one card", more);
    }

    /* ============================ 9. NO HIGHER EDUCATION WITHOUT ITS PIPELINE ============================ */

    /**
     * NO HIGHER EDUCATION WITHOUT ITS PIPELINE (0.7.51). Every college or
     * university row wants no more than would come and no more than would
     * be hired, to the bit, and with no seats of its kind at least half the
     * smallest school. The short city without its university has students
     * for one and too few posts for its graduates: no row and no card; the
     * posts brought in - Engineering Services Offices, standing - and the
     * row is there. And the playtest's founding at month 9, which the old
     * rule told to build a university, gets none.
     */
    static void thePipeline(Path root, Game g, List<CityNeeds.Need> all) {
        out.println("\n--- 9. no higher education without its pipeline: no more wanted than would come or be hired;"
                + " a first school half full ---");
        int[] rows = {0, 0};
        higherRows(g, all, "the short city", rows);

        EducationType uni = EducationType.UNIVERSITY;
        Game u = shortCity(root, "short-no-university", true, Set.of("University"));
        // Its students' fees paid, so more would come than a first university wants (Education.willingShare()).
        u.getEducation().setTuitionSubsidy(1);
        double first = Math.max(CityNeeds.SEATS_FLOOR, CityNeeds.FIRST_SCHOOL_SHARE * CityNeeds.smallestSchool(u, uni));
        double come = CityNeeds.wouldCome(u, uni), hires = CityNeeds.hires(u, uni, 1);
        out.printf("      no university, fees paid: %,.0f would come, %,.0f would be hired, a first university wants %,.0f%n",
                come, hires, first);
        assertTrue("fixture: with no university and its fees paid, more would come than a first one wants", come >= first);
        assertTrue("fixture: ...and its graduates' posts would not fill one", hires < first);
        List<CityNeeds.Need> before = CityNeeds.measure(u, CityNeeds.PLAIN);
        assertTrue("no UNIVERSITY row", schoolRow(before, uni) == null);
        assertTrue("...and no university card", !suggests(u, before, uni));
        BuildingsTemplate office = template(u, "Engineering Services Office");
        int posts = 0;
        for (JobType j : JobType.values()) if (WageBand.of(j) == uni.produces()) posts += office.getJobs(j);
        int offices = 0;
        while (CityNeeds.hires(u, uni, 1) < first && offices < 10_000) {
            u.getBuildingManager().addStack(office, 1, true);
            offices++;
        }
        hires = CityNeeds.hires(u, uni, 1);
        out.printf("      %,d Engineering Services Offices standing, %,d university posts each: %,.0f would be hired%n",
                offices, posts, hires);
        assertTrue("fixture: the offices' posts bring the hires to a first university's", hires >= first);
        List<CityNeeds.Need> after = CityNeeds.measure(u, CityNeeds.PLAIN);
        assertTrue("...and the UNIVERSITY row is there", schoolRow(after, uni) != null);
        higherRows(u, after, "...with the offices", rows);
        assertTrue("fixture: the rows above held a school with seats and one with none", rows[0] > 0 && rows[1] > 0);

        Game f = founded(root, "founded", 9);
        double come9 = CityNeeds.wouldCome(f, uni);
        long uniPosts = 0;
        long[] jobs = f.getBuildingManager().getTotalJobs();
        for (JobType j : JobType.values()) if (WageBand.of(j) == uni.produces()) uniPosts += jobs[j.ordinal()];
        out.printf("      the founding at month %d: %,d people, %,.0f would come to a university, %,d university posts%n",
                f.getMonth(), f.getPopulationManager().getPopulation(), come9, uniPosts);
        assertTrue("fixture: month 9 of a Standard founding, at least a class would come to a university",
                f.getMonth() == 9 && come9 >= CityNeeds.SEATS_FLOOR);
        List<CityNeeds.Need> f9 = CityNeeds.measure(f, CityNeeds.PLAIN);
        assertTrue("no university row there", schoolRow(f9, uni) == null);
        assertTrue("...and no university card", !suggests(f, f9, uni));
    }

    /** Every school row above the ladder: no more wanted than would come or be hired, to the bit; with no seats, a first school's. rows[0] counts those with seats, rows[1] those without. */
    static void higherRows(Game g, List<CityNeeds.Need> all, String where, int[] rows) {
        for (CityNeeds.Need n : all) {
            if (n.kind() != CityNeeds.Kind.HIGHER_SCHOOL) continue;
            EducationType t = n.school();
            double want = n.b(), come = CityNeeds.wouldCome(g, t), hires = CityNeeds.hires(g, t, 1);
            String what = where + ", " + n.label() + " (" + n.reading() + ")";
            assertTrue(what + ": wanted is no more than would come (" + String.format("%,.0f", come) + ")", want <= come);
            assertTrue(what + ": ...and no more than would be hired (" + String.format("%,.0f", hires) + ")", want <= hires);
            bits(what + ": ...it is the smaller", want, Math.min(come, hires));
            if (n.a() <= 0) {
                assertTrue(what + ": with no seats, at least a class and half the smallest school",
                        want >= Math.max(CityNeeds.SEATS_FLOOR, CityNeeds.FIRST_SCHOOL_SHARE * CityNeeds.smallestSchool(g, t)));
                rows[1]++;
            } else {
                rows[0]++;
            }
        }
    }

    static CityNeeds.Need schoolRow(List<CityNeeds.Need> all, EducationType t) {
        for (CityNeeds.Need n : all) if (n.kind() == CityNeeds.Kind.HIGHER_SCHOOL && n.school() == t) return n;
        return null;
    }

    static boolean suggests(Game g, List<CityNeeds.Need> all, EducationType t) {
        for (BuildAdvice.Suggestion s : BuildAdvice.suggest(g, all)) {
            if (s.measure().kind() == BuildAdvice.Kind.SCHOOL && s.measure().school() == t) return true;
        }
        return false;
    }

    /** The default playtest's founding (LongPlaytest's own: Standard, its first houses, shops and fields), played to `month`. */
    static Game founded(Path root, String name, int month) {
        Game g = new Game(new GameFiles(root.resolve(name), root.resolve(name + "-no-legacy")), LongPlaytest.founding());
        PrintStream was = LongPlaytest.out;
        LongPlaytest.out = quiet;
        try {
            quietly(() -> {
                g.run();
                LongPlaytest.build(g, "House", 40);
                LongPlaytest.build(g, "Convenience Store", 3);
                LongPlaytest.build(g, "Mixed Farm", 2);
                g.simulateMonths(3);
                LongPlaytest.build(g, "House", 20);
                g.simulateMonths(4);
                LongPlaytest.build(g, "Convenience Store", 2);
                LongPlaytest.build(g, "Construction Depot", 1);
                g.simulateMonths(month - g.getMonth());
            });
        } finally {
            LongPlaytest.out = was;
        }
        return g;
    }

    /* ============================ 10. SIZED TO THE PROJECTION ============================ */

    /**
     * SIZED TO THE PROJECTION (0.7.51): the short city's population history
     * rising - two per cent a month over the businesses' window to the
     * people it has - against the same history flat. Every card's growth is
     * BusinessInvestment.growthFactor() over its wait, held to their
     * MAX_ORDER_MONTHS, and their PLANNING_HORIZON, to the bit; and no card
     * orders fewer than the flat history's, for the same need and building.
     */
    static void sizedToTheProjection(Path root) {
        out.println("\n--- 10. sized to the projection: the businesses' growthFactor() over the wait and the horizon ---");
        Game g = shortCity(root, "short-grow", true);
        BusinessInvestment bi = g.getBusinessInvestment();
        long pop = g.getPopulationManager().getPopulation();
        List<Long> flat = new ArrayList<>(), rising = new ArrayList<>();
        for (int i = 0; i < BusinessInvestment.TREND_WINDOW; i++) {
            flat.add(pop);
            rising.add(Math.round(pop * (1 - .02 * (BusinessInvestment.TREND_WINDOW - 1 - i))));
        }
        bi.restorePopulationHistory(flat);
        List<BuildAdvice.Suggestion> still = BuildAdvice.suggest(g);
        bi.restorePopulationHistory(rising);
        List<BuildAdvice.Suggestion> grown = BuildAdvice.suggest(g);
        out.printf("      %,d people, homes for %,.0f; rising %,.1f a month%n", pop, bi.reachablePopulation(), bi.getPopulationGrowth());
        assertTrue("fixture: the rising history is a trend the businesses read", bi.getPopulationGrowth() > 0);
        boolean grows = !grown.isEmpty(), raised = false, compared = false;
        for (BuildAdvice.Suggestion s : grown) {
            String what = s.need().label() + ", " + s.template().getName();
            double wait = Double.isNaN(s.lead()) ? 0 : Math.max(0, Math.min(s.lead(), BusinessInvestment.MAX_ORDER_MONTHS));
            bits(what + ": k is growthFactor(" + String.format("%.2f", wait) + " + the horizon)", s.ahead().k(),
                    bi.growthFactor(wait + BusinessInvestment.PLANNING_HORIZON));
            grows &= s.ahead().k() > 1;
            for (BuildAdvice.Suggestion f : still) {
                if (f.need().kind() != s.need().kind() || f.measure().equals(s.measure()) == false || f.template() != s.template()) continue;
                compared = true;
                assertTrue(what + ": " + s.count() + " rising, never fewer than flat's " + f.count(), s.count() >= f.count());
                raised |= s.count() > f.count();
            }
        }
        assertTrue("fixture: every card grows with the trend (k over 1)", grows);
        assertTrue("fixture: a card of the same building in both", compared);
        assertTrue("fixture: and the projection raises one count", raised);
    }

    /* ============================ 11. THE RUN ============================ */

    /**
     * THE RUN (0.7.51): "Build all three" places BuildAdvice.run(), the
     * cards' orders in their order. In the short city no card is short of
     * land and the run goes all the way; placed in turn in a twin it charges
     * Game.buildRunInvoice(), the header's total, to the bit. With the
     * ground for the first card alone, the second is short, and the run
     * stops there, for land. (With half the second's as well, the roads
     * card turned to a road that fits: the land rule at work.)
     */
    static void theRun(Path root, Game g, List<BuildAdvice.Suggestion> advice) {
        out.println("\n--- 11. the run: Build all three goes all the way exactly when no card is short of land,"
                + " and charges the header's total ---");
        LinkedHashMap<BuildingsTemplate, Integer> run = BuildAdvice.run(advice);
        LinkedHashMap<BuildingsTemplate, Integer> mine = new LinkedHashMap<>();
        for (BuildAdvice.Suggestion s : advice) mine.merge(s.template(), s.count(), Integer::sum);
        assertTrue("the run is the cards' orders in their order, two of one building added together",
                new ArrayList<>(run.entrySet()).equals(new ArrayList<>(mine.entrySet())));
        boolean anyShort = false;
        for (BuildAdvice.Suggestion s : advice) anyShort |= s.landShort() > 0;
        assertTrue("fixture: the short city has the ground for every card", !anyShort);
        assertTrue("...and the run goes all the way: buildRunAhead() is every order, buildRunStop() SUCCESS",
                g.buildRunAhead(run) == run.size() && g.buildRunStop(run) == Game.BuildResult.SUCCESS);
        double invoice = g.buildRunInvoice(run);
        Game t = twin(g);
        double[] charged = {0};
        boolean[] placed = {true};
        quietly(() -> {
            t.setCashForTest(invoice * 2 + Founding.WEALTHY_CASH);
            for (Map.Entry<BuildingsTemplate, Integer> e : run.entrySet()) {
                placed[0] &= t.buildStack(template(t, e.getKey().getName()), e.getValue(), false) == Game.BuildResult.SUCCESS;
                charged[0] += t.getTotalBuildingCost();
            }
        });
        assertTrue("fixture: the run placed in turn in a twin", placed[0]);
        bits("...charges the header's total, Game.buildRunInvoice() of the run, to the bit", charged[0], invoice);

        Game h = shortCity(root, "short-run", true);
        List<BuildAdvice.Suggestion> plenty = BuildAdvice.suggest(h);
        LandManager land = h.getLandManager();
        land.setOwnedSqFt(land.getAllocatedSqFt() + plenty.get(0).landSqFt());
        List<BuildAdvice.Suggestion> cut = BuildAdvice.suggest(h);
        int firstShort = -1;
        for (int i = 0; i < cut.size() && firstShort < 0; i++) if (cut.get(i).landShort() > 0) firstShort = i;
        LinkedHashMap<BuildingsTemplate, Integer> stops = BuildAdvice.run(cut);
        out.printf("      ground for the first card alone: %,.0f sq ft free; the first short card: %d of %d%n",
                land.getAvailableSqFt(), firstShort + 1, cut.size());
        assertTrue("fixture: with the ground for the first card alone, a card after it is short of land", firstShort > 0);
        assertTrue("fixture: ...and no two cards are one building, so the run's orders are the cards", stops.size() == cut.size());
        assertTrue("...and the run stops at the first short card, for land",
                h.buildRunAhead(stops) == firstShort && h.buildRunStop(stops) == Game.BuildResult.NO_LAND);
    }
}

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
 *   3. THE COUNT AND THE PRICE: the count is the least that takes the need
 *      off the list after what is on site, at the staffed capacity the model
 *      counts; the price is Game.quoteBuild() for it, to the bit; and with
 *      the cash short the count is the most it affords, the next suggestion
 *      capped by what the one before left, and with no cash at all the full
 *      count marked as needing credit; and the building fits the land free,
 *      or none that closes the need does.
 *   4. BEFORE AND AFTER, BY THE MODEL: in a twin of the city, the order and
 *      what is on site built standing and the month's own services pass run
 *      on it, every suggestion's figure is the one the model reads - and the
 *      same for an order of every measure a city category opens on.
 *   5. THE STAFFING WEIGHTING: in a city short of one job type, of two
 *      buildings alike in everything but how many of those posts they have,
 *      the advice takes the one with fewer.
 *   6. NOTHING NEEDED, NOTHING SUGGESTED: a city NEEDS YOU lists no
 *      city-built need for gets no suggestion.
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
     * road and three home daycares of the city's own, and with onSiteClinic
     * a clinic too, so the advice has something on site to count. Measured
     * (the calibration probe): general care at 28% (red; amber with the
     * clinic on site), the law school red
     * with no seats for 28, crime red at 2.7x Canada's, the roads near 400%
     * of capacity, childcare 26%, a person caught and not held.
     */
    static Game shortCity(Path root, String name, boolean onSiteClinic) {
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
                g.buildStack(template(g, w[0]), Integer.parseInt(w[1]), true);
            }
            g.simulateMonths(36);
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.buildStack(template(g, "Gravel Road"), 1, false);
            g.buildStack(template(g, "Home Daycare"), 3, false);
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
        theCashCap(root);
        beforeAndAfter(root, g, advice);
        everyMeasure(root, g);
        theStaffingWeighting(root);
        nothingNeeded(root);

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
            out.printf("      suggests %s: %,d x %s (full %,d) for %s, %.4f -> on site %.4f -> %.4f%n",
                    s.need().label(), s.count(), s.template().getName(), s.fullCount(),
                    Formats.INSTANCE.amount(s.price()), s.before(), s.whenOnSite(), s.after());
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
        assertTrue("investors build the market's nine, by BusinessInvestment's own list, and none of the city's five",
                checkInvestors(cats));
    }

    static boolean checkInvestors(List<BuildAdvice.Category> cats) {
        EnumSet<BuildingType> theirs = EnumSet.of(BuildingType.RESIDENTIAL, BuildingType.COMMERCIAL,
                BuildingType.INDUSTRIAL, BuildingType.HEAVY_INDUSTRY, BuildingType.MINING, BuildingType.CONSTRUCTION,
                BuildingType.BUSINESS_SERVICES, BuildingType.AGRICULTURE, BuildingType.RAIL, BuildingType.AUTOMOTIVE,
                BuildingType.LUXURY, BuildingType.HOSPITALITY);
        for (BuildAdvice.Category c : cats) {
            boolean market = theirs.containsAll(c.types());
            if (market == c.cityBuilds()) return false;
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
            boolean onSiteCloses = BuildAdvice.clear(m, BuildAdvice.figure(g, m, BuildAdvice.onSite(g, m)));
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
        out.println("\n--- 3. the count closes the need after what is on site; the price is the quote ---");
        boolean anyOnSite = false;
        double cashLeft = g.getCash();
        for (BuildAdvice.Suggestion s : advice) {
            BuildAdvice.Measure m = s.measure();
            Map<BuildingsTemplate, Integer> site = BuildAdvice.onSite(g, m);
            anyOnSite |= BuildAdvice.units(site) > 0;
            String what = s.need().label() + ", " + s.template().getName();
            assertTrue(what + ": not capped - the city can afford the whole count", !s.capped() && !s.needsCredit());
            assertTrue(what + ": the count takes it off the list",
                    s.closes() && BuildAdvice.clear(m, BuildAdvice.figure(g, m, BuildAdvice.plus(site, s.template(), s.count()))));
            assertTrue(what + ": ...and one fewer does not",
                    !BuildAdvice.clear(m, BuildAdvice.figure(g, m, BuildAdvice.plus(site, s.template(), s.count() - 1))));
            bits(what + ": the price is Game.quoteBuild() for the count", s.price(), g.quoteBuild(s.template(), s.count()).total);
            bits(what + ": before is the figure NEEDS YOU read", s.before(), s.need().value());
            assertTrue(what + ": it fits the land free, or no building that closes it does", fitsOrNone(g, s));
            // The staffed capacity it counted at is the model's: one more of it, in the model's own sum.
            if (m.kind() != BuildAdvice.Kind.ROADS) {
                close(what + ": a unit at today's staffing is the model's own sum, one more standing",
                        unitByTheModel(twin(g), m, s.template()), s.unit(), 1e-9);
            }
            cashLeft -= s.price();
        }
        assertTrue("fixture: at least one suggestion counted what is on site", anyOnSite);
        // WHAT WOULD HELP MOST's "all three ≈ $X" (0.7.38): the model adds the quotes up, not the screen.
        double each = 0;
        for (BuildAdvice.Suggestion s : advice) each += g.quoteBuild(s.template(), s.count()).total;
        assertTrue("fixture: more than one suggestion to add up", advice.size() > 1);
        bits("the suggestions' total is each one's Game.quoteBuild(), added in their order",
                BuildAdvice.quoteTotal(advice), each);
    }

    /** The land rule: the chosen building's whole count fits the free ground, unless none that closes it would. */
    static boolean fitsOrNone(Game g, BuildAdvice.Suggestion s) {
        double free = g.getLandManager().getAvailableSqFt();
        if (s.template().getLandSqFt() * (double) s.fullCount() <= free) return true;
        BuildAdvice.Measure m = s.measure();
        Map<BuildingsTemplate, Integer> site = BuildAdvice.onSite(g, m);
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            boolean cand = m.kind() == BuildAdvice.Kind.ROADS ? t.getCategory() == BuildingType.INFRASTRUCTURE : m.serves(t);
            if (!cand || !(BuildAdvice.unit(g, m, t) > 0)) continue;
            int[] c = BuildAdvice.count(g, m, site, t);
            if (c[1] == 1 && t.getLandSqFt() * (double) c[0] <= free) return false;
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
     * The cash cap: with cash for the suggestions before the largest one and
     * half of it, the largest is cut to the most the cash left affords and
     * the ones before it are whole; with none, every one is the full count,
     * marked as needing credit.
     */
    static void theCashCap(Path root) {
        out.println("\n--- 3b. the cash: the most it affords, after what the ones before took; none, on credit ---");
        Game g = shortCity(root, "short-cash", true);
        List<BuildAdvice.Suggestion> rich = BuildAdvice.suggest(g);
        int big = 0;
        for (int i = 0; i < rich.size(); i++) if (rich.get(i).fullCount() > rich.get(big).fullCount()) big = i;
        BuildAdvice.Suggestion largest = rich.get(big);
        assertTrue("fixture: a suggestion of more than two buildings (" + largest.fullCount() + " x "
                + largest.template().getName() + ")", largest.fullCount() > 2);
        double before = 0;
        for (int i = 0; i < big; i++) before += rich.get(i).price();
        double cash = before + g.quoteBuild(largest.template(), largest.fullCount() / 2).total + 1;
        g.setCashForTest(cash);
        List<BuildAdvice.Suggestion> capped = BuildAdvice.suggest(g);
        boolean wholeBefore = true;
        for (int i = 0; i < big; i++) wholeBefore &= !capped.get(i).capped() && !capped.get(i).needsCredit();
        assertTrue("the suggestions before the largest are whole", wholeBefore);
        BuildAdvice.Suggestion c = capped.get(big);
        double left = cash - before;
        assertTrue("the largest is capped", c.capped() && !c.needsCredit() && c.count() < c.fullCount());
        assertTrue("...at the most the cash the ones before left affords: within it, and one more is not",
                g.quoteBuild(c.template(), c.count()).total <= left
                        && g.quoteBuild(c.template(), c.count() + 1).total > left);
        assertTrue("...and the full count is still the one that closes the need", c.fullCount() == largest.fullCount());
        bits("...its full price is the full count's quote", c.fullPrice(), g.quoteBuild(c.template(), c.fullCount()).total);
        g.setCashForTest(0);
        boolean credit = true;
        for (BuildAdvice.Suggestion b : BuildAdvice.suggest(g)) {
            credit &= b.needsCredit() && !b.capped() && b.count() == b.fullCount();
        }
        assertTrue("with no cash, every one is its full count, marked as needing credit", credit);
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
     */
    static double builtAndRead(Game twin, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> add) {
        BuildingManager bm = twin.getBuildingManager();
        for (Map.Entry<BuildingsTemplate, Integer> e : add.entrySet()) {
            bm.addStack(template(twin, e.getKey().getName()), e.getValue(), true);
        }
        ServicesManager services = twin.getServicesManager();
        services.updateServiceWages(twin.getPopulationManager().getWagesPerType());
        services.updateJobFillRate(twin.getPopulationManager().getJobFillRate());
        services.updateServices();
        return readByTheModel(twin, m);
    }

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
                    return CityNeeds.wouldCome(twin, m.school()) / Math.max(seats, 1);
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
}

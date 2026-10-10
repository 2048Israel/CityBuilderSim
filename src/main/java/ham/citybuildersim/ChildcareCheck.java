package ham.citybuildersim;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Childcare resized (0.7.71, batch N2): the three childcare centres, the
 * build advice's size that fits the need (BuildAdvice, THE SIZE THAT FITS
 * THE NEED), a city's old daycares kept as their ids' new centres, and the
 * test player's childcare rule.
 *
 * WHY. Jerus: "one city had 5k daycares and 2k residential buildings,
 * hilarious, the numbers children and housing wise make sense ... i think we
 * need to resize those, daycares are childcares and childcares are even
 * bigger." A Home Daycare held 8 children and was the cheapest a place to
 * put up, so the advice ordered them by the hundred - 629 in his city of
 * 24,000, 2,951 in the playtest's at m4,000 with a childcare rule on
 * (runs/fixN2-notes.md). Since 0.7.71 ids 15, 16 and 17 are centres of 80,
 * 220 and 360 places, a place cheaper the bigger, and the advice ranks a
 * living care building by its whole order over the places the need lacks.
 *
 * What this has to prove:
 *   1. THREE CENTRES, A PLACE CHEAPER THE BIGGER: ids 15, 16 and 17 are
 *      childcare, named centres (no "Daycare" left), in rising size; a place
 *      in a bigger one costs less to build all in, to keep, to stand on and to
 *      staff, and costs less to run in a played city; every one keeps between
 *      Ontario's infant and preschool ratios of children to an adult.
 *   2. THE SIZE THAT FITS THE NEED: in a town short of hundreds of places the
 *      advice's childcare card is the order whose quote and ground over the
 *      places lacking are the least of the three, to the bit - the Large
 *      Childcare Centre; with the gap brought under one small centre's
 *      places, one Small Childcare Centre - where a place in it costs more
 *      than in the Large; and the card's figure is the advice's own.
 *   3. A CITY'S BUILDINGS KEEP THEIR TYPE: a save holds its childcare by id,
 *      so a city of the old daycares loads as the same count of the ids'
 *      centres, with their places, posts and ground; overbuilt so, the advice
 *      says nothing for childcare and NEEDS YOU does not list it.
 *   4. THE TEST PLAYER'S RULE (-Dplaytest.childcare): where NEEDS YOU lists
 *      childcare it orders the advice's card, building and count; overbuilt,
 *      nothing.
 *   5. THE MAP DRAWS A CENTRE AS CARE: id 15 is no longer drawn as a home;
 *      home care (22) still is.
 *
 * Every fixture causes its condition.
 */
public class ChildcareCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    /** Children to an adult in Ontario's infant groups, 3 adults to 10 (O. Reg. 137/15, Schedule 1): the most adults a place any centre here keeps. */
    static final double ONTARIO_INFANTS_PER_ADULT = 10.0 / 3;

    /** ...and in its preschool groups, 1 to 8: the fewest. */
    static final double ONTARIO_PRESCHOOL_PER_ADULT = 8;

    /** The three centres' ids in buildings.json: the Small Childcare Centre, the Childcare Centre and the Large. */
    static final int SMALL = 15, CENTRE = 16, LARGE = 17;

    /** The advice's measure for childcare, which the sections here read the need, the site and the card through. */
    static final BuildAdvice.Measure CHILDCARE = BuildAdvice.Measure.care(CareType.CHILDCARE);

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-104s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void bits(String label, double actual, double expected) {
        boolean ok = Double.doubleToLongBits(actual) == Double.doubleToLongBits(expected);
        if (!ok) fails++;
        out.printf("%-104s %s  %s against %s%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    static BuildingsTemplate byId(Game g, int id) {
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) if (t.getId() == id) return t;
        throw new IllegalStateException("no template with id " + id);
    }

    static BuildingsTemplate template(Game g, String name) {
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName(name);
        if (t == null) throw new IllegalStateException("no template named " + name);
        return t;
    }

    static int staff(BuildingsTemplate t) {
        int n = 0;
        for (JobType j : JobType.values()) n += t.getJobs(j);
        return n;
    }

    /**
     * The fixture: BuildAdviceCheck's short city with no daycare of its own -
     * nine hundred houses, its schools, college, university and four medical
     * schools for the posts that bring the people, every sector held - played
     * three years: its children far past the founding's 201 places, more
     * than a Large Childcare Centre holds (calibrated: scratch-n2/h/cc-2.txt).
     */
    static Game town(Path root, String name) {
        return town(root, name, 1);
    }

    /** ...with every building `k` times over: at k 4 a city of thousands of children (calibrated: scratch-n2/h/cc-3.txt). */
    static Game town(Path root, String name, int k) {
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
                g.buildStack(template(g, w[0]), Integer.parseInt(w[1]) * k, true);
            }
            g.simulateMonths(36);
            g.setCashForTest(Founding.WEALTHY_CASH);
        });
        return g;
    }

    static CityNeeds.Need need(Game g) {
        return BuildAdvice.needFor(CityNeeds.measure(g, CityNeeds.PLAIN), CHILDCARE);
    }

    static BuildAdvice.Suggestion advice(Game g) {
        CityNeeds.Need need = need(g);
        return need == null ? null
                : BuildAdvice.suggestFor(g, need, CHILDCARE, g.getCash(), g.getLandManager().getAvailableSqFt());
    }

    /** The places the need lacks at a suggestion's own projection: its demand less what is on site. */
    static double lacks(Game g, BuildAdvice.Ahead p) {
        double[] sd = BuildAdvice.supplyDemand(g, CHILDCARE, BuildAdvice.onSite(g, CHILDCARE), p);
        return sd[1] - sd[0];
    }

    /** One centre weighed as suggestFor() weighs it: its count at the demand it opens to, and its figure. */
    record Weighed(BuildingsTemplate t, int count, BuildAdvice.Ahead p, double per) { }

    static Weighed weigh(Game g, BuildingsTemplate t) {
        Map<BuildingsTemplate, Integer> site = BuildAdvice.onSite(g, CHILDCARE);
        double landLeft = g.getLandManager().getAvailableSqFt();
        int[] today = BuildAdvice.count(g, CHILDCARE, site, t, new BuildAdvice.Ahead(0, 1, BuildAdvice.SLACK));
        BuildAdvice.Ahead p = BuildAdvice.opening(g, g.quoteBuild(t, Math.max(1, today[0])).months);
        int n = BuildAdvice.count(g, CHILDCARE, site, t, p)[0];
        double[] sd = BuildAdvice.supplyDemand(g, CHILDCARE, site, p);
        // The figure recomputed here: the order's quote and its ground over the places lacking.
        double per = (g.quoteBuild(t, n).total + BuildAdvice.landValue(g, t.getLandSqFt() * (double) n, landLeft))
                / (sd[1] - sd[0]);
        return new Weighed(t, n, p, per);
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        Path root = Files.createTempDirectory("childcarecheck");

        threeCentres(root);
        sizeThatFits(root);
        keepTheirType(root);
        thePlayer(root);
        theMap(root);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ---------------------------------------------------------------- 1 */

    static void threeCentres(Path root) {
        out.println("--- 1. three centres, a place cheaper the bigger ---");
        GameFiles files = new GameFiles(root.resolve("founded"), root.resolve("founded-no-legacy"));
        Game g = new Game(files);
        quietly(() -> { g.run(); g.simulateMonths(24); });
        BuildingsTemplate[] c = { byId(g, SMALL), byId(g, CENTRE), byId(g, LARGE) };
        boolean care = true, named = true;
        for (BuildingsTemplate t : c) {
            care &= t.getCare() == CareType.CHILDCARE && t.getCategory() == BuildingType.HEALTHCARE;
            named &= t.getName().contains("Childcare Centre") && !t.getName().contains("Daycare");
        }
        int daycares = 0;
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) if (t.getName().contains("Daycare")) daycares++;
        assertTrue("ids 15, 16 and 17 are childcare (" + c[0].getName() + ", " + c[1].getName() + ", " + c[2].getName() + ")", care);
        assertTrue("...named centres, and no building in the catalogue is a daycare any more (" + daycares + ")",
                named && daycares == 0);
        assertTrue(String.format("...in rising size: %d, %d, %d places", c[0].getCapacity(), c[1].getCapacity(), c[2].getCapacity()),
                c[0].getCapacity() < c[1].getCapacity() && c[1].getCapacity() < c[2].getCapacity());
        String[] what = { "to build all in (cash and material at the world price)", "to keep (upkeep)", "to stand on (land)",
                "to staff (posts)", "to run in a played city (upkeep and posts at its wages, BuildCard.runningCost())" };
        for (int k = 0; k < what.length; k++) {
            double[] per = new double[3];
            for (int i = 0; i < 3; i++) {
                BuildingsTemplate t = c[i];
                double v = switch (k) {
                    case 0 -> t.getCashCost() + BuildingManager.MATERIALS_WORLD_PRICE * t.getConstructionMaterials();
                    case 1 -> t.getUpkeep();
                    case 2 -> t.getLandSqFt();
                    case 3 -> staff(t);
                    default -> BuildCard.runningCost(g, t);
                };
                per[i] = v / t.getCapacity();
            }
            assertTrue(String.format("a place costs less %s the bigger the centre: %.4f > %.4f > %.4f", what[k],
                    per[0], per[1], per[2]), per[0] > per[1] && per[1] > per[2]);
        }
        boolean ratios = true;
        StringBuilder said = new StringBuilder();
        for (BuildingsTemplate t : c) {
            double r = t.getCapacity() / (double) staff(t);
            ratios &= r >= ONTARIO_INFANTS_PER_ADULT && r <= ONTARIO_PRESCHOOL_PER_ADULT;
            said.append(String.format(" %.2f", r));
        }
        assertTrue("every centre keeps between Ontario's infant (3:10) and preschool (1:8) ratios: children an adult" + said,
                ratios);
    }

    /* ---------------------------------------------------------------- 2 */

    static void sizeThatFits(Path root) {
        out.println("\n--- 2. the size that fits the need ---");
        BuildAdvice.Suggestion s;

        // Short of thousands: the town four times over.
        Game big = town(root, "town-x4", 4);
        BuildingsTemplate small = byId(big, SMALL), large = byId(big, LARGE);
        s = advice(big);
        double gap = s == null ? 0 : lacks(big, s.ahead());
        out.printf("      the town x4: %,d people, %,.0f children; lacks %,.0f at the card's projection%n",
                big.getPopulationManager().getPopulation(), CareType.CHILDCARE.populationServed(big.getCohorts()), gap);
        assertTrue("fixture: the town four times over lacks more places than five Large Childcare Centres hold",
                s != null && gap > 5 * large.getCapacity());
        if (s != null) {
            theLeast(big, s, "short of thousands");
            assertTrue("...and short of thousands it is not the Small Childcare Centre (" + s.count() + " x "
                    + s.template().getName() + ")", s.template() != small);
        }

        // Short of hundreds: the town (its own catalogue's templates).
        Game g = town(root, "town");
        small = byId(g, SMALL);
        large = byId(g, LARGE);
        s = advice(g);
        gap = s == null ? 0 : lacks(g, s.ahead());
        out.printf("      the town: %,d people, %,.0f children, %,.0f places staffed; lacks %,.0f at the card's projection%n",
                g.getPopulationManager().getPopulation(), CareType.CHILDCARE.populationServed(g.getCohorts()),
                g.getBuildingManager().getStaffedCareCapacity(CareType.CHILDCARE, g.getPopulationManager().getJobFillRate()), gap);
        assertTrue("fixture: the town lacks more places than one Large Childcare Centre holds",
                s != null && gap > large.getCapacity());
        if (s != null) theLeast(g, s, "short of hundreds");

        // Short of a few: standing centres added to the town - Large while what it lacks today
        // (the advice's own test, opening(0)) is more than two of them, then small ones - until
        // that is no more than one small centre's places.
        int guard = 0;
        while (guard++ < 400) {
            double l = lacks(g, BuildAdvice.opening(g, 0));
            if (l <= BuildAdvice.unit(g, CHILDCARE, small)) break;
            BuildingsTemplate add = l > 2 * BuildAdvice.unit(g, CHILDCARE, large) ? large : small;
            quietly(() -> g.buildStack(add, 1, true));
        }
        double today = lacks(g, BuildAdvice.opening(g, 0));
        assertTrue(String.format("fixture: what it lacks today brought under one small centre's places (%.1f of %.1f)", today,
                BuildAdvice.unit(g, CHILDCARE, small)), today > 0 && today <= BuildAdvice.unit(g, CHILDCARE, small));
        BuildAdvice.Suggestion one = advice(g);
        assertTrue("...where the advice still orders", one != null);
        if (one != null) {
            theLeast(g, one, "short of a few");
            double l = lacks(g, one.ahead()), unit = BuildAdvice.unit(g, CHILDCARE, small);
            assertTrue(String.format("...and short of a few that is the Small Childcare Centre, the least count that closes it"
                    + " (%d x %s for %.1f lacking at %.1f each)", one.count(), one.template().getName(), l, unit),
                    one.template() == small && one.count() * unit >= l && (one.count() - 1) * unit < l);
            double landLeft = g.getLandManager().getAvailableSqFt();
            double perSmall = (g.quoteBuild(small, 1).total + BuildAdvice.landValue(g, small.getLandSqFt(), landLeft))
                    / BuildAdvice.unit(g, CHILDCARE, small);
            double perLarge = (g.quoteBuild(large, 1).total + BuildAdvice.landValue(g, large.getLandSqFt(), landLeft))
                    / BuildAdvice.unit(g, CHILDCARE, large);
            out.printf("      a place in one, quote and ground (the rule before 0.7.71): Small %,.4f, Large %,.4f%n",
                    perSmall, perLarge);
            BuildCard.Group group = BuildCard.groups(g, BuildAdvice.HEALTHCARE, CHILDCARE).get(0);
            assertTrue("Build's childcare page shows the three centres", group.cards().size() == 3);
        }
    }

    /** The suggestion is the centre with the least order over the places lacking, each recomputed here, to the bit. */
    static void theLeast(Game g, BuildAdvice.Suggestion s, String when) {
        Weighed best = null;
        StringBuilder said = new StringBuilder();
        for (int id : new int[] { SMALL, CENTRE, LARGE }) {
            Weighed w = weigh(g, byId(g, id));
            said.append(String.format(" [%s: %d, %,.4f a place lacking]", w.t().getName(), w.count(), w.per()));
            if (best == null || w.per() < best.per()) best = w;
        }
        out.printf("     %s:%s%n", when, said);
        assertTrue("the card is the centre whose order, quote and ground, over the places lacking is the least of the three ("
                + when + ")", best != null && s.template() == best.t() && s.count() == best.count());
        if (best != null && s.template() == best.t()) bits("...its figure is that, recomputed here, to the bit", s.pricePerUnit(), best.per());
    }

    /* ---------------------------------------------------------------- 3 */

    static void keepTheirType(Path root) throws Exception {
        out.println("\n--- 3. a city's buildings keep their type ---");
        Game g = town(root, "kept");
        BuildingsTemplate small = byId(g, SMALL);
        double kids = CareType.CHILDCARE.populationServed(g.getCohorts());
        // As many of id 15 as Jerus's city held of it per child (629 for 6,845): eight times the places they need.
        int n = (int) Math.ceil(kids * 629 / 6845.0);
        quietly(() -> g.buildStack(small, n, true));
        double founding = Healthcare.foundingCapacity(CareType.CHILDCARE);
        quietly(() -> g.saveGame(10, "childcarecheck"));
        GameFiles files = g.getGameFiles();
        JsonObject saved = JsonParser.parseString(Files.readString(files.saveFile(10), StandardCharsets.UTF_8)).getAsJsonObject();
        JsonArray held = saved.getAsJsonArray("buildings");
        assertTrue("a save holds the city's buildings by id: index 15 holds " + n, held.size() > SMALL
                && held.get(SMALL).getAsInt() == n);
        Game back = new Game(files);
        quietly(() -> back.loadGameSave(10));
        BuildingManager bm = back.getBuildingManager();
        assertTrue("...and loads as that many of id 15, the Small Childcare Centre", bm.getQuantity(SMALL) == n
                && byId(back, SMALL).getName().equals("Small Childcare Centre"));
        bits("...with its places: the founding's and " + n + " x " + small.getCapacity(),
                bm.getCareCapacity(CareType.CHILDCARE), founding + n * (double) small.getCapacity());
        long posts = bm.getJobArrayPerCategory(BuildingType.HEALTHCARE)[JobType.NO_DIPLOMA.ordinal()];
        long others = 0;
        for (BuildingsTemplate t : bm.getTemplates()) {
            if (t.getCategory() == BuildingType.HEALTHCARE && t.getId() != SMALL) {
                others += (long) bm.getQuantity(t.getId()) * t.getJobs(JobType.NO_DIPLOMA);
            }
        }
        assertTrue("...its posts: " + n + " x " + small.getJobs(JobType.NO_DIPLOMA) + " unskilled",
                posts - others == (long) n * small.getJobs(JobType.NO_DIPLOMA));
        double ground = 0;
        for (BuildingsTemplate t : bm.getTemplates()) ground += bm.getQuantity(t.getId()) * (double) t.getLandSqFt();
        assertTrue(String.format("...and its ground in the city's: %,.0f sq ft of %,.0f", n * (double) small.getLandSqFt(), ground),
                ground >= n * (double) small.getLandSqFt());
        List<CityNeeds.Need> all = CityNeeds.measure(back, CityNeeds.PLAIN);
        CityNeeds.Need need = BuildAdvice.needFor(all, CHILDCARE);
        assertTrue(String.format("fixture: overbuilt - %,.0f places for %,.0f children", bm.getCareCapacity(CareType.CHILDCARE),
                kids), bm.getCareCapacity(CareType.CHILDCARE) > 4 * kids);
        assertTrue("overbuilt so, the advice says nothing for childcare", need == null
                || BuildAdvice.suggestFor(back, need, CHILDCARE, back.getCash(), back.getLandManager().getAvailableSqFt()) == null);
        boolean listed = false;
        for (BuildAdvice.Suggestion s : BuildAdvice.suggest(back, all)) listed |= s.measure().equals(CHILDCARE);
        assertTrue("...nor does the overview, and NEEDS YOU does not list childcare",
                !listed && (need == null || !CityNeeds.biting(all).contains(need)));
    }

    /* ---------------------------------------------------------------- 4 */

    static void thePlayer(Path root) {
        out.println("\n--- 4. the test player's rule (-Dplaytest.childcare) ---");
        Game g = town(root, "player");
        List<CityNeeds.Need> all = CityNeeds.measure(g, CityNeeds.PLAIN);
        CityNeeds.Need need = BuildAdvice.needFor(all, CHILDCARE);
        assertTrue("fixture: NEEDS YOU lists the town's childcare", need != null && CityNeeds.biting(all).contains(need));
        BuildAdvice.Suggestion card = advice(g);
        int[] before = new int[3];
        for (int i = 0; i < 3; i++) {
            BuildingsStacks st = g.getBuildingManager().getStack(byId(g, SMALL + i));
            before[i] = st == null ? 0 : st.getUnderConstruction();
        }
        BuildAdvice.Suggestion[] placed = new BuildAdvice.Suggestion[1];
        PrintStream was = LongPlaytest.out;
        LongPlaytest.out = quiet;
        quietly(() -> placed[0] = LongPlaytest.orderChildcare(g));
        assertTrue("it orders the advice's card", placed[0] != null && card != null
                && placed[0].template() == card.template() && placed[0].count() == card.count());
        if (placed[0] != null) {
            BuildingsStacks st = g.getBuildingManager().getStack(placed[0].template());
            int i = placed[0].template().getId() - SMALL;
            assertTrue("...and that is what went on site: " + placed[0].count() + " x " + placed[0].template().getName(),
                    st != null && st.getUnderConstruction() - before[i] == placed[0].count());
        }
        Game full = town(root, "player-full");
        int kids = (int) CareType.CHILDCARE.populationServed(full.getCohorts());
        quietly(() -> full.buildStack(byId(full, LARGE), kids / 360 * 2 + 2, true));
        BuildAdvice.Suggestion[] none = new BuildAdvice.Suggestion[1];
        quietly(() -> none[0] = LongPlaytest.orderChildcare(full));
        LongPlaytest.out = was;
        int site = 0;
        for (int id : new int[] { SMALL, CENTRE, LARGE }) {
            BuildingsStacks st = full.getBuildingManager().getStack(byId(full, id));
            site += st == null ? 0 : st.getUnderConstruction();
        }
        assertTrue("overbuilt, it orders nothing", none[0] == null && site == 0);
    }

    /* ---------------------------------------------------------------- 5 */

    static void theMap(Path root) {
        out.println("\n--- 5. the map draws a centre as care ---");
        BuildingManager bm = new BuildingManager();
        bm.initializeTemplates();
        BuildingsTemplate small = null, home = null;
        for (BuildingsTemplate t : bm.getTemplates()) {
            if (t.getId() == SMALL) small = t;
            if (t.getId() == 22) home = t;
        }
        assertTrue("id 15 is drawn as care, not as a home", small != null
                && BuildingVisual.of(small).cls() == BuildingVisual.classOf(BuildingType.HEALTHCARE)
                && BuildingVisual.of(small).cls() != BuildingVisual.HOME);
        assertTrue("...and home care (22) still as a home", home != null && BuildingVisual.of(home).cls() == BuildingVisual.HOME);
    }
}

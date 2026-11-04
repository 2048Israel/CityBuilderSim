package ham.citybuildersim;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Land bought from the nation it lies in, checked (WN5; docs/studies/spec-nations.md 6.2-6.4, D21, D22): the land table, each nation's land factor and
 * how it moves, the seller of every plot of a real-Earth world and the territorial sea, an offer's ground split by its sellers, the price each part
 * is struck at, the payment in each seller's money, the save, how often it fires in a real run, and the words the land office writes of it.
 *
 * WHY THIS EXISTS. Land was priced by one formula for the whole world and bought from nobody; WN5 made a plot at Windsor on the Detroit bank dearer
 * than the one across the river, and paid each part to its own nation in its own money. Three slices built it (the table and the two models, the
 * office and the purse, this) and each could be right and the whole wrong: a factor that is 1.0 for Canada and still moves it, a seller read at a
 * plot's centre and a split that counts another ground, a price that is today's to the bit in Canada and a cent off in a city of its own, a purse
 * that takes the dollars from the largest holding and not the seller's. So this holds each against fixtures that CAUSE the condition (a city founded
 * on the river, one in Canada's reference, one at the Strait, a generated world) and against the model's own constants, never a figure typed; and
 * where the model's rule is a search (the 12 nautical miles) the answer is a brute-force search over the data.
 *
 * WHAT IT HAS TO PROVE:
 *
 *   1. THE TABLE. land-2024.json has a line for every nation of the world file; the 25 rows of tools/world/land.tsv read their published figure
 *      (a price an acre times acres a hectare, then the tsv's own rate to US$) with mark 3; every other line is the fit's, mark 1, and no dearer
 *      than the dearest published; the fit's elasticity is positive.
 *   2. CANADA IS 1. level() and factorAt() of the reference are exactly 1.0 in months 0, 1, 600 and 1,488 on a bare table and on both paths; a number
 *      outside 1..N is 1.0.
 *   3. THE FOUNDING, AND HISTORICAL'S 2024. factorAt(n, 0) is level(n) for every nation; on the Historical path in January 2024 it is the published
 *      ratio to Canada's, for every nation whose ratio lies inside the floor and ceiling (the round trip the founding level was built for).
 *   4. RANDOM MOVES IT, WITHIN ITS BOUNDS. On a Random path the factors differ from Historical's and between two seeds, one seed gives them twice,
 *      and every factor stays between FACTOR_FLOOR and FACTOR_CEILING in every January and July from 1900 to 2100, on all three paths.
 *   5. THE SELLERS. The host at Windsor, Halifax and the Strait is the world market's host; the plots either side of the Detroit River sell through
 *      Canada and the United States; an area, no one and a number past the file are the host's; and the territorial sea: a walk off the Moroccan coast
 *      reads Morocco within TERRITORIAL_KM of its land and the host beyond, every plot as a brute-force search over the landmass layer says.
 *   6. THE SPLIT. An offer across the river keeps parts whose areas add up to its own and whose dollars add up to its price; split() of a rectangle
 *      across it counts, class by class, what CityLand counts whole; a block of Canadian ground away from the river is one part and reads no plot.
 *   7. THE PRICE. Halifax's offers, every offer wholly Canadian at Windsor, and every offer of a generated world are priced by the one-part formula to
 *      the bit; an offer wholly the United States' is dearer than that by its factor, to a step of rounding.
 *   8. PAYING. A split offer bought from the vault takes Canada's part from the C$ holding and the United States' from the US$, nothing else; converted,
 *      the treasury pays usd x rate; the receipt names the sellers; the tally keeps km2 and dollars by nation; a Canadian offer and a generated world's
 *      name no seller.
 *   9. THE SAVE. The Windsor city after its purchases, saved and loaded in another folder, has the same offers (sellers and prices) and the same
 *      tally; a save without the three keys loads with no seller named and nothing bought; a generated city's save carries none of them.
 *  10. HOW OFTEN IT FIRES. The Windsor city played 120 months, buying the offers that name a foreign seller and the cheapest: how many offers were
 *      listed split, wholly foreign and wholly the host's, and how many purchases named a seller; the mechanic fires.
 *  11. THE WORDS. LandSellersView on the Windsor offers and on hand-made ones: the split of 62 and 38, three sellers whose rounding must still add to
 *      100, a host's own offer saying nothing, a wholly foreign offer naming its seller, and (WN5b) a split whose shares round differently on their
 *      own (three equal thirds, 50.5 and 49.5) reading the same percents on the card and on the receipt (LandSellersView.receiptLine()).
 *  12. THE HOVER'S QUOTE AWAY FROM THE SCREEN'S THREAD (WN5b). Blocks straddling the Detroit River and the Strait are quoted by two sellers or more;
 *      the cost of a cold quote by level is measured and printed (what moved the hover's quote off the screen's thread); a LandMarket.Quoter made
 *      here and used on another thread gives the office's quote to the bit, block by block; it is the office as it stood when it was made, a purchase
 *      since not seen by it; making one takes no more than a frame; and the map's source quotes through one on its own thread.
 *
 * Every premise is asserted before what rests on it. Without the world file (-Dcbs.world) nothing here runs; without the Earth's data (-Dcbs.earth)
 * sections 1 to 4 run and 5 to 12 say so and are skipped. The one timing it asserts is section 12's making of a quoter (Timing.within; TimingCheck owns the mechanism).
 *
 * @author Jerus
 */
public class LandNationsCheck {

    static int fails = 0;
    static final PrintStream out = System.out;
    static final PrintStream QUIET = new PrintStream(OutputStream.nullOutputStream());

    /** The seeds the Random paths are drawn from. */
    static final long SEED_A = 20261101L, SEED_B = 20261102L;

    /** Where the cities stand: Windsor on the Detroit River (Canada), Detroit across it (the United States), Halifax (Canada, the reference), the Strait of Gibraltar. */
    static final double WINDSOR_LAT = 42.30, WINDSOR_LON = -83.03, DETROIT_LAT = 42.335, DETROIT_LON = -83.045;
    static final double HALIFAX_LAT = 44.64, HALIFAX_LON = -63.57, GIBRALTAR_LAT = 36.14, GIBRALTAR_LON = -5.35;

    /** A hectare in acres: the unit the USDA and Statistics Canada give their prices in (tools.LandTable converts by the same figure). */
    static final double ACRES_PER_HECTARE = ham.citybuildersim.tools.LandTable.ACRES_PER_HECTARE;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-118s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-118s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(QUIET);
        try { r.run(); } finally { System.setOut(real); }
    }

    static <T> T quietlyGet(Supplier<T> s) {
        PrintStream real = System.out;
        System.setOut(QUIET);
        try { return s.get(); } finally { System.setOut(real); }
    }

    /** Two figures within a relative gap (and equal when both are 0). */
    static boolean near(double a, double b, double rel) {
        return Math.abs(a - b) <= rel * Math.max(Math.abs(a), Math.abs(b));
    }

    /** The game month of January of a year. */
    static int januaryOf(int year) { return (year - CityCalendar.EPOCH_YEAR) * 12 + 1; }

    /* =====================================================================
       THE FIXTURES
       ===================================================================== */

    /** A city on a fixture world: its game and its folder. */
    static final class City {
        final Game g;
        final GameFiles files;

        City(String label, Founding founding) {
            this.files = GameFiles.scratch(label);
            this.g = new Game(files);
            quietly(() -> {
                g.newGame(founding);
                g.setCashForTest(Founding.WEALTHY_CASH);
            });
        }

        LandSellers sellers() { return g.getLandSellers(); }

        LandMarket office() { return g.getLandManager().getMarket(); }

        List<LandParcel> offers() { return office().getListing(); }

        void months(int n) { quietly(() -> g.simulateMonths(n)); }
    }

    /** A city as it stood when founded: its offers and the numbers they were priced at (the office's, which move with the month). */
    record Founded(List<LandParcel> offers, double groundUsd, double usLevel) {
        static Founded of(City c) {
            return new Founded(new ArrayList<>(c.offers()), c.office().getGroundUsdPerSqFt(), c.office().getUsPriceLevel());
        }

        /** The one-part dollar formula on an offer's own ground, at the numbers it was listed by. */
        double formula(LandParcel p) {
            double[] km2 = new double[CityLand.AREAS];
            for (int a = 0; a < CityLand.AREAS; a++) km2[a] = p.getKm2(a);
            double[] amounts = new double[CityLand.KINDS];
            for (Resource r : Resource.values()) amounts[r.ordinal()] = p.getAmount(r);
            return LandMarket.price(km2, p.reliefRow(), amounts, groundUsd, usLevel);
        }
    }

    static Founding onEarth(double lat, double lon) { return Founding.defaults().onEarth(lat, lon); }

    static long[] plotOf(World w, double lat, double lon) {
        double[] p = new double[2];
        w.earthTerrain().projection().plotOf(lat, lon, p);
        return new long[] { (long) Math.floor(p[0]), (long) Math.floor(p[1]) };
    }

    /* =====================================================================
       MAIN
       ===================================================================== */

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
        Path wp = WorldData.locate();
        if (wp == null || !Files.isRegularFile(wp)) {
            out.println("world-1.bin not named (-D" + WorldData.PROPERTY + "=<path>) nor at " + WorldData.DATA_FILE + ": no section was run");
            finish(started);
            return;
        }
        try (WorldData file = WorldData.open(wp)) {
            Nations hist = new Nations(file, Nations.Path.HISTORICAL, SEED_A);
            Nations randA = new Nations(file, Nations.Path.RANDOM, SEED_A);
            Nations randB = new Nations(file, Nations.Path.RANDOM, SEED_B);
            LandFactors table = LandFactors.load(file);
            theTable(file, table);
            canadaIsOne(file, hist, randA);
            theFounding(file, hist, randA);
            randomMovesIt(file, hist, randA, randB);
            EarthSite.Data earth = EarthSite.gameData();
            if (!earth.here()) {
                out.println("\nthe game's Earth is not here (" + earth.words() + "): sections 5 to 12 were not run");
            } else {
                out.println("\n--- the fixtures: Windsor, Halifax and the Strait on the real Earth, and a generated world ---");
                City windsor = new City("land-nations-windsor", onEarth(WINDSOR_LAT, WINDSOR_LON));
                City halifax = new City("land-nations-halifax", onEarth(HALIFAX_LAT, HALIFAX_LON));
                City strait = new City("land-nations-strait", onEarth(GIBRALTAR_LAT, GIBRALTAR_LON));
                City generated = new City("land-nations-generated", Founding.defaults());
                Founded windsorFounded = Founded.of(windsor), halifaxFounded = Founded.of(halifax), generatedFounded = Founded.of(generated);
                int usa = file.numberOf("USA");
                LandParcel detroitQuote = detroitQuote(windsor);
                double usFactor = windsor.sellers() == null ? Double.NaN : windsor.sellers().factors().factorAt(usa, windsor.g.getMonth());
                double usFormula = windsorFounded.formula(detroitQuote);
                windsor.months(1);
                halifax.months(1);
                strait.months(1);
                theSellers(file, windsor, halifax, strait);
                theSplit(file, windsor);
                thePrice(file, windsorFounded, windsor, halifaxFounded, halifax, generatedFounded, generated, detroitQuote, usFactor, usFormula);
                paying(file, windsor, halifax, generated);
                theSave(file, windsor);
                howOften(file);
                theWords(file, windsor, windsorFounded, detroitQuote);
                theHover(file, windsor, strait);
            }
        }
        finish(started);
    }

    static void finish(long started) {
        out.printf("%nLandNationsCheck took %.1f s%n", (System.nanoTime() - started) / 1e9);
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* =====================================================================
       1. THE TABLE
       ===================================================================== */

    static void theTable(WorldData file, LandFactors f) throws IOException {
        out.println("--- 1. the table: a line for every nation, 25 published, the rest fitted ---");
        int n = file.nationCount();
        int none = 0, published = 0, estimates = 0, other = 0;
        double dearestPublished = 0, dearestFitted = 0;
        for (int i = 1; i <= n; i++) {
            double usd = f.usdPerHectare(i);
            int mark = f.mark(i);
            if (mark == WorldData.MARK_NONE || "none".equals(f.source(i)) || !(usd > 0) || !Double.isFinite(usd)) {
                none++;
            } else if (mark == WorldData.MARK_PUBLISHED) {
                published++;
                dearestPublished = Math.max(dearestPublished, usd);
            } else if (mark == WorldData.MARK_ESTIMATE) {
                estimates++;
                dearestFitted = Math.max(dearestFitted, usd);
            } else {
                other++;
            }
        }
        check("the world file has " + n + " nations and land-2024.json has a line with a price for every one", n > 0 && none == 0);
        Path tsv = Path.of("tools", "world", "land.tsv");
        if (!Files.isRegularFile(tsv)) {
            out.println("   " + tsv + " is not beside the harness (run it from the repository root): its rows were not read, so the published figures were not compared");
        } else {
            Map<String, Double> usdPer = new HashMap<>();
            List<String[]> rows = new ArrayList<>();
            for (String line : Files.readAllLines(tsv, StandardCharsets.UTF_8)) {
                if (line.isBlank()) continue;
                String[] c = line.split("\t");
                if (c[0].equals("#rate")) {
                    double v = Double.parseDouble(c[2]);
                    usdPer.put(c[1], c[3].equals("usd-per-unit") ? v : 1 / v);
                } else if (!c[0].startsWith("#")) {
                    rows.add(c);
                }
            }
            check("tools/world/land.tsv has 25 published rows (23 Eurostat, the USDA's and Statistics Canada's)", rows.size() == 25);
            double worst = 0;
            int wrong = 0, unmarked = 0, unsourced = 0;
            for (String[] c : rows) {
                int nation = file.numberOf(c[0]);
                double money = c[2].equals("USD") ? 1 : usdPer.getOrDefault(c[2], Double.NaN);
                double want = Double.parseDouble(c[1]) * money * (c[3].equals("acre") ? ACRES_PER_HECTARE : 1);
                double got = nation >= 1 ? f.usdPerHectare(nation) : Double.NaN;
                boolean ok = nation >= 1 && Math.abs(got - want) <= 1e-6 * want;   // written to the cent: half a cent of the smallest of the 25 (about US$5,200 a hectare) is under a millionth
                if (!ok) wrong++;
                else worst = Math.max(worst, Math.abs(got - want) / want);
                if (nation < 1 || f.mark(nation) != WorldData.MARK_PUBLISHED) unmarked++;
                if (nation < 1 || !c[5].equals(f.source(nation))) unsourced++;
            }
            report("...each reads its published figure (per acre times acres a hectare, then the tsv's own rate to US$) to a millionth", wrong == 0 && !rows.isEmpty(),
                    String.format("%d rows, worst gap %.2e", rows.size(), worst));
            check("...with mark 3, and its source's name", unmarked == 0 && unsourced == 0);
            check("...and no other line is published: " + published + " nations are marked 3", published == rows.size());
        }
        check("every other line is the fit's, mark 1 (" + estimates + " nations, none of another mark)", estimates == n - published && other == 0);
        report("...and no fitted nation is dearer than the dearest published", dearestFitted <= dearestPublished && dearestPublished > 0,
                String.format("dearest published US$%,.0f a hectare, dearest fitted US$%,.0f", dearestPublished, dearestFitted));
        report("the fit's elasticity of price on output a head is positive", f.elasticity() > 0 && Double.isFinite(f.elasticity()), String.format("%.3f", f.elasticity()));
    }

    /* =====================================================================
       2. CANADA IS 1
       ===================================================================== */

    static void canadaIsOne(WorldData file, Nations hist, Nations rand) {
        out.println("\n--- 2. Canada is 1: exactly, in every month, on every path ---");
        int can = file.numberOf("CAN"), n = file.nationCount();
        check("premise: the world file has Canada", can >= 1);
        LandFactors bare = LandFactors.load(file), onHist = LandFactors.load(file), onRand = LandFactors.load(file);
        onHist.setPaths(hist);
        onRand.setPaths(rand);
        check("premise: the table's reference is Canada, and the two paths are handed", bare.reference() == can && !bare.hasPaths() && onHist.hasPaths() && onRand.hasPaths());
        int[] months = { 0, 1, 600, 1488 };
        String[] names = { "a bare table", "the Historical path", "a Random path" };
        LandFactors[] each = { bare, onHist, onRand };
        for (int k = 0; k < each.length; k++) {
            boolean exact = each[k].level(can) == 1.0;
            for (int m : months) exact &= each[k].factorAt(can, m) == 1.0;
            check("Canada's level and factor are 1.0 exactly in months 0, 1, 600 and 1,488, on " + names[k], exact);
            boolean outside = true;
            for (int number : new int[] { 0, -1, n + 1, n + 15, Integer.MAX_VALUE }) {
                outside &= each[k].level(number) == 1.0;
                for (int m : months) outside &= each[k].factorAt(number, m) == 1.0;
            }
            check("...and a number outside 1.." + n + " is 1.0, on " + names[k], outside);
        }
        boolean moves = false;
        for (int i = 1; i <= n; i++) moves |= i != can && onHist.factorAt(i, januaryOf(2024)) != onHist.level(i);
        check("...while the other nations' factors do move on the Historical path (the table is not simply 1 everywhere)", moves);
    }

    /* =====================================================================
       3. THE FOUNDING, AND HISTORICAL'S 2024
       ===================================================================== */

    static void theFounding(WorldData file, Nations hist, Nations rand) {
        out.println("\n--- 3. the founding is the level; Historical meets the published ratio in January 2024 ---");
        int can = file.numberOf("CAN"), n = file.nationCount();
        int jan2024 = januaryOf(LandFactors.BASE_YEAR);
        check("premise: January " + LandFactors.BASE_YEAR + " is game month " + jan2024, CityCalendar.yearOf(jan2024) == LandFactors.BASE_YEAR && CityCalendar.monthOfYear(jan2024) == 1);
        LandFactors onHist = LandFactors.load(file), onRand = LandFactors.load(file);
        onHist.setPaths(hist);
        onRand.setPaths(rand);
        int atFounding = 0, atFoundingMonth1 = 0;
        for (int i = 1; i <= n; i++) {
            if (onHist.factorAt(i, 0) == onHist.level(i) && onRand.factorAt(i, 0) == onRand.level(i)) atFounding++;
            if (onHist.factorAt(i, 1) == onHist.level(i) && onRand.factorAt(i, 1) == onRand.level(i)) atFoundingMonth1++;
        }
        check("factorAt(n, 0) is level(n) for all " + n + " nations, on Historical and on Random", atFounding == n);
        check("...and so is January of " + CityCalendar.EPOCH_YEAR + ", the first month of play (month 1)", atFoundingMonth1 == n);
        double refUsd = onHist.usdPerHectare(can);
        int inside = 0, outside = 0, broken = 0;
        double worst = 0;
        StringBuilder bad = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            if (i == can) continue;
            double ratio = onHist.usdPerHectare(i) / refUsd;
            if (!(ratio >= LandFactors.FACTOR_FLOOR && ratio <= LandFactors.FACTOR_CEILING)) {
                outside++;
                continue;
            }
            inside++;
            double gap = Math.abs(onHist.factorAt(i, jan2024) - ratio) / ratio;
            worst = Math.max(worst, gap);
            if (gap > 1e-9) {
                broken++;
                if (bad.length() < 200) bad.append(file.nation(i).iso3()).append(String.format(" %.4g vs %.4g; ", onHist.factorAt(i, jan2024), ratio));
            }
        }
        report("on the Historical path in January " + LandFactors.BASE_YEAR + " every nation whose ratio lies in " + LandFactors.FACTOR_FLOOR + ".." + LandFactors.FACTOR_CEILING
                        + " is the published ratio to Canada's, to a part in a billion", broken == 0 && inside > 0,
                String.format("%d of %d nations (%d ratios outside the bounds left out), worst gap %.2e %s", inside, n - 1, outside, worst, bad));
        check("...nearly all of them: the bounds leave out under a tenth of the nations", outside * 10 < n);
    }

    /* =====================================================================
       4. RANDOM MOVES IT, WITHIN ITS BOUNDS
       ===================================================================== */

    static void randomMovesIt(WorldData file, Nations hist, Nations randA, Nations randB) {
        out.println("\n--- 4. a Random path moves the factors, and never past the floor or the ceiling ---");
        int can = file.numberOf("CAN"), n = file.nationCount();
        int jan2024 = januaryOf(LandFactors.BASE_YEAR);
        LandFactors h = LandFactors.load(file), a = LandFactors.load(file), a2 = LandFactors.load(file), b = LandFactors.load(file);
        h.setPaths(hist);
        a.setPaths(randA);
        a2.setPaths(new Nations(file, Nations.Path.RANDOM, SEED_A));
        b.setPaths(randB);
        int differHist = 0, differSeeds = 0, repeated = 0;
        for (int i = 1; i <= n; i++) {
            if (i == can) continue;
            if (!near(a.factorAt(i, jan2024), h.factorAt(i, jan2024), 1e-9)) differHist++;
            if (!near(a.factorAt(i, jan2024), b.factorAt(i, jan2024), 1e-9)) differSeeds++;
            if (a.factorAt(i, jan2024) == a2.factorAt(i, jan2024) && a.factorAt(i, jan2024 + 5) == a2.factorAt(i, jan2024 + 5)) repeated++;
        }
        report("in January " + LandFactors.BASE_YEAR + " a Random path's factors differ from Historical's for most nations", differHist * 2 > n - 1, differHist + " of " + (n - 1));
        report("...and the two seeds' differ from each other for most", differSeeds * 2 > n - 1, differSeeds + " of " + (n - 1));
        check("...while one seed drawn twice (two Nations, one seed) gives the same factors in every nation, in January and in June", repeated == n - 1);
        LandFactors[] paths = { h, a, b };
        String[] names = { "Historical", "Random, seed A", "Random, seed B" };
        for (int k = 0; k < paths.length; k++) {
            int outOfBounds = 0, notNumbers = 0, floor = 0, ceiling = 0, looked = 0;
            for (int year = CityCalendar.EPOCH_YEAR; year <= 2100; year++) {
                for (int month : new int[] { januaryOf(year), januaryOf(year) + 6 }) {
                    for (int i = 1; i <= n; i++) {
                        double v = paths[k].factorAt(i, month);
                        looked++;
                        if (!Double.isFinite(v)) notNumbers++;
                        else if (v < LandFactors.FACTOR_FLOOR || v > LandFactors.FACTOR_CEILING) outOfBounds++;
                        if (year == 2100 && month == januaryOf(year)) {
                            if (v == LandFactors.FACTOR_FLOOR) floor++;
                            if (v == LandFactors.FACTOR_CEILING) ceiling++;
                        }
                    }
                }
            }
            report("every factor is a number between " + LandFactors.FACTOR_FLOOR + " and " + LandFactors.FACTOR_CEILING + " in every January and July from "
                            + CityCalendar.EPOCH_YEAR + " to 2100, on " + names[k], outOfBounds == 0 && notNumbers == 0 && looked > 0,
                    String.format("%,d looks; in January 2100 %d nations at the floor, %d at the ceiling", looked, floor, ceiling));
        }
    }

    /* =====================================================================
       5. THE SELLERS
       ===================================================================== */

    static void theSellers(WorldData file, City windsor, City halifax, City strait) {
        out.println("\n--- 5. the sellers: the host, either bank of the river, the areas, the territorial sea ---");
        int can = file.numberOf("CAN"), usa = file.numberOf("USA"), n = file.nationCount();
        LandSellers w = windsor.sellers(), h = halifax.sellers(), s = strait.sellers();
        check("premise: a city on the real Earth with the world file here has sellers", w != null && h != null && s != null);
        if (w == null || h == null || s == null) return;
        check("the host at Windsor is Canada, and is the world market's host for the same city", w.host() == can && windsor.g.getWorldMarket() != null
                && windsor.g.getWorldMarket().host() == w.host());
        check("...at Halifax is Canada, and the world market's", h.host() == can && halifax.g.getWorldMarket() != null && halifax.g.getWorldMarket().host() == h.host());
        report("...at the Strait is the nation the world market finds (" + file.nation(s.host()).name() + ")", strait.g.getWorldMarket() != null
                && strait.g.getWorldMarket().host() == s.host(), "nation " + s.host());
        World ww = windsor.g.getWorld();
        long[] canada = plotOf(ww, WINDSOR_LAT, WINDSOR_LON), america = plotOf(ww, DETROIT_LAT, DETROIT_LON);
        check("a plot at Windsor sells through Canada and a plot across the Detroit River through the United States ("
                + file.nation(can).name() + ", " + file.nation(usa).name() + ")", w.sellerAt(canada[0], canada[1]) == can && w.sellerAt(america[0], america[1]) == usa);
        check("premise: the two points are two plots, not one", canada[0] != america[0] || canada[1] != america[1]);
        int owners = file.nations().size();
        boolean nationsSellTheirOwn = true;
        for (int i = 1; i <= n; i++) nationsSellTheirOwn &= w.sellerOfOwner(i) == i;
        check("a nation's own ground is its own to sell: sellerOfOwner(1.." + n + ") is the number itself", nationsSellTheirOwn);
        boolean areasNamed = false, areasHost = true;
        for (int a = n + 1; a <= owners; a++) {
            areasNamed |= file.nation(a).seller() != null;
            areasHost &= w.sellerOfOwner(a) == w.host();
        }
        check("premise: none of the file's " + (owners - n) + " areas names a seller", !areasNamed);
        check("...so every area (" + (n + 1) + ".." + owners + ") is the host's to sell, and so are no one (0) and a number past the file", areasHost
                && w.sellerOfOwner(0) == w.host() && w.sellerOfOwner(owners + 1) == w.host() && w.sellerOfOwner(255) == w.host());
        territorialSea(file, strait);
    }

    /** What a walk of plots found: how many sea plots it read, how many of a foreign nation's sea were kept and how many given to the host, and the plots that disagreed with the search. */
    static final class Walk {
        int salt, kept, given, wrong;
        boolean givenOwnedByMorocco;
        final StringBuilder bad = new StringBuilder();
        double lastKeptKm = Double.NaN, firstGivenKm = Double.NaN;
        boolean givenThenKept;
    }

    static void territorialSea(WorldData file, City strait) {
        LandSellers sellers = strait.sellers();
        World w = strait.g.getWorld();
        EarthData data = w.earthTerrain().data();
        int mar = file.numberOf("MAR");
        check("premise: the Strait city's world has a landmass layer on the owner layer's grid (240 cells a degree), and the file has Morocco",
                w.hasLandmasses() && data.landmassCellsPerDegree() == WorldData.OWNER_CELLS && mar >= 1);
        Walk across = walk(file, sellers, w, 36.01, -5.60, 35.40, -6.20, 31);
        Walk west = walk(file, sellers, w, 35.20, -6.15, 35.20, -7.35, 41);
        report("a walk south-west across the Strait: each of its " + across.salt + " sea plots sells through the nation a brute-force search of the landmass layer says",
                across.wrong == 0 && across.salt > 0, across.bad.toString());
        report("a walk due west off Larache: each of its " + west.salt + " sea plots does", west.wrong == 0 && west.salt > 0, west.bad.toString());
        report("...and that walk crossed from Morocco's sea to the host's: " + west.kept + " plots kept for Morocco, then " + west.given + " given to " + file.nation(sellers.host()).name(),
                west.kept > 0 && west.given > 0 && !west.givenThenKept && west.lastKeptKm <= LandSellers.TERRITORIAL_KM && west.firstGivenKm > LandSellers.TERRITORIAL_KM,
                String.format("the last plot kept is %.1f km from Morocco's land, the first given %.1f km (the limit %.1f km)", west.lastKeptKm, west.firstGivenKm, LandSellers.TERRITORIAL_KM));
        check("...and the owner layer gave Morocco every plot of the stretch that was given away (the layer's sea is a tile's nearest owner's, the rule's work is the cut)", west.givenOwnedByMorocco);
    }

    /**
     * Walks `samples` points from (lat0, lon0) to (lat1, lon1), takes the plot under each and checks its seller against the rule written out the slow way:
     * the owner at the plot's blend point sells it; a sea plot of a foreign nation sells through it only when a land cell of that nation lies within
     * LandSellers.TERRITORIAL_KM of the 15" cell holding the plot's point, found by looking at every cell of the box round it.
     */
    static Walk walk(WorldData file, LandSellers sellers, World w, double lat0, double lon0, double lat1, double lon1, int samples) {
        Walk walk = new Walk();
        EarthProj proj = w.earthTerrain().projection();
        EarthData data = w.earthTerrain().data();
        int host = sellers.host();
        int mar = file.numberOf("MAR");
        double[] ll = new double[2];
        long last = -1;
        boolean sawGiven = false;
        boolean givenAllMorocco = true;
        for (int i = 0; i < samples; i++) {
            double lat = lat0 + (lat1 - lat0) * i / (samples - 1), lon = lon0 + (lon1 - lon0) * i / (samples - 1);
            long[] plot = plotOf(w, lat, lon);
            long id = plot[0] * World.SIDE + plot[1];
            if (id == last) continue;
            last = id;
            long t = World.TILE;
            EarthProj.Blend blend = proj.blend(Math.floorDiv(plot[0], t) * t, Math.floorDiv(plot[1], t) * t, t);
            if (!blend.latLon(plot[0] + 0.5, plot[1] + 0.5, ll)) continue;
            int owner = file.ownerAt(ll[0], ll[1]);
            int seller = sellers.sellerOfOwner(owner);
            boolean salt = World.salt(w.terrainAt(plot[0], plot[1]));
            double km = Double.NaN;
            int want = seller;
            if (salt && seller != host) {
                km = nearestLandKm(file, data, sellers, ll[0], ll[1], seller);
                want = km <= LandSellers.TERRITORIAL_KM ? seller : host;
            }
            int got = sellers.sellerAt(plot[0], plot[1]);
            if (salt) {
                walk.salt++;
                if (seller != host && want == seller) {
                    walk.kept++;
                    walk.lastKeptKm = km;
                    if (sawGiven) walk.givenThenKept = true;
                } else if (seller != host) {
                    walk.given++;
                    if (!sawGiven) walk.firstGivenKm = km;
                    sawGiven = true;
                    givenAllMorocco &= owner == mar;
                }
            }
            if (got != want) {
                walk.wrong++;
                if (walk.bad.length() < 300) walk.bad.append(String.format("(%.3f,%.3f) owner %d read %d wanted %d at %.1f km; ", ll[0], ll[1], owner, got, want, km));
            }
        }
        walk.givenOwnedByMorocco = givenAllMorocco;
        return walk;
    }

    /**
     * The least great-circle distance, in kilometres, from the centre of the 15" cell holding a point to the centre of a land cell (the landmass layer's)
     * whose owner sells through `seller`, looking at every cell of a box that holds the circle of TERRITORIAL_KM; infinity when there is none that near.
     */
    static double nearestLandKm(WorldData file, EarthData data, LandSellers sellers, double lat, double lon, int seller) {
        int cpd = WorldData.OWNER_CELLS;
        double radius = file.dial("EARTH_RADIUS_KM");
        long row = data.landmassRow(lat), col = data.landmassCol(lon);
        double lat0 = 90 - (row + 0.5) / cpd, lon0 = (col + 0.5) / cpd - 180;
        double cellKm = radius * Math.PI / 180 / cpd;
        long dRows = (long) Math.ceil(LandSellers.TERRITORIAL_KM / cellKm) + 1;
        long dCols = (long) Math.ceil(LandSellers.TERRITORIAL_KM / (cellKm * Math.cos(Math.toRadians(lat0)))) + 1;
        double best = Double.POSITIVE_INFINITY;
        for (long r = row - dRows; r <= row + dRows; r++) {
            double la = 90 - (r + 0.5) / cpd;
            if (la > 90 || la < -90) continue;
            for (long c = col - dCols; c <= col + dCols; c++) {
                double lo = (c + 0.5) / cpd - 180;
                if (data.landmassAt(la, lo) == 0) continue;
                if (sellers.sellerOfOwner(file.ownerAt(la, lo)) != seller) continue;
                best = Math.min(best, greatCircleKm(radius, lat0, lon0, la, lo));
            }
        }
        return best;
    }

    static double greatCircleKm(double radius, double lat1, double lon1, double lat2, double lon2) {
        double p1 = Math.toRadians(lat1), p2 = Math.toRadians(lat2), dp = p2 - p1, dl = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dp / 2) * Math.sin(dp / 2) + Math.cos(p1) * Math.cos(p2) * Math.sin(dl / 2) * Math.sin(dl / 2);
        return 2 * radius * Math.asin(Math.min(1, Math.sqrt(a)));
    }

    /* =====================================================================
       6. THE SPLIT
       ===================================================================== */

    static void theSplit(WorldData file, City windsor) {
        out.println("\n--- 6. the split: an offer across the river, a rectangle across it, a block of Canada's own ground ---");
        int can = file.numberOf("CAN"), usa = file.numberOf("USA");
        LandSellers sellers = windsor.sellers();
        CityLand land = windsor.g.getCityLand();
        World w = windsor.g.getWorld();
        List<LandParcel> offers = windsor.offers();
        List<LandParcel> split = new ArrayList<>();
        for (LandParcel p : offers) if (p.getSellers().size() > 1) split.add(p);
        report("premise: the founding's offers include some that cross the river (named by more than one seller)", !split.isEmpty() && offers.size() > split.size(),
                split.size() + " of " + offers.size() + " offers: " + split.stream().map(LandParcel::where).toList());
        if (sellers == null || split.isEmpty()) return;
        double worstKm2 = 0, worstUsd = 0;
        int areasOff = 0, dollarsOff = 0, negative = 0, exactDollars = 0;
        for (LandParcel p : offers) {
            double km2 = 0, usd = 0;
            for (LandParcel.Sale s : p.getSellers()) {
                km2 += s.km2();
                usd += s.usd();
                if (s.usd() < 0 || s.km2() < 0) negative++;
            }
            if (!p.getSellers().isEmpty()) {
                worstKm2 = Math.max(worstKm2, Math.abs(km2 - p.getKm2()) / Math.max(p.getKm2(), 1e-300));
                worstUsd = Math.max(worstUsd, Math.abs(usd - p.getPriceUsd()) / Math.max(p.getPriceUsd(), 1e-300));
                if (!near(km2, p.getKm2(), 1e-9)) areasOff++;
                if (!near(usd, p.getPriceUsd(), 1e-12)) dollarsOff++;
                if (usd == p.getPriceUsd()) exactDollars++;
            }
        }
        report("every offer names its sellers, and the parts' areas add up to the offer's own to a part in a billion", areasOff == 0 && negative == 0 && offers.stream().noneMatch(p -> p.getSellers().isEmpty()),
                String.format("%d offers, worst gap %.2e", offers.size(), worstKm2));
        report("...and their dollars add up to its price (to the last bit in " + exactDollars + " of " + offers.size() + ", the rest to a rounding of the last place)", dollarsOff == 0,
                String.format("worst gap %.2e", worstUsd));
        boolean bothNations = false;
        for (LandParcel p : split) bothNations |= p.getSellers().stream().map(LandParcel.Sale::seller).collect(java.util.stream.Collectors.toSet()).equals(Set.of(can, usa));
        check("...an offer across the river is sold by Canada and by the United States, both", bothNations);

        int mismatched = 0;
        for (LandParcel p : offers) {
            List<LandSellers.Part> parts = sellers.split(land, p.getX0(), p.getY0(), p.getX1(), p.getY1(), p.getKm2(CityLand.SEA));
            if (!Arrays.equals(sum(parts), land.unownedCounts(p.getX0(), p.getY0(), p.getX1(), p.getY1()))) mismatched++;
        }
        check("split() of each of the " + offers.size() + " offers' rectangles counts, class by class, what CityLand counts whole", mismatched == 0);

        // the rectangle round Windsor and Detroit, from a corner north-west of Detroit's bank to one south-east of Windsor's: the river runs through it
        long[] a = plotOf(w, 42.345, -83.06), b = plotOf(w, 42.295, -83.00);
        long x0 = Math.min(a[0], b[0]), x1 = Math.max(a[0], b[0]), y0 = Math.min(a[1], b[1]), y1 = Math.max(a[1], b[1]);
        double sea = land.groundWithRelief(x0, y0, x1, y1).km2()[CityLand.SEA];
        long before = sellers.plotsRead();
        List<LandSellers.Part> river = sellers.split(land, x0, y0, x1, y1, sea);
        long[] whole = land.unownedCounts(x0, y0, x1, y1);
        Set<Integer> who = new HashSet<>();
        for (LandSellers.Part part : river) who.add(part.seller());
        check("a rectangle of " + (x1 - x0) + " x " + (y1 - y0) + " plots across the river splits into Canada's part and the United States', and the parts count class by class what the whole does",
                who.equals(Set.of(can, usa)) && river.size() == 2 && Arrays.equals(sum(river), whole));
        check("...the larger part first (the order split() says), each with ground", river.size() == 2 && river.get(0).plots() >= river.get(1).plots() && river.get(1).plots() > 0);
        check("...and a border ran through it, so the plots of its tiles were read one by one", sellers.plotsRead() > before);

        int single = 0, quiet = 0, blocks = 0;
        long readBefore = sellers.plotsRead();
        for (double[] at : new double[][] { { 42.20, -82.95 }, { 42.22, -82.92 }, { 42.18, -82.97 } }) {
            long[] c = plotOf(w, at[0], at[1]);
            long bx0 = c[0] - 8, by0 = c[1] - 8, bx1 = c[0] + 8, by1 = c[1] + 8;
            double blockSea = land.groundWithRelief(bx0, by0, bx1, by1).km2()[CityLand.SEA];
            long beforeBlock = sellers.plotsRead();
            List<LandSellers.Part> parts = sellers.split(land, bx0, by0, bx1, by1, blockSea);
            blocks++;
            if (parts.size() == 1 && parts.get(0).seller() == can && Arrays.equals(parts.get(0).counts(), land.unownedCounts(bx0, by0, bx1, by1))) single++;
            if (sellers.plotsRead() == beforeBlock) quiet++;
        }
        check("a block of Canada's own ground away from the river, three of them, is one part (Canada's) counted as CityLand counts it", single == blocks && blocks == 3);
        check("...and reads no plot", quiet == blocks && sellers.plotsRead() == readBefore);
    }

    static long[] sum(List<LandSellers.Part> parts) {
        long[] total = new long[CityLand.WIDE];
        for (LandSellers.Part p : parts) for (int i = 0; i < total.length; i++) total[i] += p.counts()[i];
        return total;
    }

    /* =====================================================================
       7. THE PRICE
       ===================================================================== */

    /** A block of the United States' ground in Detroit, quoted by the Windsor office: a draft of it, nothing kept. */
    static LandParcel detroitQuote(City windsor) {
        long[] d = plotOf(windsor.g.getWorld(), 42.36, -83.05);
        return windsor.office().quote(d[0] - 8, d[1] - 8, d[0] + 8, d[1] + 8);
    }

    static void thePrice(WorldData file, Founded windsorF, City windsor, Founded halifaxF, City halifax, Founded generatedF, City generated, LandParcel detroit,
                         double usFactor, double usFormula) {
        out.println("\n--- 7. the price: the one-part formula to the bit where the seller is the reference, its factor times that where it is not ---");
        int can = file.numberOf("CAN"), usa = file.numberOf("USA");
        check("premise: Halifax's offers stand (" + halifaxF.offers().size() + ") each with one seller, Canada",
                !halifaxF.offers().isEmpty() && halifaxF.offers().stream().allMatch(p -> p.getSellers().size() == 1 && p.getSellers().get(0).seller() == can));
        int off = 0;
        for (LandParcel p : halifaxF.offers()) if (p.getPriceUsd() != halifaxF.formula(p)) off++;
        check("a Halifax offer is priced as LandMarket.price() of its own ground, the same bits, for all " + halifaxF.offers().size() + " (Canada is the factor 1.0)", off == 0);

        int canadian = 0;
        off = 0;
        for (LandParcel p : windsorF.offers()) {
            if (p.getSellers().size() == 1 && p.getSellers().get(0).seller() == can) {
                canadian++;
                if (p.getPriceUsd() != windsorF.formula(p)) off++;
            }
        }
        check("an offer at Windsor wholly Canada's is priced so too, the same bits (" + canadian + " of " + windsorF.offers().size() + ")", canadian > 0 && off == 0);

        check("premise: the generated world's city has no sellers, and none of its " + generatedF.offers().size() + " offers names one", generated.sellers() == null
                && !generatedF.offers().isEmpty() && generatedF.offers().stream().allMatch(p -> p.getSellers().isEmpty()));
        off = 0;
        for (LandParcel p : generatedF.offers()) if (p.getPriceUsd() != generatedF.formula(p)) off++;
        check("a generated world's offer is priced as LandMarket.price() of its own ground, the same bits, for all of them", off == 0);

        check("premise: the quoted block in Detroit is wholly the United States' (" + file.nation(usa).name() + "), and its factor is above Canada's",
                detroit != null && detroit.getSellers().size() == 1 && detroit.getSellers().get(0).seller() == usa && usFactor > 1);
        if (detroit == null || detroit.getSellers().size() != 1) return;
        double price = detroit.getPriceUsd();
        double slack = (1 + usFactor) / 2 * LandMarket.PRICE_STEP_USD;
        report("...and is dearer than the one-part formula by the United States' factor, to the two roundings (the formula's, the sum's)",
                price > usFormula && Math.abs(price - usFactor * usFormula) <= slack + 1e-9,
                String.format("factor %.4f; US$%,.3fk against the formula's US$%,.3fk, a step %.4fk", usFactor, price, usFormula, LandMarket.PRICE_STEP_USD));
    }

    /* =====================================================================
       8. PAYING
       ===================================================================== */

    static void paying(WorldData file, City windsor, City halifax, City generated) {
        out.println("\n--- 8. paying: each part from the vault's holding of its seller's money, or converted at usd x rate; the receipt; the tally ---");
        int can = file.numberOf("CAN"), usa = file.numberOf("USA");
        Game g = windsor.g;
        ForeignAccounts fx = g.getForeignAccounts();
        Nations nations = g.getNations();
        String canMoney = nations.year(can, Nations.yearOf(g.getMonth())).money(), usMoney = nations.year(usa, Nations.yearOf(g.getMonth())).money();
        int kCan = ForeignAccounts.moneyIndex(canMoney), kUs = ForeignAccounts.moneyIndex(usMoney);
        check("premise: in this month Canada's money is the C$ and the United States' the US$, both among the vault's eight, and the C$ has a rate",
                "CAD".equals(canMoney) && "USD".equals(usMoney) && kCan >= 1 && kUs == 0 && fx.hasRate(kCan));
        List<LandParcel> split = new ArrayList<>();
        for (LandParcel p : windsor.offers()) {
            if (p.getSellers().size() == 2 && p.getSellers().stream().map(LandParcel.Sale::seller).collect(java.util.stream.Collectors.toSet()).equals(Set.of(can, usa))) split.add(p);
        }
        check("premise: two offers across the river stand (one to buy from the vault, one converting)", split.size() >= 2);
        if (split.size() < 2 || kCan < 1 || kUs != 0) return;

        // from the vault: the C$ part from the C$ holding, the US$ part from the US$ holding, nothing else moves
        LandParcel fromVault = split.get(0);
        LandParcel.Sale canPart = part(fromVault, can), usPart = part(fromVault, usa);
        quietly(() -> {
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.buyForeignCurrency(10 * fromVault.getPriceUsd() * fx.getRate(), canMoney);
            g.buyForeignCurrency(10 * fromVault.getPriceUsd() * fx.getRate(), usMoney);
            g.setLandPaidFromVault(true);
        });
        check("premise: the vault holds the C$ and the US$ each at many times the offer's part", fx.getHoldingUsd(kCan) > 3 * canPart.usd() && fx.getHoldingUsd(kUs) > 3 * usPart.usd());
        double[] held = new double[ForeignAccounts.MONEYS];
        for (int k = 0; k < held.length; k++) held[k] = fx.getHoldingUsd(k);
        double cash = g.getCash(), kmCan = g.getLandBoughtKm2(can), kmUs = g.getLandBoughtKm2(usa), kmNone = g.getLandBoughtKm2(0);
        double usdCan = g.getLandBoughtUsd(can), usdUs = g.getLandBoughtUsd(usa), usdNone = g.getLandBoughtUsd(0);
        boolean bought = quietlyGet(() -> g.buyLandParcel(fromVault.getId()));
        check("the offer across the river is bought out of the vault", bought);
        check(String.format("...the C$ holding fell by Canada's part (US$%,.3fk) to a part in a billion", canPart.usd()), near(held[kCan] - fx.getHoldingUsd(kCan), canPart.usd(), 1e-9));
        check("...the US$ holding by the United States' part, and by nothing else", near(held[kUs] - fx.getHoldingUsd(kUs), usPart.usd(), 1e-9));
        boolean untouched = true;
        for (int k = 1; k < held.length; k++) untouched &= k == kCan || fx.getHoldingUsd(k) == held[k];
        check("...no other money's holding moved, and no cash went", untouched && g.getCash() == cash);
        String receipt = g.getLastLandReceipt();
        check("...the receipt says it came out of the vault and names the sellers: \"" + receipt.substring(Math.max(0, receipt.indexOf("Sold by"))) + "\"",
                receipt.contains("out of the vault") && receipt.contains("Sold by") && receipt.contains(file.nation(can).name()) && receipt.contains(file.nation(usa).name()));
        check("...and the tally took each part: its km2 and its dollars by nation, and none to the no-seller index",
                near(g.getLandBoughtKm2(can) - kmCan, canPart.km2(), 1e-12) && near(g.getLandBoughtKm2(usa) - kmUs, usPart.km2(), 1e-12)
                        && near(g.getLandBoughtUsd(can) - usdCan, canPart.usd(), 1e-12) && near(g.getLandBoughtUsd(usa) - usdUs, usPart.usd(), 1e-12)
                        && g.getLandBoughtKm2(0) == kmNone && g.getLandBoughtUsd(0) == usdNone);

        // converting: the treasury pays usd x rate, as ever
        LandParcel converted = windsor.office().find(split.get(1).getId());
        check("premise: the second offer across the river still stands", converted != null);
        if (converted == null) return;
        quietly(() -> {
            g.setLandPaidFromVault(false);
            g.setCashForTest(Founding.WEALTHY_CASH);
        });
        double rate = fx.getRate(), reserves = fx.getReservesUsd();
        cash = g.getCash();
        double usd = converted.getPriceUsd();
        LandParcel.Sale canPart2 = part(converted, can), usPart2 = part(converted, usa);
        kmCan = g.getLandBoughtKm2(can);
        kmUs = g.getLandBoughtKm2(usa);
        usdCan = g.getLandBoughtUsd(can);
        usdUs = g.getLandBoughtUsd(usa);
        LandParcel toBuy = converted;
        bought = quietlyGet(() -> g.buyLandParcel(toBuy.getId()));
        check("the second offer across the river is bought converting", bought);
        report("...the treasury paid usd x rate to a part in a billion, as it does for an offer with one seller", near(cash - g.getCash(), usd * rate, 1e-9),
                String.format("US$%,.3fk at %.4f", usd, rate));
        check("...and the vault ended where it began", fx.getReservesUsd() == reserves);
        receipt = g.getLastLandReceipt();
        check("...the receipt says it converted and names the sellers: \"" + receipt.substring(Math.max(0, receipt.indexOf("Sold by"))) + "\"",
                receipt.contains("converting") && receipt.contains("Sold by") && receipt.contains(file.nation(can).name()) && receipt.contains(file.nation(usa).name()));
        check("...and the tally took its parts too", near(g.getLandBoughtKm2(can) - kmCan, canPart2.km2(), 1e-12) && near(g.getLandBoughtKm2(usa) - kmUs, usPart2.km2(), 1e-12)
                && near(g.getLandBoughtUsd(can) - usdCan, canPart2.usd(), 1e-12) && near(g.getLandBoughtUsd(usa) - usdUs, usPart2.usd(), 1e-12));

        // an offer that is the host's alone names no seller on its receipt, and is tallied to its nation
        LandParcel home = halifax.office().cheapest();
        double km2Before = halifax.g.getLandBoughtKm2(can), usdBefore = halifax.g.getLandBoughtUsd(can);
        bought = home != null && quietlyGet(() -> halifax.g.buyLandParcel(home.getId()));
        check("a Halifax offer (Canada's, the host's) is bought", bought);
        check("...its receipt names no seller: \"" + halifax.g.getLastLandReceipt() + "\"", !halifax.g.getLastLandReceipt().contains("Sold by"));
        check("...and the tally took its ground and dollars for Canada",
                home != null && near(halifax.g.getLandBoughtKm2(can) - km2Before, home.getKm2(), 1e-12) && near(halifax.g.getLandBoughtUsd(can) - usdBefore, home.getPriceUsd(), 1e-12)
                        && halifax.g.getLandBoughtKm2(0) == 0);

        // a generated world's purchase is the no-seller index's
        LandParcel plain = generated.office().cheapest();
        bought = plain != null && quietlyGet(() -> generated.g.buyLandParcel(plain.getId()));
        check("a generated world's offer is bought", bought);
        check("...its receipt names no seller: \"" + generated.g.getLastLandReceipt() + "\"", !generated.g.getLastLandReceipt().contains("Sold by"));
        check("...and the tally took it at index 0, the purchase that named no seller, and at no nation's",
                plain != null && near(generated.g.getLandBoughtKm2(0), plain.getKm2(), 1e-12) && near(generated.g.getLandBoughtUsd(0), plain.getPriceUsd(), 1e-12)
                        && generated.g.getLandBoughtKm2(can) == 0 && generated.g.getLandBoughtKm2(usa) == 0);
    }

    static LandParcel.Sale part(LandParcel p, int seller) {
        for (LandParcel.Sale s : p.getSellers()) if (s.seller() == seller) return s;
        return new LandParcel.Sale(seller, 0, 0);
    }

    /* =====================================================================
       9. THE SAVE
       ===================================================================== */

    static void theSave(WorldData file, City windsor) throws IOException {
        out.println("\n--- 9. the save: the sellers and the tally survive, a save without the keys is the host's whole, a generated city carries none ---");
        int n = file.nationCount();
        Game g = windsor.g;
        GameFiles.Result saved = quietlyGet(() -> g.saveGame(1, "windsor"));
        check("premise: the Windsor city, after its purchases, saved", saved.ok);
        String text = Files.readString(windsor.files.saveFile(1), StandardCharsets.UTF_8);
        check("the save carries the three keys, landOffersSellers, landBoughtKm2 and landBoughtUsd",
                text.contains("\"landOffersSellers\"") && text.contains("\"landBoughtKm2\"") && text.contains("\"landBoughtUsd\""));
        Game back = loadCopy(windsor.files, text, "land-nations-windsor-back");
        check("loaded into a fresh game in another folder it loads whole", back.getLoadFailure() == null && back.getLandSellers() != null && back.getLandSellers().host() == windsor.sellers().host());
        List<LandParcel> was = windsor.offers(), now = back.getLandManager().getMarket().getListing();
        boolean same = was.size() == now.size() && !was.isEmpty();
        boolean pricesSame = same, sellersSame = same;
        for (int i = 0; same && i < was.size(); i++) {
            same &= was.get(i).same(now.get(i));
            pricesSame &= was.get(i).getPriceUsd() == now.get(i).getPriceUsd();
            sellersSame &= was.get(i).getSellers().equals(now.get(i).getSellers());
        }
        check("...its " + was.size() + " offers are the saved ones, field for field (same()), and so are their prices and their sellers, to the bit", same && pricesSame && sellersSame);
        boolean tally = true;
        for (int i = 0; i <= n; i++) tally &= g.getLandBoughtKm2(i) == back.getLandBoughtKm2(i) && g.getLandBoughtUsd(i) == back.getLandBoughtUsd(i);
        double bought = 0;
        for (int i = 0; i <= n; i++) bought += g.getLandBoughtUsd(i);
        check("...and the tally is the saved one in every nation (US$" + String.format("%,.2f", bought) + " thousand bought so far)", tally && bought > 0);

        // the same city's save with the three keys taken out: a save from before WN5
        JsonObject json = JsonParser.parseString(text).getAsJsonObject();
        json.remove("landOffersSellers");
        json.remove("landBoughtKm2");
        json.remove("landBoughtUsd");
        Game old = loadCopy(windsor.files, json.toString(), "land-nations-windsor-old");
        List<LandParcel> oldOffers = old.getLandManager().getMarket().getListing();
        boolean none = oldOffers.size() == was.size() && oldOffers.stream().allMatch(p -> p.getSellers().isEmpty());
        boolean prices = oldOffers.size() == was.size();
        for (int i = 0; prices && i < was.size(); i++) prices &= oldOffers.get(i).getPriceUsd() == was.get(i).getPriceUsd();
        boolean nothing = true;
        for (int i = 0; i <= n; i++) nothing &= old.getLandBoughtKm2(i) == 0 && old.getLandBoughtUsd(i) == 0;
        check("the same save with the three keys taken out loads: every offer is the host's whole (no seller named), at the price it was listed, and nothing is bought",
                old.getLoadFailure() == null && none && prices && nothing);

        // a generated city's save carries none of them
        City plain = new City("land-nations-plain", Founding.defaults());
        GameFiles.Result plainSaved = quietlyGet(() -> plain.g.saveGame(1, "plain"));
        String plainText = Files.readString(plain.files.saveFile(1), StandardCharsets.UTF_8);
        check("a generated city saved carries none of the three keys", plainSaved.ok && !plainText.contains("landOffersSellers") && !plainText.contains("landBought"));
        Game plainBack = loadCopy(plain.files, plainText, "land-nations-plain-back");
        List<LandParcel> plainWas = plain.offers(), plainNow = plainBack.getLandManager().getMarket().getListing();
        boolean plainSame = plainWas.size() == plainNow.size() && !plainWas.isEmpty();
        for (int i = 0; plainSame && i < plainWas.size(); i++) plainSame &= plainWas.get(i).same(plainNow.get(i));
        boolean plainNothing = true;
        for (int i = 0; i <= n; i++) plainNothing &= plainBack.getLandBoughtKm2(i) == 0 && plainBack.getLandBoughtUsd(i) == 0;
        check("...and loads with no sellers, the offers as saved, and nothing bought",
                plainBack.getLoadFailure() == null && plainBack.getLandSellers() == null && plainSame && plainNothing && plainNow.stream().allMatch(p -> p.getSellers().isEmpty()));
    }

    /** A save's text written to slot 1 of a folder of its own, beside whatever else the city's saves folder holds, and loaded into a fresh game. */
    static Game loadCopy(GameFiles from, String text, String label) throws IOException {
        GameFiles other = GameFiles.scratch(label);
        Files.createDirectories(other.savesDirectory());
        try (Stream<Path> kept = Files.list(from.savesDirectory())) {
            for (Path p : (Iterable<Path>) kept::iterator) {
                if (Files.isRegularFile(p)) Files.copy(p, other.savesDirectory().resolve(p.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        Files.writeString(other.saveFile(1), text, StandardCharsets.UTF_8);
        Game back = new Game(other);
        quietly(() -> back.loadGameSave(1));
        return back;
    }

    /* =====================================================================
       10. HOW OFTEN IT FIRES IN A REAL RUN
       ===================================================================== */

    /** How many years the run plays, a look and some purchases at the start of each. */
    static final int RUN_YEARS = 10;

    static void howOften(WorldData file) {
        out.println("\n--- 10. how often it fires: a Windsor city played " + RUN_YEARS * 12 + " months, buying up to six offers that name a foreign seller and the three cheapest, a year ---");
        // a city of its own: section 8 bought the two offers across the river from the first, and the offers past them are the United States' alone
        City windsor = new City("land-nations-run", onEarth(WINDSOR_LAT, WINDSOR_LON));
        Game g = windsor.g;
        LandSellers sellers = windsor.sellers();
        int host = sellers.host();
        Set<Integer> seen = new HashSet<>();
        int split = 0, foreign = 0, home = 0, tried = 0, bought = 0, named = 0, namedWholly = 0;
        double km2 = 0;
        int startMonth = g.getMonth();
        for (int year = 0; year < RUN_YEARS; year++) {
            List<LandParcel> shelf = new ArrayList<>(windsor.offers());
            for (LandParcel p : shelf) {
                if (!seen.add(p.getId())) continue;
                switch (kind(p, host)) {
                    case SPLIT -> split++;
                    case FOREIGN -> foreign++;
                    default -> home++;
                }
            }
            List<LandParcel> abroad = new ArrayList<>();
            for (LandParcel p : shelf) if (kind(p, host) != Kind.HOME) abroad.add(p);
            abroad.sort(Comparator.comparingDouble(LandParcel::getPriceUsd));
            List<LandParcel> toBuy = new ArrayList<>(abroad.subList(0, Math.min(6, abroad.size())));
            List<LandParcel> cheapest = new ArrayList<>(shelf);
            cheapest.sort(Comparator.comparingDouble(LandParcel::getPriceUsd));
            toBuy.addAll(cheapest.subList(0, Math.min(3, cheapest.size())));
            for (LandParcel p : toBuy) {
                LandParcel still = windsor.office().find(p.getId());
                if (still == null) continue;
                quietly(() -> g.setCashForTest(Founding.WEALTHY_CASH));
                tried++;
                boolean ok = quietlyGet(() -> g.buyLandParcel(p.getId()));
                if (!ok) continue;
                bought++;
                km2 += p.getKm2();
                if (g.getLastLandReceipt().contains("Sold by")) {
                    named++;
                    if (p.getSellers().size() == 1) namedWholly++;
                }
            }
            windsor.months(12);
        }
        out.printf("   months %d to %d: %d offers listed in all: %d split between nations, %d wholly foreign, %d wholly the host's%n", startMonth, g.getMonth(), seen.size(), split, foreign, home);
        out.printf("   %d purchases tried, %d made (%.3f km2): %d named a foreign seller on the receipt (%d of them wholly one nation's)%n", tried, bought, km2, named, namedWholly);
        check("the run played its " + RUN_YEARS * 12 + " months", g.getMonth() >= startMonth + RUN_YEARS * 12);
        check("offers of both kinds were listed: the host's own, and offers sold by another nation (split or wholly foreign)", home > 0 && split + foreign > 0);
        check("...and purchases that named a foreign seller were made", bought > 0 && named > 0);
    }

    enum Kind { HOME, SPLIT, FOREIGN }

    static Kind kind(LandParcel p, int host) {
        List<LandParcel.Sale> sold = p.getSellers();
        if (sold.size() > 1) return Kind.SPLIT;
        if (sold.size() == 1 && sold.get(0).seller() != host) return Kind.FOREIGN;
        return Kind.HOME;
    }

    /* =====================================================================
       11. THE WORDS
       ===================================================================== */

    static void theWords(WorldData file, City windsor, Founded founded, LandParcel detroit) {
        out.println("\n--- 11. the words: \"Sold by United States 62% (a middle dot) Canada 38%\" ---");
        int can = file.numberOf("CAN"), usa = file.numberOf("USA");
        LandSellers sellers = windsor.sellers();
        String canada = file.nation(can).name(), america = file.nation(usa).name();
        int host = sellers.host();
        List<LandParcel> real = new ArrayList<>(founded.offers());
        if (detroit != null) real.add(detroit);
        Pattern percent = Pattern.compile("(\\d+)%");

        int checked = 0, wrong = 0, shown = 0, silent = 0;
        String example = "";
        for (LandParcel p : real) {
            String line = LandSellersView.offerLine(p, sellers);
            List<LandParcel.Sale> sold = p.getSellers();
            boolean hostsOwn = sold.isEmpty() || (sold.size() == 1 && sold.get(0).seller() == host);
            checked++;
            if (hostsOwn) {
                silent++;
                if (!line.isEmpty()) wrong++;
                continue;
            }
            shown++;
            if (example.isEmpty()) example = line;
            boolean ok = line.startsWith("Sold by ");
            int from = 0;
            double km2 = 0;
            for (LandParcel.Sale s : sold) km2 += s.km2();
            if (sold.size() == 1) {
                ok &= line.equals("Sold by " + file.nation(sold.get(0).seller()).name());
            } else {
                Matcher m = percent.matcher(line);
                int total = 0, found = 0;
                while (m.find()) {
                    int pct = Integer.parseInt(m.group(1));
                    total += pct;
                    ok &= found < sold.size() && Math.abs(pct - 100 * sold.get(found).km2() / km2) < 1.0;
                    found++;
                }
                ok &= found == sold.size() && total == 100;
                for (LandParcel.Sale s : sold) {
                    int at = line.indexOf(file.nation(s.seller()).name(), from);
                    ok &= at >= from;
                    from = Math.max(from, at);
                }
            }
            if (!ok) wrong++;
        }
        report("on the Windsor offers at the founding and a block of Detroit, the line is empty for the host's own and names each seller, in order, with whole percents that add up to 100 within a point of their share",
                wrong == 0 && silent > 0 && shown >= 3, String.format("%d offers: %d said nothing, %d named sellers (e.g. \"%s\")", checked, silent, shown, example));

        String dot = " · ";
        LandParcel two = new LandParcel(900, 1000, 5, 0, 0);
        two.setSellers(List.of(new LandParcel.Sale(usa, 0.62, 3.1), new LandParcel.Sale(can, 0.38, 1.9)));
        check("an offer sold 0.62 km2 by the United States and 0.38 by Canada reads \"Sold by " + america + " 62%\", a middle dot, \"" + canada + " 38%\"",
                LandSellersView.offerLine(two, sellers).equals("Sold by " + america + " 62%" + dot + canada + " 38%"));

        LandParcel three = new LandParcel(901, 1000, 5, 0, 0);
        three.setSellers(List.of(new LandParcel.Sale(usa, 0.333, 1), new LandParcel.Sale(can, 0.333, 1), new LandParcel.Sale(file.numberOf("MEX"), 0.334, 1)));
        String mexico = file.nation(file.numberOf("MEX")).name();
        String threeLine = LandSellersView.offerLine(three, sellers);
        int[] pcts = LandSellersView.percents(three.getSellers());
        check("a three-way split whose shares round to 99 (33.3, 33.3, 33.4) still adds to 100: the largest remainder takes the point, \"" + threeLine.replace(dot, " / ") + "\"",
                threeLine.equals("Sold by " + america + " 33%" + dot + canada + " 33%" + dot + mexico + " 34%") && Arrays.stream(pcts).sum() == 100);
        LandParcel tie = new LandParcel(902, 1000, 5, 0, 0);
        tie.setSellers(List.of(new LandParcel.Sale(usa, 1, 1), new LandParcel.Sale(can, 1, 1), new LandParcel.Sale(file.numberOf("MEX"), 1, 1)));
        int[] tied = LandSellersView.percents(tie.getSellers());
        check("...three equal shares are 34, 33 and 33 (a tie goes to the seller that comes first)", Arrays.equals(tied, new int[] { 34, 33, 33 }));
        LandParcel dollars = new LandParcel(903, 1000, 5, 0, 0);
        dollars.setSellers(List.of(new LandParcel.Sale(usa, 0, 3), new LandParcel.Sale(can, 0, 1)));
        check("...an offer whose parts have no area is shared by their dollars (75 and 25), and one with neither equally",
                Arrays.equals(LandSellersView.percents(dollars.getSellers()), new int[] { 75, 25 })
                        && Arrays.equals(LandSellersView.percents(List.of(new LandParcel.Sale(usa, 0, 0), new LandParcel.Sale(can, 0, 0))), new int[] { 50, 50 })
                        && LandSellersView.percents(List.of()).length == 0 && LandSellersView.percents(null).length == 0);

        LandParcel own = new LandParcel(904, 1000, 5, 0, 0);
        own.setSellers(List.of(new LandParcel.Sale(host, 0.1, 1)));
        LandParcel abroad = new LandParcel(905, 1000, 5, 0, 0);
        abroad.setSellers(List.of(new LandParcel.Sale(usa, 0.1, 1)));
        LandParcel unnamed = new LandParcel(906, 1000, 5, 0, 0);
        check("an offer wholly the host's says nothing, an offer wholly the United States' reads \"Sold by " + america + "\", an offer with no seller named says nothing",
                LandSellersView.offerLine(own, sellers).isEmpty() && LandSellersView.offerLine(abroad, sellers).equals("Sold by " + america) && LandSellersView.offerLine(unnamed, sellers).isEmpty());
        check("...a split that includes the host is named in full, and no sellers (a generated world's) or no offer say nothing",
                LandSellersView.offerLine(two, sellers).contains(canada) && LandSellersView.offerLine(two, null).isEmpty() && LandSellersView.offerLine(null, sellers).isEmpty());

        // WN5b: the receipt rounded each share on its own and the card by largest remainder; both read LandSellersView.percents() now.
        int mex = file.numberOf("MEX"), fra = file.numberOf("FRA");
        java.util.function.Function<LandParcel.Sale, String> money = s -> "US$1.0M";
        List<List<LandParcel.Sale>> splits = List.of(
                List.of(new LandParcel.Sale(usa, 1, 1), new LandParcel.Sale(mex, 1, 1), new LandParcel.Sale(fra, 1, 1)),
                List.of(new LandParcel.Sale(usa, 50.5, 1), new LandParcel.Sale(mex, 49.5, 1)));
        String[] splitNames = { "three equal thirds", "50.5 and 49.5 km2" };
        boolean premise = true, same = true;
        StringBuilder sums = new StringBuilder(), lines = new StringBuilder();
        for (int i = 0; i < splits.size(); i++) {
            LandParcel p = new LandParcel(910 + i, 1000, 5, 0, 0);
            p.setSellers(splits.get(i));
            List<LandParcel.Sale> sold = p.getSellers();
            double km2 = 0;
            for (LandParcel.Sale s : sold) km2 += s.km2();
            long each = 0;
            for (LandParcel.Sale s : sold) each += Math.round(100 * (s.km2() / km2));
            premise &= each != 100;
            sums.append(i > 0 ? ", " : "").append(splitNames[i]).append(" ").append(each);
            int[] want = LandSellersView.percents(sold);
            String card = LandSellersView.offerLine(p, sellers), receipt = LandSellersView.receiptLine(sold, sellers, money);
            int[] onCard = percentsIn(percent, card), onReceipt = percentsIn(percent, receipt);
            same &= want.length == sold.size() && Arrays.equals(onCard, want) && Arrays.equals(onReceipt, want) && Arrays.stream(want).sum() == 100;
            lines.append(i > 0 ? " | " : "").append(Arrays.toString(onCard)).append(" / ").append(Arrays.toString(onReceipt));
        }
        report("premise: rounding each share on its own does not add to 100 for either split (the receipt's old way): " + sums, premise, "sums of Math.round(100 * share) as the receipt had them");
        report("a split whose shares round differently reads the same on the card and the receipt: the percents are percents()' own, adding to 100 (card / receipt)",
                same, lines.toString());
    }

    /** The whole percents a line shows, in order: the digits before each "%". */
    private static int[] percentsIn(Pattern percent, String line) {
        Matcher m = percent.matcher(line);
        List<Integer> found = new ArrayList<>();
        while (m.find()) found.add(Integer.parseInt(m.group(1)));
        return found.stream().mapToInt(Integer::intValue).toArray();
    }

    /* =====================================================================
       12. THE HOVER'S QUOTE AWAY FROM THE SCREEN'S THREAD
       ===================================================================== */

    /** Where the hover is held: a spot on the Detroit River at Windsor, and one off the coast at the Strait of Gibraltar. */
    static final double RIVER_LAT = 42.32, RIVER_LON = -83.04, COAST_LAT = 35.95, COAST_LON = -5.45;

    /** The block of `level` holding a point, as the map's hover cuts it (LandMap.blockAt(), blockRect()): {x0, y0, x1, y1}. */
    static long[] blockAround(World w, double lat, double lon, int level) {
        long[] p = plotOf(w, lat, lon);
        return LandMap.blockRect(level, LandMap.blockAt(level, p[0], p[1]));
    }

    static LandParcel quoteOf(LandMarket office, long[] r) { return office.quote(r[0], r[1], r[2], r[3]); }

    static LandParcel quoteOf(LandMarket.Quoter quoter, long[] r) { return quoter.quote(r[0], r[1], r[2], r[3]); }

    /** Two figures the same to the bit. */
    static boolean bits(double a, double b) { return Double.compare(a, b) == 0; }

    /** Whether two quotes of one block are the same to the bit: the price, the area of every kind, and the sellers one by one (seller, area, dollars). `parts[0]` counts the sellers compared. */
    static boolean sameQuote(LandParcel a, LandParcel b, int[] parts) {
        if (a == null || b == null) return a == b;
        boolean same = bits(a.getPriceUsd(), b.getPriceUsd());
        for (int k = 0; k < CityLand.AREAS; k++) same &= bits(a.getKm2(k), b.getKm2(k));
        List<LandParcel.Sale> sa = a.getSellers(), sb = b.getSellers();
        same &= sa.size() == sb.size();
        for (int i = 0; i < Math.min(sa.size(), sb.size()); i++) {
            same &= sa.get(i).seller() == sb.get(i).seller() && bits(sa.get(i).km2(), sb.get(i).km2()) && bits(sa.get(i).usd(), sb.get(i).usd());
            parts[0]++;
        }
        return same;
    }

    /** What one cold quote of a block cost: its level, its side in km, its milliseconds, the plots the sellers read for it, and the sellers it named. */
    record Cost(int level, double km, double ms, long plots, int sellers) { }

    /** A cost as a line of the table: level L (km across): N ms, P plots read, S sellers. */
    static String costLine(Cost c) {
        return String.format("   level %d (%.1f km across): %.1f ms, %,d plots read, %d sellers", c.level(), c.km(), c.ms(), c.plots(), c.sellers());
    }

    /** The block of `level` round a point quoted once by the city's office, timed, with the plots LandSellers read beside it. */
    static Cost costOf(City c, double lat, double lon, int level) {
        long[] r = blockAround(c.g.getWorld(), lat, lon, level);
        LandSellers s = c.sellers();
        long before = s.plotsRead();
        long t0 = System.nanoTime();
        LandParcel q = quoteOf(c.office(), r);
        double ms = (System.nanoTime() - t0) / 1e6;
        return new Cost(level, (1L << level) * World.PLOT_M / 1000, ms, s.plotsRead() - before, q == null ? 0 : q.getSellers().size());
    }

    /** The quotes of blocks asked of a quoter on a thread of its own, in the order given; the thread is stopped after. */
    static List<LandParcel> quotedElsewhere(LandMarket.Quoter quoter, List<long[]> blocks) throws Exception {
        ExecutorService pool = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "land-nations-quoter");
            t.setDaemon(true);
            return t;
        });
        try {
            return pool.submit(() -> {
                List<LandParcel> got = new ArrayList<>();
                for (long[] b : blocks) got.add(quoteOf(quoter, b));
                return got;
            }).get();
        } finally {
            pool.shutdownNow();
        }
    }

    static void theHover(WorldData file, City windsor, City strait) throws Exception {
        out.println("\n--- 12. the hover's quote away from the screen's thread: a quoter made here and used on another thread gives the office's quote to the bit ---");
        World ww = windsor.g.getWorld(), sw = strait.g.getWorld();
        LandMarket wo = windsor.office(), so = strait.office();
        check("premise: both offices have sellers (a quote then splits the block's ground by nation, which is what costs)", wo.getSellers() != null && so.getSellers() != null);
        if (wo.getSellers() == null || so.getSellers() == null) return;
        LandParcel river8 = quoteOf(wo, blockAround(ww, RIVER_LAT, RIVER_LON, 8)), coast9 = quoteOf(so, blockAround(sw, COAST_LAT, COAST_LON, 9));
        report("premise: the block of 256 plots a side across the Detroit River at Windsor is quoted with two sellers or more", river8 != null && river8.getSellers().size() >= 2,
                river8 == null ? "no quote" : river8.getSellers().size() + " sellers");
        report("premise: the block of 512 plots a side at the Strait of Gibraltar is quoted with two sellers or more", coast9 != null && coast9.getSellers().size() >= 2,
                coast9 == null ? "no quote" : coast9.getSellers().size() + " sellers");

        // the cost, measured first so that each is the block's first quote of the run: what moved the hover's quote off the screen's thread
        double frame = LandMarket.QUOTER_MS;
        List<Cost> costs = new ArrayList<>();
        out.println("   the cost of one cold quote by the block's level, across the Detroit River at Windsor:");
        for (int level = 4; level <= 12; level++) costs.add(costOf(windsor, RIVER_LAT, RIVER_LON, level));
        for (Cost c : costs) out.println(costLine(c));
        out.println("   ...and off the coast at the Strait of Gibraltar:");
        List<Cost> coast = new ArrayList<>();
        for (int level = 4; level <= 11; level++) coast.add(costOf(strait, COAST_LAT, COAST_LON, level));
        for (Cost c : coast) out.println(costLine(c));
        costs.addAll(coast);
        int slow = 0;
        for (Cost c : costs) if (c.ms() > frame) slow++;
        out.printf("   %d of the %d quotes took more than QUOTER_MS (%.0f ms): that many hovers would have held a frame on the screen's thread%n", slow, costs.size(), frame);
        check("the cost was measured at every level asked (9 at Windsor, 8 at the Strait), each quote a parcel with a seller", costs.size() == 17 && costs.stream().allMatch(c -> c.sellers() >= 1));

        // a quoter made here and quoted on another thread
        List<long[]> riverBlocks = new ArrayList<>(), coastBlocks = new ArrayList<>();
        for (int level = 4; level <= 10; level++) riverBlocks.add(blockAround(ww, RIVER_LAT, RIVER_LON, level));
        for (int level = 4; level <= 9; level++) coastBlocks.add(blockAround(sw, COAST_LAT, COAST_LON, level));
        LandMarket.Quoter wq = wo.quoter(), sq = so.quoter();
        check("a quoter is made of each office (neither is bare, and the land copies)", wq != null && sq != null);
        if (wq == null || sq == null) return;
        List<LandParcel> wGot = quotedElsewhere(wq, riverBlocks), sGot = quotedElsewhere(sq, coastBlocks);
        int[] parts = new int[1];
        int blocks = 0, differ = 0;
        for (int i = 0; i < riverBlocks.size(); i++, blocks++) if (!sameQuote(quoteOf(wo, riverBlocks.get(i)), wGot.get(i), parts)) differ++;
        for (int i = 0; i < coastBlocks.size(); i++, blocks++) if (!sameQuote(quoteOf(so, coastBlocks.get(i)), sGot.get(i), parts)) differ++;
        report("a quoter made on this thread and quoted on another gives the office's quote to the bit: the price, every kind's area and every seller (seller, area, dollars)",
                differ == 0 && blocks == 13 && parts[0] > blocks, blocks + " blocks (Windsor levels 4 to 10, the Strait 4 to 9), " + parts[0] + " sellers compared");

        // the quoter is the office as it stood: a block of 256 plots a side round some free ground of a standing offer, quoted before and after the offer is bought
        LandMarket.Quoter old = wo.quoter();
        long[] rect = null;
        LandParcel offer = null;
        int level = wo.getLevel();
        CityLand land = windsor.g.getCityLand();
        for (LandParcel p : windsor.offers()) {
            for (long by = p.getY0() >> level; by <= (p.getY1() - 1) >> level && offer == null; by++) {
                for (long bx = p.getX0() >> level; bx <= (p.getX1() - 1) >> level && offer == null; bx++) {
                    long[] b = LandMap.blockRect(level, LandMap.blockKey(bx, by));
                    long x0 = Math.max(b[0], p.getX0()), y0 = Math.max(b[1], p.getY0()), x1 = Math.min(b[2], p.getX1()), y1 = Math.min(b[3], p.getY1());
                    if (x0 < x1 && y0 < y1 && land.grid().unowned(x0, y0, x1, y1) > 0) {
                        offer = p;
                        rect = LandMap.blockRect(8, LandMap.blockAt(8, x0, y0));
                    }
                }
            }
            if (offer != null) break;
        }
        check("premise: a standing offer of the Windsor city has free ground, and a block of 256 plots a side round it", offer != null && old != null);
        if (offer == null || old == null) return;
        LandParcel officeBefore = quoteOf(wo, rect), oldBefore = quoteOf(old, rect);
        check("premise: the block is quoted, with ground, and the old quoter's quote of it is the office's", officeBefore != null && officeBefore.getKm2() > 0 && sameQuote(officeBefore, oldBefore, parts));
        LandParcel toBuy = offer;
        quietly(() -> {
            windsor.g.setCashForTest(Founding.WEALTHY_CASH);
            windsor.g.setLandPaidFromVault(false);
        });
        boolean bought = quietlyGet(() -> windsor.g.buyLandParcel(toBuy.getId()));
        LandParcel officeAfter = quoteOf(wo, rect), oldAfter = quoteOf(old, rect), freshAfter = quoteOf(wo.quoter(), rect);
        check("premise: the offer was bought, and the ground of it in the block is the city's: the office quotes the block with less ground than before",
                bought && officeAfter != null && officeAfter.getKm2() < officeBefore.getKm2());
        check("the quoter made before the purchase quotes the block as the office did before it, to the bit: a purchase after it is not seen", sameQuote(oldBefore, oldAfter, parts));
        report("...and a quoter made after it quotes the office's quote now to the bit, with less ground", sameQuote(officeAfter, freshAfter, parts) && freshAfter.getKm2() < oldAfter.getKm2(),
                String.format("%.4f km2 against %.4f", freshAfter == null ? Double.NaN : freshAfter.getKm2(), oldAfter == null ? Double.NaN : oldAfter.getKm2()));
        check("...and a quoter keeps the month it was made in", old.month() == wo.quoter().month());

        // the screen's thread's part: the land copied
        boolean made = true;
        double worstMs = 0;
        for (int i = 0; i < 20; i++) {
            long t0 = System.nanoTime();
            LandMarket.Quoter q = wo.quoter();
            worstMs = Math.max(worstMs, (System.nanoTime() - t0) / 1e6);
            made &= q != null;
        }
        String frameLabel = String.format("making a quoter (the land copied) takes no more than QUOTER_MS (%.0f ms) on the screen's thread: worst %.2f ms of 20", LandMarket.QUOTER_MS, worstMs);
        boolean framed = made && Timing.within(worstMs, LandMarket.QUOTER_MS);
        String verdict = Timing.verdict(framed, frameLabel);
        if (!framed) fails++;
        out.printf("%-118s %s%n", frameLabel, verdict);

        // the move, held in the source
        Path view = Path.of("src", "main", "java", "ham", "citybuildersim", "ui", "MapView.java");
        if (!Files.isRegularFile(view)) {
            out.println("   " + view + " is not beside the harness (run it from the repository root): the map's source was not read");
        } else {
            String src = Files.readString(view, StandardCharsets.UTF_8);
            check("the map's hover quotes through a quoter on its own thread, never the office's quote() on the screen's thread",
                    !src.contains("market.quote(") && src.contains(".quoter()") && src.contains("QUOTES.submit("));
        }
    }
}

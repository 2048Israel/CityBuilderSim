package ham.citybuildersim;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ham.citybuildersim.sectors.Retail;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The household's basket by year (R0e slice 1): the year's basket in a city founded the 1900 way, today's in every other. Not part of the game.
 *
 * WHAT THIS HAS TO PROVE (R0e, 2026-11-07; docs/studies/research-1900.md 5.4 item 5, "1900's has more raw staples, lamp oil and coal, and no
 * car", and section 4, "Home butter and bread: data, not a mechanism"). A 1900 household baked its bread from flour, lit its rooms with a lamp
 * and heated them with a coal stove, and did not own a car; the basket reaches today's at the last knot, and only a city founded the 1900 way
 * (Founding.isUnfitted()) reads any of it. The figures are the data's (consumption.json's years, sourced in R0e's notes), so none is written here: every
 * one is read from the loaded model (years(), yearAt(), item(), homeMadeFrom()) or is a constant of the model (HomeFuel's two). What is checkable:
 *
 *   1. THE DATA: the packaged file loads years, in ascending order, every share and cap in [0, 1], every bought key and every homeMadeFrom a
 *      good of the file, bread and the bakery's goods made from grain, and the last knot today's (so nothing jumps at its January)
 *   2. THE INTERPOLATION, on a synthetic file: the first knot before its January, a knot's own figures at its January, the mean half-way, null
 *      from the last knot's January on, a good a knot does not name bought whole there; a malformed block leaves no years and the goods loaded
 *   3. THE HOME-MADE BASKET, on the packaged model: a calorie-bound household holds today's calories in more grain and less bread, a good with
 *      no home-made source done without; a money-bound one pays the same and holds more calories; with no year it is today's basket, to the bit
 *   4. A DEFAULT CITY READS NONE OF IT: no year in any month, the homes' shares 0, no lamp oil or coal sold, its shops' basket today's
 *   5. A 1900 CITY'S SHOPS STOCK THE YEAR'S BASKET: less bread and more grain than the same city's today's basket, and than a default twin's
 *   6. LAMP OIL AND COAL BY YEAR: the homes' shares are the year's each month, the litres and tonnes sold the homes x share x the model's
 *      per-home figure, and the households charged the bill the month after
 *   7. NO CAR IN 1900: where a default city motorises, the 1900 city's fleet stays under the year's cap, and its ownership ceiling is at most it
 *   8. THE HELD SALE CROSSES A SAVE: a 1900 city saved in a month that struck lamp oil and coal loads the same sale and pays the same bill
 *   9. TODAY'S FROM THE LAST KNOT: from the last knot's January a 1900 city has no year, no share, no cap and today's basket
 *
 * Every fixture CAUSES its condition: the cities are founded through newGame(Founding.withUnfitted(true)) as the player's door founds them,
 * a default twin on the same village beside each, the car town is CarCheck's (900 houses, 60 roads) with a control whose cap is lifted by
 * editing the packaged file's years, and the synthetic files are written here. Figures are asserted against the model's own constants and
 * methods; a harness city lives in GameFiles.scratch(), never in a slot of the player's.
 */
public class BasketYearCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void check(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-100s %s%s%n", label, ok ? "OK" : "FAIL", ok || detail == null ? "" : "  " + detail);
    }

    static void check(String label, boolean ok) { check(label, ok, null); }

    /** A figure against the harness's own, to a relative tolerance. */
    static void near(String label, double actual, double expected, double rel) {
        boolean ok = Math.abs(actual - expected) <= rel * Math.max(Math.abs(expected), 1e-12);
        check(label, ok, String.format("%.12g != %.12g", actual, expected));
    }

    /** A figure against the model's, to the bit. */
    static void same(String label, double actual, double expected) {
        check(label, Double.doubleToLongBits(actual) == Double.doubleToLongBits(expected), String.format("%.17g != %.17g", actual, expected));
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    /** What a piece of code prints, as lines (the loader's one line in the log is part of what it promises). */
    static List<String> printed(Runnable r) {
        PrintStream real = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bytes, true, StandardCharsets.UTF_8));
        try { r.run(); } finally { System.setOut(real); }
        List<String> lines = new ArrayList<>();
        for (String line : bytes.toString(StandardCharsets.UTF_8).split("\\R")) if (!line.isBlank()) lines.add(line);
        return lines;
    }

    /** One press of the clock, as the player's button makes it, quietly. */
    static void step(Game g, int months) {
        quietly(() -> { for (int i = 0; i < months; i++) g.toggleNextMonth(); });
    }

    /** A village founded as given: roads, homes, shops, farms, bakeries, power and water (FittingCheck.village()). */
    static Game village(String label, Founding founding) {
        return FittingCheck.village(label, founding, false, false);
    }

    static final Founding DEFAULT = Founding.defaults();
    static final Founding THE_1900_WAY = Founding.defaults().withUnfitted(true);

    /** The months a village is played before anything is asked of its shops and homes (Forms1900Check.SETTLE). */
    static final int SETTLE = Forms1900Check.SETTLE;

    public static void main(String[] args) throws Exception {
        out = System.out;
        quiet = new PrintStream(OutputStream.nullOutputStream());
        if (FittingCheck.quiet == null) FittingCheck.quiet = quiet;
        if (EquipmentCheck.quiet == null) EquipmentCheck.quiet = quiet;

        sectionOne();
        sectionTwo();
        sectionThree();
        sectionFour();
        sectionFive();
        sectionSix();
        sectionSeven();
        sectionEight();
        sectionNine();

        out.println(fails == 0 ? "\nAll BasketYearCheck assertions passed." : "\n" + fails + " FAILED");
        System.exit(fails);
    }

    /* ============================ the packaged file ============================ */

    /** The packaged consumption.json as text: the file the game loads when no editable copy lies beside it. */
    static String packagedText() {
        try (InputStream in = Consumption.class.getResourceAsStream("/" + Consumption.FILE_NAME)) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("no packaged " + Consumption.FILE_NAME, e);
        }
    }

    /** The packaged file loaded into a model of its own. */
    static Consumption packaged() {
        Consumption c = new Consumption();
        quietly(() -> c.loadFrom(new StringReader(packagedText())));
        return c;
    }

    /** A model from a text; what the loader says is the caller's to catch (printed()) or to hear. */
    static Consumption model(String text) {
        Consumption c = new Consumption();
        c.loadFrom(new StringReader(text));
        return c;
    }

    /** The packaged file with every knot's cap on the cars lifted to 1 (no cap), as text: the control of section 7. */
    static String packagedWithoutTheCap() {
        JsonObject root = JsonParser.parseString(packagedText()).getAsJsonObject();
        for (JsonElement e : root.getAsJsonArray("years")) e.getAsJsonObject().addProperty("carsPerHousehold", 1.0);
        return root.toString();
    }

    /** The packaged file with no years block at all, as text: a city founded the 1900 way on it behaves as today (section 5's twin). */
    static String packagedWithoutYears() {
        JsonObject root = JsonParser.parseString(packagedText()).getAsJsonObject();
        root.remove("years");
        return root.toString();
    }

    /** The packaged file with the years cut to its first knot and the last, the last one year on, as text: the year runs out after twelve months (section 9). */
    static String packagedWithTheYearShort() {
        JsonObject root = JsonParser.parseString(packagedText()).getAsJsonObject();
        JsonArray years = root.getAsJsonArray("years");
        JsonObject first = years.get(0).getAsJsonObject().deepCopy();
        JsonObject last = years.get(years.size() - 1).getAsJsonObject().deepCopy();
        last.addProperty("year", first.get("year").getAsInt() + 1);
        JsonArray cut = new JsonArray();
        cut.add(first);
        cut.add(last);
        root.add("years", cut);
        return root.toString();
    }

    /* ============================ 1. the data ============================ */

    static void sectionOne() {
        out.println("\n--- 1. the data: years in order, shares in [0, 1], bought keys goods, bread and the bakery's goods from grain, the last knot today's ---");
        Consumption c = packaged();
        List<Consumption.Year> years = c.years();
        check("the packaged file loads goods and years: at least two knots, the first from the 1900s on", c.isLoaded() && years.size() >= 2
                && years.get(0).year() >= CityCalendar.EPOCH_YEAR);
        boolean ascending = true, shares = true, goods = true;
        for (int i = 0; i < years.size(); i++) {
            Consumption.Year y = years.get(i);
            if (i > 0) ascending &= y.year() > years.get(i - 1).year();
            shares &= inUnit(y.lampShare()) && inUnit(y.coalShare()) && inUnit(y.carsPerHousehold());
            for (Map.Entry<String, Double> e : y.bought().entrySet()) {
                shares &= inUnit(e.getValue());
                goods &= c.item(e.getKey()) != null;
            }
        }
        check("the years are strictly ascending", ascending);
        check("every lamp share, coal share, cap on the cars and bought share is a number in [0, 1]", shares);
        check("every bought key of the loaded years is a good in the file", goods);

        // The loader ignores a stranger with a line in the log, so the file itself is read as well: nothing may have been ignored.
        JsonObject root = JsonParser.parseString(packagedText()).getAsJsonObject();
        List<String> keys = new ArrayList<>();
        for (JsonElement g : root.getAsJsonArray("goods")) keys.add(g.getAsJsonObject().get("key").getAsString());
        boolean boughtNamed = true, madeNamed = true;
        for (JsonElement y : root.getAsJsonArray("years")) {
            JsonObject b = y.getAsJsonObject().getAsJsonObject("bought");
            if (b != null) for (String key : b.keySet()) boughtNamed &= keys.contains(key);
        }
        for (JsonElement g : root.getAsJsonArray("goods")) {
            JsonObject o = g.getAsJsonObject();
            if (o.has("homeMadeFrom")) {
                String made = o.get("homeMadeFrom").getAsString();
                madeNamed &= keys.contains(made) && !made.equals(o.get("key").getAsString());
            }
        }
        check("...and read off the file itself, no bought key names a stranger to the goods", boughtNamed);
        check("...and no homeMadeFrom names a stranger, or the good itself", madeNamed);
        check("bread is made at home from grain", "GRAINS".equals(c.homeMadeFrom("BREAD")) && c.item("GRAINS") != null && c.item("BREAD") != null);
        check("...and so is the bakery's", "GRAINS".equals(c.homeMadeFrom("BAKERY")) && c.item("BAKERY") != null);
        check("a good with no homeMadeFrom, and a key that is no good, have none", c.homeMadeFrom("READY_MEALS") == null && c.homeMadeFrom("NOT_A_GOOD") == null && c.homeMadeFrom(null) == null);

        Consumption.Year last = years.get(years.size() - 1);
        boolean boughtWhole = true;
        for (Consumption.Item it : c.items()) boughtWhole &= last.bought().getOrDefault(it.key(), 1.0) == 1.0;
        check("the last knot is today's: no lamp, no coal stove, no cap on the cars, every good bought whole",
                last.lampShare() == 0 && last.coalShare() == 0 && last.carsPerHousehold() == 1 && boughtWhole);
        int january = ArrivalMix.januaryOf(last.year());
        check("...and from its January the model is today's: no year at all, in that month and after", c.yearAt(january) == null && c.yearAt(january + 1) == null
                && c.yearAt(january + 10_000) == null);

        // Nothing jumps at that January: the month before it is one month's step from the last knot, no more.
        Consumption.Year before = years.get(years.size() - 2);
        int span = january - ArrivalMix.januaryOf(before.year());
        Consumption.YearPoint just = c.yearAt(january - 1);
        boolean smooth = just != null
                && Math.abs(just.lampShare() - last.lampShare()) <= Math.abs(before.lampShare() - last.lampShare()) / span * (1 + 1e-9) + 1e-12
                && Math.abs(just.coalShare() - last.coalShare()) <= Math.abs(before.coalShare() - last.coalShare()) / span * (1 + 1e-9) + 1e-12
                && Math.abs(just.carCeiling() - last.carsPerHousehold()) <= Math.abs(before.carsPerHousehold() - last.carsPerHousehold()) / span * (1 + 1e-9) + 1e-12;
        for (Consumption.Item it : c.items()) {
            if (just == null) break;
            smooth &= Math.abs(just.bought(it.key()) - 1.0) <= Math.abs(before.bought().getOrDefault(it.key(), 1.0) - 1.0) / span * (1 + 1e-9) + 1e-12;
        }
        check("...so the month before the last knot's January is a month's step from it and nothing jumps there", smooth);
    }

    static boolean inUnit(double v) { return Double.isFinite(v) && v >= 0 && v <= 1; }

    /* ============================ 2. the interpolation ============================ */

    // The synthetic file's three knots, a decade apart (the harness's own figures, not the packaged file's): lamp share, coal share, cars.
    static final int Y1 = 1910, Y2 = 1920, Y3 = 1930;
    static final double[] LAMP = { .8, .4, 0 }, COAL = { .6, .2, 0 }, CARS = { 0, .5, 1 };
    // LOAF is named by the first two knots, FLOUR by the second alone; a knot that does not name a good buys it whole.
    static final double LOAF_1 = .2, LOAF_2 = .6, FLOUR_2 = .4;

    static String good(String key, String made, double kcal, double price) {
        return String.format(Locale.ROOT, "{\"key\":\"%s\",\"label\":\"%s\",\"unit\":\"kg\",\"category\":\"FOOD_STAPLE\",\"worldImportPrice\":%s,\"worldExportPrice\":%s,"
                + "\"kcalPerUnit\":%s,\"quality\":0.5,\"elasticity\":-0.1,\"convenience\":0.5,\"referenceKg\":3.0%s}",
                key, key, price, price / 2, kcal, made == null ? "" : ",\"homeMadeFrom\":\"" + made + "\"");
    }

    static String knot(int year, double lamp, double coal, double cars, String bought) {
        return String.format(Locale.ROOT, "{\"year\":%d,\"lampShare\":%s,\"coalShare\":%s,\"carsPerHousehold\":%s,\"bought\":{%s}}", year, lamp, coal, cars, bought);
    }

    /** A file of two goods, FLOUR and LOAF (made from flour, unless it names another), and the years block as given (null: none). */
    static String file(String years, String loafMadeFrom) {
        return "{\"referenceIncomeMultiple\":8.0,\"targetCalorieShares\":{\"FOOD_STAPLE\":1.0},"
                + (years == null ? "" : "\"years\":" + years + ",")
                + "\"goods\":[" + good("FLOUR", null, 3000, .001) + "," + good("LOAF", loafMadeFrom, 2500, .002) + "]}";
    }

    static String synthetic() {
        return file("[" + knot(Y1, LAMP[0], COAL[0], CARS[0], "\"LOAF\":" + LOAF_1) + ","
                + knot(Y2, LAMP[1], COAL[1], CARS[1], "\"LOAF\":" + LOAF_2 + ",\"FLOUR\":" + FLOUR_2) + ","
                + knot(Y3, LAMP[2], COAL[2], CARS[2], "") + "]", "FLOUR");
    }

    static void sectionTwo() {
        out.println("\n--- 2. the interpolation, on a synthetic file: the first knot before its January, the mean half-way, null from the last knot's January ---");
        Consumption c = model(synthetic());
        check("fixture: the synthetic file loads two goods and three knots", c.items().size() == 2 && c.years().size() == 3);
        int j1 = ArrivalMix.januaryOf(Y1), j2 = ArrivalMix.januaryOf(Y2), j3 = ArrivalMix.januaryOf(Y3);

        for (int month : new int[] { 1, j1 - 1 }) {
            Consumption.YearPoint p = c.yearAt(month);
            check("before the first knot's January (month " + month + ") the first knot's figures: lamp, coal, cap, the loaf bought in part, the flour whole",
                    p != null && p.lampShare() == LAMP[0] && p.coalShare() == COAL[0] && p.carCeiling() == CARS[0] && p.bought("LOAF") == LOAF_1 && p.bought("FLOUR") == 1);
        }
        Consumption.YearPoint at1 = c.yearAt(j1), at2 = c.yearAt(j2);
        check("at a knot's January that knot's figures, to the bit: the first", at1 != null && at1.lampShare() == LAMP[0] && at1.coalShare() == COAL[0]
                && at1.carCeiling() == CARS[0] && at1.bought("LOAF") == LOAF_1 && at1.bought("FLOUR") == 1);
        check("...and the second, a good the first knot does not name read as bought whole there",
                at2 != null && at2.lampShare() == LAMP[1] && at2.coalShare() == COAL[1] && at2.carCeiling() == CARS[1]
                        && at2.bought("LOAF") == LOAF_2 && at2.bought("FLOUR") == FLOUR_2);

        Consumption.YearPoint half12 = c.yearAt(j1 + (j2 - j1) / 2);
        near("half-way between the first two Januaries the lamp share is the mean", half12.lampShare(), (LAMP[0] + LAMP[1]) / 2, 1e-12);
        near("...the coal share", half12.coalShare(), (COAL[0] + COAL[1]) / 2, 1e-12);
        near("...the cap on the cars", half12.carCeiling(), (CARS[0] + CARS[1]) / 2, 1e-12);
        near("...the loaf bought", half12.bought("LOAF"), (LOAF_1 + LOAF_2) / 2, 1e-12);
        near("...and the flour, which the first knot does not name, the mean of 1 and the second's", half12.bought("FLOUR"), (1 + FLOUR_2) / 2, 1e-12);
        Consumption.YearPoint half23 = c.yearAt(j2 + (j3 - j2) / 2);
        near("half-way between the last two Januaries the lamp share is the mean", half23.lampShare(), (LAMP[1] + LAMP[2]) / 2, 1e-12);
        near("...the cap on the cars", half23.carCeiling(), (CARS[1] + CARS[2]) / 2, 1e-12);
        near("...the loaf, which the last knot does not name: the mean of the second's and 1", half23.bought("LOAF"), (LOAF_2 + 1) / 2, 1e-12);
        Consumption.YearPoint quarter = c.yearAt(j1 + (j2 - j1) / 4);
        near("a quarter of the way it is the quarter, linear by month", quarter.lampShare(), LAMP[0] + (LAMP[1] - LAMP[0]) / 4, 1e-12);
        check("a good the point does not name is bought whole", half12.bought("NOT_A_GOOD") == 1);
        boolean readOnly;
        try {
            half12.bought().put("LOAF", 0.0);
            readOnly = false;
        } catch (UnsupportedOperationException e) {
            readOnly = true;
        }
        check("...and the point's map cannot be written to", readOnly);
        check("from the last knot's January the model is today's: no year, in that month and after", c.yearAt(j3) == null && c.yearAt(j3 + 1) == null && c.yearAt(j3 + 5000) == null);
        check("...and the month before it is still a year", c.yearAt(j3 - 1) != null);

        // Malformed blocks: one line in the log, no years, the goods loaded.
        String[][] bad = {
                { "years not in order", "[" + knot(Y2, .4, .2, .5, "") + "," + knot(Y1, .8, .6, 0, "") + "]" },
                { "a year twice", "[" + knot(Y1, .8, .6, 0, "") + "," + knot(Y1, .4, .2, .5, "") + "]" },
                { "a share past 1", "[" + knot(Y1, 1.5, .6, 0, "") + "," + knot(Y2, .4, .2, .5, "") + "]" },
                { "a share below 0", "[" + knot(Y1, .8, -.1, 0, "") + "," + knot(Y2, .4, .2, .5, "") + "]" },
                { "a bought share past 1", "[" + knot(Y1, .8, .6, 0, "\"LOAF\":2") + "," + knot(Y2, .4, .2, .5, "") + "]" },
                { "a knot with no cap", "[" + knot(Y1, .8, .6, 0, "").replace("\"carsPerHousehold\":0.0,", "") + "," + knot(Y2, .4, .2, .5, "") + "]" },
                { "a block that is no list", "5" },
        };
        for (String[] b : bad) {
            Consumption[] m = new Consumption[1];
            List<String> log = printed(() -> m[0] = model(file(b[1], "FLOUR")));
            check("a malformed years block (" + b[0] + ") leaves no years, the goods and the home-made source loaded, and says so once",
                    m[0].years().isEmpty() && m[0].yearAt(1) == null && m[0].items().size() == 2 && "FLOUR".equals(m[0].homeMadeFrom("LOAF")) && log.size() == 1,
                    m[0].years().size() + " years, " + m[0].items().size() + " goods, " + log.size() + " lines");
        }
        Consumption none = model(file(null, "FLOUR"));
        check("a file with no years block has no years: a city founded the 1900 way behaves as today's", none.years().isEmpty() && none.yearAt(1) == null && none.items().size() == 2);

        Consumption[] stranger = new Consumption[1];
        List<String> log = printed(() -> stranger[0] = model(file("[" + knot(Y1, .8, .6, 0, "\"LOAF\":.2,\"NOT_A_GOOD\":.5") + "," + knot(Y2, .4, .2, .5, "") + "]", "FLOUR")));
        check("a bought key that is no good in the file is ignored with a line of its own, and the rest of the block kept",
                stranger[0].years().size() == 2 && !stranger[0].years().get(0).bought().containsKey("NOT_A_GOOD") && stranger[0].years().get(0).bought().get("LOAF") == .2
                        && log.size() == 1, log.size() + " lines");
        Consumption[] ghost = new Consumption[1], self = new Consumption[1];
        List<String> ghostLog = printed(() -> ghost[0] = model(file(null, "NOT_A_GOOD")));
        List<String> selfLog = printed(() -> self[0] = model(file(null, "LOAF")));
        check("a homeMadeFrom that names no good is ignored with a line, and the goods still load", ghost[0].homeMadeFrom("LOAF") == null && ghost[0].items().size() == 2 && ghostLog.size() == 1);
        check("...and so is a good that names itself", self[0].homeMadeFrom("LOAF") == null && self[0].items().size() == 2 && selfLog.size() == 1);
    }

    /* ============================ 3. the home-made basket ============================ */

    /** A household's basket before the year's scale, in the null basket's units: what the year leaves bought, plus what the homes make of the raw goods. */
    static Map<String, Double> afterTheYear(Consumption c, Map<String, Double> today, Consumption.YearPoint y) {
        Map<String, Double> e = new LinkedHashMap<>();
        for (Map.Entry<String, Double> t : today.entrySet()) e.put(t.getKey(), t.getValue() * Math.min(1, y.bought(t.getKey())));
        for (Map.Entry<String, Double> t : today.entrySet()) {
            double b = y.bought(t.getKey());
            String raw = c.homeMadeFrom(t.getKey());
            if (b < 1 && raw != null) {
                e.merge(raw, t.getValue() * (1 - b) * c.item(t.getKey()).kcalPerUnit() / c.item(raw).kcalPerUnit(), Double::sum);
            }
        }
        return e;
    }

    static double dot(Map<String, Double> basket, Consumption c, boolean calories) {
        double sum = 0;
        for (Map.Entry<String, Double> e : basket.entrySet()) {
            Consumption.Item it = c.item(e.getKey());
            sum += e.getValue() * (calories ? it.kcalPerUnit() : it.worldImportPrice());
        }
        return sum;
    }

    static void sectionThree() {
        out.println("\n--- 3. the home-made basket, on the packaged model: today's calories in more grain and less bread, the same money for more calories ---");
        Consumption c = packaged();
        Consumption.YearPoint year = c.yearAt(ArrivalMix.januaryOf(c.years().get(0).year()));
        check("fixture: the first knot's point makes bread in part at home, from grain",
                year != null && year.bought("BREAD") < 1 && "GRAINS".equals(c.homeMadeFrom("BREAD")));

        // (a) a household bound by its appetite: the reference household's income, doubled until its basket holds all the calories it can eat
        double rich = c.referenceMultiple();
        while (rich < 1e9 && c.caloriesOf(c.basket(rich, 0)) < Consumption.KCAL_A_MONTH * Consumption.SATIATION * (1 - 1e-9)) rich *= 2;
        Map<String, Double> today = c.basket(rich, 0), made = c.basket(rich, 0, year);
        near("fixture: a rich enough household eats all it can: today's basket is the appetite's calories", c.caloriesOf(today), Consumption.KCAL_A_MONTH * Consumption.SATIATION, 1e-9);
        near("a calorie-bound household's 1900 basket holds the same calories as today's", c.caloriesOf(made), c.caloriesOf(today), 1e-9);
        check("...more grain", made.get("GRAINS") > today.get("GRAINS"), made.get("GRAINS") + " vs " + today.get("GRAINS"));
        check("...less bread", made.get("BREAD") < today.get("BREAD"), made.get("BREAD") + " vs " + today.get("BREAD"));

        Map<String, Double> pre = afterTheYear(c, today, year);
        double scale = dot(today, c, true) / dot(pre, c, true);
        boolean every = true;
        String worst = null;
        for (String key : today.keySet()) {
            double want = scale * pre.get(key);
            if (Math.abs(made.get(key) - want) > 1e-9 * Math.max(Math.abs(want), 1e-12)) {
                every = false;
                if (worst == null) worst = key + " " + made.get(key) + " vs " + want;
            }
        }
        check("...every good as the model says: what is bought, plus grain at the same calories for what is made at home, over the appetite's scale", every, worst);

        // A good bought whole, and not a source of anything, only scales: the bought share of bread, the bakery's, the ready meals' are read off it.
        String plain = null;
        for (Consumption.Item it : c.items()) {
            boolean raw = false;
            for (Consumption.Item other : c.items()) raw |= it.key().equals(c.homeMadeFrom(other.key()));
            if (plain == null && year.bought(it.key()) == 1 && !raw) plain = it.key();
        }
        double ratio = plain == null ? Double.NaN : made.get(plain) / today.get(plain);
        near("fixture: a good bought whole and made from nothing scales by the household's ratio, which is the calories' ratio", ratio, scale, 1e-9);
        near("bread is the bought share of today's, before the scale", made.get("BREAD"), ratio * year.bought("BREAD") * today.get("BREAD"), 1e-9);
        // ...and on a wider year written here (the first knot's, with the bakery's goods and the ready meals half made at home: the data names
        // bread alone), the bakery's goods are made from grain as the bread is, and the ready meals, which name no home-made source, are bought
        // their share and the rest done without.
        Map<String, Double> widened = new LinkedHashMap<>(year.bought());
        widened.put("BAKERY", .5);
        widened.put("READY_MEALS", .5);
        Consumption.YearPoint wide = new Consumption.YearPoint(year.lampShare(), year.coalShare(), year.carCeiling(), widened);
        check("fixture: the bakery's goods are made from grain and the ready meals from nothing, and neither is the good the ratio is read off",
                "GRAINS".equals(c.homeMadeFrom("BAKERY")) && c.homeMadeFrom("READY_MEALS") == null && !"BAKERY".equals(plain) && !"READY_MEALS".equals(plain));
        Map<String, Double> madeWide = c.basket(rich, 0, wide);
        double ratioWide = plain == null ? Double.NaN : madeWide.get(plain) / today.get(plain);
        near("...the bakery's goods are the bought share of today's, before the scale", madeWide.get("BAKERY"), ratioWide * wide.bought("BAKERY") * today.get("BAKERY"), 1e-9);
        near("...and a good with no home-made source is bought its share and the rest done without: the ready meals", madeWide.get("READY_MEALS"),
                ratioWide * wide.bought("READY_MEALS") * today.get("READY_MEALS"), 1e-9);
        near("...and the calories are still the appetite's", c.caloriesOf(madeWide), c.caloriesOf(today), 1e-9);

        // (b) the transfers are worked from the quantities as first computed: the grain's own bought share does not touch the bread's grain
        Consumption.YearPoint both = new Consumption.YearPoint(0, 0, 1, Map.of("BREAD", .5, "GRAINS", .5));
        Map<String, Double> madeBoth = c.basket(rich, 0, both);
        Map<String, Double> preBoth = afterTheYear(c, today, both);
        double scaleBoth = dot(today, c, true) / dot(preBoth, c, true);
        near("when the grain is itself made at home in part, the bread's grain is worked from the grain as first computed (two passes)",
                madeBoth.get("GRAINS"), scaleBoth * preBoth.get("GRAINS"), 1e-9);
        near("...and the calories are still the appetite's", c.caloriesOf(madeBoth), c.caloriesOf(today), 1e-9);

        // (c) a household bound by its money
        double poor = 1.5;
        Map<String, Double> poorToday = c.basket(poor, 0), poorMade = c.basket(poor, 0, year);
        double spend = poor * c.subsistenceCostAtWorldPrices() * Consumption.foodShare(poor);
        near("fixture: a poor household spends all it has on food: today's basket costs the food share of its income", c.costOf(poorToday), spend, 1e-9);
        check("...and does not eat all it could", c.caloriesOf(poorToday) < Consumption.KCAL_A_MONTH * Consumption.SATIATION);
        near("a money-bound household's 1900 basket costs the same", c.costOf(poorMade), c.costOf(poorToday), 1e-9);
        check("...and holds more calories: it bakes its own bread from the cheapest calorie", c.caloriesOf(poorMade) > c.caloriesOf(poorToday),
                c.caloriesOf(poorMade) + " vs " + c.caloriesOf(poorToday));

        // (d) no year is today's, to the bit
        boolean bits = true;
        for (double x : new double[] { .5, 1, 1.5, 3, 9, rich, 90, 400 }) {
            for (double pressure : new double[] { 0, .5, 1.5 }) {
                bits &= sameBasket(c.basket(x, pressure, null), c.basket(x, pressure));
            }
        }
        check("with no year the three-argument basket is the two-argument one, key by key, to the bit, across incomes and time pressures", bits);
        check("...and a year that names no good changes nothing: today's basket, to the bit",
                sameBasket(c.basket(rich, .5, new Consumption.YearPoint(0, 0, 1, Map.of())), c.basket(rich, .5)));
        check("...and nobody earning nothing eats nothing, in any year", c.basket(0, 0, year).isEmpty() && c.basket(-1, 0, year).isEmpty());
    }

    static boolean sameBasket(Map<String, Double> a, Map<String, Double> b) {
        if (a.size() != b.size() || !new ArrayList<>(a.keySet()).equals(new ArrayList<>(b.keySet()))) return false;
        for (String key : a.keySet()) if (Double.doubleToLongBits(a.get(key)) != Double.doubleToLongBits(b.get(key))) return false;
        return true;
    }

    /* ============================ the shops' basket, worked here ============================ */

    /** The basket per head the shops stock, worked as CityBasket.perHead() works it, but in the year given (null: today's). */
    static Map<Good, Double> shopsBasket(Game g, Consumption.YearPoint year) {
        Map<Good, Double> out = new java.util.EnumMap<>(Good.class);
        Consumption c = g.getConsumption();
        double rate = g.getForeignAccounts() == null ? 1 : g.getForeignAccounts().getRate();
        double heads = 0;
        for (Household cell : g.getHouseholdBalance().cells()) {
            double homes = cell.households();
            if (homes < .5) continue;
            double perHome = cell.headcount();
            if (perHome <= 0) continue;
            double people = homes * perHome;
            double x = c.incomeMultiple(cell.disposable() / perHome, rate);
            if (!(x > 0)) continue;
            Map<String, Double> theirs = year == null ? c.basket(x, Consumption.timePressure(cell.shape())) : c.basket(x, Consumption.timePressure(cell.shape()), year);
            for (Map.Entry<String, Double> e : theirs.entrySet()) {
                Good good = Good.byName(e.getKey());
                if (good != null) out.merge(good, e.getValue() * people, Double::sum);
            }
            heads += people;
        }
        if (heads <= 0) {
            Map<Good, Double> reference = new java.util.EnumMap<>(Good.class);
            Map<String, Double> theirs = year == null ? c.basket(c.referenceMultiple(), 0) : c.basket(c.referenceMultiple(), 0, year);
            for (Map.Entry<String, Double> e : theirs.entrySet()) {
                Good good = Good.byName(e.getKey());
                if (good != null && e.getValue() > 0) reference.put(good, e.getValue());
            }
            return reference;
        }
        for (Map.Entry<Good, Double> e : out.entrySet()) e.setValue(e.getValue() / heads);
        return out;
    }

    static boolean sameShelf(Map<Good, Double> a, Map<Good, Double> b) {
        if (!a.keySet().equals(b.keySet())) return false;
        for (Good good : a.keySet()) if (Double.doubleToLongBits(a.get(good)) != Double.doubleToLongBits(b.get(good))) return false;
        return true;
    }

    /** A good's share of the basket's kilograms. */
    static double kgShare(Map<Good, Double> basket, Good good) {
        double total = 0;
        for (double v : basket.values()) total += v;
        return total > 0 ? basket.getOrDefault(good, 0.0) / total : 0;
    }

    /* ============================ 4. a default city reads none of it ============================ */

    static void sectionFour() {
        out.println("\n--- 4. a default city reads none of it: no year in any month, the homes' shares 0, no lamp oil or coal sold, its shops' basket today's ---");
        Game g = village("basket-year-default", DEFAULT);
        HomeFuel hf = g.getHomeFuel();
        Retail retail = g.getSectors().retail();
        check("fixture: a village founded the default way, whose homes light and heat as today", !g.foundedUnfitted() && !hf.isOn() && !hf.hasSale());
        boolean noYear = g.basketYear() == null, shares = true, sold = true, shelf = true, bill = true;
        String bad = null;
        int months = SETTLE + 3;
        for (int i = 1; i <= months; i++) {
            step(g, 1);
            noYear &= g.basketYear() == null;
            shares &= hf.getLampShare() == 0 && hf.getCoalShare() == 0 && !hf.isOn();
            boolean none = hf.getLitres() == 0 && hf.getTonnes() == 0 && !hf.hasSale()
                    && Forms1900Check.unitsSold(retail, Good.JET) == 0 && Forms1900Check.unitsSold(retail, Good.THERMAL_COAL) == 0;
            sold &= none;
            bill &= g.getHouseholds().getHomeFuel() == 0;
            boolean same = sameShelf(g.cityBasketPerHead(), shopsBasket(g, null));
            shelf &= same;
            if (!(none && same) && bad == null) bad = "month " + g.getMonth() + (none ? ", shelf differs" : ", fuel sold");
        }
        check("in every month of " + months + " the city has no year to read", noYear);
        check("...the homes' lamp and stove shares stay 0", shares);
        check("...no lamp oil or coal is asked, sold to the homes or held, and the households are charged none", sold && bill, bad);
        check("...and the shops' basket is the one worked with no year at all, to the bit, every month", shelf, bad);
        Map<Good, Double> now = g.cityBasketPerHead();
        check("fixture: the shops' basket is a real one, with bread and grain on it", kgShare(now, Good.BREAD) > 0 && kgShare(now, Good.GRAINS) > 0);
    }

    /* ============================ 5. a 1900 city's shops ============================ */

    static void sectionFive() {
        out.println("\n--- 5. a 1900 city's shops stock the year's basket: less bread and more grain than today's, and than a twin's with no years ---");
        Game old = village("basket-year-1900", THE_1900_WAY), twin = village("basket-year-twin", THE_1900_WAY);
        // The twin is founded the same way, on the same wage ladder and the same village, and reads a file with no years block: a missing block is
        // no years. (A default-founded twin would differ in its households' incomes as well, and bread is Bennett's inferior good, so it would
        // compare the incomes and not the year.)
        quietly(() -> twin.getConsumption().loadFrom(new StringReader(packagedWithoutYears())));
        check("fixture: two villages founded the 1900 way, one on the packaged file and one on the same file with no years", old.foundedUnfitted() && twin.foundedUnfitted()
                && !old.getConsumption().years().isEmpty() && twin.getConsumption().years().isEmpty());
        step(old, SETTLE + 3);
        step(twin, SETTLE + 3);
        Consumption.YearPoint year = old.basketYear();
        check("fixture: the first has a year, with bread made in part at home, and the second has none", year != null && year.bought("BREAD") < 1 && twin.basketYear() == null);
        Map<Good, Double> shelf = old.cityBasketPerHead();
        check("the shelf the 1900 city's shops stock is the basket worked in the year's point, to the bit", sameShelf(shelf, shopsBasket(old, year)));
        Map<Good, Double> todays = shopsBasket(old, null);
        check("...which is not the one it would stock with no year", !sameShelf(shelf, todays));
        check("the 1900 city's bread is a smaller share of its shelf than the same city's would be with no year", kgShare(shelf, Good.BREAD) < kgShare(todays, Good.BREAD),
                kgShare(shelf, Good.BREAD) + " vs " + kgShare(todays, Good.BREAD));
        check("...and its grain a larger one", kgShare(shelf, Good.GRAINS) > kgShare(todays, Good.GRAINS), kgShare(shelf, Good.GRAINS) + " vs " + kgShare(todays, Good.GRAINS));
        Map<Good, Double> theirs = twin.cityBasketPerHead();
        check("the twin's shelf, with no years to read, is the no-year one, to the bit", sameShelf(theirs, shopsBasket(twin, null)));
        check("the 1900 city's bread share is below the twin's", kgShare(shelf, Good.BREAD) < kgShare(theirs, Good.BREAD),
                kgShare(shelf, Good.BREAD) + " vs " + kgShare(theirs, Good.BREAD));
        check("...and its grain share above", kgShare(shelf, Good.GRAINS) > kgShare(theirs, Good.GRAINS), kgShare(shelf, Good.GRAINS) + " vs " + kgShare(theirs, Good.GRAINS));
        check("...and the twin's homes burn no lamp oil, with no year to say they do", !twin.getHomeFuel().isOn() && twin.getHomeFuel().getLitres() == 0);
    }

    /* ============================ 6. lamp oil and coal by year ============================ */

    static void sectionSix() {
        out.println("\n--- 6. lamp oil and coal by year: the homes' shares are the year's, the litres and tonnes sold the homes x share x per-home figure, the bill charged the month after ---");
        Game g = village("basket-year-lamp", THE_1900_WAY);
        HomeFuel hf = g.getHomeFuel();
        Retail retail = g.getSectors().retail();
        check("fixture: a 1900 village with homes and a shop, whose homes' shares are the first month's year (set at the founding)",
                g.foundedUnfitted() && g.basketYear() != null && hf.getLampShare() == g.basketYear().lampShare() && hf.getCoalShare() == g.basketYear().coalShare()
                        && (hf.getLampShare() > 0 || hf.getCoalShare() > 0) && !hf.hasSale());
        step(g, SETTLE);

        boolean shares = true, litres = true, tonnes = true, charged = true, moved = false;
        String bad = null;
        double bill = hf.getBill(), firstShare = hf.getLampShare();
        check("fixture: the homes were struck a sale in the month before the first one measured", hf.hasSale() && bill > 0, String.valueOf(bill));
        for (int i = 1; i <= 4; i++) {
            double paid = bill;
            step(g, 1);
            Consumption.YearPoint year = g.basketYear();
            shares &= year != null && hf.getLampShare() == year.lampShare() && hf.getCoalShare() == year.coalShare();
            double homes = hf.getHomes();
            double wantLitres = homes * hf.getLampShare() * HomeFuel.LAMP_OIL_LITRES_A_HOME_MONTH;
            double wantTonnes = homes * hf.getCoalShare() * HomeFuel.COAL_TONNES_A_HOME_MONTH;
            boolean l = homes > 0 && Math.abs(Forms1900Check.unitsSold(retail, Good.JET) - wantLitres) <= 1e-9 * wantLitres && Math.abs(hf.getLitres() - wantLitres) <= 1e-9 * wantLitres;
            boolean t = Math.abs(Forms1900Check.unitsSold(retail, Good.THERMAL_COAL) - wantTonnes) <= 1e-9 * wantTonnes && Math.abs(hf.getTonnes() - wantTonnes) <= 1e-9 * wantTonnes;
            litres &= l && wantLitres > 0;
            tonnes &= t && wantTonnes > 0;
            boolean c = Double.doubleToLongBits(g.getHouseholds().getHomeFuel()) == Double.doubleToLongBits(paid) && paid > 0;
            charged &= c;
            if (!(l && t && c) && bad == null) bad = "month " + g.getMonth() + ": litres " + l + ", tonnes " + t + ", charged " + c;
            bill = hf.getBill();
            moved |= hf.getLampShare() != firstShare;
        }
        check("each month the homes' lamp and stove shares are the year's (Game.basketYear()), as the clock moves", shares);
        check("...the JET Retail sells the homes and the litres HomeFuel struck are the homes x lamp share x LAMP_OIL_LITRES_A_HOME_MONTH", litres, bad);
        check("...the THERMAL_COAL the homes x coal share x COAL_TONNES_A_HOME_MONTH", tonnes, bad);
        check("...and the households are charged the bill the month after, to the bit, the month's own having moved with the year", charged && bill > 0 && moved, bad);
        check("fixture: the year's lamp share moved over the months played (the shares follow the year, they are not set once)", moved);
    }

    /* ============================ 7. no car in 1900 ============================ */

    /** CarCheck's town (900 houses, 60 roads, shops, power, water, depots) founded as given, the consumption file replaced if one is given, then played. */
    static Game carTown(String label, Founding founding, String consumption, int months, double[] ceilings) {
        Game g = new Game(GameFiles.scratch(label));
        quietly(() -> {
            g.newGame(founding);
            if (consumption != null) g.getConsumption().loadFrom(new StringReader(consumption));
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.getLandManager().setOwnedSqFt(400_000_000L);
            BuildingManager b = g.getBuildingManager();
            g.buildStack(b.getTemplateByName("House"), 900, true);
            g.buildStack(b.getTemplateByName("Convenience Store"), 20, true);
            g.buildStack(b.getTemplateByName("Small Grocery Store"), 6, true);
            g.buildStack(b.getTemplateByName("Paved Road"), 60, true);
            g.buildStack(b.getTemplateByName("Coal Power Plant"), 1, true);
            g.buildStack(b.getTemplateByName("Water Treatment Plant"), 1, true);
            g.buildStack(b.getTemplateByName("Construction Depot"), 4, true);
        });
        for (int i = 0; i < months; i++) {
            step(g, 1);
            // The ceiling the car market ran under this month (Motoring.month()) and the cap the month's year puts on it, 1 where there is none.
            Consumption.YearPoint year = g.basketYear();
            ceilings[0] = Math.max(ceilings[0], year == null ? 1 : year.carCeiling());
            ceilings[1] = Math.max(ceilings[1], g.getMotoring().getOwnershipCeiling());
            if (year != null && g.getMotoring().getOwnershipCeiling() > year.carCeiling() * (1 + 1e-12) + 1e-12) ceilings[2]++;
        }
        return g;
    }

    static void sectionSeven() {
        out.println("\n--- 7. no car in 1900: where a default city motorises, the 1900 city's fleet stays under the year's cap and its ceiling is at most it ---");
        final int months = 24;
        double[] defaultCeilings = new double[3], cappedCeilings = new double[3], liftedCeilings = new double[3];
        Game plain = carTown("basket-year-cars-default", DEFAULT, null, months, defaultCeilings);
        Game capped = carTown("basket-year-cars-1900", THE_1900_WAY, null, months, cappedCeilings);
        Game lifted = carTown("basket-year-cars-lifted", THE_1900_WAY, packagedWithoutTheCap(), months, liftedCeilings);
        double plainCars = plain.getHouseholdBalance().carsPerHousehold(), cappedCars = capped.getHouseholdBalance().carsPerHousehold(),
                liftedCars = lifted.getHouseholdBalance().carsPerHousehold();
        System.out.printf("   %d months: %.4f cars a household in the default town, %.4f in the 1900 town, %.4f with the cap lifted (the year's cap peaked at %.4f)%n",
                months, plainCars, cappedCars, liftedCars, cappedCeilings[0]);
        check("fixture: the default town's households can afford cars, and buy them", plainCars > 0 && plain.getHouseholdBalance().totalCars() > 0, String.valueOf(plainCars));
        check("fixture: the 1900 town with the cap lifted (the same file, every knot's cap 1) buys them too: only the cap holds the other back",
                liftedCars > 0 && lifted.foundedUnfitted() && liftedCeilings[0] == 1, String.valueOf(liftedCars));
        check("fixture: the year's cap was below 1 in the 1900 town, and 0 in its first month", cappedCeilings[0] < 1 && capped.getConsumption().yearAt(1) != null
                && capped.getConsumption().yearAt(1).carCeiling() == 0);
        check("the 1900 town's fleet a household stays under the highest cap the year put on it, in all " + months + " months",
                cappedCars <= cappedCeilings[0] * (1 + 1e-9) + 1e-9, cappedCars + " vs " + cappedCeilings[0]);
        check("...and is smaller than the same town's with the cap lifted, and than the default town's", cappedCars < liftedCars && cappedCars < plainCars);
        check("the ownership ceiling the car market ran under was at most the year's cap in every month of the 1900 town", cappedCeilings[2] == 0 && cappedCeilings[1] <= cappedCeilings[0] * (1 + 1e-12) + 1e-12,
                cappedCeilings[1] + " vs " + cappedCeilings[0]);
        check("...while the default town ran under the transit ceiling alone (no year, no cap) and the lifted one under the same", defaultCeilings[2] == 0 && defaultCeilings[1] > cappedCeilings[1]
                && liftedCeilings[2] == 0);
    }

    /* ============================ 8. the held sale crosses a save ============================ */

    static void sectionEight() throws Exception {
        out.println("\n--- 8. the held sale crosses a save: a 1900 city saved in a month that struck lamp oil and coal loads the same sale and pays the same bill ---");
        Game g = village("basket-year-save", THE_1900_WAY);
        step(g, SETTLE + 1);
        HomeFuel hf = g.getHomeFuel();
        check("fixture: the month struck lamp oil and coal, and the sale is held for the households' next bill",
                hf.hasSale() && hf.getLitres() > 0 && hf.getTonnes() > 0 && hf.getBill() > 0 && hf.getHomes() > 0,
                String.format("%.1f L, %.3f t, bill %.6f", hf.getLitres(), hf.getTonnes(), hf.getBill()));
        double[] held = hf.heldSale();
        check("...and it is six numbers, a fresh array each time", held != null && held.length == 6 && held != hf.heldSale() && Arrays.equals(held, hf.heldSale()));
        GameFiles files = g.getGameFiles();
        quietly(() -> g.saveGame(10, "basket-year"));
        Path file = files.saveFile(10);
        String json = Files.readString(file);
        check("the save of a month that struck one carries the sale under its own key", json.contains("\"homeFuelSale\""));

        Game back = new Game(files);
        quietly(() -> back.loadGameSave(10));
        check("a fresh Game loaded from it holds the same sale, every figure to the bit", Arrays.equals(back.getHomeFuel().heldSale(), held),
                Arrays.toString(back.getHomeFuel().heldSale()) + " vs " + Arrays.toString(held));
        check("...in the same month, in a city founded the same way, on the same year", back.getMonth() == g.getMonth() && back.foundedUnfitted()
                && back.basketYear() != null && back.basketYear().equals(g.basketYear()));
        check("...and the homes' shares, which are not saved, are the year's again", back.getHomeFuel().getLampShare() == g.getHomeFuel().getLampShare()
                && back.getHomeFuel().getCoalShare() == g.getHomeFuel().getCoalShare());

        // A month on, the households of both cities are charged the sale.
        double bill = hf.getBill();
        step(g, 1);
        step(back, 1);
        same("a month on, the loaded city's households are charged the unsaved twin's home fuel bill, to the bit", back.getHouseholds().getHomeFuel(), g.getHouseholds().getHomeFuel());
        same("...which is the sale the month before struck", g.getHouseholds().getHomeFuel(), bill);

        // The control: the same file without the key loses the month's bill, so it is the key that carries it.
        JsonObject o = JsonParser.parseString(json).getAsJsonObject();
        o.remove("homeFuelSale");
        Files.writeString(file, new com.google.gson.GsonBuilder().serializeSpecialFloatingPointValues().create().toJson(o));
        Game lost = new Game(files);
        quietly(() -> lost.loadGameSave(10));
        check("control: the same save with the key removed holds no sale", !lost.getHomeFuel().hasSale());
        step(lost, 1);
        check("...and its households are charged none, which is the bill the key carries", lost.getHouseholds().getHomeFuel() == 0 && bill > 0, String.valueOf(lost.getHouseholds().getHomeFuel()));

        // A default city strikes none, and its save has no such key.
        Game plain = village("basket-year-save-default", DEFAULT);
        step(plain, SETTLE + 1);
        quietly(() -> plain.saveGame(10, "basket-year-default"));
        String plainJson = Files.readString(plain.getGameFiles().saveFile(10));
        check("a default city's save has no homeFuelSale: Gson leaves the null out and the file is as it was", !plain.getHomeFuel().hasSale() && !plainJson.contains("homeFuelSale"));
        Game plainBack = new Game(plain.getGameFiles());
        quietly(() -> plainBack.loadGameSave(10));
        check("...and loaded it holds no sale, shares or year", !plainBack.getHomeFuel().hasSale() && !plainBack.getHomeFuel().isOn() && plainBack.basketYear() == null);

        // The sale of a city left behind does not follow a new one into the same Game.
        quietly(() -> g.newGame(DEFAULT));
        check("a Game that played a 1900 city and is founded the default way holds none of its shares or its sale", !g.foundedUnfitted() && !g.getHomeFuel().isOn()
                && !g.getHomeFuel().hasSale() && g.basketYear() == null);
    }

    /* ============================ 9. today's from the last knot ============================ */

    static void sectionNine() {
        out.println("\n--- 9. today's from the last knot: a 1900 city whose year has run out has no year, no share, no cap and today's basket ---");
        Game g = village("basket-year-last", THE_1900_WAY);
        String shortYear = packagedWithTheYearShort();
        quietly(() -> g.getConsumption().loadFrom(new StringReader(shortYear)));
        Consumption c = g.getConsumption();
        int january = ArrivalMix.januaryOf(c.years().get(c.years().size() - 1).year());
        check("fixture: the model is the packaged one with the last knot a year after the first, so the year runs out in month " + january, c.years().size() == 2 && january > 1);

        boolean year = true, shares = true;
        while (g.getMonth() < january - 1) {
            step(g, 1);
            year &= g.basketYear() != null && g.getHomeFuel().getLampShare() == g.basketYear().lampShare();
        }
        check("before the last knot's January every month has a year, and the homes' shares are its", year && g.getMonth() == january - 1);
        check("fixture: the homes still burn lamp oil and coal in the month before", g.getHomeFuel().isOn() || g.basketYear().lampShare() == 0);
        step(g, 1);
        check("fixture: the month played is the last knot's January", g.getMonth() == january);
        HomeFuel hf = g.getHomeFuel();
        check("from the last knot's January the city reads no year", g.basketYear() == null);
        shares &= hf.getLampShare() == 0 && hf.getCoalShare() == 0 && !hf.isOn();
        check("...the homes' shares are 0", shares);
        check("...nothing is sold to the homes, the sale struck the month before having been charged and cleared", hf.getLitres() == 0 && hf.getTonnes() == 0 && !hf.hasSale()
                && Forms1900Check.unitsSold(g.getSectors().retail(), Good.JET) == 0 && Forms1900Check.unitsSold(g.getSectors().retail(), Good.THERMAL_COAL) == 0);
        step(g, 1);
        check("...and the households are charged none the month after", g.getHouseholds().getHomeFuel() == 0 && g.basketYear() == null && hf.getLampShare() == 0);
        check("...the cars have no cap: the ownership ceiling is the transit one, 1 where no line is built",
                g.getInfrastructureManager().getTransitCover() > 0 || g.getMotoring().getOwnershipCeiling() == 1, String.valueOf(g.getMotoring().getOwnershipCeiling()));
        check("...and the shops stock today's basket, to the bit", sameShelf(g.cityBasketPerHead(), shopsBasket(g, null)));
        Consumption.YearPoint early = c.yearAt(1);
        check("fixture: the same city's first month had a year, and its shelf then was not today's", early != null && !sameBasket(c.basket(c.referenceMultiple(), 0, early), c.basket(c.referenceMultiple(), 0)));
    }
}

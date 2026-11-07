package ham.citybuildersim;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * What a household eats, and what changes it.
 *
 * ONE PLACE, AND NOBODY KEEPS A COPY. Jerus: "basically its just where other
 * files get there numbers from". Every figure a grocery basket is built out of
 * is in consumption.json and every question about one is answered here. The
 * rule this is meant to end is the one HistoryScreen.historyValues() broke on
 * 2026-09-15, when a second copy of the unemployment formula sat beside the
 * model's and rotted for nine days without anybody noticing.
 *
 * ==========================================================================
 * THE TWO LAWS THESE NUMBERS ARE SHAPED TO OBEY
 * ==========================================================================
 *
 * ENGEL'S LAW, which is the most robust regularity in economics: as income
 * rises the SHARE of it spent on food falls, while the AMOUNT rises. Both
 * halves matter. A model with only the first makes rich cities starve and a
 * model with only the second makes them spend everything on groceries.
 *
 * BENNETT'S LAW, the second most robust: as income rises the share of CALORIES
 * from starchy staples falls and the share from meat, dairy and produce rises.
 * That is the whole of the "healthy or unhealthy" question - it is not a
 * separate dial, it is what income does, and it is why GRAINS carries a
 * NEGATIVE elasticity. A staple is an inferior good: a household eats less
 * rice as it gets richer, not more.
 *
 * AND A THIRD AXIS THAT IS NOT INCOME AT ALL. Convenience food is bought for
 * TIME, not for money - a working parent buys it at any income, and an
 * out-of-work parent with the same children and the same money does not,
 * because what they are short of is money and what they have is afternoons.
 * Folding that into income would make a poor household that cooks and a
 * time-poor household that does not into the same household, and they are
 * different cities with different health.
 *
 * ==========================================================================
 * WHY INCOME IS MEASURED IN MULTIPLES OF SUBSISTENCE
 * ==========================================================================
 *
 * Engel's curve is a logarithm of income, and a logarithm of a MONEY figure is
 * a money constant wearing a function's clothes: lop two zeroes off the
 * currency and every household in the city would appear to fall to a tenth of
 * its real income and start eating like the destitute. This project has found
 * twenty bugs of that family and seeded four constants against it.
 *
 * So income is divided by what bare subsistence COSTS - both are money, the
 * ratio is a pure number, and a currency reform moves neither. It needs no
 * seeding, it cannot be missed by a redenominate(), and it means something a
 * player can say out loud: "this household can afford four times the calories
 * it needs to survive".
 *
 * ==========================================================================
 * NOTHING EATS THIS YET
 * ==========================================================================
 *
 * Deliberate, and Jerus's call. The model, the data and the harness are real
 * and tested; no household consumes any of it. FOOD remains the good that
 * drives hunger, subsistence and the shopping plan, exactly as it did. What
 * this batch buys is the SHADOW BASKET - what every cell in a played city
 * WOULD buy - measured against what it spends today, which is the evidence
 * that decides whether turning it on is a realism change or a balance change.
 * Hunger feeds sickness feeds mortality, so the cost of guessing that wrong is
 * counted in people.
 */
public class Consumption {

    public static final String FILE_NAME = "consumption.json";

    /* =====================================================================
       ENGEL'S CURVE, AND WHY IT HAS NO FLOOR IN IT

       share = A - B x ln(x), where x is income over what subsistence costs.

       ANCHORED ON ONE REAL POINT AND ONE DEFINITION. At x = 1 a household can
       afford exactly the calories it needs and nothing else, so the share is 1:
       every dollar is food. The bottom fifth of Canadian households spend .24
       of their money on food at home, and their income per head against a bare
       grain-and-oil diet is about ninety times its cost - those two fix B.

       THE FIRST DRAFT HAD A FLOOR AND THE FLOOR WAS THE BUG. A logarithm keeps
       falling and real food spending does not, so .09 was typed in as the
       richest fifth's share. What that produced, measured before anything ate
       it: calories that went DOWN as a household got richer - 426 a day at five
       times subsistence and 194 at twelve - because the share collapsed faster
       than the income grew. A constant doing a mechanism's job, which is the
       same shape as the shelf floor that has held the price index still for the
       life of this project.

       THE MECHANISM IS SATIATION. People eat about two thousand calories a day
       whatever they earn; what changes is what those calories are made of. So a
       basket is scaled by the LESSER of what the household can afford and what
       it can eat - poor households are money-limited and go hungry, rich ones
       are calorie-limited and buy dearer food instead of more. The falling food
       share at the top is then something the model PRODUCES rather than
       something it is told: the bill is flat and the income is not.
       ===================================================================== */

    /** The share at subsistence: all of it. */
    public static final double ENGEL_A = 1.0;

    /** How fast the share falls with log income. Set by the .24 at ninety times subsistence. */
    public static final double ENGEL_B = (ENGEL_A - .24) / Math.log(90);

    /**
     * How much past need a household eats when money has stopped being the
     * constraint. Fifteen per cent, which is roughly the gap between what a
     * rich country needs and what it actually consumes.
     */
    public static final double SATIATION = 1.15;

    /**
     * Where the curve stops being a description of anything, DERIVED.
     *
     * The money a household puts into food is share(x) times income, and the
     * slope of that in x is share - B. So the implied spend rises while the
     * share is above B, is flat at exactly B, and FALLS below it: past that
     * point the curve says a richer household buys less food, which is not
     * Engel's law, it is a logarithm running out of road.
     *
     * Measured before it ate anything: at five hundred times subsistence the
     * share came out negative and the basket came back EMPTY - a household rich
     * enough to buy the city starving to death. Held at B the implied spend is
     * flat rather than falling, appetite binds long before it matters, and
     * nothing anywhere reads a floor that was typed in rather than solved for.
     */
    public static final double ENGEL_MIN_SHARE = ENGEL_B;

    /**
     * How hard the time axis pushes, per dependant an earner carries.
     *
     * A working single parent - one dependant, one adult, pressure 1.0 - moves
     * about a fifth of their basket toward food that needs no cooking. Large
     * enough to see on a screen and to change what a city imports; small
     * enough that it never beats the income axis, which is the one with two
     * centuries of evidence behind it.
     */
    public static final double TIME_WEIGHT = .40;

    /** What one person needs in a month, in calories. Two thousand a day. */
    public static final double KCAL_A_MONTH = 2_000 * 30;

    /* ------------------------------ the goods ------------------------------ */

    /**
     * One row of the file.
     *
     * ADDRESSED BY KEY, NEVER BY POSITION. Good is an enum today and will not
     * be one at a thousand goods; nothing here knows or cares, so the day that
     * changes this file does not. It is the same rule the save format arrived
     * at in September, applied before rather than after the accident.
     */
    public record Item(String key, String label, String unit, String category,
                       double worldImportPrice, double worldExportPrice,
                       double kcalPerUnit, double quality, double elasticity,
                       double convenience, double referenceKg) { }

    /**
     * One knot of the household's basket by year, at January of its year: what a city founded the 1900 way (Founding.isUnfitted()) holds
     * then. lampShare is the share of the homes lit by lamp oil and coalShare the share heated by a coal stove (HomeFuel); carsPerHousehold
     * caps the households' cars (Motoring's ownership ceiling, 1 being no cap); bought is the share of each good a household buys ready made,
     * the rest being made at home (a key not named is bought whole). source, estimate and note say where the figures were read.
     *
     * WHY (docs/studies/research-1900.md 5.4 item 5): "1900's has more raw staples, lamp oil and coal, and no car". The map is unmodifiable.
     */
    public record Year(int year, double lampShare, double coalShare, double carsPerHousehold, Map<String, Double> bought,
                       String source, boolean estimate, String note) {
        public Year {
            bought = bought == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(bought));
        }
    }

    /**
     * The figures of one game month, interpolated between two knots (Consumption.yearAt): the shares of the homes on lamp oil and coal, the
     * ceiling the year puts on the households' cars, and the share of each good a household buys ready made. What Consumption.basket(...,
     * YearPoint) and Motoring.month() read. The map is unmodifiable and a good it does not name is bought whole.
     */
    public record YearPoint(double lampShare, double coalShare, double carCeiling, Map<String, Double> bought) {
        public YearPoint {
            bought = bought == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(bought));
        }

        /** The share of this good a household buys ready made: the map's, or 1 (bought whole) for a good it does not name. */
        public double bought(String key) {
            Double b = bought.get(key);
            return b == null ? 1 : b;
        }
    }

    private final Map<String, Item> byKey = new LinkedHashMap<>();
    private final Map<String, Double> targetShares = new LinkedHashMap<>();
    private final List<Item> all = new ArrayList<>();
    /** Each good's homeMadeFrom good by key, only for the goods that name one the file holds (see parse()). */
    private final Map<String, String> homeMade = new LinkedHashMap<>();
    /** For each good in `all` the index of its homeMadeFrom good, or -1: homeMade turned into positions once, so basket() scans nothing. */
    private int[] homeMadeIndex = new int[0];
    private final List<Year> years = new ArrayList<>();
    private double referenceIncomeMultiple = 8.0;
    private String source = "none";
    private double meanConvenience;

    public String getSource()           { return source; }
    public List<Item> items()           { return Collections.unmodifiableList(all); }
    public Item item(String key)        { return key == null ? null : byKey.get(key); }
    public boolean isLoaded()           { return !all.isEmpty(); }
    public double referenceMultiple()   { return referenceIncomeMultiple; }

    /**
     * The household's basket by year, in year order, empty when the file has no years block (or a malformed one, which parse() drops).
     * Read only by a city founded the 1900 way; see yearAt(). WHY: research-1900 5.4 item 5, R0e.
     */
    public List<Year> years() { return Collections.unmodifiableList(years); }

    /**
     * The good a good is made from at home when a household does not buy it ready made, by key; null when it names none, or names a good the
     * file does not hold, or itself. WHY: research-1900 section 4, "Home butter and bread: data, not a mechanism" - the 1900 household baked
     * its bread from flour at the same calories, which is a line in the file and not a branch in the code (R0e).
     */
    public String homeMadeFrom(String key) { return key == null ? null : homeMade.get(key); }

    /** The calorie share each category should carry in a balanced diet. */
    public Map<String, Double> targetShares() { return Collections.unmodifiableMap(targetShares); }

    /** Every category in the file, in the order the goods first mention them. */
    public List<String> categories() {
        List<String> out = new ArrayList<>();
        for (Item i : all) if (!out.contains(i.category())) out.add(i.category());
        return out;
    }

    /* ------------------------------- loading ------------------------------- */

    /**
     * An editable copy beside the game first, the packaged one second - the
     * same two steps and the same order as buildings.json, because a modder
     * who can retune the buildings should be able to retune the diet.
     */
    public boolean load() {
        File external = new File(FILE_NAME);
        if (external.isFile()) {
            try (Reader r = new FileReader(external, StandardCharsets.UTF_8)) {
                if (parse(r)) {
                    source = external.getAbsolutePath();
                    return true;
                }
            } catch (Exception e) {
                System.out.println("Could not read " + external.getAbsolutePath() + ": " + e.getMessage());
            }
        }
        try (InputStream in = Consumption.class.getResourceAsStream("/" + FILE_NAME)) {
            if (in == null) {
                System.out.println("No packaged " + FILE_NAME + " found; nobody eats anything.");
                return false;
            }
            try (Reader r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                if (parse(r)) {
                    source = "packaged " + FILE_NAME;
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Could not read packaged " + FILE_NAME + ": " + e.getMessage());
        }
        return false;
    }

    /**
     * Loads from anywhere, for a harness that needs to ask what the model does
     * with DIFFERENT numbers - the currency-reform test below all others, which
     * cannot be written at all if the only way in is a file on disk.
     */
    public boolean loadFrom(Reader reader) {
        try {
            if (parse(reader)) {
                source = "a reader";
                return true;
            }
        } catch (Exception e) {
            System.out.println("Could not read consumption data: " + e.getMessage());
        }
        return false;
    }

    private boolean parse(Reader reader) {
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        JsonArray goods = root.getAsJsonArray("goods");
        if (goods == null || goods.isEmpty()) return false;
        byKey.clear();
        all.clear();
        homeMade.clear();
        years.clear();
        Map<String, String> declaredHomeMade = new LinkedHashMap<>();
        if (root.has("referenceIncomeMultiple")) {
            referenceIncomeMultiple = root.get("referenceIncomeMultiple").getAsDouble();
        }
        targetShares.clear();
        JsonObject targets = root.getAsJsonObject("targetCalorieShares");
        if (targets != null) {
            for (String k : targets.keySet()) targetShares.put(k, targets.get(k).getAsDouble());
        }
        for (JsonElement e : goods) {
            JsonObject o = e.getAsJsonObject();
            Item it = new Item(
                    o.get("key").getAsString(),
                    o.get("label").getAsString(),
                    o.get("unit").getAsString(),
                    o.get("category").getAsString(),
                    // The file's prices are today's, as Good's table writes them; read at the founding's price level, the same product Good's
                    // constructor forms (EP1), so ConsumptionCheck's equality to Good holds to the bit, and the income multiple stays a pure ratio.
                    o.get("worldImportPrice").getAsDouble() * PriceLevel.FACTOR,
                    o.get("worldExportPrice").getAsDouble() * PriceLevel.FACTOR,
                    o.get("kcalPerUnit").getAsDouble(),
                    o.get("quality").getAsDouble(),
                    o.get("elasticity").getAsDouble(),
                    o.get("convenience").getAsDouble(),
                    o.get("referenceKg").getAsDouble());
            if (byKey.put(it.key(), it) != null) {
                System.out.println(FILE_NAME + ": two goods share the key " + it.key());
                return false;
            }
            all.add(it);
            if (o.has("homeMadeFrom")) {
                JsonElement h = o.get("homeMadeFrom");
                if (h.isJsonPrimitive() && h.getAsJsonPrimitive().isString()) declaredHomeMade.put(it.key(), h.getAsString());
                else System.out.println(FILE_NAME + ": " + it.key() + "'s homeMadeFrom is not a good's key; ignored");
            }
        }
        double c = 0;
        for (Item i : all) c += i.convenience();
        meanConvenience = all.isEmpty() ? 0 : c / all.size();
        readHomeMade(declaredHomeMade);
        readYears(root);
        return true;
    }

    /**
     * Keeps the homeMadeFrom lines that name a good the file holds and not the good itself; any other is ignored with a line in the log (a data
     * file must not stop a game). Run once every good is in, since a good may name one that comes after it.
     */
    private void readHomeMade(Map<String, String> declared) {
        for (Map.Entry<String, String> e : declared.entrySet()) {
            String made = e.getValue();
            if (made.equals(e.getKey())) {
                System.out.println(FILE_NAME + ": " + e.getKey() + " is homeMadeFrom itself; ignored");
            } else if (!byKey.containsKey(made)) {
                System.out.println(FILE_NAME + ": " + e.getKey() + " is homeMadeFrom " + made + ", which is no good in the file; ignored");
            } else {
                homeMade.put(e.getKey(), made);
            }
        }
        homeMadeIndex = new int[all.size()];
        for (int i = 0; i < all.size(); i++) {
            String made = homeMade.get(all.get(i).key());
            homeMadeIndex[i] = -1;
            if (made == null) continue;
            for (int j = 0; j < all.size(); j++) {
                if (all.get(j).key().equals(made)) { homeMadeIndex[i] = j; break; }
            }
        }
    }

    /**
     * Reads the optional "years" block into years. A missing block is no years; a malformed one (a field missing or not a number, years not
     * strictly ascending, a share or cap not finite or outside [0, 1]) logs one line and leaves no years, the goods already loaded; a bought key
     * naming no good in the file is ignored with a line of its own. A data file must not stop a game, and a city founded the 1900 way with no
     * years behaves as every other city does.
     */
    private void readYears(JsonObject root) {
        if (!root.has("years") || root.get("years").isJsonNull()) return;
        List<Year> read = new ArrayList<>();
        try {
            JsonArray array = root.getAsJsonArray("years");
            for (JsonElement e : array) {
                JsonObject o = e.getAsJsonObject();
                double yearValue = yearNumber(o, "year");
                if (yearValue != Math.rint(yearValue)) throw new IllegalArgumentException("year " + yearValue + " is not a whole year");
                int year = (int) yearValue;
                if (!read.isEmpty() && year <= read.get(read.size() - 1).year()) {
                    throw new IllegalArgumentException("year " + year + " does not follow " + read.get(read.size() - 1).year());
                }
                double lamp = yearShare(o, "lampShare", year);
                double coal = yearShare(o, "coalShare", year);
                double cars = yearShare(o, "carsPerHousehold", year);
                Map<String, Double> bought = new LinkedHashMap<>();
                if (o.has("bought")) {
                    JsonObject b = o.getAsJsonObject("bought");
                    for (String key : b.keySet()) {
                        double v = b.get(key).getAsDouble();
                        if (!(v >= 0 && v <= 1)) throw new IllegalArgumentException(year + "'s bought " + key + " " + v + " is not in [0, 1]");
                        if (!byKey.containsKey(key)) {
                            System.out.println(FILE_NAME + ": " + year + "'s bought names " + key + ", which is no good in the file; ignored");
                            continue;
                        }
                        bought.put(key, v);
                    }
                }
                read.add(new Year(year, lamp, coal, cars, bought,
                        yearText(o, "source", ""), o.has("estimate") && o.get("estimate").getAsBoolean(), yearText(o, "note", "")));
            }
        } catch (RuntimeException ex) {
            System.out.println(FILE_NAME + ": the years block is dropped, no basket follows the year: " + ex.getMessage());
            return;
        }
        years.addAll(read);
    }

    private static double yearNumber(JsonObject o, String name) {
        JsonElement v = o.get(name);
        if (v == null || !v.isJsonPrimitive() || !v.getAsJsonPrimitive().isNumber()) {
            throw new IllegalArgumentException(name + " is missing or not a number");
        }
        return v.getAsDouble();
    }

    private static double yearShare(JsonObject o, String name, int year) {
        double v = yearNumber(o, name);
        if (!(v >= 0 && v <= 1)) throw new IllegalArgumentException(year + "'s " + name + " " + v + " is not in [0, 1]");
        return v;
    }

    private static String yearText(JsonObject o, String name, String otherwise) {
        JsonElement v = o.get(name);
        return v != null && v.isJsonPrimitive() ? v.getAsString() : otherwise;
    }

    /**
     * The household's basket of a game month in a city founded the 1900 way: null when the file has no years, and null from the LAST knot's
     * January on, where the basket is today's model and nothing jumps. Before the first knot's January the first knot's figures; between two
     * knots every figure linear by month from one January to the next (ArrivalMix.januaryOf, the same rule as ArrivalMix.at()), a bought key
     * one knot does not name reading 1 there. No state: the same month always gives the same point.
     *
     * WHY (research-1900 5.4 item 5; R0e): a household's basket in 1900 was not today's, and growing into today's at the last knot is the
     * rule every year-following table here keeps. A city founded the default way never asks.
     */
    public YearPoint yearAt(int gameMonth) {
        if (years.isEmpty()) return null;
        Year first = years.get(0), last = years.get(years.size() - 1);
        if (gameMonth >= ArrivalMix.januaryOf(last.year())) return null;
        if (gameMonth < ArrivalMix.januaryOf(first.year())) return pointOf(first);
        for (int i = 1; i < years.size(); i++) {
            Year to = years.get(i);
            int end = ArrivalMix.januaryOf(to.year());
            if (gameMonth >= end) continue;
            Year from = years.get(i - 1);
            int start = ArrivalMix.januaryOf(from.year());
            double t = (gameMonth - start) / (double) (end - start);
            Map<String, Double> bought = new LinkedHashMap<>();
            for (String key : from.bought().keySet()) bought.put(key, 0.0);
            for (String key : to.bought().keySet()) bought.put(key, 0.0);
            for (Map.Entry<String, Double> e : bought.entrySet()) {
                double a = from.bought().getOrDefault(e.getKey(), 1.0);
                double b = to.bought().getOrDefault(e.getKey(), 1.0);
                e.setValue(a + (b - a) * t);
            }
            return new YearPoint(
                    from.lampShare() + (to.lampShare() - from.lampShare()) * t,
                    from.coalShare() + (to.coalShare() - from.coalShare()) * t,
                    from.carsPerHousehold() + (to.carsPerHousehold() - from.carsPerHousehold()) * t,
                    bought);
        }
        return null;   // unreachable: the month is under the last knot's January, so some knot ends after it
    }

    private static YearPoint pointOf(Year y) {
        return new YearPoint(y.lampShare(), y.coalShare(), y.carsPerHousehold(), y.bought());
    }

    /* ---------------------------- what it costs ---------------------------- */

    /**
     * The cheapest calorie in the file, per kcal.
     *
     * What a household with nothing eats, and the yardstick every income in
     * this class is measured against. A real famine diet is grain and oil and
     * this finds that on its own rather than being told it.
     */
    public double cheapestCalorie() {
        double best = Double.MAX_VALUE;
        for (Item i : all) {
            if (i.kcalPerUnit() <= 0 || i.worldImportPrice() <= 0) continue;
            best = Math.min(best, i.worldImportPrice() / i.kcalPerUnit());
        }
        return best == Double.MAX_VALUE ? 0 : best;
    }

    /**
     * What a month of bare survival costs one person, IN THE WORLD'S MONEY.
     *
     * THE NAME CARRIES THE CURRENCY BECAUSE THE FIRST VERSION DID NOT, and
     * that cost a measurement. consumption.json is priced at the world's
     * prices; a city's incomes are in the city's own money; the two are
     * separated by the exchange rate. ShadowBasket divided one straight into
     * the other and reported a city sitting at 117 times subsistence when the
     * true figure was 56 - the whole of the error was the rate, which stood at
     * 2.083 in the city being measured.
     *
     * The class header above says why the income multiple must be a pure
     * ratio, and it still must. What it did not say is that a ratio of two
     * monies is only pure when they are the SAME money. Use incomeMultiple()
     * below and the question cannot be got wrong.
     */
    public double subsistenceCostAtWorldPrices() {
        return cheapestCalorie() * KCAL_A_MONTH;
    }

    /**
     * What a month of bare survival costs one person, in THIS city's money.
     *
     * @param exchangeRate what a unit of the world's money costs here
     */
    public double subsistenceCost(double exchangeRate) {
        return subsistenceCostAtWorldPrices() * Math.max(0, exchangeRate);
    }

    /**
     * Where a household sits on Engel's curve.
     *
     * THE ONE PLACE THE TWO CURRENCIES MEET, which is what makes it the only
     * place they can be got wrong. Both sides are put into the world's money
     * before they are divided, and what comes back is the pure number the
     * curve wants - unmoved by a currency reform, by the price level, or by
     * the rate itself.
     *
     * @param incomePerHead take-home per person per month, in THIS city's money
     * @param exchangeRate  what a unit of the world's money costs here
     */
    public double incomeMultiple(double incomePerHead, double exchangeRate) {
        double subsistence = subsistenceCost(exchangeRate);
        return subsistence > 0 ? Math.max(0, incomePerHead) / subsistence : 0;
    }

    /* ----------------------------- the basket ----------------------------- */

    /**
     * How much of its income a household spends on food. Engel, with a floor.
     *
     * @param incomeMultiple income per head over what subsistence costs
     */
    public static double foodShare(double incomeMultiple) {
        if (!(incomeMultiple > 0)) return 1;
        double share = ENGEL_A - ENGEL_B * Math.log(incomeMultiple);
        return Math.max(ENGEL_MIN_SHARE, Math.min(1, share));
    }

    /**
     * What one person buys in a month, by key, in the file's units.
     *
     * BENNETT IN ONE LINE - each good's reference quantity moved by income to
     * the power of its own elasticity, so a negative one falls as the city
     * gets richer and a positive one climbs. The quantities are then scaled
     * together so the money adds up to what Engel says they spend, which is
     * what keeps the two laws from arguing.
     *
     * @param incomeMultiple income per head over what subsistence costs
     * @param timePressure   dependants per adult in a household that works;
     *                       zero for one that does not, however many children
     *                       it has - see the note at the top
     */
    public Map<String, Double> basket(double incomeMultiple, double timePressure) {
        return basket(incomeMultiple, timePressure, null);
    }

    /**
     * The same basket in the year of a city founded the 1900 way: what one person buys in a month when part of what today's household buys
     * ready made was made at home. With year == null it is exactly the two-argument basket (the default city's, to the bit).
     *
     * WHY (research-1900 5.4 item 5, "1900's has more raw staples", and section 4, "Home butter and bread: data, not a mechanism"; R0e): the
     * quantities are first the same as today (reference x income to the elasticity x the time lean); then for each good the year says a share b
     * of it is bought, the rest, q x (1 - b), is not bought but made at home - from the good's homeMadeFrom good (bread from grain), at the same
     * calories (the home-made part's kcal over the raw good's kcal a kilogram), or done without where the good names none. Every transfer is
     * worked from the quantities as first computed (two passes), so the order of the goods does not matter; the value, the calories and the
     * scale are then worked exactly as today, so a household bound by its appetite holds today's calories in more raw staple and one bound
     * by its money holds more calories for the same cost.
     *
     * @param year the year's figures (yearAt), or null for today's basket
     */
    public Map<String, Double> basket(double incomeMultiple, double timePressure, YearPoint year) {
        Map<String, Double> out = new LinkedHashMap<>();
        if (all.isEmpty() || !(incomeMultiple > 0)) return out;

        double x = Math.max(1e-9, incomeMultiple / referenceIncomeMultiple);
        double pressure = Math.max(0, timePressure);

        double[] qty = new double[all.size()];
        for (int i = 0; i < all.size(); i++) {
            Item it = all.get(i);
            double q = it.referenceKg() * Math.pow(x, it.elasticity());
            // ...and the time axis, which moves the mix and not the money.
            double lean = 1 + TIME_WEIGHT * pressure * (it.convenience() - meanConvenience);
            q *= Math.max(0, lean);
            qty[i] = q;
        }
        if (year != null) {
            double[] first = qty.clone();
            for (int i = 0; i < all.size(); i++) {
                double b = year.bought(all.get(i).key());
                if (!(b < 1)) continue;
                double home = first[i] * (1 - Math.max(0, b));
                qty[i] -= home;
                int r = homeMadeIndex[i];
                double rawKcal = r < 0 ? 0 : all.get(r).kcalPerUnit();
                if (r >= 0 && rawKcal > 0) qty[r] += home * all.get(i).kcalPerUnit() / rawKcal;
            }
        }
        double value = 0;
        for (int i = 0; i < all.size(); i++) value += qty[i] * all.get(i).worldImportPrice();
        if (value <= 0) return out;

        /*
         * THE LESSER OF WHAT THEY CAN AFFORD AND WHAT THEY CAN EAT. Which one
         * binds is the whole difference between a poor household and a rich
         * one, and it is why neither needs to be told what share to spend.
         */
        double spend = incomeMultiple * subsistenceCostAtWorldPrices() * foodShare(incomeMultiple);
        double byMoney = spend / value;

        double kcal = 0;
        for (int i = 0; i < all.size(); i++) kcal += qty[i] * all.get(i).kcalPerUnit();
        double byAppetite = kcal > 0 ? KCAL_A_MONTH * SATIATION / kcal : byMoney;

        double scale = Math.min(byMoney, byAppetite);
        for (int i = 0; i < all.size(); i++) out.put(all.get(i).key(), qty[i] * scale);
        return out;
    }

    /* --------------------------- reading a basket --------------------------- */

    public double costOf(Map<String, Double> basket) {
        double sum = 0;
        for (Map.Entry<String, Double> e : basket.entrySet()) {
            Item it = byKey.get(e.getKey());
            if (it != null) sum += e.getValue() * it.worldImportPrice();
        }
        return sum;
    }

    public double caloriesOf(Map<String, Double> basket) {
        double sum = 0;
        for (Map.Entry<String, Double> e : basket.entrySet()) {
            Item it = byKey.get(e.getKey());
            if (it != null) sum += e.getValue() * it.kcalPerUnit();
        }
        return sum;
    }

    /**
     * The average healthiness of what is in a basket, weighted by calories.
     *
     * Useful for one shelf and WRONG FOR A DIET, which is why it is not called
     * quality any more. Measured on the first draft: a famine diet of eighty
     * per cent grain scored .61 and a rich mixed diet .55, so the metric said
     * poor households ate better. Nothing is wrong with rice; what is wrong is
     * eating only rice, and an average cannot see that. See dietQuality().
     */
    public double itemQualityOf(Map<String, Double> basket) {
        double kcal = 0, weighted = 0;
        for (Map.Entry<String, Double> e : basket.entrySet()) {
            Item it = byKey.get(e.getKey());
            if (it == null) continue;
            double k = e.getValue() * it.kcalPerUnit();
            kcal += k;
            weighted += k * it.quality();
        }
        return kcal > 0 ? weighted / kcal : 0;
    }

    /**
     * How close a basket is to a balanced diet, 1 for on target and 0 for
     * nothing in common with it.
     *
     * HALF THE TOTAL ABSOLUTE DEVIATION from the target shares, which is the
     * standard shape for a diet-quality index and has the property the average
     * lacks: it punishes BOTH ends. A household too poor to eat anything but
     * starch scores badly, and so does one rich enough to take a third of its
     * calories as cake - which is true, and it means food has a health cost at
     * both ends of a city rather than only at the bottom.
     */
    public double dietQuality(Map<String, Double> basket) {
        if (targetShares.isEmpty()) return 0;
        double kcal = caloriesOf(basket);
        if (kcal <= 0) return 0;
        double deviation = 0;
        for (Map.Entry<String, Double> t : targetShares.entrySet()) {
            deviation += Math.abs(calorieShareOf(basket, t.getKey()) - t.getValue());
        }
        // ...and anything in a category the target does not name is all deviation.
        double named = 0;
        for (String cat : targetShares.keySet()) named += calorieShareOf(basket, cat);
        deviation += Math.max(0, 1 - named);
        return Math.max(0, 1 - deviation / 2);
    }

    /** The share of a basket's calories that comes from one category. Bennett, measured. */
    public double calorieShareOf(Map<String, Double> basket, String category) {
        double kcal = 0, inCategory = 0;
        for (Map.Entry<String, Double> e : basket.entrySet()) {
            Item it = byKey.get(e.getKey());
            if (it == null) continue;
            double k = e.getValue() * it.kcalPerUnit();
            kcal += k;
            if (it.category().equals(category)) inCategory += k;
        }
        return kcal > 0 ? inCategory / kcal : 0;
    }

    /**
     * What a basket is worth to each good, as shares of one.
     *
     * The price index's weights, and the reason they come from here rather
     * than from watching a month's tills: a basket measured at a founding city
     * came out four per cent food and ninety-six per cent rent, and a good the
     * city has not stocked yet would weigh nothing for ever. This says what
     * THIS city's households eat at THIS city's incomes, which is the half of
     * "measured, not invented" that was worth keeping.
     */
    public Map<String, Double> valueShares(Map<String, Double> basket) {
        Map<String, Double> out = new LinkedHashMap<>();
        double total = costOf(basket);
        if (total <= 0) return out;
        for (Map.Entry<String, Double> e : basket.entrySet()) {
            Item it = byKey.get(e.getKey());
            if (it != null) out.put(e.getKey(), e.getValue() * it.worldImportPrice() / total);
        }
        return out;
    }

    /* ------------------------- who a household is ------------------------- */

    /** One good by its key, or null if the file has no such line. */
    public Item byKey(String key) { return key == null ? null : byKey.get(key); }

    /**
     * Dependants per adult, for a household that works - and zero for one that
     * does not, which is the whole point of the time axis.
     *
     * A LIMITATION WORTH WRITING DOWN: this model has one pay tier per
     * household and FamilyStructure.earners() is every adult in it, so a couple
     * where one partner works and one stays home cannot be represented. Both
     * adults are earners or neither is. When that changes, a stay-at-home adult
     * should take the pressure back off, and this is where it would go.
     */
    public static double timePressure(FamilyStructure shape) {
        if (shape == null || shape.earners() <= 0) return 0;
        return (double) shape.dependants() / shape.earners();
    }
}

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
 * rule this is meant to end is the one UserInterface.historyValues() broke on
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

    private final Map<String, Item> byKey = new LinkedHashMap<>();
    private final Map<String, Double> targetShares = new LinkedHashMap<>();
    private final List<Item> all = new ArrayList<>();
    private double referenceIncomeMultiple = 8.0;
    private String source = "none";
    private double meanConvenience;

    public String getSource()           { return source; }
    public List<Item> items()           { return Collections.unmodifiableList(all); }
    public Item item(String key)        { return key == null ? null : byKey.get(key); }
    public boolean isLoaded()           { return !all.isEmpty(); }
    public double referenceMultiple()   { return referenceIncomeMultiple; }

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
                    o.get("worldImportPrice").getAsDouble(),
                    o.get("worldExportPrice").getAsDouble(),
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
        }
        double c = 0;
        for (Item i : all) c += i.convenience();
        meanConvenience = all.isEmpty() ? 0 : c / all.size();
        return true;
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
        Map<String, Double> out = new LinkedHashMap<>();
        if (all.isEmpty() || !(incomeMultiple > 0)) return out;

        double x = Math.max(1e-9, incomeMultiple / referenceIncomeMultiple);
        double pressure = Math.max(0, timePressure);

        double[] qty = new double[all.size()];
        double value = 0;
        for (int i = 0; i < all.size(); i++) {
            Item it = all.get(i);
            double q = it.referenceKg() * Math.pow(x, it.elasticity());
            // ...and the time axis, which moves the mix and not the money.
            double lean = 1 + TIME_WEIGHT * pressure * (it.convenience() - meanConvenience);
            q *= Math.max(0, lean);
            qty[i] = q;
            value += q * it.worldImportPrice();
        }
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
    /** One good by its key, or null if the file has no such line. */
    public Item byKey(String key) { return key == null ? null : byKey.get(key); }

    public static double timePressure(FamilyStructure shape) {
        if (shape == null || shape.earners() <= 0) return 0;
        return (double) shape.dependants() / shape.earners();
    }
}

package ham.citybuildersim;

import java.io.StringReader;
import java.util.Map;

/**
 * Verifies the consumption model against the two laws it is shaped to obey,
 * and guards the data file against the Java.
 *
 * WHY A HARNESS FOR SOMETHING NOTHING EATS YET. Every number in here will be
 * load-bearing the month households start buying these goods, and hunger runs
 * into sickness and sickness runs into mortality - so a wrong curve is not a
 * wrong figure on a screen, it is a body count. The model is proved first and
 * connected second, which is the only order that lets the ensemble say
 * anything when it IS connected.
 *
 * THREE OF THESE ASSERTIONS EXIST BECAUSE THE PROBE FOUND THE BUG FIRST, and
 * each is written so the bug cannot come back: calories that fell as a
 * household got richer, a basket that came back empty at five hundred times
 * subsistence, and a quality score that said a famine diet of eighty per cent
 * starch was better than a mixed one.
 */
public class ConsumptionCheck {

    static int fails = 0;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-72s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void check(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) fails++;
        System.out.printf("%-72s %s  %,.6f vs %,.6f%n", label, ok ? "OK  " : "FAIL", actual, expected);
    }

    static final double[] LADDER = { 1, 2, 5, 10, 20, 27, 50, 90, 200, 500, 2_000 };

    public static void main(String[] args) {

        Consumption c = new Consumption();
        assertTrue("the file loads", c.load());
        assertTrue("...and has goods in it", c.items().size() >= 13);
        System.out.println("   " + c.items().size() + " goods from " + c.getSource()
                + "; subsistence costs " + String.format("$%.2f", c.subsistenceCostAtWorldPrices() * 1000)
                + " a person a month at the world's prices");

        /* ================= 1. the file itself ================= */
        System.out.println("\n--- the data says what the model needs it to say ---");

        boolean pricesPositive = true, kcalSane = true, elasticitySane = true,
                qualitySane = true, convenienceSane = true, referenceSane = true,
                exportUnderImport = true;
        for (Consumption.Item i : c.items()) {
            if (!(i.worldImportPrice() > 0) || !(i.worldExportPrice() > 0)) pricesPositive = false;
            if (i.worldExportPrice() >= i.worldImportPrice()) exportUnderImport = false;
            if (!(i.kcalPerUnit() > 0)) kcalSane = false;
            if (i.elasticity() < -1 || i.elasticity() > 2) elasticitySane = false;
            if (i.quality() < 0 || i.quality() > 1) qualitySane = false;
            if (i.convenience() < 0 || i.convenience() > 1) convenienceSane = false;
            if (!(i.referenceKg() > 0)) referenceSane = false;
        }
        assertTrue("every good has a delivered price and a gate price", pricesPositive);
        assertTrue("...and the gate is under the door, as it is for every other good", exportUnderImport);
        assertTrue("every good carries calories, so subsistence can be met out of it", kcalSane);
        assertTrue("every elasticity is inside a band a demand system can mean", elasticitySane);
        assertTrue("...and quality and convenience are shares of one", qualitySane && convenienceSane);
        assertTrue("every good says what a person eats of it at the reference", referenceSane);

        double targets = 0;
        for (double v : c.targetShares().values()) targets += v;
        check("a balanced diet's shares are a whole diet", targets, 1.0, 1e-9);
        boolean everyCategoryTargeted = true;
        for (String cat : c.categories()) if (!c.targetShares().containsKey(cat)) everyCategoryTargeted = false;
        assertTrue("...and every category a good declares has a target", everyCategoryTargeted);

        /*
         * AT LEAST ONE STAPLE IS AN INFERIOR GOOD. Bennett's law is not a
         * preference, it is the claim that staples fall as income rises, and a
         * file where every elasticity is positive cannot produce it however
         * good the arithmetic above it is.
         */
        boolean anInferiorGood = false;
        for (Consumption.Item i : c.items()) if (i.elasticity() < 0) anInferiorGood = true;
        assertTrue("something in the file is an inferior good, or Bennett cannot happen", anInferiorGood);

        /* ================= 2. Engel, both halves ================= */
        System.out.println("\n--- Engel: the share falls, the amount rises ---");

        double lastShare = Double.MAX_VALUE, lastSpend = -1, lastKcal = -1;
        boolean shareFalls = true, spendRises = true, kcalRises = true, everEmpty = false;
        for (double x : LADDER) {
            Map<String, Double> b = c.basket(x, 0);
            double share = Consumption.foodShare(x);
            double spend = c.costOf(b);
            double kcal = c.caloriesOf(b);
            if (share > lastShare + 1e-12) shareFalls = false;
            if (spend < lastSpend - 1e-12) spendRises = false;
            if (kcal < lastKcal - 1e-9) kcalRises = false;
            if (b.isEmpty() || kcal <= 0) everEmpty = true;
            lastShare = share; lastSpend = spend; lastKcal = kcal;
        }
        assertTrue("the food SHARE never rises with income", shareFalls);
        assertTrue("...while the AMOUNT spent never falls with it", spendRises);
        check("a household at bare subsistence spends all of it on food",
                Consumption.foodShare(1), 1.0, 1e-9);
        assertTrue("...and a household at fifty times subsistence spends a fraction",
                Consumption.foodShare(50) < .5);

        /*
         * THE BUG THE FIRST DRAFT HAD, both halves of it. A typed floor under
         * the share made the spend collapse faster than income grew, so
         * CALORIES FELL as a household got richer - 426 a day at five times
         * subsistence and 194 at twelve. And past the point the curve went
         * negative the basket came back EMPTY: a household rich enough to buy
         * the city, starving. Satiation replaced the floor and the share is
         * held where the curve stops describing anything.
         */
        assertTrue("NOBODY EATS LESS FOR BEING RICHER, at any rung of the ladder", kcalRises);
        assertTrue("...and no household anywhere comes back with an empty basket", !everEmpty);

        /* ================= 3. Bennett ================= */
        System.out.println("\n--- Bennett: off the starch and onto the protein ---");

        Map<String, Double> poor = c.basket(2, 0);
        Map<String, Double> middling = c.basket(27, 0);
        Map<String, Double> rich = c.basket(200, 0);

        double staplePoor = c.calorieShareOf(poor, "FOOD_STAPLE");
        double stapleRich = c.calorieShareOf(rich, "FOOD_STAPLE");
        double proteinPoor = c.calorieShareOf(poor, "FOOD_PROTEIN");
        double proteinRich = c.calorieShareOf(rich, "FOOD_PROTEIN");
        System.out.printf("   staples %.0f%% -> %.0f%% of calories; protein %.0f%% -> %.0f%%%n",
                staplePoor * 100, stapleRich * 100, proteinPoor * 100, proteinRich * 100);

        assertTrue("the calorie share from staples FALLS as a city gets richer", stapleRich < staplePoor);
        assertTrue("...and the share from protein rises", proteinRich > proteinPoor);
        assertTrue("...and from produce too",
                c.calorieShareOf(rich, "FOOD_PRODUCE") > c.calorieShareOf(poor, "FOOD_PRODUCE"));
        assertTrue("a poor household takes most of its calories from starch", staplePoor > .6);

        /* ================= 4. satiation ================= */
        System.out.println("\n--- and nobody eats for ever ---");

        double need = Consumption.KCAL_A_MONTH;
        boolean neverOverfed = true;
        for (double x : LADDER) {
            if (c.caloriesOf(c.basket(x, 0)) > need * Consumption.SATIATION + 1e-6) neverOverfed = false;
        }
        assertTrue("no household eats past what satiation allows, however rich", neverOverfed);
        check("...and a rich one eats exactly that", c.caloriesOf(c.basket(2_000, 0)),
                need * Consumption.SATIATION, 1e-6);
        assertTrue("a household at subsistence is hungry, which is the point of the floor",
                c.caloriesOf(c.basket(1, 0)) < need);

        /* ================= 5. the time axis ================= */
        System.out.println("\n--- and the time axis, which is not income ---");

        Map<String, Double> unhurried = c.basket(27, 0);
        Map<String, Double> hurried = c.basket(27, 2);
        double convUnhurried = c.calorieShareOf(unhurried, "FOOD_PREPARED")
                + c.calorieShareOf(unhurried, "FOOD_TREAT");
        double convHurried = c.calorieShareOf(hurried, "FOOD_PREPARED")
                + c.calorieShareOf(hurried, "FOOD_TREAT");
        assertTrue("a household with no time takes more of its calories ready-made",
                convHurried > convUnhurried);
        check("...and it costs them the same money, because this axis is not money",
                c.costOf(hurried), c.costOf(unhurried), 1e-9);
        assertTrue("...but it costs them diet quality", c.dietQuality(hurried) < c.dietQuality(unhurried));

        /*
         * AND IT IS THE HOUSEHOLDS THAT WORK. An out-of-work parent and a
         * working parent with the same children and the same money do not buy
         * the same food, because what one is short of is money and what the
         * other is short of is afternoons. If this ever reads the same for
         * both, the axis has collapsed into income and can be deleted.
         */
        check("a working single parent carries one dependant an adult",
                Consumption.timePressure(FamilyStructure.SINGLE_PARENT), 1, 1e-9);
        check("a large family, two adults and four others, carries two",
                Consumption.timePressure(FamilyStructure.LARGE_FAMILY), 2, 1e-9);
        check("a couple with nobody to feed carries none",
                Consumption.timePressure(FamilyStructure.COUPLE), 0, 1e-9);
        check("and a retired household has all the time in the world",
                Consumption.timePressure(FamilyStructure.SENIOR_ALONE), 0, 1e-9);

        /* ================= 6. diet quality is balance ================= */
        System.out.println("\n--- a diet is judged on balance, not on an average ---");

        double qPoor = c.dietQuality(poor);
        double qMiddle = c.dietQuality(middling);
        double qRich = c.dietQuality(rich);
        System.out.printf("   diet quality: poor %.2f, middling %.2f, rich %.2f%n", qPoor, qMiddle, qRich);

        /*
         * THE SECOND BUG THE PROBE FOUND. Quality began as the calorie-weighted
         * average of what was in the basket, and by that measure the famine
         * diet scored .61 against a mixed diet's .55 - because there is nothing
         * wrong with rice, and an average cannot see that a diet of ONLY rice
         * is the problem. Distance from a balanced target punishes both ends.
         */
        assertTrue("a diet of mostly starch scores badly, however wholesome the starch",
                qPoor < qMiddle);
        assertTrue("...and so does one with a third of its calories as treats", qRich < qMiddle);
        assertTrue("the middle of a city eats best", qMiddle > .8);

        /* ================= 7. a currency reform changes nothing ================= */
        System.out.println("\n--- and a hundred to one reform moves none of it ---");

        /*
         * THE PROPERTY THIS MODEL WAS BUILT AROUND. Engel's curve is a
         * logarithm of income, and a logarithm of a MONEY figure is a money
         * constant wearing a function's clothes - this codebase has found
         * twenty bugs of that family. Income is divided by what subsistence
         * costs instead, so the argument is a pure number.
         *
         * Asserted by feeding the model the same file with every price
         * multiplied by a hundred, which is what a reform does. The QUANTITIES
         * must not move by one gram, and the cost must move by exactly a
         * hundred - and nothing anywhere had to be seeded for that to be true.
         */
        Consumption lopped = new Consumption();
        assertTrue("fixture: the same file at a hundred times the prices",
                lopped.loadFrom(new StringReader(scaledFile(c, 100))));

        boolean sameGrams = true;
        Map<String, Double> before = c.basket(27, 1);
        Map<String, Double> after = lopped.basket(27, 1);
        for (String k : before.keySet()) {
            double a = before.get(k), b = after.getOrDefault(k, 0.0);
            if (Math.abs(a - b) > 1e-9 * Math.max(1, Math.abs(a))) sameGrams = false;
        }
        assertTrue("every household buys exactly what it bought, to the gram", sameGrams);
        check("...and the bill is a hundred times what it was, and nothing else",
                lopped.costOf(after), c.costOf(before) * 100, 1e-6 * c.costOf(before) * 100);
        check("...subsistence costing a hundred times as much too",
                lopped.subsistenceCostAtWorldPrices(), c.subsistenceCostAtWorldPrices() * 100, 1e-12);
        check("...and the diet is the same diet", lopped.dietQuality(after), c.dietQuality(before), 1e-9);

        /* ================= 8. the OTHER currency ================= */
        /*
         * A RATIO OF TWO MONIES IS PURE ONLY WHEN THEY ARE THE SAME MONEY.
         *
         * Section 7 above proves the model survives a currency reform. It did,
         * and it still shipped a currency bug - because the reform it tested
         * was of the FILE's prices, and the bug was at the boundary where the
         * CITY's money meets them. consumption.json is priced at the world's
         * prices; a household's take-home is in the city's own money; the
         * exchange rate is the whole of the difference. ShadowBasket divided
         * one straight into the other and reported a played city sitting at
         * 117 times subsistence when the true figure was 56.
         *
         * A harness that guards one end of a conversion guards one end of it.
         */
        System.out.println("\n--- the city's money is not the world's money ---");

        double homeIncome = 40.0;                             // a head, a month, in the city's money
        double cheapCity  = c.incomeMultiple(homeIncome, 1.0);

        // The same real household in a city where the world costs twice as
        // much in local money - so it is paid twice as much of it.
        check("the same household is the same household at twice the rate",
                c.incomeMultiple(homeIncome * 2, 2.0), cheapCity, 1e-12);

        check("...and the same wage at twice the rate is half the household",
                c.incomeMultiple(homeIncome, 2.0), cheapCity / 2, 1e-12);

        /*
         * And the bug itself as a number rather than a story: at the rate the
         * measured city actually carried, leaving it out overstates every
         * income in that city by exactly the rate.
         */
        double measuredRate = 2.083;
        double ignoringTheRate = homeIncome / c.subsistenceCostAtWorldPrices();
        check("leaving the rate out overstates the city by exactly the rate",
                ignoringTheRate / c.incomeMultiple(homeIncome, measuredRate), measuredRate, 1e-9);
        assertTrue("fixture: which is a doubling, not a rounding",
                ignoringTheRate > c.incomeMultiple(homeIncome, measuredRate) * 2);

        /* ================= 9. the file and the enum ================= */
        /*
         * THE ONE DUPLICATION IN THIS MODEL, GUARDED RATHER THAN TRUSTED.
         *
         * consumption.json owns what a good costs the world. Good owns the
         * same number again, because Markets is built on the enum and an enum
         * cannot read a file at class-init time. Two copies of a number is how
         * a balance pass edits one of them and the city quietly prices its
         * imports off the other for a year.
         *
         * So every one of the thirteen is checked against its constant here,
         * to the cent, and a good in the file with no Good behind it is a
         * failure rather than a line that silently never gets imported.
         */
        System.out.println("\n--- the file and the enum are the same numbers ---");

        int matched = 0;
        for (Consumption.Item it : c.items()) {
            Good g = Good.byName(it.key());
            assertTrue("\"" + it.key() + "\" is a Good the market can trade", g != null);
            if (g == null) continue;
            matched++;
            check("   " + it.key() + " costs the world what the file says",
                    g.worldImportPrice(), it.worldImportPrice(), 1e-12);
            check("   ...and sells to it for what the file says",
                    g.worldExportPrice(), it.worldExportPrice(), 1e-12);
            assertTrue("   ...and is importable, because nothing here is made at home",
                    g.importable());
        }
        check("every good in the file has a good in the market", matched, c.items().size(), 0);

        System.out.println();
        System.out.println(fails == 0 ? "A household eats what it can afford, and what it has time for."
                : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** The file as it stands, with every price multiplied - a currency reform, on paper. */
    static String scaledFile(Consumption c, double by) {
        StringBuilder s = new StringBuilder("{\"referenceIncomeMultiple\":")
                .append(c.referenceMultiple()).append(",\"targetCalorieShares\":{");
        boolean first = true;
        for (Map.Entry<String, Double> t : c.targetShares().entrySet()) {
            if (!first) s.append(',');
            s.append('"').append(t.getKey()).append("\":").append(t.getValue());
            first = false;
        }
        s.append("},\"goods\":[");
        first = true;
        for (Consumption.Item i : c.items()) {
            if (!first) s.append(',');
            s.append(String.format("{\"key\":\"%s\",\"label\":\"%s\",\"unit\":\"%s\",\"category\":\"%s\","
                            + "\"worldImportPrice\":%.12f,\"worldExportPrice\":%.12f,\"kcalPerUnit\":%s,"
                            + "\"quality\":%s,\"elasticity\":%s,\"convenience\":%s,\"referenceKg\":%s}",
                    i.key(), i.label(), i.unit(), i.category(),
                    i.worldImportPrice() * by, i.worldExportPrice() * by, i.kcalPerUnit(),
                    i.quality(), i.elasticity(), i.convenience(), i.referenceKg()));
            first = false;
        }
        return s.append("]}").toString();
    }
}

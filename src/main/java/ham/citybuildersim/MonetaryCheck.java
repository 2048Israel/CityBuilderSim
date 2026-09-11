package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Money: what a basket costs, what the world charges, and what the rate does.
 *
 * WHAT IS ACTUALLY BEING ASKED
 *
 *   1. Does the price index measure what households BUY, on a basket fixed at a
 *      base period? A CPI that re-weights as spending shifts shows no inflation
 *      for a family that switched to cheaper food while eating worse.
 *
 *   2. Do prices RATION? A shop that can meet a fifth of demand and charges
 *      cost-plus is not a shop, it is a queue - and a model with no demand-pull
 *      channel gives a policy rate nothing to cool.
 *
 *   3. Is the world a real place? Its own inflation is the one price shock the
 *      player cannot cause and cannot stop.
 *
 *   4. And does the rate DO anything - to credit, to the currency, and to the
 *      city that has to live with it?
 */
public class MonetaryCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-58s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) {
            fails++;
            out.printf("%-58s FAIL  %,.6f != %,.6f%n", label, actual, expected);
        } else {
            out.printf("%-58s OK%n", label);
        }
    }

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        /* ================= 1. the basket ================= */
        out.println("--- a fixed basket, priced repeatedly ---");

        PriceIndex px = new PriceIndex();
        for (int m = 0; m < PriceIndex.SETTLING_MONTHS - 1; m++) px.takeMonth(.30, .12, 60, 40);
        assertTrue("a young city has no basket yet", !px.isBased());
        px.takeMonth(.30, .12, 60, 40);
        assertTrue("...and a settled one does", px.isBased());
        close("the basket is what they actually spent", px.getFoodWeight(), .6, 1e-9);
        close("...and it starts at one", px.getIndex(), 1.0, 1e-9);

        /*
         * FIXED, NOT RE-WEIGHTED. Doubling the food price while households
         * respond by spending everything on rent must still show food inflation
         * - a basket that follows the spending shows none, and tells a family
         * eating worse that nothing has happened.
         */
        px.takeMonth(.60, .12, 1, 99);
        close("doubling food moves the index by the food weight",
                px.getIndex(), 1.6, 1e-9);
        close("...and the weights did not move to hide it", px.getFoodWeight(), .6, 1e-9);

        /* AND A YEAR-ON-YEAR RATE NEEDS A YEAR. */
        PriceIndex young = new PriceIndex();
        for (int m = 0; m < PriceIndex.SETTLING_MONTHS + 3; m++) young.takeMonth(.30, .12, 60, 40);
        assertTrue("a rate is not quoted before there is a year of readings",
                !young.hasRate() && young.inflation() == 0);

        /* ================= 2. prices ration ================= */
        out.println("\n--- and a shortage is priced ---");

        ham.citybuildersim.sectors.Retail full = new ham.citybuildersim.sectors.Retail();
        full.repriceShelf(100, .20, 0, .20, 100, 100);
        close("shelves that meet demand charge cost-plus",
                full.getScarcityMultiple(), 1.0, 1e-9);

        ham.citybuildersim.sectors.Retail shortage = new ham.citybuildersim.sectors.Retail();
        shortage.repriceShelf(100, .20, 0, .20, 100, 0);
        out.printf("   nothing delivered: a %.2fx mark-up%n", shortage.getScarcityMultiple());
        close("a total shortage charges the ceiling",
                shortage.getScarcityMultiple(), ham.citybuildersim.sectors.Retail.MAX_SCARCITY_MULTIPLE, 1e-9);

        ham.citybuildersim.sectors.Retail half = new ham.citybuildersim.sectors.Retail();
        half.repriceShelf(100, .20, 0, .20, 100, 50);
        assertTrue("...and half a shortage is between the two",
                half.getScarcityMultiple() > 1
                        && half.getScarcityMultiple() < ham.citybuildersim.sectors.Retail.MAX_SCARCITY_MULTIPLE);

        /* ================= 3. the world is a real place ================= */
        out.println("\n--- and the world has its own inflation ---");

        WorldEconomy w = new WorldEconomy();
        double lowest = 9, highest = -9;
        for (int m = 1; m <= 2_400; m++) {
            w.advanceMonth(m);
            lowest = Math.min(lowest, w.getInflation());
            highest = Math.max(highest, w.getInflation());
        }
        out.printf("   two centuries: inflation ranged %.1f%%-%.1f%%, prices ended at %.2fx%n",
                lowest * 100, highest * 100, w.getPriceLevel());
        assertTrue("it stays inside its band",
                lowest >= WorldEconomy.MIN_INFLATION - 1e-9
                        && highest <= WorldEconomy.MAX_INFLATION + 1e-9);
        assertTrue("...and it actually moves around in it", highest - lowest > .01);
        assertTrue("...and the price level stays a usable number",
                w.getPriceLevel() > .1 && w.getPriceLevel() < 100);

        /*
         * DETERMINISTIC. ForeignCheck asserts two runs of one city come out
         * identical, and a world with real randomness in it would end that.
         */
        WorldEconomy twin = new WorldEconomy();
        for (int m = 1; m <= 2_400; m++) twin.advanceMonth(m);
        close("two worlds from the same seed agree exactly",
                twin.getPriceLevel(), w.getPriceLevel(), 1e-12);

        WorldEconomy held = new WorldEconomy();
        held.pin();
        for (int m = 1; m <= 240; m++) held.advanceMonth(m);
        close("a pinned world does not move", held.getPriceLevel(), 1.0, 1e-12);

        /* ================= 4. and the rate does something ================= */
        out.println("\n--- and the policy rate is a lever ---");

        DebtManager market = new DebtManager();
        close("it opens at neutral", market.getPolicyRate(), DebtManager.NEUTRAL_RATE, 1e-9);

        market.setPolicyRate(.10);
        close("the dial moves it", market.getPolicyRate(), .10, 1e-9);
        market.setPolicyRate(-1);
        close("...and will not go below zero", market.getPolicyRate(),
                DebtManager.MIN_POLICY_RATE, 1e-9);
        market.setPolicyRate(99);
        close("...nor past where it stops transmitting",
                market.getPolicyRate(), DebtManager.MAX_POLICY_RATE, 1e-9);

        /*
         * THE TAYLOR PRINCIPLE, which is the whole content of the rule: a point
         * of extra inflation must be met with MORE than a point of extra rate,
         * or money is cheaper in real terms than it was and the rise feeds what
         * it was meant to stop.
         */
        double at2 = market.advisedPolicyRate(.02);
        double at5 = market.advisedPolicyRate(.05);
        out.printf("   the rule: %.2f%% at 2%% inflation, %.2f%% at 5%%%n",
                at2 * 100, at5 * 100);
        close("on target it advises neutral", at2, DebtManager.NEUTRAL_RATE, 1e-9);
        assertTrue("above target it advises more than one for one",
                (at5 - at2) > (.05 - .02));
        assertTrue("...and below target, less", market.advisedPolicyRate(0) < at2);
        assertTrue("...and says why in words about inflation",
                market.adviceReason(.05).contains("target"));

        /* ...AND IT REACHES THE CURRENCY. */
        ForeignAccounts cheap = new ForeignAccounts();
        cheap.setRateDifferential(-.04);
        ForeignAccounts dear = new ForeignAccounts();
        dear.setRateDifferential(.04);

        out.printf("   four points under the world pulls %+.2f, four points over %+.2f%n",
                cheap.ratePressure(), dear.ratePressure());
        assertTrue("paying under the world weakens the currency", cheap.ratePressure() > 0);
        assertTrue("...and paying over it supports the currency", dear.ratePressure() < 0);
        assertTrue("...and neither can swamp the trade balance on its own",
                Math.abs(dear.ratePressure()) <= ForeignAccounts.MAX_RATE_PRESSURE + 1e-9);

        /* ================= 5. in a city, and across a reload ================= */
        out.println("\n--- and all of it survives a city and a reload ---");

        Path root = Files.createTempDirectory("monetary");
        Game city = new Game(new GameFiles(root.resolve("data"), root.resolve("no-legacy")));

        System.setOut(quiet);
        try {
            city.run();
            city.getLandManager().setOwnedSqFt(30_000_000);
            city.buildStack(template(city, "House"), 400, false);
            city.buildStack(template(city, "Convenience Store"), 6, false);
            city.buildStack(template(city, "Construction Depot"), 4, false);
            city.buildStack(template(city, "Coal Power Plant"), 1, false);
            city.buildStack(template(city, "Water Treatment Plant"), 1, false);
            city.buildStack(template(city, "Textile Mill"), 2, false);
            city.buildStack(template(city, "Paved Road"), 30, false);
            city.simulateMonths(120);
            city.getDebtManager().setPolicyRate(.075);
            city.simulateMonths(12);
        } finally {
            System.setOut(out);
        }

        PriceIndex lived = city.getPriceIndex();
        out.printf("   ten years: index %.3f, world %.3f, rate %.4f, parity %.4f, policy %.2f%%%n",
                lived.getIndex(), city.getWorldEconomy().getPriceLevel(),
                city.getForeignAccounts().getRate(), city.getForeignAccounts().getParity(),
                city.getDebtManager().getPolicyRate() * 100);

        assertTrue("the city based its basket", lived.isBased());
        assertTrue("...and the world's prices moved",
                Math.abs(city.getWorldEconomy().getPriceLevel() - 1) > .01);
        assertTrue("...and the currency is not against a bound",
                city.getForeignAccounts().getRate() > ForeignAccounts.MIN_RATE * 2
                        && city.getForeignAccounts().getRate() < ForeignAccounts.MAX_RATE / 2);

        System.setOut(quiet);
        Game back;
        try {
            city.saveGame(6, "the monetary city");
            back = new Game(new GameFiles(root.resolve("data"), root.resolve("no-legacy")));
            back.run();
            back.loadGameSave(6);
        } finally {
            System.setOut(out);
        }

        close("the policy rate reloads", back.getDebtManager().getPolicyRate(),
                city.getDebtManager().getPolicyRate(), 1e-9);
        close("...and the world's price level", back.getWorldEconomy().getPriceLevel(),
                city.getWorldEconomy().getPriceLevel(), 1e-9);
        close("...and the price index", back.getPriceIndex().getIndex(),
                lived.getIndex(), 1e-9);
        close("...and the basket it is measured on",
                back.getPriceIndex().getFoodWeight(), lived.getFoodWeight(), 1e-9);

        /*
         * AND THE YEAR OF READINGS BEHIND THE RATE. The index can be restruck
         * from today's prices; a twelve-month inflation rate cannot, and a
         * reloaded city that reported 0% for a year would advise the wrong
         * policy rate for a year.
         */
        close("...and the year of history the inflation rate is struck from",
                back.getPriceIndex().inflation(), lived.inflation(), 1e-9);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    static BuildingsTemplate template(Game game, String name) {
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException("no template named " + name);
    }
}

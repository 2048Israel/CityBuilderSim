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
 *      city that has to live with it? And since 0.7.4, does the rule aim
 *      where the player's target says, and only move its intercept?
 *
 *   5. Does all of it hold in a real city, and survive a reload?
 *
 *   6. And does INFLATION FALL WITH THE RATE? One founding held at 3%, 10%,
 *      20% and 40% from month 25 to 60: each higher dial's inflation no
 *      higher than the lower one's, within the noise a month's delay in the
 *      hand makes, and the 3% row at least a point above the 40% row. A
 *      measurement from 0.6.11 to 0.7.2, the baseline 7.0 had to turn; an
 *      assertion since 0.7.3, with the households' saving answering the real
 *      deposit rate - and the columns that say which channel carried it.
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
        int mo = 0;
        for (int m = 0; m < PriceIndex.SETTLING_MONTHS - 1; m++) px.takeMonth(.30, .12, 60, 40, ++mo);
        assertTrue("a young city has no basket yet", !px.isBased());
        px.takeMonth(.30, .12, 60, 40, ++mo);
        assertTrue("...and a settled one does", px.isBased());
        close("the basket is what they actually spent", px.getFoodWeight(), .6, 1e-9);
        close("...and it starts at one", px.getIndex(), 1.0, 1e-9);

        /*
         * FIXED, NOT RE-WEIGHTED. Doubling the food price while households
         * respond by spending everything on rent must still show food inflation
         * - a basket that follows the spending shows none, and tells a family
         * eating worse that nothing has happened.
         */
        int basedAt = mo;
        px.takeMonth(.60, .12, 1, 99, ++mo);
        close("doubling food moves the index by the food weight",
                px.getIndex(), 1.6, 1e-9);
        close("...and the weights did not move to hide it", px.getFoodWeight(), .6, 1e-9);

        /* ---------------- the high and low water marks ----------------
         *
         * Jerus, 2026-09-14. Every price figure this project quotes is the
         * level on the last month measured, and the level does not sit still -
         * sixteen seeds that FINISH between 0.84 and 1.26 peak at a median of
         * 1.62 and as high as 3.78 on the way. These two readings are the only
         * part of that path that survives not having watched it.
         */
        close("the peak is the dearest month, not the last one", px.getPeak(), 1.6, 1e-9);
        close("...and it remembers which month that was", px.getPeakMonth(), mo, 0);
        close("the trough opens where the basket was based", px.getTrough(), 1.0, 1e-9);
        close("...on the month it was based", px.getTroughMonth(), basedAt, 0);

        // Back down: the peak must STAY, which is the whole point of a mark.
        px.takeMonth(.15, .12, 60, 40, ++mo);
        close("cheaper food moves the index", px.getIndex(), .7, 1e-9);
        close("...and the peak does not come down with it", px.getPeak(), 1.6, 1e-9);
        close("...while the trough follows it down", px.getTrough(), .7, 1e-9);
        close("...and stamps the new month", px.getTroughMonth(), mo, 0);
        close("the swing is peak over trough", px.swing(), 1.6 / .7, 1e-9);

        /*
         * AND THEY SURVIVE A SAVE, because they are a record rather than a
         * derivation - nothing in the state a month ended in can reconstruct
         * what the level was three hundred months ago.
         */
        PriceIndex reloaded = new PriceIndex();
        reloaded.restore(px.toSaveArray());
        close("the peak reloads", reloaded.getPeak(), px.getPeak(), 1e-9);
        close("...with its month", reloaded.getPeakMonth(), px.getPeakMonth(), 0);
        close("the trough reloads", reloaded.getTrough(), px.getTrough(), 1e-9);
        close("...with its month", reloaded.getTroughMonth(), px.getTroughMonth(), 0);

        /*
         * A SAVE FROM BEFORE THE MARKS EXISTED has no tail, and the honest
         * answer is today's level rather than a record it never kept. Claiming
         * a flat history would be inventing one.
         */
        double[] old = java.util.Arrays.copyOf(px.toSaveArray(), 5 + PriceIndex.WINDOW);
        PriceIndex older = new PriceIndex();
        older.restore(old);
        close("an older save opens both marks on where it is now",
                older.getPeak(), older.getIndex(), 1e-9);
        close("...both of them", older.getTrough(), older.getIndex(), 1e-9);

        /* AND A YEAR-ON-YEAR RATE NEEDS A YEAR. */
        PriceIndex young = new PriceIndex();
        int ym = 0;
        for (int m = 0; m < PriceIndex.SETTLING_MONTHS + 3; m++) young.takeMonth(.30, .12, 60, 40, ++ym);
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
                lowest >= w.minInflation() - 1e-9
                        && highest <= w.maxInflation() + 1e-9);
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
        close("...nor past the guard that stops a typo setting 9,900%",
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

        /*
         * THE TARGET IS A DIAL (0.7.4). The rule aims at the player's target
         * now, and a target is the rule's intercept and nothing else: the same
         * inflation aimed five points higher is met with exactly TAYLOR_WEIGHT
         * times five points less rate, and the slope - the Taylor principle
         * above - does not move.
         */
        out.println("\n--- the target is a dial ---");
        DebtManager aims = new DebtManager();
        close("it opens at the target it always had", aims.getInflationTarget(),
                DebtManager.DEFAULT_INFLATION_TARGET, 0);
        close("...where the rule is the rule it always was", aims.ruleRate(.05),
                DebtManager.NEUTRAL_RATE + DebtManager.TAYLOR_WEIGHT * (.05 - DebtManager.DEFAULT_INFLATION_TARGET), 0);
        aims.setInflationTarget(0);
        double aimedAtZero = aims.ruleRate(.05);
        aims.setInflationTarget(.05);
        double aimedAtFive = aims.ruleRate(.05);
        out.printf("   at 5%% inflation the rule sets %.2f%% aiming at 0%% and %.2f%% aiming at 5%%%n",
                aimedAtZero * 100, aimedAtFive * 100);
        close("the same inflation aimed at 0% and at 5% differs by TAYLOR_WEIGHT x 5 points",
                aimedAtZero - aimedAtFive, DebtManager.TAYLOR_WEIGHT * .05, 1e-12);
        close("...and on its target the rule advises neutral, whatever the target is",
                aimedAtFive, DebtManager.NEUTRAL_RATE, 1e-12);
        close("...the dial's rule being the rule at that target", aims.ruleRate(.05),
                aims.ruleRate(.05, .05), 0);
        close("...and the advice it clamps to the dial reads it too", aims.advisedPolicyRate(.05),
                DebtManager.NEUTRAL_RATE, 1e-12);
        aims.setInflationTarget(.035);
        assertTrue("...and the reason names the target it aims at, half point and all",
                aims.adviceReason(.05).contains("3.5% target"));
        aims.setInflationTarget(.5);
        close("the dial stops at MAX_INFLATION_TARGET", aims.getInflationTarget(),
                DebtManager.MAX_INFLATION_TARGET, 0);
        aims.setInflationTarget(-.03);
        close("...and at MIN_INFLATION_TARGET below", aims.getInflationTarget(),
                DebtManager.MIN_INFLATION_TARGET, 0);

        /*
         * ...AND IT REACHES THE CURRENCY - in REAL terms since 0.7.2, and
         * uncapped: the rate term was held under the trade term's reach
         * (MAX_RATE_PRESSURE .8, "neither can swamp the trade balance on its
         * own"), and that cap was the channel's saturation at 13 points that
         * 0.7.2 lifts on purpose (ForeignAccounts, THE REAL RATE, NOT THE
         * NOMINAL). What is asserted now is the channel's price: RATE_PULL
         * per point of real gap, either way.
         */
        ForeignAccounts cheap = new ForeignAccounts();
        cheap.setRealRateDifferential(-.04);
        ForeignAccounts dear = new ForeignAccounts();
        dear.setRealRateDifferential(.04);

        out.printf("   four real points under the world pulls %+.2f, four points over %+.2f%n",
                cheap.ratePressure(), dear.ratePressure());
        assertTrue("paying under the world in real terms weakens the currency", cheap.ratePressure() > 0);
        assertTrue("...and paying over it supports the currency", dear.ratePressure() < 0);
        close("...by RATE_PULL for every point of the real gap",
                dear.ratePressure(), -.04 * ForeignAccounts.RATE_PULL, 1e-12);
        ForeignAccounts wild = new ForeignAccounts();
        wild.setRealRateDifferential(-10);
        close("...and past any gap a city reaches, only the numerical guard",
                wild.ratePressure(), ForeignAccounts.MAX_RATE_PRESSURE, 0);

        /* ================= 5. in a city, and across a reload ================= */
        out.println("\n--- and all of it survives a city and a reload ---");

        Path root = Files.createTempDirectory("monetary");
        Game city = new Game(new GameFiles(root.resolve("data"), root.resolve("no-legacy")));

        System.setOut(quiet);
        try {
            city.run();
            // THE TREASURY THIS FIXTURE WAS WRITTEN AGAINST (0.7.10): its build list
            // is bought out of cash, and a city founds on D$100M since 0.7.10, not the
            // D$2.5B it assumed - so it is given that, the Wealthy preset's, explicitly.
            city.setCashForTest(Founding.WEALTHY_CASH);
            city.getLandManager().setOwnedSqFt(30_000_000);
            city.buildStack(template(city, "House"), 400, false);
            city.buildStack(template(city, "Convenience Store"), 6, false);
            city.buildStack(template(city, "Construction Depot"), 4, false);
            city.buildStack(template(city, "Coal Power Plant"), 1, false);
            city.buildStack(template(city, "Water Treatment Plant"), 1, false);
            city.buildStack(template(city, "Industrial Bakery"), 2, false);
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

        // The target off its default before the save (0.7.4), after the
        // months are played so it changes nothing they did.
        city.getDebtManager().setInflationTarget(.035);

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

        /*
         * ...AND THE TARGET, under its own key (0.7.4) - and a save from
         * before the dial, which has no key, reads the 2% every city had.
         */
        close("...and the inflation target, the player's dial",
                back.getDebtManager().getInflationTarget(), .035, 0);
        Path saved = new GameFiles(root.resolve("data"), root.resolve("no-legacy")).saveFile(6);
        com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(
                Files.readString(saved)).getAsJsonObject();
        assertTrue("fixture: the save carried the target under its own key", json.has("inflationTarget"));
        json.remove("inflationTarget");
        Files.writeString(saved, json.toString());
        Game beforeTheDial;
        System.setOut(quiet);
        try {
            beforeTheDial = new Game(new GameFiles(root.resolve("data"), root.resolve("no-legacy")));
            beforeTheDial.run();
            beforeTheDial.loadGameSave(6);
        } finally {
            System.setOut(out);
        }
        close("a save from before the dial reads the default: 2%, the constant it was",
                beforeTheDial.getDebtManager().getInflationTarget(), DebtManager.DEFAULT_INFLATION_TARGET, 0);

        inflationFallsWithTheRate(root);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* =================================================================
       6. INFLATION FALLS WITH THE RATE (asserted since 0.7.3; measured
          since 2026-09-21)

       Jerus: "the short term rates, aka the one you choose, those should be
       basically the tbill rate ... and i think how it works, is that we
       implement a central bank". Before it was built there had to be a
       number that said what the dial did to inflation - otherwise 7.0 had no
       red line to turn green, and nobody could say whether it did anything.
       From 0.6.11 to 0.7.2 this section printed that number and asserted
       nothing; the design (the-central-bank.md section 10) held the
       assertion back for the demand channel, and 0.7.3 is the demand
       channel: a household's spending above subsistence answers the real
       deposit rate (HouseholdBalance, AND WHAT IT SPENDS ANSWERS THE REAL
       RATE).

       THE ROWS, BUILD BY BUILD - inflation a year over months 25-60, the
       dial held at 3%, 10%, 20% and (since 0.7.2) 40%:

           0.6.11   +0.53%   -0.06%   -0.34%      -     the old discount
           0.7.0    +0.49%   +0.03%   -0.26%      -     the floor real
           0.7.1    +0.49%   +0.03%   -0.26%   -0.26%   40% clamped at 25%
           0.7.2    +0.02%   +0.01%   -0.38%   -0.98%   the currency answers the real rate
           0.7.3    +0.04%   -0.00%   -0.37%   -0.99%   ...and the households' saving does

       THE ASSERTION: each higher dial's inflation is no higher than the
       lower one's, within MEASUREMENT_NOISE, and the 3% row is at least
       TRANSMISSION_FLOOR above the 40% row - a point across the range, or
       the rate is not a channel. At 0.7.2 the spread was 0.996 of a point
       and the floor would have failed; at 0.7.3 it is 1.032.

       AND WHAT CARRIES THE POINT IS STILL MOSTLY THE CURRENCY, which the
       rows below print the reasons for. On this founding the demand channel
       moves the spread by about four hundredths of a point - at 0.7.3
       twice what a one-month offset of the dial did, and no more
       (SAVING_RESPONSE at 2.0 was measured at 1.145; since 0.7.11 the
       offset alone moves a row by up to 0.053, MEASUREMENT_NOISE):
         - the deposit rate carries about a quarter of the dial - 1.4% at 3%,
           11% at 40%, because the bank paid savers Bank.DEPOSIT_PASS_THROUGH
           of what it earned and not the dial - so the spend factor only runs
           from 0.99 to 0.88 across the whole range (as measured before
           0.7.7; since then the bank chooses a share of the dial by its
           funding, Bank.depositShare(), and the figures in the table this
           prints are the current ones);
         - the shelf is at its floor, Retail.OPENING_SELL_PRICE, by month 60
           on every row, so the food four fifths of the index cannot fall
           whatever demand does; what separates the rows is rent, which
           follows the currency through what a home costs to build;
         - and demand here is short of what the shops can serve, with no
           scarcity mark-up to take off, so what is not spent is simply not
           bought: the shops sell less at the same price.
       A city whose shelf is priced by scarcity would answer; this one's is
       priced by its floor. The assertion is on the transmission, whichever
       channel carries it; the columns say which one did.

       One founding - the playtest's own, borrowed the way the ensemble
       probes borrow it - run to month 24 and then once for each held rate
       from there: the dial set at month 25, the first month the currency is
       allowed to move, and held to month 60. Nothing else touches the city
       after the founding, so the dial is the only hand on it; the runs are
       asserted to differ by the dial and by nothing else. Each row prints
       inflation averaged over the last 36 months (struck on the index,
       month 24 to month 60, as an annual rate), the price index, the
       exchange rate, the bank's rate to a good credit (Bank.lendingRate()
       on the policy rate, as the month prices the carry trade) and its
       strain, what the businesses owe, the city's own rate, M2, and - since
       0.7.3 - the deposit rate, the real deposit rate and the spend factor
       the households planned the last month at.

       M2 IS THE GAME'S SINCE 0.7.3: Game.getM2(), the Money page's figure -
       the households' savings, the sectors' cash in credit, the world's
       deposits at the bank and currency, the treasury left out. Until 0.7.3
       this section summed its own, without the world's deposits.
       ================================================================= */

    /**
     * The policy rates the one founding is held at, from month 25: the three
     * of the baseline, and since 0.7.2 a fourth at 40% - past the old stop of
     * the dial (25% until 0.7.2), the uncapped case.
     */
    static final double[] HELD_RATES = { .03, .10, .20, .40 };

    /** How long each run is: five years, the last three of them at the held rate. */
    static final int MEASURED_MONTHS = 60;

    /** The month the dial is held from: the first in which the currency may move. */
    static final int HELD_FROM = ForeignAccounts.SETTLING_MONTHS + 1;

    /**
     * How far apart two runs of the one founding may read, in inflation a year, when only the dial's timing moves: 0.10 points.
     *
     * RE-MEASURED AT 0.7.11 (Jerus, 2026-09-24: "re-measure it"). Holding each row's dial from month 26 instead of 25 moves
     * the rows by 0.000 / 0.053 / 0.051 / 0.040 points (3% / 10% / 20% / 40%); at 0.7.3 the largest was 0.019 (0.026 at
     * 0.7.2) and the allowance 0.05. What moved it is the landlords' insured mortgage (0.7.11): its rate is fixed for a
     * ten-year term at whatever the dial reads the month it is written, so the month the dial moves in now decides the
     * rate a building carries for a decade - a House written a month before the hold pays 4.48% where one written after
     * it pays 11.2%. That is how fixed-rate lending behaves, so it is noise in the sense this constant means: the timing
     * of the hand, not the level of the dial. The allowance keeps the headroom it had (about twice the largest reading).
     */
    static final double MEASUREMENT_NOISE = .0010;

    /** How much lower inflation must run at a dial of 40% than at 3%, a year: one point - the channel has to be worth a point across the range or it is not a channel. */
    static final double TRANSMISSION_FLOOR = .01;

    /** One month, as the playtest steps it: a broke city steps rather than skips. */
    static void step(Game g, double heldRate) {
        step(g, heldRate, HELD_FROM);
    }

    /** ...holding the dial from a month of the caller's: the noise run holds it a month late. */
    static void step(Game g, double heldRate, int heldFrom) {
        if (g.getMonth() >= heldFrom) g.getDebtManager().setPolicyRate(heldRate);
        int before = g.getMonth();
        g.simulateMonths(1);
        if (g.getMonth() == before) g.toggleNextMonth();
    }

    /** The playtest's founding (seed 0), to the month before the dial is held. */
    static Game founding(Path root, String label) {
        Game g = new Game(new GameFiles(root.resolve(label), root.resolve("no-legacy")));
        g.run();
        LongPlaytest.build(g, "House", 40);
        LongPlaytest.build(g, "Convenience Store", 3);
        LongPlaytest.build(g, "Mixed Farm", 2);
        for (int m = 0; m < 3; m++) step(g, Double.NaN);
        LongPlaytest.build(g, "House", 20);
        for (int m = 0; m < 4; m++) step(g, Double.NaN);
        LongPlaytest.build(g, "Convenience Store", 2);
        LongPlaytest.build(g, "Construction Depot", 1);
        for (int m = 0; m < 5; m++) step(g, Double.NaN);
        LongPlaytest.advise(g);
        while (g.getMonth() < HELD_FROM) step(g, Double.NaN);
        return g;
    }

    /** What the founding looks like the month before the dial moves - equal across the runs, or they are not one founding. */
    static double[] fingerprint(Game g) {
        return new double[] {
                g.getMonth(), g.getCash(), g.getPopulationManager().getPopulation(),
                g.getEconomyManager().getMonthGdp(), g.getPriceIndex().getIndex(),
                g.getForeignAccounts().getRate(), g.getBank().getCash(),
                g.getEconomyManager().getBusinessDebtManager().getTotalPrincipal(),
                g.getHouseholdBalance().totalSavings(), g.getDebtManager().getPolicyRate() };
    }

    /**
     * One run of the founding at a held rate: the dial held from heldFrom to
     * MEASURED_MONTHS, and inflation over the held months as an annual rate.
     * The city is handed back through `city` for its columns.
     */
    static double heldRun(Path root, String label, double rate, int heldFrom, double[] print, Game[] city) {
        System.setOut(quiet);
        try {
            Game g = founding(root, label);
            if (print != null) System.arraycopy(fingerprint(g), 0, print, 0, print.length);
            double indexFrom = g.getPriceIndex().getIndex();
            while (g.getMonth() <= MEASURED_MONTHS) step(g, rate, heldFrom);
            if (city != null) city[0] = g;
            return Math.pow(g.getPriceIndex().getIndex() / indexFrom,
                    12.0 / (MEASURED_MONTHS - HELD_FROM + 1)) - 1;
        } finally {
            System.setOut(out);
        }
    }

    static void inflationFallsWithTheRate(Path root) {
        out.println("\n--- inflation falls with the rate ---");

        int n = HELD_RATES.length;
        double[][] prints = new double[n][10];
        double[] inflation = new double[n];
        boolean dialsHeld = true;
        for (int r = 0; r < n; r++) {
            Game[] held = new Game[1];
            inflation[r] = heldRun(root, "rate-" + r, HELD_RATES[r], HELD_FROM, prints[r], held);
            Game g = held[0];
            dialsHeld &= Math.abs(g.getDebtManager().getPolicyRate() - HELD_RATES[r]) < 1e-12;
            if (r == 0) {
                out.printf("   the founding at month %d: %,.0f people, index %.4f (basket %s), policy %.2f%%%n",
                        (int) prints[r][0], prints[r][2], prints[r][4],
                        prints[r][4] == 1.0 ? "based at 1.0 or not yet based" : "based",
                        prints[r][9] * 100);
                out.printf("   %6s %9s %8s %7s %9s %7s %13s %9s %14s %8s %9s %6s%n", "policy", "inflation",
                        "index", "fx", "bank rate", "strain", "business debt", "city rate", "M2 (the page)",
                        "deposit", "real dep.", "spend");
            }
            Bank bank = g.getBank();
            out.printf("   %5.0f%% %8.2f%% %8.4f %7.4f %8.2f%% %7.2f %,13.0f %8.2f%% %,14.0f %7.2f%% %+8.2f%% %6.3f%n",
                    HELD_RATES[r] * 100, inflation[r] * 100, g.getPriceIndex().getIndex(),
                    g.getForeignAccounts().getRate(),
                    bank.lendingRate(g.getDebtManager().getPolicyRate()) * 100, bank.strain(),
                    g.getEconomyManager().getBusinessDebtManager().getTotalPrincipal(),
                    g.getDebtManager().getRate() * 100, g.getM2(),
                    bank.depositRate() * 100, g.realDepositRate() * 100,
                    g.getHouseholdBalance().getSpendFactor());
        }
        StringBuilder rates = new StringBuilder(), rows = new StringBuilder();
        for (int r = 0; r < n; r++) {
            rates.append(r == 0 ? "" : " / ").append(String.format("%.0f%%", HELD_RATES[r] * 100));
            rows.append(r == 0 ? "" : " / ").append(String.format("%.2f%%", inflation[r] * 100));
        }
        out.printf("   inflation at %s: %s a year, months %d-%d; %.3f points from the lowest dial to the highest%n",
                rates, rows, HELD_FROM, MEASURED_MONTHS, (inflation[0] - inflation[n - 1]) * 100);

        /*
         * THE NOISE, MEASURED AND NOT ASSUMED: every row again with its dial
         * held from a month later. The only thing that moves is the hand's
         * timing, so how far the pair differ is how far two readings of this
         * founding can differ without the dial's LEVEL meaning anything - the
         * allowance the ordering below is given. The 3% row reads exactly
         * nothing, because 3% is the founding's own dial and a month's delay
         * changes no month.
         */
        double noise = 0;
        StringBuilder offsets = new StringBuilder();
        for (int r = 0; r < n; r++) {
            double late = heldRun(root, "late-" + r, HELD_RATES[r], HELD_FROM + 1, null, null);
            noise = Math.max(noise, Math.abs(late - inflation[r]));
            offsets.append(r == 0 ? "" : " / ").append(String.format("%.3f", Math.abs(late - inflation[r]) * 100));
        }
        out.printf("   the same rows held a month late differ by %s points; the allowance is %.3f%n",
                offsets, MEASUREMENT_NOISE * 100);

        boolean oneFounding = true;
        for (int r = 1; r < n; r++) {
            oneFounding &= java.util.Arrays.equals(prints[r], prints[0]);
        }
        assertTrue("the runs differ by the dial and by nothing else",
                oneFounding && dialsHeld);
        assertTrue("...and a month's delay in the hand moves them less than the allowance",
                noise <= MEASUREMENT_NOISE);
        for (int r = 1; r < n; r++) {
            assertTrue(String.format("inflation at %.0f%% is no higher than at %.0f%%, within the noise",
                            HELD_RATES[r] * 100, HELD_RATES[r - 1] * 100),
                    inflation[r] <= inflation[r - 1] + MEASUREMENT_NOISE);
        }
        assertTrue(String.format("...and a dial of %.0f%% holds it at least a point under %.0f%%",
                        HELD_RATES[n - 1] * 100, HELD_RATES[0] * 100),
                inflation[0] - inflation[n - 1] >= TRANSMISSION_FLOOR);
    }

    static BuildingsTemplate template(Game game, String name) {
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException("no template named " + name);
    }
}

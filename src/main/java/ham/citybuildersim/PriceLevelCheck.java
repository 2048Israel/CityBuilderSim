package ham.citybuildersim;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A city founded at 1900's prices is today's real economy with every dollar multiplied by one factor, and this is how we know (EP1, Jerus
 * 2026-10-30: "1900 price level"; docs/studies/spec-1900-start.md 2).
 *
 * WHY. The calendar starts in 1900 with today's technology, and "1900 price level" means every nominal anchor - the world's prices, the wage
 * rungs, the building costs, the opening money, every dollar dial - is today's figure times PriceLevel.FACTOR (the US CPI of 1900 over 2025,
 * 0.02588) and no ratio, rate, count or physical quantity moves. The anchors are in over a hundred places and a missed one is not an error: it
 * is a relative price that is silently off by 38.6 times, so food costs a labourer's day or a house costs a week's pay, and nobody sees it
 * until a screen reads strange. Nobody can enumerate this codebase's money by reading it, so the test is not a list of fields: it is TWO CITIES,
 * as DenominationCheck's is.
 *
 *   A is founded at the factor, as the game is.
 *   B is founded at today's prices (-Dcbs.priceLevel=1, PriceLevel's one door).
 *
 * If the factor is only a change of price level then A is B with a different label on the axis: every real quantity identical to the person
 * and the tonne, every nominal one identical after dividing by the factor, month by month. A missed anchor changes a relative price, a changed
 * relative price changes what gets built and who moves in, and the two part company; the harness says which series, and when.
 *
 * WHAT THIS PROVES, in sections.
 *   1. The series: 126 years, the first year the calendar's, the factor the series' ratio.
 *   2. The world file's US dollar, from founding to 2025, times the factor, is 1 (the dollar the world inflates on and the factor that set the
 *      opening prices are one dollar).
 *   3. Each funnel carries the factor: the six rungs, a good's three prices, the food prices of consumption.json, a building's cost, upkeep and
 *      consumables read from the data file, and the one copy of the materials' price.
 *   4. The rankings' bridge is the series' ratio, 2015 over the founding year.
 *   5. The named literals each carry it, and the cross-file ratios between them and the funnels (the unskilled wage over a food, tuition over a
 *      wage, materials over a cash cost, the two land bases, the works' dollars over the cash a point) equal the same ratios of today's figures.
 *   6. THE TWIN, which is the finding: the default founding run for TWIN_MONTHS months in a child JVM at the factor and in one at today's prices,
 *      one after the other, each printing a month-by-month digest; the parent loads no city.
 *   7. The twin again on a built city, BUILT_MONTHS months.
 *   8. The twin once more on the PLAYED city - the one MapCheck and the playtest play: a founding on MiningCheck's world, forty houses, three
 *      stores and two farms put up, then the playtest's advisor looking every six months, ten houses retired at PLAYED_DEMOLITION and an iron mine
 *      bought at PLAYED_IRON, PLAYED_MONTHS months. It found what the first two could not, because it is the one that BUYS GROUND and
 *      BORROWS: a proceeds figure rounded to a fixed hundredth of a unit (Game.cents: a thirty-eight times coarser rounding at the factor), and
 *      three offers tied at one rate of ground a dollar that the nearer should win, an ulp apart at the factor, so the farther won
 *      (LandMarket.RANK_TIE).
 *   9. The twin on ScaleCheck's town (the playtest's founding with forty houses, stores and farms, saved and loaded as ScaleCheck loads it),
 *      TOWN_MONTHS months. It found what the three before could not, because it is the one that SELLS ABROAD for a year and reads the year back:
 *      the history rounded a table's money to a fixed hundredth of a thousand (PartnerTrade.HISTORY_ROUND), a thirty-eight times coarser
 *      rounding at the factor, and Game.cityExportPriceLevel() weights the world's prices by those rounded exports, so the second January's
 *      terms of trade, and the D$, were another's by month 24.
 *  10. THE SCREENS' CUT-OFFS (EP2), at the factor and at today's prices, over the default founding's two children, the built city's and the town's: the partners given
 *      a lane (TradeLanes.of over the feed, at minLane()), the holdings folded and shown (FundView.FOLD_UNDER) and every live nominal line
 *      ui.Money.something() shows are the same every month, a lane is drawn somewhere, and the lines the old half-thousand would have hidden
 *      are there at the factor and none at today's prices. Section 8 is left out: its twin parts on series of its own (main's record).
 *
 * The one oracle for "today's figure" is the child at today's prices: a constant that is read there is the figure as the class writes it, so
 * nothing in this file copies a literal from the model.
 */
public class PriceLevelCheck {

    /** The months each twin runs: five years, enough for the founding's wages, rents, builds, loans, trade and the first inflation to act, short enough for a minute or two a child. */
    static final int TWIN_MONTHS = 60;

    /** ...and the built city's: ten years, as DenominationCheck's reformed town is run (its fixture reads the loans, the schools and the first replacements). */
    static final int BUILT_MONTHS = 120;

    /**
     * ...and the town's: ScaleCheck's founded city (its section 1), to month 33 (55 until FC1, 34 until BA1, below) - EP1's gate review found that this town parts from its twin at
     * the factor where the three cities above did not (the second January's terms of trade, from a history that rounded a young city's exports
     * to a fixed hundredth of a thousand: PartnerTrade.HISTORY_ROUND_MONEY), and the twin holds it to the bit-level of rounding until then. It
     * stops there on purpose: in month 56 a cell's debt is a hair above zero (5e-18) at today's prices and exactly zero at the factor and at 0.5, and
     * Exchange.mayBuy() reads the hair as a debt, so one cell buys a company's shares at one level and not at the other; the town then parts
     * from month 57 at 0.0259, 0.5, 0.25, 0.1 and at 0.999999 and 1.000001 beside 1 (a millionth off 1 is enough), so it is a draw of the
     * rounding and not of the price level, and a twin cannot hold two draws equal. To month 34 since FC1 (2026-11-02), by the
     * nudge below (35 on the world file before LC3's; on LC3's it parts a month sooner, a tie by the same nudge): the builders'
     * scarcity premium is on through the town's long founding queue, and from month 35 the town
     * at today's prices parts from every other level - the factor, the factor nudged by 1e-12 and today's nudged by 1e-12
     * all run equal - by a tie in a planner's cost (BusinessInvestment.totalCostOf(), the work times the multiple). To month 33 since BA1
     * (2026-11-03): with each building of an order priced on the queue the ones before it leave (BuildersShort.meanPremium()) the same tie
     * comes a month sooner, at month 34 - the factor, the factor nudged by 1e-12 and today's nudged by 1e-12 run equal for 35 months on
     * world-1.bin, and today's alone parts.
     */
    static final int TOWN_MONTHS = 33;

    /**
     * ...and the played city's: past its demolition (PLAYED_DEMOLITION), its iron mine (PLAYED_IRON) and the land the advisor keeps buying ahead
     * of the city (month 96 was the first to part), to month 103 (105 until RT1, below) - ten years until EP1 merged IC1, when the played city became another and
     * parted from its twin at month 106 by a rounding tie, not a missed price (EP1's notes, "PriceLevelCheck 8, traced"): a household cell's
     * count is 1e-16 at one level and 0 at the other from month 104, 70 cells move by a fraction of a household at 106 with people, homes and
     * stores equal to the person; the factor nudged by 1e-12 either way runs equal to today's for 120 months, and today's own nudged to
     * 1.0000001 parts from 1 at the same month with the same two values. A twin cannot hold two draws equal, so the horizon stops before it,
     * as TOWN_MONTHS does. When a later batch parts this city earlier, nudge the factor by 1e-12 first: a parting the nudge moves is a tie
     * (the horizon goes to the month before it), one it does not move is a price the factor missed (mend it). RT1 (2026-11-05) did both: with a
     * farm counted in money (Agriculture.unitsOf()) one retires at the factor as at today's prices, and the city first parted at month 70, on a
     * businesses' holding of a city bond left 4.4e-16 at today's prices and none at the factor (a sale struck pro rata an ulp short), which the
     * nudge did not move: mended in Debt.moved(). Then at month 104 in Manufacturing's cash by 4.9e-9, where the factor nudged by 1e-12 parts
     * from the factor by 3.5e-9 and today's nudged from today's by 2.1e-9: a tie (the bond search's stop, BS1). So to month 103.
     * WG2, {next}, 2026-11-10: with the pulls lifting the homes' draw only (Migration.jobsPull()/homesPull()) the played city is another path - the same
     * people, workforce, homes and stores to the person at both levels to month 103, but Manufacturing's cash parts at month 83 (3.3e-5 of 8,073 at its
     * peak so far, 4e-9 of it; bank.deposits 2.2e-9 at 88, cash 3.8e-9 at 94, TRUCKS demand 2.3e-8 at 96, Manufacturing's cash 1.5e-7 at 103). It is a draw
     * of rounding and not a price the factor missed: the factor nudged by 1e-12 (0.025883463843004304) parts from the factor, today's nudged by 1e-12
     * parts from today's, and each of those against the other level parts in the same series at the same month 83 (first month over 1e-9, four pairs:
     * 83, 83, 83, 83), and the gap is Manufacturing's cash refilled by a lump in months 52, 59, 63, 70, 76, 79 and 83 with each lump taking the last one's
     * gap a factor of 10 to 100 larger (1e-12, 4e-10, 6e-9, 5e-9, 4e-8, 1.4e-6, 3.3e-5 of a thousand) and a month of cash below zero (66-69, 73-75, 82)
     * putting it back to 1e-13. The Migration brake and drift of WG2 do not act in it (BRAKE_FROM_MONTH, month 240). To month 82: the worst nominal gap to
     * there is 3.6e-10 (Manufacturing's cash, month 80) and the worst real one 3e-12, in all four pairs.
     */
    static final int PLAYED_MONTHS = 82;

    /** The month the played city pulls down ten houses: month 60, as when the horizon was 120 months and the demolition its half. */
    static final int PLAYED_DEMOLITION = 60;

    /** The month the played city is given its iron mine: month 40, as when the horizon was 120 months and the mine its third. */
    static final int PLAYED_IRON = 40;

    /**
     * How far the world file's US price level from founding to 2025, times PriceLevel.FACTOR, may be from 1, as a share. The years before the file's
     * own (the stand-in century) follow PriceLevel's series exactly; the file's own 2000-2025 are the BLS's CPI-U to the file's rounding, and its
     * 2025 is an ESTIMATE (the mean of the months published, 0.17% over the BLS's), so this is a band the file's estimate sits in, not a bound on
     * the model.
     */
    static final double US_LEVEL_TOLERANCE = .003;

    /** The stand-in century follows the series to this share: floating point of one logarithm, nothing the model rounds. */
    static final double STAND_IN_TOLERANCE = 1e-9;

    /**
     * How far a nominal series of the factor's city, divided by the factor, may be from the same series in the city at today's prices, as a share
     * of the larger of the two series' largest size so far (a balance is a sum of flows, and its rounding is the flows' size, not what is left:
     * LD1's played city spent its treasury down to 34 of today's thousands at month 103, and a gap of 7e-8 there was 1.9e-9 of what was left and
     * 7e-13 of the treasury it had been). Floating point only: a money amount multiplied by the factor and divided by it again carries a relative error of a few parts
     * in 1e16 a step, and a month is a few hundred steps, so 1e-9 is a hundred thousand times what rounding gives and a million times less than
     * a relative price that is off by an anchor (the smallest misses are a few per cent). A series that crosses a rule whose edge is a dollar
     * threshold would part company at the edge by a whole step; none is excused here.
     */
    static final double TWIN_NOMINAL_TOLERANCE = 1e-9;

    /** ...and a real one (a count, a ratio, a quantity in units), the same band: nothing real is rounded differently by a change of price level. */
    static final double TWIN_REAL_TOLERANCE = 1e-9;

    /**
     * A nominal series' value below this, in today's thousands, is zero for the comparison (a millionth of a dollar): the model's own numeric
     * floors are at 1e-9 (OrderBook.DUST, Exchange.MIN_FAIR), and a balance that is rounding noise either side of zero is not a price.
     */
    static final double TWIN_MONEY_FLOOR = 1e-9;

    /**
     * The history records money to a fixed grid (HistorySave.round2, round4, round6) or to six significant figures (roundSig), so a nominal history
     * series of the factor's city, divided by the factor, is on its grid scaled by 1/factor and today's is on the grid itself: they differ by up to
     * half a step of each, and the harness allows exactly that, the step read from the grid today's series sits on (gridOf()). A series on no grid is
     * recorded to six significant figures, half a unit in the last place of each, this share together. The live series sampled each month carry
     * no rounding and are held to TWIN_NOMINAL_TOLERANCE.
     */
    static final double HISTORY_SIG_TOLERANCE = 1.1e-5;

    /**
     * A real quantity below this, in units, is zero for the comparison: a billionth of a unit, the size of the model's own numeric floors
     * (OrderBook.DUST, Exchange.MIN_FAIR). What a month imported is a sum less a sum, and the two cities' sums leave 3e-14 of cancellation on one side
     * and exactly 0 on the other in the month the quantity is nothing (BREAD and BAKERY, month 118 of the built city).
     */
    static final double TWIN_QUANTITY_FLOOR = 1e-9;

    /**
     * A real series the history records to a grid may be one step apart in at most this many months: a value that sits on the middle of two steps
     * (0.00175 recorded to four places, the bank's deposit rate at month 0) rounds to either on one rounding more or less of the last bit. A
     * systematic difference is in every month; two ties in a hundred and twenty is chance.
     */
    static final int TIE_FLIPS = 2;

    /** The relative gap allowed between a constant that carries the factor and today's figure times it, and between a ratio and today's ratio: one rounding of one product. */
    static final double CONSTANT_TOLERANCE = 1e-12;

    /** The literal half-thousand the screens held before EP2 as their "nothing" (ui.Money, FundView's fold), in thousands as written: the figures between it and the factor's own cut-off are the ones the old cut-off would have hidden. */
    static final double OLD_CUT = .5;

    /** The child property that stands in for the factor (PriceLevel's one door). */
    static final String DOOR = "cbs.priceLevel";

    static int fails = 0;
    static PrintStream out;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-86s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void relative(String label, double actual, double expected, double band) {
        boolean ok = gap(actual, expected) <= band;
        if (!ok) fails++;
        out.printf("%-86s %s%s%n", label, ok ? "OK" : "FAIL", ok ? "" : String.format("  %.10g != %.10g (%.3g apart)", actual, expected, gap(actual, expected)));
    }

    /** The relative gap of two figures: |a - b| over the larger; 0 for two NaNs, two equal infinities or two zeros, infinite for one NaN. */
    static double gap(double a, double b) {
        if (Double.isNaN(a) && Double.isNaN(b)) return 0;
        if (Double.isNaN(a) || Double.isNaN(b)) return Double.POSITIVE_INFINITY;
        if (a == b) return 0;
        double m = Math.max(Math.abs(a), Math.abs(b));
        return m == 0 ? 0 : Math.abs(a - b) / m;
    }

    /**
     * Today's figures priced at the founding's level: each times PriceLevel.FACTOR (EP1). The harnesses whose tables are the studies' (the materials chains'
     * costs, running costs and world prices, in thousands of today's prices) write them through this, so the premise is the study's figure and not a moved one.
     */
    static double[] atFounding(double... today) {
        double[] priced = new double[today.length];
        for (int i = 0; i < today.length; i++) priced[i] = today[i] * PriceLevel.FACTOR;
        return priced;
    }

    /** Two money figures built differently (a price and a price times units, against the study's product) are the same to a billionth: floating point only. */
    static boolean sameMoney(double a, double b) {
        return Math.abs(a - b) <= 1e-9 * Math.max(Math.abs(a), Math.abs(b));
    }

    /* ================================================================== */

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].startsWith("--twin")) {
            Twin.run(args[0]);
            return;
        }
        out = System.out;
        long start = System.nanoTime();

        out.println("--- 1. the series: 126 years of the US consumer price index ---");
        theSeries();

        out.println("\n--- 2. the world file's US dollar, from the founding to 2025, times the factor, is one ---");
        theWorldFile();

        out.println("\n--- 3. each funnel carries the factor ---");
        theFunnels();

        out.println("\n--- 4. the rankings' bridge is the series' ratio ---");
        theBridge();

        // 5 and 6 need the children: the named literals and the ratios, then the twin's months.
        String why = null;
        if (System.getProperty(DOOR) != null) why = "-D" + DOOR + " is set here; the harness runs at the founding's level and sets it only in its children";
        if (why != null) {
            assertTrue("the twin runs: " + why, false);
        } else {
            Child plain = Child.run(false, false);
            Child today = Child.run(true, false);
            out.println("\n--- 5. the named literals and the cross-file ratios, against today's figures ---");
            theLiterals(plain, today);
            theRatios(plain, today);
            out.println("\n--- 6. the twin: the default founding at the factor and at today's prices, " + TWIN_MONTHS + " months, month by month ---");
            theTwin(plain, today, TWIN_MONTHS);
            Child builtPlain = Child.run(false, true);
            Child builtToday = Child.run(true, true);
            out.println("\n--- 7. the twin again, on a built city (400 houses, schools, clinics, a bank, mines, furnaces, roads and a bus network), " + BUILT_MONTHS + " months ---");
            theTwin(builtPlain, builtToday, BUILT_MONTHS);
            Child playedPlain = Child.run(false, "--twin-played");
            Child playedToday = Child.run(true, "--twin-played");
            out.println("\n--- 8. the twin on the played city (MapCheck's: houses, stores and farms, the advisor every six months, a demolition, an iron mine), " + PLAYED_MONTHS + " months ---");
            theTwin(playedPlain, playedToday, PLAYED_MONTHS);
            Child townPlain = Child.run(false, "--twin-town");
            Child townToday = Child.run(true, "--twin-town");
            out.println("\n--- 9. the twin on ScaleCheck's town (the playtest's founding, forty houses, stores and farms, twenty houses more, a depot; saved and loaded as ScaleCheck loads it), " + TOWN_MONTHS + " months ---");
            theTwin(townPlain, townToday, TOWN_MONTHS);
            out.println("\n--- 10. the screens' cut-offs, at the factor and at today's prices: the lanes drawn, the holdings folded, the lines shown ---");
            theCutOffs(plain, today, builtPlain, builtToday, townPlain, townToday);
        }

        out.printf("%nPriceLevelCheck took %.1f s%n", (System.nanoTime() - start) / 1e9);
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* =================================================================
       1. THE SERIES
       ================================================================= */

    static void theSeries() {
        assertTrue("the series' first year is the calendar's (CityCalendar.EPOCH_YEAR)", PriceLevel.FIRST_YEAR == CityCalendar.EPOCH_YEAR);
        assertTrue("...its last is the year the literals are priced in (WorldMarket.REFERENCE_YEAR)", PriceLevel.LAST_YEAR == WorldMarket.REFERENCE_YEAR);
        assertTrue("...so it spans 126 years", PriceLevel.LAST_YEAR - PriceLevel.FIRST_YEAR + 1 == 126);
        boolean all = true;
        for (int y = PriceLevel.FIRST_YEAR; y <= PriceLevel.LAST_YEAR; y++) {
            double v = PriceLevel.usCpi(y);
            if (!(v > 0) || !Double.isFinite(v)) all = false;
        }
        assertTrue("every year has a positive index", all);
        boolean refused = false;
        try { PriceLevel.usCpi(PriceLevel.FIRST_YEAR - 1); } catch (IllegalArgumentException e) { refused = true; }
        try { PriceLevel.usCpi(PriceLevel.LAST_YEAR + 1); } catch (IllegalArgumentException e) { refused = refused && true; }
        assertTrue("a year outside the series is refused", refused);
        assertTrue("1913 is the BLS's first published year, and 1912 is Rees's", PriceLevel.isPublished(1913) && !PriceLevel.isPublished(1912));
        relative("the factor is the first year's index over the last's (8.333 / 321.943)",
                PriceLevel.FACTOR, PriceLevel.usCpi(PriceLevel.FIRST_YEAR) / PriceLevel.usCpi(PriceLevel.LAST_YEAR), 1e-15);
        assertTrue("...about a thirty-eighth (the 1900 dollar buys 38.6 of today's)", Math.abs(1 / PriceLevel.FACTOR - 38.6) < .1);
        relative("atFounding(x) is x times the factor", PriceLevel.atFounding(1234.5), 1234.5 * PriceLevel.FACTOR, 1e-15);
    }

    /* =================================================================
       2. THE WORLD FILE
       ================================================================= */

    static void theWorldFile() throws Exception {
        byte[] bytes = WorldDataCheck.resource(WorldData.FIXTURE);
        if (bytes != null) {
            try (WorldData data = WorldData.of(bytes)) {
                usLevels("the fixture", data);
            }
        } else {
            assertTrue("no fixture: " + WorldData.FIXTURE + " is not beside the classes", false);
        }
        Path full = WorldData.locate();
        String named = System.getProperty(WorldData.PROPERTY);
        if (named != null && !named.isBlank() && (full == null || !Files.isRegularFile(full))) {
            assertTrue("the file -D" + WorldData.PROPERTY + " names is there (" + named + ")", false);
        } else if (full == null) {
            out.println("world-1.bin not named (-D" + WorldData.PROPERTY + "=<path>) nor at " + WorldData.DATA_FILE + ": the fixture stood in for the full file");
        } else {
            try (WorldData data = WorldData.open(full)) {
                usLevels("the full file", data);
            }
        }
    }

    /** A Historical city's US price level from its founding (month 0, January 1900) to 2025, against the series. */
    static void usLevels(String scope, WorldData data) {
        Nations nations = new Nations(data, Nations.Path.HISTORICAL, Nations.DEFAULT_SEED);
        WorldMoneys moneys = new WorldMoneys(data, nations, null);
        int first = PriceLevel.FIRST_YEAR;
        double worstStandIn = 0, worstFile = 0;
        int worstFileYear = first;
        for (int y = first; y <= PriceLevel.LAST_YEAR; y++) {
            double level = moneys.usPriceLevel(12 * (y - Nations.FIRST_YEAR));
            double series = PriceLevel.usCpi(y) / PriceLevel.usCpi(first);
            double g = gap(level, series);
            if (y < 2000) worstStandIn = Math.max(worstStandIn, g);
            else if (g > worstFile) { worstFile = g; worstFileYear = y; }
        }
        double at2025 = moneys.usPriceLevel(12 * (PriceLevel.LAST_YEAR - Nations.FIRST_YEAR));
        relative(scope + ": the US level founding to 2025 (" + String.format(Locale.ROOT, "%.2f", at2025) + ") times the factor is 1", at2025 * PriceLevel.FACTOR, 1, US_LEVEL_TOLERANCE);
        assertTrue(scope + ": the stand-in century (1900-1999) follows the series exactly (worst "
                + String.format(Locale.ROOT, "%.2g", worstStandIn) + ")", worstStandIn <= STAND_IN_TOLERANCE);
        assertTrue(scope + ": the file's own years (2000-2025) follow it within " + US_LEVEL_TOLERANCE + " (worst "
                + String.format(Locale.ROOT, "%.2g", worstFile) + " in " + worstFileYear + ")", worstFile <= US_LEVEL_TOLERANCE);
    }

    /* =================================================================
       3. THE FUNNELS
       ================================================================= */

    static void theFunnels() throws Exception {
        // PayTier: the six rungs
        boolean rungs = true, ladder = true;
        PayTier floor = PayTier.UNSKILLED;
        for (PayTier t : PayTier.values()) {
            rungs &= gap(t.getMonthlyWage(), t.getTodaysWage() * PriceLevel.FACTOR) <= 1e-15;
            ladder &= gap(t.getMonthlyWage() / floor.getMonthlyWage(), t.getTodaysWage() / floor.getTodaysWage()) <= CONSTANT_TOLERANCE;
        }
        assertTrue("PayTier: each of the " + PayTier.values().length + " rungs is its literal times the factor", rungs && PayTier.values().length == 6);
        assertTrue("...and the ladder's shape (each rung over the unskilled) is today's", ladder);
        // WG1: the going rate is the unskilled rung, and the default floor its share of it (the floor no longer anchors the ladder).
        assertTrue("LabourMarket.GOING_RATE is the unskilled rung, not scaled again", LabourMarket.GOING_RATE == PayTier.UNSKILLED.getMonthlyWage());
        relative("...and DEFAULT_MINIMUM_WAGE its share of it, not scaled again", LabourMarket.DEFAULT_MINIMUM_WAGE,
                LabourMarket.DEFAULT_FLOOR_SHARE * PayTier.UNSKILLED.getMonthlyWage(), 1e-15);
        relative("...and its dial's bounds are the rung's x.25 and x5", LabourMarket.MAX_SETTABLE / LabourMarket.MIN_SETTABLE,
                LabourMarket.MAX_SETTABLE_MULTIPLE / LabourMarket.MIN_SETTABLE_SHARE, 1e-12);
        relative("TaxPolicy.DEFAULT_FIXED_GRANT is its share of the unskilled rung, not scaled again", TaxPolicy.DEFAULT_FIXED_GRANT,
                TaxPolicy.DEFAULT_STUDENT_GRANT_SHARE * PayTier.UNSKILLED.getMonthlyWage(), 1e-15);

        // Good: the three prices of every good
        int goods = 0, importable = 0, exportable = 0;
        boolean prices = true, wedge = true, shape = true;
        for (Good g : Good.values()) {
            goods++;
            if (g.importable()) importable++;
            if (g.exportable()) exportable++;
            prices &= gap(g.worldImportPrice(), g.todaysImportPrice() * PriceLevel.FACTOR) <= 1e-15
                    && gap(g.worldExportPrice(), g.todaysExportPrice() * PriceLevel.FACTOR) <= 1e-15
                    && gap(g.baseFreight(), g.todaysBaseFreight() * PriceLevel.FACTOR) <= 1e-15;
            shape &= g.importable() == (g.todaysImportPrice() > 0) && g.exportable() == (g.todaysExportPrice() > 0);
            if (g.importable() && g.exportable() && g.todaysImportPrice() > 0) {
                wedge &= gap(g.baseFreight() / g.worldImportPrice(), g.todaysBaseFreight() / g.todaysImportPrice()) <= CONSTANT_TOLERANCE
                        && gap(g.worldExportPrice() / g.worldImportPrice(), g.todaysExportPrice() / g.todaysImportPrice()) <= CONSTANT_TOLERANCE;
            }
        }
        assertTrue("Good: the " + goods + " goods carry the factor on all three prices (import, export, base freight)", prices && goods > 0);
        assertTrue("...a good that cannot be imported or exported still cannot (" + importable + " import, " + exportable + " export)", shape);
        assertTrue("...and the wedge (freight and export over import) is each good's today's", wedge);
        assertTrue("BuildingManager.MATERIALS_WORLD_PRICE is Good.MATERIALS's import price, the one copy", BuildingManager.MATERIALS_WORLD_PRICE == Good.MATERIALS.worldImportPrice());

        // the screens: a price a tenth of a cent shows its digits (ui/Money.unitPrice), not $0.00 (study 2.7, the last item)
        int shown = 0, hidden = 0;
        String smallest = "";
        double smallestPrice = Double.POSITIVE_INFINITY;
        for (Good g : Good.values()) {
            for (double price : new double[] { g.worldImportPrice(), g.worldExportPrice(), g.baseFreight() }) {
                if (!(price > 0)) continue;
                shown++;
                String text = ham.citybuildersim.ui.Money.unitPrice(price);
                if (!text.matches(".*[1-9].*")) hidden++;
                if (price < smallestPrice) { smallestPrice = price; smallest = text; }
            }
        }
        assertTrue("ui.Money.unitPrice shows every one of the " + shown + " world prices with a digit (the smallest, " + smallest + ")", hidden == 0 && shown > 0);

        // consumption.json: the food prices, against the file read here and against Good
        Consumption c = new Consumption();
        assertTrue("consumption.json loads", c.load());
        JsonObject consumptionFile = readResource("/" + Consumption.FILE_NAME);
        boolean food = true, atGood = true;
        int foods = 0;
        for (JsonElement e : consumptionFile.getAsJsonArray("goods")) {
            JsonObject o = e.getAsJsonObject();
            Consumption.Item it = c.item(o.get("key").getAsString());
            if (it == null) { food = false; continue; }
            foods++;
            food &= gap(it.worldImportPrice(), o.get("worldImportPrice").getAsDouble() * PriceLevel.FACTOR) <= 1e-15
                    && gap(it.worldExportPrice(), o.get("worldExportPrice").getAsDouble() * PriceLevel.FACTOR) <= 1e-15;
            Good g = Good.byName(it.key());
            atGood &= g != null && it.worldImportPrice() == g.worldImportPrice() && it.worldExportPrice() == g.worldExportPrice();
        }
        assertTrue("consumption.json: its " + foods + " foods are the file's figures times the factor", food && foods >= 13);
        assertTrue("...to the bit what Good prices them at (ConsumptionCheck's equality)", atGood);

        // buildings.json: cashCost, upkeep, consumables, against the file read here
        JsonObject buildingsFile = readResource("/" + BuildingCatalog.FILE_NAME);
        List<BuildingsTemplate> loaded = new BuildingCatalog().load();
        assertTrue("buildings.json loads", loaded != null && !loaded.isEmpty());
        Map<Integer, BuildingsTemplate> byId = new LinkedHashMap<>();
        for (BuildingsTemplate t : loaded) byId.put(t.getId(), t);
        int costs = 0, upkeeps = 0, consumables = 0, onshores = 0;
        boolean cost = true, upkeep = true, cons = true, physical = true, onshore = true;
        for (JsonElement e : buildingsFile.getAsJsonArray("buildings")) {
            JsonObject o = e.getAsJsonObject();
            BuildingsTemplate t = byId.get(o.get("id").getAsInt());
            if (t == null) { cost = false; continue; }
            double rawCost = num(o, "cashCost"), rawUpkeep = num(o, "upkeep"), rawCons = num(o, "consumables");
            costs++;
            if (rawUpkeep != 0) upkeeps++;
            if (rawCons != 0) consumables++;
            cost &= gap(t.getCashCost(), rawCost * PriceLevel.FACTOR) <= 1e-15;
            upkeep &= gap(t.getUpkeep(), rawUpkeep * PriceLevel.FACTOR) <= 1e-15;
            cons &= gap(t.getConsumables(), rawCons * PriceLevel.FACTOR) <= 1e-15;
            // ...and the one money figure in a nested block: a pipeline's kilometre on land ("offshore": {"onshore": ...})
            double rawOnshore = o.has("offshore") && o.get("offshore").isJsonObject() ? num(o.getAsJsonObject("offshore"), "onshore") : 0;
            if (rawOnshore != 0) onshores++;
            onshore &= gap(t.onshoreCashPerKm(), rawOnshore * PriceLevel.FACTOR) <= 1e-15;
            physical &= t.getConstructionPoints() == (int) num(o, "constructionPoints")
                    && t.getConstructionMaterials() == (int) num(o, "constructionMaterials")
                    && t.getLandSqFt() == num(o, "land") && t.getCapacity() == (int) num(o, "capacity");
        }
        assertTrue("buildings.json: the " + costs + " buildings' cash costs are the file's times the factor", cost && costs == loaded.size());
        assertTrue("...the " + upkeeps + " upkeeps", upkeep && upkeeps > 0);
        assertTrue("...the " + consumables + " consumables", cons && consumables > 0);
        assertTrue("...and the " + onshores + " pipeline's kilometre on land", onshore && onshores > 0);
        assertTrue("...and the points, materials, land and capacity are the file's, untouched", physical);

        // the built-in set the data file is held to is priced the same way
        BuildingManager builtIn = new BuildingManager();
        builtIn.initializeBuiltInTemplates();
        BuildingsTemplate house = builtIn.getTemplateByName("House");
        assertTrue("the built-in definitions are priced at the founding's level too (the House, as the file's)",
                house != null && byId.get(house.getId()) != null && house.getCashCost() == byId.get(house.getId()).getCashCost());
    }

    static JsonObject readResource(String name) throws Exception {
        try (InputStream in = PriceLevelCheck.class.getResourceAsStream(name)) {
            return new Gson().fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), JsonObject.class);
        }
    }

    static double num(JsonObject o, String key) {
        return o.has(key) && o.get(key).isJsonPrimitive() ? o.get(key).getAsDouble() : 0;
    }

    /* =================================================================
       4. THE RANKINGS' BRIDGE
       ================================================================= */

    static void theBridge() {
        relative("RealDollar.BRIDGE is the 2015 index over the founding year's (" + String.format(Locale.ROOT, "%.2f", RealDollar.BRIDGE) + ")",
                RealDollar.BRIDGE, PriceLevel.usCpi(Rankings.BASE_YEAR) / PriceLevel.usCpi(CityCalendar.EPOCH_YEAR), 1e-15);
        assertTrue("...a founding dollar is worth more than a 2015 one, because 1900's dollar bought more", RealDollar.BRIDGE > 1);
        relative("a real 2015 dollar costs 1/BRIDGE of a founding one at the founding's rate (D$ per real US$)", RealDollar.dollarsPer2015Usd(1, 1) * RealDollar.BRIDGE, 1, 1e-15);
        relative("a month's GDP over the index and the founding rate is bridged into real 2015 dollars", RealDollar.realGdp2015Usd(1000, 1, 1), 1000 * RealDollar.BRIDGE, 1e-15);
        relative("the founding game dollar is BRIDGE real ones; the factor makes the 2025 literals 1/BRIDGE-th of that again (1900 -> 2015 -> 2025)",
                PriceLevel.FACTOR * RealDollar.BRIDGE, PriceLevel.usCpi(Rankings.BASE_YEAR) / PriceLevel.usCpi(PriceLevel.LAST_YEAR), 1e-15);
    }

    /* =================================================================
       5. THE NAMED LITERALS AND THE RATIOS
       ================================================================= */

    /** The nominal amounts struck as constants, each a class and a name: today's figure times the factor, in both children. */
    static final String[] NOMINAL_CONSTANTS = {
        "Game.FOUNDING_CASH", "Game.FOUNDING_RESERVE_USD", "Game.FIXED_ISSUE_COST", "Game.BUILD_NOTE_GRANULE", "Game.BUILD_BOND_GRANULE", "Game.MINIMUM_ISSUE_FLOOR",
        "Founding.LEAN_CASH", "Founding.LEAN_RESERVE_USD", "Founding.WEALTHY_CASH", "Founding.WEALTHY_RESERVE_USD",
        "Founding.MIN_CASH", "Founding.MAX_CASH", "Founding.MIN_RESERVE_USD", "Founding.MAX_RESERVE_USD",
        "Bank.PAID_IN_PER_BRANCH", "Bank.DOMESTIC_CAPITAL_SCALE", "Bank.ACCOUNT_FEE",
        "Healthcare.GENERAL_FEE", "Healthcare.CHILDCARE_FEE", "Healthcare.SENIOR_FEE", "Healthcare.BURIAL_FEE", "Healthcare.CREMATION_FEE",
        "LandManager.DEFAULT_PRICE_PER_SQ_FT", "LandMarket.BASE_PRICE_PER_SQ_FT", "LandMarket.PRICE_STEP_USD", "sectors.Retail.OPENING_SELL_PRICE",
        "TaxPolicy.DEFAULT_TRANSIT_FARE", "TaxPolicy.MAX_TRANSIT_FARE", "TaxPolicy.MAX_FUEL_DUTY",
        "Equity.FOUNDING_PRICE", "FundView.MIN_RECORDED_PRICE", "Exchange.MIN_DEALER_EQUITY", "ForeignAccounts.MIN_TRADE", "CapitalFlows.MIN_STOCK",
        "EconomyManager.NRRP_CAP", "EconomyManager.NRRP_FULL_BELOW", "EconomyManager.NRRP_NONE_FROM",
        "Motoring.CAR_FUEL_PER_JOURNEY", "sectors.Freight.WORLD_FUEL_PER_TONNE", "BuildingManager.MATERIALS_WORLD_PRICE",
        "SitePreparation.EARTH_USD_PER_M3", "SitePreparation.WALL_USD_PER_M2", "SitePreparation.USD_PER_M2",
        "RunWorks.DECK_USD_PER_M2", "RunWorks.TUNNEL_BORE_USD_PER_KM", "RunWorks.FOOTHILL_USD_PER_M2", "RunWorks.STEEP_USD_PER_M2", "RunWorks.VERY_STEEP_USD_PER_M2",
        "BridgeWorks.BASE_USD_PER_M2", "BridgeWorks.CONFEDERATION_USD_PER_M2",
        "FundView.FOLD_UNDER", "ui.Money.NOTHING",
    };

    static void theLiterals(Child plain, Child today) {
        int n = 0, bad = 0;
        for (String name : NOMINAL_CONSTANTS) {
            Double a = plain.consts.get(name), b = today.consts.get(name);
            n++;
            if (a == null || b == null) { bad++; out.println("   FAIL " + name + " was not read in a child"); continue; }
            double expected = b * PriceLevel.FACTOR;
            boolean ok = (b == 0 && a == 0) || gap(a, expected) <= CONSTANT_TOLERANCE;
            if (!ok) { bad++; out.printf("   FAIL %s = %.10g, today's %.10g times the factor is %.10g%n", name, a, b, expected); }
        }
        fails += bad;
        out.printf("%-86s %s%n", "each of the " + n + " named literals is today's figure times the factor (0 stays 0)", bad == 0 ? "OK" : "FAIL");
        int others = 0, off = 0;
        for (String name : plain.consts.keySet()) {
            if (!name.startsWith("tuition.") && !name.startsWith("wage.") && !name.startsWith("utility.") && !name.startsWith("good.")) continue;
            Double a = plain.consts.get(name), b = today.consts.get(name);
            others++;
            if (a == null || b == null || !((Double.isNaN(a) && Double.isNaN(b)) || gap(a, b * PriceLevel.FACTOR) <= CONSTANT_TOLERANCE)) {
                off++;
                out.printf("   FAIL %s = %s, today's %s times the factor is %s%n", name, a, b, b == null ? null : b * PriceLevel.FACTOR);
            }
        }
        fails += off;
        out.printf("%-86s %s%n", "...and the " + others + " more read the same way (the tuition table, the rungs, the water and the power, every good's prices)", off == 0 && others > 0 ? "OK" : "FAIL");
        if (others == 0) fails++;
    }

    static void theRatios(Child plain, Child today) {
        int n = 0, bad = 0;
        double worst = 0;
        String worstName = "";
        for (Map.Entry<String, Double> e : today.ratios.entrySet()) {
            Double a = plain.ratios.get(e.getKey());
            n++;
            double g = a == null ? Double.POSITIVE_INFINITY : gap(a, e.getValue());
            if (g > worst) { worst = g; worstName = e.getKey(); }
            if (!(g <= CONSTANT_TOLERANCE)) { bad++; out.printf("   FAIL %s: %.10g at the factor, %.10g at today's prices (%.3g apart)%n", e.getKey(), a == null ? Double.NaN : a, e.getValue(), g); }
        }
        fails += bad;
        out.printf("%-86s %s%n", "the " + n + " cross-file ratios (a wage over a food, tuition over a wage, ...) equal today's, worst " + String.format(Locale.ROOT, "%.2g", worst)
                + (worstName.isEmpty() ? "" : " in " + worstName), bad == 0 && n > 0 ? "OK" : "FAIL");
        if (n == 0) fails++;
    }

    /* =================================================================
       6. THE TWIN
       ================================================================= */

    static void theTwin(Child plain, Child today, int monthsRun) {
        assertTrue("the child at the factor ran with the factor (" + plain.factor + ")", plain.ok && gap(plain.factor, PriceLevel.FACTOR) <= 1e-15);
        assertTrue("the child at today's prices ran with 1", today.ok && today.factor == 1);
        if (!plain.ok || !today.ok) return;
        int months = monthsRun + 1;
        int checked = 0, nominalSeries = 0, realSeries = 0, exact = 0, failed = 0;
        double worstNominal = 0, worstReal = 0;
        String worstNominalName = "", worstRealName = "";
        int recordedSeries = 0, recordedOff = 0, lostToRounding = 0;
        List<String> lost = new ArrayList<>();
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, Series> e : today.series.entrySet()) {
            String name = e.getKey();
            Series b = e.getValue();
            Series a = plain.series.get(name);
            if (a == null) { failed++; lines.add("   FAIL " + name + " is missing from the child at the factor"); continue; }
            if (a.v.length != b.v.length) { failed++; lines.add("   FAIL " + name + " has " + a.v.length + " months at the factor and " + b.v.length + " at today's prices"); continue; }
            // Whether a series is money is read in the child at today's prices (b.kind): at the factor a small flow can round to zeros in the
            // history, and the model's own definition (HistorySave.redenominate) cannot tell a series of zeros from a count.
            if (a.kind != b.kind && b.kind == 'N' && allZero(a.v)) { lostToRounding++; if (lost.size() < 6) lost.add(name.substring(2)); }
            else if (a.kind != b.kind) { failed++; lines.add("   FAIL " + name + " is " + a.kind + " at the factor and " + b.kind + " at today's prices"); continue; }
            checked++;
            boolean nominal = b.kind == 'N';
            if (nominal) nominalSeries++; else realSeries++;
            boolean history = name.startsWith("h.");
            boolean recorded = nominal && history;
            double grid = history && !SIG_RECORDED.matcher(name).matches() ? gridOf(b.v) : 0;
            double tol = !nominal ? TWIN_REAL_TOLERANCE : recorded ? (grid > 0 ? Double.POSITIVE_INFINITY : HISTORY_SIG_TOLERANCE) : TWIN_NOMINAL_TOLERANCE;
            double worst = 0, scale = 0;
            int at = -1, flips = 0, firstFlip = -1;
            for (int m = 0; m < b.v.length; m++) {
                double x = nominal ? a.v[m] / plain.factor : a.v[m];
                double y = b.v[m];
                if (Double.isFinite(x)) scale = Math.max(scale, Math.abs(x));
                if (Double.isFinite(y)) scale = Math.max(scale, Math.abs(y));
                double g;
                if (nominal && Math.abs(x) < TWIN_MONEY_FLOOR && Math.abs(y) < TWIN_MONEY_FLOOR) g = 0;
                else if (!nominal && Math.abs(x) < TWIN_QUANTITY_FLOOR && Math.abs(y) < TWIN_QUANTITY_FLOOR) g = 0;
                else if (!nominal && history && grid > 0 && gap(x, y) > TWIN_REAL_TOLERANCE && Math.abs(x - y) <= grid * 1.0001) {
                    // a real series recorded to a grid, one step apart: a value on the middle of two steps rounded either way (TIE_FLIPS)
                    g = 0;
                    if (flips++ == 0) firstFlip = m;
                } else if (recorded && grid > 0) {
                    // on a grid: the gap in steps of the grid, scaled so that 1 is the most two roundings can give (half a step of each)
                    double allowed = .5 * grid * (1 / plain.factor + 1) * (1 + 1e-9) + TWIN_NOMINAL_TOLERANCE * Math.max(Math.abs(x), Math.abs(y));
                    g = Double.isNaN(x) != Double.isNaN(y) ? Double.POSITIVE_INFINITY : (Double.isNaN(x) ? 0 : Math.abs(x - y) / allowed);
                    if (g <= 1) g = 0;
                } else if (nominal && !recorded && scale > 0 && !Double.isNaN(x) && !Double.isNaN(y)) g = Math.abs(x - y) / scale;
                else g = gap(x, y);
                if (g > worst) { worst = g; at = m; }
            }
            if (recorded && grid > 0) tol = 0;
            if (flips > TIE_FLIPS) { worst = Math.max(worst, 1); at = firstFlip; }
            if (worst == 0) exact++;
            if (recorded) { recordedSeries++; if (worst > tol) recordedOff++; }
            else if (nominal && worst > worstNominal) { worstNominal = worst; worstNominalName = name; }
            if (!nominal && worst > worstReal) { worstReal = worst; worstRealName = name; }
            if (worst > tol) {
                failed++;
                if (lines.size() < 24) {
                    double x = nominal ? a.v[at] / plain.factor : a.v[at];
                    lines.add(String.format(Locale.ROOT, "   FAIL %s (%s) month %d: %.10g at the factor %s, %.10g at today's prices (%.3g apart)",
                            name, nominal ? "nominal" : "real", at, x, nominal ? "over the factor" : "", b.v[at], worst));
                }
            }
        }
        for (String name : plain.series.keySet()) if (!today.series.containsKey(name)) { failed++; lines.add("   FAIL " + name + " is missing from the child at today's prices"); }
        for (String l : lines) out.println(l);
        fails += failed;
        out.printf("%-86s %s%n", "fixture: " + checked + " series over " + months + " months (" + nominalSeries + " nominal, " + realSeries + " real), " + exact + " identical to the bit", checked > 200 ? "OK" : "FAIL");
        if (checked <= 200) fails++;
        out.printf("%-86s %s%n", "every real series equal within " + TWIN_REAL_TOLERANCE + " (worst " + String.format(Locale.ROOT, "%.2g", worstReal)
                + (worstRealName.isEmpty() ? "" : " in " + worstRealName) + ")", worstReal <= TWIN_REAL_TOLERANCE ? "OK" : "FAIL");
        if (lostToRounding > 0) out.println("   note: " + lostToRounding + " money series of the history are all zeros at the factor (recorded to cents of thousands, HistorySave.round2) and are not at today's prices: " + lost + "...");
        out.printf("%-86s %s%n", "the history's " + recordedSeries + " money series equal over the factor to the rounding they were recorded at", recordedOff == 0 ? "OK" : "FAIL");
        out.printf("%-86s %s%n", "every live nominal series equal over the factor within " + TWIN_NOMINAL_TOLERANCE + " (worst " + String.format(Locale.ROOT, "%.2g", worstNominal)
                + (worstNominalName.isEmpty() ? "" : " in " + worstNominalName) + ")", worstNominal <= TWIN_NOMINAL_TOLERANCE ? "OK" : "FAIL");
        // the people, to the person (a real count is equal to the bit, not within a band)
        for (String key : new String[] { "pop", "workforce", "homes", "stores" }) {
            Series a = plain.series.get(key), b = today.series.get(key);
            boolean same = a != null && b != null && java.util.Arrays.equals(a.v, b.v);
            assertTrue("...the " + key + " is the same to the person in every month", same);
        }
        assertTrue("...and the city really lived (more people at the end than the start)",
                today.series.get("pop") != null && today.series.get("pop").v[today.series.get("pop").v.length - 1] > today.series.get("pop").v[0]);
    }

    /** The history series recorded to six significant figures (HistorySave.roundSig) rather than to a grid: the share prices and the household-scale prices. */
    static final java.util.regex.Pattern SIG_RECORDED = java.util.regex.Pattern.compile(
            "h\\.(minimumWage|landPrice|foodPrice|materialsPrice|orePrice|rentPrice|sharePrice\\..*|shareValue\\..*)");

    static boolean allZero(double[] v) {
        for (double x : v) if (x != 0 && !Double.isNaN(x)) return false;
        return true;
    }

    /** The grid a recorded series sits on, 1, .01, 1e-4 or 1e-6, the coarsest every value is a multiple of; 0 for none (six significant figures) or a series of zeros. */
    static double gridOf(double[] v) {
        boolean any = false;
        for (double x : v) if (x != 0 && !Double.isNaN(x)) any = true;
        if (!any) return 0;
        for (int d : new int[] { 0, 2, 4, 6 }) {
            double scale = Math.pow(10, d);
            boolean on = true;
            for (double x : v) {
                if (Double.isNaN(x)) continue;
                double y = x * scale;
                if (Math.abs(y - Math.rint(y)) > 1e-6) { on = false; break; }
            }
            if (on) return 1 / scale;
        }
        return 0;
    }

    /* =================================================================
       10. THE SCREENS' CUT-OFFS
       ================================================================= */

    /**
     * What the screens do at a dollar cut-off (EP2: ui.Money.NOTHING says nothing of a figure under it, FundView.FOLD_UNDER folds a holding under it
     * into "and N more", TradeLanes.minLane() gives a partner under it no lane) is the same at the factor as at today's prices: the children counted
     * each month, with the model's own code, the partners given a lane, the holdings folded and shown and, for every live nominal series the digest
     * sampled, whether ui.Money.something() shows it. Section 8 is left out on purpose: main records it red, its twin parting at months 91 to 103 on
     * series of its own (the gate's PriceLevelCheck lines), and a count taken over those months would carry the parting and not the cut-off. 6 and 7
     * are green, and 9 is red on main by one series nine billionths apart (sector.Retail.cash, month 29), far inside any cut-off, so a count that
     * differs in any of the three is the cut-off's. None of the three trades much with any one partner: a lane is drawn in one month of their 216
     * (the town's), at either level, so the lanes' count is mostly zeros, and the floor's own figure at the factor is DenominationCheck's to assert.
     */
    static void theCutOffs(Child plain, Child today, Child builtPlain, Child builtToday, Child townPlain, Child townToday) {
        String[] cities = { "the default founding", "the built city", "the town" };
        Child[][] pairs = { { plain, today }, { builtPlain, builtToday }, { townPlain, townToday } };
        int lanesMonths = 0, lanesMost = 0, held = 0, heldMost = 0, foldedMost = 0, bandAtFactor = 0;
        for (int c = 0; c < pairs.length; c++) {
            Child a = pairs[c][0], b = pairs[c][1];
            String city = cities[c];
            boolean counted = a.ok && b.ok && !a.counts.isEmpty() && a.counts.keySet().equals(b.counts.keySet()) && a.counts.containsKey("lanes") && a.counts.containsKey("band");
            assertTrue(city + ": both children counted the cut-offs, the same rows (" + a.counts.size() + " at the factor, " + b.counts.size() + " at today's prices)", counted);
            if (!counted) continue;
            int months = a.counts.get("lanes").length;
            for (String row : new String[] { "lanes", "arrows", "holdings.folded", "holdings.shown" }) {
                List<String> first = new ArrayList<>();
                int off = unequal(a.counts.get(row), b.counts.get(row), first);
                String what = row.equals("lanes") ? "the partners given a lane (TradeLanes.of over the feed, at minLane())"
                        : row.equals("arrows") ? "the arrows drawn for a generated world's partners"
                        : row.equals("holdings.folded") ? "the holdings folded into \"and N more\" (FundView.FOLD_UNDER)" : "the holdings shown";
                assertTrue(city + ": " + what + " are the same at the factor and at today's prices every month (" + months + " months)", off == 0);
                for (String line : first) out.println("      " + row + " " + line);
            }
            double[] lanes = a.counts.get("lanes"), folded = a.counts.get("holdings.folded"), shown = a.counts.get("holdings.shown");
            int drew = 0, most = 0, heldHere = 0, heldAt = 0, foldedAt = 0;
            for (int m = 0; m < months; m++) {
                if (lanes[m] > 0) drew++;
                most = Math.max(most, (int) lanes[m]);
                heldHere = Math.max(heldHere, (int) (folded[m] + shown[m]));
                foldedAt = Math.max(foldedAt, (int) folded[m]);
                heldAt += folded[m] + shown[m] > 0 ? 1 : 0;
            }
            lanesMonths += drew;
            lanesMost = Math.max(lanesMost, most);
            held += heldAt;
            heldMost = Math.max(heldMost, heldHere);
            foldedMost = Math.max(foldedMost, foldedAt);
            out.printf("   %s: a lane drawn in %d of %d months (at most %d partners); the fund held something in %d months (at most %d holdings, %d folded)%n",
                    city, drew, months, most, heldAt, heldHere, foldedAt);

            // every line a screen gives a word or a line: shown at one level, shown at the other
            List<String> firstFlips = new ArrayList<>();
            int series = 0, flags = 0, flips = 0, shownFlags = 0;
            for (String row : a.counts.keySet()) {
                if (!row.startsWith("shown.")) continue;
                series++;
                double[] x = a.counts.get(row), y = b.counts.get(row);
                for (int m = 0; m < Math.min(x.length, y.length); m++) {
                    flags++;
                    if (x[m] != 0) shownFlags++;
                    if (x[m] != y[m]) {
                        flips++;
                        if (firstFlips.size() < 5) firstFlips.add(row.substring("shown.".length()) + " month " + m + ": " + (x[m] != 0 ? "shown" : "left out") + " at the factor, "
                                + (y[m] != 0 ? "shown" : "left out") + " at today's prices");
                    }
                }
            }
            assertTrue(city + ": every live nominal line is shown or left out alike at both levels, " + series + " series over " + months + " months (" + flips + " of "
                    + flags + " differ; " + shownFlags + " shown)", series > 0 && flips == 0);
            for (String line : firstFlips) out.println("      " + line);

            // the lines the old literal hid: at the factor a figure between the factor's cut-off and the half-thousand; at today's prices none can be
            int bandHere = 0, bandThere = 0;
            for (double v : a.counts.get("band")) bandHere += (int) v;
            for (double v : b.counts.get("band")) bandThere += (int) v;
            if (c == 0) bandAtFactor = bandHere;
            out.printf("   %s: lines shown that the old half-thousand would have hidden, %d series-months at the factor, %d at today's prices%n", city, bandHere, bandThere);
            assertTrue(city + ": at today's prices no line sits between ui.Money.NOTHING and the old half-thousand (it is the half-thousand)", bandThere == 0);
        }
        assertTrue("some partner is given a lane in some month of at least one of the two cities (the floor at the factor draws them): " + lanesMonths
                + " city-months, at most " + lanesMost + " partners", lanesMonths > 0);
        out.printf("   the fund: %s%n", held == 0 ? "held nothing in either city, so the fold is counted over no holding"
                : "held something in " + held + " city-months, at most " + heldMost + " holdings and " + foldedMost + " folded");
        assertTrue("the default founding shows lines at the factor that the old half-thousand would have hidden: " + bandAtFactor + " series-months", bandAtFactor > 0);
    }

    /**
     * How many months two rows part (a row of a different length parts in every month it does not share), and the first few as lines. A count is an
     * integer, so equal is equal.
     */
    static int unequal(double[] x, double[] y, List<String> first) {
        if (x == null || y == null) { first.add("is missing from one child"); return Integer.MAX_VALUE; }
        int off = 0;
        for (int m = 0; m < Math.max(x.length, y.length); m++) {
            if (m < x.length && m < y.length && x[m] == y[m]) continue;
            off++;
            if (first.size() < 5) first.add("month " + m + ": " + (m < x.length ? x[m] : Double.NaN) + " at the factor, " + (m < y.length ? y[m] : Double.NaN) + " at today's prices");
        }
        return off;
    }

    /* ================================================================== */

    /** One series of the digest: a month-by-month list of values and whether it is money (N) or not (R). */
    static final class Series {
        final char kind;
        final double[] v;
        Series(char kind, double[] v) { this.kind = kind; this.v = v; }
    }

    /** How long a child JVM may run before the check kills it and fails: the longest (the played city) takes a few minutes. */
    static final long CHILD_MINUTES = 10;

    /** What a child printed: its factor, the constants it read, the ratios, and the series. */
    static final class Child {
        double factor = Double.NaN;
        boolean ok;
        final Map<String, Double> consts = new LinkedHashMap<>();
        final Map<String, Double> ratios = new LinkedHashMap<>();
        final Map<String, Series> series = new LinkedHashMap<>();
        /** What the screens' cut-offs did each month (EP2, the K lines: Counts); kept apart from the series so that theTwin never sees them. */
        final Map<String, double[]> counts = new LinkedHashMap<>();

        /** A child JVM started as AllChecks starts a harness, with the one door open or not; one after the other, never at once. */
        static Child run(boolean todaysPrices, boolean built) throws Exception {
            return run(todaysPrices, built ? "--twin-built" : "--twin");
        }

        /** ...of the scenario the flag names: --twin, --twin-built, --twin-played. */
        static Child run(boolean todaysPrices, String flag) throws Exception {
            String javaExe = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            List<String> cmd = new ArrayList<>(AllChecks.command(javaExe, System.getProperty("java.class.path"), "PriceLevelCheck", true));
            int at = cmd.indexOf("-cp");
            if (todaysPrices) cmd.add(at, "-D" + DOOR + "=1");
            for (String key : new String[] { "cbs.earth", WorldData.PROPERTY }) {
                String v = System.getProperty(key);
                if (v != null) cmd.add(at, "-D" + key + "=" + v);
            }
            cmd.add(flag);
            out.println("   child " + (flag.equals("--twin") ? "" : "(" + flag.substring("--twin-".length()) + " city) ") + (todaysPrices ? "at today's prices" : "at the factor") + " ...");
            long t0 = System.nanoTime();
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            // The child's output goes to a file, not through a pipe this thread reads to the end: a read to the end returns only when the child
            // exits, so the wait below could never time out on a child that hangs (EP1, the ship gate's review).
            Path log = TempDirs.create("pricelevel-child").resolve((todaysPrices ? "today-" : "factor-") + flag.substring(2) + ".txt");
            pb.redirectOutput(log.toFile());
            Process p = pb.start();
            Child c = new Child();
            boolean done = p.waitFor(CHILD_MINUTES, TimeUnit.MINUTES);
            if (!done) {
                p.destroyForcibly();
                p.waitFor(1, TimeUnit.MINUTES);
                out.println("   FAIL the child (" + flag + ", " + (todaysPrices ? "at today's prices" : "at the factor") + ") did not finish in " + CHILD_MINUTES
                        + " minutes and was killed (its output so far: " + log + ")");
                fails++;
                return c;
            }
            int exit = p.exitValue();
            for (String line : new String(Files.readAllBytes(log), StandardCharsets.UTF_8).split("\\R")) c.read(line);
            out.printf("   child %s: exit %d, %d constants, %d ratios, %d series, %.0f s (its output: %s)%n", todaysPrices ? "at today's prices" : "at the factor",
                    exit, c.consts.size(), c.ratios.size(), c.series.size(), (System.nanoTime() - t0) / 1e9, log);
            if (exit != 0) { out.println("   FAIL the child exited " + exit); fails++; }
            c.ok = exit == 0 && !Double.isNaN(c.factor) && !c.series.isEmpty();
            return c;
        }

        void read(String line) {
            if (line.isEmpty()) return;
            String[] f = line.split("\t");
            switch (f[0]) {
                case "F" -> factor = Double.parseDouble(f[1]);
                case "C" -> consts.put(f[1], Double.parseDouble(f[2]));
                case "Q" -> ratios.put(f[1], Double.parseDouble(f[2]));
                case "S" -> {
                    String[] parts = f.length > 3 ? f[3].split(",") : new String[0];
                    double[] v = new double[parts.length];
                    for (int i = 0; i < v.length; i++) v[i] = Double.parseDouble(parts[i]);
                    series.put(f[1], new Series(f[2].charAt(0), v));
                }
                case "K" -> {
                    String[] parts = f.length > 2 ? f[2].split(",") : new String[0];
                    double[] v = new double[parts.length];
                    for (int i = 0; i < v.length; i++) v[i] = Double.parseDouble(parts[i]);
                    counts.put(f[1], v);
                }
                default -> { if (line.startsWith("E\t")) out.println("   child: " + line.substring(2)); }
            }
        }
    }

    /* =================================================================
       THE CHILD: found the default city, run it, print what happened
       ================================================================= */

    /** The city, run in a JVM of its own. Everything it prints that the parent reads is a line of tab-separated fields on the real stdout; the game's own println go to nothing. */
    static final class Twin {

        static void run(String flag) throws Exception {
            boolean built = flag.equals("--twin-built"), played = flag.equals("--twin-played"), town = flag.equals("--twin-town");
            PrintStream real = System.out;
            PrintStream sink = new PrintStream(OutputStream.nullOutputStream());
            System.setOut(sink);
            real.println("F\t" + PriceLevel.FACTOR);
            try {
                constants(real);
                ratios(real);
                int months = town ? TOWN_MONTHS : played ? PLAYED_MONTHS : built ? BUILT_MONTHS : TWIN_MONTHS;
                Digest d = new Digest(months + 1);
                Counts k = new Counts(months + 1);
                GameFiles townFiles = town ? GameFiles.scratch("pricelevel-town") : null;
                Game g = town ? townCity(sink, townFiles) : played ? playedCity(sink) : built ? builtCity() : foundedCity();
                d.sample(g, 0);
                k.sample(g, d, 0);
                for (int m = 1; m <= months; m++) {
                    if (town) townMonth(g, m);
                    else if (played) playedMonth(g, m - 1);
                    else g.simulateMonths(1);
                    d.sample(g, m);
                    k.sample(g, d, m);
                }
                d.history(g);
                if (town) {
                    // ScaleCheck reads the town as its own save loads it: the save, then the city that comes back, month by month the same figures
                    g.saveGame(10, "pricelevel town");
                    Game back = new Game(townFiles);
                    back.loadGameSave(10);
                    Digest r = new Digest(1);
                    r.prefix = "reload.";
                    r.sample(back, 0);
                    d.values.putAll(r.values);
                    d.kinds.putAll(r.kinds);
                }
                d.print(real);
                k.print(real);
                real.flush();
            } catch (Throwable t) {
                t.printStackTrace(real);
                real.flush();
                System.exit(2);
            }
            System.exit(0);
        }

        /** The default founding, as a player starts it. */
        static Game foundedCity() {
            Game g = new Game(GameFiles.scratch("pricelevel-twin"));
            g.run();
            return g;
        }

        /**
         * DenominationCheck's town (its buses variant): the Wealthy treasury and a build list that puts a bit of everything in the city, so the
         * anchors a default founding never reaches (tuition, fees, loans, a mine's ore, a furnace's consumables, the fare, the fuel) are read.
         * Its two fixture amounts are today's figures times the factor, as the model's are.
         */
        static Game builtCity() {
            Game g = new Game(GameFiles.scratch("pricelevel-built"));
            g.run();
            g.getForeignAccounts().pinRate(g.getForeignAccounts().getRate());
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.getGovernmentInvestor().spend(-2_000_000 * PriceLevel.FACTOR);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 200_000_000L);
            g.buildStack(g.getBuildingManager().getTemplateByName("Construction Depot"), 2, true);
            LongPlaytest.build(g, "House", 400);
            LongPlaytest.build(g, "Convenience Store", 20);
            LongPlaytest.build(g, "Construction Depot", 6);
            LongPlaytest.build(g, "Coal Power Plant", 2);
            LongPlaytest.build(g, "Water Treatment Plant", 2);
            LongPlaytest.build(g, "Industrial Bakery", 4);
            LongPlaytest.build(g, LongPlaytest.nameOf(g, ham.citybuildersim.sectors.Mining.IRON_MINE), 2);
            LongPlaytest.build(g, LongPlaytest.nameOf(g, ham.citybuildersim.sectors.HeavyIndustry.ARC_FURNACE), 2);
            LongPlaytest.build(g, "Commercial Bank", 1);
            LongPlaytest.build(g, "Elementary School", 3);
            LongPlaytest.build(g, "Walk-in Clinic", 3);
            LongPlaytest.build(g, "Paved Road", 20);
            BuildingsTemplate network = g.getBuildingManager().getTemplateByName("Bus Network");
            g.setCashForTest(g.getCash() + network.getCashCost());
            g.buildStack(network, 1, true);
            return g;
        }

        /**
         * ScaleCheck's founded town (its section 1): the playtest's founding, forty houses, three stores and two farms at the start, twenty houses
         * after three months, two stores and a depot after seven, to the month ScaleCheck saves it at (FIXTURE_MONTHS). Nothing in it is a money
         * figure of mine: the builds are the playtest's own, which buys its land and borrows as the model does.
         */
        static Game townCity(PrintStream sink, GameFiles files) {
            LongPlaytest.out = sink;
            Game g = new Game(files, LongPlaytest.founding());
            g.run();
            if (g.getMonth() != 1) throw new IllegalStateException("the town is meant to start in month 1, not " + g.getMonth());
            LongPlaytest.build(g, "House", 40);
            LongPlaytest.build(g, "Convenience Store", 3);
            LongPlaytest.build(g, "Mixed Farm", 2);
            return g;
        }

        /** One month of the town, ScaleCheck's script: month m is the m-th since the founding. */
        static void townMonth(Game g, int m) {
            g.simulateMonths(1);
            if (m == 3) LongPlaytest.build(g, "House", 20);
            if (m == 7) {
                LongPlaytest.build(g, "Convenience Store", 2);
                LongPlaytest.build(g, "Construction Depot", 1);
            }
        }

        /**
         * MapCheck's played city (its playedCity()): MiningCheck's iron world, the playtest's autopilot, rollover and fund dial, forty houses, three
         * stores and two farms. Nothing in it is a money figure of mine: every sum it reaches is the model's own.
         */
        static Game playedCity(PrintStream sink) {
            LongPlaytest.out = sink;
            Game g = new Game(GameFiles.scratch("pricelevel-played"), LongPlaytest.founding().withWorldSeed(MiningCheck.IRON_SEED));
            g.quietTheWorldForTheD();
            g.run();
            g.getDebtManager().setAutopilot(LongPlaytest.AUTOPILOT);
            g.setRolloverMode(LongPlaytest.ROLLOVER);
            g.setRescueMode(LongPlaytest.RESCUE_AUTO ? TreasuryFund.RescueMode.AUTOMATIC : TreasuryFund.RescueMode.BUTTON);
            g.setFundDial(LongPlaytest.FUND_DIAL);
            LongPlaytest.villageBuild(g, "House", 40);
            LongPlaytest.villageBuild(g, "Convenience Store", 3);
            LongPlaytest.villageBuild(g, "Mixed Farm", 2);
            return g;
        }

        /** One month of it, as MapCheck plays it: the advisor and the schools every sixth month, a demolition at the half, an iron mine a third in. */
        static void playedMonth(Game g, int m) {
            if (m % 6 == 5) {
                LongPlaytest.advise(g);
                LongPlaytest.ensureSchools(g);
            }
            if (m == PLAYED_DEMOLITION) {
                BuildingsTemplate house = g.getBuildingManager().getTemplateByName("House");
                if (house != null) g.getBuildingManager().retire(house, 10);
            }
            if (m == PLAYED_IRON && g.getLandManager().getIronDeposits() == 0) {
                MiningCheck.makeRoom(g, 0, true);
                BuildingsTemplate mine = g.getBuildingManager().getTemplate(ham.citybuildersim.sectors.Mining.IRON_MINE);
                g.setCashForTest(g.getCash() + g.getBusinessInvestment().getCostOf(mine, 1));
                LongPlaytest.build(g, mine.getName(), 1);
            }
            LongPlaytest.run(g, 1);
        }

        static double constant(String qualified) throws Exception {
            int dot = qualified.lastIndexOf('.');
            String cls = qualified.substring(0, dot);
            Field f = Class.forName("ham.citybuildersim." + cls).getDeclaredField(qualified.substring(dot + 1));
            f.setAccessible(true);
            return f.getDouble(null);
        }

        static void constants(PrintStream o) throws Exception {
            for (String name : NOMINAL_CONSTANTS) o.println("C\t" + name + "\t" + constant(name));
            for (PayTier t : PayTier.values()) o.println("C\twage." + t.name() + "\t" + t.getMonthlyWage());
            for (EducationType t : EducationType.values()) o.println("C\ttuition." + t.name() + "\t" + Education.foundingTuition(t));
            UtilitiesHandler u = new UtilitiesHandler();
            o.println("C\tutility.water\t" + u.getPricePerWaterUnit());
            o.println("C\tutility.power\t" + u.getPricerPerWatt());
            for (Good g : Good.values()) {
                o.println("C\tgood." + g.name() + ".import\t" + g.worldImportPrice());
                o.println("C\tgood." + g.name() + ".export\t" + g.worldExportPrice());
                o.println("C\tgood." + g.name() + ".freight\t" + g.baseFreight());
            }
        }

        /** Ratios between files: dimensionless, so today's and the factor's are one number (study 2.7, item 4). */
        static void ratios(PrintStream o) throws Exception {
            double wage = PayTier.UNSKILLED.getMonthlyWage();
            BuildingsTemplate house = null;
            for (BuildingsTemplate t : new BuildingCatalog().load()) if (t.getName().equals("House")) house = t;
            double houseCashPerPoint = house.getCashCost() / house.getConstructionPoints();
            double houseTotal = house.getCashCost() + house.getConstructionMaterials() * Good.MATERIALS.worldImportPrice();
            Consumption c = new Consumption();
            c.load();
            q(o, "unskilled wage / grains import price", wage / Good.GRAINS.worldImportPrice());
            q(o, "unskilled wage / meat import price", wage / Good.MEAT.worldImportPrice());
            q(o, "unskilled wage / consumption.json's grains price", wage / c.item("GRAINS").worldImportPrice());
            q(o, "unskilled wage / the cheapest calorie (a month of bare survival)", wage / c.subsistenceCostAtWorldPrices());
            q(o, "consumption.json's grains price / Good.GRAINS", c.item("GRAINS").worldImportPrice() / Good.GRAINS.worldImportPrice());
            q(o, "university tuition / skilled wage", Education.foundingTuition(EducationType.UNIVERSITY) / PayTier.SKILLED.getMonthlyWage());
            q(o, "elementary tuition / unskilled wage", Education.foundingTuition(EducationType.ELEMENTARY) / wage);
            q(o, "medical tuition / elite wage", Education.foundingTuition(EducationType.MEDICAL) / PayTier.ELITE.getMonthlyWage());
            q(o, "materials import price / House cash cost", Good.MATERIALS.worldImportPrice() / house.getCashCost());
            q(o, "BuildingManager.MATERIALS_WORLD_PRICE / Good.MATERIALS", BuildingManager.MATERIALS_WORLD_PRICE / Good.MATERIALS.worldImportPrice());
            q(o, "House cash cost / unskilled wage", house.getCashCost() / wage);
            q(o, "House cash a point / unskilled wage", houseCashPerPoint / wage);
            q(o, "land office's opening price / land market's base", constant("LandManager.DEFAULT_PRICE_PER_SQ_FT") / constant("LandMarket.BASE_PRICE_PER_SQ_FT"));
            q(o, "land market's base a sq ft / unskilled wage", constant("LandMarket.BASE_PRICE_PER_SQ_FT") / wage);
            q(o, "site preparation: points a m2 of foothill", SitePreparation.pointsPerM2(houseCashPerPoint));
            q(o, "run works: points a m2 of viaduct", RunWorks.DECK_USD_PER_M2 / 1000 / houseCashPerPoint);
            q(o, "run works: points a m2 of tunnel", RunWorks.VERY_STEEP_USD_PER_M2 / 1000 / houseCashPerPoint);
            q(o, "bridge works: points for a 20 m deep metre", BridgeWorks.points(BridgeWorks.deckUsdPerM2(20), houseCashPerPoint));
            q(o, "founding cash / unskilled wage", constant("Game.FOUNDING_CASH") / wage);
            q(o, "founding vault / founding cash", constant("Game.FOUNDING_RESERVE_USD") / constant("Game.FOUNDING_CASH"));
            q(o, "founding cash / House total", constant("Game.FOUNDING_CASH") / houseTotal);
            q(o, "minimum treasury / House total", constant("Founding.MIN_CASH") / houseTotal);
            q(o, "the new-rental rebate's full-below / a House's total", constant("EconomyManager.NRRP_FULL_BELOW") / houseTotal);
            q(o, "the new-rental rebate's cap / its none-from", constant("EconomyManager.NRRP_CAP") / constant("EconomyManager.NRRP_NONE_FROM"));
            q(o, "transit fare / unskilled wage", constant("TaxPolicy.DEFAULT_TRANSIT_FARE") / wage);
            q(o, "car fuel a journey / unskilled wage", constant("Motoring.CAR_FUEL_PER_JOURNEY") / wage);
            q(o, "shelf price / grains import price", constant("sectors.Retail.OPENING_SELL_PRICE") / Good.GRAINS.worldImportPrice());
            q(o, "account fee / unskilled wage", constant("Bank.ACCOUNT_FEE") / wage);
            q(o, "bank paid-in capital a branch / founding cash", constant("Bank.PAID_IN_PER_BRANCH") / constant("Game.FOUNDING_CASH"));
            q(o, "general care fee / unskilled wage", constant("Healthcare.GENERAL_FEE") / wage);
            q(o, "burial fee / unskilled wage", constant("Healthcare.BURIAL_FEE") / wage);
            q(o, "founding share / founding cash", constant("Equity.FOUNDING_PRICE") / constant("Game.FOUNDING_CASH"));
            UtilitiesHandler u = new UtilitiesHandler();
            q(o, "water a unit / unskilled wage", u.getPricePerWaterUnit() / wage);
            q(o, "power a kW / unskilled wage", u.getPricerPerWatt() / wage);
        }

        static void q(PrintStream o, String name, double v) { o.println("Q\t" + name + "\t" + v); }
    }

    /** The month-by-month record: a name, whether it is money, and a value for every month. */
    static final class Digest {
        final int months;
        final Map<String, Character> kinds = new LinkedHashMap<>();
        final Map<String, double[]> values = new LinkedHashMap<>();

        /** A prefix for every series name, so a second city's one month rides in the first's digest. */
        String prefix = "";

        Digest(int months) { this.months = months; }

        void put(String name, char kind, int m, double x) {
            name = prefix + name;
            double[] row = values.computeIfAbsent(name, k -> new double[months]);
            kinds.putIfAbsent(name, kind);
            row[m] = x;
        }

        void sample(Game g, int m) {
            put("cash", 'N', m, g.getCash());
            put("gdp.month", 'N', m, g.getEconomyManager().getMonthGdp());
            put("bank.deposits", 'N', m, g.getBank().getDeposits());
            put("retail.shelf", 'N', m, g.getSectors().retail().getStoreSellPrice());
            put("rent", 'N', m, g.getSectors().realEstate().getRentPrice());
            put("wage.minimum", 'N', m, g.getLabourMarket().getMinimumWage());
            put("rate", 'R', m, g.getForeignAccounts().getRate());
            put("world.level", 'R', m, g.getWorldEconomy().getPriceLevel());
            put("priceIndex", 'R', m, g.getPriceIndex().getIndex());
            put("pop", 'R', m, g.getPopulationManager().getPopulation());
            put("workforce", 'R', m, g.getPopulationManager().getWorkforce());
            put("unemployed", 'R', m, g.getPopulationManager().getUnemployed());
            put("homes", 'R', m, g.getBuildingManager().getTotalHomes());
            put("stores", 'R', m, g.getBuildingManager().getTotalStoreCoverage());
            long[] jobs = g.getBuildingManager().getTotalJobs();
            for (int i = 0; i < jobs.length; i++) put("jobs." + i, 'R', m, jobs[i]);
            for (GoodsMarket k : g.getMarkets().all()) {
                String n = "mkt." + k.good().name() + ".";
                put(n + "supply", 'R', m, k.getSupply());
                put(n + "demand", 'R', m, k.getDemand());
                put(n + "imported", 'R', m, k.getImported());
                put(n + "exported", 'R', m, k.getExported());
                put(n + "local", 'N', m, k.getLocalPrice());
                put(n + "import", 'N', m, k.importPrice());
                put(n + "export", 'N', m, k.exportPrice());
                double stock = 0;
                for (Sector s : g.getSectors().all()) stock += s.getStock(k.good());
                put("stock." + k.good().name(), 'R', m, stock);
            }
            for (Sector s : g.getSectors().all()) put("sector." + s.key() + ".cash", 'N', m, s.getCash());
        }

        /**
         * The history the game keeps, every series of it, classified by the model's own definition of money: the series HistorySave.redenominate()
         * divides are money, but for the four rates it divides by the unit (the D$ per US$ is a ratio of two moneys and a price level moves neither);
         * and the three it leaves because they are in US dollars (the vault, the foreign debt and the ground's price) are money in the world's.
         */
        void history(Game g) throws Exception {
            HistorySave h = g.getHistorySave();
            HistorySave probe = h.snapshot();
            probe.redenominate(.5);
            java.util.Set<String> rates = java.util.Set.of("fxRate", "fxParity", "fxFair", "fxShadow");
            java.util.Set<String> usd = java.util.Set.of("reservesUsd", "foreignDebtUsd", "landPrice");
            for (Field f : HistorySave.class.getDeclaredFields()) {
                if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
                f.setAccessible(true);
                Object now = f.get(h), after = f.get(probe);
                if (now instanceof List<?> l && after instanceof List<?> l2) {
                    if (l.isEmpty() || !hasNumber(l)) continue;
                    boolean halved = halved(l, l2);
                    char kind = (halved && !rates.contains(f.getName())) || usd.contains(f.getName()) ? 'N' : 'R';
                    addList("h." + f.getName(), kind, l);
                } else if (now instanceof Map<?, ?> m1 && after instanceof Map<?, ?> m2 && isListMap(f)) {
                    for (Map.Entry<?, ?> e : m1.entrySet()) {
                        Object other = m2.get(e.getKey());
                        if (!(e.getValue() instanceof List<?> l) || !(other instanceof List<?> l2)) continue;
                        addList("h." + f.getName() + "." + e.getKey(), halved(l, l2) ? 'N' : 'R', l);
                    }
                }
            }
        }

        static boolean hasNumber(List<?> l) { for (Object o : l) if (o instanceof Number) return true; return false; }

        static boolean isListMap(Field f) {
            Type t = f.getGenericType();
            return t instanceof ParameterizedType p && p.getActualTypeArguments().length == 2 && p.getActualTypeArguments()[1].getTypeName().startsWith("java.util.List<java.lang.Double");
        }

        /** Whether redenominating by one half halved the series: at least one nonzero entry, and every nonzero one halved. */
        static boolean halved(List<?> before, List<?> after) {
            boolean any = false;
            for (int i = 0; i < Math.min(before.size(), after.size()); i++) {
                if (!(before.get(i) instanceof Number a) || !(after.get(i) instanceof Number b)) continue;
                if (a.doubleValue() == 0) continue;
                if (Math.abs(b.doubleValue() * 2 - a.doubleValue()) > 1e-9 * Math.abs(a.doubleValue())) return false;
                any = true;
            }
            return any;
        }

        void addList(String name, char kind, List<?> l) {
            double[] row = new double[l.size()];
            boolean numbers = false;
            for (int i = 0; i < row.length; i++) {
                Object o = l.get(i);
                if (o instanceof Number n) { row[i] = n.doubleValue(); numbers = true; } else row[i] = Double.NaN;
            }
            if (!numbers) return;
            values.put(name, row);
            kinds.put(name, kind);
        }

        void print(PrintStream o) {
            StringBuilder b = new StringBuilder();
            for (Map.Entry<String, double[]> e : values.entrySet()) {
                b.setLength(0);
                b.append("S\t").append(e.getKey()).append('\t').append(kinds.get(e.getKey())).append('\t');
                double[] row = e.getValue();
                for (int i = 0; i < row.length; i++) { if (i > 0) b.append(','); b.append(row[i]); }
                o.println(b);
            }
        }
    }

    /**
     * What the screens' cut-offs did to the city each month (EP2): counts the model's own code gives, a row of one per month for each, printed as K
     * lines for section 10. Never a copy of a formula: the lanes are TradeLanes' own over the feed the map is given, the holdings are FundView's own,
     * the lines shown are ui.Money.something()'s own over the series the digest sampled the same month.
     */
    static final class Counts {
        final int months;
        final Map<String, double[]> values = new LinkedHashMap<>();
        NationsMap map;
        boolean mapTried;

        Counts(int months) { this.months = months; }

        void put(String name, int m, double x) { values.computeIfAbsent(name, key -> new double[months])[m] = x; }

        /** The nations' map of the world file, built once the first time it is wanted (as MapView builds it); null where there is no file or it will not open. */
        NationsMap map() {
            if (!mapTried) {
                mapTried = true;
                java.nio.file.Path file = WorldData.locate();
                if (file != null) {
                    try {
                        map = NationsMap.of(WorldData.open(file));
                    } catch (java.io.IOException | RuntimeException e) {
                        map = null;
                    }
                }
            }
            return map;
        }

        void sample(Game g, Digest d, int m) {
            lanes(g, m);
            // holdings, as FundScreen folds them: under FOLD_UNDER goes into "and N more"
            int folded = 0, shown = 0;
            for (FundView.Position p : FundView.positions(g)) {
                if (p.value() < FundView.FOLD_UNDER) folded++;
                else shown++;
            }
            put("holdings.folded", m, folded);
            put("holdings.shown", m, shown);
            // the lines shown: every live nominal series the digest sampled this month (the history is recorded rounded, so not those)
            int band = 0;
            for (Map.Entry<String, Character> e : d.kinds.entrySet()) {
                String name = e.getKey();
                if (e.getValue() != 'N' || name.startsWith("h.")) continue;
                double v = d.values.get(name)[m];
                boolean shows = ham.citybuildersim.ui.Money.something(v);
                put("shown." + name, m, shows ? 1 : 0);
                if (shows && Math.abs(v) < OLD_CUT) band++;
            }
            put("band", m, band);
        }

        /**
         * The partners given a lane this month, built as ui/MapView.lanesNow() builds them: TradeLanes.of over the feed's exports and imports by
         * nation and its minLane(), at the city's place (the Earth's key where the map draws on it, else a generated world's stand-in the feed
         * names; MapView draws arrows for that, TradeLanes.Arrows.of over the same figures, counted in "arrows"). Where the child cannot get a
         * NationsMap (no world file) the same call is made over finite stand-in nodes spread over the globe; only the floor matters to the count.
         */
        void lanes(Game g, int m) {
            TradeLanes.Feed f = g.getTradeFeed();
            double[] none = new double[0];
            double[] ex = f.exportsByNation() == null ? none : f.exportsByNation(), im = f.importsByNation() == null ? none : f.importsByNation();
            World.Key key = g.getCityLand().key();
            boolean stands = false;
            double lat = 0, lon = 0;
            if (NationsMap.drawsOn(key)) {
                lat = key.lat();
                lon = EarthProj.normaliseLon(key.lon());
            } else if (Double.isFinite(f.standInLat()) && Double.isFinite(f.standInLon())) {
                lat = f.standInLat();
                lon = f.standInLon();
                stands = true;
            }
            NationsMap nm = map();
            int lanes, arrows = 0;
            if (nm != null) {
                lanes = TradeLanes.of(nm, lat, lon, f.month(), ex, im, f.minLane()).partners();
                if (stands) arrows = TradeLanes.Arrows.of(nm, lat, lon, f.month(), ex, im, f.minLane()).count();
            } else {
                int n = Math.max(ex.length, im.length);
                double[] nodeLat = new double[n], nodeLon = new double[n];
                for (int i = 0; i < n; i++) {
                    nodeLat[i] = -60 + (i * 37) % 120;
                    nodeLon[i] = -170 + (i * 53) % 340;
                }
                lanes = TradeLanes.of(lat, lon, f.month(), nodeLat, nodeLon, ex, im, f.minLane()).partners();
                if (stands) arrows = TradeLanes.Arrows.of(lat, lon, f.month(), nodeLat, nodeLon, ex, im, f.minLane()).count();
            }
            put("lanes", m, lanes);
            put("arrows", m, arrows);
        }

        void print(PrintStream o) {
            StringBuilder b = new StringBuilder();
            for (Map.Entry<String, double[]> e : values.entrySet()) {
                b.setLength(0);
                b.append("K\t").append(e.getKey()).append('\t');
                double[] row = e.getValue();
                for (int i = 0; i < row.length; i++) { if (i > 0) b.append(','); b.append(row[i]); }
                o.println(b);
            }
        }
    }
}

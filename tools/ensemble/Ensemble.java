package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The inflation ensemble's driver: one seeded city, founded and played the way
 * LongPlaytest founds and plays it, under one of five monetary policies, written
 * out as one CSV row a month.
 *
 * WHY (P1b, 2026-10-04). The roadmap "inflation that answers to money" judges
 * 0.7.42 and 0.7.43 against five policies over sixteen seeds. One seed is the
 * weather; sixteen seeds under one fixed driver are the climate. This file is
 * that fixed driver. It lives in tools/ensemble/, outside the game's sources
 * (tools/ensemble/ensemble.sh compiles it against a snapshot of target/classes)
 * and it must keep compiling as the model changes, so
 * every figure it writes is read through the Adapter at the bottom, by name and
 * by reflection: a getter a later tree renames or removes reads NaN, never a
 * compile error, and a getter a later tree adds is one line in columns().
 *
 * usage:  java ... ham.citybuildersim.Ensemble <POLICY> <seed> <out.csv>
 *   POLICY  AUTO2 | ZERO_SLACK | ZERO_HOT | SHOCK | HIGH   (see THE FIVE POLICIES)
 *   seed    0..15, LongPlaytest's ensemble seed (-Dplaytest.seed): a few houses
 *           more, a month or two longer in the founding
 * optional:
 *   -Densemble.months=N       stop after month N (a timing pilot, or a cut)
 *   -Densemble.hotStart=M     ZERO_HOT's first month at 0% (default HOT_START)
 *   -Densemble.slackStart=M   ZERO_SLACK's first month at 0% (default SLACK_START)
 *   -Densemble.extra=a=p;b=q  extra columns, each a dotted getter path off Game,
 *                             e.g. expected=getCentralBank.getExpectedInflation
 *
 * What it does NOT do that LongPlaytest does: the save/reload round trip every
 * seventh stop (it tests the save, not the economy; it costs time and disk), the
 * report and the traces. Everything else - the founding, the advisor, the skip
 * rhythm, the tax fiddles, the borrowing, the land, the demolitions, the player's
 * answer to a failed bank and to its offer, and the audit of every month - is
 * LongPlaytest's own code, called rather than copied where it can be.
 */
public class Ensemble {

    /* =====================================================================
       THE FIVE POLICIES (roadmap-inflation.md). Every policy starts on the
       autopilot at a 2% target from the founding; the months below are
       months already run (g.getMonth()) when the setting is applied for the
       month about to run. ★ marks the harness's own judgement calls.
       ===================================================================== */

    enum Policy { AUTO2, ZERO_SLACK, ZERO_HOT, SHOCK, HIGH }

    /** The autopilot's target, every policy, every month it is on. */
    static final double TARGET = .02;
    /** AUTO2: the autopilot for this many months. */
    static final int AUTO2_MONTHS = 2400;
    /** ★ A mature city: where SHOCK and HIGH change policy (and ZERO_SLACK did until 0.7.100). */
    static final int MATURE = 600;
    /**
     * ★ ZERO_SLACK: the first month at 0%, in a mature city (0.7.100, batch P1; Jerus's A27 of
     * 2026-10-10). Until 0.7.100 it was MATURE, and month 600 is a city at takeoff, not a slack
     * one: on 0.7.43 its twin's unemployment was 14.9% at the switch and about 5% within the
     * window while its population grew x2.44 in 240 months (the 0.7.44 diagnosis's star 1). The
     * inflation spec's section 8 names the month for slack: "use city2400-like mature seeds or
     * month 1,800" (runs/spec-inflation.md), and every 0.7.99 AUTO2 seed has taken off (40,000
     * people) by month 1,243. The same hold from 1,800 was measured on 0.7.43 (out/diag-slack1800).
     */
    static final int SLACK_START = Integer.getInteger("ensemble.slackStart", 1800);
    /** ZERO_SLACK: 0% held this long after SLACK_START. */
    static final int SLACK_ZERO_MONTHS = 240;
    /**
     * ★ ZERO_HOT: the first month at 0%, in a young city's growth phase. Measured on 0.7.41's
     * AUTO2 seeds: the city runs hottest in its first building boom - unemployment under 5%,
     * rent up about 45%, the autopilot's own rate above 5% from month 45-77 (to 9-29% at the
     * peak) and above 5% again on and off to month 291. Month 42 is the first month after the
     * index has its first twelve-month rate (41), so the autopilot has had nothing to lean
     * against yet, and the 240 months at 0% cover every seed's early months above 5% but the
     * last few (to month 291).
     * (Month 120, the brief's example, starts after every seed's first month above 5% and
     * after the peak in 10 of 16.)
     */
    static final int HOT_START = Integer.getInteger("ensemble.hotStart", 42);
    /** ZERO_HOT: 0% held this long... */
    static final int HOT_ZERO_MONTHS = 240;
    /** ...then ★ the autopilot back on for this long, to see the spiral stop. */
    static final int HOT_STOP_MONTHS = 120;
    /** ★ SHOCK: the world's price level times this, once, at MATURE (an import-price shock). */
    static final double SHOCK_SIZE = 1.3;
    /** SHOCK: months run after it. */
    static final int SHOCK_AFTER = 240;
    /** ★ HIGH: the rate held after MATURE. */
    static final double HIGH_RATE = .15;
    /** HIGH: held this long. */
    static final int HIGH_MONTHS = 120;

    /** ★ A city has taken off once its population has reached this (LongPlaytest has no takeoff test of its own). */
    static final int TAKEOFF_POP = Integer.getInteger("ensemble.takeoffPop", 40_000);

    /** The month a policy's run ends. */
    static int end(Policy p) {
        return switch (p) {
            case AUTO2 -> AUTO2_MONTHS;
            case ZERO_SLACK -> SLACK_START + SLACK_ZERO_MONTHS;
            case ZERO_HOT -> HOT_START + HOT_ZERO_MONTHS + HOT_STOP_MONTHS;
            case SHOCK -> MATURE + SHOCK_AFTER;
            case HIGH -> MATURE + HIGH_MONTHS;
        };
    }

    /** The rate the policy holds for the month after month m, or NaN for the autopilot. */
    static double heldRate(Policy p, int m) {
        return switch (p) {
            case AUTO2, SHOCK -> Double.NaN;
            case ZERO_SLACK -> m >= SLACK_START ? 0 : Double.NaN;
            case ZERO_HOT -> m >= HOT_START && m < HOT_START + HOT_ZERO_MONTHS ? 0 : Double.NaN;
            case HIGH -> m >= MATURE ? HIGH_RATE : Double.NaN;
        };
    }

    /** The phase the month after month m runs in, for the scorer: pre, zero, stop, shock, high, auto. */
    static String phase(Policy p, int m) {
        return switch (p) {
            case AUTO2 -> "auto";
            case ZERO_SLACK -> m >= SLACK_START ? "zero" : "pre";
            case ZERO_HOT -> m < HOT_START ? "pre" : m < HOT_START + HOT_ZERO_MONTHS ? "zero" : "stop";
            case SHOCK -> m >= MATURE ? "shock" : "pre";
            case HIGH -> m >= MATURE ? "high" : "pre";
        };
    }

    static Policy policy;
    static boolean shocked;

    /** Sets the dial for the month about to run - the Policy screen's own setters. */
    static void applyPolicy(Game g) {
        int m = g.getMonth();
        DebtManager dm = g.getDebtManager();
        double r = heldRate(policy, m);
        if (Double.isNaN(r)) {
            if (dm.getInflationTarget() != TARGET) dm.setInflationTarget(TARGET);
            if (!dm.isAutopilot()) dm.setAutopilot(true);
        } else if (dm.isAutopilot() || dm.getPolicyRate() != r) {
            // The advisor nudges a hand-held dial between looks; the hand takes it back.
            dm.takeTheDial(r);
        }
        if (policy == Policy.SHOCK && !shocked && m >= MATURE) {
            shockWorld = Adapter.shockWorldLevel(g, SHOCK_SIZE);
            shocked = true;
        }
    }

    static double shockWorld = Double.NaN;

    /* =====================================================================
       THE COLUMNS - the one place a figure is read. A later version adds a
       column here (or through -Densemble.extra, no edit at all). Every read
       goes through Adapter.num(), which returns NaN for a getter this tree
       does not have.
       ===================================================================== */

    static double peakPop;

    static LinkedHashMap<String, Object> columns(Game g, String phase, int flagsNew, String newNames) {
        LinkedHashMap<String, Object> c = new LinkedHashMap<>();
        double pop = Adapter.num(g, "getPopulationManager.getPopulation");
        peakPop = Math.max(peakPop, Double.isNaN(pop) ? 0 : pop);
        boolean hasRate = Adapter.num(g, "getPriceIndex.hasRate") == 1;

        c.put("month", (double) g.getMonth());
        c.put("phase", phase);
        c.put("pop", pop);
        c.put("peak_pop", peakPop);
        c.put("took_off", peakPop >= TAKEOFF_POP ? 1.0 : 0.0);
        // the price index and its year
        c.put("index", Adapter.num(g, "getPriceIndex.getIndex"));
        c.put("infl_yoy", hasRate ? Adapter.num(g, "getPriceIndex.inflation") : Double.NaN);
        c.put("food_weight", Adapter.num(g, "getPriceIndex.getFoodWeight"));
        // the dial
        c.put("policy_rate", Adapter.num(g, "getDebtManager.getPolicyRate"));
        c.put("target", Adapter.num(g, "getDebtManager.getInflationTarget"));
        c.put("autopilot", Adapter.num(g, "getDebtManager.isAutopilot"));
        c.put("deposit_rate", Adapter.num(g, "getBank.depositRate"));
        // the currency
        c.put("fx", Adapter.num(g, "getForeignAccounts.getRate"));
        c.put("parity", Adapter.num(g, "getForeignAccounts.getParity"));
        c.put("world_level", Adapter.num(g, "getWorldEconomy.getPriceLevel"));
        // work
        c.put("avg_wage", Adapter.num(g, "getHouseholds.getAverageWage"));
        c.put("unemployment", Adapter.num(g, "getPopulationManager.getUnemploymentRate"));
        // money and output
        c.put("m0", Adapter.num(g, "getCentralBank.m0"));
        c.put("m2", Adapter.num(g, "getM2"));
        c.put("hh_deposits", Adapter.num(g, "getHouseholdDeposits"));
        c.put("gdp_month", Adapter.num(g, "getEconomyManager.getMonthGdp"));
        // the shelf and rent
        c.put("shelf", Adapter.num(g, "getSectors.retail.getStoreSellPrice"));
        c.put("shelf_floor", Adapter.shelfFloor(g));
        c.put("coverage", Adapter.num(g, "getSectors.retail.getStoreCoverage"));
        c.put("wanted", Adapter.num(g, "getSectors.retail.getWantedDemand"));
        c.put("hh_want", Adapter.num(g, "getSectors.retail.getHouseholdWant"));
        c.put("delivered", Adapter.num(g, "getSectors.retail.getDeliveredShare"));
        c.put("rent", Adapter.num(g, "getSectors.realEstate.getAverageRentPaid"));
        c.put("hunger", Adapter.num(g, "getHouseholdBalance.getHungerRate"));
        // the treasury and the bank
        c.put("treasury_cash", Adapter.num(g, "getCash"));
        c.put("treasury_debt", Adapter.num(g, "getDebtManager.getAllPrincipal"));
        c.put("cb_advances", Adapter.num(g, "getCentralBank.getAdvancesToTreasury"));
        c.put("bank_capital_ratio", Adapter.num(g, "getBank.capitalRatio"));
        // the checks
        double residual = Adapter.auditResidual(g);
        c.put("audit_ok", Double.isNaN(residual) ? Double.NaN : residual == 0 ? 1.0 : 0.0);
        c.put("audit_residual", residual);
        c.put("findings_new", (double) flagsNew);
        c.put("findings_distinct", (double) Adapter.findingNames().size());
        // what 0.7.42/0.7.43 are expected to add: NaN until a tree has one of the candidates
        for (Map.Entry<String, String[]> e : Adapter.FUTURE.entrySet()) {
            c.put(e.getKey(), Adapter.first(g, e.getValue()));
        }
        for (Map.Entry<String, String> e : Adapter.EXTRA.entrySet()) {
            c.put(e.getKey(), Adapter.num(g, e.getValue()));
        }
        c.put("shock_world", policy == Policy.SHOCK && g.getMonth() == MATURE + 1 ? shockWorld : Double.NaN);
        c.put("finding", newNames);
        return c;
    }

    /* =====================================================================
       RUNNING - LongPlaytest's own month (run(g, 1): the month, the player's
       answer to a failed bank and its offer, the audit) with the policy set
       before it and a row written after it.
       ===================================================================== */

    static PrintStream real = System.out;
    static final PrintStream quiet = new PrintStream(new OutputStream() {
        @Override public void write(int b) { }
        @Override public void write(byte[] b, int off, int len) { }
    });
    static PrintWriter csv;
    static boolean header;
    static int endMonth;
    static boolean stuck;
    static long started;

    static void run(Game g, int months) {
        for (int i = 0; i < months && !stuck && g.getMonth() < endMonth; i++) step(g);
    }

    static void step(Game g) {
        int before = g.getMonth();
        String ph = phase(policy, before);
        applyPolicy(g);
        int flagsBefore = Adapter.findingCount();
        List<String> namesBefore = new ArrayList<>(Adapter.findingNames());
        // System.out is already the quiet stream (foundAndPlay); progress goes to `real`.
        LongPlaytest.run(g, 1);
        if (g.getMonth() == before) stuck = true;
        StringBuilder names = new StringBuilder();
        for (String n : Adapter.findingNames()) {
            if (!namesBefore.contains(n)) names.append(names.length() > 0 ? " | " : "").append(n);
        }
        write(columns(g, ph, Adapter.findingCount() - flagsBefore, names.toString()));
        if (g.getMonth() % 120 == 0) {
            real.printf("  %s seed %d month %d pop %.0f index %.4f rate %.2f%% (%.0fs)%n", policy,
                    seed, g.getMonth(), Adapter.num(g, "getPopulationManager.getPopulation"),
                    Adapter.num(g, "getPriceIndex.getIndex"), 100 * Adapter.num(g, "getDebtManager.getPolicyRate"),
                    (System.currentTimeMillis() - started) / 1000.0);
        }
    }

    static void write(LinkedHashMap<String, Object> row) {
        if (!header) {
            csv.println(String.join(",", row.keySet()));
            header = true;
        }
        StringBuilder b = new StringBuilder();
        for (Object v : row.values()) {
            if (b.length() > 0) b.append(',');
            if (v instanceof Double d) {
                if (!Double.isNaN(d)) b.append(Double.isInfinite(d) ? (d > 0 ? "inf" : "-inf")
                        : String.format(Locale.ROOT, "%.9g", d));
            } else if (v != null) {
                b.append('"').append(v.toString().replace('"', '\'')).append('"');
            }
        }
        csv.println(b);
    }

    static int seed;

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.ROOT);
        policy = Policy.valueOf(args[0].toUpperCase(Locale.ROOT));
        seed = Integer.parseInt(args[1]);
        Path outCsv = Paths.get(args[2]);
        endMonth = Math.min(end(policy), Integer.getInteger("ensemble.months", Integer.MAX_VALUE));
        // LongPlaytest's founding reads its seed from this property.
        System.setProperty("playtest.seed", Integer.toString(seed));
        Adapter.parseExtra(System.getProperty("ensemble.extra"));
        Adapter.quietLongPlaytest(quiet);

        Path root = Files.createTempDirectory("ensemble");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        started = System.currentTimeMillis();
        Game g;
        System.setOut(quiet);
        try {
            g = new Game(files, LongPlaytest.founding());
            g.run();
        } finally {
            System.setOut(real);
        }
        String threw = null;
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(outCsv))) {
            csv = w;
            try {
                foundAndPlay(g, files);
            } catch (Throwable t) {
                threw = t.toString();
                System.setOut(real);
                LinkedHashMap<String, Object> last = columns(g, "threw", 1, "THREW: " + t);
                write(last);
            }
        }
        double secs = (System.currentTimeMillis() - started) / 1000.0;
        real.printf("DONE %s seed %d months %d of %d in %.1fs%s%n", policy, seed, g.getMonth(), endMonth, secs,
                threw == null ? (stuck ? " (stuck)" : "") : " THREW " + threw);
        LongPlaytest.cleanUp(root);
    }

    /* =====================================================================
       THE FOUNDING AND THE RHYTHM - LongPlaytest.main's, line for line, with
       the policy's setups in place of the playtest flags and run() the one
       above. Keep it in step with LongPlaytest.main when that changes.
       ===================================================================== */

    static void foundAndPlay(Game g, GameFiles files) {
        System.setOut(quiet);
        try {
            // THE DIAL'S HAND AND THE ROLLOVER, STATED: the setups LongPlaytest
            // makes at the founding - here the autopilot at 2%, every policy.
            g.getDebtManager().setInflationTarget(TARGET);
            g.getDebtManager().setAutopilot(true);
            g.setRolloverMode(LongPlaytest.ROLLOVER);
            g.setRescueMode(LongPlaytest.RESCUE_AUTO ? TreasuryFund.RescueMode.AUTOMATIC : TreasuryFund.RescueMode.BUTTON);
            g.setFundDial(LongPlaytest.FUND_DIAL);

            if (LongPlaytest.FOUNDING == Founding.Preset.INSANE) LongPlaytest.borrowForTheVillage(g);
            LongPlaytest.villageBuild(g, "House", 40 + seed % 4);
            LongPlaytest.villageBuild(g, "Convenience Store", 3);
            LongPlaytest.villageBuild(g, "Mixed Farm", 2);
            run(g, 3 + (seed / 4) % 3);
            LongPlaytest.villageBuild(g, "House", 20 + (seed / 12) % 3);
            run(g, 4);
            LongPlaytest.villageBuild(g, "Convenience Store", 2);
            LongPlaytest.villageBuild(g, "Construction Depot", 1);
            run(g, 5);
            LongPlaytest.advise(g);
            run(g, 6);
            LongPlaytest.ensureSchools(g);

            int stop = 0;
            while (g.getMonth() < endMonth && !stuck) {
                stop++;
                int skip = switch (stop % 6) {
                    case 0 -> 100;
                    case 1 -> 12;
                    case 2 -> 24;
                    case 3 -> 60;
                    case 4 -> 6;
                    default -> 120;
                };
                skip = Math.min(skip, LongPlaytest.longestSkip());
                run(g, skip);
                if (g.getMonth() >= endMonth || stuck) break;
                LongPlaytest.ensureSchools(g);

                for (int move = 0; move < LongPlaytest.movesPerLook(); move++) {
                    if (LongPlaytest.advise(g) == null) break;
                    run(g, 1);
                }
                run(g, 2);

                if (stop % 11 == 0) {
                    TaxPolicy tax = g.getEconomyManager().getTaxPolicy();
                    double income = tax.getIncomeTaxRate();
                    tax.setIncomeTaxRate(income > .25 ? .12 : income + .06);
                }
                if (stop % 17 == 0) {
                    TaxPolicy tax = g.getEconomyManager().getTaxPolicy();
                    double prop = tax.getPropertyTaxRate();
                    tax.setPropertyTaxRate(prop > .03 ? .01 : prop + .01);
                }
                if (stop % 19 == 0) {
                    DebtManager m = g.getDebtManager();
                    if (m.foreignWindowOpen() && m.foreignRate() < m.getRate()) {
                        double ask = Math.max(5_000, g.getEconomyManager().getMonthGdp() * .5
                                / Math.max(.01, LongPlaytest.fxRate(g)));
                        if (LongPlaytest.BORROW_AT_HOME) {
                            g.handleLongBondLogic(ask * Math.max(.01, LongPlaytest.fxRate(g)), 20, 100);
                        } else {
                            g.handleForeignLogic("Term", ask, 20, 100, false);
                        }
                    }
                }
                if (stop % 13 == 0) {
                    LandParcel spare = g.getLandManager().getMarket().bestValue();
                    if (spare != null && spare.localPrice(g.getForeignAccounts().getRate()) < g.getCash() * .1) {
                        g.buyLandParcel(spare.getId());
                    }
                }
                if (stop % 19 == 0) {
                    BuildingsTemplate house = LongPlaytest.template(g, "House");
                    if (house != null && g.getBuildingManager().getQuantity(house.getId()) > 60) {
                        g.getBuildingManager().retire(house, 10);
                    }
                }
                // (LongPlaytest's save/reload round trip every 7th stop is left out: see the class comment.)
            }
        } finally {
            System.setOut(real);
        }
    }

    /* =====================================================================
       THE ADAPTER - every read that could change between versions.

       num(root, "a.b.c") calls a(), then b() on what that returned, then c(),
       each a no-argument method (or, failing that, a field) found by name,
       public or not; the result is a double (a boolean reads 1 or 0). A name
       this tree does not have, a null on the way or a throw reads NaN - so a
       driver compiled against 0.7.43 still compiles and runs, and a column a
       tree has not grown yet is empty in the CSV rather than a build failure.

       Three reads are more than one getter and are kept here so the version
       logic is in one place: the shelf's floor, the money audit's verdict and
       the import shock. And LongPlaytest's findings map, read by reflection so
       a change in its Finding record does not break the build.
       ===================================================================== */

    static final class Adapter {

        /** Columns 0.7.42/0.7.43 are expected to add, each with the getter paths it might be found at; the first that exists wins. Edit a line here when the name is known. */
        static final LinkedHashMap<String, String[]> FUTURE = new LinkedHashMap<>();
        static {
            FUTURE.put("expected_infl", new String[] {
                    "getCentralBank.getExpectedInflation", "getDebtManager.getExpectedInflation",
                    "getPriceIndex.getExpectedInflation", "getLabourMarket.getExpectedInflation",
                    "getExpectedInflation" });
            FUTURE.put("credibility", new String[] {
                    "getCentralBank.getCredibility", "getDebtManager.getCredibility", "getCredibility" });
            FUTURE.put("shelf_clearing", new String[] {
                    "getSectors.retail.getClearingPrice", "getSectors.retail.getClearingSellPrice" });
            FUTURE.put("rent_clearing", new String[] {
                    "getSectors.realEstate.getClearingRent", "getSectors.realEstate.getClearingPrice" });
        }

        /** -Densemble.extra columns, name to path. */
        static final LinkedHashMap<String, String> EXTRA = new LinkedHashMap<>();

        static void parseExtra(String spec) {
            if (spec == null || spec.isBlank()) return;
            for (String part : spec.split(";")) {
                int eq = part.indexOf('=');
                if (eq > 0) EXTRA.put(part.substring(0, eq).trim(), part.substring(eq + 1).trim());
            }
        }

        private static final Map<String, Object> CACHE = new HashMap<>();
        private static final Object MISSING = new Object();

        /** A no-argument method, else a field, by name on this class or a superclass; MISSING if neither. */
        private static Object member(Class<?> type, String name) {
            String key = type.getName() + "#" + name;
            Object hit = CACHE.get(key);
            if (hit != null) return hit;
            Object found = MISSING;
            for (Class<?> c = type; c != null && found == MISSING; c = c.getSuperclass()) {
                try {
                    Method m = c.getDeclaredMethod(name);
                    m.setAccessible(true);
                    found = m;
                } catch (NoSuchMethodException | RuntimeException e) {
                    try {
                        Field f = c.getDeclaredField(name);
                        f.setAccessible(true);
                        found = f;
                    } catch (NoSuchFieldException | RuntimeException e2) { /* next class up */ }
                }
            }
            CACHE.put(key, found);
            return found;
        }

        /** Walks a dotted path off root; null when any step is missing or throws. */
        static Object get(Object root, String path) {
            Object at = root;
            for (String name : path.split("\\.")) {
                if (at == null) return null;
                Object m = member(at.getClass(), name);
                try {
                    if (m instanceof Method method) at = method.invoke(at);
                    else if (m instanceof Field field) at = field.get(at);
                    else return null;
                } catch (ReflectiveOperationException | RuntimeException e) {
                    return null;
                }
            }
            return at;
        }

        static double num(Object root, String path) {
            Object v = get(root, path);
            if (v instanceof Number n) return n.doubleValue();
            if (v instanceof Boolean b) return b ? 1 : 0;
            return Double.NaN;
        }

        /** The first of several paths this tree has. */
        static double first(Object root, String[] paths) {
            for (String p : paths) {
                double v = num(root, p);
                if (!Double.isNaN(v)) return v;
            }
            return Double.NaN;
        }

        /** A static field's number, by class and name; NaN if this tree has no such field. */
        static double staticNum(String className, String field) {
            try {
                Field f = Class.forName(className).getDeclaredField(field);
                f.setAccessible(true);
                Object v = f.get(null);
                return v instanceof Number n ? n.doubleValue() : Double.NaN;
            } catch (ReflectiveOperationException | RuntimeException e) {
                return Double.NaN;
            }
        }

        /**
         * THE SHELF'S FLOOR: a getter if the tree has one (0.7.43 is expected to
         * make cost-plus the floor under a clearing price), else 0.7.41's own
         * arithmetic in Retail.repriceShelf - the opening price or the food
         * basket's cost times RETAIL_MARKUP, whichever is higher.
         */
        static double shelfFloor(Game g) {
            double named = first(g, new String[] {
                    "getSectors.retail.getShelfFloor", "getSectors.retail.getCostFloor",
                    "getSectors.retail.getFloorPrice" });
            if (!Double.isNaN(named)) return named;
            double opening = num(g, "getSectors.retail.getOpeningSellPrice");
            double cost = num(g, "getSectors.retail.getFoodPrice");
            double markup = staticNum("ham.citybuildersim.sectors.Retail", "RETAIL_MARKUP");
            return Math.max(opening, cost * markup);
        }

        /**
         * THE MONEY AUDIT'S VERDICT, LongPlaytest.audit()'s own test: a month's
         * residual over a cent and over 1e-7 of what moved, or money that moved
         * after the audit struck, is a failure. Returns 0 for a clean month,
         * the residual (or the drift) otherwise, NaN if the tree has no audit.
         */
        static double auditResidual(Game g) {
            Object result = get(g, "getLastMoneyAudit");
            if (result == null) return Double.NaN;
            double residual = num(result, "residual");
            double relative = num(result, "relative");
            double drift = num(g, "getPostAuditDrift");
            if (Math.abs(residual) > .01 && relative > 1e-7) return residual;
            if (Math.abs(drift) > .01) return drift;
            return 0;
        }

        /**
         * THE IMPORT SHOCK: the world's price level times `size`, once, through
         * WorldEconomy's own public save and restore - the only door the level
         * has. Only the level moves: the thirteen-month ring stays, so the
         * world's realised inflation reads the jump for a year, and the trend
         * pull (TREND_PULL) takes it back over decades. Returns the new level,
         * NaN if this tree's world has no such door.
         */
        static double shockWorldLevel(Game g, double size) {
            Object world = get(g, "getWorldEconomy");
            if (world == null) return Double.NaN;
            try {
                Method save = world.getClass().getMethod("toSaveArray");
                Method restore = world.getClass().getMethod("restore", double[].class);
                double[] s = (double[]) save.invoke(world);
                s[0] *= size;
                restore.invoke(world, (Object) s);
                return num(world, "getPriceLevel");
            } catch (ReflectiveOperationException | RuntimeException e) {
                return Double.NaN;
            }
        }

        /** LongPlaytest's findings, by name. */
        @SuppressWarnings("unchecked")
        static java.util.Set<String> findingNames() {
            Object map = staticObject("ham.citybuildersim.LongPlaytest", "findings");
            return map instanceof Map<?, ?> m ? (java.util.Set<String>) m.keySet() : java.util.Set.of();
        }

        /** Every flag LongPlaytest has raised, summed over its findings (each finding's count). */
        static int findingCount() {
            Object map = staticObject("ham.citybuildersim.LongPlaytest", "findings");
            if (!(map instanceof Map<?, ?> m)) return 0;
            int sum = 0;
            for (Object f : m.values()) {
                double n = num(f, "count");
                sum += Double.isNaN(n) ? 1 : (int) n;
            }
            return sum;
        }

        /** LongPlaytest's report stream, pointed at nothing: its audit prints under some flags. */
        static void quietLongPlaytest(PrintStream quiet) {
            try {
                Field f = Class.forName("ham.citybuildersim.LongPlaytest").getDeclaredField("out");
                f.setAccessible(true);
                f.set(null, quiet);
            } catch (ReflectiveOperationException | RuntimeException e) { /* nothing to quiet */ }
        }

        private static Object staticObject(String className, String field) {
            try {
                Field f = Class.forName(className).getDeclaredField(field);
                f.setAccessible(true);
                return f.get(null);
            } catch (ReflectiveOperationException | RuntimeException e) {
                return null;
            }
        }
    }
}

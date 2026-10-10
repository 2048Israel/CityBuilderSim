package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Automatic building (0.7.73, batch N4): AutoBuilder's month held to its
 * rules, in a city it builds for decades and in fixtures that cause each of
 * the things that hold it back.
 *
 * WHY. Jerus: "an automatic build and acquire debt button for basically
 * automatic building, with a required slack button that you add, aka
 * maintain say 15% surplus service of everything ... right in the build
 * menu, and on/off, so that one can focus on other things." A switch that
 * spends the treasury and borrows by itself every month has to be shown to
 * keep its word over a city's life, not on one order: what it builds, what
 * it borrows, and that a saved city plays on as the one it was saved from.
 *
 * What this has to prove:
 *   1. THE SETTINGS: a new city has it off, at a 15% margin and a debt
 *      limit of 60% of a year's GDP (0.7.81; 15% of revenue until then),
 *      "Build from cash anyway" off; the sliders keep to their steps and
 *      their ends; switching it, moving a slider and the toggle are
 *      decisions; off, a month's pass does nothing; a save from before it
 *      loads with it off at its defaults, and one from 0.7.73 to 0.7.80 - its
 *      limit a share of revenue - with the default limit and the toggle off.
 *   2. THE ADVICE'S CARD: the first order a pass places is the build
 *      advice's card for that measure at the player's margin - its building
 *      and its count - where nothing cuts it.
 *   3. A CITY ON IT FOR DECADES, played by the test player with the city's
 *      works left to it (-Dplaytest.autobuild's player): after every pass
 *      every service it keeps is at or above the margin's target, or has
 *      works under way, or the pass said why not - and each reason it gave
 *      is true; it builds nothing outside its remit; it buys ground (0.7.77;
 *      none until then) only for its own orders - each short of ground when
 *      it was weighed, placed, of a building in its remit - bare ground only,
 *      the city's ground grown in a pass by exactly what they bought; it
 *      never borrows past the debt limit - the city's debt over a year of
 *      GDP after every pass that borrowed at or under it (0.7.81; debt
 *      payments over revenue until then), the ground's price in what it
 *      borrowed - and a pass that begins over it orders nothing and borrows
 *      nothing; nor spends the cash under a month's tax; and a staffed
 *      order fits what the budget leaves.
 *   4. A SAVED CITY PLAYS ON AS THE ONE IT WAS SAVED FROM: the city at the
 *      end of section 3, saved and loaded, and both run on with it on - the
 *      cash, the debt, every building and site, the ground it owns and what
 *      it bought, the people and its own log, month by month, to the cent.
 *   5. WHAT HOLDS IT BACK, CAUSED: a debt limit of nothing, a limit that
 *      binds and one that does not, a budget that cannot run a building, and
 *      a first police station a town does not need - each one held as it
 *      says, and the inbox's notice raised while it holds and settled when
 *      it is switched off; and (0.7.81) THE LIMIT, A SHARE OF GDP: a bond's
 *      face is what it adds to the debt; under the limit a bond that would
 *      cross it is not taken; a city over it builds nothing and borrows
 *      nothing with the cash to pay, the inbox saying why and naming the
 *      toggle; with "Build from cash anyway" on it builds what the cash pays
 *      for and borrows nothing, and with the cash at a month's tax holds for
 *      the cash; and (0.7.77) THE GROUND IT BUYS: with none free it
 *      buys the offer Build's land shortcut offers for the shortfall and
 *      places the order, the inbox noting the purchase and why; its ground
 *      and order borrowed for within the limit, and none bought at a limit
 *      of nothing, nor over the limit (0.7.81) - unless the toggle is on and
 *      the cash pays for both; no field bought for its ore, where the
 *      shortcut would buy one; and with no offer at all, held, the inbox
 *      naming the land office.
 *
 * Every fixture causes its condition.
 */
public class AutoBuildCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-110s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** Two money figures, in thousands, equal to the cent. */
    static void cents(String label, double actual, double expected) {
        boolean ok = Math.abs(actual - expected) <= .00001;
        if (!ok) fails++;
        out.printf("%-110s %s  %.5f against %.5f%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        Path root = Files.createTempDirectory("autobuildcheck");
        // ChildcareCheck's town is the fixture for sections 2 and 5: its quiet stream too.
        ChildcareCheck.out = quiet;
        ChildcareCheck.quiet = quiet;

        theSettings(root);
        theAdvicesCard(root);
        Game decades = decades(root);
        savedAndLoaded(root, decades);
        groundSavedAndLoaded(root);
        whatHoldsIt(root);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ---------------------------------------------------------------- 1 */

    static void theSettings(Path root) {
        out.println("--- 1. the settings ---");
        Game g = new Game(new GameFiles(root.resolve("new"), root.resolve("new-no-legacy")));
        quietly(g::newGame);
        AutoBuilder ab = g.getAutoBuilder();
        assertTrue("a new city has it off", !ab.isOn());
        assertTrue("...its spare margin at DEFAULT_SLACK, 15%, its debt limit at DEFAULT_DEBT_LIMIT, 60% of a year's GDP"
                        + " (0.7.81; 15% of revenue until then), and Build from cash anyway off",
                ab.getSlack() == AutoBuilder.DEFAULT_SLACK && ab.getDebtLimit() == AutoBuilder.DEFAULT_DEBT_LIMIT
                        && AutoBuilder.DEFAULT_SLACK == .15 && AutoBuilder.DEFAULT_DEBT_LIMIT == .60 && !ab.isCashAnyway());
        int decided = g.getDecisions().size();
        ab.setSlack(.987, g.getDecisions());
        boolean top = ab.getSlack() == AutoBuilder.SLACK_MOST;
        ab.setSlack(-.3, g.getDecisions());
        boolean bottom = ab.getSlack() == 0;
        ab.setSlack(.1234, g.getDecisions());
        boolean step = Math.abs(ab.getSlack() - .12) < 1e-12;
        assertTrue("the margin's slider keeps to its ends, 0 and SLACK_MOST, and to whole per cents", top && bottom && step);
        ab.setDebtLimit(9.87, g.getDecisions());
        boolean limitTop = ab.getDebtLimit() == AutoBuilder.DEBT_LIMIT_MOST && AutoBuilder.DEBT_LIMIT_MOST == 3.0;
        ab.setDebtLimit(-1, g.getDecisions());
        boolean limitBottom = ab.getDebtLimit() == 0;
        ab.setDebtLimit(1.234, g.getDecisions());
        assertTrue("...the debt limit's to 0 and DEBT_LIMIT_MOST, three years of GDP, and DEBT_STEP's five per cents (0.7.81;"
                + " the Finances tab's \"constrained\" line of debt payments, and whole per cents, until then)",
                limitTop && limitBottom && Math.abs(ab.getDebtLimit() - 1.25) < 1e-12 && AutoBuilder.DEBT_STEP == .05);
        ab.setCashAnyway(true, g.getDecisions());
        boolean anyway = ab.isCashAnyway();
        ab.setCashAnyway(false, g.getDecisions());
        ab.setOn(true, g.getDecisions());
        DecisionLog.Entry last = g.getDecisions().last();
        assertTrue("switching it, moving a slider and Build from cash anyway are decisions, in the construction kind",
                anyway && !ab.isCashAnyway() && g.getDecisions().size() == decided + 9 && last != null
                        && DecisionLog.CONSTRUCTION.equals(last.kind()) && last.label().startsWith("Automatic building on"));
        ab.setOn(false, g.getDecisions());
        int months = 12;
        quietly(() -> g.simulateMonths(months));
        assertTrue("off, " + months + " months' passes order nothing and take no step",
                ab.getOrders() == 0 && ab.log().isEmpty() && ab.steps().isEmpty() && ab.getLastPass() == 0);
        AutoBuilder older = new AutoBuilder();
        older.setOn(true, null);
        older.setSlack(.3, null);
        older.setCashAnyway(true, null);
        older.restore(null);
        assertTrue("a save from before it (no state) loads with it off, at its defaults, with nothing done",
                !older.isOn() && older.getSlack() == AutoBuilder.DEFAULT_SLACK && !older.isCashAnyway()
                        && older.getDebtLimit() == AutoBuilder.DEFAULT_DEBT_LIMIT && older.getOrders() == 0);
        // A save from 0.7.73 to 0.7.80: its limit a share of revenue under "debtLimit", no debtToGdp, no toggle.
        AutoBuilder was = new AutoBuilder();
        was.setDebtLimit(2, null);
        was.setCashAnyway(true, null);
        was.restore(new com.google.gson.Gson().fromJson("{\"on\":true,\"slack\":0.2,\"debtLimit\":0.15,\"orders\":3}",
                AutoBuilder.State.class));
        assertTrue("one from 0.7.73 to 0.7.80 keeps its switch and margin, and reads the default limit and the toggle off:"
                        + " its 15% was of revenue, not of GDP",
                was.isOn() && Math.abs(was.getSlack() - .2) < 1e-12 && was.getOrders() == 3
                        && was.getDebtLimit() == AutoBuilder.DEFAULT_DEBT_LIMIT && !was.isCashAnyway());
    }

    /* ---------------------------------------------------------------- 2 */

    static void theAdvicesCard(Path root) {
        out.println("\n--- 2. the advice's card ---");
        Game g = ChildcareCheck.town(root, "card");
        AutoBuilder ab = g.getAutoBuilder();
        ab.setOn(true, null);
        ab.setSlack(BuildAdvice.SLACK, null);
        assertTrue(String.format("fixture: the town within the debt limit (0.7.81) - %.0f%% of a year's GDP against %.0f%%",
                AutoBuilder.ratio(g) * 100, ab.getDebtLimit() * 100), ab.within(g));
        BuildAdvice.Suggestion[] first = new BuildAdvice.Suggestion[1];
        int[] expected = new int[1];
        g.autoBuildProbeForTest = after -> {
            if (after || first[0] != null) return;
            // What the pass will see first: the first measure it keeps that the advice has a card for.
            List<CityNeeds.Need> all = CityNeeds.measure(g, CityNeeds.PLAIN);
            for (BuildAdvice.Measure m : AutoBuilder.remit(g, all)) {
                if (BuildAdvice.ahead(g, m, BuildAdvice.onSite(g, m), BuildAdvice.opening(g, 0, ab.getSlack()))) continue;
                BuildAdvice.Suggestion s = BuildAdvice.suggestFor(g, AutoBuilder.rowFor(all, m), m, g.getCash(),
                        g.getLandManager().getAvailableSqFt(), ab.getSlack());
                if (s == null) continue;
                first[0] = s;
                // The builders' rule, worked here: the most that opens inside MAX_ORDER_MONTHS, one when none would.
                int k = s.count();
                while (k > 1 && AutoBuilder.wait(g, s, k) > BusinessInvestment.MAX_ORDER_MONTHS) k--;
                expected[0] = k;
                return;
            }
        };
        quietly(() -> g.simulateMonths(1));
        g.autoBuildProbeForTest = null;
        AutoBuilder.Step placed = null;
        for (AutoBuilder.Step s : ab.steps()) if (s.outcome() == AutoBuilder.Outcome.ORDERED) { placed = s; break; }
        assertTrue("fixture: the town is short of something the advice has a card for, at the advice's own margin (SLACK)",
                first[0] != null);
        assertTrue("fixture: ...a building the budget does not run, and ground for all of it", first[0] != null
                && !AutoBuilder.staffedBuilding(first[0].template()) && (first[0].paving()
                || first[0].template().getLandSqFt() * (double) first[0].count() <= g.getLandManager().getAvailableSqFt()));
        if (first[0] != null && placed != null) {
            assertTrue(String.format("the pass's first order is the card's building, %s for %s, and its count, %d - cut by"
                            + " the builders' rule to the %d that open inside a year", first[0].template().getName(),
                            first[0].measure().label(), first[0].count(), expected[0]),
                    placed.measure().equals(first[0].measure()) && placed.building().equals(first[0].template().getName())
                            && placed.wanted() == first[0].count() && placed.ordered() == expected[0]);
        } else {
            assertTrue("the pass's first order is the card", false);
        }
    }

    /* ---------------------------------------------------------------- 3 */

    /** What a pass found, measure by measure, over the run. */
    static int passes, kept, atTarget, works, held, nothing, unexplained, borrowingPasses, ordered;
    static int outsideRemit, underReserve, overBudget, overLimit, falseHolds;
    /** The ground it bought (0.7.77): passes the city's ground grew in, those not by exactly what its orders bought, orders that bought ground, those not its own short orders, offers holding ore, passes that borrowed with ground bought. */
    static int landPasses, landMismatch, landOrders, landNotItsOwn, oreBought, landBorrowPasses;
    static double landSqFt, landCost;
    /** The passes that held a kept service short of its target with nothing on site, by the cut that held it. */
    static final Map<AutoBuilder.Cut, Integer> heldBy = new LinkedHashMap<>();
    /** The buildings it ordered over the run, by measure. */
    static final Map<String, Integer> builtBy = new LinkedHashMap<>();
    /** The city's debt over a year of GDP after the passes that borrowed, the worst (0.7.81); passes that began over the limit, and the orders and bonds placed in them. */
    static double worstShare = 0;
    static int overPasses, overOrders, overBonds;
    static double mostOver = 0;

    static Game decades(Path root) {
        out.println("\n--- 3. a city on it for decades ---");
        int months = 600;
        Path dir = root.resolve("decades");
        GameFiles files = new GameFiles(dir.resolve("data"), dir.resolve("no-legacy"));
        PrintStream was = LongPlaytest.out;
        boolean flag = LongPlaytest.AUTOBUILD;
        LongPlaytest.out = quiet;
        LongPlaytest.AUTOBUILD = true;
        Game g = new Game(files, LongPlaytest.founding());
        AutoBuilder ab = g.getAutoBuilder();
        int[][] before = new int[1][];
        double[] snap = new double[5];      // land owned, cash, reserve, the debt over GDP, bonds placed
        g.autoBuildProbeForTest = after -> {
            if (!ab.isOn()) return;
            if (!after) {
                before[0] = standingAndOnSite(g);
                snap[0] = g.getLandManager().getOwnedSqFt();
                snap[1] = g.getCash();
                snap[2] = AutoBuilder.reserve(g);
                snap[3] = AutoBuilder.ratio(g);
                snap[4] = ab.getBonds();
                return;
            }
            look(g, ab, before[0], snap);
        };
        try {
            quietly(() -> {
                g.run();
                g.getDebtManager().setAutopilot(LongPlaytest.AUTOPILOT);
                g.setRolloverMode(LongPlaytest.ROLLOVER);
                g.setRescueMode(TreasuryFund.RescueMode.AUTOMATIC);
                ab.setOn(true, g.getDecisions());
                // The test player's founding, by hand, and its rhythm after (LongPlaytest.main()).
                LongPlaytest.villageBuild(g, "House", 40);
                LongPlaytest.villageBuild(g, "Convenience Store", 3);
                LongPlaytest.villageBuild(g, "Mixed Farm", 2);
                LongPlaytest.run(g, 3);
                LongPlaytest.villageBuild(g, "House", 20);
                LongPlaytest.run(g, 4);
                LongPlaytest.villageBuild(g, "Convenience Store", 2);
                LongPlaytest.villageBuild(g, "Construction Depot", 1);
                LongPlaytest.run(g, 5);
                LongPlaytest.advise(g);
                LongPlaytest.run(g, 6);
                int stop = 0;
                while (g.getMonth() < months) {
                    stop++;
                    int skip = switch (stop % 6) { case 0 -> 100; case 1 -> 12; case 2 -> 24; case 3 -> 60; case 4 -> 6; default -> 120; };
                    LongPlaytest.run(g, Math.min(skip, months - g.getMonth()));
                    if (g.getMonth() >= months) break;
                    for (int move = 0; move < LongPlaytest.movesPerLook(); move++) {
                        if (LongPlaytest.advise(g) == null) break;
                        LongPlaytest.run(g, 1);
                    }
                    LongPlaytest.run(g, 2);
                }
            });
        } finally {
            g.autoBuildProbeForTest = null;
            LongPlaytest.AUTOBUILD = flag;
            LongPlaytest.out = was;
        }
        out.printf("    (%d months, %,d people at the end; %d passes, %d orders: %s)%n", g.getMonth(),
                g.getPopulationManager().getPopulation(), passes, ordered, builtBy);
        out.printf("    (after each pass, of %d services kept: %d at the target, %d with works under way, %d held %s,"
                + " %d with nothing to build)%n", kept, atTarget, works, held, heldBy, nothing);
        assertTrue("fixture: it ran a pass every month and placed orders for at least three services",
                passes >= months - 1 && builtBy.size() >= 3);
        assertTrue("fixture: it borrowed, on the funding page's bond, at least once", borrowingPasses > 0);
        assertTrue("after every pass every service it keeps is at the margin's target, has works under way, or the"
                + " pass said why not (" + unexplained + " otherwise)", unexplained == 0);
        assertTrue("...and every reason it gave was true: no ground for one, a bond for one past the limit, a budget"
                + " short of one's running, no site output (" + falseHolds + " not)", falseHolds == 0);
        assertTrue(String.format("...and most of the time at the target itself: %.1f%% of the services kept, the"
                + " rest works under way or held", 100.0 * atTarget / Math.max(1, kept)), atTarget * 2 > kept);
        assertTrue("it builds nothing outside its remit (" + outsideRemit + " other buildings rose in a pass)", outsideRemit == 0);
        out.printf("    (ground bought: %d orders in %d passes, %s for %s)%n", landOrders, landPasses,
                LandManager.areaWords(landSqFt), DecisionLog.money(landCost));
        assertTrue("fixture: it bought ground for its orders", landOrders > 0);
        assertTrue(String.format("it buys ground only for its own held orders: each placed, of a building in its remit, short of ground"
                + " when weighed and covered by what it bought (%d of %d not)", landNotItsOwn, landOrders), landNotItsOwn == 0);
        assertTrue(String.format("...the city's ground grown in a pass by exactly the ground its orders bought (%d of %d passes not)",
                landMismatch, landPasses), landMismatch == 0);
        assertTrue("...and bare ground only: no field bought for its ore (" + oreBought + " offers held ore)", oreBought == 0);
        assertTrue(String.format("it never borrows past the debt limit, the ground's price in what it borrowed: %d passes borrowed"
                + " (%d of them buying ground), the city's debt after them at most %.2f%% of a year's GDP against %.0f%%"
                + " (0.7.81; debt payments over revenue until then)",
                borrowingPasses, landBorrowPasses, worstShare * 100, ab.getDebtLimit() * 100), overLimit == 0);
        assertTrue(String.format("...and a pass that begins over it orders nothing and borrows nothing: %d passes began over it"
                + " (at most %.0f%% of a year's GDP), %d orders and %d bonds placed in them", overPasses, mostOver * 100,
                overOrders, overBonds), overOrders == 0 && overBonds == 0);
        assertTrue("...nor spends the cash under a month's tax, the reserve (" + underReserve + " passes did)", underReserve == 0);
        assertTrue("...and a pass's staffed orders leave the budget's room at nothing or more, what is on site counted ("
                + overBudget + " passes did not)",
                overBudget == 0);
        return g;
    }

    /** Every template's standing and on-site count, by id. */
    static int[] standingAndOnSite(Game g) {
        BuildingManager bm = g.getBuildingManager();
        int[] site = bm.getUnderConstructionById();
        int[] out = new int[bm.getMaxTemplateId() + 1];
        for (BuildingsTemplate t : bm.getTemplates()) {
            out[t.getId()] = bm.getQuantity(t.getId()) + (t.getId() < site.length ? site[t.getId()] : 0);
        }
        return out;
    }

    /** One pass, read: what rose, what it did measure by measure, the debt, the cash and the budget. */
    static void look(Game g, AutoBuilder ab, int[] before, double[] snap) {
        passes++;
        int[] now = standingAndOnSite(g);
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            int id = t.getId();
            if (id < before.length && now[id] > before[id] && !AutoBuilder.inRemit(t)) outsideRemit++;
        }
        // The ground it bought: each for an order of its own, short of ground when weighed; bare; the city's ground grown by exactly it.
        double grew = g.getLandManager().getOwnedSqFt() - snap[0], boughtSq = 0;
        boolean anyLand = false;
        for (AutoBuilder.Step s : ab.steps()) {
            if (s.land().isEmpty()) continue;
            anyLand = true;
            landOrders++;
            boughtSq += s.landSqFt();
            landSqFt += s.landSqFt();
            landCost += s.landCost();
            BuildingsTemplate t = g.getBuildingManager().getTemplateByName(s.building());
            if (s.outcome() != AutoBuilder.Outcome.ORDERED || t == null || !AutoBuilder.inRemit(t) || !(s.landShort() > 0)
                    || s.landSqFt() < s.landShort()) landNotItsOwn++;
            for (LandParcel p : s.land()) if (!LandMarket.bareGround(p)) oreBought++;
        }
        if (anyLand || grew != 0) {
            landPasses++;
            if (Math.abs(grew - boughtSq) > 1e-6 * Math.max(1, boughtSq)) landMismatch++;
        }
        double raised = 0;
        boolean staffed = false;
        for (AutoBuilder.Step s : ab.steps()) {
            raised += s.borrowed();
            if (s.outcome() == AutoBuilder.Outcome.ORDERED) {
                ordered++;
                builtBy.merge(s.measure().label(), s.ordered(), Integer::sum);
                BuildingsTemplate t = g.getBuildingManager().getTemplateByName(s.building());
                if (t != null && AutoBuilder.staffedBuilding(t)) staffed = true;
            }
        }
        // Each staffed order fits the room the budget left, what is on site counted - this pass's too - so none leaves it under nothing.
        if (staffed && ab.budgetRoom(g) < -1e-6) overBudget++;
        if (raised > 0) {
            borrowingPasses++;
            if (anyLand) landBorrowPasses++;
            double share = AutoBuilder.ratio(g);
            worstShare = Math.max(worstShare, share);
            if (!(share <= ab.getDebtLimit() + 1e-12)) overLimit++;
        }
        // A pass that began over the limit (0.7.81): it builds nothing and borrows nothing, the toggle off as here.
        if (!(snap[3] <= ab.getDebtLimit()) && !ab.isCashAnyway()) {
            overPasses++;
            mostOver = Math.max(mostOver, snap[3]);
            for (AutoBuilder.Step s : ab.steps()) if (s.outcome() == AutoBuilder.Outcome.ORDERED) overOrders++;
            if (ab.getBonds() > snap[4]) overBonds++;
        }
        if (g.getCash() < Math.min(snap[1], snap[2]) - 1e-6) underReserve++;

        List<CityNeeds.Need> all = CityNeeds.measure(g, CityNeeds.PLAIN);
        Map<BuildAdvice.Measure, AutoBuilder.Step> stepOf = new LinkedHashMap<>();
        for (AutoBuilder.Step s : ab.steps()) stepOf.put(s.measure(), s);
        for (BuildAdvice.Measure m : AutoBuilder.remit(g, all)) {
            AutoBuilder.Step s = stepOf.get(m);
            if (s == null) continue;               // kept after the pass, not before it: its first building came due
            kept++;
            boolean at = ab.atTarget(g, m);
            boolean site = BuildAdvice.units(BuildAdvice.onSite(g, m)) > 0;
            if (at) atTarget++;
            else if (site) works++;
            else if (s.outcome() == AutoBuilder.Outcome.HELD) {
                held++;
                heldBy.merge(s.cut(), 1, Integer::sum);
                if (!holdIsTrue(g, ab, s)) {
                    falseHolds++;
                    if (falseHolds <= 3) out.println("    (a hold not true at m" + g.getMonth() + ": " + s + ")");
                }
            } else if (s.outcome() == AutoBuilder.Outcome.NOTHING) nothing++;
            else unexplained++;
        }
    }

    /**
     * A hold, checked after the pass against the city as the pass left it. A
     * paving's (its building "Gravel Road, paved", 0.7.70) is weighed as the
     * pass weighs it: no ground, its cost the paving's quote (Game.quotePave())
     * - until 0.7.99 it was looked up as a template by that name, found none,
     * and was counted untrue; no run had held a paving for its money before.
     */
    static boolean holdIsTrue(Game g, AutoBuilder ab, AutoBuilder.Step s) {
        boolean paving = s.building().equals(ConstructionControl.PAVE_FROM + ", paved");
        if (paving && s.cut() == AutoBuilder.Cut.DEBT) {
            if (!ab.within(g)) return !ab.isCashAnyway();
            double gap = AutoBuilder.gapFor(g, g.quotePave(1).total);
            if (!(gap > 0)) return false;
            if (!(ab.getDebtLimit() > 0)) return true;
            DebtQuote q = AutoBuilder.bondFor(g, gap);
            return q == null || AutoBuilder.ratioAfter(g, q) > ab.getDebtLimit();
        }
        if (paving && s.cut() == AutoBuilder.Cut.CASH) {
            return !ab.within(g) && ab.isCashAnyway() && AutoBuilder.gapFor(g, g.quotePave(1).total) > 0;
        }
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName(s.building());
        switch (s.cut()) {
            case GROUND:
                // No ground free for one, and (0.7.77) no bare ground on offer to buy for it.
                return t != null && t.getLandSqFt() > g.getLandManager().getAvailableSqFt() && AutoBuilder.groundFor(g, t, 1) == null;
            case BUDGET:
                return t != null && BuildAdvice.running(g, t, new LinkedHashMap<>()) > ab.budgetRoom(g);
            case BUILDERS:
                return t != null && Double.isNaN(g.quoteBuild(t, 1).months);
            case DEBT: {
                if (t == null) return false;
                // Over the limit (0.7.81), the toggle off: it builds nothing, whatever the cash.
                if (!ab.within(g)) return !ab.isCashAnyway();
                // One and (0.7.77) the ground it lacks.
                AutoBuilder.Ground ground = AutoBuilder.groundFor(g, t, 1);
                if (ground == null) return false;
                double gap = AutoBuilder.gapFor(g, g.quoteBuild(t, 1).total + ground.cash());
                if (!(gap > 0)) return false;
                if (!(ab.getDebtLimit() > 0)) return true;
                DebtQuote q = AutoBuilder.bondFor(g, gap);
                return q == null || AutoBuilder.ratioAfter(g, q) > ab.getDebtLimit();
            }
            case CASH: {
                // Over the limit with Build from cash anyway on (0.7.81): the cash over the reserve short of one and its ground.
                if (t == null || ab.within(g) || !ab.isCashAnyway()) return false;
                AutoBuilder.Ground ground = AutoBuilder.groundFor(g, t, 1);
                return ground != null && AutoBuilder.gapFor(g, g.quoteBuild(t, 1).total + ground.cash()) > 0;
            }
            default:
                return false;
        }
    }

    /* ---------------------------------------------------------------- 4 */

    static void savedAndLoaded(Path root, Game g) {
        out.println("\n--- 4. a saved city plays on as the one it was saved from ---");
        int slot = 10;
        boolean[] saved = new boolean[1];
        quietly(() -> saved[0] = g.saveGame(slot, "autobuildcheck").ok);
        assertTrue("fixture: the decades' city, auto-build on, saved", saved[0] && g.getAutoBuilder().isOn());
        GameFiles files = new GameFiles(root.resolve("decades").resolve("data"), root.resolve("decades").resolve("no-legacy"));
        Game back = new Game(files);
        quietly(() -> back.loadGameSave(slot));
        AutoBuilder a = g.getAutoBuilder(), b = back.getAutoBuilder();
        assertTrue("it loads on, at the same margin, limit and Build from cash anyway, with the same log, totals and holds",
                back.getLoadFailure() == null && b.isOn() && b.getSlack() == a.getSlack() && b.getDebtLimit() == a.getDebtLimit()
                        && b.isCashAnyway() == a.isCashAnyway()
                        && b.log().size() == a.log().size() && b.getOrders() == a.getOrders()
                        && b.getBuildings() == a.getBuildings() && b.held().equals(a.held()));
        cents("...the revenue it reads the budget against", b.revenue(back), a.revenue(g));
        cents("...the debt it reads the limit against (0.7.81)", AutoBuilder.debt(back), AutoBuilder.debt(g));
        cents("...and the year of GDP it sets the debt against", AutoBuilder.annualGdp(back), AutoBuilder.annualGdp(g));
        int months = 36, drift = 0, ordersBefore = a.getOrders();
        // Ground for both, the same: the decades ended short of it, and a city that can build nothing proves little.
        for (Game c : new Game[] { g, back }) c.getLandManager().setOwnedSqFt(c.getLandManager().getOwnedSqFt() + 50_000_000L);
        double worst = 0;
        String first = null;
        for (int i = 0; i < months; i++) {
            quietly(() -> { g.simulateMonths(1); back.simulateMonths(1); });
            double[][] pairs = {
                    { g.getCash(), back.getCash() },
                    { g.getDebtManager().getAllPrincipal(), back.getDebtManager().getAllPrincipal() },
                    { a.getSpent(), b.getSpent() }, { a.getBorrowed(), b.getBorrowed() },
                    { g.getPopulationManager().getPopulation(), back.getPopulationManager().getPopulation() },
                    { g.getLandManager().getOwnedSqFt(), back.getLandManager().getOwnedSqFt() },
                    { a.getLandSpent(), b.getLandSpent() }, { a.getLandOffers(), b.getLandOffers() } };
            String[] names = { "cash", "debt", "spent", "borrowed", "people", "ground owned", "ground bought", "offers bought" };
            for (int k = 0; k < pairs.length; k++) {
                double d = Math.abs(pairs[k][0] - pairs[k][1]);
                worst = Math.max(worst, d);
                if (d > .00001) { drift++; if (first == null) first = "m" + g.getMonth() + " " + names[k]; }
            }
            if (!java.util.Arrays.equals(standingAndOnSite(g), standingAndOnSite(back))) {
                drift++;
                if (first == null) first = "m" + g.getMonth() + " buildings";
            }
            if (a.getOrders() != b.getOrders() || !a.held().equals(b.held())) {
                drift++;
                if (first == null) first = "m" + g.getMonth() + " its log";
            }
        }
        assertTrue(String.format("both run on %d months, it ordering in both (%d orders now): the cash, the debt, what it"
                        + " spent and borrowed, the ground it owns and what it bought, the people, every building and site and"
                        + " its log the same each month, to the cent (worst %.2g%s)", months, a.getOrders(), worst,
                        first == null ? "" : ", first " + first),
                drift == 0);
        assertTrue("fixture: ...and it placed orders in those months (" + (a.getOrders() - ordersBefore) + ")",
                a.getOrders() > ordersBefore);
        AutoBuilder.State st = a.toState();
        String json = new com.google.gson.Gson().toJson(st);
        AutoBuilder c = new AutoBuilder();
        c.restore(new com.google.gson.Gson().fromJson(json, AutoBuilder.State.class));
        boolean landLogged = true;
        for (int i = 0; i < a.log().size(); i++) {
            AutoBuilder.Entry x = a.log().get(i), y = c.log().get(i);
            landLogged &= x.landSqFt() == y.landSqFt() && x.landCost() == y.landCost() && x.landWhere().equals(y.landWhere());
        }
        a.setCashAnyway(true, null);
        AutoBuilder c2 = new AutoBuilder();
        c2.restore(new com.google.gson.Gson().fromJson(new com.google.gson.Gson().toJson(a.toState()), AutoBuilder.State.class));
        a.setCashAnyway(false, null);
        assertTrue("its state through the save's own Gson: the same settings, log and totals, the ground it bought among them,"
                        + " and Build from cash anyway on and off",
                c.isOn() == a.isOn() && c.getSlack() == a.getSlack() && c.getDebtLimit() == a.getDebtLimit()
                        && c.isCashAnyway() == a.isCashAnyway() && c2.isCashAnyway() && c.log().size() == a.log().size()
                        && c.getSpent() == a.getSpent() && c.getBorrowed() == a.getBorrowed() && landLogged
                        && c.getLandSqFt() == a.getLandSqFt() && c.getLandSpent() == a.getLandSpent()
                        && c.getLandOffers() == a.getLandOffers());
    }

    /**
     * A city buying its ground, saved and loaded (0.7.77): the town with no
     * ground free, it on, saved before its first pass; both run on with it on
     * - the cash, the debt, the ground owned and what it bought, every
     * building and site, its log - month by month, to the cent. The town's
     * businesses are held from investing (ChildcareCheck.town()), a
     * harness's hold no save carries: the loaded copy is held the same.
     */
    static void groundSavedAndLoaded(Path root) {
        Game g = noGround(root, "groundsave");
        AutoBuilder a = g.getAutoBuilder();
        a.setOn(true, null);
        int slot = 10;
        boolean[] saved = new boolean[1];
        quietly(() -> saved[0] = g.saveGame(slot, "autobuildcheck ground").ok);
        GameFiles files = new GameFiles(root.resolve("groundsave"), root.resolve("groundsave-no-legacy"));
        Game back = new Game(files);
        quietly(() -> back.loadGameSave(slot));
        for (Sector sc : back.getSectors().all()) back.getBusinessInvestment().holdSector(sc.key());
        AutoBuilder b = back.getAutoBuilder();
        assertTrue("fixture: the town with no ground free, auto-build on, saved and loaded on, with the same ground",
                saved[0] && back.getLoadFailure() == null && b.isOn()
                        && back.getLandManager().getOwnedSqFt() == g.getLandManager().getOwnedSqFt()
                        && back.getLandManager().getAvailableSqFt() == g.getLandManager().getAvailableSqFt());
        int months = 24, drift = 0, offersBefore = a.getLandOffers();
        double worst = 0;
        String first = null;
        for (int i = 0; i < months; i++) {
            quietly(() -> { g.simulateMonths(1); back.simulateMonths(1); });
            double[][] pairs = {
                    { g.getCash(), back.getCash() },
                    { g.getDebtManager().getAllPrincipal(), back.getDebtManager().getAllPrincipal() },
                    { g.getLandManager().getOwnedSqFt(), back.getLandManager().getOwnedSqFt() },
                    { a.getLandSpent(), b.getLandSpent() }, { a.getLandSqFt(), b.getLandSqFt() }, { a.getLandOffers(), b.getLandOffers() },
                    { a.getSpent(), b.getSpent() }, { a.getBorrowed(), b.getBorrowed() } };
            String[] names = { "cash", "debt", "ground owned", "ground's price", "ground bought", "offers bought", "spent", "borrowed" };
            for (int k = 0; k < pairs.length; k++) {
                double d = Math.abs(pairs[k][0] - pairs[k][1]);
                worst = Math.max(worst, d);
                if (d > .00001) { drift++; if (first == null) first = "m" + g.getMonth() + " " + names[k]; }
            }
            if (!java.util.Arrays.equals(standingAndOnSite(g), standingAndOnSite(back)) || a.getOrders() != b.getOrders()
                    || !a.held().equals(b.held()) || !a.bought().equals(b.bought())) {
                drift++;
                if (first == null) first = "m" + g.getMonth() + " buildings or its log";
            }
        }
        assertTrue(String.format("both run on %d months: the cash, the debt, the ground owned and what it bought, every building and"
                        + " site and its log the same each month, to the cent (worst %.2g%s)", months, worst,
                        first == null ? "" : ", first " + first), drift == 0);
        assertTrue("fixture: ...and it bought ground in those months (" + (a.getLandOffers() - offersBefore) + " offers)",
                a.getLandOffers() > offersBefore);
    }

    /* ---------------------------------------------------------------- 5 */

    static void whatHoldsIt(Path root) {
        out.println("\n--- 5. what holds it back, caused ---");
        BuildAdvice.Measure general = BuildAdvice.Measure.care(CareType.GENERAL);
        BuildAdvice.Measure roads = BuildAdvice.Measure.of(BuildAdvice.Kind.ROADS);

        theGround(root);

        // The debt limit.
        Game d = ChildcareCheck.town(root, "limit");
        AutoBuilder ad = d.getAutoBuilder();
        d.setCashForTest(AutoBuilder.reserve(d));
        ad.setDebtLimit(0, null);
        ad.setOn(true, null);
        int[] paper = new int[2];
        d.autoBuildProbeForTest = after -> paper[after ? 1 : 0] = d.getDebtManager().getDebt().size();
        quietly(() -> d.simulateMonths(1));
        d.autoBuildProbeForTest = null;
        AutoBuilder.Step sd = stepFor(ad, roads);
        assertTrue("fixture: the town short of road, its cash at a month's tax", sd != null
                && sd.outcome() != AutoBuilder.Outcome.AHEAD);
        assertTrue("a debt limit of 0%: it holds and borrows nothing", sd != null && sd.outcome() == AutoBuilder.Outcome.HELD
                && sd.cut() == AutoBuilder.Cut.DEBT && ad.getBonds() == 0 && paper[1] == paper[0]);
        Game e = ChildcareCheck.town(root, "limit2");
        AutoBuilder ae = e.getAutoBuilder();
        e.setCashForTest(AutoBuilder.reserve(e));
        ae.setDebtLimit(AutoBuilder.DEBT_LIMIT_MOST, null);
        ae.setOn(true, null);
        double cashBefore = e.getCash(), reserve = AutoBuilder.reserve(e), shareBefore = AutoBuilder.ratio(e);
        double debtBefore = AutoBuilder.debt(e);
        double[] shareAfter = { Double.NaN };
        e.autoBuildProbeForTest = after -> { if (after) shareAfter[0] = AutoBuilder.ratio(e); };
        quietly(() -> e.simulateMonths(1));
        e.autoBuildProbeForTest = null;
        AutoBuilder.Step se = stepFor(ae, roads);
        assertTrue(String.format("at %.0f%% of a year's GDP, it borrows on the funding page's bond for what the cash over the"
                        + " reserve does not cover, and orders: the city's debt from %.2f%% to %.2f%% of a year's GDP",
                        AutoBuilder.DEBT_LIMIT_MOST * 100, shareBefore * 100, shareAfter[0] * 100),
                se != null && se.outcome() == AutoBuilder.Outcome.ORDERED && se.borrowed() > 0
                && shareAfter[0] <= AutoBuilder.DEBT_LIMIT_MOST && ae.getBonds() >= 1 && cashBefore <= reserve + 1e-6);

        theLimit(root, roads);

        // The budget.
        Game f = ChildcareCheck.town(root, "budget");
        AutoBuilder af = f.getAutoBuilder();
        af.setOn(true, null);
        double[] room = { Double.NaN };
        f.autoBuildProbeForTest = after -> { if (!after) room[0] = af.budgetRoom(f); };
        // A month's spending past what the year's revenue brings: every one of its services' bills, and as much again.
        f.getEconomyManager().setHealthcare(f.getEconomyManager().getNationalAccounts().getTotalRevenue() * 2, 0);
        f.getEconomyManager().refreshGovernmentAccounts(0, 0, 0, 0);
        quietly(() -> f.simulateMonths(1));
        f.autoBuildProbeForTest = null;
        AutoBuilder.Step sf = stepFor(af, general);
        assertTrue(String.format("fixture: the budget leaves nothing to run a clinic (%,.0fk a month)", room[0]), room[0] <= 0
                && sf != null && sf.outcome() != AutoBuilder.Outcome.AHEAD);
        assertTrue("a staffed service the budget cannot run is held, every building that serves it passed over",
                sf != null && sf.outcome() == AutoBuilder.Outcome.HELD && sf.cut() == AutoBuilder.Cut.BUDGET);
        Notice nf = f.getInbox().live("autobuild");
        assertTrue("...and the inbox names the Policy tab", nf != null && String.join(" ", nf.getBody()).contains("Policy tab"));

        // A first police station.
        Game p = ChildcareCheck.town(root, "police");
        BuildAdvice.Measure police = BuildAdvice.Measure.of(BuildAdvice.Kind.POLICE);
        BuildingsTemplate station = p.getBuildingManager().getTemplateByName("Police Station");
        quietly(() -> p.getBuildingManager().retire(station, p.getBuildingManager().getQuantity(station.getId())));
        double[] sdp = BuildAdvice.supplyDemand(p, police, new LinkedHashMap<>());
        List<CityNeeds.Need> all = CityNeeds.measure(p, CityNeeds.PLAIN);
        assertTrue(String.format("fixture: a town with no station, needing %,.0f officers - under half a station's %d",
                sdp[1], station.getCapacity()), AutoBuilder.serving(p, police) == 0
                && sdp[1] - sdp[0] < AutoBuilder.FIRST_SHARE * station.getCapacity());
        assertTrue("a first police station waits for half a station's worth of need: police is not a service it keeps",
                !AutoBuilder.remit(p, all).contains(police));
        int warranted = (int) Math.ceil(AutoBuilder.FIRST_SHARE * station.getCapacity() * 100_000.0
                / Crime.FULL_OFFICERS_PER_100K);
        assertTrue(String.format("...which comes at %,d people (%.0f officers for 100,000, Crime.FULL_OFFICERS_PER_100K);"
                + " the town has %,d", warranted, Crime.FULL_OFFICERS_PER_100K, p.getPopulationManager().getPopulation()),
                warranted > p.getPopulationManager().getPopulation());
        quietly(() -> p.buildStack(station, 1, true));
        assertTrue("...and with one standing, it is kept again", AutoBuilder.remit(p, CityNeeds.measure(p, CityNeeds.PLAIN))
                .contains(police));
    }

    /** A tonne of iron under each offer of the ore fixture: any ore makes an offer not bare ground (LandMarket.bareGround()). */
    static final double ORE_TONNES = 1;

    /** The town with no ground free: its ground cut to what its buildings use (Game's land office relists for the smaller city). */
    static Game noGround(Path root, String name) {
        Game g = ChildcareCheck.town(root, name);
        LandManager land = g.getLandManager();
        land.setOwnedSqFt((long) Math.ceil(land.getAllocatedSqFt()));
        return g;
    }

    /** The first step of the last pass that bought ground, or null. */
    static AutoBuilder.Step firstLand(AutoBuilder ab) {
        for (AutoBuilder.Step s : ab.steps()) if (!s.land().isEmpty()) return s;
        return null;
    }

    /** THE GROUND IT BUYS (0.7.77): none free, at a limit of nothing and of the most, only ore on offer. */
    static void theGround(Path root) {
        // None free: it buys what Build's land shortcut offers for the shortfall, and places the order.
        Game g = noGround(root, "noground");
        LandManager land = g.getLandManager();
        AutoBuilder ab = g.getAutoBuilder();
        ab.setOn(true, null);
        double free0 = land.getAvailableSqFt(), owned0 = land.getOwnedSqFt();
        quietly(() -> g.simulateMonths(1));
        AutoBuilder.Step s = firstLand(ab);
        boolean shortOf = false;
        for (AutoBuilder.Step x : ab.steps()) shortOf |= x.outcome() != AutoBuilder.Outcome.AHEAD;
        assertTrue("fixture: the town short of a service, with no ground free", shortOf && free0 < 1);
        assertTrue(s == null ? "with none free, it buys the ground an order lacks and places the order (none bought)"
                        : String.format("with none free, it buys the ground an order lacks and places the order: %s for %d %s (%s), %s short",
                        LandManager.areaWords(s.landSqFt()), s.ordered(), s.building(), s.measure().label(), LandManager.areaWords(s.landShort())),
                s != null && s.outcome() == AutoBuilder.Outcome.ORDERED && s.landShort() > 0 && s.landSqFt() >= s.landShort()
                        && land.getOwnedSqFt() > owned0);
        // ...the offer the shortcut offers for that shortfall, on the listing the pass saw: a twin town, asked.
        Game twin = noGround(root, "noground-twin");
        LandParcel shortcut = s == null ? null : twin.bestOffer(Game.LandNeed.shortfall(s.landShort()));
        LandParcel first = s == null ? null : s.land().get(0);
        assertTrue(String.format("...its first offer the one Build's land shortcut offers for that shortfall on the listing it saw"
                        + " (Game.bestOffer(LandNeed.shortfall()), asked of a twin town): %s", first == null ? "none" : first.where()),
                shortcut != null && first != null && shortcut.getId() == first.getId() && shortcut.where().equals(first.where())
                        && shortcut.getPriceUsd() == first.getPriceUsd());
        Notice n = g.getInbox().live("autobuild-land");
        assertTrue("...and the inbox notes the purchase and why: the offers, the order and what it lacked", n != null && s != null
                && String.join(" ", n.getBody()).contains(first.where()) && String.join(" ", n.getBody()).contains(s.measure().label())
                && String.join(" ", n.getBody()).contains("lacked"));
        AutoBuilder.Entry e = ab.latest(AutoBuilder.LOG_MOST).stream().filter(x -> x.landSqFt() > 0).findFirst().orElse(null);
        assertTrue("...its log's order carries the ground: its square feet, price and places", e != null && s != null
                && e.landSqFt() == s.landSqFt() && e.landCost() == s.landCost() && e.landWhere().contains(first.where()));

        // The limit: at nothing, with the cash at a month's tax, it buys none and borrows nothing; at the most, it borrows for both.
        Game d = noGround(root, "groundlimit0");
        AutoBuilder ad = d.getAutoBuilder();
        d.setCashForTest(AutoBuilder.reserve(d));
        ad.setDebtLimit(0, null);
        ad.setOn(true, null);
        int[] paper = new int[2];
        d.autoBuildProbeForTest = after -> paper[after ? 1 : 0] = d.getDebtManager().getDebt().size();
        double ownedD = d.getLandManager().getOwnedSqFt();
        quietly(() -> d.simulateMonths(1));
        d.autoBuildProbeForTest = null;
        AutoBuilder.Step held = null;
        for (AutoBuilder.Step x : ad.steps()) {
            BuildingsTemplate t = d.getBuildingManager().getTemplateByName(x.building());
            if (x.outcome() == AutoBuilder.Outcome.HELD && x.cut() == AutoBuilder.Cut.DEBT && t != null && t.getLandSqFt() > 0) { held = x; break; }
        }
        assertTrue("fixture: no ground free and the cash at a month's tax: an order that needs ground and money", held != null);
        assertTrue("at a debt limit of 0% it buys no ground and borrows nothing: the order held for the limit, the ground's price in it",
                held != null && d.getLandManager().getOwnedSqFt() == ownedD && ad.getLandOffers() == 0 && ad.getBonds() == 0
                        && paper[1] == paper[0] && String.join(" ", ad.held()).contains("of ground it lacks"));
        // ...and a limit over nothing its bond would cross (0.7.81): the ground's price in it.
        Game b = noGround(root, "groundbinds");
        AutoBuilder ag = b.getAutoBuilder();
        b.setCashForTest(AutoBuilder.reserve(b));
        ag.setDebtLimit(justOver(b), null);
        ag.setOn(true, null);
        int[] paperB = new int[2];
        b.autoBuildProbeForTest = after -> paperB[after ? 1 : 0] = b.getDebtManager().getDebt().size();
        double ownedB = b.getLandManager().getOwnedSqFt(), shareB = AutoBuilder.ratio(b);
        quietly(() -> b.simulateMonths(1));
        b.autoBuildProbeForTest = null;
        AutoBuilder.Step heldB = null;
        for (AutoBuilder.Step x : ag.steps()) {
            BuildingsTemplate t = b.getBuildingManager().getTemplateByName(x.building());
            if (x.outcome() == AutoBuilder.Outcome.HELD && x.cut() == AutoBuilder.Cut.DEBT && t != null && t.getLandSqFt() > 0) { heldB = x; break; }
        }
        assertTrue(String.format("fixture: no ground free, the cash at a month's tax, the town's debt %.2f%% of a year's GDP under a"
                + " limit of %.0f%%: an order that needs ground and a bond", shareB * 100, ag.getDebtLimit() * 100),
                heldB != null && shareB <= ag.getDebtLimit());
        assertTrue("under a limit its bond would cross it buys no ground and borrows nothing: the order held for the limit, the"
                        + " ground's price in it",
                heldB != null && b.getLandManager().getOwnedSqFt() == ownedB && ag.getLandOffers() == 0 && ag.getBonds() == 0
                        && paperB[1] == paperB[0] && String.join(" ", ag.held()).contains("of ground it lacks")
                        && String.join(" ", ag.held()).contains("past your limit"));
        Game f = noGround(root, "groundlimit25");
        AutoBuilder af = f.getAutoBuilder();
        f.setCashForTest(AutoBuilder.reserve(f));
        af.setDebtLimit(AutoBuilder.DEBT_LIMIT_MOST, null);
        af.setOn(true, null);
        double[] shareAfter = { Double.NaN };
        f.autoBuildProbeForTest = after -> { if (after) shareAfter[0] = AutoBuilder.ratio(f); };
        quietly(() -> f.simulateMonths(1));
        f.autoBuildProbeForTest = null;
        AutoBuilder.Step sf = firstLand(af);
        assertTrue(String.format("at %.0f%% of a year's GDP it borrows for the ground and the order together on the funding page's"
                        + " bond, and buys both: the city's debt then %.2f%% of a year's GDP", AutoBuilder.DEBT_LIMIT_MOST * 100,
                        shareAfter[0] * 100),
                sf != null && sf.outcome() == AutoBuilder.Outcome.ORDERED && sf.borrowed() > 0
                        && shareAfter[0] <= AutoBuilder.DEBT_LIMIT_MOST && af.getBonds() >= 1);

        // Over the limit (0.7.81): it buys no ground and places nothing; with Build from cash anyway on, the cash pays for both.
        Game v = overTheLimit(noGround(root, "groundover"));
        AutoBuilder av = v.getAutoBuilder();
        av.setOn(true, null);
        double ownedV = v.getLandManager().getOwnedSqFt(), debtV = AutoBuilder.debt(v), cashV = v.getCash();
        int paperV = v.getDebtManager().getDebt().size();
        boolean overV = !av.within(v);
        quietly(() -> v.simulateMonths(1));
        int orderedV = 0;
        for (AutoBuilder.Step x : av.steps()) if (x.outcome() == AutoBuilder.Outcome.ORDERED) orderedV++;
        assertTrue(String.format("fixture: no ground free, the city's debt %.0f%% of a year's GDP against the %.0f%% limit, and"
                        + " the cash to pay (%s over the reserve)", AutoBuilder.ratio(v) * 100, av.getDebtLimit() * 100,
                        DecisionLog.money(cashV - AutoBuilder.reserve(v))), overV && cashV - AutoBuilder.reserve(v) > 0);
        assertTrue("over the limit it buys no ground and places nothing, and borrows nothing",
                orderedV == 0 && v.getLandManager().getOwnedSqFt() == ownedV && av.getLandOffers() == 0 && av.getBonds() == 0
                        && v.getDebtManager().getDebt().size() == paperV);
        Game w = overTheLimit(noGround(root, "groundover-cash"));
        AutoBuilder aw = w.getAutoBuilder();
        aw.setCashAnyway(true, null);
        aw.setOn(true, null);
        double ownedW = w.getLandManager().getOwnedSqFt();
        int paperW = w.getDebtManager().getDebt().size();
        double principalW = w.getDebtManager().getAllPrincipal();
        quietly(() -> w.simulateMonths(1));
        AutoBuilder.Step sw = firstLand(aw);
        assertTrue(sw == null ? "...with Build from cash anyway on, it buys the ground from the cash and places the order (none)"
                        : String.format("...with Build from cash anyway on, it buys the ground from the cash and places the order:"
                        + " %s for %d %s, nothing borrowed", LandManager.areaWords(sw.landSqFt()), sw.ordered(), sw.building()),
                sw != null && sw.outcome() == AutoBuilder.Outcome.ORDERED && sw.borrowed() == 0 && aw.getBonds() == 0
                        && w.getLandManager().getOwnedSqFt() > ownedW && w.getDebtManager().getDebt().size() == paperW
                        && w.getDebtManager().getAllPrincipal() <= principalW);

        // Only ore on offer: the shortcut would buy a field for its ore; it buys none and holds for want of ground.
        Game o = noGround(root, "groundore");
        LandMarket market = o.getLandManager().getMarket();
        List<LandParcel> ore = new ArrayList<>();
        int sitesAt = 1 + LandParcel.RECT_FIELDS + CityLand.AREAS, amountsAt = sitesAt + CityLand.KINDS;
        for (LandParcel p : market.getListing()) {
            double[] row = p.offerRow();
            row[sitesAt + Resource.IRON.ordinal()] = 1;
            row[amountsAt + Resource.IRON.ordinal()] = ORE_TONNES;
            ore.add(LandParcel.fromOfferRow(row));
        }
        market.putOffers(ore);
        boolean allOre = !market.getListing().isEmpty();
        for (LandParcel p : market.getListing()) allOre &= !LandMarket.bareGround(p) && p.getSizeSqFt() > 0;
        LandParcel would = o.bestOffer(Game.LandNeed.shortfall(1));
        AutoBuilder ao = o.getAutoBuilder();
        ao.setOn(true, null);
        double ownedO = o.getLandManager().getOwnedSqFt();
        quietly(() -> o.simulateMonths(1));
        AutoBuilder.Step so = null;
        for (AutoBuilder.Step x : ao.steps()) if (x.outcome() == AutoBuilder.Outcome.HELD && x.cut() == AutoBuilder.Cut.GROUND) { so = x; break; }
        assertTrue("fixture: no ground free and every offer with ore under it, dry ground in each - Build's shortcut would buy one",
                allOre && would != null && !LandMarket.bareGround(would));
        assertTrue("it buys none of them - no field for its ore - and holds the order for want of ground",
                so != null && o.getLandManager().getOwnedSqFt() == ownedO && ao.getLandOffers() == 0);
        Notice no = o.getInbox().live("autobuild");
        assertTrue("...and the inbox says so, naming the land office", no != null && String.join(" ", no.getBody()).contains("land office"));
        ao.setOn(false, o.getDecisions());
        quietly(() -> o.simulateMonths(1));
        assertTrue("...settled the month it is switched off", o.getInbox().live("autobuild") == null);
    }

    /** A year of GDP as SummaryScreen's Debt/GDP reads it (Pieces.annualGdp()): the history's last twelve, scaled up from fewer. */
    static double annualised(Game g) {
        NationalAccounts na = g.getEconomyManager().getNationalAccounts();
        int months = na.getMonthsRecorded();
        return months <= 0 ? 0 : months >= 12 ? na.getAnnualGdp() : na.getAnnualGdp() / months * 12;
    }

    /** The debt limit's next step over a city's debt now (0.7.81): a limit it is within, which one road's bond crosses. */
    static double justOver(Game g) {
        return (Math.floor(AutoBuilder.ratio(g) / AutoBuilder.DEBT_STEP) + 1) * AutoBuilder.DEBT_STEP;
    }

    /**
     * A town over the debt limit (0.7.81): a twenty-year bond on the funding page for twice a year of its GDP, so the
     * city's debt is past DEFAULT_DEBT_LIMIT whatever the bond's price, and its cash back where it was - the cash to pay.
     */
    static Game overTheLimit(Game g) {
        double cash = g.getCash();
        quietly(() -> g.handleLongBondForCash(2 * AutoBuilder.annualGdp(g), Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE));
        g.setCashForTest(cash);
        return g;
    }

    /**
     * THE LIMIT, A SHARE OF GDP (0.7.81): a bond's face is what it adds to the debt; under the limit a bond that would
     * cross it is not taken; over it nothing is built and nothing borrowed, with the cash to pay; and with Build from cash
     * anyway on, what the cash pays for, nothing borrowed - and with the cash at a month's tax, held for the cash.
     */
    static void theLimit(Path root, BuildAdvice.Measure roads) {
        // The arithmetic: a bond's face on the books - overdrawn or not, the debt being the bonds and bills.
        Game a = ChildcareCheck.town(root, "limit-face");
        double before = AutoBuilder.debt(a), gdp = AutoBuilder.annualGdp(a);
        DebtQuote q = AutoBuilder.bondFor(a, gdp / 10);
        double predicted = q == null ? Double.NaN : AutoBuilder.debtAfter(a, q);
        quietly(() -> a.handleLongBondForCash(gdp / 10, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE));
        cents(String.format("a bond's face is what it adds to the city's debt: %s raised, %s owed (ratioAfter()'s arithmetic)",
                q == null ? "none" : DecisionLog.money(q.cashReceived()), q == null ? "none" : DecisionLog.money(q.faceValue())),
                AutoBuilder.debt(a), predicted);
        Game o = ChildcareCheck.town(root, "limit-overdrawn");
        o.setCashForTest(-gdp / 20);
        DebtQuote qo = AutoBuilder.bondFor(o, gdp / 10);
        double predictedO = qo == null ? Double.NaN : AutoBuilder.debtAfter(o, qo);
        quietly(() -> o.handleLongBondForCash(gdp / 10, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE));
        cents("...overdrawn, the same: the debt is the bonds and bills, the overdraft the central bank's page's",
                AutoBuilder.debt(o), predictedO);
        boolean asTheScreens = Math.abs(AutoBuilder.ratio(a) - a.getDebtManager().getAllPrincipal() / gdp) <= 1e-12
                && gdp == annualised(a);
        assertTrue(String.format("...and the ratio is the left panel's Debt/GDP: the bonds and bills over the year's GDP,"
                + " annualised under a year (%.2f%%)", AutoBuilder.ratio(a) * 100), asTheScreens);

        // A limit a bond would cross: under it now, the cash at a month's tax.
        Game b = ChildcareCheck.town(root, "limit-binds");
        AutoBuilder ab = b.getAutoBuilder();
        b.setCashForTest(AutoBuilder.reserve(b));
        ab.setDebtLimit(justOver(b), null);
        ab.setOn(true, null);
        int[] paper = new int[2];
        double shareB = AutoBuilder.ratio(b);
        b.autoBuildProbeForTest = after -> paper[after ? 1 : 0] = b.getDebtManager().getDebt().size();
        quietly(() -> b.simulateMonths(1));
        b.autoBuildProbeForTest = null;
        AutoBuilder.Step sb = stepFor(ab, roads);
        assertTrue(String.format("fixture: the town short of road, its debt %.2f%% of a year's GDP - under a limit of %.0f%%, the"
                + " slider's next step - and its cash at a month's tax", shareB * 100, ab.getDebtLimit() * 100), sb != null
                && sb.outcome() != AutoBuilder.Outcome.AHEAD && shareB <= ab.getDebtLimit());
        String heldB = String.join(" ", ab.held());
        assertTrue("under the limit, a bond that would take the debt past it is not taken: held for the limit, no paper, the"
                        + " inbox saying how far it would go",
                sb != null && sb.outcome() == AutoBuilder.Outcome.HELD && sb.cut() == AutoBuilder.Cut.DEBT && ab.getBonds() == 0
                        && paper[1] == paper[0] && heldB.contains("of a year's GDP, past your limit of "
                        + AutoBuilder.gdpShare(ab.getDebtLimit())));

        // Over the limit, with the cash to pay: nothing built, nothing borrowed.
        Game c = overTheLimit(ChildcareCheck.town(root, "limit-over"));
        AutoBuilder ac = c.getAutoBuilder();
        ac.setOn(true, null);
        double shareC = AutoBuilder.ratio(c), spare = c.getCash() - AutoBuilder.reserve(c);
        int[] paperC = new int[2];
        c.autoBuildProbeForTest = after -> paperC[after ? 1 : 0] = c.getDebtManager().getDebt().size();
        int sitesC = siteCount(c);
        quietly(() -> c.simulateMonths(1));
        c.autoBuildProbeForTest = null;
        AutoBuilder.Step sc = stepFor(ac, roads);
        BuildingsTemplate road = sc == null ? null : c.getBuildingManager().getTemplateByName(sc.building());
        double roadCost = road == null ? Double.NaN : c.quoteBuild(road, 1).total;
        assertTrue(String.format("fixture: the town short of road, its debt %.0f%% of a year's GDP against the default %.0f%% limit,"
                        + " and the cash over the reserve (%s) to pay for one (%s)", shareC * 100, ac.getDebtLimit() * 100,
                        DecisionLog.money(spare), DecisionLog.money(roadCost)),
                sc != null && sc.outcome() != AutoBuilder.Outcome.AHEAD && shareC > ac.getDebtLimit() && spare > roadCost);
        int orderedC = 0;
        for (AutoBuilder.Step x : ac.steps()) if (x.outcome() == AutoBuilder.Outcome.ORDERED) orderedC++;
        assertTrue("over the limit it builds nothing and borrows nothing, though the cash would pay: held for the limit",
                orderedC == 0 && sc != null && sc.outcome() == AutoBuilder.Outcome.HELD && sc.cut() == AutoBuilder.Cut.DEBT
                        && ac.getBonds() == 0 && paperC[1] == paperC[0] && siteCount(c) == sitesC);
        Notice nc = c.getInbox().live("autobuild");
        String bodyC = nc == null ? "" : String.join(" ", nc.getBody());
        assertTrue("...and the inbox says the city is over the limit, and names Build from cash anyway",
                bodyC.contains("over your limit of 60%") && bodyC.contains("Build from cash anyway"));

        // ...with Build from cash anyway on: what the cash pays for, nothing borrowed.
        Game d = overTheLimit(ChildcareCheck.town(root, "limit-cash"));
        AutoBuilder ad = d.getAutoBuilder();
        ad.setCashAnyway(true, null);
        ad.setOn(true, null);
        double principalD = d.getDebtManager().getAllPrincipal(), cashD = d.getCash(), reserveD = AutoBuilder.reserve(d);
        int paperD = d.getDebtManager().getDebt().size();
        double[] spentD = { 0 };
        d.autoBuildProbeForTest = after -> { if (after) spentD[0] = cashD - d.getCash(); };
        quietly(() -> d.simulateMonths(1));
        d.autoBuildProbeForTest = null;
        int orderedD = 0;
        double costD = 0;
        for (AutoBuilder.Step x : ad.steps()) if (x.outcome() == AutoBuilder.Outcome.ORDERED) { orderedD++; costD += x.cost(); }
        assertTrue(String.format("with Build from cash anyway on, over the limit it builds what the cash pays for and borrows"
                        + " nothing: %d orders, %s, the cash still at or over its reserve", orderedD, DecisionLog.money(costD)),
                orderedD > 0 && ad.getBonds() == 0 && d.getDebtManager().getDebt().size() == paperD
                        && d.getDebtManager().getAllPrincipal() <= principalD && cashD - spentD[0] >= reserveD - 1e-6);

        // ...and with the cash at a month's tax: held for the cash.
        Game f = overTheLimit(ChildcareCheck.town(root, "limit-nocash"));
        AutoBuilder af = f.getAutoBuilder();
        f.setCashForTest(AutoBuilder.reserve(f));
        af.setCashAnyway(true, null);
        af.setOn(true, null);
        int paperF = f.getDebtManager().getDebt().size();
        quietly(() -> f.simulateMonths(1));
        AutoBuilder.Step sf = stepFor(af, roads);
        assertTrue("...and with the cash at a month's tax, held for the cash, nothing borrowed, the inbox saying it builds from"
                        + " the cash alone",
                sf != null && sf.outcome() == AutoBuilder.Outcome.HELD && sf.cut() == AutoBuilder.Cut.CASH && af.getBonds() == 0
                        && f.getDebtManager().getDebt().size() == paperF
                        && String.join(" ", af.held()).contains("builds from the cash alone"));
    }

    static AutoBuilder.Step stepFor(AutoBuilder ab, BuildAdvice.Measure m) {
        for (AutoBuilder.Step s : ab.steps()) if (s.measure().equals(m)) return s;
        return null;
    }

    static int siteCount(Game g) {
        int n = 0;
        for (int v : g.getBuildingManager().getUnderConstructionById()) n += v;
        return n;
    }
}

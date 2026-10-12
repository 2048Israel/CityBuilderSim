package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The builders' own depots first (0.7.130, batch T1-C): the rule that serves
 * a depot on site the crews' rule would starve - keep there past the months
 * the builders expand at - off the top of their month, before the rule shares
 * the rest, while the city could staff it; and, while the dead wait, the
 * city's death care beside it at the same priority - held on a city whose
 * builders are stalled the way the trace found Jerus's.
 *
 * WHY. Batch T1 traced Jerus's construction-bound city (the project's
 * why-the-builders-stall.md, section 3): repairs took 0.81 of the builders'
 * nameplate, about 420 points a month reached the sites, and the builders'
 * one depot order (BusinessInvestment.MAX_CONCURRENT_ORDERS) waited in the
 * queue at its crew's weight - 0.4 points a month - so their plan said
 * "already building" in 119 of 120 months and they could not grow out of it.
 * Jerus chose option C, the depots first, as its own batch (2026-10-10), and
 * put the death care beside them at one priority (2026-10-11). The city in
 * the trace is an old save the game now refuses, so this fixture is the
 * evidence: it causes the stall - repairs taking most of the builders' sick
 * month, a queue of years, the builders' own depot ordered by their own plan
 * and on site at its crew's weight - and plays it with the rule and without
 * it (BuildingManager.setBuildersFirstForTest(), the twin's switch).
 * DeathCareFirstCheck's trap is the city (its city() and sites()).
 *
 * What this has to prove:
 *   1. THE RULE'S ARITHMETIC: the first group takes what it owes, or the
 *      whole month if less, shared among its sites by the crews' rule
 *      (BuildingManager.waterFill()); every other site shares what is left by
 *      the same rule; the month is conserved; with a depot and a cemetery in
 *      the group neither takes the other's turn.
 *   2. THE STALL, CAUSED: repairs taking most of the builders' sick month,
 *      the queue past BACKLOG_MONTHS_BEFORE_EXPANDING months of their site
 *      output, every post offered, and the builders' one depot ordered by
 *      their own plan and on site at its crew's weight.
 *   3. BEFORE AND WITH THE RULE, the same month in twin cities: without it
 *      the depot's share is its crew-rule share, to the bit; with it, what it
 *      owes or the whole month; every other site the rule's share of what is
 *      left; the line and the one wait say so.
 *   4. THE BUILDERS GROW: over a fixed run the city with the rule opens its
 *      first depot within the months its points take at the trap's month
 *      (and one more), orders the next the month after, and from then its
 *      one order never holds the plan past the months it expands at (and
 *      one more) - a depot the rule would open in time waits by the rule -
 *      and it ends with more nameplate; the twin without it holds its
 *      nameplate and its plan says "already building" every month.
 *   5. NEVER LAID OFF THE MONTH IT OPENS (the 0.7.17 failure: depots bought
 *      for rushes, then laid off): every month a depot opens the builders
 *      offer every post and their loss clock is under the retirement rule's,
 *      and no depot is retired over the run.
 *   6. ONLY A DEPOT THE CITY COULD STAFF: in a new town whose founding
 *      put depots on site among a hundred people, the builders' own test
 *      (Sector.staffing()) says the town could not staff them, and they are
 *      crewed by the rule, to the bit; the twin that builds them first all
 *      the same opens one in its second month, its builders' fill falls
 *      under MIN_STAFFABLE_TO_ORDER, the month under the repairs, and no
 *      home is built, where the town with the test builds its homes.
 *   7. ONE PRIORITY WITH THE DEATH CARE: while the dead wait, a depot of the
 *      builders' and the city's cemetery on site together share what the
 *      first group takes by the crews' rule - neither ahead, neither starved;
 *      the city's order keeps the cemetery at its top; with the player's
 *      order set, the city's other sites take the city's share of what is
 *      left in that order.
 *   8. MONEY IS CONSERVED: the money audit every month played here.
 *
 * Every fixture causes its condition. How often the rule fires in a real
 * run is LongPlaytest's line "the builders' depots first".
 */
public class BuildersFirstCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-96s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= Math.max(tol, Math.abs(expected) * tol);
        if (!ok) fails++;
        out.printf("%-96s %s  %,.9f against %,.9f%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    static void bits(String label, double actual, double expected) {
        boolean ok = Double.doubleToLongBits(actual) == Double.doubleToLongBits(expected);
        if (!ok) fails++;
        out.printf("%-96s %s  %s against %s%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    static BuildingsTemplate template(Game g, String name) {
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName(name);
        if (t == null) throw new IllegalStateException("no template named " + name);
        return t;
    }

    static final String BUILDERS = Sectors.CONSTRUCTION;

    /** The builders' depot: the one template that adds to their capacity. */
    static final String DEPOT = "Construction Depot";

    /** The months the fixture's run is played: two years, a span the trace's city held through ten times over. */
    static final int RUN_MONTHS = 24;

    /** The worst month of the audit any month here saw, and the worst drift after it. */
    static double worstResidual = 0, worstDrift = 0;
    static int monthsAudited = 0;

    /** One month, audited. */
    static void month(Game g) {
        quietly(() -> g.simulateMonths(1));
        MoneyAudit.Result r = g.getLastMoneyAudit();
        if (r != null && r.relative() > worstResidual) worstResidual = r.relative();
        if (Math.abs(g.getPostAuditDrift()) > Math.abs(worstDrift)) worstDrift = g.getPostAuditDrift();
        monthsAudited++;
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        // DeathCareFirstCheck's fixture builds the city, quietly, through its own streams.
        DeathCareFirstCheck.out = out;
        DeathCareFirstCheck.quiet = quiet;
        Path root = Files.createTempDirectory("buildersfirstcheck");

        arithmetic();
        Game[] twins = stall(root);
        beforeAndWith(twins[0], twins[1]);
        grow(twins[0], twins[1]);
        neverLaidOff();
        onlyWhatItCouldStaff(root);
        onePriority(root);
        conserved();

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ============================ 1. THE RULE'S ARITHMETIC ============================ */

    /** A site's crew by the rule (BuildingManager.weightOf()): its buildings times one's points to CREW_SCALE_EXPONENT. */
    static double crew(double points, int buildings) {
        return buildings * Math.pow(points, BuildingManager.CREW_SCALE_EXPONENT);
    }

    static void arithmetic() {
        out.println("--- 1. the rule's arithmetic: the first group off the top of the month, the rest by the rule ---");
        // Six slots as a plan has them: the builders' depot, a cemetery, a
        // university and two schools of the city's, the empty slot, the
        // landlords' houses.
        List<String> keys = List.of("B4", "B27", "B35", "B32", "", "B0");
        double[] weight = { crew(400, 1), crew(900, 1), crew(26_000, 1), crew(2_100, 2), 0, crew(10, 40) };
        double[] owed = { 400, 900, 26_000, 4_200, 0, 400 };
        for (List<String> first : List.of(List.of("B4"), List.of("B4", "B27"))) {
            for (double output : new double[] { 80, 420, 5_000 }) {
                double[] rule = BuildingManager.waterFill(output, weight, owed);
                double[] share = BuildingManager.firstOffTheTop(output, weight, owed, keys, first);
                String at = String.format(" (%s, %,.0f points)", first.size() == 1 ? "the depot" : "depot and cemetery", output);
                double[] w = new double[keys.size()], o = new double[keys.size()];
                double groupOwed = 0;
                for (int i = 0; i < keys.size(); i++) {
                    if (first.contains(keys.get(i))) { w[i] = weight[i]; o[i] = owed[i]; groupOwed += owed[i]; }
                }
                double[] group = BuildingManager.waterFill(output, w, o);
                double took = 0;
                for (int i = 0; i < keys.size(); i++) {
                    if (!first.contains(keys.get(i))) continue;
                    bits("the first group's site takes its crews' share among the group: " + keys.get(i) + at, share[i], group[i]);
                    assertTrue("...more than the crews' rule among everyone gave it" + at, share[i] > rule[i]);
                    took += share[i];
                }
                close("the group takes what it owes, or the whole month if less" + at, took, Math.min(groupOwed, output), 1e-12);
                double[] rw = new double[keys.size()];
                for (int i = 0; i < keys.size(); i++) if (!first.contains(keys.get(i))) rw[i] = weight[i];
                double[] rest = BuildingManager.waterFill(output - took, rw, owed);
                boolean same = true, never = true;
                double sum = 0;
                for (int i = 0; i < keys.size(); i++) {
                    sum += share[i];
                    if (first.contains(keys.get(i))) continue;
                    same &= Double.doubleToLongBits(share[i]) == Double.doubleToLongBits(rest[i]);
                    never &= share[i] <= rule[i];
                }
                assertTrue("every other site shares what is left by the crews' rule, to the bit" + at, same);
                assertTrue("...none more than the rule gave it" + at, never);
                double total = 0;
                for (double v : owed) total += v;
                close("the month is conserved: every point to a site, or none owed" + at, sum, Math.min(output, total), 1e-12);
                if (first.size() == 2 && output < groupOwed) {
                    assertTrue("with both in the group, neither takes the whole of what the group takes" + at,
                            share[0] > 0 && share[1] > 0 && share[0] < took && share[1] < took);
                }
            }
        }
    }

    /* ============================ 2. THE STALL, CAUSED ============================ */

    /**
     * DeathCareFirstCheck's trap - repairs leaving the builders' sick month
     * a year's share of a cemetery's points, a University, a Penitentiary,
     * schools and the landlords' Houses on site - with the death-care rule
     * told nobody lies unburied (the dead still make the crews sick: the
     * trap's arithmetic stands, only the cemetery is not served first), the
     * builders let plan again, and a month played: their own plan orders
     * their one depot.
     */
    static Game stalled(Path root, String name, boolean rule) {
        Game g = DeathCareFirstCheck.city(root, name);
        DeathCareFirstCheck.sites(g);
        g.getBuildingManager().setUnburied(() -> 0);
        g.getBuildingManager().setBuildersFirstForTest(rule);
        g.getBusinessInvestment().releaseSector(BUILDERS);
        month(g);
        return g;
    }

    static Game[] stall(Path root) {
        out.println("\n--- 2. the stall, caused: repairs taking the builders' month, a queue of years, their one depot on site ---");
        Game without = stalled(root, "without", false), with = stalled(root, "with", true);
        Game g = with;
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate depot = template(g, DEPOT);
        String key = ConstructionControl.keyOf(depot);
        double output = g.getBuildingOutput(), atEvery = g.getBuildingOutputAtEveryPost();
        double repairs = g.getMaintenancePoints(), sick = g.getConstructionOutputAtEveryPost();
        out.printf("  nameplate %,d, sick output %,.0f, repairs %,.0f (%.1f%% of it, %.1f%% of nameplate), sites %,.0f;"
                        + " owed on site %,.0f = %,.0f months%n",
                bm.getTotalConstructionCapacity(), sick, repairs, repairs / sick * 100,
                repairs / bm.getTotalConstructionCapacity() * 100, atEvery, bm.getRemainingConstructionPoints(),
                bm.getRemainingConstructionPoints() / atEvery);
        out.println("  the builders' plan: " + g.getLastInvestment(BUILDERS));
        assertTrue("fixture: the repairs take most of the builders' sick month", repairs / sick > 0.8);
        assertTrue("fixture: the queue is past the months the builders expand at",
                bm.getRemainingConstructionPoints() / atEvery >= BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING);
        assertTrue("fixture: the builders offer every post", output == atEvery);
        assertTrue("fixture: their own plan ordered one depot", g.getLastInvestment(BUILDERS).startsWith("Built 1 " + DEPOT));
        assertTrue("...which is on site: one, their MAX_CONCURRENT_ORDERS", bm.underConstructionByName(DEPOT)
                == BusinessInvestment.MAX_CONCURRENT_ORDERS && bm.getUnderConstructionBySector(BUILDERS) == 1);
        assertTrue("...the builders' site, not the city's", !bm.isCitySite(key) && bm.addsToTheBuilders(key));
        assertTrue("...among other owners' sites and the city's", bm.siteKeysOnSite().size() >= 5 && bm.citySiteKeys().size() >= 4);
        double total = 0;
        for (String k : bm.siteKeysOnSite()) {
            BuildingsStacks s = bm.stackOfKey(k);
            if (s != null) total += BuildingManager.weightOf(s.getBuilding(), s.getUnderConstruction());
        }
        double weight = BuildingManager.weightOf(depot, 1);
        out.printf("  the depot's crew %.1f of %,.1f on site (%.2f%%): %.2f of the month's %,.0f points%n",
                weight, total, weight / total * 100, output * weight / total, output);
        close("...at its crew's weight: its points to CREW_SCALE_EXPONENT", weight,
                Math.pow(depot.getConstructionPoints(), BuildingManager.CREW_SCALE_EXPONENT), 1e-12);
        assertTrue("the twins stand alike", with.getMonth() == without.getMonth()
                && with.getBuildingOutput() == without.getBuildingOutput()
                && Double.doubleToLongBits(bm.getRemainingConstructionPoints())
                == Double.doubleToLongBits(without.getBuildingManager().getRemainingConstructionPoints())
                && without.getLastInvestment(BUILDERS).equals(g.getLastInvestment(BUILDERS)));
        return new Game[] { without, with };
    }

    /* ============================ 3. BEFORE AND WITH ============================ */

    static void beforeAndWith(Game without, Game with) {
        out.println("\n--- 3. before the rule and with it, the same month: the depot off the top, the rest by the rule ---");
        BuildingManager a = without.getBuildingManager(), b = with.getBuildingManager();
        String key = ConstructionControl.keyOf(template(with, DEPOT));
        String houses = ConstructionControl.keyOf(template(with, "House"));
        double output = with.getBuildingOutput();
        BuildingManager.Plan pa = a.plan(output), pb = b.plan(output);
        int di = pb.keys.indexOf(key);
        double[] rule = b.siteShares(output);

        assertTrue("without the rule no depot is served first", a.firstGroup().isEmpty() && pa.buildersFirst.isEmpty()
                && pa.line() == null);
        bits("...and the depot's share is its crew-rule share", pa.shareOf(key), rule[di]);
        out.printf("  the month's %,.0f points: the depot owes %,.0f, the rule gives it %,.2f%n",
                output, pb.owedOf(key), pb.ruleOf(key));
        assertTrue("with it, the depot is the first group", b.firstGroup().equals(List.of(key))
                && pb.buildersFirst.equals(List.of(key)));
        close("...and takes what it owes, or the whole month if less", pb.shareOf(key), Math.min(pb.owedOf(key), output), 1e-12);
        assertTrue("...more than the rule's share", pb.shareOf(key) > pb.ruleOf(key));
        double[] w = new double[pb.keys.size()], o = pb.owed;
        for (int i = 0; i < pb.keys.size(); i++) {
            BuildingsStacks s = b.stackOfKey(pb.keys.get(i));
            if (i != di && s != null && s.getUnderConstruction() > 0) w[i] = BuildingManager.weightOf(s.getBuilding(), s.getUnderConstruction());
        }
        double[] rest = BuildingManager.waterFill(output - pb.shareOf(key), w, o);
        boolean same = true;
        for (int i = 0; i < pb.keys.size(); i++) if (i != di) same &= Double.doubleToLongBits(pb.shareOf(pb.keys.get(i)))
                == Double.doubleToLongBits(rest[i]);
        assertTrue("every other site, the city's and the landlords', the rule's share of what is left, to the bit", same);
        assertTrue("...the landlords' Houses less than without it", pb.shareOf(houses) < pa.shareOf(houses)
                || pb.shareOf(key) >= output);
        assertTrue("the plan's line says so, for the page", pb.line() != null
                && pb.line().startsWith(BuildingManager.BUILDERS_FIRST));
        out.println("  " + pb.line());

        // ONE WAIT: the depot by the rule among the group, every other site
        // behind the group's points at the month's pace and then by the rule.
        double at = with.getBuildingOutputAtEveryPost();
        double total = 0, mine = 0;
        for (String k : b.siteKeysOnSite()) {
            BuildingsStacks s = b.stackOfKey(k);
            if (s == null) continue;
            double wk = BuildingManager.weightOf(s.getBuilding(), s.getUnderConstruction());
            if (k.equals(key)) mine = wk; else total += wk;
        }
        close("the one wait: the depot's months are what it owes over the whole month", with.siteMonths(key),
                pb.owedOf(key) / at, 1e-9);
        String uni = ConstructionControl.keyOf(template(with, "University"));
        BuildingsStacks u = b.stackOfKey(uni);
        double wu = BuildingManager.weightOf(u.getBuilding(), u.getUnderConstruction());
        close("...the university's, the depot's points at the month's pace and then its own by the rule",
                with.siteMonths(uni), pb.owedOf(key) / at + pb.owedOf(uni) * total / (at * wu), 1e-9);
        out.printf("  the depot's wait: %,.1f months with the rule, %,.1f without%n", with.siteMonths(key), without.siteMonths(key));
        assertTrue("...far shorter than without it", with.siteMonths(key) * 10 < without.siteMonths(key));
        close("a second depot's quote: both depots' points over the whole month", with.quoteCityMonths(template(with, DEPOT), 1),
                (pb.owedOf(key) + template(with, DEPOT).getConstructionPoints()) / at, 1e-9);
        assertTrue("...where without the rule it waits years", without.quoteCityMonths(template(without, DEPOT), 1)
                > 10 * with.quoteCityMonths(template(with, DEPOT), 1));
    }

    /* ============================ 4. THE BUILDERS GROW ============================ */

    /** What the run saw in the city with the rule, the months a depot opened: read by section 5. */
    static int openings = 0, offeredAtOpening = 0, clockUnderAtOpening = 0, monthsFell = 0;

    static void grow(Game without, Game with) {
        out.println("\n--- 4. the builders grow: over a fixed run with the rule, and hold without it ---");
        BuildingManager a = without.getBuildingManager(), b = with.getBuildingManager();
        ham.citybuildersim.sectors.Construction builders = with.getSectors().construction();
        long startA = a.getTotalConstructionCapacity(), startB = b.getTotalConstructionCapacity();
        int depotsA = a.countByName(DEPOT), depotsB = b.countByName(DEPOT);
        double trap = with.getBuildingOutput();
        int points = template(with, DEPOT).getConstructionPoints();
        int bound = (int) Math.ceil(points / trap) + 1;
        out.printf("  the bound: a depot's %,d points over the trap's month of %,.0f, and one more: %d months%n", points, trap, bound);
        int firstOpened = 0, holdingWithout = 0, holdingWith = 0, streak = 0, longestAfter = 0;
        boolean orderedAfter = false;
        for (int m = 1; m <= RUN_MONTHS; m++) {
            int before = b.countByName(DEPOT);
            month(without);
            month(with);
            int now = b.countByName(DEPOT);
            String plan = with.getLastInvestment(BUILDERS);
            // The month's plan runs before its sites advance (runPrivateInvestment(),
            // then simulateMonth()): the month after a depot opens is the first that
            // can order the next.
            if (firstOpened > 0 && m == firstOpened + 1) orderedAfter = plan.startsWith("Built 1 " + DEPOT);
            if (now > before) {
                if (firstOpened == 0) firstOpened = m;
                openings++;
                if (builders.getPostsOfferedShare() >= 1) offeredAtOpening++;
                if (with.getBusinessInvestment().getLossMonths(BUILDERS) < BusinessInvestment.RETIREMENT_LOSS_MONTHS) {
                    clockUnderAtOpening++;
                }
            }
            if (now < before) monthsFell++;
            if (plan.contains("already building")) {
                holdingWith++;
                if (firstOpened > 0) streak++;
            } else {
                streak = 0;
            }
            longestAfter = Math.max(longestAfter, streak);
            if (without.getLastInvestment(BUILDERS).contains("already building")) holdingWithout++;
            out.printf("  month %2d: %2d depots, the sites %,6d a month, owed %,9.0f | %s%n", m, now, with.getBuildingOutput(),
                    b.getRemainingConstructionPoints(), plan.length() > 110 ? plan.substring(0, 110) + "..." : plan);
        }
        long endA = a.getTotalConstructionCapacity(), endB = b.getTotalConstructionCapacity();
        out.printf("  with the rule: %d depots opened in %d months (the first in month %d), nameplate %,d -> %,d;"
                        + " the sites %,.0f -> %,d a month; its plan held 'already building' in %d months%n",
                openings, RUN_MONTHS, firstOpened, startB, endB, trap, with.getBuildingOutput(), holdingWith);
        out.printf("  without it: nameplate %,d -> %,d, the depot %,.1f of %,d points in; its plan held 'already building'"
                        + " in %d of %d months%n", startA, endA,
                a.stackOfKey(ConstructionControl.keyOf(template(without, DEPOT))).getConstructionProgress(), points,
                holdingWithout, RUN_MONTHS);
        assertTrue("with the rule the first depot opens within the bound", firstOpened > 0 && firstOpened <= bound);
        assertTrue("...and the month after, the builders' plan orders the next one", orderedAfter);
        out.printf("  after the first opened, its one order held the plan at most %d months running%n", longestAfter);
        assertTrue("...and after it, its one order never held the plan past the months it expands at, and one more",
                longestAfter <= (int) Math.ceil(BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING) + 1);
        assertTrue("with the rule the builders' nameplate grows", endB > startB
                && endB - startB == (long) (b.countByName(DEPOT) - depotsB) * template(with, DEPOT).makes(Good.BUILDING_WORK));
        assertTrue("...by more than one depot over the run", b.countByName(DEPOT) - depotsB >= 2);
        assertTrue("...and the sites get more than the trap's month", with.getBuildingOutput() > trap);
        bits("without the rule the builders' nameplate holds", endA, startA);
        assertTrue("...their depot still on site, not open", a.countByName(DEPOT) == depotsA && a.underConstructionByName(DEPOT) == 1);
        assertTrue("...and their plan said 'already building' every month", holdingWithout == RUN_MONTHS);
    }

    /* ============================ 5. NEVER LAID OFF ============================ */

    static void neverLaidOff() {
        out.println("\n--- 5. never laid off the month it opens: every post offered, no depot retired ---");
        out.printf("  the run's %d openings: every post offered at %d, the loss clock under %d at %d; months a depot was"
                        + " retired %d%n", openings, offeredAtOpening, BusinessInvestment.RETIREMENT_LOSS_MONTHS,
                clockUnderAtOpening, monthsFell);
        assertTrue("fixture: depots opened in the run", openings > 1);
        assertTrue("every month a depot opens the builders offer every post: none of its crews laid off",
                offeredAtOpening == openings);
        assertTrue("...and their loss clock is under the RETIREMENT_LOSS_MONTHS the spare-capacity rule waits for",
                clockUnderAtOpening == openings);
        assertTrue("no depot is retired in any month of the run", monthsFell == 0);
    }

    /* ============================ 6. ONLY WHAT IT COULD STAFF ============================ */

    /** The months the new town is played: long enough for its first depot to open, crewed first, and its homes to come or not. */
    static final int NEW_TOWN_MONTHS = 6;

    static void onlyWhatItCouldStaff(Path root) {
        out.println("\n--- 6. only a depot the city could staff: a new town's founding depots, crewed by the rule ---");
        // MortgageCheck's new town: its works standing, its four founding depots
        // on site, a hundred people; the landlords build. Twins: one as the game
        // has it, one told the town could staff anything.
        MortgageCheck.out = out;
        MortgageCheck.quiet = quiet;
        Game gated = MortgageCheck.landlordCity(root, "town-gated", true);
        Game ungated = MortgageCheck.landlordCity(root, "town-ungated", true);
        ungated.getBuildingManager().setBuildersCouldStaff(null);
        BuildingManager bm = gated.getBuildingManager();
        BuildingsTemplate depot = template(gated, DEPOT);
        String key = ConstructionControl.keyOf(depot);
        int onSite = bm.underConstructionByName(DEPOT);
        ham.citybuildersim.sectors.Construction builders = gated.getSectors().construction();
        Sector.Staffing staffing = builders.staffing(depot, onSite);
        out.printf("  %,d people; %d depots on site, the town could staff %.0f%% of their posts%n",
                gated.getPopulationManager().getPopulation(), onSite, staffing.share * 100);
        assertTrue("fixture: depots on site in a new town", onSite > 0 && bm.getTotalHomes() < 100);
        assertTrue("fixture: by the builders' own test it could not staff them", !staffing.passes());
        assertTrue("the depots are not served first", bm.firstGroup().isEmpty() && !bm.goesFirst(depot, onSite));
        double output = gated.getBuildingOutput();
        BuildingManager.Plan p = bm.plan(output);
        double[] rule = bm.siteShares(output, null, 0);
        boolean same = p.share.length == rule.length;
        for (int i = 0; same && i < rule.length; i++) same = Double.doubleToLongBits(p.share[i]) == Double.doubleToLongBits(rule[i]);
        assertTrue("...the plan is the crews' rule's, every slot to the bit", same && p.buildersFirst.isEmpty());
        long homesAtStart = bm.getTotalHomes();
        double floorG = Double.MAX_VALUE, floorU = Double.MAX_VALUE, fillU = 1;
        int firstG = 0, firstU = 0;
        for (int m = 1; m <= NEW_TOWN_MONTHS; m++) {
            month(gated);
            month(ungated);
            BuildingManager.Plan pg = bm.getLastPlan(), pu = ungated.getBuildingManager().getLastPlan();
            if (pg != null && !pg.buildersFirst.isEmpty()) firstG++;
            if (pu != null && !pu.buildersFirst.isEmpty()) firstU++;
            floorG = Math.min(floorG, gated.getBuildingOutput());
            floorU = Math.min(floorU, ungated.getBuildingOutput());
            fillU = Math.min(fillU, ungated.getSectors().construction().getAverageFill());
        }
        out.printf("  months the depots went first: %d in the town with the test, %d in the twin without it%n", firstG, firstU);
        assertTrue("in the town with the test its depots never went first", firstG == 0);
        assertTrue("...where the twin told it could staff them built them first", firstU > 0);
        BuildingManager bu = ungated.getBuildingManager();
        out.printf("  after %d months: with the test %d depots open, the sites at least %,.0f a month, %,d homes, %,d people;"
                        + " without it %d open, the builders' fill down to %.2f, the sites at least %,.0f, %,d homes, %,d people%n",
                NEW_TOWN_MONTHS, bm.countByName(DEPOT), floorG, bm.getTotalHomes(), gated.getPopulationManager().getPopulation(),
                bu.countByName(DEPOT), fillU, floorU, bu.getTotalHomes(), ungated.getPopulationManager().getPopulation());
        assertTrue("without the test a depot opens early", bu.countByName(DEPOT) > bm.countByName(DEPOT));
        assertTrue("...the builders' fill falls under MIN_STAFFABLE_TO_ORDER", fillU < Sector.MIN_STAFFABLE_TO_ORDER);
        assertTrue("...and their month under the repairs: the sites get nothing", floorU == 0);
        assertTrue("...so no home is built", bu.getTotalHomes() == homesAtStart);
        assertTrue("with the test the sites get work every month", floorG > 0);
        assertTrue("...and the town builds its homes", bm.getTotalHomes() > bu.getTotalHomes());
    }

    /* ============================ 7. ONE PRIORITY ============================ */

    static void onePriority(Path root) {
        out.println("\n--- 7. one priority with the death care: the depot and the cemetery share the first group's month ---");
        // The trap with the dead waiting, the death-care rule live: the
        // builders let plan, a month played - their depot ordered, the
        // cemetery served first - and both on site at the next.
        Game g = DeathCareFirstCheck.city(root, "both");
        DeathCareFirstCheck.sites(g);
        g.getBusinessInvestment().releaseSector(BUILDERS);
        month(g);
        BuildingManager bm = g.getBuildingManager();
        String depot = ConstructionControl.keyOf(template(g, DEPOT));
        String cem = ConstructionControl.keyOf(template(g, DeathCareFirstCheck.CEMETERY));
        assertTrue("fixture: the dead lie unburied", g.getHealthcare().getUnburied() > 0);
        assertTrue("fixture: the builders' depot and the city's cemetery both on site",
                bm.underConstructionByName(DEPOT) == 1 && bm.underConstructionByName(DeathCareFirstCheck.CEMETERY) == 1);
        double output = g.getBuildingOutput();
        BuildingManager.Plan p = bm.plan(output);
        assertTrue("the first group is the depot and the cemetery", bm.firstGroup().size() == 2
                && bm.firstGroup().containsAll(List.of(depot, cem)));
        assertTrue("...and the plan serves both: the builders' and the death care", p.buildersFirst.equals(List.of(depot))
                && p.deathCareFirst.equals(List.of(cem)));
        int di = p.keys.indexOf(depot), ci = p.keys.indexOf(cem);
        BuildingsStacks sd = bm.stackOfKey(depot), sc = bm.stackOfKey(cem);
        double wd = BuildingManager.weightOf(sd.getBuilding(), sd.getUnderConstruction());
        double wc = BuildingManager.weightOf(sc.getBuilding(), sc.getUnderConstruction());
        double[] both = BuildingManager.waterFill(output, new double[] { wd, wc }, new double[] { p.owed[di], p.owed[ci] });
        out.printf("  the month's %,.0f points: the depot owes %,.0f (crew %.1f), the cemetery %,.0f (crew %.1f);"
                        + " the depot takes %,.2f, the cemetery %,.2f%n",
                output, p.owed[di], wd, p.owed[ci], wc, p.share[di], p.share[ci]);
        bits("the depot takes its crews' share of the group's month", p.share[di], both[0]);
        bits("...and the cemetery its", p.share[ci], both[1]);
        close("together what they owe, or the whole month if less", p.share[di] + p.share[ci],
                Math.min(output, p.owed[di] + p.owed[ci]), 1e-12);
        assertTrue("neither is starved: each more than its crews' share among everyone", p.share[di] > p.ruleOf(depot)
                && p.share[ci] > p.ruleOf(cem));
        assertTrue("...and neither leads: each less than what it owes while the other still owes",
                output >= p.owed[di] + p.owed[ci] || (p.share[di] < p.owed[di] && p.share[ci] < p.owed[ci]));
        assertTrue("the city's order has the cemetery at its top", bm.cityOrder().get(0).equals(cem) && bm.cityOrderFirst() == 1);
        assertTrue("the plan's line names both", p.line() != null && p.line().startsWith(BuildingManager.BUILDERS_FIRST)
                && p.line().contains("unburied"));
        out.println("  " + p.line());
        double at = g.getBuildingOutputAtEveryPost();
        close("the one wait: the cemetery by the crews' rule among the group",
                g.siteMonths(cem), p.owed[ci] * (wd + wc) / (at * wc), 1e-9);
        close("...and the depot", g.siteMonths(depot), p.owed[di] * (wd + wc) / (at * wd), 1e-9);

        // The player's order, the city's other sites reversed: the city's share
        // of what the group leaves, top down in it.
        List<String> mine = new ArrayList<>(bm.citySiteKeys());
        mine.remove(cem);
        java.util.Collections.reverse(mine);
        bm.getControl().setOrder(mine);
        for (double month : new double[] { output, 4.0 * (p.owed[di] + p.owed[ci]) }) {
            BuildingManager.Plan q = bm.plan(month);
            String at2 = String.format(" (%,.0f points)", month);
            double took = q.shareOf(depot) + q.shareOf(cem);
            close("with the player's order set the group still takes what it owes, or the month" + at2, took,
                    Math.min(month, q.owedOf(depot) + q.owedOf(cem)), 1e-12);
            double[] w = new double[q.keys.size()];
            for (int i = 0; i < q.keys.size(); i++) {
                String k = q.keys.get(i);
                BuildingsStacks s = bm.stackOfKey(k);
                if (!k.equals(depot) && !k.equals(cem) && s != null && s.getUnderConstruction() > 0)
                    w[i] = BuildingManager.weightOf(s.getBuilding(), s.getUnderConstruction());
            }
            double[] rest = BuildingManager.waterFill(month - took, w, q.owed);
            double pool = 0;
            for (String k : mine) { int i = q.keys.indexOf(k); if (i >= 0) pool += rest[i]; }
            String houses = ConstructionControl.keyOf(template(g, "House"));
            bits("...the landlords' Houses the rule's share of what is left" + at2, q.shareOf(houses), rest[q.keys.indexOf(houses)]);
            double left = pool;
            for (String k : mine) {
                double give = Math.max(0, Math.min(q.owedOf(k), left));
                close("...then " + bm.nameOfSite(k) + " in the player's order, top down" + at2, q.shareOf(k), give, 1e-12);
                left -= give;
            }
        }
        assertTrue("the city's order keeps the cemetery at its top, the rest in the player's order",
                bm.cityOrder().get(0).equals(cem) && bm.cityOrder().subList(1, bm.cityOrder().size()).equals(mine));
        bm.getControl().clearOrder();
    }

    /* ============================ 8. CONSERVED ============================ */

    static void conserved() {
        out.println("\n--- 8. money is conserved ---");
        out.printf("  %d months audited; the worst residual %.3g, the worst drift after the audit %.3g%n",
                monthsAudited, worstResidual, worstDrift);
        assertTrue("every month played here was audited", monthsAudited > 0);
        assertTrue("the money audit's worst month is within 0.01% of what moved", worstResidual < 1e-4);
        assertTrue("...and nothing moved a pool after it", Math.abs(worstDrift) < 1e-6);
    }
}

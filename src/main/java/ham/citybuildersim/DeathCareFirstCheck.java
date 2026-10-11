package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Death care first while the dead wait (0.7.129, batch T1-B): the rule that
 * serves the city's own cemeteries and crematoria first out of its share of
 * the crews while anybody lies unburied, held on a city that is caught in
 * the trap it breaks.
 *
 * WHY. Batch T1 traced Jerus's construction-bound city (the project's
 * why-the-builders-stall.md): its cemeteries full and no crematorium, the
 * dead at Healthcare's cap made Health's capped share of the city sick, the
 * sick builders' month went almost all on repairs, and the cemetery that
 * would have buried the dead got its crew-rule share of what was left - a
 * thousand months. Nothing in the model broke the circle. Jerus chose the
 * rule (2026-10-10). The city in the trace is an old save the game now
 * refuses, so this fixture is the evidence: it causes the trap - no plot,
 * no crematorium, the dead at the cap, repairs taking most of the builders'
 * month - and plays it with the rule and without it.
 *
 * What this has to prove:
 *   1. THE RULE'S ARITHMETIC: of the city's share as the crews' rule gave
 *      it, the death care takes what it owes, top down, or what is left if
 *      less; the city's other sites share the rest by the crews' rule
 *      (BuildingManager.waterFill()); every other owner's slot to the bit,
 *      and the city's total the rule's; with the player's order, the death
 *      care heads it and the rest follow in the player's order.
 *   2. THE TRAP, CAUSED: no plot and no crematorium, the unburied at
 *      Healthcare.MAX_BACKLOG_MONTHS of deaths, Health's unburied term at
 *      MAX_UNBURIED_SICKNESS, and repairs taking all but a year's share of
 *      a cemetery's points of the builders' sick month; a Memorial Cemetery
 *      on site among the city's other sites, and the landlords' Houses.
 *   3. BEFORE AND WITH THE RULE, the same month in twin cities: without it
 *      the cemetery's share is its crew-rule share, to the bit; with it,
 *      what it owes or the city's pool; the landlords' site the same to the
 *      bit in both; the order, the plan's line and the one wait say so.
 *   4. THE LOOP BREAKS: with the rule the cemetery opens within the months
 *      its points take at the trap's city share (and one more), the
 *      unburied fall to none and the sick rate to its served level - every
 *      part of it but the dead's - and the sites get more; the twin without
 *      it is in the trap still, the cemetery a long way off.
 *   5. THE PLAYER'S ORDER: set with the death care at its bottom, the death
 *      care still heads it while the dead wait, and the rest keep the
 *      player's order.
 *   6. ONCE NOBODY LIES UNBURIED: a death-care site on site is served as
 *      before - no death care first, the order the player's (or the
 *      stacks'), the plan the rule's to the bit, and no plan applied.
 *   7. MONEY IS CONSERVED: the money audit every month played here.
 *
 * Every fixture causes its condition. How often the rule fires in a real
 * run is LongPlaytest's line "the dead waited".
 */
public class DeathCareFirstCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-84s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= Math.max(tol, Math.abs(expected) * tol);
        if (!ok) fails++;
        out.printf("%-84s %s  %,.9f against %,.9f%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    static void bits(String label, double actual, double expected) {
        boolean ok = Double.doubleToLongBits(actual) == Double.doubleToLongBits(expected);
        if (!ok) fails++;
        out.printf("%-84s %s  %s against %s%n", label, ok ? "OK" : "FAIL", actual, expected);
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

    static final String RE = Sectors.REAL_ESTATE;

    /** The death care the fixture puts on site: the smaller cemetery, which serves the dead by its care type. */
    static final String CEMETERY = "Memorial Cemetery";

    /** The road whose repairs fill the builders' month: it has its own repairs share (0.7.103), and carries traffic, nothing else. */
    static final String ROAD = "Paved Road";

    /**
     * The months the cemetery's points take at the trap's city share, in
     * the fixture: the trace's measure A opened Jerus's in about twelve
     * (why-the-builders-stall.md), so the repairs leave the builders' sick
     * month a year's share of the cemetery's points over them.
     */
    static final int TRAP_MONTHS = 12;

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
        Path root = Files.createTempDirectory("deathcarefirstcheck");

        arithmetic();
        Game[] twins = trap(root);
        beforeAndWith(twins[0], twins[1]);
        loopBreaks(twins[0], twins[1]);
        playersOrder(root);
        onceBuried(twins[1]);
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
        out.println("--- 1. the rule's arithmetic: the death care first out of the city's share, the rest by the rule ---");
        // Five slots as a plan has them: three city sites (a cemetery, a
        // university, two schools), the empty slot, the landlords' houses.
        List<String> keys = List.of("B27", "B35", "B32", "", "B0");
        double[] weight = { crew(900, 1), crew(26_000, 1), crew(2_100, 2), 0, crew(10, 40) };
        double[] owed = { 900, 26_000, 4_200, 0, 400 };
        List<String> city = List.of("B27", "B35", "B32");
        List<String> dead = List.of("B27");
        for (double output : new double[] { 80, 5_000 }) {
            double[] rule = BuildingManager.waterFill(output, weight, owed);
            double[] share = rule.clone();
            BuildingManager.applyDeathCareFirst(share, owed, weight, keys, dead, city);
            double pool = rule[0] + rule[1] + rule[2];
            String at = String.format(" (%,.0f points)", output);
            close("the cemetery takes what it owes, or the city's pool if less" + at, share[0], Math.min(owed[0], pool), 1e-12);
            assertTrue("...which is more than the crews' rule gave it" + at, share[0] > rule[0]);
            close("the city's sites together keep the share the rule gave them" + at,
                    share[0] + share[1] + share[2], pool, 1e-12);
            double[] rest = BuildingManager.waterFill(pool - share[0],
                    new double[] { weight[1], weight[2] }, new double[] { owed[1], owed[2] });
            bits("the rest share what is left by the crews' rule: the university" + at, share[1], rest[0]);
            bits("...and the schools" + at, share[2], rest[1]);
            assertTrue("...neither more than the rule gave it" + at, share[1] <= rule[1] && share[2] <= rule[2]);
            bits("the landlords' site keeps the rule's share, to the bit" + at, share[4], rule[4]);
        }
        // ...and with the player's order: the death care at its top, the rest as the player set them.
        List<String> mine = List.of("B32", "B35", "B27");
        List<String> served = BuildingManager.firstTheDead(mine, dead);
        assertTrue("with the player's order, the death care heads it and the rest keep theirs",
                served.equals(List.of("B27", "B32", "B35")));
        assertTrue("...and with nobody unburied the order is the player's, the same list",
                BuildingManager.firstTheDead(mine, List.of()) == mine);
        double[] rule = BuildingManager.waterFill(80, weight, owed);
        double[] share = rule.clone();
        BuildingManager.applyPriority(share, owed, keys, served);
        close("top down in that order, the cemetery first: what it owes, or the pool",
                share[0], Math.min(owed[0], rule[0] + rule[1] + rule[2]), 1e-12);
        bits("...and the landlords' site to the bit", share[4], rule[4]);
    }

    /* ============================ 2. THE TRAP, CAUSED ============================ */

    /**
     * A city caught in the trap: works, homes, a clinic for everybody, one
     * depot, every sector held so no planner builds or scraps anything but
     * by this fixture's hand - the founding churchyard full and no
     * crematorium, so the dead lie where they fall until Healthcare's cap -
     * and a Penitentiary of the city's on site all the while, as in the
     * trace's city, which keeps the builders' every post offered. Then
     * enough Paved Roads that the repairs leave the builders' sick month
     * TRAP_MONTHS' share of a cemetery's points, and on site a Memorial
     * Cemetery, a University and two Middle Schools of the city's and forty
     * Houses of the landlords'.
     */
    static Game city(Path root, String name) {
        GameFiles files = new GameFiles(root.resolve(name), root.resolve(name + "-no-legacy"));
        Game g = new Game(files);
        quietly(() -> {
            g.run();
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 120_000_000L);
            for (Sector s : g.getSectors().all()) g.getBusinessInvestment().holdSector(s.key());
            for (String[] w : new String[][] {
                    { "Industrial Bakery", "3" }, { "Wind Farm", "4" }, { "Water Treatment Plant", "1" },
                    { "Commercial Bank", "1" }, { "Convenience Store", "6" }, { "House", "400" },
                    { "Elementary School", "3" }, { "Walk-in Clinic", "2" }, { "Construction Depot", "1" },
                    { ROAD, "4" } }) {
                g.buildStack(template(g, w[0]), Integer.parseInt(w[1]), true);
            }
            g.buildStack(template(g, "Penitentiary"), 1, false);
            // The churchyard the city was founded with, full (Healthcare.FOUNDING_PLOTS
            // used: the state's first figure), and no crematorium.
            double[] state = g.getHealthcare().getState();
            state[0] = Healthcare.FOUNDING_PLOTS;
            g.getHealthcare().restore(state);
            // Three years with nowhere to put the dead: the backlog reaches its cap.
            g.simulateMonths(36);
        });
        return g;
    }

    /** ...and the roads and the sites: the half of the fixture that follows the trap. */
    static void sites(Game g) {
        BuildingsTemplate cemetery = template(g, CEMETERY);
        BuildingsTemplate road = template(g, ROAD);
        double perRoad = road.getConstructionPoints() * road.repairsPerYear() / 12;
        double leave = cemetery.getConstructionPoints() / (double) TRAP_MONTHS;
        int roads = (int) Math.floor((g.getConstructionOutputAtEveryPost() - g.getMaintenancePoints() - leave) / perRoad);
        quietly(() -> {
            if (roads > 0) g.buildStack(road, roads, true);
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.buildStack(template(g, "University"), 1, false);
            g.buildStack(template(g, "Middle School"), 2, false);
            g.buildStack(cemetery, 1, false);
            g.getEconomyManager().setSectorCash(RE, g.getEconomyManager().getSectorCash(RE) + 200_000);
            g.buildFor(g.getSectorInvestor(RE), template(g, "House"), 40);
        });
    }

    static Game[] trap(Path root) {
        out.println("\n--- 2. the trap, caused: no plot, the dead at the cap, repairs taking the builders' month ---");
        Game without = city(root, "without"), with = city(root, "with");
        // The twin without the rule: the hook Game sets, told nobody lies unburied.
        without.getBuildingManager().setUnburied(() -> 0);
        sites(without);
        sites(with);
        Game g = with;
        Healthcare hc = g.getHealthcare();
        Health health = g.getHealth();
        out.printf("  %,d people, %,.1f deaths a month, %,.0f unburied; sick %.1f%% (the dead %.1f%%);"
                        + " nameplate %,d, output %,d, repairs %,.0f, sites %,d%n",
                g.getPopulationManager().getPopulation(), hc.getDeaths(), hc.getUnburied(),
                health.getSickRate() * 100, health.getUnburiedRate() * 100,
                g.getBuildingManager().getTotalConstructionCapacity(), g.getConstructionOutputAtEveryPost(),
                g.getMaintenancePoints(), g.getBuildingOutputAtEveryPost());
        assertTrue("fixture: no plot free, the founding churchyard full", hc.getPlotsBuilt() > 0
                && hc.getPlotsUsed() >= hc.getPlotsBuilt());
        assertTrue("fixture: and no crematorium", hc.getCremationCapacity() == 0);
        assertTrue("fixture: the dead lie unburied", hc.getUnburied() > 0);
        close("...at Healthcare's cap: MAX_BACKLOG_MONTHS of the month's deaths",
                hc.getUnburied(), hc.getDeaths() * Healthcare.MAX_BACKLOG_MONTHS, 0.05);
        close("they add Health's capped share to the sick rate", health.getUnburiedRate(),
                Health.MAX_UNBURIED_SICKNESS, 1e-12);
        double repairsShare = g.getMaintenancePoints() / g.getConstructionOutputAtEveryPost();
        out.printf("  repairs take %.1f%% of the sick builders' month (%.1f%% of their nameplate)%n", repairsShare * 100,
                g.getMaintenancePoints() / g.getBuildingManager().getTotalConstructionCapacity() * 100);
        assertTrue("fixture: the repairs take most of the builders' sick month", repairsShare > 0.8);
        BuildingManager bm = g.getBuildingManager();
        String cem = ConstructionControl.keyOf(template(g, CEMETERY));
        assertTrue("fixture: the cemetery is the city's own site", bm.isCitySite(cem));
        assertTrue("fixture: among other city sites, the Penitentiary still owing most of its points",
                bm.citySiteKeys().size() >= 4 && bm.stackOfKey(ConstructionControl.keyOf(template(g, "Penitentiary")))
                        .getConstructionProgress() < template(g, "Penitentiary").getConstructionPoints() / 2.0);
        assertTrue("fixture: the builders offer every post", g.getBuildingOutput() == g.getBuildingOutputAtEveryPost());
        String houses = ConstructionControl.keyOf(template(g, "House"));
        assertTrue("fixture: and the landlords' Houses on site, not the city's", bm.siteKeysOnSite().contains(houses)
                && !bm.isCitySite(houses));
        assertTrue("fixture: the twins stand alike", with.getMonth() == without.getMonth()
                && Double.doubleToLongBits(with.getHealthcare().getUnburied())
                == Double.doubleToLongBits(without.getHealthcare().getUnburied())
                && with.getBuildingOutputAtEveryPost() == without.getBuildingOutputAtEveryPost());
        return new Game[] { without, with };
    }

    /* ============================ 3. BEFORE AND WITH ============================ */

    static void beforeAndWith(Game without, Game with) {
        out.println("\n--- 3. before the rule and with it, the same month: only the city's sites move ---");
        BuildingManager a = without.getBuildingManager(), b = with.getBuildingManager();
        String cem = ConstructionControl.keyOf(template(with, CEMETERY));
        String houses = ConstructionControl.keyOf(template(with, "House"));
        double output = with.getBuildingOutput();
        BuildingManager.Plan pa = a.plan(output), pb = b.plan(output);
        int ci = pb.keys.indexOf(cem);
        double[] rule = b.siteShares(output);

        assertTrue("without the rule no death care is served first", a.deathCareFirst().isEmpty() && pa.line() == null);
        bits("...and the cemetery's share is its crew-rule share", pa.shareOf(cem), rule[ci]);
        assertTrue("...the city's order the stacks'", a.cityOrder().equals(a.citySiteKeys()));
        double pool = 0;
        for (String k : b.citySiteKeys()) pool += pb.ruleOf(k);
        out.printf("  the month's %,.0f points: the city's pool %,.1f; the cemetery owes %,.0f, the rule gives it %,.2f%n",
                output, pool, pb.owedOf(cem), pb.ruleOf(cem));
        assertTrue("with it, the cemetery is served first", b.deathCareFirst().equals(List.of(cem))
                && pb.deathCareFirst.equals(List.of(cem)));
        close("...and takes what it owes, or the city's pool if less", pb.shareOf(cem), Math.min(pb.owedOf(cem), pool), 1e-12);
        assertTrue("...more than the rule's share", pb.shareOf(cem) > pb.ruleOf(cem));
        double cityAfter = 0;
        for (String k : b.citySiteKeys()) cityAfter += pb.shareOf(k);
        close("the city's sites together keep the rule's share", cityAfter, pool, 1e-12);
        bits("the landlords' site's share is the rule's in the city with the rule", pb.shareOf(houses), pb.ruleOf(houses));
        bits("...and the same as in the city without it", pb.shareOf(houses), pa.shareOf(houses));
        assertTrue("the city's order has the cemetery at its top", b.cityOrder().get(0).equals(cem)
                && !a.citySiteKeys().get(0).equals(cem));
        assertTrue("the plan's line says so, for the page", pb.line() != null
                && pb.line().startsWith(BuildingManager.DEATH_CARE_FIRST));
        out.println("  " + pb.line());

        // ONE WAIT: the cemetery as the top of the city's order, every other
        // city site behind the death care and then by the rule.
        double at = with.getBuildingOutputAtEveryPost();
        double total = 0, cityWeight = 0, deadOwed = 0;
        for (String k : b.siteKeysOnSite()) {
            BuildingsStacks s = b.stackOfKey(k);
            double w = BuildingManager.weightOf(s.getBuilding(), s.getUnderConstruction());
            total += w;
            if (b.isCitySite(k)) cityWeight += w;
        }
        deadOwed = pb.owedOf(cem);
        double pace = at * cityWeight / total;
        close("the one wait: the cemetery's months are what it owes over the city's pace", with.siteMonths(cem),
                deadOwed / pace, 1e-9);
        String uni = ConstructionControl.keyOf(template(with, "University"));
        BuildingsStacks u = b.stackOfKey(uni);
        double wu = BuildingManager.weightOf(u.getBuilding(), u.getUnderConstruction());
        close("...the university's, the death care's at that pace and then its own by the rule", with.siteMonths(uni),
                deadOwed / pace + pb.owedOf(uni) * total / (at * wu), 1e-9);
        bits("...the landlords' the rule's in both cities", with.siteMonths(houses), without.siteMonths(houses));
        out.printf("  the cemetery's wait: %,.1f months with the rule, %,.1f without%n",
                with.siteMonths(cem), without.siteMonths(cem));
        assertTrue("...far shorter than without it", with.siteMonths(cem) * 5 < without.siteMonths(cem));
        assertTrue("a second cemetery's quote is shorter with the rule too",
                with.quoteCityMonths(template(with, CEMETERY), 1) < without.quoteCityMonths(template(without, CEMETERY), 1));
    }

    /* ============================ 4. THE LOOP BREAKS ============================ */

    static void loopBreaks(Game without, Game with) {
        out.println("\n--- 4. the loop breaks: the cemetery opens, the dead are buried, the crews come back ---");
        BuildingManager b = with.getBuildingManager();
        String cem = ConstructionControl.keyOf(template(with, CEMETERY));
        String houses = ConstructionControl.keyOf(template(with, "House"));
        double output = with.getBuildingOutput();
        BuildingManager.Plan first = b.plan(output);
        double pool = 0;
        for (String k : b.citySiteKeys()) pool += first.ruleOf(k);
        double owed = first.owedOf(cem);
        // The bound: its points at the trap's city share, and the month it opens in.
        int bound = (int) Math.ceil(owed / pool) + 1;
        double trapSites = with.getBuildingOutput();
        out.printf("  the bound: %,.0f points over the trap's city share of %,.1f a month, and one more: %d months%n",
                owed, pool, bound);

        month(without);
        month(with);
        BuildingManager.Plan applied = b.getLastPlan();
        assertTrue("the month's advance applied the rule", applied != null && applied.deathCareFirst.equals(List.of(cem)));
        bits("...and built the landlords' Houses as the city without it did", b.stackOfKey(houses).getConstructionProgress(),
                without.getBuildingManager().stackOfKey(houses).getConstructionProgress());

        int opened = with.getBuildingManager().countByName(CEMETERY) > 0 ? 1 : 0;
        int m = 1;
        while (opened == 0 && m < bound + 24) {
            month(without);
            month(with);
            m++;
            if (with.getBuildingManager().countByName(CEMETERY) > 0) opened = m;
        }
        out.printf("  with the rule the cemetery opened in month %d; the unburied %,.0f, sick %.2f%% (the dead %.2f%%)%n",
                opened, with.getHealthcare().getUnburied(), with.getHealth().getSickRate() * 100,
                with.getHealth().getUnburiedRate() * 100);
        assertTrue("with the rule the cemetery opens", opened > 0);
        assertTrue("...within the bound", opened > 0 && opened <= bound);
        bits("the month it opens the dead are buried", with.getHealthcare().getUnburied(), 0);
        bits("...and Health's unburied term is none", with.getHealth().getUnburiedRate(), 0);
        Health h = with.getHealth();
        close("the sick rate is its served level: every part of it but the dead's", h.getSickRate(),
                Math.min(Health.MAX_SICK_RATE, h.getBaselineRate() + h.getOutbreakSeverity() + h.getHungerRate()
                        + h.getUnhousedRate() + h.getInjuryRate()), 1e-12);
        Healthcare twin = without.getHealthcare();
        out.printf("  without it, month %d: the cemetery %d built, %,.0f of %,d points in; the unburied %,.0f, sick %.2f%%%n",
                m, without.getBuildingManager().countByName(CEMETERY),
                without.getBuildingManager().stackOfKey(cem).getConstructionProgress(),
                template(without, CEMETERY).getConstructionPoints(), twin.getUnburied(),
                without.getHealth().getSickRate() * 100);
        assertTrue("without the rule the cemetery is not open", without.getBuildingManager().countByName(CEMETERY) == 0);
        assertTrue("...the dead still lie unburied", twin.getUnburied() > 0);
        close("...and still make Health's capped share sick", without.getHealth().getUnburiedRate(),
                Health.MAX_UNBURIED_SICKNESS, 1e-12);
        assertTrue("...the cemetery still years off", without.siteMonths(cem) > 5 * bound);

        // ...and the crews come back: a month on, the sites get more than in the trap.
        month(without);
        month(with);
        out.printf("  the next month's site output: %,d with the rule, %,d without; %,.0f in the trap%n",
                with.getBuildingOutput(), without.getBuildingOutput(), trapSites);
        assertTrue("the month after, the sites get more than the city without the rule gives them",
                with.getBuildingOutput() > without.getBuildingOutput());
        assertTrue("...and more than in the trap", with.getBuildingOutput() > trapSites);
        assertTrue("with the dead buried no death care is served first", b.deathCareFirst().isEmpty()
                && b.getLastPlan() == null);
    }

    /* ============================ 5. THE PLAYER'S ORDER ============================ */

    static void playersOrder(Path root) {
        out.println("\n--- 5. the player's order: the death care heads it while the dead wait, the rest keep theirs ---");
        Game g = city(root, "ordered");
        sites(g);
        BuildingManager bm = g.getBuildingManager();
        String cem = ConstructionControl.keyOf(template(g, CEMETERY));
        List<String> mine = new ArrayList<>(bm.citySiteKeys());
        mine.remove(cem);
        java.util.Collections.reverse(mine);
        mine.add(cem);
        bm.getControl().setOrder(mine);
        List<String> order = bm.cityOrder();
        List<String> expected = new ArrayList<>();
        expected.add(cem);
        for (String k : mine) if (!k.equals(cem)) expected.add(k);
        assertTrue("an order with the cemetery at its bottom: the cemetery heads it while the dead wait",
                order.equals(expected));
        // The trap's month, and a month of twice the cemetery's points, which
        // leaves the rest of the order something once the cemetery has its own.
        double trap = g.getBuildingOutput();
        for (double output : new double[] { trap, 2.0 * template(g, CEMETERY).getConstructionPoints() }) {
            BuildingManager.Plan p = bm.plan(output);
            String at = String.format(" (%,.0f points)", output);
            double pool = 0;
            for (String k : bm.citySiteKeys()) pool += p.ruleOf(k);
            close("...takes what it owes, or the city's pool" + at, p.shareOf(cem), Math.min(p.owedOf(cem), pool), 1e-12);
            double left = pool - p.shareOf(cem);
            if (output > trap) assertTrue("...which leaves the rest something" + at, left > 0);
            for (String k : order) {
                if (k.equals(cem)) continue;
                double give = Math.max(0, Math.min(p.owedOf(k), left));
                close("...then " + bm.nameOfSite(k) + " in the player's order, top down" + at, p.shareOf(k), give, 1e-12);
                left -= give;
            }
            assertTrue("the plan's line says so" + at, p.line() != null);
        }
    }

    /* ============================ 6. ONCE NOBODY LIES UNBURIED ============================ */

    static void onceBuried(Game g) {
        out.println("\n--- 6. once nobody lies unburied: death care on site is served as before ---");
        BuildingManager bm = g.getBuildingManager();
        String cem = ConstructionControl.keyOf(template(g, CEMETERY));
        quietly(() -> {
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.buildStack(template(g, CEMETERY), 1, false);
        });
        assertTrue("fixture: nobody lies unburied", g.getHealthcare().getUnburied() == 0);
        assertTrue("fixture: a cemetery of the city's on site among its other sites",
                bm.isCitySite(cem) && bm.citySiteKeys().size() >= 2);
        assertTrue("no death care is served first", bm.deathCareFirst().isEmpty());
        assertTrue("the city's order is the stacks', the death care where its stack stands, not at the top",
                bm.cityOrder().equals(bm.citySiteKeys()) && !bm.cityOrder().get(0).equals(cem));
        double output = g.getBuildingOutput();
        BuildingManager.Plan p = bm.plan(output);
        double[] rule = bm.siteShares(output);
        boolean same = true;
        for (int i = 0; i < rule.length; i++) same &= Double.doubleToLongBits(p.share[i]) == Double.doubleToLongBits(rule[i]);
        assertTrue("the plan is the rule's, every slot to the bit", same);
        assertTrue("...and has no line", p.line() == null);
        List<String> mine = new ArrayList<>(bm.citySiteKeys());
        java.util.Collections.reverse(mine);
        bm.getControl().setOrder(mine);
        assertTrue("with the player's order set, the order is the player's, exactly", bm.cityOrder().equals(mine));
        bm.getControl().clearOrder();
        month(g);
        assertTrue("the month's advance applied no plan", bm.getLastPlan() == null);
    }

    /* ============================ 7. CONSERVED ============================ */

    static void conserved() {
        out.println("\n--- 7. money is conserved ---");
        out.printf("  %d months audited; the worst residual %.3g, the worst drift after the audit %.3g%n",
                monthsAudited, worstResidual, worstDrift);
        assertTrue("every month played here was audited", monthsAudited > 0);
        assertTrue("the money audit's worst month is within 0.01% of what moved", worstResidual < 1e-4);
        assertTrue("...and nothing moved a pool after it", Math.abs(worstDrift) < 1e-6);
    }
}

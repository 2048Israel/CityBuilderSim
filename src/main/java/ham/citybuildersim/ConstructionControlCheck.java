package ham.citybuildersim;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

/**
 * The player's hand on the construction queue (0.7.22): each of
 * ConstructionControl's five rules held to its own arithmetic, in a played
 * city.
 *
 * WHY. Jerus asked to reprioritise, cancel and demolish, and chose how
 * (2026-09-30): priority "Both", cancel "Keep the half-built shell",
 * demolish "City's, plus buy-outs". Every rule has a source and a figure,
 * and every one moves money between the treasury, the builders' book, the
 * owners' tills and the households; a rule that is right on the screen
 * and wrong in the books is the bug this codebase keeps finding. None of it
 * runs in the default playtest - none of it may, unless the player uses it -
 * so this is the only place it is played.
 *
 * What this has to prove:
 *   1. THE OVERTIME TABLE: the Business Roundtable's 50-hour productivity,
 *      averaged week by week over each month, written out here from the
 *      report's figures; about 1.14, 1.02 and 0.94 times a month's work;
 *      time and a half making the wage bill 1.375.
 *   2. NO ORDER, THE RULE'S OWN SPLIT, TO THE BIT: a city whose player has
 *      set no order is not engaged, its plan is the rule's shares bit for
 *      bit, and a twin whose order was set and taken back plays the same
 *      month to the bit.
 *   3. PRIORITY: the city's share, as the rule gave it, handed out top
 *      down in the player's order; every other site's share to the bit, and
 *      the landlords' site in the twin without an order built the same.
 *   4. RUSH: the month's work is the share x 50/40 x the month's factor,
 *      months one, two and three, the third less than a normal month; the
 *      premium is 0.375 of the crews' wage bill at the builders' own rate a
 *      point, paid with their tax in it and paid out as wages the
 *      households receive; the count resets after a month off; the inbox
 *      says so before the third month.
 *   5. CANCEL: the refund is the city's contract left after the month's
 *      work - the work not done, the allowance not drawn and the tax on
 *      both - out of the builders' book; the shell keeps its buildings,
 *      progress, material owed and ground, and gets no crews; a restart is
 *      today's quote for the remainder.
 *   6. DEMOLITION: 5% of the template's points, at the builders' rate for
 *      its work, taxed; the buildings close as the month starts, not
 *      before; the material is sold to the builders by the 0.7.8 rule and
 *      the ground returns when done.
 *   7. BUY-OUT: market value - the building at its owner's value, the
 *      ground at the land market's - plus the business loss; the owner's
 *      cash rises by exactly that; its debts stay its own; nobody living
 *      in a home bought is deleted.
 *   8. MONEY IS CONSERVED through every one: the money audit every month,
 *      and every between-the-presses hand moves money between pools only.
 *   9. A SAVE AND A LOAD round-trip all of it, and a format-27 save loads
 *      with none of it.
 *  10. ONE WAIT, AND AN ORDER THAT LASTS AS LONG AS ITS SITES (after the
 *      docs pass): with the player's hand off, every screen's wait and the
 *      quote are the rule's to the bit; with an order set, the right panel's,
 *      a card's and the Needs-you line's wait is the page's - the order's
 *      own arithmetic - and a city order's quote is where it would land, a
 *      new site at the bottom; a city site placed under an order joins it at
 *      the bottom, and an order with none of its sites left is cleared; and
 *      "A demolition is done" reads each site's own sale.
 *
 * Every fixture causes its condition.
 */
public class ConstructionControlCheck {

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
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException("no template named " + name);
    }

    static final String RE = Sectors.REAL_ESTATE;

    /** The money in the pools the audit reads, all of them. */
    static double pooled(Game g) {
        double sum = 0;
        for (double p : MoneyAudit.pools(g)) sum += p;
        return sum;
    }

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

    /** A between-the-presses hand, and the pools before and after it. */
    static double[] handMoves(Game g, Runnable hand) {
        double before = pooled(g);
        hand.run();
        return new double[] { before, pooled(g) };
    }

    /**
     * A city with work on site: works standing, a population, depots,
     * every sector held so no planner orders or scraps anything in the
     * measurement, and on site a University, two Middle Schools and three
     * Gravel Roads of the city's and forty Houses of the landlords'.
     */
    static Game city(Path root, String name) {
        GameFiles files = new GameFiles(root.resolve(name), root.resolve(name + "-no-legacy"));
        Game g = new Game(files);
        quietly(() -> {
            g.run();
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 50_000_000L);
            // A harness names what its question is not about: no sector builds
            // or scraps anything here but by this fixture's hand.
            for (Sector s : g.getSectors().all()) g.getBusinessInvestment().holdSector(s.key());
            for (String[] w : new String[][] {
                    { "Industrial Bakery", "3" }, { "Coal Power Plant", "1" }, { "Water Treatment Plant", "1" },
                    { "Commercial Bank", "1" }, { "Convenience Store", "6" }, { "House", "400" },
                    { "Elementary School", "3" }, { "Walk-in Clinic", "2" }, { "Construction Depot", "4" },
                    { "Paved Road", "4" } }) {
                g.buildStack(template(g, w[0]), Integer.parseInt(w[1]), true);
            }
            g.simulateMonths(36);
            g.setCashForTest(20_000_000);
            g.buildStack(template(g, "University"), 1, false);
            g.buildStack(template(g, "Middle School"), 2, false);
            g.buildStack(template(g, "Gravel Road"), 3, false);
            g.getEconomyManager().setSectorCash(RE, g.getEconomyManager().getSectorCash(RE) + 200_000);
            g.buildFor(g.getSectorInvestor(RE), template(g, "House"), 40);
        });
        return g;
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        Path root = Files.createTempDirectory("constructioncontrolcheck");

        overtimeTable();
        noOrder(root);
        priority(root);
        rush(root);
        cancel(root);
        demolition(root);
        buyOut(root);
        conserved();
        saveAndLoad(root);
        oneWaitAndTheOrder(root);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ============================ 1. THE OVERTIME TABLE ============================ */

    static void overtimeTable() {
        out.println("--- 1. the overtime table: the report's figures, week by week, and time and a half ---");
        double w = 52.0 / 12.0;
        // Business Roundtable C-2, Figure 4, the 50-hour week: 0.926 (0-2),
        // 0.90 (2-4), 0.87 (4-6), 0.80 (6-8), 0.752 (8-10), 0.750 beyond.
        double m1 = (2 * .926 + 2 * .90 + (w - 4) * .87) / w;
        double m2 = ((6 - w) * .87 + 2 * .80 + (2 * w - 8) * .752) / w;
        double m3 = ((10 - 2 * w) * .752 + (3 * w - 10) * .750) / w;
        close("month 1's factor: weeks 0 to 4.33 of the table, each week weighted",
                ConstructionControl.overtimeProductivity(1), m1, 1e-12);
        close("month 2's: weeks 4.33 to 8.67", ConstructionControl.overtimeProductivity(2), m2, 1e-12);
        close("month 3's: weeks 8.67 to 13", ConstructionControl.overtimeProductivity(3), m3, 1e-12);
        close("month 4's and after: the last step, 0.750", ConstructionControl.overtimeProductivity(4), .750, 1e-12);
        close("...and month 12's", ConstructionControl.overtimeProductivity(12), .750, 1e-12);
        for (int m = 1; m <= 4; m++) {
            out.printf("      month %d on overtime: productivity %.4f, a month's work x %.4f%n", m,
                    ConstructionControl.overtimeProductivity(m), ConstructionControl.overtimeOutput(m));
        }
        close("a month's work on overtime is the factor x 50/40", ConstructionControl.overtimeOutput(1),
                m1 * ConstructionControl.OVERTIME_HOURS / ConstructionControl.STANDARD_HOURS, 1e-12);
        assertTrue("month 1 does about 1.14 times a normal month's work",
                Math.abs(ConstructionControl.overtimeOutput(1) - 1.14) < .005);
        assertTrue("month 2 about 1.02 times (within 0.01 of the brief's estimate, 1.03)", Math.abs(ConstructionControl.overtimeOutput(2) - 1.03) < .01);
        assertTrue("month 3 LESS than a normal month, about 0.94",
                ConstructionControl.overtimeOutput(3) < 1 && Math.abs(ConstructionControl.overtimeOutput(3) - .94) < .005);
        close("the wage bill on overtime is (40 + 10 x 1.5) / 40", ConstructionControl.OVERTIME_WAGE_BILL,
                (40 + 10 * 1.5) / 40, 0);
        close("...1.375, so the premium is 0.375 of the bill", ConstructionControl.premiumShare(), .375, 1e-15);
        close("demolition is 5% of the work: $14,855 over $289,415 is 5.1%, taken at 5%",
                ConstructionControl.DEMOLITION_SHARE, .05, 0);
    }

    /* ============================ 2. NO ORDER ============================ */

    static void noOrder(Path root) {
        out.println("\n--- 2. no order: the rule's own split, to the bit ---");
        Game a = city(root, "noorder-a");
        Game b = city(root, "noorder-b");
        BuildingManager ma = a.getBuildingManager(), mb = b.getBuildingManager();
        assertTrue("fixture: the city has sites of its own and the landlords one",
                ma.citySiteKeys().size() >= 3 && ma.getStack(template(a, "House")).getUnderConstruction() > 0
                        && !ma.getStack(template(a, "House")).isCitysOwn());
        assertTrue("fixture: the twins stand alike, to the bit", a.getCash() == b.getCash()
                && ma.getRemainingConstructionPoints() == mb.getRemainingConstructionPoints());
        assertTrue("a city whose player has done nothing is not engaged", !ma.getControl().engaged());
        double output = a.getBuildingOutput();
        BuildingManager.Plan plan = ma.plan(output);
        double[] rule = ma.siteShares(output, null, 0);
        boolean same = plan.share.length == rule.length;
        for (int i = 0; same && i < rule.length; i++) {
            same = Double.doubleToLongBits(plan.share[i]) == Double.doubleToLongBits(rule[i])
                    && Double.doubleToLongBits(plan.work[i]) == Double.doubleToLongBits(rule[i]);
        }
        assertTrue("its plan is the rule's shares, every slot to the bit", same);
        // The twin: an order set, then taken back before the month.
        mb.getControl().setOrder(mb.citySiteKeys());
        b.resetSiteOrder();
        assertTrue("an order set and taken back leaves the twin not engaged", !mb.getControl().engaged());
        month(a);
        month(b);
        boolean built = true;
        for (BuildingsStacks s : ma.getStacksUnderConstruction()) {
            BuildingsStacks t = mb.getStack(template(b, s.getName()));
            built &= t != null && Double.doubleToLongBits(s.getConstructionProgress())
                    == Double.doubleToLongBits(t.getConstructionProgress())
                    && s.getUnderConstruction() == t.getUnderConstruction();
        }
        assertTrue("...and plays the month as the untouched city does: every site, to the bit",
                built && !ma.getStacksUnderConstruction().isEmpty());
        bits("...and its treasury", b.getCash(), a.getCash());
        assertTrue("the month's advance applied no plan of the hand's", ma.getLastPlan() == null);
    }

    /* ============================ 3. PRIORITY ============================ */

    static void priority(Path root) {
        out.println("\n--- 3. priority: the city's share, top down in the player's order; nobody else's moves ---");
        Game a = city(root, "prio-a");
        Game b = city(root, "prio-b");
        BuildingManager ma = a.getBuildingManager(), mb = b.getBuildingManager();
        List<String> order = mb.cityOrder();
        java.util.Collections.reverse(order);
        // The player's hand: the last site first, by the arrows.
        double[] moved = handMoves(b, () -> mb.getControl().setOrder(order));
        close("setting an order moves no money", moved[1], moved[0], 0);
        assertTrue("...and engages the hand", mb.getControl().engaged());
        String first = order.get(0);
        month(a);
        month(b);
        BuildingManager.Plan p = mb.getLastPlan();
        assertTrue("fixture: the month applied the city's order", p != null);
        java.util.Set<String> city = new java.util.HashSet<>(order);
        boolean others = true;
        double ruleCity = 0, shareCity = 0;
        for (int i = 0; i < p.keys.size(); i++) {
            String k = p.keys.get(i);
            if (city.contains(k)) { ruleCity += p.rule[i]; shareCity += p.share[i]; continue; }
            others &= Double.doubleToLongBits(p.share[i]) == Double.doubleToLongBits(p.rule[i]);
        }
        assertTrue("every site not the city's keeps the rule's share, to the bit", others);
        close("the city's sites together keep the share the rule gave them", shareCity, ruleCity, 1e-12);
        double pool = ruleCity;
        boolean topDown = true;
        for (String k : order) {
            int i = p.keys.indexOf(k);
            double give = Math.max(0, Math.min(p.owed[i], pool));
            topDown &= Math.abs(p.share[i] - give) <= 1e-9 * Math.max(1, give);
            pool -= give;
        }
        assertTrue("...handed out top down: each what it owes, at most, of what is left", topDown);
        int f = p.keys.indexOf(first);
        assertTrue("fixture: the first in the order owes more than the rule gave it",
                p.owed[f] > p.rule[f]);
        assertTrue("...so it took more than the rule's share", p.share[f] > p.rule[f]);
        BuildingsStacks houseA = ma.getStack(template(a, "House")), houseB = mb.getStack(template(b, "House"));
        bits("the landlords' site built the month the twin without an order built",
                houseB.getConstructionProgress() + houseB.getQuantity() * 1e9,
                houseA.getConstructionProgress() + houseA.getQuantity() * 1e9);
        close("the builders built the same points in all", mb.getPointsBuilt(), ma.getPointsBuilt(), 1e-9);
    }

    /* ============================ 4. RUSH ============================ */

    static void rush(Path root) {
        out.println("\n--- 4. rush: the table's work for 1.375 times the wages, paid out as wages ---");
        Game g = city(root, "rush");
        BuildingManager bm = g.getBuildingManager();
        String key = ConstructionControl.keyOf(template(g, "University"));
        assertTrue("fixture: the University is the city's own site", bm.isCitySite(key));
        double[] moved = handMoves(g, () -> g.rushSite(key, true));
        close("a rush moves no money when it is set", moved[1], moved[0], 0);
        assertTrue("a landlords' site cannot be rushed",
                !g.rushSite(ConstructionControl.keyOf(template(g, "House")), true));
        BuildingsTemplate depot = template(g, "Construction Depot");
        double rB = Math.max(0, Math.min(TaxPolicy.MAX_INCOME_TAX,
                g.getEconomyManager().getTaxPolicy().effectiveSalesRate(g.getSectors().construction())));
        for (int m = 1; m <= 3; m++) {
            // The builders' wage bill a point, as the advance will read it: a
            // depot's posts at the wages struck last month, over its points.
            double[] wages = g.getEconomyManager().getWageRates();
            double bill = 0;
            for (JobType job : JobType.values()) bill += depot.getJobs(job) * wages[job.ordinal()];
            double perPoint = bill / depot.makes(Good.BUILDING_WORK);
            BuildingsStacks uni = bm.getStack(template(g, "University"));
            double progressBefore = uni.getConstructionProgress();
            month(g);
            ConstructionControl.Overtime o = null;
            for (ConstructionControl.Overtime x : g.getControlThisMonth().overtime) if (x.key().equals(key)) o = x;
            assertTrue("month " + m + ": the site was worked on overtime, its month " + m, o != null && o.month() == m);
            if (o == null) continue;
            close("  its work is its share x 50/40 x the month's factor", o.work(),
                    Math.min(bm.getLastPlan().owedOf(key), o.share() * ConstructionControl.overtimeOutput(m)), 1e-12);
            close("  ...and is what the site was built by", uni.getLastApplied(), o.work(), 1e-12);
            close("  ...its progress moved by it", uni.getConstructionProgress() - progressBefore, o.work(), 1e-9);
            close("  the wage bill a point is a depot's posts at today's wages over its points", o.perPoint(),
                    perPoint, 1e-12);
            close("  the premium is 0.375 of its crews' bill", o.premium(),
                    ConstructionControl.premiumShare() * o.share() * perPoint, 1e-12);
            close("  the treasury paid it with the builders' tax in it", g.getOvertimePaidThisMonth(),
                    o.premium() / (1 - rB), 1e-9);
            close("  ...which the builders earned", g.getSectors().construction().getOvertimeThisMonth(),
                    g.getOvertimePaidThisMonth(), 1e-12);
            double[] ot = g.getSectors().construction().getOvertimeWages();
            double sum = 0;
            if (ot != null) for (double v : ot) sum += v;
            close("  the builders pay the premium out as wages", sum, o.premium(), 1e-9);
            PopulationManager pm = g.getPopulationManager();
            double withIt = pm.getTotalWage();
            final ham.citybuildersim.sectors.Construction builders = g.getSectors().construction();
            pm.setOvertimeWages(null);
            double without = pm.getTotalWage();
            pm.setOvertimeWages(builders::getOvertimeWages);
            close("  ...which the households are paid, on top of the posts' wages", withIt - without, o.premium(), 1e-6);
            if (m == 3) {
                assertTrue("  month 3 does less than a normal month's work on the share", o.work() < o.share());
            }
            if (m == 2) {
                Notice n = g.getInbox().live("overtime");
                assertTrue("  after month 2 the inbox says the third month costs more than it gains", n != null);
            }
        }
        // Stopped: a month off resets the count.
        g.rushSite(key, false);
        month(g);
        assertTrue("a month off overtime: the site was not worked on it",
                g.getControlThisMonth().overtime.isEmpty() && g.getSectors().construction().getOvertimeWages() == null);
        assertTrue("...and its count is forgotten", bm.getControl().rushOf(key) == null);
        assertTrue("...and the inbox's notice is settled", g.getInbox().live("overtime") == null);
        g.rushSite(key, true);
        month(g);
        ConstructionControl.Overtime again = g.getControlThisMonth().overtime.isEmpty() ? null
                : g.getControlThisMonth().overtime.get(0);
        assertTrue("rushed again, it starts at month 1", again != null && again.month() == 1);
    }

    /* ============================ 5. CANCEL ============================ */

    static void cancel(Path root) {
        out.println("\n--- 5. cancel: the contract left comes back; the shell keeps its work and its ground ---");
        Game g = city(root, "cancel");
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate t = template(g, "Middle School");
        String key = ConstructionControl.keyOf(t);
        BuildingsStacks s = bm.getStack(t);
        month(g);
        assertTrue("fixture: the city's Middle Schools are part built", s.getUnderConstruction() > 0
                && s.getConstructionProgress() > 0 && s.isCitysOwn());
        assertTrue("a landlords' site cannot be cancelled",
                !g.cancelSite(ConstructionControl.keyOf(template(g, "House")), true));
        double[] moved = handMoves(g, () -> g.cancelSite(key, true));
        close("a cancel moves no money until the month's end", moved[1], moved[0], 0);
        BuildingsStacks.Contract c = null;
        for (BuildingsStacks.Contract x : s.getContracts()) if ("City".equals(x.payer)) c = x;
        double v0 = c.getValue(), a0 = c.getAllowance();
        int onSite = s.getUnderConstruction();
        double owed0 = onSite * (double) t.getConstructionPoints() - s.getConstructionProgress();
        double progress0 = s.getConstructionProgress(), materials0 = s.getMaterialsOwed();
        double footprint0 = bm.getTotalLandFootprint(), allocated0 = g.getLandManager().getAllocatedSqFt();
        ham.citybuildersim.sectors.Construction builders = g.getSectors().construction();
        double unearned0 = builders.getUnearnedRevenue();
        month(g);
        List<ConstructionControl.Refund> refunds = g.getControlThisMonth().refunds;
        assertTrue("the month's end stopped it: one refund", refunds.size() == 1);
        ConstructionControl.Refund r = refunds.get(0);
        double applied = s.getLastApplied();
        assertTrue("fixture: the month's work did not finish it", applied < owed0);
        close("the refund is the city's contract left after the month's work",
                r.value(), v0 * (1 - applied / owed0), 1e-9);
        close("...the allowance not drawn in it", r.allowance(), a0 * (1 - applied / owed0), 1e-9);
        double rB = Math.max(0, Math.min(TaxPolicy.MAX_INCOME_TAX,
                g.getEconomyManager().getTaxPolicy().effectiveSalesRate(builders)));
        out.printf("      the refund %s: work not done %s, material not drawn %s, the sales tax on both %s%n",
                fmt(r.value()), fmt((r.value() - r.allowance()) * (1 - rB)), fmt(r.allowance() * (1 - rB)),
                fmt(r.value() * rB));
        double work = builders.getRecognisedThisMonth() - builders.getEscalationThisMonth()
                - builders.getOvertimeThisMonth();
        close("the builders' book fell by the month's work and by the refund, no more",
                builders.getUnearnedRevenue(), unearned0 - work - r.value(), 1e-9);
        int finished = s.getLastFinished();
        ConstructionControl.Shell shell = bm.getControl().shellOf(t.getId());
        assertTrue("a shell stands", shell != null);
        assertTrue("...with the buildings that were on site", shell.buildings == onSite - finished);
        close("...the work in them", shell.progress, progress0 + applied - finished * (double) t.getConstructionPoints(), 1e-9);
        close("...and the material they had still to draw", shell.materialsOwed, materials0 - s.getMaterialsDue(), 1e-9);
        assertTrue("the site has nothing on it, and no contract of the city's",
                s.getUnderConstruction() == 0 && s.getContracts().isEmpty());
        close("the shell holds its ground", bm.getTotalLandFootprint(), footprint0, 0);
        close("...the land office still has it allocated", g.getLandManager().getAllocatedSqFt(), allocated0, 0);
        double held = shell.progress;
        month(g);
        close("a month on, a shell gets no crews: its work is where it stopped", shell.progress, held, 0);
        // Restart: today's quote for the remainder.
        Game.BuildQuote q = g.quoteRestart(shell);
        double pointsLeft = shell.buildings * (double) t.getConstructionPoints() - shell.progress;
        close("a restart's work is the remainder's non-material cost at today's wages",
                q.sticker, bm.nonMaterialCost(t) * pointsLeft / t.getConstructionPoints(), 1e-9);
        close("...its material what the shell has still to draw", q.materialsNeeded, shell.materialsOwed, 0);
        close("...priced by the builders' rule, the tax in it", q.total,
                q.sticker / (1 - rB) + q.allowance, 1e-9);
        int n = shell.buildings;
        double p = shell.progress, cash0 = g.getCash(), book0 = builders.getUnearnedRevenue();
        double[] restart = handMoves(g, () -> g.restartShell(t.getId()));
        close("a restart moves the money from the treasury to the builders' book, no more",
                restart[1], restart[0], 1e-9);
        close("...the treasury paid the quote", cash0 - g.getCash(), q.total, 1e-9);
        close("...into the builders' book", builders.getUnearnedRevenue() - book0, q.total, 1e-9);
        assertTrue("its buildings are back on site, the shell gone",
                s.getUnderConstruction() == n && bm.getControl().shellOf(t.getId()) == null);
        close("...with the work they held", s.getConstructionProgress(), p, 0);
    }

    /* ============================ 6. DEMOLITION ============================ */

    static void demolition(Path root) {
        out.println("\n--- 6. demolition: 5% of the work; closed as the next month starts; material to the builders, ground back ---");
        Game g = city(root, "demolish");
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate t = template(g, "Elementary School");
        int standing = bm.getQuantity(t.getId());
        assertTrue("fixture: the city has three schools standing", standing == 3);
        assertTrue("the city's own to demolish; a landlords' House is not", g.isCitysToDemolish(t)
                && !g.isCitysToDemolish(template(g, "House")) && !g.isCitysToDemolish(template(g, "Commercial Bank")));
        Game.DemolitionQuote q = g.quoteDemolition(t, 2);
        close("its work is 5% of the template's points", q.points(),
                ConstructionControl.DEMOLITION_SHARE * t.getConstructionPoints() * 2, 0);
        close("its price is that share of the building's work at the builders' rate today",
                q.price().sticker, ConstructionControl.DEMOLITION_SHARE * bm.nonMaterialCost(t) * 2, 1e-12);
        double rB = Math.max(0, Math.min(TaxPolicy.MAX_INCOME_TAX,
                g.getEconomyManager().getTaxPolicy().effectiveSalesRate(g.getSectors().construction())));
        close("...no material, and the tax in it", q.price().total, q.price().sticker / (1 - rB), 1e-12);
        double places0 = bm.getBuiltEducationPlaces()[t.getTeaches().ordinal()];
        long posts0 = 0;
        for (JobType job : JobType.values()) posts0 += bm.getTotalJobsAtEveryPost(job);
        double footprint0 = bm.getTotalLandFootprint(), allocated0 = g.getLandManager().getAllocatedSqFt();
        double cash0 = g.getCash();
        double[] moved = handMoves(g, () -> g.demolish(t, 2));
        close("the order moves money from the treasury to the builders' book only", moved[1], moved[0], 1e-9);
        close("...the treasury paid the quote", cash0 - g.getCash(), q.price().total, 1e-9);
        assertTrue("until the month starts the two still stand, and no third can be ordered twice",
                bm.getQuantity(t.getId()) == standing && g.demolishable(t) == standing - 2
                        && !g.demolish(t, 2));
        close("...and hold their own ground, once", bm.getTotalLandFootprint(), footprint0, 0);
        ConstructionControl.Demolition site = bm.getControl().demolitions().get(0);
        assertTrue("a demolition is on site, the city's", site != null && "City".equals(site.from)
                && bm.isCitySite(site.key()));
        int months = 0;
        ConstructionControl.Completed done = null;
        month(g);
        months++;
        for (ConstructionControl.Completed c : g.getControlThisMonth().completed) done = c;
        assertTrue("as the month after the order starts, they close: one school stands",
                bm.getQuantity(t.getId()) == standing - 2);
        close("...its school places gone with them", bm.getBuiltEducationPlaces()[t.getTeaches().ordinal()],
                places0 - 2 * t.getCapacity(), 0);
        long posts1 = 0; int staff = 0;
        for (JobType job : JobType.values()) { posts1 += bm.getTotalJobsAtEveryPost(job); staff += 2 * t.getJobs(job); }
        assertTrue("...and their posts", posts1 == posts0 - staff);
        if (done == null) close("the ground is held until the demolition is done", bm.getTotalLandFootprint(), footprint0, 0);
        while (done == null && months++ < 60) {
            month(g);
            for (ConstructionControl.Completed c : g.getControlThisMonth().completed) done = c;
        }
        assertTrue("it finished, in " + months + " month(s)", done != null);
        if (done == null) return;
        Game.Salvage sold = null;
        for (Game.Salvage x : g.getSalvageThisMonth()) if ("City".equals(x.seller())) sold = x;
        assertTrue("its material was sold", sold != null);
        close("...all the material the buildings held", sold.units(), 2.0 * t.getConstructionMaterials(), 0);
        assertTrue("...as much as the builders' cash covered", sold.unitsBought() <= sold.units() + 1e-9);
        close("...paid for at the price it was sold at", sold.paid(), sold.unitsBought() * sold.price(), 1e-9);
        close("...into the treasury, on the journal", g.getDemolitionSalvageThisMonth(), sold.paid(), 0);
        boolean journalled = false;
        for (TreasuryJournal.Entry e : g.getTreasuryJournalBook().lastMonth()) {
            journalled |= e.label().startsWith("Sold a demolition's material") && Math.abs(e.amount() - sold.paid()) < 1e-9;
        }
        assertTrue("...where the treasury's bridge names it", journalled);
        close("its ground came back", footprint0 - bm.getTotalLandFootprint(), 2 * t.getLandSqFt(), 1e-9);
        close("...free on the land office's books", allocated0 - g.getLandManager().getAllocatedSqFt(),
                2 * t.getLandSqFt(), 1e-9);
        assertTrue("the inbox says it is done", g.getInbox().live("demolished") != null);

        // The 0.7.8 rule itself, between two presses where the price and the
        // builders' till stand still: the last school's demolition, finished
        // by hand.
        quietly(() -> g.demolish(t, 1));
        month(g);
        ConstructionControl.Demolition last = bm.getControl().demolitions().get(0);
        double footprintBefore = bm.getTotalLandFootprint();
        bm.getControl().removeDemolition(last);
        ConstructionControl.Events ev = new ConstructionControl.Events();
        ev.completed.add(new ConstructionControl.Completed(last));
        double price = g.getMarkets().get(Good.MATERIALS).getLocalPrice();
        double till = g.getSectors().construction().getCash(), cashBefore = g.getCash();
        double salvage0 = g.getSectors().construction().getSalvage();
        double[] sale = handMoves(g, () -> quietly(() -> g.settleConstructionControl(ev)));
        double units = t.getConstructionMaterials();
        double bought = Math.min(units, Math.max(0, till) / price);
        close("the rule: the builders buy the material at the materials market's price", 
                g.getSalvageThisMonth().get(g.getSalvageThisMonth().size() - 1).price(), price, 0);
        close("...as much of it as their cash covers", g.getSectors().construction().getSalvage() - salvage0, bought, 1e-9);
        close("...and the treasury has what they paid", g.getCash() - cashBefore, bought * price, 1e-9);
        close("...out of their till", till - g.getSectors().construction().getCash(), bought * price, 1e-9);
        close("...money between two pools, no more", sale[1], sale[0], 1e-9);
        close("...and the ground is free", footprintBefore - bm.getTotalLandFootprint(), t.getLandSqFt(), 1e-9);
    }

    /* ============================ 7. BUY-OUT ============================ */

    static void buyOut(Path root) {
        out.println("\n--- 7. buy-out: market value and the business loss, to the owner; its debts its own ---");
        Game g = city(root, "buyout");
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate t = template(g, "House");
        int k = 20;
        assertTrue("a landlords' House is bought out, not demolished outright", g.isBuyOutable(t)
                && !g.isCitysToDemolish(t));
        Game.BuyOutQuote q = g.quoteBuyOut(t, k);
        assertTrue("fixture: there is a quote", q != null);
        if (q == null) return;
        double each = t.getCashCost() + t.getConstructionMaterials() * bm.getConstructionMaterialPrice();
        close("the building at its owner's value: its cash cost and its material at today's price",
                q.buildingValue(), each * k, 1e-12);
        close("its ground at the land market's price", q.ground(), g.getLandManager().priceFor(t.getLandSqFt() * k), 0);
        Sector owner = g.getSectors().byKey(RE);
        double share = q.buildingValue() / bm.getBookValueBySector(RE);
        close("its share of its owner's buildings, at that value", q.profitShare(), share, 1e-12);
        close("the business loss: that share of the operating income, for a replacement's months",
                q.businessLoss(), Math.max(0, share * owner.statement().operatingIncome) * g.quoteMonths(t, k), 1e-9);
        assertTrue("...and nothing for a loss", q.businessLoss() >= 0
                && (owner.statement().operatingIncome > 0 || q.businessLoss() == 0));
        out.printf("      %d Houses: building %s, ground %s, business loss %s (%.1f months of %.4f of %s)%n", k,
                fmt(q.buildingValue()), fmt(q.ground()), fmt(q.businessLoss()), q.replacementMonths(), share,
                fmt(owner.statement().operatingIncome));
        double till0 = owner.getCash(), cash0 = g.getCash();
        double debt0 = g.getEconomyManager().getBusinessDebtManager().getPrincipal(RE);
        long homes0 = bm.getTotalHomes(); int houses0 = bm.getQuantity(t.getId());
        double households0 = g.getFamilies().totalHouseholds(), people0 = g.getCohorts().total();
        out.printf("      %d homes stand, %.1f needed, %.1f households%n", homes0, g.getFamilies().homesNeeded(),
                households0);
        assertTrue("fixture: every home is taken - the households outnumber the homes", households0 > homes0);
        double[] moved = handMoves(g, () -> g.buyOutAndDemolish(t, k));
        close("the buy-out moves money between the pools only", moved[1], moved[0], 1e-9);
        close("the owner's cash rose by exactly the compensation", owner.getCash() - till0, q.compensation(), 1e-9);
        close("the treasury paid it and the demolition", cash0 - g.getCash(), q.total(), 1e-9);
        close("the owner still owes what it owed", g.getEconomyManager().getBusinessDebtManager().getPrincipal(RE),
                debt0, 0);
        ConstructionControl.Expropriation e = bm.getControl().expropriations().isEmpty() ? null
                : bm.getControl().expropriations().get(0);
        assertTrue("the purchase is on record", e != null && e.buildings == k && RE.equals(e.sector)
                && Math.abs(e.total() - q.compensation()) <= 1e-9 * q.compensation());
        assertTrue("its demolition is on site", !bm.getControl().demolitions().isEmpty()
                && RE.equals(bm.getControl().demolitions().get(0).from));
        month(g);
        assertTrue("the month starts with the Houses gone: what stands is what stood, less them, and the month's new ones",
                bm.getQuantity(t.getId()) - bm.getStack(t).getLastFinished() == houses0 - k);
        assertTrue("...and the households who lived in them are not deleted: the families did not fall with the homes",
                g.getFamilies().totalHouseholds() >= households0);
        assertTrue("...nor the people (fixture: a growing city)", g.getCohorts().total() >= people0);
    }

    /* ============================ 8. CONSERVED ============================ */

    static void conserved() {
        out.println("\n--- 8. money is conserved through every one ---");
        out.printf("      %d months audited in sections 2 to 7%n", monthsAudited);
        assertTrue("the money audit's worst month is within 0.01% of what moved", worstResidual < 1e-4);
        assertTrue("...and nothing moved a pool after it", Math.abs(worstDrift) < 1e-6);
    }

    /* ============================ 9. SAVE AND LOAD ============================ */

    static void saveAndLoad(Path root) throws Exception {
        out.println("\n--- 9. a save and a load round-trip all of it; a format-27 save loads with none ---");
        Game g = city(root, "saved");
        BuildingManager bm = g.getBuildingManager();
        quietly(() -> {
            g.rushSite(ConstructionControl.keyOf(template(g, "University")), true);
            g.cancelSite(ConstructionControl.keyOf(template(g, "Gravel Road")), true);
        });
        month(g);
        List<String> order = bm.cityOrder();
        java.util.Collections.reverse(order);
        bm.getControl().setOrder(order);
        quietly(() -> {
            g.demolish(template(g, "Walk-in Clinic"), 1);
            g.buyOutAndDemolish(template(g, "Convenience Store"), 1);
            g.cancelSite(ConstructionControl.keyOf(template(g, "Middle School")), true);
        });
        ConstructionControl c = bm.getControl();
        assertTrue("fixture: an order, a rush a month in, a cancel waiting, a shell, two demolitions, a buy-out",
                c.isPrioritySet() && c.monthsOnOvertime(ConstructionControl.keyOf(template(g, "University"))) == 1
                        && !c.shells().isEmpty() && c.demolitions().size() == 2 && c.expropriations().size() == 1
                        && c.isCancelling(ConstructionControl.keyOf(template(g, "Middle School")))
                        && g.getSectors().construction().getOvertimeWages() != null);
        final boolean[] saved = { false };
        quietly(() -> saved[0] = g.saveGame(1, "hand city").ok);
        assertTrue("saved", saved[0]);
        GameFiles files = new GameFiles(root.resolve("saved"), root.resolve("saved-no-legacy"));
        Game back = new Game(files);
        quietly(() -> back.loadGameSave(1));
        // ...held as the fixture held it: a harness's hold is not saved.
        for (Sector s : back.getSectors().all()) back.getBusinessInvestment().holdSector(s.key());
        BuildingManager bb = back.getBuildingManager();
        Gson gson = new GsonBuilder().create();
        assertTrue("the state, every field of it, reads back as it was written",
                gson.toJson(bb.getControl().toState()).equals(gson.toJson(c.toState())));
        double[] w0 = g.getSectors().construction().getOvertimeWages(), w1 = back.getSectors().construction().getOvertimeWages();
        assertTrue("the overtime wages the next statement charges, to the bit", java.util.Arrays.equals(w0, w1));
        bits("...and the households' wage bill that pays them", back.getPopulationManager().getTotalWage(),
                g.getPopulationManager().getTotalWage());
        bits("the ground the shells and the demolitions hold", bb.getTotalLandFootprint(), bm.getTotalLandFootprint());
        bits("...as the land office allocates it", back.getLandManager().getAllocatedSqFt(),
                g.getLandManager().getAllocatedSqFt());
        month(g);
        month(back);
        bits("a month on, the two treasuries agree", back.getCash(), g.getCash());
        boolean sites = !bm.getStacksUnderConstruction().isEmpty();
        for (BuildingsStacks s : bm.getStacksUnderConstruction()) {
            BuildingsStacks t = bb.getStack(template(back, s.getName()));
            sites &= t != null && Double.doubleToLongBits(t.getConstructionProgress())
                    == Double.doubleToLongBits(s.getConstructionProgress());
        }
        assertTrue("...and every site's work, to the bit", sites);
        assertTrue("...and the hand's state", gson.toJson(bb.getControl().toState()).equals(gson.toJson(c.toState())));

        Path file = files.saveFile(1);
        JsonObject json = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
        // The format the hand's key came in, 28, or a later one that carries it too (0.7.23 moved it to 29
        // for the decision log): pinned at 28 it went red over a number this harness does not own.
        assertTrue("the save says format " + GameVersion.SAVE_FORMAT, json.get("saveFormat").getAsInt() == GameVersion.SAVE_FORMAT
                && GameVersion.SAVE_FORMAT >= 28 && json.has("constructionControl"));
        json.remove("constructionControl");
        json.addProperty("saveFormat", 27);
        Files.writeString(files.saveFile(2), new GsonBuilder().setPrettyPrinting().create().toJson(json),
                StandardCharsets.UTF_8);
        Game old = new Game(files);
        quietly(() -> old.loadGameSave(2));
        ConstructionControl oc = old.getBuildingManager().getControl();
        assertTrue("a format-27 save loads", old.getLoadFailure() == null || old.getLoadFailure().isEmpty());
        assertTrue("...with none of it: no order, no rush, no cancel, no shell, no demolition, no buy-out",
                !oc.engaged() && !oc.isPrioritySet() && oc.rushes().isEmpty() && oc.shells().isEmpty()
                        && oc.demolitions().isEmpty() && oc.expropriations().isEmpty());
    }

    /* ====================== 10. ONE WAIT, AND THE ORDER'S LIFE ====================== */

    static void oneWaitAndTheOrder(Path root) {
        out.println("\n--- 10. one wait for a site everywhere; an order lasts as long as its sites ---");
        Game g = city(root, "onewait");
        BuildingManager bm = g.getBuildingManager();
        double output = g.getBuildingOutputAtEveryPost();

        // With the player's hand off the queue: the rule's waits, to the bit.
        boolean plain = true;
        for (BuildingsStacks s : bm.getStacksUnderConstruction()) {
            plain &= Double.doubleToLongBits(g.onSiteMonths(s.getBuilding()))
                    == Double.doubleToLongBits(bm.waitOnSite(s.getBuilding(), output));
        }
        assertTrue("no order, no rush: every site's wait is waitOnSite()'s, to the bit", plain);
        BuildingsTemplate high = template(g, "High School"), middle = template(g, "Middle School");
        bits("...and a city order's quote is quoteMonths()'s", g.quoteCityMonths(high, 1), g.quoteMonths(high, 1));
        bits("...and the build quote's months with it", g.quoteBuild(high, 1).months, g.quoteMonths(high, 1));

        // An order set: the city's sites in reverse.
        List<String> order = bm.cityOrder();
        java.util.Collections.reverse(order);
        bm.getControl().setOrder(order);
        double all = 0, cityWeight = 0;
        java.util.Map<String, double[]> by = new java.util.LinkedHashMap<>();
        for (BuildingsStacks s : bm.getStacksUnderConstruction()) {
            double w = BuildingManager.weightOf(s.getBuilding(), s.getUnderConstruction());
            double o = s.getUnderConstruction() * (double) s.getBuilding().getConstructionPoints() - s.getConstructionProgress();
            by.put(ConstructionControl.keyOf(s.getBuilding()), new double[] { w, o });
            all += w;
            if (order.contains(ConstructionControl.keyOf(s.getBuilding()))) cityWeight += w;
        }
        double ahead = 0;
        boolean same = true, ordered = true;
        BuildingManager.Plan plan = bm.plan(g.getBuildingOutput());
        String starved = null;
        for (String key : order) {
            ahead += by.get(key)[1];
            double expected = ahead / (output * cityWeight / all);
            BuildingsStacks s = bm.stackOfKey(key);
            same &= Double.doubleToLongBits(g.onSiteMonths(s.getBuilding())) == Double.doubleToLongBits(g.siteMonths(key));
            ordered &= Math.abs(g.siteMonths(key) - expected) <= 1e-9 * expected;
            if (plan.shareOf(key) <= 0 && plan.ruleOf(key) > 0) starved = key;
        }
        assertTrue("with an order set, a card's, the panel's and the Needs-you line's wait is the page's", same);
        assertTrue("...which is everything ahead of it in the order and itself over the city's share", ordered);
        assertTrue("fixture: the order starves a city site the rule would have crewed", starved != null);
        if (starved != null) {
            BuildingsStacks s = bm.stackOfKey(starved);
            assertTrue("...and that site reads the order's wait, not the rule's",
                    Math.abs(g.onSiteMonths(s.getBuilding()) - bm.waitOnSite(s.getBuilding(), output)) > 1e-9
                            && g.onSiteMonths(s.getBuilding()) == g.siteMonths(starved));
        }
        double w = BuildingManager.weightOf(high, 1);
        double cityOwed = 0;
        for (String key : order) cityOwed += by.get(key)[1];
        close("a new city order's quote: a new site at the bottom of the order",
                g.quoteCityMonths(high, 1),
                (cityOwed + high.getConstructionPoints()) / (output * (cityWeight + w) / (all + w)), 1e-9);
        close("...and the build quote says so", g.quoteBuild(high, 1).months, g.quoteCityMonths(high, 1), 0);
        String mk = ConstructionControl.keyOf(middle);
        double[] m0 = by.get(mk);
        double wm = BuildingManager.weightOf(middle, bm.getStack(middle).getUnderConstruction() + 1);
        double aheadMiddle = 0;
        for (String key : order) {
            aheadMiddle += key.equals(mk) ? m0[1] + middle.getConstructionPoints() : by.get(key)[1];
            if (key.equals(mk)) break;
        }
        close("...one more of a city site's building joins that site where it stands in the order",
                g.quoteCityMonths(middle, 1),
                aheadMiddle / (output * (cityWeight - m0[0] + wm) / (all - m0[0] + wm)), 1e-9);

        // A city site placed under the order joins it at the bottom.
        BuildingsTemplate paved = template(g, "Paved Road");
        quietly(() -> g.buildStack(paved, 1, false));
        String pk = ConstructionControl.keyOf(paved);
        List<String> before = bm.cityOrder();
        assertTrue("a city site placed while an order is set is served at its bottom",
                before.get(before.size() - 1).equals(pk));
        month(g);
        List<String> saved = bm.getControl().savedOrder();
        assertTrue("...and the month's end writes it into the order, at the bottom",
                !saved.isEmpty() && saved.get(saved.size() - 1).equals(pk) && bm.getControl().isPrioritySet());
        // Every ordered site stopped: the order is cleared.
        for (String key : bm.cityOrder()) g.cancelSite(key, true);
        month(g);
        assertTrue("fixture: none of the order's sites is left on site", bm.citySiteKeys().isEmpty());
        assertTrue("an order with none of its sites left is cleared", !bm.getControl().isPrioritySet()
                && bm.getControl().savedOrder().isEmpty());
        quietly(() -> g.buildStack(high, 1, false));
        assertTrue("...so the next city site is the crews' rule's: the hand is off the queue",
                !bm.getControl().engaged());

        // "A demolition is done" reads each site's own sale: two demolitions
        // of one building and count, finished in one breath, the builders'
        // till covering one and a half of them.
        BuildingsTemplate school = template(g, "Elementary School");
        quietly(() -> { g.demolish(school, 1); g.demolish(school, 1); });
        month(g);
        List<ConstructionControl.Demolition> two = new java.util.ArrayList<>(bm.getControl().demolitions());
        assertTrue("fixture: two demolitions of one Elementary School each, closed", two.size() == 2
                && !two.get(0).closing && !two.get(1).closing);
        ConstructionControl.Events ev = new ConstructionControl.Events();
        for (ConstructionControl.Demolition d : two) {
            bm.getControl().removeDemolition(d);
            ev.completed.add(new ConstructionControl.Completed(d));
        }
        double price = g.getMarkets().get(Good.MATERIALS).getLocalPrice();
        double units = school.getConstructionMaterials();
        g.getEconomyManager().setSectorCash(Sectors.CONSTRUCTION, 1.5 * units * price);
        quietly(() -> g.settleConstructionControl(ev));
        g.getInbox().takeMonth(g);
        ConstructionControl.Completed first = ev.completed.get(0), second = ev.completed.get(1);
        close("the first sale is its own: all its material", first.unitsSold(), units, 1e-9);
        close("...the second its own: what the till had left", second.unitsSold(), .5 * units, 1e-9);
        Notice done = g.getInbox().live("demolished");
        List<String> lines = done == null ? java.util.List.of() : done.getBody();
        assertTrue("the notice says each one's sale, not the last one's twice", lines.size() == 2
                && lines.get(0).contains(String.format("%,.0f units", first.unitsSold()))
                && lines.get(1).contains(String.format("%,.0f units", second.unitsSold())));
    }

    static String fmt(double thousands) {
        return String.format("$%,.0f", thousands * 1000);
    }
}

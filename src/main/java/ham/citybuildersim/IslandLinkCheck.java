package ham.citybuildersim;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * The island link checked (AL1, 2026-11-09; docs/queue/AL1.md): the rule that says what the city should order to link an
 * island it owns ground on, the automatic builder that orders it once its orders lack ground, and the test player that
 * orders it by the same rule. Not part of the game.
 *
 * WHY. Jerus, 2026-10-31: "autobuild will build bridges and ports if sees that land is low and there is something unlinked
 * (basically, if link is allowed with bridge, then it builds a highway bridge or road bridge ... if not it build a link via
 * port)". Until AL1 the automatic builders never linked an island (ET5's call 5, ET6-C): ground the city owned across the
 * water stayed unlinked, owned and counted, however full home was. Every part of the rule is caused here on the Earth
 * fixture of LandmassCheck and BridgeCheck (Rotterdam, and Goeree-Overflakkee's square bought across the water): the
 * crossing is handed to the survey (BridgeCheck.seam(), because the strait the fixture holds is open water past 45 km and no
 * crossing), the ground low is cut to the condition, and each figure is asserted against the model's own constants.
 *
 * WHAT THIS HAS TO PROVE:
 *
 *   1. THE RULE, A CROSSING ALLOWED. With a shallow channel the survey can cross, BuildAdvice.linkFor() is a BRIDGE toward the
 *      island, one order: the Road Bridge with no highway standing and the Highway Bridge with one; its price is the quote of
 *      that template with the island picked, its ground the island's buildable ground and its price a square foot that over
 *      it; the card's pick of the far landmass is as the rule found it, both with none and with another set; a crossing is
 *      not remembered (each ask is a survey).
 *   2. THE RULE, DEEP SEA. Where the survey finds deep water, a PORT: the island's order is the cheapest known terminal that
 *      stands on the island and fits its room (against every port template's quote), and with no terminal on linked ground a
 *      second at home; with one standing at home the island's alone (a port shared where one serves); with one on the island
 *      and none at home, the home order alone.
 *   3. NO LINK, OR NOTHING TO LINK. An island owned with no shore and no crossing is NONE, in words naming both; so is one
 *      with shore where home owns none; a city whose ground is all at home and a generated world are null and read nothing;
 *      a survey that found no crossing stands NO_CROSSING_MONTHS and is made again after, a crossing is not remembered.
 *   4. THE AUTO-BUILDER, A CROSSING. With the city's linked free ground cut below what its first order needs, the pass sets
 *      groundShort(), orders one bridge for the island (a LinkStep, a bridge on site, a log entry, the spending counted), and
 *      the island is unlinked while it is on site; the next passes, the ground still low, order no second; a city with room
 *      sets groundShort() in none of its months and orders no link.
 *   5. THE AUTO-BUILDER, DEEP SEA. The same, ports: a terminal on site on the island and one at home; once both stand the
 *      island is linked and the next pass orders nothing.
 *   6. THE MONEY. With the debt limit at 0 and the cash at the reserve, the link is HELD, in the words of the limit: nothing
 *      ordered, no ground bought, no bond sold.
 *   7. THE BRIDGE STANDS AND LINKS. The bridge the auto-builder ordered, run to standing: the island is linked, its free
 *      ground counts as room, and the next pass with ground low orders nothing more for it.
 *   8. THE TEST PLAYER LINKS AS THE AUTO-BUILDER DOES. LongPlaytest.keepGroundAhead() with the used share over its line orders
 *      the same templates toward the same landmass the rule gives, on the crossing and on deep sea, and counts them; where the
 *      rule says NONE it counts the link held.
 *   9. COUNTED IN A PLAYED RUN. Rotterdam with the island square, its real strait, 240 months with the auto-builder on: how
 *      many months were short of ground, how many links of each kind, the month the island linked; at most one link order
 *      for an island while one is under way, every order for an island unlinked when it was ordered. A generated world with
 *      its ground cut low every month meets the cue and orders no link, no bridge and no port.
 *
 * Every premise is asserted before what rests on it.
 *
 * @author Jerus
 */
public class IslandLinkCheck {

    static int fails = 0;
    static final PrintStream out = System.out;

    /** The seed every world here is drawn with: the default world's. */
    static final long SEED = Founding.DEFAULT_WORLD_SEED;

    /** Months a city with room is played in section 4: 12. */
    static final int ROOM_MONTHS = 12;

    /** Most months a bridge is given to stand on two builders' depots: 800 (BridgeCheck allows 600 for the same fixture). */
    static final int BRIDGE_MONTHS = 800;

    /** Months the played Earth run and the generated world run in section 9: 240. */
    static final int RUN_MONTHS = 240;

    /** The label the auto-builder enters its island links under in its log (AutoBuilder's, private). */
    static final String LINKS_LABEL = "Island links";

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-112s %s%n", label, Timing.verdict(ok, label));
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-112s %s  %s%n", label, Timing.verdict(ok, label), detail);
    }

    /** Two figures are one within a part in a billion. */
    static boolean near(double a, double b) { return Math.abs(a - b) <= 1e-9 * Math.max(1, Math.abs(b)); }

    static void quietly(Runnable r) { LandmassCheck.quietly(r); }

    /** One month played quietly, the auto-builder's pass first in it. */
    static void month(LandmassCheck.City c) { quietly(() -> c.g.simulateMonths(1)); }

    /**
     * The condition "the linked free ground is low": the ground the buildings stand on set to what the city owns on linked
     * landmasses (and the island terminals' ground, which is the island's room, not the linked pool's), the way LandmassCheck
     * 5 fills a landmass, so that no order that needs ground finds any free.
     */
    static void cutGround(LandmassCheck.City c) {
        quietly(() -> c.lm.setAllocatedSqFt(c.lm.getLinkedOwnedSqFt() + c.books().unlinkedPortsSqFt()));
    }

    /** Passes whose orders must meet the ground short, in a stretch of months with the ground cut low before each, for "the next passes order no second link" to mean something: 1 (the village orders only when it needs something, 1 to 12 months in 240). */
    static final int CUE_PASSES = 1;

    /** Most months such a stretch is given to meet CUE_PASSES: 120. */
    static final int CUE_MONTHS = 120;

    /**
     * The pass's last line called by hand with the ground cut low: link(), asked of the city as it stands (a pass sets the cue only when
     * it places an order, which the village does in a few months of a hundred) - whether it added a step, an order, a bridge or took cash.
     */
    static boolean linkOrdersNothing(LandmassCheck.City c) {
        AutoBuilder ab = c.g.getAutoBuilder();
        cutGround(c);
        int steps = ab.linkSteps().size(), bridges = c.g.getBridges().size();
        long orders = ab.getOrders();
        double cash = c.g.getCash();
        quietly(() -> {
            c.g.getDecisions().hold();
            try { ab.link(c.g); } finally { c.g.getDecisions().release(); }
        });
        return ab.linkSteps().size() == steps && ab.getOrders() == orders && c.g.getBridges().size() == bridges && c.g.getCash() == cash;
    }

    /**
     * Months played, the ground cut low before each, until `passes` of them met an order short of ground (groundShort()), at most
     * CUE_MONTHS: the months played, the passes that met it, the link steps seen in them and the months `holds` was false after.
     */
    static int[] lowPasses(LandmassCheck.City c, int passes, java.util.function.BooleanSupplier holds) {
        AutoBuilder ab = c.g.getAutoBuilder();
        int months = 0, cue = 0, steps = 0, broken = 0;
        while (cue < passes && months < CUE_MONTHS) {
            cutGround(c);
            month(c);
            months++;
            if (ab.groundShort()) cue++;
            steps += ab.linkSteps().size();
            if (!holds.getAsBoolean()) broken++;
        }
        return new int[] { months, cue, steps, broken };
    }

    static void autoBuilderOn(LandmassCheck.City c) {
        quietly(() -> c.g.getAutoBuilder().setOn(true, c.g.getDecisions()));
    }

    /** The card's pick of a bridge's far landmass put back as it was. */
    static void putBack(Game g, BuildingsTemplate t, int pick) {
        if (pick == Bridges.NONE_CHOSEN) g.getBridges().clearChoice(t.getId());
        else g.chooseBridgeLandmass(t, pick);
    }

    static Landmasses.Row rowOf(Landmasses lms, int id) {
        for (Landmasses.Row r : lms.rows()) if (r.id() == id) return r;
        return null;
    }

    /** The quote of one bridge with the island picked, the pick put back. */
    static Game.BuildQuote bridgeQuote(Game g, BuildingsTemplate t, int island) {
        int before = g.getBridges().chosen(t.getId());
        g.chooseBridgeLandmass(t, island);
        try {
            return g.quoteBuildOn(t, 1, Landmasses.RULE);
        } finally {
            putBack(g, t, before);
        }
    }

    /** What the sea terminals the city knows come to for a landmass asked: how many are port templates the city knows, how many would stand there, and whether one of those is cheaper than a price. */
    record Terminals(int known, int fitting, boolean cheaper, double cheapest) { }

    /**
     * Every known port template, quoted for `asked`: those that stand where it is asked (an island's own shore, fitting its room;
     * the rule's landmass for RULE) and the cheapest of them, and whether any is cheaper than `price` by a hair.
     */
    static Terminals terminals(Game g, int asked, boolean onIsland, double price) {
        Landmasses lms = g.getLandManager().getLandmasses();
        int known = 0, fitting = 0;
        boolean cheaper = false;
        double cheapest = Double.POSITIVE_INFINITY;
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            if (!Ports.isPort(t) || !g.knows(t)) continue;
            known++;
            int where = lms.portLandmass(t.getId(), asked);
            boolean fits = onIsland ? where == asked && g.getLandManager().canAllocateOn(asked, t.getLandSqFt()) : where != Landmasses.NO_LANDMASS;
            if (!fits) continue;
            fitting++;
            double p = g.quoteBuildOn(t, 1, asked).total;
            cheapest = Math.min(cheapest, p);
            if (p < price * (1 - 1e-12)) cheaper = true;
        }
        return new Terminals(known, fitting, cheaper, cheapest);
    }

    /** Whether the order's template is a known sea terminal that would stand where `asked` says, and the order's price is the quote of one. */
    static boolean terminalOrder(Game g, BuildAdvice.LinkOrder o, int asked) {
        Landmasses lms = g.getLandManager().getLandmasses();
        BuildingsTemplate t = o.template();
        int where = lms.portLandmass(t.getId(), asked);
        return Ports.isPort(t) && g.knows(t) && o.asked() == asked && near(o.price(), g.quoteBuildOn(t, 1, asked).total)
                && (asked == Landmasses.RULE ? where != Landmasses.NO_LANDMASS : where == asked && g.getLandManager().canAllocateOn(asked, t.getLandSqFt()));
    }

    /** The orders of a link by template name, "Bulk Berth on Island 2, Bulk Berth on linked ground". */
    static String words(BuildAdvice.Link l) {
        if (l == null) return "null";
        StringBuilder s = new StringBuilder(l.kind().toString());
        for (BuildAdvice.LinkOrder o : l.orders()) s.append(s.length() > l.kind().toString().length() ? ", " : ": ").append(o.template().getName()).append(o.asked() == Landmasses.RULE ? " at home" : " for " + o.asked());
        return s.append(String.format(", $%,.0fk", l.price())).toString();
    }

    /* =====================================================================
       MAIN
       ===================================================================== */

    public static void main(String[] args) throws Exception {
        java.util.Locale.setDefault(java.util.Locale.CANADA);
        out.println("--- the fixture ---");
        byte[] fixture = EarthCheck.resource(EarthCheck.FIXTURE);
        check("EarthCheck's fixture is beside the classes (" + EarthCheck.FIXTURE + ")", fixture != null);
        if (fixture == null) {
            out.println("\n" + fails + " FAILED");
            System.exit(1);
        }
        EarthData data = EarthData.of(fixture);
        String id = EarthTerrain.use(data);
        World.Key rotterdam = World.Key.earth(SEED, LandmassCheck.RLAT, LandmassCheck.RLON, id);
        LandmassCheck.Fixtures f = LandmassCheck.fixtures(rotterdam);
        check("the island square with shore, the one with none and home's coast were found (LandmassCheck's fixtures)", f.ok);
        if (!f.ok) {
            out.println("\n" + fails + " FAILED");
            System.exit(1);
        }
        BridgeCheck.Island isle = new BridgeCheck.Island(rotterdam, f.withShore), dry = new BridgeCheck.Island(rotterdam, f.withoutShore);
        Path root = TempDirs.create("islandlinkcheck");
        ruleWithACrossing(isle);
        ruleOnDeepSea(f, isle);
        noLink(f, isle, dry, root);
        autoBuilderOnACrossing(f, isle);
        autoBuilderOnDeepSea(f, isle);
        theMoney(isle);
        theBridgeStands(isle);
        testPlayer(f, isle, dry);
        playedRun(f, root);
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* =====================================================================
       1. THE RULE, A CROSSING ALLOWED
       ===================================================================== */

    static void ruleWithACrossing(BridgeCheck.Island isle) {
        out.println("\n--- 1. the rule, a crossing allowed: the Road Bridge, the Highway Bridge where a highway stands, priced as the card prices it ---");
        LandmassCheck.City c = BridgeCheck.bridgeCity(isle, "link-rule-bridge");
        Game g = c.g;
        Landmasses lms = c.books();
        Bridges bridges = g.getBridges();
        BuildingsTemplate road = LandmassCheck.tpl(c, "Road Bridge"), highway = LandmassCheck.tpl(c, "Highway Bridge"), elevated = LandmassCheck.tpl(c, "Elevated Highway");
        int island = isle.id(), home = lms.home();
        boolean highways = g.getBuildingManager().standingWhere(t -> t.getCategory() == BuildingType.INFRASTRUCTURE && t.isHighway()) > 0;
        report("premise: the city knows the Road Bridge, the Highway Bridge and the Elevated Highway this month; it owns the island, unlinked, with no bridge, no highway and no pick",
                g.knows(road) && g.knows(highway) && g.knows(elevated) && road.isBridge() && highway.isBridge() && elevated.isHighway() && lms.anyUnlinked() && !lms.linked(island)
                        && bridges.size() == 0 && !highways && bridges.chosen(road.getId()) == Bridges.NONE_CHOSEN && bridges.chosen(highway.getId()) == Bridges.NONE_CHOSEN,
                "month " + g.getMonth() + ", island " + island + ", home " + home);
        BridgeCheck.seam(c, isle, 14, World.SHALLOW, 10);
        BuildAdvice.Link l = BuildAdvice.linkFor(g);
        Landmasses.Row row = rowOf(lms, island);
        report("with a shallow channel of 14 plots between them the rule is a BRIDGE toward the island: one order, the Road Bridge (no highway stands), for the island",
                l != null && l.kind() == BuildAdvice.LinkKind.BRIDGE && l.orders().size() == 1 && l.orders().get(0).template().getId() == road.getId() && l.orders().get(0).asked() == island
                        && l.landmass() == island && l.name().equals(lms.name(island)) && l.survey() != null && l.survey().ok() && l.why() == null,
                words(l) + (l == null || l.survey() == null ? "" : String.format(" over %.0f m", l.survey().lengthM())));
        Game.BuildQuote q = bridgeQuote(g, road, island);
        check("...its price is the quote of one Road Bridge with the island picked, and its months that quote's", l != null && near(l.price(), q.total) && near(l.orders().get(0).price(), q.total) && near(l.months(), q.months) && q.total > 0);
        report("...its ground is the island row's buildable ground, and the price a square foot is the price over it",
                l != null && row != null && near(l.sqFt(), row.buildableSqFt()) && l.sqFt() > 0 && near(l.perSqFt(), l.price() / row.buildableSqFt()),
                l == null ? "" : String.format("$%,.0fk over %,.0f sq ft: $%.2f a square foot", l.price(), l.sqFt(), l.perSqFt() * 1000));
        check("the card's pick of the bridge's far landmass is as the rule found it: none before, none after", bridges.chosen(road.getId()) == Bridges.NONE_CHOSEN && bridges.chosen(highway.getId()) == Bridges.NONE_CHOSEN);
        g.chooseBridgeLandmass(road, home);
        BuildAdvice.Link again = BuildAdvice.linkFor(g);
        check("...and with the pick set to another value (home) it is home after, and the rule's answer is the same",
                bridges.chosen(road.getId()) == home && again != null && again.kind() == BuildAdvice.LinkKind.BRIDGE && again.landmass() == island && near(again.price(), l.price()));
        bridges.clearChoice(road.getId());
        int surveys = BuildAdvice.linkSurveys;
        for (int k = 0; k < 3; k++) BuildAdvice.linkFor(g);
        check("an island with a crossing is not remembered: three asks in a month are three surveys", BuildAdvice.linkSurveys == surveys + 3);

        // A highway standing: the Highway Bridge.
        Game.BuildResult built = LandmassCheck.order(c, elevated, 1, true, Landmasses.RULE);
        highways = g.getBuildingManager().standingWhere(t -> t.getCategory() == BuildingType.INFRASTRUCTURE && t.isHighway()) > 0;
        report("an Elevated Highway built at once: a highway stands", built == Game.BuildResult.SUCCESS && highways, "order " + built);
        BuildAdvice.Link h = BuildAdvice.linkFor(g);
        Game.BuildQuote hq = bridgeQuote(g, highway, island);
        report("the rule is then the Highway Bridge, one order for the island, priced as the quote of one with the island picked",
                h != null && h.kind() == BuildAdvice.LinkKind.BRIDGE && h.orders().size() == 1 && h.orders().get(0).template().getId() == highway.getId() && h.orders().get(0).asked() == island
                        && near(h.price(), hq.total) && h.sqFt() == l.sqFt(),
                words(h));
        check("...and the pick is still none", bridges.chosen(road.getId()) == Bridges.NONE_CHOSEN && bridges.chosen(highway.getId()) == Bridges.NONE_CHOSEN);
    }

    /* =====================================================================
       2. THE RULE, DEEP SEA
       ===================================================================== */

    static void ruleOnDeepSea(LandmassCheck.Fixtures f, BridgeCheck.Island isle) {
        out.println("\n--- 2. the rule, deep sea: the cheapest terminal on the island's shore, and one at home where no linked ground has one ---");
        int island = isle.id();
        LandmassCheck.City c = LandmassCheck.portCity(f, "link-rule-port", true);
        Game g = c.g;
        Landmasses lms = c.books();
        int home = lms.home();
        BridgeCheck.seam(c, isle, 12, World.SALT, 45);
        BuildingsTemplate road = LandmassCheck.tpl(c, "Road Bridge");
        BridgeSurvey.Result survey = g.bridgeSurvey(road);
        report("premise: the survey of the island's channel finds deep water (45 m), the city owns shore on the island and at home, and no terminal stands or is on site anywhere",
                survey != null && !survey.ok() && survey.why() == BridgeSurvey.Why.DEEP && lms.shorePlots(island) > 0 && lms.shorePlots(home) > 0 && lms.portsAt(island) == 0 && lms.portsAt(home) == 0
                        && lms.anyUnlinked() && !lms.linked(island),
                survey == null ? "" : BridgeView.refusal(survey, lms));
        BuildAdvice.Link l = BuildAdvice.linkFor(g);
        BuildAdvice.LinkOrder there = l == null || l.orders().isEmpty() ? null : l.orders().get(0);
        report("the rule is a PORT: two orders, the island's own first and then one at home (the rule's landmass), no survey in the answer",
                l != null && l.kind() == BuildAdvice.LinkKind.PORT && l.orders().size() == 2 && there.asked() == island && l.orders().get(1).asked() == Landmasses.RULE
                        && l.landmass() == island && l.survey() == null && l.why() == null,
                words(l));
        Terminals ti = there == null ? null : terminals(g, island, true, there.price());
        report("...the island's order is a known terminal that stands on the island and fits its room, at its quote, and none that fits is cheaper (every port template's quote asked)",
                there != null && terminalOrder(g, there, island) && !ti.cheaper() && ti.fitting() >= 1,
                ti == null ? "" : String.format("%d port templates known, %d fit the island's room; dearest to cheapest from $%,.0fk", ti.known(), ti.fitting(), ti.cheapest()));
        BuildAdvice.LinkOrder atHome = l == null || l.orders().size() < 2 ? null : l.orders().get(1);
        Terminals th = atHome == null ? null : terminals(g, Landmasses.RULE, false, atHome.price());
        report("...the home order is a known terminal that stands where the rule puts it, at its quote, and none is cheaper",
                atHome != null && terminalOrder(g, atHome, Landmasses.RULE) && !th.cheaper() && th.fitting() >= 1,
                th == null ? "" : String.format("%d port templates known, %d stand at home; cheapest $%,.0fk", th.known(), th.fitting(), th.cheapest()));
        check("...the price is the two orders' sum and the months the longer one's, the island's ground the row's buildable ground",
                l != null && near(l.price(), l.orders().get(0).price() + l.orders().get(1).price()) && l.months() > 0 && near(l.sqFt(), rowOf(lms, island).buildableSqFt()));

        // A terminal standing at home: the island's alone.
        BuildingsTemplate homeT = atHome == null ? null : LandmassCheck.own(c, atHome.template());
        Game.BuildResult homeBuilt = homeT == null ? null : LandmassCheck.order(c, homeT, 1, true, Landmasses.RULE);
        BuildAdvice.Link shared = BuildAdvice.linkFor(g);
        report("with a terminal standing at home (ordered at once) the rule is the island's order alone: a port shared where one already serves",
                homeBuilt == Game.BuildResult.SUCCESS && lms.portsAt(home) == 1 && lms.portsAt(island) == 0 && shared != null && shared.kind() == BuildAdvice.LinkKind.PORT && shared.orders().size() == 1
                        && shared.orders().get(0).asked() == island && terminalOrder(g, shared.orders().get(0), island) && !terminals(g, island, true, shared.orders().get(0).price()).cheaper()
                        && near(shared.price(), shared.orders().get(0).price()),
                words(shared));

        // A terminal on the island and none at home: the home order alone.
        LandmassCheck.City d = LandmassCheck.portCity(f, "link-rule-port-island", true);
        BridgeCheck.seam(d, isle, 12, World.SALT, 45);
        Landmasses ld = d.books();
        BuildAdvice.Link first = BuildAdvice.linkFor(d.g);
        BuildingsTemplate islandT = first == null ? null : LandmassCheck.own(d, first.orders().get(0).template());
        Game.BuildResult islandBuilt = islandT == null ? null : LandmassCheck.orderOn(d, islandT, 1, true, island);
        BuildAdvice.Link rest = BuildAdvice.linkFor(d.g);
        report("with a terminal on the island and none at home the rule is the home order alone, the cheapest that stands at home",
                islandBuilt == Game.BuildResult.SUCCESS && ld.portsAt(island) == 1 && ld.portsAt(ld.home()) == 0 && rest != null && rest.kind() == BuildAdvice.LinkKind.PORT && rest.orders().size() == 1
                        && rest.orders().get(0).asked() == Landmasses.RULE && terminalOrder(d.g, rest.orders().get(0), Landmasses.RULE)
                        && !terminals(d.g, Landmasses.RULE, false, rest.orders().get(0).price()).cheaper() && near(rest.price(), rest.orders().get(0).price()),
                words(rest));

        // Both on their way: nothing more to order.
        Game.BuildResult second = rest == null ? null : LandmassCheck.order(d, LandmassCheck.own(d, rest.orders().get(0).template()), 1, false, Landmasses.RULE);
        check("with the home terminal ordered too (on site) every link is under way: the rule is null, and the island stays unlinked until both stand",
                second == Game.BuildResult.SUCCESS && ld.portsAt(ld.home()) == 1 && BuildAdvice.linkFor(d.g) == null && ld.anyUnlinked() && !ld.linked(island));
    }

    /* =====================================================================
       3. NO LINK, OR NOTHING TO LINK
       ===================================================================== */

    static void noLink(LandmassCheck.Fixtures f, BridgeCheck.Island isle, BridgeCheck.Island dry, Path root) {
        out.println("\n--- 3. no link possible and nothing to link: NONE in words, null where there is nothing, and a survey that found no crossing remembered ---");
        // An island with no shore and no crossing.
        LandmassCheck.City c = BridgeCheck.bridgeCity(dry, "link-none-dry");
        Game g = c.g;
        Landmasses lms = c.books();
        int island = dry.id();
        BridgeCheck.seam(c, dry, 12, World.SALT, 45);
        BuildingsTemplate road = LandmassCheck.tpl(c, "Road Bridge");
        BridgeSurvey.Result survey = g.bridgeSurvey(road);
        report("premise: the island square owned has no shore (the city owns none on it), the survey finds deep water, and the city is not on one landmass",
                survey != null && survey.why() == BridgeSurvey.Why.DEEP && lms.shorePlots(island) == 0 && lms.anyUnlinked() && !lms.linked(island) && g.knows(road),
                "survey: " + (survey == null ? "null" : BridgeView.refusal(survey, lms)));
        int surveys0 = BuildAdvice.linkSurveys;
        int month0 = g.getMonth();
        BuildAdvice.Link l = BuildAdvice.linkFor(g);
        String refusal = survey == null ? "" : BridgeView.refusal(survey, lms);
        report("the rule is NONE: no orders, no survey in it, no price, and the words give the bridge's refusal and the port's, naming the island",
                l != null && l.kind() == BuildAdvice.LinkKind.NONE && l.orders().isEmpty() && l.survey() == null && l.price() == 0 && l.landmass() == island && l.why() != null
                        && l.why().startsWith(refusal) && l.why().contains("No port either") && l.why().contains(lms.name(island) + " has no shore the city owns"),
                l == null ? "null" : l.why());
        // The memory: asked every month for NO_CROSSING_MONTHS - 1 months, surveyed once; at the month it ends, again.
        boolean steady = BuildAdvice.linkSurveys == surveys0 + 1;
        String words = l == null ? null : l.why();
        for (int k = 1; k < BuildAdvice.NO_CROSSING_MONTHS; k++) {
            month(c);
            BuildAdvice.Link again = BuildAdvice.linkFor(g);
            steady &= again != null && again.kind() == BuildAdvice.LinkKind.NONE && again.why().equals(words);
        }
        report("a survey that found no crossing stands NO_CROSSING_MONTHS (" + BuildAdvice.NO_CROSSING_MONTHS + "): asked every month for " + (BuildAdvice.NO_CROSSING_MONTHS - 1) + " more months it is surveyed once in all, the same words each time",
                steady && BuildAdvice.linkSurveys == surveys0 + 1 && g.getMonth() == month0 + BuildAdvice.NO_CROSSING_MONTHS - 1, "surveys " + (BuildAdvice.linkSurveys - surveys0) + ", month " + g.getMonth());
        month(c);
        BuildAdvice.linkFor(g);
        check("...and at the month it ends it is surveyed again", BuildAdvice.linkSurveys == surveys0 + 2 && g.getMonth() == month0 + BuildAdvice.NO_CROSSING_MONTHS);
        // An island with shore where home owns none.
        LandmassCheck.City h = LandmassCheck.portCity(f, "link-none-home", false);
        BridgeCheck.seam(h, isle, 12, World.SALT, 45);
        Landmasses lh = h.books();
        BuildAdvice.Link noHome = BuildAdvice.linkFor(h.g);
        report("an island with shore where home owns none (a terminal on the island would link nothing without one beside it): NONE, in words naming home",
                lh.shorePlots(isle.id()) > 0 && lh.shorePlots(lh.home()) == 0 && noHome != null && noHome.kind() == BuildAdvice.LinkKind.NONE && noHome.orders().isEmpty()
                        && noHome.why().contains("No port either") && noHome.why().contains("home has no shore"),
                noHome == null ? "null" : noHome.why());

        // Nothing to link.
        LandmassCheck.City allHome = new LandmassCheck.City("link-all-home", f.key, 40);
        long read0 = allHome.books().plotsRead();
        check("a city whose ground is all at home: the rule is null, and no plot was read", !allHome.books().anyUnlinked() && BuildAdvice.linkFor(allHome.g) == null && allHome.books().plotsRead() == read0);
        GameFiles files = GameFiles.scratch("link-generated-rule");
        Game gen = new Game(files);
        quietly(() -> {
            gen.newGame(Founding.defaults());
            gen.setCashForTest(Founding.WEALTHY_CASH);
        });
        Landmasses lg = gen.getLandManager().getLandmasses();
        long readG = lg.plotsRead();
        int surveysG = BuildAdvice.linkSurveys;
        check("a generated world is one landmass: the rule is null, no plot was read and no survey asked", lg.single() && !lg.anyUnlinked() && BuildAdvice.linkFor(gen) == null && lg.plotsRead() == readG && BuildAdvice.linkSurveys == surveysG);
    }

    /* =====================================================================
       4. THE AUTO-BUILDER, A CROSSING
       ===================================================================== */

    /** The city section 4 played and section 7 stands the bridge of. */
    static LandmassCheck.City bridged;
    static int bridgedIsland;

    static void autoBuilderOnACrossing(LandmassCheck.Fixtures f, BridgeCheck.Island isle) {
        out.println("\n--- 4. the auto-builder, ground low caused, an island across shallow water: one bridge ordered, none ordered twice, none where there is room ---");
        int island = isle.id();
        // The side with room: home's coast bought beside the island (28 million square feet free), so that no order lacks ground; the cue is asked of real passes and is false.
        LandmassCheck.City r = BridgeCheck.bridgeCity(isle, "link-ab-room");
        r.buy(f.homeSalt);
        BridgeCheck.seam(r, isle, 14, World.SHALLOW, 10);
        autoBuilderOn(r);
        AutoBuilder rab = r.g.getAutoBuilder();
        long orders0 = rab.getOrders();
        int shortMonths = 0, linkSteps = 0;
        for (int m = 0; m < ROOM_MONTHS; m++) {
            month(r);
            if (rab.groundShort()) shortMonths++;
            linkSteps += rab.linkSteps().size();
        }
        report("a city with room (the island owned and unlinked, a crossing allowed): " + ROOM_MONTHS + " months with the auto-builder on, its orders placed, groundShort() in none, no link step, no bridge",
                rab.getOrders() > orders0 && shortMonths == 0 && linkSteps == 0 && r.g.getBridges().size() == 0 && r.books().anyUnlinked() && !r.books().linked(island),
                String.format("%d orders, linked free ground %,.0f sq ft", rab.getOrders() - orders0, r.lm.getAvailableSqFt()));

        // Ground low.
        LandmassCheck.City c = BridgeCheck.bridgeCity(isle, "link-ab-bridge");
        Game g = c.g;
        Landmasses lms = c.books();
        Bridges bridges = g.getBridges();
        AutoBuilder ab = g.getAutoBuilder();
        BridgeCheck.seam(c, isle, 14, World.SHALLOW, 10);
        autoBuilderOn(c);
        BuildingsTemplate road = LandmassCheck.tpl(c, "Road Bridge");
        BuildAdvice.Link want = BuildAdvice.linkFor(g);
        cutGround(c);
        report("premise: the linked free ground is cut to nothing, the island has free ground that is no room, and the rule is the Road Bridge for it",
                c.lm.getAvailableSqFt() < 1 && c.lm.getUnlinkedFreeSqFt() > 0 && AutoBuilder.overFull(g) == 0 && want != null && want.kind() == BuildAdvice.LinkKind.BRIDGE && bridges.size() == 0,
                String.format("linked free %,.1f sq ft, the island's %,.0f; %s", c.lm.getAvailableSqFt(), c.lm.getUnlinkedFreeSqFt(), words(want)));
        long orders1 = ab.getOrders(), buildings1 = ab.getBuildings();
        double spent1 = ab.getSpent();
        month(c);
        AutoBuilder.LinkStep step = ab.linkSteps().size() == 1 ? ab.linkSteps().get(0) : null;
        report("one pass: groundShort() is set and the pass orders one link, ORDERED, a bridge for the island",
                ab.groundShort() && step != null && step.outcome() == AutoBuilder.Outcome.ORDERED && step.kind() == BuildAdvice.LinkKind.BRIDGE && step.landmass() == island
                        && step.building().contains(road.getName()) && step.line().contains(lms.name(island)) && step.cost() > 0,
                step == null ? "no link step (" + ab.linkSteps().size() + ")" : step.line());
        check("...a bridge is on site toward the island (Bridges.buildingTo), none stands, and the island is unlinked while it is",
                bridges.buildingTo(island) && bridges.size() == 1 && bridges.count(Bridges.ON_SITE) == 1 && bridges.all().get(0).farLandmass == island && bridges.all().get(0).template == road.getId()
                        && LandmassCheck.onSite(c, road) == 1 && LandmassCheck.standing(c, road) == 0 && !lms.linked(island) && lms.anyUnlinked());
        int month = ab.getLastPass();
        double entryCost = 0;
        int entries = 0, entryUnits = 0;
        AutoBuilder.Entry entry = null;
        for (AutoBuilder.Entry e : ab.log()) {
            if (e.month() != month) continue;
            entries++;
            entryCost += e.cost();
            entryUnits += e.count();
            if (e.measure().equals(LINKS_LABEL) && e.building().equals(road.getName())) entry = e;
        }
        report("...the log has its entry (the island links' label, the Road Bridge, one, the island named in the why) and the totals count it with the pass's other orders",
                entry != null && entry.count() == 1 && step != null && near(entry.cost(), step.cost()) && entry.why().contains(lms.name(island)) && ab.getOrders() == orders1 + entries && ab.getBuildings() == buildings1 + entryUnits
                        && near(ab.getSpent(), spent1 + entryCost),
                String.format("%d orders in the pass for $%,.0fk; the bridge $%,.0fk", entries, entryCost, entry == null ? 0 : entry.cost()));
        check("...the step's line is the pass's sentence for the order, and the rule asked again says every link is under way: null",
                step != null && step.line().startsWith("Automatic building:") && BuildAdvice.linkFor(g) == null);

        // The next passes, the ground still low.
        int[] low = lowPasses(c, CUE_PASSES, () -> bridges.size() == 1 && (LandmassCheck.standing(c, road) >= 1 || !lms.linked(island)));
        report("the next passes, the ground cut low before each, until one met an order short of ground (the cue): no link step, no second bridge, and the island unlinked while the first is on site",
                low[1] >= CUE_PASSES && low[2] == 0 && low[3] == 0 && linkOrdersNothing(c),
                String.format("%d months, %d with the cue, %d link steps; bridges %d, on site %d, standing %d", low[0], low[1], low[2], bridges.size(), LandmassCheck.onSite(c, road), LandmassCheck.standing(c, road)));
        bridged = c;
        bridgedIsland = island;
    }

    /* =====================================================================
       5. THE AUTO-BUILDER, DEEP SEA
       ===================================================================== */

    static void autoBuilderOnDeepSea(LandmassCheck.Fixtures f, BridgeCheck.Island isle) {
        out.println("\n--- 5. the auto-builder, ground low caused, an island across deep water: the terminals ordered, once, and the island linked when both stand ---");
        int island = isle.id();
        LandmassCheck.City c = LandmassCheck.portCity(f, "link-ab-port", true);
        Game g = c.g;
        Landmasses lms = c.books();
        int home = lms.home();
        AutoBuilder ab = g.getAutoBuilder();
        BridgeCheck.seam(c, isle, 12, World.SALT, 45);
        autoBuilderOn(c);
        BuildAdvice.Link want = BuildAdvice.linkFor(g);
        cutGround(c);
        report("premise: the linked free ground is cut to nothing, no terminal anywhere, and the rule is a port: the island's terminal and one at home",
                c.lm.getAvailableSqFt() < 1 && AutoBuilder.overFull(g) == 0 && want != null && want.kind() == BuildAdvice.LinkKind.PORT && want.orders().size() == 2 && lms.portsAt(island) == 0 && lms.portsAt(home) == 0,
                words(want));
        month(c);
        AutoBuilder.LinkStep step = ab.linkSteps().size() == 1 ? ab.linkSteps().get(0) : null;
        report("one pass: groundShort() is set and the pass orders the link, ORDERED, a port for the island",
                ab.groundShort() && step != null && step.outcome() == AutoBuilder.Outcome.ORDERED && step.kind() == BuildAdvice.LinkKind.PORT && step.landmass() == island && step.cost() > 0
                        && step.building().contains(want.orders().get(0).template().getName()) && step.building().contains(want.orders().get(1).template().getName()),
                step == null ? "no link step (" + ab.linkSteps().size() + ")" : step.line());
        int onSiteIsland = lms.portsAt(island), onSiteHome = lms.portsAt(home);
        BuildingsTemplate t0 = want.orders().get(0).template(), t1 = want.orders().get(1).template();
        int wantedOnSite = t0.getId() == t1.getId() ? 2 : 1;
        check("...a terminal is on site on the island and one at home, none stands, and the island is unlinked",
                onSiteIsland == 1 && onSiteHome == 1 && LandmassCheck.onSite(c, t0) >= wantedOnSite && LandmassCheck.onSite(c, t1) >= wantedOnSite && lms.portsOn(island)[t0.berthCargo().ordinal()] == 0
                        && !lms.linked(island) && lms.anyUnlinked() && BuildAdvice.linkFor(g) == null);
        int month = ab.getLastPass();
        int linkEntries = 0;
        for (AutoBuilder.Entry e : ab.log()) if (e.month() == month && e.measure().equals(LINKS_LABEL)) linkEntries++;
        check("...the log has an entry for each of the two orders", linkEntries == 2);
        int[] low = lowPasses(c, CUE_PASSES, () -> lms.portsAt(island) == 1 && lms.portsAt(home) == 1);
        report("the next passes, the ground cut low before each, until one met an order short of ground (the cue): no link step, no more terminals than the two",
                low[1] >= CUE_PASSES && low[2] == 0 && low[3] == 0 && !lms.linked(island) && linkOrdersNothing(c),
                String.format("%d months, %d with the cue, %d link steps; month %d", low[0], low[1], low[2], g.getMonth()));
        boolean linked = LandmassCheck.until(c, () -> lms.linked(island), LandmassCheck.TERMINAL_MONTHS);
        report("the terminals stand after some months and the island is linked, nothing unlinked, each landmass holding its own", linked && !lms.anyUnlinked() && lms.portsOn(island)[t0.berthCargo().ordinal()] + lms.portsOn(island)[t1.berthCargo().ordinal()] >= 1
                        && lms.portsAt(home) >= 1, "month " + g.getMonth());
        int[] after = lowPasses(c, CUE_PASSES, () -> true);
        report("the passes after, the ground cut low (the village orders only when it needs something, so the cue may not come): no link step, and the rule is null",
                after[2] == 0 && linkOrdersNothing(c) && BuildAdvice.linkFor(g) == null, String.format("%d months, %d with the cue, %d link steps", after[0], after[1], after[2]));
    }

    /* =====================================================================
       6. THE MONEY
       ===================================================================== */

    static void theMoney(BridgeCheck.Island isle) {
        out.println("\n--- 6. the money: a debt limit of 0 and the cash at the reserve hold the link, with the limit's words, and nothing is bought or borrowed ---");
        int island = isle.id();
        LandmassCheck.City c = BridgeCheck.bridgeCity(isle, "link-ab-money");
        Game g = c.g;
        Landmasses lms = c.books();
        AutoBuilder ab = g.getAutoBuilder();
        BridgeCheck.seam(c, isle, 14, World.SHALLOW, 10);
        BuildAdvice.Link want = BuildAdvice.linkFor(g);
        ab.setDebtLimit(0, null);
        autoBuilderOn(c);
        cutGround(c);
        quietly(() -> g.setCashForTest(AutoBuilder.reserve(g)));
        report("premise: the debt limit is 0, the cash is the reserve - the link's price is far past what is spendable - and the ground is cut low",
                ab.getDebtLimit() == 0 && AutoBuilder.spendable(g) <= 0 && want != null && want.price() > AutoBuilder.spendable(g) && c.lm.getAvailableSqFt() < 1,
                String.format("spendable $%,.0fk, the link $%,.0fk", AutoBuilder.spendable(g), want == null ? 0 : want.price()));
        int[] paper = new int[2];
        g.autoBuildProbeForTest = after -> paper[after ? 1 : 0] = g.getDebtManager().getDebt().size();
        long orders0 = ab.getOrders();
        int offers0 = ab.getLandOffers(), bonds0 = ab.getBonds();
        double owned0 = c.lm.getOwnedSqFt(), borrowed0 = ab.getBorrowed();
        month(c);
        g.autoBuildProbeForTest = null;
        AutoBuilder.LinkStep step = ab.linkSteps().size() == 1 ? ab.linkSteps().get(0) : null;
        report("one pass: groundShort() is set, the link is HELD, and the line is in held() naming the island and the limit",
                ab.groundShort() && step != null && step.outcome() == AutoBuilder.Outcome.HELD && step.kind() == BuildAdvice.LinkKind.BRIDGE && step.landmass() == island
                        && ab.held().contains(step.line()) && step.line().startsWith("Island links:") && step.line().contains(lms.name(island)) && step.line().contains("debt limit is 0%"),
                step == null ? "no link step (" + ab.linkSteps().size() + ")" : step.line());
        report("...nothing is ordered, no ground bought, no bond sold, nothing borrowed, and no bridge placed",
                ab.getOrders() == orders0 && ab.getLandOffers() == offers0 && ab.getBonds() == bonds0 && ab.getBorrowed() == borrowed0 && paper[1] == paper[0] && c.lm.getOwnedSqFt() == owned0
                        && g.getBridges().size() == 0 && !g.getBridges().buildingTo(island),
                String.format("%d offers, %d bonds, %d debts before and after", ab.getLandOffers() - offers0, ab.getBonds() - bonds0, paper[1]));
        // ...asked again, the pass's last line called by hand (the village orders only in some months, and the cue is met only then): the cash at the reserve again, the ground cut again.
        cutGround(c);
        quietly(() -> g.setCashForTest(AutoBuilder.reserve(g)));
        int steps1 = ab.linkSteps().size();
        quietly(() -> {
            g.getDecisions().hold();
            try { ab.link(g); } finally { g.getDecisions().release(); }
        });
        check("...asked again with the cash at the reserve once more it is held again, one more HELD step and no order - it asks again, it does not give up",
                ab.linkSteps().size() == steps1 + 1 && ab.linkSteps().get(steps1).outcome() == AutoBuilder.Outcome.HELD && g.getBridges().size() == 0 && ab.getOrders() == orders0 && ab.getBonds() == bonds0);
    }

    /* =====================================================================
       7. THE BRIDGE STANDS AND LINKS
       ===================================================================== */

    static void theBridgeStands(BridgeCheck.Island isle) {
        out.println("\n--- 7. the bridge stands and links: the island linked, its free ground room, and the next pass with ground low orders nothing more ---");
        LandmassCheck.City c = bridged;
        if (c == null) { check("section 4's city was played", false); return; }
        Game g = c.g;
        Landmasses lms = c.books();
        Bridges bridges = g.getBridges();
        AutoBuilder ab = g.getAutoBuilder();
        BuildingsTemplate road = LandmassCheck.tpl(c, "Road Bridge");
        int island = bridgedIsland, version = lms.version();
        double availPrev = c.lm.getAvailableSqFt(), unlinkedPrev = c.lm.getUnlinkedFreeSqFt();
        int months = 0;
        boolean early = false;
        while (!lms.linked(island) && months < BRIDGE_MONTHS) {
            availPrev = c.lm.getAvailableSqFt();
            unlinkedPrev = c.lm.getUnlinkedFreeSqFt();
            month(c);
            months++;
            early |= lms.linked(island) && LandmassCheck.standing(c, road) < 1;
        }
        report("the bridge the auto-builder ordered stands after some months, and the island stayed unlinked every month it was on site", LandmassCheck.standing(c, road) == 1 && lms.linked(island) && !early && months >= 2,
                "month " + g.getMonth() + ", " + months + " more months");
        report("the month it stood the island was linked: the linked set moved once, nothing is unlinked, the island's ground is the linked ground and is room (the linked free ground rose by about its free ground)",
                lms.version() == version + 1 && !lms.anyUnlinked() && c.lm.getUnlinkedFreeSqFt() == 0 && LandmassCheck.near(c.lm.getLinkedOwnedSqFt(), c.lm.getOwnedSqFt())
                        && bridges.count(Bridges.STANDING) == 1 && bridges.joins(island) && c.lm.getAvailableSqFt() - availPrev > unlinkedPrev / 2,
                String.format("linked free %,.0f -> %,.0f sq ft; the island's free ground was %,.0f", availPrev, c.lm.getAvailableSqFt(), unlinkedPrev));
        check("the rule is null: nothing is unlinked", BuildAdvice.linkFor(g) == null);
        int bridgesBefore = bridges.size();
        int[] low = lowPasses(c, CUE_PASSES, () -> true);
        report("the passes after, the ground cut low (the cue may not come): no link step, no more bridges", low[2] == 0 && linkOrdersNothing(c) && bridges.size() == bridgesBefore && !lms.anyUnlinked(),
                String.format("%d months, %d with the cue, %d link steps; bridges %d", low[0], low[1], low[2], bridges.size()));
    }

    /* =====================================================================
       8. THE TEST PLAYER LINKS AS THE AUTO-BUILDER DOES
       ===================================================================== */

    static void testPlayer(LandmassCheck.Fixtures f, BridgeCheck.Island isle, BridgeCheck.Island dry) {
        out.println("\n--- 8. the test player links as the auto-builder does: the same rule, the same templates, the same landmass, counted ---");
        int island = isle.id();
        // A crossing.
        LandmassCheck.City c = BridgeCheck.bridgeCity(isle, "link-tp-bridge");
        Game g = c.g;
        Landmasses lms = c.books();
        BridgeCheck.seam(c, isle, 14, World.SHALLOW, 10);
        BuildAdvice.Link want = BuildAdvice.linkFor(g);
        cutGround(c);
        double line = LongPlaytest.groundAheadUtilisation(g);
        report("premise: the used share is over the test player's line (groundAheadUtilisation) and the island is owned and unlinked",
                c.lm.getUsedShare() > line && c.lm.getOwnedSqFt() > 0 && lms.anyUnlinked() && want != null && want.kind() == BuildAdvice.LinkKind.BRIDGE,
                String.format("used %.4f against %.4f; %s", c.lm.getUsedShare(), line, words(want)));
        int b0 = LongPlaytest.islandBridges, p0 = LongPlaytest.islandPorts, h0 = LongPlaytest.islandLinksHeld;
        quietly(() -> LongPlaytest.keepGroundAhead(g));
        Bridges.Row row = g.getBridges().size() == 1 ? g.getBridges().all().get(0) : null;
        report("on the crossing it orders the bridge the rule gives, toward the landmass the rule gives, and counts it", row != null && want != null && row.template == want.orders().get(0).template().getId()
                        && row.farLandmass == want.landmass() && row.state == Bridges.ON_SITE && LongPlaytest.islandBridges == b0 + 1 && LongPlaytest.islandPorts == p0 && LongPlaytest.islandLinksHeld == h0 && !lms.linked(island),
                String.format("bridges %d, ports %d, held %d (before %d, %d, %d)", LongPlaytest.islandBridges, LongPlaytest.islandPorts, LongPlaytest.islandLinksHeld, b0, p0, h0));
        // Deep sea.
        LandmassCheck.City d = LandmassCheck.portCity(f, "link-tp-port", true);
        Landmasses ld = d.books();
        BridgeCheck.seam(d, isle, 12, World.SALT, 45);
        BuildAdvice.Link wantPort = BuildAdvice.linkFor(d.g);
        cutGround(d);
        int b1 = LongPlaytest.islandBridges, p1 = LongPlaytest.islandPorts, h1 = LongPlaytest.islandLinksHeld;
        boolean over = d.lm.getUsedShare() > LongPlaytest.groundAheadUtilisation(d.g);
        quietly(() -> LongPlaytest.keepGroundAhead(d.g));
        BuildingsTemplate t0 = wantPort == null ? null : wantPort.orders().get(0).template(), t1 = wantPort == null ? null : wantPort.orders().get(1).template();
        report("on deep sea it orders the terminals the rule gives, one on the island and one at home, and counts two", over && wantPort != null && wantPort.kind() == BuildAdvice.LinkKind.PORT && wantPort.orders().size() == 2
                        && ld.portsAt(island) == 1 && ld.portsAt(ld.home()) == 1 && LandmassCheck.onSite(d, t0) >= (t0.getId() == t1.getId() ? 2 : 1) && LandmassCheck.onSite(d, t1) >= (t0.getId() == t1.getId() ? 2 : 1)
                        && LongPlaytest.islandPorts == p1 + 2 && LongPlaytest.islandBridges == b1 && LongPlaytest.islandLinksHeld == h1 && !ld.linked(island) && BuildAdvice.linkFor(d.g) == null,
                String.format("bridges %d, ports %d, held %d (before %d, %d, %d)", LongPlaytest.islandBridges, LongPlaytest.islandPorts, LongPlaytest.islandLinksHeld, b1, p1, h1));
        // Nothing to order.
        LandmassCheck.City n = BridgeCheck.bridgeCity(dry, "link-tp-none");
        BridgeCheck.seam(n, dry, 12, World.SALT, 45);
        BuildAdvice.Link none = BuildAdvice.linkFor(n.g);
        cutGround(n);
        int b2 = LongPlaytest.islandBridges, p2 = LongPlaytest.islandPorts, h2 = LongPlaytest.islandLinksHeld;
        quietly(() -> LongPlaytest.keepGroundAhead(n.g));
        check("where the rule is NONE it orders nothing and counts the link held", none != null && none.kind() == BuildAdvice.LinkKind.NONE && LongPlaytest.islandLinksHeld == h2 + 1
                && LongPlaytest.islandBridges == b2 && LongPlaytest.islandPorts == p2 && n.g.getBridges().size() == 0 && !n.books().hasPlacements());
    }

    /* =====================================================================
       9. COUNTED IN A PLAYED RUN
       ===================================================================== */

    static void playedRun(LandmassCheck.Fixtures f, Path root) {
        out.println("\n--- 9. counted in a played Earth run: the island square, its real strait, " + RUN_MONTHS + " months with the auto-builder on; and a generated world ---");
        LandmassCheck.City c = LandmassCheck.portCity(f, "link-run", true);
        Game g = c.g;
        Landmasses lms = c.books();
        AutoBuilder ab = g.getAutoBuilder();
        int island = f.withShore.id();
        autoBuilderOn(c);
        report("premise: the city owns the island square, unlinked, home's coast too, and the strait between is the fixture's own (no seam handed to the survey)",
                lms.anyUnlinked() && !lms.linked(island) && lms.shorePlots(island) > 0 && lms.shorePlots(lms.home()) > 0 && !lms.hasPlacements(), "month " + g.getMonth());
        int[] shortMonths = { 0 }, bridgeLinks = { 0 }, portLinks = { 0 }, terminals = { 0 }, held = { 0 }, twice = { 0 }, notUnlinked = { 0 };
        int[] linkedAt = { -1 };
        Set<Integer> unlinkedBefore = new HashSet<>(), underway = new HashSet<>();
        g.autoBuildProbeForTest = after -> {
            if (!after) {
                underway.removeIf(lms::linked);
                unlinkedBefore.clear();
                unlinkedBefore.addAll(lms.unlinkedIds());
                return;
            }
            if (ab.groundShort()) shortMonths[0]++;
            for (AutoBuilder.LinkStep s : ab.linkSteps()) {
                if (s.outcome() != AutoBuilder.Outcome.ORDERED) {
                    held[0]++;
                    continue;
                }
                if (s.kind() == BuildAdvice.LinkKind.BRIDGE) bridgeLinks[0]++;
                else {
                    portLinks[0]++;
                    terminals[0] += s.building().split(" and ").length;
                }
                if (!unlinkedBefore.contains(s.landmass())) notUnlinked[0]++;
                if (!underway.add(s.landmass())) twice[0]++;
            }
            if (linkedAt[0] < 0 && lms.linked(island)) linkedAt[0] = g.getMonth();
        };
        int month0 = g.getMonth();
        for (int m = 0; m < RUN_MONTHS; m++) {
            month(c);
            if (linkedAt[0] < 0 && lms.linked(island)) linkedAt[0] = g.getMonth();
        }
        g.autoBuildProbeForTest = null;
        report(RUN_MONTHS + " months: " + shortMonths[0] + " with groundShort(), " + bridgeLinks[0] + " bridge link(s), " + portLinks[0] + " port link(s) (" + terminals[0] + " terminal order(s)), " + held[0] + " held; the island linked in month "
                        + (linkedAt[0] < 0 ? "never" : linkedAt[0] + " (" + (linkedAt[0] - month0) + " months in)"),
                shortMonths[0] > 0 && bridgeLinks[0] == 0 && portLinks[0] >= 1, "the real strait is open water past 45 km: no crossing, so ports");
        check("...at most one link order for an island while one is under way, and every order was for an island unlinked when it was ordered", twice[0] == 0 && notUnlinked[0] == 0);
        check("...the island is linked by the end (the terminals stand on both sides), and nothing is unlinked", lms.linked(island) && !lms.anyUnlinked() && linkedAt[0] > 0);

        // A generated world, its ground cut low every month: the cue is there, the link is not.
        ChildcareCheck.out = BridgeCheck.QUIET;
        ChildcareCheck.quiet = BridgeCheck.QUIET;
        Game t = AutoBuildCheck.noGround(root, "link-generated-run");
        AutoBuilder tab = t.getAutoBuilder();
        Landmasses lt = t.getLandManager().getLandmasses();
        quietly(() -> tab.setOn(true, t.getDecisions()));
        int shortT = 0, stepsT = 0;
        for (int m = 0; m < RUN_MONTHS; m++) {
            // ...the ground cut to what it uses before every month, as the Earth city's is in sections 4 and 5, so that the cue is met as often as the town orders.
            quietly(() -> t.getLandManager().setAllocatedSqFt(t.getLandManager().getLinkedOwnedSqFt()));
            quietly(() -> t.simulateMonths(1));
            if (tab.groundShort()) shortT++;
            stepsT += tab.linkSteps().size();
        }
        report("a generated world, the town with its ground cut to what it uses, " + RUN_MONTHS + " months with the auto-builder on: ground short in " + shortT + " of them, and no link step, no bridge, no port off home",
                tab.getOrders() > 0 && shortT > 0 && stepsT == 0 && t.getBridges().size() == 0 && lt.single() && !lt.hasPlacements() && lt.plotsRead() == 0,
                tab.getOrders() + " orders, " + lt.rows().size() + " landmass");
    }
}

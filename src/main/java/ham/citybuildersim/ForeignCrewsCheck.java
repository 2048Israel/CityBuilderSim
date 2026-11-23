package ham.citybuildersim;

import ham.citybuildersim.sectors.Construction;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Locale;

import static ham.citybuildersim.ConstructionTrapCheck.BUILDERS;
import static ham.citybuildersim.ConstructionTrapCheck.HOUSE;
import static ham.citybuildersim.ConstructionTrapCheck.RE;
import static ham.citybuildersim.ConstructionTrapCheck.assertTrue;
import static ham.citybuildersim.ConstructionTrapCheck.audited;
import static ham.citybuildersim.ConstructionTrapCheck.bits;
import static ham.citybuildersim.ConstructionTrapCheck.calm;
import static ham.citybuildersim.ConstructionTrapCheck.close;
import static ham.citybuildersim.ConstructionTrapCheck.month;
import static ham.citybuildersim.ConstructionTrapCheck.out;
import static ham.citybuildersim.ConstructionTrapCheck.perRoad;
import static ham.citybuildersim.ConstructionTrapCheck.quietly;
import static ham.citybuildersim.ConstructionTrapCheck.roads;
import static ham.citybuildersim.ConstructionTrapCheck.roadsFor;
import static ham.citybuildersim.ConstructionTrapCheck.template;

/**
 * The foreign crews always on offer (FC2): the threshold past which the city's orders go abroad, read at the exchange
 * rate the city's money stands at and the city's own inflation; the handover of the queue past it; the supply curve
 * their month climbs; and the export of the builders' spare points - held on the calm city of ConstructionTrapCheck
 * (its landlords' Houses queued at 0.8 of the knee), whose roads are stood one at a time until the model's own bound is
 * crossed.
 *
 * WHY. Jerus, 2026-11-22 (docs/queue/FC2.md): "make it a imported/exported good". FC1's contractor came only under a
 * floor of the month, at a price that read no exchange rate; FC2 has him always on offer at p (1 + r), p the city's own
 * base price for a point and r = E x / p, x FOREIGN_EXCESS of the world's base price for the same work and E the rate
 * the goods are struck at, capped where the threshold is a year past the knee. That is a price that must answer the
 * currency and not the city's inflation, and a threshold that must stand exactly where the premium reaches it. The model
 * is BuildersShort (the arithmetic, THE FOREIGN CREWS banner), BuildingManager (foreignTerms() and what reads it), Game
 * (foreignCrewsOffer(), settleForeignCrews()) and sectors.Construction (his book, his bill, the sales abroad).
 * ConstructionTrapCheck holds the trap, the price's shape and the money of FC1 and FC2's first cut; this holds FC2's own
 * bounds, each caused through the model's inputs - the rate, the queue, the level, the roads - on both sides of its edge.
 *
 * What this has to prove:
 *   1. A STRONG CURRENCY, UNDER THE KNEE: at the strongest D$ the threshold is the knee and not a thousandth of a month
 *      over it; with the queue under the knee no order goes abroad, at every road walked an order goes abroad exactly when
 *      the months queued pass the threshold, never within the knee, and the first road past it sends it abroad.
 *   2. TWO RATES PAST THE KNEE: the rate at which the first share is 5% and then 10% gives that share (FOREIGN_EXCESS E x
 *      over p, independently computed) and the threshold BuildersShort.thresholdMonths() of it; setting the rate leaves the
 *      queue under the new threshold; the road before the crossing is not abroad with its premium within the share and the
 *      crossing road is abroad with its premium past it; the second crossing is at more months than the first.
 *   3. THE WEAKEST RATE: the first share is the cap, the threshold exactly SURE_MONTHS past the knee; never abroad within
 *      it, abroad at the first road past it.
 *   4. DOMESTIC INFLATION: the city's own level doubled (as the month re-strikes it) moves neither the world's price of a
 *      point (E x) nor x; p rises, so r falls and the threshold falls - never under the knee.
 *   5. THE EXPORT: a month in which the sites took less than the builders' month sells the rest abroad at the price read
 *      before it (the month re-strikes), never more than the sites' month nor starving a site, booked as the builders'
 *      exports the month after in the audit's TRADE scope and the national accounts; the price is something to the knee
 *      and nothing past it, and a month played past it sells nothing.
 *   6. A 240-MONTH BACKLOG: the offer hands him exactly the overflow read before it and leaves the queue at home at the
 *      threshold's months of the sites; his month is far over the sites'; half the backlog is a smaller month at a smaller
 *      excess; the backlog read at both paces stays within a month under the threshold, which stays within SURE_MONTHS of
 *      the knee; and his crews take no worker from the city (a twin with the price switched off for its first month).
 *   7. THE BUILDING THE THRESHOLD FALLS IN (REV-FC2, the second review): a Coal Power Plant, a year of the sites' month
 *      many times over, ordered with the queue under the threshold is split at it - its points past the threshold abroad,
 *      point for point, priced at the premium for its part at home and the crews' share for the rest, and the queue at home
 *      left at the threshold, not years past it; a month on, the queue at home is under it and the next order is built at
 *      home. Each site claims its own work on his book, and his month falls on every claim alike; a cancel takes the
 *      cancelled site's own off it: nothing for a Middle School quoted at home, the plant's own and not the Houses' ordered
 *      after it; the claims survive a save, and a save without them shares his book by the sites' points owed. A site that
 *      finished claims none of his book when Houses quoted at home go on it later, in the city and its loaded twin alike,
 *      and a cancel after it takes the same share in both (the third review). A works on foothill, split, hands him its
 *      pad's preparation in its building's share, and the queue counts the preparation owed on its site.
 *   8. A CURRENCY REFORM: a reform dividing the money by 1,000 divides his book's price a point by 1,000, and the offer's,
 *      and his next bill is his points at it - not a thousandfold abroad at the old unit's price.
 *   9. THE AUDIT: every month played here balanced to the cent and left nothing moving after it.
 *
 * Every fixture causes its condition.
 */
public class ForeignCrewsCheck {

    /** The most single roads a walk adds after its jump: the jump stops JUMP_SHORT roads under the limit, so a handful reach it. */
    static final int MOST_ROADS = 100;

    /** How many roads under the model's own figure a walk's jump stops, so the roads either side of the limit are walked one at a time. */
    static final int JUMP_SHORT = 3;

    /** The most months the export city is played for the queue at home to fall under one month of the sites, and again for a month that sells. */
    static final int MOST_MONTHS = 24;

    /** The first shares section 2 sets the rate for: both under the cap, so the cap does not bind. */
    static final double[] SHARES = { 0.05, 0.10 };

    /** The share section 4 sets the rate for before the city's level doubles: under the cap, so r can fall. */
    static final double INFLATION_SHARE = 0.10;

    /** The queue, in months of the sites, of the long backlog (twenty years) and of the half of it. */
    static final double LONG_MONTHS = 240, HALF_MONTHS_QUEUED = 120;

    /** How many sites' months the contractor's first month must be over, for a backlog of twenty years: far over. */
    static final double FAR_OVER = 5;

    /** The months played in the long backlog, to read the pace each one. */
    static final int PLAYED = 6;

    /**
     * What each backlog fixture's order was quoted, struck just before it was placed (REV-FC2's P1): the foreign crews'
     * share for its work past the threshold without the order (what FC2 quoted until the mend) and with it (the month the
     * order makes him run, Game.foreignTermsOfOrder()), and the base a point the quote read with the order on site.
     */
    static final java.util.Map<String, double[]> QUOTED = new java.util.HashMap<>();

    /** Section 7's buildings: one quoted at home, and one far bigger than the threshold's months of the sites, which it splits. */
    static final String SCHOOL = "Middle School", PLANT = "Coal Power Plant";

    /** ...and the Houses ordered after the plant, abroad whole: work of another site's on his book beside it. */
    static final long HOUSES = 200;

    /** ...and the works ordered on foothill ground at the section's end, split at the threshold with its pad's preparation. */
    static final String WORKS = "Water Treatment Plant";

    /** The months the section plays at most for the Houses' site to finish with the plant's work on his book. */
    static final int MOST_MONTHS_TO_FINISH = 120;

    /** Section 8's reform: the money divided by a thousand, the reviewer's case. */
    static final double REFORM = 1000;

    /** The months queued within which, under the threshold, the backlog read at both paces must stay: one month. */
    static final double ONE_MONTH = 1;

    /** The points within which two figures of one month are the same: the sites take whole points of a month struck in whole ones. */
    static final double A_POINT = 1;

    /** The assertions made here (those ConstructionTrapCheck's helpers count are in its own total). */
    static int checked = 0;

    static void ok(String label, boolean cond) { checked++; assertTrue(label, cond); }

    static void near(String label, double actual, double expected, double tol) { checked++; close(label, actual, expected, tol); }

    static void exact(String label, double actual, double expected) { checked++; bits(label, actual, expected); }

    /** Two figures within `tol` of each other, absolutely: the points of a month, which are whole. */
    static void within(String label, double actual, double expected, double tol) {
        checked++;
        boolean good = Math.abs(actual - expected) <= tol;
        if (!good) ConstructionTrapCheck.fails++;
        out.printf("%-100s %s  %,.6f against %,.6f (within %,.3f)%n", label, good ? "OK" : "FAIL", actual, expected, tol);
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        ConstructionTrapCheck.out = System.out;
        ConstructionTrapCheck.quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        // DeathCareFirstCheck's fixture builds the city, quietly, through its own streams.
        DeathCareFirstCheck.out = ConstructionTrapCheck.out;
        DeathCareFirstCheck.quiet = ConstructionTrapCheck.quiet;
        Path root = TempDirs.create("foreigncrewscheck");

        // Sections 1 to 4 read the one city, no month played, so the rate set is the rate read; it is let go before the next is built.
        Game[] held = { calm(root, "rates") };
        strongCurrency(held[0]);
        twoRates(held[0]);
        weakestRate(held[0]);
        domesticInflation(held[0]);
        held[0] = null;
        theExport(root);
        Game reformed = theBacklog(root);
        theSplit(root);
        theReform(reformed);
        reformed = null;
        out.println("\n--- 9. the audit ---");
        audited("in every month here");

        out.printf("%n%d assertions made here%n", checked);
        out.println(ConstructionTrapCheck.fails == 0 ? "\nAll checks passed." : "\n" + ConstructionTrapCheck.fails + " FAILED");
        System.exit(ConstructionTrapCheck.fails == 0 ? 0 : 1);
    }

    /* ============================ THE WALK ============================ */

    /**
     * What a walk of single roads left: the roads added, the months queued at the last road not past the limit and at the
     * first past it (the crossing), whether an order went abroad at each, the threshold at the crossing, whether at every
     * road an order went abroad exactly when the months queued passed the threshold, whether any road within the limit
     * sent one abroad, and what a point sold abroad fetched at the two.
     */
    record Walk(int roads, double before, double at, boolean abroadBefore, boolean abroadAt, double thresholdAt,
                boolean agreed, boolean abroadWithin, double exportBefore, double exportAt) { }

    /**
     * Standing Paved Roads on the city, no month played: JUMP_SHORT roads under the model's own figure for `limit` months
     * queued (ConstructionTrapCheck.roadsFor()), then one at a time until the months queued pass it. Their repairs take points
     * from the sites, so the months rise as the queue stands.
     */
    static Walk walk(Game g, double limit) {
        BuildingManager bm = g.getBuildingManager();
        roads(g, Math.max(0, roadsFor(g, limit) - JUMP_SHORT));
        double before = bm.monthsQueued();
        if (before > limit) throw new IllegalStateException("the jump overshot " + limit + " months: " + before);
        boolean abroadBefore = bm.ordersGoAbroad();
        double exportBefore = bm.exportPricePerPoint();
        boolean agreed = abroadBefore == (before > bm.thresholdMonths());
        boolean abroadWithin = abroadBefore;
        for (int k = 1; k <= MOST_ROADS; k++) {
            roads(g, 1);
            double months = bm.monthsQueued();
            boolean abroad = bm.ordersGoAbroad();
            agreed &= abroad == (months > bm.thresholdMonths());
            if (months > limit) {
                return new Walk(k, before, months, abroadBefore, abroad, bm.thresholdMonths(), agreed, abroadWithin,
                        exportBefore, bm.exportPricePerPoint());
            }
            abroadWithin |= abroad;
            before = months;
            abroadBefore = abroad;
            exportBefore = bm.exportPricePerPoint();
        }
        throw new IllegalStateException(MOST_ROADS + " roads did not pass " + limit + " months: " + bm.monthsQueued() + " (a road takes " + perRoad(g) + " points)");
    }

    static void printWalk(String what, Walk w) {
        out.printf("  %s: %d roads walked; before the crossing %.4f months (premium %.4f, abroad %s), the crossing %.4f months"
                        + " (premium %.4f, abroad %s), the threshold there %.6f%n",
                what, w.roads(), w.before(), BuildersShort.premium(w.before()), w.abroadBefore(), w.at(),
                BuildersShort.premium(w.at()), w.abroadAt(), w.thresholdAt());
    }

    /** The rate E at which the foreign crews' first share is `share`, from the base price now and the founding's: E = r p / (FOREIGN_EXCESS x_founding). */
    static double rateFor(BuildingManager bm, double share) {
        return share * bm.basePricePerPoint() / (BuildersShort.FOREIGN_EXCESS * bm.basePricePerPointAtFounding());
    }

    /* ============================ 1. A STRONG CURRENCY, UNDER THE KNEE ============================ */

    static void strongCurrency(Game g) {
        out.println("--- 1. a strong currency, under the knee: the strongest D$ and the threshold is the knee ---");
        BuildingManager bm = g.getBuildingManager();
        double knee = BuildersShort.KNEE_MONTHS;
        g.getEconomyManager().setExchangeRate(ForeignAccounts.MIN_RATE);
        exact("the builders hold the strongest rate set (ForeignAccounts.MIN_RATE)", bm.getExchangeRate(), ForeignAccounts.MIN_RATE);
        double threshold = bm.thresholdMonths();
        out.printf("  first share %.3g; threshold %.9f months (the knee %.1f); queue %.3f months%n",
                bm.foreignExcessShare(), threshold, knee, bm.monthsQueued());
        ok("the threshold is the knee and not a thousandth of a month over it", threshold >= knee && threshold < knee + 1e-3);
        ok("fixture: the queue starts under the knee", bm.monthsQueued() <= knee);
        ok("...and with it no order goes abroad", !bm.ordersGoAbroad());
        Walk w = walk(g, knee);
        printWalk("roads to the knee", w);
        ok("an order goes abroad exactly when the months queued pass the threshold, at every road walked", w.agreed());
        ok("...and never while the months queued are at or under the knee", !w.abroadWithin());
        ok("the road before the knee: not abroad", w.before() <= knee && !w.abroadBefore());
        ok("fixture: the first road past the knee lands past the threshold, the width of which is a thousandth of a month", w.at() > knee && w.at() > w.thresholdAt());
        ok("the first road past the knee: abroad", w.abroadAt());
    }

    /* ============================ 2. TWO RATES PAST THE KNEE ============================ */

    static void twoRates(Game g) {
        out.println("\n--- 2. two rates past the knee: the threshold is where the premium reaches the first share ---");
        BuildingManager bm = g.getBuildingManager();
        double cap = BuildersShort.capShare(), previousCrossing = BuildersShort.KNEE_MONTHS;
        for (double target : SHARES) {
            String at = " (first share " + target + ")";
            ok("fixture: the share is under the cap, so the cap does not bind" + at, target < cap);
            double rate = rateFor(bm, target);
            g.getEconomyManager().setExchangeRate(rate);
            exact("the builders hold the rate set" + at, bm.getExchangeRate(), rate);
            double independent = BuildersShort.FOREIGN_EXCESS * rate * bm.basePricePerPointAtFounding() / bm.basePricePerPoint();
            near("the first share is FOREIGN_EXCESS E x_founding over p, computed here" + at, bm.foreignExcessShare(), independent, 1e-12);
            near("...and the share the rate was set for" + at, bm.foreignExcessShare(), target, 1e-12);
            double threshold = BuildersShort.thresholdMonths(target);
            near("the threshold is BuildersShort.thresholdMonths() of the share" + at, bm.thresholdMonths(), threshold, 1e-12);
            near("...KNEE + HALF_MONTHS sqrt(r / (CEILING - r))" + at, bm.thresholdMonths(), BuildersShort.KNEE_MONTHS
                    + BuildersShort.HALF_MONTHS * Math.sqrt(target / (BuildersShort.CEILING - target)), 1e-12);
            out.printf("  rate %.6g: first share %.6f, threshold %.4f months; the queue now %.4f months%n",
                    rate, bm.foreignExcessShare(), bm.thresholdMonths(), bm.monthsQueued());
            ok("setting the rate left the queue under the new threshold: no order goes abroad" + at, bm.monthsQueued() <= threshold && !bm.ordersGoAbroad());
            Walk w = walk(g, threshold);
            printWalk("roads to the threshold", w);
            ok("an order goes abroad exactly when the months queued pass the threshold, at every road" + at, w.agreed());
            ok("...and never while they are within it" + at, !w.abroadWithin());
            ok("the road before the crossing: not abroad, its premium within the first share" + at, !w.abroadBefore() && BuildersShort.premium(w.before()) <= target);
            ok("the crossing road: abroad, its premium past the first share" + at, w.abroadAt() && BuildersShort.premium(w.at()) > target);
            ok("the crossing is at more months than the rate before it had" + at, w.at() > previousCrossing);
            previousCrossing = w.at();
        }
    }

    /* ============================ 3. THE WEAKEST RATE ============================ */

    static void weakestRate(Game g) {
        out.println("\n--- 3. the weakest rate: the first share is the cap, the threshold SURE_MONTHS past the knee ---");
        BuildingManager bm = g.getBuildingManager();
        double reach = BuildersShort.KNEE_MONTHS + BuildersShort.SURE_MONTHS;
        g.getEconomyManager().setExchangeRate(ForeignAccounts.MAX_RATE);
        exact("the builders hold the weakest rate set (ForeignAccounts.MAX_RATE)", bm.getExchangeRate(), ForeignAccounts.MAX_RATE);
        exact("the first share is the cap", bm.foreignExcessShare(), BuildersShort.capShare());
        near("the threshold is the knee and SURE_MONTHS", bm.thresholdMonths(), reach, 1e-9);
        out.printf("  first share %.4f; threshold %.9f months; the queue now %.4f months%n", bm.foreignExcessShare(), bm.thresholdMonths(), bm.monthsQueued());
        ok("fixture: the queue is within the threshold, so no order goes abroad yet", bm.monthsQueued() <= reach && !bm.ordersGoAbroad());
        Walk w = walk(g, reach);
        printWalk("roads to the threshold", w);
        ok("an order goes abroad exactly when the months queued pass the threshold, at every road", w.agreed());
        ok("never abroad at or under KNEE + SURE_MONTHS", !w.abroadWithin() && !w.abroadBefore());
        ok("fixture: the crossing lands past the threshold", w.at() > w.thresholdAt());
        ok("abroad at the first road past it", w.abroadAt());
    }

    /* ============================ 4. DOMESTIC INFLATION ============================ */

    static void domesticInflation(Game g) {
        out.println("\n--- 4. domestic inflation: the city's own level doubled moves p and not the world's price of a point ---");
        BuildingManager bm = g.getBuildingManager();
        g.getEconomyManager().setExchangeRate(rateFor(bm, INFLATION_SHARE));
        double p0 = bm.basePricePerPoint(), r0 = bm.foreignExcessShare(), t0 = bm.thresholdMonths();
        double x0 = BuildersShort.FOREIGN_EXCESS * bm.worldPricePerPoint() / bm.getExchangeRate();
        double ex0 = BuildersShort.FOREIGN_EXCESS * bm.worldPricePerPoint();
        near("fixture: the share is the one set, under the cap", r0, INFLATION_SHARE, 1e-12);
        double level = bm.getExpectedLevel(), unit = g.getDenomination().getUnit();
        // As Game.restrikeMoneyConstants() strikes the month: the templates at the unit over the level, and the builders told it.
        bm.seedConstants(unit / (2 * level));
        bm.setExpectedLevel(2 * level);
        double p1 = bm.basePricePerPoint(), r1 = bm.foreignExcessShare(), t1 = bm.thresholdMonths();
        double x1 = BuildersShort.FOREIGN_EXCESS * bm.worldPricePerPoint() / bm.getExchangeRate();
        double ex1 = BuildersShort.FOREIGN_EXCESS * bm.worldPricePerPoint();
        out.printf("  level %.4f to %.4f: p %,.6f to %,.6f; x %,.6f to %,.6f; E x %,.6f to %,.6f; first share %.5f to %.5f; threshold %.4f to %.4f%n",
                level, bm.getExpectedLevel(), p0, p1, x0, x1, ex0, ex1, r0, r1, t0, t1);
        exact("the builders hold the level struck", bm.getExpectedLevel(), 2 * level);
        near("x, the world's excess a point at the founding's price, is unchanged", x1, x0, 1e-12);
        near("...and so is E x", ex1, ex0, 1e-12);
        ok("p, the city's own price for a point, rose", p1 > p0);
        ok("the first share fell", r1 < r0);
        near("...to E x over the new p", r1, ex1 / p1, 1e-12);
        ok("the threshold fell", t1 < t0);
        ok("...and not under the knee", t1 >= BuildersShort.KNEE_MONTHS);
        // The level back as the month had it (the city is let go after this, but a harness leaves what it read as it found it).
        bm.seedConstants(unit / level);
        bm.setExpectedLevel(level);
    }

    /* ============================ 5. THE EXPORT ============================ */

    static void theExport(Path root) {
        out.println("\n--- 5. the export: a month the sites took less than the builders' month sells the rest abroad, within the knee ---");
        Game g = calm(root, "export");
        BuildingManager bm = g.getBuildingManager();
        Construction c = g.getSectors().construction();
        int played = 0;
        while (bm.monthsQueued() >= 1 && played < MOST_MONTHS) {
            month(g);
            played++;
        }
        out.printf("  %d months played: the queue at home %,.0f points = %.3f months of the sites%n", played, bm.queueAtHome(), bm.monthsQueued());
        if (!(bm.monthsQueued() < 1)) throw new IllegalStateException("the queue at home never fell under a month of the sites in " + MOST_MONTHS + " months");
        double sites = 0, remaining = 0, price = 0;
        boolean sold = false;
        for (int k = 0; k < MOST_MONTHS && !sold; k++) {
            sites = g.getBuildingOutput();
            remaining = bm.getRemainingConstructionPoints();
            price = bm.exportPricePerPoint();
            month(g);
            sold = c.getExportPointsThisMonth() > 0;
        }
        ok("a month sold points abroad once the queue at home was under a month of the sites", sold);
        if (sold) {
            double points = c.getExportPointsThisMonth(), receipts = c.getExportReceiptsThisMonth();
            double built = bm.getPointsBuilt();
            out.printf("  the month before: the sites' month %,.0f, %,.1f points left to build, a point sold fetches %,.6f%n", sites, remaining, price);
            out.printf("  the month: %,.1f points sold for %,.4f; the sites took %,.1f; the foreign crews built %,.1f; the sites' month after it %,d%n",
                    points, receipts, built, c.getForeignPointsThisMonth(), g.getBuildingOutput());
            ok("fixture: a point sold fetched something before the month", price > 0);
            ok("the points sold are some, and never more than the sites' month", points > 0 && points <= sites);
            near("the receipts are the points at the price read before the month (the month re-strikes it)", receipts, points * price, 5e-3);
            exact("no foreign crews built in a month the queue was at home and within the knee", c.getForeignPointsThisMonth(), 0);
            // The month's own site output is the one struck the month before (the crews are struck after the sites advance, SimulationEngine), read before it.
            within("no site was starved: the points the sites took and the points sold are the city's own month at the sites", built + points, sites, A_POINT);
            month(g);
            MoneyAudit.Result r = g.getLastMoneyAudit();
            double stated = c.statement().exports, line = r == null ? 0 : r.figure("+ " + BUILDERS + " Exports");
            out.printf("  the month after: the builders' statement carries %,.4f of exports; the audit's line %,.4f; the trade in %,.4f; the national accounts' exports %,.4f; the foreign accounts' %,.4f%n",
                    stated, line, r == null ? 0 : r.tradeIn, g.getEconomyManager().getNationalAccounts().getExports(), g.getForeignAccounts().getExports());
            ok("the builders' statement the month after carries what they fetched, at least", stated >= receipts * (1 - 1e-9));
            near("...and nothing else, exactly", stated, receipts, 1e-9);
            within("the audit's exports line for the builders is the statement's", line, stated, MoneyAudit.CENT);
            ok("...in the audit's TRADE scope: the trade in is at least that line", r != null && r.tradeIn >= line - MoneyAudit.CENT);
            ok("...and in the national accounts' exports", g.getEconomyManager().getNationalAccounts().getExports() >= line - MoneyAudit.CENT);
            ok("...and the foreign accounts'", g.getForeignAccounts().getExports() >= line - MoneyAudit.CENT);
            // ...and the Trade tab's reading of it (Sectors.TradeByGood.servicesSold()): an export with no good behind it, on the
            // builders' row, so what the tab says was sold abroad is still the balance of payments' exports (ForeignCheck's footing,
            // which runs with the foreign crews off: its note says why).
            Sectors.TradeByGood t = g.getTradeByGood();
            out.printf("  the Trade tab: the builders sold abroad %,.4f with no good behind it; sold abroad in all %,.4f, the foreign accounts' exports %,.4f%n",
                    t.servicesSold().getOrDefault(BUILDERS, 0.0), t.sold(), g.getForeignAccounts().getExports());
            near("on the Trade tab the builders sold abroad what their statement carries, an export with no good behind it",
                    t.servicesSold().getOrDefault(BUILDERS, 0.0), stated, 1e-9);
            near("...and what the tab says was sold abroad is the balance of payments' exports, theirs among them",
                    t.sold(), g.getForeignAccounts().getExports(), 1e-9);
        }

        Game kneeCity = calm(root, "knee");
        BuildingManager kb = kneeCity.getBuildingManager();
        double knee = BuildersShort.KNEE_MONTHS;
        Walk w = walk(kneeCity, knee);
        printWalk("roads to the knee", w);
        out.printf("  a point sold fetches %,.6f at %.4f months and %,.6f at %.4f%n", w.exportBefore(), w.before(), w.exportAt(), w.at());
        ok("a point the builders sell abroad fetches something while the months queued are at or under the knee", w.before() <= knee && w.exportBefore() > 0);
        ok("...and nothing at the first road past it", w.at() > knee && w.exportAt() == 0);
        // Well past the knee, so the month's own top reads the queue past it whatever the month re-strikes: a month sells nothing.
        roads(kneeCity, roadsFor(kneeCity, knee + 2));
        ok("fixture: the queue is well past the knee", kb.monthsQueued() > knee + 1);
        exact("...and a point sold fetches nothing there", kb.exportPricePerPoint(), 0);
        month(kneeCity);
        Construction ck = kneeCity.getSectors().construction();
        out.printf("  a month played at %.3f months queued: %,.1f points sold for %,.4f%n", kb.monthsQueued(), ck.getExportPointsThisMonth(), ck.getExportReceiptsThisMonth());
        exact("a month played past the knee sold no points", ck.getExportPointsThisMonth(), 0);
        exact("...and took nothing for them", ck.getExportReceiptsThisMonth(), 0);
    }

    /* ============================ 6. A 240-MONTH BACKLOG ============================ */

    /**
     * A calm city with the landlords' Houses ordered on top of its own, enough that the queue at home is `months` of the
     * sites (ConstructionTrapCheck.calm()'s own way of ordering them: the landlords given twice the quoted price, land granted
     * where the city's ground runs short).
     */
    static Game backlog(Path root, String name, double months) {
        Game g = calm(root, name);
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate house = template(g, HOUSE);
        int n = (int) Math.ceil((months * g.getBuildingOutputAtEveryPost() - bm.queueAtHome()) / house.getConstructionPoints());
        if (n < 1) throw new IllegalStateException("the city already queues " + bm.monthsQueued() + " months");
        double land = n * house.getLandSqFt();
        if (g.getLandManager().getAvailableSqFt() < land) g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + land);
        boolean[] ordered = new boolean[1];
        quietly(() -> {
            BuildingManager.ForeignTerms without = bm.foreignTerms();
            Game.BuildQuote quote = g.quoteBuild(house, n);
            QUOTED.put(name, new double[] { without == null ? Double.NaN : without.share(), quote.abroadShare,
                    quote.abroadPricePerPoint, quote.abroadPoints, bm.nonMaterialCost(house) / house.getConstructionPoints(),
                    g.getSectors().construction().getForeignBook() });
            double price = quote.total;
            g.getEconomyManager().setSectorCash(RE, g.getEconomyManager().getSectorCash(RE) + 2 * price);
            ordered[0] = g.buildFor(g.getSectorInvestor(RE), house, n);
        });
        if (!ordered[0]) throw new IllegalStateException("the landlords' " + n + " Houses could not be ordered");
        return g;
    }

    static Game theBacklog(Path root) {
        out.println("\n--- 6. a 240-month backlog: the order hands him its buildings past the threshold as it is billed, his month and excess rise with it, the city's workers are not taken ---");
        Game a = backlog(root, "backlog-a", LONG_MONTHS);
        Game b = backlog(root, "backlog-b", HALF_MONTHS_QUEUED);
        Game off = backlog(root, "backlog-off", LONG_MONTHS);
        BuildingManager bmA = a.getBuildingManager(), bmB = b.getBuildingManager(), bmOff = off.getBuildingManager();
        Construction ca = a.getSectors().construction(), cb = b.getSectors().construction(), cOff = off.getSectors().construction();
        double reach = BuildersShort.KNEE_MONTHS + BuildersShort.SURE_MONTHS;
        double sitesA = a.getBuildingOutputAtEveryPost(), sitesB = b.getBuildingOutputAtEveryPost();
        double orderedA = (bmA.queueAtHome() + ca.getForeignBook()) / sitesA, orderedB = (bmB.queueAtHome() + cb.getForeignBook()) / sitesB;
        double orderedOff = (bmOff.queueAtHome() + cOff.getForeignBook()) / off.getBuildingOutputAtEveryPost();
        out.printf("  queued at home and abroad: a %.2f months of the sites, b %.2f, the twin %.2f; at home a %.2f, b %.2f; the sites' month a %,.0f, b %,.0f; the threshold a %.4f, b %.4f%n",
                orderedA, orderedB, orderedOff, bmA.monthsQueued(), bmB.monthsQueued(), sitesA, sitesB, bmA.thresholdMonths(), bmB.thresholdMonths());
        ok("fixture: the long order is twenty years of the sites' month, at home and abroad, in a and in its twin", orderedA >= LONG_MONTHS && orderedOff >= LONG_MONTHS);
        ok("fixture: ...and half of it in b", orderedB >= HALF_MONTHS_QUEUED && orderedB < LONG_MONTHS);
        ok("fixture: a and its twin are the same city", bmA.queueAtHome() == bmOff.queueAtHome() && ca.getForeignBook() == cOff.getForeignBook()
                && sitesA == off.getBuildingOutputAtEveryPost());
        // REV-FC2, the second review: the queue at home is left at the threshold, not past it (the House it falls in split).
        ok("fixture: both are far past the threshold, at home and abroad", orderedA > bmA.thresholdMonths() + ONE_MONTH
                && orderedB > bmB.thresholdMonths() + ONE_MONTH);

        // REV-FC2's P1: the order's buildings past the threshold went to him as it was billed, at what its quote charged for them.
        double[] q = QUOTED.get("backlog-a"), qb = QUOTED.get("backlog-b");
        double thresholdA = bmA.thresholdMonths(), firstA = bmA.foreignExcessShare();
        out.printf("  a's order: %,.0f points past the threshold quoted at +%.4f (+%.4f his share without it), %,.6f a point (the House's base %,.6f); his book %,.0f at +%.4f, %,.6f%n",
                q[3], q[1], q[0], q[2], q[4], ca.getForeignBook(), ca.getForeignBookShare(), ca.getForeignBookPrice());
        ok("fixture: his book was empty before the order", q[5] == 0 && qb[5] == 0);
        exact("a: billing the order handed him its buildings past the threshold: his book is the quote's points abroad", ca.getForeignBook(), q[3]);
        exact("b: ...and likewise", cb.getForeignBook(), qb[3]);
        near("...at the share the quote charged for them", ca.getForeignBookShare(), q[1], 1e-12);
        near("...and the price a point it charged, the House's own base and that share", ca.getForeignBookPrice(), q[2], 1e-12);
        near("...which is the House's base a point and that share", q[2], q[4] * (1 + q[1]), 1e-12);
        // REV-FC2, the second review: the House the threshold falls in is split at it, so the queue at home is the threshold.
        near("...and the queue at home is the threshold's months of the sites, the House it falls in split at it",
                bmA.monthsQueued(), thresholdA, 1e-9);
        double monthA = BuildersShort.foreignMonth(ca.getForeignBook(), thresholdA, bmA.largestDepotPoints());
        near("his excess is the supply curve's at the month the order makes him run", q[1],
                BuildersShort.excessAt(firstA, monthA, a.getConstructionOutputAtEveryPost()), 1e-12);
        ok("...the month the order makes him run, over his share without it", q[1] > q[0]);
        ok("...and over the first share", q[1] > firstA);

        // The offer, before the month: nothing more handed, his month billed at the book's price.
        Game.ForeignOffer offerA = a.foreignCrewsOffer();
        Game.ForeignOffer offerB = b.foreignCrewsOffer();
        out.printf("  a: his month %,d (the sites' %,.0f), his excess %.4f, price %,.6f a point; b: his month %,d, his excess %.4f%n",
                offerA.points(), sitesA, offerA.excessShare(), offerA.pricePerPoint(), offerB.points(), offerB.excessShare());
        exact("the offer counts the order's handover as the month's", ca.getHandedAbroadThisMonth(), q[3]);
        exact("...and hands him nothing more: the work at home is its payers', at the home price they were quoted", ca.getForeignBook(), q[3]);
        near("...his month billed at the book's price a point, the payer's", offerA.pricePerPoint(), q[2], 1e-12);
        ok("a: his month is far over the sites' month", offerA.points() > FAR_OVER * sitesA);
        ok("b's month is smaller than a's", offerB.points() > 0 && offerB.points() < offerA.points());
        ok("...and so is its excess", offerB.excessShare() < offerA.excessShare());

        // Six months in a; the first of the twin's with the foreign crews' price switched off.
        boolean inside = true, thresholdOk = true;
        double lowest = Double.POSITIVE_INFINITY, highest = Double.NEGATIVE_INFINITY, highestThreshold = 0;
        double handedLater = 0, priceDrift = 0, quotedPrice = q[2];
        for (int m = 1; m <= PLAYED; m++) {
            if (m >= 2) {
                handedLater += ca.getHandedAbroadThisMonth();
                priceDrift = Math.max(priceDrift, Math.abs(ca.getForeignBookPrice() - quotedPrice) / quotedPrice);
                double threshold = bmA.thresholdMonths();
                double paced = bmA.getRemainingConstructionPoints() / (a.getBuildingOutputAtEveryPost() + bmA.foreignMonthNow());
                // REV-FC2: within a month of it either side - billing put the work past the threshold then abroad, and the
                // work stays where it was put while the threshold moves with the base.
                inside &= Math.abs(threshold - paced) <= ONE_MONTH;
                thresholdOk &= threshold <= reach;
                lowest = Math.min(lowest, threshold - paced);
                highest = Math.max(highest, threshold - paced);
                highestThreshold = Math.max(highestThreshold, threshold);
            }
            month(a);
            if (m == 1) {
                double built = ca.getForeignPointsThisMonth(), bill = ca.getForeignBillThisMonth();
                double gap = bill - built * q[2], before = built * q[4] * (q[1] - q[0]);
                out.printf("  a's first month: he built %,.0f points, billed %,.4f; the payer's price for them %,.4f; the gap in the builders' cash %,.6f, where FC2 before the mend left %,.4f there%n",
                        built, bill, built * q[2], gap, before);
                ok("fixture: he built in a's first month", built > 0);
                near("the builders paid abroad what the payer was quoted for those points: the quote is the bill", bill, built * q[2], 1e-9);
                ok("...so the builders' cash carries no gap between them, where the quote without the order left them short", Math.abs(gap) <= 1e-9 * bill && before > 0);
                BuildingManager.buildersShortOn = false;
                try {
                    month(off);
                } finally {
                    BuildingManager.buildersShortOn = true;
                }
                afterTheFirstMonth(a, off);
            }
        }
        out.printf("  months 2 to %d in a: the threshold less the backlog read at both paces ran %.4f to %.4f months; the threshold at most %.4f (KNEE + SURE_MONTHS %.1f)%n",
                PLAYED, lowest, highest, highestThreshold, reach);
        ok("before each month from the second the backlog at both paces is within a month of the threshold, either side", inside);
        ok("...and the threshold is within SURE_MONTHS of the knee", thresholdOk);
        // REV-FC2: nothing goes abroad but what an order's quote put there - the queue at home is its payers' at the home price.
        out.printf("  months 2 to %d in a: %,.0f points handed after the order's own %,.0f; the book's price a point %,.6f, %.2e from the order's %,.6f%n",
                PLAYED, handedLater, q[3], ca.getForeignBookPrice(), priceDrift, quotedPrice);
        ok("after the order's own handover nothing more was handed him: the work at home stays its payers'", handedLater == 0);
        ok("...so each point he built for it was billed the order's price a point", priceDrift <= 1e-15);

        // ...and the book's price survives a save (REV-FC2's new saved state, Construction's extras), in a's own scratch folder.
        double book = ca.getForeignBook(), share = ca.getForeignBookShare(), price = ca.getForeignBookPrice();
        Game back = new Game(new GameFiles(root.resolve("backlog-a"), root.resolve("backlog-a-no-legacy")));
        quietly(() -> {
            a.saveGame(3, "the backlog");
            back.run();
            back.loadGameSave(3);
        });
        Construction cBack = back.getSectors().construction();
        out.printf("  saved and loaded: the book %,.3f at +%.9f, %,.9f a point; back %,.3f at +%.9f, %,.9f%n",
                book, share, price, cBack.getForeignBook(), cBack.getForeignBookShare(), cBack.getForeignBookPrice());
        exact("the book loads as it was saved", cBack.getForeignBook(), book);
        exact("...and the share its points were handed at", cBack.getForeignBookShare(), share);
        exact("...and their price a point, so the load bills them what their payers were quoted", cBack.getForeignBookPrice(), price);
        ok("...a book that is priced", !cBack.foreignBookUnpriced() && price > 0);
        return a;
    }

    /* ============================ 7. THE BUILDING THE THRESHOLD FALLS IN ============================ */

    /** The refund the month's end made for t's site, or null. */
    static ConstructionControl.Refund refundOf(Game g, BuildingsTemplate t) {
        for (ConstructionControl.Refund r : g.getControlThisMonth().refunds) if (r.templateId() == t.getId()) return r;
        return null;
    }

    /** n of t ordered by the city, quietly, its cash and ground given first; false if the order was not placed. */
    static boolean cityOrders(Game g, BuildingsTemplate t, long n) {
        Game.BuildQuote q = g.quoteBuild(t, n);
        g.setCashForTest(g.getCash() + 2 * q.total);
        double land = n * t.getLandSqFt();
        if (g.getLandManager().getAvailableSqFt() < land) g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 2 * land);
        Game.BuildResult[] r = new Game.BuildResult[1];
        quietly(() -> r[0] = g.buildStack(t, n, false));
        if (r[0] != Game.BuildResult.SUCCESS) out.printf("  the order of %d %s: %s%n", n, t.getName(), r[0]);
        return r[0] == Game.BuildResult.SUCCESS;
    }

    static void theSplit(Path root) {
        out.println("\n--- 7. the building the threshold falls in: split at it, its points past it abroad, the queue at home left at the threshold; a cancel takes its site's own work off his book, saved ---");
        Game g = calm(root, "split");
        BuildingManager bm = g.getBuildingManager();
        Construction c = g.getSectors().construction();
        BuildingsTemplate school = template(g, SCHOOL), plant = template(g, PLANT), house = template(g, HOUSE);

        // The school first, quoted at home: the queue at home is the calm city's, under the knee.
        Game.BuildQuote qs = g.quoteBuild(school, 1);
        ok("fixture: a Middle School quoted with the queue under the threshold is quoted at home", qs.abroadPoints == 0
                && bm.monthsQueued() < bm.thresholdMonths());
        ok("fixture: ...and ordered", cityOrders(g, school, 1));
        ok("fixture: his book is empty", c.getForeignBook() == 0);

        // The plant: a year of the sites' month many times over.
        BuildingManager.OrderAbroad oa = g.abroadOfOrder(plant, 1);
        Game.BuildQuote qp = g.quoteBuild(plant, 1);
        double each = Fitout.of(g, plant).points(), sites = g.getBuildingOutputAtEveryPost();
        double from = oa.fromMonths(), step = oa.stepMonths(), threshold = oa.thresholdMonths(), share = oa.share();
        double home = (threshold - from) / step;
        double multiple = bm.orderPriceMultiplier(0, 0, each, 1, plant);
        out.printf("  the plant: %,.0f points, %.2f months of the sites' %,.0f; the queue at home %.4f months, the threshold %.4f; %,.1f points past it quoted abroad at +%.4f, its parts' %,.0f points; the multiple %.6f%n",
                each, step, sites, from, threshold, qp.abroadPoints, share, qp.prepPoints + qp.worksPoints + qp.bridgePoints + qp.deepWaterPoints, multiple);
        out.printf("  FC2's whole buildings kept it at home: %.2f months queued at home after it, every order after it abroad until its builders had worked %.2f months of it down%n",
                from + step, from + step - threshold);
        ok("fixture: the queue at home is under the threshold and the plant's points run past it by more than a month of the sites",
                from < threshold && from + step > threshold + ONE_MONTH);
        ok("the plant is split: one building with work abroad", oa.buildings() == 1);
        near("...its points past the threshold abroad, point for point", oa.points(), each * (1 - home), 1e-12);
        near("...priced at the premium where it begins for its part at home and the crews' share for the rest, independently computed",
                multiple, 1 + home * BuildersShort.premium(from) + (1 - home) * share, 1e-12);
        ok("...the part at home dearer a point than none and cheaper than the crews'", BuildersShort.premium(from) < share);

        ok("fixture: the plant is ordered", cityOrders(g, plant, 1));
        BuildingsStacks ss = bm.getStack(school), sh = bm.getStack(house);
        exact("billing handed him the quote's points abroad", c.getForeignBook(), qp.abroadPoints);
        near("...and the queue at home is the threshold's months of the sites, and its parts' share at home", bm.monthsQueued(),
                bm.thresholdMonths() + (qp.prepPoints + qp.worksPoints + qp.bridgePoints + qp.deepWaterPoints) * home / sites, 1e-9);

        // Houses after it, with the queue at home at the threshold: abroad whole, on his book beside the plant's work.
        double houseEach = Fitout.of(g, house).points();
        Game.BuildQuote qh = g.quoteBuild(house, HOUSES);
        ok("fixture: the Houses are ordered", cityOrders(g, house, HOUSES));
        double P = qp.abroadPoints, H = qh.abroadPoints;
        near("Houses ordered with the queue at home at the threshold go abroad whole", H, HOUSES * houseEach, 1e-9);
        near("...his book is the plant's work abroad and theirs", c.getForeignBook(), P + H, 1e-12);
        near("the plant's site claims its own work on his book", bm.abroadOwedOf(plant), P, 1e-12);
        near("...the Houses' site theirs", bm.abroadOwedOf(house), H, 1e-9);
        exact("...and the school's, quoted at home, none", bm.abroadOwedOf(school), 0);
        near("...the sites' work on his book sums to his book", sum(bm.getAbroadOwedById()), c.getForeignBook(), 1e-12);

        // The claims saved, and a save from before them.
        java.util.Map<Integer, Double> owed = bm.getAbroadOwedById();
        Game back = new Game(new GameFiles(root.resolve("split"), root.resolve("split-no-legacy")));
        quietly(() -> {
            g.saveGame(3, "the split");
            back.run();
            back.loadGameSave(3);
        });
        BuildingManager bmBack = back.getBuildingManager();
        java.util.Map<Integer, Double> loaded = bmBack.getAbroadOwedById();
        out.printf("  each site's work on his book by id saved %s, loaded %s%n", owed, loaded);
        boolean same = owed.keySet().equals(loaded.keySet()) && owed.containsKey(plant.getId());
        for (java.util.Map.Entry<Integer, Double> e : owed.entrySet())
            same &= loaded.get(e.getKey()) != null && Math.abs(loaded.get(e.getKey()) - e.getValue()) <= 1e-9 * e.getValue();
        ok("each site's work on his book loads as it was saved", same);
        bmBack.restoreAbroadOwed(null);
        BuildingsStacks backPlant = bmBack.getStack(template(back, PLANT)), backHouse = bmBack.getStack(template(back, HOUSE));
        out.printf("  read as a save from before it: the plant %,.1f of its %,.1f owed, the Houses %,.1f of their %,.1f%n",
                bmBack.abroadOwedOf(template(back, PLANT)), backPlant.owedPoints(), bmBack.abroadOwedOf(template(back, HOUSE)), backHouse.owedPoints());
        near("...and a save from before it shares his book among the sites by their points owed, as FC1b cut them",
                bmBack.abroadOwedOf(template(back, PLANT)) / bmBack.abroadOwedOf(template(back, HOUSE)), backPlant.owedPoints() / backHouse.owedPoints(), 1e-12);
        near("...the sites' work on his book still his book", sum(bmBack.getAbroadOwedById()), back.getSectors().construction().getForeignBook(), 1e-12);

        // A month on: his month falls on every site's work on his book alike.
        month(g);
        double b1 = c.getForeignBook();
        BuildingManager.OrderAbroad next = g.abroadOfOrder(house, 1);
        out.printf("  a month on: the queue at home %.4f months against the threshold %.4f; a House quoted %,.3f of its %,.3f points abroad; his book %,.1f, the plant's %,.1f, the Houses' %,.1f%n",
                bm.monthsQueued(), bm.thresholdMonths(), next.points(), houseEach, b1, bm.abroadOwedOf(plant), bm.abroadOwedOf(house));
        ok("a month on, the queue at home is under the threshold again", bm.monthsQueued() < bm.thresholdMonths());
        ok("...and the next order is built at home", next.points() < houseEach);
        near("his month fell on the Houses' work on his book in its share", bm.abroadOwedOf(house), H * b1 / (P + H), 1e-9);
        near("...and on the plant's, never more than the points it owes", bm.abroadOwedOf(plant),
                Math.min(bm.getStack(plant).owedPoints(), P * b1 / (P + H)), 1e-9);

        // The cancels, in one month: the school's, quoted at home, and the plant's.
        ok("fixture: the school is still on site", ss.getUnderConstruction() > 0 && ss.owedPoints() > 0);
        double bookBefore = c.getForeignBook();
        ok("fixture: the school's and the plant's sites can be cancelled", g.cancelSite(ConstructionControl.keyOf(school), true)
                && g.cancelSite(ConstructionControl.keyOf(plant), true));
        month(g);
        ConstructionControl.Refund rs = refundOf(g, school), rp = refundOf(g, plant);
        double built = c.getForeignPointsThisMonth(), then = bookBefore - built, after = c.getForeignBook();
        double f = P / (P + H), expected = rp == null ? Double.NaN : Math.min(rp.points(), f * then);
        out.printf("  cancelled: the school %,.1f points left, a share of his book %.6f (FC2 cut the %,.1f); the plant %,.1f left, a share %.6f, its work's share %.6f; his book %,.1f, %,.1f after his month of %,.1f, %,.1f after the cancels%n",
                rs == null ? Double.NaN : rs.points(), rs == null ? Double.NaN : rs.bookShare(), rs == null ? Double.NaN : rs.points(),
                rp == null ? Double.NaN : rp.points(), rp == null ? Double.NaN : rp.bookShare(), f, bookBefore, then, built, after);
        ok("fixture: the month's end stopped both with points left", rs != null && rs.points() > 0 && rp != null && rp.points() > 0);
        exact("a cancel of an order quoted at home takes no share of his book", rs == null ? Double.NaN : rs.bookShare(), 0);
        near("the plant's takes its work's share of his book", rp == null ? Double.NaN : rp.bookShare(), f, 1e-12);
        near("...off his book after his month, never more than its points left", after, then - expected, 1e-9);
        ok("...which leaves the Houses' work on his book whole", after >= (1 - f) * then - 1e-9);

        claimOfAFinishedSite(root, g);
        partsAbroadOwedOnSite(g);
    }

    /**
     * REV-FC2, the third review (P2-1): the Houses' site finishes with the work its orders handed him on his book beside a
     * second plant's; a load reads no claim for a site that owes nothing, and the city must read none either - so Houses
     * ordered on it later, quoted at home, claim none of his book in the city or its loaded twin, and a cancel after it
     * takes the same share of his book in both.
     */
    static void claimOfAFinishedSite(Path root, Game g) {
        BuildingManager bm = g.getBuildingManager();
        Construction c = g.getSectors().construction();
        BuildingsTemplate plant = template(g, PLANT), house = template(g, HOUSE);
        BuildingsStacks sh = bm.getStack(house);
        Game.BuildQuote qp = g.quoteBuild(plant, 1);
        ok("fixture: a second plant, split at the threshold, its work past it on his book beside the Houses'", qp.abroadPoints > 0
                && cityOrders(g, plant, 1) && bm.abroadOwedOf(house) > 0);
        int months = 0;
        while (months < MOST_MONTHS_TO_FINISH && sh.getUnderConstruction() > 0) { month(g); months++; }
        out.printf("  the Houses' site finished %d months on; his book %,.1f, the plant's work on it %,.1f of its %,.1f owed%n",
                months, c.getForeignBook(), bm.abroadOwedOf(plant), bm.getStack(plant).owedPoints());
        ok("fixture: the Houses' site finished with the plant's work still on his book", sh.getUnderConstruction() == 0
                && !(sh.owedPoints() > 0) && bm.abroadOwedOf(plant) > 0);

        Game twin = new Game(new GameFiles(root.resolve("split"), root.resolve("split-no-legacy")));
        quietly(() -> {
            g.saveGame(4, "the finished site");
            twin.run();
            twin.loadGameSave(4);
            // ...its businesses held as the city's are: the hold is the fixture's (calm()), not the save's.
            for (Sector s : twin.getSectors().all()) twin.getBusinessInvestment().holdSector(s.key());
        });
        BuildingManager tm = twin.getBuildingManager();
        Construction tc = twin.getSectors().construction();
        BuildingsTemplate tPlant = template(twin, PLANT), tHouse = template(twin, HOUSE);
        // Houses quoted at home, half the room the queue at home has under the threshold.
        double houseEach = Fitout.of(g, house).points(), sites = g.getBuildingOutputAtEveryPost();
        long n = Math.max(1, (long) Math.floor(0.5 * (bm.thresholdMonths() - bm.monthsQueued()) * sites / houseEach));
        Game.BuildQuote qh = g.quoteBuild(house, n), tqh = twin.quoteBuild(tHouse, n);
        ok("fixture: Houses on the finished site are quoted at home, in the city and in its loaded twin", qh.abroadPoints == 0
                && tqh.abroadPoints == 0);
        ok("fixture: ...and ordered in both", cityOrders(g, house, n) && cityOrders(twin, tHouse, n));
        out.printf("  %d Houses ordered at home on it: their site's work on his book %,.1f, the twin's %,.1f; the plant's %,.1f, the twin's %,.1f%n",
                n, bm.abroadOwedOf(house), tm.abroadOwedOf(tHouse), bm.abroadOwedOf(plant), tm.abroadOwedOf(tPlant));
        exact("an order quoted at home on a site that finished claims none of his book", bm.abroadOwedOf(house), 0);
        exact("...as its loaded twin reads it", tm.abroadOwedOf(tHouse), 0);
        near("...and the plant's work on his book is the twin's", bm.abroadOwedOf(plant), tm.abroadOwedOf(tPlant), 1e-9);

        ok("fixture: the Houses' and the plant's sites can be cancelled, in both", g.cancelSite(ConstructionControl.keyOf(house), true)
                && g.cancelSite(ConstructionControl.keyOf(plant), true) && twin.cancelSite(ConstructionControl.keyOf(tHouse), true)
                && twin.cancelSite(ConstructionControl.keyOf(tPlant), true));
        month(g);
        month(twin);
        ConstructionControl.Refund rh = refundOf(g, house), rp = refundOf(g, plant), trh = refundOf(twin, tHouse), trp = refundOf(twin, tPlant);
        out.printf("  cancelled: the Houses' share of his book %.6f, the twin's %.6f; the plant's %.6f, the twin's %.6f; his book %,.3f, the twin's %,.3f%n",
                rh == null ? Double.NaN : rh.bookShare(), trh == null ? Double.NaN : trh.bookShare(), rp == null ? Double.NaN : rp.bookShare(),
                trp == null ? Double.NaN : trp.bookShare(), c.getForeignBook(), tc.getForeignBook());
        ok("fixture: the month's end stopped both, in both, with points left", rh != null && rp != null && trh != null && trp != null
                && rh.points() > 0 && trh.points() > 0);
        exact("the Houses' cancel takes no share of his book: their order was quoted at home", rh == null ? Double.NaN : rh.bookShare(), 0);
        exact("...nor in the twin", trh == null ? Double.NaN : trh.bookShare(), 0);
        near("the plant's takes the share the twin's takes", rp == null ? Double.NaN : rp.bookShare(), trp == null ? Double.NaN : trp.bookShare(), 1e-12);
        near("...and his book after the cancels is the twin's", c.getForeignBook(), tc.getForeignBook(), 1e-9);
    }

    /**
     * REV-FC2, the third review (P2-2): a works on foothill ground, split at the threshold, hands him its pad's preparation
     * in the share its building's points are (Game.abroadWork()); the preparation is owed on its site with its building
     * (BuildingsStacks.addPreparation(): progress below nothing), so the queue (getRemainingConstructionPoints()) counts it
     * and the next offer's cut of his book to what the sites owe (Construction.capForeignBook()) has it to stand on.
     */
    static void partsAbroadOwedOnSite(Game g) {
        BuildingManager bm = g.getBuildingManager();
        Construction c = g.getSectors().construction();
        BuildingsTemplate works = template(g, WORKS);
        double dry = g.getCityLand().totalKm2(CityLand.DRY);
        g.getCityLand().setCentreRelief(new double[] { 0, dry, 0, 0, 0 });
        Game.BuildQuote q = g.quoteBuild(works, 1);
        BuildingManager.OrderAbroad oa = g.abroadOfOrder(works, 1);
        double each = Fitout.of(g, works).points(), parts = q.prepPoints + q.worksPoints + q.bridgePoints + q.deepWaterPoints;
        out.printf("  a %s on foothill: %,.0f points and %,.1f of its pad's preparation; %,.1f of its points past the threshold, %,.1f of its work quoted abroad%n",
                WORKS, each, parts, oa == null ? Double.NaN : oa.points(), q.abroadPoints);
        ok("fixture: on foothill ground it carries its pad's preparation, and the threshold splits it", q.prepPoints > 0 && oa != null
                && oa.buildings() == 1 && oa.points() > 0 && oa.points() < each);
        near("...its preparation quoted abroad in the share its building's points are", q.abroadPoints, oa == null ? Double.NaN
                : oa.points() * (1 + parts / each), 1e-12);
        double owedBefore = bm.getRemainingConstructionPoints(), bookBefore = c.getForeignBook();
        ok("fixture: ...and ordered", cityOrders(g, works, 1));
        double owedAfter = bm.getRemainingConstructionPoints(), bookAfter = c.getForeignBook();
        near("the queue counts its preparation with its building: what the sites owe rose by both", owedAfter - owedBefore, each + parts, 1e-9);
        near("...his book took its work quoted abroad, the preparation's share with it", bookAfter - bookBefore, q.abroadPoints, 1e-9);
        ok("...which the sites owe: the offer's cut to what they owe takes none of it", bookAfter <= owedAfter);
        month(g);
        near("a month on, his book is what it held less what he built: the offer cut none of the work its payer paid for abroad",
                c.getForeignBook(), bookAfter - c.getForeignPointsThisMonth(), 1e-9);
    }

    static double sum(java.util.Map<Integer, Double> m) {
        double t = 0;
        for (double v : m.values()) t += v;
        return t;
    }

    /* ============================ 8. A CURRENCY REFORM ============================ */

    static void theReform(Game a) {
        out.println("\n--- 8. a currency reform: his book's price a point is the city's money, divided with it ---");
        Construction c = a.getSectors().construction();
        double book = c.getForeignBook(), price = c.getForeignBookPrice();
        ok("fixture: his book holds points at a price", book > 0 && price > 0);
        boolean[] done = new boolean[1];
        quietly(() -> done[0] = a.reformCurrencyForTest(REFORM));
        ok("fixture: the reform is made", done[0]);
        Game.ForeignOffer offer = a.foreignCrewsOffer();
        out.printf("  his book %,.1f points at %,.9f a point, after the reform %,.12f; the offer's price %,.12f%n",
                book, price, c.getForeignBookPrice(), offer.pricePerPoint());
        near("the book's price a point is divided by the reform", c.getForeignBookPrice(), price / REFORM, 1e-12);
        near("...and the offer bills his month at it", offer.pricePerPoint(), price / REFORM, 1e-12);
        month(a);
        double builtNow = c.getForeignPointsThisMonth(), bill = c.getForeignBillThisMonth();
        out.printf("  the month after: he built %,.1f points and billed %,.6f, at the new unit's price %,.6f%n",
                builtNow, bill, builtNow * price / REFORM);
        ok("fixture: he built in the month after", builtNow > 0);
        near("...and billed them at the book's price in the new unit, not a thousandfold", bill, builtNow * price / REFORM, 1e-9);
    }

    /** The city's workers after a's first month and the twin's, which had no foreign crews. */
    static void afterTheFirstMonth(Game a, Game off) {
        Construction ca = a.getSectors().construction(), coff = off.getSectors().construction();
        double builtA = a.getBuildingManager().getPointsBuilt(), builtOff = off.getBuildingManager().getPointsBuilt();
        out.printf("  the first month: the builders' workers a %,.4f, twin %,.4f; jobs filled a %,d, twin %,d; points built a %,.1f, twin %,.1f; the foreign crews built %,.1f in a, %,.1f in the twin%n",
                ca.getWorkers(), coff.getWorkers(), a.getPopulationManager().getJobsFilled(), off.getPopulationManager().getJobsFilled(),
                builtA, builtOff, ca.getForeignPointsThisMonth(), coff.getForeignPointsThisMonth());
        int differing = 0;
        for (Sector s : a.getSectors().all()) {
            double there = off.getSectors().byKey(s.key()).getWorkers();
            if (Math.abs(s.getWorkers() - there) > 1e-9) {
                differing++;
                out.printf("  workers differ in %s: a %,.4f, twin %,.4f%n", s.key(), s.getWorkers(), there);
            }
        }
        PopulationManager pa = a.getPopulationManager(), po = off.getPopulationManager();
        for (int tier = 0; tier < pa.getJobs().length; tier++) {
            out.printf("  tier %d: posts a %,d, twin %,d; fill a %.9f, twin %.9f%n", tier, pa.getJobs()[tier], po.getJobs()[tier],
                    pa.getJobFillRate()[tier], po.getJobFillRate()[tier]);
        }
        out.printf("  the people: a %,d, twin %,d; the labour force a %,.1f, twin %,.1f; homes on site a %,d, twin %,d%n",
                pa.getPopulation(), po.getPopulation(), pa.getLabourForce(), po.getLabourForce(),
                a.getBuildingManager().getHomesUnderConstruction(), off.getBuildingManager().getHomesUnderConstruction());
        exact("the builders' workers are the same with and without the foreign crews: no city worker is taken", ca.getWorkers(), coff.getWorkers());
        ok("...in every sector, not the builders' alone", differing == 0);
        // Not "the jobs filled are the same": the fill rate is the households' side of the posts, which the month moves
        // through what it built and paid (a's pooled month finished more of the landlords' Houses than the twin's, and its
        // builders paid the foreign crews), by a person a tier. A worker taken would be a post: the posts, tier by tier.
        ok("...and the city offers the same posts in every tier: his crews are his own, and open none",
                java.util.Arrays.equals(pa.getJobs(), po.getJobs()));
        ok("the foreign crews built in a and not in the twin", ca.getForeignPointsThisMonth() > 0 && coff.getForeignPointsThisMonth() == 0);
        ok("a's sites took more points than the twin's", builtA > builtOff);
        within("...by his month", builtA - builtOff, ca.getForeignPointsThisMonth(), A_POINT);
    }
}

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
 *   7. THE AUDIT: every month played here balanced to the cent and left nothing moving after it.
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
        theBacklog(root);
        out.println("\n--- 7. the audit ---");
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
            double price = g.quoteBuild(house, n).total;
            g.getEconomyManager().setSectorCash(RE, g.getEconomyManager().getSectorCash(RE) + 2 * price);
            ordered[0] = g.buildFor(g.getSectorInvestor(RE), house, n);
        });
        if (!ordered[0]) throw new IllegalStateException("the landlords' " + n + " Houses could not be ordered");
        return g;
    }

    static void theBacklog(Path root) {
        out.println("\n--- 6. a 240-month backlog: the offer hands him the overflow, his month and excess rise with it, the city's workers are not taken ---");
        Game a = backlog(root, "backlog-a", LONG_MONTHS);
        Game b = backlog(root, "backlog-b", HALF_MONTHS_QUEUED);
        Game off = backlog(root, "backlog-off", LONG_MONTHS);
        BuildingManager bmA = a.getBuildingManager(), bmB = b.getBuildingManager(), bmOff = off.getBuildingManager();
        Construction ca = a.getSectors().construction(), cb = b.getSectors().construction();
        double reach = BuildersShort.KNEE_MONTHS + BuildersShort.SURE_MONTHS;
        out.printf("  queued at home: a %.2f months, b %.2f, the twin %.2f; the sites' month a %d, b %d; the threshold a %.4f, b %.4f%n",
                bmA.monthsQueued(), bmB.monthsQueued(), bmOff.monthsQueued(), a.getBuildingOutputAtEveryPost(), b.getBuildingOutputAtEveryPost(),
                bmA.thresholdMonths(), bmB.thresholdMonths());
        ok("fixture: the long backlog is twenty years of the sites' month at home in a and in its twin", bmA.monthsQueued() >= LONG_MONTHS && bmOff.monthsQueued() >= LONG_MONTHS);
        ok("fixture: ...and half of it in b", bmB.monthsQueued() >= HALF_MONTHS_QUEUED && bmB.monthsQueued() < LONG_MONTHS);
        ok("fixture: a and its twin are the same city", bmA.queueAtHome() == bmOff.queueAtHome() && a.getBuildingOutputAtEveryPost() == off.getBuildingOutputAtEveryPost());
        ok("fixture: both are far past the threshold", bmA.ordersGoAbroad() && bmB.ordersGoAbroad());

        // The offer, before the month: the overflow handed now.
        double thresholdA = bmA.thresholdMonths(), sitesA = a.getBuildingOutputAtEveryPost();
        double overflowA = bmA.overflowAtHome(), bookBefore = ca.getForeignBook();
        Game.ForeignOffer offerA = a.foreignCrewsOffer();
        exact("a: the book grew by the overflow read just before the offer", ca.getForeignBook(), bookBefore + overflowA);
        near("...and the queue at home is then the threshold's months of the sites", bmA.queueAtHome(), thresholdA * sitesA, 1e-9);
        double overflowB = bmB.overflowAtHome();
        Game.ForeignOffer offerB = b.foreignCrewsOffer();
        exact("b: the book grew by the overflow read just before the offer", cb.getForeignBook(), overflowB);
        double firstA = bmA.foreignExcessShare(), firstB = bmB.foreignExcessShare();
        out.printf("  a: overflow %,.0f points, his month %,d (the sites' %,.0f), his excess %.4f over the first share %.4f, price %,.6f a point%n",
                overflowA, offerA.points(), sitesA, offerA.excessShare(), firstA, offerA.pricePerPoint());
        out.printf("  b: overflow %,.0f points, his month %,d, his excess %.4f over the first share %.4f%n", overflowB, offerB.points(), offerB.excessShare(), firstB);
        ok("a: his month is far over the sites' month", offerA.points() > FAR_OVER * sitesA);
        near("...his excess is the supply curve's at that month", offerA.excessShare(),
                BuildersShort.excessAt(firstA, offerA.points(), a.getConstructionOutputAtEveryPost()), 1e-12);
        ok("...and over the first share", offerA.excessShare() > firstA);
        ok("b's month is smaller than a's", offerB.points() > 0 && offerB.points() < offerA.points());
        ok("...and so is its excess", offerB.excessShare() < offerA.excessShare());

        // Six months in a; the first of the twin's with the foreign crews' price switched off.
        boolean inside = true, thresholdOk = true;
        double lowest = Double.POSITIVE_INFINITY, highest = Double.NEGATIVE_INFINITY, highestThreshold = 0;
        for (int m = 1; m <= PLAYED; m++) {
            if (m >= 2) {
                double threshold = bmA.thresholdMonths();
                double paced = bmA.getRemainingConstructionPoints() / (a.getBuildingOutputAtEveryPost() + bmA.foreignMonthNow());
                inside &= paced >= threshold - ONE_MONTH && paced <= threshold + 1e-9;
                thresholdOk &= threshold <= reach;
                lowest = Math.min(lowest, threshold - paced);
                highest = Math.max(highest, threshold - paced);
                highestThreshold = Math.max(highestThreshold, threshold);
            }
            month(a);
            if (m == 1) {
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
        ok("before each month from the second the backlog at both paces is within a month under the threshold and never over it", inside);
        ok("...and the threshold is within SURE_MONTHS of the knee", thresholdOk);
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

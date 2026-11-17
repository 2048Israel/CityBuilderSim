package ham.citybuildersim;

import ham.citybuildersim.sectors.LuxuryRetail;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

/**
 * What a luxury shop's post sells: its payroll over its margin at BEA's mark (LX1, 2026-11-17).
 *
 * WHY. LS1 (docs/studies/spec-labour-share.md) read Luxury Retail's payroll at 2.2% of its value added where BEA's
 * other retail (4A0) pays 41.7%, and found the gap in the sales per post: a Boutique covered 320 pieces of a $22,000
 * good a month with seven posts, so each post took 47 mean monthly wages of margin a month where BEA's take 2.4. On
 * main after CU2 it read 0.5-1.7% (LX1's notes). No harness read LuxuryRetail before this one, so nothing said what a
 * shop's post sells; LX1 struck the two shops' coverage and posts to BEA's share (LuxuryRetail, WHAT A POST SELLS).
 *
 * WHAT THIS HAS TO PROVE:
 *
 *   1. THE CATALOGUE STRIKES THE SHARE. Each luxury template's payroll at the wage ladder, over the margin a full
 *      shop earns at the margin the planner builds to (the position a queue TARGET_HEADROOM over the counters
 *      strikes), on what a piece lands at, is LuxuryRetail.LABOUR_SHARE within LABOUR_SHARE_FACTOR.
 *
 *   2. A TOWN'S BOUTIQUES BOOK IT. A town given the Boutiques its own queue asks for, at the margin the planner builds
 *      to, staffed and stocked, sells, and a year of its books puts payroll over value added (LS1's share: the
 *      margin less power, water and upkeep) inside the band. Twelve months summed, as LS1's probe sums them: a share
 *      is a ratio of two flows, and one month of one sector is noise.
 *
 *   3. ...AND A BOUTIQUE STILL PAYS. The investor builds against estimatedMonthlyProfit(); a shop whose payroll is
 *      BEA's share of its margin still has the rest, so the town's Boutique is worth building and no city's queue
 *      stands at a door nobody will open.
 *
 * Every fixture CAUSES its condition: the town is given its shops, and Luxury Retail's planner is held so the count is
 * the fixture's, not a planner's.
 */
public class LuxuryRetailCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-96s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-96s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
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
        return null;
    }

    /** The band's floor: BEA's share over LABOUR_SHARE_FACTOR. */
    static double low() { return LuxuryRetail.LABOUR_SHARE / LuxuryRetail.LABOUR_SHARE_FACTOR; }

    /** ...and its top: BEA's share times LABOUR_SHARE_FACTOR. */
    static double high() { return LuxuryRetail.LABOUR_SHARE * LuxuryRetail.LABOUR_SHARE_FACTOR; }

    static boolean inBand(double share) { return share >= low() && share <= high(); }

    /** The fixture town's houses: enough that its workers outnumber its posts, so its wages are the ladder's and not a short town's. */
    static final int TOWN_HOUSES = 200;

    /** The Boutiques' worth of queue the fixture's households are given the fortune to ask for. */
    static final int BOUTIQUES_ASKED = 2;

    /**
     * The margin the planner builds to: it orders counters while the queue at its price stands more than
     * TARGET_HEADROOM over them (LuxuryRetail.plan()), and a queue of (1 + headroom) counters strikes the position
     * (1 + headroom) / (2 + headroom) on the margin's own rule (strikeMargin()).
     */
    static double plannersMargin() {
        double h = BusinessInvestment.TARGET_HEADROOM;
        return LuxuryRetail.MARGIN_FLOOR
                + (LuxuryRetail.MARGIN_CEILING - LuxuryRetail.MARGIN_FLOOR) * (1 + h) / (2 + h);
    }

    /** A template's payroll at the wage ladder, every post filled. */
    static double ladderPayroll(BuildingsTemplate t) {
        double payroll = 0;
        for (JobType job : JobType.values()) payroll += t.getJobs(job) * PayTier.of(job).getMonthlyWage();
        return payroll;
    }

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        Path dir = TempDirs.create("luxuryretailcheck");
        GameFiles files = new GameFiles(dir.resolve("data"), dir.resolve("no-legacy"));
        Game game = new Game(files);
        quietly(game::run);

        LuxuryRetail shops = game.getSectors().luxuryRetail();
        BuildingsTemplate boutique = template(game, "Boutique");
        BuildingsTemplate store = template(game, "Department Store");
        double mStar = plannersMargin();
        out.printf("   BEA's band: %.1f%% to %.1f%% (LABOUR_SHARE %.3f, factor %.1f); the planner's margin %.4fx%n",
                low() * 100, high() * 100, LuxuryRetail.LABOUR_SHARE, LuxuryRetail.LABOUR_SHARE_FACTOR, mStar);

        /* ============== 1. the catalogue strikes the share ============== */
        out.println("--- 1. the catalogue strikes the share: a full shop's payroll over its margin at the planner's margin ---");
        assertTrue("the catalogue has both luxury shops, under Luxury Retail",
                boutique != null && store != null
                        && Sectors.LUXURY_RETAIL.equals(boutique.getSector()) && Sectors.LUXURY_RETAIL.equals(store.getSector()));
        double landed = LuxuryRetail.landedCost(game.getMarkets().get(Good.LUXURIES));
        assertTrue("fixture: a piece lands at a price (" + String.format("$%.4fk", landed) + ")", landed > 0);
        for (BuildingsTemplate t : new BuildingsTemplate[]{ boutique, store }) {
            double payroll = ladderPayroll(t);
            double margin = t.getCoverage() * landed * (mStar - 1);
            double share = payroll / margin;
            long posts = 0;
            for (JobType job : JobType.values()) posts += t.getJobs(job);
            report(t.getName() + ": its payroll at the ladder over a full month's margin is in BEA's band",
                    inBand(share),
                    String.format("%.1f%%: %d posts, $%,.3fk of wages, on %,d pieces at $%,.4fk of margin each",
                            share * 100, posts, payroll, t.getCoverage(), landed * (mStar - 1)));
            out.printf("   %s: a post's month of margin is %.2f of its own wage (BEA's other retail: 2.4 at 41.7%%)%n",
                    t.getName(), (margin / posts) / (payroll / posts));
        }

        /* ============== 2. a town's Boutiques book it ============== */
        out.println("\n--- 2. a town's Boutiques book it: a year of payroll over value added inside BEA's band ---");
        double[] queueAt = new double[1];
        long[] built = new long[1];
        quietly(() -> {
            /*
             * THE FIXTURE BUYS ITS OWN TOWN, as RestaurantsCheck's does: the question is what a shop's post sells,
             * not whether a sixty-house town could afford the shops. Luxury Retail's planner is held, so the shops
             * standing are the fixture's.
             */
            game.getBusinessInvestment().holdSector(Sectors.LUXURY_RETAIL);
            game.getGovernmentInvestor().spend(-4_000_000 * PriceLevel.FACTOR);
            game.getLandManager().setOwnedSqFt(game.getLandManager().getOwnedSqFt() + 20_000_000L);
            LongPlaytest.build(game, "House", TOWN_HOUSES);
            LongPlaytest.build(game, "Convenience Store", 4);
            LongPlaytest.build(game, "Mixed Farm", 3);
            LongPlaytest.build(game, "Coal Power Plant", 1);
            LongPlaytest.build(game, "Water Treatment Plant", 1);
            LongPlaytest.build(game, "Paved Road", 6);
            game.simulateMonths(24);
            /*
             * ...AND A FORTUNE TO SPEND. A town this young has no savings past its cushion, so nobody asks a counter
             * for anything (measured: no buyer at the planner's margin after two years). The households are handed
             * what the wealth term turns into BOUTIQUES_ASKED Boutiques' queue at that margin, by equal weight,
             * and a month to plan on it.
             */
            double price = shops.getLanded() * mStar;
            double asked = BOUTIQUES_ASKED * boutique.getCoverage() * (1 + BusinessInvestment.TARGET_HEADROOM);
            double[] weight = new double[game.getHouseholdBalance().cellCount()];
            java.util.Arrays.fill(weight, 1);
            game.getHouseholdBalance().creditByWeight(asked * price / HouseholdBalance.WEALTH_SPENT_A_MONTH, weight);
            game.simulateMonths(1);
            /*
             * THE SHOPS THE QUEUE ASKS FOR, at the margin the planner builds to: the town's own buyers at that price,
             * over a Boutique's coverage and the headroom, standing at once - the question is what they sell, not
             * how long one waits on site.
             */
            price = shops.getLanded() * mStar;
            queueAt[0] = game.getHouseholdBalance().luxuriesWanted(price);
            built[0] = Math.max(1, (long) Math.ceil(queueAt[0]
                    / (boutique.getCoverage() * (1 + BusinessInvestment.TARGET_HEADROOM))));
            game.buildStack(boutique, built[0], true);
            // ...and two years for the margin charged to come to its target (a sixth of the way a month, in logs).
            game.simulateMonths(24);
        });
        assertTrue("fixture: the town's households would buy at the planner's margin ("
                + String.format("%,.0f pieces a month", queueAt[0]) + ")", queueAt[0] > 0);
        long standing = game.getBuildingManager().countByName("Boutique");
        report("fixture: the Boutiques that queue asks for stand", standing == built[0] && standing >= 1,
                String.format("%d standing, %d built, %,d pieces of counters", standing, built[0], shops.coverage()));

        double revenue = 0, inputs = 0, payroll = 0, utilities = 0, served = 0, posts = 0, workers = 0, wanted = 0,
                margin = 0;
        int months = 12;
        for (int m = 0; m < months; m++) {
            quietly(() -> game.simulateMonths(1));
            SectorBooks.SectorMonth books = game.getSectorBooks().get(shops);
            revenue += books.revenue();
            inputs += books.inputs();
            payroll += books.payroll();
            utilities += books.electricity() + books.water() + books.maintenance();
            served += shops.getServed();
            wanted += shops.getWanted();
            posts += shops.getPostsOffered();
            workers += shops.getWorkers();
            margin += shops.getMargin();
        }
        report("fixture: the shops sold every month of the year, staffed",
                served > 0 && workers > 0 && revenue > inputs,
                String.format("%,.0f pieces served of %,.0f wanted, %.1f of %.1f posts filled a month, at %.3fx",
                        served / months, wanted / months, workers / months, posts / months, margin / months));
        double valueAdded = revenue - inputs - utilities;
        double share = payroll / valueAdded;
        report("a year of the town's Boutiques: payroll over value added (LS1's share) is in BEA's band",
                valueAdded > 0 && inBand(share),
                String.format("%.1f%%: $%,.3fk of payroll on $%,.3fk of margin less $%,.3fk of power, water and upkeep",
                        share * 100, payroll, revenue - inputs, utilities));
        double ladder = ladderPayroll(boutique) * standing;
        out.printf("   the town: %,d people, %.1f%% out of work; the shops paid %.2f of the ladder a filled post%n",
                game.getPopulationManager().getPopulation(), game.getPopulationManager().getUnemploymentRate() * 100,
                (payroll / workers) / (ladder / (posts / months)));
        double perPost = served / Math.max(1e-9, workers);
        out.printf("   a post sold %.2f pieces a month; before LX1 a Boutique's nameplate was 45.7%n", perPost);

        /* ============== 3. a Boutique still pays ============== */
        out.println("\n--- 3. ...and a Boutique still pays: what the investor reads is a profit ---");
        double profit = shops.estimatedMonthlyProfit(boutique, game.getBusinessInvestment());
        double running = game.getBusinessInvestment().runningCostOf(boutique);
        report("the investor's estimate of one more Boutique's month is a profit, after its payroll, power and water",
                profit > 0, String.format("$%,.4fk a month, after $%,.4fk of running costs", profit, running));

        out.println();
        out.println(fails == 0 ? "LuxuryRetailCheck: all passed" : "LuxuryRetailCheck: " + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}

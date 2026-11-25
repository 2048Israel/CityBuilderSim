package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * The growth hold when a full city's treasury cannot buy a whole land offer.
 *
 * WHY: ER1's Rotterdam replay has willing private investors, no free plots,
 * and a builder with no orders. This fixture consumes the free ground and
 * removes the city's purchasing cash, then tests the actual private build
 * path each month. Funding the land buyer must release the same order.
 * The long replay's measurements belong in docs/queue/ER1-notes.md.
 */
public class ER1Check {
    static int fails;
    static PrintStream out;
    /** Suppresses the game's month narration while the fixture runs. */
    static final PrintStream QUIET = new PrintStream(OutputStream.nullOutputStream());

    static void check(String label, boolean ok) {
        out.println(label + (ok ? " OK" : " FAIL"));
        if (!ok) fails++;
    }

    static void quietly(Runnable action) {
        PrintStream was = System.out;
        System.setOut(QUIET);
        try { action.run(); } finally { System.setOut(was); }
    }

    static long posts(BuildingManager buildings) {
        return java.util.Arrays.stream(JobType.values())
                .mapToLong(buildings::getTotalJobsAtEveryPost).sum();
    }

    public static void main(String[] args) throws Exception {
        out = System.out;
        long started = System.nanoTime();
        Game g = new Game(GameFiles.scratch("ER1-land-hold"));
        quietly(g::run);
        g.getDebtManager().setAutopilot(false);
        LandManager land = g.getLandManager();
        BuildingManager buildings = g.getBuildingManager();
        BuildingsTemplate shop = buildings.getTemplateByName("Convenience Store");
        Investor buyer = g.getSectorInvestor(shop.getSector());
        land.allocate(land.getAvailableSqFt());
        g.setCashForTest(0);
        quietly(() -> g.simulateMonths(1));
        double funded = 2 * (g.quoteBuild(shop, 1).total
                + land.priceFor(shop.getLandSqFt(), shop.getSector(), LandManager.Use.BUILDING));
        g.getEconomyManager().setSectorCash(shop.getSector(), funded);

        out.println("--- a full city holds jobs while its land buyer has no cash ---");
        check("premise: the shop is known and its private buyer can pay its whole quote",
                buildings.isTemplateKnown(shop) && buyer.getCash() >= funded / 2);
        check("premise: used ground exceeds the player's own horizon and slack line",
                land.getUsedShare() > LongPlaytest.groundAheadUtilisation(g));
        check("premise: the office has linked ground, and every whole offer exceeds the treasury cash",
                land.getMarket().getListing().stream().anyMatch(LandMarket::hasLinkedDry)
                && land.getMarket().getListing().stream().noneMatch(g::canAffordParcel));
        double owned = land.getOwnedSqFt();
        long jobs = posts(buildings);
        long coming = buildings.getJobsUnderConstruction();
        check("premise: the city has jobs and no inherited jobs on site", jobs > 0 && coming == 0);
        int looks = 0, refused = 0, shortBefore = LongPlaytest.groundAheadShort;
        boolean held = true;
        for (int month = 0; month < BusinessInvestment.PLANNING_HORIZON; month++) {
            quietly(() -> LongPlaytest.keepGroundAhead(g));
            looks++;
            final boolean[] built = {false};
            quietly(() -> built[0] = g.buildFor(buyer, shop, 1));
            if (!built[0]) refused++;
            quietly(() -> g.simulateMonths(1));
            held &= land.getOwnedSqFt() == owned
                    && !land.canAllocate(shop.getLandSqFt(), shop.getSector())
                    && buildings.getJobsUnderConstruction() <= coming
                    && posts(buildings) <= jobs;
        }
        out.printf("   looks %d, private refusals %d, cash %.6f, free %.6f, jobs %d -> %d%n",
                looks, refused, g.getCash(), land.getAvailableSqFt(), jobs,
                posts(buildings));
        check("every look is counted cash-short and refuses the funded private order",
                LongPlaytest.groundAheadShort - shortBefore == looks && refused == looks);
        check("every simulated month retains the land shortage and adds no physical or queued jobs", held);

        out.println("--- restoring the land buyer's cash releases the same held order ---");
        LandParcel offer = g.bestOffer(Game.LandNeed.room());
        check("premise: a linked offer remains available for the counterfactual",
                offer != null && LandMarket.hasLinkedDry(offer));
        if (offer != null) {
            // FC2, 2026-11-23: the cash was sized from the offer read with no cash, when no parcel was affordable and the whole
            // listing's best value was the offer (Game.bestOffer(): the running is the parcels the cash affords). Restored, the
            // cash affords more of them and the best of those was another, dearer one: 4.08 the offer read and 4.62 the one the
            // look found, the cash 42.88 for the first and so a budget of 4.29, and the look bought none and counted short, the
            // shop's plot never freed. So the cash is sized from the offer the cash itself brings, until it covers that offer's
            // price as the look's budget reads it (a tenth, with the slack): the offer the look buys then is the one the cash
            // was sized from, on any listing.
            double rate = g.getForeignAccounts().getRate();
            double restored = offer.localPrice(rate) / LongPlaytest.GROUND_AHEAD_CASH_SHARE * (1 + BuildAdvice.SLACK);
            for (int i = 0; i <= land.getMarket().getListing().size(); i++) {
                g.setCashForTest(restored);
                LandParcel brought = g.bestOffer(Game.LandNeed.room());
                double needed = brought == null ? 0
                        : brought.localPrice(rate) / LongPlaytest.GROUND_AHEAD_CASH_SHARE * (1 + BuildAdvice.SLACK);
                if (needed <= restored) break;
                restored = needed;
            }
            quietly(() -> LongPlaytest.keepGroundAhead(g));
            check("the same ground rule now buys land and permits a plot for the held shop",
                    land.getOwnedSqFt() > owned
                    && land.canAllocate(shop.getLandSqFt(), shop.getSector()));
            g.getEconomyManager().setSectorCash(shop.getSector(),
                    2 * (g.quoteBuild(shop, 1).total
                    + land.priceFor(shop.getLandSqFt(), shop.getSector(), LandManager.Use.BUILDING)));
            long before = buildings.getJobsUnderConstruction();
            final boolean[] built = {false};
            quietly(() -> built[0] = g.buildFor(buyer, shop, 1));
            check("the funded private order is accepted and adds the template's own jobs on site",
                    built[0] && buildings.getJobsUnderConstruction() - before
                    == java.util.Arrays.stream(JobType.values()).mapToLong(shop::getJobs).sum());
        }
        out.printf("   fixture %.3f seconds%n", (System.nanoTime() - started) / 1e9);
        out.println(fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}

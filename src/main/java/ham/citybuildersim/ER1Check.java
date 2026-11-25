package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * The growth hold when a full city's treasury cannot buy a whole land offer, and its repair: the offer funded on the
 * land office's bond while a sector waits on ground (FB11).
 *
 * WHY: ER1's Rotterdam replay has willing private investors, no free plots,
 * and a builder with no orders: the player kept ground ahead out of a tenth of
 * its cash and bought no whole offer for hundreds of months. FB11 (ER1-F)
 * funds that ground on the funding page's bond while a sector waits on it and
 * the player's own test for borrowing carries it (LongPlaytest, GROUND FUNDED
 * WHEN A SECTOR WAITS ON IT). This fixture consumes the free ground and
 * removes the city's purchasing cash, then proves, on the actual private build
 * path each month: with nothing waiting on ground the share alone decides; a
 * city its own test will not lend to still holds its jobs every month; a city
 * it will lend to buys the whole offer on the bond, its cash paying no more
 * than the share, the sellers paid, the month's money audit closed, and the
 * held order released; land paid from the vault is not the bond's; an
 * overdrawn treasury's bond covers the overdraft with the offer and buys it
 * (REV-FB11: a bond the size of the offer alone went to the overdraft and
 * bought nothing, look after look); and restoring the cash still buys
 * through the share. The long replays'
 * measurements belong in docs/queue/ER1-notes.md and docs/queue/FB11-notes.md.
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

    /** A scratch city with its free ground consumed and no cash in its treasury, one month played. */
    static Game fullCity(String label) {
        Game g = new Game(GameFiles.scratch(label));
        quietly(g::run);
        g.getDebtManager().setAutopilot(false);
        LandManager land = g.getLandManager();
        land.allocate(land.getAvailableSqFt());
        g.setCashForTest(0);
        quietly(() -> g.simulateMonths(1));
        return g;
    }

    /** The shop's private buyer given twice its whole quote, ground included. */
    static void fundShop(Game g, BuildingsTemplate shop) {
        g.getEconomyManager().setSectorCash(shop.getSector(), 2 * (g.quoteBuild(shop, 1).total
                + g.getLandManager().priceFor(shop.getLandSqFt(), shop.getSector(), LandManager.Use.BUILDING)));
    }

    /** Whether the shop's private buyer holds its whole quote, ground included: what fundShop() gives it twice. */
    static boolean canPayWhole(Game g, Investor buyer, BuildingsTemplate shop) {
        return buyer.getCash() >= g.quoteBuild(shop, 1).total
                + g.getLandManager().priceFor(shop.getLandSqFt(), shop.getSector(), LandManager.Use.BUILDING);
    }

    /** The part of the best offer for room a look's cash share cannot pay: its local price less a tenth of the cash. */
    static double gap(Game g, LandParcel offer) {
        return offer.localPrice(g.getForeignAccounts().getRate()) - Math.max(0, g.getCash()) * LongPlaytest.GROUND_AHEAD_CASH_SHARE;
    }

    /** Whether the last month's money audit closed, by the ensemble's rule (tools/ensemble, auditResidual()): no residual past a cent and a part in ten million, no drift past a cent. */
    static boolean auditClosed(Game g) {
        MoneyAudit.Result r = g.getLastMoneyAudit();
        return r != null && !(Math.abs(r.residual) > .01 && r.relative() > 1e-7) && !(Math.abs(g.getPostAuditDrift()) > .01);
    }

    public static void main(String[] args) throws Exception {
        out = System.out;
        long started = System.nanoTime();
        held();
        funded();
        overdrawn();
        out.printf("   fixture %.3f seconds%n", (System.nanoTime() - started) / 1e9);
        out.println(fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** The holds: nothing waiting on ground, then a city past its own test for borrowing; then the cash restored. */
    static void held() {
        // ...its first PLANNING_HORIZON months played with the ground free, so that no sector has waited on ground and the city
        // has a revenue the player's test can lend against; then the ground consumed and the cash taken, as fullCity() does.
        Game g = new Game(GameFiles.scratch("ER1-land-hold"));
        quietly(g::run);
        g.getDebtManager().setAutopilot(false);
        quietly(() -> g.simulateMonths((int) BusinessInvestment.PLANNING_HORIZON));
        LandManager land = g.getLandManager();
        land.allocate(land.getAvailableSqFt());
        g.setCashForTest(0);
        BuildingManager buildings = g.getBuildingManager();
        BuildingsTemplate shop = buildings.getTemplateByName("Convenience Store");
        Investor buyer = g.getSectorInvestor(shop.getSector());
        DebtManager debt = g.getDebtManager();

        out.println("--- with nothing waiting on ground the cash share alone decides, and nothing is borrowed ---");
        LandParcel first = g.bestOffer(Game.LandNeed.room());
        out.printf("   waiting on ground: %s; month %d, revenue %.6f a month, the share short by %.6f%n", g.getLandBlockedSectors(),
                g.getMonth(), g.getEconomyManager().getTaxIncome(), first == null ? Double.NaN : gap(g, first));
        check("premise: used ground exceeds the player's own horizon and slack line",
                land.getUsedShare() > LongPlaytest.groundAheadUtilisation(g));
        check("premise: a linked offer stands, past what the cash share pays",
                first != null && LandMarket.hasLinkedDry(first) && gap(g, first) > 0);
        check("premise: no sector waits on ground, and the player's own test would lend what the share cannot pay",
                !LongPlaytest.groundDemanded(g) && first != null && LongPlaytest.canService(g, gap(g, first)));
        double owned = land.getOwnedSqFt(), principal = debt.getAllPrincipal();
        int shortBefore = LongPlaytest.groundAheadShort, fundedBefore = LongPlaytest.groundAheadFunded;
        quietly(() -> LongPlaytest.keepGroundAhead(g));
        check("the look is counted short, buys no ground and borrows nothing",
                LongPlaytest.groundAheadShort == shortBefore + 1 && LongPlaytest.groundAheadFunded == fundedBefore
                && land.getOwnedSqFt() == owned && debt.getAllPrincipal() == principal);

        out.println("--- a full city past its own test for borrowing holds jobs while its land buyer has no cash ---");
        // The city borrows on the funding page's own bond until the player's test refuses any more, then as much again: its
        // revenue does not double inside the planning horizon, so no look in it is lent to.
        double lot = Math.max(g.fundingLot("Term"), 12 * Math.max(1, g.getEconomyManager().getTaxIncome()));
        String issued = "";
        for (int i = 0; i < 200 && LongPlaytest.canService(g, 0); i++) issued = g.handleLongBondForCash(lot, Game.BUILD_BOND_YEARS, g.fundingLot("Term"));
        double added = debt.getAllPrincipal() - principal;
        if (added > 0) g.handleLongBondForCash(added, Game.BUILD_BOND_YEARS, g.fundingLot("Term"));
        out.printf("   borrowed %.6f then as much again (lot %.6f, last answer: %s); debt %.6f%n", added, lot, issued, debt.getAllPrincipal());
        g.setCashForTest(0);
        fundShop(g, shop);
        quietly(() -> g.simulateMonths(1));
        check("premise: the shop is known and its private buyer can pay its whole quote",
                buildings.isTemplateKnown(shop) && canPayWhole(g, buyer, shop));
        // ...past the share, not past the whole cash as until FB11: the city has six months' revenue in hand, and the share is
        // what a look pays out of it (the old premise read a city with no revenue, whose every offer was past the cash).
        LandParcel found = g.bestOffer(Game.LandNeed.room());
        check("premise: used ground exceeds the line, and the linked offer a look finds is past what the cash share pays",
                land.getUsedShare() > LongPlaytest.groundAheadUtilisation(g)
                && found != null && LandMarket.hasLinkedDry(found) && gap(g, found) > 0);
        check("premise: the city borrowed until the player's own test refused even no more",
                added > 0 && !LongPlaytest.canService(g, 0));
        owned = land.getOwnedSqFt();
        long jobs = posts(buildings);
        long coming = buildings.getJobsUnderConstruction();
        check("premise: the city has jobs and no inherited jobs on site", jobs > 0 && coming == 0);
        int looks = 0, refused = 0, demanded = 0;
        shortBefore = LongPlaytest.groundAheadShort;
        fundedBefore = LongPlaytest.groundAheadFunded;
        boolean held = true, lent = false;
        for (int month = 0; month < BusinessInvestment.PLANNING_HORIZON; month++) {
            if (LongPlaytest.groundDemanded(g)) demanded++;
            double before = debt.getAllPrincipal();
            quietly(() -> LongPlaytest.keepGroundAhead(g));
            lent |= debt.getAllPrincipal() != before;
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
        out.printf("   looks %d (a sector waiting on ground at %d), private refusals %d, cash %.6f, free %.6f, jobs %d -> %d%n",
                looks, demanded, refused, g.getCash(), land.getAvailableSqFt(), jobs, posts(buildings));
        check("premise: a sector waited on ground at a look, so the hold is the lender's", demanded > 0);
        check("every look is counted cash-short, borrows nothing and refuses the funded private order",
                LongPlaytest.groundAheadShort - shortBefore == looks && LongPlaytest.groundAheadFunded == fundedBefore
                && !lent && refused == looks);
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
            double ownedBefore = land.getOwnedSqFt(), principalBefore = debt.getAllPrincipal();
            quietly(() -> LongPlaytest.keepGroundAhead(g));
            check("the same ground rule now buys land out of the share, borrowing nothing, and permits a plot for the held shop",
                    land.getOwnedSqFt() > ownedBefore && debt.getAllPrincipal() == principalBefore
                    && land.canAllocate(shop.getLandSqFt(), shop.getSector()));
            fundShop(g, shop);
            long before = buildings.getJobsUnderConstruction();
            final boolean[] built = {false};
            quietly(() -> built[0] = g.buildFor(buyer, shop, 1));
            check("the funded private order is accepted and adds the template's own jobs on site",
                    built[0] && buildings.getJobsUnderConstruction() - before
                    == java.util.Arrays.stream(JobType.values()).mapToLong(shop::getJobs).sum());
        }
    }

    /** The repair: the same full city, its own test willing to lend, buys the demanded ground on the funding page's bond. */
    static void funded() {
        Game g = fullCity("ER1-land-funded");
        LandManager land = g.getLandManager();
        BuildingManager buildings = g.getBuildingManager();
        BuildingsTemplate shop = buildings.getTemplateByName("Convenience Store");
        Investor buyer = g.getSectorInvestor(shop.getSector());
        DebtManager debt = g.getDebtManager();
        ForeignAccounts fx = g.getForeignAccounts();
        fundShop(g, shop);
        // ...a month with the fixture's hand in it (the cash and the buyer's purse set), then one with none, whose audit is the premise.
        quietly(() -> g.simulateMonths(2));

        out.println("--- land paid from the vault is not the bond's: the look borrows nothing ---");
        LandParcel offer = g.bestOffer(Game.LandNeed.room());
        out.printf("   waiting on ground: %s%n", g.getLandBlockedSectors());
        check("premise: used ground exceeds the line, a sector waits on ground, and a linked offer stands past the share",
                land.getUsedShare() > LongPlaytest.groundAheadUtilisation(g) && LongPlaytest.groundDemanded(g)
                && offer != null && LandMarket.hasLinkedDry(offer) && gap(g, offer) > 0);
        check("premise: the player's own test would lend what the share cannot pay",
                offer != null && LongPlaytest.canService(g, gap(g, offer)));
        double principal = debt.getAllPrincipal();
        int fundedBefore = LongPlaytest.groundAheadFunded;
        g.setLandPaidFromVault(true);
        quietly(() -> LongPlaytest.keepGroundAhead(g));
        g.setLandPaidFromVault(false);
        check("paying from the vault, the look borrows nothing and funds no offer",
                debt.getAllPrincipal() == principal && LongPlaytest.groundAheadFunded == fundedBefore);

        out.println("--- a city its own test will lend to funds the demanded ground on the funding page's bond ---");
        offer = g.bestOffer(Game.LandNeed.room());
        check("premise: the month before the look closed its money audit, the fixture's hand out of it", auditClosed(g));
        check("premise: a sector still waits on ground and the share cannot pay the offer the look will find",
                LongPlaytest.groundDemanded(g) && offer != null && gap(g, offer) > 0 && LongPlaytest.canService(g, gap(g, offer)));
        long coming = buildings.getJobsUnderConstruction();
        check("premise: the shop's plot is refused before the look, and its private buyer can pay its whole quote",
                !land.canAllocate(shop.getLandSqFt(), shop.getSector())
                && canPayWhole(g, buyer, shop));
        double cash = g.getCash(), share = Math.max(0, cash) * LongPlaytest.GROUND_AHEAD_CASH_SHARE;
        principal = debt.getAllPrincipal();
        double owned = land.getOwnedSqFt();
        double pending = fx.getLandUsdPending(), usd = LongPlaytest.groundAheadUsd, raised = LongPlaytest.groundAheadBorrowed;
        int shortBefore = LongPlaytest.groundAheadShort, boughtBefore = LongPlaytest.groundAheadBought;
        fundedBefore = LongPlaytest.groundAheadFunded;
        quietly(() -> LongPlaytest.keepGroundAhead(g));
        int bought = LongPlaytest.groundAheadBought - boughtBefore, funded = LongPlaytest.groundAheadFunded - fundedBefore;
        double borrowed = LongPlaytest.groundAheadBorrowed - raised, paidUsd = LongPlaytest.groundAheadUsd - usd;
        out.printf("   bought %d offer(s), %d funded; cash %.6f -> %.6f (share %.6f); raised %.6f; debt %.6f -> %.6f; sellers' dollars %.6f%n",
                bought, funded, cash, g.getCash(), share, borrowed, principal, debt.getAllPrincipal(), paidUsd);
        check("the look bought the offer it found, whole: it leaves the listing and the city owns at least its ground",
                land.getMarket().find(offer.getId()) == null && land.getOwnedSqFt() - owned >= offer.getSizeSqFt());
        // ...a look buys until the line or an offer it cannot buy: it may end counted short, at most once.
        check("...on the bond: the offers past the share are counted funded, the look short at most at its end",
                funded > 0 && funded <= bought && LongPlaytest.groundAheadShort - shortBefore <= 1);
        check("...the treasury's own cash paid no more than the share", cash - g.getCash() <= share * (1 + 1e-9) + 1e-9);
        check("...the city's debt rose by no less than the bonds raised", borrowed > 0 && debt.getAllPrincipal() - principal >= borrowed * (1 - 1e-9));
        check("...what the bonds raised and the cash paid together are the offers' price",
                Math.abs(borrowed + (cash - g.getCash()) - paidUsd * fx.getRate()) <= 1e-6 * Math.max(1, paidUsd * fx.getRate()));
        check("...and the sellers are owed the offers' dollars", Math.abs(fx.getLandUsdPending() - pending - paidUsd) <= 1e-6 * Math.max(1, paidUsd));
        check("the held shop's plot is now permitted", land.canAllocate(shop.getLandSqFt(), shop.getSector()));
        final boolean[] built = {false};
        quietly(() -> built[0] = g.buildFor(buyer, shop, 1));
        check("the private order is accepted and adds the template's own jobs on site",
                built[0] && buildings.getJobsUnderConstruction() - coming
                == java.util.Arrays.stream(JobType.values()).mapToLong(shop::getJobs).sum());
        // ...and nothing of the fixture's between the look and the month: the bond, the purchase and the order are the model's.
        quietly(() -> g.simulateMonths(1));
        check("the month after the funded look closes its money audit", auditClosed(g));
    }

    /** An overdrawn treasury (REV-FB11): the funded look's bond covers the overdraft with the offer, as buyWhole()'s Game.landCashGap() counts it, and buys the offer. */
    static void overdrawn() {
        Game g = fullCity("ER1-land-overdrawn");
        LandManager land = g.getLandManager();
        DebtManager debt = g.getDebtManager();
        ForeignAccounts fx = g.getForeignAccounts();
        fundShop(g, g.getBuildingManager().getTemplateByName("Convenience Store"));
        quietly(() -> g.simulateMonths(2));

        out.println("--- an overdrawn treasury: the bond covers the overdraft with the offer, and buys it ---");
        // ...overdrawn by the fixture by as much as it held: Game.landPayable() reads such a treasury as no cash, and buyWhole()'s gap counts the overdraft.
        g.setCashForTest(-Math.max(1, g.getCash()));
        double cash = g.getCash(), rate = fx.getRate();
        LandParcel offer = g.bestOffer(Game.LandNeed.room());
        double local = offer == null ? Double.NaN : offer.localPrice(rate);
        double officeGap = offer == null ? Double.NaN : g.landCashGap(java.util.List.of(offer.getId()));
        out.printf("   cash %.6f; offer %.6f; the office's gap %.6f; waiting on ground: %s%n", cash, local, officeGap, g.getLandBlockedSectors());
        check("premise: the treasury is overdrawn, a sector waits on ground, and a linked offer stands past the line",
                cash < 0 && LongPlaytest.groundDemanded(g) && land.getUsedShare() > LongPlaytest.groundAheadUtilisation(g)
                && offer != null && LandMarket.hasLinkedDry(offer));
        check("premise: the office's gap is the price and the overdraft, and the player's own test would lend it",
                Math.abs(officeGap - (local - cash)) <= 1e-9 * officeGap && LongPlaytest.canService(g, officeGap));
        double principal = debt.getAllPrincipal(), owned = land.getOwnedSqFt();
        double pending = fx.getLandUsdPending(), usd = LongPlaytest.groundAheadUsd, raised = LongPlaytest.groundAheadBorrowed;
        int boughtBefore = LongPlaytest.groundAheadBought, fundedBefore = LongPlaytest.groundAheadFunded, unspent = LongPlaytest.groundBondsUnspent;
        quietly(() -> LongPlaytest.keepGroundAhead(g));
        int bought = LongPlaytest.groundAheadBought - boughtBefore, funded = LongPlaytest.groundAheadFunded - fundedBefore;
        double borrowed = LongPlaytest.groundAheadBorrowed - raised, paidUsd = LongPlaytest.groundAheadUsd - usd;
        out.printf("   bought %d offer(s), %d funded; cash %.6f -> %.6f; raised %.6f; debt %.6f -> %.6f; bonds that bought nothing %d%n",
                bought, funded, cash, g.getCash(), borrowed, principal, debt.getAllPrincipal(), LongPlaytest.groundBondsUnspent - unspent);
        check("the look bought the offer it found, whole: it leaves the listing and the city owns at least its ground",
                offer != null && land.getMarket().find(offer.getId()) == null && land.getOwnedSqFt() - owned >= offer.getSizeSqFt());
        check("...on the bond, and sold no bond that bought no ground",
                funded > 0 && funded <= bought && LongPlaytest.groundBondsUnspent == unspent);
        check("...the bond cleared the overdraft: the treasury ends the look not overdrawn", g.getCash() >= -1e-6 * Math.max(1, paidUsd * rate));
        check("...the city's debt rose by no less than the bonds raised", borrowed > 0 && debt.getAllPrincipal() - principal >= borrowed * (1 - 1e-9));
        check("...what the bonds raised, less the overdraft they cleared, is the offers' price",
                Math.abs(borrowed + (cash - g.getCash()) - paidUsd * rate) <= 1e-6 * Math.max(1, paidUsd * rate));
        check("...and the sellers are owed the offers' dollars", Math.abs(fx.getLandUsdPending() - pending - paidUsd) <= 1e-6 * Math.max(1, paidUsd));
        // ...the fixture's overdraft is a hand in the first month's books; the second has none of it.
        quietly(() -> g.simulateMonths(2));
        check("the second month after the look closes its money audit", auditClosed(g));
    }
}

package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * The test player's ground across its long skips (LP-G1): a growing town whose ground holds the look's
 * BuildAdvice.HORIZON months but not the skip after it, skipped as the player skipped until LP-G1 and as it skips now
 * (LongPlaytest.skipWatchingGround()), and an offer the look's cash share cannot pay, held inside a skip.
 *
 * WHY: LongPlaytest.keepGroundAhead() leaves room for HORIZON months' growth and ran only at the advisor's looks, six
 * to a hundred and twenty months apart. The default playtest (seed 0) looked at m743 and next at m805, and from m760
 * its landlords were held for land with no home on site, full power, unlimited mortgage capital and builders to spare,
 * until 12.3 households had nowhere at m807; the ensemble's seed 9 went from m500 to m603 without a look and flagged
 * m605 (docs/queue/PW5-notes.md, docs/queue/LP-G1.md). This town causes the class from the model's own inputs, not
 * a played month: the playtest's own village, its ground built on up to the player's line, then the rhythm's
 * sixty-month skip.
 *
 * What this has to prove:
 *   1. THE OLD SKIP RAN THE RESERVE OUT. The town at its line - a look buys nothing, the ground holding HORIZON months'
 *      growth with the slack - skipped sixty months with no look at its ground (runPlayer() alone, the skip until
 *      LP-G1): its ground passes the line more than HORIZON months after the look with no look to serve it, and the
 *      landlords' order is held for land ("no land") with no home on site.
 *   2. THE WATCHED SKIP LOOKS IN TIME. The same town, built again and equal at the look, skipped the same sixty months
 *      through skipWatchingGround(): no month comes more than HORIZON months after a look at the ground; the looks
 *      inside the skip buy ground, each look within its GROUND_AHEAD_CASH_SHARE of the cash, and every offer bought is
 *      paid (the land office's dollars and the city's ground grow by the offers); the landlords are never held for
 *      land; and the playtest's own audit of every month (LongPlaytest.audit()) finds nothing the old skip did not.
 *   3. AN OFFER THE SHARE CANNOT PAY STILL WAITS. The same town skipped the old way until its landlords are held for
 *      land, its cash then short of the best offer over GROUND_AHEAD_CASH_SHARE but within the cash: the look the
 *      watched skip makes buys nothing, pays nothing and places nothing, and is counted short; the next comes HORIZON
 *      months on, and with the share able to pay it buys the offer and the landlords are released.
 *   4. EACH CITY KEEPS ITS OWN LOOK. Two towns built alike and skipped watched in step, a month of one and a month of
 *      the other, look at their ground in the same months and end alike: the record of the last look is the city's.
 */
public class GroundSkipCheck {
    static int fails;
    static PrintStream out;
    /** The game's and the playtest's narration while a fixture plays. */
    static final PrintStream QUIET = new PrintStream(OutputStream.nullOutputStream());

    /** The rhythm's sixty-month skip (LongPlaytest.main(), stop % 6 == 3): the gap the default playtest's m743-m805 was. */
    static final int SKIP = 60;

    /** How far under the player's line the fixture leaves the ground built on: a look there buys nothing. */
    static final double UNDER_THE_LINE = .002;

    static void check(String label, boolean ok) {
        out.println(label + (ok ? " OK" : " FAIL"));
        if (!ok) fails++;
    }

    static void quietly(Runnable action) {
        PrintStream was = System.out;
        System.setOut(QUIET);
        try { action.run(); } finally { System.setOut(was); }
    }

    /** One month of the player's own play, as the playtest's skip runs it (LongPlaytest.runPlayer()). */
    static void month(Game g) {
        quietly(() -> LongPlaytest.runPlayer(g, 1));
    }

    /** Whether the landlords' order held for ground this month (Game.getLandBlockedSectors(): BusinessInvestment's noLand()). */
    static boolean landlordsHeld(Game g) {
        return g.getLandBlockedSectors().contains(Sectors.REAL_ESTATE);
    }

    /** The homes the landlords have on site. */
    static double homesOnSite(Game g) {
        return g.getBuildingManager().getHousingInventory().pipelineHomes();
    }

    /** The months the town is played before its look: past the founding's surge, when the businesses' forecast reads a town growing by the tenth of a percent a month and not by percents. */
    static final int TOWN_MONTHS = 240;

    /** The town's save, in its own scratch folder, which each section loads a copy of. */
    static GameFiles townFiles;
    static final int TOWN_SLOT = 1;

    /** The playtest's own town (LongPlaytest.probeCity(), then probePlay(): its founding and its rhythm of skips, looks and moves) to TOWN_MONTHS, saved. */
    static void playTheTown() {
        Game[] made = new Game[1];
        quietly(() -> {
            made[0] = LongPlaytest.probeCity(GameFiles.scratch("groundskip-town"), null);
            LongPlaytest.probePlay(made[0], TOWN_MONTHS);
        });
        Game t = made[0];
        townFiles = t.getGameFiles();
        boolean[] saved = {false};
        quietly(() -> saved[0] = t.saveGame(TOWN_SLOT, "groundskip m" + t.getMonth()).ok);
        t.awaitSaves();
        check("premise: the playtest's town is played to month " + t.getMonth() + " and saved", saved[0] && t.getMonth() >= TOWN_MONTHS);
    }

    /**
     * A copy of the town, loaded from its save, at its look: the look made (keepGroundAhead()), then its dry ground
     * built on up to UNDER_THE_LINE below the player's line (LandManager.allocate(), as LandCheck 22 builds a city's
     * ground) - where a look leaves a town that has grown into its ground.
     */
    static Game town() {
        Game g = new Game(townFiles);
        quietly(() -> g.loadGameSave(TOWN_SLOT));
        if (g.getLoadFailure() != null) throw new IllegalStateException("the town's save did not load: " + g.getLoadFailure());
        quietly(() -> LongPlaytest.keepGroundAhead(g));
        LandManager land = g.getLandManager();
        double target = LongPlaytest.groundAheadUtilisation(g) - UNDER_THE_LINE;
        for (int step = 0; step < 80 && land.getUsedShare() < target - UNDER_THE_LINE / 10; step++) {
            double room = land.getAvailableSqFt() / Math.max(1e-9, 1 - land.getUsedShare());
            double take = Math.min(land.getAvailableSqFt(), Math.max(1, (target - land.getUsedShare()) * room * .9));
            if (!land.allocate(take)) break;
        }
        return g;
    }

    /** What a month of a skip saw. */
    record Seen(int month, double cash, double used, double line, int sinceLook, boolean held, double onSite, String order) { }

    static Seen seen(Game g) {
        LandManager land = g.getLandManager();
        return new Seen(g.getMonth(), g.getCash(), land.getUsedShare(), LongPlaytest.groundAheadUtilisation(g),
                LongPlaytest.monthsSinceGroundLook(g), landlordsHeld(g), homesOnSite(g),
                g.getLastInvestment(Sectors.REAL_ESTATE));
    }

    /** The playtest's findings' names, as its audit has them now. */
    static java.util.Set<String> findingNames() {
        return new java.util.TreeSet<>(LongPlaytest.findings.keySet());
    }

    public static void main(String[] args) throws Exception {
        out = System.out;
        LongPlaytest.out = QUIET;
        long started = System.nanoTime();
        playTheTown();

        /* ---------- 1. the old skip ---------- */
        out.println("--- 1. the old skip ran the reserve out: no look in sixty months, the landlords held for land ---");
        Game a = town();
        LandManager al = a.getLandManager();
        int lookMonth = a.getMonth();
        double aLine = LongPlaytest.groundAheadUtilisation(a), aOwned = al.getOwnedSqFt(), aCash = a.getCash(), aUsed = al.getUsedShare();
        out.printf("   the town at m%d: %,d people, %.4f of its ground built on against the line %.4f (growth %.6f over %.0f months), %,.0f sq ft free of %,.0f owned%n",
                lookMonth, a.getPopulationManager().getPopulation(), al.getUsedShare(), aLine,
                a.getBusinessInvestment().growthFactor(BuildAdvice.HORIZON), BuildAdvice.HORIZON, al.getAvailableSqFt(), aOwned);
        check("premise: the town is growing: the businesses' growthFactor() over HORIZON is above 1",
                a.getBusinessInvestment().growthFactor(BuildAdvice.HORIZON) > 1);
        check("premise: its ground is built on up to the player's line, under it",
                al.getUsedShare() <= aLine && al.getUsedShare() > aLine - 2 * UNDER_THE_LINE);
        check("premise: by the businesses' own forecast its ground holds HORIZON months' growth with the slack, and not the skip's",
                al.getUsedShare() * a.getBusinessInvestment().growthFactor(BuildAdvice.HORIZON) * (1 + BuildAdvice.SLACK) <= 1
                && al.getUsedShare() * a.getBusinessInvestment().growthFactor(SKIP) > 1);
        int boughtBefore = LongPlaytest.groundAheadBought;
        quietly(() -> LongPlaytest.keepGroundAhead(a));
        check("premise: a look now buys nothing - the ground holds HORIZON months' growth with the slack",
                LongPlaytest.groundAheadBought == boughtBefore && al.getOwnedSqFt() == aOwned);
        check("premise: the landlords are not held for land at the look", !landlordsHeld(a));
        java.util.Set<String> aFound = findingNames();
        java.util.List<Seen> aSeen = new java.util.ArrayList<>();
        for (int i = 0; i < SKIP; i++) {
            month(a);
            aSeen.add(seen(a));
        }
        Seen aPast = aSeen.stream().filter(s -> s.used() > s.line()).findFirst().orElse(null);
        Seen aUnserved = aSeen.stream().filter(s -> s.used() > s.line() && s.sinceLook() > BuildAdvice.HORIZON).findFirst().orElse(null);
        Seen aHeld = aSeen.stream().filter(s -> s.held() && s.onSite() == 0).findFirst().orElse(null);
        long aHeldMonths = aSeen.stream().filter(Seen::held).count();
        java.util.Set<String> aNew = findingNames();
        aNew.removeAll(aFound);
        out.printf("   past the line first at m%s; unserved (past it, %.0f months or more since a look) first at m%s; held for land %d of %d months, first with no home on site at m%s (%s)%n",
                aPast == null ? "-" : aPast.month(), BuildAdvice.HORIZON + 1, aUnserved == null ? "-" : aUnserved.month(),
                aHeldMonths, SKIP, aHeld == null ? "-" : aHeld.month(), aHeld == null ? "" : aHeld.order());
        out.printf("   at m%d: %.4f built on, %,.0f sq ft free, findings new in the skip %s%n", a.getMonth(), al.getUsedShare(), al.getAvailableSqFt(), aNew);
        check("the old skip makes no look at the ground in its sixty months",
                LongPlaytest.monthsSinceGroundLook(a) == SKIP && al.getOwnedSqFt() == aOwned);
        check("...its ground passes the line more than HORIZON months after the look, with no look to serve it", aUnserved != null);
        check("...and the landlords' order is held for land with no home on site", aHeld != null);

        /* ---------- 2. the watched skip ---------- */
        out.println("--- 2. the watched skip looks in time: a look each HORIZON months, ground bought and paid, the landlords never held ---");
        Game b = town();
        LandManager bl = b.getLandManager();
        check("premise: the town built again is the same town at the look",
                b.getMonth() == lookMonth && b.getCash() == aCash && bl.getUsedShare() == aUsed);
        double bOwned = bl.getOwnedSqFt();
        quietly(() -> LongPlaytest.keepGroundAhead(b));
        check("premise: a look now buys nothing here too", bl.getOwnedSqFt() == bOwned);
        java.util.Set<String> bFound = findingNames();
        int looksBefore = LongPlaytest.groundLooksInSkips, bought = LongPlaytest.groundAheadBought, shortBefore = LongPlaytest.groundAheadShort;
        int purchases = b.getCityLand().purchases().size();
        double landUsd = b.getForeignAccounts().getLandUsdLifetime();
        // What each month of the skip ended at, the look's own month first: a look inside the skip is made before a
        // month runs, so its purchases carry the month it was made in and the cash it had is that month's opening.
        java.util.Map<Integer, Seen> bAt = new java.util.LinkedHashMap<>();
        bAt.put(b.getMonth(), seen(b));
        quietly(() -> LongPlaytest.skipWatchingGround(b, SKIP, () -> {
            LongPlaytest.runPlayer(b, 1);
            bAt.put(b.getMonth(), seen(b));
        }));
        java.util.List<Seen> bSeen = new java.util.ArrayList<>(bAt.values());
        bSeen.remove(0);
        int worstGap = bSeen.stream().mapToInt(Seen::sinceLook).max().orElse(-1);
        long bHeldMonths = bSeen.stream().filter(Seen::held).count();
        int looks = LongPlaytest.groundLooksInSkips - looksBefore, offers = LongPlaytest.groundAheadBought - bought;
        java.util.List<CityLand.Purchase> made = new java.util.ArrayList<>(
                b.getCityLand().purchases().subList(purchases, b.getCityLand().purchases().size()));
        java.util.Map<Integer, Double> spentAt = new java.util.TreeMap<>();
        for (CityLand.Purchase p : made) spentAt.merge(p.month(), p.paidLocal(), Double::sum);
        boolean withinShare = !spentAt.isEmpty();
        for (java.util.Map.Entry<Integer, Double> s : spentAt.entrySet()) {
            Seen opening = bAt.get(s.getKey());
            withinShare &= opening != null && s.getValue() > 0
                    && s.getValue() <= LongPlaytest.GROUND_AHEAD_CASH_SHARE * Math.max(0, opening.cash()) * (1 + 1e-12);
        }
        double usdPaid = made.stream().mapToDouble(p -> p.offer().getPriceUsd()).sum();
        double sqFtBought = made.stream().mapToDouble(p -> p.offer().getSizeSqFt()).sum();
        java.util.Set<String> bNew = findingNames();
        bNew.removeAll(bFound);
        out.printf("   %d look(s) inside the skip, %d offer(s) bought at m%s (%d stopped short); the most months without a look %d; held for land %d of %d months; at m%d %.4f built on against %.4f; findings new in the skip %s%n",
                looks, offers, spentAt.keySet(), LongPlaytest.groundAheadShort - shortBefore, worstGap, bHeldMonths, SKIP, b.getMonth(),
                bl.getUsedShare(), LongPlaytest.groundAheadUtilisation(b), bNew);
        check("no month of the watched skip comes more than HORIZON months after a look at the ground",
                worstGap >= 0 && worstGap <= BuildAdvice.HORIZON);
        check("...a look each HORIZON months: as many looks as the skip holds horizons after the look's own",
                looks == (int) ((SKIP - 1) / BuildAdvice.HORIZON));
        check("...and its looks bought ground, each offer counted", offers > 0 && made.size() == offers);
        check("...each look spending no more than GROUND_AHEAD_CASH_SHARE of the cash it had", withinShare);
        check("...paid: the land office's dollars grew by the offers' and the city's ground by their dry ground",
                Math.abs(b.getForeignAccounts().getLandUsdLifetime() - landUsd - usdPaid) <= 1e-9 * Math.max(1, usdPaid)
                && Math.abs(bl.getOwnedSqFt() - bOwned - sqFtBought) <= 1e-6 * Math.max(1, sqFtBought));
        check("the landlords are never held for land across the watched skip, where the old skip held them "
                + aHeldMonths + " month(s)", bHeldMonths == 0 && aHeldMonths > 0);
        check("...and the playtest's audit of every month finds nothing the old skip did not",
                aNew.containsAll(bNew));

        /* ---------- 3. the unaffordable offer ---------- */
        out.println("--- 3. an offer the share cannot pay still waits: nothing bought, paid or placed, the next look HORIZON months on ---");
        Game c = town();
        LandManager cl = c.getLandManager();
        for (int i = 0; i < SKIP && !(landlordsHeld(c) && LongPlaytest.groundLookDue(c)); i++) month(c);
        check("premise: skipped the old way, the landlords are held for land and a look is due",
                landlordsHeld(c) && LongPlaytest.groundLookDue(c) && cl.getUsedShare() > LongPlaytest.groundAheadUtilisation(c));
        LandParcel offer = c.bestOffer(Game.LandNeed.room());
        double price = offer == null ? Double.NaN : offer.localPrice(c.getForeignAccounts().getRate());
        if (offer != null) c.setCashForTest(price / LongPlaytest.GROUND_AHEAD_CASH_SHARE * .9);
        check("premise: the best value is past the cash share, inside the cash",
                offer != null && LandMarket.hasLinkedDry(offer)
                && price > LongPlaytest.GROUND_AHEAD_CASH_SHARE * c.getCash() && c.canAffordParcel(offer));
        double cOwned = cl.getOwnedSqFt(), cUsd = c.getForeignAccounts().getLandUsdLifetime();
        int cPurchases = c.getCityLand().purchases().size(), cShort = LongPlaytest.groundAheadShort, cLooks = LongPlaytest.groundLooksInSkips;
        quietly(() -> LongPlaytest.skipWatchingGround(c, 1, () -> LongPlaytest.runPlayer(c, 1)));
        check("the watched skip looks, and the look is counted stopped short",
                LongPlaytest.groundLooksInSkips == cLooks + 1 && LongPlaytest.groundAheadShort == cShort + 1);
        check("...buys nothing and pays nothing: the ground, the purchases and the land office's dollars as they were",
                cl.getOwnedSqFt() == cOwned && c.getCityLand().purchases().size() == cPurchases
                && c.getForeignAccounts().getLandUsdLifetime() == cUsd);
        check("...and places nothing: the landlords are still held for land", landlordsHeld(c));
        int waited = 0;
        while (!LongPlaytest.groundLookDue(c) && waited < 2 * BuildAdvice.HORIZON) {
            month(c);
            waited++;
        }
        check("the next look is due HORIZON months after the held one", waited == (int) BuildAdvice.HORIZON - 1);
        LandParcel again = c.bestOffer(Game.LandNeed.room());
        if (again != null) c.setCashForTest(again.localPrice(c.getForeignAccounts().getRate())
                / LongPlaytest.GROUND_AHEAD_CASH_SHARE * (1 + BuildAdvice.SLACK));
        double cOwnedNow = cl.getOwnedSqFt(), cUsdNow = c.getForeignAccounts().getLandUsdLifetime();
        quietly(() -> LongPlaytest.skipWatchingGround(c, 1, () -> LongPlaytest.runPlayer(c, 1)));
        check("...and with the share able to pay, that look buys the offer and pays for it",
                again != null && cl.getOwnedSqFt() > cOwnedNow && c.getForeignAccounts().getLandUsdLifetime() > cUsdNow);
        for (int i = 0; i < BuildAdvice.HORIZON && landlordsHeld(c); i++) {
            quietly(() -> LongPlaytest.skipWatchingGround(c, 1, () -> LongPlaytest.runPlayer(c, 1)));
        }
        check("...and the landlords are released", !landlordsHeld(c));

        /* ---------- 4. each city its own look ---------- */
        out.println("--- 4. each city keeps its own look: two towns skipped watched in step look and end alike ---");
        Game d = town(), e = town();
        StringBuilder dLooks = new StringBuilder(), eLooks = new StringBuilder();
        for (int i = 0; i < SKIP; i++) {
            if (LongPlaytest.groundLookDue(d)) dLooks.append(d.getMonth()).append(' ');
            quietly(() -> LongPlaytest.skipWatchingGround(d, 1, () -> LongPlaytest.runPlayer(d, 1)));
            if (LongPlaytest.groundLookDue(e)) eLooks.append(e.getMonth()).append(' ');
            quietly(() -> LongPlaytest.skipWatchingGround(e, 1, () -> LongPlaytest.runPlayer(e, 1)));
        }
        out.printf("   looks at m %s and m %s%n", dLooks.toString().trim(), eLooks.toString().trim());
        check("the two look at their ground in the same months, HORIZON apart",
                dLooks.toString().equals(eLooks.toString()) && dLooks.length() > 0);
        check("...and end alike: the month, the cash, the ground owned and built on",
                d.getMonth() == e.getMonth() && d.getCash() == e.getCash()
                && d.getLandManager().getOwnedSqFt() == e.getLandManager().getOwnedSqFt()
                && d.getLandManager().getUsedShare() == e.getLandManager().getUsedShare());

        out.printf("   fixtures %.1f seconds%n", (System.nanoTime() - started) / 1e9);
        out.println(fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}

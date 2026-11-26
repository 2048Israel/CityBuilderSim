package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The landlords' order at the lender's door (HN1, 2026-11-18): a played
 * city's households held from the homes that fit them while the plan passed
 * those homes and the door would not write the whole order.
 *
 * WHY: R0h's 80-run ensemble found AUTO2 and ZERO_SLACK seed 12 with 3.6 to
 * 13.2 households with no door at all at months 744-747 - LongPlaytest's
 * watch, more than one - on main's model too. The probe behind this harness
 * (docs/queue/HN1-notes.md) found the landlords asking 26-35 Low-Rise
 * Apartments a month from m699 to m746, the family segment 2,257 to 2,870
 * doors short, and the lender's door refusing every whole order for the
 * bank's capital or the down payment at the purchase's price, with 32,000 in
 * the till: five blocks bought outright. Nothing was built for four years;
 * 2,959 of 5,918 households doubled up, the ceiling, and then past it. Today's
 * main no longer takes seed 12 to the ceiling, but its landlords are held at
 * the door the same way in some twenty months of m437-m496 with the door's
 * price of a block in the till. This harness plays that city - the
 * ensemble's AUTO2 seed 12, its founding and rhythm line for line
 * (the driver in tools/ensemble/, its foundAndPlay()) - through those months, its
 * player and its automatic works as they play, and watches the landlords'
 * every order there (Game.watchOrders()).
 * HL1's merged path moves the first capital-refused trim from m495 to m502, just beyond that old cutoff; the watch now retains
 * those months and continues to m900 (eight capital trims), with the households' cash identity checked every played month too.
 *
 * What this has to prove:
 *   1. the fixture: the plan passes the landlords orders while their homes'
 *      segment is short of doors; before FB15 the door cut nine of them for a
 *      down payment raised at the plan's price (m600-m706), and since FB15
 *      (HN1-A, the order at the door's own price) it cuts or holds none for
 *      its down payment (OrderDoorCheck 2 causes the case in one month);
 *   2. in no month is a landlord whose till holds the door's price of one of
 *      the homes the plan passed, on ground free for one, while that
 *      segment's households have no door of their own, left with none
 *      (Game.considerOnMortgage(), THE DOOR'S NO IS TO THE ORDER);
 *   3. an order the door cut is a halving of what the plan passed, and the
 *      advisor says it was trimmed; a month the door trimmed for the bank's
 *      capital is not left marked as held for it (REV-HN1-1: LongPlaytest
 *      counts the mark as a month held for the bank's leverage); since MC1
 *      an order the door would refuse for the bank's capital is put to the
 *      owners first, and the door trims for the capital only what their
 *      issue left uncovered (that trim caused in CreditCrunchCheck 3);
 *   4. LongPlaytest's watch holds - no month leaves more than one household
 *      with no door - and the homes that finish are counted by size; since
 *      call A (RES3c, 2026-11-25) a household the census line has no door for
 *      lives past it, overcrowded (HousingMarket.overcrowding(), the 1891 and
 *      1901 censuses' 7.04 million families in 6.26 million houses), counted
 *      by the census's own line on its door - past two persons a room
 *      overcrowded, within it sharing (REV-RES3b P1-4) - and sharing, the
 *      families more than the doors they lease, rises
 *      when the town's households outgrow its homes: over the months the
 *      households seeking a home grow by more than the homes standing, the
 *      households sharing a door rise, and by more a month than over the
 *      months the homes catch up; the coda's fire puts households past the
 *      line, overcrowded, none doorless. The months the landlords' order is trimmed or held
 *      (its down payment, the lender's test, the bank's capital, or no land)
 *      are printed beside it, not asserted: a month's order is not its stock
 *      of crowding, and the comparison by order turned over with main 0.9.208
 *      (behind 10.688 a month against 3.454 kept up on RES3's own town; 0.192
 *      against 2.065 on the merge, its peak, 52.100 at m830, the month before
 *      the order trimmed at m831; RES3c, 2026-11-26); and a household that
 *      could pay and found no door boards with a housed family, counted by
 *      the census's own line on its door, paying its hosts board on both
 *      sides of the households' money, the month's own audit exact in its
 *      months, and a save and load with boarders standing bringing them all
 *      back (call A over R1g's waiting, the orchestrator's call of
 *      2026-11-26; REV-RES3b's four P1s);
 *   5. every played month's households' cash identity closes beside the accepted and refused mortgage orders (HL1).
 */
public final class HousingDoorCheck {

    static PrintStream out;
    static int fails;

    /** The seed R0h's ensemble found it on, and today's main holds its landlords at the door on. */
    static final int SEED = 12;
    /** The months watched, months already run: HL1 retains m431-500 and continues to m900; its first capital trim is m502, eight fire by m900. */
    static final int FROM = 430, TO = 900;
    /** The ensemble's AUTO2: the autopilot at this target, every month (Ensemble.TARGET). */
    static final double TARGET = .02;
    static final String RE = Sectors.REAL_ESTATE;
    /** Call A's first town's boom (a6b4e2369): 36.993 households overcrowded, the landlords' 389 Houses trimmed to 14 for the down payment. */
    static final int BOOM = 831;
    /** The months played past the window once the fire has taken its homes, for the payers that find no door to board (call A over R1g). */
    static final int CODA = 6;
    /** The fire at the window's end leaves one structure in this many of every housing class standing, the rest taken occupied as they stand (call A over R1g's coda). */
    static final int FIRE = 4;

    /** Months of one kind and the households overcrowded in them (call A). */
    static final class Tally {
        int months, first = -1, last = -1, mostAt = -1;
        double sum, most;
        void add(int m, double crowded) {
            months++; sum += crowded; if (first < 0) first = m; last = m;
            if (crowded > most) { most = crowded; mostAt = m; }
        }
        double mean() { return months > 0 ? sum / months : 0; }
        String text() { return String.format(Locale.ROOT, "%d months (m%d-m%d): %.3f households sharing a door a month, at most %.3f (m%d)", months, first, last, mean(), most, mostAt); }
    }

    static void check(String label, boolean ok) {
        out.println((ok ? "  ok   " : "  FAIL ") + label);
        if (!ok) fails++;
    }

    /** One month's order at the door: what the plan passed, the till it was judged on, and the door's price of one. */
    static final class Ask {
        int month;
        String building;
        boolean family;
        long asked, passed;
        double till, doorOne, shortfall;
        boolean groundForOne;
        String line;
        long built;
        /** The month ended with the landlords marked as refused a mortgage for the bank's capital (what LongPlaytest counts). */
        boolean markedForCapital;
        /** The order's cost, the till before the owners' issue, what consider() offered the owners this month and what they put in, and whether the door would have refused the order's mortgage for the bank's capital on that till (MC1). */
        double cost, held, offered, raised;
        boolean shortBeforeOwners;
        /** The landlord's quote supported no issue this month, so consider() offered nothing (Exchange.quoteSupportsIssue()). */
        boolean quoteShut;
    }

    static Ask pending;
    static final List<Ask> asks = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        out = System.out;
        Locale.setDefault(Locale.ROOT);   // as the ensemble plays it; the advisor's counts read "1,234"
        PrintStream quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        // LongPlaytest's founding reads its seed here, as the ensemble sets it; each harness has its JVM.
        System.setProperty("playtest.seed", Integer.toString(SEED));
        GameFiles files = GameFiles.scratch("housingdoor");

        final Game g;
        PrintStream was = LongPlaytest.out;
        LongPlaytest.out = quiet;
        System.setOut(quiet);
        long[][] sizes = new long[1][];
        double[] worstUnplaced = { 0 };
        int[] worstMonth = { -1 };
        String[] worstHousing = { "" };
        long[] finished = new long[16];
        // HL1: the same played path must close the households' cash books, including accepted trimmed orders.
        int[] householdMonths = { 0, 0 };
        MoneyAudit.Households[] worstHouseholds = { MoneyAudit.Households.NONE };
        // Call A: the households living past the census line, by whether building fell behind the town's households.
        Tally behind = new Tally(), keptUp = new Tally(), forLand = new Tally();
        // ...and by the cause the censuses read: [0] the months the households outgrew the homes, [1] the months the homes
        // caught up; the rise of overcrowding over each, and last month's overcrowded, households seeking and homes standing.
        int[] grew = { 0, 0 };
        double[] rose = { 0, 0 }, last = { -1, 0, 0 };
        // ...and the payers boarding (call A over R1g): [0] months with boarders, [1] of them every boarder counted by the
        // census's own line, [2] of them the month's own money audit and the households' cash identity exact, [3] of them
        // the board paid out of the boarders' cells all received into their hosts' (REV-RES3b); the most, its month, and
        // the most board paid in a month.
        int[] board = { 0, 0, 0, 0 };
        double[] boardMost = { 0, -1, 0 };
        // A save and load with boarders standing (REV-RES3b P1-2): [0] the month (-1 none), [1] 1 if every boarding
        // figure came back as saved, [2] 1 if the loaded market's maps equal the running one's.
        int[] trip = { -1, 0, 0 };
        String[] tripLine = { "" };
        double[] windowPast = { 0 };   // the window's most households past the census line, overcrowded
        // The coda past the window: [0] the month its vacant homes were retired, [1] the structures, [2] the months played.
        long[] coda = { -1, 0, 0 };
        double[] codaMost = { 0, 0 };   // the coda's most households overcrowded, and with no door
        double[] boom = { -1, -1 };
        String[] boomLine = { "" };
        try {
            g = new Game(files, LongPlaytest.played(null));
            g.run();
            g.watchOrders(new BusinessInvestment.OrderWatch() {
                @Override public void mortgaged(long asked, java.util.function.LongToDoubleFunction costOf, double cash,
                                                double noiPerUnit, double annualRate, Mortgage.Decision found) {
                    if (g.getMonth() < FROM - 1) return;
                    pending = ask(g, asked, costOf, cash, found);
                }
            });
            foundAndPlay(g, () -> {
                int m = g.getMonth();
                MoneyAudit.Households households = g.getLastHouseholdsAudit();
                householdMonths[0]++;
                if (!LongPlaytest.MoneyFixtures.householdsClose(households)) householdMonths[1]++;
                if (households.relative() > worstHouseholds[0].relative()) worstHouseholds[0] = households;
                long[] now = g.getBuildingManager().homesBySize();
                if (sizes[0] != null && m > FROM) {
                    for (int s = 1; s < now.length && s < finished.length; s++) {
                        finished[s] += Math.max(0, now[s] - (s < sizes[0].length ? sizes[0][s] : 0));
                    }
                }
                sizes[0] = now;
                if (m <= FROM) { pending = null; return; }
                double still = g.getFamilies().getStillUnplaced();
                if (still > worstUnplaced[0]) { worstUnplaced[0] = still; worstMonth[0] = m; worstHousing[0]=housingSnapshot(g); }
                var market = g.getSectors().realEstate().housingMarket();
                // The households sharing a door, the census's families more than its houses (1891/1901: 7.04 million
                // families in 6.26 million houses): every lease's households past its doors - a share's second family, a
                // guest beside its host, a boarder, a lodger - and of them the overcrowded, past the census line. Restated
                // (RES3c, on the 0.9.210 merge) from overcrowded() + withinTheLine(), which read only the households
                // overcrowding() placed: from 0.9.210 the town's 2,210 households beside another family at m900 are all
                // the earlier passes' guests within the line, and overcrowding() places none in the window.
                double placed = 0;
                for (var l : market.leases()) placed += l.households();
                double crowded = placed - market.occupied();
                windowPast[0] = Math.max(windowPast[0], market.overcrowded());
                double seeking = 0;
                for (var d : market.demands()) seeking += d.count();
                long standing = g.getBuildingManager().getHousingInventory().homes();
                if (last[0] >= 0) {
                    int k = seeking - last[1] > standing - last[2] ? 0 : 1;
                    grew[k]++;
                    rose[k] += crowded - last[0];
                }
                last[0] = crowded; last[1] = seeking; last[2] = standing;
                boarders(g, households, board, boardMost);
                // The model's own readings of building falling behind: the month's order at the door trimmed or held (its
                // down payment, the lender's test, the bank's capital), or the landlords held for land.
                boolean land = g.getLandBlockedSectors().contains(RE);
                boolean door = pending != null && (pending.passed < pending.asked
                        || g.getEconomyManager().getBusinessDebtManager().wasMortgageRefusedForCapital(RE));
                (land || door ? behind : keptUp).add(m, crowded);
                if (land) forLand.add(m, crowded);
                if (m == BOOM) { boom[0] = crowded; boom[1] = market.overcrowdedPeople(); boomLine[0] = g.getLastInvestment(RE); }
                if (pending != null) {
                    pending.month = m;
                    pending.line = g.getLastInvestment(RE);
                    pending.built = builtIn(pending.line);
                    pending.markedForCapital = g.getEconomyManager().getBusinessDebtManager().wasMortgageRefusedForCapital(RE);
                    asks.add(pending);
                    pending = null;
                }
            });
            // The payers who find no door standing (call A over R1g, the orchestrator's call of 2026-11-26). The cause: at the
            // window's end a fire leaves one structure in FIRE of every housing class standing, households in the rest, and
            // their ground is released (the occupied retirement HousingMarketCheck's retiredRoom() proves); at m900 the town
            // has no whole structure vacant, so the households it puts out, and those that form or arrive after, find no
            // door standing (RES3c), as San Francisco's fire of 1906 left more than half its people with no home. 78 Houses
            // alone (a quarter) left every one a door past the line in another class, and a quarter of every class (415
            // structures) 53.347 households overcrowded, every payer a door or a share; three in four (1,249) left 2,049.786
            // payers boarding at m901.
            var bm = g.getBuildingManager();
            coda[0] = g.getMonth();
            for (BuildingsTemplate t : bm.getTemplates()) {
                if (t.getHousingRecipe() == null || bm.getQuantity(t.getId()) < FIRE) continue;
                long gone = bm.retire(t, bm.getQuantity(t.getId()) * (FIRE - 1) / FIRE);
                g.getLandManager().release(gone * t.getLandSqFt(), t.getSector(), Game.groundUse(t));
                coda[1] += gone;
            }
            for (int i = 0; i < CODA && !stuck; i++) {
                int before = g.getMonth();
                LongPlaytest.runPlayer(g, 1);
                if (g.getMonth() == before) break;
                coda[2]++;
                boarders(g, g.getLastHouseholdsAudit(), board, boardMost);
                var after = g.getSectors().realEstate().housingMarket();
                if (trip[0] < 0 && after.boarders() > 0) tripLine[0] = roundTrip(g, trip);
                codaMost[0] = Math.max(codaMost[0], after.overcrowded());
                codaMost[1] = Math.max(codaMost[1], g.getFamilies().getStillUnplaced());
            }
        } finally {
            LongPlaytest.out = was;
            System.setOut(out);
        }

        out.printf("HousingDoorCheck: the ensemble's AUTO2 seed %d, months %d-%d, the landlords' orders at the lender's door%n",
                SEED, FROM + 1, TO);
        out.printf("   played to month %d: %,d people, %,d homes, %s by size%n", g.getMonth(),
                g.getPopulationManager().getPopulation(), g.getBuildingManager().getTotalHomes(),
                java.util.Arrays.toString(g.getBuildingManager().homesBySize()));

        out.println("\n--- 1. the fixture: orders the plan passed, the segment short; none cut for its down payment ---");
        int shortWhole = 0, cut = 0, held = 0, forDown = 0;
        for (Ask a : asks) {
            if (a.passed < 1 || a.shortfall <= 0) continue;
            if (a.built >= a.passed) shortWhole++; else if (a.built > 0) cut++; else held++;
            // Game.doorTrimmed()'s and the door's hold's words for the down payment the till lacked there, on an order the door
            // did not write whole (Mortgage.Decision's own trim, inside the plan, says the same of what the till buys).
            if (a.built < a.passed && a.line != null
                    && (a.line.contains("its own funds put the") || a.line.contains("down payment on"))) forDown++;
        }
        for (Ask a : asks) {
            if (a.passed >= 1 && a.built < a.passed) {
                out.printf(Locale.ROOT, "   m%d %s: asked %d, the plan passed %d, built %d; till %.1f, the door's price of one %.1f;"
                        + " %s short %.0f doors%n     %s%n", a.month, a.building, a.asked, a.passed, a.built, a.till, a.doorOne,
                        a.family ? "family" : "studio", a.shortfall, a.line);
            }
        }
        out.printf("   %d month(s) watched with an order at the door; with the segment short %d written whole, %d cut, %d held"
                + " whole, %d of them for the down payment%n", asks.size(), shortWhole, cut, held, forDown);
        check("fixture: the plan passed the landlords orders while their segment was short of doors", shortWhole + cut + held > 0);
        check("...and the door cut or held none of them for its down payment, raised at the door's own price (FB15, HN1-A)",
                forDown == 0);

        out.println("\n--- 2. no landlord with the door's price of one in its till, on ground for one, is left with none ---");
        int stranded = 0;
        String first = null;
        for (Ask a : asks) {
            boolean couldBuyOne = a.passed >= 1 && a.shortfall > 0 && a.groundForOne && a.till >= a.doorOne;
            if (couldBuyOne && a.built == 0) {
                stranded++;
                if (first == null) first = String.format(Locale.ROOT, "m%d: %s, till %.1f against %.1f - %s",
                        a.month, a.building, a.till, a.doorOne, a.line);
            }
        }
        out.printf("   %d month(s) held a landlord that could have bought one%n", stranded);
        if (first != null) out.println("   first: " + first);
        check("no month holds a landlord whose till buys one of the homes the plan passed outright, while its segment"
                + " is short of doors", stranded == 0);

        out.println("\n--- 3. an order the door cut is a halving of what the plan passed, and says it was trimmed ---");
        int halved = 0, notHalved = 0, unsaid = 0, forCapital = 0, leftMarked = 0;
        for (Ask a : asks) {
            if (a.built < 1 || a.built >= a.passed) continue;
            boolean isHalving = false;
            for (long q = a.passed / 2; q >= 1 && !isHalving; q /= 2) isHalving = q == a.built;
            if (isHalving) halved++; else notHalved++;
            if (a.line == null || !a.line.contains("(trimmed from ")) unsaid++;
            // Game.doorTrimmed()'s words for a first no for the capital: "...the bank is short of capital for more".
            if (a.line != null && a.line.contains("the bank is short of capital")) forCapital++;
            if (a.markedForCapital) leftMarked++;
        }
        out.printf("   %d cut order(s): %d a halving of what the plan passed; %d first refused for the bank's capital,"
                + " %d left marked as held for it%n", halved + notHalved, halved, forCapital, leftMarked);
        check("every order the door cut is the plan's order halved, down to one", notHalved == 0);
        check("...and the advisor's line says it was trimmed", unsaid == 0);
        /* MC1: while the door would refuse an order's mortgage for the bank's capital, the owners are offered what
           the till lacks on the whole order first, so the door trims for the capital only what their issue left
           uncovered. That trim is caused where the issue falls short, CreditCrunchCheck 3. Here the window must hold
           such an order, and each must have been offered whole: what consider() offered (Equity's offered this
           month, the ask itself) against what the till lacked - not the desk's test asked again here (REV-MC1-2). */
        int crunched = 0, shut = 0, offeredWhole = 0, offeredShort = 0;
        String firstWhole = null, firstShort = null;
        for (Ask a : asks) {
            if (!a.shortBeforeOwners) continue;
            crunched++;
            if (a.quoteShut) { shut++; continue; }
            double lacking = a.cost - a.held;
            boolean whole = a.offered >= lacking - 1e-6 * Math.max(1, Math.abs(lacking));
            String said = String.format(Locale.ROOT, "m%d: order %.0f, the till %.0f, offered %.0f, put in %.0f - %s",
                    a.month, a.cost, a.held, a.offered, a.raised, a.line);
            if (whole) { offeredWhole++; if (firstWhole == null) firstWhole = said; }
            else { offeredShort++; if (firstShort == null) firstShort = said; }
        }
        out.printf("   %d order(s) the door would have refused for the bank's capital: %d offered to the owners whole,"
                + " %d offered less, %d behind a quote that supported no issue%n", crunched, offeredWhole, offeredShort, shut);
        if (firstWhole != null) out.println("   first offered whole " + firstWhole);
        if (firstShort != null) out.println("   first offered less " + firstShort);
        check("fixture: the window holds an order the door would have refused for the bank's capital, put to the owners"
                + " (MC1)", offeredWhole + offeredShort > 0);
        check("...and each was offered to the owners whole: what the till lacked on the order, not only its down payment",
                offeredShort == 0);
        check("...and no month the door trimmed is left marked as held for the bank's capital", leftMarked == 0);

        out.println("\n--- 4. LongPlaytest's watch, and the homes that finished by size ---");
        StringBuilder by = new StringBuilder();
        for (int s = 1; s < finished.length; s++) {
            if (finished[s] > 0) by.append(String.format(" size %d (%s): %,d;", s,
                    s > FamilyModel.STUDIO_MAX_SIZE ? "family" : "studio", finished[s]));
        }
        out.printf("   homes finished in months %d-%d:%s%n", FROM + 1, TO, by.length() > 0 ? by.toString() : " none");
        out.printf(Locale.ROOT, "   the most households with no door: %.3f (month %d)%n", worstUnplaced[0], worstMonth[0]);
        if(worstUnplaced[0]>1)out.print(worstHousing[0]);
        check("no month of the window leaves more than one household with no door (LongPlaytest's watch)",
                worstUnplaced[0] <= 1);
        out.println("   building behind the town's households, the order trimmed or held at the door or for land, " + behind.text());
        out.println("   building keeping up, " + keptUp.text());
        out.println("   of them held for land, " + forLand.text());
        out.printf(Locale.ROOT, "   the boom, m%d: %.3f households sharing a door, %.0f people overcrowded; %s%n", BOOM, boom[0], boom[1], boomLine[0]);
        out.printf(Locale.ROOT, "   of the households sharing, at most %.3f past the census line, overcrowded%n", windowPast[0]);
        out.printf(Locale.ROOT, "   the households outgrowing the homes, %d months: sharing rose %.3f households over them (%.4f a month)%n",
                grew[0], rose[0], grew[0] > 0 ? rose[0] / grew[0] : 0);
        out.printf(Locale.ROOT, "   the homes catching up, %d months: it rose %.3f over them (%.4f a month)%n",
                grew[1], rose[1], grew[1] > 0 ? rose[1] / grew[1] : 0);
        // Restated (REV-RES3b P1-4): "households live overcrowded in the window, counted past the census line" read every
        // household overcrowding() placed as overcrowded; within two persons a room it is sharing, not overcrowding.
        check("households share a door in the window, beside another family or past the census line, none of them doorless (call A)",
                Math.max(behind.most, keptUp.most) > 0);
        check("fixture: the households outgrew the homes in some months of the window and the homes caught up in others",
                grew[0] > 0 && grew[1] > 0);
        out.printf(Locale.ROOT, "   the coda: a fire takes %d structures of the housing at m%d, %d months played after; at most %.3f households overcrowded, %.3f with no door%n",
                coda[1], coda[0], coda[2], codaMost[0], codaMost[1]);
        out.printf(Locale.ROOT, "   payers boarding with a housed family (no door standing): %d months, at most %.3f households (m%.0f); counted by the census line in %d, board paid and received in %d (at most %.2f a month), the month's own audit exact in %d%n",
                board[0], boardMost[0], boardMost[1], board[1], board[3], boardMost[2], board[2]);
        out.println("   saved and loaded with boarders standing: " + tripLine[0]);
        check("fixture: households that could pay found no door standing and boarded with a housed family (call A over R1g)", coda[1] > 0 && board[0] > 0);
        check("...every boarder counted by the census's own line, overcrowded past two persons a room and boarding within it (REV-RES3b P1-4)", board[0] > 0 && board[1] == board[0]);
        check("...paying its hosts board, every cent paid out of the boarders' cells received into their hosts' (REV-RES3b P1-1)", board[0] > 0 && board[3] == board[0]);
        check("...and the month's own money audit and the households' cash identity exact in every month of them (REV-RES3b P1-3)", board[0] > 0 && board[2] == board[0]);
        check("a save and load with boarders standing brings back every boarder, its count by the line and its board (REV-RES3b P1-2)", trip[0] >= 0 && trip[1] == 1 && trip[2] == 1);
        check("the fire's displaced live past the census line, more than two persons a room, overcrowded and never doorless (call A, REV-RES3b P1-4)",
                codaMost[0] > 0 && codaMost[1] <= 1);
        // Restated (REV-RES3b P1-4) from "overcrowding rises over the months ...": the census's families more than its houses.
        check("sharing rises over the months the town's households outgrow its homes", rose[0] > 0);
        check("...by more a month than over the months the homes catch up",
                grew[0] > 0 && grew[1] > 0 && rose[0] / grew[0] > rose[1] / grew[1]);

        out.println("\n--- 5. the households' identity through the mortgage door (HL1) ---");
        out.printf("   %d months, %d open; worst %s%n", householdMonths[0], householdMonths[1], worstHouseholds[0]);
        check("the households' cash identity closes every played month, beside the landlords' trimmed mortgages",
                householdMonths[0] > TO - FROM && householdMonths[1] == 0);

        out.println(fails == 0 ? "\nHousingDoorCheck: all passed" : "\nHousingDoorCheck: " + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** The door's own facts for one of the homes Mortgage.decide() was asked about, read at the moment it was asked. */
    static Ask ask(Game g, long asked, java.util.function.LongToDoubleFunction costOf, double cash, Mortgage.Decision found) {
        Ask a = new Ask();
        a.asked = asked;
        a.passed = found.quantity();
        a.till = cash;
        // MC1: the till before the owners' issue, and the door's capital test on the order's mortgage there (ownersAskOnMortgage()).
        a.cost = costOf.applyAsDouble(Math.max(1, asked));
        a.raised = g.getEconomyManager().getEquityRaised(RE);
        a.held = cash - a.raised;
        a.shortBeforeOwners = g.getEconomyManager().getBusinessDebtManager()
                .mortgageShortOfCapital(RE, a.cost - Math.max(a.held, Mortgage.ownFundsFor(a.cost)));
        int company = Equity.indexOf(RE);
        a.offered = company >= 0 ? g.getEquity().getOfferedThisMonth(company) : 0;
        a.quoteShut = company >= 0 && !g.getExchange().quoteSupportsIssue(company);
        double one = costOf.applyAsDouble(1);
        for (BuildingsTemplate t : g.getBuildingManager().getTemplatesBySector(RE)) {
            if (t.getCapacity() <= 0) continue;
            LandManager.Use use = Game.groundUse(t);
            // What buildFor() charges: the builders' quote and the ground at its zone's price.
            double doorOne = g.quoteBuild(t, 1).total + g.getLandManager().priceFor(t.getLandSqFt(), t.getSector(), use);
            // The lender's price of one is the door's since FB15 (HN1-A, Game.doorPriceOf()), the planner's before: matched
            // on the planner's alone, every order read as no building, no segment short and no door (REV-FB15).
            if (doorOne != one && g.getBusinessInvestment().getCostOf(t, 1) != one) continue;
            a.building = t.getName();
            a.family = t.homeSize() > FamilyModel.STUDIO_MAX_SIZE;
            a.doorOne = doorOne;
            a.groundForOne = g.getLandManager().canAllocate(t.getLandSqFt(), t.getSector(), use);
            a.shortfall = g.getSectors().realEstate().doorShortfall(a.family);
            return a;
        }
        a.building = "?";
        a.doorOne = Double.POSITIVE_INFINITY;
        return a;
    }

    /** How many the advisor's line says were built: "Built 4 Low-Rise Apartments ...", or none. */
    static long builtIn(String line) {
        if (line == null || !line.startsWith("Built ")) return 0;
        String n = line.substring(6, line.indexOf(' ', 6)).replace(",", "");
        try { return Long.parseLong(n); } catch (NumberFormatException e) { return 0; }
    }

    /** A failed played finding keeps the actual budgets, occupancy and posted rents that caused it. */
    private static String housingSnapshot(Game g){
        var market=g.getSectors().realEstate().housingMarket();var result=market.result();
        StringBuilder text=new StringBuilder(String.format(Locale.ROOT,"   typed result kept %.3f normal %.3f emergency %.3f poor %.3f shortage %.3f; %s%n",result.kept(),result.suitable(),result.crowded(),result.unaffordable(),result.noSuitable(),g.getLastInvestment(RE)));
        for(var d:market.demands()){
            double taken=market.leases().stream().filter(l->l.cell().equals(d.key())).mapToDouble(HousingChoice.Lease::households).sum();
            if(d.count()-taken>1)text.append(String.format(Locale.ROOT,"     %s: %.3f missing of %.3f, budget %.6f, size %d children %d%n",d.key(),d.count()-taken,d.count(),d.budget(),d.size(),d.children()));
        }
        for(var c:market.classes())if(c.homes>0)text.append(String.format(Locale.ROOT,"     class %d/%s: %.3f doors, %.3f occupied, ask %.6f, carry %.6f%n",c.recipe,c.dwelling,c.homes,c.occupied,c.ask,c.carry));
        return text.toString();
    }

    /* =====================================================================
       THE FOUNDING AND THE RHYTHM - the ensemble's foundAndPlay(), line for
       line (the driver in tools/ensemble/), the AUTO2 policy's setups, to TO.
       ===================================================================== */

    static boolean stuck;

    /**
     * A month's payers boarding with the housed (call A over R1g): [0] the months with any, [1] of them every boarder
     * counted by the census's own line on its door, overcrowded past two persons a room and within the line otherwise
     * (REV-RES3b P1-4), [2] of them the month's own money audit and the households' cash identity exact (P1-3: an audit
     * of an earlier month is no witness), [3] of them the board paid out of the boarders' cells received into their
     * hosts' to the audit's tolerance (P1-1) - no landlord billed, the board a transfer between households.
     */
    static void boarders(Game g, MoneyAudit.Households households, int[] board, double[] boardMost) {
        var market = g.getSectors().realEstate().housingMarket();
        double boarding = market.boarders();
        if (!(boarding > 0)) return;
        board[0]++;
        boolean counted = true;
        for (var e : market.boardingByCell().entrySet()) {
            double both = market.overcrowdedByCell().getOrDefault(e.getKey(), 0.0) + market.withinTheLineByCell().getOrDefault(e.getKey(), 0.0);
            if (e.getValue() > both + FamilyModel.packingRounding(both)) counted = false;
        }
        if (counted) board[1]++;
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        boolean now = audit != null && households != null && audit.month == g.getMonth() && households.month == g.getMonth();
        boolean exact = now && !(Math.abs(audit.residual) > .01 && audit.relative() > 1e-7) && !(Math.abs(g.getPostAuditDrift()) > .01);
        if (exact && LongPlaytest.MoneyFixtures.householdsClose(households)) board[2]++;
        double paid = market.boardPaid(), received = market.boardReceived();
        if (paid > 0 && Math.abs(paid - received) <= MoneyAudit.tolerance(paid)) board[3]++;
        if (boarding > boardMost[0]) { boardMost[0] = boarding; boardMost[1] = g.getMonth(); }
        boardMost[2] = Math.max(boardMost[2], paid);
    }

    /**
     * The city saved to its own scratch folder's slot 10 and loaded into another Game, as a player's reload, with boarders
     * standing (REV-RES3b P1-2): [1] 1 if the boarders, the overcrowded, those within the line and the board paid and
     * received came back as saved, [2] 1 if the loaded market's maps equal the running one's, cell by cell.
     */
    static String roundTrip(Game g, int[] trip) {
        var market = g.getSectors().realEstate().housingMarket();
        trip[0] = g.getMonth();
        g.saveGame(10);
        Game back = new Game(g.getGameFiles());
        back.loadGameSave(10);
        var loaded = back.getSectors().realEstate().housingMarket();
        boolean same = loaded.boarders() == market.boarders() && loaded.overcrowded() == market.overcrowded()
                && loaded.withinTheLine() == market.withinTheLine() && loaded.boardPaid() == market.boardPaid()
                && loaded.boardReceived() == market.boardReceived() && back.getMonth() == g.getMonth();
        boolean maps = loaded.boardingByCell().equals(market.boardingByCell()) && loaded.overcrowdedByCell().equals(market.overcrowdedByCell())
                && loaded.withinTheLineByCell().equals(market.withinTheLineByCell()) && loaded.boardPaidByCell().equals(market.boardPaidByCell())
                && loaded.boardReceivedByCell().equals(market.boardReceivedByCell());
        trip[1] = same ? 1 : 0;
        trip[2] = maps ? 1 : 0;
        return String.format(Locale.ROOT, "m%d, %.3f boarders (loaded %.3f), %.3f overcrowded (%.3f), %.3f within the line (%.3f), board %.2f paid (%.2f) and %.2f received (%.2f)",
                g.getMonth(), market.boarders(), loaded.boarders(), market.overcrowded(), loaded.overcrowded(), market.withinTheLine(), loaded.withinTheLine(),
                market.boardPaid(), loaded.boardPaid(), market.boardReceived(), loaded.boardReceived());
    }
    static void run(Game g, int months, Runnable after) {
        for (int i = 0; i < months && !stuck && g.getMonth() < TO; i++) {
            int before = g.getMonth();
            DebtManager dm = g.getDebtManager();
            if (dm.getInflationTarget() != TARGET) dm.setInflationTarget(TARGET);
            if (!dm.isAutopilot()) dm.setAutopilot(true);
            LongPlaytest.runPlayer(g, 1);
            if (g.getMonth() == before) stuck = true;
            after.run();
        }
    }

    static void foundAndPlay(Game g, Runnable after) {
        int seed = SEED;
        g.getDebtManager().setInflationTarget(TARGET);
        g.getDebtManager().setAutopilot(true);
        g.setRolloverMode(LongPlaytest.ROLLOVER);
        g.setRescueMode(LongPlaytest.RESCUE_AUTO ? TreasuryFund.RescueMode.AUTOMATIC : TreasuryFund.RescueMode.BUTTON);
        g.setFundDial(LongPlaytest.FUND_DIAL);
        LongPlaytest.seedMarket(g, seed);
        if (LongPlaytest.FOUNDING == Founding.Preset.INSANE) LongPlaytest.borrowForTheVillage(g);
        LongPlaytest.startAutomaticWorks(g);
        LongPlaytest.villageBuild(g, "House", 40 + seed % 4);
        LongPlaytest.villageBuild(g, "Convenience Store", 3);
        LongPlaytest.villageBuild(g, "Mixed Farm", 2);
        run(g, 3 + (seed / 4) % 3, after);
        LongPlaytest.villageBuild(g, "House", 20 + (seed / 12) % 3);
        run(g, 4, after);
        LongPlaytest.villageBuild(g, "Convenience Store", 2);
        LongPlaytest.villageBuild(g, "Construction Depot", 1);
        run(g, 5, after);
        LongPlaytest.advise(g);
        run(g, 6, after);
        LongPlaytest.ensureSchools(g);

        int stop = 0;
        while (g.getMonth() < TO && !stuck) {
            stop++;
            int skip = switch (stop % 6) {
                case 0 -> 100;
                case 1 -> 12;
                case 2 -> 24;
                case 3 -> 60;
                case 4 -> 6;
                default -> 120;
            };
            skip = Math.min(skip, LongPlaytest.longestSkip());
            run(g, skip, after);
            if (g.getMonth() >= TO || stuck) break;
            LongPlaytest.ensureSchools(g);
            for (int move = 0; move < LongPlaytest.movesPerLook(); move++) {
                if (LongPlaytest.advise(g) == null) break;
                run(g, 1, after);
            }
            run(g, 2, after);
            if (stop % 19 == 0) {
                DebtManager m = g.getDebtManager();
                if (m.foreignWindowOpen() && m.foreignRate() < m.getRate()) {
                    double ask = Math.max(5_000, g.getEconomyManager().getMonthGdp() * .5
                            / Math.max(.01, LongPlaytest.fxRate(g)));
                    if (LongPlaytest.BORROW_AT_HOME) {
                        g.handleLongBondLogic(ask * Math.max(.01, LongPlaytest.fxRate(g)), 20, 100);
                    } else {
                        g.handleForeignLogic("Term", ask, 20, 100, false);
                    }
                }
            }
            if (stop % 13 == 0) {
                LandParcel spare = g.getLandManager().getMarket().bestValue();
                if (spare != null && spare.localPrice(g.getForeignAccounts().getRate()) < g.getCash() * .1) {
                    g.buyLandParcel(spare.getId());
                }
            }
            if (stop % 19 == 0) {
                BuildingsTemplate house = LongPlaytest.template(g, "House");
                if (house != null && g.getBuildingManager().getQuantity(house.getId()) > 60) {
                    g.getBuildingManager().retire(house, 10);
                }
            }
        }
    }
}

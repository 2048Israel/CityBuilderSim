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
 *      with no door - and the homes that finish are counted by size;
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
        long[] finished = new long[16];
        // HL1: the same played path must close the households' cash books, including accepted trimmed orders.
        int[] householdMonths = { 0, 0 };
        MoneyAudit.Households[] worstHouseholds = { MoneyAudit.Households.NONE };
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
                if (still > worstUnplaced[0]) { worstUnplaced[0] = still; worstMonth[0] = m; }
                if (pending != null) {
                    pending.month = m;
                    pending.line = g.getLastInvestment(RE);
                    pending.built = builtIn(pending.line);
                    pending.markedForCapital = g.getEconomyManager().getBusinessDebtManager().wasMortgageRefusedForCapital(RE);
                    asks.add(pending);
                    pending = null;
                }
            });
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
        check("no month of the window leaves more than one household with no door (LongPlaytest's watch)",
                worstUnplaced[0] <= 1);

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

    /* =====================================================================
       THE FOUNDING AND THE RHYTHM - the ensemble's foundAndPlay(), line for
       line (the driver in tools/ensemble/), the AUTO2 policy's setups, to TO.
       ===================================================================== */

    static boolean stuck;

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

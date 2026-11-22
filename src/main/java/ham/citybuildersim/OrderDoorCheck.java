package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Locale;

/**
 * An order at the door (FB15, 2026-11-20): a landlord's order sized, and its
 * owners asked, at the price buildFor() charges; a project's order the lender
 * says no to asked again of half, down to one.
 *
 * WHY: HN1 left two rows (docs/queue/HN1-notes.md, section 6). HN1-A: while
 * the city buys materials in, the planner prices a landlord's home 2-5% under
 * the door (BusinessInvestment.getCostOf() at the builders' materials price,
 * buildFor() at the market's draw), and consider() raised the owners' money to
 * the plan's down payment exactly, so the door said "needs more of its own for
 * the 15% down payment" even on one block, and an order of more lost half of
 * it. HN1-B: consider()'s order with no mortgage was still all or nothing at
 * the door on the bank's capital, where HN1 had made the landlords' a halving.
 *
 * The city is the ensemble's AUTO2 seed 12, founded and played as the
 * ensemble plays it, to month FROM. There the two conditions of HN1-A's edge
 * that are prices are set by hand (FS9): the yard left YARD_SHARE of a block's
 * materials, and the market's materials, made here and imported, at DEAR over
 * the planner's world price (GoodsMarket.setLocalPrice(), setWorldPrices()).
 * WHY: the harness searched m200-420 for the first month the played landlord
 * stood at the edge, and on HT1's path (0.9.184) none did: the door was at or
 * under the planner in 170 of 220 months, the yard holding a block or the
 * market's materials cheaper than the world's. Each order is then put to
 * Game.consider() itself, by reflection (as BankFailureCheck puts one to
 * retire()), between months, on the till the fixture sets.
 *
 * What this has to prove:
 *   1. the fixture: a month whose landlord is past its first year and asks its
 *      register for nothing on two blocks, holds nothing abroad, whose quote
 *      supports an issue, whose yard is short of a block's materials so the
 *      door's price of one is over the planner's (both set by hand, the
 *      market's price read back at the door's draw), the lender's test passing
 *      two on the down payment at the door's price and the bank not short of
 *      capital for them; and a shopkeeper whose register and money abroad
 *      bring its till at the door to under half a store. Ground for three
 *      blocks, and later for SIX stores, is bought a plot at a time, as the
 *      ensemble's player buys it (Game.buyLandBlock());
 *   2. HN1-A: the order is priced as the door charges (the order's price of
 *      one and of two, the build card's lender test); an order of one, the
 *      till empty and the owners asked for its down payment, is built on a
 *      mortgage, not held at the door; an order of two the same way is built
 *      whole, not halved at the door;
 *   3. HN1-B: an order of SIX stores whose slice carries its interest, the
 *      bank's capital rule letting no borrower's debt grow and the till
 *      holding one at the door's price but not six, is built in part: the
 *      halving's largest the till buys outright, paid at the door's price, in
 *      the advisor's words, and not left marked as refused for capital; from
 *      the other side, a till short of one is held whole, in the bank's words;
 *      and with the bank shut, as past the default point, the order is held
 *      whole with the till holding one, as on main (REV-FB15: there the till is
 *      the sector's last money).
 */
public class OrderDoorCheck {

    static PrintStream out;
    static int fails;

    /** The ensemble's seed HN1's harness plays (HousingDoorCheck.SEED). */
    static final int SEED = 12;
    /** The month the city is played to, the edge's prices then set by hand (FS9: no search). */
    static final int FROM = 200;
    /** ★ The yard the fixture leaves, a share of one block's materials: short of a block, as main's own edge was (204 of 377 at m206). */
    static final double YARD_SHARE = .5;
    /** ★ The market's materials at the door over the planner's world price, set by hand: within HN1-A's measured 2-5%. */
    static final double DEAR = .035;
    /** The ensemble's AUTO2: the autopilot at this target, every month (Ensemble.TARGET). */
    static final double TARGET = .02;
    static final String RE = Sectors.REAL_ESTATE;
    static final String BLOCK = "Low-Rise Apartments", STORE = "Convenience Store";
    /** The project's order, and its till in stores at the door's price: one, and not two. */
    static final long SIX = 6;
    static final double TILL = 1.5, SHORT_TILL = .5;
    /** The plots the fixture may buy for its ground. */
    static final int MAX_PLOTS = 60;

    static void check(String label, boolean ok) {
        out.println((ok ? "  ok   " : "  FAIL ") + label);
        if (!ok) fails++;
    }

    static boolean near(double a, double b) {
        return Math.abs(a - b) <= 1e-9 * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }

    /** What buildFor() charges for n of these, read as HousingDoorCheck reads the door: the quote and the plot. */
    static double door(Game g, BuildingsTemplate t, long n) {
        return g.quoteBuild(t, n).total + g.getLandManager().priceFor(t.getLandSqFt() * n, t.getSector(), Game.groundUse(t));
    }

    static boolean ground(Game g, BuildingsTemplate t, long n) {
        return g.getLandManager().canAllocate(t.getLandSqFt() * n, t.getSector(), Game.groundUse(t));
    }

    /** What its register would ask its owners for on this cost (Equity.raiseFor()); nothing for a sector not listed. */
    static double raise(Game g, String sector, double cost) {
        int c = Equity.indexOf(sector);
        if (c < 0) return 0;
        SectorBooks.SectorMonth books = g.getSectorBooks().get(sector);
        return g.getEquity().raiseFor(c, books.totalAssets(), books.equity(), cost);
    }

    /** Past its first year and asking its register for nothing on this cost. */
    static boolean raisesNothing(Game g, String sector, double cost) {
        int c = Equity.indexOf(sector);
        Equity.Regime r = c < 0 ? null : g.getEquity().getRegime(c);
        return (r == Equity.Regime.NORMAL || r == Equity.Regime.BAD) && raise(g, sector, cost) == 0;
    }

    /** The lender's test of n blocks with the down payment at the door's price in hand, as lenderReads() asks it. */
    static boolean lenderPasses(Game g, BuildingsTemplate t, long n) {
        Game.LenderTest lt = g.lenderTest(RE, t);
        BusinessInvestment bi = g.getBusinessInvestment();
        return Mortgage.decide(n, k -> door(g, t, k), Mortgage.ownFundsFor(door(g, t, n)), lt.noi(), lt.tested(),
                k -> bi.carryOf(t, k, lt.insured())).quantity() == n;
    }

    /** HN1-A's landlord at its edge, its ground apart (bought: buyGround()), read and never acted on. */
    static boolean landlordAtTheEdge(Game g) {
        BuildingsTemplate block = LongPlaytest.template(g, BLOCK);
        if (block == null) return false;
        EconomyManager em = g.getEconomyManager();
        BusinessDebtManager credit = em.getBusinessDebtManager();
        int c = Equity.indexOf(RE);
        double one = door(g, block, 1), two = door(g, block, 2);
        return c >= 0 && raisesNothing(g, RE, one) && raisesNothing(g, RE, two) && g.getExchange().quoteSupportsIssue(c)
                && em.getForeignAssets(RE) == 0 && credit.isLendingOpen() && !credit.isBorrowingBlocked(RE)
                && one > g.getBusinessInvestment().getCostOf(block, 1) * (1 + 1e-6)
                && lenderPasses(g, block, 1) && lenderPasses(g, block, 2)
                && !credit.mortgageShortOfCapital(RE, two - Mortgage.ownFundsFor(two));
    }

    /** What the shopkeeper's register raises on SIX and it holds abroad: what reaches its till before the door. */
    static double shopBrings(Game g, BuildingsTemplate store) {
        String shop = store.getSector();
        return raise(g, shop, g.getBusinessInvestment().getCostOf(store, SIX)) + g.getEconomyManager().getForeignAssets(shop);
    }

    /** A shopkeeper HN1-B's order can be put to: lending open, not banned, and bringing under half a store to its till. */
    static boolean shopkeeperReady(Game g) {
        BuildingsTemplate store = LongPlaytest.template(g, STORE);
        if (store == null) return false;
        BusinessDebtManager credit = g.getEconomyManager().getBusinessDebtManager();
        return credit.isLendingOpen() && !credit.isBorrowingBlocked(store.getSector())
                && shopBrings(g, store) <= SHORT_TILL * door(g, store, 1);
    }

    /** Free ground for n of these, bought the cheapest plot at a time while the city can pay; how many plots it took. */
    static int buyGround(Game g, BuildingsTemplate t, long n) {
        int bought = 0;
        while (!ground(g, t, n) && bought < MAX_PLOTS && g.buyLandBlock()) bought++;
        return bought;
    }

    /**
     * HN1-A's two prices, by hand: the yard left YARD_SHARE of a block, and the market's materials, the makers' and the
     * world's landed, at DEAR over what the planner prices them at (BuildingManager.getConstructionMaterialPrice()). The
     * landed price's freight moves with the world price it is set from (bandFreight()), so the set is repeated to its fixed point.
     */
    static void shortAndDear(Game g, BuildingsTemplate block) {
        g.getBuildingManager().setConstructionMaterials((long) (block.getConstructionMaterials() * YARD_SHARE));
        GoodsMarket mat = g.getMarkets().get(Good.MATERIALS);
        double dear = dearPrice(g);
        mat.setLocalPrice(dear);
        for (int i = 0; i < 8; i++) mat.setWorldPrices(dear / mat.getExchangeRate() - mat.freightChange(), mat.getWorldExportSet());
    }

    /** What the fixture prices the market's materials at: DEAR over the planner's price. */
    static double dearPrice(Game g) {
        return g.getBuildingManager().getConstructionMaterialPrice() * (1 + DEAR);
    }

    /** Each condition of the edge, printed, so a fixture that misses it says which. */
    static void edgeParts(Game g, BuildingsTemplate block) {
        EconomyManager em = g.getEconomyManager();
        BusinessDebtManager credit = em.getBusinessDebtManager();
        int c = Equity.indexOf(RE);
        double one = door(g, block, 1), two = door(g, block, 2);
        out.printf(Locale.ROOT, "  a block at the door %.4f (the quote %.4f), the planner %.4f%n", one, g.quoteBuild(block, 1).total, g.getBusinessInvestment().getCostOf(block, 1));
        out.printf("  the edge's parts: ground for three %b; listed %b, regime %s, raises nothing on one %b and two %b;"
                        + " quote supports an issue %b; nothing abroad %b; lending open and not banned %b; door over planner %b;"
                        + " lender passes one %b and two %b; bank not short for two %b; shopkeeper ready %b%n",
                ground(g, block, 3), c >= 0, c < 0 ? "-" : g.getEquity().getRegime(c), raisesNothing(g, RE, one),
                raisesNothing(g, RE, two), c >= 0 && g.getExchange().quoteSupportsIssue(c), em.getForeignAssets(RE) == 0,
                credit.isLendingOpen() && !credit.isBorrowingBlocked(RE),
                one > g.getBusinessInvestment().getCostOf(block, 1) * (1 + 1e-6), lenderPasses(g, block, 1),
                lenderPasses(g, block, 2), !credit.mortgageShortOfCapital(RE, two - Mortgage.ownFundsFor(two)), shopkeeperReady(g));
    }

    /** What the landlord's order told the door: asked, its price of one and of the order, the till, what the lender passed. */
    static long asked, passed;
    static double priceOne, priceAsked, tillAtOrder;
    /** What the project's order told the door: the till, and the slice that carries its interest. */
    static double tillAtProject;
    static long carried;

    static void consider(Game g, BusinessInvestment.Decision d) throws Exception {
        Method investor = Game.class.getDeclaredMethod("sectorInvestor", String.class);
        investor.setAccessible(true);
        Method consider = Game.class.getDeclaredMethod("consider", BusinessInvestment.Decision.class, Investor.class);
        consider.setAccessible(true);
        consider.invoke(g, d, investor.invoke(g, d.sector));
    }

    public static void main(String[] args) throws Exception {
        out = System.out;
        Locale.setDefault(Locale.ROOT);   // as the ensemble plays it; the advisor's counts read "1,234"
        PrintStream quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        // LongPlaytest's founding reads its seed here, as the ensemble sets it; each harness has its JVM.
        System.setProperty("playtest.seed", Integer.toString(SEED));
        GameFiles files = GameFiles.scratch("orderdoor");
        final Game g;
        PrintStream was = LongPlaytest.out;
        LongPlaytest.out = quiet;
        System.setOut(quiet);
        boolean played;
        int groundPlots = 0;
        try {
            g = new Game(files, LongPlaytest.played(null));
            g.run();
            foundAndPlay(g);
            played = !stuck && g.getMonth() == FROM;
            if (played) {
                BuildingsTemplate block = LongPlaytest.template(g, BLOCK);
                groundPlots = buyGround(g, block, 3);
                shortAndDear(g, block);
                // The planner reads the land as the month's plan does, refreshed before it asks (Game.runPrivateInvestment()):
                // after the plots, or it prices the ground bought as ground still to buy (as CreditCrunchCheck 4 does).
                Method refreshLand = Game.class.getDeclaredMethod("refreshLand");
                refreshLand.setAccessible(true);
                refreshLand.invoke(g);
            }
        } finally {
            LongPlaytest.out = was;
            System.setOut(out);
        }
        g.watchOrders(new BusinessInvestment.OrderWatch() {
            @Override public void invested(BusinessInvestment.Decision decision, double cash, double perUnitProfit, Game.Afford found) {
                tillAtProject = cash;
                carried = found.quantity();
            }
            @Override public void mortgaged(long a, java.util.function.LongToDoubleFunction costOf, double cash,
                                            double noiPerUnit, double annualRate, Mortgage.Decision found) {
                asked = a;
                priceOne = costOf.applyAsDouble(1);
                priceAsked = costOf.applyAsDouble(a);
                tillAtOrder = cash;
                passed = found.quantity();
            }
        });

        out.println("--- 1. the fixture: a landlord at HN1-A's edge, and a shopkeeper ---");
        BuildingsTemplate block = LongPlaytest.template(g, BLOCK), store = LongPlaytest.template(g, STORE);
        check(String.format("fixture: the ensemble's seed %d played to FROM (m%d, at m%d)", SEED, FROM, g.getMonth()), played);
        if (!played) { finish(); return; }
        out.println("  played city wires " + SubstationCheck.gridReading(g));
        UtilitiesHandler.Book power = g.getServicesManager().getUtilitiesHandler().getStruckBook();
        out.printf("  billed power: %.3f kWh served, %.3f free founding grid, %.3f fuelled plants, %.3f wind, %.3f hydro, %.3f works; fuel %s; highest block price %.9f%n",
                power.servedKwh, power.gridKwh, power.fuelledKwh, power.windKwh, power.hydroKwh, power.worksKwh,
                power.fuel, java.util.Arrays.stream(power.price).max().orElse(0));
        check("fixture: the played city has the wire capacity its billed and closing peaks need at the order-door month",
                SubstationCheck.gridCovered(g));
        GoodsMarket mat = g.getMarkets().get(Good.MATERIALS);
        out.printf("  %d plots bought for three blocks; the yard left %,d; the materials at %.6f made here and %.6f landed,"
                        + " against the planner's %.6f%n", groundPlots, g.getBuildingManager().getConstructionMaterials(),
                mat.getLocalPrice(), mat.importPrice(), g.getBuildingManager().getConstructionMaterialPrice());
        check("fixture: the market's materials, made here and landed, at DEAR over the planner's price, by hand",
                near(mat.getLocalPrice(), dearPrice(g)) && near(mat.importPrice(), dearPrice(g)));
        edgeParts(g, block);
        boolean edge = ground(g, block, 3) && landlordAtTheEdge(g) && shopkeeperReady(g);
        check("fixture: there the landlord stands at HN1-A's edge, with ground for three blocks, and the shopkeeper is ready", edge);
        if (!edge) { finish(); return; }
        EconomyManager em = g.getEconomyManager();
        BusinessDebtManager credit = em.getBusinessDebtManager();
        double plan1 = g.getBusinessInvestment().getCostOf(block, 1), door1 = door(g, block, 1);
        out.printf("  a Low-Rise Apartments block: the planner's %.4f, the door's %.4f (%+.2f%%), the yard %,d of %,d materials%n",
                plan1, door1, (door1 / plan1 - 1) * 100, g.getBuildingManager().getConstructionMaterials(),
                (long) block.getConstructionMaterials());
        check("the planner's price of one is under the door's: the yard is short, the materials bought in",
                g.getBuildingManager().getConstructionMaterials() < block.getConstructionMaterials() && door1 > plan1);
        check("...by more than the door's tolerance on the down payment", Mortgage.ownFundsFor(door1) > Mortgage.ownFundsFor(plan1) * (1 + 1e-6));

        out.println("--- 2. HN1-A: a landlord's order at the door's own price ---");
        for (long n = 1; n <= 2; n++) {
            em.setSectorCash(RE, 0);
            double doorN = door(g, block, n), raisedBefore = em.getEquityRaised(RE);
            int written = credit.getMortgagesWrittenThisMonth();
            asked = passed = 0;
            consider(g, new BusinessInvestment.Decision(RE, block, n, "FB15's order of " + n, true));
            String line = g.getLastInvestment(RE);
            out.printf("  an order of %d: price %.4f against the door's %.4f; the owners put in %.4f, the down payment at"
                    + " that price %.4f; the lender passed %d; \"%s\"%n", n, priceAsked, doorN,
                    em.getEquityRaised(RE) - raisedBefore, Mortgage.ownFundsFor(priceAsked), passed, line);
            check("fixture: the owners put in the order's down payment, all the till held at the order (" + n + ")",
                    asked == n && near(tillAtOrder, Mortgage.ownFundsFor(priceAsked))
                            && near(em.getEquityRaised(RE) - raisedBefore, tillAtOrder) && passed == n);
            if (n == 1) check("the order's price of one is the door's", near(priceOne, door1));
            else check("the order's price of two is the door's", near(priceAsked, doorN));
            check(n == 1 ? "an order of one, its down payment raised to the order's own figure, is built on a mortgage at the door"
                            : "an order of two the same way is built whole, not halved at the door",
                    credit.getMortgagesWrittenThisMonth() == written + 1
                            && line.startsWith(String.format("Built %d %s on an insured mortgage", n, BLOCK))
                            && !line.contains("trimmed"));
        }
        Game.LenderTest card = g.lenderTest(RE, block);
        check("the build card's lender test reads the door's price of one, as the order does",
                card != null && near(card.cost(), door(g, block, 1)));

        out.println("--- 3. HN1-B: a project's order the lender says no to, asked again of half ---");
        String shop = store.getSector();
        int plots = buyGround(g, store, SIX);
        credit.setCapitalRule(0, false);
        double one = door(g, store, 1);
        double[] doors = new double[(int) SIX + 1];
        for (int k = 1; k <= SIX; k++) doors[k] = door(g, store, k);
        // The till is set so that, with what its register raises and it brings home, it holds TILL stores at the door.
        em.setSectorCash(shop, Math.max(0, TILL * one - shopBrings(g, store)));
        boolean groundForSix = ground(g, store, SIX),
                markedBefore = credit.wasRefusedForCapital(shop) || credit.wasProjectRefusedForCapital(shop);
        carried = 0;
        tillAtProject = -1;
        consider(g, new BusinessInvestment.Decision(shop, store, SIX, "FB15's order of " + SIX, true));
        String line = g.getLastInvestment(shop);
        double till = tillAtProject, paid = till - em.getSectorCash(shop);
        long expect = SIX;
        while (expect > 1 && doors[(int) expect] > till) expect /= 2;
        out.printf("  %s: %d %s, one at the door %.4f, the till at the door %.4f (%d plots bought); paid %.4f; \"%s\"%n",
                shop, SIX, STORE, one, till, plots, paid, line);
        check("fixture: ground for six, the slice carried all six, and the till at the door held one at the door's price but not six",
                groundForSix && carried == SIX && till >= doors[1] && till < doors[(int) SIX] && !markedBefore);
        check("fixture: the bank's capital rule lets no borrower's debt grow this month",
                credit.getCapitalGrowth() == 0 && !credit.isKeepGoingOnly() && credit.capitalRoom(shop) < doors[(int) SIX] - till);
        check(String.format("the order is built in part: the halving's largest the till buys outright (%d)", expect),
                line.startsWith(String.format("Built %d %s (trimmed from %d - the bank is short of capital for a loan,"
                        + " so what its own funds buy outright)", expect, STORE, SIX)));
        check("...paid from the till at the door's price, with no loan", near(paid, doors[(int) expect]));
        check("...and the shopkeeper is not left marked as refused for capital, which the playtest counts",
                !credit.wasRefusedForCapital(shop) && !credit.wasProjectRefusedForCapital(shop));
        // REV-FB15: with the bank shut, as past the default point, the till is the sector's last money: held as on main.
        buyGround(g, store, SIX);
        em.setSectorCash(shop, Math.max(0, TILL * door(g, store, 1) - shopBrings(g, store)));
        boolean groundAgain = ground(g, store, SIX);
        credit.setLendingOpen(false);
        tillAtProject = -1;
        consider(g, new BusinessInvestment.Decision(shop, store, SIX, "FB15's order of " + SIX, true));
        credit.setLendingOpen(true);
        line = g.getLastInvestment(shop);
        double held = tillAtProject;
        out.printf("  the bank shut, with %.4f in the till at the door: \"%s\"%n", held, line);
        check("with the bank shut, the order is held whole, the till that holds one untouched, as on main (REV-FB15)",
                groundAgain && held >= door(g, store, 1) && !line.startsWith("Built") && near(em.getSectorCash(shop), held));
        em.setSectorCash(shop, Math.max(0, SHORT_TILL * door(g, store, 1) - shopBrings(g, store)));
        tillAtProject = -1;
        consider(g, new BusinessInvestment.Decision(shop, store, SIX, "FB15's order of " + SIX, true));
        line = g.getLastInvestment(shop);
        held = tillAtProject;
        out.printf("  with %.4f in the till at the door: \"%s\"%n", held, line);
        check("from the other side, a till short of one at the door is held whole, in the bank's words",
                held >= 0 && held < door(g, store, 1) && line.startsWith("Holding: the bank is rebuilding its capital")
                        && near(em.getSectorCash(shop), held));
        finish();
    }

    static void finish() {
        out.println(fails == 0 ? "\nOrderDoorCheck: all passed" : "\nOrderDoorCheck: " + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* =====================================================================
       THE ENSEMBLE'S FOUNDING (as HousingDoorCheck plays it), to the edge
       ===================================================================== */

    static boolean stuck;

    // ...and stopped at the month-end FROM, every later step a no-op (FS9: the edge is then caused, not searched for).
    static void run(Game g, int months) {
        for (int i = 0; i < months && !stuck && g.getMonth() < FROM; i++) {
            int before = g.getMonth();
            DebtManager dm = g.getDebtManager();
            if (dm.getInflationTarget() != TARGET) dm.setInflationTarget(TARGET);
            if (!dm.isAutopilot()) dm.setAutopilot(true);
            LongPlaytest.runPlayer(g, 1);
            if (g.getMonth() == before) stuck = true;
        }
    }

    static void foundAndPlay(Game g) {
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
        run(g, 3 + (seed / 4) % 3);
        LongPlaytest.villageBuild(g, "House", 20 + (seed / 12) % 3);
        run(g, 4);
        LongPlaytest.villageBuild(g, "Convenience Store", 2);
        LongPlaytest.villageBuild(g, "Construction Depot", 1);
        run(g, 5);
        LongPlaytest.advise(g);
        run(g, 6);
        LongPlaytest.ensureSchools(g);
        int stop = 0;
        while (g.getMonth() < FROM && !stuck) {
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
            run(g, skip);
            if (g.getMonth() >= FROM || stuck) break;
            LongPlaytest.ensureSchools(g);
            for (int move = 0; move < LongPlaytest.movesPerLook(); move++) {
                if (g.getMonth() >= FROM || LongPlaytest.advise(g) == null) break;
                run(g, 1);
            }
            run(g, 2);
            // FS9: the player acts on no month past FROM, so the city at FROM is the ensemble's at that month-end.
            if (g.getMonth() >= FROM || stuck) break;
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

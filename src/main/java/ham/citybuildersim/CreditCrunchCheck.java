package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * A credit crunch and the city's homes (MC1, 2026-11-20): a bank whose
 * leverage ratio binds, standing in its band between its leverage minimum and
 * its target, the landlords' insured mortgages refused at its door for its
 * capital, and the owners asked for what the till lacks on the order.
 *
 * WHY: CU2's 80-run ensemble found SHOCK seed 13 with up to 174 households
 * with neither a home nor one to double up in (m738-776, F1's one finding):
 * every month from m705 to m772 the landlords' plan asked for 20-33 Low-Rise
 * Apartments, and the door refused each order for the bank's capital
 * (BusinessDebtManager.canFundMortgage(), MORTGAGE_CAPITAL) while its leverage
 * ratio bound. One block was built in 70 months. The owners were asked only
 * for the down payment, which buys nothing when the bank lends nothing; HN1's
 * door buys outright only what the till already holds. Now, while the bank's
 * capital rule has no room for the order's mortgage, the owners are asked for
 * what the till lacks on the order (Game.consider(), ...AND THE WHOLE ORDER
 * WHILE THE BANK IS SHORT; docs/queue/MC1-notes.md has the three candidates).
 * This harness builds the crunch itself rather than waiting for a played path
 * to reach one: a desk and a bank by hand for the test, and a played city
 * whose bank its restore setters hold halfway down its leverage band, its
 * homes cut at the start to the doubling valve's last few households.
 *
 * WHY THE HOLD MOVES CASH AND DEPOSITS FROM ABROAD TOGETHER (FS9: the
 * played city moved under it with HN1-A, seed 13 standing at 483
 * households at m150 where it stood at 930): the desks' capital rule is
 * struck inside the month, in Game.startOfMonthUpdate() (strikeBankBonds(),
 * lendingGrowthLimit(), lendsOnlyToKeepBorrowersGoing(), leverageBinds()),
 * and the bank's equity and exposure hardly move between the hold before
 * the month and that strike. What had moved was the book: it weighs nearly
 * all of the bank's exposure, so the risk-based requirement (CAPITAL_RATIO
 * of the weighted book) was the larger of the two, and a hold by cash alone
 * set the leverage ratio to the band while the leverage requirement did not
 * bind: the rule was the risk weights', the bank under its risk minimum
 * and lending only to keep borrowers going, most months. The hold now
 * brings the exposure to BINDS_BY times what the risk weights ask (cash,
 * funded by deposits from abroad, which weigh nothing) and sets the equity
 * by the deposits, so the leverage requirement is the larger and the ratio
 * stands in its band at the strike. Game.nextMonth() sets the deposits
 * from abroad back to the hot money's stock after the strike, so the bank
 * a month ends on is not the one its rule was struck on: section 4 holds
 * and strikes again before its order.
 *
 * What this has to prove:
 *   1. the door's capital test asked first (mortgageShortOfCapital()) says
 *      what the door says, records nothing, and is no while the risk weights
 *      bind, the lender is shut, the landlord is banned or past its default
 *      point; the owners are asked for the whole order (ownersAskOnMortgage())
 *      only while it says short, on the loan the door is asked for once the
 *      down payment is in, never on the cost less a till under the down
 *      payment (REV-MC1);
 *   2. the fixture: a played city whose bank's leverage requirement is the
 *      larger and its ratio held in its band before every month of the crunch
 *      (so the rule the month strikes is the band's), its landlords asking for homes the
 *      bank has no capital to lend on, ground for GROUND_BLOCKS of their blocks
 *      stood before every month (FS9: HT1's path left them none for 25 months),
 *      its households doubled up to within a few of the valve's ceiling;
 *   3. through the crunch the owners put in what the till lacked, homes are
 *      built, no month leaves more households with no home
 *      (FamilyModel.getStillUnplaced()) than the crunch's first, each month over
 *      WATCH is one the bank was short for the landlords' order, and once the
 *      owners' first homes stand none is over WATCH (LongPlaytest's watch; R1g:
 *      a crunch that stops building leaves newcomers with no home until it
 *      resumes, Calgary 1905-1913, section 3's banner), and no
 *      month the door trimmed is left marked as held for the bank's capital;
 *   4. an order of six caused at the crunch's end (REV-FB15), since the
 *      played crunch asks for one block at a time once the door prices it,
 *      the bank held in its band and the rule struck on it by hand just
 *      before, the yard emptied and the planner's land refreshed after the
 *      plots bought, so the door buys the materials at the market's quote and
 *      the planner at the yard's world price, and the two part (either way: FS9):
 *      the lender reads it, and the owners are offered what the till lacks,
 *      at the door's own price (FB15, HN1-A; red on main's classes); where
 *      their issue falls short, the door trims for the bank's capital what
 *      equity did not cover, builds what the till buys, and leaves it
 *      unmarked (HN1, REV-HN1-1).
 */
public final class CreditCrunchCheck {

    static PrintStream out;
    static int fails;

    static final String RE = Sectors.REAL_ESTATE;

    static void check(String label, boolean ok) {
        out.println((ok ? "  ok   " : "  FAIL ") + label);
        if (!ok) fails++;
    }

    public static void main(String[] args) throws Exception {
        out = System.out;
        Locale.setDefault(Locale.ROOT);
        theTestAskedFirst();
        thePlayedCrunch();
        out.println(fails == 0 ? "\nCreditCrunchCheck: all passed" : "\nCreditCrunchCheck: " + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* =====================================================================
       1. THE DOOR'S CAPITAL TEST, ASKED FIRST - a desk and a bank by hand
       ===================================================================== */

    /** What the landlords owe before the month, and what they own: room to spare under the default point. */
    static final double OWED = 1_000_000, OWNED = 100_000_000;

    /**
     * A bank of one branch whose whole exposure is its cash, funded by deposits
     * from abroad: nothing weighs, so its leverage requirement is the larger,
     * and its leverage ratio stands where its liabilities put it - x of the way
     * from LEVERAGE_RATIO_MIN to its leverage target.
     */
    static Bank bankAt(double x, double exposure) {
        Bank bank = new Bank();
        bank.refresh(1, 0, 0, 0, 0, 0);
        bank.setCash(exposure);
        for (int i = 0; i < 4; i++) {
            double ratio = Bank.LEVERAGE_RATIO_MIN + x * (bank.leverageTarget() - Bank.LEVERAGE_RATIO_MIN);
            bank.setForeignDeposits(exposure * (1 - ratio));
        }
        return bank;
    }

    /** The landlords' shortfall whose mortgage's principal is this (Mortgage.loanFor(), the premium added). */
    static double shortfallFor(double principal) {
        return principal / (1 + Mortgage.premiumRate()) * (1 - Bank.LOAN_FEE * (1 + Mortgage.premiumRate()));
    }

    /** The landlords' desk, owing OWED on an insured mortgage, under that bank's rule for the month. */
    static BusinessDebtManager deskUnder(Bank bank) {
        BusinessDebtManager desk = new BusinessDebtManager();
        desk.setInsuredMortgageRate(.04);
        desk.setAssets(RE, OWNED);
        desk.issueMortgage(RE, shortfallFor(OWED), 0);
        desk.startAuditMonth();
        desk.setCapitalRule(bank.lendingGrowthLimit(), bank.lendsOnlyToKeepBorrowersGoing(), bank.leverageBinds());
        desk.setProjectBudget(bank);
        return desk;
    }

    /** The door's answer on a mortgage of this principal, on a building costing plenty more. */
    static boolean door(BusinessDebtManager desk, double principal) {
        double shortfall = shortfallFor(principal);
        return desk.canFundMortgage(RE, shortfall, shortfall * 10);
    }

    static void theTestAskedFirst() {
        out.println("--- 1. the door's capital test asked first says what the door says, and records nothing ---");
        Bank half = bankAt(.5, 10_000_000);
        BusinessDebtManager desk = deskUnder(half);
        double ask = OWED * .05;
        out.printf("   in the band: leverage ratio %.4f, minimum %.4f, target %.4f%n",
                half.leverageRatio(), Bank.LEVERAGE_RATIO_MIN, half.leverageTarget());
        check("fixture: the bank's leverage requirement is the larger and it stands in its band",
                half.leverageBinds() && desk.isInsuredRationed() && !desk.isKeepGoingOnly()
                        && half.leverageRatio() < half.leverageTarget());
        boolean first = desk.mortgageShortOfCapital(RE, shortfallFor(ask));
        check("in the band the test asked first says the bank has no capital for the mortgage", first);
        check("...and records no refusal", !desk.wasMortgageRefusedForCapital(RE) && !desk.wasRefusedForCapital(RE));
        check("...where the door refuses the same mortgage for the bank's capital",
                !door(desk, ask) && BusinessDebtManager.MORTGAGE_CAPITAL.equals(desk.getMortgageRefusal()));

        Bank over = bankAt(1.5, 10_000_000);
        BusinessDebtManager overDesk = deskUnder(over);
        double room = over.leverageHeadroom();
        check("fixture: over its target the bank has finite room for insured growth (BF1)",
                Double.isInfinite(overDesk.getCapitalGrowth()) && over.leverageBinds() && room > 0);
        check("over the target a mortgage within the room: the test says no shortage, and the door writes it",
                !overDesk.mortgageShortOfCapital(RE, shortfallFor(room * .5)) && door(overDesk, room * .5));
        check("...one past the room: the test says short, and the door refuses it for the bank's capital",
                overDesk.mortgageShortOfCapital(RE, shortfallFor(room * 2)) && !door(overDesk, room * 2)
                        && BusinessDebtManager.MORTGAGE_CAPITAL.equals(overDesk.getMortgageRefusal()));

        BusinessDebtManager risk = deskUnder(half);
        risk.setCapitalRule(0, true, false);
        check("while the risk weights bind, the insured mortgage is not rationed: no shortage",
                !risk.isInsuredRationed() && !risk.mortgageShortOfCapital(RE, shortfallFor(ask)) && door(risk, ask));
        BusinessDebtManager shut = deskUnder(half);
        shut.setLendingOpen(false);
        check("...and with the lender shut the door's no is not the capital's: no shortage",
                !shut.mortgageShortOfCapital(RE, shortfallFor(ask)) && !door(shut, ask)
                        && BusinessDebtManager.MORTGAGE_BANK_SHUT.equals(shut.getMortgageRefusal()));
        check("...and nothing to borrow is no shortage", !desk.mortgageShortOfCapital(RE, 0));

        BusinessDebtManager banned = deskUnder(half);
        banned.restoreCreditRecord(java.util.Map.of(), java.util.Map.of(RE, 12));
        check("a landlord banned from borrowing is refused for the ban, not the capital: no shortage",
                banned.isBorrowingBlocked(RE) && !banned.mortgageShortOfCapital(RE, shortfallFor(ask))
                        && !door(banned, ask) && BusinessDebtManager.MORTGAGE_BANNED.equals(banned.getMortgageRefusal()));
        BusinessDebtManager broke = deskUnder(half);
        broke.setAssets(RE, OWED / 2);
        broke.setCash(RE, 0);
        check("...nor one past its default point: no shortage",
                !broke.mortgageShortOfCapital(RE, shortfallFor(ask)) && !door(broke, ask)
                        && BusinessDebtManager.MORTGAGE_PAST_DEFAULT_POINT.equals(broke.getMortgageRefusal()));

        out.println("\n--- 1b. the owners are asked for the whole order only while the door would refuse its mortgage for the bank's capital ---");
        double cost = OWED * .2, down = Mortgage.ownFundsFor(cost), held = down / 2;
        check("in the band the owners are asked for what the till lacks on the whole order",
                Math.abs(desk.ownersAskOnMortgage(RE, cost, held) - (cost - held)) < 1e-6);
        check("...and a banned landlord's owners for what it lacks on the down payment, as before",
                Math.abs(banned.ownersAskOnMortgage(RE, cost, held) - (down - held)) < 1e-6);
        check("...as are the owners of a landlord the risk weights leave unrationed",
                Math.abs(risk.ownersAskOnMortgage(RE, cost, held) - (down - held)) < 1e-6);
        /* The door is asked for the loan on the cost less the down payment the owners put in first: a
           till under the down payment must not price the loan on the cost less the till (REV-MC1). */
        BusinessDebtManager fresh = deskUnder(over);
        // An order whose two loans, each a constant times its cost, stand the same ratio either side of the room.
        double prem = 1 + Mortgage.premiumRate();
        double between = room / (prem * Math.sqrt(Mortgage.loanFor(1) * Mortgage.loanFor(1 - Mortgage.ownFundsFor(1))));
        double doorPrincipal = Mortgage.loanFor(between - Mortgage.ownFundsFor(between)) * prem;
        double tillPrincipal = Mortgage.loanFor(between) * prem;
        out.printf(Locale.ROOT, "   over the target, room %.0f: an order of %.0f, its down payment %.0f, the till empty;"
                + " the door's loan %.0f, the loan on the cost less the till %.0f%n",
                room, between, Mortgage.ownFundsFor(between), doorPrincipal, tillPrincipal);
        check("fixture: the bank's room lies between the door's loan and the loan on the cost less the till",
                doorPrincipal < room && room < tillPrincipal);
        double owners = fresh.ownersAskOnMortgage(RE, between, 0);
        check("...so the owners are asked for the down payment only",
                Math.abs(owners - Mortgage.ownFundsFor(between)) < 1e-6);
        check("...and the door writes the mortgage on the rest",
                fresh.canFundMortgage(RE, between - owners, between));
    }

    /* =====================================================================
       2-3. A PLAYED CITY, ITS BANK HELD IN ITS BAND
       ===================================================================== */

    /** The ensemble's seed CU2 found the crunch on (SHOCK seed 13, the AUTO2 path before its shock). */
    static final int SEED = 13;
    /** The crunch, months already run: from the city's first years of growth, before its bank's leverage first binds on its own. */
    static final int FROM = 150, TO = 270;
    /** Where the bank is held: halfway from its leverage minimum to its target. */
    static final double HOLD = .5;
    /** ★ The households the placement may still take when the crunch starts: the homes cut to the doubling valve's last few. */
    static final double MARGIN = 12;
    /** The ensemble's AUTO2: the autopilot at this target, every month (Ensemble.TARGET). */
    static final double TARGET = .02;

    /** One crunch month as it ended. */
    record Month(int month, double unplaced, double seekersUnhoused, double doubled, double households, long homes, int written,
                 boolean refusedForCapital, long asked, boolean shortOfCapital, double raised, boolean rationed,
                 double growth, boolean keepGoing, double ratio, double target, String line) { }

    static final List<Month> months = new ArrayList<>();
    static long askedThisMonth;
    static boolean shortThisMonth;
    /** What the lender's watch read the last order at, its whole cost (Game.lenderReads()' costOf). */
    static double lenderCost = Double.NaN;
    /** ...and the lender's own reading of it (R1g): how many its test passed, the rent a unit it read, at the rate it tested. */
    static long lenderPassed;
    static double lenderNoi = Double.NaN, lenderRate = Double.NaN;
    static double roomAtStart = Double.NaN, roomAfterCut = Double.NaN;
    static double singleDoorRoom = Double.POSITIVE_INFINITY;
    static long cutHomes;

    /** ★ How far over the risk-based requirement the hold puts the leverage requirement: the book may weigh a quarter more by the strike. */
    static final double BINDS_BY = 1.25;
    /** The leverage ratio the last hold left, which is what the month's rule is struck on (the month's end reads the hot money's reset). */
    static double heldRatio = Double.NaN;

    /**
     * The restore setters: the bank's cash and its deposits from abroad moved, its book as it is, so that its leverage
     * requirement is the larger (BINDS_BY over the risk-based one: the cash and the deposits both come in at no weight)
     * and its leverage ratio stands HOLD of its band up. Cash for the exposure, the deposits for the equity.
     * The played city's own bank sits under its risk weights (its book weighs more than LEVERAGE_RATIO_MIN/CAPITAL_RATIO of its exposure), so
     * the cash alone, which moves the ratio but not the requirement that binds, held it in the wrong band (FS9).
     */
    static void holdInBand(Bank bank) {
        holdInBand(bank, 0);
    }

    /** ...with what the bank pays out of its cash before the rule is struck held in it too, so that the rule finds it HOLD of its band up. */
    static void holdInBand(Bank bank, double dueBeforeTheRule) {
        double ratio = Bank.LEVERAGE_RATIO_MIN + HOLD * (bank.leverageTarget() - Bank.LEVERAGE_RATIO_MIN);
        double owedAtHome = bank.totalLiabilities() - bank.borrowings() - Math.max(0, bank.getForeignDeposits());
        double book = bank.exposure() - bank.cashReserves();
        double wanted = BINDS_BY * Bank.CAPITAL_RATIO * bank.getWeightedBook() / Bank.LEVERAGE_RATIO_MIN;
        double exposure = Math.max(wanted, Math.max(book, owedAtHome / (1 - ratio)));
        bank.setCash(exposure - book + dueBeforeTheRule);
        bank.setForeignDeposits(exposure * (1 - ratio) - owedAtHome);
    }

    /**
     * The played month's hold: the bank HOLD of its band up as the month's rule reads it, after the profit tax the month's top
     * charges from its cash first (Bank.chargeTax(), in the audit window, before startOfMonthUpdate() strikes the rule; Bank.taxAt()
     * is that bill, at the model's own rate). Returns the leverage ratio the rule is struck on.
     * WHY (RES3c, 2026-11-25): since call A the overcrowded need homes too, and seed 13's town, before the House's ground was stood
     * (cutToTheValve()), built 285 homes to 717 in the crunch where it had built 268 to 566; its bank's profit tax grew with its book,
     * and at m267 it took 37.76 of the equity at the month's top (Bank.taxAt() at the hold read the same 37.76) where the half band
     * the hold left was 33.49 over the minimum: the rule found the bank under it, lending only to keep borrowers going, in the one
     * month the fixture says it stood in its band. The header's "equity and exposure hardly move between the hold and that strike"
     * was the old town's. The town with the House's ground pays no profit tax in the crunch, so this hold is the old one there.
     */
    static double holdInBand(Game g) {
        Bank bank = g.getBank();
        double due;
        try {
            java.lang.reflect.Method rate = Game.class.getDeclaredMethod("bankProfitTaxRate");
            rate.setAccessible(true);
            due = bank.taxAt((Double) rate.invoke(g));
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
        holdInBand(bank, due);
        return (bank.equity() - due) / (bank.exposure() - due);
    }

    /** ★ The ground stood before each crunch month, in blocks of Low-Rise Apartments: section 4's order, so the landlords never wait on land. */
    static final long GROUND_BLOCKS = CreditCrunchCheck.ORDER;
    /** The crunch months that began with ground for GROUND_BLOCKS blocks, and the plots bought for it. */
    static int groundMonths, groundPlots;

    /**
     * The city buys the cheapest plots (Game.buyLandBlock(), the player's own purchase, MAX_PLOTS at most a month)
     * until the landlords' zone holds GROUND_BLOCKS blocks. WHY (FS9): on HT1's path seed 13's landlords asked for
     * nothing in m197-221, the planner refusing for no land (5,570 square metres needed, 1,600-1,800 free), and the
     * households with no home rose to 9.1: section 3's law read whether the played town happened to have ground.
     */
    static void standGround(Game g) {
        BuildingsTemplate block = LongPlaytest.template(g, "Low-Rise Apartments");
        if (block == null) return;
        int plots = 0;
        while (!ground(g, block, GROUND_BLOCKS) && plots < MAX_PLOTS && g.buyLandBlock()) plots++;
        groundPlots += plots;
        if (ground(g, block, GROUND_BLOCKS)) groundMonths++;
    }

    /** The seekers' households (FamilyModel.Seeker) past their own ceiling, two to a door up to half the group: no door. */
    static double seekersUnhoused(FamilyModel fm) {
        double n = 0;
        for (FamilyModel.Seeker s : FamilyModel.Seeker.values()) n += fm.getSeekersUnhoused(s);
        return n;
    }

    /** The households placed nowhere that LongPlaytest's watch flags past (its audit's "households with no home"): one, the rounding. */
    static final double WATCH = 1;

    /** The households the placement could still take against the homes standing. */
    static double room(Game g) {
        FamilyModel.Room r = g.getFamilies().roomLeft(g.getBuildingManager().homesBySize());
        return r == null ? 0 : r.households();
    }

    /** The landlords' homes retired, larger mixes before single doors, while MARGIN households of room remain. */
    static void cutToTheValve(Game g) {
        BuildingManager bm = g.getBuildingManager();
        // The played catalogue can contain only multi-home forms. Stand a paid single door so this fixture can measure
        // the room quantum of the one-house bound, rather than assuming that a door means one household slot. The
        // House where the city knows it; R1g's 1900 city does not, so its own single door (a cottage of HOMES_1900).
        BuildingsTemplate single = null;
        for (BuildingsTemplate t : bm.getTemplatesBySector(g.getSectors().realEstate().key()))
            if (t.getDwellings() == 1 && (!g.foundedUnfitted() || t.knownAt(g.getMonth(), g.getResearch()))
                    && (single == null || "House".equals(t.getName()))) single = t;
        check("fixture: the city knows a single-door home (" + (single == null ? "none" : single.getName()) + ")", single != null);
        if (single == null) return;
        // Its ground, as the player stands it (standGround(): the cheapest plots, Game.buyLandBlock()). WHY (RES3c, 2026-11-25): since
        // call A the overcrowded need homes too (Game.getHouseholdsWantingAHome()), and seed 13's town had built to its ground by m150
        // (280 square feet free): the House was refused for no land, and the one house's room went unmeasured.
        for (int plots = 0; !ground(g, single, 1) && plots < MAX_PLOTS && g.buyLandBlock(); plots++) { }
        g.setCashForTest(Math.max(g.getCash(), g.quoteBuild(single, 1).total));
        double withoutIt = room(g);
        check("fixture: a paid single-door home stands before trimming the capacity buffer",
                g.buildStack(single, 1, true) == Game.BuildResult.SUCCESS);
        roomAtStart = room(g);
        // One house's room: what the door just stood added, and what any single door the cut retires takes away; the
        // cut need not reach a single door to know it (RES3c: tenure left the 1900 city's cut short of one at m150).
        singleDoorRoom = roomAtStart > withoutIt ? roomAtStart - withoutIt : Double.POSITIVE_INFINITY;
        java.util.List<BuildingsTemplate> homes = new java.util.ArrayList<>(bm.getTemplatesBySector(g.getSectors().realEstate().key()));
        homes.sort(java.util.Comparator.comparingInt(BuildingsTemplate::getDwellings).reversed());
        for (BuildingsTemplate t : homes) {
            while (bm.getQuantity(t.getId()) > 0 && room(g) - 2 * t.getDwellings() >= MARGIN) {
                double before = room(g);
                if (bm.retire(t, 1) < 1) break;
                cutHomes += t.getDwellings();
                double removedRoom = before - room(g);
                if (t.getDwellings() == 1 && removedRoom > 0) singleDoorRoom = Math.min(singleDoorRoom, removedRoom);
            }
        }
        roomAfterCut = room(g);
    }

    static void thePlayedCrunch() throws Exception {
        PrintStream quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        System.setProperty("playtest.seed", Integer.toString(SEED));
        GameFiles files = GameFiles.scratch("creditcrunch");
        final Game g;
        PrintStream was = LongPlaytest.out;
        LongPlaytest.out = quiet;
        System.setOut(quiet);
        try {
            g = new Game(files, LongPlaytest.played(null));
            g.run();
            g.watchOrders(new BusinessInvestment.OrderWatch() {
                @Override public void mortgaged(long asked, java.util.function.LongToDoubleFunction costOf, double cash,
                                                double noiPerUnit, double annualRate, Mortgage.Decision found) {
                    askedThisMonth += Math.max(0, asked);
                    BusinessDebtManager desk = g.getEconomyManager().getBusinessDebtManager();
                    double cost = costOf.applyAsDouble(Math.max(1, asked));
                    lenderCost = cost;
                    lenderPassed = found == null ? 0 : found.quantity();
                    lenderNoi = noiPerUnit;
                    lenderRate = annualRate;
                    if (desk.mortgageShortOfCapital(RE, cost - Math.max(cash, Mortgage.ownFundsFor(cost)))) shortThisMonth = true;
                }
            });
            foundAndPlay(g, () -> {
                int m = g.getMonth();
                BusinessDebtManager desk = g.getEconomyManager().getBusinessDebtManager();
                FamilyModel fm = g.getFamilies();
                Bank bank = g.getBank();
                if (m > FROM) {
                    months.add(new Month(m, fm.getStillUnplaced(), seekersUnhoused(fm), fm.getDoubledUpHouseholds(), fm.totalHouseholds(),
                            g.getBuildingManager().getTotalHomes(), desk.getMortgagesWrittenThisMonth(),
                            desk.wasMortgageRefusedForCapital(RE), askedThisMonth, shortThisMonth,
                            g.getEconomyManager().getEquityRaised(RE), desk.isInsuredRationed(),
                            desk.getCapitalGrowth(), desk.isKeepGoingOnly(), heldRatio, bank.leverageTarget(),
                            g.getLastInvestment(RE)));
                }
                askedThisMonth = 0;
                shortThisMonth = false;
            }, () -> {
                if (g.getMonth() == FROM) cutToTheValve(g);
                if (g.getMonth() >= FROM) {
                    standGround(g);
                    heldRatio = holdInBand(g);
                }
            });
        } finally {
            LongPlaytest.out = was;
            System.setOut(out);
        }

        out.printf("%nthe ensemble's seed %d, its bank held %.0f%% of the way up its leverage band, months %d-%d%n",
                SEED, HOLD * 100, FROM + 1, TO);
        out.printf(Locale.ROOT, "   at m%d the placement had room for %.1f households; %,d homes retired left %.1f (a one-door trim removed %.9g households of room)%n",
                FROM, roomAtStart, cutHomes, roomAfterCut, singleDoorRoom);
        for (Month r : months) {
            if (r.month % 6 == 0 || r.unplaced > 1 || r.written > 0 || r.raised > 0)
                out.printf(Locale.ROOT, "   m%d: %,.0f households, %,d homes, %,.0f doubled up, %.1f with no home;"
                                + " asked %d%s, raised %.0f, %d mortgage(s); leverage %.4f of %.4f%n",
                        r.month, r.households, r.homes, r.doubled, r.unplaced, r.asked,
                        r.shortOfCapital ? " (bank short)" : "", r.raised, r.written, r.ratio, r.target);
        }

        out.println("\n--- 2. the fixture: leverage binding, the bank in its band, the landlords asking, households waiting ---");
        int rationed = 0, inBand = 0, asking = 0, shortMonths = 0;
        for (Month r : months) {
            if (r.rationed) rationed++;
            if (!r.keepGoing && r.growth > 0 && !Double.isInfinite(r.growth)) inBand++;
            else out.printf(Locale.ROOT, "   out-of-band fixture at m%d: growth %.12g, keepGoing %s, leverage %.12g of %.12g%n",
                    r.month, r.growth, r.keepGoing, r.ratio, r.target);
            if (r.asked > 0) asking++;
            if (r.shortOfCapital) shortMonths++;
        }
        out.printf("   %d crunch months: %d rationed on the leverage ratio, %d in the band, %d with the landlords asking,"
                + " %d of them with the bank short of capital for the order%n", months.size(), rationed, inBand, asking,
                shortMonths);
        check("fixture: the city played every month of the crunch", months.size() == TO - FROM);
        check("fixture: every month the bank's leverage ratio bound, so the rule rationed the insured mortgages",
                rationed == months.size());
        check("fixture: every month the bank stood in its band, the rule neither free nor keeping borrowers going",
                inBand == months.size());
        check("fixture: the landlords asked for homes the bank had no capital to lend on",
                shortMonths > 0 && asking > 0);
        out.printf("   %d of %d crunch months began with ground for %d blocks; %d plots bought for it%n",
                groundMonths, TO - FROM, GROUND_BLOCKS, groundPlots);
        check("fixture: every month of the crunch began with ground for GROUND_BLOCKS blocks of Low-Rise Apartments",
                groundMonths == TO - FROM);
        check("fixture: the homes cut left the placement room for MARGIN households and less than one house more",
                cutHomes > 0 && Double.isFinite(singleDoorRoom) && roomAfterCut >= MARGIN
                        && roomAfterCut - MARGIN < singleDoorRoom && roomAfterCut < roomAtStart);

        /*
         * ----- the households with no home: bounded by the crunch's first month, and cleared by the owners' homes (R1g, 2026-11-25) -----
         * Restated from "no month leaves more than one" (LongPlaytest's watch, unmoved since HT1). On the 1900 city R1g plays, the cut
         * leaves MARGIN households of room as Room.households() counts the arrivals' mix, but the month after it 15.1 households have no
         * door, the doubling valve at its ceiling (178 of 355 doubled up, FamilyModel's half the households plus one, no seeker among them);
         * the bank, short of capital, writes no mortgage for thirty-eight months, and they stay until the owners' first block, mortgaged at
         * m189, stands (m190), then none again to TO (the material form's run, R1g's last). That is the real law: a credit crunch that stops
         * the building leaves newcomers without a home until building resumes, and adds none past what it began with. Calgary, 1905-1913
         * (The City of Calgary's First Boom and Bust, Alberta's Real Estate Heritage, Heritage Community Foundation, after J. Peach 1993): the
         * 1905-1909 housing shortage left some newcomers living in tents, and in the 1913 bust the collapse "caused building permits to
         * drop to 8.5 million dollars in 1913" from over 20 million in 1912; "This in turn created high unemployment." Today's city (-Dplaytest.founded=today) holds 0.00.
         */
        out.println("\n--- 3. the owners put in what the till lacked, homes are built, and the households left with no home clear ---");
        double raised = 0, worst = 0;
        int worstMonth = -1, firstBuilt = -1, lastUnhoused = -1, unhousedNotShort = 0;
        Month opened = months.isEmpty() ? null : months.get(0);
        long homesFrom = opened == null ? 0 : opened.homes, homesTo = homesFrom;
        for (Month r : months) {
            raised += r.raised;
            homesTo = Math.max(homesTo, r.homes);
            if (firstBuilt < 0 && r.homes > homesFrom) firstBuilt = r.month;
            if (r.unplaced > worst) { worst = r.unplaced; worstMonth = r.month; }
            if (r.unplaced > WATCH) {
                lastUnhoused = r.month;
                if (!(r.shortOfCapital && r.asked > 0)) unhousedNotShort++;
            }
        }
        out.printf(Locale.ROOT, "   the owners put in %.0f; homes %,d to %,d, the first built standing at m%d; the most households with no home"
                        + " %.2f (m%d), %.2f in the crunch's first month (m%d, %.2f of them seekers), the last month over the watch m%d%n",
                raised, homesFrom, homesTo, firstBuilt, worst, worstMonth, opened == null ? Double.NaN : opened.unplaced,
                opened == null ? -1 : opened.month, opened == null ? Double.NaN : opened.seekersUnhoused, lastUnhoused);
        check("the landlords' owners were asked, and put money in, while the bank was short", raised > 0);
        check("...and homes were built in the crunch", homesTo > homesFrom);
        check("no month of the crunch leaves more households with no home than its first month, before any home could be built (R1g)",
                opened != null && worst <= opened.unplaced);
        check("...every month leaving more than WATCH with no home is one the landlords asked for homes the bank was short of capital for",
                unhousedNotShort == 0);
        check("...and they clear: once the owners' first homes stand, no month leaves more than WATCH with no home (LongPlaytest's watch)",
                firstBuilt > 0 && lastUnhoused < firstBuilt);

        // The played crunch's own capital trims (HN1), as it happened to ask: printed, and asserted on the order caused below.
        int capitalTrims = 0, leftMarked = 0;
        for (Month r : months) {
            if (r.line == null || !r.line.contains("(trimmed from ") || !r.line.contains("the bank is short of capital")) continue;
            if (r.refusedForCapital) leftMarked++;
            if (++capitalTrims <= 3) out.printf(Locale.ROOT, "   m%d: raised %.0f; %s%n", r.month, r.raised, r.line);
        }
        out.printf("   the played crunch: %d order(s) the door trimmed for the bank's capital, %d left marked as held for it%n",
                capitalTrims, leftMarked);
        check("...and no played month the door trimmed is left marked as held for the bank's capital (REV-HN1-1)", leftMarked == 0);

        theShortIssue(g);
    }

    /* =====================================================================
       4. AN ORDER THE OWNERS' ISSUE FALLS SHORT OF - caused at TO
       ===================================================================== */

    /** ★ The order caused (REV-FB15): main's seed 13 asked for up to six blocks while its bank was short (m190-194). */
    static final long ORDER = 6;
    /** ★ The till the order meets, in the door's price of one block: one block outright, whatever the issue brings. */
    static final double TILL = 1.5;
    /** The plots the fixture may buy for the order's ground. */
    static final int MAX_PLOTS = 60;
    /**
     * ★ The insured mortgage rate the order is read at (R1g): a calm market's, so the lender's test passes more of the
     * order than the till buys outright and what trims it is the bank's capital. The played crunch's own rate at TO moves
     * with the city: on R1g's seed 13 the autopilot's inflation rule (the currency floating) ran the policy rate to 0.26 on
     * 18% inflation at m270, a real rate for that (US 1981: fed funds about 19% on about 11%), and the lender passed one
     * block, the till's.
     */
    static final double ORDER_RATE = .08;

    /**
     * Since FB15 the played crunch asks for one block at a time while the bank
     * is short, and an order of one has no half to trim to: the door's trim
     * for the capital where the owners' issue fell short (MC1, HN1) would be
     * watched over no order at all. So the order is caused, at the crunch's
     * end: ORDER blocks, put to Game.consider() (by reflection, as
     * OrderDoorCheck does) on ground bought for them and a till of TILL
     * blocks, the owners asked for what it lacks on the order. The bank is
     * held in its band again just before, and the desks' capital rule
     * struck on it by the calls Game.startOfMonthUpdate() strikes it with,
     * since the month's end has left the bank where its hot money's reset
     * puts it, not where the rule was struck (FS9), and the insured rate held
     * at ORDER_RATE, so the lender passes more than the till buys (R1g). The yard is emptied, so
     * the builders buy the order's materials at the market's quote and the
     * door's price is not the planner's.
     */
    static void theShortIssue(Game g) throws Exception {
        out.println("\n--- 4. an order the owners' issue falls short of, the bank short: the door trims it for the capital ---");
        EconomyManager em = g.getEconomyManager();
        BusinessDebtManager desk = em.getBusinessDebtManager();
        BuildingsTemplate block = LongPlaytest.template(g, "Low-Rise Apartments");
        // Cause actual replacement demand for this hand order. Standing homes
        // and funded sites otherwise reserve its prospective tenants first;
        // a vacancy cannot supply the lender with fictitious occupied proceeds.
        long replacementDoors=0;
        for(BuildingsTemplate t:g.getBuildingManager().getTemplatesBySector(RE)){
            if(t.getHousingRecipe()==null)continue;
            var stack=g.getBuildingManager().getStack(t);if(stack==null)continue;
            long n=stack.getQuantity();
            while(n>0&&!g.getBuildingManager().getHousingInventory().mayRemove(stack,n))n--;
            long removed=g.getBuildingManager().retire(t,n);
            replacementDoors+=removed*t.getDwellings();
            g.getLandManager().release(removed*t.getLandSqFt(),t.getSector(),Game.groundUse(t));
        }
        check("fixture: the hand order has actual replacement demand from removed homes",replacementDoors>=ORDER*block.getDwellings());
        int plots = 0;
        while (!ground(g, block, ORDER) && plots < MAX_PLOTS && g.buyLandBlock()) plots++;
        // The yard empty: the order's materials come in at their landed price, over the planner's (the yard short of its materials).
        g.getBuildingManager().setConstructionMaterials(0);
        // The planner reads the land as the month's plan does, refreshed before it asks (Game.runPrivateInvestment()): after the plots.
        java.lang.reflect.Method refreshLand = Game.class.getDeclaredMethod("refreshLand");
        refreshLand.setAccessible(true);
        refreshLand.invoke(g);
        Bank bank = g.getBank();
        holdInBand(bank);
        strikeTheRule(g);
        double playedRate = desk.getInsuredMortgageRate(), playedPolicy = g.getDebtManager().getPolicyRate();
        desk.setInsuredMortgageRate(ORDER_RATE);
        double growth = desk.getCapitalGrowth();
        boolean bandsRule = bank.leverageBinds() && bank.leverageRatio() > Bank.LEVERAGE_RATIO_MIN
                && bank.leverageRatio() < bank.leverageTarget() && desk.isInsuredRationed()
                && growth > 0 && !Double.isInfinite(growth) && !desk.isKeepGoingOnly();
        out.printf(Locale.ROOT, "   the bank held at leverage %.4f of %.4f (minimum %.4f); the rule struck: insured %s, growth %.4f, %s%n",
                bank.leverageRatio(), bank.leverageTarget(), Bank.LEVERAGE_RATIO_MIN,
                desk.isInsuredRationed() ? "rationed" : "free", growth, desk.isKeepGoingOnly() ? "keeping borrowers going" : "lending in its band");
        em.setSectorCash(RE, TILL * door(g, block, 1));
        double whole = door(g, block, ORDER), held = em.getSectorCash(RE) + em.getForeignAssets(RE);
        double asked = desk.ownersAskOnMortgage(RE, whole, held), raisedBefore = em.getEquityRaised(RE);
        double plan = g.getBusinessInvestment().getCostOf(block, ORDER);
        int company = Equity.indexOf(RE);
        boolean groundForAll = ground(g, block, ORDER), quote = company >= 0 && g.getExchange().quoteSupportsIssue(company);
        double offeredBefore = company >= 0 ? g.getEquity().getOfferedThisMonth(company) : 0;
        askedThisMonth = 0;
        shortThisMonth = false;
        lenderCost = Double.NaN;
        lenderPassed = 0;
        lenderNoi = Double.NaN;
        lenderRate = Double.NaN;
        java.lang.reflect.Method investor = Game.class.getDeclaredMethod("sectorInvestor", String.class);
        investor.setAccessible(true);
        java.lang.reflect.Method consider = Game.class.getDeclaredMethod("consider", BusinessInvestment.Decision.class, Investor.class);
        consider.setAccessible(true);
        consider.invoke(g, new BusinessInvestment.Decision(RE, block, ORDER, "CreditCrunchCheck's order of " + ORDER, true),
                investor.invoke(g, RE));
        double raised = em.getEquityRaised(RE) - raisedBefore;
        double offered = company >= 0 ? g.getEquity().getOfferedThisMonth(company) - offeredBefore : 0;
        String line = g.getLastInvestment(RE);
        out.printf(Locale.ROOT, "   m%d: %d %s at %.1f at the door, %.1f planned, the till %.1f (%d plots bought); the lender read %.1f;"
                        + " the owners were offered %.1f of the %.1f it lacked and put in %.1f; the door asked for %d%s%n     \"%s\"%n",
                g.getMonth(), ORDER, block.getName(), whole, plan, held, plots, lenderCost, offered, asked, raised,
                askedThisMonth, shortThisMonth ? ", the bank short of capital for its mortgage" : "", line);
        check("fixture: the bank held in its band and the rule struck on it: leverage binding, the insured mortgages rationed,"
                + " growth finite and above nothing, not keeping borrowers going", bandsRule);
        check("fixture: ground for the order, a quote that supports an issue, and the bank short of capital for the order's mortgage",
                groundForAll && quote && askedThisMonth == ORDER && shortThisMonth);
        out.printf(Locale.ROOT, "   the lender's test passed %d of the %d, a block's rent read %.3f a month at %.4f a year tested (insured held at %.4f,"
                        + " the played city's %.4f on a policy rate of %.4f; inflation %.4f, expected %.4f)%n",
                lenderPassed, ORDER, lenderNoi, lenderRate, desk.getInsuredMortgageRate(), playedRate, playedPolicy,
                g.getPriceIndex().inflation(), g.getBusinessInvestment().getExpectedInflation());
        check("fixture: the lender's test passes more than the till buys outright, so the door asks the bank for a mortgage", lenderPassed > 1);
        out.printf(Locale.ROOT, "   the door %s the planner by %.1f%%%n", whole > plan ? "over" : "under", 100 * Math.abs(whole - plan) / plan);
        check("fixture: the door's price of the order is not the planner's (the yard emptied: its materials at the market's quote)",
                Math.abs(whole - plan) > 1e-6 * plan);
        // FB15 (HN1-A, REV-FB15-2): what consider() itself did, so the line fails if the order is priced at the plan again.
        check("the lender read the order at the door's own price, and the owners were offered what the till lacked at it (HN1-A)",
                Math.abs(lenderCost - whole) <= 1e-9 * whole && offered >= asked - 1e-6 && asked > whole - held - 1e-6);
        check("fixture: ...and their issue fell short of it", raised > 0 && raised < offered - 1e-6);
        check("the door trimmed the order for the bank's capital, what equity did not cover, and built what the till buys",
                line != null && line.startsWith("Built ") && line.contains("(trimmed from " + ORDER + " - the bank is short of capital"));
        check("...and is not left marked as held for the bank's capital (REV-HN1-1)", !desk.wasMortgageRefusedForCapital(RE));
    }

    /** The desks' capital rule struck from the bank as it stands, by the calls Game.startOfMonthUpdate() makes (strikeBankBonds() is its own). */
    static void strikeTheRule(Game g) throws Exception {
        java.lang.reflect.Method bonds = Game.class.getDeclaredMethod("strikeBankBonds");
        bonds.setAccessible(true);
        bonds.invoke(g);
        Bank bank = g.getBank();
        double growth = bank.lendingGrowthLimit();
        boolean keepGoingOnly = bank.lendsOnlyToKeepBorrowersGoing();
        BusinessDebtManager desk = g.getEconomyManager().getBusinessDebtManager();
        desk.setCapitalRule(growth, keepGoingOnly, bank.leverageBinds());
        desk.setProjectBudget(bank);
        g.getHouseholdBalance().setCapitalRule(growth, keepGoingOnly);
    }

    /** What buildFor() charges for n: the builders' quote and the ground at its zone's price (Game.doorPriceOf() since FB15). */
    static double door(Game g, BuildingsTemplate t, long n) {
        return g.quoteBuild(t, n).total + g.getLandManager().priceFor(t.getLandSqFt() * n, t.getSector(), Game.groundUse(t));
    }

    static boolean ground(Game g, BuildingsTemplate t, long n) {
        return g.getLandManager().canAllocate(t.getLandSqFt() * n, t.getSector(), Game.groundUse(t));
    }

    /* =====================================================================
       THE FOUNDING AND THE RHYTHM - the ensemble's foundAndPlay(), as
       HousingDoorCheck plays it (the driver in tools/ensemble/), the AUTO2
       policy's setups, to TO; before each month the hold, after it the watch.
       ===================================================================== */

    static boolean stuck;

    static void run(Game g, int months, Runnable after, Runnable before) {
        for (int i = 0; i < months && !stuck && g.getMonth() < TO; i++) {
            int was = g.getMonth();
            DebtManager dm = g.getDebtManager();
            if (dm.getInflationTarget() != TARGET) dm.setInflationTarget(TARGET);
            if (!dm.isAutopilot()) dm.setAutopilot(true);
            before.run();
            LongPlaytest.runPlayer(g, 1);
            if (g.getMonth() == was) stuck = true;
            after.run();
        }
    }

    static void foundAndPlay(Game g, Runnable after, Runnable before) {
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
        run(g, 3 + (seed / 4) % 3, after, before);
        LongPlaytest.villageBuild(g, "House", 20 + (seed / 12) % 3);
        run(g, 4, after, before);
        LongPlaytest.villageBuild(g, "Convenience Store", 2);
        LongPlaytest.villageBuild(g, "Construction Depot", 1);
        run(g, 5, after, before);
        LongPlaytest.advise(g);
        run(g, 6, after, before);
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
            run(g, skip, after, before);
            if (g.getMonth() >= TO || stuck) break;
            LongPlaytest.ensureSchools(g);
            for (int move = 0; move < LongPlaytest.movesPerLook(); move++) {
                if (LongPlaytest.advise(g) == null) break;
                run(g, 1, after, before);
            }
            run(g, 2, after, before);
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

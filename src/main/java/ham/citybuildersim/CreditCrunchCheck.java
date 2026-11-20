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
 * What this has to prove:
 *   1. the door's capital test asked first (mortgageShortOfCapital()) says
 *      what the door says, records nothing, and is no while the risk weights
 *      bind, the lender is shut, the landlord is banned or past its default
 *      point; the owners are asked for the whole order (ownersAskOnMortgage())
 *      only while it says short, on the loan the door is asked for once the
 *      down payment is in, never on the cost less a till under the down
 *      payment (REV-MC1);
 *   2. the fixture: a played city whose bank's leverage ratio binds, held in
 *      its band every month of the crunch, its landlords asking for homes the
 *      bank has no capital to lend on, its households doubled up to within a
 *      few of the valve's ceiling;
 *   3. through the crunch the owners put in what the till lacked, homes are
 *      built, and no month leaves more than one household with no home
 *      (FamilyModel.getStillUnplaced(), LongPlaytest's watch, unmoved); where
 *      their issue fell short of the order, the door trimmed for the bank's
 *      capital what equity did not cover, and left no such month marked as
 *      held for it (HN1, REV-HN1-1; HousingDoorCheck's city covers its orders).
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
    record Month(int month, double unplaced, double doubled, double households, long homes, int written,
                 boolean refusedForCapital, long asked, boolean shortOfCapital, double raised, boolean rationed,
                 double growth, boolean keepGoing, double ratio, double target, String line) { }

    static final List<Month> months = new ArrayList<>();
    static long askedThisMonth;
    static boolean shortThisMonth;
    static double roomAtStart = Double.NaN, roomAfterCut = Double.NaN;
    static long cutHomes;

    /** The restore setters: the bank's cash moved so its leverage ratio stands HOLD of its band up, its book as it is. */
    static void holdInBand(Bank bank) {
        for (int i = 0; i < 6; i++) {
            double ratio = Bank.LEVERAGE_RATIO_MIN + HOLD * (bank.leverageTarget() - Bank.LEVERAGE_RATIO_MIN);
            bank.setCash(bank.getCash() - (bank.equity() - ratio * bank.exposure()));
        }
    }

    /** The households the placement could still take against the homes standing. */
    static double room(Game g) {
        FamilyModel.Room r = g.getFamilies().roomLeft(g.getBuildingManager().homesBySize());
        return r == null ? 0 : r.households();
    }

    /** The landlords' homes retired, blocks then houses, while what is left still leaves MARGIN households of room. */
    static void cutToTheValve(Game g) {
        roomAtStart = room(g);
        BuildingManager bm = g.getBuildingManager();
        for (String name : new String[] { "Low-Rise Apartments", "House" }) {
            BuildingsTemplate t = LongPlaytest.template(g, name);
            if (t == null) continue;
            while (bm.getQuantity(t.getId()) > 0 && room(g) - t.getDwellings() >= MARGIN) {
                if (bm.retire(t, 1) < 1) break;
                cutHomes += t.getDwellings();
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
                    if (desk.mortgageShortOfCapital(RE, cost - Math.max(cash, Mortgage.ownFundsFor(cost)))) shortThisMonth = true;
                }
            });
            foundAndPlay(g, () -> {
                int m = g.getMonth();
                BusinessDebtManager desk = g.getEconomyManager().getBusinessDebtManager();
                FamilyModel fm = g.getFamilies();
                Bank bank = g.getBank();
                if (m > FROM) {
                    months.add(new Month(m, fm.getStillUnplaced(), fm.getDoubledUpHouseholds(), fm.totalHouseholds(),
                            g.getBuildingManager().getTotalHomes(), desk.getMortgagesWrittenThisMonth(),
                            desk.wasMortgageRefusedForCapital(RE), askedThisMonth, shortThisMonth,
                            g.getEconomyManager().getEquityRaised(RE), desk.isInsuredRationed(),
                            desk.getCapitalGrowth(), desk.isKeepGoingOnly(), bank.leverageRatio(), bank.leverageTarget(),
                            g.getLastInvestment(RE)));
                }
                askedThisMonth = 0;
                shortThisMonth = false;
            }, () -> {
                if (g.getMonth() == FROM) cutToTheValve(g);
                if (g.getMonth() >= FROM) holdInBand(g.getBank());
            });
        } finally {
            LongPlaytest.out = was;
            System.setOut(out);
        }

        out.printf("%nthe ensemble's seed %d, its bank held %.0f%% of the way up its leverage band, months %d-%d%n",
                SEED, HOLD * 100, FROM + 1, TO);
        out.printf(Locale.ROOT, "   at m%d the placement had room for %.1f households; %,d homes retired left %.1f%n",
                FROM, roomAtStart, cutHomes, roomAfterCut);
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
        check("fixture: the homes cut left the placement room for MARGIN households and less than one house more",
                cutHomes > 0 && roomAfterCut >= MARGIN && roomAfterCut < MARGIN + 1 && roomAfterCut < roomAtStart);

        out.println("\n--- 3. the owners put in what the till lacked, homes are built, nobody is left with no home ---");
        double raised = 0, worst = 0;
        int worstMonth = -1;
        long homesFrom = months.isEmpty() ? 0 : months.get(0).homes, homesTo = homesFrom;
        for (Month r : months) {
            raised += r.raised;
            homesTo = Math.max(homesTo, r.homes);
            if (r.unplaced > worst) { worst = r.unplaced; worstMonth = r.month; }
        }
        out.printf(Locale.ROOT, "   the owners put in %.0f; homes %,d to %,d; the most households with no home %.2f (m%d)%n",
                raised, homesFrom, homesTo, worst, worstMonth);
        check("the landlords' owners were asked, and put money in, while the bank was short", raised > 0);
        check("...and homes were built in the crunch", homesTo > homesFrom);
        check("no month of the crunch leaves more than one household with no home (LongPlaytest's watch)", worst <= 1);

        // The door's capital trim (HN1) where the owners' issue fell short of the order: what equity did not cover.
        int capitalTrims = 0, leftMarked = 0;
        for (Month r : months) {
            if (r.line == null || !r.line.contains("(trimmed from ") || !r.line.contains("the bank is short of capital")) continue;
            capitalTrims++;
            if (r.refusedForCapital) leftMarked++;
            if (capitalTrims <= 3) out.printf(Locale.ROOT, "   m%d: raised %.0f; %s%n", r.month, r.raised, r.line);
        }
        out.printf("   %d order(s) the door trimmed after first refusing them for the bank's capital, %d left marked as held for it%n",
                capitalTrims, leftMarked);
        check("the door trimmed an order the owners' issue did not cover, first refused for the bank's capital", capitalTrims > 0);
        check("...and no month the door trimmed is left marked as held for the bank's capital (REV-HN1-1)", leftMarked == 0);
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

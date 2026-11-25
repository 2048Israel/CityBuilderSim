package ham.citybuildersim;

import java.util.Random;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Cards inside a household cell: shapes, shocks, minimums, arrears, and the bank (HC1).
 * WHY: a cell average used to discharge all its neighbours and conceal thin buffers.
 * Every edge is caused with the household's own inputs; money is checked as an identity.
 */
public final class CardCheck {
    static int fails;
    static void check(String label, boolean pass) {
        System.out.println((pass ? "OK   " : "FAIL ") + label); if (!pass) fails++;
    }
    static void close(String label, double got, double want, double tolerance) {
        check(label + " (" + got + "; expected " + want + ")", Math.abs(got - want) <= tolerance);
    }
    static WorkingHousehold cell(double income, double savings, double debt, double holders) {
        WorkingHousehold c = new WorkingHousehold(FamilyStructure.SINGLE_ADULT, PayTier.UNSKILLED);
        c.households = 1000; c.disposable = income; c.savings = savings; c.debt = debt; c.revolving = holders;
        return c;
    }
    static void spread() {
        System.out.println("--- Closed forms against a draw, including both sides of the point mass ---");
        for (double sigma : new double[]{.35, CellSpread.CARD_SIGMA, CellSpread.SAVINGS_SIGMA}) {
            Random random = new Random(7614);
            int n = 600_000; double mean = 4, line = 3, below = 0, above = 0, gap = 0, capped = 0;
            for (int i = 0; i < n; i++) {
                double x = mean * Math.exp(sigma * random.nextGaussian() - sigma * sigma / 2);
                if (x <= line) below++; else above += x;
                gap += Math.max(0, line - x);
                capped += Math.min(line, x);
            }
            close("CDF matches drawn holders at sigma " + sigma, CellSpread.below(line, mean, sigma), below / n, .003);
            close("balance tail matches drawn holders at sigma " + sigma, CellSpread.aboveMean(line, mean, sigma), above / n, .04);
            close("shortfall matches drawn buffers at sigma " + sigma, CellSpread.shortfall(line, mean, sigma), gap / n, .012);
            close("capped repayment matches drawn holders at sigma " + sigma, CellSpread.cappedMean(line, mean, sigma), capped / n, .012);
        }
        close("zero buffers borrow the whole bill", CellSpread.shortfall(3, 0, 1.5), 3, 0);
        close("no bill borrows nothing", CellSpread.shortfall(0, 3, 1.5), 0, 0);
        close("a point amount below the line is counted", CellSpread.below(4, 4, 0), 1, 0);
        close("a point amount above the line is excluded", CellSpread.below(3, 4, 0), 0, 0);
        double sigma = CellSpread.CARD_SIGMA;
        double farLine = 4 * Math.exp(9 * sigma - sigma * sigma / 2);
        double tail = CellSpread.above(farLine, 4, sigma);
        check("a nine-sigma holding tail stays positive beside its balance tail", tail > 0);
        close("the holding tail is the model's direct normal tail", tail, BusinessDebtManager.normalCdf(-9), 1e-30);
    }
    /** A lender can stay closed while borrowers' exclusion shares age normally. */
    static void closedLender() {
        HouseholdBalance hb = new HouseholdBalance();
        hb.censusOnly((shape, tier) -> shape == FamilyStructure.SINGLE_ADULT && tier == PayTier.UNSKILLED ? 100 : 0);
        Household c = hb.cell(FamilyStructure.SINGLE_ADULT, PayTier.UNSKILLED);
        c.disposable = 1; c.savings = c.debt = c.revolving = 0; c.shutOut = 0;
        hb.setCardLenderOpenForHarness(false);
        c.settle(1, 0, 0, 2, 1, .03, null, 1);
        check("REV2 P1: a closed lender lends nothing despite the ordinary and unexpected gaps", c.borrowed() == 0 && c.unfunded() > 0);
        c.shutOut = 1;
        c.chargeOff();
        check("REV2 P1: exclusion ages, but no card room enters the next plan",
                c.shutOut() < 1 && c.creditRoom(1) == 0 && c.planningRoom() == 0);
        c.debt = 1; c.revolving = 1; c.shutOut = 0;
        check("REV2 P1: the closed lender also stops existing holders drawing more", c.creditRoom(1) == 0);
        c.debt = c.revolving = 0;
        hb.setCardLenderOpenForHarness(true);
        c.settle(1, 0, 0, 2, 1, .03, null, 1);
        check("REV2 P1: reopening the same lender funds the same household's gap", c.borrowed() > 0 && c.creditRoom(1) > 0);
    }
    /** A pure imported broker has no profit to list, at either currency scale. */
    static void brokerRounding() {
        System.out.println("--- The broker's tax reserve creates no phantom company ---");
        boolean zero = true, real = true;
        for (double factor : new double[]{.001, 1, 1_000_000}) {
            for (double bill : new double[]{.017471, .3, 3.14159265359, 100}) {
                for (double tax : new double[]{0, .075, .10, .125, .2135, .35, 1}) {
                    var broker = new ham.citybuildersim.sectors.BusinessServices();
                    double amount = bill * factor;
                    broker.sellHouseholdEmergencies(amount, tax);
                    broker.strike(); broker.bank(amount * tax);
                    zero &= broker.statement().preTaxIncome == 0 && broker.getCash() == 0;
                    // A genuine change of tax after the bill was booked must
                    // still produce either sign of margin, beyond round-off.
                    for (double sign : new double[]{-1, 1}) {
                        var changed = new ham.citybuildersim.sectors.BusinessServices();
                        changed.sellHouseholdEmergencies(amount, tax);
                        changed.strike();
                        double difference = sign * amount * 1e-8;
                        changed.bank(amount * tax + difference);
                        real &= changed.statement().preTaxIncome * sign < 0
                                && Math.abs(changed.statement().preTaxIncome + difference) <= 16 * Math.ulp(amount);
                    }
                }
            }
        }
        check("an exactly offset service receipt/import/VAT leaves no profit or cash at any scale", zero);
        check("both genuine gains and losses survive the broker's arithmetic-dust boundary", real);
    }
    static void month() {
        System.out.println("--- Thin buffers borrow; fixed minimum and all the cash have a recipient ---");
        WorkingHousehold thin = cell(4, 0, 0, 0), rich = cell(4, 400, 0, 0);
        for (WorkingHousehold c : new WorkingHousehold[]{thin, rich}) {
            double before = c.savings, debt = c.debt;
            c.settle(4, 1, 0, 1, .2, .04);
            close("household cash identity through minimum, shock and lending", c.savings - before,
                    4 - 1 - 1 - c.interest - c.repaid + c.borrowed - c.emergencyBill, 1e-10);
            close("card book identity including its fee", c.debt - debt, c.borrowed + c.loanFee - c.repaid, 1e-10);
            check("emergency bill is positive", c.emergencyBill > 0);
            check("holding share fits the acceptance rule", c.revolving > 0 && c.revolving <= c.cardAccess(4));
        }
        check("thinner buffers borrow more on the same bill", thin.borrowed > rich.borrowed);
        WorkingHousehold owe = cell(4, 0, 2, .5);
        owe.settle(4, 1, 0, 1, .2, .04);
        check("a current debtor pays minimum principal before the food", owe.minimumPaid > 0 && owe.afterFixed < 4 - 1 - owe.interest);
        check("extra paydown reaches the bank with the minimum", owe.repaid >= owe.minimumPaid);
        WorkingHousehold priority = cell(1, 0, 2, .5);
        priority.settle(1, .6, 0, .4, .2, .04);
        check("cash pays the fixed minimum before a food shortfall draws credit",
                priority.minimumPaid > 0 && priority.afterFixed < .4 && priority.borrowed > 0);
        WorkingHousehold poor = cell(1, 0, 2, .5), comfortable = cell(100, 0, 2, .5);
        poor.settle(1, 1, 0, .2, .2, .04);
        comfortable.settle(100, 1, 0, .2, .2, .04);
        check("inability to carry the minimum puts more balance behind", poor.lateDebt > comfortable.lateDebt);
        WorkingHousehold capped = cell(4, 0, 2, .1);
        capped.settle(4, 1, 0, 1, .2, 2);
        close("rate cap binds above its edge", capped.rate, Bank.CARD_RATE_CAP, 0);
        check("lower rate stays below the cap", owe.rate < Bank.CARD_RATE_CAP);
        for (double extraCapital : new double[]{0, .1}) {
            WorkingHousehold rationed = cell(1, 0, 2, .5);
            rationed.capitalCeiling = 2 + extraCapital;
            rationed.settle(1, 1, 0, .5, .2, .04);
            check("waterfall and emergency draws share one capital/interest allowance at " + extraCapital,
                    rationed.debt <= 2 + extraCapital + rationed.interest + 1e-12);
        }
        // Cause both sides of the minimum's crossover through the supplied bank rate.
        for (double side : new double[]{-1, 1}) {
            WorkingHousehold edge = cell(100, 0, 2, .5);
            double risk = HouseholdBalance.RISK_SLOPE * 2 / .5 / 100;
            double base = 12 * (Bank.CARD_MINIMUM - Bank.CARD_MIN_PRINCIPAL) - Bank.CARD_SPREAD - risk + side * 1e-6;
            edge.settle(100, 1, 0, 1, .2, base);
            close("minimum follows its " + (side < 0 ? "percentage" : "interest plus principal") + " branch",
                    edge.minimumDue, side < 0 ? Bank.CARD_MINIMUM * 2
                            : edge.interest + Bank.CARD_MIN_PRINCIPAL * 2, 1e-12);
        }
    }
    static void chargeOff() {
        System.out.println("--- Late stock cures or charges off; only its share is excluded ---");
        WorkingHousehold c = cell(4, .2, 2, .5);
        c.lateDebt = 1; c.lateShare = .2;
        double before = c.debt, late = c.lateDebt, savings = c.savings;
        double written = c.chargeOff();
        close("annual write-off hazard is the sourced ratio", 12 / Bank.CARD_CHARGE_OFF_MONTHS, 3.82 / 2.85, 1e-12);
        close("late balances cure and charge off once", c.lateDebt, late * (1 - Bank.CARD_CURE - 1 / Bank.CARD_CHARGE_OFF_MONTHS), 1e-12);
        close("ended debt is exactly the repayment and the bank's loss", before - c.debt, c.seizedSavings + written / c.households, 1e-12);
        close("seized savings reach the creditor", savings - c.savings, c.seizedSavings, 1e-12);
        close("write-off is the exact loss of the bank's loan book", c.cardWrittenOff * c.households, written, 0);
        close("only the charged-off share goes bankrupt", c.bankrupt, .2 / Bank.CARD_CHARGE_OFF_MONTHS * c.households, 1e-12);
        check("neighbours still have credit", c.shutOut > 0 && c.creditRoom(4) > 0);
        c.lateDebt = c.lateShare = 0;
        double excluded = c.shutOut;
        c.chargeOff();
        close("exclusion decays on the year's own hazard", c.shutOut, excluded * (1 - 1.0 / HouseholdBalance.LOCKOUT_MONTHS), 1e-12);
        WorkingHousehold all = cell(4, 0, 0, 0); all.shutOut = 1;
        close("all excluded cannot borrow", all.creditRoom(4), 0, 0);
        all.shutOut = 0;
        check("an accepted household can borrow again", all.creditRoom(4) > 0);
        close("no income fails the card test", Bank.cardAccess(0, PayTier.UNSKILLED, 0), 0, 0);
        close("bottom tier has its estimated access", Bank.cardAccess(1, PayTier.UNSKILLED, 0), Bank.CARD_BOTTOM_ACCESS, 0);
        close("other tiers have their own access", Bank.cardAccess(1, PayTier.COLLEGE, 0), Bank.CARD_OTHER_ACCESS, 0);
        check("late balances need a larger loss allowance", Bank.cardAllowance(2, 1) > Bank.cardAllowance(2, 0));
        Bank bank = new Bank();
        bank.refresh(1, 1_000_000, 0, 0, 0, 0);
        bank.closeMonth();
        close("sound neutral card rate is the agreed rate", bank.cardRate(DebtManager.NEUTRAL_RATE), Bank.CARD_NEUTRAL_RATE, 1e-12);
        for (double tax : new double[]{0, .2}) {
            ham.citybuildersim.sectors.BusinessServices seller = new ham.citybuildersim.sectors.BusinessServices();
            seller.sellHouseholdEmergencies(10, tax);
            close("emergency receipt has one household buyer at tax " + tax, seller.pending().salesToHouseholds, 10, 0);
            close("emergency receipt is named without inventing a seat-service good", seller.pending().householdServices, 10, 0);
            close("emergencies produce no domestic seat-service units", seller.pending().unitsSold.getOrDefault(Good.SUPPORT_WORK, 0.0), 0, 0);
            close("emergency receipt pays its imports and its sales tax at " + tax,
                    seller.pending().imports + seller.pending().salesToHouseholds * tax, 10, 1e-12);
            seller.redenominate(.01);
            close("the pending named receipt follows a currency reform", seller.pending().householdServices, .1, 1e-12);
        }
    }
    static void save() {
        System.out.println("--- Card stocks and settled flows survive the cell save and currency reform ---");
        HouseholdBalance balance = new HouseholdBalance();
        Household c = balance.cell(FamilyStructure.SINGLE_ADULT, PayTier.UNSKILLED);
        c.households = 100; c.savings = 2; c.debt = 1; c.revolving = .4;
        c.lateDebt = .2; c.lateShare = .05; c.shutOut = .1;
        c.minimumPaid = .02; c.minimumDue = .04; c.emergencyBill = .3; c.cardWrittenOff = .01; c.disposable = 3; c.bankrupt = 2; c.depositIncome = .07;
        c.studentDebt = 2; c.studentRepaid = .02; c.studentInterest = .005;
        c.rate = .24;
        c.bankruptcyLeaving = .01; c.bankruptcyCash = .007;
        HouseholdBalance restored = new HouseholdBalance();
        check("cell save is accepted", restored.restoreCells(balance.cellKeys(), balance.toCellSaveArray(), Equity.COMPANIES));
        Household d = restored.cell(FamilyStructure.SINGLE_ADULT, PayTier.UNSKILLED);
        check("all four card stocks round-trip", c.revolving == d.revolving && c.lateDebt == d.lateDebt && c.lateShare == d.lateShare && c.shutOut == d.shutOut);
        check("settled card figures round-trip", c.minimumPaid == d.minimumPaid && c.minimumDue == d.minimumDue && c.emergencyBill == d.emergencyBill && c.cardWrittenOff == d.cardWrittenOff && c.disposable == d.disposable && c.bankrupt == d.bankrupt && c.depositIncome == d.depositIncome);
        check("settled student service is saved alongside card service", c.studentRepaid == d.studentRepaid && c.studentInterest == d.studentInterest);
        close("the applied APR is saved rather than repriced from the closing balances", d.rate, c.rate, 0);
        check("REV-HC1 P2: pending debt-free departure share and cash survive the cell save",
                c.bankruptcyLeaving == d.bankruptcyLeaving && c.bankruptcyCash == d.bankruptcyCash);
        d.redenominate(.01);
        close("late balance follows the money", d.lateDebt, c.lateDebt * .01, 0);
        close("holder share survives a currency reform", d.revolving, c.revolving, 0);
        close("REV-HC1 P2: pending departure cash redenominates", d.bankruptcyCash, c.bankruptcyCash * .01, 0);
        close("REV-HC1 P2: pending departure share does not redenominate", d.bankruptcyLeaving, c.bankruptcyLeaving, 0);
        check("old saves are refused at the new format", GameVersion.SAVE_FORMAT == GameVersion.OLDEST_LOADABLE_FORMAT);
    }
    static void neighbours() {
        System.out.println("--- A cardholder does not bar its debt-free neighbours from investing ---");
        HouseholdBalance b = new HouseholdBalance();
        Household c = b.cell(FamilyStructure.SINGLE_ADULT, PayTier.UNSKILLED);
        c.households = 1000; c.disposable = 4; c.savings = 100; c.debt = 2; c.revolving = .2;
        close("the paper budget belongs to the debt-free share", b.spareForPaper(),
                (100 - HouseholdBalance.SHARE_CUSHION_MONTHS * 4) * 1000 * .8, 1e-8);
        check("debt-free neighbours can subscribe to shares", b.subscribe(0, 1000, 1) > 0);
        c.shutOut = .3;
        close("exclusion removes only its own share", b.debtFreeShare(c), .5, 1e-12);
        c.shutOut = 0; c.revolving = 1;
        close("a wholly indebted cell has no paper budget", b.spareForPaper(), 0, 0);
        c.revolving = 0;
        close("raw debt without a supplied distribution is conservatively ineligible", b.debtFreeShare(c), 0, 0);
    }
    static void census() {
        System.out.println("--- All four card stocks follow the same families across a birthday ---");
        HouseholdBalance b = new HouseholdBalance();
        Household child = b.cell(FamilyStructure.COUPLE_CHILD, PayTier.UNSKILLED);
        child.households = 100; child.debt = 1; child.revolving = .4;
        child.lateDebt = .2; child.lateShare = .05; child.shutOut = .1;
        b.censusOnly((shape, tier) -> shape == FamilyStructure.COUPLE_CHILD && tier == PayTier.UNSKILLED ? 100 : 0);
        close("unchanged census keeps the holder share", child.revolving, .4, 1e-12);
        b.censusOnly((shape, tier) -> shape == FamilyStructure.COUPLE_TEEN && tier == PayTier.UNSKILLED ? 100 : 0);
        Household teen = b.cell(FamilyStructure.COUPLE_TEEN, PayTier.UNSKILLED);
        close("birthday carries the card balance", teen.debt, 1, 1e-12);
        close("birthday carries holders", teen.revolving, .4, 1e-12);
        close("birthday carries late balances", teen.lateDebt, .2, 1e-12);
        close("birthday carries late holders", teen.lateShare, .05, 1e-12);
        close("birthday carries exclusion", teen.shutOut, .1, 1e-12);
        close("a birthday writes off no loan", b.getWrittenOff(), 0, 0);
    }
    static void city() {
        System.out.println("--- Real city money, annual surveys, saved reads and continuation ---");
        PrintStream real = System.out, quiet = new PrintStream(OutputStream.nullOutputStream());
        Game g = LongPlaytest.probeCity(GameFiles.scratch("cardcheck"), null);
        double bills = 0, worst = 0, worstHouseholds = 0;
        int billMonths = 0, drawMonths = 0, minimumMonths = 0, lateMonths = 0, chargeOffMonths = 0;
        System.setOut(quiet);
        try {
            for (int month = 0; month < 24; month++) {
                g.simulateMonths(1);
                bills += g.getHouseholdBalance().totalEmergencyBills();
                HouseholdBalance balance = g.getHouseholdBalance();
                if (balance.totalEmergencyBills() > 0) billMonths++;
                if (balance.totalBorrowed() > 0) drawMonths++;
                if (balance.cells().stream().anyMatch(c -> c.minimumDue > 0)) minimumMonths++;
                if (balance.cells().stream().anyMatch(c -> c.lateDebt > 0)) lateMonths++;
                if (balance.getWrittenOff() > 0) chargeOffMonths++;
                MoneyAudit.Result audit = g.getLastMoneyAudit();
                worst = Math.max(worst, Math.abs(audit.residual) / MoneyAudit.tolerance(audit.moved()));
                MoneyAudit.Households households = g.getLastHouseholdsAudit();
                worstHouseholds = Math.max(worstHouseholds, Math.abs(households.residual)
                        / MoneyAudit.tolerance(Math.abs(households.toHouseholds) + Math.abs(households.fromHouseholds)));
            }
        } finally { System.setOut(real); }
        System.out.printf("24 measured months: emergency bills %d, card draws %d, minimums %d, late stock %d, charge-offs %d%n",
                billMonths, drawMonths, minimumMonths, lateMonths, chargeOffMonths);
        check("city actually paid unexpected bills in the measured months", bills > 0);
        check("every measured month conserves the money pools (worst tolerance ratio " + worst + ")", worst <= 1);
        check("every measured month conserves household cash (worst tolerance ratio " + worstHouseholds + ")", worstHouseholds <= 1);
        check("the yearly surveys were actually recorded", g.getHistorySave().getDebtSurveys().size() >= 2);
        double[] beforeReads = g.getHouseholdBalance().toCellSaveArray();
        for (int i = 0; i < 3; i++) DebtStatistics.survey(g.getHouseholdBalance(), g.getMonth());
        check("reading every debt group changes no saved cell field", java.util.Arrays.equals(beforeReads, g.getHouseholdBalance().toCellSaveArray()));
        System.setOut(quiet);
        Game back;
        boolean saved;
        try {
            saved = g.saveGame(10, "card check").ok;
            back = new Game(g.getGameFiles());
            back.loadGameSave(10);
        } finally { System.setOut(real); }
        check("scratch city saves in the assistant slot", saved);
        boolean sameCards = true;
        for (Household c : g.getHouseholdBalance().cells()) {
            Household d = back.getHouseholdBalance().cellByKey(c.key());
            sameCards &= d != null && c.revolving == d.revolving && c.lateDebt == d.lateDebt
                    && c.lateShare == d.lateShare && c.shutOut == d.shutOut && c.minimumPaid == d.minimumPaid
                    && c.emergencyBill == d.emergencyBill && c.cardWrittenOff == d.cardWrittenOff
                    && c.minimumDue == d.minimumDue && c.disposable == d.disposable && c.bankrupt == d.bankrupt;
            sameCards &= c.rate == d.rate;
        }
        check("all card stocks and settled statistics survive the game loader", sameCards);
        check("annual surveys survive the history loader", g.getHistorySave().getDebtSurveys().equals(back.getHistorySave().getDebtSurveys()));
        double pendingServices = g.getSectors().businessServices().pending().householdServices;
        check("a positive pending emergency receipt survives its ledger save", pendingServices > 0
                && pendingServices == back.getSectors().businessServices().pending().householdServices);
        double closedServices = g.getSectors().businessServices().statement().otherParts.getOrDefault(Sector.HOUSEHOLD_SERVICES, 0.0);
        check("a positive struck emergency receipt survives in its named statement", closedServices > 0
                && closedServices == back.getSectors().businessServices().statement().otherParts.getOrDefault(Sector.HOUSEHOLD_SERVICES, 0.0));
        DebtStatistics.Snapshot live = g.getHouseholdBalance().debtStatistics();
        DebtStatistics.Snapshot loaded = back.getHouseholdBalance().debtStatistics();
        if (!live.equals(loaded)) {
            System.out.printf("Current statistics live/load: DTI %.12f / %.12f; assets %.12f / %.12f; service %.12f / %.12f; high %.12f / %.12f%n",
                    live.debtToDisposableIncome(), loaded.debtToDisposableIncome(), live.debtToAssets(), loaded.debtToAssets(),
                    live.debtServiceRatio(), loaded.debtServiceRatio(), live.payingOver40(), loaded.payingOver40());
            for (Game city : new Game[]{g, back}) {
                HouseholdBalance b = city.getHouseholdBalance();
                double financial = 0, cars = 0;
                for (Household c : b.cells()) {
                    financial += b.financialAssets(c) * c.households;
                    cars += (b.assets(c) - b.financialAssets(c)) * c.households;
                }
                System.out.printf("Asset inputs: savings %.12f, abroad %.12f, paper %.12f, bonds %.12f, shares %.12f, financial %.12f, cars %.12f%n",
                        b.totalSavings(), b.totalAbroadValue(), b.marketValueOfPaper(), b.marketValueOfBonds(), b.marketValueOfShares(), financial, cars);
            }
        }
        check("all current statistics survive the loader", live.equals(loaded));
        check("REV-HC1 P5: format-62 card, service, loan-book and capital continuation", continues(g, back));
        DebtStatistics.Measure cards = live.perHousehold().get(DebtStatistics.Kind.CARD);
        System.out.printf("Closing card figures: holders %.3f%%, late households %.3f%%, late balances %.3f%%, annualised charge-off %.3f%%%n",
                100 * cards.shareHolding(), 100 * cards.lateShare(), 100 * cards.delinquency(), 100 * cards.annualChargeOff());
        int cutMonth = g.getHistorySave().getDebtSurveys().get(0).month();
        check("replay history excludes later surveys", g.getHistorySave().cutAt(cutMonth).getDebtSurveys().stream().allMatch(s -> s.month() <= cutMonth));
    }
    public static void main(String[] args) {
        spread(); month(); chargeOff(); save(); neighbours(); census(); reviewFlows(); brokerRounding(); closedLender();
        check("REV-HC1 P4: native format-60 save/history are refused with bytes and running city intact", SaveFileCheck.nativeCard60Refusal());
        check("HC1 format62: native format61 refusal keeps bytes and the running loaded city intact", SaveFileCheck.nativeCard61Refusal(false));
        city();
        System.out.println("CardCheck: " + fails + " failures");
        System.exit(fails == 0 ? 0 : 1);
    }

    static void reviewFlows() {
        System.out.println("--- REV-HC1: estates, debt-free bankruptcy departures and rejected credit ---");
        HouseholdFlowCheck.Town estate = new HouseholdFlowCheck.Town();
        Household dead = estate.cell(FamilyStructure.ELDER_ALONE, PayTier.UNSKILLED, 20);
        dead.savings = 3; dead.debt = 2; dead.revolving = .6; dead.lateDebt = 1; dead.lateShare = .1; dead.shutOut = .2;
        Household heir = estate.cell(FamilyStructure.SINGLE_ADULT, PayTier.UNSKILLED, 100);
        heir.debt = 4; heir.revolving = .4;
        estate.bal.setFlows(0, 0, 0, 0, .25, 0, 0);
        estate.expectRetired(FamilyStructure.ELDER_ALONE, 15).run();
        check("REV-HC1 P1: an estate passes no late debt, holder share or credit exclusion to heirs",
                heir.lateDebt == 0 && heir.lateShare == 0 && heir.shutOut == 0 && heir.revolving == .4);
        close("REV-HC1 P1: the heirs' performing book cannot be charged off by the dead's arrears", heir.chargeOff(), 0, 0);
        close("REV-HC1 P1: the estate still repays its own debt", estate.bal.getEstateRepaid(), 10, 1e-12);

        HouseholdFlowCheck.Town leaving = new HouseholdFlowCheck.Town();
        Household source = leaving.cell(FamilyStructure.SINGLE_ADULT, PayTier.UNSKILLED, 100);
        source.debt = 5; source.revolving = .6; source.lateDebt = 1; source.lateShare = .2;
        Household neighbour = leaving.cell(FamilyStructure.COUPLE, PayTier.ELITE, 100);
        neighbour.debt = 2; neighbour.revolving = .3;
        source.chargeOff();
        double leavers = source.bankrupt * HouseholdBalance.LEAVE_ON_BANKRUPTCY;
        double book = leaving.bal.totalDebt(), late = source.lateDebt * source.households;
        double excluded = source.shutOut * source.households;
        leaving.expect(FamilyStructure.SINGLE_ADULT, PayTier.UNSKILLED, 100 - leavers).run();
        close("REV-HC1 P2: discharged leavers cause no second loss of performing debt", leaving.bal.getWrittenOff(), 0, 1e-10);
        close("REV-HC1 P2: only zero-card-debt households depart", leaving.bal.totalDebt(), book, 1e-10);
        close("REV-HC1 P2: surviving late balances stay in their source cell", source.lateDebt * source.households, late, 1e-10);
        close("REV-HC1 P2: the excluded leavers leave their own exclusion behind", source.shutOut * source.households, excluded - leavers, 1e-10);
        close("REV-HC1 P2: neighbours retain their own book", neighbour.debt, 2, 1e-12);

        HouseholdFlowCheck.Town families = new HouseholdFlowCheck.Town();
        Household parents = families.cell(FamilyStructure.COUPLE_CHILD, PayTier.UNSKILLED, 100);
        families.youngCell(FamilyStructure.SINGLE_ADULT, PayTier.ELITE, 100);
        parents.savings = 4; parents.debt = 1; parents.revolving = .5; parents.lateDebt = .5; parents.lateShare = .2;
        parents.chargeOff();
        double departing = parents.bankrupt * HouseholdBalance.LEAVE_ON_BANKRUPTCY;
        double cashToLeave = parents.bankruptcyCash * parents.households;
        double allCash = families.bal.totalSavings();
        PopulationCohorts people = HouseholdMemoryCheck.staged(0, 100, 0, 300, 100);
        var request = families.bal.prepareBankruptcyDepartures(people);
        close("REV-HC1 P2: the departure count is the source family's own three heads", request.people(), 3 * departing, 1e-12);
        var admitted = families.bal.limitBankruptcyDepartures(request, request.people() / 2);
        people.migrate(-admitted.people(), admitted);
        close("REV-HC1 P2: bankruptcy leaves the other adult stage alone", people.get(AdultStage.YOUNG), 100, 1e-12);
        close("REV-HC1 P2: only the source family's child leaves", people.get(AgeBand.CHILD), 100 - departing / 2, 1e-12);
        families.expect(FamilyStructure.COUPLE_CHILD, PayTier.UNSKILLED, 100 - departing / 2).run();
        close("REV-HC1 P2: a capped departure takes only its own post-seizure cash",
                families.bal.totalSavings(), allCash - cashToLeave / 2, 1e-10);
        close("REV-HC1 P2: capped source departures still write off no second debt", families.bal.getWrittenOff(), 0, 1e-10);

        for (double shut : new double[]{0, .2, 1}) {
            WorkingHousehold rejected = cell(1, 0, 0, 0);
            rejected.shutOut = shut;
            double access = rejected.cardAccess(1);
            rejected.settle(1, 0, 0, 1.5, 0, .04);
            close("REV-HC1 P3: rejected ordinary borrowers remain unfunded at exclusion " + shut,
                    rejected.unfunded, .5 * (1 - access), 1e-12);
            close("REV-HC1 P3: only accepted borrowers receive the ordinary advance at exclusion " + shut,
                    rejected.borrowed - rejected.emergencyBill, .5 * access, 1e-12);
        }
    }

    /** Advance both real cities; a matching reload must also make the same next month. */
    static boolean continues(Game live, Game loaded) {
        int month = live.getMonth();
        var gson = new com.google.gson.Gson();
        var beforeLive = gson.toJsonTree(live.prepareSaveForChecks(10, "continuation").data()).getAsJsonObject();
        var beforeLoaded = gson.toJsonTree(loaded.prepareSaveForChecks(10, "continuation").data()).getAsJsonObject();
        var openingFields = SaveFileCheck.jsonDiff(beforeLive, beforeLoaded, java.util.Set.of("savedAt"));
        boolean same = openingFields.isEmpty();
        // Freight buys fuel before the next goods clearing. Its opening band is
        // derived from the saved shares, so a fixture that edits those shares must
        // apply them before saving, just as the production haul does.
        same &= live.getMarkets().get(Good.DIESEL).importPrice() == loaded.getMarkets().get(Good.DIESEL).importPrice();
        PrintStream out = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        try { live.simulateMonths(1); loaded.simulateMonths(1); }
        finally { System.setOut(out); }
        same &= live.getMonth() == month + 1 && loaded.getMonth() == month + 1;
        for (Household c : live.getHouseholdBalance().cells()) {
            Household d = loaded.getHouseholdBalance().cellByKey(c.key());
            same &= d != null && c.households == d.households && c.debt == d.debt
                    && c.revolving == d.revolving && c.lateDebt == d.lateDebt && c.lateShare == d.lateShare && c.shutOut == d.shutOut
                    && c.bankruptcyLeaving == d.bankruptcyLeaving && c.bankruptcyCash == d.bankruptcyCash
                    && c.minimumDue == d.minimumDue && c.minimumPaid == d.minimumPaid && c.emergencyBill == d.emergencyBill;
        }
        var a = live.getSectors().businessServices().pending();
        var b = loaded.getSectors().businessServices().pending();
        same &= a.householdServices == b.householdServices && a.salesToHouseholds == b.salesToHouseholds && a.imports == b.imports;
        same &= live.getBank().getHouseholdBook() == loaded.getBank().getHouseholdBook()
                && live.getBank().equity() == loaded.getBank().equity();
        if (!same) System.out.printf("Continuation: opening differences %s; loans %.17g / %.17g; capital %.17g / %.17g%n",
                openingFields, live.getBank().getHouseholdBook(), loaded.getBank().getHouseholdBook(), live.getBank().equity(), loaded.getBank().equity());
        return same;
    }
}

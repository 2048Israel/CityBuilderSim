package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Reading the city must not change the city.
 *
 * WHY THIS EXISTS
 *
 * Three of the worst bugs this project has had were the same bug:
 *
 *   printCommercialInfo()      banked a month of net income every time it ran,
 *                              so opening the sector screen twice paid the
 *                              shops twice
 *   getIndustrialTaxIncome()   recomputed industry's month from live fields and
 *                              rewrote two report figures doing it, so a
 *                              reloaded city collected $0 where the live one
 *                              collected $89,347
 *   getStoreIncome()           assigned productsSold - uncapped by stock - on
 *                              the tax path, and updateCommercialHandler() then
 *                              took that quantity off the shelf
 *
 * Each was found by hand, months apart, after it had already corrupted
 * something. The backlog ends with a note recommending "a periodic sweep: any
 * get*() on the tax or income path that assigns a field is a bug waiting for a
 * save to expose it". This is that sweep, automated.
 *
 * HOW IT WORKS
 *
 * It does not inspect the code. It plays a real city into a state where every
 * sector has money moving, fingerprints ~90 fields, then calls every read path
 * the UI and the treasury use - repeatedly, in a jumbled order, the way a player
 * clicking between screens would - and fingerprints again. Any field that moved
 * is a getter that is not a getter, including ones that do not exist yet.
 *
 * The repetition is the point. A read path that mutates ONCE and then settles
 * would pass a before/after comparison; one that accumulates shows up as a field
 * that drifts further the more the screens are opened.
 */
public class ReadPathCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-58s %s%n", label, ok ? "OK" : "FAIL");
    }

    static BuildingsTemplate template(Game game, String name) {
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException("no template named " + name);
    }

    /**
     * The bank's own fields beside NewGameCheck's (0.7.7): its price is now
     * struck from records it keeps, and a read that struck it again would
     * move the price and nothing in the shared snapshot.
     */
    static void bankPrint(Game g, Map<String, Double> into) {
        Bank b = g.getBank();
        into.put("bank.cash", b.getCash());
        into.put("bank.depositRate", b.depositRate());
        into.put("bank.profitLastMonth", b.getProfitLastMonth());
        into.put("bank.lateProfit", b.lateProfit());
        into.put("bank.runningCostRate", b.runningCostRate());
        into.put("bank.expectedLossRate", b.expectedLossRate());
        into.put("bank.windowShare", b.windowShare());
        into.put("bank.feeIncome", b.feeIncome());
        double[] record = b.pricingHistoryToSave();
        for (int i = 0; i < record.length; i++) into.put("bank.record" + i, record[i]);
        // ...and 0.7.8's: what it set aside, its target's record, its month.
        into.put("bank.allowance", b.getAllowance());
        into.put("bank.openingAllowance", b.getOpeningAllowance());
        into.put("bank.worstLossRate", b.getWorstLossRate());
        double[] capital = b.capitalRecordToSave();
        for (int i = 0; i < capital.length; i++) into.put("bank.capital" + i, capital[i]);
        double[] lines = b.monthLinesToSave();
        for (int i = 0; i < lines.length; i++) into.put("bank.line" + i, lines[i]);
        for (java.util.Map.Entry<String, double[]> e : b.allowanceToSave().entrySet()) {
            for (int i = 0; i < e.getValue().length; i++) into.put("bank.book." + e.getKey() + i, e.getValue()[i]);
        }
        // ...and 0.7.9's: its year of statements and its record of rescues.
        double[] year = b.statementYearToSave();
        for (int i = 0; i < year.length; i++) into.put("bank.year" + i, year[i]);
        double[] solvency = b.solvencyToSave();
        for (int i = 0; i < solvency.length; i++) into.put("bank.solvency" + i, solvency[i]);
        into.put("bank.monthKnown", b.isMonthKnown() ? 1.0 : 0.0);
        // ...and 0.7.8's slices: what each sector owes and has lost, and the
        // month's defaults, which the Lending page and the notice read.
        BusinessDebtManager cr = g.getEconomyManager().getBusinessDebtManager();
        for (String s : cr.sectors()) {
            into.put("credit.owed." + s, cr.getPrincipal(s));
            into.put("credit.lost." + s, cr.getWrittenOffTotal(s));
            into.put("credit.month." + s, cr.getWrittenOffThisMonth(s));
            into.put("credit.defaulted." + s, cr.getDefaultedThisMonth(s));
            into.put("credit.record." + s, (double) cr.getRestructureCount(s));
            // ...and the landlords' insured mortgages (0.7.11): what is owed on
            // them, what their payments took this month, and what the insurance
            // paid the bank - this month and over the city's life.
            into.put("credit.mortgages." + s, cr.getMortgagePrincipal(s));
            into.put("credit.mortgageRepaid." + s, cr.getMortgageRepaidThisMonth(s));
            into.put("credit.claimed." + s, cr.getInsuredWrittenOffThisMonth(s));
            into.put("credit.claimedEver." + s, cr.getInsuredWrittenOffTotal(s));
            into.put("credit.premiums." + s, cr.getPremiumsThisMonth(s));
        }
        into.put("credit.premiumsEver", cr.getPremiumsTotal());
        into.put("credit.insuredRate", cr.getInsuredMortgageRate());
        for (Mortgage m : cr.getMortgages()) {
            into.put("mortgage." + System.identityHashCode(m) + ".owed", m.getOutstandingPrincipal());
            into.put("mortgage." + System.identityHashCode(m) + ".rate", m.getAnnualRate());
            into.put("mortgage." + System.identityHashCode(m) + ".term", (double) m.getRemainingMonths());
            into.put("mortgage." + System.identityHashCode(m) + ".left", (double) m.getAmortizationLeft());
        }
        into.put("bank.mortgageBook", b.getMortgageBook());
        into.put("bank.insuranceClaims", b.getInsuranceClaims());
        // ...and 0.7.13's: its year of balance sheets and its loans by sector,
        // which the Balance sheet page reads; and the treasury's rollover, its
        // ledger and its record, which the Finances tab's borrow page reads.
        double[] sheets = b.sheetYearToSave();
        for (int i = 0; i < sheets.length; i++) into.put("bank.sheet" + i, sheets[i]);
        // ...and its equity in two parts, carried at the top of the month (round 2)
        into.put("bank.paidInOpening", b.paidInOpening());
        into.put("bank.retainedOpening", b.retainedOpening());
        into.put("bank.splitKnown", b.knowsEquitySplit() ? 1.0 : 0.0);
        for (int s = 0; s < Sectors.KEYS.length; s++) {
            into.put("bank.loansTo" + s, b.getLoansToSector(s));
            into.put("bank.interimTo" + s, b.getInterimToSector(s));
        }
        double[] ledger = g.getRollover().ledgerToSave();
        for (int i = 0; i < ledger.length; i++) into.put("rollover.ledger" + i, ledger[i]);
        double[] rolled = g.getRollover().recordToSave();
        for (int i = 0; i < rolled.length; i++) into.put("rollover.record" + i, rolled[i]);
        into.put("rollover.mode", (double) g.getRolloverMode().ordinal());
        // ...and round 2's: its leverage ratio against its requirements, and
        // how long its book has not kept its branches' staff.
        into.put("bank.exposure", b.exposure());
        into.put("bank.leverageRatio", Math.min(1e6, b.leverageRatio()));
        into.put("bank.minimumEquity", b.minimumEquity());
        into.put("bank.targetEquity", b.targetEquity());
        into.put("bank.uncoveredMonths", (double) b.getUncoveredMonths());
        into.put("credit.insuredRationed", cr.isInsuredRationed() ? 1.0 : 0.0);
        into.put("budget.mortgagePremiums", g.getEconomyManager().getNationalAccounts().getMortgagePremiums());
        into.put("budget.mortgageClaims", g.getEconomyManager().getNationalAccounts().getMortgageClaims());
        // ...and 0.7.12's: every bond and who holds it, every book and what
        // rests on it, the market's month and life, the cells' own bonds,
        // the bank's bonds and its concentration, each sector's mix.
        BondMarket bm = g.getBondMarket();
        BondMarket.State state = bm.toState();
        for (int i = 0; i < state.month.length; i++) into.put("bonds.month" + i, state.month[i]);
        for (int i = 0; i < state.life.length; i++) into.put("bonds.life" + i, state.life[i]);
        into.put("bonds.books", (double) state.books.size());
        into.put("bonds.nextId", (double) state.nextId);
        for (CorporateBond x : bm.getBonds()) {
            String k = "bond." + x.id();
            into.put(k + ".face", x.face());
            into.put(k + ".households", x.households());
            into.put(k + ".bank", x.bank());
            into.put(k + ".bankCost", x.bankCost());
            into.put(k + ".world", x.world());
            into.put(k + ".companies", x.companiesTotal());
            OrderBook book = bm.bookOf(x);
            into.put(k + ".bids", (double) book.bids().size());
            into.put(k + ".asks", (double) book.asks().size());
            into.put(k + ".bidDepth", book.depth(OrderBook.Side.BUY, 1e-9));
            into.put(k + ".askDepth", book.depth(OrderBook.Side.SELL, 1e9));
            into.put(k + ".last", Double.isNaN(book.lastPrice()) ? -1 : book.lastPrice());
        }
        into.put("households.bonds", g.getHouseholdBalance().totalBonds());
        into.put("households.bondRatio", g.getHouseholdBalance().getBondRatio());
        into.put("bank.bondBook", b.getBondBook());
        into.put("bank.bondWeighted", b.getBondWeighted());
        into.put("bank.concentration", b.getConcentrationAddOn());
        into.put("bank.herfindahl", b.getConcentrationHerfindahl());
        for (String s : cr.sectors()) {
            into.put("credit.bonds." + s, cr.getBondPrincipal(s));
            into.put("credit.bondsLost." + s, cr.getBondWrittenOffTotal(s));
            into.put("credit.concentration." + s, cr.getConcentrationCharge(s));
            into.put("credit.rate." + s, cr.getRate(s));
            into.put("cash." + s, g.getEconomyManager().getSectorCash(s));
        }
    }

    /** What rests on every bond's book. */
    static int restingOrders(BondMarket bm) {
        int n = 0;
        for (CorporateBond x : bm.getBonds()) n += bm.bookOf(x).bids().size() + bm.bookOf(x).asks().size();
        return n;
    }

    /**
     * Everything a screen can ask the game, called the way a player browsing
     * would call it.
     *
     * Deliberately includes the printers. printCommercialInfo() is where this
     * class of bug started, and the JavaFX sector screens are pure readers of
     * the same report fields these print.
     */
    static void readEverything(Game g) {

        EconomyManager e = g.getEconomyManager();
        ham.citybuildersim.sectors.Retail c = g.getSectors().retail();
        ham.citybuildersim.sectors.RealEstate re = g.getSectors().realEstate();
        Sector ih = g.getSectors().industry();
        Sector hh = g.getSectors().heavyIndustry();
        ham.citybuildersim.sectors.Mining mh = g.getSectors().mining();
        ServicesManager s = g.getServicesManager();

        // the treasury's own read - the one that was mutating
        e.getTaxIncome();
        e.getTotalIncome();
        // ...and the health service's price and premium, as the Health page
        // and the Services tab read them (2026-09-19)
        e.getHealthPremiums();
        e.getNationalAccounts().getHealthPremiums();
        e.getNationalAccounts().getTotalRevenue();
        e.getTaxPolicy().getHealthFeeScale();
        e.getTaxPolicy().getHealthPremiumRate();
        for (CareType care : CareType.values()) {
            g.getHealthcare().feesFrom(care);
            g.getHealthcare().feeNow(care);
            g.getHealthcare().getServed(care);
            g.getHealthcare().getOffered(care);
            g.getHealthcare().getPricedOut(care);
            g.getHealthcare().getAffordability(care);
            g.careAffordability(care);
        }
        g.getHealthcare().getPricedOutTotal();
        g.getHealthcare().fullTreatmentFees();
        g.getHealthcare().getFullFees();
        g.getHealthcare().breakEvenScale();
        g.getHealthcare().getCostRecovery();
        g.getHouseholdBalance().getCareSkipped();
        g.getHouseholdBalance().carePaidShares();
        g.getHouseholds().getHealthPremiums();
        for (int r = 0; r < g.getHouseholds().getRowCount(); r++) {
            g.getHouseholds().getRowHealthPremiums(r);
            g.getHouseholds().getRowCareBilled(r);
            g.getHouseholds().getRowCareFull(r);
            g.getHouseholds().getRowDisposable(r);
        }
        e.getExpenses();
        e.getMonthGdp();
        // ...and the Schools page's five dials and their previews (2026-09-21):
        // the grant under a basis the city has not chosen, the interest a
        // rate would bring in, the fee at a scale it has not set
        e.getStudentLoanInterest();
        e.getNationalAccounts().getStudentLoanInterest();
        e.getTaxPolicy().getGrantBasis();
        e.getTaxPolicy().getGrantAmount();
        e.getTaxPolicy().getStudentLoanRate();
        e.getTaxPolicy().getTuitionScale();
        g.studentGrantBill();
        g.getUnskilledWage();
        for (TaxPolicy.GrantBasis basis : TaxPolicy.GrantBasis.values()) {
            e.getTaxPolicy().maxGrantAmount(basis);
            g.studentGrantBillUnder(basis, .5);
            g.grantPerStudentUnder(basis, .5);
            g.grantAmountAs(basis);
        }
        g.getHouseholdBalance().totalGraduateDebt();
        g.getHouseholdBalance().studentInterestAt(.05);
        g.getHouseholdBalance().totalStudentInterest();
        g.getHouseholdBalance().getStudentLoanRate();
        g.getEducation().studentBodyTuition();
        g.getEducation().getTuitionScale();
        for (EducationType course : EducationType.values()) {
            g.getEducation().feeFor(course);
            g.getEducation().feeAtOne(course);
            g.getEducation().outOfPocket(course);
        }

        // the bridge on the Government tab, and the journal it opens into
        // (2026-09-18) - the row is read every time the panel is rebuilt
        g.getTreasuryChange();
        g.getTreasuryUnexplained();
        g.getTreasuryJournal();
        g.getTreasuryResidual();
        // ...and the desk's re-mark on the bank's statement
        g.getBank().getMarkChange();
        // the bank's price and what it is made of, its savers' share and its
        // fees (0.7.7) - the Bank tab's build-up reads every one of them
        double dial = g.getDebtManager().getPolicyRate();
        g.getBank().prime(dial);
        g.getBank().householdRate(dial);
        g.getBank().carryRate(dial);
        g.getBank().lendingRate(dial);
        g.getBank().fundsTransferPrice(dial, Bank.PRIME_TERM_MONTHS);
        g.getBank().capitalCharge(dial, Bank.PRIME_TERM_MONTHS, Bank.RISK_BUSINESS);
        g.getBank().fundingPosition();
        g.getBank().depositShare();
        g.getBank().accountFee(g.getPriceIndex().getIndex());
        g.getBank().feeIncome();
        g.getBank().lateProfit();
        g.getBank().getProfitAfterTaxLastMonth(.15);
        g.getBank().pricingHistoryToSave();
        g.getBank().lastMonthToSave();
        // its capital, what it set aside and what it does with its profit
        // (0.7.8) - the Bank tab (0.7.9) and the month's lenders read them
        g.getBank().provisions();
        g.getBank().getProvisionCharge();
        g.getBank().getWriteOffsBeyondAllowance();
        g.getBank().netLoans();
        g.getBank().capitalTarget();
        g.getBank().capitalTop();
        g.getBank().trailingLossRate();
        g.getBank().targetEquity();
        g.getBank().topEquity();
        g.getBank().excessCapital();
        g.getBank().payoutStance();
        g.getBank().payoutDecision();
        g.getBank().dividendDue(g.getBank().getProfitAfterTaxLastMonth(.15));
        g.getBank().buysBackOwnShares();
        g.getBank().issuesOwnShares();
        g.getBank().dividendsOverYear();
        g.getBank().buybacksOverYear();
        g.getBank().returnOnEquity();
        g.getBank().lendingGrowthLimit();
        g.getBank().lendsOnlyToKeepBorrowersGoing();
        g.getBank().lendingLimit();
        g.getBank().lendingStance();
        g.getBank().headroom();
        for (String key : Sectors.KEYS) {
            g.getBank().getStage(key);
            g.getBank().getSectorAllowance(key);
            g.getEconomyManager().getBusinessDebtManager().capitalRoom(key);
            g.getEconomyManager().getBusinessDebtManager().getPrincipalJudged(key);
            // a sector defaults a slice at a time (0.7.8): the Lending page,
            // the sector screen, the notice and the playtest read these
            g.getBank().getWrittenOff(key);
            g.getEconomyManager().getBusinessDebtManager().getDefaultRate(key);
            g.getEconomyManager().getBusinessDebtManager().getDefaultedThisMonth(key);
            g.getEconomyManager().getBusinessDebtManager().getDefaultShareThisMonth(key);
            g.getEconomyManager().getBusinessDebtManager().wasRestructuredThisMonth(key);
            g.getEconomyManager().getBusinessDebtManager().defaultsAreNews(key);
            g.getEconomyManager().getBusinessDebtManager().isInsolvent(key);
            // ...and round 2's: the price off the curve, the staging, the salvage
            g.getEconomyManager().getBusinessDebtManager().getRiskSpread(key);
            g.getEconomyManager().getBusinessDebtManager().getRecordSurcharge(key);
            g.getEconomyManager().getBusinessDebtManager().projectRate(key, 1_000);
            g.getEconomyManager().getBusinessDebtManager().leverageAfterProject(key, 1_000);
            g.getBank().getStageTwoShare(key);
            g.getSalvageThisMonth(key);
            // ...and the quarter's (0.7.8)
            g.getEconomyManager().getBusinessDebtManager().quarterPrincipal(key);
            g.getEconomyManager().getBusinessDebtManager().quarterAssets(key);
            g.getEconomyManager().getBusinessDebtManager().getQuarterLeverage(key);
            g.getEconomyManager().getBusinessDebtManager().getQuarterDefaultRate(key);
            g.getEconomyManager().getBusinessDebtManager().getStatementCount(key);
        }
        g.getEconomyManager().getBusinessDebtManager().getWrittenThisMonth();
        g.getSalvageThisMonth();
        // ...and the landlords' insured mortgages (0.7.11): the Bank tab's
        // mortgage block, its weight table and ladder, the landlords' screen,
        // the Government tab's two lines and the advisor's reasons
        BusinessDebtManager lender = g.getEconomyManager().getBusinessDebtManager();
        g.getBank().insuredMortgageRate(dial);
        g.getBank().capitalCharge(dial, Mortgage.MORTGAGE_TERM_MONTHS, Bank.RISK_INSURED_MORTGAGE);
        g.getBank().ladder(dial);
        g.getBank().weightTable();
        g.getBank().getMortgageBook();
        g.getBank().getInsuranceClaims();
        lender.getInsuredMortgageRate();
        lender.getMortgages();
        lender.getMortgagePrincipal();
        lender.getInsuredPrincipal();
        lender.getMortgagePayment();
        lender.getMortgageRate();
        lender.getMortgageRepaidThisMonth();
        lender.getMortgagesRenewingWithin(12);
        lender.allMortgagesInsured();
        lender.getPremiumsThisMonth();
        lender.getPremiumsTotal();
        lender.getInsuredWrittenOffThisMonth();
        lender.getInsuredWrittenOffTotal();
        lender.getMortgagesWrittenThisMonth();
        lender.getRenewedThisMonth();
        lender.getFallenDueThisMonth();
        for (String key : Sectors.KEYS) {
            lender.getMortgages(key);
            lender.getMortgageCount(key);
            lender.getMortgagePrincipal(key);
            lender.getInsuredPrincipal(key);
            lender.getUninsuredPrincipal(key);
            lender.getMortgagePayment(key);
            lender.getMortgageRate(key);
            lender.getNextRenewalMonth(key);
            lender.getMortgageRepaidThisMonth(key);
            lender.getInsuredWrittenOffThisMonth(key);
            lender.getInsuredWrittenOffTotal(key);
            lender.getPremiumsThisMonth(key);
        }
        for (Mortgage m : lender.getMortgages()) {
            m.getMonthlyPayment();
            m.getNextRenewalMonth();
            m.getPaidOffMonth();
            m.getMonthlyInterestExpense();
        }
        for (BuildingsTemplate t : g.getBuildingManager().getTemplatesBySector(re.key())) {
            e.housingCarry(t);
            e.housingBuildHurdle(t);
        }
        e.getNationalAccounts().getMortgagePremiums();
        e.getNationalAccounts().getMortgageClaims();
        e.getNationalAccounts().getTotalExpenses();
        g.getRefusedByLender();
        g.getHeldForDownPayment();
        g.getSalvageUsedThisMonth();
        g.getRefusedOnPrice();
        g.getSectors().construction().getSalvage();
        g.getSectors().construction().getSalvageCost();
        g.getEconomyManager().getBusinessDebtManager().getStatementsToSave();
        g.getBank().allowanceToSave();
        g.getBank().capitalRecordToSave();
        g.getBank().monthLinesToSave();
        g.getHouseholdBalance().lossAllowance();
        g.getHouseholdBalance().debtInTrouble();
        g.getHouseholds().getAccountFees();
        // the Bank tab's redesign (0.7.9): its status, its scorecard, the
        // ladder, the year of statements, the weight table, its funding and
        // branches, the equity's movement - every getter it reads
        Bank bk = g.getBank();
        bk.status();
        bk.targetReason();
        bk.ladder(dial).parts();
        bk.ladder(dial).saversOverPolicy();
        bk.ladder(dial).overPrime(g.getInterestRate());
        bk.getBooksWatched();
        for (Bank.Line line : Bank.Line.values()) {
            bk.thisMonth(line);
            bk.lastMonth(line);
            bk.overYear(line);
            bk.averageOverYear(line);
        }
        bk.knowsLastMonth();
        bk.monthsInYear();
        bk.revenue();
        bk.getRetained();
        bk.returnOnEquityOverYear();
        bk.provisionRateOverYear();
        bk.netInterestMarginOverYear();
        bk.costShareOverYear();
        bk.statementYearToSave();
        for (Bank.WeightRow row : bk.weightTable()) row.weighted();
        bk.getInterestFromBusinesses();
        bk.getInterestFromCity();
        bk.getInterestFromHouseholds();
        bk.getDiscountAccreted();
        bk.getTreasuryBuybackGain();
        bk.getAllowanceOpened();
        bk.getBailoutsLifetime();
        bk.isMonthKnown();
        bk.getHouseholdDeposits();
        bk.getSectorDeposits();
        bk.getDepositsPerBranch();
        bk.getPaidInPerBranch();
        bk.branchReach();
        bk.localDeposits();
        bk.localDepositsReached();
        bk.localDepositsBeyondReach();
        bk.fundingLimit();
        bk.capacityAnotherBranchWouldAdd();
        bk.overflowPastComfortable();
        bk.runningCostPerBranch();
        bk.keptPerBranch();
        bk.branchWouldPayForItself();
        bk.bookAnotherBranchWouldCarry();
        bk.wantsBranch();
        // ...and round 2 of 0.7.11's: the leverage ratio and the branch test
        // in reverse, as the Bank tab reads them.
        bk.exposure();
        bk.leverageRatio();
        bk.leverageTarget();
        bk.leverageTop();
        bk.minimumEquity();
        bk.leverageBinds();
        bk.bindingRatio();
        bk.bindingMinimum();
        bk.bindingTarget();
        bk.bindingTop();
        // ...and the Balance sheet page (0.7.13): every line this month and a
        // year ago, the businesses' by sector, and what they leave unexplained.
        for (Bank.Sheet line : Bank.Sheet.values()) {
            bk.sheet(line);
            bk.yearAgo(line);
        }
        for (int at = 0; at < Sectors.KEYS.length; at++) {
            bk.getLoansToSector(at); bk.getInterimToSector(at);
            bk.yearAgoLoansToSector(at); bk.yearAgoInterimToSector(at);
        }
        bk.knowsYearAgo(); bk.sheetResidual(); bk.yearAgoResidual(); bk.sheetYearToSave();
        bk.paidInCapital(); bk.retainedEarnings(); bk.equitySplitResidual(); bk.paidInThisMonth();
        bk.retainedThisMonth(); bk.knowsEquitySplit();
        bk.liabilitiesAndEquity(); bk.yearAgoLiabilitiesAndEquity();
        bk.getBusinessLoans(); bk.getInterimBook(); bk.getBailoutsLifetime(); bk.getResolutionLoss();
        // ...the treasury's rollover, as the borrow page reads it
        g.rolloverPlan().toRoll(); g.surplusOverLastYear(); g.getRolloverMode();
        g.getRollover().usedInYear(g.getMonth()); g.getRollover().ledgerToSave(); g.getRollover().recordToSave();
        // ...and the land office's funding page and its next-N control
        java.util.List<Integer> next = g.nextLandParcels(5);
        g.landShelf(); g.landPriceUsd(next); g.landPriceLocal(next); g.landCashGap(next);
        g.landVaultGapUsd(next); g.landNeedsFunding(next); g.canAffordLandParcels(next); g.landTopUpCovers(next);
        g.landTopUpLocal(next); g.getLandManager().getMarket().getMinSqFt();
        double gap = Math.max(1, g.landCashGap(next));
        g.quoteLongBondForCash(gap, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE);
        g.quoteTBill(gap, Game.BUILD_NOTE_MONTHS, Game.BUILD_NOTE_GRANULE);
        g.quoteForeignForCash("Term", gap, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE);
        g.quoteForeignForCash("Note", gap, Game.BUILD_NOTE_MONTHS, Game.BUILD_NOTE_GRANULE);
        g.quoteMediumBondForCash(gap, 5, Game.BUILD_BOND_GRANULE);
        bk.capitalPerDollar(Bank.RISK_INSURED_MORTGAGE);
        bk.branchesCoverTheirStaff();
        bk.closesBranch();
        bk.getUncoveredMonths();
        bk.status();
        g.getBranchesClosed();
        bk.equityMovement().residual();
        bk.solvencyToSave();
        g.canRecapitaliseBank();
        g.getHouseholdBalance().averageRate();
        for (int company = 0; company < Equity.COMPANIES.length; company++) g.getEquity().deskShare(company);
        g.getExchange().deskSoldToHouseholds();
        g.getExchange().deskSoldAbroad();
        g.getExchange().deskBoughtFromHouseholds();
        g.getExchange().deskBoughtFromAbroad();
        // ...and the desk held to the bank's capital (0.7.8, round 4): what it
        // has over its target, and what the capital rule turned away
        bk.spareCapital();
        bk.spareCapital(g.getExchange().markToMarket(g.getEquity()));
        bk.buybackRoom(g.getExchange().markToMarket(g.getEquity()));
        for (int company = 0; company < Equity.COMPANIES.length; company++) {
            bk.deskCanCarry(g.getExchange().markToMarket(g.getEquity()),
                    g.getExchange().fair(company), g.getExchange().mark(company));
            // ...and the book's own readers since 0.7.12 round 2: the price,
            // the best bid and ask, the depth, the desk's orders and the
            // month's flows the owners' pages read.
            Exchange x = g.getExchange();
            x.price(company); x.hasTraded(company); x.bestBid(company); x.bestAsk(company);
            x.bookOf(company).levels(OrderBook.Side.BUY); x.bookOf(company).levels(OrderBook.Side.SELL);
            x.deskResting(company, OrderBook.Side.BUY); x.deskResting(company, OrderBook.Side.SELL);
            x.deskCanBuy(g.getEquity(), company); x.getVolume(company); x.getBetweenHouseholds(company);
            x.getHouseholdsBoughtAbroad(company); x.getHouseholdsSoldAbroad(company); x.pricePerFoundingShare(company);
        }
        g.getExchange().getLastPostedSellValue();
        g.getExchange().getLifeBetweenHouseholdsTrades();
        // ...and 0.7.12's bond market: the Bonds page and one bond's book, the
        // sector screen's bank loans and bonds, the household panel, the Bank
        // tab's bonds, weight table, ladder rung and concentration, the Trade
        // tab's lines, the advisor's words and the desks' plans.
        BondMarket bm = g.getBondMarket();
        int month = g.getMonth();
        bm.totalFace(); bm.faceHeldByHouseholds(); bm.faceHeldByBank(); bm.faceHeldByCompanies(); bm.faceHeldByWorld();
        bm.averageCoupon(); bm.getLastIssuer(); bm.getLastIssueFace(); bm.getLastIssueCoupon();
        bm.getLastIssueLoanRate(); bm.getLastIssueMonth(); bm.getIssues(); bm.getIssuedFace(); bm.getIssuedCosts();
        bm.getCouponsToHouseholds(); bm.getCouponsToBank(); bm.getCouponsToCompanies(); bm.getCouponsAbroad();
        bm.getPrincipalToHouseholds(); bm.getPrincipalToBank(); bm.getPrincipalToCompanies(); bm.getPrincipalAbroad();
        bm.getLossHouseholds(); bm.getLossBank(); bm.getLossCompanies(); bm.getWorldWrittenOff();
        bm.getWorldPurchases(); bm.getWorldSales(); bm.getLastPostedSell(); bm.getLastFilled();
        bm.getLastSellsPosted(); bm.getLastSellsWaited(); bm.getLifeIssued(); bm.getLifeVolume();
        bm.crossover(.06, .05); bm.issueCost(1_000); bm.allIn(1_000, .05);
        bm.valueHeld(CorporateBond::households, month);
        for (CorporateBond x : bm.getBonds()) {
            OrderBook book = bm.bookOf(x);
            book.levels(OrderBook.Side.BUY); book.levels(OrderBook.Side.SELL);
            book.bestBid(); book.bestAsk(); book.lastPrice(); book.lastTradeMonth();
            bm.lastPrice(x); bm.lastYield(x, month); bm.modelPrice(x, month); bm.modelYield(x, month);
            bm.defaultRate(x.issuer(), 0, 0); bm.expectedLoss(x.issuer());
            bm.bond(x.id());
        }
        bm.bookOf(new CorporateBond());
        for (String key : Sectors.KEYS) {
            bm.principal(key); bm.monthlyCoupon(key); bm.averageCoupon(key); bm.faceHeldBy(key); bm.bankCost(key);
            bm.bankYield(key, CorporateBond.TERM_MONTHS, 0, 0, 0); bm.quoteCoupon(key, 1_000, 0);
            bm.getIssued(key); bm.getProceeds(key); bm.getRepaid(key); bm.getBoughtNet(key); bm.getCouponsTo(key);
            bm.getBonds(key);
            lender.getLoanPrincipal(key); lender.getBondPrincipal(key); lender.getLoanShare(key);
            lender.getLoanInterest(key); lender.getMonthlyInterest(key); lender.getConcentrationCharge(key);
            lender.getBondWrittenOffTotal(key); lender.getBondWrittenOffThisMonth(key);
            lender.getLoansDefaultedThisMonth(key); lender.getBondsDefaultedThisMonth(key);
            lender.bondCeilingRoom(key, BusinessDebtManager.MAX_LOAN_TO_ASSETS);
            lender.projectLoanRoom(key, 1_000); lender.projectBondRoom(key, 1_000);
            Game.financingWords(lender.getShortfallPlan(key));
            bm.plan(key, 1_000, lender.projectRate(key, 1_000), lender.projectLoanRoom(key, 1_000),
                    lender.projectBondRoom(key, 1_000), 1_000, month);
            bk.concentrationPerDollar(key); bk.capitalPerDollar(Bank.RISK_BUSINESS, key);
            bk.concentrationCharge(dial, Bank.PRIME_TERM_MONTHS, key); bk.getExposure(key);
        }
        bk.getBondBook(); bk.getBondFace(); bk.getBondWeighted(); bk.getInterestFromBonds(); bk.getBondGains();
        bk.getUnderwritingFees(); bk.getConcentrationHerfindahl(); bk.getConcentrationAddOn();
        bk.getConcentrationWeighted(); bk.getConcentrationExposure();
        HouseholdBalance cells = g.getHouseholdBalance();
        cells.totalBonds(); cells.marketValueOfBonds(); cells.totalBondsSold(); cells.totalBondIncome();
        cells.getBondRatio(); cells.bondSpare(); cells.getBondsTakenAway();
        for (int r = 0; r < g.getHouseholds().getRowCount(); r++) cells.getBonds(r);
        for (Household cell : cells.cells()) { cell.bonds(); cell.bondIncome(); cell.bondsSold(); }
        HistorySave record = g.getHistorySave();
        record.monthsUnder("bankCapitalRatio", "bankCapitalTarget");
        record.monthsUnder("bankCapitalRatio", Bank.CAPITAL_RATIO);
        record.monthsUnder("bankProfit", 0.0);
        record.monthsRecorded("bankWriteOffs");
        record.total("bankWriteOffs");
        record.worstYear("bankProvisions");
        // the trade page's forces on the rate, and what the currency did to
        // the debt (2026-09-21): the page previews the push rather than asking
        // the month's own effectivePressure(), which records - ForeignCheck
        // section 13 causes the case where the two differ
        g.getForeignAccounts().previewPressure();
        g.getForeignAccounts().previewRawPressure();
        g.getForeignAccounts().previewAbsorption();
        g.getForeignAccounts().getLastPressure();
        g.getForeignAccounts().getLastAbsorption();
        g.getForeignAccounts().getLastRevaluation();
        // the curve and who holds the paper (0.7.1): the borrow page's five
        // rows, the rate page's premium and compression, the book page's
        // holders, the money page's holdings and the households' paper
        DebtManager debt = g.getDebtManager();
        for (int years : LongTermBond.MATURITIES) {
            debt.curveRate(years * 12);
            debt.compression(years * 12);
            g.quoteLongBond(1_000, years, 100);
        }
        // ...and the build screen's bond, sized to the cash it brings (0.7.10)
        g.quoteLongBondForCash(1_000, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE);
        debt.curveRate(6);
        debt.bookValues();
        debt.householdPrincipal();
        debt.centralBankPrincipal();
        debt.bankPrincipal();
        debt.bankBook();
        debt.householdBookRatio();
        debt.householdBookYield();
        debt.bankUnearnedDiscount();
        debt.termPrincipal();
        debt.centralBankShareOfTerm();
        debt.getTotalMarketValue();
        for (Debt paper : debt.getDebt()) debt.marketValue(paper);
        g.getHouseholdBalance().totalPaper();
        g.getHouseholdBalance().marketValueOfPaper();
        g.getHouseholdBalance().getPaperRatio();
        g.getCentralBank().getPaperHeld();
        g.getCentralBank().stepFor(debt.termPrincipal());
        g.getBuybackUnsettled();
        // how the city was founded (0.7.10): the founders' note, the window's
        // title and every screen that writes the city's money read these, and
        // the founding screen asks what each preset would buy - of the live
        // city's catalogue, which it must not touch
        g.getFounding();
        g.getCityName();
        g.getFoundingCash();
        g.getFoundingReserveUsd();
        Currency money = g.getCurrency();
        money.describe();
        money.rateUnit();
        money.qualified("1");
        for (Founding.Preset p : Founding.Preset.values()) {
            if (p != Founding.Preset.CUSTOM) g.whatItBuys(p.cash(), p.reserveUsd());
        }
        for (double m : WorldEconomy.FOUNDING_CHOICES) WorldEconomy.settledLevelAt(m);
        g.getDenomination().name(money);
        g.getDenomination().describeUnit(money);

        /*
         * calculateSalesTax() used to be read here. It is settleSalesTax() now -
         * a monthly step that strikes the VAT and assigns it, exactly like
         * chargePropertyTax() - so it has no business in a sweep whose entire
         * premise is that nothing in it changes anything. It is called once a
         * month from the month loop, and getTaxIncome() above reads the result,
         * which is the read path this sweep is actually for.
         */

        // each sector's tax line, read on its own the way the panels do
        for (Sector sec : g.getSectors().all()) {
            sec.getProfitTax();
            sec.getNetIncome();
            sec.statement();
            sec.getBalanceSheet();
            sec.getInventoryValue();
            sec.getPayroll();
            sec.getOperatingRate();
            for (Good good : Good.values()) {
                sec.getStock(good);
                sec.getPantry(good);
                sec.getCapacity(good);
                sec.getPlannedOutput(good);
                sec.getCostPerUnit(good);
                sec.getMarginalCostPerUnit(good);
                sec.output(good);
                sec.input(good);
            }
            // the operations page, which is what the sector screen draws
            sec.operations(g);
            e.getAssessedValue(sec);
            e.getMaintenanceCharge(sec.key());
            g.isAutoSubsidised(sec);
            g.getSubsidyPaid(sec);
        }
        re.getRentIncome();
        re.rentBreakEven();
        re.blendedRentTarget();
        re.housingPressure();

        // the statements themselves, as text
        g.getSectorBooks();

        // the aggregates behind the info panels
        c.getLastMonthSales();
        c.getStoreInventory();
        c.getStoreSellPrice();
        c.getFoodPrice();
        c.getSupplyRatio();
        ih.statement();
        ih.getNetIncome();
        hh.getNetIncome();
        mh.getNetIncome();
        mh.getPotentialOutput();
        g.getSectors().totalCash();

        // services, roads and construction
        s.getEnergyRatio();
        s.getWaterRatio();
        s.getRoadRatio();
        s.getServiceNetIncome();
        g.getSectors().construction().getAverageFill();
        g.getConstructionOutput();
        g.quoteBuild(template(g, "House"), 1);
        // ...and what the build screen's funding page is sized to (0.7.10),
        // for an order the city can pay for and one it cannot
        g.buildFundingGap(template(g, "House"), 1);
        g.buildFundingGap(template(g, "Water Treatment Plant"), 1_000);

        // land, ore and every market
        g.getLandManager().getAvailableSqFt();
        g.getLandManager().getPricePerSqFt();
        g.getLandListing();
        for (GoodsMarket m : g.getMarkets().all()) {
            m.getLocalPrice();
            m.getDemand();
            m.getSupply();
            m.getPriceIndex();
            m.isShortage();
            m.floor();
            m.ceiling();
        }
    }

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        Path root = Files.createTempDirectory("readpath");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));

        /* ============ a city with money moving in every sector ============ */
        out.println("--- a city worth reading ---");

        Game g = new Game(files);
        System.setOut(quiet);
        try {
            g.run();

            BuildingManager b = g.getBuildingManager();
            LandManager land = g.getLandManager();

            // Instant builds and land by fiat, for the same reason MiningCheck
            // does it: this is about read paths, not about the build queue.
            land.setOwnedSqFt(14_000_000);
            land.restoreIron(2, 20_000_000);

            b.addStack(template(g, "House"), 500, true);
            b.addStack(template(g, "Convenience Store"), 8, true);
            b.addStack(template(g, "Small Grocery Store"), 2, true);
            b.addStack(template(g, "Construction Depot"), 4, true);
            b.addStack(template(g, "Bakery"), 1, true);
            b.addStack(template(g, "Coal Power Plant"), 1, true);
            b.addStack(template(g, "Water Treatment Plant"), 1, true);
            b.addStack(template(g, "Paved Road"), 3, true);
            b.addStack(template(g, "Steel Foundry"), 1, true);
            b.addStack(template(g, "Iron Mine"), 1, true);

            // ...and a landlord's insured mortgage on the books (0.7.11), by the
            // fixture's hand - a claim, no money moved - so the reads below take
            // in every figure the mortgage block and the landlords' screen show,
            // on a mortgage two years into its term.
            BusinessDebtManager lender = g.getEconomyManager().getBusinessDebtManager();
            lender.setInsuredMortgageRate(g.getBank().insuredMortgageRate(g.getDebtManager().getPolicyRate()));
            lender.issueMortgage(Sectors.REAL_ESTATE, 50_000, g.getMonth());

            g.simulateMonths(24);
            // ...and a bond on the books (0.7.12): the builders five million
            // short, set between months, borrow it in the next - a bond where
            // the book takes one, so the reads below take in every figure the
            // bond pages, the Bank tab and the Trade tab show.
            g.getEconomyManager().setSectorCash(Sectors.CONSTRUCTION, -5_000);
            g.simulateMonths(2);
        } finally {
            System.setOut(out);
        }
        assertTrue("fixture: the businesses owe bonds, and orders rest on their books",
                !g.getBondMarket().getBonds().isEmpty() && restingOrders(g.getBondMarket()) > 0);

        out.printf("   month %d, %d people, $%,.0fk cash, %d stacks%n",
                g.getMonth(), g.getPopulationManager().getPopulation(),
                g.getCash(), g.getBuildingManager().getStackCount());

        EconomyManager econ = g.getEconomyManager();
        assertTrue("every sector is actually trading",
                g.getSectors().retail().statement().revenue > 0
                        && g.getSectors().industry().statement().revenue > 0
                        && g.getSectors().heavyIndustry().getNetIncome() != 0
                        && g.getSectors().mining().getNetIncome() != 0);

        /* ============ the FIRST read, which is the hard one ============ */
        out.println("\n--- the first read after the month ends ---");

        /*
         * Taken before anything has been read, and that ordering is the whole
         * point of this section.
         *
         * The fifty-pass sweep below cannot catch a read path that mutates
         * IDEMPOTENTLY - one that writes the same wrong value every time. Its
         * own first snapshot has already triggered the write, so pass fifty
         * looks exactly like pass one and nothing appears to move. Item 7 was
         * precisely that shape: getStoreIncome() assigned the same uncapped
         * demand figure on every call.
         *
         * So the figures a read must never touch are captured here, straight
         * out of the month, with the city not yet asked a single question.
         */
        Map<String, Double> untouched = new LinkedHashMap<>();
        ham.citybuildersim.sectors.Retail ch = g.getSectors().retail();
        untouched.put("retail.productsSold", (double) ch.getProductsSold());
        untouched.put("retail.inventory", (double) ch.getStoreInventory());
        untouched.put("retail.reportSold", (double) ch.getLastMonthSales());
        untouched.put("retail.grossRevenue", ch.statement().revenue);
        // What the next month's hunger reads, and saved since 0.7.12 round 2
        // (SaveFileCheck section 14): a read must not move it either.
        untouched.put("retail.householdShare", ch.getHouseholdShare());
        untouched.put("retail.pending", ch.pending().revenue());
        untouched.put("retail.cash", ch.getCash());
        untouched.put("industry.inventory", g.getSectors().industry().getStock(Good.BREAD));
        untouched.put("industry.cash", g.getSectors().industry().getCash());
        untouched.put("cash", g.getCash());

        System.setOut(quiet);
        readEverything(g);
        System.setOut(out);

        int firstReadMoved = 0;
        for (Map.Entry<String, Double> entry : untouched.entrySet()) {
            double now = switch (entry.getKey()) {
                case "retail.productsSold" -> ch.getProductsSold();
                case "retail.inventory"    -> ch.getStoreInventory();
                case "retail.reportSold"   -> ch.getLastMonthSales();
                case "retail.grossRevenue" -> ch.statement().revenue;
                case "retail.householdShare" -> ch.getHouseholdShare();
                case "retail.pending"      -> ch.pending().revenue();
                case "retail.cash"         -> ch.getCash();
                case "industry.inventory"  -> g.getSectors().industry().getStock(Good.BREAD);
                case "industry.cash"       -> g.getSectors().industry().getCash();
                default                    -> g.getCash();
            };
            if (Math.abs(now - entry.getValue()) > 1e-9) {
                firstReadMoved++;
                out.printf("   MOVED  %-28s %,.4f -> %,.4f%n",
                        entry.getKey(), entry.getValue(), now);
            }
        }
        assertTrue("one pass over the screens moved nothing", firstReadMoved == 0);

        /*
         * And the invariant underneath all of it.
         *
         * Snapshot comparison has a blind spot even here: if a read path
         * mutated a field DURING the month as well, the value captured "before
         * any read" is already the corrupted one, and the read reproduces it
         * faithfully. Nothing appears to move because the damage was done
         * earlier by the same broken call.
         *
         * So state the property directly instead of inferring it from movement.
         * The units sold and the units in the month's ledger are written by
         * one Trade in Retail.sellOwnPriced(); if they ever disagree,
         * something else has written to one of them, and the shops are billing
         * for a different quantity than they are shipping.
         */
        assertTrue("the live sale figure IS the one in the ledger",
                Math.abs(ch.getProductsSold()
                        - ch.pending().unitsSold.getOrDefault(Good.GROCERIES, 0.0)) < 1e-9);

        /* ================ read it, and read it again ================ */
        out.println("\n--- fifty passes over every screen in the game ---");

        System.setOut(quiet);
        Map<String, Double> before = NewGameCheck.snapshot(g);
        bankPrint(g, before);
        for (int i = 0; i < 50; i++) {
            readEverything(g);
        }
        Map<String, Double> after = NewGameCheck.snapshot(g);
        bankPrint(g, after);
        System.setOut(out);

        out.printf("%d fields fingerprinted%n", before.size());

        Map<String, String> moved = new LinkedHashMap<>();
        for (String key : before.keySet()) {
            double a = before.get(key);
            double z = after.get(key);
            if (Math.abs(a - z) > 1e-9) {
                moved.put(key, String.format("%,.4f -> %,.4f", a, z));
            }
        }

        for (Map.Entry<String, String> entry : moved.entrySet()) {
            out.printf("   MOVED  %-34s %s%n", entry.getKey(), entry.getValue());
        }
        assertTrue("reading the city fifty times changed nothing", moved.isEmpty());

        /* ============ and the specific one item 7 was about ============ */
        out.println("\n--- the shops sell what the statement says they sold ---");

        ham.citybuildersim.sectors.Retail c = g.getSectors().retail();

        /*
         * The shelf, across one real month.
         *
         * The old version drove the shops' own sale-and-restock call twenty
         * times over between tax reads, because the bug it caught lived in a
         * report field the tax path was assigning. There is no such field
         * now: the sale is a Trade, the restock is the market's fill, and
         * both land on the same shelf in Markets.clearMonth(). So the law is
         * asserted across a month: what was on the shelf, less what the
         * ledger says was sold, plus what the market delivered, is what is
         * on the shelf now.
         */
        int shelfBefore = c.getStoreInventory();
        /*
         * THIRTEEN LAWS WHERE THERE WAS ONE.
         *
         * getStoreInventory() used to BE the shelf - units of FOOD, one good,
         * one number, and the law could be asserted straight on it. It is now
         * a SUMMARY: the person-months the scarcest of thirteen goods allows,
         * which is a min and not a stock, so nothing conserves about it.
         *
         * The law itself did not weaken - it multiplied. Each good's pantry
         * still falls by exactly what the month sold of it and rises by
         * exactly what the market delivered, so that is asserted thirteen
         * times, on the thirteen quantities that are actually stocks. A
         * harness that checked one number now checks thirteen.
         */
        java.util.Map<Good, Double> pantryBefore = new java.util.EnumMap<>(Good.class);
        for (Good sg : ham.citybuildersim.sectors.Retail.SHELF) pantryBefore.put(sg, c.getPantry(sg));
        System.setOut(quiet);
        for (int i = 0; i < 20; i++) {
            econ.getTaxIncome();                 // the path that used to assign
        }
        g.simulateMonths(1);
        System.setOut(out);

        double sold = c.pending().unitsSold.getOrDefault(Good.GROCERIES, 0.0);

        boolean everyGoodConserves = true;
        double restocked = 0;
        Good worst = null;
        double worstGap = 0;
        for (Good sg : ham.citybuildersim.sectors.Retail.SHELF) {
            Sector.Input in = c.input(sg);
            double delivered = in.boughtLocal + in.imported;
            restocked += delivered;
            double expected = pantryBefore.get(sg) - sold * c.kgPerHead(sg) + delivered;
            double gap = Math.abs(c.getPantry(sg) - expected);
            if (gap > 1e-6) everyGoodConserves = false;
            if (gap > worstGap) { worstGap = gap; worst = sg; }
        }
        assertTrue("every one of the thirteen pantries fell by what sold and rose by what arrived",
                everyGoodConserves);
        out.printf("   %,d person-months on the shelf, %,.0f sold, %,.0fkg restocked, %,d left"
                + " (worst gap %.2eg on %s)%n",
                shelfBefore, sold, restocked, c.getStoreInventory(),
                worstGap, worst == null ? "nothing" : worst.name());

        assertTrue("...and the statement never sold more than was in stock",
                sold <= shelfBefore + 1e-9);
        assertTrue("...and the shelf never goes negative",
                c.getStoreInventory() >= 0);

        /* ============ the tax the city takes is the tax it shows ============ */
        out.println("\n--- and the treasury agrees with the screen ---");

        // Every sector's own deducted figure, plus the bank's, against the
        // two lines the treasury screen prints them on.
        double collected = econ.getBankTax();
        for (Sector sec : g.getSectors().all()) collected += sec.getProfitTax();
        double shown = econ.getBusinessTax() + econ.getIndustrialTax();
        assertTrue("business tax collected == business tax printed",
                Math.abs(collected - shown) < 1e-9);
        out.printf("   collected $%,.2fk, printed $%,.2fk%n", collected, shown);

        assertTrue("...and it is the companies taxed separately, not netted",
                Math.abs(econ.getHeavyIndustryTax()
                        - (g.getSectors().heavyIndustry().getProfitTax()
                        + g.getSectors().mining().getProfitTax())) < 1e-9);

        /* ============ a rate change reaches the treasury at once ============ */
        out.println("\n--- changing the rate is not a month late ---");

        // The two commercial companies together, as the old commercial
        // statement printed them. Retail alone would not do: the income rate
        // sets the VAT rate too (all three bases, since 0.7.4 split them),
        // so doubling it halves the shops' profit
        // before the profit tax is struck, and the shops' profit tax alone
        // barely moves. The landlords' rent is VAT-exempt, so theirs doubles.
        Sector landlords = g.getSectors().realEstate();
        double lowRate = econ.getTaxRate();
        double lowTake = c.getProfitTax() + landlords.getProfitTax();

        System.setOut(quiet);
        econ.getTaxPolicy().setIncomeTaxRate(lowRate * 2);
        g.simulateMonths(1);
        System.setOut(out);

        double highTake = c.getProfitTax() + landlords.getProfitTax();
        out.printf("   at %.0f%%: $%,.2fk    at %.0f%%: $%,.2fk%n",
                lowRate * 100, lowTake, lowRate * 200, highTake);
        assertTrue("doubling the rate moves the very next month's commercial tax",
                highTake > lowTake * 1.5);

        cleanUp(root);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    static void cleanUp(Path root) {
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }
    }
}

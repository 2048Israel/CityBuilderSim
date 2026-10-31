package ham.citybuildersim;

import ham.citybuildersim.sectors.HeavyIndustry;
import ham.citybuildersim.sectors.Mining;

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
        // ledger and its record, which the Finances tab reads (its hub and Debt
        // service since 0.7.32, its borrow pages until then).
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
        // ...and 0.7.14's: the city's fund - its cash, its settings, its orders waiting, its holdings on
        // the register and on the bonds - the bank's preferred, and the budget's line for the transfer.
        TreasuryFund fund = g.getFund();
        into.put("fund.cash", fund.getCash());
        into.put("fund.dial", fund.getDial());
        into.put("fund.rescue", (double) fund.getRescueMode().ordinal());
        into.put("fund.offerPending", fund.isOfferPending() ? 1.0 : 0.0);
        into.put("fund.handOrders", (double) fund.getHandOrders().size());
        into.put("fund.transferDue", fund.getTransferDue());
        // ...and 0.7.48's withdrawal dial, and what it owes over the default.
        into.put("fund.withdrawal", fund.getWithdrawal());
        into.put("fund.toRaise", fund.getToRaise());
        into.put("fund.transferPaidLate", fund.getTransferPaidLate());
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            into.put("fund.shares" + c, g.getEquity().getCityShares(c));
            into.put("fund.rescueShares" + c, g.getEquity().getCityRescueShares(c));
        }
        into.put("fund.bondsFace", g.getBondMarket().faceHeldByCity());
        // ...and 0.7.39's: what each holding cost, its record, the hand's orders on the books and the cash they hold.
        FundLedger fundLedger = fund.getLedger();
        into.put("fund.ledgerLots", (double) fundLedger.getLots().size());
        into.put("fund.ledgerRows", (double) fundLedger.getActivity().size());
        into.put("fund.ledgerSince", (double) fundLedger.getTrackingSince());
        into.put("fund.ledgerCost", fundLedger.acbHeld());
        into.put("fund.ledgerRealized", fundLedger.realized());
        into.put("fund.ledgerBought", fundLedger.purchases());
        into.put("fund.ledgerProceeds", fundLedger.proceeds());
        double boughtValue = 0;
        for (FundLedger.Lot l : fundLedger.getLots()) boughtValue += l.boughtValue();
        into.put("fund.ledgerBoughtValue", boughtValue);
        into.put("fund.posted", (double) fund.getPosted().size());
        into.put("fund.handReserve", fund.handReserve());
        into.put("bank.preferred", b.preferredOutstanding());
        into.put("bank.preferredArrears", b.getPreferredArrears());
        into.put("bank.repaymentRaised", b.getRepaymentRaisedLifetime());
        double[] market = g.getDebtManager().marketToSave();
        for (int i = 0; i < market.length; i++) into.put("debtMarket" + i, market[i]);
        // ...and the auctions counted (CD2, D33): only a booking counts, so a quote read a dozen times adds nothing.
        double[] tally = g.getDebtManager().getAuctionTally().toSave();
        for (int i = 0; i < tally.length; i++) into.put("auctionTally" + i, tally[i]);
        into.put("budget.fundTransfer", g.getEconomyManager().getNationalAccounts().getFundTransfer());
        // ...and CD7's: the month's exchange gain on the foreign debt, a flow the list of paper cannot give back
        into.put("budget.debtExchangeGain", g.getEconomyManager().getNationalAccounts().getDebtExchangeGain());
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
        // ...and the city's old steel (0.7.123, batch I4): what stands to be bought, and the yards' last month.
        OldMetal old = g.getOldMetal();
        into.put("oldMetal.available", old.available());
        into.put("oldMetal.lastArisen", old.lastArisen());
        into.put("oldMetal.lastBought", old.lastBought());
        into.put("oldMetal.lastLandfill", old.lastLandfill());
        into.put("oldMetal.lastPrice", old.lastPrice());
        into.put("oldMetal.lastPaid", old.lastPaidTotal());
        // ...and its old copper (0.7.133, batch I8): the same, the scrap refineries' month.
        OldMetal.Pool cu = old.copper();
        into.put("oldCopper.available", cu.available());
        into.put("oldCopper.lastArisen", cu.lastArisen());
        into.put("oldCopper.lastBought", cu.lastBought());
        into.put("oldCopper.lastLandfill", cu.lastLandfill());
        into.put("oldCopper.lastPrice", cu.lastPrice());
        into.put("oldCopper.lastPaid", cu.lastPaidTotal());
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
        // ...and the city's old steel and what Iron & Steel's yards would make of it (0.7.123, batch I4; OldMetal), as a
        // sector page and the build card read them: the pool, the yards' feed and price, and every works weighed.
        OldMetal old = g.getOldMetal();
        old.available();
        old.lastArisen();
        old.lastPaid();
        old.lastPaidTotal();
        for (OldMetal.Source src : OldMetal.Source.values()) { old.available(src); old.lastArisen(src); }
        ham.citybuildersim.sectors.HeavyIndustry heavy = g.getSectors().heavyIndustry();
        heavy.oldSteelIntake();
        heavy.oldSteelPerTonneOfScrap();
        heavy.oldSteelPrice();
        heavy.tonnesMoved();
        heavy.appraise(g.getBusinessInvestment(), g);
        // ...and the old copper and what Copper Refining's scrap refineries would make of it (0.7.133, batch I8), as its page and the
        // build card read them: the pool, the refineries' intake, price and take, their bid for scrap, and every works weighed.
        OldMetal.Pool cu = old.copper();
        cu.available();
        cu.lastArisen();
        cu.lastPaid();
        for (OldMetal.Source src : OldMetal.Source.values()) { cu.available(src); cu.lastArisen(src); }
        ham.citybuildersim.sectors.CopperRefining copper = g.getSectors().copperRefining();
        copper.oldCopperIntake();
        copper.oldCopperPrice();
        copper.oldCopperTaken();
        copper.spareOldCopper();
        copper.leftForTheLast();
        copper.bid(Good.COPPER_SCRAP);
        copper.ownLines(g);
        copper.appraise(g.getBusinessInvestment(), g);
        g.getSectors().copperMining().lift();
        g.getSectors().copperMining().ownLines(g);
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
        // ...and what the Services screen added (0.7.28): the school leavers'
        // diplomas, the homes' draws, the long-sick ring in people
        g.getEducation().getNewDiplomas();
        s.getUtilitiesHandler().getHomesElectricityDraw();
        s.getUtilitiesHandler().getHomesWaterDraw();
        for (int k = 0; k < Sickness.RING; k++) g.getSickness().peopleInSlot(g.getCohorts(), k);
        // ...and what the Infrastructure screen added (0.7.29): the curve at
        // the road's own use, the walk from trips to the road, the freight
        // bill three ways and each good's wedge taken apart
        InfrastructureManager.throughputAt(g.getInfrastructureManager().getUtilisation());
        g.getInfrastructureManager().roadBreakdown().withoutCars();
        g.getInfrastructureManager().getHeadroom();
        g.getInfrastructureManager().getSpareCapacity();
        g.getSectors().freight().getPaidAbroad();
        g.getSectors().freight().getKept();
        g.getSectors().freight().getAllowedRevenue();
        for (GoodsMarket m : g.getMarkets().all()) {
            m.freightInBand();
            m.bandByLorry();
            m.worldMargin();
            m.railInWedge();
        }

        // the bridge on the Government tab, and the journal it opens into
        // (2026-09-18) - the row is read every time the panel is rebuilt
        g.getTreasuryChange();
        g.getTreasuryUnexplained();
        g.getTreasuryJournal();
        g.getTreasuryResidual();
        // ...and the walk from EARNED to the budget, which the Government
        // tab draws in front of it, and EARNED read without striking (0.7.31)
        g.getIncome();
        e.getTaxIncomeNow();
        g.getEarnedToBudget();
        g.getEarnedResidual();
        // ...and CD7's exchange gain on the foreign debt, a line the budget's result carries and the bridge takes out as not cash
        e.getNationalAccounts().getDebtExchangeGain();
        g.getTreasuryNotCash();
        e.getNationalAccounts().getMonthsRecorded();
        e.getNationalAccounts().getAnnualGdp();
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
            // ...and the quote in its parts, which 0.7.33's ladder draws
            g.getEconomyManager().getBusinessDebtManager().quoteParts(key);
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
        bk.ladder(dial).mortgageRunning();
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
        bk.earningAssets();
        bk.averageEarningAssetsOverYear();
        bk.costShareOverYear();
        bk.statementYearToSave();
        for (Bank.WeightRow row : bk.weightTable()) row.weighted();
        bk.getInterestFromBusinesses();
        bk.getInterestFromCity();
        bk.getInterestFromHouseholds();
        bk.getDiscountAccreted();
        bk.getTreasuryBuybackGain();
        bk.getDiscountRestruck();
        bk.getAllowanceOpened();
        bk.getBailoutsLifetime();
        bk.isMonthKnown();
        bk.getHouseholdDeposits();
        bk.getSectorDeposits();
        bk.getPaidInPerBranch();
        bk.getPaidInPerBranch();
        bk.getCustomers();
        bk.localDeposits();
        bk.feesPerBranch();
        bk.feesPerBranchWithAnother();
        bk.fundingLimit();
        bk.capacityAnotherBranchWouldAdd();
        bk.customersPerBranch();
        bk.runningCostPerBranch();
        bk.branchesTheFeesCover();
        bk.feesWouldCoverAnother();
        bk.customersForAnother();
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
        // ...the treasury's rollover, as the Finances hub reads it (the borrow page until 0.7.32)
        g.rolloverPlan().toRoll(); g.surplusOverLastYear(); g.getRolloverMode();
        g.getRollover().usedInYear(g.getMonth()); g.getRollover().ledgerToSave(); g.getRollover().recordToSave();
        // ...and the land office's funding page and its next-N control
        java.util.List<Integer> next = g.nextLandParcels(5);
        g.landShelf(); g.landPriceUsd(next); g.landPriceLocal(next); g.landCashGap(next);
        g.landVaultGapUsd(next); g.landNeedsFunding(next); g.canAffordLandParcels(next); g.landTopUpCovers(next);
        g.landTopUpLocal(next); g.getLandManager().getMarket().getBlockPlots();
        double gap = Math.max(1, g.landCashGap(next));
        g.quoteLongBondForCash(gap, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE);
        g.quoteTBill(gap, Game.BUILD_NOTE_MONTHS, Game.BUILD_NOTE_GRANULE);
        g.quoteForeignForCash("Term", gap, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE);
        g.quoteForeignForCash("Note", gap, Game.BUILD_NOTE_MONTHS, Game.BUILD_NOTE_GRANULE);
        g.quoteMediumBondForCash(gap, 5, Game.BUILD_BOND_GRANULE);
        bk.capitalPerDollar(Bank.RISK_INSURED_MORTGAGE);
        bk.feeCover();
        bk.closesBranch();
        bk.getUncoveredMonths();
        bk.status();
        g.getBranchesClosed();
        bk.equityMovement().residual();
        bk.solvencyToSave();
        g.canResolveBank();
        // ...and 0.7.14's fund, its rescue and the bank's preferred: the Finances tab's fund
        // pages and the hub's rescue card, the Bank tab's rescue, offer, sheet line and capital page,
        // and the founding screen's Insane lines (the clock's refusal went in 0.7.15).
        g.bankRecapitalisationNeeded(); g.bankResolutionAdvance(); g.getLastResolution(); g.cityStakeInBank();
        g.getOwnersWipedAbroadThisMonth();
        g.isPreferredOfferPending(); g.preferredOfferSize(); g.preferredOfferWarrantValue(); g.preferredOfferStrike();
        g.preferredOfferShortBy(); g.bankVolatility();
        g.fundSharesValue(); g.fundMarketSharesValue(); g.fundRescueSharesValue(); g.fundBondsValue();
        g.fundPreferredValue(); g.fundWarrantsValue(); g.fundRescueValue(); g.fundValue(); g.fundEquityShare();
        g.fundTransferDue(); g.getFundDial(); g.getRescueMode(); g.monthOfSpending(); g.surplusThisYearSoFar();
        // ...and CD7's: the exchange gain on the foreign debt that surplus leaves out, the line the fund page writes under it
        g.debtExchangeThisYearSoFar();
        g.getFundWithdrawal();
        g.fundReservation();
        // ...and 0.7.15's skip report on the central bank's advances, as the skip's screen reads it.
        g.getSkipReport().getAdvancedDuringSkip(); g.getSkipReport().getAdvancesOwedAtEnd();
        for (int company = 0; company < Equity.COMPANIES.length; company++) {
            g.fundCompanyValue(company); g.fundCompanyRescueValue(company); g.fundCompanyShare(company);
            g.fundCompanyMarketShare(company);
            g.getEquity().getCityShares(company); g.getEquity().getCityRescueShares(company);
            g.getEquity().getCityMarketShares(company); g.getEquity().cityShare(company);
            g.getEquity().getDividendCityThisMonth(company); g.getEquity().getLifetimeDividendsCity(company);
        }
        for (String key : Sectors.KEYS) g.fundBondsValueOf(key);
        for (CorporateBond x : g.getBondMarket().getBonds()) { g.fundBondValue(x); x.city(); }
        g.getBondMarket().faceHeldByCity();
        g.getExchange().cityValue(g.getEquity()); g.getExchange().cityMarketValue(g.getEquity());
        g.getExchange().getFundBoughtFromHouseholds(); g.getExchange().getFundBoughtAbroad();
        g.getExchange().getFundSoldToHouseholds(); g.getExchange().getFundSoldAbroad();
        TreasuryFund fund = g.getFund();
        fund.getCash(); fund.getDial(); fund.getRescueMode(); fund.getTransferDue(); fund.getTransferPaid();
        fund.getWithdrawal(); fund.getWithdrawalSteps(); fund.withdrawalOn(g.fundValue()); fund.sellsToPay();
        fund.getToRaise(); fund.getTransferPaidLate();
        fund.getTransferShort(); fund.getTransfersThisYear(); fund.getTransferShortThisYear(); fund.getRescueCost();
        fund.isOfferPending(); fund.getOfferMonth(); fund.getDeclinedMonth(); fund.getAcceptedMonth();
        fund.mayOffer(g.getMonth()); fund.getOffersMade(); fund.getOffersAccepted(); fund.getOffersDeclined();
        fund.getHandOrders(); fund.getLastPayInMonth(); fund.getLastPayInYearSurplus(); fund.getLastPayInFromSurplus();
        fund.getLastPayInFromCash(); fund.getPaidInFromSurplus(); fund.getPaidInFromCash(); fund.getHandPaidIn();
        fund.getHandDrawnOut(); fund.getTransfersPaid(); fund.getTransfersShort(); fund.getDividendsMarket();
        fund.getDividendsRescue(); fund.getCoupons(); fund.getPrincipal(); fund.getBondFaceLost();
        fund.getPreferredBought(); fund.getPreferredDividends(); fund.getPreferredRedeemed();
        fund.getWarrantsBoughtBack(); fund.getWarrantSharesTaken(); fund.getSharesBought(); fund.getSharesSold();
        fund.getBondsBought(); fund.getBondsSold(); fund.getRescueSold(); fund.getMonthDividends();
        fund.getMonthCoupons(); fund.getMonthPrincipal(); fund.getMonthBought(); fund.getMonthSold(); fund.isEmpty();
        // ...and 0.7.39's: the fund as a brokerage - every read its pages make, through FundView.
        fund.getRescuesPaid(); fund.getPutIn(); fund.getTakenOut(); fund.handReserve(); fund.getPosted();
        fund.needsLedgerSeed(); g.fundCashFree();
        for (FundLedger.Lot lot : fund.getLedger().getLots()) { lot.acb(); lot.realized(); lot.income(); lot.isClosed(); }
        for (FundLedger.Activity row : fund.getLedger().getActivity()) { row.average(); row.lapsed(); }
        java.util.List<FundView.Position> held = FundView.positions(g);
        FundView.portfolio(g);
        FundView.closedLots(g);
        HistorySave fh = g.getHistorySave();
        if (fh.months() > 0) {
            int lastMonth = fh.getMonth().get(fh.months() - 1);
            for (int range : ChartModel.RANGES) FundView.rangeReturn(fh, lastMonth - range, lastMonth);
        }
        for (String filter : new String[] { "All", "Shares", "Bonds", "Held" }) {
            FundView.search(g, "", filter);
            FundView.search(g, "a", filter);
        }
        for (String group : new String[] { "All", FundView.TRADES, FundView.INCOME, FundView.MONEY, FundView.EVENTS }) {
            FundView.activity(g, group, null);
        }
        for (FundView.Position pos : held) {
            FundView.yieldOnCost(g, pos);
            FundView.activity(g, "All", pos.key());
            if (pos.isShare() || pos.isBond()) {
                FundView.quote(g, new FundView.Order(pos.key(), true, true, 1_000, 0));
                FundView.quote(g, new FundView.Order(pos.key(), false, false, pos.units() / 4, 0));
            }
        }
        for (FundView.Hit hit : FundView.market(g)) {
            FundView.quote(g, new FundView.Order(hit.key(), true, true, 1_000, hit.price() * 1.05));
        }
        for (TreasuryFund.Resolution r : fund.getResolutions()) {
            r.month(); r.paid(); r.fromCash(); r.advanced(); r.shortfall(); r.exitCapital(); r.shares();
            r.householdsShares(); r.householdsValue(); r.worldShares(); r.worldValue(); r.fundShares(); r.fundValue();
            r.preferredCancelled(); r.warrantsCancelled(); r.ownersLost();
        }
        bk.getShortfallThisMonth(); bk.getShortfallLifetime(); bk.isInResolution(); bk.resolutionExitEquity();
        bk.preferredOutstanding(); bk.getPreferred(); bk.getPreferredArrears(); bk.inConsentPeriod();
        bk.wantsPreferred(); bk.preferredOfferSize(); bk.preferredOfferCapped(); bk.preferredOfferShortOfTarget();
        bk.warrantSharesOut(); bk.warrantValue(1, .3, .02); bk.getPreferredIn(); bk.getPreferredRedeemedThisMonth();
        bk.getPreferredDividendsThisMonth(); bk.getPreferredAccruedThisMonth(); bk.getOwnersWipedThisMonth();
        bk.getPreferredCancelledThisMonth(); bk.getPreferredDividendsLifetime(); bk.getPreferredRedeemedLifetime();
        bk.getWarrantsBoughtBackLifetime(); bk.sheet(Bank.Sheet.PREFERRED); bk.yearAgo(Bank.Sheet.PREFERRED);
        bk.equityMovement().preferredIn(); bk.equityMovement().warrantsBoughtBack(); bk.preferredRecordToSave();
        for (Bank.Preferred block : bk.getPreferred()) {
            block.par(); block.issued(); block.capPerShare(); block.warrantShares(); block.strike();
            block.warrantsExpire(); block.warrantsOut(); block.rate(g.getMonth()); block.inConsent(g.getMonth());
            block.arrears(); block.due(g.getMonth());
        }
        bk.getRepaymentRaisedThisMonth(); bk.getRepaymentRaisedLifetime(); bk.getRepaymentShortLifetime();
        g.getDebtManager().marketToSave(); g.getDebtManager().getAdvances();
        g.getEconomyManager().getNationalAccounts().getFundTransfer();
        Founding.landBondUsd(g.getLandManager().getCityLand());
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
            // ...and EM2's: the quote and what it is made of, the world's short and who lends it, the month's fees, and what the short paid.
            x.lastTrade(company); x.quoteIsLastTrade(company); x.quoteWords(company); x.markedAtFair(company); x.cityMark(company);
            x.shortShares(company); x.shortCollateral(company); x.shortInterest(company); x.lendingRateOf(company);
            x.lentByCells(company); x.lentByFund(company);
            x.lendingFeeToCells(company); x.lendingFeeToFund(company); x.lendingFeeThisMonth(company);
            g.getEquity().getShortPaysThisMonth(company); g.getEquity().getLifetimeShortPays(company);
            FundView.quoteMonth(x, company, g.getMonth());
        }
        g.getExchange().isShortSelling();
        g.getExchange().getLendingFeeToFund(); g.getExchange().getLendingFeeToCells();
        g.getExchange().getLifeShortSold(); g.getExchange().getLifeShortSoldValue();
        g.getExchange().getLifeShortCovered(); g.getExchange().getLifeShortCoveredValue();
        g.getExchange().getLifeShortAsks(); g.getExchange().getLifeShortMonths();
        g.getExchange().getLifeMarginCalls(); g.getExchange().getLifeRecalls(); g.getExchange().getLifeLendingFees();
        g.getEquity().getShortPaysThisMonth(); g.getEquity().getLifetimeShortPays();
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
        // the debt (2026-09-21): since XR1 the page reads the float's pure
        // readers (the gate, the balance's and the cover's levels, the fair
        // level, the trend, the month's six moves) rather than a preview of
        // the push, and none of them records - ForeignCheck's section on what
        // the Trade tab reads asserts the rate, the vault, the moves and the
        // market equal before and after
        g.getForeignAccounts().gate();
        g.getForeignAccounts().balanceTarget();
        g.getForeignAccounts().coverTarget();
        g.getForeignAccounts().getFairLevel();
        g.getForeignAccounts().deviationFromFair();
        g.getForeignAccounts().getParityTrend();
        g.getForeignAccounts().getMoveTrend();
        g.getForeignAccounts().getMoveGap();
        g.getForeignAccounts().getMoveBalance();
        g.getForeignAccounts().getMoveCover();
        g.getForeignAccounts().getMoveIntervention();
        g.getForeignAccounts().getMoveMarket();
        // ...and the world's (WN4): its line among the forces, the cycle, the risk-off and the loading the card names, the terms of trade reached
        g.getForeignAccounts().getMoveWorld();
        g.getForeignAccounts().getWorldCycle();
        g.getForeignAccounts().getWorldRiskOff();
        g.getForeignAccounts().getWorldLoading();
        g.getForeignAccounts().getWorldLevel();
        g.getForeignAccounts().getTermsOfTradeReached();
        g.getForeignAccounts().riskOffLoading();
        g.getForeignAccounts().getLastMove();
        g.getForeignAccounts().getLastAbsorption();
        g.getForeignAccounts().getLastRevaluation();
        // ...and what 0.7.35's Trade tab reads: the cover a purchase would buy,
        // its verdicts, the month's goods off the businesses' books, what is
        // held where, and History's rate and parity
        // ...and what 0.7.45's pass over the new price model reads: the anchor's
        // move and miss, the real dial, the neutral rate, the index's components,
        // the shelf's limit and its prices, who went without and the vouchers,
        // NEEDS YOU's PRICES row, the basket's links on the chart, the year's
        // rate per component, EARNED's walk with the vouchers in it
        g.getExpectations().getCredibilityStep();
        g.getExpectations().missFrom(g.getDebtManager().getInflationTarget());
        g.realPolicyRate();
        g.getDebtManager().neutralRate();
        for (int k = 0; k < PriceIndex.COMPONENTS; k++) {
            g.getPriceIndex().getComponentLevel(k);
            YearBook.componentInflation(record, k);
        }
        g.getPriceIndex().isLuxuryCapped();
        ham.citybuildersim.sectors.Retail shops = g.getSectors().retail();
        shops.getHandOver();
        shops.isShelfBound();
        shops.getCapPrice();
        shops.clearsPastTheCap();
        shops.isSlack();
        shops.isSaleCounted();
        shops.getChargedPrice();
        shops.ownLines(g);
        g.getHouseholdBalance().getHungerPricedOut();
        g.getHouseholdBalance().getHungerShortOfStock();
        g.getHouseholdBalance().groceriesByRow(PolicyPreview.lastSalePrice(g));
        g.getHouseholdBalance().getHouseholdsAssisted();
        for (double share : new double[] {0, .5, 1}) {
            PolicyPreview.foodAssistanceAt(g, share);
            PolicyPreview.foodAssistanceHouseholdsAt(g, share);
            PolicyPreview.foodAssistanceBasketsAt(g, share);
        }
        CityNeeds.measure(g, CityNeeds.PLAIN);
        ChartModel.basketLinks(record, g.getPriceIndex());
        g.getEarnedToBudget();
        g.getEarnedResidual();
        g.getInfrastructureManager().getFareLevel();
        // ...and 0.7.46's load path (batch A): the month's trade in units a
        // save carries (A1, both maps through the getters and the save's state)
        for (Sector trader : g.getSectors().all()) {
            for (Good good : Good.values()) { trader.unitsExported(good); trader.unitsImported(good); }
            // ...and since 0.7.133 the month's write-offs, carried the same way, and the struck ones the accounts read
            for (Good good : Good.values()) { trader.unitsWrittenOff(good); trader.writtenOffStruck(good); }
            trader.toState();
        }
        g.getEconomyManager().getNationalAccounts().getHeldWrittenOff();
        // ...and since 0.7.136 (batch FX1) the other three terms' write-offs and what each added back
        NationalAccounts fx1 = g.getEconomyManager().getNationalAccounts();
        fx1.getFoodWrittenOff(); fx1.getMaterialsWrittenOff(); fx1.getLuxuriesWrittenOff(); fx1.getWrittenOffAddedBack();
        g.getForeignAccounts().coverWith(1_000);
        g.getForeignAccounts().coverWith(-1_000);
        g.getForeignAccounts().toCover(ForeignAccounts.THIN_COVER);
        g.getForeignAccounts().isMonthCounted();
        g.getForeignAccounts().getLifetimeTradeBalance();
        ForeignAccounts.parityLevel(g.getForeignAccounts().deviationFromParity());
        ForeignAccounts.coverLevel(g.getForeignAccounts().importCover());
        g.getTradeByGood();
        g.getOwnReserves();
        g.getSharesHeldAbroad();
        g.getHeldAbroad();
        g.getHeldHereByTheWorld();
        record.changeOver("fxRate", 1);
        record.changeOver("fxRate", 12);
        record.recentTotal("exportsAbroad", 12);
        // the Policy tab's previews (0.7.36): a staged set through a copy of
        // the policy, and every owner's read of it - the take and the budget
        // (PolicyPreview), the payroll lines, the pensions, the EI pool, the
        // bank's next bill, the savers' rate, the floor, the curve's
        // compression, the ceiling, the pensioner household
        TaxPolicy staged = g.getEconomyManager().getTaxPolicy().copy();
        staged.setIncomeTaxRate(staged.getIncomeTaxRate() + .01);
        staged.setPensionReplacement(staged.getPensionReplacement() + .05);
        PolicyPreview.taxTake(g, staged);
        PolicyPreview.budget(g, staged, g.getEducation().getTuitionSubsidy());
        PolicyPreview.realDepositRateAt(g, g.getDebtManager().getPolicyRate() + .0025);
        PolicyPreview.spendFactorAt(g, g.getDebtManager().getPolicyRate() + .0025);
        PolicyPreview.cityPayrollAt(g, g.getLabourMarket().getMinimumWage() * 1.1);
        PolicyPreview.pensionerHasUnder(g, staged);
        PolicyPreview.tuitionPaid(g, staged, g.getEducation().getTuitionSubsidy());
        g.getEconomyManager().contributionsAt(.07);
        g.getEconomyManager().premiumAt(.02);
        g.getEconomyManager().pensionsPaidUnder(staged);
        g.getUnemployment().benefitsAt(.6);
        g.bankTaxUnder(staged);
        g.getBank().depositRateAt(.05);
        g.getLabourMarket().targetWageAt(JobType.NO_DIPLOMA, g.getLabourMarket().getMinimumWage() * 1.1);
        g.getLabourMarket().floorForCash(g.getLabourMarket().cashAt(g.getLabourMarket().getMinimumWage()));
        g.getDebtManager().compressionAt(.3, 360);
        g.getCentralBank().ceilingAt(12);
        // the curve and who holds the paper (0.7.1): the borrow page's term
        // columns (five rows until 0.7.32), the rate page's premium and
        // compression, the book's holders bars, the money page's holdings
        // and the households' paper
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
        // ...and the businesses' paper (CD2, slice 4): the piece's share, the book at the curve, and the sectors' side of it
        debt.firmPrincipal();
        debt.firmBookRatio();
        debt.firmBookYield();
        g.getOutwardInvestment().totalPaper();
        g.getOutwardInvestment().paperOf(Sectors.RETAIL);
        g.getOutwardInvestment().idleWealth(g.getEconomyManager());
        g.getOutwardInvestment().getPaperRatio();
        g.getOutwardInvestment().getPaperYield();
        g.getOutwardInvestment().getLifetimePaperBought();
        g.getOutwardInvestment().getLifetimePaperSold();
        g.getFirmsBoughtPaper();
        g.getCouponsToFirms();
        g.getPrincipalToFirms();
        // ...and the world's (CD2, slice 5): the piece's share, the book at the curve, CapitalFlows' side of it, the month's flows
        debt.worldPrincipal();
        debt.worldBookRatio();
        g.getCapitalFlows().getPaper();
        g.getCapitalFlows().getLifetimePaperBought();
        g.getCapitalFlows().getLifetimePaperSold();
        // ...and FX8's: the hot money's month, saved since slots 16-18, what the central bank paid in new money for the vault's dollars, the vault's record and the peg's doubt
        g.getCapitalFlows().getArrived();
        g.getCapitalFlows().getDeparted();
        g.getCapitalFlows().netFlow();
        g.getCapitalFlows().getDoubtDeparted();
        g.getCapitalFlows().getDoubtDepartedLifetime();
        // ...and XR4's: what ran home in a risk-off (the hot money's and the carry's, the month's and since founding) and the months it did
        g.getCapitalFlows().getRiskOffDeparted();
        g.getCapitalFlows().getRiskOffDepartedLifetime();
        g.getCapitalFlows().getCarryRiskRepaid();
        g.getCapitalFlows().getCarryRiskRepaidLifetime();
        g.getCapitalFlows().getRiskOffMonths();
        g.getCapitalFlows().toSaveArray();
        g.getCentralBank().getDollarsBought();
        g.getCentralBank().getDollarsBoughtLifetime();
        g.getForeignAccounts().getLifetimeIntervention();
        g.getForeignAccounts().getDoubtShare();
        g.getWorldBoughtPaper();
        g.getCouponsToWorld();
        g.getPrincipalToWorld();
        g.getPaperBoughtFromWorld();
        debt.bankUnearnedDiscount();
        debt.termPrincipal();
        debt.centralBankShareOfTerm();
        // ...and 0.7.15's floor split by who holds the paper, as the rate page and the playtest read it
        debt.centralBankShareOfPaper();
        debt.floorRate();
        debt.bankFloorRate();
        debt.baseComponent();
        debt.rateAtPolicy(debt.getPolicyRate() + .01);
        g.getCentralBank().getBoughtFromHouseholds();
        g.getCentralBank().getBoughtFromHouseholdsLifetime();
        // ...and 0.7.15's rollover at issue, as the Finances hub's rollover card and the playtest read it
        g.getCentralBank().getBoughtAtIssue();
        g.getCentralBank().getParAtIssue();
        g.getCentralBank().getBoughtAtIssueLifetime();
        g.getCentralBank().getParAtIssueLifetime();
        g.centralBankParFallingDue();
        g.centralBankOverItsDial();
        g.rolloverPlan().centralBankRunsOff();
        // ...and what last year's surplus pays of the central bank's par
        g.rolloverPlan().centralBankNetted();
        debt.getTotalMarketValue();
        for (Debt paper : debt.getDebt()) debt.marketValue(paper);
        // ...and what 0.7.32's Finances tab draws the debt from: the coupon, each
        // kind's principal, the ladder by calendar year with and without a
        // proposed issue, the next twelve months, the rate a piece is valued at,
        // the rollover's cash, the borrowing flags, the bond market's issuers
        // and sums, what every business owes, the net position, M0's move,
        // what a buyback saves against face and the service band
        debt.getMonthlyCoupon();
        // ...and CD6's: the uplift due next month, the interest booked on each kind of paper (and the copy a save
        // takes of it), each piece's coupon without its uplift, the call's gain and the honest line's four figures
        debt.getUpliftDueNextMonth();
        for (String kind : DebtManager.BOOK_KINDS) debt.getInterestBookedOf(kind);
        debt.interestByKindToSave();
        for (Debt paper : debt.getDebt()) {
            paper.getMonthlyCoupon();
            paper.getUpliftDue();
            g.callGain(paper);
            g.honestLine(paper, Buyback.Result.none(paper.getOustandingPrincipal()));
        }
        // ...and CD6's desk: the desk itself (a pure model; Game.bookDesk() strikes the market as fairValueOf() does), each
        // piece's books ahead, sales and price path, its value of a point and duration, and the curve book and the rule
        BookDesk.of(debt);
        g.bookDesk();
        // ...and CD7's: the exchange since the sale, on the desk and on each line, and what each piece keeps of the rate it was sold at
        BookDesk exchangeDesk = g.bookDesk();
        exchangeDesk.exchangeSinceSold();
        exchangeDesk.exchangeShare();
        for (BookDesk.Line line : exchangeDesk.lines()) {
            line.exchangeSinceSold();
            line.exchangeShare();
        }
        for (Debt paper : debt.getDebt()) {
            paper.soldAtRate();
            paper.exchangeSinceSold();
            for (Debt.Sale sale : paper.getSales()) sale.getRate();
            paper.booksAhead(6);
            paper.getSales();
            paper.getPricePath();
            paper.getCarriedPath();
            paper.getPathFirstMonth();
            debt.valueOfAPoint(paper);
            debt.durationOf(paper);
        }
        for (int ago = 0; ago <= DebtManager.CURVE_KEPT; ago++) {
            debt.curveMonthsAgo(ago);
            debt.worldCurveMonthsAgo(ago);
            debt.policyRateMonthsAgo(ago);
        }
        debt.curveBookSize();
        debt.curveBookToSave();
        debt.ruleNow();
        debt.settledDial();
        for (String kind : DebtManager.LADDER_KINDS) debt.getPrincipalOf(kind);
        debt.ladder(g.getMonth());
        debt.ladder(g.getMonth(), g.quoteDebt("Serial", 1_000, 3, 100).schedule(1));
        debt.dueWithin(12);
        for (Debt paper : debt.getDebt()) debt.valuationRate(paper);
        g.rolloverPlan().fromCash();
        ChartModel.flags(g.getDecisions(), DecisionLog.BORROWING);
        // ...and 0.7.33's Bank tab: its rates' flags, the branch verdict the
        // investors' planner gives (a read - BankCheck section 13b)
        ChartModel.flagsOf(g.getDecisions(), DecisionLog.CENTRAL_BANK, DecisionLog.BANK);
        // ...and 0.7.37's City History: what is running, the flags as its lane
        // draws them, and where each good's price sits in its band
        YearBook.running(record);
        if (record.months() > 0) ChartModel.onAxis(ChartModel.flags(g.getDecisions()), record.getMonth().get(0));
        for (Good good : Good.values()) if (g.getMarkets().get(good) != null) g.getMarkets().get(good).getPriceIndex();
        g.getBusinessInvestment().planBank();
        g.getBondMarket().byIssuer();
        g.getBondMarket().getCouponsPaid(); g.getBondMarket().getPrincipalRepaid();
        g.getBondMarket().getWrittenOffThisMonth();
        g.getEconomyManager().getBusinessDebtManager().getEverythingOwed();
        g.getNetPosition(); g.getCentralBank().getM0Moved();
        for (Debt paper : debt.getDebt()) debt.underFace(paper);
        CityNeeds.serviceLevel(debt.dueWithin(12) / Math.max(1, g.getEconomyManager().getNationalAccounts().getTotalRevenue() * 12));
        g.getHouseholdBalance().totalPaper();
        g.getHouseholdBalance().marketValueOfPaper();
        g.getHouseholdBalance().getPaperRatio();
        g.getCentralBank().getPaperHeld();
        g.getCentralBank().stepFor(debt.termPrincipal());
        g.getBuybackUnsettled();
        // how the city was founded (0.7.10): the founders' note, the window's
        // title and every screen that writes the city's money read these, and
        // the founding screen asked what each preset would buy (until 0.7.20;
        // the read stays) - of the live city's catalogue, which it must not touch
        g.getFounding();
        // ...and the world it stands on, the real Earth or not (0.7.121): the founding record's and the save's reads.
        Founding founded = g.getFounding();
        founded.getWorldKind(); founded.isOnEarth(); founded.getEarthLat(); founded.getEarthLon(); founded.getEarthData();
        CityMap.worldId(founded.getWorldKey());
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
            // ...and what the redrawn Sectors screen draws it from (0.7.30): the
            // page's two halves, the flow, the plant, its investors and their
            // word's kind, its Build group and note, and its debt by kind
            sec.plantLines(g);
            sec.ownLines(g);
            sec.buildingsStanding();
            sec.buildingsOnSite();
            SectorFlow.of(g, sec);
            SectorFlow.plant(sec);
            BuildCard.SectorInvestors si = BuildCard.sectorInvestors(g, sec);
            BuildCard.wordKind(si.word(), si.theirs(), si.landBlocked());
            BuildCard.groupOf(g, sec);
            BuildCard.categoryOf(g, sec);
            BuildCard.noteOf(g, sec);
            // ...and the investor report's every gate, with how each would be paid for (0.7.75, R4), and the
            // statements' debt by kind, what moved it, its stock at today's prices (R2, R6, R7)
            BuildCard.appraiseAll(g, sec);
            e.getBusinessDebtManager().debtMovedByKind(sec.key());
            e.getBusinessDebtManager().debtByKind(sec.key(), g.getMonth());
            sec.stockAtPrices();
            g.getEquity().getPaidIn(Equity.indexOf(sec.key()));
            e.getBusinessDebtManager().getTermLoanPrincipal(sec.key());
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
        // ...and an order bar's several at once, its Build button's total (0.7.34)
        BuildAdvice.quoteTotal(g, Map.of(template(g, "House"), 3, template(g, "Water Treatment Plant"), 1));
        // ...and the overview's suggestions added up (0.7.38)
        BuildAdvice.quoteTotal(BuildAdvice.suggest(g));
        // ...and every gauge as served, with its one verdict (0.7.41): the owners' getters, each row's,
        // each measure's now and with what is on site, each suggestion's after, the tile's unlisted row
        s.getUtilitiesHandler().getPowerServed();
        s.getUtilitiesHandler().getWaterServed();
        g.getInfrastructureManager().getServed();
        g.getInfrastructureManager().getTransitServed();
        InfrastructureManager.throughputAtServed(g.getInfrastructureManager().getServed());
        java.util.List<CityNeeds.Need> served = CityNeeds.measure(g, CityNeeds.PLAIN);
        for (CityNeeds.Need n : served) { n.served(); n.verdictLevel(); }
        for (CityNeeds.Go go : CityNeeds.Go.values()) CityNeeds.worstUnlisted(served, go);
        for (CareType care : new CareType[] {CareType.GENERAL, CareType.CHILDCARE, CareType.SENIOR}) {
            CityNeeds.careServed(g, care, g.getCohorts(), g.getPopulationManager().getJobFillRate());
        }
        for (String category : new String[] {BuildAdvice.UTILITIES, BuildAdvice.ROADS, BuildAdvice.HEALTHCARE,
                BuildAdvice.EDUCATION, BuildAdvice.SAFETY}) {
            for (BuildAdvice.Measure m : BuildAdvice.measuresOf(category)) {
                BuildAdvice.verdict(g, m, Map.of());
                BuildAdvice.verdict(g, m, BuildAdvice.onSite(g, m));
                BuildAdvice.servedLines(m);
            }
        }
        for (BuildAdvice.Suggestion sg : BuildAdvice.suggest(g)) BuildAdvice.verdictAfter(g, sg);
        // ...and what the advice reads since 0.7.51: the businesses' growth, the land office's price, the
        // high schools' leavers, the students a higher school would get and hire, an order's projection,
        // and every suggestion's ground, its runner-up and the run it makes
        adviceReads(g);
        // the fare's dial card (0.7.38): its rows at the city's fare, at none and at the dearest
        for (double fare : new double[] {g.getEconomyManager().getTaxPolicy().getTransitFare(), 0, TaxPolicy.MAX_TRANSIT_FARE}) {
            g.getInfrastructureManager().ridersAt(fare);
            g.getEconomyManager().transitNetAt(g.getInfrastructureManager().faresAt(fare));
            g.getInfrastructureManager().backOnTheRoadAt(fare);
            TaxPolicy.monthlyFareAt(fare);
        }
        // ...and what the build screen's funding page was sized to from 0.7.10
        // (one order's gap), for an order the city can pay for and one it cannot
        g.buildFundingGap(template(g, "House"), 1);
        g.buildFundingGap(template(g, "Water Treatment Plant"), 1_000);
        // ...and a run of orders, which its funding page is sized to since 0.7.40
        Map<BuildingsTemplate, Integer> run = new java.util.LinkedHashMap<>();
        run.put(template(g, "House"), 3);
        run.put(template(g, "Water Treatment Plant"), 1_000);
        g.buildRunInvoice(run);
        g.buildFundingGap(run);
        g.buildRunAhead(run);
        g.buildRunStop(run);
        g.cashShortfall();
        // the borrow page's presets (0.7.40)
        g.minimumIssueSizeUsd();
        g.getDebtManager().dueWithin(12);
        g.getDebtManager().dueAbroadWithinUsd(12);
        g.monthOfSpending();
        g.getDebtManager().annualCapacityRevenue();

        // land, ore and every market
        g.getLandManager().getAvailableSqFt();
        g.getLandManager().getPricePerSqFt();
        g.getLandListing();
        // ...and the land on the world (0.7.57): what it holds, the offers by
        // every measure, the best for each need, the world's ledger
        LandManager lm = g.getLandManager();
        LandMarket lmk = lm.getMarket();
        g.getCityLand().centreState(); g.getCityLand().holdingsState(); g.getCityLand().centreRectsState();
        g.getCityLand().partFieldsState(); g.getCityLand().convertedState(); g.getCityLand().stamp();
        lm.getOwnedKm2(); lm.getFreshKm2(); lm.getSeaKm2(); lm.getForestKm2(); lm.getLandDrySqFt();
        lm.getDepletionState(); lm.getWorldTotalsState(); lm.getWorldSeaTheta();
        for (Resource r : Resource.values()) {
            lm.getOwnedAmount(r); lm.getRemaining(r); lm.getExtracted(r); lm.getUnowned(r); lm.remainingByHolding(r);
            lmk.richest(r);
            g.bestOffer(Game.LandNeed.deposit(r));
            // ...and the ground by type (0.7.110): its sites, tonnes and E by type, its runs and where a works fits
            for (Deposit.FieldType t : r.typesOf()) {
                lm.getSites(r, t); lm.getOwnedAmount(r, t); lm.getRemaining(r, t); lm.getExtracted(r, t); g.committedOn(r, t);
                lm.fits(r, t, java.util.List.of(new LandManager.Claim(null, 1, g.committedOn(r))), 1);
            }
            lm.getExtractedByType(r); g.getCityLand().sitesByType(r); lm.typeRuns(r); g.committedOn(r, null);
        }
        lm.getDepletionByTypeState();
        for (int side = 0; side < CityLand.SIDES; side++) lmk.offersOn(side);
        lmk.bestValue(); lmk.cheapest(); lmk.cheapestWithSea(); lmk.getLevel(); lmk.getOffersState();
        lmk.goingUsdPerSqFt(); lmk.getBlockPlots(); for (int side = 0; side < CityLand.SIDES; side++) lmk.emptyOn(side);
        g.bestOffer(Game.LandNeed.room()); g.bestOffer(Game.LandNeed.coast());
        // ...and the water on it (0.7.59): the limit, the rights, the coast and their words
        UtilitiesHandler uw = g.getServicesManager().getUtilitiesHandler();
        g.getFreshCap(); g.getFreshRights(); lmk.bestFresh();
        uw.getFreshCap(); uw.getFreshNameplate(); uw.getFreshDrawn(); uw.getDesalNameplate(); uw.getDesalOutput();
        uw.isFreshCapped(); uw.getFreshIdleShare(); uw.getFreshHeadroom(); uw.getWaterAtFullStaff();
        CityNeeds.freshLimitLine(uw);
        g.hasCoastFor(g.getBuildingManager().getTemplateByName("Desalination Plant"), 1);
        g.bestOffer(Game.LandNeed.shortfall(lm.getAvailableSqFt() + 1_000_000));
        // ...and fuel (0.7.62): the oil the city owns and its wells, the refinery's crude and estimate, the
        // drivers' and the railway's fuel and what of it was imported, a journey's fuel at the landed price
        lm.getOilSites(); lm.getOilReserveTonnes(); lm.getOilLiftedThisMonth();
        for (Resource r : Resource.values()) { lm.getSites(r); g.committedOn(r); }
        g.wellsCommitted(); g.minesCommitted(); g.getHouseholdFuel(); g.getHouseholdFuelImports();
        g.hasDepositFor(g.getBuildingManager().getTemplateByName("Oil Well"), 1);
        g.getSectors().oil().getPotentialOutput(); g.getSectors().oil().ownLines(g);
        BuildingsTemplate refineryT = g.getBuildingManager().getTemplateByName("Oil Refinery");
        g.getSectors().refining().spareCrude(); g.getSectors().refining().getCrudeDemand();
        // ...and the slate's reads (0.7.76): the products, their room, what a crude unit makes.
        for (Good p : g.getSectors().refining().goodsMade()) {
            g.getSectors().refining().getCapacity(p); g.getSectors().refining().getPipeline(p);
            g.getSectors().refining().getStockCapacity(p);
        }
        ham.citybuildersim.sectors.Refining.madeBy(refineryT); ham.citybuildersim.sectors.Refining.slate(8300);
        // ...and crude by grade (0.7.79): the mix and its slate, the oil as it is worked out, the lift's grades, dry and sea sites, the depth.
        g.getSectors().refining().getCrudeMix(); g.getSectors().refining().slateOf(8300);
        ham.citybuildersim.sectors.Refining.slate(8300, g.getSectors().refining().getCrudeMix());
        lm.oilRuns(); lm.getOilLiftedByGrade();
        // ...and the two pools (0.7.93): each pool's tonnes, E and what is left, the sea's runs, an order's pool, the month's lift by pool.
        lm.getOilOwnedOnGround(); lm.getOilOwnedAtSea(); lm.getOilExtractedOnGround(); lm.getOilExtractedAtSea();
        lm.getOilLeftOnGround(); lm.getOilLeftAtSea(); lm.oilRunsAtSea();
        g.remainingFor(g.getBuildingManager().getTemplateByName("Oil Well"));
        g.remainingFor(g.getBuildingManager().getTemplateByName("Platform Well"));
        g.getSectors().oil().getLiftedOnGround(); g.getSectors().oil().getLiftedAtSea();
        for (Resource r : Resource.values()) { lm.getSites(r, true); lm.getSites(r, false); }
        // (the elevation field's shelf a generated world's alone: an Earth world has none, and refuses it - 0.7.121)
        if (!g.getWorld().isEarth()) g.getWorld().shelfTheta();
        g.getWorld().depthAt(g.getCityLand().siteX(), g.getCityLand().siteY());
        g.getSectors().refining().estimatedMonthlyProfit(refineryT, g.getBusinessInvestment());
        // ...and the refinery's units and flow (0.7.80): the flow and what it is solved on, a unit's products, feed and estimate.
        g.getSectors().refining().flow(); g.getSectors().refining().flowValues();
        g.getSectors().refining().unitFeed(false); g.getSectors().refining().unitFeed(true);
        BuildingsTemplate reformerT = g.getBuildingManager().getTemplateByName("Small Reformer");
        ham.citybuildersim.sectors.Refining.madeBy(reformerT);
        ham.citybuildersim.sectors.Refining.feedValueOf(reformerT, g.getSectors().refining().flowValues());
        g.getSectors().refining().estimatedMonthlyProfit(reformerT, g.getBusinessInvestment());
        BuildCard.valueAdded(g, reformerT);
        // ...and the spread planner (0.7.82): the city's values, the outlook and the appraisal, the money gate's hurdle,
        // and the idle months and the shedding hooks.
        ham.citybuildersim.sectors.Refining refiners = g.getSectors().refining();
        refiners.cityValues(); refiners.outlook(g.getBusinessInvestment());
        refiners.appraise(refineryT, g.getBusinessInvestment()); refiners.appraise(reformerT, g.getBusinessInvestment());
        refiners.planner().hurdle(refiners, g.getBusinessInvestment(), g, g.getBusinessInvestment().getCostOf(reformerT, 1));
        refiners.idleFeed(); refiners.idleMonths(ham.citybuildersim.sectors.RefineryFlow.Kind.LUBE);
        refiners.mayRetire(reformerT); refiners.mayRetire(refineryT); refiners.retirementDemandAndCapacity(g);
        refiners.unitsOf(reformerT);
        // ...and the wells' decline (0.7.84): the vintages as struck and as read, the lift, the worn out, a well's part of
        // the shrinking measure, the land well and its dry sites.
        ham.citybuildersim.sectors.Oil wellsR = g.getSectors().oil();
        BuildingsTemplate wellT = g.getBuildingManager().getTemplateByName("Oil Well");
        wellsR.vintages(); wellsR.vintagesNow(); wellsR.getCapacity(Good.CRUDE); wellsR.newWellsCapacity(); wellsR.wornOut();
        wellsR.landWellsStanding(); wellsR.wellNameplate(); wellsR.landWell(); wellsR.unitsOf(wellT);
        wellsR.retirementDemandAndCapacity(g); g.sitesFor(wellT); g.getWellsWornOut();
        g.getMotoring().getFuelBill(); g.getMotoring().getFuelImports(); g.getMotoring().getFuelLitres();
        Motoring.journeyFuel(g.getMarkets());
        g.getSectors().freight().getFuelBill(); g.getSectors().freight().getFuelImported();
        // ...and the phase-1 buyers (0.7.83): the forecourts' month, their stations, the price on the sign, a station's
        // word and estimate; the vans' diesel; a road's bitumen and its quote
        ham.citybuildersim.sectors.Retail forecourts = g.getSectors().retail();
        BuildingsTemplate stationT = g.getBuildingManager().getTemplateByName("Filling Station");
        Motoring.journeyFuel(g);
        forecourts.pumpPriceToday(g); forecourts.stationsStanding(); forecourts.stationsOnSite(); forecourts.stationLitres();
        forecourts.stationCapacity(); forecourts.getPumpLitres(); forecourts.getQueueLitres(); forecourts.getPumpCapacity();
        forecourts.getPumpPrice(); forecourts.getQueuePrice(); forecourts.getWholesaleLitre(); forecourts.getFuelBill();
        forecourts.getFuelImported(); forecourts.litresForecast(stationT, g.getBusinessInvestment());
        forecourts.estimatedMonthlyProfit(stationT, g.getBusinessInvestment()); forecourts.mayRetire(stationT);
        forecourts.planStations(g.getBusinessInvestment(), g);
        // ...and the fuel duty (0.7.103): the forecourts' month's, the treasury's, the budget's line, the dial and the previews.
        forecourts.getFuelDuty(); g.getEconomyManager().getFuelDuty(); g.getEconomyManager().getNationalAccounts().getFuelDuty();
        TaxPolicy dutyDial = g.getEconomyManager().getTaxPolicy();
        dutyDial.getFuelDuty(); dutyDial.chargedFuelDuty(); dutyDial.maxFuelDuty();
        PolicyPreview.fuelLitres(g); PolicyPreview.fuelDutyAt(g, dutyDial); PolicyPreview.pumpPriceAt(g, dutyDial);
        // ...the lubricants' cap and the road kinds' repairs (0.7.103).
        g.getSectors().refining().lubricantsWorldTakes(); g.getSectors().refining().getExportBoundOutput(Good.LUBRICANTS);
        for (BuildingsTemplate road : g.getBuildingManager().getTemplates()) { road.repairsPerYear(); road.hasOwnRepairs(); }
        for (Sector fleet : g.getSectors().all()) { fleet.getFleetDieselLitres(); fleet.getFleetDieselCost(); fleet.getFleetDieselImported(); }
        g.bitumenFor(g.getBuildingManager().getTemplateByName("Paved Road")); g.bitumenForPaving();
        g.getBitumenTonnes(); g.getBitumenCost(); g.getBitumenImported();
        g.quoteBuild(g.getBuildingManager().getTemplateByName("Elevated Highway"), 2);
        // ...and the oil storage (0.7.85): the refiners' Tank Farm and its store, the city's reserve, their cards
        BuildingsTemplate farmT = g.getBuildingManager().getTemplateByName("Tank Farm");
        BuildingsTemplate reserveT = g.getBuildingManager().getTemplateByName("Strategic Reserve");
        ham.citybuildersim.sectors.Refining.isTankFarm(farmT); StrategicReserve.isReserve(reserveT);
        refiners.tankFarmRoom(); refiners.crudeKept(); refiners.keepsCrude(); refiners.buysAhead(Good.CRUDE);
        refiners.bid(Good.CRUDE); refiners.getStockCapacity(Good.CRUDE); refiners.getPantry(Good.CRUDE);
        StrategicReserve.room(g.getBuildingManager());
        StrategicReserve res = g.getReserve();
        res.getTonnes(); res.getCost(); res.costPerTonne(); res.getRelease(); res.getFill();
        res.getBoughtHome(); res.getBoughtAbroad(); res.getSoldHome(); res.getSoldAbroad();
        res.getSettledImports(); res.getSettledExports(); res.getSettledBought(); res.getSettledSold();
        res.cityBid(Good.CRUDE); res.cityOffer(Good.CRUDE); res.toState();
        BuildCard.of(g, farmT, null); BuildCard.of(g, reserveT, null); BuildCard.citys(reserveT);
        // ...and the ports (0.7.86): the terminals, their berths and cards, the shares in force, the month, the boats
        BuildingsTemplate portT = g.getBuildingManager().getTemplateByName("General Cargo Terminal");
        Ports.isPort(portT); Ports.berthMonth(portT); portT.berthCargo(); portT.berthTonnesAYear(); portT.needsCoast();
        Ports.berths(g.getBuildingManager()); Ports.berthsOnSite(g.getBuildingManager()); Ports.freeCrudeRoom(refiners);
        Ports.crudeShipFor(50_000); Ports.bulkShipFor(70_000); Ports.factor(.25, .5, Ports.SEA_FREIGHT_SHARE_GENERAL);
        Ports pts = g.getPorts();
        for (Ports.Cargo kind : Ports.Cargo.values()) {
            pts.share(kind); pts.seaFirst(kind); pts.seaIn(kind); pts.seaOut(kind); pts.tradeIn(kind); pts.tradeOut(kind);
            pts.carriable(kind);
        }
        for (Good good : Good.values()) { good.cargo(); pts.seaShareOf(good, .5); pts.railShareOf(good, .5); Ports.seaFreightShareOf(good); }
        pts.crudeShip(); pts.billedCrudeShip(); pts.getHeldBack(); pts.anyAtSea(); pts.seaTonnes(); pts.crudeTrade();
        pts.crudeSeaIn(); pts.crudeSeaOut(); pts.railOfStream(g.getSectors().freight().getCarried()); pts.seaOfStream(); pts.toState();
        BoatSchedule boats = g.boats(java.util.List.of(new BoatSchedule.Berth(Ports.Cargo.GENERAL, 0, 0)));
        boats.callCount(); boats.calls(); boats.frame(-256, -256, 256, 256, .5); boats.touched(); boats.routes();
        // ...and the trains (0.8.2, batch FR1): the railway's tonnes by kind, the month's trains on one route, a frame, a
        // train's vehicles and shapes
        for (Ports.Cargo kind : Ports.Cargo.values()) { g.getSectors().freight().railedIn(kind); g.getSectors().freight().railedOut(kind); TrainSchedule.Kind.of(kind); }
        TrainSchedule.Route trainRoute = TrainSchedule.Route.through(0, true, 0, new double[] { -64, 0, 24 }, new double[] { 0, 0, 0 }, TrainSchedule.WORLD_PLOTS);
        TrainSchedule trains = TrainSchedule.of(g.getMonth(), g.getSectors().freight(), java.util.List.of(trainRoute));
        trains.callCount(); trains.calls(); trains.frame(-256, -256, 256, 256, .5); trains.touched(); trains.crosses(-256, -256, 256, 256); trains.stands(); trains.boxes(); trains.routes(); TrainSchedule.boxesByRail(g.getSectors().freight());
        for (TrainSchedule.Call trainCall : trains.calls()) {
            TrainSchedule.Train arriving = TrainSchedule.position(trainCall, trainRoute, trainCall.arrives());
            if (arriving == null) continue;
            TrainShapes.colour(arriving); TrainShapes.markColour(arriving);
            for (TrainSchedule.Car trainCar : TrainSchedule.cars(arriving, trainRoute)) { TrainShapes.body(trainCar); TrainShapes.mark(trainCar, arriving); }
        }
        g.getInfrastructureManager().getSeaShare(Traffic.BULK);
        BuildCard.of(g, portT, null); BuildCard.citys(portT); g.hasCoastFor(portT, 1); refiners.ownLines(g);
        // ...and where the terminals stand (ET5; Landmasses): the books by landmass, the linked set, the card's choices, the coast on a landmass,
        // the ground across the water that is no room, the map's picture, the placements as saved, and the words that say it
        Landmasses lms = lm.getLandmasses();
        lms.rows(); lms.linked(0); lms.linked(lms.home()); lms.home(); lms.single(); lms.anyUnlinked(); lms.unlinkedIds(); lms.unlinkedNames(); lms.version(); lms.plotsRead(); lms.active();
        lms.unlinkedDrySqFt(); lms.unlinkedForestSqFt(); lms.unlinkedPortsSqFt(); lms.unlinkedFreeSqFt(); lms.unlinkedFreeSqFt(lms.home()); lms.unlinkedReliefSqFt(World.FLAT);
        lms.portRoomSqFt(lms.home()); lms.shorePlots(lms.home()); lms.dryPlots(lms.home()); lms.name(lms.home()); lms.portsOn(lms.home()); lms.portsGroundSqFt(lms.home());
        lms.padSqFt(lms.home(), 1_000, 0); lms.portChoices(); lms.linkedShore(); lms.hasPlacements();
        for (Landmasses.Row row : lms.rows()) { lms.linked(row.id()); lms.portsOn(row.id()); lms.portRoomSqFt(row.id()); lms.portLandmass(portT.getId(), row.id()); lms.name(row.id()); g.hasCoastFor(portT, 1, row.id()); }
        lms.portLandmass(portT.getId(), Landmasses.CHOSEN); lms.portLandmass(portT.getId(), Landmasses.RULE); lms.chosen(portT.getId()); lms.freeToGo(portT.getId()); lms.linkedBy(portT.getId());
        lms.picture(); lms.toState();
        g.hasCoastFor(portT, 1, Landmasses.CHOSEN); g.hasCoastFor(portT, 1, Landmasses.RULE); g.portLandmassChoices(portT); g.freeGroundFor(portT); g.getLandmassPicture();
        lm.getUnlinkedFreeSqFt(); lm.getUnlinkedNames(); lm.getLinkedOwnedSqFt(); lm.getLinkedAllocatedSqFt(); lm.getLinkedForestSqFt(); lm.landmassState(); g.demolishable(portT); g.quoteBuild(portT, 1);
        for (LandParcel offer : lmk.getListing()) { LandmassView.islandTagged(offer); offer.getUnlinkedDryKm2(); offer.getUnlinkedBuildableKm2(); offer.getUnlinkedLargest(); }
        // ...and the long bridges (ET6; Bridges): the list as saved, the card's far landmass and its choices, the survey of the crossing (kept, never
        // filled by a figure the save holds), the gate, the span, the quote, the verdict and the words that say a refusal
        BuildingsTemplate bridgeT = g.getBuildingManager().getTemplateByName("Road Bridge");
        Bridges bridges = lm.getBridges();
        bridges.all(); bridges.size(); bridges.count(Bridges.STANDING); bridges.count(Bridges.ON_SITE); bridges.count(Bridges.SHELL); bridges.count(bridgeT.getId(), Bridges.STANDING);
        bridges.standingLengthM(); bridges.joins(lms.home()); bridges.joinedTo(lms.home()); bridges.chosen(bridgeT.getId()); lm.bridgeState(); lms.linkedIds();
        g.bridgeChoices(bridgeT); g.bridgeLandmass(bridgeT); g.hasBridgeFor(bridgeT, 1); g.bridgeSpan(bridgeT, 1); g.quoteBuild(bridgeT, 1); g.demolishable(bridgeT);
        BridgeSurvey.Result crossing = g.bridgeSurvey(bridgeT);
        BridgeView.refusal(crossing, lms); BridgeView.crossingWords(crossing, lms); BuildCard.bridge(g, bridgeT); BuildCard.verdict(g, bridgeT, 1);
        BuildCard.groups(g, BuildAdvice.ROADS, BuildAdvice.measuresOf(BuildAdvice.ROADS).get(0));
        // ...and the oil at sea (0.7.91): the platforms, their slots and wells, the pipelines, the shuttle tankers, the
        // planner's cases, the cards and the counts the orders are weighed against
        BuildingsTemplate jacketT = g.getBuildingManager().getTemplateByName("Offshore Platform");
        BuildingsTemplate seaWellT = g.getBuildingManager().getTemplateByName("Platform Well");
        BuildingsTemplate pipeT = g.getBuildingManager().getTemplateByName("Crude Pipeline");
        wellsR.platforms(); wellsR.pipelines(); wellsR.platformsNow(); wellsR.pipelinesNow(); wellsR.platformFields();
        wellsR.slotsStanding(); wellsR.platformWellsInSlots(); wellsR.platformRoom(); wellsR.jacketsStanding();
        wellsR.jacketsOnSite(); wellsR.platformWellsStanding(); wellsR.platformWellsCommitted(); wellsR.pipeKmStanding();
        wellsR.pipeKmCommitted(); wellsR.pipedShare(); wellsR.getCapacityAtSea(); wellsR.shuttleFreightPerTonne();
        wellsR.getShuttleTonnes(); wellsR.getShuttleBill(); wellsR.homeShareLastMonth(); wellsR.wornOut(ham.citybuildersim.sectors.Oil.WellKind.PLATFORM);
        wellsR.jacketCase(g.getBusinessInvestment()); wellsR.bestPipe(g.getBusinessInvestment()); wellsR.atSeaWord();
        wellsR.estimatedMonthlyProfit(jacketT, g.getBusinessInvestment()); wellsR.estimatedMonthlyProfit(seaWellT, g.getBusinessInvestment());
        wellsR.estimatedMonthlyProfit(pipeT, g.getBusinessInvestment()); wellsR.mayRetire(jacketT); wellsR.mayRetire(pipeT);
        wellsR.planOnLand(g.getBusinessInvestment(), g); wellsR.ownLines(g); g.getLandManager().seaFields(Resource.OIL);
        g.sitesFor(jacketT); g.sitesFor(seaWellT); g.committedFor(jacketT); g.committedFor(seaWellT); g.committedFor(wellT);
        g.landWellsCommitted(); g.platformWellsCommitted(); g.hasDepositFor(jacketT, 1); g.hasDepositFor(seaWellT, 1);
        BuildCard.of(g, jacketT, null); BuildCard.of(g, seaWellT, null); BuildCard.of(g, pipeT, null);
        // ...and the graduates the next census carries the loans of, as the save carries them (0.7.63)
        g.getHouseholdBalance().getGraduating(); g.getHouseholdBalance().getLastGraduated();
        g.getHouseholdBalance().graduatesToSave();
        // ...and the city map (0.7.60): drawn the first time it is asked, then its districts, the deal and the
        // painter's inputs for the founding site's tile, the tile painted and rastered, the pyramid and the sidecar
        CityMap map = g.getCityMap();
        g.hasCityMap(); g.getMapCounts(); g.getMapTypes(); g.getMapFailures();
        map.totals(); map.pyramid().root(); map.ownedSites(Resource.IRON);
        long tileX = Math.floorDiv(g.getCityLand().siteX(), World.TILE), tileY = Math.floorDiv(g.getCityLand().siteY(), World.TILE);
        TilePainter.Input tileIn = new TilePainter.Input();
        TilePainter.Painted tileOut = new TilePainter.Painted();
        map.tileInput(tileX, tileY, tileIn);
        TilePainter.paint(tileIn, tileOut);
        TileRaster.raster(tileIn, tileOut, 4, new int[(World.TILE * 4) * (World.TILE * 4)]);
        map.writeSidecar(g.getMonth());
        for (GoodsMarket m : g.getMarkets().all()) {
            m.getLocalPrice();
            m.getDemand();
            m.getSupply();
            m.getPriceIndex();
            m.isShortage();
            m.floor();
            m.ceiling();
        }

        // ...and the People page's reads since 0.7.27: the draw and its two
        // halves, who left and why, the dead by cause, the two hungers, each
        // tier's wage per earner and the fees named apart, the households'
        // ledger per row for the Pensions page
        Migration mig = g.getMigration();
        mig.getLastJobs(); mig.getLastHomeCapacity(); mig.getLastJobDraw(); mig.getLastHomeDraw();
        mig.getLastDrawBeforePulls(); mig.getLastWorkDepartures(); mig.getLastBankruptcyDepartures();
        mig.getLastCrimeDepartures(); mig.getLastAffordabilityPull(); mig.getLastCrimePull();
        mig.getLastSeniorPull(); mig.getLastTarget(); mig.getLastCrowding(); mig.toSaveArray();
        PopulationCohorts pyramid = g.getCohorts();
        pyramid.getLastDeathsOfAge(); pyramid.getLastAgedOut(); pyramid.getLastKilled(); pyramid.toSaveArray();
        for (AgeBand b : AgeBand.values()) { pyramid.share(b); PopulationCohorts.equilibriumShare(b); }
        HouseholdBalance people = g.getHouseholdBalance();
        people.getHungryAtFullShelves(); people.getHungryPeople(); people.getDeliveredShare(); people.toSaveArray();
        for (int r = 0; r < Household.ROWS; r++) { people.getDisposable(r); people.getFixedCosts(r); }
        HouseholdAccounts books = g.getHouseholds();
        books.careAndSchoolPerHead(); books.faresPerHead(); books.getAccountFees();
        for (PayTier tier : PayTier.values()) {
            books.wagePerEarner(g.getFamilies(), tier);
            books.interestPerHousehold(tier);
            books.accountFeePerHousehold(tier);
        }
        g.getHistorySave().aligned("births"); g.getHistorySave().aligned("outOfWork");
    }

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        Path root = TempDirs.create("readpath");
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
            b.addStack(b.getTemplate(HeavyIndustry.ARC_FURNACE), 1, true);
            b.addStack(b.getTemplate(Mining.IRON_MINE), 1, true);

            // ...and a landlord's insured mortgage on the books (0.7.11), by the
            // fixture's hand - a claim, no money moved - so the reads below take
            // in every figure the mortgage block and the landlords' screen show,
            // on a mortgage two years into its term.
            BusinessDebtManager lender = g.getEconomyManager().getBusinessDebtManager();
            lender.setInsuredMortgageRate(g.getBank().insuredMortgageRate(g.getDebtManager().getPolicyRate()));
            lender.issueMortgage(Sectors.REAL_ESTATE, 50_000, g.getMonth());

            // ...and the city's fund at work (0.7.14): the dial at the year's surplus and a
            // sum paid in by hand, so the reads below take in every figure the Fund pages show
            // on a fund that holds shares and bonds.
            g.setFundDial(1);
            g.fundPayIn(20_000);

            g.simulateMonths(24);
            // ...and a bond on the books (0.7.12): the builders five million
            // short, set between months, borrow it in the next - a bond where
            // the book takes one, so the reads below take in every figure the
            // bond pages, the Bank tab and the Trade tab show.
            g.getEconomyManager().setSectorCash(Sectors.CONSTRUCTION, -5_000);
            g.simulateMonths(2);
            /*
             * ...AND PLAYED ON UNTIL THE FUND HOLDS A BOND (0.7.18, re-caused).
             * The fund buys bonds only on the book, at fair value, from a
             * holder who offers one - never at an issue - so the month it
             * first holds one is whenever a holder first sells at its bid.
             * On 0.7.17 that was month 27, this fixture's last, and $4.0k of
             * bonds; in 0.7.18's city (planners that wait for staff, workers
             * that take the best-paid job) it is month 29. A history, not a
             * cause: so the city is played on, a month at a time and bounded
             * at a year, until its fund holds a bond. RE-CAUSED (revised
             * 0.7.19): with the charter branch exempt from its operating cost
             * it is month 74, past the year, so the bound is five years.
             */
            for (int more = 0; more < 60 && !(g.fundBondsValue() > 0); more++) g.simulateMonths(1);
            /*
             * ...AND IF NO HOLDER HAS SOLD IT ONE BY THEN, ONE ABROAD DOES, AT
             * THE BOND'S VALUE (0.7.120, batch I3). In 0.7.120's city - its
             * furnace on scrap at steel's world price, its mine bigger and
             * dearer - none had by the five years' end (month 87), nor by
             * month 147. So the fixture causes it as FundLedgerCheck 3 does,
             * between months: the world sells half its holding of the bond it
             * holds most of on its book, and the fund's hand buys it.
             */
            if (!(g.fundBondsValue() > 0)) {
                BondMarket bonds = g.getBondMarket();
                CorporateBond most = null;
                for (CorporateBond bond : bonds.getBonds()) if (most == null || bond.world() > most.world()) most = bond;
                if (most != null && most.world() > 0) {
                    double p = bonds.modelPrice(most, g.getMonth()), q = most.world() / 2;
                    // ...its book cleared of the month's resting orders first, as SaveFileCheck clears a share's, so the two meet.
                    if (bonds.bookOf(most) != null) bonds.bookOf(most).withdrawAll();
                    bonds.tradeForCheck(most, BondMarket.WORLD, OrderBook.Side.SELL, p, q);
                    bonds.tradeForCheck(most, BondMarket.FUND_HAND, OrderBook.Side.BUY, p, q);
                }
            }
        } finally {
            System.setOut(out);
        }
        assertTrue("fixture: the businesses owe bonds, and orders rest on their books",
                !g.getBondMarket().getBonds().isEmpty() && restingOrders(g.getBondMarket()) > 0);
        assertTrue("fixture: the city's fund holds shares and bonds",
                g.fundMarketSharesValue() > 0 && g.fundBondsValue() > 0);
        out.printf("   the fund: shares %,.2fk, bonds %,.2fk at month %d%n", g.fundMarketSharesValue(), g.fundBondsValue(), g.getMonth());

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
        exchangePrint(g, before);
        for (int i = 0; i < 50; i++) {
            readEverything(g);
        }
        Map<String, Double> after = NewGameCheck.snapshot(g);
        bankPrint(g, after);
        exchangePrint(g, after);
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
        long shelfBefore = c.getStoreInventory();
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

        // RE-CAUSED (revised 0.7.19, Jerus: "Both rebates"): the profit rate
        // alone is doubled. This doubled every income base at once, the
        // builders' sales rate with them, and since 0.7.19 the landlords'
        // repairs carry that tax and no rebate: doubled, it took their month
        // from $122k to $75k before the profit tax was struck (from $168k to
        // $129k before the rebate, which lowered their rent), and the two
        // companies' take rose 1.23 times - the landlords' profit moving, not
        // the rate arriving late. The profit base alone reaches the treasury
        // the same month or it does not, which is the question.
        System.setOut(quiet);
        econ.getTaxPolicy().setProfitTaxRate(lowRate * 2);
        g.simulateMonths(1);
        System.setOut(out);

        double highTake = c.getProfitTax() + landlords.getProfitTax();
        out.printf("   at %.0f%%: $%,.2fk    at %.0f%%: $%,.2fk%n",
                lowRate * 100, lowTake, lowRate * 200, highTake);
        assertTrue("doubling the rate moves the very next month's commercial tax",
                highTake > lowTake * 1.5);

        /* ============ the build advice's reads, after a load (0.7.51) ============ */
        out.println("\n--- the build advice reads saved state: the same after a load ---");
        afterALoad(g, files);

        onEarth(files);
        onTheWorldMarket(root);

        cleanUp(root);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** The build advice's reads (0.7.51), each a figure: what readEverything() reads, and afterALoad() compares. */
    static Map<String, Double> adviceReads(Game g) {
        Map<String, Double> r = new LinkedHashMap<>();
        BusinessInvestment bi = g.getBusinessInvestment();
        r.put("growthFactor(0)", bi.growthFactor(0));
        r.put("growthFactor(horizon)", bi.growthFactor(BusinessInvestment.PLANNING_HORIZON));
        r.put("growthFactor(cap + horizon)", bi.growthFactor(BusinessInvestment.MAX_ORDER_MONTHS + BusinessInvestment.PLANNING_HORIZON));
        r.put("officePricePerSqFt", g.getLandManager().getOfficePricePerSqFt());
        r.put("schoolLeavers", g.getEducation().schoolLeavers(g.getCohorts(), g.getLabourMarket()));
        for (EducationType t : EducationType.values()) {
            if (!t.isAdult()) continue;
            r.put(t + ".wanted", CityNeeds.wanted(g, t, 0, 1));
            r.put(t + ".wantedAhead", CityNeeds.wanted(g, t, BuildAdvice.HORIZON, 1.1));
            r.put(t + ".hires", CityNeeds.hires(g, t, 1));
            r.put(t + ".feederOver", CityNeeds.feederOver(g, t, BuildAdvice.HORIZON));
        }
        BuildAdvice.Ahead p = BuildAdvice.opening(g, Double.NaN);
        r.put("opening(NaN).k", p.k());
        r.put("opening(NaN).scale", p.scale());
        java.util.List<BuildAdvice.Suggestion> advice = BuildAdvice.suggest(g);
        int i = 0;
        for (BuildAdvice.Suggestion s : advice) {
            String k = "card" + (i++) + ".";
            r.put(k + "count", (double) s.count());
            r.put(k + "price", s.price());
            r.put(k + "afterAtOpening", s.afterAtOpening());
            r.put(k + "pricePerUnit", s.pricePerUnit());
            r.put(k + "k", s.ahead().k());
            r.put(k + "lead", s.lead());
            r.put(k + "landValue", s.landValue());
            r.put(k + "landShort", s.landShort());
            r.put(k + "runnerUpPer", s.runnerUpPer());
            r.put(k + "credit", s.credit());
        }
        r.put("run invoice", g.buildRunInvoice(BuildAdvice.run(advice)));
        return r;
    }

    /** How many of a land office's offers standing hold shallow sea (ET1). */
    static long shallowOffers(LandMarket market) {
        return market.getListing().stream().filter(p -> p.getReliefKm2(CityLand.RELIEF_SHALLOW) > 0).count();
    }

    /** A ground by relief given by hand (ET1): of `dry` km2 of dry ground, half flat, three tenths foothill, 15% steep and 5% very steep, and 0.004 km2 of shallow sea (a fiat figure: the fixture's world has no sea) - RELIEF_AREAS figures. */
    static double[] handRelief(double dry) {
        return new double[] { 0.5 * dry, 0.3 * dry, 0.15 * dry, 0.05 * dry, 0.004 };
    }

    /**
     * THE BUILD ADVICE READS SAVED STATE (0.7.51): its new reads - the
     * businesses' growth, the land office's price, the high schools'
     * leavers (not getNewDiplomas(), this month's flow, NaN after a load),
     * the higher schools' students wanted and hired, each card's projection,
     * ground and runner-up - give the same figures, to the bit, on the city
     * saved and loaded into another game as on the city that played.
     */
    static void afterALoad(Game g, GameFiles files) {
        // The fixture's buildings were stood up by fiat and took no ground; a load allocates the city's
        // footprint (Game's load path), so the city is given it first, as a load would.
        g.getLandManager().setAllocatedSqFt(g.getBuildingManager().getTotalLandFootprint());
        // ...and a dig by type (0.7.110), so the save carries E by type: the fixture's ore is by fiat, the first type's.
        double dug = g.getLandManager().extract(Resource.IRON, Deposit.FieldType.HEMATITE, 1_000.5);
        // ...and the ground by relief (ET1, spec-earth2-terrain 3.3): the fixture's ground is a generated world's, all flat, so it is given hills by
        // hand - the centre, each purchase and each offer standing foothill, steep and very steep ground and some shallow sea, as fixtures
        // are given ore (setCentre()) - for the save to carry, and the load to bring back, in the keys of their own.
        CityLand land = g.getCityLand();
        land.setCentreRelief(handRelief(land.centreKm2(CityLand.DRY)));
        for (int k = 0; k < land.purchases().size(); k++) {
            LandParcel p = land.purchases().get(k).offer();
            land.setPurchaseRelief(k, handRelief(p.getDryKm2()));
        }
        LandMarket market = g.getLandManager().getMarket();
        double[][] standing = market.getOffersState(), standingRelief = new double[standing.length][];
        for (int i = 0; i < standing.length; i++) {
            LandParcel p = market.getListing().get(i);
            standingRelief[i] = handRelief(p.getDryKm2());
        }
        market.restoreOffers(standing, standingRelief, market.getNextOfferId());
        assertTrue("fixture: the city holds hills by hand - the centre, " + land.purchases().size() + " purchase(s) and " + market.getListing().size()
                + " offers, foothill and shallow sea in them", land.centreReliefKm2(CityLand.RELIEF_FOOTHILL) > 0 && land.centreReliefKm2(CityLand.RELIEF_SHALLOW) > 0
                && !market.getListing().isEmpty() && market.getListing().stream().allMatch(p -> p.getReliefKm2(CityLand.RELIEF_SHALLOW) > 0)
                && market.getListing().stream().anyMatch(p -> p.getReliefKm2(CityLand.RELIEF_FOOTHILL) > 0));
        System.setOut(quiet);
        g.saveGame(10, "readpath");
        Game loaded = new Game(files);
        loaded.loadGameSave(10);
        System.setOut(out);
        assertTrue("fixture: the loaded city's diplomas this month are not known (getNewDiplomas() NaN after a load)",
                Double.isNaN(loaded.getEducation().getNewDiplomas()));
        Map<String, Double> played = adviceReads(g), again = adviceReads(loaded);
        int same = 0;
        for (Map.Entry<String, Double> e : played.entrySet()) {
            Double l = again.get(e.getKey());
            if (l != null && Double.doubleToLongBits(l) == Double.doubleToLongBits(e.getValue())) same++;
            else out.printf("   DIFFERS  %-28s %s -> %s%n", e.getKey(), e.getValue(), l);
        }
        out.printf("   %d of the advice's figures compared, %d cards%n", played.size(), BuildAdvice.suggest(g).size());
        assertTrue("fixture: the city has suggestions to compare", !BuildAdvice.suggest(g).isEmpty());
        assertTrue("every one of them the same after a load, to the bit", same == played.size() && again.size() == played.size());
        assertTrue("...the high schools' leavers a number, not NaN", Double.isFinite(again.get("schoolLeavers")));
        // ...and the land on the world (0.7.57), which the office's price is read from.
        assertTrue("the land on the world is the same after a load: its centre, its blocks and purchases",
                loaded.getCityLand().same(g.getCityLand()));
        assertTrue("...its offers, field for field", java.util.Arrays.deepEquals(
                loaded.getLandManager().getMarket().getOffersState(), g.getLandManager().getMarket().getOffersState()));
        assertTrue("...with the ground by relief (ET1) as given: the centre's, each purchase's and each offer's, foothill, steep, very steep and shallow sea, to the bit",
                java.util.Arrays.equals(loaded.getCityLand().centreReliefState(), g.getCityLand().centreReliefState())
                        && java.util.Arrays.deepEquals(loaded.getCityLand().holdingsReliefState(), g.getCityLand().holdingsReliefState())
                        && java.util.Arrays.deepEquals(loaded.getLandManager().getMarket().getOffersReliefState(), g.getLandManager().getMarket().getOffersReliefState())
                        && loaded.getCityLand().centreReliefKm2(CityLand.RELIEF_FOOTHILL) == g.getCityLand().centreReliefKm2(CityLand.RELIEF_FOOTHILL)
                        && shallowOffers(loaded.getLandManager().getMarket()) == shallowOffers(g.getLandManager().getMarket()) && shallowOffers(loaded.getLandManager().getMarket()) > 0);
        // ...and the same save with the three keys taken out, as every save before ET1 is, loads all flat - never refused.
        try {
            com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(Files.readString(files.saveFile(10))).getAsJsonObject();
            boolean keys = true;
            for (String k : new String[] { "landCentreRelief", "landHoldingsRelief", "landOffersRelief" }) {
                keys &= json.has(k);
                json.remove(k);
            }
            assertTrue("the save carries the three keys of the ground by relief, and they are what the load read", keys);
            Files.writeString(files.saveFile(10), new com.google.gson.Gson().toJson(json));
            Game bare = new Game(files);
            System.setOut(quiet);
            bare.loadGameSave(10);
            System.setOut(out);
            CityLand flat = bare.getCityLand();
            boolean allFlat = bare.getLoadFailure() == null && flat.centreReliefKm2(CityLand.RELIEF_FLAT) == flat.centreKm2(CityLand.DRY)
                    && flat.totalReliefKm2(CityLand.RELIEF_FLAT) == flat.totalKm2(CityLand.DRY) && !bare.getLandManager().getMarket().getListing().isEmpty();
            for (int i = CityLand.RELIEF_FOOTHILL; i < CityLand.RELIEF_AREAS; i++) allFlat &= flat.centreReliefKm2(i) == 0 && flat.totalReliefKm2(i) == 0;
            for (LandParcel p : bare.getLandManager().getMarket().getListing()) allFlat &= p.getReliefKm2(CityLand.RELIEF_FLAT) == p.getDryKm2() && p.getReliefKm2(CityLand.RELIEF_SHALLOW) == 0;
            for (CityLand.Purchase p : flat.purchases()) allFlat &= p.offer().getReliefKm2(CityLand.RELIEF_FLAT) == p.offer().getDryKm2();
            assertTrue("...a save with none of the three loads, and reads all flat: its dry ground flat in the centre, every holding and every offer,"
                    + " nothing steep and nothing shallow", allFlat);
            // ...and, since ET5, a city on one landmass writes no "landmasses" key: loaded, and loaded from the stripped save, it is the one landmass it is.
            assertTrue("a city on one landmass writes no \"landmasses\" key, and loaded it is the same one landmass: no placements, nothing unlinked, the same free ground",
                    !json.has("landmasses") && loaded.getLandManager().landmassState() == null && loaded.getLandManager().getLandmasses().single()
                            && !loaded.getLandManager().getLandmasses().anyUnlinked() && bare.getLandManager().getLandmasses().single()
                            && loaded.getLandManager().getAvailableSqFt() == g.getLandManager().getAvailableSqFt());
        } catch (java.io.IOException e) {
            assertTrue("the saved file reads: " + e, false);
        }
        assertTrue("...and what was taken out of its ground", java.util.Arrays.equals(
                loaded.getLandManager().getDepletionState(), g.getLandManager().getDepletionState()));
        assertTrue("...and out of its offshore pool (0.7.93)",
                loaded.getLandManager().getOilExtractedAtSea() == g.getLandManager().getOilExtractedAtSea());
        assertTrue("...and by type, once a works dug by type (0.7.110): the save's state and E by type, to the bit", dug == 1_000.5
                && java.util.Arrays.deepEquals(loaded.getLandManager().getDepletionByTypeState(), g.getLandManager().getDepletionByTypeState())
                && java.util.Arrays.equals(loaded.getLandManager().getExtractedByType(Resource.IRON),
                        g.getLandManager().getExtractedByType(Resource.IRON)));
    }

    /**
     * A CITY ON THE REAL EARTH (0.7.121, batch E4): reading its world - the
     * founding card of its own spot, the hover's place and province, the
     * sea's depth (the coast's distance, kept by plot), the founding's
     * problem and record, the oil at sea, the map's world - changes nothing
     * the snapshot sweeps, fifty times over. Rotterdam's window of
     * EarthCheck's fixture, a town played a year.
     */
    static void onEarth(GameFiles files) throws Exception {
        out.println("\n--- and a city on the real Earth: reading its world fifty times ---");
        byte[] fixture = EarthCheck.resource(EarthCheck.FIXTURE);
        assertTrue("fixture: EarthCheck's fixture is beside the classes", fixture != null);
        if (fixture == null) return;
        String id = EarthTerrain.use(EarthData.of(fixture));
        World.Key key = World.Key.earth(Founding.DEFAULT_WORLD_SEED, 51.90625, 4.48125, id);
        Game g = new Game(files);
        System.setOut(quiet);
        try {
            g.newGame(Founding.defaults().withWorldKey(key));
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.buildStack(template(g, "House"), 150, true);
            g.buildStack(template(g, "Convenience Store"), 3, true);
            g.buildStack(template(g, "Water Treatment Plant"), 1, true);
            // ...and, since ET5, ground across the water with a terminal on it, so that the reads of the landmasses below are not all trivial.
            World islandWorld = World.of(key);
            long[] tip = LandmassCheck.plotOf(islandWorld, 51.848, 3.998);
            LandmassCheck.Square island = LandmassCheck.squareWith(islandWorld, 648, tip[0], tip[1], 40, s -> s.pure() && s.onId() >= 2000 && s.shore() >= 20);
            if (island != null) {
                g.getLandManager().buyRectForTest(island.x0(), island.y0(), island.x0() + LandmassCheck.SQUARE, island.y0() + LandmassCheck.SQUARE, g.getMonth());
                g.choosePortLandmass(template(g, "General Cargo Terminal"), 648);
                g.buildStack(template(g, "General Cargo Terminal"), 1, true);
            }
            g.simulateMonths(12);
            g.getCityMap();
        } finally {
            System.setOut(out);
        }
        assertTrue("fixture: a town on the Earth, played a year, with ground across the water and a terminal on it that no port at home links",
                g.getWorld().isEarth() && g.getMonth() == 13 && !g.getLandManager().getLandmasses().single() && g.getLandManager().getLandmasses().anyUnlinked()
                        && g.getLandManager().landmassState() != null);
        World w = g.getWorld();
        long sx = g.getCityLand().siteX(), sy = g.getCityLand().siteY();
        long seaX = sx - 2_000;
        assertTrue("fixture: a plot of the North Sea west of it", World.salt(w.terrainAt(seaX, sy)));
        System.setOut(quiet);
        Map<String, Double> before = NewGameCheck.snapshot(g);
        bankPrint(g, before);
        for (int i = 0; i < 50; i++) {
            EarthSite.measure(key);
            EarthSite.foundingProblem(key);
            LandMap.earthWords(g.getCityLand(), sx, sy);
            w.depthAt(sx, sy); w.depthAt(seaX, sy);
            g.getFounding().problem();
            g.getFounding().getEarthLat(); g.getFounding().getEarthData();
            for (Resource r : Resource.values()) { g.getLandManager().getSites(r, true); g.getLandManager().getSites(r, false); }
            g.getLandManager().seaFields(Resource.OIL);
            OilView.of(g);
            CityMap.worldId(key);
            readEverything(g);
        }
        Map<String, Double> after = NewGameCheck.snapshot(g);
        bankPrint(g, after);
        System.setOut(out);
        int movedCount = 0;
        for (String k : before.keySet()) {
            if (Math.abs(before.get(k) - after.get(k)) > 1e-9) {
                movedCount++;
                out.printf("   MOVED  %-34s %,.4f -> %,.4f%n", k, before.get(k), after.get(k));
            }
        }
        assertTrue("reading an Earth city's world fifty times changed nothing (" + before.size() + " fields)", movedCount == 0);
    }

    /**
     * THE WORLD MARKET AND THE TRADE BY PARTNER (WN3): a city on the twelve-nation fixture world file (written beside the saves and named for the
     * length of this section), played through a January, read fifty times over through everything the screens read of the market (its bands, its
     * allocations, its mill and buyer prices, its names and nodes, its saved state) and of the trade by partner (the snapshot, its feed, the year
     * to date of every nation, the history, the map's lanes' arrays), and through the markets' own prices: the snapshot sweeps and the world's own
     * fingerprint (the solved year, the prices, the trade by partner's totals, every market's band) are what they were.
     */
    static void onTheWorldMarket(Path root) throws Exception {
        out.println("\n--- the world market and the trade by partner: reading them changes nothing (WN3) ---");
        byte[] fixture = WorldDataCheck.resource(WorldData.FIXTURE);
        assertTrue("fixture: the twelve-nation world file is beside the classes", fixture != null);
        if (fixture == null) return;
        Path file = root.resolve("world-fixture.bin");
        Files.write(file, fixture);
        String named = System.getProperty(WorldData.PROPERTY);
        try {
            System.setProperty(WorldData.PROPERTY, file.toString());
            Game g = new Game(new GameFiles(root.resolve("wm-data"), root.resolve("no-legacy")));
            System.setOut(quiet);
            try {
                g.newGame(Founding.defaults().withWorldSeed(2_026_102_8L));
                g.setCashForTest(Founding.WEALTHY_CASH);
                g.buildStack(template(g, "House"), 120, true);
                g.buildStack(template(g, "Convenience Store"), 2, true);
                g.buildStack(template(g, "Water Treatment Plant"), 1, true);
                g.simulateMonths(14);
                // WN4: the vault in euros and pounds beside its dollars, on the world's mix, and a month under it, so the vault's readers read something.
                g.buyForeignCurrency(40_000, "EUR");
                g.buyForeignCurrency(25_000, "GBP");
                g.getForeignAccounts().setReserveMix(ForeignAccounts.Mix.WORLD_MIX);
                g.buyForeignCurrency(10_000);
                g.simulateMonths(1);
            } finally {
                System.setOut(out);
            }
            WorldMarket market = g.getWorldMarket();
            assertTrue("fixture: a city on the file has a market, solved for the year it is in, and a trade by partner that has seen a year turn",
                    market != null && market.solvedYear() == CityCalendar.yearOf(g.getMonth()) && g.getPartnerTrade() != null
                            && g.partnerTradeForTest().yearsFinished() == 1 && g.getPartnerTrade().monthTotal() + g.getPartnerTrade().yearCells().size() > 0);
            if (market == null || g.getPartnerTrade() == null) return;
            System.setOut(quiet);
            Map<String, Double> before = NewGameCheck.snapshot(g);
            bankPrint(g, before);
            worldPrint(g, before);
            for (int i = 0; i < 50; i++) readTheWorld(g);
            Map<String, Double> after = NewGameCheck.snapshot(g);
            bankPrint(g, after);
            worldPrint(g, after);
            System.setOut(out);
            int movedCount = 0;
            for (String k : before.keySet()) {
                if (!sameFigure(before.get(k), after.get(k))) {
                    movedCount++;
                    out.printf("   MOVED  %-34s %,.4f -> %,.4f%n", k, before.get(k), after.get(k));
                }
            }
            assertTrue("reading the world market and the trade by partner fifty times changed nothing (" + before.size() + " fields, "
                    + before.keySet().stream().filter(k -> k.startsWith("world.")).count() + " of them the world's own)", movedCount == 0);
        } finally {
            System.setOut(out);
            if (named == null) System.clearProperty(WorldData.PROPERTY); else System.setProperty(WorldData.PROPERTY, named);
        }
    }

    /** Two readings of one figure agree: equal, or both not a number. */
    static boolean sameFigure(double a, double b) {
        return Double.compare(a, b) == 0 || Math.abs(a - b) <= 1e-9;
    }

    /** Everything a screen reads of the market, the trade by partner and the band the markets stand at. */
    static void readTheWorld(Game g) {
        WorldMarket m = g.getWorldMarket();
        m.solvedYear(); m.lastSolveMillis(); m.host(); m.berth(); m.nationCount();
        m.savedPrices(); m.savedScales();
        for (int nation = 1; nation <= m.nationCount(); nation++) {
            m.iso3(nation); m.name(nation); m.nodeLat(nation); m.nodeLon(nation);
        }
        for (Good good : Good.values()) {
            m.trades(good); m.passes(good);
            m.band(good, 1_000, 2_000); m.band(good, 0, 0); m.allocate(good, 500, 500);
            for (int nation = 1; nation <= m.nationCount(); nation++) { m.millPrice(good, nation); m.buyerPrice(good, nation); }
            GoodsMarket gm = g.getMarkets().get(good);
            gm.importPrice(); gm.exportPrice(); gm.floor(); gm.ceiling(); gm.bandByLorry(); gm.worldMargin(); gm.hasWorldPrices();
        }
        PartnerTrade.Snapshot s = g.getPartnerTrade();
        s.monthTotal(); s.nations(); s.history(); s.yearCells(); s.exportsByNation(); s.importsByNation();
        for (int nation = 1; nation <= s.nations(); nation++) { s.yearToDate(nation); s.nameOf(nation); }
        // WN4: everything a screen reads of the world's moneys (the nation card's rate, the dollar's chart's series) and of the D$'s world.
        WorldMoneys moneys = g.getWorldMoneys();
        if (moneys != null) {
            moneys.month(); moneys.ruling(); moneys.cycle(); moneys.riskOff(); moneys.riskOffStarted(); moneys.breaksThisMonth(); moneys.codes();
            for (String code : moneys.codes()) {
                moneys.rate(code); moneys.shadow(code); moneys.ppp(code); moneys.swing(code); moneys.totReached(code); moneys.pegHeld(code);
                moneys.pegAnchor(code); moneys.pegGap(code); moneys.kind(code); moneys.ruled(code);
            }
            moneys.cross("EUR", "JPY");
            for (int nation = 1; nation <= m.nationCount(); nation++) { moneys.rateOfNation(nation); moneys.commodityShare(nation); }
            g.getHistorySave().aligned(HistorySave.WORLD_CYCLE);
            g.getHistorySave().aligned(HistorySave.worldRateKey("EUR"));
        }
        // XR4: everything a screen reads of the eight central banks' policy rates, the history's series of them and the city's price of money.
        PolicyRates policy = g.getPolicyRates();
        if (policy != null) {
            policy.month(); policy.ruleStart(); policy.ruling(); policy.usRate(); policy.toSaveArray();
            for (String code : WorldMoneys.EIGHT) {
                policy.rate(code); policy.shadow(code); policy.target(code); policy.inflation(code);
                g.getHistorySave().aligned(HistorySave.policyRateKey(code));
            }
            for (int k = 0; k < WorldMoneys.EIGHT.size(); k++) policy.rate(k);
        }
        g.getDebtManager().worldRate();
        // WN4, the vault in eight: everything the Reserves page reads of it.
        ForeignAccounts vault = g.getForeignAccounts();
        vault.getReserveMix(); vault.getChosenShares(); vault.getTradeShares(); vault.mixShares(); vault.getLastVaultCrossUsd(); vault.getLastVaultCrossDriver();
        // ...and what the vault earned (XR4): the month's and since founding, in US$ and at home, the money that earned most, the yield
        vault.getVaultInterestUsd(); vault.getVaultInterestUsdLifetime(); vault.getVaultInterest(); vault.getVaultInterestDriver(); vault.getVaultInterestDriverUsd(); vault.getVaultYield();
        for (int k = 0; k < ForeignAccounts.MONEYS; k++) {
            vault.getHolding(k); vault.getHoldingUsd(k); vault.getMoneyUsd(k); vault.hasRate(k);
            vault.sellableReserves(WorldMoneys.EIGHT.get(k));
        }
        TradeLanes.Feed feed = g.getTradeFeed();
        feed.month(); feed.exportsByNation(); feed.importsByNation(); feed.standInLat(); feed.standInLon(); feed.yearToDate(1); feed.nameOf(1);
        g.partnerTradeForTest().toState();
        g.partnerTradeForTest().history();
    }

    /** The exchange's fingerprint (EM2): each company's quote and last trade, the world's short of it and the collateral, who lends it and the month's fees, what the short paid at the dividends; the short's life. Not-a-number is -1. */
    static void exchangePrint(Game g, Map<String, Double> into) {
        Exchange x = g.getExchange();
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            String k = "exchange." + Equity.COMPANIES[c] + ".";
            into.put(k + "quote", x.price(c));
            into.put(k + "last", finiteOrMinusOne(x.lastTrade(c)));
            into.put(k + "short", x.shortShares(c));
            into.put(k + "collateral", x.shortCollateral(c));
            into.put(k + "lent", x.lentByCells(c) + 3 * x.lentByFund(c));
            into.put(k + "fees", x.lendingFeeToCells(c) + 3 * x.lendingFeeToFund(c));
            into.put(k + "shortPays", g.getEquity().getLifetimeShortPays(c) + 3 * g.getEquity().getShortPaysThisMonth(c));
        }
        into.put("exchange.life.short", x.getLifeShortSold() + 3 * x.getLifeShortSoldValue() + 5 * x.getLifeShortCovered() + 7 * x.getLifeShortCoveredValue());
        into.put("exchange.life.counts", x.getLifeShortAsks() + 3.0 * x.getLifeShortMonths() + 5.0 * x.getLifeMarginCalls() + 7.0 * x.getLifeRecalls());
        into.put("exchange.life.fees", x.getLifeLendingFees());
    }

    /** The world's own fingerprint: the solved year, the prices and scales, the trade by partner's totals, every market's band. Not-a-number is -1. */
    static void worldPrint(Game g, Map<String, Double> into) {
        WorldMarket m = g.getWorldMarket();
        into.put("world.solvedYear", (double) m.solvedYear());
        double[] prices = m.savedPrices(), scales = m.savedScales();
        double weighted = 0, plain = 0;
        for (int i = 0; i < prices.length; i++) { weighted += (i + 1) * prices[i]; plain += prices[i]; }
        into.put("world.pricesSum", plain);
        into.put("world.pricesWeighted", weighted);
        double sc = 0;
        for (int i = 0; i < scales.length; i++) sc += (i + 1) * scales[i];
        into.put("world.scalesWeighted", sc);
        // WN4: the world's moneys (their whole saved state, weighted) and what the D$ carries of them (slots 95-104 and the readers the card uses).
        WorldMoneys moneys = g.getWorldMoneys();
        if (moneys != null) {
            double[] state = moneys.toSaveArray();
            double stateSum = 0;
            for (int i = 0; i < state.length; i++) stateSum += (i + 1) * state[i];
            into.put("world.moneys.month", (double) moneys.month());
            into.put("world.moneys.state", stateSum);
            into.put("world.moneys.cycle", moneys.cycle());
            into.put("world.moneys.riskOff", moneys.riskOff());
        }
        // XR4: the policy rates (their whole saved state, weighted), the US's and the price of money the city pays by.
        PolicyRates policy = g.getPolicyRates();
        if (policy != null) {
            double[] state = policy.toSaveArray();
            double stateSum = 0;
            for (int i = 0; i < state.length; i++) stateSum += (i + 1) * state[i];
            into.put("world.policy.month", (double) policy.month());
            into.put("world.policy.state", stateSum);
            into.put("world.policy.us", policy.usRate());
        }
        into.put("world.policy.worldRate", g.getDebtManager().worldRate());
        ForeignAccounts fx = g.getForeignAccounts();
        double[] fxSlots = fx.toSaveArray();
        double worldSlots = 0;
        for (int i = 95; i < fxSlots.length && i <= 104; i++) worldSlots += (i - 94) * fxSlots[i];
        into.put("world.fx.slots", worldSlots);
        // WN4, the vault in eight: its slots (105-141) and the readers the Reserves page uses.
        double vaultSlots = 0;
        for (int i = 105; i < fxSlots.length && i <= 141; i++) vaultSlots += (i - 104) * fxSlots[i];
        into.put("world.fx.vaultSlots", vaultSlots);
        into.put("world.fx.vaultSlotCount", (double) fxSlots.length);
        // XR4, what the vault earned (slots 142-146) and what ran home in a risk-off (CapitalFlows' slots 19-23): the readers the Reserves page and the Policy tab use.
        into.put("world.fx.interest", fx.getVaultInterestUsd() + 3 * fx.getVaultInterestUsdLifetime() + 5 * fx.getVaultInterestDriver() + 7 * fx.getVaultInterestDriverUsd()
                + 11 * fx.getVaultYield() + 13 * fx.getVaultInterest());
        CapitalFlows hotRun = g.getCapitalFlows();
        into.put("world.hot.riskOff", hotRun.getRiskOffDeparted() + 3 * hotRun.getRiskOffDepartedLifetime() + 5 * hotRun.getCarryRiskRepaid()
                + 7 * hotRun.getCarryRiskRepaidLifetime() + 11 * hotRun.getRiskOffMonths());
        double held = 0, heldUsd = 0, rates = 0;
        for (int k = 0; k < ForeignAccounts.MONEYS; k++) {
            held += (k + 1) * fx.getHolding(k);
            heldUsd += (k + 1) * fx.getHoldingUsd(k);
            rates += (k + 1) * (fx.hasRate(k) ? fx.getMoneyUsd(k) : -1);
        }
        into.put("world.fx.holdings", held);
        into.put("world.fx.holdingsUsd", heldUsd);
        into.put("world.fx.moneyRates", rates);
        into.put("world.fx.vault", fx.getReservesUsd());
        into.put("world.fx.mix", (double) fx.getReserveMix().ordinal());
        into.put("world.fx.cross", fx.getLastVaultCrossUsd());
        into.put("world.fx.crossDriver", fx.getLastVaultCrossDriver() == null ? -1 : fx.getLastVaultCrossDriver().index() + fx.getLastVaultCrossDriver().move());
        double shares = 0;
        for (int k = 0; k < ForeignAccounts.MONEYS; k++) shares += (k + 1) * (fx.mixShares()[k] + 3 * fx.getChosenShares()[k] + 5 * fx.getTradeShares()[k]);
        into.put("world.fx.shares", shares);
        into.put("world.fx.moveWorld", fx.getMoveWorld());
        into.put("world.fx.level", fx.getWorldLevel());
        into.put("world.fx.terms", fx.getTermsOfTradeReached());
        into.put("world.fx.loading", fx.riskOffLoading());
        PartnerTrade.Snapshot s = g.getPartnerTrade();
        double ex = 0, im = 0, yex = 0, yim = 0, tn = 0;
        for (int i = 0; i < s.nations(); i++) {
            ex += (i + 1) * s.exportsByNation()[i]; im += (i + 1) * s.importsByNation()[i];
            yex += (i + 1) * s.yearExportValue()[i]; yim += (i + 1) * s.yearImportValue()[i];
            tn += (i + 1) * (s.exportTonnes()[i] + s.importTonnes()[i] + s.yearExportTonnes()[i] + s.yearImportTonnes()[i]);
        }
        into.put("world.trade.month", (double) s.month());
        into.put("world.trade.exports", ex);
        into.put("world.trade.imports", im);
        into.put("world.trade.yearExports", yex);
        into.put("world.trade.yearImports", yim);
        into.put("world.trade.tonnes", tn);
        into.put("world.trade.noPartner", s.noPartnerExports() + 3 * s.noPartnerImports());
        into.put("world.trade.cells", (double) s.yearCells().size());
        into.put("world.trade.history", (double) s.history().size());
        double cellSum = 0;
        for (PartnerTrade.Cell c : s.yearCells()) cellSum += c.nation() * (c.unitsOut() + 2 * c.unitsIn() + 3 * c.valueOut() + 5 * c.valueIn());
        into.put("world.trade.cellSum", cellSum);
        for (Good good : Good.values()) {
            GoodsMarket gm = g.getMarkets().get(good);
            into.put("world.band." + good + ".import", finiteOrMinusOne(gm.importPrice()));
            into.put("world.band." + good + ".export", finiteOrMinusOne(gm.exportPrice()));
        }
    }

    private static double finiteOrMinusOne(double v) { return Double.isFinite(v) ? v : -1; }

    static void cleanUp(Path root) {
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }
    }
}

package ham.citybuildersim;

import ham.citybuildersim.sectors.WoodProducts;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * The hands-off bank's failures, caused separately and counted across Random cities.
 * WHY. BF1 follows LD2's sawmill loans: a warehouse with no buyers, an empty
 * borrower still owing its plant, growth credit after losses, and an apparent
 * disappearance of shares which must be distinguished from a consolidation.
 * MC2 (section 9): BF1's room to the target is none under it, so a bank in
 * its band lent no growth; under the target the growth limit's share now
 * is the room, never past the room to the minimum; over it the room to the
 * target, and none while a default takes it under before the settle.
 *
 * FIXTURES (FS9). Every section but 6 is a fixture built for its one cause (the mill, the closing, the retired oil, the credit,
 * the band, the shares, the rounding, the dust). Section 6 is the ensemble, kept PLAYED for its breadth: 24 default Random
 * cities of 120 months, in which what is asserted is an identity - the money audit closes every month at its own tolerance and
 * the bank's equity movement is explained by its books - and the months they sold pellets, the failures and the splits are
 * printed, not asserted: that a pellet mill sells is caused in section 1, and a played city's count of it is no premise.
 * WHY: the played cities moved under the premise (a count of pellet sales read off them), and the identities do not need it.
 */
public final class BankFailureCheck {
    static final PrintStream out = System.out;
    static final PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());
    static int fails;

    static void check(String label, boolean ok) {
        out.println(label + (ok ? " OK" : " FAIL"));
        if (!ok) fails++;
    }

    static void close(String label, double a, double b) {
        check(label, Math.abs(a - b) <= MoneyAudit.tolerance(0, Math.max(Math.abs(a), Math.abs(b))));
    }

    static Game town(String label) {
        Game g = new Game(GameFiles.scratch(label));
        g.newGame(Founding.defaults().withPath(Nations.Path.RANDOM, 4));
        g.setRescueMode(TreasuryFund.RescueMode.BUTTON);
        return g;
    }

    static void mill() {
        out.println("--- 1. a lightly staffed mill exports to its buyers instead of filling an unwanted shed ---");
        Game g = town("bankfailure-mill");
        BuildingManager buildings = g.getBuildingManager();
        Markets markets = g.getMarkets();
        Sectors sectors = g.getSectors();
        WoodProducts wood = sectors.woodProducts();
        BuildingsTemplate mill = buildings.getTemplate(WoodProducts.PELLET_MILL);
        buildings.addStack(mill, 1, true);
        double[] fill = new double[11];
        Arrays.fill(fill, .2);
        wood.updateJobFillRate(fill);
        wood.updateWages(g.getPopulationManager().getWagesPerType(), wood.postsPerTier());
        check("fixture: no city demand, export price pays the inputs, and output fits below the dump line",
                markets.get(Good.WOOD_PELLETS).getDemand() == 0
                && wood.getOperatingRate() > 0 && wood.getOperatingRate() < Sector.DUMP_THRESHOLD
                && wood.getCapacity(Good.WOOD_PELLETS) * wood.getOperatingRate()
                        < wood.getStockCapacity(Good.WOOD_PELLETS) * Sector.DUMP_THRESHOLD
                && markets.get(Good.WOOD_PELLETS).netExportPrice() > wood.getMarginalCostPerUnit(Good.WOOD_PELLETS));
        markets.clearMonth(sectors, null);
        double opening = wood.getStock(Good.WOOD_PELLETS);
        check("fixture: the mill made a positive first month's stock below the dump line", opening > 0
                && opening < wood.getStockCapacity(Good.WOOD_PELLETS) * Sector.DUMP_THRESHOLD
                && wood.getPayroll() > 0 && wood.pending().imports > 0);
        markets.clearMonth(sectors, null);
        check("the pellet mill sells its stock in the following month", wood.output(Good.WOOD_PELLETS).exported > 0);
        close("with no local buyers the world buys the opening stock", wood.output(Good.WOOD_PELLETS).exported, opening);
        close("the market counts the mill's shipment", wood.output(Good.WOOD_PELLETS).exported,
                markets.get(Good.WOOD_PELLETS).getExported());
        close("the seller books the world's invoice", wood.pending().exports,
                opening * markets.get(Good.WOOD_PELLETS).exportPrice());
    }

    static void closing() throws Exception {
        out.println("--- 2. the last mill closes through the borrower's debt books ---");
        Game g = town("bankfailure-closing");
        BuildingManager buildings = g.getBuildingManager();
        BuildingsTemplate mill = buildings.getTemplate(WoodProducts.PELLET_MILL);
        WoodProducts wood = g.getSectors().woodProducts();
        buildings.addStack(mill, 1, true);
        double doomed = wood.getStockCapacity(Good.WOOD_PELLETS) * Sector.DUMP_THRESHOLD;
        wood.setStock(Good.WOOD_PELLETS, doomed);
        BusinessDebtManager credit = g.getEconomyManager().getBusinessDebtManager();
        double principal = buildings.getBuildingsValueBySector(wood.key());
        credit.issueProjectLoan(wood.key(), principal, g.getMonth());
        wood.setCash(0);
        g.getEconomyManager().pushBalanceSheetInputs();
        Method investor = Game.class.getDeclaredMethod("sectorInvestor", String.class);
        investor.setAccessible(true);
        Method retire = Game.class.getDeclaredMethod("retire", BusinessInvestment.Decision.class, Investor.class, boolean.class);
        retire.setAccessible(true);
        long gone = (long) retire.invoke(g, new BusinessInvestment.Decision(wood.key(), mill, 1, "BF1 fixture", true),
                investor.invoke(g, wood.key()), true);
        g.getEconomyManager().settleInsolvency();
        close("the demolished shed's stock is written off before creditors value it", wood.unitsWrittenOff(Good.WOOD_PELLETS), doomed);
        check("fixture: the only mill was retired and its creditor suffered a loss", gone == 1
                && buildings.getBuildingsValueBySector(wood.key()) == 0 && credit.getWrittenOffThisMonth(wood.key()) > 0);
        close("cash from closing repays the bank before its remaining claim is resolved", credit.getPrincipal(wood.key()), 0);
        check("the empty defaulter serves the model's borrowing exclusion", credit.getBlockedMonths(wood.key())
                >= BusinessDebtManager.exclusionFor(1));
        check("it cannot borrow for a replacement mill", !credit.canFundProject(wood.key(), principal));

        BusinessDebtManager carrying = new BusinessDebtManager();
        carrying.setLoans(new java.util.ArrayList<>(java.util.List.of(
                new InterimLoan(wood.key(), 40, BusinessDebtManager.LOAN_TERM_MONTHS, 1, .04),
                new BusinessLoan(wood.key(), 100, BusinessDebtManager.LOAN_TERM_MONTHS, 1, .04))));
        close("partial retirement pays the interim claim before ordinary loans", carrying.settleRetirement(wood.key(), 60, false), 60);
        close("the senior interim claim was paid whole", carrying.getInterimPrincipal(wood.key()), 0);
        close("the remaining plant carries the unpaid ordinary claim", carrying.getPrincipal(wood.key()), 80);
        close("repayment reaches the bank's cash settlement", carrying.getRepaidThisMonth(wood.key()), 60);
        close("repayment is recorded separately from creditor losses", carrying.getWrittenOffThisMonth(wood.key()), 0);
        check("a partial retirement alone does not ban a continuing borrower", !carrying.isBorrowingBlocked(wood.key()));
    }

    static void refineryClosing() throws Exception {
        refineryClosing(false);
        refineryClosing(true);
    }

    static void refineryClosing(boolean maturesWhilePending) throws Exception {
        out.println("--- 7. retained oil is recoverable before a closing haircut ---");
        GameFiles files = GameFiles.scratch("bankfailure-refinery-close");
        Game g = new Game(files);
        g.newGame(Founding.defaults().withPath(Nations.Path.RANDOM, 4));
        g.setRescueMode(TreasuryFund.RescueMode.BUTTON);
        g.simulateMonths(2);
        g.setCashForTest(0);
        var refinery = g.getSectors().refining();
        var plant = g.getBuildingManager().getTemplateByName("Oil Refinery");
        g.getBuildingManager().addStack(plant, 1, true);
        double litres = refinery.getStockCapacity(Good.DIESEL);
        refinery.setStock(Good.DIESEL, litres);
        double proceeds = litres * g.getMarkets().get(Good.DIESEL).netExportPrice();
        var credit = g.getEconomyManager().getBusinessDebtManager();
        double face = (refinery.getInventoryValue() * BusinessDebtManager.RESTRUCTURE_TARGET + proceeds) / 2;
        credit.issueProjectLoan(refinery.key(), face, g.getMonth());
        BusinessDebt claim = credit.getLoans(refinery.key()).get(0);
        if (maturesWhilePending) {
            claim.remainingMonths = 1;
            claim.monthStarted = g.getMonth() - claim.duration + 1;
            check("fixture: the bullet matures in the invoice's pending month",
                    claim.getMaturityMonth() == g.getMonth() + 1 && claim.getRemainingMonths() == 1);
        }
        refinery.setCash(0);
        g.getEconomyManager().setSectorCash(Sectors.CONSTRUCTION, 0);
        g.getEconomyManager().pushBalanceSheetInputs();
        Method investor = Game.class.getDeclaredMethod("sectorInvestor", String.class);
        investor.setAccessible(true);
        Method retire = Game.class.getDeclaredMethod("retire", BusinessInvestment.Decision.class, Investor.class, boolean.class);
        retire.setAccessible(true);
        retire.invoke(g, new BusinessInvestment.Decision(refinery.key(), plant, 1, "BF1 oil fixture", true),
                investor.invoke(g, refinery.key()), true);
        g.getEconomyManager().pushBalanceSheetInputs();
        g.getEconomyManager().refreshCreditAssets();
        double owed = credit.getPrincipal(refinery.key());
        check("fixture: retained oil covers the loan but the ordinary asset haircut would not",
                refinery.getStock(Good.DIESEL) > 0 && owed > credit.getAssets(refinery.key()) * BusinessDebtManager.RESTRUCTURE_TARGET
                && proceeds >= owed);
        g.getEconomyManager().settleInsolvency();
        close("closing keeps a claim fully recoverable from retained oil", credit.getPrincipal(refinery.key()), owed);
        close("recoverable oil causes no creditor loss at closure", credit.getWrittenOffThisMonth(refinery.key()), 0);
        // Keep the waiting interest funded; the retained oil must repay principal.
        refinery.setCash(claim.getMonthlyInterestExpense() * 2);
        g.saveGame(10, "BF1 retired oil");
        Game loaded = new Game(files);
        loaded.loadGameSave(10);
        BusinessDebtManager restoredCredit = loaded.getEconomyManager().getBusinessDebtManager();
        BusinessDebt restored = restoredCredit.getLoans(refinery.key()).get(0);
        check("a city save and reload retain the retirement repayment obligation", restored.isRetirementClaim());
        loaded.simulateMonths(1);
        if (maturesWhilePending) check("the reloaded maturing claim stays marked until liquidation cash lands",
                restored.isMatured() && restoredCredit.getLoans(refinery.key()).contains(restored));
        check("the reloaded liquidation invoice covers the carried claim before cash is banked",
                loaded.getSectors().refining().retirementRecovery() >= restored.getOutstandingPrincipal());
        close("the reloaded claim takes no haircut while the invoice waits", restoredCredit.getWrittenOffTotal(refinery.key()), 0);
        loaded.simulateMonths(1);
        close("the reloaded retired bullet is repaid whole when the sale cash lands", restored.getOutstandingPrincipal(), 0);
        close("the reloaded claim never takes a later haircut", restoredCredit.getWrittenOffTotal(refinery.key()), 0);
        g.simulateMonths(1);
        if (maturesWhilePending) check("a maturing retired claim stays on its liquidation path without refinancing",
                claim.isMatured() && credit.getLoans(refinery.key()).contains(claim)
                    && credit.getLentThisMonth(refinery.key()) == 0);
        check("the liquidation invoice covers the carried claim before cash is banked",
                refinery.retirementRecovery() >= claim.getOutstandingPrincipal());
        close("the live claim takes no haircut while the invoice waits", credit.getWrittenOffTotal(refinery.key()), 0);
        g.simulateMonths(1);
        close("banked oil proceeds repay the retired bullet in full", claim.getOutstandingPrincipal(), 0);
        close("no later haircut substitutes for that retirement repayment", credit.getWrittenOffTotal(refinery.key()), 0);
        close("the regular liquidation line still empties the retired tanks", refinery.getStock(Good.DIESEL), 0);

    }

    static void writtenOffRetirement() {
        out.println("--- 8. a written-off retired bullet leaves the loan book at maturity ---");
        String sector = Sectors.REFINING;
        BusinessDebtManager credit = new BusinessDebtManager();
        BusinessLoan claim = new BusinessLoan(sector, 100, 1, 1, .04);
        credit.setLoans(new java.util.ArrayList<>(java.util.List.of(claim)));
        credit.settleRetirement(sector, 0, true);
        claim.writeDown(0);
        check("fixture: the marked claim has no principal or interest and no cash to sweep it",
                claim.isRetirementClaim() && claim.getOutstandingPrincipal() == 0
                    && claim.getMonthlyInterestExpense() == 0 && claim.getRemainingMonths() == 1);
        credit.processMonth();
        check("a written-off retired claim leaves the loan list at maturity", !credit.getLoans().contains(claim));
        check("Loans running no longer counts the written-off retired claim", credit.getLoanCount(sector) == 0);
    }

    static void credit() throws Exception {
        out.println("--- 3. a new project cannot spend capital the bank does not have ---");
        Game g = town("bankfailure-capital");
        g.simulateMonths(2);
        BusinessDebtManager credit = g.getEconomyManager().getBusinessDebtManager();
        String sector = Sectors.WOOD_PRODUCTS;
        credit.setAssets(sector, g.getBank().equity());
        credit.setCash(sector, 0);
        Method investor = Game.class.getDeclaredMethod("sectorInvestor", String.class);
        investor.setAccessible(true);
        Investor borrower = (Investor) investor.invoke(g, sector);
        double request = Math.max(g.getBank().headroom(), g.getBank().equity()) / Bank.CAPITAL_RATIO;
        check("fixture: the bank stands above target and the requested project exceeds its capital headroom",
                !g.getBank().isInsolvent() && Double.isInfinite(credit.getCapitalGrowth())
                && request > g.getBank().headroom());
        check("new project credit is refused at the bank's own capital limit", !borrower.canBorrow(request));

        double room = g.getBank().projectHeadroom();
        BusinessDebtManager shared = new BusinessDebtManager();
        shared.setProjectBudget(room);
        shared.setAssets(Sectors.WOOD_PRODUCTS, request);
        shared.setAssets(Sectors.MANUFACTURING, request);
        check("fixture: an affordable project is still lent", room > 0
                && shared.canFundProject(Sectors.WOOD_PRODUCTS, room * .75));
        shared.issueProjectLoan(Sectors.WOOD_PRODUCTS, room * .75, 1);
        close("one sector's commitment consumes the shared room", shared.projectRoom(), room * .25);
        check("another sector cannot spend the same capital again", !shared.canFundProject(Sectors.MANUFACTURING, room * .5));
        Game bonds = town("bankfailure-bond-capital");
        bonds.simulateMonths(2);
        BusinessDebtManager claims = bonds.getEconomyManager().getBusinessDebtManager();
        Bank bank = bonds.getBank();
        double deposits = 1e6 * PriceLevel.FACTOR;
        bank.setForeignDeposits(0);
        bank.refresh(1, deposits, 0, deposits, 0, 0);
        bank.setCash(deposits * .5);
        claims.setProjectBudget(bank.projectHeadroom());
        double before = claims.projectRoom();
        claims.setAssets(sector, request * 100);
        BondMarket market = bonds.getBondMarket();
        double face = before * .75, book = market.faceHeldByBank();
        market.issue(new BusinessDebtManager.Plan(sector, face, face, 1, 1,
                0, 1, 1, market.issueCost(face), 1, request * 100, face, face), bonds.getMonth());
        double bought = market.faceHeldByBank() - book;
        check("fixture: the bank actually buys the corporate issue", before > 0 && bought > 0);
        out.printf("bond allowance before %.17g bought %.17g room %.17g headroom %.17g%n", before, bought, claims.projectRoom(), bank.projectHeadroom());
        double remaining = bank.projectHeadroom()
                - Math.max(0, claims.getLentThisMonth() - claims.getRepaidThisMonth());
        check("corporate bonds and pending loans share the bank's remaining capital",
                claims.projectRoom() <= Math.max(0, remaining)
                    + MoneyAudit.tolerance(0, before));
        check("a later project cannot reuse the capital spent on corporate bonds",
                !claims.canFundProject(Sectors.MANUFACTURING, (before + Math.max(0, remaining)) / 2));
        double afterBond = claims.projectRoom();
        claims.setAssets(Sectors.MANUFACTURING, before * 100);
        claims.issueProjectLoan(Sectors.MANUFACTURING, afterBond * .9, bonds.getMonth());
        double forBonds = claims.projectRoom(), held = market.faceHeldByBank();
        market.issue(new BusinessDebtManager.Plan(sector, face, face, 1, 1,
                0, 1, 1, market.issueCost(face), 1, request * 100, face, face), bonds.getMonth());
        double laterBuy = market.faceHeldByBank() - held;
        check("fixture: a pending loan leaves positive but smaller room for a second bond",
                forBonds > 0 && forBonds < afterBond && laterBuy > 0);
        check("a later bond purchase cannot reuse a pending loan's capital",
                laterBuy <= forBonds + MoneyAudit.tolerance(0, before));
        Bank settledBank = new Bank();
        settledBank.refresh(1, deposits, 0, deposits, 0, 0);
        settledBank.setCash(deposits * .5);
        EconomyManager settledEconomy = new EconomyManager(bonds.getBuildingManager());
        BusinessDebtManager settling = settledEconomy.getBusinessDebtManager();
        settling.setAssets(Sectors.MANUFACTURING, deposits * 100);
        settling.setProjectBudget(settledBank);
        double openingRoom = settling.projectRoom(), loan = openingRoom * .25;
        settling.issueProjectLoan(Sectors.MANUFACTURING, loan, 1);
        settledBank.lend(loan);
        settledBank.refresh(1, deposits, 0, deposits + loan, 0, 0);
        double beforeSettle = settling.projectRoom();
        settling.projectLoansSettled();
        double settledRoom = settling.projectRoom();
        check("fixture: the settled loan leaves positive capital room on the bank's books",
                loan > 0 && settledBank.projectHeadroom() > 0);
        check("settlement does not subtract the same loans from the live book twice",
                settledRoom > beforeSettle && settledRoom <= settledBank.projectHeadroom()
                    + MoneyAudit.tolerance(0, openingRoom));
        settling.setAssets(sector, deposits * 1000);
        BondMarket settledBonds = new BondMarket();
        settledBonds.attach(new BondMarket.Readings() {
            public int month() { return 1; }
            public double curve(int months) { return .04; }
            public double policyRate() { return .04; }
            public double depositRate() { return .02; }
            public double worldRate() { return .04; }
            public double countryPremium() { return 0; }
            public double monthlyGdp() { return 0; }
            public double localPerUsd() { return 1; }
            public boolean worldRunning() { return false; }
            public double unit() { return 1; }
        }, null, settledBank, settledEconomy, null);
        double faceAfter = .9 * Math.min(openingRoom * .75, settledBank.projectHeadroom());
        settledBonds.issue(new BusinessDebtManager.Plan(sector, faceAfter, faceAfter, 1, 1,
                0, 1, 1, settledBonds.issueCost(faceAfter), 1, deposits * 1000, faceAfter, faceAfter), 1);
        close("a bond can use capital left after loan settlement without a second loan deduction",
                settledBonds.faceHeldByBank(), faceAfter);
        double afterPurchase = settling.projectRoom();
        close("new debt after settlement still consumes room once",
                issueAfterSettlement(settling, afterPurchase, 1), afterPurchase * .75);
        g.getBank().writeOffSector(sector, g.getBank().equity() + g.getBank().getSectorAllowance(sector));
        close("uncovered defaults reach the growth desk before the bank's month-end settle", g.getBank().projectHeadroom(), 0);

        BusinessDebtManager insured = new BusinessDebtManager();
        insured.setAssets(Sectors.REAL_ESTATE, request);
        insured.setProjectBudget(0);
        insured.setCapitalRule(0, true, false);
        double shortfall = request * Mortgage.MORTGAGE_MAX_LOAN_TO_COST / 2;
        check("fixture: insured mortgages carry the model's zero risk weight", Bank.RISK_INSURED_MORTGAGE == 0);
        check("the uninsured project budget does not ration a zero-risk insured mortgage",
                insured.canFundMortgage(Sectors.REAL_ESTATE, shortfall, request));
        Bank leverage = new Bank();
        leverage.refresh(1, deposits, 0, 0, 0, 0);
        leverage.setCash(deposits);
        BusinessDebtManager above = new BusinessDebtManager();
        above.setAssets(Sectors.REAL_ESTATE, deposits * 1000);
        above.setProjectBudget(leverage);
        above.setCapitalRule(leverage.lendingGrowthLimit(), leverage.lendsOnlyToKeepBorrowersGoing(), leverage.leverageBinds());
        double leverageRoom = leverage.equity() / leverage.leverageTarget() - leverage.exposure();
        check("fixture: leverage binds above target while the old growth rule is infinite",
                leverage.leverageBinds() && leverage.equity() > leverage.targetEquity()
                    && Double.isInfinite(above.getCapitalGrowth()) && leverageRoom > 0);
        check("an insured mortgage cannot exceed finite leverage room above target",
                !above.canFundMortgage(Sectors.REAL_ESTATE, leverageRoom * 2, deposits * 1000)
                    && BusinessDebtManager.MORTGAGE_CAPITAL.equals(above.getMortgageRefusal()));
        above.setProjectBudget(0);
        check("a zero-risk insured mortgage still uses available leverage room despite zero uninsured room",
                above.canFundMortgage(Sectors.REAL_ESTATE, leverageRoom * .25, deposits * 1000));
        insured.setCapitalRule(0, true, true);
        check("the leverage rule still rations that same insured mortgage",
                !insured.canFundMortgage(Sectors.REAL_ESTATE, shortfall, request)
                && BusinessDebtManager.MORTGAGE_CAPITAL.equals(insured.getMortgageRefusal()));
    }

    /* ----- 9. THE BAND'S SHARE UNDER THE LEVERAGE TARGET (MC2, 2026-11-20) -----
       BF1 bounded growth by the room to the bank's target, which is none
       anywhere under it: a bank in its band, between its minimum and its
       target, wrote no building's loan and, while its leverage ratio binds, no
       mortgage, though its growth limit allows each borrower a share. Banks by
       hand at the minimum, halfway down the band, just under the target and
       just over it; a landlord owing a mortgage and a works owing a loan.
       REV-MC2 added a default that takes a bank over its target under it
       (none until the settle, its bond bid too), the cap at the room to the
       minimum near the target, and a bank whose risk weights bind. */

    /** What the landlords and the works owe before the month, what each owns, and the hand-built banks' exposure. */
    static final double BAND_OWED = 1_000_000, BAND_OWNED = 100_000_000, BAND_EXPOSURE = 10_000_000;

    /**
     * A bank of one branch whose exposure is its cash, funded from abroad:
     * nothing weighs, so its leverage requirement is the larger, and its
     * leverage ratio stands x of the way from LEVERAGE_RATIO_MIN to its
     * leverage target (over it past 1), held by its restore setters.
     */
    static Bank leverageBankAt(double x, double exposure) {
        Bank bank = new Bank();
        bank.refresh(1, 0, 0, 0, 0, 0);
        bank.setCash(exposure);
        double ratio = Bank.LEVERAGE_RATIO_MIN + x * (bank.leverageTarget() - Bank.LEVERAGE_RATIO_MIN);
        bank.setForeignDeposits(exposure * (1 - ratio));
        return bank;
    }

    /** The landlords owing an insured mortgage and the works a loan, under this bank's capital rule for the month, as Game hands it to them. */
    static BusinessDebtManager deskOn(Bank bank) {
        BusinessDebtManager desk = new BusinessDebtManager();
        desk.setInsuredMortgageRate(.04);
        desk.setAssets(Sectors.REAL_ESTATE, BAND_OWNED);
        desk.setAssets(Sectors.MANUFACTURING, BAND_OWNED);
        desk.issueMortgage(Sectors.REAL_ESTATE, BAND_OWED, 0);
        desk.issueLoan(Sectors.MANUFACTURING, BAND_OWED, 0);
        desk.startAuditMonth();
        desk.setCapitalRule(bank.lendingGrowthLimit(), bank.lendsOnlyToKeepBorrowersGoing(), bank.leverageBinds());
        desk.setProjectBudget(bank);
        return desk;
    }

    /** The mortgage door's answer for the landlords on this shortfall, on a building costing ten times it. */
    static boolean mortgage(BusinessDebtManager desk, double shortfall) {
        return desk.canFundMortgage(Sectors.REAL_ESTATE, shortfall, shortfall * 10);
    }

    static boolean near(double a, double b) {
        return Math.abs(a - b) <= MoneyAudit.tolerance(Math.max(Math.abs(a), Math.abs(b)));
    }

    static void band() {
        out.println("--- 9. a bank in its leverage band lends each borrower the band's share; over its target, the room to it (MC2) ---");
        String re = Sectors.REAL_ESTATE, works = Sectors.MANUFACTURING;
        Bank floor = leverageBankAt(0, BAND_EXPOSURE), half = leverageBankAt(.5, BAND_EXPOSURE),
                under = leverageBankAt(.9, BAND_EXPOSURE), over = leverageBankAt(1.1, BAND_EXPOSURE);
        out.printf("   leverage ratio at the minimum %.6f, halfway %.6f, under %.6f, over %.6f; target %.6f; growth limit halfway %.6f, under %.6f%n",
                floor.leverageRatio(), half.leverageRatio(), under.leverageRatio(), over.leverageRatio(),
                under.leverageTarget(), half.lendingGrowthLimit(), under.lendingGrowthLimit());
        boolean binds = floor.leverageBinds() && half.leverageBinds() && under.leverageBinds() && over.leverageBinds();
        check("fixture: four banks whose leverage ratio binds, at the minimum, halfway down the band, just under the target and just over it",
                binds && near(floor.lendingGrowthLimit() * floor.exposure(), 0)
                && half.leverageRatio() < half.leverageTarget() && under.leverageRatio() < under.leverageTarget()
                && under.leverageRatio() > half.leverageRatio() && over.leverageRatio() > over.leverageTarget()
                && half.lendingGrowthLimit() > 0 && under.lendingGrowthLimit() > half.lendingGrowthLimit()
                && Double.isInfinite(over.lendingGrowthLimit()));

        double share = under.lendingGrowthLimit() * under.exposure();
        check("the band's share rises from none at the minimum to RATIONED_GROWTH of the exposure halfway down the band",
                near(floor.leverageHeadroom(), 0) && near(floor.projectHeadroom(), 0)
                && near(half.leverageHeadroom(), Bank.RATIONED_GROWTH * half.exposure()));
        check("just under the target the insured room is the growth limit's share of the exposure, more than none",
                share > 0 && near(under.leverageHeadroom(), share) && near(under.projectLeverageHeadroom(), share));
        check("...and every building's loan reads the same share, before the book settles and after",
                near(under.projectHeadroom(), share) && near(under.settledProjectHeadroom(), share));

        BusinessDebtManager underDesk = deskOn(under);
        double landlordShare = underDesk.capitalRoom(re), owedBefore = underDesk.getPrincipal(re);
        check("fixture: the leverage rule rations the landlords, and its growth limit allows their debt a share smaller than the bank's",
                underDesk.isInsuredRationed() && !underDesk.isKeepGoingOnly()
                && near(landlordShare, under.lendingGrowthLimit() * owedBefore) && landlordShare < share);
        double asked = landlordShare * .25;
        boolean yes = mortgage(underDesk, asked);
        Mortgage written = yes ? underDesk.issueMortgage(re, asked, 1) : null;
        check("just under the target the door writes a mortgage within the landlords' share of the band",
                written != null && underDesk.getPrincipal(re) > owedBefore);
        double left = underDesk.capitalRoom(re);
        check("...what it wrote comes off their share, and one past what is left is refused for the bank's capital",
                written != null && near(left, landlordShare - (underDesk.getPrincipal(re) - owedBefore))
                && !mortgage(underDesk, left * 2)
                && BusinessDebtManager.MORTGAGE_CAPITAL.equals(underDesk.getMortgageRefusal()));
        BusinessDebtManager halfDesk = deskOn(half);
        check("halfway down the band the landlords' share is RATIONED_GROWTH of their debt, and a mortgage within it is written",
                near(halfDesk.capitalRoom(re), Bank.RATIONED_GROWTH * halfDesk.getPrincipal(re))
                && mortgage(halfDesk, halfDesk.capitalRoom(re) * .25));

        BusinessDebtManager building = deskOn(under);
        double worksShare = building.capitalRoom(works);
        check("just under the target a building's loan within the works' share is lent, and one past it refused",
                worksShare > 0 && building.canFundProject(works, worksShare * .5)
                && !building.canFundProject(works, worksShare * 2) && building.wasRefusedForCapital(works));

        BusinessDebtManager sharing = deskOn(under);
        sharing.setAssets(Sectors.WOOD_PRODUCTS, BAND_OWNED);
        sharing.issueProjectLoan(Sectors.WOOD_PRODUCTS, share * .95, 1);
        double rest = share * .05;
        check("the band's share is one allowance: another sector's loan this month leaves the rest of it",
                near(sharing.projectRoom(), rest));
        double between = (rest + sharing.capitalRoom(re)) / 2;
        check("...so a mortgage within the landlords' own share but past that rest is refused, and one within the rest written",
                sharing.capitalRoom(re) > rest && !mortgage(sharing, between)
                && BusinessDebtManager.MORTGAGE_CAPITAL.equals(sharing.getMortgageRefusal())
                && mortgage(sharing, rest * .5));

        Bank hit = leverageBankAt(.9, BAND_EXPOSURE);
        double loss = hit.equity() * .02;
        hit.writeOffSector(re, loss);
        Bank poorer = leverageBankAt(.9, BAND_EXPOSURE);
        poorer.setForeignDeposits(BAND_EXPOSURE - (poorer.equity() - loss));
        double shareLeft = poorer.lendingGrowthLimit() * poorer.exposure();
        check("fixture: the month's default is the bank's loss before its book settles, and leaves it in its band",
                near(hit.equity() - loss, poorer.equity()) && shareLeft > 0 && shareLeft < share);
        check("the month's known losses come off the band's share before the book settles: the share on the equity they leave",
                near(hit.projectLeverageHeadroom(), shareLeft) && near(hit.projectHeadroom(), shareLeft)
                && near(hit.leverageHeadroom(), share));

        BusinessDebtManager overDesk = deskOn(over), sideDesk = deskOn(under);
        double toTarget = over.equity() / over.leverageTarget() - over.exposure();
        out.printf("   just over the target the room to it %.2f; just under, the landlords' share %.2f of the bank's %.2f%n",
                toTarget, sideDesk.capitalRoom(re), share);
        check("on both sides of the target the door writes what the rule allows and refuses past it: the room to the target just over it (BF1's bound, kept), the band's share just under it",
                toTarget > 0 && near(over.leverageHeadroom(), toTarget) && Double.isInfinite(overDesk.getCapitalGrowth())
                && mortgage(overDesk, toTarget * .25) && !mortgage(overDesk, toTarget * 2)
                && BusinessDebtManager.MORTGAGE_CAPITAL.equals(overDesk.getMortgageRefusal())
                && mortgage(sideDesk, sideDesk.capitalRoom(re) * .25) && !mortgage(sideDesk, sideDesk.capitalRoom(re) * 2));

        // REV-MC2 P1: a bank over its target when the month's rule is struck, its
        // growth limit none, that the month's default takes just under the target
        // before its book settles: BF1's room, none, never the band's share.
        Bank within = leverageBankAt(1.1, BAND_EXPOSURE), past = leverageBankAt(1.1, BAND_EXPOSURE);
        double cushion = within.equity() - within.targetEquity();
        BusinessDebtManager pastDesk = deskOn(past);
        within.writeOffSector(re, cushion * .5);
        past.writeOffSector(re, cushion * 1.05);
        pastDesk.setProjectBudget(past);
        check("fixture: two banks over their target and lending freely, the month's default leaving one over it and taking the other just under it",
                cushion > 0 && Double.isInfinite(within.lendingGrowthLimit()) && Double.isInfinite(past.lendingGrowthLimit())
                && Double.isInfinite(pastDesk.getCapitalGrowth())
                && within.equity() - cushion * .5 > within.targetEquity() && past.equity() - cushion * 1.05 < past.targetEquity());
        check("a default never raises the room: one within the bank's cushion leaves the room to the target, one past it none, insured and not",
                within.projectHeadroom() > 0 && within.projectLeverageHeadroom() > 0
                && near(past.projectHeadroom(), 0) && near(past.projectLeverageHeadroom(), 0));
        check("...so a bank the default takes under its target writes no building's loan and no mortgage before its book settles",
                !pastDesk.canFundProject(works, cushion * .1) && !mortgage(pastDesk, cushion * .1)
                && BusinessDebtManager.MORTGAGE_CAPITAL.equals(pastDesk.getMortgageRefusal()));

        // REV-MC2 P3: ...and its bond bid, which reads the same room (BondMarket.bankClaimRoom()).
        double face = within.projectHeadroom() * .25;
        double boughtWithin = bankBuysOf(within, face), boughtPast = bankBuysOf(past, face);
        check("fixture: the bank its default leaves over the target buys part of a works' issue",
                boughtWithin > 0);
        check("...and the bank its default takes under the target bids nothing for the same issue before its book settles",
                near(boughtPast, 0));

        // REV-MC2 P2: near the target the curve rises without bound; the share stops at the minimum.
        Bank edge = leverageBankAt(.99, BAND_EXPOSURE);
        double toMinimum = edge.equity() / Bank.LEVERAGE_RATIO_MIN - edge.exposure();
        double room = edge.leverageHeadroom();
        // Lent a margin either side of the room, never on its edge: the minimum's test is a strict <.
        Bank lent = lentFrom(edge, room * (1 - BAND_EDGE)), overLent = lentFrom(edge, room * (1 + BAND_EDGE));
        out.printf("   at x = .99 the growth limit %.4f a month, %.0f of the exposure; the room to the minimum %.0f; lent, the leverage ratio %.6f%n",
                edge.lendingGrowthLimit(), edge.lendingGrowthLimit() * edge.exposure(), toMinimum, lent.leverageRatio());
        out.printf("   lent the room less %.0e of it, the leverage ratio %.9f; %.0e past it, %.9f; the minimum %.9f%n",
                BAND_EDGE, lent.leverageRatio(), BAND_EDGE, overLent.leverageRatio(), Bank.LEVERAGE_RATIO_MIN);
        check("at x = .99, where the growth limit's share would pass the bank's minimum, the room is the room to the minimum, insured and not",
                edge.lendingGrowthLimit() * edge.exposure() > toMinimum && toMinimum > 0
                && near(room, toMinimum) && near(edge.projectHeadroom(), toMinimum));
        check("...so the bank lent all but a hair of it stands over its minimum, the next month not one of keeping borrowers going, and a hair past it would stand under",
                near(lent.equity(), edge.equity()) && near(overLent.equity(), edge.equity()) && !lent.isInsolvent()
                && lent.leverageRatio() > Bank.LEVERAGE_RATIO_MIN && !lent.lendsOnlyToKeepBorrowersGoing()
                && overLent.leverageRatio() < Bank.LEVERAGE_RATIO_MIN && overLent.lendsOnlyToKeepBorrowersGoing());

        // REV-MC2 P4: a bank whose risk weights bind reads the band on the weighted book.
        Bank risk = riskBankAt(.5, BAND_EXPOSURE);
        BusinessDebtManager riskDesk = deskOn(risk);
        double riskShare = risk.lendingGrowthLimit() * risk.getWeightedBook();
        check("fixture: a bank whose risk weights bind, halfway down its band on them and over its leverage target",
                !risk.leverageBinds() && risk.getWeightedBook() > 0
                && risk.capitalRatio() < risk.capitalTarget() && risk.leverageRatio() > risk.leverageTarget()
                && near(risk.lendingGrowthLimit(), Bank.RATIONED_GROWTH) && !riskDesk.isInsuredRationed());
        check("its uninsured room is RATIONED_GROWTH of its weighted book, and a works' loan within the works' share is lent",
                near(risk.projectHeadroom(), riskShare) && near(risk.settledProjectHeadroom(), riskShare)
                && riskDesk.canFundProject(works, riskDesk.capitalRoom(works) * .5)
                && !riskDesk.canFundProject(works, riskDesk.capitalRoom(works) * 2));
    }

    /** The margin, a share of the room, by which section 9 lends either side of the room to the minimum. */
    static final double BAND_EDGE = 1e-6;

    /** A bank with this one's equity and its exposure grown by what it lent, by its restore setters. */
    static Bank lentFrom(Bank bank, double lent) {
        Bank after = new Bank();
        after.refresh(1, 0, 0, 0, 0, 0);
        after.setCash(bank.exposure() + lent);
        after.setForeignDeposits(bank.exposure() + lent - bank.equity());
        return after;
    }

    /**
     * A bank whose whole exposure is a business book weighing in full, funded
     * from abroad: its risk weights bind, and its capital ratio stands x of
     * the way from CAPITAL_RATIO to its capital target.
     */
    static Bank riskBankAt(double x, double book) {
        Bank bank = new Bank();
        bank.refresh(1, 0, 0, book, 0, 0);
        bank.setCash(0);
        double ratio = Bank.CAPITAL_RATIO + x * (bank.capitalTarget() - Bank.CAPITAL_RATIO);
        bank.setForeignDeposits(book * (1 - ratio));
        return bank;
    }

    /** What this bank buys of a works' issue of this face, its desk's allowance struck on the bank as Game strikes it after the month's defaults. */
    static double bankBuysOf(Bank bank, double face) {
        EconomyManager economy = new EconomyManager(new BuildingManager());
        BusinessDebtManager desk = economy.getBusinessDebtManager();
        desk.setAssets(Sectors.MANUFACTURING, BAND_OWNED);
        desk.setProjectBudget(bank);
        BondMarket bonds = new BondMarket();
        bonds.attach(new BondMarket.Readings() {
            public int month() { return 1; }
            public double curve(int months) { return .04; }
            public double policyRate() { return .04; }
            public double depositRate() { return .02; }
            public double worldRate() { return .04; }
            public double countryPremium() { return 0; }
            public double monthlyGdp() { return 0; }
            public double localPerUsd() { return 1; }
            public boolean worldRunning() { return false; }
            public double unit() { return 1; }
        }, null, bank, economy, null);
        bonds.issue(new BusinessDebtManager.Plan(Sectors.MANUFACTURING, face, face, 1, 1,
                0, 1, 1, bonds.issueCost(face), 1, BAND_OWNED, face, face), 1);
        return bonds.faceHeldByBank();
    }

    static double issueAfterSettlement(BusinessDebtManager claims, double room, int month) {
        claims.issueProjectLoan(Sectors.MANUFACTURING, room * .25, month);
        return claims.projectRoom();
    }

    static void shares() {
        out.println("--- 4. a consolidation changes the unit, preserving every holder's value ---");
        for (String key : new String[] { Sectors.WOOD_PRODUCTS, Sectors.MANUFACTURING }) {
            int c = Equity.indexOf(key);
            Equity register = new Equity();
            register.offer(c, 400, 0, null, .02);
            double issued = register.getShares(c);
            register.moveForeign(c, -issued * .35);
            register.moveDesk(c, issued * .35);
            Exchange exchange = new Exchange();
            ExchangeCheck.Firms firms = new ExchangeCheck.Firms();
            double quote = register.foundingPrice();
            firms.wage = quote / (Exchange.CONSOLIDATE_BELOW / 100);
            exchange.attach(register, null, null, firms, 1);
            exchange.bookOf(c).seedLastPrice(quote);
            double world = register.getForeignShares(c) * exchange.price(c);
            double desk = register.getDealerShares(c) * exchange.mark(c);
            double paidIn = register.getPaidIn(c);
            exchange.splitForCheck();
            double k = exchange.getSplit(c);
            check(key + ": fixture actually consolidates under SX1's wage dial", k > 0 && k < 1);
            close(key + ": shares in founding units are conserved", register.getShares(c) / k, issued);
            close(key + ": the desk's value is conserved", register.getDealerShares(c) * exchange.mark(c), desk);
            close(key + ": the world's value is conserved", register.getForeignShares(c) * exchange.price(c), world);
            close(key + ": the register remains whole", register.getForeignShares(c) + register.getDealerShares(c), register.getShares(c));
            close(key + ": paid-in capital is money, not a share count", register.getPaidIn(c), paidIn);
            close(key + ": nothing was bought back", register.getBoughtBackThisMonth(c), 0);
        }
    }

    static void rounding() {
        out.println("--- 5. a repayment's rounding does not draw a loss buffer ---");
        for (boolean rounding : new boolean[] { true, false }) {
            Game g = town("bankfailure-short-" + rounding);
            g.simulateMonths(2);
            Sector s = g.getSectors().retail();
            BusinessDebtManager credit = g.getEconomyManager().getBusinessDebtManager();
            double principal = g.getBank().equity() / 10;
            double gap = rounding ? principal - Math.nextDown(principal) : principal * 1e-6;
            credit.setLoans(new java.util.ArrayList<>(java.util.List.of(
                    new BusinessLoan(s.key(), principal, 1, g.getMonth() - 1, .04))));
            credit.startAuditMonth();
            credit.setAssets(s.key(), principal * 100);
            credit.setLendingOpen(true);
            g.getOutwardInvestment().recall(s.key(), principal * 1000, g.getEconomyManager());
            g.getBondMarket().sellForCompany(s.key(), principal * 1000, credit.getRate(s.key()));
            check("fixture: no liquid foreign assets or bonds can cover the shortfall",
                    g.getEconomyManager().getForeignAssets(s.key()) == 0 && g.getEconomyManager().getBondAssets(s.key()) == 0);
            s.setCash(principal - gap);
            s.statement().netIncome = -principal / 10;
            s.statement().preTaxIncome = -principal / 10;
            check("fixture: a shortfall would draw a positive loss buffer", s.getNetIncome() < 0);
            check("fixture: " + (rounding ? "rounding" : "real shortfall") + " is on its intended side of the model's roundoff band",
                    gap > 0 && (gap <= MoneyAudit.tolerance(0, principal)) == rounding);
            g.getEconomyManager().settleBusinessCredit(g.getMonth());
            if (rounding) {
                close("rounding borrows no loss buffer", credit.getLentThisMonth(s.key()), 0);
                close("the repaid borrower's till is empty", s.getCash(), 0);
            } else {
                check("a real shortfall still draws the working-capital line and its loss buffer", credit.getLentThisMonth(s.key())
                        >= -s.getNetIncome() * BusinessDebtManager.BUFFER_MONTHS);
            }
        }
    }

    static void bondDust() throws Exception {
        out.println("--- 5b. a bond's face floor is money at the founding price level ---");
        Method open = BondMarket.class.getDeclaredMethod("openBook", CorporateBond.class);
        open.setAccessible(true);
        for (double unit : new double[] { 1, 100 }) {
            BondMarket market = new BondMarket();
            market.seedConstants(unit);
            CorporateBond bond = new CorporateBond(1, Sectors.CONSTRUCTION, 1, .04, 0, 120);
            OrderBook book = (OrderBook) open.invoke(market, bond);
            double floor = PriceLevel.atFounding(OrderBook.DUST) / unit;
            book.submit("seller", OrderBook.Side.SELL, 1, floor * 2, 0, null);
            check("a face above the same real dust trades at unit " + unit,
                    book.submit("buyer", OrderBook.Side.BUY, 1, floor * 2, 0, null).size() == 1);
            book.submit("seller", OrderBook.Side.SELL, 1, floor / 2, 0, null);
            check("a face below the same real dust is ignored at unit " + unit, book.asks().isEmpty());
        }
    }

    static void seeds() {
        out.println("--- 6. 24 default Random cities, button unpressed, every one of 120 months audited ---");
        int failed = 0, audited = 0, badAudits = 0, badEquity = 0, selling = 0, splits = 0;
        for (int seed = 0; seed < 24; seed++) {
            Game g = new Game(GameFiles.scratch("bankfailure-seed-" + seed));
            g.newGame(Founding.defaults().withPath(Nations.Path.RANDOM, seed));
            g.setRescueMode(TreasuryFund.RescueMode.BUTTON);
            int failedAt = -1;
            for (int m = 0; m < 120; m++) {
                g.toggleNextMonth();
                MoneyAudit.Result a = g.getLastMoneyAudit();
                audited++;
                if (a == null || !Double.isFinite(a.residual) || Math.abs(a.residual) > MoneyAudit.tolerance(a.moved())) {
                    badAudits++;
                    out.printf("audit seed %d month %d: %s%n", seed, g.getMonth(), a);
                }
                if (g.getBank().isInResolution() && failedAt < 0) failedAt = g.getMonth();
                Bank.EquityMovement movement = g.getBank().equityMovement();
                if (g.getBank().isMonthKnown() && (!Double.isFinite(movement.residual()) || !Double.isFinite(movement.scale())
                        || Math.abs(movement.residual()) > MoneyAudit.tolerance(movement.scale()))) {
                    badEquity++;
                    out.printf("equity seed %d month %d residual %.8g%n", seed, g.getMonth(), movement.residual());
                }
                if (g.getSectors().woodProducts().output(Good.WOOD_PELLETS).exported > 0) selling++;
                for (int c = 0; c < Equity.COMPANIES.length; c++) if (g.getExchange().getSplit(c) > 0) splits++;
            }
            if (failedAt >= 0) failed++;
            out.printf("seed %d failed %d equity %.3f%n", seed, failedAt, g.getBank().equity());
        }
        out.printf("BANK FAILURES %d/24; audits %d; pellet sale months %d; splits/consolidations %d%n", failed, audited, selling, splits);
        check("MoneyAudit closes in every month of every seed at its own tolerance", audited == 24 * 120 && badAudits == 0);
        // Failure frequency is the observation BF1 asks for, not a promise
        // that a bank taking genuine credit losses can never fail.
        check("every bank's equity movement is explained by its books, including failures", badEquity == 0);
        // FS9: that a pellet mill sells is caused in mill() (section 1: "the pellet mill sells its stock in the following month"), so it is
        // no premise read off these played cities: their months of pellet sales are in the line above, printed, and asserted of nothing.
    }

    public static void main(String[] args) throws Exception {
        System.setOut(quiet);
        try {
            if (!Arrays.asList(args).contains("--count")) { mill(); closing(); refineryClosing(); writtenOffRetirement(); credit(); band(); shares(); rounding(); bondDust(); }
            if (!Arrays.asList(args).contains("--fixtures")) seeds();
        } finally { System.setOut(out); }
        out.println(fails == 0 ? "All checks passed." : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}

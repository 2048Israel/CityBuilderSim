package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;

/**
 * Household average-cost stocks and investment taxes, through the real fills, census and city (HT1).
 * WHY: the fund already tracked average cost, but a household sale previously lost its acquisition cost.
 */
public final class CapitalGainsCheck {
    static int fails;
    static PrintStream out = System.out;
    static final PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());

    static void check(String label, boolean ok) {
        out.println(label + (ok ? " OK" : " FAIL"));
        if (!ok) fails++;
    }

    static void close(String label, double got, double want) {
        boolean ok = Math.abs(got - want) <= MoneyAudit.tolerance(1e-9, Math.max(Math.abs(got), Math.abs(want)));
        check(label + " (" + got + " / " + want + ")", ok);
    }

    static void quietly(Runnable action) {
        PrintStream before = System.out;
        System.setOut(quiet);
        try { action.run(); } finally { System.setOut(before); }
    }

    static final class Market {
        final HouseholdFlowCheck.Town town = new HouseholdFlowCheck.Town();
        final HouseholdBalance balance = town.bal;
        final Household holder = town.cell(FamilyStructure.SINGLE_ADULT, PayTier.ELITE, 1);
        final TaxPolicy policy = new TaxPolicy();
        final Equity equity = ExchangeCheck.equity();
        final Exchange exchange = ExchangeCheck.exchange();
        final Bank bank = ExchangeCheck.bankWith(0);
        final ExchangeCheck.Firms firms = new ExchangeCheck.Firms();
        final int company = 0;
        double received;

        Market() {
            equity.listIfUnlisted(company, 10_000, balance);
            ExchangeCheck.addPaid(equity, company, 100, 10);
            double[] books = ExchangeCheck.bookOf(company, 10_000);
            firms.wage = 1;
            quietly(() -> ExchangeCheck.step(exchange, equity, balance, bank, firms, books,
                    DebtManager.WORLD_BASE_RATE, 0, 1));
            exchange.bookOf(company).withdrawAll();
            // All issued shares offered by the world: the fixture buys rather than assumes its holding.
            double domestic = balance.sharesHeld(company);
            equity.moveForeign(company, domestic);
            for (Household c : balance.cells()) { c.shares[company] = 0; c.basis[company] = 0; }
            holder.savings = 20_000;
            balance.setInvestmentTaxes(policy, exchange::price, (line, amount) -> received += amount);
        }

        void trade(boolean buy, double price, double shares) {
            exchange.bookOf(company).withdrawAll();
            String cell = Exchange.CELL + holder.key();
            exchange.tradeForCheck(company, buy ? Exchange.WORLD : cell, OrderBook.Side.SELL, price, shares);
            double filled = FundLedgerCheck.filled(exchange.tradeForCheck(company, buy ? cell : Exchange.WORLD,
                    OrderBook.Side.BUY, price, shares));
            close("the real order book fills " + shares + " shares", filled, shares);
        }
    }

    static void costAndLoss() {
        out.println("--- average-cost purchases, partial and full sales, loss carry ---");
        Market m = new Market();
        double before = m.holder.savings;
        m.trade(true, 2, 100);
        m.trade(true, 4, 100);
        close("two purchases add the actual 600 paid to basis", m.holder.basis[0], 600);
        close("the buyer has paid exactly its basis", before - m.holder.savings, 600);
        double rate = m.policy.getCapitalGainsInclusion() * m.policy.effectiveWageRate(WageBand.UNIVERSITY);
        m.trade(false, 5, 50);
        close("a partial sale leaves 450 of average cost", m.holder.basis[0], 450);
        close("the 100 realised gain reaches the treasury", m.received, 100 * rate);
        close("cash held plus tax is the actual purchase and sale money", m.holder.savings + m.received, before - 600 + 250);
        m.trade(false, 1, 100);
        close("the month's loss refunds its earlier gain tax", m.received, 0);
        close("only the net loss carries", m.holder.lossCarried, 100);
        m.holder.taxedGains = 0; // a new assessment month, as advanceMonth clears it
        m.trade(false, 6, 50);
        close("the final sale releases all remaining basis", m.holder.basis[0], 0);
        close("the loss offsets the later 150 gain, leaving 50 taxable", m.received, 50 * rate);
        close("the carried loss is consumed", m.holder.lossCarried, 0);
        close("a full sale leaves no share", m.holder.shares[0], 0);
        close("all proceeds and actual taxes conserve the money", m.holder.savings + m.received, before - 600 + 250 + 100 + 300);
    }

    static void lossesFirst() {
        out.println("--- a loss before the gain in the same month ---");
        Market m = new Market();
        m.trade(true, 4, 100);
        m.trade(false, 2, 50);
        close("the first sale carries its actual loss", m.holder.lossCarried, 100);
        m.trade(false, 6, 50);
        close("the later gain uses the earlier loss", m.holder.lossCarried, 0);
        close("fill order leaves the month's net gain untaxed", m.received, 0);
    }

    static void foundersSplitsAndDividends() {
        out.println("--- founders, offerings, splits and integration ---");
        Market m = new Market();
        m.trade(true, 2, 100);
        m.trade(false, 1.5 * Exchange.SPLIT_AT * m.firms.wage, 1);
        double cost = m.holder.basis[0], units = m.holder.shares[0], quote = m.exchange.price(0);
        m.exchange.splitForCheck();
        double factor = m.exchange.getSplit(0);
        check("the actual quote crosses the model's split trigger", quote > Exchange.SPLIT_AT * m.firms.wage && factor > 1
                && m.exchange.price(0) <= Exchange.SPLIT_AT * m.firms.wage);
        close("a share split leaves total basis standing", m.holder.basis[0], cost);
        close("a share split changes the units", m.holder.shares[0], units * factor);
        double before = m.holder.investmentIncome;
        double gross = m.balance.creditDividend(0, .1);
        close("default integration transfers no dividend tax", m.balance.getDividendTax(), 0);
        close("investment income receives the whole default dividend", m.holder.investmentIncome - before, gross);
        m.policy.setWageOffset(WageBand.UNIVERSITY, .15);
        before = m.holder.investmentIncome;
        gross = m.balance.creditDividend(0, .1);
        double tax = gross * 0.17647058823529413; // accepted 30% wage / 15% profit integration example
        close("the raised band actually pays integrated dividend tax", m.balance.getDividendTax(), tax);
        close("the raised-band investment income is net", m.holder.investmentIncome - before, gross - tax);
        m.policy.setProfitOffset(Sectors.RETAIL, .10);
        m.holder.shares[Equity.BANK] = 10;
        double taxBefore = m.balance.getDividendTax();
        m.balance.creditDividend(Equity.BANK, 1);
        close("the bank integrates the Retail profit rate it actually pays", m.balance.getDividendTax() - taxBefore, 2.0 / 3);
        m.policy.setCapitalGainsInclusion(2);
        close("the inclusion dial stops at the whole gain", m.policy.getCapitalGainsInclusion(), 1);
        m.policy.setCapitalGainsInclusion(-1);
        close("the inclusion dial stops at zero", m.policy.getCapitalGainsInclusion(), 0);
        m.policy.setCapitalGainsInclusion(.75);
        close("a staged policy copy keeps the inclusion", m.policy.copy().getCapitalGainsInclusion(), .75);
        HouseholdBalance b = new HouseholdBalance();
        Household h = b.cell(FamilyStructure.SINGLE_ADULT, PayTier.SKILLED);
        h.households = 2;
        Equity e = new Equity();
        e.listIfUnlisted(0, 100, b);
        close("founders receive their actual founding book as cost", h.basis[0] * h.households, 100);
        h.savings = 50;
        h.disposable = 0;
        double paid = b.subscribe(0, 20, e.foundingPrice());
        close("offerings add only the actual paid amount to basis", h.basis[0] * h.households, 100 + paid);
    }

    static void waterfall() {
        out.println("--- the spending waterfall receives proceeds after the actual tax ---");
        Market m = new Market();
        m.trade(true, 2, 100);
        m.exchange.bookOf(0).withdrawAll();
        double bid = Math.max(5, m.exchange.fair(0) * 2);
        m.exchange.tradeForCheck(0, Exchange.WORLD, OrderBook.Side.BUY, bid, 100);
        double held = m.holder.shares[0], saved = m.holder.savings, cost = m.holder.basis[0];
        double raised = m.exchange.sellForHousehold(m.equity, m.bank, m.balance, m.holder, 50);
        double sold = held - m.holder.shares[0];
        check("the waterfall actually sells shares and pays tax", sold > 0 && m.balance.getCapitalGainsTax() > 0);
        close("its released cost has the purchased average", cost - m.holder.basis[0], 2 * sold);
        close("net spending, surplus banked and tax equal buyer payment", raised + m.holder.savings - saved + m.received, bid * sold);
    }

    static void stockFlows() {
        out.println("--- census and stage stocks ---");
        HouseholdFlowCheck.Town t = new HouseholdFlowCheck.Town();
        Household a = t.youngCell(FamilyStructure.SINGLE_ADULT, PayTier.SKILLED, 100);
        a.shares[0] = 10; a.basis[0] = 20; a.lossCarried = 3;
        t.bal.setFlows(0, 0, 0, 0, 0, 0, 0, 0, .25);
        t.bal.censusOnly((HouseholdBalance.Census) (shape, tier, stage) ->
                shape == FamilyStructure.SINGLE_ADULT && tier == PayTier.SKILLED
                        ? stage == AdultStage.YOUNG ? 75 : 25 : 0);
        Household established = t.bal.cell(FamilyStructure.SINGLE_ADULT, PayTier.SKILLED);
        close("stage ageing carries the original average basis", established.basis[0], 20);
        close("stage ageing carries the original loss stock", established.lossCarried, 3);
        close("stage ageing conserves basis", HouseholdFlowCheck.across(t.bal, c -> c.basis[0]), 2000);
        // A separate pool move has an exact same-tier destination.
        HouseholdFlowCheck.Town pool = new HouseholdFlowCheck.Town();
        Household from = pool.cell(FamilyStructure.SINGLE_ADULT, PayTier.SKILLED, 100);
        from.shares[0] = 10; from.basis[0] = 20; from.lossCarried = 3;
        Household to = pool.cell(FamilyStructure.COUPLE, PayTier.SKILLED, 0);
        pool.expect(FamilyStructure.SINGLE_ADULT, PayTier.SKILLED, 80).expect(FamilyStructure.COUPLE, PayTier.SKILLED, 10);
        pool.run();
        close("the census conserves shares", HouseholdFlowCheck.across(pool.bal, c -> c.shares[0]), 1000);
        close("basis follows the shares through the pool", HouseholdFlowCheck.across(pool.bal, c -> c.basis[0]), 2000);
        close("loss carry is a census stock", HouseholdFlowCheck.across(pool.bal, c -> c.lossCarried), 300);
        close("the receiving cell takes the average basis", to.basis[0], 40);
    }

    static void liveCity() {
        out.println("--- real months: actual assessments and both money identities ---");
        Game g = LongPlaytest.probeCity(GameFiles.scratch("ht1-live"), null);
        long gains = 0, dividends = 0;
        double tax = 0;
        boolean pools = true, households = true, budget = true;
        for (int month = 0; month < 120; month++) {
            quietly(g::toggleNextMonth);
            HouseholdBalance b = g.getHouseholdBalance();
            gains += b.getGainAssessments(); dividends += b.getDividendAssessments();
            tax += b.getCapitalGainsTax();
            pools &= Math.abs(g.getLastMoneyAudit().residual) <= MoneyAudit.tolerance(MoneyAudit.CENT, g.getLastMoneyAudit().moved());
            households &= Math.abs(g.getLastHouseholdsAudit().residual) <= MoneyAudit.tolerance(MoneyAudit.CENT, g.getLastHouseholdsAudit().moved());
            budget &= Math.abs(g.getTreasuryResidual()) <= MoneyAudit.CENT;
        }
        check("the live city actually assesses sales", gains > 0);
        check("the live city actually assesses dividends", dividends > 0);
        check("the default city actually transfers gain tax", tax > 0);
        check("every played month's pool audit holds", pools);
        check("every played month's household identity holds", households);
        check("the budget and journal account for the actual transfers", budget);
        out.println("Observed: " + gains + " gain assessments, " + dividends + " dividend assessments, tax " + tax);
        g.awaitSaves();
    }

    /** Nonzero stocks, inclusion and struck flows through both actual Game load routes, in scratch slot 10 only. */
    static boolean saveChecks() {
        int before = fails;
        GameFiles files = GameFiles.scratch("ht1-save");
        Game g = LongPlaytest.probeCity(files, null);
        HouseholdBalance b = g.getHouseholdBalance();
        Household h = b.cells().stream().filter(c -> !c.isEmpty()).findFirst().orElseThrow();
        h.basis[0] = 123.5; h.lossCarried = 17.25; h.taxedGains = 9.5;
        g.getEconomyManager().getTaxPolicy().setCapitalGainsInclusion(.75);
        b.restoreInvestmentTaxes(new double[] {12.5, 3.25, 7, 4});
        g.getEconomyManager().getNationalAccounts().setInvestmentTaxes(12.5, 3.25);
        double[] cells = b.toCellSaveArray();
        g.getHistorySave().recordMonth(g);
        double[] gainsHistory = g.getHistorySave().raw("capitalGainsTax");
        double[] dividendsHistory = g.getHistorySave().raw("dividendTax");
        check("both investment receipts are declared monthly flows", YearBook.kindOf("capitalGainsTax") == YearBook.Kind.FLOW
                && YearBook.kindOf("dividendTax") == YearBook.Kind.FLOW);
        quietly(() -> g.saveGame(10, "HT1 stocks and struck flows"));
        g.awaitSaves();
        for (boolean rebuild : new boolean[] {true, false}) {
            Game loaded = new Game(files);
            quietly(() -> {
                if (rebuild) loaded.loadGameSave(10);
                else { loaded.run(); loaded.loadGame(10); }
            });
            Household restored = loaded.getHouseholdBalance().cellByKey(h.key());
            close("basis survives " + rebuild, restored.basis[0], 123.5);
            close("loss carry survives " + rebuild, restored.lossCarried, 17.25);
            close("the current month's taxable gain survives " + rebuild, restored.taxedGains, 9.5);
            check("every appended cell slot survives " + rebuild, Arrays.equals(cells, loaded.getHouseholdBalance().toCellSaveArray()));
            close("the inclusion policy survives " + rebuild, loaded.getEconomyManager().getTaxPolicy().getCapitalGainsInclusion(), .75);
            check("the struck transfers and call counts survive " + rebuild,
                    Arrays.equals(b.investmentTaxesToSave(), loaded.getHouseholdBalance().investmentTaxesToSave()));
            close("budget gain revenue survives " + rebuild, loaded.getEconomyManager().getNationalAccounts().getCapitalGainsTax(), 12.5);
            close("budget dividend revenue survives " + rebuild, loaded.getEconomyManager().getNationalAccounts().getDividendTax(), 3.25);
            check("the recorded gain-tax history survives " + rebuild,
                    Arrays.equals(gainsHistory, loaded.getHistorySave().raw("capitalGainsTax")));
            check("the recorded dividend-tax history survives " + rebuild,
                    Arrays.equals(dividendsHistory, loaded.getHistorySave().raw("dividendTax")));
            loaded.awaitSaves();
        }
        check("the save floor rises past the last zero-basis format", GameVersion.SAVE_FORMAT > 58
                && GameVersion.SAVE_FORMAT == GameVersion.OLDEST_LOADABLE_FORMAT);
        try {
            com.google.gson.JsonObject saved = com.google.gson.JsonParser.parseString(java.nio.file.Files.readString(files.saveFile(10))).getAsJsonObject();
            saved.addProperty("saveFormat", 58); // the actual immediately preceding, zero-basis format
            java.nio.file.Files.writeString(files.saveFile(10), saved.toString());
            Game refused = new Game(files);
            quietly(() -> refused.loadGameSave(10));
            check("an old zero-basis save is actually refused before rebuild", refused.getLoadFailure() != null
                    && !refused.getLoadFailure().isBlank());
        } catch (java.io.IOException ex) {
            check("the scratch save can be read for the incompatible-format proof", false);
        }
        return fails == before;
    }

    public static void main(String[] args) throws Exception {
        costAndLoss();
        lossesFirst();
        foundersSplitsAndDividends();
        waterfall();
        stockFlows();
        liveCity();
        saveChecks();
        out.println("CapitalGainsCheck: " + fails + " failures");
        System.exit(fails);
    }
}

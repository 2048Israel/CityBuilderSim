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
    static final com.google.gson.Gson custodyJson = new com.google.gson.Gson();
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
            balance.attachInvestmentRegister(equity, exchange::shortShares);
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
        boolean pools = true, households = true, budget = true, claims = true;
        for (int month = 0; month < 120; month++) {
            quietly(g::toggleNextMonth);
            HouseholdBalance b = g.getHouseholdBalance();
            gains += b.getGainAssessments(); dividends += b.getDividendAssessments();
            tax += b.getCapitalGainsTax();
            claims &= Math.abs(b.getCapitalTaxOutstanding() - b.securedClaimCounterpart())
                    <= MoneyAudit.tolerance(MoneyAudit.CENT, b.getCapitalTaxOutstanding());
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
        check("every actual government claim has a debtor or unhanded transit counterpart", claims);
        out.println("Observed: " + gains + " gain assessments, " + dividends + " dividend assessments, tax " + tax);
        g.awaitSaves();
    }

    /** Nonzero stocks, inclusion and struck flows through both actual Game load routes, in scratch slot 10 only. */
    static boolean saveChecks() {
        int before = fails;
        GameFiles files = GameFiles.scratch("ht1-save");
        Game g = LongPlaytest.probeCity(files, null);
        HouseholdBalance b = g.getHouseholdBalance();
        Household h = b.cells().stream().filter(c -> !c.isEmpty() && c.shares[0] > 0).findFirst().orElseThrow();
        // Cause actual foreign ownership before seeding an already-assessed secured snapshot for the loader proof.
        double foreignUnits = h.shares[0] * h.households() * .1;
        double price = g.getEquity().foundingPrice();
        g.getExchange().bookOf(0).withdrawAll();
        g.getExchange().tradeForCheck(0, Exchange.CELL + h.key(), OrderBook.Side.SELL, price, foreignUnits);
        close("the save fixture actually transfers its foreign collateral", FundLedgerCheck.filled(
                g.getExchange().tradeForCheck(0, Exchange.WORLD, OrderBook.Side.BUY, price, foreignUnits)), foreignUnits);
        h.basis[0] = 123.5; h.lossCarried = 17.25; h.taxedGains = 9.5;
        g.getEconomyManager().getTaxPolicy().setCapitalGainsInclusion(.75);
        double[] taxState = new double[2 * HouseholdBalance.TAX_FLOW_SLOTS + 1];
        taxState[0] = 12.5; taxState[1] = 3.25; taxState[2] = 7; taxState[3] = 4;
        taxState[4] = 12.5; taxState[6] = 12.5;
        taxState[HouseholdBalance.TAX_FLOW_SLOTS + 7] = 1.25; // saved between-press noncash write-off
        taxState[taxState.length - 1] = 0;
        b.restoreInvestmentTaxes(taxState);
        h.taxShares[0] = h.shares[0] * .5; h.taxClaim[0] = 2.5;
        double[] secured = b.securedTaxesToSave(); secured[0] = 2.5 * h.households();
        double[] foreign = g.getEquity().foreignTaxesToSave();
        foreign[0] = foreignUnits; foreign[Equity.COMPANIES.length] = 3.5;
        g.getEquity().restoreForeignTaxes(g.getEquity().keys(), foreign);
        secured[0] += 3.5; b.restoreSecuredTaxes(secured);
        close("the nonzero saved domestic and foreign claims match their government asset",
                b.securedClaimCounterpart(), b.getCapitalTaxOutstanding());
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
            close("tagged units survive " + rebuild, restored.taxShares[0], h.taxShares[0]);
            close("secured liability survives " + rebuild, restored.taxClaim[0], 2.5);
            check("matching government/transit snapshot survives " + rebuild,
                    Arrays.equals(secured, loaded.getHouseholdBalance().securedTaxesToSave()));
            check("foreign attribution snapshot survives " + rebuild,
                    Arrays.equals(foreign, loaded.getEquity().foreignTaxesToSave()));
            close("loss carry survives " + rebuild, restored.lossCarried, 17.25);
            close("the current month's taxable gain survives " + rebuild, restored.taxedGains, 9.5);
            check("every appended cell slot survives " + rebuild, Arrays.equals(cells, loaded.getHouseholdBalance().toCellSaveArray()));
            close("the inclusion policy survives " + rebuild, loaded.getEconomyManager().getTaxPolicy().getCapitalGainsInclusion(), .75);
            check("the struck transfers and call counts survive " + rebuild,
                    Arrays.equals(b.investmentTaxesToSave(), loaded.getHouseholdBalance().investmentTaxesToSave()));
            close("restored claims retain matching government and debtor stocks " + rebuild,
                    loaded.getHouseholdBalance().getCapitalTaxOutstanding(), loaded.getHouseholdBalance().securedClaimCounterpart());
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

    /** A real census estate transfers one of ten senior households to twenty established heirs. */
    static final class Estate {
        final HouseholdFlowCheck.Town town = new HouseholdFlowCheck.Town();
        final HouseholdBalance balance = town.bal;
        final Household deceased = town.cell(FamilyStructure.SENIOR_ALONE, PayTier.SKILLED, 10);
        final Household heirs = town.cell(FamilyStructure.SINGLE_ADULT, PayTier.SKILLED, 20);
        final TaxPolicy policy = new TaxPolicy();
        final Equity equity = ExchangeCheck.equity();
        double received;
        Estate(double cash, double debt) {
            equity.listIfUnlisted(0, 1000, balance);
            for (Household c : balance.cells()) { Arrays.fill(c.shares, 0); Arrays.fill(c.basis, 0); }
            deceased.shares[0] = 100; deceased.basis[0] = 400;
            deceased.savings = cash; deceased.debt = debt;
            balance.setInvestmentTaxes(policy, c -> 10, (line, amount) -> received += amount);
            balance.attachInvestmentRegister(equity, c -> 0);
            town.expectRetired(FamilyStructure.SENIOR_ALONE, 9);
            balance.setFlows(0, 0, 0, .1, 0, 0, 0);
        }
        void run() { town.run(); }
    }

    static void securedEstates() {
        out.println("--- cash-first secured estate: assessment is distinct from collection ---");
        Estate shortCash = new Estate(10, 0); shortCash.run();
        close("the accepted 1000 value / 400 basis estate assesses 45", shortCash.balance.getCapitalTaxAssessed(), 45);
        close("only the actual ten cash is collected", shortCash.received, 10);
        close("the remaining 35 is a saved government claim", shortCash.balance.getCapitalTaxOutstanding(), 35);
        close("the heirs carry the claim with their tagged shares", shortCash.heirs.taxClaim[0] * 20, 35);
        close("the stepped-up inherited basis is 1000", shortCash.heirs.basis[0] * 20, 1000);
        close("the surviving source households keep their 400 basis", shortCash.deceased.basis[0], 400);
        close("matching receivable equals domestic and transit liabilities", shortCash.balance.securedClaimCounterpart(), 35);
        shortCash.balance.creditDividend(0, .2);
        close("a real attributable net dividend of twenty pays twenty old tax", shortCash.received, 30);
        close("fifteen remains, without repeating the assessment", shortCash.balance.getCapitalTaxOutstanding(), 15);
        close("assessment is still the original 45", shortCash.balance.getCapitalTaxAssessed(), 45);
        shortCash.balance.creditDividend(0, 1);
        close("collection caps at the old claim", shortCash.received, 45);
        close("paid security is released", shortCash.heirs.taxShares[0], 0);
        double before = shortCash.received; shortCash.balance.creditDividend(0, 1);
        close("a later unencumbered receipt cannot pay the cleared claim", shortCash.received, before);
        Estate ample = new Estate(100, 0); ample.run();
        close("cash-sufficient estate pays the actual 45", ample.received, 45);
        close("cash-sufficient heirs get the remaining 55", ample.heirs.savings * 20, 55);
        close("a fully paid estate creates no claim", ample.balance.getCapitalTaxOutstanding(), 0);
        Estate priority = new Estate(20, 15); priority.run();
        close("the existing revolving debt gets its fifteen first", priority.balance.getEstateRepaid(), 15);
        close("tax takes only the five remaining cash", priority.received, 5);
        close("the rest is secured, not written off for cash shortage", priority.balance.getCapitalTaxOutstanding(), 40);
        priority.balance.closeTaxMonth();
        priority.balance.surrenderShares(0); priority.equity.takeAllForCity(0, 0);
        double[] pending = priority.balance.investmentTaxesToSave();
        close("between-press common extinction saves its pending noncash claim loss", pending[HouseholdBalance.TAX_FLOW_SLOTS + 7], 40);
        close("common extinction never receives tax cash", priority.received, 5);
        priority.balance.beginTaxMonth();
        close("the next window records the saved pending loss once", priority.balance.getCapitalTaxWrittenOff(), 40);
        Estate losses = new Estate(10, 0); losses.deceased.lossCarried = 200; losses.run();
        close("the transferred loss reduces the assessed gain", losses.balance.getCapitalTaxAssessed(), 30);
        close("the inherited loss stock is consumed only in that fraction", losses.heirs.lossCarried, 0);
        close("the living source keeps its own loss carry", losses.deceased.lossCarried, 200);

        HouseholdFlowCheck.Town widows = new HouseholdFlowCheck.Town();
        Household couple = widows.cell(FamilyStructure.SENIOR_COUPLE, PayTier.SKILLED, 10);
        couple.shares[0] = couple.taxShares[0] = 100; couple.basis[0] = 400; couple.taxClaim[0] = 35;
        double[] claims = widows.bal.securedTaxesToSave(); claims[0] = 350; widows.bal.restoreSecuredTaxes(claims);
        widows.bal.setInvestmentTaxes(new TaxPolicy(), c -> 10, (line, amount) -> {});
        widows.expectRetired(FamilyStructure.SENIOR_COUPLE, 8).expectRetired(FamilyStructure.SENIOR_ALONE, 2);
        widows.bal.setFlows(0, 0, 0, .1, 0, 0, 0); widows.run();
        Household survivor = widows.bal.cell(FamilyStructure.SENIOR_ALONE, PayTier.SKILLED);
        close("a survivor rolls the original basis untaxed", survivor.basis[0], 400);
        close("a survivor carries the secured liability", survivor.taxClaim[0], 35);
        close("survivor rollover does not assess a deemed gain", widows.bal.getCapitalTaxAssessed(), 0);
        close("survivor claims conserve exactly", widows.bal.securedClaimCounterpart(), 350);
    }

    static void securedDepartures() {
        out.println("--- persistent foreign security through actual unfilled and filled orders ---");
        Market m = new Market(); m.trade(true, 4, 100); m.holder.savings = 10;
        m.balance.setInvestmentTaxes(m.policy, c -> 10, (line, amount) -> m.received += amount);
        m.town.expect(FamilyStructure.SINGLE_ADULT, PayTier.ELITE, 0);
        m.balance.setFlows(0, 0, 0, 0, 0, 1, 0); m.town.run();
        close("departure is assessed once before cash leaves", m.balance.getCapitalTaxAssessed(), 45);
        close("departure cash first collects ten", m.received, 10);
        close("no negative departing savings is created", m.balance.getTakenAway(), 0);
        m.equity.followEmigrants(m.balance, m.exchange::shortShares);
        double foreignBefore = m.equity.getForeignShares(0);
        m.equity.followEmigrants(m.balance, m.exchange::shortShares);
        close("a repeated handoff cannot add gross foreign holdings again", m.equity.getForeignShares(0), foreignBefore);
        close("all hundred unpaid tagged units remain attributed", m.equity.foreignTaxShares(0), 100);
        close("the 35 is foreign, not erased with the cell", m.equity.foreignTaxClaim(0), 35);
        m.exchange.bookOf(0).withdrawAll();
        check("the tagged order really rests with no bid", m.exchange.tradeForCheck(0, Exchange.TAX_EMIGRANTS, OrderBook.Side.SELL, 10, 100).isEmpty());
        double[] books = ExchangeCheck.bookOf(0, 1000);
        // A very high required return puts fair at the model's floor, so the ordinary world posts no bid.
        quietly(() -> ExchangeCheck.step(m.exchange, m.equity, m.balance, m.bank, m.firms, books, 1e6, 0, 2));
        close("the actual monthly reset preserves the unfilled tagged holding", m.equity.foreignTaxShares(0), 100);
        close("the actual monthly reset preserves the unpaid claim", m.equity.foreignTaxClaim(0), 35);
        close("unfilled is not a tax receipt", m.received, 10);
        m.holder.households = 1; m.holder.savings = 100;
        m.trade(true, 1, 10);
        close("an unrelated WORLD purchase does not pay old foreign tax", m.equity.foreignTaxClaim(0), 35);
        m.balance.setFlows(0, 0, 0, 0, 0, 1, 0); m.town.run();
        m.equity.followEmigrants(m.balance, m.exchange::shortShares);
        quietly(() -> ExchangeCheck.step(m.exchange, m.equity, m.balance, m.bank, m.firms, books, 1e6, 0, 3));
        m.exchange.bookOf(0).withdrawAll();
        m.exchange.tradeForCheck(0, Exchange.EMIGRANTS, OrderBook.Side.SELL, 1, 10);
        close("the ordinary new leaver actually sells ten units", FundLedgerCheck.filled(m.exchange.tradeForCheck(0, Exchange.WORLD, OrderBook.Side.BUY, 1, 10)), 10);
        close("ordinary new-leaver proceeds do not pay the older tagged claim", m.equity.foreignTaxClaim(0), 35);
        m.exchange.bookOf(0).withdrawAll();
        m.exchange.tradeForCheck(0, Exchange.TAX_EMIGRANTS, OrderBook.Side.SELL, 1, 20);
        close("a real partial tagged fill delivers twenty units", FundLedgerCheck.filled(m.exchange.tradeForCheck(0, Exchange.WORLD, OrderBook.Side.BUY, 1, 20)), 20);
        close("the partial receipt collects twenty and leaves fifteen", m.equity.foreignTaxClaim(0), 15);
        close("native tagged gross settlement is twenty", m.exchange.getTaxEmigrantsGross(0), 20);
        close("retained tax is a separate actual amount", m.exchange.getTaxEmigrantsRetained(0), 20);
        close("the tagged holder has no net cash from that fill", m.exchange.getTaxEmigrantsNet(0), 0);
        double[] foreignState = m.equity.foreignTaxesToSave(), government = m.balance.securedTaxesToSave();
        Equity copy = ExchangeCheck.equity(); copy.restore(m.equity.keys(), m.equity.toSaveArray());
        copy.restoreForeignTaxes(m.equity.keys(), foreignState);
        check("partial foreign attribution is copied by value", Arrays.equals(foreignState, copy.foreignTaxesToSave()));
        m.equity.split(0, 10); m.balance.splitShares(0, 10);
        close("split multiplies foreign tagged units", m.equity.foreignTaxShares(0), 800);
        close("split preserves the nominal claim", m.equity.foreignTaxClaim(0), 15);
        m.equity.redenominate(.1); m.balance.redenominate(.1);
        close("currency scales the foreign claim", m.equity.foreignTaxClaim(0), 1.5);
        close("government claim still matches the debtor after currency reform", m.balance.getCapitalTaxOutstanding(), m.balance.securedClaimCounterpart());
        m.equity.takeAllForCity(0, 0);
        close("genuine old-common extinction writes off the unpaid claim", m.balance.getCapitalTaxOutstanding(), 0);
        close("extinction never collects invented cash", m.exchange.getTaxEmigrantsRetained(0), 20);
        check("a saved government snapshot contains the outstanding claim", government[0] > 0);
    }

    /** All property enters the real Game receiver through census death, never a fabricated sale. */
    static Game custodyEstate(GameFiles files) { return custodyEstate(files, false); }
    static Game custodyEstate(GameFiles files, boolean lossCase) {
        Game g = LongPlaytest.probeCity(files, null);
        HouseholdBalance balance = g.getHouseholdBalance(); Equity equity = g.getEquity();
        double[] domestic = new double[Equity.COMPANIES.length];
        for (int c = 0; c < domestic.length; c++) domestic[c] = balance.sharesHeld(c);
        for (Household c : balance.cells()) { c.clearAll(); c.households = 0; }
        Household source = balance.cell(FamilyStructure.SENIOR_ALONE, PayTier.SKILLED); source.households = 10;
        source.savings = 3; source.debt = 1; source.studentDebt = 2;
        source.abroad = 4; source.cars = 2; source.paper = 10; source.lossCarried = 1;
        for (int c = 0; c < domestic.length; c++) {
            source.shares[c] = domestic[c] / 10; source.basis[c] = source.shares[c] * 4;
        }
        if (lossCase) source.lossCarried = Arrays.stream(source.shares).sum() * 12 + 10;
        source.taxShares[0] = source.shares[0] / 2; source.taxClaim[0] = .7;
        double[] claims = balance.securedTaxesToSave(); Arrays.fill(claims, 0); claims[0] = 7;
        balance.restoreSecuredTaxes(claims);
        balance.setInvestmentTaxes(g.getEconomyManager().getTaxPolicy(), c -> 10,
                (line, amount) -> { throw new AssertionError("A no-heir self-claim must not collect tax cash"); });
        LongTermBond paper = new LongTermBond(1000, 120, g.getMonth(), .05);
        paper.markIssued(900, .06); paper.takeAtSettle(Debt.Holder.HOUSEHOLDS, 100, 95); paper.settled();
        paper.reopen(200, 180, .06); // settled old private property while a new sale really is unsettled
        paper.allot(new Auction.Fill(20, 0, 0, 180), 200, 180);
        g.getDebtManager().setDebt(new java.util.ArrayList<>(java.util.List.of(paper)));
        CorporateBond a = new CorporateBond(1, Sectors.RETAIL, 100, .06, g.getMonth(), 1);
        CorporateBond b = new CorporateBond(2, Sectors.RETAIL, 200, .06, g.getMonth(), 2);
        a.households = 30; a.world = 70; b.households = 70; b.world = 130;
        source.setBondFace(1, 3); source.setBondFace(2, 7);
        BondMarket.State market = new BondMarket.State(); market.bonds = new java.util.ArrayList<>(java.util.List.of(a, b)); market.nextId = 3;
        BondMarket bm = g.getBondMarket(); bm.restore(market); bm.strikeCoupons();
        double charged = bm.getCouponsStruck(), issuerCash = g.getEconomyManager().getSectorCash(Sectors.RETAIL);
        // The same issuer cash API that the real finance phase charges; recording/payment cannot charge it again.
        g.getEconomyManager().setSectorCash(Sectors.RETAIL, issuerCash - charged); bm.recordCouponIssuerCharge(charged); bm.recordCouponBankPayment(0);
        double pendingCash = bm.couponClearingCash(), before = g.getCash();
        double openingPools = poolTotal(g), openingHouseholds = balance.totalSavings();
        check("no eligible working heir exists before the census", balance.cells().stream()
                .noneMatch(c -> c instanceof WorkingHousehold && c.households() > 0));
        balance.setOutsideCensus(c -> 0); balance.setFlows(0, 0, 0, 1, 0, 0, 0);
        balance.censusOnly((HouseholdBalance.Census) (shape, tier, stage) -> 0);
        EstateCustody owner = g.getEstateCustody();
        close("the estate repays existing revolving debt first", balance.getEstateRepaid(), 10);
        close("only actual remaining cash reaches treasury as property", g.getCash() - before, 20);
        close("the unchanged pool identity sees only the actual inherited cash edge", poolTotal(g) - openingPools, 20);
        close("household savings conserve estate cash plus the native debt repayment", balance.totalSavings()
                + g.getCash() - before + balance.getEstateRepaid(), openingHouseholds);
        close("no cash is labelled capital tax on a self-claim", balance.getCapitalGainsTax(), 0);
        check("new and old secured assessments consolidate after receipt", lossCase
                ? balance.getCapitalTaxAssessed() == 0 && balance.getCapitalTaxConsolidated() == 7
                : balance.getCapitalTaxAssessed() > 0 && balance.getCapitalTaxConsolidated() > 7);
        check("loss carry is consumed or retained by this actual assessment", lossCase ? owner.lossCarried() > 0 : owner.lossCarried() == 0);
        close("the government has no remaining self-claim", balance.getCapitalTaxOutstanding(), 0);
        close("no estate cash enters either emigration leg", balance.getTakenAway(), 0);
        close("the receiver retains actual USD", owner.usd(), 40);
        close("the receiver retains actual idle cars", owner.cars(), 20);
        close("city paper is reserved, not silently redeemed", owner.pendingPaper(), 100);
        close("the precise holder discount follows the reservation", paper.custodyReservedBook(), 95);
        close("reserved face cannot be traded as private face", paper.householdPrincipal(), 0);
        close("unpaid auction entitlement never enters the paid household estate stock", paper.allotHouseholdsFace(), 20);
        close("all real own-paper holder counterparts including custody add to the issuer", paper.householdPrincipal()
                + paper.custodyReservedFace() + paper.bankPrincipal() + paper.centralBankPrincipal()
                + paper.firmPrincipal() + paper.worldPrincipal(), paper.getOustandingPrincipal());
        check("the no-heir event really occurs during an unsettled sale", paper.unsettledFace() == 200 && paper.getSettleDue() == 180);
        close("no coupon is lost or duplicated at the holder handoff", bm.couponClearingCash(), pendingCash);
        close("the already-recorded source coupon reaches custody", owner.pendingCoupons(), .5);
        close("inheritance creates no bought-cash fiction", owner.getLedger().purchases(), 0);
        close("inheritance creates no disposal-proceeds fiction", owner.getLedger().proceeds(), 0);
        for (int c = 0; c < domestic.length; c++) {
            close("custody retains every domestic share " + c, owner.shares(c), domestic[c]);
            close("issuer and custody quantities match " + c, equity.getCustodyShares(c), owner.shares(c));
            close("the complete share identity holds " + c, equity.getShares(c) + g.getExchange().shortShares(c),
                    balance.sharesHeld(c) + equity.getForeignShares(c) + equity.getDealerShares(c)
                    + equity.getCityShares(c) + owner.shares(c));
        }
        for (CorporateBond bond : bm.getBonds()) {
            close("all actual corporate holders add to face " + bond.id(), bond.held(), bond.face());
            close("the capsule is the actual new bond holder " + bond.id(), owner.bondFace(bond.id()), bond.custody());
        }
        close("no paper counterpart was invented", owner.paperFinding(), 0);
        return g;
    }

    static void invokeHouseholdSettle(Game g) throws Exception {
        java.lang.reflect.Method settle = Game.class.getDeclaredMethod("householdsTakeTheirShare");
        settle.setAccessible(true); settle.invoke(g); // the production settlement, including its native unsettled guard
    }
    static double poolTotal(Game g) { return Arrays.stream(MoneyAudit.pools(g)).sum(); }

    static void custodyContinuation(Game g, String label) throws Exception {
        EstateCustody owner = g.getEstateCustody(); Debt d = g.getDebtManager().getDebt().get(0);
        double openingFace = d.getOustandingPrincipal(), treasury = g.getCash(), reserved = owner.pendingPaper();
        boolean refused = false;
        try { d.retirePart(0, reserved, 0, 0); } catch (IllegalStateException expected) { refused = true; }
        check(label + " preserves the native unsettled-retirement guard", refused);
        owner.consolidatePaper(); close(label + " does not consolidate before actual settlement", owner.pendingPaper(), reserved);
        invokeHouseholdSettle(g); owner.consolidatePaper();
        close(label + " native settlement permits exact face retirement", d.getOustandingPrincipal(), openingFace - reserved);
        close(label + " both reservation counterparts are cleared", d.custodyReservedFace() + owner.pendingPaper(), 0);
        close(label + " own debt consolidation manufactures no redemption cash", g.getCash(), treasury);
        double issuerCash = g.getEconomyManager().getSectorCash(Sectors.RETAIL), before = poolTotal(g);
        BondMarket bm = g.getBondMarket(); double worldBefore = bm.getCouponsAbroad();
        payRecordedCoupons(bm);
        close(label + " the recorded custody coupon is actual cash once", g.getCash() - treasury, .5);
        close(label + " the issuer is not charged a second time", g.getEconomyManager().getSectorCash(Sectors.RETAIL), issuerCash);
        close(label + " coupon clearing conserves real pooled cash across the world edge", poolTotal(g) + bm.getCouponsAbroad() - worldBefore, before);
        close(label + " the record-date entitlement is discharged", owner.pendingCoupons() + bm.couponClearingCash(), 0);
        double paidOnce = g.getCash(); payRecordedCoupons(bm); close(label + " cleared coupons cannot pay twice", g.getCash(), paidOnce);
        // Keep a distinct genuine market step: coupon-only payment above cannot conceal trading cash edges.
        double[] marketBefore = bondCashEdges(bm); double marketPools = poolTotal(g);
        double orphaned = bm.getOrphanedToHouseholds();
        bm.takeMonth(g.getMonth());
        double[] marketAfter = bondCashEdges(bm);
        check(label + " the full market really fills native cash trades", marketAfter[2] > marketBefore[2]);
        close(label + " native trading/coupon/principal and paired-world edges conserve pooled cash",
                poolTotal(g) - marketPools, (marketAfter[0] - marketBefore[0]) - (marketAfter[1] - marketBefore[1]));
        close(label + " the full market leaves no uncredited household cash", bm.getOrphanedToHouseholds(), orphaned);
        // A later ordinary coupon is charged on actual custody holdings and entered on its existing FundLedger lot.
        bm.strikeCoupons();
        check(label + " trading caused a nonzero bank record-date entitlement", bm.getCouponsDueToBank() > 0);
        chargeFixtureCoupons(g); bookFixtureBankCoupon(g);
        payRecordedCoupons(bm); close(label + " a later real custody coupon is paid", g.getCash() - paidOnce, .5);
        double basis = owner.getLedger().acbHeld(), units = owner.shares(0), ordinaryFund = g.getEquity().getCityShares(0);
        g.getEquity().split(0, 2);
        close(label + " custody split scales units", owner.shares(0), units * 2);
        close(label + " custody split preserves total basis", owner.getLedger().acbHeld(), basis);
        double cash = g.getCash(), sector = g.getEconomyManager().getSectorCash(Equity.COMPANIES[0]);
        double dividend = 1;
        g.getEconomyManager().setSectorCash(Equity.COMPANIES[0], sector - dividend);
        double abroad = g.getEquity().payDividend(0, dividend, g.getHouseholdBalance(), g.getExchange().shortShares(0));
        check(label + " a true issuer dividend reaches its passive holder", g.getCash() > cash && abroad >= 0);
        close(label + " custody never becomes an ordinary fund lot", g.getEquity().getCityShares(0), ordinaryFund * 2);
        cash = g.getCash(); sector = g.getEconomyManager().getSectorCash(Sectors.RETAIL);
        bm.redeemMaturing(g.getMonth() + 1);
        close(label + " actual issuer maturity pays only its custody face to treasury", g.getCash() - cash, 30);
        close(label + " actual maturity charges the whole issued face once", sector - g.getEconomyManager().getSectorCash(Sectors.RETAIL), 100);
        close(label + " the matured custody issue disappears", owner.bondFace(1), 0);
        bm.writeDown(Sectors.RETAIL, .5);
        close(label + " a genuine default writes down the other custody bond", owner.bondFace(2), 35);
        check(label + " default records a noncash asset loss", owner.assetLoss() > 0);
        for (CorporateBond bond : bm.getBonds()) close(label + " default preserves complete holders", bond.held(), bond.face());
        double usd = owner.usd(), cars = owner.cars();
        owner.payForeignCoupon(.02, g.getForeignAccounts().getRate());
        check(label + " the existing foreign coupon really pays home", owner.foreignCoupon() > 0);
        close(label + " earning does not mark USD principal to local cash", owner.usd(), usd);
        check(label + " actual currency reform succeeds", g.reformCurrencyForTest(100));
        close(label + " currency reform preserves USD units", owner.usd(), usd);
        close(label + " currency reform preserves physical cars", owner.cars(), cars);
        g.getEquity().cancelHeld(0);
        close(label + " resolved old common leaves no custody replacement shares", owner.shares(0), 0);
        check(label + " the issuer has no custody residual after extinction", g.getEquity().getCustodyShares(0) == 0);
    }

    static void payRecordedCoupons(BondMarket market) throws Exception {
        java.lang.reflect.Method pay = BondMarket.class.getDeclaredMethod("payCoupons"); pay.setAccessible(true); pay.invoke(market);
    }
    /** The bond edges declared by MoneyAudit, including both legs of household/world trades. */
    static double[] bondCashEdges(BondMarket b) {
        double in = b.getHouseholdsBought() + b.getWorldBought() + b.getHouseholdsSoldAbroad()
                + b.getHouseholdsBoughtAbroad() + b.getWorldWrittenOff();
        double out = b.getHouseholdsSold() + b.getCouponsToHouseholds() + b.getPrincipalToHouseholds()
                + b.getWorldSold() + b.getCouponsAbroad() + b.getPrincipalAbroad()
                + b.getHouseholdsSoldAbroad() + b.getHouseholdsBoughtAbroad() + b.getWorldWrittenOff();
        double fills = b.getHouseholdsBought() + b.getHouseholdsSold() + b.getWorldBought() + b.getWorldSold()
                + b.getHouseholdsBoughtAbroad() + b.getHouseholdsSoldAbroad();
        return new double[] {in, out, fills};
    }
    static void chargeFixtureCoupons(Game g) {
        BondMarket bm = g.getBondMarket(); double due = bm.getCouponsStruck(), pools = poolTotal(g);
        check("every recorded issuer bill is still unpaid before its one actual charge", !bm.toState().couponsCharged);
        double charged = 0;
        for (var bill : bm.toState().dueByIssuer.entrySet()) {
            double cash = g.getEconomyManager().getSectorCash(bill.getKey());
            g.getEconomyManager().setSectorCash(bill.getKey(), cash - bill.getValue());
            close("actual issuer pays its own recorded coupon " + bill.getKey(),
                    cash - g.getEconomyManager().getSectorCash(bill.getKey()), bill.getValue());
            charged += bill.getValue();
        }
        close("all issuer debits fund the exact aggregate recorded bill", charged, due);
        bm.recordCouponIssuerCharge(charged);
        close("actual issuer charges and clearing conserve whole cash", poolTotal(g), pools);
    }
    static void bookFixtureBankCoupon(Game g) {
        double due = g.getBondMarket().getCouponsDueToBank(), bank = g.getBank().getCash(), pools = poolTotal(g);
        g.getBank().takeBondCoupons(due); g.getBondMarket().recordCouponBankPayment(due);
        close("actual bank coupon credit matches its clearing debit", g.getBank().getCash() - bank, due);
        close("actual bank booking and clearing conserve whole cash", poolTotal(g), pools);
    }
    static void bondCounterparts(Game g, String label) {
        java.util.Set<Integer> ids = new java.util.HashSet<>();
        for (CorporateBond b : g.getBondMarket().getBonds()) {
            check(label + " unique live issuer " + b.id(), ids.add(b.id()));
            close(label + " complete issuer/holder face " + b.id(), b.held(), b.face());
            close(label + " actual private face " + b.id(), b.households(), g.getHouseholdBalance().bondFaceHeld(b.id()));
            close(label + " actual custody face " + b.id(), b.custody(), g.getEstateCustody().bondFace(b.id()));
        }
        for (Household cell : g.getHouseholdBalance().cells()) for (int id : cell.bondFace.keySet())
            check(label + " private holding has its original live issuer " + cell.key() + "/" + id, ids.contains(id));
        for (int id : g.getEstateCustody().bondFaces().keySet())
            check(label + " custody holding has a live issuer " + id, ids.contains(id));
        BondMarket.State s = g.getBondMarket().toState();
        close(label + " cell entitlements equal the private record", s.dueCells.values().stream().mapToDouble(Double::doubleValue).sum(), s.couponDues[0]);
        close(label + " capsule entitlement equals the custody record", g.getEstateCustody().pendingCoupons(), s.couponDues[4]);
        if (s.couponRecordOpen) {
            double holders = Arrays.stream(s.couponDues).sum() + s.dueCompanies.values().stream().mapToDouble(Double::doubleValue).sum();
            close(label + " all record-date holders match every issuer bill", holders, s.dueByIssuer.values().stream().mapToDouble(Double::doubleValue).sum());
        }
    }
    /** Nonempty phase snapshots: no end-holder reconstruction, no charge/payment replay. */
    static void custodyCouponPhases() throws Exception {
        out.println("--- coupon cash clearing before/after charge, bank booking and partial custody transfer ---");
        for (int phase = 0; phase < 4; phase++) {
            GameFiles files = GameFiles.scratch("ht1-coupon-phase-" + phase);
            Game g = LongPlaytest.probeCity(files, null); BondMarket bm = g.getBondMarket();
            Household h = g.getHouseholdBalance().cells().stream().filter(c -> c.households() > 0).findFirst().orElseThrow();
            bondCounterparts(g, "warmed opening");
            BondMarket.State state = bm.toState().copy();
            check("the warmed book has no pending record to replace", !state.couponRecordOpen);
            int id = state.nextId;
            check("the appended issue uses an unused native nextId", state.bonds.stream().noneMatch(b -> b.id() == id)
                    && g.getHouseholdBalance().cells().stream().noneMatch(c -> c.bondFace.containsKey(id)));
            double baselineAll = 0, baselineHouseholds = 0, baselineBank = 0, baselineSource = 0;
            for (CorporateBond b : state.bonds) {
                double coupon = b.monthlyCoupon(); baselineAll += coupon;
                baselineHouseholds += coupon * b.households() / b.face();
                baselineBank += coupon * b.bank / b.face();
                baselineSource += coupon * h.bondFace(b.id()) * h.households() / b.face();
            }
            CorporateBond bond = new CorporateBond(id, Sectors.RETAIL, 100, .06, g.getMonth(), 120);
            bond.households = 40; bond.bank = bond.bankCost = 30; bond.world = 30;
            h.setBondFace(id, 40 / h.households());
            state.bonds.add(bond); state.nextId = id + 1;
            bm.restore(state);
            check("the complete warmed register plus one new issue remains live", bm.getBonds().size() == state.bonds.size() && bm.toState().nextId == id + 1);
            bondCounterparts(g, "before strike"); bm.strikeCoupons();
            double totalCoupon = baselineAll + .5, sourceCoupon = baselineSource + .2;
            close("the new issue contributes exactly its own coupon", bm.getCouponsStruck() - baselineAll, .5);
            close("the new source face contributes exactly its own entitlement", bm.toState().recordDueCells.get(h.key()) - baselineSource, .2);
            close("preserved baseline and new private obligations are fully recorded", bm.toState().couponDues[0], baselineHouseholds + .2);
            close("preserved baseline and new bank obligations are fully recorded", bm.getCouponsDueToBank(), baselineBank + .15);
            bondCounterparts(g, "struck opening");
            close("an uncharged record-date promise is zero clearing cash", bm.couponClearingCash(), 0);
            double cashBeforeCharge = poolTotal(g), treasuryBefore = g.getCash();
            payRecordedCoupons(bm);
            close("the uncharged promise cannot pay custody or disappear", g.getCash(), treasuryBefore);
            close("the uncharged promise remains fully recorded", bm.couponUnpaidEntitlement(), totalCoupon);
            // A quarter of the source record-date holder dies; the other three quarters retain their entitlement.
            java.util.Map<Integer, Double> sourceBundle = new java.util.LinkedHashMap<>(h.bondFace);
            for (CorporateBond b : bm.getBonds()) {
                double face = sourceBundle.getOrDefault(b.id(), 0.0) * h.households();
                CorporateBond opening = b.copy();
                double privateFace = b.households(), custodyFace = b.custody();
                h.setBondFace(b.id(), sourceBundle.getOrDefault(b.id(), 0.0) * .75);
                bm.transferToCustody(b.id(), face * .25);
                close("the complete source bundle transfers its quarter " + b.id(), b.custody() - custodyFace, face * .25);
                close("only the source quarter leaves private ownership " + b.id(), privateFace - b.households(), face * .25);
                close("the original issuer face is preserved " + b.id(), b.face(), opening.face());
                close("the unrelated bank retains its position " + b.id(), b.bank, opening.bank);
                close("the unrelated world retains its position " + b.id(), b.world, opening.world);
                close("the ordinary city fund retains its position " + b.id(), b.city, opening.city);
                check("every unrelated company retains its position " + b.id(), b.companies.equals(opening.companies));
            }
            bm.transferRecordedCouponToCustody(h.key(), .25);
            close("partial custody transfer preserves the complete holder sum", bond.held(), bond.face());
            close("the new inherited issue contributes exactly ten face", g.getEstateCustody().bondFace(id), 10);
            close("only the deceased complete source fraction receives its recorded coupon", g.getEstateCustody().pendingCoupons(), sourceCoupon * .25);
            close("the new inherited issue contributes exactly its own recorded coupon", g.getEstateCustody().pendingCoupons() - baselineSource * .25, .05);
            close("the live source retains its full record-date fraction", bm.toState().dueCells.get(h.key()), sourceCoupon * .75);
            close("other private record-date holders are preserved", bm.toState().couponDues[0], baselineHouseholds + .2 - sourceCoupon * .25);
            bondCounterparts(g, "after complete quarter transfer");
            if (phase >= 1) {
                chargeFixtureCoupons(g);
                close("the real issuer debit moves to cash clearing", poolTotal(g), cashBeforeCharge);
                close("charged clearing includes the still-unpaid bank", bm.couponClearingCash(), totalCoupon);
                // Remove an actual issuer debit deliberately: the unchanged cash audit pool definition MUST expose it.
                double issuer = g.getEconomyManager().getSectorCash(Sectors.RETAIL);
                g.getEconomyManager().setSectorCash(Sectors.RETAIL, issuer + .5);
                check("a missing issuer debit is caught by the actual pool identity", Math.abs(poolTotal(g) - cashBeforeCharge) > MoneyAudit.CENT);
                g.getEconomyManager().setSectorCash(Sectors.RETAIL, issuer);
            }
            if (phase >= 2) { bookFixtureBankCoupon(g); close("bank-booked clearing excludes the paid bank", bm.couponClearingCash(), totalCoupon - baselineBank - .15); }
            if (phase >= 3) { payRecordedCoupons(bm); close("post-payment cash clearing is empty", bm.couponClearingCash(), 0); }
            bondCounterparts(g, "before phase save " + phase);
            String pending = custodyJson.toJson(bm.toState());
            quietly(() -> g.saveGame(10, "HT1 nonempty coupon phase")); g.awaitSaves();
            for (boolean rebuild : new boolean[] {true, false}) {
                Game loaded = new Game(files);
                quietly(() -> { if (rebuild) loaded.loadGameSave(10); else { loaded.run(); loaded.loadGame(10); } });
                BondMarket restored = loaded.getBondMarket();
                check("all record-date maps/flags/cash survive phase " + phase + "/" + rebuild,
                        pending.equals(custodyJson.toJson(restored.toState())));
                bondCounterparts(loaded, "after phase load " + phase + "/" + rebuild);
                double heldBefore = poolTotal(loaded), bankBefore = loaded.getBank().getCash();
                double expectedBankCredit = restored.getCouponsDueToBank();
                if (phase == 0) chargeFixtureCoupons(loaded);
                if (phase < 2) bookFixtureBankCoupon(loaded);
                close("the bank's once-only credit survives phase " + phase + "/" + rebuild,
                        loaded.getBank().getCash() - bankBefore, expectedBankCredit);
                close("native charges/bookings conserve pooled cash", poolTotal(loaded), heldBefore);
                double receiverBefore = loaded.getCash(), households = loaded.getHouseholdBalance().totalSavings();
                Household liveSource = loaded.getHouseholdBalance().cells().stream().filter(c -> c.key().equals(h.key())).findFirst().orElseThrow();
                double sourceSavings = liveSource.savings * liveSource.households();
                double abroad = restored.getCouponsAbroad(); payRecordedCoupons(restored);
                close("actual partial inherited coupon reaches treasury once", loaded.getCash() - receiverBefore, phase == 3 ? 0 : sourceCoupon * .25);
                close("the live source keeps its original record-date fraction", liveSource.savings * liveSource.households() - sourceSavings, phase == 3 ? 0 : sourceCoupon * .75);
                close("every preserved private holder receives its recorded entitlement", loaded.getHouseholdBalance().totalSavings() - households,
                        phase == 3 ? 0 : baselineHouseholds + .2 - sourceCoupon * .25);
                close("final clearing is discharged", restored.couponClearingCash(), 0);
                close("the actual debit, bank credit and remaining payments conserve cash", poolTotal(loaded)
                        + loaded.getHouseholdBalance().totalSavings() - households + restored.getCouponsAbroad() - abroad, heldBefore);
                double after = poolTotal(loaded); payRecordedCoupons(restored);
                close("post-payment cannot create a duplicate receipt", poolTotal(loaded), after);
                loaded.awaitSaves();
            }
            g.awaitSaves();
        }
    }

    static void custodySaves() throws Exception {
        out.println("--- actual all-stock no-heir estate, pending settlement and both loaders ---");
        GameFiles files = GameFiles.scratch("ht1-custody"); Game original = custodyEstate(files);
        original.getHistorySave().recordMonth(original);
        String expected = custodyJson.toJson(original.getEstateCustody().toState());
        quietly(() -> original.saveGame(10, "HT1 custody pending native settlement")); original.awaitSaves();
        for (boolean rebuild : new boolean[] {true, false}) {
            Game loaded = new Game(files);
            quietly(() -> { if (rebuild) loaded.loadGameSave(10); else { loaded.run(); loaded.loadGame(10); } });
            check("every custody owner slot survives loader " + rebuild,
                    expected.equals(custodyJson.toJson(loaded.getEstateCustody().toState())));
            close("saved holder discount survives loader " + rebuild, loaded.getDebtManager().getDebt().get(0).custodyReservedBook(), 95);
            close("recorded coupon entitlement survives loader " + rebuild, loaded.getEstateCustody().pendingCoupons(), .5);
            close("noncash ownership history survives loader " + rebuild, loaded.getHistorySave().raw("custodyUsd")[original.getHistorySave().raw("custodyUsd").length - 1], 40);
            custodyContinuation(loaded, "loader " + rebuild); loaded.awaitSaves();
            String afterState = custodyJson.toJson(loaded.getEstateCustody().toState());
            quietly(() -> loaded.saveGame(11, "HT1 custody after native receipts")); loaded.awaitSaves();
            for (boolean again : new boolean[] {true, false}) {
                Game after = new Game(files);
                quietly(() -> { if (again) after.loadGameSave(11); else { after.run(); after.loadGame(11); } });
                check("post-settlement custody snapshot survives " + rebuild + "/" + again,
                        afterState.equals(custodyJson.toJson(after.getEstateCustody().toState())));
                double closing = after.getCash(); after.getEstateCustody().consolidatePaper();
                close("post-settlement load cannot repeat retirement " + rebuild + "/" + again, after.getCash(), closing);
                close("post-settlement reservation remains cleared " + rebuild + "/" + again, after.getEstateCustody().pendingPaper(), 0);
                after.awaitSaves();
            }
        }
        // Unrelated real heirs continue on the ordinary taxed route even when custody exists elsewhere.
        Estate heirs = new Estate(10, 0); heirs.run();
        close("a real unrelated heir receives its stepped basis", heirs.heirs.basis[0] * 20, 1000);
        close("ordinary real heirs still carry the unpaid 35 claim", heirs.heirs.taxClaim[0] * 20, 35);
        original.awaitSaves();
        Game losses = custodyEstate(GameFiles.scratch("ht1-custody-loss"), true);
        check("the loss-only estate retains a positive exact memo stock", losses.getEstateCustody().lossCarried() > 0);
        losses.awaitSaves();
        for (Debt d : java.util.List.of(new MediumTermBond(1000, 120, 1, .05),
                new FloatingRateNote(1000, 120, 1, .01, .03), new InflationLinkedBond(1000, 120, 1, .02, 100))) {
            d.markIssued(900, .06); d.takeAtSettle(Debt.Holder.HOUSEHOLDS, 100, 95); d.settled();
            Debt control = custodyJson.fromJson(custodyJson.toJson(d), d.getClass());
            control.retirePart(0, 100, 0, 0); // the existing native holder/schedule operation is the oracle
            d.reserveCustody(100); d.consolidateCustody();
            check("reservation uses the native schedule " + d.getType(), Arrays.equals(d.remainingCashFlows(), control.remainingCashFlows()));
            close("reservation uses the native issuer carrying operation " + d.getType(), d.getCarryingValue(), control.getCarryingValue());
            close("nonzero reserved discount is retired with its exact holder " + d.getType(), d.discountOf(Debt.Holder.HOUSEHOLDS), 0);
        }
    }

    public static void main(String[] args) throws Exception {
        custodyCouponPhases();
        custodySaves();
        securedEstates();
        securedDepartures();
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

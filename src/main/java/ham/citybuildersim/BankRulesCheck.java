package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The window against collateral and the bank's rules as the player's dials.
 * WHY (CBK3): a capital-only equity desk could be financed without limit at
 * the window. Numbered sections cause collateral, funding and credit limits;
 * the optional evidence run plays a second copy of the supplied autosave.
 */
public final class BankRulesCheck {
    static int fails;
    static final PrintStream out = System.out;
    static final PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-76s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void silently(Runnable action) {
        PrintStream before = System.out;
        System.setOut(quiet);
        try { action.run(); } finally { System.setOut(before); }
    }

    /** Real writer and loader, including an otherwise identical save with the new keys absent. */
    static boolean saveChecks() throws Exception {
        Path root = TempDirs.create("cbk3-save");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        Game g = new Game(files);
        silently(g::run);
        for (BankRules.Dial dial : BankRules.Dial.values())
            g.getBank().rules().set(dial, dial.min + (dial.max - dial.min) * .4);
        Bank b = g.getBank();
        b.refresh(1, 1, 0, 20, 0, 0);
        b.setCash(-30);
        b.setForeignFundingRate(.08);
        b.fundToCover(.05);
        g.getCentralBank().settleWindow(b.windowFunding());
        b.settleForeignFunding(g.getCentralBank().getAdvancesToBank());
        double[] expected = b.fundingToSave();
        double[] dials = b.rules().toSave();
        boolean[] wrote = {false};
        silently(() -> wrote[0] = g.saveGame(GameFiles.AUTOSAVE_SLOT, "CBK3 scratch").ok);
        if (!wrote[0] || !(b.getForeignWholesale() > 0) || !(b.getForeignFundingCost() > 0)) return false;
        Game loaded = new Game(files);
        silently(() -> loaded.loadGameSave(GameFiles.AUTOSAVE_SLOT));
        boolean ok = java.util.Arrays.equals(expected, loaded.getBank().fundingToSave())
                && java.util.Arrays.equals(dials, loaded.getBank().rules().toSave());
        com.google.gson.JsonObject old = com.google.gson.JsonParser.parseString(
                Files.readString(files.saveFile(GameFiles.AUTOSAVE_SLOT))).getAsJsonObject();
        old.remove("bankRules");
        old.remove("bankFunding");
        Files.writeString(files.saveFile(GameFiles.AUTOSAVE_SLOT), old.toString());
        Game legacy = new Game(files);
        silently(() -> legacy.loadGameSave(GameFiles.AUTOSAVE_SLOT));
        ok &= java.util.Arrays.equals(new BankRules().toSave(), legacy.getBank().rules().toSave())
                && legacy.getBank().getForeignWholesale() == 0
                && legacy.getBank().getWindowFundingCost() == legacy.getBank().getFundingCost();
        // A new city resets settings; an absent array never inherits the last city's rule.
        loaded.getBank().rules().restore(null);
        ok &= java.util.Arrays.equals(new BankRules().toSave(), loaded.getBank().rules().toSave());
        g.awaitSaves(); loaded.awaitSaves(); legacy.awaitSaves();
        return ok;
    }

    /** Reads only the supplied evidence folder; all play and autosaves use a fresh scratch. */
    static void evidence(Path source) throws Exception {
        out.println("--- 6. the copied month-133 city played forward ---");
        Path root = TempDirs.create("cbk3-evidence");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        Files.createDirectories(files.savesDirectory());
        for (String name : new String[]{"autosave.json", "autosave-history.json", "autosave-map.bin"})
            Files.copy(source.resolve(name), files.savesDirectory().resolve(name));
        Game g = new Game(files);
        silently(() -> g.loadGameSave(GameFiles.AUTOSAVE_SLOT));
        check("the evidence loaded month 133 on scratch", g.getMonth() == 133);
        Bank bank = g.getBank();
        CentralBank cb = g.getCentralBank();
        out.printf("EVIDENCE start month=%d desk=%.9f cash=%.9f window=%.9f revenue12=%.9f failures=%d%n",
                g.getMonth(), bank.getSecurities(), bank.getCash(), cb.getAdvancesToBank(),
                cb.trailingRevenue() * 12, bank.getFailures());
        int failures = bank.getFailures();
        double revenue = 0, remitted = 0, worstAudit = 0;
        int months = Integer.getInteger("cbs.cbk3.months", 120);
        for (int i = 0; i < months; i++) {
            int before = g.getMonth();
            silently(g::toggleNextMonth);
            check("evidence month advances " + before, g.getMonth() == before + 1);
            revenue += g.getEconomyManager().getNationalAccounts().getTotalRevenue();
            remitted += cb.getRemitted();
            MoneyAudit.Result audit = g.getLastMoneyAudit();
            check("evidence window stays within collateral at month " + g.getMonth(),
                    cb.getAdvancesToBank() <= bank.windowCollateral()
                            * bank.rules().get(BankRules.Dial.WINDOW_LIMIT) + MoneyAudit.CENT);
            check("evidence desk stays outside window funding at month " + g.getMonth(),
                    cb.getAdvancesToBank() <= Math.max(0, bank.wholesaleFunding()
                            - Math.max(0, bank.getSecurities())) + MoneyAudit.CENT);
            check("evidence money audit at month " + g.getMonth(), audit != null
                    && Math.abs(audit.residual) <= MoneyAudit.tolerance(audit.moved()));
            if (audit != null) worstAudit = Math.max(worstAudit, Math.abs(audit.residual));
            if ((i + 1) % 12 == 0)
                out.printf("EVIDENCE month=%d share=%.9f desk=%.9f cash=%.9f window=%.9f failures=%d audit=%.12g%n",
                        g.getMonth(), revenue > 0 ? remitted / revenue : 0,
                        bank.getSecurities(), bank.getCash(), cb.getAdvancesToBank(),
                        bank.getFailures(), worstAudit);
        }
        out.printf("EVIDENCE final months=%d remittance=%.9f revenue=%.9f share=%.9f newFailures=%d audit=%.12g%n",
                months, remitted, revenue, revenue > 0 ? remitted / revenue : 0,
                bank.getFailures() - failures, worstAudit);
        g.awaitSaves();
    }

    public static void main(String[] args) throws Exception {
        out.println("--- 1. the window lends only against haircutted collateral ---");
        Bank b = new Bank();
        b.refresh(1, 100, 0, 1_000, 200, 50);
        b.injectCapital(1_000);
        b.setCash(-2_000);
        CentralBank cb = new CentralBank();
        cb.setWindowCapacity(b::windowFunding);
        double collateral = b.windowCollateral();
        check("the published loan and government margins determine lending value",
                Math.abs(collateral - (1_000 * .70 + 50 * .15 + 200 * .95)) < 1e-12);
        b.setGovernmentCollateral(() -> 100);
        check("government collateral follows market value instead of its carried face",
                Math.abs(b.windowCollateral() - (1_000 * .70 + 50 * .15 + 100 * .95)) < 1e-12);
        b.setGovernmentCollateral(null);
        cb.advanceToBank(collateral * 10);
        check("even a direct advance cannot exceed the collateral", cb.getAdvancesToBank() <= collateral);
        check("a further advance has no room", cb.advanceToBank(1) == 0);
        b.setCash(1);
        cb.settleWindow(1_000);
        check("a repaid shortfall destroys the whole advance", cb.getAdvancesToBank() == 0);

        out.println("--- 2. the equity desk has a stable cash budget ---");
        b.setCash(-2_000);
        double before = b.windowCollateral();
        b.markSecurities(600);
        check("equity adds no eligible collateral", b.windowCollateral() == before);
        cb.settleWindow(b.wholesaleFunding());
        b.settleForeignFunding(cb.getAdvancesToBank());
        check("the desk's whole unsupported inventory is funded abroad",
                b.getForeignWholesale() >= Math.min(b.wholesaleFunding(), b.getSecurities()));
        check("the window is still inside the collateral after refinancing", cb.getAdvancesToBank() <= b.windowCollateral());
        check("capital without stable cash cannot buy shares", b.deskCanCarry(600, 1, 1) == 0);
        b.setCash(-50);
        double room = b.stableDeskRoom();
        check("core deposits cause a positive but finite desk cash budget", room > 0);
        check("the capital rule also respects that cash budget", b.deskCanCarry(600, 1, 1) <= room);
        b.deskPays(room);
        check("one fill spends the budget for every later name", b.stableDeskRoom() == 0);
        b.receiveHotMoney(1_000);
        b.setForeignDeposits(1_000);
        check("hot money cannot replenish the stable desk budget", b.stableDeskRoom() == 0);

        out.println("--- 3. tighter saved rules constrain their own credit door ---");
        Bank capital = new Bank();
        capital.refresh(1, 100_000, 0, 20_000, 0, 0);
        capital.injectCapital(10_000);
        double capacity = capital.capacity();
        double insuredTarget = capital.leverageTarget();
        capital.rules().set(BankRules.Dial.CAPITAL_MINIMUM, .16);
        check("a higher capital minimum supports less credit", capital.capacity() < capacity);
        check("raising risk capital does not relax the insured-loan leverage target", capital.leverageTarget() >= insuredTarget);
        Bank concentrated = new Bank();
        concentrated.refresh(1, 100_000, 0, 20_000, 0, 0);
        concentrated.setConcentration(java.util.Map.of(Sectors.RETAIL, new Bank.Exposure(20_000, .04, .45)));
        double addOn = concentrated.getConcentrationAddOn(), minimum = concentrated.minimumEquity();
        concentrated.rules().set(BankRules.Dial.CAPITAL_MINIMUM, .16);
        check("a capital change reprices concentration weight at once, preserving its absolute add-on",
                addOn > 0 && Math.abs(concentrated.getConcentrationWeighted() * .16 - addOn) < 1e-10);
        check("concentration does not let a tighter capital rule reduce required equity",
                concentrated.minimumEquity() > minimum);
        capital.rules().reset();
        double leverage = capital.leverageHeadroom();
        capital.rules().set(BankRules.Dial.LEVERAGE_MINIMUM, .06);
        check("a higher leverage floor leaves less exposure room", capital.leverageHeadroom() < leverage);
        capital.rules().reset();
        double target = capital.targetEquity();
        capital.rules().set(BankRules.Dial.COUNTERCYCLICAL, .025);
        check("the countercyclical buffer requires more retained capital", capital.targetEquity() > target);
        capital.rules().reset();
        double funding = capital.fundingLimit();
        capital.rules().set(BankRules.Dial.RESERVE_REQUIREMENT, .20);
        check("reserves withhold deposits from lending", capital.fundingLimit() < funding);
        capital.rules().reset();
        Bank spread = new Bank();
        spread.refresh(1, 10_000, 0, 100_000, 0, 0);
        spread.injectCapital(20_000);
        spread.setCash(-70_000);
        spread.setForeignFundingRate(.07);
        spread.fundToCover(.05);
        spread.closeMonth();
        double prime = spread.prime(.05);
        spread.rules().set(BankRules.Dial.WINDOW_SPREAD, .025);
        check("a higher window spread raises the cost of genuinely window-funded credit",
                spread.windowFunding() > 0 && spread.prime(.05) > prime);
        capital.rules().reset();
        capital.setCash(-200_000);
        double window = capital.windowFunding();
        capital.rules().set(BankRules.Dial.WINDOW_LIMIT, 0);
        check("closing the window removes secured credit without changing collateral",
                window > 0 && capital.windowFunding() == 0 && capital.windowCollateral() > 0);
        BusinessDebtManager business = new BusinessDebtManager();
        business.setAssets(Sectors.RETAIL, 1_000);
        double businessRoom = business.borrowingRoom(Sectors.RETAIL);
        business.rules().set(BankRules.Dial.BUSINESS_LTA, .45);
        check("a tighter business loan-to-assets rule leaves less credit and exposure",
                business.borrowingRoom(Sectors.RETAIL) < businessRoom && businessRoom > 0);
        BankRules mortgage = new BankRules();
        long normal = Mortgage.decide(1, n -> n * 100, 20, 2, .05, n -> 1, mortgage).quantity();
        mortgage.set(BankRules.Dial.MORTGAGE_LTC, .70);
        check("a tighter loan-to-cost cap refuses the insufficient down payment",
                normal == 1 && Mortgage.decide(1, n -> n * 100, 20, 2, .05, n -> 1, mortgage).quantity() == 0);
        mortgage.reset();
        mortgage.set(BankRules.Dial.MORTGAGE_DTI, 2);
        check("a tighter debt-to-income rule refuses excess debt against rent",
                Mortgage.decide(1, n -> n * 100, 20, 2, .05, n -> 1, mortgage).quantity() == 0);

        DebtManager quotes = new DebtManager();
        quotes.setPolicyRate(.15);
        double localQuote = quotes.localWholesaleRate(Bank.WHOLESALE_TERM_MONTHS);
        check("foreign local-money term funding cannot use a cheap USD short rate",
                localQuote >= quotes.getPolicyRate() && localQuote > quotes.foreignRate());

        DebtManager collateralBook = new DebtManager();
        collateralBook.getDebt().add(new ShortTermTBill(100, 3, 0));
        collateralBook.getDebt().add(new LongTermBond(200, 48, 0, .06));
        collateralBook.getDebt().add(LongTermBond.callable(300, 60, 0, .07, 24));
        collateralBook.getDebt().add(new LongTermBond(400, 24, 0, .04, true));
        collateralBook.getDebt().get(1).householdPrincipal = 80;
        for (double rate : new double[]{.01, .05, .15}) {
            collateralBook.setPolicyRate(rate);
            double separate = 0;
            for (Debt paper : collateralBook.getDebt()) {
                double face = paper.getOustandingPrincipal(), held = paper.bankPrincipal();
                if (face > 0 && held > 0)
                    separate += Math.max(0, collateralBook.marketValue(paper)) * Math.min(1, held / face);
            }
            check("one-read collateral curve keeps each paper's price to the bit at " + rate,
                    separate > 0 && Double.doubleToLongBits(separate)
                            == Double.doubleToLongBits(collateralBook.bankCollateralValue()));
        }

        out.println("--- 4. every default is the pre-dial Part 1 rule ---");
        BankRules defaults = new BankRules();
        // The values used by the measured Part 1 city, before exposing the controls.
        // This deliberately fences the numbers as well as checking the owner's declaration.
        double[] measuredDefaults = {.0025, 1, .08, 0, .03, 0, .85, 0, .90};
        check("the dial list matches the measured Part 1 rules", BankRules.Dial.values().length == measuredDefaults.length);
        for (BankRules.Dial dial : BankRules.Dial.values())
            check(dial.label + " starts at the measured Part 1 value",
                    defaults.get(dial) == dial.initial && defaults.get(dial) == measuredDefaults[dial.ordinal()]);
        check("the additional mortgage income limit starts off",
                defaults.get(BankRules.Dial.MORTGAGE_DTI) == 0);

        out.println("--- 5. dials and financing survive the real save paths ---");
        check("new fields round-trip and a save with no fields reads the defaults", saveChecks());

        String source = System.getProperty("cbs.cbk3.evidence", "");
        if (!source.isBlank()) evidence(Path.of(source));
        out.printf("BankRulesCheck: %d failures%n", fails);
        System.exit(fails == 0 ? 0 : 1);
    }
}

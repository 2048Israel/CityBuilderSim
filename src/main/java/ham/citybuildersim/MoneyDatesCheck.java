package ham.citybuildersim;

import com.google.gson.Gson;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

/**
 * Proves Canada's dated Money: both gold episodes, the Advance Rate, the Bank of Canada, and a reserve exhaustion that survives a load.
 *
 * WHY. PolicyDates declared Money's dates but nothing used them. These towns actually cross every boundary; no fixture moves a boundary
 * or opens a lever early. The empty-vault town causes exhaustion, then receives reserves to distinguish remembering an exit from rechecking
 * today's balance. Save files belong only to GameFiles.scratch(), never a player's folder.
 */
public final class MoneyDatesCheck {
    private static int fails;
    private static boolean exhaustedTwinMatches;
    private static int auditedMonths, auditFailures, goldMonths, floatingMonths;
    private static final PrintStream OUT = System.out;
    private static final PrintStream QUIET = new PrintStream(OutputStream.nullOutputStream());

    private static void check(String label, boolean ok) {
        if (!ok) fails++;
        OUT.printf("%-106s %s%n", label, ok ? "OK" : "FAIL");
    }

    private static Game town(String name, double reserves) {
        Game g = new Game(GameFiles.scratch(name), Founding.custom(name, Founding.WEALTHY_CASH, reserves,
                WorldEconomy.DEFAULT_MEAN_INFLATION).withUnfitted(true));
        g.run();
        return g;
    }

    private static void step(Game g) {
        g.toggleNextMonth();
        MoneyAudit.Result a = g.getLastMoneyAudit();
        auditedMonths++;
        if (Math.abs(a.residual) > MoneyAudit.tolerance(Math.max(Math.abs(a.before), Math.abs(a.after)))) {
            auditFailures++;
            OUT.println("Money audit at " + g.getMonth() + ": " + a);
        }
        if (g.getForeignAccounts().getRegime() == ForeignAccounts.Regime.PEG) goldMonths++;
        if (g.getForeignAccounts().getRegime() == ForeignAccounts.Regime.FLOAT) floatingMonths++;
    }

    private static void through(Game g, int month) {
        while (g.getMonth() < month) step(g);
    }

    /** The loader's real JSON door, on scratch autosave paths, with no player slot written. */
    private static Game reload(Game g, String label) throws Exception {
        DataSave saved = g.prepareSaveForChecks(0, label).data();
        Files.createDirectories(g.getGameFiles().saveFile(0).getParent());
        Files.writeString(g.getGameFiles().saveFile(0), new Gson().toJson(saved), StandardCharsets.UTF_8);
        Game back = new Game(g.getGameFiles());
        back.loadGameSave(0);
        if (back.getLoadFailure() != null) throw new AssertionError(back.getLoadFailure());
        return back;
    }

    /** SaveFileCheck calls the same caused exhaustion through DataSave, JSON, and the ordinary load path. */
    static boolean saveChecks() throws Exception {
        PrintStream before = System.out;
        System.setOut(QUIET);
        try {
            Game g = town("money-date-save", 0);
            step(g);
            boolean left = g.getForeignAccounts().getRegime() == ForeignAccounts.Regime.FLOAT;
            // An endowment is fixture setup, not a player intervention: the empty vault already caused its exit above.
            g.getForeignAccounts().buyReserves(Founding.WEALTHY_RESERVE_USD);
            g.getForeignAccounts().forgetDealing();
            DataSave stock = g.prepareSaveForChecks(0, "exhausted gold").data();
            Game back = reload(g, "exhausted gold");
            boolean sameStock = Arrays.equals(stock.getForeignAccounts(), back.getForeignAccounts().toSaveArray());
            step(g);
            step(back);
            if (!sameStock) difference("after load", stock.getForeignAccounts(), back.getForeignAccounts().toSaveArray());
            exhaustedTwinMatches = Arrays.equals(g.getForeignAccounts().toSaveArray(), back.getForeignAccounts().toSaveArray());
            if (!exhaustedTwinMatches)
                difference("after the next month", g.getForeignAccounts().toSaveArray(), back.getForeignAccounts().toSaveArray());
            if (!left) OUT.println("FAIL fixture: the empty vault did not leave gold");
            return left && sameStock && back.getMonth() == g.getMonth()
                    && back.getForeignAccounts().getRegime() == ForeignAccounts.Regime.FLOAT
                    && g.getForeignAccounts().getRegime() == ForeignAccounts.Regime.FLOAT
                    && g.getDebtManager().getPolicyRate() == back.getDebtManager().getPolicyRate();
        } finally { System.setOut(before); }
    }

    private static void difference(String when, double[] a, double[] b) {
        for (int i = 0; i < Math.min(a.length, b.length); i++)
            if (Double.doubleToLongBits(a[i]) != Double.doubleToLongBits(b[i])) OUT.printf("%s: foreign save slot %d: %.17g != %.17g%n", when, i, a[i], b[i]);
    }

    /** ReadPathCheck holds the saved rule still while its public readers are asked repeatedly. */
    static boolean readChecks() throws Exception {
        PrintStream before = System.out;
        System.setOut(QUIET);
        try {
            Game g = town("money-date-read", Founding.WEALTHY_RESERVE_USD);
            step(g);
            double[] stock = g.getForeignAccounts().toSaveArray();
            double rate = g.getDebtManager().getPolicyRate();
            for (int i = 0; i < 20; i++) {
                g.isLeverOpen(PolicyDates.Lever.POLICY_RATE);
                g.isLeverOpen(PolicyDates.Lever.CENTRAL_BANK);
                g.leverOpeningMonth(PolicyDates.Lever.CENTRAL_BANK);
                g.getForeignAccounts().regimeWords();
                g.getForeignAccounts().getRate();
                g.getDebtManager().getPolicyRate();
            }
            return Arrays.equals(stock, g.getForeignAccounts().toSaveArray()) && rate == g.getDebtManager().getPolicyRate();
        } finally { System.setOut(before); }
    }

    public static void main(String[] args) throws Exception {
        System.setOut(QUIET);
        try {
            OUT.println("--- 1. gold to August 1914, with no policy rate ---");
            Game g = town("money-dates", Founding.MAX_RESERVE_USD);
            DebtManager rates = g.getDebtManager();
            ForeignAccounts fx = g.getForeignAccounts();
            check("January 1900 starts held at par by the vault", fx.getRegime() == ForeignAccounts.Regime.PEG
                    && fx.getRate() == ForeignAccounts.OPENING_RATE);
            rates.takeTheDial(DebtManager.MAX_POLICY_RATE);
            rates.setAutopilot(true);
            g.setCurrencyRegime(ForeignAccounts.Regime.FLOAT);
            check("before the Finance Act, the player's rate and the rule are refused and gold stays in force",
                    rates.getPolicyRate() == rates.worldRate() && !rates.isAutopilot() && fx.getRegime() == ForeignAccounts.Regime.PEG);
            int advance = PolicyDates.openingMonth(PolicyDates.Lever.POLICY_RATE);
            through(g, advance - 1);
            check("the founded town actually reaches July 1914 still held at par, without a doubt break", g.getMonth() == advance - 1
                    && fx.getRate() == ForeignAccounts.OPENING_RATE && fx.getRegime() == ForeignAccounts.Regime.PEG
                    && rates.getPolicyRate() == rates.worldRate());
            boolean heldBeforeWar = fx.getRegime() == ForeignAccounts.Regime.PEG && !g.isLeverOpen(PolicyDates.Lever.POLICY_RATE);
            step(g);
            check("crossing August 1914 floats the dollar and opens the Advance Rate", g.getMonth() == advance && heldBeforeWar
                    && fx.getRegime() == ForeignAccounts.Regime.FLOAT && g.isLeverOpen(PolicyDates.Lever.POLICY_RATE));
            double manual = DebtManager.MAX_POLICY_RATE / 10;
            rates.takeTheDial(manual);
            rates.setAutopilot(true);
            rates.setInflationTarget(DebtManager.MAX_INFLATION_TARGET);
            g.getCentralBank().setTargetShare(CentralBank.MAX_QE_SHARE);
            g.setCurrencyRegime(ForeignAccounts.Regime.LEAN);
            check("the Advance Rate takes a setting while the rest of Money remains shut", rates.getPolicyRate() == manual
                    && !rates.isAutopilot() && rates.getInflationTarget() == DebtManager.DEFAULT_INFLATION_TARGET
                    && g.getCentralBank().getTargetShare() == 0 && fx.getRegime() == ForeignAccounts.Regime.FLOAT);
            step(g);
            check("the chosen Advance Rate survives the next month", rates.getPolicyRate() == manual);
            Game advanceBack = reload(g, "advance rate");
            check("the Advance Rate survives the ordinary load", advanceBack.getDebtManager().getPolicyRate() == manual
                    && advanceBack.getForeignAccounts().getRegime() == ForeignAccounts.Regime.FLOAT);

            OUT.println("--- 2. July 1926 returns to par; September 1931 floats again ---");
            through(g, PolicyDates.GOLD_RETURN - 1);
            check("June 1926 remains a float in the same founded city", fx.getRegime() == ForeignAccounts.Regime.FLOAT);
            step(g);
            check("crossing July 1926 returns to the old par and world interest", g.getMonth() == PolicyDates.GOLD_RETURN
                    && fx.getRegime() == ForeignAccounts.Regime.PEG && fx.getRate() == ForeignAccounts.OPENING_RATE
                    && rates.getPolicyRate() == rates.worldRate());
            rates.takeTheDial(manual);
            check("the returned gold fixes interest to the world even though the Advance Rate's date has passed", rates.getPolicyRate() == rates.worldRate());
            Game goldBack = reload(g, "returned gold");
            check("the second gold episode crosses JSON intact", Arrays.equals(fx.toSaveArray(), goldBack.getForeignAccounts().toSaveArray()));
            through(g, PolicyDates.GOLD_END - 1);
            check("August 1931 is still on gold at par", fx.getRegime() == ForeignAccounts.Regime.PEG && fx.getRate() == ForeignAccounts.OPENING_RATE);
            step(g);
            check("crossing the final gold date floats again", g.getMonth() == PolicyDates.GOLD_END && fx.getRegime() == ForeignAccounts.Regime.FLOAT);

            OUT.println("--- 3. March 1935 opens the Bank of Canada as data, never a discovery ---");
            check("a law find cannot bring either Money date forward", !g.getEconomyManager().getTaxPolicy().bringForward(PolicyDates.Lever.CENTRAL_BANK, g.getMonth())
                    && !g.getEconomyManager().getTaxPolicy().bringForward(PolicyDates.Lever.POLICY_RATE, 1));
            int bank = PolicyDates.openingMonth(PolicyDates.Lever.CENTRAL_BANK);
            through(g, bank - 1);
            boolean bankWasShut = !g.isLeverOpen(PolicyDates.Lever.CENTRAL_BANK) && !rates.isAutopilot()
                    && rates.getInflationTarget() == DebtManager.DEFAULT_INFLATION_TARGET && g.getCentralBank().getTargetShare() == 0;
            check("February 1935 keeps the Bank and its controls closed", bankWasShut);
            step(g);
            rates.setAutopilot(true);
            rates.setInflationTarget(DebtManager.MAX_INFLATION_TARGET);
            g.getCentralBank().setTargetShare(CentralBank.MAX_QE_SHARE);
            g.setCurrencyRegime(ForeignAccounts.Regime.LEAN);
            check("crossing March 1935 opens the rule, target, holdings and currency regime", g.getMonth() == bank && bankWasShut
                    && g.isLeverOpen(PolicyDates.Lever.CENTRAL_BANK) && rates.isAutopilot()
                    && rates.getInflationTarget() == DebtManager.MAX_INFLATION_TARGET
                    && g.getCentralBank().getTargetShare() == CentralBank.MAX_QE_SHARE && fx.getRegime() == ForeignAccounts.Regime.LEAN);
            Game bankBack = reload(g, "bank open");
            check("Money's opened settings cross a save", bankBack.getDebtManager().isAutopilot()
                    && bankBack.getDebtManager().getInflationTarget() == rates.getInflationTarget()
                    && bankBack.getCentralBank().getTargetShare() == g.getCentralBank().getTargetShare());
            step(g);
            check("after March 1935 the historical switch leaves the chosen regime alone", fx.getRegime() == ForeignAccounts.Regime.LEAN);

            OUT.println("--- 4. empty reserves cause an early exit and the exit survives next month and a load ---");
            check("an exhausted episode stays left even after reserves return, identically after JSON and a month", saveChecks());
            check("the exhausted-gold city's whole foreign-account twin matches after the next month", exhaustedTwinMatches);
            check("reading Money never mutates its saved rule or inherited rate", readChecks());
            check("every played month conserves money to MoneyAudit's tolerance, across both gold and floating months",
                    auditedMonths > PolicyDates.openingMonth(PolicyDates.Lever.CENTRAL_BANK) && goldMonths > 0 && floatingMonths > 0 && auditFailures == 0);
            OUT.printf("Caused months: %d audited, %d on dated gold, %d floating; audit failures %d%n", auditedMonths, goldMonths, floatingMonths, auditFailures);
        } finally { System.setOut(OUT); }
        OUT.println("MoneyDatesCheck: " + fails + " failures");
        System.exit(fails == 0 ? 0 : 1);
    }
}

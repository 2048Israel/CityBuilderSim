package ham.citybuildersim;

import com.google.gson.Gson;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

/**
 * Proves Canada's dated gold in a city founded the 1900 way: both episodes, Money the player's from the first month, and a reserve exhaustion
 * that survives a load.
 *
 * WHY. PolicyDates declared Money's dates but nothing used them (R0f). Since OP1 (2026-11-19) the Advance Rate and the Bank of Canada are open
 * from the founding, as every lever is, and the gold years stay history's (call 2): the town is founded on gold, floats in August 1914 if still
 * on it, is put back at par in July 1926 if floating, and floats in September 1931; a player who leaves gold leaves it for the episode, and the
 * Bank's date of March 1935 passes as data. These towns actually cross every boundary; no fixture moves a boundary. The empty-vault town causes exhaustion, then receives reserves to distinguish remembering an exit from rechecking
 * today's balance. Save files belong only to GameFiles.scratch(), never a player's folder.
 */
public final class MoneyDatesCheck {
    private static int fails;
    private static boolean exhaustedTwinMatches;
    /** FB3 (R0f-S): the loaded shops' spending seam is the saved one, to the bit; and the next month's rent, cash and savings are the twin's. */
    private static boolean exhaustedWantedNothing, exhaustedSeamCarried, exhaustedMonthMatches;
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
            // FB3 (R0f-S): the second month of a founding is one its shops want nothing in (wantedSpend 0); the load's rebuild strikes a
            // want of its own, and the load put the saved one back only when it was over zero.
            ham.citybuildersim.sectors.Retail shopsSaved = g.getSectors().retail(), shopsBack = back.getSectors().retail();
            exhaustedWantedNothing = shopsSaved.getWantedSpend() == 0;
            exhaustedSeamCarried = Double.doubleToLongBits(shopsSaved.getWantedSpend()) == Double.doubleToLongBits(shopsBack.getWantedSpend())
                    && Double.doubleToLongBits(shopsSaved.getSpendingCapacity()) == Double.doubleToLongBits(shopsBack.getSpendingCapacity());
            if (!exhaustedSeamCarried) OUT.printf("after load: Retail wantedSpend %.17g != %.17g, spendingCapacity %.17g != %.17g%n",
                    shopsSaved.getWantedSpend(), shopsBack.getWantedSpend(), shopsSaved.getSpendingCapacity(), shopsBack.getSpendingCapacity());
            step(g);
            step(back);
            if (!sameStock) difference("after load", stock.getForeignAccounts(), back.getForeignAccounts().toSaveArray());
            exhaustedTwinMatches = Arrays.equals(g.getForeignAccounts().toSaveArray(), back.getForeignAccounts().toSaveArray());
            exhaustedMonthMatches = g.getSectors().realEstate().getRentIncome() == back.getSectors().realEstate().getRentIncome()
                    && g.getCash() == back.getCash() && g.getHouseholdDeposits() == back.getHouseholdDeposits();
            if (!exhaustedMonthMatches) OUT.printf("after the next month: rent %.17g != %.17g, cash %.17g != %.17g, savings %.17g != %.17g%n",
                    g.getSectors().realEstate().getRentIncome(), back.getSectors().realEstate().getRentIncome(), g.getCash(), back.getCash(),
                    g.getHouseholdDeposits(), back.getHouseholdDeposits());
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
                g.getForeignAccounts().isCanadaGold();
                g.getDebtManager().isTied();
                g.getForeignAccounts().regimeWords();
                g.getForeignAccounts().getRate();
                g.getDebtManager().getPolicyRate();
            }
            return Arrays.equals(stock, g.getForeignAccounts().toSaveArray()) && rate == g.getDebtManager().getPolicyRate();
        } finally { System.setOut(before); }
    }

    /** REV-R0f-1: exhaustion of the returned gold must release the dial in that very month, before any reload. */
    private static boolean secondExhaustion() throws Exception {
        Game g = town("money-second-exhaustion", 0);
        ForeignAccounts fx = g.getForeignAccounts();
        through(g, PolicyDates.GOLD_RETURN - 1);
        int breaks = fx.getBreaks();
        boolean premise = g.getMonth() == PolicyDates.GOLD_RETURN - 1 && breaks > 0
                && fx.getRegime() == ForeignAccounts.Regime.FLOAT && fx.getReservesUsd() == 0;
        step(g);
        boolean exhaustedAgain = g.getMonth() == PolicyDates.GOLD_RETURN && fx.getBreaks() == breaks + 1
                && fx.getLastBreakCause() == ForeignAccounts.BreakCause.VAULT && fx.getRegime() == ForeignAccounts.Regime.FLOAT;
        double before = g.getDebtManager().getPolicyRate();
        double want = before == DebtManager.MAX_POLICY_RATE ? DebtManager.MIN_POLICY_RATE : DebtManager.MAX_POLICY_RATE;
        g.getDebtManager().takeTheDial(want);
        boolean acceptedImmediately = g.getDebtManager().getPolicyRate() == want;
        Game back = reload(g, "second gold exhausted");
        boolean kept = back.getDebtManager().getPolicyRate() == g.getDebtManager().getPolicyRate();
        back.getDebtManager().takeTheDial(want);
        return premise && exhaustedAgain && acceptedImmediately && kept && back.getDebtManager().getPolicyRate() == want;
    }

    public static void main(String[] args) throws Exception {
        System.setOut(QUIET);
        try {
            OUT.println("--- 1. gold to August 1914, with Money the player's from the first month ---");
            Game g = town("money-dates", Founding.MAX_RESERVE_USD);
            DebtManager rates = g.getDebtManager();
            ForeignAccounts fx = g.getForeignAccounts();
            check("January 1900 starts held at par by the vault, its dial tied to the US$", fx.getRegime() == ForeignAccounts.Regime.PEG
                    && fx.isCanadaGold() && fx.getRate() == ForeignAccounts.OPENING_RATE && rates.isTied());
            check("the rule has the dial from the first month (OP1: the Bank is open from the founding)", rates.isAutopilot());
            rates.takeTheDial(DebtManager.MAX_POLICY_RATE);
            step(g);
            check("on gold a hand's rate is taken, and the month's tie puts the dial at the world's", !rates.isAutopilot()
                    && rates.getPolicyRate() == rates.worldRate() && fx.getRegime() == ForeignAccounts.Regime.PEG);
            rates.setAutopilot(true);
            int suspended = PolicyDates.GOLD_SUSPENDED;
            through(g, suspended - 1);
            check("the founded town actually reaches July 1914 still held at par, without a doubt break", g.getMonth() == suspended - 1
                    && fx.getRate() == ForeignAccounts.OPENING_RATE && fx.getRegime() == ForeignAccounts.Regime.PEG && fx.isCanadaGold()
                    && rates.getPolicyRate() == rates.worldRate());
            step(g);
            check("crossing August 1914 floats the dollar", g.getMonth() == suspended && fx.getRegime() == ForeignAccounts.Regime.FLOAT
                    && !fx.isCanadaGold());
            double manual = DebtManager.MAX_POLICY_RATE / 10;
            rates.takeTheDial(manual);
            check("floating, the dial takes a hand's rate", rates.getPolicyRate() == manual && !rates.isAutopilot());
            step(g);
            check("the chosen rate survives the next month", rates.getPolicyRate() == manual);
            Game advanceBack = reload(g, "advance rate");
            check("the chosen rate survives the ordinary load", advanceBack.getDebtManager().getPolicyRate() == manual
                    && advanceBack.getForeignAccounts().getRegime() == ForeignAccounts.Regime.FLOAT);

            OUT.println("--- 2. July 1926 returns a floating dollar to par; September 1931 floats again ---");
            through(g, PolicyDates.GOLD_RETURN - 1);
            check("June 1926 remains a float in the same founded city", fx.getRegime() == ForeignAccounts.Regime.FLOAT);
            step(g);
            check("crossing July 1926 returns to the old par and world interest", g.getMonth() == PolicyDates.GOLD_RETURN
                    && fx.getRegime() == ForeignAccounts.Regime.PEG && fx.isCanadaGold() && fx.getRate() == ForeignAccounts.OPENING_RATE
                    && rates.getPolicyRate() == rates.worldRate());
            rates.takeTheDial(manual);
            step(g);
            check("the returned gold ties interest to the world's whatever the hand sets", rates.getPolicyRate() == rates.worldRate());
            Game goldBack = reload(g, "returned gold");
            check("the second gold episode crosses JSON intact", Arrays.equals(fx.toSaveArray(), goldBack.getForeignAccounts().toSaveArray()));
            through(g, PolicyDates.GOLD_END - 1);
            check("August 1931 is still on gold at par", fx.getRegime() == ForeignAccounts.Regime.PEG && fx.getRate() == ForeignAccounts.OPENING_RATE);
            step(g);
            check("crossing the final gold date floats again", g.getMonth() == PolicyDates.GOLD_END && fx.getRegime() == ForeignAccounts.Regime.FLOAT
                    && !fx.isCanadaGold());

            OUT.println("--- 3. the player leaves gold when they like, and March 1935 passes as data ---");
            Game early = town("money-left-gold", Founding.MAX_RESERVE_USD);
            ForeignAccounts earlyFx = early.getForeignAccounts();
            early.getDebtManager().setInflationTarget(DebtManager.MAX_INFLATION_TARGET);
            early.getCentralBank().setTargetShare(CentralBank.MAX_QE_SHARE);
            early.setCurrencyRegime(ForeignAccounts.Regime.BAND);
            check("in January 1900 the target, the holdings and the regime take settings, and the band leaves gold",
                    early.getDebtManager().getInflationTarget() == DebtManager.MAX_INFLATION_TARGET
                            && early.getCentralBank().getTargetShare() == CentralBank.MAX_QE_SHARE
                            && earlyFx.getRegime() == ForeignAccounts.Regime.BAND && !earlyFx.isCanadaGold());
            step(early);
            check("...and the next month leaves the band as chosen", earlyFx.getRegime() == ForeignAccounts.Regime.BAND && !earlyFx.isCanadaGold());
            double par = ForeignAccounts.OPENING_RATE;
            earlyFx.hearMoneyMonth(PolicyDates.GOLD_SUSPENDED, par);
            check("the suspension passes a dollar already off gold by", earlyFx.getRegime() == ForeignAccounts.Regime.BAND);
            earlyFx.hearMoneyMonth(PolicyDates.GOLD_RETURN, par);
            check("...and so does the return: a band the player chose is not put back on gold", earlyFx.getRegime() == ForeignAccounts.Regime.BAND
                    && !earlyFx.isCanadaGold());
            int bank = PolicyDates.canadaMonth(PolicyDates.Lever.CENTRAL_BANK);
            g.setCurrencyRegime(ForeignAccounts.Regime.LEAN);
            rates.setAutopilot(true);
            rates.setInflationTarget(DebtManager.MAX_INFLATION_TARGET);
            g.getCentralBank().setTargetShare(CentralBank.MAX_QE_SHARE);
            through(g, bank);
            check("crossing March 1935 leaves the regime, the rule, the target and the holdings as set", g.getMonth() == bank
                    && fx.getRegime() == ForeignAccounts.Regime.LEAN && rates.isAutopilot() && rates.getInflationTarget() == DebtManager.MAX_INFLATION_TARGET
                    && g.getCentralBank().getTargetShare() == CentralBank.MAX_QE_SHARE);
            Game bankBack = reload(g, "bank open");
            check("Money's settings cross a save", bankBack.getDebtManager().isAutopilot()
                    && bankBack.getDebtManager().getInflationTarget() == rates.getInflationTarget()
                    && bankBack.getCentralBank().getTargetShare() == g.getCentralBank().getTargetShare()
                    && bankBack.getForeignAccounts().getRegime() == ForeignAccounts.Regime.LEAN);
            step(g);
            check("after March 1935 the historical switch leaves the chosen regime alone", fx.getRegime() == ForeignAccounts.Regime.LEAN);

            OUT.println("--- 4. empty reserves cause an early exit and the exit survives next month and a load ---");
            check("an exhausted episode stays left even after reserves return, identically after JSON and a month", saveChecks());
            check("fixture: the exhausted-gold city is saved in a month its shops want nothing in", exhaustedWantedNothing);
            check("the exhausted-gold city comes back with the shops' saved want and means, to the bit, a want of nothing included", exhaustedSeamCarried);
            check("the exhausted-gold city's whole foreign-account twin matches after the next month", exhaustedTwinMatches);
            check("...and after the next month its rent, the city's cash and the households' savings are its twin's", exhaustedMonthMatches);
            check("second-episode exhaustion releases the Advance Rate in the breaking month, identically before and after JSON", secondExhaustion());
            check("reading Money never mutates its saved rule or inherited rate", readChecks());
            check("every played month conserves money to MoneyAudit's tolerance, across both gold and floating months",
                    auditedMonths > PolicyDates.canadaMonth(PolicyDates.Lever.CENTRAL_BANK) && goldMonths > 0 && floatingMonths > 0 && auditFailures == 0);
            OUT.printf("Caused months: %d audited, %d on dated gold, %d floating; audit failures %d%n", auditedMonths, goldMonths, floatingMonths, auditFailures);
        } finally { System.setOut(OUT); }
        OUT.println("MoneyDatesCheck: " + fails + " failures");
        System.exit(fails == 0 ? 0 : 1);
    }
}

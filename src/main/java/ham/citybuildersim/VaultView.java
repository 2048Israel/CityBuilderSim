package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;

/**
 * The vault in eight (WN6; docs/studies/spec-nations.md 8, the Trade screen's The reserves with the eight moneys in a table): what the vault holds of each
 * of the eight moneys of the IMF's COFER (WorldMoneys.EIGHT's order), what each is worth, how each moved against the US$ this month, its central bank's
 * policy rate, and what the city owes in it. A pure model of what a screen draws, built from the game's public getters alone.
 * <p>
 * WHY. The vault keeps a balance in each of the eight (ForeignAccounts, THE VAULT IN EIGHT) but the page said only the sum, the sum's revaluation by the
 * rate and the biggest cross mover. A table of the eight needs each money's own figures from one place, so that a screen does not divide a balance by a rate
 * of its own choosing: the worths add to getReservesUsd() to the bit (the same additions in the same order), the shares to 1, and the month's move is the
 * history's - read from the saved series, so a city just loaded shows it.
 * <p>
 * UNITS. Money in the model is thousands: held is thousands of the money's own units, worthUsd thousands of US$, worthD thousands of the city's money
 * (at the rate of the day, D$ a US$). move is a share (a euro up 2% against the US$ is +0.02; the US$ itself 0), NaN where the history does not hold two
 * months of it. policyRate is a fraction a year, NaN with no policy rates. owedOwn is what the city owes in the money (DebtManager.getForeignPrincipalIn,
 * CD8: the principal of every foreign piece written in it), thousands of its own units, and owedUsd that at the vault's rate for it (NaN with owed and no rate).
 */
public record VaultView(List<Row> rows, double worthUsd, double worthD, double dollarsAUsd, double crossUsd, ForeignAccounts.CrossDriver crossDriver,
                        double revaluationD) {

    /** One of the eight. rated says the vault has a rate for it this month (a purchase can go into it); share is of the vault's US$ worth (0 for an empty vault). */
    public record Row(int index, String code, String name, String mark, double held, boolean rated, double usdPerUnit, double worthUsd, double worthD,
                      double share, double move, double policyRate, double owedOwn, double owedUsd) { }

    /**
     * The vault as the eight table has it. worthUsd is the sum of the rows' worths and equals ForeignAccounts.getReservesUsd(); worthD that at the rate
     * (ForeignAccounts.getReserves()); crossUsd the month's move of the vault across the moneys in US$ (getLastVaultCrossUsd), crossDriver the money that
     * moved it most (null when none did) and revaluationD the month's move of the whole vault in D$ (getLastVaultRevaluation: the currency's, the crosses'
     * included).
     */
    public static VaultView of(Game g) {
        ForeignAccounts fx = g.getForeignAccounts();
        PolicyRates policy = g.getPolicyRates();
        DebtManager debts = g.getDebtManager();
        HistorySave history = g.getHistorySave();
        double rate = fx.getRate();
        // the same additions in the same order as getReservesUsd(): the US$'s balance first, then each of the others at its day's rate
        double total = fx.getHoldingUsd(0);
        for (int k = 1; k < ForeignAccounts.MONEYS; k++) total += fx.getHoldingUsd(k);
        List<Row> rows = new ArrayList<>(ForeignAccounts.MONEYS);
        for (int k = 0; k < ForeignAccounts.MONEYS; k++) {
            String code = WorldMoneys.EIGHT.get(k);
            double usd = fx.getHoldingUsd(k), owed = debts.getForeignPrincipalIn(code), usdPer = fx.getMoneyUsd(k);
            rows.add(new Row(k, code, ForeignAccounts.MONEY_NAME[k], ForeignAccounts.MONEY_MARK[k], fx.getHolding(k), fx.hasRate(k), usdPer, usd, usd * rate,
                    total > 0 ? usd / total : 0, move(history, code), policy == null ? Double.NaN : policy.rate(k), owed, owed == 0 ? 0 : owed * usdPer));
        }
        return new VaultView(List.copyOf(rows), total, fx.getReserves(), rate, fx.getLastVaultCrossUsd(), fx.getLastVaultCrossDriver(), fx.getLastVaultRevaluation());
    }

    /**
     * A money's move against the US$ in the last month: the history's series of its units a US$ (HistorySave.worldRateKey, "fxEUR": a rise is a weaker
     * money), the month before's over this month's, less one - the share more US$ a unit buys, which is the direction ForeignAccounts.CrossDriver's move
     * has. 0 for the US$; NaN where the history does not hold two months of it, or a figure is not above 0 (a city with no world file, or one whose moneys came late).
     */
    static double move(HistorySave history, String code) {
        if (Nations.DOLLAR.equals(code)) return 0;
        double[] tail = history.alignedTail(HistorySave.worldRateKey(code), 2);
        if (tail.length < 2 || !(tail[0] > 0) || !(tail[1] > 0)) return Double.NaN;
        return tail[0] / tail[1] - 1;
    }
}

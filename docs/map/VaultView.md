# VaultView.java - 66 lines · 2 methods · 0 constants · model

`ham/citybuildersim/VaultView.java` - generated 2026-11-12 by CodeMap; line numbers are as of that run.

> The vault in eight, and gold (WN6, GD2; docs/studies/spec-nations.md 8, the Trade screen's The reserves with the eight moneys in a table): what the vault holds of
> each of the eight moneys of the IMF's COFER (WorldMoneys.EIGHT's order) and of gold, the ninth (ForeignAccounts.GOLD, XAU), what each is worth, how each moved
> against the US$ this month, its central bank's policy rate (gold has none: NaN), and what the city owes in it (gold: nothing, the city's paper is in the eight).
> A pure model of what a screen draws, built from the game's public getters alone.
> <p>
> WHY. The vault keeps a balance in each of the eight (ForeignAccounts, THE VAULT IN EIGHT) but the page said only the sum, the sum's revaluation by the
> rate and the biggest cross mover. A table of the eight needs each money's own figures from one place, so that a screen does not divide a balance by a rate
> of its own choosing: the worths add to getReservesUsd() to the bit (the same additions in the same order), the shares to 1, and the month's move is the
> history's - read from the saved series, so a city just loaded shows it.
> <p>
> UNITS. Money in the model is thousands: held is thousands of the money's own units, worthUsd thousands of US$, worthD thousands of the city's money
> (at the rate of the day, D$ a US$). move is a share (a euro up 2% against the US$ is +0.02; the US$ itself 0), NaN where the history does not hold two
> months of it - and gold's is the price's own (Game.getGoldMove: pure, so a city just loaded shows it). policyRate is a fraction a year, NaN with no policy rates. owedOwn is what the city owes in the money (DebtManager.getForeignPrincipalIn,
> CD8: the principal of every foreign piece written in it), thousands of its own units, and owedUsd that at the vault's rate for it (NaN with owed and no rate).

**Uses:** [ForeignAccounts](ForeignAccounts.md) (10), [HistorySave](HistorySave.md) (3), [Game](Game.md) (1), [PolicyRates](PolicyRates.md) (1), [DebtManager](DebtManager.md) (1), [Nations](Nations.md) (1)

**Used by (2):** [PartnersCheck](PartnersCheck.md), [VaultTable](VaultTable.md)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 22 | 45 | **type** `public record VaultView(List<Row> rows, double worthUsd, double worthD, double dollarsAUsd, double crossUsd...` | The vault in eight, and gold (WN6, GD2; docs/studies/spec-nations.md 8, the Trade screen's The reserves with the eight moneys in a table): what the vault holds of each of the eight moneys of the IMF's COFER (WorldMone... |
| 26 | 2 | **type** `public record Row(int index, String code, String name, String mark, double held, boolean rated, double usdP...` | One of the nine (the eight, then gold). |
| 35 | 19 | `public static VaultView of(Game g)` | The vault as the table has it: ForeignAccounts.MONEYS rows, the eight and then gold. |
| 60 | 6 | `static double move(HistorySave history, String code)` | A money's move against the US$ in the last month: the history's series of its units a US$ (HistorySave.worldRateKey, "fxEUR": a rise is a weaker money), the month before's over this month's, less one - the share more ... |


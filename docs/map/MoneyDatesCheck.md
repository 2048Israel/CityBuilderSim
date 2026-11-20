# MoneyDatesCheck.java - 259 lines · 10 methods · 2 constants · harnesses

`ham/citybuildersim/MoneyDatesCheck.java` - generated 2026-11-19 by CodeMap; line numbers are as of that run.

> Proves Canada's dated gold in a city founded the 1900 way: both episodes, Money the player's from the first month, and a reserve exhaustion
> that survives a load.
> 
> WHY. PolicyDates declared Money's dates but nothing used them (R0f). Since OP1 (2026-11-19) the Advance Rate and the Bank of Canada are open
> from the founding, as every lever is, and the gold years stay history's (call 2): the town is founded on gold, floats in August 1914 if still
> on it, is put back at par in July 1926 if floating, and floats in September 1931; a player who leaves gold leaves it for the episode, and the
> Bank's date of March 1935 passes as data. These towns actually cross every boundary; no fixture moves a boundary. The empty-vault town causes exhaustion, then receives reserves to distinguish remembering an exit from rechecking
> today's balance. Save files belong only to GameFiles.scratch(), never a player's folder.

**Uses:** [ForeignAccounts](ForeignAccounts.md) (34), [Game](Game.md) (19), [PolicyDates](PolicyDates.md) (14), [DebtManager](DebtManager.md) (10), [Founding](Founding.md) (6), [CentralBank](CentralBank.md) (4), [MoneyAudit](MoneyAudit.md) (2), [DataSave](DataSave.md) (2), [GameFiles](GameFiles.md) (1), [WorldEconomy](WorldEconomy.md) (1), [Retail](Retail.md) (1)

**Used by (2):** [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 26 | `MoneyDatesCheck.OUT` | `System.out` |  |
| 27 | `MoneyDatesCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 21 | `private static int fails` |  |
| 22 | `private static boolean exhaustedTwinMatches` |  |
| 24 | `private static boolean exhaustedWantedNothing, exhaustedSeamCarried, exhaustedMonthMatches` | FB3 (R0f-S): the loaded shops' spending seam is the saved one, to the bit; and the next month's rent, cash and savings are the twin's. |
| 25 | `private static int auditedMonths, auditFailures, goldMonths, floatingMonths` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 20 | 240 | **type** `public final class MoneyDatesCheck` | Proves Canada's dated gold in a city founded the 1900 way: both episodes, Money the player's from the first month, and a reserve exhaustion that survives a load. |
| 29 | 4 | `private static void check(String label, boolean ok)` |  |
| 34 | 6 | `private static Game town(String name, double reserves)` |  |
| 41 | 11 | `private static void step(Game g)` |  |
| 53 | 3 | `private static void through(Game g, int month)` |  |
| 58 | 9 | `private static Game reload(Game g, String label) throws Exception` | The loader's real JSON door, on scratch autosave paths, with no player slot written. |
| 69 | 39 | `static boolean saveChecks() throws Exception` | SaveFileCheck calls the same caused exhaustion through DataSave, JSON, and the ordinary load path. |
| 109 | 4 | `private static void difference(String when, double[] a, double[] b)` |  |
| 115 | 18 | `static boolean readChecks() throws Exception` | ReadPathCheck holds the saved rule still while its public readers are asked repeatedly. |
| 135 | 19 | `private static boolean secondExhaustion() throws Exception` | REV-R0f-1: exhaustion of the returned gold must release the dial in that very month, before any reload. |
| 155 | 104 | `public static void main(String[] args) throws Exception` |  |


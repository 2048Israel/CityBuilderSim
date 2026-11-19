# MoneyDatesCheck.java - 246 lines · 10 methods · 2 constants · harnesses

`ham/citybuildersim/MoneyDatesCheck.java` - generated 2026-11-19 by CodeMap; line numbers are as of that run.

> Proves Canada's dated Money: both gold episodes, the Advance Rate, the Bank of Canada, and a reserve exhaustion that survives a load.
> 
> WHY. PolicyDates declared Money's dates but nothing used them. These towns actually cross every boundary; no fixture moves a boundary
> or opens a lever early. The empty-vault town causes exhaustion, then receives reserves to distinguish remembering an exit from rechecking
> today's balance. Save files belong only to GameFiles.scratch(), never a player's folder.

**Uses:** [ForeignAccounts](ForeignAccounts.md) (30), [PolicyDates](PolicyDates.md) (22), [Game](Game.md) (18), [DebtManager](DebtManager.md) (11), [Founding](Founding.md) (5), [CentralBank](CentralBank.md) (3), [MoneyAudit](MoneyAudit.md) (2), [DataSave](DataSave.md) (2), [GameFiles](GameFiles.md) (1), [WorldEconomy](WorldEconomy.md) (1), [Retail](Retail.md) (1)

**Used by (2):** [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 23 | `MoneyDatesCheck.OUT` | `System.out` |  |
| 24 | `MoneyDatesCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 18 | `private static int fails` |  |
| 19 | `private static boolean exhaustedTwinMatches` |  |
| 21 | `private static boolean exhaustedWantedNothing, exhaustedSeamCarried, exhaustedMonthMatches` | FB3 (R0f-S): the loaded shops' spending seam is the saved one, to the bit; and the next month's rent, cash and savings are the twin's. |
| 22 | `private static int auditedMonths, auditFailures, goldMonths, floatingMonths` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 17 | 230 | **type** `public final class MoneyDatesCheck` | Proves Canada's dated Money: both gold episodes, the Advance Rate, the Bank of Canada, and a reserve exhaustion that survives a load. |
| 26 | 4 | `private static void check(String label, boolean ok)` |  |
| 31 | 6 | `private static Game town(String name, double reserves)` |  |
| 38 | 11 | `private static void step(Game g)` |  |
| 50 | 3 | `private static void through(Game g, int month)` |  |
| 55 | 9 | `private static Game reload(Game g, String label) throws Exception` | The loader's real JSON door, on scratch autosave paths, with no player slot written. |
| 66 | 39 | `static boolean saveChecks() throws Exception` | SaveFileCheck calls the same caused exhaustion through DataSave, JSON, and the ordinary load path. |
| 106 | 4 | `private static void difference(String when, double[] a, double[] b)` |  |
| 112 | 19 | `static boolean readChecks() throws Exception` | ReadPathCheck holds the saved rule still while its public readers are asked repeatedly. |
| 133 | 19 | `private static boolean secondExhaustion() throws Exception` | REV-R0f-1: exhaustion after the Advance Rate's date must release the dial in that very month, before any reload. |
| 153 | 93 | `public static void main(String[] args) throws Exception` |  |


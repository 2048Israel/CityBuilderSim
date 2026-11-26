# BankRulesCheck.java - 251 lines · 5 methods · 2 constants · harnesses

`ham/citybuildersim/BankRulesCheck.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

> The window against collateral and the bank's rules as the player's dials.
> WHY (CBK3): a capital-only equity desk could be financed without limit at
> the window. Numbered sections cause collateral, funding and credit limits;
> the optional evidence run plays a second copy of the supplied autosave.

**Uses:** [BankRules](BankRules.md) (23), [Bank](Bank.md) (12), [GameFiles](GameFiles.md) (10), [Game](Game.md) (8), [MoneyAudit](MoneyAudit.md) (4), [Sectors](Sectors.md) (4), [CentralBank](CentralBank.md) (3), [Mortgage](Mortgage.md) (3), [TempDirs](TempDirs.md) (2), [BusinessDebtManager](BusinessDebtManager.md) (2), [DebtManager](DebtManager.md) (2)

**Used by (1):** [SaveFileCheck](SaveFileCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 16 | `BankRulesCheck.out` | `System.out` |  |
| 17 | `BankRulesCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 15 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 14 | 238 | **type** `public final class BankRulesCheck` | The window against collateral and the bank's rules as the player's dials. |
| 19 | 4 | `static void check(String label, boolean ok)` |  |
| 24 | 5 | `static void silently(Runnable action)` |  |
| 31 | 39 | `static boolean saveChecks() throws Exception` | Real writer and loader, including an otherwise identical save with the new keys absent. |
| 72 | 45 | `static void evidence(Path source) throws Exception` | Reads only the supplied evidence folder; all play and autosaves use a fresh scratch. |
| 118 | 133 | `public static void main(String[] args) throws Exception` |  |


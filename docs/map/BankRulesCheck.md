# BankRulesCheck.java - 837 lines · 18 methods · 2 constants · harnesses

`ham/citybuildersim/BankRulesCheck.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

> The window against collateral and the bank's rules as the player's dials.
> WHY (CBK3): a capital-only equity desk could be financed without limit at
> the window. Numbered sections cause collateral, funding and credit limits;
> the optional evidence run plays a second copy of the supplied autosave.

**Uses:** [CentralBank](CentralBank.md) (35), [BankRules](BankRules.md) (33), [Bank](Bank.md) (31), [MoneyAudit](MoneyAudit.md) (23), [LongTermBond](LongTermBond.md) (19), [GameFiles](GameFiles.md) (17), [Game](Game.md) (16), [DebtManager](DebtManager.md) (14), [LongPlaytest](LongPlaytest.md) (11), [CallTree](CallTree.md) (8), [YieldCurve](YieldCurve.md) (7), [Mortgage](Mortgage.md) (6), [Exchange](Exchange.md) (5), [TempDirs](TempDirs.md) (4), [Sectors](Sectors.md) (4), [Debt](Debt.md) (3), [ShortTermTBill](ShortTermTBill.md) (2), [BusinessDebtManager](BusinessDebtManager.md) (2), [GameVersion](GameVersion.md) (1)

**Used by (2):** [CentralBankCheck](CentralBankCheck.md), [SaveFileCheck](SaveFileCheck.md)

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
| 14 | 824 | **type** `public final class BankRulesCheck` | The window against collateral and the bank's rules as the player's dials. |
| 19 | 4 | `static void check(String label, boolean ok)` |  |
| 24 | 5 | `static void silently(Runnable action)` |  |
| 30 | 1 | **type** `record WindowFixture(Bank bank, CentralBank window)` |  |
| 33 | 1 | `static WindowFixture deferredWindow()` | Native collateral and stable capital; the earlier repayment leaves a real principal claim, not a fresh advance. |
| 35 | 19 | `static WindowFixture deferredWindow(boolean fullCollateral)` |  |
| 55 | 59 | `static void windowInterestLaw()` |  |
| 116 | 15 | `static double[] withoutWindowInterestDue(double[] saved)` | A native format-67 array fixture before CBK3's appended flow, sheet column and receivable stock. |
| 132 | 45 | `static void interestCollateralLaw()` |  |
| 178 | 66 | `static boolean windowInterestSaveChecks() throws Exception` |  |
| 246 | 58 | `static boolean saveChecks() throws Exception` | Real writer and loader, including an otherwise identical save with the new keys absent. |
| 306 | 45 | `static void evidence(Path source) throws Exception` | Reads only the supplied evidence folder; all play and autosaves use a fresh scratch. |
| 352 | 4 | `static boolean coldCollateralSameBits(double a, double b)` |  |
| 358 | 9 | `static int coldCollateralTreeSteps(DebtManager manager) throws ReflectiveOperationException` | Read only the existing cache to prove that excluded horizons were not fitted; no model test API. |
| 369 | 35 | `static DebtManager coldCollateralBook(double policy, int[] volatilityReads, boolean eligible)` | Independent pieces in insertion order: three increasing eligible maxima, then excluded longer pieces. |
| 406 | 19 | `static double coldCollateralReference(DebtManager manager, boolean proveOptionality)` | The old public per-paper valuation path, in its original accumulation order. |
| 426 | 52 | `static void coldCallableCollateral() throws ReflectiveOperationException` |  |
| 479 | 34 | `static void coldCallableTreePrefixes()` |  |
| 515 | 113 | `static void rolloverLaw() throws Exception` | Actual capital actions do not forgive debt; new credit and the next rollover use the live cap. |
| 629 | 208 | `public static void main(String[] args) throws Exception` |  |


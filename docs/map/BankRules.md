# BankRules.java - 73 lines · 10 methods · 3 constants · model

`ham/citybuildersim/BankRules.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

> The city's saved banking rules, shared by lenders and both policy pages.
> WHY (CBK3): the bank's prudential constants were invisible to the player;
> exposing a rule must not quietly change its default or create a second model.

**Uses:** [Bank](Bank.md) (3), [DecisionLog](DecisionLog.md) (3), [CentralBank](CentralBank.md) (1), [Mortgage](Mortgage.md) (1), [BusinessDebtManager](BusinessDebtManager.md) (1)

**Used by (9):** [Bank](Bank.md), [BankRulesCheck](BankRulesCheck.md), [BankRulesPane](BankRulesPane.md), [BusinessDebtManager](BusinessDebtManager.md), [Game](Game.md), [GoldStandardCheck](GoldStandardCheck.md), [LongPlaytest](LongPlaytest.md), [Mortgage](Mortgage.md), [MortgageCheck](MortgageCheck.md)

## Enum constants

| line | constant | says |
|---:|---|---|
| 18 | `BankRules.Dial.WINDOW_SPREAD` |  |
| 20 | `BankRules.Dial.WINDOW_LIMIT` |  |
| 22 | `BankRules.Dial.CAPITAL_MINIMUM` |  |
| 24 | `BankRules.Dial.COUNTERCYCLICAL` |  |
| 26 | `BankRules.Dial.LEVERAGE_MINIMUM` |  |
| 28 | `BankRules.Dial.RESERVE_REQUIREMENT` |  |
| 30 | `BankRules.Dial.MORTGAGE_LTC` |  |
| 32 | `BankRules.Dial.MORTGAGE_DTI` |  |
| 34 | `BankRules.Dial.BUSINESS_LTA` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 10 | `BankRules.COUNTERCYCLICAL_DEFAULT` | `0` | No discretionary countercyclical buffer in the existing rule. |
| 12 | `BankRules.RESERVE_DEFAULT` | `0` | No reserve requirement, matching the Fed since March 2020. |
| 14 | `BankRules.MORTGAGE_DTI_DEFAULT` | `0` | No additional debt-to-annual-income limit in the existing mortgage product. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 37 | `public final String label, explanation` |  |
| 38 | `public final double initial, min, max, step` |  |
| 45 | `private final double[] values` |  |
| 46 | `private transient DecisionLog decisions` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 8 | 66 | **type** `public final class BankRules` | The city's saved banking rules, shared by lenders and both policy pages. |
| 17 | 27 | **type** `public enum Dial` | Saved order: append new rules, never reorder existing ones. |
| 39 | 4 | `Dial(String label, double initial, double min, double max, double step, String explanation)` _(in BankRules.Dial)_ |  |
| 47 | 1 | `public BankRules()` |  |
| 48 | 1 | `public void recordTo(DecisionLog log)` |  |
| 49 | 1 | `public double get(Dial dial)` |  |
| 50 | 6 | `public void set(Dial dial, double value)` |  |
| 56 | 5 | `public static String reading(Dial dial, double value)` |  |
| 62 | 3 | `private static double bounded(Dial dial, double value)` |  |
| 65 | 1 | `public void reset()` |  |
| 66 | 1 | `public double[] toSave()` |  |
| 67 | 5 | `public void restore(double[] saved)` |  |


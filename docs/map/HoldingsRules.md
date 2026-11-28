# HoldingsRules.java - 97 lines · 14 methods · 5 constants · model

`ham/citybuildersim/HoldingsRules.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

> The central bank's saved rules for its holdings of the city's paper: how
> fast the book moves toward the holdings dial, how it shrinks, its share of
> each auction, and the law on buying from the treasury at all.
> WHY (CBK2): Jerus, 2026-11-26: "setting it at 10% doesnt mean it goes to 10%
> right away, just means thats teh cap", and "when you issue debt the ability
> to just issue directly to the central bank? if real life is not like that
> then no". Until CBK2 the pace was QE_SPEED's, a quarter of the larger
> setting a month, never the player's; QT was whatever ran off past the dial
> plus sales at that pace; and the bank bought at issue only its own maturing
> par. BankRules' shape (CBK3): an enum of dials, a saved array, appended in
> order, an old save's missing slots reading the defaults, which are main's.

**Uses:** [DecisionLog](DecisionLog.md) (3)

**Used by (4):** [CentralBank](CentralBank.md), [Game](Game.md), [HoldingsRulesPane](HoldingsRulesPane.md), [PolicyScreen](PolicyScreen.md)

## Enum constants

| line | constant | says |
|---:|---|---|
| 30 | `HoldingsRules.Dial.PACE` |  |
| 33 | `HoldingsRules.Dial.QT_MODE` |  |
| 36 | `HoldingsRules.Dial.AUCTION_SHARE` |  |
| 39 | `HoldingsRules.Dial.DIRECT_PURCHASES` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 18 | `HoldingsRules.PACE_AUTO` | `0` | The pace dial's setting that keeps the old pace: QE_SPEED of the larger of the dial, the setting before it and the share held, a month. |
| 20 | `HoldingsRules.QT_SELL` | `0` | QT_MODE's setting for runoff and sales at market, main's (the default). |
| 22 | `HoldingsRules.QT_RUNOFF` | `1` | ...and for runoff only, capped at the pace (the Fed's 2022 QT). |
| 24 | `HoldingsRules.LAW_OFF` | `0` | The law's setting that bars buying at issue beyond the bank's own rollover (the Federal Reserve Act, s.14(b), since 1935). |
| 26 | `HoldingsRules.LAW_ON` | `1` | ...and the one that allows it (the Bank of Canada's practice). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 43 | `public final String label, explanation` |  |
| 44 | `public final double initial, min, max, step` |  |
| 51 | `private final double[] values` |  |
| 52 | `private transient DecisionLog decisions` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 16 | 82 | **type** `public final class HoldingsRules` | The central bank's saved rules for its holdings of the city's paper: how fast the book moves toward the holdings dial, how it shrinks, its share of each auction, and the law on buying from the treasury at all. |
| 29 | 21 | **type** `public enum Dial` | Saved order: append new rules, never reorder existing ones. |
| 45 | 4 | `Dial(String label, double initial, double min, double max, double step, String explanation)` _(in HoldingsRules.Dial)_ |  |
| 53 | 1 | `public HoldingsRules()` |  |
| 54 | 1 | `public void recordTo(DecisionLog log)` |  |
| 55 | 1 | `public double get(Dial dial)` |  |
| 56 | 6 | `public void set(Dial dial, double value)` |  |
| 64 | 1 | `public double pace()` | The pace a month as a share of the term paper, or PACE_AUTO for the old pace. |
| 66 | 1 | `public boolean runoffOnly()` | True when the book shrinks by runoff only. |
| 68 | 1 | `public boolean directPurchasesAllowed()` | True when the law allows buying at issue beyond the bank's own rollover. |
| 70 | 1 | `public double auctionShare()` | The auction share in force: the dial's under the law, nothing without it. |
| 72 | 10 | `public static String reading(Dial dial, double value)` |  |
| 83 | 6 | `private static double bounded(Dial dial, double value)` |  |
| 89 | 1 | `public void reset()` |  |
| 90 | 1 | `public double[] toSave()` |  |
| 92 | 5 | `public void restore(double[] saved)` | A save's rules; a save from before CBK2 (null) or a shorter array reads the defaults for what it lacks, which move the city as before. |


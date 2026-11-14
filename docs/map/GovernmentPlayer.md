# GovernmentPlayer.java - 73 lines · 2 methods · 8 constants · model

`ham/citybuildersim/GovernmentPlayer.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

> The test player's annual response to the government's wealth, shared by the playtest and its measuring drivers.
> 
> WHY (GV1/GV2, spec-wealth.md 3.2). Scripted income and property tax changes ignored the government's accumulating wealth and fought the
> study's policy rule. This is a player's move, called by LongPlaytest after a successful January; Game never calls it for a live city.
> January has finished before this runs, so this January's newly opened policy levers accept their setters. The prototype acted after
> December instead, before those gates opened. The first eligible January is month 13, with a full year's revenue history.

**Uses:** [TaxPolicy](TaxPolicy.md) (4), [TreasuryFund](TreasuryFund.md) (4), [GovernmentView](GovernmentView.md) (3), [Game](Game.md) (2), [Unemployment](Unemployment.md) (1), [CityCalendar](CityCalendar.md) (1)

**Used by (2):** [GovernmentPlayerCheck](GovernmentPlayerCheck.md), [LongPlaytest](LongPlaytest.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 17 | `GovernmentPlayer.PROFIT_STEP` | `.05` | The prototype's annual profit-tax move: five percentage points, small enough to give each year's response a chance to work. |
| 19 | `GovernmentPlayer.SALES_STEP` | `.02` | The prototype's annual sales-tax move: two percentage points, since it taxes turnover rather than profit. |
| 21 | `GovernmentPlayer.TRANSFER_STEP` | `.05` | The annual pension and EI move: five percentage points, the prototype's transfer step extended to both promised benefits. |
| 23 | `GovernmentPlayer.POOR_PROFIT_MAX` | `.30` | The prototype's estimated poor-side profit-tax ceiling, to rebuild the buffer without an ever-rising tax. |
| 25 | `GovernmentPlayer.POOR_SALES_MAX` | `TaxPolicy.DEFAULT_INCOME_TAX` | The prototype's poor-side sales-tax ceiling: stop at the tax's founding rate rather than raising the whole turnover bill further. |
| 27 | `GovernmentPlayer.POOR_PENSION_MIN` | `.30` | The prototype's estimated pension floor: rebuilding the buffer can trim a promise, but does not remove it. |
| 29 | `GovernmentPlayer.POOR_EI_MIN` | `Unemployment.DEFAULT_BENEFIT_RATE` | EI's poor-side floor is its existing default replacement, so the rule unwinds its increases rather than cutting the founding benefit. |
| 32 | `GovernmentPlayer.considered` | `new WeakHashMap<>()` | The January already considered for each city; weak keys let finished scratch cities go, without sharing a player's year across cities. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 14 | 60 | **type** `public final class GovernmentPlayer` | The test player's annual response to the government's wealth, shared by the playtest and its measuring drivers. |
| 34 | 1 | `private GovernmentPlayer()` |  |
| 42 | 31 | `public static synchronized boolean step(Game g)` | Consider this completed January once, returning true only if at least one dial actually moved. |


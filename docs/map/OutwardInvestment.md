# OutwardInvestment.java - 384 lines · 24 methods · 5 constants · model

`ham/citybuildersim/OutwardInvestment.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> Outward investment: what the city's businesses do with money the bank will
> not pay for.
> 
> THE MIRROR OF HOT MONEY. CapitalFlows is foreign savings coming here for a
> spread; this is the city's own savings going abroad for one. A treasury
> sitting on a billion that the bank pays nothing on, while the world pays two
> percent, does what every corporate treasurer does with idle cash - it buys
> the world's paper - and brings it home when the bank pays better again.
> 
> WHY IT EXISTS (2026-09-10). Every long run in this game was a steel economy
> and every steel economy cycled on the currency: the city exported ore and
> steel, imported scrap and a little food, and ran a surplus of 30-50% of GDP
> that NOTHING recycled - no household imported anything but food, no company
> paid a dividend, no saver could hold a foreign asset, and the treasury only
> bought reserves to back hot money it did not have. So the float did the one
> thing a float can do with a surplus nobody recycles: it appreciated at ten
> percent a year until the exporters were dead, the bank ate their loans, the
> city recapitalised it, the price recovered and it went round again. Twenty
> cycles in 4,000 months; $4.5-7.9bn written off a run. The same eight seeds
> with the rate pinned produced a city twice the size with the spread across
> seeds collapsed. See claude/the-surplus-has-nowhere-to-go.md.
> 
> In the balance of payments this is a financial outflow, which is the thing
> that offsets a current-account surplus in every real surplus economy - the
> Norwegians, the Swiss, the Singaporeans all run one. It reaches the currency
> through ForeignAccounts, which prices the rate on the overall balance now
> rather than the trade balance alone, and the money that leaves earns the
> world's rate abroad and comes back as income. Jerus's call, from four
> options: "outward investment".
> 
> WHAT IT IS NOT. Not a dividend - the sector still owns the money, abroad
> rather than at the counter, and its balance sheet carries it. Not hot money
> in reverse - hot money is a stranger's money with a panic clock; this is the
> owner's, and the owner is in no hurry either way. And not the household's
> savings, yet: the $2bn households hold is real and should follow the same
> rule, but it lives inside HouseholdBalance's own machinery and is a second
> change.
> 
> WHO GOES. A sector with money in the bank and nothing owed to it. A sector
> carrying a loan at six percent has no business lending abroad at two, and a
> sector in overdraft brings what it has home first - so the rule is also the
> rule that keeps a borrower liquid, which is worth having on its own.

**Uses:** [Sectors](Sectors.md) (6), [ForeignAccounts](ForeignAccounts.md) (3), [EconomyManager](EconomyManager.md) (2), [BusinessDebtManager](BusinessDebtManager.md) (1)

**Used by (7):** [CapitalFlowCheck](CapitalFlowCheck.md), [EconomyManager](EconomyManager.md), [ExchangeCheck](ExchangeCheck.md), [Game](Game.md), [HouseholdBalance](HouseholdBalance.md), [LongPlaytest](LongPlaytest.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 54 | HOW MUCH WANTS TO GO |
| 86 | THE ROUNDING FLOOR, AND WHY IT IS SEEDED (2026-09-13) |
| 118 | · state |
| 154 | · the month |
| 184 | · · the income, first |
| 203 | · · the target |
| 209 | · · the move |
| 264 | · reading |
| 315 | · save and restore |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 71 | `OutwardInvestment.APPETITE` | `40` | The share of a sector's wealth it holds abroad, per unit of spread. |
| 74 | `OutwardInvestment.MAX_SHARE` | `.9` | Never more than this. |
| 81 | `OutwardInvestment.OUT_SPEED` | `.04` | How much of the gap to its target moves abroad in a month. |
| 84 | `OutwardInvestment.HOME_SPEED` | `.10` | ...and how much comes home in a month, which is faster, because it is needed. |
| 108 | `OutwardInvestment.MIN_MOVE` | `1e-9` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 111 | `private double minMove` | The same floor in today's money. |
| 121 | `private final Map<String, Double> usd` | Held abroad, per sector, in the dollars it is held in. |
| 124 | `private final Map<String, Double> moved` | This month's move, per sector, in local money: positive went abroad, negative came home. |
| 127 | `private final Map<String, Double> recalled` | Brought home on demand earlier this month, per sector, in local money. |
| 130 | `private final Map<String, Double> interest` | This month's income from abroad, per sector, in local money, rolled where it was earned. |
| 139 | `private double lastRate` | Local currency per dollar the stock was last valued at - the rate the month was traded at. |
| 141 | `private double spread` | what the world pays over the bank, annual |
| 142 | `private double targetShare` | what the world pays over the bank, annual |
| 143 | `private double lifetimeOut, lifetimeHome, lifetimeInterest` | of wealth, abroad |
| 144 | `private double peakUsd` | local money |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 52 | 333 | **type** `public class OutwardInvestment` | Outward investment: what the city's businesses do with money the bank will not pay for. |

### HOW MUCH WANTS TO GO (lines 54-85)

### THE ROUNDING FLOOR, AND WHY IT IS SEEDED (2026-09-13) (lines 86-117)

| line | len | member | says |
|---:|---:|---|---|
| 114 | 3 | `public void seedConstants(double unit)` | Re-seeds the floor at a given unit. |

### state (lines 118-153)

| line | len | member | says |
|---:|---:|---|---|
| 146 | 7 | `public OutwardInvestment()` |  |

### the month (lines 154-263)

| line | len | member | says |
|---:|---:|---|---|
| 169 | 67 | `public void takeMonth(double depositRate, double worldRate, double rate, EconomyManager economy)` | Moves the month's money, in both directions, and rolls the month's income. |
| 251 | 12 | `public double recall(String sector, double local, EconomyManager economy)` | Brings money home because it is needed now - to build, or to pay the owners - rather than because the bank pays more. |

### reading (lines 264-314)

| line | len | member | says |
|---:|---:|---|---|
| 267 | 1 | `public double getUsd(String sector)` | Dollars held abroad by one sector. |
| 270 | 1 | `public double localValue(String sector)` | ...and what they are worth in the city's money, at the rate they were last valued at. |
| 272 | 5 | `public double totalUsd()` |  |
| 278 | 1 | `public double totalLocalValue()` |  |
| 281 | 1 | `public double getMovedThisMonth(String sector)` | This month's move for one sector, local money: positive went abroad. |
| 284 | 1 | `public double getInterestThisMonth(String sector)` | This month's income from abroad for one sector, local money - earned and reinvested there, not in the till. |
| 287 | 5 | `public double getInvestedAbroadThisMonth()` | Everything that went abroad this month, all sectors, local money. |
| 294 | 5 | `public double getBroughtHomeThisMonth()` | Everything that came home this month, all sectors, local money. |
| 301 | 5 | `public double getInterestThisMonth()` | Everything the world paid the city's businesses this month, local money, reinvested where it was paid. |
| 307 | 1 | `public double getSpread()` |  |
| 308 | 1 | `public double getTargetShare()` |  |
| 309 | 1 | `public double getLastRate()` |  |
| 310 | 1 | `public double getLifetimeOut()` |  |
| 311 | 1 | `public double getLifetimeHome()` |  |
| 312 | 1 | `public double getLifetimeInterest()` |  |
| 313 | 1 | `public double getPeakUsd()` |  |

### save and restore (lines 315-384)

| line | len | member | says |
|---:|---:|---|---|
| 322 | 17 | `public double[] toSaveArray()` | Seven headline figures, then three per sector in SECTORS order. |
| 340 | 16 | `public void restore(double[] saved)` |  |
| 357 | 10 | `public void reset()` |  |
| 373 | 11 | `public void redenominate(double scale)` | A reform divides the local money and leaves the dollars alone. |


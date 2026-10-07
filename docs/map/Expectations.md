# Expectations.java - 259 lines · 15 methods · 10 constants · model

`ham/citybuildersim/Expectations.java` - generated 2026-10-06 by CodeMap; line numbers are as of that run.

> What the city expects prices to do, and how far it believes the central bank.
> 
> WHY THIS EXISTS (0.7.42, the anchor). Until 0.7.41 the only things that held
> the price level were constants: the shelf's opening price, the price of
> ground, the cash in every building's cost, the fees - all in founding money,
> all fixed for good. A push ended as a one-off level step, pulled back by
> those constants, and an inflation target meant nothing because nothing in
> the city ever read it: under the autopilot a city ran at about 0% a year
> whatever the dial said (the project's spec-inflation.md, section 1, and
> inflation-research.md). A real economy's anchor is not a constant; it is
> what people expect, and what they expect depends on whether they believe
> the bank.
> 
> SO THE MONEY CONSTANTS FOLLOW WHAT PEOPLE EXPECT PRICES TO BE, not what they
> were. This class keeps four numbers, the level the month is struck at and
> two records (the month's lean and, since 0.7.45, its move in credibility):
> the bank's CREDIBILITY
> (kappa, between KMIN and KMAX), recent inflation SMOOTHED over ADAPT_MONTHS,
> EXPECTED INFLATION - the target weighted by credibility plus the smoothed
> rate weighted by the rest - and the EXPECTED PRICE LEVEL, which compounds at
> expected inflation from 1.0 the month the basket is based. Game re-strikes
> the money constants at that level every month (Game.restrikeMoneyConstants()),
> the wages take half their indexing from it (LabourMarket.updateCostOfLiving()),
> the real rates are struck against it (Game.realRateDifferential(),
> realDepositRate()), and the currency drifts at its credible part
> (ForeignAccounts.setExpectedDrift()).
> 
> CREDIBILITY IS HARD TO WIN AND SLOW TO LOSE WHILE THE BANK FIGHTS. On target
> (a smoothed miss within TOLERANCE) it climbs a GAIN_MONTHS-th of the way to
> KMAX a month, five years to most of it. Off target it falls toward KMIN at a
> LOSS_MONTHS-th of the way a month times the share of a full miss - but only
> as far as the bank is NOT leaning against it: a rate set at the rule's
> advice, or past it, keeps every bit of credibility a supply shock would
> otherwise cost (the lean). The advice is what holding the target takes -
> the Standard rule's, however strict the player has made the bank (0.7.52,
> DebtManager's HOW STRICT) - so a looser rule leans less, and loses trust
> as far as it falls short. Measured in the prototype: a credibility floor
> of .1, lost to supply shocks regardless of the rate, locked the founding
> at 10% a year; at .25 and lean-aware it settles.
> 
> THE MONTH STRIKES ITS CONSTANTS AT ITS TOP. The level is struck into the
> money constants as the first thing a press does (Game.nextMonth(),
> strikeLevel() then Game.restrikeMoneyConstants()), at the level the last
> month ended on - so the month lives at one level from its first statement
> to the next press, and a constant read between the presses (the pension in
> the header's EARNED, a build card's cost) is the month's, as every other
> price is. The struck level is saved beside the anchor's own, so a load
> strikes the constants where the month that was saved had them. What Game
> strikes for itself from the level (the FIXED grant, the rebates, the issue
> fee) reads getExpectedLevel(), which inside a month is the struck level and
> between the presses is the one the next press strikes at - so a bill read
> before a month runs is the bill it pays.
> 
> The level is drift-only. A save from before 0.7.42 seeds it at 1.0, because
> its constants are still at founding money and nothing should jump on the
> load; a level catch-up to the index was measured to be a unit root with the
> floor. A reform does not touch it - it is a ratio, and a constant is struck
> as founding / unit x level.

**Uses:** [DebtManager](DebtManager.md) (3), [PriceIndex](PriceIndex.md) (2)

**Used by (8):** [CentralBankCheck](CentralBankCheck.md), [CityNeeds](CityNeeds.md), [DebtManager](DebtManager.md), [ExpectationsCheck](ExpectationsCheck.md), [Game](Game.md), [PolicyScreen](PolicyScreen.md), [UserInterface](UserInterface.md), [YearBook](YearBook.md)

## Sections

| line | section |
|---:|---|
| 67 | THE DIALS (spec-inflation.md, section 2.1 and star 14) |
| 98 | THE STATE - eight slots saved: four numbers and whether they are seeded, the level the month is struck at, and the mo... |
| 148 | THE MONTH |
| 231 | · carrying |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 72 | `Expectations.ADAPT_MONTHS` | `12` | Months recent inflation is smoothed over, as an exponential average: a twelfth of the gap a month. |
| 75 | `Expectations.TOLERANCE` | `.01` | How far smoothed inflation may sit from the target, a fraction a year, and still count as on target: one point. |
| 78 | `Expectations.MISS_SCALE` | `.04` | The miss past TOLERANCE that costs credibility at the full LOSS_MONTHS speed: four points more, five off target in all. |
| 81 | `Expectations.LOSS_MONTHS` | `24` | Months over which credibility falls toward KMIN on a full miss nobody leans against: a twenty-fourth of the distance a month, half of it gone in about seventeen. |
| 84 | `Expectations.GAIN_MONTHS` | `60` | Months over which credibility climbs toward KMAX on target: a sixtieth of the distance a month, about five years to rebuild. |
| 87 | `Expectations.KMIN` | `.25` | The least anyone believes the bank: a quarter of expected inflation stays the target however long it misses (.1 was measured to lock the founding at 10%). |
| 90 | `Expectations.KMAX` | `.95` | The most anyone believes it: a twentieth of expected inflation is always recent experience. |
| 93 | `Expectations.KSEED` | `.80` | Where a new city, and a save from before the anchor, starts: a bank believed more than not, with something to earn. |
| 96 | `Expectations.EXPECTED_FLOOR` | `-.01` | The least inflation anybody expects, a fraction a year: the floor under the deflation attractor (minus one per cent). |
| 234 | `Expectations.SAVE_SLOTS` | `8` | The slots a save carries: credibility, smoothed, expected, level, seeded - then the lean, the level the month's constants are struck at, and (0.7.45) the month's move in credibility. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 102 | `private double credibility` |  |
| 103 | `private double smoothed` |  |
| 104 | `private double expected` |  |
| 105 | `private double level` |  |
| 107 | `private boolean seeded` | True once seeded - by a new game's first month, or an old save's load. |
| 109 | `private double lean` | This month's lean against the miss, 0 to 1 - a record for the screens; nothing reads it back. |
| 111 | `private double struck` | The level the money constants are struck at this month: the expected level as the month began (strikeLevel()). |
| 113 | `private double step` | This month's move in credibility, the month's own less the last's (0.7.45): a record for NEEDS YOU and the Policy tab - nothing reads it back. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 65 | 195 | **type** `public class Expectations` | What the city expects prices to do, and how far it believes the central bank. |

### THE DIALS (spec-inflation.md, section 2.1 and star 14) (lines 67-97)

### THE STATE - eight slots saved: four numbers and whether they are seeded, the level the month is struck at, and the mo... (lines 98-147)

| line | len | member | says |
|---:|---:|---|---|
| 116 | 1 | `public double getCredibility()` | How far the city believes the bank, KMIN to KMAX. |
| 119 | 1 | `public double getSmoothedInflation()` | Year-on-year inflation smoothed over ADAPT_MONTHS, a fraction a year. |
| 122 | 1 | `public double getExpectedInflation()` | What the city expects inflation to be, a fraction a year: the target before the basket is based. |
| 125 | 1 | `public double getExpectedLevel()` | What it expects prices to be against founding: 1.0 until the basket is based, then compounding at expected inflation. |
| 128 | 1 | `public double getLean()` | How hard the bank leaned against the miss this month, 0 to 1 (0 on target, or the wrong way). |
| 131 | 1 | `public double getStruckLevel()` | The level this month's money constants are struck at: the expected level the last month ended on (THE MONTH STRIKES ITS CONSTANTS AT ITS TOP). |
| 134 | 1 | `public double getCredibilityStep()` | How far credibility moved this month, a fraction (0.7.45): negative is trust lost. |
| 137 | 1 | `public double missFrom(double target)` | How far smoothed inflation - what credibility is judged on - sits from a target, a fraction a year: positive is over it (0.7.45). |
| 140 | 4 | `public double strikeLevel()` | The top of a month: the constants are to be struck at the level the last month ended on. |
| 146 | 1 | `public double monthlyExpected()` | Expected inflation as a month's growth: (1 + expected) to the twelfth, less one. |

### THE MONTH (lines 148-230)

| line | len | member | says |
|---:|---:|---|---|
| 162 | 40 | `public void takeMonth(PriceIndex index, double target, double policy, double neutral, double rule)` | One month, read straight after the price index has taken its month. |
| 209 | 10 | `public void seed(PriceIndex index, double target)` | Seeds a city with no anchor yet: a new game's first month, or a save from before 0.7.42 on its load. |
| 221 | 9 | `public void reset()` | A new game: unseeded, at the defaults; its first month seeds it at the city's target. |

### carrying (lines 231-259)

| line | len | member | says |
|---:|---:|---|---|
| 237 | 3 | `public double[] toSaveArray()` | The state as the save carries it: DataSave.expectations. |
| 246 | 13 | `public boolean restore(double[] saved)` | from before 0.7.42), with nothing changed - the load path then seeds it (seed()). |


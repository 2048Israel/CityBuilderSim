# ForeignDebtCheck.java - 1,057 lines · 15 methods · 3 constants · harnesses

`ham/citybuildersim/ForeignDebtCheck.java` - generated 2026-11-21 by CodeMap; line numbers are as of that run.

> Borrowing in somebody else's money.
> 
> ORIGINAL SIN, which is the name the literature gives the thing this file is
> here to prove works: a city that cannot borrow abroad in its own currency owes
> dollars and earns local money, so a devaluation makes the debt dearer without
> anybody having borrowed another cent. Domestic debt does the opposite -
> inflation and devaluation quietly shrink it.
> 
> WHAT IS ACTUALLY BEING ASKED
> 
>   1. Does the instrument speak two currencies HONESTLY? The contract is in
>      dollars and does not move; the city's books are in local money and must.
>      Both, at once, with neither leaking into the other.
> 
>   2. Do the books still balance? Foreign paper is the first city borrowing
>      that genuinely crosses the city's edge, so MoneyAudit has three new lines
>      and one deliberate absence - the revaluation, which is not a cash flow
>      and must not appear as one.
> 
>   3. Is the circular-capital hole actually closed? The whole point of foreign
>      paper is that it does not reach the bank's book. If it does, the city is
>      still borrowing from the institution it is recapitalising and none of
>      this was worth building.
> 
>   4. Does the window shut when it should, does a default cost what it is
>      supposed to cost, and does any of it survive a reload?
> 
>   5. Is the world's paper on the world's curve (0.7.2)? A dollar bond is
>      worth what the world would pay for it - the foreign rate plus the
>      term premium for the months it has left - not what the city's own
>      dial says; the dial moving leaves it where it is, the world's view of
>      the city moving does not; and a dollar issue is priced on the same
>      curve it is then valued on.
> 
>   6. Does the ladder the Finances tab draws (0.7.32) carry every payment
>      the paper asks, each in the calendar year it is paid in, the dollar
>      part as what the dollar paper asks, and the heaviest year never the
>      "later" that sums many? And do the tab's other reads - the next
>      twelve months, the coupon, each kind's principal, the rate a piece
>      is valued at, a quote's schedule - agree with the paper, the
>      rollover's parts add to what falls due, and the borrowing flags
>      carry nothing else?
> 
>   7. Does the long playtest's build, under -Dplaytest.quotedBorrowing
>      (CC1-S, section 10; off by default, CC1-S-B), take the cheaper money
>      by the quote for the sale itself? Not by the two rates before it: a sale of two years' tax is quoted dear abroad and goes home
>      though the world's rate before it is the lower, and a small one goes
>      abroad once the city's own paper is dearer; quoting books nothing.
> 
> THE FIXTURES (FS13, 2026-11-20): a played city (tradingCity(), 60 months) is the state the sections read, and what the world prices
> it on is SET on it, not read off what its depots earned or borrowed up to: section 2 holds the dial over the world's rate, section 8
> hands the city its exports (earnsAbroad()) sized to its bond, and section 10 hands it exports sized to the big sale and holds the
> dial over the world's quote for the small one.

**Uses:** [DebtManager](DebtManager.md) (38), [PriceLevel](PriceLevel.md) (24), [Game](Game.md) (19), [LongPlaytest](LongPlaytest.md) (16), [Debt](Debt.md) (9), [CityCalendar](CityCalendar.md) (6), [GameFiles](GameFiles.md) (3), [MoneyAudit](MoneyAudit.md) (3), [DebtQuote](DebtQuote.md) (3), [Rollover](Rollover.md) (3), [ChartModel](ChartModel.md) (3), [DecisionLog](DecisionLog.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [Founding](Founding.md) (2), [TempDirs](TempDirs.md) (2), [ForeignAccounts](ForeignAccounts.md) (2), [Ladder](Ladder.md) (2)

**Used by (3):** [BuybackCheck](BuybackCheck.md), [CurveCheck](CurveCheck.md), [RestructureCheck](RestructureCheck.md)

## Sections

| line | section |
|---:|---|
| 192 | · 1. the instrument speaks two currencies |
| 240 | · 2. and the world is cheaper, to begin with |
| 298 | · 3. the books balance with dollars on them |
| 361 | · 4. original sin |
| 427 | · 4b. and where the dollars actually went |
| 501 | · 5. the window shuts |
| 525 | · 6. and the price of walking away |
| 576 | · 7. across a reload |
| 700 | 8. the world's paper on the world's curve (0.7.2) |
| 784 | 9. the ladder, by the calendar year it is paid in (0.7.32) |
| 950 | 10. the test player takes the cheaper money by the quote for the sale (CC1-S) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 167 | `ForeignDebtCheck.DIAL_OVER_THE_WORLD` | `.01` | How far over the world's rate a fixture holds the city's dial to make its own paper the dearer: a point, over the floor the paper stands on. |
| 170 | `ForeignDebtCheck.BOND_STRESS_SHARE` | `1.0 / 8` | The share of a full solvency stress (DebtManager.FULL_STRESS_EXPORT_YEARS of exports owed) section 8's bond is sized to: an eighth, a few points of premium with room above it for a quarter of the exports to raise. |
| 173 | `ForeignDebtCheck.BIG_SALE_STRESS_SHARE` | `1.0 / 2` | ...and the share section 10's big sale (two years of tax) is sized to: half, so the world prices the big sale dear and the small one a twenty-fourth of it near nothing. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 64 | `static int fails` |  |
| 65 | `static PrintStream out` |  |
| 66 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 62 | 996 | **type** `public class ForeignDebtCheck` | Borrowing in somebody else's money. |
| 68 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 73 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 83 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 105 | 6 | `static void tradingPlant(Game g)` | THE PLANT THE TRADING CITY EARNS WITH, STANDING (0.7.17): the builders' depots, the power, the water and the two bakeries whose bread is what it sells abroad. |
| 113 | 3 | `static Game tradingCity(Path dir) throws Exception` | A small city that has been going long enough to have a credit record. |
| 130 | 3 | `static Game tradingCity(Path dir, int depotsStanding) throws Exception` | ...with that many more depots standing beside the plant's and the founding yard's (PT1-M4). |
| 135 | 19 | `private static Game tradingCity(Path dir, int depotsStanding, boolean levers) throws Exception` | ...the one builder of both: `levers` puts the defaults back before a month (every lever is open since OP1; tradingCityTaxed()). |
| 162 | 3 | `static Game tradingCityTaxed(Path dir, int depotsStanding) throws Exception` | ...THE SAME CITY WITH EVERY LEVER OPEN (R0), for section 10 alone: tradingCity() is BuybackCheck's, CurveCheck's and RestructureCheck's as well and is left as it was. |
| 183 | 3 | `static void earnsAbroad(DebtManager dm, double owedLocal, double stressShare)` | THE EXPORTS A FIXTURE HANDS THE WORLD (FS13, 2026-11-20). |
| 187 | 512 | `public static void main(String[] args) throws Exception` |  |

### 8. the world's paper on the world's curve (0.7.2) (lines 700-783)

| line | len | member | says |
|---:|---:|---|---|
| 701 | 82 | `static void theWorldsCurve(Path dir) throws Exception` |  |

### 9. the ladder, by the calendar year it is paid in (0.7.32) (lines 784-949)

| line | len | member | says |
|---:|---:|---|---|
| 798 | 151 | `static void theLadder(Path dir) throws Exception` | WHAT THE FINANCES TAB DRAWS THE DEBT FROM. |

### 10. the test player takes the cheaper money by the quote for the sale (CC1-S) (lines 950-1057)

| line | len | member | says |
|---:|---:|---|---|
| 971 | 71 | `static void theCheaperMoney(Path dir) throws Exception` | The long playtest's build borrows abroad only when the quote for the sale is the cheaper. |
| 1044 | 4 | `private static double[] books(Game g)` | What quoting must leave alone: cash, the debts at home and abroad, and the two rates. |
| 1050 | 7 | `private static void quotes(String state, Game g, double small, double big)` | Both sales' quotes, abroad (the 20-year dollar bond, as the build asks it) and at home. |


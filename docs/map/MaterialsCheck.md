# MaterialsCheck.java - 605 lines · 20 methods · 2 constants · harnesses

`ham/citybuildersim/MaterialsCheck.java` - generated 2026-10-11 by CodeMap; line numbers are as of that run.

> The materials chains, checked: the shared machinery every chain stands on
> (0.7.112, batch I2; runs/spec-materials.md 2.1, 2.5-2.8 and 4), and each
> chain's section as its batch comes (I3 onwards).
> 
> WHY. Five chains - iron redone, coal, stone, copper and wood - share one
> planner, two sector bases, a captive good, a works' own power and the
> consumables it buys abroad, and none of them builds a works until batch I3
> brings the first rows. So the machinery is held here on fixtures that cause
> each condition, before any chain leans on it, and the playtest - which has
> none of it - stays the same to the byte.
> 
> WHAT THIS HAS TO PROVE (the spec's 4, MaterialsCheck's I2 bullets):
> 
>   1. THE CITY'S VALUE OF A WORKS' OUTPUT AND FEED, SPLIT AT THE MONTH'S
>      TRADE (SpreadPlanner.outValue(), inValue()): made here, the net import
>      price for as much as the city imported and the net export price past
>      it; used here, the net export price for as much as it exported and the
>      net import price past it; the first unit's worth cityValue()'s, to the
>      bit; a price the world does not quote, the local price; the game's
>      values the market's own tally.
> 
>   2. THE WHOLE-COST GATE REFUSES A CASH-RICH SECTOR'S CASE A (decision A):
>      a works whose case A - feed imported, output exported - earns
>      something, but half the hurdle on its whole cost, in a sector holding
>      that cost in its till: the rule to 0.7.111 (ON_ITS_BORROWING) passes
>      it, the rule in force (ON_ITS_WHOLE_COST, the default) refuses it at
>      the money; and the four cases are the study's arithmetic, the works'
>      consumables and power in them.
> 
>   3. CAPTIVE USE BOOKS NO SALE: a ProcessingSector's good it both makes and
>      uses goes to its own need first, min(made, needed); the market sees
>      the net - bid() and what is made for the market - and the ledger
>      books sales of the rest and no purchase of it, a flow good and a
>      stockable one; no sector in the game has a captive good.
> 
>   4. A WORKS' OWN POWER IS BILLED NET AND THE CITY PAYS THE SURPLUS: in a
>      town whose bakeries are handed power of their own, each building buys
>      its draw past what it makes and sells the grid what it makes past its
>      draw; the sector's bill is its net draw at the ratio and the tariff,
>      its statement's powerSold the surplus at the tariff, in its revenue;
>      the grid's consumption is the net draws, its generation the plants'
>      and the works' surplus; the utility pays the statements' figure out of
>      its income; the audit closes; powerSold crosses a save.
> 
>   5. CONSUMABLES GO TO TRADE: a works' consumables, world money a month at
>      nameplate, are bought at the operating rate and the exchange factor as
>      a service from the world - an import named "Consumables" on the input
>      line - and the audit sees them leave in TRADE.
> 
>   6. A PROCESSING SECTOR'S PLAN: a candidate earns what it would make less
>      what it would use, at outValue() and inValue(), less its running and
>      standing costs and its consumables and power, to the bit; the last
>      one standing earns the mirror; the investors' test reads that
>      estimate.
> 
>   7. IDLE, THEN SHED: a kind of its works whose last one earns below
>      nothing counts a month idle, a working kind none; at IDLE_MONTHS that
>      kind alone may be sold, the spare-capacity rule's measure its
>      buildings, and the rule sells it; the months cross a save (extras
>      idleMonths.<id>); a month working clears the count.
> ... (9 more lines in the source)

**Uses:** [Good](Good.md) (96), [SpreadPlanner](SpreadPlanner.md) (70), [BuildingsTemplate](BuildingsTemplate.md) (27), [Game](Game.md) (18), [Sector](Sector.md) (15), [ProcessingSector](ProcessingSector.md) (12), [Sectors](Sectors.md) (10), [BuildingManager](BuildingManager.md) (9), [GameFiles](GameFiles.md) (8), [GoodsMarket](GoodsMarket.md) (8), [BusinessInvestment](BusinessInvestment.md) (8), [BuildingType](BuildingType.md) (6), [Markets](Markets.md) (6), [MoneyAudit](MoneyAudit.md) (3), [Trade](Trade.md) (3), [Resource](Resource.md) (3), [Statement](Statement.md) (2), [Founding](Founding.md) (1), [UtilitiesHandler](UtilitiesHandler.md) (1), [Mining](Mining.md) (1), [LandManager](LandManager.md) (1), [ExtractionSector](ExtractionSector.md) (1)

## Sections

| line | section |
|---:|---|
| 183 | 1. outValue and inValue |
| 234 | 2. the whole-cost gate and the four cases |
| 297 | 3. captive use |
| 351 | 4. the works' own power |
| 419 | 5. consumables |
| 455 | 6. a processing sector's plan |
| 506 | 7. idle, then shed |
| 568 | 8. Mining on the extraction base |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 90 | `MaterialsCheck.out` | `System.out` | Where the harness prints: the console, kept while a fixture town's prose is sent to `quiet`. |
| 92 | `MaterialsCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` | A stream that drops what is written to it: the game's prose while a fixture town is founded and played. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 88 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 86 | 520 | **type** `public class MaterialsCheck` | The materials chains, checked: the shared machinery every chain stands on (0.7.112, batch I2; runs/spec-materials.md 2.1, 2.5-2.8 and 4), and each chain's section as its batch comes (I3 onwards). |
| 94 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 99 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 104 | 5 | `static void quietly(Runnable r)` |  |
| 111 | 3 | `static boolean near(double a, double b)` | Two money figures agree to MoneyAudit's tolerance at their size. |
| 120 | 17 | **type** `static final class Works extends ProcessingSector` | A processing sector of the fixture's own (no ProcessingSector stands in the game until batch I3 moves Heavy Industry onto it): Heavy Industry's key, so it reads that sector's buildings, and the goods it is told. |
| 121 | 5 | `Works(Good[] made, Good[] used)` _(in MaterialsCheck.Works)_ |  |
| 128 | 5 | `Map<String, Double> extras()` _(in MaterialsCheck.Works)_ | Its extras as a save writes them. |
| 135 | 1 | `void load(Map<String, Double> m)` _(in MaterialsCheck.Works)_ | ...and read back. |
| 139 | 7 | `static Works works(BuildingManager b, Markets m, Game g, Good[] made, Good[] used)` | A fixture's works, wired as Sectors wires a sector: to buildings and markets, and to a game when there is one. |
| 148 | 3 | `static Game town(String label)` | A town to stand works in: founded, the Wealthy preset's cash and ground, and the houses, shops, power and water to run them. |
| 153 | 15 | `static Game town(GameFiles files)` | ...in a save folder of the caller's, for a section that saves the town and loads its twin. |
| 169 | 13 | `public static void main(String[] args) throws Exception` |  |

### 1. outValue and inValue (lines 183-233)

| line | len | member | says |
|---:|---:|---|---|
| 185 | 48 | `static void values()` |  |

### 2. the whole-cost gate and the four cases (lines 234-296)

| line | len | member | says |
|---:|---:|---|---|
| 236 | 60 | `static void wholeCost()` |  |

### 3. captive use (lines 297-350)

| line | len | member | says |
|---:|---:|---|---|
| 299 | 51 | `static void captive()` |  |

### 4. the works' own power (lines 351-418)

| line | len | member | says |
|---:|---:|---|---|
| 354 | 14 | `static Game powerTown(GameFiles files, int months)` | The power town: section 4's and 5's, bakeries handed power of their own and consumables, run `months`. |
| 369 | 49 | `static void power() throws Exception` |  |

### 5. consumables (lines 419-454)

| line | len | member | says |
|---:|---:|---|---|
| 421 | 33 | `static void consumables()` |  |

### 6. a processing sector's plan (lines 455-505)

| line | len | member | says |
|---:|---:|---|---|
| 457 | 48 | `static void plan()` |  |

### 7. idle, then shed (lines 506-567)

| line | len | member | says |
|---:|---:|---|---|
| 508 | 59 | `static void idleThenShed()` |  |

### 8. Mining on the extraction base (lines 568-605)

| line | len | member | says |
|---:|---:|---|---|
| 570 | 35 | `static void mining()` |  |


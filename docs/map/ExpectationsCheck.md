# ExpectationsCheck.java - 570 lines · 21 methods · 1 constants · harnesses

`ham/citybuildersim/ExpectationsCheck.java` - generated 2026-10-05 by CodeMap; line numbers are as of that run.

> Proves the anchor (Expectations, 0.7.42): credibility won on target and lost to a miss nobody leans against, expected inflation and its floor, the expected price level, the wages' half-and-half indexing, the money constants struck at the level, the currency's anchored drift and its UIP level, and the anchor through a save. Not part of the game.
> 
> WHY THIS EXISTS. Until 0.7.42 nothing in the city read the inflation
> target and the money constants sat at founding money for good, so a push
> ended as a level step and the autopilot ran a city at about 0% a year
> whatever its target (the project's inflation-research.md). The anchor is
> the design that replaced that - spec-inflation.md, sections 2.1-2.5, batch
> P2 - and every rule in it is a recurrence with a constant in it, so each
> is asserted against its own constants, month by month, on a fixture that
> causes the condition.
> 
> What it has to prove, a section each:
> 
>   1. Before the basket is based the expected level is 1, expected and
>      smoothed inflation are the target, credibility waits at KSEED - on a
>      bare index and in a founding city, whose constants are at founding.
>   2. On target, credibility climbs a GAIN_MONTHS-th of the way to KMAX a
>      month, to the bit, and never past it; the month's step (0.7.45) is
>      nothing the month the anchor is seeded and the move it made after.
>   3. A miss nobody leans against costs a LOSS_MONTHS-th of the way to KMIN
>      times the miss's share of MISS_SCALE past TOLERANCE, both short of a
>      full miss and past it; a rate at the rule's advice costs nothing; a
>      rate halfway costs half; each month's step is the move it made, a
>      fall in the months it fell (NEEDS YOU's PRICES row, 0.7.45).
>   4. Expected inflation never goes under EXPECTED_FLOOR, in a deflation
>      where credibility and recent inflation would put it there.
>   5. The expected level compounds at expected inflation every month: a
>      year at it is the year's growth.
>   6. Wages: in steady inflation that is expected they grow at it, not at
>      twice it, and lag the index by two years of it; a level step nobody
>      expected passes a forty-eighth of its log a month and all of it in
>      the end.
>   7. In a city: every Pe-struck constant is founding / (unit / the struck
>      level), the fare is the dial at the level, a FIXED grant at the
>      expected level, the labour in a cost split at the level; a reform
>      divides them all at once, and the month after strikes them at the new
>      unit and the reformed twin agrees with its unreformed self.
>   8. The currency: the rate's own pressure is zero at uipLevel(), a real
>      gap holds the rate at a level rather than a drift, the anchored drift
>      moves an unpushed rate by its twelfth root and a pinned one not at
>      all, and a city hands the currency its anchored drift - nothing
>      before the basket is based.
>   9. A save carries the anchor whole and the next month is the same
>      month, the month's step in its eighth slot (0.7.45), which a save of
>      seven slots reads as nothing; a save from before 0.7.42 is seeded -
>      credibility KSEED, the year's inflation smoothed, the level 1, its
>      constants at founding, its step nothing.

**Uses:** [Expectations](Expectations.md) (46), [Game](Game.md) (18), [PriceIndex](PriceIndex.md) (14), [LabourMarket](LabourMarket.md) (7), [ForeignAccounts](ForeignAccounts.md) (5), [CurrencyCheck](CurrencyCheck.md) (5), [Retail](Retail.md) (4), [DebtManager](DebtManager.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (4), [Bank](Bank.md) (4), [CareType](CareType.md) (3), [EducationType](EducationType.md) (3), [Healthcare](Healthcare.md) (2), [GameFiles](GameFiles.md) (2), [TaxPolicy](TaxPolicy.md) (2), [MonetaryCheck](MonetaryCheck.md) (1), [Education](Education.md) (1), [PayTier](PayTier.md) (1), [Equity](Equity.md) (1), [BuildingManager](BuildingManager.md) (1)

## Sections

| line | section |
|---:|---|
| 104 | · fixtures |
| 138 | 1. before the basket is based |
| 172 | 2. on target |
| 209 | 3. a miss |
| 267 | 4. the floor |
| 296 | 5. the level |
| 325 | 6. wages |
| 357 | 7. the constants |
| 454 | 8. the currency |
| 512 | 9. a save |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 107 | `ExpectationsCheck.TARGET` | `.03` | The target the bare fixtures aim at: not the default, so nothing reads the default by accident. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 61 | `static int fails` |  |
| 62 | `static PrintStream out` |  |
| 63 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 59 | 512 | **type** `public class ExpectationsCheck` | Proves the anchor (Expectations, 0.7.42): credibility won on target and lost to a miss nobody leans against, expected inflation and its floor, the expected price level, the wages' half-and-half indexing, the money con... |
| 65 | 4 | `static void check(String label, boolean ok)` |  |
| 70 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 80 | 4 | `static void quietly(Runnable r)` |  |
| 85 | 18 | `public static void main(String[] args) throws Exception` |  |

### fixtures (lines 104-137)

| line | len | member | says |
|---:|---:|---|---|
| 110 | 3 | `static void price(PriceIndex index, double level, int month)` | A month's basket at this price level, both halves spent alike. |
| 115 | 5 | `static int based(PriceIndex index)` | A basket based on a steady price: SETTLING_MONTHS of shopping at 1. |
| 122 | 5 | `static void press(Game g)` | One press of a city, as the playtest steps it: a broke city steps rather than skips. |
| 129 | 8 | `static Game city(Path root, String label, int toMonth)` | The playtest's founding (MonetaryCheck's), played on to this month. |

### 1. before the basket is based (lines 138-171)

| line | len | member | says |
|---:|---:|---|---|
| 139 | 32 | `static void beforeTheBase(Path root)` |  |

### 2. on target (lines 172-208)

| line | len | member | says |
|---:|---:|---|---|
| 173 | 35 | `static void onTarget()` |  |

### 3. a miss (lines 209-266)

| line | len | member | says |
|---:|---:|---|---|
| 212 | 29 | `static double[] miss(double annual, java.util.function.DoubleBinaryOperator policy, double wantLean)` | Plays a basket rising at `annual` past a based start, with the rate set by `policy` (neutral, the rule's advice): returns {worst deviation from the recurrence, months of a part-share miss, months of a full miss, month... |
| 242 | 24 | `static void aMiss()` |  |

### 4. the floor (lines 267-295)

| line | len | member | says |
|---:|---:|---|---|
| 268 | 27 | `static void theFloor()` |  |

### 5. the level (lines 296-324)

| line | len | member | says |
|---:|---:|---|---|
| 297 | 27 | `static void theLevel()` |  |

### 6. wages (lines 325-356)

| line | len | member | says |
|---:|---:|---|---|
| 326 | 30 | `static void wages()` |  |

### 7. the constants (lines 357-453)

| line | len | member | says |
|---:|---:|---|---|
| 360 | 8 | `static Game fresh(Path root)` | A city at founding, before any month has struck anything: its catalogue and its ground are the founding figures. |
| 370 | 15 | `static double[] constantsOf(Game g, String template)` | The Pe-struck constants a city holds, in order, for twins to be compared by. |
| 387 | 7 | `static double worstRelative(double[] scaled, double by, double[] against)` | The largest relative gap between each of `scaled` times `by` and its `against` - a House has no upkeep, so a zero against a zero is no gap. |
| 395 | 58 | `static void constants(Path root)` |  |

### 8. the currency (lines 454-511)

| line | len | member | says |
|---:|---:|---|---|
| 455 | 56 | `static void theCurrency(Path root)` |  |

### 9. a save (lines 512-570)

| line | len | member | says |
|---:|---:|---|---|
| 513 | 57 | `static void aSave(Path root) throws Exception` |  |


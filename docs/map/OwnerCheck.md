# OwnerCheck.java - 683 lines · 18 methods · 3 constants · harnesses

`ham/citybuildersim/OwnerCheck.java` - generated 2026-11-10 by CodeMap; line numbers are as of that run.

> The owner families (BO1): one two-adult household per company that has
> listed and not gone broke, holding the company's founding stake alone.
> 
> Jerus, 2026-11-02, on the households study: business owners are "few in
> nature and very rich", "one per listed company", "No job, lives of
> dividends or sells or whatever"; "the others still have shares, they just
> dont get the initial ownership". Every fixture here is a bare balance and a
> bare register, the cause set by the fixture and not stood next to.
> 
>   1. THE LINE OFF: a bare balance that never switched the line on gets
>      today's founders' grant, by grown-ups, every non-empty cell its share.
>   2. THE LINE ON, NO OWNER HOUSEHOLD YET: the listing waits (listIfUnlisted
>      says false, nothing is in issue), the family is reserved, and a
>      company with a book and no owners raises nothing at its first offering.
>   3. THE LINE ON, WITH HOUSEHOLDS: the founders' shares go to the owner cell
>      alone, each family an equal part of every stake; the row, the label and
>      the save of the line.
>   4. EVERYONE ELSE STILL BUYS: an offering is taken up by the ordinary cells
>      and never by the owner cell, even when it is the richest.
>   5. THE OWNERS TAKE NO JOB: their adults come out of the adult pool with
>      the outside adults (FamilyModel) and out of the labour force
>      (PopulationManager).
> 
>   6. THE LINE SELLS A TWENTIETH A YEAR: the owner cell offers OWNER_SELL_A_MONTH
>      of each stake at the market, as its own seller class, and nothing else:
>      not as a household under its cushion, and not a company at the floor.
>   7. A YEAR AT THE FLOOR SENDS THE FAMILY BACK: OWNER_BROKE_MONTHS running,
>      a month off the floor starting the count again; a failed company at once.
>   8. THE LEAVING FAMILY TAKES ITS MONEY, NOT THE SHARES: the line's total of
>      each company is what it was, the savings and money abroad go to the
>      cell that grew.
>   9. A REAL CITY: the playtest's city with the line on, 120 months, counted.

**Uses:** [Exchange](Exchange.md) (33), [Equity](Equity.md) (26), [HouseholdBalance](HouseholdBalance.md) (25), [Household](Household.md) (24), [PayTier](PayTier.md) (17), [LongPlaytest](LongPlaytest.md) (17), [FamilyStructure](FamilyStructure.md) (14), [FamilyModel](FamilyModel.md) (10), [ExchangeCheck](ExchangeCheck.md) (7), [AgeBand](AgeBand.md) (5), [OwnerHousehold](OwnerHousehold.md) (4), [PopulationCohorts](PopulationCohorts.md) (4), [Game](Game.md) (4), [PopulationManager](PopulationManager.md) (3), [PriceLevel](PriceLevel.md) (2), [Sectors](Sectors.md) (2), [DebtManager](DebtManager.md) (2), [HouseholdAccounts](HouseholdAccounts.md) (2), [JobType](JobType.md) (2), [OrderBook](OrderBook.md) (2), [GameFiles](GameFiles.md) (1), [TreasuryFund](TreasuryFund.md) (1)

## Sections

| line | section |
|---:|---|
| 99 | · 1. the line off: today's grant |
| 118 | · 2. the line on, no owner household yet: the listing waits |
| 138 | · 3. the line on, with households: the founders' shares to the owners alone |
| 185 | · 4. everyone else still buys |
| 208 | · 5. the owners take no job |
| 271 | 6. the line sells a twentieth a year |
| 364 | 7. a year at the floor sends the family back |
| 416 | 8. the leaving family takes its money, not the shares |
| 481 | 9. a real city: one family per listed company |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 56 | `OwnerCheck.owners` | `new double [ 1 ]` | The owners the fixture's census names: set by a section, read by the balance's outside census. |
| 487 | `OwnerCheck.CRUMB` | `1e-3` | The most of a company with no family the owner cell may hold, as a part of the shares in issue: the line's stakes come with a family, and what is over is the crumbs of the pool a growing cell claims from the cells tha... |
| 490 | `OwnerCheck.LOG` | `new java.io.ByteArrayOutputStream()` | The game's log while a city runs: kept, and read for the families that rejoin the population and why. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 41 | `static int fails` |  |
| 275 | `Exchange x` |  |
| 276 | `Equity reg` |  |
| 277 | `HouseholdBalance hb` |  |
| 278 | `Household owner` |  |
| 279 | `double holding, book` |  |
| 280 | `String who` |  |
| 563 | `final int n` |  |
| 564 | `final boolean[] had, leftOnce` |  |
| 565 | `int formed, dropped, droppedAtFloor, droppedFailed, reformed, emptyMonths, emptyBefore, logged` |  |
| 566 | `boolean cellEverHeld` |  |
| 568 | `double lastCensus` | The owner families the census counted last month. |
| 570 | `String gap` | The first month each check failed, said in the log: what the run found, not only that it did. |
| 572 | `final java.util.TreeMap<String, Integer> otherwise` | Companies in issue with no family, never granted to founders: the bank (its desk issues its shares), a company that offered on a book of nothing. |
| 574 | `double crumbs` | The most of a company without a family the owners ever held, as a part of its shares in issue. |
| 575 | `boolean countsAgreed` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 39 | 645 | **type** `public class OwnerCheck` | The owner families (BO1): one two-adult household per company that has listed and not gone broke, holding the company's founding stake alone. |
| 43 | 4 | `static void check(String label, boolean ok)` |  |
| 49 | 5 | `static void close(String label, double actual, double expected, double rel)` | Relative to the larger of the two, so a figure of any size is held to the same 1e-9. |
| 59 | 8 | `static HouseholdBalance town(boolean lineOn)` | A bare balance in today's dollar: a hundred unskilled couples and forty skilled single adults, owners as the census says. |
| 69 | 3 | `static void month(HouseholdBalance hb)` | One month's census, with the owner count in owners[0]. |
| 74 | 11 | `static void month(HouseholdBalance hb, int couples)` | ...with this many unskilled couples (the town's ordinary working cell) beside the forty single adults. |
| 86 | 5 | `static Equity register()` |  |
| 92 | 178 | `public static void main(String[] args)` |  |

### 6. the line sells a twentieth a year (lines 271-363)

| line | len | member | says |
|---:|---:|---|---|
| 274 | 8 | **type** `static final class Sale` | The exchange's first month with the line on and one family holding every founders' share of one company. |
| 287 | 24 | `static Sale sale(int company, boolean floor, boolean control)` |  |
| 312 | 51 | `static void sells(int IND, int RETAIL)` |  |

### 7. a year at the floor sends the family back (lines 364-415)

| line | len | member | says |
|---:|---:|---|---|
| 366 | 49 | `static void sendsBack(int IND, int RETAIL)` |  |

### 8. the leaving family takes its money, not the shares (lines 416-480)

| line | len | member | says |
|---:|---:|---|---|
| 422 | 15 | `static HouseholdBalance leaving(int IND, int RETAIL, int ownersNext, int couplesNext)` | A town with four owner families, savings and money abroad in the owner cell and its shares in two companies, a working couple cell with some of its own, and a second month's census of these owner and couple counts. |
| 438 | 42 | `static void takesMoneyNotShares(int IND, int RETAIL)` |  |

### 9. a real city: one family per listed company (lines 481-683)

| line | len | member | says |
|---:|---:|---|---|
| 492 | 68 | `static void realCity()` |  |
| 562 | 108 | **type** `static final class Tally` | What a real run counts, month by month. |
| 577 | 5 | `Tally(int n)` _(in OwnerCheck.Tally)_ |  |
| 583 | 6 | `void play(Game g, int months)` _(in OwnerCheck.Tally)_ |  |
| 590 | 79 | `void audit(Game g)` _(in OwnerCheck.Tally)_ |  |
| 672 | 5 | `static double inFamilies(FamilyModel f)` | The adults the family matrix holds. |
| 678 | 5 | `static double sum(double[] v)` |  |


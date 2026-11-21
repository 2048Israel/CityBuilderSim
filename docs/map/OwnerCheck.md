# OwnerCheck.java - 992 lines · 28 methods · 7 constants · harnesses

`ham/citybuildersim/OwnerCheck.java` - generated 2026-11-21 by CodeMap; line numbers are as of that run.

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
>      A family reserved for a company that never lists is let go once the
>      owner cell holds families.
>   8. THE LEAVING FAMILY TAKES ITS MONEY, NOT THE SHARES: the line's total of
>      each company is what it was, the savings and money abroad go to the
>      cell that grew.
>   9. A REAL CITY: the playtest's city with the line on, 120 months, counted.
>      Since FS16 its two premises are read at the months the founding causes
>      them, not found over the run: the first month a company stands
>      unlisted with no family on a positive book (the village's stores),
>      it has its family when the month ends; the first month that opens with
>      the owner cell holding a stake above the floor, the owners offer.
>  10. A SAVE FROM BEFORE THE OWNER FAMILIES LOADS ITS ROWS: main's statement
>      and row array, one row short, fed to the readers; the marked figures
>      come back where the save put them, the owners' row empty.
>  11. AT MOST A SHARE OF THE ADULTS (BO1-S): a town of nine adults with three
>      families reserved has no owner families - its census holds under half
>      a household, the cell is empty and the listings wait - where two a
>      family took six of its nine; a town far above the cap keeps one family
>      a company, as BO1 had it, and its listings list to them; between, the
>      share holds and there are fewer families than companies.

**Uses:** [Exchange](Exchange.md) (36), [HouseholdBalance](HouseholdBalance.md) (35), [Equity](Equity.md) (35), [LongPlaytest](LongPlaytest.md) (34), [Household](Household.md) (31), [PayTier](PayTier.md) (19), [FamilyStructure](FamilyStructure.md) (14), [FamilyModel](FamilyModel.md) (14), [AgeBand](AgeBand.md) (11), [Game](Game.md) (11), [OwnerHousehold](OwnerHousehold.md) (10), [PopulationCohorts](PopulationCohorts.md) (10), [ExchangeCheck](ExchangeCheck.md) (7), [HouseholdAccounts](HouseholdAccounts.md) (5), [Sectors](Sectors.md) (3), [PopulationManager](PopulationManager.md) (3), [PriceLevel](PriceLevel.md) (2), [DebtManager](DebtManager.md) (2), [JobType](JobType.md) (2), [OrderBook](OrderBook.md) (2), [GameFiles](GameFiles.md) (2), [TreasuryFund](TreasuryFund.md) (2), [CareType](CareType.md) (1)

**Used by (1):** [LongPlaytest](LongPlaytest.md)

## Sections

| line | section |
|---:|---|
| 116 | · 1. the line off: today's grant |
| 135 | · 2. the line on, no owner household yet: the listing waits |
| 155 | · 3. the line on, with households: the founders' shares to the owners alone |
| 202 | · 4. everyone else still buys |
| 225 | · 5. the owners take no job |
| 290 | 6. the line sells a twentieth a year |
| 383 | 7. a year at the floor sends the family back |
| 457 | 8. the leaving family takes its money, not the shares |
| 522 | 9. a real city: one family per listed company |
| 618 | · the probe (BO1-S): the playtest city, line on against line off; -Downer.probe=true runs it alone, never the default |
| 872 | 10. a save from before the owner families loads its rows |
| 925 | 11. at most a share of the adults (BO1-S) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 72 | `OwnerCheck.owners` | `new double [ 1 ]` | The owners the fixture's census names: set by a section, read by the balance's outside census. |
| 386 | `OwnerCheck.ALL_LISTED` | `c -> true` | Every company in issue, for the fixtures whose families were reserved by a grant alone: the floor is all they are tallied on. |
| 528 | `OwnerCheck.CRUMB` | `1e-3` | The most of a company with no family the owner cell may hold, as a part of the shares in issue: the line's stakes come with a family, and what is over is the crumbs of the pool a growing cell claims from the cells tha... |
| 535 | `OwnerCheck.MONTHS_TO_THE_LISTING` | `2` | The month played, counting from one, in which a company of the founding lists: the first month's dividends read no sheet (none is recorded before it: Game.recordMonth runs after payDividends), and the second reads Ret... |
| 538 | `OwnerCheck.LOG` | `new java.io.ByteArrayOutputStream()` | The game's log while a city runs: kept, and read for the families that rejoin the population and why. |
| 621 | `OwnerCheck.PROBE_MONTHS` | `{ 1, 13, 37, 61, 121 }` | The months the probe prints: BO1's table's, with the founding month. |
| 687 | `OwnerCheck.PROBE_HEADER` | `" line month people retired disp/hh pension paid careAff(SENIOR) senior pull ...` | The probe's header, above its rows. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 57 | `static int fails` |  |
| 294 | `Exchange x` |  |
| 295 | `Equity reg` |  |
| 296 | `HouseholdBalance hb` |  |
| 297 | `Household owner` |  |
| 298 | `double holding, book` |  |
| 299 | `String who` |  |
| 707 | `final int n` |  |
| 708 | `final boolean[] had, leftOnce` |  |
| 709 | `int formed, dropped, droppedAtFloor, droppedFailed, reformed, emptyMonths, emptyBefore, logged` |  |
| 710 | `boolean cellEverHeld` |  |
| 712 | `double lastCensus` | The owner families the census counted last month. |
| 714 | `String gap` | The first month each check failed, said in the log: what the run found, not only that it did. |
| 716 | `final java.util.TreeMap<String, Integer> otherwise` | Companies in issue with no family, never granted to founders: the bank (its desk issues its shares), a company that offered on a book of nothing. |
| 718 | `double crumbs` | The most of a company without a family the owners ever held, as a part of its shares in issue. |
| 719 | `boolean countsAgreed` |  |
| 732 | `int listedMonth` | The first month that opened with a company unlisted, with no family, on a positive book; the company; whether it had its family when the month ended. |
| 733 | `String listedCompany` |  |
| 734 | `boolean listedFamily` |  |
| 736 | `int offerMonth` | The first month that opened with the exchange open and the owner cell holding a stake in a company in issue above its floor; whether the owners' offers rose in it. |
| 737 | `boolean offerRose` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 55 | 938 | **type** `public class OwnerCheck` | The owner families (BO1): one two-adult household per company that has listed and not gone broke, holding the company's founding stake alone. |
| 59 | 4 | `static void check(String label, boolean ok)` |  |
| 65 | 5 | `static void close(String label, double actual, double expected, double rel)` | Relative to the larger of the two, so a figure of any size is held to the same 1e-9. |
| 75 | 8 | `static HouseholdBalance town(boolean lineOn)` | A bare balance in today's dollar: a hundred unskilled couples and forty skilled single adults, owners as the census says. |
| 85 | 3 | `static void month(HouseholdBalance hb)` | One month's census, with the owner count in owners[0]. |
| 90 | 11 | `static void month(HouseholdBalance hb, int couples)` | ...with this many unskilled couples (the town's ordinary working cell) beside the forty single adults. |
| 102 | 5 | `static Equity register()` |  |
| 108 | 181 | `public static void main(String[] args)` |  |

### 6. the line sells a twentieth a year (lines 290-382)

| line | len | member | says |
|---:|---:|---|---|
| 293 | 8 | **type** `static final class Sale` | The exchange's first month with the line on and one family holding every founders' share of one company. |
| 306 | 24 | `static Sale sale(int company, boolean floor, boolean control)` |  |
| 331 | 51 | `static void sells(int IND, int RETAIL)` |  |

### 7. a year at the floor sends the family back (lines 383-456)

| line | len | member | says |
|---:|---:|---|---|
| 388 | 68 | `static void sendsBack(int IND, int RETAIL)` |  |

### 8. the leaving family takes its money, not the shares (lines 457-521)

| line | len | member | says |
|---:|---:|---|---|
| 463 | 15 | `static HouseholdBalance leaving(int IND, int RETAIL, int ownersNext, int couplesNext)` | A town with four owner families, savings and money abroad in the owner cell and its shares in two companies, a working couple cell with some of its own, and a second month's census of these owner and couple counts. |
| 479 | 42 | `static void takesMoneyNotShares(int IND, int RETAIL)` |  |

### 9. a real city: one family per listed company (lines 522-617)

| line | len | member | says |
|---:|---:|---|---|
| 540 | 77 | `static void realCity()` |  |

### the probe (BO1-S): the playtest city, line on against line off; -Downer.probe=true runs it alone, never the default (lines 618-871)

| line | len | member | says |
|---:|---:|---|---|
| 629 | 6 | `static void probe()` | BO1's section 1 re-measured on today's model: the playtest city (the rhythm of section 9) played to month 121 twice, the owner line on and off, printing at each of PROBE_MONTHS the people, the retired's disposable inc... |
| 636 | 36 | `static void probeRun(boolean lineOn)` |  |
| 673 | 6 | `static void probePlay(Game g, int months, boolean lineOn, java.util.List<String> rows)` |  |
| 680 | 5 | `static void probeRow(Game g, boolean lineOn, java.util.List<String> rows)` |  |
| 691 | 13 | `static String probeLine(Game g, boolean lineOn)` | The probe's row for this month, or null at a month it does not print; LongPlaytest prints it under -Dplaytest.owners. |
| 706 | 152 | **type** `static final class Tally` | What a real run counts, month by month. |
| 721 | 5 | `Tally(int n)` _(in OwnerCheck.Tally)_ |  |
| 739 | 18 | `void play(Game g, int months)` _(in OwnerCheck.Tally)_ |  |
| 759 | 8 | `static int unlistedOnAPositiveBook(Game g)` _(in OwnerCheck.Tally)_ | A company the month's dividends would list (Equity.listIfUnlisted): none in issue, no family, a positive book; -1 if none. |
| 769 | 8 | `static boolean holdsAboveTheFloor(Game g)` _(in OwnerCheck.Tally)_ | Whether the owners would offer this month (Exchange.postOwners): the exchange open, the cell not empty, a stake in a company in issue above its floor. |
| 778 | 79 | `void audit(Game g)` _(in OwnerCheck.Tally)_ |  |
| 860 | 5 | `static double inFamilies(FamilyModel f)` | The adults the family matrix holds. |
| 866 | 5 | `static double sum(double[] v)` |  |

### 10. a save from before the owner families loads its rows (lines 872-924)

| line | len | member | says |
|---:|---:|---|---|
| 881 | 43 | `static void oldSaves()` | Main's shapes fed to the two readers keyed to the rows: the statement and the row array of a 0.9.88 save, each row array one row short (the owner row cut from today's). |

### 11. at most a share of the adults (BO1-S) (lines 925-992)

| line | len | member | says |
|---:|---:|---|---|
| 928 | 7 | `static PopulationCohorts adultsOnly(double adults)` | A cohort of this many adults and nobody else: the town's adult pool, as section 5's pyramid is restored. |
| 942 | 50 | `static void shareOfTheAdults(int IND, int RETAIL)` | Two adults a company, whatever the town, took a founding town's whole labour force (the OwnerHousehold javadoc): the cap, caused on the census by the adults, and what it leaves the cell and the listings. |


# PartnersCheck.java - 830 lines · 18 methods · 3 constants · harnesses

`ham/citybuildersim/PartnersCheck.java` - generated 2026-11-01 by CodeMap; line numbers are as of that run.

> The model the Trade screen's Partners board, a nation's page and The reserves read (batch WN6, "Nations: the screens"; docs/studies/spec-nations.md 8,
> boards 2 and 3, 9, D29): each good's curve at the city's site (WorldMarket.Curve), the partners table and a nation's page (PartnersView), and the
> vault in eight (VaultView). No screen is drawn here and nothing in the month reads any of it: this holds the figures a screen will print, to the
> model's own getters, so that a screen never has to recompute one.
> 
> WHY. The band was two numbers and the allocation an array; the sinks, where each buyer joins, who sets the level and the world's line were in the
> market's private fills, read only by a harness on the model's thread. A screen that rebuilt them would carry its own idea of the sale side; one that
> read the live fills from its own thread would read half a rebuild. So the curve is a snapshot, published whole, whose floors and ceilings and
> allocations are the band's and allocate()'s own code, and every other board figure here is held to the getter it comes from.
> 
> What this has to prove:
>   1. THE CURVE IS THE BAND (a world-file city, Historical, a fixed seed, run to trade): for every good with a curve, at last month's exports and
>      imports and at volumes up the world's use, floorAt/ceilingAt equal band() to the bit and buyersAt/sellersAt equal allocate()'s arrays to the
>      bit; the buyers taking at a volume are the sinks above the level and each nets at least it; the joins ascend; the next buyers all join beyond the
>      volume and none is taking; the month's line never rises with the volume and the world's line is never above it; the grid ends where the level does.
>   2. A LOADED CITY HAS ITS CURVES: saved to a scratch folder and loaded, before any month, the curves are the original's to the bit (sinks, sellers,
>      floors, grid).
>   3. THE PARTNERS TABLE FOOTS, both spans: the rows plus the no-partner remainder are the snapshot's month (or year); the order is by what was sold
>      and bought, ranks 1..n; each row's money is its nation's this year and its mark the eight's or its code; a policy rate exactly where the money
>      is one of the eight; D$ a unit times units a US$ is D$ a US$; its distance is the great circle to its node.
>   4. A NATION'S PAGE: its goods foot to its year's totals; its years are the history's; its ranks are the map card's (a NationsMap built on the file)
>      and the rule's; its land is Game's tallies and the land factors'; the vault's holding of its money and what the city owes in it (CD8) are the
>      getters'; the city's GDP in real 2015 US$ stands beside its output.
>   5. THE VAULT IN EIGHT: the worths add to getReservesUsd() to the bit and the shares to 1; a vault caused to hold two moneys shows both; the month's
>      move is the history's, and a loaded city shows it; the policy rates and what is owed are the getters'.
>   6. A PUBLISHED CURVE NEVER CHANGES: a curve read before a solve is the same after it, the market publishes a new one, and a thread that reads while
>      the model solves sees only whole curves.
>   7. AN EARTH CITY (where the game's Earth is here): Halifax's distances are from its own spot, and its land factors, marks and prices are the land
>      sellers' - the branch a generated world's city (no land sellers, a stand-in's node) leaves out.
>   8. A GOOD'S BOARD (WN6, the screens' second slice): the volumes it is asked at are the tally's month (the snapshot's, which is the live record's),
>      its floor and ceiling the curve's at them, its conversion into the city's money the market's own rate, the goods it offers exactly those with
>      a curve (the sold ones, by the books, largest first), the parts of a sale or a purchase the curve's allocation, adding to 1, and what moves it the
>      curve's three differences; and Game's year of real GDP is the page's.

**Uses:** [PartnersView](PartnersView.md) (51), [WorldMarket](WorldMarket.md) (30), [Game](Game.md) (20), [Good](Good.md) (18), [ForeignAccounts](ForeignAccounts.md) (12), [VaultView](VaultView.md) (11), [PartnerTrade](PartnerTrade.md) (10), [Nations](Nations.md) (9), [LandNationsCheck](LandNationsCheck.md) (9), [NationsMap](NationsMap.md) (7), [WorldData](WorldData.md) (6), [Founding](Founding.md) (2), [PolicyRates](PolicyRates.md) (2), [EarthProj](EarthProj.md) (2), [LandSellers](LandSellers.md) (2), [LandFactors](LandFactors.md) (2), [DebtMoneysCheck](DebtMoneysCheck.md) (2), [DebtManager](DebtManager.md) (2), [HistorySave](HistorySave.md) (2), [EarthSite](EarthSite.md) (2), [RealDollarCheck](RealDollarCheck.md) (1), [GameFiles](GameFiles.md) (1), [WorldMoneys](WorldMoneys.md) (1), [GoodsMarket](GoodsMarket.md) (1), [Sectors](Sectors.md) (1)

## Sections

| line | section |
|---:|---|
| 127 | · 1 |
| 300 | · 2 |
| 338 | · 3 |
| 442 | · 4 |
| 568 | · 5 |
| 649 | · 6 |
| 719 | · 8 |
| 793 | · 7 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 54 | `PartnersCheck.out` | `System.out` |  |
| 55 | `PartnersCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 58 | `PartnersCheck.MONTHS` | `14` | The months the city plays before the sections look: past a January, so a year has finished and the history holds it. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 53 | `static int fails` |  |
| 72 | `int n, bad` |  |
| 73 | `String first` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 51 | 780 | **type** `public class PartnersCheck` | The model the Trade screen's Partners board, a nation's page and The reserves read (batch WN6, "Nations: the screens"; docs/studies/spec-nations.md 8, boards 2 and 3, 9, D29): each good's curve at the city's site (Wor... |
| 60 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 66 | 3 | `static void close(String label, double got, double want, double tol)` | A figure the arithmetic gives, judged to a tolerance (relative to the larger, with an absolute floor of the tolerance). |
| 71 | 12 | **type** `static final class Tally` | Figures compared one by one, reported as one line: every one has to agree, and at least one has to have been read. |
| 75 | 7 | `void see(boolean ok, Supplier<String> what)` _(in PartnersCheck.Tally)_ |  |
| 84 | 6 | `static void tally(String label, Tally t)` |  |
| 91 | 5 | `static void quietly(Runnable work)` |  |
| 97 | 1 | `static void play(Game city)` |  |
| 100 | 1 | `static boolean same(double a, double b)` | Two figures the same to the bit (NaN is NaN). |
| 102 | 24 | `public static void main(String[] args)` |  |

### 1 (lines 127-299)

| line | len | member | says |
|---:|---:|---|---|
| 130 | 4 | `static double[][] volumes(double lastOut, double lastIn, double use)` | The volumes a good is asked at: last month's, none, one side alone, and shares of the world's use up to all of it (game units a month). |
| 141 | 18 | `static int widenTheTrade(Game city)` | Gives the city's tally of the month a trade with many nations, the condition the board needs: a town of five hundred houses sells and buys at the nation next door and nowhere else (the first sink takes it all), so a f... |
| 160 | 139 | `static void theCurveIsTheBand(Game city)` |  |

### 2 (lines 300-337)

| line | len | member | says |
|---:|---:|---|---|
| 302 | 35 | `static Game aLoadedCityHasItsCurves(Game city)` |  |

### 3 (lines 338-441)

| line | len | member | says |
|---:|---:|---|---|
| 340 | 101 | `static void theTableFoots(Game city)` |  |

### 4 (lines 442-567)

| line | len | member | says |
|---:|---:|---|---|
| 444 | 123 | `static void aNationsPage(Game city, Path file)` |  |

### 5 (lines 568-648)

| line | len | member | says |
|---:|---:|---|---|
| 570 | 78 | `static void theVaultInEight(Game city)` |  |

### 6 (lines 649-718)

| line | len | member | says |
|---:|---:|---|---|
| 651 | 67 | `static void aPublishedCurveNeverChanges(Game city, Game back)` |  |

### 8 (lines 719-792)

| line | len | member | says |
|---:|---:|---|---|
| 721 | 71 | `static void aGoodsBoard(Game city)` |  |

### 7 (lines 793-830)

| line | len | member | says |
|---:|---:|---|---|
| 795 | 35 | `static void anEarthCity()` |  |


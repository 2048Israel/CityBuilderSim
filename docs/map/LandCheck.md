# LandCheck.java - 2,079 lines · 24 methods · 2 constants · harnesses

`ham/citybuildersim/LandCheck.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> Verifies the land ledger: what the city owns, what it can allocate, what it
> charges, and that the three numbers never drift apart.
> 
> The one thing worth being paranoid about here is that allocated land can only
> ever go up by exactly what was built. A leak in either direction is invisible
> for a hundred months and then the city is either mysteriously full or
> mysteriously infinite.
> 
> Sections 1-11 are the ledger and the market. The land office has its own
> at the end: land priced in dollars and the two ways to pay for it (0.7.6,
> sections 12-13); and, since 0.7.13, the office in square kilometres (14),
> the funding page a city short of the price is offered, converting and
> from the vault, with the window abroad open and shut (15), and the next N
> plots bought at once ending exactly as N bought one by one (16); and,
> since 0.7.26, the three figures the redrawn office takes from the model:
> the going rate, which it used to work out itself, the GROUND row's
> verdict and the receipt in the screens' money (17); and, since 0.7.55,
> the ground priced by how crowded the city is rather than how big, at the
> world's price level and not the city's (18); and, since 0.7.57, the land
> on the world (spec-land.md, batch J1b): forty offers, ten a side, each the
> next band of its lane, priced from batch I's ground and holding the world's
> fields (5 to 5f), the world's totals kept to the tonne, the ground worked
> out in the order it was bought, ground set by hand, forest's regrowth and
> the best offer for each need (19), a saved city's land field for field
> (20), and an older save's land converted - the three research cities' (21);
> and the playtest's player keeping its ground ahead, as the build advice
> keeps slack (22, batch J1d). From 0.7.58 to 0.7.63 (batch J1c) a field was
> shared site by site among the ground its sites lie under; since 0.7.64
> (batch L) a field goes whole again to the one piece of ground holding its
> centre, an offer priced by its fields' tonnes, a new city's iron a
> significant investment the funding page sizes a bond to (5e, 19); and the
> playtest's player buys its iron a whole field at a time, the cheapest
> standing, with cash or that bond (23).

**Uses:** [LandManager](LandManager.md) (106), [Game](Game.md) (84), [CityLand](CityLand.md) (72), [LandParcel](LandParcel.md) (57), [Resource](Resource.md) (55), [LandMarket](LandMarket.md) (45), [LongPlaytest](LongPlaytest.md) (29), [World](World.md) (16), [Deposit](Deposit.md) (10), [CityNeeds](CityNeeds.md) (10), [GameFiles](GameFiles.md) (8), [LandConversion](LandConversion.md) (8), [ForeignAccounts](ForeignAccounts.md) (7), [LandMap](LandMap.md) (6), [BuildingsTemplate](BuildingsTemplate.md) (4), [MoneyAudit](MoneyAudit.md) (4), [DebtQuote](DebtQuote.md) (4), [Formats](Formats.md) (3), [BuildAdvice](BuildAdvice.md) (3), [Founding](Founding.md) (2), [BuildingManager](BuildingManager.md) (2), [TreasuryJournal](TreasuryJournal.md) (2), [Good](Good.md) (1), [Sectors](Sectors.md) (1)

## Sections

| line | section |
|---:|---|
| 69 | · 1. what the city starts with |
| 81 | · 2. allocating |
| 102 | · 3. filling up |
| 114 | · 4. releasing |
| 123 | · 5. the listing |
| 189 | · 5b. buying one |
| 223 | · 5c. a more crowded city pays more |
| 251 | · 5d. supply and demand inside the city |
| 327 | · 5e. iron in the ground |
| 401 | · 5f. the size rule |
| 484 | · 6. not affording it |
| 501 | · 7. selling |
| 525 | · 8. the player's price |
| 545 | · 9. reset |
| 560 | · 10. every building fits on a starting city |
| 612 | · 11. the price is a density policy |
| 660 | 18. CROWDED, NOT BIG, AND THE DOLLAR'S OWN INFLATION (0.7.55) |
| 755 | 12. LAND IS PRICED IN DOLLARS (0.7.6) |
| 815 | 13. THE TWO WAYS TO PAY (0.7.6) |
| 1017 | 14. THE OFFICE IN SQUARE KILOMETRES (0.7.13) |
| 1047 | 15. SHORT OF THE PRICE: THE FUNDING PAGE (0.7.13) |
| 1176 | 16. THE NEXT N PLOTS, AT ONCE (0.7.13) |
| 1296 | 17. THE OFFICE'S OWN FIGURES, IN THE MODEL (0.7.26) |
| 1395 | 19. THE LAND ON THE WORLD (0.7.57) |
| 1719 | 20. A CITY SAVED AND LOADED IS THE SAME LAND (0.7.57) |
| 1777 | 21. AN OLDER SAVE'S LAND IS CONVERTED (0.7.57) |
| 1898 | 22. THE TEST PLAYER KEEPS ITS GROUND AHEAD (0.7.58, batch J1d) |
| 1957 | 23. THE TEST PLAYER BUYS ITS IRON A WHOLE FIELD AT A TIME (0.7.64, batch L) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 1795 | `LandCheck.RESEARCH` | `{ new Research("city600 m612", 906013741141069877L, 32_635_000, 10, 36200450....` | The three research saves' land (the autosaves at months 612, 2412 and 1851), and a city whose mines outnumber its sites. |
| 1896 | `LandCheck.DEAL` | `7` | The width of the bands the plane is tiled with: 7 plots, which no offer is. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 40 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 38 | 2042 | **type** `public class LandCheck` | Verifies the land ledger: what the city owns, what it can allocate, what it charges, and that the three numbers never drift apart. |
| 42 | 6 | `static void check(String label, double actual, double expected)` |  |
| 49 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 55 | 5 | `static void near(String label, double actual, double expected, double relative)` | ...and to a part in `relative` of the expected figure (0.7.55): prices a ten-thousandth of a unit, where check()'s 1e-6 is no test. |
| 61 | 5 | `static void quietly(Runnable work)` |  |
| 67 | 592 | `public static void main(String[] args) throws Exception` |  |

### 18. CROWDED, NOT BIG, AND THE DOLLAR'S OWN INFLATION (0.7.55) (lines 660-754)

| line | len | member | says |
|---:|---:|---|---|
| 673 | 81 | `static void crowdedNotBig()` |  |

### 12. LAND IS PRICED IN DOLLARS (0.7.6) (lines 755-814)

| line | len | member | says |
|---:|---:|---|---|
| 765 | 49 | `static void inDollars()` |  |

### 13. THE TWO WAYS TO PAY (0.7.6) (lines 815-1016)

| line | len | member | says |
|---:|---:|---|---|
| 826 | 6 | `static Game dollarCity(String label)` |  |
| 833 | 5 | `static double[] moved(double[] before, double[] after)` |  |
| 839 | 177 | `static void bothWays() throws Exception` |  |

### 14. THE OFFICE IN SQUARE KILOMETRES (0.7.13) (lines 1017-1046)

| line | len | member | says |
|---:|---:|---|---|
| 1025 | 21 | `static void inSquareKilometres()` |  |

### 15. SHORT OF THE PRICE: THE FUNDING PAGE (0.7.13) (lines 1047-1175)

| line | len | member | says |
|---:|---:|---|---|
| 1062 | 113 | `static void whenShort() throws Exception` |  |

### 16. THE NEXT N PLOTS, AT ONCE (0.7.13) (lines 1176-1295)

| line | len | member | says |
|---:|---:|---|---|
| 1191 | 104 | `static void severalAtOnce() throws Exception` |  |

### 17. THE OFFICE'S OWN FIGURES, IN THE MODEL (0.7.26) (lines 1296-1394)

| line | len | member | says |
|---:|---:|---|---|
| 1311 | 71 | `static void theOfficesFigures()` |  |
| 1384 | 10 | `static java.util.List<LandParcel> handMade(double[] perSqFt, double[] sizes, int n)` | A listing of `n` offers by hand: dry ground of each size at its dollars a square foot, ids from 1, each in its own lane, no ore. |

### 19. THE LAND ON THE WORLD (0.7.57) (lines 1395-1718)

| line | len | member | says |
|---:|---:|---|---|
| 1405 | 313 | `static void onTheWorld()` |  |

### 20. A CITY SAVED AND LOADED IS THE SAME LAND (0.7.57) (lines 1719-1776)

| line | len | member | says |
|---:|---:|---|---|
| 1728 | 48 | `static void savedAndLoaded() throws Exception` |  |

### 21. AN OLDER SAVE'S LAND IS CONVERTED (0.7.57) (lines 1777-1897)

| line | len | member | says |
|---:|---:|---|---|
| 1792 | 1 | **type** `record Research(String name, long seed, double landOwned, int ironSites, double ironTonnes, int mines)` | One research city's land as its save carries it: its name, its world's seed, its square feet, iron sites and tonnes, and its Iron Mines. |
| 1802 | 72 | `static void converted() throws Exception` |  |
| 1876 | 18 | `static Deposit nearestUnowned(CityLand land, Resource kind)` | The iron field nearest a city's site, in the nine cells round it, whose centre the city does not own; null when none. |

### 22. THE TEST PLAYER KEEPS ITS GROUND AHEAD (0.7.58, batch J1d) (lines 1898-1956)

| line | len | member | says |
|---:|---:|---|---|
| 1909 | 47 | `static void groundKeptAhead()` |  |

### 23. THE TEST PLAYER BUYS ITS IRON A WHOLE FIELD AT A TIME (0.7.64, batch L) (lines 1957-2079)

| line | len | member | says |
|---:|---:|---|---|
| 1974 | 67 | `static void ironAWholeFieldAtATime()` |  |
| 2043 | 11 | `static Game ironListed(String label)` | A new city at 1.6 to the dollar whose lane holding the founding field has been pushed out, by hand, until that field is listed. |
| 2056 | 7 | `static double[] bandFields(CityLand land, LandParcel offer, Resource kind)` | An offer's fields of one resource, recounted from the world: every field whose centre lies in its band, whole - its sites and its amount (0.7.58 to 0.7.63: every site whose own centre did, and its share). |
| 2065 | 13 | `static double[] recount(CityLand land, Resource kind, double out, java.util.function.BiPredicate<Double, Double> in)` | Every field of a resource centred within `out` plots of a city's site (L-infinity) whose centre, seen from the site, passes `in`: its sites and its amount, whole - read off the world's cells, with none of CityLand's s... |


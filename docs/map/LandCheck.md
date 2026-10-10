# LandCheck.java - 2,497 lines · 32 methods · 2 constants · harnesses

`ham/citybuildersim/LandCheck.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

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
> on the world (spec-land.md, batch J1b): offers priced from batch I's ground
> and holding the world's fields (5 to 5f) - forty, ten a side, each the next
> band of its lane, until 0.7.66; since 0.7.67 (batch M3, spec-grid.md) six
> places a side, each a rectangle of whole blocks against the city or none
> while its side has no room, listed once there is, the city's level setting
> the blocks and the books its plots counted - the world's totals kept to the tonne, the ground worked
> out in the order it was bought, ground set by hand, forest's regrowth and
> the best offer for each need (19), a saved city's land field for field
> (20), and an older save's land converted - the three research cities' (21);
> and the playtest's player keeping its ground ahead, as the build advice
> keeps slack (22, batch J1d; since 0.7.67, batch M3b, its room to grow
> weighed from the same line). From 0.7.58 to 0.7.63 (batch J1c) a field was
> shared site by site among the ground its sites lie under; since 0.7.64
> (batch L) a field goes whole again to the one piece of ground holding its
> centre, an offer priced by its fields' tonnes, a new city's iron a
> significant investment the funding page sizes a bond to (5e, 19); and the
> playtest's player buys its iron a whole field at a time, the cheapest
> standing, with cash or that bond - since 0.7.67 (batch M3b) only once the
> mines it would carry pay it back (23); and, since 0.7.68 (batch M4), the
> units the player reads land in - square metres under a hundredth of a
> square kilometre, square kilometres from it, ground prices a square
> metre (24).

**Uses:** [LandManager](LandManager.md) (158), [Game](Game.md) (86), [CityLand](CityLand.md) (77), [LandParcel](LandParcel.md) (71), [World](World.md) (68), [Resource](Resource.md) (66), [LandMarket](LandMarket.md) (46), [LongPlaytest](LongPlaytest.md) (34), [LandGrid](LandGrid.md) (14), [Deposit](Deposit.md) (14), [CityNeeds](CityNeeds.md) (10), [GameFiles](GameFiles.md) (8), [ForeignAccounts](ForeignAccounts.md) (7), [LandConversion](LandConversion.md) (7), [MiningCheck](MiningCheck.md) (6), [LandMap](LandMap.md) (6), [BuildingsTemplate](BuildingsTemplate.md) (5), [LegacyLand](LegacyLand.md) (5), [MoneyAudit](MoneyAudit.md) (4), [DebtQuote](DebtQuote.md) (4), [Formats](Formats.md) (3), [GridConversion](GridConversion.md) (3), [BuildAdvice](BuildAdvice.md) (3), [BuildingManager](BuildingManager.md) (2), [TreasuryJournal](TreasuryJournal.md) (2), [Founding](Founding.md) (2), [Sectors](Sectors.md) (2), [GridOffers](GridOffers.md) (1), [Good](Good.md) (1), [GridCheck](GridCheck.md) (1)

## Sections

| line | section |
|---:|---|
| 77 | · 1. what the city starts with |
| 152 | · 2. allocating |
| 174 | · 3. filling up |
| 186 | · 4. releasing |
| 195 | · 5. the listing |
| 284 | · 5b. buying one |
| 352 | · 5c. a more crowded city pays more |
| 380 | · 5d. supply and demand inside the city |
| 456 | · 5e. iron in the ground |
| 532 | · 5f. the level rule |
| 630 | · 6. not affording it |
| 648 | · 7. selling |
| 672 | · 8. the player's price |
| 692 | · 9. reset |
| 707 | · 10. every building fits on a starting city |
| 768 | · 11. the price is a density policy |
| 817 | 18. CROWDED, NOT BIG, AND THE DOLLAR'S OWN INFLATION (0.7.55) |
| 940 | 12. LAND IS PRICED IN DOLLARS (0.7.6) |
| 1000 | 13. THE TWO WAYS TO PAY (0.7.6) |
| 1205 | 14. THE OFFICE IN SQUARE KILOMETRES (0.7.13) |
| 1235 | 24. THE UNITS THE PLAYER READS (0.7.68, batch M4) |
| 1298 | 15. SHORT OF THE PRICE: THE FUNDING PAGE (0.7.13) |
| 1427 | 16. THE NEXT N PLOTS, AT ONCE (0.7.13) |
| 1547 | 17. THE OFFICE'S OWN FIGURES, IN THE MODEL (0.7.26) |
| 1646 | · the grid's own tests (0.7.67) |
| 1694 | 19. THE LAND ON THE WORLD (0.7.57) |
| 2043 | 20. A CITY SAVED AND LOADED IS THE SAME LAND (0.7.57) |
| 2102 | 21. AN OLDER SAVE'S LAND IS CONVERTED (0.7.57) |
| 2267 | 22. THE TEST PLAYER KEEPS ITS GROUND AHEAD (0.7.58, batch J1d) |
| 2350 | 23. THE TEST PLAYER BUYS ITS IRON A WHOLE FIELD AT A TIME (0.7.64, batch L) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 2122 | `LandCheck.RESEARCH` | `{ new Research("city600 m612", 906013741141069877L, 32_635_000, 10, 36200450....` | The three research saves' land (the autosaves at months 612, 2412 and 1851), and a city whose mines outnumber its sites. |
| 2265 | `LandCheck.DEAL` | `7` | Read by nothing since 0.7.67: the width of the bands section 19 tiled the plane with until then, 7 plots, which no offer was - the pieces are the holdings and the offers standing now. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 48 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 46 | 2452 | **type** `public class LandCheck` | Verifies the land ledger: what the city owns, what it can allocate, what it charges, and that the three numbers never drift apart. |
| 50 | 6 | `static void check(String label, double actual, double expected)` |  |
| 57 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 63 | 5 | `static void near(String label, double actual, double expected, double relative)` | ...and to a part in `relative` of the expected figure (0.7.55): prices a ten-thousandth of a unit, where check()'s 1e-6 is no test. |
| 69 | 5 | `static void quietly(Runnable work)` |  |
| 75 | 741 | `public static void main(String[] args) throws Exception` |  |

### 18. CROWDED, NOT BIG, AND THE DOLLAR'S OWN INFLATION (0.7.55) (lines 817-939)

| line | len | member | says |
|---:|---:|---|---|
| 833 | 80 | `static void crowdedNotBig()` |  |
| 920 | 19 | `static LandManager timesOver(LandManager town, long k)` | A land office holding a city's land k times over, by its records (0.7.67): the town's blocks, holdings and offers, each holding's five areas and its forest's timber times k, and the figure times k - a copy K times ove... |

### 12. LAND IS PRICED IN DOLLARS (0.7.6) (lines 940-999)

| line | len | member | says |
|---:|---:|---|---|
| 950 | 49 | `static void inDollars()` |  |

### 13. THE TWO WAYS TO PAY (0.7.6) (lines 1000-1204)

| line | len | member | says |
|---:|---:|---|---|
| 1011 | 6 | `static Game dollarCity(String label)` |  |
| 1018 | 5 | `static double[] moved(double[] before, double[] after)` |  |
| 1024 | 180 | `static void bothWays() throws Exception` |  |

### 14. THE OFFICE IN SQUARE KILOMETRES (0.7.13) (lines 1205-1234)

| line | len | member | says |
|---:|---:|---|---|
| 1213 | 21 | `static void inSquareKilometres()` |  |

### 24. THE UNITS THE PLAYER READS (0.7.68, batch M4) (lines 1235-1297)

| line | len | member | says |
|---:|---:|---|---|
| 1246 | 43 | `static void inThePlayersUnits()` |  |
| 1291 | 6 | `static boolean readsAs(String words, double squareMetres)` | Whether words like "7,430 m2" or "0.0301 km2" read `squareMetres` to half a percent, never as nothing. |

### 15. SHORT OF THE PRICE: THE FUNDING PAGE (0.7.13) (lines 1298-1426)

| line | len | member | says |
|---:|---:|---|---|
| 1313 | 113 | `static void whenShort() throws Exception` |  |

### 16. THE NEXT N PLOTS, AT ONCE (0.7.13) (lines 1427-1546)

| line | len | member | says |
|---:|---:|---|---|
| 1442 | 104 | `static void severalAtOnce() throws Exception` |  |

### 17. THE OFFICE'S OWN FIGURES, IN THE MODEL (0.7.26) (lines 1547-1645)

| line | len | member | says |
|---:|---:|---|---|
| 1562 | 71 | `static void theOfficesFigures()` |  |
| 1635 | 10 | `static java.util.List<LandParcel> handMade(double[] perSqFt, double[] sizes, int n)` | A listing of `n` offers by hand: dry ground of each size at its dollars a square foot, ids from 1, each in its own place on no ground of the grid, no ore. |

### the grid's own tests (0.7.67) (lines 1646-1693)

| line | len | member | says |
|---:|---:|---|---|
| 1649 | 3 | `static boolean touchesOwned(LandGrid g, LandParcel p)` | Whether an offer's rectangle holds or borders ground the city owns (GridCheck.touches()). |
| 1654 | 17 | `static double[] recountOn(CityLand land, Resource kind, java.util.function.BiPredicate<Long, Long> on)` | Every field of a resource whose centre plot passes `on`, within the nine cells round a city's site and its owned box's: its sites and amount, whole - read off the world's cells. |
| 1673 | 3 | `static double[] offerFields(CityLand land, LandParcel offer, Resource kind)` | An offer's fields of one resource, recounted from the world: every field whose centre plot is one of its rectangle's free plots, whole (spec-land star 12 on the grid). |
| 1678 | 15 | `static double[] freePlotsCounted(CityLand land, LandParcel p)` | An offer's five areas counted again, plot by plot from the world's tiles: the plots of its rectangle the city does not own (0.7.67, the books to the plot). |

### 19. THE LAND ON THE WORLD (0.7.57) (lines 1694-2042)

| line | len | member | says |
|---:|---:|---|---|
| 1704 | 338 | `static void onTheWorld()` |  |

### 20. A CITY SAVED AND LOADED IS THE SAME LAND (0.7.57) (lines 2043-2101)

| line | len | member | says |
|---:|---:|---|---|
| 2053 | 48 | `static void savedAndLoaded() throws Exception` |  |

### 21. AN OLDER SAVE'S LAND IS CONVERTED (0.7.57) (lines 2102-2266)

| line | len | member | says |
|---:|---:|---|---|
| 2119 | 1 | **type** `record Research(String name, long seed, double landOwned, int ironSites, double ironTonnes, int mines)` | One research city's land as its save carries it: its name, its world's seed, its square feet, iron sites and tonnes, and its Iron Mines. |
| 2129 | 76 | `static void converted() throws Exception` |  |
| 2207 | 18 | `static Deposit nearestUnowned(CityLand land, Resource kind)` | The iron field nearest a city's site, in the nine cells round it, whose centre the city does not own; null when none. |
| 2227 | 19 | `static Deposit nearestOnDryGround(CityLand land, Resource kind)` | The field of a resource nearest a city's site, in the nine cells round it, whose centre plot is dry ground and not the city's (0.7.99): where foundedOn() puts a fixture's city. |
| 2256 | 7 | `static LandManager foundedOn(long x, long y)` | A land office whose city is founded on the default world at plot (x, y) rather than its founding site (0.7.99, batch W1): a centre of whole blocks round it holding a new city's dry ground (CityLand.found()), nothing t... |

### 22. THE TEST PLAYER KEEPS ITS GROUND AHEAD (0.7.58, batch J1d) (lines 2267-2349)

| line | len | member | says |
|---:|---:|---|---|
| 2281 | 68 | `static void groundKeptAhead()` |  |

### 23. THE TEST PLAYER BUYS ITS IRON A WHOLE FIELD AT A TIME (0.7.64, batch L) (lines 2350-2497)

| line | len | member | says |
|---:|---:|---|---|
| 2378 | 91 | `static void ironAWholeFieldAtATime()` |  |
| 2471 | 10 | `static Game ironListed(String label)` | A new city at 1.6 to the dollar grown, by hand, toward the founding field - the offer nearest it each time - until an offer holds iron. |
| 2483 | 13 | `static double[] recount(CityLand land, Resource kind, double out, java.util.function.BiPredicate<Double, Double> in)` | Every field of a resource centred within `out` plots of a city's site (L-infinity) whose centre, seen from the site, passes `in`: its sites and its amount, whole - read off the world's cells, with none of CityLand's s... |


# LandCheck.java - 2,812 lines · 36 methods · 3 constants · harnesses

`ham/citybuildersim/LandCheck.java` - generated 2026-10-22 by CodeMap; line numbers are as of that run.

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
> metre (24); and, since 0.7.107 (batch Z1), the ledger with zones and the
> forest law - a sector's allocation on its zone's ground first, the three
> numbers still closing, forest dry ground a building can take until a law
> keeps it (25; ZoningCheck plays the rest); and, since 0.7.110 (batch I1),
> the ground by type - sites and tonnes by type adding up to the whole, E
> by type the walk's reading of E until a works digs by type, a typed dig
> moving its own type's E and nothing else, W by type to the tonne, the
> state through a save, and where a works fits (fits()), pure (26); and,
> since 0.7.138 (batch I9), where Forestry's cut and the clearing meet - the
> clearing taking the crews' cut-over ground first, the salvage the standing
> wood alone and taken once, cleared ground never grown back by the planted
> stands, W exact (27).

**Uses:** [LandManager](LandManager.md) (191), [Game](Game.md) (83), [Resource](Resource.md) (78), [CityLand](CityLand.md) (75), [LandParcel](LandParcel.md) (71), [World](World.md) (67), [LandMarket](LandMarket.md) (43), [LongPlaytest](LongPlaytest.md) (35), [Deposit](Deposit.md) (26), [LandGrid](LandGrid.md) (14), [MiningCheck](MiningCheck.md) (12), [Sectors](Sectors.md) (12), [CityNeeds](CityNeeds.md) (10), [GameFiles](GameFiles.md) (8), [Mining](Mining.md) (8), [ForeignAccounts](ForeignAccounts.md) (7), [BuildingsTemplate](BuildingsTemplate.md) (6), [LandMap](LandMap.md) (6), [LegacyLand](LegacyLand.md) (5), [MoneyAudit](MoneyAudit.md) (4), [DebtQuote](DebtQuote.md) (4), [BuildingManager](BuildingManager.md) (3), [LandConversion](LandConversion.md) (3), [Formats](Formats.md) (3), [GridConversion](GridConversion.md) (3), [BuildAdvice](BuildAdvice.md) (3), [Good](Good.md) (2), [TreasuryJournal](TreasuryJournal.md) (2), [SaveFileCheck](SaveFileCheck.md) (2), [Founding](Founding.md) (2)... and 3 more

**Used by (4):** [CoalMiningCheck](CoalMiningCheck.md), [CopperMiningCheck](CopperMiningCheck.md), [MaterialsCheck](MaterialsCheck.md), [QuarryingCheck](QuarryingCheck.md)

## Sections

| line | section |
|---:|---|
| 90 | · 1. what the city starts with |
| 165 | · 2. allocating |
| 187 | · 3. filling up |
| 199 | · 4. releasing |
| 208 | · 5. the listing |
| 297 | · 5b. buying one |
| 365 | · 5c. a more crowded city pays more |
| 393 | · 5d. supply and demand inside the city |
| 469 | · 5e. iron in the ground |
| 545 | · 5f. the level rule |
| 643 | · 6. not affording it |
| 661 | · 7. selling |
| 685 | · 8. the player's price |
| 705 | · 9. reset |
| 720 | · 10. every building fits on a starting city |
| 781 | · 11. the price is a density policy |
| 833 | 18. CROWDED, NOT BIG, AND THE DOLLAR'S OWN INFLATION (0.7.55) |
| 956 | 12. LAND IS PRICED IN DOLLARS (0.7.6) |
| 1016 | 13. THE TWO WAYS TO PAY (0.7.6) |
| 1214 | 14. THE OFFICE IN SQUARE KILOMETRES (0.7.13) |
| 1244 | 24. THE UNITS THE PLAYER READS (0.7.68, batch M4) |
| 1307 | 15. SHORT OF THE PRICE: THE FUNDING PAGE (0.7.13) |
| 1436 | 16. THE NEXT N PLOTS, AT ONCE (0.7.13) |
| 1556 | 17. THE OFFICE'S OWN FIGURES, IN THE MODEL (0.7.26) |
| 1655 | · the grid's own tests (0.7.67) |
| 1703 | 19. THE LAND ON THE WORLD (0.7.57) |
| 2068 | 20. A CITY SAVED AND LOADED IS THE SAME LAND (0.7.57) |
| 2127 | 21. AN OLDER SAVE'S LAND IS CONVERTED (0.7.57) |
| 2258 | 22. THE TEST PLAYER KEEPS ITS GROUND AHEAD (0.7.58, batch J1d) |
| 2341 | 23. THE TEST PLAYER BUYS ITS IRON A WHOLE FIELD AT A TIME (0.7.64, batch L) |
| 2490 | 25. THE LEDGER WITH ZONES AND THE FOREST LAW (0.7.107, batch Z1) |
| 2536 | 26. THE GROUND BY TYPE (0.7.110, batch I1; docs/studies/spec-materials.md 2.4) |
| 2768 | 27. WHERE THE CUT AND THE CLEARING MEET (0.7.138, batch I9; |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 1714 | `LandCheck.IRON_IN_THE_GROUND` | `.0004` | Iron ore in the ground, a tonne in thousands: US$0.40, the land office's charge since 0.7.57, kept when ore fell to its world price (Jerus's D, 2026-10-08; 0.7.120). |
| 2154 | `LandCheck.RESEARCH` | `{ new Research("city600 m612", 906013741141069877L, 32_635_000, 10, 36200450....` | The three research saves' land (the autosaves at months 612, 2412 and 1851), and a city whose mines outnumber its sites. |
| 2256 | `LandCheck.DEAL` | `7` | Read by nothing since 0.7.67: the width of the bands section 19 tiled the plane with until then, 7 plots, which no offer was - the pieces are the holdings and the offers standing now. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 61 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 59 | 2754 | **type** `public class LandCheck` | Verifies the land ledger: what the city owns, what it can allocate, what it charges, and that the three numbers never drift apart. |
| 63 | 6 | `static void check(String label, double actual, double expected)` |  |
| 70 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 76 | 5 | `static void near(String label, double actual, double expected, double relative)` | ...and to a part in `relative` of the expected figure (0.7.55): prices a ten-thousandth of a unit, where check()'s 1e-6 is no test. |
| 82 | 5 | `static void quietly(Runnable work)` |  |
| 88 | 744 | `public static void main(String[] args) throws Exception` |  |

### 18. CROWDED, NOT BIG, AND THE DOLLAR'S OWN INFLATION (0.7.55) (lines 833-955)

| line | len | member | says |
|---:|---:|---|---|
| 849 | 80 | `static void crowdedNotBig()` |  |
| 936 | 19 | `static LandManager timesOver(LandManager town, long k)` | A land office holding a city's land k times over, by its records (0.7.67): the town's blocks, holdings and offers, each holding's five areas and its forest's timber times k, and the figure times k - a copy K times ove... |

### 12. LAND IS PRICED IN DOLLARS (0.7.6) (lines 956-1015)

| line | len | member | says |
|---:|---:|---|---|
| 966 | 49 | `static void inDollars()` |  |

### 13. THE TWO WAYS TO PAY (0.7.6) (lines 1016-1213)

| line | len | member | says |
|---:|---:|---|---|
| 1027 | 6 | `static Game dollarCity(String label)` |  |
| 1034 | 5 | `static double[] moved(double[] before, double[] after)` |  |
| 1040 | 173 | `static void bothWays() throws Exception` |  |

### 14. THE OFFICE IN SQUARE KILOMETRES (0.7.13) (lines 1214-1243)

| line | len | member | says |
|---:|---:|---|---|
| 1222 | 21 | `static void inSquareKilometres()` |  |

### 24. THE UNITS THE PLAYER READS (0.7.68, batch M4) (lines 1244-1306)

| line | len | member | says |
|---:|---:|---|---|
| 1255 | 43 | `static void inThePlayersUnits()` |  |
| 1300 | 6 | `static boolean readsAs(String words, double squareMetres)` | Whether words like "7,430 m2" or "0.0301 km2" read `squareMetres` to half a percent, never as nothing. |

### 15. SHORT OF THE PRICE: THE FUNDING PAGE (0.7.13) (lines 1307-1435)

| line | len | member | says |
|---:|---:|---|---|
| 1322 | 113 | `static void whenShort() throws Exception` |  |

### 16. THE NEXT N PLOTS, AT ONCE (0.7.13) (lines 1436-1555)

| line | len | member | says |
|---:|---:|---|---|
| 1451 | 104 | `static void severalAtOnce() throws Exception` |  |

### 17. THE OFFICE'S OWN FIGURES, IN THE MODEL (0.7.26) (lines 1556-1654)

| line | len | member | says |
|---:|---:|---|---|
| 1571 | 71 | `static void theOfficesFigures()` |  |
| 1644 | 10 | `static java.util.List<LandParcel> handMade(double[] perSqFt, double[] sizes, int n)` | A listing of `n` offers by hand: dry ground of each size at its dollars a square foot, ids from 1, each in its own place on no ground of the grid, no ore. |

### the grid's own tests (0.7.67) (lines 1655-1702)

| line | len | member | says |
|---:|---:|---|---|
| 1658 | 3 | `static boolean touchesOwned(LandGrid g, LandParcel p)` | Whether an offer's rectangle holds or borders ground the city owns (GridCheck.touches()). |
| 1663 | 17 | `static double[] recountOn(CityLand land, Resource kind, java.util.function.BiPredicate<Long, Long> on)` | Every field of a resource whose centre plot passes `on`, within the nine cells round a city's site and its owned box's: its sites and amount, whole - read off the world's cells. |
| 1682 | 3 | `static double[] offerFields(CityLand land, LandParcel offer, Resource kind)` | An offer's fields of one resource, recounted from the world: every field whose centre plot is one of its rectangle's free plots, whole (spec-land star 12 on the grid). |
| 1687 | 15 | `static double[] freePlotsCounted(CityLand land, LandParcel p)` | An offer's five areas counted again, plot by plot from the world's tiles: the plots of its rectangle the city does not own (0.7.67, the books to the plot). |

### 19. THE LAND ON THE WORLD (0.7.57) (lines 1703-2067)

| line | len | member | says |
|---:|---:|---|---|
| 1716 | 351 | `static void onTheWorld()` |  |

### 20. A CITY SAVED AND LOADED IS THE SAME LAND (0.7.57) (lines 2068-2126)

| line | len | member | says |
|---:|---:|---|---|
| 2078 | 48 | `static void savedAndLoaded() throws Exception` |  |

### 21. AN OLDER SAVE'S LAND IS CONVERTED (0.7.57) (lines 2127-2257)

| line | len | member | says |
|---:|---:|---|---|
| 2151 | 1 | **type** `record Research(String name, long seed, double landOwned, int ironSites, double ironTonnes, int mines)` | One research city's land as its save carries it: its name, its world's seed, its square feet, iron sites and tonnes, and its Iron Mines. |
| 2161 | 35 | `static void converted() throws Exception` |  |
| 2198 | 18 | `static Deposit nearestUnowned(CityLand land, Resource kind)` | The iron field nearest a city's site, in the nine cells round it, whose centre the city does not own; null when none. |
| 2218 | 19 | `static Deposit nearestOnDryGround(CityLand land, Resource kind)` | The field of a resource nearest a city's site, in the nine cells round it, whose centre plot is dry ground and not the city's (0.7.99): where foundedOn() puts a fixture's city. |
| 2247 | 7 | `static LandManager foundedOn(long x, long y)` | A land office whose city is founded on the default world at plot (x, y) rather than its founding site (0.7.99, batch W1): a centre of whole blocks round it holding a new city's dry ground (CityLand.found()), nothing t... |

### 22. THE TEST PLAYER KEEPS ITS GROUND AHEAD (0.7.58, batch J1d) (lines 2258-2340)

| line | len | member | says |
|---:|---:|---|---|
| 2272 | 68 | `static void groundKeptAhead()` |  |

### 23. THE TEST PLAYER BUYS ITS IRON A WHOLE FIELD AT A TIME (0.7.64, batch L) (lines 2341-2489)

| line | len | member | says |
|---:|---:|---|---|
| 2371 | 91 | `static void ironAWholeFieldAtATime()` |  |
| 2464 | 10 | `static Game ironListed(String label)` | A new city at 1.6 to the dollar grown, by hand, toward the iron field nearest its site (nearestUnowned(); its founding field until 0.7.98) - the offer nearest it each time - until an offer holds iron. |
| 2476 | 13 | `static double[] recount(CityLand land, Resource kind, double out, java.util.function.BiPredicate<Double, Double> in)` | Every field of a resource centred within `out` plots of a city's site (L-infinity) whose centre, seen from the site, passes `in`: its sites and its amount, whole - read off the world's cells, with none of CityLand's s... |

### 25. THE LEDGER WITH ZONES AND THE FOREST LAW (0.7.107, batch Z1) (lines 2490-2535)

| line | len | member | says |
|---:|---:|---|---|
| 2501 | 34 | `static void zonesOnTheLedger()` |  |

### 26. THE GROUND BY TYPE (0.7.110, batch I1; docs/studies/spec-materials.md 2.4) (lines 2536-2767)

| line | len | member | says |
|---:|---:|---|---|
| 2558 | 196 | `static void byType()` |  |
| 2756 | 10 | `static boolean wByType(LandManager land)` | Unowned + the remaining by type + E by type is the world's total, every resource, to the tonne. |

### 27. WHERE THE CUT AND THE CLEARING MEET (0.7.138, batch I9; (lines 2768-2812)

| line | len | member | says |
|---:|---:|---|---|
| 2780 | 31 | `static void cutAndCleared()` |  |


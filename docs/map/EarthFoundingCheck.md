# EarthFoundingCheck.java - 574 lines · 14 methods · 5 constants · harnesses

`ham/citybuildersim/EarthFoundingCheck.java` - generated 2026-10-24 by CodeMap; line numbers are as of that run.

> Founding a city on the real Earth (0.7.121, batch E4; the project's spec-earth.md 2, 6 and 8): the founding card's figures against the plots they are counted on, the place search and Random spot, the sea's depth on Earth, and an Earth city founded, played and drilling at sea.
> 
> WHY. Until E4 only harnesses could found a city on the Earth (E2's door,
> Founding.withWorldKey()), and nothing a player saw was asked of it: the
> card the founding screen shows is the player's whole view of a spot before
> a city is founded on it for good, so every figure on it must be the one
> the city will stand on - the snap's site, the plots the land office sells,
> the geology the fields are drawn by. And the Earth has no heights, so the
> sea's depth, which the land wells' count and every platform ask, threw
> (E2's star 7): an Earth city with oil at sea would have stopped at its
> first purchase off the coast.
> 
> THE DATA. EarthCheck's fixture (the three windows, Rotterdam, Perth and
> Paris, and Fiji's) for everything a player can cause anywhere; the full
> file, when it is named (-Dcbs.earth=<path>, EarthData.locate()), for what
> only the whole planet holds - an ice sheet, a desert, the shelf's width
> measured over the Earth's sea - and says so in one line when it is not.
> The save's four fields are SaveFileCheck's, NewGameCheck's and
> ReadPathCheck's.
> 
> What this has to prove (spec-earth 8, E4):
>   1. THE DATA, OR WHY NOT: a reader made available is available and an
>      identity nothing holds is not; the words for a missing file name where
>      it goes and (0.7.125) how it gets there - EarthSite.GET_EARTH and Play
>      CityBuilderSim.bat in a copy of the repository, the zip in the built
>      game - for a file that is not this build's what replaces it, the
>      load's the same; for another identity both identities, for a save that
>      does not say where that it does not; the game's Earth is here when the
>      full file is named, its identity EARTH_1.
>   2. THE CARD IS THE PLOTS': on each window, the card's site is the world's
>      founding site, the snap's; its disc of CARD_PLOTS counted again plot
>      by plot adds up and agrees, and so do the nearest fresh water and sea;
>      its province and multipliers are World's and Geology's (since 0.8.12,
>      batch EG1, the multiplier at the site's point, Geology.multiplierAt():
>      a recorded iron district shows); the nearest iron is the nearest of
>      every field in the cells round it; printed against the study's
>      figures (spec-earth 2).
>   3. A SPOT IN THE SEA IS REFUSED: the card says NO_SITE_WORDS, the
>      founding's problem() the same, and the game will not found it; on the
>      full file, Greenland's sheet is refused ICE_WORDS and the Sahara warned
>      DESERT_WORDS, a spot with no water near warned.
>   4. SEARCH AND RANDOM SPOT: a place's own name finds it first, accents and
>      case aside; the hits are ranked whole, begun, a word begun, within, each
>      group in the data's order, at most SEARCH_HITS, nothing for nothing;
>      the same seed picks the same place, which passes the site test, and
>      seeds pick more than one.
>   5. THE SEA'S DEPTH: on the plots a line out to sea crosses, nothing where
>      a plot is not the sea's, and where it is World.SHELF_BREAK_M x its
>      distance from the coast - a brute force over every cell within reach -
>      over EarthTerrain.SHELF_WIDTH_M, at least a plot's; on the full file
>      the width measured again is the constant, to its rounding.
>   6. AN EARTH CITY, FOUNDED, PLAYED AND AT SEA: Rotterdam's founded through
>      the game stands on its Earth world, its land the snap's; it buys the
>      ground to an oil field in the sea, the field's sites the sea's, its
>      depth World's and a platform's to stand on; a platform and its wells
>      are built, the city plays two months, the investors plan at sea, and
>      the oil's page reads its depth - nothing throws. Since 0.8.12 (batch
>      EG1) the nearest oil field a platform can stand in is 55 km out (it was
>      10), so the fixture buys toward it until it lies in the city's ground,
>      bounded by its distance in plots, not by a constant (Jerus, 2026-10-20:
> ... (3 more lines in the source)

**Uses:** [EarthSite](EarthSite.md) (73), [World](World.md) (50), [EarthData](EarthData.md) (41), [EarthTerrain](EarthTerrain.md) (16), [Resource](Resource.md) (12), [Founding](Founding.md) (8), [Game](Game.md) (8), [EarthCheck](EarthCheck.md) (5), [Deposit](Deposit.md) (4), [EarthProj](EarthProj.md) (4), [OilView](OilView.md) (4), [GameFiles](GameFiles.md) (3), [Oil](Oil.md) (3), [LandManager](LandManager.md) (3), [CityLand](CityLand.md) (2), [TempDirs](TempDirs.md) (1), [BuildingManager](BuildingManager.md) (1), [LandParcel](LandParcel.md) (1), [MiningCheck](MiningCheck.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1), [LongPlaytest](LongPlaytest.md) (1)

## Sections

| line | section |
|---:|---|
| 135 | 1. THE DATA, OR WHY NOT |
| 186 | 2. THE CARD IS THE PLOTS' |
| 259 | 3. A SPOT IN THE SEA IS REFUSED (and on the full file: ice, a desert) |
| 305 | 4. SEARCH AND RANDOM SPOT |
| 355 | 5. THE SEA'S DEPTH |
| 455 | 6. AN EARTH CITY, FOUNDED, PLAYED AND AT SEA |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 81 | `EarthFoundingCheck.out` | `System.out` | Where the harness prints: the console. |
| 83 | `EarthFoundingCheck.quiet` | `new PrintStream(java.io.OutputStream.nullOutputStream())` | Where the game's own narration goes while a city is founded or played here (quietly()): nowhere. |
| 99 | `EarthFoundingCheck.SEED` | `Founding.DEFAULT_WORLD_SEED` | The seed every world here is drawn with: the default world's. |
| 102 | `EarthFoundingCheck.STUDY` | `Map.of("Rotterdam", new double[] { 88, 9.2, Double.NaN, 0.6, 3.0 }, "Perth", ...` | The study's card for each window (spec-earth 2): dry %, river and lake %, sea %, fresh water km, sea km - NaN where it gave none. |
| 415 | `EarthFoundingCheck.BRUTE_MARGIN_DEG` | `2` | How far past the search's answer the brute force reads, degrees: 2, so a cell the search missed nearer than its answer lies well inside what is read. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 79 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 77 | 498 | **type** `public class EarthFoundingCheck` | Founding a city on the real Earth (0.7.121, batch E4; the project's spec-earth.md 2, 6 and 8): the founding card's figures against the plots they are counted on, the place search and Random spot, the sea's depth on Ea... |
| 85 | 4 | `static void check(String label, boolean ok)` |  |
| 90 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 96 | 1 | **type** `record Spot(String name, double lat, double lon)` | The fixture's windows: name, latitude, longitude. |
| 107 | 27 | `public static void main(String[] args) throws Exception` |  |

### 1. THE DATA, OR WHY NOT (lines 135-185)

| line | len | member | says |
|---:|---:|---|---|
| 138 | 47 | `static EarthData theData(String id) throws Exception` |  |

### 2. THE CARD IS THE PLOTS' (lines 186-258)

| line | len | member | says |
|---:|---:|---|---|
| 189 | 49 | `static void theCard(String id, Map<String, Spot> spots)` |  |
| 239 | 1 | `static String figure(double v)` |  |
| 241 | 1 | `static String km(double v)` |  |
| 244 | 14 | `static double[] bruteIron(World w, long x, long y, double cardKm)` | The nearest iron field's centre to a plot by brute force: every field in every cell within the card's distance and two cells more, {plots away}; null for none. |

### 3. A SPOT IN THE SEA IS REFUSED (and on the full file: ice, a desert) (lines 259-304)

| line | len | member | says |
|---:|---:|---|---|
| 262 | 42 | `static void refused(String id, Map<String, Spot> spots, EarthData file)` |  |

### 4. SEARCH AND RANDOM SPOT (lines 305-354)

| line | len | member | says |
|---:|---:|---|---|
| 308 | 46 | `static void search(EarthData data, String id)` |  |

### 5. THE SEA'S DEPTH (lines 355-454)

| line | len | member | says |
|---:|---:|---|---|
| 358 | 55 | `static void depth(String id, Spot r, EarthData file)` |  |
| 424 | 30 | `static double bruteCoast(EarthData d, double lat, double lon)` | The great-circle distance to the centre of the nearest 15" cell whose sea share is under the coast's level, by brute force: every cell of every 1-degree tile whose centre lies within the search's answer and BRUTE_MARG... |

### 6. AN EARTH CITY, FOUNDED, PLAYED AND AT SEA (lines 455-574)

| line | len | member | says |
|---:|---:|---|---|
| 458 | 110 | `static void aCity(String id, Spot r)` |  |
| 569 | 5 | `static void quietly(Runnable r)` |  |


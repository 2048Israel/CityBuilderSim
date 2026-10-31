# EarthFoundingCheck.java - 693 lines · 18 methods · 4 constants · harnesses

`ham/citybuildersim/EarthFoundingCheck.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

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
> only the whole planet holds - an ice sheet, a desert, the sea off
> Valparaiso and the North Sea's depth 40 and 80 km out - and says so in one
> line when it is not.
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
>      full file is named, its identity EARTH_2.
>   2. THE CARD IS THE PLOTS': on each window, the card's site is the world's
>      founding site, the snap's; its disc of CARD_PLOTS counted again plot
>      by plot adds up and agrees, and so do the nearest fresh water and sea;
>      its province and multipliers are World's and Geology's (since 0.8.12,
>      batch EG1, the multiplier at the site's point, Geology.multiplierAt():
>      a recorded iron district shows); the nearest iron is the nearest of
>      every field in the cells round it; printed against the study's
>      figures (spec-earth 2).
>      Since ET1 (D21) the card's three lines are held the same way, on the windows and
>      on the full file: the dry ground within 5 km by relief, the nearest shallow water and
>      how deep the sea gets within reach, and the landmass, each counted again by another
>      road (reliefChecks()); on the full file Medellin reads hills, Halifax has shallow water
>      near and a sea well over a metre deep, Valparaiso a deep one, Prince Edward Island is a landmass of its own and
>      Rotterdam is flat (reliefCards()).
>   3. A SPOT IN THE SEA IS REFUSED: the card says NO_SITE_WORDS, the
>      founding's problem() the same, and the game will not found it; on the
>      full file, Greenland's sheet is refused ICE_WORDS and the Sahara warned
>      DESERT_WORDS, a spot with no water near warned.
>   4. SEARCH AND RANDOM SPOT: a place's own name finds it first, accents and
>      case aside; the hits are ranked whole, begun, a word begun, within, each
>      group in the data's order, at most SEARCH_HITS, nothing for nothing;
>      the same seed picks the same place, which passes the site test, and
>      seeds pick more than one.
>   5. THE SEA'S DEPTH (rewritten by ET1, D17: the real depth replaces E4's
>      guess from the coast's distance): on the plots a line out to sea
>      crosses, nothing where a plot is not the sea's (SALT or SHALLOW), and
>      where it is the depth layer's code at the plot's centre, decoded, at
>      least a metre; on the full file the study's spots - the North Sea off
>      Rotterdam 40 and 80 km out, where a platform stands, and the sea off
>      Valparaiso 20 and 40 km out, where a floating platform stands at deep
> ... (15 more lines in the source)

**Uses:** [EarthSite](EarthSite.md) (100), [World](World.md) (82), [EarthData](EarthData.md) (47), [EarthTerrain](EarthTerrain.md) (12), [Resource](Resource.md) (12), [Oil](Oil.md) (10), [Founding](Founding.md) (8), [Game](Game.md) (8), [EarthCheck](EarthCheck.md) (5), [Deposit](Deposit.md) (4), [OilView](OilView.md) (4), [GameFiles](GameFiles.md) (3), [LandManager](LandManager.md) (3), [CityLand](CityLand.md) (2), [TempDirs](TempDirs.md) (1), [BuildingManager](BuildingManager.md) (1), [LandParcel](LandParcel.md) (1), [MiningCheck](MiningCheck.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1), [LongPlaytest](LongPlaytest.md) (1)

## Sections

| line | section |
|---:|---|
| 148 | 1. THE DATA, OR WHY NOT |
| 199 | 2. THE CARD IS THE PLOTS' |
| 385 | 3. A SPOT IN THE SEA IS REFUSED (and on the full file: ice, a desert) |
| 431 | 4. SEARCH AND RANDOM SPOT |
| 481 | 5. THE SEA'S DEPTH |
| 574 | 6. AN EARTH CITY, FOUNDED, PLAYED AND AT SEA |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 93 | `EarthFoundingCheck.out` | `System.out` | Where the harness prints: the console. |
| 95 | `EarthFoundingCheck.quiet` | `new PrintStream(java.io.OutputStream.nullOutputStream())` | Where the game's own narration goes while a city is founded or played here (quietly()): nowhere. |
| 111 | `EarthFoundingCheck.SEED` | `Founding.DEFAULT_WORLD_SEED` | The seed every world here is drawn with: the default world's. |
| 114 | `EarthFoundingCheck.STUDY` | `Map.of("Rotterdam", new double[] { 88, 9.2, Double.NaN, 0.6, 3.0 }, "Perth", ...` | The study's card for each window (spec-earth 2): dry %, river and lake %, sea %, fresh water km, sea km - NaN where it gave none. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 91 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 89 | 605 | **type** `public class EarthFoundingCheck` | Founding a city on the real Earth (0.7.121, batch E4; the project's spec-earth.md 2, 6 and 8): the founding card's figures against the plots they are counted on, the place search and Random spot, the sea's depth on Ea... |
| 97 | 4 | `static void check(String label, boolean ok)` |  |
| 102 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 108 | 1 | **type** `record Spot(String name, double lat, double lon)` | The fixture's windows: name, latitude, longitude. |
| 119 | 28 | `public static void main(String[] args) throws Exception` |  |

### 1. THE DATA, OR WHY NOT (lines 148-198)

| line | len | member | says |
|---:|---:|---|---|
| 151 | 47 | `static EarthData theData(String id) throws Exception` |  |

### 2. THE CARD IS THE PLOTS' (lines 199-384)

| line | len | member | says |
|---:|---:|---|---|
| 202 | 50 | `static void theCard(String id, Map<String, Spot> spots)` |  |
| 253 | 1 | `static String figure(double v)` |  |
| 255 | 1 | `static String km(double v)` |  |
| 258 | 14 | `static double[] bruteIron(World w, long x, long y, double cardKm)` | The nearest iron field's centre to a plot by brute force: every field in every cell within the card's distance and two cells more, {plots away}; null for none. |
| 278 | 36 | `static void reliefChecks(String name, World w, EarthSite.Card c)` | THE CARD'S THREE LINES OF ET1 (D21), counted again by another road: the disc's dry plots by World.reliefAt(), the nearest shallow plot, the deepest the sea is under any sea plot of the disc - read two ways, World.dept... |
| 321 | 54 | `static void reliefCards(EarthData file, Map<String, Spot> spots)` | 2b. |
| 377 | 7 | `static EarthSite.Card fullCard(String name, double lat, double lon)` | A card on the full file, its relief and landmass counted again (reliefChecks()). |

### 3. A SPOT IN THE SEA IS REFUSED (and on the full file: ice, a desert) (lines 385-430)

| line | len | member | says |
|---:|---:|---|---|
| 388 | 42 | `static void refused(String id, Map<String, Spot> spots, EarthData file)` |  |

### 4. SEARCH AND RANDOM SPOT (lines 431-480)

| line | len | member | says |
|---:|---:|---|---|
| 434 | 46 | `static void search(EarthData data, String id)` |  |

### 5. THE SEA'S DEPTH (lines 481-573)

| line | len | member | says |
|---:|---:|---|---|
| 484 | 70 | `static void depth(String id, Spot r, EarthData file)` |  |
| 556 | 5 | `static double layerDepth(EarthTerrain e, EarthData data, long x, long y)` | The depth layer's metres at a plot's centre, at least SEA_LEAST_DEPTH_M: what depthAt() must give, read by another road (the raw code at the plot's latitude and longitude). |
| 563 | 10 | `static double seaOut(World w, double km)` | The sea's depth `km` out from the coast west of a world's founding site, along its row: the first sea plot going west, then km further; -1 where the row has no sea within 200 km. |

### 6. AN EARTH CITY, FOUNDED, PLAYED AND AT SEA (lines 574-693)

| line | len | member | says |
|---:|---:|---|---|
| 577 | 110 | `static void aCity(String id, Spot r)` |  |
| 688 | 5 | `static void quietly(Runnable r)` |  |


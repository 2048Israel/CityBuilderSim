# EarthSite.java - 541 lines · 34 methods · 14 constants · model

`ham/citybuildersim/EarthSite.java` - generated 2026-10-14 by CodeMap; line numbers are as of that run.

> A spot on the real Earth as the founding screen weighs it: whether the game has the Earth's data, the place names a player searches, the spot the seed picks, and the founding card - the site the snap finds, the ground within 5 km measured on the world's own plots, the province's deposit multipliers, the nearest iron, and why a spot is refused or warned of.
> 
> WHY THIS EXISTS (0.7.121, batch E4; the project's spec-earth.md 2, 6 and 8;
> Jerus's answers to the study, 2026-10-10, "all rec"). E1 to E5 built the
> real Earth under the game - its data, its ground, its geology and its map -
> and only harnesses could found a city on it. The founding screen offers it
> now, and a player choosing a spot from a globe needs to know what is there
> before the city is founded on it for good: the screen draws, and every
> figure it shows is worked out here, on the world the city would stand on.
> 
> WHAT JERUS DECIDED, and what this holds to:
>   - Earth for new cities, the default where the data is here; the
>     generated world stays as an option (gameData(): the game runs without
>     the file, and says where it goes and, since 0.7.125, how it gets there -
>     dataWords()).
>   - A spot in the sea or on ice is refused; a desert, or no fresh water
>     within 5 km, is warned of (Card.refusal(), Card.warnings()).
>   - No iron field is promised: the card names the nearest (Card.ironKm()).
>   - Place names for searching only (search()), and no borders: a place is
>     its name and its latitude and longitude, never a country.
> 
> THE CARD (measure()) is the city's own world, World.of(key): the site is
> its snap (EarthTerrain.siteNear(), the founding's test 1 within 5 km of
> the spot), the ground within CARD_PLOTS of the site counted plot by plot
> as the land office will sell it, the province the geology under the site
> (E3's multipliers, w x Z, oil's with its petroleum), and the nearest iron
> field's centre among the world's own fields. Nothing here is a model
> change: the city founded is the one the card describes. Since 0.7.127
> (batch E7) ice is a kind of ground, never dry: the card's ice is the
> plot's class (onIce()), so the snap never finds a site on it, and a spot
> on an ice sheet is refused on its own plot.
> 
> RANDOM SPOT (randomPlace()): the seed picks among the data's places that
> pass the site test - a site within the snap and not on ice - trying them in
> an order its hash gives, so the same seed picks the same place.
> 
> EarthFoundingCheck holds it, on the fixture's windows and on the full file.

**Uses:** [World](World.md) (33), [EarthData](EarthData.md) (27), [EarthTerrain](EarthTerrain.md) (11), [Resource](Resource.md) (5), [LandMap](LandMap.md) (3), [Geology](Geology.md) (2), [GameVersion](GameVersion.md) (1), [Deposit](Deposit.md) (1), [EarthProj](EarthProj.md) (1)

**Used by (10):** [DataSave](DataSave.md), [EarthFoundingCheck](EarthFoundingCheck.md), [EarthTerrainCheck](EarthTerrainCheck.md), [Founding](Founding.md), [FoundingScreen](FoundingScreen.md), [Game](Game.md), [LongPlaytest](LongPlaytest.md), [NewGameCheck](NewGameCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 80 | THE GAME'S EARTH: is the file here? |
| 119 | · how the file gets there (0.7.125, batch C2) |
| 235 | SEARCHING THE PLACES (names for finding a spot, no borders) |
| 280 | RANDOM SPOT |
| 351 | THE CARD |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 54 | `EarthSite.CARD_PLOTS` | `EarthTerrain.SNAP_PLOTS` | The card's reach, in plots: EarthTerrain.SNAP_PLOTS, 167 (5 km, spec-earth 2) - the snap's and the ground's. |
| 57 | `EarthSite.SEARCH_HITS` | `8` | Places a search offers at most: 8, as many as the list under the field shows. |
| 60 | `EarthSite.RANDOM_TRIES` | `64` | Places Random spot tries, in its seed's order, before it gives up: 64 (the data's 7,342 are towns and cities, nearly all on dry ground). |
| 63 | `EarthSite.IRON_RINGS` | `16` | How far the nearest iron is looked for, in world cells round the site's: 16 (983 km); past it the card says there is none that near. |
| 66 | `EarthSite.EARTH_FORMAT` | `38` | The save format a city on the real Earth is first written in: 38 (GameVersion's A CITY ON THE REAL EARTH) - a build before it would put the city on its seed's generated ground, so it refuses the save as a newer build'... |
| 75 | `EarthSite.ICE_FORMAT` | `39` | The save format the real Earth is first drawn in with ice and desert: 39 (0.7.127, batch E7; GameVersion's ICE AND DESERT ON THE REAL EARTH). |
| 78 | `EarthSite.RANDOM_SALT` | `0x5B07A11DL` | The stream Random spot draws its order from. |
| 129 | `EarthSite.GET_EARTH` | `"tools/earth/get-earth.bat"` | The script that fetches the file, from the folder the game runs in (the folder DATA_FILE is looked for from): there in a copy of the repository, not in the built game, which carries the file in its zip. |
| 225 | `EarthSite.BEFORE_ICE_WORDS` | `"This city was founded on the real Earth before ice was a kind of ground(0.7....` | The load's words for a city on the real Earth saved before ice was a kind of ground (ICE_FORMAT). |
| 395 | `EarthSite.NO_SITE_WORDS` | `"No dry ground within 5 km to found a city on: the spot is in the sea, or on ...` | The card's words for a spot with no site. |
| 398 | `EarthSite.ICE_WORDS` | `"On ice: snow and ice cover half the ground here or more, and no city is foun...` | ...on ice. |
| 401 | `EarthSite.DESERT_WORDS` | `"A desert: bare ground covers half the land here or more."` | The warning for a desert. |
| 404 | `EarthSite.NO_FRESH_WORDS` | `"No river or lake within 5 km: a water plant needs one, so the city's first w...` | The warning where there is no fresh water within the card's reach and the sea is near. |
| 407 | `EarthSite.NO_WATER_WORDS` | `"No river, lake or sea within 5 km: the city has nowhere near to draw its wat...` | ...and where there is neither. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 90 | `private static Data looked` |  |
| 91 | `private static String lookedAt` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 49 | 493 | **type** `public final class EarthSite` | A spot on the real Earth as the founding screen weighs it: whether the game has the Earth's data, the place names a player searches, the spot the seed picks, and the founding card - the site the snap finds, the ground... |
| 51 | 1 | `private EarthSite()` |  |

### THE GAME'S EARTH: is the file here? (lines 80-118)

| line | len | member | says |
|---:|---:|---|---|
| 85 | 4 | **type** `public record Data(EarthData reader, String words)` | What the game found when it looked for the Earth's data: the reader, or null and why not, in the player's words. |
| 87 | 1 | `public boolean here()` _(in EarthSite.Data)_ | Whether Earth can be offered. |
| 101 | 17 | `public static synchronized Data gameData()` | The game's Earth (EarthData.EARTH_1): the file EarthData.locate() finds (-Dcbs.earth, or EarthData.DATA_FILE in the folder the game runs from) when its identity is the one this build reads, or a reader a harness made ... |

### how the file gets there (0.7.125, batch C2) (lines 119-234)

| line | len | member | says |
|---:|---:|---|---|
| 132 | 3 | `static boolean fromRepository()` | Whether the game runs from a copy of the repository - GET_EARTH is there - rather than as the built game: which of the two ways to the file the words say. |
| 137 | 3 | `static String dataWords(Path found)` | Why the game's Earth is not here, in the player's words: where the file goes and how it gets there, or that the one there is not this build's and what replaces it. |
| 142 | 8 | `static String dataWords(Path found, boolean repository)` | ...for a copy of the repository or the built game, said: the founding screen's words. |
| 152 | 3 | `private static boolean missing(Path found)` | Whether no file is at the path found. |
| 157 | 5 | `private static String fetchWords(boolean repository, String then)` | How a missing file gets there, then what to do: GET_EARTH and Play CityBuilderSim.bat, or the built game's zip. |
| 164 | 5 | `private static String replaceWords(boolean repository, String then)` | What replaces a file that is not this build's, then what to do: GET_EARTH, which fetches it again, or the built game's zip. |
| 171 | 7 | `public static String shortId(String identity)` | An identity as a person reads it: "earth-1, b754a28f". |
| 180 | 9 | `public static boolean available(String identity)` | Whether the data of an identity can be read here: a reader EarthTerrain.use() made available, or the file found when it is that one. |
| 199 | 3 | `public static String loadProblem(String identity, Double lat, Double lon)` | Why a city saved on the real Earth cannot be loaded here, or null when it can (spec-earth 6: the data is frozen per version, and a save names its own): its data is not this build's, or this build's is not here - and t... |
| 204 | 9 | `public static String loadProblem(String identity, Double lat, Double lon, int format)` | ...and of a save in a given format: one before ICE_FORMAT is refused first (0.7.127), whatever its data (DataSave.earthProblem() passes the save's). |
| 215 | 8 | `static String missingWords(Path found, boolean repository)` | The load's words for a city on this build's Earth whose file is not here or not this build's (0.7.125): how it gets there, then load the city again. |
| 229 | 5 | `private static String otherDataWords(String identity)` | The load's words for a city on other data than this build's. |

### SEARCHING THE PLACES (names for finding a spot, no borders) (lines 235-279)

| line | len | member | says |
|---:|---:|---|---|
| 240 | 5 | `static String fold(String s)` | Words folded for matching: lower case, accents off, runs of anything but letters and digits one space. |
| 253 | 13 | `public static List<EarthData.Place> search(EarthData d, String typed, int limit)` | The places a typed name finds, at most `limit`: the name or its plain spelling whole first, then those it begins, then those with a word it begins, then those it is anywhere in - each group most populous first (the da... |
| 268 | 3 | `static int rankOf(EarthData.Place p, String q)` | How well a place matches a folded query: 0 whole, 1 begun, 2 a word begun, 3 within, 4 not at all - the better of its name and plain spelling. |
| 272 | 7 | `private static int rankIn(String n, String q)` |  |

### RANDOM SPOT (lines 280-350)

| line | len | member | says |
|---:|---:|---|---|
| 290 | 10 | `public static EarthData.Place randomPlace(EarthData d, String identity, long seed)` | The place the seed picks (spec-earth 2's Random spot): the data's places tried in the order the seed's hash gives them, RANDOM_TRIES at most, the first that passes the site test (passes()) - so the same seed and data ... |
| 302 | 5 | `public static boolean passes(World.Key key)` | The site test (spec-earth 2): the snap finds dry ground within CARD_PLOTS of the spot, and it is not on ice. |
| 315 | 11 | `public static String foundingProblem(World.Key key)` | Why a city cannot be founded on the world an Earth key names, or null when it can (Founding.problem()): its data not here, no site within the snap (NO_SITE_WORDS), or ice under it (ICE_WORDS: since 0.7.127 a spot with... |
| 336 | 3 | `static boolean onIce(EarthTerrain e, long x, long y)` | Whether a plot is ice: its class as drawn, World.ICE (0.7.127, batch E7; spec-globe 3.9) - Greenland's and Antarctica's sheets and a glacier's heart, the founding card reading the map's own rule, so the card and the p... |
| 341 | 3 | `static boolean inDesert(EarthTerrain e, long x, long y)` | ...and a desert: bare ground on half its land or more (EarthData.LC_DESERT). |
| 345 | 5 | `private static int cover(EarthTerrain e, long x, long y)` |  |

### THE CARD (lines 351-541)

| line | len | member | says |
|---:|---:|---|---|
| 369 | 24 | **type** `public record Card(World.Key key, boolean hasSite, long siteX, long siteY, double siteLat, double siteLon, ...` | What the founding card says of a spot (measure()). |
| 376 | 1 | `public boolean foundable()` _(in EarthSite.Card)_ | Whether a city can be founded on it. |
| 379 | 1 | `public double dryShare()` _(in EarthSite.Card)_ | The dry ground's share of the disc. |
| 382 | 1 | `public double freshShare()` _(in EarthSite.Card)_ | Rivers' and lakes' share of the disc. |
| 385 | 1 | `public double seaShare()` _(in EarthSite.Card)_ | The sea's share of the disc. |
| 388 | 1 | `public double iceShare()` _(in EarthSite.Card)_ | Ice's share of the disc (0.7.127). |
| 391 | 1 | `public double multiplier(Resource r)` _(in EarthSite.Card)_ | A resource's deposit multiplier under the site (Geology.multiplier()), NaN for one not in fields. |
| 421 | 83 | `public static Card measure(World.Key key)` | The founding card for the world a key names (spec-earth 2): the snap's site, the ground within CARD_PLOTS of it plot by plot - dry, rivers and lakes, sea and (0.7.127) ice - and the nearest fresh water and sea to its ... |
| 511 | 24 | `static double[] nearestIron(World w, long x, long y)` | The nearest iron field's centre to a plot, {plots away, east, south}, or null when none lies within IRON_RINGS cells: the world's fields cell by cell, ring by ring round the plot's cell, until no ring left can hold a ... |
| 537 | 4 | `public static String latLonWords(double lat, double lon)` | A latitude and longitude as the card and the search write them: "51.92° N, 4.48° E". |


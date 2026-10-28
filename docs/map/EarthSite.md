# EarthSite.java - 723 lines · 44 methods · 21 constants · model

`ham/citybuildersim/EarthSite.java` - generated 2026-10-28 by CodeMap; line numbers are as of that run.

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
>   - A spot in the sea or on ice is refused - and, since ET2, one among
>     slopes no building may stand on, none within 5 km; a desert, or no fresh
>     water within 5 km, is warned of (Card.refusal(), Card.warnings()).
>   - No iron field is promised: the card names the nearest (Card.ironKm()).
>   - Place names for searching only (search()), and no borders: a place is
>     its name and its latitude and longitude, never a country.
> 
> THE CARD (measure()) is the city's own world, World.of(key): the site is
> its snap (EarthTerrain.siteNear(), the founding's test 1 within 5 km of
> the spot), the ground within CARD_PLOTS of the site counted plot by plot
> as the land office will sell it, the province the geology under the site
> (E3's multipliers, w x Z, since EG1 oil's by its known petroleum and iron's
> lifted in a recorded district), and the nearest iron
> field's centre among the world's own fields. Nothing here is a model
> change: the city founded is the one the card describes. Since 0.7.127
> (batch E7) ice is a kind of ground, never dry: the card's ice is the
> plot's class (onIce()), so the snap never finds a site on it, and a spot
> on an ice sheet is refused on its own plot.
> 
> THE CARD'S THREE LINES OF ET1 (the project's spec-earth2-terrain.md 3.4;
> Jerus 2026-10-12, D21): the dry ground within 5 km by relief (the same
> plots the walk counts, read beside the cover: flat, foothill, steep, very
> steep kept apart in the Card's numbers and folded in its words), the
> nearest shallow water with how deep the sea gets within 5 km, and the
> landmass the site stands on, by its place's name and its area. The snap and
> the refusal on ground no building may stand on are ET2's (slice C, the
> founding on buildable ground): the snap's test 1 reads World.buildable() -
> dry, and flat or foothill - so a spot among steep slopes finds its site on
> the nearest buildable ground within 5 km, or none; with none the card is
> refused in noSiteWords(), which names the nearest buildable ground within
> 50 km (EarthTerrain.siteBeyond()), or that there is none. A save from before
> is refused (SLOPE_FORMAT, BEFORE_SLOPE_WORDS): the world's founding site
> is found again from the spot and the data each time, and would not be the
> one the city was built round.
> 
> RANDOM SPOT (randomPlace()): the seed picks among the data's places that
> pass the site test - a site within the snap and not on ice - trying them in
> an order its hash gives, so the same seed picks the same place.
> 
> EarthFoundingCheck holds it, on the fixture's windows and on the full file.

**Uses:** [World](World.md) (47), [EarthData](EarthData.md) (32), [EarthTerrain](EarthTerrain.md) (17), [Resource](Resource.md) (5), [LandMap](LandMap.md) (4), [Geology](Geology.md) (2), [GameVersion](GameVersion.md) (1), [Deposit](Deposit.md) (1), [EarthProj](EarthProj.md) (1)

**Used by (18):** [BridgeCheck](BridgeCheck.md), [DataSave](DataSave.md), [EarthDepositsCheck](EarthDepositsCheck.md), [EarthFoundingCheck](EarthFoundingCheck.md), [EarthTerrainCheck](EarthTerrainCheck.md), [Founding](Founding.md), [FoundingScreen](FoundingScreen.md), [Game](Game.md), [HydroCheck](HydroCheck.md), [LandCheck](LandCheck.md), [LandmassCheck](LandmassCheck.md), [LongPlaytest](LongPlaytest.md), [NewGameCheck](NewGameCheck.md), [ReadPathCheck](ReadPathCheck.md), [ReloadCheck](ReloadCheck.md), [SaveFileCheck](SaveFileCheck.md), [SlopeCheck](SlopeCheck.md), [SlopeRenders](SlopeRenders.md)

## Sections

| line | section |
|---:|---|
| 129 | THE GAME'S EARTH: is the file here? |
| 168 | · how the file gets there (0.7.125, batch C2) |
| 305 | SEARCHING THE PLACES (names for finding a spot, no borders) |
| 350 | RANDOM SPOT |
| 439 | THE CARD |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 72 | `EarthSite.CARD_PLOTS` | `EarthTerrain.SNAP_PLOTS` | The card's reach, in plots: EarthTerrain.SNAP_PLOTS, 167 (5 km, spec-earth 2) - the snap's and the ground's. |
| 75 | `EarthSite.SEARCH_HITS` | `8` | Places a search offers at most: 8, as many as the list under the field shows. |
| 78 | `EarthSite.RANDOM_TRIES` | `64` | Places Random spot tries, in its seed's order, before it gives up: 64 (the data's 7,342 are towns and cities, nearly all on dry ground). |
| 81 | `EarthSite.IRON_RINGS` | `16` | How far the nearest iron is looked for, in world cells round the site's: 16 (983 km); past it the card says there is none that near. |
| 84 | `EarthSite.EARTH_FORMAT` | `38` | The save format a city on the real Earth is first written in: 38 (GameVersion's A CITY ON THE REAL EARTH) - a build before it would put the city on its seed's generated ground, so it refuses the save as a newer build'... |
| 93 | `EarthSite.ICE_FORMAT` | `39` | The save format the real Earth is first drawn in with ice and desert: 39 (0.7.127, batch E7; GameVersion's ICE AND DESERT ON THE REAL EARTH). |
| 103 | `EarthSite.DEPOSITS_FORMAT` | `41` | The save format the real Earth's fields are first drawn on its geology in: 41 (0.8.12, batch EG1; GameVersion's DEPOSITS WHERE THE GEOLOGY SAYS). |
| 116 | `EarthSite.SLOPE_FORMAT` | `46` | The save format the real Earth's founding site is first snapped to buildable ground in: 46 (ET2, spec-earth2-terrain 3.4; GameVersion's BUILDABLE GROUND, FOUNDED ON). |
| 124 | `EarthSite.LANDMASS_FORMAT` | `47` | The save format the city's ground is first counted by landmass in: 47 (ET5; Landmasses, GameVersion's LANDMASSES). |
| 127 | `EarthSite.RANDOM_SALT` | `0x5B07A11DL` | The stream Random spot draws its order from. |
| 178 | `EarthSite.GET_EARTH` | `"tools/earth/get-earth.bat"` | The script that fetches the file, from the folder the game runs in (the folder DATA_FILE is looked for from): there in a copy of the repository, not in the built game, which carries the file in its zip. |
| 280 | `EarthSite.BEFORE_ICE_WORDS` | `"This city was founded on the real Earth before ice was a kind of ground(0.7....` | The load's words for a city on the real Earth saved before ice was a kind of ground (ICE_FORMAT). |
| 284 | `EarthSite.BEFORE_DEPOSITS_WORDS` | `"This city was founded on the real Earth before its oil, iron and other field...` | The load's words for a city on the real Earth saved before its fields were drawn on the geology (DEPOSITS_FORMAT). |
| 289 | `EarthSite.BEFORE_SLOPE_WORDS` | `"This city was founded on the real Earth before slope decided where a buildin...` | The load's words for a city on the real Earth saved before its founding site was snapped to ground a building may stand on (SLOPE_FORMAT). |
| 294 | `EarthSite.BEFORE_LANDMASS_WORDS` | `"This city was founded on the real Earth before its ground was counted by lan...` | The load's words for a city on the real Earth saved before its ground was counted by landmass (LANDMASS_FORMAT). |
| 561 | `EarthSite.NO_SITE_WORDS` | `"No dry ground within 5 km to found a city on: the spot is in the sea, or on ...` | The card's words for a spot with no site: in the sea, or on an island too small (a spot among steep slopes has SLOPE_WORDS). |
| 564 | `EarthSite.SLOPE_WORDS` | `"No ground a building may stand on within 5 km: the slopes here are steep."` | The card's words for a spot among steep slopes (ET2): the first sentence of noSiteWords(), the nearest buildable ground after it. |
| 567 | `EarthSite.ICE_WORDS` | `"On ice: snow and ice cover half the ground here or more, and no city is foun...` | ...on ice. |
| 570 | `EarthSite.DESERT_WORDS` | `"A desert: bare ground covers half the land here or more."` | The warning for a desert. |
| 573 | `EarthSite.NO_FRESH_WORDS` | `"No river or lake within 5 km: a water plant needs one, so the city's first w...` | The warning where there is no fresh water within the card's reach and the sea is near. |
| 576 | `EarthSite.NO_WATER_WORDS` | `"No river, lake or sea within 5 km: the city has nowhere near to draw its wat...` | ...and where there is neither. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 139 | `private static Data looked` |  |
| 140 | `private static String lookedAt` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 67 | 657 | **type** `public final class EarthSite` | A spot on the real Earth as the founding screen weighs it: whether the game has the Earth's data, the place names a player searches, the spot the seed picks, and the founding card - the site the snap finds, the ground... |
| 69 | 1 | `private EarthSite()` |  |

### THE GAME'S EARTH: is the file here? (lines 129-167)

| line | len | member | says |
|---:|---:|---|---|
| 134 | 4 | **type** `public record Data(EarthData reader, String words)` | What the game found when it looked for the Earth's data: the reader, or null and why not, in the player's words. |
| 136 | 1 | `public boolean here()` _(in EarthSite.Data)_ | Whether Earth can be offered. |
| 150 | 17 | `public static synchronized Data gameData()` | The game's Earth (EarthData.EARTH_2): the file EarthData.locate() finds (-Dcbs.earth, or EarthData.DATA_FILE in the folder the game runs from) when its identity is the one this build reads, or a reader a harness made ... |

### how the file gets there (0.7.125, batch C2) (lines 168-304)

| line | len | member | says |
|---:|---:|---|---|
| 181 | 3 | `static boolean fromRepository()` | Whether the game runs from a copy of the repository - GET_EARTH is there - rather than as the built game: which of the two ways to the file the words say. |
| 186 | 3 | `static String dataWords(Path found)` | Why the game's Earth is not here, in the player's words: where the file goes and how it gets there, or that the one there is not this build's and what replaces it. |
| 191 | 8 | `static String dataWords(Path found, boolean repository)` | ...for a copy of the repository or the built game, said: the founding screen's words. |
| 201 | 3 | `private static boolean missing(Path found)` | Whether no file is at the path found. |
| 206 | 5 | `private static String fetchWords(boolean repository, String then)` | How a missing file gets there, then what to do: GET_EARTH and Play CityBuilderSim.bat, or the built game's zip. |
| 213 | 5 | `private static String replaceWords(boolean repository, String then)` | What replaces a file that is not this build's, then what to do: GET_EARTH, which fetches it again, or the built game's zip. |
| 220 | 7 | `public static String shortId(String identity)` | An identity as a person reads it: "earth-2, c897b242". |
| 229 | 9 | `public static boolean available(String identity)` | Whether the data of an identity can be read here: a reader EarthTerrain.use() made available, or the file found when it is that one. |
| 251 | 3 | `public static String loadProblem(String identity, Double lat, Double lon)` | Why a city saved on the real Earth cannot be loaded here, or null when it can (spec-earth 6: the data is frozen per version, and a save names its own): its data is not this build's, or this build's is not here - and t... |
| 256 | 12 | `public static String loadProblem(String identity, Double lat, Double lon, int format)` | ...and of a save in a given format: one before ICE_FORMAT is refused first (0.7.127), then one before DEPOSITS_FORMAT (0.8.12), then one before SLOPE_FORMAT (ET2), then one before LANDMASS_FORMAT (ET5), whatever its d... |
| 270 | 8 | `static String missingWords(Path found, boolean repository)` | The load's words for a city on this build's Earth whose file is not here or not this build's (0.7.125): how it gets there, then load the city again. |
| 299 | 5 | `private static String otherDataWords(String identity)` | The load's words for a city on other data than this build's. |

### SEARCHING THE PLACES (names for finding a spot, no borders) (lines 305-349)

| line | len | member | says |
|---:|---:|---|---|
| 310 | 5 | `static String fold(String s)` | Words folded for matching: lower case, accents off, runs of anything but letters and digits one space. |
| 323 | 13 | `public static List<EarthData.Place> search(EarthData d, String typed, int limit)` | The places a typed name finds, at most `limit`: the name or its plain spelling whole first, then those it begins, then those with a word it begins, then those it is anywhere in - each group most populous first (the da... |
| 338 | 3 | `static int rankOf(EarthData.Place p, String q)` | How well a place matches a folded query: 0 whole, 1 begun, 2 a word begun, 3 within, 4 not at all - the better of its name and plain spelling. |
| 342 | 7 | `private static int rankIn(String n, String q)` |  |

### RANDOM SPOT (lines 350-438)

| line | len | member | says |
|---:|---:|---|---|
| 360 | 10 | `public static EarthData.Place randomPlace(EarthData d, String identity, long seed)` | The place the seed picks (spec-earth 2's Random spot): the data's places tried in the order the seed's hash gives them, RANDOM_TRIES at most, the first that passes the site test (passes()) - so the same seed and data ... |
| 372 | 5 | `public static boolean passes(World.Key key)` | The site test (spec-earth 2): the snap finds buildable ground (ET2: dry, flat or foothill) within CARD_PLOTS of the spot, and it is not on ice. |
| 386 | 11 | `public static String foundingProblem(World.Key key)` | Why a city cannot be founded on the world an Earth key names, or null when it can (Founding.problem()): its data not here, no site within the snap (noSiteWords(): NO_SITE_WORDS, or since ET2 SLOPE_WORDS and where the ... |
| 405 | 9 | `static String noSiteWords(EarthTerrain e)` | The words for a spot whose snap found no site and whose own plot is not ice (ET2, spec-earth2-terrain 3.4): where the dry ground within the snap is steep or very steep on half of it or more, SLOPE_WORDS and where the ... |
| 424 | 3 | `static boolean onIce(EarthTerrain e, long x, long y)` | Whether a plot is ice: its class as drawn, World.ICE (0.7.127, batch E7; spec-globe 3.9) - Greenland's and Antarctica's sheets and a glacier's heart, the founding card reading the map's own rule, so the card and the p... |
| 429 | 3 | `static boolean inDesert(EarthTerrain e, long x, long y)` | ...and a desert: bare ground on half its land or more (EarthData.LC_DESERT). |
| 433 | 5 | `private static int cover(EarthTerrain e, long x, long y)` |  |

### THE CARD (lines 439-723)

| line | len | member | says |
|---:|---:|---|---|
| 461 | 31 | **type** `public record Card(World.Key key, boolean hasSite, long siteX, long siteY, double siteLat, double siteLon, ...` | What the founding card says of a spot (measure()). |
| 469 | 1 | `public boolean foundable()` _(in EarthSite.Card)_ | Whether a city can be founded on it. |
| 472 | 1 | `public double dryShare()` _(in EarthSite.Card)_ | The dry ground's share of the disc. |
| 475 | 1 | `public double freshShare()` _(in EarthSite.Card)_ | Rivers' and lakes' share of the disc. |
| 478 | 1 | `public double seaShare()` _(in EarthSite.Card)_ | The sea's share of the disc. |
| 481 | 1 | `public double iceShare()` _(in EarthSite.Card)_ | Ice's share of the disc (0.7.127). |
| 484 | 1 | `public double multiplier(Resource r)` _(in EarthSite.Card)_ | A resource's deposit multiplier under the site (Geology.multiplierAt(), at the site's point: an iron district's shows, EG1), NaN for one not in fields. |
| 487 | 1 | `public double reliefShare(int relief)` _(in EarthSite.Card)_ | One relief's share of the disc's dry ground (ET1): World.FLAT, FOOTHILL, STEEP or VERY_STEEP; 0 where there is no dry ground. |
| 490 | 1 | `public double steepShare()` _(in EarthSite.Card)_ | The share of the dry ground that is steep or very steep (ET1): the card's words fold the two. |
| 498 | 15 | `public static String reliefWords(Card c)` | THE CARD'S THREE LINES OF ET1 (spec-earth2-terrain 3.4; Jerus 2026-10-12, D21: relief, water and landmass). |
| 515 | 6 | `public static String shallowWords(Card c)` | ...the nearest shallow water and how deep the sea gets in reach: "Shallow water 0.4 km away · the sea is up to 34 m deep within 5 km", or none within the reach (the mockup's "deepest 1' cell", the depth layer's). |
| 523 | 4 | `private static double plotDepth(EarthTerrain e, EarthData d, long x, long y, double[] ll)` | The depth layer's metres under a sea plot (World.depthAt()'s rule, read here without the world's plot cache): a metre at least, and the layer's deepest past the planet's rim. |
| 532 | 7 | `public static String landmassWords(Card c)` | ...and the landmass: its place's name (the data's most populous place on it) and its area ("Its landmass: New York's, 36.8 million km² (id 2)"), or null where the data has none to say. |
| 541 | 1 | `private static String reachWords()` | The card's reach as it writes it: "5 km". |
| 544 | 3 | `private static String distanceWords(double km)` | A distance as the card writes it: a tenth of a km under ten, whole km from there (the founding screen's). |
| 556 | 3 | `static EarthData.Landmass siteLandmass(World w, long sx, long sy)` | The landmass a site stands on (ET1; ET5): the one World.landmassAt() reads at the snapped site's plot - the data's cell at the plot's blend point, or for a plot the game draws dry on a cell the layer calls no land the... |
| 590 | 96 | `public static Card measure(World.Key key)` | The founding card for the world a key names (spec-earth 2): the snap's site, the ground within CARD_PLOTS of it plot by plot - dry, rivers and lakes, sea and (0.7.127) ice - and the nearest fresh water and sea to its ... |
| 693 | 24 | `static double[] nearestIron(World w, long x, long y)` | The nearest iron field's centre to a plot, {plots away, east, south}, or null when none lies within IRON_RINGS cells: the world's fields cell by cell, ring by ring round the plot's cell, until no ring left can hold a ... |
| 719 | 4 | `public static String latLonWords(double lat, double lon)` | A latitude and longitude as the card and the search write them: "51.92° N, 4.48° E". |


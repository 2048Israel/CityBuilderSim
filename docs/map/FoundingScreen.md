# FoundingScreen.java - 1,330 lines · 59 methods · 19 constants · interface

`ham/citybuildersim/ui/FoundingScreen.java` - generated 2026-10-11 by CodeMap; line numbers are as of that run.

> Found a city: its name, its money, what the founders leave in the treasury and the vault, the ground it stands on and the world it is founded into.
> 
> WHY THIS EXISTS (0.7.10). "Start New Game" founded the same city every time
> - Danzik, the Danzik dollar, D$2.5B and US$1B - and the one founding choice
> there was, the world's inflation, sat on Settings, where it did nothing
> until the next city. Jerus: "a small thing at the start of the game where
> you choose the name of the city and the currency ... and perhaps the
> settings to choose the starting cash and usd cash and offworld inflation
> rate ... (with option to just start at default)". So Start New Game - New
> city since 0.7.21 - comes here, and game.newGame() runs only when the
> player founds the city.
> 
> EVERY FIGURE IS THE MODEL'S. The currency's name, code and symbols are
> Currency.fromCityName() and Currency.typed(); the presets and the bounds on
> a custom founding are Founding's; the world's settled level is
> WorldEconomy.settledLevelAt(); and whether the city can be founded, and if
> not why, is Founding.problem(). The screen holds the player's half-made
> choices between redraws and nothing else.
> 
> AND IT NO LONGER SAYS WHAT THE MONEY BUYS (0.7.20). It listed "...the
> founding village" and its houses, "...and what is left", and the first
> works "in cash" or "a bond for" (Game.whatItBuys()) - and the village is
> the playtest's, placed by hand before its advisor takes over, which the
> game never builds for a player: Jerus's play-through found a treasury of
> $100.0M and one house. Jerus: "even the screen shouldnt say what the money
> could buy, the start screen should be real simple". Each choice shows its
> treasury and its vault, and nothing about spending them.
> Founding.whatItBuys() stays in the model: the harnesses use it
> (NewGameCheck, FundCheck, ReadPathCheck, LongPlaytest). So does
> Game.dayZeroQuotes(), which only this screen called and nothing calls now.
> 
> REAL SIMPLE (0.7.21). Jerus: "the start screen should be real simple". The
> page is the mockups' (Found.dc.html): one panel in the middle of the window
> over the main menu's backdrop, dimmed - the name, one line for its money,
> four cards for what it starts with, a row of five for the world, and Found
> and Back. Each card says how hard the start is, never what the money
> buys; the currency's fields and Custom's open in place from a link; the
> long explanations sit behind an (i) or a tooltip. "Found with defaults"
> went, because the defaults are what the page opens on. It fits a window
> 768 pixels high and scrolls in a smaller one.
> 
> TYPING DOES NOT REDRAW THE PAGE. The page is rebuilt when a card, a link or
> a choice of world changes it; a keystroke only refreshes the lines that
> depend on it - the currency, and whether Found is lit and why not - so the
> field being typed in keeps its focus and its caret.
> 
> AND THE GROUND IT STANDS ON (0.7.56, batch J1a). A row for the World: the
> seed of the city's coast, lakes, river and ore (World, the project's
> spec-land.md), rolled when the page opens and again by the dice, or typed.
> Any whole number is a world and the same number is the same world; it
> cannot be changed later. The city's land stands on it since 0.7.57
> (CityLand).
> 
> ...OR THE REAL EARTH (0.7.121, batch E4; the project's spec-earth.md 2;
> Jerus's answers of 2026-10-10, "all rec": Earth for new cities, the default
> once it ships, the generated world kept as the other choice). THE WORLD is
> two joined chips, Earth | Generated, Earth chosen when the Earth's data is
> here (EarthSite.gameData()); where it is not, Earth is greyed and a line
> says in plain words where the file goes. On Earth the panel widens to two
> columns, the city's choices on the left and the world on the right, in the
> ... (17 more lines in the source)

**Uses:** [Palette](Palette.md) (122), [Founding](Founding.md) (46), [EarthSite](EarthSite.md) (25), [World](World.md) (24), [EarthData](EarthData.md) (22), [EarthProj](EarthProj.md) (16), [Currency](Currency.md) (12), [WorldEconomy](WorldEconomy.md) (5), [TileRaster](TileRaster.md) (3), [UserInterface](UserInterface.md) (2), [Resource](Resource.md) (2), [MapFrame](MapFrame.md) (2), [Icons](Icons.md) (1), [Geology](Geology.md) (1), [MapTiles](MapTiles.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 117 | · the choices, between redraws |
| 143 | · what a keystroke refreshes |
| 225 | THE PAGE |
| 236 | · · the name |
| 284 | · · what it starts with |
| 320 | · · the ground |
| 323 | · · the world |
| 340 | · · found it |
| 421 | THE WORLD: EARTH OR GENERATED (0.7.121) |
| 621 | · the card |
| 825 | WHAT A KEYSTROKE CHANGES |
| 844 | THE CHOICES, AS THE MODEL READS THEM |
| 946 | THE GLOBE (0.7.121, batch E4; spec-earth 2: "the globe from the 4' |
| 1197 | · the globe's arithmetic |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 110 | `FoundingScreen.SCREEN` | `"showFoundingScreen"` | This screen's name for clearMenu(), the key filter and isGameMenu(). |
| 154 | `FoundingScreen.CARDS` | `Executors.newSingleThreadExecutor(r -> { Thread t = new Thread(r, "founding-c...` | The worker the card is measured on, one at a time. |
| 202 | `FoundingScreen.PANEL` | `760` | The panel's width, as the mockups draw it. |
| 205 | `FoundingScreen.PANEL_PAD` | `40` | The panel's padding, left and right: what the cards and fields share is the rest. |
| 208 | `FoundingScreen.SEED_FIELD` | `200` | The World's field: room for a seed of nineteen digits and a sign in the figures' face at 14 px (0.7.56). |
| 211 | `FoundingScreen.EARTH_PANEL` | `1200` | The panel on Earth (0.7.121): two columns, the city's and the world's, 40 apart - 1,200 px, inside a 1,280 window with the page's margins. |
| 214 | `FoundingScreen.COLUMN_GAP` | `40` | Between the two columns on Earth. |
| 217 | `FoundingScreen.GLOBE_W` | `560` | The globe's width, the right column's: 560 px. |
| 220 | `FoundingScreen.GLOBE_H` | `270` | The globe's height: 270 px, so the page still fits an 868-pixel window with the longest card under it (the words probe's, 0.7.121). |
| 223 | `FoundingScreen.LEFT` | `EARTH_PANEL - 2 * PANEL_PAD - COLUMN_GAP - GLOBE_W` | The left column's width on Earth: what the panel leaves. |
| 659 | `FoundingScreen.NO_SPOT_WORDS` | `"None yet.Drag the globe to turn it, zoom with the wheel or - and +, and clic...` | The card's line before a spot is picked; how the globe is used. |
| 970 | `FoundingScreen.Globe.MOST_KM_PX` | `2 * EarthProj.RADIUS_M / 1000 /(0.9 * GLOBE_H)` | The widest view: the whole planet, 12,742 km, across nine tenths of the globe's height. |
| 973 | `FoundingScreen.Globe.GROUND_KM_PX` | `4` | From here closer the ground is the city's own far view, not the globe's picture: 4 km a pixel. |
| 976 | `FoundingScreen.Globe.LEAST_KM_PX` | `5 * World.PLOT_M / 1000.0` | The closest view: 150 m a pixel, five plots (spec-earth 2's 150 m view). |
| 979 | `FoundingScreen.Globe.PICK_KM_PX` | `0.3` | How close a pick from the search or Random spot opens: 0.3 km a pixel, the 5 km round the site 33 pixels across. |
| 982 | `FoundingScreen.Globe.STEP` | `MapFrame.ZOOM_STEP` | A wheel notch's zoom, the map's: MapFrame.ZOOM_STEP. |
| 985 | `FoundingScreen.Globe.TEXEL_ARCMIN` | `12` | The globe picture's cell, in minutes of arc: 12, three of the 4' layers' cells a side (1,800 x 900 for the planet). |
| 988 | `FoundingScreen.Globe.ICE` | `0xffdfe7ee` | Ice on the globe: what the map has no class for (spec-earth's E6), light so a refused sheet reads as one. |
| 991 | `FoundingScreen.Globe.SPACE` | `0xff0b1118` | Space round the globe: the page's stage. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 113 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 119 | `private String cityName` |  |
| 120 | `private boolean ownCurrency` |  |
| 121 | `private String typedName` |  |
| 122 | `private Founding.Preset preset` |  |
| 124 | `private String customCash` | A custom founding, as typed: millions of the city's money, and millions of US dollars. |
| 125 | `private double mean` |  |
| 127 | `private String worldSeed` | The world's seed, as typed or rolled. |
| 130 | `private boolean earth` | The real Earth or the generated world (0.7.121): Earth when its data is here. |
| 132 | `private EarthSite.Data earthData` | What the game found when it looked for the Earth's data; null while it is looking. |
| 134 | `private double spotLat` | The spot picked, degrees; NaN until one is. |
| 136 | `private String spotPlace` | The place it was picked by, from the search or Random spot; null for a click. |
| 138 | `private EarthSite.Card card` | The card for the spot, and the key it was measured for; null while it is measured. |
| 139 | `private World.Key cardKey` |  |
| 141 | `private String searchText` | The search as typed. |
| 145 | `private Label currencyLine, whyNot` |  |
| 146 | `private Button found` |  |
| 147 | `private TextField nameField, searchField` |  |
| 148 | `private VBox cardBox, hitsBox` |  |
| 151 | `private final Globe globe` | The globe, kept across redraws: its view and its last picture. |
| 161 | `private long cardAsked` | Which measurement is the latest: a card that comes back for an older one is dropped. |
| 994 | `double lat` | The view: its centre, degrees, and its scale, km a pixel. |
| 996 | `private Canvas canvas` |  |
| 997 | `private WritableImage picture` |  |
| 999 | `private double drawnLat` | The view the picture was drawn for; a drag's offset from it while it is not drawn again. |
| 1000 | `private long asked` |  |
| 1001 | `private double pressX, pressY, dragX, dragY` |  |
| 1002 | `private boolean dragging` |  |
| 1005 | `private final ExecutorService worker` | The worker the globe is drawn on: one at a time, the newest asked for. |
| 1226 | `private static int[] texture` | The globe's picture of the planet, made once from a reader (Globe.TEXEL_ARCMIN), and the reader it was made from. |
| 1227 | `private static EarthData textured` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 107 | 1224 | **type** `final class FoundingScreen` | Found a city: its name, its money, what the founders leave in the treasury and the vault, the ground it stands on and the world it is founded into. |
| 115 | 1 | `FoundingScreen(UserInterface ui)` |  |

### the choices, between redraws (lines 117-142)

### what a keystroke refreshes (lines 143-224)

| line | len | member | says |
|---:|---:|---|---|
| 164 | 36 | `void show()` | From the main menu: a fresh page on the defaults, whatever was typed last time. |

### THE PAGE (lines 225-420)

| line | len | member | says |
|---:|---:|---|---|
| 228 | 185 | `void draw()` |  |
| 415 | 5 | `private static Label caption(String text)` | A section's caption over its controls, as the mockups set it. |

### THE WORLD: EARTH OR GENERATED (0.7.121) (lines 421-620)

| line | len | member | says |
|---:|---:|---|---|
| 426 | 14 | `private HBox kindChips()` | The two joined chips, Earth \| Generated: Earth greyed while its data is being looked for or is not here. |
| 441 | 10 | `private static Button kindChip(String words, boolean on, boolean first, boolean usable)` |  |
| 453 | 22 | `private VBox generatedBlock()` | THE WORLD on a generated world: the chips, and the seed's row as since 0.7.56; where the Earth's data is not here, why. |
| 476 | 9 | `private Button diceButton(String tip, Runnable roll)` |  |
| 487 | 30 | `private VBox earthBlock()` | THE WORLD on the real Earth: the chips, the search and Random spot, the globe with the hits over it, and the card. |
| 519 | 19 | `private void showHits()` | The search's hits over the globe: each its name and where it is; none while nothing is typed. |
| 539 | 4 | `private List<EarthData.Place> hits()` |  |
| 545 | 6 | `private void pickPlace(EarthData.Place p)` | A place picked from the search: the spot, the view on it, the search cleared, the card measured. |
| 553 | 19 | `private void randomSpot()` | Random spot: a new seed, and the place it picks. |
| 574 | 6 | `void pick(double lat, double lon, boolean closer)` | A spot picked - by a click, the search or Random spot: the view centred on it (and closer, `closer`), its card measured. |
| 582 | 38 | `private void measure()` | The card for the spot as chosen now, measured on the worker; the card shows the measuring until it comes back. |

### the card (lines 621-824)

| line | len | member | says |
|---:|---:|---|---|
| 624 | 33 | `private void fillCard()` | The card's words under the globe: where the spot is, then what is within 5 km, its geology and iron, and why not or what to know. |
| 663 | 3 | `static String seedWords(long seed)` | The card's last line: what the seed decides on Earth. |
| 668 | 10 | `static String geologyWords(EarthSite.Card c)` | The card's geology line: the province and its class, and the multiplier of each resource in fields. |
| 680 | 4 | `static String siteWords(EarthSite.Card c)` | The card's first line: where the site is, from the nearest place, and how far the snap moved it. |
| 686 | 3 | `static String groundWords(EarthSite.Card c)` | The card's ground line. |
| 691 | 4 | `static String waterWords(EarthSite.Card c)` | The card's water line. |
| 697 | 5 | `static String ironWords(EarthSite.Card c)` | The card's iron line. |
| 703 | 7 | `private static Label cardLine(String words, String colour)` |  |
| 712 | 11 | `static String kindWords(Geology.Kind k)` | A geologic class in the card's words, as the map's hover writes it. |
| 725 | 5 | `static String share(double s)` | A share as the card writes it: a whole per cent, and "under 1%" for a little. |
| 732 | 3 | `static String distance(double km)` | A distance as the card writes it: a tenth of a km under ten, whole km from there. |
| 742 | 50 | `private VBox presetCard(Founding.Preset p)` | One of the four starts, as a card: its name, its money, and one line on how hard it is - never on what the money buys (Jerus: "even the screen shouldnt say what the money could buy"). |
| 794 | 11 | `private String insaneWords()` | Insane's card's tip: the advances, and the land bond on the plots this world's centre holds (Founding.landBondUsd(), 0.7.106) - the default world's while the seed will not parse; on Earth (0.7.121) the spot's, once it... |
| 807 | 3 | `private static String compact(String amount)` | A whole amount without its ".0": "D$100.0M" is "D$100M" on a card, where the room is a card's. |
| 812 | 12 | `private Button worldChip(double m, boolean first, boolean last)` | One choice of world, as one of a row of five joined segments. |

### WHAT A KEYSTROKE CHANGES (lines 825-843)

| line | len | member | says |
|---:|---:|---|---|
| 828 | 15 | `private void refresh()` |  |

### THE CHOICES, AS THE MODEL READS THEM (lines 844-945)

| line | len | member | says |
|---:|---:|---|---|
| 849 | 4 | `private Currency currency()` | The money as chosen, or null when a typed pair does not pass. |
| 855 | 3 | `private double cash()` | The treasury in thousands, or NaN when a custom figure will not parse. |
| 860 | 3 | `private double reserveUsd()` | The vault in thousands of US dollars, or NaN. |
| 865 | 3 | `private Long seed()` | The world's seed as typed, or null when it is not a whole number. |
| 876 | 18 | `private String problem()` | Why Found is not lit, or null: the model's reason, or the screen's when a field will not parse - and on Earth (0.7.121) no spot yet, the card still being measured, or its refusal. |
| 896 | 6 | `private Founding generatedChoices()` | The founding as chosen on its seed's generated world: the city's own choices, which problem() asks first. |
| 904 | 4 | `private Founding choices()` | The founding as chosen: on Earth (0.7.121) round the spot picked. |
| 913 | 15 | `boolean foundIfReady()` | Found it if it can be - Found's button and the Enter key. |
| 929 | 8 | `private static double millions(String typed)` |  |
| 938 | 3 | `private static String trim(double v)` |  |
| 942 | 3 | `private static String percent(double m)` |  |

### THE GLOBE (0.7.121, batch E4; spec-earth 2: "the globe from the 4' (lines 946-1196)

| line | len | member | says |
|---:|---:|---|---|
| 967 | 229 | **type** `final class Globe` |  |
| 1012 | 42 | `StackPane node(FoundingScreen screen)` _(in FoundingScreen.Globe)_ | The globe's canvas with its zoom buttons, made again with the page. |
| 1055 | 8 | `private Button zoomButton(String words, String tip)` _(in FoundingScreen.Globe)_ |  |
| 1065 | 7 | `void centre(double atLat, double atLon, boolean closer)` _(in FoundingScreen.Globe)_ | The view centred on a spot, and as close as a pick opens (`closer`) if it is wider than that. |
| 1073 | 4 | `private void zoom(double by)` _(in FoundingScreen.Globe)_ |  |
| 1079 | 5 | `private double snapped(double km)` _(in FoundingScreen.Globe)_ | A scale as drawn: on the ground a whole number of plots a pixel. |
| 1086 | 5 | `private void turn(double dx, double dy)` _(in FoundingScreen.Globe)_ | The view turned by a drag of (dx, dy) pixels: the ground follows the pointer. |
| 1093 | 6 | `double[] latLonAt(double px, double py)` _(in FoundingScreen.Globe)_ | The latitude and longitude under a canvas point, in the view as it is drawn; null off the planet. |
| 1101 | 7 | `double[] at(double atLat, double atLon)` _(in FoundingScreen.Globe)_ | Where a latitude and longitude lie on the canvas in the view as drawn, or null where it is not seen. |
| 1110 | 29 | `void redraw()` _(in FoundingScreen.Globe)_ | The picture asked for again, on the worker: the newest wins. |
| 1141 | 5 | `private World.Key spotKey()` _(in FoundingScreen.Globe)_ | The spot's own world's key while the view is centred on it (the city's ground), else null. |
| 1148 | 47 | `void repaint()` _(in FoundingScreen.Globe)_ | The canvas painted: the last picture (slid by a drag), the spot, and the site with its 5 km once the card has one. |

### the globe's arithmetic (lines 1197-1330)

| line | len | member | says |
|---:|---:|---|---|
| 1200 | 14 | `static boolean orthoInverse(double lat0, double lon0, double x, double y, double[] ll)` | The orthographic projection's inverse round (lat0, lon0): the point x metres east and y north on the plane, into ll; false off the planet's disc. |
| 1216 | 8 | `static boolean orthoForward(double lat0, double lon0, double lat, double lon, double[] xy)` | ...and forward: (lat, lon) on the plane round (lat0, lon0), metres; false on the far side. |
| 1230 | 45 | `static synchronized int[] texture(EarthData d)` | The planet's picture: a texel every TEXEL_ARCMIN, its colour the sea's, the lakes' and the land's by their shares in the 4' layers - the land the map's grass, its forest by land cover, ice and desert marked. |
| 1277 | 9 | `static int mix(int[] colours, double[] w)` | Colours mixed by weights, opaque. |
| 1288 | 20 | `static int[] globePixels(EarthData d, double lat0, double lon0, double kmPx)` | The globe wide: each pixel's point on the planet, orthographic round the view's centre, its texel's colour; space off the disc. |
| 1310 | 20 | `static int[] groundPixels(World.Key spot, double lat0, double lon0, double kmPx)` | The ground close: the city's own far view (MapTiles.nodeTerrain()) on an Earth world centred on the view - the spot's own when it is there - a whole number of plots a pixel; the void past the planet's rim. |


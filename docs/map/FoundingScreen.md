# FoundingScreen.java - 1,583 lines · 69 methods · 20 constants · interface

`ham/citybuildersim/ui/FoundingScreen.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

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
> a custom founding are Founding's; and whether the city can be founded, and if
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
> depend on it - the currency, the figures of the cards and the bounds written
> in its sign (FM1), the sign's place chips, and whether Found is lit and why
> not - so the field being typed in keeps its focus and its caret.
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
> says in plain words where the file goes and, since 0.7.125, how it gets
> there (EarthSite.dataWords()). On Earth the panel widens to two
> ... (37 more lines in the source)

**Uses:** [Palette](Palette.md) (133), [Founding](Founding.md) (62), [Currency](Currency.md) (34), [EarthSite](EarthSite.md) (28), [World](World.md) (20), [Nations](Nations.md) (17), [GlobeFrame](GlobeFrame.md) (15), [EarthProj](EarthProj.md) (14), [EarthData](EarthData.md) (12), [MapFrame](MapFrame.md) (4), [GlobePicture](GlobePicture.md) (4), [UserInterface](UserInterface.md) (2), [FoundingName](FoundingName.md) (2), [SignFont](SignFont.md) (2), [Resource](Resource.md) (2), [ForeignAccounts](ForeignAccounts.md) (2), [PolicyScreen](PolicyScreen.md) (2), [TileRaster](TileRaster.md) (2), [Icons](Icons.md) (1), [Geology](Geology.md) (1), [CityCalendar](CityCalendar.md) (1), [WorldData](WorldData.md) (1), [WorldEconomy](WorldEconomy.md) (1), [MapTiles](MapTiles.md) (1)

**Used by (2):** [SlopeRenders](SlopeRenders.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 140 | · the choices, between redraws |
| 178 | · what a keystroke refreshes |
| 272 | THE PAGE |
| 289 | · · the name |
| 364 | · · what it starts with |
| 408 | · · the ground |
| 411 | · · the world beyond the city |
| 414 | · · found it |
| 499 | THE WORLD: EARTH OR GENERATED (0.7.121) |
| 714 | · the card |
| 961 | THE WORLD BEYOND THE CITY: HISTORICAL OR RANDOM (WN1; spec-nations 7, board 4) |
| 1036 | WHAT A KEYSTROKE CHANGES |
| 1065 | THE CHOICES, AS THE MODEL READS THEM |
| 1186 | THE GLOBE (0.7.121, batch E4; spec-earth 2: "the globe from the 4' |
| 1412 | · W, A, S and D (0.7.135; spec-globe 3.11, ★E9: the founding globe takes the keys while it has the focus) |
| 1560 | · the close tier's ground |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 133 | `FoundingScreen.SCREEN` | `"showFoundingScreen"` | This screen's name for clearMenu(), the key filter and isGameMenu(). |
| 192 | `FoundingScreen.CARDS` | `Executors.newSingleThreadExecutor(r -> { Thread t = new Thread(r, "founding-c...` | The worker the card is measured on, one at a time. |
| 246 | `FoundingScreen.PANEL` | `760` | The panel's width, as the mockups draw it. |
| 249 | `FoundingScreen.PANEL_PAD` | `40` | The panel's padding, left and right: what the cards and fields share is the rest. |
| 252 | `FoundingScreen.SEED_FIELD` | `200` | The World's field: room for a seed of nineteen digits and a sign in the figures' face at 14 px (0.7.56). |
| 255 | `FoundingScreen.PATH_SEED_FIELD` | `130` | The path's seed field (WN1): 130 px, room for a rolled seed's nine digits in the figures' face at 14 px, so the row of chips, field and dice fits the Earth's left column (LEFT). |
| 258 | `FoundingScreen.EARTH_PANEL` | `1200` | The panel on Earth (0.7.121): two columns, the city's and the world's, 40 apart - 1,200 px, inside a 1,280 window with the page's margins. |
| 261 | `FoundingScreen.COLUMN_GAP` | `40` | Between the two columns on Earth. |
| 264 | `FoundingScreen.GLOBE_W` | `GlobeFrame.FOUNDING_W` | The globe's width, the right column's: 560 px (GlobeFrame.FOUNDING_W since 0.7.135, so a harness draws what it draws). |
| 267 | `FoundingScreen.GLOBE_H` | `GlobeFrame.FOUNDING_H` | The globe's height: 270 px, so the page still fits an 868-pixel window with the longest card under it (the words probe's, 0.7.121; GlobeFrame.FOUNDING_H since 0.7.135). |
| 270 | `FoundingScreen.LEFT` | `EARTH_PANEL - 2 * PANEL_PAD - COLUMN_GAP - GLOBE_W` | The left column's width on Earth: what the panel leaves. |
| 757 | `FoundingScreen.NO_SPOT_WORDS` | `"None yet.Drag the globe or hold W, A, S and D to turn it, zoom with the whee...` | The card's line before a spot is picked; how the globe is used. |
| 1076 | `FoundingScreen.SIGN_EXAMPLE` | `"2.6M"` | The figure the place chips show the sign beside. |
| 1215 | `FoundingScreen.Globe.MOST_KM_PX` | `2 * EarthProj.RADIUS_M / 1000 /(GlobeFrame.FIT_SHARE * GLOBE_H)` | The widest view: the whole planet, 12,742 km, across GlobeFrame.FIT_SHARE (nine tenths) of the globe's height. |
| 1218 | `FoundingScreen.Globe.GROUND_KM_PX` | `GlobeFrame.FOUNDING_HANDOFF_KM` | From here closer the ground is drawn round the view's centre, not through the globe: GlobeFrame.FOUNDING_HANDOFF_KM, 2.32 km a pixel (★E9; 4 until 0.7.135). |
| 1221 | `FoundingScreen.Globe.LEAST_KM_PX` | `5 * World.PLOT_M / 1000.0` | The closest view: 150 m a pixel, five plots (spec-earth 2's 150 m view). |
| 1224 | `FoundingScreen.Globe.PICK_KM_PX` | `0.3` | How close a pick from the search or Random spot opens: 0.3 km a pixel, the 5 km round the site 33 pixels across. |
| 1227 | `FoundingScreen.Globe.STEP` | `MapFrame.ZOOM_STEP` | A wheel notch's zoom, the map's: MapFrame.ZOOM_STEP. |
| 1230 | `FoundingScreen.Globe.SPACE` | `0xff0b1118` | Space round the globe: the page's stage. |
| 1233 | `FoundingScreen.Globe.COLOURS` | `GlobePicture.colours(false)` | The ground's colours, as the map draws them where the city owns it (GlobePicture.colours()): the founding globe dims nothing. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 136 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 143 | `private String cityName` | The name as the field reads, mirrored from `name`, which knows whether a place or the player wrote it (FN1). |
| 144 | `private final FoundingName name` |  |
| 145 | `private boolean ownCurrency` |  |
| 146 | `private String typedName` |  |
| 148 | `private String typedSign` | The typed money's sign and its place (FM1): "$" before the figure until the player types another or moves it. |
| 149 | `private boolean signAfter` |  |
| 150 | `private Founding.Preset preset` |  |
| 152 | `private String customCash` | A custom founding, as typed: millions of the city's money, and millions of US dollars. |
| 154 | `private String worldSeed` | The world's seed, as typed or rolled. |
| 156 | `private Nations.Path path` | The path of the world beyond the city (WN1): Historical or Random. |
| 158 | `private boolean historicalEquipment` | The equipment chosen (R1d): the "1900" chip or "All fitted". |
| 160 | `private boolean startOnGold` | The "start on gold" box beside the money (FB14; GD3-F, spec-gold-money 4.3): off until ticked, kept in the founding (Founding.withGold()). |
| 162 | `private String pathSeed` | The path's seed, as typed or rolled: not the ground's (WN1). |
| 165 | `private boolean earth` | The real Earth or the generated world (0.7.121): Earth when its data is here. |
| 167 | `private EarthSite.Data earthData` | What the game found when it looked for the Earth's data; null while it is looking. |
| 169 | `private double spotLat` | The spot picked, degrees; NaN until one is. |
| 171 | `private String spotPlace` | The place it was picked by, from the search or Random spot; null for a click. |
| 173 | `private EarthSite.Card card` | The card for the spot, and the key it was measured for; null while it is measured. |
| 174 | `private World.Key cardKey` |  |
| 176 | `private String searchText` | The search as typed. |
| 180 | `private Label currencyLine, whyNot, boundsLine, goldLine` |  |
| 181 | `private Button found, beforeChip, afterChip` |  |
| 183 | `private final java.util.Map<Founding.Preset, Label> cardFigures` | The preset cards' figures and tips, written in the money being chosen: refresh() rewrites them on a keystroke, which redraws nothing. |
| 184 | `private final java.util.Map<Founding.Preset, Tooltip> cardTips` |  |
| 185 | `private TextField nameField, searchField` |  |
| 186 | `private VBox cardBox, hitsBox` |  |
| 189 | `private final Globe globe` | The globe, kept across redraws: its view and its last picture. |
| 199 | `private long cardAsked` | Which measurement is the latest: a card that comes back for an older one is dropped. |
| 1236 | `double lat` | The view: its centre, degrees, and its scale, km a pixel. |
| 1239 | `private final GlobeFrame wide` | The wide view's arithmetic: the map's globe, a canvas's size, north up (it never comes near the map's handoff, where it would turn). |
| 1241 | `private Canvas canvas` |  |
| 1242 | `private WritableImage picture` |  |
| 1244 | `private double drawnLat` | The view the picture was drawn for. |
| 1245 | `private long asked` |  |
| 1246 | `private double pressX, pressY, lastX, lastY` |  |
| 1247 | `private boolean dragging` |  |
| 1249 | `private boolean keyW, keyA, keyS, keyD` | W, A, S and D held, and when they last turned it. |
| 1250 | `private long keyNanos` |  |
| 1252 | `private javafx.animation.AnimationTimer keys` | The frames that turn it while a key is held: made at the first key (an AnimationTimer needs the toolkit, which a harness's window has not started). |
| 1255 | `private final ExecutorService worker` | The worker the globe is drawn on: one at a time, the newest asked for. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 130 | 1454 | **type** `public final class FoundingScreen` | Found a city: its name, its money, what the founders leave in the treasury and the vault, the ground it stands on and the world it is founded into. |
| 138 | 1 | `FoundingScreen(UserInterface ui)` |  |

### the choices, between redraws (lines 140-177)

### what a keystroke refreshes (lines 178-271)

| line | len | member | says |
|---:|---:|---|---|
| 202 | 42 | `void show()` | From the main menu: a fresh page on the defaults, whatever was typed last time. |

### THE PAGE (lines 272-498)

| line | len | member | says |
|---:|---:|---|---|
| 275 | 216 | `void draw()` |  |
| 493 | 5 | `private static Label caption(String text)` | A section's caption over its controls, as the mockups set it. |

### THE WORLD: EARTH OR GENERATED (0.7.121) (lines 499-713)

| line | len | member | says |
|---:|---:|---|---|
| 504 | 14 | `private HBox kindChips()` | The two joined chips, Earth \| Generated: Earth greyed while its data is being looked for or is not here. |
| 520 | 8 | `private static Button signChip(String sign, TextField into)` | One of the signs the font draws, as a small button that types itself into the sign's field. |
| 529 | 10 | `private static Button kindChip(String words, boolean on, boolean first, boolean usable)` |  |
| 541 | 22 | `private VBox generatedBlock()` | THE WORLD on a generated world: the chips, and the seed's row as since 0.7.56; where the Earth's data is not here, why. |
| 564 | 9 | `private Button diceButton(String tip, Runnable roll)` |  |
| 575 | 30 | `private VBox earthBlock()` | THE WORLD on the real Earth: the chips, the search and Random spot, the globe with the hits over it, and the card. |
| 607 | 19 | `private void showHits()` | The search's hits over the globe: each its name and where it is; none while nothing is typed. |
| 627 | 4 | `private List<EarthData.Place> hits()` |  |
| 633 | 11 | `private void pickPlace(EarthData.Place p)` | A place picked from the search: the spot, the view on it, the search cleared, the card measured, and (FN1) the city named for it unless the player has named it. |
| 646 | 19 | `private void randomSpot()` | Random spot: a new seed, and the place it picks. |
| 667 | 6 | `void pick(double lat, double lon, boolean closer)` | A spot picked - by a click, the search or Random spot: the view centred on it (and closer, `closer`), its card measured. |
| 675 | 38 | `private void measure()` | The card for the spot as chosen now, measured on the worker; the card shows the measuring until it comes back. |

### the card (lines 714-960)

| line | len | member | says |
|---:|---:|---|---|
| 717 | 38 | `private void fillCard()` | The card's words under the globe: where the spot is, then what is within 5 km, its geology and iron, and why not or what to know. |
| 761 | 3 | `public static String seedWords(long seed)` | The card's last line: what the seed decides on Earth. |
| 766 | 10 | `public static String geologyWords(EarthSite.Card c)` | The card's geology line: the province and its class, and the multiplier of each resource in fields. |
| 778 | 4 | `public static String siteWords(EarthSite.Card c)` | The card's first line: where the site is, from the nearest place, and how far the snap moved it. |
| 784 | 4 | `public static String groundWords(EarthSite.Card c)` | The card's ground line; ice's share after the sea's where there is any (0.7.127). |
| 790 | 4 | `public static String waterWords(EarthSite.Card c)` | The card's water line. |
| 796 | 5 | `public static String ironWords(EarthSite.Card c)` | The card's iron line. |
| 802 | 7 | `private static Label cardLine(String words, String colour)` |  |
| 811 | 11 | `static String kindWords(Geology.Kind k)` | A geologic class in the card's words, as the map's hover writes it. |
| 824 | 5 | `static String share(double s)` | A share as the card writes it: a whole per cent, and "under 1%" for a little. |
| 831 | 3 | `static String distance(double km)` | A distance as the card writes it: a tenth of a km under ten, whole km from there. |
| 841 | 51 | `private VBox presetCard(Founding.Preset p)` | One of the four starts, as a card: its name, its money, and one line on how hard it is - never on what the money buys (Jerus: "even the screen shouldnt say what the money could buy"). |
| 894 | 11 | `private String insaneWords()` | Insane's card's tip: the advances, and the land bond on the plots this world's centre holds (Founding.landBondUsd(), 0.7.106) - the default world's while the seed will not parse; on Earth (0.7.121) the spot's, once it... |
| 907 | 4 | `private Currency chosenMoney()` | The money the page is choosing now: the typed one, the city's derived one, or the "$" before while a typed pair does not pass yet. |
| 913 | 4 | `private String cardWords(Founding.Preset p)` | A card's figures, the treasury (with the money's qualified sign, as the vault is beside it) and the vault, written in the money being chosen. |
| 919 | 4 | `private String tipWords(Founding.Preset p)` | A card's tip: the same figures in full. |
| 929 | 17 | `private String goldWords()` | The "start on gold" box's line (FB14): what the founding puts into gold, in the money being chosen - Founding.goldUsd() at the opening rate, the law's 40% of the treasury and never more than the vault, which is all of... |
| 948 | 7 | `private String boundsWords()` | The Custom fields' bounds, in the money being chosen. |
| 957 | 3 | `private static String compact(String amount)` | A whole amount without its ".0": "D$100.0M" is "D$100M", and "2.0B kr" "2B kr", on a card, where the room is a card's. |

### THE WORLD BEYOND THE CITY: HISTORICAL OR RANDOM (WN1; spec-nations 7, board 4) (lines 961-1035)

| line | len | member | says |
|---:|---:|---|---|
| 971 | 22 | `private static String[] pathWords(Nations.Path p)` | The path's three texts: the line under the row, the (i)'s whole words, and the chip's tooltip, which is the whole words' first sentence. |
| 995 | 13 | `private VBox equipmentBlock()` | The 1900 founding and the all-fitted one, the page's default (R1d call 2), kept in the founding record. |
| 1010 | 25 | `private VBox pathBlock()` | The two path chips, its seed and the words for the path chosen. |

### WHAT A KEYSTROKE CHANGES (lines 1036-1064)

| line | len | member | says |
|---:|---:|---|---|
| 1039 | 25 | `private void refresh()` |  |

### THE CHOICES, AS THE MODEL READS THEM (lines 1065-1185)

| line | len | member | says |
|---:|---:|---|---|
| 1070 | 4 | `private Currency currency()` | The money as chosen, or null when a typed name, code or sign does not pass. |
| 1079 | 5 | `private Currency signedAs(boolean after)` | A money that is only the typed sign, in the given place, for the place chips' captions: the "$" where the sign does not pass yet. |
| 1086 | 3 | `private double cash()` | The treasury in thousands, or NaN when a custom figure will not parse. |
| 1091 | 3 | `private double reserveUsd()` | The vault in thousands of US dollars, or NaN. |
| 1096 | 3 | `private Long seed()` | The world's seed as typed, or null when it is not a whole number. |
| 1101 | 3 | `private Long pathSeed()` | The path's seed as typed (WN1), or null when it is not a whole number. |
| 1112 | 22 | `private String problem()` | Why Found is not lit, or null: the model's reason, or the screen's when a field will not parse - and on Earth (0.7.121) no spot yet, the card still being measured, or its refusal. |
| 1136 | 10 | `private Founding generatedChoices()` | The founding as chosen on its seed's generated world: the city's own choices, which problem() asks first. |
| 1148 | 4 | `private Founding choices()` | The founding as chosen: on Earth (0.7.121) round the spot picked. |
| 1157 | 15 | `boolean foundIfReady()` | Found it if it can be - Found's button and the Enter key. |
| 1173 | 8 | `private static double millions(String typed)` |  |
| 1182 | 3 | `private static String trim(double v)` |  |

### THE GLOBE (0.7.121, batch E4; spec-earth 2: "the globe from the 4' (lines 1186-1559)

| line | len | member | says |
|---:|---:|---|---|
| 1212 | 347 | **type** `final class Globe` |  |
| 1262 | 51 | `StackPane node(FoundingScreen screen)` _(in FoundingScreen.Globe)_ | The globe's canvas with its zoom buttons, made again with the page. |
| 1314 | 8 | `private Button zoomButton(String words, String tip)` _(in FoundingScreen.Globe)_ |  |
| 1324 | 7 | `void centre(double atLat, double atLon, boolean closer)` _(in FoundingScreen.Globe)_ | The view centred on a spot, and as close as a pick opens (`closer`) if it is wider than that. |
| 1333 | 1 | `private boolean isWide()` _(in FoundingScreen.Globe)_ | Whether the view is wide: drawn through the map's globe. |
| 1336 | 4 | `private GlobeFrame wide()` _(in FoundingScreen.Globe)_ | The wide view's frame, on the view. |
| 1342 | 24 | `private void zoomAt(double px, double py, double by)` _(in FoundingScreen.Globe)_ | A zoom by a factor (under one closer) at a canvas point: the point under it stays under it - through the map's globe while wide, round the view's centre close - snapped to whole plots close. |
| 1368 | 5 | `private double snapped(double km)` _(in FoundingScreen.Globe)_ | A scale as drawn: close, a whole number of plots a pixel. |
| 1375 | 10 | `private void turn(double x0, double y0, double x1, double y1)` _(in FoundingScreen.Globe)_ | The view turned by the pointer from (x0, y0) to (x1, y1): the point grabbed stays under it - the map's globe's drag while wide, the plane round the view's centre slid close. |
| 1387 | 8 | `private void slide(double dx, double dy)` _(in FoundingScreen.Globe)_ | Close, the ground slid by (dx, dy) pixels: the new centre is the point that lay (dx, dy) back from it, on the plane round the old. |
| 1397 | 6 | `double[] latLonAt(double px, double py)` _(in FoundingScreen.Globe)_ | The latitude and longitude under a canvas point, in the view as it is drawn; null off the planet. |
| 1405 | 6 | `double[] at(double atLat, double atLon)` _(in FoundingScreen.Globe)_ | Where a latitude and longitude lie on the canvas in the view as drawn, or null where it is not seen. |
| 1415 | 17 | `private boolean key(javafx.scene.input.KeyCode k, boolean down)` _(in FoundingScreen.Globe)_ | A key down or up on the focused globe: true when it was one of the four. |
| 1434 | 3 | `private void letGo()` _(in FoundingScreen.Globe)_ | The focus gone: the keys let go, whose key-ups it would not hear. |
| 1439 | 21 | `private void keysFrame()` _(in FoundingScreen.Globe)_ | A frame of the keys held: the globe turned by MapFrame.keyVelocity() over the time since the last - the map's globe's keyPan() while wide, the ground slid close. |
| 1462 | 40 | `void redraw()` _(in FoundingScreen.Globe)_ | The picture asked for again, on the worker: the newest wins. |
| 1504 | 5 | `private World.Key spotKey()` _(in FoundingScreen.Globe)_ | The spot's own world's key while the view is centred on it (the city's ground), else null. |
| 1511 | 47 | `void repaint()` _(in FoundingScreen.Globe)_ | The canvas painted: the last picture (where its centre lies now, at the view's scale), the spot, and the site with its 5 km once the card has one. |

### the close tier's ground (lines 1560-1583)

| line | len | member | says |
|---:|---:|---|---|
| 1563 | 20 | `static int[] groundPixels(World.Key spot, double lat0, double lon0, double kmPx)` | The ground close: the city's own far view (MapTiles.nodeTerrain()) on an Earth world centred on the view - the spot's own when it is there - a whole number of plots a pixel, each pixel's colour by its cover and relief... |


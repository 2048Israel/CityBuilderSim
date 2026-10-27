# LandScreen.java - 2,350 lines · 92 methods · 22 constants · interface

`ham/citybuildersim/ui/LandScreen.java` - generated 2026-10-27 by CodeMap; line numbers are as of that run.

> The land office: whether the city has room to grow, and which ground to
> buy - since 0.7.61 round the city's map (batch J4; the project's
> spec-land.md 2.8): a head with how the city pays; the HERO ROW, the map
> (MapView, 600 x 400: the city's land and its offers hatched and numbered
> over the world, Expand to lay it over the whole window) and beside it THE
> CITY - its size in km2, its share of the world's iron, the four sides as
> chips (a side listing fewer than six with its count) and the chosen side's
> six places as rows, each its place (#), its size (its blocks on hover),
> its ground and water as a bar, its deposits, its price, its price a dry
> km2 and Buy - or, waiting for room, one muted line (0.7.69);
> then THE GROUND - the ground free now and the best N as one bar, "Buy the
> best N" over it; what the ground is worth to the city in four cards - the
> margin, the ground on top of the build, the ore and oil, who is waiting;
> the price of ground over the city's life behind "details"; and, when the
> city is short, the funding page in the same frame. (Until 0.7.60 the best
> nine of the forty offers stood as a three-by-three of cards.)
> 
> BEST VALUE is the office's best: the cheapest dry ground a square foot
> with any ore and water in it paid for inside its price (Game.landShelf()'s
> order) - Jerus's "the best one is always at the top left", the shelf's
> first card until 0.7.60; since 0.7.61 the tag on its row and on its side's
> chip, and "Buy the best N" buys the shelf's first N. It is not
> LandMarket.bestValue(), the most dry ground a dollar among the offers not
> mostly sea, which Build's shortcut buys for room (Game.bestOffer()). MOST
> ORE is LandMarket.richest() in iron.
> 
> In US dollars since 0.7.6: every offer shows its dollar price and what
> that costs in local money at today's rate, and a chip pair at the top
> chooses whether the treasury converts cash for it (the default) or pays
> out of the vault. Since 0.7.13 an offer's price is in the money the toggle
> pays in, its button stays live and, short, opens the funding page sized to
> the gap (showLandFunding()), and a control buys the best N at once
> (nextPlots()); since 0.7.61 its size is in square kilometres, and since
> 0.7.64 the ground free and the bar's figures too - under a hundredth of a
> square kilometre in square metres since 0.7.68, ground prices a square
> metre (LandManager.areaWords(), perM2()).
> 
> WHY THE REDRAW (0.7.26). Jerus, on the screens not yet redone: "the others
> are still full of text and the design could be more intuitive and fun". The
> office was a ledger round its cards - a paragraph pinned over them, a
> hand-built position bar, four statements under them - and two of its
> verdicts were wrong: a red "USED" at 90% of the ground built on, where a
> city sits for centuries with nothing waiting (it takes NEEDS YOU's GROUND
> row now, CityNeeds.ground()), and "Who is waiting", which matched a word and
> missed a sector refused at the last moment (it reads
> Game.getLandBlockedSectors() now). Nothing it said is gone: what is not on a
> card is behind an (i). The design study is the project's
> spec-land-0726.md.
> 
> What it says is worked out in methods that make no node (strip(),
> groundFigures(), cityLine(), sideChips(), sideRows(), margin(), and the
> rest), so a probe without the toolkit can read every word on a played
> city; the drawing methods lay those out. Since 0.7.137 (batch M6) it also
> words and draws the expanded map's card for buying from the map (THE CARD
> ON THE MAP: mapCard(), mapCardNode()), its rows' words and figures the
> card's. Split out of UserInterface on 2026-09-18; the rail calls
> showLandMenu(), as do Build's LAND FREE and its no-land and no-deposit
> pages, NEEDS YOU's GROUND row, HOW THE CITY IS's GROUND USED and the
> inbox's landlock notice.

**Uses:** [Palette](Palette.md) (305), [LandParcel](LandParcel.md) (43), [LandManager](LandManager.md) (35), [Pieces](Pieces.md) (29), [Game](Game.md) (27), [LandmassView](LandmassView.md) (22), [CityLand](CityLand.md) (21), [LandMap](LandMap.md) (16), [LandMarket](LandMarket.md) (15), [Resource](Resource.md) (15), [Icons](Icons.md) (15), [SectorScreen](SectorScreen.md) (13), [MapView](MapView.md) (12), [HistoryScreen](HistoryScreen.md) (10), [World](World.md) (7), [TileRaster](TileRaster.md) (6), [UserInterface](UserInterface.md) (5), [OilView](OilView.md) (5), [DebtQuote](DebtQuote.md) (5), [Money](Money.md) (5), [CityNeeds](CityNeeds.md) (4), [Kept](Kept.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [ForeignAccounts](ForeignAccounts.md) (2), [SummaryScreen](SummaryScreen.md) (2), [LandClassView](LandClassView.md) (2), [Sector](Sector.md) (2), [Statement](Statement.md) (2), [Currency](Currency.md) (1), [BuildScreen](BuildScreen.md) (1)... and 7 more

**Used by (3):** [MapView](MapView.md), [PeopleScreen](PeopleScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 89 | THE LAND OFFICE (0.7.26) |
| 133 | · what the page remembers between draws (0.7.26) |
| 249 | THE WORDS. Everything the office says, worked out without a node - |
| 286 | · the head |
| 326 | · the ground |
| 474 | · buy the best N |
| 545 | · THE CITY AND ITS SIDES (0.7.61) |
| 800 | · what the ground is worth |
| 946 | · details |
| 961 | THE DRAWING |
| 1027 | · THE GROUND |
| 1140 | · THE LANDMASSES (ET5, 2026-10-25) |
| 1195 | · BUY THE BEST N (0.7.13; "the next N" until 0.7.61) |
| 1278 | · THE HERO ROW (0.7.61) |
| 1520 | · WHAT THE GROUND IS WORTH |
| 1695 | · details |
| 1718 | WHEN THE CITY IS SHORT (0.7.13), the land office's own page since 0.7.26 |
| 2005 | THE CARD ON THE MAP (0.7.137, batch M6; docs/studies/spec-earth.md 3) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 289 | `LandScreen.WHO_PAYS` | `"Investors build only on ground the city owns, and pay the city for each " + ...` | The page's (i): who pays for the ground, and who does not. |
| 298 | `LandScreen.PAY_TIPS` | `{ "Pay by converting cash", "Pay from the vault" }` | The toggle's tooltips: its 0.7.6 names, which the chips shortened. |
| 515 | `LandScreen.BEST_HEAD` | `"THE BEST OFFERS · cheapest dry ground first, from every side"` | The section's head over THE GROUND: the best N, from every side. |
| 656 | `LandScreen.EMPTY_PLACE` | `"no room on this edge yet: it lists when the city grows here"` | A place waiting for room, as its row says it (spec-grid 2.5): one muted line where its offer would stand. |
| 659 | `LandScreen.ROW_DEPOSITS` | `2` | Deposits a row shows by name before "+N": 2... |
| 662 | `LandScreen.ROW_DEPOSIT_CHARS` | `20` | ...while their words run to no more than this many characters together, else one: 20 - "8 · 136 Mt" and "1 · 179 kt" and "+1" measured 152.8 px in the column's 158 at 9 px Plex Mono. |
| 795 | `LandScreen.WHOLE_FIELDS` | `" in the ground: every field of it centred in this offer, whole, paid for in ...` | What a row's tooltip says after each resource's sites and tonnes (0.7.64, batch L): an offer holds every field centred on its ground whole, all its sites and tonnes, wherever its sites lie (CityLand) - Jerus: "whole i... |
| 901 | `LandScreen.ON_TOP_INFO` | `"The ground is charged on top of the build, so a cheap building on" + " expen...` | The second card's (i), the 0.7.6 note word for word, and what the bars are. |
| 925 | `LandScreen.ORE_INFO` | `"A mine stands on one deposit, a large one on twenty of one field, and each d...` | The Ore card's (i). |
| 943 | `LandScreen.WAITING_INFO` | `"As of last month — the sectors decide once a month, so ground bought" + " no...` | The Waiting card's (i), the 0.7.6 note word for word. |
| 956 | `LandScreen.DETAILS_INFO` | `"What the world asks for a square metre of ground, as each month recorded" + ...` | The chart's (i). |
| 1296 | `LandScreen.MAP_W` | `MapView.SMALL_W, HERO_H = MapView.SMALL_H` | The map's width and the hero row's height (spec-land 2.8). |
| 1299 | `LandScreen.HERO_GAP` | `16` | The gap between the map and THE CITY... |
| 1302 | `LandScreen.CITY_PANEL` | `657` | ...and THE CITY's width: what the content area's 1,273 px leave at 1,389 x 868 (spec-land 2.8's 657). |
| 1305 | `LandScreen.ROW_H` | `28` | A row's height: 28 px (spec-land 2.8), six of them, their heads and THE CITY's inside HERO_H. |
| 1308 | `LandScreen.ROW_COLUMNS` | `{ 24, 74, 64, 158, 82, 78, 76, 58 }` | The rows' columns, in pixels: the place (#), size, ground and water, deposits, price, a dry km2, tag, button - with the gaps, CITY_PANEL. |
| 1311 | `LandScreen.ROW_GAP` | `4` | The gap between a row's columns. |
| 1314 | `LandScreen.ROW_HEADS` | `{ "#", "size", "dry · fresh · sea", "deposits", "price", "a dry km²", "", "" }` | The rows' column names. |
| 1554 | `LandScreen.WORTH_CARD` | `"-fx-padding: 10 12 10 12; -fx-background-color: " + Palette.RAISED + ";" + "...` | A worth card's style, its edge's colour last. |
| 1773 | `LandScreen.ABROAD_INFO` | `"Issued abroad; the dollars are in reserve, and the " + "vault pays for the l...` | The dollar offers' (i), the 0.7.13 note word for word. |
| 2025 | `LandScreen.MAP_CARD_W` | `320` | The map's card, its width: 320 px, the pay toggle's wrap (payToggle()), so the toggle lies on one line in it. |
| 2028 | `LandScreen.MAP_CARD_PAD` | `12` | The map's card's padding, a side: the hover card's 9 px, and 3 more for the buttons' edges. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 85 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 128 | `int nextCount` | How many offers the best-N control buys; kept across redraws, held inside 1..listed. |
| 131 | `boolean detailsOpen` | Whether "details" is open; kept while the game runs, as the page's place is. |
| 145 | `private double freeBeforeBuying` | The ground free just before the purchase being drawn, or NaN: the solid segment grows from it. |
| 148 | `private int depositsBeforeBuying` | The deposits owned just before it, or -1: the Ore card pops the difference. |
| 151 | `private java.util.Set<Integer> lastShelf` | The plot ids on the shelf at the last draw, or null before the first. |
| 154 | `private int newAbove` | An offer whose id is above this is NEW (ids only grow: LandMarket's next id)... |
| 157 | `private int newMonth` | ...in this month only. |
| 160 | `private int shownMonth` | The month, the rate and the world's dollar price at the last draw: what the month's pop compares. |
| 161 | `private double shownRate` |  |
| 164 | `private Region waitingCard` | The waiting card as last drawn, for the strip's click to scroll to. |
| 167 | `private MapView map` | The map, the small view's and Expand's (MapView); made the first time the office is drawn, as the toolkit is up by then. |
| 170 | `int side` | The side whose six places the rows show (0 north, 1 east, 2 south, 3 west), -1 until the office's best decides it; and the offer picked on it, its place (0 to 5), or -1. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 82 | 2269 | **type** `final class LandScreen` | The land office: whether the city has room to grow, and which ground to buy - since 0.7.61 round the city's map (batch J4; the project's spec-land.md 2.8): a head with how the city pays; the HERO ROW, the map (MapView... |
| 87 | 1 | `LandScreen(UserInterface ui)` |  |

### THE LAND OFFICE (0.7.26) (lines 89-132)

### what the page remembers between draws (0.7.26) (lines 133-248)

| line | len | member | says |
|---:|---:|---|---|
| 173 | 13 | `void forget()` | A new city or a load: nothing on its shelf is new to the player, and nothing has just been bought. |
| 187 | 36 | `void showLandMenu()` |  |
| 230 | 13 | `void noteShelf()` | NEW, worked out: when the shelf's ids differ from the last draw's, the plots above the last draw's highest id arrived since - the refill after a purchase (LandManager.buyParcel() refills the window at once). |
| 245 | 3 | `boolean isNew(LandParcel p)` | Whether this plot came onto the shelf since the player last saw it, this month. |

### THE WORDS. Everything the office says, worked out without a node - (lines 249-285)

| line | len | member | says |
|---:|---:|---|---|
| 256 | 1 | `String here()` | The city's money's mark: "D$". |
| 259 | 1 | `String local(double thousandsPerSqFt)` | A ground price, kept a square foot, a square metre in the city's money: "D$500.74" (a square foot's D$46.52; since 0.7.68, Money.groundPrice()). |
| 262 | 1 | `static String dollars(double thousandsPerSqFt)` | ...in the world's: "US$264.15". |
| 265 | 3 | `String signedLocal(double thousandsPerSqFt)` | A ground price a square metre with its direction: "+D$313.88", "−D$33.37". |
| 270 | 4 | `double houseSqFt()` | The House's plot, in square feet: what "room for N houses" divides by; 0 if the catalogue has none. |
| 276 | 6 | `String room(double sqFt)` | "room for 1,209 houses", or "" when there is no House to count in. |
| 284 | 1 | `static String plots(int n)` | Some plots, as the office's notes say them: "The plot", "5 plots". |

### the head (lines 286-325)

| line | len | member | says |
|---:|---:|---|---|
| 293 | 3 | `String[] payNames()` | The pay toggle's two chips: "Convert D$" and "From the vault". |
| 306 | 19 | `String[] payCaption()` | The line under the toggle, and the whole sentence behind its (i) - the "how" sentence (0.7.6; its vault half 0.7.13's), word for word: converting, "D$0.7072 per US$ · vault US$6.8M untouched"; from the vault, "vault h... |

### the ground (lines 326-473)

| line | len | member | says |
|---:|---:|---|---|
| 329 | 1 | **type** `record Cell(String label, String value, String note, String tone)` | One cell of THE GROUND's strip: its label, its figure, its line, and the figure's colour. |
| 338 | 23 | `Cell[] strip()` | The four cells. |
| 363 | 9 | `List<LandParcel> nextShelf(Next next)` | The offers "the best N" buys, in the shelf's order. |
| 381 | 11 | `String[] groundFigures(Next next)` | The big figures beside the strip: the ground free and what it holds, then what the best N would leave free - "0.0301 km² free", "room for 40 houses", "→ 3.49 km² free after the best 5", "room for 4,704 houses" (square... |
| 398 | 13 | `List<String> reliefLines()` | What the city's relief asks of its ground (ET2), one line for each kind it owns, empty for a city whose ground is all flat: the steep ground only a works may stand on and how much of it works stand on, then the foothi... |
| 417 | 3 | `List<String> officeLines()` | What the office charges for each class of ground (ET3, spec-earth2-terrain 5.3), one line for each class the city owns or the shelf offers, empty while neither has any foothill or steep ground: LandClassView's words, ... |
| 422 | 8 | `double groundScale(Next next)` | The bar's scale: the ground free and the best N together. |
| 437 | 23 | `List<Segment> groundSegments(Next next, java.util.function.IntFunction<Runnable> go)` | The bar's segments: the ground free, solid pink; then the best N, each a ghost in the light pink, numbered in the shelf's order, with a sand stripe where it holds ore, its side and place, size and price on hover, and ... |
| 462 | 5 | `List<Tick> groundTicks()` | The bar's one tick: NEEDS YOU's line, the free ground under which the GROUND row is listed. |
| 469 | 4 | `String priceInToggle(LandParcel p)` | A plot's price in the money the toggle pays in: "D$149.7M" converting, "US$211.7M" from the vault. |

### buy the best N (lines 474-544)

| line | len | member | says |
|---:|---:|---|---|
| 485 | 2 | **type** `record Next(int n, int listed, List<Integer> ids, String total, String totalTone, String other, Pieces.Pres...` | What "Buy the best N" buys and says: how many, out of how many listed, their ids, the total in the toggle's money (neutral, red only when no way pays without debt - canAffordLandParcels()), the other money small, the ... |
| 488 | 25 | `Next next()` |  |
| 523 | 14 | `String offerInfo()` | The section's (i): the twenty-four offers, six a side, place by place; how the office ranks them (BEST VALUE, the best N); and the blocks every offer is made of, as LandMarket lists them (0.7.67: whole blocks of the c... |
| 539 | 5 | `static String blockSide(int level)` | A block's side in words (0.7.69): metres under a kilometre - "120 m", "960 m" - and kilometres from it - "1.92 km", "245.76 km" - exact, as a block is 2^level plots of World.PLOT_M. |

### THE CITY AND ITS SIDES (0.7.61) (lines 545-799)

| line | len | member | says |
|---:|---:|---|---|
| 558 | 5 | `static String km2Figure(double km2)` | A size in km2 as THE CITY writes it: a decimal from 1 km2 up, grouped - "107.8", "1,760,034.2" - three figures under it, "0.312". |
| 565 | 7 | `String cityLine()` | THE CITY's size, the land's five areas but forest: "107.8 km² · 89.6 dry · 0 fresh · 18.2 sea" (spec-land 2.8) - and since 0.7.109 (Z2), with a zone standing, the zones' sizes: "· 3.2 zoned" (ZoningView's ZONED). |
| 574 | 8 | `String worldLine()` | The world's line: "of the world's 1.12 Pt of iron, the city owns 0.0000452%" - its share of the world's tonnes, to three figures. |
| 584 | 4 | `String inToggle(double usdThousands)` | Thousands of US dollars in the money the toggle pays in: as listed from the vault, at today's rate converting. |
| 590 | 3 | `String perDryKm2(LandParcel p)` | An offer's price a dry km2, in the toggle's money: "D$5.6M"; "—" with no dry ground. |
| 595 | 4 | `LandParcel officeBest()` | The office's best offer, the shelf's first (BEST VALUE), or null with nothing listed. |
| 601 | 6 | `void chooseSide()` | The side shown before the player picks one: the office's best's, or the north. |
| 614 | 1 | **type** `record SideChip(int side, String name, String label, int listed, String best, boolean bestValue, String tip)` | One side's chip: the side, its name and the chip's words - the name, and since 0.7.69 " · N" when it lists fewer than six (spec-grid 2.5, "North · 4") - how many it lists, its cheapest dry ground a km2 in the toggle's... |
| 616 | 21 | `List<SideChip> sideChips()` |  |
| 639 | 1 | **type** `record DepositWords(String colour, String words, String tip)` | A deposit on a row: its resource's colour, its sites and amount - "3 · 38.4 Mt" - and the tooltip's line. |
| 651 | 3 | **type** `record Row(LandParcel parcel, String place, String km2, String sizeTip, double dry, double fresh, double se...` | One place as its row: its offer, its place (1 to 6), its size in km2 and the size's tooltip - its blocks and its ground (0.7.69, spec-grid 2.5) - its dry, fresh and sea shares for the bar, its deposits (the first ROW_... |
| 665 | 10 | `List<Row> sideRows(int side)` | A side's six places, 1 to 6: its offers, and a place waiting for room as its one muted line. |
| 677 | 6 | `Row emptyRow(int side, int at)` | A place waiting for room: its number, EMPTY_PLACE, and a tooltip naming it. |
| 690 | 10 | `static String sizeWords(LandParcel p)` | An offer's size, its blocks and its ground (0.7.69, spec-grid 2.5): "1 × 2 blocks of 120 m · 0.0288 km² · 0.0273 dry, 0.0015 fresh" - its blocks across and out, each part in its whole's unit (LandManager.partFigure())... |
| 702 | 3 | `static List<DepositWords> depositWords(java.util.function.ToIntFunction<Resource> sites, java.util.function.ToDoubleFunction<Re...` | Each resource in fields that ground holds - its colour, its sites and amount, the tooltip's line - in Resource's order: a row's (one offer's), and since 0.7.137 the map's card's (several summed, LandMap.card()). |
| 713 | 13 | `static List<DepositWords> depositWords(java.util.function.ToIntFunction<Resource> sites, java.util.function.ToDoubleFunction<Re...` | ...with a typed resource's sites by type on its tooltip's line (0.7.142, batch I13; spec-materials 5's I13 row, "the land office's hover - field types"): "Iron ore: 3 sites, 38.4 Mt - 2 hematite, 1 magnetite", off Cit... |
| 728 | 8 | `static String typeWords(Resource r, long[] byType)` | A typed resource's sites by type, each with any: "2 hematite, 1 magnetite", "4 limestone"; "" with none. |
| 738 | 8 | `static long[] typesOn(CityLand land, List<LandParcel> parcels, Resource r)` | The sites of each type of a resource several offers hold together (0.7.142): CityLand.sitesOnByType() summed. |
| 747 | 40 | `Row row(LandParcel p, LandParcel office, LandParcel richest)` |  |
| 798 | 1 | **type** `record TagWords(String text, String colour)` | A tag's words and colour. |

### what the ground is worth (lines 800-945)

| line | len | member | says |
|---:|---:|---|---|
| 803 | 1 | **type** `record BarWords(String name, String figure, String colour, double share)` | One of a card's bars: its name, its figure, its colour and its share of the card's scale. |
| 813 | 2 | **type** `record Margin(BarWords world, BarWords investors, String chip, String chipTone, String lastMonth, String in...` | The Margin card: the world's price today and what investors pay, on one scale; the margin; what investors paid for ground last month (NationalAccounts.getLandSales() - LandManager's own month's flow is cleared at the ... |
| 825 | 7 | `String partsWords()` | THE WORLD'S PRICE, IN ITS PARTS (0.7.55): the founding's dollars, what US prices have done since, and the premium for the city's crowding - "US$7.53 × 1.14 US prices × 104 crowding: 5,684 people a km² of the city's la... |
| 834 | 4 | `static String times(double multiple)` | A multiple as the land office writes it: "×1.65", "×36.0", "×104". |
| 839 | 33 | `Margin margin()` |  |
| 874 | 1 | **type** `record OnTop(String name, double ground, double build, String words, String share)` | One stacked bar on the second card: the building, its ground and its build, and the ground's share. |
| 881 | 18 | `List<OnTop> onTop()` | On top of the build: a House's plot and a food plant's (the Bakery's) at what investors pay today, each against the building's own cash cost - "House: ground D$372k · build D$266k · 58%". |
| 907 | 1 | **type** `record Ore(String deposits, String tonnes, String mines, boolean undug, String popped, String oil)` | The Ore card: the deposits, the tonnes, those under mines (since 0.7.124 the sites the mines take, a large mine's twenty: Game.minesCommitted()), whether ore lies undug, and the oil (0.7.61). |
| 909 | 14 | `Ore ore()` |  |
| 933 | 8 | `List<String> waitingNames()` | The sectors waiting on ground, by name: Game.getLandBlockedSectors(), as the sector pages and the inbox read it. |

### details (lines 946-960)

| line | len | member | says |
|---:|---:|---|---|
| 949 | 5 | `String spentLine()` | The line under the chart: what the city has paid the world for ground, and how much of it the vault paid. |

### THE DRAWING (lines 961-1026)

| line | len | member | says |
|---:|---:|---|---|
| 972 | 28 | `HBox head(String sub)` | The head: "Land office" with the building area's swatch and the page's (i), and at the right the pay toggle with its line, and "‹ Build". |
| 1002 | 24 | `VBox payToggle()` | The pay toggle, applied at once and saved with the city, and the line under it with its (i). |

### THE GROUND (lines 1027-1139)

| line | len | member | says |
|---:|---:|---|---|
| 1034 | 90 | `VBox groundPanel(Next next)` | THE GROUND: the strip and the big figures, then the bar and its scale - one panel, the shape of Construction's builders' gauge. |
| 1126 | 13 | `static HBox ghostSwatch(String colour, String name)` | A key's swatch for a ghost: tinted and outlined as the ghost is drawn. |

### THE LANDMASSES (ET5, 2026-10-25) (lines 1140-1194)

| line | len | member | says |
|---:|---:|---|---|
| 1143 | 8 | `static String toneColour(LandmassView.Tone tone)` | A table cell's tone as a colour. |
| 1158 | 24 | `VBox landmassesTable()` | The city's ground by landmass, a row each in SectorScreen.oilTable()'s shape: its dry ground, its free ground (the linked landmasses share one figure, an island no port links says its own and "only a port"), the termi... |
| 1184 | 10 | `private void scrollTo(javafx.scene.Node target)` | The page brought to a card: the menu's scroller set so it sits near the top. |

### BUY THE BEST N (0.7.13; "the next N" until 0.7.61) (lines 1195-1277)

| line | len | member | says |
|---:|---:|---|---|
| 1207 | 32 | `HBox nextPlots(Next next)` |  |
| 1241 | 12 | `HBox receiptLine(String receipt)` | This month's receipt, under ON OFFER: a pink tick and the receipt, wrapped rather than cut. |
| 1262 | 9 | `void buyOrFund(List<Integer> ids)` | Buys these plots the way the toggle pays, or - the city short - opens the funding page for them. |
| 1273 | 4 | `void rememberBeforeBuying()` | What a purchase is measured against when the page is drawn after it. |

### THE HERO ROW (0.7.61) (lines 1278-1519)

| line | len | member | says |
|---:|---:|---|---|
| 1316 | 12 | `HBox hero()` |  |
| 1330 | 3 | `void expandMapOn(MiniMapFrame.Box box)` | The map over the window on a box of plots (0.7.134: the minimap's click, UserInterface.openMapOn()), once the office is drawn. |
| 1335 | 5 | `void picked(int side, int place)` | The map picked a side, and an offer's place on it (-1 for none): its rows, the offer's lit. |
| 1342 | 15 | `VBox cityPanel()` | THE CITY, its sides, and the chosen side's offers. |
| 1359 | 32 | `javafx.scene.layout.FlowPane sideChipsNode()` | The four sides as Pieces' chips, each its cheapest dry ground a km2 after its name - and its count when it lists fewer than six - and BEST VALUE on the best's side. |
| 1393 | 10 | `VBox rowsNode(List<Row> rows, boolean turned)` | The rows: the column names, then the side's six places, 1 to 6; `turned`, their local prices pop. |
| 1405 | 6 | `private static Region column(Region cell, int c)` | A cell held to its column's width. |
| 1413 | 92 | `HBox rowNode(Row r, boolean turned)` | One offer's row: hover lights it on the map, a click picks it, its button buys it or opens the funding page; a place waiting for room, its muted line. |
| 1507 | 12 | `HBox emptyRowNode(Row r)` | A place waiting for room (spec-grid 2.5): its number, then EMPTY_PLACE across the rest of the row, muted. |

### WHAT THE GROUND IS WORTH (lines 1520-1694)

| line | len | member | says |
|---:|---:|---|---|
| 1523 | 10 | `javafx.scene.layout.GridPane worthCards()` | The four cards, a quarter of the page each. |
| 1535 | 17 | `VBox worthCard(String svg, String colour, String name, String info, javafx.scene.Node...body)` | A worth card's frame: its icon square, its name and its (i), then what it says. |
| 1558 | 14 | `VBox namedBar(BarWords w)` | One labelled bar on a worth card: its name and figure on a line, the bar under them. |
| 1573 | 22 | `VBox marginCard()` |  |
| 1596 | 25 | `VBox onTopCard()` |  |
| 1622 | 38 | `VBox oreCard()` |  |
| 1661 | 33 | `VBox waitingCard()` |  |

### details (lines 1695-1717)

| line | len | member | says |
|---:|---:|---|---|
| 1698 | 19 | `VBox details()` | "details ▸": the world's price of ground over the city's life, and what the city has paid for it. |

### WHEN THE CITY IS SHORT (0.7.13), the land office's own page since 0.7.26 (lines 1718-2004)

| line | len | member | says |
|---:|---:|---|---|
| 1747 | 1 | **type** `record Funding(String priceLine, String heldWords, String gapMoney, double held, double gap)` | What the funding page's card says: the price, what is held, the gap in money, and the bar's two parts. |
| 1749 | 22 | `Funding funding(List<Integer> ids)` |  |
| 1778 | 2 | **type** `record Offer(String name, String info, DebtQuote quote, String rate, String tone, String ending, Pieces.Pre...` | One loan on the funding page, worded - what Pieces.offerCard() draws: its name, its (i), its quote, the rate's words and colour, its ending, its button (a Pieces.Press since 0.7.34: the purchase, and the paper under i... |
| 1782 | 5 | `String buyWords(List<Integer> ids)` | What the funding page's buttons buy (0.7.34): "Buy · D$1.0B" for a plot, "Buy 5 plots · D$9.8B" for several, in the money the toggle pays in. |
| 1798 | 61 | `List<Offer> offers(List<Integer> ids)` | The loans the funding page offers for these plots, in the build page's order - the bond, then the note: converting, in local money for the cash gap; from the vault, in dollars on the world's curve for the vault's gap,... |
| 1860 | 79 | `void showLandFunding(List<Integer> ids)` |  |
| 1944 | 23 | `VBox topUpCard(List<Integer> ids, String here)` | The third way, from the vault: what the vault holds, the rest converted from cash - no debt. |
| 1969 | 3 | `Pieces.Press topUpPress(List<Integer> ids)` | The third way's button (0.7.34): the purchase, and what it takes - the 0.7.13 button's words, under it. |
| 1981 | 23 | `private void buyOnTheLoan(List<Integer> ids, String paper)` | The money is in: the plots are bought, each as its own button would buy it, and whatever the purchase answers is shown - the build screen's rule that a refusal nobody reads is a silent one (BuildScreen.goAhead()). |

### THE CARD ON THE MAP (0.7.137, batch M6; docs/studies/spec-earth.md 3) (lines 2005-2350)

| line | len | member | says |
|---:|---:|---|---|
| 2047 | 4 | **type** `record MapCard(List<Integer> ids, LandParcel first, String title, TagWords tag, List<LandParcel> chips, Str...` | What the map's card says for some picks, worked out without a node: the picks still listed and the first of them, the title (the place, or "3 offers") and one offer's tag, the chips of several, the size, the ground's ... |
| 2053 | 60 | `MapCard mapCard(List<Integer> picks)` | The card for these picks, or null with none of them listed. |
| 2115 | 7 | `static String sizeWords(LandMap.Card c)` | Several offers' size as one: "0.0864 km² · 0.0819 dry, 0.0045 fresh" - sizeWords()' ground, its parts in the whole's unit. |
| 2124 | 5 | `void mapCardPress(MapCard m, MapView map)` | What the card's main button - and Enter - does: buy them, or short issue the bond and buy them, or with no loan on offer open the funding page. |
| 2131 | 132 | `VBox mapCardNode(MapCard m, MapView map)` | The card as a node, its buttons the map's (MapView): the drawing of mapCard(). |
| 2265 | 30 | `VBox mapReceiptNode(String title, List<int[]> places, MapView map)` | A purchase made from the map, its receipt by the ground (spec-earth 3.1, board 6): what was bought - its title and its places, side and place - drawn as the office's receipt, the city's size now and each place listing... |
| 2297 | 21 | `private VBox mapCardFrame(String title, MapView map)` | The card's frame: its width and padding, a title line with a close at its right. |
| 2320 | 5 | `String mapHeadMoney()` | The money the map's head shows, as the pane covers the window's header (spec-earth 6.1): the cash, and paying from the vault its dollars first. |
| 2327 | 23 | `javafx.scene.layout.FlowPane mapPayChips(MapView map)` | The pay toggle on the map, its head's and its card's: the office's chips, applied at once - the office drawn again under the map, which stays - and the map told; the caller sets where they wrap. |


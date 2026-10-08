# LandScreen.java - 1,831 lines · 75 methods · 20 constants · interface

`ham/citybuildersim/ui/LandScreen.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

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
> city; the drawing methods lay those out. Split out of UserInterface on 2026-09-18; the rail calls
> showLandMenu(), as do Build's LAND FREE and its no-land and no-deposit
> pages, NEEDS YOU's GROUND row, HOW THE CITY IS's GROUND USED and the
> inbox's landlock notice.

**Uses:** [Palette](Palette.md) (221), [Game](Game.md) (31), [LandParcel](LandParcel.md) (29), [Pieces](Pieces.md) (22), [LandManager](LandManager.md) (21), [CityLand](CityLand.md) (16), [LandMarket](LandMarket.md) (13), [Icons](Icons.md) (8), [Resource](Resource.md) (7), [MapView](MapView.md) (6), [UserInterface](UserInterface.md) (5), [LandMap](LandMap.md) (5), [DebtQuote](DebtQuote.md) (5), [CityNeeds](CityNeeds.md) (4), [World](World.md) (4), [HistoryScreen](HistoryScreen.md) (4), [Money](Money.md) (4), [TileRaster](TileRaster.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [ForeignAccounts](ForeignAccounts.md) (2), [SummaryScreen](SummaryScreen.md) (2), [Sector](Sector.md) (2), [Statement](Statement.md) (2), [Currency](Currency.md) (1), [BuildScreen](BuildScreen.md) (1), [LandGrid](LandGrid.md) (1), [BuildAdvice](BuildAdvice.md) (1), [HistorySave](HistorySave.md) (1), [Rollover](Rollover.md) (1)

**Used by (2):** [PeopleScreen](PeopleScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 86 | THE LAND OFFICE (0.7.26) |
| 130 | · what the page remembers between draws (0.7.26) |
| 240 | THE WORDS. Everything the office says, worked out without a node - |
| 277 | · the head |
| 317 | · the ground |
| 428 | · buy the best N |
| 499 | · THE CITY AND ITS SIDES (0.7.61) |
| 704 | · what the ground is worth |
| 848 | · details |
| 863 | THE DRAWING |
| 929 | · THE GROUND |
| 1036 | · BUY THE BEST N (0.7.13; "the next N" until 0.7.61) |
| 1112 | · THE HERO ROW (0.7.61) |
| 1349 | · WHAT THE GROUND IS WORTH |
| 1524 | · details |
| 1547 | WHEN THE CITY IS SHORT (0.7.13), the land office's own page since 0.7.26 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 280 | `LandScreen.WHO_PAYS` | `"Investors build only on ground the city owns, and pay the city for each " + ...` | The page's (i): who pays for the ground, and who does not. |
| 289 | `LandScreen.PAY_TIPS` | `{ "Pay by converting cash", "Pay from the vault" }` | The toggle's tooltips: its 0.7.6 names, which the chips shortened. |
| 469 | `LandScreen.BEST_HEAD` | `"THE BEST OFFERS · cheapest dry ground first, from every side"` | The section's head over THE GROUND: the best N, from every side. |
| 608 | `LandScreen.EMPTY_PLACE` | `"no room on this edge yet: it lists when the city grows here"` | A place waiting for room, as its row says it (spec-grid 2.5): one muted line where its offer would stand. |
| 611 | `LandScreen.ROW_DEPOSITS` | `2` | Deposits a row shows by name before "+N": 2... |
| 614 | `LandScreen.ROW_DEPOSIT_CHARS` | `20` | ...while their words run to no more than this many characters together, else one: 20 - "8 · 136 Mt" and "1 · 179 kt" and "+1" measured 152.8 px in the column's 158 at 9 px Plex Mono. |
| 699 | `LandScreen.WHOLE_FIELDS` | `" in the ground: every field of it centred in this offer, whole, paid for in ...` | What a row's tooltip says after each resource's sites and tonnes (0.7.64, batch L): an offer holds every field centred on its ground whole, all its sites and tonnes, wherever its sites lie (CityLand) - Jerus: "whole i... |
| 804 | `LandScreen.ON_TOP_INFO` | `"The ground is charged on top of the build, so a cheap building on" + " expen...` | The second card's (i), the 0.7.6 note word for word, and what the bars are. |
| 828 | `LandScreen.ORE_INFO` | `"A mine stands on one deposit, and every mine draws on the city's tonnes" + "...` | The Ore card's (i). |
| 845 | `LandScreen.WAITING_INFO` | `"As of last month — the sectors decide once a month, so ground bought" + " no...` | The Waiting card's (i), the 0.7.6 note word for word. |
| 858 | `LandScreen.DETAILS_INFO` | `"What the world asks for a square metre of ground, as each month recorded" + ...` | The chart's (i). |
| 1130 | `LandScreen.MAP_W` | `MapView.SMALL_W, HERO_H = MapView.SMALL_H` | The map's width and the hero row's height (spec-land 2.8). |
| 1133 | `LandScreen.HERO_GAP` | `16` | The gap between the map and THE CITY... |
| 1136 | `LandScreen.CITY_PANEL` | `657` | ...and THE CITY's width: what the content area's 1,273 px leave at 1,389 x 868 (spec-land 2.8's 657). |
| 1139 | `LandScreen.ROW_H` | `28` | A row's height: 28 px (spec-land 2.8), six of them, their heads and THE CITY's inside HERO_H. |
| 1142 | `LandScreen.ROW_COLUMNS` | `{ 24, 74, 64, 158, 82, 78, 76, 58 }` | The rows' columns, in pixels: the place (#), size, ground and water, deposits, price, a dry km2, tag, button - with the gaps, CITY_PANEL. |
| 1145 | `LandScreen.ROW_GAP` | `4` | The gap between a row's columns. |
| 1148 | `LandScreen.ROW_HEADS` | `{ "#", "size", "dry · fresh · sea", "deposits", "price", "a dry km²", "", "" }` | The rows' column names. |
| 1383 | `LandScreen.WORTH_CARD` | `"-fx-padding: 10 12 10 12; -fx-background-color: " + Palette.RAISED + ";" + "...` | A worth card's style, its edge's colour last. |
| 1602 | `LandScreen.ABROAD_INFO` | `"Issued abroad; the dollars are in reserve, and the " + "vault pays for the l...` | The dollar offers' (i), the 0.7.13 note word for word. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 82 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 125 | `int nextCount` | How many offers the best-N control buys; kept across redraws, held inside 1..listed. |
| 128 | `boolean detailsOpen` | Whether "details" is open; kept while the game runs, as the page's place is. |
| 142 | `private double freeBeforeBuying` | The ground free just before the purchase being drawn, or NaN: the solid segment grows from it. |
| 145 | `private int depositsBeforeBuying` | The deposits owned just before it, or -1: the Ore card pops the difference. |
| 148 | `private java.util.Set<Integer> lastShelf` | The plot ids on the shelf at the last draw, or null before the first. |
| 151 | `private int newAbove` | An offer whose id is above this is NEW (ids only grow: LandMarket's next id)... |
| 154 | `private int newMonth` | ...in this month only. |
| 157 | `private int shownMonth` | The month, the rate and the world's dollar price at the last draw: what the month's pop compares. |
| 158 | `private double shownRate` |  |
| 161 | `private Region waitingCard` | The waiting card as last drawn, for the strip's click to scroll to. |
| 164 | `private MapView map` | The map, the small view's and Expand's (MapView); made the first time the office is drawn, as the toolkit is up by then. |
| 167 | `int side` | The side whose six places the rows show (0 north, 1 east, 2 south, 3 west), -1 until the office's best decides it; and the offer picked on it, its place (0 to 5), or -1. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 79 | 1753 | **type** `final class LandScreen` | The land office: whether the city has room to grow, and which ground to buy - since 0.7.61 round the city's map (batch J4; the project's spec-land.md 2.8): a head with how the city pays; the HERO ROW, the map (MapView... |
| 84 | 1 | `LandScreen(UserInterface ui)` |  |

### THE LAND OFFICE (0.7.26) (lines 86-129)

### what the page remembers between draws (0.7.26) (lines 130-239)

| line | len | member | says |
|---:|---:|---|---|
| 170 | 13 | `void forget()` | A new city or a load: nothing on its shelf is new to the player, and nothing has just been bought. |
| 184 | 30 | `void showLandMenu()` |  |
| 221 | 13 | `void noteShelf()` | NEW, worked out: when the shelf's ids differ from the last draw's, the plots above the last draw's highest id arrived since - the refill after a purchase (LandManager.buyParcel() refills the window at once). |
| 236 | 3 | `boolean isNew(LandParcel p)` | Whether this plot came onto the shelf since the player last saw it, this month. |

### THE WORDS. Everything the office says, worked out without a node - (lines 240-276)

| line | len | member | says |
|---:|---:|---|---|
| 247 | 1 | `String here()` | The city's money's mark: "D$". |
| 250 | 1 | `String local(double thousandsPerSqFt)` | A ground price, kept a square foot, a square metre in the city's money: "D$500.74" (a square foot's D$46.52; since 0.7.68, Money.groundPrice()). |
| 253 | 1 | `static String dollars(double thousandsPerSqFt)` | ...in the world's: "US$264.15". |
| 256 | 3 | `String signedLocal(double thousandsPerSqFt)` | A ground price a square metre with its direction: "+D$313.88", "−D$33.37". |
| 261 | 4 | `double houseSqFt()` | The House's plot, in square feet: what "room for N houses" divides by; 0 if the catalogue has none. |
| 267 | 6 | `String room(double sqFt)` | "room for 1,209 houses", or "" when there is no House to count in. |
| 275 | 1 | `static String plots(int n)` | Some plots, as the office's notes say them: "The plot", "5 plots". |

### the head (lines 277-316)

| line | len | member | says |
|---:|---:|---|---|
| 284 | 3 | `String[] payNames()` | The pay toggle's two chips: "Convert D$" and "From the vault". |
| 297 | 19 | `String[] payCaption()` | The line under the toggle, and the whole sentence behind its (i) - the "how" sentence (0.7.6; its vault half 0.7.13's), word for word: converting, "D$0.7072 per US$ · vault US$6.8M untouched"; from the vault, "vault h... |

### the ground (lines 317-427)

| line | len | member | says |
|---:|---:|---|---|
| 320 | 1 | **type** `record Cell(String label, String value, String note, String tone)` | One cell of THE GROUND's strip: its label, its figure, its line, and the figure's colour. |
| 329 | 23 | `Cell[] strip()` | The four cells. |
| 354 | 9 | `List<LandParcel> nextShelf(Next next)` | The offers "the best N" buys, in the shelf's order. |
| 372 | 11 | `String[] groundFigures(Next next)` | The big figures beside the strip: the ground free and what it holds, then what the best N would leave free - "0.0301 km² free", "room for 40 houses", "→ 3.49 km² free after the best 5", "room for 4,704 houses" (square... |
| 385 | 5 | `double groundScale(Next next)` | The bar's scale: the ground free and the best N together. |
| 397 | 17 | `List<Segment> groundSegments(Next next, java.util.function.IntFunction<Runnable> go)` | The bar's segments: the ground free, solid pink; then the best N, each a ghost in the light pink, numbered in the shelf's order, with a sand stripe where it holds ore, its side and place, size and price on hover, and ... |
| 416 | 5 | `List<Tick> groundTicks()` | The bar's one tick: NEEDS YOU's line, the free ground under which the GROUND row is listed. |
| 423 | 4 | `String priceInToggle(LandParcel p)` | A plot's price in the money the toggle pays in: "D$149.7M" converting, "US$211.7M" from the vault. |

### buy the best N (lines 428-498)

| line | len | member | says |
|---:|---:|---|---|
| 439 | 2 | **type** `record Next(int n, int listed, List<Integer> ids, String total, String totalTone, String other, Pieces.Pres...` | What "Buy the best N" buys and says: how many, out of how many listed, their ids, the total in the toggle's money (neutral, red only when no way pays without debt - canAffordLandParcels()), the other money small, the ... |
| 442 | 25 | `Next next()` |  |
| 477 | 14 | `String offerInfo()` | The section's (i): the twenty-four offers, six a side, place by place; how the office ranks them (BEST VALUE, the best N); and the blocks every offer is made of, as LandMarket lists them (0.7.67: whole blocks of the c... |
| 493 | 5 | `static String blockSide(int level)` | A block's side in words (0.7.69): metres under a kilometre - "120 m", "960 m" - and kilometres from it - "1.92 km", "245.76 km" - exact, as a block is 2^level plots of World.PLOT_M. |

### THE CITY AND ITS SIDES (0.7.61) (lines 499-703)

| line | len | member | says |
|---:|---:|---|---|
| 512 | 5 | `static String km2Figure(double km2)` | A size in km2 as THE CITY writes it: a decimal from 1 km2 up, grouped - "107.8", "1,760,034.2" - three figures under it, "0.312". |
| 519 | 5 | `String cityLine()` | THE CITY's size, the land's five areas but forest: "107.8 km² · 89.6 dry · 0 fresh · 18.2 sea" (spec-land 2.8). |
| 526 | 8 | `String worldLine()` | The world's line: "of the world's 1.12 Pt of iron, the city owns 0.0000452%" - its share of the world's tonnes, to three figures. |
| 536 | 4 | `String inToggle(double usdThousands)` | Thousands of US dollars in the money the toggle pays in: as listed from the vault, at today's rate converting. |
| 542 | 3 | `String perDryKm2(LandParcel p)` | An offer's price a dry km2, in the toggle's money: "D$5.6M"; "—" with no dry ground. |
| 547 | 4 | `LandParcel officeBest()` | The office's best offer, the shelf's first (BEST VALUE), or null with nothing listed. |
| 553 | 6 | `void chooseSide()` | The side shown before the player picks one: the office's best's, or the north. |
| 566 | 1 | **type** `record SideChip(int side, String name, String label, int listed, String best, boolean bestValue, String tip)` | One side's chip: the side, its name and the chip's words - the name, and since 0.7.69 " · N" when it lists fewer than six (spec-grid 2.5, "North · 4") - how many it lists, its cheapest dry ground a km2 in the toggle's... |
| 568 | 21 | `List<SideChip> sideChips()` |  |
| 591 | 1 | **type** `record DepositWords(String colour, String words, String tip)` | A deposit on a row: its resource's colour, its sites and amount - "3 · 38.4 Mt" - and the tooltip's line. |
| 603 | 3 | **type** `record Row(LandParcel parcel, String place, String km2, String sizeTip, double dry, double fresh, double se...` | One place as its row: its offer, its place (1 to 6), its size in km2 and the size's tooltip - its blocks and its ground (0.7.69, spec-grid 2.5) - its dry, fresh and sea shares for the bar, its deposits (the first ROW_... |
| 617 | 10 | `List<Row> sideRows(int side)` | A side's six places, 1 to 6: its offers, and a place waiting for room as its one muted line. |
| 629 | 6 | `Row emptyRow(int side, int at)` | A place waiting for room: its number, EMPTY_PLACE, and a tooltip naming it. |
| 642 | 10 | `static String sizeWords(LandParcel p)` | An offer's size, its blocks and its ground (0.7.69, spec-grid 2.5): "1 × 2 blocks of 120 m · 0.0288 km² · 0.0273 dry, 0.0015 fresh" - its blocks across and out, each part in its whole's unit (LandManager.partFigure())... |
| 653 | 38 | `Row row(LandParcel p, LandParcel office, LandParcel richest)` |  |
| 702 | 1 | **type** `record TagWords(String text, String colour)` | A tag's words and colour. |

### what the ground is worth (lines 704-847)

| line | len | member | says |
|---:|---:|---|---|
| 707 | 1 | **type** `record BarWords(String name, String figure, String colour, double share)` | One of a card's bars: its name, its figure, its colour and its share of the card's scale. |
| 717 | 2 | **type** `record Margin(BarWords world, BarWords investors, String chip, String chipTone, String lastMonth, String in...` | The Margin card: the world's price today and what investors pay, on one scale; the margin; what investors paid for ground last month (NationalAccounts.getLandSales() - LandManager's own month's flow is cleared at the ... |
| 729 | 7 | `String partsWords()` | THE WORLD'S PRICE, IN ITS PARTS (0.7.55): the founding's dollars, what US prices have done since, and the premium for the city's crowding - "US$7.53 × 1.14 US prices × 104 crowding: 5,684 people a km² of the city's la... |
| 738 | 4 | `static String times(double multiple)` | A multiple as the land office writes it: "×1.65", "×36.0", "×104". |
| 743 | 33 | `Margin margin()` |  |
| 778 | 1 | **type** `record OnTop(String name, double ground, double build, String words, String share)` | One stacked bar on the second card: the building, its ground and its build, and the ground's share. |
| 785 | 17 | `List<OnTop> onTop()` | On top of the build: a House's plot and a food plant's (the Bakery's) at what investors pay today, each against the building's own cash cost - "House: ground D$372k · build D$266k · 58%". |
| 810 | 1 | **type** `record Ore(String deposits, String tonnes, String mines, boolean undug, String popped, String oil)` | The Ore card: the deposits, the tonnes, the mines on them, whether ore lies undug, and the oil (0.7.61). |
| 812 | 14 | `Ore ore()` |  |
| 835 | 8 | `List<String> waitingNames()` | The sectors waiting on ground, by name: Game.getLandBlockedSectors(), as the sector pages and the inbox read it. |

### details (lines 848-862)

| line | len | member | says |
|---:|---:|---|---|
| 851 | 5 | `String spentLine()` | The line under the chart: what the city has paid the world for ground, and how much of it the vault paid. |

### THE DRAWING (lines 863-928)

| line | len | member | says |
|---:|---:|---|---|
| 874 | 28 | `HBox head(String sub)` | The head: "Land office" with the building area's swatch and the page's (i), and at the right the pay toggle with its line, and "‹ Build". |
| 904 | 24 | `VBox payToggle()` | The pay toggle, applied at once and saved with the city, and the line under it with its (i). |

### THE GROUND (lines 929-1035)

| line | len | member | says |
|---:|---:|---|---|
| 936 | 72 | `VBox groundPanel(Next next)` | THE GROUND: the strip and the big figures, then the bar and its scale - one panel, the shape of Construction's builders' gauge. |
| 1010 | 13 | `static HBox ghostSwatch(String colour, String name)` | A key's swatch for a ghost: tinted and outlined as the ghost is drawn. |
| 1025 | 10 | `private void scrollTo(javafx.scene.Node target)` | The page brought to a card: the menu's scroller set so it sits near the top. |

### BUY THE BEST N (0.7.13; "the next N" until 0.7.61) (lines 1036-1111)

| line | len | member | says |
|---:|---:|---|---|
| 1048 | 32 | `HBox nextPlots(Next next)` |  |
| 1082 | 12 | `HBox receiptLine(String receipt)` | This month's receipt, under ON OFFER: a pink tick and the receipt, wrapped rather than cut. |
| 1096 | 9 | `void buyOrFund(List<Integer> ids)` | Buys these plots the way the toggle pays, or - the city short - opens the funding page for them. |
| 1107 | 4 | `void rememberBeforeBuying()` | What a purchase is measured against when the page is drawn after it. |

### THE HERO ROW (0.7.61) (lines 1112-1348)

| line | len | member | says |
|---:|---:|---|---|
| 1150 | 12 | `HBox hero()` |  |
| 1164 | 5 | `void picked(int side, int place)` | The map picked a side, and an offer's place on it (-1 for none): its rows, the offer's lit. |
| 1171 | 15 | `VBox cityPanel()` | THE CITY, its sides, and the chosen side's offers. |
| 1188 | 32 | `javafx.scene.layout.FlowPane sideChipsNode()` | The four sides as Pieces' chips, each its cheapest dry ground a km2 after its name - and its count when it lists fewer than six - and BEST VALUE on the best's side. |
| 1222 | 10 | `VBox rowsNode(List<Row> rows, boolean turned)` | The rows: the column names, then the side's six places, 1 to 6; `turned`, their local prices pop. |
| 1234 | 6 | `private static Region column(Region cell, int c)` | A cell held to its column's width. |
| 1242 | 92 | `HBox rowNode(Row r, boolean turned)` | One offer's row: hover lights it on the map, a click picks it, its button buys it or opens the funding page; a place waiting for room, its muted line. |
| 1336 | 12 | `HBox emptyRowNode(Row r)` | A place waiting for room (spec-grid 2.5): its number, then EMPTY_PLACE across the rest of the row, muted. |

### WHAT THE GROUND IS WORTH (lines 1349-1523)

| line | len | member | says |
|---:|---:|---|---|
| 1352 | 10 | `javafx.scene.layout.GridPane worthCards()` | The four cards, a quarter of the page each. |
| 1364 | 17 | `VBox worthCard(String svg, String colour, String name, String info, javafx.scene.Node...body)` | A worth card's frame: its icon square, its name and its (i), then what it says. |
| 1387 | 14 | `VBox namedBar(BarWords w)` | One labelled bar on a worth card: its name and figure on a line, the bar under them. |
| 1402 | 22 | `VBox marginCard()` |  |
| 1425 | 25 | `VBox onTopCard()` |  |
| 1451 | 38 | `VBox oreCard()` |  |
| 1490 | 33 | `VBox waitingCard()` |  |

### details (lines 1524-1546)

| line | len | member | says |
|---:|---:|---|---|
| 1527 | 19 | `VBox details()` | "details ▸": the world's price of ground over the city's life, and what the city has paid for it. |

### WHEN THE CITY IS SHORT (0.7.13), the land office's own page since 0.7.26 (lines 1547-1831)

| line | len | member | says |
|---:|---:|---|---|
| 1576 | 1 | **type** `record Funding(String priceLine, String heldWords, String gapMoney, double held, double gap)` | What the funding page's card says: the price, what is held, the gap in money, and the bar's two parts. |
| 1578 | 22 | `Funding funding(List<Integer> ids)` |  |
| 1607 | 2 | **type** `record Offer(String name, String info, DebtQuote quote, String rate, String tone, String ending, Pieces.Pre...` | One loan on the funding page, worded - what Pieces.offerCard() draws: its name, its (i), its quote, the rate's words and colour, its ending, its button (a Pieces.Press since 0.7.34: the purchase, and the paper under i... |
| 1611 | 5 | `String buyWords(List<Integer> ids)` | What the funding page's buttons buy (0.7.34): "Buy · D$1.0B" for a plot, "Buy 5 plots · D$9.8B" for several, in the money the toggle pays in. |
| 1625 | 61 | `List<Offer> offers(List<Integer> ids)` | The loans the funding page offers for these plots, in the build page's order - the bond, then the note: converting, in local money for the cash gap; from the vault, in dollars on the world's curve for the vault's gap,... |
| 1687 | 79 | `void showLandFunding(List<Integer> ids)` |  |
| 1771 | 23 | `VBox topUpCard(List<Integer> ids, String here)` | The third way, from the vault: what the vault holds, the rest converted from cash - no debt. |
| 1796 | 3 | `Pieces.Press topUpPress(List<Integer> ids)` | The third way's button (0.7.34): the purchase, and what it takes - the 0.7.13 button's words, under it. |
| 1808 | 23 | `private void buyOnTheLoan(List<Integer> ids, String paper)` | The money is in: the plots are bought, each as its own button would buy it, and whatever the purchase answers is shown - the build screen's rule that a refusal nobody reads is a silent one (BuildScreen.goAhead()). |


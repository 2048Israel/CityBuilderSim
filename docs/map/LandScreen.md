# LandScreen.java - 1,772 lines · 73 methods · 19 constants · interface

`ham/citybuildersim/ui/LandScreen.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> The land office: whether the city has room to grow, and which ground to
> buy - since 0.7.61 round the city's map (batch J4; the project's
> spec-land.md 2.8): a head with how the city pays; the HERO ROW, the map
> (MapView, 600 x 400: the city's land and its forty offers hatched over the
> world, Expand to lay it over the whole window) and beside it THE CITY - its
> size in km2, its share of the world's iron, the four sides as chips and the
> chosen side's ten offers as rows, each its lane, its size, its ground and
> water as a bar, its deposits, its price, its price a dry km2 and Buy;
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
> 0.7.64 the ground free and the bar's figures too.
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

**Uses:** [Palette](Palette.md) (215), [Game](Game.md) (31), [LandParcel](LandParcel.md) (27), [Pieces](Pieces.md) (22), [CityLand](CityLand.md) (18), [LandManager](LandManager.md) (15), [LandMarket](LandMarket.md) (8), [Icons](Icons.md) (8), [Resource](Resource.md) (7), [LandMap](LandMap.md) (7), [MapView](MapView.md) (6), [UserInterface](UserInterface.md) (5), [DebtQuote](DebtQuote.md) (5), [CityNeeds](CityNeeds.md) (4), [Money](Money.md) (4), [TileRaster](TileRaster.md) (3), [World](World.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [ForeignAccounts](ForeignAccounts.md) (2), [SummaryScreen](SummaryScreen.md) (2), [Sector](Sector.md) (2), [HistoryScreen](HistoryScreen.md) (2), [Statement](Statement.md) (2), [Currency](Currency.md) (1), [BuildScreen](BuildScreen.md) (1), [BuildAdvice](BuildAdvice.md) (1), [HistorySave](HistorySave.md) (1), [Rollover](Rollover.md) (1)

**Used by (2):** [PeopleScreen](PeopleScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 82 | THE LAND OFFICE (0.7.26) |
| 126 | · what the page remembers between draws (0.7.26) |
| 236 | THE WORDS. Everything the office says, worked out without a node - |
| 292 | · the head |
| 332 | · the ground |
| 442 | · buy the best N |
| 505 | · THE CITY AND ITS SIDES (0.7.61) |
| 665 | · what the ground is worth |
| 809 | · details |
| 824 | THE DRAWING |
| 890 | · THE GROUND |
| 997 | · BUY THE BEST N (0.7.13; "the next N" until 0.7.61) |
| 1073 | · THE HERO ROW (0.7.61) |
| 1290 | · WHAT THE GROUND IS WORTH |
| 1465 | · details |
| 1488 | WHEN THE CITY IS SHORT (0.7.13), the land office's own page since 0.7.26 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 295 | `LandScreen.WHO_PAYS` | `"Investors build only on ground the city owns, and pay the city for each " + ...` | The page's (i): who pays for the ground, and who does not. |
| 304 | `LandScreen.PAY_TIPS` | `{ "Pay by converting cash", "Pay from the vault" }` | The toggle's tooltips: its 0.7.6 names, which the chips shortened. |
| 483 | `LandScreen.BEST_HEAD` | `"THE BEST OFFERS · cheapest dry ground first, from every side"` | The section's head over THE GROUND: the best N, from every side. |
| 598 | `LandScreen.ROW_DEPOSITS` | `2` | Deposits a row shows by name before "+N": 2... |
| 601 | `LandScreen.ROW_DEPOSIT_CHARS` | `20` | ...while their words run to no more than this many characters together, else one: 20 - "8 · 136 Mt" and "1 · 179 kt" and "+1" measured 152.8 px in the column's 158 at 9 px Plex Mono. |
| 660 | `LandScreen.WHOLE_FIELDS` | `" in the ground: every field of it centred in this offer, whole, paid for in ...` | What a row's tooltip says after each resource's sites and tonnes (0.7.64, batch L): an offer holds every field centred in its band whole, all its sites and tonnes, wherever its sites lie (CityLand) - Jerus: "whole iro... |
| 765 | `LandScreen.ON_TOP_INFO` | `"The ground is charged on top of the build, so a cheap building on" + " expen...` | The second card's (i), the 0.7.6 note word for word, and what the bars are. |
| 789 | `LandScreen.ORE_INFO` | `"A mine stands on one deposit, and every mine draws on the city's tonnes" + "...` | The Ore card's (i). |
| 806 | `LandScreen.WAITING_INFO` | `"As of last month — the sectors decide once a month, so ground bought" + " no...` | The Waiting card's (i), the 0.7.6 note word for word. |
| 819 | `LandScreen.DETAILS_INFO` | `"What the world asks for a square foot of ground, as each month recorded" + "...` | The chart's (i). |
| 1091 | `LandScreen.MAP_W` | `MapView.SMALL_W, HERO_H = MapView.SMALL_H` | The map's width and the hero row's height (spec-land 2.8). |
| 1094 | `LandScreen.HERO_GAP` | `16` | The gap between the map and THE CITY... |
| 1097 | `LandScreen.CITY_PANEL` | `657` | ...and THE CITY's width: what the content area's 1,273 px leave at 1,389 x 868 (spec-land 2.8's 657). |
| 1100 | `LandScreen.ROW_H` | `28` | A row's height: 28 px (spec-land 2.8), ten of them and THE CITY's head inside HERO_H. |
| 1103 | `LandScreen.ROW_COLUMNS` | `{ 24, 74, 64, 158, 82, 78, 76, 58 }` | The rows' columns, in pixels: lane, size, ground and water, deposits, price, a dry km2, tag, button - with the gaps, CITY_PANEL. |
| 1106 | `LandScreen.ROW_GAP` | `4` | The gap between a row's columns. |
| 1109 | `LandScreen.ROW_HEADS` | `{ "lane", "size", "dry · fresh · sea", "deposits", "price", "a dry km²", "", ...` | The rows' column names. |
| 1324 | `LandScreen.WORTH_CARD` | `"-fx-padding: 10 12 10 12; -fx-background-color: " + Palette.RAISED + ";" + "...` | A worth card's style, its edge's colour last. |
| 1543 | `LandScreen.ABROAD_INFO` | `"Issued abroad; the dollars are in reserve, and the " + "vault pays for the l...` | The dollar offers' (i), the 0.7.13 note word for word. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 78 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 121 | `int nextCount` | How many offers the best-N control buys; kept across redraws, held inside 1..listed. |
| 124 | `boolean detailsOpen` | Whether "details" is open; kept while the game runs, as the page's place is. |
| 138 | `private double freeBeforeBuying` | The ground free just before the purchase being drawn, or NaN: the solid segment grows from it. |
| 141 | `private int depositsBeforeBuying` | The deposits owned just before it, or -1: the Ore card pops the difference. |
| 144 | `private java.util.Set<Integer> lastShelf` | The plot ids on the shelf at the last draw, or null before the first. |
| 147 | `private int newAbove` | An offer whose id is above this is NEW (ids only grow: LandMarket's next id)... |
| 150 | `private int newMonth` | ...in this month only. |
| 153 | `private int shownMonth` | The month, the rate and the world's dollar price at the last draw: what the month's pop compares. |
| 154 | `private double shownRate` |  |
| 157 | `private Region waitingCard` | The waiting card as last drawn, for the strip's click to scroll to. |
| 160 | `private MapView map` | The map, the small view's and Expand's (MapView); made the first time the office is drawn, as the toolkit is up by then. |
| 163 | `int side` | The side whose ten offers the rows show (0 north, 1 east, 2 south, 3 west), -1 until the office's best decides it; and the offer picked on it, its lane, or -1. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 75 | 1698 | **type** `final class LandScreen` | The land office: whether the city has room to grow, and which ground to buy - since 0.7.61 round the city's map (batch J4; the project's spec-land.md 2.8): a head with how the city pays; the HERO ROW, the map (MapView... |
| 80 | 1 | `LandScreen(UserInterface ui)` |  |

### THE LAND OFFICE (0.7.26) (lines 82-125)

### what the page remembers between draws (0.7.26) (lines 126-235)

| line | len | member | says |
|---:|---:|---|---|
| 166 | 13 | `void forget()` | A new city or a load: nothing on its shelf is new to the player, and nothing has just been bought. |
| 180 | 30 | `void showLandMenu()` |  |
| 217 | 13 | `void noteShelf()` | NEW, worked out: when the shelf's ids differ from the last draw's, the plots above the last draw's highest id arrived since - the refill after a purchase (LandManager.buyParcel() refills the window at once). |
| 232 | 3 | `boolean isNew(LandParcel p)` | Whether this plot came onto the shelf since the player last saw it, this month. |

### THE WORDS. Everything the office says, worked out without a node - (lines 236-291)

| line | len | member | says |
|---:|---:|---|---|
| 243 | 1 | `String here()` | The city's money's mark: "D$". |
| 246 | 1 | `static String sqFt(double v)` | An area in square feet, compact to three figures: "324k sq ft", "9.68M sq ft", "8,000 sq ft" - what the office wrote its areas in until 0.7.64, now LandManager.km2Words()'s square kilometres. |
| 249 | 14 | `static String compact3(double v)` | A count to three significant figures past ten thousand - "324k", "9.68M", "13.7M" - and whole below. |
| 265 | 1 | `String local(double thousandsPerSqFt)` | A price a square foot in the city's money: "D$46.52". |
| 268 | 1 | `static String dollars(double thousandsPerSqFt)` | ...in the world's: "US$24.54". |
| 271 | 3 | `String signedLocal(double thousandsPerSqFt)` | A price a square foot with its direction: "+D$29.16", "−D$3.10". |
| 276 | 4 | `double houseSqFt()` | The House's plot, in square feet: what "room for N houses" divides by; 0 if the catalogue has none. |
| 282 | 6 | `String room(double sqFt)` | "room for 1,209 houses", or "" when there is no House to count in. |
| 290 | 1 | `static String plots(int n)` | Some plots, as the office's notes say them: "The plot", "5 plots". |

### the head (lines 292-331)

| line | len | member | says |
|---:|---:|---|---|
| 299 | 3 | `String[] payNames()` | The pay toggle's two chips: "Convert D$" and "From the vault". |
| 312 | 19 | `String[] payCaption()` | The line under the toggle, and the whole sentence behind its (i) - the "how" sentence (0.7.6; its vault half 0.7.13's), word for word: converting, "D$0.7072 per US$ · vault US$6.8M untouched"; from the vault, "vault h... |

### the ground (lines 332-441)

| line | len | member | says |
|---:|---:|---|---|
| 335 | 1 | **type** `record Cell(String label, String value, String note, String tone)` | One cell of THE GROUND's strip: its label, its figure, its line, and the figure's colour. |
| 344 | 23 | `Cell[] strip()` | The four cells. |
| 369 | 9 | `List<LandParcel> nextShelf(Next next)` | The offers "the best N" buys, in the shelf's order. |
| 386 | 11 | `String[] groundFigures(Next next)` | The big figures beside the strip: the ground free and what it holds, then what the best N would leave free - "0.0301 km² free", "room for 40 houses", "→ 3.49 km² free after the best 5", "room for 4,704 houses" (square... |
| 399 | 5 | `double groundScale(Next next)` | The bar's scale: the ground free and the best N together. |
| 411 | 17 | `List<Segment> groundSegments(Next next, java.util.function.IntFunction<Runnable> go)` | The bar's segments: the ground free, solid pink; then the best N, each a ghost in the light pink, numbered in the shelf's order, with a sand stripe where it holds ore, its side and lane, size and price on hover, and a... |
| 430 | 5 | `List<Tick> groundTicks()` | The bar's one tick: NEEDS YOU's line, the free ground under which the GROUND row is listed. |
| 437 | 4 | `String priceInToggle(LandParcel p)` | A plot's price in the money the toggle pays in: "D$149.7M" converting, "US$211.7M" from the vault. |

### buy the best N (lines 442-504)

| line | len | member | says |
|---:|---:|---|---|
| 453 | 2 | **type** `record Next(int n, int listed, List<Integer> ids, String total, String totalTone, String other, Pieces.Pres...` | What "Buy the best N" buys and says: how many, out of how many listed, their ids, the total in the toggle's money (neutral, red only when no way pays without debt - canAffordLandParcels()), the other money small, the ... |
| 456 | 25 | `Next next()` |  |
| 491 | 13 | `String offerInfo()` | The section's (i): the forty offers, ten a side, lane by lane; how the office ranks them (BEST VALUE, the best N); and the size every offer is a multiple of, worded as LandMarket draws it (a block for a new city, UNIT... |

### THE CITY AND ITS SIDES (0.7.61) (lines 505-664)

| line | len | member | says |
|---:|---:|---|---|
| 516 | 5 | `static String km2Figure(double km2)` | A size in km2 as THE CITY writes it: a decimal from 1 km2 up, grouped - "107.8", "1,760,034.2" - three figures under it, "0.312". |
| 523 | 5 | `String cityLine()` | THE CITY's size, the land's five areas but forest: "107.8 km² · 89.6 dry · 0 fresh · 18.2 sea" (spec-land 2.8). |
| 530 | 8 | `String worldLine()` | The world's line: "of the world's 1.12 Pt of iron, the city owns 0.0000452%" - its share of the world's tonnes, to three figures. |
| 540 | 4 | `String inToggle(double usdThousands)` | Thousands of US dollars in the money the toggle pays in: as listed from the vault, at today's rate converting. |
| 546 | 3 | `String perDryKm2(LandParcel p)` | An offer's price a dry km2, in the toggle's money: "D$5.6M"; "—" with no dry ground. |
| 551 | 4 | `LandParcel officeBest()` | The office's best offer, the shelf's first (BEST VALUE), or null with nothing listed. |
| 557 | 6 | `void chooseSide()` | The side shown before the player picks one: the office's best's, or the north. |
| 565 | 1 | **type** `record SideChip(int side, String name, String best, boolean bestValue)` | One side's chip: its name, its cheapest dry ground a km2 in the toggle's money, and whether it holds the office's best. |
| 567 | 14 | `List<SideChip> sideChips()` |  |
| 583 | 1 | **type** `record DepositWords(String colour, String words, String tip)` | A deposit on a row: its resource's colour, its sites and amount - "3 · 38.4 Mt" - and the tooltip's line. |
| 593 | 3 | **type** `record Row(LandParcel parcel, String lane, String km2, double dry, double fresh, double sea, List<DepositWo...` | One offer as its row: its lane (1 to 10), its size in km2, its dry, fresh and sea shares for the bar, its deposits (the first ROW_DEPOSITS, and "+N" for the rest), its price in the toggle's money and colour (red only ... |
| 604 | 7 | `List<Row> sideRows(int side)` | A side's ten offers, lane by lane. |
| 612 | 40 | `Row row(LandParcel p, LandParcel office, LandParcel richest)` |  |
| 663 | 1 | **type** `record TagWords(String text, String colour)` | A tag's words and colour. |

### what the ground is worth (lines 665-808)

| line | len | member | says |
|---:|---:|---|---|
| 668 | 1 | **type** `record BarWords(String name, String figure, String colour, double share)` | One of a card's bars: its name, its figure, its colour and its share of the card's scale. |
| 678 | 2 | **type** `record Margin(BarWords world, BarWords investors, String chip, String chipTone, String lastMonth, String in...` | The Margin card: the world's price today and what investors pay, on one scale; the margin; what investors paid for ground last month (NationalAccounts.getLandSales() - LandManager's own month's flow is cleared at the ... |
| 690 | 7 | `String partsWords()` | THE WORLD'S PRICE, IN ITS PARTS (0.7.55): the founding's dollars, what US prices have done since, and the premium for the city's crowding - "US$0.70 × 1.14 US prices × 104 crowding: 5,684 people a km² of the city's la... |
| 699 | 4 | `static String times(double multiple)` | A multiple as the land office writes it: "×1.65", "×36.0", "×104". |
| 704 | 33 | `Margin margin()` |  |
| 739 | 1 | **type** `record OnTop(String name, double ground, double build, String words, String share)` | One stacked bar on the second card: the building, its ground and its build, and the ground's share. |
| 746 | 17 | `List<OnTop> onTop()` | On top of the build: a House's plot and a food plant's (the Bakery's) at what investors pay today, each against the building's own cash cost - "House: ground D$372k · build D$266k · 58%". |
| 771 | 1 | **type** `record Ore(String deposits, String tonnes, String mines, boolean undug, String popped, String oil)` | The Ore card: the deposits, the tonnes, the mines on them, whether ore lies undug, and the oil (0.7.61). |
| 773 | 14 | `Ore ore()` |  |
| 796 | 8 | `List<String> waitingNames()` | The sectors waiting on ground, by name: Game.getLandBlockedSectors(), as the sector pages and the inbox read it. |

### details (lines 809-823)

| line | len | member | says |
|---:|---:|---|---|
| 812 | 5 | `String spentLine()` | The line under the chart: what the city has paid the world for ground, and how much of it the vault paid. |

### THE DRAWING (lines 824-889)

| line | len | member | says |
|---:|---:|---|---|
| 835 | 28 | `HBox head(String sub)` | The head: "Land office" with the building area's swatch and the page's (i), and at the right the pay toggle with its line, and "‹ Build". |
| 865 | 24 | `VBox payToggle()` | The pay toggle, applied at once and saved with the city, and the line under it with its (i). |

### THE GROUND (lines 890-996)

| line | len | member | says |
|---:|---:|---|---|
| 897 | 72 | `VBox groundPanel(Next next)` | THE GROUND: the strip and the big figures, then the bar and its scale - one panel, the shape of Construction's builders' gauge. |
| 971 | 13 | `static HBox ghostSwatch(String colour, String name)` | A key's swatch for a ghost: tinted and outlined as the ghost is drawn. |
| 986 | 10 | `private void scrollTo(javafx.scene.Node target)` | The page brought to a card: the menu's scroller set so it sits near the top. |

### BUY THE BEST N (0.7.13; "the next N" until 0.7.61) (lines 997-1072)

| line | len | member | says |
|---:|---:|---|---|
| 1009 | 32 | `HBox nextPlots(Next next)` |  |
| 1043 | 12 | `HBox receiptLine(String receipt)` | This month's receipt, under ON OFFER: a pink tick and the receipt, wrapped rather than cut. |
| 1057 | 9 | `void buyOrFund(List<Integer> ids)` | Buys these plots the way the toggle pays, or - the city short - opens the funding page for them. |
| 1068 | 4 | `void rememberBeforeBuying()` | What a purchase is measured against when the page is drawn after it. |

### THE HERO ROW (0.7.61) (lines 1073-1289)

| line | len | member | says |
|---:|---:|---|---|
| 1111 | 12 | `HBox hero()` |  |
| 1125 | 5 | `void picked(int side, int lane)` | The map picked a side, and an offer's lane on it (-1 for none): its rows, the offer's lit. |
| 1132 | 15 | `VBox cityPanel()` | THE CITY, its sides, and the chosen side's offers. |
| 1149 | 29 | `javafx.scene.layout.FlowPane sideChipsNode()` | The four sides as Pieces' chips, each its cheapest dry ground a km2 after its name and BEST VALUE on the best's side. |
| 1180 | 10 | `VBox rowsNode(List<Row> rows, boolean turned)` | The rows: the column names, then the side's offers, lane by lane; `turned`, their local prices pop. |
| 1192 | 6 | `private static Region column(Region cell, int c)` | A cell held to its column's width. |
| 1200 | 89 | `HBox rowNode(Row r, boolean turned)` | One offer's row: hover lights its band, a click picks it, its button buys it or opens the funding page. |

### WHAT THE GROUND IS WORTH (lines 1290-1464)

| line | len | member | says |
|---:|---:|---|---|
| 1293 | 10 | `javafx.scene.layout.GridPane worthCards()` | The four cards, a quarter of the page each. |
| 1305 | 17 | `VBox worthCard(String svg, String colour, String name, String info, javafx.scene.Node...body)` | A worth card's frame: its icon square, its name and its (i), then what it says. |
| 1328 | 14 | `VBox namedBar(BarWords w)` | One labelled bar on a worth card: its name and figure on a line, the bar under them. |
| 1343 | 22 | `VBox marginCard()` |  |
| 1366 | 25 | `VBox onTopCard()` |  |
| 1392 | 38 | `VBox oreCard()` |  |
| 1431 | 33 | `VBox waitingCard()` |  |

### details (lines 1465-1487)

| line | len | member | says |
|---:|---:|---|---|
| 1468 | 19 | `VBox details()` | "details ▸": the world's price of ground over the city's life, and what the city has paid for it. |

### WHEN THE CITY IS SHORT (0.7.13), the land office's own page since 0.7.26 (lines 1488-1772)

| line | len | member | says |
|---:|---:|---|---|
| 1517 | 1 | **type** `record Funding(String priceLine, String heldWords, String gapMoney, double held, double gap)` | What the funding page's card says: the price, what is held, the gap in money, and the bar's two parts. |
| 1519 | 22 | `Funding funding(List<Integer> ids)` |  |
| 1548 | 2 | **type** `record Offer(String name, String info, DebtQuote quote, String rate, String tone, String ending, Pieces.Pre...` | One loan on the funding page, worded - what Pieces.offerCard() draws: its name, its (i), its quote, the rate's words and colour, its ending, its button (a Pieces.Press since 0.7.34: the purchase, and the paper under i... |
| 1552 | 5 | `String buyWords(List<Integer> ids)` | What the funding page's buttons buy (0.7.34): "Buy · D$1.0B" for a plot, "Buy 5 plots · D$9.8B" for several, in the money the toggle pays in. |
| 1566 | 61 | `List<Offer> offers(List<Integer> ids)` | The loans the funding page offers for these plots, in the build page's order - the bond, then the note: converting, in local money for the cash gap; from the vault, in dollars on the world's curve for the vault's gap,... |
| 1628 | 79 | `void showLandFunding(List<Integer> ids)` |  |
| 1712 | 23 | `VBox topUpCard(List<Integer> ids, String here)` | The third way, from the vault: what the vault holds, the rest converted from cash - no debt. |
| 1737 | 3 | `Pieces.Press topUpPress(List<Integer> ids)` | The third way's button (0.7.34): the purchase, and what it takes - the 0.7.13 button's words, under it. |
| 1749 | 23 | `private void buyOnTheLoan(List<Integer> ids, String paper)` | The money is in: the plots are bought, each as its own button would buy it, and whatever the purchase answers is shown - the build screen's rule that a refusal nobody reads is a silent one (BuildScreen.goAhead()). |


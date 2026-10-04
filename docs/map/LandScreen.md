# LandScreen.java - 1,579 lines · 59 methods · 8 constants · interface

`ham/citybuildersim/ui/LandScreen.java` - generated 2026-10-04 by CodeMap; line numbers are as of that run.

> The land office: whether the city has room to grow, and which ground to
> buy. Since 0.7.26 in the style Build got in 0.7.24 and 0.7.25: a head with
> how the city pays; THE GROUND - the ground free now and the plots the next
> purchase would add, as one bar, with the position across its top; the nine
> plots on offer as a three-by-three of cards, cheapest ground a square foot
> first, each with its price, its value against the going rate and its tags;
> what the ground is worth to the city in four cards - the margin, the ground
> on top of the build, the ore, who is waiting; the price of ground over the
> city's life behind "details"; and, when the city is short, the funding page
> in the same frame.
> 
> BEST VALUE is the top-left card: the cheapest ground a square foot with any
> ore on it paid for inside its price (Game.landShelf()'s order) - Jerus's
> "the best one is always at the top left". It is not LandMarket.bestValue(),
> which skips ore and so sits further down the shelf; this screen never called
> it, though this comment said it did until 0.7.26. MOST ORE is
> LandMarket.richestDeposit(). The two are tags of their own, and one card can
> carry both.
> 
> In US dollars since 0.7.6: every plot shows its dollar price and what that
> costs in local money at today's rate, and a chip pair at the top chooses
> whether the treasury converts cash for it (the default) or pays out of the
> vault. Since 0.7.13 a plot's price is large in the money the toggle pays in,
> its size in square kilometres beside its square feet (the square feet to
> three figures since 0.7.26), its button stays live and, short, opens the
> funding page sized to the gap (showLandFunding()), and a control buys the
> next N plots at once (nextPlots()).
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
> groundFigures(), plot(), margin(), and the rest), so a probe without the
> toolkit can read every word on a played city; the drawing methods lay those
> out. Split out of UserInterface on 2026-09-18; the rail calls
> showLandMenu(), as do Build's LAND FREE and its no-land and no-deposit
> pages, NEEDS YOU's GROUND row, HOW THE CITY IS's GROUND USED and the
> inbox's landlock notice.

**Uses:** [Palette](Palette.md) (197), [Game](Game.md) (31), [Pieces](Pieces.md) (31), [LandParcel](LandParcel.md) (16), [LandManager](LandManager.md) (10), [Icons](Icons.md) (10), [LandMarket](LandMarket.md) (7), [UserInterface](UserInterface.md) (5), [BuildScreen](BuildScreen.md) (5), [DebtQuote](DebtQuote.md) (5), [CityNeeds](CityNeeds.md) (4), [Money](Money.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (2), [ForeignAccounts](ForeignAccounts.md) (2), [SummaryScreen](SummaryScreen.md) (2), [Sector](Sector.md) (2), [Statement](Statement.md) (2), [Currency](Currency.md) (1), [BuildAdvice](BuildAdvice.md) (1), [HistorySave](HistorySave.md) (1), [Rollover](Rollover.md) (1)

**Used by (2):** [PeopleScreen](PeopleScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 75 | THE LAND OFFICE (0.7.26) |
| 117 | · what the page remembers between draws (0.7.26) |
| 220 | THE WORDS. Everything the office says, worked out without a node - |
| 276 | · the head |
| 316 | · the ground |
| 423 | · buy the next N |
| 479 | · one plot |
| 569 | · what the ground is worth |
| 682 | · details |
| 697 | THE DRAWING |
| 763 | · THE GROUND |
| 869 | · BUY THE NEXT N PLOTS (0.7.13) |
| 945 | · ON OFFER: the shelf |
| 1105 | · WHAT THE GROUND IS WORTH |
| 1272 | · details |
| 1295 | WHEN THE CITY IS SHORT (0.7.13), the land office's own page since 0.7.26 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 279 | `LandScreen.WHO_PAYS` | `"Investors build only on ground the city owns, and pay the city for each " + ...` | The page's (i): who pays for the ground, and who does not. |
| 288 | `LandScreen.PAY_TIPS` | `{ "Pay by converting cash", "Pay from the vault" }` | The toggle's tooltips: its 0.7.6 names, which the chips shortened. |
| 643 | `LandScreen.ON_TOP_INFO` | `"The ground is charged on top of the build, so a cheap building on" + " expen...` | The second card's (i), the 0.7.6 note word for word, and what the bars are. |
| 664 | `LandScreen.ORE_INFO` | `"A mine stands on one deposit, and every mine draws on the city's tonnes" + "...` | The Ore card's (i). |
| 679 | `LandScreen.WAITING_INFO` | `"As of last month — the sectors decide once a month, so ground bought" + " no...` | The Waiting card's (i), the 0.7.6 note word for word. |
| 692 | `LandScreen.DETAILS_INFO` | `"What the world asks for a square foot of ground, as each month recorded" + "...` | The chart's (i). |
| 1139 | `LandScreen.WORTH_CARD` | `"-fx-padding: 10 12 10 12; -fx-background-color: " + Palette.RAISED + ";" + "...` | A worth card's style, its edge's colour last. |
| 1350 | `LandScreen.ABROAD_INFO` | `"Issued abroad; the dollars are in reserve, and the " + "vault pays for the l...` | The dollar offers' (i), the 0.7.13 note word for word. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 71 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 112 | `int nextCount` | How many plots the next-N control buys; kept across redraws, held inside 1..listed. |
| 115 | `boolean detailsOpen` | Whether "details" is open; kept while the game runs, as the page's place is. |
| 129 | `private double freeBeforeBuying` | The ground free just before the purchase being drawn, or NaN: the solid segment grows from it. |
| 132 | `private int depositsBeforeBuying` | The deposits owned just before it, or -1: the Ore card pops the difference. |
| 135 | `private java.util.Set<Integer> lastShelf` | The plot ids on the shelf at the last draw, or null before the first. |
| 138 | `private int newAbove` | A card whose id is above this is NEW (ids only grow: LandMarket's next id)... |
| 141 | `private int newMonth` | ...in this month only. |
| 144 | `private int shownMonth` | The month, the rate and the world's dollar price at the last draw: what the month's pop compares. |
| 145 | `private double shownRate` |  |
| 148 | `private final List<Region> shelfCards` | The cards and the waiting card as last drawn, for the bar's and the strip's clicks to scroll to. |
| 149 | `private Region waitingCard` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 68 | 1512 | **type** `final class LandScreen` | The land office: whether the city has room to grow, and which ground to buy. |
| 73 | 1 | `LandScreen(UserInterface ui)` |  |

### THE LAND OFFICE (0.7.26) (lines 75-116)

### what the page remembers between draws (0.7.26) (lines 117-219)

| line | len | member | says |
|---:|---:|---|---|
| 152 | 10 | `void forget()` | A new city or a load: nothing on its shelf is new to the player, and nothing has just been bought. |
| 163 | 31 | `void showLandMenu()` |  |
| 201 | 13 | `void noteShelf()` | NEW, worked out: when the shelf's ids differ from the last draw's, the plots above the last draw's highest id arrived since - the refill after a purchase (LandManager.buyParcel() refills the window at once). |
| 216 | 3 | `boolean isNew(LandParcel p)` | Whether this plot came onto the shelf since the player last saw it, this month. |

### THE WORDS. Everything the office says, worked out without a node - (lines 220-275)

| line | len | member | says |
|---:|---:|---|---|
| 227 | 1 | `String here()` | The city's money's mark: "D$". |
| 230 | 1 | `static String sqFt(double v)` | An area in square feet, compact to three figures: "324k sq ft", "9.68M sq ft", "8,000 sq ft". |
| 233 | 14 | `static String compact3(double v)` | A count to three significant figures past ten thousand - "324k", "9.68M", "13.7M" - and whole below. |
| 249 | 1 | `String local(double thousandsPerSqFt)` | A price a square foot in the city's money: "D$46.52". |
| 252 | 1 | `static String dollars(double thousandsPerSqFt)` | ...in the world's: "US$24.54". |
| 255 | 3 | `String signedLocal(double thousandsPerSqFt)` | A price a square foot with its direction: "+D$29.16", "−D$3.10". |
| 260 | 4 | `double houseSqFt()` | The House's plot, in square feet: what "room for N houses" divides by; 0 if the catalogue has none. |
| 266 | 6 | `String room(double sqFt)` | "room for 1,209 houses", or "" when there is no House to count in. |
| 274 | 1 | `static String plots(int n)` | Some plots, as the office's notes say them: "The plot", "5 plots". |

### the head (lines 276-315)

| line | len | member | says |
|---:|---:|---|---|
| 283 | 3 | `String[] payNames()` | The pay toggle's two chips: "Convert D$" and "From the vault". |
| 296 | 19 | `String[] payCaption()` | The line under the toggle, and the whole sentence behind its (i) - the "how" sentence (0.7.6; its vault half 0.7.13's), word for word: converting, "D$0.7072 per US$ · vault US$6.8M untouched"; from the vault, "vault h... |

### the ground (lines 316-422)

| line | len | member | says |
|---:|---:|---|---|
| 319 | 1 | **type** `record Cell(String label, String value, String note, String tone)` | One cell of THE GROUND's strip: its label, its figure, its line, and the figure's colour. |
| 328 | 22 | `Cell[] strip()` | The four cells. |
| 352 | 9 | `List<LandParcel> nextShelf(Next next)` | The plots "the next N" buys, in the shelf's order. |
| 368 | 11 | `String[] groundFigures(Next next)` | The big figures beside the strip: the ground free and what it holds, then what the next N would leave free - "324k sq ft free", "room for 40 houses", "→ 37.6M sq ft free after the next 5", "room for 4,704 houses". |
| 381 | 5 | `double groundScale(Next next)` | The bar's scale: the ground free and the next N together. |
| 392 | 17 | `List<Segment> groundSegments(Next next, java.util.function.IntFunction<Runnable> go)` | The bar's segments: the ground free, solid pink; then the next N, each a ghost in the light pink, numbered as its card is, with a sand stripe where it holds ore, its size and price on hover and a click to its card. |
| 411 | 5 | `List<Tick> groundTicks()` | The bar's one tick: NEEDS YOU's line, the free ground under which the GROUND row is listed. |
| 418 | 4 | `String priceInToggle(LandParcel p)` | A plot's price in the money the toggle pays in: "D$149.7M" converting, "US$211.7M" from the vault. |

### buy the next N (lines 423-478)

| line | len | member | says |
|---:|---:|---|---|
| 432 | 2 | **type** `record Next(int n, int listed, List<Integer> ids, String total, String totalTone, String other, Pieces.Pres...` | What "Buy the next N" buys and says: how many, out of how many listed, their ids, the total in the toggle's money (neutral, red only when no way pays without debt - canAffordLandParcels()), the other money small, the ... |
| 435 | 25 | `Next next()` |  |
| 468 | 10 | `String offerInfo()` | The section's (i): the floor under every plot, worded as LandMarket moves it (0.7.26: it said the office "stops splitting them at all as the city grows"; the floor rises a block for every BLOCKS_PER_FLOOR_STEP the cit... |

### one plot (lines 479-568)

| line | len | member | says |
|---:|---:|---|---|
| 482 | 1 | **type** `record TagWords(String text, String colour)` | A tag's words and colour. |
| 492 | 3 | **type** `record Plot(LandParcel parcel, int badge, String size, String caption, String price, String priceTone, Stri...` | Everything one card says: its badge (1..N in the next N, else 0), the size and its caption, the price in the toggle's money and its colour, the other money, the value bar's shares (its own dollars a square foot, the g... |
| 501 | 6 | `double valueScale()` | The value bars' scale: the dearest of the nine a square foot - or the going rate, or the world's price today, if either is higher, so both ticks are always on the bar. |
| 508 | 60 | `Plot plot(LandParcel p, int index, Next next)` |  |

### what the ground is worth (lines 569-681)

| line | len | member | says |
|---:|---:|---|---|
| 572 | 1 | **type** `record BarWords(String name, String figure, String colour, double share)` | One of a card's bars: its name, its figure, its colour and its share of the card's scale. |
| 582 | 1 | **type** `record Margin(BarWords world, BarWords investors, String chip, String chipTone, String lastMonth, String info)` | The Margin card: the world's price today and what investors pay, on one scale; the margin; what investors paid for ground last month (NationalAccounts.getLandSales() - LandManager's own month's flow is cleared at the ... |
| 584 | 31 | `Margin margin()` |  |
| 617 | 1 | **type** `record OnTop(String name, double ground, double build, String words, String share)` | One stacked bar on the second card: the building, its ground and its build, and the ground's share. |
| 624 | 17 | `List<OnTop> onTop()` | On top of the build: a House's plot and a food plant's (the Bakery's) at what investors pay today, each against the building's own cash cost - "House: ground D$372k · build D$266k · 58%". |
| 649 | 1 | **type** `record Ore(String deposits, String tonnes, String mines, boolean undug, String popped)` | The Ore card: the deposits, the tonnes, the mines on them, and whether ore lies undug. |
| 651 | 11 | `Ore ore()` |  |
| 669 | 8 | `List<String> waitingNames()` | The sectors waiting on ground, by name: Game.getLandBlockedSectors(), as the sector pages and the inbox read it. |

### details (lines 682-696)

| line | len | member | says |
|---:|---:|---|---|
| 685 | 5 | `String spentLine()` | The line under the chart: what the city has paid the world for ground, and how much of it the vault paid. |

### THE DRAWING (lines 697-762)

| line | len | member | says |
|---:|---:|---|---|
| 708 | 28 | `HBox head(String sub)` | The head: "Land office" with the building area's swatch and the page's (i), and at the right the pay toggle with its line, and "‹ Build". |
| 738 | 24 | `VBox payToggle()` | The pay toggle, applied at once and saved with the city, and the line under it with its (i). |

### THE GROUND (lines 763-868)

| line | len | member | says |
|---:|---:|---|---|
| 770 | 71 | `VBox groundPanel(Next next)` | THE GROUND: the strip and the big figures, then the bar and its scale - one panel, the shape of Construction's builders' gauge. |
| 843 | 13 | `static HBox ghostSwatch(String colour, String name)` | A key's swatch for a ghost: tinted and outlined as the ghost is drawn. |
| 858 | 10 | `private void scrollTo(javafx.scene.Node target)` | The page brought to a card: the menu's scroller set so it sits near the top. |

### BUY THE NEXT N PLOTS (0.7.13) (lines 869-944)

| line | len | member | says |
|---:|---:|---|---|
| 881 | 32 | `HBox nextPlots(Next next)` |  |
| 915 | 12 | `HBox receiptLine(String receipt)` | This month's receipt, under ON OFFER: a pink tick and the receipt, wrapped rather than cut. |
| 929 | 9 | `void buyOrFund(List<Integer> ids)` | Buys these plots the way the toggle pays, or - the city short - opens the funding page for them. |
| 940 | 4 | `void rememberBeforeBuying()` | What a purchase is measured against when the page is drawn after it. |

### ON OFFER: the shelf (lines 945-1104)

| line | len | member | says |
|---:|---:|---|---|
| 969 | 12 | `javafx.scene.layout.GridPane shelf(Next next)` |  |
| 1011 | 88 | `Region plotCard(Plot p, boolean turned)` | One plot, as a card: the land icon in its square (with its number when it is in the next N), then its size and what it holds, its price, and its value - its dollars a square foot as a bar, with the going rate's tick a... |
| 1101 | 3 | `private static void popText(javafx.scene.text.Text run)` | popPip()'s quarter second, for a run of text in a flow: its flow pops. |

### WHAT THE GROUND IS WORTH (lines 1105-1271)

| line | len | member | says |
|---:|---:|---|---|
| 1108 | 10 | `javafx.scene.layout.GridPane worthCards()` | The four cards, a quarter of the page each. |
| 1120 | 17 | `VBox worthCard(String svg, String colour, String name, String info, javafx.scene.Node...body)` | A worth card's frame: its icon square, its name and its (i), then what it says. |
| 1143 | 14 | `VBox namedBar(BarWords w)` | One labelled bar on a worth card: its name and figure on a line, the bar under them. |
| 1158 | 17 | `VBox marginCard()` |  |
| 1176 | 25 | `VBox onTopCard()` |  |
| 1202 | 35 | `VBox oreCard()` |  |
| 1238 | 33 | `VBox waitingCard()` |  |

### details (lines 1272-1294)

| line | len | member | says |
|---:|---:|---|---|
| 1275 | 19 | `VBox details()` | "details ▸": the world's price of ground over the city's life, and what the city has paid for it. |

### WHEN THE CITY IS SHORT (0.7.13), the land office's own page since 0.7.26 (lines 1295-1579)

| line | len | member | says |
|---:|---:|---|---|
| 1324 | 1 | **type** `record Funding(String priceLine, String heldWords, String gapMoney, double held, double gap)` | What the funding page's card says: the price, what is held, the gap in money, and the bar's two parts. |
| 1326 | 22 | `Funding funding(List<Integer> ids)` |  |
| 1355 | 2 | **type** `record Offer(String name, String info, DebtQuote quote, String rate, String tone, String ending, Pieces.Pre...` | One loan on the funding page, worded - what Pieces.offerCard() draws: its name, its (i), its quote, the rate's words and colour, its ending, its button (a Pieces.Press since 0.7.34: the purchase, and the paper under i... |
| 1359 | 5 | `String buyWords(List<Integer> ids)` | What the funding page's buttons buy (0.7.34): "Buy · D$1.0B" for a plot, "Buy 5 plots · D$9.8B" for several, in the money the toggle pays in. |
| 1373 | 61 | `List<Offer> offers(List<Integer> ids)` | The loans the funding page offers for these plots, in the build page's order - the bond, then the note: converting, in local money for the cash gap; from the vault, in dollars on the world's curve for the vault's gap,... |
| 1435 | 79 | `void showLandFunding(List<Integer> ids)` |  |
| 1519 | 23 | `VBox topUpCard(List<Integer> ids, String here)` | The third way, from the vault: what the vault holds, the rest converted from cash - no debt. |
| 1544 | 3 | `Pieces.Press topUpPress(List<Integer> ids)` | The third way's button (0.7.34): the purchase, and what it takes - the 0.7.13 button's words, under it. |
| 1556 | 23 | `private void buyOnTheLoan(List<Integer> ids, String paper)` | The money is in: the plots are bought, each as its own button would buy it, and whatever the purchase answers is shown - the build screen's rule that a refusal nobody reads is a silent one (BuildScreen.goAhead()). |


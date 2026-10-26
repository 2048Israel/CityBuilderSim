# ZoningScreen.java - 1,087 lines · 71 methods · 25 constants · interface

`ham/citybuildersim/ui/ZoningScreen.java` - generated 2026-10-26 by CodeMap; line numbers are as of that run.

> Policy › Zoning (0.7.109, batch Z2): the fifth Policy area - the city's
> ground and its zones on one page, a sector's zone on its own page of dial
> cards, and the forest law on a third - drawn in Policy's own style (the
> area chips, the dial cards and the staged tray, 0.7.36).
> 
> WHY. Z1 (0.7.107) built Jerus's zoning (2026-10-08, the project's
> spec-zoning.md: "in policy zoning will now exist ... you can allocate a %
> of total land or a specific km² for a specific industry, and even provide
> the land at a lower rate, or free") as a model with doors and a preview,
> and drew none of it. The zoning study drew three boards; these are they.
> PolicyScreen dispatches its Zoning area here, as Finances dispatches its
> fund to FundScreen, and keeps the frame, the staged set and the tray's
> register: every dial here is a PolicyScreen.Lever staged in policyStaged,
> so a page change throws it away and one press applies it.
> 
>   THE GROUND (board 1): the four figures (ZONED, HELD FOR THEM, PRICE GIVEN
>     UP, FOREST KEPT) across the frame; THE CITY'S GROUND as one bar of the
>     dry ground - built, each zone used solid and held hatched, the bare
>     ground and open forest free for anyone, the forest the law keeps - and
>     under it a closer bar of the zones and the bare ground; THE ZONES a
>     row each, a door to its page; THE FOREST LAW's bar with a door; and
>     "Zone ground for an industry".
>   A SECTOR'S ZONE (board 2): HOW MUCH GROUND (a share of the dry ground or
>     km²), ITS PRICE (0-100% of the going price) and ITS TAXES (the
>     sector's own three offsets - "one dial, two doors", Z5 - the same keys
>     as Policy › Taxes' rows), each before and after; the zone taken off.
>   THE FOREST LAW (board 3): its bar, its two shares, and what it would do
>     in the long run - the wood kept standing for ever, the most that may
>     be cut (Forestry's crews are I9's: what the law holds, not crews), the
>     forest a building may take.
> 
> EVERY FIGURE IS THE MODEL'S. They are ZoningView's (the hub, the rows, a
> page and the forest, each on the city's zoning or a staged copy),
> PolicyPreview's (the tax take, THE BUDGET, zoningTaxChange()) and
> LandManager's - ZoningViewCheck holds them; nothing here adds up a figure.

**Uses:** [Palette](Palette.md) (82), [PolicyScreen](PolicyScreen.md) (77), [ZoningView](ZoningView.md) (64), [Zoning](Zoning.md) (53), [LandManager](LandManager.md) (42), [Icons](Icons.md) (16), [Pieces](Pieces.md) (15), [PolicyPreview](PolicyPreview.md) (9), [Money](Money.md) (9), [TaxPolicy](TaxPolicy.md) (6), [Sector](Sector.md) (4), [Ladder](Ladder.md) (4), [BuildCard](BuildCard.md) (4), [Game](Game.md) (3), [UserInterface](UserInterface.md) (2), [Formats](Formats.md) (2), [Forestry](Forestry.md) (2), [Sectors](Sectors.md) (1), [EconomyManager](EconomyManager.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1)

**Used by (2):** [BuildScreen](BuildScreen.md), [PolicyScreen](PolicyScreen.md)

## Sections

| line | section |
|---:|---|
| 79 | THE PAGES |
| 179 | THE FOUR FIGURES (board 1's head): ZONED, HELD FOR THEM, PRICE GIVEN |
| 219 | THE COLOURS: the zones in the business violet and then the other |
| 306 | THE GROUND (board 1) |
| 523 | THE STAGED SET: the zone's and the law's keys, beside the taxes' |
| 703 | A SECTOR'S ZONE (board 2): HOW MUCH GROUND, ITS PRICE, ITS TAXES |
| 975 | THE FOREST LAW (board 3) |
| 1056 | THE HUB'S CARD (Policy's fifth area card) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 84 | `ZoningScreen.THE_GROUND` | `"The ground", THE_FOREST = "The forest law"` | The area's two fixed pages: the ground and its zones (the hub), and the forest law. |
| 87 | `ZoningScreen.PAGES` | `{ THE_GROUND, THE_FOREST }` | ...as the tabs list them, before each zone's own. |
| 90 | `ZoningScreen.PAGE_ICONS` | `{ Icons.ZONING, Icons.TREES }` | ...and their icons: the plot, the trees. |
| 93 | `ZoningScreen.ZONE_PAGE` | `"zone:"` | A sector's zone's page is this and its key: "zone:Agriculture". |
| 173 | `ZoningScreen.AREA_INFO` | `"Ground held for one industry: a share of the city's dry ground or a size in ...` | The area's (i). |
| 227 | `ZoningScreen.ZONE_COLOURS` | `{ Palette.BUSINESS, Palette.BUSINESS_LIGHT, Palette.BUSINESS_DARK, Palette.MO...` | The zones' colours, in the zones' order: the business ramp (a zone is a sector's), then the other categories. |
| 231 | `ZoningScreen.BUILT_COLOUR` | `Palette.RAMP_REST` | Built ground: the rest's grey - ground nobody can have. |
| 234 | `ZoningScreen.FREE_COLOUR` | `Palette.BUILDING` | Bare ground free for anyone: the land office's free ground, solid pink. |
| 237 | `ZoningScreen.FOREST_OPEN` | `"#9cc48a", FOREST_WORKING = "#6f9a5e", FOREST_PROTECTED = "#527c45"` | The forest's three, open to protected: two lighter steps of the map's forest green, and the map's own. |
| 311 | `ZoningScreen.GROUND_INFO` | `"The city's dry ground, all of it: what is built on; each zone's ground - bui...` | THE CITY'S GROUND's (i). |
| 318 | `ZoningScreen.ZONES_INFO` | `"A zone holds its size, less what its sector has built in it, out of the free...` | THE ZONES' (i). |
| 324 | `ZoningScreen.LAW_INFO` | `"Of the city's forest ground, a protected share is never cut nor built on and...` | THE FOREST LAW's (i) on the hub. |
| 369 | `ZoningScreen.PICK` | `"zoning:pick"` | The open line of the sector picker. |
| 541 | `ZoningScreen.PROTECTED_KEY` | `"forest:protected", KEPT_KEY = "forest:kept"` | The forest law's two staged keys: its protected share and its kept share. |
| 544 | `ZoningScreen.SHARE_STEP` | `.005` | A share of the dry ground's step: half a point. |
| 547 | `ZoningScreen.PRICE_STEP` | `.05` | A price share's step: five points of the going price, a twentieth (PolicyScreen's TUITION_STEP's). |
| 550 | `ZoningScreen.LAW_STEP` | `.01` | The forest law's step: a point of the forest. |
| 553 | `ZoningScreen.KM2_STEPS` | `200` | How many steps a km² dial is cut into across the city's dry ground, before its step is rounded to 1, 2 or 5 of a power of ten. |
| 668 | `ZoningScreen.BUDGET_INFO` | `PolicyScreen.BUDGET_INFO + " On a zone's page it adds the property tax its st...` | THE BUDGET's (i) on these pages. |
| 672 | `ZoningScreen.TRAY_INFO` | `"Everything staged on this page, applied together by one press: the zone's si...` | The tray's (i). |
| 708 | `ZoningScreen.SIZE_INFO` | `"A share of the city's dry ground - re-read as the city buys land, so 2% stay...` | HOW MUCH GROUND's (i). |
| 714 | `ZoningScreen.PRICE_INFO` | `"What the city sells the zone's ground at, as a share of the going business p...` | ITS PRICE's (i). |
| 719 | `ZoningScreen.TAXES_INFO` | `"A zone's taxes are its sector's own rates: the city's rate and the sector's ...` | ITS TAXES' (i). |
| 724 | `ZoningScreen.GROUND_CAVEAT` | `"Ground as it stands: what the city buys next widens a share."` | The caveat a zone's ground owes: amounts, not places. |
| 980 | `ZoningScreen.FOREST_INFO` | `LAW_INFO + " What it would do is the long run: the protected share of the for...` | The law's (i) on its page. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 71 | `private final PolicyScreen policy` |  |
| 72 | `private final UserInterface ui` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 69 | 1019 | **type** `final class ZoningScreen` | Policy › Zoning (0.7.109, batch Z2): the fifth Policy area - the city's ground and its zones on one page, a sector's zone on its own page of dial cards, and the forest law on a third - drawn in Policy's own style (the... |
| 74 | 4 | `ZoningScreen(PolicyScreen policy, UserInterface ui)` |  |

### THE PAGES (lines 79-178)

| line | len | member | says |
|---:|---:|---|---|
| 95 | 1 | `static String zonePage(String sector)` |  |
| 98 | 3 | `static String sectorOf(String page)` | The sector a page is the zone of, or null for the two fixed pages. |
| 103 | 5 | `boolean knows(String page)` | Whether a page is one of this area's: the two, or a sector's (zoned or about to be). |
| 110 | 4 | `String nameOf(String sector)` | A sector's name as the screens write it. |
| 116 | 1 | `static String possessive(String name)` | "Agriculture's", "Restaurants'". |
| 119 | 6 | `List<String> tabPages()` | The tabs: the two pages, then each zone in the zones' order - and the open sector's, a zone not yet applied. |
| 127 | 4 | `String tabName(String page)` | ...as their chips read: a page's name, a zone's sector's. |
| 133 | 14 | `FlowPane tabs()` | The tab strip under the head. |
| 149 | 6 | `void page(VBox page)` | The page open, drawn into `page`. |
| 157 | 14 | `HBox head(HBox head)` | The breadcrumb on a zone's page: "Policy › Zoning › Agriculture", "Zoning" a way back to the ground. |

### THE FOUR FIGURES (board 1's head): ZONED, HELD FOR THEM, PRICE GIVEN (lines 179-218)

| line | len | member | says |
|---:|---:|---|---|
| 185 | 20 | `List<Cell> vitalCells()` | The four, worked out without drawing them (pure: the probe reads them). |
| 207 | 11 | `HBox vitals()` | The strip: the four as limit cells, each a door. |

### THE COLOURS: the zones in the business violet and then the other (lines 219-305)

| line | len | member | says |
|---:|---:|---|---|
| 240 | 7 | `String zoneColour(String sector)` | A zone's colour, by its place in the zones' order. |
| 249 | 11 | `String partName(ZoningView.Part p)` | A bar part's name, as its key and its tooltip say it. |
| 262 | 10 | `String partColour(ZoningView.Part p)` | ...its colour. |
| 274 | 11 | `static String partWhat(ZoningView.Part p)` | ...its tooltip's second line. |
| 287 | 18 | `Pieces.CauseBar groundBar(List<ZoningView.Part> parts, double band)` | The bar: a part a stretch, held ground hatched, a click on a zone's part its page. |

### THE GROUND (board 1) (lines 306-522)

| line | len | member | says |
|---:|---:|---|---|
| 328 | 39 | `void groundPage(VBox page)` |  |
| 372 | 15 | `VBox picker()` | "Zone ground for an industry": every sector with no zone, a chip each, opening its page. |
| 389 | 3 | `static String sizeWords(Zoning.Size kind, double size)` | A zone's size in words: "2.50% of the dry ground", "1.25 km²". |
| 394 | 5 | `static String rateChip(Zoning.Tax tax, double rate, double offset)` | A rate chip's words: "profit 15.00%", "sales 13.00% (−2.00 pts)". |
| 401 | 62 | `HBox zoneRow(ZoningView.Row r)` | One zone: its sector, its size and share, used against held, its price, its three rates, its relief, a door. |
| 465 | 4 | `static String usedWords(ZoningView.Row r)` | "built 0.4 · holds 1.6 km²", and "of 4.0 it wants" when it is short. |
| 471 | 10 | `VBox lawCard(ZoningView.Forest f, Zoning z)` | The forest law's card: the forest as a bar of its kinds, cleared ground grey, and what the law means. |
| 483 | 4 | `static String lawTitle(Zoning z)` | "NO FOREST LAW", "10% PROTECTED · 25% KEPT FROM BUILDING". |
| 489 | 10 | `static String lawWords(ZoningView.Forest f, Zoning z)` | What a law means, in one sentence. |
| 501 | 6 | `static String m3(double m3)` | Cubic metres of wood: "13.7k m³", "1.2M m³". |
| 509 | 13 | `Pieces.CauseBar forestBar(ZoningView.Forest f, double band)` | The forest as a bar: protected, working, the open forest standing, and the cleared ground. |

### THE STAGED SET: the zone's and the law's keys, beside the taxes' (lines 523-702)

| line | len | member | says |
|---:|---:|---|---|
| 536 | 1 | `static String sizeKey(String s)` |  |
| 537 | 1 | `static String unitKey(String s)` |  |
| 538 | 1 | `static String priceKey(String s)` |  |
| 539 | 1 | `static String offKey(String s)` |  |
| 556 | 1 | `double dryKm2()` | The dry ground that is room, km² (ET5: the linked ground, which a share zone is a share of; the whole dry ground while nothing is unlinked). |
| 559 | 6 | `double km2Step()` | A km² dial's step: the dry ground over KM2_STEPS, down to 1, 2 or 5 of a power of ten. |
| 567 | 4 | `double km2Top()` | ...and its top: the dry ground, up to a whole step. |
| 573 | 1 | `Zoning.Size kindNow(String s)` | A zone's settings as the city has them - a sector with no zone: a share, nothing, at the going price. |
| 574 | 1 | `double sizeNow(String s)` |  |
| 575 | 1 | `double priceNow(String s)` |  |
| 578 | 1 | `double sizeAs(String s, Zoning.Size kind)` | A zone's size as the city has it, said in a unit: what its size dial reads as current in the unit staged - and what an unstaged size is, the unit staged. |
| 581 | 3 | `double value(String k, double current, String key, double v)` | A key's value as staged, or a value of the caller's for one key (a thumb while it is dragged). |
| 585 | 1 | `Zoning.Size kindOf(double unit)` |  |
| 587 | 1 | `static double unitOf(Zoning.Size kind)` |  |
| 590 | 5 | `double convert(double size, Zoning.Size from, Zoning.Size to)` | A size in one unit, said in the other: a share of the dry ground as km², or km² as a share. |
| 597 | 12 | `Set<String> stagedSectors(String extra)` | The sectors with anything of their zone staged, and `extra`. |
| 611 | 1 | `Zoning stagedZoning()` | The city's zoning with everything staged put through a copy (ZoningView.with(), without(), withLaw()). |
| 614 | 27 | `Zoning stagedZoning(String key, double v)` | ...and one key at a value of the caller's on top. |
| 643 | 7 | `void applyZone(String s)` | A zone as staged, through its door: Game.setZone() with its three as staged - nothing for a new zone of no size, nothing when it is to come off. |
| 652 | 4 | `void applyLaw()` | The law as staged, through its door. |
| 658 | 8 | `Effect budget(String key, double v)` | THE BUDGET under everything staged - the taxes through the staged policy, the zones' relief through the staged zoning (PolicyPreview.zoningTaxChange()) - with one key at a thumb's value. |
| 681 | 18 | `Region tray()` | The tray (Policy's, 0.7.36): a chip a change, THE BUDGET before and after the set, "Apply it" or "Apply all N", and "Leave it as it is". |
| 701 | 1 | `static String applyWords(int count)` | "Apply it", "Apply all 3". |

### A SECTOR'S ZONE (board 2): HOW MUCH GROUND, ITS PRICE, ITS TAXES (lines 703-974)

| line | len | member | says |
|---:|---:|---|---|
| 726 | 23 | `void zonePage(VBox page, String s)` |  |
| 751 | 14 | `Node offControl(String s, String name, boolean off)` | "Take the zone off" - staged, its chip in the tray - or, staged, "Keep it". |
| 767 | 23 | `VBox sizeCard(String s, String name, Sector sector)` | HOW MUCH GROUND: the unit's two chips over the dial, staged before → after. |
| 797 | 14 | `Label unitChip(String s, String words, Zoning.Size to, Zoning.Size kind)` | One unit's chip: lit when it is the unit; a click stages the other - the zone's own ground said in it, or a staged size carried over, snapped to the new unit's step (an unstaged size is the city's, in whichever unit i... |
| 813 | 18 | `java.util.function.DoubleFunction<List<Effect>> sizeEffects(String s, ZoningView.Page now)` | HOW MUCH GROUND's effects at any value of its thumb (pure: the probe reads them): what it carries, what it holds, the ground left free for anyone, its ground at the going price. |
| 833 | 12 | `VBox priceCard(String s, String name)` | ITS PRICE: 0-100% of the going price a m², and what it gives up. |
| 847 | 16 | `java.util.function.DoubleFunction<List<Effect>> priceEffects(String s, ZoningView.Page now)` | ITS PRICE's effects at any value of its thumb (pure: the probe reads them): a plot's ground, the price given up on what it holds, the tax relief, THE BUDGET. |
| 865 | 1 | **type** `record Dial(Node words, String key, Ladder ladder, double at)` | One dial of a card of several: its words, its key, its ladder (unbuilt) and where its thumb sits. |
| 868 | 38 | `static VBox multiDialCard(String svg, String title, String info, String status, List<Dial> dials, BiFunction<String, Double, Li...` | A dial card of several dials (ITS TAXES, the forest law): their words and ladders down the left, what they would do at the right, following whichever thumb moves. |
| 908 | 7 | `static String taxKey(Zoning.Tax t, String s)` | A tax row's key, as Policy › Taxes' rows have it. |
| 917 | 4 | `static String taxName(Zoning.Tax t)` | "Profit", "Sales", "Property". |
| 923 | 5 | `static String taxWords(Zoning.Tax t, double city, double offset, double rate, double then, double paid)` | A tax row's words over its dial: "PROFIT · the city's 15.00% −2.00 pts → 13.00% · $1.2M last month". |
| 930 | 39 | `VBox taxesCard(String s, String name)` | ITS TAXES: the sector's three offsets, as Policy › Taxes' rows, the same keys. |
| 971 | 3 | `static String sameDials(String name)` | The study's line: "The same dials as Policy › Taxes: a move here moves it for every Agriculture building." |

### THE FOREST LAW (board 3) (lines 975-1055)

| line | len | member | says |
|---:|---:|---|---|
| 984 | 40 | `void forestPage(VBox page)` |  |
| 1031 | 9 | `static String crewRuleWords(BuildCard.CrewRule rule, BuildingsTemplate smallest)` | The crew rule in a line (0.7.142; spec-zoning board 3: "room for 1 more crew now; the forest carries 7"): a crew is added only while the forest Forestry may cut lasts FOREST_CREW_MONTHS of every crew's cut - BuildCard... |
| 1042 | 13 | `List<Effect> forestEffects(ZoningView.Forest now, String key, double v)` | WHAT IT WOULD DO at any value of either thumb (pure: the probe reads them): the wood kept standing for ever, the most that may be cut, the forest a building may take, the ground free for anyone. |

### THE HUB'S CARD (Policy's fifth area card) (lines 1056-1087)

| line | len | member | says |
|---:|---:|---|---|
| 1061 | 14 | `VBox areaCard()` | Policy's ZONING area card: the zoned ground, the dry ground as slices, the zones' line and the law. |
| 1077 | 8 | `static SegmentBar areaSlices(List<Slice> slices)` | Area slices as one bar, a part's name and ground its tooltip. |
| 1086 | 1 | `static String pct0(double share)` |  |


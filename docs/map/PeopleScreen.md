# PeopleScreen.java - 4,794 lines · 167 methods · 27 constants · interface

`ham/citybuildersim/ui/PeopleScreen.java` - generated 2026-11-10 by CodeMap; line numbers are as of that run.

> The People tab and its second page, Household money.
> 
> Since 0.7.27 in the style Build got in 0.7.24 and 0.7.25 and the land
> office in 0.7.26 (Jerus, on the screens not yet redone: "the others are
> still full of text and the design could be more intuitive and fun").
> People is one scrolling page that leads with pictures - the age pyramid
> with a settled city's shape as a ghost, the month as a waterfall, why
> people come as a bridge, care as Build's rings, the homes as a gauge, the
> households as a mosaic of who lives in them, the people outside the
> families as tiles, and the skill ladder as bars - with every table behind
> "details" and every paragraph behind an (i). Household money is a page of
> its own, joined to People by a two-chip strip in the head: the grid of
> household cells with the open cell's books beside it, the city's month as
> a waterfall, and what the households have put by.
> 
> Nothing the old page printed is gone: the design study's inventory (the
> project's spec-people-0727.md, section 1) says where each figure and each
> paragraph went. What the page says is worked out in methods that make no
> node - peopleCells(), homes(), bridge(), careLine(), outsideTiles(),
> ladder(), verdict(), cityMonth() and the rest - so a probe can read every
> word on a played city without the toolkit; the drawing methods lay them
> out.
> 
> Split out of UserInterface on 2026-09-18. The panels the grid opens into
> (THE MONEY BLOCKS BOTH PANELS SHARE) and the tier table's bar are as they
> were, but for the fees named apart and the rows below the rule measured
> against a basket (0.7.27). The page reveals the open cell itself now:
> revealTop and revealBottom are its own, read by its own scroll.
> 
> Since 0.8.26 (study UP4, docs/studies/spec-screens-in-place.md) the People
> page is kept between redraws, the study's pilot: drawPage() runs through
> Kept over the nodes it made last month (UserInterface.keepMenu()), so a
> month that lands sets its figures on the page on show instead of building
> another. KeptCheck holds the kept page to one built new. Household money is
> drawn as before.

**Uses:** [Palette](Palette.md) (523), [Kept](Kept.md) (202), [AgeBand](AgeBand.md) (64), [Pieces](Pieces.md) (42), [Household](Household.md) (33), [PayTier](PayTier.md) (29), [HouseholdAccounts](HouseholdAccounts.md) (27), [WageBand](WageBand.md) (23), [Migration](Migration.md) (22), [FamilyStructure](FamilyStructure.md) (21), [HouseholdBalance](HouseholdBalance.md) (20), [FamilyModel](FamilyModel.md) (20), [BuildAdvice](BuildAdvice.md) (17), [CareType](CareType.md) (13), [Icons](Icons.md) (11), [PopulationManager](PopulationManager.md) (10), [Healthcare](Healthcare.md) (10), [UnemployedHousehold](UnemployedHousehold.md) (8), [PopulationCohorts](PopulationCohorts.md) (7), [Sickness](Sickness.md) (7), [JobType](JobType.md) (7), [LabourMarket](LabourMarket.md) (6), [EducationType](EducationType.md) (6), [Unemployment](Unemployment.md) (5), [Equity](Equity.md) (5), [BuildingManager](BuildingManager.md) (4), [Health](Health.md) (4), [Statement](Statement.md) (4), [LandScreen](LandScreen.md) (3), [UserInterface](UserInterface.md) (2)... and 15 more

**Used by (4):** [KeptBench](KeptBench.md), [SectorScreen](SectorScreen.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 64 | PEOPLE (0.7.27) |
| 153 | · what the page remembers between draws |
| 300 | THE WORDS. Everything the page says, worked out without a node, each |
| 376 | · homes |
| 442 | · the month |
| 580 | · why they come |
| 684 | · care |
| 785 | · outside the families |
| 880 | · work |
| 952 | THE DRAWING |
| 1055 | · the vitals |
| 1106 | · the pyramid |
| 1238 | · the month |
| 1439 | · why they come |
| 1569 | · care |
| 1641 | · homes |
| 1691 | · households |
| 2183 | · outside the families |
| 2362 | · work |
| 2547 | THE TABLES BEHIND "details", and the pieces they share with the |
| 2895 | HOUSEHOLD MONEY: CAN THE PEOPLE OF THIS CITY AFFORD TO LIVE IN IT? |
| 3156 | HOUSEHOLD MONEY, DRAWN |
| 3807 | · · the retired |
| 3824 | · · and outside the families |
| 4063 | THE MONEY BLOCKS BOTH PANELS SHARE |
| 4467 | · · the month |
| 4684 | THE TIER TABLE, AS A TABLE. |
| 4790 | · Small helpers so the report screens stay readable. Shared by the sector |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 122 | `PeopleScreen.OUT_OF_WORK_SHORT` | `.03` | Under this share out of work, nobody is spare: amber, "jobs going unfilled". |
| 125 | `PeopleScreen.OUT_OF_WORK_HIGH` | `.07` | Over this share out of work, high: amber (Toronto runs 8.0% and Madrid 9.0%, the high end of the big rich-country cities). |
| 128 | `PeopleScreen.OUT_OF_WORK_FAR` | `Migration.ARRIVALS_STOP_OUT_OF_WORK` | Over this, a city in trouble: red, and the share at which nobody moves here for work (Migration.ARRIVALS_STOP_OUT_OF_WORK). |
| 138 | `PeopleScreen.GOING_SHORT_WARN` | `.01` | Over this share of the city eating less than a basket, GOING SHORT is amber; the dashboard's HUNGRY reads it too (0.7.27). |
| 141 | `PeopleScreen.GOING_SHORT_BAD` | `.10` | ...and over this, red. |
| 267 | `PeopleScreen.COUNT_MILLIS` | `600` | How long the headcount counts and the month's bars grow when a month lands (Jerus's "fun": the number moves). |
| 291 | `PeopleScreen.STILL_FAKED` | `"What this model does not do yet:\n" + "· Nobody has an age.Each band holds a...` | What the model does not do yet: the four lines of the old page's eight that are still true (0.7.27). |
| 532 | `PeopleScreen.BORN` | `Palette.PEOPLE_LIGHT, MOVED_IN = Palette.PEOPLE` | The born and the arrived: the people teal's light step and the teal. |
| 539 | `PeopleScreen.DIED_OF_AGE` | `Palette.PEOPLE_DARK, DIED_OF_ILLNESS = Palette.BUSINESS, DIED_KILLED = Palett...` | The dead by cause: of age the dark teal, of illness the violet, the killed the pink, the aged out a light blue, and since R0b the causes of 1900 (typhoid, TB, childbirth and the rest) CATEGORIES' sand, the fifth colou... |
| 543 | `PeopleScreen.LEFT_WORK` | `Palette.PEOPLE_DARK, LEFT_BROKE = Palette.MONEY, LEFT_CRIME = Palette.BUILDING` | The leavers by why: for want of work the dark teal, broke the blue, driven out by crime the pink. |
| 965 | `PeopleScreen.CARD` | `"-fx-padding: 10 12 10 12; -fx-background-color: " + Palette.RAISED + ";" + "...` | The Build card ground every card on both pages stands on. |
| 1109 | `PeopleScreen.PYRAMID_HALF` | `200` | How wide half of the widest band is drawn, at most. |
| 1442 | `PeopleScreen.WHY_INFO` | `"The city draws people the way Migration strikes it each month: every post " ...` | WHY PEOPLE COME's (i): what the bridge is. |
| 1836 | `PeopleScreen.TILE_MIN` | `56` | The narrowest a tile is drawn; anything smaller folds into "+n more". |
| 1931 | `PeopleScreen.Strip.UNUSED` | `" "` | What the "+n more" label says while nothing folds: a space, never nothing (PT1-M4). |
| 2002 | `PeopleScreen.Strip.TILE_TALL` | `62` | How tall a strip is. |
| 2186 | `PeopleScreen.OUTSIDE_INFO` | `"Families are built from the adults who work, and their children." + "Everybo...` | OUTSIDE THE FAMILIES' (i): who they are, the old page's words. |
| 2463 | `PeopleScreen.LADDER_INFO` | `"open = posts this band can be put into(licensed posts are on the " + "chips ...` | The ladder's (i): the old legend. |
| 2952 | `PeopleScreen.IN_DOLLARS` | `"Every figure on this page is in dollars, not the thousands the rest of the "...` | The page's (i): what its money is counted in. |
| 2957 | `PeopleScreen.PAID_IN_ORDER` | `"The order is fixed: the payslip first, then rent, then the fees, and " + "th...` | The order a household pays in, and what happens when it comes up short: the verdict's (i). |
| 3161 | `PeopleScreen.BESIDE` | `1200` | The narrowest page that sets the open cell's books beside the grid; under it they stack. |
| 3523 | `PeopleScreen.CARS_INFO` | `"Bought out of savings past the same cushion a share is, so income " + "decid...` | The cars' (i): the old note. |
| 3595 | `PeopleScreen.BAR_ROOM` | `new javafx.geometry.Insets(8, 0, 0, 0)` | The room over the flow bar in a cell's statement: one object (KP2b), set where it is not that already (Pieces.vMargin). |
| 3598 | `PeopleScreen.TIER_COL` | `88` | How wide a tier column is. |
| 3599 | `PeopleScreen.SHAPE_COL` | `168` |  |
| 3606 | `PeopleScreen.MATRIX_COLUMNS` | `"people.matrixColumns"` | The key a matrix grid keeps its columns under - the household's name, then the six tiers - made once in the grid's life (KP2b) and added to it at each draw as one list. |
| 3783 | `PeopleScreen.BELOW_THE_RULE` | `"Below the rule: the retired, on a pension and no wage; the out of work, " + ...` | The section captions' (i): who is below the rule, and what their one cell says. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 60 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 160 | `private int shownMonth` | The month at the last draw of the page being watched, or -1: a month landing on it counts LIVING HERE up. |
| 163 | `boolean yearView` | Whether the month's waterfall shows the last twelve months, from the history, instead. |
| 166 | `final java.util.Set<String> detailsOpen` | Which "details" are open, by name; kept while the game runs, as the page's place is. |
| 169 | `private Region monthCard, homesCard, workCard` | The cards the vitals' doors scroll to, as last drawn. |
| 172 | `final Kept.Pool pool` | The People page's kept nodes (Kept; study UP4): the page is drawn over them on each redraw. |
| 178 | `final Kept.Pool moneyPool` | Household money's kept nodes (KP7): a pool of its own, since the two pages are two screens to keepMenu() - an arrival at either forgets the pool it draws from, and People's nodes are never handed to Household money's. |
| 1198 | `private double share, settled, widest` |  |
| 1199 | `private final Region bar` |  |
| 1916 | `private List<Piece> pieces` |  |
| 1917 | `private double total, widest` |  |
| 1919 | `private double[] counts` | The households' counts at the last set(), or null before the first. |
| 1920 | `private final List<TileNodes> tiles` |  |
| 1921 | `private final VBox more` |  |
| 1922 | `private final Label moreLabel` |  |
| 1923 | `private final Tooltip moreTip` |  |
| 2079 | `final String shape` |  |
| 2080 | `final VBox box` |  |
| 2081 | `final Label name` |  |
| 2082 | `final Tooltip tip` |  |
| 2084 | `final HBox ramp` | Under a family's tile, else null. |
| 2510 | `private double open, queue, scale` |  |
| 2511 | `private final Region q` |  |
| 2893 | `boolean householdPerFamily` | Whether the per-tier table shows one family or the whole city. |
| 2942 | `String openCell` | Which cell of the grid is open, as "tier:shape", or "OUT:" and a ledger's key. |
| 2945 | `boolean revealOpened` | Set by a click that opens a cell, so the panel it opened is brought into view. |
| 2948 | `javafx.scene.Node revealTop` | What this draw should bring into view, once, when the books stack under the grid: the grid's top and the panel's foot. |
| 2949 | `javafx.scene.Node revealBottom` |  |
| 3164 | `private Books books` | The books' pane as last drawn, for the scroll to hold the panel at the top of the view. |
| 3167 | `private boolean booksHooked` | Whether the scroll listener is on the menu's scroller yet: once, for the window's life. |
| 3331 | `private VBox grid` |  |
| 3332 | `private javafx.scene.Node panel` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 57 | 4738 | **type** `final class PeopleScreen` | The People tab and its second page, Household money. |
| 62 | 1 | `PeopleScreen(UserInterface ui)` |  |

### PEOPLE (0.7.27) (lines 64-152)

| line | len | member | says |
|---:|---:|---|---|
| 131 | 5 | `static String outOfWorkTone(double jobless)` | The out-of-work rate's verdict: red far too high, amber high or short of hands, green between. |
| 144 | 3 | `static String goingShortTone(double hunger)` | GOING SHORT's verdict, on the share of people eating less than a basket (HouseholdBalance.getHungerRate()). |
| 149 | 3 | `static String sickTone(double sick)` | OFF SICK's verdict: red over 12% of the workforce, amber over 6%. |

### what the page remembers between draws (lines 153-299)

| line | len | member | says |
|---:|---:|---|---|
| 181 | 6 | `void forget()` | A new city or a load: nothing has just landed, and nothing of the last city's pages is kept. |
| 188 | 9 | `void showPopulationInfoMenu()` |  |
| 203 | 62 | `void drawPage(boolean landed)` | The People page itself, put in the window through setPage(): what keepMenu runs as a kept draw from the pool, and what KeptBench runs kept and built new for KeptCheck. |
| 270 | 13 | `javafx.scene.layout.FlowPane pageChips(boolean onPeople)` | The two-chip strip in both pages' heads: People and Household money. |

### THE WORDS. Everything the page says, worked out without a node, each (lines 300-375)

| line | len | member | says |
|---:|---:|---|---|
| 306 | 1 | **type** `record Cell(String label, String value, String note, String tone, String where)` | One of the vitals: what it reads, its note, its verdict and where its door goes. |
| 309 | 26 | `Cell[] peopleCells()` | The five vitals: living here, out of work, the homes, off sick, going short. |
| 337 | 9 | `static String outOfWorkNote(double jobless, double unemployed, long unfilled)` | OUT OF WORK's note: the count, and the verdict the old page printed under it. |
| 355 | 13 | `String goingShortNote()` | GOING SHORT's note: which of the two hungers it is (0.7.27). |
| 370 | 5 | `static String shareWords(double share)` | A share in words, "under 1%" when it rounds to nothing but is not nothing. |

### homes (lines 376-441)

| line | len | member | says |
|---:|---:|---|---|
| 385 | 12 | **type** `record Homes(double built, double needed, double spare, double onSite, double shares, double doubled, Strin...` | The homes, in one verdict (0.7.27). |
| 388 | 5 | `String cellNote()` _(in PeopleScreen.Homes)_ | SPARE HOMES' note: the old cell's words. |
| 395 | 1 | `boolean isShort()` _(in PeopleScreen.Homes)_ | Short of doors by a whole household or more: the verdict's red, and the gauge's short segment. |
| 398 | 43 | `Homes homes()` |  |

### the month (lines 442-579)

| line | len | member | says |
|---:|---:|---|---|
| 452 | 4 | **type** `record Month(boolean year, boolean available, double start, double end, double born, double died, double in...` | The month's flows, or the year's (yearView): where the headcount started, the four flows with what made up the dead and the leavers, and where it ended. |
| 454 | 1 | `double net()` _(in PeopleScreen.Month)_ |  |
| 457 | 49 | `Month month(boolean year)` |  |
| 508 | 5 | `static void unsplit(List<Slice> parts, double total)` | Whatever of a total its parts do not account for, as a grey part of its own: an older save's month. |
| 519 | 3 | `boolean drawStruck()` | Whether Migration struck (or the save carried) this month's draw: a city with people draws somebody. |
| 523 | 1 | `private static double finite(double v)` |  |
| 546 | 16 | `List<String[]> monthEvents()` | The month's events, as chips in the card's head: each a state of the model, nothing random. |
| 564 | 15 | `String milestone()` | "100,000 people in March 2031": the last of 1k, 10k, 100k and 1M the history's population crossed in the last twelve months, or null. |

### why they come (lines 580-683)

| line | len | member | says |
|---:|---:|---|---|
| 590 | 3 | **type** `record Bridge(double jobs, double perJob, double jobDraw, double jobsPull, double homesFor, double homeDraw...` | The draw as Migration struck it: the jobs' half and its pull (the rent's and the crime's, never above 1), the homes' half and its pulls (senior care, the rent, the crime), the target, and the city against it, with the... |
| 594 | 72 | `Bridge bridge()` |  |
| 668 | 6 | `static String firstSentence(String text)` | A paragraph's first sentence: what a card shows, the rest behind its (i). |
| 676 | 4 | `static String roomWords(double draws, double living)` | "1,158 room to grow", or "more than it draws by 5,989". |
| 682 | 1 | `static String times(double x)` | A multiplier: "×1.01". |

### care (lines 684-784)

| line | len | member | says |
|---:|---:|---|---|
| 687 | 17 | `String careLine(BuildAdvice.Measure m)` | A care ring's line: what the city has against who needs it. |
| 706 | 78 | `String careInfo(BuildAdvice.Measure m)` | A care ring's (i): the old page's paragraphs for it, and where Services shows it in full. |

### outside the families (lines 785-879)

| line | len | member | says |
|---:|---:|---|---|
| 788 | 1 | **type** `record Tile(String name, String icon, double count, String split, String tone, String series, String where)` | One of the five tiles: its name, icon, count, its split, the history series its sparkline draws, and its door. |
| 790 | 27 | `List<Tile> outsideTiles()` |  |
| 819 | 10 | `static String byBand(double[] perBand)` | "21 babies · 1 child · 27 teens": who, by age, where there is anybody; "nobody" where there is not. |
| 831 | 10 | `static String oneOf(AgeBand b)` | One of a band: "baby", "child", "teen". |
| 843 | 5 | **type** `record Pool(List<Slice> in, List<Slice> out, double droppedOffEi, double paid, double premiums, double perC...` | The pool's month: in, out, and the EI line under it. |
| 845 | 1 | `double inTotal()` _(in PeopleScreen.Pool)_ |  |
| 846 | 1 | `double outTotal()` _(in PeopleScreen.Pool)_ |  |
| 849 | 30 | `Pool pool()` |  |

### work (lines 880-951)

| line | len | member | says |
|---:|---:|---|---|
| 883 | 1 | **type** `record Rung(WageBand band, double open, double queue, double chance, double premium, double reach)` | One rung of the skill ladder: the posts this band can be put into, everyone queueing for them, the chance, the pay and how far it reaches. |
| 885 | 16 | `List<Rung> ladder()` |  |
| 903 | 3 | `static String payTone(double premium)` | The pay chip's verdict: red over 1.05x, the city paying over the odds to staff a band (as the ladder's rows were). |
| 908 | 23 | `String[] labourWords()` | The labour line: surplus, shortage or balance, in the old page's words. |
| 933 | 18 | `String pulledWords()` | The pay chips' (i): what paying over the going rate is pulling, in the old page's words. |

### THE DRAWING (lines 952-1054)

| line | len | member | says |
|---:|---:|---|---|
| 957 | 6 | `static VBox card(double gap)` | A card on the Build card ground: RAISED, radius 8, a 1 px EDGE. |
| 969 | 13 | `static HBox cardHead(String title, String info, javafx.scene.Node...right)` | A card's own small heading, in the section heads' grey, and whatever sits at its right. |
| 984 | 7 | `static Label words(String text, int size, String colour)` | Words that wrap, at a size, in a colour. |
| 993 | 6 | `static Label figure(String text, int size, String colour)` | A figure, in the mono face, at a size, in a colour. |
| 1004 | 13 | `VBox details(String name, String caption, javafx.scene.Node...inside)` | "details ▸": a fold the player opens, remembered by `name` while the game runs - every table on the page is behind one. |
| 1019 | 10 | `void scrollTo(javafx.scene.Node target)` | The page brought to a card: the menu's scroller set so it sits near the top. |
| 1031 | 5 | `void toServices(String area, String page)` | Services on one of its pages: an area and a page of it, as its strips name them. |
| 1038 | 9 | `void toHealthcare(BuildAdvice.Measure m)` | Build's Healthcare page with a ring picked, as NEEDS YOU's doors open it. |
| 1049 | 5 | `void toBooks(String cellKey)` | Household money with a cell open: its key as openCell keeps it. |

### the vitals (lines 1055-1105)

| line | len | member | says |
|---:|---:|---|---|
| 1058 | 19 | `VBox vitals()` | The five cells, each a door: the waterfall, the Work card, the Homes card, Services, Household money. |
| 1083 | 15 | `void countUp(VBox vitals)` | LIVING HERE counts from last month's headcount to this one's (the history's population a month back to the model's now) over COUNT_MILLIS - only on a month that landed while the page was watched. |
| 1100 | 5 | `static Label headcount(VBox vitals)` | LIVING HERE's figure in the vitals as drawn, or null. |

### the pyramid (lines 1106-1237)

| line | len | member | says |
|---:|---:|---|---|
| 1116 | 4 | `static String bandColour(AgeBand b)` | A band's colour: who it is, not how it is doing - the dependants the light teal, the working age the teal, the retired the dark (0.7.27; the working bands were GOOD green before, a verdict on nothing). |
| 1122 | 3 | `static String ages(AgeBand b)` | The ages a band holds, as its caption: "18–69", "85–120". |
| 1132 | 63 | `VBox pyramidCard()` | WHO LIVES HERE: the six bands mirrored about an axis, widest at the top's scale, each with the shape a settled city would have as a 1 px outline behind it (PopulationCohorts.equilibriumShare()) - the city's 68% adults... |
| 1197 | 40 | **type** `static final class Mirror extends javafx.scene.layout.Pane` | One band of the pyramid: a bar centred on the axis, `share` of the widest's scale, and the settled share's outline. |
| 1202 | 11 | `Mirror()` _(in PeopleScreen.Mirror)_ | Its three bars, made once in its life (Kept: a kept Mirror is told each month's figures by set()). |
| 1215 | 8 | `void set(double share, double settled, double widest, String colour)` _(in PeopleScreen.Mirror)_ | This band's share, the settled city's, the widest band's, and its colour: laid out again only when one moved. |
| 1224 | 12 | `protected void layoutChildren()` _(in PeopleScreen.Mirror)_ |  |

### the month (lines 1238-1438)

| line | len | member | says |
|---:|---:|---|---|
| 1241 | 3 | `static String signedPeople(double v)` | The people a waterfall column moves, signed: "+142", "−293", "under 1". |
| 1251 | 86 | `VBox monthCard(Pieces.Waterfall[] grow)` | THIS MONTH: the headcount at the start and the end over the columns, and the waterfall - born, died (stacked by cause), moved in, moved out (stacked by why), and the net. |
| 1343 | 6 | `String notStruck()` | What the bridge and the month's popovers say before the month's draw has been struck: a city that has not run its first month, or one saved before 0.7.27 kept the month, read before its first month here. |
| 1351 | 5 | `static List<Slice> negated(List<Slice> parts)` | The same parts taken away: a waterfall's step down is negative, and so are its parts. |
| 1358 | 14 | `VBox skillBar(double[] mix)` | A skill mix as a bar, in whole people, with its table under it. |
| 1374 | 43 | `void arrivalsPopover(javafx.scene.Node anchor)` | Moved in's popover: who arrived by skill, which of them hold a licence, and why. |
| 1419 | 19 | `void departuresPopover(javafx.scene.Node anchor)` | Moved out's popover: who left by skill, and why the educated go first. |

### why they come (lines 1439-1568)

| line | len | member | says |
|---:|---:|---|---|
| 1455 | 17 | `VBox bridgeBox(String caption, String value, String under, String tone, Runnable go, String tip)` | One box of the bridge: a caption over a figure, and what a click on it does. |
| 1474 | 6 | `static Label op(String glyph)` | An operator between the bridge's boxes. |
| 1481 | 87 | `VBox bridgeCard()` |  |

### care (lines 1569-1640)

| line | len | member | says |
|---:|---:|---|---|
| 1572 | 23 | `javafx.scene.layout.GridPane careRow()` | Four of Build's rings, with Build's verdicts: General care, Childcare, Senior care, Death care. |
| 1597 | 43 | `List<javafx.scene.Node> careAlerts()` | Under the rings, only when they fire: death care's two alerts, and the unburied. |

### homes (lines 1641-1690)

| line | len | member | says |
|---:|---:|---|---|
| 1644 | 46 | `VBox homesCard()` | HOMES: households wanting a door against the doors, what is on site, one verdict and one line. |

### households (lines 1691-2182)

| line | len | member | says |
|---:|---:|---|---|
| 1694 | 20 | `static String shortShape(FamilyStructure s)` | A household shape's tile words: short, the full name in its tooltip (0.7.27; the members are drawn). |
| 1722 | 9 | `static HBox members(int[] byBand)` | A household's members, drawn: an adult a full dot, a teen and a child smaller, a baby smaller still, a senior or an elder half filled - in the teal's steps, so "Large family" reads as two adults and four children and ... |
| 1733 | 27 | `static javafx.scene.Node member(AgeBand b)` | One member's glyph. |
| 1762 | 5 | `static int[] membersOf(FamilyStructure s)` | The members of a shape, as members() counts them. |
| 1769 | 5 | `static int[] one(AgeBand b)` | One adult, for the outside strip's tiles: a household of one. |
| 1776 | 1 | **type** `record Piece(String name, String full, double count, int[] members, double[] tiers, String cellKey)` | One tile of the mosaic: its members, its words, how many, its tier mix (families only) and where a click opens. |
| 1779 | 55 | `List<List<Piece>> mosaic()` | The mosaic's three strips: the retired, the families, and the people outside them. |
| 1839 | 53 | `VBox householdsCard()` | HOUSEHOLDS: the mosaic, the totals, the (i) and the matrix behind details. |
| 1894 | 10 | `static HBox rampKey()` | The key to the ramp under a family's tile: a sample of the six steps and what they are. |
| 1915 | 267 | **type** `static final class Strip extends javafx.scene.layout.Pane` | One strip of the mosaic, laid out at whatever width the card gives it. |
| 1934 | 14 | `Strip()` _(in PeopleScreen.Strip)_ | The "+n more" tile and the strip's size, made once in its life (Kept: a kept Strip is told each month's households by set()). |
| 1955 | 38 | `void set(List<Piece> pieces, double total, double widest, boolean families, PeopleScreen screen)` _(in PeopleScreen.Strip)_ | This strip's households: the tiles of the last draw kept, and filled again, for every shape of household still here (a tile is its shape's: same name, members and tier ramp, wherever the sort now puts it); one for a s... |
| 1995 | 5 | `private static String shapeOf(Piece p, boolean families)` _(in PeopleScreen.Strip)_ | What a tile's nodes are made of: its members, its words, and under a family its tier ramp. |
| 2004 | 68 | `protected void layoutChildren()` _(in PeopleScreen.Strip)_ |  |
| 2078 | 51 | **type** `private static final class TileNodes` _(in PeopleScreen.Strip)_ | One tile's nodes: the members drawn, the short name, the count, and under a family the tier ramp. |
| 2086 | 14 | `TileNodes(Piece p, boolean families, String shape)` _(in PeopleScreen.Strip.TileNodes)_ |  |
| 2102 | 26 | `void fill(Piece p, PeopleScreen screen)` _(in PeopleScreen.Strip.TileNodes)_ | The piece's words, its click and its tooltip set again. |
| 2131 | 4 | `static String hex(javafx.scene.paint.Color c)` _(in PeopleScreen.Strip)_ | A colour as the style sheets write it: "#93e2da". |
| 2137 | 24 | `static HBox ramp(double[] tiers, boolean inDraw)` _(in PeopleScreen.Strip)_ | A family's tiers along its foot: six segments as wide as their households, unskilled light to elite dark. |
| 2163 | 7 | `static String rampShape(double[] tiers)` _(in PeopleScreen.Strip)_ | Which of a ramp's segments exist: the tiers with households, as a mask. |
| 2172 | 9 | `static void fillRamp(HBox r, double[] tiers)` _(in PeopleScreen.Strip)_ | A ramp's segments as wide as their households: the shape stands (rampShape), the widths move. |

### outside the families (lines 2183-2361)

| line | len | member | says |
|---:|---:|---|---|
| 2191 | 28 | `List<javafx.scene.Node> outsideRow()` | The five tiles, the pool's month and EI, the orphans' alert, and the table by age behind details. |
| 2221 | 17 | `VBox outsideTile(Tile t)` | One tile: its icon, name, count, split, and a sparkline of the last ten years. |
| 2240 | 26 | `void openOutside(String name)` | Where an outside tile's click goes: Services for the students and the prisons, the books for the rest. |
| 2268 | 44 | `javafx.scene.Node spark(double[] series, int months, double tall)` | A sparkline of the last `months` of a history series, in the people teal; a dash with no history. |
| 2314 | 30 | `VBox poolCard()` | The pool's month, in against out, and EI: what it paid against what the premiums raised. |
| 2346 | 15 | `VBox poolBar(String caption, List<Slice> parts, double scale)` | One of the pool's two bars, on the two's common scale, with its parts named under it. |

### work (lines 2362-2546)

| line | len | member | says |
|---:|---:|---|---|
| 2365 | 70 | `VBox workCard()` | WORK: five figures, the labour line, the ladder drawn, the professions, three tables behind details. |
| 2442 | 19 | `static String wantedNote(WantedPosts wanted)` | The posts the wages are priced on beyond the open ones (LD1): what the city's businesses wanted and the staffing gate would not let them open for want of these workers (WantedPosts), as a sentence to follow the minimu... |
| 2472 | 3 | `static String capitalised(String s)` | "Doctors" from "doctors". |
| 2477 | 4 | `static VBox mini(String caption, String value, String tone)` | A small figure under a caption, for the Work card's strip. |
| 2483 | 24 | `HBox rung(Rung r, double scale, String pulled)` | One rung: its name, the queue as an outline with the open posts filled over it, the figures, the chips. |
| 2509 | 37 | **type** `static final class Pair extends javafx.scene.layout.Pane` | The queue as a tinted outline and the open posts filled over it, on one scale. |
| 2514 | 11 | `Pair()` _(in PeopleScreen.Pair)_ | Its three bars, made once in its life (Kept: a kept Pair is told each month's figures by set()). |
| 2527 | 7 | `void set(double open, double queue, double scale)` _(in PeopleScreen.Pair)_ | The posts open, the queue for them, and the scale they share: laid out again only when one moved. |
| 2535 | 10 | `protected void layoutChildren()` _(in PeopleScreen.Pair)_ |  |

### THE TABLES BEHIND "details", and the pieces they share with the (lines 2547-2894)

| line | len | member | says |
|---:|---:|---|---|
| 2553 | 18 | `javafx.scene.layout.GridPane mixTable(double[] mix, double total)` | Who arrived, or who left, by the skill they hold. |
| 2574 | 37 | `javafx.scene.layout.GridPane outsideTable(Unemployment u, FamilyModel families, double[] noDoor)` | Everybody outside the families, by age. |
| 2620 | 81 | `javafx.scene.layout.GridPane shapeMatrix(FamilyModel families)` | Household shape down the side, pay tier across the top. |
| 2707 | 24 | `String oneMarketNote(PopulationManager pm)` | The bands this month's fill joined into one market, in a sentence, or null when none were (0.7.18): workers take the best-paid post they qualify for, so bands paying the same share the shortage. |
| 2733 | 56 | `javafx.scene.layout.GridPane ladderTable(PopulationManager pm, LabourMarket market, double[] studying)` | The skill ladder: what each band has, what it can take, and what it costs. |
| 2791 | 58 | `javafx.scene.layout.GridPane professionTable(PopulationManager pm, LabourMarket market)` | The posts only a licence can fill. |
| 2851 | 32 | `javafx.scene.layout.GridPane jobTable(long[] jobs, long[] vacancies, double[] fillRates, double[] jobWage)` | Every kind of post the city has built, and how well it is staffed. |

### HOUSEHOLD MONEY: CAN THE PEOPLE OF THIS CITY AFFORD TO LIVE IN IT? (lines 2895-3155)

| line | len | member | says |
|---:|---:|---|---|
| 2964 | 21 | `Cell[] moneyCells()` | The four money vitals: what they keep, what rent takes, who goes short, and the income behind it. |
| 2994 | 2 | **type** `record Verdict(String tone, String word, String words, String whole, int kinds, int shortKinds, List<String...` | The verdict over the grid (0.7.27), counting every row drawn: the working households' cells by what their month leaves; the rows below the rule - the retired, the out of work, the students, the orphans, the prison - b... |
| 2997 | 36 | `Verdict verdict()` |  |
| 3035 | 5 | `static String lowerFirst(String s)` | "out of work, EI run out": a label in a sentence, its first letter only lowered - and not an acronym's. |
| 3051 | 4 | `static boolean hungry(Household own)` | Going hungry, for a row below the rule: the baskets it asked for at the price were fewer than it needs - the money half of the hunger, as getHungryAtFullShelves() counts it, but for the meals eaten out, which that fig... |
| 3057 | 3 | `static double leftAgainstBasket(Household own)` | What a month leaves one of a row below the rule, against a basket (0.7.27; against what it wanted before). |
| 3068 | 6 | `double rowLeft(Household own)` | What the grid's one wide cell says a month leaves one of a row below the rule, in thousands: the retired through their statement, as their pension and their rent make one, and everybody else against a basket. |
| 3076 | 20 | `List<Household> belowTheRule(HouseholdBalance bal, FamilyModel families)` | The rows below the rule, in the grid's order: the retired, then everybody outside the families. |
| 3098 | 4 | `static String gridKey(Household c)` | The grid's key for a cell: "tier:SHAPE" for a family, "OUT:" and its ledger's key for the rest. |
| 3110 | 39 | `List<Pieces.Step> cityMonth()` | The city's month, as the waterfall's steps, in dollars: the wages, what comes off them and what is added, take-home, what it is spent on - the bank's account fees a step of their own (0.7.27; the statement left them o... |
| 3151 | 4 | `static String signedMoney(double dollars)` | Money on the waterfall, signed and compact: "−$36.8M". |

### HOUSEHOLD MONEY, DRAWN (lines 3156-4062)

| line | len | member | says |
|---:|---:|---|---|
| 3178 | 6 | `void showHouseholdMenu()` | The residents' own books - the last participant in this economy that did not have any. |
| 3190 | 44 | `void drawHousehold()` | The Household money page itself, put in the window through setPage(): what keepMenu runs as a kept draw from the moneyPool, and what KeptBench runs kept and built new for KeptCheck. |
| 3236 | 13 | `private VBox moneyVitals()` | The four money vitals as a bar, centred. |
| 3251 | 24 | `private HBox verdictRow()` | The verdict over the grid, its chip and its words, and the two chips that say one household or all of them. |
| 3277 | 1 | **type** `record ShortRow(int row, String name, double got, String gotWords, String chip)` | One kind of household's groceries at the last sale, worked out without drawing it (pure: the probe reads them). |
| 3280 | 17 | `List<ShortRow> shortRows()` | Every kind of household with anybody in it, the hungriest first: its baskets got per basket needed, and how many hold a food voucher (0.7.45). |
| 3299 | 15 | `VBox goesShortRows()` | ...as ranked bars: a row a kind, its bar the baskets it got per basket needed. |
| 3316 | 7 | `VBox quietBooks()` | With no cell open, the books' place says what goes there. |
| 3330 | 76 | **type** `static final class Books extends javafx.scene.layout.Pane` | The grid and the open cell's books: side by side at BESIDE and wider - the grid at its own width, the books in the rest, held at the top of the view while the grid scrolls past - and the books under the grid below that. |
| 3335 | 1 | `Books()` _(in PeopleScreen.Books)_ | A picture on a kept page (KP7): made empty through Kept.of, and told its two children by set(). |
| 3341 | 5 | `void set(VBox grid, javafx.scene.Node panel)` _(in PeopleScreen.Books)_ | The grid and the books it is told to lay out, put in as its children only where they differ (a layout is asked for then; a month that changes neither the nodes nor which are there lays out as its parts ask). |
| 3347 | 1 | `boolean beside(double w)` _(in PeopleScreen.Books)_ |  |
| 3350 | 3 | `public javafx.geometry.Orientation getContentBias()` _(in PeopleScreen.Books)_ | Its height follows its width: beside or stacked. |
| 3354 | 8 | `protected double computePrefHeight(double width)` _(in PeopleScreen.Books)_ |  |
| 3363 | 1 | `protected double computeMinHeight(double width)` _(in PeopleScreen.Books)_ |  |
| 3365 | 1 | `protected double computePrefWidth(double height)` _(in PeopleScreen.Books)_ |  |
| 3367 | 15 | `protected void layoutChildren()` _(in PeopleScreen.Books)_ |  |
| 3384 | 13 | `private double held(double panelHeight)` _(in PeopleScreen.Books)_ | How far down the books sit so their top stays at the top of the view, within the grid's height. |
| 3399 | 6 | `static javafx.scene.control.ScrollPane scrollerOf(javafx.scene.Node n)` _(in PeopleScreen.Books)_ | The scroll pane this sits in - up through its skin's viewport - or null. |
| 3408 | 11 | `void hookBooks()` | The menu's scroll moves the books with it: one listener, for the window's life, laying the books out again. |
| 3428 | 23 | `void reveal()` | THE GRID AND ITS BOOKS ARE WHAT GETS REVEALED, when they stack: the top of the grid to the top of the view and the panel landing under it, both on screen at once - or, when they do not both fit, the panel's foot, whic... |
| 3453 | 13 | `VBox cityMonthCard(HouseholdAccounts hh)` | THE CITY'S MONTH: the waterfall, what was saved since the founding, and the statement behind details. |
| 3468 | 53 | `VBox cityStatement(HouseholdAccounts hh)` | The city's month as the statement it was, with the bank's account fees on a line of their own (0.7.27). |
| 3529 | 6 | `static VBox statCard(String caption, String value, String tone, String line, String info)` | A small figure card: a caption, the figure, a line, and its (i). |
| 3537 | 19 | `javafx.scene.layout.GridPane putByCards(HouseholdBalance bal)` | WHAT THEY HAVE PUT BY: saved, owed, the month's dividends, and the cars. |
| 3558 | 35 | `List<javafx.scene.Node> putByChips(HouseholdBalance bal, FamilyModel families)` | The two chips under the cards: who is at the credit ceiling, and who flatshares because one wage won't cover a home - each opens the cell. |
| 3608 | 12 | `private static List<javafx.scene.layout.ColumnConstraints> matrixColumns()` |  |
| 3625 | 86 | `VBox affordabilityGrid(HouseholdAccounts hh, HouseholdBalance bal, FamilyModel families)` | Household shape down the side, pay tier across the top, and in every cell what that household has left at the end of the month. |
| 3713 | 24 | `private VBox tierHead(HouseholdAccounts hh, FamilyModel families, int t)` | A tier's head over its column: its name, and under it what one earner of the tier was paid. |
| 3739 | 11 | `private Label tierCell(HouseholdAccounts hh, HouseholdBalance bal, int t)` | "Every household of that tier", one tier's cell: the average of the column above it, ringed when the tier is going short. |
| 3755 | 7 | `private static Label moneyCell(String text, String tone, String more)` | A figure's cell in the grid (gridCell's, right-aligned) with a style of its own after the figure's: set whole, once, since a kept cell is never built on the style it was handed back with (KP7). |
| 3764 | 17 | `int gridSectionRow(javafx.scene.layout.GridPane grid, String key, String caption, int line)` | A rule and a caption across the matrix, with the (i) that says who is below it: a different kind of household. |
| 3802 | 58 | `int otherRows(javafx.scene.layout.GridPane grid, HouseholdAccounts hh, HouseholdBalance bal, FamilyModel families, int line)` | The retired, the out of work, the students, the orphans and the prison, as rows of the same matrix. |
| 3862 | 16 | `int otherRow(javafx.scene.layout.GridPane grid, String label, Household own, double perHousehold, double homes, int line)` | One matrix row for a household with no tier: a name, then one wide cell. |
| 3897 | 49 | `javafx.scene.Node wideCell(Household own, double perHousehold, double homes)` | cashCell's cell, one row wide: the same tint, the same ring, the same click - with the headcount inside it, because the six tier columns it spans have nothing of their own to say about this household. |
| 3955 | 40 | `Label cashCell(HouseholdAccounts hh, FamilyModel families, FamilyStructure shape, PayTier tier, int tierIndex)` | One cell: what this household has left, and how badly. |
| 4011 | 51 | `VBox basketBlock(Household cell)` | What a full basket would have been, what they planned to spend, and where the difference came from. |

### THE MONEY BLOCKS BOTH PANELS SHARE (lines 4063-4683)

| line | len | member | says |
|---:|---:|---|---|
| 4085 | 3 | `private static Label note(String text)` | A statement's note under a line, in a scope of its own (Kept, KP7): a note that comes and goes in a panel shifts no other label, and the notes are one kind, so any of them may be handed to any. |
| 4090 | 4 | `String costMoney(double dollars)` | A cost, printed negative - but a cost of nothing reads "$0", never "-$0". |
| 4096 | 7 | `static String stakePct(double share)` | A stake, with enough decimals left on it to still say something. |
| 4111 | 7 | `static String shareOf(double part, double whole)` | What part is of whole - or a dash, when there is no whole to be part of. |
| 4120 | 6 | `static String monthsRun(double months)` | A run of months, said the way a person would say it. |
| 4128 | 9 | `Label panelBlockHead(String text)` | The small heading that divides a statement panel into blocks. |
| 4139 | 20 | `Label panelWho(HouseholdBalance bal, Household cell)` | How many of them there are, how big each one is, and how much of the city that is. |
| 4169 | 229 | `void positionBlock(VBox panel, HouseholdBalance bal, Household own)` | What one of these households HAS, what it OWES, what it OWNS and what all of that leaves it worth. |
| 4407 | 31 | `void ratioBlock(VBox panel, Household own, double income, double tax, String billsLabel, double bills, double fees, double shop...` | The same month again, as shares rather than figures. |
| 4448 | 40 | `VBox outsideStatement(HouseholdBalance bal)` | One of the people outside the families, and what a month does to them. |
| 4497 | 186 | `VBox openStatement(HouseholdAccounts hh, HouseholdBalance bal, FamilyModel families)` | The month of whichever cell is open, in full. |

### THE TIER TABLE, AS A TABLE. (lines 4684-4789)

| line | len | member | says |
|---:|---:|---|---|
| 4727 | 26 | `HBox flowBar(double tax, double rent, double fees, double shops, double left, double width)` | Where a household's month went, as one bar. |
| 4755 | 11 | `Region barPart(double width, String colour, String what)` | One segment. |
| 4768 | 21 | `HBox flowKey()` | The key under the bar, so the colours mean something the first time. |


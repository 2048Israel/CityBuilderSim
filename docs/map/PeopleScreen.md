# PeopleScreen.java - 4,285 lines · 146 methods · 24 constants · interface

`ham/citybuildersim/ui/PeopleScreen.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

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

**Uses:** [Palette](Palette.md) (515), [AgeBand](AgeBand.md) (64), [Pieces](Pieces.md) (36), [Household](Household.md) (32), [PayTier](PayTier.md) (29), [HouseholdAccounts](HouseholdAccounts.md) (24), [WageBand](WageBand.md) (21), [FamilyStructure](FamilyStructure.md) (21), [FamilyModel](FamilyModel.md) (19), [BuildAdvice](BuildAdvice.md) (17), [HouseholdBalance](HouseholdBalance.md) (15), [CareType](CareType.md) (13), [Migration](Migration.md) (10), [Healthcare](Healthcare.md) (10), [Icons](Icons.md) (10), [PopulationManager](PopulationManager.md) (9), [UnemployedHousehold](UnemployedHousehold.md) (8), [PopulationCohorts](PopulationCohorts.md) (7), [Sickness](Sickness.md) (7), [JobType](JobType.md) (7), [LabourMarket](LabourMarket.md) (6), [EducationType](EducationType.md) (6), [Unemployment](Unemployment.md) (5), [Equity](Equity.md) (5), [BuildingManager](BuildingManager.md) (4), [Health](Health.md) (4), [Statement](Statement.md) (4), [LandScreen](LandScreen.md) (3), [UserInterface](UserInterface.md) (2), [HistorySave](HistorySave.md) (2)... and 11 more

**Used by (2):** [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 57 | PEOPLE (0.7.27) |
| 135 | · what the page remembers between draws |
| 254 | THE WORDS. Everything the page says, worked out without a node, each |
| 327 | · homes |
| 393 | · the month |
| 525 | · why they come |
| 595 | · care |
| 696 | · outside the families |
| 791 | · work |
| 863 | THE DRAWING |
| 966 | · the vitals |
| 1010 | · the pyramid |
| 1137 | · the month |
| 1323 | · why they come |
| 1432 | · care |
| 1500 | · homes |
| 1546 | · households |
| 1932 | · outside the families |
| 2099 | · work |
| 2237 | THE TABLES BEHIND "details", and the pieces they share with the |
| 2573 | HOUSEHOLD MONEY: CAN THE PEOPLE OF THIS CITY AFFORD TO LIVE IN IT? |
| 2823 | HOUSEHOLD MONEY, DRAWN |
| 3330 | · · the retired |
| 3347 | · · and outside the families |
| 3581 | THE MONEY BLOCKS BOTH PANELS SHARE |
| 3970 | · · the month |
| 4176 | THE TIER TABLE, AS A TABLE. |
| 4281 | · Small helpers so the report screens stay readable. Shared by the sector |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 104 | `PeopleScreen.OUT_OF_WORK_SHORT` | `.03` | Under this share out of work, nobody is spare: amber, "jobs going unfilled". |
| 107 | `PeopleScreen.OUT_OF_WORK_HIGH` | `.15` | Over this, high: amber. |
| 110 | `PeopleScreen.OUT_OF_WORK_FAR` | `.25` | Over this, far too many adults with nothing to do: red. |
| 120 | `PeopleScreen.GOING_SHORT_WARN` | `.01` | Over this share of the city eating less than a basket, GOING SHORT is amber; the dashboard's HUNGRY reads it too (0.7.27). |
| 123 | `PeopleScreen.GOING_SHORT_BAD` | `.10` | ...and over this, red. |
| 221 | `PeopleScreen.COUNT_MILLIS` | `600` | How long the headcount counts and the month's bars grow when a month lands (Jerus's "fun": the number moves). |
| 245 | `PeopleScreen.STILL_FAKED` | `"What this model does not do yet:\n" + "· Nobody has an age.Each band holds a...` | What the model does not do yet: the four lines of the old page's eight that are still true (0.7.27). |
| 481 | `PeopleScreen.BORN` | `Palette.PEOPLE_LIGHT, MOVED_IN = Palette.PEOPLE` | The born and the arrived: the people teal's light step and the teal. |
| 484 | `PeopleScreen.DIED_OF_AGE` | `Palette.PEOPLE_DARK, DIED_OF_ILLNESS = Palette.BUSINESS, DIED_KILLED = Palett...` | The dead by cause: of age the dark teal, of illness the violet, the killed the pink, the aged out a light blue. |
| 488 | `PeopleScreen.LEFT_WORK` | `Palette.PEOPLE_DARK, LEFT_BROKE = Palette.MONEY, LEFT_CRIME = Palette.BUILDING` | The leavers by why: for want of work the dark teal, broke the blue, driven out by crime the pink. |
| 876 | `PeopleScreen.CARD` | `"-fx-padding: 10 12 10 12; -fx-background-color: " + Palette.RAISED + ";" + "...` | The Build card ground every card on both pages stands on. |
| 1013 | `PeopleScreen.PYRAMID_HALF` | `200` | How wide half of the widest band is drawn, at most. |
| 1326 | `PeopleScreen.WHY_INFO` | `"The city draws people the way Migration strikes it each month: every post " ...` | WHY PEOPLE COME's (i): what the bridge is. |
| 1690 | `PeopleScreen.TILE_MIN` | `56` | The narrowest a tile is drawn; anything smaller folds into "+n more". |
| 1803 | `PeopleScreen.Strip.TILE_TALL` | `62` | How tall a strip is. |
| 1935 | `PeopleScreen.OUTSIDE_INFO` | `"Families are built from the adults who work, and their children." + "Everybo...` | OUTSIDE THE FAMILIES' (i): who they are, the old page's words. |
| 2165 | `PeopleScreen.LADDER_INFO` | `"open = posts this band can be put into(licensed posts are on the " + "chips ...` | The ladder's (i): the old legend. |
| 2630 | `PeopleScreen.IN_DOLLARS` | `"Every figure on this page is in dollars, not the thousands the rest of the "...` | The page's (i): what its money is counted in. |
| 2635 | `PeopleScreen.PAID_IN_ORDER` | `"The order is fixed: the payslip first, then rent, then the fees, and " + "th...` | The order a household pays in, and what happens when it comes up short: the verdict's (i). |
| 2828 | `PeopleScreen.BESIDE` | `1200` | The narrowest page that sets the open cell's books beside the grid; under it they stack. |
| 3104 | `PeopleScreen.CARS_INFO` | `"Bought out of savings past the same cushion a share is, so income " + "decid...` | The cars' (i): the old note. |
| 3174 | `PeopleScreen.TIER_COL` | `88` | How wide a tier column is. |
| 3175 | `PeopleScreen.SHAPE_COL` | `168` |  |
| 3306 | `PeopleScreen.BELOW_THE_RULE` | `"Below the rule: the retired, on a pension and no wage; the out of work, " + ...` | The section captions' (i): who is below the rule, and what their one cell says. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 53 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 142 | `private int shownMonth` | The month at the last draw of the page being watched, or -1: a month landing on it counts LIVING HERE up. |
| 145 | `boolean yearView` | Whether the month's waterfall shows the last twelve months, from the history, instead. |
| 148 | `final java.util.Set<String> detailsOpen` | Which "details" are open, by name; kept while the game runs, as the page's place is. |
| 151 | `private Region monthCard, homesCard, workCard` | The cards the vitals' doors scroll to, as last drawn. |
| 1104 | `private final double share, settled, widest` |  |
| 1105 | `private final Region bar` |  |
| 1769 | `private final List<Piece> pieces` |  |
| 1770 | `private final double total, widest` |  |
| 1771 | `private final List<VBox> tiles` |  |
| 1772 | `private final VBox more` |  |
| 1773 | `private final Label moreLabel` |  |
| 1774 | `private final Tooltip moreTip` |  |
| 2207 | `private final double open, queue, scale` |  |
| 2208 | `private final Region q` |  |
| 2571 | `boolean householdPerFamily` | Whether the per-tier table shows one family or the whole city. |
| 2620 | `String openCell` | Which cell of the grid is open, as "tier:shape", or "OUT:" and a ledger's key. |
| 2623 | `boolean revealOpened` | Set by a click that opens a cell, so the panel it opened is brought into view. |
| 2626 | `javafx.scene.Node revealTop` | What this draw should bring into view, once, when the books stack under the grid: the grid's top and the panel's foot. |
| 2627 | `javafx.scene.Node revealBottom` |  |
| 2831 | `private Books books` | The books' pane as last drawn, for the scroll to hold the panel at the top of the view. |
| 2834 | `private boolean booksHooked` | Whether the scroll listener is on the menu's scroller yet: once, for the window's life. |
| 2924 | `private final VBox grid` |  |
| 2925 | `private final javafx.scene.Node panel` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 50 | 4236 | **type** `final class PeopleScreen` | The People tab and its second page, Household money. |
| 55 | 1 | `PeopleScreen(UserInterface ui)` |  |

### PEOPLE (0.7.27) (lines 57-134)

| line | len | member | says |
|---:|---:|---|---|
| 113 | 5 | `static String outOfWorkTone(double jobless)` | The out-of-work rate's verdict: red far too high, amber high or short of hands, green between. |
| 126 | 3 | `static String goingShortTone(double hunger)` | GOING SHORT's verdict, on the share of people eating less than a basket (HouseholdBalance.getHungerRate()). |
| 131 | 3 | `static String sickTone(double sick)` | OFF SICK's verdict: red over 12% of the workforce, amber over 6%. |

### what the page remembers between draws (lines 135-253)

| line | len | member | says |
|---:|---:|---|---|
| 154 | 3 | `void forget()` | A new city or a load: nothing has just landed. |
| 158 | 61 | `void showPopulationInfoMenu()` |  |
| 224 | 13 | `javafx.scene.layout.FlowPane pageChips(boolean onPeople)` | The two-chip strip in both pages' heads: People and Household money. |

### THE WORDS. Everything the page says, worked out without a node, each (lines 254-326)

| line | len | member | says |
|---:|---:|---|---|
| 260 | 1 | **type** `record Cell(String label, String value, String note, String tone, String where)` | One of the vitals: what it reads, its note, its verdict and where its door goes. |
| 263 | 25 | `Cell[] peopleCells()` | The five vitals: living here, out of work, the homes, off sick, going short. |
| 290 | 9 | `static String outOfWorkNote(double jobless, double unemployed, int unfilled)` | OUT OF WORK's note: the count, and the verdict the old page printed under it. |
| 306 | 13 | `String goingShortNote()` | GOING SHORT's note: which of the two hungers it is (0.7.27). |
| 321 | 5 | `static String shareWords(double share)` | A share in words, "under 1%" when it rounds to nothing but is not nothing. |

### homes (lines 327-392)

| line | len | member | says |
|---:|---:|---|---|
| 336 | 12 | **type** `record Homes(double built, double needed, double spare, double onSite, double shares, double doubled, Strin...` | The homes, in one verdict (0.7.27). |
| 339 | 5 | `String cellNote()` _(in PeopleScreen.Homes)_ | SPARE HOMES' note: the old cell's words. |
| 346 | 1 | `boolean isShort()` _(in PeopleScreen.Homes)_ | Short of doors by a whole household or more: the verdict's red, and the gauge's short segment. |
| 349 | 43 | `Homes homes()` |  |

### the month (lines 393-524)

| line | len | member | says |
|---:|---:|---|---|
| 403 | 4 | **type** `record Month(boolean year, boolean available, double start, double end, double born, double died, double in...` | The month's flows, or the year's (yearView): where the headcount started, the four flows with what made up the dead and the leavers, and where it ended. |
| 405 | 1 | `double net()` _(in PeopleScreen.Month)_ |  |
| 408 | 47 | `Month month(boolean year)` |  |
| 457 | 5 | `static void unsplit(List<Slice> parts, double total)` | Whatever of a total its parts do not account for, as a grey part of its own: an older save's month. |
| 468 | 3 | `boolean drawStruck()` | Whether Migration struck (or the save carried) this month's draw: a city with people draws somebody. |
| 472 | 1 | `private static double finite(double v)` |  |
| 491 | 16 | `List<String[]> monthEvents()` | The month's events, as chips in the card's head: each a state of the model, nothing random. |
| 509 | 15 | `String milestone()` | "100,000 people in March 2031": the last of 1k, 10k, 100k and 1M the history's population crossed in the last twelve months, or null. |

### why they come (lines 525-594)

| line | len | member | says |
|---:|---:|---|---|
| 533 | 3 | **type** `record Bridge(double jobs, double perJob, double jobDraw, double homesFor, double homeDraw, double beforePu...` | The draw as Migration struck it: the jobs' half and the homes' half, the three pulls, the target, and the city against it, with the old page's why sentence - its first sentence on the card, the whole behind the (i). |
| 537 | 40 | `Bridge bridge()` |  |
| 579 | 6 | `static String firstSentence(String text)` | A paragraph's first sentence: what a card shows, the rest behind its (i). |
| 587 | 4 | `static String roomWords(double draws, double living)` | "1,158 room to grow", or "more than it draws by 5,989". |
| 593 | 1 | `static String times(double x)` | A multiplier: "×1.01". |

### care (lines 595-695)

| line | len | member | says |
|---:|---:|---|---|
| 598 | 17 | `String careLine(BuildAdvice.Measure m)` | A care ring's line: what the city has against who needs it. |
| 617 | 78 | `String careInfo(BuildAdvice.Measure m)` | A care ring's (i): the old page's paragraphs for it, and where Services shows it in full. |

### outside the families (lines 696-790)

| line | len | member | says |
|---:|---:|---|---|
| 699 | 1 | **type** `record Tile(String name, String icon, double count, String split, String tone, String series, String where)` | One of the five tiles: its name, icon, count, its split, the history series its sparkline draws, and its door. |
| 701 | 27 | `List<Tile> outsideTiles()` |  |
| 730 | 10 | `static String byBand(double[] perBand)` | "21 babies · 1 child · 27 teens": who, by age, where there is anybody; "nobody" where there is not. |
| 742 | 10 | `static String oneOf(AgeBand b)` | One of a band: "baby", "child", "teen". |
| 754 | 5 | **type** `record Pool(List<Slice> in, List<Slice> out, double droppedOffEi, double paid, double premiums, double perC...` | The pool's month: in, out, and the EI line under it. |
| 756 | 1 | `double inTotal()` _(in PeopleScreen.Pool)_ |  |
| 757 | 1 | `double outTotal()` _(in PeopleScreen.Pool)_ |  |
| 760 | 30 | `Pool pool()` |  |

### work (lines 791-862)

| line | len | member | says |
|---:|---:|---|---|
| 794 | 1 | **type** `record Rung(WageBand band, double open, double queue, double chance, double premium, double reach)` | One rung of the skill ladder: the posts this band can be put into, everyone queueing for them, the chance, the pay and how far it reaches. |
| 796 | 16 | `List<Rung> ladder()` |  |
| 814 | 3 | `static String payTone(double premium)` | The pay chip's verdict: red over 1.05x, the city paying over the odds to staff a band (as the ladder's rows were). |
| 819 | 23 | `String[] labourWords()` | The labour line: surplus, shortage or balance, in the old page's words. |
| 844 | 18 | `String pulledWords()` | The pay chips' (i): what paying over the going rate is pulling, in the old page's words. |

### THE DRAWING (lines 863-965)

| line | len | member | says |
|---:|---:|---|---|
| 868 | 6 | `static VBox card(double gap)` | A card on the Build card ground: RAISED, radius 8, a 1 px EDGE. |
| 880 | 13 | `static HBox cardHead(String title, String info, javafx.scene.Node...right)` | A card's own small heading, in the section heads' grey, and whatever sits at its right. |
| 895 | 7 | `static Label words(String text, int size, String colour)` | Words that wrap, at a size, in a colour. |
| 904 | 6 | `static Label figure(String text, int size, String colour)` | A figure, in the mono face, at a size, in a colour. |
| 915 | 13 | `VBox details(String name, String caption, javafx.scene.Node...inside)` | "details ▸": a fold the player opens, remembered by `name` while the game runs - every table on the page is behind one. |
| 930 | 10 | `void scrollTo(javafx.scene.Node target)` | The page brought to a card: the menu's scroller set so it sits near the top. |
| 942 | 5 | `void toServices(String area, String page)` | Services on one of its pages: an area and a page of it, as its strips name them. |
| 949 | 9 | `void toHealthcare(BuildAdvice.Measure m)` | Build's Healthcare page with a ring picked, as NEEDS YOU's doors open it. |
| 960 | 5 | `void toBooks(String cellKey)` | Household money with a cell open: its key as openCell keeps it. |

### the vitals (lines 966-1009)

| line | len | member | says |
|---:|---:|---|---|
| 969 | 19 | `VBox vitals()` | The five cells, each a door: the waterfall, the Work card, the Homes card, Services, Household money. |
| 994 | 15 | `void countUp(VBox vitals)` | LIVING HERE counts from last month's headcount to this one's (the history's population a month back to the model's now) over COUNT_MILLIS - only on a month that landed while the page was watched. |

### the pyramid (lines 1010-1136)

| line | len | member | says |
|---:|---:|---|---|
| 1020 | 4 | `static String bandColour(AgeBand b)` | A band's colour: who it is, not how it is doing - the dependants the light teal, the working age the teal, the retired the dark (0.7.27; the working bands were GOOD green before, a verdict on nothing). |
| 1026 | 3 | `static String ages(AgeBand b)` | The ages a band holds, as its caption: "18–69", "85–120". |
| 1036 | 65 | `VBox pyramidCard()` | WHO LIVES HERE: the six bands mirrored about an axis, widest at the top's scale, each with the shape a settled city would have as a 1 px outline behind it (PopulationCohorts.equilibriumShare()) - the city's 68% adults... |
| 1103 | 33 | **type** `static final class Mirror extends javafx.scene.layout.Pane` | One band of the pyramid: a bar centred on the axis, `share` of the widest's scale, and the settled share's outline. |
| 1107 | 15 | `Mirror(double share, double settled, double widest, String colour)` _(in PeopleScreen.Mirror)_ |  |
| 1123 | 12 | `protected void layoutChildren()` _(in PeopleScreen.Mirror)_ |  |

### the month (lines 1137-1322)

| line | len | member | says |
|---:|---:|---|---|
| 1140 | 3 | `static String signedPeople(double v)` | The people a waterfall column moves, signed: "+142", "−293", "under 1". |
| 1150 | 71 | `VBox monthCard(Pieces.Waterfall[] grow)` | THIS MONTH: the headcount at the start and the end over the columns, and the waterfall - born, died (stacked by cause), moved in, moved out (stacked by why), and the net. |
| 1227 | 6 | `String notStruck()` | What the bridge and the month's popovers say before the month's draw has been struck: a city that has not run its first month, or one saved before 0.7.27 kept the month, read before its first month here. |
| 1235 | 5 | `static List<Slice> negated(List<Slice> parts)` | The same parts taken away: a waterfall's step down is negative, and so are its parts. |
| 1242 | 14 | `VBox skillBar(double[] mix)` | A skill mix as a bar, in whole people, with its table under it. |
| 1258 | 43 | `void arrivalsPopover(javafx.scene.Node anchor)` | Moved in's popover: who arrived by skill, which of them hold a licence, and why. |
| 1303 | 19 | `void departuresPopover(javafx.scene.Node anchor)` | Moved out's popover: who left by skill, and why the educated go first. |

### why they come (lines 1323-1431)

| line | len | member | says |
|---:|---:|---|---|
| 1334 | 21 | `VBox bridgeBox(String caption, String value, String under, String tone, Runnable go, String tip)` | One box of the bridge: a caption over a figure, and what a click on it does. |
| 1357 | 6 | `static Label op(String glyph)` | An operator between the bridge's boxes. |
| 1364 | 67 | `VBox bridgeCard()` |  |

### care (lines 1432-1499)

| line | len | member | says |
|---:|---:|---|---|
| 1435 | 20 | `javafx.scene.layout.GridPane careRow()` | Four of Build's rings, with Build's verdicts: General care, Childcare, Senior care, Death care. |
| 1457 | 42 | `List<javafx.scene.Node> careAlerts()` | Under the rings, only when they fire: death care's two alerts, and the unburied. |

### homes (lines 1500-1545)

| line | len | member | says |
|---:|---:|---|---|
| 1503 | 42 | `VBox homesCard()` | HOMES: households wanting a door against the doors, what is on site, one verdict and one line. |

### households (lines 1546-1931)

| line | len | member | says |
|---:|---:|---|---|
| 1549 | 20 | `static String shortShape(FamilyStructure s)` | A household shape's tile words: short, the full name in its tooltip (0.7.27; the members are drawn). |
| 1576 | 9 | `static HBox members(int[] byBand)` | A household's members, drawn: an adult a full dot, a teen and a child smaller, a baby smaller still, a senior or an elder half filled - in the teal's steps, so "Large family" reads as two adults and four children and ... |
| 1587 | 27 | `static javafx.scene.Node member(AgeBand b)` | One member's glyph. |
| 1616 | 5 | `static int[] membersOf(FamilyStructure s)` | The members of a shape, as members() counts them. |
| 1623 | 5 | `static int[] one(AgeBand b)` | One adult, for the outside strip's tiles: a household of one. |
| 1630 | 1 | **type** `record Piece(String name, String full, double count, int[] members, double[] tiers, String cellKey)` | One tile of the mosaic: its members, its words, how many, its tier mix (families only) and where a click opens. |
| 1633 | 55 | `List<List<Piece>> mosaic()` | The mosaic's three strips: the retired, the families, and the people outside them. |
| 1693 | 52 | `VBox householdsCard()` | HOUSEHOLDS: the mosaic, the totals, the (i) and the matrix behind details. |
| 1747 | 10 | `static HBox rampKey()` | The key to the ramp under a family's tile: a sample of the six steps and what they are. |
| 1768 | 163 | **type** `static final class Strip extends javafx.scene.layout.Pane` | One strip of the mosaic, laid out at whatever width the card gives it. |
| 1776 | 25 | `Strip(List<Piece> pieces, double total, double widest, boolean families, PeopleScreen screen)` _(in PeopleScreen.Strip)_ |  |
| 1805 | 60 | `protected void layoutChildren()` _(in PeopleScreen.Strip)_ |  |
| 1867 | 32 | `static VBox tile(Piece p, boolean families, PeopleScreen screen)` _(in PeopleScreen.Strip)_ | One tile: the members drawn, the short name, the count, and under a family the tier ramp. |
| 1901 | 4 | `static String hex(javafx.scene.paint.Color c)` _(in PeopleScreen.Strip)_ | A colour as the style sheets write it: "#93e2da". |
| 1907 | 23 | `static HBox ramp(double[] tiers)` _(in PeopleScreen.Strip)_ | A family's tiers along its foot: six segments as wide as their households, unskilled light to elite dark. |

### outside the families (lines 1932-2098)

| line | len | member | says |
|---:|---:|---|---|
| 1940 | 23 | `List<javafx.scene.Node> outsideRow()` | The five tiles, the pool's month and EI, the orphans' alert, and the table by age behind details. |
| 1965 | 19 | `VBox outsideTile(Tile t)` | One tile: its icon, name, count, split, and a sparkline of the last ten years. |
| 1986 | 26 | `void openOutside(String name)` | Where an outside tile's click goes: Services for the students and the prisons, the books for the rest. |
| 2014 | 35 | `javafx.scene.Node spark(double[] series, int months, double tall)` | A sparkline of the last `months` of a history series, in the people teal; a dash with no history. |
| 2051 | 30 | `VBox poolCard()` | The pool's month, in against out, and EI: what it paid against what the premiums raised. |
| 2083 | 15 | `VBox poolBar(String caption, List<Slice> parts, double scale)` | One of the pool's two bars, on the two's common scale, with its parts named under it. |

### work (lines 2099-2236)

| line | len | member | says |
|---:|---:|---|---|
| 2102 | 61 | `VBox workCard()` | WORK: five figures, the labour line, the ladder drawn, the professions, three tables behind details. |
| 2172 | 3 | `static String capitalised(String s)` | "Doctors" from "doctors". |
| 2177 | 4 | `static VBox mini(String caption, String value, String tone)` | A small figure under a caption, for the Work card's strip. |
| 2183 | 21 | `HBox rung(Rung r, double scale, String pulled)` | One rung: its name, the queue as an outline with the open posts filled over it, the figures, the chips. |
| 2206 | 30 | **type** `static final class Pair extends javafx.scene.layout.Pane` | The queue as a tinted outline and the open posts filled over it, on one scale. |
| 2210 | 14 | `Pair(double open, double queue, double scale)` _(in PeopleScreen.Pair)_ |  |
| 2225 | 10 | `protected void layoutChildren()` _(in PeopleScreen.Pair)_ |  |

### THE TABLES BEHIND "details", and the pieces they share with the (lines 2237-2572)

| line | len | member | says |
|---:|---:|---|---|
| 2243 | 18 | `javafx.scene.layout.GridPane mixTable(double[] mix, double total)` | Who arrived, or who left, by the skill they hold. |
| 2264 | 37 | `javafx.scene.layout.GridPane outsideTable(Unemployment u, FamilyModel families, double[] noDoor)` | Everybody outside the families, by age. |
| 2310 | 77 | `javafx.scene.layout.GridPane shapeMatrix(FamilyModel families)` | Household shape down the side, pay tier across the top. |
| 2393 | 24 | `String oneMarketNote(PopulationManager pm)` | The bands this month's fill joined into one market, in a sentence, or null when none were (0.7.18): workers take the best-paid post they qualify for, so bands paying the same share the shortage. |
| 2419 | 56 | `javafx.scene.layout.GridPane ladderTable(PopulationManager pm, LabourMarket market, double[] studying)` | The skill ladder: what each band has, what it can take, and what it costs. |
| 2477 | 54 | `javafx.scene.layout.GridPane professionTable(PopulationManager pm, LabourMarket market)` | The posts only a licence can fill. |
| 2533 | 28 | `javafx.scene.layout.GridPane jobTable(int[] jobs, int[] vacancies, double[] fillRates, double[] jobWage)` | Every kind of post the city has built, and how well it is staffed. |

### HOUSEHOLD MONEY: CAN THE PEOPLE OF THIS CITY AFFORD TO LIVE IN IT? (lines 2573-2822)

| line | len | member | says |
|---:|---:|---|---|
| 2642 | 21 | `Cell[] moneyCells()` | The four money vitals: what they keep, what rent takes, who goes short, and the income behind it. |
| 2672 | 2 | **type** `record Verdict(String tone, String word, String words, String whole, int kinds, int shortKinds, List<String...` | The verdict over the grid (0.7.27), counting every row drawn: the working households' cells by what their month leaves; the rows below the rule - the retired, the out of work, the students, the orphans, the prison - b... |
| 2675 | 36 | `Verdict verdict()` |  |
| 2713 | 5 | `static String lowerFirst(String s)` | "out of work, EI run out": a label in a sentence, its first letter only lowered - and not an acronym's. |
| 2727 | 3 | `static boolean hungry(Household own)` | Going hungry, for a row below the rule: its own plan could not buy a basket - the money half of the hunger, as getHungryAtFullShelves() counts it, but for the meals eaten out, which that figure adds to the plan and th... |
| 2732 | 3 | `static double leftAgainstBasket(Household own)` | What a month leaves one of a row below the rule, against a basket (0.7.27; against what it wanted before). |
| 2743 | 6 | `double rowLeft(Household own)` | What the grid's one wide cell says a month leaves one of a row below the rule, in thousands: the retired through their statement, as their pension and their rent make one, and everybody else against a basket. |
| 2751 | 20 | `List<Household> belowTheRule(HouseholdBalance bal, FamilyModel families)` | The rows below the rule, in the grid's order: the retired, then everybody outside the families. |
| 2773 | 4 | `static String gridKey(Household c)` | The grid's key for a cell: "tier:SHAPE" for a family, "OUT:" and its ledger's key for the rest. |
| 2785 | 31 | `List<Pieces.Step> cityMonth()` | The city's month, as the waterfall's steps, in dollars: the wages, what comes off them and what is added, take-home, what it is spent on - the bank's account fees a step of their own (0.7.27; the statement left them o... |
| 2818 | 4 | `static String signedMoney(double dollars)` | Money on the waterfall, signed and compact: "−$36.8M". |

### HOUSEHOLD MONEY, DRAWN (lines 2823-3580)

| line | len | member | says |
|---:|---:|---|---|
| 2845 | 62 | `void showHouseholdMenu()` | The residents' own books - the last participant in this economy that did not have any. |
| 2909 | 7 | `VBox quietBooks()` | With no cell open, the books' place says what goes there. |
| 2923 | 69 | **type** `static final class Books extends javafx.scene.layout.Pane` | The grid and the open cell's books: side by side at BESIDE and wider - the grid at its own width, the books in the rest, held at the top of the view while the grid scrolls past - and the books under the grid below that. |
| 2927 | 5 | `Books(VBox grid, javafx.scene.Node panel)` _(in PeopleScreen.Books)_ |  |
| 2933 | 1 | `boolean beside(double w)` _(in PeopleScreen.Books)_ |  |
| 2936 | 3 | `public javafx.geometry.Orientation getContentBias()` _(in PeopleScreen.Books)_ | Its height follows its width: beside or stacked. |
| 2940 | 8 | `protected double computePrefHeight(double width)` _(in PeopleScreen.Books)_ |  |
| 2949 | 1 | `protected double computeMinHeight(double width)` _(in PeopleScreen.Books)_ |  |
| 2951 | 1 | `protected double computePrefWidth(double height)` _(in PeopleScreen.Books)_ |  |
| 2953 | 15 | `protected void layoutChildren()` _(in PeopleScreen.Books)_ |  |
| 2970 | 13 | `private double held(double panelHeight)` _(in PeopleScreen.Books)_ | How far down the books sit so their top stays at the top of the view, within the grid's height. |
| 2985 | 6 | `static javafx.scene.control.ScrollPane scrollerOf(javafx.scene.Node n)` _(in PeopleScreen.Books)_ | The scroll pane this sits in - up through its skin's viewport - or null. |
| 2994 | 11 | `void hookBooks()` | The menu's scroll moves the books with it: one listener, for the window's life, laying the books out again. |
| 3014 | 23 | `void reveal()` | THE GRID AND ITS BOOKS ARE WHAT GETS REVEALED, when they stack: the top of the grid to the top of the view and the panel landing under it, both on screen at once - or, when they do not both fit, the panel's foot, whic... |
| 3039 | 11 | `VBox cityMonthCard(HouseholdAccounts hh)` | THE CITY'S MONTH: the waterfall, what was saved since the founding, and the statement behind details. |
| 3052 | 50 | `VBox cityStatement(HouseholdAccounts hh)` | The city's month as the statement it was, with the bank's account fees on a line of their own (0.7.27). |
| 3110 | 6 | `static VBox statCard(String caption, String value, String tone, String line, String info)` | A small figure card: a caption, the figure, a line, and its (i). |
| 3118 | 19 | `javafx.scene.layout.GridPane putByCards(HouseholdBalance bal)` | WHAT THEY HAVE PUT BY: saved, owed, the month's dividends, and the cars. |
| 3139 | 33 | `List<javafx.scene.Node> putByChips(HouseholdBalance bal, FamilyModel families)` | The two chips under the cards: who is at the credit ceiling, and who flatshares because one wage won't cover a home - each opens the cell. |
| 3181 | 107 | `VBox affordabilityGrid(HouseholdAccounts hh, HouseholdBalance bal, FamilyModel families)` | Household shape down the side, pay tier across the top, and in every cell what that household has left at the end of the month. |
| 3290 | 14 | `int gridSectionRow(javafx.scene.layout.GridPane grid, String caption, int line)` | A rule and a caption across the matrix, with the (i) that says who is below it: a different kind of household. |
| 3325 | 58 | `int otherRows(javafx.scene.layout.GridPane grid, HouseholdAccounts hh, HouseholdBalance bal, FamilyModel families, int line)` | The retired, the out of work, the students, the orphans and the prison, as rows of the same matrix. |
| 3385 | 12 | `int otherRow(javafx.scene.layout.GridPane grid, String label, Household own, double perHousehold, double homes, int line)` | One matrix row for a household with no tier: a name, then one wide cell. |
| 3416 | 51 | `javafx.scene.Node wideCell(Household own, double perHousehold, double homes)` | cashCell's cell, one row wide: the same tint, the same ring, the same click - with the headcount inside it, because the six tier columns it spans have nothing of their own to say about this household. |
| 3476 | 45 | `Label cashCell(HouseholdAccounts hh, FamilyModel families, FamilyStructure shape, PayTier tier, int tierIndex)` | One cell: what this household has left, and how badly. |
| 3534 | 46 | `VBox basketBlock(Household cell)` | What a full basket would have been, what they ate, and where the difference came from. |

### THE MONEY BLOCKS BOTH PANELS SHARE (lines 3581-4175)

| line | len | member | says |
|---:|---:|---|---|
| 3600 | 4 | `String costMoney(double dollars)` | A cost, printed negative - but a cost of nothing reads "$0", never "-$0". |
| 3606 | 7 | `static String stakePct(double share)` | A stake, with enough decimals left on it to still say something. |
| 3621 | 7 | `static String shareOf(double part, double whole)` | What part is of whole - or a dash, when there is no whole to be part of. |
| 3630 | 6 | `static String monthsRun(double months)` | A run of months, said the way a person would say it. |
| 3638 | 6 | `Label panelBlockHead(String text)` | The small heading that divides a statement panel into blocks. |
| 3646 | 17 | `Label panelWho(HouseholdBalance bal, Household cell)` | How many of them there are, how big each one is, and how much of the city that is. |
| 3673 | 228 | `void positionBlock(VBox panel, HouseholdBalance bal, Household own)` | What one of these households HAS, what it OWES, what it OWNS and what all of that leaves it worth. |
| 3910 | 31 | `void ratioBlock(VBox panel, Household own, double income, double tax, String billsLabel, double bills, double fees, double shop...` | The same month again, as shares rather than figures. |
| 3951 | 38 | `VBox outsideStatement(HouseholdBalance bal)` | One of the people outside the families, and what a month does to them. |
| 3998 | 177 | `VBox openStatement(HouseholdAccounts hh, HouseholdBalance bal, FamilyModel families)` | The month of whichever cell is open, in full. |

### THE TIER TABLE, AS A TABLE. (lines 4176-4280)

| line | len | member | says |
|---:|---:|---|---|
| 4219 | 26 | `HBox flowBar(double tax, double rent, double fees, double shops, double left, double width)` | Where a household's month went, as one bar. |
| 4247 | 10 | `Region barPart(double width, String colour, String what)` | One segment. |
| 4259 | 21 | `HBox flowKey()` | The key under the bar, so the colours mean something the first time. |


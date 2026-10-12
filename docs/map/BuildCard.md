# BuildCard.java - 1,335 lines · 67 methods · 13 constants · model

`ham/citybuildersim/BuildCard.java` - generated 2026-10-12 by CodeMap; line numbers are as of that run.

> One build card's figures, for every one of the buildings in buildings.json
> (0.7.25; 129 of them since 0.7.133): what
> it gives the city and in what unit, its price per unit of that, the scarce
> resource it takes, the group it is compared within and where in that group
> it stands, what one costs to run, the verdict on an order of it - and, for
> the buildings investors put up, what they last decided and the first gate
> this one fails for them now.
> 
> WHY. Jerus, after seeing 0.7.24: "also the build card for every building,
> i think the card itself needs a redesign dont you think?" The city's five
> categories had a card that answered "which of these, and what does it give
> me"; the market's nine kept the 0.7.21 card, a name, two prices and a
> stepper. One card for all 73 now (the project's design note for 0.7.25,
> from the design study card-spec-0725): a hero that says what the building
> gives the city, a money bar, a bar for the category's scarce resource, the
> investors' line, what it needs, and the order. The figures moved out of
> the screen into this class so a harness (BuildCardCheck) can hold them
> without the toolkit; the screen keeps the words that carry money (it
> formats money the way every screen does) and the layout.
> 
> Pure: it reads the city and changes nothing. Every figure is a model read,
> or a ratio of two, and each is named where it is made below.

**Uses:** [BuildAdvice](BuildAdvice.md) (46), [BuildingsTemplate](BuildingsTemplate.md) (38), [Game](Game.md) (37), [Good](Good.md) (22), [Sector](Sector.md) (20), [Sectors](Sectors.md) (16), [Refining](Refining.md) (9), [JobType](JobType.md) (8), [CareType](CareType.md) (7), [BuildingType](BuildingType.md) (7), [Resource](Resource.md) (7), [StrategicReserve](StrategicReserve.md) (5), [Ports](Ports.md) (5), [Oil](Oil.md) (5), [Retail](Retail.md) (4), [LandManager](LandManager.md) (4), [BuildingsStacks](BuildingsStacks.md) (3), [ConstructionControl](ConstructionControl.md) (2), [BuildingManager](BuildingManager.md) (2), [Bank](Bank.md) (2), [RefineryFlow](RefineryFlow.md) (1), [Formats](Formats.md) (1), [BusinessInvestment](BusinessInvestment.md) (1)

**Used by (15):** [BuildAdvice](BuildAdvice.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [ChildcareCheck](ChildcareCheck.md), [OilCheck](OilCheck.md), [PortCheck](PortCheck.md), [ReadPathCheck](ReadPathCheck.md), [RoadCheck](RoadCheck.md), [SectorFlow](SectorFlow.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [SectorStatementCheck](SectorStatementCheck.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 36 | WHAT KIND OF CARD |
| 124 | THE CITY'S WORDS (0.7.24, moved here from the screen in 0.7.25) |
| 211 | THE MARKET'S GROUPS |
| 341 | VALUE ADDED |
| 434 | ONE CARD |
| 690 | A GROUP OF CARDS, SCALED AND TAGGED |
| 846 | THE INVESTORS' LINE (market cards) |
| 1052 | EVERY GATE (0.7.75, the sector statements' R4) |
| 1165 | THE WORD'S KIND (0.7.30) |
| 1256 | ONE SECTOR'S INVESTORS (0.7.30) |
| 1310 | THE VERDICT ON AN ORDER |

## Enum constants

| line | constant | says |
|---:|---|---|
| 63 | `BuildCard.Kind.CITY` |  |
| 63 | `BuildCard.Kind.HOMES` |  |
| 63 | `BuildCard.Kind.CUSTOMERS` |  |
| 63 | `BuildCard.Kind.MEALS` |  |
| 63 | `BuildCard.Kind.BRANCH` |  |
| 63 | `BuildCard.Kind.RAIL` |  |
| 63 | `BuildCard.Kind.POINTS` |  |
| 63 | `BuildCard.Kind.MAKER` |  |
| 63 | `BuildCard.Kind.OFFICE` |  |
| 63 | `BuildCard.Kind.PUMP` |  |
| 63 | `BuildCard.Kind.TANKS` |  |
| 63 | `BuildCard.Kind.BERTHS` |  |
| 63 | `BuildCard.Kind.SLOTS` |  |
| 63 | `BuildCard.Kind.PIPE` |  |
| 71 | `BuildCard.Bar2.LAND` |  |
| 71 | `BuildCard.Bar2.UNFILLED` |  |
| 71 | `BuildCard.Bar2.UNSTAFFABLE` |  |
| 71 | `BuildCard.Bar2.NONE` |  |
| 716 | `BuildCard.NoteKind.DOORS` |  |
| 716 | `BuildCard.NoteKind.SHOPS` |  |
| 716 | `BuildCard.NoteKind.MADE` |  |
| 716 | `BuildCard.NoteKind.RAIL` |  |
| 716 | `BuildCard.NoteKind.LUXURY` |  |
| 716 | `BuildCard.NoteKind.MEALS` |  |
| 716 | `BuildCard.NoteKind.PUMP` |  |
| 716 | `BuildCard.NoteKind.NONE` |  |
| 886 | `BuildCard.GateKind.DEPOSIT` |  |
| 886 | `BuildCard.GateKind.LICENCE` |  |
| 886 | `BuildCard.GateKind.STAFFING` |  |
| 886 | `BuildCard.GateKind.LAND` |  |
| 886 | `BuildCard.GateKind.LOSS` |  |
| 886 | `BuildCard.GateKind.INTEREST` |  |
| 1072 | `BuildCard.Mark.PASS` |  |
| 1072 | `BuildCard.Mark.FAIL` |  |
| 1072 | `BuildCard.Mark.NONE` |  |
| 1196 | `BuildCard.WordKind.BUILDING` |  |
| 1196 | `BuildCard.WordKind.SELLING` |  |
| 1196 | `BuildCard.WordKind.LAND` |  |
| 1196 | `BuildCard.WordKind.STAFF` |  |
| 1196 | `BuildCard.WordKind.LICENCE` |  |
| 1196 | `BuildCard.WordKind.ORE` |  |
| 1196 | `BuildCard.WordKind.SUPPLY` |  |
| 1196 | `BuildCard.WordKind.CREDIT` |  |
| 1196 | `BuildCard.WordKind.MONEY` |  |
| 1196 | `BuildCard.WordKind.ENOUGH` |  |
| 1196 | `BuildCard.WordKind.NONE` |  |
| 1196 | `BuildCard.WordKind.OTHER` |  |
| 1319 | `BuildCard.VerdictKind.NO_DEPOSIT` |  |
| 1319 | `BuildCard.VerdictKind.NO_LICENCE` |  |
| 1319 | `BuildCard.VerdictKind.NO_LAND` |  |
| 1319 | `BuildCard.VerdictKind.BILL` |  |
| 1319 | `BuildCard.VerdictKind.MONTHS` |  |
| 1319 | `BuildCard.VerdictKind.NO_COAST` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 143 | `BuildCard.KILOWATTS` | `" kW"` | The words after a power plant's figure (0.7.28): kilowatts, the model's unit and a rate - "units a month" until then, which was neither. |
| 233 | `BuildCard.GROUP_ORDER` | `{ "Homes", "Groceries", "Filling stations", "The bank's branches", "Food mill...` | The market groups, in the order a page lays them out. |
| 439 | `BuildCard.ROAD_PER` | `"trip over " + BuildAdvice.LIFE_MONTHS / 12 + " years, with its land"` | What a road card's first bar is per (0.7.70): a trip it takes off the road, over its life, its ground in it - BuildAdvice.lifetime() over BuildAdvice.unit(); the bar's (i) says the rest. |
| 904 | `BuildCard.DRY_OIL` | `"dry oil"` | A DEPOSIT gate's word for a land Oil Well (0.7.103): its count is of dry sites (Game.sitesFor()). |
| 907 | `BuildCard.NO_FEED` | `"no feed: no crude unit stands to run its stream"` | What a conversion unit's card says in place of a loss with no crude unit standing to feed it (0.7.103). |
| 1199 | `BuildCard.LAND_WORDS` | `{ "no land", "short of homes" }` | The phrases each kind is read by, in lower case; a word starting "Could not build" is LAND as well, and one starting "Declined" is MONEY unless CREDIT took it. |
| 1201 | `BuildCard.STAFF_WORDS` | `{ "could staff" }` | ...STAFF's. |
| 1203 | `BuildCard.LICENCE_WORDS` | `{ "licence", "licences" }` | ...LICENCE's: whole words, so the plural as well. |
| 1205 | `BuildCard.ORE_WORDS` | `{ "deposit", "ore" }` | ...ORE's. |
| 1207 | `BuildCard.SUPPLY_WORDS` | `{ "fabricates", "will not sell" }` | ...SUPPLY's. |
| 1209 | `BuildCard.CREDIT_WORDS` | `{ "borrowing ban", "bank", "default point", "down payment", "lender", "mortga...` | ...CREDIT's. |
| 1211 | `BuildCard.MONEY_WORDS` | `{ "below cost", "no margin", "worth sinking", "worth building", "cost more th...` | ...MONEY's. |
| 1215 | `BuildCard.ENOUGH_WORDS` | `{ "ahead of", "already", "covers all", "months of work queued", "months of wo...` | ...ENOUGH's. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 32 | 1304 | **type** `public final class BuildCard` | One build card's figures, for every one of the buildings in buildings.json (0.7.25; 129 of them since 0.7.133): what it gives the city and in what unit, its price per unit of that, the scarce resource it takes, the gr... |
| 34 | 1 | `private BuildCard()` |  |

### WHAT KIND OF CARD (lines 36-123)

| line | len | member | says |
|---:|---:|---|---|
| 63 | 1 | **type** `public enum Kind` | CITY: the picked ring's measure. |
| 71 | 1 | **type** `public enum Bar2` | What bar 2 measures: LAND per unit (the market's scarce resource), UNFILLED posts per 10,000 served (the city's, Jerus's pick in 0.7.24), UNSTAFFABLE posts of 100 (offices, whose three cards differ only in who they em... |
| 105 | 18 | **type** `public record Figures(BuildingsTemplate template, Kind kind, String group, String verb, double figure, Stri...` | One card's figures. |
| 115 | 1 | `public boolean addsNothing()` _(in BuildCard.Figures)_ | No positive unit to price against: a maker whose inputs cost more than it makes, or a city building nobody could staff. |
| 118 | 1 | `public boolean inMonths()` _(in BuildCard.Figures)_ | Measured in months of what it adds or exports, not a price per unit. |
| 121 | 1 | `public boolean market()` _(in BuildCard.Figures)_ | Built by investors as well as by the city - not the city's own Strategic Reserve (0.7.85) or sea terminals (0.7.86), which only the city builds. |

### THE CITY'S WORDS (0.7.24, moved here from the screen in 0.7.25) (lines 124-210)

| line | len | member | says |
|---:|---:|---|---|
| 146 | 16 | `public static String[] doesWords(BuildAdvice.Measure m, BuildingsTemplate t)` | The words before the figure and after it, for a city building on a measure. |
| 164 | 13 | `public static String perWords(BuildAdvice.Measure m)` | ...the one of it a cost is per: "cost per patient", "cheapest per child". |
| 179 | 13 | `public static String perPlural(BuildAdvice.Measure m)` | ...and many of it, for the staff bar's "per 10,000 patients". |
| 198 | 4 | `public static double served(Game game, BuildAdvice.Measure m, BuildingsTemplate t)` | What one serves at today's staffing, in the measure's own unit (BuildAdvice.unit()); for road capacity the road's own capacity, since BuildAdvice.unit() is the advice's trips relieved. |
| 204 | 6 | `public static BuildAdvice.Measure cityMeasure(BuildingsTemplate t)` | The first ring of its category a city building serves - its group on a page that has none picked. |

### THE MARKET'S GROUPS (lines 211-340)

| line | len | member | says |
|---:|---:|---|---|
| 246 | 41 | `public static String group(BuildingsTemplate t)` | The heading a building is compared under: a market building's group by its owning sector, or, for a city building, the first ring of its category it serves. |
| 294 | 6 | `public static String groupOf(Game game, Sector sector)` | The group a sector's own buildings are compared under on Build (0.7.30): its first own building's - "Food mills" for the sector called Industry, which bakes. |
| 302 | 3 | `public static boolean citys(BuildingsTemplate t)` | A building only the city builds and nobody's investors weigh: a city category's, and the city's Strategic Reserve (0.7.85) and sea terminals (0.7.86) on a market page. |
| 307 | 8 | `public static String categoryOf(Game game, Sector sector)` | ...and the Build category that group is on: "Industry"; null with none. |
| 317 | 4 | `public static int groupRank(String group)` | Where a market group sits in Build's order (GROUP_ORDER); after them all for one not in it. |
| 323 | 17 | `public static Kind kindOf(BuildingsTemplate t)` | What a building is measured in (see Kind). |

### VALUE ADDED (lines 341-433)

| line | len | member | says |
|---:|---:|---|---|
| 362 | 5 | `public static double priceOf(Game game, Good g)` | The price a good is valued at for value added, or NaN for one with no market price. |
| 377 | 24 | `public static double valueAdded(Game game, BuildingsTemplate t)` | What one makes less what it uses a month, at today's prices - what it makes being a crude unit's slate since 0.7.76 (sectors.Refining.madeBy()), its template naming only the crude. |
| 403 | 3 | `public static String feedWords(BuildingsTemplate t)` | A conversion unit's feed in words, after its figure (0.7.80): " litres of heavy naphtha a month". |
| 413 | 7 | `public static String goodWords(Good g)` | A good after its count: " t of steel", " kg of bread", " units of building materials", " seat-months of support work" - and for a good counted in itself, " cars", " vans and trucks", " wagon sets of rolling stock". |
| 422 | 5 | `static String makerVerb(BuildingsTemplate t)` | The verb a maker's hero opens on: a farm grows, a mine lifts, the rest make. |
| 429 | 4 | `static Good firstGood(BuildingsTemplate t)` | The good a maker's or an office's hero names: the first it makes. |

### ONE CARD (lines 434-689)

| line | len | member | says |
|---:|---:|---|---|
| 451 | 2 | **type** `public record Paving(int paveable, int paving, Game.BuildQuote one, double unit, double frees, double perTr...` | PAVING, ON THE GRAVEL ROAD'S CARD (0.7.70; ConstructionControl, F): the gravel roads the city could pave and those being paved, the quote for one (Game.quotePave()), the trips one paving takes off the road (BuildAdvic... |
| 454 | 14 | `public static Paving paving(Game game, BuildingsTemplate t)` |  |
| 470 | 10 | `public static double runningCost(Game game, BuildingsTemplate t)` | What one costs to keep a month, which is not what it costs to buy: its upkeep and its posts at today's wages (PopulationManager.getWagesPerType()). |
| 482 | 4 | `public static Sector ownerOf(Game game, BuildingsTemplate t)` | The sector whose money and staffing test a market building is judged by: its owner, or retail for the bank's branch (planBank() asks retail). |
| 491 | 198 | `public static Figures of(Game game, BuildingsTemplate t, BuildAdvice.Measure m)` | One card's figures. |

### A GROUP OF CARDS, SCALED AND TAGGED (lines 690-845)

| line | len | member | says |
|---:|---:|---|---|
| 716 | 1 | **type** `public enum NoteKind` | A note under a group's heading, by kind: DOORS {studios' households without a door, families'} (RealEstate.doorShortfall(); a negative is doors to spare); SHOPS {the shops' coverage a month, the people}; MADE {the cit... |
| 718 | 1 | **type** `public record Note(NoteKind kind, double a, double b, Good good)` |  |
| 721 | 38 | **type** `public record Group(String title, List<Figures> cards, Note note)` | One group: its heading, its cards in catalogue order, and its note. |
| 724 | 1 | `public boolean track()` _(in BuildCard.Group)_ | More than one card, so the bars have something to compare and draw a track. |
| 726 | 1 | `public double most1()` _(in BuildCard.Group)_ |  |
| 727 | 1 | `public double least1()` _(in BuildCard.Group)_ |  |
| 728 | 1 | `public double most2()` _(in BuildCard.Group)_ |  |
| 729 | 1 | `public double least2()` _(in BuildCard.Group)_ |  |
| 731 | 9 | `private double extreme(boolean first, boolean most)` _(in BuildCard.Group)_ |  |
| 742 | 1 | `public double share1(Figures f)` _(in BuildCard.Group)_ | How much of the track a card's first bar fills: its figure over the group's largest; full for an infinite one, none for one with none. |
| 743 | 1 | `public double share2(Figures f)` _(in BuildCard.Group)_ |  |
| 745 | 5 | `private static double share(double v, double most)` _(in BuildCard.Group)_ |  |
| 752 | 1 | `public boolean best1(Figures f)` _(in BuildCard.Group)_ | The tag on bar 1: the strict best of two or more. |
| 753 | 1 | `public boolean best2(Figures f)` _(in BuildCard.Group)_ |  |
| 755 | 3 | `private boolean best(double v, double least, double most)` _(in BuildCard.Group)_ |  |
| 765 | 21 | `public static List<Group> groups(Game game, String category, BuildAdvice.Measure m)` | A page's groups. |
| 788 | 36 | `static Note note(Game game, List<Figures> cards)` | The note under a market group's heading (see NoteKind). |
| 833 | 7 | `public static Note noteOf(Game game, Sector sector)` | The note of the group a sector's own buildings are compared under on Build (0.7.30): the Sectors screen's flow reads a seller-priced good's capacity off it - the shops' coverage, the counters', the kitchens' seats, th... |
| 842 | 3 | `static double counted(Game game, Sector sector, double flow)` | A month's flow the save does not carry, or NaN while no month has run since the load (Game.hasMonthRun(); until 0.7.102, while the sector had no word for the month - the word is saved since). |

### THE INVESTORS' LINE (market cards) (lines 846-1051)

| line | len | member | says |
|---:|---:|---|---|
| 879 | 5 | `public static String slot(BuildingsTemplate t)` | Where Game files a building's word: the branch under "Bank" (Game.consider()'s label), every other under its sector. |
| 886 | 1 | **type** `public enum GateKind` | The first gate a building fails for investors now: the last, INTEREST, is consider()'s brake on its whole cost (0.7.122). |
| 889 | 3 | `public static String depositWord(Resource r)` | The resource a deposit refusal names (0.7.62): "iron" for an Iron Mine (and for none), "oil" for an Oil Well. |
| 910 | 3 | `public static String siteCountWord(BuildingsTemplate t)` | The word a building's site count carries (0.7.103): " dry" for a land Oil Well, whose count is the dry sites alone; "" for any other. |
| 915 | 3 | `static String depositWordFor(BuildingsTemplate t, Resource r)` | A DEPOSIT gate's word: depositWord(), or DRY_OIL for a land Oil Well (0.7.103). |
| 920 | 5 | `public static boolean unfed(Game game, BuildingsTemplate t)` | Whether a building is a refinery's conversion unit with no crude unit standing to feed it (0.7.103): its estimate is a loss only for want of a stream. |
| 933 | 5 | `static Gate landGate(LandManager ground, BuildingsTemplate t, double free)` | The LAND gate (0.7.109, batch Z2): the ground one needs against the ground its sector may build on (LandManager.freeFor(): free for anyone, and its zone's), and in `why` what the other sectors' zones hold (BusinessInv... |
| 940 | 3 | `static Gate lossGate(Game game, BuildingsTemplate t, double estimate)` | The LOSS gate: what it would lose, worded NO_FEED for a conversion unit nothing feeds (0.7.103). |
| 945 | 3 | `static boolean asksInterest(Game game, BuildingsTemplate t, Sector owner)` | Whether investors ask a building the INTEREST gate (0.7.122): every order but a landlord's home, which the mortgage lender's test decides. |
| 960 | 6 | `static Gate interestGate(Game game, BuildingsTemplate t, Sector owner, double estimate)` | The INTEREST gate (0.7.122, batch C1): consider()'s test of an order of one, asked through the model's door - Game.interestTest(), whose answer is carriesItsWholeCost()'s - with its figures {what one would earn a mont... |
| 968 | 4 | `static String banWords(Game game, Sector owner)` | What an INTEREST gate says under a borrowing ban, in consider()'s words: "borrowing ban, 7 more months - it would need credit". |
| 986 | 5 | **type** `public record Gate(GateKind kind, double a, double b, JobType licence, String why, Game.InterestTest test)` | One gate: DEPOSIT {deposits the city owns, mines committed} with the resource's word in `why` (depositWord(); DRY_OIL for a land Oil Well since 0.7.103); LICENCE {licences one needs, spare licences} with the licence; ... |
| 989 | 1 | `public Gate(GateKind kind, double a, double b, JobType licence, String why)` _(in BuildCard.Gate)_ | Every gate but INTEREST: no test of its own. |
| 1005 | 6 | **type** `public record Investors(String building, String slot, Sector owner, boolean theirs, boolean yoursToo, int o...` | The investors' line's figures. |
| 1009 | 1 | `public boolean showOwn()` _(in BuildCard.Investors)_ | "this one:" is said when there is a gate, no investor is already building it, and the word does not already name it. |
| 1012 | 17 | `public static Investors investors(Game game, BuildingsTemplate t)` |  |
| 1031 | 20 | `static Gate gate(Game game, BuildingsTemplate t, Sector owner, double estimate)` | The first gate investors would stop this building at now, in buildStack()'s order and then the planners': null when it passes them all. |

### EVERY GATE (0.7.75, the sector statements' R4) (lines 1052-1164)

| line | len | member | says |
|---:|---:|---|---|
| 1072 | 1 | **type** `public enum Mark` | A gate's answer for one building: it passes, it fails, or it is not a gate this building has. |
| 1075 | 1 | **type** `public record GateMark(GateKind kind, Mark mark, Gate failure)` | One gate's answer: its kind (LOSS is "it pays", INTEREST "it carries its interest on its whole cost"), the mark, and gate()'s figures when it fails. |
| 1088 | 22 | **type** `public record Appraisal(BuildingsTemplate template, Sector owner, List<GateMark> gates, double estimate, do...` | One building at every gate (R4). |
| 1092 | 4 | `public Gate first()` _(in BuildCard.Appraisal)_ | The first gate it fails - gate()'s answer - or null when it passes them all. |
| 1098 | 5 | `public List<GateMark> failures()` _(in BuildCard.Appraisal)_ | Every gate it fails, in order. |
| 1105 | 4 | `public Mark at(GateKind kind)` _(in BuildCard.Appraisal)_ | Its mark at one gate. |
| 1112 | 42 | `public static Appraisal appraise(Game game, BuildingsTemplate t)` | Every gate investors would ask this building at now, and what one would earn, cost and be paid for with. |
| 1156 | 8 | `public static List<Appraisal> appraiseAll(Game game, Sector sector)` | Every market building one sector owns, appraised at every gate, in the catalogue's order: the investor report's (R4). |

### THE WORD'S KIND (0.7.30) (lines 1165-1255)

| line | len | member | says |
|---:|---:|---|---|
| 1196 | 1 | **type** `public enum WordKind` | What a sector's word is about. |
| 1220 | 1 | `public static WordKind wordKind(String word)` | The kind of a word on its text alone. |
| 1227 | 15 | `public static WordKind wordKind(String word, boolean building, boolean landBlocked)` | ...with what the text cannot say: whether investors have an order on site of one of the sector's buildings (`building`), and whether the month put it on Game.getLandBlockedSectors() (`landBlocked`). |
| 1244 | 11 | `static boolean says(String text, String[] phrases)` | Whether any of the phrases is in the text as whole words: no letter or digit either side of it. |

### ONE SECTOR'S INVESTORS (0.7.30) (lines 1256-1309)

| line | len | member | says |
|---:|---:|---|---|
| 1279 | 8 | **type** `public record SectorInvestors(Sector owner, String word, WordKind kind, List<Investors> buildings, boolean ...` | One sector's investors. |
| 1283 | 3 | `public Investors line()` _(in BuildCard.SectorInvestors)_ | The sector's line as a build card's line is worded: its own name, its word, no gate of its own. |
| 1288 | 21 | `public static SectorInvestors sectorInvestors(Game game, Sector sector)` |  |

### THE VERDICT ON AN ORDER (lines 1310-1335)

| line | len | member | says |
|---:|---:|---|---|
| 1319 | 1 | **type** `public enum VerdictKind` |  |
| 1322 | 3 | **type** `public record Verdict(VerdictKind kind, Game.BuildQuote quote, double figure, Resource site)` | The verdict, the quote it is on, and how short: sq ft of land, cash, or the months to wait - and, refused for a deposit, whose sites it needs (0.7.62; null otherwise). |
| 1323 | 1 | `public Verdict(VerdictKind kind, Game.BuildQuote quote, double figure)` _(in BuildCard.Verdict)_ |  |
| 1326 | 9 | `public static Verdict verdict(Game game, BuildingsTemplate t, int n)` |  |


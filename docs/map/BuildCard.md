# BuildCard.java - 1,893 lines · 91 methods · 14 constants · model

`ham/citybuildersim/BuildCard.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> One build card's figures, for every one of the buildings in buildings.json
> (0.7.25; 143 of them since FR3, the Bulk Berth): what
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
> Since 0.7.142 (batch I13) a materials building's card says where it
> stands, how its sector weighs it, its own power, the old metal it buys,
> who here buys what it makes and a crew's E (THE MATERIALS' CARDS), and
> the nine industries' pages end on the same chain (chainLines()).
> 
> Pure: it reads the city and changes nothing. Every figure is a model read,
> or a ratio of two, and each is named where it is made below.

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (64), [Game](Game.md) (58), [BuildAdvice](BuildAdvice.md) (50), [Sector](Sector.md) (40), [Good](Good.md) (33), [Sectors](Sectors.md) (19), [Forestry](Forestry.md) (10), [Refining](Refining.md) (9), [JobType](JobType.md) (9), [Resource](Resource.md) (9), [StrategicReserve](StrategicReserve.md) (8), [CareType](CareType.md) (7), [Ports](Ports.md) (7), [BuildingType](BuildingType.md) (7), [Oil](Oil.md) (5), [LandManager](LandManager.md) (5), [ProcessingSector](ProcessingSector.md) (5), [SpreadPlanner](SpreadPlanner.md) (5), [Retail](Retail.md) (4), [BuildingManager](BuildingManager.md) (4), [Formats](Formats.md) (3), [BuildingsStacks](BuildingsStacks.md) (3), [ExtractionSector](ExtractionSector.md) (3), [ConstructionControl](ConstructionControl.md) (2), [Bank](Bank.md) (2), [Haulage](Haulage.md) (2), [BusinessInvestment](BusinessInvestment.md) (2), [Deposit](Deposit.md) (2), [CoalMining](CoalMining.md) (2), [BridgeSurvey](BridgeSurvey.md) (2)... and 10 more

**Used by (29):** [BridgeCheck](BridgeCheck.md), [BuildAdvice](BuildAdvice.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [CementConcrete](CementConcrete.md), [ChildcareCheck](ChildcareCheck.md), [CoalMining](CoalMining.md), [CopperMining](CopperMining.md), [CopperRefining](CopperRefining.md), [Forestry](Forestry.md), [HeavyIndustry](HeavyIndustry.md), [HydroCheck](HydroCheck.md), [LandmassCheck](LandmassCheck.md), [Mining](Mining.md), [OilCheck](OilCheck.md), [PortCheck](PortCheck.md), [Quarrying](Quarrying.md), [ReadPathCheck](ReadPathCheck.md), [RoadCheck](RoadCheck.md), [RunWorksCheck](RunWorksCheck.md), [SectorFlow](SectorFlow.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [SectorStatementCheck](SectorStatementCheck.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md), [WoodProducts](WoodProducts.md), [ZoningScreen](ZoningScreen.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 41 | WHAT KIND OF CARD |
| 129 | THE CITY'S WORDS (0.7.24, moved here from the screen in 0.7.25) |
| 219 | THE MARKET'S GROUPS |
| 376 | VALUE ADDED |
| 469 | ONE CARD |
| 736 | A BERTH'S PAYBACK AND THE DEPOT'S LINE (FR4, 2026-10-20; the study's |
| 773 | A GROUP OF CARDS, SCALED AND TAGGED |
| 950 | THE INVESTORS' LINE (market cards) |
| 1200 | EVERY GATE (0.7.75, the sector statements' R4) |
| 1321 | THE MATERIALS' CARDS (0.7.142, batch I13; docs/studies/spec-materials.md |
| 1720 | THE WORD'S KIND (0.7.30) |
| 1812 | ONE SECTOR'S INVESTORS (0.7.30) |
| 1866 | THE VERDICT ON AN ORDER |

## Enum constants

| line | constant | says |
|---:|---|---|
| 68 | `BuildCard.Kind.CITY` |  |
| 68 | `BuildCard.Kind.HOMES` |  |
| 68 | `BuildCard.Kind.CUSTOMERS` |  |
| 68 | `BuildCard.Kind.MEALS` |  |
| 68 | `BuildCard.Kind.BRANCH` |  |
| 68 | `BuildCard.Kind.RAIL` |  |
| 68 | `BuildCard.Kind.POINTS` |  |
| 68 | `BuildCard.Kind.MAKER` |  |
| 68 | `BuildCard.Kind.OFFICE` |  |
| 68 | `BuildCard.Kind.PUMP` |  |
| 68 | `BuildCard.Kind.TANKS` |  |
| 68 | `BuildCard.Kind.BERTHS` |  |
| 68 | `BuildCard.Kind.SLOTS` |  |
| 68 | `BuildCard.Kind.PIPE` |  |
| 68 | `BuildCard.Kind.DEPOT` |  |
| 68 | `BuildCard.Kind.BRIDGE` |  |
| 76 | `BuildCard.Bar2.LAND` |  |
| 76 | `BuildCard.Bar2.UNFILLED` |  |
| 76 | `BuildCard.Bar2.UNSTAFFABLE` |  |
| 76 | `BuildCard.Bar2.NONE` |  |
| 799 | `BuildCard.NoteKind.DOORS` |  |
| 799 | `BuildCard.NoteKind.SHOPS` |  |
| 799 | `BuildCard.NoteKind.MADE` |  |
| 799 | `BuildCard.NoteKind.RAIL` |  |
| 799 | `BuildCard.NoteKind.LUXURY` |  |
| 799 | `BuildCard.NoteKind.MEALS` |  |
| 799 | `BuildCard.NoteKind.PUMP` |  |
| 799 | `BuildCard.NoteKind.NONE` |  |
| 996 | `BuildCard.GateKind.DEPOSIT` |  |
| 996 | `BuildCard.GateKind.LICENCE` |  |
| 996 | `BuildCard.GateKind.STAFFING` |  |
| 996 | `BuildCard.GateKind.LAND` |  |
| 996 | `BuildCard.GateKind.LOSS` |  |
| 996 | `BuildCard.GateKind.INTEREST` |  |
| 996 | `BuildCard.GateKind.LENDER` |  |
| 1220 | `BuildCard.Mark.PASS` |  |
| 1220 | `BuildCard.Mark.FAIL` |  |
| 1220 | `BuildCard.Mark.NONE` |  |
| 1752 | `BuildCard.WordKind.BUILDING` |  |
| 1752 | `BuildCard.WordKind.SELLING` |  |
| 1752 | `BuildCard.WordKind.LAND` |  |
| 1752 | `BuildCard.WordKind.STAFF` |  |
| 1752 | `BuildCard.WordKind.LICENCE` |  |
| 1752 | `BuildCard.WordKind.ORE` |  |
| 1752 | `BuildCard.WordKind.SUPPLY` |  |
| 1752 | `BuildCard.WordKind.CREDIT` |  |
| 1752 | `BuildCard.WordKind.MONEY` |  |
| 1752 | `BuildCard.WordKind.ENOUGH` |  |
| 1752 | `BuildCard.WordKind.NONE` |  |
| 1752 | `BuildCard.WordKind.OTHER` |  |
| 1875 | `BuildCard.VerdictKind.NO_DEPOSIT` |  |
| 1875 | `BuildCard.VerdictKind.NO_LICENCE` |  |
| 1875 | `BuildCard.VerdictKind.NO_LAND` |  |
| 1875 | `BuildCard.VerdictKind.BILL` |  |
| 1875 | `BuildCard.VerdictKind.MONTHS` |  |
| 1875 | `BuildCard.VerdictKind.NO_COAST` |  |
| 1875 | `BuildCard.VerdictKind.NO_RIVER` |  |
| 1875 | `BuildCard.VerdictKind.NO_BRIDGE` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 148 | `BuildCard.KILOWATTS` | `" kW"` | The words after a power plant's figure (0.7.28): kilowatts, the model's unit and a rate - "units a month" until then, which was neither. |
| 245 | `BuildCard.GROUP_ORDER` | `{ "Homes", "Groceries", "Filling stations", "The bank's branches", "Food mill...` | The market groups, in the order a page lays them out. |
| 474 | `BuildCard.ROAD_PER` | `"trip over " + BuildAdvice.LIFE_MONTHS / 12 + " years, with its land"` | What a road card's first bar is per (0.7.70): a trip it takes off the road, over its life, its ground in it - BuildAdvice.lifetime() over BuildAdvice.unit(); the bar's (i) says the rest. |
| 1014 | `BuildCard.DRY_OIL` | `"dry oil"` | A DEPOSIT gate's word for a land Oil Well (0.7.103): its count is of dry sites (Game.sitesFor()). |
| 1017 | `BuildCard.NO_FEED` | `"no feed: no crude unit stands to run its stream"` | What a conversion unit's card says in place of a loss with no crude unit standing to feed it (0.7.103). |
| 1463 | `BuildCard.NOTHING_TO_WASH` | `"no raw coal goes unwashed"` | What a washing plant's feed gate says while its sector's coking mines make no raw coal its plants do not wash (CoalMining.unwashed()). |
| 1755 | `BuildCard.LAND_WORDS` | `{ "no land", "short of homes" }` | The phrases each kind is read by, in lower case; a word starting "Could not build" is LAND as well, and one starting "Declined" is MONEY unless CREDIT took it. |
| 1757 | `BuildCard.STAFF_WORDS` | `{ "could staff" }` | ...STAFF's. |
| 1759 | `BuildCard.LICENCE_WORDS` | `{ "licence", "licences" }` | ...LICENCE's: whole words, so the plural as well. |
| 1761 | `BuildCard.ORE_WORDS` | `{ "deposit", "ore", "forest", "field", "fields", "worked out" }` | ...ORE's: the ground has none - and since 0.7.138 Forestry's, the forest it may cut (none, or too little for E); since 0.7.142 a field of its type and a resource worked out, named as itself ("no coking coal left in th... |
| 1763 | `BuildCard.SUPPLY_WORDS` | `{ "fabricates", "will not sell" }` | ...SUPPLY's. |
| 1765 | `BuildCard.CREDIT_WORDS` | `{ "borrowing ban", "bank", "default point", "down payment", "lender", "mortga...` | ...CREDIT's. |
| 1767 | `BuildCard.MONEY_WORDS` | `{ "below cost", "no margin", "worth sinking", "worth building", "cost more th...` | ...MONEY's. |
| 1771 | `BuildCard.ENOUGH_WORDS` | `{ "ahead of", "already", "covers all", "months of work queued", "months of wo...` | ...ENOUGH's. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 37 | 1857 | **type** `public final class BuildCard` | One build card's figures, for every one of the buildings in buildings.json (0.7.25; 143 of them since FR3, the Bulk Berth): what it gives the city and in what unit, its price per unit of that, the scarce resource it t... |
| 39 | 1 | `private BuildCard()` |  |

### WHAT KIND OF CARD (lines 41-128)

| line | len | member | says |
|---:|---:|---|---|
| 68 | 1 | **type** `public enum Kind` | CITY: the picked ring's measure. |
| 76 | 1 | **type** `public enum Bar2` | What bar 2 measures: LAND per unit (the market's scarce resource), UNFILLED posts per 10,000 served (the city's, Jerus's pick in 0.7.24), UNSTAFFABLE posts of 100 (offices, whose three cards differ only in who they em... |
| 110 | 18 | **type** `public record Figures(BuildingsTemplate template, Kind kind, String group, String verb, double figure, Stri...` | One card's figures. |
| 120 | 1 | `public boolean addsNothing()` _(in BuildCard.Figures)_ | No positive unit to price against: a maker whose inputs cost more than it makes, or a city building nobody could staff. |
| 123 | 1 | `public boolean inMonths()` _(in BuildCard.Figures)_ | Measured in months of what it adds or exports, not a price per unit. |
| 126 | 1 | `public boolean market()` _(in BuildCard.Figures)_ | Built by investors as well as by the city - not the city's own Strategic Reserve (0.7.85) or sea terminals (0.7.86), which only the city builds. |

### THE CITY'S WORDS (0.7.24, moved here from the screen in 0.7.25) (lines 129-218)

| line | len | member | says |
|---:|---:|---|---|
| 151 | 17 | `public static String[] doesWords(BuildAdvice.Measure m, BuildingsTemplate t)` | The words before the figure and after it, for a city building on a measure. |
| 170 | 14 | `public static String perWords(BuildAdvice.Measure m)` | ...the one of it a cost is per: "cost per patient", "cheapest per child". |
| 186 | 14 | `public static String perPlural(BuildAdvice.Measure m)` | ...and many of it, for the staff bar's "per 10,000 patients". |
| 206 | 4 | `public static double served(Game game, BuildAdvice.Measure m, BuildingsTemplate t)` | What one serves at today's staffing, in the measure's own unit (BuildAdvice.unit()); for road capacity the road's own capacity, since BuildAdvice.unit() is the advice's trips relieved. |
| 212 | 6 | `public static BuildAdvice.Measure cityMeasure(BuildingsTemplate t)` | The first ring of its category a city building serves - its group on a page that has none picked. |

### THE MARKET'S GROUPS (lines 219-375)

| line | len | member | says |
|---:|---:|---|---|
| 261 | 52 | `public static String group(BuildingsTemplate t)` | The heading a building is compared under: a market building's group by its owning sector, or, for a city building, the first ring of its category it serves. |
| 320 | 6 | `public static String groupOf(Game game, Sector sector)` | The group a sector's own buildings are compared under on Build (0.7.30): its first own building's - "Food mills" for the sector called Industry, which bakes. |
| 328 | 3 | `public static boolean citys(BuildingsTemplate t)` | A building only the city builds and nobody's investors weigh: a city category's, and the city's Strategic Reserve (0.7.85) and sea terminals (0.7.86) on a market page. |
| 333 | 8 | `public static String categoryOf(Game game, Sector sector)` | ...and the Build category that group is on: "Industry"; null with none. |
| 343 | 4 | `public static int groupRank(String group)` | Where a market group sits in Build's order (GROUP_ORDER); after them all for one not in it. |
| 353 | 22 | `public static Kind kindOf(BuildingsTemplate t)` | What a building is measured in (see Kind). |

### VALUE ADDED (lines 376-468)

| line | len | member | says |
|---:|---:|---|---|
| 397 | 5 | `public static double priceOf(Game game, Good g)` | The price a good is valued at for value added, or NaN for one with no market price. |
| 412 | 24 | `public static double valueAdded(Game game, BuildingsTemplate t)` | What one makes less what it uses a month, at today's prices - what it makes being a crude unit's slate since 0.7.76 (sectors.Refining.madeBy()), its template naming only the crude. |
| 438 | 3 | `public static String feedWords(BuildingsTemplate t)` | A conversion unit's feed in words, after its figure (0.7.80): " litres of heavy naphtha a month". |
| 448 | 7 | `public static String goodWords(Good g)` | A good after its count: " t of steel", " kg of bread", " units of building materials", " seat-months of support work" - and for a good counted in itself, " cars", " vans and trucks", " wagon sets of rolling stock". |
| 457 | 5 | `static String makerVerb(BuildingsTemplate t)` | The verb a maker's hero opens on: a farm grows, a mine lifts, the rest make. |
| 464 | 4 | `static Good firstGood(BuildingsTemplate t)` | The good a maker's or an office's hero names: the first it makes. |

### ONE CARD (lines 469-735)

| line | len | member | says |
|---:|---:|---|---|
| 486 | 2 | **type** `public record Paving(int paveable, int paving, Game.BuildQuote one, double unit, double frees, double perTr...` | PAVING, ON THE GRAVEL ROAD'S CARD (0.7.70; ConstructionControl, F): the gravel roads the city could pave and those being paved, the quote for one (Game.quotePave()), the trips one paving takes off the road (BuildAdvic... |
| 489 | 14 | `public static Paving paving(Game game, BuildingsTemplate t)` |  |
| 505 | 10 | `public static double runningCost(Game game, BuildingsTemplate t)` | What one costs to keep a month, which is not what it costs to buy: its upkeep and its posts at today's wages (PopulationManager.getWagesPerType()). |
| 517 | 4 | `public static Sector ownerOf(Game game, BuildingsTemplate t)` | The sector whose money and staffing test a market building is judged by: its owner, or retail for the bank's branch (planBank() asks retail). |
| 526 | 209 | `public static Figures of(Game game, BuildingsTemplate t, BuildAdvice.Measure m)` | One card's figures. |

### A BERTH'S PAYBACK AND THE DEPOT'S LINE (FR4, 2026-10-20; the study's (lines 736-772)

| line | len | member | says |
|---:|---:|---|---|
| 752 | 3 | `public static BuildAdvice.BerthPayback payback(Game game, BuildingsTemplate t)` | A sea terminal's payback for its card: BuildAdvice.berthPayback(), the advice's own read; null for any building but a terminal. |
| 763 | 1 | **type** `public record Depot(double trucks, double room, double onStreet, boolean wouldBuild, double waitsFor)` | The Truck Depot's investors' line (FR4): Freight's trucks, the room its depots keep (Haulage.depotRoom()), the trucks past it on the street (Haulage.onStreet()), whether Freight would order this depot now (Haulage.dep... |
| 766 | 6 | `public static Depot depot(Game game, BuildingsTemplate t)` | The Truck Depot's line figures (FR4), or null for any other building. |

### A GROUP OF CARDS, SCALED AND TAGGED (lines 773-949)

| line | len | member | says |
|---:|---:|---|---|
| 799 | 1 | **type** `public enum NoteKind` | A note under a group's heading, by kind: DOORS {studios' households without a door, families'} (RealEstate.doorShortfall(); a negative is doors to spare); SHOPS {the shops' coverage a month, the people}; MADE {the cit... |
| 801 | 1 | **type** `public record Note(NoteKind kind, double a, double b, Good good)` |  |
| 804 | 38 | **type** `public record Group(String title, List<Figures> cards, Note note)` | One group: its heading, its cards in catalogue order, and its note. |
| 807 | 1 | `public boolean track()` _(in BuildCard.Group)_ | More than one card, so the bars have something to compare and draw a track. |
| 809 | 1 | `public double most1()` _(in BuildCard.Group)_ |  |
| 810 | 1 | `public double least1()` _(in BuildCard.Group)_ |  |
| 811 | 1 | `public double most2()` _(in BuildCard.Group)_ |  |
| 812 | 1 | `public double least2()` _(in BuildCard.Group)_ |  |
| 814 | 9 | `private double extreme(boolean first, boolean most)` _(in BuildCard.Group)_ |  |
| 825 | 1 | `public double share1(Figures f)` _(in BuildCard.Group)_ | How much of the track a card's first bar fills: its figure over the group's largest; full for an infinite one, none for one with none. |
| 826 | 1 | `public double share2(Figures f)` _(in BuildCard.Group)_ |  |
| 828 | 5 | `private static double share(double v, double most)` _(in BuildCard.Group)_ |  |
| 835 | 1 | `public boolean best1(Figures f)` _(in BuildCard.Group)_ | The tag on bar 1: the strict best of two or more. |
| 836 | 1 | `public boolean best2(Figures f)` _(in BuildCard.Group)_ |  |
| 838 | 3 | `private boolean best(double v, double least, double most)` _(in BuildCard.Group)_ |  |
| 851 | 39 | `public static List<Group> groups(Game game, String category, BuildAdvice.Measure m)` | A page's groups. |
| 892 | 36 | `static Note note(Game game, List<Figures> cards)` | The note under a market group's heading (see NoteKind). |
| 937 | 7 | `public static Note noteOf(Game game, Sector sector)` | The note of the group a sector's own buildings are compared under on Build (0.7.30): the Sectors screen's flow reads a seller-priced good's capacity off it - the shops' coverage, the counters', the kitchens' seats, th... |
| 946 | 3 | `static double counted(Game game, Sector sector, double flow)` | A month's flow the save does not carry, or NaN while no month has run since the load (Game.hasMonthRun(); until 0.7.102, while the sector had no word for the month - the word is saved since). |

### THE INVESTORS' LINE (market cards) (lines 950-1199)

| line | len | member | says |
|---:|---:|---|---|
| 986 | 5 | `public static String slot(BuildingsTemplate t)` | Where Game files a building's word: the branch under "Bank" (Game.consider()'s label), every other under its sector. |
| 996 | 1 | **type** `public enum GateKind` | The first gate a building fails for investors now: INTEREST is consider()'s brake on its whole cost (0.7.122), and the last, LENDER, a landlord's home's in its place - the mortgage lender's test (I13-L). |
| 999 | 3 | `public static String depositWord(Resource r)` | The resource a deposit refusal names (0.7.62): "iron" for an Iron Mine (and for none), "oil" for an Oil Well. |
| 1020 | 3 | `public static String siteCountWord(BuildingsTemplate t)` | The word a building's site count carries (0.7.103): " dry" for a land Oil Well, whose count is the dry sites alone; "" for any other. |
| 1025 | 3 | `static String depositWordFor(BuildingsTemplate t, Resource r)` | A DEPOSIT gate's word: depositWord(), or DRY_OIL for a land Oil Well (0.7.103). |
| 1030 | 5 | `public static boolean unfed(Game game, BuildingsTemplate t)` | Whether a building is a refinery's conversion unit with no crude unit standing to feed it (0.7.103): its estimate is a loss only for want of a stream. |
| 1043 | 8 | `static Gate landGate(LandManager ground, BuildingsTemplate t, double free)` | The LAND gate (0.7.109, batch Z2): the ground one needs against the ground its sector may build on (LandManager.freeFor(): free for anyone, and its zone's), and in `why` what the other sectors' zones hold (BusinessInv... |
| 1053 | 3 | `static Gate lossGate(Game game, BuildingsTemplate t, double estimate)` | The LOSS gate: what it would lose, worded NO_FEED for a conversion unit nothing feeds (0.7.103). |
| 1058 | 3 | `static boolean asksInterest(Game game, BuildingsTemplate t, Sector owner)` | Whether investors ask a building the INTEREST gate (0.7.122): every order but a landlord's home, which the mortgage lender's test decides (lenderGate()). |
| 1073 | 6 | `static Gate interestGate(Game game, BuildingsTemplate t, Sector owner, double estimate)` | The INTEREST gate (0.7.122, batch C1): consider()'s test of an order of one, asked through the model's door - Game.interestTest(), whose answer is carriesItsWholeCost()'s - with its figures {what one would earn a mont... |
| 1087 | 6 | `static Gate lenderGate(Game game, BuildingsTemplate t, Sector owner)` | The LENDER gate (I13-L): a landlord's home is not asked the INTEREST gate, the mortgage lender's test decides it (Game.considerOnMortgage(), THE LANDLORDS' MORTGAGES), and the card asks that, through the model's own d... |
| 1095 | 4 | `static String banWords(Game game, Sector owner)` | What an INTEREST gate says under a borrowing ban, in consider()'s words: "borrowing ban, 7 more months - it would need credit". |
| 1116 | 11 | **type** `public record Gate(GateKind kind, double a, double b, JobType licence, String why, Game.InterestTest test, ...` | One gate: DEPOSIT {deposits the city owns, mines committed} with the resource's word in `why` (depositWord(); DRY_OIL for a land Oil Well since 0.7.103); LICENCE {licences one needs, spare licences} with the licence; ... |
| 1120 | 3 | `public Gate(GateKind kind, double a, double b, JobType licence, String why, Game.InterestTest test)` _(in BuildCard.Gate)_ | INTEREST: the model's test. |
| 1125 | 1 | `public Gate(GateKind kind, double a, double b, JobType licence, String why)` _(in BuildCard.Gate)_ | Every gate but INTEREST and LENDER: no test of its own. |
| 1143 | 14 | **type** `public record Investors(String building, String slot, Sector owner, boolean theirs, boolean yoursToo, int o...` | The investors' line's figures. |
| 1147 | 1 | `public boolean showOwn()` _(in BuildCard.Investors)_ | "this one:" is said when there is a gate, no investor is already building it, and the word does not already name it. |
| 1155 | 1 | `public boolean showPlanned()` _(in BuildCard.Investors)_ | ...and for a materials building the planner's gate is said in its place (0.7.142): the first its sector asks, in SpreadPlanner's words, on the same terms - when it fails one, no investor is building it and the word do... |
| 1158 | 18 | `public static Investors investors(Game game, BuildingsTemplate t)` |  |
| 1178 | 21 | `static Gate gate(Game game, BuildingsTemplate t, Sector owner, double estimate)` | The first gate investors would stop this building at now, in buildStack()'s order and then the planners': null when it passes them all. |

### EVERY GATE (0.7.75, the sector statements' R4) (lines 1200-1320)

| line | len | member | says |
|---:|---:|---|---|
| 1220 | 1 | **type** `public enum Mark` | A gate's answer for one building: it passes, it fails, or it is not a gate this building has. |
| 1223 | 1 | **type** `public record GateMark(GateKind kind, Mark mark, Gate failure)` | One gate's answer: its kind (LOSS is "it pays", INTEREST "it carries its interest on its whole cost"), the mark, and gate()'s figures when it fails. |
| 1238 | 22 | **type** `public record Appraisal(BuildingsTemplate template, Sector owner, List<GateMark> gates, double estimate, do...` | One building at every gate (R4). |
| 1242 | 4 | `public Gate first()` _(in BuildCard.Appraisal)_ | The first gate it fails - gate()'s answer - or null when it passes them all. |
| 1248 | 5 | `public List<GateMark> failures()` _(in BuildCard.Appraisal)_ | Every gate it fails, in order. |
| 1255 | 4 | `public Mark at(GateKind kind)` _(in BuildCard.Appraisal)_ | Its mark at one gate. |
| 1262 | 48 | `public static Appraisal appraise(Game game, BuildingsTemplate t)` | Every gate investors would ask this building at now, and what one would earn, cost and be paid for with. |
| 1312 | 8 | `public static List<Appraisal> appraiseAll(Game game, Sector sector)` | Every market building one sector owns, appraised at every gate, in the catalogue's order: the investor report's (R4). |

### THE MATERIALS' CARDS (0.7.142, batch I13; docs/studies/spec-materials.md (lines 1321-1719)

| line | len | member | says |
|---:|---:|---|---|
| 1360 | 5 | `public static boolean materials(Game game, BuildingsTemplate t)` | Whether a building is one of the nine materials industries' (0.7.142): its owner a ProcessingSector or an ExtractionSector. |
| 1381 | 6 | **type** `public record Stands(Resource resource, Deposit.FieldType type, int sites, int owned, int committed, double...` | Where a building on a typed resource's sites stands (0.7.142): its resource and the type of field it needs (null for ANY - a quarry), the sites one takes, the city's sites of its type and those spoken for (Game.sitesF... |
| 1385 | 1 | `public boolean anyType()` _(in BuildCard.Stands)_ | Whether it stands on its resource's fields of either type (a Quarry, a Large Quarry). |
| 1389 | 15 | `public static Stands stands(Game game, BuildingsTemplate t)` | ...for a building on a typed resource's sites (iron, coal, copper, stone); null for any other. |
| 1410 | 1 | **type** `public record Hydro(int free, int slots, int sites, double widthM, double headM)` | Where a Hydro Station stands (HY1): the slots free of the stations the city's linked hydro sites hold in all (Game.hydroSlotsFree(), hydroSlots()), how many sites that is, and the best linked site's river width and he... |
| 1418 | 1 | **type** `public record Bridge(BridgeSurvey.Result survey, String farName, Game.BridgeSpan span, double landings)` | Where a long bridge would go (ET6): the crossing the model surveyed toward the landmass the card picked (Game.bridgeSurvey()), the far landmass's name as the card says it, one bridge's span (Game.bridgeSpan(): its dol... |
| 1421 | 8 | `public static Bridge bridge(Game game, BuildingsTemplate t)` | ...for a bridge; null for any other building and for a game with no city. |
| 1431 | 7 | `public static Hydro hydro(Game game, BuildingsTemplate t)` | ...for a hydro station; null for any other building. |
| 1452 | 9 | **type** `public record Weighed(double earns, double cost, double vans, double fleet, double hurdle, ham.citybuilders...` | A materials building weighed by the rule its sector orders by (0.7.142): what one more would earn a month at the city's values, its freight in it (FR2: its vans' wear and diesel and its bulk's city legs at Haulage's q... |
| 1456 | 1 | `public double score()` _(in BuildCard.Weighed)_ | What it earns a month on its whole cost; 0 with no cost (SpreadPlanner.Candidate.score()). |
| 1459 | 1 | `public boolean passes()` _(in BuildCard.Weighed)_ | Whether it passes every gate its sector asks. |
| 1476 | 31 | `public static Weighed weighed(Game game, BuildingsTemplate t)` | ...a works by its sector's own appraisal (ProcessingSector.appraise(): the candidate the plan weighs, to the bit); a mine, a crew or a washing plant as its sector's plan weighs one - ExtractionSector .estimatedMonthly... |
| 1516 | 1 | **type** `public record Power(double gen, double draw, double bought, double sold, double soldValue)` | A works' own power (0.7.142): the kW it makes (BuildingsTemplate .getGen()), its draw, what it buys past what it makes and what it sells the grid past its draw (powerBought(), powerSold()), and what that sells for a m... |
| 1518 | 6 | `public static Power power(Game game, BuildingsTemplate t)` |  |
| 1537 | 1 | **type** `public record OldMetalBought(Good metal, double intake, double price, double arisen, double standingTake, d...` | The city's old metal a yard buys (0.7.142; OldMetal, the spec's 2.9): the metal (STEEL for a Scrap Yard, COPPER for a Scrap Copper Refinery), what one takes a month at nameplate (BuildingsTemplate .oldMetal()), what i... |
| 1539 | 20 | `public static OldMetalBought oldMetal(Game game, BuildingsTemplate t)` |  |
| 1561 | 1 | **type** `public record Buyer(Sector sector, double takes)` | One sector here that buys a good (0.7.142): what its standing buildings take of it a month at nameplate (Sector.getInputAtCapacity()) - none while none stands. |
| 1572 | 12 | `public static List<Buyer> buyers(Game game, Good g)` | Every sector here whose catalogue holds a building that uses `g`, in Sectors' order, with what its standing buildings take a month at nameplate (0.7.142): who could buy the good in this city. |
| 1595 | 5 | **type** `public record Outlet(Good good, double made, boolean captive, double ownTakes, List<Buyer> others)` | What one of a materials building makes of a good, and where it goes (0.7.142): its tonnes a month at nameplate (Stands.makes() for a building on sites, its row's for any other), whether its owner's own works take it f... |
| 1598 | 1 | `public boolean ships()` _(in BuildCard.Outlet)_ | Whether nothing here buys it: no other sector, and its owner does not take it first. |
| 1602 | 16 | `public static List<Outlet> outlets(Game game, BuildingsTemplate t)` | ...each good one makes, in its makes' order; empty for a building that is not a materials one. |
| 1632 | 28 | `public static List<Sector.Line> chainLines(Game game, Sector sector)` | A materials sector's chain as its Sectors page's lines (0.7.142; each of the nine industries' ownLines() ends on them): its works' own power, when any standing makes some - what they make (BuildingsTemplate .getGen())... |
| 1671 | 2 | **type** `public record CrewRule(double cuttable, double committed, double cut, double months, double monthsWith, boo...` | E, the crew rule (0.7.142; sectors.Forestry, E AND THE PLAN; spec-zoning 3 and board 3): the forest Forestry may cut (LandManager .cuttableForest()), every crew's cut a month standing and on site (Forestry.crewsCut(tr... |
| 1674 | 16 | `public static CrewRule crewRule(Game game, BuildingsTemplate t)` |  |
| 1692 | 4 | `public static CrewRule crewRule(Game game)` | ...the rule as the forest law's page says it: for the smallest crew in the catalogue (the one E refuses last); null with none. |
| 1698 | 8 | `public static BuildingsTemplate smallestCrew(Game game)` | The crew in the catalogue that cuts least a month (Forestry.cutOf()), the first on a tie; null with none. |
| 1708 | 6 | `static String eWords(CrewRule rule, BuildingsTemplate t)` | E's refusal in Forestry's plan's words (Forestry.eWords(), which BuildCardCheck 12 holds this to): the forest it may cut, and the months it would last with one more. |
| 1716 | 3 | `static String a(String name)` | "a Logging Camp", "an Iron Mine": ProcessingSector.a()'s article. |

### THE WORD'S KIND (0.7.30) (lines 1720-1811)

| line | len | member | says |
|---:|---:|---|---|
| 1752 | 1 | **type** `public enum WordKind` | What a sector's word is about. |
| 1776 | 1 | `public static WordKind wordKind(String word)` | The kind of a word on its text alone. |
| 1783 | 15 | `public static WordKind wordKind(String word, boolean building, boolean landBlocked)` | ...with what the text cannot say: whether investors have an order on site of one of the sector's buildings (`building`), and whether the month put it on Game.getLandBlockedSectors() (`landBlocked`). |
| 1800 | 11 | `static boolean says(String text, String[] phrases)` | Whether any of the phrases is in the text as whole words: no letter or digit either side of it. |

### ONE SECTOR'S INVESTORS (0.7.30) (lines 1812-1865)

| line | len | member | says |
|---:|---:|---|---|
| 1835 | 8 | **type** `public record SectorInvestors(Sector owner, String word, WordKind kind, List<Investors> buildings, boolean ...` | One sector's investors. |
| 1839 | 3 | `public Investors line()` _(in BuildCard.SectorInvestors)_ | The sector's line as a build card's line is worded: its own name, its word, no gate of its own. |
| 1844 | 21 | `public static SectorInvestors sectorInvestors(Game game, Sector sector)` |  |

### THE VERDICT ON AN ORDER (lines 1866-1893)

| line | len | member | says |
|---:|---:|---|---|
| 1875 | 1 | **type** `public enum VerdictKind` |  |
| 1878 | 3 | **type** `public record Verdict(VerdictKind kind, Game.BuildQuote quote, double figure, Resource site)` | The verdict, the quote it is on, and how short: sq ft of land, cash, or the months to wait - and, refused for a deposit, whose sites it needs (0.7.62; null otherwise). |
| 1879 | 1 | `public Verdict(VerdictKind kind, Game.BuildQuote quote, double figure)` _(in BuildCard.Verdict)_ |  |
| 1882 | 11 | `public static Verdict verdict(Game game, BuildingsTemplate t, int n)` |  |


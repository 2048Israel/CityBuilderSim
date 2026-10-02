# BuildCard.java - 901 lines · 48 methods · 10 constants · model

`ham/citybuildersim/BuildCard.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

> One build card's figures, for every one of the 73 buildings (0.7.25): what
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

**Uses:** [BuildAdvice](BuildAdvice.md) (30), [BuildingsTemplate](BuildingsTemplate.md) (23), [Game](Game.md) (19), [Good](Good.md) (16), [Sector](Sector.md) (13), [Sectors](Sectors.md) (8), [BuildingType](BuildingType.md) (8), [CareType](CareType.md) (7), [JobType](JobType.md) (4), [BuildingsStacks](BuildingsStacks.md) (3), [Bank](Bank.md) (2), [LandManager](LandManager.md) (2), [Formats](Formats.md) (1), [BuildingManager](BuildingManager.md) (1)

**Used by (6):** [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [ReadPathCheck](ReadPathCheck.md), [SectorFlow](SectorFlow.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md)

## Sections

| line | section |
|---:|---|
| 35 | WHAT KIND OF CARD |
| 117 | THE CITY'S WORDS (0.7.24, moved here from the screen in 0.7.25) |
| 204 | THE MARKET'S GROUPS |
| 294 | VALUE ADDED |
| 362 | ONE CARD |
| 495 | A GROUP OF CARDS, SCALED AND TAGGED |
| 646 | THE INVESTORS' LINE (market cards) |
| 734 | THE WORD'S KIND (0.7.30) |
| 825 | ONE SECTOR'S INVESTORS (0.7.30) |
| 879 | THE VERDICT ON AN ORDER |

## Enum constants

| line | constant | says |
|---:|---|---|
| 57 | `BuildCard.Kind.CITY` |  |
| 57 | `BuildCard.Kind.HOMES` |  |
| 57 | `BuildCard.Kind.CUSTOMERS` |  |
| 57 | `BuildCard.Kind.MEALS` |  |
| 57 | `BuildCard.Kind.BRANCH` |  |
| 57 | `BuildCard.Kind.RAIL` |  |
| 57 | `BuildCard.Kind.POINTS` |  |
| 57 | `BuildCard.Kind.MAKER` |  |
| 57 | `BuildCard.Kind.OFFICE` |  |
| 65 | `BuildCard.Bar2.LAND` |  |
| 65 | `BuildCard.Bar2.UNFILLED` |  |
| 65 | `BuildCard.Bar2.UNSTAFFABLE` |  |
| 65 | `BuildCard.Bar2.NONE` |  |
| 520 | `BuildCard.NoteKind.DOORS` |  |
| 520 | `BuildCard.NoteKind.SHOPS` |  |
| 520 | `BuildCard.NoteKind.MADE` |  |
| 520 | `BuildCard.NoteKind.RAIL` |  |
| 520 | `BuildCard.NoteKind.LUXURY` |  |
| 520 | `BuildCard.NoteKind.MEALS` |  |
| 520 | `BuildCard.NoteKind.NONE` |  |
| 667 | `BuildCard.GateKind.DEPOSIT` |  |
| 667 | `BuildCard.GateKind.LICENCE` |  |
| 667 | `BuildCard.GateKind.STAFFING` |  |
| 667 | `BuildCard.GateKind.LAND` |  |
| 667 | `BuildCard.GateKind.LOSS` |  |
| 765 | `BuildCard.WordKind.BUILDING` |  |
| 765 | `BuildCard.WordKind.SELLING` |  |
| 765 | `BuildCard.WordKind.LAND` |  |
| 765 | `BuildCard.WordKind.STAFF` |  |
| 765 | `BuildCard.WordKind.LICENCE` |  |
| 765 | `BuildCard.WordKind.ORE` |  |
| 765 | `BuildCard.WordKind.SUPPLY` |  |
| 765 | `BuildCard.WordKind.CREDIT` |  |
| 765 | `BuildCard.WordKind.MONEY` |  |
| 765 | `BuildCard.WordKind.ENOUGH` |  |
| 765 | `BuildCard.WordKind.NONE` |  |
| 765 | `BuildCard.WordKind.OTHER` |  |
| 888 | `BuildCard.VerdictKind.NO_DEPOSIT` |  |
| 888 | `BuildCard.VerdictKind.NO_LICENCE` |  |
| 888 | `BuildCard.VerdictKind.NO_LAND` |  |
| 888 | `BuildCard.VerdictKind.BILL` |  |
| 888 | `BuildCard.VerdictKind.MONTHS` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 136 | `BuildCard.KILOWATTS` | `" kW"` | The words after a power plant's figure (0.7.28): kilowatts, the model's unit and a rate - "units a month" until then, which was neither. |
| 214 | `BuildCard.GROUP_ORDER` | `{ "Homes", "Groceries", "The bank's branches", "Food mills", "Food processing...` | The market groups, in the order a page lays them out. |
| 768 | `BuildCard.LAND_WORDS` | `{ "no land", "short of homes" }` | The phrases each kind is read by, in lower case; a word starting "Could not build" is LAND as well, and one starting "Declined" is MONEY unless CREDIT took it. |
| 770 | `BuildCard.STAFF_WORDS` | `{ "could staff" }` | ...STAFF's. |
| 772 | `BuildCard.LICENCE_WORDS` | `{ "licence", "licences" }` | ...LICENCE's: whole words, so the plural as well. |
| 774 | `BuildCard.ORE_WORDS` | `{ "deposit", "ore" }` | ...ORE's. |
| 776 | `BuildCard.SUPPLY_WORDS` | `{ "fabricates", "will not sell" }` | ...SUPPLY's. |
| 778 | `BuildCard.CREDIT_WORDS` | `{ "borrowing ban", "bank", "default point", "down payment", "lender", "mortga...` | ...CREDIT's. |
| 780 | `BuildCard.MONEY_WORDS` | `{ "below cost", "no margin", "worth sinking", "worth building", "cost more th...` | ...MONEY's. |
| 784 | `BuildCard.ENOUGH_WORDS` | `{ "ahead of", "already", "covers all", "months of work queued", "months of wo...` | ...ENOUGH's. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 31 | 871 | **type** `public final class BuildCard` | One build card's figures, for every one of the 73 buildings (0.7.25): what it gives the city and in what unit, its price per unit of that, the scarce resource it takes, the group it is compared within and where in tha... |
| 33 | 1 | `private BuildCard()` |  |

### WHAT KIND OF CARD (lines 35-116)

| line | len | member | says |
|---:|---:|---|---|
| 57 | 1 | **type** `public enum Kind` | CITY: the picked ring's measure. |
| 65 | 1 | **type** `public enum Bar2` | What bar 2 measures: LAND per unit (the market's scarce resource), UNFILLED posts per 10,000 served (the city's, Jerus's pick in 0.7.24), UNSTAFFABLE posts of 100 (offices, whose three cards differ only in who they em... |
| 98 | 18 | **type** `public record Figures(BuildingsTemplate template, Kind kind, String group, String verb, double figure, Stri...` | One card's figures. |
| 108 | 1 | `public boolean addsNothing()` _(in BuildCard.Figures)_ | No positive unit to price against: a maker whose inputs cost more than it makes, or a city building nobody could staff. |
| 111 | 1 | `public boolean inMonths()` _(in BuildCard.Figures)_ | Measured in months of what it adds or exports, not a price per unit. |
| 114 | 1 | `public boolean market()` _(in BuildCard.Figures)_ | Built by investors as well as by the city. |

### THE CITY'S WORDS (0.7.24, moved here from the screen in 0.7.25) (lines 117-203)

| line | len | member | says |
|---:|---:|---|---|
| 139 | 16 | `public static String[] doesWords(BuildAdvice.Measure m, BuildingsTemplate t)` | The words before the figure and after it, for a city building on a measure. |
| 157 | 13 | `public static String perWords(BuildAdvice.Measure m)` | ...the one of it a cost is per: "cost per patient", "cheapest per child". |
| 172 | 13 | `public static String perPlural(BuildAdvice.Measure m)` | ...and many of it, for the staff bar's "per 10,000 patients". |
| 191 | 4 | `public static double served(Game game, BuildAdvice.Measure m, BuildingsTemplate t)` | What one serves at today's staffing, in the measure's own unit (BuildAdvice.unit()); for road capacity the road's own capacity, since BuildAdvice.unit() is the advice's trips relieved. |
| 197 | 6 | `public static BuildAdvice.Measure cityMeasure(BuildingsTemplate t)` | The first ring of its category a city building serves - its group on a page that has none picked. |

### THE MARKET'S GROUPS (lines 204-293)

| line | len | member | says |
|---:|---:|---|---|
| 225 | 25 | `public static String group(BuildingsTemplate t)` | The heading a building is compared under: a market building's group by its owning sector, or, for a city building, the first ring of its category it serves. |
| 257 | 6 | `public static String groupOf(Game game, Sector sector)` | The group a sector's own buildings are compared under on Build (0.7.30): its first own building's - "Food mills" for the sector called Industry, which bakes. |
| 265 | 8 | `public static String categoryOf(Game game, Sector sector)` | ...and the Build category that group is on: "Industry"; null with none. |
| 275 | 4 | `public static int groupRank(String group)` | Where a market group sits in Build's order (GROUP_ORDER); after them all for one not in it. |
| 281 | 12 | `public static Kind kindOf(BuildingsTemplate t)` | What a building is measured in (see Kind). |

### VALUE ADDED (lines 294-361)

| line | len | member | says |
|---:|---:|---|---|
| 315 | 5 | `public static double priceOf(Game game, Good g)` | The price a good is valued at for value added, or NaN for one with no market price. |
| 322 | 12 | `public static double valueAdded(Game game, BuildingsTemplate t)` | What one makes less what it uses a month, at today's prices. |
| 341 | 7 | `public static String goodWords(Good g)` | A good after its count: " t of steel", " kg of bread", " units of building materials", " seat-months of support work" - and for a good counted in itself, " cars", " vans and trucks", " wagon sets of rolling stock". |
| 350 | 5 | `static String makerVerb(BuildingsTemplate t)` | The verb a maker's hero opens on: a farm grows, a mine lifts, the rest make. |
| 357 | 4 | `static Good firstGood(BuildingsTemplate t)` | The good a maker's or an office's hero names: the first it makes. |

### ONE CARD (lines 362-494)

| line | len | member | says |
|---:|---:|---|---|
| 367 | 10 | `public static double runningCost(Game game, BuildingsTemplate t)` | What one costs to keep a month, which is not what it costs to buy: its upkeep and its posts at today's wages (PopulationManager.getWagesPerType()). |
| 379 | 4 | `public static Sector ownerOf(Game game, BuildingsTemplate t)` | The sector whose money and staffing test a market building is judged by: its owner, or retail for the bank's branch (planBank() asks retail). |
| 388 | 106 | `public static Figures of(Game game, BuildingsTemplate t, BuildAdvice.Measure m)` | One card's figures. |

### A GROUP OF CARDS, SCALED AND TAGGED (lines 495-645)

| line | len | member | says |
|---:|---:|---|---|
| 520 | 1 | **type** `public enum NoteKind` | A note under a group's heading, by kind: DOORS {studios' households without a door, families'} (RealEstate.doorShortfall(); a negative is doors to spare); SHOPS {the shops' coverage a month, the people}; MADE {the cit... |
| 522 | 1 | **type** `public record Note(NoteKind kind, double a, double b, Good good)` |  |
| 525 | 38 | **type** `public record Group(String title, List<Figures> cards, Note note)` | One group: its heading, its cards in catalogue order, and its note. |
| 528 | 1 | `public boolean track()` _(in BuildCard.Group)_ | More than one card, so the bars have something to compare and draw a track. |
| 530 | 1 | `public double most1()` _(in BuildCard.Group)_ |  |
| 531 | 1 | `public double least1()` _(in BuildCard.Group)_ |  |
| 532 | 1 | `public double most2()` _(in BuildCard.Group)_ |  |
| 533 | 1 | `public double least2()` _(in BuildCard.Group)_ |  |
| 535 | 9 | `private double extreme(boolean first, boolean most)` _(in BuildCard.Group)_ |  |
| 546 | 1 | `public double share1(Figures f)` _(in BuildCard.Group)_ | How much of the track a card's first bar fills: its figure over the group's largest; full for an infinite one, none for one with none. |
| 547 | 1 | `public double share2(Figures f)` _(in BuildCard.Group)_ |  |
| 549 | 5 | `private static double share(double v, double most)` _(in BuildCard.Group)_ |  |
| 556 | 1 | `public boolean best1(Figures f)` _(in BuildCard.Group)_ | The tag on bar 1: the strict best of two or more. |
| 557 | 1 | `public boolean best2(Figures f)` _(in BuildCard.Group)_ |  |
| 559 | 3 | `private boolean best(double v, double least, double most)` _(in BuildCard.Group)_ |  |
| 569 | 21 | `public static List<Group> groups(Game game, String category, BuildAdvice.Measure m)` | A page's groups. |
| 592 | 32 | `static Note note(Game game, List<Figures> cards)` | The note under a market group's heading (see NoteKind). |
| 633 | 7 | `public static Note noteOf(Game game, Sector sector)` | The note of the group a sector's own buildings are compared under on Build (0.7.30): the Sectors screen's flow reads a seller-priced good's capacity off it - the shops' coverage, the counters', the kitchens' seats, th... |
| 642 | 3 | `static double counted(Game game, Sector sector, double flow)` | A month's flow the save does not carry, or NaN while no month has run since the load: the sector has no word for the month. |

### THE INVESTORS' LINE (market cards) (lines 646-733)

| line | len | member | says |
|---:|---:|---|---|
| 662 | 3 | `public static String slot(BuildingsTemplate t)` | Where Game files a building's word: the branch under "Bank" (Game.consider()'s label), every other under its sector. |
| 667 | 1 | **type** `public enum GateKind` | The first gate a building fails for investors now. |
| 675 | 1 | **type** `public record Gate(GateKind kind, double a, double b, JobType licence, String why)` | One gate: DEPOSIT {deposits the city owns, mines committed}; LICENCE {licences one needs, spare licences} with the licence; STAFFING with the staffing test's own why; LAND {sq ft one needs, sq ft free}; LOSS {what the... |
| 690 | 6 | **type** `public record Investors(String building, String slot, Sector owner, boolean theirs, boolean yoursToo, int o...` | The investors' line's figures. |
| 694 | 1 | `public boolean showOwn()` _(in BuildCard.Investors)_ | "this one:" is said when there is a gate, no investor is already building it, and the word does not already name it. |
| 697 | 17 | `public static Investors investors(Game game, BuildingsTemplate t)` |  |
| 716 | 17 | `static Gate gate(Game game, BuildingsTemplate t, Sector owner, double estimate)` | The first gate investors would stop this building at now, in buildStack()'s order and then the planners': null when it passes them all. |

### THE WORD'S KIND (0.7.30) (lines 734-824)

| line | len | member | says |
|---:|---:|---|---|
| 765 | 1 | **type** `public enum WordKind` | What a sector's word is about. |
| 789 | 1 | `public static WordKind wordKind(String word)` | The kind of a word on its text alone. |
| 796 | 15 | `public static WordKind wordKind(String word, boolean building, boolean landBlocked)` | ...with what the text cannot say: whether investors have an order on site of one of the sector's buildings (`building`), and whether the month put it on Game.getLandBlockedSectors() (`landBlocked`). |
| 813 | 11 | `static boolean says(String text, String[] phrases)` | Whether any of the phrases is in the text as whole words: no letter or digit either side of it. |

### ONE SECTOR'S INVESTORS (0.7.30) (lines 825-878)

| line | len | member | says |
|---:|---:|---|---|
| 848 | 8 | **type** `public record SectorInvestors(Sector owner, String word, WordKind kind, List<Investors> buildings, boolean ...` | One sector's investors. |
| 852 | 3 | `public Investors line()` _(in BuildCard.SectorInvestors)_ | The sector's line as a build card's line is worded: its own name, its word, no gate of its own. |
| 857 | 21 | `public static SectorInvestors sectorInvestors(Game game, Sector sector)` |  |

### THE VERDICT ON AN ORDER (lines 879-901)

| line | len | member | says |
|---:|---:|---|---|
| 888 | 1 | **type** `public enum VerdictKind` |  |
| 891 | 1 | **type** `public record Verdict(VerdictKind kind, Game.BuildQuote quote, double figure)` | The verdict, the quote it is on, and how short: sq ft of land, cash, or the months to wait. |
| 893 | 8 | `public static Verdict verdict(Game game, BuildingsTemplate t, int n)` |  |


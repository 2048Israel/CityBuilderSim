# Oil.java - 1,328 lines · 88 methods · 14 constants · sectors

`ham/citybuildersim/sectors/Oil.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> Oil wells. THE SIXTEENTH SECTOR (0.7.62, batch K; the project's
> spec-land.md 2.7).
> 
> MINING'S SHAPE, ON THE OTHER RESOURCE. The world laid oil in fields from
> the start (Resource.OIL, batch J1a) and the land office has sold the
> ground over it since J1b; this is what lifts it. A well stands on an
> unworked oil site the city owns (Game.hasDepositFor(), by its good), lifts
> 415 t of crude a month - a hundred barrels a day - and ships what it
> lifts: CRUDE is a flow good, so what the refiners do not take at the
> local price leaves at the export price the same month, which is why a
> well is worth drilling before there is a refinery. The ground limits it
> through the template's hook (groundLimit()), and the wells retire when
> the oil runs out, as the mines do when the ore does.
> 
> SINCE 0.7.84 (batch O7; runs/spec-oil.md 2.6) A WELL DECLINES: it lifts
> its 415 t the month it opens and 10% less each year after, and is retired
> below ten barrels a day, after 263 months (THE WELLS DECLINE, below); a
> land well stands only on a dry site - a field whose centre is on land -
> and has one post, not three.
> 
> SINCE 0.7.91 (batch O10; runs/spec-oil.md 2.7, 2.11) THE SEA'S OIL IS
> LIFTED TOO: an Offshore Platform stands on a shallow sea field the city
> owns, its Platform Wells lift in its slots, shuttle tankers take their
> crude ashore at the boundary's freight, and a Crude Pipeline replaces
> them where the freight it saves over the field's life repays it (THE OIL
> AT SEA, below). The planner weighs the three kinds after the land well.
> 
> SINCE 0.7.93 (batch O10b; Jerus: "two pools, offshore oil and ground
> oil") THE CITY'S CRUDE IS TWO POOLS (LandManager's THE TWO OIL POOLS): the
> land wells lift the ground pool only, the platform wells the offshore pool
> only, each sized from its own fields (groundLimit()).
> 
> Everything else - the books, the credit, the market, the payroll, the
> export - is the template's.

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (31), [BusinessInvestment](BusinessInvestment.md) (30), [LandManager](LandManager.md) (29), [Good](Good.md) (25), [Deposit](Deposit.md) (8), [Game](Game.md) (6), [Resource](Resource.md) (4), [Sector](Sector.md) (2), [BuildingType](BuildingType.md) (2), [GoodsMarket](GoodsMarket.md) (2), [Formats](Formats.md) (2), [BuildingsStacks](BuildingsStacks.md) (1), [World](World.md) (1), [Sectors](Sectors.md) (1)

**Used by (17):** [BuildCard](BuildCard.md), [BuildScreen](BuildScreen.md), [BuildingDataCheck](BuildingDataCheck.md), [Game](Game.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [ReadPathCheck](ReadPathCheck.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [Refining](Refining.md), [SaveFileCheck](SaveFileCheck.md), [SectorScreen](SectorScreen.md), [Sectors](Sectors.md), [WellCheck](WellCheck.md)

## Sections

| line | section |
|---:|---|
| 101 | THE WELLS DECLINE (0.7.84, batch O7; runs/spec-oil.md 2.6, the |
| 609 | THE OIL AT SEA (0.7.91, batch O10; runs/spec-oil.md 2.7 and 2.11, the |
| 944 | · the pipelines |
| 1045 | · the shuttle tankers |
| 1090 | · the pipeline rule |
| 1168 | · the planner's three kinds at sea |

## Enum constants

| line | constant | says |
|---:|---|---|
| 138 | `Oil.WellKind.LAND` |  |
| 138 | `Oil.WellKind.PLATFORM` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 144 | `Oil.LAND_KEEPS_A_YEAR` | `.9` | A land well keeps nine tenths of its lift a year: it loses 10% a year (the research's 3.2, est.; [W6]). |
| 147 | `Oil.PLATFORM_PLATEAU_MONTHS` | `36` | A platform well holds its lift for a plateau of three years (the research's 3.1: two or three [W6][W16]; spec-oil 2.6)... |
| 150 | `Oil.PLATFORM_KEEPS_A_YEAR` | `.915` | ...and then keeps 91.5% of it a year: it loses 8.5% a year, the IEA's shallow-offshore rate [W6][W16]. |
| 153 | `Oil.NAMEPLATE_BARRELS_A_DAY` | `100` | A well's nameplate is a hundred barrels a day (BuildingManager, FUEL: 415 t a month at 7.33 barrels a tonne). |
| 156 | `Oil.WORN_OUT_BARRELS_A_DAY` | `10` | A well lifting under ten barrels a day is worn out and retired (the research's 3.2): 41.5 t a month of a 415 t well's. |
| 159 | `Oil.WORN_OUT_SHARE` | `WORN_OUT_BARRELS_A_DAY / NAMEPLATE_BARRELS_A_DAY` | ...the share of its nameplate a well is retired under: WORN_OUT_BARRELS_A_DAY over NAMEPLATE_BARRELS_A_DAY, a tenth. |
| 162 | `Oil.VINTAGE_KEY` | `"vintages."` | The extras' key a vintage is saved under: vintages.<month opened>.<kind>, its count the value. |
| 185 | `Oil.LAND_LIFE` | `firstWornOut(WellKind.LAND), PLATFORM_LIFE = firstWornOut(WellKind.PLATFORM)` |  |
| 675 | `Oil.PLATFORM_MAX_DEPTH_M` | `150` | The deepest sea a platform's jacket stands in, in metres: 150, the fixed platform's limit (the research's 3.2; spec-oil 2.7). |
| 678 | `Oil.PIPE_LIFE_MONTHS` | `480` | A pipe's working life, in months: 480, 40 years (the research's 4.5, est.) - what the pipeline rule counts the field's months to at most. |
| 681 | `Oil.PIPE_PAYBACK` | `1.25` | The pipeline rule's margin: the freight a pipe saves over the field's life must repay its cost 1.25 times (the research's 4.5; spec-oil 2.11). |
| 684 | `Oil.SHUTTLE_TANKERS` | `"Shuttle tankers"` | The shuttle tankers' name on the Oil sector's input line. |
| 687 | `Oil.PLATFORM_KEY` | `"platforms."` | The extras' key a platform is saved under: platforms.<place>.<cell>.<index>.<month opened>, the wells in its slots the value. |
| 690 | `Oil.PIPELINE_KEY` | `"pipelines."` | ...and a pipeline: pipelines.<place>.<cell>.<index>.<month ordered>, its kilometres the value. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 91 | `private double liftedOnGround, liftedAtSea` | The month's crude by pool, as groundLimit() lifted it (0.7.93): the land wells' from the ground pool and the platform wells' from the offshore pool. |
| 194 | `private final List<Vintage> vintages` | The vintages as struck, oldest first (the month's reading is vintagesNow()). |
| 197 | `private long vintagesStamp` | Moves with every change to `vintages`, for the lift's cache. |
| 302 | `private int liftMonth` | The lift's cache: the month, the wells standing (land, and platform in slots) and the vintages' stamp it was summed for. |
| 303 | `private long liftStamp` |  |
| 304 | `private double liftCached, liftAtSeaCached` |  |
| 492 | `private String atSeaWord` | Why nothing at sea was ordered the last month the planner looked (null when it ordered, or had nothing at sea to weigh): the Oil page's line. |
| 699 | `private final List<Platform> platforms` | The platforms as struck last, in the order they opened. |
| 702 | `private final List<Pipeline> pipelines` | The pipelines as struck last, in the order they were ordered. |
| 705 | `private long offshoreStamp` | Moves with every change to the two lists, for the reads' caches. |
| 708 | `private Platform pipeFor` | The field this month's planner ordered a pipe for: where the kilometres ordered this month go (pipelinesNow()); cleared at the strike. |
| 869 | `private int pvMonth` | The platforms' cache: the month, the jackets and wells standing, the record's stamp and the fields it was read against. |
| 870 | `private long pvStamp` |  |
| 871 | `private Object pvFields` |  |
| 872 | `private List<Platform> pvCached` |  |
| 1054 | `private double shuttleTonnes, shuttleBill` | The month's shuttle tankers: tonnes brought ashore and what they cost (payShuttle()); not saved - the statement carries the bill. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 58 | 1271 | **type** `public final class Oil extends Sector` | Oil wells. |
| 60 | 7 | `public Oil()` |  |
| 76 | 13 | `protected double groundLimit(Good g, double asked)` | The ground, not the well, decides what comes up - since 0.7.93 each kind's own pool (LandManager's THE TWO OIL POOLS): the platform wells' part of the month's ask, their nameplate (getCapacityAtSea()) at the month's r... |
| 93 | 1 | `public double getLiftedOnGround()` |  |
| 94 | 1 | `public double getLiftedAtSea()` |  |
| 97 | 3 | `public double getPotentialOutput()` | What the wells could lift this month if the ground allowed it. |

### THE WELLS DECLINE (0.7.84, batch O7; runs/spec-oil.md 2.6, the (lines 101-608)

| line | len | member | says |
|---:|---:|---|---|
| 138 | 1 | **type** `public enum WellKind` | A well's kind: on land, on a dry site; or in an offshore platform's slot (0.7.91, THE OIL AT SEA). |
| 141 | 1 | **type** `public record Vintage(int month, int count, WellKind kind)` | A batch of wells: the month they opened, how many, and their kind. |
| 171 | 8 | `public static double profile(WellKind kind, int age)` | The share of its nameplate a well of `kind` lifts at `age` months (pure): a land well LAND_KEEPS_A_YEAR to the power of its years; a platform well 1 through its plateau and PLATFORM_KEEPS_A_YEAR to the power of its ye... |
| 181 | 3 | `public static int lifeMonths(WellKind kind)` | The age, in months, from which a well of `kind` lifts under WORN_OUT_SHARE of its nameplate: 263 on land, 348 on a platform. |
| 187 | 5 | `private static int firstWornOut(WellKind kind)` |  |
| 200 | 3 | `public List<Vintage> vintages()` | The vintages as struck last, oldest first: what the save carries. |
| 205 | 3 | `public static boolean isLandWell(BuildingsTemplate t)` | Whether a building is a land well: one of this sector's MINING templates that lifts crude, and not a platform's well (0.7.91). |
| 210 | 3 | `public static boolean isWell(BuildingsTemplate t)` | Whether a building is a well of either kind: a land well, or a platform's (0.7.91). |
| 215 | 8 | `public int landWellsStanding()` | The land wells standing, finished. |
| 225 | 7 | `public double wellNameplate()` | A land well's nameplate a month: the template's crude (415 t); 0 with no such template. |
| 234 | 3 | `private int monthNow()` | The month the vintages are read at: the game's. |
| 244 | 25 | `public static List<Vintage> view(List<Vintage> kept, WellKind kind, int standing, int month)` | Vintages read against the wells standing (pure): of `kind`, those it holds past the wells standing are dropped oldest first, and wells standing past those it holds are a new vintage opened in `month`; vintages of the ... |
| 276 | 3 | `public List<Vintage> vintagesNow()` | The vintages this month, read against the wells standing (view()): what the lift and the retirements read - the land wells, and since 0.7.91 the platform wells in a platform's slots (THE OIL AT SEA). |
| 281 | 3 | `private List<Vintage> vintagesFor(int land, int platform, int month)` | The vintages read against `land` land wells and `platform` platform wells in slots, in `month`: the land's view, then the platforms'. |
| 286 | 7 | `public void strikeVintages()` | Strikes the vintages to this month's reading (endOfMonth(): once a month, after it has lifted). |
| 295 | 5 | `public void setVintagesForTest(List<Vintage> given)` | A fixture's vintages, struck as given, oldest first (WellCheck, SaveFileCheck). |
| 315 | 5 | `public double getCapacity(Good g)` | The crude the wells can lift a month (0.7.84): over this month's vintages, each vintage's wells times a well's nameplate times its kind's profile at its age, oldest first - cached on the month, the wells standing and ... |
| 322 | 5 | `public double getCapacityAtSea()` | The part of getCapacity(CRUDE) the platform wells lift (0.7.91): what the shuttle tankers and the pipelines carry. |
| 329 | 20 | `private void strikeLift()` | Sums the month's lift into the cache, if the month, the wells or the vintages moved. |
| 351 | 3 | `public double newWellsCapacity()` | The wells' nameplate at the template's figure, as if every one were new: what the decline is measured against. |
| 356 | 7 | `public int wornOut()` | The wells standing that are worn out this month: past their kind's life (lifeMonths()), under ten barrels a day. |
| 365 | 7 | `public int wornOut(WellKind kind)` | ...of one kind (0.7.91): the land wells at 263 months, the platform wells at 348. |
| 374 | 5 | `public BuildingsTemplate landWell()` | The land well the sector drills, its own template: what a worn-out well's retirement sells. |
| 382 | 6 | `public void endOfMonth(Game game)` | The month's loose ends (Markets.clearMonth()'s last step): the shuttle tankers paid for the month's crude ashore (0.7.91), the platforms and pipelines struck to what stood, and the vintages struck to the wells that st... |
| 390 | 11 | `protected void saveExtras(Map<String, Double> extras)` |  |
| 404 | 19 | `protected void restoreExtras(Map<String, Double> extras)` | A save from before 0.7.84 has none: its wells are read as new (view()). |
| 425 | 26 | `private void restoreOffshore(Map<String, Double> extras)` | The platforms and pipelines out of a save's extras (0.7.91): by their place in the list; a save from before them has none, and a key that does not read is skipped. |
| 453 | 7 | `protected void resetExtras()` |  |
| 478 | 12 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Whether to drill another well: one a month while an owned oil site is unworked and oil is left in the ground - Mining's rule, on oil (spec-land 2.7). |
| 494 | 1 | `public String atSeaWord()` |  |
| 497 | 52 | `public BusinessInvestment.Decision planOnLand(BusinessInvestment plans, Game game)` | The land well's order, or why not: Mining's rule on the dry sites (until 0.7.91 the whole of plan(); WellCheck 5 reads it). |
| 560 | 10 | `public double[] retirementDemandAndCapacity(Game game)` | A price-taking exporter sells what it lifts and shrinks on distress - while there is oil. |
| 579 | 5 | `public double unitsOf(BuildingsTemplate t)` | A well's part of the measure above (0.7.84): the average well's lift - the template's nameplate times the wells' lift over what they would lift new - so a field worked out sheds wells by the count, as it did before th... |
| 595 | 13 | `public boolean mayRetire(BuildingsTemplate t)` | Whether a holding may be sold back this month (0.7.91): a platform's jacket and a pipeline are not plant the lift measures, so the shrinking rules never pick them while they stand - they go when their wells have (Game... |

### THE OIL AT SEA (0.7.91, batch O10; runs/spec-oil.md 2.7 and 2.11, the (lines 609-943)

| line | len | member | says |
|---:|---:|---|---|
| 693 | 1 | **type** `public record Platform(int cell, int index, int wells, int month)` | A platform's jacket as it stands: its field (the world cell and index; -1 when it stands on none), the wells in its slots, and the month it opened. |
| 696 | 1 | **type** `public record Pipeline(int cell, int index, int km, int month)` | A field's crude pipeline: its field, the kilometres ordered for it, and the month the first was ordered. |
| 711 | 1 | `public List<Platform> platforms()` | The platforms as struck last (what the save carries). |
| 714 | 1 | `public List<Pipeline> pipelines()` | The pipelines as struck last. |
| 717 | 7 | `public void setOffshoreForTest(List<Platform> given, List<Pipeline> pipes)` | A fixture's platforms and pipelines, struck as given (WellCheck). |
| 726 | 5 | `private BuildingsTemplate templateWhere(java.util.function.Predicate<BuildingsTemplate> which)` | The first of this sector's templates that `which` picks, or null. |
| 733 | 1 | `public BuildingsTemplate platformTemplate()` | The Offshore Platform's jacket, the Platform Well and the Crude Pipeline: this sector's templates at sea, or null. |
| 734 | 1 | `public BuildingsTemplate platformWell()` |  |
| 735 | 1 | `public BuildingsTemplate pipelineTemplate()` |  |
| 737 | 1 | `private int standing(BuildingsTemplate t)` |  |
| 739 | 4 | `private int onSite(BuildingsTemplate t)` |  |
| 745 | 1 | `public int jacketsStanding()` | The jackets standing, finished. |
| 748 | 1 | `public int platformWellsStanding()` | The platform wells standing, finished - in a slot or not. |
| 751 | 4 | `public double platformWellNameplate()` | A platform well's nameplate a month: its template's crude (415 t); 0 with none. |
| 757 | 4 | `public int slotsEach()` | The wells a jacket holds: its template's slots (12); 0 with none. |
| 763 | 3 | `private long[] site()` | The city's founding site, {x, y} in plots: where every pipe runs to. |
| 768 | 6 | `public List<LandManager.SeaField> shallowFields()` | The oil fields the city owns in the sea no deeper than PLATFORM_MAX_DEPTH_M at their centre, in LandManager.seaFields()'s order: what a jacket stands on. |
| 776 | 3 | `public static boolean isShallow(LandManager.SeaField f)` | Whether a jacket may stand on a sea field (pure): in the sea, PLATFORM_MAX_DEPTH_M deep or less at its centre, with a site the city owns. |
| 781 | 4 | `static int sitesOf(List<LandManager.SeaField> fields, int cell, int index)` | A field's sites in the list, by its cell and index; 0 when the city owns none of it (or it is not a shallow sea field). |
| 787 | 4 | `static Deposit fieldOf(List<LandManager.SeaField> fields, int cell, int index)` | The field in the list by its cell and index, or null. |
| 798 | 14 | `public static int[] slotsOf(List<Platform> ps, List<LandManager.SeaField> fields, int slotsEach)` | Each platform's slots, in the list's order (pure): min(slotsEach, its field's sites less the slots of the platforms before it on the same field), never under none - none for a platform on no field, or on one the city ... |
| 814 | 10 | `public static int[] freeSites(List<Platform> ps, List<LandManager.SeaField> fields, int slotsEach)` | Each field's sea sites no platform in the list has slotted, in the list of fields' order (pure). |
| 826 | 15 | `public static int freeField(List<Platform> ps, List<LandManager.SeaField> fields, int slotsEach, long x, long y)` | The field a new jacket stands on (pure): the one with the most sea sites free, the nearest (x, y) on a tie, then the first listed; -1 for none free. |
| 848 | 19 | `public static List<Platform> platformView(List<Platform> kept, int jackets, int wells, int month, List<LandManager.SeaField> fi...` | The platforms read against `jackets` standing and `wells` platform wells (pure): those past the jackets dropped newest first, jackets past them opened in `month` on freeField() (or on none), then the wells in slots de... |
| 875 | 15 | `public List<Platform> platformsNow()` | The platforms this month, read against the jackets and platform wells standing (platformView()): what the slots, the lift and the shuttle read. |
| 891 | 1 | `private boolean sameFields(List<LandManager.SeaField> fields)` |  |
| 894 | 7 | `public int slotsStanding()` | The slots of the platforms standing, all of them. |
| 903 | 6 | `public int platformWellsInSlots()` | The platform wells in a slot: those that lift. |
| 911 | 9 | `public int platformRoom()` | How many more jackets the shallow fields the city owns could take: each field's sea sites no standing platform has slotted, a jacket to every slotsEach of them or part. |
| 922 | 1 | `public int jacketsOnSite()` | The jackets ordered and on site, not yet standing: what an order for another counts against platformRoom(). |
| 925 | 4 | `public int platformWellsCommitted()` | The platform wells standing and on site: what an order for another counts against the slots. |
| 931 | 12 | `public void strikeOffshore()` | Strikes the platforms and pipelines to this month's reading (endOfMonth(): once a month, after the month's crude is paid ashore). |

### the pipelines (lines 944-1044)

| line | len | member | says |
|---:|---:|---|---|
| 947 | 4 | `public static int lengthKm(Deposit field, long x, long y)` | A field's pipe, in whole kilometres: the straight line from its centre to (x, y), rounded up - at least one. |
| 953 | 1 | `public int pipeKmStanding()` | The kilometres of pipe standing, finished. |
| 956 | 4 | `public int pipeKmCommitted()` | ...and standing and on site: what the records hold. |
| 968 | 41 | `public static List<Pipeline> pipelineView(List<Pipeline> kept, int km, Platform wanted, List<Platform> ps, int month)` | The pipelines read against `km` kilometres committed (pure): those past them taken from the newest record; kilometres past the records go to `wanted`'s field (this month's order), else to the first platform field in `... |
| 1011 | 5 | `public List<Pipeline> pipelinesNow()` | The pipelines this month, read against the kilometres committed (pipelineView()). |
| 1022 | 11 | `public static java.util.Set<Long> pipedFields(List<Pipeline> ls, int kmStanding, List<LandManager.SeaField> fields, long x, lon...` | The fields whose whole pipe stands (pure): the kilometres standing dealt to the records oldest first, each field's pipe whole when its kilometres reach its length (lengthKm()). |
| 1035 | 9 | `public double pipedShare()` | The share of the platform wells in slots whose field's whole pipe stands: the share of the crude at sea the shuttle tankers do not carry. |

### the shuttle tankers (lines 1045-1089)

| line | len | member | says |
|---:|---:|---|---|
| 1048 | 4 | `public double shuttleFreightPerTonne()` | What a tonne of crude costs to bring ashore by shuttle tanker this month: CRUDE's band freight, baseFreight x freightFactor x the rate (spec-oil 2.7). |
| 1056 | 1 | `public double getShuttleTonnes()` |  |
| 1057 | 1 | `public double getShuttleBill()` |  |
| 1068 | 14 | `public void payShuttle()` | Pays the shuttle tankers for the month's crude brought ashore (endOfMonth(), after the crude's market cleared): the sea's share of what was sold at home - since 0.7.93 the offshore pool's part of the month's lift (get... |
| 1084 | 5 | `public double homeShareLastMonth()` | Last month's share of the wells' crude sold at home, by its money at home and abroad (the statement's): 0 with none sold. |

### the pipeline rule (lines 1090-1167)

| line | len | member | says |
|---:|---:|---|---|
| 1098 | 6 | `public static boolean pipelinePays(double freightPerTonne, double standingPerMonth, double tonnesPerMonth, double monthsLeft, d...` | The pipeline rule (pure; spec-oil 2.11): (the freight a tonne the pipe saves less its repairs and property tax a tonne) x the tonnes a month it carries x min(the months the oil lasts, PIPE_LIFE_MONTHS) is at least PIP... |
| 1115 | 2 | **type** `public record PipeCase(Deposit field, int km, double cost, double freight, double standing, double tonnes, ...` | A field's pipe weighed (spec-oil 2.11): its kilometres still to order, their cost, the freight a tonne it saves (the shuttle's at sea; none on land), its repairs and tax a month, the tonnes a month it would carry (the... |
| 1119 | 29 | `public PipeCase pipeCase(LandManager.SeaField f, BusinessInvestment plans)` | ...for a field the city owns a platform on; null with no pipeline template, or the field's whole pipe ordered. |
| 1150 | 17 | `public PipeCase bestPipe(BusinessInvestment plans)` | The field whose pipe pays best, by its life's saving over its cost; null for none. |

### the planner's three kinds at sea (lines 1168-1328)

| line | len | member | says |
|---:|---:|---|---|
| 1171 | 1 | **type** `public record JacketCase(LandManager.SeaField field, int wells, double earns, double cost)` | A platform's jacket weighed with its wells (spec-oil 2.7): its field, the wells it is judged with, the package's earnings a month and its cost. |
| 1174 | 14 | `public JacketCase jacketCase(BusinessInvestment plans)` | ...on freeField(): min(slotsEach, its free sea sites) wells at a platform well's earnings, less the jacket's running and standing costs; null with no field free or no templates. |
| 1190 | 8 | `private double shuttleOnOneMore(BuildingsTemplate well, BusinessInvestment plans)` | The platform crude's shuttle freight a month on one more well's crude sold at home: its nameplate at the plants' rate, as much of it as the house forecast leaves room for at home (BusinessInvestment.estimatedMakerProf... |
| 1208 | 12 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | What one more building of the wells' would clear a month (0.7.91): a platform well the house's maker estimate less the shuttle tankers on its crude sold at home; a jacket its share by cost of its package's (jacketCase... |
| 1222 | 6 | `private PipeCase pipeCaseOf(Platform at, BusinessInvestment plans)` | The pipe case of the field a record names, or null when the city holds it no longer. |
| 1230 | 46 | `private BusinessInvestment.Decision planAtSea(BusinessInvestment plans, Game game)` | The order at sea, or null when there is nothing at sea to weigh (THE OIL AT SEA, the planner). |
| 1278 | 50 | `public List<Line> ownLines(Game game)` |  |


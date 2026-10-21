# IndustryView.java - 883 lines · 64 methods · 6 constants · model

`ham/citybuildersim/IndustryView.java` - generated 2026-10-21 by CodeMap; line numbers are as of that run.

> Each materials industry's month as a picture (0.7.141, batch I11;
> docs/studies/spec-materials.md 2.13): where what it worked came from - its
> fields by type, filled to what remains, or the forest; its own works, the
> other industries, the city's old metal, the world - each kind of its
> buildings with its run, what the last one standing earns and the gate one
> more would fail, the products it made and who took them, the power its
> works made, drew and sold, and Forestry's forest: where it settles and the
> line its planting starts at. The figures, their words and their colours,
> for each of the nine industries.
> 
> WHY. The materials chains (batches I1 to I10) gave the city nine
> industries - Iron Mining, Iron & Steel, Coal Mining, Quarrying, Cement &
> Concrete, Copper Mining, Copper Refining, Forestry and Wood Products - and
> their Operations pages draw them as every business is drawn (SectorFlow):
> goods in, a ring of the rate, goods out. None of what the chains are made
> of shows there: not the fields by type and what is left in them, not the
> coke an oven hands its own furnace, not the old steel a yard buys, not why
> a works stands idle or why no second one is built, not the power a pulp
> mill sells, not where the forest settles. The refinery has had a picture
> since 0.7.95 (RefineryView, O11); this is the same for the nine, its
> figures held by a harness (IndustryViewCheck) rather than worked out in
> the screen. Since 0.7.143 (batch I12) IndustryPicture lays them out and
> ui/SectorScreen's OPERATIONS paints that, a roof per industry.
> 
> WHICH MONTH. The month the production rows were made on (Sector.Output,
> Sector.Input): what each good made, kept for its own works (captive), sold
> at home and shipped, and what each feed was bought at home and abroad -
> who sold it and who bought it off the market's own fills of the month
> (GoodsMarket.getTrades()). The rows and the fills are not saved, so after
> a load nothing of the month is counted until one runs (counted(),
> Game.hasMonthRun() - SectorFlow's rule). The ground, the forest, the old
> metal's last month, the plan's appraisals and the power are read as they
> stand.
> 
> Every figure is a model getter's or a sum of them: a unit's appraisal is
> its sector's own (ProcessingSector.appraise(), ExtractionSector.appraise()
> - the plan's gates, in SpreadPlanner's words), the last one's earnings the
> idle book's (ProcessingSector.lastEarns()), the forest's settling point
> Forestry.settlesAt()'s.
> 
> Pure: it reads the city and changes nothing; the rows are read without
> making any (Sector.outputRow(), inputRow()).

**Uses:** [Good](Good.md) (34), [Sectors](Sectors.md) (19), [Resource](Resource.md) (11), [Sector](Sector.md) (10), [Forestry](Forestry.md) (10), [SpreadPlanner](SpreadPlanner.md) (8), [RefineryView](RefineryView.md) (8), [Deposit](Deposit.md) (7), [OldMetal](OldMetal.md) (7), [Game](Game.md) (7), [Trade](Trade.md) (5), [CoalMining](CoalMining.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (3), [LandManager](LandManager.md) (3), [ExtractionSector](ExtractionSector.md) (3), [BuildingManager](BuildingManager.md) (2), [Markets](Markets.md) (2), [ProcessingSector](ProcessingSector.md) (2), [CopperRefining](CopperRefining.md) (2), [HeavyIndustry](HeavyIndustry.md) (2), [BusinessInvestment](BusinessInvestment.md) (1), [Quarrying](Quarrying.md) (1), [GoodsMarket](GoodsMarket.md) (1), [Formats](Formats.md) (1)

**Used by (3):** [IndustryPicture](IndustryPicture.md), [IndustryViewCheck](IndustryViewCheck.md), [SectorScreen](SectorScreen.md)

## Sections

| line | section |
|---:|---|
| 67 | THE FIGURES |
| 404 | · · the sources: the ground, the forest, the old metal |
| 429 | · · the units |
| 456 | · · the feeds |
| 471 | · · the products |
| 514 | · the forest |
| 530 | · the old metal |
| 559 | · a unit's share of its run |
| 586 | · who sold a feed, who bought a product |
| 621 | THE COLOURS (on the card's raised ground, RefineryView.GROUND) |
| 705 | THE WORDS |

## Enum constants

| line | constant | says |
|---:|---|---|
| 82 | `IndustryView.Roof.PIT` |  |
| 82 | `IndustryView.Roof.CAMP` |  |
| 82 | `IndustryView.Roof.FURNACE` |  |
| 82 | `IndustryView.Roof.KILN` |  |
| 82 | `IndustryView.Roof.MILL` |  |
| 99 | `IndustryView.Origin.FIELDS` | Its own ground: a field's ore, coal, stone or copper, the forest's wood. |
| 101 | `IndustryView.Origin.CAPTIVE` | Its own works: a good it makes and uses, to its own need first (Sector, CAPTIVE USE). |
| 103 | `IndustryView.Origin.HOME` | Bought, or sold, in the city: another industry, the households, the city. |
| 105 | `IndustryView.Origin.OLD_METAL` | The city's old steel or old copper (OldMetal), which is not a good. |
| 107 | `IndustryView.Origin.CARRIED` | Carried into another of its goods: the limestone no buyer took, into Quarrying's aggregate (Quarrying, THE CARRY). |
| 109 | `IndustryView.Origin.WORLD` | Landed from the world, or shipped to it. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 72 | `IndustryView.INDUSTRIES` | `List.of(Sectors.HEAVY_INDUSTRY, Sectors.MINING, Sectors.COAL_MINING, Sectors....` | The nine industries the view draws, in Sectors.KEYS' order. |
| 626 | `IndustryView.POWER` | `"#ffd866"` | Power, a ribbon to or from the grid. |
| 628 | `IndustryView.CAPTIVE` | `RefineryView.UNIT_EDGE` | A part from or to the industry's own works. |
| 630 | `IndustryView.HOME` | `"#6fa8dc"` | A part bought or sold in the city. |
| 632 | `IndustryView.OLD_METAL` | `"#9a8f7e"` | The city's old metal. |
| 634 | `IndustryView.WORLD` | `RefineryView.HOT` | A part from or to the world: imported, drawn hatched (the spec's 2.13), or shipped. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 63 | 821 | **type** `public final class IndustryView` | Each materials industry's month as a picture (0.7.141, batch I11; docs/studies/spec-materials.md 2.13): where what it worked came from - its fields by type, filled to what remains, or the forest; its own works, the ot... |
| 65 | 1 | `private IndustryView()` |  |

### THE FIGURES (lines 67-513)

| line | len | member | says |
|---:|---:|---|---|
| 77 | 3 | `public static boolean covers(Sector s)` | Whether a sector is one of the nine. |
| 82 | 1 | **type** `public enum Roof` | The roof an industry's works are drawn under (the spec's 2.13: furnace, mill, kiln, pit) - and ★ the crews' camp, Forestry's: a forest has no pit. |
| 85 | 10 | `public static Roof roofOf(Sector s)` | An industry's roof: the four that dig a pit's, Forestry's camp, the smelters' furnace, the kilns', the mills'. |
| 97 | 14 | **type** `public enum Origin` | Where a part of what an industry works came from, or where a part of what it made went. |
| 124 | 9 | **type** `public record Vessel(Resource resource, Deposit.FieldType type, int sites, int worked, int onAny, double ow...` | A field type's ground, drawn as a vessel filled to what remains. |
| 127 | 1 | `public double filled()` _(in IndustryView.Vessel)_ | The share of the vessel still in the ground; 0 with none owned. |
| 129 | 1 | `public double taken()` _(in IndustryView.Vessel)_ | What has been taken out of it. |
| 131 | 1 | `public int taking()` _(in IndustryView.Vessel)_ | The sites its works take, on its type and on ANY. |
| 157 | 9 | **type** `public record Forest(double owned, double standing, double protectedWood, double cuttable, double cleared, ...` | Forestry's forest (the spec's 2.11; Q13). |
| 162 | 1 | `public double lastsMonths()` _(in IndustryView.Forest)_ | Months the forest the crews may cut lasts every crew's cut, on site too; infinite with no crew. |
| 164 | 1 | `public boolean pastPlantingLine()` _(in IndustryView.Forest)_ | Whether the month's cut is past the planting line. |
| 174 | 1 | **type** `public record Part(Origin origin, String party, double units)` | A part of a feed by where it came from, or of a product by where it went. |
| 189 | 2 | **type** `public record Metal(Good metal, double arose, Map<OldMetal.Source, Double> bySource, double bought, double ...` | The city's old metal an industry buys (OldMetal; the spec's 2.9): the steel Iron & Steel's yards take, the copper Copper Refining's scrap refineries take, the pool's last month. |
| 204 | 16 | **type** `public record Feed(Good good, double needed, double captive, double oldMetal, double home, double imported,...` | A good an industry's works use, by where it came from. |
| 207 | 1 | `public double received()` _(in IndustryView.Feed)_ | Everything its works received of it: their own, the old metal, bought at home and landed. |
| 209 | 1 | `public double tonnes()` _(in IndustryView.Feed)_ | ...as tonnes. |
| 211 | 8 | `public List<Part> parts()` _(in IndustryView.Feed)_ | Its parts in drawing order: its own works, the old metal, each seller in the city, the world. |
| 237 | 16 | **type** `public record Product(Good good, double made, double captive, double home, double exported, double carriedI...` | A good an industry makes, and where it went. |
| 240 | 1 | `public double intoStock()` _(in IndustryView.Product)_ | Into the warehouse this month (below nothing: out of it): made and carried in, less every way it left. |
| 242 | 1 | `public double tonnes()` _(in IndustryView.Product)_ | Made, as tonnes. |
| 244 | 8 | `public List<Part> parts()` _(in IndustryView.Product)_ | Its parts in drawing order: to its own works, each buyer in the city, carried into another of its goods, the world. |
| 271 | 30 | **type** `public record Unit(BuildingsTemplate template, int standing, int onSite, double rate, double feedShare, boo...` | One kind of an industry's buildings. |
| 274 | 1 | `public double run()` _(in IndustryView.Unit)_ | The share of nameplate it ran at this month: its sector's rate times its feed's share. |
| 276 | 1 | `public double makes(Good g)` _(in IndustryView.Unit)_ | Units of a good its buildings standing make at its run. |
| 278 | 1 | `public double uses(Good g)` _(in IndustryView.Unit)_ | ...and use. |
| 280 | 6 | `public double tonnesIn()` _(in IndustryView.Unit)_ | Tonnes of goods and old metal into its buildings standing at its run. |
| 287 | 5 | `public double tonnesOut()` _(in IndustryView.Unit)_ | ...and tonnes of goods out. |
| 293 | 1 | `public double loss()` _(in IndustryView.Unit)_ | What a works' recipe loses on the way, tonnes: in less out - the slag, the gas, the water, the bark burned; NaN for a building on a site of the ground (a mine) or with no feed (a crew, a stand, a wash plant). |
| 295 | 1 | `public boolean weighed()` _(in IndustryView.Unit)_ | Whether the plan weighs one more of it. |
| 297 | 1 | `public double earnsOnCost()` _(in IndustryView.Unit)_ | What one more would earn a month on its cost (the plan's score); NaN for one it does not weigh. |
| 299 | 1 | `public SpreadPlanner.Gate gate()` _(in IndustryView.Unit)_ | The first gate one more fails; null when it passes them all, or is not weighed. |
| 312 | 4 | **type** `public record Power(double made, double drawn, double bought, double sold, double soldValue)` | The power an industry's works make, draw and trade with the grid, kW at nameplate (Sector, THE POWER ITS WORKS MAKE THEMSELVES). |
| 314 | 1 | `public boolean makes()` _(in IndustryView.Power)_ | Whether its works make any power. |
| 324 | 1 | **type** `public record Lorries(double vans, double needed, double diesel)` | The lorries an industry keeps for its tonnes (Sector, THE FLEET). |
| 347 | 46 | **type** `public record View(Sector sector, Roof roof, boolean counted, double rate, int standing, int onSite, List<V...` | An industry's month. |
| 351 | 1 | `public boolean drawn()` _(in IndustryView.View)_ | Whether there is a month to draw: a building standing, and the month counted. |
| 353 | 4 | `public Feed feed(Good g)` _(in IndustryView.View)_ | A feed, or null for a good its works neither use nor bought. |
| 358 | 4 | `public Product product(Good g)` _(in IndustryView.View)_ | A product, or null for a good it has no plant for, made none of and holds none of. |
| 363 | 4 | `public Unit unit(int id)` _(in IndustryView.View)_ | A kind of building by its permanent id, or null. |
| 368 | 5 | `public List<Unit> built()` _(in IndustryView.View)_ | The kinds with a building standing or on site: what the picture draws as works. |
| 374 | 6 | `public Unit best()` _(in IndustryView.View)_ | Of the kinds the plan weighs, the one it would order: passing every gate and earning most on its cost (SpreadPlanner.best()); null for none. |
| 381 | 5 | `public double tonnesIn()` _(in IndustryView.View)_ | Tonnes its works received, every feed. |
| 387 | 5 | `public double tonnesMade()` _(in IndustryView.View)_ | Tonnes it made, every product. |
| 395 | 107 | `public static View of(Game game, Sector sector)` | The month of `sector` in `game` (pure); null for a sector that is not one of the nine. |
| 504 | 9 | `public static List<View> all(Game game)` | All nine, in INDUSTRIES' order (pure). |

### the forest (lines 514-529)

| line | len | member | says |
|---:|---:|---|---|
| 517 | 12 | `static Forest forest(Game game, Forestry fo)` | Forestry's forest, every figure its own or the land's read (pure). |

### the old metal (lines 530-558)

| line | len | member | says |
|---:|---:|---|---|
| 533 | 25 | `static Metal metal(Game game, Sector sector)` | The old metal Iron & Steel's yards or Copper Refining's scrap refineries buy, its pool's last month; null for any other industry. |

### a unit's share of its run (lines 559-585)

| line | len | member | says |
|---:|---:|---|---|
| 568 | 17 | `static double feedShare(Game game, Sector sector, BuildingsTemplate t, double rate, boolean workedOut, CoalMining.Flow wash, bo...` | The share of its sector's rate a kind ran at for its feed or its ground (pure): a Scrap Yard's, the old steel the yards bought over what they would take at the rate; a crew's, the forest's cut over the crews' ask; a w... |

### who sold a feed, who bought a product (lines 586-620)

| line | len | member | says |
|---:|---:|---|---|
| 595 | 17 | `static List<Part> parties(Markets markets, Good g, String key, boolean sold)` | The month's fills of `g` in the city with `key` on one side, by the party on the other, in Sectors.KEYS' order and then the households and the city (pure): `sold` its sales to buyers here, else its purchases from sell... |
| 614 | 6 | `private static int rank(String party)` | A party's place: a sector by Sectors.KEYS, then the households, the city, anyone else. |

### THE COLOURS (on the card's raised ground, RefineryView.GROUND) (lines 621-704)

| line | len | member | says |
|---:|---:|---|---|
| 637 | 32 | `public static String colour(Good g)` | A good's colour: each chain's in its own family - iron's rust and greys, coal's blacks, stone's sands, copper's coppers and the acid's yellow, wood's browns and the paper's white; the oil products the refinery's. |
| 671 | 10 | `public static String colour(Origin o)` | A part's colour by where it came from or went. |
| 683 | 3 | `public static boolean hatched(Origin o)` | Whether a part is drawn hatched: what came from the world (the spec's 2.13, "imports hatched"). |
| 688 | 16 | `public static String colour(Resource r, Deposit.FieldType type)` | A field type's colour, its vessel's fill: the ore's own (hematite red, magnetite black), the coals', the stones', the coppers'; the forest's green. |

### THE WORDS (lines 705-883)

| line | len | member | says |
|---:|---:|---|---|
| 710 | 4 | `public static String figure(Good g, double units)` | A good's quantity in its own unit, short, for a band or a cell: "1,234 t", "830k m³", "2.00M L". |
| 716 | 4 | `public static String unitsWords(Good g, double units)` | ...and whole: "8,306 t", "1,640 m³", "60,000 L". |
| 722 | 4 | `public static String unitOf(Good g)` | A good's unit, short: "t", "m³", "L". |
| 728 | 4 | `public static String groundWords(Resource r, double amount)` | A resource's quantity in the ground, short: "12.3M t", "830k m³". |
| 734 | 3 | `public static String pct(double share)` | A share, whole: "87%". |
| 739 | 3 | `public static String kw(double kw)` | kW, whole: "13,500 kW". |
| 744 | 3 | `public static String money(double thousands)` | A month's money as the screens write it (Formats.amount()). |
| 749 | 5 | `public static String typeWords(Resource r, Deposit.FieldType type)` | A type in words: "Hematite", "Hard rock"; the forest "Forest". |
| 756 | 6 | `public static String vesselWords(Vessel v)` | A vessel's line: "Hematite · 3 of 5 sites worked · 12.3M t of 20.0M t left"; the forest's "Forest · 1.23M m³ of 2.34M m³ standing". |
| 764 | 10 | `public static String partWords(Part p, Game game, boolean product)` | A part's words: who it came from or went to - "its own works", "Coal Mining", "the households", "the city's old steel", "imported", "shipped". |
| 776 | 6 | `public static String partyWords(String party, Game game)` | A party in the city by name: a sector's label, "the households", "the city". |
| 784 | 3 | `public static String metalName(Metal m)` | The old metal's name: "old steel", "old copper". |
| 789 | 4 | `public static String metalWords(Metal m)` | The old metal's line: "old steel: 12,353 t arose · 10,500 t bought at $210.00 · 1,853 t to landfill". |
| 795 | 6 | `static String names(String name, int n)` | A building's name, plural for more than one: "Coke Ovens", "Quarries", "Sawmills". |
| 803 | 16 | `public static String unitWords(Unit u)` | A kind's line: "2 Coke Ovens · runs 87% · 1 on site"; a mine worked out says so; a stand its growth: "3 Woodlots · grow 500 m³ a month"; with none standing "1 Harvesting Operation on site", "no Sawmill". |
| 821 | 7 | `public static String lastWords(Unit u)` | What the last one standing earns, as the idle book counts it: "the last one earns $12.3k a month", "...loses $4.1k a month · idle 3 months"; "" for a mine or none standing. |
| 830 | 9 | `public static String gateWords(Unit u)` | One more, in the plan's words: "one more earns $1,234.5k a month on $123,456k at the city's prices, 1.00% a month" (ProcessingSector.plan()'s), or the first gate it fails, its refusal: "one more: would lose $295.8k a ... |
| 841 | 9 | `public static String gateName(SpreadPlanner.Gate g)` | A gate's name, as SpreadPlanner orders them: "its feed" (a mine's ground, a crew's forest), "the ground", "the staff", "the money". |
| 852 | 7 | `public static String powerWords(Power p)` | The works' power: "makes 8,000 kW, draws 2,200 kW · sells 5,800 kW to the grid"; with none made "draws 750 kW from the grid". |
| 861 | 13 | `public static List<String> forestWords(Forest f)` | The forest's lines, top to bottom: what stands and may be cut, the cut against the regrowth and the planting line, and where it settles with the law and without. |
| 876 | 7 | `public static String headline(View v)` | The industry's line: "Iron & Steel · 5 works at 87%", "Forestry · nothing standing"; a city just loaded "...· a month must run". |


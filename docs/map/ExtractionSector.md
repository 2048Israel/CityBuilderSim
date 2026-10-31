# ExtractionSector.java - 598 lines · 33 methods · 0 constants · sectors

`ham/citybuildersim/sectors/ExtractionSector.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> A sector that digs or cuts one resource out of the ground the city owns:
> the template every extraction industry of the materials chains extends
> (0.7.112, batch I2; docs/studies/spec-materials.md 2.1 and 2.6b).
> 
> WHY. The materials chains bring four more industries that lift their
> good off the city's fields - Coal Mining, Quarrying, Copper Mining and
> Forestry - and each is Mining's shape: the ground, not the plant, is the
> limit; a mine stands on a site of a field the city bought; one order at a
> time, the best return on its cost; and a mine over a worked-out field is
> spare by any measure. Until 0.7.112 that shape was Mining's own code, for
> iron. Written here once, a chain's extraction sector says only which
> resource it lifts and what it makes, and stays a declaration.
> 
> WHAT IT HOLDS, from Mining as it was to 0.7.111, to the bit:
>   - THE GROUND (groundLimit()): what the month lifts of its resource's
>     good is what the city's ground still holds of it (dig(): LandManager
>     .extract()), nothing once it is out;
>   - THE PLAN (plan()): one order in flight; a free site of its resource
>     (the sites owned less those its buildings stand on, Game.committedOn())
>     and the resource still in the ground; of the buildings that make its
>     good and the city could staff, the best estimatedMonthlyProfit() over
>     its cost; then ground for it. To 0.7.119 the money gate was
>     Game.consider()'s alone (on the order's whole cost since 0.7.113,
>     batch I2b); since 0.7.120 (batch I3; spec-materials 2.6b) the plan
>     asks it first, through SpreadPlanner's MONEY gate (planner(): on the
>     whole cost, decision A), so a mine that would not carry its interest
>     is not the one ordered and the sector says why - consider() asks the
>     same of the order after it;
>   - THE ESTIMATE (estimatedMonthlyProfit()): the maker's (Sector's), less
>     what a mine buys abroad that is not a good, its consumables at the
>     exchange factor (SpreadPlanner.ownCosts(), since 0.7.120: the Iron
>     Mine's explosives and wear parts) - what the plan, consider() and the
>     build card read;
>   - THE RETIREMENT (retirementDemandAndCapacity()): a price-taking
>     exporter shrinks on distress only, while there is ore; past it, all
>     its plant is spare;
>   - THE PAGE (ownLines()): the deposits found and the ones being worked.
> 
> BY TYPE AND SIZE SINCE 0.7.124 (batch I5; spec-materials 2.4, 2.6b; the
> study's 1.3: "the sector chooses the mine type that fits"). A building's
> site (BuildingsTemplate.Site) says the type of field it stands on, the
> sites it takes and the tonnes of field a unit it makes digs, and each of
> the four rules above reads it:
>   - THE GROUND by type: the month's ask split by each type's share of the
>     sector's nameplate, each part dug from its own type's ground at its
>     dig (a magnetite works three tonnes of rock a tonne of concentrate),
>     LandManager.extract(r, type, t) - so a hematite mine never lifts a
>     magnetite tonne, and W holds by type;
>   - THE PLAN only among the mines that fit: its type's ore left, and room
>     for its sites on ONE field of its type beside the works standing
>     (Game.claimsOn(), LandManager.fits()) - a Large Open-Pit Mine twenty
>     free sites of one hematite field - the best earnings on the whole cost
>     winning; the size is the free sites' (Jerus's Q19);
>   - THE ESTIMATE and the plan's cost with the mine's freight (★, as I4
>     weighs every works: ProcessingSector.freightRunning(), fleetCapital());
>   - THE RETIREMENT by type: a mine whose type is worked out is spare while
>     the other type still has ore, and only it may be sold.
> 
> A STOCKABLE GOOD'S LIFT (0.7.126, batch I6): groundLimit() reaches only
> the flow goods. A good the sector stocks - Coal Mining's two coals, Copper
> ... (9 more lines in the source)

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (25), [LandManager](LandManager.md) (15), [BusinessInvestment](BusinessInvestment.md) (15), [Deposit](Deposit.md) (10), [SpreadPlanner](SpreadPlanner.md) (8), [Resource](Resource.md) (5), [ProcessingSector](ProcessingSector.md) (5), [Good](Good.md) (4), [Game](Game.md) (3), [Formats](Formats.md) (2), [Sector](Sector.md) (1), [BuildingType](BuildingType.md) (1)

**Used by (17):** [BuildAdvice](BuildAdvice.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [CoalMining](CoalMining.md), [CoalMiningCheck](CoalMiningCheck.md), [CopperMining](CopperMining.md), [CopperMiningCheck](CopperMiningCheck.md), [Forestry](Forestry.md), [ForestryCheck](ForestryCheck.md), [IndustryView](IndustryView.md), [IndustryViewCheck](IndustryViewCheck.md), [MaterialsCheck](MaterialsCheck.md), [Mining](Mining.md), [OreView](OreView.md), [Quarrying](Quarrying.md), [QuarryingCheck](QuarryingCheck.md)

## Sections

| line | section |
|---:|---|
| 109 | · the ground |
| 183 | · the estimate |
| 244 | · the dig |
| 261 | · the plan |
| 453 | · the retirement |
| 515 | · the page |
| 545 | THE PICTOGRAMS' READS (0.7.141, batch I11; docs/studies/spec-materials.md |

## Fields (state)

| line | field | says |
|---:|---|---|
| 93 | `private final Resource resource` | The resource whose ground it lifts. |
| 96 | `private final SpreadPlanner planner` | Its spread planner, for its money gate (0.7.120, batch I3; spec-materials 2.6b): ON_ITS_WHOLE_COST unless a check or a probe sets another. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 90 | 509 | **type** `public abstract class ExtractionSector extends Sector` | A sector that digs or cuts one resource out of the ground the city owns: the template every extraction industry of the materials chains extends (0.7.112, batch I2; docs/studies/spec-materials.md 2.1 and 2.6b). |
| 98 | 4 | `protected ExtractionSector(String key, String label, BuildingType group, Resource resource)` |  |
| 104 | 1 | `public final Resource resource()` | The resource whose ground it lifts. |
| 107 | 1 | `public final Good groundGood()` | The good its ground limits: its resource's (Resource.good()). |

### the ground (lines 109-182)

| line | len | member | says |
|---:|---:|---|---|
| 121 | 20 | `protected double groundLimit(Good g, double asked)` | The ground, not the mine, decides what comes up - since 0.7.124 by type (BY TYPE AND SIZE, above): the month's ask split by each kind of dig's share of the nameplate (digs()), each part's field tonnes - the ask times ... |
| 143 | 3 | `protected double dig(LandManager land, double asked)` | What the ground gives of `asked` this month: its resource's remainder taken out (LandManager.extract()). |
| 153 | 3 | `protected double dig(LandManager land, Deposit.FieldType type, double fieldTonnes, boolean more)` | ...of one type (0.7.124): `fieldTonnes` of its type's ground (null for ANY, the walk) taken out (LandManager.extract(r, type, t)), what was there; `more` when another type was dug before it this month. |
| 158 | 1 | **type** `record Dig(Deposit.FieldType type, double dig, double capacity)` | One kind of dig its buildings make: the type of field they stand on (null for ANY), the tonnes of field a unit takes, and their nameplate of the good. |
| 165 | 14 | `List<Dig> digs(Good g)` | Its buildings standing that make `g`, by the type of field they stand on and their dig (a building with no site of its resource on ANY at a tonne a tonne), each kind's nameplate, in the catalogue's order. |
| 181 | 1 | `public SpreadPlanner planner()` | Its spread planner (0.7.120): its money gate - a check or a probe sets the counterfactual on it. |

### the estimate (lines 183-243)

| line | len | member | says |
|---:|---:|---|---|
| 186 | 3 | `public double exchangeFactor()` | What its mines' consumables are paid at: the exchange factor every world price is converted at; 1 with no markets. |
| 201 | 7 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | What one more of its mines would clear a month (0.7.120, batch I3): the maker's estimate (Sector's - its good at home and abroad, its inputs, its wages, power, water, repairs and tax, at the rate its plant runs) less ... |
| 234 | 4 | `public double freightRunning(BuildingsTemplate t, double rate)` | What the freight of one of `t` costs a month at `rate`: ProcessingSector.freightRunning() at what a van and a litre cost here and at Haulage's quote (legPrice()); none with no markets. |
| 240 | 3 | `public double fleetCapital(BuildingsTemplate t)` | ...and buying its vans: ProcessingSector.fleetCapital() (no truck is in a mine's cost: FR2); none with no markets. |

### the dig (lines 244-260)

| line | len | member | says |
|---:|---:|---|---|
| 247 | 3 | `public double getPotentialOutput()` | What its buildings could lift this month if the ground allowed it. |
| 252 | 3 | `public int sitesOwned()` | The sites of its resource the city owns: how many of its buildings can stand. |
| 257 | 3 | `public int sitesWorked()` | ...and those its buildings stand on, standing, on site or ordered (Game.committedOn()). |

### the plan (lines 261-452)

| line | len | member | says |
|---:|---:|---|---|
| 279 | 80 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Whether to open another mine. |
| 366 | 3 | `protected boolean lifts(BuildingsTemplate t)` | Whether a building of its lifts its ground - a mine the plan weighs and the retirement measures: one that makes its ground's good (Resource .good()). |
| 371 | 3 | `public boolean fits(BuildingsTemplate t)` | Whether one more of `t` fits the city's ground now (0.7.124): fits() below against the works standing, on site or ordered (Game.claimsOn()) - what the plan asks of each candidate, and the build card may. |
| 382 | 5 | `boolean fits(LandManager land, BuildingsTemplate t, List<LandManager.Claim> standing)` | Whether one more of `t` fits the city's ground beside the claims standing (0.7.124): a building that stands on its resource's sites needs ore of its type left (getRemaining(r, type)) and room for its sites on one fiel... |
| 396 | 10 | `private String noField(LandManager land, BuildingsTemplate t)` | Why the first mine that does not fit cannot stand: its type worked out, or no field of its type with room for its sites - each in its own resource's words since 0.7.142 (batch I13; the docs pass's flag, CONFIRM 0.7.13... |
| 412 | 4 | `public String fieldWanting(BuildingsTemplate t)` | Why one more of `t` cannot stand on the city's ground now, in the plan's words (noField()), or null when it fits (fits()): the build card's field, the planner's feed for a mine (0.7.142, batch I13). |
| 418 | 3 | `static String typeWord(Deposit.FieldType type)` | A field type in words: "hematite", "magnetite". |
| 428 | 11 | `public static String oreWords(Resource r, Deposit.FieldType type)` | What a resource's ground holds, of a type, in words (0.7.142): "hematite ore", "coking coal", "sulphide copper ore", "limestone", "hard rock"; of ANY (null) or a resource of one type its own - "iron ore", "coal", "cop... |
| 441 | 11 | `public static String fieldWords(Resource r, Deposit.FieldType type)` | ...and one field of it (0.7.142): "hematite field", "thermal coal field", "oxide copper field", "limestone field"; "iron field", "stone field" of ANY. |

### the retirement (lines 453-514)

| line | len | member | says |
|---:|---:|---|---|
| 471 | 10 | `public double[] retirementDemandAndCapacity(Game game)` | A price-taking exporter always sells what it lifts: it shrinks on distress only - WHILE THERE IS ORE. |
| 483 | 10 | `double workedOutCapacity(LandManager land)` | Its nameplate standing on a type whose ore is worked out (0.7.124): the mines that lift nothing. |
| 495 | 6 | `boolean workedOut(LandManager land, BuildingsTemplate t)` | Whether a building of its stands on ground with nothing left: its type's ore (ANY or no site: its resource's) worked out. |
| 509 | 5 | `public boolean mayRetire(BuildingsTemplate t)` | ...and which of its mines may be sold (0.7.124): while any stands over a worked-out type, only those - by the spare-capacity rule above and by the distress rule alike, so a sector in trouble sells the mines that lift ... |

### the page (lines 515-544)

| line | len | member | says |
|---:|---:|---|---|
| 518 | 26 | `public List<Line> ownLines(Game game)` |  |

### THE PICTOGRAMS' READS (0.7.141, batch I11; docs/studies/spec-materials.md (lines 545-598)

| line | len | member | says |
|---:|---:|---|---|
| 563 | 3 | `public boolean isWeighed(BuildingsTemplate t)` | Whether the plan weighs a building of its: one that lifts its ground (lifts()); Coal Mining's wash plants too. |
| 573 | 6 | `protected String groundRefusal(BuildingsTemplate t)` | Its ground's refusal of one more `t`, in plan()'s words, or null when the ground has room for it: a site of its resource unworked, its ore not worked out, and room for its sites on one field of its type (fits(), noFie... |
| 587 | 5 | `public SpreadPlanner.Candidate appraise(BuildingsTemplate t, BusinessInvestment plans)` | One more of `t` weighed as plan() weighs it (above), every gate asked: its ground (groundRefusal()), the ground for the building, the staff, and the money on its whole cost - its order and its lorries (fleetCapital())... |
| 594 | 4 | `public boolean isWorkedOut(BuildingsTemplate t)` | Whether a building of its stands over ground with nothing left of its type (workedOut()): a mine that lifts nothing; false with no city. |


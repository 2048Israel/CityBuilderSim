# Quarrying.java - 400 lines · 19 methods · 3 constants · sectors

`ham/citybuildersim/sectors/Quarrying.java` - generated 2026-10-21 by CodeMap; line numbers are as of that run.

> Quarries - Quarrying (0.7.131, batch I7; docs/studies/spec-materials.md
> 2.1, 2.4, 2.10 and 5's I7 row; the study's 6): the third materials chain's
> digging industry, on Sectors.KEYS after Coal Mining - its end when it came
> (Jerus's Q21, 2026-10-08: iron, coal, stone, copper, wood).
> 
> WHY. Since the integrated route (0.7.123, batch I4) the blast furnaces
> have taken LIMESTONE as flux and the oxygen shop LIME, and nobody in the
> city made either, so every tonne was imported. Stone lies in fields of two
> types (the study's 1.4): limestone (70% of fields), which kilns and blast
> furnaces must have, and hard rock - granite and the like - which is
> crushed for aggregate or cut into blocks. Real crushed stone is 70%
> limestone [S1], and what the kilns leave is sold as aggregate at the
> aggregate price. So this sector's one rule of its own is THE CARRY.
> 
> THE THREE BUILDINGS (the study's 6.4; ids 115 to 117):
>   - the QUARRY, 40,000 t a month on one stone site of either type;
>   - the LARGE QUARRY, 250,000 t on three sites of one field;
>   - the DIMENSION STONE QUARRY, 1,000 t of blocks a month on one HARD ROCK
>     site, three tonnes of rock a tonne of blocks.
> The Quarry and the Large Quarry stand on ANY stone field, and their rows
> say they make AGGREGATE, stone's pricing good (Resource.STONE).
> 
> WHICH FIELD A QUARRY IS ON (spec 2.4: "fits() packs ANY quarries onto
> limestone first"). No quarry is recorded on a field; LandManager.packed()
> places the quarries standing - then those on site - as fits() would, a
> works on a type first, ANY onto the first type, limestone, first. A
> quarry's sites on limestone make LIMESTONE, those on hard rock AGGREGATE
> (nameplate()): what getCapacity() and getPipeline() read, so the
> Quarrying's limestone is what the market sees. A quarry the ground has no
> room for (a fixture placed by hand, or an older save) reads as on
> limestone, the first type.
> 
> THE CARRY (spec 2.10; the study's 6.1; Jerus's Q17, "limestone sells as
> aggregate, one way"). LIMESTONE clears before AGGREGATE - Good's order -
> so when limestone's market has taken what the kilns and furnaces want,
> shipUnsoldFlow(LIMESTONE) books nothing: it carries the tonnes left into
> the month's AGGREGATE output, which sells them at home or ships them at
> aggregate's price. The ground they came out of stays limestone (the dig
> is by type), so W holds; the limestone's row keeps what sold as
> limestone, and carried() what went on as aggregate - which the works that
> burn limestone weigh it at, its export price (ProcessingSector.values()).
> Never the other way: hard rock is not limestone.
> 
> THE GROUND. All three goods are flow goods, so groundLimit() digs them:
> limestone from limestone ground, aggregate and blocks from hard rock, each
> at its quarries' dig (one tonne of field a tonne; three for blocks), with
> LandManager.extract(r, type, t), so E and W hold by type.
> 
> THE PLAN is ExtractionSector's: the best earnings on the whole cost among
> the quarries that fit, their freight in it - its estimate the quarry's
> increment to the packing (madeBy()): one more on ANY makes limestone while
> a limestone field has room, and its limestone past what the city buys of
> it is valued as aggregate. Who buys: Iron & Steel's blast furnaces and
> Cement & Concrete's kilns and cement plants the limestone; nobody here the
> aggregate or the blocks until the construction upgrade (Q18), so they ship
> - a quarry pays where it replaces imported limestone (the study's 6.5).

**Uses:** [Good](Good.md) (43), [Resource](Resource.md) (15), [BuildingsTemplate](BuildingsTemplate.md) (15), [Deposit](Deposit.md) (8), [LandManager](LandManager.md) (6), [GoodsMarket](GoodsMarket.md) (2), [BusinessInvestment](BusinessInvestment.md) (2), [Game](Game.md) (2), [Formats](Formats.md) (2), [BuildCard](BuildCard.md) (2), [ExtractionSector](ExtractionSector.md) (1), [Sectors](Sectors.md) (1), [BuildingType](BuildingType.md) (1), [SpreadPlanner](SpreadPlanner.md) (1)

**Used by (9):** [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildingDataCheck](BuildingDataCheck.md), [CementConcreteCheck](CementConcreteCheck.md), [IndustryPicture](IndustryPicture.md), [IndustryView](IndustryView.md), [IndustryViewCheck](IndustryViewCheck.md), [QuarryingCheck](QuarryingCheck.md), [Sectors](Sectors.md)

## Sections

| line | section |
|---:|---|
| 103 | · which field a quarry is on |
| 231 | · the ground |
| 271 | · the carry |
| 306 | · the estimate |
| 332 | · the plan and the retirement |
| 376 | · the page |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 81 | `Quarrying.QUARRY` | `115` | The Quarry's permanent id in buildings.json (0.7.131): 40,000 t a month on one stone site of either type. |
| 84 | `Quarrying.LARGE_QUARRY` | `116` | The Large Quarry's (0.7.131): 250,000 t a month on three sites of one stone field. |
| 87 | `Quarrying.DIMENSION_STONE_QUARRY` | `117` | The Dimension Stone Quarry's (0.7.131): 1,000 t of blocks a month on one hard-rock site. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 90 | `private double carried` | Limestone carried into aggregate at the last clearing (THE CARRY), tonnes; nothing until limestone first clears. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 78 | 323 | **type** `public final class Quarrying extends ExtractionSector` | Quarries - Quarrying (0.7.131, batch I7; docs/studies/spec-materials.md 2.1, 2.4, 2.10 and 5's I7 row; the study's 6): the third materials chain's digging industry, on Sectors.KEYS after Coal Mining - its end when it ... |
| 92 | 10 | `public Quarrying()` |  |

### which field a quarry is on (lines 103-230)

| line | len | member | says |
|---:|---:|---|---|
| 106 | 3 | `static boolean onAny(BuildingsTemplate t)` | Whether a building of its stands on a stone site of either type: the Quarry and the Large Quarry. |
| 111 | 3 | `static double tonnesOf(BuildingsTemplate t)` | What a quarry digs a month at nameplate, whatever field it stands on: what its row makes of aggregate and blocks. |
| 125 | 67 | `double[] nameplate(boolean onSite, BuildingsTemplate plus)` | Its quarries' nameplate a month by good, placed as the ground packs them (pure but for the city's ground): {LIMESTONE, AGGREGATE, DIMENSION_STONE} of the quarries standing, with `onSite` those on site too, and with `p... |
| 194 | 3 | `private static int slot(Good g)` | The index of a good in nameplate()'s array, or -1 for one it does not make. |
| 200 | 4 | `public double getCapacity(Good g)` | Nameplate of each of its goods a month: the quarries standing as the ground packs them - those on limestone make limestone, on hard rock aggregate. |
| 207 | 5 | `public double getPipeline(Good g)` | ...and what the quarries on site will add, which counts as supply for the planner: the packing with them less the packing without, never below nothing (one on site can push no standing quarry off its field, but a fixt... |
| 219 | 11 | `public Map<Good, Double> madeBy(BuildingsTemplate t)` | What one more of `t` adds to its nameplate, on top of what stands and is on site: {good: tonnes} - one more on ANY makes limestone while a limestone field has room for it, aggregate on hard rock past that; the Dimensi... |

### the ground (lines 231-270)

| line | len | member | says |
|---:|---:|---|---|
| 243 | 10 | `protected double groundLimit(Good g, double asked)` | The ground decides what comes up, by type: the month's ask of a good - limestone out of limestone ground, aggregate and blocks out of hard rock - at its quarries' dig (one tonne of field a tonne of stone, three a tonn... |
| 255 | 15 | `double digOf(Good g)` | The tonnes of field a tonne of a good takes: its quarries' dig, weighted by their nameplate of it (a tonne a tonne for limestone and aggregate, three for blocks); 1 with none standing. |

### the carry (lines 271-305)

| line | len | member | says |
|---:|---:|---|---|
| 275 | 4 | `public void produceFlow(Good g)` | The month's limestone is made: and the last month's carry is spent, so carried() is this month's from here (THE CARRY). |
| 288 | 12 | `public void shipUnsoldFlow(Good g, GoodsMarket market)` | A flow good's unsold units, once its market has taken what it wants: LIMESTONE's carried into the month's AGGREGATE output, tonne for tonne, booking nothing (THE CARRY) - aggregate clears after it and sells them at ho... |
| 302 | 3 | `public double carried()` | Limestone carried into aggregate at the last clearing, tonnes (THE CARRY): what the works that burn limestone weigh it at its export price for (ProcessingSector.values()). |

### the estimate (lines 306-331)

| line | len | member | says |
|---:|---:|---|---|
| 315 | 16 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | What one more of its quarries would clear a month: ExtractionSector's estimate on what it adds to the packing (madeBy()), not its row's makes - limestone the city would buy at the makers' rule, and the rest of it as a... |

### the plan and the retirement (lines 332-375)

| line | len | member | says |
|---:|---:|---|---|
| 336 | 3 | `protected boolean lifts(BuildingsTemplate t)` | A quarry of its digs stone: aggregate or limestone, or the Dimension Stone Quarry's blocks. |
| 342 | 3 | `public double unitsOf(BuildingsTemplate t)` | What one of its quarries contributes to the measures it is retired on: the tonnes it digs a month at nameplate. |
| 354 | 9 | `public double[] retirementDemandAndCapacity(Game game)` | Stone worked out leaves its quarries spare, measured in the tonnes they dig: all of them once the city's stone is out; while some is left, those over a type worked out (ExtractionSector's workedOut(): the Dimension St... |
| 366 | 9 | `double workedOutCapacity(LandManager land)` | Its nameplate standing over a type worked out, in the tonnes each digs (unitsOf()): the blocks' once the hard rock is out. |

### the page (lines 376-400)

| line | len | member | says |
|---:|---:|---|---|
| 379 | 21 | `public List<Line> ownLines(Game game)` |  |


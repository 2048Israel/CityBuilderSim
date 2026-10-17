# CopperMining.java - 256 lines · 12 methods · 5 constants · sectors

`ham/citybuildersim/sectors/CopperMining.java` - generated 2026-10-17 by CodeMap; line numbers are as of that run.

> Copper mines and the leach plants on oxide fields - Copper Mining (0.7.133,
> batch I8; docs/studies/spec-materials.md 2.1, 2.4 and 5's I8 row; the
> study's - materials-chains-research.md - 4): the fourth materials chain's
> digging industry, on the end of Sectors.KEYS with Copper Refining, after
> Cement & Concrete (Jerus's Q21, 2026-10-08: iron, coal, stone, copper,
> wood).
> 
> WHY. Copper lies in the world's ground since 0.7.56 and its fields have had
> two types since 0.7.110 - sulphide (80% of fields) and oxide (20%: SX-EW
> was 21% of mine output in 2024 [K2]) - but nothing dug it, so its ground
> was free. Ore at 0.5% copper has no market [K12], so a sulphide mine is a
> mine AND MILL: it floats its ore into a concentrate of 30% copper, which is
> what it sells, and a mine is worth building before anything downstream
> exists (the study's 1.1). An oxide field is leached with acid instead and
> makes finished copper - cathode - at the mine, buying the acid the
> smelters make (Jerus's Q12: acid is traded).
> 
> THE THREE BUILDINGS (the study's 4.3; ids 121 to 123):
>   - the COPPER MINE & MILL, 2,200 t of concentrate a month (660 t of
>     copper) from 150,000 t of ore at 0.5%, on one sulphide site;
>   - the LARGE COPPER MINE, 26,400 t from 1.8 Mt, on eight sites of one
>     sulphide field - an open-pit porphyry mine, 1906 (Bingham Canyon);
>   - the LEACH & SX-EW PLANT, 1,000 t of cathode from 280,000 t of ore and
>     3,500 t of acid, on one oxide site - 1968 (Bluebird, Arizona).
> 
> THE GROUND (spec 2.4). A field holds tonnes of copper metal, and a
> building's site says how much of it a unit made takes: CONC_DIG, .341 t a
> tonne of concentrate (the mill's 88% recovery of 0.5% ore into 30%), and
> LEACH_DIG, 1.4 t a tonne of cathode (72% recovered). Concentrate is a flow
> good, so groundLimit() digs it out of the sulphide ground as the month
> makes it, as Iron Mining's ore is dug; cathode is stockable, so - as Coal
> Mining's coals are (batch I6) - a month's cathode is what the leach
> plants can make at the sector's rate held to what the oxide ground still
> holds (lift(), through Sector.madeAtRate()), and the same dig comes out of
> the ground at the month's end (endOfMonth()). Either way
> LandManager.extract(r, type, t), so E and W hold by type.
> 
> THE PLAN is ExtractionSector's: the best earnings on the whole cost among
> the mines that fit a field of their type, their lorries in it. What it pays
> (the study's 4.4, at parity): the Copper Mine & Mill 2.6% of its cost a
> month exporting its concentrate and 2.9% replacing imported concentrate,
> the Large Copper Mine 2.8% and 3.1%, the leach plant 3.1-3.4% - copper is a
> mine worth building, its field's price the gate (Resource.COPPER: a tonne
> of metal in the ground at 1/100 of cathode's export price).
> 
> Who buys: Copper Refining's smelters the concentrate, its rod mills the
> cathode; the world the rest. The leach plants buy Copper Refining's acid
> first, the world's past it.

**Uses:** [Good](Good.md) (14), [LandManager](LandManager.md) (5), [Resource](Resource.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (4), [Game](Game.md) (3), [Formats](Formats.md) (2), [BuildCard](BuildCard.md) (2), [ExtractionSector](ExtractionSector.md) (1), [Sectors](Sectors.md) (1), [BuildingType](BuildingType.md) (1), [Deposit](Deposit.md) (1)

**Used by (6):** [BuildCardCheck](BuildCardCheck.md), [BuildingDataCheck](BuildingDataCheck.md), [CopperMiningCheck](CopperMiningCheck.md), [CopperRefiningCheck](CopperRefiningCheck.md), [IndustryViewCheck](IndustryViewCheck.md), [Sectors](Sectors.md)

## Sections

| line | section |
|---:|---|
| 95 | · the ground |
| 187 | · the plan and the retirement |
| 234 | · the page |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 70 | `CopperMining.MINE_AND_MILL` | `121` | The Copper Mine & Mill's permanent id in buildings.json (0.7.133): 2,200 t of concentrate a month on one sulphide site. |
| 73 | `CopperMining.LARGE_MINE` | `122` | The Large Copper Mine's (0.7.133): 26,400 t of concentrate a month on eight sites of one sulphide field. |
| 76 | `CopperMining.LEACH_PLANT` | `123` | The Leach & SX-EW Plant's (0.7.133): 1,000 t of cathode a month on one oxide site, of 3,500 t of acid. |
| 79 | `CopperMining.CONC_DIG` | `.341` | Tonnes of a sulphide field's copper a tonne of concentrate takes: .341 (spec 2.4; the study's 4.3 - 150,000 t of ore at 0.5% into 2,200 t of concentrate). |
| 82 | `CopperMining.LEACH_DIG` | `1.4` | Tonnes of an oxide field's copper a tonne of leached cathode takes: 1.4 (spec 2.4; the study's 4.3 - 280,000 t of ore at 0.5% into 1,000 t). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 67 | 190 | **type** `public final class CopperMining extends ExtractionSector` | Copper mines and the leach plants on oxide fields - Copper Mining (0.7.133, batch I8; docs/studies/spec-materials.md 2.1, 2.4 and 5's I8 row; the study's - materials-chains-research.md - 4): the fourth materials chain... |
| 84 | 10 | `public CopperMining()` |  |

### the ground (lines 95-186)

| line | len | member | says |
|---:|---:|---|---|
| 106 | 21 | `protected double groundLimit(Good g, double asked)` | The ground decides what comes up of concentrate, as ExtractionSector's groundLimit() decides its resource's good: the month's ask split by each kind of dig's share of the nameplate (digs()), each part's field tonnes -... |
| 135 | 18 | `double[] leached(List<Dig> kinds)` | What each kind of its leach plants makes of cathode this month (pure): its nameplate at the sector's rate, held to what its type still has in the ground - kinds on one type sharing it in proportion to the copper they ... |
| 155 | 5 | `public double lift()` | The month's cathode at the sector's rate, as far as the oxide ground holds it (pure): what the market is priced on and the month makes. |
| 163 | 4 | `protected double madeAtRate(Good g)` | Cathode this month: the leach plants' lift, the ground's limit in it (Sector.madeAtRate()); every other good as Sector's. |
| 175 | 11 | `public void endOfMonth(Game game)` | The month's cathode out of the ground, once every good is made: each kind's lift (leached(), the figures the month's cathode was made of) taken out of its type's ground at its dig, so E and W hold by type to the tonne. |

### the plan and the retirement (lines 187-233)

| line | len | member | says |
|---:|---:|---|---|
| 190 | 3 | `static double tonnesOf(BuildingsTemplate t)` | What a building of its makes a month at nameplate, concentrate or cathode, in tonnes - what the plan weighs and the retirement measures. |
| 196 | 3 | `protected boolean lifts(BuildingsTemplate t)` | A building of its digs copper: the mines' concentrate or the leach plant's cathode (copper's pricing good is cathode, which the mines do not make). |
| 202 | 3 | `public double unitsOf(BuildingsTemplate t)` | What one of its buildings contributes to the measures it is retired on: the tonnes it makes a month at nameplate. |
| 212 | 9 | `public double[] retirementDemandAndCapacity(Game game)` | Copper worked out leaves its mines spare, measured in the tonnes they make: all of them once the city's copper is out; while some is left, those over a type worked out (ExtractionSector's workedOut()). |
| 224 | 9 | `double workedOutCapacity(LandManager land)` | Its nameplate standing over a type worked out, in the tonnes each makes (unitsOf()). |

### the page (lines 234-256)

| line | len | member | says |
|---:|---:|---|---|
| 237 | 19 | `public List<Line> ownLines(Game game)` |  |


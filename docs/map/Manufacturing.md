# Manufacturing.java - 440 lines · 22 methods · 4 constants · sectors

`ham/citybuildersim/sectors/Manufacturing.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

> What the city makes out of its own steel, and ships.
> 
> THE NINTH SECTOR (2026-09-13, Jerus's call). The eighth answered the
> plateau with pure labour - a seat, a wage, a client who is not here - and
> it works, and it is bounded by exactly one thing: the unskilled wage. A
> city that succeeds at contact centres bids that wage up until they close.
> That is the boom-and-bleed the sector was calibrated on and it is supposed
> to happen. What the city had no answer for was the NEXT rung - work that
> pays more than a seat and can still be sold to somebody who is not here.
> 
> The old note in claude/jobs-beyond-the-stalemate.md asked for exactly this
> and described it exactly right: "an export sector designed to absorb
> labour, unlike steel which is designed around a deposit. Needs power, land,
> materials, port." Business Services was built first because it needed
> nothing but people. This is the one that needed the goods economy to exist.
> 
> WHAT IT DOES. It buys steel - from the city's mills if there are any, from
> the world if there are not - and sells two things abroad:
> 
>   Fabrication Shop      fabricated steel   135 posts, mostly labour and trades
>   Fabrication Works     fabricated steel   276 posts, the same trade at scale
>   Machine Works         machinery          173 posts, trades and technicians
> 
> (and since 0.7.133, batch I8, a third of copper: the Wire & Cable Works
> draws Copper Refining's rod into wire and cable, 400 posts - THE WIRE &
> CABLE WORKS below).
> 
> TWO GOODS, BOUNDED BY TWO DIFFERENT THINGS, and that is the whole design
> rather than a detail. Fabricated steel is 61% steel and 20% wages, so what
> decides whether a shop pays is THE STEEL PRICE: thin on imported steel at
> the ceiling, an ordinary business on local steel in the middle of the band,
> a good one on steel at its export floor. Machinery is 21% steel and about
> half wages, so what decides it is THE WAGE BILL AND THE CURRENCY, the same
> two that decide a contact centre - but at a diploma machinist's wage rather
> than an agent's, which is why it is the rung above rather than a competitor.
> 
> So a city reads its own position off this sector's screen. If the sector is
> dying and the steel line is the red one, it needs a mill. If it is dying and
> the wage line is, it has priced itself out - and, unlike a contact centre,
> that is not fatal, because the machine works survives a wage that closes the
> centre and the fabrication shop barely notices the wage at all.
> 
> AND IT IS THE FIRST DOMESTIC CUSTOMER STEEL HAS EVER HAD. Until this
> sector, STEEL's entry in Good said "not importable: there is no local buyer
> to import for", and every tonne the mills made left at the export floor for
> want of anybody here to sell it to. A fabricator bids, the strike lifts the
> price off the floor toward the hot-rolled ceiling, and the mills get paid
> more for the same tonne without changing anything they do. Ore, to steel, to
> a beam that leaves: three links, and the middle one is finally worth
> something at home. That is the cluster the ore market was built to reward,
> one link further up than it could reach.
> 
> NOT GATED ON THE GROUND, which is the point of it. Heavy Industry will not
> build a mill without spare local ore, and should not - a mill on scrap alone
> earns almost nothing. A fabricator on imported steel earns thin but real, so
> the sector can open in a city with no deposit at all. Geology decides how
> WELL it does here, not whether it can be done here, and that is the
> difference between a sector that absorbs labour and one designed around a
> deposit.
> 
> ... (1 more lines in the source)

**Uses:** [Good](Good.md) (12), [BusinessInvestment](BusinessInvestment.md) (9), [BuildingsTemplate](BuildingsTemplate.md) (4), [NewUnit](NewUnit.md) (4), [Game](Game.md) (3), [Formats](Formats.md) (3), [SpreadPlanner](SpreadPlanner.md) (3), [Sector](Sector.md) (2), [ProcessingSector](ProcessingSector.md) (2), [OldMetal](OldMetal.md) (1), [BuildingType](BuildingType.md) (1)

**Used by (7):** [BuildingDataCheck](BuildingDataCheck.md), [BuildingManager](BuildingManager.md), [CopperRefiningCheck](CopperRefiningCheck.md), [GdpCheck](GdpCheck.md), [ManufacturingCheck](ManufacturingCheck.md), [MaterialsCheck](MaterialsCheck.md), [Sectors](Sectors.md)

## Sections

| line | section |
|---:|---|
| 187 | · reading |
| 238 | · plan |
| 388 | · the screen |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 82 | `Manufacturing.EXPANSION_RECOVERY_MONTHS` | `12` | Months covering its running costs after the lent DISTRESS_LOSS_MONTHS operating-loss runway before Manufacturing orders more plant: a full year's recovery (MR1). |
| 142 | `Manufacturing.LUBRICANT_LITRES_A_TONNE_OF_MACHINERY` | `10` | Lubricants a tonne of machinery made takes: ten litres (spec-oil 2.5, est.). |
| 145 | `Manufacturing.LUBRICANT_LITRES_A_TONNE_FABRICATED` | `2` | ...and a tonne of fabricated steel: two litres (spec-oil 2.5, est.). |
| 162 | `Manufacturing.WIRE_CABLE_WORKS` | `128` | The Wire & Cable Works' permanent id in buildings.json (0.7.133): 2,000 t of wire and cable a month from 2,000 t of rod. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 85 | `private int expansionRecoveryMonths` | The dial means no recovery hold is active; first entry and ordinary short losses are allowed. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 79 | 362 | **type** `public final class Manufacturing extends Sector` | What the city makes out of its own steel, and ships. |
| 88 | 1 | `public int getExpansionRecoveryMonths()` | Paying months in an armed recovery, or the expansion dial when no recovery hold is active (MR1). |
| 102 | 7 | `protected void afterBank()` | THE MARKET'S OWN RECORD (MR1). |
| 111 | 5 | `private boolean lentLossRunway()` | bank() closes before recordOperatingResult(): the saved streak here is through the previous close. |
| 118 | 3 | `protected void saveExtras(Map<String, Double> extras)` |  |
| 123 | 5 | `protected void restoreExtras(Map<String, Double> extras)` |  |
| 130 | 1 | `protected void resetExtras()` |  |
| 164 | 15 | `public Manufacturing()` |  |
| 182 | 4 | `public void produceFlow(Good g)` | What it makes of a flow good, and since 0.7.123 (batch I4) the offcuts of its fabricated steel to the city's old steel, its own to sell (OldMetal). |

### reading (lines 187-237)

| line | len | member | says |
|---:|---:|---|---|
| 190 | 6 | `public double getPosts()` | Every post the sector holds, staffed or not - the sector in one number. |
| 198 | 3 | `public double getSteelDemand()` | Tonnes of steel the plants want this month, at the rate they are running. |
| 203 | 3 | `public double getSteelPrice()` | What a tonne of steel is costing the plants here - the number the shops live or die on. |
| 212 | 3 | `public double getSteelPosition()` | Where the steel price sits between the mills' export floor and the world's delivered ceiling: 0 is a mill with nobody else to sell to, 1 is a city with no mill at all. |
| 224 | 4 | `public double steelShare()` | The steel bill as a share of what the sector sold. |
| 230 | 4 | `public double payrollShare()` | ...and the wage bill, the same way. |
| 236 | 1 | `public double costShare()` | Both together, which is what has to stay under one. |

### plan (lines 238-387)

| line | len | member | says |
|---:|---:|---|---|
| 278 | 53 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Overridden for the reason Business Services and Heavy Industry override it: the generic planner forecasts from LOCAL demand, and BusinessInvestment says so in its own header - "the world is not demand". |
| 353 | 9 | `public double estimatedMonthlyProfit(NewUnit u, BusinessInvestment plans)` | What one more of its buildings would clear a month (0.7.133, batch I8): the maker's estimate (Sector's) - and for the Wire & Cable Works, the materials chains' row, less what it buys abroad that is not a good, its ins... |
| 364 | 3 | `double fleetCapital(BuildingsTemplate t)` | The vans one of `t` must buy, at what a van costs here (ProcessingSector.fleetCapital()); none with no markets. |
| 369 | 3 | `double fleetCapital(NewUnit t)` | ...for one new building already resolved (QN1): the vans of its own recipe. |
| 382 | 1 | `public double[] retirementDemandAndCapacity(Game game)` | A price taker with no stock: it shrinks on distress, not on a demand reading. |
| 386 | 1 | `public boolean bleedsWhileLent()` | With no demand reading it shrinks on its losses alone, so it sells plant while still lent (AQ1: GW1's seed 2 lent it 96 months of them). |

### the screen (lines 388-440)

| line | len | member | says |
|---:|---:|---|---|
| 391 | 49 | `public List<Sector.Line> ownLines(Game game)` |  |


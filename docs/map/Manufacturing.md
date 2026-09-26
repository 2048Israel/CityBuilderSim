# Manufacturing.java - 279 lines · 11 methods · 0 constants · sectors

`ham/citybuildersim/sectors/Manufacturing.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

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
>   Fabrication Works     fabricated steel   330 posts, the same trade at scale
>   Machine Works         machinery          173 posts, trades and technicians
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
> See claude/manufacturing.md.

**Uses:** [BusinessInvestment](BusinessInvestment.md) (8), [Good](Good.md) (7), [Game](Game.md) (3), [Formats](Formats.md) (3), [Sector](Sector.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (2), [BuildingType](BuildingType.md) (1)

**Used by (2):** [ManufacturingCheck](ManufacturingCheck.md), [Sectors](Sectors.md)

## Sections

| line | section |
|---:|---|
| 86 | · reading |
| 137 | · plan |
| 227 | · the screen |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 72 | 208 | **type** `public final class Manufacturing extends Sector` | What the city makes out of its own steel, and ships. |
| 74 | 11 | `public Manufacturing()` |  |

### reading (lines 86-136)

| line | len | member | says |
|---:|---:|---|---|
| 89 | 6 | `public double getPosts()` | Every post the sector holds, staffed or not - the sector in one number. |
| 97 | 3 | `public double getSteelDemand()` | Tonnes of steel the plants want this month, at the rate they are running. |
| 102 | 3 | `public double getSteelPrice()` | What a tonne of steel is costing the plants here - the number the shops live or die on. |
| 111 | 3 | `public double getSteelPosition()` | Where the steel price sits between the mills' export floor and the world's delivered ceiling: 0 is a mill with nobody else to sell to, 1 is a city with no mill at all. |
| 123 | 4 | `public double steelShare()` | The steel bill as a share of what the sector sold. |
| 129 | 4 | `public double payrollShare()` | ...and the wage bill, the same way. |
| 135 | 1 | `public double costShare()` | Both together, which is what has to stay under one. |

### plan (lines 137-226)

| line | len | member | says |
|---:|---:|---|---|
| 167 | 48 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Overridden for the reason Business Services and Heavy Industry override it: the generic planner forecasts from LOCAL demand, and BusinessInvestment says so in its own header - "the world is not demand". |
| 225 | 1 | `public double[] retirementDemandAndCapacity(Game game)` | A price taker with no stock: it shrinks on distress, not on a demand reading. |

### the screen (lines 227-279)

| line | len | member | says |
|---:|---:|---|---|
| 230 | 49 | `public List<Sector.Line> operations(Game game)` |  |


# BusinessServices.java - 267 lines · 8 methods · 0 constants · sectors

`ham/citybuildersim/sectors/BusinessServices.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> Somebody else's work, done here, paid for from outside.
> 
> WHY (2026-09-12). Jerus, on why the city plateaus: every job-creating sector
> in the game is either a DOMESTIC SERVICE whose demand is the population
> itself - retail, real estate, construction, healthcare, education, and they
> scale WITH people so they cannot lead them - or the ONE TRADABLE CHAIN of
> ore to steel, which geology gates. Once the domestic sectors are saturated
> and the ore is out, nobody in the city can pay for what another worker would
> produce. That is the wall, and it sits at eight hundred to fourteen hundred
> people past their twelve months of EI in every seed.
> 
> There are exactly three ways out: sell something to foreigners, replace an
> import, or have the government employ people. Inventing occupations is none
> of them - a sector only breaks the stalemate if its output can LEAVE THE
> CITY. This is the first one that does it with labour instead of a deposit.
> 
> THREE RUNGS, one per rung of the education ladder, so that building a
> university finally has a payoff other than staffing a hospital:
> 
>   Contact Centre                support work       mostly unskilled
>   Shared Services Centre        back-office work   diploma and college
>   Engineering Services Office   engineering work   licensed engineers, GATED
> 
> THE MECHANISM IS ALREADY BUILT, which is the best thing about it. An
> export-only BAND good clears at its FLOOR - there is no local demand to lift
> it off the bottom - so revenue per seat is fixed by the world and the wage
> bill is the entire constraint. Which makes the sector self-limiting with no
> new rule: it hires, the labour market walks that band's wage up on
> clamp(tightness^0.5, .70, 4.0), unit cost rises, the expansion test
> (profit >= interest * 1.25) fails, and twenty-four months of losses sheds
> plant. BusinessInvestment.runningCostOf() already prices a prospective
> building's payroll at TODAY's wages, so the brake needs no wiring at all.
> 
> And because the world's price arrives THROUGH THE EXCHANGE RATE, a weak
> currency wins the city contracts and a strong one kills the sector. That is
> offshoring, and it is the model working rather than a special case. Rising
> wages also draw migrants, who add supply, which relieves tightness - so the
> equilibrium is a BIGGER CITY, which is the entire point of the batch.
> 
> NO CEILING ON THE WORLD'S APPETITE, per Jerus. The research says the raw wage
> gap that drives relocation is ten to one but the realised saving is only
> 45-55%, because telecom, management and attrition eat the rest - so work
> actually moves at about a 2x loaded-cost ratio, not a wage ratio. The game
> does not need that separately: the world price already embeds what a client
> will pay, so local loaded cost crossing it IS the threshold, expressed as a
> price. What would falsify the design is a seed where this sector employs a
> third of the workforce and the wage never moves.
> 
> Calibrated on Nova Scotia's own call-centre decade, which is also the best
> evidence the shape is right: 3,700 jobs across 13 centres in 1999, 15,693
> across 53 by 2007 - about 296 seats a centre and 3.5% of provincial
> employment - landed with roughly $8,800 a job of payroll rebates, paying
> $8.50-10.50 an hour where one steelworker went from $16 to $8.75, and then
> shedding two thousand jobs to the Philippines and India inside a decade.
> Boom and bleed, documented. See claude/business-services.md.

**Uses:** [Good](Good.md) (12), [BusinessInvestment](BusinessInvestment.md) (9), [Game](Game.md) (3), [JobType](JobType.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [Formats](Formats.md) (2), [Sector](Sector.md) (1), [BuildingType](BuildingType.md) (1)

**Used by (3):** [BusinessServicesCheck](BusinessServicesCheck.md), [LongPlaytest](LongPlaytest.md), [Sectors](Sectors.md)

## Sections

| line | section |
|---:|---|
| 119 | · plan |
| 226 | · the screen |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 71 | 197 | **type** `public final class BusinessServices extends Sector` | Somebody else's work, done here, paid for from outside. |
| 73 | 11 | `public BusinessServices()` |  |
| 86 | 5 | `public double getSeats()` | Seats of every kind, staffed or not - the sector in one number. |
| 99 | 3 | `public double priceOfSeat(Good g)` | What a seat-month of each kind fetches in the city's own money. |
| 110 | 8 | `public double payrollShare()` | Payroll as a share of revenue, at the wages the city is paying now. |

### plan (lines 119-225)

| line | len | member | says |
|---:|---:|---|---|
| 135 | 67 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Overridden for the same reason Mining and Heavy Industry override it: the generic planner forecasts from LOCAL demand, and BusinessInvestment says so in its own header - "the world is not demand". |
| 209 | 1 | `public double[] retirementDemandAndCapacity(Game game)` | A price taker with no stock: it shrinks on distress, not on a demand reading. |
| 215 | 10 | `private static String licenceLabel(JobType job)` | A licence, in words, without reaching into the UI for it - a sector must not depend on the screen that draws it. |

### the screen (lines 226-267)

| line | len | member | says |
|---:|---:|---|---|
| 229 | 38 | `public List<Line> operations(Game game)` |  |


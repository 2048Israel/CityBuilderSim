# Automotive.java - 294 lines · 7 methods · 2 constants · sectors

`ham/citybuildersim/sectors/Automotive.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> The automobile industry. THE THIRTEENTH SECTOR (2026-09-16, Jerus's call).
> 
> Jerus: "we are going to also add the autombile industry, where customers can
> buy cars and business can buy vans/trucks, and rail sector can buy trains."
> 
> =======================================================================
> WHAT IT IS FOR, AND IT IS NOT THE CARS
> =======================================================================
> 
> Until today this game's manufacturing chain stopped one link short of
> anything a person could own. Ore becomes steel, steel becomes fabricated
> steel and machinery, and then BOTH LEAVE - see Good's header on those two:
> "nothing in the city buys a beam or a machine". A city could build the whole
> industrial chain and still have nothing at the end of it but a dock.
> 
> An assembly plant is the first thing that buys them. That is the point of
> this sector: it gives Manufacturing a customer that is not the world, and it
> gives the city a FOURTH link - ore, steel, fabrication, assembly - which is
> the longest chain in the game and the one that pays the most people.
> 
> AND THE HEAVY HALF OF THE CHAIN CANNOT BE SHORTCUT. Fabricated steel is not
> importable, so a city with money and no fabrication shop cannot be in the
> car business however much it wants to be - it can only buy cars, like
> anybody else. plan() below refuses to sink a plant the city cannot supply,
> for exactly the reason HeavyIndustry refuses to sink a mill with no ore
> behind it: a sector that expands on a business case it does not have is the
> borrowing spiral the investment engine was written to end.
> 
> MACHINERY CAN COME OFF A SHIP, and that asymmetry is deliberate and was
> forced by a measurement. A car is three and a half tonnes of fabricated
> steel and four hundred kilos of machinery: the bulky input is the one a city
> has to be able to make, and the specialised one is the one every
> industrialising country in history has imported. Gating on both was the
> first draft, and the four-thousand-month playtest showed why it could not
> stand - 179 Fabrication Works and ZERO Machine Works, because Manufacturing
> scores its two products against each other every month and fabrication wins
> every month. An industry gated on a building the model never builds is a
> dead branch, not a design. Machinery got an import ceiling instead, which is
> what Good's own header had said would happen the day anything here bought
> one. A city that DOES build a machine works still wins: it buys its
> machinery at the local floor instead of the world's landed price.
> 
> =======================================================================
> THREE THINGS, THREE CUSTOMERS
> =======================================================================
> 
> CARS go to households, VANS to businesses and ROLLING STOCK to the railway -
> and in this batch NONE of those customers exists yet. All three clear at the
> export floor, like fabricated steel and machinery before them, which makes
> this an export industry on the day it opens and is what Jerus asked for when
> he picked "a real export industry": building cars is a strategy in its own
> right and not just a way to supply your own people.
> 
> The domestic side comes next, and it is the larger half: a household that
> owns a car puts far more on the road than one that does not, a business
> short of vans cannot move what it makes, and a railway with track and no
> locomotives carries nothing. See claude/the-railway.md for where that goes.

**Uses:** [Good](Good.md) (19), [BusinessInvestment](BusinessInvestment.md) (10), [Game](Game.md) (5), [Formats](Formats.md) (4), [Sector](Sector.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [BuildingType](BuildingType.md) (1)

**Used by (4):** [BuildingDataCheck](BuildingDataCheck.md), [BuildingManager](BuildingManager.md), [CarCheck](CarCheck.md), [Sectors](Sectors.md)

## Sections

| line | section |
|---:|---|
| 136 | PLANNING - can the city supply it, and then is it worth it |
| 247 | · the screen |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 109 | `Automotive.MAX_SHARE_OF_LOCAL_SUPPLY` | `.25` | The most of the city's WHOLE fabrication output one new plant may want. |
| 119 | `Automotive.LUBRICANT_LITRES_A_VEHICLE` | `8` | Lubricants a vehicle built takes (0.7.83, batch O6; runs/spec-oil.md 2.5, est., to confirm): eight litres - its first fill of engine, gearbox and axle oils and the line's own - on the car plants' and (star O6) the van... |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 73 | 222 | **type** `public final class Automotive extends Sector` | The automobile industry. |
| 121 | 14 | `public Automotive()` |  |

### PLANNING - can the city supply it, and then is it worth it (lines 136-246)

| line | len | member | says |
|---:|---:|---|---|
| 141 | 3 | `public double localSupplyOf(Good g, Game game)` | What the city can fabricate a month, whoever is currently buying it. |
| 146 | 3 | `public double biggestDrawAllowed(Good g, Game game)` | The largest draw a new plant may have on that supply. |
| 159 | 71 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Manufacturing's shape - the best template by profit over cost, floored on staffing - with HeavyIndustry's supply gate in front of it. |
| 245 | 1 | `public double[] retirementDemandAndCapacity(Game game)` | A price taker with nobody at home to sell to - YET. |

### the screen (lines 247-294)

| line | len | member | says |
|---:|---:|---|---|
| 250 | 1 | `public boolean hasPlantBlock()` |  |
| 253 | 41 | `public List<Sector.Line> ownLines(Game game)` |  |


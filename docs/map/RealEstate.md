# RealEstate.java - 800 lines · 84 methods · 9 constants · sectors

`ham/citybuildersim/sectors/RealEstate.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

> The landlords. Own every home in the city and let them by the month.
> 
> THE LARGEST SECTOR CLASS, because rent is the most-argued price in the
> game and none of the argument belongs in the template. What it overrides:
> 
>   the price     two rents, per person of dwelling capacity - family doors
>                 and studios - each walking a twelfth of the way a month
>                 toward a target struck from what a new home costs, how
>                 tight its segment is, and what the standing stock costs to
>                 hold. See TWO RENTS and RENT AS A MARKET below, kept from
>                 the old CommercialHandler because the reasoning is the
>                 mechanic.
>   the sale      HOUSING to the households, billed off the actual match -
>                 FamilyModel puts households behind doors that fit them and
>                 hands back the weight each segment bills on.
>   planning      off jobs, not population: population is capped by housing,
>                 so a landlord that watched population would conclude
>                 demand had stopped exactly when it was the one causing
>                 the shortage. And a shortage of family doors is its own
>                 reason to build, which more studios cannot answer.
>   shrinking     an occupied home is never scrapped out from under anyone,
>                 and a segment with no doors to spare sheds nothing.
> 
> Everything else - the books, the credit, the listing, the property tax on
> the whole stock, the repair order it places with the builders - is the
> template's.
> 
> ======================================================================
> RENT
> 
> WHAT IT USED TO BE: $350 a month PER RESIDENT. Babies paid rent. Measured
> on a city of 1,218, rent came to 106% of the entire wage bill and a large
> family was bankrupt before it bought food. WHAT IT IS NOW: one household,
> one rent, scaled by how big the home is - charged per person of DWELLING
> CAPACITY, not per resident, because a flat is what a landlord charges for.
> 
> RENT AS A MARKET. Jerus: "real estates margin, plus homes vs households
> both ways, with lag", and rent should be free to leave affordability
> entirely rather than being capped the way the shelf price is. A cost
> floor, a scarcity multiple, and a lag:
> 
>   THE FLOOR is what it costs to supply one more person of capacity - the
>   cheapest residential building the city could put up, at today's
>   materials and today's land, over the return a landlord wants. Expensive
>   land and expensive concrete reach households through their rent.
>   THE MULTIPLE is households against front doors, both ways and unbounded.
>   Short of doors, rent rises without limit; overbuild and it falls below
>   cost and the landlords eat it, which is what makes overbuilding a real
>   mistake rather than a free one.
>   THE LAG is a twelve-month lease: a twelfth of tenancies come up for
>   renewal each month, so a twelfth of the gap closes each month.
> 
> Unbounded is safe because dear rent empties the city - through the
> household balance sheet, which discharges households that cannot meet
> their fixed costs and sends a share of them away. The runaway has a brake
> with people in it.
> 
> TWO RENTS, BECAUSE THERE ARE TWO DECISIONS. Jerus: "theyll rent at zero
> profit, but they wont build more if new rent is zero profit." THE FLOOR -
> rentBreakEven() - is what the EXISTING stock costs to hold this month, per
> ... (12 more lines in the source)

**Uses:** [BusinessInvestment](BusinessInvestment.md) (11), [BuildingsTemplate](BuildingsTemplate.md) (8), [FamilyModel](FamilyModel.md) (8), [Game](Game.md) (5), [PayTier](PayTier.md) (3), [Trade](Trade.md) (3), [Good](Good.md) (2), [Formats](Formats.md) (2), [Sector](Sector.md) (1), [BuildingType](BuildingType.md) (1), [Markets](Markets.md) (1), [GoodsMarket](GoodsMarket.md) (1)

**Used by (12):** [BusinessInvestment](BusinessInvestment.md), [EconomyManager](EconomyManager.md), [Game](Game.md), [HouseholdCheck](HouseholdCheck.md), [HousingCheck](HousingCheck.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [Migration](Migration.md), [NewGameCheck](NewGameCheck.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [Sectors](Sectors.md)

## Sections

| line | section |
|---:|---|
| 132 | · the prices |
| 143 | · the doors |
| 187 | INPUTS FROM THE CITY, set each month by EconomyManager and Game |
| 241 | · readers |
| 285 | THE PRICE |
| 423 | THE SALE, at the bottom of the month |
| 457 | PLANNING - off jobs, and off the segment that is short |
| 669 | THE SCREEN |
| 737 | SAVE, RESET, THE REFORM |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 96 | `RealEstate.TARGET_RENT_BURDEN` | `.30` | What share of a working household's income rent should take. |
| 99 | `RealEstate.REFERENCE_EARNERS` | `2` | The household the yardstick is struck against: a couple both working... |
| 102 | `RealEstate.REFERENCE_HOME_CAPACITY` | `4` | ...in a home for four. |
| 110 | `RealEstate.LANDLORD_YIELD` | `.058` | What a landlord wants back each year for what the building cost. |
| 118 | `RealEstate.MAINTENANCE_PER_YEAR` | `.01` | Repairs: one percent a year of what a building cost to put up, placed as a real order with the builders in the building's own inputs. |
| 121 | `RealEstate.SCARCITY_ELASTICITY` | `1.0` | How hard rent answers a shortage of front doors. |
| 124 | `RealEstate.LEASE_MONTHS` | `12` | The lease, in months, which is also the lag. |
| 127 | `RealEstate.BUILD_MARGIN` | `.33` | The margin a new building has to clear over its own costs to be worth putting up. |
| 130 | `RealEstate.MIN_HOMES_FOR_A_MARKET` | `10` | Below this many front doors the ratio stops meaning anything. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 135 | `private double rentPrice` | Rent per person of dwelling capacity, family doors. |
| 138 | `private double studioRentPrice` | The studio price, per person of capacity. |
| 140 | `private double lastRentTarget` |  |
| 141 | `private double lastStudioTarget` |  |
| 145 | `private int homes` |  |
| 146 | `private double occupiedHomes` |  |
| 148 | `private int household` | Household CAPACITY - the people the residential buildings hold. |
| 149 | `private int population` |  |
| 150 | `private double householdCount` |  |
| 151 | `private double marginalHousingCost` |  |
| 154 | `private double structurePerCapacity` | What supplying one more person of capacity costs, land included - the cheapest home. |
| 155 | `private double landPerCapacity` |  |
| 157 | `private double studioCostPerCapacity` | ...and per segment, off the cheapest building that segment can supply. |
| 158 | `private double familyCostPerCapacity` |  |
| 161 | `private double ownedCapacity` | People the residential buildings hold, sites included - the break-even's denominator. |
| 163 | `private int studioHomes, familyHomes` |  |
| 164 | `private double studioSeekers, familySeekers` |  |
| 165 | `private double studioSeekerHeads, familySeekerHeads` |  |
| 168 | `private double studioRentWeight, familyRentWeight` | What the households behind the doors add up to, per segment. |
| 170 | `private double rBilledStudioWeight, rBilledFamilyWeight` | The weights the month's rent was actually billed on. |
| 172 | `private double rRentIncome` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 93 | 708 | **type** `public final class RealEstate extends Sector` | The landlords. |

### the prices (lines 132-142)

### the doors (lines 143-186)

| line | len | member | says |
|---:|---:|---|---|
| 174 | 7 | `public RealEstate()` |  |
| 183 | 3 | `public static double rentFor(double unskilledWage)` | Rent per person of capacity at a given unskilled wage: the founding value and the yardstick. |

### INPUTS FROM THE CITY, set each month by EconomyManager and Game (lines 187-240)

| line | len | member | says |
|---:|---:|---|---|
| 191 | 1 | `public void setHomes(int homes)` |  |
| 199 | 4 | `public void setOccupiedHomes(double occupied)` | How many doors have somebody behind them. |
| 204 | 1 | `public void setHousehold(int capacity)` |  |
| 205 | 1 | `public void setPopulation(int population)` |  |
| 206 | 1 | `public void setHouseholdCount(double count)` |  |
| 207 | 1 | `public void setMarginalHousingCost(double perCapacity)` |  |
| 209 | 4 | `public void setHousingCosts(double structurePerCapacity, double landPerCapacity)` |  |
| 214 | 4 | `public void setSegmentHousingCosts(double studioPerCapacity, double familyPerCapacity)` |  |
| 219 | 1 | `public void setOwnedHousingCapacity(double capacity)` |  |
| 221 | 10 | `public void setSegments(int studioHomes, int familyHomes, double studioSeekers, double familySeekers, double studioSeekerHeads,...` |  |
| 233 | 4 | `public void setRentWeight(double studio, double family)` | Both halves, from FamilyModel.house(). |
| 239 | 1 | `public void setRentWeight(double weight)` | The old single-weight setter: everything in the family half. |

### readers (lines 241-284)

| line | len | member | says |
|---:|---:|---|---|
| 243 | 1 | `public int getHomes()` |  |
| 244 | 1 | `public double getOccupiedHomes()` |  |
| 245 | 1 | `public int getHousehold()` |  |
| 246 | 1 | `public double getHouseholdCount()` |  |
| 247 | 1 | `public double getMarginalHousingCost()` |  |
| 248 | 1 | `public double getStructurePerCapacity()` |  |
| 249 | 1 | `public double getLandPerCapacity()` |  |
| 250 | 1 | `public double getStudioCostPerCapacity()` |  |
| 251 | 1 | `public double getFamilyCostPerCapacity()` |  |
| 252 | 1 | `public double getOwnedHousingCapacity()` |  |
| 253 | 1 | `public int getStudioHomes()` |  |
| 254 | 1 | `public int getFamilyHomes()` |  |
| 255 | 1 | `public double getStudioSeekers()` |  |
| 256 | 1 | `public double getFamilySeekers()` |  |
| 257 | 1 | `public double getStudioSeekerHeads()` |  |
| 258 | 1 | `public double getFamilySeekerHeads()` |  |
| 259 | 1 | `public double getRentWeight()` |  |
| 260 | 1 | `public double getStudioRentWeight()` |  |
| 261 | 1 | `public double getFamilyRentWeight()` |  |
| 262 | 1 | `public double getBilledRentWeight()` |  |
| 263 | 1 | `public double getBilledStudioWeight()` |  |
| 264 | 1 | `public double getBilledFamilyWeight()` |  |
| 265 | 1 | `public double getRentPrice()` |  |
| 266 | 1 | `public double getStudioRentPrice()` |  |
| 267 | 1 | `public double getRentTarget()` |  |
| 268 | 1 | `public double getStudioRentTarget()` |  |
| 270 | 1 | `public double getRentIncomeBilled()` | Rent billed in the month last sold - the figure the households were charged. |
| 272 | 1 | `public void setRentPrice(double price)` |  |
| 273 | 1 | `public void setStudioRentPrice(double price)` |  |
| 276 | 1 | `public double averageHomeSize()` | Capacity per front door, averaged over whatever the city has built. |
| 279 | 5 | `public double averageHouseholdSize(boolean family)` | Average household size in one segment. |

### THE PRICE (lines 285-422)

| line | len | member | says |
|---:|---:|---|---|
| 298 | 5 | `public double rentBreakEven()` | What the standing stock costs to hold this month, per person of capacity - this month's actual maintenance, property tax and interest over the capacity they are spread across. |
| 305 | 5 | `public double rentRequired()` | What a landlord WANTS on a new building, before scarcity: the yield on what the cheapest home costs. |
| 312 | 5 | `public double rentRequired(boolean family)` | The same required return, on the cheapest home THIS segment can supply. |
| 319 | 4 | `public double rentFloor()` | The floor under rent: the measured break-even, or the required return while there is nothing to measure. |
| 325 | 4 | `public double housingPressure()` | Households per front door: above one is a shortage, below one a glut. |
| 330 | 4 | `private double segmentPressure(int doors, double seekers)` |  |
| 335 | 1 | `public double studioPressure()` |  |
| 336 | 1 | `public double familyPressure()` |  |
| 338 | 1 | `public double rentScarcityMultiple()` |  |
| 340 | 4 | `public double scarcityMultipleOf(double pressure)` |  |
| 345 | 1 | `public double rentTarget()` |  |
| 346 | 1 | `public double studioRentTarget()` |  |
| 356 | 6 | `private double targetFor(double pressure, boolean family)` | One segment's target: what its own next building costs, times how tight that segment is, lifted so the COMPANY still covers its carry. |
| 368 | 11 | `private double carryLift()` | How far short of the carry the two legs come, struck on their own costs, weighted by what is actually billed. |
| 381 | 14 | `public void repriceRent()` | Moves each rent a lease-length closer to what its market says it should be. |
| 397 | 5 | `public double getAverageRentPaid()` | What one person of capacity actually cost on average this month - the price index's figure. |
| 404 | 6 | `public double blendedRentTarget()` | Where that average is heading - the invariant HousingCheck asserts the break-even against. |
| 416 | 6 | `public double getRentIncome()` | The month's rent, billed off the actual match: each segment's weight at its own price. |

### THE SALE, at the bottom of the month (lines 423-456)

| line | len | member | says |
|---:|---:|---|---|
| 428 | 15 | `public void sellOwnPriced(Markets markets, Game game)` |  |
| 449 | 3 | `public void endOfMonth(Game game)` | Rent walks here, and only here: moving a lagged price one step toward its target IS a month passing, and the load path must not do it. |
| 455 | 1 | `public double getInventoryValue()` | The landlords hold no stock: a home is not a unit in a warehouse. |

### PLANNING - off jobs, and off the segment that is short (lines 457-668)

| line | len | member | says |
|---:|---:|---|---|
| 462 | 82 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` |  |
| 546 | 3 | `public double doorShortfall(boolean family)` | Households in one segment with no door of their own, or fewer than none. |
| 551 | 4 | `private boolean hasDoorsToSpare(BuildingsTemplate t)` | Whether the segment this template belongs to has spare doors. |
| 557 | 6 | `private double latentHeadShortfall()` | The head shortage plan() would see if it were asked right now - for the credit check. |
| 571 | 7 | `private double fillableDoors(BuildingsTemplate t, double headShortfall)` | How many of this template's doors the city would actually put somebody in: a studio only a studio-seeker; a family unit a family first and then a studio-seeker the studios have no room for. |
| 585 | 8 | `private double doorsNeeded(boolean family, double headShortfall)` | Doors one segment is short: households here with nowhere, plus the ones the job market is about to bring, at the segment's own household size. |
| 595 | 3 | `public double priceForSegment(BuildingsTemplate t)` | The price the segment this template belongs to is charging. |
| 605 | 8 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | What the city's households would pay for it, not what it would collect if it were full. |
| 616 | 3 | `public double[] retirementDemandAndCapacity(Game game)` | Housing: demand is people actually living in it. |
| 651 | 7 | `public boolean mayRetire(BuildingsTemplate t)` | A residential holding is only sheddable if ITS OWN segment has doors to spare - AND NOBODY IN THE CITY IS SHARING A DOOR THEY DID NOT CHOOSE. |
| 660 | 4 | `public String noRetirementReason(boolean distress)` |  |
| 667 | 1 | `public double unitsOf(BuildingsTemplate t)` | People, not doors: the measure the spare-capacity rule counts in. |

### THE SCREEN (lines 669-736)

| line | len | member | says |
|---:|---:|---|---|
| 674 | 1 | `public String inputLabel()` |  |
| 677 | 59 | `public List<Line> operations(Game game)` |  |

### SAVE, RESET, THE REFORM (lines 737-800)

| line | len | member | says |
|---:|---:|---|---|
| 742 | 13 | `protected void saveExtras(Map<String, Double> extras)` |  |
| 757 | 13 | `protected void restoreExtras(Map<String, Double> extras)` |  |
| 772 | 13 | `protected void resetExtras()` |  |
| 788 | 12 | `protected void redenominateExtras(double scale)` | The prices and the per-capacity costs are money; the doors, the weights and the rates are not. |


# RealEstate.java - 950 lines · 91 methods · 9 constants · sectors

`ham/citybuildersim/sectors/RealEstate.java` - generated 2026-10-24 by CodeMap; line numbers are as of that run.

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

**Uses:** [BusinessInvestment](BusinessInvestment.md) (13), [BuildingsTemplate](BuildingsTemplate.md) (12), [FamilyModel](FamilyModel.md) (10), [Game](Game.md) (5), [PayTier](PayTier.md) (3), [Trade](Trade.md) (3), [Good](Good.md) (2), [Retail](Retail.md) (2), [SectorStatements](SectorStatements.md) (2), [Formats](Formats.md) (2), [Sector](Sector.md) (1), [BuildingType](BuildingType.md) (1), [Markets](Markets.md) (1), [GoodsMarket](GoodsMarket.md) (1), [BuildingsStacks](BuildingsStacks.md) (1)

**Used by (18):** [BuildingsTemplate](BuildingsTemplate.md), [BusinessInvestment](BusinessInvestment.md), [EconomyManager](EconomyManager.md), [Game](Game.md), [HouseholdCheck](HouseholdCheck.md), [HousingCheck](HousingCheck.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [Migration](Migration.md), [MortgageCheck](MortgageCheck.md), [NewGameCheck](NewGameCheck.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [RoadCheck](RoadCheck.md), [SectorFlow](SectorFlow.md), [SectorScreen](SectorScreen.md), [Sectors](Sectors.md)

## Sections

| line | section |
|---:|---|
| 133 | · the prices |
| 144 | · the doors |
| 188 | INPUTS FROM THE CITY, set each month by EconomyManager and Game |
| 242 | · readers |
| 286 | THE PRICE |
| 446 | THE SALE, at the bottom of the month |
| 480 | PLANNING - off jobs, and off the segment that is short |
| 812 | THE SCREEN |
| 887 | SAVE, RESET, THE REFORM |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 96 | `RealEstate.TARGET_RENT_BURDEN` | `.30` | What share of a working household's income rent should take. |
| 99 | `RealEstate.REFERENCE_EARNERS` | `2` | The household the yardstick is struck against: a couple both working... |
| 102 | `RealEstate.REFERENCE_HOME_CAPACITY` | `4` | ...in a home for four. |
| 110 | `RealEstate.LANDLORD_YIELD` | `.058` | What a landlord wants back each year for what the building cost. |
| 119 | `RealEstate.MAINTENANCE_PER_YEAR` | `.01` | Repairs: one percent a year of what a building cost to put up, placed as a real order with the builders in the building's own inputs. |
| 122 | `RealEstate.SCARCITY_ELASTICITY` | `1.0` | How hard rent answers a shortage of front doors. |
| 125 | `RealEstate.LEASE_MONTHS` | `12` | The lease, in months, which is also the lag. |
| 128 | `RealEstate.BUILD_MARGIN` | `.33` | The margin a new building has to clear over its own costs to be worth putting up. |
| 131 | `RealEstate.MIN_HOMES_FOR_A_MARKET` | `10` | Below this many front doors the ratio stops meaning anything. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 136 | `private double rentPrice` | Rent per person of dwelling capacity, family doors. |
| 139 | `private double studioRentPrice` | The studio price, per person of capacity. |
| 141 | `private double lastRentTarget` |  |
| 142 | `private double lastStudioTarget` |  |
| 146 | `private long homes` |  |
| 147 | `private double occupiedHomes` |  |
| 149 | `private long household` | Household CAPACITY - the people the residential buildings hold. |
| 150 | `private long population` |  |
| 151 | `private double householdCount` |  |
| 152 | `private double marginalHousingCost` |  |
| 155 | `private double structurePerCapacity` | What supplying one more person of capacity costs, land included - the cheapest home. |
| 156 | `private double landPerCapacity` |  |
| 158 | `private double studioCostPerCapacity` | ...and per segment, off the cheapest building that segment can supply. |
| 159 | `private double familyCostPerCapacity` |  |
| 162 | `private double ownedCapacity` | People the residential buildings hold, sites included - the break-even's denominator. |
| 164 | `private long studioHomes, familyHomes` |  |
| 165 | `private double studioSeekers, familySeekers` |  |
| 166 | `private double studioSeekerHeads, familySeekerHeads` |  |
| 169 | `private double studioRentWeight, familyRentWeight` | What the households behind the doors add up to, per segment. |
| 171 | `private double rBilledStudioWeight, rBilledFamilyWeight` | The weights the month's rent was actually billed on. |
| 173 | `private double rRentIncome` |  |
| 388 | `private double expectedMonthly` | Expected inflation a month, which each rent drifts at between leases (0.7.43): told by Game before the month's sale; never saved. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 93 | 858 | **type** `public final class RealEstate extends Sector` | The landlords. |

### the prices (lines 133-143)

### the doors (lines 144-187)

| line | len | member | says |
|---:|---:|---|---|
| 175 | 7 | `public RealEstate()` |  |
| 184 | 3 | `public static double rentFor(double unskilledWage)` | Rent per person of capacity at a given unskilled wage: the founding value and the yardstick. |

### INPUTS FROM THE CITY, set each month by EconomyManager and Game (lines 188-241)

| line | len | member | says |
|---:|---:|---|---|
| 192 | 1 | `public void setHomes(long homes)` |  |
| 200 | 4 | `public void setOccupiedHomes(double occupied)` | How many doors have somebody behind them. |
| 205 | 1 | `public void setHousehold(long capacity)` |  |
| 206 | 1 | `public void setPopulation(long population)` |  |
| 207 | 1 | `public void setHouseholdCount(double count)` |  |
| 208 | 1 | `public void setMarginalHousingCost(double perCapacity)` |  |
| 210 | 4 | `public void setHousingCosts(double structurePerCapacity, double landPerCapacity)` |  |
| 215 | 4 | `public void setSegmentHousingCosts(double studioPerCapacity, double familyPerCapacity)` |  |
| 220 | 1 | `public void setOwnedHousingCapacity(double capacity)` |  |
| 222 | 10 | `public void setSegments(long studioHomes, long familyHomes, double studioSeekers, double familySeekers, double studioSeekerHead...` |  |
| 234 | 4 | `public void setRentWeight(double studio, double family)` | Both halves, from FamilyModel.house(). |
| 240 | 1 | `public void setRentWeight(double weight)` | The old single-weight setter: everything in the family half. |

### readers (lines 242-285)

| line | len | member | says |
|---:|---:|---|---|
| 244 | 1 | `public long getHomes()` |  |
| 245 | 1 | `public double getOccupiedHomes()` |  |
| 246 | 1 | `public long getHousehold()` |  |
| 247 | 1 | `public double getHouseholdCount()` |  |
| 248 | 1 | `public double getMarginalHousingCost()` |  |
| 249 | 1 | `public double getStructurePerCapacity()` |  |
| 250 | 1 | `public double getLandPerCapacity()` |  |
| 251 | 1 | `public double getStudioCostPerCapacity()` |  |
| 252 | 1 | `public double getFamilyCostPerCapacity()` |  |
| 253 | 1 | `public double getOwnedHousingCapacity()` |  |
| 254 | 1 | `public long getStudioHomes()` |  |
| 255 | 1 | `public long getFamilyHomes()` |  |
| 256 | 1 | `public double getStudioSeekers()` |  |
| 257 | 1 | `public double getFamilySeekers()` |  |
| 258 | 1 | `public double getStudioSeekerHeads()` |  |
| 259 | 1 | `public double getFamilySeekerHeads()` |  |
| 260 | 1 | `public double getRentWeight()` |  |
| 261 | 1 | `public double getStudioRentWeight()` |  |
| 262 | 1 | `public double getFamilyRentWeight()` |  |
| 263 | 1 | `public double getBilledRentWeight()` |  |
| 264 | 1 | `public double getBilledStudioWeight()` |  |
| 265 | 1 | `public double getBilledFamilyWeight()` |  |
| 266 | 1 | `public double getRentPrice()` |  |
| 267 | 1 | `public double getStudioRentPrice()` |  |
| 268 | 1 | `public double getRentTarget()` |  |
| 269 | 1 | `public double getStudioRentTarget()` |  |
| 271 | 1 | `public double getRentIncomeBilled()` | Rent billed in the month last sold - the figure the households were charged. |
| 273 | 1 | `public void setRentPrice(double price)` |  |
| 274 | 1 | `public void setStudioRentPrice(double price)` |  |
| 277 | 1 | `public double averageHomeSize()` | Capacity per front door, averaged over whatever the city has built. |
| 280 | 5 | `public double averageHouseholdSize(boolean family)` | Average household size in one segment. |

### THE PRICE (lines 286-445)

| line | len | member | says |
|---:|---:|---|---|
| 305 | 5 | `public double rentBreakEven()` | What the standing stock costs to hold this month, per person of capacity - this month's actual maintenance, property tax and interest over the capacity they are spread across. |
| 312 | 5 | `public double rentRequired()` | What a landlord WANTS on a new building, before scarcity: the yield on what the cheapest home costs. |
| 319 | 5 | `public double rentRequired(boolean family)` | The same required return, on the cheapest home THIS segment can supply. |
| 326 | 4 | `public double rentFloor()` | The floor under rent: the measured break-even, or the required return while there is nothing to measure. |
| 332 | 4 | `public double housingPressure()` | Households per front door: above one is a shortage, below one a glut. |
| 337 | 4 | `private double segmentPressure(long doors, double seekers)` |  |
| 342 | 1 | `public double studioPressure()` |  |
| 343 | 1 | `public double familyPressure()` |  |
| 345 | 1 | `public double rentScarcityMultiple()` |  |
| 347 | 4 | `public double scarcityMultipleOf(double pressure)` |  |
| 352 | 1 | `public double rentTarget()` |  |
| 353 | 1 | `public double studioRentTarget()` |  |
| 363 | 6 | `private double targetFor(double pressure, boolean family)` | One segment's target: what its own next building costs, times how tight that segment is, lifted so the COMPANY still covers its carry. |
| 375 | 11 | `private double carryLift()` | How far short of the carry the two legs come, struck on their own costs, weighted by what is actually billed. |
| 391 | 3 | `public void setExpectedMonthly(double monthly)` | Told by Game each month (Expectations.monthlyExpected()); anything not finite is ignored. |
| 404 | 14 | `public void repriceRent()` | Moves each rent a lease-length closer to what its market says it should be - in logs since 0.7.43, and drifting with expected inflation as it goes: the one form every seller in the city prices by (Retail.stickyPrice()... |
| 420 | 5 | `public double getAverageRentPaid()` | What one person of capacity actually cost on average this month - the price index's figure. |
| 427 | 6 | `public double blendedRentTarget()` | Where that average is heading - the invariant HousingCheck asserts the break-even against. |
| 439 | 6 | `public double getRentIncome()` | The month's rent, billed off the actual match: each segment's weight at its own price. |

### THE SALE, at the bottom of the month (lines 446-479)

| line | len | member | says |
|---:|---:|---|---|
| 451 | 15 | `public void sellOwnPriced(Markets markets, Game game)` |  |
| 472 | 3 | `public void endOfMonth(Game game)` | Rent walks here, and only here: moving a lagged price one step toward its target IS a month passing, and the load path must not do it. |
| 478 | 1 | `public double getInventoryValue()` | The landlords hold no stock: a home is not a unit in a warehouse. |

### PLANNING - off jobs, and off the segment that is short (lines 480-811)

| line | len | member | says |
|---:|---:|---|---|
| 485 | 163 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` |  |
| 650 | 3 | `public double doorShortfall(boolean family)` | Households in one segment with no door of their own, or fewer than none. |
| 660 | 3 | `private double doorsWanted(boolean family)` | The same shortfall as a planner reads it: less the doors of that segment already on the landlords' sites (0.7.17) - see plan(), "AND SO DO THE HOMES ON SITE". |
| 665 | 12 | `private double doorsOnSite(boolean family)` | Doors of one segment on the landlords' sites. |
| 679 | 3 | `private double capacityOnSite()` | People of capacity on the landlords' sites. |
| 687 | 4 | `public double monthsOfWorkOnSite()` | Months of the builders' site output the landlords' sites owe (0.7.17), for the investor page. |
| 693 | 4 | `private boolean hasDoorsToSpare(BuildingsTemplate t)` | Whether the segment this template belongs to has spare doors. |
| 699 | 7 | `private double latentHeadShortfall()` | The head shortage plan() would see if it were asked right now - for the credit check. |
| 714 | 7 | `private double fillableDoors(BuildingsTemplate t, double headShortfall)` | How many of this template's doors the city would actually put somebody in: a studio only a studio-seeker; a family unit a family first and then a studio-seeker the studios have no room for. |
| 728 | 8 | `private double doorsNeeded(boolean family, double headShortfall)` | Doors one segment is short: households here with nowhere, plus the ones the job market is about to bring, at the segment's own household size. |
| 738 | 3 | `public double priceForSegment(BuildingsTemplate t)` | The price the segment this template belongs to is charging. |
| 748 | 8 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | What the city's households would pay for it, not what it would collect if it were full. |
| 759 | 3 | `public double[] retirementDemandAndCapacity(Game game)` | Housing: demand is people actually living in it. |
| 794 | 7 | `public boolean mayRetire(BuildingsTemplate t)` | A residential holding is only sheddable if ITS OWN segment has doors to spare - AND NOBODY IN THE CITY IS SHARING A DOOR THEY DID NOT CHOOSE. |
| 803 | 4 | `public String noRetirementReason(boolean distress)` |  |
| 810 | 1 | `public double unitsOf(BuildingsTemplate t)` | People, not doors: the measure the spare-capacity rule counts in. |

### THE SCREEN (lines 812-886)

| line | len | member | says |
|---:|---:|---|---|
| 818 | 1 | `public ham.citybuildersim.SectorStatements.Format statementFormat()` | Its formal statements' format (0.7.74, spec-sector-statements 4.6): a landlord, whose middle line is its net operating income, before its mortgages. |
| 821 | 1 | `public String inputLabel()` |  |
| 824 | 1 | `public boolean hasPlantBlock()` |  |
| 827 | 59 | `public List<Line> ownLines(Game game)` |  |

### SAVE, RESET, THE REFORM (lines 887-950)

| line | len | member | says |
|---:|---:|---|---|
| 892 | 13 | `protected void saveExtras(Map<String, Double> extras)` |  |
| 907 | 13 | `protected void restoreExtras(Map<String, Double> extras)` |  |
| 922 | 13 | `protected void resetExtras()` |  |
| 938 | 12 | `protected void redenominateExtras(double scale)` | The prices and the per-capacity costs are money; the doors, the weights and the rates are not. |


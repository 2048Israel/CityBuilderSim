# RealEstate.java - 938 lines · 93 methods · 9 constants · sectors

`ham/citybuildersim/sectors/RealEstate.java` - generated 2026-11-08 by CodeMap; line numbers are as of that run.

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

**Uses:** [BusinessInvestment](BusinessInvestment.md) (9), [BuildingsTemplate](BuildingsTemplate.md) (9), [FamilyModel](FamilyModel.md) (9), [Game](Game.md) (5), [PayTier](PayTier.md) (3), [Trade](Trade.md) (3), [Good](Good.md) (2), [Retail](Retail.md) (2), [SectorStatements](SectorStatements.md) (2), [Formats](Formats.md) (2), [Sector](Sector.md) (1), [BuildingType](BuildingType.md) (1), [Markets](Markets.md) (1), [GoodsMarket](GoodsMarket.md) (1), [BuildingsStacks](BuildingsStacks.md) (1)

**Used by (20):** [BuildingsTemplate](BuildingsTemplate.md), [BusinessInvestment](BusinessInvestment.md), [DiscoveryCheck](DiscoveryCheck.md), [EconomyManager](EconomyManager.md), [Game](Game.md), [HouseholdCheck](HouseholdCheck.md), [HousingCheck](HousingCheck.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [Migration](Migration.md), [MortgageCheck](MortgageCheck.md), [NewGameCheck](NewGameCheck.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [RoadCheck](RoadCheck.md), [SectorFlow](SectorFlow.md), [SectorScreen](SectorScreen.md), [Sectors](Sectors.md), [WageLadderCheck](WageLadderCheck.md)

## Sections

| line | section |
|---:|---|
| 147 | · the prices |
| 176 | · the doors |
| 220 | INPUTS FROM THE CITY, set each month by EconomyManager and Game |
| 274 | · readers |
| 318 | THE PRICE |
| 478 | THE SALE, at the bottom of the month |
| 512 | PLANNING - off jobs, and off the segment that is short |
| 800 | THE SCREEN |
| 875 | SAVE, RESET, THE REFORM |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 96 | `RealEstate.TARGET_RENT_BURDEN` | `.30` | What share of a working household's income rent should take. |
| 99 | `RealEstate.REFERENCE_EARNERS` | `2` | The household the yardstick is struck against: a couple both working... |
| 102 | `RealEstate.REFERENCE_HOME_CAPACITY` | `4` | ...in a home for four. |
| 110 | `RealEstate.LANDLORD_YIELD` | `.058` | What a landlord wants back each year for what the building cost. |
| 133 | `RealEstate.MAINTENANCE_PER_YEAR` | `.01` | Repairs: one percent a year of what a building cost to put up, placed as a real order with the builders in the building's own inputs. |
| 136 | `RealEstate.SCARCITY_ELASTICITY` | `1.0` | How hard rent answers a shortage of front doors. |
| 139 | `RealEstate.LEASE_MONTHS` | `12` | The lease, in months, which is also the lag. |
| 142 | `RealEstate.BUILD_MARGIN` | `.33` | The margin a new building has to clear over its own costs to be worth putting up. |
| 145 | `RealEstate.MIN_HOMES_FOR_A_MARKET` | `10` | Below this many front doors the ratio stops meaning anything. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 150 | `private double rentPrice` | Rent per person of dwelling capacity, family doors. |
| 153 | `private double studioRentPrice` | The studio price, per person of capacity. |
| 159 | `private double wageAnchor` | The unskilled wage the founding rent is struck on (R0d): PayTier's in every city but one founded the 1900 way, which opens on 1901's (WageLadder.anchor()). |
| 173 | `private double lastRentTarget` |  |
| 174 | `private double lastStudioTarget` |  |
| 178 | `private long homes` |  |
| 179 | `private double occupiedHomes` |  |
| 181 | `private long household` | Household CAPACITY - the people the residential buildings hold. |
| 182 | `private long population` |  |
| 183 | `private double householdCount` |  |
| 184 | `private double marginalHousingCost` |  |
| 187 | `private double structurePerCapacity` | What supplying one more person of capacity costs, land included - the cheapest home. |
| 188 | `private double landPerCapacity` |  |
| 190 | `private double studioCostPerCapacity` | ...and per segment, off the cheapest building that segment can supply. |
| 191 | `private double familyCostPerCapacity` |  |
| 194 | `private double ownedCapacity` | People the residential buildings hold, sites included - the break-even's denominator. |
| 196 | `private long studioHomes, familyHomes` |  |
| 197 | `private double studioSeekers, familySeekers` |  |
| 198 | `private double studioSeekerHeads, familySeekerHeads` |  |
| 201 | `private double studioRentWeight, familyRentWeight` | What the households behind the doors add up to, per segment. |
| 203 | `private double rBilledStudioWeight, rBilledFamilyWeight` | The weights the month's rent was actually billed on. |
| 205 | `private double rRentIncome` |  |
| 420 | `private double expectedMonthly` | Expected inflation a month, which each rent drifts at between leases (0.7.43): told by Game before the month's sale; never saved. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 93 | 846 | **type** `public final class RealEstate extends Sector` | The landlords. |

### the prices (lines 147-175)

| line | len | member | says |
|---:|---:|---|---|
| 162 | 3 | `public void setWageAnchor(double anchor)` | Takes the founding wage a reset seeds the rent from (R0d); the standing rents are left where they are, as a load restores them. |
| 167 | 5 | `public void foundOn(double anchor)` | A city founded on a wage (R0d): the anchor, and both rents at the rent for it, as a fresh sector opens at PayTier's. |

### the doors (lines 176-219)

| line | len | member | says |
|---:|---:|---|---|
| 207 | 7 | `public RealEstate()` |  |
| 216 | 3 | `public static double rentFor(double unskilledWage)` | Rent per person of capacity at a given unskilled wage: the founding value and the yardstick. |

### INPUTS FROM THE CITY, set each month by EconomyManager and Game (lines 220-273)

| line | len | member | says |
|---:|---:|---|---|
| 224 | 1 | `public void setHomes(long homes)` |  |
| 232 | 4 | `public void setOccupiedHomes(double occupied)` | How many doors have somebody behind them. |
| 237 | 1 | `public void setHousehold(long capacity)` |  |
| 238 | 1 | `public void setPopulation(long population)` |  |
| 239 | 1 | `public void setHouseholdCount(double count)` |  |
| 240 | 1 | `public void setMarginalHousingCost(double perCapacity)` |  |
| 242 | 4 | `public void setHousingCosts(double structurePerCapacity, double landPerCapacity)` |  |
| 247 | 4 | `public void setSegmentHousingCosts(double studioPerCapacity, double familyPerCapacity)` |  |
| 252 | 1 | `public void setOwnedHousingCapacity(double capacity)` |  |
| 254 | 10 | `public void setSegments(long studioHomes, long familyHomes, double studioSeekers, double familySeekers, double studioSeekerHead...` |  |
| 266 | 4 | `public void setRentWeight(double studio, double family)` | Both halves, from FamilyModel.house(). |
| 272 | 1 | `public void setRentWeight(double weight)` | The old single-weight setter: everything in the family half. |

### readers (lines 274-317)

| line | len | member | says |
|---:|---:|---|---|
| 276 | 1 | `public long getHomes()` |  |
| 277 | 1 | `public double getOccupiedHomes()` |  |
| 278 | 1 | `public long getHousehold()` |  |
| 279 | 1 | `public double getHouseholdCount()` |  |
| 280 | 1 | `public double getMarginalHousingCost()` |  |
| 281 | 1 | `public double getStructurePerCapacity()` |  |
| 282 | 1 | `public double getLandPerCapacity()` |  |
| 283 | 1 | `public double getStudioCostPerCapacity()` |  |
| 284 | 1 | `public double getFamilyCostPerCapacity()` |  |
| 285 | 1 | `public double getOwnedHousingCapacity()` |  |
| 286 | 1 | `public long getStudioHomes()` |  |
| 287 | 1 | `public long getFamilyHomes()` |  |
| 288 | 1 | `public double getStudioSeekers()` |  |
| 289 | 1 | `public double getFamilySeekers()` |  |
| 290 | 1 | `public double getStudioSeekerHeads()` |  |
| 291 | 1 | `public double getFamilySeekerHeads()` |  |
| 292 | 1 | `public double getRentWeight()` |  |
| 293 | 1 | `public double getStudioRentWeight()` |  |
| 294 | 1 | `public double getFamilyRentWeight()` |  |
| 295 | 1 | `public double getBilledRentWeight()` |  |
| 296 | 1 | `public double getBilledStudioWeight()` |  |
| 297 | 1 | `public double getBilledFamilyWeight()` |  |
| 298 | 1 | `public double getRentPrice()` |  |
| 299 | 1 | `public double getStudioRentPrice()` |  |
| 300 | 1 | `public double getRentTarget()` |  |
| 301 | 1 | `public double getStudioRentTarget()` |  |
| 303 | 1 | `public double getRentIncomeBilled()` | Rent billed in the month last sold - the figure the households were charged. |
| 305 | 1 | `public void setRentPrice(double price)` |  |
| 306 | 1 | `public void setStudioRentPrice(double price)` |  |
| 309 | 1 | `public double averageHomeSize()` | Capacity per front door, averaged over whatever the city has built. |
| 312 | 5 | `public double averageHouseholdSize(boolean family)` | Average household size in one segment. |

### THE PRICE (lines 318-477)

| line | len | member | says |
|---:|---:|---|---|
| 337 | 5 | `public double rentBreakEven()` | What the standing stock costs to hold this month, per person of capacity - this month's actual maintenance, property tax and interest over the capacity they are spread across. |
| 344 | 5 | `public double rentRequired()` | What a landlord WANTS on a new building, before scarcity: the yield on what the cheapest home costs. |
| 351 | 5 | `public double rentRequired(boolean family)` | The same required return, on the cheapest home THIS segment can supply. |
| 358 | 4 | `public double rentFloor()` | The floor under rent: the measured break-even, or the required return while there is nothing to measure. |
| 364 | 4 | `public double housingPressure()` | Households per front door: above one is a shortage, below one a glut. |
| 369 | 4 | `private double segmentPressure(long doors, double seekers)` |  |
| 374 | 1 | `public double studioPressure()` |  |
| 375 | 1 | `public double familyPressure()` |  |
| 377 | 1 | `public double rentScarcityMultiple()` |  |
| 379 | 4 | `public double scarcityMultipleOf(double pressure)` |  |
| 384 | 1 | `public double rentTarget()` |  |
| 385 | 1 | `public double studioRentTarget()` |  |
| 395 | 6 | `private double targetFor(double pressure, boolean family)` | One segment's target: what its own next building costs, times how tight that segment is, lifted so the COMPANY still covers its carry. |
| 407 | 11 | `private double carryLift()` | How far short of the carry the two legs come, struck on their own costs, weighted by what is actually billed. |
| 423 | 3 | `public void setExpectedMonthly(double monthly)` | Told by Game each month (Expectations.monthlyExpected()); anything not finite is ignored. |
| 436 | 14 | `public void repriceRent()` | Moves each rent a lease-length closer to what its market says it should be - in logs since 0.7.43, and drifting with expected inflation as it goes: the one form every seller in the city prices by (Retail.stickyPrice()... |
| 452 | 5 | `public double getAverageRentPaid()` | What one person of capacity actually cost on average this month - the price index's figure. |
| 459 | 6 | `public double blendedRentTarget()` | Where that average is heading - the invariant HousingCheck asserts the break-even against. |
| 471 | 6 | `public double getRentIncome()` | The month's rent, billed off the actual match: each segment's weight at its own price. |

### THE SALE, at the bottom of the month (lines 478-511)

| line | len | member | says |
|---:|---:|---|---|
| 483 | 15 | `public void sellOwnPriced(Markets markets, Game game)` |  |
| 504 | 3 | `public void endOfMonth(Game game)` | Rent walks here, and only here: moving a lagged price one step toward its target IS a month passing, and the load path must not do it. |
| 510 | 1 | `public double getInventoryValue()` | The landlords hold no stock: a home is not a unit in a warehouse. |

### PLANNING - off jobs, and off the segment that is short (lines 512-799)

| line | len | member | says |
|---:|---:|---|---|
| 517 | 114 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` |  |
| 633 | 3 | `public double doorShortfall(boolean family)` | Households in one segment with no door of their own, or fewer than none. |
| 643 | 3 | `private double doorsWanted(boolean family)` | The same shortfall as a planner reads it: less the doors of that segment already on the landlords' sites (0.7.17) - see plan(), "AND SO DO THE HOMES ON SITE". |
| 648 | 12 | `private double doorsOnSite(boolean family)` | Doors of one segment on the landlords' sites. |
| 662 | 3 | `private double capacityOnSite()` | People of capacity on the landlords' sites. |
| 670 | 4 | `public double monthsOfWorkOnSite()` | Months of the builders' site output the landlords' sites owe (0.7.17), for the investor page. |
| 676 | 4 | `private boolean hasDoorsToSpare(BuildingsTemplate t)` | Whether the segment this template belongs to has spare doors. |
| 687 | 7 | `public double latentHeadShortfall()` | The head shortage plan() would see if it were asked right now - for the credit check, and the harnesses. |
| 702 | 7 | `private double fillableDoors(BuildingsTemplate t, double headShortfall)` | How many of this template's doors the city would actually put somebody in: a studio only a studio-seeker; a family unit a family first and then a studio-seeker the studios have no room for. |
| 716 | 8 | `private double doorsNeeded(boolean family, double headShortfall)` | Doors one segment is short: households here with nowhere, plus the ones the job market is about to bring, at the segment's own household size. |
| 726 | 3 | `public double priceForSegment(BuildingsTemplate t)` | The price the segment this template belongs to is charging. |
| 736 | 8 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | What the city's households would pay for it, not what it would collect if it were full. |
| 747 | 3 | `public double[] retirementDemandAndCapacity(Game game)` | Housing: demand is people actually living in it. |
| 782 | 7 | `public boolean mayRetire(BuildingsTemplate t)` | A residential holding is only sheddable if ITS OWN segment has doors to spare - AND NOBODY IN THE CITY IS SHARING A DOOR THEY DID NOT CHOOSE. |
| 791 | 4 | `public String noRetirementReason(boolean distress)` |  |
| 798 | 1 | `public double unitsOf(BuildingsTemplate t)` | People, not doors: the measure the spare-capacity rule counts in. |

### THE SCREEN (lines 800-874)

| line | len | member | says |
|---:|---:|---|---|
| 806 | 1 | `public ham.citybuildersim.SectorStatements.Format statementFormat()` | Its formal statements' format (0.7.74, spec-sector-statements 4.6): a landlord, whose middle line is its net operating income, before its mortgages. |
| 809 | 1 | `public String inputLabel()` |  |
| 812 | 1 | `public boolean hasPlantBlock()` |  |
| 815 | 59 | `public List<Line> ownLines(Game game)` |  |

### SAVE, RESET, THE REFORM (lines 875-938)

| line | len | member | says |
|---:|---:|---|---|
| 880 | 13 | `protected void saveExtras(Map<String, Double> extras)` |  |
| 895 | 13 | `protected void restoreExtras(Map<String, Double> extras)` |  |
| 910 | 13 | `protected void resetExtras()` |  |
| 926 | 12 | `protected void redenominateExtras(double scale)` | The prices and the per-capacity costs are money; the doors, the weights and the rates are not. |


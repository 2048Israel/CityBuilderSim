# RealEstate.java - 1,067 lines · 98 methods · 10 constants · sectors

`ham/citybuildersim/sectors/RealEstate.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

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
> ... (39 more lines in the source)

**Uses:** [BusinessInvestment](BusinessInvestment.md) (9), [BuildingsTemplate](BuildingsTemplate.md) (9), [FamilyModel](FamilyModel.md) (9), [Game](Game.md) (5), [PayTier](PayTier.md) (3), [Trade](Trade.md) (3), [Good](Good.md) (2), [Retail](Retail.md) (2), [SectorStatements](SectorStatements.md) (2), [Formats](Formats.md) (2), [Sector](Sector.md) (1), [BuildingType](BuildingType.md) (1), [Markets](Markets.md) (1), [GoodsMarket](GoodsMarket.md) (1), [BuildingsStacks](BuildingsStacks.md) (1), [NewUnit](NewUnit.md) (1)

**Used by (23):** [BuildingsTemplate](BuildingsTemplate.md), [BusinessInvestment](BusinessInvestment.md), [DiscoveryCheck](DiscoveryCheck.md), [EconomyManager](EconomyManager.md), [Game](Game.md), [HouseholdCheck](HouseholdCheck.md), [HousingCheck](HousingCheck.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [Migration](Migration.md), [MortgageCheck](MortgageCheck.md), [NewGameCheck](NewGameCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [ReadPathCheck](ReadPathCheck.md), [ReloadCheck](ReloadCheck.md), [RoadCheck](RoadCheck.md), [SectorFlow](SectorFlow.md), [SectorScreen](SectorScreen.md), [Sectors](Sectors.md), [StaffingDrawCheck](StaffingDrawCheck.md), [WageLadderCheck](WageLadderCheck.md)

## Sections

| line | section |
|---:|---|
| 178 | · the prices |
| 207 | · the doors |
| 261 | INPUTS FROM THE CITY, set each month by EconomyManager and Game |
| 356 | · readers |
| 401 | THE PRICE |
| 583 | THE SALE, at the bottom of the month |
| 617 | PLANNING - off jobs, and off the segment that is short |
| 919 | THE SCREEN |
| 994 | SAVE, RESET, THE REFORM |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 124 | `RealEstate.TARGET_RENT_BURDEN` | `.30` | What share of a working household's income rent should take. |
| 127 | `RealEstate.REFERENCE_EARNERS` | `2` | The household the yardstick is struck against: a couple both working... |
| 130 | `RealEstate.REFERENCE_HOME_CAPACITY` | `4` | ...in a home for four. |
| 138 | `RealEstate.LANDLORD_YIELD` | `.058` | What a landlord wants back each year for what the building cost. |
| 161 | `RealEstate.MAINTENANCE_PER_YEAR` | `.01` | Repairs: one percent a year of what a building cost to put up, placed as a real order with the builders in the building's own inputs. |
| 164 | `RealEstate.SCARCITY_ELASTICITY` | `1.0` | How hard rent answers a shortage of front doors. |
| 167 | `RealEstate.GLUT_PRESSURE` | `1.0` | Households a door below which a segment is in glut and its rent is not lifted to cover the carry, an empty flat being the landlord's loss (Jerus, 2026-10-31, RN1). |
| 170 | `RealEstate.LEASE_MONTHS` | `12` | The lease, in months, which is also the lag. |
| 173 | `RealEstate.BUILD_MARGIN` | `.33` | The margin a new building has to clear over its own costs to be worth putting up. |
| 176 | `RealEstate.MIN_HOMES_FOR_A_MARKET` | `10` | Below this many front doors the ratio stops meaning anything. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 181 | `private double rentPrice` | Rent per person of dwelling capacity, family doors. |
| 184 | `private double studioRentPrice` | The studio price, per person of capacity. |
| 190 | `private double wageAnchor` | The unskilled wage the founding rent is struck on (R0d): PayTier's in every city but one founded the 1900 way, which opens on 1901's (WageLadder.anchor()). |
| 204 | `private double lastRentTarget` |  |
| 205 | `private double lastStudioTarget` |  |
| 209 | `private long homes` |  |
| 210 | `private double occupiedHomes` |  |
| 212 | `private long household` | Household CAPACITY - the people the residential buildings hold. |
| 213 | `private long population` |  |
| 214 | `private double householdCount` |  |
| 215 | `private double marginalHousingCost` |  |
| 218 | `private double structurePerCapacity` | What supplying one more person of capacity costs, land included - the cheapest home. |
| 219 | `private double landPerCapacity` |  |
| 221 | `private double studioCostPerCapacity` | ...and per segment, off the cheapest building that segment can supply. |
| 222 | `private double familyCostPerCapacity` |  |
| 226 | `private double ownedCapacity` | Last counted RE-owned places, from HousingInventory including sites, excluding the founding endowment. |
| 228 | `private double loadedCapacity` | ...the count a save held, waiting for the load's rebuild to have counted (RN2): see restoreLoadedCapacity(); NaN when none waits (a count of none is a count: REV-FB16). |
| 235 | `private double strandedDebtShare` | The share, in [0,1], of the company's debt whose homes are gone (RN1, 2026-11-09): Game.retire sells the land back and leaves the mortgage on the company, so what is left of the debt is the closed homes' and not the s... |
| 237 | `private long studioHomes, familyHomes` |  |
| 238 | `private double studioSeekers, familySeekers` |  |
| 239 | `private double studioSeekerHeads, familySeekerHeads` |  |
| 242 | `private double studioRentWeight, familyRentWeight` | What the households behind the doors add up to, per segment. |
| 244 | `private double rBilledStudioWeight, rBilledFamilyWeight` | The weights the month's rent was actually billed on. |
| 246 | `private double rRentIncome` |  |
| 525 | `private double expectedMonthly` | Expected inflation a month, which each rent drifts at between leases (0.7.43): told by Game before the month's sale; never saved. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 121 | 947 | **type** `public final class RealEstate extends Sector` | The landlords. |

### the prices (lines 178-206)

| line | len | member | says |
|---:|---:|---|---|
| 193 | 3 | `public void setWageAnchor(double anchor)` | Takes the founding wage a reset seeds the rent from (R0d); the standing rents are left where they are, as a load restores them. |
| 198 | 5 | `public void foundOn(double anchor)` | A city founded on a wage (R0d): the anchor, and both rents at the rent for it, as a fresh sector opens at PayTier's. |

### the doors (lines 207-260)

| line | len | member | says |
|---:|---:|---|---|
| 248 | 7 | `public RealEstate()` |  |
| 257 | 3 | `public static double rentFor(double unskilledWage)` | Rent per person of capacity at a given unskilled wage: the founding value and the yardstick. |

### INPUTS FROM THE CITY, set each month by EconomyManager and Game (lines 261-355)

| line | len | member | says |
|---:|---:|---|---|
| 265 | 1 | `public void setHomes(long homes)` |  |
| 273 | 4 | `public void setOccupiedHomes(double occupied)` | How many doors have somebody behind them. |
| 278 | 1 | `public void setHousehold(long capacity)` |  |
| 279 | 1 | `public void setPopulation(long population)` |  |
| 280 | 1 | `public void setHouseholdCount(double count)` |  |
| 281 | 1 | `public void setMarginalHousingCost(double perCapacity)` |  |
| 283 | 4 | `public void setHousingCosts(double structurePerCapacity, double landPerCapacity)` |  |
| 288 | 4 | `public void setSegmentHousingCosts(double studioPerCapacity, double familyPerCapacity)` |  |
| 304 | 11 | `public void setOwnedHousingCapacity(double capacity)` | Told by the economy each month. |
| 325 | 4 | `public void restoreLoadedCapacity()` | The count the save held put back, once the load's rebuild has counted (RN2, 2026-11-22; Game.rebuildSimulationState(), after the economy's updateEcon()). |
| 331 | 1 | `public double strandedDebtShare()` | The share of the debt whose homes are closed (RN1): see setOwnedHousingCapacity(). |
| 334 | 1 | `public double carriedInterest()` | The interest of the homes still standing: all of it but the closed homes' share (RN1). |
| 336 | 10 | `public void setSegments(long studioHomes, long familyHomes, double studioSeekers, double familySeekers, double studioSeekerHead...` |  |
| 348 | 4 | `public void setRentWeight(double studio, double family)` | Both halves, from FamilyModel.house(). |
| 354 | 1 | `public void setRentWeight(double weight)` | The old single-weight setter: everything in the family half. |

### readers (lines 356-400)

| line | len | member | says |
|---:|---:|---|---|
| 358 | 1 | `public long getHomes()` |  |
| 359 | 1 | `public double getOccupiedHomes()` |  |
| 360 | 1 | `public long getHousehold()` |  |
| 361 | 1 | `public double getHouseholdCount()` |  |
| 362 | 1 | `public double getMarginalHousingCost()` |  |
| 363 | 1 | `public double getStructurePerCapacity()` |  |
| 364 | 1 | `public double getLandPerCapacity()` |  |
| 365 | 1 | `public double getStudioCostPerCapacity()` |  |
| 366 | 1 | `public double getFamilyCostPerCapacity()` |  |
| 368 | 1 | `public double getOwnedHousingCapacity()` | The last inventory count used to allocate the debt; the live inventory is BuildingManager.getHousingInventory(). |
| 369 | 1 | `public long getStudioHomes()` |  |
| 370 | 1 | `public long getFamilyHomes()` |  |
| 371 | 1 | `public double getStudioSeekers()` |  |
| 372 | 1 | `public double getFamilySeekers()` |  |
| 373 | 1 | `public double getStudioSeekerHeads()` |  |
| 374 | 1 | `public double getFamilySeekerHeads()` |  |
| 375 | 1 | `public double getRentWeight()` |  |
| 376 | 1 | `public double getStudioRentWeight()` |  |
| 377 | 1 | `public double getFamilyRentWeight()` |  |
| 378 | 1 | `public double getBilledRentWeight()` |  |
| 379 | 1 | `public double getBilledStudioWeight()` |  |
| 380 | 1 | `public double getBilledFamilyWeight()` |  |
| 381 | 1 | `public double getRentPrice()` |  |
| 382 | 1 | `public double getStudioRentPrice()` |  |
| 383 | 1 | `public double getRentTarget()` |  |
| 384 | 1 | `public double getStudioRentTarget()` |  |
| 386 | 1 | `public double getRentIncomeBilled()` | Rent billed in the month last sold - the figure the households were charged. |
| 388 | 1 | `public void setRentPrice(double price)` |  |
| 389 | 1 | `public void setStudioRentPrice(double price)` |  |
| 392 | 1 | `public double averageHomeSize()` | Capacity per front door, averaged over whatever the city has built. |
| 395 | 5 | `public double averageHouseholdSize(boolean family)` | Average household size in one segment. |

### THE PRICE (lines 401-582)

| line | len | member | says |
|---:|---:|---|---|
| 428 | 5 | `public double rentBreakEven()` | What the standing stock costs to hold this month, per person of capacity - this month's actual maintenance, property tax and interest over the capacity they are spread across. |
| 435 | 5 | `public double rentRequired()` | What a landlord WANTS on a new building, before scarcity: the yield on what the cheapest home costs. |
| 442 | 5 | `public double rentRequired(boolean family)` | The same required return, on the cheapest home THIS segment can supply. |
| 449 | 4 | `public double rentFloor()` | The floor under rent: the measured break-even, or the required return while there is nothing to measure. |
| 455 | 4 | `public double housingPressure()` | Households per front door: above one is a shortage, below one a glut. |
| 460 | 4 | `private double segmentPressure(long doors, double seekers)` |  |
| 465 | 1 | `public double studioPressure()` |  |
| 466 | 1 | `public double familyPressure()` |  |
| 468 | 1 | `public double rentScarcityMultiple()` |  |
| 470 | 4 | `public double scarcityMultipleOf(double pressure)` |  |
| 475 | 1 | `public double rentTarget()` |  |
| 476 | 1 | `public double studioRentTarget()` |  |
| 491 | 7 | `private double targetFor(double pressure, boolean family)` | One segment's target: what its own next building costs, times how tight that segment is, lifted so the COMPANY still covers its carry. |
| 500 | 4 | `public boolean glutWaivesLift(boolean family)` | Whether this leg is in glut with a carry lift waiting that its glut has waived (RN1). |
| 512 | 11 | `private double carryLift()` | How far short of the carry the two legs come, struck on their own costs, weighted by what is actually billed. |
| 528 | 3 | `public void setExpectedMonthly(double monthly)` | Told by Game each month (Expectations.monthlyExpected()); anything not finite is ignored. |
| 541 | 14 | `public void repriceRent()` | Moves each rent a lease-length closer to what its market says it should be - in logs since 0.7.43, and drifting with expected inflation as it goes: the one form every seller in the city prices by (Retail.stickyPrice()... |
| 557 | 5 | `public double getAverageRentPaid()` | What one person of capacity actually cost on average this month - the price index's figure. |
| 564 | 6 | `public double blendedRentTarget()` | Where that average is heading - the invariant HousingCheck asserts the break-even against. |
| 576 | 6 | `public double getRentIncome()` | The month's rent, billed off the actual match: each segment's weight at its own price. |

### THE SALE, at the bottom of the month (lines 583-616)

| line | len | member | says |
|---:|---:|---|---|
| 588 | 15 | `public void sellOwnPriced(Markets markets, Game game)` |  |
| 609 | 3 | `public void endOfMonth(Game game)` | Rent walks here, and only here: moving a lagged price one step toward its target IS a month passing, and the load path must not do it. |
| 615 | 1 | `public double getInventoryValue()` | The landlords hold no stock: a home is not a unit in a warehouse. |

### PLANNING - off jobs, and off the segment that is short (lines 617-918)

| line | len | member | says |
|---:|---:|---|---|
| 627 | 1 | `public boolean ordersOnlyWhatItCanStaff()` | False (HD1): the landlords build homes before anyone can staff them. |
| 630 | 117 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` |  |
| 749 | 3 | `public double doorShortfall(boolean family)` | Households in one segment with no door of their own, or fewer than none. |
| 759 | 3 | `private double doorsWanted(boolean family)` | The same shortfall as a planner reads it: less the doors of that segment already on the landlords' sites (0.7.17) - see plan(), "AND SO DO THE HOMES ON SITE". |
| 764 | 12 | `private double doorsOnSite(boolean family)` | Doors of one segment on the landlords' sites. |
| 778 | 3 | `private double capacityOnSite()` | People of capacity on the landlords' sites. |
| 786 | 4 | `public double monthsOfWorkOnSite()` | Months of the builders' site output the landlords' sites owe (0.7.17), for the investor page. |
| 792 | 4 | `private boolean hasDoorsToSpare(BuildingsTemplate t)` | Whether the segment this template belongs to has spare doors. |
| 803 | 7 | `public double latentHeadShortfall()` | The head shortage plan() would see if it were asked right now - for the credit check, and the harnesses. |
| 818 | 7 | `private double fillableDoors(BuildingsTemplate t, double headShortfall)` | How many of this template's doors the city would actually put somebody in: a studio only a studio-seeker; a family unit a family first and then a studio-seeker the studios have no room for. |
| 832 | 8 | `private double doorsNeeded(boolean family, double headShortfall)` | Doors one segment is short: households here with nowhere, plus the ones the job market is about to bring, at the segment's own household size. |
| 842 | 3 | `public double priceForSegment(BuildingsTemplate t)` | The price the segment this template belongs to is charging. |
| 854 | 9 | `public double estimatedMonthlyProfit(NewUnit u, BusinessInvestment plans)` | What the city's households would pay for it, not what it would collect if it were full. |
| 866 | 3 | `public double[] retirementDemandAndCapacity(Game game)` | Housing: demand is people actually living in it. |
| 901 | 7 | `public boolean mayRetire(BuildingsTemplate t)` | A residential holding is only sheddable if ITS OWN segment has doors to spare - AND NOBODY IN THE CITY IS SHARING A DOOR THEY DID NOT CHOOSE. |
| 910 | 4 | `public String noRetirementReason(boolean distress)` |  |
| 917 | 1 | `public double unitsOf(BuildingsTemplate t)` | People, not doors: the measure the spare-capacity rule counts in. |

### THE SCREEN (lines 919-993)

| line | len | member | says |
|---:|---:|---|---|
| 925 | 1 | `public ham.citybuildersim.SectorStatements.Format statementFormat()` | Its formal statements' format (0.7.74, spec-sector-statements 4.6): a landlord, whose middle line is its net operating income, before its mortgages. |
| 928 | 1 | `public String inputLabel()` |  |
| 931 | 1 | `public boolean hasPlantBlock()` |  |
| 934 | 59 | `public List<Line> ownLines(Game game)` |  |

### SAVE, RESET, THE REFORM (lines 994-1067)

| line | len | member | says |
|---:|---:|---|---|
| 999 | 16 | `protected void saveExtras(Map<String, Double> extras)` |  |
| 1017 | 18 | `protected void restoreExtras(Map<String, Double> extras)` |  |
| 1037 | 15 | `protected void resetExtras()` |  |
| 1055 | 12 | `protected void redenominateExtras(double scale)` | The prices and the per-capacity costs are money; the doors, the weights and the rates are not. |


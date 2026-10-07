# InfrastructureManager.java - 1,059 lines · 82 methods · 13 constants · model

`ham/citybuildersim/InfrastructureManager.java` - generated 2026-10-05 by CodeMap; line numbers are as of that run.

> The road network: what the city's buildings demand of it, what it can carry,
> and what happens when the first number passes the second.
> 
> WHY ROADS ARE A BUILDING
> 
> A road is not obviously a building, and the first instinct is to model the
> network as its own continuous quantity. It is modelled as a BuildingType
> instead, and that choice buys the whole feature almost for free: ordering road
> capacity goes through the construction sector, consumes materials and land,
> takes months to finish, shows up in the construction queue, can be demolished,
> and survives a save - all of it machinery that already exists and is already
> tested. A parallel system would have had to reimplement every one of those and
> would have been the only thing in the game that worked differently.
> 
> The city pays for roads out of its own cash, like the power and water plants,
> and the construction sector earns the revenue for building them. That is the
> point of putting them through construction rather than conjuring them: public
> works are a demand-side lever on a private industry, and now they visibly are.
> 
> WHY THE RESPONSE CURVE IS NOT A STRAIGHT LINE
> 
> Electricity and water are step functions in this game: the ratio is
> production over consumption, so a city one unit short is a city 99% supplied.
> That is right for a wire and wrong for a road.
> 
> Traffic does not degrade linearly. A network runs at full speed until it is
> near capacity and then falls over fairly suddenly, which is why a road that
> carried yesterday's traffic fine can gridlock after one more office opens. So
> the network here is free-flowing up to FREE_FLOW of capacity and degrades as
> the inverse of how far past that it is pushed.
> 
> And it has a floor. A gridlocked city still moves - people walk, deliveries
> arrive late rather than never - so throughput bottoms out at MIN_THROUGHPUT
> rather than going to zero the way an unpowered factory does. That makes
> congestion a tax on growth rather than an instant death, which is the right
> shape for something a player is meant to notice and then fix.

**Uses:** [Traffic](Traffic.md) (36), [TaxPolicy](TaxPolicy.md) (3), [CityNeeds](CityNeeds.md) (2)

**Used by (17):** [BuildAdvice](BuildAdvice.md), [BuildCardCheck](BuildCardCheck.md), [CarCheck](CarCheck.md), [CityNeeds](CityNeeds.md), [EconomyManager](EconomyManager.md), [Game](Game.md), [InfrastructureCheck](InfrastructureCheck.md), [InfrastructureScreen](InfrastructureScreen.md), [LongPlaytest](LongPlaytest.md), [Motoring](Motoring.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [ServicesManager](ServicesManager.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [TradeCostCheck](TradeCostCheck.md)

## Sections

| line | section |
|---:|---|
| 65 | · inputs |
| 80 | THE SAME LOAD, IN THE THREE STREAMS IT IS MADE OF (2026-09-16) |
| 136 | THE MODES (2026-09-16) |
| 228 | THE CARS (2026-09-16) |
| 320 | · the remembered jam |
| 475 | · the fare |
| 524 | WHO RIDES, BY WHAT THEY PAY (0.7.49) |
| 834 | · results |
| 941 | FROM TRIPS TO THE ROAD (0.7.29) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 51 | `InfrastructureManager.BASE_CAPACITY` | `400` | The streets that already exist, before the city builds anything. |
| 54 | `InfrastructureManager.FREE_FLOW` | `.9` | Up to this share of capacity, traffic moves freely. |
| 57 | `InfrastructureManager.MIN_THROUGHPUT` | `.35` | However bad it gets, the city does not stop moving entirely. |
| 60 | `InfrastructureManager.STRAINED` | `.85` | Utilisation past which the network is worth warning about. |
| 193 | `InfrastructureManager.TRANSIT_MAX_SHARE` | `.65` | The share of every group of commuters a city's lines reach (0.7.49): the car-less and the owners alike, so also the most of its commuters any city can ever put on transit. |
| 196 | `InfrastructureManager.TRANSIT_NEEDS_ROAD` | `2.0` | Transit capacity a city can use, per unit of road capacity under it. |
| 199 | `InfrastructureManager.BULK_HIGHWAY_RELIEF` | `.40` | What a fully grade-separated network takes off a tonne of bulk. |
| 202 | `InfrastructureManager.GOODS_HIGHWAY_RELIEF` | `.15` | ...and off a crate of goods, which shares fewer junctions to begin with. |
| 225 | `InfrastructureManager.RAIL_ROAD_RELIEF` | `.75` | ...AND RAIL TAKES THE LONG HAUL OFF IT ALTOGETHER, which is the third mode and the only one the city does not own. |
| 271 | `InfrastructureManager.CAR_LOAD_AT_SATURATION` | `3.0` | What a city where every household owns a car asks of the road, against the same city where none does. |
| 289 | `InfrastructureManager.CAR_OWNER_RIDES_AT_GRIDLOCK` | `.75` | How many of the people who own a car get on the tram anyway once the road is completely gridlocked. |
| 292 | `InfrastructureManager.JAM_MEMORY` | `.25` | How fast the remembered commute catches up with this month's. |
| 581 | `InfrastructureManager.MODE_SPREAD` | `1.0 / 3` | How a cell splits between two costs: all on the bus at half the drive's cost, all in the car at twice it, the straight line between. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 62 | `private double capacity` |  |
| 63 | `private double load` |  |
| 113 | `private final double[] byStream` |  |
| 294 | `private double carOwnership` |  |
| 322 | `private double rememberedThroughput` |  |
| 396 | `private double highwayCapacity` |  |
| 397 | `private double transitCapacity` |  |
| 398 | `private final double[] railShare` |  |
| 511 | `private double fareShare` |  |
| 514 | `private double fareDial` | The dial as the month was told it (0.7.49): what the owners weigh, at fareLevel. |
| 584 | `private double captiveShare` | The share of the working cells' workers with no car of their own; 1 until told (no cars). |
| 587 | `private double fuelPerJourney` | What a journey to work by car burns, in today's money; 0 until told. |
| 698 | `private double fareLevel` | The level a ride is charged at - the level the month's money constants are struck at (TaxPolicy.getExpectedLevel(), Expectations.getStruckLevel()): told by Game with every re-strike; 1 until it is. |
| 707 | `private double fareUnit` | The currency's unit (Denomination.getUnit()), which turns the dial into founding money for the ridership curve (B6, 0.7.47): told by Game with every re-strike and at a reform's end; 1 until it is. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 41 | 1019 | **type** `public class InfrastructureManager` | The road network: what the city's buildings demand of it, what it can carry, and what happens when the first number passes the second. |

### inputs (lines 65-79)

| line | len | member | says |
|---:|---:|---|---|
| 71 | 3 | `public void setBuiltCapacity(double builtCapacity)` | before the base network is added |
| 76 | 3 | `public void setLoad(double load)` | Total monthly trips generated by everything standing. |

### THE SAME LOAD, IN THE THREE STREAMS IT IS MADE OF (2026-09-16) (lines 80-135)

| line | len | member | says |
|---:|---:|---|---|
| 116 | 5 | `public void setBreakdown(double[] streams)` | What the month's load is made of. |
| 122 | 3 | `public double getLoad(Traffic stream)` |  |
| 127 | 3 | `public double getShareOf(Traffic stream)` | What share of everything on the road is this. |
| 132 | 3 | `public double getFreightLoad()` | People and things, which is the cut a player acts on. |

### THE MODES (2026-09-16) (lines 136-227)

### THE CARS (2026-09-16) (lines 228-319)

| line | len | member | says |
|---:|---:|---|---|
| 300 | 4 | `public void setCarOwnership(double perHousehold)` | Cars per household, 0 to 1, handed over each month by Motoring. |
| 305 | 1 | `public double getCarOwnership()` |  |
| 315 | 4 | `public double carRoadFactor()` | How much road one commuter who is still driving asks for. |

### the remembered jam (lines 320-474)

| line | len | member | says |
|---:|---:|---|---|
| 334 | 5 | `public void noteCongestion(double ratio)` | Told what the road actually did, at the end of a month. |
| 340 | 1 | `public double getRememberedThroughput()` |  |
| 343 | 4 | `public void setRememberedThroughput(double ratio)` | Put back from a save. |
| 349 | 4 | `public double getJam()` | How bad the commute has been, 0 clear to 1 gridlocked. |
| 367 | 4 | `public double willingToRide()` | How willing the city's commuters are to get on a tram at all, against a city where nobody owns a car. |
| 385 | 5 | `public double getTransitCover()` | What share of the city's commuters the transit stock could carry if they all turned up - which is what decides whether a household bothers buying a car. |
| 392 | 3 | `public double getTransitServed()` | ...unclamped, as Build's transit ring reads it since 0.7.41: the room on the stock over the commuters, served (+∞ with no commuters). |
| 406 | 4 | `public void setModes(double highwayCapacity, double transitCapacity)` | grade-separated, weighted by each road's grade |
| 416 | 7 | `public void setRailShare(double[] shares)` | What share of each stream the railway is carrying, handed over each month by Game.chargeFreight(). |
| 424 | 3 | `public double getRailShare(Traffic stream)` |  |
| 436 | 5 | `private boolean hasModes()` | True once the city has something other than an ordinary street. |
| 460 | 1 | `private boolean streamsDiffer()` | Whether two businesses standing in this city can face DIFFERENT road ratios - and ONLY TRANSIT can do that. |
| 463 | 3 | `public double getHighwayShare()` | How much of the road network is built for lorries, 0 to 1. |
| 468 | 1 | `public double getTransitCapacity()` | What the transit stock could carry, before the road under it is considered. |
| 471 | 3 | `public double getUsableTransit()` | ...and what it can actually carry, which a city with no streets cannot raise. |

### the fare (lines 475-523)

| line | len | member | says |
|---:|---:|---|---|
| 505 | 5 | `public static double ridershipAt(double fare)` | What share of the ceiling would ride at a given fare, on the first pass's straight line. |
| 517 | 4 | `public void setFare(double fare)` | Told to the network each month, because the dial is the player's: in today's unit, read in founding money (fareUnit). |
| 522 | 1 | `public double getFareShare()` |  |

### WHO RIDES, BY WHAT THEY PAY (0.7.49) (lines 524-833)

| line | len | member | says |
|---:|---:|---|---|
| 590 | 4 | `public void setCommute(double captive, double fuel)` | Told by Game each month: the captive share (0 to 1; 1 if not finite) and a journey's fuel in today's money (0 or more). |
| 595 | 1 | `public double getCaptiveShare()` |  |
| 596 | 1 | `public double getFuelPerJourney()` |  |
| 599 | 1 | `public void redenominateFuel(double scale)` | A currency reform: the fuel struck is money, divided where it sits. |
| 608 | 6 | `public static double transitChosen(double driveCost, double rideCost)` | The share of a cell that chooses transit, driving at one cost and riding at the other: 1/2 at equal costs, 1 at a ride of half the drive or less, 0 at twice it or more, the straight line in (drive - ride) / (drive + r... |
| 616 | 1 | `public double ownersChoosingAt(double dial)` | The owners in reach who choose the bus on cost, at a dial: a ride (the dial at fareLevel) against a journey's fuel. |
| 619 | 4 | `public double ownersRidingAt(double dial)` | ...and who ride, at a dial: those, and of the rest the jam's share, walked down by the old fare curve (ridershipAt()). |
| 625 | 1 | `public double getCaptiveCommuters()` | The commuters with no car of their own: the captive share of the commuters. |
| 628 | 1 | `public double getCaptiveDemand()` | ...those of them a line reaches: TRANSIT_MAX_SHARE of them. |
| 631 | 1 | `public double getCaptiveRiders()` | ...and those who ride, whatever the fare: all in reach, to the seats. |
| 634 | 1 | `public double getWalking()` | The car-less who walk: out of reach of a line, or with no seat on it. |
| 637 | 1 | `public double getOwnerCommuters()` | The commuters with a car of their own. |
| 640 | 4 | `public double choiceRidersAt(double dial)` | The owners who ride at a dial: those in reach who ride (ownersRidingAt()), to the seats the car-less left. |
| 646 | 1 | `public double getChoiceRiders()` | ...at the month's dial. |
| 649 | 1 | `public double getDrivers()` | The owners who drive: those who are not on a bus. |
| 659 | 3 | `public double getTransitRiders()` | Commuters actually carried off the road this month (0.7.49): the car-less in reach, to the seats (getCaptiveRiders()), and the owners in reach who chose the bus or were put on it by the jam, to the seats left (getChoi... |
| 673 | 1 | `public double getTransitRoadCeiling()` | The second ceiling: what the road under the transit lets it carry, TRANSIT_NEEDS_ROAD times the street capacity. |
| 676 | 1 | `public double getTransitShareCeiling()` | The third: TRANSIT_MAX_SHARE of the commuters, of each group - the most a city's lines reach. |
| 679 | 1 | `public double getTransitCeiling()` | The lowest of the three ceilings - the stock, the road under it, the share - which is what a free system would carry. |
| 682 | 1 | `public double getRidersAtFare()` | ...what the fare's curve (ridershipAt()) leaves of it: the funnel's step until 0.7.49, before the cars walked it down (willingToRide()); the Transit page draws the riders by reason since, and nothing in the month read... |
| 685 | 1 | `public double ridersAt(double fare)` | The riders at a fare the city has not set, with today's ceilings, cars and fuel: the car-less, who ride at any fare, and the owners at it. |
| 695 | 1 | `public double faresAt(double fare)` | ...and a month of fares from them (0.7.38): those riders at a month of journeys each, each journey charged the dial at the level the fare is struck at (TaxPolicy.chargedFare(), since 0.7.45 - the UI spec's B7: it pric... |
| 701 | 1 | `public void setFareLevel(double level)` | Told by Game.restrikeMoneyConstants(); anything not positive is ignored. |
| 704 | 1 | `public double getFareLevel()` | The level a ride is charged at. |
| 710 | 1 | `public void setFareUnit(double unit)` | Told by Game.restrikeMoneyConstants() and Game.reformCurrency(); anything not positive is ignored. |
| 713 | 1 | `public double getFareUnit()` | The unit the dial is read in founding money at. |
| 723 | 1 | `public double backOnTheRoadAt(double fare)` | The trips a fare would put back onto the road (0.7.38), negative for trips it would take off: the riders it loses against today's, each asking carRoadFactor() of the road, as getEffectiveLoad() counts a commuter who d... |
| 726 | 9 | `public double roadCostOf(Traffic stream)` | What one unit of a stream costs the road, after the highways are counted. |
| 743 | 8 | `public double getEffectiveLoad()` | What the road is actually being asked to carry, after transit has taken its riders and the highways have eased the lorries. |
| 762 | 18 | `public double throughputOf(Traffic stream)` | What each stream actually gets through. |
| 806 | 15 | `public double throughputFor(double[] mix)` | The ratio a business with THIS mix of traffic actually feels. |
| 823 | 10 | `public double cityThroughput()` | What the city as a whole is getting through, blended over its own traffic. |

### results (lines 834-940)

| line | len | member | says |
|---:|---:|---|---|
| 836 | 1 | `public double getCapacity()` |  |
| 837 | 1 | `public double getLoad()` |  |
| 840 | 3 | `public double getBuiltCapacity()` | Built capacity only - what the player actually paid for. |
| 845 | 1 | `public double getFreeFlowLoad()` | The load past which traffic starts to slow: FREE_FLOW of the capacity (0.7.29, the Roads page's line). |
| 848 | 3 | `public double getUtilisation()` | Load over capacity. |
| 859 | 3 | `public double getServed()` | SERVED (0.7.41): capacity over the load the curve reads - the road's figure on every screen since Jerus's one rule for the gauges ("Served %, higher = better"), where it read how full, getUtilisation(). |
| 864 | 3 | `public static double throughputAtServed(double served)` | The flow at a served share of the traffic (0.7.41): throughputAt() at its load, one over it - the curve as the Roads page draws it, against served. |
| 874 | 3 | `public double getSpareCapacity()` | What the network could still take before it is full: capacity less the load the curve reads (0.7.29: it read the raw trips, which in a city with cars sit well under what the road carries - a road 161% full read 1,525 ... |
| 886 | 6 | `public double getThroughputRatio()` | What fraction of its business the city can actually conduct. |
| 900 | 7 | `public static double throughputAt(double utilisation)` | The curve itself, at any use of the road (0.7.29): 1 up to FREE_FLOW, then FREE_FLOW over the use, floored at MIN_THROUGHPUT. |
| 908 | 3 | `public boolean isCongested()` |  |
| 913 | 3 | `public boolean isStrained()` | True while there is still room, but not much. |
| 929 | 3 | `public double getHeadroom()` | How much more the network could carry before traffic starts to slow. |
| 934 | 6 | `public String getStatus()` | One line for the city panel. |

### FROM TRIPS TO THE ROAD (0.7.29) (lines 941-1059)

| line | len | member | says |
|---:|---:|---|---|
| 963 | 32 | **type** `public record RoadBreakdown(double[] raw, double transitOff, double carsAdd, double[] freightOff, double[] ...` | The effective load, taken apart: by stream, in Traffic order, the trips made (raw) and the trips on the road (onRoad); the commuters transit carried off it; what the cars add; what the highways and the railway take of... |
| 967 | 1 | `public double withoutCars()` _(in InfrastructureManager.RoadBreakdown)_ | The road with nobody driving: the load less what the cars add (the Roads page's "without cars"). |
| 970 | 4 | `public double costPerTrip(Traffic stream)` _(in InfrastructureManager.RoadBreakdown)_ | What one trip of a stream asks of the road once it is on it: on the road over trips made; 1 with none made. |
| 976 | 1 | `public double ridersAsDrivers()` _(in InfrastructureManager.RoadBreakdown)_ | What the commuters on transit would ask of the road if they drove instead, at today's car factor. |
| 979 | 1 | `public double driving()` _(in InfrastructureManager.RoadBreakdown)_ | The commuters still driving: those not on transit. |
| 982 | 5 | `public double freightOffTotal()` _(in InfrastructureManager.RoadBreakdown)_ | What the highways and the railway take off the freight, both streams together. |
| 989 | 5 | `public double rawTotal()` _(in InfrastructureManager.RoadBreakdown)_ | The trips made, all three streams together. |
| 997 | 21 | `public RoadBreakdown roadBreakdown()` | The walk from the trips the city makes to the load on its road, by the arithmetic getEffectiveLoad() sums. |
| 1028 | 22 | `public InfrastructureManager with(double capacityAdded, double highwayAdded, double transitAdded, double loadAdded, double[] st...` | This network with buildings added to it (0.7.24): a copy, every input as it stands, and the added roads' capacity, their grade-separated share, the added transit and the load the added buildings put on the road. |
| 1051 | 8 | `public void reset()` |  |


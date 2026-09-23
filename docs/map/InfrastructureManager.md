# InfrastructureManager.java - 686 lines · 43 methods · 12 constants · model

`ham/citybuildersim/InfrastructureManager.java` - generated 2026-09-22 by CodeMap; line numbers are as of that run.

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

**Uses:** [Traffic](Traffic.md) (24), [TaxPolicy](TaxPolicy.md) (1)

**Used by (11):** [CarCheck](CarCheck.md), [EconomyManager](EconomyManager.md), [Game](Game.md), [InfrastructureCheck](InfrastructureCheck.md), [LongPlaytest](LongPlaytest.md), [Motoring](Motoring.md), [RailCheck](RailCheck.md), [ServicesManager](ServicesManager.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [TradeCostCheck](TradeCostCheck.md)

## Sections

| line | section |
|---:|---|
| 65 | · inputs |
| 80 | THE SAME LOAD, IN THE THREE STREAMS IT IS MADE OF (2026-09-16) |
| 136 | THE MODES (2026-09-16) |
| 221 | THE CARS (2026-09-16) |
| 313 | · the remembered jam |
| 457 | · the fare |
| 614 | · results |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 51 | `InfrastructureManager.BASE_CAPACITY` | `400` | The streets that already exist, before the city builds anything. |
| 54 | `InfrastructureManager.FREE_FLOW` | `.9` | Up to this share of capacity, traffic moves freely. |
| 57 | `InfrastructureManager.MIN_THROUGHPUT` | `.35` | However bad it gets, the city does not stop moving entirely. |
| 60 | `InfrastructureManager.STRAINED` | `.85` | Utilisation past which the network is worth warning about. |
| 186 | `InfrastructureManager.TRANSIT_MAX_SHARE` | `.65` | The most of its commuters any city can ever put on transit. |
| 189 | `InfrastructureManager.TRANSIT_NEEDS_ROAD` | `2.0` | Transit capacity a city can use, per unit of road capacity under it. |
| 192 | `InfrastructureManager.BULK_HIGHWAY_RELIEF` | `.40` | What a fully grade-separated network takes off a tonne of bulk. |
| 195 | `InfrastructureManager.GOODS_HIGHWAY_RELIEF` | `.15` | ...and off a crate of goods, which shares fewer junctions to begin with. |
| 218 | `InfrastructureManager.RAIL_ROAD_RELIEF` | `.75` | ...AND RAIL TAKES THE LONG HAUL OFF IT ALTOGETHER, which is the third mode and the only one the city does not own. |
| 264 | `InfrastructureManager.CAR_LOAD_AT_SATURATION` | `3.0` | What a city where every household owns a car asks of the road, against the same city where none does. |
| 282 | `InfrastructureManager.CAR_OWNER_RIDES_AT_GRIDLOCK` | `.75` | How many of the people who own a car get on the tram anyway once the road is completely gridlocked. |
| 285 | `InfrastructureManager.JAM_MEMORY` | `.25` | How fast the remembered commute catches up with this month's. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 62 | `private double capacity` |  |
| 63 | `private double load` |  |
| 113 | `private final double[] byStream` |  |
| 287 | `private double carOwnership` |  |
| 315 | `private double rememberedThroughput` |  |
| 378 | `private double highwayCapacity` |  |
| 379 | `private double transitCapacity` |  |
| 380 | `private final double[] railShare` |  |
| 484 | `private double fareShare` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 41 | 646 | **type** `public class InfrastructureManager` | The road network: what the city's buildings demand of it, what it can carry, and what happens when the first number passes the second. |

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

### THE MODES (2026-09-16) (lines 136-220)

### THE CARS (2026-09-16) (lines 221-312)

| line | len | member | says |
|---:|---:|---|---|
| 293 | 4 | `public void setCarOwnership(double perHousehold)` | Cars per household, 0 to 1, handed over each month by Motoring. |
| 298 | 1 | `public double getCarOwnership()` |  |
| 308 | 4 | `public double carRoadFactor()` | How much road one commuter who is still driving asks for. |

### the remembered jam (lines 313-456)

| line | len | member | says |
|---:|---:|---|---|
| 327 | 5 | `public void noteCongestion(double ratio)` | Told what the road actually did, at the end of a month. |
| 333 | 1 | `public double getRememberedThroughput()` |  |
| 336 | 4 | `public void setRememberedThroughput(double ratio)` | Put back from a save. |
| 342 | 4 | `public double getJam()` | How bad the commute has been, 0 clear to 1 gridlocked. |
| 354 | 4 | `public double willingToRide()` | How willing the city's commuters are to get on a tram at all, against a city where nobody owns a car. |
| 372 | 5 | `public double getTransitCover()` | What share of the city's commuters the transit stock could carry if they all turned up - which is what decides whether a household bothers buying a car. |
| 388 | 4 | `public void setModes(double highwayCapacity, double transitCapacity)` | grade-separated, weighted by each road's grade |
| 398 | 7 | `public void setRailShare(double[] shares)` | What share of each stream the railway is carrying, handed over each month by Game.chargeFreight(). |
| 406 | 3 | `public double getRailShare(Traffic stream)` |  |
| 418 | 5 | `private boolean hasModes()` | True once the city has something other than an ordinary street. |
| 442 | 1 | `private boolean streamsDiffer()` | Whether two businesses standing in this city can face DIFFERENT road ratios - and ONLY TRANSIT can do that. |
| 445 | 3 | `public double getHighwayShare()` | How much of the road network is built for lorries, 0 to 1. |
| 450 | 1 | `public double getTransitCapacity()` | What the transit stock could carry, before the road under it is considered. |
| 453 | 3 | `public double getUsableTransit()` | ...and what it can actually carry, which a city with no streets cannot raise. |

### the fare (lines 457-613)

| line | len | member | says |
|---:|---:|---|---|
| 478 | 5 | `public static double ridershipAt(double fare)` | What share of the ceiling actually rides, at a given fare. |
| 487 | 3 | `public void setFare(double fare)` | Told to the network each month, because the dial is the player's. |
| 491 | 1 | `public double getFareShare()` |  |
| 500 | 4 | `public double getTransitRiders()` | Commuters actually carried off the road this month. |
| 506 | 9 | `public double roadCostOf(Traffic stream)` | What one unit of a stream costs the road, after the highways are counted. |
| 523 | 8 | `public double getEffectiveLoad()` | What the road is actually being asked to carry, after transit has taken its riders and the highways have eased the lorries. |
| 542 | 18 | `public double throughputOf(Traffic stream)` | What each stream actually gets through. |
| 586 | 15 | `public double throughputFor(double[] mix)` | The ratio a business with THIS mix of traffic actually feels. |
| 603 | 10 | `public double cityThroughput()` | What the city as a whole is getting through, blended over its own traffic. |

### results (lines 614-686)

| line | len | member | says |
|---:|---:|---|---|
| 616 | 1 | `public double getCapacity()` |  |
| 617 | 1 | `public double getLoad()` |  |
| 620 | 3 | `public double getBuiltCapacity()` | Built capacity only - what the player actually paid for. |
| 625 | 3 | `public double getUtilisation()` | Load over capacity. |
| 629 | 3 | `public double getSpareCapacity()` |  |
| 641 | 11 | `public double getThroughputRatio()` | What fraction of its business the city can actually conduct. |
| 653 | 3 | `public boolean isCongested()` |  |
| 658 | 3 | `public boolean isStrained()` | True while there is still room, but not much. |
| 668 | 3 | `public double getHeadroom()` | How much more the network could carry before traffic starts to slow. |
| 673 | 6 | `public String getStatus()` | One line for the city panel. |
| 680 | 6 | `public void reset()` |  |


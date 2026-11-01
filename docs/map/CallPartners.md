# CallPartners.java - 154 lines · 10 methods · 3 constants · model

`ham/citybuildersim/CallPartners.java` - generated 2026-11-01 by CodeMap; line numbers are as of that run.

> Who each ship and train of the month calls on: a nation drawn by the month's tonnes of the call's kind of cargo, and the compass bearing
> of its node from the city (batch WN7; docs/studies/spec-nations.md 9); immutable, pure, and safe on the screen's thread.
> 
> <p>WHY THIS EXISTS. The map drew the sea trade as boats on the sea's routes and the railed trade as trains on the line to the world, and
> every one of them left the way its route went: nothing on the water said which nation it was bound for, though the month's trade by partner
> (PartnerTrade, WN3) says exactly who bought what. A call now carries a partner, drawn here from the month's tonnes of its cargo to each
> nation in its direction (dry bulk exports for a bulker leaving, container tonnes both ways for a feeder, which carries both), so a nation
> that took half the city's coal is half the bulkers' destinations and one that took none is never one; and the boat leaves the offing on
> its partner's bearing (BoatSchedule). A train goes by land: only the host nation and the nodes within WorldMarket.MAX_LAND_KM of the
> city, the reach the market's own land legs have, can be a train's partner. A call's partner is a hash of the call and the month's
> snapshot, held nowhere: nothing in the model's month reads it, so no trace moves, and panning away and back shows the same boat bound for
> the same nation.
> 
> <p>HOW. of() makes, once a snapshot, a table of cumulative tonnes by nation for every kind of cargo, direction (out, in, and both for the
> boxes, whose calls carry both ways) and reach (any partner, or by land); pick() is then a binary search of one of them under a hash.
> A kind with no tonnes in the direction falls back on the direction's tonnes of every kind (the snapshot's own exports and imports by
> nation), and a month with none gives no partner at all (0); NONE is that always.

**Uses:** [Ports](Ports.md) (5), [PartnerTrade](PartnerTrade.md) (4), [WorldMarket](WorldMarket.md) (1), [TradeLanes](TradeLanes.md) (1), [EarthProj](EarthProj.md) (1), [World](World.md) (1)

**Used by (3):** [BoatSchedule](BoatSchedule.md), [PartnerCallsCheck](PartnerCallsCheck.md), [TrainSchedule](TrainSchedule.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 25 | `CallPartners.NONE` | `new CallPartners()` | No partners: every pick is 0, no bearing is known, nothing is by land. |
| 28 | `CallPartners.LAND_KM` | `WorldMarket.MAX_LAND_KM` | How far a node is reached by land, in km: WorldMarket.MAX_LAND_KM, the market's own limit (the host nation, wherever its node, is always reached). |
| 31 | `CallPartners.OUT` | `0, IN = 1, BOTH = 2` | The directions a table is made for: the call's cargo going out, coming in, and both (the boxes). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 33 | `private final PartnerTrade.Snapshot snapshot` |  |
| 34 | `private final double cityLat, cityLon` |  |
| 35 | `private final int host, n` |  |
| 36 | `private final boolean[] land` |  |
| 37 | `private final double[] bearing` |  |
| 40 | `private final double[][][][] cargo` | Cumulative tonnes by nation: [kind of cargo][OUT, IN, BOTH][any, by land]; null where they add to nothing. |
| 43 | `private final double[][][] all` | The same of every kind of cargo together: [OUT, IN, BOTH][any, by land]; the fallback. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 22 | 133 | **type** `public final class CallPartners` | Who each ship and train of the month calls on: a nation drawn by the month's tonnes of the call's kind of cargo, and the compass bearing of its node from the city (batch WN7; docs/studies/spec-nations.md 9); immutable... |
| 45 | 10 | `private CallPartners()` |  |
| 56 | 27 | `private CallPartners(PartnerTrade.Snapshot s, double cityLat, double cityLon, double[] nodeLat, double[] nodeLon, int host)` |  |
| 85 | 14 | `private void fill(double[][] into, double[] a, double[] b)` | Makes the cumulative tonnes by nation into [any, by land]: those of `a`, and of `b` added where it is given; null where they add to nothing. |
| 105 | 3 | `public static CallPartners of(PartnerTrade.Snapshot s, double cityLat, double cityLon, double[] nodeLat, double[] nodeLon, int ...` | The partners of a month's trade for a city standing at (cityLat, cityLon) in nation `host` (1-based): by the snapshot's tonnes of each kind of cargo, and the nodes' places (index nation - 1; a node with no place has n... |
| 110 | 4 | `public boolean matches(PartnerTrade.Snapshot s, double cityLat, double cityLon, int host)` | Whether this is what of() would make of these: the same snapshot object and the same place and host, so a map keeps one until the month's snapshot or the place moves. |
| 121 | 16 | `public int pick(Ports.Cargo cargo, boolean inbound, boolean byLand, long hash)` | The nation (1-based) a call of this kind and direction is bound for or from, by the hash: drawn with a probability proportional to the month's tonnes of `cargo` in the direction (imports for an `inbound` call, exports... |
| 139 | 3 | `public boolean byLand(int nation)` | Whether a train can reach the nation: it is the host, or its node is within LAND_KM of the city by the great circle. |
| 144 | 3 | `public double bearing(int nation)` | The compass bearing of a nation's node from the city, degrees clockwise from north in [0, 360); -1 where it is unknown or at the city's own place. |
| 149 | 3 | `public String nameOf(int nation)` | A nation's name as the snapshot has it, or null. |
| 153 | 1 | `public String toString()` |  |


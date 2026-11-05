# CallPartners.java - 233 lines · 16 methods · 3 constants · model

`ham/citybuildersim/CallPartners.java` - generated 2026-11-05 by CodeMap; line numbers are as of that run.

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
> 
> <p>WHAT THE MAP SHOWS OF THEM (slice U). seaPartners() is the nations a boat of a kind can be bound for, which BoatSchedule turns into the
> forks a lane draws at its offing; words() is what a boat's or a train's label says of its call, the class or kind and the nation by the way it goes.

**Uses:** [Ports](Ports.md) (12), [PartnerTrade](PartnerTrade.md) (4), [WorldMarket](WorldMarket.md) (1), [TradeLanes](TradeLanes.md) (1), [EarthProj](EarthProj.md) (1), [World](World.md) (1), [BoatSchedule](BoatSchedule.md) (1), [TrainSchedule](TrainSchedule.md) (1)

**Used by (4):** [BoatSchedule](BoatSchedule.md), [MapView](MapView.md), [PartnerCallsCheck](PartnerCallsCheck.md), [TrainSchedule](TrainSchedule.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 28 | `CallPartners.NONE` | `new CallPartners()` | No partners: every pick is 0, no bearing is known, nothing is by land. |
| 31 | `CallPartners.LAND_KM` | `WorldMarket.MAX_LAND_KM` | How far a node is reached by land, in km: WorldMarket.MAX_LAND_KM, the market's own limit (the host nation, wherever its node, is always reached). |
| 34 | `CallPartners.OUT` | `0, IN = 1, BOTH = 2` | The directions a table is made for: the call's cargo going out, coming in, and both (the boxes). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 36 | `private final PartnerTrade.Snapshot snapshot` |  |
| 37 | `private final double cityLat, cityLon` |  |
| 38 | `private final int host, n` |  |
| 39 | `private final boolean[] land` |  |
| 40 | `private final double[] bearing` |  |
| 43 | `private final double[][][][] cargo` | Cumulative tonnes by nation: [kind of cargo][OUT, IN, BOTH][any, by land]; null where they add to nothing. |
| 46 | `private final double[][][] all` | The same of every kind of cargo together: [OUT, IN, BOTH][any, by land]; the fallback. |
| 49 | `private final int[][] sea` | The nations a ship of each kind of cargo can be bound for or from, either direction, biggest first (seaPartners()): [kind of cargo][rank], nations 1-based; and each one's share of the kind's tonnes there, same order (... |
| 50 | `private final double[][] seaShare` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 25 | 209 | **type** `public final class CallPartners` | Who each ship and train of the month calls on: a nation drawn by the month's tonnes of the call's kind of cargo, and the compass bearing of its node from the city (batch WN7; docs/studies/spec-nations.md 9); immutable... |
| 52 | 12 | `private CallPartners()` |  |
| 65 | 30 | `private CallPartners(PartnerTrade.Snapshot s, double cityLat, double cityLon, double[] nodeLat, double[] nodeLon, int host)` |  |
| 100 | 21 | `private void seaNations(Ports.Cargo c)` | The nations pick() can draw for a ship of this kind, either direction (the tables a boat's call would draw by), by their tonnes there, biggest first, the lower number first on a tie - kept in sea[], and each one's sha... |
| 123 | 14 | `private void fill(double[][] into, double[] a, double[] b)` | Makes the cumulative tonnes by nation into [any, by land]: those of `a`, and of `b` added where it is given; null where they add to nothing. |
| 143 | 3 | `public static CallPartners of(PartnerTrade.Snapshot s, double cityLat, double cityLon, double[] nodeLat, double[] nodeLon, int ...` | The partners of a month's trade for a city standing at (cityLat, cityLon) in nation `host` (1-based): by the snapshot's tonnes of each kind of cargo, and the nodes' places (index nation - 1; a node with no place has n... |
| 148 | 4 | `public boolean matches(PartnerTrade.Snapshot s, double cityLat, double cityLon, int host)` | Whether this is what of() would make of these: the same snapshot object and the same place and host, so a map keeps one until the month's snapshot or the place moves. |
| 159 | 16 | `public int pick(Ports.Cargo cargo, boolean inbound, boolean byLand, long hash)` | The nation (1-based) a call of this kind and direction is bound for or from, by the hash: drawn with a probability proportional to the month's tonnes of `cargo` in the direction (imports for an `inbound` call, exports... |
| 181 | 3 | `public int[] seaPartners(Ports.Cargo cargo)` | Every nation a ship carrying this kind of cargo can be bound for or from, in either direction (WN7): those pick() can return for a boat's call of the kind - the kind's own tonnes in the direction, else every kind's - ... |
| 190 | 3 | `public double[] seaShares(Ports.Cargo cargo)` | Each of seaPartners(cargo)'s nations' share of the kind's tonnes as seaPartners() weighs them (either direction added, the fallback where a direction has none of the kind): the same order, so seaShares(c)[k] is seaPar... |
| 199 | 4 | `public String words(BoatSchedule.Call c)` | What a boat's label says (WN7): its class's words, then its partner by the way it goes - " · to Aland" for one going out laden with exports, " · from Aland" for one coming in with imports, " · Aland" for the boxes, wh... |
| 205 | 4 | `public String words(TrainSchedule.Call c)` | ...and a train's: its kind's words, then its partner the same way (the intermodal train, boxes, goes both ways). |
| 211 | 5 | `private String via(int partner, int direction)` | " · to X" going out, " · from X" coming in, " · X" both ways; "" for no partner or no name. |
| 218 | 3 | `public boolean byLand(int nation)` | Whether a train can reach the nation: it is the host, or its node is within LAND_KM of the city by the great circle. |
| 223 | 3 | `public double bearing(int nation)` | The compass bearing of a nation's node from the city, degrees clockwise from north in [0, 360); -1 where it is unknown or at the city's own place. |
| 228 | 3 | `public String nameOf(int nation)` | A nation's name as the snapshot has it, or null. |
| 232 | 1 | `public String toString()` |  |


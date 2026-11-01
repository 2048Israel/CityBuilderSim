# WN7 - ships and trains to their partners (0.8.96, 2026-11-01)

*The batch's record. The brief is `docs/studies/spec-nations.md` §9's WN7 row (D25: "recommend yes, last"), the last
batch of the nations line (WN0-WN7); it ships as 0.9.0 (Jerus, 2026-10-31: "with the last WN its 0.9.0"), which IR1
puts on itch.io. Lane B, beside WN5 (§9: "WN7 after WN3"), built on main 0.8.95 (67f5a05e, EP1 in). Opus wrote the
briefs, reviewed each diff and committed; two Sonnet 5.5 slices at xhigh wrote the code (M the model, U the map), one
at a time; Opus wrote the partners' cache (2) and sent the forks' rule back once (3).*

## 1. What it is

- **Each call carries its partner.** `PartnerTrade` keeps the month's tonnes by kind of cargo (`Good.cargo()`) and
  nation, each way, in its snapshot and its `State` (a save without them restores zeros: its first month has no
  partners; no format bump, nothing reads it wrongly). **`CallPartners`** (new, pure, the screen's) draws a call's
  nation from those tonnes under a hash of the call, salted apart from the arrival's and the route's hashes, so a
  month's calls, arrivals and routes are the same with partners as without: a bulker leaving is bound for a nation
  in proportion to the dry bulk it bought this month; a feeder, which carries both ways, by both; a kind with no
  partner tonnes falls back on the direction's tonnes of every kind. `BoatSchedule.Call` and `TrainSchedule.Call`
  gain `partner`.
- **An offing per bearing.** `SeaRoutes` gives every route its **exits**: from the offing, on each of the 72 compass
  bearings (`BEARING_STEP` 5), the run of open sea - the least of `abyssRun()` on the bearing and `SPREAD_DEG` either
  side, so a turned leg keeps to the sea - and 0 where it is under `EXIT_LEAST_M` (1.5 km, half the abyss). Joined
  routes share their lane's offing and its exits, worked out once. A boat with a partner rides its route to the
  offing and leaves it on the open exit nearest its partner's bearing (`exitFor()`; the great-circle bearing of the
  nation's node from the city, `TradeLanes.bearingDeg()`), turned by the call's spread about the offing, fading to
  the exit's end. The compass meets the plane in one place, `BoatSchedule.compassX/compassY` (y runs south).
- **Trains go by land**: a train's partner is drawn the same way among the host and the nodes within
  `WorldMarket.MAX_LAND_KM` (2,500 km), the market's own land reach, so a far nation never boards a train.
- **The map** (`ui/MapView`): both schedules are made with the month's `CallPartners` (the feed's snapshot, the
  Earth city's place or a generated world's stand-in, the built map's nodes, the host); anything missing gives
  `CallPartners.NONE`, which draws exactly what the map drew. **Each lane forks at its offing** toward the exits that
  a twentieth or more of its kind's partners' tonnes take (`FORK_SHARE` .05, at most `MAX_FORKS` 8 a kind), in the
  lane's band and dashes, each offing's forks once. **A boat or a train under the pointer names its partner**
  beside it, following it: "Capesize · from Brazil", "VLCC · to United States", "feeder · Germany" (boxes go both
  ways), "unit train of hoppers · to United States" (`CallPartners.words()`); no partner, the class alone.
- Nothing in the model's month reads any of it: no dollar figure is added (EP1's price level is untouched).

## 2. The partners' cache (Opus)

Slice M's frame drew each on-screen call's partner again every frame: at 10 billion the prototype's 8-district view
took 0.478 ms with partners against 0.230 without (`BOAT_FRAME_MS` 0.5), 0.51 under load. `BoatSchedule.callAt()`
now draws a call's partner and exit the first time a frame asks and keeps them in an int a call (`drawn[]`, made only
with partners), so the frames after read them: 0.056, 0.111 and 0.249 ms on an L0 screen, an L1 one and 8 districts,
against 0.051, 0.097 and 0.227 without partners (soft, beside other sessions).

## 3. The forks' rule (sent back once)

Slice U first forked a lane toward every exit any partner took: from 13 ports on the Earth's 196 nodes that is 47 to
61 of the 72 bearings, a starburst at every offing. The Earth map's lanes draw only partners over `MIN_LANE`, at most
16 strokes, so the forks now draw only the exits a twentieth of the kind's tonnes take, the eight largest. On the 13
ports (shares by 2020 GDP over distance) that is 1 to 7 forks; by GDP alone, 4 to 7. Every partner's boats still
leave on its own exit: a small partner's sail off the drawn forks, as a small partner has no lane on the Earth map.

## 4. The checks

- **`PartnerCallsCheck`** (new, 9 sections): the cargo table adds to the month's tonnes and goes through `State`;
  `pick()` draws by tonnes (3:1 within tolerance, none for a nation without the kind, the boxes both ways, the
  fallback, `NONE`); with `NONE` every call, arrival, route and position is the old overload's; exits on a mask of the
  sea keep to it, a bearing into land is closed, a boat leaves on the nearest open exit and fades to its end, `frame()`
  finds every boat on exits; trains only to land partners; the plane's compass against `EarthProj.latLonOfPlot`
  (worst 0.0003 degrees, bare and on a founded site); at 10B with 196 partners a frame within `BOAT_FRAME_MS`; the
  label's words; the forks' rule against `FORK_SHARE` and `MAX_FORKS`, a shared offing's union, and the forks' frame
  (0.001-0.003 ms) and build (31 ms at the prototype's 20,000 lanes; under a millisecond on a real map).
- `MapCheck`'s `sameRoute()` compares the exits too. MapCheck, PortCheck, SaveFileCheck, ReadPathCheck,
  FreightViewCheck, TruckCheck green on the slices; the route-finding budgets held.
- **The trace** (7): identical to main's, so no ensemble (EN1's rule).

## 5. ★ Calls (the recommended way taken in each)

1. ★ **A train's way is the world line's.** The row says "the world line's trains toward the neighbours' network":
   read as trains bound for the land partners only, on the one world line FR0's gateway gives (its bearing toward the
   nearest larger place). A fork of the world line past its far end toward each neighbour's bearing would be drawn
   past where a train has faded, off every view that draws trains, and would lay track the city never built.
   **Recommended and taken:** partners by land, the line unchanged; the label names them.
2. ★ **A partner's bearing is the great circle's**, from the city to the nation's node (the generated world's arrows
   use the same), not a real sea route: from Halifax a ship for China leaves north, as the Earth map's lane does,
   where a real one goes by Panama or Suez. **Recommended and taken:** the map's two views agree; a routed bearing
   would need the world's sea routes, which the world file does not carry.
3. ★ **The forks** (3): a twentieth of the kind's tonnes, eight at most. **Recommended and taken.**
4. ★ **The label** is white at the world lines' alpha (.45) over a dark halo, in their mono font; one constant
   (`MapView.WORLD_LINE_ALPHA`, shared) if it reads faint. **Recommended:** look at it in WN7-C first.

## 6. For HB1 and HB2 (the harbour)

- A route's **exits belong to its offing**, out past the city's radius plus 10 km, so bundling the ports into one
  harbour and berthing ships alongside the quay changes nothing of them. A new way of making routes must give them
  their exits: `SeaRoutes.Router.withExits()` (searched and joined routes) or `Route.withExits()`; a route without
  (`exits` null) sails today's abyss.
- A boat with a partner rides its route's points **unturned up to the offing** and turns only on its exit, so boats of
  one route queue on its way out to the offing; a harbour with one lane fans its boats only past it.
- `BoatSchedule.Call.partner` is what a berthed ship's card or a quay's stock (HB2) would name; `CallPartners.words()`
  the words. Nothing new here draws below the near view: the forks are the lanes' (drawn where they are), the label
  only where boats and trains are (L0, L1).

## 7. The trace

The default playtest on main (67f5a05e, built apart) and on this branch (b7a450c2), both data files named, traced
(`-Dplaytest.trace`): all eight CSVs byte-identical, the log identical but for its two run-time lines (162 s, 145 s);
every month passed the audit and every reload matched on both. So no ensemble (EN1's rule).

## 8. Not checked by eye

The forks at the offing, the boats leaving on them, and the label on a boat and a train, on an Earth city with a
port and on a generated one (WN7-C).

## 9. One in, one out

Not a process batch: no script, check machinery or session rule changed (`Timing.HARNESSES` and `AllChecks` gain
the new harness's name, as every harness does).

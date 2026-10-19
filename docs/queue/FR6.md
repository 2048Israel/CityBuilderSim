# FR6 - the railway carries the city's own bulk (D4), and the port yard (D7)

The brief for batch FR6 (2026-10-19). `QUEUE.md` links the spec; this file is
what the batch builds, with every number it uses and where each comes from.
The spec is `docs/studies/spec-freight-and-infrastructure.md`: §7's FR6 row is
the batch, §3.3 the railway's design, §3.4's last paragraph and §8's D4 and D7
the decisions. Jerus answered every decision on 2026-10-12: "all the
freighting just do rec"; D4 is FR6, "after FR2 is measured, and D7, the port
yard, rides with it". Where the spec leaves a choice open the call is marked
★ below and argued in a line.

It builds on FR2 (0.8.5: Freight, Haulage's city leg, the siding's relief),
FR7 (0.8.6: the Rail Depot, the stations and the sidings that cap what is
railed) and FR3 (0.8.7: the Ports division, the berths' sea share). FR7.md:
"FR6 runs the city's own bulk on FR7's stations and sidings." FR4 has the
screens; FR6 adds getters for them and no screen.

## 1. What the game does today (read from the source, 2026-10-19)

- **The railway is offered only what crosses the boundary.** `Freight.haul()`
  step 3 deals its capacity to the month's BULK then GOODS tonnes across the
  boundary, after the berths that go first (`Ports.planFirst()`), capped by
  the track or the sets and by the stations and sidings (`Freight.deal()`).
- **A BULK tonne bought at home is a whole truck leg** (`Haulage.legTonnes()`:
  `unitsBoughtLocal` in full), paid by its buyer at Haulage's quote. The mine's
  ore to the city's mill never sees the railway, though FR7 lays the mine's
  siding (spec §3.3: "a yard 'near the mines' never carries the mine's ore to
  the city's mill").
- **A tonne by sea relieves nothing:** quay to door is a truck
  (`legTonnes()`: the siding's relief is on the railed part only; FR2.md §1:
  "a port yard is D7, with FR6").

## 2. The rule, as built

- **The city's own bulk joins the railway's offer, after the boundary's**
  (§3.3, D4). Of a BULK tonne bought at home, `RAIL_ROAD_RELIEF` (.75) is the
  part a siding serves at both ends; that part is offered the railway once
  the boundary's bulk and goods are dealt, on what is left of the track or the
  sets and of the stations and sidings together. A BULK tonne is a unit train
  (FR7's rule): it rides a siding or a station.
- **Priced at the city leg's rate** (D4: "at the city leg's price"; §3.3:
  "priced at its quote on the city leg's own rate (Haulage's price, not the
  long-haul lorry's)"). A home tonne the railway carries is billed at the
  railway's quote in force x Haulage's quote in force, D$ a tonne, on the
  shipper's input line "Rail freight", as the long haul is. The trucks carry
  the rest of it: its leg falls by what the railway took. So the shipper's
  leg is `hq x (1 - RELIEF x h x (1 - quote))`, the boundary's own shape
  (Freight's header: `baseFreight x (1 - share x (1 - quote))`), never more
  than the trucks alone since the quote is at most 1.
- **The port yard** (D7: "rail on the quay ... the quay-to-door leg by
  rail"). When a station stands (a Rail Depot or a Rail Terminal) and a sea
  terminal stands, up to `PORT_YARD_SHARE` of the BULK tonnes that crossed a
  berth are offered the railway with the home tonnes, at the same price, and
  their truck leg falls by what it took.
- **Each leg is still paid once.** A railed home or quay tonne pays the
  railway for the part on rails and Haulage for the rest; nothing pays twice
  and every rail dollar is a shipper's input dollar and Freight's revenue.
- **The railway's own costs.** Its diesel on a city tonne is
  `CITY_LITRES_A_TONNE`, not the long haul's `FUEL_LITRES_PER_TONNE` (18 L, a
  boundary haul). Its track and sets are sunk; the city tonnes join the
  revenue its quote is struck over (step 4), so the same allowed revenue over
  more tonnes lowers the quote for every shipper.

## 3. The numbers

Sources are the spec's (its Sources list; [R#] as there). (est.) marks a
figure derived or estimated, and the line says from what.

| figure | value | source, and the choice |
|---|---|---|
| the siding's part of a home tonne | `InfrastructureManager.RAIL_ROAD_RELIEF` .75 | §3.3: "of the tonnes the railway hauls for a works, .75 go door to door by siding and the rest is drayage by truck". One number, where it is: a home tonne's quarter with no siding stays a truck leg end to end. |
| when it is dealt | after the boundary's bulk and goods | §3.3: "Dealt after the boundary's." |
| what caps it | the track or the sets left, and the stations and sidings left | FR7's `deal()`: bulk rides a siding or a station. |
| its price a tonne | the railway's quote x Haulage's quote | §3.3, D4. The railway's quote is at least `RAIL_FLOOR` (.30), so a railed tonne costs at least 30% of the truck's, as a boundary tonne costs at least 30% of the lorry's. Against the sources: rail about 3 US¢ a t-km, a truck 8-10 [R50][R51], a ratio of .30-.38, which is the floor's own range. |
| `PORT_YARD_SHARE` | .45 | the most of the quay's bulk the port yard takes. Rail takes about 45% of the Port of Montreal's traffic, on the port's own railway on the quay [R70]; trucks carry 59% of Rotterdam's containers inland (41% left to rail and barge together) [R67] and about three quarters of Long Beach's [R69]. ★3: Montreal's, because it is the one source that is rail on the quay, which is what D7 is; the others are boxes to the hinterland by on-dock rail or barge. |
| the port yard's condition | a station and a sea terminal standing | ★4. D7 says "a port and a Rail Terminal in the same district". The model does not read the map (nothing in the model reads it), so the district cannot be its test; and the spec predates FR7's Rail Depot. The stations' room caps the yard's tonnes in the deal, so a depot's yard is a small one and a terminal's a large one. |
| `CITY_LITRES_A_TONNE` | `Haulage.DIESEL_LITRES_A_TONNE` / 3 = .22 L | (est.) a truck burns .66 L a city-leg tonne (FR2.md §2); the railway costs about a third of a truck a t-km [R50][R51], and fuel is taken at the same ratio. Against `FUEL_LITRES_PER_TONNE` (18), which is a boundary haul's: charged at 18 L a city tonne would cost D$12 of diesel, three times the truck's whole leg. |
| tightness and the planner | the city tonnes are in neither | ★2. The railway's room after the boundary is what they ride; they do not make its quote scarce or order track. A city whose own bulk outgrows the room trucks the rest, as today. The planner ordering track for the city's own bulk is FR6-C's if Jerus wants it. |
| the shares in force | `homeShare`, `quayShare`, saved | the bill is struck at the shares in force while the cargo moved (Freight's order), so they cross a save as `carried` does; a save without them loads 0, a month of trucks: no format bump. |

What it comes to (est.): at Haulage's D$4.50 a tonne (FR2.md §2) and a quote
of .50, a railed home tonne pays the railway D$1.69 for its .75 and Haulage
D$1.13 for its .25: D$2.81 against D$4.50 by truck, D$1.69 a tonne saved.

## 4. ★ calls

1. **The siding's .75 is the railed part of a home tonne**, not a cap on
   top of a rail share: one number, the one FR2 uses for the boundary's
   railed tonnes, so a home and an imported railed tonne relieve the trucks
   alike.
2. **The city's own bulk fills the railway's room; it orders no track and
   makes no scarcity** (§3).
3. **`PORT_YARD_SHARE` .45, Montreal's** (§3).
4. **The port yard is a station and a berth standing**, not a building and
   not a district test (§3). A Port Rail Yard building, placed on the quay
   by the map, is the spec's picture and FR4's or later's to draw.
5. **One share for home and quay tonnes**: dealt together, pro rata, as the
   boundary's bulk is one share for the city. `quayShare` is 0 with no yard.
6. **The map's trains stay the boundary's.** `railIn`/`railOut` count
   crossing tonnes only; the city's own unit trains on the map are FR4's or
   FR5's. So FR6 draws nothing new and moves no map harness.

## 5. The slices

Each to a Sonnet 5.5 subagent at xhigh (DeepSeek's key not being set on the
PC; Jerus, 2026-10-18), reviewed by Claude as a stranger's diff and committed
by Claude. The design, the month's order, GameVersion, the trace and the
notes are Claude's.

1. **The model** (`Freight`, `Haulage`): `PORT_YARD_SHARE` and
   `CITY_LITRES_A_TONNE` on Freight; the shares in force and their save; the
   home and quay offers measured in step 1, billed there at the shares in
   force, dealt in step 3 after `deal()` by a static `dealHome()`, their diesel
   in step 2, their revenue in step 4's base; `hasPortYard()`;
   `Haulage.legTonnes()` with the home and quay shares (the old three-argument
   form kept, to the bit); `inWeight()`, `outWeight()` and `legPrice()` with
   them (the planner weighs the railway's charge on the home part too);
   getters for the month (home tonnes offered, railed, billed, the quay's).
2. **The harness** `HomeRailCheck` (§6) and its line in `AllChecks`.
3. **The real run's count**: LongPlaytest's "Home rail" line - months with a
   home share above zero, months with a port yard, the tonnes and the bill
   over the run, the last quote.

## 6. HomeRailCheck - what it has to prove

Fixtures in `GameFiles.scratch()` cities or bare sectors, each causing its
condition; every assertion against the model's constants.

1. **No railway, no change.** With no station and no spur, `dealHome()` is
   exactly 0 and `legTonnes()` with zero shares is the three-argument form's
   to the bit.
2. **Dealt after the boundary.** `dealHome()` takes at most the offer, what
   the track or sets leave after the boundary's bulk and goods, and what the
   stations and sidings leave; 0 when either is used up.
3. **Each leg once.** A home tonne at share h: its truck leg is
   1 - `RAIL_ROAD_RELIEF` x h; the railway's tonnes are `RAIL_ROAD_RELIEF` x h;
   together one tonne. A quay tonne with the yard: 1 - `PORT_YARD_SHARE` x h.
4. **The port yard.** No station, or no sea terminal: `hasPortYard()` false
   and `quayShare` 0; both standing: true.
5. **The bill.** In a played fixture city with a station, a mine selling
   ore at home to a mill: the railway bills the mill's home tonnes at its
   quote x Haulage's quote; Freight's "Rail freight" revenue is the shippers'
   lines to the cent; the mine pays nothing for what it sold at home.
6. **The diesel.** The city tonnes draw `CITY_LITRES_A_TONNE` each.
7. **The weighing.** `legPrice().in(g)` of a home-bought bulk good with a
   home share h is the truck's part plus the railway's charge, below the
   all-truck leg when the quote is below 1.
8. **The save.** The shares in force cross a save and the reload bills the
   same.
9. **A real run.** LongPlaytest's fixture city: how many months the home
   share is above zero and the port yard stands; printed, and the mechanic
   must fire.

## 7. What moves, and what is not built

- **The trace moves** (§7: "moves") from the first month a railway stands
  with room left after the boundary's tonnes: home bulk's truck legs fall,
  Freight's rail revenue rises, Haulage's trucks are fewer. Re-baselined; the
  ensemble run and read against main (FR3's final).
- **Not built here:** the planner ordering track for the city's own bulk
  (★2); a Port Rail Yard building and the map's yard on the quay (★4); the
  city's own unit trains on the map (★6); every screen of §6 (FR4).

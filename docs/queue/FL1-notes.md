# FL1 - a works making a flow good stops running at a loss ({next}, 2026-10-30)

WN3's pending item WN3-F, queued by the orchestrator on Jerus's word (2026-10-30, asked whether WN3 ships with it
known: "Ship, fix next (Recommended)"). Built on WN3 as its lane successor; WN3 (0.8.75) merged in once on main.

## 1. The cause, proved on the two fixtures

Both reds show only where **`world-1.bin` is named** beside `earth-2.bin`: without it the world market is off, the
city plays on the literals, and SlopeCheck and LandmassCheck are green (three runs, 671 orders and the same figures
each time). WN3's sessions named both files; the gate names `earth-2.bin` alone (`docs/orchestrator.md`, The PC), which
is why WN3's gate record (`docs/notes/gate.txt`) has the suite at 154 of 154 (5 item 1).

- **LandmassCheck's Rotterdam**, m961 (a January: the world re-solves): fourteen Rod Mills on imported cathode;
  cathode's import price 79.7 to 120.1, rod's net export price 95.5 to 68.8. The mills went on making 84,000 t a month
  and shipping 75,000 t of it: GDP -997,331 at m962, negative to m968, when the planner had retired seven of them.
- **SlopeCheck's Medellin** (this tip; the m834 of WN3's notes was an earlier tip's path): twenty-three mills from
  m962 shipping 153,600 t a month, rod's net export price **3.33** against cathode at 442.6, GDP -60.7M; sixteen
  negative months in 2,000. The world's band has a **slope**: a month's band is set at its top from LAST month's
  exports (`Markets.setWorldBand()`), so the city's own export sank rod's price from 550 to 7 the month after.
- **Why the rule missed it**: THE ORDER (WN3) ordered a works' inputs for what its month would make only for a
  stockable good; `buildingOrder()` read 1 for any building making a flow good, `produceFlow()` made it at the rate,
  and `shipUnsoldFlow()` shipped what the city did not take at whatever it fetched. And the sector's
  `getMarginalCostPerUnit()`, which the stockable rule judges by, splits a joint bill by sales value: in Medellin it
  read 214 a tonne of rod while the cathode in it cost 443.

## 2. The rule (`Sector`, THE ORDER, its FL1 paragraph)

A traded, exportable flow good gets an order share, as a stockable good has since WN3: what the sector's own works
need of it and what the home market demanded at the last strike are made at any price; the spare, which can only be
exported, only while it nets over what the inputs of a unit cost (`flowCostPerUnit()`), and on the world's slope only
as much as keeps **next** month's band over that cost plus `FLOW_EXPORT_HEADROOM` (2%; `GoodsMarket.netExportPriceAt()`,
the band as a function of the month's exports, from the curve `Markets.setWorldBand()` now hands each market). The
cost is the building's own traded inputs at what they cost here, less its other goods at what they net; an input the
sector makes enough of itself costs what not using it saves (the more of what it nets abroad and what its own inputs
cost a unit, to no more than buying it). A by-product comes out of its run: a flow good is made at the run share of
the buildings that make it (the smelter's anode out of the concentrate it bought for its acid). A month where nothing
is cut has share exactly 1 and is WN3's to the bit (Rotterdam: identical to month 504, the first cut at 505); with no
world file there is no slope and the rule is the stockable good's export bound.

**How often it fires** (both files named; the session replayed the two harnesses' runs and the playtest's founding
month by month, on WN3's classes and FL1's, a cut read as `idled` on an exportable flow good):

| run | negative-GDP months, WN3 | FL1 | months a flow order was cut (good: months) |
|---|---|---|---|
| Rotterdam, 1,200 | 7 (m962-m968) | 0 | rod 119 (from m505), wire and cable 34 |
| Medellin, 2,000 | 16 (from m962) | 0 | rod 122, wire and cable 91, fabricated steel 53, pig iron 13 |
| the playtest's founding, 2,000 | 0 | 0 | wire and cable 83, rod 37, pig iron 36 |

Population at the end: Rotterdam 53,511 (54,842), Medellin 171,735 (178,014), the founding 199,742 (179,055).

## 3. The calls (★, each taken as recommended, BP1)

1. ★ **Only the exported spare is gated.** The own works' need and the home market's demand are made whatever they
   fetch, as the stockable good's plan for home is; the city would otherwise import them at the ceiling, and GDP gains
   on them. Alternative: price the home part too (on the band's strike it would swing). **Taken: as built.**
2. ★ **The cost is the building's inputs**, not `getMarginalCostPerUnit()`: power, water and consumables are billed
   whatever the order (`getElectricityCost()`, `buyConsumables()`), so the inputs are all an order saves; a joint
   bill split by sales value misprices one line of a chain. **Taken.**
3. ★ **The slope's volume, with a 2% headroom.** Exporting the whole spare whenever this month's price pays sinks
   next month's under the cost, so the line would run every other month. The spare exported is the most whose next
   band still nets the cost plus 2%; this month's price must clear the cost too. **Taken.**
4. ★ **A captive input's own supply is its nameplate at the rate**, which a save holds; last month's lift off the
   output rows would be truer for a scrap yard short of old steel, but a save does not keep the rows, and a reloaded
   city would cost it otherwise and play another month (the standing rule: save it, or do not read it). **Taken.**
5. ★ **A flow good the world will not buy keeps share 1**: made at the rate and its unsold lost, as before. Cutting it
   to last month's demand could leave a buyer short where the good cannot be imported either. **Left** (FL1-N).

## 4. The checks

- **SlopeCheck and LandmassCheck green**, premises unmoved, both files named and with the Earth file alone.
- **CopperRefiningCheck 9** (new, (a)-(h)): the rod mill on imported cathode orders none and ships none; with a wire
  works the home demand is made and its cathode ordered; a rod that pays is unchanged to the bit; on a straight-line
  slope the export is the most at the headroom; the chain keeps the refinery's anode; (g) the smelter runs for its
  acid and makes anode and acid in the template's ratios; (h) a rod mill on its own refinery's cathode is not cut,
  first month and third; (f) Rotterdam 999 months: rod cut 104 months from m505, no negative GDP, every curve
  reproduces its band to the bit. Sections 3 and 4 as before FL1.
- **Four fixtures cause their conditions again** (no assertion moved): PortCheck 6 and MaterialsCheck 10 (two Arc
  Furnaces on imported scrap: steel nets 0.205 abroad against 0.264 of scrap), HomeRailCheck's rail city and
  TruckRouteCheck 9 (HaulageCheck's Pelletising Plant: pellets 0.057 against 0.066) play on the literals with their
  product's world prices doubled - their premise is works that ship, and on imported feed those exports lose.
- **The full suite, as the gate runs it** (`earth-2.bin` named, on 5c0e320b): 154 harnesses, every red main's
  recorded ones aside, once TradeBandCheck 6 was mended back (its city is `HomeRailCheck.railed()`'s, which this
  batch had first mended for every caller: a city on the literals is not saved so, and the reload read another band;
  now only HomeRailCheck's own rail city plays its pellets paying, and TradeBandCheck, FreightViewCheck and
  HomeRailCheck are green both ways). After main 0.8.76 (HD2) was merged, `--pick`: **exit 0**, 43 harnesses with
  LongPlaytest in `Sector`'s place.
- **With both files named**, on the merged tip (a8190fb1): SlopeCheck, LandmassCheck, CopperRefiningCheck, the four
  mended harnesses, TradeBandCheck and LongPlaytest (543,040 at m4000, nothing found) green; **one NEW red**, reached on
  FL1's path and not shown at the gate (5 item 2): SaveFileCheck "...and the bank's equity with it in" FAIL
  64,614.915170 != 64,586.700448 and "...its three parts still adding up" FAIL 50,662.214296 != 50,690.429019.
- **The ensemble** (both files, as WN3's and HD2's; `docs/ensemble/FL1.md`, on the merged tip, against 0.8.76):
  **23 of 26** (0.8.76: 23 of 25, one vacuous). AUTO2 median 317,518 (280,956), T1 16 of 16 at m1,104 (m956-m1,145),
  F1 0 of 80, M1 0. A2 and S1 fail as on 0.8.76; R3 passes (2 seeds over +1 point, median -0.95); **R2 fails**: the
  15% rate's unemployment over its twin, median +0.32 points against +0.50 (0.8.76 +2.86; FL1 on WN3, before HD2,
  +1.35 and passing), seeds -1.84 to +8.46 (FL1-E). On WN3 (0.8.75) FL1 scored 23 of 26 as WN3 did, R3 red in 5 seeds.

## 5. Found on the way

1. **The gate does not name `world-1.bin`.** `docs/orchestrator.md` names `earth-2.bin` alone, so WN3's world market
   has never run at a gate: its two reds and FL1's fix are visible only in a batch session that names both. For the
   orchestrator: name both files (★ in the report).
2. **SaveFileCheck's bank after a load** (both files named only): the fund city's bank reads 64,620.42 of equity just
   after its load against 64,592.31 live, no month played, its paid-in, retained and preferred unchanged (the split's
   residual off by the same 28.10). Green on slice A's model, red on slice B's (the by-product's run share and the
   captive input's cost move this city's path); FL1's own state (the shares, the cut, the curve) is cleared each month
   and is not read at a load. Not traced: which of the bank's assets (cash reserves, net loans, securities) is rebuilt
   from something the save does not keep (FL1-R).
3. **The operating rate moves inside a month**: the vans that arrive between `produceFlow()` and the clearing move
   it by about 0.3%, so a flow works buys its inputs at the clearing's rate for output made at the earlier one
   (CopperRefiningCheck 9 (b) compares with a 2% `close()` there). As before FL1.
4. **A stockable joint product** is made at its own share while its building orders at the greatest
   (`produceStock()`, WN3's): a sawmill whose lumber is cut and whose residues are not buys logs for lumber it does not
   make. Before FL1 every building making a flow good ordered at 1, so FL1 narrows it; left.
5. **On the literals an Arc Furnace on imported scrap and a Pelletising Plant on imported ore lose on what they
   export** (steel nets 0.205 against 0.264 of scrap). The rule now stops those exports in every city; the planner's
   estimate (`SpreadPlanner`) already weighs them at those prices.
6. **LongPlaytest met WN3-M on FL1's path before HD2** (both files, on 5c0e320b): "an old works moved", m4000,
   district (0,0), Rod Mills (4 of the 8 standing at m1001 not where they stood) - the map fault WN3's notes 5 item 1
   (2) found on Historical, ground bought inside a built district moving old works. On the merged tip the path finds
   nothing; the fault is latent, and WN3-M holds it.

## 6. One in, one out

Not a process batch: no script, check machinery or session rule changed.

## 7. How it was built

Two Sonnet 5.5 slices at xhigh, reviewed: A (the rule, the slope, section 9 (a)-(f)), B (the by-product, the captive
input, 9 (g)-(h); it stalled before its runs and the four fixture mends, which this session finished and checked). The
save fix in `inputCost()` and the notes are this session's.

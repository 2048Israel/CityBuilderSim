# CPB2 — no feed bought for undemanded output that cannot cover it

Base: origin/main 8ada323e3 (0.9.164); main's FB14, 5c8e6c937 (0.9.165), is
merged in c5dc51182 (no conflict). Built by CPB2 (Claude, Opus) on
`batch/CPB2`. Source changed: `Sector.java` (the model) and the new
`UndemandedFeedCheck.java`, with its `AllChecks` line and `docs/tree.md`
entry. No save field, balance figure, dial or `SAVE_FORMAT` change.

## The findings, on main

The methods were re-found with `Where` on this base: `Sector.getPlannedOutput`
(the CPB1 buffer gate), `Sector.getExportBoundOutput`,
`Sector.getMarginalCostPerUnit`, `Sector.costShareOf`, `Sector.flowCostPerUnit`,
`Sector.forecastOrder` and `GoodsMarket.netExportPrice`. Overrides that reach
them: `Refining` (marginal cost of power and water only; it never orders for
what it makes), `GoldMining` (bullion to the mint under the gold regime) and
`Materials` (MATERIALS fills its room); none needed a change.

1. **The export spare priced on the allocated cost** (REV-CPB1-1's first).
   `getPlannedOutput` refuses an undemanded buffer whose net export price is
   under `flowCostPerUnit`, but `getExportBoundOutput` then sees the whole
   nameplate as spare and ships it if the price clears
   `getMarginalCostPerUnit`, the joint bill split by `costShareOf`'s sales
   value. Beside a rod mill whose rod sells over cathode, cathode's share of
   the joint bill falls to 0.517. `UndemandedFeedCheck` section 1 on main's
   `Sector`: allocated marginal 5.719 <= net export 9 < anode a cathode
   11.057; order share 1, anode bid and import 9,750 t where the rod mill's
   cathode takes 8,057.3; 1,684 t of cathode exported at 9; GDP −10,004.0
   against −6,540.5 mended (the rest is the demanded rod, kept).
2. **NaN authorised the buffer** (REV-CPB1-1's second). VANS, TRUCKS,
   ROLLING_STOCK and LUXURIES have no world export price, so
   `netExportPrice()` is NaN and `NaN < cost` is false: the gate never
   refused. Section 4 on main: the Commercial Vehicle Plant plans 150 vans
   with no demand, bids 2,040 units of feed and imports 31.26 of machinery
   and lubricants; the Locomotive Works plans 4 sets (27.75 imported), the
   Heavy Truck Plant 6 trucks (4.17). LUXURIES has no plant or sector making
   it: no path, only the price.
3. **Found while building: committed recovered copper beside a rod mill.**
   `buildingOrder` runs the Scrap Refinery at its committed floor whatever the
   order share, but `forecastOrder` divided the month's cathode by the whole
   nameplate, as if every refinery ran at that share. With 4,000 t of cathode
   from committed scrap and a rod mill needing 8,016, the Electrolytic
   Refinery still ordered anode for 5,675.6 t; the 1,659.6 t past the rod
   mill shipped at 9 against 11.06 of anode a unit (a probe on the model with
   mends 1 and 2, `target/probe/src/ham/citybuildersim/Cpb2Probe3.java`). On
   main's whole `Sector` the same fixture buys 9,750 t of anode and ships
   5,684 t (section 3). ★ Mended here rather than queued: the brief requires
   that imported feed is not bought for undemanded output that cannot cover
   its inputs, and a CPB3 would owe a second ensemble.

## What was built

- ★ **The ordered spare covers its recipe.** While the month's feed is ordered
  (`deciding`, inside `planOrders`), `getExportBoundOutput` returns nothing
  unless the net export price also covers `flowCostPerUnit`, the buffer
  gate's cost, as well as the allocated marginal cost. Recommended over
  pricing the spare on `flowCostPerUnit` alone, which would ship more where
  power and water are not covered: this only ever cuts. Outside the order
  pass the feed is in hand (committed, or bought for a forecast that ran
  high), so the line still ships at the marginal cost: sunk feed is not held
  back into the shed, and `Refining`, which never orders for what it makes,
  is untouched.
- ★ **What a buffer with no world outlet can sell for: nothing.**
  `Sector.bufferSalePrice` is the net export price where it is a number and 0
  where the world will not buy; the CPB1 gate compares that with
  `flowCostPerUnit`. Recommended over the local price, which with no demand at
  the last strike is a price nobody paid, and over the import price, a bet on
  demand the market has not shown. A city that asks for vans is served by the
  demand branch at `STOCK_MONTHS` of cover from the next strike, the one-month
  lag every plan off the last strike has. A good with no traded feed (cost 0)
  still fills its buffer: no feed is bought for it.
- **The order share over the committed floors.** `forecastOrder` returns
  `shareAboveFloors`: the share at which the buildings that make the good,
  each at the greater of the share and its committed floor (the floor now
  `committedFloor`, lifted out of `buildingOrder` unchanged), make what the
  month needs. Exactly the old `(own + made) / nameplate` wherever no building
  has a floor, so only Copper Refining's Scrap Refinery with recovered copper
  in the pool can move.

## The harness

`UndemandedFeedCheck` (registered in `AllChecks`): fixtures from
`GameFiles.scratch`, real buildings by `addStack`, the real
`Markets.clearMonth`, then `EconomyManager.strikeSectors` and
`updateNationalAccounts`; assertions against the model's own figures
(`flowCostPerUnit`, `getMarginalCostPerUnit`, `captiveUnits`, the templates'
recipes, `STOCK_MONTHS`, `DUMP_THRESHOLD`).

1. Cathode beside a demanded rod mill, undemanded, allocated marginal <= net
   export < anode: the order is the rod mill's cathode alone, the anode bid
   and purchase are its cathode at the recipe ratio, nothing ships, the
   refinery's export takings are the rod's, GDP = C + I + G + NX.
2. Kept: a shed at the dump line with cathode over its anode orders its spare
   whole and makes the run to the bit; committed recovered copper beside an
   idle rod mill (rod under its cathode abroad, but still halving cathode's
   share) is all taken, made at the scrap ratio with no anode or scrap
   imported, and ships at the marginal cost rather than filling the shed.
3. Committed recovered copper beside a demanded rod mill: anode bid and
   purchase are the rod mill's cathode less the scrap's, the run makes the
   rod mill's cathode exactly and nothing ships.
4. Every stockable traded good the world will not buy (a loop over `Good`,
   so a new one is tried), each of its real plants alone, undemanded: no
   plan, order share 0, no feed bid, nothing bought or made, the struck
   sector's imports only its consumables and its fleet's diesel, the
   identity; three plants tried, LUXURIES' want of a maker asserted.
5. Kept: vans demanded at the last strike plan and make their cover and bid
   machinery; profitable cars beside the Locomotive Works order and make
   their whole buffer while the works orders nothing; vans already in the
   shed all sell to the fleets (a rod mill's fleet bidding 8.33), none made.

Each new assertion fails with its mend removed and its premise kept: the
harness run against shadow `Sector` classes under `target/cpb2-mut/<v>/`
(each `fixture.log`), never a model source edit. No premise fails in any.

| variant | what is removed | fails |
|---|---|---:|
| main | main's whole `Sector` | 22 |
| no-floor | the order-pass floor in `getExportBoundOutput` | 9 (sections 1, 2's committed pair, 3) |
| no-nan | `bufferSalePrice` returns the raw price | 13 (section 4, 5's idle works and shed) |
| floor-always | the floor applied outside the order pass too | 1 (2's sunk feed ships) |
| no-floors | `forecastOrder` back to `(own + made) / nameplate` | 2 (section 3) |

On main the vans in the shed do not sell: main's phantom buffer of 150 enters
the strike as supply, the price falls under the maker's marginal cost and
the shed withholds; that assertion therefore fails on main and on no-nan.
The other kept assertions (2's profitable spare, 5's demand and cars) pass
on every variant.

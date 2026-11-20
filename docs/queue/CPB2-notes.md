# CPB2 — no feed bought for undemanded output that cannot cover it

Base: origin/main 8ada323e3 (0.9.164); main's FB14, 5c8e6c937 (0.9.165), is
merged in c5dc51182 (no conflict), and 0.9.166, 2677b1580 (SB1, FS2,
R2e-PE4), in 2f7b25ca0 (only the generated indexes met; main's taken and
regenerated), 0.9.167, bf048eb69 (HS2), in 66e9dc33f (the same), GB3 and GB4
(64ec45fbe) in 76dc564f2, the ensemble's commit, and 0.9.168 to 0.9.169
(c60165591) in d91592be8 after it: Bank's integrity getters, CityRuns and
Observation, nothing the month runs, so the ensemble stands. None of
the merges touched `Sector` or the order pass. Built by CPB2 (Claude, Opus) on
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
  in the pool can move; a sector with no committed feed that month returns
  the old share before the stack is scanned, so the solve costs the others
  nothing.

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

## The default trace

The trace moves beyond runtime, so the ensemble is owed (`docs/ensemble/CPB2.md`,
against `docs/ensemble/0.9.167.md`, the newest, on the model with HS2 merged). I measured it by counting, not
by diffing. Main's retained trace, `cbs-trace-main-24b7bfcbc`, is 101 commits
old and differs from month 2 on, so a line-by-line comparison with it says
nothing. Instead I played the default playtest (2,400 months) on a shadow
`Sector` (`target/cpb2-mut/count`) that runs main's rule and counts every call
where the mended rule would have answered differently. It exited 0 in 1,543 s
and its trace is under `target/cpb2-trace/`.

| path | sector, good | calls changed | months |
|---|---|---:|---|
| 1, the ordered spare refused | Copper Refining, cathode | 23 | 3-25 |
| 2, no world outlet, order pass | Automotive, trucks | 87 | 1,139-2,118 |
| 2, no world outlet, production | Automotive, trucks | 174 | 1,139-2,118 |

The count was taken before mend 3, so mend 3 has no figure here. It can only
move where a Scrap Refinery holds committed recovered copper. The ensemble runs
on the final model, so it covers all three. Seed 1 on the final model (section
11 of CopperRefiningCheck, below) plays its 2,400 months with no new finding.

## The checks

- `./batch-check.sh --fast` green on the merged branch (3312d2218, and again
  after the HS2 merge, 66e9dc33f, and the 0.9.169 one, d91592be8): compile,
  maps regenerated, Stale 0 firm, tree clean.
- `UndemandedFeedCheck` green on each merged model. The mutation table above
  was run before the merges. Since then the only model change is the solve's
  early exit, which gives the same answer wherever it applies (with no
  committed feed every floor is 0, and the solve already returned the old
  share).
- The named run before the 0.9.166 merge (`-Dcbs.parallel=1`, 2,285 s on the
  shared laptop) was green apart from two reds. These harnesses passed:
  CopperBufferCheck, BooksCheck, CementConcreteCheck, FreightViewCheck,
  GdpCheck, GoldMiningCheck, ManufacturingCheck, MaterialsCheck, RailCheck,
  RefineryCheck, SectorFlowCheck, SectorStatementCheck, TradeBandCheck,
  VanCheck and UndemandedFeedCheck. The two reds:
  - CarCheck: the ten-year town at 0.210 against 0.215 cars a household. This
    is main's known red, FS7's row, from BO1-S's moved path. Rerun alone on
    the merged branch it exits 3, main's known red only (76 s).
  - CopperRefiningCheck: "timed out after 1740 s" in section 11. The child
    playing seed 1 was still running when AllChecks' `HARNESS_SECONDS` (4 x
    435) ended it, with sections 1-10 already passed. Run again alone on the
    final model (`target/cpb2-crc.log`), every section passed and it exited 0
    in 1,807 s; the child alone took about 27 minutes on the shared laptop. So
    the cause is load, not a hang. It is in `AllChecks.NIGHTLY`, and
    `sh tools/pick.sh --nightly origin/main HEAD` picks nothing, so the gate
    does not run it; speed is judged nightly on the gate box.
- `sh tools/pick.sh origin/main HEAD`: ALL (230 harnesses), since `Sector` is
  in the month's flow, so a dry run is owed. Review: not needed, because the
  diff has no money, saves or threading.
- `sh tools/known-reds.sh CPB2 docs/queue/CPB2-ship.md`: exit 0. The ship
  file adds no known-reds row.

## Left for Jerus

- ★ The two calls above: the ordered spare must also cover its recipe, and a
  buffer with no world outlet is worth nothing. The second means a city
  starts making vans, trucks or rolling stock for its shed only after demand
  shows at a strike: the one-month lag every plan has.

## The ensemble

The 80-run ensemble ran on the final model (76dc564f2, with 0.9.167 and GB3
merged) on the gate box: label CPB2, against `docs/ensemble/0.9.167.md`, with
the baseline's `-Densemble.extra` and the box's own data files. It is committed
as `docs/ensemble/CPB2.md`. Batch verdict PASS: the hard criteria passed 10 of
10. M1 (the money audit) failed in 0 months and F1 found 0 LongPlaytest
findings in every policy. 25 of 26 criteria passed. The one red, AUTO2's A2
(the longest spell of yearly inflation over 10% from month 240, info), is red
on the baseline too: 17 months [7 to 33] here against 20 [7 to 33].

| policy | final population, median [min to max] | baseline |
|---|---|---|
| AUTO2 | 2,109,814 [76,002 to 3,947,440] | 1,820,652 [76,002 to 4,402,721] |
| ZERO_SLACK | 1,379,164 [35,917 to 2,626,820] | 1,308,226 [35,917 to 2,776,509] |
| SHOCK | 40,233 [10,082 to 124,922] | 39,590 [10,082 to 124,922] |
| ZERO_HOT | 6,532 [3,054 to 14,077] | the same |
| HIGH | 30,433 [6,064 to 36,871] | the same |

Two verdicts changed, both info and both toward green: the final population T2
for AUTO2 and for ZERO_SLACK, FAIL to PASS. T1 is unchanged, with 16 of 16
AUTO2 seeds reaching 40,000 people at month 919 [637 to 1,732]. ZERO_HOT and
HIGH match the baseline's final population exactly in median, minimum and
maximum.

## The dry run

`sh tools/gate.sh CPB2 --dry-run` on 05403b9c0, with main at c60165591
(0.9.169), on the gate box: kept as `/home/jerus/cbs/runs/cbs-gate-CPB2-SdfZd3lQ`
(copied home to `%TEMP%/cbs-gate-CPB2-SdfZd3lQ`). Gate exit 0. The pick was
ALL, 231 harnesses. The suite finished in 631 s, exit 3: the reds were
main's known reds only, in 11 harnesses (BankCheck BasketYearCheck BuildAdviceCheck CentralBankCheck ExchangeRateCheck Forms1900Check FundCheck HoldersCheck PassThroughCheck RoadCheck YoungBankCheck). `unnamed.txt` is empty.
UndemandedFeedCheck passed. The dry run kept nothing and left the tree as it
was.

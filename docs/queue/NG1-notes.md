# NG1 — imported inputs the ground does not give are not bought

Base: origin/main da94b6973 (0.9.170). Main's MC1 and FS16 (b7a9edfc9,
0.9.171) are merged in 9b79bec5e, and FS10 (1d8a1f243) in b204c5bc1. In both
merges only the generated indexes met: main's were taken and regenerated, and
the files whose only change was the date line were left as main has them. No
merge touched the six model files. Built by NG1 (Claude, Opus) on `batch/NG1`. Source changed:
`Sector.java`, `LandManager.java`, `sectors/ExtractionSector.java`,
`sectors/CoalMining.java`, `sectors/CopperMining.java` and
`sectors/GoldMining.java` (the model), plus the new `GroundInputCheck.java`
with its `AllChecks` line and `docs/tree.md` entry. No save field, balance
figure, dial or `SAVE_FORMAT` change.

## The finding HL1 named (seed 12, m902)

HL1's row: the works imported 2.6 times a month's inputs in the month their
exports went. I replayed AUTO2 seed 12 on HL1's own commit, fe1a0930, with the
ensemble's driver (`target/probe/hl1/`). It reproduces to the cent:
m902's GDP is −263.06.

The cause is GDP1's class, closed in 0.9.97, and none of CPB2's three. In that
January the world re-solve raised rod's import price from 3.03 to 4.04, and
the city's rod mill cut its run. The Wire & Cable Works was still ordering on
the stale price, so it imported 2,651 t of rod and exported the wire at 3.45
against 4.04. Those inputs were used, not left unused. Since 0.9.97 a works
forecasts its inputs on the current band, and `GdpCheck`'s `forecastPrices`
and `delayedExports` sections hold that month.

On 0.9.170 (da94b6973) seed 12 no longer reaches that month: it is another
city. m902's GDP is 112,983.75 with 118,915 people, and there is no negative
month in 2,400 (554 s on the laptop, `target/probe/main2-AUTO2-12.csv`).

## The class still live on main

Searching for a works that buys inputs it does not use found one class: the
ground. A works the ground limits ordered its recipe inputs for its whole
plant at its order share, while the ground gave it less.

- The Iron Mine (a flow good): `produceFlow` digs the ask through
  `ExtractionSector.groundLimit`, ahead of the market, but
  `Sector.inputToMake` still asked the diesel of the whole ask.
- A surface coal mine's diesel, a leach plant's acid, and a gold mill's acid
  and diesel (stockable goods): the order share is a share of the lift
  (`forecastOrder` over `madeAtRate`, the ground-limited lift). An empty lift
  reads share 1, so a mine on a worked-out seam bought a whole month's inputs
  and made nothing, and one on a short seam bought its plant's.

What nothing made was bought (mostly abroad) and became nothing: no output, no
stock. GDP fell by its value with nothing on the other side.

## What was built

- `Sector.groundShare(t)`, a new hook: the share of a building's run its ground
  gives it this month. It is 1 by default. `inputToMake` multiplies each
  building by it (`uses × buildingOrder × groundShare`), so where every share
  is 1 the order is the same to the bit (x × 1.0 = x).
- `ExtractionSector`: `groundLimit` keeps what each kind of dig got of its ask
  (`dugShares`, emptied in `planOrders`, never saved), and `groundShare` reads
  it. It returns 1 before the dig, for a kind not dug, and for a building that
  makes no ground good. `LandManager.extract` returns the amount asked, whole,
  when the ground holds it, so the share is exactly 1 there. `digs()` and the
  shares use one key, `kind(type, dig)`.
- `CoalMining.groundShare`, beside `dug()`: the building's kind of dig over
  its ask at the rate. `CopperMining.groundShare`, beside `leached()`: the
  same for the leach plants. A Copper Mine & Mill reads its concentrate's dig
  share (REV-NG1's note 1). `CopperMining.groundLimit` is its own copy of the
  dig loop, so `ExtractionSector.noteDug` keeps what each kind got for both
  loops, and `dugShare(t)` reads it.
- `GoldMining.groundShare`: `LandManager.goldOreShare(throughput)`, the ore
  the fields still hold over the month's throughput, read off `goldOutput`'s
  own walk (`goldWalk`, split out of it). `goldOutput` is unchanged to the bit.
  The share is exactly 1 on a falling grade, where the same ore makes fewer
  ounces and its acid and diesel are used, and 0 once the fields are out.
- ★ The order uses a product, not a minimum. Coal's and copper's order shares
  are already shares of their ground-limited lift, and iron's dig share is
  taken against the ask it was ordered for, so share × ground share is what
  the run makes in each case.
- ★ Not bought rather than booked as stock. The row asked for "imported inputs
  not used, booked as stock built". Here they are not bought at all: acid is a
  flow good that nothing holds, and diesel kept by a worked-out mine would
  never be burned. So no stock is booked that would only be written off later,
  and GDP's imports carry only inputs that output was made with.
- Left out on purpose: consumables and the fleet's diesel. They are the
  plant's standing bills at its rate, as an idle plant's are (THE ORDER leaves
  them out for the same reason).
- Not given a share: the other limits on a run. Oil's wells (`Oil.groundLimit`,
  the two pools), the scrap yards (`HeavyIndustry`, the old steel), the
  quarries (`Quarrying.groundLimit`) and the logging camps use no traded
  recipe input in `buildings.json`, so nothing is ordered past what they make.
  The copper mills are in the same position, but their share is kept anyway,
  as the review asked.

## Bought and not used, after the mend (the orchestrator's (b))

GDP is measured by expenditure only (`NationalAccounts`, C + I + G + NX), so
its identity holds by construction both before the mend and after it. What the
mend changes is that M no longer carries inputs that no output was made with.
The paths:

- **The month's run.** The bid is `inputToMake × rate` and the run is the
  order share at the same rate (THE ORDER: a flow good's `asked` is
  `full × flowRunShare`, a stockable good's made is the forecast the share was
  struck on). They now agree for a ground-limited works too.
- **Staffing.** The operating rate is set before the market pass. The bid and
  the run both use it, so a staffing hold cuts both.
- **A stop.** A works short of cash buys less through `purchasesLeft` (0.7.12)
  but still runs. That is the other way round, inputs fewer than the run used,
  and it is not this row.
- **The month's end.** Inputs a works holds over are its pantry (vans, rolling
  stock, trucks, luxuries, the shops' food) or its stock (a refinery's crude).
  Every one of them is in `NationalAccounts.HELD` or in the food and luxuries
  terms, so their change is booked in investment. `GdpCheck` asserts that
  every good a sector declares it holds is in one of the five terms.
  `GroundInputCheck` section 6 causes it: the vans a second mine's fleet buys
  and has not worn are in HELD as the accounts count them.

## The harness

`GroundInputCheck`, 34 lines in six sections, built on the scratch towns of
`CoalMiningCheck`, `CopperMiningCheck` and `GoldMiningCheck`, plus its own
iron town. The fixtures use `GameFiles.scratch`.

1. Iron Mine on a 2,000 t field: asked 20,000 t, dug 2,000 t, run diesel
   6,000 L (the ask's would be 60,000). Worked out: no run diesel, and the
   struck statement's diesel from abroad is the fleet's. The accounts add up
   in both months.
2. Surface coal mine with 30,000 t of thermal seam left: lift 28,500 t,
   diesel bid 65,550 L (the plant's would be 230,000). Seam out: no bid and
   nothing cleared.
3. Leach plant on 140 t of oxide: lift 100 t, acid bid 350 t (the plant's
   would be 3,500). Oxide out: none. A Copper Mine & Mill on 300 t of
   sulphide field: asked 2,200 t of concentrate, dug 879.8 t, ground share
   0.3999. With 9b79bec5e's two copper classes (before the review's note) it
   reads 1, and that line alone fails.
4. Gold mill: on a half-worked field (5,771 oz against 9,618 rich) it still
   bids its whole throughput's acid and diesel. Worked out: no bid and nothing
   cleared.
5. Kept: on ample ground each of the four bids its recipe × share × rate, to
   the bit (`==`).
6. Held over: the vans bought for a second mine are in the pantry at the
   month's end (0.424 + 0.106 − wear = 0.526), and HELD's vans equal every
   sector's.

On main's own classes (1d8a1f243, built clean from `git archive` in
`target/ng1-mainsrc`, with only `GroundInputCheck` added) it exits 1 with 10
fails, and they are exactly the 10 mend lines: iron short, iron worked out,
iron's struck imports (1.07 from abroad against the fleet's 0.00), coal short,
coal out, leach short, leach out, the mill's share (NaN: main has no
`groundShare`), and gold's bid and clearing worked out. Every premise, kept
and accounts line passes on both.

## The replays and the default trace

The proof the orchestrator asked for, the usual way: the default playtest,
2,400 months with `-Dplaytest.trace`, one JVM each. One run was on main
1d8a1f243 (`target/trace-main/`) and one on the branch merged with it,
3333706c6 (`target/trace-ng1/`). All eight CSVs are byte-identical (`cmp`):

| file | bytes |
|---|---:|
| t-bank.csv | 889,614 |
| t-bonds.csv | 5,256,526 |
| t-borrow.csv | 2,441 |
| t-build.csv | 669,616 |
| t-house.csv | 840,931 |
| t-labour.csv | 768,420 |
| t-month.csv | 936,352 |
| t-pop.csv | 434,715 |

Both runs exit 0 with "Nothing. Every month passed the audit and every reload
matched": no new F1 finding.

Before that, two plays on 9b79bec5e each carried a counting overlay
(`target/ng1-count/`). The overlay is the mended `Sector` plus a probe that
records each month, sector and good where a ground share fell under 1, with
the units it withheld. Its positive control, `GroundInputCheck` on the same
classpath, records all five: iron, coal and gold diesel, and leach and gold
acid.

| play | months | LongPlaytest findings (F1) | NG1 firings |
|---|---:|---|---:|
| AUTO2 seed 12, HL1's, through the ensemble's driver | 2,400 (1,460 s) | none; no negative GDP month | 0 |
| the default playtest, `LongPlaytest` | 2,400 | none: "Every month passed the audit and every reload matched" | 0 |

So in both plays NG1's trace is main's to the bit: every share is exactly 1,
and x × 1.0 = x. On the merged model, seed 12's m902 has GDP 23,931.10 with
38,101 people, and the run ends at 2,909,195 people. Its trace first differs
from 0.9.170's at m424. That divergence is MC1's and FS16's, merged before the
play, because NG1 fired in no month. The ensemble can only move where another
seed's ground runs short.

## The checks

- `./batch-check.sh --fast` green on 9da3fd87b, and again on each merge
  (9b79bec5e and 3333706c6): compile, maps regenerated, Stale 0 firm, tree
  clean but the generated docs.
- Named harnesses, one per call, each exit 0 on 9da3fd87b: GroundInputCheck,
  GdpCheck, CoalMiningCheck, CopperMiningCheck, SulphideLeachCheck,
  GoldMiningCheck, MiningCheck (4 harnesses by name), LandCheck,
  UndemandedFeedCheck, ForestryCheck, QuarryingCheck, HistoricalRecipeCheck.
  The last three read `ExtractionSector` (Forestry and Quarrying extend it,
  and HistoricalRecipeCheck calls `groundLimit` reflectively). After the
  copper note (301969df5), CopperMiningCheck, SulphideLeachCheck and
  GroundInputCheck passed again. GroundInputCheck also passed on 3333706c6,
  the branch merged with 1d8a1f243.
- REV-NG1 on 9b79bec5e: SHIP, no P1. Its note 1 is mended in 301969df5 (the
  mills' share, above). Its note 2 asked for the notes, which are here.
- `sh tools/pick.sh origin/main HEAD` on 3333706c6: ALL (233 harnesses),
  because `Sector` is in the month's flow and `AllChecks` changed, so a dry
  run is owed. The pick's review line says not needed, but the brief names
  it: review needed - money (GDP's imports and the inputs a works buys).


## Left for Jerus

- ★ The two calls above. Unused ground-limited inputs are not bought rather
  than held as stock. The order uses a product of the shares.
- Seen and not mended (not this row): a works short of cash
  (`purchasesLeft`) buys fewer inputs than its run uses. It is the other way
  round from this row, and could be a row of its own.

## The ensemble

None. Contract 4 runs the ensemble only when the default trace moves beyond
runtime, and here the eight trace CSVs are byte-identical to main's (above),
so the orchestrator called it off (2026-11-20, after the count). The class
fired in no month of the default playtest or of AUTO2 seed 12, so the 80 runs
could move only where another seed's ground runs short, and there only by not
buying inputs that nothing used.


## The dry run

`sh tools/gate.sh NG1 --dry-run` ran on the gate box on bfc08e647, with main at
1d8a1f243 (FS10). The run is kept as `/home/jerus/cbs/runs/cbs-gate-NG1-JaKT00P2`
and copied home to `%TEMP%/cbs-gate-NG1-JaKT00P2`. Gate exit 0. The pick was
ALL, 233 harnesses. The suite finished in 1,032 s with exit 3, meaning main's
known reds only, in 8 harnesses: BankCheck, BasketYearCheck, BuildAdviceCheck,
CopperRefiningCheck, Forms1900Check, HoldersCheck, RoadCheck and
YoungBankCheck. GroundInputCheck passed. The dry run kept nothing and left the
tree as it was.


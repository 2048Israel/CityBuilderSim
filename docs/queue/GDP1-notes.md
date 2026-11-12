# GDP1 - builder's notes

## Brief and workflow

The brief is [GDP1.md](GDP1.md), recording Jerus's words of 2026-11-12.
This branch starts at main, independently of RT3. WC1's settled instruction
that Codex builds whole batches governs this session. No save slot is written.
The initial known-reds.txt contains no GDP1 entry; any entry added before the
gate is to be judged without its cover as required by the brief.

## Investigation

The proposed timing diagnosis is disproved by the replay. At RT3's m902 the
struck Copper Refining statement's imports **26,713.959** accompany a local
rod sale **27,566.151**, not unsold output. The preceding m901 pending ledger,
which that strike banks, shows **4,626.188478 t** of rod made and the same
quantity sold locally, zero exported. Its cathode purchase is **4,635.440855 t**,
the Rod Mill recipe's 8,016 / 8,000 ratio. No rod remains to count as inventory.

Manufacturing buys that rod in the same clearing. Its rod bill is
**45,827.18**: **27,566.15** local and **18,261.03** imported. It makes and
exports **7,611.705911 t** of wire and cable for **34,345.42**. Thus the wire
revenue is **11,481.76 less than the rod bill**, before its other costs.
This is a loss on sold output, not an omitted later export. Manufacturing's
own statement imports are 23,852.334 and total exports 37,672.432, including
fabricated steel; its local rod purchase must be included when following the
chain, even though intermediate local trades cancel from the city's GDP.

A temporary print in `Sector.forecastFlow()` pinpoints the price used by
FL1's order decision. In m901 it allows all **7,611.705911 t** of wire to be
made for export: its computed input cost is **4.470670 per tonne**, exactly
rod's still-stale landed price, against wire's net export price **4.512185**.
Rod's import price is already **6.064825**. The later rod clearing charges
the new price. The forecast and the eventual purchase are priced in different
phases; the goods themselves have already been sold. This is the concrete
production/pricing issue to consider if Jerus authorizes a wire fix.

The m902 pending Copper Refining exports **26,756.764** belong to a different
complete clearing, with a new **26,715.604** import bill and **4,621.895 t**
of rod made and exported. Adding that export to the earlier statement or
carrying its already sold rod as inventory would count different months'
goods together. The original negative GDP is reproduced: **-431.688583**.

The kept attribution log is
`target/checks/gdp1-rt3-attribution.txt` in this worktree. Its driver and
preparation scripts are `target/gdp1-probe/Ensemble.java`,
`target/gdp1-probe.py` and `target/gdp1-probe.sh`. They use RT3's compiled
model, seed 11, AUTO2, through m903, with the required Earth/world paths.
They print pending ledgers before the strike as well as statements after it.
The temporary probe uses TempDirs and no real save slot.
RT3's worktree HEAD at the replay is
`77e41367239ec424b5c2232ff67a5f35289cc3c9`; its relevant model is the held RT3
model. The last replay adds only the diagnostic print to a temporary copy of
Sector (identical to RT3's source otherwise) and still reproduces -431.689.
The replay CSV, `target/gdp1-probe/rt3.csv`, has all 902 recorded monthly audit
verdicts (m2 through m903) green; its only finding is "negative monthly GDP"
in m902.

## Common path and the requested inventory rule

This is shared, rather than Copper Refining specific. `Markets.clearMonth()`
produces flow goods, clears their markets, buys inputs and ships unsold flow
in the same pass. All processing and extraction sectors use
`Sector.shipUnsoldFlow()`. Stockable outputs sell from opening stock, then
`Sector.produceStock()` makes closing stock after clearing; direct exports
from the line are booked in that pass too. ProcessingSector and
ExtractionSector do not have independent export-booking paths.

`Game.startOfMonthUpdate()` strikes those pending ledgers before calling
`EconomyManager.updateNationalAccounts()`. The latter already sums actual
stock and pantries over **every sector**, with stockable outputs and imported
feeds held there included in NationalAccounts' inventory terms. The explicit
existing exception is household cars (NOT_HELD), whose sales are not in C.
There is no evidence of an uncounted unsold copper shipment at this boundary.

GdpCheck now has a section exercising the requested cross-phase condition
with both a real Electrolytic Refinery (processing) and a Leach & SX-EW Plant
(extraction). Each imports its recipe's feed, produces unsold cathode, strikes
the accounts, holds it another month, then exports from the shed after that
boundary. It checks the import, inventory increase, value added and identity
in the first month; zero output while holding; and the later export's
inventory drawdown and total counted once. A flat cathode price isolates
timing from trading losses and revaluation. These assertions pass on main's
unchanged model: the requested inventory mechanism already exists.

**★ Call:** retain the actual stock rule and LongPlaytest's exact
"negative monthly GDP" finding. Do not create inventory from goods already
sold, floor GDP, move the finding's premise, or change production/pricing
without a new decision. The negative RT3 month needs investigation of the
wire works' sales and feed prices, outside an unsold-inventory correction.
This evidence was sent to lanekeeper while the harness was being verified.

## GDP readers and the trace

GDP is a model input. `Game.priceTheDebtMarket()` passes monthly GDP to
DebtManager's debt/GDP spread. Auctions, corporate-bond foreign capacity and
Buyback also use monthly GDP; hot money and ForeignAccounts receive it.
AutoBuilder's debt limit uses rolling annual GDP. Therefore an accounting
change can move funding, FX and subsequent production, even without moving
cash directly.

CentralBank's policy-rate rule has no direct GDP read: its dial responds to
prices and labour-market slack. GovernmentPlayer's annual tax/benefit rule
reads GovernmentView.wealthYears(), based on wealth and revenue, not GDP.
LongPlaytest's broader test player does read GDP for intervention sizes and
throttles, and through the automatic builder. GDP is not merely a screen
statistic. At the initial proof-only stage no model, policy, save or
LongPlaytest code changed; the resumed forecast correction is described below.

## Initial proof-only checks

- `./batch-check.sh --fast`: exit 0; compile, regenerated maps, zero firm
  Stale findings. Kept log: `target/checks/gdp1-fast.txt`.
- `./batch-check.sh GdpCheck`: exit 0 on the first fixture revision, all its
  new delayed-export assertions green. Kept log:
  `target/checks/gdp1-gdp-check.txt`; full harness log `GdpCheck.log` beside it.
- Main's default LongPlaytest, 2,400 target months (2,404 audited steps),
  completes with six successful reloads. Its only finding is the already
  covered CL6 industry ring at m2400; no money-audit or negative-GDP finding.
  Log `target/checks/gdp1-main-LongPlaytest.log`; traces
  `target/checks/gdp1-main-*.csv`. This is a diagnostic playtest, not a baseline
  suite. The RT3 replay above remains the negative-month counterexample.

- Initial `./batch-check.sh --pick`: **exit 0**, GdpCheck and TimingCheck both
  pass; compile succeeds, maps regenerated, zero firm Stale findings.
  This includes the final fixture with imports preceding production and an
  explicit identity check in the import month. Model/harness commit:
  `373de3df`; kept log `target/checks/gdp1-pick.txt`, full logs beside it.
- `git diff --check` passes; edited files keep LF. No save slot, model,
  LongPlaytest finding or publisher-owned record is changed.

## Initial disposition: blocked on the author's decision

Lanekeeper's instruction of 2026-11-12 16:54
(`20261112-165434-548-lanekeeper-17b5`) confirms this attribution is the right
call and says the question is with Jerus: a proof-only batch, or a fix to
Manufacturing's wire. It explicitly instructs this session to finish the
pick, push notes and code, **not push a ship file**, and report blocked.
Accordingly there is no GDP1-ship.md, no readiness marker and no gate dry run.
No ensemble is run: no model or default-player code changed. A resumed
production/pricing change must judge its own trace and ensemble requirement.

## Resumed: the author's decision and the forecast correction

Asked "Should GDP1 make Manufacturing's forecast read the current rod price?",
Jerus answered, 2026-11-12 about 17:00: **"Yes, fix the forecast"**.
Lanekeeper relayed this as instruction `20261112-171726-733-lanekeeper-fdf9`.
The resumed batch fixes the common run forecast, retains the inventory proof,
and must keep every ensemble criterion that the newest baseline passed.

Before editing, the stale-input reader survey found:

- `Sector.inputCost()` (including captive-input recursion) reads
  `inputPrice()`, then `unitCost()` and `flowCostPerUnit()` feed
  `exportsAtNoLoss()` and `forecastFlow()` for every flow-making sector.
- `Sector.inputCostAtRate()` reads that same input price, feeding marginal
  costs, `getExportBoundOutput()` and `forecastOrder()` for stockable outputs.
  CopperRefining's override also uses it to replace purchased scrap cost with
  the old-copper pool's actual price. There is no wire-specific price reader
  to repair; all these works inherit the corrected input quote.
- Direct `landedPrice()` consumers outside the works' run decision are
  Markets' order/supplier-credit budgets, EconomyManager's inventory pricing,
  BuildCard and Motoring fuel figures, Freight's diesel figure, Haulage's
  crop/diesel/truck estimates, LuxuryRetail's shelf cost, and Retail's basket
  and fuel costs. They retain the existing recorded-clearing quote.
- SpreadPlanner's building appraisals use current net import/export prices
  (local price only for a missing side of a band). Refining's separate run
  rule explicitly reads after clearing; its run/outlook local crude and
  product quotes are the just-cleared prices. Neither is the pre-clearing
  FL1 stale-input path. Ordinary price getters and statements remain reads
  of the recorded month, rather than being rewritten by this correction.

**★ Design:** `GoodsMarket.currentLandedPrice()` prices the last observed
supply and demand on the current world/exchange/freight band. It shares the
clearing's price calculation, uses the current import price with no local
supply, and does not mutate the recorded clearing price. `Sector.inputPrice()`
uses it for every works. Quantities for the next order are still forecasts
of demand, as FL1 specifies; this corrects their price, without adding a
second clearing or changing the prior statement. No new saved state is needed:
the last supply and demand and the world band are already saved.

The new GdpCheck section causes the error twice, with a Wire & Cable Works
and a Rod Mill. Local supply is present at the old clearing, so the old
landed getter really is stale. The import band then doubles without clearing;
the output export price lies between the old and new input costs. On the
old model, both works order their inputs and make the full run at a loss:
**six assertions fail** in `target/checks/gdp1-forecast-before.txt`. Each
fixture also raises the output price enough to cover an all-imported bill
and checks that a real margin still produces and exports the goods.

## Forecast checks and the RT3 replay

The model commit is `aea7eb1d`. The forecast fast check exits 0; the named
GdpCheck, CopperRefiningCheck and ManufacturingCheck run exits 0, including
all six formerly red assertions and the profitable controls. Logs:
`target/checks/gdp1-forecast-fast.txt` and `gdp1-forecast-named.txt`.

The RT3 replay overlays RT3's BusinessInvestment (the retirement guard) on
this batch's compiled model, without changing either worktree's source. It
uses the same AUTO2 seed-11 driver through m903. At the clearing whose books
are struck in m902, the wire works now makes and exports **0 t**, orders
**0 t** of rod, pays **0** for rod and earns **0** from wire. Its former
7,611.705911 t loss-making run is idled. GDP m902 is **10,223.556**, versus
the reproduced **-431.689**. All 902 recorded audit verdicts pass and there
are no findings in this replay. Manufacturing still exports fabricated steel
(3,327.006595), so this is a refusal of the unprofitable wire run, not the
entire sector stopping.

The pure shipping model (main's retirement rule plus GDP1) is also replayed
with seed 11 through m903. Its history differs from RT3's, so it is reported
separately: wire made/exported and rod bought are also **0**, rod cost and
wire proceeds **0**, and m902 GDP **45,291.114**. Its 902 audit verdicts pass,
with no findings. Logs `target/checks/gdp1-rt3-fixed-seed11.txt` and
`gdp1-candidate-seed11.txt`; CSVs and exact driver/retirement overlay under
`target/gdp1-fixed-probe/`, prepared by `target/gdp1-fixed-probe.{py,sh}`.

The final model's default LongPlaytest completes with **no findings**, every
money audit passing and all six reloads matching. LongPlaytest's source,
including its exact "negative monthly GDP" finding, is unchanged. Its eight
CSV traces move against the initial main run; the month, bank and bond traces
first differ at row 853. Kept report:
`target/checks/gdp1-forecast-LongPlaytest.log`; comparison and SHA-256 hashes:
`target/checks/gdp1-trace-comparison.txt`. Therefore the final-model ensemble
was run as `GDP1`, against **0.9.93** (23/26), with three
JVMs to leave room for the shared PC's full pick and other sessions.

The ensemble completes **80/80**, **exit 0**, in **32.7 minutes**, on model
snapshot `aea7eb1d`. Its score is **23/26 beside 0.9.93's 23/26**. Every
criterion passed by that baseline still passes, with no PASS/FAIL verdict
changes. AUTO2 A2, AUTO2 A3 and HIGH R3 remain the same three failures.
All runs finish, all recorded money audits are green, and all five F1 rows
report zero runs with findings. The kept baseline is `docs/ensemble/GDP1.md`
with `{next}`; detailed score, run CSVs and snapshot remain under
`target/ensemble/GDP1/`. The exact comparison is
`target/checks/gdp1-criterion-comparison.txt`; launcher summary
`target/checks/gdp1-ensemble.txt`.

The paired RT3 replay's recorded CSV columns agree through m900 and first
differ at m901. `target/checks/gdp1-replay-comparison.txt` records that check.

## Full pick and provisional shipping record

The final forecast `./batch-check.sh --pick` selects **ALL 201** (Sector has
86 readers) and completes **exit 3**: **196 pass, five known red**, no NEW red.
The known harnesses are MonetaryCheck, HealthCheck, PlanCheck, KeptCheck and
DeathRecordCheck, covered by the existing exact/scoped records. The suite
takes 2,915 seconds on the shared PC; KeptCheck accounts for 1,218 seconds.
Thirty timing assertions are soft, as prescribed. Compile succeeds, maps
regenerate, Stale has zero firm findings and the tree check passes. Logs:
`target/checks/gdp1-pick.txt`, `target/checks/suite.txt`, and the five harness
logs beside them. No known-red entry naming GDP1 exists in this branch's
record. That pick required no new waiver.

The full pick is of the final model `aea7eb1d`, with only notes, the design
write-up and its ensemble baseline committed afterwards. A provisional
GDP1-ship.md is now required on origin for the versioned gate dry run.
After that run, only these notes and GDP1-ship.md may change.

## Retained gate dry run

`sh tools/gate.sh GDP1 --dry-run` is launched after provisional ship commit
`6535c3a12b28aba42e043bf5b448bef58c9983e0` is pushed. Its main is
`6e3698445cb0dbceba70c2aa51c749f65b91e879` (DM2). The retained fresh-clone run
is `C:/Users/Jerus/AppData/Local/Temp/cbs-gate-GDP1-TYIaTCZM`, also available
in Git Bash as `/tmp/cbs-gate-GDP1-TYIaTCZM`; its launcher log in this worktree
is `target/checks/gdp1-gate.txt`. The run's `branch.commit` and `main.commit`
hold those two identities.

The first dry run completes its checks but exits **1** (suite exit 4), solely
for one NEW MapCheck fixture assertion in section 14: "fixture: the boxes
take offers, several at once, and every offer is taken by some and missed
by others". All 201 harnesses complete in 2,388 seconds: 195 pass, the five
existing known-red harnesses remain red, and MapCheck adds that single
assertion. Compile, generated maps, tree and Stale pass. Its full MapCheck
log is in the retained clone's `target/checks/MapCheck.log`.

The provisional ship file was removed and that withdrawal pushed first at
`45a10101`, before attribution or any repair. No model source changed.

## MapCheck offer-fixture follow-up M6-F

The failing fixture scatters 200 deterministic random boxes over the map and
expects every one of 24 offers to have been taken by at least one of them.
In the GDP1 merge it takes 22, misses all 24 at least once, and includes 37
multi-offer boxes. **All 200 selection results equal the independent
plot-by-plot oracle.** The separate deliberately placed one-free-plot box
also takes each of **all 24 offers** alone. Thus actual selection and offer
geometry assertions hold; the scattered sample does not cause the fixture's
extra all-offers coverage condition.

A named main-model control removes every GDP1 model change by compiling
Sector, GoodsMarket and GameVersion from main `6e369844` over the gate's
otherwise-main DM2 classes. The exact control sources and runner are under
`target/gdp1-map-control/` and `target/gdp1-map-control.{py,sh}`. This is an
attribution control, not a rerun to locate the already known assertion. Its
first compile used an MSYS classpath, then was corrected to native C:/ paths.
The completed control is `target/checks/gdp1-map-main-control.log`.

The control takes **24/24** offers with its scatter: 108 taking an offer and
50 taking several, with all 200 oracle comparisons and all 24 targeted boxes
passing. Its only two failures are the existing BQ1 card-bond fixture lines.
Consequently main alone does **not** reproduce this coverage failure. The
forecast correction changes the legal fixture city's offer geometry, and
the newer DM2 harness also removes the former FC1-off history switch. The
sample's coverage assumption is state dependent; no map-selection code is
changed by GDP1.

**★ Call:** classify the coverage setup as a separate harness repair, M6-F,
under the standing rule for unrelated fixture reds. The oracle and targeted
tests demonstrate the selector is correct on the actual GDP1 geometry;
restoring the old economic path would undermine the forecast fix. Keep the
failed assertion exactly as it is and queue a fixture that causes its premise.
No existing known-red entry naming GDP1 exists to reassign. The ship file
records this single unrelated assertion and its queued fix, with this evidence;
no model, map code, finding or harness premise is changed in GDP1.

M6-F's brief: in MapCheck section 14, retain all 200 scattered box/oracle
comparisons and the full all-offers take-and-miss assertion. Add deliberate
hit and miss boxes for each offer, evaluated against the same independent
plot oracle and included in the fixture's coverage sets. Cause a multi-offer
box explicitly as well. The premise must hold for the main and GDP1 offer
geometries, rather than depending on a particular city or increasing a random
sample until it happens to pass. This is harness-only work; no model change
or ensemble is needed. The author may review the queued repair in its own batch.

## Accepted gate and readiness

After the control and queued fixture record, `sh tools/gate.sh GDP1 --dry-run
--reuse C:/Users/Jerus/AppData/Local/Temp/cbs-gate-GDP1-TYIaTCZM` completes
**exit 0**. The common suite judge returns **exit 3**, known reds only:
DeathRecordCheck, HealthCheck, KeptCheck, MapCheck, MonetaryCheck and PlanCheck.
There are no unnamed red lines. The accepted dry run is retained at
`C:/Users/Jerus/AppData/Local/Temp/cbs-gate-GDP1-BTV6i5yq`; its `ship.log` records
the rejudgment and successful dry run. The launcher summary in this worktree
is `target/checks/gdp1-gate-rejudge.txt`.

That accepted run's two commits are branch
`f6a651709fab0413b9cc08bc0c445490e242d2d9` and main
`6e3698445cb0dbceba70c2aa51c749f65b91e879`. It explicitly reuses the complete
201-harness checks from `cbs-gate-GDP1-TYIaTCZM`, whose checked branch was
`6535c3a12b28aba42e043bf5b448bef58c9983e0` on the same main. The ship marker
names that complete reusable run; both retained directories are kept. The
publisher's trial records use 0.9.96, leave the missing GDP1 queue row alone,
and add the M6-F queued row. No real merge, version edit, tag or main push
was made by this session.

Only GDP1-notes.md and GDP1-ship.md changed after the first gate's checked
commit, as required. The final model remains `aea7eb1d`, with the 23/26
ensemble retaining every 0.9.93 PASS. GDP1 is ready with the unrelated
offer-coverage fixture repair explicitly queued and no GDP or money-audit
finding waived.

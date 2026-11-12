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
statistic. This candidate changes no model, policy, save or LongPlaytest code;
no accounting-induced trace movement is claimed.

## Checks so far

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

Final pick and disposition are to be recorded after the fixture's final check.

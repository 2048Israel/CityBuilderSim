# FB12 — fixtures on two landmasses

Harness-only batch, 2026-11-18: RI1-F and KP2b-N. No game class, balance,
save format, screen or default-playtest code changes; the default trace cannot
move, so no ensemble is owed.

RI1-F adds IslandRailCheck section 10 on BridgeMapCheck.MapFixture's real
generated-world terrain. Its owned banks are separate map pieces, divided by
103–131 plots of fresh water. The far piece has built weight, no terminal,
its highway hub nearest the founding site's tile, and its rail hub nearest
that anchor at RAIL_AT modulo CELL. It grows its own track and one of the two
yards. The bridge carries track over 131 water plots; the otherwise identical
map without it carries none over those plots. Expectations use the existing
crossing constants, model type ids and fixture track/yard counts. The first
run found 783 island rail plots and one island yard.

KP2b-N adds a self-contained section beside KeptCheck's land office. A
GameFiles.scratch city on the existing Rotterdam Earth fixture initially owns
only home; buying a measured square of landmass 648 causes the second
Landmasses.rows() entry and the actual page's grid. Every cell is compared
with LandmassView.rows(), and the column heads come from LandmassView.HEADS.
The grid and all its authored descendants must retain identity on a redraw,
then after another island purchase and a month; the values must update and
match a fresh table. No KeptBench or screen API change is needed.

★ Use BridgeMapCheck's real channel, which the brief explicitly permits,
rather than adding another Earth survey fixture. CityRuns reads map pieces,
not their link type: the bridge's independent water-plot assertion proves
the link while the hub, track and yard assertions prove RI1 call 3 there.
This does not extend trains' routing over bridges (RI1 call 2).

★ Reuse the existing Earth fixture and scratch-city helper for the land
office, rather than changing Landmasses or LandManager. Check the actual
rendered grid and its descendants, not the bench's table-presence helper,
which constructs a table when called.

Checks and cause-removal evidence are recorded below.
The orchestrator cleared IslandRailCheck and KeptCheck one at a time, and
reserved TimingCheck for the gate because BW1's dry run occupies the machine.
The focused diagnostic selector used during construction was removed
before the final whole KeptCheck; normal suite coverage is unconditional.

Review scope: money no; saves no; threading no. All additions are harness
code; the FX fixture uses KeptCheck's existing fx() handoff. Only generated
dependency indexes mention the read-only model classes.

Construction checks: initial `--fast` green; IslandRailCheck green (20 s);
focused landmass-table KeptCheck green, all 12 assertions reached. Both Earth
and world paths were supplied with JDK_JAVA_OPTIONS on every check.

Cause-removal runs (harness source only, restored immediately; no model edit):

- Omit both island purchases: eight new assertions fail, including the
  second Landmasses.rows() entry, real grid presence, every value/identity
  check and the fresh-table comparison. The original one-row/no-table
  control still passes. `target/FB12-Kept-no-ownership.log`.
- Forget the kept pool before each draw: all three identity assertions fail
  (unchanged redraw, changed month, restored kept page); the value assertions
  pass. `target/FB12-Kept-no-keeping.log`.
- Disable island rail in section 10 and omit its bridge: the railway hub,
  island track and island yard assertions fail, as does the independent
  water-crossing assertion (four failures). The founding-side highway hub
  and no-port anchor assertions still pass, as RI1 call 3 requires: a bridge
  does not relocate the hub. `target/FB12-Island-no-rail-or-bridge.log`.

The passing construction logs are `target/FB12-IslandRail-positive.log` and
`target/FB12-Kept-focused.log`. Target logs are local evidence, not shipped
files; the outcomes and the exact cause changes are kept here.

The final **whole KeptCheck** passes with exit 3: only the two already
recorded main failures, Construction's Cash & debt statement layout (KRT1)
and the Bank's post-choice node-retention figure (KBP1). All 12 FB12
assertions are reached and pass in that full run. The common judge names
those existing queued fixes; there is no new red or new known-reds row.
Log: `target/FB12-Kept-whole.log`; verdict: `target/FB12-Kept-whole.txt`.

Final sequential checks on the restored code: IslandRailCheck, ChartFitCheck,
MapCheck, ResearchViewCheck, SaveFileCheck and ToastCheck each exit 0; final
`./batch-check.sh --fast` exits 0 (compile, regenerated maps, 0 firm Stale
findings). Logs are `target/FB12-<Harness>.txt` and `.log`, the fast log
`target/FB12-fast-final.txt`; exits are in `target/FB12-final-summary.txt`.
The final source is the two harness commits 86e150ac6 and 9f2083f49, based
on main 28ee126e8 (0.9.150); no game class changes.

The pick is **eight, not ALL**: ChartFitCheck, IslandRailCheck, KeptCheck,
MapCheck, ResearchViewCheck, SaveFileCheck, TimingCheck and ToastCheck.
The orchestrator explicitly reserved TimingCheck for the gate, and cleared
the other seven one at a time (mail 20261118-153040-430-orchestrator-d323);
therefore `--pick`, which would also run TimingCheck, was not run here.
Review line: **not needed — no money, saves or threading in the diff**.
This is an untagged batch; no dry run is owed for this pick, so there is no
kept dry-run directory or gate-run comment. Both rows are complete; nothing
leaves the bundle and there are no new calls left for Jerus.

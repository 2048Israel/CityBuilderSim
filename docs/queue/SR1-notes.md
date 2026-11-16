# SR1 — A city stocked complete in bulk reloads with its cash drifting

## Finding

This is an untagged harness repair on `batch/SR1`, cut from main
`a316d19bd` (0.9.122). The brief's leak was already cured by AW2:
`b94a6eae0` saved the live land allocation; merge `b8d4750d5` shipped it.
SR1 changes no model, save format, load path, fixture building or cash.

`CausesOfDeathCheck.stock()` and SicknessCheck's equivalent put complete
stacks straight into BuildingManager without calling LandManager.allocate().
At m3 the unchanged fixture has 69,000 sq ft allocated but 20,989,000 sq ft
of catalogue footprint. Before AW2, Game's loader reconstructed allocation
from that footprint. The next month's land scarcity and prices then fed
building costs, business pools and treasury cash. This was the fixture's
unallocated ground, not a lost dues, trade, fuel or credit flow.

The real founding bank and yard separately allocate their ground after
addStack(..., true); municipal and private orders allocate before placing
their stacks. Complete construction itself is not the condition. AW2's
production-caller audit and existing SaveFileCheck allocation regression
already cover the saved field and its absent-key fallback. No new DataSave,
HistorySave, Game, SaveFileCheck or ReadPathCheck change is needed here.

## Reproduction and isolation

The first probe ran today's main before any source edit, using founded()
unchanged, scratch saves, one-month ticks, and the section-10 comparison.
Cash and every MoneyAudit pool are identical immediately after load and
after playing on at all four points. Bare, cash-only and playtest-founded
controls also match at all four points (16 paired probes in total).

| reload at | R0g aab9483df: next cash, reloaded minus live | main a316d19bd |
|---|---:|---:|
| m3 | +0.014345825984491967 | 0, every pool bit-exact |
| m12 | +0.0036139250587439165 | 0, every pool bit-exact |
| m60 | +1.2788340823099134 | 0, every pool bit-exact |
| m228 | +0.16187560382240918 | 0, every pool bit-exact |

A short historical search, with one JVM job at a time:

- R0g release `aab9483df`: reproduces the brief's entire table above.
- FM1 merge `a10084494`: m3 still +0.014345825984491967.
- After HA1 `c5c7a460f`: m3 is bit-exact.
- AW2's parent `5a87e1021`: m3 +0.014345822855830193; allocation
  changes from 69,000 to 20,989,000 on load, with the same footprint.
- AW2 merge `b8d4750d5`: m3 zero; allocation remains 69,000 on both.

The exact new early-reload and bulk-influenza guard methods were extracted
unchanged into a temporary subclass of R0g's CausesOfDeathCheck. Its shared
helpers and model were R0g's. It reports **10 failures**: allocation and
next-month equality at m3/m12/m60, allocation in December 1918, and equality
in January/February/March 1919. Thus the guards catch the original leak,
including its mid-influenza continuation; this is not an uncaused premise.

Local evidence: `target/SR1-main-probe.log` (all four main controls and the
reflective after-load diff), `target/SR1-evidence/R0g-table.log`,
`FM1-table.log`, `after-HA1-table.log`, `before-AW2.log`, `after-AW2.log`
and `R0g-guard.log` under that evidence directory. The historical worktree
was `C:/Users/Jerus/AppData/Local/Temp/cbs-SR1-history`.

## What SR1 adds and the calls taken

★ Keep founded() and stock() exactly as they were. Allocating the injected
ground now would move the harness's premise and hide the historical fault.
The founded() comment explains the bypass and names AW2 as the existing cure.

★ Section 10 now runs the full reload twin on BOTH founded()'s bulk city
and LongPlaytest.founding(): December 1918 saves, then January through
March 1919 compared to the bit through firstDifference (cash/fingerprint,
every audit pool, cohorts, causes, Health and Sickness state). Early saves
at m3/m12/m60 additionally prove the raw-stack allocation gap still exists,
check that allocation survives loading, compare immediately on load and
after the following month, and print the cash/pool result.

★ Harness-only, no tag: no game code or unbroken-game behaviour changes,
and neither SAVE_FORMAT nor OLDEST_LOADABLE_FORMAT changes. On the bus
(2026-11-16 00:31), the orchestrator explicitly waived the eight-trace
comparison because a harness-only diff cannot move the playtest. No main
playtest or ensemble was rerun. Its picked selection is the dry run.

No known-red entry names SR1, and no transfer is needed. No other batch's
source files were changed. WC1/docs/batch.md explicitly assign this whole
batch to Codex. No process changes: one-in/one-out does not apply.

## Verification and readiness

- Initial main `./batch-check.sh --fast`: exit 0; compile, maps, Stale/tree green.
- `./batch-check.sh CausesOfDeathCheck SicknessCheck`: exit 0; both pass,
  compile/maps/Stale/tree green. Each early cash difference is exactly zero
  and every audit pool matches; both influenza founding twins pass. Log:
  `target/SR1-evidence/named.log`, detailed harness logs in `target/checks/`.
- `./batch-check.sh --pick` at source commit `cb91b14a`: exit 0;
  CausesOfDeathCheck and TimingCheck pass, compile/maps/Stale/tree green.
  `sh tools/pick.sh origin/main HEAD`: 2 of 208 harnesses;
  `review not needed - no money, saves or threading in the diff`.
  Log: `target/SR1-evidence/pick.log`.
- Reviewed the final diff: only the causes-of-death harness, its generated
  maps/index and these records. The original stock/cash/founding premises
  and the existing playtest twin remain unchanged.
- Merge of origin/main (FA1, 0.9.124, `d55f18c37`) into the branch as
  `2d598ecf5`, pushed: `docs/harnesses.md` and `docs/map/README.md`
  conflicted (generated), taken from main's and regenerated by the check's
  Maps step. `./batch-check.sh CausesOfDeathCheck SicknessCheck` on the
  merge: exit 0, both pass, compile/maps/Stale/tree green.
- Retained launcher dry run, `sh tools/gate.sh SR1 --dry-run --no-tag`:
  **exit 0**, kept directory `/tmp/cbs-gate-SR1-Z0ArLg8T`, 02:26:57 to
  02:29:04 on 2026-11-16. Its two commits: branch `2d598ecf5` (the pushed
  merge, 3 commits of batch/SR1 merged), main `d55f18c37`. Pick: 2 of 209
  harnesses (CausesOfDeathCheck, TimingCheck), version 0.9.124; "no game
  code: 2 harnesses; green"; review not needed (no money, saves or
  threading in the diff). Reds: none, `checks/reds.txt`, `new-reds.txt` and
  `unnamed.txt` are all empty, so no known red either; Stale 0 firm; tree 7
  changed, 3 of them the generated docs.
- An earlier launch, `nohup sh tools/gate.sh ... &` from a shell that then
  returned, was cancelled at startup (`/tmp/cbs-gate-SR1-mym7O2SY`, exit
  130, "Gate caller ended during startup"): the launcher watches the
  calling shell's process tree. Run with the gate command itself as the
  background task (Bash `run_in_background`), not detached from a shell
  that returns. Nothing was merged, tagged or pushed by either run.

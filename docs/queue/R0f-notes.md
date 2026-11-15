# R0f — Money on Canada's dates

Built from main c0120d77. The initial `docs/notes/known-reds.txt` had no rows naming R0f; existing entries are kept separate from this change.

## Calls and implementation

- ★ Scope: the dates apply only to `Game.foundedUnfitted()` cities, matching R0c–e. Orchestrator clarified this by bus on 2026-11-15 (message 20261115-000500-368-orchestrator-9ac3): its brief's “post-1935” meant “founded the default way.” All founding calendars currently start in January 1900; R1d will enable the full 1900 founding and run its ensemble. Default founding therefore retains all existing Money mechanics.
- ★ September 1931 is the final departure in the study's "1929–31" interval: the recommendation specifically says Canada followed Britain off gold in 1931. July 1926 and the two lever dates are explicit monthly data in `PolicyDates`. No discovery brings Money forward.
- ★ Canada's dated gold is a par held by the whole reserve vault, implemented through the existing peg settlement, without the chosen peg's doubt break or import-cover floor. It is distinct from GD3's player-chosen Gold, whose metal cover, gold points, defence rate and US gold-date decisions remain available after the Bank opens.
- The first exhaustion of a dated gold episode spends the remaining reserve and floats the dollar. The episode and its exhausted flag occupy `ForeignAccounts` save slots 156–157, carried by `DataSave.foreignAccounts`; hearing the calendar again does not re-enter an exhausted episode. The 1926 return starts a new episode.
- Before August 1914 and during dated gold, interest follows the world's rate; August 1914 opens the government's manual Advance Rate. March 1935 opens the autopilot, target, strictness, holdings, advance-ceiling and currency controls. Automatic central-bank books still represent the earlier government's money accounting.
- One idempotent `Game.moneyHearsTheMonth()` serves founding, `Game.nextMonth()`, `SimulationEngine.simulateMonth()`, the world's current-rate handoff and the load after saved books return. Permissions are derived from the saved founding and month; episode exhaustion is saved state.
- ★ Save format and oldest readable format rise together from 52 to 53. An older unfitted city would otherwise load under a different monetary contract. No conversion is added.
- Kept clear of CM1b and RT2's owned source files, including CentralBankCheck and MonetaryCheck. New dated fixtures live in MoneyDatesCheck, with narrow save/read sections in SaveFileCheck and ReadPathCheck.

## Verification (in progress)

- Initial `./batch-check.sh --fast`: exit 0, compile, maps, Stale and tree green.
- The original default LongPlaytest trace is being retained under `target/R0f-base/`; model verification and trace comparison follow.

### Save investigation

The new exhausted-gold fixture first found an R0f bug: after a load the inherited policy rate was the fallback 2%, although the restored world rate was 5.5%. The world's rate returns later than the saved dial. `handTheUsRate()` now updates inherited interest when that final rate returns; defaults do nothing extra.

A separate whole-foreign-account equality after the next month is still red. The same untouched fixture on main c0120d77 reproduces its earlier cause: the saved Retail `wantedSpend` is 0, the loaded one 0.02587509525508486, with subsequent rent, household and cash differences. Both R0f and main first differ in that same saved Retail field; R0f's new foreign stock is exactly equal immediately after loading, its exhausted rule stays off gold after returned reserves and the next month, and its rate is identical. The whole-twin equality is kept as a separate hard assertion (no fixture or tolerance moved), queued as R0f-S. Attribution logs: `target/checks/R0f-main-load-diff.txt` on untouched main classes and `target/checks/R0f-load-diff.txt` on R0f; temporary `MoneyLoadDiff` probe uses the same zero-vault founding, first month, reserve endowment, snapshot, JSON load and next month in both. No player folder is touched.

- The latent R0f-S discrepancy is first visible immediately after loading the **month-2 (February 1900)** snapshot; the first played divergence is **month 3**. Orchestrator agreed by bus (20261115-001621-967-orchestrator-3091) to retain that exact line and queue R0f-S with main's reproduction.
- Current MoneyDatesCheck: **427 audited months, 237 on dated gold, 189 floating, zero money-audit failures**, using `MoneyAudit.tolerance`. All R0f mechanics are green; only the separately attributed whole-account twin is red.
- `./batch-check.sh MoneyDatesCheck PolicyDatesCheck SaveFileCheck ReadPathCheck`: exit **3**, only the R0f-S whole-account assertion; compile, Stale and tree pass. Detailed launcher log `target/checks/R0f-save-read.txt`.
- Visual QA: rendered and inspected `target/R0f-ui/1900-rate.png`, `1900-currency.png`, `1914-advance.png`, `1935-bank.png`. Closed pages show dates and the inherited-interest explanation; August 1914 shows only the manual Advance Rate; March 1935 shows the existing full Money controls. Temporary KeptBench renderer exited 0 after writing all images; JavaFX emitted a toolkit-thread NullPointerException on shutdown after the completed rendering, not during drawing.
- Before-change proof: the new fixture compiled against main c0120d77 model classes (with the new pure PolicyDates data) failed the gold and gate conditions (`target/checks/R0f-before.txt`), without a linkage error. Both boundary assertions also retain their before-state premise, so merely already being off gold or having controls open cannot count as a transition.
- The first original trace attempt was invalidated by a clean compile deleting its lazily loaded class files. Both traces therefore run from independent copied class directories, preventing that build/run race; no result from the interrupted attempt is used.

### Default trace

The final model (34851590; source and fixtures frozen at c4f49283) and unchanged main c0120d77 ran the full default 2,400-month LongPlaytest from isolated class directories. **All eight CSV trace files are byte-identical**, with no exclusions or normalisation. Therefore the ensemble is skipped under docs/batch.md 6. `target/checks/R0f-trace-comparison.txt` retains every matching SHA-256; traces are under `target/R0f-base/trace-*.csv` and `target/R0f-final/trace-*.csv`.

Both playtests exit 1 only for the existing DM3-R finding: twelve of district (-1,-1)'s 4,301 boxes move at month 2389. The finding text and counts match exactly. Both runs' money fixtures finish green. Logs: `target/checks/R0f-base-playtest.txt`, `target/checks/R0f-final-playtest.txt`. This is separate from R0f-S and from any newly caused failure.

The final committed fixture on the before-change model reports 17 failures (`target/checks/R0f-before-final.txt`), including **each** explicit crossing assertion: August 1914, July 1926, September 1931 and March 1935. It still audits 427 months with zero money-audit failures; dates and permissions, rather than an incidental economic crash, are the failures.

Matching trace hashes (main and R0f):

| CSV | SHA-256 in both runs |
|---|---|
| trace-bank.csv | `ec87f0b662ef4454901a3969682a27d54294edbf866648798d92c6982e29b9dc` |
| trace-bonds.csv | `38960e947f63cdfc0f36efea066e79e6ff0701db129013052dd99ac2adaaaecf` |
| trace-borrow.csv | `56f88a3dc5eafcf2b69c92b591aa1d0fb1458529b0a9d854e772e6b22cf91bf3` |
| trace-build.csv | `4f8d05d6be61bb06e55c51926b68d48aa40e5fd9a999fd7bb53bb1cc3a98e62d` |
| trace-house.csv | `e4a0d57746386b8a0a361edd92dd1f60eb228f94821c02874e9b3ece424f9b54` |
| trace-labour.csv | `87e8c6a36d828feab8dbad3f7544742a823eeb7ab57cb7c9b150a45eddf631a2` |
| trace-month.csv | `4777025983e027980e45e54551c62e8dbb7f4a551726d882ab760fd6a3ed6ed0` |
| trace-pop.csv | `d18dcab4154c4327249148c1670354d67d6a04b40d2f45fd22adc4dbd46c7c7f` |

### Stranger review REV-R0f-1

The review of c0120d77..c4f49283 found a real in-month permission bug: after exhaustion in the 1926-31 gold episode, DebtManager's cached `rateOpen` stayed false until the next month, while a load of that month computed it true. Game now refreshes the dated rate permission immediately after the reprice breaks a hold in a 1900 city. The default founding skips the added branch.

The new fixture starts with zero reserves, actually plays from 1900 to June 1926, asserts the first exit occurred and the vault is empty, then crosses July 1926. The new episode breaks by VAULT, the break count increments, and a distinct manual Advance Rate must take immediately in the same month and round-trip through JSON. Before the fix it fails (`target/checks/R0f-review-before.txt`, two failures including R0f-S); after it passes (`target/checks/R0f-review-after.txt`, only R0f-S red). The expanded dated run audits **745 months: 237 on dated gold, 507 floating, zero money-audit failures**.

The reviewer also confirmed R0f-S on unchanged main: the positive-only `carriedRetailWant` / `carriedRetailCapacity` restoration guard leaves the loaded zero-spending month with a recomputed positive want. The exact whole-account assertion remains hard and queued separately. No review finding is left unmended in R0f.

### Gate preparation

Orchestrator accepted b8eb46dc's review mend and, after CM1b shipped, explicitly instructed merging main e8ac5a53 (0.9.109) and using the launcher dry run for the ALL pick, with no duplicate local full run (bus 20261115-004002-379-orchestrator-604d). The merge had no source conflict: its three conflicts were generated `docs/dials.md`, `docs/map/GameVersion.md` and `docs/map/README.md`; all maps were regenerated. `./batch-check.sh --fast` on the merge exits 0 (`target/checks/R0f-merge-fast.txt`). The official main changes to VERSION, queue, changelog and gate record enter only by this merge.

The coordinated launcher gets the required Earth/world options plus `-Dplaytest.trace=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/.claude/worktrees/codex-R0f/target/R0f-gate-final/trace`, so its full playtest supplies the trace on the final reviewed model as well. This changes trace output only; no playtest premise, model setting, seed or duration changes.

### First ALL dry run (failed; not reusable)

Run directory: `C:/Users/Jerus/AppData/Local/Temp/cbs-gate-R0f-mq9Oy4O8` (Git Bash `/tmp/cbs-gate-R0f-mq9Oy4O8`). Started by the normal `sh tools/gate.sh R0f --dry-run` route with the orchestrator's go. Its immutable identities are:

- branch: `456e6c65f119cb483765f52c1b8d4a6e4bc5f29b`
- main: `e8ac5a53b1bbc604ce984dbe7b366a69d00e3fff`

The launcher transcript is `target/checks/R0f-gate-launch.txt`; the retained directory owns `ship.log`, the fresh clone's `target/checks/` logs and its result metadata. The launcher exited 1 because R0f-owned assertions and fixture preconditions failed; this run is not reusable for readiness. The provisional ship marker stayed on, and source/fixture mends require a new coordinated run.


### First ALL findings and mends

The normal launcher ran the full suite on branch 456e6c65 and main e8ac5a53, then stopped for these R0f-owned lines, kept individually in the run's `unnamed.txt`:

- VaultMoneysCheck's two old 156-slot assertions, PegAnchorCheck's one and CurrencyRegimeCheck's band-support fixture length. The array is now 158 slots because the episode and exit must be saved. The model names CANADA_GOLD_PERIOD_SLOT, CANADA_GOLD_EXIT_SLOT and SAVE_SLOTS; those exact size assertions use the model's contract. No held band, peg, dealing or restore premise changes, and all old slot positions stay fixed.
- ResearchCheck expected at least eleven discovery keys. Money is data, so CENTRAL_BANK no longer has a discovery key. The check now requires every dated non-Money lever to have its catalogue entry and both Money keys to be empty, preserving the date/field/catalogue assertions rather than weakening them to an arbitrary new floor.
- DiscoveryCheck and PrizeCheck's reform-invariance fixtures already use the existing `reformCurrencyForTest` seam, which sets forcedReform to bypass player eligibility. The new date guard incorrectly ignored that flag. It now applies only to the player's door, preserving the existing seam and both unchanged fixtures. Public Currency reform remains closed before the Bank opens.
- ★ PrizeCheck section 12's medical-lab staffing and its two consequent ordering lines. Main's unchanged section 12 passes (`target/checks/R0f-main-prize12.txt`). The identical month-173 city with handed heads [0,5000,2200,1000] has main posts [3514,3674,2659,454.0629] and 86.937 college spare; R0f's dated money changes the works before that month to posts [7520,5106,2094,373.1107], whose diploma shortage draws college heads down to -43.567 spare and the lab staffs 69.935%, below the unchanged 80% floor. Logs `target/checks/R0f-main-medical.txt` and `target/checks/R0f-branch-medical.txt` retain wanted/spare/drawn vectors. handMore keeps its 200-head minimum, then physically supplies the college deficit for two model-quoted medical labs (wanted minus spare). The city, month, wages, lab target, ordering cap and hard staffing floor are unchanged; no loop tunes the fixture until a verdict passes. This restores the original premise of a village handed enough workers to test the one-lab-a-field monthly cap.

Orchestrator agreed the seam/schema/key mends and requested another delta review and dry-run go; the next slot order is RT1b, R0f, then RT2 (bus 20261115-012315-619-orchestrator-2ae3). No unrelated line was re-filed for these failures.

Orchestrator accepted the PrizeCheck repair on the house rule (20261115-013026-149-orchestrator-2f1f): the two-lab staffing premise, every assertion and the 80% floor stay as they are; only the handing follows the model's wanted-minus-spare deficit with the existing 200-head minimum. The named mending check runs with `-Dcbs.parallel=1`, so only one harness is JVM-heavy while RT1b owns its dry-run slot.


The serial named run `./batch-check.sh MoneyDatesCheck VaultMoneysCheck PegAnchorCheck CurrencyRegimeCheck ResearchCheck DiscoveryCheck PrizeCheck` exits **3**, only accepted R0f-S red; all seven harnesses ran and every repaired assertion passes. PrizeCheck has one existing policy-approved OBSERVED path assertion, recorded as information. Compile, maps, Stale (0 firm) and tree pass. Log: `target/checks/R0f-contract-mends.txt`. No assertion was weakened or deleted, and no fixture date, city or staffing floor moved.

The first full launcher's post-review, main-merged default trace also matches all eight original main traces byte for byte (the same SHA-256 values above). Thus its additional owned reds were contract/fixture assertions, with no default model trace movement. The next launcher will capture the final mended model again.

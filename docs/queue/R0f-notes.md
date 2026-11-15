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

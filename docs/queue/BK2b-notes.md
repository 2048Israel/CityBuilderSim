# BK2b - BK2's review follow-ups: each holder's own discount

Brief: [BK2b.md](BK2b.md); accounting decisions: [BK2-notes.md](BK2-notes.md).
Started from main 6114b00f8 on batch/BK2b. No known-reds entry names BK2b.

## Changes and calls

- The Game issue comment now describes each holder's discount remaining unchanged at a sale; nothing else in Game is changed.
- ★ Apply pass()'s HOLDING_ROUNDING rule in unaccretedOn(Holder, face), the discount quote retirePart uses. Keeping the quote and retirement together also hands the bank's buyback booking the same discount the line removes. A remainder above the model's relative floor still keeps proportional discount.
- The ulp-short fixture buys Math.nextDown(held) from each of BANK, HOUSEHOLDS, FIRMS and WORLD, on a settled five-holder piece, at today's and PriceLevel.FACTOR's scales. It proves the positive remainder is below the model's floor and the old proportional calculation leaves positive discount dust; it checks the discount quoted and retired exactly, the other holders unchanged, and a control above the floor.
- ★ Add a separate premium fixture using the same city and auction as 13a, with its first sale at .001 of spare household cash. Existing fixtures keep .15. This makes the next premium exceed the bank's positive discount, with a hard premise assertion. Its no-restrike twin proves the loss routes to retained and equity's movement/split, outside income, tax, dividends and paper gains.

## Checks

- Before the model fix: --fast green (zero firm Stale findings). HoldersCheck causes exactly 16 new failing assertions: all eight ulp-short holdings fail both full-discount handover and zero-discount-after-retirement. The new premium case already passes before the fix (premium 0.523920656k, positive own discount 0.070544306k, shortfall and retained loss 0.453376350k), as expected: it covers existing accounting, not a new behavior. Evidence: target/BK2b/holders-before.log and target/BK2b/check-before.log in this worktree.
- After the fix: ./batch-check.sh HoldersCheck BuybackCheck exits 0; both harnesses green, maps regenerated, zero firm Stale findings. Evidence: target/BK2b/holders-after.log and target/BK2b/check-after.log. The ulp-short assertions are now green with the same fixture; controls above the floor remain proportional. Existing HoldersCheck laws also pass.
- sh tools/pick.sh selects ALL 208 harnesses (Game has 172 readers), with money review required for Debt. Per the starter's one-ALL-run instruction, no local ALL pick is run; the normal launcher dry run supplies the full merge verdict.
- Only the specified comment changes in Game. All three edited source files retain LF. No save schema, save slot, process machinery or gameplay dial changed.
- The default trace is unchanged; the full launcher merge dry run is green, as recorded below.

## Full merge gate and trace

The orchestrator granted the ALL slot in cbs-bus message 20261115-191859-628-orchestrator-0842.
Normal command: sh tools/gate.sh BK2b --dry-run, using the suite's default pool and both required data files.
Kept directory: C:/Users/Jerus/AppData/Local/Temp/cbs-gate-BK2b-20261115-1920.
Branch input: c70dbcc2cdf659c21934b2cb35b139abad342a64.
Main input: 6114b00f8b2f2d42f5b796045fe571bd572999a6.
Final model/fixture commit: b09109cc; the source is unchanged after it.

- Launcher exit 0; check suite exit 3 in 3,520 seconds. The suite ran 195 harnesses in 3,437 seconds: 189 passed, six known-red harnesses (DeathRecordCheck, InvestCheck, KeptCheck, MapCheck, MoneyDatesCheck, ResearchViewCheck). No NEW reds; unnamed.txt is empty. No BK2b known-red cover existed or was transferred. Maps regenerated; Stale has zero firm findings.
- Fifteen timing assertions are soft, per the normal gate route. The default resource pool held 48 starts (47 CPU, one available RAM); no serial override and no overlapping JVM job was launched here. The persistent hidden launcher outlived tool calls. No branch pushes occurred during the run.
- The existing ER1 run C:/Users/Jerus/AppData/Local/Temp/cbs-gate-ER1-afpO6SF1/checks/LongPlaytest.log is the baseline. Its branch 4a64004cea14e2cc32191b5bd889c7ba892ee0c7 has no Java/resource diff from main 6114b00f8. No main rerun was made.
- All 486 playtest log lines match after removing the JVM-option banner, the two runtime measurements and wall-clock prefixes on the two vault-land messages. Both normalized SHA256 values are 01a23171750932ce03e3ee4f8920d4a3c542fa39bec532b1c58ed9716533ffe4. The initial diff contained only those two timestamps; the orchestrator was told before proceeding and then told the trace is unchanged. No ensemble is warranted.
- The kept directory also holds BK2b-holders-before.log and BK2b-check-before.log (the 16 failing regression assertions before the repair), BK2b-trace-comparison.txt, and BK2b-compare-trace.py. Its sealed checks/HoldersCheck.log contains the final green fixtures.
- Required stranger money review: REV-BK2b-1 was started by the orchestrator on b09109cc; verdict requested after the green dry run. The provisional marker remains pending that response.

# PD1b - PD1's review follow-ups (the drivers' petrol, OilCheck 20)

Built from main a3fb38c06 (R0h, 0.9.121). Scope: OilCheck 20 and the
comment beside Game's petrol subtraction. No executable model code, saved
field, format, or order of the month changes; no new getter or hook.

## Findings and calls

- ★ Observe the rebuild through Game's existing package-visible
  refreshHouseholdAccounts() override seam. The fixture calls super first
  and captures shopping and fuel before restoreStatement can replace them.
  Compare shopping with the saved household shopping, and rebuilt fuel with
  the saved Motoring bill. Those are deliberately different months from the
  saved household fuel: do not change the model to make them coincide.
  The intact-save check separately verifies the restored household lines.
- ★ Keep an active negative control: remove only Retail's PETROL sold
  detail before writing a second scratch save, restoring the live detail
  immediately afterward. Its saved household statement is unchanged.
  The same comparison used for the intact save must reject its rebuilt
  shopping, which is high by exactly the petrol. This proves that restoring
  the carried statement cannot make a broken rebuild pass the check.
- ★ Count the homes' lamp oil and coal in the footing, rather than requiring
  their bill to remain zero. Read the household statement's own home-fuel
  line, which is the month of the other lines being added. After retaining
  the original driving-town fixture, turn on both home-fuel shares and play
  two months: both goods sell, their bill reaches the households, and the
  complete footing closes against Retail's sales plus the duty.
- Correct Game's comment: the monthly update's petrol subtraction and fuel
  line refer to the same month; on load the carried Motoring bill is a month
  ahead of Retail's statement until the saved household statement is restored.
  No source change outside the check and that comment.

## Evidence on unchanged main

An uncommitted diagnostic compiled against main's classes in target/pd1b
used the same driving-town fixture and existing refresh override. Main's
original seven lines all passed. Logs: target/pd1b/main-probe.log and
target/pd1b/main-controls.log.

- Saved shopping and rebuilt shopping: 7.476534616515927k each.
  Saved household fuel: 0.007176767096213547k. Rebuilt fuel and saved
  Motoring bill: 0.014399672535020348k. The override ran twice on load;
  the final refresh is the one immediately before restoration.
- Missing-petrol control: the old restored-lines assertion still passed,
  while comparing the rebuilt shopping failed: 7.481816700025034k
  against 7.476534616515927k, a gap of 0.005282083509106528k,
  the petrol sale 0.005282083509106356k.
- Nonzero home-fuel control: the old footing failed, 8.699952296641555k
  against 9.227163886228983k. The missing 0.5272115895874272k
  equals the model's home-fuel bill 0.5272115895874281k; adding the
  household home-fuel line passed.

The fixtures retain the duty, the 600-house driving town, its petrol
purchase and original following month. The extra cases cause the missing
detail and nonzero home-fuel conditions rather than relaxing a premise.
Every monetary expectation comes from the model's own figures.

## Checks

- Initial ./batch-check.sh --fast on unchanged main: exit 0, maps current,
  Stale 0 firm findings, tree complete.
- ./batch-check.sh OilCheck Forms1900Check SaveFileCheck TimingCheck:
  exit 0, all four harnesses passed (72 seconds of suite time); OilCheck's intact
  rebuild, offset/restoration premise, missing-detail control and nonzero
  home-fuel footing all passed.
- sh tools/pick.sh selects ALL: Game has 172 readers. No local ALL run:
  the required final --pick runs inside the fresh-clone launcher dry run.
  The automatic review line says no money, saves or threading in the diff;
  a load-path review is nevertheless requested explicitly as the brief
  requires, because the check now observes the save rebuild.
- Final sh tools/gate.sh PD1b --dry-run: launcher exit 0, inner
  ./batch-check.sh --pick exit 3. 194 gate-tier harnesses ran in 2,607
  seconds: 188 passed, six failed only on existing known-red lines
  (DeathRecordCheck, InvestCheck, KeptCheck, MapCheck, MoneyDatesCheck,
  ResearchViewCheck). OilCheck passed, including every new fixture.
  unnamed.txt is empty; Stale has 0 firm findings. The suite used its default
  pool. Eleven timing assertions were soft and four existing path assertions
  observed, as the normal gate reports.
- No known-reds entry named PD1b when this batch began; no transfer is needed.


## Final launcher and trace (2026-11-16)

Kept run: C:/Users/Jerus/AppData/Local/Temp/cbs-gate-PD1b-20261115-2210.
The two tested commits are branch
37618a1528eae06cbe909ff0eb4288cdf373f024 and main
a3fb38c06de26cf44e462597e48278413b31300a. The fresh-clone launcher ran from
a detached hidden Git Bash process, native root PID 27432, which survived
the launching tool call. tools/wait.sh waited through completion. No branch
push occurred during the run.

ship.log, checks/, the eight trace-*.csv files and trace-compare.txt are
kept there. evidence/ also keeps the unchanged-main diagnostic source and
both main logs, plus the focused-check log. The launcher completed its
checks in 2,680 seconds and exited 0. Its full inner --pick run was the
batch's ALL run; no local ALL run or main playtest was repeated.

Every CSV is byte-identical to
C:/Users/Jerus/AppData/Local/Temp/cbs-gate-R0h-NrRv6F33/traces/trace-*.csv:
bank, bonds, borrow, build, house, labour, month and pop, eight of eight,
zero mismatches. The default trace did not move, so no ensemble was needed.

The orchestrator read and accepted fdd6ae7e on 2026-11-15: the load path is
unchanged and Game changes only its comment, so no additional Codex review
was required. Before the dry run, origin/main was fetched and merged again:
already up to date at a3fb38c06. The final go explicitly retained the
existing named results because neither main nor the source had moved.
After the run only these notes and the ship file advance; the provisional
marker is removed last in its own commit.

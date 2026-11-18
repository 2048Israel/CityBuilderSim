# TW1 - TreasuryCheck's dollar-note fixture causes its own window

Harness only. No model change: `src/main` is untouched, the default trace is unmoved, no ensemble.
`TreasuryCheck.java` is the one source file (`git diff origin/main --numstat`: +129 -18).

## 1. What was found

Section 7 of `TreasuryCheck` ("the surplus city, exporting: a dollar note rolls abroad, in dollars") asked its city
for a flat D$500k six-month dollar note at month 60 and asserted `foreignWindowOpen()` as the note fell due at month
65. Nothing in the fixture made it open. The window's own test, `DebtManager.foreignWindowReason()`, shuts it on four
conditions: a default abroad in the last 60 months, owing dollars while selling nothing abroad, owing more than
`WINDOW_SHUT_EXPORT_YEARS` (14) years of exports, and **the next year's service on what is owed abroad being more than
`WINDOW_SHUT_SERVICE_SHARE` (.45) of a year's exports**. The fixture's note was a fixed size and the exports are the
path's, so the window was whatever the path gave (TQ1's notes, 5: shut on dc8f4da6, "65% of exports already goes to
foreign creditors", five assertions red; open once DQ1 and CU2 had changed the path).

The shape of the same fragility, measured on the two trees with the final fixture:

| tree | exports a year, month 60 | the old fixed note (service, as a share of them) |
|---|---|---|
| main 2fc5f02c (0.9.145) | 219.87k | 12% (26.91k abroad: open) |
| dc8f4da6 (TQ1's path) | 38.40k | about 65%: shut |

The path moves the exports nearly six-fold between the two trees; the fixture's note did not move at all. (The
shares are read off the runs: main's 26.91k abroad over a year of exports of about 219k; TQ1's notes' 16.26k, which
they print as 65%, so about 25k of exports at month 65 there.)

## 2. What changed

All in `rolling()`'s last part, and three helpers beside it.

- **The note is sized from the city's own exports**, in `dollarNoteAtShare(g, of)`: the face is the share `of` times
  `DebtManager.WINDOW_SHUT_SERVICE_SHARE` of a year of the city's exports (`getMonthlyExports() * 12`), converted
  to dollars at the day's rate, so the service on it is that share of what the window reads by construction.
  `serviceShare(g)` is the window's own figure (`nextYearService()` over a year of exports), read through the model's
  getters; the assertions compare it with the model's constant.
- **Two twins.** The city at month 60 is saved and reloaded (`GameFiles.scratch`'s folder, as the section's own
  reload does) into a twin, asserted the same month, the same exports and the same D$. `OPEN_SHARE = .5` sizes the
  city's note to half the shut line, `SHUT_SHARE = 2` the twin's to twice it. Both are dials of the harness, named,
  with the sentence above them.
- **The D$ is pinned in both** (`ForeignAccounts.pinRate()`, 9d's own lever). The first run of the sized fixture read
  22.5% of exports at the issue and 30.0% as it fell due, the exports not having moved: the service is owed in dollars
  and is read in local money at the month's rate, which moved a third in five months. With the D$ held the share at the
  maturity is the share the fixture set: 22.6% in the city, 90.4% in the twin (main's path).
- **The twin's case is asserted whole**, as the city's: its service is over the constant, its window is shut and the
  reason is the service share ("90% of exports already goes to foreign creditors") and no other; the plan issues
  nothing abroad and rolls the one dollar note at home as a 6-month note of its term, saying why (`Issue.why`);
  the press issues that note, pays the dollar note, leaves the city owing no dollars, covers what fell due, logs
  "the window abroad is shut" and closes its audit. This is the model's own handling of a shut window, the one the
  deficit city's `roads` fixture already asserts for a window shut a different way (owing dollars, selling nothing).
- **Two cause-guards the open and the shut sides both need:** the rollover issues a dollar note only when the dollar
  piece's share of what it rolls is at least `Game.minimumIssueSize()` (max of $50k's worth and 5% of a year's tax
  income), so both twins assert `dueAbroad() > minimumIssueSize()`; without it a note sized too small is skipped
  by the rollover, and the assertions that follow it fail for a cause that is not the window's. On main's path the room is wide (the smallest issue 4.17k against 49k). On dc8f4da6's it is narrow
  (the smallest 4.70k, the open note 8.65k abroad, the shut line 17k a year of exports at the issue): the .5 share sits near the geometric middle of
  that room, which is why it is .5 and not smaller.
- **The year's surplus is spent in both twins** (`spendTheYearsSurplus`, the existing block moved into a helper
  unchanged), so the note rolls whole in each.

Every law the section asserted before is kept, word for word: the window open as it falls due, the surplus netted
already, the dollar note rolls abroad as a 6-month dollar note, issued the month before, the one falling due paid and
the new one owed in dollars, its cash covering what fell due, a bigger dollar face, the audit. The premise is the same
(a city selling abroad rolls a dollar note abroad); only what the fixture sets changed.

## 3. Proof that each new assertion has its cause

Each run is the harness with one cause removed, on a scratch copy of the source (`target/tw1/var-*.txt`), the rest as
it is; every run exits 1, and the lines are the new assertions:

| cause removed | result |
|---|---|
| the city's note sized to 2x the line (`OPEN_SHARE = 2`) | 6 FAILED: "service under the share", "the window is open as it falls due", and the four that follow (rolls abroad, issued abroad, paid and owed in dollars, a bigger face) |
| the twin's note sized to .5x (`SHUT_SHARE = .5`) | 8 FAILED: "over the share", "window shut by that share", "issues nothing abroad", "rolls at home", "says why", "issued at home", "paid, owes no dollars", "the log says why" |
| the city's note sized to .01x (below the smallest issue) | 5 FAILED: "more than the smallest issue the rollover makes", and the four that need the dollar issue |
| the twin's note sized to .01x | 7 FAILED: the guard on the shut side, "over the share", and the five that need the shut window |
| the twin's save not taken (the slot held month 16) | 9 FAILED: "its twin is the same city" first, then the shut side's lines |

## 4. The fixture on the path the row was about

The row's own condition, run: the new `TreasuryCheck.java` copied onto a scratch checkout of dc8f4da6 (TQ1 before main's
0.9.130; built in `AppData\Local\Temp\tw1-tq1`, removed after), `target/tw1/TreasuryCheck-on-dc8f4da6.txt`. There the
exports at month 60 are 38.40k a year (main's 219.87k); the notes sized to the line read open at 34.8% as it falls due
and shut at 139.1%, and the harness exits 0. Where its old fixture failed (5 FAILED, the window shut), it passes.
The open side's margin there is the path's own: its exports fell by about a third in five months with the D$ held; the
share went from 22.5% to 34.8% against .45. A path that halved them in the five months would shut the open side's
window; the one on dc8f4da6 came within 1.3x of that. That is the one thing the fixture does not cause (`setTrade` is handed down by the game every month and a
fixture cannot hold it), and the assertion "fixture: the next year's service on the note is under the share of exports
the window shuts at" names it if it ever happens, with the figure in the line above it.

## 5. Checks

- `TreasuryCheck` before the change on main's tree: exit 0, 188 assertions OK (`target/tw1/TreasuryCheck-base.txt`);
  after: exit 0, 205 OK (17 added: 5 in the shared setup and the city's side, 12 in the twin's section).
- `./batch-check.sh --fast`: compile OK, maps regenerated (committed), Stale 0 firm findings, tree 4 changed, all of
  them generated docs.
- The pick (`sh tools/pick.sh origin/main HEAD`): 7 of 219, not ALL (so no dry run, no `--no-tag` run), review not needed: CallCheck, DebtExchangeCheck,
  LongPlaytest, SaveFileCheck, ScaleCheck, TimingCheck, TreasuryCheck. Each run by `./batch-check.sh <name>`, one at
  a time:
  - TreasuryCheck 0, CallCheck 0, DebtExchangeCheck 0, SaveFileCheck 0, TimingCheck 0.
  - ScaleCheck 3: its one red is "...savings: the copy is K times the city's within 0.0160 in all 6 months (worst
    2.83e-02)", the HP2 entry of `known-reds.txt` and a line of `gate.txt` (worst 2.83e-02, the same figure). Not this
    batch's: the file has no change under ScaleCheck's path.
  - LongPlaytest 0 (04:18-04:32, 14 minutes, the default 2,400 months).
  - No run was kept for judging and none is named: the pick is not ALL, so the batch has no dry run.

## 6. For Jerus

Nothing. A row that queued this was the orchestrator's call (2026-11-17). Nothing queued from this batch.

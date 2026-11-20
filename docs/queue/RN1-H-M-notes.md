# RN1-H-M - the old Industrial Bakery that moved

No box moved. The oldest-works watch (LongPlaytest, THE OLDEST WORKS, CL3) was short a term, as HL1-M's law was:
it took the oldest of a district's own count to be boxes the district drew, and district (0,0)'s oldest bakery had
been sent on by its plan at its arrival. The watch now counts the works sent on: of the oldest `lowest since` of the
district's own live instances, those its plan had sent on are not its to stand. CL3's newest-first rule stays, and
so do the run and spill exclusions, the seed and the hard finding (the orchestrator's go, mail
20261120-165403-608). Untagged: a harness change and one `CityMap` accessor nothing in the model reads. MapCheck 26
causes the event: the law as it stood flags it, the law as it stands does not, and a drawn old box that really
moves is still flagged by both.

## Main no longer names the line

Every gate on main since OP1's (2026-11-20 01:36 and 03:01, main 24b7bfcbc with OP1) prints "Nothing. Every month
passed the audit" for the default playtest, as do BO1-S's and FB13's to FS15's (FS15's on main 12a9e7f03). The last
gates that named it were 2026-11-19's: RN1-H's, HS1's, QN1's and R1e's (19:22, main 7117fef6b, "holds 5"). The
cities that came after are other cities (the model batches between moved the trace); the watch's gap is in its law,
not in the city, so it is mended rather than waited out. The ship file carries no `## known-reds` section: the
registry's row names RN1-H-M as its fix, and `ship.sh` takes out the batch's own rows at its merge
(`sh tools/known-reds.sh RN1-H-M` marks it `! `).

## Found (the city of d05201e11, RN1-H's merge; the code is main's fa889c423, before this batch)

- Reproduced on d05201e11 (RN1-H's merge, the brief's pair), the real `earth-2.bin` and `world-1.bin` named, the
  default playtest with a probe that planned district (0,0) every month from 590 and dumped its plan inputs about
  each change (not committed). Its one finding is the brief's, word for word: "district (0,0), Industrial Bakery: 1
  of the 1 standing at m601 ... holds 4 ... the model's count of it 2 then, as low as 1".
- The bakery (type 3) in (0,0)'s log: m43 +1, m365 +1, m744 +1, m745 +1, m906, m907 and m908 -1 each, m938 +2,
  m939 +1, m1508 +1.
- m43: the district had one cell open, for homes; the bakery found no estate cell, so its plan sent it on at its
  arrival (CL3: an arrival with no place goes on the overflow log, the next district's carry). The pair {43, 3} is
  on (0,0)'s carry-out at m601 (pair 83 of 170).
- m365: the second bakery opened an estate cell and stood at (6,17), the one bakery box (0,0) drew at m601: the
  watch's old box. The model's count then was 2: the drawn one and the one sent on.
- m906-908: the count fell 4 to 1, and CL3 took the newest of the type each month: m745's box at (24,1), m744's at
  (20,1), m365's at (6,17). m43's stayed, still sent on, so (0,0) drew no bakery from m908 (the probe's plan of the
  district from its own inputs, month by month: [] with 1 sent on).
- m938-939: the bakeries that came again stood at (20,1) and (24,1), the places m744's and m745's had left, and at
  (16,2); m1508's at (20,4). (16,2)'s is drawn at (1,25) from m2114, outside every window, and it was no old box. At
  m2399 the district drew 4 against 1 then, so the watch judged the type held, and of its kept 1 (the least of 1
  drawn, 2 counted and 1 lowest since) none stood where it had.
- The law, "a departure takes the newest of a type, so the oldest of the district's own stand while its count never
  fell below them", holds for the district's own count, and its own count includes what its plan sent on. The
  lowest since was 1, and the 1 oldest was the one sent on: the district had nothing of its own to keep standing.

## What was built

- `LongPlaytest`, THE WORKS SENT ON: at the kept month, for each type a district draws an industry box of, its own
  live instances by arrival month (its log replayed, departures taking the newest: `layoutLiveMonths`) and the
  months of the pairs its plan sent on and has not taken back (`layoutSentMonths`, from the carry-out). At the end,
  `s = layoutSentAmongOldest(live, sent, lowest since)` and `kept = min(had, count then, lowest since - s)`. The
  finding's detail names `s`; the report's oldest-works line counts what the term took out ("its plan had sent
  on, not its to stand"). `layoutOldForget()` clears the kept state, so a harness can keep twice.
- `CityMap.carriedOnLog(District)`: the district's carry-out as {month, code} pairs (its link's `out`), cloned.
- MapCheck 26, `oldWorksSentOn()` (fixture `SentOnCity`: `CityLand.restore` + `CityMap.canonical`, no Game). One
  district on the default world's dry ground, one cell owned round the founding site: m10 12 houses open it for
  homes; m11 a bakery finds no estate cell and is sent on; the next cell is bought; m12 a second bakery stands in
  it; kept at m13 (1 drawn, 2 counted, 1 sent on); m14 the count back to 1 while a works of the bakery's size takes
  its place; m15 a bakery again, elsewhere. The law as it stood flags it, as it stands not (1 of the oldest kept sent
  on). The control keeps at m13, asserts the term is 1 there (the older bakery sent on; REV-RN1-H-M's nit), and
  moves the drawn bakery a plot while the count holds: both laws flag it; the boxes as drawn are no
  finding by either. Three checks pin the term's arithmetic, ties included.

## The trace

I ran the default playtest (`LongPlaytest`, seed 0, 2,400 months, the real earth and world named,
`-Dplaytest.trace=<prefix>`) twice, one JVM each, one after the other: on `origin/main` fa889c423, built in its own
folder, and on this branch's classes at 2fa82fcc7, snapshotted before the run. All eight trace files are **byte for
byte identical** (`cmp`): month 924,572 bytes, borrow 2,811, house 855,560, pop 423,017, bank 894,568, bonds
5,225,176, build 645,180, labour 746,860. The month file has 2,405 rows and one SHA-256 for both
(`1116241283632bd20c15f52c...`). The reports differ in 12 lines:
- runtime and identity: one `LandParcel@...` hash inside a Step's `toString`, 260 s against 285 s, the windows' 6.3 s
  against 7.5 s, and two log lines' clock stamps;
- the one change: the oldest-works line gains "; 0 its plan had sent on, not its to stand".

Both runs exit 0 with "Nothing. Every month passed the audit". On main's city of today the term takes nothing out:
1 of 5 of m601's industry boxes stand, and 4 moved off a run. No ensemble is owed, since the trace did not move.
Files: `target/rn1hm-trace/{main,mine}-*`. BI2 (0.9.169) and CPB2 (0.9.170) merged after the trace; they are not this
batch's, and its own delta (a harness and one accessor nothing in the model calls) is the same on them.

## Checks

- `./batch-check.sh --fast`: before merging main, after c60165591 (BI2's 0.9.169 and FS7), and after f4fb7dc65 (CPB2's
  0.9.170). None of main's commits touch this batch's files; the generated docs were taken from main and made
  again. Each time: compile OK, maps regenerated, Stale 0 firm findings, exit 0.
- `./batch-check.sh MapCheck` (it picks BridgeMapCheck): suite OK, 2 harnesses, exit 0, on the code, on the code
  merged with c60165591, and on the final code (with the nit) merged with f4fb7dc65. MapCheck has 967 OK lines, no FAIL;
  section 26's 8 assertions are all OK (the numbers above); section 2d, the other caller of the watch, is OK. The
  one `OBSERVED - not reached` (section 4's second bought block) is main's, as HL1-M found it.
- `LongPlaytest`: the branch's trace run above, exit 0, no finding.
- `./batch-check.sh SlopeCheck`: suite OK, 1 harness, exit 0. It is the one harness besides LongPlaytest that turns the watch on
  (`layoutOn`, 2,000 months), so it runs the new keep at m601; it never reaches the m2400 finish.
- The pick (`sh tools/pick.sh origin/main HEAD`) is 99 of 230, not ALL, so no dry run is owed. Review line: "not
  needed - no money, saves or threading in the diff". The pick is LongPlaytest's G (the harnesses that borrow its
  fixture builder), CityMap's readers, MapCheck's and TimingCheck; the list is in `target/rn1hm-pick.txt`.
  - Run: MapCheck, BridgeMapCheck, LongPlaytest and SlopeCheck.
  - Left to the gate: the rest. They never turn the watch on, never call `carriedOnLog`, and never call
    MapCheck 26.
- Not run: a dry run (the pick is not ALL) and an ensemble (the trace did not move).

## Review

The orchestrator sent the batch to a stranger, because it changes a hard watch's law and the term differs from the
wording first cleared. REV-RN1-H-M on 77baa2b0a: **SHIP, no P1, no P2.**
- s is exact and strict: the replay takes the newest first, ties count the drawn first, and a late or unmatched
  pair only lowers s. Districts with a carry in are skipped before the term.
- Section 26 causes the event through the map's own inputs. Its control is exactly the case of a drawn box that
  moved, and both laws flag it.
- `carriedOnLog` is read only by LongPlaytest and MapCheck.

One nit, taken (one line): the control should assert s = 1 itself, not only through the event's `!newLaw`. It now
does (`controlTerm == 1`, read from the kept state the watch judges by). The orchestrator's go followed, untagged,
with the pick at 99 and so no dry run.

## Calls

- ★ The watch's term, not the placement. The cause was the watch's own accounting, so I mailed the evidence before
  touching it. The orchestrator's go (mail 20261120-165403-608): the term, CL3's newest-first rule kept, harness-only
  and untagged. A placement change (sending on the newest, or placing a sent-on works again when ground opens) would
  move every district's plan after it, and nothing was wrong with the plan.
- ★ The tie rule, corrected from my mail. The mailed wording, "the live instances sent on strictly older than the
  lowest-since-th oldest, ties counted as drawn", gives 0 on the real case, because m43 is itself the oldest one,
  not older than it. What was built counts, of the oldest `lowest since` live instances, those sent on. Within a
  month that holds several of the type, the sent-on count is held to what that month's numbers force: with `cl`
  live and `cs` sent in the month and `take` of them inside the oldest, s += max(0, take - (cl - min(cs, cl))). This
  is the fewest the plan could have sent on, so the term never excuses more than it must (MapCheck 26: two of one
  month, one sent on, gives 0 for the oldest one and 1 for the oldest two).
- ★ `CityMap.carriedOnLog(District)`, one public accessor (a clone of the link's `out`). Nothing in the model reads
  it, the same as HL1-M's `stationLine()`. The watch needs the carry-out, which no accessor gave.
- ★ `LongPlaytest.layoutCountsSentOn` (true in play) lets MapCheck 26 judge one city by the law as it stood and by
  the law as it stands in one run, so "fails before the fix" is shown, not claimed. Also, `layoutName` takes a null
  Game (the fixture has none), and `layoutOldForget()` clears the kept state.
- A works moved off a run and sent on (MF1) is dated by the run's month in the carry-out, not by its arrival.
  Counted at that later month, it falls inside the oldest no more often than it should: the term can only be
  stricter for it, never slacker.
- Not touched: the model, `DistrictPlan`, CL3's rule, the run and spill exclusions, the seed, `Game`, a save,
  `SAVE_FORMAT`, `CityMap.FORMAT`, QUEUE.md, the changelog, gate.txt.
- The probe that found it (a per-month plan of (0,0), its inputs dumped) stayed outside the tree, under
  `%TEMP%/rn1hm-probe`. The repro worktree at d05201e11 was removed.

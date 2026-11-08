# RE2 - Real Estate's workers, back in (0.9.54, 2026-11-08)

Jerus, 2026-10-28: "gotta add workers to real estate". The posts as HD1
built them (fec704a7, reverted in f3704d8d), now that RE1 found the stall was
HD1's town. The world lane, after TP1. Saves unchanged; the trace moves.

## 1. What went back

fec704a7's `buildings.json`, `BuildingManager`, `Sector`, `sectors/RealEstate`
and `BuildCard`, and its edits to `HousingCheck` (section 6) and
`SectorStatementCheck`. A Studio Apartments (80 units) and a Low-Rise
Apartments (63) each have one office post and one maintenance post, both
DIPLOMA, by the National Apartment Association's staffing rule; a House has
none. The landlords do not ask the staffing gate
(`Sector.ordersOnlyWhatItCanStaff()`, false for `RealEstate`): homes come
before workers. The build card follows, and `rentBreakEven()` carries the
payroll. The patch applied cleanly on 0.9.50 but for one hunk in `Sector`,
where IC1's banner (an idle builder's crews are spare) now stands. Both are
kept, HD1's method first. HD1's `HistorySave`, `YearBook`, History screen,
SaveFileCheck and EducationCheck edits stay out, as the brief says.

## 2. The harnesses

With both data files named, as the gate runs:

- **EducationCheck's surplus fixture holds with the posts** ($401.9k
  surplus). WN3's month-at-a-time stepping (its comment above the line) now
  causes it, so it needed no change.
- **HousingCheck section 6, re-caused.** Left free, the landlords (who never
  ask the gate) build their own Studios and Low-Rises in the fixture's 24
  months: 1,148 people, 493 posts, 1,598 beds, and the city could staff all of
  a Studio. Held through the build-up and released before the plan is asked,
  the city has 106 people, 453 posts and 172 beds, every band has more posts
  than workers, and 0% of a Studio's posts can be staffed. The plan then
  orders 6 Studios. No assertion or dial moved.
- **SectorStatementCheck and BuildingDataCheck pass** with HD1's edits.
- **BuildCardCheck is red only with the data files unnamed**, with or without
  the posts (section 11's Bakery: "at today's prime ... passes every gate").
  A batch worktree has no `data/`, so `batch-check.sh` there runs it without
  them unless `JDK_JAVA_OPTIONS` names them. Main's gate names them, and it
  passes there.
- **ScaleCheck section 9: not the posts' own, traced to the bank.** With the
  posts, section 6's town at 488 passes its fixture (main's own red there) and
  misses section 9's people (1.69e-2 against .015), cash (1.57e-2 against
  .0125) and prices (4.78e-3 against .004). The landlords' flows scale K-fold
  to 0.2% (posts 14 against 14,000, filled 1.0 in both; payroll, rent, floor,
  interest and tax). In month 1 the 1,000-times copy's bank closes 975 of its
  1,000 branches, while the city's one is never the last
  (`branchesTheFeesCover()` is at least 1). The plots sold back to the
  treasury are 24,944 of the 27,551 a K it is short, the cash line. The
  12,125 jobs the branches took part the people and the prices after. Of 29
  months scanned (455 to 525), none holds every line: cash misses in all
  (1.29e-2 at best, at 483), and the fewest misses are two (480; 515). The dial
  stays at 488 with a paragraph for RE2. The finding is added to ST3's brief:
  section 9 should hold the branches in both copies, as SK1 holds the
  sectors. Main's eight red lines become these three, so `--pick` tags them
  NEW by text; they are a known class (FREE_FIXTURE_MONTHS, BA1 + R0's
  paragraph), outside this batch's code.

**`--pick` on the merge with main ab904e13: exit 4.** 113 harnesses ran; the
four files with the most readers were left to the gate, with LongPlaytest run
in their place. Every NEW line is a red of main's, on the posts' town, or
load. Three harnesses were run again on the same build with the posts taken
out of the compiled `buildings.json` alone. There MapCheck and KeptCheck read
main's recorded lines word for word, and ReplayCheck passes:

- **ScaleCheck**: the three lines above (ST3).
- **PriceLevelCheck**: one line where main has dozens. Retail's cash is 9.6e-9
  apart over the factor, against 1e-9: main's factor class, smaller.
- **MapCheck 2d** (a run laid across a district's buildings): main's two yard
  lines, with this town's figures (71 of 520 boxes under the yard, 77 not in
  the plan after; main 72 of 530 and 78). Two more lines are the same fault
  on the same town: a yard that goes moves 22 of 726 boxes, and a yard laid
  again moves 121 where 67 lie under it. That is DM2's class (BA1's notes).
- **KeptCheck**: four lines where main has eleven. Section 92's city at
  m4,812 has no wood works drawn ("Construction, null"), which is KF1's
  queued fixture. Two node-share lines (11 of 25 months make no node; 209 of
  233 nodes handed back) are KC1's class, and the header signature is
  main's own line.
- **ReplayCheck**: "asks 1" under `--pick`'s three JVMs at once. It passes
  alone with the posts (33 s).

## 3. The ensemble

**Scored like-for-like:** neither data file named, as 0.9.50's baseline ran
(its logs: "world-1.bin not named ... trade on the literals"). The score is
**23/26, main's count**. Two verdicts swap:

- HIGH R3 now fails: 3 seeds over +1 point against 1, median -0.97% against
  -2.92%.
- SHOCK T2 now passes.

AUTO2 A2 fails as on main, 12 [7 to 13] against 10 [5 to 15]. T1 is 16/16,
the median at m1,118 against m1,090 and the latest at m1,486 against m1,239.
The AUTO2 final populations are 0.59 to 1.35 of main's, median 0.99 (RE1 on
0.8.77: 0.63 to 1.31, median 1.02). No seed stalls. The payroll is 8-15% of
the landlords' rent at the end (RE1: 7-9%). `docs/ensemble/RE2.md` is this
run.

**RE1's trap: seed 3 shows it, short of the staffing gate.**
`docs/queue/RE2/trap.py` flags a run where, over some 120-month stretch, the
landlords lose money in half the months while the population moves less than
5%. Fourteen runs are flagged. The flagged AUTO2 seeds and the slowest seed
were replayed with RE1's probe on both models, m600 to m2,400:

| AUTO2 seed | loss months, main / RE2 | down-payment holds (longest run), main / RE2 | months with 3+ sectors at the staffing gate, main / RE2 | final, RE2 over main |
|---|---|---|---|---|
| 1 | 0 / 204 | 1 (1) / 15 (8) | 27 / 71 | 0.92 |
| 3 | 0 / 125 | 0 / 70 (59) | 0 / 2 | 0.88 |
| 11 | 0 / 526 | 7 (7) / 1 (1) | 0 / 6 | 1.01 |
| 13 | 0 / 0 | 0 / 4 (3) | 0 / 23 | 0.59 |

On seed 3 the landlords lose money from m980 (-418 before tax on 2,359 of
rent, with a payroll of 313 and a property tax of 1,033). Their till is empty
from m1,100 to m1,220, they hold for the 15% down payment 59 months running,
and the town sits at 19,600-24,000 for 160 months while main's goes from
21,616 to 51,953. It recovers when the till refills (m1,260). That is RE1's
chain without its last link: the staffing gate does not hold the builders
(IC1's spare crews). Seeds 1 and 11 lose money without the stall. Seed 13's
lag is not the landlords': it has no loss month. **So RE3 is queued: RE1-T's
way (a), the payroll billed with the rent at once, with seed 3 as its
fixture** (`docs/queue/RE3.md`).

**The run with the data files named** (info; no baseline is scored that way)
scores 24/26. A2 is 18 [12 to 42]. Seven seeds' longest spell starts at
m2,186 to m2,190; in seeds 1 and 6 it follows the currency falling 26-42% in
six months. Seed 8 there
(replayed on both models) has the landlords at a loss in 250 of the 351 months
from m1,950, against none on main. It has 26 down-payment holds and the town
grows throughout, 630,132 at the end against main's 686,451 with the world
named.

## 4. ★ Calls taken, on the recommendation

- **★1 ScaleCheck section 9 left at 488 and red.** The branch finding goes
  into ST3's brief; there is no harness change here. Recommended: this batch
  is the landlords', the miss is not theirs, and ST3 rebuilds the section's
  town.
- **★2 The ensemble scored like-for-like, without the data files.** The named
  run is kept as information. Recommended: a score against 0.9.50's baseline
  must run as it ran.
- **★3 The posts ship as built, and RE3 is queued with seed 3 as its
  fixture.** Recommended: RE1-T as settled 2026-10-31, "(d) until a seed
  shows it, then (a)". The loss is the trap's first link, and seed 3 has it
  with the down-payment hold and the flat town.
- **★4 For the orchestrator, not taken here: the ensemble does not name the
  data files in a worktree.** `ensemble.sh` has no `data/` there, so its runs,
  and the baselines, trade on the literals without the world market, while the
  gate and `nightly.sh` name `earth-2.bin` and `world-1.bin`. Recommended: a
  small process batch that names them in `ensemble.sh` as `nightly.sh` does,
  with one baseline taken under the new setting. Under the world market the
  posts' runs read differently (A2 above).

## 5. How it was done

Opus applied the posts, wrote the briefs, reviewed and ran the ensemble and
the probes. One slice on Sonnet 5.5 at xhigh did HousingCheck's fixture, and
its diff was reviewed as a stranger's. One Opus agent at xhigh, in a worktree
of its own, traced ScaleCheck section 9 and scanned its months. Two ensembles
of 80 runs were run (named 15.1 min, unnamed 12.1 min), plus ten probe
replays. `--pick` ran on the merge with main ab904e13 (MF2 in): exit 4, every
NEW line traced above.

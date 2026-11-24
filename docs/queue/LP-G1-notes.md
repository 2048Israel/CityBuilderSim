# LP-G1 - keep ground ahead across the player's long skips

Builder: Claude (Opus), headless, started by the orchestrator 2026-11-23, on `batch/LP-G1` cut from origin/main
0c4249711 (0.9.194, PW2, SAVE_FORMAT 63). The brief: [docs/queue/LP-G1.md](LP-G1.md).

**Untagged.** The change is in the reference player (`LongPlaytest`), the ensemble's controller
(`tools/ensemble/Ensemble.java`), a new harness and its line in `AllChecks`, and `docs/tree.md`: no model class, no
save field, no balance file. The ship file's heading has no `{next}`; the dry run runs with `--no-tag`.

## 0. Scope

- **The player looks at its ground across a skip.** `LongPlaytest.keepGroundAhead()` holds room for
  `BuildAdvice.HORIZON` (6) months of the businesses' growth with `BuildAdvice.SLACK` past it, and ran only at the
  advisor's looks, six to a hundred and twenty months apart. Now a skip runs month by month and makes the same look
  whenever HORIZON months have passed since the city's last one.
- **Unchanged:** `keepGroundAhead()` itself (its line, its best value, `GROUND_AHEAD_CASH_SHARE` = 0.10 of the cash
  at each look, its hold), the skip's lengths, the advisor's looks and moves, the housing finding, every housing and
  money bound. The cash-share dial is not raised.
- **Both integration windows**, each traced before and after (§5): the default playtest's seed 0 (m792-807 in the
  brief) and the monetary ensemble's seed 9 (m605 in AUTO2, ZERO_SLACK, SHOCK and HIGH).

## 1. What changed

- **`LongPlaytest`, banner THE GROUND, WATCHED ACROSS A SKIP (LP-G1, 2026-11-23).** `skipWatchingGround(g, months,
  month)` runs `months` months through the caller's own month and, before any month it falls due, calls
  `keepGroundAhead(g)`. Due: never looked at, or `BuildAdvice.HORIZON` months or more since the last look.
  `keepGroundAhead()` records its own month in a weak map by city, so an advisor's look counts as one too. The
  callers are `main()`'s loop (`runPlayer`) and `earthCity()`'s; `probePlay()` is not one (★5).
- **The report** gains a line under "ground kept ahead (0.7.58)": "...and across the skips (LP-G1): N ground look(s)
  inside a skip; the most months a skip let the ground go unlooked at, M (the horizon 6)". The default run: 367, 6.
- **Diagnostics, read-only.** `-Dplaytest.groundWatch=off` plays the skip as before (the before-traces; to the byte
  main's, §5). `-Dplaytest.groundTrace=FROM-TO` prints to stderr an `LPG1_MONTH` line each month of the window
  (unplaced, homes, on site, used share, line, free ft², months since the look, held for land, cash, the housing
  order) and an `LPG1_SKIP_LOOK` line at each look inside a skip, with PW5's ground lines of those months.
- **`tools/ensemble/Ensemble.java`**: its skip goes through `LongPlaytest.skipWatchingGround(..., () -> run(g, 1))`,
  as `LongPlaytest.main()`'s.
- **`GroundSkipCheck`** (new; `AllChecks` after FB13Check; `docs/tree.md`), §4.

## 2. The calls (★)

1. ★ **A timely look, not a reserve sized to the skip.** The brief allows either. A reserve sized to a sixty- or
   hundred-and-twenty-month skip rests on the businesses' forecast (`BusinessInvestment.growthFactor`, linear in the
   months), and the forecast's error grows with the months it is asked to cover; measured, at m743 in the seed-0
   replay it understated the next sixty months' use about threefold. A look each HORIZON months re-measures. It
   also buys only what each horizon needs, where a skip-long reserve is the city's money in ground years ahead.
2. ★ **The cadence is `BuildAdvice.HORIZON`**, the horizon the reserve is sized to: look and reserve agree by
   construction, and no new dial.
3. ★ **The cash share per look is unchanged.** Over a long skip the player looks more often, so it may spend its
   share at several looks; each look spends at most `GROUND_AHEAD_CASH_SHARE` of the cash it then has
   (GroundSkipCheck §2 asserts it look by look). The brief's own remedy, "a timely look", is this.
4. ★ **The hold stays.** An offer past the share still waits: nothing bought, paid or placed, the next look HORIZON
   months on (GroundSkipCheck §3; in the seed-0 after-trace every look m763-810 holds an offer of $190,000-$310,000
   against a budget of $94,000-$200,000, and the landlords' months held for land there follow those).
5. ★ **Who watches: the player, not the fixtures.** `main()`'s loop, `earthCity()` and the ensemble watch;
   `probePlay()` and the rhythms AuctionCheck, AutoBuildCheck, HomeRailCheck and SectorStatementCheck copy by hand
   do not. Those build the towns their harnesses wrote premises on. Measured with `probePlay()` watched: KeptCheck's
   town at m605 grew from 2,518 people to 21,945, its Bank Overview took a new sentence after the month ("$356.6B
   would take it back to its own target of 16.5%"), two of its redraw lines went red (79.4% and 81.9% handed back
   against nine in ten) and its 2 GB heap ran out at section 63 (`KeptBench.signature`); none of that is ground.
   KeptTimingCheck costs its pages on the same town. So `probePlay()` keeps main's rhythm.
6. ★ **A new harness**, not a LandCheck section: its fixture is the playtest's own town (`probeCity()`, then
   `probePlay()` to m240, saved once to `GameFiles.scratch` and loaded for each section), where LandCheck's are built.
7. ★ **The ensemble watches too.** The brief's second window is its seed 9, and its controller plays
   `LongPlaytest.main()`'s rhythm "line for line" (its own banner).

## 3. Old saves

None: no saved field, no format change. GroundSkipCheck saves its town to a scratch folder, never a slot.

## 4. The checks

All on the PC, one JVM at a time, with the data files named.

- **GroundSkipCheck**, 0 FAILED, 25.7 s. The town: the playtest's own (`probeCity()`, `probePlay()` to m245), 1,229
  people, 0.9146 of its ground built on against the line 0.9166, growth 1.039 over six months; built on to just
  under the line, it holds six months' growth by the businesses' forecast and not sixty's.
  1. The old skip (sixty months of `runPlayer()`, no look): past the line at m246, unserved (seven months or more
     since a look) at m252, the landlords held for land 17 of 60 months, first with no home on site at m283
     ("Holding: no land - needs 5,570 m², 4,850 m² free").
  2. The watched skip from the same town: 9 looks, 14 offers bought at m251-299, never more than six months
     without a look, each look's spending within `GROUND_AHEAD_CASH_SHARE` of its opening cash, the land office's
     dollars up by the offers' prices and the ground by their dry ground, the landlords held 0 of 60 months, and the
     audit of every month finding nothing the old skip did not.
  3. The unaffordable offer: skipped the old way until the landlords are held and a look is due, the cash set so the
     best offer is past the share and inside the cash; the look is counted short, buys, pays and places nothing, the
     next is due HORIZON months on, and with the share able to pay that look buys and the landlords are released.
  4. Two towns played in step look in the same months and end alike (the look is kept per city).
  - ★ Fixed in the harness on the way: `LongPlaytest.lastM0` is one static, and the harness plays several towns,
    so section 2's first month was read against section 1's last ("M0 moved by something other than the money
    made"). `GroundSkipCheck.play()` gives a town its own M0 when the month is another town's than the last; months
    of one town chain as the playtest's do. The audit itself is not touched.
- **LongPlaytest**, the default, green: "Nothing. Every month passed the audit and every reload matched." 1,377 s.
- **RankingsCheck, LandCheck, LandmassCheck, IslandLinkCheck, DeterminismCheck (`earthCity()`), ReloadCheck,
  MoneyWordsCheck, ClockCheck, ER1Check, EnsembleCheck**: green (ten harnesses, one at a time). Four of them
  (RankingsCheck, ReloadCheck, MoneyWordsCheck, ClockCheck) borrow `probePlay()` and were run with it watched,
  before ★5; on the final code their towns are main's again.
- **KeptCheck**, run with `probePlay()` watched: red (★5's two redraw lines and the heap). With `probePlay()` back
  on main's rhythm its town is main's and its run is main's.
- **CopperRefiningCheck**: section 11 plays the playtest's seed 1 for 2,400 months in a child JVM, which watches
  its ground now. On the PC the harness reached `AllChecks.HARNESS_SECONDS` (1,740 s) inside section 11 and was
  ended. So seed 1 was played alone, as the child plays it (target/LP-G1-seed1): exit 0, "Nothing. Every month
  passed the audit and every reload matched.", "copper works at the end: ... Rod Mill 0" (`ROD_MILLS_STANDING`
  is 1), 5,511,003 people at m2405, 366 looks inside skips, the longest gap 6; 1,647 s, inside the child's
  `ROD_CHILD_MINUTES` (28) and not, with sections 1-10 before it, inside the harness's 1,740 s on the PC. Section
  11's two lines pass on their merits; main's known red for it (CL7, seed 1's "industry stands farther out than
  the homes" at m2400) does not fire on the watched player. The box ran main's whole harness in 225 s at PW5's
  gate (§6).
- **`./batch-check.sh --fast`**: exit 0; Stale 0 firm findings.
- **Known reds.** Main's row `LongPlaytest | households with no home 16 time(s), months 792-807` names LP-G1; the
  gate retires a batch's own rows (GR2), and the default run is green. The ship file adds none.

## 5. The traces

### Seed 0, the default playtest

- **Before**: main's player, `-Dplaytest.groundWatch=off -Dplaytest.months=830 -Dplaytest.groundTrace=700-830`
  (target/LP-G1-off830). Its trace lines are main 0.9.194's (target/LP-G1-main, the full default run, green,
  657 s, 1,326,634 people at m2405) up to the 830-month limit. Ground looks m714/715, m741/742, then none until
  m804-806; the longest gap 62 months. The landlords held for land every month m756-804: m792 and m800 "no land -
  needs 5,570 m², 283 m² free", no home on site, homes 1,685 from m780 to m804; at m805 ground is bought and 253
  homes go on site. On 0.9.194 no household is unplaced in that window (the brief's 12.3 at m807 was PW5's
  aebb83eae); the class, ground deferred for sixty months and the landlords held, is what remains. The diagnostic's
  only reds are three household-line bounds at m835, after its limit, which the brief does not count.
- **After**: the full default run (target/LP-G1-on, `-Dplaytest.groundTrace=700-835`). "Nothing. Every month passed
  the audit and every reload matched." Looks in m700-835 at least every six months (705, 711, 714-716, 722, 728,
  734, 740, 743-745, 751, 757, 763, ... 805, 808-810, 816, 819-821, 827, 833); the longest gap 6, in the window and
  over the run (367 looks inside skips). No household unplaced in the window. The landlords are held for land at
  m771, m785-786 and m803-808, each after a look whose offer the share could not pay (★4); at m809 the town has
  15 million ft² free and builds 34 studio apartments.

### Seed 9, the monetary ensemble

The four policies replayed through m700 with the window m480-700 traced, before (`-Dplaytest.groundWatch=off`) and
after (target/LP-G1-seed9, `run.sh`). The four are one city through m600, as the brief found (29,460 people at
m600 in each before, 483,639 in each after); they part after it.

| | looks m480-700 | longest gap | months held for land | unplaced > 1 | audit, findings | people at m700 |
|---|---|---|---|---|---|---|
| before, AUTO2 / ZERO_SLACK / HIGH | 5 (480, 481, 604-606) | 123 | 77 of 221 | 0 | clean, none | 34,408 / 34,408 / 31,251 |
| before, SHOCK | 5 | 123 | 112 of 221 | 0 | clean, none | 34,812 |
| after, AUTO2 / ZERO_SLACK | 76 | 6 | 26 of 221 | 0 | clean, none | 958,293 |
| after, SHOCK | 76 | 6 | 32 of 221 | 0 | clean, none | 986,408 |
| after, HIGH | 76 | 6 | 23 of 221 | 0 | clean, none | 565,037 |

Before, no look m482-603 and the landlords held for most of m491-603. On 0.9.194 the m605 flag no longer fires (the
0.9.194 baseline's F1 is 0/16 for all four), but the class does. After, every month held for land follows a look
whose offer the share could not pay (26 of 26 in AUTO2's window, 32 of 32 in SHOCK's, 23 of 23 in HIGH's; seed 0's
9 of 9 the same).

## 6. What it costs: the player's city grows

Held for land, the player's city grew slowly; watched, it grows as fast as the rest of the model lets it.

- **The default seed 0**: 5,709,493 people at m2405 against 1,326,634; 27,393 at m597 against 5,372; 138,116 at
  m813 against 6,922. 1,377 s on the PC against 657 s (FC2's checks were running beside it). The box ran main's in
  160 s at PW5's gate, the PC's suite in 579 s at XR7's; twice those are inside `AllChecks.HARNESS_SECONDS` (1,740).
- **The ensemble's seed 9**: 958,293 people at m700 against 34,408; 276 s to m700 against 101 s on the PC.
- **The playtest's seed 1** (CopperRefiningCheck's child): 5,511,003 people at m2405, 1,647 s alone on the PC.
- **What it moves**: the ensemble's T1 (seeds reaching 40,000 people) and T2 (final population) are info rows and
  will move up; M1 and F1 are what stop a ship (§7). The fixtures built by `probePlay()` do not move (★5).
- **For the orchestrator: the PC as the gate.** On the box the two playtest runs fit easily (main's LongPlaytest
  160 s and CopperRefiningCheck 225 s at PW5's gate; about twice that now). On the PC CopperRefiningCheck now
  passes 1,740 s (above), and LongPlaytest's 1,377 s leaves little room under a full suite's load. A gate run on
  the PC while the box is down would end CopperRefiningCheck at the limit. Neither `HARNESS_SECONDS` nor
  `ROD_CHILD_MINUTES` is changed here: they guard against a hang for every harness, and are main's to set. The
  nightly's 4,000-month playtest and the ensemble's 80 runs grow the same way.

## 7. The ensemble

(after the review)

## 8. The dry run

(after the ensemble)

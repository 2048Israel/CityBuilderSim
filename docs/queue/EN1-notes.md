# EN1 - the ensemble only when the trace moves, in the tree, with a stored baseline (2026-10-20)

Tooling, no game change, no version bump; shipped untagged. The brief is
`docs/queue/EN1.md`. Jerus, 2026-10-20, on whether the 80-run ensemble is
worth it: "yes, do that".

## 1. What changed

- **The rule** is in CLAUDE.md (step 4, step 5's gate, "Before saying a
  change works"), README's *The checks*, the check scripts' headers,
  `tools/ensemble/README.md` and FR8's brief, the one queued brief that ran
  it against main. A batch whose default playtest's trace is byte-identical
  to main's but for its run-time line skips the ensemble and says so. One
  whose trace moved runs it once, on its final model, against the newest
  `docs/ensemble/<version>.md`, never main rerun, and commits its score
  there.
- **The runner** is `tools/ensemble/`, brought in from
  `workbench/workbench-delta-20261010.zip` (`runs/ensemble/`) and from FR6's
  PC copies (`runpc.sh`, `par.sh`, `score_pc.py`). `ensemble.sh <label>`
  snapshots `target/classes` and the dependency classpath into
  `target/ensemble/<label>/`, compiles the driver against the snapshot, and
  runs the 80 runs `JOBS` at a time (5 by default, 1 under the cloud's JVM
  lock). It resumes, scores, and writes `baseline.md`, the file a
  trace-moving batch commits. It runs in Git Bash on the PC and in the cloud
  alike: ';' or ':' by `uname`, Windows paths by `pwd -W`, gson from Maven's
  classpath file as the suite takes it, and Python as `python -I`, with
  pandas read from `ENSEMBLE_PYLIB` (default `~/.cache/cbs-ensemble/py`,
  installed there once by `pip --target`). No shim is needed. `Ensemble.java`
  is the zip's to the byte but its header; `score.py` is the zip's (the copy
  every session since 0.7.100 has run) with the changes in §3.
- **The baseline** is `docs/ensemble/0.8.9.md`: FR6's final run, fr6s1,
  rescored by the tree's scorer against FR3's fr3b, as FR6 scored it. 0.8.9
  is the model it measured. 0.8.10 (FX4) and 0.8.11 (FR4) kept it, with
  traces byte-identical, and HIGH seed 0 rerun on main at 06d1a43 matches
  fr6s1's run in all 42 columns. So it is main's model's score today, and
  EG1 (beside this batch, against fr6s1 itself) reads the same numbers.
- **EnsembleCheck**, a harness in the suite: the runner's files are there,
  the driver compiles against the tree (a planted call to a missing method
  must not), the driver and the scorer name the same five policies, and the
  newest baseline reads (canned ones missing a policy or the json must not).
  The driver calls LongPlaytest's code, so a refactor of the model could
  break it unnoticed until the next trace-moving batch; now the suite sees
  it.

## 2. The count: 22/26 was 22/27

A3's verdict was worked out on pandas' figures and came back as numpy's
bool. The scorer's `ok is True` test never matches numpy's bool, so A3
printed "True" in place of PASS, and the count skipped it. It had passed
uncounted since it was written. Fixed in `add()`. The ensemble has 27
criteria, and fr6s1 scored 23/27 as written.

## 3. The four that failed on main

| criterion | what it asserts | on 0.8.9 | kind | now |
|---|---|---|---|---|
| A2 runaway | AUTO2: no seed's yoy above 10% for 12 months on end, from m120 | 15 [4-19], 13 seeds over | premise out of date | restated, PASS 8 [2-11] |
| S1 little | ZERO_SLACK: 0% held m1800-2040 adds at most +1 point a year over the twin (all seeds +3), with no such spell | +2.17% [1.16-3.45], spell 21 | the premise is a closed economy's | ★1 |
| H2 stops | ZERO_HOT: the autopilot back on brings m342-402 to at most 4% a year | 4.08% [3.29-6.02], 9/16 below | premise out of date | restated, PASS |
| R3 no trap | HIGH: 15% held m600-720 leaves the last 60 months no hotter than the twin, at most 2 seeds over +1 point | -2.34% median, 5 seeds over | the model, by Jerus's rent floor | ★2 (RN1 proposed) |

**A2.** `SPELL_FROM` (m120) left out "the founding's own blip", measured on
0.7.4x as over by m104. The founding has since moved later. On 0.8.9 every
one of the 13 failing seeds fails on the founding's rent boom: a spell of
12-19 months starting at m128-141, in a city of 995-1,712 people whose
rents rose 32-82% in the year, while the autopilot already held 17-23%. That
is a village short of homes, not the autopilot losing the money. A2 and A3
now start at `AUTO2_FROM` (m240), the young city's twenty years A1 already
leaves out, which the inflation spec's §8 asked for ("A1/A2 per window").
The founding's spells are printed as an info row (15 [2-19] months). From
m240 the longest spell is 11 months, in seed 9, twice: one under the limit,
so A2 is still the one to watch. A2 was red as far back as 0.7.100 (7
[0-16]).

**H2.** The AUTO2 twins' own inflation over the same months, m342-402, is
4.99% at the median. A young city on the autopilot runs that hot (m240-600:
3.43%, now an info row), so the 4% ceiling judged the young city's
autopilot, not whether it stopped the burst. The runs end 0.40 points below
their twins at the median, and their excess is below the second half's
(the burst) in 15 of 16 seeds. H2 now reads the tail against the twin, as
A27 made H1 read the burst: the excess at most the anchor's tolerance
(`Expectations.TOLERANCE`, 1 point), below the burst's in at least 12 seeds.
The old figures are an info row. H2 passed on 0.7.100 and 0.7.106 (2.65%,
3.60%), when the young city ran cooler.

**S1 (★1).** A held 0% from m1800 adds 2.17% a year over the autopilot
[1.16-3.45]. The cause is the currency, not the labour market. It falls
2.43% a year faster than the twin's [1.78-3.68], 1.59x weaker by m2040, and
in every seed the excess inflation is below that fall (0.13-0.85 points
under). Unemployment is 13.06% in both runs and their twins; the population
ends at 102% of the twin's. The autopilot holds 3.82% in the window, so 0%
is a cut of nearly four points below what the city needs, and an open city
pays for it through its currency. The anchor follows: credibility 0.37 at
m2040 against the twin's 0.91. "Little inflation in a slack city" is a
liquidity trap's premise. It holds where the city's own rate is near zero,
and this city's never is: the twin is at 0% in 17% of its months from m240.
The spell part fails for the same reason: seed 14's 21 months over 10% ride
a 5.0% trend. So it waits on Jerus. Restate S1 as "the currency's and no
more": the excess at most the currency's extra fall plus the anchor's
tolerance, in every seed, without the spell guard (it passes, by 0.13-0.85
points). Or retire it, keeping its rows as info. S1 stays as written until
he answers.

**R3 (★2).** The 5 seeds over +1 point (0, 1, 3, 7, 15) are the five cities
the rate breaks: 33-45% of their twin's people by m720, 38-45% out of work
over the tail. Their rent goes from 0.68-0.82 of the twin's to 1.20-1.51 in
those 60 months. Seed 0, rerun with the landlord's books as columns: the
interest goes 3,039 to 9,851 a month, the owned places halve (8,230 to
4,204), the break-even per place goes 1.095 to 2.909, and the rent target
follows it through `carryLift()`, though the market is a glut (0.69-0.85
households a door). Rent rises 73% while the people halve. That floor is
Jerus's rule: "theyll rent at zero profit - and no further" (HousingCheck
3c). R3 and the floor cannot both hold. `docs/queue/RN1.md` is the fix if
he picks it: a glut's rent goes under the carry, and why the interest
triples on mortgages fixed for ten years. Or R3 is retired, and the
landlords' carry is the model's own cost channel. R3 passed on 0.7.100 and
failed from 0.7.106.

So 0.8.9 scores **25/27** by the tree's scorer: S1 and R3 red, each for a
named reason that waits on a call.

## 4. The checks

(filled at the end)

## 5. Who wrote what

(filled at the end)

## 6. For Jerus (EN1-C)

- ★1 S1: restate as "the currency's and no more" (recommended) or retire.
- ★2 R3: queue RN1 (recommended) or retire R3.

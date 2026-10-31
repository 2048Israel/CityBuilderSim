# XS1 - seed 11's stall under XR4, found at its cause (study, no tag, 2026-10-31)

*The study's record. The brief is `docs/queue/XS1.md` (XR4's notes §5, XR4-S). Opus alone, on main at 0.8.88: no
model change ships. The switches, the probe and the scripts are in `docs/queue/XS1/` (§6).*

## 1. The cause

**The builders' idle crews, counted as spoken for.** Seed 11 under AUTO2 is not held by XR4's rates. It is held by a
trap of the staffing gate's that every seed passes through, and that seed 11 under XR4 entered at its deepest and could
not leave:

- **A boom leaves the builders oversized.** Construction ordered a depot most months from m870 to m885, against a
  queue of "73.8 months of work" that fell as each depot came. The depots worked the queue off by m918, and nothing
  new had been ordered. In that month the builders went idle at the core crew (`Construction.IDLE_PAYROLL_FLOOR`, a
  quarter of their posts) and laid off 3,187 of their 4,249 posts. The builders held a third of the city's posts, and
  their laid-off crews were a quarter of its 12,500 workers.
- **The laid-off crews are spoken for** (0.7.17, `BuildingManager.getPostsWithheld()`): `Sector.staffing()` adds them to
  the posts, because "they come back with the work". So the gate's spare is the unemployed less the crews.
  - At m1,000 that was 3,074 unemployed against 3,187 crews.
  - Every sector that needs workers holds: 8 to 11 sectors at the gate in 314 of the 381 months from m920 to m1,300,
    "the city could staff 33% of a Mixed Farm; it wants 80%". Manufacturing, Agriculture, Freight, Automotive,
    Materials, Heavy Industry, Copper Refining, Cement & Concrete, Luxury Retail and Filling stations all hold.
- **Nothing ordered, so no work for the builders, so the crews stay laid off.** Construction reads "0.0 months of work
  queued" for most of 1,300 months. The landlords count the crews as jobs too (`RealEstate.plan()`), so they read
  "housing ahead of jobs".
- **The city stays where its posts are**: 20,000 people at 20-25% unemployment, posts frozen at 9,600-10,400. The
  trap is left only by chance:
  - a landlord's block for unhoused people (0.8.87's seed 11 got out so at m1,074);
  - an advisor's order big enough to restart the builders.
  Seed 11 under XR4 found none until m2,200: at m2,250 the crews were back (87 withheld) and the town was at 36,203.

The PT1-M4 founding (`Game.FOUNDING_YARD`'s javadoc) met this trap at a village's scale: a depot's 37 laid-off posts in
a village of 70 workers, no shop in 240 months. It was mended there with a smaller yard, not in the gate.

## 2. The evidence: seed 11, one switch at a time

AUTO2 seed 11 reproduces on main to the person (68,654). Each switch is armed from the month named, and the run is
byte-identical to main's before it (`switches.patch`, `XS1.off()`). The control **nudge** moves the expectations'
smoothed inflation by a millionth once, and no money moves with it.

| run (armed from) | m1,000 | m1,200 | m1,800 | end (m2,400) | past 40,000 |
|---|---|---|---|---|---|
| main 0.8.88 | 20,301 | 21,069 | 23,848 | **68,654** | m2,256 |
| 0.8.87, main before XR4 (byte-identical to the stored 0.8.78 run, so 0.8.86 is too) | 23,892 | 65,189 | 163,305 | 304,137 | m1,130 |
| XR4: the unwind off (m800) | 20,301 | 21,069 | 23,848 | 130,276 | m1,910 |
| XR4: the vault's interest off (m800) | 20,083 | 44,544 | 155,893 | 351,006 | m1,192 |
| XR4: the US's rate at the constant 2% (m800) | 27,071 | 66,798 | 189,069 | 298,376 | m1,106 |
| XR4: no risk-off reaches the city (m800) | 31,717 | 67,712 | 196,057 | 363,853 | m1,098 |
| XR4: all of it off (unwind, vault, US's rate, BUDGET_NOISE; m800) | 28,787 | 63,774 | 172,851 | 277,427 | m1,107 |
| BUDGET_NOISE off (m800): never fires, byte-identical | 20,301 | 21,069 | 23,848 | 68,654 | m2,256 |
| **the world quieted** wholesale (m800) | 18,617 | 18,444 | 21,138 | **23,327** | never |
| control: the nudge (m800) | 20,301 | 21,069 | 23,848 | 130,785 | m1,920 |
| **the gate counts the laid-off crews as spare** (m950, in the trap) | 21,755 | **70,898** | 185,882 | 298,722 | m1,114 |
| the landlords alone count only the posts offered (m950) | 20,301 | 20,934 | 22,863 | 65,431 | m2,280 |
| both (m950) | 21,495 | 63,090 | 194,400 | 333,743 | m1,110 |
| the subsidy's shield on the depots off (m950): byte-identical | 20,301 | 21,069 | 23,848 | 68,654 | m2,256 |
| **the candidate: an idle builder's crews are spare** (m800) | 21,794 | 72,940 | 192,689 | **343,355** | m1,113 |

**No part of XR4 holds the town; every part of the gate's does.**
- Three of XR4's four parts free the seed when switched off. The fourth (the unwind) leaves it held to m1,900, like the
  control. But the world quieted wholesale holds it to the end, at 23,327, in the same trap (1,653 months of it).
- Switching off a part of XR4 at m800 moves the path through the m840-m920 boom, so the builders end it sized
  differently.
- The gate's own switch frees the town from inside the trap (m950): 70,898 people by m1,200.
- The probe (m600-m1,300, `probe.patch`) prints every sector's decision and the gate's figures a month:
  - main: 8-11 sectors held at the gate in 314 of the 381 months from m920 to m1,300;
  - 0.8.87: 5-6 held through m1,000-m1,060, the crews laid off (3,446) as on main, and out at m1,074;
  - XR4 with no risk-off: the same trap m980-m1,060, out at m1,080.

## 3. XR4's part

XR4 made the bust, and the gate made it a trap.
- Under XR4 the US's rate swings between 0.25% and 5.75% from m780 to m1,200 (the Taylor rule from 2026). The
  autopilot's dial swings with it, between 0 and 17.5% (at m850).
- The hot money comes in on the gap (2.5 months of output at m790, 1.3 at m850) and goes, and the D$ moves with it,
  from 2.9 to 0.8 per US$ between m780 and m1,100.
- That cycle sized the builders for a boom at m880 and stopped the boom at m910: from all crews offered to the core
  crew in ten months.
- Before XR4 the same seed met the same trap at m1,000 with 3,446 crews laid off, and left it by m1,074.

The inflation of -13% to +11% and the hot money's swings in the brief are the town's cycle while it is held, not what
holds it: the dial still swings with the world's rate, but nothing a business could order can be staffed.

## 4. The other fifteen seeds

`trap.sh` counts a trap month as one in which three things hold:
- the builders are idle at the core crew;
- their laid-off posts are at least half the unemployed;
- unemployment is at least 10%.

All sixteen AUTO2 seeds on main (0.8.88) meet it:
- **The fifteen**: 122 to 482 months each, in spells of 26 to 149 months, the longest at one of three sizes:
  - the young town of 3,000-4,000 people (m300-m370; seeds 2, 5, 6 and 8);
  - the town of 8,000-12,000 (m460-m615; seeds 10, 12 and 14);
  - the town of 22,000-45,000 at m850-m1,070, just before it takes off. Seeds 0, 1, 3, 9, 13 and 15 pass 40,000
    within 10 to 133 months of that spell's end.
- **Seed 11**: 1,560 months, its longest spell 854 months (m990-m1,843).

So the trap is the game's holding pattern before take-off, and seed 11 is the seed that did not leave it. Its ZERO_SLACK
twin in XR4's ensemble holds the same way (1,133 months over 15% unemployment, ending at 24,996).

The candidate (§5, (a)) from m0 over the sixteen AUTO2 seeds:

| | main 0.8.88 | the candidate |
|---|---|---|
| final population | median 347,140 [68,654 to 383,997] | median 329,143 [267,251 to 373,763] |
| past 40,000 | m881 to m1,116, seed 11 m2,256 | m855 to m1,194, seed 11 m858 |

- No seed stalls under the candidate. Seed 11 ends at 296,345.
- But four seeds pass 40,000 50-170 months later than on main (4, 5, 6 and 13), and the median ends 5% lower.
- So the rule is not a free change. When the work returns, the builders are short of the crews they let go, and the
  gate lets more orders through than the builders can then build.

## 5. The fix, queued (IC1)

Not made here:
- **It is the staffing gate's rule.** It sits in `Sector.staffing()`, which LD1 works now (beside this batch), with
  `RealEstate`'s reading of the same posts.
- **It reverses a deliberate rule** (0.7.17, Jerus's "hired back when work returns"), written into four javadocs.
- **Every trace moves**, and §4 shows the simplest form costs some seeds their pace.

The ways, for IC1:
- **(a) An idle builder's laid-off crews are spare.** While Construction offers only its core crew, `Sector.staffing()`
  does not add its withheld posts. This is the candidate tested above, and three lines.
- **(b) The crews are spoken for only for a time after the layoff** (say twelve months). Jerus's 0.7.17 answer says
  they "can take other jobs"; after a year they have.
- **(c) An idle builder sheds depots.** The spare-capacity rule never sold one here: with the subsidy's shield off
  nothing changed, so something else stops it. Finding what would let an oversized builder shrink.
- **(d) LD1's wage bid alone.** The gate's refused posts raise the band's wage. In this trap it would raise wages in a
  town with 3,000 unemployed, so it does not reach this cause.

**Recommended: (a), with (b)'s time limit if the ensemble shows (a)'s slow seeds come from crews let go too soon.** Run
it after LD1, on LD1's gate, with the full suite and the ensemble. Watch:
- seed 11 and the four slower seeds;
- RE1's seed 4 (RE1's notes §1.4: its gate held six to eight sectors in its town, which may be this trap);
- FoundingYardCheck 6, whose village met the trap at founding.

The landlords' count of the crews (`RealEstate.plan()`) can stay: alone it freed nothing (§2).

## 6. What CD8 should know (and LD1, ST1)

- **CD8**: seed 11's stall is not in the rates or the debt code: no XR4 rate holds it, and the gate's own switch frees
  it.
  - CD8's eight curves will move every path again, so any seed may be left in this trap as XR4 left seed 11.
  - A stalled seed in CD8's ensemble with Construction at "0.0 months of work queued", its laid-off posts near the
    unemployed and 8-11 sectors at "the city could staff N%" is this trap: read it with `trap.sh` before blaming the
    borrowing.
  - A quieted world is no way around it in a fixture: seed 11 quieted stalls harder (23,327).
- **LD1**: in this trap the gate refuses with 20-25% unemployed, because the unemployed are the builders' laid-off
  crews counted as spoken for. A wage bid would raise wages in a town with 3,000 jobless and leave the gate shut.
  - LD1's count of "how often the gate refuses for want of workers" should split out the months when the builders'
    crews are laid off.
- **ST1**: the young towns of seeds 2, 5, 6 and 8 meet this trap at 3,000-4,000 people. A stalled small town with idle
  builders may be it: `trap.sh` reads it from a run.sh CSV.

## 7. The tools (`docs/queue/XS1/`)

- **`switches.patch`** (on 0.8.88): `XS1.java` and the switches.
  - The keys: unwind, vault, usrate, riskoff, quiet, noise, nudge, withheld, rewithheld, protect, idlefree.
  - Arm them with `-Dxs1.off=a,b -Dxs1.from=<month>`; Game sets the month at the top of each one.
- **`probe.patch`**: on `tools/ensemble/Ensemble.java`. Each month in `-Dprobe.from`..`-Dprobe.to`, every sector's
  decision, the advisor's move, and the gate's figures (withheld, unemployed and each band's posts and supply) go to
  `-Dprobe.file`.
- **The scripts**: `setup.sh` (a snapshot of the classes and the driver), `run.sh` (one AUTO2 run with XR4's and the
  gate's columns), `matrix.sh` (one run per switch), `seeds.sh` (the sixteen seeds), and `pops.sh`, `staff.sh` and
  `trap.sh` (the tables above).
  - Output goes to `target/xs1/` (`XS1_DIR`), two runs at once, about 40 seconds a run.

## 8. The checks

- `./batch-check.sh --fast` on the branch: exit 0. The compile is OK, the maps are regenerated with no generated doc
  changed, Stale has 0 firm findings, and the tree holds only the study's own files.
- No model change, so no suite and no ensemble.

## 9. One in, one out

Not a process batch: no script under `tools/`, no check rule and no session rule changed. The study's scripts live in
its own folder.

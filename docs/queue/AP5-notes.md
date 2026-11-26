# AP5 notes - the auto-builder's pass holds the labour figures

Brief: `docs/queue/AP5.md` (Jerus, 2026-11-25: "ok once again, slow down, slot 9"). Built by Claude
(Opus), 2026-11-26, on `batch/AP5` from main e3e390acf (0.9.209, format 66). No save format change.
The harness sections were a `slice`'s, reviewed here; the model is the builder's.

## 1. What was found

Main's profile at 50x, the slot-9 copy in a window (section 5): `Game.nextMonth` 86.5% of the FX
thread, `AutoBuilder.pass` 50.7%, `BuildAdvice.supplyDemand` 34.3%, and under it the brief's
three re-reads: `PopulationManager.getJobVacancy` 17.1%, `BuildingManager.getTotalJobs` 12.4%
(`getPostsWithheld` 4.6%), `BuildingManager.totalBySector` 9.8%. The brief's cause, confirmed.

## 2. What changed

- **`WorksValue`**, banner THE LABOUR FIGURES HELD FOR ONE PASS. `Scope` (AP1's, open around the
  pass and the Build Overview's draw) now also carries the held vacancies with their stamp, the
  held posts, and two counts (`vacanciesRead`, `postsRead`). `heldFor(PopulationManager)` and
  `heldFor(BuildingManager)` give the scope open on this thread for that city, or null.
  `bareWhile(reading)` reads as if no scope were open. `labourChecked`, a harness's switch,
  compares every held reading with the bare one (`LABOUR_COMPARED`, `LABOUR_DIFFERED`); off in play.
- **`PopulationManager.getJobVacancy()`**: in a scope, held against `vacancyInputs()`, every number
  the fill reads as raw bits (the posts and licences by job type, the skilled heads, the bands'
  bases, the workforce, the prisoners, the owner families, the students); read afresh when any
  moves. Returns a copy. The old body is `getJobVacancyDrawn()`, the bare figure.
- **`BuildingManager`**, banner THE POSTS HELD FOR ONE PASS. `HeldPosts`: each job type's posts
  over the stacks, each sector's posts of each type, each sector's stacks in the list's order,
  keyed on `POSTS_EDITS`, read before it is built. `getTotalJobsAtEveryPost`, both
  `getPostsWithheld`, and `totalBySector` read it in a scope; the shares offered, the posts past
  and the instances are read live. `postsMoved()` raises the count after: `addStack` (every
  order, in a `finally`; the old body is `placeStack`), `orderStacks`, `clearStacks`,
  `addInstance`; `BuildingsStacks.addQuantity`, `increaseQuantity`, `removeQuantity`, a building
  finished in `advanceConstruction`; `BuildingsTemplate.setJobs` and both paths of `runRecipes`.
  Those are every writer of a stack's quantity, of a template's posts (`jobsByEducation`,
  package-private but written only in its class) and of the list: grepped, no other.
- **`LabourCheck`**, last section (banner 14), 82 assertions; **`AutoBuildCheck` 11**, 8.

## 3. The calls

- ★1 **The people's side is keyed on a stamp of its inputs, not a count.** The brief named a
  version count for both sides. The vacancies' inputs leave the class: `getSkilledHeads()` and
  `getLicensedHeads()` hand out the live arrays, and `setStudying()` keeps the caller's. A count
  would miss a write through them; a stamp of the forty-odd numbers cannot, and costs less than
  one step of the fill's bisection. Same inputs, same figure to the bit, by any path, RES3's too.
- ★2 **The stacks' side is one static count for every city.** A stack and a template do not know
  their manager. A raise in another city only rebuilds the held posts, never leaves a wrong one;
  the suite runs one harness to a JVM.
- ★3 **Held: the stacks' sums only.** The shares a sector offers (the builders' crews, Freight's
  drivers) and the posts past (the farms' horses) are sector state set in the month, which no
  count of the stacks covers: read live, as before, with the same `postsOffered()` rounding on
  the same per-sector sums. Longs, so the order of the sectors does not matter; `totalBySector`
  adds the same terms in the list's order.
- ★4 **Raised at every order**, whether or not a post moved ("read afresh after every build the
  pass makes"): an order on site adds no finished building, so it costs one rebuild an order.
  AutoBuildCheck 11: 2 orders, 3 builds of the posts, 20,424 held readings.
- ★5 **Held only in a scope** (AP1's two). `NewUnit.resolving()` and `LicenceFees.pricing()` are not
  excluded as they are for AP1's loads: they change what `NewUnit.of` reads, not the stacks or the
  people, and any change to those moves the count or the stamp.
- ★6 **A comparison switch in the model** (`labourChecked`, a plain static, harness-only): the only
  way to compare each held reading with the bare one inside a real pass, its own orders included.
- RES3 (people and houses) and QR1 (plant sold) are in flight: RES3's paths move the people, which
  the stamp reads whatever the path; QR1 sells through `removeQuantity`, which raises. Checked
  again at each merge.

## 4. The checks

- `./batch-check.sh --fast`: compile OK, maps regenerated, Stale 0 firm.
- `LabourCheck`: exit 0. `AutoBuildCheck`: exit 3, its one red main's known
  (`fixture: the saved city turned people away from general care for its fee`, gate.txt, FS12).
- AutoBuildCheck 11's line: "the pass ordered 13 buildings in 2 orders; 20424 held labour figures
  compared with the bare ones, 0 differed; the posts were built 3 times and the vacancies filled 1".
- The mutations (each run, then restored):

| mutation | harness | new assertions red |
|---|---|---:|
| no raise in `BuildingsStacks.addQuantity` | LabourCheck | 4 |
| held vacancies never read again | LabourCheck | 28 |
| no raise in `addStack`'s `finally` | LabourCheck / AutoBuildCheck | 2 / 1 |

- **The trace does not move**: the default playtest, `-Dplaytest.trace`, one JVM each, main's
  classes (e3e390acf) and the branch's (a7906f265's model, run at 54d9ad311). All eight the same:

| file | md5, main and AP5 |
|---|---|
| t-bank.csv | 1822bf4674686a771a2e3f1d061c17d8 |
| t-bonds.csv | 9a0b45fdf455660b70796637d8362b22 |
| t-borrow.csv | 0cdb9ebc78b764c8297126e328629389 |
| t-build.csv | 1535215910439495145d34a050ee5818 |
| t-house.csv | ed21cdf3387f0fd776bbf314f8b56718 |
| t-labour.csv | 8036eb8a43e27a9e890502e2aa069fb9 |
| t-month.csv | 76cb77eb8939ba272d1b9dd04d50adce |
| t-pop.csv | e414af0cff9d46c1c091b99598b1674e |

  **Again after QR1 shipped** (0.9.210, which moves the trace: it changes when firms sell plant).
  Merged at f48f79d77; QR1 adds no writer of a stack's buildings, a template's posts or the
  people (its sales go through the paths that raise). Main's classes built from the merged tree
  with main's five model files (ded8345fc), against the merged branch's. All eight the same:

| file | md5, main ded8345fc and AP5 f48f79d77 |
|---|---|
| t-bank.csv | aa2af5f1316c33dde8ba9366884ce152 |
| t-bonds.csv | fa135be296fe76e98685d040a2d05a33 |
| t-borrow.csv | 36b47896b1f126a8b75ef1102132319d |
| t-build.csv | d2e6afe778756cef8b4c8186b0c316df |
| t-house.csv | 96cf152bb7b38f96e2198749252b88b2 |
| t-labour.csv | e9cf9e0947a832addd388e041eaeccaf |
| t-month.csv | 9989337eb618a85564fa3d65307aeabe |
| t-pop.csv | c6f5e3bacc4ae22a60a9be9775855794 |

  LabourCheck 0 and AutoBuildCheck 3 (the same known red) again on the merge.
  So no ensemble. The slot-9 run's log (565 lines: the auto-builder's orders and their figures,
  the reports, every month's people) is the same in all four headless runs below once the timing
  lines are out: md5 ef60c24e224995a79164964eb11d6df5.

## 5. Slot 9, before and after

The copy of 2026-11-25 (dojha, m4866), recopied into `%TEMP%/cbs-AP5-scratchpad/` for every run,
loaded through `GameFiles(dir, legacy)`, `-Xmx4g`, AP2's drivers (`Perf9`, `PausedProbe`). Other
sessions loaded the laptop, so AP2's 219 ms is not comparable; main and AP5 alternated. Taken
before the QR1 merge: main e3e390acf against a7906f265's model (QR1 touches nothing the pass holds).

Headless, 12 months after 2 warm:

| run | mean | median | max (ms) |
|---|---:|---:|---:|
| main, 1st | 477.7 | 453.1 | 603.7 |
| AP5, 1st | 360.5 | 343.6 | 475.0 |
| main, 2nd | 474.3 | 445.4 | 611.5 |
| AP5, 2nd | 357.3 | 337.4 | 467.7 |

In the window at 50x (`-Dprobe.rung=8`, 5 s a screen): months a second, frame p50 / p95 (ms):

| screen | main | AP5 | `FRAME_P95` |
|---|---|---|---:|
| Build | 6.6, 116 / 199 | 8.8, 107 / 158 | 99 |
| Land | 5.6, 172 / 301 | 7.7, 119 / 214 | 57 |
| People | 6.4, 144 / 223 | 8.2, 115 / 178 | 34 |
| Services | 6.8, 143 / 194 | 9.6, 81 / 141 | 37 |
| Infra | 8.1, 116 / 164 | 9.8, 80 / 118 | 43 |
| Sectors | 6.2, 158 / 213 | 7.7, 127 / 177 | 65 |
| Govt | 6.7, 142 / 208 | 9.8, 21 / 120 | 36 |
| Finances | 7.3, 130 / 192 | 10.0, 22 / 121 | 32 |
| Bank | 9.2, 103 / 145 | 9.8, 15 / 98 | 42 |
| Trade | 8.3, 115 / 158 | 10.0, 19 / 110 | 20 |
| Policy | 8.5, 106 / 179 | 9.9, 9 / 70 | 32 |
| History | 7.3, 131 / 205 | 9.9, 17 / 101 | 60 |
| Rankings | 8.3, 112 / 180 | 9.5, 15 / 96 | 60 |

Every p95 is still past its figure: at 50x a frame that runs a month carries all of it. The FX
thread after AP5: `AutoBuilder.pass` 36.4% (from 50.7%), `getJobVacancy` 0.8% (from 17.1%),
`getTotalJobs` 7.5% (from 12.4%), nearly all of that now `getPostsPast`.

## 6. Next: AP6, queued

Still short of 10 months a second on four screens, so profiled once more and queued as AP6
(`docs/queue/AP6.md`, the figures there), not built: the posts past read once per job type in
every `getTotalJobs()` (6.6%), the bond book (`OrderBook.submit` 10.2%), the month's sector walks
through `totalBySector` outside the pass (9.5%), `Game.runPrivateInvestment` (11.9%).

## 7. The slice

Briefed with (a)-(g) for LabourCheck and the pass section for AutoBuildCheck. The review kept it
whole: fixtures cause each move (a hospital for the gated posts, depots laying crews off, a
template the town lacks), each threshold shown from both sides, every comparison exact. It added
two assertions the brief lacked (`addQuantity`, `increaseQuantity` on the stack), without which
the first mutation stayed green.

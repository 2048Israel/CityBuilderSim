# WG3 — schooling on pay after cost, including WS2

Builder WG3codex1, Codex gpt-6.1-sol at xhigh. Cut from origin/main
`d1d70f0aee8312bb86c888f8cf9831c71322754b` (0.9.180), after fetching and
checking the batch id, branches, worktrees and worker records. No earlier WG3
work was overwritten. The orchestrator gates and publishes.

## Decisions and implementation

Jerus's approved C3 is applied: college, university and the four professional
schools value pay after cost; high school's coverage and affordability rule
stays as it was. WS2 is folded in by the queue, not left for another batch.

The historical founding `WageLadder` and `WageLadderCheck` already exist.
Their names, semantics and APIs are untouched. The pure helper is instead
`SchoolingLadder`, with its own `SchoolingLadderCheck`. It reads actual current
wages, Education's current course fees and subsidy, EducationType's durations,
TaxPolicy's student-loan rate and the actual policy rate. No catalogue or
outside statistical series is added.

The cash comparison follows `docs/studies/wages/ladder.py`. The high-school
bill precedes adult work and is carried forward; adults lose the studied-from
band's ungated wage while studying. College and university start from a
diploma. A professional qualification's earning date follows university's
adult years, and its displayed fees and wages forgone are its own course's,
as in the prototype (not a claim that all prerequisite fees were paid again).
The result is a level month over the remaining working horizon, with wages
and prices held at today's level, not a forecast. Negative values are retained.
The annual discount is the maximum of loan interest, policy interest and 3%;
the floor remains the study's explicit estimate of a family's price of waiting.

HS2's approved stages are used. Both may study; no young-only eligibility rule
is invented. In its memoryless compartments the young have an expected 204
months plus the established's 420 left, and the established have 420. These
are derived by following `AdultStage.next()` and `spanMonths()`, not a new
single-age pool or a claim about each adult's actual age. There is no saved
qualification-by-stage inventory: the existing FamilyModel pro-rata assumption
is used. Each stage's gain passes through the original 1.3 return curve and
cap, then its decision is weighted by the current pyramid share. A stage with
no adults contributes nothing; a city with no adults has no willing intake.
The orchestrator explicitly adopted this interpretation (mail 0e6f, 2026-11-21).

The only additional implementation file is Game: the orchestrator authorized
one disjoint call binding Education to live tax, policy-rate and pyramid
inputs. The hard construction check caught the initial placement in the
prices helper, which is not called before a new city's first month. On the
orchestrator's mend GO (mail 2064), that same sole call moved immediately
after Education's construction in `buildWorld`, once its three owners exist.
NewGame/reset rebuilds these references; a load restores their state in place.
It is outside R1g's current Game hunks; R1gc2 was told
to keep its own branch independent. There are no saved fields, money transfers,
loan-accounting changes, save-format changes or new threads. Adult enrollment
changes existing future student counts and therefore future money flows.

Wages now has a second page, The ladder. Its eight qualifications have solid
paid bars and outlined after-cost bars on one common signed axis. A stage chip
selects the expected horizon; details give posts, holders,
course months, total family fees, total wages forgone and the model's verdict.
Degree posts exclude gated jobs, licences read their own jobs, and the degree
wage is its best ungated job. No-post rows are muted. A faint tick gives that
job's current wage target independently of the floor's staged preview. The
far-above verdict uses the existing full-willingness threshold (gain of 1),
avoiding a new arbitrary numerical threshold. A modest positive gain has a
plain above-step verdict. The under/equal and no-post verdicts are the approved
study's. Display coordinates translate the axis for negative values without
changing the shared Pieces implementation.

No For-WG3 Pending row was found in the current QUEUE or todo. The naming,
stage-horizon and verdict-threshold recommendations are taken here. No
standing-rule, spending or unresolved premise decision is left for Jerus.

## Verification and remaining gate work

- Initial `batch-check.sh --fast`: exit 0, compile/maps/Stale green.
- `batch-check.sh SchoolingLadderCheck`: exit 0. Each qualification in each
  stage is compared with an independent monthly cash ledger, not the helper's
  closed-form annuity. Fee/subsidy, duration, forgone wage, loan/policy/floor,
  stage horizon, gated/ungated jobs, posts, holders and verdict changes are caused.
- A second `--fast` after EducationCheck additions: exit 0.
- The page's first compile passed; Stale found the UI file count in CLAUDE
  (51 before the new probe, 52 after). Only that count was corrected.
- `batch-check.sh SchoolingPageCheck`: exit 0, maps and Stale green. The
  actual bar inputs match every model row in both stages, with caused
  inversions, negative values and no-post cases.
- First coordinated `batch-check.sh EducationCheck`: wrapper exit 1; all
  eight failures were new WG3 assertions, and all prior sections passed.
  Three exposed a fee-crossing fixture that changed the diploma baseline
  too; the fixture now holds the high-school fee at zero, so only the
  college bill moves. Five caught an actual construction gap: month and
  load bindings passed, but newGame did not call the prices helper until
  its first month. Saved trace: `target/WG3-education-first-red.log`.
  The pre-mend source patch and Game/EducationCheck checkpoints are also
  retained under target. No failing assertion was removed or relaxed.
- Final-model `batch-check.sh --fast`: exit 0, compile/maps/Stale green.
- The orchestrator-authorized standard `batch-check.sh EducationCheck`
  verification after the mend: exit 0, one harness passed in 70 seconds,
  all old and new assertions green; wrapper/maps/Stale exit 0. Bare
  construction, newGame, live loan/policy changes, a month, load before
  the first month, live pyramid mutation and load-to-newGame/reset with
  fresh cohort/loan/policy references all pass. Final saved trace:
  `target/WG3-education-final.log`. No job remains running.
- Final diff/pick against `d1d70f0ae`: ALL, 239 harnesses, because the two
  new harnesses are appended to AllChecks. The review line is exactly
  `review    not needed - no money, saves or threading in the diff`.
  The full pick has not been run. There is exactly one added line in Game,
  after Education's construction; historical WageLadder and its check have
  an empty diff against origin/main. Changed source and record files have
  no whitespace errors; LF is retained. Generated map/index descriptions
  were regenerated, not hand merged.
- Mends `f431daddef4e77fef429b4ccd2642f62f29465d1` were pushed with the
  provisional marker, and the orchestrator independently reviewed source,
  notes, ship file, exact Game call and pick.

## Final snapshot and ensemble authorization

The orchestrator changed the order on 2026-11-21 (mail 370b): R1g's final
ensemble has a hard F1 negative-GDP finding and is now Pending. WG3 proceeds
first, without any of R1g's unpublished model. Origin/main `df12d7ae3` is a
QUEUE-only update of .180 and was merged as `d923521c0`. Source/proofs,
pom.xml and ensemble tools are byte-identical before/after that merge:
src tree `792d396543a62a90cbfacfc563db1cc47d5608a5`, tools/ensemble tree
`cd39e3ed647106973569168278d7ade6eda065d0`, pom blob
`299d33b1324a0397d7d4f83b5c1237da3493925d`. No compile or green harness
was replayed for this records merge. The stale binding javadoc was then
corrected, with its generated description refreshed; no executable line or
proof changed.

GO is for one unique final-model ensemble, 80 runs (16 seeds, five policies),
eight at once on the box, against `docs/ensemble/0.9.180.md`. Gates and the
nightly keep priority. The local Java/javac/Maven PATH guard and a production
box-wrapper fallback proof are required before invoking it. The laptop belongs
to FS5: an unavailable or full box must stop, never overflow to the laptop.
The final source snapshot is committed/pushed with the provisional marker,
then frozen through completion, home copy and scoring. All 80 results and the
complete baseline must be committed and hard M1/F1 reported. No dry run,
full pick, separate playtest or readiness GO is implied by this authorization.
R1g rebases its future work on WG3 later, rather than holding WG3 now.

## Final ensemble result: hard PASS

The authorized unique label `WG3codex1-final180` ran the pushed source
`6ede2fa3a76fc700e03bb7cb326ff5edcf6565f9`, against .180, on gatebox
(OpenJDK 21.0.13), eight at once, from 2026-11-21T18:22:01Z to
18:36:49Z including home copy and scoring. All **80/80** runs completed,
none missing or short, with **64/64** before-switch policy twins identical.
The wrapper exited **0**, batch **PASS**, **10/10 M1/F1 hard criteria**,
24/26 criteria overall (one retired criterion excluded). Every policy has
zero failed and zero unread money-audit months, and zero runs with a
LongPlaytest finding. Validation read all 80 CSVs and found no finding rows.

The complete report and summary JSON are in [the baseline](../ensemble/WG3.md).
The snapshot names the box's main earth-2.bin and world-1.bin. Runs took
14.3 minutes on the box, 113.2 summed run minutes; median seconds were
AUTO2 198.65, ZERO_SLACK 163.1, ZERO_HOT 18.35, SHOCK 41.65 and HIGH 34.3.
The remote run is under
`/home/jerus/cbs/tools/6ede2fa3a76fc700e03bb7cb326ff5edcf6565f9/target/ensemble/WG3codex1-final180`,
copied home to `target/ensemble/WG3codex1-final180`. All CSVs, logs, 80
timings, progress, snapshot, score, baseline and summary remain there.

One information verdict changed: **AUTO2 A1 PASS → FAIL**, 11/16 seeds
in the inflation band against at least 12; median 2.86% [2.32% to 3.15%],
against .180's 2.87% [2.03% to 3.29%] and 12/16. A2 remains an
information FAIL: longest high-inflation spell 14 [6 to 36] months against
under 12 in every seed (.180: 15 [4 to 36]). AUTO2 T2 remains PASS:
final population median 2,294,876 [36,437 to 4,806,932], against .180's
2,532,050 [28,685 to 4,208,078]. These readings are retained as information,
as EI1 requires; no threshold or premise was changed to pass the batch.

The ignored local invocation and proofs are under `target/wg3-ensemble/`.
Java, javac and Maven PATH guards were exercised, then the production box
wrapper's no-host path was exercised with a fake SSH command and mail
disabled. It reached the guarded Maven command and stopped before any
local compile/JVM. The first proof expected Maven's 75 to propagate; the
wrapper deliberately converts compile refusal to setup exit 1. That proof
expectation was corrected and the proof passed, with the first output kept.
The actual ensemble never fell back: its guard log hash stayed exactly
`df93202346808639a97d0e93d2ff7a7727fa135535d4f0086ac71e6f8ab13835`.
Preflight confirmed unused local/remote labels and safe worktree destinations.

Post-run validation requires all 80 unique CSVs, timing entries and DONE
markers, matching final months, all hard readings, all 64 twins, exact
baseline/summary JSON, host/data provenance, source/proof/pom/runner byte
identity to the frozen commit, and the retained provisional marker.
`target/wg3-ensemble/validated-result.json` and `validation.txt` retain the
results. The baseline differs from the runner's complete report only in
the publisher's version tokens and trailing-whitespace cleanup. No source
changed during the run, home copy or scoring; the remote owned JVM count
was zero after completion. The baseline and these notes are committed
before requesting the separate ALL-pick dry-run GO.

## Dry run: sealed NEW reds, no readiness

On the orchestrator's separate GO (mail 3931), one normal box-only
`sh tools/gate.sh WG3 --dry-run` ran on branch
`089f5e2282891d47593e942899fbc4581135b58f`, main
`df12d7ae3dfd04d541590bb0f1d3ab5b93233504`, with source still frozen at
6ede2fa3a. The adapted PW1 local shell guard refused an empty-host gate
before clone/compile/suite; local Java, javac and Maven each refused too.
The box's executable paths remained `/usr/bin/java`, `/usr/bin/javac` and
`/usr/bin/mvn`. The timeout was 7,200 seconds; no laptop fallback occurred.

The real remote run was
`/home/jerus/cbs/runs/cbs-gate-WG3-cM0qFfei`. It exited **1** at
2026-11-21T19:01:50Z, inactive, uncancelled, with completed checks sealed.
Its original publisher and guardian exited and no JVM remained under its
working directory. Client exit 1 and automatic home copy finished at
19:01:55Z. This is a failed dry run, not a successful reusable gate receipt.
The selection was `full nightly EducationCheck version=0.9.181`; the kept
full-suite summary is 225 harnesses, 213 passed, 12 failed. Ten failed
harnesses matched the existing scoped red records; two had new assertions.

All kept evidence was copied again into `target/wg3-dry/kept` before any
records change. Independent remote/local verification matches **863/863**
sealed checks/generated-file hashes and **10/10** auxiliary receipts.
`checks.sha256` is
`51ddcb0305e983167b84dbbbd712d0c787eb77a26423caa677e19698d148311b`;
`checks/suite.txt` is
`13ef88929641c136863333841b9530913220ec2946dd82846a4ce5ca1c583971`.
The five exact unnamed lines, complete logs, seal and manifests remain in
that archive; validation is in `target/wg3-dry/kept-receipt.json` and
`remote-evidence.txt`. Neither the kept run nor its verdict was rejudged.

The new lines are BuildAdviceCheck's exit/count wrappers and
`fixture: a month of a Standard founding, from the ninth, that at least a class would come to a university FAIL`,
plus LongPlaytest's exit wrapper and
`FAIL households with no home 15 time(s), months 380-394`.
The university fixture reached month 36, population 385, zero would-be
university students and five university posts. Its no-university row/card
assertions passed. The separate positive-demand fixture passed too: 1,134
would come against a first school's 600, initially only 164 would be hired;
adding offices raised hiring demand to 632 and the row appeared.
The home finding's worst case is month 386: **4.922** households nowhere,
**644** homes, six sizes. The home law remains strictly greater than one.
Neither new assertion matches the old streets-plan FS12 or ring-distance
CL7 rows; no class-wide known-red classification is used.

The orchestrator moved WG3 to Pending in records-only main `da43868fb`
(mail 077a); the game is still .180. WG3 is diagnosing/mending, not ready.
Return to Queued is Jerus's decision. These notes do not edit that queue,
name a new red as unrelated, override a red, or place the failed gate path
in the ship file's successful `gate-run` marker.

## Read-only cause trace: established facts and limits

The two city setups are different. The failed university case uses
[BuildAdviceCheck.founded](../../src/main/java/ham/citybuildersim/BuildAdviceCheck.java#L1248):
Standard founding, its listed House/shop/farm builds, three/four initial
months, then the requested ninth month. It waits only while demand is below
the model's class floor and the month is below 36 in
[thePipeline](../../src/main/java/ham/citybuildersim/BuildAdviceCheck.java#L1171).
It does not apply the full player's explicit map/policy/rescue/fund setup,
repeated advice, periodic land/retirement moves or round trips.

[CityNeeds.wouldCome](../../src/main/java/ham/citybuildersim/CityNeeds.java#L1093)
reads Education's eligible band, willingness, enrolment rate and course
months. [Education's participation](../../src/main/java/ham/citybuildersim/Education.java#L688)
multiplies the unchanged participation cap, after-cost return curve and
affordability. [The live stage decision](../../src/main/java/ham/citybuildersim/Education.java#L704)
weights each stage's own curve; the pure
[gain](../../src/main/java/ham/citybuildersim/SchoolingLadder.java#L54)
compares after-cost qualification levels. The university's wage is its best
ungated job; a doctor's wage cannot establish its return. The played fixture
never pins its eligible pool, affordability or positive after-cost return.
The saved log gives only the product zero: it does **not** identify which
component was zero. A correct zero-return outcome and an uncaused positive
demand premise are plausible; neither is proved for this city yet.

The advice guards are explicit:
[wanted](../../src/main/java/ham/citybuildersim/CityNeeds.java#L1022) is the
smaller of would-come and hires; [listsSchool](../../src/main/java/ham/citybuildersim/CityNeeds.java#L1002)
requires `SEATS_FLOOR` (25), plus half the smallest school when no seats exist.
[BuildAdvice.schoolNeed](../../src/main/java/ham/citybuildersim/BuildAdvice.java#L794)
reads that same wanted figure at its projection. Hence the passing no-row
result is consistent with the measured zero, without proving the fixture's
separate demand premise or proving a production bug. A free tuition subsidy
alone also cannot eliminate years out of work or wages forgone.

For homes, [Game passes sized doors to migration](../../src/main/java/ham/citybuildersim/Game.java#L15879)
before migrating the cohorts. [Migration](../../src/main/java/ham/citybuildersim/Migration.java#L981)
asks the existing census's `roomLeft` and caps **arrivals**, not every change
in the later household census, at its people answer. The room uses the
[native match and existing household mix](../../src/main/java/ham/citybuildersim/FamilyModel.java#L1991).
The previous education step's students and the out-of-work adults are then
outside working families in [Game's census rebuild](../../src/main/java/ham/citybuildersim/Game.java#L15989).
This is a real route from changed enrolment to changed door demand, but a
changed city does not identify the failing component.

The live placement sequence is
[house → squeeze → house → noteUnplaced](../../src/main/java/ham/citybuildersim/Game.java#L16009).
The [match](../../src/main/java/ham/citybuildersim/FamilyModel.java#L1569)
queues largest shapes first, takes the smallest fitting unit then crowds,
and refuses children in studios. The
[valves](../../src/main/java/ham/citybuildersim/FamilyModel.java#L1718)
share singles, then [noteUnplaced](../../src/main/java/ham/citybuildersim/FamilyModel.java#L1804)
applies each group's doubling ceiling and records the residual. Subsequent
[affordability sharing](../../src/main/java/ham/citybuildersim/Game.java#L16038)
can change current shapes. A query on those later shapes is **not** the
earlier recorded placement flow. The residual is explicitly carried through
saves for that reason ([toSaveArray](../../src/main/java/ham/citybuildersim/FamilyModel.java#L2335)).
Owners are [outside this native housing match](../../src/main/java/ham/citybuildersim/FamilyModel.java#L553);
their headcount must be logged separately, not silently added to the matched
matrix. This is a source fact, not an attribution to HO1 or another batch.

The [player's housing response](../../src/main/java/ham/citybuildersim/LongPlaytest.java#L3892)
considers House/apartment/studio moves when beds are short or the recorded
unplaced count is positive. It is made at a player's look, not continuously
inside a long skip. The monthly [home audit](../../src/main/java/ham/citybuildersim/LongPlaytest.java#L1733)
still tests the final raw residual above one. The kept summary lacks the
by-size stock, rejected shapes, seeker/owner/student split and the transitions
through months 370–400. It cannot establish supply shortage, wrong size mix,
transition timing, stale placement data, rounding or a different latent fault.
No homelessness mend or classification is selected from this evidence.

## Prepared diagnostic proposal — not compiled or executed

The orchestrator authorized static preparation only (mail 077a), with R1g's
next box job ahead of this one. Draft source is under ignored
`target/wg3-cause/driver-src/ham/citybuildersim/WG3CauseTrace.java`, with
`prepare-overlay.py`, `preparation.json`, original harness copy and
`overlay.diff`. No project source, harness, model class or model constant
has changed, and no JVM has started under this preparation GO.

The proposed one box diagnostic job has **two sequential fresh JVMs** to
avoid contaminating the original university setup with playtest static state.
Proposed hard bounds: one at a time, `-Xmx2g`, 300 seconds total, explicit
box only after the orchestrator releases the slot, native gate/nightly locks
first and local Java/javac/Maven fallback blocked. Use OpenJDK 21.0.13 and
the named box main earth-2.bin/world-1.bin, exactly as the retained run.
Compile only the target driver/harness overlay into separate directories
against an audited copy of the frozen 6ede2fa3a model/dependency classes;
do not compile or alter production sources. Record source/class manifests,
actual host/JDK/data/start/end, exit, elapsed timings and zero owned JVMs.

1. **Original main to month 400.** Use a target-only copy of the frozen
   LongPlaytest harness with six count-checked insertion sites: establish the
   primary game/root; observe just before its original monthly simulation;
   observe after the original audit, countWorks and native trace; observe
   before/after the original round-trip call and its loaded copy; route only
   a dedicated planned-bound signal out of the original catch. All original
   main actions, decision order, stop counter and save/load code remain.
   Keep `TARGET_MONTHS=2400`: setting it to 400 also changes
   [layoutStarts](../../src/main/java/ham/citybuildersim/LongPlaytest.java#L4720).
   Stop after the complete original month-400 audit/trace; close the native
   writers and clean only the original harness's own scratch root.
   The frozen original harness SHA256 is
   `c3d8732e3565c29be3d6c30dd90d4874bb2908b79362c549d1449afa923229cc`;
   its overlay SHA256 is
   `38c70e6c9274fbb5c2e9474b1f3b88840b0b7e04877975811856e29afbb2cd35`.
   No `probePlay` or ensemble driver is substituted: the
   [original stop loop](../../src/main/java/ham/citybuildersim/LongPlaytest.java#L6837)
   and [periodic roundTrip](../../src/main/java/ham/citybuildersim/LongPlaytest.java#L6933)
   stay in use. `roundTrip` loads a separate game for comparisons; it does
   not replace the main game or explicitly advance it.
2. **Original university case to its own stopping month, at most 36.** A
   separate JVM uses the untouched frozen BuildAdviceCheck/LongPlaytest
   classes, calls `BuildAdviceCheck.founded(..., 9)`, then the same original
   while condition and quiet monthly step. Capture each month through the
   first class-size demand or month 36. Change no skill, wage, fee or policy
   inputs. Preserve the class-floor/month-window, no-row and no-card laws;
   a failing law still gives a red diagnostic exit, not a fabricated pass.

The JSONL callbacks name before-month, after-original-audit/trace and each
save/load boundary. They record the real eligible pool, wages/best ungated
job, native after-cost rows for both stages, fees/subsidy/scales, loan/policy/
discount rate, native return/affordability/willingness, in-flight cohorts,
student body and would-come/hires. Housing records include current and last
recorded-match stock by size, current shape demand, stored unplaced arrays,
each seeker group's sharing/doubling/unhoused people, owners separately,
raw residual, census-vs-current student count, births/deaths, migration's
recorded arrival/room figures and household reformation/return counters.

Detailed rows cover 370–400 and milestones 120/240/360. An isolated
FamilyModel copy additionally runs the actual matcher to report **current-fit
query** placed/free by size and residuals, checking matching inputs and that
the live census's saved state is unchanged. This is labelled separately;
it is not a reconstructed migration or earlier placement flow and cannot by
itself clear a finding. The original raw home law above one and native audit
stay hard; the driver retains a red exit when they fail.

Validation must compare only retained shared evidence: original served/works
milestone rows at 120/240/360 and the exact 380–394 finding interval, worst
month 386, rounded 4.922 residual, 644 doors/six sizes. Record original
round-trip events. The retained run lacks full monthly state rows, so even
a match does **not** prove a complete byte-identical prefix. Any mismatch,
missing reload, timeout or incomplete marker stops classification. No rerun
is automatic.

Only after the original university capture identifies the zero component is
a harness-only mend proposed: cause an eligible pool, affordability and a
positive native after-cost return in that fixture, rather than waiting for
a played wage to happen. Keep its month bound, `SEATS_FLOOR` and no-row/card
premises; read the original curve/helper, never duplicate a new formula.
Targeted verification would be that causing fixture plus BuildAdviceCheck,
one named run only on a new GO. No change to CityNeeds, advice production,
education production or the home law is proposed. Housing needs its evidence
before a precise mend can be selected. Any model mend would need a new
final-model ensemble; a harness-only mend retains the current valid ensemble.

All local worktree JVMs have named main's absolute earth-2.bin and world-1.bin
through JDK_JAVA_OPTIONS; the box ensemble/gate name their own main copies.
Game checks use scratch saves only. No baseline suite of main, standalone
full pick or separate LongPlaytest replay was run. The one authorized ensemble
and the failed dry run are recorded above; no dry restart, kept rejudge,
model/harness mend or diagnostic execution is authorized yet.
The ship file remains provisional until the orchestrator's readiness GO.

For the optional PC eye check: open Policy → Wages → The ladder, switch both
adult stages and inspect the common zero, negative outlines, muted no-post
rows, costs and faint wage-walk ticks at a narrow window width. High school
still runs on coverage and affordability. This is an inspection note, not a
Pending row.

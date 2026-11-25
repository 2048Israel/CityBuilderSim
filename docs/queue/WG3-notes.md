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

## Authorized diagnostic stopped before compilation

Mail `20261121-153912-395-orchestrator-4eaa` approved exactly one box job:
isolated target compilation and two sequential fresh JVMs, housing then
university, within 300 seconds. It retained all pinned sources, original
laws and matching-anchor requirements. The single attempt was
`/home/jerus/cbs/runs/WG3codex1-cause01`, with its complete home copy in
`target/wg3-cause/runs/WG3codex1-cause01`. The local foreground owner ran
2026-11-21 19:50:28–19:50:29 UTC; both actual remote job and controller
exited **1**. No retry was made.

The strict JDK provenance guard stopped the job **before compilation**.
The wrapper selected SSH's default `/usr/bin/java`, whose release metadata
says Ubuntu `21.0.13.1`; the required frozen run used `21.0.13`. No `javac`
or model JVM was started, no compile log or compiled target classes exist,
and no diagnostic observations were produced. Source hashes of all three
uploaded/home-copied pinned files still match. Cached-class before/after
checks were not reached, so this attempt does not claim those receipts.
The recorded job/controller PIDs, 2119263/2119260, are gone, the remote active
file is absent and the box lock is released. `one-job.log`, the actual exit
files, `stopped-inspection.txt` and all uploaded inputs are preserved under
`target/wg3-cause`; no original kept gate evidence was changed.

Read-only metadata identified the setup error: the normal
[gate-box server](../../tools/gate-box.sh#L62) first sources
`$HOME/cbs/gate.env`. Its third line selects `$HOME/cbs/jdk` and prepends
that JDK's bin directory to PATH. This wrapper omitted that step. The
configured symlink resolves to `jdk-21.0.13+9`; its release file says
Eclipse Adoptium, `JAVA_VERSION="21.0.13"` and
`JAVA_RUNTIME_VERSION="21.0.13+9-LTS"`, matching the retained run's JDK
version. This inspection executed neither Java nor javac.

Smallest setup proposal: source that same existing gate environment before
resolving Java/javac, keeping the strict version/provenance guards. The
two-line proposal is saved as `target/wg3-cause/proposed-gate-env.diff`;
it is not applied to the attempted wrapper. Driver, overlay, original
harness and frozen model remain unchanged. A new, uniquely labelled
attempt needs fresh execution GO; unused time from the stopped attempt
does not authorize a retry. The university zero component and home-law
cause both remain **INCONCLUSIVE**. No mend or classification is selected
from this setup failure.

## Second diagnostic: housing captured; university setup incomplete

Mail `20261121-155723-324-orchestrator-31c6` explicitly authorized a new
unused `cause02` label and the setup-only gate-environment correction.
The one job is `/home/jerus/cbs/runs/WG3codex1-cause02`; its complete retained
copy is `target/wg3-cause/runs/WG3codex1-cause02`. Local foreground ownership
was 2026-11-21 **19:58:22–19:58:36 UTC**, 14 seconds including preflight,
upload, isolated compilation, both sequential processes and home copy.
The job/controller actually exited **1**, with no complete-job marker.
This is an **INCONCLUSIVE diagnostic**, not a gate pass or classification.

The JDK guard selected Temurin **21.0.13+9**, native named earth/world files
and the pinned frozen 6ede2fa3a snapshot. Only the driver/overlay compiled;
17 isolated class files are retained and hash-verified. All three source
pins match. Cached-class before/after manifests are identical, SHA256
`29134c44400431e4025953b675a7e21f76b1390340c7238b282e4576da445792`:
**2,444/2,444** cached classes remain unchanged, including a terminal
read-only recheck. Both data hashes still match. All **67/67** remote/home
retained file hashes match. Receipts are `cause02-terminal-receipt.txt` and
`cause02-copy-receipt.json`; raw CSV/JSONL/logs and compiled classes remain
beside the attempted sources. Job/controller/housing/fixture PIDs
2141165/2141162/2141289/2141787 are gone, active is absent, the box lock is
released and both scratch namespaces are empty. No source or records were
changed while the actual remote job was live.

**Housing observations.** The original main reached the planned complete
month **400**, retained `TARGET_MONTHS=2400` and exited **1** for the raw
home law. There are 399 contiguous completed monthly rows, months 2–400.
All **12** retained served/works milestone rows at 120/240/360 match. The
actual round trip at month **373** logged before, loaded copy and after,
with no load failure. Exact raw failures remain **380–394**, worst **386**,
**4.92236852101189** households, **644** doors/six sizes. These anchors do
not establish a complete byte-identical prefix.

At 386 the stored match stock and current stock both contain 80 size-2,
63 size-4 and 501 size-6 doors. Stored unplaced-shape entries sum to
**630.484471323489** family households. The native
[half-households-plus-one allowance](../../src/main/java/ham/citybuildersim/FamilyModel.java#L1882)
is **625.562102802477** and is exhausted; their difference is the recorded
**4.922368521012** residual to floating-point precision. Neither student nor
unemployed seekers have an unhoused residual. Owners are 14, recorded
separately and excluded from the native match. The isolated **CURRENT-fit
query** also leaves 630.484 family households unmatched before doubling,
with 16.254 size-2 doors free and no size-4/6 capacity free. Its native-input
and live-census-unchanged assertions held. It remains a current hypothetical,
not a replacement for the stored placement or a reconstructed migration.

Across 370–400 the stock remains 644. The native build trace records no
remaining construction points or real-estate sites/owed work in that
window. At 370 the census has 204.590 students; at 386 it has 45.530, while
current students are 39.428. Households rise from 1,145.954 to 1,249.124.
At 380–394 recorded arrivals and room are zero, but births, deaths,
departures, returning adults/children and family reformation still occur.
These observations and the native allowance ledger narrow the trace to
family demand beyond available matching/doubling capacity. They do not
establish which production or player premise should change, nor attribute
the finding to R1g, HO1 or a known class. The raw law stays hard.

**University setup failure.** The separate fresh JVM used the untouched
BuildAdviceCheck/LongPlaytest classes, but threw before `founded` completed:
`System.out` was null at BuildingCatalog:101 through
BuildAdviceCheck.quietly:129/founded:1253 and WG3CauseTrace:173. The fixture
JSONL contains only START, no month or complete row. Its exit **1** is this
exception, not the original university demand assertion. The checker
correctly stopped on the absent `FIXTURE_COMPLETE`; no second attempt or
automatic extension ran.

The diagnostic directly called `founded` without the initialization in
[BuildAdviceCheck.main](../../src/main/java/ham/citybuildersim/BuildAdviceCheck.java#L226):
`out` and `quiet` are otherwise null, and
[quietly](../../src/main/java/ham/citybuildersim/BuildAdviceCheck.java#L126)
installs that null quiet stream. Smallest proposed diagnostic correction:
copy the original main's Locale.CANADA/out/quiet initialization into the
target driver before calling the untouched fixture. The exact five-line
proposal is `target/wg3-cause/proposed-fixture-init.diff`, **not applied**.
No model, fixture condition, fee, wage, pool, policy, month bound or assertion
would change. Recommend a separately authorized, uniquely labelled,
university-only fresh JVM and isolated target-driver compile; the valid
housing capture needs no replay. New driver bytes/pin and execution scope
need review/GO before editing or running. No production/harness mend,
reclassification, waiver, kept rejudge, dry restart or readiness is implied.

## University capture completed with the original red law

Mail `20261121-160318-981-orchestrator-84ac` authorized one new university-only
box job. Only the exact five reviewed target-driver initialization lines
were applied; its SHA256 matched the independently specified
`642212538d658c0fd2cf8ae69c1cf36d34dcfb2435faf38f2feb9ce175820e73`.
The production/original harness/overlay sources remain unchanged. The new
job `/home/jerus/cbs/runs/WG3codex1-cause03` compiled **only** that driver into
an isolated empty-sourcepath directory, then ran one fresh `-Xmx2g` JVM with
the untouched frozen model/BuildAdviceCheck/LongPlaytest classes. No overlay
was on its runtime classpath and housing was not replayed.

Local ownership was 2026-11-21 **20:04:26–20:04:30 UTC**, four seconds including
preflight/upload/compile/run/home copy, inside the 90-second total bound.
Remote controller was 20:04:28–20:04:31 UTC, fixture 20:04:29–20:04:31;
small host clock skew is visible in the copy log. Job/controller/JVM exit
**1** and the complete marker records the original red university law,
without a setup exception. PIDs 2158884/2158881/2159005 are gone, active is
absent, lock released and the scratch namespace empty. **41/41** retained
remote/home hashes match, all **2,444** cached classes remain unchanged
(same `29134c44…` manifest), the three isolated driver class files verify,
all source pins and data hashes match. Receipts/raw/compiled inputs are
under `target/wg3-cause/runs/WG3codex1-cause03`, with terminal/copy receipts
in `cause03-terminal-receipt.txt` and `cause03-copy-receipt.json`.

The exact original `founded(...,9)` and quiet monthly loop produced **28**
rows, months **9–36**. Every required education field is present and every
JSON number is finite. At 36 population is **385**, would-come **0** against
the unchanged **25** floor; demand/month conjunction is false, no-row and
no-card are true. University student body and every in-flight cohort are
zero throughout. Native eligibility rises **41.178934 → 187.511875**;
affordability is **0.889129** at 9 and **0.845204** at 36. The native return
curve is exactly **0 in all 28 rows**, making willingness and would-come
zero. This identifies the zero component; it is not an empty eligible pool
or zero affordability. Native hires at 36 are **4.286519**, against five
ungated university posts, so the no-row/card outcome also respects the
unchanged hiring guard.

The native month-36 ladder records annual discount **3%**, loan **0%**, policy
**3%**, tuition subsidy **60%** and tuition scale **1**. The degree's best
ungated job is **UNIV_SCIENCE**, paid **0.162543266**, against diploma pay
**0.134432256**. Its 48-month course costs the family **0.599314676** in fees
and **6.452748300** in wages forgone. Native after-cost rows are:

| Adult stage | Working months before study | Diploma | University |
|---|---:|---:|---:|
| Young | 624 | 0.134048845 | 0.118268846 |
| Established | 420 | 0.133966302 | 0.108737149 |

Both stages are eligible. Their university after-cost row is below their
diploma row, and the native gain/curve yields zero. These are direct model
outputs, not a second formula or a substituted professional wage. The
played fixture therefore does not cause the positive after-cost return its
separate class-demand assertion needs. Recommend a harness-only causing
setup for that positive native return while keeping the original eligible/
affordable pool, 25 floor, month window and no-row/card premises. No new
positive fixture is implemented, no production change or red classification
is authorized, and the original red remains preserved.

The housing query's premises were also checked against
[the whole native match](../../src/main/java/ham/citybuildersim/FamilyModel.java#L1569):
its mutable inputs are the household matrix and seeker/sharing arrays,
all copied and equality-asserted; stock is supplied separately. Shape rules
and rounding use immutable model definitions. Owners are excluded by the
native census, not assigned invented doors in this query. The live
[month sequence](../../src/main/java/ham/citybuildersim/Game.java#L15989)
uses the previous education step's students, rebuilds families, matches/
squeezes/matches/records, then applies affordability sharing. The query is
after those phases and remains hypothetical. The earlier migration room
limits arrivals, while household reformation and study completion can still
change demand. This phase trace and the recorded residual do not select a
housing mend or establish a known unrelated cause; that finding remains open.

## Controlled university fixture mend

Mail `20261121-161024-305-orchestrator-09ea` authorizes a harness-only mend
to the founding case in `BuildAdviceCheck.thePipeline`. The source now
explicitly controls one existing ungated degree job's wage at **three
diploma wages**, through the existing LabourMarket wage-array API. In game
terms this is a well-paid graduate offer with the same few graduate posts,
not a claim about default Standard wages. The fixed input was chosen once;
no return formula, threshold search, fee/pool/post changes or extended
waiting window was introduced. The original default negative capture,
all 28 rows and its native inputs remain preserved above and in cause03.

New hard guards read native eligibility, affordability and each current
adult stage's after-cost return, and require genuine posts and hires both
positive and below `CityNeeds.SEATS_FLOOR`. The original demand assertion
still requires at least that floor in months 9–36; original no-row and
no-card assertions remain, with controlled-return labels. Earlier section-9
positive-post cases are unchanged. Incremental harness machinery numstat is
**18 additions / 13 removals**, net **+5** lines. The removed lines compress
the adjacent old pipeline narration and replace the obsolete default-wage
description; no test logic or finding assertion was removed.

Production model, save, UI, pom and ensemble-driver bytes remain identical
to frozen 6ede2fa3a. This harness-only change retains the valid 80-run WG3
ensemble. The authorized box verification used exactly one normal
`bash ./batch-check.sh BuildAdviceCheck` call on pushed source
`8ff9577eb3764e60cb75a15bb2d36d631478ac7a`: **exit 0**. Its compile (8s),
named suite (one harness, 7s), maps and Stale supply the same checks as
`--fast`, without a separate unchanged replay. Stale reports **0 firm**,
194 soft findings. No local JVM was started. The seven generated index/map
changes were copied from that wrapper's regeneration, not hand-merged.

The fixed setup succeeds at **month 9**, population **94**: rounded **26**
would come, against the unchanged 25 floor, with the same **five** actual
university posts and native hires **4.943570**. Native eligibility is
**41.178934**, affordability **0.889129**, and both young/established native
after-cost return curves are **1**. All new cause guards and original
month/demand/no-row/no-card laws pass. Earlier section-9 cases also pass:
the short city has 1,134 would-come and 164 hires against 600 first seats;
offices raise hires to 632 and the university row appears. No input tuning
or second harness run was needed.

The box run is
`/home/jerus/cbs/runs/WG3codex1-university-mend01`; foreground ownership was
2026-11-21 **20:18:15–20:18:43 UTC**. Actual remote job/controller exits are
**0**, PIDs 2197056/2197053 gone, active absent, no owned JVM and box lock
released. **664/664** remote/home retained hashes match; the **2,444**
compiled files verify, data hashes match and source-after diff is empty.
Source-tree manifests excluding only the mended harness match frozen
6ede2fa3a exactly, SHA256
`9714f3e5340ada049928629e33d5ca80456aede3c9f6764f4328a3f2d4eeeb4e`.
Native gate.env selected Temurin **21.0.13+9**, headless flags and the named
box earth/world files. Empty-host and local Java/javac/Maven refusal proofs
precede invocation. Wrapper, checks, class/source/data manifests and terminal/
copy receipts are preserved in `target/wg3-university-mend`; no source or
records changed during the live remote run. Prior pick **ALL 239** and review
**not needed** remain applicable; no picked full suite was run.

The housing evidence remains read-only and its hard red still blocks WG3.
No housing mend, red classification, waiver, kept rejudge, full pick,
playtest, ensemble replay, dry restart or readiness is authorized.

## Housing checkpoint: recorded refusal and the next public look

Mail `20261121-162526-137-orchestrator-ae4b` authorizes only saved-evidence/
source analysis. This checkpoint reads the retained cause02 JSONL and native
house/build/pop/labour/month CSVs locally. It starts no JVM, accesses no box,
changes no model/player/harness, and rejudges no kept result. The university
mend at a48e1f6a9 is accepted; the housing finding still blocks WG3.

The four requested months have the following **stored** placement ledger.
The shape entries are the last native match's unplaced matrix, before
[noteUnplaced](../../src/main/java/ham/citybuildersim/FamilyModel.java#L1804)
absorbs up to its native half-households-plus-one allowance. These are
recorded fields, not placements reconstructed from current shapes.

| Month | Family households now | Stored matrix unplaced | Native doubling allowance, exhausted | Stored raw residual | Original `>1` law |
|---:|---:|---:|---:|---:|---|
| 379 | 1,208.636542 | 605.563780 | 605.318271 | 0.245509 | last good |
| 380 | 1,215.507976 | 609.821972 | 608.753988 | 1.067984 | first bad |
| 386 | 1,249.124206 | 630.484471 | 625.562103 | 4.922369 | worst |
| 395 | 1,259.671893 | 631.788381 | 630.835947 | 0.952435 | below threshold again |

Every row has **80 size-2, 63 size-4 and 501 size-6 doors**, in both stored
match stock and current stock. Stored seeker-unplaced arrays are zero.
Fourteen owners remain outside this native match. The parity-checked
**CURRENT-fit hypothetical**, separately from that stored ledger, places
61.096781/61.488754/63.745715/65.716481 size-2 doors at these four months,
leaving 18.903219/18.511246/16.254285/14.283519 free; all 63 size-4 and 501
size-6 doors are used in each query. Actual historical by-size placed counts
were not recorded. The hypothetical cannot supply that missing flow.

The native family counters below are booked by
[rebuild/keepFormed](../../src/main/java/ham/citybuildersim/FamilyModel.java#L877)
and [bringHome](../../src/main/java/ham/citybuildersim/FamilyModel.java#L1027),
including the retired-band builder. They precede the housing valves and are
not a reconciliation of end-of-month household snapshots.

| Month | Kept | Re-formed | Newly formed | Adults booked back | Children booked back | Student census snapshot | Current adult-student snapshot |
|---:|---:|---:|---:|---:|---:|---:|---:|
| 379 | 1,285.790009 | 12.987778 | 20.601382 | 11.047097 | 9.676667 | 110.120094 | 99.013750 |
| 380 | 1,293.327477 | 13.063914 | 20.562505 | 11.106344 | 9.727485 | 99.013750 | 88.356301 |
| 386 | 1,332.206037 | 13.456627 | 18.911313 | 7.900261 | 6.915147 | 45.529903 | 39.427841 |
| 395 | 1,352.354746 | 13.660149 | 12.463516 | 0 | 0 | 9.164844 | 7.115371 |

`getLastBackAdults` is specifically the model's bounded allocation when
adults at home decrease: it includes the at-home groups, not solely students
or graduates. Its children use the previous at-home dependants rate. The
driver did not log `getLastBackChildrenHome`, so even the children-booked-
back figure is not substituted for that separately recorded placement.
The census uses the prior education step's students; the later
[education step](../../src/main/java/ham/citybuildersim/Game.java#L16106)
then applies band flows and replaces PopulationManager's studying snapshot.
There is no student-exit or household-creation flow inferred by subtracting
those snapshots. In particular, the saved trace omitted Education's native
`getFinished`, net graduate/licence flows, local per-course intake and the
applied attrition components. Cohort arrays and labour supply/filled counts
are stocks; they do not fill that gap.

Recorded arrivals/room are 0.442251/2.211319 at 379, then zero at the other
three months. Recorded departures are 2.557566, 2.566615, 2.609898 and
15.562608; births are 10.293083, 10.306077, 10.374242 and 10.355633. At 395
the native declining-share field is 0.6863, against zero at the first three
months. These are actual demographic/migration fields. They do not isolate
how much household reformation belongs to course completion, ageing,
employment or any other input.

### Which decision prevented a new home?

The native house CSV's `why` is **not a fresh query**:
[traceHouse](../../src/main/java/ham/citybuildersim/LongPlaytest.java#L3049)
reads `Game.getLastInvestment(Real Estate)`. At every saved month 350–400 it
records the same actual decision, with CSV comma escaping:
`Holding: no land - needs 5;570 m²; 3;060 m² free`.
[The monthly private pass](../../src/main/java/ham/citybuildersim/Game.java#L3984)
asks RealEstate.plan and [consider's holding branch](../../src/main/java/ham/citybuildersim/Game.java#L7053)
records the reason and returns **before** the credit/equity/mortgage tests.
The primary playtest does not hold Real Estate out of that pass. Thus the
recorded private no-order reason is land, not a lender refusal inferred from
a cash balance.

[RealEstate.plan](../../src/main/java/ham/citybuildersim/sectors/RealEstate.java#L668)
first ranks templates by fillable income/cost after the native rent hurdle.
Only after selecting `best` does it ask
[`plotsAvailableFor(best)`](../../src/main/java/ham/citybuildersim/sectors/RealEstate.java#L714);
zero plots returns `Decision.noLand`, whose actual area wording comes from
[BusinessInvestment.landReason](../../src/main/java/ham/citybuildersim/BusinessInvestment.java#L537).
The shipped catalogue's Low-Rise Apartments need **60,000 ft²** (about
5,570 m²), matching that refused footprint. A House needs **8,000 ft²** and
a Studio block **25,000 ft²**; both are physically smaller than the rounded
3,060 m² free. House is family-sized; Studio's adults-only rule cannot be
treated as a family alternative. The trace has no per-template hurdle,
fillable-income/cost or alternative-finance result, so it does **not** prove
that a House was profitable or could have been financed in this city.

| Month | Recorded private hold | Landlord cash snapshot | City cash snapshot | Real-estate sites/owed points | Total queued points |
|---:|---|---:|---:|---:|---:|
| 379 | selected footprint does not fit | 284.876 | -211.286 | 0 / 0 | 0 |
| 380 | selected footprint does not fit | 285.571 | -207.910 | 0 / 0 | 0 |
| 386 | selected footprint does not fit | 290.547 | -116.739 | 0 / 0 | 0 |
| 395 | selected footprint does not fit | 302.397 | -144.777 | 0 / 0 | 0 |

The build CSV proves no queued housing was waiting for completion in this
window. Landlord cash is not proof of its ability to pay for a block, and
negative city cash is not an actual failed borrowing decision. The `noMoney`
CSV value is a **cumulative** sum of build refusals, six throughout this
window ([traceMonth](../../src/main/java/ham/citybuildersim/LongPlaytest.java#L2836));
it is not six new housing refusals each month.

For the public player, there is a source-backed control-flow deduction from
an executed anchor, not a reconstructed money flow. The first original
round trip is recorded at **373**, slot 1; in the unchanged
[main loop](../../src/main/java/ham/citybuildersim/LongPlaytest.java#L6933)
that is stop 7. The saved launch has no attentive-player flag, so the next
stop is 8, whose [24-month skip](../../src/main/java/ham/citybuildersim/LongPlaytest.java#L6841)
runs 374–397 **before** its `advise` calls. The whole 380–394 failure interval,
and recovery below one at 395, are inside that skip. The housing branch of
`advise` is therefore not visited during the failure interval. No shortened
skip or extra public intervention is proposed to make the audit pass.

The last look's exact move ranking/attempts and land/funding quotes were not
captured: main buffers those decisions for its final report, and the planned
bound ends before that report. Source says
[keepGroundAhead](../../src/main/java/ham/citybuildersim/LongPlaytest.java#L4530)
has a nonnegative-cash budget, while [public build](../../src/main/java/ham/citybuildersim/LongPlaytest.java#L5651)
tries land and then, if needed,
[canService](../../src/main/java/ham/citybuildersim/LongPlaytest.java#L5804)
tests quoted interest against `DEBT_SERVICE_LIMIT`. Those are possible
constraints, not proof of which one rejected a move at the preceding look.

### WG3's mechanical ownership and the smallest next fixture

The source comparison against inherited published .180 `d1d70f0ae` is exact:
FamilyModel, Migration, RealEstate, BusinessInvestment, SimulationEngine and
the production LongPlaytest are unchanged. That fact grants no latent-fault
waiver. Game adds one construction-time live finance/stage binding at
[508](../../src/main/java/ham/citybuildersim/Game.java#L508); no housing-phase
call moved. Education now reads fees/duration/forgone pay, actual wages,
live discount and stage-specific life through the pure helper; it also
refreshes the existing stage reference on advance. Pipeline shifting,
attrition, the intake cap and the later band/studying updates are inherited.

This matters on the **observed inputs**, not just a moved town: at 386 the
paid college wage 0.435162 exceeds diploma 0.278522, which makes inherited
[gross returnOn](https://github.com/2048Israel/CityBuilderSim/blob/d1d70f0aee8312bb86c888f8cf9831c71322754b/src/main/java/ham/citybuildersim/Education.java#L680)'s
old branch positive. WG3's native young after-cost college row is 0.264681
against diploma 0.273943; established is 0.263221 against 0.273903. Both
now give zero. The native discount is **13.389654%**, exactly the live policy
rate, with loan rate zero. The college's native return is 0.026333 at 379,
0.017447 at 380 and zero at 386/395; adult college body is respectively
99.013750/88.356301/39.427841/7.115371. This identifies WG3's changed decision
mechanism on this state, **not** a claim that inherited .180 would reach the
same state, or a measured completion-to-family causal decomposition.

Existing evidence supports **one controlled real-API planner fixture** as
the next narrow test, not a housing mend yet: test whether ranking a home
that cannot fit suppresses a profitable family-sized alternative that can.
Use the native House/Low-Rise templates, `RealEstate.setSegments`/rent
setters, `BusinessInvestment.setLandAvailable`, `plotsAvailableFor`,
`getCostOf`, `Game`'s native housing hurdle and `RealEstate.plan`. Declare
the family demand/rent/finance inputs once, without threshold search. Hard
guards must establish unmet family demand and positive cost/native rent
hurdle acceptance for **both** templates; a House must have a plot.

First give enough land for the Low-Rise and require the native plan to
select it. Change **only available land**, keeping its unit price and all
other inputs fixed, to one House's footprint: House still has a native
plot, Low-Rise has none. Capture the real plan result. A land hold then
isolates the pre-finance selection gate without copying the planner's score
formula. If any fixed precondition fails, stop and report; do not tune until
red/green. This would establish a planner issue only, not that the retained
city's House finance would succeed or that its home red is resolved.

Recommend static preparation/review of that one target fixture first. Any
later execution needs explicit central slot GO: one fresh scratch-only JVM,
target-only empty-sourcepath compile against untouched frozen 6ede model/
dependencies on **gatebox**, native gate.env/Temurin 21.0.13+9/headless and
named box data, source/class/data pins before/after, local fallback blocked,
**30 seconds remote / 60 total including copy**, no seed replay/retry.
No such fixture source, compile or execution was created in this checkpoint.
If a native feasible-alternative guard cannot be established, the exact
missing observation is the rejected House's native hurdle/filled doors/
cost/plot result **inside RealEstate.plan before selecting best**, alongside
the selected Low-Rise's same fields; end-state cash cannot replace it.
Do not replay a seed merely to retrieve the whole education or player log.
No production/player/harness mend or red classification is selected here;
the original `home > 1` law, 15 raw findings and provisional marker remain.

## Static target planner preparation for review

Mail `20261121-164307-265-orchestrator-f786` authorizes preparation only.
The actual ignored source is
`target/wg3-planner/driver-src/ham/citybuildersim/WG3PlannerTrace.java`;
`driver.diff`, `README.md`, `preparation.json` and retained class/dependency/
data manifests sit beside it. Driver SHA256 is
`0fe69a94deb4129fb2e1c90aa44ceb06b9aeedc2fecc58117010489c30f35e6d`;
the full addition diff is
`4b1fc364f35c6a0eff59b5dda791a9e5b7d351845f98f877eca00498df786851`.
Only file/hash/JSON and source-signature inspection occurred: **no compile,
typecheck, JVM, box access or poll**. No scenario or threshold was tried.

One fixed controlled census is formed by native
`PopulationCohorts.reset/migrate(4 * Low-Rise.capacity)` and
`FamilyModel.rebuild`, with native age/stage/shape rules, all adults declared
working in the first PayTier and no outside/owner/seeker adults. Native
segment/head accessors and standing by-size stock feed RealEstate's ordinary
setters. Guards require both adult stages, family demand beyond a whole
Low-Rise and no construction queue. Segment rents are fixed once at 10,
the existing HousingCheck planner-fixture input; all catalogue templates and
native maintenance/tax/hurdle/quote rules remain available and unchanged.

The diagnostic uses **native** `getCostOf`, `estimatedMonthlyProfit`,
`housingBuildHurdle`, `Game.quoteBuild`, sector cash/principal/rate and
`pastDefaultPoint`. Both positive native rent hurdles must be cleared, with
finite positive costs/quotes. Declared owner cash is seeded once at 1,000
times the larger native one-unit cost/quote and must cover both alternatives
and the ample order; no loan or finance transaction is executed. This is a
cash-financed controlled planner input, not a substitute lender success flag.
There is no planner score or latent-demand/GDP formula in the driver.

The ample arm has the native Low-Rise footprint and must natively plan exactly
one Low-Rise. The second arm changes **only** BusinessInvestment's available-
area view to the native House footprint, at the same native unit price;
House plots must be positive and Low-Rise plots zero. Native demand, full
costs, quotes, hurdles, rents, cash/principal/rate, jobs/stock and month must
remain identical, and the native family census must remain bitwise equal.
The Game quote's physical-land view is logged separately and stays fixed:
this seam test does not pretend to execute a land purchase or building.
All native templates remain eligible; failed ample selection or any other
premise yields LIMIT, without input tuning or a retry.

Original 6ede mechanics are expected to return a narrow land hold, since
selection precedes the plot gate. The target records that complete
observation with exit 1; a natively selected House is an observation/0,
never a green gate. Other outcomes, failed premises, nonfinite required
fields, exceptions or cleanup failure are INCONCLUSIVE/2. This does not
establish a normative planner mend or clear the original `home > 1` law.
Original-month House hurdle/finance and prior-look flows remain missing;
the accepted 5570/3060 land refusal, 373→397 public skip and observed-state
after-cost owner comparison retain exactly the limits stated above.

The prospective isolated compile uses only this target source, empty
sourcepath and frozen **6ede2fa3a** model classes/dependencies; runtime is
driver-classes:base-classes:DEPS with no model/harness overlay. The retained
2,444-class manifest is `29134c44…`, exact dependency classpath `fbc5cc67…`;
full pins/source blob ids and Earth/world hashes are in preparation.json.
Native gate.env must select Temurin **21.0.13+9-LTS**, with named box data,
headless flags and local fallback refusal. Future cached-class/dependency/
data before/after checks and actual terminal/home-copy receipts remain owed.

Only on separate central execution GO: **one fresh BOX model JVM, 30 seconds
remote / 60 total including preflight/compile/copy**, no monthly/seed replay,
parallel run, extension, retry or laptop fallback. Explicit GameFiles live
under one TempDirs child; validated child-only, non-following cleanup emits
a marker, followed by the native shutdown cleanup. The README names every
accessor, phase, command/classpath and required receipt. No runtime wrapper
or execution started. Production/harness source, accepted university proof,
valid ensemble and provisional marker remain unchanged; housing is unresolved.

## Planner execution: hard ample-arm LIMIT, no narrow arm

Mail `20261121-170843-048-orchestrator-89b2` authorized exactly one bounded
box execution of the reviewed driver, with no edits or tuning. The original
driver `0fe69a94…` and diff `4b1fc364…` matched before compilation and remain
unchanged. The unique run is
`/home/jerus/cbs/runs/WG3codex1-planner01`; all partial evidence is retained
locally in `target/wg3-planner/runs/WG3codex1-planner01`.

Isolated target compilation exited **0**, producing only three driver class
files. One fresh model JVM ran. Actual model/job/controller/client receipts
each report **2**; the outer PowerShell tool reported 1. The raw events are
**START → AMPLE → LIMIT → CLEANUP_COMPLETE**, with no NARROW or COMPLETE.
The exact hard premise that stopped it is
`ample native plan is not exactly one Low-Rise`.

At the ample 60,000-ft² planner view, native House plots are **7**, Low-Rise
plots **1**, but the real plan chooses **three Houses**, `build=true`,
`landBlocked=false`, with reason
`16 capacity needed for demand plus headroom against 100 units`.
The ample-order finance check, second area's assignment and all subsequent
narrow/parity guards were **not executed**. No scenario or threshold changed
and no retry ran.
The proposed two-arm mechanism was therefore **not isolated**; this is a
LIMIT/INCONCLUSIVE result, not evidence for a mend or a housing-law pass.

The preceding strict controls did hold: native family seekers/door shortfall
**238.521384**, studio seekers **295.408873**, family/studio heads
**585.548570 / 393.878498**, both adult stages present, no queued
construction. Native unit land price is **0.000011776976048555181**, rents
are the declared 10, native jobs **49**, housing capacity **100**, and actual
landlord principal/mortgage rate are zero. Declared cash is **498,095.440223**,
covering the native one-unit costs/quotes and clearing the native default
guard before the ample plan.

| Alternative | Native expected rental receipts | Native positive housing hurdle | Native planner cost | Native Game build quote |
|---|---:|---:|---:|---:|
| House | 60 | 0.034244444 | 8.194217435 | 8.100001626 |
| Low-Rise | 2,520 | 1.303899449 | 498.095440223 | 497.388821660 |

The receipts column is exactly RealEstate's `estimatedMonthlyProfit`
accessor output: expected rental receipts, **not net earnings** and not the
planner's separate full-capacity comparison. Its stricter existing guard
was preserved. The Game quote's physical-land-free view is separately
**2,982,568.603137 ft²**; only the declared planner availability was 60,000.
These controlled values do not establish original-month House profitability,
finance, past public-look outcomes or student-to-family flows.

Local foreground timestamps are **2026-11-21 21:16:05–21:16:08 UTC**. The
remote preflight begins 21:16:06; its after-home-copy bound receipt records
**3 seconds**, within 30 remote/60 total. Job/controller and compile run
21:16:07–08; model start/end both read 21:16:08 at one-second precision.
No deadline/priority/provenance guard failed. Actual job/controller/model
PIDs **2375348 / 2375343 / 2375498** are gone, active is absent and box lock
released. The explicit scratch child and PID directory are absent; only
the empty TempDirs namespace remains.

All **57/57** remote/home retained file hashes match. Before/after checks
match **2,444 cached classes**, **660 source files**, **10 dependency jars**,
both data files and three JDK release/launcher files; the base copy and
three compiled-target hashes verify too. The class/dependency-list/data pins
match the retained frozen 6ede inputs, and native gate.env selected strict
Temurin **21.0.13+9-LTS**. Jar byte manifests were acquired in this run and
prove **current before/current after** equality only; no historical jar-byte
identity is claimed. Remote/controller/compiler/model logs, raw JSONL,
source/class/dependency/data/JDK manifests, actual exit files and terminal/
copy/bound receipts are preserved. `planner01-copy-receipt.json` is local
verification; the original partial result was not changed or rejudged.

No driver, model, player or harness mend follows this failed control. No
new run is authorized. The original played-city 5570/3060 land refusal and
373→397 public skip remain separate evidence with their existing limits.
The hard housing red, Pending status, university proof, valid 80-run
baseline and provisional marker remain unchanged. End for root review.

All local worktree JVMs have named main's absolute earth-2.bin and world-1.bin
through JDK_JAVA_OPTIONS; the box ensemble/gate name their own main copies.
Game checks use scratch saves only. No baseline suite of main, standalone
full pick or separate LongPlaytest replay was run. The one authorized ensemble
and the failed dry run are recorded above. Both diagnostic attempts and their
limits are recorded above; no dry restart, kept rejudge, model/harness mend
or further diagnostic execution is authorized.
The ship file remains provisional until the orchestrator's readiness GO.

For the optional PC eye check: open Policy → Wages → The ladder, switch both
adult stages and inspect the common zero, negative outlines, muted no-post
rows, costs and faint wage-walk ticks at a narrow window width. High school
still runs on coverage and affordability. This is an inspection note, not a
Pending row.

## 2026-11-25: the restart

Builder WG3c (Claude Opus 5.5, xhigh), started by the orchestrator once WG3
was back in Queued (2026-11-24). It took the branch over at 98989a29c. The
sections above are the earlier Codex session's record, and this one is the
record from here on.

**The merge.** origin/main 304506de2 (0.9.203, format 66) went in at
cdafdc612:
- AllChecks' registry was joined by hand: main's lines, then WG3's two
  harnesses.
- The generated docs were taken from main and regenerated.
- CLAUDE.md's interface count went to fifty-three (`ui/SchoolingProbe`). That
  was Stale's one firm finding.
- No format bump. WG3 saves nothing: Education's three new fields are
  transient, and Game binds them at construction.

**The trace.** A slice helper ran it on cdafdc612, before any mend.
- BuildAdviceCheck: main's section 9 on the merged code is red. The founding
  reaches month 36 with 329 people and 0 who would come, against 25. It has 5
  university posts, a studyReturn of 0 for both adult stages, eligible 155.5
  and affordability .84. The diploma pays 0.13 and the best ungated degree job
  (UNIV_SCIENCE) 0.15. Main's kept runs (R1g-EyG2IaHs, FC2-zHb6IoDf) show month
  11, 116 people and 27 who would come, because on main a degree's "after" is
  the doctor's wage. The red is the merged code's alone.
- LongPlaytest: green on the merged code, exit 0, "findings: Nothing". It was
  run directly, because batch-check's 1,740 s cap stopped it at m2280 on the
  loaded laptop. 0.9.180's households-with-no-home red does not reproduce on
  0.9.203's city. The 103,224 evictions over the run are at main's scale
  (FC2's gate 180,928, R1g's 132,533).

**The double count: a defect, mended.** The capture above shows a degree
paying 1.21 diplomas coming out at 0.118 a level month, against the diploma's
0.134.
- The cause: the row put a degree's earnings off until after its course AND
  took the wages given up off it. That is ladder.py's
  `v = earn - fees_at_18 - given_up_pv`, copied line for line.
- Why that is twice: the path that studies earns nothing in those months, and
  the diploma's row earns them. So the gap between the two rows already holds
  the wages given up, and taking them off again counts them twice. Becker
  (1964, *Human Capital*): the cost of schooling is its fees plus the earnings
  forgone, counted once. The world does not do this, so it is mended.
- A row is now its whole path: every fee along it and its earnings once its
  courses end. A degree's path includes the high-school bill, and a licence's
  includes the university bill. The first cut left those off the higher rows,
  while the diploma's row paid its own.
- `forgone` stays the page's figure for the course's bill. ladder.py is
  mended the same way, and spec-wages 3.5 says so.
- SchoolingLadderCheck books a path's cash and no wages forgone. Its new
  section, "The wages given up are counted once":
  - a free degree that pays the diploma's wage costs four years of that wage,
    counted once;
  - at the break-even wage the gain is 0, to 1e-9;
  - 1% above it is a gain.

  With the double count put back, the section fails (0.572 against 0.286) and
  17 assertions fail in all. With the mend it passes.
- "A higher studied-from wage..." now reads: it raises the wages forgone and
  lowers the step's gain, and leaves the college path's own value unchanged.

**The university red, restated to the real law.** With the mend, the
founding's degree at month 36 pays 1.15 diplomas and comes out −3.2% after
cost (young). Four years out of work at 3% need about 1.17 diplomas just to
break even (624 working months against 576).
- Does a real university draw students when its degree barely covers its
  cost? No. Enrolment follows the return: Becker 1964; Freeman 1976, *The
  Overeducated American* (young men's college enrolment fell when the college
  premium fell in the early 1970s); Card and Lemieux 2001, QJE 116(2).
- So the model is right. The premise that a class would come to the
  founding's university came from the doctor's wage.
- Section 9 is restated. Its javadoc keeps 0.7.51's text and adds WG3's.
  - The native founding, months 9 to 36, gets no row and no card, which still
    holds.
  - New assertion: its would-be students follow the degree's pay after cost,
    and none come without a gain.
  - The hiring guard is still caused, on a second founding with the earlier
    session's controlled offer: one existing ungated degree job at three
    diplomas. At month 9, 26 would come against 5 posts and 4.9 hires, and
    there is still no row.

**Calls taken:**
- ★ The mend departs from the spec's "ladder.py is its prototype, line for
  line". The prototype's arithmetic was wrong, and a defect is mended.
  Spec-wages 2.2's ladder.py figures (1.57×, 1.92× over no diploma) are a
  study's table, not a check. The spec now says they predate the mend and
  understate the steps, and they were not remeasured.
- A row counts every fee along its path, so the ratio compares like with like.
  The fee shown stays the course's own.

**Checks on cfd768a0d (merged and mended):**
- `--fast`: exit 0, 0 firm.
- SchoolingLadderCheck, SchoolingPageCheck, EducationCheck and
  BuildAdviceCheck: exit 0.
- KeptCheck's section 77 walks every fixed Policy page, The ladder among them
  (`KeptBench.policyPages()`). That is the page's Kept proof, and it runs in
  the pick.
- The pick is ALL (AllChecks, Game). The review line says "not needed".
  Schooling moves money flows, so the orchestrator orders the brief's money
  and saves review.
- LongPlaytest, mended (direct run, `target/checks/LongPlaytest-mend.log`):
  exit 0, "All checks passed", no households-with-no-home finding.
  - An observation, not a check: 342,208 evictions over the run and a most of
    63,153 with no home (m2116). The pre-mend run had 103,224 and 22,179, and
    main's kept runs 132k-181k. One run of a chaotic city settles nothing; the
    ensemble's spread is the judge.
  - Students at m2400: 47,522 here, against 147,851 before the mend.
- KeptCheck: known red only, main's HT1-H note line in `docs/notes/gate.txt`.
  "Wages › The ladder: the redraw and the next equal a page built new OK":
  the ladder page is drawn through Kept.
- The housing red is not yet called traced. R1g moves LongPlaytest's city,
  so once R1g ships, main is merged again and LongPlaytest re-run on that
  city, before the ensemble.

### The review (REV-WG3: SHIP, no P1) and its two points

The orchestrator's reviewer agreed the double count was real and the mend is
the standard return. The founding's 1.15 is the live founding market (the
base ladder is 1.82, inside Goldin and Katz's range), so section 9's
restatement stands. Both points are done:
- **P2: fees already paid are sunk for whoever decides.** `View.gain()` used
  to divide by the prior row's whole-path value, fees included. Now both
  values are taken net of the fees on the way to the prior step
  (`Row.pathFees`, a new record component, a level month). A degree under
  nothing after cost still leaves its holder a licence worth taking.
  - SchoolingLadderCheck's new section, "The fees already paid are sunk for
    the next step": a degree earning half its path's fees reads under 0
    after cost, and the licence's gain equals the ledger's (its own bill
    against the degree's pay).
  - With the old base put back, the section fails (gain 0.0 against 1,903.7,
    2 FAILs). With the new base it passes.
- **P3: one source for the page and the intake.** The page reads
  `Education.schoolingLadder(market, stage)`, the same call enrolment reads,
  in place of building `SchoolingLadder.of()` from the owners itself.
- Re-run after both: SchoolingLadderCheck, SchoolingPageCheck,
  EducationCheck and BuildAdviceCheck all exit 0, and `--fast` exits 0. The
  founding is unchanged at 1.15 diplomas and −3.2% after cost.
- KeptCheck was not re-run for P3. The view it draws is the same object from
  the same owners, so the drawing does not change.

### The ensemble (the review's go) and its gate condition

**The 80 runs.** On 4686bd100, the final model with P2 and P3, on the gate
box: 80 of 80 runs, scored here against `docs/ensemble/0.9.203.md` and
committed as `docs/ensemble/WG3.md`. **PASS.**
- Hard criteria 10 of 10: M1 0 months and F1 0/16 in every policy. F1
  counts LongPlaytest's own findings, "households with no home" among them,
  through `LongPlaytest.runPlayer`, so no run of any policy raised it.
- 25 of 26 criteria (one retired), no verdict changed.
- The trace moves, modestly. Medians against 0.9.203's:
  - A1: 2.68% [2.20 to 3.05], against 2.75%.
  - A2: 16 months [6 to 24], against 14 [4 to 37]. FAIL in both, a soft
    criterion.
  - S1: 2.63%, against 2.63%.
  - K1: 16.07%, against 15.39%.
  - R3: −2.64%, against −2.73%.
  - T1: 16/16 seeds reach 40,000 people at m308, against m320.
  - T2: 9,206,904, against 9,023,330.

**Evictions and households with no home against main's spread.** The
ensemble has no eviction column, and its baseline has none either.
- ★ Call taken: a paired diagnostic, not a baseline. It is AUTO2's 16 seeds
  on the box, run once on WG3 (9a37b27ce) and once on main (304506de2, a
  detached worktree, removed afterwards). Both runs add
  `-Densemble.extra=evicted=getHouseholdBalance.getEvicted;unhoused=getUnemployment.getUnhoused`.
- Both runs are in `target/ensemble/WG3-evict` and `WG3-evict-main`, and
  `target/diag/evict.sh` sums them. Over 2,400 months:

| AUTO2, 16 seeds | main | WG3 |
|---|---|---|
| evictions over the run | 54,200 [7,828 to 179,634] | 89,989 [14,473 to 176,383] |
| the most unhoused in a month | 11,515 [718 to 77,571] | 14,872 [1,927 to 37,912] |

- WG3's range lies inside main's on both. Seed for seed, WG3 is higher in 8
  of 16 seeds (evictions) and 10 of 16 (the worst month), so there is no
  shift beyond a chaotic city's own spread, and no trace is owed. The
  no-home finding is F1's 0/16.
- LongPlaytest's single city (342k) is one draw from this spread.

**The grown cities' degree pays.** The reading is an uncommitted copy of the
driver (`target/diag/src`), with the ratio of the best ungated degree wage
to the diploma wage and the gain after cost added as columns. AUTO2, run
here to m1200:
- seed 0: 2.29, 2.76, 2.32, 2.54 and 2.45 at m240, m480, m720, m960 and
  m1200;
- seed 1: 2.64, 2.59, 2.48, 2.42 and 2.46.

Both are above 1.2 everywhere past the founding (m36: 1.34), so no
labour-market row is owed. The degree's gain after cost runs 0.6 to 1.3,
near or past full willingness, while university students sit at its seats
(2,000, then 4,000-6,000). That is the page's "too few seats" verdict, and
the auto-builder's to answer, not WG3's.

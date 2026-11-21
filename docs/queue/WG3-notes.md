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

All local worktree JVMs name main's absolute earth-2.bin and world-1.bin through
JDK_JAVA_OPTIONS; the box ensemble names its own main copies. Game checks use
scratch saves only. No baseline suite of main, full pick, separate LongPlaytest
or dry run has been started by this builder. The one authorized ensemble is
complete as recorded above.
The ship file remains provisional until the orchestrator's readiness GO.

For the optional PC eye check: open Policy → Wages → The ladder, switch both
adult stages and inspect the common zero, negative outlines, muted no-post
rows, costs and faint wage-walk ticks at a narrow window width. High school
still runs on coverage and affordability. This is an inspection note, not a
Pending row.

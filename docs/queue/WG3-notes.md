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
- Merge of R1g's published main and the final-model ensemble are still
  owed, followed by a coordinated dry run because the pick is ALL.
  Both require the orchestrator's GO; no readiness claim is made now.

All worktree JVMs name main's absolute earth-2.bin and world-1.bin through
JDK_JAVA_OPTIONS. Game checks use scratch saves only. No baseline suite of main,
full pick, LongPlaytest, ensemble or dry run has been started by this builder.
The ship file remains provisional until the orchestrator's readiness GO.

For the optional PC eye check: open Policy → Wages → The ladder, switch both
adult stages and inspect the common zero, negative outlines, muted no-post
rows, costs and faint wage-walk ticks at a narrow window width. High school
still runs on coverage and affordability. This is an inspection note, not a
Pending row.

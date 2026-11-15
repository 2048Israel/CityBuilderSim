# XR7 - The world's pegs overshoot both ways, and the hold capped

Built on main ade96cfb (0.9.115), on batch/XR7. XR8 is folded in.
No known-reds entry names XR7 on this starting main.

## Decisions and scope

- The world's break adds ForeignAccounts.OVERSHOOT times the signed gap,
  as the city's break does. The hazard remains hazard(max(0, gap)): Jerus's
  call moves the landing, not the chance of a strong-side break.
- ★ A chosen peg breaks before a purchase greater than HOLD_PURCHASE_YEARS
  (one year's GDP in a month); a band gives way for that month at the same
  bound. Above this estimate the first bill alone exceeds annual city income.
  Test the lift against interventionMove(bound), before evaluating the
  exponential inverse. Keep the calendar's Canadian-gold peg outside this
  player-peg limit, as its exhaustion rule requires.
- This changes XR5's premise that a purchase never breaks a peg. Its usual
  held-month equations remain laws below the bound. A bound must release the
  hold: buying only part and still claiming the target would hold it for free.
- Main already saves holdFelt in ForeignAccounts slot 147, DataSave's
  foreignAccounts, Game.fillSave(), and Game's loaded foreign.restore().
  Prove a nonzero supported peg crosses those paths and pays only its fading
  support next month. Do not reconstruct missing support or convert old saves.
  Main's oldest readable format is already 53, newer than XR5's support.

## Fixtures before the behaviour change

target/checks/XR7-before.log, regression exit 1, with only the dial and appended
break cause introduced: 3x costs 8.939470420466048 years of output and 5x costs
114.96232536607569; both buy without breaking, the 1.01-bound and band cases fail.
Five seeded world breaks include four weak and one strong; the strong landing
has gap -0.1552103291150757 and excess zero, failing the signed landing and fade.
The same fixtures must turn green; no premise was changed to excuse a failure.

Initial and fixture builds: batch-check.sh --fast passed, Stale 0 firm findings.

## Implementation and focused checks

WorldMoneys adds the signed gap to its own swing through the city's existing
OVERSHOOT dial. ForeignAccounts checks the positive lift against
interventionMove(HOLD_PURCHASE_YEARS * annualGDP, annualGDP) before invoking the
inverse. A peg breaks with appended cause PURCHASE_LIMIT; a band stays a band
and deals nothing for that month. Prior saved cause ordinals are unchanged.
The calendar's Canadian-gold peg and the Gold regime retain their own rules.

★ Why one year rather than the inverse's .1-year knee: a 50% devaluation costs
about .28 years of output on this rule, which remains an available costly
choice. A doubling costs about 1.18 years and must release the hold. One year
bounds a monthly bill at annual income while leaving room for those large
devaluations. The dial's own sentence states both sides of that choice.

- Final narrow regression: target/checks/XR7-final-regression.log, exit 0.
  The identical pre-change fixtures now pass, including 3x/5x, .99/1.01 of the
  bound, band release, signed break landing, and saved purchase-limit cause.
- CurrencyRegimeCheck passed all its existing sections and the new section 19
  in target/checks/XR7-named.log. No existing premise or assertion was relaxed.
- SaveFileCheck and ReadPathCheck passed in target/checks/XR7-save-read.log,
  exit 0, on one JVM at a time. The live chosen peg's support is
  0.404216327; the next controlled purchase needs 2871.095527740, versus
  17379.176037331 with only its support erased. The actual reload preserves the
  whole foreign-account array to the bit. The following real month settles
  1994.499972332 (0.2776 years of output), with audit residual exactly zero.
- MoneyAudit.tolerance at the original 3x/5x bills (643641.870274 and
  8277287.426357 local thousands in the bare fixture) stays at CENT,
  0.000258834638. The bound's actual settlement also closes at this cent;
  neither tolerance nor audit laws changed.
- Final WorldMoneysCheck: target/checks/XR7-world-final.log, exit 0. The
  seeded paths counted 43 strong-side and 460 weak-side breaks on the fixture,
  and 4452 strong-side and 24062 weak-side breaks on the full world file.
  The separate signed landing/fade fixtures found both signs, and reloaded
  breaks continue to the bit.
- Builds and maps regenerated; Stale has 0 firm findings. Source diff whitespace
  check is clean. The generated indexes retain their generator's line endings
  and Markdown hard breaks.
- During test construction the initial read fixture ran ahead of output-stream
  initialization, and an erased-support control hit the new purchase limit.
  Both were corrected before the final checks: initialize first and compare
  required purchases even when the erased control cannot afford its hold.
  These are fixture repairs, not changes to a prior harness premise.

## Save compatibility

No saved field is added: XR5 already wrote and restored support at slot 147.
The new checks cause nonzero support, exercise DataSave JSON and the real loader,
and expose the extra bill if that slot is erased. The older array fallback is
unchanged; no conversion was added. SAVE_FORMAT and OLDEST_LOADABLE_FORMAT stay
53: no currently readable save gains a missing-support problem in this batch.
The new break cause is appended, and its full save-array round-trip is checked.

## Wide run, ensemble and review

The orchestrator instructed XR7 on 2026-11-15 to use its launcher dry run as its
final pick, rather than run the local 62-harness pick as well (FP1 held the heavy
slot). Code/fixtures final, then stranger's Codex review, then ensemble permission,
then dry-run permission after the review and ensemble. No local ALL or full suite.
Latest fetched main before final-model publication: ade96cfb, still 0.9.115.

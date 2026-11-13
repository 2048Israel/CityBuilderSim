# EI1 - The ensemble's outcome criteria as information

2026-11-13. Built on batch/EI1 from origin/main d289e7d8 for Jerus's
"yes queue it and do it next". Process batch, no tag.

## Built

The scorer classes M1 (money audit) and F1 (findings) as hard. Every other
criterion defaults to info. Each retains its measurement, threshold and
verdict. The table prints class, current readings, baseline readings and
baseline verdict. Separate changed-verdict lines name each change.
The existing overall criteria-passed line remains. A separate batch verdict
counts hard criteria alone. Summary JSON retains criterion readings.

Old Markdown baselines supply their verdicts from their existing table.
New JSON carries readings directly. Old summary-only JSON continues to work
and shows unavailable where the previous verdict was never stored.
The runner propagates the hard-only verdict and retains its score, summary
and baseline when hard criteria fail. Its self-test companion is discovered
by the publisher's existing changed-script self-test mechanism.

The batch rules, orchestrator gate rules and ensemble README state the hard
criteria and informational reporting. The scorer's long premise history and
the runner's duplicated introduction were shortened to summaries and pointers
to the existing batch notes. Thresholds and measurement calculations stay as
written.

## Calls

- ★ Keep F1's existing baseline-relative comparison and the existing RETIRED
  H1 and VACUOUS H2 behavior. EI1 changes which verdicts stop shipping.
- ★ Use exit 4 for a scored hard failure, matching the checks' new-red status.
  Keep exit 2 for scoring errors and exit 3 for missing runs. Retain all
  scored artifacts before returning 4 so the author can inspect every row.
- ★ Keep the compatible criteria-passed line and add a distinct hard-only
  batch verdict. Existing baseline readers still recognize the stored page.
- The starter clarified by cbs-bus that the brief template is its builder
  prompt outside the repository and it will update that itself. That item is
  outside this branch. No Claude memory file was edited.
- No known-reds entry names EI1. No game code, resources, ensemble driver or
  playtest fixtures change, so the default trace is identical and the 80-run
  ensemble is skipped under the unchanged-trace rule.

## Checks

All JVM checks use the supplied C:/ Earth and world paths in JDK_JAVA_OPTIONS.

- Initial ensemble self-tests passed, 3 tests in 21.5 seconds. They exercise
  info failures passing, M1 failed/unread months and F1 findings stopping in
  all five policies, old Markdown and JSON inputs, new summary comparisons,
  and the resumed runner retaining its baseline on exits 0 and 4.
- Final checks, current-main merge and one-in/one-out count follow below.

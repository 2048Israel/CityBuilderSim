# R2b — Buildings on history's dates

Started from main b49a6023 (0.9.111), on batch/R2b, 2026-11-15.

## Built and calls

- ★ One live knowledge predicate belongs to BuildingManager, supplied by Game: a default founding permits every template, while a 1900 founding asks Game.knows. Sector and category candidate lists apply it; getKnownTemplates supplies the city's works and berths. Raw getTemplates and lookups keep the complete catalogue for accounting, saves and research.
- ★ Each query reads the current month and local Research, so no cached list misses a date or early local discovery. A standalone catalogue stays unrestricted.
- ★ Game.knows retains its original history/discovery contract. An initial default bypass in that method made two original DiscoveryCheck assertions red; moving it to the candidate adapter mended both without changing any fixture premise. Existing island-link knowledge filters also stay as before.
- City, instant and private orders refuse unknown templates before quoting, borrowing, spending, allocating ground or drawing materials. The run preflight applies the same rule and city orders return NOT_KNOWN.
- ★ Existing catalogue dates remain the authority: reformers, crackers, hydrocrackers, alkylation and hydrotreaters are future units in 1900, while the lube and asphalt residue-separation units remain available (research-1900 §8.5); the orchestrator confirmed this reading on cbs-bus at 02:53 Halifax.
- ResearchCheck section 10 causes an otherwise paying spread-planner choice, a paying container berth, a power shortage selecting a Wind Farm, and funded direct city/private orders. It checks every dated sector candidate at its boundary, category/menu candidates, refinery packages, an early local discovery and default-founded candidates.
- The power fixture explicitly supplies an uncovered utility load after standing its houses; standing houses alone had not refreshed UtilitiesHandler's load. Its positive control chooses the Wind Farm on its date before testing the refusal in 1900. The scratch grant is the existing village fixtures' 400 million sq ft, ample for every tested order.
- One necessary RT2 overlap, reported immediately to the orchestrator: LongPlaytest.terminalFor passes getKnownTemplates instead of the raw catalogue to Ports.payingTerminal. A single reader line; no retirement or fixture-builder changes. No KD1 files changed.
- No save format change. Not a process batch.

## Verification

- Main b49a6023: batch-check --fast passed (39s compile, no firm Stale findings).
- Main's default trace completed all 2,405 months in 550s. Its exit 1 is the already recorded map movement at m2389 (DM3-R); every money fixture passed. Eight CSVs are preserved as target/checks/R2b-main-*.csv.
- Before rebuilding model changes, compiled only the final ResearchCheck fixture against main's classes. It fails 13 laws, with every fixture premise passing: target/checks/R2b-red-main.log, exit 13. The paying cracker, paying berth and actual power shortage all cause the unknown choice; funded orders actually land on main. The same fixture source is commit 7fef4be9.
- Rebuilt model: batch-check --fast passed (48s compile), zero firm Stale findings.
- Final focused run, batch-check ResearchCheck DiscoveryCheck FittingCheck: exit 0 (50s compile), three harnesses green; ResearchCheck 207 assertions, DiscoveryCheck 285, FittingCheck all passed. Generated indexes regenerated; zero firm Stale findings. Log target/checks/R2b-focused.txt.
- tools/pick.sh selects ALL (BuildingManager has 92 readers), so the full verdict will be the coordinated fresh-clone gate dry run, not a concurrent local full suite.
- Main has no known-reds row assigned to R2b. No existing harness premise, known-red registry, publisher-owned file or save slot changed.
- Final eight-file trace comparison and the retained gate directory will be recorded before readiness.

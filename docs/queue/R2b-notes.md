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
- Final model 3398da4a versus main b49a6023: all eight complete CSVs byte-identical (month, borrow, house, pop, bank, bonds, build, labour). Final run 2,405 months in 536s, exit 1 for the same recorded DM3-R map movement at m2389; all money fixtures pass. Comparison target/checks/R2b-trace-comparison.txt; run target/checks/R2b-final-trace.log and R2b-final-*.csv. No ensemble: the default trace did not move (batch.md §6).

## Gate and review

- Provisional ship file pushed at 759f74ce. Final code and fixtures 3398da4a, reported as mends to the orchestrator. REV-R2b-1 reviews b49a6023..3398da4a.
- Requested the dry-run slot at 03:16 Halifax on cbs-bus; waiting for the orchestrator go after RT2 and KD1. No local full suite or ensemble started. The kept run directory and its branch/main identities will be recorded here before readiness.

## ResearchViewCheck finding and queued R2b-V

The first normal gate is retained at C:/Users/Jerus/AppData/Local/Temp/cbs-gate-R2b-WdCE5H6G
(ship.log, checks/suite.txt and clone/target/checks/NAME.log).
It checked branch 7313196a770355d441192441f1408926bd709351 against main
b49a6023c892e61e575f39c57b4d16774cf6fdd7. ALL selected 203 harnesses;
the gate tier ran 196: 191 passed, four reds were main's recorded
MoneyDatesCheck, MapCheck, DeathRecordCheck and LongPlaytest, and one new
ResearchViewCheck assertion. Suite exit 4; launcher exit 1. The run is
complete and checksummed, not cancelled. KeptCheck ended ALL OK,
CentralBankCheck passed, no harness timed out, and only one start waited
on the commit guard. Four path observations and 14 soft timing assertions
were informational.

R2b's proper exclusion of the future Assembly Plant makes the played
1900 researchTown choose the known Fabrication Shop. Its known electric
drive gives Manufacturing a legitimate fifth equipment row: Industry 2,
Manufacturing 1, Agriculture 2 once the tractor is found. The original
sector-filter assertion correctly reads each sector and sums five, but
also hardcodes sectors.size() == 2 and Industry + Agriculture == the whole
city. researchTown's guarantees never exclude a third sector.

★ Keep the valid planner choice, every equipment row and the fixture's
existing premise. This is a latent fixture assumption, not a filtering
failure. Neither ResearchView nor ResearchViewCheck is changed. The
batch brief requires demonstrating such a red on main and queuing a
separate repair; R2b-V is that repair. Per the orchestrator's decision it
takes section 6's expected sector count from the city's own sectors and
keeps every other law, with the known Fabrication Shop causing the third
sector. Retain the played town's guarantees and the all-sector partition
law. Do not hide Manufacturing or relax the partition law.
The orchestrator accepted this attribution under the 11-04 rule at 04:12
Halifax on cbs-bus: changing the hardcoded assertion belongs to its own batch.

The demonstration uses a separately compiled git archive of whole main
b49a6023 under target/R2b-main-evidence. The probe classpath contains that
main's classes and dependency jars only, with no R2b model classes.
target/checks/R2b-view-main-control.log runs the original researchTown and
original sectionSix: the line passes, Industry 2 + Agriculture 2 of 4.
target/checks/R2b-view-main-caused.log adds one successful, legal main
order for a known Fabrication Shop, then calls the unchanged sectionSix:
the identical line fails, Industry 2 + Agriculture 2 of 5, with no other
section-6 failure. The diagnostic prints the actual partition:
Industry, Manufacturing, Agriculture; sum 5, city 5.
It also prints Game and ResearchView code-source paths proving both load
from the separately compiled main. The relevant input is:

```java
Game g = ResearchViewCheck.researchTown();
g.setCashForTest(Founding.WEALTHY_CASH * 100);
g.buildStack(g.getBuildingManager().getTemplateByName("Fabrication Shop"), 1, true);
ResearchViewCheck.sectionSix(g);
```

Main and branch source SHA-256 hashes are identical:
ResearchView 636401d70f4811f25662fed98b9519c5d732eb8d15c686d73be2c4abae68faa0;
ResearchViewCheck d78b071f5c900d2d994c1f4452df8de81dfc00c91bfff40ac2939bb4ffa7d716.
The replay source is target/R2bViewProbe.java, the main compilation log
target/checks/R2b-main-evidence-compile.log, and the original R2b path
diagnostic target/checks/R2b-view-probe.log.

Only these notes and the provisional ship file advance after the kept run.
The narrow known-red row and queued R2b-V are rejudged through the normal
gate route; tools/gate-reuse.sh explicitly binds and reuses a completed
exit-4 run when only records change.

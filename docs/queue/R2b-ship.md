<!-- gate-run: C:/Users/Jerus/AppData/Local/Temp/cbs-gate-R2b-Y8tUFPu3 -->
## changelog
### {next} — R2b, Buildings on history's dates — 2026-11-15, ready, see docs/queue/R2b-notes.md
A city founded the 1900 way offers planners, city works and berths only buildings known on history's date or discovered locally; direct city and private orders refuse unknown buildings before spending or drawing materials. The refinery's dated cracking and upgrading units wait for their discoveries. Default-founded cities keep every candidate. ResearchCheck causes the planner, works, berth and direct-order cases and holds their date boundaries.

## queued
| R2b-V | ResearchViewCheck section 6: take the sector count from the city's own sectors and keep its other laws; cause the valid third Manufacturing sector with a known Fabrication Shop, preserve researchTown guarantees and the all-sector partition law | [R2b finding](docs/queue/R2b-notes.md#researchviewcheck-finding-and-queued-r2b-v) |
| R2b-J | KnownReds: recognize harness-prefixed N FAILED of TOTAL summaries as derived counts in live and retained-run judgments; keep unknown assertions NEW and incomplete-log checks hard; its ship removes both derived ResearchViewCheck rows | [R2b process finding](docs/queue/R2b-notes.md#process-finding-and-queued-r2b-j) |

## known-reds
ResearchViewCheck | the sector filter keeps only its sector's templates | R2b-V | 2026-11-15 | Whole main b49a6023 reproduces the identical failure after a successful known Fabrication Shop order: Industry 2 + Agriculture 2 of 5, with Manufacturing 1 and a correct all-sector partition. The fixture assumes exactly two sectors without causing that premise; see R2b notes.
ResearchViewCheck | exit 1 | R2b-J | 2026-11-15 | Main classifier does not recognize the harness-prefixed summary as a derived count, leaving this exit NEW although the only primary assertion is the main-proven R2b-V fixture fault. Whole-main live-report probe reproduces; remove this row when R2b-J ships.
ResearchViewCheck | ResearchViewCheck: 1 FAILED of 217 | R2b-J | 2026-11-15 | Main KnownReds.count accepts only bare N FAILED and exit lines, so this derived summary remains NEW beside the covered R2b-V assertion. Whole-main AllChecks.report probe reproduces; remove this row when R2b-J ships.

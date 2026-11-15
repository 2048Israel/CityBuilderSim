<!-- readiness: provisional -->
<!-- gate-run: C:/Users/Jerus/AppData/Local/Temp/cbs-gate-R2b-WdCE5H6G -->
## changelog
### {next} — R2b, Buildings on history's dates — 2026-11-15, ready, see docs/queue/R2b-notes.md
A city founded the 1900 way offers planners, city works and berths only buildings known on history's date or discovered locally; direct city and private orders refuse unknown buildings before spending or drawing materials. The refinery's dated cracking and upgrading units wait for their discoveries. Default-founded cities keep every candidate. ResearchCheck causes the planner, works, berth and direct-order cases and holds their date boundaries.

## queued
| R2b-V | ResearchViewCheck section 6: take the sector count from the city's own sectors and keep its other laws; cause the valid third Manufacturing sector with a known Fabrication Shop, preserve researchTown guarantees and the all-sector partition law | [R2b finding](docs/queue/R2b-notes.md#researchviewcheck-finding-and-queued-r2b-v) |

## known-reds
ResearchViewCheck | the sector filter keeps only its sector's templates | R2b-V | 2026-11-15 | Whole main b49a6023 reproduces the identical failure after a successful known Fabrication Shop order: Industry 2 + Agriculture 2 of 5, with Manufacturing 1 and a correct all-sector partition. The fixture assumes exactly two sectors without causing that premise; see R2b notes.

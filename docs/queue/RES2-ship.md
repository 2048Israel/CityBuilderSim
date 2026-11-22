<!-- gate-run: /home/jerus/cbs/runs/cbs-gate-RES2-OIFTyuGi -->
<!-- readiness: provisional -->
## changelog
### {next} — RES2, aggregate housing inventory — 2026-11-22, ready, see docs/queue/RES2-notes.md
Separates completed residential structures from their immutable dwelling mixes,
aggregate titles, occupancy and listing subsets. The three current templates
remain compatible; the founding places are a named non-saleable endowment.
Supplies pure stock/pipeline reads, retained-occupant matching, quantity transfers,
one completion mix, saved/rebuilt cohorts and fixed asset/collateral allocation.
Preserves next-month construction work and RN2 debt allocation across reloads;
refuses genuine older saves without conversion. Caused housing/save checks pass or retain named main-only reds;
all eight default trace CSVs are observed byte-identical to current main.
## known-reds
ReloadCheck | fixture 3b, by hand: the caused point's own city exercised it | HT1-H | 2026-11-22 | Existing native five-House hand order causes no material draw; all actual continuation and RN2 comparisons pass. See RES2-notes.md.
AutoBuildCheck | fixture: ...and it placed orders in those months (0) | FS12 | 2026-11-22 | Existing replay-order premise has no debt room and places zero new orders; both twins agree. See RES2-notes.md.
LongPlaytest | industry stands farther out than the homes | CL7 | 2026-11-22 | Fresh main 01e6c2d4b and merged RES2 independently cause the same sole m2400 map-ring finding; all eight traces are byte-identical. See RES2-notes.md.

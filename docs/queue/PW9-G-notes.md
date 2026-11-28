# PW9-G — wires ready with the power: notes

**Resume here (2026-11-28):** measured on main 39fcbee71 (below); the model mend (M1, M2) being written in
`BuildAdvice.supplyDemand` (GRID) and `AutoBuilder.step`; the harness `PowerWiresCheck` next (a slice).
The measuring observer is `target/PW9-G-measure/observer.patch` (+ `PW9GMeasureCheck.java`), never committed:
`git apply` it, run `-Dpw9g.main=true ... ./batch-check.sh PW9GMeasureCheck`, then `git checkout` it away.

## What is left of the finding on 0.9.218 (main 39fcbee71, after ASH1)

Measured with PW9's observer of the real LongPlaytest player, ported (labels g0-1900, g0-today), against PW9's
final r2 (before ASH1). Both runs exit 0, save nothing.

| | default 1900, m2-440: r2 | now | modern equipment, m2-180: r2 | now |
|---|---|---|---|---|
| months below 95% current energy | 0 | 0 | 8 (min .72736, m127) | 0 (min .99683, m175) |
| dispatches wire-bound (offered > carry, short of asked) | 1 | 0 | 31 (dispatch 123-174), 12.61 GWh unserved | 1 (dispatch 175, 37.2 MW offered, 15 MW carry), 0.029 GWh |
| plants behind the founding 10 MW | 1 Diesel | none | 31 Small Steam, 19.5 MW offered | 6 units, 3.7 MW offered, to m124 |
| grid orders (pass: units) | 155 1@409, open m440 | 173 1@224, 6@343, 1@355, 1@380, 1@385; 60 MW | 155 1@119, open m175 | 173 1@90 (open m125), 1@138 (open m177), 1@175 |
| generator orders | Diesel 1@417, 2@429 | Diesel 1@224, 2@339, 1@384 | Steam 31 @101-129 | Steam 2@77, 1@84, 3@92, 1@94, 2@129, 9@135; Diesel 1@134 (open m168) |
| asked / delivered GWh | 723.75 / 687.52 | 1,316.16 / 1,250.35 | 570.75 / 530.24 | 591.78 / 562.17 |
| generator / grid spend | $10.507M / not kept | $7.783M / $11.219M | $4.213M / not kept | $5.397M / $4.067M + $40.9k land |
| population at the end | 8,245 | 14,656 | 10,471 | 10,734 |

What remains, the modern-equipment city: GRID wanted one unit and ordered none in 13 passes (pre114-116, 120-122,
126-129, 132-134), each `Cut.BUILDERS` "to preserve High school's quoted first completion"; POWER ordered a 21 MW
Diesel at pre134 and 9 Small Steam at pre135. From m168 the plants' firm supply is 36.5 MW (BuildAdvice POWER)
behind 15 MW of wires (20 MW from m177); the July peak of m175-176 (15.4 MW half-block) passed the carry before the
unit ordered at pre138 opened (m177). GRID's figure at m168 read 11.7 MW of peak and at m175 15.4 MW: December's season
(1.012) against July's (1.193), read for the present month whatever the Ahead (the inherited reader the brief names).
The default 1900 city's first Diesel and first Distribution Substation were ordered in the same pass (pre224).

Evidence: `target/PW9-G-measure/g0-{1900,today}-{months,events,passes}.csv`, `analysis.txt` (kept in this worktree).
Note: the measuring slice ran `pkill -f python` once to clear a hung heredoc of its own; it may have ended another
session's python process (reported to the orchestrator with "mends at").

## The law and the mend


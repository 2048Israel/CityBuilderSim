## changelog
### LQ1 — the luxury queue's size, a study — 2026-11-17, ready, see docs/queue/LQ1-notes.md
`docs/studies/luxury-budget.md`, its probe, two prototypes and the real figures in `docs/studies/luxury-budget/`: the
households' luxury budget on 0.9.139 against BEA, the Census, BLS and the Fed. The counter takes 13-19% of GDP (36% in
seed 0's old city) against BEA's other goods' 9.5%. Nearly all of it is the wealth term, the whole of a wealth effect
spent at one counter on a pile of 10-17 years of disposable income (the Fed's 7.9), and the shops keep 2.66 times the
landed cost against the specialty stores' 1.92. On three seeds a margin of 1.89 takes the shops' workers down 23-45%
with the spend and the pile held; a cut in the counter's share alone grows the pile. The Department Store is never
chosen, and by the Census it has no edge to give it (REV-LX1-1). Three calls (LQ1-K); LQ2 (the margin) and LQ3 (the
fortune's other spending, a study) queued. No code.
## pending
| LQ1-K | The luxury budget study's three calls, each recommended (a): ★1 the counter is BEA's other goods (14% of spending), not jewelry and watches; ★2 all of the wealth term stays at the counter until LQ3 finds a second drain, then the cut to about 0.4; ★3 the Department Store left unchosen (the Census gives it no edge, and the accounts would not change) | Jerus's calls; LQ2 and LQ3 take the recommended ways unless he says otherwise | [the study §8](docs/studies/luxury-budget.md#8-the-calls-for-jerus) |
## queued
| LQ2 | Luxury Retail's margin at the specialty stores' (LQ1 ★1): `MARGIN_CEILING` 2.5, settling at 1.89 against the Census's 1.92, its comment corrected; the Boutique's and the Department Store's coverage re-struck (7 to 13, 80 to 149) so payroll over the margin holds at BEA's share; `SectorScreen`'s ceiling printed to a decimal; `LuxuryRetailCheck` and the study's probe rerun; not beside QN1 (`BuildingManager`, `SectorScreen`) | [the study §9.1](docs/studies/luxury-budget.md#91-lq2) |
| LQ3 | The fortune's other spending, a study (after LQ2; LQ1 ★2): where a household's spending out of wealth goes besides the counter (services, the tables, travel), so the counter's share can fall to other goods' without growing the pile, and the city's consumption can move toward BEA's 68% of GDP; no model change; runs beside any | [the study §9.2](docs/studies/luxury-budget.md#92-lq3) |

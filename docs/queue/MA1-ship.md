## changelog
### MA1 — what carries the city next — 2026-11-17, ready, see docs/queue/MA1-notes.md
`docs/studies/manufacturing-margin.md`, its probe, the census and BEA extracts and the runs' tables in
`docs/studies/manufacturing-margin/`: Manufacturing's and Automotive's margins against the 2022 Economic Census and BEA's
use tables, as CU1 did for copper. On 0.9.138 the two carry 52-76% of the exports and 42-51% of GDP. Their goods and
wages are real, but a works buys nothing beyond its recipe, where a real one buys half to two thirds of its sales, so it
keeps 21-41% against the census's 13-23%; fabricated steel's world price floats 1.1-1.8 times its chain; a works costs
two to four times the census's assets a sale; the Machine Works has three times a machine shop's crew. On five
prototypes, with everything in line two cities of three take off, on the same two sectors at the census's margin, and
the third stays at 40 thousand people. Five calls (MA1-K); MA2 (the hold) and MA3 (the books) queued, MA4 pending on
MA3's ensemble. No code.
## pending
| MA1-K | The manufacturing margin study's five calls, each recommended (a): ★1 a works buys its industry's purchases less its recipe's, as consumables its planner weighs; ★2 the Machine Works' crew a machine shop's, 117 posts; ★3 the eight works' build cost at the census's assets a sale (0.29-0.41 years of sales), "the three plants" re-derived; ★4 the six manufactures held, not every made good; ★5 MA3 ships and its ensemble counts the cities that never take off | Jerus's calls; MA2 and MA3 take the recommended ways unless he says otherwise | [the study §7.6](docs/studies/manufacturing-margin.md#76-the-calls-for-jerus) |
| MA4 | Manufacturing: what starts a city on real margins (a study, after MA3). Why a city on real books stalls at 40 thousand people (the prototype's seed 1), and what a real town that size sells, 1900-2100. No model change | Waits on MA3's ensemble: queued if it has more cities under 60,000 people at m2400 than main's, dropped if not | [the study §7.5](docs/studies/manufacturing-margin.md#75-ma4) |
## queued
| MA2 | Manufacturing: a manufactured good's world band held to its chain (MA1 ★4). In `WorldMarket.solveYear`'s made branch, fabricated steel, machinery and the four vehicles moved by `Base.anchor` until their band at the city's site is their inputs' by their shares plus their own part on the level; first why the prototype lets fabricated steel go once the world tips. A `WorldMarketCheck` section. An ensemble | [the study §7.3](docs/studies/manufacturing-margin.md#73-ma2) |
| MA3 | Manufacturing and Automotive: the books in line (after MA2; MA1 ★1-★3). The eight works' consumables at their industry's purchases, weighed by both sectors' planners; their build cost at the census's assets a sale; the Machine Works' crew at 117 posts; `ManufacturingCheck`'s three plants re-derived. An ensemble, counting the cities that never take off (★5) | [the study §7.4](docs/studies/manufacturing-margin.md#74-ma3) |

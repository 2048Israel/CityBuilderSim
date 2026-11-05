## changelog
### {next} — R1, equipment as data and on each building — 2026-11-05, ready, see docs/queue/R1-notes.md
The first part of research's recipes and equipment, built off. `buildings.json` gives a building the equipment it can be fitted with:
the electric motor drive on the 18 factories (it ends R0c's steam), the motor truck on the Truck Depot (the carts' horses) and the
tractor on three farms (the farms' horses; its date is the research catalogue's, so it is not known yet), each with what fitting it
buys, its work and its life. Each stack of buildings counts the ones still without each piece, and the count is saved; R0c's shares
are set from the counts each month, weighted by the buildings' power, land or truck room. The payback rule (a piece fitted only when
what it saves over its life repays its cost and clears the interest hurdle, as a new building's) gives each piece a verdict at the
city's prices, read on demand. Every building in a played city is fitted, so the default city's trace is unchanged. EquipmentCheck
holds it. Next: the fit bought and ranked with new buildings (R1b), recipes (R1c), and the 1900 city switched on with R0d's wages (R1d).
## queued
| R1b | Research: the fit acted on - the fitting's goods bought at the city's market and its work, one order at a time, ranked with the sector's new buildings in `SpreadPlanner.best()` (research-design §2.2 step 5); new buildings with every piece that pays at the order (step 6); the city's buildings' Fit action; a city founded the 1900 way (the known pieces unfitted), off by default | [R1 notes §1](docs/queue/R1-notes.md) |
| R1c | Research: recipes as data - `buildings.json`'s `recipes`, the best known recipe each month at the city's prices (`SpreadPlanner.cityValue()`), the tractor's `PETROL` in today's farm (R0c ★5), the 1900 recipes of research-1900 §3's rows with no form yet (open hearth, by-product coke, coal cutter, steam shovel, rock drill, rotary kiln, pulp, binder, Hulett unloader, X-ray, antitoxin) | [R1 notes §1](docs/queue/R1-notes.md), [research-design §2.1](docs/studies/research-design.md#21-recipes-and-the-shape-the-materials-batches-should-leave-room-for) |
| R1d | Research: the 1900 city switched on, with R0d's 1901 wages - founded with the known equipment unfitted, judged on a 1900 city: the carters' sizing and the team's price (R0c ★6-7), steam in the brownout ratio (★8), the electric drive's fitting work (R1 notes, found), the ensemble | [R1 notes §1](docs/queue/R1-notes.md), [R0 notes call 2](docs/queue/R0-notes.md) |

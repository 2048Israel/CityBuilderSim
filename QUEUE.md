# The queue

The batches waiting to be done, in order, and nothing else: one line each and
a link to its brief. Jerus, 2026-10-11: "a proper md file with just the
batches queued, just indexed and as soon as a batch is done its gone from
that md fully ... just minimal info, just with links or something, not actual
stuff".

- **The top row is next.** A new batch goes on the end unless Jerus places it.
- **Done: delete the row**, in the merge that ships it. What shipped is the
  changelog's to say; this file keeps no history.
- **Not done, or done in part: delete the row and put what was NOT done under
  Pending**, with why - it failed, it waits on Jerus's decision, a caveat, a
  bug.
- **A Pending row leaves when Jerus reviews it**: back into the queue, or
  dropped. Nobody else moves one.
- **No detail here.** The brief holds it - a study in `docs/studies/`, a note
  in `docs/queue/`, or a file in the project, named.

## Queued

| batch | what | brief |
|---|---|---|
| FX1 | Three small faults copper found: stock written off in food, materials and luxuries; shares raised before the credit test; the playtest's bank trace on short keys (running) | [FX1](docs/queue/FX1.md) |
| I9 | Forestry, with the 100-ha woodlot (running) | [spec-materials §5](docs/studies/spec-materials.md#5-build-order) |
| I10 | Wood products | [spec-materials §5](docs/studies/spec-materials.md#5-build-order) |
| I11 | The materials pictograms' model | [spec-materials §5](docs/studies/spec-materials.md#5-build-order) |
| I12 | The materials pictograms drawn | [spec-materials §5](docs/studies/spec-materials.md#5-build-order) |
| I13 | Cards and pages for the materials buildings | [spec-materials §5](docs/studies/spec-materials.md#5-build-order) |
| I14 | The materials on the map (0.8.0) | [spec-materials §5](docs/studies/spec-materials.md#5-build-order) |
| CC1 | Childcare at real demand | project: `claude/decisions-2026-10-10.md` |
| CD1 | The city's debt: the curve from the dial the market expects, every payment priced on its month | [spec-city-debt §8](docs/studies/spec-city-debt.md#8-batches) |
| CD2 | The city's debt: bills, bonds in reopened lines, the amortizing bond, sold at auction | [spec-city-debt §8](docs/studies/spec-city-debt.md#8-batches) |
| CD3 | The city's debt: effective interest, carrying value, gain or loss on retirement | [spec-city-debt §8](docs/studies/spec-city-debt.md#8-batches) |
| CD4 | The city's debt: the buyback as a reverse auction, callable lines and the call | [spec-city-debt §8](docs/studies/spec-city-debt.md#8-batches) |
| CD5 | The city's debt: floating-rate notes and inflation-linked bonds | [spec-city-debt §8](docs/studies/spec-city-debt.md#8-batches) |
| CD6 | The city's debt: the screens - the book as a desk, a line's page, Close it now, Borrow as an auction, Your rate | [spec-city-debt §8](docs/studies/spec-city-debt.md#8-batches) |
| R0 | Research: the 1900 economy and the calendar from 1900 | [research-1900](docs/studies/research-1900.md), [research-design §9](docs/studies/research-design.md#9-for-the-implementer) |
| R0b | Research: causes of death in Health - typhoid, summer diarrhoea, outbreaks, TB, home births, pneumonia, the 1918 influenza | [research-1900 §2.7](docs/studies/research-1900.md#27-health) |
| R0c | Research: the 1900 mechanics - horses on farms and road freight, lamp oil and heat as goods, steam recipes | [research-1900 §4](docs/studies/research-1900.md#4-new-mechanics-a-1900-city-needs) |
| HY1 | Hydro Station, a template that needs a river and a drop on the Earth's map; not research, it goes with the electricity run | [research-1900 §4](docs/studies/research-1900.md#4-new-mechanics-a-1900-city-needs) |
| R1 | Research: recipes and equipment as data | [research-design §9](docs/studies/research-design.md#9-for-the-implementer) |
| R2 | Research: the catalogue and the world | [research-design §9](docs/studies/research-design.md#9-for-the-implementer) |
| R3 | Research: posts and discoveries | [research-design §9](docs/studies/research-design.md#9-for-the-implementer) |
| R4 | Research: prizes and the Research Lab | [research-design §9](docs/studies/research-design.md#9-for-the-implementer) |
| R5 | Research: the screens | [research-design §9](docs/studies/research-design.md#9-for-the-implementer) |
| PT1 | Eleven small fixes to screens, words and the log, from a played city | [PT1](docs/queue/PT1.md#pt1) |
| FR0 | The map's network: gateways, the main line and yards, rail drawn as rail, access roads, junctions by rule | [spec-freight §7](docs/studies/spec-freight-and-infrastructure.md#7-batches) |
| FR1 | Trains on the map, as the ships are | [spec-freight §7](docs/studies/spec-freight-and-infrastructure.md#7-batches) |
| FR2 | The Freight sector: Haulage's trucks carry the city leg, vans for goods only | [spec-freight §7](docs/studies/spec-freight-and-infrastructure.md#7-batches) |
| FR3 | Ports as a landlord port, and the Bulk Berth | [spec-freight §7](docs/studies/spec-freight-and-infrastructure.md#7-batches) |
| FR4 | The freight screens, and Build's Infrastructure and Freight tabs | [spec-freight §7](docs/studies/spec-freight-and-infrastructure.md#7-batches) |

## Pending

Not done, and waiting on Jerus.

| item | what is left | why it waits | brief |
|---|---|---|---|
| PT1-M1 | One Boutique opening moved the policy rate to 23% | his decision on the model; a harness first | [M1](docs/queue/PT1.md#m1) |
| PT1-M2 | The ground runs out and the city stops, quietly | his choice of three | [M2](docs/queue/PT1.md#m2) |
| PT1-M3 | The advice and automatic building disagree on the same page | his word on the wording | [M3](docs/queue/PT1.md#m3) |
| PT1-M4 | Construction earns with nobody working | is it meant? | [M4](docs/queue/PT1.md#m4) |
| FR-D | The freight study's 22 decisions | his word, each or "all rec"; FR0 to FR4 follow the recommendations until then | [spec-freight §8](docs/studies/spec-freight-and-infrastructure.md#8--decisions) |
| FR5 | Trucks drawn on the map | his call (D21), optional | [spec-freight §7](docs/studies/spec-freight-and-infrastructure.md#7-batches) |
| FR6 | The railway carries the city's own bulk | his call (D4), after FR2 is measured | [spec-freight §7](docs/studies/spec-freight-and-infrastructure.md#7-batches) |
| I7-K | A works on imported feed is ordered, loses money and is sold back: a Lime Kiln seven times in the playtest (stone, 0.7.131), a Scrap Copper Refinery six times (copper, 0.7.133) | his call on I4's ★2 (weigh one more works at the prices after its own supply), and the shed rule's wait (IDLE_MONTHS 6 against FLEET_DELIVERY_MONTHS 8) | `runs/fixI7-notes.md` ★1, in the project's `claude/todo.md` |

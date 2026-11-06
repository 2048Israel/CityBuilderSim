## changelog
### {next} — R2, Research: the catalogue and the world's dates — 2026-11-05, ready, see docs/queue/R2-notes.md
Research has its catalogue: `research.json`'s 89 discoveries (the first study's 40, the 1900 study's 48 and L5, the
Canada Pension Plan), each with its field, kind, history's month, what it needs first, whether it carries a licence fee
and the work it takes by the study's rule; the law rows carry the Policy levers' own dates (`ResearchCatalog`,
`ResearchCheck`). Every catalogue code in `buildings.json` now resolves to history's month (`knownAt()`): in a city
founded the 1900 way the tractor is known from June 1917 and the oxygen converter's recipe from November 1952, where a
code was never known, and the 29 buildings that did not exist in 1900 are dated by the discovery that opens each (the
Container Terminal by E5, the cracking units by PE11); the Oxygen Steel Shop stands from 1900. A city founded the
default way reads none of it: the playtest's trace is unchanged. The rest of the brief is queued: buildings ordered only
on their dates (R2b), licence fees (R2c), each entry's old way (R2d, R2e), medicine (R2f), the near-future list (R6).
## pending
| R2-R | For R1d ({next}): the tractor, known from June 1917 in a 1900 city, never pays - `EquipmentFit.gainOf()` has no gain for a piece that ends FARM_HORSES, and the recipe's difference alone is the fuel, a cost; its gain is the form's feed and hands less the fuel | Out of R2's brief: R1d plays a 1900 city | [R2-notes §3](docs/queue/R2-notes.md) |
## queued
| R2b | Research: buildings on history's dates - in a city founded the 1900 way a template not yet known (`BuildingsTemplate.knownAt()`) is no candidate for any planner (the spread planner, extraction, retail, `BusinessInvestment`, the city's own works, the berths: about 40 readers of `getTemplatesBySector()`) and its order is refused; a 1900 refinery without its conversion units | [R2-notes §1](docs/queue/R2-notes.md) |
| R2c | Research: licence fees on the current account - 25% of what a patented entry got from the world adds, for 20 years from history's date (research-design §2.4, ★E2), paid by the firm that runs it as IP charges; the planners' and the fitting's gain less the fee; in a 1900 city | [R2-notes §1](docs/queue/R2-notes.md), [research-design §2.4](docs/studies/research-design.md#24-licences) |
| R2d | Research: the 1900-1950 entries undone - each P row's 1900 recipe or piece (research-1900 §8.5) on its templates, so a city with every entry runs today's figures (★E3), with recipes written as changes so two pieces compose on one template (R1c ★4, ★8: the binder beside the tractor) | [R2-notes §1](docs/queue/R2-notes.md), [research-1900 §8.5](docs/studies/research-1900.md#85-each-rows-1900-recipe-what-undoing-it-leaves) |
| R2e | Research: the 1951-2000 entries undone - the engineering and business rows' 1950 forms (research-design §3.1-3.2), and the Panel Mill and Wood Pellet Mill's entries with sourced dates (research-1900 Q13) | [R2-notes §1](docs/queue/R2-notes.md), [research-design §3](docs/studies/research-design.md#3-the-1950-starting-set) |
| R2f | Research: the medical rows - each M and PM entry mapped to the constants it moves (research-design Q17), with R1c-R's X-ray and antitoxin | [R2-notes §1](docs/queue/R2-notes.md), [research-design §4](docs/studies/research-design.md#4-the-catalogue-40) |
| R6 | Research: the near-future catalogue, 2025-2060 - a study first (today's pilots and roadmaps with projected dates, marked as projections), then its rows in `research.json` (Q2, decided 2026-10-18: the catalogue ends with them) | [research-design §7](docs/studies/research-design.md#7-the-world), [R2-notes §1](docs/queue/R2-notes.md) |

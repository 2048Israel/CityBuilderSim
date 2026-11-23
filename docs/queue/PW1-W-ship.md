<!-- gate-run: /home/jerus/cbs/runs/cbs-gate-PW1-W-3D5i6fZ2 -->
<!-- readiness: provisional -->
## changelog
### {next} — PW1-W, water priced from its cost — 2026-11-23, ready, see docs/queue/PW1-W-notes.md
The sea's water is priced as PW1 prices power, at its margin: while the city needs it, a unit costs its tariff and the
desalination works' power over the water the sea delivers, at the struck commerce price with PW5's wires. A plant under half
used spreads its power over the water served instead, recovered once and over no more than half its output, so a sliver of
the sea has a bounded price. While the land's water (the wells and the fresh works) serves, the price is the tariff, which
pays the fresh works' power. The water's books buy all the works' power of the power's at the top of the next month, inside
the treasury, so the utilities' net is unmoved; the Services screen shows it on both books. A Desalination Plant's water
covers its power again and a patented one earns its licence: PublicLicenceCheck's four desalination reds pass. A licence's
power is now the dispatch's energy served with the plant's row moved, not the flat rule (PW1-L), and a recipe's surplus power
is weighed at the works price the strike pays it, not its class's (PW1-G). WaterPowerCheck holds the price, the bills and the
save on both sides of each edge. No format bump.
## queued
| PW1-S | SpreadPlanner.ownCosts() credits a works' own generation at the class tariff its callers pass: at the works price, as PW1-G weighs a recipe's (Recipes.powerCost()) | [PW1-W notes §2](docs/queue/PW1-W-notes.md) |

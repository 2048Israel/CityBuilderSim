## changelog
### {next} — R0c, the 1900 mechanics as forms — 2026-11-05, ready, see docs/queue/R0c-notes.md
The 1900 mechanics of research-1900 §4 are built as forms, each a share of a sector or of the homes that runs the 1900 way, and
all are off: 0 in every played city, so the default city's trace is unchanged, until R1 sets them from each building's recipe
(and R0e the homes'). A farm on horses feeds them 22% of its own crop and needs 1/(1-0.273) of the hands; Freight's Haulage on
horse and wagon carries its city leg by hired teams (10 t a team-month, two horses eating 0.29 t of crops each, one carter),
with no diesel and no trucks; a factory on steam draws nothing from the grid and burns 1.8 kg of coal a kWh instead; a lamp-lit
home buys 25 L of lamp oil a month and a stove-heated one 0.7 t of coal, from the grocers, a bill of its own. Forms1900Check
holds each form, with the money's audit closed every month. Found on the way: the drivers' petrol may be counted twice in the
households' spending (PD1, queued).
## pending
| R0c-O | The Bus Network's horse omnibus, its 1900 form (research-1900 §4) ({next}) | the bus is a city building that buys nothing, and the city has no path to buy its horses' feed | [R0c notes](docs/queue/R0c-notes.md) ★9 |
| R0c-R | R0c's forms, for R1 to switch on ({next}): Agriculture's, Haulage's and each factory's steam share set from the buildings' recipes; the tractor's PETROL in today's farm recipe; a team and wagon's price; the carters' sizing (on nameplate, as the trucks') judged on a 1900 city; the steam share in the brownout ratio | R0c built the forms off (★1) and left these to the recipes | [R0c notes](docs/queue/R0c-notes.md) ★1-8 |
## queued
| PD1 | The drivers' petrol counted twice in the households' spending: `Retail.sellFuel()` books each litre as a sale to the households, so it is in Retail's `salesToHouseholds`, which `Game.syncHouseholdAccounts()` hands them as `shopping`, and it is again their `fuel` line (`HouseholdAccounts.getSpending()`, the rows' fees); settle whether the cells' cash pays it twice, and take it out of the shopping as R0c's slice 3 does the homes' fuel | [R0c notes §3](docs/queue/R0c-notes.md) |

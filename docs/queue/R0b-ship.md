## changelog
### {next} — R0b, causes of death in Health — 2026-11-05, ready, see docs/queue/R0b-notes.md
A city's dead now have causes (`CausesOfDeath`): typhoid, babies' summer diarrhoea, the childhood outbreaks, TB, mothers at
home births, pneumonia and the 1918 influenza, each at its 1900 rate and falling on Canada's own dates - typhoid with
chlorination by 1915, TB to 1948 and with streptomycin, mothers' deaths with sulfa and penicillin - to nothing by 1980; a city
short of water, food or general care dies of them sooner than history did. For teens, adults and the old they add to today's
rates; for babies and children, whose unserved rate childcare's swing already holds at 1900's, they name their share of that
excess and history takes it away (an unserved baby dies at 21 times today's rate by 1980, where it died at 40). A 1900 city
loses about 17 per 1,000 a year, as Canada did. The influenza kills 0.6% of the people from October 1918 and puts a quarter
off work in its waves, with an inbox notice. Services, Health lists what people die of beside 1900's rates; People's dead gain
the part. Twelve fixtures sized on today's deaths hold the causes off; the ensemble scores 24/26, the same two as 0.9.12's,
both nearer. MapCheck 2h (DM2's) and ShortSellingCheck 7 (main's orphan-share fault, OH1 queued) pass by name at the gate.
## pending
| R0b-M | MapCheck 2h on R0b's city ({next}): its played city's interchange reserve is never laid (month -1, 0 plots), so every line after the fixture's fails - DM2's class, which DM2's row names ("found by DM1: MapCheck 2h, 2d"); R0b's diff touches no map code; passed by name at R0b's gate | DM2 mends it | [R0b notes](docs/queue/R0b-notes.md) ★9 |
| R0b-S | ShortSellingCheck 7 on R0b's city ({next}): Real Estate's register 0.004616 shares short at m83 (1.15e-6 of its issue, against 1e-6) - the orphans' shares `HouseholdBalance.moveStock()` hands to no one, which R0b's city reaches first (more adults die, more orphans hold shares); passed by name at R0b's gate | OH1 mends it | [R0b notes](docs/queue/R0b-notes.md) ★10 |
## queued
| OH1 | The orphans' shares in a move: `HouseholdBalance.moveStock()` (lines 1535-1544) puts a shrinking cell's stock in its row's pool but weighs its loss by `abs(delta) x grownUps()`, 0 for a cell of orphans, so their shares go to no cell and are not counted as leaving - the register short (ShortSellingCheck 7 on R0b's city: Real Estate 0.004616 shares at m83); weigh an orphan cell by its people, or hand its stock on as the cell that holds them, mending R0b-S | [R0b notes §6](docs/queue/R0b-notes.md) |

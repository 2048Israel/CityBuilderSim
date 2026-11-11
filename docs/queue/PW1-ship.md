## changelog
### {next} — PW1, power by the merit order at the market's price — 2026-11-11, ready, see docs/queue/PW1-notes.md
Power is dispatched by the merit order and billed at the market's price (Jerus, 2026-11-02: "Market price"). The month
is three load blocks, each a windy and a calm half (`PowerDispatch`): the works' surplus first, wind (its excess turned
away), hydro levelling the load, then the fuelled plants by marginal cost; a half-block clears at its last plant's
marginal, or at the cap when short. The Coal Power Plant burns thermal coal bought from the market. Commerce pays the
energy and both wires, Heavy Industry and Mining the energy and transmission, the works are paid each half-block's
price; one struck book a month, saved, holds the ratio, the bills and the fuel. The advice plans power for the calm
peak (a Wind Farm counts a third) and the playtest's player buys it as the advice says; a founded city counts its free
grid from the first month, so it orders no plant before it draws. The probe city was short at every turn from month 600
on main, at 31 turns and none after month 607 on PW1. The Services screen shows the prices, the coal, the wind turned
away and the load not served; the Trade tab the power plants' coal and upkeep. The ensemble scores 24 of 26
(`docs/ensemble/PW1.md`; 0.9.93 23), R2 newly red (PW1-E). New: PowerDispatchCheck, PowerPriceCheck, BuildAdviceCheck
14-15; CallCheck 4's sale on a bank that stands. PW5 and PW8 pending.
## pending
| PW5 | Substations: one per 100 MW of peak, built by the auto-builder, their cost the wires' part of the bills ({next}) | Left out of PW1 (its notes §0): a new building and an AutoBuilder rule that change every city's builds, the cost to be priced from the bills PW1 now strikes. Recommendation: its own batch, next in the power lane | [PW1 notes §0](docs/queue/PW1-notes.md) |
| PW8 | Homes pay for their power: a household power bill at the market's price ({next}) | Left out of PW1 (its notes §0): it changes `HouseholdBalance`, which HL1 (the households' books closed) was changing beside it. Recommendation: its own batch once HL1 is on main | [PW1 notes §0](docs/queue/PW1-notes.md) |
| PW1-P | The power planner's gaps, measured on PW1 ({next}): the works' surplus is not counted as supply, so a town builds wind for load its works cover (95% of the wind turned away at month 244); a plant under construction counts as on site, so nothing bridges its build (the ratio 0.857 while the first coal plant built); the advice ranks plants by their price a firm kW, not their fuel; the cities south of the equator read the northern season | Recommendation: fold into PW3 (the power planner, the reserve test, the season by latitude), with PW2's small plants as the bridge | [PW1 notes §0, §2](docs/queue/PW1-notes.md) |
| PW1-E | The ensemble's R2 (HIGH's unemployment over its twin) newly fails on PW1: 0.32 points against at least 0.50 (0.83 on 0.9.93), the seeds -8.81 to +13.04, while A3 and R3 newly pass ({next}) | Recommendation: keep PW1 as it is; the next model batch's ensemble says whether it holds, and a study of HIGH's window only if it does | [PW1 notes §2, §4](docs/queue/PW1-notes.md) |
## queued
| MR1 | The map lays rail after a load where the live map leaves the plots bare: ReloadCheck's `.mapStamp` red on PW1's earlier commits (district 3, 6 plots, month 1,205; green on its final tree, whose path misses it) and on clean main once the probe follows past ReloadCheck's last checkpoint (0a5b74ef: district 11, 2 plots, month 2,105), with the two maps byte-equal at the load, the same builds, no reserves and no mined sites. Main's fault, not PW1's; the probe is in the orchestrator's notes folder, `PW1-map-rail-after-load/`. Done means the reloaded map lays what the live one lays, and the probe clean on main to month 2,400 | [PW1 notes §3](docs/queue/PW1-notes.md) |
| AU1 | AuctionCheck section 10's flat-price premise: the fixture holds the builders' order multiplier at 1 but not their wages (1.8% a month), so each ask's cost drifts 0.4-0.6% a month and "a granule's top-up" passes on the rounding remainder each ask holds (main two top-ups against a bound of three, PW1 three, exactly the bound). Done means the fixture holds the wages too (or derives the bound from the drift) and section 10 causes the top-up it counts | [PW1 notes §3](docs/queue/PW1-notes.md) |

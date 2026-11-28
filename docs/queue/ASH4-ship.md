<!-- readiness: provisional -->
## changelog
### {next} — ASH4, four checks from Ashbourne — 2026-11-28, ready, see docs/queue/ASH4-notes.md
From Jerus's Ashbourne findings 5, 12, 11 and 16, each caused on a fixture in AshbourneCheck. Finding 5 was a bug: the
households keep three months in the bank before they buy shares or paper, and the owner families, who take home nothing
but dividends, kept none, so a 30-year city bond took 91% of Ashbourne's deposits in three months. The cushion is now three
months of take-home or of the month's outgoings, the greater (HouseholdBalance.cushion), wherever savings are held:
shares, the city's paper, bonds and money abroad. The outgoings are the rent and the other bills first, then everyday
spending without what a fortune asks for, so a bond never takes next month's rent and a cushion grows with spending, never
with wealth; they are saved with each cell. What a household spends, on a car, a meal or a luxury, still leaves three
months of take-home in the bank (HouseholdBalance.spendingCushion), as before.
Finding 12 is real in the model: the yen piece is priced on the yen curve, unchanged by the D$, and 1917's yen rate is the
stand-in century's 13% where the Bank of Japan's was 5.11%, which EP1b's real rates mend. Finding 11 is real: two Lime Kilns
closed, and with one block of the month fewer over the 10 MW wires the scarcity price fell 78.5% for 12.9% less load.
Finding 16 is a defect of design: a lost job splits a family at once and the families re-form at 1% a month; its mend is
queued as HF1, which keeps the out-of-work adults in their households.
## pending
| ASH4-A | One large city issue still takes about 90% of a rentier family's balance past its cushion ({next}) | D32's appetite has no limit on a household's share of its wealth in one issue; a limit would change his D32 (2026-10-20) | [ASH4 notes](docs/queue/ASH4-notes.md) |
## queued
| HF1 | The families keep their out-of-work adults: a lost job leaves the household standing, and HouseholdMemoryCheck's re-forming pace holds only for real formation | [HF1](docs/queue/HF1.md) |
| PL1 | A building's power draw follows its output or staffing: an idle works draws its standing share, the orchestrator's call on Ashbourne's finding 11 (real load follows output) | [PL1](docs/queue/PL1.md) |
| PH1 | A rounding residue in Unemployment.reconcile becomes a cell of 2^-43 households that survives and hands its hunger to the next month's arrivals: DenominationCheck's twins part within a year on ASH4's path | [PH1](docs/queue/PH1.md) |
| FC3 | The foreign crews take their share of a contract, not the whole of it: FC2b's handAbroad moves a payer's whole home contract, so the home crews draw material with nothing to earn and the month's GDP can read negative (ASH4c's ZERO_HOT seed 3, m275, -85.08); R1g's earnAhead drains a second site the same way | [FC3](docs/queue/FC3.md) |

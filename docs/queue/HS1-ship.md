<!-- readiness: provisional -->
## changelog
### {next} — HS1, money follows people — 2026-11-19, ready, see docs/queue/HS1-notes.md
The month's flows now carry their money. A household that retires, ages on, loses a partner, dies
alone or leaves takes its cell's average of every stock with it before the census's pool nets the
rest. A survivor keeps everything. An estate repays the bank from its savings and passes the rest
to the working households of the dead's former tier. Leavers take theirs out through the balance of
payments, so HL1's identity closes as before. The retired keep their former tier: 24 cells, 99 in
all, shown on the People screen as "Senior couple, formerly Skilled". An orphan cell weighs by its
people for what a child can hold, never a debt, a loan or a car (OH1: the register stays whole). HouseholdFlowCheck causes each flow. Save format 57,
floor 57: an older save's retired money would load into no cell. Ensemble: M1 passes. F1's one
finding (ZERO_HOT seed 0, 1.6 households with no home in months 205-207) is FB11's held land,
passed by name. The towns take off at main's pace (T1 by m786, against m788). Unemployment runs
1.6-6.6 points higher: the retired now pay for their care, so the senior care pull draws more
people to the homes. AUTO2's final population (T2, information) is 65% of main's, two long stalls
in sixteen against one.
## pending
| HS1-A | The senior care pull sits at its cap since the retired pay for their care: `Migration.seniorCarePull` reads the senior coverage times `Game.careAffordability` (the share of seniors whose cell paid), which main's pool held at 1.06-1.30 and HS1's retired, keeping their money, hold at 1.30 (`SENIOR_CARE_PULL` .30). It lifts the homes' draw for every arrival (`Migration.homesPull`), so towns draw more people than their jobs hold: unemployment m720-1800 runs 1.6-6.6 points higher (AUTO2 6.9% against 5.3%, SHOCK 7.4% against 5.4%, HIGH 14.2% against 7.6%), and AUTO2's final median is 65% of main's (2.01M against 3.10M; T2, information), carried by two long stalls of sixteen against one (0.9.158 + HS1) | Call for Jerus. Recommended: bound the pull to what it is for, letting full senior care lift only the seniors' share of the homes' draw, so a well-cared town draws retirees rather than working families beyond its jobs; HS2's stages do not touch migration. The other way: leave it, a town with care is more attractive and pays for it in unemployment. | [notes](docs/queue/HS1-notes.md#the-ensemble-2026-11-19-on-ed9a1ad35), [ensemble](docs/ensemble/HS1.md) |
## queued
| SB1 | `BuildingManager.retire()` scraps from every stack of the template, not only the first: it returns after the first match, even an empty stack left in place, so a works or a plant shed twice keeps its other stacks (Game's shedding and closing paths; found by HS1's OutsideCheck fixture) (after SZ1) | [HS1 notes](docs/queue/HS1-notes.md#the-premises-restated-the-studys-5) |
## known-reds
SaveFileCheck | fixture: over the default it owes what it sells for, and paid some late | FS17 | 2026-11-19 | HS1's moved college town: its fund holds 44,441 Retail shares, no bid stands and it paid nothing late in 12 months, so the fixture's condition never arises (HS1-notes.md, The checks)

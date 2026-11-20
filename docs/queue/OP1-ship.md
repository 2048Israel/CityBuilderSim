<!-- readiness: provisional -->
## changelog
### {next} — OP1, every policy open from the first month — 2026-11-19, ready, see docs/queue/OP1-notes.md
Every Policy lever is open from a city's founding, in every city. The profit, wage and sales taxes
open at 15%; the pension, its contribution, EI, student aid and the health premium open at zero. The
minimum wage binds and zoning is free. Money's dial, target, holdings and regime are the player's
(or the rule's) from January 1900. The Policy, Zoning and Trade screens show no dates or shut cards,
and the inbox's "a lever opens" notices are gone. A law find opens nothing now; its text says so.
History's regimes stay history's: a 1900 city founds on gold at par, tied to the US rate; 1914 and
1931 float a dollar still on it, 1926 puts a floating one back, and a player may leave it any month.
PolicyOpenCheck replaces PolicyDatesCheck. No format bump: an old save's openings are ignored.
## queued
| OP2 | `Household.studentLoansOpen` and its javadoc ("opens 1939") retire: nothing sets it false since OP1 (HouseholdBalance's setter went), so `StudentHousehold.fundShortfall()` and `planningRoom()` read a constant; Household is HS2's and BO1-S's file, so after them (OP1-notes.md, Left for Jerus) | docs/queue/OP1-notes.md |
## known-reds
KeptCheck | the fund holds something to draw | FS5 | 2026-11-19 | OP1's moved played city (month 603): KP5's fixture pays half the cash into the fund and orders shares and bonds, and three months on the fund holds 0 lots (OP1-notes.md, The checks)
KeptCheck | fixture: the fund holds a bond, null, that runs 0 months | FS5 | 2026-11-19 | the fund holds no lot on OP1's moved played city, so the Portfolio has no row that folds (OP1-notes.md, The checks); its bond watch has no bond
KeptCheck | every row that folds opened on Portfolio: the redraw and the next equal a page built new | FS5 | 2026-11-19 | the fund holds no lot on OP1's moved played city, so the Portfolio has no row that folds (OP1-notes.md, The checks)
KeptCheck | every row that folds opened on Portfolio adds to the page | FS5 | 2026-11-19 | the fund holds no lot on OP1's moved played city, so the Portfolio has no row that folds (OP1-notes.md, The checks)
KeptCheck | no month makes more than a tenth of the nodes the first draw made (the most made in one: 39 of 373 | FS5 | 2026-11-19 | Section 69's Government Overview, 39 of 373 nodes in a month: OP1's moved played city (month 603; the taxes at 15% from 1900): KFP1's class on KeptCheck's path, as 68's Government Overview was (OP1-notes.md, The checks)
KeptCheck | most months make no node at all (11 of 25) | FS5 | 2026-11-19 | Section 72's Trade reserves page, 11 of 25 quiet months: OP1's moved played city (month 603; the taxes at 15% from 1900): KFP1's class on KeptCheck's path, as 68's Government Overview was (OP1-notes.md, The checks)
PriceLevelCheck | month 79: | FS2 | 2026-11-20 | Section 9's twin on ScaleCheck's town on OP1's moved path (the taxes at 15% from the founding): the same-level 1e-12 nudge parts at month 80 at both levels, so the horizon is 79, and the factor twin parts at 79 itself, a landlords' order taken at one level only (homes 179 against 150); every series equal through 78 (OP1-notes.md, The dry run)
PriceLevelCheck | ...the homes is the same to the person in every month | FS2 | 2026-11-20 | The same month-79 parting of section 9's twin, homes 179 against 150 (OP1-notes.md, The dry run)
MapCheck | ...its streets one network: every other piece one the city's own ground does not join to it | FS6 | 2026-11-20 | Section 8's copy a mine is born on, MapCheck 1's played city on OP1's moved path: 5,981 houses on its ground, 1,272 road plots in 2 pieces, 1 of them joined by the city's own ground; no map, plan or land file is OP1's (OP1-notes.md, The dry run)
MapCheck | ...and every building drawn, one for one | FS6 | 2026-11-20 | The same copy: 2,631 of 5,867 buildings drawn, 115 with no ground and 1,181 past the ground the city owns (OP1-notes.md, The dry run)
PlanCheck | every district as the map draws it keeps every check: one network or pieces the ground parts | FS6 | 2026-11-20 | Section 17 on MapCheck 1's played city at month 121, OP1's moved path: 803 of 1,418 buildings placed, and 1 of its 2 districts keeps every check, the other failing only T junctions only in an estate cell (OP1-notes.md, The dry run)
IslandLinkCheck | ...the island is linked by the end (the terminals stand on both sides), and nothing is unlinked | FS7 | 2026-11-20 | Section 9's played Earth run, 240 months with the auto-builder on, on OP1's moved path: the port link is ordered (2 terminal orders) and the island not linked by month 240 (OP1-notes.md, The dry run)

# FB1 - three fixtures that read the city's path (2026-11-16)

Harnesses only: no model change, no save field, the playtest does not move. Rows MQ1, HX1, FPK1 and RN1-F
(brief: [FB1](FB1.md)). Built by a Claude Sonnet session (Codex out of plan until 2026-11-17 19:35); the two
fixtures that did not depend on each other (FPK1, MQ1) were first written by `slice` subagents in scratch worktrees
and reviewed here as a stranger's diff.

## What the three harnesses showed, before and after

The three trees are main (a9f48daf, 0.9.129), CU2 (origin/batch/CU2 a395e69a, main already merged in) and TQ1
(origin/batch/TQ1 with main merged, scratch d8b33127). The "after" runs are a scratch merge of batch/FB1 into each of
the three (throwaway local branches, never pushed). All three harnesses were green before and are green after, on all
three trees; what changed is what they read.

| harness | main before | CU2 before | TQ1 before |
|---|---|---|---|
| FoodProcessingCheck 5 | $-243.58 against $-595.02, green by a sign (-243 > 2 x -595), printed `Infinityx` | $260.78 against $-156.27, `Infinityx` | $-244.08 against $-595.55 |
| HealthCheck, poor city | x1 and x15 serve 83.1% alike, coverage 86.35% alike; 1 OBSERVED: the beds line (99.60% against 99.66% of the beds, the fee's gap 0) | 82.3% alike, coverage 71.23% / 70.18%; 1 OBSERVED: 30,000 places against peaks of 34,610 | as main |
| MonetaryCheck | rows 17.90 / 15.53 / 12.93 / 12.46, late rows 0.000 / 0.276 / 0.322 / 0.333, allowance 0.650: green, premises reached | 15.05 / 12.88 / 13.07 / 13.42 (20% and 40% above 10%, hidden by the allowance), late 0.000 / 0.137 / 0.241 / 0.235 | 17.95 / 15.57 / 12.97 / 12.49, late 0.000 / 0.276 / 0.328 / 0.342 |

| harness | main after | CU2 after | TQ1 after |
|---|---|---|---|
| FoodProcessingCheck 5 | $296.23 on a herd against $-107.55 on a ship | $290.67 against $-113.11 | $296.21 against $-107.57 |
| HealthCheck, poor city | served 100.0 / 100.0 / 85.1% at x0 / x1 / x15, coverage 100.00% against 88.06%; deaths 9.36 against 14.58 a year per 1,000, the old 48.50 against 91.46; the year's hunger +4.8 points (5.6 priced out, -0.8 shelf); 12 clinics, 15.7% / 12.6% of the people between the bills | 100.0 / 100.0 / 87.7%, coverage 96.36% against 89.13%; deaths 11.26 against 14.05, the old 61.66 against 84.97; hunger -1.7 (7.3 priced out, -9.0 shelf); 17 clinics (42,500 places against peaks of 34,368 / 34,549 / 32,605) | identical to main, to the person |
| MonetaryCheck | unchanged | unchanged | unchanged |

No OBSERVED line is left in FoodProcessingCheck or MonetaryCheck, and in HealthCheck only the one premise it does not
cause (`the poor city has somebody at the eat-less step`), reached on all three.

## FPK1 - FoodProcessingCheck 5

The section read the lived-in city's own wages ($163 an unskilled hand), exchange rate and freight, what the city takes
of the processed meat (which prices it between floor and ceiling and puts the sales tax on it), the rate the sector
runs at and what it pays for crops. Any move of the path moved the two profit sums. It now sets the five goods'
markets at their literal world band at par (exchange rate and freight 1), the plant's wage at the founding ladder
(`PayTier.wageOf`, through `EconomyManager.updateWages`, the call the game makes), the sector's four ratios at
nameplate, the city's taking of the plant's output to nothing (so it all goes abroad at the floor and pays no tax at
home: the header's "sells at the world's floor"), and the crops at their ceiling in both cases, so meat is the only
price that moves. The city's values are put back before section 6 reads them (every later log line is identical to the
baseline). Four hard `fixture:` lines say the premises were caused, among them that a Meat Works on a farm's floor earns
($296.23 a month): without it "more than twice a ship's" passed on a herd that lost money.
The slice probed path independence: wages x0.5 to x2.5, exchange rate 0.7 to 1.3, operating rate 0.22 to 1, demand 0 to
5x the output and crop prices x3 left the two figures at $296.23 and $-107.55 to the cent; what is left of the city in the
sum is upkeep, power and water (the figures move by a few dollars from tree to tree, above).
*Finding, not a defect:* a Meat Works on a ship's meat cannot be positive on the model's own ladder (-$107.55 a month
with the crops at their floors), which is the header's "twenty-seven cents a tonne before a wage". The claim
`homegrown > imported * 2` is the same inequality as before; it is true now for a reason (the herd earns, the ship does
not pay its wage), and the printed detail says so instead of `Infinityx`.

## HX1 and RN1-F - HealthCheck's poor city at three prices

Both rows are the one fixture. The drain (savings 0, bank shut) left every cell clearing fifteen bills (the employed) or
not one (the out of work): 11.9% of the people paid nothing at x1 and at x15 alike, 88.1% paid everything. A household's
general-care bill is about 0.007 against a basket of 0.1, so the span from one bill to fifteen is a thin band of income.
The claims then compared the twins' noise (the brief's 101.39 against 101.37 a year per 1,000).
- **betweenTheBills()** (each window month, every twin): a cell that is short of a basket and `BETWEEN_THE_BILLS` bills
  (the square root of 15, the geometric middle of one bill and fifteen, from `TaxPolicy`'s own two constants) is given the
  savings that leave it exactly there after the month's rent, fixed bills and planned shopping; a cell with more is left
  as it is. The bill is the model's own: `Healthcare.treatmentFeesAtOne()` over the paying people, a household's share by
  its people (as `HouseholdAccounts` splits it). Its first form swung (x15: 100% paid, then none, then 100%): it struck the
  month's money from the month's after-fixed alone, which already holds last month's charge. It adds back what the cell
  was charged (remembered by cell), the stability `affordCare()`'s own comment asks for. It is the fixture's cause only:
  nothing outside the harness moves.
- **keepBedsAhead()**: CU2's path grows the free twin to 34,610 people against the fixture's twelve clinics (30,000
  places), so "a place for more people than any twin holds" was not caused there. The free twin strikes the clinics each
  month (a fifth over its people, `BEDS_AHEAD`) and the others lay the same clinics in the same months: a twin that added
  its own read the buildings' upkeep 3% apart (36.24 against 37.40), failing the line that says the same buildings stand
  in both. On main it adds none (30,000 places against 17,000 people), so main's path is the harness's old one.
- **Hard lines the fixture now causes:** every twin held a place for every person in every month; the dear fee turns away
  at least `MATERIAL_TURNED_AWAY` (five points of the care offered; the old twins parted by 0.0, the new ones by 15); the
  beds are not what differs; a high fee serves a smaller share; its coverage is lower and its baseline sickness higher
  (these two lost their `if (pricedOut)` gate: it is caused).
- **The three played claims are hard again**, as they were before PO1 made them observations: the dear city buries more of
  its people (14.58 against 9.36 a year per 1,000), its old first (91.46 against 48.50), and the households who skipped
  a bill ate with it (+4.8 points of hunger against the unchanged tolerance of 5).
  *Call, with its reason:* the hunger line is hard on a margin of 0.2 point on main and TQ1 (-1.7 on CU2). I kept it hard
  because it is the harness's own claim with its tolerance untouched and the fixture now causes what it reads. The leak
  behind it is the model's, not filed here: 5.6 points of the dear city are "priced out of a basket" although the rule
  (`Household.affordCare()`) leaves a household its basket, which it counts at `subsistence` while the grocers ask at the
  satiation price. A harness line with a thin margin may fail on a later batch; say if the leak should be queued.

## MQ1 - MonetaryCheck: not mended, left in the queue (a row bigger than a fix)

A slice wrote the settle on the price index (it waits until the index's monthly rate is within a quarter of a point of the
year's mean for twelve months, at most 90 months) and measured it on all three trees. It does not give a steady town:
- main: month 97 (72 months of run-on, 713 people), rows 24.76 / 23.62 / 20.39 / 16.47%, late rows 0.000 / 0.031 / 0.403 /
  0.679 against the allowance of 0.650: the premise misses by 0.03 and the ordering laws are skipped.
- CU2: the index is not steady after 90 months (month 115, 791 people), rows 17.94 / 16.23 / 13.28 / 11.57, late rows up to
  0.644, and the builders' crews premise misses too.
- TQ1: month 97, rows 24.84 / 23.68 / 20.60 / 16.40, late rows up to 0.946: over the allowance.
At its own 3% dial the founding's index never goes still: it rises 1-2% a month for good (16% a year at month 70, 25-30% at
120-150, the D$ falling to 4.87 a US$, the town flat at 700 people from month 85). The shelf reprices once a year (months
49, 61, 73, 85), and a month's delay in the hand moves each repricing by a month, which is what the late rows measure: the
same founding read in other windows gives the 20% and 40% late rows 0.108 / 0.071 (months 48-83), 0.194 / 0.400, 0.296 /
0.723, 0.189 / 0.432. So steadiness does not shrink the noise, where the repricings fall is not set by how steady the rate
is, and the allowance by its own rule (twice the largest reading) would be 1.4-1.9 points, 14 to 19 times GW1's 0.100.
*Not taken:* widening the allowance to read a town that is not steady is moving a premise to let a line through, and
keeping the cap leaves the laws skipped where they ran. MonetaryCheck stays main's: green on all three trees, its premises
reached under the shelf's settle, and the row stays in the queue with this evidence. What a steady reading needs is a
different experiment (a dial rule that holds the currency, or a window cut between the repricings), not a settle test.

## Calls

- MQ1 left the bundle (above); the orchestrator files it back under its own id. MonetaryCheck is not in this diff.
- The `FoodProcessingCheck | ...and the difference is most of what a Meat Works is worth | FB1` entry is deleted from
  `docs/notes/known-reds.txt` (the line is green on all three trees and no longer reads the path). No new known red.
- RN1-F's "tried fourteen clinics so the beds cannot bind" is superseded by `keepBedsAhead()`: the beds are caused, not
  waited for.
- No process change: no script, check machinery or session rule is touched (`git diff --numstat` over `tools/`: none).

# BO1-S - the owner line switched on (notes)

The brief is `docs/queue/BO1-S.md`; BO1's measurement it re-takes is
`docs/queue/BO1-notes.md`, section 1.

**The young city, plainly (the orchestrator, 11-20):** the playtest's own city with the
line on is a fifth smaller at month 121 on 0.9.161, after OP1 (1,359 people
against 1,732 with it off), where on 0.9.160 it was a third larger (1,567
against 1,172). Section 10 has the tables; Jerus hears it from the
orchestrator.

## 1. The probe

`OwnerCheck` with `-Downer.probe=true` (never the default run): the playtest
city of OwnerCheck's section 9 (`LongPlaytest.founding()`, the auto-builder,
AuctionCheck's founding rhythm), played to month 121 twice, the line on
(`HouseholdBalance.setOwnerLine(true)`) and off, printing at months 1, 13,
37, 61 and 121: the people, the retired's disposable income a household
(`getDisposable(RETIRED_ROW)`), the pension paid
(`EconomyManager.getPensionsPaid`), `careAffordability(SENIOR)`, the
senior-care pull (`Migration.getLastSeniorPull`), the owners' bank savings
against all households' and their money abroad. Month 1 is the founding
month, before the first arrivals. About 15 s for both runs.

Run: `java -Downer.probe=true -cp "target/classes;$(cat target/classpath.txt)" ham.citybuildersim.OwnerCheck`
(with the data files named).

## 2. On 0.9.160 (24b7bfcbc), before OP1

| month | people on / off | retired disp/hh on / off | pension paid on / off | careAff(SENIOR) on / off | senior pull on / off | owners' bank / all bank (on) | owners abroad (on) |
|---|---|---|---|---|---|---|---|
| 1 | 0 / 0 | 0 / 0 | 0 / 0 | 1.00 / 1.00 | 1.000 / 1.000 | 0 / 0 | 0 |
| 13 | 128 / 152 | 0 / 0 | 0 / 0 | 0.01 / 0.83 | 1.004 / 1.249 | 38 / 65 | 11 |
| 37 | 440 / 511 | 0 / 0 | 0 / 0 | 0.04 / 1.00 | 1.013 / 1.300 | 722 / 848 | 537 |
| 61 | 700 / 450 | 0 / 0 | 0 / 0 | 0.06 / 0.98 | 1.019 / 1.294 | 782 / 1,042 | 1,040 |
| 121 | 1,160 / 404 | 0 / 0 | 0 / 0 | 0.05 / 0.44 | 1.016 / 1.133 | 1,061 / 2,017 | 1,747 |

Five owner families from month 13 to the end, none dropped. Line off, the
bank holds 645 at month 37 and 203 at month 121.

- The retired are paid nothing in either run: no pension and no disposable
  income of their own, as BO1 found. With the line on, the senior-care pull
  is almost all gone (`careAffordability(SENIOR)` 0.01 to 0.06), as in BO1.
- The difference from BO1 is the line-off city: on today's model it
  peaks near month 37 (511) and falls to 404 by month 121 (BO1's 0.9.90 had
  3,500). With the line on it keeps growing, to 1,160. So on today's model the
  line does not shrink this city; it ends nearly three times larger.
- The owners' banked dividends (BO1's second channel): 53% of the city's
  bank savings at month 121 (1,061 of 2,017), 1,747 abroad.

## 3. Which ship turned the line-off city (the orchestrator's question, 11-19)

The probe's line-off run on 0.9.158 (ba734bf85, before HS1) and 0.9.159
(7117fef6b, HS1), each in a scratch worktree with the probe patched in:

| month | people off: 0.9.158 / 0.9.159 / 0.9.160 | careAff(SENIOR) off | senior pull off |
|---|---|---|---|
| 13 | 152 / 152 / 152 | 0.83 / 0.83 / 0.83 | 1.249 / 1.249 / 1.249 |
| 37 | 509 / 511 / 511 | 0.83 / 1.00 / 1.00 | 1.250 / 1.300 / 1.300 |
| 61 | 448 / 450 / 450 | 0.83 / 0.98 / 0.98 | 1.250 / 1.294 / 1.294 |
| 121 | 405 / 404 / 404 | 0.63 / 0.44 / 0.44 | 1.190 / 1.133 / 1.133 |

(Line on, 0.9.158: 437, 698, 1,218 at months 37, 61, 121, careAff(SENIOR) 0.00.)
The fall is already on 0.9.158: HS1 did not turn it. HS1 moved senior care's
affordability (0.83 flat to 1.00, then 0.44 at month 121) but not the people.
0.9.160 is 0.9.159 to the figure.

The playtest's own city (`LongPlaytest -Dplaytest.months=130
-Dplaytest.trace`, 0.9.160, its `-pop.csv`): 152, 635, 917, 1,172 at months
13, 37, 61, 121, still growing (jobs flat at 617 from month 91). So the
playtest city does not fall; only the probe's rhythm does (OwnerCheck
section 9's: the auto-builder from month 1 with forty houses at once).
(Run cut at month 130, so its exit 1 is the audit's lines at month 132, the
short run's end.)

## 4. The playtest's own city, line on against off (0.9.160)

The orchestrator (11-19): the ensemble judges the playtest's city, so it
comes first. `LongPlaytest -Dplaytest.owners=on|off` (new: unset, the
city's dial; set, the line forced and OwnerCheck's probe row printed at its
months as `OWNERS ...`), `-Dplaytest.months=130 -Dplaytest.trace`, about
60 s each. The off run's `-pop.csv` is the default run's, column for column.

| month | people on / off | retired disp/hh | pension paid | careAff(SENIOR) on / off | senior pull on / off | owners' bank / all bank (on) | all bank (off) | owners abroad (on) |
|---|---|---|---|---|---|---|---|---|
| 13 | 128 / 152 | 0 / 0 | 0 / 0 | 0.01 / 0.83 | 1.004 / 1.249 | 38 / 65 | 65 | 11 |
| 37 | 563 / 635 | 0 / 0 | 0 / 0 | 0.06 / 1.00 | 1.019 / 1.300 | 1,092 / 1,235 | 405 | 578 |
| 61 | 1,020 / 917 | 0 / 0 | 0 / 0 | 0.02 / 1.00 | 1.007 / 1.300 | 1,256 / 1,635 | 1,619 | 1,830 |
| 121 | 1,567 / 1,172 | 0 / 0 | 0 / 0 | 0.08 / 1.00 | 1.025 / 1.300 | 1,572 / 3,464 | 2,516 | 2,672 |

(Month 1 is not printed by the playtest: its first audited month is 2.)
Both runs exit 1 on the households' own line in their last month, the
cut run's (off at 132: UnsettledIncome, Rent, Fees and Shopping; on at 133:
the same four and Unfunded). The default run (lp160, unset, traced the same
way) has the off run's four at 132, so the cut predates the line.

- The retired are paid nothing, line on or off, and with the line on senior
  care is beyond them (0.01 to 0.08) and its pull gone. Yet the playtest city
  with the line on is a third larger at month 121 (1,567 against 1,172): on
  today's model, the care pull is not what holds this city back.
- The owners hold 45% of the bank's savings at month 121 (1,572 of 3,464) and
  2,672 abroad; the city's bank savings are larger with the line on (3,464
  against 2,516), so the banked dividends do not stall it.

## 5. The switch, prepared (in its own commit, to drop if Jerus says no)

- `OwnerHousehold.IN_CITIES = true`; the comments that said it was off
  restated (`OwnerHousehold`, Game's initializer, OwnerCheck 9).
- **The People screen** (BO1's section 2 named the places): the owner
  families are a piece of the mosaic's outside strip (two adults a
  household, a click opens their books), a column of the outside table by
  age (`families.getOwnerAdults()`, in the total), a row below the rule on
  Household money (`belowTheRule`, `otherRows`: their books against a
  basket, as the prison's), and the texts that list the groups
  (`OUTSIDE_INFO`, `BELOW_THE_RULE`, the verdict's and `otherRows`'
  javadocs). The outside panel (`outsideStatement`) finds any cell by key,
  so their books open with no new code.
- ★ **No "Owner families" tile** in the outside row: a tile draws a
  sparkline from a history series, and an owners' series is a saved field
  (`HistorySave`, YearBook). Kept out so the batch changes no save; the
  mosaic piece opens the same books. Recommended later, with the wealth study.
- ★ **The owners stay out of the housing match**, as BO1 shipped them: they
  pay a whole door's rent (`Game.rentShareOf` 1) and count as housed
  (`housedShareOf` 1) without taking a door. Placing them is a third seeker
  group in `FamilyModel` (HS2's file, in flight; its seeker arrays are
  saved), the change that broke HousingCheck and ReadPathCheck in BO1. Five
  families of two in a city of 1,500 people; recommended: queued, not here.
- **Saves:** switching on changes nothing an old save reads. A save from
  before has no owner families (the line was off): its listed companies keep
  their founders' shares with the households, and a company that lists after
  the load gets a family. Incomplete, not wrong: no format bump.

## 6. A theft nobody was handed (ShortSellingCheck 3, mended)

With the line on, ShortSellingCheck's town of 16 broke the households'
cash identity every month from the first theft: residual = -2 x
`StolenFromBusinesses`, 3.3e-4 of the wage bill. Its three owner families
took 6 of its 9 adults, so every working cell fell under half a household
(`Household.isEmpty()`, `EMPTY_CELL`). The offenders were all in those
cells. `Offending.steal()` weighed every cell's offenders, so the theft was
taken, half from the savers and half from the tills, and
`creditByWeight()`, which pays only cells not empty, paid nobody: the
savers' half vanished from the households, and the tills' half was booked
by the audit as handed to them.

- **The mend** (31e13a429): the weight is the offenders' in the cells that
  can be paid; with none, nothing is stolen. It is not the owners' fault:
  any city whose offenders are all in empty cells did this, line on or off.
  Measured on the leak's own town at 1e-14 to month 56. `HouseholdBalance`
  is not touched (HS2's file).
- **Restated for the owners** (the same commit): `CrimeCheck` (the labour
  force is the workforce less the students, the prisoners and the owners),
  `EducationCheck` (the bands, the students, the prisoners and the owners
  are the workforce), `ShortSellingCheck` 9 (where no ordinary cell holds a
  company, the owner families do: their trades with the world settle as any
  cell's).

## 7. The suite with the line on: each red read (0.9.160, after the mend)

The suite's first pass (`AllChecks -f`, timing soft) and then the 18 red
harnesses rerun one at a time after 31e13a429 (`target/rerun-1.txt`), each
assertion read against main's R1e gate log. The timing reds (WorldMarket,
EarthTerrain, SaveSlot, Map, Plan, Kept's clocked lines) were skipped, as
the orchestrator said: the dry run's suite judges them.

**A young town starved: one cause under most of them.** The supply takes
the owners off every band by `max(0, 1 - owners/all)`
(`PopulationManager.workforceByBand`), two adults a listed company from the
month the founders' grant reserves the family (`ownerFamilies()` counts a
reserved family before its cell is formed), whatever the town's size. A
town with no more adults than that has nobody working, and a young one
grows slower:

| harness | line on | main (R1e's gate) |
|---|---|---|
| Fitout 3 (1900 farm town) | the wait NaN: no builders (`getAverageFill` 0) | 61.7 months |
| Slope 5 (held scratch city) | Home Care unbuilt in 120 months | built in 53 |
| IslandLink 9 (240 months, auto-built) | the island never linked | linked in m106 |
| CentralBank 7 | no year of prices by m142 (60 months played) | at m99 (17) |
| AutoBuild 7 | general-care demand 1,181 | 2,077 |
| AutoBuild 10 (1900 town, m12) | the power order NOTHING | 4 Coal Power Plants, cut to ground |
| Slope 9 (Medellin, 2000 months) | owns 5.3 km2 | owns 66 km2 |
| the probe (section 2) | 440 people at m37 | 511 (line off) |

**The other moved fixtures** (a played city the line moves; each with its
FS sweep, docs/studies/flaky-suite.md section 6):

- Road 9 (FS12): the gravel town carries 399 vehicles a day (main 457), so
  the advice gives no card and the move has nothing to follow.
- EssentialWorks, "an unfunded school keeps its funding before spare care"
  (FS15): the played town's premise (a school warranted and unserved, a care
  card cheaper than the school) fails; the five lines follow it.
- Buyback 9 (FS13): the callable line at m104 is D$22,977k, the world 0k of
  it (main at m97: 40,660k, the world 18k); the bank's equity -137.8.
- Fund (FS13): (b) the offering raises 841.10 of the 1,028.41 owed, so the
  preferred is not redeemed whole (five lines); 9e no company but the Bank
  whose households hold what the limit takes; 10 a limit posts nothing.
  FundLedger 8 (FS13): the fund holds none of the company.
- Holders (FS14): 2's paper gain, 10's businesses' part of the line, 13's
  premium reopening (its six closes follow the fixture). Its three [known]
  lines are main's (gate.txt), at other figures.
- Denomination (FS2): the plain city's month has no partner with a lane (0
  lanes, 0 arrows), twice.
- SaveFile (FS17): at m154 everybody can afford a basket at full shelves.
- BasketYear 7 (FS17): the 1900 town with the cap lifted buys no cars (0.0).
- Fitting 13 (FS1, the sweep that leaves Fitting played): the village has
  two bakeries (main three), and the section scraps two before it asks for
  a fresh order, so there is no bakery left to fit.
- Slope 9 and Landmass 13, "households with no home" (FS7): not the owner
  families, who are outside the housing match (`FamilyModel` leaves them
  out of the squeeze, so they are never unplaced). Slope 9: 36.0 households
  nowhere, m183 to m358, with 243 homes; HN1's class (homes not built in
  time). Main's runs of both cities have none.
- ScaleCheck (FS2): one owner family a company does not scale with K: the
  copy has its city's five families, not five K. The named savings line
  (HP2's, folded into FS2) moves from 2.83e-2 to 3.54e-2 against
  `TRACK_SAVINGS` .016, and section 10's six "savings obeys the existing
  TRACK law" lines are new; kept named under FS2, as the orchestrator said.

## 8. The mend: the owners at most a share of the adults (★, the orchestrator's call, 11-20)

The orchestrator (00:44): "A town with nobody working is a fault, not a
moved fixture", taking the recommendation under Jerus's 20:01 "do
recommended"; he hears of it in the ship report.

- **A change to BO1's "a family a company"** for towns under about 200
  adults. `OwnerHousehold.OWNER_ADULT_SHARE` = .05: the owner families'
  adults are at most a twentieth of the city's adults
  (`OwnerHousehold.adultsOf`, which `Game.ownerAdultsWanted()` is on the
  month's figures). The same figure goes to the supply
  (`PopulationManager.setOwners`) and to the census (`FamilyModel.rebuild`),
  where before each took `2 x ownerFamilies()`, a family the grant had only
  reserved counted. The load path already read the census's figure.
- **What it leaves.** A family a company holds in a city of 40 adults a
  company (200 for five). Under it the cell holds fewer families than
  companies, the stakes shared equally as before. Under 20 adults it is
  under half a household, empty (`EMPTY_CELL`), and the founders' grant
  waits, as it always did for an empty cell (`grantFounders` false), so a
  company lists once the town has grown to its family. ShortSellingCheck's
  town of nine adults has no owner families until it grows.
- **Proved in OwnerCheck 11**, bare fixtures caused on the census: three
  families reserved in a town of nine adults give 0.45 owner adults, under
  half a household, an empty cell and listings that still wait; a hundred
  adults hold five, fewer families than companies; ten thousand adults hold
  two a family, unchanged, and a waiting listing lists to them.
- **The People screen's two texts** (`OUTSIDE_INFO`, `BELOW_THE_RULE`) say
  the owners are never more than one adult in twenty, from the dial.

**ScaleCheck plays every game with the line off** (`ScaleCheck.lineOff`, at
each construction, before a founding or a load). The owner families are a
fixed count by design, not K times the city's, so a copy K times over
carries its city's five families where the law wants five K, and their
savings cannot scale: with the line on the named savings line moved from
2.83e-2 to 3.54e-2, and section 10's six "savings obeys the existing TRACK
law" lines were new. Six new named lines for a count that does not scale by
design was too many (the orchestrator). The check proves the rest of the
model's scaling, which the line does not touch; HP2's line stays named
under FS2 as main has it, at main's figure.

## 9. After the merge of OP1 (0.9.161, 17f8a4c05), with the cap: each red again

Main merged at b3f36be07, every harness of section 7 rerun one at a time
(`target/rerun-2.txt` to `rerun-7.txt`), against OP1's gate logs.

**Green now:** OwnerCheck (11 included), ScaleCheck (with the line off, as
on main, so no FS2 line of its own), PolicyPreview, AutoBuild, Buyback,
Landmass, ShortSelling, FundLedger. Fund and Holders show only main's
`[known]` lines. Slope 9's CQ1 line, "households' money was not conserved",
known on main, passes on the branch (section 6's theft mend).

**The town of nine adults plays with the line off** (★ mine, under the
orchestrator's "restatements fine"; agreed at 04:11: "those harnesses are
about markets, and OwnerCheck covers the owners"): ShortSelling, Fund, FundLedger and
Holders build `run()`'s town, which stays at nine adults for 169 months.
Under `OWNER_ADULT_SHARE` its owner cell is empty, so the founders' grant
waits, no company lists, and no household holds a share or is paid a
dividend: the households' savings at m72 are 4 with the line on, 205 off
(a trace). Every premise those harnesses have (a company the households
hold, the fund buying it, the households' paper) cannot hold there, so each
plays every city with the line off (`ShortSellingCheck.lineOff`, in each
class javadoc), and section 6's owner-cell fallback in ShortSelling 9 is
back to main's. The line's own claims for a small town are OwnerCheck 11's.

**Two fixtures restated to cause their premise:**

- Fitout's 1900 farm town stood one Construction Depot: 560 points at a fill
  of .350 and a work ratio of .885 make 173 a month against 174 of repairs,
  so nothing for the sites and every wait NaN (the owners' 12 adults of 334
  at work tip it; on main the same depot leaves a few points, a wait of
  61.67 months). Two depots: the wait 6.02 months, all 66 green.
- Fitting 13 needs four bakeries standing (its scraps take three, the last
  line asks for a fourth to fit); the month `fitTown` plays retired one on
  the branch. It tops them up to four: green, 0 of its 3 still without, as
  on main.

**Named under their sweeps** (the ship file's `## known-reds`), each on the
branch's moved path and passing on main:

| harness, line | sweep | the branch | main 0.9.161 |
|---|---|---|---|
| Slope 9, steep ground owned | FS7 | 0 of 11.920 km2 | 5.287 of 81.945 |
| CentralBank 10, a note and its run-off (2) | FS10 | no note: the nine-adult town's households unpaid (savings 4 at m72, 205 off) | sells |
| Road 9, the move and its count (2) | FS12 | 399 vehicles a day, no card | 457 |
| EssentialWorks, the protected school (5) | FS15 | the premise fails (no figure printed) | passes |
| Denomination, a partner with a lane | FS2 | 0 lanes, 0 arrows | has one |
| SaveFile, somebody short of a basket | FS17 | everybody can afford one | somebody cannot |
| BasketYear 7, the lifted town's cars (2) | FS17 | 0.0485 a household default, 0 lifted | 0.1631, 0.0133 |

CentralBank 10 is named, not played with the line off: its rerun is twenty
minutes and its sections 1 to 9 and the rest of 10 pass with the line on.
IslandLink 9 is OP1's FS7 row, unchanged.

## 10. Re-measured on 0.9.161 with the cap (after section 9)

The playtest's own city, `LongPlaytest -Dplaytest.owners=on|off
-Dplaytest.months=130 -Dplaytest.trace=target/lp161on|off`:

| month | people on / off | retired disp/hh on / off | pension paid on / off | careAff(SENIOR) on / off | senior pull on / off | owners' bank / all bank (on) | all bank (off) | owners abroad (on) |
|---|---|---|---|---|---|---|---|---|
| 13 | 136 / 148 | 0 / 0 | 0 / 0 | 0.02 / 0.83 | 1.005 / 1.249 | 35 / 60 | 55 | 9 |
| 37 | 512 / 705 | 0.01 / 0.01 | 1 / 1 | 1.00 / 1.00 | 1.300 / 1.300 | 755 / 872 | 495 | 440 |
| 61 | 879 / 1,242 | 0.02 / 0.01 | 3 / 3 | 0.44 / 1.00 | 1.133 / 1.300 | 565 / 753 | 1,233 | 1,260 |
| 121 | 1,359 / 1,732 | 0.08 / 0.07 | 11 / 15 | 1.00 / 1.00 | 1.300 / 1.300 | 1,359 / 3,324 | 7,683 | 1,916 |

Both exit 1 on the households' own line in the cut run's last month (on
134, off 132), as on 0.9.160: the cut predates the line.

OwnerCheck's probe (`-Downer.probe=true`, section 9's city):

| month | people on / off | careAff(SENIOR) on / off | senior pull on / off | owners' bank / all bank (on) | all bank (off) | owners abroad (on) |
|---|---|---|---|---|---|---|
| 13 | 136 / 148 | 0.02 / 0.83 | 1.005 / 1.249 | 35 / 60 | 55 | 9 |
| 37 | 385 / 479 | 0.05 / 1.00 | 1.014 / 1.300 | 470 / 538 | 489 | 390 |
| 61 | 361 / 443 | 0.06 / 0.36 | 1.018 / 1.107 | 228 / 324 | 172 | 675 |
| 121 | 646 / 363 | 0.04 / 0.84 | 1.013 / 1.253 | 404 / 571 | 145 | 925 |

- **The playtest's city turned.** On 0.9.160 the line-on city was a third
  larger at m121 (1,567 against 1,172); on 0.9.161 it is a fifth smaller
  (1,359 against 1,732). OP1 (every policy open from the founding) lifted
  the off city by half (1,172 to 1,732) and the on city not (1,567 to
  1,359). With the line on the city's bank holds 3,324 at m121, 1,359 of it
  the owners', against 7,683 with it off.
- **The probe's city still gains** with the line (646 against 363 at m121;
  0.9.160: 1,160 against 404), both smaller than on 0.9.160.
- The retired are paid a little on both lines now (11 and 15 at m121), and
  senior care is within reach with the line on from m37 on the playtest's
  city.
- The ensemble judges the whole spread (80 runs against OP1's baseline).

## 11. The ensemble (80 runs, against OP1's `docs/ensemble/0.9.161.md`)

`tools/ensemble/ensemble.sh BO1-S` on the classes of cbac2d8dc, OP1's extra
columns and five of the line's own (`owners_bank_share`, `retired_disp`,
`senior_care_aff`, `owner_families`, `owner_adults`; the three no-argument
getters on Game are new for them). 115.9 minutes, exit 0, committed as
`docs/ensemble/BO1-S.md`.

**Verdicts:** batch PASS, hard criteria 10/10 (M1 and F1 in every
policy), 24 of 26 criteria. The two that fail are AUTO2's A1 and A2, which
fail on OP1 too. One verdict changed: HIGH R2 (info), FAIL to PASS.

| AUTO2 | BO1-S | OP1 (0.9.161) |
|---|---|---|
| A1 inflation m240-end | 2.89% [2.56 to 3.21], 10/16 in band | 3.05% [2.46 to 3.56], 7/16 |
| A2 longest yoy > 10% spell from m240 | 14 [10 to 36] | 22 [9 to 40] |
| young city inflation m240-600 | 4.18% [1.86 to 6.59] | 4.18% [2.77 to 6.67] |
| policy rate mean from m240; months at 0% | 4.84%; 19.1% | 5.17%; 17.7% |
| mean yoy from m240 | 2.95% | 3.12% |
| T1 seeds reaching 40,000 (month) | 16/16 (m798 [655 to 1,166]) | 16/16 (m769 [527 to 1,231]) |
| T2 final population | 2,435,388 [359,531 to 4,630,876] | 2,848,186 [1,346,579 to 4,340,954] |

T2 is 85.5% of OP1's, inside its 75% floor; the lowest seed is about a
quarter of OP1's lowest.

**The line's own columns,** AUTO2's median of 16 seeds (no OP1 figure:
the columns are new):

| month | people | owners' share of the bank's savings | retired disposable | careAff(SENIOR) | owner families | owner adults |
|---|---|---|---|---|---|---|
| 121 | 1,304 | 55% | 0.062 | 0.64 | 5 | 10 |
| 600 | 27,760 | 27% | 0.125 | 1.00 | 6 | 12 |
| 1,200 | 149,100 | 28% | 0.315 | 1 | 7 | 14 |
| 2,400 | 2,435,000 | 4.5% | 7.90 | 1 | 7 | 14 |

The owners hold more than half the bank's savings in the young city and a
twentieth by the end; the cap binds only in the first months (10 adults
for 5 families at m121 is the uncapped two each).

**The worst seed, plainly (the orchestrator, 06:17):** AUTO2's lowest final
population is 359,531, against OP1's lowest 1,346,579: a fall of about three
quarters. It is one seed, AUTO2 seed 7, which stalls from m600 to m1800
(24,617, 29,985, 45,009 people at m600, m1200, m1800) and grows to 359,531
by m2400; the next lowest of the sixteen ends at 1,666,727, above OP1's
lowest. The ensemble passes (M1 and F1 clean, T2 inside its floor), so the
batch ships on the rule.

## 12. REV-BO1-S: SHIP, no P1; three minors queued as BO1-R

The review (06:18, read-only) found nothing in money, saves or the screen.
Its three minor findings ship as they are, queued as one row, BO1-R:

1. **No adults reserved where no family forms.** Under 20 adults the
   reserved families still take up to 0.95 adults (`OWNER_ADULT_SHARE` of
   the adults, under half a household) off work and out of the families,
   into an owner cell that is empty and never settles, its door's rent
   going to UnsettledRent (`HouseholdBalance.java:1172`), for as long as the
   town stays small. The changelog's "a town under 20 adults has none" was
   true of the families, not of those adults; it now says so. The mend:
   `Game.ownerAdultsWanted()` gives nothing while the figure is under
   `HouseholdBalance.EMPTY_CELL`, so the supply, the census and the cell agree.
2. **The owner cell's trades with the world, in OwnerCheck.** ShortSelling
   9's owner-cell line (the owner families' trades with the world settle)
   went with `lineOff` (`ShortSellingCheck.java:493`), and OwnerCheck has
   no shorts, lending or fund section. The mend: an OwnerCheck section on a
   town large enough to list, the owner cell's shares lent and sold to the
   world, each a pair of lines in the audit.
3. **OwnerCheck 11's middle case built through the census and the cell.**
   Its hundred-adult case (`OwnerCheck.java:908`) checks only `adultsOf`'s
   arithmetic (2.5 families of 3); it builds no census, no cell and no grant
   split. The mend: the case through `FamilyModel.rebuild`, the owner cell's
   households and a listing's grant shared among fewer families than
   companies.

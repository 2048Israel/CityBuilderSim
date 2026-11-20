# FS16 — schools, work and the households' books cause their conditions — notes

Batch FS16, the flaky-suite study's sweep §6.15 (`docs/queue/FS1-notes.md`, `docs/queue/FS1.md`): nine harnesses
(EducationCheck, LabourCheck, StaffingTieCheck, StaffingDrawCheck, ArrivalMixCheck, OwnerCheck, OutsideCheck,
HouseholdCheck, WageLadderCheck) cause their own conditions, so their lines stop reading whatever a played city
happens to do. Built 2026-11-20 by the FS16 session, cut from origin/main 9a5ca6ea8 (0.9.169). It merged origin/main
at 0.9.171 (CPB2, RN1-H-M, MC1, FS7) cleanly: no conflict.

**Harness-only.** No model class, no dial outside a harness, no `buildings.json` row and no save field moves. The
model and the trace are unchanged: the month runs the same statements in the same order. No ensemble is owed
(moves_playtest=no). Every number the harnesses print about a played run that is still played (OwnerCheck's
village, the identities' played towns) is the same as on base: OwnerCheck's village still forms 5 families and
drops 0.

## The rule each section was held to (FS1 ★1-★4)

- A sweep edits only its own harnesses. Borrowed builders stay as they were: `ArrivalMixCheck.bidUp/draw`,
  `OwnerCheck.probeLine`, `MonetaryCheck.founding/step` and `FittingCheck.village` are unchanged.
- The order of causes: (1) a setter on a scratch city, (2) a town built for it, (3) a threshold from both sides
  against the model's constant, (4) the reading at the caused month. A bounded play is the last resort, and it
  must fail as a fixture.
- A caused premise is no Observation. KEEP sections (identities) are not touched, except where a premise inside
  them was found.
- Labels stay where the law is the same. Every fixture city comes from `GameFiles.scratch` or `TempDirs`, never a
  save slot.

## The rows (41), what caused each and what changed

### EducationCheck (17 rows; 14 CAUSE, 3 KEEP)

- The town is built for the purpose (`townBuilt(withSchools, withMedical)`, lived `TOWN_MONTHS` = 6 for its schools
  to staff) and the skills are set by hand (`skillsSet`). The licensed heads, the posts and the workforce by band
  are set through PopulationManager's restorers. The students are put into the pipeline by `seeded(g, type,
  students, monthsLeft)` and `finishing(ed, type, students, monthsLeft)`.
- **L165** (no medical school, no doctor) and **L213** (the school staffs itself; once picked at month 360): the
  graduates are set over the doctor posts (`DOCTORS_OVER_POSTS` 1.1) and a short town's under them
  (`DOCTORS_UNDER_POSTS` 0.9). The seats are read against the posts from both sides on the model's staffing count.
- **L238** (every school enrols), **L258** (the band beneath is not drained), **L278** (a graduate stops being a
  diploma-holder), **L328** (the month's graduates, gross): the people studying and finishing are seeded with
  `FINISHING_SHARE` and the bands' shares are set (`DIPLOMA/COLLEGE/UNIVERSITY_SHARE`), so somebody studies and
  somebody finishes in the month read.
- **L343** (the ladder is its bottleneck) and **L388** (the leavers' diplomas, gross): the pinched town is built
  with no high school and children to seat. The leavers are read in its caused month.
- **L422** (tuition decides who goes): the seats stand for the whole pool (`PLENTY_OF_PLACES` .9 with
  `SEATS_MARGIN` .05), so they cannot bind. The free place and the paid one are priced on one hand-fed market
  (`PRICE_POOL_SHARE`).
- **L544** (the wait is real): the students are seeded at a known month of their course
  (`WAIT_POOL_SHARE` .5), where it was a 120-month city waited on for more than 10 students.
- **L656** (the unskilled band is a report card): ★ it is read as the month's flow (school leavers less the new
  diplomas), not as the stock of a pair of 300-month cities. The labourers are set plenty or scarce
  (`PLENTY/SCARCE_OF_LABOURERS` 6000/2400) on `LabourCheck.handFed`, and the market is priced by
  `LabourCheck.pricedOn`.
- **L870** (the grant on four bases): `GRANT_STUDENTS` 400 students are seeded and the expected level is set to
  `GRANT_EXPECTED_LEVEL` 1.25 through `Expectations.restore`, where a menu city was read at month 150. The surplus
  basis is caused by `SURPLUS_CONTRIBUTION_RATE` .3 and `SURPLUS_HEALTH_PREMIUM` .1.
- **L1040** (the loan at a rate): `LOAN_STAYING_STUDENTS` 300 and `LOAN_FINISHING_STUDENTS` 100 are seeded, where a
  lender city ran 150 months and then up to 24 more until loans were lent.
- **L1194** (the price of a place, EDU1's): ★ split in two. The trap runs on a hand-fed poor town
  (`PRICE_TOWN_WORKFORCE` 4000, `PRICE_TOWN_DIPLOMAS` 1500, `priceTownHeads/Posts`). The wiring keeps three Game
  towns, all fed the same pool.
- **KEEP:** L306 (a licence belongs to a person), L475 (through a save) and L580 (on the city's books) keep their
  played cities. Section 12 ("the queue includes the overqualified") is not in the rows and stays as played.
- The fullness, heads and share helpers the played sections used are gone.

### LabourCheck (10 rows; 6 CAUSE, 4 KEEP: L551 untouched, three whose premises were caused)

- New package-private helpers: `handFed(workforce, skilledHeads, posts, licensed)`, `pricedOn(pm)` (★ a market
  priced by `PRICED_MONTHS` = 60 pure `LabourMarket.advanceMonth` calls), and `arrivalsOnce(market, pm)` (a fresh
  Migration into `ROOM_TOWN` = 4000 with room, jobs and homes). EducationCheck borrows them.
- **L472** (a city cannot staff what it has not attracted): on `handFed`, where a town was played 240 months.
- **L656** (rent is a market): ★ the rent target is read against a same-month control town
  (`rentTownBuilt()`, `RENT_TOWN_PEOPLE` 4000), and the law is `rents.getRentTarget() <
  controlRents.getRentTarget() * 1.5`. Before, it was a reloaded 90-month city.
- **L725** (arriving children are not graduates), **L911** (who moves in, and why) and **L949** (some arrive
  without a diploma, for the wage) were Observations. They are caused on `arrivalsOnce`, and on a built town for
  its warm month and its measured month. The fixture lines are hard: arrivals > 1, arrivals over 10× departures
  (`ARRIVALS_OVER_DEPARTURES`), the adult share in (.3, .9), and the explanations differing by more than 1. All
  eight `Observation.reached` calls are gone.
- **L828** (a doctor shortage is priced on doctors): the licensed heads are set under the posts by hand, where it
  was 60 months and then up to 300 more.
- **L1050** (the tie's average, WS1) and **L1309** (wages against the index), KEEP: ★ the floor is struck on a
  hand-fed average (`TIE_AVERAGE_OVER_GOING_RATE` 1.3, `TIE_NEXT_AVERAGE_OVER_LAST` 1.1). The month's own reads
  are kept ("the month struck the floor on an average of the played city's wages", "the month read an average of
  its own"). `BASING_GRACE_MONTHS` is 120.
- **L551** (through a save), KEEP: an identity, not touched.
- **L1495** (payroll), KEEP: ★ the fills are set by hand (`PAYROLL_GRADUATE_FILL` .5) on the 120-month town
  (`PAYROLL_TOWN_MONTHS`), since the identity is a KEEP and the played town is its breadth. The layoff town is
  `LAYOFF_TOWN_PEOPLE` 1000.
- **Observation.HARNESSES loses "LabourCheck":** a one-line edit. FS10, FS13 and FS7 edit the same line, and FS7's
  shipped edit merged with it cleanly.

### StaffingTieCheck (4 rows, all CAUSE)

- **Section 1:** both ties are caused in a month. The founding's (share 0) is caused by `StaffingDrawCheck.noRoomMonth`.
  One at a share in (0, `Sector.MIN_STAFFABLE_TO_ORDER`) is caused by `roomMonth` and `seatWith(people,
  SPARE_WORKERS = 0, EMPLOYED_WORKERS = 200)`, through `caused(root, name, room)`. `NO_SHARE` replaces `ANY_SHARE`.
  `MONTHS_TO_A_TIE` and `play()` are gone, so the tie no longer waits on HS2's month 328.
- **Sections 2 and 3:** the laws are unchanged on the caused ties.
- **Section 4:** `sectorsNamingTwo()` reads the founding's first month (fixture: people > 0, workforce 0), and the
  hold and the asks naming two buildings are hard. The 120 played months are kept for the every-month identities;
  the "how often" counts stay printed.

### StaffingDrawCheck (2 rows, CAUSE)

- **Section 7:** caused through `drawCounts(g)` with `roomMonth`, `seatFor` and `noRoomMonth`: one order in a room
  month (the draw opens it) and one in a no-room month. `MONTHS_PLAYED` is gone.
- **Section 8:** `roomMonth` plus `seatWith` plus `setPopulation` puts the population over migration's target by the
  mean of the quartile orders' additions. The positive part `max(0, gap + add) - max(0, gap)` is asserted to the bit
  on both sides of a zero gap.

### HouseholdCheck (3 rows, CAUSE)

- **L1202** (a tier priced out of living alone shares): `flatshareTown()` is a family model of `FLATSHARE_ADULTS`
  600 unskilled adults, and `flatshareBooks()` strikes its books by hand: a rent of 400 a door, a basket of 300 a
  head, and a take-home of 500 after 60% tax. Before, it was a 60%-tax town played 120 months. The threshold is
  tried from both sides: a wage a dollar short of a home and a basket prices some out, and one that covers it
  prices none out and nobody shares. The law `sharing.left() > alone.left()` is unchanged.
- **L1475** (spending answers the real deposit rate): `handSavingAReturn()` hands the bank the deposit rate
  expected inflation + `HAND_REAL_RETURN` (.03) for `RATE_MONTHS` = 3. Before, it was a year at the dial, where
  savers were paid 0.00% on one path and 2.9% on another. Both sides are read on the page: a real rate of nought
  gives a factor of one, and a positive one gives a factor under one. The money audit (< 1e-6 each month) and "the
  factor the next month's plan was struck at" are kept. The zero side's fixture (expected inflation ≥ 0) is read on
  the founding's three months; it is a hard fixture line.
- **L1658** (fares on the riders, fuel on the drivers): the bus town is lived 2 months, every home is handed
  `Household.cars` = 1, and it is read 2 months later, where it was played 60 months (fuel $0.02k). The row-sum
  (1e-9) and waterfall identities are unchanged.

### OutsideCheck (2 rows, CAUSE)

- **L439** (EI, the premium and the books): the city is built at once and lived `CITY_MONTHS` = 6, where it was
  played 59. The adults are then set to `OVERSUPPLY` = 1.5 × the posts through the named pyramid. Before, adults
  were added in up to four waves until a pool appeared. The closing and the one-month lag are as they were, and the
  audit and the EI-credit identities stay.
- **L574** (a college town: grants, loans, tuition and a save): it is lived `EducationCheck.TOWN_MONTHS`, then three
  university cohorts are seeded (`EducationCheck.seeded`): stayers (300), first finishers (200, graduating in the
  first month) and last finishers (100, in the fifth). It is read over `COLLEGE_MONTHS` = 5, where it was played 180
  months. The money audit each month and the save round trip stay.

### ArrivalMixCheck (1 row, CAUSE)

- **L332** (section 5, played 1900 city): "people arrived" and "more without a diploma than with one" are caused on
  section 4's hand-fed draw at the 1900 point. The draw is taken at the going rate and in a market bid up, and it
  matches the year's no-diploma ratio to 1e-9. The other side is today's mix at the going rate, which brings nobody
  without a diploma. The 24-month every-month identity (mix = `ArrivalMix.at(month)`, band sums) is KEEP. The class
  ArrivalMix is R1g's and is not touched.

### OwnerCheck (1 row, CAUSE)

- **L521** (a real city: one family per listed company): the 120-month village is kept, because its every-month
  identities (the counts agree, the labour force is lower by the owner adults, the crumbs, the positive stake, every
  leaving logged) are KEEP and need its breadth.
- Its two premises are read at the months the founding causes them. The first played month's dividends read no
  sheet (`Game.recordMonth` runs after `payDividends`). The second reads Retail's first, which holds the three stores
  the village built before it and owes nothing. So the fixture is exactly that month (`MONTHS_TO_THE_LISTING` = 2,
  hard), and the law is that the company has its family when the month ends (`Equity.listIfUnlisted`'s reservation).
- The first month that opens with the exchange open and the owner cell holding a stake above the floor (month 6)
  must see the owners' offers rise (`Exchange.postOwners`).
- The slice's first try, listing by hand with `listIfUnlisted`, broke "every family that left said why" (logged 1,
  dropped 0): `tallyOwnersAtFloor` let the reserved family go within the month. It was reverted, and the model's own
  path is read instead.

### WageLadderCheck (1 row, KEEP)

- L382 is an identity: every wage is positive and finite over 24 months. It is not touched.

## The known red

`docs/notes/known-reds.txt` holds one FS16 row: LabourCheck | ...and not the whole mix (month 14 of the moved city,
1.8 skilled arrivals against 14.5 departures). Its cause was found. On a month where departures dominate,
`PopulationManager.applySkilledFlows` clips each band's skilled count at 0 and the retirements are of a size with
the flows, so the law, which is about arrivals, has nothing to tell the ADULT share from the whole mix by. It is not
a model fault. The section is now caused on a built town whose measured month is all arrivals (542 skilled arrivals
against 0 departures). The slice reproduced the old red on a departures-dominated month (the counts moved -6.1,
expected -2.4, whole mix -4.0), and that is the section's fault line. **The row goes green and leaves the file:**
the ship file's known-reds section does not carry it.

## Checks

- `./batch-check.sh --fast` on the merge of 0.9.171: compile OK, maps regenerated, stale 0 firm, the tree's changes
  only the generated docs.
- Each named harness alone on the merge, all exit 0 with no FAIL. Time against base (base on 0.9.169, this machine):

  | harness | base | branch |
  |---|---|---|
  | EducationCheck | 150 s | 38.8 s |
  | LabourCheck | 36 s | 17.5 s |
  | OutsideCheck | 25 s | 4.3 s |
  | StaffingTieCheck | 17 s | 6.3 s |
  | StaffingDrawCheck | 11 s | 6.3 s |
  | HouseholdCheck | 9 s | 4.2 s |
  | OwnerCheck | 8 s | 7.7 s |
  | WageLadderCheck | 6 s | 6.1 s (untouched) |
  | ArrivalMixCheck | 5 s | 4.9 s |
  | ObservationCheck | - | 0.8 s |

  The nine together drop from 267 s to 96 s.
- The fault lines: each slice showed one for each section it changed (a scratch edit turning the law red, then
  reverted). The two re-run here:
  - EDU1's affordability fault (reading `feeAtOne`) still turns the price-of-a-place trap red.
  - LabourCheck's old red reproduces on a departures-dominated month, as above.
- `sh tools/pick.sh origin/main HEAD` picks 48 of 232 harnesses. Observation.java's edit picks every
  Observation.HARNESSES member and LongPlaytest. Review: not needed (no money, saves or threading in the diff). Only
  the named ten were run here (the brief: no JVM beyond `--fast` and the named harnesses); the gate runs the pick.

## The calls (★ a design choice)

- ★ The rent target is read against a same-month control town, not a reloaded played city.
- ★ The markets are priced by 60 pure `advanceMonth` calls (`LabourCheck.pricedOn`).
- ★ The tie's floor is struck on a hand-fed average, and the month's own reads are kept beside it.
- ★ The payroll fills are set by hand on the 120-month town, which is kept for the identity's breadth.
- ★ EducationCheck's report card is read as the month's flow, not the stock.
- ★ L1194 is split: the trap on a hand-fed poor town, the wiring on three Game towns.
- ★ Helpers are borrowed rather than copied: EducationCheck uses LabourCheck's, OutsideCheck uses
  `EducationCheck.seeded`, StaffingTieCheck uses StaffingDrawCheck's seat helpers. All of them are this sweep's own
  harnesses.
- ★ OwnerCheck keeps its played village for the identities and reads its premises at the caused months, rather than
  standing a bare register. The bare-register sections 1-8 and 11 already prove the rules; section 9 is the wiring.
- EducationCheck section 12 and StaffingDrawCheck section 9 are left as played: they are not in the rows.
- WageLadderCheck is untouched (its one row is KEEP).

## Left for Jerus

Nothing. The batch is harness-only and every row is either caused or KEEP.

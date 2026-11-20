# FS15 notes: the works and the services' town cause their conditions

Batch FS15, built 2026-11-20 by Claude (Opus, headless) on `batch/FS15` from
origin/main b537278de (0.9.166). Origin/main bf048eb69 (0.9.167, HS2) is
merged in. Brief: `docs/studies/flaky-suite.md` 6.14 and its table
(`flaky-suite/by-section.md`, the FS15 rows), with the nine FS15 rows of
`docs/notes/known-reds.txt`.

The batch is harness-only. The diff is `ChildcareCheck`, `WorksFinanceCheck`,
`EssentialWorksCheck`, `BusinessServicesCheck`, `AuctionCheck`, one entry of
`Observation.HARNESSES` and the generated maps. No model class is touched, so
the model and the trace do not move (`moves_playtest=no`).

## The rows

All nine FS15 rows go green and leave the file. None is a model fault, so
nothing is queued.

| row | harness, line | cause | change |
|---|---|---|---|
| 82 | Childcare: the least net present value (short of hundreds) | the harness's `weigh()` was a copy of the advice from before HW1's full-service fixed point: on HS1's path the advice said Large x5 at 2.4404, the copy Childcare Centre x7 | `weigh()` asks the advice itself for each centre alone; sections 2 and 4 stand on a founding (below) |
| 83 | EssentialWorks: covered today, short at the opening | the town has modes, so its effective load reads its streams and the `setLoad` calibration moved nothing: the premise was the played town's own traffic | the town's streams scaled to the midpoint of the two edges, each found by bisection |
| 84 | EssentialWorks: streets lose more though power has the greater missing share | the played town served .274 of its street traffic, against power's .25 set by hand: only just the right side | the streets set to serve `SERVED_STREETS` = .5 of it |
| 96 | BusinessServices: two centres is six hundred seats | the sector's own plan ordered a third centre in the 24 months | the sector held (`holdSector`) |
| 113-117 | EssentialWorks: the protected school and its four laws | whole clinics overshoot the lack: on BO1-S's path, 5,471 places for a demand of 4,052, past the 4,660 its slack allows, so there was no spare-care card | the people scaled, the workforce held, until demand is supply over half the slack |

## ChildcareCheck (row 82; the table's L290 and L425)

`founding(root, name, larges, growth)` builds the city by `buildStack` with no
months:

- five depots, and houses for twice the people;
- the people placed at the cohorts' equilibrium shares
  (`PopulationCohorts.equilibriumShare`), as many as want the founding
  capacity plus `larges` Large centres' places;
- the trend, both `BusinessInvestment`'s and `HistorySave`'s, a straight year
  grown by `growth`.

Section 2 is short of thousands (`THOUSANDS_LARGES` = 8, flat) and short of
hundreds (`HUNDREDS_LARGES` = 2.5, `GROWING` = 1.3). Section 4's player and
its full twin are short of hundreds, flat. `town()` stays (★1: AutoBuildCheck
borrows it), for section 3 and the harnesses that borrow it.

`weigh()` now calls `BuildAdvice.suggestFor` with every other template
skipped, so each centre's figure is the model's own. The results on today's
foundings:

| founding | the advice's card | per place |
|---|---|---|
| short of thousands | Large x10 | 5.9058 |
| short of hundreds | Large x4 (k 1.265) | 6.2723 |
| short of a few | Large x1 | 7.3305 |

**The fault line.** On the growing founding the old copy weighs Small x16 at a
net of 19,209.23, against the advice's x17 at 19,215.58. Row 82 was exactly
this: a weighing that is not the model's. The law still catches it, because it
compares the advice's card with each centre's own card, to the bit.

## WorksFinanceCheck (the table's sections 1-15)

Every section stands on `founded(root, name, works)`, built in this order:

1. run, reset the tax, give the wealthy cash and the ground, and hold every
   sector;
2. build the works by `buildStack`: `TOWN_WORKS` is ChildcareCheck.town's
   list at x1, `SERVED_WORKS` is BuildAdviceCheck.servedCity's;
3. place people at three quarters of the homes (`PEOPLE_PER_PLACE`);
4. strike `STRUCK_MONTHS` = 2;
5. set the people, the trend and a year of GDP again by hand
   (`NationalAccounts.seedHistory`, twelve copies of the last month).

`flat()` and `laterQuotes()` add the bond and the closed debt room that
`EssentialWorksCheck.town()` adds. Until FS15 these sections read
`EssentialWorksCheck.town` (36 months) or `servedCity` (24 months). Section
2's benchmark was already a `Debt` piece made by hand (`addLongTermBond`,
twelve months processed); it now stands on the founding.

Three premises that the played towns met by chance are now set:

- **promotedDepot:** the town keeps one standing depot of its founding's two.
  Two depots build 535 a month against a depot's 400, so they owe it less
  than half a month. One depot is under the model's builders-first
  threshold, which the next fixture asserts.
- **growingCalendarChoice, the growth:** the year grew from `GROWN_FROM` =
  .75 of today's people. It was .9 on the played city, whose builders were
  slower. The founding's Paved Road opens in 7.5 months, so at .9 the road
  had no growing plan.
- **growingCalendarChoice, the limit:** the player's limit is set to
  `DEBT_LIMIT_MOST`. Rail's 3,248 was over the default limit of 2.4 years of
  GDP, and the founding's GDP is twelve copies of one seeded month.

## EssentialWorksCheck (rows 83, 84, 113-117; the table's staff-short works)

The town stays `ChildcareCheck.town()` at x1 with the bond (★a). The premises
read off it are now set:

- **Row 83.** `mostCovered()` finds by bisection the largest multiple of the
  town's traffic (every stream alike, through `carry()`) that is covered
  today, and the largest covered at the opening. The town carries their
  midpoint. On 0.9.167 the played town, at x1, sat between x0.6495 and
  x1.0537, close to today's edge; on HS1's path it fell outside.
  - *The fault line* is the two edges themselves: past x1.0537 "covered
    today" fails, and under x0.6495 "short at the opening" fails.
- **Row 84.** The streets serve .500 of their traffic and lose 111.39; power
  serves .250 and loses 53.16.
  - *The fault line:* a share under power's .25 breaks "power has the greater
    missing share".
- **Rows 113-117.** `scalePeople()` scales every band and the two adult
  stages. The workforce is held, so the clinics' staffing does not move.
  Scaling the workforce too moves supply along with demand and never
  converges: probed, it went from 4,058/4,024 to 3,096/3,095 in five rounds.
  The result is 4,058 places for a demand of 3,775, under the 4,341 the
  slack allows, and a fixture asserts it.
  - *The fault line* is BO1-S's path: when supply is past demand times one
    plus the slack there is no card, and the first fixture says so.
- **Staff-short works.** The workforce is set to half the town's posts,
  instead of the fill the 36-month play left.

## BusinessServicesCheck (row 96)

The two Contact Centres are placed and the sector's own plan is held
(`holdSector(Sectors.BUSINESS_SERVICES)`). The 24 months now trade on the city
the section built: 600 seats, with payroll 82% of what it billed.

## AuctionCheck (the table's 7, 8, 10 and 12; 11 kept)

- **Sections 7 and 8.** `handSpare(city, HANDED_SPARE)` gives every household
  in the market's cells the same extra savings, until their spare for paper is
  at least `HANDED_SPARE` (32 of the smallest asks). This sits outside the
  audited pools, as FundCheck hands its households theirs, and section 8's
  settle audit still closes. Section 8 hands the savings again, because
  section 7's sales used them up.
  - The three `Observation.reached` calls are now hard fixtures (★3). Each
    still returns where its premise fails, as before.
  - *The fault line:* a city with no spare was the Observation's old case;
    the fixture is now red on it instead of skipped.
- **Section 10.** FC1's eight depots stay. More are stood one at a time, up to
  64, while foreign crews take orders or the order price is off its base. The
  Observation is now a hard fixture.
- **Section 12.** The 1900 city's households are handed twice the bill's face
  past their cushion, and a fixture asserts it. At month 12 the city has 2.4
  households in three market cells, and only one of those cells may buy
  paper. Its bank's room (52,355 of face) would buy the bill too, but that
  room comes from the played months.
- **The rounds.** A cell that may not buy paper takes its share of the handed
  savings and adds nothing to the spare. So each round after the first is
  sized by what the round before added. A fixed share per round only closed
  92% of the 1900 gap in eight rounds.
- **Section 11** is untouched (★4).

`AuctionCheck` leaves `Observation.HARNESSES`. ObservationCheck asserts that
the list equals the harnesses that call `Observation.reached(`. The
orchestrator was told on 2026-11-20.

## The calls (★)

- **★a. EssentialWorksCheck keeps its played town.** The table gives it only
  one CAUSE section (staff-short works). Its other sections weigh losses from
  the books and GDP, which a no-month founding lacks: WorksFinanceCheck's
  first founding failed six sections for that reason. So the rows' premises
  are set on the town instead.
- **★b. Two struck months in `founded()`.** Books, road loads and GDP need a
  traded month, and the first month barely trades (GDP 5.6). Two is a fixed
  count, not a play until a condition holds, and everything read after them
  is set again by hand.
- **★c. The advice is called, not copied** (`weigh()`). Copying the model's
  formula instead of calling it is one of the contract's red flags.
- **★d. The handed savings are outside the audit** (FundCheck's precedent).
  The settle month's audit is asserted, and it closes.
- **★e. Section 10 keeps FC1's eight depots** and stands more only while the
  builders are short, so on today's path its city is the one FC1 measured.
- **★f. BusinessServicesCheck holds the sector** instead of dropping the 24
  months, because its books' laws need traded months.
- **★g. `Observation.java`'s one entry** is outside the brief's files. It was
  changed because ObservationCheck requires it (★3).

## The pick, the review and the checks

**The pick** (`sh tools/pick.sh origin/main HEAD`): 39 of 230 harnesses for
23 files changed since bf048eb6. Most are picked because
`Observation.java` changed, which every Observation harness reads; the list
itself is read only by AllChecks and ObservationCheck. The pick is not ALL,
so there is no dry run.

**The review:** not needed, because there are no money, saves or threading
changes in the diff.

**The base**, b537278de (0.9.166), each harness run alone:

| harness | exit | time | reds |
|---|---|---|---|
| ChildcareCheck | 1 | 17 s | row 82 |
| EssentialWorksCheck | 1 | 96 s | rows 113-117 |
| WorksFinanceCheck | 0 | 101 s | |
| BusinessServicesCheck | 0 | 10 s | |
| AuctionCheck | 0 | 304 s | |

Rows 83, 84 and 96 were green there and red on HS1's path.

**The branch**, with 0.9.167 merged, each harness run alone and all of them
exit 0:

- ChildcareCheck
- WorksFinanceCheck (run before and after the merge)
- EssentialWorksCheck
- BusinessServicesCheck
- AuctionCheck: 362 assertions in 804 s. About a dozen other sessions' JVMs
  were running on the PC at the same time; the first run was 12 m 38 s, and
  section 11's 1,200 months are untouched.
- ObservationCheck: "every source with a path reading is selected for
  nightly observations" OK.

**`./batch-check.sh --fast`:** compile OK (123 s), maps regenerated, Stale 0
firm findings, the tree's 17 changed files all generated docs (committed).

**Probes** (`target/probe`, not committed): the 1900 city's bidders, the
school's scaling with and without the workforce held, the streets' and
power's losses against load, and the old `weigh()` against the advice on
both foundings.

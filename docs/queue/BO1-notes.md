# BO1 - business owners ({next}, 2026-11-10)

The batch's record. Its brief is the households study, section 3.5
(`docs/studies/spec-households.md`), with Jerus's answers of 2026-11-02 in its
section 7: one owner family per listed company, no job, living on dividends and
what it sells; a company that goes broke sends its family back into the
population; everyone else still buys shares.

## 0. Decisions as taken (kept current while the batch runs)

- **The line runs in a city by a dial, `OwnerHousehold.IN_CITIES`, shipped OFF**
  (section 1 says why: a call for Jerus). The owner cell
  is always in the cell list (so a save keys it, `OWNER`), but
  `HouseholdBalance.grantFounders` gives it the founders' shares only once Game
  has switched the line on. A bare fixture, which never does, keeps today's rule
  (the study: "With no owner household (a bare fixture), today's rule"), so the
  thirty-odd harness fixtures that list a company keep their premise.
- **The line's state is one map, company key to months at the floor**: a
  company is in it while it has a family. Saved in `DataSave`, read back on the
  load path; a save without it loads with an empty line and the founders'
  shares where they were (the study's section 4: incomplete, not wrong; no
  `SAVE_FORMAT` bump).
- **The new row is `OWNER_ROW`, after `PRISON_ROW`**, and the cell's save key
  `OWNER`: cells are saved by key, so an old save has no owner cell and loads it
  empty.
- **The sale**: the owner cell is left out of the households' three share
  loops on the exchange and posts its own sell, each month, of
  `1 - (1 - OWNER_SELL_A_YEAR)^(1/12)` of each holding at the market, under a
  new seller class `BY_OWNERS` (the life tallies appended to the save's array).
- **No offering is subscribed by the line** ("its stakes also dilute at every
  offering"); it keeps its bonds and its money abroad by the ordinary rules.
- **A company's first listing waits for its family while the owner cell is
  empty.** The census runs inside the month's simulation and
  `Game.payDividends` lists the companies after it, so the first family a city
  has is counted only at the next month's census. Until then the owner cell
  holds nobody and cannot hold a stake, so `grantFounders` reserves the family
  and refuses: `Equity.listIfUnlisted` lists nothing that month and an
  offering's founders' branch raises nothing. The next month the family is
  there and the company lists into it. This happens only while the cell is
  empty: a founding city's first listings, or after every family has left.
  The other way to do it, holding the stake aside for a month, would have had
  to carry its dividend, its lending fee, its splits and its deposits through
  every reader of the households' holdings. A later family joins a cell that
  is not empty: the grant goes straight to it and the new household, arriving
  at the next census from the population, dilutes the stake equally.
- **A family that leaves takes its money and none of the shares**: in
  `followThePeople` the owner cell's share stocks are kept whole when it
  shrinks; every other stock leaves with the leaver as today.
- **Broke** is a failed bank's resolution (`Game.resolveBank`), or a fair value
  at the exchange's floor for `OWNER_BROKE_MONTHS` running, tallied in
  `Game.tradeShares` after the exchange values the month.
- **The owners take no job and are out of the housing match.** They are out
  of `PopulationManager`'s labour force, as the prisoners are. The study had
  them seek a door like a family, but `FamilyModel`'s squeeze splits the
  unplaced between the matrix and the seekers, and a third bucket broke
  HousingCheck and ReadPathCheck. So they pay a door's rent by the default
  share and are not placed; placing them is queued with the switch-on.
- **The owner adults are saved** (`DataSave.ownerAdults`): the census's
  count, which the line at the save can differ from (a listing at the
  month's dividends adds a family, a year at the floor drops one). The first
  draft read them back off the outside adults; that rebuilt a flow from the
  state, which the rule forbids.

## 1. Why the line ships switched off (the call for Jerus)

The playtest city (`LongPlaytest.founding()`, the auto-builder, the rhythm of
AuctionCheck's founding), line on against line off, same build:

| month | people, on | people, off | senior-care pull on / off |
|---|---|---|---|
| 13 | 97 | 123 | 1.05 / 1.25 |
| 37 | 522 | 699 | 1.00 / 1.25 |
| 61 | 1,249 | 1,885 | 1.00 / 1.25 |
| 121 | 1,102 | 3,500 | (not read) |

With the line on, the city stops building at month 55 (jobs 818 to 504,
homes 1,948 for the next 65 months) and loses people until month 97. Two
channels, measured:

- **The retired lose their care.** The retired have no income of their own at
  a founding (`disposable` 0); today they pay senior care out of the
  dividends on the founders' shares they were granted. Without them,
  `careAffordability(SENIOR)` is 0.17 until month 13 and 0 from month 14
  (1.0 with the line off), so `Migration.SENIOR_CARE_PULL`'s 30% is lost
  from the target every month, and general care's affordability falls to
  0.84 (1.0 off).
- **The owners bank the dividends.** Five families take every founders'
  dividend; their plan asks the grocer for 80% of it, which two people
  cannot eat, so it is banked or sent abroad. By month 61 they hold 81% of
  the city's bank savings (2,514 of 3,091) and 2,289 abroad.
- **Letting the owners subscribe to offerings** (a variant, not shipped)
  recovers half: 1,977 people at month 121, still stalled from month 73.

The study expected this (its risks 2 and 5) and left the size to the
ensemble; the size is a city a third as large at ten years. That is not
BO1's to settle: what pays the retired's care without the founders'
dividends (pensions, HS1's flows, estates) and where a fortune spends (the
wealth study's) are other batches'. So BO1 ships the line built, tested
(OwnerCheck switches it on in its own fixtures and in a real city) and
off, the trace byte-identical; switching it on is a Pending row with this
section as its brief.

## 2. The People screen waits for the switch

Slice C (the owner families on the People screen) was not run: with the line
off the row never holds a family, so every place it would draw is empty, and
KeptCheck could not see it. The switch-on batch draws it where the prisoners
are drawn in `ui/PeopleScreen.java`: the outside tiles (`outsideTiles`, an
"Owner families" tile, its `openOutside` case to Household money), the
mosaic's outside strip, `belowTheRule`, `otherRows`, the outside table if its
columns grow cleanly, and the three texts that list the groups
(`OUTSIDE_INFO`, `BELOW_THE_RULE`, the verdict's javadoc). HS3's household
money page names the line too. The Government screen's vouchers list only
the rows paid, so the empty row shows nowhere today.

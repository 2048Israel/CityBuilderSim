# RES3 — home choice, typed rents and the 14-form catalogue

Started on batch/RES3 from shipped RES2 (7e484c4da). The dated sections below
retain the successive HC1, AP2, PW5, FC2 and later integration evidence.

## Current integration and validation, 2026-11-25

The latest integration is114528489, merging main304506de2 and shipped
PW1-R0.9.203. Main retains format/floor66; RES3 stays67. The genuine66 pair
below is from unedited shipped R6e-rest0.9.201 at78ed67315.
Substation155, RES3 homes156-166 and existing power167-171 retain their IDs;
nextId is172. The genuine unedited shipped66 save/history pair is committed,
and current SaveFileCheck refuses it with both files and the running city intact.
No conversion or current-schema restamping is used. The newest ensemble
baseline is now docs/ensemble/0.9.203.md. Results labelled8bb or75fb below
precede PW1-R and remain0.9.202 evidence. The current114 refresh verifies the
merged billing/save paths and catalogue; the default measurement is pending.
No old native result is promoted to a0.9.203 pass.

Current114/.203 nine-check refresh is complete on the gate box; the later721
medicine change is confined to KeptCheck and leaves this numeric model intact.
All full footers, empty new-red files, source/class guards, copy hashes and
owned-group end guards verify. Runtime is Temurin21.0.13+9/Linux/Monocle.

| Current check | Native/batch | Seconds |
|---|---:|---:|
| SaveFileCheck | 0/0 | 29.2 |
| ReadPathCheck | 0/0 | 22.1 |
| MonetaryCheck | 0/0 | 101.6 |
| LabourCheck | 0/0 | 11.0 |
| ExchangeRateCheck | 0/0 | 16.3 |
| DenominationCheck | 1/3 | 18.0 |
| HousingMarketCheck | 0/0 | 5.4 |
| InvestCheck | 1/3 | 10.0 |
| ReloadCheck | 0/0 | 42.1 |

Denomination has exactly7 DK1 decade lines and TP1.8195; Invest only its
HT1-H wage premise. Fresh current Monetary original-town reduction is
5.512698 points (24.47 to18.96 percent), pooled16-31 TOTAL3.635685 and
non-shelter3.102148. The original and pooled TOTAL floors stay at least1
point; values are read from current114 output, not copied from older runs.

Current14/32 report replaces docs/queue/RES3-catalogue.md, SHA256
30b039fcb1908857516c2e6360c5e68623633629e7228dc1dbc7e8f88336039b.
Box integrated months: small282 people12.599ms; present14.042ms and larger
147739 people13.675ms; historical13.778ms and larger147508 people9.830ms;
141 permanent cells. Do not compare these with laptop timings as a speed law.
All paid-build, positive held-debt, physical closure, native save and ordinary
twin/audit guards pass. Full Kept/Chart, affected follow-ups, bank and default
measurements remain pending; the earlier dated evidence below stays labelled.

### KeptCheck's section-63 heap: found and mended (RES3c, 2026-11-25)

The Codex builder's session failed at 11:13 (OpenAI's filter); RES3c
(Claude) continues the batch from 3ce757db6 and dropped its uncommitted
four-line note edit, superseded by what follows.

What held the heap. The helper RES3-keptgap's live histograms (main
304506de2 m2219, RES3 ce504d07b m2221, same fixture) put the branch +730 MB
and +95,132 Labels over main, but LabeledText (skinned labels) only +2,557:
the extra Labels were never styled, so made and dropped off a scene. The
class deltas fit FundScreen's Found rows exactly (Found 4,614 to 18,438,
Kept rows about three a row, Labels about seven). Its pool walk reached
17,841 of 153,159 Labels; main and branch both hold 3 Games, 3 UIs and one
KeptBench there, so the two towns left alive after section 96 are main's,
not RES3's. RES3c's probe (a per-choice live histogram and each bench
scene's dirty-node list read by reflection, sections 55-63, temporary and
reverted; target/res3-probe.patch, its output target/res3-probe-before-fix.txt)
found the whole spike in one choice, 63's "Search's final partial page":
Labels 24,622 to 47,318, Found 80 to 3,320, live heap 893 to 1,417 MB, and
the main scene's dirty-node array grown to 496,693 slots. Every other choice
from 55 to 63 stays at 24,000-28,600 Labels and 836-919 MB.

The mechanism. A typed search also names the lots the fund closed on the
issuer's matured bonds (FundView.search), and those grow with the months:
RES3's Real Estate, the longest issuer, has 1,107 matches at m2222 and
1,108 a month later (the choice's 3,320 Found rows are three lists of it),
so the choice presses Show more 27 times.
Each press refilled the whole list outside a draw (FundScreen.fillResults),
every row on show made new, so reaching the last page made rows in the
square of its pages; with no pulse on the bench's windowless scene they all
waited in its dirty-node list until the next release snapshot. On the box's
software renderer that ran past 2 GiB. RES3's bigger city (52,269 people
against main's 34,194) is what lengthens the issuer's list; the churn is
main's code, latent until a list is long.

★ The mend, FundScreen's Show more redraws kept (f77063213): a click, not a
key, so the rows on show are handed back and only the next SEARCH_ROWS are
made; typing still refills in place, the one exception the screen documents
(a redraw would lose the next key). FP1's design, which Jerus's brief fixed
("Show more adding as many again ... the rows are what they were, in the
order they were"), is kept: true paging or aggregating the closed lots would
change what the page shows, his call, and is not needed. The heap, the
parked-scope bound, the fixture and every law are unchanged.

The fixture, KeptCheck section 63's new part (searchShowMoreBounded): the
longest issuer's search past (SHOW_MORE_PRESSES + 1) x SEARCH_ROWS; each of
two presses hands back every row on show as the same nodes and adds only
SEARCH_ROWS; then SHOW_MORE_MONTHS months each show the presses' 120 rows,
the same nodes, while the search grows (1,107 to 1,108), equal to a page
built new. On the old code its identity law cannot pass (not run there):
each press refilled outside a draw, where Kept makes every node new.
Laptop KeptCheck on f77063213, no other heavy JVM: ALL OK, no
FAIL line, live heap after release 1,288.9 MiB of 2,048 (law: under
three quarters). The merge of main 0.9.204 (WG3, BX1, 00a5dcc72) needed
only the generated docs regenerated; the fast check is green on it.
The named harnesses on 00a5dcc72, laptop, one at a time: SaveFileCheck,
ReadPathCheck, MonetaryCheck, LabourCheck, ExchangeRateCheck,
HousingMarketCheck and ReloadCheck green; DenominationCheck and
InvestCheck known red only (their HT1-H rows, unchanged). KeptCheck on
the merge (WG3 changed ui/PolicyScreen), on the gate box between gates
with a gate's Monocle options, where it had run out: ALL OK, no FAIL line,
section 63's new part OK (Real Estate 1,107 matches; 120 of 1,107, 1,108
and 1,108 over the three months), live heap after release 1,426.3 MiB,
the objects 1,421.7 MiB under the law's 1,536.

### REV-RES3z and the merge of main 0.9.206 (RES3c, 2026-11-25)

REV-RES3z (on 7209dddb9): SHIP, no P1, three to make, done in 4ae0e7446.
HouseholdBalance.creditDividend and creditLendingFee skipped cells under
EMPTY_CELL, which keep their shares since 0894946a7, so Equity booked their
dividend abroad and Exchange paid them no fee; both now test
!(households > 0), as 3d2cb76de's coupons do. HousingMarketCheck's
fractionalDividends pays a .25 cell and one just under half at home, with
conservation; on the old lines it fails ten parts (run). KeptCheck's
Show more fixture now asserts the issuer's search grew over its months
(1,001 to 1,003 on the box). GameVersion's floor history gains 67.

The merge (d0477aa17). RES3 had ported R1g's home pay walk from an early
batch/R1g and saved the sites' raw abroad claims itself; R1g shipped both on
main under its own names. Main's are kept (DataSave.setHomePay,
abroadClaimById, BuildingManager.getAbroadClaimById and its restore) and
RES3's duplicates dropped; with a saved map both restores leave an unnamed
site at zero (restoreAbroadOwed), so RES3's late re-application over the
final rebuild reads main's key unchanged in effect. BuildingsTemplate takes
main's identical founding-cost members. CreditCrunchCheck keeps RES3's
one-door cut, built for its catalogue. SaveFileCheck's format-66 fixture
(0.9.201, before R1g) and its README name main's key.

R1g makes the 1900 city the playtest's and probeCity()'s founding. Measured
on 4ae0e7446, laptop: HousingMarketCheck's door-census fixture, whose
subject is today's city (Studio Apartments, a depot), found no arrivals in
the 1900 city and now founds probeTodayCity(), R1g's own pattern (KeptCheck,
GroundSkipCheck): green, 17.5 arrivals. WorldMoneysCheck, HouseholdCheck,
ExchangeCheck, SaveFileCheck and ReadPathCheck green. KeptCheck on the box:
known red only (main's HT1-H paper row).

★ Found, not yet mended: the 1900 city meets RES3's housing.
CreditCrunchCheck (its city LongPlaytest.played(), now 1900) has five reds;
with -Dplaytest.founded=today it has one (the cut fixture, below). Helper
RES3-noi measured them (driver and output in target/res3-noi):
- Section 4: the lender reads a 1900 Low-Rise block's rent at -0.088 a
  month (today +2.077), rent 1.088 less carry 1.176. The class ask is
  capital x LANDLORD_YIELD 0.058 / 12 with capital the door price, and the
  door charges only materials beyond the builders' yard (Game.quoteBuild):
  R1g's dated block, 141-150 materials, sits inside the yard, so the door is
  141 against a market base near 233. RES3's housingCarry values repairs
  and property tax on market materials and adds payroll (jobs x wages, not
  dated) and utilities, so it runs 5.6-5.8% of the door a year in 1900,
  2.6-2.9% today.
- Section 3: households with no home climb from 0 at m151 to 126.5 at m235,
  each month equal to the market's unaffordable count; they are
  UNEMPLOYED:OFF_EI cells with a zero budget (64 to 188 of 385 households)
  while 79 of 166 D90 doors stand empty. RES3 shelters a zero budget only
  with a funded host (free host places); 1900 has too few. Not the lender.
- In today's city the one red is RES3's own cut fixture: no single door was
  retired before the room reached MARGIN, so a one-door trim's room is
  unmeasured (Infinity).
Village1900Check's sections 2-3 (the stalled village, R1g's switches off)
lose their premises: rent 0.202 of take-home (main 0.717), pull 1.000,
jobs and homes support 323 for 306, diploma posts 45 for 41.1 (main 141
for 34.7); its fixes (5-10) pass, the home village 570 at m60 (main 678).
Mailed to the orchestrator as a block with the proposed mend for section 4
(the ask's capital on the carry's own base); section 3 is a design call.

The orchestrator's calls (2026-11-25) and what they did, on the jobs trace
(target/res3-jobs: CreditCrunchCheck's played city replayed, seed 13; main
0.9.206 beside it):
- Mend 1 (4f690edfc): the ask's capital is the carry's own base, cash plus
  materials at market plus land (EconomyManager.housingStructureValue and
  housingLandValue). The lender reads a block's rent at +0.222.
- Mend 2a (83da694bb, OutsideCheck's fixture 1e56fcb1e): RES3 did raise the
  unemployment. The typed market bills no rent to a household with no paid
  door, so main's eviction test (rentDue > 0, "holds a home") never struck;
  OFF_EI reached 250 at m270 (main 8.9), the rate 27% (main 12.5%). Struck on
  the unfunded test alone in typed mode: mean rate from m60 15.3% (main
  19.5%), OFF_EI 11.6, unhoused 41.4.
- Mend 2b (383c1f74a): lodging, as decided: who funds no half share of a
  standing home lodges with the housed beyond two to a share, up to the
  door's people, at no bill (HousingMarket.lodging()); a lodger keeps no
  prior claim on the doubling's room (retainHosted), which otherwise
  displaced guests who could pay. The m253 EI cohort's 252 households with
  no home clear. But the doubling already fills occupied doors past built
  capacity (H120 6.6 people a door of 6, D90 5.1 of 4), so at capacity 11
  months still leave 6-29 with no home beside vacant doors.
- Clearing paid shares before free host places (tried, reverted): one seed
  moved to a 105.8 peak; the room, not the order, binds.
- Two persons a room, rooms = bedrooms + 1 (the 1891 census of England and
  Wales' overcrowding line), traced uncommitted: one month over the watch,
  m152, 13.4 (EI families with a child priced out of their door the month
  their budget fell). Real CreditCrunchCheck on it: 3 FAILs (was 6), the
  lender passes 2 of 6; the cut fixture's House is unknown in 1900 (mended
  to the city's own single door, uncommitted); the other two read m152.
  Mailed: the people standard, and tenure (an incumbent not yet evictable
  keeps its door, main's rule), are the orchestrator's call.

Decided by the orchestrator, 2026-11-25 16:33 (tell-at-ship, no question):
(a) the unemployment mend stands; (b) lodging's standard is the 1891 census
overcrowding line, more than two persons a room, rooms = bedrooms + 1 (the
census of England and Wales 1891, used to 1931; H55 4, D90 6, H120 8), and
the doubling's two to a share is held to the same line, so no occupied door
exceeds it; (c) tenure: a sitting household not yet evictable keeps its door,
its unpaid rent booked as main books it (Jerus 2026-09-11); (d) Migration
reads the asked burden (the cheapest suitable vacant door's ask over
take-home), Village1900Check's premise 1 restated to it; today's twin moving
927 to 903 people is the honest effect. Then the named harnesses, R1g's
no-home watch and CreditCrunchCheck green, the ensemble against 0.9.206.md,
'RES3 dry run?'; no further review unless the ensemble moves a money or
saves figure. Village trace: target/res3-village (helper's probes).

What the calls did (RES3c, 2026-11-25; probes in target/res3-c):
- (b) HousingChoice.PERSONS_PER_ROOM 2, line(bedrooms) = 2(bedrooms + 1).
  An offer carries its people: a whole door its line, a host place the line
  less its host (host places keyed by the host's size), a paid half the line
  over two. No occupied door passes it: alone, two to a share, a guest, a
  lodger (lodgers spread evenly over the class's room). HousingMarketCheck's
  fixtures caused again on the line (four to a household on the H120, an H55
  couple and its guest couple, the family reservation on two H120 and two
  H55), each result asserted within the line.
- (c) Tenure. A cell not yet evictable (HousingMarket.sitting()) keeps its
  full and half shares whatever its budget, the bill due and booked unfunded
  (the landlord's arrears), within the census line rather than the built
  capacity. And the doors follow the people: a lease is a cell's, so a
  household that changed cell (a child born, a job lost, a pension drawn)
  left its door behind. CreditCrunchCheck's no-home months were working
  LARGE_FAMILY:UNSKILLED households at a zero budget (6-8 of 19 at m152).
  HouseholdBalance.housingRoute() routes the month's cell changes as the
  money goes (the flows' moves, then the census's net change by
  SecurityRoute's rule, stock group first); HousingMarket.carry() moves each
  mover's share of its old cell's leases before the clearing. The crunch
  now leaves no household with no home in any month (was 12.57 at m155).
- (d) Migration reads HousingMarket.askedBurden(): each household's cheapest
  suitable standing class's ask over take-home, read from the saved classes
  (a load keeps no offers; the first try read the clearing's and a reloaded
  twin diverged). Village1900Check sections 2 and 5 read it ("ask").
- Found by CreditCrunchCheck, mended: (1) the cut fixture reads one house's
  room on the single door it stands; tenure left the m150 cut short of one
  (room 13.22, MARGIN 12). (2) The lender reads each size the halving tries
  at its own rent roll (Mortgage.decide's noiOf, Game.lenderReads()). The
  typed rent of an order past its demand falls a unit: the m270 order of 6,
  demand for 4.6 blocks, read at its average 0.822 a block passed only the
  till's one where 3 let at 1.073 each. Main's flat rent decides the same
  (MortgageCheck 4). A lending path: the ensemble reads it.
- Found by MonetaryCheck's home-fuel rows (the helper's bisection: 83da694bb
  green, 383c1f74a red), mended in 3b8b387be: completePaidExcess counted the
  households left after paired halves as households - n, which with every
  half paired is the doors exactly and in floating point came out one step
  under them. The 1900 town's month 26 threw an Invalid lease, the month
  stopped before HomeFuel.strike, and Game.simulateMonths swallowed it. The
  households are now the new funding's, never under the doors;
  HousingChoiceCheck causes the edge with that lease's figures (red on the
  old line). HousingDoorCheck's cash identity, open in 4 of 899 months
  before (worst 2.4%), closes in all of them on it.
- InvestCheck's two fixtures, caused again on mend 1 (9fe3eb834; bisected:
  4ae0e7446 green, 4f690edfc red, the four switches each still red). The
  housed town's best home is now a Duplex, one kind of door, whose need is
  its income's knee (641 at 0.0776 a month against a hurdle of 0.0486), so
  the hurdle never cuts inside its need. The need-cut best homes are priced
  out of first place as section 2 prices out the House, until the best is a
  form of several kinds (Mixed walk-up), and the bracket claim reads it. A
  month's output is whole points, so 95% of the Duplex's 86 left a year's
  room a step high: the crews are thinned on until it is under (74).

The orchestrator's calls, 2026-11-25 19:24 (mine, from reality; tell-at-ship):
(1) the lender: keep the slice mend; a lender underwrites each phase on its
own rent roll. (2) Village1900Check 2, 4, 5: a helper shows what lets the
stalled twin's landlords build; if real (shares and lodgers make a door
pay, as 1900's tenements were built to), restate each premise to that law,
keep what holds, assert the real effect and cite it; if an artefact, mend
it. (3) A household that fits two to a share weighs the cheaper of a door's
ask and a paid half share's (half the ask); only guests pay nothing.
(4) SaveFileCheck AW2: after (3), if its shop still cannot be staffed,
cause its spare workers again (more closures), never by relaxing it. Then
the named harnesses and the ensemble against the newest baseline in
docs/ensemble/ (FB11.md, or 0.9.207.md once it ships).

What they did (RES3c, 2026-11-25):
- (3) HousingMarket.askedBurden(): a household that halves a standing door
  (HousingChoice.halves(), two to a share within the census line) weighs half
  its ask; HousingMarketCheck's doubled doors assert the half share's ask.
  The door census's press month drew more seekers the month before (829 where
  772), they took the finished doors, and the press month met the room cap
  (11.17 of 11.17) before the crowding ramp held it: the fixture migrates 450,
  not 500 (room 41, arrivals 12.8), the scarcity and floor premises caused.
- (4) SaveFileCheck AW2, still 0% staffed after (3): RES3's town keeps its
  unskilled hands busy, the shop's two unskilled posts finding -270 spare
  after the schools close. The fixture also closes the town's bakeries
  (180 unskilled posts: -14 spare, 60%) and home care service (44 more: +55,
  the shop staffed in full). The staffing gate and its 80% stand.
- (2) Village1900Check, an artefact, mended (808421280). The slice lender
  passed 67 units in the stalled twin from m9 to m53 where the flat one
  would have passed 25 (m40-43: a House slice read 0.054 a unit against the
  whole order's 0.036, carry 0.027), and all of the rent it read was whole
  doors let at the class ask to the forecast's "#incoming" cohorts. None was
  half shares or lodgers. The forecast sized those arrivals on posts over
  workers (m40: 552 posts, 203 withheld, for 231 workers, so 1.39 times its
  working households, about 479 heads). Neither the affordability pull nor
  Migration's target bounded it (577 for 533 at m60), and the twin grew 112
  from m40 to m60. Its 28 new Houses let 4.1 doors; half shares moved -0.03.
  The tenants a landlord and its lender count on are the ones the town draws:
  HousingMarket.forecast() bounds the new heads by Migration's last target
  over the people here (once a month's draw is read). The twin, 533 people
  at 6b326cb22, is 453 on it; s4 and s5 are green.
  Helper's table at m60 (people, asked burden, bare draw over people):
  flat lender, full ask 330, 0.552, 2.004; slice, full ask 396, 0.506,
  1.831; flat, half ask 431, 0.452, 1.555; slice, half ask 533, 0.391,
  1.401. The mend: 453, 0.436, 1.50.
  The two section-2 reds left on it are real, restated (d3ee0f2a8): the
  half-share ask (call 3) takes the whole DEAR_RENT red, and with the slice
  lender (call 1) the bare draw falls under 2.0 even at the full ask (1.972).
  The village doubles up as 1900's households did: 24.3 of its 190.7
  households pay half shares (today's twin 0.4 of 481.3), 22.5 of them on
  doors its landlords built (the Duplex's 10.5 of 38 doors at 0.0200 a share
  against half its ask, 0.0205). 95% of the movers weigh a half share. The
  1901 census of England and Wales counted 7.04 million families or separate
  occupiers in 6.26 million inhabited houses. Restated: a household alone
  is asked over DEAR_RENT times the target (HousingMarket.aloneBurden():
  0.827; today's twin 0.359, under). The village's households pay half
  shares in a larger share than today's twin's, and a mover there weighs a
  half share's ask, under a household alone's (0.436). The rent binds as the
  pull strikes more off the bare draw than the room it leaves the village
  (197 struck, 29 left; was "the bare draw over UNPULLED, 2, times its
  people", now 1.50). Kept as they were: the pull cuts the draw, the room
  under today's twin's, s4's draw share, s5's home.
- The lender's readers, run on it. OrderSearchCheck's countdown read the
  whole order's rent at every count, so 29 of 168 mortgages to m500 differed
  (m15: an order of 53, the game lends 1 at a coverage of 1.743, the count
  reads 0.791). Read per slice, all 168 match. The watch hook now hands
  noiOf on (OrderWatch.mortgaged's default overload; f4ae23aa4).
  OrderSearchCheck's copy failed to load: ScaleCheck.scale multiplied a
  lease's count, bill and households but not its funding (d82144d00), so
  the scaled lease funded a thousandth of its households. The scaler now
  scales full, half and sponsored. BuildCardCheck's town, since 4f690edfc:
  600 Houses held 1,784 people and none out of work, so every card failed
  STAFFING first (Rail Spur 26% staffable now). 800 hold 2,107 and 71
  (cece71550), and the cards read LOSS, no feed and INTEREST again.
- MonetaryCheck, 5 reds on 6b326cb22, 1 on 808421280: the non-shelter
  basket's mean low-to-high effect of a point, RES3's own claim (REV-RES3x
  P1, 563b5f17c). Bisected over paths 16-31: 4ae0e7446 3.107, 4f690edfc
  2.713, 83da694bb 1.617, bbb290e6b 2.803, 0f710131e 2.790, 6b326cb22 0.139,
  808421280 0.160 (median 0.71, 14 of 16 positive). The basket is 86%
  groceries at a shelf floor; the expected-inflation gap between 3% and 40%
  fell from 3.40 points to 1.06 as RES3's rents stopped leading inflation.
  At 40% a Convenience Store closes (m66-72), and two paths meet grocery
  shortages at full employment (-4.77, -3.00). No line to mend found; to the
  orchestrator.
- HousingDoorCheck: MC1 is main's known red (the 1900-founded replay's bank
  leverage never binds). The no-door watch went 6.918 at m490 (3b8b387be),
  253.8 with the half ask and no bound (6b326cb22's model), 47.986 at m888
  on the bound. All are sitters, families of six. Every door of every
  class is full and the ground is gone ("needs 743 m2, 13.1 free"), and no
  line takes a second six. Arrivals slow to about 0.3 of their rate but
  never stop. Reading the room under the census line (a second household
  only where halves() holds) made the city bigger and the watch worse
  (114.6 at m897), and broke CreditCrunchCheck's fixtures: not applied.
  Green founded today's way. To the orchestrator.

The orchestrator's calls, 2026-11-25 22:42 (from reality, tell-at-ship):
(A) A family past the line lives overcrowded, as the 1891 and 1901
censuses counted it (more than two a room is overcrowded, not homeless;
7.04 million families or separate occupiers in 6.26 million inhabited
houses in 1901), never doorless. It takes the nearest door or share at the
over-line burden and is counted overcrowded. HousingDoorCheck's no-door
watch stays, with the real effect asserted beside it. Not a hard gate on
migration, which answers housing through cost and its pull. A town out of
ground builds denser: its own batch, DN1, queued in the ship file with
m888's figures. (B) MonetaryCheck takes the helper's restatement:
Jerus's 11-23 answer ("even reality higher interest rates doesnt always
equal lower inflation") is the law, and the one-point mean was a
reviewer's line.

- ★ (B), 33614cba8. Reviewer's line (REV-RES3x P1, 563b5f17c): "the
  highest rate lowers common-basket non-shelter inflation across the
  declared towns by at least a point". Restated: expected inflation falls
  with the rate in every town (16 of 16, the least 0.7948 points); the
  non-shelter effect is positive in the median town (+0.7149) and in at
  least 14 of the 16 (14); each contrary town's grocery shortage is named
  in the months that carry it, and its effect turns positive with the
  groceries at the shelf's floor (town 21: -4.7655, short at 40% in m81-84,
  +0.2490 at the floor; town 29: -2.9959, m82-84, +0.9528).
- ScaleCheck, once the copy loaded (the lease funding above): the scaler
  left the bank's held books (9d2d98699) and the month's wage totals
  (18c8dd4b9) at one city's, and multiplied the water tariff (ca9bb9339), a
  price, K times. The 1000x copy's deposits read a K-th of the city's at
  month 0 (0.266 against 265.99), its wage bill a K-th (the affordability
  pull parting by 1.0), and its sectors billed K squared water (1.44e6 a
  city against 1,437 in month 1, the bank in resolution). All three are
  mended in the scaler (fa8a16692); ScaleCheck is green, the two
  known-reds lines included.
- OrderSearchCheck 2, the 1000x copy deciding no orders: a fourth scaler
  gap. RES3's month-500 city has one paving under way, and the scaler left
  constructionControl.paving's roads and ahead at 1 while the Paved Roads on
  site were K times over, so the footprint took one road's 250,000 sq ft
  off where it had K: the copy's buildings stood on 41,660,350 sq ft a city
  against the 41,540,083 it owned, and every sector held on "no land".
  Mended (dd98bfcec): the copy judges 19 orders on their interest and 7 on
  a mortgage, its twin 17 and 6; OrderSearchCheck green. The bank's payroll
  parting (3.98 against 21.08 a city) is the whole-branch scale-dependence
  SK1 and ST3 already note, not this. Left: constructionControl.abroad's
  amounts stay at 1x; no check reads it.
- (A), fda75569e. HousingMarket.overcrowding(), the clearing's last pass:
  who the census line leaves with no door takes the nearest door still
  standing at its ask, then half of one at half of it, past the line, by
  the placement's own score; who funds neither lodges beside an occupied
  door, no bill and no new money. It is counted overcrowded by cell, saved
  with the market's state (an older save has none; ScaleCheck's scaler
  multiplies it, 6ff01223b), still seeks a door of its own in the room the
  forecast reads, and still presses the rent. Game.getHouseholdsWantingAHome()
  (no door plus overcrowded) is what LongPlaytest's player, CityNeeds' HOMES
  and the fields' guard now read. HousingDoorCheck: no household doorless
  in the window, and the real effect asserted from the model's readings:
  376 months building behind the households (the landlords' order trimmed
  or held, at the door or for land) average 10.688 households overcrowded,
  the most 77.426 (m893); the 94 months it keeps up average 3.454; the 222
  held for land 11.236. The m831 boom has 36.993 overcrowded (222 people)
  with 389 Houses trimmed to 14 for the down payment. For the orchestrator:
  overcrowding goes with building falling behind, not with the ground
  running out as such; the land-held months are about every behind month's
  figure, so the assertion is "in the months building falls behind, more
  than when it keeps up". A first cut tripped the households' cash identity
  at m838 (a vacancy taken whole summed D90 to 1,988.0000000000002 of
  1,988 and the inventory refused it mid-month); the pass now holds each
  home's doors to the standing stock, and all 899 months close.
  HousingMarketCheck :693 and :1077-1080 restated to the law (placed past
  the line, counted, a lodger billed nothing; priority and the fractional
  shortage kept). CreditCrunchCheck's three reds, caused again: the House
  refused for no land at m150 (279.6 sq ft free) now has its plots bought
  first, which also gives the cut's MARGIN room (2.0); and the crunch's
  hold adds the bank's profit tax, drawn at the month's top before the
  lending rule is set (37.76 at m267 where the hold left 33.49), at
  Bank.taxAt's own rate. Green: HousingMarketCheck, HousingChoiceCheck,
  CreditCrunchCheck, Village1900Check, SaveFileCheck, ReloadCheck,
  ScaleCheck; HousingDoorCheck known red only (MC1's capital fixture).
- (A) on the merge of main 0.9.208 (ca10c6aae): the comparison by the
  month's order turned over. Behind 344 months at 0.192 a month (most
  35.104, m831), kept up 126 months at 2.065 (most 52.100 at m830, the
  month before the trimmed order); held for land 0.014. A month's order is
  not its stock of crowding, so the order and land tallies are printed and
  the assertion is the cause the censuses read (9f7f0316e): over the 337
  months the households seeking a home grew by more than the homes
  standing, overcrowding rose 55.254 households (0.1640 a month); over the
  132 the homes caught up, it fell 55.254 (-0.4186 a month). Asserted:
  overcrowding counted and none doorless; it rises over the months the
  households outgrow the homes, by more a month than over the months the
  homes catch up. HousingDoorCheck known red only (MC1); OrderSearchCheck
  and SaveFileCheck green on the merge.
- The ensemble on the merge (RES3-m208-20261126, ca10c6aae, against
  0.9.208.md): M1 and F1 FAIL in AUTO2 (45 audit months, 11 of 16 runs
  with findings), ZERO_SLACK (44), SHOCK (14) and HIGH (2); ZERO_HOT green;
  inflation (A1 2.69%, 14/16 in band) and the final population (8.39
  million against 6.24 million) inside the baseline's. Every failing run had
  one month where food, both utilities' revenue and the central bank
  disagreed at once and the audit failed the month after: a month that
  threw partway. The throw was call A's own: overcrowding()'s trim to the
  standing stock kept a lease's households and cut its funding, so at AUTO2
  seed 6's m568 a lease of 2.73e-12 households of D90 was short 9.1e-13 of
  its funding and threw "Invalid lease". Mended (c7adb1caf): who the trim
  takes off a door lodges on it, sponsored, no bill, counted hosted and
  crowded. AUTO2-0's finding "households with no home" from m1950 reads
  getStillUnplaced() > 1, the doorless only: traced with the rerun.

The passive default observer's counting fix, 4a5f516cb, requires 600 completed
post-audit steps and audit rows, clock 1 to 601. Its measurement window remains
240–600 inclusive, with 361 observations. Native findings, audits, purity and
output-error checks remain. The observer has not run; earlier unexecuted
payloads are superseded. This changes the diagnostic driver, not the model.

Fresh merged8bb03c69f results: SaveFileCheck native/canonical0 in123 seconds,
ReadPathCheck native/canonical0 in105 seconds, and MonetaryCheck
native/canonical0. Its pooled16-31 TOTAL reduction is3.635685 percentage
points and non-shelter3.102148; both original-town and pooled TOTAL floors
remain at least1 point. Original, supply, nested, save/reform and purity
assertions all pass. The remaining affected checks and default observation
are still pending; these three results are current-source evidence.

The first merged group completes with HousingMarketCheck0 in18.8 seconds
and ReloadCheck0 in199.2 seconds, including all1229 reload comparisons.
Current8bb native14/32 output replaces docs/queue/RES3-catalogue.md. Integrated
month profiles on Oracle21.0.4: small282 people56.726ms; present59.866ms and
larger147739 people63.842ms; historical42.515ms and larger147508 people44.926ms.
The larger city retains141 permanent cells. These are individual ordinary
integrated months, not isolated solver times or a hard timing benchmark.

The final ensemble remains after the combined review, against the newest
baseline (currently0.9.203). Use a fresh output directory for this final model:
earlier RES3 attempts retain their first class snapshot and cannot be resumed
as proof of the changed model. The label and committed baseline remain RES3.

The money/saves delta for review includes the actual funded household bid,
the saved like-for-like class rent index, and actual consumer observations
for the nine-component reference basket. The0.058 gross return is compared
with actual carry rather than added to it; builder ranking uses observed net
operating returns and the feasible funded backlog. These are model changes,
not altered policy-effect bounds. Every positive fractional financial owner
settles its positions; optional-action thresholds retain their existing rule.

The exact-load mends save held Bank books, water tariff, employer/payroll
vectors, wage anchors, index relatives and raw physical abroad-claim weights
through DataSave and the late rebuild. Nominal amounts scale; physical raw
claims and relative-price coordinates retain their own units. The genuine
shipped66 save/history pair is refused byte-intact under67, while current
held-state and next-month twins pass. Existing CPI readers are adapted to the
nine-component model. The default-null
passive observation hook is diagnostic only, with native finding/audit/output
failures preserved; it does not claim a completed full playtest or ensemble.

Further current8bb native/canonical0 results are Labour, ExchangeRate,
HousingDoor (899 played-month household identities and no open audit),
Household, BuildingData, PowerPlan (35 reserve and1 spread decisions caused),
PowerDispatch, Hydro, WaterPower (41), BuildAdvice, BuildCard, WorksFinance,
EssentialWorks, Research (249), PublicLicence (68) and LicenceFees (45).
Denomination is native1/batch3 for exactly7 DK1 decade labels and1 HT1-H
TP1 premise; Invest1/3 for its single HT1-H wage premise; AutoBuild1/3 for
main's single FS12 care-fee premise. No new red occurs in these runs.
Kept, the complete box Bank/Central/YoungBank query and default measurement
remain pending at this snapshot. The older results below retain their labels.

Kept subsequently reports a NEW reset-law failure at section48: the card's
own reset does not make its next kept draw equal a page built new. No current
approval covers it. Keep the native1740-second bound and let the full run
finish; the helper diagnoses actual state/node differences against pushed
source. All box/default conditional starts are suspended. Do not classify the
failure as a moved premise merely because UI files are unchanged, and do not
weaken the reset equality or add an unproved waiver. No model patch is made
before that diagnosis and source review.

The first bounded source finding (9084) narrows the composite: its final
fresh-page signature returned null, so reset lookup or immediate barGone is
false. Existing output does not distinguish those flags. KeptBench's button
helper can report a found disabled button and its bar-label helper searches
hidden descendants too. These are candidates, not a demonstrated cause.
The four involved files match main byte-for-byte, which alone proves nothing
about whether the changed model exposed a product fault or absent condition.

Review and add39 read-only diagnostic lines in the harness and its KeptBench
helper: actual count/order/card identity, reset enabled/action/ancestor state,
matching-label ancestor visibility and the real orderBar identity before the
press, after it and after the next draw. Preserve every input, month, action,
draw and predicate. This is instrumentation, not a mend or acceptance pass.
Current native still finishes on its original8bb classes; no overlapping JVM
or source edit in that running worktree is authorized.

The next native reaches section75 and throws: basketParts reads colour5
from the five-entry BASKET_RAMP while the real PriceIndex has nine components.
The component names already have nine entries; the missing colours cause the
crash even for a zero-weight new component. This is an owned reader regression,
not a missing premise. Adapt the existing basket reader to all nine components
and inventory its other readers; keep every weight and history comparison.
The fatal main leaves only JavaFX threads alive. Parent authorizes stopping
only that verified owned tree if no meaningful test continuation remains,
recorded as cancelled-after-fatal with unknown numeric exit, never a pass.

The bounded reset replay preserves the entire original played-city setup and
sections1-45, including the failed reset assertion. A temporary explicit
diagnostic flag stops ONLY after that assertion, prints a nonacceptance marker
and exits77. Normal full Kept remains unchanged. This saves replaying later
sections before the actual reset flags are known; it cannot supply acceptance
evidence. Remove the stop branch before the final full run. Source-only patch
96c750ce53d7c74733e55ae85d27bade6684da2f7b1b6298e94bd8865c32f41c is reviewed
and applied; no native starts until the earlier owned jobs end and fresh gates pass.

The PolicyScreen compatibility patch29603823bd604a1b5bcf69a12df7b4cb0046651c72be29540540d5e5cd30afbf
preserves the first five colours and adds four existing palette colours for
new cars, petrol, lamp oil and home coal in authoritative PriceIndex order.
It removes stale five-part link wording. Every weight, name, price, history
read and loop remains; no truncation, padding or numerical model change.
HistoryScreen's index/weight trace catalogue and component preset also omit
the four new parts; prepare the same narrow reader compatibility repair,
preserving old trace keys. These are required reader mends, not a new UI flow.

The reviewed HistoryScreen patch4b8b6dbd968b79c8f2a5127ea96ba4f6b3c1b89dcc46b82e524fe4dbbe9c0aa3
wraps the existing completed trace catalogue, preserving every old object,
key and order. It appends missing index/weight entries from authoritative
HistorySave keys and PriceIndex names, and extends the existing basket preset
after its unchanged price-index/old-five prefix. The units match the old
entries (index and percent). No duplicate keys, new preset, saved field or
numeric model change is introduced. Render/acceptance proof remains pending.

The GB5 reset diagnostic completes in67.8 seconds with actual native77 and
canonical4, its explicit nonacceptance marker and all original prefix/reset
checks. Runtime is Linux Temurin21.0.13+9/Monocle/Pango, distinct from the
original Oracle Windows run. Exact668 source, both data hashes, fork/record,
JDK/class manifests, resource gates, complete raw snapshots and all endguards
are retained; all three copied archives verify. This is not full acceptance.

Actual flags identify the lost condition: before the month, the first card
is Small Childcare Centre and its held order is2. Afterwards, the live first
card is Walk-in Clinic with count0, while the childcare order remains2.
No reset node exists on that current card. The press reports found=false,
barGone=true and the subsequent fresh signature is null. Thus the failed
conjunct is reset lookup, not the page equality. The actual owner bar is
attached before the press; its detached pointer after the later fresh
comparison does not explain this failure and is not changed speculatively.

★ Cause the intended reset condition through the normal service-ring choice:
retain the same measure across the month, assert the same first building and
a strictly positive held order before reset, then keep every original step,
month, reset, bar and fresh-page assertion. Unpicked Build draws intentionally
choose the current worst measure, which changes here. No product reset mend
or known-red waiver is justified by these flags. Original Oracle individual
flags were not logged, so claim no hidden Oracle value; the final full run
must prove the properly caused fixture. Remove all48 temporary diagnostic
lines before that acceptance run. Bank/default starts remain held.

The reviewed f970be9102827b5e79da7c6bbc6a50daf692459be2d4fecfc4af381e43d8ce00
proposal is now applied: normal buildRing selects Childcare before the first
draw, and a new strict finding asserts the same first building, positive held
UI quantity and matching shown quantity immediately before reset. The original
small/current-large step, ordinary month, reset/bar/fresh comparisons remain.
All48 temporary diagnostic lines are removed; KeptBench returns exactly to
its8bb source. The final run cannot stop after section45. The numeric model,
saved fields and format remain unchanged. Full Kept/reader proof is still owed.

Current combined75fb --fast exits0 (compile/maps/Stale), retained at
target/RES3-root-fast-75fb04d80-20261125-060856. Fresh MG1:83-percent committed,
4.248 GiB available RAM,9.242 GiB free commit and mean29.845-percent CPU from
three two-second samples; pinned Oracle21.0.4/absolute C:/ assets/parallel1.
Controller88265 ends0, no owned jobs remain. Regenerate the maps for the
actual reset fixture and both CPI readers. Full Kept/Chart and subsequent
bank/default evidence remain pending; no economic-model source changed.

The complete75fb GB5 Kept run finishes normally in297.5 seconds, native2 and
canonical4 with footer2FAILED. The new same-Childcare/held2/shown2 finding and
the original reset/bar/fresh assertion pass; the nine-component Policy reader
renders without the former crash and all sections complete. One PAPER1-of-6
failure matches main's HT1-H record. The medicine-reach sentence is NEW and
unapproved; no FS5 waiver is used. All remote groups/controller end normally,
all three copied archive hashes verify. This proves the two repairs on their
named0.9.202 source, not a full pass. Hold Chart, bank and default runs while
the helper diagnoses and causes the medicine fixture's intended condition.

PW1-R ships0.9.203 during this run. Merge114528489 brings main304506de2
cleanly into source; only12 generated documents conflict and take main's
copies pending regeneration. Power bills now use the struck season; planners
read annual-mean water prices while bills use the season, and the load path
restores that season. RES3's held water tariff and billed-once homes remain.
Main still has format66, so67 and the genuine0.9.201 format66 pair remain
correct. The newest baseline is0.9.203. The earlier source-labelled model
checks and14/32 report require affected integrated refresh before review.

Root114 --fast does not start: fresh MG1 has3.750 GiB available RAM,
8.532 GiB free commit and25.868-percent CPU. It is a refused launch, not a
compile or test failure. Retain target/RES3-root-fast-114528489-20261125-062558.
Request box compile/maps and the source-driven affected checks rather than
waiting for laptop memory; no local JVM is left active and no guard is waived.

The parent-reviewed box114 --fast then completes0 at
~/cbs/runs/res3-integrated67-fast-114-gb5 (root payload
eb2e60fc1f4e125fc3400f502dbd6edfb8900e55c62f2815eb313476a9b594f2).
Helper0a1f verifies explicit compile0, cached batch compile0, maps regenerated,
Stale0 firm/206 soft, source guard empty and class manifests identical before
and after the batch. No game harness ran. Fresh compile gate:27.828 GiB RAM,
21.912 GiB free commit,0.428-percent CPU; batch gate27.807/21.911/0.0 percent.
Runtime is box Temurin21.0.13+9/Monocle, not local Oracle21.0.4.

All registered groups/controller end0/empty; all three archives copy and verify.
Official record a723bc07de05f854d07a85db6ac2df04e60f719c6db51e199c87b640a8f3dab6
is reproduced from actual114 metadata. A helper's preliminary0296 record used
an old registry and is obsolete/unexecuted; no native or approval used it.
Transfer only the12 generated paths into this worktree from verified archive
7e6eb44d25d752d565609d68476cc5651239fc7d0aee9b1ca83ecaec6477dff6,
preserving notes/publisher records. The actual copied source/checks/classes and
full logs remain at helper target/integrated67/helperfast-114/remote. Current
model money/save/catalogue and default results still await their native runs.

Current114/.203 SaveFile, ReadPath, Monetary, Labour and ExchangeRate native
and canonical results pass0; Denomination completes1/3 with only its seven
main decade lines and the unchanged TP1.8195 premise. New reds remain empty,
full footers, all three copy hashes and empty owned groups verify. These
results supersede their older source labels only for these named checks;
the remaining cases are still in the approved serial box chain.

The medicine source finding corrects the timing: the supporting town starts
at19, reaches80, then the original25+25 page windows put the literal at130.
Business can open at18 and medicine at66 under the actual catalogue lag;
the fixture never caused its assumed empty-reach condition. The actual screen
branch requires researching=true and positive room before its no-reach sentence.
No guessed false-researching condition is used.

★ The reviewed414d031a62eead7c196456078c09886336814909773121c04480822ba0524f62
patch adds a separate genuine month-one unfitted founding using the existing
ResearchViewCheck/FittingCheck scratch path, the same two Universities, one
Institute of Technology and one Medical School, with native worker support.
Strict findings require the actual2/1/1 school rows, month1, positive room and
researching in every field, engineering open, the three other fields' actual
openProblems empty, and reach counts equal those real keys. Actual model
sentences and kept/fresh parity are checked before the unchanged literal.
The shared town, its original aging windows, staged/grant/prize checks and
all comparisons remain. Currency formatting is restored in finally and its
identity/PriceLevel.FACTOR are guarded; no old clock, finds or cards are reset.
The previous130-month case also logs its actual field reads. This is a caused
fixture proposal now applied, not a predicted native pass. Full Kept must
prove every precondition and the original laws on the integrated model.

Final721 --fast exits0 on the box, verified and copied by2804: explicit
compile0, cached batch0, maps regenerated, Stale0 firm/206 soft, source guard
empty, class manifests equal. Fresh gates are27.788/27.638 GiB available RAM,
21.912 GiB free commit,0.323/0-percent CPU. Temurin21.0.13+9/Monocle runtime,
assets/fork/a723 records and all end/copy guards match. Archive
feb841ec42aaac95d7b746e613f8f5d7627aff11ddd56961e52aebda29afde20
supplies only the five changed generated documents; notes and records stay.

The first local six-script manifest verification used wrong relative paths
and the wrong hash-property name, so its printed zero-mismatch count was not
proof. Immediately recheck all six absolute paths and actual SHA256 fields
with terminating errors: all six match, including both reviewed remote
payloads. Native launchers also verify the exact payload themselves. No
acceptance or native result is inferred from the failed verification command.

Canonical laptop results below are on exact f0b960367, pinned Oracle21.0.4,
pinned C:/ Earth/world assets and fresh MG1 per start. Model/resources remain
byte-identical through dce's later observer and harness-only commits;
their added hooks default to null in ordinary checks.

| Check | Native/batch | Labelled checks | Seconds |
|---|---:|---:|---:|
| SaveFileCheck | 0/0 | 1274 | 104.2 |
| ReadPathCheck | 0/0 | 92 | 115.9 |
| MonetaryCheck | 0/0 | 722 | 453.3 |
| LabourCheck | 0/0 | 248 | 43.1 |
| ExchangeRateCheck | 0/0 | 236 | 28.4 |

The unchanged pooled16-31 TOTAL policy-effect floor of at least one point
passes at3.635685 percentage points; non-shelter reduction is3.102148 points.
Original-town, supply, saved-observation and purity guards also pass.
DenominationCheck on75937af98 passes canonical3 with only the seven recorded
decade comparisons and TP1's recorded upward-price premise. All new caused
financial-owner, paid-credit, per-position and weighted-conservation/reform
guards pass; no absent real-city owner holds a raw position. The preceding
bounded diagnostic retains every original predicate and
all202 looks: exact-zero owners are0 in all206 snapshots;35109 individual
reform stock comparisons and all purity/inventory guards pass. Lawful positive
fractional owners cause the two legacy under-half claims. The registered scan
correction passes its normal native run; separately captured TP1
deflation preserves every floor comparison. Combined review remains owed.
HousingMarketCheck on dce7b53d5 passes native/canonical0 in19 seconds with
the current14/32 report and profiles. Its caused positive closed-home debt
case preserves actual mortgage claims, the held share and the next ordinary
month through a native save/reload; every retirement and audit guard passes.
Physical closure is distinct from Game.retire's salvage/repayment path.
InvestCheck passes3 for only its recorded builder-wage premise. Stage1 is green.
CentralBankCheck's laptop run reaches its unchanged1740-second timeout;
the exact c7e named gate-box run then passes0 in533 seconds with all laws
intact. Native24-shape result is22 below-Standard cases, with18 required.

CallCheck sections2+6, DeskCheck's household spare and FundCheck's market-book
cases all pass0. The bounded CardCheck/BuildCardCheck query passes canonical0
with both native classes0; its five obsolete BuildCard waiver rows are removed.
WaterCheck and WaterPowerCheck pass0. SubstationCheck, DebtStatisticsCheck and
ResearchCheck on dce also pass native/canonical0. Bank's native output completes
every assertion, including both TAB_LOSS equity identities, but its numeric
native exit was not persisted before the accidentally selected duplicate
Central run was cancelled. Do not label that incomplete query canonical0.

Remaining named checks, transfer of the current catalogue/profiles, default600-month spending
observations, final fast/maps, combined money/saves review, final ensemble and
dry run remain pending. The ship marker stays provisional. Earlier final,
green, format and baseline statements below belong to their named commits.
No user city or user save slot 1–9 is opened or written. Existing checks use
their own isolated scratch slots; new RES3 native fixtures use slot 10.

## Initial implementation

The implementation adds the section 12 recipe catalogue, aggregate cell/class
assignment and class rental ledgers. The existing builder quotes, work queue,
funding and mortgage path remain authoritative; no finish timer is introduced.
The model and its caused checks precede the final calibration report.

★ The initial area/quality/layout utility follows the numerical prototype.
Actual garden geometry and explicitly ground-level access add family/retired
terms. Unknown equipment supplies no extra amenity. Equivalent demand and offer
nodes coalesce and their allocation splits pro rata. Residual paths maximize
compatible affordable placements, retaining valid affordable incumbents first.

★ R1g's selected residential wage scaler is ported, including its saved twelve
monthly pay indices and its load rebuild, from origin/batch/R1g. Its demographic,
founding, research, advice and blocked GDP changes are excluded. Land is not scaled.
BO1-H remains a seam; this batch adds no business-owner seeker group. OG1's real
construction queue and work proofs are retained.

★ Saved class quotes, actual trade quantities/values, staleness and assignments
are flows that cannot be reconstructed from closing stock. They pass through
DataSave and the load path. A format bump and matching oldest-loadable floor
are owed under the old-saves rule: old class IDs/mixes and missing assignments
would read wrongly. The current format and floor are67, advancing from shipped
R6e-rest66, including saved consumer observations and held monetary state.
Earlier boundary evidence is retained at its original source. No conversion
code is added.

Initial baseline --fast passed before source edits (52 s compile; no firm Stale
finding). The first integrated compile exposed and corrected a pooled-weight
getter typo. Focused acceptance, integrated profiles and the final report follow.

## Findings while integrating

HousingChoiceCheck passed its caused cardinality, adults-only, affordability,
premium vacancy, family garden, retired accessibility, incumbency, fractional
demand, equivalent-tie/input-reversal and billion-fold count cases.
HousingInventoryCheck passed its existing native inventory/title/listing and
RN2 first-month continuation proofs with the new catalogue.

The first BuildingDataCheck run retained its exact dated-ID assertion and
correctly found the new housing dates. The expected date map now additionally
contains all fourteen forms; every existing pinned date and equality remains.

The integrated fixture initially ordered the entire catalogue onto an empty
city's yard at once: no project completed in 24 months. This is the real shared
work queue, not a finish timer. Its premise was corrected to stage the existing
founding playtest's houses/jobs first, then measure the catalogue-wide orders.
Both historical and present-day cases now cause paid starts, actual occupied
rent, conserved monthly pool/household money, exact class-state reload and exact
next-month parity. A final repeat will incorporate the cash-strike budget split.

HousingCheck caught two model integration defects. Quote-only classes with no
physical row were erroneously sent to the inventory's explicit occupancy setter;
only physical rows now receive assignments. Its strict conservation check then
caught 4.000000000000001 assigned to four Houses. The allocation now rounds only
within FamilyModel.packingRounding, caps the sequential sum to actual supply,
and leaves a genuine excess an error. Neither the strict title/occupancy guard
nor a conservation/save assertion was relaxed. Typed monthly matching clamps
listing subsets as the existing legacy match did, retaining non-RE occupants.

★ Quote-only unavailable classes are reported but cannot match, start or trade.
Their asks freeze until the existing research/calendar gate opens them. The
initial year cutoffs (1900 through 12 storeys, 1931 for ordinary/luxury towers,
1969 for the 80-storey residential form) are conservative catalogue calibration
choices, not claims about the first building of each height. Their reference
points are the owner's [Empire State Building history](https://www.esbnyc.com/about/history)
(1931) and the contemporary AISC [Architect-Engineer, 1971](https://www.aisc.org/media/l3mkj3lx/aae_1971.pdf)
description of the Hancock mixed office/apartment tower. No modern equipment
attribute is inferred from either; only explicit ground-floor access is scored.
These are pointers to evidence, not redistributed source data or illustrations.

★ Asks use Game.doorPriceOf and its actual taxed builder/work/material/land path,
including the order queue. Separately let parking's capital is excluded from
housing's initial gross-return anchor, while all actual project costs still
have a payer. Candidates receive one-count eligible demand, after standing
assignments and the funded pipeline; quantities use the same bounded allocation
and mortgage forecasts use its attainable income per requested structure.

★ Existing debt is still a sector obligation. Fixed capital weights, including
sites, allocate its carried interest once; class rent targets then cover only
their own allocated carry when tight. A glut class cannot pass its shortfall
to another class. The 0.058 rate is an initialization comparator, not a forced
actual return. BO1-H's absent owner-family seeker group remains its queued seam.

## Integration mends

The prospective-worker forecast now uses actual unfilled/funded jobs and the
current wage/tax schedule, rather than the budget of a current unemployed or
retired household. Standing homes and funded sites reserve eligible demand
once. Each candidate alternative reads the same remaining demand snapshot;
requested quantities and the mortgage desk use its attainable occupied income.
No candidate receives full-capacity proceeds merely because it has capacity.

★ The live pooled-price sharing passes are retired. A single monthly quote
feeds normal and emergency allocation phases. Only unplaced singles whose
pooled recurring resources fund a real remaining door enter sharing groups.
Working singles move into their existing shape/tier/stage shared cells;
seekers retain explicit sharing flows. The existing financial census moves
stocks, without resetting debts or balances. Saved bounded transfer records
retain pre-transfer counts/resources for prospective normal-home substitution.
There is no new permanent household preference axis.

HousingMarketCheck now causes both sides: affordable singles remain normal,
while five otherwise unaffordable budgets fund an actual home. People, bills,
sharing counts/resources and the native snapshot reconcile. Quote-only classes
retain their cost comparator until built; an unbuilt class is not vacant stock.
Equal residual preferences split one-count rent pressure without catalogue-order
priority. Zero posted asks remain valid instead of silently reinitializing.

InvestCheck passes with the new forecast. Its grouped-thousands hold-reason
fixture now supplies 10,000 House doors and explicitly posts their class ask at
zero: with typed affordability, spare unaffordable houses do not cause the
original no-beneficial-order premise. All original assertions remain. The
controlled HD1/RN1-H fixtures retain their original three-form menu; independent
catalogue checks exercise all fourteen forms. No conservation or save assertion
has been removed or weakened.

The catalogue fixture observes BuildLog completion events every month, including
other funded work completing in the window. Its initial workforce, supporting
infrastructure, land and equity are disclosed fixture inputs; all residential
work and settlement use the paid builder path. All fourteen present-day orders
and all historically available orders complete through actual work, and both
cities close the monthly money identities and native continuation exactly.
Final focused checks and the committed report follow.

HousingDoorCheck's first typed run found a real omission: 1,081.827 households
were still unplaced at month 703. Smaller-home crowding was present, but the
original two-households-to-a-door emergency valve was absent. Worse, the legacy
noteUnplaced projection still inferred guests without actual class assignments.
The finding and its threshold remain intact; it now retains actual budgets and
class occupancy at the worst month when it fails.

★ A lease now carries both household quantity and occupied door-equivalents.
Normal placements and valid incumbents precede emergency sharing and doubling.
Doubling can fund two households in a vacant door, with half of the one rent
charged to each. Seekers keep their own-kind rule for those paid pairs, and
emergency groups rotate access to shared vacancies. Equal class/cell nodes
still coalesce.
No additional permanent preference or identity axis is introduced. The actual
allocation, rather than the legacy inferred valve, sets the crowding, unplaced
and reason readers. Separate household/door quantities and bills are saved and
validated together. HousingMarketCheck causes an otherwise unaffordable pair,
proves one physical door and one rent, and round-trips those quantities.

★ Retirement quantities are bounded by vacant RE titles in every class of the
actual mix, including the distress path. The existing spare-retirement hook is
also consulted for distress with a zero keep floor; other sectors retain their
existing result (Freight's zero floor permits the requested quantity). This
protects incumbents when a multi-structure order follows the one-structure
eligibility test. The caused double-door fixture asserts that cap.

The repeated lender run's retained inputs found two further real integration
errors. FamilyStructure.dependants includes retired adults; treating it as a
child count barred retired singles from compact homes. Retired shapes now have
zero children, while working and outside dependants retain the adults-only rule.
The student budget also omitted its existing open treasury facility, although
StudentHousehold actually funds the rent shortfall through that loan. Housing
now reads that actual planning room only while the facility is open; quoting
does not borrow anything. Ordinary households still receive no assumed new
credit. Caused checks assert both facility states, unchanged debt while quoting,
and retired input classification in the actual city. The lender finding is
being repeated without changing its people, rhythm or unplaced threshold.

The next played failure retained retired households with zero resources after
food/fees and unemployed parents with no own-kind host. The former implicit
valve called these guests, without requiring a second rent payment.

★ Emergency hosting now makes that actual: after normal affordable placements
and funded sharing, one additional household may join an already funded home.
The host continues its existing full bill; the guest pays zero, creates no extra
rental proceeds or credit, and cannot bypass the adults-only rule. Extended
family hosting may cross income/status cells; the bounded saved hosted counter
distinguishes it from a normal affordable lease and from a paid vacant pair.
The corresponding shares still sum to one real door, with at most two household
units per occupied door. The primary funded tenant is retained next month from
its actual class payment, rather than treating the emergency guest as a new
normal incumbent. A caused funded-host check asserts zero guest resources, the
host's unchanged bill, one physical home, one actual rent, and explicit hosting.

The hosting run narrowed the lender finding to 230.452 household units at month
810, almost entirely students, while every door and guest slot was used. The
cause was a second omitted part of the original valve: sharing groups were
formed only if a vacant door could be paid for. An unplaced sharing group can
now occupy a real remaining guest slot in a funded home too. Its actual placed
group count, not a proposal, changes the source census. Both emergency phases
use the remaining capacity 2 x door-equivalents minus household units, so the
later phase cannot add a third household unit to an already doubled home. The
caused host fixture also covers this combination, one rent and the two-unit cap.

## Actual migration room and the played proof

The next lender run had 137.346 unplaced household units at month 720. Every
physical door and its one guest slot was occupied. Migration still asked the
old size-only projection for room, using an already squeezed household mix as
its model of new arrivals. That could admit more raw new households than the
typed ledger could place.

★ In a live city FamilyModel.roomLeft now derives the remaining normal/guest
capacity from the actual class assignments and current physical inventory,
reserves all existing unplaced demand first, and uses the raw formed working
household mix for incoming size/children. It is a read-only projection: no new
quote, clearing, trade or saved counter. Current completions enter its room
without rewriting last month's quote. The existing migration lastRoom save
continues to carry the resulting bound. Bare FamilyModel fixtures retain their
original API. The callback is wired both at initial construction and the new-city
rebuild, and native continuation proves the load context.

The crowding approach ramp remains a physical measure, with the existing
separate affordability pull. Using only paid occupancy for that ramp froze an
empty founding city before workers could earn their first wages; the fixture
correctly failed both its growth and mortgage-door premises. The hard arrival
bound still uses actual remaining physical slots. Quote-only classes are never
queried as physical inventory; its Unknown-class guard remains strict.

HousingDoorCheck now passes the unchanged AUTO2 seed 12 city to month 900:
15,093 people, 3,192 homes; the worst unplaced value prints 0.000, and all 899
household cash identities close. The original mortgage-order, capital-issue,
trimming and no-land checks are intact. HousingMarketCheck passes the actual
room/host/paired-door invariants, both historical and present-day paid catalogue
construction, monthly pool/household money, native state and next-month parity.

RES3 initially used format/floor 62 above RES2's 61. After HC1 shipped its 62,
RES3 merged it and advanced both numbers to 63; no conversion is introduced.
Its new form IDs must likewise follow any shipped Substation row. PW5 has not
shipped as of the latest main refresh recorded above.

## Save and unit findings during final checks

SaveFileCheck found two real zero-weight reload errors: the old size matcher
could replace a saved zero rent weight with a positive one. The load rebuild
now projects saved typed assignments without clearing again, and restores zero
weights as well as positive weights. The topology save byte comparisons pass
unchanged. Compatibility occupied-home readers and their saved field now read
actual typed lets, including a valid zero, rather than a capped demand estimate.

PowerViewCheck's future-input alignment changed the fixture's unsaved House
power override back to the native template but left the live sector's cached
residential draw behind. RE now genuinely pays that building-side utility bill.
The fixture recomputes its future sector draws from the aligned templates and
also asserts every sector's draw agrees; all dispatch/observation/history,
actual next bill, fuel, currency and audit comparisons remain. PowerViewCheck
passes, including both native fuel and exact native bills.

SaveFileCheck's withdrawal case drained only free fund cash, leaving cash
reserved by its earlier hand-order fixture. The withdrawal could therefore
pay instead of owing the sale. It now drains the whole actual till through
fundDrawOut (the treasury is the counterparty) and asserts it opens empty.
The actual fund sale, world fills, late payment and save comparisons pass.
The remaining hunger premise is being strengthened through actual retail input
prices and a nonzero rationed shelf, rather than depending on a poor cell's
pre-census balances remaining in that shape after housing moves. Assertions
and every continuation comparison remain, with delivered share also required
positive to avoid a default-zero round trip.

ReadPathCheck passes. The latest ReloadCheck exits 3 with only the two existing
RN3 fixture-3k premises (live and caused all-homes-closed). They are main's
documented stranded-debt-share rounding class: the hand closure can yield
0.99999999999999990 where the fixture requires exactly 1. Actual continuation
comparisons remain green; neither premise nor save comparison was relaxed.
DenominationCheck found a new divergence to mend, not a tolerance to relax.
The student's large finite planning allowance now follows the actual currency
unit through derived city context, including after load/new-city reset. No new
saved stock is added. Genuinely equivalent demand on zero-cost offers now
coalesces independent of irrelevant cash budgets; caused tests cover pro-rata
free offers and the scaled treasury allowance. Final currency proof follows.

The affected ScaleCheck fixture copier now classifies native housing titles and
typed flows explicitly: counts and aggregate bills grow with the copied city;
per-home asks/carry/capital, per-household budgets, attributes, dates and wage
indices remain intensive. It also includes RES2's native inventory holdings,
which the older copier omitted. No assertion or scaling tolerance changes.

The full 2,400-month LongPlaytest then reported one new model finding: three
early unplaced-household months, 2–38, with no money or native-continuation
finding. A short traced replay identifies zero housing budgets across every
new working cell in months 2–3: last month's empty aggregate income rows were
being divided across this month's new people. ★ Current scheduled net wages
and pensions now supply recurring housing income, with all existing fee,
food, interest and savings reserves retained. The ordinary payroll/cash strike
still pays it; the quote creates no income, loan or receipt. The same founding
inputs now have a caused HousingMarketCheck case with monthly money audits.

DenominationCheck's extended trace localized a second discrete choice defect:
class asks parted at m124 while actual assigned counts still agreed to about
2e-13 households and all budgets to about 9e-16 relative. A recomputed positive
ULP remainder was pressure on an unbuilt class; its no-stock positive-demand
branch changed the target multiplier. Residual demand now observes the same
existing packing-rounding bound as the allocation/summarizer. No capacity,
conservation or money/save tolerance moves. The caused fractional-demand case
requires exactly zero demand in every unbuilt class after full placement,
then checks each opening comparator is unchanged.

The first hunger input shock was too broad and stopped ordinary shopping;
the second revealed that a one-basket recipe pantry can round just under one
and Retail's whole-basket sale becomes zero. The fixture now caps its price
input at the model's existing retail cap, supplies ten real baskets and strikes
three months for health's lag. Its original hunger, shopping and continuation
assertions remain, and delivered share is additionally required positive.

The next trace proved why the per-source remainder bound alone was insufficient:
at m125 an unbuilt Duplex had demand 6.17145223814e-14 in one currency and zero
in the other, despite actual allocations agreeing to about 3e-13. ★ A blueprint
without standing stock retains its opening cost comparator; it has no observed
scarcity market. Demand is still reported and used once by prospective choice.
Scarcity/glut repricing applies when stock exists. This removes the discontinuous
"any positive quantity means double the target" shortcut without discarding
small fractional households or changing any financial comparison.

A separate caused price case failed before its mend: one ULP below ten fully
occupied homes waived carry and targeted 4.833333333333332 instead of 5. The
economic glut classification now uses the existing packing bound, while an
actual 0.1-home vacancy still waives the lift. Physical occupancy stays strictly
bounded. Native loading additionally refuses class demand below its occupied
quantity, atomically.

Final SaveFileCheck passes. Its full-shelves poverty premise is now caused by
the actual zero-pension policy and empty cash/foreign/paper stocks across one
large former tier's working and retired cells, with consumer credit exhausted.
That prevents the census's shape/retirement moves from refilling a single cell.
Other working tiers keep shopping. Ten real pantry baskets cause positive
rationed delivery; three presses carry the sale through health's lag. The broad
price shock was removed. No pension or hunger output is assigned directly, and
all original money/save comparisons remain; positive delivery is strengthened.

★ Historical ScaleCheck controls now begin with an ample one-billion-unit city
material yard each. The copier intentionally does not K-scale that yard or its
fixed 36-unit monthly replenishment. With actual builder quotes now pricing
homes, the large copy exhausted its yard on repairs at month 1 and the small
one retained 3,838 units: the House's allowance was 1.78522166439 versus zero,
with identical land and wage scaler. That is a different intensive cost input,
not a tracking error to hide in a larger band. Both controls now assert each
month that their House quote draws all its material from the same free-yard
regime. All original TRACK prices, savings, counts, registers and other bounds
pass unchanged; ordinary construction and the catalogue report retain the
actual unnormalized yard/funding path. ScaleCheck exits 0.

Refreshed main through 7b60b9a8d (queue coordination only). The current traced
DenominationCheck exits 3 with eight recorded DK1 comparison lines; first-month
and year checks pass, and national-card/trade-lane comparisons pass. Its
first later financial drift is visible in the Real Estate share mark at m133,
while housing asks and assigned quantities still agree to about 2e-12 and
5e-13. A native Bank book price split is visible by m142. The residual decade
gaps are about 1e-6 in output/rent/index/currency, with the shops' small till
about 2.5e-5 relative. The share/desk code is untouched. The recorded financial
rounding class remains with DK1; its automatic known-red verdict is not a
relaxed comparison. The stranger's money/save review sees the trace and the
unchanged 1e-9 checks. Final ordinary (untraced) repetition follows below.

The final HousingCheck staffing premise selected 150 Houses, a valid zero-staff
home, so it did not exercise the assertion's "cannot staff the ordered home"
condition. For that one decision the fixture now offers its two apartment
blocks, asserts that every offered home has posts, then immediately restores
its original three-form menu in finally. Its original order/staffing assertion
and every card, payroll and carry assertion remain. No model gate is bypassed.

The catalogue report now gives full-staff payroll cash as well as posts for
each form, plus the actual current staffed RE total. Both burden columns use
the same two-earner unskilled reference household, with its actual payslip
levies; the observed mixed unskilled row mean is reported separately so sharing
does not silently change the take-home comparison's reference household.

The final 2,400-month run passed its money and native continuation checks but
caused a later supply finding: 56 unplaced-household months, m1009–1102, worst
108.751 at m1067 with 4,547 physical homes. The traced replay shows all paid
doors already doubled: m1009 has 3,924 occupied doors, 3,924 emergency household
units, and 7.016 still needing supply despite positive budgets. Arrivals were
admitted against last month's household formation at working-family density;
current domestic formation and arrivals who start work next month then exceeded
the real two-household capacity. No occupancy limit is raised.

★ Before migration, a bounded copy of the existing FamilyModel previews current
domestic formation from the current pyramid and existing employment/outside
context. Positive family/adult demand changes reserve their compatible rooms
first. Immediate arrivals conservatively require at most one room per person;
when children are present their outside-household profile requires family space.
They do not assume the established workers' sharing/density. This frame moves
no people, money, quote or lease. It is derived once before migration, discarded
by clearing, rebuilt after load, and adds no saved field. The caused room cases
prove the formation reserve, actual extra-door admission and unchanged live
families/monthly state; the founding case also requires positive population and
occupied homes.

Main was refreshed through 653cb95ce: only QUEUE.md and the future FC2 brief
changed. No foreign-construction-pricing code is taken ahead of that batch.

The changed played path also exposed a withdrawal-fixture observation error.
Its first withdrawal owed 67.884292601; the normal step had already sold
12,168.8674462 Retail shares by rule to the world for 67.8842926011. No ask was
left resting for the fixture's later manual world buyer. The fund's actual cash
was 59.079630949 and that amount was paid late next press. The fixture now
counts actual current-month rule share-sale ledger units plus resting asks.
It separately requires no hand sale mixed in, and reads actual fund-to-world
settlement cash. Dividing by the highest actual sale price gives a conservative
lower bound on world-filled share quantity, preserving the original share-unit
thresholds. An unsold/unfunded position still fails; no sale output is assigned.

HousingDoorCheck then caused a compatibility bottleneck: 77.558 unplaced at
m706, mostly large child families, with all paid doors occupied but spare compact
guest slots. The bounded HousingChoiceCheck reproduction failed four assertions:
one adult softly chose the family door, the large family fit only that door in
emergency, and the compact door stayed unused. Locking the normal assignment
before the emergency pass made the soft choice an accidental hard constraint.

★ Emergency completion now uses the normal matching's residual paths. Integer
edge costs count normally suitable placements: a zero-cost augmentation may
reroute movers while preserving the maximum normal count. Retained incumbents
have no reverse edge. Equivalent cells/classes still coalesce and split pro rata;
physical caps, budgets, children restrictions and strict final stock checks stay.
The reproduction is green, including input reversal and an incumbent that must
stay even when that leaves the family blocked.

A separate integrated input case failed with 0.85 of a child family unplaced:
five actual single adults formed a group that took the family host space before
ordinary guests were offered compact space. Three funded hosts had enough real
slots for everyone after that transfer. ★ The group phase reserves compatible
family host slots for waiting child families first; adult groups can use compact
host space explicitly as crowding. Only actual occupied host slots are offered,
and each funded host retains its original bill. No income, credit or capacity
is invented. The caused case also checks actual family-member conservation,
sharing, the exact three occupied doors, receipts and the two-household cap.

The final allocation replay passed HousingDoorCheck through m900: 36,625 people,
8,603 homes, no material unplaced household (worst prints 0.000, m802), and all
899 household cash identities closed. The bank-capital premise is real again:
49 capital-short orders were offered to the owners whole, none offered less;
the original down-payment, halving, advisor and no-stranded-cash checks pass.
HousingChoiceCheck and HousingMarketCheck are green with both caused mends.

The committed [catalogue report](RES3-catalogue.md) is generated from those model
inputs. HC1-integrated repeat: small city 210 people, 55.842 ms,
H/T/A/M/L 65/32/112/0/0. Present-day: 45.487 ms, 84/32/330/0/0;
larger 148,967-person case 54.640 ms, 82/32/314/0/0. Historical:
39.424 ms, 62/32/193/0/0; larger 149,126-person case 41.473 ms,
62/32/193/0/0. These are entire integrated months, not solver
microbenchmarks. All retain 141 permanent household cells. Modern physical
classes are 32; historical known stock is 16 classes, with all 32 quoted and
availability reported. M is new typed loan cohorts (HO1), L live listing cohorts;
both are zero here. Paid starts and BuildLog completions are observed separately.

The next full named pass was green for --fast, inventory, households/flows,
building data, investment, housing, utilities, SaveFileCheck, ReadPathCheck and
ReloadCheck. Reload compared 423,170 history figures and 99,495 saved figures
through m1229 exactly. HousingCheck's existing optional retirement-in-debt path
was reported OBSERVED/not reached, not counted as a pass.

DenominationCheck then returned the recorded exit 3 but with 18 large comparison
failures, rather than the earlier eight small DK1 differences. That verdict was
not accepted as evidence of a main fault. The trace shows equivalent housing
asks/allocations through m124 (about 1e-15/1e-13); at m125 Agriculture alone
offered 22.3997697976 in the plain twin, nothing in the reformed twin. The plain
twin built two Mixed Farms while the other held for the city's housing priority.
Their m124 Agriculture cash, debt, shares, income and quote agreed to arithmetic
precision; the share quote parted only after that unequal offering. Both books
were empty, so this was not the queued DK1 dust-fill event.

★ A caused final-match case has 100 funded hosts, 100 zero-resource guests and
one ULP of a 100-household source in a separate cell. It failed three exact-zero
assertions: that tiny entire cell survived the per-cell rounding and became a
positive final shortage, activating Agriculture's unchanged `> 0` guard. A real
0.0001-household deficit remained explicitly unplaced in the same fixture.
The final aggregate shortage/reason counters now use the existing whole-source
FamilyModel.packingRounding once. Family status rows and residual price pressure
respect that final classification. No assignment, title, bill, money tolerance
or stock check is changed; physical occupancy and two-household limits remain
strict. The genuine fractional shortage must still activate housing priority.
This is RES3's integration fault and is mended here, not waived as DK1.

The large unit fork disappeared, but the remaining eight small comparisons were
also investigated rather than waived. Their first assignment split was m181,
0.002825 household units, while budgets/asks still agreed within 1e-14/1e-15.
The affected cells had identical affordability decisions and preference order in
both currencies. A second caused kernel case failed: an incumbent's one-ULP
retention remainder was counted as an extra suitable mover. ★ Residual need and
free supply now use their original source's existing packing resolution before
becoming graph nodes, and again after a phase. No capacity is rounded upward.
That caused case passes; DenominationCheck is now fully green, all comparisons.
At m181 the assignment difference is only 9e-13 households. This was another RES3
residual-node defect, not the queued DK1 event. No money/save bound was changed.

Modern ScaleCheck tracking is now green too. Its historical tracking still has
five empirical savings/deposit findings after the changed played founding. The
optional trace compares the native class quotes, free-material regime, bank,
household stocks, audits and financial flows; that diagnosis follows below.

The historical source and K-copy agree at load on native stock and money, and
the existing whole-horse-post rounding bounds pass. Every home quote draws the
same free-material regime. Through the tracking window rent weights are exactly
33464 at both sizes, rents agree to about 1e-6, GDP differs only about 1e-5 at
the savings jump, and neither bank resolves or writes down deposits. Month four
household cash is 18522.6861343 versus 19529.1947414 per city. The actual pool
ledger isolates the transfer: desk shares bought from households are 0.8451357253
versus 1073.4886562494. Purchases in the opposite direction are 320.8681100 versus
349.2379038. These are cash-for-share transactions, not rent or invented income.
The household cash identity closes on both actual sets of flows. The optional
trace keeps their prior/closing savings, foreign holdings, bonds, debt and bank
cash; the historical tracking loop now additionally asserts pool and household
conservation at the existing MoneyAudit tolerances in both cities every month.

★ The five changed historical empirical TRACK pins are recorded with FS2, the
queued sweep that owns ScaleCheck's played fixtures. All existing bands and
native/save/count/register checks remain. They are not labelled the earlier
DK1 event: no precise desk-threshold fix is claimed here. The historical town's
whole-post differences change a discrete financial trading outcome after the
played founding moved. This batch changes no Exchange or OrderBook production
code. RES3's own two unit-invariance defects above are mended and all currency
comparisons pass. The final ScaleCheck repeat must also pass the added money
identities and name only these five FS2 pins.

## HC1 integration

HC1 shipped on main efca2c5fd (0.9.190) while the prior-source full playtest was
running. It was merged immediately; its published VERSION, card stocks, debt
statistics, cash channels and census stock routes are kept. Generated map
conflicts are regenerated from the combined source. Format/floor advance from
shipped 62 to 63 because RES3's class flows cannot be reconstructed. PW5's IDs
remain to be refreshed if it ships before this batch.

The kept prior-source full playtest is target/RES3-LongPlaytest-pre-HC1.log:
its only model red is 20 unplaced-household months, 689-922. Its money, own-line
bounds and native continuation checks are green. That supply finding is not
waived; the integrated model's traced replay follows the card-budget seam.

A caused five-to-one sharing/census move preserves all HC1 positions: card
balance 328.0831740677, holder position 3.2808317407, late balance 65.6166348135,
late-holder position 0.6561663481, and excluded position 1.6404158703. The budget
case first failed with zero principal reserve where 0.3 was due. ★ Live housing
now quotes the entire opening balance's contractual card minimum at the bank's
current funding price and scheduled income. The bare API reserves interest and
minimum principal. The prediction is separate from HC1's saved settled flow:
it matches actual card settlement, and neither the saved minimum/interest nor
any card position changes when a home is quoted. HousingMarketCheck is green
with these caused cases and both native-era continuations.

The integrated 1,000-month diagnostic has no unplaced-household finding. Its
three end-of-run own-line mean bounds are short-window findings and are not
waived; the full 2,400-month acceptance run uses the original bounds.

The integrated HousingDoorCheck's only red is its played capital-shortage
opportunity: there are zero such orders in the m431-900 window. It observes
157 lender-door months, with 70 short-segment orders written whole, no cut or
held short-segment orders, no down-payment refusal, no stranded cash and zero
unplaced households. All 899 household cash identities close. Population is
15,168 and homes 2,656. ★ This missing played opportunity is recorded with
FS11, the queued sweep that owns HousingDoorCheck, retaining the original
assertion and all money/placement checks. The deliberately caused capital
cases in CreditCrunchCheck are included in the final named verification.

## Integrated native asset-mark finding

The unchanged exact HC2 debt-statistics save assertion caught a real missing
flow. Every financial holding, cell count, car stock and exchange rate matched,
but debt/assets was 2.5883189261098205e-9 live versus 2.5883189268332515e-9
loaded. The live used-car mark was 0.22470679371627045; load lost it and used
the new-car-price floor, 0.17791977463721569, instead. This was not a housing
or debt-stock rounding tolerance.

★ The closed used-car trade is now one bounded citywide DataSave tuple: traded,
offered, spend, credit and actual price. The late load restorer keeps the mark
after the rebuild. Currency reform divides spend, credit and price; the K-city
copier multiplies counts, spend and credit and leaves the unit price alone.
SaveFileCheck causes a positive actual used-car trade, checks the tuple exactly,
and retains the original exact debt-statistics assertion. Both reload assertions
passed on the first mended run. DenominationCheck and ScaleCheck additionally
check the tuple through their existing reform and native copy paths.

The AW2 construction twin's staffing premise also failed after the played town
moved: closing its ten shops and four medical schools left only 60.22% of a new
shop's posts available, below the unchanged 80% gate. ★ Its fixture now also
retires the actual university and releases its ground before the existing
settling month. This causes spare workers through the model's workplace inputs;
it changes neither the gate nor its staffing answer. The original staffing,
automatic land buying, private-order and native continuation assertions all
passed. SaveFileCheck is green, including the exact used-car and debt-survey
checks (target/RES3-save-mends.txt and target/checks/SaveFileCheck.log).

The first new reform guard found that DenominationCheck's played town offered
no used cars. Its money comparisons passed but the positive-mark premise was
empty. The fixture now causes a financed second-hand sale with the existing
HouseholdBalance market (an insolvent car owner and a buyer with a deposit),
then restores that same actual closed trade to both currency twins before the
reform. ScaleCheck also sends that nonzero trade through its native JSON copier.
This is an input for the flow-transform checks; SaveFileCheck independently
causes the positive trade through the integrated played city's monthly market.
The caused flow has 800 offered, 20 traded, price 15.679012345679013,
spend 313.58024691358025 and credit 223.58024691358042. The buyer's deposit
is above SHARE_CUSHION_MONTHS times income and derived from CAR_DEPOSIT,
CAR_ADOPTION and the used-car floor; every saved tuple component is nonzero.
The copier proof uses a separate copy of the fixture's complete native save,
leaving the economic tracking source intact. Its initial tuple-only JSON input
was incomplete for the census copier and correctly failed on missing keys.

## Final integrated profile

The final HousingMarketCheck repeat passed both eras, their actual paid starts
and completion receipts, monthly pool/household money identities and exact
native continuation. Its complete report is RES3-catalogue.md. Measured full
months on this laptop (not solver-only timings):

| Scratch city | People | Month ms | H/T/A/M/L |
| --- | ---: | ---: | --- |
| Small | 210 | 49.178 | 65/32/112/0/0 |
| Present-day | supporting catalogue inputs above | 51.096 | 84/32/330/0/0 |
| Larger present-day | 148,967 | 45.542 | 82/32/314/0/0 |
| Historical | supporting catalogue inputs above | 31.336 | 62/32/193/0/0 |
| Larger historical | 149,126 | 35.990 | 62/32/193/0/0 |

Permanent household cells remain 141 in both larger cities; physical classes
are 32 today and 16 in 1900. All 32 class quotes and 14 form availability rows
are reported in each era. M is the new typed ownership-loan cohort seam for HO1,
and L is live listing cohorts; both remain zero in these rental fixtures.

The final ReloadCheck repeat is exit 0: its m1229 autosave continuation compares
818,555 history numbers in 341 series and 105,494 saved numbers exactly. After
the passed assertions it prints an asynchronous autosave-history temporary-file
rename warning in the shared Earth scratch fixture (the same warning appeared
on the earlier pre-card-integration repeat). This is recorded separately from
the successful native comparisons; no save check or production I/O path was
weakened to pass it. SaveSlotCheck also passes, including its held-writer and
one-writer-at-a-time cases; its wall-clock timing observation is soft.

## Retirement-room and capital-fixture finding

The deliberately caused CreditCrunchCheck exposed a derived-room defect: its
physical retirement removed 70 homes, yet the room read stayed at 108.6. Leases
from the previous clearing still occupied a demolished class, and their
displaced households were not reserved against other classes before arrivals.
The crunch then had 7.59 unplaced households in its first month.

A focused HousingMarketCheck case first failed with room 4 where 2 fit: two
actually placed child families lose their occupied form while another form
stands. ★ The read-only room projection now reserves other rooms for displaced
lease households, keeping child families in family-capable doors and using
compact rooms for displaced adults first. This adds no quote, clearing or
saved state; the assertion also checks that the read leaves the monthly state
unchanged. The physical two-household-per-door limit remains exact.

The crunch fixture's cut budget consequently counts two potential slots per
removed door. Its unchanged margin assertion and no-homelessness watch still
apply. Its separate hand-written six-block order previously reached a demand
limit before the bank-capital trim: actual standing homes and funded sites
already reserved those tenants. ★ That hand-order fixture now physically removes
standing rental homes before its order, causing actual replacement demand;
no vacant candidate receives fictitious rent to force the lender through.

The mended CreditCrunchCheck is fully green: 48 homes retired leave 12.6
households of room, every crunch month has zero unplaced households, and the
original six-block hand-order assertion reaches the bank-capital trim. Its
door quote is 6851.6 versus the planner's 6754.4; the lender reads the door
price, the owners are offered 5138.7 and put in 244.6. The original pricing,
partial-equity, leverage-band and no-sticky-capital-refusal assertions all pass.

AP2's shipped exact-state performance changes are retained. Its VERSION is
0.9.191; the shipped save format remains 62, so RES3's format/floor remain 63.
The source merge resolved only four generated-document conflicts, regenerated
by the next named checks. The already-compiled full playtest finishes on its
pre-AP2 classes; AP2's own 2,405-month trace comparison is byte-identical.
Following native checks compile the merged source.

The final HousingDoorCheck repeat has 149 lender-door months, 52 whole orders
while the segment is short, no cut/held/down-payment refusal, and zero unplaced
households throughout the window. All 899 household money identities close.
It ends at 10,801 people and 1,893 homes. Its only red remains the missing
played bank-capital opportunity, recorded with FS11; the deliberately caused
CreditCrunchCheck is fully green. These final numbers supersede the earlier
157/70 observation on the pre-retirement-room source.

The final full LongPlaytest is exit 0. Its 2,404 audited monthly transitions
have no money finding, every reload matches, and the maximum unplaced-household
count is zero. The original mean-month carrier bounds hold: UnsettledIncome
3,360 ppm against 5,000; UnsettledRent 345 against 500; UnsettledFees 21.1
against 30. The additional HA1 and payslip-carrier fixtures pass too. Kept logs:
target/RES3-LongPlaytest-final.log and target/RES3-LongPlaytest-final.txt,
compiled from a5f4f6c0c before the AP2 merge described above. No short-window
bound or homeless finding is waived.

SaveFileCheck on the AP2-integrated source is exit 0. It retains exact card
positions/flows, used-car trade/mark and debt-statistics comparisons. The AW2
workplace inputs now give share 1.000000 before the original 80% staffing gate;
the ordinary saved/live construction twins still pass every original assertion.

## Local acceptance and review handoff

The final model includes HC1 and AP2 from shipped main 1b89c5c4c. PW5 has not
shipped at this handoff; its eventual shipped ID and save format must be
refreshed before RES3's final gate. All calls below use the supplied earth/world
paths and isolated fixture directories. Named checks run one call at a time;
the final card selection additionally uses cbs.parallel=1 because it selects
BuildCardCheck too.

| Check | Result |
| --- | --- |
| --fast | 0; no firm Stale finding, 199 soft observations |
| HousingInventoryCheck, HousingChoiceCheck, HousingMarketCheck | 0 each; final merged-tree repeats |
| HouseholdCheck, HouseholdFlowCheck, BuildingDataCheck | 0 each; final merged-tree repeats |
| CardCheck (including BuildCardCheck), DebtStatisticsCheck | 0; final merged-tree repeats |
| HousingCheck, InvestCheck, CreditCrunchCheck | 0 each; all original laws retained |
| HousingDoorCheck | 3; only its one FS11 played-opportunity premise; money and placement green |
| DenominationCheck | 0; original 1e-9 comparisons and the nonzero used-trade transforms |
| ScaleCheck | 3; two HT1-H historical financial pins after REV-RES3; strict native, count, flow-transform and money checks green |
| LongPlaytest | 0; full 2,400-month run, additional fixtures, no findings or unplaced households |
| SaveFileCheck, ReloadCheck, ReadPathCheck | 0 each on the AP2-integrated source |
| SaveSlotCheck | 0 in the post-HC1 native-writer repeat; one soft wall-clock observation |
| WaterCheck | Red at 42c991099 for the residential utility partition; 0 after REV-RES3 mends |

The earlier pre-HC1 five-pin ScaleCheck record is retained above as evidence,
not as five current waivers. The final ship file keeps only month-four savings
and month-five deposits under HT1-H, plus the single HousingDoorCheck FS11 row.
No money, conservation, save, numeric or carrier bound is widened.

The final read-only ScaleCheck trace confirms the current FS2 class. At month
four, household savings per city are 25,568.8001625 versus 26,637.8022059.
The desk buys household shares for 0.0196997180 versus 1,173.78489006;
its reverse sales are 323.779344144 versus 323.780738880. Both banks have
zero resolution loss. Housing rent is 0.00344637227121 versus 0.00344637010252,
with equal occupied weight 33,286. The GDP miss is 1.76e-5 and CPI miss 9.56e-6;
the existing half-post bound and both pool/household money identities pass.
This is the same discrete cash-for-shares class traced before HC1, with the
current card-integrated numbers. Its only remaining original TRACK reds are
month-four savings and month-five deposits. Kept trace:
target/RES3-ScaleCheck-final-trace.log (metadata target/RES3-scale-final-trace.txt).

Review is owed for money and saves: typed lease budgets/rents and actual builder
funding; DataSave, the load rebuild, SAVE_FORMAT and OLDEST_LOADABLE_FORMAT.
There is no threading change. The pick is ALL 246. The provisional marker
requests the stranger's review; the changed trace's 80-run ensemble and the ALL
dry run on the gate box follow that review's go, before the marker is dropped.

## REV-RES3: mends from 42c991099

The review found two P1s and three further findings. All five are handled in
this delta; the earlier FS2 ownership references above describe the superseded
handoff, not an approval to pass a red under a shipped row.

★ `acceptTypedHousing` now takes `lastHomesBySize` from the inventory on both
the live and load paths. This is derived state, so it needs no saved field.
The typed load previously skipped `house()` and left this input null, or kept
the previous city's census during an in-place load. The caused scarce-door
fixture first failed its fresh-load and in-place-load floor comparisons and
its first-next-month migration crowding comparison. Its live floor is
161.284185872; the same full family matrix without a census reads 100.769453876.
The corrected basic case preserves the actual next-month crowding factor
0.547229504882 in all three cities. The final fixture also funds replacement
housing before the native save, to exercise the following construction and
migration phase. Save/read/reload results are recorded below.

★ The two ScaleCheck TRACK rows now name queued HT1-H. FS2 shipped on
2026-11-20 and is not an eligible queued owner. The m4 trace establishes the
cash-for-shares divergence, but does not establish an `Exchange.deskLimits`
room threshold parting the controls; no DK1 cause is claimed. HT1-H is the
review's specified fallback for that played premise. Its queue wording is the
orchestrator's record. Both original TRACK assertions and all money identities
remain. HousingDoorCheck retains its valid FS11 owner.

The numeric IDs 0 (House), 1 (Studio Apartments) and 6 (Low-Rise Apartments)
are retained while their specifications are revised to RES1 section 12:

| Existing row | Specification changes |
| --- | --- |
| 0 / C02 | dwelling `legacy` to `H120`; one private bay; benchmark cost weight 846,000 |
| 1 / C06 | cash 8,628 to 8,692; materials 320 to 322; points 680 to 700; dwelling `S`; 20 shared bays; benchmark cost weight 196,725 |
| 6 / C08 | cash 10,196 to 10,284; materials 377 to 381; points 2,400 to 2,432; area 80 to 75 m²; dwelling `F`; 32 shared bays; benchmark cost weight 319,714.285714 |

All three explicitly use the existing 1900 availability gate. Their door counts,
capacities, bedrooms and qualities retain their previous values. Eleven new
rows append at IDs 155-165; no numeric ID is renumbered. The catalogue's nextId
and fallback comment now say 166. These recipe/dwelling-key changes are also
part of why the format/floor bump is owed. PW5 remains unshipped at this mend;
if it ships first, its 155 Substation must precede our unshipped rows 156-166.
Move `HousingForms.ALL`'s hard-coded IDs (HousingForms.java:40-96) with the JSON
rows: otherwise `installFallback` rewrites PW5's 155 Substation as a Compact
house. Set nextId 167, raise format/floor from its shipped value, and regenerate
the report before gating.

★ The services partition now includes only *unbilled* residential buildings in
the homes category. Rental-building power/water already belong to Real Estate's
billed draw. The existing screen's categories therefore count each unit once
and retain the city's own draw. BuildingsTemplate, ServicesManager,
EconomyManager and UtilitiesHandler documentation now agrees with that policy.
The 40,000-house WaterCheck case first failed four assertions: it double-counted
8,000 water units and 40,000 power units, reported the city's 20 water units as
-7,980, and summed 64,170 units against the actual 56,170. The corrected test
keeps actual draw, supply, rationing, payroll, revenue and city-use checks;
billed homes add zero to the unbilled category. WaterCheck is green.
WaterCheck was also red at the reviewed 42c991099 handoff; its omission from
that handoff's acceptance list did not make it green. The Services screen's
homes segment now reads 0, with the rented homes under "business, billed".
The existing category labels need a later relabel to explain that partition;
this batch changes no UI.

★ The exact one-below-floor boundary uses the genuine shipped 0.9.191
format-62 save/history pair, not current JSON re-stamped as old. Its producer
ran alone against unedited shipped 1b89c5c4c classes, after that detached
worktree's --fast compile. It wrote scratch slot 10 at month 31. The pair has
141 native 144-field household cells and two HC2 surveys, with no RES3 market,
home-pay history or used-trade tuple. Original schema, version and SHA-256
hashes are pinned in the check and in
src/main/resources/fixtures/res3-format62/README.md. No file is converted.
The common refusal helper retains every previous format-60/61 byte, history,
manager, month and cash guard, and additionally checks family identity and the
migration floor. The new pair is refused in fresh and already-running cities,
with both files byte-identical and the entire running city/history unchanged;
ReadPathCheck repeats its reads after refusal. SaveFileCheck is green.

The final caused reload fixture pays for twenty construction depots, holds
new private/city orders, and saves one funded aggregate order of 100 Houses
while family doors are scarce. Existing work and staffing complete 70 Houses
on the next native press. The source floor is 189.183099038 versus
122.097069387 in the identical family matrix without its door census. Fresh
and in-place loads preserve that floor. All three cities then admit
20.8935037267 people with crowding factor 0.470736722549 and identical class
markets; their money audits close. A separate guard requires positive arrivals,
an active crowding ramp strictly between zero and one, and arrivals below the
physical-room cap, so the migration comparison cannot pass on a zero-flow or
already-capped case. The earlier simpler fixture's zero-arrival outcome was
retained as a finding of insufficient test inputs, not used as acceptance.

Post-review checks: HousingMarketCheck (both eras, profiles, positive scarce-door
reload twins), WaterCheck, SaveFileCheck, ReadPathCheck, ReloadCheck and
BuildingDataCheck all exit 0. ScaleCheck exits 3 with only the same two original
TRACK pins, now assigned to queued HT1-H. The fast check reports no firm Stale
finding and 199 soft observations. The full playtest and other household/card
acceptance results above remain the pre-review live-model record; no bound or
assertion was widened in these mends. The changed main since the review is
queue/measurement documentation only (through ea16576cd), merged locally.

REV-RES3's delta review of 47764c061 is SHIP (2026-11-22). Its three small
follow-ups are folded in without another review: the fallback-ID warning
above, one-space nextId indentation, and the WaterCheck/Services-label record.

## First ensemble and the funded-demand mend (2026-11-23)

The approved model, merged with main's queue records through cbf8a362e, was
pushed as 964e65cd7. `RES3-final-20261123` ran on the gate box, eight cities at
a time: 80/80 finished, none short or missing, 16.7 minutes of runs. Its files
are kept under target/ensemble/RES3-final-20261123 and its controller log is
target/RES3-ensemble-final.txt. The box used its earth-2/world-1 copies; the
0.9.190 baseline has no extra JAVA_OPTS beyond its data properties.

This first run is **not acceptance of the final model**. Against 0.9.190's
25/26 information-plus-hard score and 10/10 hard score, it scored 11/26 and
5/10. Every M1 money-audit criterion passed, with zero failed or unread months.
Every F1 criterion failed solely on "households with no home": AUTO2 seeds
2/10/12, ZERO_SLACK 2/10/12, ZERO_HOT 11, SHOCK 4/10, HIGH 7/10. AUTO2's final
median population was 2,723,424 against the baseline's 5,217,451; its takeoff
criterion still passed 16/16, median month 1,016 against 739. The complete
score and changed information verdicts remain in that run's score.md. No
F1 line is waived on the basis of the money audit passing.

Read-only native HousingDoorCheck overlays replayed six representative seeds,
with the ensemble's founding, advice rhythm and native policy setters. Each
matched the ensemble's population, price index, paid rent and monthly GDP at
the written nine-significant-figure precision in every observed row: AUTO2-10
(171 rows), AUTO2-12 (301), AUTO2-2 (111), ZERO_HOT-11 (61), SHOCK-4 (71), HIGH-7
(81). These are four-column comparisons, not a claim of complete CSV byte
identity. The overlays and logs remain under target/RES3-shortage-*; production
HousingDoorCheck and LongPlaytest are restored after diagnostics.

The native shortages include distinct causes. Seed 10's first shortage has
605 homes, no pipeline, and 1,211.88980565 seeking households, with the plan
held for land. Seed 12's first has 2,001 homes, no pipeline, and 4,005.29230203
seekers; its 192 flagged months include 178 land holds and 14 months building.
SHOCK-4's nine flagged months are land holds. FB11's growth-land funding class
is relevant to those observations; that does not explain away the other
funding holds or establish the final model's ensemble verdict.

At ZERO_HOT-11 month 247 the plan selects the Large-home luxury tower, whose
native lender cover is 0.938915846302 against Mortgage's 1.20 requirement.
A read-only check of all fourteen alternatives finds no ignored feasible
project: the other thirteen miss the operating build hurdle; the selected
tower alone covers it and then fails the lender. For example, Compact house
income 0.0412068448168 is below its 0.0434345266555 hurdle. This finding does
not justify bypassing either the operating margin or the lender's coverage.

★ The class-price pressure had an omission in RES3 itself. After emergency
doubling it counted the occupied physical shares, then looked for additional
demand only among households still without any shelter. Two households able
to fund whole suitable homes, sheltered in one door, therefore registered only
one unit of demand. The legacy rent pressure still counted seekers who were
doubled up. With demand erased until shelter actually ran out, rents could
stay below the native build hurdle during a real shortage of separate doors.

The mend keeps the physical occupied shares, then counts each funded remaining
share once in the actual class when a whole home there is suitable and
affordable. Other residual demand goes once to its best compatible affordable
alternative, retaining the existing tie split. It does not create a second
lease, receipt or permanent preference cell. The new native scratch regression
first failed with class demand 1 against two sheltered households. Its control
retains the lower demand when only a half bill is affordable. An intermediate
attempt sent all extra demand to preferred alternatives; the occupied-class
scarcity guard correctly stayed red. Keeping the funded demand for the actual
suitable class makes both guards green, with the one actual rent unchanged.

HousingMarketCheck passes the mend, including both era reports, real funded
builds, the positive-arrival reload twins, cards, budgets and money audits.
The existing saved class-demand field carries the corrected quantity; no new
saved field is introduced. The format/floor remains 63, with the real shipped
format-62 boundary. The final model's ensemble is still owed after the new
money-review delta; the failed first run is retained as the finding.

The corrected 4a33d0a83 model replayed ZERO_HOT-11 through its full endpoint,
month 402: all 401 observed months have no flagged housing shortage, against
the first model's 16 flagged months. This uses the same native founding,
advice rhythm and policy, without changing the operating margin or mortgage
test. Kept: target/RES3-pressure-ZERO_HOT-11.log and its batch metadata.

The new report in RES3-catalogue.md is from this corrected model:

| Scratch city | Integrated month | H/T/A/M/L |
| --- | --- | --- |
| Small, 203 people | 39.840 ms | 70/32/70/0/0 |
| Present-day catalogue | 51.649 ms | 84/32/351/0/0 |
| Present-day larger, 148,949 people | 47.180 ms | 82/32/341/0/0 |
| Historical catalogue | 38.997 ms | 62/32/187/0/0 |
| Historical larger, 149,029 people | 38.411 ms | 62/32/187/0/0 |

Both catalogue eras still quote all 32 classes, and retain 141 permanent
household cells in the larger cities. All fourteen forms follow the actual
era gates and paid builder path.
The table and catalogue were refreshed on the final PW5-integrated model
ec8fb7510, including its native format-64 reloads and next-month twins.

The post-mend ScaleCheck's historical month-four savings and month-five
deposits TRACK assertions are green, so their old ship-file pins are removed.
Its only red is the existing free-growth fixture search: eight candidate
windows within the unchanged 60-month search provide no six consecutive
months with affordability pull 1. The evaluated native count, tracking and
money laws pass; the free-growth copy lacks its fixture premise. This is the
played-condition class owned by queued HT1-H, not shipped FS2. Its original
assertion and bounds remain, and the replacement ship row names that exact
premise rather than carrying the now-green financial pins. Rejudging the kept
native log against the updated rows reports only the known fixture red.

The original HousingDoorCheck is now fully green after the pressure mend:
6,809 family homes finish in its window, no household is left without shelter,
and all 899 monthly household cash identities close. Its FS11 ship row is
removed. No part of HousingDoorCheck was changed to obtain that result.

The affected SaveFileCheck then exposed an exact load-seam difference:
familyRentWeight 7636.075444713484 became 7636.075444713485. The loader derived
the family half by subtracting studio weight from total weight, overwriting
the exact family value already saved in Real Estate's extras. Card loans and
bank capital agreed; the complete opening JSON comparison correctly failed.
★ Carry the existing saved family segment through the rebuild, and restore
total/studio/family aggregates independently at both typed-load restore sites.
This adds one load-workspace value, with no new JSON key or conversion. The
original complete CardCheck.continues comparison now passes. Its temporary
diagnostic writes are removed; no equality or tolerance was relaxed.

## PW5 shipped: applied integration and native format-63 boundary

PW5 shipped as f937d2c22 (0.9.192, format/floor 63) during these checks. The
own check controller was stopped by its verified PID; no verdict from a partial
run is acceptance. Merge 59a40be89 keeps PW5's JSON/fallback Substation 155,
shifts only RES3's eleven unshipped rows to 156-166, and moves HousingForms.ALL's
hard-coded IDs with them. The three existing numeric IDs remain 0/1/6, with
the specification changes recorded above. nextId and the fallback comment
are 167. Format and floor rise together from shipped 63 to 64: PW5's native
layout has its closing grid costs but lacks RES3's class market and new rental
flows. No old-save conversion is introduced.

The merge preserves both refusal histories. Its poor-retiree fixture keeps
RES3's source/destination cohort cash/credit inputs and PW5's securities
conservation: the cohort's securities, basis, tax claims and bonds move to a
populated recipient outside that former-tier cohort. They do not disappear or
refill the target through retirement/shape redistribution. The original care,
issuer, money, save and continuation assertions remain.

The genuine native 0.9.192 format-63 pair was produced at month 31 by
HousingFormat63Fixture, running alone against unedited f937d2c22 classes after
that detached tree's --fast build. Source diffs before/after are empty. Native
slot 10 wrote both files; gzip only packages the raw bytes. The pair contains
141 144-field cells, two HC1 debt surveys and PW5's pending/struck grid costs,
with no RES3 market/home-pay/used-trade fields. Provenance and both pinned
uncompressed SHA-256 values are in
src/main/resources/fixtures/res3-format63/README.md. The detached source tree
was removed only after its resolved target path and unedited source were
verified; raw files and build/producer evidence remain under target/RES3-native63-*.

ec8fb7510 preserves all native 60/61 refusal fixtures, PW5's native 62 pair,
and RES3's independent native 62 pair. The new 63 pair supplies the exact
one-below-floor case. All pairs retain the byte, manager, family-floor, history
identity and full running-city guards. The PW5-integrated SaveFileCheck exits
0, including the populated-city exact continuation. The final ensemble must
use this merged model and the newer 0.9.192 baseline; the failed 0.9.190-based
first run is not reused as that verdict.

On the merged model, ReloadCheck's only red is main's already recorded HT1-H
hand-order premise: fixture 3b has zero crews drawing materials. Every native
save, field and continuation comparison passes. ScaleCheck retains only the
free-growth premise above, now ten candidate windows on the PW5-integrated
path; all its evaluated tracking, count and money laws pass. No bound changes.

DenominationCheck exposed another played-fixture premise: its bus towns had
positive riders, fares and transit bills, but no positive private-driver fuel
payment. ★ Give this unit-reform fixture explicit opening fleet assets in an
existing working household cell, and transfer a buffer from its funded
treasury using the model's savings-cushion, subsistence and journey-cost inputs.
No closed fuel value is set. A cash-only attempt still produced no fleet; the
opening fleet makes native drivers and a fuel bill. The first press strikes
that bill, and the following press carries it into household settlement.
Both fixed native periods now assert pool and household money conservation in
both towns. This causes the existing positive-fuel condition through model
inputs and the actual settlement sequence, without changing the condition or
the exact currency-reform comparisons.

387d54043's DenominationCheck exits 0. Its native driver readings are ownership
0.00218071132 per household, 4.01000040 drivers, fuel struck 0.0000463920781,
billed 0.00783993061 and paid 0.00782785503. The paid figure is the preceding
press's actual bill. All reform assertions remain, with added money audits;
the ordinary floating-currency neutrality twins keep their original setup.
The failed attempts remain in target/RES3-driver-original.*, -cash-only.log
and -first-press.log; the final record is target/RES3-PW5-DenominationCheck*.

## Final integrated checks before the second review

The final code is 387d54043, with shipped main f937d2c22 merged. Each named
harness ran alone with the two shared data paths and cbs.parallel=1. These
checks exit 0: HousingMarketCheck, HousingCheck, HousingDoorCheck,
HouseholdCheck, HouseholdFlowCheck, BuildingDataCheck, InvestCheck, WaterCheck,
SubstationCheck, SaveFileCheck, ReadPathCheck, DenominationCheck and LongPlaytest.
HousingDoorCheck closes all 899 monthly household cash identities. The complete
2,400-month playtest and its additional controls report 2,404 audited months,
no findings, and every reload matched. The final --fast exits 0, with zero
firm stale findings and 199 soft findings. Evidence is kept in
target/RES3-PW5-<harness>.txt and .log, and target/RES3-PW5-fast.txt.

ReloadCheck and ScaleCheck exit 3, known reds only. ReloadCheck's one hand-order
premise is already recorded on main under HT1-H; all its save/continuation
comparisons pass. ScaleCheck's sole free-growth premise is the ten-window case
above, recorded under queued HT1-H in this ship file. The old historical TRACK
and HousingDoorCheck pins are gone because those assertions pass. No money,
save, conservation or comparison check was weakened.

The final pick against origin/main is ALL, 247 harnesses. Its review line is
"needed - saves: DataSave.java, +SAVE_FORMAT, +OLDEST_LOADABLE_FORMAT".
Money and the load path also changed after REV-RES3's SHIP on 47764c061:
funded doubled demand, exact saved rental segments, the shipped PW5 integration
and the native driver fixture need the reviewer's delta check. The provisional
ship marker stays in place. A fresh 80-run ensemble against the newest
0.9.192 baseline and the ALL dry run remain owed after that review; the failed
first ensemble is evidence, not the final ensemble verdict. No dry run has
started, and the orchestrator's dry-run go is still required.

REV-RES3 reviewed 47764c061..8c7a470ff and returned SHIP with no P1. Its two P2s
are folded without another review: main ecbc3b975 now gives HT1-H the remaining
free-growth premise, and SaveFileCheck's boundary comment names the actual
res3-format63 pair from shipped 0.9.192. The native bytes and assertions are
unchanged. The final ensemble is scored against docs/ensemble/0.9.192.md,
with rents, affordability and growth read alongside the hard gates.
The comment-only follow-up's --fast exits 0 (0 firm/199 soft); the six
generated map changes were dates only and were discarded. The named model
checks above remain the validation of the unchanged reviewed model.

## Second ensemble: emergency host space must respect feasible alternatives

The reviewed fd7071f54 snapshot ran on gatebox as
RES3-reviewed-PW5-20261123 against docs/ensemble/0.9.192.md, all 80 runs complete,
none missing or short. It exits 4: 14/26 overall, 6/10 hard, beside the baseline's
20/26 and 6/10. All five M1 policies pass, with zero failed or unread audit
months. F1 fails for AUTO2, ZERO_SLACK and HIGH (3/16 each versus baseline 1),
and SHOCK (2/16 versus 1); ZERO_HOT passes with 0/16. The unchanged raw score
and CSVs remain in target/ensemble/RES3-reviewed-PW5-20261123. This failed run
is not the final baseline and grants no exception to the findings.

All sixteen AUTO2 towns reach 40,000, median month 1,027 versus 747. Final
median populations are AUTO2 2,490,129 versus 4,649,724; ZERO_SLACK 1,462,930
versus 2,883,284; ZERO_HOT 5,968 versus 7,632; SHOCK 23,140 versus 67,498; HIGH
9,789 versus 32,356. Only ZERO_HOT passes T2's informational 75% bound.

For the reviewer's rent/growth watch, these are medians across the sixteen
seed readings, each reading averaged over its last 120 native months:

| Policy | Rent series / index, baseline | RES3 | Rent series / worker wage, baseline | RES3 |
| --- | --- | --- | --- | --- |
| AUTO2 | 0.007019 | 0.006542 | 0.0844 | 0.0803 |
| ZERO_SLACK | 0.007420 | 0.007230 | 0.1008 | 0.0961 |
| ZERO_HOT | 0.008373 | 0.009185 | 0.0997 | 0.1109 |
| SHOCK | 0.007967 | 0.008674 | 0.0886 | 0.0895 |
| HIGH | 0.008852 | 0.010366 | 0.0967 | 0.1156 |

These use Ensemble's existing weighted getAverageRentPaid series and average
worker wage, a price-series proxy. Actual affordability uses each household's
full recurring budget. Native baseline and RES3 CSV readings, sources, ranges
and per-seed values are kept in target/RES3-reviewed-rent-readings.json.

Native AUTO2 replays of seeds 9, 3 and 14 compare all 491 observed months'
population, index, rent and GDP with the ensemble at its nine-significant-digit
CSV precision: zero differences. This establishes the recorded city path,
without claiming full CSV byte identity. Findings in a CSV label their first
occurrence; duration comes from the native trace, not that label alone.

Seed 3 is short from m609 through the trace's m660 endpoint (52 months),
peaking there at 103.203 households, 1,147 homes, no pipeline and 2,271 sq ft
free. Every flagged month is held for land. Ground looks m461, 469 and 591
already refuse the whole offer under the cash-share budget: at m591 price
1,470.890 versus budget 46.142. Seed 14 flags m634 and two later months through
m660, peak 2.442 at m651, 941 homes, no pipeline and 4,048 sq ft free. Its
m592/593 ground looks also refuse the offer under the cash-share budget. These
observations show the whole-offer/cash-share mechanism, but do not establish
that its cause is outside RES3. REV-RES3 requires native main 0.9.192 twins
before an FB11 attribution; the forms' land use, demand and smaller towns may
instead be the cause. Neither seed passes by name on this evidence alone.
The kept traces are target/RES3-reviewed-native-{3,9,14}.log, with the native
market/inventory snapshots in target/RES3-shortage-<seed>-*.json.

Seed 9 is different: at m47 it leaves 1.344704 retired households unplaced
while homes are vacant and land is available. The controlled first-red case
is one funded host, one zero-budget guest, one guest able to fund half a door,
and a second actual vacant door. The sequential free-host pass splits its one
free place pro rata across the guests; the paid-vacancy pass then shelters the
fundable residue but strands half the poor household. HousingMarketCheck
exits 4 with four original assertions red (target/RES3-free-host-first-red.*).

★ Match free host places and self-funded vacant half doors in one flow, after
normal placements and same-kind five-adult sharing groups. Feasible shelter
takes priority over soft preference or which emergency pass happened first.
The existing solver retains its suitability, maximum-two-household shares,
budget checks and rotated equivalent ties. Temporary flow-node keys are
translated to actual comparable classes before a lease is retained; no new
class, saved field or quote is introduced. Original hosts keep their full
bills; guests pay only their actual funded vacant shares, and zero-budget
hosted guests owe zero. Physical shares and hosted/crowding reasons reconcile.

The positive case now shelters all three, records one hosted guest, 1.5 actual
occupied door equivalents and exactly 1.5 asks of receipts. The full existing
HousingMarketCheck passes (target/RES3-free-host-mend.*). A counter-case lowers
the second guest below half the ask and requires one explicitly unplaced
household, just one occupied home and no additional rent. The affected native
replay, checks, money/save delta review and another final ensemble remain owed.

The corrected native AUTO2-9 replay completes m2-70 with zero unplaced
households in all 69 observations (target/RES3-host-mend-native9.*), including
the old m47 failure. Its diagnostics are removed, both observed source files
match the reviewed source, and their timestamps force a clean recompilation.

The regular HousingMarketCheck passes with the unfunded counter-case too.
Compiled HousingDoorCheck and LongPlaytest contain no temporary diagnostic
branch. Paid half-door shares retain each cell's separate lease and can be
partitioned by seeker kind within aggregate vacant stock; the solver does not
merge their household counts. Existing same-kind five-adult groups still form
only in emergencySharing. No borrowing, host subsidy or new building ID is
created by a temporary flow node.

On this mend's played HousingDoorCheck window, the only red is again its FS11
capital/owners fixture premise: six orders would be capital-short, all six
behind quotes supporting no share issue, so none is offered to the owners.
The lending/quote rules and every assertion are unchanged. There are zero
unplaced households, 2,155 homes finish by size in the watched window, and all
899 monthly household cash identities close. The same FS11 row is restored
with this native cause; it had correctly been removed on the previous model
where the premise occurred. The pre-pin report is retained separately as
target/RES3-host-mend-HDC-beforepin.*.

The refreshed catalogue and profiles are from the regular check of ff8ea23be,
including the new unfunded control. All fourteen forms and thirty-two classes
remain reported in both eras, with the shipped Substation at 155 and residential
IDs through 166. Both populated native reloads and their next months pass.

| Scratch city | Integrated month | H/T/A/M/L |
| --- | --- | --- |
| Small, 203 people | 40.277 ms | 70/32/70/0/0 |
| Present-day catalogue | 48.869 ms | 84/32/352/0/0 |
| Present-day larger, 148,950 people | 47.710 ms | 82/32/341/0/0 |
| Historical catalogue | 38.512 ms | 62/32/190/0/0 |
| Historical larger, 149,168 people | 35.396 ms | 62/32/189/0/0 |

The larger cities still have 141 permanent household cells. This algorithmic
mend adds no saved state; the batch's already owed format/floor 64 stays 64.

## Emergency-match mend: completed checks and review handoff

Final model ff8ea23be passes HousingMarketCheck, HousingChoiceCheck,
HousingCheck, HouseholdCheck, HouseholdFlowCheck, BuildingDataCheck,
InvestCheck, WaterCheck, SubstationCheck, SaveFileCheck, ReadPathCheck,
DenominationCheck and LongPlaytest (exit 0), each run alone. HousingDoorCheck
exits 3 with only its restored FS11 quote-supported-capital premise.
ReloadCheck exits 3 with only main's HT1-H hand-order/material premise.
ScaleCheck exits 3 with only HT1-H's free-growth premise, still ten candidate
windows; every historical savings/deposit TRACK assertion passes. All native
cash, save, continuation, lending, placement and count comparisons are kept.

The full default playtest and its additional money controls pass: 2,404
audited months, no findings, every reload matched, ending "All checks passed."
The final --fast exits 0 (0 firm/199 soft). All final logs and batch metadata
are retained as target/RES3-host-mend-<harness>.log and .txt, with
target/RES3-host-mend-fast.txt. The pick against merged main ecbc3b975 is ALL,
247 harnesses; its review line remains "needed - saves: DataSave.java,
+SAVE_FORMAT, +OLDEST_LOADABLE_FORMAT". The new money delta is the emergency
allocation itself; neither DataSave nor its format changes again in this mend.

No diagnostic source or compiled diagnostic branch remains. The refreshed
catalogue is the check's actual model report, not recalculated estimates.
The provisional marker stays while REV-RES3 reviews the delta after fd7071f54.
Another final-model ensemble against the newest baseline remains owed after
that go. The second ensemble's raw FAIL and land-funding evidence above remain
unaltered; no F1 assertion is waived by this handoff. No dry run has started.

## REV-RES3 attribution P1: native shipped-main counterfactual

REV-RES3 returned SHIP for fd7071f54..ab8656288's code, and rejected the
outside-cause attribution above. The presence of a cash-share hold alone
does not make a new RES3 shortage main's fault. That attribution is withdrawn:
neither seed 3 nor seed 14 is passed to FB11 or LP-G1 by name.

Native shipped 0.9.192, f937d2c22, replayed AUTO2 seeds 3 and 14 through m660,
observing every month m450-660. Its isolated worktree first passed pristine
--fast with an empty source diff. The only temporary edits were read-only
observations in HousingDoorCheck and LongPlaytest's existing ground-look trace;
founding, controller rhythm, cash-share budget and all model code stayed native.
Each case ran alone through the named harness with the shared Earth/world data.
Both main traces have zero unplaced households in all 422 observations. Their
population, index, rent and GDP match all 422 baseline CSV rows at the CSV's
nine-significant-digit precision, zero differences. The baseline snapshot
9c769adf3 differs from shipped f937d2c22 in source only by the version string.

The already reviewed final model ff8ea23be was then replayed with the same
observations: seeds 3, 14 and 9, each m450-660. All 633 observations have exactly
zero unplaced households. Thus the failing fd7071f54 snapshot's new shortages
belong to RES3; its later emergency-match mend already avoids those windows.
No further model change is made on the strength of a missing main failure.
These bounded replays do not establish later months or the final 80-run verdict.

Land per home below is actual standing residential template plot area divided
by its dwelling count, in square feet. Seeking is the model's current household
door demand, including explicit sharing; sites counts actual funded home doors.
Cash is the city's native cash in model thousands. Every displayed unplaced
count is zero, rather than a tolerated finding or a changed threshold.

| Seed | Model | Month | People | City cash | Homes / sites | Seeking | Plot ft2/home | Unplaced |
| --- | --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| 3 | Main 0.9.192 | 450 | 24,511 | 9,870.534 | 6,504 / 269 | 6,719.764 | 1,414.668 | 0 |
| 3 | Reviewed RES3 | 450 | 6,914 | 13,909.152 | 1,476 / 30 | 2,239.281 | 4,382.114 | 0 |
| 3 | Main 0.9.192 | 609 | 33,642 | 48,050.688 | 9,635 / 0 | 9,614.121 | 1,211.313 | 0 |
| 3 | Reviewed RES3 | 609 | 8,718 | 38,512.648 | 2,050 / 108 | 2,873.854 | 4,414.634 | 0 |
| 3 | Main 0.9.192 | 660 | 36,499 | 3,018.449 | 10,458 / 0 | 10,458.000 | 1,193.632 | 0 |
| 3 | Reviewed RES3 | 660 | 10,379 | 107,061.061 | 2,182 / 90 | 3,383.050 | 4,329.056 | 0 |
| 14 | Main 0.9.192 | 450 | 5,897 | 10,170.548 | 1,014 / 160 | 1,708.487 | 5,642.012 | 0 |
| 14 | Reviewed RES3 | 450 | 4,979 | 687.426 | 1,081 / 66 | 1,559.864 | 4,725.254 | 0 |
| 14 | Main 0.9.192 | 634 | 25,055 | 86,170.114 | 7,025 / 63 | 7,025.000 | 1,625.907 | 0 |
| 14 | Reviewed RES3 | 634 | 7,021 | 8,513.868 | 1,413 / 0 | 2,286.432 | 4,326.964 | 0 |
| 14 | Main 0.9.192 | 660 | 25,686 | 150,685.552 | 7,340 / 0 | 7,271.894 | 1,597.003 | 0 |
| 14 | Reviewed RES3 | 660 | 7,295 | 32,263.430 | 1,413 / 0 | 2,366.155 | 4,326.964 | 0 |

The ground looks differ with the actual played paths. Main seed 3 looks at
m467/468 with budgets 2,182.526/2,510.346; reviewed RES3 looks at m463/464
with 1,900.179/2,095.244. Main seed 14 looks at m463/471 with
1,256.883/1,443.203; reviewed RES3 looks at m456/464 with zero cash-share
budget, and again at m586 with zero. Both may encounter exhausted land;
only the actual observed shelter outcome establishes the finding. RES3's
lower populations and different plot-per-home mix remain visible in the table.

Seed 9's reviewed replay also covers the baseline's m605 window: m600, 605,
609 and 660 all have zero unplaced households. At m605, 8,106 people seek
2,626.395 doors beside 1,502 homes and 6 doors on site, with 424.505 city cash.
The landlord is held for land, with 3,605 sq ft free, but nobody is unplaced.
The final ensemble must still read this month in its own seed-9 CSVs.

Evidence: target/RES3-main192-{3,14}.{log,txt},
target/RES3-final-compare-{3,14,9}.{log,txt},
target/RES3-main192-pristine-fast.txt and
target/RES3-counterfactual-records.json. The JSON retains every observed field,
including family/studio door shortfalls, latent head demand, city cash, land
and exact decisions; each raw log retains ground looks/offers/holds/purchases
and standing form counts. Both observed sources were restored byte-for-byte
afterward. Only the notes change in this attribution follow-up, so the next
step under the reviewer's go is the final-model ensemble against 0.9.192.

## Third ensemble: own land exhaustion and blueprint/order integration

RES3-host-final-20261123 ran b948e245e (model ff8ea23be) on gatebox against
0.9.192: 80/80 complete, none missing/short, raw FAIL 16/26, hard 8/10.
Every M1 passes with zero failed/unread audits. AUTO2 and ZERO_SLACK each have
two F1 runs versus baseline one: seed 8 first flags m618, seed 12 m927.
SHOCK and HIGH each have one and pass against baseline one; ZERO_HOT has none.
All sixteen AUTO2 cities take off, median m1,040 versus 747. Final medians
are 3,031,056 AUTO2, 1,634,084 ZERO_SLACK, 5,819 ZERO_HOT, 22,681 SHOCK and
8,995 HIGH, beside baseline 4,649,724 / 2,883,284 / 7,632 / 67,498 / 32,356.
Only ZERO_HOT passes the informational T2 bound. The raw FAIL stays unchanged
in target/ensemble/RES3-host-final-20261123; no outside-class pass is used.

The 633 native rows for seeds 3/14/9 match all four observed columns of this
ensemble at CSV precision, zero differences. Seed 9's m605 rows have audit=1,
findings_new=0, findings_distinct=0 and an empty finding in AUTO2, ZERO_SLACK,
SHOCK and HIGH; ZERO_HOT ends before m605. Exact values and rent/wage/real-rent
readings are retained in target/RES3-host-final-{readings.txt,rent-readings.json,
seed9-m605.json}. The third-run F1s are new RES3 failures, not an extension of
the withdrawn FB11 attribution.

Read-only native observations reproduced seeds 8 and 12 and saved full native
slot-10 checkpoints immediately before their first flagged presses. The
observed source is restored afterward. Loading each checkpoint and taking one
native player press reproduces the original population, index, rent, GDP and
unplaced count at CSV precision, even after the read-only candidate quotes.
The evidence is target/RES3-host-final-finding-{8,12}.*,
target/RES3-host-final-{8,12}-before-{save,history}.json,
target/RES3-host-final-{8,12}-bad-market.json and target/RES3-candidate-reads.*.
Two temporary observer mistakes (uncaught file I/O, then a missing scratch
save directory) were corrected; their failures are diagnostic setup, not
accepted model checks. No observer or checkpoint changes a model input.

At seed 8 m618, all 1,063 homes are fully doubled, 2.033377 households lack
shelter, there is no home pipeline and only 1,393 ft2 is free. House/terrace
stock consumes 5,354,000 ft2, 5,036.689 ft2 per home. At seed 12 m927, all
3,458 homes are fully doubled, 13.241186 households lack shelter and there is
no home pipeline. The remaining 7,279 ft2 fits a compact home, while the planner
holds for an 18,000-ft2 terrace. These are actual physical shortages, not
unfunded rents, and belong to RES3.

The checkpoint's candidate readings identify the blueprint trap. At seed 8,
unbuilt Low-Rise quotes 19.449 per project against a 34.778 operating build
hurdle; mixed mid-rise quotes 15.960 against 33.710. Established houses and
terraces quote roughly twice the neutral gross capital anchor. A new class
was initialized at that gross anchor and had no operating-cost quote because
it was absent from physical stock. It could not enter, while the established
large-plot forms kept winning. Seed 12 also shows a profitable compact offer
that fits the remaining land but loses to the preferred, non-fitting terrace.
Finally, the typed order replaced the full door backlog with income from one
project, capping its door-based quantity at one project when head-capacity
shortfall did not dominate.

★ The mend in progress quotes an unbuilt class's actual operation plus its
capital-return anchor, with the existing lease update speed. Actual standing
stock keeps its existing scarcity/glut and carry-floor rule. A blueprint cannot
inherit debt from another class. All form quotes are prepared, including
classes absent from physical stock; optional parking creates no lease receipts.
★ Profitable candidates that fit the actual land compete before a preferred
form that cannot fit. If none fits, the native land hold remains. Child-family
eligibility and actual budgets remain hard constraints.
★ Size the typed order from the compatible, affordable remaining demand and
the actual recipe's per-class unit counts, reserving standing homes and funded
sites once. The existing full-cost, build-margin, mortgage, quantity-halving,
owner-funding and actual construction gates remain in the path.

The controlled first-red HousingMarketCheck reports fourteen missing
operating quotes, a stranded compact alternative and a one-project order for
an eighty-household backlog (target/RES3-planner-first-red.*). Its child-family
and zero-budget controls already pass. The first mend fixed selection/sizing
but still missed unbuilt operating carry because it iterated physical stock;
that intermediate failure is retained as target/RES3-planner-stock-only-carry.log.
The corrected catalogue-wide cost preparation and native replays are being
checked. This model moves money/quotes/orders: fresh named checks, REV-RES3's
delta review and another final-model ensemble remain owed before any dry run.

The corrected HousingMarketCheck now passes (target/RES3-planner-mend.*).
The fractional-demand comparator is updated to the explicitly selected unbuilt
supply quote (capital anchor plus actual operating carry); it retains the same
exact near comparison and all zero-demand/phantom-scarcity assertions. The
operating-return test uses allocated home capital, preserving optional parking's
separate capital with no invented parking receipts. Neither a money/save
comparison nor a quantity/conservation bound changed. Both loaded checkpoint
controls reproduce the old failing press exactly in the four observed columns
and the unplaced count. Fresh from-founding seed-8 and seed-12 mend replays are
now running; their success is not assumed from the isolated fixtures.

The fresh model ae8aad170 removes both native shortages: seed 8 has exactly
zero unplaced households in all 151 observations m550-700, and seed 12 in
all 151 observations m850-1000. At the former m618 failure seed 8 now has
26,203 people, 7,688 homes and 184 funded doors on site; at m927 seed 12 has
12,254 people, 4,418 homes, no doors on site and zero unplaced. The differing
growth paths are retained rather than passed off as identical cities. Seed 12
is smaller than its old path, while shelter is ample; growth remains an
explicit informational comparison for the next ensemble.

The raw evidence is target/RES3-planner-native-{8,12}.{log,txt}. Both observed
source files are restored to their committed bytes and touched for compilation.
The model changes are committed at ae8aad170; its full named-check pass,
catalogue refresh and money-review handoff are in progress. No dry run has
started, no F1 exception is requested, and another 80-run ensemble is owed
after REV-RES3 reviews this actual model change.

The normal HousingDoorCheck is green on this model: 61 capital-short orders,
59 offered to owners whole and two behind unsupported quotes; 2,918 homes
finish in its watched window, no household is unplaced and all 899 monthly
household cash identities close. Its now-green FS11 pin is removed again.

InvestCheck exposed two stale premises, retained in
target/RES3-planner-InvestCheck-beforefix.*. Its land-negative case supplied
one foot less than the preferred plot, although the new compact alternative
could fit. It now supplies one foot less than the smallest known housing plot
and asserts no known form fits; the original refusal and land-reason assertions
remain. Its ten-house labour quote assumed a scarcity multiplier of one on a
played construction queue. The exact work identity now includes the model's
actual order multiplier, retaining the 1e-9 bound, wage-input checks and every
tax, materials and settlement identity. No money or save check is removed or
loosened; the resumed run must make both premises explicit.

InvestCheck now exits 0 with those same exact bounds and identities.
The model's latest catalogue/profile report is from HousingMarketCheck on
ae8aad170, with both populated native reloads and next-month twins green:

| Scratch city | Integrated month | H/T/A/M/L |
| --- | --- | --- |
| Small, 214 people | 48.813 ms | 63/32/124/0/0 |
| Present-day catalogue | 44.496 ms | 84/32/274/0/0 |
| Present-day larger, 148,943 people | 45.706 ms | 82/32/259/0/0 |
| Historical catalogue | 38.753 ms | 62/32/126/0/0 |
| Historical larger, 148,988 people | 37.317 ms | 62/32/135/0/0 |

Both larger cities retain 141 permanent household cells. The report still
lists all fourteen forms and thirty-two classes, their actual price/budget/
availability/work outcomes, and the shipped IDs after Substation 155.

CardCheck also selects BuildCardCheck by the runner's substring rule. The
credit-card harness itself has zero failures, including the exact saved
statistics and native continuation. The build-card harness finds three red
assertions in its existing loss-first Rail Spur case. Read-only observations
show a negative estimate, -28.13918112808416, and both LOSS and INTEREST
correctly failing. Its first failure is STAFFING: 29% now, 70.7393422% with a
year's arrivals, below the unchanged 80% bar. The standing railway already
covers all 1,590 tonnes traded with 49,792 tonnes of capacity; lack of trade
is not the missing premise. The fixture must cause enough labour to reach
its loss-first assertion. These three moved played-town premises pass by
name to queued FS12, the existing build/card sweep. Every original staffing,
loss, interest and first-failure assertion stays intact; no UI or production
code is changed for them. Evidence: target/RES3-planner-BuildCardCheck-observe.*
and the restored-source target/RES3-planner-CardCheck.*. The two diagnostic
prints also contain the word FAIL and are not accepted check lines. The
observed source is restored byte-for-byte and compiled class files are
checked to contain no RES3_CARD_DIAG remnants.

LongPlaytest completes its normal 2,400 months, all subsequent material and
household money fixtures passing. Its one raw finding is the map ring at
m2400: industry 2.43 km, homes' band 1.78 km, outer kinds 3.35 km. The
repository's existing LongPlaytest/CL7 record classifies that named finding;
the raw result remains visible. There is no shelter finding in this run.
This is not an exception to the pending monetary ensemble's M1/F1 verdict.

Main cdbe83dc0 is merged at 30dd6a7e0. The only main delta from ecbc3b975
is QUEUE.md's ownership record for FS5; no source, schema, catalogue IDs or
baseline changes. Format and load floor remain 64; the planner algorithm
adds no saved state. No new bump is owed by this delta. The batch's original
bump remains owed under the old-saves rule, with all real previous-format
pairs and intact-file/live-city refusal checks retained.

MortgageCheck's original branch-reload premise also moved: the short closure
window had zero paying customers while its homes and supporting works were
still being built. All origination, lender, insurance, capital, closure timing,
money and payout laws passed, but the nonzero customer assertion properly
failed. FS9 has shipped, so that is not a usable queued owner. This fixture
is mended through inputs: its existing landlordCity factory stands operating
works and three depots, standLandlords stands fifty actual homes, and the
town is given exactly seven branches before the six past the charter close.
Two additional assertions require that starting count. The original six-at-once,
first-cost-known, demolition, capital, bank-equity, money and exact reload
comparisons remain unchanged. A trial with the full standing factory and six
additional branches correctly failed: it had eight and closed seven. Standing
homes alone still had no paying customers. Both intermediate failures are
retained, not accepted checks. The final fixture passes with 84.023846119
actual fee-paying customers and later-branch cost 6.411122386 restored exactly.
No bank/customer field is stubbed, no fee is invented, no closure limit or
save tolerance is loosened, and no production code changes for this fixture.
Evidence: target/RES3-planner-MortgageCheck-beforefix.*,
target/RES3-planner-MortgageCheck-standing-works.*,
target/RES3-planner-MortgageCheck-homes-only.* and the final
target/RES3-planner-MortgageCheck.*.

## Shipped PW1-W integration before the new money review

Main b34277adc shipped 0.9.193 while the final fixture checks were running.
It is merged at 17abb80ab. All source merges were clean; the generated map,
dial, harness and month-order conflicts were resolved by regeneration from
the combined compiled source. --fast exits 0, zero firm/199 soft Stale findings
(target/RES3-PW1W-fast.txt). The merge retains typed rent collection, the
unbilled-homes draw of zero, IDs 156-166 after Substation 155, nextId 167 and
format/floor 64. VERSION 0.9.193 is inherited from the publisher, not edited.
PW1-W's new pending-water record and internal water/power transfer coexist
with the saved class market; its billed utility revenue stays the real payers'
bills. No new RES3 saved field is added in this follow-up.

PW1-W-notes section 5 retains all eight default trace CSVs byte-identical to
native main 0.9.192, so it publishes no newer ensemble baseline. The newest
baseline remains docs/ensemble/0.9.192.md. The new RES3 model still owes its
own final 80-run ensemble after REV-RES3's delta review; the water integration
does not excuse it or the new seeds' F1 findings.

The merged HousingMarketCheck passes, including the controlled operating-quote,
fit and backlog first-red cases, all original conservation/budget controls,
both populated native reloads and next-month twins. The catalogue report is
regenerated from this merged model. Its populations, class counts, assignments
and cohorts are the previous report's; the measured integrated months are:

| Scratch city | Integrated month | H/T/A/M/L |
| --- | --- | --- |
| Small, 214 people | 44.137 ms | 63/32/124/0/0 |
| Present-day catalogue | 38.053 ms | 84/32/274/0/0 |
| Present-day larger, 148,943 people | 48.567 ms | 82/32/259/0/0 |
| Historical catalogue | 39.702 ms | 62/32/126/0/0 |
| Historical larger, 148,988 people | 32.851 ms | 62/32/135/0/0 |

Both larger cities retain 141 permanent household cells. The combined model's
utility, native save/rebuild and banking checks finish one named call at a time:

| Scope | Checks | Result |
| --- | --- | --- |
| Planner ae8aad170, with the fixture mends above | HousingChoiceCheck, HousingCheck, HousingDoorCheck, HouseholdCheck, HouseholdFlowCheck, BuildingDataCheck, InvestCheck, WaterCheck, SubstationCheck, SaveFileCheck, ReadPathCheck, ReloadCheck, DenominationCheck, CardCheck, MortgageCheck | All pass; exact comparisons and all money/conservation checks retained |
| Same planner | ScaleCheck | 3, one HT1-H free-growth premise; six windows, all evaluated TRACK/count/money laws pass |
| Same planner | LongPlaytest | 3, the one inherited CL7 map-ring line at m2400; all money fixtures pass, no shelter finding |
| Combined 17abb80ab | HousingMarketCheck, WaterPowerCheck, WaterCheck, SubstationCheck, SaveFileCheck, ReadPathCheck, ReloadCheck, MortgageCheck | 0 each; WaterPowerCheck 41/41 |
| Combined 17abb80ab | CardCheck selection | 3 only for the three BuildCardCheck/FS12 staffing premises; CardCheck itself has zero failures and exact native continuation passes |
| Combined 17abb80ab | --fast | 0, zero firm/199 soft Stale findings |

The combined reload compares 821,235 history numbers in 336 series after the
autosave, then 185,156 save numbers exactly. Its log also retains an actual
post-success warning: the Earth scratch city's asynchronous autosave history
rename fails after "All checks passed." The tested month-1229 autosave/history
and all continuation comparisons completed successfully before this message.
ReloadCheck.main calls System.exit without draining all Earth/twin saves;
TempDirs installs its deletion hook at shutdown. This ordering is consistent
with a scratch teardown race, not proof of an exclusive cause. A harness
lifecycle follow-up should drain its pending saves before exit. The warning
is disclosed for the saves review; no save comparison is removed, no failure
line is pinned for it, and no production save workaround is introduced.
Raw evidence remains target/RES3-PW1W-ReloadCheck.{log,txt}.

The pre-integration check evidence is target/RES3-planner-<name>.{log,txt}
(HousingMarketCheck is target/RES3-planner-mend.*); the final integration
evidence is target/RES3-PW1W-<name>.{log,txt}. All temporary observations are
absent from source and compiled classes. Each named invocation uses the shared
Earth/world files, cbs.parallel=1 and scratch/TempDirs city files. No full
local pick or save slot in the player's data directory is used.

tools/pick.sh origin/main HEAD selects ALL 248. Its review line is
"needed - saves: DataSave.java, +SAVE_FORMAT, +OLDEST_LOADABLE_FORMAT".
The changed quotes/orders also owe a stranger's money review. The provisional
ship record retains only the diagnosed ScaleCheck/HT1-H and three
BuildCardCheck/FS12 rows; HousingDoorCheck's FS11 row is removed because its
opportunity, owner funding and 899 cash identities are green. The ship file
passes tools/known-reds.sh. No F1 exception or outside attribution is requested
for the new seeds: seeds 8 and 12 remain RES3's own mended findings.

This checkpoint goes back to REV-RES3 before another ensemble, as ordered
when the model changes. The new final 80 runs against docs/ensemble/0.9.192.md
remain owed on the reviewed combined model, including seed 9 at m605 and all
new F1 seeds, with rents, affordability and growth reported. All three earlier
raw FAIL verdicts remain recorded above. No dry run has started, no kept
dry-run directory is claimed, and the provisional readiness marker remains.

## REV-RES3 SHIP and the three P2 folds

REV-RES3 returns SHIP on b948e245e..ae903ee4b, no P1: the backlog preserves
the funding gates and creates no money, the fixture conditions are caused,
the FS12/CL7 premises and main's teardown lifecycle are accepted, and the
PW1-W merge holds. Its three P2s are folded without moving the model:

- InvestCheck's wage/work identity no longer reads orderPriceMultiplier as
  its expected multiplier. A separate fresh city has no queued site or
  priority work, the same explicit wage ladder and tax rates, and either no
  builder month or enough sites' output that all ten Houses stay below
  BuildersShort.KNEE_MONTHS without foreign crews. The expected premium is
  exactly one. Every existing tax, material and settlement identity and its
  strict bound remains; the played city's later billing/save cases remain.
- HousingMarketCheck retains the eighty-door lower bound, requires
  quantityNeeded to be the smallest whole six-door recipe covering eighty,
  and requires the actual plan quantity to equal it: fourteen projects,
  eighty-four doors. The smaller head-based gap cannot inflate the order.
- Main 86359b9d4 is merged at f33363857. Only QUEUE.md changes, explicitly
  naming the Rail Spur loss-first staffing case under queued FS12. The three
  ship-file rows keep that owner.

InvestCheck and HousingMarketCheck both exit 0; --fast exits 0 with zero
firm/199 soft findings. Evidence: target/RES3-P2-{InvestCheck,
HousingMarketCheck}.{log,txt}, target/RES3-P2-fast.txt. The refreshed catalogue
has the same populations, mix, cohorts and 141 permanent cells. Its measured
months are small 49.316 ms, present-day 45.067 ms, present-day larger 46.302 ms,
historical 36.216 ms and historical larger 37.092 ms. No additional money/save
review is owed under the explicit P2 go: only the harnesses, reports and main's
ownership record move. The final-model ensemble runs next against 0.9.192.

The final run is RES3-planner-final-20261123, pushed snapshot 2d9a79aea,
against docs/ensemble/0.9.192.md with no extra JAVA_OPTS. Gatebox accepts it
at /home/jerus/cbs/tools/2d9a79aeafeb004bdad67b532fdf4a929b1ec1a2/target/ensemble/RES3-planner-final-20261123,
queued behind FC2's dry run under the runner's gate-first rule. Its controller
log is target/RES3-planner-final-ensemble.txt. No laptop fallback is used.

## Final ensemble: all M1/F1 criteria green

RES3-planner-final-20261123 finishes 80/80, no missing or short run, on
the pushed 2d9a79aea snapshot (0.9.193), gatebox, OpenJDK 21.0.13, eight
jobs. It exits 0: PASS, hard 10/10, total 19/25 with one vacuous and one
retired criterion excluded. Beside it 0.9.192's recorded raw score is 20/26,
hard 6/10. Every policy has zero failed/unread audits and zero findings in
all sixteen runs. Pairing is 64/64. No known-class exception is used for a
single run. The earlier three raw FAIL ensembles remain unchanged above.

The complete baseline is committed as [RES3](../ensemble/RES3.md), with
publisher version placeholders only in its heading and model introductory version, and the raw score,
summary JSON and provenance preserved. The residential comparison is appended
there beside the scored results: final populations, last-120-month real paid
rents, the paid-rent/average-worker-wage comparison, and the exact seed-9
month-605 rows. The ratio is descriptive, not a household disposable-budget
share; actual eligibility remains the matching checks' hard constraint.

Late real rents are higher than baseline in every policy. Paid rent divided
by worker wage moves AUTO2 8.44% -> 9.07%, ZERO_SLACK 10.08% -> 10.29%,
ZERO_HOT 9.97% -> 12.39%, SHOCK 8.86% -> 11.25%, HIGH 9.67% -> 12.54%.
Final population medians are 4,431,240.5 / 2,188,964 / 5,805.5 / 30,798.5 /
15,385.5 against 4,649,724.5 / 2,883,284.5 / 7,632.5 / 67,498 / 32,356.
T2 passes for AUTO2, ZERO_SLACK and ZERO_HOT, and fails for SHOCK and HIGH.
T1 still passes its stated bound, but only 15/16 AUTO2 cities reach 40,000,
median m977 against baseline 16/16 and m747. Seed 4 ends at 32,259, far
below baseline's smallest AUTO2 city, 2,459,618. That growth outlier belongs
in the comparison and is not attributed to an outside class.

The other changed informational verdicts remain explicit: AUTO2 A1 fails
(2.89% median inflation, eleven of sixteen in band); HIGH R2 fails (0.47
unemployment points versus its 0.50 bound); HIGH R3 fails (three seeds above
one inflation point); ZERO_HOT H2 is vacuous because there is no burst to
stop, with the scorer retaining that the numbers alone would fail.
★ Keep the reviewed costs, budget eligibility and real funding rules, and
publish these informational rent/growth outcomes under contract section 4.
The comparison covers the full RES3 catalogue and match; it does not isolate
one price-rule change. No calibration or model change follows this run.

At seed 9 m605, AUTO2 is 17,841 people, mean paid rent 0.164557612 and
average worker wage 1.358492740, with audit=1 and new/distinct findings=0/0.
The baseline witness has 21,605, 0.155366776, 1.089396160 and a homelessness
finding. ZERO_SLACK, SHOCK and HIGH also have passing audits and empty
findings at m605; ZERO_HOT ends at m402. Seeds 3, 14, 8 and 12 finish with
no findings in any policy. All 302 rows of the two prior native AUTO2
replays (8 m550-700, 12 m850-1000) match the final ensemble's population,
index, rent and GDP at CSV precision, zero differences.

Evidence: target/ensemble/RES3-planner-final-20261123/{score.md,baseline.md,
summary.json,snapshot.txt,progress.txt}; target/RES3-planner-final-ensemble.txt;
target/RES3-planner-final-{readings.txt,rent-readings.json,seed9-m605.json}.
The gatebox run path above is an ensemble, not a dry-run reuse directory.
Main is still 86359b9d4 after the final fetch. The model remains 2d9a79aea;
only the baseline, notes and ship record follow it. Pick ALL 248 still owes
the dry run, which is requested from the orchestrator after these records
are pushed. No dry run has started; the provisional marker stays until the
explicit go and a green kept dry run.

## First ALL dry run: stopped, slot released

The orchestrator grants the dry run at fde7db7b9. Main remains
86359b9d448e95fe1a5632a16d075c3f1d0b14d2, so no further merge precedes it.
The gatebox run is /home/jerus/cbs/runs/cbs-gate-RES3-xIpD5dJW, copied to
C:/Users/Jerus/AppData/Local/Temp/cbs-gate-RES3-xIpD5dJW. These are retained
red-run evidence, not a reuse claim. Branch commit:
fde7db7b9411feb6dfa1832f240dc8f3db595d4e. Local controller log:
target/RES3-dry.txt. No process or command is run inside the kept clone.

The gate exits 1, 2026-11-23 12:06:09 UTC: 247 harnesses ran in 1,289 s,
204 passed/43 failed, 38 harnesses with 182 unnamed lines (including exits
and summaries; some harnesses have both known and new lines). Stale does not
decide this run; the suite stops first. The slot is released by bus at
08:10:42 Halifax. The provisional marker remains; no ready message is sent.
The nine-minute foreground watcher intervals expired normally while the gate
continued; the controller and gate both finish, no process is abandoned.

The initial trace distinguishes owned data changes from unclassified laws:

- EquipmentCheck's old House is now billed to RE, so it cannot be its
  unbilled-building example. A city's Gravel Road has positive draw and no
  utility invoice; the unchanged zero-cost/zero-steam assertions use it, with
  the unbilled premise asserted explicitly.
- ResearchCheck's four newly dated tower rows are the catalogue's 1931/1969
  dates. All fourteen residential rows must retain their declared form year;
  every other non-code/non-special row retains the old undated/1900 rule.
  DiscoveryCheck and ResearchViewCheck use the actually undated Gravel Road
  for the original undated case; the year-dated Quarry and all known/from-
  research/unknown/origin assertions remain. These are being checked alone,
  not passed to a sweep.
- A diagnostic-only AutoBuildCheck run isolates section 2, changing no model
  input. Its native callback at m37 reads 3,112 people, annual GDP
  2,925.739475548268, cash 57,227.47701054205 and debt ratio 0. Full builder
  output is 302 and site output is 0. Paved Road, Walk-in Clinic and Childcare
  Centre are all HELD with cut BUILDERS. The helper's WG2 comment records
  five depots calibrated at 3,902 people/output 418, just past repairs. Its
  original full 600-month operational/save laws pass and place 28 orders;
  this trace does not establish all 38 short-fixture failures' causes.
  Evidence: target/RES3-dry-AutoBuild-trace.{log,txt}. The observation is
  restored byte-for-byte before accepted checks; its scoped failure is a
  diagnostic, not a green full-harness result.
- LandmassCheck's total builder points do not establish the demolition's
  own share: it receives 15,754 total points against 7,778 initially owed
  without finishing in 129 months. Allocation and competition still need
  tracing. The same warning applies to other work/pending-order premises.
- Currency and other money comparisons remain unclassified. Their original
  laws and tolerances are kept; neither a sweep name nor a different played
  city is an attribution by itself.

The orchestrator supplies FC2's red run, e90eb0b93,
C:/Users/Jerus/AppData/Local/Temp/cbs-gate-FC2-0uEcU2DO, and asks whether
the currency cases share a premise. Fourteen red harnesses overlap:
BankCheck, CallCheck, CarCheck, CentralBankCheck, CurrencyCheck,
DeathRecordCheck, DeskCheck, EssentialWorksCheck, FundCheck, FundLedgerCheck,
GoldStandardCheck, OrderDoorCheck, RailCheck and ShortSellingCheck.
GoldStandardCheck explicitly fails its no-top-of-month-M0-move premise in
both: RES3 has edge owed 34.18; FC2 has gold due 31.66 with zero reserves,
and its mid-cover premise already fails. RES3's before-top cover cannot be
used for its struck-rate equality; FC2 additionally loses its Gold fixture.
These are related fixture/phase assumptions, not an exclusive common cause
or a waiver. CurrencyCheck's cumulative defence comparison fails in both
with different figures and still needs its actual-flow trace. DeskCheck's
premises differ (unchanged spare for paper here, fixed-fee coverage there).
The overlap and evidence are mailed to the orchestrator at 08:25:29, with
the recommendation that FC2 owns common money-fixture mends to avoid two
builders editing them together. RES3 leaves those fourteen files untouched
pending coordination while its own catalogue checks proceed.

No production model, money/save check, threshold or ensemble score is
changed in this trace. The final monetary ensemble remains the passing
2d9a79aea model; another dry run requires a new slot go after every unnamed
line is mended or honestly named with its queued owner and cause.

The four catalogue/utility assumption repairs all pass independently:
EquipmentCheck, ResearchCheck, DiscoveryCheck and ResearchViewCheck, each
exit 0. The first compile caught a remaining local `house` reference in
ResearchViewCheck's separate lab example; that example still tests House,
and the undated-origin example now tests Gravel Road. No threshold changed.

The ChildcareCheck.town trace is now a native-main comparison, not a guess
about PW5. Native 0.9.193 at 86359b9d4, with the identical observer and
36-month fixture, ends at m37 with 4,016 people, 2,415 workers, construction
fill 0.7279804650063673, full output 431 and site output 87. RES3 ends with
3,112 people, 1,865 workers, fill 0.5029633156194911, full output 302 and
site output 0. Both have exactly 344.05000000000007 repair points, including
7.5 for the same 900 Houses; the entire standing-stock lists match and
neither has a Substation. Road ratio is 0.35 in both; health work ratios
are 0.7833219508408702 and 0.7934365755180257 respectively. RES3's housing
choice changes the people and staffed crews; PW5 has not added a repair
load to this fixture. Raw diagnostics are
`target/RES3-native-builder-trace.{txt,log}` and
`target/RES3-dry-Childcare-trace.{txt,log}`. These are one-fixture observer
runs, not full ChildcareCheck passes. The native scratch worktree was
restored, checked clean and removed after its process ended.

★ Cause this fixture's intended positive site output with physical inputs:
start with its five depots, add paid standing depots while the actual
staffed output leaves nothing after repairs, play a labour month after
each addition, and fail the fixture if twelve additions cannot cause it.
Do not repick a fixed count or change the service, funding or payback laws.
This harness-only mend is awaiting the full named checks below; it does
not change the final ensemble's production model.

The LandmassCheck observer reproduces both demolition reds. The standing
terminal owes 7,777.8 points; at its old 129-month ceiling the fixture has
counted 15,754 whole-city site points, but the demolition has received only
7,666.487358457717. Its queue now also includes C04, Compact House,
Duplex, other homes and business sites. For the shell, the initial owed is
47.75, and the second month counts 99 site points while a new C04 site gets
92.99986942494814 and the demolition gets only 6.000130575051852. This is
the endogenous orders breaking the wait's assumed bound on later work;
the demolition does advance, and the ground-release rule has not yet
fired. Raw: `target/RES3-dry-Landmass-trace.{txt,log}`. This observer run is
diagnostic, not a green check. The queued mend is to cause a fixed queue
for these three demolition-only cities by holding private sectors and
bank branches before their first month; the deadline and the exact ground
and save assertions stay intact. Other LandmassCheck growth runs remain
live.

The diagnostic Landmass run was stopped after the section-6 causes were
captured, during section 13's separate 1,200-month full-file growth run:
only its verified own child PID 14176 (parent 35324, LandmassCheck) was
ended. Its exit -1 is a cancelled diagnostic, not a third model failure.
The observers were restored before the repaired named checks.

PriceLevelCheck's three new lines have a concrete same-level trace. Its
eight built-city children, six months each, all complete. At history index
3, the factor's unnudged child records sick rate .1833 and unhoused 26.62;
the other seven record .1825 and 26.09, including all three children at
the same factor nudged by +1e-12, -1e-12 and +1e-13. Every child has the
same live population vector `[0,92,92,92,92,92,91]`. The unnudged child
also rejoins the other seven's history readings at the next index. This
is not a split confined to different price levels. `parting()` explicitly
skips every `h.*` series, so it reports the later live split at month 5
and compares a history already parted by rounding. Raw eight-child
readings: `target/RES3-dry-price-trace.{txt,log}`. The temporary observer
is removed. Keep the invariant and its tolerances; name these three exact
history lines under the existing HT1-H rounding/onset sweep, with this
same-level evidence. The orchestrator gets the new case in the mends list.

Two more data assumptions are mended through their inputs, awaiting named
checks. PolicyOpenCheck moved the first textual `month` in a save; RES3's
earlier housing-market state has its own `month`, before DataSave's root
month, so the city stayed at its original date with no load failure. Set
DataSave.month through its setter before serializing, retaining the exact
loaded-month and policy assertions. TruckRouteCheck's supposedly empty
three-month founding imports 30 tonnes for new private construction. Hold
private sectors and bank branches before those months to cause the zero
bulk case; retain the exact zero bulk and three zero tally laws.

ZoningCheck likewise removed only the building named House, leaving the
other typed homes available to the displaced households, so its outside
count was zero and its unzoned-farm shortage premise was absent. Buy out
and demolish at least nine in ten buildings of every available residential
form (asserting each actual purchase), hold replacement home orders for
that month, and retain the positive-outside, staffing, farm-zone and tiny
zone refusal assertions. No production rule or funding gate changes.

The first full AutoBuildCheck after causing positive builder output places
its first advice card and passes the decades/reload laws, but leaves 19
failed assertions. The subsequent trace causes the remaining premises:

- The school town has 172 elementary pupils, below
  `FIRST_WORK_SHARE * 600 = 180`, so no first school is warranted. In the
  school-only cases, set the child cohort so demand is halfway between the
  first-work threshold and half the smallest school's places, retaining
  the old below-half-school condition. Restore the named cohort array,
  population and workforce through their existing model inputs. Assert
  the warranted demand explicitly; do not change its threshold, payback,
  funding or first-school assertions.
- A bond for two years of GDP yields only 202-203% debt, below the 240%
  default limit. Size the fixture's funding-page bond from twice
  `DEFAULT_DEBT_LIMIT` years of GDP and assert that it actually crosses the
  limit. The zero-limit/central-bank case separately causes a real advance
  by playing a month overdrawn and asserts that advance exists.
- The three-month default young town has no output. Give it the standard
  forty Houses and three Convenience Stores before its months; its actual
  recorded GDP is now 105,123 against screens' annualised 420,492, and the
  unchanged strict inequality and national-accounts identity both pass.

With those inputs, the full AutoBuildCheck suite passes every assertion.
`target/RES3-dry-mend-AutoBuildCheck.txt` reports suite OK; the final serial
driver records its exit after maps/Stale. The earlier 19-red run is a
failed fixture iteration, not a waived result. HydroCheck, IslandLinkCheck,
LandmassCheck, ChildcareCheck and AuctionCheck follow sequentially.

AutoBuildCheck, HydroCheck and IslandLinkCheck finish exit 0 on the repaired
town. IslandLinkCheck's generated-world case now places 16 orders, meets
ground shortage in 142 of its 240 months, and still orders no link, bridge
or off-home port; the Earth link and both kinds of crossings pass too.

The first frozen-queue LandmassCheck iteration clears the shell but fails
the standing terminal's fixture: no repair headroom is caused in this tiny
town, and it stalls 24 months after its first 437 site points. Do not lift
the stall or work bounds. Prepare its crews before the three demolition
scenarios: leave homes and construction available while adding paid
standing depots and playing actual labour months, with linked home ground
to house the workers; hold other sectors and bank branches. Stop only when
staffed site output exceeds the repairs of all three future terminals,
then freeze every private sector. The initial wait includes any work
already queued during this preparation, and no later private orders can
invalidate its bound. Other landmass growth cases still invest normally.

The focused section-6 diagnostic passes all three cases: the standing
terminal finishes in 19 months (9,096 site points against 16,102 initially
owed), the lone terminal in 17 (16,123 against 16,102), and the shell in six
(4,023 against 8,197). Every exact release, island placement and on-site
save assertion in the section passes. Raw
`target/RES3-demolition-prep.{txt,log}`; its temporary main shortcut is
removed. These are diagnostic results, not a substitute for the full
LandmassCheck now running in the serial final harness driver.

The other fixture mends awaiting that driver are explicit about the actual
catalogue and costs. CreditCrunchCheck trims all residential forms, larger
mixes first, retaining its exact MARGIN bound. BuildersFirstCheck funds the
private top-up's complete `doorPriceOf`, including ground, starting from
nonnegative cash, and asserts the private order was actually placed.
ForestryCheck holds unrelated private orders before the hand clearing so
new home construction cannot add salvage during its one-month accounting
comparison. TreasuryCheck retains its failed note fixture, prints the two
surpluses that size its notes, and stops that dependent section if a note
is absent instead of dereferencing null; it does not report a passed
rollover or netting law on missing paper. CurrencyRegimeCheck and
MonetaryCheck add read-only phase/rent reports beside their unchanged
assertions to trace the remaining played-city premises.

The remaining map/page/rail fixture lines are named individually, not as
suite waivers. PlanCheck's contrasting reuse geometry now retains 74 of
74 boxes with both algorithms, so the strict advantage condition is
absent (queued FS6). KeptCheck's additional three NEW lines are in the
queued FS5 sections: its held-rule holding set grows from 62 to 162 with
100 other building lots; its literal research reach sentence is false
while the preceding full card-to-model equality passes; and its effect
fixture has zero moved rows after the held month against three while
dragged, while the held thumb, node and text laws pass. The fund page's
other retention line and the 1,796.5 MiB heap line were already named on
main, not NEW here: `unnamed.txt` contains just those three new assertions.
The kept dry logs remain the authority.

HomeRailCheck's boundary uses 175,253 of 199,167 tonnes, leaving a city
share .898104529914984 for 26,626 tonnes, so it has not caused saturation.
Its interior fixture leaves 91,376 tonnes after boundary traffic, enough
for all 58,561 city tonnes, so share 1 is correct and the strict interior
premise (and its dependent check) is absent. HT1-H already names that
guarded played-city search; the mends list includes the saturation case
too. RailDepotCheck's sizing snapshot has 97,209 bulk tonnes and does not
meet its more-than-two-spurs premise; the computed count is zero, so the
subsequent zero dealing at zero siding capacity is correct. FS7 shipped;
the mends list asks that these two lines join HT1-H's played-city cases.
The ship rows scope each literal condition; no capacity, money, save or
retention comparison is weakened.

The full LandmassCheck now passes, including the prepared fixed-queue
demolitions, their saved on-site units and exact ground releases, every
live investment/map case, and the separate 1,200-month Earth run and its
audit. `target/RES3-dry-final-LandmassCheck.{txt,log}` reports suite OK.
No diagnostic shortcut remains in the source. The full check retains
`STAND_WORK = 2` and `STALL_MONTHS = 24` unchanged.

The first-dry counts distinguish the suite from its uncovered list: 38
harnesses are NEW against main, two already scoped by RES3's existing
ScaleCheck/BuildCardCheck rows. `unnamed.txt` therefore names 36 uncovered
harnesses, of which 14 are the agreed FC2 files. The disposition helper
`target/RES3-dry-disposition.py` reports those 36; the mends list also
includes the two previously named ones.

The serial pass finds PolicyOpenCheck and TruckRouteCheck green. The
follow-up fixes remain inputs/measurements, with their hard laws kept:

- AuctionCheck's repeated-ask bound called `AutoSale.asked` cash, but
  `noteSale()` supplies the offered face there. Compare actual net proceeds
  with the repriced cash gap and count of lots, and principal growth with
  actual face sold (the old bound used face offered), as the adjoining
  conservation assertions do. The first retry's receipt shows offered and
  sold face 168.276586140 against cash gap 74.9227378377 plus one lot
  84.1382930699; the cash comparison must account for the issue by reading
  net proceeds, not by lifting its allowance. Print each component. The separate
  already-over-limit case now sets the zero limit, so its existing funded
  paper actually exceeds it instead of assuming it exceeds 10% of GDP.
- BuildersFirstCheck observes `getPointsBuilt()`, reset at each actual
  advance and including the foreign crews. The previous post-month
  `getBuildingOutput()` is next month's local capacity, not the work that
  just built 28 homes. This keeps the every-month positive-work law.
- ZoningCheck restores the prior home-investment hold before its later
  save comparison. ForestryCheck reapplies the same unsaved harness holds
  to its loaded twin. Their new controls had otherwise differed between
  the live and loaded cities; keep the exact cash/stock comparisons.
- CreditCrunchCheck reaches room 13.4 after retiring 309 actual homes.
  The original one-house upper bound added one household of room, though
  its trimming loop removes two household slots per door. Measure the
  smallest actual positive room reduction from a single-door retirement
  and require the remaining room above MARGIN to be smaller than that
  quantum. Keep MARGIN, the all-form trimming inputs, every leverage,
  mortgage, money and unplaced-household assertion unchanged.
- TreasuryCheck's forty startup homes make the second annual surplus
  positive (180.25) while the first is still zero. Step the three producing
  twins together until a positive trailing-year surplus is actually
  caused, bounded by another year, assert it, and only then size the first
  note. Do not manufacture a surplus or silently skip a failed note case.

CurrencyRegimeCheck's read-only reports reproduce all six new lines.
After the first month the gap is .0641459260050 against BREAK_GAP .10;
hazard is still .001 and the held peg has hot stock 2171.03138642, so its
larger-gap predicate is absent. The purchase-save fixture at m40 has gap
-.0763558615137, edge owed 450.353060385 and held=true, but doubt=0. The
hot-money save at m40 has gap -.0505131416061, edge owed 296.720373641,
stock 2323.19882352 and 2.32552434787 leaving on the previous .001 doubt,
but no doubt for the next month. Its loaded twin reports the same negative
gap, zero doubt, edge and stock. The subsequent stay/leave contrast and
positive-departure assertion require a positive source doubt that the
save never held. These six exact fixture/phase lines join the proposed
HT1-H scope in the mends list (FS10 shipped). No FX rule or save equality
changes; the source reports and assertion lines are in
`target/RES3-dry-final-CurrencyRegimeCheck.{txt,log}`.

The later full checks finish green for BuildersFirstCheck, ZoningCheck,
ForestryCheck, TruckRouteCheck, PolicyOpenCheck and TreasuryCheck.
Forestry's exact continuation now has city cash 30544.948813 on both
twins, identical cut/forest/sector cash, and its money residual is
1.63e-14 of what moved. Treasury's producing twins wait for the actual
positive annual surplus before issuing their two notes; all note,
netting, rollover, currency and conservation assertions pass. Raw final
results are the `RES3-dry-retry-*` files, except the already-green
PolicyOpenCheck and TruckRouteCheck in `RES3-dry-final-*`.

CreditCrunchCheck's measured one-door quantum is initially absent
(`Infinity`): the played town has only multi-home forms to retire. Stand
one paid House at the trimming input, then measure its actual room
reduction. This causes the stated one-house condition rather than assuming
the catalogue still contains an old House. The unchanged bank and
unplaced-household laws already pass; the corrected fixture is rerunning.

The last AuctionCheck receipt confirms the unit correction: net proceeds
139.295743191 fit the repriced cash gap 74.9227378377 plus one lot
84.1382930699; offered and sold face are 168.276586140, exactly the
principal increase. The cash bound has not been raised. Its full audited
run is still completing, not yet reported green here.

Native main's MonetaryCheck control at 86359b9d4 is green (exit 0), using
the same JDK/data and only the read-only rent/component report additions
to its harness. No production file differed. Its 3/10/20/40 percent rows
have inflation 11.44/10.10/9.23/9.21 percent, a 2.231-point reduction, and
calendar-year timing noise .044/.025/.110 points, within .650. Family
rent per capacity at 3 percent goes .0112442786132 -> .0174763533338;
at 40 percent .0113796763528 -> .0158280937670. Rent component growth is
14.4974 versus 11.9776 percent a year, and groceries/services also slow
with the dial. Its fixed D$ is .6991; each model pins its own common
pre-treatment founding rate, so this is a native-model control, not a
claim of identical cross-model opening states. Raw
`target/RES3-native-policy.{txt,log}`. The scratch worktree was restored,
checked clean and removed after its process ended.

RES3's three monetary-curve lines are therefore an owned finding pending
diagnosis, not attributed to main and not entered as known reds. Its
3/10/20/40 percent rows are 6.46/8.64/9.16/8.15, with calendar-year timing
noise .308/1.173/2.269 points. Business borrowing still falls (1,473 at
3 percent versus 768 at 10), but higher-rate rent growth and other
feedbacks can oppose that channel. The expanded component report is
running on RES3 too. No rent, price-index, credit or macro calibration is
changed to make the curve pass.

AuctionCheck now finishes exit 0, and CreditCrunchCheck finishes exit 0.
The latter has a real paid single-door input, retires 310 homes across
the catalogue, leaves room 13.4 above MARGIN 12, and measures the actual
one-door quantum as 2.00000000 households. Its leverage, mortgage, money
and no-more-than-one-unplaced-household checks pass. Final raw logs:
`target/RES3-dry-last-AuctionCheck.{txt,log}` and
`target/RES3-dry-last-CreditCrunchCheck.{txt,log}`.

The monetary component report localises the counter-channel: at 3 versus
10 percent, groceries slow from 10.6249 to 3.00265 percent a year, while
the rent component accelerates from 5.00121 to 10.6751 percent. Rent has
71.83-72.11 percent of the basket. Thus the borrowing/goods response is
present, but the rent component outweighs it in this fixed-FX toy. This
is an owned observed outcome, not a waived conservation finding.

Two predeclared input controls are being tried in a temporary curve-only
diagnostic, without changing any rate, direction, minimum-effect or noise
assertion: the model's wealthy founding cash (to remove fiscal borrowing),
and the same cash plus one paid standing specimen of each currently known
residential form, with subsequent private housing orders held (to remove
the transitional supply mix). Both retain actual matching, class rent
updates, utility/repair bills and every quoted/funded purchase. The first
control has zero public principal and advances at every reported close
but reproduces the original curve exactly: 6.46/8.64/9.16/8.15. Fiscal
borrowing is not the explanation. The stocked control is still running.
Raw outputs are `target/RES3-policy-wealthy.{txt,log}` and
`target/RES3-policy-stocked.{txt,log}`. This diagnostic has not been
adopted as a final fixture, and its temporary shortcut will be removed.

Neither diagnostic is adopted. The wealthy control reproduces all three
original curve failures with no public debt. The stocked control restores
direction (-2.50/-2.44/-2.21/-5.35 percent inflation) but fails the unchanged
timing-noise tests, with calendar-year differences 1.763/1.650/1.406 points.
Its failure remains a finding; no noise allowance is increased. Both
temporary controls and the curve-only shortcut are removed from source.

The subsequent model audit finds a concrete price-demand budget defect.
After emergency sharing pools poor singles into an existing shared cell,
the price-pressure loop applies the old target cell's budget to its larger
new household count. That can promise unfunded whole doors even though the
matcher itself uses the conserved current budget correctly. A validated
snapshot causes it: five old shared households at budget 20 plus twenty-five
singles at budget 1 form ten shared households at budget 12.5, preserving
125 resources. They occupy five half-price doors at ask 15, paying exactly
75. The old pressure loop reports ten whole-door demands; the current
budget can fund no extra whole door, so occupied pressure is five. A second
case with current budget 20 must still report all ten funded demands.

`HousingMarketCheck.mergedBudgetPressure` is first-red on the old algorithm:
10 observed, 5 expected; the resource, receipts and funded-20 controls pass.
Raw `target/RES3-pressure-first-red.{txt,log}`. The price-demand calculation
is extracted without behavioral change so the saved, resource-consistent
inputs exercise the same path as monthly clearing. ★ Use the actual
post-transfer budget for price pressure; retain pre-transfer metadata for
the independent planning projection. Do not apply a historical richer
budget to the new pooled count. The existing lease-length sticky-price
function and all price constants remain unchanged.

This is a new production model mend, so it requires a stranger's delta
review and a fresh final ensemble after the named checks. It adds no saved
field and changes no stored meaning: format/floor 64 remain the owed bump
from shipped 63. The existing old-format native refusal pairs and reload
comparisons remain required. HousingMarketCheck and MonetaryCheck are
running again before further attribution of the curve findings.

The pooled-budget regression and full HousingMarketCheck now pass. Both
historical and present-day catalogue runs, paid builder completions,
MoneyAudit/household identities, actual rent receipts and next-month native
reload comparisons remain green; the regenerated report is committed as
`RES3-catalogue.md`. Raw `target/RES3-pressure-HousingMarketCheck.{txt,log}`.
HousingCheck, HousingDoorCheck, HouseholdCheck, BuildingDataCheck, InvestCheck
and ReadPathCheck also finish exit 0 on this model. HousingDoorCheck includes
899 played months of the household cash identity. SaveFileCheck is still
running at this point.

MonetaryCheck reproduces its three original failures after the budget fix,
with exactly the same curve and timing-noise readings. The mixed rich/poor
sharing defect is real, but it is not the cause of that toy's counter-channel.
Raw `target/RES3-pressure-MonetaryCheck.{txt,log}`. No assertion or threshold
is relaxed, and no monetary line is entered in the ship file. A further
read-only report records each standing class's homes, occupied leases,
funded demand, ask, target, capital and carry at the measurement opening
and each calendar-year close; it changes no fixture input or model state.

SaveFileCheck finishes exit 0 too, including the genuine 0.9.192 format-63
save/history boundary and preservation of both refused files and the
running city. All seven checks in `target/RES3-pressure-core.sh` pass;
their individual raw outputs are `target/RES3-pressure-<Harness>.{txt,log}`.

ReloadCheck also passes in full (215 seconds): checkpoints through m1205,
the caused events at m1211, autosave m1229, and next-month history/save
comparisons all agree. The autosave continuation compares 821,235 history
numbers in 336 series and 185,156 save numbers. Its shutdown again prints
the already-traced native harness lifecycle warning while deleting its
scratch autosave-history folder, after all assertions pass; it is not a
changed comparison or a player save. Raw
`target/RES3-pressure-detail-ReloadCheck.{txt,log}`.

The corrected read-only class report completes and reproduces the three
monetary reds (`target/RES3-pressure-detail-final-MonetaryCheck.{txt,log}`).
At m49 the 3/10 percent towns have similar populations (285/293) and H55
capital quotes (4.8007804/4.7619382), but 58/12 actual Compact houses. Their
asks are .025161104/.039079114. By m84 their H55 stocks are 87/66; the
3-percent town also has 30 T100 homes where the 10-percent town has none.
Thus the dial changes actual financed supply and the occupied mix, not
only households' demand. The higher-rate class carry at m49 is actually
lower (.018494662 versus .022005625 for H55), so a rising interest floor
alone is not the explanation.

A third, single-variable control is declared from that observation:
hold new private Real Estate orders immediately after the common m25
founding, without changing its stock, pending orders, households, money,
prices, any rate or the measurement window. Keep actual leases, rent
updates, maintenance and every existing policy/noise assertion. This
isolates the newly observed supply response in the fixture's stated
steady-town premise, unlike the rejected all-form stocked town with its
changed founding population. If the control does not cause identical
standing stock through the measurement window or still fails the hard
assertions, it is not a completed fixture repair. Its temporary input hook
is restored automatically after the diagnostic, and is not final source.

That hold-after-founding control restores every noise bound, but inflation
is 11.32/10.61/10.90/11.02 percent: only .296 points across the range, so the
one-point minimum still fails. Nor does the control cause fixed standing
stock: the original paid housing queue continues to complete, at different
times across the dials. At m49 its House counts are 52/53 and H240 counts
13/14 at 3/10 percent. It is rejected as a completed repair. Raw
`target/RES3-policy-fixed-stock.{txt,log}`. The source hook is removed.

Complete that causal test by causing the actual proposed condition,
rather than repicking a seed, rate, window or threshold: the same two
40/20 House inputs are paid standing fixture stock at their original
founding months, and new private housing orders are held before the first
month. The fixture supplies their actual plot area and cost, as it already
supplies its Bakery/Diner/Boutique and hospital capital. No intervention
occurs after the common founding. A diagnostic assertion requires exactly
those sixty Houses and zero other RE homes at every reported measurement
checkpoint; all existing monetary assertions remain. This control too is
temporary unless it actually causes its condition and passes the laws.

The completed fixed-standing-stock control causes all 32 checkpoint
inventory predicates: exactly sixty Houses and no other RE homes, across
the four on-time and four month-late runs. Populations are nearly steady
(3 percent: 259 at m49, 273 at m84; 40 percent: 260/274). Every original
fixture, noise and direction assertion passes, but the one-point minimum
still fails: inflation is 7.42/7.47/7.16/7.30 percent, a .121-point effect.
Calendar-year timing noise is .034/.033/.159 points, against the unchanged
.650 allowance. Raw `target/RES3-policy-standing-stock.{txt,log}`.
This control is rejected too; the automatic restoration removes its input
hook, extra fixture assertions and all changes to founding. Final source
keeps only the read-only component/class reports. There is no adopted
fixture change, model calibration, rate/window/tolerance change or
MonetaryCheck known-red row.

### Monetary calibration block after the first dry-run mends

The owned block is the original MonetaryCheck policy curve: native main
86359b9d4 passes; RES3 reverses the 3-to-10-percent CPI ordering, misses the
one-point 3-to-40-percent minimum, and exceeds the calendar-year timing
bound. Credit/goods transmission remains present, while financed housing
supply and its paid rent mix oppose it. Removing fiscal borrowing did not
change the result; alternative stocked/fixed-supply controls did not pass
all the retained laws. Even the genuinely fixed, nearly steady sixty-House
control misses the minimum. These are evidence, not replacement premises.

The brief authorizes calibrated starting inputs and actual rent/choice,
but does not decide a new CPI definition, a different minimum policy
effect or a new smoothing/choice rule. Changing those solely to turn the
curve green would not mend the finding. The next needed call is a focused
money/macro review of the measured rate-and-housing response and a concrete
calibration direction that retains the laws. Recommend keeping the current
assertions and this block until that diagnosis is settled, rather than
assigning these owned failures to a fixture-sweep row. The pooled-budget
price-demand defect has its separate first-red/fix and still requires a
stranger's delta review. No new final ensemble or second dry run is claimed.

First-dry disposition: of the 36 uncovered NEW harnesses, fifteen have
green input/measurement mends, fourteen are the agreed untouched FC2 files,
six have individually traced fixture-premise rows proposed for their
queued owners, and MonetaryCheck is the remaining owned block. The two
previously named ScaleCheck/BuildCardCheck cases bring the total NEW count
to 38. HT1-H's additional CurrencyRegime/PriceLevel/HomeRail/RailDepot scope
still needs the orchestrator's queue amendment; no branch edits QUEUE.md.
FC2 must ship, be merged and have its fourteen shared checks verified before
another slot request. The provisional ship marker stays in place.

The restored final source passes `./batch-check.sh --fast` (exit 0): compile,
generated maps, and zero firm Stale findings (199 soft). Its MonetaryCheck
bytes equal the pre-control backup; only the read-only reports differ from
the reviewed source. `git diff --check`, LF checks and
`sh tools/known-reds.sh RES3 docs/queue/RES3-ship.md` pass. Raw fast output:
`target/RES3-block-fast.txt`. This is a blocked checkpoint, not a mends/SHIP
or ready claim. The earlier 80-run report remains explicitly a snapshot of
2d9a79aea, not validation of the new pooled-budget mend.

### DX-RES3 monetary direction and the caused mends (2026-11-23)

The orchestrator accepts DX-RES3's like-for-like CPI diagnosis and orders
that defect mended, then the missing funded-demand channel, preserving
MonetaryCheck's fixture, rates, calendar window, minimum effect and noise
laws. DX mail `20261123-135707-894-DX-RES3-5da0`, call
`20261123-135800-281-orchestrator-be0f`. Main's records-only LP-G1 ordering
commit 5eae9afd4 is merged before the mends; FC2 has not shipped.

The CPI regression fails on the previous calculation: eight studios at
20 and two family homes at 10 open at 180/24 = 7.5 per capacity; switching
the occupied counts to two/eight with unchanged class bills incorrectly
reports 120/36 = 3.3333333333. A subsequent ten-percent rise in both class
bills should read 8.25, but reads 3.6666666667. Raw
`target/RES3-like-first-red.{txt,log}`. These are model snapshot inputs,
not an adjusted CPI assertion or a different played-city premise.

★ Chain the typed rent level on the preceding month's occupied doors at
each class's previous and current actual paid bill per door. Open at the
first paid unit value. Carry forward a class without a paid observation;
a newly occupied class joins the next basket. `sold()` strikes the level
before replacing the existing saved lastTrade/tradeQuantity fields, so
there is one update at the actual monthly sale, none on a getter/load.
The new scalar `HousingMarket.State.rentPriceIndex` travels through the
existing DataSave housing snapshot and native restore; validation is
atomic, reset clears it, and redenomination scales it once. The branch's
unshipped format/floor 64 already covers this stored-meaning change, as
the orchestrator specifies; no conversion or additional bump is added.
Actual leases, weights, receipts, bills and money settlement are untouched.

HousingMarketCheck passes the unchanged-mix-price regression, actual
receipt controls, ten-percent comparable price rise, pure reads, saved
level restore, redenomination, invalid-level atomic refusal and missing
observation carry-forward. Full historical/modern paid build and native
continuation cases pass too. `target/RES3-like-HousingMarketCheck.{txt,log}`.
With this defect alone MonetaryCheck exactly reproduces DX's probe:
6.77/2.99/4.52/2.47 percent; 4.299 points across the range, with the 20/10
ordering and two noise assertions still red. Whole-window timing offsets
are .000/1.254/.213/.094; calendar-year maxima .054/1.096/3.093.
Raw `target/RES3-like-MonetaryCheck.{txt,log}`.

The budget audit finds the omitted link: housing reads available income
plus one year's liquid-savings support but never the existing ex-ante
saving response used by household spending. The new regression starts
with resources 10 and a current rent 4; SPEND_FLOOR .5 should leave a bid
7. The old path still returns 10, admits the same twenty bidders at an ask
between the two intended budgets, and leaves the price target unchanged.
All four assertions fail first (`target/RES3-bid-first-red.{txt,log}`).

★ Apply the existing spending response to resources above the committed
funded rent, with available resources as the cash ceiling. Preserve a
fundable current lease, every fixed fee, subsistence food and HC1's current
card minimum; a stimulus cannot invent credit above that ceiling. Incoming
workers have no current commitment. Live clearing and incoming forecasts
receive `Game.spendFactor()` for this month's ex-ante real deposit return,
not the previous plan's stored workspace. Bare model calls retain the
existing factor-1 default. No new dial, saved budget axis or cash movement
is introduced. The budget-to-affordable-demand-to-target regression now
passes, as do the cash ceiling, current lease and untouched savings controls.

The full HousingMarketCheck remains green. MonetaryCheck with both mends
reads 6.77/2.99/4.11/2.26 percent, 4.515 points across the range. The higher
20/40-percent dials now lower inflation through the restored bid channel,
but the 20/10 ordering and both timing assertions still fail. Whole-window
offsets .000/1.254/.095/1.426; calendar-year maxima .165/1.308/3.174.
Raw `target/RES3-bid-<Harness>.{txt,log}`. SCARCITY_ELASTICITY stays at the
existing value 1.0; no coefficient is fitted to the harness. A read-only
monthly class/CPI/bid trace is running to determine whether the remaining
laws fail on the lender-held supply timing alone, as the call requires.

The monthly diagnostic reproduces those same three reds with no changed
fixture input (`-Dres3.housingTrace=true`, a read-only reporting switch).
Raw `target/RES3-bid-flow.{txt,log}`, parsed rows
`target/RES3-bid-flow.json`, event table
`target/RES3-bid-flow-summary.txt`. The remaining price comparison is now
in the expected direction at a common supply: at m72, 10/20 percent have
77.0897/64.6021 funded Compact demands and capital 5.0411358/4.9877745,
with carry .020302509/.019131820. Both demand and holding costs are lower
at 20 percent. Its stock, however, is only 40 Compacts against 52, so its
target is .038935015 against .036121694. Holding its stock at the same 52
doors would give .029950012 from its own observed capital and demand,
below the 10-percent target. This is a direct scarcity calculation, not
a changed simulation or a new target law. The actual 20/10 rent-component
growth is 8.0790/3.4575 percent in year two, and 7.8860/6.0662 in year three.

The worst timing pair is 40 percent. Its first new Compact completions
after the common 14-door stock occur at m64 on time (five doors), but m66
on the month-late run (four doors). At m66 it is 27 versus 18 doors; at m72
50 versus 43. Capital at m72 is nearly identical, 4.9994720/5.0008834,
but target rents are .027682695/.033510600 and asks .040079686/.042439961.
The different completion path continues into year three: m84 stock is
66/70 Compacts and 28/26 Duplexes, with CPI 1.030045117/1.071200417 and
chained rent .007047656550/.007444763787. Year-three CPI inflation is
2.405991/5.580067 percent, the 3.174-point timing failure. Year-two maxima
are 1.308 points. DX's lender-held supply mechanism remains visible after
both defect mends; the resulting population and bid feedback is recorded,
not assumed identical across different construction paths.

Following call 4, stop monetary model changes here and report these
remaining supply-timing figures for the orchestrator's premise decision.
The corrected index and funded saving channel have caused regression tests
which fail before their respective fixes and pass after them;
the full macro assertions remain red, unwaived and unassigned to another
queue row. No scarcity exponent, lease speed, lender gate, fixture, rate,
window, minimum effect or tolerance is changed. The pending conservation,
native save and reload checks are completing before that checkpoint is
pushed; no new ensemble or dry-run slot is requested.

All eight serial invariant checks now pass on these model mends:
HousingCheck, HousingDoorCheck, HouseholdCheck, BuildingDataCheck,
InvestCheck, ReadPathCheck, SaveFileCheck and ReloadCheck. The housing cash
identity closes for 899 months. The genuine shipped format-63 refusal and
both-file/running-city preservation checks remain green. The long reload
finishes in 53 seconds with 820,770 history values in 338 series and
106,202 saved values equal in its autosave continuation. Raw
`target/RES3-dx-<Harness>.{txt,log}`. HousingMarketCheck's complete catalogue,
money, native round-trip and new caused regressions already pass under
`target/RES3-bid-HousingMarketCheck.{txt,log}`.

`./batch-check.sh --fast` passes (exit 0), with generated maps and zero firm
Stale findings, 199 soft; raw `target/RES3-dx-fast.txt`. Authored source and
queue notes pass `git diff --check`; generated month-order references keep
Maps' existing Markdown hard-break spaces. No extra JVM or fixture probe
remains running. The three MonetaryCheck lines stay owned and red; the
provisional marker and the deferred combined review remain in place.

### Lane resumed: reality, several towns and the format-65 boundary

Jerus's 2026-11-23 23:33 answer is the authority for correcting the old
universal adjacent-rate law: "even reality higher interest rates doesnt
always equal liwer inflation". His 23:55 direction is "sometimes its as
simple as check reality". The orchestrator's revised 23:42 instruction
replaces the earlier B wording: retain the overall effect, assert the
housing supply channel separately, and assess ordering/timing over several
towns or a longer window without hiding an actual supply response as noise.
The lane go is `20261124-015023-063-orchestrator-5dec`.

The evidence supports that correction. [Jorda and Yalcin, FRBSF Economic
Letter 2024-23, September 3, 2024](https://www.frbsf.org/research-and-insights/publications/economic-letter/2024/09/when-is-shelter-services-inflation-coming-down/)
explain the opposing effects of higher rates on housing demand and supply:
financing costs reduce new construction's returns, while leases reset
slowly. Their US discussion through July 2024 shows why shelter inflation
can remain high after other components cool. [Kmetz, Louie and Mondragon,
FRBSF Economic Letter 2023-19](https://www.frbsf.org/research-and-insights/publications/economic-letter/where-is-shelter-inflation-headed/)
document that divergence after the 2022 tightening as market asking rents
slowed before the CPI shelter component. These observations establish
persistence and timing, not a causal estimate from the dates alone.

[Dias and Duarte, Federal Reserve IFDP 1248, May 2019](https://www.federalreserve.gov/econres/ifdp/monetary-policy-housing-rents-and-inflation.htm)
find that rents can rise after a contractionary monetary shock, while
other major CPI components decline or are unresponsive; shelter accounts
for much of the measured price puzzle. Their tenure mechanism is evidence
against a universal net-price sign, not a claim that RES3 already implements
HO1's own-versus-rent choices. The game's lender-held construction is the
financing/supply mechanism described by the 2024 FRBSF analysis. No model
coefficient is fitted to these papers or to a passing harness result.

★ Before the new measurement, fix world paths 0,1,2,3,4, all four original
rates, and both original hold dates. Keep months 49-84, the original seed-0
one-point effect bound, and the .650-point timing bound. The multi-town
ordering statistic is the mean policy/CPI regression slope over the whole
four-rate curve, plus the mean far-end effect; local adjacent inversions
remain reported as actual outcomes. Timing is the absolute paired mean
late-minus-on-time difference for each rate, over the window and in each
calendar year. Individual magnitudes remain printed. This measures the
timing of the aggregate estimate rather than cancelling an unreported
failure or selecting towns after observing them.

★ Observe accepted starts in a harness-only `HousingTown` subclass:
`buildFor` calls the real implementation once and records housing doors
only after it succeeds. The helper verified that BuildLog and the previous
stock differences are completions, not starts. No model hook, new saved
flow or inferred start from closing stock is introduced. The supply case
is predeclared as the original town's 3/10-percent pair: count accepted
housing doors from m25 through m48, then compare the same C01 class's
average posted rent during m49-60. Both halves are hard assertions.

Tracing was delegated first to a short read-only helper, then, under the
new contract 4 rule, to MemWatch `RES3-logs` and `RES3-log2`, each reporting
at most fifteen lines, with no source edits or JVMs. Committed memory was
63/67 percent before launches. The builder retains implementation, source
review, checks and commits. The helper sessions use an isolated checkout.

Main through R6f 0.9.196 (`94ba28823`) is merged. PW2 already reserved
156-166 for these housing forms and shipped power IDs 167-171, so no IDs
are moved: the merged catalogue has unique IDs through 171, nextId 172,
and the fallback preserves both catalogues. AllChecks keeps both batches'
harnesses; date checks keep both the housing and power rows; both independent
used-car reload checks remain.

R6f owns the shipped format/floor 64. RES3 therefore raises both to 65,
without conversion. A new real format-64 pair is captured from unchanged
shipped `94ba28823`, 0.9.196, by HousingFormat64Fixture after that tree's
`--fast` passed with an empty source diff. It plays twelve native months
after probeCity and writes the native scratch slot-10 pair at m31. The
compressed artifacts, raw hashes and provenance are beside the older
pairs in `src/main/resources/fixtures/res3-format64/`. SaveFileCheck's exact
one-below-floor boundary now uses that pair, with the same byte/history,
fresh-city and running-city guards; the real format-62/63 checks remain.

The first five-path probe exposed a fixture-control error: calling the
old town "seed 0" did not make its default path seed zero. Founding's
actual default is 48213. The helper caught that the first probe replaced
the original town rather than retaining it. Correct it by explicitly
including `Founding.defaults().getPathSeed()` first. Earlier MonetaryCheck
references to its original "seed 0" mean this default town; ensemble path
numbers are unaffected. All five pilot paths and their adverse outcomes
remain in the record, not silently dropped. The pilot's four reds and
per-path values are in the immutable helper mail
`20261124-021155-604-RES3-log2-9efb` and
`target/RES3-reality5-helper.txt`, with its wrapper output
`target/RES3-reality-MonetaryCheck.txt`.

★ Use the existing ensemble's sixteen consecutive world paths 0-15 for
the broader paired estimate, retaining all pilot paths, and the original
default town for its unchanged effect/supply tests. This is a sample-size
increase, not a choice of passing towns; it is declared before the new run.
The pilot's pooled yearly timing sensitivity was 1.543760 points, so five
towns did not establish the retained .650-point bound. The expanded sample
keeps that bound pending measurement. Do not impose a new universal
one-point effect on every world-path population: the original bound is
asserted on the original town, while the whole-curve slope is assessed
over the fixed broader sample and all per-town effects are reported.

The corrected 17-town calibration run preserves the actual default town:
its effect is still 4.515 points, and the supply assertions now observe
137 versus 24 accepted doors, followed by C01 average asks
.02486437563/.04124895851. All founding, currency, crew, calendar and
fixed-basket premises pass for every path. Pooled whole-window timing is
.356534 points; the three annual maxima are
.2011492203/.5114997838/.9612071193. Only the pooled third-year timing
bound remains red. The pooled policy/CPI slope is -.000676, but individual
paths have both positive and negative total effects, as the revised law
allows. `RES3-log3` reports the complete results in immutable mail
`20261124-022255-747-RES3-log3-bf93`; raw
`target/RES3-reality17-MonetaryCheck.{txt,log}`.

★ Keep the old harness's approximately twofold calibration margin, and
choose upward quarter-percentage-point rounding for these two new
aggregate timing limits. The 17-town calibration therefore gives
.750 points for the whole-window estimate (twice .356534) and 2.000 for
an annual estimate (twice .961207). Preserve the old .650-point ordering
resolution and the original one-point effect bound. For ordering, compare
the pooled whole-curve slope across the 3%-40% range with that retained
resolution; do not assert an exact negative sign at arbitrarily small
scale or require every local pair to fall. Print the full mean curve.
These are calibrated harness measurement limits, not altered price,
credit or household-response coefficients.

Before seeing any new outcomes, choose the next sixteen consecutive paths
16-31 as independent validation. The final harness retains the original
default-town effect and supply cases, and applies the frozen aggregate
ordering/timing limits to those validation paths. Every calibration path
and every adverse outcome remains in the study record above; none is
relabelled a pass or selected away. The source default resets to the
actual default seed after measurement. No fixture intervention, new
month window, model adjustment or per-town noise waiver is involved.

The original calibration's .650-point ordering allowance is retained for
the pooled curve: scale its regression slope by the full 3%-40% rate span
and compare with that resolution, rather than impose an exact negative
sign below the measurement's resolution. This does not change the original
town's one-point effect assertion. The two newly calibrated timing limits
are frozen at .750/2.000 points before validation on paths 16-31; the
validation sample excludes the calibration paths. The original default
town is tested separately, not reused in the validation estimate. The
whole validation curve, paired timing estimates and every individual
town's outcome remain printed for review.

Calibration observations (percentage points; timing columns are each
town's actual responses, not limits on the paired mean):

| Path | Low-minus-high inflation | Largest full-window timing response | Largest annual timing response |
|---:|---:|---:|---:|
| 48213, original | 4.515 | 1.426 | 3.174 |
| 0 | -1.916 | .256 | 1.183 |
| 1 | 1.963 | .260 | .950 |
| 2 | 1.276 | 1.633 | 3.943 |
| 3 | -2.942 | .380 | 1.338 |
| 4 | 1.409 | 1.310 | 4.047 |
| 5 | 7.179 | .181 | 1.221 |
| 6 | .791 | .251 | .706 |
| 7 | -2.124 | 1.335 | 2.589 |
| 8 | 1.791 | .697 | 2.182 |
| 9 | -2.715 | .596 | 1.352 |
| 10 | .462 | 2.254 | 5.713 |
| 11 | -3.596 | .516 | 1.503 |
| 12 | .571 | .265 | 1.080 |
| 13 | -3.180 | .882 | 3.120 |
| 14 | 2.761 | .565 | 1.449 |
| 15 | -2.164 | .562 | 1.573 |

The independent validation on paths 16-31 passes MonetaryCheck (exit 0),
with no recalibration after observing them. Its mean 3/10/20/40-percent CPI
curve is 4.056172/3.093337/4.375457/4.046189 percent. The whole-curve slope
is +.010666, or +.3946 percentage points across the rate range, below the
retained .650-point resolution. The mean far-end reduction is only
.009983 points. That near-zero broader effect is a limitation of the
result: the retained one-point transmission assertion is on the original
town, whose curve remains 6.77/2.99/4.11/2.26 percent and effect 4.515
points. The multi-town result does not establish a universal negative
slope or a one-point effect in every town.

Paired timing sensitivity on the validation sample is .193845 points for
the full window and .130032/.329205/.774209 for its three calendar years,
all within the predeclared .750/2.000 limits. Individual towns can have
much larger real construction responses: path 29 reaches 4.313 points
over the window and 15.071 in one year. Those printed observations remain
actual supply/timing outcomes, not a per-town waiver or a claim that they
are small. Every path passes the same dial-only, pinned-currency, full-crew,
whole-calendar and based-basket premises. The separate original-town
supply assertions pass with 137/24 accepted homes and later C01 rents
.02486437563/.04124895851. Source cleanup after this run changes names,
formatting and output wording only, including replacing the old
MEASUREMENT_NOISE name with ORDERING_RESOLUTION; no statistic or bound
changes. Raw `target/RES3-reality-validation-MonetaryCheck.{txt,log}`;
the read-only helper's fifteen-line report is immutable mail
`20261124-024247-795-RES3-validation-770c`. Its session has ended.

After merging main, the named HousingMarketCheck, HousingInventoryCheck,
HousingChoiceCheck, HousingCheck, HousingDoorCheck, HouseholdCheck,
BuildingDataCheck, ReadPathCheck, ReloadCheck and ResearchCheck all pass
with exit 0; InvestCheck returns 3 for only main's known red. The typed
catalogue, paid builds, money/household audits and real reload comparisons
therefore run under the format-65 model, not yesterday's classes. Their
raw files are `target/RES3-resume-<Harness>.{txt,log}`. SaveFileCheck separately
passes the real format-64 boundary and its older native pairs (exit 0),
`target/RES3-format65-SaveFileCheck.{txt,log}`. The catalogue report is
regenerated after the merge, including its current small and larger city
integrated-month observations. The validation runner's subsequent
`./batch-check.sh --fast` also passes, exit 0.

Final CurveCheck, ExpectationsCheck, TimingCheck and WaterCheck each pass
(exit 0), one named harness per call. The final fast check passes after
all source cleanup, regenerating the maps with 0 firm and 199 soft Stale
findings. `RES3-close-logs`, launched at 69-percent committed memory, reads
all named outputs and confirms these results in immutable mail
`20261124-025240-784-RES3-close-logs-305d`; it has ended. Raw final outputs:
`target/RES3-reality-<Harness>.{txt,log}` and
`target/RES3-reality-final-fast.txt`. The InvestCheck red is exactly main's
HT1-H/FB3/RT2-L wage/rebate premise: index 1.158005 and both rebates zero,
so the required strict contrast is not caused. No new red is passed as
known. HousingCheck also prints an uncounted OBSERVED line because its
played town did not close homes leaving landlord debt; that observation
is not a new passing assertion. HousingDoorCheck still closes all 899
months' cash identities. Native refusal, current-format reload and the
atomic malformed-market guards all pass.

The final source pick against merged main `94ba28823` is ALL, 251
harnesses. The review line is "needed - saves: DataSave.java,
+SAVE_FORMAT, +OLDEST_LOADABLE_FORMAT". The combined stranger review also
covers money: the current-pool demand budget, the chained comparable-rent
CPI level, saving-sensitive funded bids and their native restoration.
There is no threading change. The revised monetary law, real boundary
pair and main merge belong to that same delta. The provisional ship
marker remains. Known-reds syntax passes with exit 0, including the four
HT1-H fixture classes awaiting the review's premise acceptance; no
MonetaryCheck waiver is added. The earlier ensemble is not validation of
this model. The fresh final ensemble against `docs/ensemble/0.9.195.md`,
the FC2 merge and its four named shared guards when it ships, and the
slot-authorized ALL dry run remain after the review's SHIP. No other job
or helper is left running at the review handoff.

### REV-RES3x P1: guard the surviving transmission across the declared towns

The review correctly rejects the earlier pooled ordering assertion as a
transmission guard: it admits zero and a positive response within .650
points, while the mean far-end effect was only printed. The original
town's 4.515-point effect does not establish a broader response. Keep that
original effect and both actual-starts/later-rent assertions; diagnose
paths 16-31 before changing the model or the law again.

MemWatch helper `RES3-P1-trace` runs native main `94ba28823` and RES3
`8fdb111b6` in separate detached checkouts, strictly serial named probes.
Its fifteen-line report is mail `20261124-033229-435-RES3-P1-trace-94a5`.
Both invocations exit 0: 272 path/rate/timing observations, with 1,904
founding, basket, crew, currency and calendar premises passing. The
portable probe and founding-body hashes match between models. Equal
fixture inputs do not imply equal month-25 states across different
models; each model's policy/timing twins have equal fingerprints. The
typed total-CPI mean exactly reproduces the earlier validation curve.
No model source or household/policy input is changed for the comparison.

Native main is adverse over the same window, not almost flat: its mean
total CPI is 5.793602/5.435019/8.231521/8.325781 percent at 3/10/20/40%,
a -2.532180-point far-end effect. Its common-basket non-shelter effect is
also adverse, -.074993 points, with twelve adverse paths. RES3's common
non-shelter curve is 4.527429/2.171282/2.117868/2.904979 percent, a
1.622450-point reduction. It has eleven positive and five adverse paths,
all retained (16, 19, 23, 25, 26 are adverse). Every path in both models
has a lower mean live spending-plan factor at 40% than at 3%: a falling
factor alone does not establish price transmission. Shelter offsets
RES3's surviving non-shelter response, while the total mean remains only
.009983 points lower. This is not a claim that every housing feedback
has been causally isolated. There is no lost non-shelter channel to mend
in RES3 on this evidence.

[The committed comparison](RES3-transmission.md) files native main's
coverage gap and every path's total/shelter/non-shelter effect. Main's
original town passes a 2.231010-point total effect while its broader
non-shelter mean is adverse; do not call that general transmission or
attribute it to RES3. The new price guard would reject that native curve.
Full portable source, entry-point patches, commands, unrounded data and
snapshots remain under `target/RES3-P1-trace/`. This is measured evidence
over the original window, not a new longer-run claim or a known-red
transfer. The FRBSF and IFDP sources and Jerus authority above still apply.

★ Guard a meaningful surviving price channel directly: the mean
common-basket non-shelter reduction across all sixteen declared paths
must be at least the existing TRANSMISSION_FLOOR, one percentage point.
This retains the original minimum effect size rather than calibrating a
new near-zero allowance. Also require the higher real saving return to
reach each town's live mean spending-plan factor. The existing
HouseholdCheck laws separately verify that this factor is actually
applied to grocery surplus, luxury and meals, including the live month.
Those quantity/application laws and the new price law protect different
parts of the channel; neither replaces the other. The original-town
effect, supply assertions, rates, fixture, window and timing limits stay.

★ Hold a genuinely common physical basket across policies, including
each services fee quantity. Policy twins can have different component
weights and nested fee shares at their links, so excluding the rent
component from each twin's own basket alone is not a clean price
comparison. Fix component quantities and every fee quantity from the
3% on-time twin; price that same bundle at both endpoints of each rate.
The common normalization cancels in the ratio. Assert a fixed link and
quantities throughout each run, and reconstruct each run's native CPI
change from its own observed quantities as a hard measurement premise.
No annual percentage changes are added as if they were CPI contributions.
Repricing both models with the identical native physical basket still
gives RES3 1.669167 points, so the positive result is not the choice of
RES3's quantity bundle. The implementation uses three pure PriceIndex
price/base readers instead of decoding the saved array in the lasting
harness. There is no new model state, coefficient, flow, format bump or
default-trace movement from those readers; format/floor 65 remain owed.

The strengthened MonetaryCheck now passes, exit 0: 491 assertions and no
reds. Mean non-shelter prices are
4.5274290012492875/2.1712822045330857/2.117867665076757/2.9049788572586515
percent, a 1.622450143990636-point effect, matching the independent
diagnostic to floating-point precision. All 136 fixed-basket/fee checks
and all 136 native CPI reconstructions pass, as do the 17 live plan
contrasts. Every older fixture premise, original-town 4.515-point effect,
137/24 starts, later C01 rents .02486437563/.04124895851 and retained
aggregate timing assertion remains green. The new guard rejects both
zero transmission and native main's observed -.074993333-point
non-shelter mean. This is a benchmark/predicate comparison, not a
response-suppression ablation. No ablation or universal-town price sign
is claimed.

`./batch-check.sh --fast` passes, exit 0, with 0 firm and 199 soft Stale
findings. Raw `target/RES3-P1-fast.txt` and
`target/RES3-P1-MonetaryCheck.{txt,log}`. Read-only `RES3-P1-verify`, launched
at 70-percent committed memory, confirms these results and the authored
measurement diff in its fifteen-line mail
`20261124-034825-281-RES3-P1-verify-9f1e`; its session has ended. Its check
of the committed per-path table finds every value within the printed
rounding precision. The earlier HouseholdCheck application laws remain
unchanged and green from the format-65 run; no existing money, savings,
conservation or reload assertion is weakened. The new production methods
are pure readers, so this P1 mend adds no default-trace movement.

Main's records-only NK1 queue update `149d88cd7` is merged after the run;
the model still equals the diagnosed `94ba28823` baseline plus RES3. The
provisional marker and review-required ALL pick remain. The independent
diagnostic's temporary worktrees are removed after its source patches,
commands, data and logs are kept under `target/RES3-P1-trace/`. No helper
or JVM is left running at the short P1 re-review handoff.

### Final-model ensemble after REV-RES3y

REV-RES3y SHIPs `563b5f17c` (orchestrator mail
`20261124-035534-148-orchestrator-7ab0`). Its next-step instruction explicitly
does not wait for FC2, which is held on a box-only red: run the final
ensemble against `docs/ensemble/0.9.195.md`, commit its baseline before
any dry run, then request the dry-run slot on current main. Whichever
batch ships second merges the other and verifies CallCheck's six plus
two, DeskCheck's spare, FundCheck's market book and BankCheck's TAB_LOSS
pair. Those guards remain required if FC2 ships first; no wait on that
batch is introduced here.

The ensemble starts at 04:00 on 2026-11-24 from pushed, source-clean
`563b5f17c`, label RES3, with the fresh local output parent
`target/ensemble/RES3-final-20261124/`. BASELINE names 0.9.195 explicitly;
JAVA_OPTS and JDK_JAVA_OPTIONS are empty for the box handoff, with no
laptop data paths. The box uses its own native data copies. All sixteen
seeds and all five policies retain the runner defaults. The Bash runner
starts in the background under a 7,200-second timeout; its outer log
and final exit file are `target/RES3-ensemble-final-20261124.{txt,exit}`.
A temporary local Maven guard makes an unannounced laptop fallback stop
before compilation, so its cause can be stated before any laptop JVM.
Read-only MemWatch `RES3-ensemble-logs`, launched at 64-percent committed
memory in its own detached checkout, handles progress, logs and the
final comparison; no other RES3 JVM runs beside the ensemble.

The box confirms `jerus@192.168.0.16`, Java 21.0.13, snapshot `563b5f17c`,
eight runs at once and no laptop fallback. The first pass runs for 16.7
minutes before FC2's dry run takes priority. At 17 completed runs (all
sixteen AUTO2 and ZERO_SLACK seed 0), AUTO2 seed 4 has a new F1 occurrence:
"households with no home" at month 37. Baseline 0.9.195 has no F1 run.

★ Stop this known-failed candidate rather than complete eighty runs on
a model that must be mended. The parent verifies its own timeout process
613029 and sends TERM only to that PID at 04:25:25. The wrapper records
143, after 25m19s; FC2 and other sessions are untouched. No final eighty-run
score or M1 pass is claimed. The helper preserves all available remote
output, sixty files, under
`target/RES3-ensemble-review/failed-563b5f17c/`, excluding classes/driver/tmp.
A fresh full eighty follows the model mend and its review. The existing
`docs/ensemble/RES3.md` remains an earlier-model report, not validation of
this candidate, and no dry-run slot is requested.

The completed sample has 40,423 clean audited months: AUTO2 38,384 and the
one ZERO_SLACK 2,039, with no failed or unread audit in those rows. Other
policies are unmeasured. AUTO2 F1 fails 1/16, seed 4, against baseline zero.
For information, AUTO2 A2 remains red and A3 changes PASS to FAIL (deflation
spells of 15 months in seed 2 and 26 in seed 8). A1/T1/T2 pass in the
complete AUTO2 sample. Its median final population is 6,780,860.5 against
9,031,627, down 24.92 percent and narrowly above the T2 floor; takeoff is
median m554 against m400. These partial-sample observations are retained,
not presented as the missing overall criterion score.

The CSV rent reader is `RealEstate.getAverageRentPaid`: now a chained
comparable-class price level, formerly a weighted unit value, so their raw
levels are not comparable. No affordability/unplaced/ground series was
requested in this run. Do not invent an aggregate affordability measure
from wages or deposits. Seed 9 is actually inspected at m605 in AUTO2:
audit clean, residual zero, findings zero. Its other policy rows are not
available in this stopped sample; ZERO_HOT would end at m402. Ground
shortage is not directly measured there. The final complete ensemble must
report the available policy rows after the mend.

For causal evidence, the helper prepares an exact driver copy with only
read-only housing observations. One named laptop diagnostic runs through
m45 while the immutable box snapshot continues: temporary MonetaryCheck
property `res3.seed4Trace` returns to the driver before its own fixture
changes, using AUTO2 seed 4 and native Earth/world bytes. It is a diagnostic,
not a passing MonetaryCheck law run. All 44 CSV rows m2-45, all 68 columns
(2,992 cells), match the immutable gatebox AUTO2-4 prefix byte-for-byte,
without runtime exclusions. Its trace/source, prefix proof and compact
three-month data are under `target/RES3-ensemble-review/seed4-causal/` and
the review folder. No model decision or additional month step is added.

The actual m37 result is 5.334830455751835 unplaced households, all
affordability and noSuitable zero, despite 135.27691877 compatible vacant
door-equivalents and no pipeline. At m36 and m38 unplaced is zero. The
affected senior cells bid .00501710/.00684241/.01057528 against compatible
quoted bills .05983892/.12256114/.05754492/.04922548. They already have
zero-bill D90 guest assignments in their m36 cell keys; net growth of those
three cells is only .14682126. This is aggregate cell evidence, not a claim
to identify every individual across a census. The first unemployment wave
(0 to 46.9496 percent) introduces zero-funded demand and exhausts current
funded guest room. No real-estate ground block or simple missing-door
shortage causes this observed failure.

The source omits hosted incumbency: HousingMarket.clear converts prior
bill/lastTrade to primary count, making a zero-bill guest zero before
HousingChoice receives incumbents. Its primary gate also uses the new
offer's affordability rather than the prior arrangement. The design's
incumbent/shortfall rule therefore needs a conserving implementation proof.
Opening-pool EI is intentionally paid before the new unemployment census,
so zero m37 EI alone proves no benefit lag defect and authorizes no invented
same-month income. Primitive financial cash/fees/debt-minimum terms were
not captured by the first housing-only trace. A focused isolated probe is
required before selecting the repair, preserving real funding, at most two
households per door, class rent updates and all existing invariants.

The temporary named route/class and its generated artifacts are removed.
The first inline restore invocation did not execute; the explicit Bash
restore script then compiles successfully (exit 0, 54 seconds, 0 firm/199
soft Stale), and the compiled MonetaryCheck is checked to contain no
`res3.seed4Trace` route. Source remains the reviewed model, with only these
notes pending. The final partial helper report is fifteen-line mail
`20261124-043132-158-RES3-ensemble-logs-e2eb`. Its focused continuation is
launched at 65-percent committed memory in the same isolated checkout;
the parent runs no JVM alongside that probe.

### Seed-four stock and incumbent mend, 2026-11-24 (candidate)

The isolated probe finds a second, distinct phase error. Housing reads the
new family census before the next financial census routing, so newly
unemployed physical cells can still have the old empty financial cells.
At m37 ON_EI goes from zero to 176.4307562 households and OFF_EI to
.5692438, with zero old-cell cash and debt. The later cash strike carries
their existing money and debt through `followThePeople`. Opening-pool EI
remains intentionally zero in this first unemployment month.

★ Quote existing stocks on a detached census view through the same stock
routing, with zero arrivals' cash buffers. Preserve HC1 card state,
securities, basis, claims, bonds and actual price/tax context; do not book
the detached view's transfers into live counterparties. There is no new
saved field or cached census. Format/floor 65 remain the already owed
boundary. The actual financial strike is unchanged.

★ Retain funded paid door shares at the current class quote before new
movers, preserving households separately from physical doors. Retain old
zero-bill guests only within this clearing's actual funded host room.
An unfunded vacant door creates no shelter entitlement. Emergency sharing
and planner residuals subtract housed households, rather than their
fractional door shares. Cap two, suitability and funding gates remain.

The five-file candidate is applied to the FC2 merge `1ae98bb3c`; the
isolated authoring base is `563b5f17c`. Before that merge, the complete
HousingChoiceCheck and HousingMarketCheck pass, including new caused
stock-preservation, no-invented-arrivals, restore, retained-paid-halves,
funded-host and no-host cases. The exact seed-four m2–45 diagnostic has
zero findings and clean audits. This is a bounded replay, not an ensemble
or a passing MonetaryCheck law run. Its m45 population is 986 versus 971,
rent index .0109625214 versus .00812986492, GDP 238.49 versus 389.52 and
hunger .001689744 versus .001385404; the adverse outcomes are retained.

Acceptance is still pending. The pre-merge CardCheck command also selects
BuildCardCheck by substring. CardCheck passes; BuildCardCheck has the
recorded three Rail assertions plus two new Pelletising Plant assertions:
staffing 38 percent now and 79 percent with arrivals preempts MONEY at the
unchanged 80-percent gate. Those two have no waiver. The merged model
needs a causal classification before a mends handoff.

The cash evidence also has an explicit limit. Three newly admitted paying
unemployed cohorts settle with zero Unfunded, and none of 475 increased-bid
paying observations exceeds its new budget. One quote-m16 ON_EI row still
has Unfunded .13661891 at its next strike despite rent .04724352 being
below both old and new bids (.12963506/.12963856). Native `b92af507f`
has generic unfunded/skipped-cell faults, owned by queued HL2, but that
alone does not prove this changed path inherited every gap. Residual zero
includes Unfunded and UnsettledRent and is not strict paid-rent equality.
The helper's evidence, failed attempts, patch and hashes are retained in
`target/RES3-F1-mend/`; a fresh isolated source/cash investigation and
merged named checks are in progress, with no parent JVM alongside it.

The FC2 merge also exposes one source-level harness dependency before
any test starts: InvestCheck still refers to removed FC1
`BuildersShort.FOREIGN_FLOOR`. FC2 always offers foreign work. Replace
that obsolete rescue condition with a positive local site pace, retaining
the independently caused empty queue and the bound that all ten Houses
stand below `KNEE_MONTHS`. The expected work price remains premium exactly
one; no quoted premium is read back into the expectation. The first
merged BuildCardCheck command therefore compiles no harness and exits 1;
its repair is included before the next validation.
The merged BuildCardCheck confirms the same five assertions. SpreadPlanner
orders FEED, LAND, STAFF, MONEY; the Pellet card has earnings 15,616, whole
cost 11.9 million and money hurdle 71,316, but staffing 38/79 percent
preempts MONEY at the unchanged 80-percent threshold. `moneyGate` changes
prime only when the initial card has no failed gate; it never causes the
staffing premise. ★ Attribute the two new lines to queued FS12's played
building-card fixture sweep, the same structural gate-preemption class as
its already named Rail case. Add exact separate ship rows for review,
without changing the fixture, its first-failure assertion or any gate.
The helper's first wrapper is edited during finalization and reports 1
although its saved suite verdict is 4; retain that mistake and rerun the
named check once after diagnostic source restoration. No green result is
claimed from that wrapper.
The second helper closes the old-path cash mechanism with one restored,
17-month named diagnostic. At quote m16, ON_EI has 21.6651344775 opening
financial households and .98852727168 fresh households. The old paid EI
row .158560130476 becomes .160400360231 per fresh household. The current
modeled EI bill .00708205500231 would instead give .00716424847872,
exactly the actual m17 income. Old row fees .0233730818742 are likewise
concentrated despite their billed source population being 22. This is a
RES3 quote defect, independent of the stock-projection mend, and cannot
pass merely because both old and new bids exceeded the rent.

The m17 cash shortfall also includes .196937458833 shopping per household,
from the existing cash strike's historical grocery row divided over the
fresh census, versus rent .04724352339. That separate actual-settlement
fault is within queued HL2; this batch changes no shopping waterfall.
The FC2-merged seed-four path has no ON_EI cell at that particular month,
so the old-path replay establishes the defect's mechanism and caused
unit cases must guard it independently of a seed or calendar month.

★ Bound fallback recurring take-home by both its source recipient rate
and the actually paid opening pool across fresh recipients. Contraction
cannot concentrate the pool; expansion cannot multiply it. Keep explicit
scheduled wage/pension callbacks and first-shock EI zero. Recurring fees
retain their billed source per-person rate, with the same pooled fee
basis as the actual cash strike, and known costs are not discarded when
source counts are absent. Account fees and loan minima remain per
household. These decisions are implemented and tested before acceptance.

The independent source review also catches a candidate-only defect:
`restoreCells` carries `afterFixed`, but not the ephemeral `disposable`.
A detached cell therefore cannot supply the previous actual fixed-bill
lower bound. Read that bound from the live original cell and its
same-phase rent; use the detached view only for routed stocks. A caused
positive-disposable floor case is required, rather than changing the
save schema to persist a derived context solely for a quote.
The incremental quote mend adds eighteen caused laws for EI and student
grants, source/fresh count directions, zero or unowned opening receipts,
source fee rates, missing source-head metadata, and the live fixed-bill
floor. The complete normal HousingMarketCheck then passes (selected one,
exit 0), with all existing integrations unchanged. Its initial nineteen
assertion progress count is corrected to eighteen in the helper's report.

A final stock-context review identifies student service as another concrete
projection risk: principal is restored and routed, but studentInterest and
studentRepaid are last-strike flow fields and are zero in the detached
view. A working household carrying 114 of student principal at annual
rate .12 owes 1.14 interest and 1 principal under the existing term, even
with an old fixed-bill floor of zero. ★ Reserve the existing pure current
contract calculations on carried principal, rather than stale flow
getters. Keep the repayment term, forbearance, rates and actual settlement
unchanged; a caused positive-service and zero/forbearance case must pass
before the wider matrix. No new save or accounting flow is introduced.
The final merged AUTO2 seed-four m2–45 diagnostic is clean: 44 rows,
68 columns, 2,992 values, all audits valid with zero residual, no findings
and no invalid saved housing allocation. This is bounded confirmation of
the original F1 case, not the missing full eighty-run verdict. Diagnostic
routes/classes are archived and removed before normal checks; all 641
tracked Java sources are compared with parent model `05370e41e`.

The load-context inquiry finds no missing production save dependency.
Load refreshes household accounts through planOnly/restrike, then restores
saved afterFixed, so idle disposable need not be identical. The only
production housing-budget strike is clearTypedHousing during demographics;
the actual month's earlier financial settlement refreshes disposable and
afterFixed before that call. Forecasts use saved market demand rather than
restriking a budget in the idle hybrid. ★ Preserve that phase order and
add a paid-bank/positive-fixed-floor native continuation premise to the
existing twins; do not persist an unused idle context. No additional saved
field, conversion, format bump or actual settlement change is needed.
Format/floor 65 remain owed above shipped 64 for the existing RES3 state.

The first merged fast check passes. The final BuildCardCheck command,
using the branch's validated records, returns 3 with its five exact FS12
lines (three Rail, two Pellet); the earlier raw 1/4 outcomes remain in the
record. A premature HousingInventory launch is held before any JVM because
the helper mistakenly adds heap reserve to the available-RAM requirement;
the corrected wrapper applies MG1's separate commitment/RAM conditions.
The retry passes. No model threshold or fixture is changed for that hold.
The positive-floor native twins pass in both eras. A bank branch is built
through the public paid path after the measured 24-month catalogue tables;
two audited months establish actual fee payments. Non-rent fixed floors
are .0450394087901 modern and .00475167644396 historical, with positive
income and actual account fees .000317810711720/.000319994827405. Native
save/load and the next real month preserve the complete typed market,
including demand budgets, and all audits. Six new premise assertions are
retained. A narrower duplicate budget equality also passes, then is
removed because the unchanged full-state comparison already includes it;
only that comparison's label is clarified. No conservation/save condition
is weakened, and a label/redundancy-only edit warrants no additional run.

The final generated catalogue replaces RES3-catalogue.md, with fourteen
unique forms and thirty-two classes in each of two eras. It explicitly
reports affordability failures in the deliberately oversized migrant
fixtures; it does not claim those inputs house everyone. Single integrated
months on this laptop are 48.896 ms for 265 residents, 52.771/41.335 ms
for the two catalogue states, and 50.651/41.547 ms after growth to
148,464/147,404 residents. Both larger states retain 141 permanent cells;
H/T/A are 82/32/341 and 62/32/178. Larger profiles follow the additional
paid-service/continuation steps; the per-form tables precede them. These
are loaded-laptop observations, not a nightly speed verdict.

The merged InvestCheck initially returns 4 with two assertions: one known
HT1-H rebate straddle premise, and one new four-plot fixture failure.
The planner has selected the 1,248-capacity Large apartment block, id 163,
with 100,000 square feet per plot. The literal 2,500 gap needs three
projects; 400,000 free feet allow four, so the model correctly returns
min(3,4)=3. Its independent whole-gap law already passes. ★ Cause the
stated binding-land condition with five times the selected model
capacity, retain expected four and the same land limit, and assert the
strictly larger demand premise. Keep the existing literal-2,500 whole-gap,
two-project and zero-land laws. This tiny fixture repair adds no FS11
known-red row, pins no selected form and changes no model/order-size rule.
The named check is rerun because its input changed.

The other Invest line remains the recorded HT1-H premise: the builder
index moves 1.139413 to 1, but both chosen quotes still have zero rebate.
Actual restored-contract rebate equals the rule's zero and all independent
caused rebate-band laws pass. No refund, price, conservation or loan law
is changed to make that missing straddle green. SaveFileCheck passes on
the final model before the remaining native/reload/finance matrix.

The corrected InvestCheck rerun returns 3: the independent larger-demand
premise and expected four-plot result pass; only the recorded HT1-H rebate
premise remains. The original two failures/exit 4 are archived separately.
The helper's outer PowerShell initially normalizes nested nonzero statuses
to 1; the directly captured batch exit.txt (3) is authoritative and the
outer wrapper is corrected without rerunning unchanged laws for labels.

Validation checkpoint, 2026-11-24 06:56: ten of nineteen named matrix
filters are complete. Normal exit 0: HousingChoiceCheck,
HousingMarketCheck, HousingInventoryCheck, HousingCheck, HousingDoorCheck,
HouseholdCheck, BuildingDataCheck, SaveFileCheck and ReadPathCheck.
InvestCheck is 3 (only HT1-H); the additional BuildCardCheck is 3 (FS12's
three Rail/two Pellet lines). The helper fast check is 0. HousingCheck
also prints one OBSERVED unreached closed-home/debt premise; no execution
of that mechanic is claimed. All 641 Java sources match the parent.

Remaining: ReloadCheck, DenominationCheck, CurrencyCheck, MonetaryCheck,
CallCheck, DeskCheck, FundCheck, BankCheck and WaterCheck. Their FC2 guards
are not yet claimed. An external laptop AllChecks gate, PID 37156, and
available RAM below MG1's four-GiB reserve hold starts before any JVM.
The gate remains active at 06:56. Respect contract section 4's no-named-
JVM-beside-gate rule; never end another session's processes. The parent
also holds its final fast/maps until the lane/resources permit. This is
an external validation block, not a model verdict or a mends/ready claim.

The checkpoint is `target/RES3-F1-audit/checkpoint.md`, matrix-status.json,
source-hashes.json, manifest.json and checkpoint.zip (343 files,
3,795,634 bytes; SHA256
`daf34773daec70d0777678f9f1c66c7bdb19e9f7fb024ac56320f639b9763a7e`).
Production source is `05370e41e`; the later committed changes are test and
record guards. No fresh full eighty-run score, dry run or ready marker
is claimed. Finish the remaining checks, regenerate maps, then request
the money/saves delta review before the fresh eighty-run ensemble.

The helper ends cleanly at 06:56 with no owned JVM or job. Its final
`blocked-artifacts.zip` has 345 files/3,797,909 bytes, SHA256
`a24c28e6e135c7c02eb7c1dce70e85f1167c092911f6f5d760399d413b364b86`;
blocked-report.md gives the exact nine pending filters. A parent recheck
still finds gate 37156 active, 3,215 MiB available and 80-percent committed.

### Resume after FC2b's full-suite stop, 2026-11-24

The orchestrator corrects the process classification: PID 37156 was
FC2b's wrongly started full suite, not a gate. He stops it at 07:03 and
explicitly authorizes the remaining nine named checks, then root fast/maps
and the money/saves mends handoff. The earlier gate description records
our inference from its command line, not an actual publisher run; no kept
gate/dry result exists from it. The low-RAM hold was separately observed.

Read committed memory first: 71 percent, 8,258 MiB available, 41.77 of
58.66 GB committed. Resume the same isolated helper on the exact nine
pending filters, serially, with per-launch memory checks and no parent
JVM. Preserve the completed ten and all raw/known-red evidence; no model
or assertion change follows merely from the scheduling correction.

DenominationCheck first returns 4 with one assertion: the played TP1 town
is assumed to have prices and its lane floor above founding after at most
twelve unforced months. Its index is instead .7730. TradeLanes applies
MIN_LANE * FACTOR * index / unit without a founding-floor clamp, giving
about 6.002 against founding 7.765. The exact floor identity, reform /100,
lane/arrow partner equality, deliberately undivided-floor fault, direct
double-price law and all four unit-boundary cases pass. No real
currency/scale defect is demonstrated by this direction-only failure.

★ Preserve the upward-price premise and all real laws; attribute only its
exact .7730 line to queued HT1-H's uncaused played-city fixture class.
DenominationCheck's FS2 sweep shipped, so FS2 cannot own a new red. DK1's
full-desk arithmetic is not this mechanism. Do not restore a made-up CPI,
change the time window or improvise a world-price shock merely for green.
The helper's initial direct PriceIndex.restore proposal is withdrawn for
that reason. The original raw exit 4 remains archived; refresh the exact
branch records and judge it without repeating unchanged model months.

The unchanged final MonetaryCheck returns 4 with two hard law failures.
Original path 48213 mean CPI at 3/10/20/40 percent is approximately
5.32/7.00/8.32/7.31 percent: the highest rate raises inflation by 1.99
points rather than lowering it by at least one. All dial-only, fixed-D$,
crew, three-calendar-year and basket premises pass. Across paths 16–31,
common-basket non-shelter means are 2.662484889421045,
2.487814813917172, 2.941129215897183 and 2.8748069355454096 percent;
the low-to-high effect is -.212322 points, violating the retained
one-point floor. These are hard failures, not HT1-H premises.

Supply assertions pass: 103 versus 32 early accepted homes at 3/10
percent, and later C01 rents .03123961074/.03422137073. Whole-window
timing .466952 is below .750; calendar differences .619752/.740589/
1.708418 are below two points. Mean CPI across the sixteen paths is
7.133053/6.419554/7.042718/7.135521 percent. Preserve every law, fixture,
window, threshold, source output and sample. No monetary known-red row,
fake price state or weak aggregate guard is introduced.

Pause Call/Desk/Fund/Bank/Water. The helper isolates original path 48213
on FC2-merged pre-mend control `1ae98bb3c` and current model `05370e41e`,
using only a temporary route to the existing original-town scenario in a
separate scratch worktree. The current diagnostic must first reproduce
the full run's unrounded outputs and shared setup. The control carries
the necessary harness-only obsolete-FOREIGN_FLOOR compile repair, with
original compatible HousingMarketCheck code; no production input changes.
Only named MonetaryCheck diagnostic calls run, serially, after memory
checks. Parent makes no model edit before the source-isolation evidence.

The current original-only diagnostic reproduces every stored unrounded
component/weight/factor vector, all eight CPI/rent endpoint strings at
12-digit precision, policy rows and original premises. The earlier full
run stored aggregate inflation to two decimals only; no unavailable
17-digit historical equality is claimed. Fresh original results are
captured at 17 digits. All diagnostics keep the normal main prefix,
original comparison call, rates, window and monthly order.

| Original-only production group | Low/high CPI percent | Reduction, points |
|---|---:|---:|
| FC2-merged pre-mend control 1ae98bb3c | 6.772473 / 2.257213 | 4.515260 |
| Current 05370e41e | 5.318921 / 7.312324 | -1.993404 |
| C: old ledger, new matching/market | 4.757488 / 8.306316 | -3.548829 |
| D: new ledger, old matching/market | 5.424820 / 4.006548 | 1.418272 |
| E: current plus blanket planning guard | 9.850778 / 8.781409 | 1.069369 |

The latest matching/market group is sufficient for the original reversal;
FC2 is not its attribution. Each version keeps its own endogenous m25 D$
fixed across rates (control .7143, current .7233, C .7445, D .7148);
no cross-version FX override is introduced. None of these subsets is a
passing full seventeen-town MonetaryCheck. E also fails the unchanged
supply-rent predicate (.041291549 below .041898393), so E is not accepted.

A toy paid-halves probe initially treats emergency=false as forbidding all
retained crowding. Its nine failed expectations are hypotheses, not new
laws: the flag forbids emergency assumptions in new projects, while
existing paid occupancy still has claims. The real-market boundary
rejects the blanket guard. With one actual door at ask 20, two old half
payers at budget 12 and a newcomer at 100, actual old incumbents remain
housed and the newcomer needs one funded new door. Current forecasting
preserves that case; the blanket guard incorrectly reports zero. At
budget 20, all three households can fund independent doors: two new doors
are needed, but current forecasting reports only one. Both versions keep
actual source, occupancy, rent and read-only forecast assertions green.
★ Reject the blanket guard, retaining its raw results as diagnostics.

The separate commitment defect is executed in a minimal named market
unit: ten old households/bill 20 shrink to two. Actual pure retention
leaves bill 4, or 2 per household. Resources are 7 after the unchanged
live non-rent floor. At saving factor .5, the current projected-count
callback protects all 7 instead of 2+.5*(7-2)=4.5; a no-source case
incorrectly protects 10 instead of bidding 5. Expansion, actual retention,
all saved stocks/flows and live working fields pass. ★ Cap only the
protected rent at min(live per-household rent, projected rent); keep the
uncapped live rent for the fixed-bill floor. No factor, income, service,
actual settlement or monetary law changes.

A count-only forecast prototype plus that rent cap passes the original
market boundaries, but three spare-stock cases expose phantom orders:
full-budget incumbents with two/three doors, and a newcomer with three
doors, request another home despite enough committed stock. ★ Derive
independent-door pressure from physical reserved shares, then match it
normally against unused committed offers before requesting construction.
This forecast-only extra match preserves actual household accounting and
existing paid occupancy. Twelve stock/budget/newcomer combinations guard
both lost funded demand and duplicate orders. A pending-construction
boundary is required too; no new order state is created.

The complete three-file prototype remains outside the final source tree
pending clean diff review and domain/original-law verification. Its first
patch contains unwanted CRLF and pre-existing Unicode re-encoding;
regenerate from exact parent UTF-8/LF bytes and retain only intended edits.
No such incidental changes are accepted. No full sixteen-town rerun or
remaining finance filters resume until the original effect and supply
conditions pass on a domain-correct candidate.

The clean F prototype fixes the domain boundaries: normal HousingMarketCheck
passes, including twelve committed-stock cases, the actual half-built pipeline,
incompatible completed studios and rent-commitment contraction/expansion/zero-source
controls. Its 189-line UTF-8/LF patch changes no pre-existing text. It is not
applied: original-only inflation is 7.749439/9.601061/10.372711/9.166562
percent, a -1.417123-point reduction. Original non-shelter reduction is
-1.779382 points. Supply starts 86/56 and later C01 rents
.02662843807794349/.0267659874242694 satisfy the two supply predicates,
but the hard policy effect does not. No full-grid pass is claimed.

A separate eight-snapshot choice witness causes actual sticky crowding.
With two current doors at ask 20 and two old paid half-households at budget
20, ordinary matching without incumbents uses two doors and bills 40;
retention keeps the same two households in one door and bills 20. Both
have zero unplaced households. At budget 12 the paid shared arrangement
must remain. With a rich newcomer, the existing free door must go to that
mover before expanding old footprints; otherwise a funded mover is stranded.
Source/funding/stock controls pass in all eight diagnostic snapshots.

★ Interpret valid incumbency as preserving household and comparable class,
while allowing a fully affordable suitable household to leave emergency
crowding when current same-class stock remains after every mover. The
brief distinguishes normal placement from emergency shelter; it does not
require sticky crowding. Authorize only an isolated prototype of a late
same-class footprint completion, with equivalent requests pro rata. Use
households minus funded doors (bill/current ask), not households minus
physical shares, so a fully funded host cannot be charged again. Add the
same physical quantity and current-ask bill quantity; preserve zero-bill
guests and their source counts. Require stock, price, newcomer, host and
funding boundaries before the unchanged original monetary scenario. This
is a model hypothesis under test, not a passing final model or a changed law.

The native mixed-host witness confirms that physical and funded coordinates
must stay separate: a paid cell has two households, physical footprint 1,
bill 30 and 1.5 funded doors at ask 20; its one zero-bill guest occupies
.5. Total occupied is 1.5, receipts 30 and hosted guests one. The proposed
completion is .5, making total occupied two and receipts 40 while retaining
the guest. Completing the physical gap of one would overcharge the host.
All diagnostic source, stock, price and capacity controls pass; this witness
precedes implementation and is not a final-model result.

The late-completion prototype passes every stock, budget, equivalent-rationing,
input-reversal and full/mixed-host boundary except the added guest-feasibility
law. Native stock two, two fully affordable old paid halves and two zero-budget
guests ends with two paid households occupying two doors/bill 40 but two
unplaced guests. The same normal-control inputs house all four in two doors,
bill 40, hosted/crowded two. The additional funded room appears after the
existing host pass. This hard failure is preserved; no monetary probe follows.

★ Complete the new room through one conditional reuse of emergencyDoubling,
only when the paid-footprint phase expanded. Its remaining source subtracts
already assigned household counts, and its host room reads the updated actual
footprints. Require exact preservation of paid households/bills, old/new guest
counts, price and hosted provenance in the caused witness. No new sharing
formation, saved state or iterative equilibrium is authorized. Full housing
checks precede the unchanged original monetary scenario; the final source
remains unchanged until the required laws pass.

Merged records-only main 77c77ab8c at 24138a2f9. It changes queue/briefs and
contract section 6 (the orchestrator can return a cleared Pending row), with
no Java source change. The controlled model comparisons retain the exact
1055bc228 source base.

FC2b shipped as 0.9.198 at e6a47dff8 while the conditional guest prototype
was awaiting its domain run. Per orchestrator 09:56, merged at fe2a08755.
AllChecks keeps both RES3 housing harnesses and ForeignChoiceCheck; the tree
keeps both entries. The version string is main's. GameVersion preserves
FC2b's contract-points rationale and raises RES3's format/floor from 65 to
66 under the old-saves rule. This supersedes the earlier 65 boundary; no
conversion is written. The helper pauses old-source monetary probes and
prepares a genuine shipped format-65 pair with independent save/history
hashes, keeping all older refusal pairs and all intact-city/file checks.
The merged model must be validated afresh. No previous monetary diagnostic,
catalogue, ensemble or check result is represented as final-merge evidence.

The conditional host prototype's pre-FC2b normal HousingMarketCheck passes.
The original hard guest witness now houses four exactly as the native
normal control does: two doors/bill 40, zero unplaced, hosted/crowded two.
Each payer remains one household/bill 20, and the two free guests pay zero.
A second native case preserves one old free guest and places two new free
guests once each, with three payers, six housed households, three doors,
bill 60, hosted/crowded three and zero unplaced. Every earlier stock,
newcomer-first, poor-budget, mixed/full-host and equivalent-rationing control
passes. The patch is still isolated. This result predates the FC2b merge;
merged-domain and monetary checks remain due.

The newest main baseline is docs/ensemble/0.9.198.md (FC2b): all five
policies have zero M1 audit failures and zero F1 findings across sixteen
seeds. The future fresh eighty-run RES3 ensemble is prepared against it,
with no output directory or job started. Earlier 0.9.195 findings are
historical comparisons, not current allowances; new findings need their
own cause and cannot pass by an old seed name.

Accepted the genuine FC2b format-65 pair after independent parent review of
the 129-line guard/producer/README patch and gzip contents. Native shipped
source is clean; native fast, producer compilation and runtime each return
zero. Runtime reflection pins 0.9.198/format 65. The slot-10 scratch pair is
month 31 with 141 household keys/20,307 values, thirty history months and
two debt surveys. A native Manufacturing contract holds
1454.9999999999998 points, without another run or field assignment.
Parent recomputed raw save SHA-256
be8f72e5c39f0bc2147d63fb44bc84a8bffa1777cf7c3432bf622e4807352082
and history SHA-256
3b4339debc58574aa470d3429e8f144f63e202ea509dd999fd4fe9b444919877;
both gzip timestamps are zero. The exact real pair is committed beside
its guarded producer. SaveFileCheck now tests real 65 directly below 66
and keeps every 62/63/64 hash/schema and both-file/running-city/money
preservation guard. ReadPathCheck keeps the repeated reads. These new
refusal assertions have not yet run; native production success is not
a claim that RES3's save harnesses pass.

On the merged conditional prototype, original-only MonetaryCheck completes
with raw batch exit 4, one selected harness, one hard effect failure and no
OBSERVED cases (44.8 seconds). The final archived CPI fractions at
3/10/20/40 percent are .0765122467743955, .09952585629531674,
.10140714638674031 and .09775066441916858: low minus high is
-2.1238417644773078 points. Non-shelter fractions are
.02754856223515212, .09132832404539792, .07751412465126406 and
.06273943828082085, a -3.519087604566873-point effect. Starts are
74/50/26/26 and later C01 rents .027038737374332485,
.02732403693869633, .02668152213002554 and .026636743481485786.
Both supply predicates are true when derived from the observed values;
the final supply assertions after the diagnostic early return did not run.
The sixteen-town grid and remaining finance filters remain held.

The helper's initial progress message accidentally read the previous F log
while the new compile was active. Its claimed F-equality and -1.417-point
result are withdrawn; the completed run above is authoritative. Subsequent
reads use the completed run's archive and exact wrapper exit. The 664
passive after-month rows are retained. All diagnostic routes/classes are
removed; source comparison is again 639 root-identical Java files and
three exact reviewed prototype files. No model prototype is applied to the
root. Continue source/spec diagnosis from the existing observations,
identifying an actual funding/placement defect or a real model tradeoff
before proposing another change. No policy law, rate, window, CPI state,
elasticity or invariant is changed for green.

★ Adopt the exact reviewed 317-line domain patch into the root while keeping
the monetary failure hard and unresolved. Its source/funding defects and
caused housing laws are independent of a passing macro result: surviving
rent commitments cannot concentrate on fewer people; independent-door
forecasts must consume spare committed stock once; suitable fully funded
paid shares can complete normal space after movers; newly funded host room
must then serve remaining guests once. The merged HousingMarketCheck and
HousingChoiceCheck pass on exactly these bytes. This is no ready/review
claim and does not justify changing a policy law. No new saved field is
introduced. The save-format 66 bump remains owed by the original typed layout.

To advance independent save verification during the helper's source-only
monetary diagnosis, the root starts only SaveFileCheck. MemWatch before
launch reads 76.7 percent committed (41.88/54.63 GiB), and the native memory
probe reads 5,671 MiB available RAM and over 12 GiB free commitment: both
MG1 reserves pass. Native Earth/world paths and parallel=1 are explicit.
The helper is instructed to launch no JVM until the parent hands back.

Root SaveFileCheck finishes batch 0, exactly one selected harness, harness
0 in 123.6 seconds. The immutable archive confirms the genuine 62/63/64/65
refusal conjunction fired, reaching the real 65 hashes/schema/nonzero points;
the one-below-floor case is exactly 65 below current 66. Fresh/running loads,
both files, full saved city, manager/history identities and money stay intact.
Current-format save/history and the seventy-five fixed-bill cells pass; the
next-month loaded twin matches the live city, every money pool and people
to the bit. No FAIL or OBSERVED case is present.

Root ReadPathCheck finishes batch 0, one selected harness, harness 0 in
136.5 seconds. Native 62/63/64/65 refusal and repeated reads fire; all
prior native/HC1/PW5 cases, dated Money and licence readers pass too.
The helper reviews only each immutable run archive, not a live overwritten
log. Both root process trees have finished before the JVM is handed back.
Generated maps retain structural changes and LF; final root fast/maps still
wait for the final model. No money-policy pass is claimed.

Source-only diagnosis of the completed G observations finds mean paid-partial
households 155.7800/151.7065 at 3/40 percent over months 49–84; almost all
can afford a normal full current door. They have no eligible same-class
free completion gap, while 16.5112/8.5653 households have some other free,
normal-affordable class. A 3 percent month-50 COUPLE:COLLEGE H120 example
has .7138344 households, .7067754 funded/physical doors, bill .0336951,
ask .0476744 and budget .3223685, with other recipe-160 classes spare.
These final states do not establish retained or late-expansion flows.
No cash/bill funding violation is established. Approve only passive capture
of actual pre-primary, retained and post-phase quantities, followed by a
separate frozen-argument choice counterfactual. The logger must not solve,
price, forecast or mutate the model; the completed original arrays must
match the archived G run before interpretation. This distinguishes a real
allocation defect from a tenure/normal-upgrade priority tradeoff, with no
new model algorithm or policy-law change authorized.

The passive actual-phase capture completes with the same single original
hard failure (batch 4, one harness, 52.6 seconds). Every CPI/non-shelter,
start, rent and late array equals archived G exactly. The 664-row monthly
trace SHA-256 is also identical:
a59008d705282b8f75c93e203352b97f3c994e5edbf86e620978baadb2713165.
Only after this equality gate are the 72 actual 3/40-percent frames over
months 49–84 interpreted. They contain 1,440 phase records, including the
actual retained list before fresh assignments are appended. Existing normal
completion expands positively in all seventy-two frames; that is now a
captured local flow, not an inference from the final stock. The separate
frozen-argument choice comparison remains diagnostic. Its filter must
identify paid partials from the built primary's reconstructed prior funded
doors, preserving full-funded hosts even when the current ask changed.
Raw hosted physical shares and old bills divided by a new ask are not
valid incumbent-funding classifiers.

The frozen choice diagnostic passes (batch 0, one harness, 1.7 seconds).
All seventy-two baseline Results exactly match the captured actual primitive;
171,941 source, stock, invoice, budget, closure, immutability and reconstruction
guards pass. Only genuine built-primary paid partials are excluded in the
counterfactual: mean 139.9681/139.3762 households at 3/40 percent, with full
primary hosts protected. Actual retained paid partials are
139.7683/139.2199 households; almost all are normal/whole affordable.
Removing them increases fresh normal placements 88.4708/87.6403 but loses
51.2419/51.5484 housed households at this pure primitive stage. These are
not final-city evictions and cannot be assumed recoverable by later hosting.
No cash-funding violation is established. Blanket nonretention is not a fix.

★ Keep every existing funded footprint and bill, and investigate only the
excess household fraction over funded doors as a normal-upgrade request
after all movers. Merging this operation into the same class reproduces
the adopted completion; other unused normal classes can be considered
without surrendering the funded incumbent claim. Authorize a frozen proof,
not a production change: old eligible lease counts/bills stay fixed,
households reduce only pro rata to a placed funded gap, and new normal
leases receive those same households. Each remaining old tenant and each
new tenant must afford the bill; total new cost is capped by actual quoted
cell resources less existing bills. Hosts/zero-bill guests remain intact.
Cause the two-old-halves/other-vacant-class case, the budget-12 refusal,
hosted/mixed-host and equivalent reversal boundaries before any live
integration proposal. Every monetary law and window stays unchanged.

The new frozen excess-household proof stops with batch 4 and one
HousingChoiceCheck failure: strict current stock cap. Its nine minimal
boundaries returned before failure in an actual frame. No assertion,
fixture or threshold is weakened and no production algorithm is changed.
Request exact class/stock/old-use/new-allocation quantities and bit patterns
before any repair; one context-only rerun is permitted only if the existing
artifacts cannot supply them. It must still fail at the original guard.
Do not label the excess a rounding exception without the witness.

A separate source-only hypothesis is queued behind that proof: current
price demand may count a fully funded host's donated physical share as
additional demand despite no funded new-door forecast. The minimal proposed
case is one door at ask 20, a host with budget/bill 20 and a zero-budget
free guest. This is not yet a finding or a pricing-change authorization;
source reasoning and a caused countercase are required first.

The strict witness is exact: 3 percent month 53, recipe 160/F stock 12
(bits 4028000000000000). Old use 8.9303497310702970 is valid; free and
new-alone sum both equal 3.0696502689297027. Sequentially combining the
34 old rows and five new additions gives 12.000000000000002 (bits
4028000000000001), one ULP above stock. This is a new combination/order
defect; neither old stock nor the matcher's separate free-cap check fails.
The repeated run still fails unchanged at the strict assertion. No magnitude
waiver is taken.

★ Bound only new additions against the actual sequential remaining stock.
An excess above the existing packing bound must still throw; a rounding
residue can only reduce a new count, with its full-price bill and household
count recomputed. Derive moved households from those bounded additions
before reducing old household quantities. Preserve every old count/bill
and all strict stock, source and budget checks. This is a pure-proof repair
pending the same nine boundaries and all seventy-two frozen frames.

Source review also confirms the closed host-pressure mismatch algebraically:
physical base one plus a full-funded host's .5 physical donation gives
pressure 1.5, while the host pays for one door, has no remaining resources,
and the guest cannot pay. Both coverage and extra-demand coordinates need
coherent accounting; changing the extra alone leaves a residual source.
No executed pricing finding is claimed yet. Authorize a caused live-model
witness with paid-pair and affordable-guest controls, and a cheap-alternative
forecast-budget case, before any pricing or forecast change. Preserve every
existing demand and funding test. The original monetary law stays hard.

The repaired pure proof passes the same nine boundaries and all seventy-two
actual snapshots: batch 0, one HousingChoiceCheck, .7 seconds, 177,698
guards. Exactly two new additions are directed down, total
4.5657921887709560e-15 doors. Every original count/bill/class is bit-exact;
strict stock and all source/budget/host/price guards stay unchanged.
Independent snapshot sums upgrade 36.537275984431254 households at 3 percent
and 23.111946463575567 at 40 percent (means 1.0149243329008681 and
.6419985128770991). These are frozen proposals, not longitudinal city results
or production integration.

The closed pricing probe executes the host finding: actual pressure 1.5
for one funded door, with no new funded forecast income. Paid-pair and
whole-affordable guest controls both produce pressure two and income 20.
The fourth cheap-alternative case initially calls inventory.owned on an
absent physical row and throws before its finding; the quoted class does
exist. Its read-only correction sums existing physical rows, yielding zero
for empty stock without adding a building or changing money/price inputs.
Complete that same four-case witness, preserving the partial failure log.
No pricing/forecast fix is authorized before the executed findings.

Before live integration, require the current hosted-guest coalescence case:
a positive-paid cell/home can also contain a sponsored guest this press.
Dropping its household count in the frozen upgrade can otherwise leave a
stale hosted reason counter greater than remaining crowding. Separate
zero-bill guest records do not prove this case. Request actual phase guest
provenance and a caused test/accounting proposal; do not reconstruct a
flow from ending bills or alter a counter merely to satisfy its bound.

The corrected closed witness completes all four cases and seventeen checks:
raw batch 4, one HousingMarketCheck, 1.7 seconds, three real finding failures
and no fixture exception. The first three case objects match the initial
partial run exactly. Host-plus-poor-guest pressure is 1.5 versus expected
one, with occupancy one, bill 20 and new forecast income/quantity zero.
Paid-pair and whole-affordable guest controls both show pressure two and
new income 20/one project. For the cheap alternative, the old B12 pair has
24 resources and holds A's bill 20, leaving four; B has no current stock
and ask 10. Forecast promises income 10/one project, implying one new
household and only one old household to pay the retained bill 20 on budget
12. Planned rent 30 exceeds resources 24. These are caused findings, not
an expected trajectory or an attribution to main. All production sources
remain unchanged and all diagnostic code is removed after the probe.

Request source/accounting recommendations before integration for sponsored
guests coalesced into positive-paid cells this press, and for affordability
of a retained full/half mixture under one representative budget. These
checks must distinguish actual guest provenance and price-region funding
from aggregate ending bills. No extra run, production fix or economic-law
change is authorized merely by those source hypotheses.

The root adds the four closed finding cases to the permanent housing harness
and mends the confirmed price-source coordinates. Coverage counts assigned
households; extra normal pressure uses households minus funded bill/current
ask, so a host's donated physical share is not a second bid. The paid-pair
and affordable-guest controls stay at two. Cheap-alternative forecast funding
is still an unresolved hard case. The normal named root HousingMarketCheck
returns exact batch 4; its immutable run archive is retained for helper review,
without assuming the complete failure list before that review.

Source review confirms representation-dependent full/half affordability and
the need to preserve actual sponsored components through coalescence. A new
funding-core mend must keep full, paid-half and sponsored quantities distinct
for eligibility and next-month retention. This is contract accounting on
existing cells/classes, not a new preference-profile axis. Any saved contract
metadata must pass DataSave/load and the real format-65 refusal boundary,
under RES3's owed format/floor 66. No old-save conversion or policy-test
weakening is authorized. Existing guests and truthful reason counters,
new-only strict stock bounds and valid incumbent claims remain mandatory.

The fresh helper verifies the root pricing-only run: exactly HousingMarketCheck
was selected; compilation passed and only the two cheap-alternative funding
assertions failed. All other normal housing cases, including host/pair/guest
price controls, pass on fcc04554a. This is still a red normal harness.

★ Persist actual full-price, paid-half and sponsored quantities on the
existing lease contract. Aggregate HH/count/bill cannot distinguish a full
payer plus sponsored guest from two half payers, so temporary provenance
alone cannot restore valid next-month eligibility. An immutable Funding
quantity tuple adds no preference profile, household cell or identity.
Actual allocation events, scaling, renames and merges must preserve it.
The existing State/DataSave path and load validation/rebuild must carry it,
with meaningful nonzero save/reload coverage. Format/floor 66 remains the
owed bump above shipped 65; write no conversion. Full and half components
must pass their own recurring-price eligibility before aggregation.

A fresh detached helper, RES3-funding-core, starts at fcc04554a with a narrow
brief. It first causes the split/merged B16 and native coalesced-guest
controls, then prepares a peer-only component-aware prototype and focused
checks for review. Parent owns adoption, records and commits. The prior
helper is finished; there is one helper and one JVM lane. MemWatch reads
79.1 percent before this launch. No MonetaryCheck law, rate, window,
coefficient or fixture change is authorized.

The funding-core prototype causes both missing conditions before mending
them. Merged B16 full/half contracts wrongly retain two households where
split contracts retain only the affordable half; native formation also
coalesces an old sponsored group with a new paying group and loses guest
provenance. The component-aware peer now passes normal HousingChoiceCheck
and HousingMarketCheck, including the prior cheap-alternative finding.
These are peer results, pending adoption and integrated policy validation.

Parent source review accepts the accounting shape: actual assignment events
create full, half and sponsored components; census/stock scaling, renames,
denomination and coalescence preserve them. Each retained full or half must
afford its own current recurring bill. Same-class completion expands only
paid halves; later cross-class completion leaves old footprint and bill
exact, updates the remaining payer mode and books only funded new normal
contracts after every ordinary mover. Strict stock checks remain, with
rounding directed down on new additions alone. Sponsored households never
become paid-half deficits merely because their merged cell has a bill.

Load validation reconciles physical occupancy and funded doors by class,
class bills against those funded components, and sponsored quantities
against hosted reasons. A native scratch-slot-10 test carries all three
nonzero quantities through State JSON, denomination, fresh and running
loads, then clears B16: the full-price claim cannot remain by averaging,
and a sponsored claim needs an actually funded host. The format remains
66 above the genuine shipped-65 pair; conversion remains prohibited.

Forecast review additionally requires caused current/next-month census
closure before accepting removal of the old sharing-count undo. The
market holds actual post-sharing contracts; mixing them with pre-sharing
counts spends a source in different coordinates. Incoming-worker
formation memory must still contribute its separate projected cohort.
The helper will prove that basis, finish save/read checks and run the full
unchanged MonetaryCheck on its final source. No policy pass is claimed yet.

The forecast-basis finding is now caused separately. Restoring only the old
pre-sharing count undo on top of the component model fails five assertions:
six B8 singles form one funded five-adult group plus one single, conserving
six heads and 48 resources with held bills 28. The undo discards the held
group's bill 20 and promises five extra doors/new income 40. It does this
both at the first clearing and after FamilyModel rebuild; a free-candidate
control with no future jobs also invents five requests. Actual census,
formed-memory and positive pending-job controls pass. Keep closing demand
with its contracts, and use pre-valve formed memory solely for the separate
incoming cohort. ReadPathCheck also passes on the peer component model.

Adopt the reviewed four-file patch, SHA256
2c44aed7e61ef1693e4d02380e67de7abc50f38f02df4f3f0a913a814ce8a648,
on fcc04554a. Parent independently verifies root/peer SHA256 equality for
all four Java files and a clean diff/patch check. Final normal peer runs
HousingChoiceCheck, HousingMarketCheck, SaveFileCheck and ReadPathCheck
each return 0, archived under the helper's target/funding-choice-final,
funding-market-final, funding-savefile and funding-readpath. The thirty-six
fractional books preserve old A counts/bills bit for bit and strict final
stock after equal-key coalescence. Every earlier boundary remains enabled.
Immutable report: cbs-bus 20261124-124815-141-RES3-funding-core-6907.

Real-city scarce-door save and actual native following-month twins also
pass, with saved full 195.4013914211201 and sponsored 113.16688659135609
households (half zero in that city). The separate all-three-mode native
fixture makes the saved half quantity nonzero; neither substitutes for
the other. Genuine old-format refusal pairs and intact files remain.

Regenerate the fourteen-form/thirty-two-class catalogue from the final
normal market run, retaining historical and modern paid builds. Its small
city has 332 residents and a 29.055 ms integrated month; modern and 1900
observations are 23.884 and 18.271 ms. These are individual laptop timings,
not a performance guarantee. The full unchanged monetary check is running
on precisely the adopted source; no result is claimed in this checkpoint.

The full normal MonetaryCheck on the adopted source completes in 177.2 s,
raw batch 4/harness 1, with 489 OK and exactly two hard failing assertions.
Archive: the helper's target/funding-monetary-final, immutable report
20261124-125323-167-RES3-funding-core-8d99. MonetaryCheck is unchanged.
The original path 48213 has CPI inflation 3.374736859 percent at the
3-percent rate and 7.266503681 percent at 40 percent: an effect of
-3.891766822 percentage points, below the required positive one-point
floor. Non-shelter inflation there falls 11.943969 to 4.627077 percent,
but rent growth rises from -.281825 to 8.355324 percent. The reported
closing rent weight is about 70 percent; its accounting and the native
same-class supply/price phases need diagnosis before attribution.

Across the sixteen declared paths, common-basket non-shelter transmission
passes at +1.333409 points. Total CPI effect is +.555722 points, fitted
slope -.007295, and whole-curve ordering passes. Multi-town timing fails
at .806726401 versus .750 points, at 40 percent late versus on-time.
All three annual timing comparisons, 1.857345286/.512931167/.571070952
points, pass their unchanged two-point bound. Both supply guards pass:
early accepted homes 122/34 at 3/10 percent and later C01 rents
.03043576996/.03170029791. Currency, calendar, basket, physical CPI,
crews and identical founding/dial premises also pass.

No monetary failure is passed as known or attributed away. Authorize a
read-only helper diagnosis of the two failures, preserving this exact
source and every law, rate, seed, fixture, bound and window. First use the
completed artifacts; any needed native paired replay may add only passive
phase reads and must reproduce the native CPI exactly before attribution.
The parent will decide whether the diagnosed channel needs a model mend
or the orchestrator's premise call. There is no new ensemble or dry run.

The one passive default-path 3/40 paired replay completes, scoped batch 0.
Its twelve original rendered CPI/class/component output lines agree,
including the full-double component arrays. Original binary CPI bits were
not archived, so no bit-for-bit CPI-output claim is made. All 642 Java
sources and 2,581 class artifacts are restored byte for byte: 3,223 hashes,
zero mismatches. No diagnostic source or classes remain. Evidence is in
the helper's target/res3-phase-probe (analysis.json, actual-phase-prices.csv,
actual-build-events.json, events.jsonl.gz, native-parity-proof.json and
restoration-proof.json); immutable fourteen-line report
20261124-131242-608-RES3-funding-core-2a76. Its session and jobs have ended.

The actual phases are clearing, sold, reprice, CPI. Closing asks are next
quotes, so they cannot be treated as the current paid bill. At month 37,
raw rent weights are 71.710185/70.503021 percent at 3/40; actual month-49
expenditure shares are 72.393467/70.097763 percent. Base/current rent inputs
use the same chained unit, with no capacity/door unit jump. The large rent
share is real recorded spending, not a class-unit accounting defect.
Weights do use the preceding twelve statement months: at base 37, cash
25-36 rents are 38.262372/36.028504, food 12.587781/12.571959, fees
2.506803/2.501609. Using synchronous 26-37 would give rent weights
71.501695/70.613624 percent. Flag this separate phase-premise concern;
do not change it or claim that it explains either monetary failure.

During months 49-84, cumulative rent contributions are -.593462/+18.485310
points and non-rent +10.758058/+4.217272, yielding the recorded annual
3.374737/7.266504 percent. Original-town common non-shelter inflation falls
7.316892 points. The sixteen-town common-basket non-shelter law also passes
at 1.333409 points; it uses its declared different towns and baskets.

All 72 captured clearings place every household. Funded pressure reconstructs
within 7.2e-14, own-source resources are never exceeded, stock is never
overbooked, and actual bills equal occupied doors times current ask within
4.5e-15. The rent chain reconstructs within 6.1e-18. No remaining funding,
price-demand or rent-accounting defect is observed in this pair.

Actual business acceptances at months 25-48 are 122/34 homes. At 3 percent,
the first Terrace is accepted at 35 and completed at 45; eight structures,
48 doors, finish by 84. At 40 percent none starts. The first Mixed Walk-up
goes 34 to 56 versus 45 to 58. These are native acceptance and BuildLog
events, not flows reconstructed from closing stock. Low-rate T100's paid
rent falls .052593319 to .034078883; at month 60 its demand/occupied is
19.872367 against 24 homes and vacancy disables the full-market floor.
High-rate H120's paid rent rises .041795942 to .060902224; at 84 its funded
demand is 89.416434 against 60 homes, target .070997683 above carry
.037865253. The captured real stock, replacement-capital and scarcity
paths explain the original-town rent split; no calibration change is taken.

The pooled timing failure remains .806726401 points at the high rate.
Town 17 contributes +1.765666151 points; its archived annual rent growth
is 9.526744 versus 11.875487 percent. At month 60, on-time/late H120 demand
is 66.9274/77.3423 at the same 58 homes, with targets .048610050/.056211019.
This is a changed native household/paid-price path, not a noise label.
Its first causal order and completion calendar cannot be proven by the
original-town replay; that limit is explicit. Both hard failures remain
unwaived, and the current source is not mends-ready.

The remaining five named root calls run serially, with a fresh MG1 sample
before each call and cbs.parallel=1, at source 57528718c (production exactly
d82144d00). Completed archives are target/RES3-final-five-20261124/NAME,
with batch.txt, exact exit.txt, memory.txt and copied checks. CallCheck and
DeskCheck return 0; FundCheck and BankCheck return 4; WaterCheck returns 0.
The runner's final 0 means completion of all calls, not passing harnesses.
No second root JVM runs beside them; the runner has ended.

Call selects only CallCheck, 207 OK lines, with every sale, call, both
worth-call sides and save branch reached. The affected bank-at-sale, room,
same-face, coupon-step tails, one callable piece, reopening/first-date,
actual central-bank holding and worth-call fixture guards all pass. The
94ba28823..e6a47dff8 diff changes playedCity/theSale to keepBankOverDesk;
it adds no assertion labels, so no invented literal six-plus-two grouping
is claimed. Desk selects only DeskCheck, 291 passing assertions. Its spare
D$14,897 is restored and reopening grows 44,690 to 89,381. Immutable helper
reports 54b8/e77e give the fired labels and log lines.

Fund selects only FundCheck, harness 1 in 28.5 s. Sections 9b and 9d's
market-book, rescue, zero-cash, bond-room/toRaise-before-idle and audit cases
execute and pass. Section 9e fails its opening no-secured-claim fixture
guard, then throws before any in-kind gift, its 240-month run or sections
10-13. This is not section 10's queued HT1-H limit-posting red. The selector
chooses the highest observed 24-month buyback rate with enough household
stock but no lien eligibility condition; the subsequent guard requires
every cell's claim and tagged shares for that company to be exactly zero.
Inherited source is not proof of an outside-RES3 cause. Native buyer,
offending cell/count and claim/tag amounts were not printed, and TempDirs
removed the scratch opening at process exit. No personal saves were read.
The real liability versus residual/inactive-cell distinction is unproved.

Authorize a peer-only passive Fund observation before that unchanged guard,
with current buyer and per-cell stock/basis/claim/tag/count plus byte copies
of the existing native scratch opening retained outside TempDirs. The
reviewed patch changes no selector, guard, throw, input, probe, 240-month
window, cap/idle or audit law. No extra Game/save call or financial mutation
is added. A lawful fixture mend must follow the actual observation, with
no automatic known-red assignment, skip or claim forgiveness.

The Bank filter selects CentralBankCheck (1, 1740.1 s), BankCheck (1,
38.5 s) and YoungBankCheck (0, 7.3 s). Both Bank failures match the existing
HT1-H classifier lines: defaults fire at month 20 on a 569.75k claim and
36.21k is written off, but net kept is positive 265.51k, so its required
net-loss condition is absent. No new waiver is inferred. The current log
does not isolate each income component. Water selects only WaterCheck and
passes in 3.9 s; YoungBank reaches its all-passed terminator.

Central's sole failure is its 1740-second timeout. It advances through
howStrict's deliberate twenty-four shapes, each with 600 founding months
and two 240-month twins, reaching ten shapes before timeout. Repeated
completed shape rows show progress; no deadlock is established. This
neither proves an outside-RES3 load issue nor excludes a per-month runtime
regression. No timing threshold, shape or law is changed or passed away.
Completed classification report: 20261124-135434-401-RES3-final-logs-e396.
The next native Fund observation owns the single JVM lane; final fast/maps
and any further diagnosis remain pending.

The passive native FundCheck reproduces section 9e's original failure in
14.6 s, then hands the JVM lane back. Peer source is restored byte for byte;
the production model is unchanged. Selected buyer is Real Estate, opening
month 73, with 3020.025596006526 issued shares, 2420.5774943801575 household
shares and 302.0025596006526 needed at the cap. Four UNSKILLED single,
couple, baby and teen cells have live counts .701140-.950207, all above
EMPTY_CELL .5, and finite positive claims with proper tagged-share subsets.
Their claims sum .0001418278887181453 versus governmentClaim
.0001418278887181452, a 1.0842e-19 difference. Tagged shares total
1.007946219828638. The native opening and loaded values agree bit for bit.
These are real accounted stocks, not empty cells, nonfinite values, a load
artifact or rounding residue. Their creating assessment event remains
unobserved; no outside-RES3 attribution is claimed.

Only Real Estate and Construction retire positive stock in the unchanged
24-month probe; both carry valid claims. There is no clean positive
candidate, so an all-company zero-lien selector cannot cause this fixture.
Evidence: the helper's target/RES3-fund9e-native-20261124 and retained
RES3-fund9e-observation-27788; immutable report
20261124-140110-710-RES3-final-logs-e454. The initial launcher aborts on
PowerShell's stderr handling before starting a child; the corrected launch
is the only actual passive FundCheck run.

★ Keep the native opening, tax policy, 24-month probe and 240-month cap/idle
run. Cause an unencumbered in-kind gift by moving only actual free units,
retaining every claim and tagged share with its existing holder. Use the
model's basisReleased/receivedShareInKind accounting for the corresponding
basis. Retarget the whole-company zero-claim setup predicate to the units
actually transferred and add before/after stock, basis, cash, counts and
exact claim/tag invariants. No tax is paid, cleared, diluted or forgiven;
no seed/month search, skip, invented cash or relaxed tolerance is allowed.
The highest positive observed buyer must have enough free stock. The peer
is authorized to prepare this FundCheck-only mend and run the complete
normal named harness; adoption still requires parent source review. No
result is claimed while that run is pending.

The complete normal peer ./batch-check.sh FundCheck returns 0 on the
reviewed one-file mend: compile 23 s, exactly FundCheck selected, harness
0 in 22.0 s, maps green and Stale zero firm/206 soft. All sections 1-13
reach their assertions and the log ends All checks passed, without a
remaining FAIL or exception. The original section-10 limit-posting case
now also reaches and passes; no known-red assignment is needed for Fund.
Immutable report: 20261124-141311-711-RES3-final-logs-524b. Source/patch
hashes and the completed batch, checks and launch sample are retained in
the helper's target/RES3-fund9e-mend-20261124. All helper jobs have ended.

The native claimed donor actually supplies free units. Every other cell
stock, government claim/tag/tax entry, treasury/fund/bank cash and issued
share count remains bit unchanged; combined shares and acquisition basis
are conserved and all retained tags remain backed. Real Estate still
reaches the original ten-percent opening cap. Its 240-month run has cap
asks in 138 months, idle payouts in 63 and buyer buybacks in 129, with
240/240 audits closing, no unmet ask and maximum holding 10.2167 percent
below the unchanged 10.9191-percent fixed point. Both section-10 market
twins, native save, Insane and never-borrowing cases execute and pass.

Parent source review accepts this fixture-only allocation and independently
verifies the root/peer source hash
d8f5d9a599d516b2db28efd4ab553101a57071cf183f8d1313edd86c50a28122.
Adopt patch 51c91268ce55fbfc95ddcbfd46272ae3d0440f1b3ebd54153aa4d7adbae517dd.
The production model remains d82144d00; no monetary law, fixture, window,
seed or bound has moved. Final root fast/maps remains to run. The two
monetary failures still require the orchestrator's premise decision, and
Central's timeout remains unattributed and unwaived. No additional long
runtime experiment is started before that call.

The final root fast attempt does not start a JVM: MG1 finds 2397 MiB
available RAM at 14:15, below its 4096-MiB floor. Rechecks find 3406, 2005
and then 2140 MiB, still below the floor; free commit remains above 2.5 GiB.
The guard is not overridden and no other session's process is stopped.
This is a resource block, not a failed compile or a claimed passing fast
check. Every RES3 simulation and helper job has ended.

During this check pass, CY1 ships as main b3ff7eb6d, 0.9.199. Merge it in
895ccac01: BusinessInvestment, GameVersion and SaveFileCheck merge cleanly,
retaining CY1's processing-year coverage and all RES3 native refusal/reload
cases. Main still uses format/floor 65; RES3 retains its owed 66. The
completed checks, catalogue/profile and monetary figures above are from
the pre-CY1 model and are not validation of this merged head. The newest
ensemble baseline is now docs/ensemble/0.9.199.md.

Preserve the generated pre-merge index changes in stash
8ce777a3e931e824e6073c384159e32c1143e01b, labelled RES3 generated maps before
CY1 merge. Do not pop stale generated pages over the merged model; fresh
root fast/maps must regenerate them when MG1 permits. The catalogue and
affected named checks, especially the monetary and save paths, also need
fresh merged-model results before review readiness. No new ensemble, dry
run, mends request or ready claim is made. Report the real resource block
and the qualified pre-merge monetary counterexample to the orchestrator;
replay the merged model before deciding whether its kept premise still
needs a call. No model or bound is changed to escape the findings.

Static checks still run without a JVM: the ship known-red parser returns
0 and the pick remains ALL 253, with a required saves review; the money
delta also explicitly needs review. No publisher record differs from
origin/main through a branch edit. Older root-fast outputs, if present,
are historical and must not be read as merged validation. The current
not-started marker is target/RES3-root-fast-current-status.json; resumption
uses target/RES3-root-fast-merged-cy1-20261124.sh, which samples MG1 first
and writes source-labelled new logs rather than reusing old output.

The orchestrator's 14:30 call (mail
20261124-143014-040-orchestrator-9d23) keeps the total high-versus-low
MonetaryCheck effect at or above its positive one-point floor. The earlier
premise-call recommendation was wrong: observing a valid shelter supply
response did not establish a valid total CPI response. Mend the model's
rent calibration or expenditure coverage/weight if the measured burden is
too high; keep the law, window, rates, seeds, bounds and supply assertions.
First measure default-city months 240-600 against actual housing-cost and
CPI benchmarks. No calibration target is chosen before that measurement.

The same call authorizes heavy named replays on the gate box, from the
pushed commit, holding box.lock shared and running one model at a time,
following FC2-notes section 6. Laptop RAM will not clear soon; do not wait
for it or run heavy replays here. Central's laptop timeout is treated as
under-pressure until the box provides its verdict. Fresh sub-session
RES3-rent-weight starts at fb2f3a74d with the measurement and source diagnosis;
parent keeps decisions, review and records. It has no model-edit authority
yet. It reports the reachable box's existing ensemble write lock and
prepares passive capture while waiting, with no laptop JVM.

Verified benchmarks, retrieved 2026-11-24:

| Measure | Published benchmark | Interpretation/source |
|---|---:|---|
| U.S. CPI-U shelter, December 2025 | 35.625% | Includes owners' equivalent rent; primary-residence rent alone is 7.840%, not the whole shelter service. [BLS relative importance](https://www.bls.gov/cpi/tables/relative-importance/2025.htm), [definitions](https://www.bls.gov/cpi/factsheets/owners-equivalent-rent-and-rent.htm). |
| Canadian 2026 basket, 2025 expenditures | 28.51% shelter | Reference-period expenditure share; the article distinguishes it from price-updated link-month weights. [Statistics Canada](https://www150.statcan.gc.ca/n1/pub/62f0014m/62f0014m2026005-eng.htm). |
| U.S. cash-paying renter households, 2024 ACS | 30.9% median gross-rent/income | Table estimate; the release rounds it to 31%. Gross rent includes utilities. [B25071](https://data.census.gov/table/ACSDT1Y2024.B25071), [release](https://www.census.gov/newsroom/press-releases/2025/acs-1-year-estimates.html). |
| Canadian affordability standard | Less than 30% before-tax income | A normative standard, not an observed population median or CPI weight. [CMHC](https://www.cmhc-schl.gc.ca/professionals/housing-markets-data-and-research/housing-research/core-housing-need/identifying-core-housing-need). |

The income burden and CPI expenditure share have different denominators;
do not substitute one for the other, or compare the model's whole shelter
service with only the U.S. primary-rent subcomponent. Model measurements
must report actual-flow/statement timing and paying versus sponsored
households. BLS statistical material is public domain with attribution
([policy](https://www.bls.gov/bls/linksite.htm)); Statistics Canada data is
under its commercially reusable [Open Licence](https://www.statcan.gc.ca/en/terms-conditions/open-licence).

The remote native default capture completes through month 600 on pushed
fb2f3a74d/CY1, world RANDOM 48213 and default ensemble variation 0, Temurin
21.0.13+9. It holds box.lock shared after PW3e's ensemble releases it.
Compile and diagnostic run return 0; native audits execute before the
observer, but its deliberate month-600 stop is not a LongPlaytest pass.
Only two passive scratch calls are added, after household settlement and
after native audit; no mathematical statement, RNG, policy or month order
changes. Actual settlement is paired with the preceding cleared leases,
not the next clearing. Reports 95a3/6c03 and the source/flow manifest are
retained in the helper's target/rent-weight/box.

Months 240-600 inclusive give 361 observations. Rent is 40.7061 percent of
the measured consumption bills; actual-funding bounds are 40.7058-40.7231
percent. Ambiguous rent funding is small; most unfunded bills are in
zero-rent cells. Aggregate rent/primary gross income is 32.481 percent,
or 24.233 percent after observed property receipts are included. These
are aggregate ratios, not Census's cash-paying renter median. Paying
full/half cohorts have 37.815 percent against primary income, but property
income is not yet matched safely to those cohorts, so no full-income
renter distribution is claimed. This evidence does not support a blanket
30-percent income cap.

Rent/consumption at 240/360/480/600 is
43.028/28.978/41.687/37.721 percent; the native CPI rent weights are
42.968/41.713/37.535/44.781 percent, averaging 41.187 percent over the
window. It is not the early prewarmed fixture's 71 percent. The four native
relinks expose raw-to-applied rent shares 41.633 to 42.968, 39.258 to
41.713, 29.881 to 37.535 and 42.096 to 44.781 percent: the fifteen-percent
luxury cap raises rent's share by 1.335/2.455/7.654/2.685 points. Observed
vehicle purchases, 4.43 percent of measured consumption, are absent from
CPI; fuel, .67 percent, is grouped with groceries but priced with grocery
shelf prices. Meals and luxury purchases also need separate treatment
when reading the household spending headline. The early fixture's full
coverage/cap decomposition remains under analysis; no model target or
cap change is selected merely from these figures.

The subsequent clean, uninstrumented remote MonetaryCheck at the same
pushed head reproduces exactly two hard failures: original total effect
-3.891767 points against the kept +1 floor, and pooled delay .806726
against .750. All annual timing, non-shelter transmission, curve,
currency, saving-plan, physical-basket, calendar and supply guards pass.
The head is now validated as still defective after CY1, rather than using
an obsolete pre-merge result. Report 64c2 retains the clean manifest/log;
the measurement and baseline jobs have both ended. The helper continues
only analysis of existing captures for a sourced model proposal.

The focused passive original 3/40 pair reproduces both current full-double
Monetary component lines exactly, including the -3.891766822369-point
effect, with all native pair guards passing. It confirms that the luxury
cap is inactive at the original base: meals, luxury, vehicles and fuel
have zero base spending. Actual later Boutique/Diner completion and first
purchases explain that difference from the native default city. Removing
the luxury cap or adding its absent base vehicles is not a demonstrated
remedy for the original failure. Income receipt credits reconcile to the
native audit within 4.45e-15; settlement census counts match exactly and
prior lease bills within 2.23e-16.

The original base's paying renter cohort has gross-income burdens
43.274/41.557 percent at 3/40, with representative-cell weighted medians
54.877/48.864 percent at month 37. Aggregate income across all households
masks that burden because much property income reaches other cohorts.
By month 84 those medians are 30.303/39.818 percent. These measured model
cohorts are not Census microdata; the before-tax money-income comparison
excludes principal, capital sale proceeds and gains. Report e2b7 and the
helper's pair-box analyses retain the exact definitions and limits.

★ First correct actual positive fractional-household settlement. The
existing empty-cell branch skips cells below .5 despite allocating real
lease services and income to them. Original base settled rent is
34.187918/32.228720k versus booked 38.262372/36.028504k; the difference
4.074454/3.799784k is recorded as unsettled rent. Do not merely delete a
legitimate consumed service from CPI. Cause a fractional-recipient money
regression, reconcile allocation and settlement, retain true zero-cell
and genuinely unfunded bookkeeping, and preserve cash/card/debt/claim
conservation. Do not globally change unrelated EMPTY_CELL eligibility.

★ Separately correct the zero-standing-stock quote's gross/net semantics.
The source audit confirms .058 is a gross advertised-rent/list-value yield,
not a net equity return. Spec section 4.4 expressly preserves the gross
comparator rather than adding all operations as though it were net. Live
standing pricing already compares its gross scarcity target with a
conditional actual-cost floor and waives that floor in a glut; leave that
unchanged. Unbuilt opening/reprice instead adds actual carry to the gross
anchor. Use the larger of gross comparator and actual cost-recovery need,
retaining cost coverage, actual expense booking and underwriting. Keep
the .058 value, scarcity elasticity and lease update unchanged. This is
not a claim that interest was booked twice or that the two corrections
will by themselves pass MonetaryCheck.

The completed yield/carry source audit is retained as the prior helper's
yield-carry-audit.txt, report 490a. All its jobs ended and that session is
stopped. A fresh RES3-fraction-pay builder prepares only these two paths
and caused regressions in its own worktree. Parent reviews before adoption
and pushes the candidate before remote native checks. No numerical
retuning, rent/income cap, CPI window change or Monetary-law change is
authorized by this implementation scope.

The source review accepts the two narrow corrections and their caused
regressions. Positive counts now settle, retain their own stocks through
census/load replanning and receive the corresponding grocery quantities;
optional credit, investment and estate eligibility keeps its .5 guard.
No new saved field or public API is added. The already-owed format 66
covers the unshipped RES3 model; the genuine format-65 refusal pair stays.
The old aggregate stock and shrinking-census checks remain, with positive
ownership made explicit and a separate exact-zero clearing case.

Review changed the quote finding to assert each comparable class before
reconciling a recipe: sum(max(anchor,cost)) is not generally the maximum
of the two recipe sums. It also removed a proposed early-return test
selector. Both cost sides are caused by actual property-tax inputs;
standing-stock, glut, funding and actual expense rules are unchanged.
Test-only commit d379c33e1 precedes the production correction so the new
findings can be demonstrated against the old pushed model. No compile or
JVM has run on this candidate yet; remote validation remains required.

Remote old/new finding proof (24 November, 15:50): the test-only pushed
commit d379c33e155c8d6ed891ba60c2da54b8e6b4a41d compiles on the box,
as does a temporary package driver calling the two new caused sections.
It fails 20 of the 110 assertions: 16 fractional financial assertions and
four gross-quote assertions. The exact pushed candidate
0894946a75bbf191e06d4ea04c184ac5953c1bdc compiles and passes all 110,
with no fixture repair or source/test alteration. These are caused finding
proofs, not substitutes for the normal named harnesses or MonetaryCheck.
Native Temurin/data/source hashes are in finding-old.manifest.txt under
~/cbs/runs/res3-fraction-pay-20261124-old-d379c33e1 and
finding-new.manifest.txt under
~/cbs/runs/res3-fraction-pay-20261124-candidate-0894946a7. Shared box.lock
covers each native compile/run. No laptop JVM ran. The helper proceeds
with separate normal HouseholdCheck, HousingMarketCheck and unchanged
MonetaryCheck calls; their results are still pending at this checkpoint.

The first normal named HouseholdCheck passes (batch exit 0, exactly one
selected harness) on 0894946a7. Normal HousingMarketCheck fails: the
corrected classification is 31 stale additive unbuilt-quote expectations
in fractionalPressure and 55 household-money conservation failures across
integrated fixture months, first foundingBudgets month 4. The caused 110
assertions still pass; no pool-conservation failure is reported. The
conservation failure is a real counterpart defect to diagnose and mend,
not a waived finding. MonetaryCheck is held until that counterpart is
fixed. A peer-only correction of the stale additive expectation is
authorized; every cash/claim/audit bound remains unchanged. Reports efb0
and 5aeb retain the exact normal run and distinguish the two failure
classes. No JVM remains from those finished checks.

The bounded native founding diagnostic proves the first conservation
counterpart: at month 4, bank deposit-interest debit is
0.0005624801107814218, while actual weighted depositIncome credited is
0.0002881474130932002. The missing 0.00027433269768822156 equals the
household residual -0.00027433269768714543 within 1.08e-15. Account fees,
lending, settlement allocation and the pool audit reconcile; genuinely
unfunded 0.005729148740415714 remains explicit. MoneyAudit uses the actual
weighted savings and has no empty-cell exception to relax.

★ Pay actual deposit interest to each positive balance owner.
creditDepositInterest's totalSavings denominator now retains fractional
owners, while its old isEmpty payment guard still skips them. Change only
the recipient count guard, keep the denominator, total bank debit and
weights, and cause a deposit ownership/counterpart regression. Optional
whole-cell credit eligibility remains unchanged. Reports 7343/8eaa prove
the exact source and recipient difference; full HousingMarketCheck must
still pass every repeated conservation guard after the pushed correction.

The narrow source audit also finds existing paper/bond payments affected
by preserving positive owners. paperWeight's totalPaper denominator
includes them, but its isEmpty recipient guard drops their share of
coupon, principal and buyback payments. creditBondCoupons instead calls
a positive fractional bond owner's coupon orphaned and redistributes it.
★ Pay those actual owned claims to the positive owner, keeping genuine
ownerless fallback, totals, principal/basis changes and all optional
purchase eligibility unchanged. Cause ownership, source debit and face
conservation at quarter/half/just-below-half/zero counts before claiming
these corrections verified. This is financial counterpart completion,
not a numeric rent or CPI calibration. Report f321 distinguishes these
source findings from the exactly proved founding deposit residual.

Payment finding proof and integrated replay: harness-only overlay on
pushed 0894946a7 leaves production unchanged and fails 28 of 227 actual
assertions (eight deposit, twenty paper/bond ownership). The corrected
examples in report d489 supersede d7cb's transcription: quarter deposit
debit 1 credits .8803911882965281; principal 5 pays/redeems 4; buyback
cash 11 pays 8 and face 5.5 redeems 4; the fractional owner's recorded
bond coupon .5 is redirected away from it. Counts and cause are unchanged.
The half and exact-zero cases pass there. Native compile/driver exits are
0/0, finding exit 1; source/data/JDK manifests remain with the old proof.

The exact pushed corrected candidate
3d2cb76debbb10ebd5e230ee09308bc351f5dcf2 compiles and passes all 227
caused assertions without source/test repair. Separate normal named
HouseholdCheck and HousingMarketCheck both return batch exit 0 with
exactly one selected harness each. Every prior 55 household-conservation
failure now passes, together with all catalogue, stock, save and lease
guards and the 31 corrected gross-quote expectations. Shared-lock native
runs are under ~/cbs/runs/res3-fraction-pay-20261124-candidate-3d2cb76de;
reports 3bfd/70f4 retain the source-labelled evidence. Unchanged full
MonetaryCheck now runs on that exact candidate; its result is pending.

The unchanged full MonetaryCheck on exact 3d2cb76de finishes with 489
assertions passing and two hard failures (native exit 1, batch exit 4).
The original total effect improves from -3.891767 points to +0.748
printed points, still below the unchanged +1 floor. Original 3/10-rate
accepted starts are 26/29 in months 25-48, so the original supply-order
assertion also fails. Later comparable C01 rents
.02362122335/.02362469045 satisfy their later-rent assertion. These are
open model findings, not known reds or premise changes.

Across declared paths 16-31, mean total effect is +1.932654 points and
non-shelter effect +1.806459; pooled whole-window delay .330867 is below
.750, and maximum calendar-year reversal 1.196466 is below 2. Currency,
physical/calendar/crew, fixed-basket reconstruction, saving-plan and
curve guards pass. Report 4df6 keeps current native source, exits and
all guard classes under the helper's target/fraction-pay/native/3d2;
all its test processes have ended. A fresh tracing session is required
for the remaining original-town effect and supply response; no calibration
or law change is inferred from their closeness to the floor.

Current passive monetary/default diagnosis (d2eb/f44f, exact c2d85d597,
production identical to 3d2cb76de): all native capture guards pass and
all original component/weight doubles match the clean completed monetary
run exactly. Original annual total inflation at 3/10/40 is
.14591404778818662/.14188799551962683/.13843167909369813; total effect
is +0.74823686944884840 percentage points. Shelter and the common
physical non-shelter basket each transmit about .74 points. Reducing
shelter weight toward non-shelter therefore does not explain the missing
quarter point in this fixture. Original base meals/luxury/cars/fuel remain
zero and luxury cap is inactive; no window/rebase escape is justified.

The supply trace finds different project selection and available cash,
rather than a reversed fixed-input lender test. At low-rate month 45 the
mid-rise gross income/cost score .005484259853846704 beats the house's
.005429462448169069, while their actual operating-net income/cost is
.0018324526914791487 versus .0023970347520867997. The selected mid-rise
fails lender coverage (.624053 versus 1.20), and no alternative is tried
that month. The 10-percent path chooses the house and starts 23 doors at
45 plus two in each following month; the 3-percent path starts 22 at 46,
then two and two. This yields the observed 26/29 early-door reversal.
Later C01 ask differences pass their check, but C01 has no standing homes
or actual trades at months 49-60; this is an ask result, not paid-rent
transmission. That limitation remains explicit.

★ Rank housing alternatives on forecast net operating income per actual
project cost, subtracting the existing housingCarry before interest.
Keep forecast receipts, physical demand, the build hurdle, all mortgage,
equity and land gates, and paid settlement. This corrects the demonstrated
cost-blind gross ranking without a fitted dial. The Federal Reserve Board's
[Real Estate Loans 3100.1, page 6](https://www.federalreserve.gov/boarddocs/SupManual/us_branches/usbranch.pdf#page=146)
uses net operating income in income-property valuation and evaluates loan
service from achievable rents, occupancy and expenses. This project's
ranking rule is an engineering inference from that source, not an
imported empirical coefficient. Board text is public domain with credit,
as its [disclaimer](https://www.federalreserve.gov/disclaimer.htm) states.
No claim either remaining monetary assertion is fixed before replay.

Current native DEFAULT random48213/variation0, months 240-600 inclusive:
rent/consumption 40.618235441 percent, mean base CPI shelter weight
48.645020098 (41.204563351-51.977948821). Paying-renter gross aggregate
burden is 35.961605100 percent, while all-household gross 19.757952430
masks it. Paying-cell weighted gross-income rent-only medians at
240/360/480/600 are 42.521505514/47.096696974/35.265893665/44.748801020.
This proxy allocates actual wages/pensions/benefits and observed property
receipts, excludes principal/assetsales/gains, and differs from ACS's
cash-renter gross-rent-plus-utilities microdata median 30.9. Actual new
cars are 5.914469519 percent of consumption and omitted from CPI; petrol
.621911723 is counted at the grocery rather than pump price. Luxury's
15-percent cap raises rent weight at link 273 from raw 40.937398727 to
51.977948821 and link 393 from 39.234424967 to 50.826277426. Thus mature
index weight excess is measured, beyond actual rent burden. The official
BLS/StatsCan expenditure and shelter benchmark distinctions cited above
still apply; these coverage issues have zero direct base-quantity effect
in the original monetary fixture and are not a proven floor remedy.

The current captures' compile/pair/default exits are 0/0/0, all own jobs
ended, native data/JDK hashes and passive diffs retained under the trace
helper's target/policy-trace and
~/cbs/runs/res3-policy-trace-c2d85d597-20261124. Property-credit audit
reconciliation is within 6.22e-15 in the original and 7.28e-12 in default;
recipient counts match exactly and prior default lease bills within
2.28e-13. No production or law changed during diagnosis.

The source review accepts the one-score net operating ranking correction.
The added scratch regression uses actual tariffs and plots to cause a
gross/net choice reversal, retains a gross-and-net winner countercase,
and keeps zero/negative NOI, land and funded-demand rejection cases. It
also checks repeated plans against cash pools, leases, inventory and
complete native save bytes. No new API, field, dial or saved state is
introduced. The legacy untyped branch likewise ranks its existing
fillable rent after actual operating costs; financing remains separate.
All fixture prerequisites are explicit and still require native proof.
The parent also corrects the catalogue generator's machine wording to
"executing host", since these heavy runs now use the gate box. No native
result is claimed for this candidate before the pushed-source replay.

While the box is occupied, bounded provenance report 0112 finds
PriceIndex.LUXURY_WEIGHT_CAP=.15 at lines 108-109, introduced in the
available d6f1512e159d8d5e4614ed07f769d8990570e42a source history;
the basket design dates itself to 0.7.43. spec-inflation (P1a, 4 October),
section 2.8 and decision 6 call it a starred engineering choice because
one city at month 2400 spent 63 percent on a watch/import at FX times
margin. No external empirical basis for 15 percent or explicit Jerus
quote choosing it is found. The later one-third RENT_CAP is a distinct
explicit XR1 currency-parity rule and stays separate. The five-component
basket design states no principled vehicle or motor-fuel exclusion;
native fuel arrived after the original basket. No CPI source change is
made in this provenance check. The current NOI native path waits behind
PW3f's box write lock; no own compile/JVM has started.

The first old-scorer native fixture run has 36 passing and three failing
assertions, so it is not yet a valid old-scorer proof. The net-dominance
prerequisite itself is false, and the expected-choice failure follows;
full saved-byte equality also fails. The source diagnosis identifies
endowed construction material stock: the actual paid invoice deducts
that stock, while operating carry prices replacement materials. No model
fault is established by this fixture. ★ Initialize the fixture's material
stock to zero before both reads and assert it, preserving all gross/net,
hurdle and rejection conditions. This makes purchased materials common
to the invoice and operating-cost comparison rather than fitting rent.

The native save mismatch is exactly write metadata savedAt: Game stamps
each save and DataSave records it. ★ Compare complete native JSON with
only the second root savedAt normalized to the first's exact timestamp,
while checking both metadata fields exist. Preserve exact serialization
of every other field/numeric value, both real scratch saves and the
separate cash/lease/inventory checks. This repairs the new test's invalid
same-timestamp premise; no existing conservation, SaveFileCheck,
ReadPathCheck or load/refusal guard changes. Reports 8fe1/0bd9 retain the
failed fixture, exact model/source and cause. Candidate 62da was never
compiled/run at this stage; no HMC/Invest/Mon result is claimed for it.
The approved repair is test-only and awaits review/push before replay.

Corrected old/new net-choice proof: old c2d85d597 production plus the full
corrected finding passes 40 assertions and fails only the actual planner
choice; exact pushed ffa2a3b558a59608d1f9f9e6cf013cf910fd271b passes
all 41. Full normal HousingMarketCheck also passes, including all cash,
lease, inventory and native saved-text guards. Actual catalogue and
source-labelled generated documentation are archived with the native run.

Unchanged full MonetaryCheck on that exact candidate passes all 491 hard
assertions (compile/harness/batch 0, 107 seconds). Printed original total
effect is +5.526 percentage points, with printed inflation 24.54/19.01
percent at 3/40; these rounded figures are not labelled full precision.
Pooled total effect is +2.643556 points, non-shelter +1.599910, policy
slope -.069005, delay .218575 below .750 and maximum calendar-year
reversal .7042097487189467 below 2. All currency, physical basket,
calendar, crew, base-before-window and saving-plan guards pass unchanged.
Original accepted starts are 44/23 at 3/10. Later C01 asks
.02674760844/.02741129335 pass, with actual stock/trades still awaiting
the authorized passive verification; no paid-rent conclusion is inferred.
Report 3057 retains all guard labels and unchanged Monetary source hash.

InvestCheck has a new unchanged quantity predicate failure, alongside its
known builders-wage premise. The active typed planner chooses a Large
high-rise; the five-plot plan returns five, while the unlimited predicate
requires more than five. The exact unlimited decision was not logged,
so the authorized bounded replay must establish its actual sizing and
hurdle before any classification or fixture change. Source report 2e89
rules out zero bestScore as the cause in this active fixture: each typed
candidate passing the same forecast-income hurdle has positive NOI.
Separately it identifies an unintended legacy gate from zero bestScore:
full-occupancy profitability can pass while partial fillable income less
full carry cannot beat zero. That behavior must be repaired while retaining
the existing full-occupancy WHETHER and fillable-door WHICH semantics, with a caused legacy
regression; no such source change has yet been adopted. No assertion or
threshold is changed in this diagnosis.

The bounded quantity extension (a19c) proves the unchanged Invest fixture
already causes its >5 condition. On the same unrestricted state and
unchanged quote/carry, six projects earn 106.58568465542092 against the
existing 95.338992998680420 hurdle; seven earn 108.98909208883254 below
111.22882516512716. Five remains feasible. The coarse search skips five
and six and returns four. ★ Refine its last rejected/accepted integer
bracket using the same actual forecast and hurdle, accepting only directly
verified feasible quantities within the original wanted/land bound.
Here rejected 9/accepted 4 probes 6 (pass), then 7 (fail), and returns 6.
Do not enlarge/rebudget this fixture to avoid the owned search defect.
Forecast's preference-based matching does not prove globally monotone
rental revenue, so this refinement is not advertised as a global maximum;
it returns a directly verified result at least as large as the prior
accepted result, with logarithmically bounded additional probes.

Completed passive FFA supply proof retains all 32 classes, all 12 later
months and both rates, with exact clean-run component/weight parity.
C01 has positive physical stock and actual trades in every month at both
rates: physical homes 50 to 72 versus 39 to 71, mean paid rent per door
.026157975829315686 versus .026971923076108545. The predefined founding
House control was also captured regardless of outcome and has the
opposite comparison (.045102163828695120/.044825779667611441); no class
was substituted. ★ Strengthen the normal MonetaryCheck with explicit
C01 physical/trade presence and actual paid-rent comparison, keeping the
existing ask assertion, declared class, rates, windows and every bound.
This prevents its earlier unbuilt-ask pass from masquerading as paid-rent
transmission. The first target-only observer attempt was separately
archived after an absent-inventory-key error; the repaired observation
records absent quoted stock as zero, changes no model input, and completes
with exact parity. Reports 2195/a19c retain all native manifests; all
owned jobs have ended. Fresh source work will cover the search refinement,
legacy candidate eligibility and stronger paid evidence before any new
production replay. CPI coverage remains separate open work.

The fresh source review accepts the three mends. Typed order sizing keeps
its last rejected/accepted bracket and directly checks every accepted
midpoint with the existing forecast and operating hurdle; it preserves the
prior feasible lower, original caps and lender path. No global optimum or
monotone preference-based revenue is assumed. Legacy selection explicitly
retains positive gross income and chooses the first eligible candidate
before comparing net scores, so a negative partial-fill net cannot add a
new eligibility gate after the existing full-occupancy hurdle. Its new
fixture uses the public inactive-market path, real family stock and a
sparse shortfall derived from actual costs/rent, with all prerequisites
asserted before the selection claim. It remains unrun at this checkpoint.

Invest's original land inputs and >5 assertion stay unchanged, with direct
returned/next-order hurdle checks added. Monetary retains all 491 old
assertions and adds declared C01 physical/trade presence in every later
month and higher actual paid rent per door; no fallback to asks is used.
The original scalar effect print now preserves round-trip precision.
There is no new public API, dial, model saved field or format change.
Source patches were statically checked and reviewed before adoption;
native old/new finding proof and named harnesses still follow the push.

For the remaining CPI correction, the parent verifies the primary BLS
[calculation methodology](https://www.bls.gov/opub/hom/cpi/calculation.htm):
upper-level weights represent reference-period consumer expenditures;
the current sample spans 12 months and expenditure references update
annually. That supports retaining this model's fixed quantities within
its existing link period while reconciling actual consumption coverage
and weights. It supplies no numerical US shelter target for this city.
The source-only plan must identify actual saved vehicle/fuel flows and
prices, state/load dimensions and caused cash/reload/price-only checks;
no CPI edit or probe is yet adopted. The new order-proof native run is
queued at the shared box lock with no compile/JVM started.

The next native attempt stops at compile, before any finding or named
harness runs: the new Invest quantity block redeclares main's existing
forecast local. Review missed that enclosing name. The test-only mend
renames four references to quantityForecast; every assertion, input and
model line stays unchanged. Reports 0f10/bca7 retain the exact compiler
error and empty old-production diff. No current 761 native pass is
claimed. The reviewed rename is pushed before repeating the old/current
proof and normal named calls.

The bounded CPI plan identifies actual Motoring new-car flow fields not
yet in DataSave, existing saved fuel and pump/queue flow fields, explicit
five-component HistorySave lists, and the need to avoid pump/queue mix
reading as inflation. It remains a source plan only. Cadence clarification
767c/e97f keeps REBASE_MONTHS=120: annual January linking was not
approved by this batch and cannot be silently imported from BLS while
fixed-basket policy premises remain. Also, zero new-car/fuel/luxury/meal
base spending was measured before the NOI mend, not established for the
current model; no claim a future CPI correction has zero original effect.
Every monetary law must still pass on its final model.

The corrected old FFA legacy proof now compiles and runs: ten assertions
pass and only the intended eligible partial-fill choice fails; all seven
prerequisites and three refusal cases pass. Its exact corrected Invest
source hash is retained. Candidate 3d84 then stops at compile because the
new Monetary guard calls public field ClassState.dwelling as dwelling().
The one-line test-only correction uses the field. Review verifies the
other new class trade fields and physical Stock.homes accessor against
the actual declarations. No candidate driver or normal HMC/Invest/Mon
runs before that compile stop, and no pass is inferred for 3d84.
Report 29e9 retains exact source/asset/JDK manifests and old proof;
its proof is reusable because the corrected Invest and old production
remain byte-identical. Only the candidate needs its compile and runtime
checks after the new pushed correction; no model behavior changes.

Exact pushed 1980899a9aa5d01015370ddbf982c552f1500c47 now validates:
compile 0, legacy driver 11 passing; normal HousingMarketCheck 0,
InvestCheck 3 with no new reds, and MonetaryCheck 0 in 104 seconds.
Invest retains every original land input and >5 predicate, with returned
hurdle/next-quantity and legacy eligibility assertions passing. Its sole
used known row is HT1-H's builders-wage premise, exactly matched from
main known-reds line 107; no new Invest finding is passed by name.

All 494 monetary assertions pass, including every old 491 assertion and
three new actual paid-supply checks. The full-precision original total
effect is 5.5261306149104295 points; pooled total
2.6435558954015903 and non-shelter 1.5999095311524307 points pass.
Timing .218575 is below .750; calendar reversals
.7042097487189467/.2518367026423454/.20954925517923106 are below 2.
Early starts remain 44/23, and C01 has physical and actual trade presence
in all 12 measured months at both 3/10 rates; paid rent per door
.02615797583/.02697192308 rises with the higher rate. All founding/dial,
currency, calendar, crew, base-before-window, saving-plan and 136
fixed-basket/fee-quantity guards pass unchanged. No extra pair was run.

Report 7049 keeps the complete source-labelled handoff, catalogue and
maps at the helper's target/order-proof/native198 and
~/cbs/runs/res3-order-proof-1980899a9-20261124. Evidence archive SHA256
is ef46344ec01b4813c1acfab743079696a393f0761f49d706222317b053bf45cc.
Native source is clean, all owned jobs ended, and source/data/JDK hashes
are verified. Earlier compile stops remain unrun historical checkpoints.
The housing core is green here; CPI coverage/weight correction and final
money/save verification remain open before mends.

The CPI source audit (269e/fd51) establishes an end-of-step sample after
Markets seller sales/endOfMonth and before closeMonth; the start-of-step
strike is not repeated there. Existing FOOD and RENT weights read struck
M-1 seller statements, while meals/luxury and billed fees use current
flows. The initially suggested subtraction of the latest fuel bill from
struck retail sales is therefore wrong-period. Retail's current pending
ledger and current rent bill are already saved/restored; no payment or
strike change is needed.

★ Observe completed current acquisitions at that existing CPI phase:
read only current actual food sales, the actual charged shelf observation
and existing getRentIncomeBilled, retain current meal/luxury and existing
billed-service convention, and add actual current car/fuel purchases.
Narrow scalar getters may read the already saved pending ledger; no
mutable ledger is exposed. This corrects observation periods, rather than
moving transactions, settlement, recordMonth, cadence or policy windows.
Normal adjacent-month varying-fuel cases must prove period reconciliation.

The audit also finds home-fuel sales in the old retail household total.
Selecting only GROCERIES would omit that existing consumption unless it
is explicitly covered. Do not drop it or leave it priced as food. The
source builder must identify its exact Good, current quantity/bill/price,
saved state and any rental-provided-energy counterpart before choosing
its separate comparable component. No home-fuel coefficient, new UI,
annual relink or actual transaction change is authorized by that finding.

Home-fuel source report a99b identifies two homogeneous goods, rather
than a single interchangeable energy unit: household JET/lamp oil in
litres and THERMAL_COAL in tonnes. Retail records each actual household
trade separately at its own VAT-inclusive price, without petrol duty or
queue pricing. HomeFuel.heldSale already saves homes/litres/tonnes and
bill/cost/imported money, and the per-Good pending ledger is saved too.
These are separate household fuel bills, not rental-provided energy.

★ Append LAMP_OIL and HOME_COAL after NEW_CARS/PETROL, leaving the old
five positions unchanged (nine components total). Include each current
household bill once at its comparable paid unit price; no heterogeneous
fuel-unit average or unnecessary new home-fuel flow state. Use actual
household quantities, or prove the corresponding Retail good has only
household buyers, before dividing household value by units. Fixtures must
reconcile the two bills/quantities with the saved HomeFuel sale and exclude
industrial JET/coal from the consumer basket. Current food is only the
actual GROCERIES line. Check the CPI service-fee sub-basket explicitly so
home fuel is not counted there as well. These observation/coverage changes
move no actual transaction, strike or settlement, and keep the existing
120-month cadence, explicit parity rent cap and every policy law.

FS5 ships on main as 0.9.200 at b17b75d42. RES3 merges it before CPI
integration; the only conflict is generated docs/map/Household.md, kept
from main for final regeneration. No CPI, Game, DataSave, Motoring,
HistorySave, Sector or Retail source is changed by this main delta;
AllChecks/Observation/Timing and Kept/UI sweep changes are retained.
Shipped format remains 65, so RES3's owed 66/floor66 and genuine format65
pair stay valid. The final pushed merge is ff123698ad9ef1072c3774b67d5bebc2202f20e2;
its immediate predecessor merge was replaced only to remove Git's conflict
footer and restore the required author line at the end of the message,
using a lease against that exact own branch tip. No source changed.
The CPI builder backs up its own work and rebases onto the final merge;
all final checks must use the integrated source.

The home-energy nonduplication audit is explicit: CPI fee spending is only
healthcare, tuition, fares and account fees; it contains no HomeFuel line.
Current rental receipts are the actual lease bills; HomeFuel and recurring
affordability costs are not added to them. The two current home-fuel trade
bills sum to the separate saved HomeFuel bill, charged to household
accounts on its existing subsequent schedule. The index observes current
acquisition, moving no such payment. Home quantities come from saved HH
heldSale litres/tonnes, not an unchecked all-buyer units denominator.
Retail declares itself only a GROCERIES maker, so ordinary markets cannot
sell its JET/coal as industrial production; its direct JET/coal sales are
the household home-fuel path. The new fixture asserts these independent
facts and reconciles pending quantities, bill and eventual payment.

The final CPI source handoff is reviewed and adopted on FS5 main's merged
ff123698a. The old five component ids stay fixed; new cars, petrol, lamp
oil and home coal append at 5-8. Current GROCERIES acquisitions use the
actual charged price; current lease bills replace the struck prior-month
weight input. Meals, luxury and the existing billed-service convention
stay. Home oil and coal keep separate physical units and actual bills.
The unsupported luxury truncation is removed; the explicit XR1 parity
rent cap and 120-month link cadence stay. No payment, funding, lease,
strike, service billing or UI code is changed by this CPI correction.

NewTrade saves actual physical new cars, local seller spending, imports,
credit and the last paid unit price. PetrolPrices saves previous actual
pump/queue litres, tax-inclusive channel prices and the comparable price
level. DataSave, load, refounding and redenomination carry both records;
physical quantities never scale. Existing saved pending ledgers and
HomeFuel heldSale supply the other acquisition inputs. The already owed,
unshipped format/floor66 covers this change over shipped65; the genuine
shipped65 refusal fixture remains unchanged.

The peer's model patch SHA256 is
9bcd7b79744cc1a34bb0190d921fc4a775edc49085a91e7af9b354fb88c9b924;
its findings patch is
e6d75c1c34f94b8026932b2e135d0b27249e9413b93eaab358dadf3ee666f4d2.
Review checks current-period food/rent/fuel reconciliation, full financed
car value, pump/queue mix neutrality, separate home units/nonduplication,
first/no-trade prices, fixed-basket continuity and native fresh/running
reload/next-month twins. Historical UI cap text is unreachable through
isLuxuryCapped=false, with no screen edits. These are source findings,
not native passes. Compile, the unchanged monetary policy laws, the
old-production finding and the new coverage/save cases remain to run on
the pushed candidate, box/shared lock/one owned JVM at a time.

The static pick rejects an unregistered *Check class even when invoked
by MonetaryCheck. Rename the nested coverage class/file to
CpiCoverageFinding and its single caller; normal Monetary still runs
all its findings, without adding a duplicate full execution to AllChecks.
No assertion or model behavior changes. Candidate 40806e6f8 is superseded
for native verification by the following pushed rename commit.

Exact candidate 0579c76b6 and old ff123698a both compile on the box with
verified source/JDK/assets. The unchanged old-API CPI finding records
20 passes and 13 failures: twelve intended omitted/mispriced-expenditure
failures, plus an erroneous new physical-stock expectation. Its setup
clears cars only in nonempty purchase-eligible cells; tiny retained cells
still own cars, included in totalCars and normal fleet wear. Closing cars
12.081211790314319 therefore cannot equal only the twelve new draws.
Keep those holdings: record the actual opening total and assert closing
equals opening minus normal CAR_LIFE_MONTHS wear plus actual draws.
No fixture input, model, tolerance, conservation gate or intended CPI
failure is changed. Patch SHA256
6d2331ab641a128231e25853f6196b4bc611610a5cad16a26e0e840671721dbe.
The sequence stopped before any candidate findings or named harnesses;
all owned jobs ended. Evidence archive SHA256
8c483082d5a8caceb4ba11fd073af154109f2fe3905e3b36ed195e0662d812bc,
helper target/cpi-native-0579/evidence and box
~/cbs/runs/res3-cpi-0579c76b6-20261124. These compile/old-finding results
are not new-model or monetary passes. The corrected finding must show
only its intended old failures, then pass with the new source.

The corrected old-API finding is run unchanged against clean ff123698a
production: driver compile 0, finding exit 1, with exactly the twelve
intended CPI failures and no failed fixture/physical/cash/credit guard.
The failure-set verifier passes (helper report 367f). Exact pushed
d2114bf2d, including only the reviewed stock-identity correction, is now
the candidate for new-source verification. Candidate results remain open.

Exact d2114bf2d compiles and its actual-purchase CPI reference finding
passes. Coverage records 157 passes and 22 native identity failures:
history equality in nominal/historical twins, redenominated data equality
and redenominated next-month exact guards/current inputs. All four
three-city money-audit closure assertions pass; that does not waive the
exact save/history/input comparisons. No normal named harness has run.
Snapshots were removed with scratch teardown, so the field-level cause
is not yet established. History has no savedAt field: these differences
must not be dismissed as timestamp metadata or normalized speculatively.

Adopt the reviewed read-only evidence patch, SHA256
ee79225fb14b3da8f4174f2fed6cc1f19e650b4dee9ab3ff3e36a4cf97c1a912,
which copies each unmodified native data/history read to a numbered
target/cpi-native-snapshots/<pid> artifact before existing comparisons.
No model, assertion, input, tolerance, observation order or return changes.
Replay only the standalone coverage finding on the pushed evidence
candidate, then let the helper diagnose the exact fields. The already
verified old-production proof and reference finding need no repetition.
Source/JDK/data manifests and raw d211 outputs are retained in helper
target/cpi-native-d211/evidence and box
~/cbs/runs/res3-cpi-d2114bf2d-20261124; evidence archive SHA256
c73f748a19bfa0599918e5927624045a63c790228ff7c75897696749f59daf4a.
All owned d211 jobs ended before the report. No monetary pass is claimed.

The exact 0e76d1ff9 evidence replay compiles and reproduces 157/22, then
ends all owned jobs. Forty-four snapshots are retained (archive SHA256
26c3cacfebab5783da2a427d4a6b3937a92209e5147be002843a419a53fcad73).
Every compared history has identical keys, numeric lexemes and values;
only debtSurveys map iteration order differs (194 map orders in the
fresh comparison). DebtStatistics Snapshot/Survey use Map.copyOf, which
does not retain recorded input order. The first redenominated data load
has only propertyTaxCharged different: default .029264137158463127 to
.02926413715846312, historical .006653111851018367 to
.006653111851018366. The loader restores the saved aggregate, then
restoreSectorBills overwrites it by summing individually scaled bills.
Both source paths predate the CPI correction; neither exact comparison
is weakened to pass them.

Review and adopt patch SHA256
dfc8b60606d0e77cbfcdf6565952b873eedea6cdb7cd3932f08175193a54a9be:
DebtStatistics takes an ordered private defensive map copy, rejects null
maps/keys/values and exposes it unmodifiable. The load path restores the
authoritative saved charged-property-tax total after component bills;
the setter only restores a held value, charging or settling nothing.
No comparator, tolerance, input or policy law changes. This preserves
recorded data and the saved aggregate instead of recomputing a flow.
It does not yet establish the cause of all 7,162/4,341 later field
differences: unchanged fresh/running/next-month native guards must verify
the candidate. No further format bump is owed beyond unshipped66; shipped65
is still refused without conversion. Detailed field evidence is at helper
target/cpi-native-0e76/evidence (archive SHA256
57b1c80eeadf32a40042f25c4672240ecc47e048907174e9853fc9bec10c3493)
and box ~/cbs/runs/res3-cpi-0e76d1ff9-20261124.

Exact 9b6f066e4 compiles and coverage improves to 171 passes/eight
failures (helper fc78). Every initial native data/history equality and
ordinary nominal/historical next-month twin now passes under the same
assertions, verifying the two reviewed fixes. Eight failures remain in
the default/historical reformed next-month guards, current inputs and
data comparisons. All four money audits still pass; exact identity is
not waived. No DebtStatisticsCheck, MonetaryCheck or other normal check
has begun. All own JVM/runner jobs ended, snapshots are retained, and
the helper traces derived/cached pre-step state against the now-identical
serialized reload state. No further cause or fix is claimed yet.

The remaining trace rules out patching the housing offers cache: that
cache is rebuilt before clearing. No further production cause is proved.
Adopt one bounded read-only cached-input observation in the finding,
patch SHA256
a441881d6d1500d135a423b84ad9a857eb60064cec32271557a7dd0d20164e0c.
Review confirms existing pure reads of economy/population wage vectors,
utility tariffs, builder material/FX/expected-level inputs, expectations,
CPI relatives/parity and three existing templates' cash/upkeep/jobs and
power/water quantities. It calls no quote, forecast, refresh or reprice.
Record original/fresh/running at the same phase and immediately before
the step, and add before/after exact money/claim/stock/index guards to
prove that observation is pure. Every existing failing assertion, input,
threshold and execution order remains; no speculative normalization or
production fix is made. Compile and replay only the standalone finding
on the pushed observation candidate, then diagnose those recorded inputs.

The 9b6f artifacts are at helper target/cpi-native-9b6f/evidence and box
~/cbs/runs/res3-cpi-9b6f066e4-20261124. Raw snapshot archive SHA256
b83f0dba6f7cf38242e6aa777f3f231c46584e66302e9bd6b6c39553c52bd197;
full evidence SHA256
415f9fae8d19b2525f1a6438d743a20797129697cdf24b9c44bdf154fa2f140e.
All owned jobs ended; every normal harness stage remains unrun.

The exact 65d92f4e6 replay compiles, with 219 passes/eight unchanged
failures. All 48 added observation-purity guards pass. The bounded
pre-step records establish a concrete reload defect: after reform the
live base water tariff is 1.2941731921489209e-6, while both fresh and
running loads use .001294173192148921 (one thousand times higher).
The sea-power marginal term is zero in all six reformed records.
UtilitiesHandler.redenominate scales the held base tariff, but PowerState
omits it; the factory default survives load and is pushed to the economy
and sectors. UtilitiesHandler is byte-identical to pre-CPI ff123698a.
Smaller wage/template/index ulps are also observed; their causal effect
and the remaining exact guards are not assumed fixed by this discovery.

Review and adopt patch SHA256
d0bfb4b88ef57be30e7986c153dfeaaf5c374ae53f4342e3fac7d918abcb5fce:
PowerState.waterTariff saves the held base tariff in local money per
water unit, excluding the separately struck power charge. toPowerState
captures it and restorePowerState restores it before rebuilding inputs.
Existing DataSave.powerBooks and load wiring carry the state; the existing
redenomination scales the live field once. No price is recomputed and no
transfer, rate, fixture, tolerance or comparison changes. The old-saves
rule applies: missing this state would load a reformed city wrongly, so
the already owed unshipped66/floor66 remains required over shipped65;
there is no further unshipped bump or conversion. Verify exact twins and
the affected water/power checks on the next pushed candidate.

Helper target/cpi-native-65d9 retains the 44 raw saves/histories and 24
pre-step records, differences and source evidence. Raw archive SHA256
dfa0558c84857f530fbb3ae92f12824dd1074746aca3d0f2d1b6a9017b1c53c7;
full box evidence SHA256
37315e3fa2b0701191585e531399e7cb511d5ed1b3d4db48ecc76aba752482bc.
All owned jobs ended. Normal monetary and other harnesses remain unrun.

Exact ca9bb9339 compiles and coverage reaches 222 passes/five failures
(phase report 8869). Both reformed acquisition-input comparisons now
pass, and the default reformed exact money/transport/stock guard passes.
The remaining five are default fresh/running next-month native equality
(two) and historical reformed exact guards plus fresh/running next-month
native equality (three). This verifies the tariff correction without
waiving the remaining exact comparisons. Runner/JVM ended, raw snapshots
are retained and the helper compares the remaining fields; no further
native or normal check has begun, and no additional cause is claimed.

The bounded ca9bb9339 field comparison (c4dd) narrows the remaining
post-reform differences to 27 numeric fields in the default city and
359 in the historical city, with no object-order differences. Source
mechanisms: Sector.redenominate scales its held wage-times-posts cache,
but load updateWages recomputes from scaled inputs before the next
top-of-month statement (Automotive payroll .014621568294559533 versus
.014621568294559531). Population's held totalWagePerType is likewise
scaled then recomputed on load (historical .008175987051984966 versus
.008175987051984964). PriceIndex intentionally preserves relative ratios
through reform, but restore recomputes scaled last/base (historical
parity 1.0187766445680204 versus 1.0187766445680202).

Authorize source preparation of the minimal authoritative held-value
save/load fix, with exact field/phase/unit/consumer evidence first.
Restore after rebuilds that overwrite the held state; nominal amounts
scale once, dimensionless CPI relatives never scale. This preserves
the actual prior flow and the preserved index ratios rather than
normalizing comparisons or changing prices/payroll/transactions.
No unrelated derived cache or new conversion path is authorized;
existing legacy-array guards remain. The already owed unshipped66
boundary covers any such saved state. The actual diff and native
proof remain to review before adoption; no further native run is
authorized until a reviewed patch is pushed with its exact SHA.

The historical labour cause is reproduced by source arithmetic against
the retained native observations (7868): goingRate .0213575 multiplied
by .001 yields the live .000021357500000000003, while load's division
by 1000 yields .0000213575. With unchanged ratio, cost of living,
combined multiple and ADJUST_RATE=.12, these reproduce the exact next
UNIV_FINANCE wages .000468626802667078 and .00046862680266707797.
This establishes an additional held nominal anchor, rather than a
reason to change the wage equation or a comparison tolerance.

Review and adopt the seven-file +44/-7 held-money restore patch,
SHA256 d8267db037315576626a10a948f3566dbf0e1b7adc27b8ad9f42a17656021dad.
SectorState.payrollPerType snapshots eleven full-staffing local-money
payroll totals with a clone, then copies them back through restoreBills
after the rebuild. Sector.strike reads them before the next wage refresh.
DataSave.populationWageTotals snapshots eleven held local-money totals
with cloned setter/getter arrays; PopulationManager copies them back late
in load, before followAverageWage can read the rebuilt alternative.
LabourMarket appends goingRate/minSettable/maxSettable and restores them
after seeding and before its minimum-wage clamp. Existing redenomination
already scales these nominal values once. PriceIndex appends its nine
dimensionless relatives, restored exactly and never scaled; these feed
parity. Existing earlier array shapes and fallback guards remain.

The actual diff preserves all prior offsets, correctly appends the
labour tail, copies into private held arrays and restores after the
overwriting rebuild. No separately derived staffed-wage or template
cache is saved. No new payroll/settlement/transaction, wage or index
equation, adjustment rate, bounds constant or comparison changes.
Every finding/assertion remains byte-identical. Existing unshipped
format/floor66 covers this held state over shipped65; no extra bump
or conversion. Native exact guards must establish the result before
the normal named checks proceed.

Final ca9b source/JDK/assets and artifacts are retained at helper
target/cpi-native-ca9b. Raw snapshot SHA256
be4b097b79577e34c3f458c90e2be46254093b39173b32106297c6737142077b;
full native evidence SHA256
e5b0221fad63794d8593bf55f47e868ebc10020f59ca99497cb43a56c2841d21.
All owned jobs ended. No current normal monetary pass is claimed.

Exact 18c8dd4b9 compiles; coverage records 223 passes/four failures
(8c23), including all 48 observation-purity guards passing. Both
default and historical reformed exact money/claims/transport/stock
guards and current-acquisition inputs now pass. Four fresh/running
next-month native data/history conjunctions remain for the two
reformed fixtures. Their field-level cause is still being compared;
no exact comparison is waived. Runner/JVM ended and snapshots are
retained; no normal check or additional native run has begun.

The bounded 18c8dd4b9 comparison identifies the remaining DATA fields
(6fb7/df50): default has eighteen differences, fifteen bank history/
opening-equity fields plus three abroadOwedById shares (ids 8/65/69);
historical has eleven bank fields only. All other DATA fields/order,
initial/native ordinary twins and exact monetary/claim/stock/acquisition
guards pass. Bank.redenominate scales its held books/loan vectors, while
load refreshBank recomputes from scaled claims; the next startMonth files
the held books and opening equity before refreshing them. Observed
business loans are 8.747001236790421 versus 8.747001236790425, mortgages
4.248843591525243 versus 4.248843591525244. This is not evidence of a
different transaction, and no monetary assertion is relaxed.

FC2's effective per-id physical points are foreignBook times rawClaim
over sumRaw; restore currently uses those effective points as raw weights.
New orders then add/reweight in the changed scale. The actual prior raw
weights were not retained, so exact numerical reproduction of those three
share differences is not claimed. Authorize source preparation of a
minimal held Bank-book snapshot and raw-weight persistence, retaining
existing effective points and funding/getter equations. Require actual
units and every addition/scaler/load-order consumer to be proved before
adoption; cancellation in a ratio alone does not establish dimensionless
units. No new loan, cash, equity, settlement, gate or normalization is
authorized. Any production diff still needs review/push/exact native GO.

Adopt the separately reviewed finding-only patch SHA256
4964b80b95d4557f81d69a43f5b46669e6b4c6217e3274b0afead4ca574e02c6:
read fresh/running next-month DATA and HISTORY into locals before each
existing dataEqual-and-historyEqual assertion. Its predicates, saves,
steps and model state stay unchanged; this retains history evidence
even when DATA fails. No native rerun is authorized for this patch alone.
Raw 18c8 snapshot SHA256
c41433f85d13e92dbf07657ac059e6078e52d1748d444ae3ad4f4e04dc9dc9ce;
full evidence SHA256
5e7c954e489dc8da450a3e993638fb8e48fd0b05981e4d7cba9e77cac574cafd,
at helper target/cpi-native-18c8. All owned jobs ended; normal checks
remain unrun, and the source-only bank/claim proposal remains open.

The source/unit plan (7554) corrects the raw claim classification: these
are physical CLAIM-POINT counts at a carried common scale, not
dimensionless or currency amounts. noteAbroad initializes with physical
work points and adds existingClaims times abroadWorkPoints over prior
foreignBookWorkPoints. Both caller paths and handAbroad pass physical
points, while nominal prices/bills remain separate. Currency reform
scales neither the raw claims nor the physical foreign book.

Review and adopt model-only patch SHA256
d80ad5bb930eec4424570cb6cb2cacacc051fd68635166a32a282a0c3c2e51f9,
five files +77/-1, unchanged from delivery. Bank.HeldBooks snapshots
seven held local-money amounts (deposits, sector/city/household/mortgage/
interim/bond books) and the two 25-sector loan vectors. Its constructor
and accessors clone the vectors, and restore copies them into bank-owned
arrays. These values already scale once in live redenomination; no
independent snapshot scaling, deposit component duplication, new cash,
claims, equity adjustment, risk/rate or transaction is introduced.
Restore follows the last refreshBank and restoreDeposits, before the
cost-of-funds reader and next startMonth sheet. It restores recorded
held amounts directly rather than re-summing scaled components.

BuildingManager returns a fresh raw-claim map only for live owed sites,
matching the existing liveClaims denominator. DataSave copies the map
in both directions; late load restores through the existing finite
setter after site rebuild. Existing effective-point field/getter,
projection, book, additions, cancellation and funding formulas remain
unchanged. Physical claim points never scale in a currency reform.
The four exact native comparisons determine the actual result; the
unretained earlier raw values do not support a retrospective numerical
reproduction claim. The already owed unshipped66/floor66 covers these
held fields over shipped65, with no further bump or new conversion.

Actual-diff review checks the existing live-site filter, raw additions,
finite copy-back, defensive records, Sectors.KEYS vector order and late
restore placement. No finding or monetary law changes in this model
patch; the already pushed unconditional history capture is retained.
Native verification remains to run on the next pushed candidate.

Exact 9d2d98699 CPI coverage is green (27dc): verified source, pinned
assets and Temurin21.0.13+9; compile/classpath/finding exit 0, 227 passes
and zero failures. All 48 observation-purity checks and every original
nominal/historical/reformed fresh/running/next-month data and HISTORY
comparison pass, including exact money/claims/stocks and acquisition
inputs. This verifies the held bank books and raw physical claim scale
with the same caused fixtures and predicates. No assertion was relaxed.
The coverage JVM ended and raw snapshots are retained; the authorized
normal named checks now proceed sequentially on that exact source.
Their results are still pending and are not inferred from this finding.

On exact 9d2d98699 the separate normal DebtStatisticsCheck, WaterCheck,
WaterPowerCheck and SubstationCheck each return batch exit 0 (one selected
harness passed), with zero firm Stale findings and unchanged source
guards (940d/1c2f). DebtStatisticsCheck's administrative wrapper initially
returns 1 only because Maps creates the two new untracked CPI map pages;
they are archived and explicitly cleaned from the owned clone. Actual
batch exit is 0; no test or source is rerun/changed to repair that wrapper.
The full normal MonetaryCheck, retaining all prior laws plus the nested
coverage finding, is next. Its policy result remains pending.

The full normal 9d2d98699 MonetaryCheck executes every policy case and
the nested 227/0 coverage, then returns Java 1/batch 4 for one failed
legacy-array assertion (22a9/d284). All policy laws pass; no rate effect,
ordering, supply, timing, window or bound is changed. The fixture's
whole.length-1 used to construct the layout before parity, but now
removes one of the nine newly appended relatives, yielding an invalid
partial current array. A second fixture's whole.length-COMPONENTS-1
now produces a with-levels layout instead of its claimed before-levels
layout; that assertion happens to pass and is false historical coverage.
This batch caused both fixture construction errors and must repair them.

Keep the production saved state/parser and every old predicate/tolerance.
Construct the actual layouts from the existing format span:
B=SLOTS_BEFORE_CHAIN+CHAIN_TAIL_0743 (before component levels), L=B+
COMPONENTS (before parity), P=L+1, current=P+COMPONENTS. Make the existing
format-span constant package-visible so the fixtures use the model's
own layout; no value or dial changes. from744 now uses B and from745 L.
Add a negative guard that the one-slot-truncated current array is not
accepted as a historical chain. This causes each claimed format and
protects rejection of the invalid current truncation, with no new save
conversion or classifier relaxation. Run the full normal MonetaryCheck
again on the pushed correction, then continue the named sequence.
Its runner/JVM ended and the exact previous source/results are retained;
ExchangeRate and later names have not run. Prior four normal passes and
the native coverage remain recorded, not replaced by this fixture fix.

The completed 9d2d policy figures (76fe) are pooled total effect
3.635685 percentage points and non-shelter effect 3.102148 points; the
unchanged +1 total-effect law and every other policy guard pass. Full
Monetary output is 720 passes/one legacy-fixture failure, including
227/0 nested CPI coverage. The corrected full-run verdict remains to
come. Exact supported array lengths are B223/L232/P233/current242;
the old fixture erroneously used 241 for pre-parity and 232 for
before-levels. Evidence at helper target/cpi-native-9d2d has native
archive SHA256
837877a1663d5eecc80588d4a63e30cb088eb56516ed88607493d2a0f5bbced8,
raw snapshots SHA256
14729d23a72240fe757b86cec1c2a3fd194511b11ec2ba0402ff05096198420f.
These are internal supported layout-shape fixtures; the genuine shipped
save-format refusal pairs remain separate and unchanged.

While the controller waits for the required shared box lock (no Java),
the adjacent static state-layout audit finds the same tail-relative
fixture error in LabourCheck (dfc7). Its old length-3 now removes the
three anchors rather than tie/share/average, its invalid tie write now
changes goingRate, and length-1 removes maxSettable rather than the
average. lastAverageOf likewise now reads maxSettable. These conflicts
are caused by this batch's appended state and must be corrected.

Add two pure package-visible format-position accessors, tieStateOffset
and averageStateOffset, without changing any existing production body,
state layout, reader, equation or constant. LabourCheck now constructs
the actual pre-tie/pre-average arrays, corrupts the actual tie ordinal
and reads the actual saved average through those positions. Every old
assertion/predicate/tolerance remains, with the same .61/.77 inputs and
expected fallback. No Labour native pass is claimed from this static
correction. Run normal LabourCheck on the pushed fix, and retain the
other exact-source validation labels; the pure added accessors change
no existing simulation behavior.

The box lock is held exclusively by another ensemble (metadata only,
PID1884071 also holding ensemble.lock); no other logs are read and its
job is untouched. Our 63001d9a4 controller had only a waiting flock and
no clone/compile/Java stage. Terminate only that owned waiting child;
its controller/SSH ends 143, with no harness executed. Exact7ba16cbf0
is then queued under the mandatory shared lock (controller1944040),
compile first followed by full MonetaryCheck, LabourCheck and remaining
names. Static audit confirms the four labour tail consumers are confined
to LabourCheck, with no other end-index or external lastAverageOf user
(9bb1/2504). Waiting for the shared lock is not a laptop-memory timeout,
a harness red or permission to interfere with the ensemble.

At 21:39 the helper confirms a sustained lock impediment: our shared
waiter has waited 12m08s, the exclusive ensemble holder has run 26m39s,
and another ensemble writer is queued. No owned JVM has started. Send
orchestrator a scheduling-only block report (29e2), keeping the existing
authorized waiter queued and all other jobs untouched. This does not
change model state, permission, timing laws or native pass labels.

Orchestrator 1528 at 21:43 authorizes remaining native checks on this
laptop now that eleven GB are available, one harness at a time with
MG1 re-read before each, and orders our box waiter dropped. R6e-rest's
ensemble/dry keep the box; no lock is bypassed. Our waiting flock and
controller end 143 before any Java/compile; both PIDs are confirmed
absent, with zero owned remote jobs (f8a1).

Read MG1 and its actual AllChecks dials: 2 GiB heap plus .5 GiB non-heap,
at least 4 GiB available RAM and CPU below 80 percent over three two-second
samples. The non-JVM preflight has 10.970 GiB RAM, 13.917 GiB free commit,
18.423 percent CPU and zero Java processes. Fresh readings and the MG1
read are required before each local named launch. Prepare a clean
detached 7ba16cbf0 source, verify all source/asset hashes, and pin the
existing Oracle21.0.4+8-LTS-274 at C:/Program Files/Java/jdk-21 without
changing the machine. This differs from box Temurin21.0.13+9 and the
validation records must say which host/JDK ran each result. Assets use
the required absolute C:/ main-data paths and parallel=1. Compile first,
then canonical one-name batch-check calls with one heavy harness at a
time and no other compile/check overlap. No local native verdict is
claimed from preflight alone; full Monetary and Labour remain pending.

The orchestrator also advises R6e-rest will likely ship format66 first:
keep our current66 until merging its published main, then take67 and
make a genuine previous-format66 save/history pair from that shipped
source. Never pre-stamp a current pair or bump from an unshipped value.

Full canonical local MonetaryCheck is green on exact7ba16cbf0 (4fce):
batch/native exit 0, 722 passes/zero failures. All 494 prior guards,
the new invalid-current-truncation guard, corrected historical layout
fixtures and nested 227/0 CPI coverage pass; all 48 purity checks remain
green. The original +1 total-effect floor passes with pooled paths16-31
effect 3.635685 points and non-shelter effect 3.102148 points, matching
the earlier box figures. This result runs under pinned Oracle21.0.4 on
the laptop, with pinned source/assets and fresh MG1 before launch;
zero firm Stale findings and clean post-source guard/tree. Its runner
ends before LabourCheck starts. Logs and Monetary-verdict.json remain
at helper target/cpi-local-7ba1. Later named verdicts are still pending.

Local canonical LabourCheck also passes on exact7ba16cbf0 (3232),
batch/native 0, zero firm Stale and clean source guard. This verifies
the true pre-tie, invalid tie, pre-average and held-average helper
corrections under unchanged assertions. Its runner ends before fresh
MG1 passes for ExchangeRateCheck (10.587 GiB RAM, 13.985 GiB free
commit, 11.661 percent sampled CPU, zero Java before start).

Local canonical ExchangeRateCheck passes exact7ba16cbf0 (c29b), native/
batch 0, zero firm Stale, clean source guard and ended runner. Fresh
MG1 before SaveFile has 8.697 GiB RAM, 11.734 GiB free commit and
13.760 percent sampled CPU. Two external GUI/launcher Java processes
are identified by sanitized metadata and left untouched; only our one
selected heavy harness runs.

SaveFileCheck then returns native1/batch4 for one assertion (b1da/7606).
Its list already includes all nine component indices and nine weights,
expectedLevel and twelve fixed series: 31 entries. Its initialization
still requires size23, the old five-component dimension, so the aggregate
must fail regardless of the data. Correct only that dimension to
13+2*PriceIndex.COMPONENTS and the obsolete descriptive label/comment.
Every per-series nonempty, exact-array-equality and non-NaN predicate
stays byte-identical, as do all other save/refusal/running-city guards.
This batch caused the stale coverage count; no production restore fix,
new tolerance or pass-by-name row is used. Do not infer all 31 data
predicates passed until the corrected full normal native rerun.
No ReadPath/Denomination/HousingMarket/Invest stage has begun; the
SaveFile runner ends before preparing the pushed correction.

R6e-rest ships 0.9.201/format66 at 78ed67315 (orchestrator7e20), so merge
that main before continuing. The conflicts are GameVersion's boundary
comment and generated ResearchCheck map. Keep main's version/floor66
in the merge, take its generated map for final regeneration, and retain
both ResearchCheck additions and all engineering/recipe/licence changes.
Merge commit73e631cef ends with the required author line; no publisher
version or record is edited by hand. The next owned change takes
SAVE_FORMAT and OLDEST_LOADABLE_FORMAT from shipped66 to67: missing
typed/consumer/held monetary state would load wrongly, so no conversion.

ID audit confirms the catalogue remains contiguous: RES3's reserved
156-166 and main's power167-171, nextId172; PW5 Substation155 remains.
The prior b17 main already has nextId172 and the power rows, so this
merge introduces no ID move and HousingForms' hard-coded IDs stay valid.
The final report/maps must be regenerated on the integrated model.
Newest ensemble baseline is now docs/ensemble/0.9.201.md.

Add an external HousingFormat66Fixture producer which reflects the
runtime build/version/floor and refuses anything except the unedited
shipped 0.9.201/66 classes. It uses LongPlaytest.probeCity with scratch
files, twelve real months and slot10, then copies both native files.
The helper must compile/run it against clean shipped78ed classes, with
fresh MG1 and no other owned JVM, and deliver raw hashes/schema/history
evidence plus deterministic gzip resources. Current67 native checks
remain held until the real66 pair and refusal assertions are installed.
No current-schema re-stamp or fabricated digest is used.

The completed pre-merge00946 local SaveFileCheck is green (308d):
native/batch0, 1,274 passes/zero failures, including every actual31-series
nonempty/exact/non-NaN predicate, genuine older pairs and saved-money
guards. All 2,524 non-SaveFile class hashes match7ba; no production change
was used to fix the count. ReadPathCheck also returns native/batch0
(6e3f). Stop the old sequence after that active stage; no Denomination,
HousingMarket or Invest is launched on the old model. All own heavy/
controller jobs end before the separate shipped-main producer begins.
Archive target/cpi-local-0094/evidence-local-0094-complete.tar.gz retains
those source-labelled results and its evidence.sha256. They are valid
pre-R6e-rest/format66 results, not current format67 passes. The merged
model and real new refusal boundary remain to verify.

The real shipped66 producer completes all four stages at exit0 (debc),
using clean fresh 78ed673151e487359b44cbac44b6994b16ec6182 and the unchanged
external producer from0cce237e2 (SHA256
d91af226d799166726e1383fcb753a422bf05b96ec9cca52224b5a3a8bce90db).
All 683 source/resource/pom/script blobs match shipped Git, 2,486 classes
are freshly compiled, and same-run class-origin records place 625 model
classes under that shipped target/classes. No RES3 model classes are
reused. Oracle21.0.4, pinned assets and fresh MG1 pass each native step.

The native pair is .201/66, month31 after the shipped month19 probe and
twelve additional months, seed4127; history has 30 rows, months2-31,
and two debt surveys. Its legacy index has169 slots and its141 household
keys have20307 values. Parent independently verifies both raw and gzip
hashes, timestamp-zero gzip headers, grid shape, native version/format,
card-cell width and absence of every RES3-only market/car/wage/book/raw-
claim field. Copy the exact pair and provenance README to
src/main/resources/fixtures/res3-format66. No JSON byte is retagged.

Raw save SHA256
43f82bba8e92d69fcb0e26625cd23e494af880643adc4201b204dc1374b35166;
raw history9b7a70fdb88856a31c3b1e7926f51bf127590170ac279ae0d5700162ee033dd8.
gzip save1ddaefe71c53dad0500bf5497e00b725bde9c5788f67833eeed61718c295ccc4;
gzip history6535fdcb892dc6f73b465210678b71911c461194f773fc594f8ad6ddcb68b6a9.
Producer evidence07f0b27f158fd00e72c3a3128a00d9d8d6f712e9dd0e3dce1b4c6a52d45762cd,
prepared resources83b1960ba42654c576052edcdbf72334a36891d486ef4b338ff161aa3c8691cf,
at helper target/cpi-format66-producer-78ed. All owned local/remote jobs end.

Extend SaveFileCheck's existing exact-hash native refusal chain with66,
point the one-below-floor boundary to this genuine pair, and update both
SaveFile/ReadPath descriptive rows to67. Retain every old62-65 case,
HC1/PW5 native pair, file/history comparison and running-manager/city
identity guard. Strengthen provenance with absence of the new car,
population-wage, held-bank and raw-claim fields. Current integrated67
native checks remain pending; producer success is not a refusal pass.

The fresh RES3-integrated67 sub-session compiles pushed f0b960367 on
the laptop with pinned Oracle21.0.4, Maven3.9.16, the same absolute
Earth/world files and fresh MG1 before each native start (fa40/3dcf).
Its first canonical current-model SaveFileCheck is green: native/batch0,
1,274 labelled checks, 104.2 seconds (8247). Genuine shipped62-66
refusals preserve both files and the already running city; the HC1/PW5
pairs remain covered. The two format66 gzip hashes match the committed
resources exactly. This is the first integrated67 save verdict; earlier
7ba/00946 results remain explicitly pre-merge. Generated maps are kept
for the final transfer rather than discarded to make the tree clean.

Integrated67 ReadPathCheck also passes, canonical/native0 with 92 labelled
checks in 115.9 seconds (4ac9), including repeated-read and native older-pair
guards. Monetary starts only after that runner ends and a new MG1 reading
passes (7.558 GiB available RAM, 10.318 GiB free commit, 19.807 percent
sampled CPU). The static pick is ALL253, with the combined Bank/DebtStatistics
money and DataSave/HistorySave/format/floor review required. The provisional
ship file parses, but that parser does not validate its old red attributions;
the remaining named checks must settle those on the current integrated tree.

The integrated67 canonical MonetaryCheck passes on f0b960367: native/batch0,
722 labelled checks, 453.3 seconds under pinned Oracle21.0.4 (8745). The
original-town highest-rate effect and all fixed16-31 town guards pass; pooled
TOTAL low-minus-high inflation is 3.635685 percentage points and non-shelter
is 3.102148 points. The total-effect floor remains at least one point, with
all rates, towns, windows and supply assertions retained. Nested CPI coverage
and its purity guards pass; diagnostic raw snapshots are retained under the
helper's target/cpi-native-snapshots. This is the current R6e-rest-integrated
format67 result, matching the prior separately labelled policy figures.

Integrated67 LabourCheck passes native/batch0 with 248 labelled checks in
43.1 seconds (8055). ExchangeRateCheck has completed its native assertions
when that report arrives, but its wrapper result is not yet claimed.

★ Use a committed external RentObservationProbe with null-default harness
callbacks around each ordinary LongPlaytest month, rather than an ensemble
driver whose policies differ from the default city. The observer invokes
the complete existing DEFAULT script with random world48213/variation0,
600 months, native Standard/today founding and the occasional player;
automatic works, hand/advisor rates and the existing yearly player remain.
No policy, fixture, model equation, threshold or assertion changes. It stops
its own diagnostic JVM after the month600 observation, before unrelated final
harness probes, retaining the normal TempDirs shutdown cleanup.

Review of the first source-prep patch catches a period-mapping risk: the
household rent strike precedes the late new housing clearing. Freeze actual
opening immutable leases, never reconstruct their bill from closing homes.
The revised patch fe9f976d is accepted after tracing the payer lineage:
HouseholdBalance.advanceMonth passes the same fresh census to splitIncome,
followThePeople and settlement. Closing HouseholdBalance counts/keys/rows/
earning weights are still those held settlement inputs; the closing family
census and new leases instead describe the next period. Do not call the
private takeCensus as a reader: it writes dependants. The observer keeps
all opening and closing cells, including exact-zero cells, row books,
every positive settled cell, frozen contracts, unmatched billed keys and
lease-minus-account-rent reconciliation. Existing saved index and household
arrays bracket the closing observation to detect unintended mutation.

The output retains raw current-thousands monthly totals and per-household
amounts, actual consumption and fee conventions, nine component weights and
reference links, payer/full/half/sponsored sharing quantities, and population/
doors/unplaced/pipeline. Income is an explicitly labelled monthly cell proxy
with raw source inputs, rather than a Census gross-income estimate; utilities
are not silently added to rent. Zero/negative-income and unmatched groups
remain visible, with no epsilon denominator, national fitted cap or .5-count
filter. Detailed income and burden claims await the approved native run.
The helper holds after its active stage until the observer is pushed and
receives a new exact-commit GO; no unpublished probe JVM is run.

ExchangeRateCheck's complete canonical result is green on f0b960367:
native/batch0, 236 labelled checks, 28.4 seconds (c08d). DenominationCheck,
already active when the observer HOLD arrives, then reports a NEW red on
the same integrated model (e69b): the two scales' output, annual output,
rent and price level differ slightly. These are not passed by name or
assigned to a fixture owner from their size. Finish that one active native,
then HOLD every further check/probe; source and retained evidence go to the
helper for the exact failed law, first divergence and causal diagnosis.
The 572dfc717 observer GO is superseded by that red HOLD (5ac2). No model,
assertion, tolerance, seed or fixture is changed to escape the difference.

The final Denomination verdict corrects the initial classification (9ece):
canonical4 in 86.1 seconds, ten failed assertions. Seven decade-twin drift
lines are already [known] under the canonical judge. The three NEW assertion
lines concern under-half household-cell holdings immediately after reform
and over the following months, and the TP1 upward-price fixture at index
0.8195. They remain held for source diagnosis. Exit and total-FAILED lines
explain the five report rows; they are not five independent model failures.
All helper-owned JVMs/controllers have ended; no next native or probe starts.

The source diagnosis (bdb5) identifies the two ownership predicates:
DenominationCheck.scanEmpty uses Household.isEmpty, whose optional-action
threshold is count below0.5. The positive-count financial settlement mend
instead preserves lawful fractional owners. The native log does not identify
the offending cells or distinguish them from exact-zero households, so do
not declare that diagnosis proven or assign the lines to a fixture sweep.
The TP1 source likewise waits at most twelve ordinary months for a floor
above founding without causing inflation; its actual index is0.8195. Its
existing same-floor, card and lane comparisons pass.

★ Approve a bounded committed DenominationHeldFinding for exact evidence,
using the original two city builders, closed-used-car prelude, reform100,
and existing1/11/88 follow-month sequence with all202 native scans. Keep
the original empty-owner and sameCity predicates and their1e-9 guards;
the diagnostic is expected to retain their failures rather than manufacture
a green result. Two null-default harness readers expose the original scan
and TP1 wait; no model, fixture, window or assertion is changed. A separate
TP1 mode runs only the original section and its unchanged twelve-month wait.

Parent reviews source-prep f6da1f19 and adds a stronger explicit raw-position
guard for every exact-zero household count. Snapshots inventory all native
stocks, including dynamic bonds, security basis and tax claims, before and
after reform and at every original look. Raw per-household values remain
visible even when their household-weighted total is zero. The added per-stock
reform comparisons retain the native1e-9 relative guard and distinguish local
money, USD, physical claims and status shares. Read direct fields rather than
HouseholdBalance.stocks, which writes its bond-id cache; bracket reads with
exact saved household/CPI array comparisons. Pushed-source compile and the
cells diagnostic need fresh MG1 and an exact GO; TP1 gets a separate GO after
the first result. Neither probe mode has run at this record point.

The6dc model compile passes with fresh MG1 and unchanged model/resource
bytes (d85c/64d2). An external-javac shell quoting failure occurs before a JVM;
the helper corrects only the retained command wrapper and gates again.
Actual external javac then finds a checked IOException from TempDirs.create
which the new tp1 helper failed to declare (5a01). Parent corrects that single
declaration; main already propagates Exception. No model or native probe has
run or changed. Reuse the already compiled, byte-identical6dc model classes,
verify source/class identities and compile only the corrected pushed external
driver under fresh MG1 before the previously approved cells-only run.

The corrected external driver compiles on1ce784c2c; all2527 model/harness
class hashes match the approved6dc compile and all model/resources/pom/script
sources match, so Maven is not repeated (1a14). Fresh native MG1 reports
6.388 GiB available RAM,12.795 GiB free commit and25.422 percent CPU. The sole
cells JVM completes all202 original looks and preserves exactly nine original
failures: seven canonical-known decade comparisons and two legacy under-half
ownership claims. It creates206 raw snapshots including the four preparatory
before/immediate-reform reads; no TP1 or other native runs concurrently.

The actual evidence (9b9d) proves exact-zero owners are0 in EVERY snapshot,
with206/206 explicit raw-owned-position guards passing. Positive under-half
owners are initially65/city, representing7.936244420111659 households; they
range64-66/city and cover66 distinct keys. The first legacy culprit exists
already before reform at month121: SINGLE_ADULT:SENIOR_PROFESSIONAL represents
0.3938910690689519 households and owns180 nonzero positions. Savings are
20.990887648283096 current-thousands per household,8.268123176488485 weighted;
other positions include debt, USD, twelve equity shares/bases, eight tax
claims,132 bond positions and card status. The raw named positions and hex
values remain retained; these are real positive owners, not empty-cell ghosts.

Every35109 individual1e-9 reform scale comparison passes, as do the unit
guard,206 purity guards,202 native-scan/inventory agreement guards and the
202-look guard. Reformed local money divides100; count/physical/USD positions
retain their units. No unexpected zero-owner, immediate scale or purity
failure occurs. Offline analysis also retains tiny later post-step differences
without inventing an attribution: first bit difference is immediate construction
basis,1.16e-16 relative; first above the comparison's normalization floor is a
month122 bond position1.6921943267570264e-21 founding units versus0. The known
decade comparisons are not changed or newly blamed from these observations.

Evidence is helper target/integrated67/held-1ce: cells/cells-raw.jsonl
(1441722078 bytes),cells.log,analysis.json with all66 keys,analysis-brief.json,
commands,gates and source/class/JDK/asset manifests. All own native/controller
jobs end before offline analysis. Separate TP1 authority then permits only the
original section/unchanged wait on the same compiled1ce source with fresh MG1;
no repeat compile or other native. Prepare a registered ownership-guard
correction only with caused zero/positive-fraction contrasts and all original
raw-stock, scale, money and save comparisons retained. Global isEmpty and its
optional-action threshold remain unchanged. No corrected native has yet run.

Standalone TP1 completes on the same1ce classes with native1 for ONLY its
existing upward-price premise (4ea8). Its dynamic endpoint is0.7551; keep
that standalone result separate from full f0 Denomination's0.8195. The
original floor/card/lane/scale guards otherwise pass. No Maven/javac repeats;
fresh MG1 passes at5.189 GiB available RAM,11.738 GiB free commit and26.278
percent CPU. All own native/controller jobs end before the next source-prep.
No extra step, input, rate, wait window, tolerance or original law changes.

Final TP1 evidence (51bb/37b6) retains13 paired observations over the same
twelve waits, months25-37: start CPI1 and plain/reformed floors7.7650391528935252
and0.077650391528935256. End plain CPI0.75509439409361367 and floor
5.8633375342673233; reformed CPI0.75509439409361334 and floor0.0586333753426732
at unit100. All106 other assertions pass, including26 raw-array purity guards.
The raw TP1 SHA256 is d610cce1fbe1c6e206a35d7683eeea21eed89928aa8d72f9472686165908aba2;
CELLS raw SHA25630190dd4a04b0648bcdc4a8e40128d2923585acb9c5be3bfab30ffafbe5d32b4.
Complete class manifest SHA25631f0a1492e46fd68c5ec241b986c12936b61c0de9b1d4fab16669b01ebdf1f14.
No model/source differences or live owned jobs remain at this diagnostic handoff.

★ Financial ownership belongs to every positive household count. Correct the
registered Denomination scan's forbidden-owner criterion from the optional
under-half threshold to absent/nonpositive counts, preserving the complete
native holdsAnything inventory and the exact emptyHeldAfterReform==0 and
emptyHolding==0 requirements at all202 original scan points. Keep a separate
positive-under-half census count, the existing1/11/88 month sequence and every
other scale, audit, money and save predicate. Global isEmpty and EMPTY_CELL's
optional-action behavior do not change. This is an explicit criterion change,
supported by the raw evidence above, not a label change or a model write-off.

Parent reviews source-prep b18bd857 and actual census/credit APIs. Its new
registered FinancialOwnerFinding causes an exact-zero owner with raw claims,
plus established owners at EMPTY_CELL/2 and1.5*EMPTY_CELL through the native
census/follow/fold path. Every stock family, including bond faces/rollup and
security basis/tax claims, must clear for the absent owner and remain for the
positive owners; every household-weighted claim must be conserved. Actual
deposit-interest and paper-coupon receiver APIs must pay the fractional owner
and add exactly the two stated inflows, with no other weighted claim change.
Native redenomination then tests the same raw/weighted1e-9 unit law. Parent
adds a separate raw-per-position reform comparison for the above-half owner,
the class's WHY and the diagnostic's updated count comment. None of this
changes production settlement or silences an original conservation/save guard.
The corrected registered finding and Denomination native run are still pending.

Parent also finds a validation-record issue from the actual script: detached
worktrees have no branch name, so record_reds cannot select RES3-ship.md.
For the next canonical calls use the existing supported CBS_KNOWN_REDS input,
generated from exactly the same fork-main gate record and committed RES3 ship
rows that the normal batch/RES3 branch selects. Retain that file, its hash and
both source identities; add no handwritten approval or extra waiver. This
replicates the ordinary branch judge, preserving all native assertions and
NEW-red stops. Stale Kept/FS5 rows are explicitly excluded as valid acceptance
even if their text matches: they still need current native evidence and an
honest queued owner if any red remains. No process/script code is changed.

Origin/main advances only to de5185104, adding QUEUE entries and PF1/PW9
briefs. Parent confirms those are the only three changed files; publisher
version stays0.9.201 and shipped format/floor66. Merge a03579d4b imports those
records without editing them by hand. Its model/harness/resource bytes remain
identical to the approved75937af98 native source, so the ongoing exact759
validation continues; no duplicate native or new format boundary is needed.

Canonical75937af98 DenominationCheck completes at exit3 (cd00) using the
supported combined record SHA256c84e95e9ed945d26549e6762db408910e8b5d8bd7d4bca137052bfb0d8d0a755.
Only eight recorded assertions remain: seven known decade comparisons and
the full-run TP1 index0.8195 upward-premise line. ALL new caused absent/
fractional/above-threshold raw-position, paid cash-credit, weighted-conservation
and reform guards pass, as do real-city absence scans. Maps regenerate and
Stale has zero firm findings. Compiled class manifests show only the expected
Denomination change and two new FinancialOwnerFinding classes; every other
model/harness class matches1ce. No first-five native rerun is needed for this
harness-only correction. HousingMarketCheck starts next after fresh MG1.

HousingMarketCheck on exact759 passes canonical0 (92af). Its actual full
catalogue report includes all14 forms/32 classes, present and1900 quotes,
budgets, availability, paid starts and completions, retained as helper
target/integrated67/validation-759/RES3-catalogue-report.md. Small scratch city
is282 residents at53.035 ms, H/T/A/M/L65/32/139/0/0; present integrated month
62.022 ms and larger147739-resident month61.781 ms. Historical month42.686 ms
and larger147508-resident month46.025 ms. There are141 permanent household
cells. These are actual integrated-month measurements under the recorded local
host/JDK, with no retiming or isolated-solver claim. Final transfer and commit
of the generated report/maps remain. Invest starts alone after fresh MG1.
The same report also confirms all819 named new Denomination position,
conservation and scaling checks pass on the corrected registered guard.

Stage1 completes green0/3 (cfcf): current759 InvestCheck returns3 for ONLY
the exact builder-wage HT1-H fixture line, already in the fork's combined
record; no broad waiver or other failed funding law. HousingMarket's full
harness takes17.6 seconds and retains its actual14/32 report. The first-five
f0 source-labelled passes remain valid unchanged-model evidence; Denomination,
HousingMarket and Invest now have current759 verdicts. The combined approval
file, commands, per-stage MG1 and source/class/asset/JDK identities are archived.
HousingCheck starts stage2 alone; all remaining names run one per call.

Current759 HousingCheck passes canonical0 (b8ab), with zero firm Stale.
Its native report also marks one existing path OBSERVED/not counted; do not
represent that observation as an asserted pass. Retain the full native output
and obtain the exact label/source context at the helper's next boundary,
without a redundant rerun. HousingDoorCheck starts next under fresh MG1.

The observed label is explicit (8b8b): "fixture: the city closed homes and
left their debt on the landlords". strandedDebtShare's0..1 range is counted,
but Observation.reached(share>0, closed-home share save round trip) does not
reach its save/load branch. That block/predicate is identical to main; the
whole HousingCheck file differs, so make no whole-file identity claim. Existing
Choice allocation and HMC class-local debt/glut checks do not prove this
positive-stranded-share path. Its missing coverage is recorded without a
rerun, changed premise or substituted counted pass. Ask the helper to identify
actual impact and any already-caused Door evidence before the final coverage claim.

Current759 HousingDoorCheck passes canonical0 in165.1 seconds, including
the exact no-month-unplaced>1 law, actual played closure/refusal checks and
all monthly household cash audits (5b0c/69cf). HousingInventoryCheck also
passes0, all native checks, with zero firm Stale. HousingChoiceCheck starts
next after its own fresh MG1; source/assets/JDK/combined approvals unchanged.

HousingChoiceCheck on759 also passes0 (9422), including retained incumbents,
source-count conservation, rounding remainders and paid-half ownership. The
preceding HousingInventory native takes2.4 seconds. HouseholdCheck starts
alone under fresh MG1. The retained controller additionally hashes pinned
java/javac/earth/world and the exact combined-approval file before each call;
these are read-only identity guards, with no model/harness/process-source edit.

HouseholdCheck passes0 in5.6 seconds and BuildingDataCheck passes0 on759
(97ba), all native counted laws green. ReloadCheck starts next alone under
fresh MG1. No extra closure fixture/probe/replay is added during the source-
only investigation of HousingCheck's separately reported observation.

The narrow impact report (cfd6) confirms no Door/HMC case directly causes
positive strandedDebtShare and reloads it. Door's "stranded" variable counts
orders missed by a lender. HMC separately counts class-local carry/no subsidy,
whole vacant-title retirement, occupied-form retirement/displacement and family-
door load census; these do not substitute for the missing positive-share save
path. The RealEstate capacity/share writer, getter, carriedInterest and legacy
extras save/reset/load code match main; EconomyManager's inventory-capacity
caller is also unchanged. RES3 does change mayRetire and mixed-class spare-
retirement limits, so its closure trajectory can affect the branch. Do not
make a blanket unchanged-path claim.

★ Add one minimal caused positive closed-home debt/save/continuation case
through the existing actual funded inventory and permitted retirement path,
preferably extending HMC's real retirement/load fixtures. Source-prep first:
actual Bank debt, capacity loss, positive held stranded share, exact reload and
next-month money/debt/capacity twins; no direct share setter, new production
seam, fabricated flow or relaxed gate/tolerance. The native HousingCheck
observation remains honestly uncounted; the new registered case must supply
the coverage. Other already-approved one-name checks continue on759 while
the helper prepares source only. No new-case JVM or model change is authorized.

ReloadCheck on759 passes0 in199.7 seconds, all checkpoints and caused states
(d1a4). CarCheck returns3 in8.0 seconds for ONLY three exact combined-record
premise lines: the4800-trial price-edge search does not cause its edge; the
all-off-ship premise has domestic maker stock (66 cars/10 maker stock on this
path); its exact declared-import-versus-total-paid comparison consequently
includes domestic purchases. These are existing recorded rows, not a new
classification or waiver. OilCheck starts alone. The closure fixture source-
prep must account for BF1's native retirement loan repayment rather than
incorrectly asserting unchanged principal; conservation includes that real flow.

OilCheck on759 stops at canonical4/native1 for one NEW assertion (7a65):
"a town with no station, its drivers petrol all past the stations at the queue
price, is given one". The native unmet demand is9317 litres, but the reported
staffing is33 percent now and75 percent with a year of arrivals, below the
unchanged80-percent gate. All own processes end, and CentralBank/other native
stages remain unstarted. Diagnose exact fixture inputs, gate eligibility,
source impact and the actual queued sweep owner from retained evidence;
no baseline/full rerun, relaxed staffing gate or invented waiver. The closed-
debt source-prep continues independently without a JVM.

Oil's bounded diagnosis (6459/6d31) identifies the actual condition: unchanged
town(oilcheck-nopump,600 Houses,8000000 spare ft2), RETAIL investment held,
300 extra Houses and24 ordinary months. The assertion requires a Filling
Station candidate but never causes staffing. Sector.staffing correctly rejects
its formatted33-percent current/75-percent forecast share below the native
80-percent MIN_STAFFABLE_TO_ORDER; no independent raw workforce draw was
logged, so do not invent one. Later gross-margin, project-cost, order-size,
land and staffable-count gates are not reached. A neighboring margin-minus-
wages1e-12 law passes at-0.260606; that is not this candidate's gross-margin
test or proof of what staffing alone would make it choose.

The whole OilCheck and AutoBuilder blobs match main, as do Sector's staffing
functions and Retail.planStations/litresForecast. Sector differs only in the
held-payroll save lines, Retail only in pure current grocery/fuel readers,
and BusinessInvestment in typed spare-retirement limits. RES3 changes the
played housing/migration trajectory; no fresh-main failure is claimed and no
baseline replay is run. All other oil/material/price/duty/save laws pass;
there is one failed assertion in29.2 seconds.

★ Pass only that exact missing-eligibility fixture line by name to EXISTING
queued FS8, the industry/research/views20-harness sweep. Parent verifies
OilCheck's FS8 ownership in by-harness row162 and the live QUEUE row; FS12 is
a different sweep. The ship row names its precise assertion and cause, keeps
24 months/80 percent and every financial/physical law, and claims neither
all later gates passing nor a new generic exemption. Record-only update and
supported rejudging of the retained native log can establish canonical3;
do not repeat Oil's native merely to rediscover this result. Remaining named
checks may resume only after the new committed record is selected exactly.

Supported saved-log rejudge on records-only9030fd45d returns3 (b312), ONLY
the exact FS8 Oil row and recorded lines. CBS_REJUDGE=1/--judge uses the
committed fork/RES3 record; the original759 native suite/log/record is kept
and a903 copy judged separately. No native/Maven repeats or handwritten
approvals. New combined-record SHA2568d4047539f8268c5138072be591959c64422d9b4529188d0c5512d5f20059c6c;
the fork gate blob c02d8ef0 is unchanged between78ed/de5185, with all source
IDs retained. Fresh judge MG1 passes at6.613 GiB RAM,12.808 GiB free commit
and24.003 percent CPU. CentralBankCheck starts alone under its own fresh MG1,
with unchanged laws/bounds and no retiming. Closure/save source-prep continues
without an extra JVM or tracked source edit.

★ Review the external observer's early month600 exit before its first run.
It originally skipped the native final summary and could hide accumulated
ordinary findings. The source proof (e796) shows flag increments the actual
LongPlaytest.findings map rather than throwing; the native terminal condition
is findings.isEmpty() && LongPlaytest.MoneyFixtures.fails==0. The counter at
line9002 belongs to that nested helper, not outer LongPlaytest. Parent checks
both classes/fields are package-visible and the raw household-audit getter
exists. Existing attached-observer Throwable rethrow remains intact.

Adopt source-prep SHA256450fda40a3f02764eb63067c8708b8e8f5756c0309e55be6cace7318def411ea.
It records ordinary-audits.jsonl for ALL months1-600 with native findings,
counts, first/last/worst and actual money/household audits; the240-600 metrics
remain361 rows. These new reads stay inside the existing exact saved-array
purity guard. Flush all outputs and use the native failure predicate for the
diagnostic exit, explicitly marking unrelated final probes unexecuted. Parent
adds PrintWriter error checks and a nonzero exit if any output failed, so
silent I/O failure cannot yield a complete measurement claim. No production
seam, policy, rate, fixture, native audit, threshold or model changes. This
external diagnostic still is not a full LongPlaytest/ensemble verdict. Its
native run remains held until the final reviewed/pushed source GO.

CentralBankCheck on903 reaches the unchanged1740-second bound and returns
canonical4/native1 for the timeout only (bd4d); no law failure is logged
before it. The last completed42-house/3-month/21-added comparison at month600
is strict4.314 versus Standard4.551 percent, but this partial output is NOT
a completed harness pass. Preserve full native/log/summary at helper
target/integrated67/validation-903/CentralBankCheck and run-CentralBankCheck.txt.
Controller96120 ends normally, native9668 is gone, and no owned JVM/controller
remains. Other sessions are untouched.

The retained progress sample at1505.7 seconds shows1660.7 CPU seconds,
1.548 GiB resident,3.87 GiB then-available RAM and9.991 GiB free commit,
after a legal fresh start with over6 GiB available. Do not infer a model
verdict or invent a timeout waiver from it. Under the orchestrator's existing
named-heavy gate-box authority, prepare a CentralBankCheck-ONLY command on
pushed d936 (model/harness903/759 unchanged), holding box.lock shared with
the same1740-second bound and exact committed approvals/assets/runtime proof.
No suite, gate, dry run or ensemble is authorized here. Parent reviews the
explicit one-name command before launch; no further native starts meanwhile.
The concrete closure/save source patch is prepared separately for review.

★ Accept the narrow held-state case proposed in76fa, SHA256
1bb5b4509c8bd130ea6b892ca028ef23fc759cb16758c04277d7915f46bd15e9.
It uses the native private mortgage payer with public paid buildFor, actual
Bank claims and construction queue, with declared vacant stock/equity/ground
before the first audit. Every funding, vacancy and whole-form retirement
precondition is asserted. The actual BuildingManager physical retirement and
land-release primitives match the existing loaded-door case; the next ordinary
month invokes the native capacity writer and must CAUSE positive stranded share.
No share setter, fabricated debt or save restamping is used.

Scope is explicit: this physically closes eligible vacant titles while retaining
actual loan principal/claims and cash, then tests the held positive share,
capacity base, Bank claim, every borrower claim and physical owner inventory
through native scratch slot10 reload and the next ordinary audited twins.
It does not invoke private Game.retire's salvage, land-sale and BF1 loan-
repayment settlement. Do not claim that planned-settlement path or the old
HousingCheck OBSERVED branch is now counted. This narrow case supplies positive
held-state/save coverage, while the existing debt-locality and ordinary
retirement/audit cases retain their distinct scope. No production eligibility,
funding, repayment, research, model, tolerance or existing test changes.
The new case is registered in HousingMarketCheck and remains unexecuted until
the reviewed source is pushed and receives a separate native GO.

The c7e model compiles actual0 after correcting a PowerShell wrapper that
mistook the routine JDK_JAVA_OPTIONS stderr NOTE for a terminating native error.
Keep that wrapper failure and its unknown Maven exit separate; the corrected
run records real shell exit0, fresh MG1 and no compiler diagnostic (0ed3/ed80).
Canonical HousingMarketCheck then returns4/native1 in18.6 seconds for EXACTLY
two new fixture preconditions (06e4): the order did not prove creation of new
Bank mortgage principal, and normal mayRetire is false. Actual paid queue4,
Bank claim, vacant-title limit>=5, physical closure, positive-share writer,
native exact reload and following-month debt/capacity/claim/title twins plus
money/household audits pass, but the whole case is invalid with failed premises.
Do not count its intended closure coverage as complete or remove those guards.
Source/retained-data diagnosis must cause the correct mortgage ledger/phase
and all normal retirement conditions through inputs; no unpublished replay.

★ The unchanged CentralBankCheck/model can be validated independently while
the helper corrects only the new fixture source. Override the earlier ordering
condition and authorize the reviewed c7e Central-only box script, SHA256
2bd3c6695d0935519395675534fedf6c550c5d42741f027191d8b00761cd5c6b,
after all local jobs have ended. It differs from reviewed76cf script only in
the pushed commit/run label, uses shared box.lock, fresh MG1, exact approvals,
asset/runtime/source/class proof, original1740 seconds and one exact name.
Local Maven, HMC, default observer and other JVMs remain held during the remote
heavy check. No timeout waiver, full suite, gate, dry run or ensemble. Preserve
any network/resource/lock impediment; no unapproved fallback or new model fix.

The c7e failure handoff (2614) proves all non-HMC compiled classes match903;
only the expected three HousingMarketCheck classes differ. Source/assets/JDK
and8d4 approvals match. Native log SHA2566e8aea58e4808fe6c6343a5858a53a27723df9f770a1e6fdebdac3a756e1df28;
suite SHA256097e2d55bdb43c2647462e7fc47e8d18e021b5ff4fee235e90e42681e4478195.
The new case's paid order was cash-funded rather than a demonstrated new
mortgage: buildFor borrows only a positive cash shortfall, and no subsequent
repayment in buildFor explains the absent increase. Raw pre-order cash/cost
was not logged; do not reconstruct it. Per-class vacant closure is allowed,
but mayRetire's separate unplaced/doubled/segment-shortfall terms were not
logged individually. Their exact failed term remains for source diagnosis.

The reviewed Central-only box launch starts02:06 local/06:06:03 UTC (ebd9),
on exact pushed c7e under shared box.lock, no local JVM overlap. Both exact
Earth/world hashes and supported8d4 record pass. Fresh box MG1 reports
27.855 GiB available RAM,21.912 GiB free commit and mean0.0 percent CPU
over three two-second samples. Remote run is
~/cbs/runs/res3-integrated67-central-c7e; local SSH controller39987 is retained.
Same1740-second bound, one exact CentralBankCheck and parallel1; no fallback,
retiming, waiver or new predicate. This is an active run, not a pass claim.

The completed named box verdict (7eba) is canonical/native0 in533 seconds,
compile0 in9 seconds, under the unchanged1740-second bound. All original
policy/preview/scale/save laws pass. Native STRICT_SHAPES reports22/24 very-
strict below-Standard cases against the unchanged18 required, median-0.378
percentage points/year. Individual reversals were not pre-judged. The suite
has one passed harness and zero failed; do not confuse that with one assertion.

Exact local c7e versus remote source/resource/pom/batch/probe manifest bytes
match (zero differences), tree05e67102ea29ba88b1e8afcf2d95f93ac02d3f3b,
source guard0. Remote runtime is Eclipse Adoptium Temurin21.0.13+9-LTS/Linux,
explicitly distinct from local Oracle21.0.4; actual release/java/javac hashes
and Maven path /usr/bin/mvn are retained. Remote class-manifest SHA256
5856dc90a37c3d5b67a52cc5064dc74aee18c33d1a90f7df238783d358f55b1d.
All data,8d4 approval, fresh MG1 and shared-lock proofs pass. No timeout waiver
or new known-red row is used; the local timeout remains separate evidence.

All box files are copied home to helper target/integrated67/central-box-c7e-prep/remote,
with verified checks.tgz SHA256bcd234b7aefd44f9a54cc9426dc7131c1bf54a8fdb7267d1099aa8abece67b1e
and generated-docs.tgz SHA25620e55b6ed6fa1bf53884d6962616cc809d761df8b8bed478bda77ce6094294dd.
Native log SHA2562f01354c9dff9f0906bc8a2085b5b8743dc3c6dbfb0f30c1e0e0c064fba2da16;
suite SHA2566d78447f35d65d0333bccd29936bad8fb07e44119cc93bdbce2eb2a7950235fc.
End guard06:16:22 UTC finds no owned process group, controller, model-cwd
process or local Java/bash job. Remaining named/default/fast stages stay held
until the next exact source GO; the separate HMC fixture red is not hidden.

Independent c7e CallCheck passes0 in16 seconds, including the required original
sections2 and6 and actual Bank-standing/room premises (5bb4). DeskCheck passes0,
291 assertions in27 seconds, with FC2's D$14902 household-spare-for-paper
premise and all ledger/load/page laws green (30be). FundCheck passes0 in46
seconds: actual two-company/rescue market books,70-percent bid cap and default
shortfall pro-rata sale cases all pass (a0e9). Its first prelaunch MG1 refuses
3.791 GiB RAM; no JVM starts. Resources return naturally and fresh RAM4.801/
free-commit11.271/CPU13.323 percent passes before the real run; retain the
refused gate separately without a waiver (c584).

The unchanged selector has substring semantics (1254/9ac0): argument BankCheck
selects BankCheck AND CentralBankCheck. Bank's native result completes
"All checks passed" at02:37:09, including TAB_LOSS20 claim558.47k, kept-25.34k,
writeoff33.78k and both equity identities. Do not claim the incomplete combined
query canonical0. Its coordinator42264 starts redundant Central child3292;
the already completed c7e box Central result remains the valid full evidence.
Parent directs an owned-PID/parent/creation/command check, then stops only
that redundant owned child/coordinator/runner tree and preserves Bank's actual
completed native/result metadata. No Bank/Central replay or selector-code fix.
CardCheck similarly selects BuildCardCheck; explicitly allow that bounded pair
as affected builder-quote/card validations with parallel1 and separate native
verdicts. No new whole-suite selection or cancelled-test pass is implied.

The opening-census source revision (e538) confirms Game.run initializes an
empty demographic/household census, so the earlier wealth loop could be
vacuous. Parent reviews full patch SHA256
d82d73005d4a7f620a405dbb892b0a3d3a386d5613a9a119b21e8b4379b36fdf.
Only an absent population is seeded through native cohorts.migrate(500),
PopulationManager's actual population/jobs/fill and FamilyModel.rebuild;
the existing non-accruing account replan makes actual held household counts.
No household counts or family-shape/tier shares are assigned directly. Assert
positive census/population, unchanged native cohort/family arrays and every
existing raw financial position before declaring any wealth.

★ Declare one native House invoice of opening savings per actual positive
household, with matching weighted Bank cash, BEFORE the first ordinary audit.
Count and print every positive cell, including below0.5; require the floor,
exact unchanged count vector and all non-savings positions. Native refreshBank
must derive the matching deposits without moving cash. Use the existing
open-audit/pre-bank-settlement callback to place the actual paid order, choosing
quantity from actual cash/native quote so a genuine admissible shortfall is
asserted. Keep every mortgage principal/queue/downpayment/one-mortgage/lending/
fee/premium guard; parent additionally aborts on a wrong mortgage count.
Normal mayRetire and vacant-title limits remain required, with full diagnostics
and abort before invalid closure. The exact held-save/next-twin/money guards
are unchanged; retain the real slot10 pair for evidence before TempDirs exits.
No production, rate, seed, tolerance, retirement or underwriting rule changes.
This revised fixture has not run until the next pushed-source native GO.

The redundant selection is stopped only after ownership verification (643d):
child3292 creation02:37:09.534362/parent42264, coordinator's exact BankCheck
command and top34776 runner match the recorded run. taskkill by owned34776
PID/tree ends exactly six owned processes; all are confirmed gone and no new
JVM starts from that call. Preserve Bank's complete "All checks passed" output,
TAB_LOSS/equity pair and native-log SHA256
d590d08e779cba0afe6c02c5c1ce9a23cf88ab4a0b1c227e2b1d7f53a9dc82d6.
IMPORTANT: numeric Bank native exit was not persisted before cancellation;
BankCheck-cancel-status.json records CanonicalVerdict=null. A raw shell wrapper
returning0 after the forced tree end is NOT a completed canonical verdict.
Do not manufacture an exit, retag/rejudge incomplete output or claim a cancelled
Central pass. The separately completed box Central remains the complete evidence.

The approved CardCheck/BuildCardCheck subset completes canonical0, both native
classes0 and two harnesses passed (0bd6). HC1 card/debt/service/loan-book/capital
continuation and format60/61 byte/running-city refusal guards pass. Actual
closing holders8.394 percent, late households0.780 percent, late balances10.024
percent and annual charge-off0.854 percent are retained, not fitted targets.
BuildCard's actual quotes, funding and insured-mortgage guards all pass with
no FS12 waiver. Remove the five obsolete own BuildCardCheck known-red rows;
the original assertions are unchanged. WaterCheck then passes0 in4 seconds,
including its format30 refusal/file-untouched law (9037); fresh RAM6.300/
commit12.328/CPU14.412 percent and all pinned identities pass. WaterPowerCheck
starts next alone before the new-source switch request reaches its boundary.

WaterPowerCheck passes0, all41 checks, on c7e (81ff). Before source adoption,
all68 changed generated docs are archived as generated-before-a09.tgz,
SHA2568893b1f60cd4ccdce3a9fd50acad911f442c23d2378e1be892bcb5eda62ca5ad.
The helper cleanly adopts a09c6d00b, tree2eae5e8ef6ffcafe58b3c0f0037919e92558c541,
and pinned Maven compiles0 with fresh RAM6.061/commit12.251/CPU26.807 percent.
Only the expected three HMC classes differ from c7e; every other model/harness
class is byte-identical. No earlier native is replayed. The first HMC preflight
Get-Counter call throws800007d6 before a valid numeric CPU gate; no native
starts and no test failure is claimed. Retry the complete fresh gate with
unchanged thresholds/samples; an API failure is not permission to substitute0.

The actual a09 run (5f62/fe00) then proves the opening census, all23 positive
endowments, Bank backing/book, genuine mortgage0->1, principal increase and
all lending/fee/premium flows with ordinary money/household audits. The case
still fails normal retirement and its vacant-title limit, aborting BEFORE
physical closure. Actual unplaced0, doubled106.35/crowded42.86, and House
vacant limit0 against14 show the declared offer stock has not caused a fully
normally housed town with spare House titles. No new save or later report is
produced; prior c7e reports stay source-labelled.

★ Increase only the declared opening House stock from200 to1000, retaining
the same500-person native census input,20 Studio structures, actual lending,
all retirement gates and every audit/save guard. The offer cushion must cause
the required normal placement and spare family titles in the native run;
it is not asserted green from a static calculation. No post-audit balance,
quote, household assignment or eligibility result is rewritten.

The dce7b53d5 case is now an ACTUAL canonical/native0 HousingMarketCheck in
19 seconds (c7fd), every original guard intact. Opening23/23 household wealth,
positions/counts/native Bank book pass. Actual paid mortgage0->1, quantity24,
native lending/fee/premium and queue pass. At month3 unplaced/doubled/crowded
are all0; normal mayRetire is true and allowed25 equals requested25 structures.
Physical closure25 then causes owned capacity0x1.1f8p13 ->0x1.1f5p13 and held
positive share0x1.55ed4d05c9afp-11 at month4. Exact native slot10 held state,
loan/Bank/title claims, next ordinary twins and all money/household audits pass.
This remains physical closure, not private Game.retire/BF1 settlement or a
claim that the older conditional HousingCheck observation was counted.

Current14/32 historical/present report and real native pair are retained with
checksums by the helper. Only HMC.class differs from a09; all other compiled
classes match. Source/Oracle21.0.4/assets/newcfed approval file are pinned,
fresh HMC gate RAM5.771/commit12.399/CPU24.195 percent passes. The remaining
seven unique checks resume one per call. After all are green and stale Kept
owners are resolved, the committed audited600-month default observer has a
conditional exact-dce GO; no policy/baseline sweep or full-playtest claim.

The remaining dce checks completed before PW3: Substation0/native0 in30s;
DebtStatistics0/native0 under1s; Research0/native0,247 checks in2s;
ModernRows0/native0,240 checks in13s; PostwarRecipe0/native0,233 in18s.
Their supported combined-record hash is
cfed388049545c5b3f4f918c0f6fb024d5c5165d7a67e720d915b833ffb013d1.
LicenceFees and Kept had not started. All owned processes ended at the
boundary; the default600 observer had not compiled or started.

PW3 shipped while those checks finished. Merge6db8db4a9 brings main
d657b3a83/0.9.202 into RES3. Only four generated maps conflict; take main's
copies and regenerate from integrated source. Actual source merges cleanly:
RES3's housing, held-save tariff and null-default observation hooks remain;
PW3's place/season power, future-known menu and pre-land/money blocker land.
PW3 makes no save-format change. RES3 remains67 over genuinely shipped66,
so the unedited0.9.201 boundary pair still proves the native previous-format
refusal. No retag, conversion or new format is needed for the merge.

The prior native evidence remains labelled, not promoted to a merged-model
pass. Hold the default observer and remaining old-source starts; the helper
reports source coupling and the necessary affected replay set. Newest ensemble
baseline is docs/ensemble/0.9.202.md. Static pick now says ALL254; money review
names Bank and DebtStatistics, saves review DataSave, HistorySave and both
format/floor declarations. The provisional marker remains until review,
ensemble, the authorized dry run and its final marker commit.

Root's actual merged6db8 --fast exits0, including compile/maps/Stale, retained
at target/RES3-root-fast-6db8db4a9-20261125-035313. Pinned Oracle21.0.4 and
the absolute C:/ assets are used. Its fresh gate has76-percent committed,
5.925 GiB RAM available,13.025 GiB free commit and mean10.109-percent CPU
from three two-second samples. No other owned JVM was active; controller45127
ends0 before the helper's next source GO. Commit the regenerated integrated
maps rather than restoring either parent's earlier generated copies.

Remove the three provisional KeptCheck rows naming shipped FS5. They provide
no approval for a future run. The integrated Kept native is still owed; any
remaining premise must be traced and assigned to a current queued owner.
PW3's main record independently names three fund-fixture premises under
queued HT1-H; do not assume those are the same as RES3's old three claims.

Fresh merged8bb SaveFileCheck exits native/canonical0 in123 seconds and
ReadPathCheck0 in105 seconds. MonetaryCheck also exits native/canonical0:
pooled16-31 TOTAL3.635685 points and non-shelter3.102148, every original,
pooled, nested, supply, saved/reform and purity law intact. HousingMarketCheck
then passes0 with the actual current14/32 report and positive held-debt
native save case; normal m3 unplaced/doubled/crowded are0, retirement25 is
allowed25 and physical owned capacity9200->9194. Reload is next.

★ Complete the Bank exit evidence as part of the required fresh box policy
replay. A bounded source read shows the named BankCheck query matches THREE
registered classes: BankCheck, CentralBankCheck and YoungBankCheck. The old
cancelled query never reached the third; its missing numeric exit stays
missing. The planned current-source box query now deliberately runs all
three sequentially (parallel1), rather than a separate Central-only replay.
All native results and the canonical query must complete; no mid-query
cancellation or retroactive pass. This additionally verifies the current
TAB_LOSS/equity pair and young-town funding with the merged power inputs.

The parent-reviewed box script is target/RES3-central-merged-approved.sh,
SHA256264dca26e945f3ec3c97dd4e140ab53236b9bde5663c2932c234519de9f3999e.
It retains the shared lock, native per-class timeouts, fresh MG1, exact assets,
source/approval manifests, owned process-group trap and archive/copy evidence.
The query is on pushed8bb, fork d657 and combined record
65641960704080c3915aca49a05d6e4619cd6f3fe1d33decfae134d8e7cedd9a.
Only after all local names pass and owned jobs end may it start. The default
observer is conditional on the complete box query passing, with no unresolved
new red, then fresh local gates for its external compile and native run.

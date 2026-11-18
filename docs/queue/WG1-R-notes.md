# WG1-R - WG1's review: the fixtures cause their own conditions (harness only, 2026-11-18)

The row: WG1-D (0.9.83) set five harnesses' cities' floor at the going rate, the wage main paid before the real minimum, so
that their sections passed: CurveCheck 7, CentralBankCheck 20, HoldersCheck 12-13, HousingCheck's closed homes and KeptCheck
64. Each pinned a path where it should have caused its condition; and two things the pins recorded about .85 were left as
questions of model behaviour: a city held at 15% closing no home in 240 months, and the bond desk standing still for six
months. Branch `batch/WG1-R` from main 2d895d646 (0.9.149), harness only, untagged. One Claude session, no slices.

## 1. What was found

**Each pin was out of date, for the section it was set for, on 0.9.149.** Each section was run on the default floor (a new city's
.85 of the going rate) with its pin taken out, alone where the section could be reached alone (a throwaway probe that called the
section's own methods, never committed), and every one of them reads as written. The path they were pinned against has moved
under them since 0.9.83: the fixtures GW1 gave them (every sector held, the glut placed) and what the model has had since (the
staffing gate, the world's band; not bisected) cause their conditions without the floor.

| section | WG1-D's figures on the .85 path | on 0.9.149, default floor, no pin |
|---|---|---|
| CurveCheck 7, the steep city at m39 | 1-month 1.80, 1-year 2.44, 10-year 3.44 (1.63 points; the fixture asks 2) | 1.00, 2.06, 3.39: 2.39 points; the same city a month before its dial was held 0.50 |
| CentralBankCheck 20, m72 | last year's surplus unused $-349.6k against a $5.2k slice | $113.1k against $5.2k; without the sector hold $106.1k, without the opened levers $27.5k |
| HoldersCheck 12 | the discount per face moved .0082 (asks over .01) | 0.0088 to 0.0210: .0122 |
| HoldersCheck 13a | second sale raised $59.25k on $59.53k of face | $193.0777k on $191.5376k of face |
| HousingCheck, held at 15% | no home closed in 240 months | 0.713 of the landlords' debt on closed homes (WG1-D's going rate: 0.106) |
| KeptCheck 64 | 0 of 6 months redrawn (DeskBars, PathChart, BuybackLadder, Columns); DeskCurve 5 of 6 handed back | 6 of 6 in each; DeskCurve 6 of 6 |

HoldersCheck's two margins are thin (.0122 against the .01 the line asks, a premium of 0.8% of the face); both read from the
model's own sale and are unmoved by anything this batch did.

**KeptCheck's pin is out of date for 64 and not for the city.** Unpinned, the whole harness (the city of sections 1 to 103 played
on the default floor) fails lines main does not, none of them 64's: five in a first full run (the four of section 63 and section
55's), and two once 63 was caused (the Government Overview's and the Partners' chart's):

- section 63, the fund's let-go month settled no holding (0 settled), the lot's page and its two tickets could not be made.
  The cause is the fixture's, found: `fundHeld()` tops the fund's cash up from the treasury, and by month 2,507 the default
  floor's treasury stood at 0.0, so `fundPayIn()` moved nothing, the fund held $7.5e12 of $1.06e16, the withdrawal sold to pay
  (toRaise 9.4e13) and the rule bought nothing. Caused now (below);
- section 68's Government Overview: "no month makes more than a tenth of the nodes the first draw made" read 46 of 395 (8 of 25
  months made none): the page's events in one month are over a tenth of its first draw, on this path and not on the pinned one;
- a pixel check in Trade & the World, "Partners' chart told other places for the buyers to join is the same picture as one built
  new, to the pixel", differed in 3 pixels (0 0 3 0 0);
- section 55's Bank Profit page, "most months make no node at all", read 6 of 25 under a loaded PC (a first draw of 297 nodes)
  and 17 of 25 under a quiet one (204 nodes), and passed in two of three runs: KBP1's page and its flake (the known row says
  a block of the page comes and goes under the suite's load), not the path's.

The last two are premises of their own sections, written on the pinned city; neither was reached alone, so their causes are
not traced (a run to section 68 and one to section 103 each cost 10 to 35 minutes on this PC). **KeptCheck's city keeps the
floor at the going rate**, now said where it is set (the comment names what the pin is for and what it is not for), and
**KFP1** (queued) takes the two lines.

## 2. What changed

- **CurveCheck 7.** The going-rate overloads of `city()` are gone. `steepCity()` holds the dial `STEEP_GAP` (two points) under
  the dial the market expects to settle at, `expectedToSettleAt(dm)` = `dm.expectedDialIn(YieldCurve.HORIZON - 1)`, the rule at
  the inflation the market expects (`YieldCurve.expectedPath()`), not under today's rule: the two are one at this month (the
  city has no year of prices, so the rule reads the target) and that is the only place they are. The fixture line now says so
  (`dial held ... under the dial the market expects it to settle at`), the steepness asked is `STEEP_10Y` (the .02 it asked
  before, now named), and a control is asserted: the same city a month before the hold, on the rule's hand, stood 0.50
  points from its 1-month to its 10-year, under the 2.0 asked, so the hold is what makes it steep.
- **CentralBankCheck 20, HoldersCheck 12-13.** The pin is out; nothing else. Their conditions are caused by what the sections
  already build (every lever opened, every sector held; the second sale at four points of rate over the first).
- **HousingCheck.** The pin is out. The loop now counts the months at the held dial that met the retirement's own conditions
  (`BusinessInvestment.RETIREMENT_LOSS_MONTHS` running months of loss, homes over the people by `RETIREMENT_SLACK`): 56 of the
  120, and `retirable > 0` is asserted: homes closing has the cause the model's own rule names, and the fixture shows it.
- **KeptCheck.** 63: `fundHeld()`'s top-up gives the treasury what the fund is paid in with (`setCashForTest`, between months, as
  BondCheck sets a sector's cash), counts the months the fund's cash still stood under `FUND_HELD_DUES` months' transfer
  (`fundShortMonths`) and asserts none; with it the let-go month settles 9 holdings on the default floor (124 in the pinned city),
  the lot's page and tickets run, and the 4 lines that were red unpinned are green. 64: for the bond desk's kinds (`DESK_KINDS`: CurveChart, DeskCurve, DeskBars, PathChart,
  BuybackLadder) the section asserts that the model's figures the picture reads (`deskFigures()`: the curve at 12 and 120
  months, the book's worth today, to the bit) moved in at least one of the six months, and that the same month read twice
  moves none; a picture that did not draw again in a month nothing moved is now told apart from a desk with nothing to do,
  which was how WG1-D read its six months. The city's floor line stays (section 1).

None of the new lines is a premise moved to let a path through: every law of the five sections is kept as it was, and every
`Observation.reached` they had is still one; none was reported not reached on the default floor (no `OBSERVED` or `SKIPPED` line
in the five harnesses' logs of the final runs).

"Each new assertion fails with its cause removed": CurveCheck's control is its own cause removed (the same city, the dial on
the rule's hand, is not steep). HousingCheck's count is not discriminating by the dial, see 3a. KeptCheck's cash line fails
with the treasury at 0.0, which is how it was found (the fund's lines above, 4 red). CentralBankCheck 20's surplus premise was
tried with each of its two constructions removed on its own (above) and survives both; with neither, not run.

## 3. The two things about .85 that WG1-D recorded

**a. "A city held at 15% closes no home in 240 months on the .85 path": not so on 0.9.149, and not a fault.** The held-rate city
closes 0.713 of the landlords' debt's homes on the default floor and 0.713 at the going rate. Without the glut the fixture places
at month 120 (a probe of the same city, held at 15% from month 120, no blocks placed) it closes 0.413 on both floors, and the
landlords' figures (people, homes, loss months, interest, operating income, net) are within a rounding of each other through month
218, 299 people against 288 at 241. What closes a home is `BusinessInvestment.planRetirement()`: the sector's loss months at
`RETIREMENT_LOSS_MONTHS` (6) and its homes over the people by `RETIREMENT_SLACK` (25%); at 15% the interest (9.05 a month at month
241 against 1.60 of operating income, net -8.61) keeps the landlords under for 92 months running on either floor, and the homes
stand over the people (1.56 a person), so the rule sheds them. The floor is under the average wage throughout (0.0726 against
0.1039 at m241): it binds nobody in a city this small. A dial held at 3% in the placed-glut city closes the same 0.713: the closing
is the glut's, not the dial's (section 4). WG1-D's zero cannot be reproduced on this tree, and 0.9.83's was not built to see it.
Most likely, and consistent with the study (spec-wages Table 1: unemployment 9.3% at .85 against 12.2% at the going rate): before
GW1 the people left a held-15% city (RN1's design, "the people go, and a leg falls under GLUT_PRESSURE some 25 months into it"),
at the going rate they went and a glut formed, and at .85 the unskilled stayed in work, no glut formed and there was nothing to
shed. Since GW1 the people stay on either floor (its comment: "no leg in glut in 240 months") and the fixture places the glut
itself. Right on that reading: a lower floor, fewer people out of work, fewer landlords in distress.

**b. "The bond desk stands still for six months (KeptCheck 64: 0 of 6 months redrawn)": not so on 0.9.149, and a desk standing still
is not a fault.** On the default floor the DeskBars, PathChart, BuybackLadder and Columns redraw in 6 of 6 months and hand back all
their pictures (DeskCurve 6 of 6 where WG1-D saw 5 of 6), and the new line says the figures they draw moved in 6 of the 6 months
(the curve at 12 and 120 months moves every month at 20% inflation and a dial held at 12.25%; so does the book's worth). A desk
stands still only when the model's figures do (no paper on the book, or a city that does not move): then the pictures are right
not to draw, and the harness now says so (`fixture: the figures ... moved in N of the 6 months`) before blaming a picture. What
stopped them at 0.9.83 is not reproduced here and was not traced. What a probe of the same city at month 605 (before KeptCheck's
sections) shows is how much the pin changes it: 7,935 people on the default floor against 2,414 pinned, the treasury at 164,950
against 8,256 and no paper on the book (pieces 0, against 1): a desk with nothing to do, which is a state the section does not
reach, since it sells one of each kind of paper itself at section 60.

## 4. Left for Jerus

- Nothing to decide. The two findings of section 3 are Pending rows with a recommended close (each can be settled by the
  orchestrator); **KFP1** is queued.
- Worth knowing: HousingCheck's closed-home tail (the debt share and its save round trip) does not test the held 15% dial, since
  the same share comes at 3%; the section's own subject (a glut's rent carries no lift) does. Not changed.
- The ensemble is not owed: no game class changed (a model change would have moved the default trace; none did).

## 5. The checks

On the tree at the commit named in the ship file, with the data named, each as `./batch-check.sh Name` one at a time (`--fast`
first: compile, maps, Stale 0 firm findings, exit 0). The pick, `sh tools/pick.sh origin/main HEAD`: 18 of 220 harnesses for 5
files, the review line "not needed - no money, saves or threading in the diff", not ALL, so no dry run:

| harness | result |
|---|---|
| CurveCheck, HousingCheck, HoldersCheck, CentralBankCheck | OK |
| AuctionCheck, BuybackCheck, CallCheck, ChartFitCheck, ForeignDebtCheck, MapCheck, MonetaryCheck | OK |
| ResearchViewCheck, RunWorksCheck, SaveFileCheck, TimingCheck, ToastCheck | OK |
| ScaleCheck | exit 3, known reds only (main's) |
| KeptCheck | exit 3, known reds only: the two lines `docs/notes/gate.txt` records ("Construction, Cash & debt as a statement: the redraw and the next equal a page built new" and "a month's redraw after each choice hands back nine in ten of the page's nodes, or more"); sections 63 and 64's new lines OK |

KeptCheck unpinned (the experiment of section 1, not a final state), run to the end on the default floor: 2 lines NEW besides the
known ones, the Government Overview's and the Partners' chart's, as section 1 says; a first run of it timed out at its 1,740 s
under another batch's suite (the PC was loaded: the same harness took 14 minutes alone and 37 minutes under load).

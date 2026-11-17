# FB6 - KeptCheck's fixtures cause their own conditions (2026-11-17)

Built on `batch/FB6` from main `f18b1053` (0.9.137), with main `886bd92d` (LX1, MED1) merged in. The rows are KS2,
KS1-Saved, KBF1, KHT1 and KF63 ([FB6.md](FB6.md)), and the seven `known-reds.txt` entries filed under FB6 on
main (lines 15, 16, 33, 35, 48, 49, 50). The change is the harness only, `KeptCheck.java` and `ui/KeptBench.java`.
No model or screen class changes, so the playtest's trace does not move. `sh tools/pick.sh origin/main HEAD`:
"review not needed - no money, saves or threading in the diff".

Each fixture now causes the condition it reads, with a `fixture:` line saying so, and every Observation guard these
sections had is out: their laws are hard again. No floor, band or premise moved.

## 1. KBF1 - section 44's funding page

**Cause.** The fixture took the first run the city could not pay for. Whether its bond and bill offers covered the
gap flipped from month to month (BS1's path: 3.9T and 3.5T against a 5.0T gap after the measured month), and
`BuildScreen.shortAnswers` draws the shortfall's red lines and the cap's chips only while an offer is short, so
that month made 19 of 93 nodes (79.6%).

**Change.** `KeptBench.fundingRunShort(FUNDING_SHORT_BY)` takes a run whose gap is at least `FUNDING_SHORT_BY`
(4) times what each offer's bids reach at the page's own cap (`fundingCap()`), trying the first template and then
the rest at quantities 1 to 10^8. The first draw is checked for every line `BuildScreen.shortWords` gives it
(`KeptBench.fundingShortWords`) and for the cap chips' title (`KeptBench.FUNDING_CAP_TITLE`), so the first
shortfall controls are caused and not met. The reshaped loop is hard again.

**The usual page as well (REV-FB6-1).** Before, the city's path picked either the short page or the covered one;
now the short one is caused, so the covered one is caused beside it. `KeptBench.fundingRunCovered` takes the
first run with a gap whose two offers, quoted at the page's cap, would each still lend `FUNDING_SHORT_BY` times
that gap. Its first draw is checked to have no shortfall line and no cap chips.

## 2. KS2 - sections 51, 66 and 67

**Cause.** RB1's path ([RB1 notes §7](RB1-notes.md)): 51's business stood still at its printed tenth for twelve
months, 66 found no debt to close, and 67 no book row. On this branch before the fix, the city's only paper fell
due at month 2323 inside 66. `boxHeld` then threw a NullPointerException on the FX thread, which kept the JVM
alive. Now it says it as a failure. 67's book row was OBSERVED, not reached.

**51.** `businessMoving` swings the plain business's property tax every month, through `Game.setZoneTax` as the
player's zone row does. The swing is `SWING_STEPS` (3) of the steps its net income is printed in
(`printedStep`, Money.tightMoney's units), up where `MAX_PROPERTY_TAX` allows and down otherwise. The offset is
put back after. A first version swung to the far end of the tax: on this city's Construction that was a bill of
$7.3B and $48.4B a month in turn, and it was sized down before any finding was taken from it.

**66, 67.** `paperLasting` puts a long-dated bond on the city's books (three times the minimum issue, 1,200
months, 4%) when the longest piece falls due within `PAPER_LASTS` (60) months. Both sections' "found one"
checks are hard again.

## 3. KF63 - section 63

**Cause.** On this branch before the fix, two of 63's runs were red.
- After "every row that folds opened on Portfolio", 81.5% was handed back (511 made, 2,244 handed back). The
  rule's buys of new bonds were new rows in the open issuers' folds.
- After "B:3015: sell by amount at fair", 46.0% (129 made, 110 handed back). The free bond was repaid at par
  while its page was watched.

PO1's case on main `df08523f` (B:3713's buy review, 88.7%) is the first again: the rule bought the free bond
during its review, and the page grew a holding's book fields.

**Change.** `fundHeld` holds the rule still for 63's runs and puts it back after.
- A withdrawal one step over the default (`TreasuryFund.sellsToPay`) buys nothing.
- The fund's cash is kept over `FUND_HELD_DUES` (3) months' transfer (`TreasuryFund.dueAt`), topped up to twice
  that through `Game.fundPayIn` after any month that leaves it under, so the withdrawal sells nothing.
- A fixture line checks that no lot appears that the fund did not hold before.

The held bond and the free bond are the ones that run longest (`lastingBond`). Each is checked to run past the
month after its own last choice.

- **The held bond's two pages come first, in 63's first run.** They are picked from the lots the fund holds as
  the hold begins. While the rule is held, the fund's bonds only fall due. In run 4 every bond it held had gone
  by the security pages (33 lots, then 15; B:3032 at 0 months, picked from a list read before 61).
- **A hand buy was tried first and dropped.** Run 5 bid the minimum issue at 10% over value for six months, and
  nothing filled.
- **The rest of the security pages read the fund's lots and reach as they stand when those pages begin.**

**A holding that settles (REV-FB6-1, PO1-notes: "a newly settled holding exposing book fields must receive
deliberate coverage").** After the security pages, a last run lets the rule go for one month (`fundRule`, the
default withdrawal), on Portfolio with every row that folds open, so its buys settle holdings the fund did not
hold. That month is an acting choice. The months after it are counted, with the rule held again:
- Portfolio with the rows the month settled;
- a settled lot's page (a bond when one settled);
- its ticket at its buy review and at its sell review.

Two fixture lines check that the month settled holdings and that nothing else came after it.

**LX1's three (lines 48 to 50).**
- `searchPagesLong` sells small bonds (the minimum issue, the market's highest coupon) of the market's largest
  issuer through `BondMarket.issue`, with the proceeds handed to the issuer's till as a bond's are. It sells
  until Search lists more than 2 x `SEARCH_ROWS`; LX1's city had 61 against 80.
- `olderYears` writes a pay-in line of a thousand in the month before Activity's window and one a year before
  that, as `fundYearNet` does, when the record has fewer than two older years.

Neither fired on this branch's path: the market already listed 131 against 80, and the record had 9 older years.
So their code has not run here. They are written to the model's own calls, `BondMarket.issue` and
`FundLedger.flow`, and to the window `FundView.activityRecord` reads, and the fixture lines after them stay hard.
A path like LX1's is where they act.

## 4. KS1-Saved - section 87's saved-city read

The trace prints, beside the unchanged check:
- whether the worker's answer landed, and the screen's read state;
- what the snapshot left off and why (`SavedCities.left()`);
- slot 10's save and history on disk;
- the folder read again off the screen, and how long that took;
- the screen's worker thread: its state, priority, CPU time and stack;
- the machine's CPU over the wait;
- when the read had not landed, whether it landed by the section's end.

**What it found.** The save succeeds, the slot's files are whole, and SavedCities reads them. The same folder,
read again on the harness's thread, gives the saved city with nothing left off. What fails is the tab's worker's
read (`RankingsScreen.WORKER`, one daemon thread at `Thread.MIN_PRIORITY`): it stalls in a native file read
under load.

| run | load | the worker's read | the worker's state | the same read on the harness's thread |
|---|---|---|---|---|
| run 4 | the 80-run ensemble's ten JVMs and another session's suite | not landed at 60.1 s, nor the tab-entry read before it, minutes earlier | RUNNABLE in `FileDispatcherImpl.read0` under `FileChannelImpl.read`, 0.0 s of CPU | 0.7 s |
| run 5 | light | landed after 16.8 s | 0.3 s of CPU | 0.2 s |
| run 6 | the machine 47% busy | landed after 8.8 s | 0.2 s of CPU | 0.7 s |

What the trace does not show, as REV-FB6-1 read it:
- **Not starvation.** A thread runnable for minutes with no CPU is blocked in its read, not starved (Windows
  boosts a ready thread starved for about 4 s). And in run 6, with cores idle, priority cannot explain 8.8 s.
- **Not which file.** Run 4's trace printed only the JDK's top six frames. SavedCities reads the slot's save (the
  header) and its history whole, through `Files.readAllBytes`. The trace now prints down to the game's frames, so
  the next stall names which file.
- **Not the thread against the files.** The control read runs after the worker's, on files the worker had already
  opened (a 7 MB save and a 10 MB history, just written). So it does not separate the thread from the files'
  first read.

It goes on as **KRW1**: "the worker's read stalls in a native read under load", with `NORM_PRIORITY` one
candidate, not the cause found. The fix lies in the screen or the save, not the fixture, and widening the minute
is ruled out. The known-reds line 15 entry goes with it as a `## known-reds` row.

## 5. KHT1 - section 89

**Cause.** BS1's path: Household money's "all of them" cell for a couple with no children, senior professional,
stood at half a household one month and under it the next. A cell under half a household draws its dash and no
tooltip, so a kept Label lost its tip to the month and not to the page.

**Change.** `tipsAcrossMonth` holds the family model across its month. The model's save array and owner adults
are taken before the month and restored after it (`PopulationCohorts.saveBands`, `FamilyModel.saveShapes`).
`restore()` acts as a load does: the next month's rebuild starts from what it carries (FamilyModel.java:506 and
2198: the adults at home and with them), so the city goes on from the held families.

The fixture line reads the grid's own cells (`KeptBench.householdCells`: `statementFor(...).households()` of half
a household or more, and the rows below the rule). It checks that every cell the grid opens before the month it
opens after, and prints how many the month alone would have closed. The first version compared `families.get()`
after `restore()` with the array it was restored from, which reads 0 by construction (REV-FB6-1). The none-lost,
same-Tooltip and per-screen carry checks are hard again.

## 6. The other entries filed under FB6

- **Line 33, 64's Columns on Borrow (7 of 10).** "THE LADDER THIS WOULD BUILD" is drawn only while the quote is
  not empty, and at the tail's cap a month's bids could all stand above it. The spot now quotes at the fill cap
  (`financesCap(true, 0)`), reset at the end of section 64.
- **Line 35, 55's Bank Profit page (256 of 299), filed back as KBP1.** It is red only in R6b's dry run on main
  `ec481b4a`. There the page's first draw was 299 nodes and one month made 93: a block of it dropped and was
  redrawn. Run alone, main keeps it. So do FB6's three full runs, where the page draws 192 nodes and each month
  hands back 193. FB6 could not cause the red, so the block is not named, and holding it still is a trace first,
  not a fixture.

  The ship file's `## queued` row and its `## known-reds` row carry it. "(256 of 299)" can never recur with a
  first draw of 192 (REV-FB6-1), and without its counts the line was every page's. So pageKept's hands-back line
  now names its page. The row's part, "the Bank's Profit page: each month hands back nine in ten of the nodes the
  first draw made, or more", covers 55's line alone. No other record uses that line (`gate.txt` none,
  `known-reds.txt` only this entry).

## 7. The checks

**The runs.**
- Run 4: KeptCheck direct on `3aa0c92e`.
- Run 5: KeptCheck direct on `0a2bb6a4`.
- Run 6: `./batch-check.sh KeptCheck` on `20a5b55e`, exit 0 ("suite OK - 1 harnesses"; Stale 0 firm).

Runs 4 and 5 were direct because the suite's 1,740 s limit ended run 1 under the ensemble's load. Run 5's one
red was the hand buy that run 6 replaced. Every KeptCheck section is green in run 6, 78 included.

**The pick.** `sh tools/pick.sh origin/main HEAD` picks 9 of 218: ChartFitCheck, KeptCheck, MapCheck,
MoneyWordsCheck, ResearchViewCheck, SaveFileCheck, ShortSellingCheck, TimingCheck, ToastCheck. Only KeptCheck was
run before the dry run, as the brief says.

| row | red before | green after (run 6 unless named) |
|---|---|---|
| KBF1, 44 | BS1's path: 79.6%, 74 of 93 kept (BS1 notes) | 100000 x Crude Pipeline, short by 601.4B, bids 47.7B and 42.8B: both short four times over; the first draw's 4 lines and the cap's chips; 93 of 93 handed back (run 4) |
| KS2, 51 | RB1 §7: "no month in twelve moved the figures", the count-up line FAIL | Construction swung 1.500% to 1.562% (3 steps of $100.0M), every rung OK, the guards out |
| KS2, 66 | this branch before the fix: the only paper fell due at month 2323, "the box is not on the page a month later" FAIL | paper due month 3581, 60 months more; the boxes OK |
| KS2, 67 | RB1 §7 and this branch: "a bookRow was found..." FAIL / OBSERVED | the same paper; "a bookRow was found on a money page of this city" OK |
| KF63, line 16 | this branch: 81.5% after "every row that folds opened on Portfolio", and 46.0% after "B:3015: sell by amount at fair"; main/PO1: 88.7% | held bond B:3094 runs 22 months, past its 2; free bond B:3213 runs 119, past its 28; the rule held still (33 lots, then 15, none new); every run over nine in ten |
| lines 48 to 50 | LX1's played city: 61 securities against 80; one older year | 131 securities against 80; Show more reaches page 3; 9 older years |
| line 33, 64 | FB2: 7 of 10 | 12 of 12 |
| KHT1, 89 | BS1's path: a kept Label lost its tip to an emptied cell | 0 emptied on every page; none lost; the same Tooltip; every screen carries tips |
| KS1-Saved, line 15 | run 1 and run 4: not landed after 60 s | runs 5 and 6 landed (16.8 s and 8.8 s, the machine 47% busy in run 6); filed back as KRW1 (§4) |
| line 35, 55 | R6b's dry run only (256 of 299) | 193 of 192 in runs 4 to 6; filed back as KBP1 (§6) |

## 8. The dry run

RUN-DRY

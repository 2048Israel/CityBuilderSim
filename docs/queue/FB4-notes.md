# FB4 - three gate reviews' follow-ups (2026-11-17)

The brief is [FB4](FB4.md): GV1-R, WG2-R and RB1-R, bundled by Jerus on 2026-11-16 ("Yes, bundle them"), each row's
brief standing. Cut from main at 0c43757b (0.9.136). Opus wrote the briefs, reviewed and committed; one Sonnet slice
(xhigh) per row did the edits.

## 1. GV1-R (cac8009e)

- **The band's notice churned at a bound.** `Inbox.takeWealth` posted a notice whenever `GovernmentView.band()`
  changed, so wealth sitting on a bound posted one nearly every month.
  - The fix: a band is now left only past a margin, `GovernmentView.BAND_MARGIN` (.1, est.).
  - `bandFrom(held, years)` keeps the band the live notice names while the wealth is within that band's range widened
    by a tenth of each finite bound. Past it, the band is the bare `bandFor(years)`.
  - The held band is read from the live notice's key, so nothing new is saved.
  - While held, the notice's body names both bands: "...%s by the bands. Its notice stays at %s until the wealth is
    10% past that band's bound."
- **The debt left out two debts.** `GovernmentView`'s debt now adds the central bank's advances to the treasury
  (`getAdvancesToTreasury()`) and the linked bonds' indexation (`getIndexationOwed()`) to the paper's principal. The
  Government screen's wealth card says so.
- **The buyback advice re-quoted on every redraw.** The quote is kept per Game against a stamp of everything it reads:
  the month, cash, the money owed, the pieces by identity and their principals, and the market's state. Any change in
  them quotes afresh. `buybackQuotes()` counts the passes, for the harness.
- **Checks.**
  - **InboxCheck `wealthBandHolds`:** 120 readings either side of two years inside the margin, a crossing past it,
    120 more, and a crossing back. With `takeWealth` put back on `band()` (by hand), the old rule raised 241 notices
    over 243 readings, and 5 lines FAIL:
    - inside the margin, no notice;
    - the held body names both bands;
    - past the margin, exactly one (rich);
    - 120 more, none;
    - back past it, exactly one (sound).
  - **InboxCheck, the fix:** green, 3 notices (the first reading and the two crossings).
  - **GovernmentViewCheck:**
    - section 1: `bandFrom` at each bound and margin;
    - section 2: an advance of 2,000 and a linker indexed to 1.2, the bridge line subtracting all three debts;
    - section 4: the cache (a second view quotes nothing; cash, a piece added or removed, the market and the month
      each quote afresh).

    Red before (by hand, on a mutated copy compiled apart):
    - with the debt on the paper's principal alone, 3 lines FAIL (the debt, "more than the paper at face alone", the
      full bridge);
    - with the kept quote never read, 9 of the cache's lines FAIL.

    Green with the fix.

## 2. WG2-R (d3a683d2)

- **Item 1, the reload at 239 and 240 (should add).** SaveFileCheck `brakeThresholdCrossesASave`:
  - **The fixture:** a town of 300 Houses and 6 Convenience Stores, played 40 months (61% out of work, so the brake
    is 0 from month 240). Its months recorded are set to n-1 through `restore()`, then one month is played.
  - **The check:** save slot 1 of a scratch root, load a twin, and compare to the bit `brakeApplies()`,
    `getLastJobsPull()`, the brake applied, the arrivals and the months recorded; then the same one month on.
  - **Red before (by hand, on the load path in Game, since a mutated `restore()` moves the fixture's own call too):**
    - a load reading months recorded +1: at 239 `brakeApplies()` FAILs, with the months line;
    - a load reading -1: at 240 `brakeApplies()` FAILs, and one month on from 239 the brake (1 against 0), the
      arrivals and `brakeApplies()` FAIL.
  - Without a mutation: green.
- **Item 2, the word reads the brake applied.**
  - `Migration` keeps the brake its month applied (`getLastBrake()`, 1 when the brake does not act).
  - It is saved in a tail slot of the migration array, after the licences. An array from before it loads all else,
    the brake read as 1 (a SaveFileCheck line). Any other length is refused whole, as before. No format bump.
  - The People screen's "slowing / stalled" word reads it, in the past tense, with today's share beside it.
  - PopulationCheck asserts the brake against the month's arrivals in three lines.
  - **Green at 6%? No.** It stays to 7%, the verdict Jerus took in call C2(a) (spec-wages: "green 3-7%, amber to 12%,
    red past it"). `OUT_OF_WORK_HIGH`'s javadoc and the tip say that arrivals already slow from 6%.
- **Item 3, RT3 with the brake off.** Already done: RT3's notes ("WG2's path") ran KeptCheck's city from m2391 with
  `arrivalBrakeNow()` disabled. Posts at m2416 were 24,076, against 24,074 with it on, so the brake is not the fall.
  Nothing rerun.
- **Item 4, the drift's units.** Fixed in Migration's comments (the class javadoc, the banner's part 3 and the line
  above `over`):
  - the excess leaves at `SURPLUS_DEPARTURE_RATE` a month, counted in people;
  - `cohorts.migrate` takes them from every age band, so the jobless excess falls by about that rate times the adult
    share.

  The rule is unchanged.
- **Item 5, HealthCheck and the picked months.**
  - HealthCheck 9's restated claim was allowed by C2(a), and WG2's notes ("HealthCheck 9 restated") record it. Only
    WG2's ship file said no claim moved. That is a record's slip; nothing to change in the harness.
  - The two picked months are filed back as **PK1** (section 5).

## 3. RB1-R (d444884b)

- **Item 1, the idle cash after last month's toRaise.**
  - `Game.fundIdleCash()` now reads the fund's cash less `max(0, toRaise)`, the cash `payTransfer` pays out as late
    before it strikes the due.
  - The preview reads the same through `fundIdleCash()`.
  - Under 17 steps the old read charged the late money again, at a 24th, as idle.
- **Item 2, the idle figures count what was paid.** `transferFromIdle` and `transfersFromIdle` count
  min(idle part, paid).
- **Items 4 and 5.**
  - `settleTreasury`'s comment no longer says "and its mix".
  - LongPlaytest's buyback line says "filled at the desk's bid".
  - `Exchange.getFundLimitAsked()` (a month's figure, not saved) is new, for the count below.
- **Checks.**
  - **FundCheck 9d:**
    - a fund at 16 steps carrying a toRaise, then paid in past its rooms;
    - the idle cash equal to the bit to `idleCash(cash - toRaise, ...)` and less than on the whole cash;
    - the preview's due, the month's due and `dueAt` agree, with `fromCash` = min(due, cash - toRaise);
    - the month's due within the bond room's move at the month's top. A fixture line holds that room under the
      toRaise, so the old read still fails.

    Red before (by hand, `fund.getCash()` put back): 5 lines FAIL (the idle to the bit, the preview's due and
    `fromCash`, the month's due and its idle part).
  - **FundCheck 9e, item 3's real run at the default dial:**
    - A 24-month probe picks the company that buys back the most per share (Construction). The fund is placed at
      exactly `OWNERSHIP_LIMIT` of it, a holding written as a fixture; the town's fund on its own reached no cap in
      600 months, the Bank never buying back.
    - The fund is then paid in past its rooms and played 240 months.
    - The cap sale fired in 230 months and the idle transfer in 90; Construction bought back in 231.
    - The most the fund held was 10.30%, against the fixed point L s / (s - b) = 10.92% at the fastest buyback the
      pace allows.

## 3b. The review's two notes (REV-FB4-1, SHIP)

- **The advice's "There is no paper to buy back."** It was gated on `debt() <= 0`. Since GV1-R that debt counts the
  advances and the indexation, so a city with advances and no paper was told it needs cash above its debt.
  - The view now has `paper()`, the paper at face alone, and the Government screen gates the line on it.
  - New GovernmentViewCheck line: `paper()` is the principal while the debt is more. Red before (by hand, the paper
    read as the debt): FAIL.
- **PopulationCheck's "a brake of 1" read a fresh Migration**, whose field starts at 1, so it could not fail if the
  7-argument month's reset went.
  - The line now reads a city that braked past the stop (a fixture line: its brake below 1) and then played a month
    with no people read.
  - Red before (by hand, the reset at `Migration` "lastBrake = 1" removed): FAIL, 0 against 1.

`./batch-check.sh PopulationCheck GovernmentViewCheck`: exit 3, on main's MR1 alone.

## 4. The trace moved: GV1-R's debt, through the player

Main's 0c43757b and this branch's cac8009e (8 files, 2,400 months):
- the same to month 217;
- every file differs from month 218.

**Read from the trace:**
- Advances of $1.12bn are on the books at the January of m217.
- `GovernmentPlayer` reads `wealthYears()` each January. Main's player found the wealth past two years and cut the
  taxes; the branch's, counting the advances as debt, did not.
- From m218 the branch has $4.2M more tax income.
- RB1-R cannot move this run: at the most of the dial a 24th is less than the dial, so the idle cash does not enter
  the due.

The brief expected only RB1-R to move the playtest.

**The branch's report:**
- "All checks passed.", exit 1, on one finding: "industry stands farther out than the homes", once, at m2400.
- That finding is recorded: `known-reds.txt` line 8, LongPlaytest, filed to FB5 (in flight). The path is a new one,
  and main's run had no finding.
- The fund's line: 0 company-months over the cap, as on main. The most of any company is 2.3% (main 0.0%), since the
  player lowers the dial a step in a poor January.

The ensemble's rules apply, so the mends mail asks for one ("FB4 ensemble?").

## 5. Filed back

- **PK1**, ScaleCheck's and FundCheck's picked months ([PK1](PK1.md)): fixtures to cause their conditions at any
  month. That is a redesign of two fixtures, larger than a follow-up, and neither is red.
- **RB2**, the withdrawal's due struck on a value that counts the toRaise ([RB2](RB2.md)): found here, the same order
  of reads as item 1 but on the value. It would move the playtest, so it gets a batch and an ensemble of its own.
  Recommended: strike on value less toRaise.

## 6. The checks and the dry run

- **`./batch-check.sh`** with FundCheck, PolicyPreviewCheck, ExchangeCheck, SaveFileCheck, PopulationCheck,
  ReadPathCheck, HealthCheck, InboxCheck, GovernmentViewCheck, GovernmentPlayerCheck, ScaleCheck and HistoryCheck
  (cac8009e): exit 3.
  - The one red is PopulationCheck "...but it does fill, given a decade": main's MR1 (`known-reds.txt` line 53,
    `gate.txt` line 42).
  - ScaleCheck observed one path assertion not reached: its town's rents at m225, "the affordability pull 1 in all
    6". That is main's too: main's classes at 0c43757b give the same town (9,285 people) and the same line.
- **`tools/pick.sh origin/main HEAD`:**
  - pick: ALL, 217 harnesses (Game.java);
  - review needed: money (TreasuryFund) and threading (GovernmentView's kept quote, a synchronized weak map and an
    atomic counter).

  The full pick was not run.

# OP1 - every policy open from the first month: notes

Brief: `docs/queue/OP1.md` (Jerus, 2026-11-19: "everything policy related is available right away").
Branch `batch/OP1`, cut from origin/main 24b7bfcbc (0.9.160).

## Before the code: does the autopilot set a lever once it is open?

Two players set policy; neither is new.

- **The rule (DebtManager's autopilot)** holds the policy rate and nothing else: `Game.java:13685-13698`, the rate branch
  (a peg's tie, gold's, the band's currency rate, then `advisedPolicyRate(inflation)` with the autopilot on). A city founded
  through the founding screen gets the rule at `Game.java:869` (`newGame`: `debtManager.setAutopilot(true)`). From R0f to
  OP1 a 1900 city's rule waited for March 1935; now it has the dial from month 1, as a default city's always did. A
  bare city (`new Game(files, founding); run()`, the harnesses' and the playtest's fixtures) founds with the hand on it.
- **GovernmentPlayer** (`GovernmentPlayer.java:42-72`) is the automated player of the playtest, the ensemble and WealthRun
  only: `LongPlaytest.runPlayer()` (`LongPlaytest.java:5838-5840`) runs it after each month (`:5864`). Game never calls
  it, so a live city has no such player. It acts in each completed January from month 13 (`:44`, `month <= 1` and
  `monthOfYear != 1` are refused). Rich (wealth years above `GovernmentView.RICH_MIN`, `:55-60`): profit tax -5 points,
  sales tax -2, **pension replacement +5 (`:58`)**, **EI benefit +5 (`:59`)**, the fund's withdrawal to its most. Poor
  (below `SOUND_MIN`, `:61-67`): the taxes up to their ceilings, the pension and EI trimmed but never below a floor
  (`:65-66`, `min(pension, max(POOR_PENSION_MIN, pension - step))`), so a promise still at the founding's zero stays zero.
- **The pension levers, for BO1-S.** Only GovernmentPlayer moves them: the replacement (`:58`, `:65`) and the EI benefit
  (`:59`, `:66`), never the contribution (no line sets `setContributionRate` outside fixtures; `LongPlaytest.java:9620`
  is one). Every promise opens at zero at the founding (`TaxPolicy.openAtFounding()`). Before OP1 their setters refused
  until the old-age pension (1927) and EI (August 1940) opened, so the playtest's pension could first move in 1927.
  Since OP1 it moves from **January 1901 (month 13)**, in a rich January only. A live city's pension moves only when
  the player sets it.
- **So the playtest moves.** The taxes now open at 15% in January 1900, not on their dates (1916, 1917, 1920); the founding
  builders' rate is 15% (`Founding.foundingBuildersRate()`); and GovernmentPlayer's first January is 1901. The 80-run
  ensemble is owed (`docs/contract.md` 4): see the dry-run section below.

## The gates opened

1. **The twelve dated levers of `PolicyDates.Lever`, Money's two among them**, open at founding in every city, by
   `TaxPolicy.openAtFounding()` (founding: `Game` calls it, then `policyHearsTheMonth()`). The profit, wage and sales
   taxes open at `DEFAULT_INCOME_TAX` (15%) with no offsets (`PolicyDates.opensAtFoundingRate()`, R0 call 16). The
   pension replacement, the pension contribution, EI (premium and benefit), the health premium and student aid (the grant
   and the loan rate) open at zero. The minimum wage binds from the founding (`LabourMarket.foundOn()`'s default). Zoning
   starts free. The policy rate and the Bank of Canada are open in a 1900 city too.
2. **The setters' refusals.** `TaxPolicy.setMonth()` held a shut lever at zero and every setter refused through
   `closed()`. Both are gone, with `isOpen()`, `openAll()`, `openNow()`, `bringForward()` and the saved openings.
3. **Money in a 1900 city** (R0f): the guards in `DebtManager` (aim, spread, target, strictness, ceiling, `takeTheDial`),
   `CentralBank` (the QE share and its dials) and `ForeignAccounts` (the regime, the mint, the peg, its anchor, the band and
   its edge, the reserve mix, the gold events), and `Game.isLeverOpen()` / `leverOpeningMonth()` / `setLeverOpenings()`.
4. **The minimum wage and zoning setters** (`Game.setMinimumWage`, `Game.setZone`).
5. **Currency reform and buying or selling foreign currency** (Policy's reform page; Trade's exchange card returned early
   while the Bank was shut).
6. **Student loans**: `HouseholdBalance.studentLoansOpen` and its setter are gone, so the treasury lends from the founding.
7. **The screens**: Policy (every shut card, `isShut`, the greyed ladders, the central bank's shut branches in the rate
   page, the dial card, the currency page and the reform page, the tie card's shut state, student aid's disabled box),
   Zoning (the shut flags, the tax lever's shut cards and disabled rows) and Trade (the exchange card). No screen names
   `PolicyDates` now.
8. **The inbox**: the "a lever opens" notices (`lever:` keys) are gone.
9. **Not found**: the founding screen states no dates; neither does the manual (nothing to publish).

## What I built

- `PolicyDates` is history now: the table (`openingMonth` renamed `canadaMonth`, the date Canada passed it), the sources,
  the gold dates and `goldPeriod(month)`, and `opensAtFoundingRate()`. `MAX_EARLY_MONTHS` is gone.
- `TaxPolicy.openAtFounding()`; `reset()` puts the defaults back (the grant at `defaultFixedGrant()`); `foundOn()` no
  longer sets the grant.
- **Gold stays history's (★2a).** `ForeignAccounts.hearMoneyMonth()` acts only when an episode begins or ends: the
  founding is on gold at par to the US$; August 1914 and September 1931 float a dollar still on it; July 1926 puts a
  floating dollar (Float or Lean) back at par. A player who changes the regime, re-pegs or moves the anchor leaves gold for
  the episode (`leaveCanadaGold()`), and the return passes a band, a peg or a Gold of their own by. A load replays nothing.
- **The tie.** Canada's gold is a peg to the US$, so Game's rate branch ties it as any peg (`tiedRate(gap)`): the dial is
  the US rate plus the spread, whatever a hand sets.
- Law finds open nothing (★1a): `Game.workTheResearch` calls no `bringForward`, and the 13 "a Policy lever" rows of
  `research.json` end "; the find opens nothing (every lever is open from the founding)".
- `Inbox` without the lever notices; `DataSave` without `leverOpenings` (Gson ignores the old key: no format bump).
- **PolicyOpenCheck** (new, replaces PolicyDatesCheck, on the RestaurantsCheck pattern), 71 checks in 7 sections:
  1. The record against PolicyDates.
  2. The founding of a 1900 city and a default city: the taxes at 15%, the promises at zero, the floor binding, the rule
     taking the dial, the builders' rate.
  3. Every setter taking a setting in month 1, and keeping it a month on.
  4. Money in both cities.
  5. A law find (PL3, found in a 1900 village through a save) moving no dial, with no "lever:" notice.
  6. A source scan of the screens and views for `PolicyDates`, `isLeverOpen` and `leverOpeningMonth`.
  7. Reflection: no gate member declared on the eight classes that had one.
  Built to fail if the dates come back, in sections 2 to 7 (not run against a reverted tree).
- **Restated harnesses**:
  - MoneyDatesCheck: the gold episodes with Money the player's from month 1, a hand's rate on gold and the tie, the
    player leaving gold, the Bank's date passing as data.
  - DiscoveryCheck 16 (a law find opens no lever: the catalogue half) and 20 (its floor premise a fixture's market with
    no floor).
  - NewGameCheck 12.
  - GovernmentPlayerCheck (`openFromFounding`: the first move in month 13).
  - ResearchCheck, KeptCheck, ExchangeRateCheck, WageLadderCheck and ZoningViewCheck.
  - 60 `openAll`/`openNow` calls removed from 44 fixtures (a slice), keeping `reset()`.
- Comments and the tree's entries restated (`docs/tree.md`, Game, LabourMarket, StudentHousehold, Founding, Research,
  ResearchCatalog, GovernmentPlayer, LongPlaytest). The maps are regenerated.

## The checks

- `./batch-check.sh --fast`: compile OK, Stale 0 firm, tree clean.
- Green alone (exit 0): PolicyOpenCheck (71 PASS), MoneyDatesCheck, DiscoveryCheck, NewGameCheck, GovernmentPlayerCheck,
  ResearchCheck, WageLadderCheck, ZoningViewCheck, ReadPathCheck, ExchangeRateCheck, CurrencyRegimeCheck.
- KeptCheck (1,070 s): exit 3 with the ship file's six rows. Its played city (`LongPlaytest.probeCity`/`probePlay` to
  month 603) is on OP1's moved path: main's construction-page red (section 40, 499 of 559) is gone, and five premises of
  sections 60-72 no longer hold:
  - 60, KP5: the fund holds 0 lots after the fixture's pay-in and orders;
  - 62 and 63 follow from it: no bond to watch, no Portfolio row that folds (two lines);
  - 69: the Government Overview made 39 of its 373 nodes in one month (a tenth is asked);
  - 72: Trade's reserves page was quiet in 11 of 25 months (most are asked).
  This is KFP1's class: the city's minimum wage is pinned at the going rate for 68's Government Overview, "46 of 395
  nodes", on this same path (KeptCheck's main, WG1-D/WG1-R). The fund's pages and the Government Overview draw no lever's
  state. The reserves page holds Trade's exchange card, whose shut branch OP1 removed, but the played city is founded
  today's way (`LongPlaytest.FOUNDED_1900` unset), where the Bank was always open, so that branch never drew there. Each is a known-reds row naming **FS5** (FS1's sweep
  for KeptCheck from section 55, which takes KFP1 and KP5-N), not FS1 itself, which has shipped. The Policy and Zoning
  sections OP1 restated pass.
- **Section 60's empty fund is the path** (the orchestrator asked this be proved before the dry run). A probe (`target/op1probe/Kp5Probe.java` in the worktree, not committed)
  builds KeptCheck's city as KeptCheck does (`probeCity`, the floor at the going rate, one `probePlay` to 600), prints the
  fund, then runs KP5's own fixture. On main (24b7bfcbc, a detached worktree, since removed) the city reaches month 605
  with 5,591 people. Its fund holds 52 positions: 1 rescue, 14 shares and 37 bonds, bought from the rescue's income with
  nothing paid in. So `held` is not empty and KP5's fixture never runs on main. On OP1 the city reaches month 603 with
  6,876 people (KeptCheck's figure). There was no rescue, and in 603 months the fund was never paid in and never bought,
  so its positions are 0 and the fixture runs. Bought there at once, the fixture's orders fill: 6 positions three months
  on. In KeptCheck they settle nothing by section 60, after sections 1-59's months and choices. That is main's own KFP1
  class: on main, section 63's hand buys "settle no new holding" (known-reds, FS5). OP1 changes nothing in the fund or
  its orders: no line of `TreasuryFund`, `FundView` or `Game`'s fund methods is in the diff.
- SaveFileCheck: exit 3, main's known red only ("over the default it owes what it sells for, and paid some late", FS17,
  in `docs/notes/known-reds.txt` since HS1).
- `sh tools/pick.sh origin/main HEAD`: ALL 228 harnesses (AllChecks changed); review needed - money: CentralBank.java,
  DebtManager.java; saves: DataSave.java. The full suite is the dry run's.
- How the fixes went:
  - "The rule has the dial from the first month" failed in a bare city, whose hand is on the dial. The checks now hand the
    rule the dial and assert it took it.
  - The tie checks compare to the US rate held before the month's step. The dial is set before the world hands the
    month's US rate (`handTheUsRate()` after the rate branch, XR2's order), so the dial follows the US rate a month late.
    This is not new.

## The calls

- ★1 (a), as recommended: a law find keeps what it did (a discovery: its notice, the law prize's purse, the record) and
  opens nothing. Every one of the 13 law rows had the opening as its only effect: 10 keyed a lever (PL2-PL7, PL9, PL10,
  PL12, L5), and PL1, PL8 and PL11 never keyed one. **Proposal**, for Jerus with the tutorial: keep them as law
  milestones that pay the law prize (as shipped), or retire the 13 rows, or give each a new effect then (for example,
  the find as the tutorial's step that introduces its lever).
- ★2 (a), as recommended: the gold years and the equipment chip stay history's. The founding is on gold; the player may
  leave it whenever they like, and history then leaves the dollar alone for that episode.
- ★ Canada's gold is tied as a peg (the dial is the US rate plus the spread). With Money open on gold, a hand's rate would
  otherwise have run a gold standard at a rate of its own.
- ★ The lever table is kept as history (the dates, the sources, the law rows' months); only the gate went.
- ★ `LabourMarket.floorBinding` is kept as a fixture's capability (a market with no floor); every city's floor binds.
- ★ `reset()` stays in the fixtures (it is the TaxPolicy defaults, not a gate); a fixture with no `reset()` now founds
  with the taxes at 15%.
- ★ No `SAVE_FORMAT` bump: an old save's `leverOpenings` is an unknown key, ignored, and its levers read open.
- In a 1900 city month 1 keeps DebtManager's default rate (3%); the tie applies from month 2, when the world has
  handed a US rate.

## Left for Jerus, and other batches' files

- `Household.java` (HS2's and BO1-S's file, untouched) keeps `studentLoansOpen = true` and a javadoc saying loans open in
  1939; nothing sets it false now. Queued as OP2, for after those batches.
- **HS2's list**: I edited `HouseholdBalance.java` (12 lines removed: `studentLoansOpen`, its setter, getter and two
  copies) and `DataSave.java` (9 lines removed: `leverOpenings`). Both are deletions only; a merge conflict with HS2
  would be in those lines.
- The law finds' proposal above.

## The dry run

(to come)

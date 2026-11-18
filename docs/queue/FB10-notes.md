# FB10 — the research checks and the judge

Built 2026-11-18 by a Claude session on `batch/FB10`, cut from origin/main
873de9427 (0.9.143). Rows R2b-V, R2b-J and RT2-P (docs/queue/FB10.md).

## Read

CLAUDE.md, docs/batch.md, FB10's row and brief, R2b-notes (ResearchViewCheck
finding, process finding), RT2-notes (main-code proofs), known-reds.txt rows
filed under FB10 (ResearchViewCheck x3, PrizeCheck x3) and OE1's YoungBankCheck
rows; gate.txt's PrizeCheck and YoungBankCheck lines (read only).

## What main showed first

- **ResearchViewCheck is green on main** (all 218 ok, 00:30). R1d (842f42700,
  11-16, after R2b's evidence on b49a6023) had already replaced the line's
  `sectors.size() == 2 && mine + theirs == city` with "the rows' own sectors
  contain Industry and Agriculture". Today's research town builds a Machine
  Works (Manufacturing) and no Fabrication Shop: Industry 1, Manufacturing 2,
  Agriculture 1 (3 with the tractor) of 6 (target/checks/fb10-view-probe.log).
  The third sector was there by the planner's choice, never caused, and no
  law looked inside it.
- **PrizeCheck is red on main**: exit 2, the two RT2-P lines and
  "PrizeCheck: 2 FAILED of 284" (raised 1060.0378 of the pre-month 1044.8565;
  till at most 434 against a cushion of at least 0; target/fb10-prize0.txt).

## R2b-J: the judge reads a harness's own tally as a count

- `KnownReds.count()`: besides `exit N` and a bare `N FAILED`, a red line
  whose text is `<Name>: N FAILED`, with or without ` of TOTAL`, is a count
  when `<Name>` is that line's own harness. A tally headed by any other name
  is an assertion. Counts still follow only a harness whose every assertion
  is covered, and the incomplete-log and tag-agreement checks are untouched.
- ★ **The optional " of TOTAL", so YoungBankCheck is covered too.** It is the
  same class: YoungBankCheck, CreditsCheck, LuxuryRetailCheck, BrandingCheck,
  ChartFitCheck and ToastCheck print the tally with no total;
  ResearchViewCheck, PrizeCheck, ResearchCheck, ResearchPreviewCheck,
  DiscoveryCheck, DriverlessTruckCheck and PolicyDatesCheck print one. A rule
  taking only one form would leave exact derived rows to file for the other.
  Proven in KnownRedsCheck ("the tally without its total, as YoungBankCheck
  prints it, follows too"). YoungBankCheck's row 26 (OE1), "YoungBankCheck:
  1 FAILED", becomes inert: the judge never reads an entry for a count line.
  FB10 leaves it alone, as asked; OE1's ship drops it as its own.
- **Proof, KnownRedsCheck section 8** (11 assertions). The fixture child
  prints `KnownRedsFixture: 1 FAILED of 2` when asked to. Live path:
  `AllChecks.redLines`/`newCount`/`report` (covered, known-tagged, the
  no-total form, an uncovered second assertion keeping itself, the exit and
  the tally NEW, a foreign-named tally NEW, exact count rows covering
  nothing). Kept path: `batch-check.sh --judge` and `KnownReds.judge` on the
  tagged log of both fixtures (3 and 4); an untagged log (3); and a log the
  unmended runner had tagged `NEW red: 2 of 3`, rejudged (3).
- **Mend removed** (count() as on main, the fixture unchanged): 7 of the 11
  fail, on both paths (target/checks/FB10-KnownRedsCheck-unmended.log). The
  four that hold either way are the laws kept: the fixture's premise, the
  uncovered assertion NEW, the foreign-named tally NEW, fixture 2 judged 4.
- **On a real kept run**: R2b's retained gate
  (C:/Users/Jerus/AppData/Local/Temp/cbs-gate-R2b-WdCE5H6G; branch 7313196a,
  main b49a6023), its checks/suite.txt copied to target/checks/fb10-r2b-kept/
  and rejudged in source mode against gate.txt plus known-reds.txt without
  rows 19-20. Main's judge leaves `ResearchViewCheck | exit 1` and
  `ResearchViewCheck | ResearchViewCheck: 1 FAILED of 217` NEW; FB10's leaves
  no ResearchViewCheck line NEW. Both exit 4 on other harnesses that day's
  main had and today's records no longer hold; this judges only the one
  harness.
- **One in, one out**: `git diff --numstat origin/main` over the process
  files: KnownReds.java 7 added, 6 removed (count() in two lines, the
  runner's NEW tags summed in one); KnownRedsCheck.java 66 added, 13 removed
  (the new section is assertions; its judge-call block is folded into
  `quiet()`). No script changed.

## R2b-V: ResearchViewCheck 6 over every sector, the third caused

- ★ **On a second `researchTown()`** (2.6 s): a Fabrication Shop, known at
  the founding, built (cash for the test, no construction) in Manufacturing,
  with the tractor found as above. The shared town, sections 7 to 10 and
  section 10's guarantees read what they read before; building into the town
  would have changed what they read.
- The law, over every key of `Sectors.KEYS`: each sector's filter keeps only
  its own sector's templates; the sectors with rows are exactly the sectors
  that the city's standing, sector-owned stacks with a piece known this month
  stand in (counted from the city's stacks, not from the rows under test);
  the sectors' rows add up to the city's. Today: {Industry=1,
  Manufacturing=4, Agriculture=3} of 8, 3 sectors. A premise line asserts the
  shop was built, is Manufacturing's and has rows, beside the Bakery's
  Industry and the farms' Agriculture. "No Such Sector" stays on the town;
  the old two-sector mine/theirs lines are inside the every-sector law.
- **Mend removed** (the original law: two sectors, Industry + Agriculture =
  the city): the caused city fails exactly the recorded line, 1 FAILED of
  219 (target/checks/FB10-ResearchViewCheck-unmended.log). With it: 219 ok.
  ResearchView.java is unchanged.

## RT2-P section 12: the cap is the order's own quote

- A probe with a copy of Game printing orderLab's figures (target/fb10probe,
  not shipped; target/checks/fb10-prize-probe2.log). At the order (month 81)
  the cost was 612.8756 and the stake 447.1622, exactly the harness's
  pre-month stake (Research.month runs after the order). Need 1060.0378 =
  raised, to 3e-13. The cost was 597.6944 before the month and 611.6926
  after it. BS's books say spentOnBuildings 612.1163, what buildFor paid
  (quote and plot), not getCostOf. No end-of-month figure is the order's
  quote: it is a flow.
- ★ **`Game.LabQuote(month, cost, stake)`**: put by orderLab when it reckons,
  read by `getLabQuote(f)` only in that month; cleared at a new game and at a
  load; not saved, since only the month that made it reads it. PrizeCheck 12
  gains a premise line (the quote is the model's own, of the month played,
  and the twin with the prize a hair under has none) and caps the raise by
  quote.cost + quote.stake: 1060.0377948361418 of 1060.0377948361415. With the
  mend removed (main) it fails 1060.04 of 1044.86.
- Game code changed, so FB10 is versioned. The record is written and never
  read by the model, so **the playtest does not move**.

## RT2-P section 20: left the bundle as R3b-R

The orchestrator's call, 01:11 on cbs-bus, on what I mailed at 01:02.
PrizeCheck 20's third law ("its till never stood above the six months of
costs it keeps") was not the fixture's to cause. On 873de942
(target/checks/fb10-stake-probe*.log, fb10-labcost-probe.log,
fb10-office-probe.log):

- The lab ordered in m81 is still on site at m116: about 25 of its 1,431
  points a month, so about 57 months to stand. Business Services has no
  other premises. Its one other template, the Engineering Services Office,
  is refused NO_LICENCE in this village.
- Its operating cost is 0 in all 36 months, so the Exchange's own cushion is
  0 too. `Exchange.cushion()` is 6 x the closed month's operating cost and
  debt service, the harness's figure. Meanwhile the till holds the stake,
  434 falling to 284.
- A standing lab would not re-cause it. Built standing in the same village
  it costs 14.4 a month (six months is about 87), against a stake of 447.16
  (100 posts x 0.4083 x 10.95 months to the find).
- Only the regime (NEW m81 to m91, then BAD) keeps the Exchange off the
  stake: bought 0 and qualified 0 hold.

That is R3b's call 4 (R3b-notes) half reached: "a Business Services that
qualified and held a stake past its six months' cushion would be bought
back; a `reserved()` on `Exchange.Companies` is the mend if a later balance
makes Business Services a buyer". The stake now stands past the cushion in
every month.

### R3b-R, queued: the labs' stake reserved from the buyback

- `Exchange.Companies.reserved(c)`: what a company holds for finds not yet
  made, the labs' stake. The Exchange keeps it out of the buyback's spare
  beside the cushion (`spare[c]` from `money(c) - cushion(c)`, and the
  buyback's own use of `cushion(c)`). Game's Companies answers it from the
  labs on site and standing, as PrizeCheck 20 reckons the stake.
- PrizeCheck 20's third law then asserts that the buyback's spare left the
  stake (in no month was the till past the cushion and the reserve). Its
  active-lab and no-buyback laws stay. A fixture where Business Services
  qualifies (its regime and equity caused) shows the buyback leaving the
  stake.
- It moves the buyback only for a company that qualifies while holding a
  stake. R3b found Business Services never qualifying in a played 1900 city,
  so the trace probably does not move; that is to be shown.
- Until then the one assertion is a known red under R3b-R: FB10's ship
  transfers known-reds row 22 to it, with this cause. Its exit and its
  "PrizeCheck: 1 FAILED of 285" follow it under R2b-J's rule, with no rows of
  their own.

## The known reds

FB10's six rows: ResearchViewCheck's three (its line passes; the exit and
tally have nothing to follow) and PrizeCheck's row 21 (section 12 passes)
and row 23 (a derived tally, a count under R2b-J) are FB10's own, vetoed at
its gate and dropped by the publisher. Row 22 moves to R3b-R through the ship
file's `## known-reds`. No other row is touched. YoungBankCheck's row 26 is
OE1's and becomes inert (above).

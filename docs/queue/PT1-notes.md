# PT1 - eleven fixes from a played city (2026-10-23)

The brief is `docs/queue/PT1.md`, its `## PT1` section, written 2026-10-11 from
a played city. Each item was checked against the code of 0.8.29 first. Coding
went to two Sonnet subagents (the mechanisms, then the looks), reviewed here.
The model's month is untouched: two log lines changed, nothing it computes.

## The items

| # | item | outcome | the check |
|---|---|---|---|
| 1 | Founding: the Standard card read "Stan..." and its chip "de..." | **fixed**: the title has the row, never ellipsized; the "default" chip has its own line under it (`FoundingScreen.presetCard()`) | by eye: `PT1/founding-cards-520-before.png`, `-after.png`, at the Earth column's 520 px |
| 2 | Month 1: "THE BANK - there is none" while one stands | **fixed**: `Game.getBankBranchesStanding()`; a standing bank no month has opened reads "opens with the first month" and is not counted in NEEDS YOU (`CityNeeds`); the Bank page's card says so, neutral, with no build button, and its banner reads NOT OPEN YET; the window's two lines likewise. A city with no bank keeps today's words | `PlaytestFixCheck` 1: a founded scratch city before its first month (premise: one standing, no branch) is not counted; the same city with its bank retired is; after a month it is open |
| 3 | "Build a Commercial Bank" landed at the top of Build > Shops | **fixed**: `BuildScreen.openOnBuilding(category, name)` opens Shops and scrolls the bank's card to the top after layout; both Bank doors use it | by eye: `PT1/build-shops-bank-landed.png` (headless layout; the real window's settle is ordered before it by reading, not run) |
| 4 | "power - 13333% served" in a town of 40 | **fixed**: `CityNeeds.SERVED_AS_MULTIPLE` = 5; from five times what is asked `servedPct` prints "30×", so every gauge, row and the advice change together | `PlaytestFixCheck` 2: just under the dial a per cent, at it and at 4,000 against 30 a multiple, from the constant |
| 5 | The city's money written "$" and "D$" | **meant, unchanged**: Jerus's convention in `Currency.java` - "$" where it is the only money in sight, "D$" where a US$ figure shares the page. Founding, the Land office and Finances each show US$ figures beside the city's. A call for Jerus if he wants one sign everywhere | - |
| 6 | City History: the population axis went to -100 / -200 | **fixed**: `ChartModel.niceScale` never floors a series whose low is 0 or more under 0, asked from zero or not | `ChartCheck` 9: six population-like ranges the old padding took under 0; a series with a negative low still pads below it |
| 7 | People: "homes for 634" beside SPARE HOMES 0 | **changed**: the 634 is people the homes hold, so the box reads "room for 634 people · ¼" and its tooltip "people, not homes" (the jobs box beside it is in people too) | by eye: `PT1/people-bridge-before.png`, `-after.png`; KeptCheck green |
| 8 | The drawer's footnote under HOW THE CITY IS | **fixed**: it closes NEEDS YOU's rows, before HOW THE CITY IS (`SummaryScreen.summaryRows()`) | by eye: `PT1/drawer-summary-before.png`, `-after.png`; KeptCheck green |
| 9 | A notice toasted again on load | **fixed**: the toasts' ledger is the model's (`Inbox.toToast`, `markAllToasted`); a load marks every notice the save holds as toasted, so one raised before the save stays unread in the inbox, the envelope red, with no toast | `PlaytestFixCheck` 3: an inbox restored as a load restores it, the emptied set would toast it, marked it does not; a later notice toasts once, not twice |
| 10 | The toast covered "Turn automatic building off" | **not done, PT1-C**: the button sits mid-page on the Build overview (the page is 1,067 px and scrolls), so it passes under the 33 px toast for about 50 px of scrolling; a bottom inset only adds scroll after it. `PT1/build-overview-toast-covers-button.png` shows it. A toast already goes when clicked (it opens the inbox) and fades in 8 s | a call for Jerus, below |
| 11 | "Debt-to-GDP Ratio: NaN%" and "Interest Rate: %3.92" | **fixed**: `EconomyManager.debtToGdpLine` prints a dash with no GDP; the rate prints "3.92%" | `PlaytestFixCheck` 4: the formatter at 0/0 and 5,000/0, and a real first month's log captured: a dash, and the sign after the figure |

`docs/queue/PT1/RenderPT1.java` made the renders (scratch, not in the build;
its usage is at its top); the befores from 0.8.29's classes.

## Calls for Jerus

- **Item 10, the toast's corner.** Two ways: (a) while a toast shows, the page
  gives up the corner - the page's scroller takes a bottom padding as tall as
  the toasts, keeping its pixel offset so nothing jumps, and a harness holds
  that a toast never covers the page's viewport; (b) leave the overlay, which
  fades in 8 s and goes when clicked. PT1-C under Pending until he picks.
- **Item 5.** The two signs are his convention; one sign everywhere is a
  change of it, not a fix.

## The checks, the trace

See the bottom of this file, written at the end of the batch.

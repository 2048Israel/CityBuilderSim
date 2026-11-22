# PW5 — substations

Brief: QUEUE's PW5 row, spec-electricity sections 2.7 and 5, and PW1-notes section 0.
Built from origin/main 01e6c2d4b (0.9.188). Money and saved flows change; stranger review owed.

## What changed

Substation is appended as permanent building id 155, a city-owned Electricity building.
It supplies carry, never generation. The existing utility staffing, construction, repairs,
city capital spending, money audit and national accounts pay its actual costs once.
The tariff's capital recovery is revenue to that same owner, the treasury, rather than
a second capital payment. Repairs go to Construction through CITY_REPAIRS; filled posts
go to households through the utility payroll. No additional audit debit or GDP output is invented.

BuildAdvice's Power grid measure uses the normal AutoBuilder essential-work path, as
the roads do: peak coverage, sites counted once, money and debt, land and real construction.
The Build screen offers the new measure and its peak-carry card. Generation remains its
own measure. Each dispatch half is held to the wires before fuel or hydro water is used;
the difference is in the existing unserved kWh, short-block count and cap-price books.

## Calls

- ★ The study's recommended 100 MW per substation, 4% real capital rate and 40-year life.
  Capital recovery uses that annuity, over twelve monthly bills; today's month's delivered
  kWh times twelve supplies the annual denominator. No demand means no wires invoice.
- ★ $440M replacement value: cash 264,000 and 9,778 material units at $18,000, $440.004M
  before tax. Construction is 132,000 points. A five-acre yard represents the district's
  lines, transformers and meters; no wires are routed. The normal 1% annual repairs and
  140 posts (100 diploma, 30 college engineering, 10 university engineering) approximate
  the study's one-third posts/repairs, two-thirds capital split at founding wages. Actual
  filled payroll, current replacement cost and builders' tax set the bill, not this estimate.
- ★ Preserve PW1's free founding 10 MW network, including its existing wires. Substations
  replace that starter carry: max(founding carry, substations × 100 MW), not an extra 10 MW
  on each developed grid. Otherwise a new town would lose its promised founding supply.
- ★ Heavy industry and mining retain PW1's transmission-only class. The AEO's 1.885/5.938
  transmission share divides actual grid cost; other customers pay the whole wires share.
  Homes remain unbilled until PW8, as the brief specifies, so full cost recovery is not guaranteed.
- ★ Snapshot costs when dispatching; strike them once with that dispatch. DataSave.powerBooks
  carries Book.gridCosts and pendingGridCosts; restorePendingPower restores the completed
  flow after the last load rebuild. Redenomination scales both snapshots. No HistorySave
  series is added. Old pending dispatches cannot supply those costs: SAVE_FORMAT and the
  oldest-loadable floor rise together, with no conversion, from the shipped value after merging main.

## Checks so far

- Initial `--fast`: green.
- SubstationCheck: nine assertions green, including 99/101 MW with one substation, the
  starter-only city, per-half conservation and cap price, actual building costs, cost changes
  leaving the earlier book alone, sites 0/1/1 after peak growth, and exact save/load snapshots
  and the first post-load strike. Cities use GameFiles.scratch and slot 10 only.
- First PowerPriceCheck found its deliberately large generation-short fixture could not
  fund eight substations. The fixture now funds the quoted wires before the audit window;
  its generation-short premise remains, with enough wires to isolate it.

Final named checks, pre-PW5 rule failures, review, ensemble and gate results follow below.

## The finding against main

Compiled untouched 01e6c2d4b in a detached scratch worktree (`--fast`, no baseline suite).
Copied only the compiled SubstationCheck.class into those classes and ran that named
harness's `--rules` section. It uses the old model's existing inputs and reads its book
through Gson, so no new model class or data row was transplanted. Exit 1, six assertions
failed: the 99/101 MW carry boundary, per-half conservation at the carry and shortage
price, the city held to its starter grid, costs in the wires' bill, a changed building cost
changing the next bill, and the peak-growth order with its site counted once. They all
pass on PW5. Raw output: target/PW5-main-rules.txt; the six labels are the harness's
first six assertions. This is a mechanic finding, not a compile-failure comparison.

PowerPriceCheck now passes all 77 checks, including filled grid payroll, actual bills,
the treasury's margin, fuel sellers and all fixture-month money audits. BuildingDataCheck's
first pass identified its exact dated-id map needed the appended 1900 Substation entry;
the map was extended, preserving all earlier dates and the exact equality check.

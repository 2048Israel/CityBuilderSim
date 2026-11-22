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

SaveFileCheck's 150-month city exposed a residual FS17 premise, rather than a failed save
comparison: its poorest cell had zero income and negative money after fixed bills, yet
the first struck month still bought every basket. Emptying savings, foreign cash and
city paper left shares/corporate bonds for the normal liquidity waterfall to sell. A
diagnostic showed first-month food money 0.0569964 and 2 of 2 baskets asked; the second
month had only 0.00000194776 and asked 0.000208009 of 2, one strike too late for the
hunger-at-full-shelves reading. A credit lockout experiment changed neither result and
was removed. Removing securities outright caused fourteen issuer/holder/debt save
comparisons to fail: the load correctly recounted the missing bonds. The fixture instead
gives those securities to another populated cell, with the proportional acquisition/tax
stocks and bond faces, preserving issuer debt and holder totals. Its chosen cell is
without means before both strikes. Every nonzero hunger assertion and every
save/continuation comparison remains. Kept diagnostic:
target/PW5-results/SaveFileCheck-premise.txt. No production household rule changes.

## Final checks before review

One harness per batch-check invocation, sequentially, with the required Earth/world paths:
PowerPriceCheck (77 assertions), BuildingDataCheck, PowerDispatchCheck, SubstationCheck
(all nine assertions), SaveFileCheck (including the repaired premise and every save identity),
ReadPathCheck, GdpCheck and PowerViewCheck all exit 0. Raw named logs and batch summaries
are retained under target/PW5-results/. No known-red waiver was needed for these runs.
AutoBuildCheck also exits 0; its one existing path assertion remains OBSERVED, and no
assertion was changed for PW5. The new substation rules are nine strict assertions.

Visual cues: Utilities' Power grid ring and Substation card say what peak kW the wires
carry; the yard appears through the existing utility building drawing. Services' existing
unserved-energy and short-half-block lines include a grid shortage.

The default trace changes: the city builds substations and its bills follow their costs.
The 80-run ensemble against the newest versioned baseline, and the ALL dry run, remain
for the stranger's SHIP/go, as the brief orders. No local full suite or ensemble was run.

Final `--fast` exits 0: compile, regenerated maps, zero firm Stale findings (196 soft,
unchanged from the initial main build), and the tree check. Source/notes whitespace and
LF checks pass; the generated month-order Markdown retains its intended hard breaks.
`known-reds.sh PW5 docs/queue/PW5-ship.md` exits 0 with no branch waiver rows.
`pick.sh origin/main HEAD`: ALL, 242 harnesses. Its review floor is saves: DataSave.java,
+SAVE_FORMAT, +OLDEST_LOADABLE_FORMAT; money review is also explicitly owed for the
changed grid tariff and treasury receipts. There are no threading changes.

At the review tip origin/main is still 01e6c2d4b (0.9.188); save format and its floor are
61, raised from main's 60. Merge main again on SHIP/go, reconciling that number above
HC1/RES2's shipped value before the ensemble and dry run. The provisional ship marker
stays in place for that review and the remaining checks.

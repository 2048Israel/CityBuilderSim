# FP1 — The fund's long lists a page at a time

## Built

Search shows SEARCH_ROWS (40) results and adds that many per Show more press.
The control states the remaining result count and disappears at the end.
Typing and chip presses reset the limit. Result order and section headings
come from FundView.search. Only the visible hits are put into screen words.

Activity shows its latest twelve months and each earlier calendar year behind
its own initially shut fold. FundView supplies each year's distinct recorded
month count, its net and its rows. Only open years have their rows drawn.
The existing grouped trade rows still open to their lots, with their money
and realized totals now supplied by FundView too.

Search's empty price and held values have separate kept scopes. The first
focused run reached the BF1-K empty wrapped-glyph bounds fault on a final
Search page. This repair stays in Search and preserves the hard comparison.

## Calls

- ★ The orchestrator accepted net as recorded realized gains and losses plus
  income, excluding contributions, withdrawals and trade principal. The fold
  labels this “Net realized + income” and explains it in a tooltip. Jerus can
  overturn this call. Net cash was the alternative. A fixture year includes
  profitable and loss-making sales, a dividend, a contribution and withdrawal,
  and checks the getter against the ledger's own rows.
- ★ The latest twelve months are a rolling window. Earlier rows are grouped
  by calendar year, so the first older year may contain part of a year.
  Counts cover distinct months with matching retained records under the chip.
- The existing activityMonths bench hand remains supported. The player's
  page starts at ACTIVITY_MONTHS, now twelve, and has year folds.
- No known-reds entry names FP1 at the starting main, 83f25ae7.
- The orchestrator confirmed that the dry run uses the gate's normal pick.
  Its selection is the widest run here. No local ALL or ensemble is run.

## Checks so far

The initial fast check passed. The first focused Fund run at month 2105
found four checks acting on the comparison's fresh search box and one
retained empty-glyph bounds failure. The fixtures now draw kept before
typing so their field reference is the live one. The bounds repair gives
empty Search values their own scopes. Every older year's open/shut
signature and canvas comparison passed that first run.

Final check results, five timing readings and retained gate details follow
when those runs finish.

The final focused replay passed every Fund assertion, including the original
Portfolio/ticket checks and the new paging/year-fold checks. Its kept costs
were Search 5.0 ms and Activity 16.7 ms, with fresh draws 22.8 and 28.3 ms.
These are correctness-replay readings, separate from the five calibration
runs below. The replay log is target/fp1/pages-final.log.

Main a309bf7f (CB2) was merged at 00aece74. Its generated README index
conflict was resolved by regeneration, and the merged fast check passed.
The orchestrator confirmed that BF1-K's existing cover and queue row stay,
because its Partners triangle finding remains. FP1 repairs its Fund Search
glyph finding only.

## Timing setup

Five exploratory cost-only JVMs each built the played city to month 2105
and called section 68's pageCost on the default Search and Activity pages.
Their kept Search readings were 19.3, 15.1, 15.9, 16.6 and 18.1 ms.
Their kept Activity readings were 32.8, 36.9, 35.8, 28.7 and 32.1 ms.
All five exited zero. These start JavaFX's Fund controls cold.
For final calibration the runner uses KP5's complete money sections 55–68,
after building the same played city, with one JVM at a time. ★ Section 68
explicitly starts Search on All with no words and Activity on All with its
twelve-month window and the year folds shut. This measures the new default
pages, independent of words or folds left by earlier control fixtures.
The original heaviest-page check remains hard.

The money-section calibration route also checks section 68's original
heaviest-page premise. In its first run Activity made 3,145 nodes, Search
803 and Portfolio 876, so that premise passed. Fund correctness passed.
Three diagnostic failures were outside Fund in this shortened entry route,
which skips KeptCheck's normal earlier sections: Bank Funding retention
85.9%, a chosen bond's empty levels fold, and Finances retention 89.4%.
Their premises and assertions were left intact. The retained gate uses the
normal KeptCheck entry point and supplies the authoritative verdict.

## Final calibration

Five sequential JVMs on main a309bf7f, with the Fund changes, each using
KeptCheck.money after LongPlaytest.probeCity and probePlay to 2100.
The floor was set to the going rate before play, matching KeptCheck.
Each pageCost reading is its median of five measured months.

| Run | Search kept | Search new | Activity kept | Activity new |
|---|---:|---:|---:|---:|
| 1 | 10.7 ms | 21.2 ms | 41.7 ms | 129.7 ms |
| 2 | 10.5 ms | 21.2 ms | 42.3 ms | 106.1 ms |
| 3 | 10.1 ms | 20.3 ms | 47.1 ms | 97.9 ms |
| 4 | 10.3 ms | 23.7 ms | 45.8 ms | 102.1 ms |
| 5 | 10.6 ms | 21.7 ms | 46.7 ms | 93.5 ms |

Search median 10.5 ms. Activity median 45.8 ms.
KEPT_FUND_MS is ceil(2 × 45.8) = 92, replacing 321.
All five had the same three pre-Fund diagnostic failures noted above and
zero Fund failures. Logs are target/fp1/money-1.log through money-5.log.
Activity remains the heaviest of the four default pages in every run.
Its twelve open months contain 3,145 nodes in this fixture, which accounts
for the larger cost than the 803-node Search and the Finances pages.
The final extra change reads the grouped trade count through FundView too,
beside its money and realized result. Its value and grouping are identical.

## Final verification

The final fast check passed with zero firm Stale findings. Maps were
regenerated. Default trace unchanged by FP1, whose production changes are
FundScreen and pure FundView getters. The model and month paths match
main a309bf7f. No ensemble was run, as directed for this screen batch.
The retained normal-route merge dry run uses the gate's default three workers.

REV-FP1-1 returned SHIP on code/fixtures commit 52ba03d2, relayed by the
orchestrator at 12:07 on 2026-11-15. The pushed provisional ship commit is
99c5bcb7. The heavy slot is held for UP6b's dry run, so FP1 is waiting for
the orchestrator's explicit go.

The orchestrator gave go at 12:25 after UP6b shipped. Main ade96cfb
(0.9.115) was merged before FP1's dry run. Conflicts were confined to
generated harness and README indexes and were resolved by regeneration.
The merged fast check passed. The review approved FP1's own code at
52ba03d2, and this merge adds the shipped UP6b changes.

The first retained run, /tmp/cbs-gate-FP1-47pPzzsa, was stopped with
tools/gate.sh --stop after compile. I had applied cbs.parallel=1 to the
gate from the building-only restriction. The orchestrator requested the
normal default and that override was removed. The run exited 130 with
origin refs unmoved. Its owned native process tree was captured before
stopping, and every captured owned PID was confirmed gone before relaunch.

## The normal run's Search retention finding

The retained default-worker run C:/Users/Jerus/AppData/Local/Temp/cbs-gate-FP1-9NqHkkGn
was of branch 4eca91edbccdf71cb114452be312437eab47ba5c and main
ade96cfbd2ed1074b834dbfb0bf0a93f329f841b. It completed at 13:12 on
2026-11-15, gate exit 0 and suite exit 3, with no unnamed lines.
The suite's recorded reds were DeathRecordCheck, InvestCheck, KeptCheck,
MapCheck, MoneyDatesCheck and ResearchViewCheck.

FP1 was held despite that verdict. Search's first forty results changed
members over a month, making 144 nodes and retaining 660, or 82.1%.
Its fields and pictures matched new. KF63's broad retention-line cover
classified the new failure as known. The other new paging/year checks
passed. No cover was moved or added. The attempted stop found the run
already complete, and its native ship process was confirmed gone.

★ Search rows now keep their visible positions. Their words, marks,
sparklines, colours and click targets are set from the current security
on every draw. A changing result set keeps the row nodes while the result
order remains FundView's. The 90% law stays hard.

A new fixture replaces listed shares with bonds at the same row positions,
checks the common row identities, compares all fields against a page built
new, and clicks a reused row to prove it opens the current bond. It checks
that the model stayed in the same month. It adds no simulated months and
leaves the existing holding/ticket fixtures in place.

The focused Fund replay passed every assertion after this mend, including
the forced replacement, refreshed fields and current click target.
The merged fast check passed with zero firm Stale findings.
The replay is target/fp1/positions-pages.log. Its concurrent-PC costs were
Search 26.9 ms kept / 80.0 new and Activity 56.4 / 95.0.

★ KEPT_FUND_MS stays 92 from the five Activity readings above. Activity's
code and the model's month path are unchanged by this Search-only mend.
The replacement fixture advances no month. Search remains lighter than
Activity in the post-mend replay.

## The warrant move's empty state

The delta review returned SHIP on bfbfd182. A further isolated row-reuse
probe then exposed the same empty wrapped-label fault in Search's move
column, whose warrants have no move. A wrapped prior value led to a retained
empty label at 220 × 23 against a fresh 220 × 15. The probe failed its hard
fresh comparison in target/fp1/warrant-probe.log.

The move now has separate empty and nonempty scopes, as price and held do.
The new cheap fixture uses an independent scratch city and a loaded preferred
block with warrants, then changes the same row from the preferred to its
warrants. It uses pageShows' spoil-and-redraw pattern to force multiple
wrapped lines before the move becomes empty. It asserts the row is retained,
the current move is empty, and the signature and picture equal new.
It creates no simulated months in the original city.

The fixture passed all four assertions after the repair, exit 0, in
target/fp1/warrant-fixture.log. The fast check passed with zero firm Stale
findings. The orchestrator will read this small scope-and-fixture delta.

The orchestrator accepted c9e5e7c2's move-scope and fixture delta at 14:21.
The new dry-run go arrived at 14:27, with main still ade96cfb. The branch
was fetched and its main ancestry checked before launch. The provisional
marker is still present.

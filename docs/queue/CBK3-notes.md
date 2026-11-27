# CBK3 - the window against collateral and the bank's rules as dials

Review candidate: required named verification is complete; stranger review, ensemble and dry run remain. Base b2f50a9ef, 0.9.213, format66.
CBK1 main482dbfab2 (0.9.214, still format66) is merged for integration checks.
The builder keeps decisions and review; CBK3-trace's bounded diagnostic
reports and scorer slice are described below. All runs name main's Earth2
and World1 files. No player save directory or slot was accessed.

## Finding and repair

Exchange.postDesk/deskLimits sized the desk from capital, fair value, company
and book caps. Bank.deskCanCarry rechecked capital at every fill; it did not
check stable cash. deskPays then reduced net cash. At the next settlement,
all borrowing beyond deposits went to the unlimited window. Large equity
inventory and repeated overnight funding could therefore reinforce each other.
The read-only helper identified the exact path; the builder implemented it.

* ★ Design choice: keep customer market making and fair-price sale rules, add
  one stable cash budget in Bank.deskCanCarry shared by posting and every fill.
  Cash plus core deposits, after hot withdrawals and required reserves, is
  what a new purchase can spend. Existing inventory is refinanced, not erased
  or sold below fair. No GDP or remittance cap was added.
* ★ Design choice: window collateral is domestic loans net of allowances plus
  bank-owned government paper at market, after haircuts. Secured business and
  rental mortgages take30%, unsecured households85%, government paper5%.
  Equity, carry and unrated corporate bonds get zero. Mortgage eligibility is
  an ELA-like secured-property approximation, not ordinary SLF eligibility.
* ★ Design choice: collateral is necessary but does not supply stable funding.
  The aggregate NSFR approximation requires85% for ordinary loans,65% for
  insured low-risk mortgages,5% for government paper and100% for equity.
  Core stable funding is equity plus95% of household and50% of sector deposits;
  hot deposits and overnight advances have no stable value. Foreign term
  funding covers the stable-funding gap and residual past the window.
* ★ Design choice: replacement wholesale principal is payable in local money,
  with a one-year rolling funding benchmark. Its independent expected local
  policy path plus existing country-credit and term premia prices it. Using
  the USD short rate without USD principal would let the bank bypass the
  local dial. Reusing the treasury yield would feed the bank's funding floor
  back into itself. The quote excludes that floor and treasury QE compression;
  loan pricing counts the loan's term premium once.
* ★ Design choice: foreign creditors retain their interest claim in resolution.
  The existing central-bank standstill remains interest-free. New credit still
  obeys the bank's freeze and capital rules. This batch adds no borrower or
  connected-group identities and no new source of bank capital.

## Reality checked

[Fed collateral eligibility](https://www.frbdiscountwindow.org/pages/collateral/collateral_eligibility)
and [Fed margins](https://www.frbdiscountwindow.org/pages/collateral/collateral_valuation)
require secured advances, give Treasuries95-99% lending values by duration,
and value loans by risk and estimated fair value. Ordinary equity is absent
from the eligible securities list. The conservative5% government haircut is
applied to the game's current market value, not face.

[Bank of Canada SLF collateral](https://www.bankofcanada.ca/2026/09/assets-eligible-collateral-standing-liquidity-facility-310125/)
lists30% for secured business portfolios and85% for unsecured consumers.
[ELA](https://www.bankofcanada.ca/markets/market-operations-liquidity-provision/framework-market-operations-liquidity-provision/emergency-lending-assistance/)
still requires collateral and permits mortgages with case-specific valuation.
The rental-mortgage30% haircut is this model's secured-property approximation.

[Basel NSFR](https://www.bis.org/committees/bcbs/basel-framework/standard/nsf?allChapters=true)
NSF30.11/.13 assign stable retail95% and short corporate deposits50%; .14 gives
short central-bank/interbank funds0%; .26/.30/.31 assign government paper5%,
low-risk mortgages65%, ordinary long loans/non-HQLA equity85%. The desk's100%
stable requirement is deliberately stronger. [LCR](https://www.bis.org/committees/bcbs/basel-framework/standard/lcr?allChapters=true)
requires HQLA against stressed30-day net outflows at100%: collateral eligibility
is not stable funding. This is an aggregate approximation, not a complete
regulatory liquidity return.

[The Fed's Volcker summary](https://www.federalreserve.gov/supervisionreg/volcker-rule.htm)
prohibits proprietary trading in general; [the SEC's market-making explanation](https://www.sec.gov/newsroom/speeches-statements/2014-spch020414jr)
retains customer market making with inventory tied to expected demand.
[The Fed's reserve rule](https://www.federalreserve.gov/monetarypolicy/reservereq.htm?hl=en-US)
has been zero since March26,2020, matching the saved default.

The real remittance comparison is a range, not a target cap. The Fed remitted
$107.4bn in2021 ([release](https://www.federalreserve.gov/newsevents/pressreleases/other20220114a.htm));
FY2021 receipts were$4,046bn ([Treasury](https://fiscal.treasury.gov/accounting/us-financial-report/2021/statements-of-change-cash-balance)),
about2.65%, with different calendar/fiscal periods.

## Copied-city evidence

BankRulesCheck copies only the supplied temp evidence into TempDirs, then
loads through package-private GameFiles(dir,legacy). The provided1257 folder
is never written. The loaded m133 values match the brief: desk9861.906318872,
cash-17366.424253986, window12142.809656253, trailing revenue1998.942287585.
Original continuation m134-253: remittance10134.551266349, revenue48341.489897126,
share20.9644992%, six further bank failures, worst audit1.02318153949e-10.
Its first12-month share was46.5852538%. Log: target/CBK3-evidence-before.log.

Current repaired continuation, the same120 months and saved settings:
remittance476.704248490, revenue33741.426003542, share1.4128160%, three further
failures, worst audit1.95086613530e-10 save units. Every month passed the
unchanged cent bound, collateral ceiling and exclusion of equity inventory
from window financing. Final window1846.310417314; inherited desk3443.455032691 remains at
market. Log: target/CBK3-evidence-final.log; launcher target/CBK3-part1-final.txt.

Provisional observations2.94%,8.78% and4.26% were superseded while correcting
the funding-price/currency link and adding stable funding. They were never
remittance targets. Their scratch logs remain for the build trace. Only the
current repaired run above is the model measurement.

## Saved rules and old saves

BankRules owns nine settings. Bank, BusinessDebtManager and mortgage order
checks share it; DataSave writes/reads the array and Game restores it. Defaults
keep window spread0.25pt, the repaired collateral limit100%, capital8%,
countercyclical0, leverage3%, reserves0, mortgage LTC85%, additional DTI off,
working-capital loan/assets90%. Conservation retains its research date and
mortgage payment coverage remains1.20. The new DTI is principal over property
annual net rent, distinct from DSCR, and starts off because none existed.

* ★ Design choice: risk-capital tightening must not relax insured-credit leverage
  targets. Their scaling keeps the original8% reference. Reserve withholding
  reaches funding, the desk budget and branch previews. Pricing, auctions,
  mortgage messages and screen minima read current rules, not default constants.
  Limits apply to new decisions and funding rollovers, not a retroactive erasure.

DataSave.bankFunding keeps authoritative foreign stock, gross draws/repayments,
interest and the lagged pricing mix. Monthly flows clear after statements are
filed; reset clears them and redenomination scales amounts, not rates.
The gross audit pool is net cash + actual CB advances + settled foreign stock
- unsettled city paper. Foreign draws/repayments are FINANCIAL, interest INCOME;
only window transactions are MONEY. Foreign principal is already in negative
net cash/borrowings and is never credited or added to liabilities twice.

No format bump/conversion: missing new foreign fields mean zero, their actual
pre-CBK3 value. A native66 save retains its actual CB claim until normal
settlement. Its saved total funding expense is still the window expense when
foreign expense is absent. Historical enum lines append, old missing lines
read zero, and lagged saved pricing remains the historical quote. Actual CB
claims are supplied separately to bank statements, rather than replaced by
the new target on load. BankRulesCheck and SaveFileCheck exercise the real
writer/loader and otherwise-identical missing-key saves.

## Single-borrower limitation settled

CBK3-trace found no borrower/group IDs or defensible firm-size measure. Sector
books pool firms; tickets and building counts do not establish control. Bank's
notes record Jerus withdrawing sector-as-borrower after99% of credit went abroad.
The builder asked by bus (mail20261126-165333-983-CBK3-565f).
The orchestrator's20261126-165359-042 instruction: ship nine working dials,
neither show nor save an inert borrower control, and queue a ledger/allocation
premise before Basel25%, exempting sovereigns. LE1 was checked free and its
brief is docs/queue/LE1.md; it will be queued through this batch's ship file.
No ticket cap or invented firm count is used.

## Equity valuation and the floor repair

The orchestrator relayed RES3's lead in mail20261126-174636-318. The helper
confirmed mature Equity.fairValue resurrected its offering quote at zero
income/spare value, while capitalising even tiny positive income. Desk marks,
trading income, equity and purchasing capacity inherited that discontinuity.
Its contribution to the original desk stock cannot be isolated from source.

* ★ Design choice: remove only the mature listing's historical-price fallback.
  Keep the existing spare-cash plus capitalised-income model continuous down
  to its monetary floor. The under-12-month prospects rule, income, ownership,
  cash and mark-loss accounting remain. This is not a new book-value promise.

[IFRS 13](https://www.ifrs.org/issued-standards/list-of-standards/ifrs-13-fair-value-measurement/)
defines fair value by a current exit price, supporting rejection of a stale
issue-price guarantee. The continuous zero limit is a model inference, not a
claim that every real loss-making company is worthless.

* ★ Design choice: keep the absolute concentration capital add-on unchanged
  when the regulatory minimum moves. Its equivalent risk weight is computed
  from the current minimum immediately, rather than cached until refresh.
  Risk staging keeps its calibrated90% reference: tightening new-credit LTA
  does not itself make an existing borrower riskier. The fixture changes the
  rule against nonzero concentration without refreshing its book.

The first uncommitted edit hit the identical return in young priceOf instead
of mature fairValue. Seven new regression assertions caught it; priceOf was
restored and only fairValue changed. No expected result or tolerance moved.
The wrong-site city1.7981594% and5/24 failure observations are invalid for the
final model (target/CBK3-evidence-wrong-return.log and
CBK3-bankfailure-wrong-return.log). Other intermediate city measurements,
including4.2055303% on mature-only a32abc1a1, are superseded.

That mature-only version exposed a real numerical defect in BankFailureCheck:
seeds6/13/20 at m76,10 at m113,22 at m75 became non-finite, with3162 splits.
Equity's presentation floor follows expected prices; Exchange's nominal
floor was lower. A never-traded company at Equity's floor bypassed the
existing worthless-consolidation skip, repeatedly shrinking its share count.
The first overflowing company/operator was not captured, so is not claimed.

* ★ Design choice: Exchange.valuationFloor uses max(nominal floor,
  Equity.noValue) consistently for fair strikes, atFloor and the existing
  no-worthless-consolidation law. No amount is clipped, no capital invented,
  and no saved cache added. Legitimate recovery, splits, ownership and cash
  remain. Six240-step regressions cover zero/negative/tiny earnings, two
  denominations and expected prices2, then cause a real positive recovery.
  BankFailureCheck now explicitly rejects a non-finite equity movement/scale;
  its former abs(NaN)>bound test could falsely pass. Its tolerance is unchanged.

## Bank health on the matched copied city

The new desk-chat health request is measured by the same standalone source,
tools/CBK3CopiedCityMetricsCheck.java, on both models. The original is rebuilt
in target/CBK3-original-metrics-352 at352aaf3b2: versus b2f50a9ef the only
changes are AllChecks, the initial BankRulesCheck and notes, no model source.
Only its observer class is copied ahead of the FINAL model on the classpath;
no old Bank/Game class shadows the final model. Both runs use the same
approved1257 files, fresh TempDirs/GameFiles(no-legacy), original120 months
and choices. Each audit rejects non-finite values and keeps the native cent
bound. The old classes compile with --fast; the observer is run by its exact
name serially, since the proprietary evidence is not a permanent suite fixture.

The monthly observer retains121 records (start plus120 closed months).
Original totals reproduce10134.551266349/48341.489897126 and six failures;
final totals reproduce476.704248490/33741.426003542 and three. Every previously
recorded annual/m143 snapshot, including all21 health/context columns, matches
exactly before and after: the new reads and callable refactor move no result.
Logs: target/CBK3-health-before.log, CBK3-health-after.log; comparison:
target/CBK3-health-comparison.json. Earlier annual-only logs are retained as
CBK3-health-before-annual.log and CBK3-health-ef5.log and are superseded for
peak/pending measures.

| Measure | Original m253 | Repaired m253 |
|---|---:|---:|
| Remittance / revenue over120 months |20.9644992%|1.4128160%|
| Risk capital ratio |14.2277376%|19.8427253%|
| Current-month NIM, annualised |13.4021800%|6.9574495%|
| Trailing-year NIM |4.8773199%|5.2311302%|
| Trailing-year ROE |-45.3458474%|11.8520962%|
| Pending preferred quote at endpoint |0|0|
| Largest MONTH-END preferred quote (native thousands) |865.057153|574.454324|
| Months with a pending preferred offer |44|19|
| Months below8% risk capital |44|19|
| Further bank failures |6|3|

Both year ratios have12 known months and positive average denominators.
Quotes are states, not issuance flows: repeated offers are never summed or
called distinct offers. The display unit is a native thousand; reserve and
capital percentage ratios are dimensionless. Book means the native bank book,
not an invented loan-only subtotal. Current-month NIM and year NIM are kept
separate; a nonpositive average denominator would mark a return unavailable,
not interpret the getter's zero fallback as recovery.

At m143 the matched original/final replays both already have one failure:
capital13.485931%/13.050730%, month NIM-1.608550%/+0.257114%, year NIM
-4.541392%/-3.077471%, year ROE-141.422498%/-151.132970%, preferred0/0.
Early year ROE has not recovered; the120-month result above shows subsequent
recovery. These are not the same capital/pending state as the desk chat's
6.7%/335k screenshot. Its exact actions/runtime checkpoint are not inferred;
only the matched1257 replay, validated against the retained original results,
is used as the quantitative control. The screenshot remains separate evidence
of the losing window-funded book.

## Checks and verification status

All runs name Earth2/World1. Laptop checks are serial with cbs.parallel=1;
exact named JVMs avoid AllChecks substring selection (BankCheck also selects
CentralBank/YoungBank; Exchange/Market select their Debt/World variants).
No fixture, horizon, clock budget, conservation, cent or save bound is reduced.

| Check | Result and code |
|---|---|
| --fast |Final3b4caaf49 source green; zero firm Stale,209 informational soft|
| BankRulesCheck |7d92 source green, including nine controls/defaults, real/missing-key saves, all120 copied-city audits and fresh callable regressions|
| BankCheck exact |7d92c82c6 green, including non-finite articulation alerts|
| MonetaryCheck |ef5c40fa7 green|
| BankFailureCheck |6ff36d2d8 full24x120:2880 audits/finite-equity checks green;5/24 ordinary failures,317 splits|
| EquityCheck |6ff green, original laws plus109 valuation/96 shared-floor regression lines|
| ExchangeCheck / MarketCheck exact |6ff green|
| SaveFileCheck |3a4 native exit3: sole registered REV-HC1 P5 populated format62 city; new CBK3 paths green|
| CentralBankStatementCheck |ab69 CBK1 integration green, played statements/read-only twins/247-slot old/new array paths|
| CentralBankCheck |3b4caaf49 FULL box run exit0: all24 rows, subsequent rules/defaults/saves and par conservation green|

The funding-law restatements in BankCheck/CentralBankCheck replace the former
all-wholesale-is-window identities with both actual tranches and the CB's own
bill. Insolvency does not turn deposit/term funding into overnight money;
credit still freezes. Original numeric bounds and caused fixtures remain.

The initial6ff full Central attempts stopped at1740s after20/24 strictness
rows, with no prior assertion failure; later rules/saves/par conservation
were unexecuted. The complete ef5 attempt stopped after18/24, likewise no
preceding assertion red. These are incomplete, not HT1-H or an FS10 exemption.
Kept logs: target/CBK3-central-6ff-timeout.log and
CBK3-central-ef5-timeout.log (with -wrapper.txt).

Two diagnostic runs were deliberately stopped only to remove owned duplicate
readers: ab69 after six rows, verified PID27760/parent9928 at19:36:26;
3a4 after three rows, PID28844/parent29616 at19:51:15. Their -stopped.log and
-wrapper-stopped.txt files retain the incomplete outputs. The3b4 local retry
was stopped ONLY on orchestrator's explicit box-placement go212218-058:
verified PID32476/parent4380 created21:05:07; partial log
CBK3-central-3b4-box-transfer.log. No other process tree was touched.

Section22 now runs before howStrict: it owns its60-month city, held-line/par
branches and saves. Helper25 proved howStrict does not use its cumulative
identity counters. Every fixture/predicate/month remains, one call moved
(1 line in/1 out). Section22 completed green in the local3b4 retry. This does
not complete section21: its later laws/defaults/saves still require the full
24-city measurement to finish. A mismatched Ensemble/strict population
comparison is NOT used to attribute timing; their founding, market, player,
works and advise premises differ.

Orchestrator approved ONE exact named CentralBankCheck on the faster box,
standard1740s/all cases/horizons, before mends (mail212218-058). Completed exit0. Run:
/home/jerus/cbs/observations/CBK3-central-20261126-2130 at3b4caaf49,
exclusive box.lock, no other builder job beside it. Its metadata and
section-times.jsonl are in target/cbk3-named there; copied home to
target/CBK3-central-box (checks and cbk3-named). All24 table rows completed
by396.993s; later rule/default/save checks also passed. Both Earth2/World1
SHA256s match the laptop. This is a named check, not a dry run or reusable gate.
No timeout exemption/FS10 classification or weakened bound was needed.

## Per-operation valuation and settlement work

* ★ Design choice: keep new collateral readers stateless across operations.
  One domestic curve per valuation avoids repeated whole-book curve-key scans.
  A zero requested advance needs no collateral price; NaN REQUESTS still reach
  the original minimum. This is a finite-valid-state identity, not universal
  equivalence for an arbitrary NaN/stateful callback.
* ★ Design choice: Game requests gross wholesale need and the CB applies its
  independent eligible window cap once. A settle posts the already-capped
  difference through the same ledger body; direct advances retain their cap.
  No economic writer intervenes. One unchanged window target feeds both
  closing pricing shares. No saved/held snapshot or new public API is added.
* ★ Design choice: prepare the existing longest eligible callable tree and
  one volatility reading within ONE collateral valuation. Old insertion-order
  cold reads could refit progressively larger trees and reread61 history rates
  per bond. Price/sum/holder order and scalar/foreign/matured branches remain;
  treeOn already allows a longer tree to price shorter flows.

Helper23 proved CallTree.fit prefixes independent of final horizon, and price
uses only its own flows. Helper24 supplied166 regression lines, reviewed and
adopted: TWO fresh cold books, three increasing partial-held callable maxima,
future calls/real optionality/above-par values, excluded longer unheld/foreign/
matured bonds, a holder change between operations, exact sum bits and1 versus3
volatility reads.18 independent short/long-prefix prices match by bits on
flat/sloped/clipped curves at floor/positive sigma. The old same-book test
prewarmed its tree and alone could not cause this condition. All assertions
pass; all120 city totals and every earlier21-column health snapshot remain
exact. No runtime share or speed improvement is inferred from source alone.
No AP5/AP6 held figure is written, and no AP7 engine cache is built.

The helper sessions supplied bounded source/red diagnoses and the scorer/test
slices, no independent shipping decisions. The builder retained defaults,
old-save policy, model edits, diff review, records and commits. The wrong
return-site regression and stronger non-finite assertion are recorded above;
no expected value was adjusted to conceal either defect. Six existing scorer
and seven observation self-tests pass under Git Bash; an initial PowerShell
bash lookup failure was corrected by using that required shell.

## Ensemble observations and the approved baseline exception

The original0.9.212/MR1 CSV has no remittance/revenue/failure counters. F1=0
means findings, not rescued ordinary bank failures. No flow was inferred from
ending cash, M0 or equity. Orchestrator173158-709 approved one supplemental
replay of retained84234b69c classes, exact80 policies/seeds, getter-only
observations and eight byte-identical default trace proofs. Its start was
mailed174132-646 and PASS verdict185017-677. The original archive is untouched.
Kept root on box: /home/jerus/cbs/observations/CBK3-before-20261126; copied data:
target/ensemble/CBK3-before; proof: target/CBK3-baseline-capture/observer-proof.

The manifest matches2567 retained entries/2510 classes, SHA256
b88b7440a03e7c9d8fbd6e1618130c9faf8ce5f2dcd07475c4183225554967a5.
ObserverTrace calls the exact Ensemble.fundingColumns through LongPlaytest's
supported observer, stores nothing in Game, and proved all eight trace files
byte/MD5 equal. The first launch rejected an equivalent explicit-path settings
string before any80-run job started; main-data autodiscovery resumed using
the original snapshot settings. Original files, locks and guards remained.

Ensemble now records remitted/revenue flows, optional monthly ratio,
cumulative failures including rescues and lifetime remittance. The scorer
reports pooled sum(remitted)/sum(revenue), never averaged monthly ratios.
All6 existing scorer self-tests and7 new observation tests pass under Git
Bash. An initial PowerShell test invocation could not locate bash for four
runner cases; the required-shell rerun resolved it. Existing criteria,
thresholds, verdicts and hard M1/F1 are unchanged.

Supplement docs/ensemble/CBK3-before.md records all80 runs,102352 observed
rows and all28 old criterion values/verdicts identical to0.9.212:24/26overall,
M1/F1hard10/10, A1/A2 informational fail as before, H1 retired. The replay's
snapshot calls itself0.9.210 because it precedes the MR1 publisher's label;
it is the exact published0.9.212 code snapshot, not another city/model.
No auto currency reform occurs; raw flow units remain a stable denomination.

| Policy | Pooled remittance/revenue | Failures | Runs affected |
|---|---:|---:|---:|
| AUTO2 |0.061122288%|80|16/16|
| ZERO_SLACK |0.194778204%|67|16/16|
| ZERO_HOT |4.668742325%|45|14/16|
| SHOCK |5.877349694%|50|16/16|
| HIGH |2.432930655%|47|16/16|

Overall0.111123032%,289 failure episodes in78/80 (maximum8). Recorded windows
are AUTO2m2-2400, ZERO_SLACK2-2040, ZERO_HOT2-402, SHOCK2-840, HIGH2-720.
114 monthly ratio cells have nonpositive revenue and remain unavailable;
all underlying flow/failure observations are present. Candidate comparison
must pair each policy/seed/window; shared prefixes and different horizons
make an unqualified pooled comparison insufficient. The final CBK3 ensemble
has not run; stranger SHIP is its go, then main is merged first. M1 and F1
stop a ship. This baseline exception is not approval to skip that review.

## Publication and next steps

CBK1 main482dbfab2 (0.9.214, stillformat66) merged in ab69c5418 without a source
conflict; only generated maps/indexes conflicted and were regenerated. Its
central Policy page uses the identical BankRulesPane and the bank's own redraw
callback, one saved model in two places. Its policy-rate tooltip now calls it
the window base before the chosen spread. CLAUDE's interface count is55.
Mainb543f1f9c's manual-scope clarification merged in30b4df952. No RES3 model
or format67 has shipped into this branch yet. The saved-array integration
review verified the old39 CB slots followed by appended history,247 total.

Historical statements use actual settled CB/foreign claims, not a new target
on load. The deposit allocation is captioned net funding outside settled
wholesale, with current deposit books distinguished in its tooltip. In the
copy, domestic4798.148 plus hot549.655 are not the same timing/allocation as
5223.615 outside settled wholesale; no missing flow/creditor is reconstructed.

The manual belongs to the orchestrator at a milestone/Jerus request, per
mail193817-304 and updated brief. Only the player text below is supplied;
docs/manual.* remains untouched. Before that clarification a browser-host
failure and native URL guard stopped inspection; no artifact content was
edited/exported/published. No further browser/app input was attempted and
the task tab's final window state is unverified. This is not a batch block.
Gameplay write-up: claude/the-window-against-collateral.md. Eye check: the two
rule pages keep thumb/scroll/nodes and set the same saved values (no pending
row for an eye check).

Final named verification and local maps/fast are complete. Push the
provisional ship/LE1 queue row and mends at for stranger money/save review.
Stranger SHIP is the final ensemble go: merge main first, compare the newest
baseline and matching bank windows; M1/F1 stop a ship. ALL needs a separate
orchestrator dry-run placement go. No final readiness, candidate ensemble or
full gate reuse is claimed here.

## For the manual

Use the Bank tab to choose the commercial bank or the central bank. The
central bank's Policy page and Policy > Money > Bank rules show the same
banking rules. Moving one changes the rule at once, on both pages, and the
city saves it. The policy-rate proposal still uses Policy's Apply control.

The window lends against eligible collateral, after a haircut: it counts 70%
of secured business loans and rental mortgages,15% of unsecured household
loans, and95% of the market value of the government paper the bank owns.
Loan allowances are deducted. Company shares, carry loans and company bonds
do not qualify. The collateral-share control can reduce the window's ceiling
further. At normal settlement, funding beyond that ceiling is borrowed abroad
at a term wholesale rate, payable in the city's own money. Its price follows
the local policy outlook and credit premium. Expensive funding and the
capital rules can therefore leave less credit available.

The share desk also needs stable cash. It cannot finance a new purchase with
overnight window money or hot deposits from abroad; the same cash budget
applies to every purchase it makes. An inherited share portfolio is still
owned and marked at current prices: these rules do not erase it or
promise its old issue price. Funding and the statements show the window and
foreign term borrowing separately. Interest paid abroad is not income of the
central bank and does not become a treasury remittance.

The controls start at these values:

| Rule | Starting value | What tightening it does |
|---|---|---|
| Window spread over policy |0.25 percentage point|Raises the price of secured overnight funds|
| Window collateral share |100%|A lower share reduces secured funding|
| Minimum risk capital |8%|Requires more equity for the same risk-weighted loans|
| Countercyclical capital buffer |0%|Adds to the bank's conservation capital target, up to 2.5%|
| Minimum leverage equity |3%|Requires more equity against total exposure, including insured mortgages|
| Reserve requirement |0%|Keeps more deposit cash out of lending and share purchases|
| Mortgage loan / cost, before insurance premium |85%|A lower cap requires a larger down payment|
| Mortgage debt / annual income |Off|An enabled, lower multiple permits less principal against annual net property rent|
| Business loan / assets |90%|A lower cap reduces new working-credit room against the business's assets|

The mortgage payment-coverage test still applies when the additional income
multiple is off. New limits guide new credit decisions and funding rollovers;
opening an older city preserves what it actually owed in the saved month.
The next settlement applies the new window rule. A remittance is the central
bank's realised surplus after its costs and carried losses; no fixed share
of the treasury's revenue is guaranteed or imposed.

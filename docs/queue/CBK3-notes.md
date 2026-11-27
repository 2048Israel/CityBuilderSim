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

## Copied-city evidence at first mends

BankRulesCheck copies only the supplied temp evidence into TempDirs, then
loads through package-private GameFiles(dir,legacy). The provided1257 folder
is never written. The loaded m133 values match the brief: desk9861.906318872,
cash-17366.424253986, window12142.809656253, trailing revenue1998.942287585.
Original continuation m134-253: remittance10134.551266349, revenue48341.489897126,
share20.9644992%, six further bank failures, worst audit1.02318153949e-10.
Its first12-month share was46.5852538%. Log: target/CBK3-evidence-before.log.

First-mends repaired continuation, the same120 months and saved settings:
remittance476.704248490, revenue33741.426003542, share1.4128160%, three further
failures, worst audit1.95086613530e-10 save units. Every month passed the
unchanged cent bound, collateral ceiling and exclusion of equity inventory
from window financing. Final window1846.310417314; inherited desk3443.455032691 remains at
market. Log: target/CBK3-evidence-final.log; launcher target/CBK3-part1-final.txt.

Provisional observations2.94%,8.78% and4.26% were superseded while correcting
the funding-price/currency link and adding stable funding. They were never
remittance targets. Their scratch logs remain for the build trace. Only the
first-mends run above is that candidate's model measurement. The P3 floor
eligibility mend changes the continuation again; the second-mends measurement
below supersedes its final-model status.

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

## Bank health on the matched copied city at first mends

The new desk-chat health request is measured by the same standalone source,
tools/CBK3CopiedCityMetricsCheck.java, on both models. The original is rebuilt
in target/CBK3-original-metrics-352 at352aaf3b2: versus b2f50a9ef the only
changes are AllChecks, the initial BankRulesCheck and notes, no model source.
Only its observer class is copied ahead of the first-mends model on the
classpath; no old Bank/Game class shadows that model. Both runs use the same
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

First-mends named verification and local maps/fast completed on a6addf7f8.
Its provisional ship/LE1 row was pushed for stranger money/save review.
Stranger SHIP is the final ensemble go: merge main first, compare the newest
baseline and matching bank windows; M1/F1 stop a ship. ALL needs a separate
orchestrator dry-run placement go. No final readiness, candidate ensemble or
full gate reuse is claimed here.

## REV-CBK3, second mends (2026-11-27)

The orchestrator's `20261126-215827-989` reports REV-CBK3 on
`a6addf7f8`: the cause, money, reality, saves and checks hold, with P1 on a
loosened capital rule; P2 and P3 below also requested before the ensemble.
Merged AP7/main `30e99ac31` at `73c743ed7`, retaining its BuildAdvice and
WorksValue changes. The three generated-index conflicts take our side and
are regenerated by the final fast check. RES3 has not yet shipped. Its
reserves-capped `CentralBank.repayFromBank` must be retained when it merges.

- **P1.** `d615099c5` floors leverage target/top scaling at the original
  8% risk minimum plus the live capital buffer. A 4% risk minimum can no
  longer make the target fall below the separate 3% leverage floor. The
  regression causes an insured mortgage bank at 2.7% equity, with and
  without L8's conservation buffer: no lending growth, no insured exposure
  headroom and no capital price below the leverage minimum. Default and
  tightened risk minima keep their mathematical values. The literal scale
  rounded `.03 * .08 / .08` to `.029999999999999995` without a buffer:
  the new strict test caught it, and both target/top now explicitly max with
  the independent minimum. ★ Top's baseline also includes
  `MANAGEMENT_CUSHION`, so loosening risk capital cannot collapse the leverage
  management band. Both 4% fixtures require top strictly above target.
- **P3.** Helper `CBK3-floor`, committed memory 54.03% before starting,
  built `20f836d26` on its own worktree; reviewed and cherry-picked as
  `e90aaeba8`. All 22 company-index eligibility predicates now use
  `atFloor(c)` / `!atFloor(c)`. Nominal seeding and denomination stay as
  they were. Its new ExchangeCheck fixture doubles the real valuation
  floor above the nominal minimum, causes mature zero earnings/book/spare,
  excludes actual orders and household sales, then causes positive earnings
  and an actual household-to-desk recovery sale. No old fixture, tolerance
  or finding was removed. The helper ran no JVM and ended before checks.
- **P2.** The monthly LongPlaytest audit now reads actual outstanding window
  advances, haircutted eligible collateral times the saved window limit,
  gross wholesale need and the equity inventory. Both funding laws name a
  finding when violated. ★ The mechanic count is a real posted bid whose
  stable cash room is strictly less than every other desk limit; an empty
  month-end budget or a UI preview is not evidence that the mechanic fired.
  A fifth existing `BOUND_` statistic separates stable cash from capital.
  Bank's pure capital-room calculation is shared; posting and fills still
  take the same minimum of risk, leverage, cash, position, book and float.
  The last/lifetime count appends to Exchange.State's existing `last` and
  `life` arrays, crossing DataSave/load, reset and month reset. Prefixes
  without it read zero; no money changes meaning and no format bump is due.
  ExchangeCheck causes this limit and tests preview purity and both resets;
  BankRulesCheck.saveChecks, also invoked by SaveFileCheck, tests nonzero
  counts through real writer/load and native-format-66 missing suffixes.
- **Queued, not implemented.** BFW1 is free on `origin/main 30e99ac31`
  (`tools/batch-id.sh`, no existing row/brief/branch). Its ship-file row and
  brief require the bank's own wholesale credit spread to widen as capital
  thins, new market access closed in resolution, and an explicit maturity /
  repricing law retaining existing debt. This is the review's follow-up to
  the sovereign 12-month quote, unlimited supply and monthly repricing.

Helper `CBK3-floor` traced the two new test reds without a JVM. The leverage
red was the one-ULP floor violation above. The recovery sale's net
0.988131313 versus gross1 was exactly capital-gains withholding; no capacity
or conservation defect. Its regression keeps positive earnings, proves
sufficient resting/live depth, and reconciles actual net proceeds plus
actual tax to the gross dollar at the unchanged 1e-9 tolerance. No tax
formula was copied and no existing assertion changed. A duplicate diagnostic
launch was cancelled after the delayed original helper resumed; both ended,
with no edits/JVM in the duplicate, before further checks. The original
helper's source and diagnosis were reviewed; the builder made all decisions,
records, fixes and commits.

Final second-mends copied continuation (same native month133, same120 months,
same saved settings): remittance786.781165186 / revenue40344.289190537 =
**1.9501674%**, versus original20.9644992%; **five further bank failures,
versus original six**. Every month's eligible-collateral and desk-exclusion
checks passed; worst absolute money residual8.95852281246e-11 save units,
below the unchanged cent bound. Final window0; desk1145.719222374;
cash-15958.477965276. The extra floor skips affect actual participation, so
this replaces first-mends1.4128160% / three as the final-model continuation;
the first-mends bank-health table remains explicitly pinned to that older
candidate and is not a second-mends observation. Final named log:
`target/CBK3-second-BankRulesCheck.log`; launcher/result:
`target/CBK3-second-BankRulesCheck.txt`. The earlier literal-formula attempt
had exactly the two one-ULP leverage assertion failures; the final identical
120-month evidence and all rules sections pass (0 failures).

Named verification on production `f853c05e2` (maps `2130668ab`):
BankRulesCheck with the full copied120-month evidence passes; ExchangeCheck
and DebtExchangeCheck pass; BankCheck's exact named entry passes (the
wrapper's substring would also select the already box-checked CentralBankCheck
and YoungBankCheck, so this entry runs just BankCheck, soft timing, serially);
MonetaryCheck passes. BankFailureCheck passes24x120 =2880 monthly money/equity
audits, with finite equity throughout, four failed-bank seeds of24 and310
splits/consolidations (first-mends observation was five of24). Logs are
`target/CBK3-second-<Harness>.log` and wrapper results `.txt`.

SaveFileCheck initially found one new setup failure: the withdrawal had zero
FUND asks and zero shares sold. Helper mail `20261127-005900-497` traces its
`fair > 0` purchase premise and `tradeForCheck` to a presentation-floor lot
that the real `postFund` rightly refuses to offer. The raw log cannot identify
which company or whether it reached the floor during the intervening month;
that inference is not presented as a proved company-level trace. The fixture
must cause saleable economic value at the selling phase and actual FUND
proceeds, keeping its preferred, ledger, withdrawal and save laws. Helper `3fa8c9dbb`, reviewed and cherry-picked as `712794e9d`, isolates
that withdrawal with native earned records, real WORLD/FUND fills, native
transfer phases and both cash counterparts. The played preferred/ledger/save
premises remain. The ask exceeds ordinary rebalancing, actual filled quantity
and proceeds reconcile, and the later payment equals only those proceeds.
ExchangeCheck now also causes a real funded lot to become worthless: no FUND
ask, no world fill or cash, and the debt/holding remain. All original
household zero/refusal and recovery/tax assertions remain. SaveFileCheck and
ExchangeCheck/DebtExchangeCheck pass (exit0). P3 is not reverted and no
known-red row is added for the corrected setup.

The optional `-Dplaytest.months=240` probe observed241 audited months and zero
stable-budget-bound months; its three household mean-own-line end findings
are 5720/1050/183 ppm versus bounds5000/500/30. TARGET_MONTHS explicitly calls
this a mechanic trace, and those bounds were calibrated over2400/4000 months.
No bounds or fixtures were changed and no known-red row was made for this
nondefault horizon. The unchanged DEFAULT2400-month LongPlaytest then ran
2404 primary monthly audits: its household end bounds pass, but it found one
owned funding-law failure at month395: window16958.847845573 versus non-desk
need14852.450210271. No eligible-collateral breach; zero months whose stable
cash budget independently bound a posted desk bid. The zero is reported as
measured, not claimed to show positive firings. Native test sections pass;
AllChecks correctly returns4 for the one funding finding. Log:
`target/CBK3-second-LongPlaytest-after-actions-red.log` (retained before
rerunning). Its cause was delegated before any mends; no tolerance is relaxed and
no main/known-red row is used. The source and real-law result follows.

The m395 finding was an **audit-phase error**. Helper `CBK3-floor` mail
`20261127-011804-267` located the month-end CB/foreign settlement before
MoneyAudit, but LongPlaytest then accepts preferred capital (or presses the
resolution button) before its broad audit. Those real capital receipts change
cash and recomputed need without buying equity inventory or taking a new
advance. The retained log has no per-action par, so exact numerical attribution
to m395's particular acceptance is not claimed. Fresh helper `CBK3-settle`
(50.23% committed before launch), mail `20261127-012818-884`, independently
verified this phase error and the correct completion/fallback callsites.

**Reality checked again, not an exemption.** Primary credit has a stated
maturity, and borrowers can elect early repayment; a cash receipt alone is not
an automatic repayment transaction. OC10 5.1 retains demand/acceleration rights;
5.4 permits prepayment without penalty. See the [Federal Reserve's primary
credit terms](https://www.federalreserve.gov/monetarypolicy/discountrate.htm)
and [OC10, sections5.1/5.4](https://www.frbdiscountwindow.org/~/media/documents/oc10.pdf),
read 2026-11-27. Inference for this monthly model: strict new-credit/rollover
caps do not erase already owed advances when capital arrives or the player
tightens a dial. This is the same old-claim/next-rollover law already stated
under saved rules and in the player text.

★ Extract **only the newly added funding predicate** to
`LongPlaytest.auditBankFunding`: identical getter reads, inequalities, finding
tags and cent bounds. Call immediately after the native simulation returns a
completed month, before GovernmentPlayer/resolution/preferred actions, and
after a successful fallback press. The returned completed count excludes a
thrown partial month even if its calendar moved. Keep every pre-existing
broad money, household and late-write audit after player actions. A separate
count verifies exactly one funding audit per completed month, including an
overshoot of the target. No production repayment, money creation/retirement,
foreign flow, cost snapshot, save claim or default behavior is changed by this
correction. The rejected alternative, automatically retiring CB loans at each
capital button press, would mutate M0/foreign flows after the frozen audit and
before the next month's opening/reset, hiding those flows from both strikes.

New BankRulesCheck3b causes the counterexample with actual treasury-funded
preferred acceptance, the real resolution button and a tightened window dial.
It proves no extra inventory/CB issue, truthful unchanged old debt and foreign
stock before maturity, refused fresh credit against the smaller live cap,
and no extra desk purchase. The next native CB/foreign rollover meets all
unchanged caps, settles the real foreign remainder, and reconciles actual
retirement plus signed foreign principal to the reduced funding need at the
cent. Charged funding costs and their saved mix stay unchanged. Finally a
fault-injected uncapped lender produces BOTH original funding findings on an
invalid closed balance; the audit has not been disabled or softened. The final native cases derive equity/deposits from the model's own minimum
and stable-funding shares; all three pass. Principal reconciliation reads the
actual change in funding need, allowing any excess capital to stay liquid
rather than assuming every dollar retires wholesale debt.

Merged main's queue-only `c71984995` at `ab38709b`; model source is unchanged.
RES3 has still not shipped into this branch. Final second-mends source is `8133c12d2`; production banking remains
`f853c05e2`. The final DEFAULT2400 LongPlaytest passes (exit0): **2404
completed months, 2404 funding audits**, no findings, every month audited and
every reload matched, followed by all native audit fixtures green. Stable
cash independently bound a posted bid in **zero** months; no positive default
firing is invented. The forced cash-budget fixture in ExchangeCheck still
causes its positive binding and the saved counter. Final log:
`target/CBK3-second-LongPlaytest-full.log`; wrapper `.txt`.

Final BankRulesCheck passes (exit0), including all three native rollover
cases, both deliberate closed-balance findings, exact principal/cash laws,
unchanged cost/mix, nine dials/defaults, saved/missing fields and cold callable
prefix laws. Log: `target/CBK3-second-BankRulesCheck-laws.log`. Final
`--fast` passes (exit0), zero firm Stale findings,209 informational soft;
`target/CBK3-second-final-checks.txt` records both statuses. Maps were
regenerated from this final source. SaveFileCheck and the expanded
ExchangeCheck/DebtExchangeCheck pass on `712794e9d`; subsequent code changes
are the funding-audit checkpoint and its new native regressions only, with
no production/save-law changes. BankCheck exact, MonetaryCheck and
BankFailureCheck are green on the unchanged production model as recorded
above; the full CentralBankCheck box result remains pinned in its earlier
section, with final ALL verification owed at the scheduled dry run.

`origin/main c71984995` is merged; RES3 has not shipped. Its reserves-capped
repayment must still be retained when it merges. Ship records parse cleanly;
BFW1 remains free on main. Pick is **ALL260**, review required for money/saves.
Push the provisional marker for REV-CBK3's second look, then end the turn.
Stranger SHIP is the final ensemble go, queued behind RES3, with main merged
first and replay/published baselines compared. M1/F1 stop a ship. Then ask
for the ALL dry-run slot and wait. No final ready marker drop, candidate
ensemble, dry run or full gate reuse is claimed. All helpers and named jobs
have ended before this turn boundary.

## Second review SHIP and ensemble (2026-11-27)

Orchestrator `20261127-020736-897` reports REV-CBK3 **SHIP** on
`4ffbb65b2`: P1 holds for every dial including the zero-buffer ULP floor;
P2's2404/2404 timing and deliberately broken-lender finding hold; P3 is the
intended default change (floor firms leave participation and fund weights
stop using stale prices). The review accepts the copied-city1.95% against
Fed2021's approximately2.65% reference. It explicitly authorizes the final
ensemble beside RES3's dry run; RES3 still ships first.

Started the standard box runner at `4ffbb65b29166be07e292a7716659efb36536afd`,
main `c71984995` already merged, label `CBK3`, all80 policy/seed runs,
eight at once. No laptop model JVM is running. Box data uses its main-data
autodiscovery, not Windows-path overrides. The caller is
`target/cbk3-final-ensemble.sh`, output `target/CBK3-ensemble.txt`; remote root
`/home/jerus/cbs/tools/4ffbb65b29166be07e292a7716659efb36536afd/target/ensemble/CBK3`.
`BASELINE=docs/ensemble/CBK3-before.md` supplies the approved augmented replay
of published0.9.212: old criteria/verdicts are identical to that published
baseline; the replay adds real remittance/revenue/failure observations. No
criterion, threshold, seed, policy or input is changed.

Helper `CBK3-metrics`, committed memory50.64% before launch, prepares only
an untracked existing-data comparator and baseline self-comparison. It uses
policy/seed/common observed months, summed flows rather than average monthly
ratios, recorded cumulative failures rather than findings/equity, and exposes
full versus common horizons and missing data. It runs no JVM, edits no model,
input or scorer, and returns a bounded report. Final results and the hard
M1/F1 verdict will be recorded after all runs return.

At 02:31 Halifax, the standard runner yielded to the nightly after finishing
64 runs (AUTO2, ZERO_SLACK, ZERO_HOT and SHOCK); the 16 HIGH runs remain.
The caller stays connected and resumes automatically. No priority bypass,
replacement run or laptop JVM is used. A read-only copy of those completed
CSVs is kept in `target/CBK3-complete-four-policies/`; this is partial evidence,
not the runner's final score.

Helper `CBK3-failures` (49.56% committed memory before launch; no JVM or edits)
diagnosed those completed pairs and ended at 02:56. In the matched windows,
recorded failure episodes fall from 242 to 201 overall, but 18 pairs increase,
31 decrease and 15 tie. Two previously failure-free ZERO_HOT seeds now fail:
seed 4 once at month 176, and seed 6 five times at months 132, 133, 135, 300
and 312. The SHOCK increases begin before its month-601 shock. Lower pooled
remittance shares and a lower aggregate episode count do not satisfy the
brief's no-new-failures aim on those pairs.

The helper traced `resolveIfFailed`, `equity` and `markToMarket`: the failure
trigger and equity/mark accounting are unchanged from the retained control.
All 64 candidate runs report clean monthly audits and zero findings. The new
foreign interest expense and the deliberate removal of worthless firms from
participation change income and subsequent paths; the observed rate, deposit
and capital paths diverge before several extra failures. The CSV omits the
intra-month loss/rescue phase, equity/assets/securities and wholesale costs,
so this does not assign individual losses or prove each extra episode valid.
No new accounting or loan-pricing defect was identified. The bank's own
funding spread and market closure in resolution remain the explicitly queued
BFW1 limitation. Report: bus `20261127-025627-942`; paired data and diagnostics
in `target/CBK3-four-policy-comparison.json` and `.md`.

★ Keep the evidence-backed funding and worthless-price corrections, retain
all paired regressions in the final comparison, and do not invent a credit
subsidy, remittance cap or weaker audit to hide them. Only the completed
80-run M1/F1 result decides the ensemble's shipping stop under contract 4;
the failure-count aim and the diagnostic limits must still be stated plainly.

### Final 80-run verdict

The standard runner resumed at approximately 03:12 and finished at 03:14
Halifax, exit **0**. All 80 runs are complete, none missing or short; all
64 pre-switch twins match. **M1 and F1 pass in all five policies, 10/10 hard
criteria**: zero failed or unread monthly money audits and zero LongPlaytest
findings. The same cent-level audit law remains active. Overall score is
**24/26**, exactly the replay and published **0.9.212** score: A1/A2 remain
informational FAILs and H1 remains retired. All 28 criterion IDs, classes,
thresholds and verdicts match both baselines; 17 measured readings change.
No criterion or finding allowance was weakened. This is the ensemble's
formal PASS, not a claim that no bank failed.

`docs/ensemble/CBK3.md` records the standard output, identical except for the
two publication headings changed to `{next}`. Its embedded JSON matches
`target/ensemble/CBK3/summary.json`. The source remains `4ffbb65b2`, main
`c71984995`, snapshot version 0.9.215, gatebox OpenJDK 21.0.13, named
`earth-2.bin` and `world-1.bin`; record commits change no model. Raw CSVs,
snapshot, progress, times, logs and scores are retained locally under
`target/ensemble/CBK3/` and on the box at the run root above. This is not a
gate dry run or a reusable full-suite result.

The following shares are **sum of actual remittances / sum of actual budget
revenue**, pooled over the 16 runs of each policy. They differ from the
baseline table's median of each run's full-window ratio. Before is the
approved exact 0.9.212 replay; the published baseline itself had no bank-flow
columns. Counts include rescued bank-failure episodes, not F1 findings.

| Policy | Before share | After share | Failure episodes before / after | Runs with failures before / after | Maximum episodes before / after |
|---|---:|---:|---:|---:|---:|
| AUTO2 | 0.061122288% | 0.024612297% | 80 / 62 | 16 / 15 | 8 / 11 |
| ZERO_SLACK | 0.194778204% | 0.075479752% | 67 / 48 | 16 / 15 | 8 / 7 |
| ZERO_HOT | 4.668742325% | 1.732505571% | 45 / 40 | 14 / 15 | 7 / 6 |
| SHOCK | 5.877349694% | 1.979491676% | 50 / 51 | 16 / 15 | 7 / 10 |
| HIGH | 2.432930655% | 0.529450037% | 47 / 43 | 16 / 15 | 5 / 7 |
| All 80 | 0.111123032% | 0.040760984% | 289 / 244 | 78 / 75 | 8 / 11 |

Full and paired common windows agree for every seed: AUTO2 months 2–2400,
ZERO_SLACK 2–2040, ZERO_HOT 2–402, SHOCK 2–840 and HIGH 2–720. Both models
have 102,352 observed rows, no gaps or horizon mismatch, complete finite
required flows and valid cumulative counters. Nonpositive-revenue monthly
ratios are unavailable (114 before, 143 after); no monthly-ratio averages or
invented currency conversion are used. These different policy horizons and
shared prefixes make the per-policy comparisons necessary beside the all-80
pooled ratio. Denomination and stable-budget-binding counts are not CSV
fields; no ensemble count is inferred. The completed-month LongPlaytest's
recorded 0 binding months and the native positive binding fixture remain
the evidence for that counter.

**The no-new-bank-failures aim is unmet on individual pairs.** There are 23
increases (+39 episodes), 39 decreases (−84), and 18 ties: net −45. Five
formerly failed pairs become failure-free, but ZERO_HOT seeds 4 and 6 change
from 0 to 1 and 0 to 5. Every increase, seed: before→after, is retained here:

| Policy | Increased pairs |
|---|---|
| AUTO2 | 10: 7→11; 11: 1→2; 15: 1→2 |
| ZERO_SLACK | 11: 1→2; 12: 4→5; 15: 1→2 |
| ZERO_HOT | 2: 2→4; 4: 0→1; 6: 0→5; 10: 4→6; 13: 2→4 |
| SHOCK | 2: 6→10; 7: 2→3; 10: 2→3; 11: 1→2; 12: 4→5; 13: 2→4; 15: 1→2 |
| HIGH | 2: 4→7; 7: 2→3; 10: 2→3; 11: 1→2; 12: 4→5 |

The earlier source diagnosis found no new accounting or pricing defect; it
does not establish the loss mechanism of each added episode. Do not call
these noise, erase them behind the aggregate improvement, or equate clean
F1 with no failures. BFW1 remains a concrete funding-model limitation, not
a proved explanation for these episodes. Contract 4 makes M1/F1 the only
ensemble hard stops; both hold. Report the unmet aim with the PASS to the
orchestrator and request the ALL dry-run slot, without starting it.

Helper `CBK3-metrics` resumed at 46.79% committed memory, independently
cross-checked all 160 CSV hashes, runner output and baseline identity, and
ended at 03:20 (bus `20261127-032021-107`). Its comparator self-tests and
baseline-versus-itself proof already passed. Final comparison artifacts are
`target/CBK3-comparison.json` and `.md`; final runner log is
`target/CBK3-ensemble.txt`. Both diagnostic helpers and the ensemble caller
have ended. Ship-file row validation exits 0; the readiness marker stays
provisional. No final ready claim or dry-run authorization is inferred.

After scoring, merged main `c52ad5136`: its only addition since `c71984995`
is the nightly's HA1 timing record in `docs/queue/HA1.md`. No model, data,
scorer or ensemble input changes; the measured source remains `4ffbb65b2`.
RES3 has not shipped at this merge.

At RES3's later merge, retain BOTH its reserves-capped window repayment and
its P1 pricing of the advance left owed on the bank and central-bank books.
Do not erase unpaid debt or retire reserves the bank has not got. The
ensemble does not authorize a dry run: ask `CBK3 dry run?` after the verdict
and wait for the orchestrator's slot.

## Solvency replay requested while RES3 mends (2026-11-27)

Orchestrator `20261127-032425-506` acknowledges the PASS and unmet aim, keeps
the dry run after RES3's ship, and explicitly authorizes four sequential
laptop replays: ZERO_HOT seeds 6 and 4, current main against CBK3. The question
is whether good collateral liquidity was refused to a solvent bank, rather
than whether its ratio looks healthy after a rescue. A CBK3 liquidity defect
must be mended here; insolvency or a demonstrated BFW1 defect must be recorded
with evidence. Any model mend moving the default trace requires a new ensemble.

★ Use main `c52ad5136` and the measured CBK3 model `4ffbb65b2`, the original
402-month policy horizons and actions, scratch instrumentation at the actual
failure/funding phases, and common-column equality against the retained CSVs
to check that observation does not perturb the model. Delegate the trace to
`CBK3-failures` in its own worktree, with a separate control worktree; committed
memory was 50.49% before dispatch. The helper alone owns the replay slot, one
compiler or model JVM at a time; the parent runs none. No save slots or live
saves, production changes, fixtures or scorer changes are authorized for the
diagnosis. Parent keeps the defect decision and records.

Reality checked again on 2026-11-27:

- The [Fed's primary-credit guidance](https://www.federalreserve.gov/monetarypolicy/discountrate.htm)
  provides ready liquidity for generally sound institutions with pledged
  collateral, and does not restrict how primary-credit proceeds are used.
- [Governor Bowman's Bagehot discussion, 2024-04-03](https://www.federalreserve.gov/newsevents/speech/bowman20240403a.htm)
  distinguishes secured liquidity support to solvent institutions from the
  resolution of failed banks. A funding ceiling is not a reason to deny a
  sound institution liquidity that its good collateral supports.
- The [Fed's collateral valuation policy](https://www.frbdiscountwindow.org/pages/collateral/collateral_valuation)
  still makes assigned collateral value determine borrowing capacity and
  applies valuation margins. Bagehot is not authority to drop the haircut
  ceiling or make the equity desk eligible.
- [RBA Governor Stevens, 2008-04-15](https://www.rba.gov.au/speeches/2008/sp-gov-150408.html)
  cautions that an excessive penalty which makes the asset/funding interest
  spread negative can hasten insolvency. Therefore the trace must identify
  the loss and expense that cross zero, not merely repeat the negative-equity
  failure trigger. Opening solvency, funding available, actual bills and
  pre-rescue marks/provisions all matter.

Column clarification from the helper (`20261127-033616-871`): the existing
ensemble column `cb_advances` reads `getAdvancesToTreasury`, not
`getAdvancesToBank`. Its phase-1 values must not be treated as bank-window
claims. The remittance/failure comparison is unaffected. The phase replay
reads the actual bank claim, collateral capacity and foreign settlement
explicitly; no bank-funding conclusion is inferred from that CSV column.

### Replay result and limits

All four authorized laptop JVMs ended, sequentially, at the full month-402
horizon: main `c52ad5136` has no failure in either seed; CBK3 `4ffbb65b2`
has five in seed 6 and one in seed 4. The observer wraps bank/funding/mark
mutations and reads the pre-rescue state; it does not change a policy,
price, fixture or model mutation. The driver uses `TempDirs.create` and a
private `GameFiles` pair, with the required C:/ earth/world settings and
`cbs.parallel=1`. Parent reviewed the patch/probe: wrappers preserve the
original bodies, reflection reads values and writes only diagnostic output.
No production patch was taken from the helper.

Both candidate runs and control seed 4 match **all 401 × 81 common retained
CSV cells exactly**. Control seed 6 matches every cell through month 342;
585 cells differ in 58 later months beginning at 343, initially in a
cumulative flow by about 2e-13 and later spreading to other observations.
The local JVM is 21.0.4+8 versus the retained box's 21.0.13, and main differs
from the older retained source. No attribution to JVM, source or observation
is established, and full control-6 neutrality is not claimed. Its entire
failure-relevant prefix is exact, and the four replays reproduce 0→5 and
0→1. No tolerance or observation was changed to hide the late drift.

**No uncovered rollover or liquidity refusal is demonstrated.** All 2,406
scheduled funding snapshots cover wholesale need exactly: 802 per candidate
seed, 401 per control seed. Existing claims can differ from current need
between settles; those are still debts, not evidence of a refused advance.
At the six pre-rescue calls, the bank is below zero book equity and its
capital growth/headroom is zero. The collateral ceiling is above each
current window request. New borrowing is not denied against available
collateral at a positive-equity funding close.

Figures below are native money units rounded to six decimals. The committed
[full-precision event CSV](CBK3-failure-trace.csv) retains assets/liabilities,
funding and deposit operands, stable funding/desk capacity and equity bridges.
Control equity is read at the matching failure-call ordinal, not after the
candidate's rescue.

| Seed / month | Control equity | Candidate equity before rescue | Minimum equity | Window request / eligible collateral | Foreign principal | Stable desk room |
|---|---:|---:|---:|---:|---:|---:|
| 6 / 132 | 3532.802798 | −111.132177 | 817.658289 | 0 / 4879.131474 | 2524.069665 | 0 |
| 6 / 133 | 3538.118193 | −1592.828771 | 421.399438 | 0 / 2027.588222 | 963.266299 | 0 |
| 6 / 135 | 3149.520322 | −125.078031 | 272.668882 | 0 / 2034.639813 | 0 | 2130.918047 |
| 6 / 300 | 8727.454124 | −551.278877 | 5947.821952 | 13910.918206 / 35044.496572 | 46595.469645 | 0 |
| 6 / 312 | 10171.663183 | −10660.351470 | 5453.178437 | 25445.906003 / 25980.028625 | 40078.946421 | 0 |
| 4 / 176 | 4210.914986 | −130.714052 | 1632.356563 | 11486.795467 / 28769.666880 | 14940.968042 | 0 |

The loss sequence explains more than the failure trigger:

- **6 / 132:** securities mark −445.314946 takes equity 363.164622 to
  −82.150324; subsequent operations leave −111.132177. The 17.960846
  funding bill left positive 363.138865 before the mark.
- **6 / 133:** net provisions 2847.125874, including Wood Products writeoff
  2879.852311, dominate net income −2819.319278 against opening equity
  1226.490506. Equity is already −1586.164497 before funding; its 6.664275
  bill deepens the deficit to −1592.828771.
- **6 / 135:** mark −448.546761 takes positive 330.183980 to −118.362781;
  the final pre-rescue figure is −125.078031. Wholesale need and its bill
  are both **zero**. This failure cannot be a denied funding rollover.
- **6 / 300:** opening equity 8905.458074 meets net provisions 8636.183005
  (Wood Products writeoff 8722.811932). Funding 552.876824 leaves positive
  231.495331; a later mark −841.242433 crosses 290.111456 to −551.130977,
  ending −551.278877 before rescue. Adding back the whole funding bill on
  this fixed path gives +1.597948, but foreign interest alone still leaves
  −119.087550. Funding is contributory; this arithmetic is not a replay
  counterfactual and does not prove a BFW1 or CBK3 pricing cause.
- **6 / 312:** net provisions 18095.427074, including Copper Refining
  writeoff 18490.243080, overwhelm opening equity 7718.999356. Equity is
  already −9921.284721 before funding; the 739.066749 bill deepens it to
  −10660.351470. This is an asset-loss insolvency in the measured book.
- **4 / 176:** opening 1684.624935 + net income −85.317169 + a retained
  `discountRestruck` loss −1730.021818 = −130.714052 (bridge residual
  about 7.7e-12). No dividend, buyback or preferred outflow explains it.
  A final securities mark −183.256804 crosses 53.066644 to −130.190161;
  the funding bill 8.576881 had left positive 81.579278 before later trading.
  The large discount entry has the inherited accounting limitation below;
  it must not be certified as an economic loss from its field name.

Deposits and the desk differ substantially between the two paths. For
example, seed 4's household deposit book at the matched call is 396.483843
versus control 11060.232307. Those levels do not establish the timing or
cause of a deposit run. The complete phase/deposit operands are retained;
despite these differences, every scheduled funding need is met. The desk
has no stable room in five cases, but seed 6 / 135 has 2130.918047 and still
fails from its existing securities mark with no funding need. Capital, desk
and deposit constraints must not be substituted for the actual loss bridge.

### Inherited premium accounting: BPM1 queued

The helper localized seed 4's −1730.021818 entry to the primary-settlement
interval, event sequence 155685→155686. The asset is **domestic treasury
paper**, separate from the equity desk. `Debt.reopen` records signed face
minus proceeds until settlement. `Debt.settled` then floors the bank's own
unearned discount at zero. `Game.restrikeBankDiscount` recognizes
delta(bank face) − bank cash paid − delta(bank unearned discount), so a
premium beyond the bank's own remaining discount becomes an immediate
retained loss. The later cash/book refresh realizes the balance-sheet change.
The related issue/settle/re-strike method bodies are identical in candidate,
current main and the retained source. BK2 notes, call ★1, explicitly chose
the floor while leaving premium carrying to its own batch.

The [OCC's August 2026 accounting guidance, 1A question 16](https://www.occ.treas.gov/publications-and-resources/publications/banker-education/files/pub-bank-accounting-advisory-series.pdf)
requires premium amortization as a yield adjustment, with applicable
call/prepayment exceptions. Purchase above par is not itself a demonstrated
economic loss of that premium. The trace does not record individual auction
line/coupon/yield/face/cash operands, so it cannot prove overpayment or
certify this bank's economic solvency after correcting its carrying basis.
It proves book insolvency, full rollover funding and an inherited accounting
concern; it does **not** prove six sound banks or six valid economic failures.

★ Queue **BPM1**, id checked free (exit 0), in the ship file with its own
brief: carry and amortize purchased premiums, cause the above-par fixture,
retain the conservation coverage, and check the saved meaning. This is
main's existing bond-cost basis, distinct from BFW1's credit spread/market
closure and from a CBK3 refusal of good collateral liquidity. Do not add a
funding subsidy or change a collateral/price/audit fixture here to conceal
the loss. No CBK3 model mend is supported by these six funding/loss bridges;
the production model and the final ensemble remain `4ffbb65b2`. Other
paired failure increases remain untraced, not certified by these two seeds.

Kept evidence, paths relative to this worktree's root:

- `R = ../codex-CBK3-failures/target/phase2`: `failure-bridges.csv`,
  `paired-prefail.csv`/`.json`, `proof-and-summary.json`,
  `premium-source-evidence.json`, `seed4-first-discount-booking.json`,
  `diagnosis.txt`, `commands.log`, `replay.sh`, `manifest.json`,
  `instrumentation.patch`, `CBK3FailureProbe.java` and `Ensemble.java`.
- `S = ../codex-CBK3-failure-control/target/phase2`: the control patch;
  the exact commands for both models are in R. Both R/S retain
  `ZERO_HOT-{6,4}.csv`, `.log`, `.csv.jvm.txt`,
  `.diagnosis.json`, `.funding-coverage.csv` and full `.csv.events.csv.gz`.
  R's `event-archives.json` verifies decompressed byte hashes.
- [The committed manifest](CBK3-failure-trace-manifest.json) records both
  source commits, probe/driver hashes and patch hashes. Parent verified the
  patch SHA256s, `e32bc6fd…a8c1b77` and `19bac4e2…e75056d`; the committed
  six-event CSV changes line endings to repository LF only.

Helper's bounded report is bus `20261127-040212-759`; all replay jobs ended,
the analysis turn ended at 04:02 and its final acknowledgment at 04:03.
No JVM remains. Report the finding in a few lines and wait for the
orchestrator's dry-run go after RES3; no new go is inferred. The event CSV
was checked equal to the measured source except LF normalization, the
manifest parses, `git diff --check` passes for these record edits, and the
ship/queued-row validator exits 0. Prior model checks and the 80-run
ensemble are not rerun for these records-only additions.

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

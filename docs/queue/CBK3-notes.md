# CBK3 - the window against collateral and the bank's rules as dials

REV-CBK3c reports SHIP on3e938553b, retaining all42 original CreditCrunch
labels and adding8. The renewed box dry KODIcBIM completes **gate exit0**
on24ee53f59/main39fcbee71, with only14 known-main red harnesses. Its source
contains the approved native fixture mend and the complete ASH1 merge.
The accepted80-run CBK3-final ensemble is PASS, M1/F1 hard10/10; its recorded
failure-aim shortfall and observation limits remain. The failed older dry
runs are not reused. Base
b2f50a9ef, 0.9.213, format66; RES3's compatible format67 is now inherited.
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
two publication headings changed to `0.9.219`. Its embedded JSON matches
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

## RES3 merge and compatible format 67 (2026-11-27, in progress)

Orchestrator `20261127-052130-016` orders main `6bd00e350` merged after RES3
shipped as 0.9.216, save format/floor 67. Keep its reserve-capped repayment
and price each actual held window dollar once, then run the named bank
checks. AP8 gets the box first and is exact: merge it when published, with
no remeasurement for AP8 itself, before asking for the dry-run slot.

The source conflicts are resolved, preserving RES3's housing/rent changes.
The mortgage search retains its per-size rent forecast and now passes the
same saved BankRules through every slice and the live lender-reading path.
The fund save fixture keeps CBK3's actual valuable lot and sale, and RES3's
assertion that its actual till is emptied. No played fixture is weakened.

★ The merged funding bill reads actual pre-rollover window and foreign
principal, prices each at its own rate once, and keeps the deferred-window
expense a subset, not an additional bill. Principal rollovers still happen
twice, with only one monthly bill/close and lagged funding-price observation.
The reserve cap means old issued principal may remain owed after the target
falls. Monthly funding reads distinguish that recorded deferred claim from
the active tranche: active principal still meets both collateral and desk
limits, the deferred amount may not exceed actual debt, and no debt is erased.

A necessary merge finding: RES3's `chargeWindow` advances a reserve shortfall
then retires its full bill. CBK3's collateral gate can refuse that advance at
full collateral, so the unchanged combination could take M0 negative. An
internal issuance bypass would instead lend past collateral. Both are wrong.

★ Keep unpaid interest as a distinct central-bank receivable and matching
commercial-bank payable. Accrue the full bill once; collect only actual
reserves, with a new advance still subject to collateral. Only collection
destroys money and debits bank cash. Later payment reduces cash and payable
equally, with no second expense. The
[Fed's January 2026 Financial Accounting Manual, 3.95](https://www.federalreserve.gov/aboutthefed/chapter-1-balance-sheet.htm)
records accrued uncollected loan interest separately from principal and
credits earnings when it accrues. This supports retaining the claim without
pretending an unsecured monetary advance occurred. No loan forgiveness,
collection from nonexistent reserves or change to the existing remittance
policy is introduced.

The first reviewed corrections are now implemented: `windowOwedCost` is
appended to Bank's monthly save lines (old 65-cell prefix defaults to zero),
and reset clears the old due supplier before computing the next city's
opening capital. The real-save regression now also covers collection's
negative due-change flow and its sign under scaling. The nine initial
BankRulesCheck failures were unfunded/stale-cap fixtures, including a probe
that issued credit into the live conservation fixture. They now establish
actual funding first, isolate the raw-collateral probe, use the real desired
tranche at both rollovers and retain any reserve-capped old claim explicitly.
BankRulesCheck exits **0**, including both deliberately broken-lender findings.
The four first exact BankCheck failures were standalone fixtures billing a
prospective foreign need with no actual foreign principal. They now establish
native window/foreign funding before the bill, retain the frozen mix and
assert the actual per-stock expense and full funding identity. Exact BankCheck
exits **0**; no phantom funding fallback or tolerance change is introduced.

The new due stock is appended to the central-bank save array after RES3's
deferred principal. Its signed accrual-minus-collection flow is an appended
Line, saved for current and last month; the balance-sheet row is appended
too. Reset/scaling and both balance sheets include the claim. The bank
reads one authoritative supplier, with no duplicate independently saved
payable. MoneyAudit's monetary debit and the money statement use collected
interest, while the income statement keeps accrued income/expense. Neither
the cent tolerance nor the money-conservation law changes.

The live central-bank capacity callback now supplies the collateral ceiling,
separate from cash need; both Game principal settles explicitly request the
bank's desk/NSFR-safe window tranche. This lets a due bill use spare good
collateral, while a fully lent ceiling leaves the bill owed rather than
inventing reserves. Native regressions cause that full-ceiling, zero-reserve
case with actual foreign funding, later collection, and no second bill.

★ Accrued interest also encumbers collateral for a discretionary new cash
advance or a later principal rollover. The
[Federal Reserve's Operating Circular 10, sections 2 and 6](https://www.frbservices.org/wp-content/uploads/082823-operating-circular-10.pdf)
includes accrued unpaid interest in the secured obligations. Atomic
refinancing of that already recorded bill instead uses spare principal
capacity and immediately collects the same dollars: principal plus interest
due is unchanged, issuance equals retirement, and no cash becomes available
for another purchase. Subtracting the same due claim again in that atomic
operation would count it twice. BankRulesCheck 3d causes both sides: a large
old due prevents discretionary new cash, while a solvent bank with sufficient
spare collateral refinances the whole smaller bill. Both pass, with exact
money and no second expense.

RES3's format/floor 67 stands. A compatible native 67 save with CBK3 keys or
appended values absent reads defaults; genuine 66 is refused, including the
original copied month-133 evidence. No restamp, conversion or loader bypass.
The existing copied-city and 80-run figures above remain truthful historical
measurements of `4ffbb65b2`, not measurements of the merged model. Actual-stock
billing moves the model, so a fresh stranger's review/final-model ensemble
must be settled before treating those records as current validation.

RES3 integration is committed as `5083cf3ba`. Orchestrator
`20261127-070326-817` clears the dry-run slot once AP8 is merged and the named
bank checks are green. Main `858378bae` (AP8, 0.9.217) merges without source
conflicts; the generated map conflicts are regenerated. AP8 is explicitly
exact and needs no separate remeasurement. Its save format/floor remains 67.

Checks so far: first `--fast` exits 0 (compile 62s, maps regenerated, 0 firm
and 210 soft Stale findings). Final BankRulesCheck and exact BankCheck exit
**0**. Logs: `target/CBK3-res3-fast.txt`,
`target/CBK3-res3-BankRulesCheck.txt`/`.log`,
`target/CBK3-res3-BankCheck.log`; the initial red logs remain separately kept.

MonetaryCheck exits **4** with one new assertion: the original seed 48213's
3%/40% held-rate pair lowers inflation by 0.718475 percentage points, below
the existing one-point floor. The source is unchanged from RES3 main; its
other grid, slope, sign, timing, supply, rent and basket assertions pass.
Helper `CBK3-monetary` first located the grocery shortage at the high rate,
then owns a narrow replay of that same pair to observe the actual supply and
credit constraints. The flag alone does not prove a legitimate effect. This
red was owned, not assigned to a known-red row. The diagnosis and law below
resolve its cause; the revised full harness now exits **0** on the AP8 merge.
Initial log: `target/CBK3-res3-MonetaryCheck-first-red.log`.

### The original monetary pair's physical supply countereffect

Helper `CBK3-monetary` replayed only the original 48213, 3%/40% pair over its
unchanged horizon. Two passively instrumented observations and a clean
same-pair control return exactly equal complete native summaries, including
floating-point bits, baskets, expected inflation, plans and the twelve
original component/rent trace lines. All 660 parent sources and 2,658 compiled
files remained unchanged. No grid, alternative fixture or passing-pin search.

The first high-rate expansion decision in the window, m64, selects a staffable
Convenience Store, but its expected monthly profit 0.016504268 is below even
the least native tested interest hurdle 0.366696558. At m69 the borrower's
asset/interest reserve clips the restock budget (3.348505158 against orders
3.414453675); bank equity 973.642658 exceeds its target 71.220755 and minimum
54.263433, and native credit/growth limits are infinite. This is not a bank
capital refusal. At 40% every window month has actual window/deferred/due/
foreign principal and their funding bill/collection all zero.

The first actual shortage is m70. After seven loss months, the ordinary
spare-capacity retirement rule sells one of five shops: its measured capacity
895.868675 exceeds 1.25 times demand 661.738417; remaining measured capacity
716.694940 exceeds its retained headroom 694.825338. The later sale's actual
operating rate is lower, 0.327350479 (road 0.355029586, staff 0.950847458,
health 0.969700425). Handover and supply are both 628.512920, below demand
661.738417, while the shelf holds 1,506.326272 baskets. Every sale m70-84 is
physically constrained with stock still available. From m73, insufficient
land also prevents replacement. More restock money cannot fix these sales.
The two phases are kept distinct; their operating rates are not interchanged.

★ Restate the original one-town magnitude law to admit this proved supply
countereffect, retaining the observed positive headline response and at least
`TRANSMISSION_FLOOR` on its same complete physical basket with groceries at
their observed native floors. The original assertion was
`inflation[0] - inflation[n - 1] >= TRANSMISSION_FLOOR`. Its replacement also
requires a smaller response to name actual capacity-limited sales, their
upward clearing/markup response, the months carrying the closing price and an
adverse scarcity contribution on the same basket. Native retained sale
getters are read after every month. A shortage flag alone cannot pass it.
Add the previously printed-only hard pooled headline effect of at least the
same one-point floor on fixed paths 16-31. Keep every existing sign, slope,
14-of-16, median, expectation, saving-plan, timing, paid-rent, supply, basket,
reload and money guard. No model, seed, horizon, tolerance or policy changes.

The exact common-basket effect is 0.720377970 points; at grocery floors it is
2.339810930, an adverse scarcity offset of 1.619432960. Expected inflation
falls 3.986827% to 2.730521%, spending-plan factor 1.023009 to 0.965161. The
full first run's fixed-grid pooled headline response remains 1.625736 points.
The supply and demand channels coexist: the
[Bank of England's 2024 transmission account](https://www.bankofengland.co.uk/quarterly-bulletin/2024/2024/about-a-rate-of-general-interest-how-monetary-policy-transmits)
describes state-dependent responses and investment costs;
[ECB Lane, July 2023](https://www.ecb.europa.eu/press/key/date/2023/html/ecb.sp230712~d950906f00.en.html)
distinguishes firms' cost-of-capital/production response from bank-credit
amplification. [Core et al., ECB WP 3064](https://www.ecb.europa.eu/pub/pdf/scpwps/ecb.wp3064~4d72f7af90.en.pdf)
find higher financing costs can sustain firms' prices and weaken disinflation.
The paper is its authors' research, not ECB policy or a calibration for this
40% city. The native operands above establish this particular model path.

Kept reproducer: `../codex-CBK3-monetary/target/phase2/diagnosis.txt`,
`verification.json`, `native-summaries.json`, `calculation.json`, `sale.csv`,
`bank.csv`, phase JSONLs, six observational patches, hash manifests and
`commands.txt`/`run.sh`. The committed `CBK3-monetary-sale.csv` keeps the
deciding high-rate sales and the low-rate m70 comparison; the committed
`CBK3-monetary-basket.json` keeps the price-decomposition operands.

The first revised assertion exposed a check error of my own: it required
`nextPrice > closingFloor` in every physical shortage month. Native
`FLOOR_CATCH_UP` can leave the sticky price below its newly raised floor;
m73, for example, has clearing 0.015730819 above charged 0.008307865 but next
posted 0.008460538 below closing floor 0.008592221. The physical shortage
does not imply an instantaneous realized premium. Helper phase3 identified
the five such months from the unchanged captured source and sale operands,
without a JVM. Correct the observation: require measured physical shortage
and clearing pressure in every carrying month, then explicitly assert the
consecutive chain from next posted price to following charged price, positive
completed acquisition and closing CPI equal to that charged price. The final
paid-price premium and adverse basket contribution remain required. This
changes neither a model operation nor any fixture or bound.

The complete revised MonetaryCheck exits **0** (`target/CBK3-res3-MonetaryCheck.log`),
including both consecutive-price/paid-CPI and physical-shortage attribution
assertions, the original basket-floor point and the hard fixed-grid pooled
headline point. The intermediate attribution red is kept separately as
`target/CBK3-res3-MonetaryCheck-attribution-red.log`. Full SaveFileCheck also
exits **0** (`target/CBK3-res3-SaveFileCheck.log`). Remaining integration
checks continue sequentially; no final-model ensemble is claimed yet.
BankFailureCheck, CentralBankStatementCheck and ReadPathCheck also exit **0**,
with retained `target/CBK3-res3-<Name>.log` files. GoldStandardCheck exits **4**
(one assertion, three framework lines) and MortgageCheck exits **4** (five
framework lines). Helper `CBK3-res3` is tracing each against the current
merge, read-only with no JVM, before a model, fixture or law decision. Both
remain owned. CentralBankCheck and LongPlaytest have not yet been run on this
merged model; their earlier results are not substituted.

### Gold and mortgage integration checks

Helper `CBK3-res3` located GoldStandardCheck's sole red: its calendar town
has zero occurrences of previous completed-month deferred window principal
coinciding with a charged month. It still passes all 318 Gold/calendar/base
months to July 1926, Canada's peg in none, lowest cover 1.323. The log does
not establish whether H or D was always zero, so no stronger claim is made.
Its incidental non-vacuity premise is uncaused under the changed funding
trace. Two equal-zero monitor assertions alone do not prove the mechanism.

★ Preserve that town's founding, horizon and every calendar/base assertion.
Cause the deferred-window condition separately by pre-audit reserve
preparation in GoldStandardCheck 15b: native loans, haircut collateral and
stable funding permit an actual draw of100; a fixture adjustment of80 across
the gold vault and reserves is retired at the next
top; the remaining 20 reserves repay only 20, leaving actual/deferred 80 and
zero reserves. The next actual need is only 30. Assert the full 80-dollar
interest invoice and the 50-dollar past-active subset, matching accrual on
both books, actual secured collection, no forgiven principal or second bill,
both native principal settles and money/pool closure to the cent. Every
premise is caused and asserted unconditionally. This avoids moving the
calendar town or searching for a naturally passing seed. Its incidental
count remains printed truthfully. The calendar monitor also stops dividing
total H/F expense by a blended rate as though it were window principal; it
reads the actual window bill and saved window spread instead. Money destroyed
remains actual collected cash in MoneyAudit, not the invoice.

MortgageCheck's three numeric failures are its transfer-over-policy
expectation at 0%, 10% and 25%; the 3% quote cancels the omitted term. All
four rate decompositions, capital charges, ladder rungs and over-transfer
assertions pass. Its expectation still describes deposits/window only,
omitting Part 1's actual independent foreign funding quote. Correct that
expected operand to include the saved foreign quote less its funding-term
premium and the quoted policy rate, weighted by the native lagged foreign
share. Preserve the landlord city, three months, all four dials and exact
tolerances. Assert that the native foreign share is positive and that at
least three quoted dials exercise a nonzero contribution. A negative spread
over a high counterfactual policy quote is not a negative mortgage rate;
neither the actual rate nor credit rules are clamped to hide it. The callback
and rules merge is not the cause: these failures use Bank.Ladder directly.

Both are harness mends with no further model/default change. Kept first red
logs have `-first-red.log` suffixes. Helper reports and source/operand
diagnoses: `../codex-CBK3-res3/target/CBK3-res3-gold-diagnosis.txt` and
`CBK3-res3-mortgage-diagnosis.txt`. No helper JVM or fresh baseline was run.
Revised MortgageCheck and GoldStandardCheck both exit **0**, with their
current logs at `target/CBK3-res3-<Name>.log`. The added deferred-window fixture
actually exercises all reserve/deferred/bill/collection premises and both
cent-closure assertions. CentralBankCheck is now running, then default
LongPlaytest. The older laptop run hit the generic 1,740-second budget (the
box completed it in 397 seconds), so a 7,200-second exact-class fallback was
prepared. The current check had already entered the ordinary named batch
wrapper at 07:58:31 before that preparation; its original wrapper is restored
and the run continues. The longer command is a separate, unexecuted
`target/cbk3-res3-CentralBankCheck-exact.sh`. No model assertion, cent
tolerance or timing-soft setting changes, and no timeout red is passed.

That ordinary CentralBankCheck run stopped at the generic budget and returned
**4**, reporting six framework red lines. Its partial source/result evidence
is retained as `target/CBK3-res3-CentralBankCheck-first-red.log`/`.txt`.
Helper `CBK3-res3` is separating every assertion failure from the timeout,
read-only with no JVM. The longer exact-class fallback is still unexecuted;
no green or timeout-only diagnosis is assumed. LongPlaytest has not started.

Helper phase7 found **four actual assertion failures plus the 1,740-second
timeout**; the sixth framework line is exit 1. No production conservation
failure was established. The first H/F fixture bills before either liability
is settled: actual F is zero, so the expected 1,341.666667 foreign bill and
6.591667% weighted rate cannot hold. It now wires a real central bank,
settles actual H/F first, asserts both positive stocks, bills/collects once
and checks per-stock expenses, weighted rate, M0 and the actual funded pool
to the cent. No residual is invented as a foreign expense.

Its repayment fixture already filed the 20 repayment before returning;
the check now reads that saved last-month flow, current repayment zero,
then current repayment 30 after 30 new reserves, preserving H80/D80/R0 and
H50 after payment. The real bond-issue city draws principal after that
month's carried-stock bill: a new draw does not imply interest was already
owed on it. Preserve that city and every existing month; capture actual F
after the issue and check its exact bill at the already existing following
play, using that month's saved foreign quote. Positive H/F billing is
non-vacuous in the explicit native section-3 fixture. Keep the city's draw,
full split expense, central-bank window income and all audit assertions.
The pure-window pool equations explicitly subtract collected cash, with
separate hard assertions that ample collateral collects their whole invoices.

The timeout stopped after 12 of the 24 existing 600-month policy shapes,
before the remaining shapes and rule/trust/save assertions. None are cut or
replaced. The exact same full compiled class now runs under the separate
7,200-second temporary wrapper after `--fast`, then LongPlaytest. These
are check setup/flow-phase mends, not model/default or tolerance changes.
Kept report: `../codex-CBK3-res3/target/CBK3-res3-centralbank-first-red-diagnosis.txt`.

The exact-class rerun's actual H/F, weighted rate, pool/M0 and next-existing-
month foreign bill assertions pass. One early failure remains: previous-month
repayment is zero, not 20. The helper's prior claim that the shared fixture
had filed that counter was incomplete. The partial log is kept as
`target/CBK3-res3-CentralBankCheck-repayment-red.log`; the run was stopped
early by its own verified JVM PID 23040 (parent 34304, created 08:40:10),
driver exit 127. It is neither a completed check nor a timeout-only result.
Helper phase8 now checks the native file/start lifecycle and the smallest
truthful fixture correction, read-only with no JVM. No repayment amount or
conservation assertion is waived; LongPlaytest remains unstarted.

Phase8 corrects the precise cause: `CentralBank.startMonth()` **does file**,
but the public `lastMonth()` returns zero until `knowsLastMonth()` has more
than one filing. The shared fixture performed its setup before opening an
initial month. It now calls the native `window.startMonth()` before setup
transactions, retaining the final paired opening: the initial empty filing
is followed by the genuine setup repayment 20, publicly known in the next
month. No private filing call, manual counter, reconstructed flow or
close/remit operation is added. CentralBankCheck now asserts known history,
previous 20/current zero/H80/D80/R0, then current 30/H50/D50 with previous 20
unchanged. Its redundant extra central-bank openings after the helper are
removed so the paired bank/window opening is the single observation boundary.

Only fixture history gains one initial empty filing; no stock, capital,
bill, policy or model default changes. BankRulesCheck, including full/limited
collateral and both positive/negative saved due-change cases, reruns **0**.
SaveFileCheck reruns because it borrows that fixture, then the unchanged full
CentralBankCheck/LongPlaytest sequence follows. Kept lifecycle diagnosis:
`../codex-CBK3-res3/target/CBK3-res3-repayment-lifecycle-diagnosis.txt`.

The final shared-fixture reruns pass: BankRulesCheck **0**, SaveFileCheck
**0**, and `--fast` **0** (0 firm/210 soft Stale findings). The full exact
CentralBankCheck then completes **0**, preserving all 24 housing shapes,
600-month horizons and every post-loop rule/trust/save assertion. No timeout
or failed assertion is waived. Its complete log is
`target/CBK3-res3-CentralBankCheck.log`; the temporary command remains
`target/cbk3-res3-CentralBankCheck-exact.sh`. This validates committed source
`855c34982` (RES3 integration `5083cf3ba`, AP8/main `858378bae`). Default
LongPlaytest is now running as the last named model check.

## Network-window dry-run handoff (2026-11-27)

Orchestrator `20261127-095539-493` explicitly orders the dry run started now
on the pushed AP8 merge; the full suite may cover unfinished local reruns.
The laptop goes offline at 11:15 until about 19:00. Ready by 10:45 permits
the morning gate; otherwise push by 11:05, keep a resume line and end by
11:10, starting nothing further that needs the network. This supersedes
waiting for local LongPlaytest before launching the dry run.

Source `855c34982` has `--fast`, BankRulesCheck, exact BankCheck,
CentralBankCheck (all 24 shapes), BankFailureCheck, MonetaryCheck,
SaveFileCheck, CentralBankStatementCheck, GoldStandardCheck, MortgageCheck
and ReadPathCheck green. The AP8 optimization is exact; the Bank model in
the earlier exact BankCheck is unchanged by it. Local default LongPlaytest
is still running beyond m1200 when this handoff is written. No completed
LongPlaytest or final-model ensemble is claimed yet.

`sh tools/pick.sh origin/main HEAD` selects **ALL 262 harnesses** and reports
review needed for money (Bank, BankRules, CentralBank, CentralBankStatements,
DebtManager, BankRulesPane and more) and saves (DataSave). Main is still
`858378bae`. The prior SHIP/80-run result is `4ffbb65b2`; the merged
actual-stock billing and saved unpaid-interest claim require their own review
and final-model ensemble before those old figures can be treated as current.
The provisional marker remains while these obligations are unsettled.

Resume if the network window closes: fetch mail/main after about 19:00;
inspect the kept dry-run result and local LongPlaytest; finish the review
and final-model ensemble (M1/F1 are hard stops), then reuse a valid dry run
only on its recorded source/main commits. Never restamp the format-66 city
or claim its historical measurement as a format-67 replay. Do not drop the
marker or announce ready before the required verdicts are complete.

Orchestrator `20261127-100121-529` agrees both remain required. Fresh review
**REV-CBK3b (Claude)** is reviewing the merge's new money and saves. Let the
current dry run finish as evidence. The final-model ensemble explicitly waits
for home, about **19:00**: away from home the box tools would fall back to
the laptop. Commit/push and end by **11:10**. At about 19:00 receive the review
verdict, mend if needed, run the final ensemble, then finish readiness. This
replaces the morning-ready target; a green morning dry run alone is not SHIP.

The pushed dry candidate is `4704e1c4ba7773d28c82780937c8e5d7bdcf7f7e`,
model/check source `855c34982`, main `858378bae`. The box run is
`/home/jerus/cbs/runs/cbs-gate-CBK3-bR0BVW8M`; controller log
`target/CBK3-dry.txt`. Its documented owned-run stop command is
`sh tools/gate.sh --stop '/home/jerus/cbs/runs/cbs-gate-CBK3-bR0BVW8M'`.
No ensemble or network-dependent job is to be started away from home.

## REV-CBK3b morning mends and resume (2026-11-27)

Orchestrator `20261127-101034-619` reports **not SHIP**, one P1 and three
related P2 mends, and orders work now, push by 11:05/end by 11:10, then
continue around 19:00: mends, second stranger look, final ensemble, dry run,
ready. Only the marker's later removal means ready.

P1: SectorStatements now draws the existing `WINDOW_INTEREST_DUE` liability.
The regression starts with genuine positive due and adds the actual displayed
liability rows, checking both their sum and the table total against the model
liabilities. An enum-based residual cannot hide a missing visible row. The
BankScreen explanation now has the row to attach to. New BankRulesCheck passes
this positive-due footing case.

★ P2 reality: [Fed PSR part III](https://www.federalreserve.gov/frrs/regulations/iii-policy-on-overnight-overdrafts.htm)
uses primary credit plus 400bp for negative overnight reserve accounts;
[Operating Circular 10, sections 2 and 4.2](https://www.frbservices.org/wp-content/uploads/082823-operating-circular-10.pdf)
instead includes unpaid loan interest in the repayment amount and charges
an unpaid repayment amount at its applicable lending rate plus **500bp**.
This model keeps a loan-interest receivable, not a negative reserve account,
so choose the literal OC10 past-due basis. Charge only the authoritative
**opening** due, once, in Bank's existing invoice; the central bank accrues
that same invoice and collects cash once. The monthly `/12` convention is an
aggregate approximation, not a claim of exact Fed daily compounding. Today's
new invoice earns no arrears charge until the next opening; a paid claim earns
none. Existing resolution standstill, discretionary collateral gate, atomic
refinance and foreign-stock pricing are unchanged. No general overdraft
facility, new player dial or extra independently saved claim is introduced.
The already saved window/total expenses and signed due-change flow carry the
charge. Native full-collateral tests prove two-period billing, actual claims,
equity/pool/M0 cents, no rebill, later payoff and no fee on paid history; these
pass. The saved-flow regression now requires a genuinely positive arrears
component, and full SaveFileCheck passes.

P3: restore a positive genuinely played overdue/charged counter. The fixed
calendar proof retains its founding, horizon and all date/base assertions;
a controlled continuation uses real native bill issuance and normal month
settlement, then a pre-audit XAU/reserve adjustment with a real vault leg but
no paying pool, saved window limit zero and two normal played months through
MoneyAudit. The gold adjustment is fixture preparation, not a native paying
redemption. Section15b remains additional coverage. The first continuation failed honestly:
its issue sizing did not create actual H, so the invoice/due stayed zero.
The helper identified omitted gathered deposits/household take-up; sizing now
covers native liquid funding/stable need and grosses up by the household
share. Successful-priced/closed counters share the same positive charged
premise, with a separate hard two-month audit count. The corrected played
case is rerunning; no positive outcome is claimed until its result returns.

P4: the resolution fixture now uses `windowCreditLimit`, exactly Game's raw
capacity callback, with an actual positive-capacity over-request that must
clip at the ceiling. Its exact native case is extracted for the short
BankRulesCheck as well as full CentralBankCheck; all 24 full policy shapes
remain. A first short reuse exposed an uninitialized harness output stream,
now initialized by its caller. Its rerun is pending.

The old-candidate dry run **exits 1**, suite 1,921 seconds, on
`4704e1c4b`/main `858378bae`, with 36 unnamed lines in nine harnesses. Kept
box run `/home/jerus/cbs/runs/cbs-gate-CBK3-bR0BVW8M`, copied home to
`C:/Users/Jerus/AppData/Local/Temp/cbs-gate-CBK3-bR0BVW8M`; driver
`target/CBK3-dry.txt` explicitly records `gate exit 1`. The shell's final
status is not substituted for that verdict. Source has changed for review
mends, so this failed old run is evidence, **not a reusable gate run**.
Helper `CBK3-res3` is classifying its lines with source/queued-owner evidence;
no unproved known-red row or red-ok is added. Local old-source LongPlaytest
also timed out at 1,740 seconds, last progress m2160, with no logged model
finding and no completed final-audit verdict. It is not called green.

Resume after home around 19:00: inspect the exact morning named-check results,
finish any uncaught played-fixture/old-dry mends, mail the new mends commit for
REV-CBK3b's second look, then run the final-model 80-run ensemble (M1/F1 hard
stops) and a new valid full dry run before readiness. The 4ff measurements
remain historical. The marker stays provisional throughout this offline gap.

Morning mends now validate: `--fast` **0**, GoldStandardCheck **0**,
BankRulesCheck **0** (including the exact extracted resolution/raw-cap case)
and SaveFileCheck **0**. Logs are `target/CBK3-review-fast.txt`,
`target/CBK3-review-last-before-offline.txt`,
`target/CBK3-review-save-bank.txt` and the kept `CBK3-res3-<Name>.log` files.
The played continuation actually carries H/deferred **5161.42971977**:
m321 invoices **29.0330421737**, collects **0**, leaves that much due,
audit **-4.71800376545e-12**; m322 opens with that due, invoices
**29.3173240450**, collects **58.3503662186**, closes due **0**, audit
**-1.04591890704e-11**. Calendar incidental count is honestly 0; the same
city's controlled continuation has **2** positively charged/deferred played
months. The hard positive premise, unpaid played bill and both full money
audits all pass. No collector cap, synthetic bank refresh or fictitious vault
payment causes the result.

The earlier dry's native box LongPlaytest completed **2404 months / 2404
funding audits**, **0** stable-budget binding months, no findings, every audit
and reload passed. Its separate raw-cap/desk find counts and exact maximum
residual were not printed/extracted, so no invented counts or exact zero is
reported. Box CentralBankCheck also passed all 24 shapes, native 246/246 rule
samples and save checks. These are old **4704** results, not validation of
the new arrears charge. The local timeout at m2160 remains separate.

Bounded dry diagnosis (helper report
`../codex-CBK3-res3/target/CBK3-res3-morning-dry-gold-classification.txt`):
36 framework lines represent 19 failed assertions, one NPE and 16 exit/count
lines across nine harnesses. OwnerCheck's owned failure is its stale expected
snapshot width 31 after the stable-budget counter was appended at cell 31;
the expected width is now 32, owner cells 29/30 are both checked, and the old
31-cell prefix must preserve them/default the new counter; old 29-cell
owner-absent coverage stays. Its named rerun is pending at this record.
CurrencyRegimeCheck needs an owned law/flow reconciliation: direct purchase
614.014021780002 versus net M0 difference 610.760367346008 has other net
flows **-3.253654433994**; equity miss up to .0949, while both audits and
intrinsic central-bank-keeps-nothing pass 10/10. Retain the direct purchase
law and reconcile independent differential issued/retired/income/remit/loss/
vault/edge effects, never loosen cents or pins. Exact attribution is pending.
FundCheck's new whole-float bid and its dependent pro-rata/band premises plus
donor secured/free-unit conjunction need native cash/stable/claim inputs;
the donor's exact failed conjunct is not yet identified. Do not call these
known main reds merely because HT1-H owns a different existing limit-order red.

CreditCrunch ground is present 104/120 months, missing 16; exact cause/owner
unresolved. Buyback's five-year issue is null then NPE, quote/refusal/allotment
unresolved. Holders reload .675335 versus .675317 remains strict-law concern;
cause unresolved. CapitalGains' second factor stays 1000 with step 0 (first
10 to 1000, step 100); native second trigger absent, cause unresolved. Kept's
four-times borrower premise fails and Spending fold retained/fresh widths
1454/1445 and heights 656/673, 680/697 need actual layout cause. GoldMining
has 0 mint/24, Gold/offer conjunction false and exports 2588.8773 versus 0;
FS8 is the verified current queued owner of that mint fixture, but its actual
regime/offer condition still needs inspection. No speculative known-red rows
are added for any of these. Shipped FS1/4/16/17 are not queued owners.

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
An interest bill that cannot yet be paid remains a liability of the bank
and a receivable of the central bank. Payment waits for reserves or secured
funding; collecting an older bill does not charge the expense again.
The next settlement applies the new window rule. A remittance is the central
bank's realised surplus after its costs and carried losses; no fixed share
of the treasury's revenue is guaranteed or imposed.


### Morning checkpoint, 2026-11-27 11:03 Halifax

OwnerCheck completed with all assertions OK (target/CBK3-review-OwnerCheck.log), including the current 32-cell snapshot and the real legacy 31-cell prefix. The named run reports suite OK. No owned harness or remote gate remains running. The current review mends have fast, GoldStandardCheck, BankRulesCheck, SaveFileCheck and OwnerCheck green; the new full CentralBankCheck and remaining old-dry diagnoses are still owed. The failed 4704e1c4b dry run is evidence only, not a reusable green gate. The marker remains provisional. Resume about19:00: finish the unresolved dry-run findings, obtain REV-CBK3b's second look on the mends, then the final-model80-run ensemble (M1/F1 hard stops) and a newly authorised full dry run. No ready claim is made.

## Evening resume, 2026-11-27

Resumed on orchestrator `20261127-184011-044`. Main's intervening
`ecac4f4bd` only queues the UX1 study; merged it without changing the model.
The morning checkpoint is `0119a4307`. The helper's bounded classification
of the old dry run remains the inventory; no unproved failure is assigned a
queued owner. One helper resumes for FundCheck's exact native premises.

★ CurrencyRegimeCheck's payer comparison now distinguishes the purchase
from the other flows the two played cities actually produce after choosing
different payers. It retains the independently equal owed bill, treasury
payment and central-bank direct issue assertions, and checks the direct
issue difference against the treasury's payment to MoneyAudit.CENT. The
cross-city M0 difference includes independently recorded non-purchase issue
less retirement. The four-month equity comparison nets the actual vault,
unpaid edge, remittance/loss, gold retirement and subsequent total purchase
differences, retaining the original positive purchase and every monthly
money audit. This restates the existing central bank balance-sheet identity;
it neither assumes equal funding income after the cities diverge nor changes
a cent bound or the model. The native named rerun is in progress.

CurrencyRegimeCheck completes **exit0**, including both payer reconciliations,
the actual settlement and every existing save/money law. Its old unexplained
base gap is now independently accounted: direct issue614.014021780, other
net issue-3.25365443399, actual M0 gap610.760367346; the four-month
equity comparison's worst miss is4.55e-13. Logs:
`target/CBK3-evening-CurrencyRegimeCheck.txt` and `.log`.
The full current CentralBankCheck is now running alone, with all24 shapes
and its post-loop native/save laws; no completion is claimed yet.

FundCheck's owned setup mends (native rerun pending while CentralBankCheck
uses the single JVM): select two actual holdings above the exchange's native
valuation floor, and explicitly endow enough stable cash for their aggregate
full-float bids before retaining the existing capital/position/book tests.
The old log proves a zero first sale fraction, not which unprinted gate
bound; the new before/after diagnostics name each holding, floor, bid room,
cash, equity and stable cash. Keep the full-float, positive pro-rata sale,
band, due and late-payment assertions and the fixed72-month founding.
The opening market gift transfers only genuinely free shares and their
released acquisition basis; tagged shares retain their backing.

★ The independent cap-gift case explicitly establishes a partially encumbered
nonempty opening donor and its matching government receivable, following
CapitalGainsCheck's paired opening secured-stock fixture. The old log did
not establish which donor predicate was absent, so no old runtime operand
is invented. The new opening stock leaves more free shares than the actual
gift needs; it neither assesses a new cash flow nor claims an incidental
m73 inheritance. Snapshots are taken after that opening stock and before
the measured gift. The unchanged gift assertions require a real positive
free-unit transfer from the secured donor, untouched tags and claims,
backing, exact cell/cash/claim snapshots, conserved shares and acquisition
basis. Per-donor diagnostics expose every actual operand. No production
lending, tax, purchase, floor or save rule changes for these fixture mends.

The helper's next bounded report identifies three more concrete setups;
no exact current queued owner covers these reds, so they are mended here.

CreditCrunchCheck: the old run bought only6 plots in all, so its16 missing
preparations could not have exhausted60 successful native purchases in a
month. The old log omitted shelf/affordability operands; do not assert a
particular old cash balance. The setup now refreshes an empty shelf through
Game.buyLandBlock, reads each changing native parcel quote, and endows the
cash shortfall only when canAffordParcel is false before the actual purchase.
It keeps MAX_PLOTS, zoning/terrain/link checks,120/120 ready months and all
credit/audit laws. Failure diagnostics expose the native quote, cash,
affordability, purchases and usable ground. Named validation is pending.

BuybackCheck: the old5-year sale's stop1.41% was below the quiet world's2%
hurdle, making firms and world native bids exactly0 and therefore the issue
size0. Its null sale precedes the refusal tests and caused the misleading
NPE. The all-holder fixture now solves only its native policy/curve input,
inside the existing bounds, for a default-cap rounded issue above the
minimum and with a real world part. The original savers' share, bank-room
inverse, actual issue, holder-part, refusal/settle/audit laws remain. Original
policy/curve, deposit and risk-book inputs are restored after the sale. A
failed actual sale still fails hard before dereferencing it; it is not
passed or skipped. No model cap or yield rule changes. Rerun pending.

CapitalGainsCheck: the exact batch cause is the truthful Equity.fairValue
floor, replacing the inherited below-floor return of lastPrice. In this
seeded fixture both floor constants equal1e-9; the unified atFloor gate by
itself is not the cause. After the first100x split, the unchanged1e6 required
return valued each share below that floor, so demanding another split was
uncaused. ★ Set the fixture's second native required-return input from its
actual trailing earnings/share count to value the company at four times the
native floor. Assert that positive valuation and the real traded quote
above the wage band before calling the ordinary exchange step. Keep the
positive second automatic-split premise, both cumulative-factor identities,
real no-fill, tagged units/basis, nominal claims, collections and saves.
No historical-price resurrection or direct split is introduced. Rerun pending.

HoldersCheck's new continuation mismatch is actual foreign paper FACE, not
a changed market mark: every stock, lifetime and individual piece already
round-tripped exactly. The live fixture pins FX at1; its loaded twin did
not reapply that deliberately unsaved fixture control. Reapply the same
live rate after load and assert both pins/rates equal before continuing.
Every strict face, coupon, save and audit comparison remains. No saved
model field or tolerance changes. Native rerun pending.

GoldMiningCheck's old positive payment proved settlement of the prior offer,
not the regime or current offer: the old log omitted those operands. Its
billion-unit opening XAU endowment also remained a pending FX deal, a concrete
setup hazard that could break Gold lawfully. ★ Use the existing
ForeignAccounts.forgetDealing helper for this opening HOLDING before entering
Gold, preserving the actual vault and books. Require actual cover/entry and
positive native accepted ounces/price, then print each current regime/event,
cover, pending deal, local/export output, accepted offer and prior payment.
Retain the fixed24-month run and positive mint firing count; strengthen its
24 audits with the cent bound as well as the original relative bound. The
intentional Float transition must begin with a positive accepted offer;
0=0 cannot prove payment. Preserve all settlement/metal/export/save laws.
The exact old break branch remains unproved until the new operands run;
no speculative known-red row is added, though FS8 currently owns this
industry fixture class. No production gold or monetary rule changes.

Full current CentralBankCheck completes **exit0**, with no truncated table:
all24 shapes,23/24 lower-price comparisons, median-0.365 points/year
(18 required),246/246 native rule comparisons in each direction, and every
post-grid bank/decision/save law pass. This is the actual morning-money
source, including the raw resolution callback and arrears rate, not the
old4704 run or the old1740-second local timeout. The native named command
keeps its full grid under the7200-second limit and exits normally; log
`target/CBK3-evening-CentralBankCheck.log`, command
`target/cbk3-evening-central.sh`. No model source changed during it.

The helper's Kept diagnostic artifact is now applied locally for the next
native run: `../codex-CBK3-res3/target/CBK3-res3-kept-diagnostics-turn15.patch`.
It records only strings from the two graphs at their original comparison
points and the rejected native quote operands. It does not force CSS,
layout, redraw, binding, signature tolerances or funding acceptance. Kept's
actual run is pending; its compile has succeeded, including all other queued
fixture mends. The broad temporary instrumentation will be removed once the
actual cause is established, retaining only useful regression evidence.

Evening native results now complete: CapitalGainsCheck **0**, including a
real second10x split (factor1000 to10000, tagged units100000), every nominal
claim/basis/collection/save law unchanged; CreditCrunchCheck **0**, with
**120/120** ground-ready crunch months and7 actual native plots; BuybackCheck
**0**, including the genuinely issued5-year line, all-holder allocations and
all refusal/settlement/no-pump laws; FundCheck **3**, only main's registered
HT1-H limit-order red. Its four newly red withdrawal/donor premises and all
free-share, claim, backing, cash and basis assertions now pass. Logs are
`target/CBK3-evening-<Name>.txt` and `.log`.

The first local Kept diagnostic run is **not green**: three actual assertion
failures (covered funding run, Spending bridge open/closed), then its1740s
watchdog, five framework lines including exit. Classes compiled20:33 precede
both the20:55 funding mend and21:06 gutter fix. Keep its evidence in
`target/CBK3-evening-KeptCheck.log` and the funding/government partial copies.
The final named Kept will retain the exact captured2048MiB heap,64MiB initial
heap, SoftRefLRUPolicyMSPerMB=0 and renderer, with a7200s outer completion
limit; no timing/conservation/save/geometry assertion is changed. The previous
local timed-out run is never substituted for a completed result.

Government geometry is now proved on Windows too. Both binding owners and
frame/body sizes are correct in both graphs and the held-still repeats.
Only the inner vertical bar differs: wide viewport1454, page656+24 foot=680
fits its696 height; narrow1446, page673+24=697 overflows. The actual Windows
gutter is8px (old box9), and wrapping adds17px: two stable AS_NEEDED solutions,
not owner drift, stale data, platform noise or a transient.
★ Reserve the native vertical gutter in GovernmentScreen.frameOver with
ALWAYS, keeping every node, binding and scroll value. Short pages now show
an idle vertical track: that is the explicit presentation tradeoff. No fixed
pixel gutter or global scroller change is used. The temporary owner/geometry
dump is removed after collecting the proof. A separate native probe derives
the CSS gutter and wrap midpoint, causes both AS_NEEDED histories, and checks
production boundary/short/tall dimensions, full pixels, node identity and
zero unchanged allocation. It runs early and fails hard if any proof fails;
the two original played Spending comparisons still run later unchanged.

The covered funding setup now has the actual missing inputs: bank treasury
paper room0, household spare71334.91, deposit6.955%, native TAIL1% cap. Large
orders have real offers but exceed them; merely shrinking cash need lowers
the ceiling below deposit and truthfully empties the short issue below its
minimum. The fixture supplies only quoted opening cash and an actual lower
savers' rate to draw on the existing household purse, keeping native minimum,
lots, cap, quote, actual proceeds and the same nonempty order. Earlier short
pages finish before the endowment. Read-only guards now require the SAME
positive eligible run and both full fourfold offers before and after the
real watched month; no post-month re-cause/freeze/restore can hide a failure.
Persistence is still unproved until the named run. A compile caught the
cross-package cash setter in the prototype; KeptCheck now passes its existing
package-private setter as a DoubleConsumer, without widening Game's API.
HoldersCheck is compiling/running on this source; no result claimed yet.

HoldersCheck's pin-only rerun still failed the same strict face continuation;
that failed native log is retained as
`target/CBK3-evening-HoldersCheck-pin-only-red.log`. The deeper source trace
proved two more intentionally unsaved fixture controls missing from the
twin: Construction and Heavy Industry planning holds. Restoring those exact
live controls after load removes the new failure: HoldersCheck **3**, only
its three registered main reds, with the same strict continuation assertion
now OK. No tolerance, saved field, format, conversion or load rule changes
were needed. The temporary98-line Game replay probe was never applied.
GoldMiningCheck **0** records24/24 positive mint months and24/24 cent audits,
worst residual7.75912667450e-11, and the positive already-accepted Float
transition. BankFailureCheck **0** and BankCheck **0** complete as well.

The independent gutter proof and the covered-run before/after guards compile
successfully. They are still awaiting native execution. Their new impossible
fixture states fail hard; KeptCheck's main now reports an exception and exits
nonzero with the FX toolkit stopped, instead of leaving a failed setup alive
until the watchdog. No assertion is skipped or relaxed. The final long named
run uses the captured heap/renderer settings and a7200s outer completion
limit, preserving every existing timed/retention/geometry/money/save check.

Selector/runner correction: AllChecks.selected uses substring matching. The
BankCheck child completed all its assertions at21:44, but the same wrapper
then selected CentralBankCheck again. I began the next Kept pipeline before
noticing that selection, causing an unintended overlap21:52:46–21:55:31.
Stopped only the duplicate's verified owned tree (34156,584,2776,40220,
41124,39140,38508); unrelated TownGrid/other sessions survived. The stopped
wrapper is NOT reported as exit0; its completed native BankCheck log is kept
as `target/CBK3-evening-BankCheck-native-complete.log`. One exact native
BankCheck will supply the unambiguous final exit. The already completed
52-minute current CentralBankCheck0 is retained; its duplicate adds no result.

The new early native gutter regression now actually passes all five laws:
CSS gutter8, wrap41/58, viewport494; AS_NEEDED control histories stay wide
1454/content486 versus narrow1446/content503. Production boundary/short/tall
retained/fresh signatures, dimensions, full pixels and original node identity
all match; unchanged draws allocate0. Full played Spending comparisons are
still owed after the remaining funding guard is resolved.

The a67 Kept run exited1 cleanly at the added post-month funding guard, log
`target/CBK3-evening-KeptCheck-gap-closed.log`. Before the month the real
Studio2336 order has positive gap25000 and both actual100000 offers funded
from the existing household purse (837368), same native cap/lots. Every
original first/next redraw, pixel and node comparison passes,100% handed
back. After the actual watched month the gap is0. The new added guard insisted
it remain positive; the original law was no shortfall/cap through the month.
The helper is checking exact cash/invoice evidence and that original premise
before any law restatement. No rerun setup, cash restore, rate freeze,
conservation/save/geometry bound or default change is used to make it pass.

★ The helper confirmed that the added post-month-positive-gap guard exceeded
the original covered-page premise. Game.buildFundingGap is max(0, the actual
invoice minus actual cash). Preserve the STRICT positive opening need and
both real full/nonempty fourfold offers. After the real month, the same
nonempty eligible positive finite invoice may remain covered by those offers
or be fully covered by finite actual treasury cash. The latter branch must
show actual cash>=invoice and gap0, explicitly labelled cash-paid, with no
shortfall words or cap chips on the watched redraw. Full after-month operands
are now captured before the former early return; income versus invoice
movement is not guessed. This restores the original no-shortfall law and adds
an assertion of the actual cash-covered effect. No cash/rate reset, recause,
month move, minimum/cap/lots/fee change or money/save/geometry tolerance change
is introduced. The next named Kept must validate those actual operands and
all original played page comparisons; no passing verdict is claimed yet.

Evening current-model BankRulesCheck completes **0** after the cash-paid
fixture guard change (`target/CBK3-evening-BankRulesCheck.log`): all window,
desk, leverage, dial, arrears and compatible-save assertions pass. The
MonetaryCheck child and batch suite both complete green, including all native
CPI/rent/redenomination continuation audits; native evidence is copied to
`target/CBK3-evening-MonetaryCheck.log`. Its launcher then exits1 because I
edited that running shell script's allowlist while Bash was blocked in the
child; its resumed file offset encounters an unmatched quote. This is a
launcher error after the completed suite, not a model/check red. The final
immutable v2 launcher passes shell syntax validation and is not edited while
running. No MonetaryCheck assertion, bound or native result is changed.

The latest --fast completes **0** on the final cash-paid guard. The full
native KeptCheck is running sequentially with the captured renderer/heap
and 7200-second outer limit; its result remains pending. Fetched main
22b6eb96f includes UX1's study, its new independent probes and read-only
rail methods, plus queue records; its diff changes no monetary behaviour.
It will be merged before the review candidate, with no remeasurement claim.

Main22b6eb96f is now merged at829102bce. All source merges are clean;
generated-view conflicts retain their current branch versions until the
next regeneration. CLAUDE's actual interface-file count is57 (CBK3's extra
page plus UX1's two probes). Both BankRulesCheck and UiSteadyCheck remain
registered, and UX1's display registration is preserved. The pick is
**ALL263**, review needed for money and DataSave; no ALL suite is launched
locally. The already-running native Kept classes precede this additive
read-only probe merge; monetary and Government behaviour are identical.

The current native Kept run passes the watched funding case without setup
reapplication. At m1410 the SAME Studio2336 order has ahead1, actual invoice
**45853405.707688615**, actual cash**45919910.018052734**, gap**0**;
native TAIL1% cap and actual rates are recorded. This proves the actual-cash
branch, rather than assuming an unchanged positive borrowing need. Both
native fourfold offers and the positive opening need passed before m1410;
the subsequent watched redraw has neither shortfall words nor cap chips.
The remaining full-page comparisons are still running; no completed Kept
verdict is claimed yet.

Terminology correction: those operands prove current invoice COVERAGE by
actual cash, not a completed invoice payment. Rename only the new branch's
diagnostic label/comment to `covered-by-actual-cash`; the predicate, native
month, setup, cash, invoice and every assertion remain identical. The active
binary's `paid-by-actual-cash` log text denotes that same coverage predicate.

The full native replay now also passes the ORIGINAL played Government
Spending bridge open/closed assertions (log2934-2937), plus all four original
Government retained/fresh full-page signature and pixel comparisons. Thus
the independent native-gutter regression is backed by the actual formerly
failing played page. The full native footer is still pending; Trade, Policy
and research checks continue, with no failure recorded so far.

## Evening completed old-dry repairs, 2026-11-27

Full native **KeptCheck0, ALL OK** (`target/CBK3-evening-KeptCheck-final.log`,
529572 bytes; startup22:13:58, footer22:43:12). This is a completed native
run, not the earlier timeout or early guard failure. The exact captured
2048MiB/64MiB/SoftRef0/default-renderer settings and 7200-second outer limit
are recorded above; no internal timing, geometry, retention, money or save
assertion is changed. KRT1 held-still has0 nonrepeating place/size differences;
KBP1 has8 monthly new blocks,0 repeated held-still, and2 genuine content
readings after a choice. Both original Spending bridge failures, the real
covered-funding case, the independent gutter cause, all pixel comparisons,
clock non-rebuild, research, speed and final release/geometry checks pass.

The helper's36 old unnamed framework lines are now accounted for by owned
repairs, with no newly invented known-red exemption:

| Old-dry harness | Cause repaired; current native result |
| --- | --- |
| CreditCrunchCheck | Fund each actual changing native land quote before purchase;120/120 ground-ready months,0. |
| BuybackCheck | Cause the actual five-year all-holder issue through legal pricing inputs; refusal, settlement, no-pump and save laws retained,0. |
| CurrencyRegimeCheck | Reconcile independent non-purchase issue/retirement and central-bank income/vault/remittance effects; native cent laws retained,0. |
| FundCheck | Supply stable desk cash, eligible live holdings and free tagged donor units while preserving secured backing and claims;3, only its existing HT1-H limit-order red. |
| HoldersCheck | Restore the live pin and both deliberately unsaved investment holds after the twin loads; strict face continuation passes,3, only three existing main reds. |
| CapitalGainsCheck | Cause the second genuine native split above the scaled floor; factors10 to1000 to10000,0. |
| KeptCheck | Cause the opening covered offers, prove actual after-month coverage, and remove the native AS_NEEDED width-history ambiguity; all original comparisons and the full footer pass,0. |
| GoldMiningCheck | Forget the opening vault's completed FX-dealing marker, then cause actual Gold offers;24/24 positive mint months and24/24 cent audits,0. |
| OwnerCheck | Check the actual32-cell snapshot and both real old31/29-cell prefixes;0 on the current monetary model. |

All model money/save behaviour remains0119a4307 through these evening
fixture and Government presentation repairs. The post-merge fast and exact
native BankCheck plus individually named SaveFileCheck, GoldStandardCheck,
CentralBankStatementCheck, SectorStatementCheck and ReadPathCheck are now
running sequentially. The final80-run ensemble and a newly authorised full
dry run remain AFTER REV-CBK3b's second SHIP; no prior measurement is relabelled.

The post-merge final sequence now completes **0** in
`target/CBK3-evening-remaining.txt`: **--fast0**, exact native **BankCheck0**,
**SaveFileCheck0**, **GoldStandardCheck0**, **CentralBankStatementCheck0**,
**SectorStatementCheck0** and **ReadPathCheck0**, strictly sequential. Every
wrapper reports zero firm stale findings (210 soft). The played gold-window
continuation reproduces m321/m322's actual two invoices and cent residuals
recorded above, preserving RES3's reserve-capped repayment and once-only
pricing of every held dollar. The read paths change none of the native
fields, world/trade state or health rings under repeated reads.

Current-model **CentralBankCheck0** (all24 shapes), **BankRulesCheck0** and
**BankFailureCheck0** are retained with their complete evening logs. The
completed **MonetaryCheck** native child and batch suite are green; the
post-suite launcher error is explicitly separate above. No further monetary
source has changed since those checks. The Kept binary precedes the final
read-only main probes and diagnostic-label correction only; all of its
predicates, native setup, production Government behaviour and every existing
check are identical. The final merged source compiles green after both.

The provisional ship file now records the native Government gutter fix and
retains all three queued follow-ups. No known-red row is added: the only
remaining Fund/Holders reds are main's existing registered lines. No failed
run is substituted for green, no money/save law is relaxed, and no player
save folder or slot is accessed. All helpers and all named JVMs have ended.
The next handoff is mends for the second review, not readiness; the marker
stays provisional until the final-model ensemble and authorised dry run.

Final documentation hygiene retains655 generated maps whose ONLY difference
was the gate box's UTC28th versus this laptop's local27th date; their bodies
match HEAD exactly after normalizing that single date line. Every changed
generated body is retained. Known-red validation is **0**, and LE1/BFW1/BPM1
all recheck free against current origin/main. The sector-statement result
specifically covers **3000 statements /36300 screen lines /120 months**,
every one balanced and readable. No further source change is needed.

## Second SHIP and final measurement, 2026-11-27

REV-CBK3b's second look at aed3b2916 is SHIP, no P1. Fetch and merge confirm
main22b6eb96f is already included. Start a fresh label, **CBK3-final**, so no
4ff snapshot or earlier completed CSV can be reused. The box runs pushed
aed3b2916, against the approved getter-only **CBK3-before** replay of the
0.9.212 baseline. Preflight found only ASH1's ensemble; the normal box runner
queues this one behind it. M1 and F1 remain hard stops, judged from CBK3's
own completed run. No final verdict is claimed while queued/running.

### Verified OC10 quotation and scope

The Federal Reserve Banks' [OC10, effective August28,2023, section4.2,
printed page5 (PDF page8 of33)](https://www.frbservices.org/wp-content/uploads/082823-operating-circular-10.pdf#page=8)
states that interest on the portion unpaid when due:

> "shall be calculated at a rate 500 basis points higher than the applicable rate then in effect"

This primary-source PDF, effective on the date above, was checked on November27. Section2's
definition on printed page1 (PDF page4) makes the repayment amount include
both the advance and its accrued unpaid interest. Thus the rule covers
unpaid principal too; the quote is not authority for applying the extra
500bp only to an interest receivable. CBK3 implements the latter part.
At Bank.fundToCover around3685, RES3's opening deferred principal past the
active tranche still pays just the window rate. That remaining P2 is queued
as BWP1 after CBK3, as the orchestrator directed, rather than silently
claiming complete OC10 coverage or changing the approved final model.

### Gold15a fixture wording corrected

GoldStandardCheck around1173 calls sellReserves, goldPaidOut and
settleGoldRedeemed directly while preparing the zero-reserve state. There
is a real matching gold-vault leg, but **no paying pool** for that reserve
retirement. This is a pre-audit fixture adjustment, not a native customer or
treasury redemption and not proof of the conservation of such a redemption.
The native bill issue/settlement before it and the two ordinary audited
months after preparation are separate evidence. Their positive invoices,
once-only accrual and cent closure remain the results recorded above. No
source, fixture, audit threshold or saved meaning changes with this wording
correction. Earlier descriptions of the gold preparation as native have
been corrected.

### BWP1: deferred principal at the past-due rate

Queue **BWP1 after CBK3**; its id rechecks free with tools/batch-id.sh.
Price the opening unpaid principal held past its due tranche under OC10
section4.2, as well as the unpaid opening interest already covered here.
Cause a genuine attempted repayment capped by reserves, positive held
principal above active funding and a subsequent charged month. Distinguish
performing/new principal, overdue principal and opening interest. Add the
500bp increment only to the unpaid overdue principal's existing base-rate
charge, so each principal dollar's base interest is still booked once.
The bank expense and central-bank receivable must match; collections remain
actual cash, debt remains owed, and repayment ends the extra charge. Preserve
RES3's reserve cap, collateral ceiling and both books' saved flows. Check
opening/partial repayment/later payment, no premature fee on today's new
invoice, save continuation and full money/pool/M0 closure to the cent.
Likely scope: Bank.fundToCover, CentralBank's invoice/collection, their
statements and named bank/gold/save checks. Follow the old-saves rule for
any changed saved meaning. Default behaviour moves: stranger review and a
final-model ensemble with M1/F1 hard stops. This is a distinct follow-up,
not BFW1's wholesale credit risk or BPM1's bond-premium carrying.

The first final-ensemble connection reset after42 DONE lines were observed;
`target/CBK3-final-ensemble.txt` records SSH reset/disconnect, no box exit,
and launcher **1**, not a score verdict. After reconnection main remains
22b6eb96f. Preserve the completed remote CSVs and the exact approved aed
snapshot; an isolated detached checkout at aed is prepared for official
RESUME=1, because the root's later records-only commit would select another
remote tools worktree. The read-only helper checks surviving process/lock
and completion state before restart. No completed CSV is declared missing,
green or rerun without that check; no model or score threshold changes.

The helper's bounded transport report
`20261128-051607-073-CBK3-res3-b170` confirms42 valid CSV/time pairs:
AUTO2 16, ZERO_SLACK13, ZERO_HOT13;38 missing, including8 partial CSVs.
There were0 NO DONE entries and no surviving own serve/shell/xargs/JVM at
both09:10:58 and09:15:20UTC. The helper's turn24 ended before resumption.
Official RESUME=1 keeps completed pairs and restarts incomplete runs from
their starts; no mid-CSV completion is inferred. Root's separate detached
aed worktree selects the SAME remote directory, while ENSEMBLE_OUT returns
the eventual copy and score to the main CBK3 worktree's target/ensemble.

External-data verification is kept in
`target/CBK3-final-data-provenance.txt`. Box SHA256 values match the laptop's
original named files: Earth2
`c897b2421d0b742899c034eeba84a4668f6534cae85e9c5500fa9a26172ae33b`
(281761695 bytes) and World1
`fbd11a6a3b880ffeeae4e5c07e7a9d950b3d28f17f7c77656aea7a1071a6fcc1`
(8712947 bytes). Both box modification times are November20, before the
November28 snapshot. These files are external, not part of the classes
snapshot; the verification does not pretend the runner hashes/copies them.
The resumed launcher `target/CBK3-final-ensemble-resume.txt` explicitly
reports **snapshot kept,38 of80 runs to do**, eight at a time on gatebox.
The original transport exit1 remains separate; final M1/F1 scoring is pending.

## Final ensemble verdict, 2026-11-28

The official resumed launcher completes **exit0**, **80/80 runs**, no missing
or short runs, **batch PASS**, **M1/F1 hard10/10**. Retained42 plus resumed38
belong to the same aed snapshot; they are not a mixture of models or ASH1
results. The resumed box phase takes8.5 minutes. Artifacts are
`target/ensemble/CBK3-final/{snapshot.txt,score.md,baseline.md,summary.json}`,
all policy/seed CSVs, times and logs; the driver is
`target/CBK3-final-ensemble-resume.txt`. The initial disconnected launcher's
exit1 remains labelled transport failure, not a score.

The counted score is **20/25**, with1 vacuous and1 retired criterion excluded.
Changed informational verdicts against the approved replay: ZERO_HOT H2
PASS to VACUOUS; T2 PASS to FAIL in ZERO_HOT, SHOCK and HIGH. These are
recorded rather than hidden or made additional ship stops. The official
side-by-side score is now [the final baseline](../ensemble/CBK3.md), replacing
the obsolete4ff baseline at that publication path; its earlier observations
in these notes stay historical. The heading/introduction use0.9.219, while
the actual source commit, snapshot time, data files, host and Java are kept.
The read-only helper independently verifies all80 exact full-horizon DONE
logs,80 valid CSV/time pairs,0 NO DONE and exact agreement between the
approved before document's fenced JSON and its replay summary. Its report
is `20261128-053443-917-CBK3-res3-3eb9`; calculation artifacts are
`../codex-CBK3-res3/target/CBK3-res3-final-ensemble-observations.{txt,json,py}`.
Its turn25 ends with no jobs. The counted score is **24/26 before ->20/25
final**, beside unchanged **M1/F1 hard10/10 PASS on both**. Every policy has
0 failed/0 unread recorded M1 months and0/16 F1 findings, with empty names.

### Final observed remittances and rescued failure episodes

Shares are100 times SUM(cb_remitted)/SUM(budget_revenue), not averages of
city ratios. Both sides contain102352 recorded months,16 seeds per policy,
with matched continuous windows: AUTO2 m2..2400, ZERO_SLACK m2..2040,
ZERO_HOT m2..402, SHOCK m2..840, HIGH m2..720. Denomination stays at unit1.
These are nominal revenue weights across recorded time; counterfactual
policy prefixes repeat, so the aggregate is not80 independent cities.

| Policy | Before remittance/revenue % | Final % | Before failure episodes | Final episodes | Pairs with more episodes |
| --- | ---: | ---: | ---: | ---: | ---: |
| AUTO2 |0.061122288|0.088759679|80|52|3/16|
| ZERO_SLACK |0.194778204|0.192379116|67|51|3/16|
| ZERO_HOT |4.668742325|1.251922116|45|28|3/16|
| SHOCK |5.877349694|2.730564514|50|51|5/16|
| HIGH |2.432930655|0.504158290|47|49|5/16|
| All pooled |0.111123032|0.117293098|289|231|19/80|

The default young AUTO2 window m241..600 falls from**5.478031103%** to
**0.754076323%**. The young HOT window m241..402 falls from**5.254215701%** to
**1.612898388%**. The all-policy pooled share instead rises slightly; it is
not reported as an aggregate reduction. Failure episodes fall overall,
but19 pairs increase,51 decrease and10 are equal; runs with any episode
fall from78/80 to70/80. Thus the brief's **no-new-bank-failures aim is not
met**, despite the hard PASS. Formerly healthy HOT/6 now has1 increment at
m234 (before0), while HOT/4 remains0 (before0). The prior historical4ff
counts and copy-city measurements are not relabelled as these final results.

### Audit observation limit

The raw native maximum audit residual is **not observed/exported** by this
ensemble. Exported audit_residual has maximum absolute value0, because
Ensemble.Adapter.auditResidual maps passing residual/relative/drift tests
to0; neither raw zero nor a raw cent ceiling follows from that export.
Report the actual M1 verdict and0 failed/unread audit months, not an invented
raw maximum. The named native bank, monetary, gold and saved-flow cent checks
and their actual recorded residuals remain the separate evidence above.
No check or threshold is weakened to hide this measurement limitation.

### Remaining formerly healthy HOT/6 event

The helper's read-only follow-up
`20261128-054148-249-CBK3-res3-fba4` checks final m234 against the earlier
trace. It is **not the same event**: the committed4ff/c52 trace covers
seed6 m132/133/135/300/312 and seed4 m176. Its old loss bridges and2406
covered scheduled snapshots cannot certify final aed's changed m234 path.
Baseline842 has no episode; final increments0 to1 between233 and234, while
historical4ff already has counter3 at both months.

The sole Bank.failures increment is conditional on shortfall=-equity()>0
and not already in resolution; equity is assets less liabilities. This
establishes **book insolvency at the increment**. The export does not prove
or exclude a collateral-only failure of an economically solvent bank, nor
establish correct carrying values or the size/cause of the loss. Final closing
capital ratio moves8.54134033% to13.4722101%, after repair; that repaired
number is not evidence of pre-rescue solvency. The CSV cannot identify
which of Game's two resolution checks produced the increment.

The export's cb_advances field is **advances to the treasury**, not bank H.
Its zero cannot show that the bank's window did not bind. Authoritative
H/deferred/interest-due, raw collateral/request, foreign principal/rate/bill,
settlement gap, desk/stable room and provision/mark/premium loss bridge are
not exported here. Closing household deposits or treasury cash cannot
reconstruct an intra-month run or rescue payment. Thus the old BPM1
seed4/176 premium proof is **not assigned to this new event**, and economic
solvency and contributory funding/collateral expense remain unobserved.
There is no evidence proving a solvent-bank/collateral-only failure, and
this event is not certified harmless by importing the old diagnosis.

Exact rows/source legs and limits are kept in
`../codex-CBK3-res3/target/CBK3-res3-final-HOT6-m234.txt`. No new replay,
fixture, model/scorer edit or process control was used; helper turn26 ended
with no jobs. The actual M1/F1 PASS, the unmet no-new-failures aim and these
limits all go to the orchestrator with the dry-run request. The approved
model remains unchanged.

## ASH1 merge and authorized dry-run preparation, 2026-11-28

Orchestrator mail20261128-062902-458 gives the box slot after ASH1 ships as
0.9.218 at39fcbee71. Merge parents are approved CBK3 records07e656579 and
that main. No Java conflict occurs: Game, AllChecks and BuildScreen merge
automatically. The nine conflicts are generated dials/harness indexes,
BuildScreen/Game/GameFiles/MoneyAudit/PriceLevel/README maps and month-order;
regeneration resolves them. Merged **--fast0** compiles and regenerates, and
**BankRulesCheck0** is the first completed named result; the rest are running.

The helper's independent two-parent source review
`20261128-063739-749-CBK3-res3-e197` finds no semantic collision. Bank,
BankRules, CentralBank, DebtManager and SimulationEngine are byte-identical
to07. DataSave, HistorySave, GameFiles and MoneyAudit code are unchanged.
Game keeps CBK3's raw window capacity, actual-H and interest-due suppliers,
the monthly invoice/collection/H/F settlement, and the later principal-only
settle after hot withdrawals/paper trades before the audit. No second bill
or ASH1 replacement of these legs appears.

ASH1 adds the once-struck wires share after research posts and before the
bank/fund clocks/jobs, plus its grid callback, founding reset and site-pace
readers. Its power implementation matches its parent. AllChecks retains
UiSteadyCheck and appends TownGridCheck. Held wiresShare's saved meaning and
format67 remain ASH1's; there is no conversion or old-save refusal bypass.
The helper's turn27 ends with no jobs. No CBK3 model mend is made.

The orchestrator explicitly accepts the existing aed ensemble for this
merge unless CBK3's model code conflicts/changes. Neither condition occurs;
**no ensemble is rerun**. Its published source hash/data and the already
recorded observation limits remain truthful, rather than claiming a new
80-run measurement of the combined power/monetary tree.

Named checks run strictly one at a time, with the real Earth2/World1 options:
BankRulesCheck, BankFailureCheck, MonetaryCheck, SaveFileCheck,
GoldStandardCheck, TownGridCheck, SubstationCheck, PowerPlantsCheck,
PowerPlanCheck, PowerPriceCheck, then exact native BankCheck and full
CentralBankCheck. Exact invocation avoids BankCheck's substring selecting
CentralBankCheck twice and preserves the existing 7200-second completion
budget and every model assertion. The additional merge-reader/posting checks
are LabourCheck, ReadPathCheck, BuildAdviceCheck, BuildCardCheck,
ConstructionControlCheck and CentralBankStatementCheck, also sequential.
No ALL suite is launched locally; the authorized box dry run follows green.

The merged named verification now completes **all18 harnesses exit0**:
BankRulesCheck, BankFailureCheck, MonetaryCheck, SaveFileCheck,
GoldStandardCheck, TownGridCheck, SubstationCheck, PowerPlantsCheck,
PowerPlanCheck, PowerPriceCheck, exact BankCheck and full exact
CentralBankCheck; then LabourCheck, ReadPathCheck, BuildAdviceCheck,
BuildCardCheck, ConstructionControlCheck and CentralBankStatementCheck.
Both sequential controllers exit0, with no overlap and no test/mode/fixture
change. Full CentralBankCheck completes all24 played shapes and its post-grid
save assertions. Logs are `target/CBK3-ASH1-named.txt`,
`target/CBK3-ASH1-extra-named.txt` and `target/CBK3-ASH1-<Name>.log/.txt`.
The wrappers have0 firm stale findings,212 soft.

The gold continuation still actually invoices29.0330421737 then29.3173240450,
collects0 then58.3503662186, and closes its two audits at
-4.71800376545e-12 and-1.04591890704e-11. This is the subsequent audited
continuation; its earlier reserve drain remains labelled pre-audit fixture
preparation, with no paying pool. TownGridCheck's separate money section
also passes. No raw ensemble maximum is inferred from these named cases.

Final pre-dry fetch still names39fcbee71; it is fully included in merge
794d2068b. The only later generated changes are six map date stamps to the
actual November28 regeneration date, not source changes. The finalized merge
and this verification record are pushed before the already authorized dry
run; no repeat permission or ensemble is requested. All named JVMs and the
merge-review helper have ended before the box run starts.

## Authorized ASH1-merge dry run: stopped, 2026-11-28

The actual gate exits **1** at12:40:44UTC; the suite takes1844 seconds and
exits4. This is a completed failed verdict, not the earlier ensemble SSH
transport failure. Kept run
`/home/jerus/cbs/runs/cbs-gate-CBK3-VRJmGJS3`, copied home to
`C:/Users/Jerus/AppData/Local/Temp/cbs-gate-CBK3-VRJmGJS3`; driver
`target/CBK3-dry-final.txt`. Its two recorded commits are branch
`b3526c053d75e4d800c16bfbd379defdb2271113` and main
`39fcbee71e51de2e6359a305757c07554a17ca20`.

The five unnamed framework lines encode three CreditCrunchCheck assertions
plus exit/count summaries: the combined ground/issue-quote/short-bank-capital
premise; reading the order at the door price and offering owners the till
gap (HN1-A); and the premise that their issue falls short. These belong to
the financing-door case, not the earlier104/120 ground-preparation case
already repaired in this batch. The provisional marker stays present and
no green reusable-run comment is added while the cause is unclassified.

The read-only helper is checking exact failed conjuncts/operands, both
parents and ASH1's kept evidence, plus current queued ownership. No own
model/fixture defect is passed by a speculative main-class label. The dry
failure is reported to the orchestrator as a result; source remains
unchanged after the run. The gate's automatic return instruction asks for
each line to be mended or named with evidence, then a new mends handoff.

The helper29 report `20261128-084913-100-CBK3-res3-d724` confirms the full
run:264 registry/pick names,258 selected harnesses completed (243 passed,
15 failed),6 nightly members unselected;3 timing assertions soft and10 path
observations unscored. Fourteen failing harnesses match their existing named
records; only CreditCrunchCheck has new assertions. All943 kept manifest
hashes match, with no missing file; checks.complete is present. The failed
verdict is not relabelled green merely because evidence integrity is intact.

Against main39, CreditCrunchCheck changes only standGround. Its financing
door section and Game's owner-ask/quote/offer code are unchanged, but CBK3's
bank/equity/desk economics differ. ASH1's authoritative kept run passed that
case with offered1258.7/raised53.1; the candidate offers0/raises0. No exact
current queued CreditCrunch owner is verified, so this is not passed as
HT1-H or attributed to the shipped ASH1 batch.

Root uses a detached diagnostic checkout at b352, with only read-only
printfs added to the named CreditCrunchCheck, then runs that one harness on
the laptop with the same data options. The production worktree's code and
kept run stay untouched. Native diagnostic result is the same3 assertions
(batch exit4), so the added reads do not hide the failure. At m270:
ground=true, quote=false, company1 already has59801.892166915284 shares and
has traded. Price**0.011205122237234182** is below the native90%-of-fair
threshold**0.012899778056801222** (fair**0.014333086729779137**).
The door and lender both read**1664.0760568673868**, held cash is
**416.01901421684670**, and ask**1248.0570426505400** matches the full gap.
Asked quantity6, capital-short=true and lender-passed2 all fire; offered
and raised stay0. Thus the quote guard, not founder availability, ground,
door-price arithmetic or an epsilon margin, suppresses the issue.

★ Keep that native under-fair refusal and every original six-order,
capital-band, door-price and positive-partial-issue assertion. The fixture
must cause a genuinely supported market quote before claiming to exercise
the partial issue; no price/rate/cap/tolerance override or speculative
known-red exemption is used. The helper is preparing the smallest native
market-step/funded-quotation cause in a scratch patch for verification.
If source must change, the failed dry's source hash cannot be reused: return
through a fresh mends/review and gate, rather than calling this run ready.

The first isolated native-market probe changes no production source or
played month: it invokes Game.tradeShares once after the fixture's home,
ground, yard and till preparation, then holds and re-strikes the original
capital band on the resulting book. It keeps all original assertions.
The probe compiles and reproduces a supported native issue quote at m270:
a real10-for1 split leaves593491.02746987260 shares, price
0.0015996726302546006 above the native threshold0.0013777084853867374.
Cash is allowed to settle naturally, from416.01901421684670 to
408.39464745550230; no holding or quote is restored after the trades.
The actual six-block order offers1255.6814094118845, exactly its till gap,
and raises145.808593469327, a positive partial issue. The lender still
reads exactly1664.0760568673868 and passes2 of6; all band and quote
premises now pass.

That probe is **not adopted as a green mend**: the increased subscriptions
let the two-block slice obtain a mortgage, so the unchanged assertion that
the door trims for capital and buys outright still fails. Named batch exit4,
one assertion, native log in the private detached worktree's
`target/CBK3-credit-native-market.log`, wrapper `.txt`. The helper now
checks the actual smaller-slice credit law and how to cause the original
capital refusal while retaining the six-order, horizon, till fraction and
rate. No assertion is broadened to accept a different constraint, and no
model/default change has been made. Latest fetch still names main39fcbee71.

The second read-only probe completes exit4 with the original trim assertion
and a new, explicit missing-capacity precondition red. At the same m270,
the native two-block door price is554.69201895579560, held408.39464745550230,
household offering means145.80859346932700, RE principal1654.6231509461875,
sector allowance16.546231509461904 and shared insured allowance
1478.6172498565543. The actual issue is wholly domestic. Its two-block
residual0.48877803096624460 becomes principal0.52240722414210650, which
validly fits. A six-block refusal never proved this smaller refusal.
The derived subscription boundary is130.81627919651137; the native
mortgageShortOfCapital call, not that arithmetic alone, will prove any
prepared case. Private logs: `target/CBK3-credit-native-rooms.log/.txt`.
The proposed cause is an actual funded asset purchase by the subscribing
owners before the order, preserving their acquired claim and the recipient's
cash, then re-striking the original half-band. The existing positive partial
issue and outright capital-trim assertions remain unchanged.

### The financing fixture is caused natively, not exempted

★ Keep the original six-block order, m270 horizon/seed, ORDER_RATE, TILL
fraction, half-band hold, ground requirement, door-price comparison,
positive partial issue, capital-trim message and cleared refusal flag.
Prepare its two missing antecedents explicitly: run the native share market
once on the fixture's changed homes/ground/yard/till, then let the owners
buy one current offering budget of bank capital through the existing
Game.capitaliseBank path. Equity.offer and HouseholdBalance.subscribe debit
actual savings and register shares and acquisition basis; Bank.injectCapital
credits the same actual domestic cash and capital counter. The bank was
already listed and traded, so no founders or supplied price are involved.
The bank recapitalisation path uses its native price and does not impose
the RE quote gate; the target RE issue must still pass that unchanged gate.
No quote, fair value, rate, tolerance, seed, month, HOLD, TILL or growth rule
is supplied or relaxed. No purse/holding is restored after these trades.
The original half-band and lending rule are re-caused **after** the real
market/capital flows, so no stale pre-offer capital snapshot is asserted.

This is fixture preparation before the hand order, not an asserted extra
played month or a change to default model code. The subscription budget
falls by actual cell subscriptions, not an assumed uniform share. All
original assertions remain. New hard assertions establish the supported
quote, positive residual subscription budget, an outright-affordable first
block, native mortgageShortOfCapital on the next block after that budget,
and an actual wholly domestic partial issue within the measured budget.
They test the smaller refusal directly; a refusal for six is insufficient.

Helper34's bounded exact-path review (20261128-092729-937) finds complete
cash, share, basis and capital booking. Root's review corrects the proposed
cash scope: MoneyAudit.pooled excludes households. A domestic subscription
therefore increases those pools and reduces the owners' savings by the same
amount; the pooled subtotal alone is not conserved. The new isolated check
asserts the pool receipt, savings debit and their combined residual each
round to zero cents against the paid cash, with finite operands. It ends
before holdInBand's synthetic fixture capital/funding stocks, which are not
called a native cash transaction. No existing audit tolerance or law moves.

The isolated complete-transfer probe passes CreditCrunchCheck (exit0):
prior domestic bank subscription145.808593469327, no foreign subscription,
residual offering means116.66384977229106; pool receipt
145.80859346932266 and savings debit145.80859346932630, combined residual
**-3.637978807091713e-12**, zero cents. The subsequent genuine order asks
1255.6814094118845 and raises116.66384977229106. Its two-block residual
29.633521728002194 needs principal31.672384695517420 against the native
sector allowance16.546231509461904 (shared allowance1478.6172498565543).
The native capital-short predicate is true and the door builds1 block
outright, trimmed from6 for capital; every original assertion passes.
Private logs: `target/CBK3-credit-complete-transfer.log/.txt` in the
separate detached diagnostic worktree. The temporary assertion that the
non-household pooled subtotal alone stay flat failed by exactly the paid
cash; it was corrected to the full two-sided transfer, never waived.

Root adopts that native cause with less diagnostic output and adds explicit
listed/traded-bank, acquired-shares and acquisition-basis assertions.
No production model/save/UI code changes in this mend, so the accepted
ensemble's final model and its observation limits remain unchanged. The
failed VRJmGJS3 dry cannot be reused through the harness source change;
this returns through a new provisional mends/review and a fresh gate.

Final production-worktree verification completes **exit0** for both
`./batch-check.sh CreditCrunchCheck` and `./batch-check.sh --fast`, run
strictly sequentially, with Earth2/World1 and parallel1. Named log
`target/CBK3-credit-mend.log`, wrapper `target/CBK3-credit-mend.txt`, fast
`target/CBK3-credit-mend-fast.txt`; 0 firm stale findings,212 soft.
CreditCrunchCheck retains all42 original assertions and adds8 hard
antecedent/transaction assertions (50 total); acquired household shares
match the register's new issue and acquisition basis matches actual paid
cash. The final operands and transfer residual match the private green
probe above. Its door builds1 outright from6 for the bank's capital and
clears the refused flag. The eighteen named bank/power/merge-reader checks
already passed on this identical production model; none is masked by a new
known-red row. Maps are regenerated. Only CreditCrunchCheck and its generated
indexes change as source/derived artifacts in this mend; no default trace,
save field, monetary model, power model or threading code is changed.
The provisional marker stays, with no failed-run reuse comment. No JVM or
helper remains running at the fresh mends handoff.

The actual harness mend is commit1b76fce05; source diff against the failed
dry's b3526c053 is CreditCrunchCheck alone. Final fetch remains main
39fcbee71, fully included. The read-only pick is ALL264 through AllChecks;
its review line still names the batch's money classes and DataSave saves.
No ALL pick/suite is run on the laptop. The next action is a provisional
mends handoff for review and a fresh authorized box gate, not ready and not
reuse of VRJmGJS3. The accepted final-model ensemble is not repeated for
this harness-only repair.

## REV-CBK3c SHIP and renewed dry authorization, 2026-11-28

Orchestrator mail20261128-094343-311 reports Claude REV-CBK3c **SHIP** on
3e938553b. The review confirms all42 original labels identical and8 added,
the original law retained, and native cash conserved to the cent. The
post-ASH1 source merge landed both sides automatically; the fixture failure
is path dependence on the combined economic run, not a dropped merge leg.

The review's non-P1 qualification is explicit: holdInBand's setCash rebases
the bank after the native subscription. The additional bought capital is
therefore overwritten in the fixture stock; that prior native purchase
serves to commit the owners' liquid means, **not** to retain extra bank
lending capacity. Its matched cash/share/basis transfer is checked before
that synthetic rebase. The residual means116.66385 stand about11% below
the130.81628 subscription boundary; the native next-slice capital-short
assertion pins the condition, rather than relying on this margin alone.

The same mail grants the box's dry-run slot and subsequent readiness on a
green result. Fresh origin/main remains39fcbee71, already fully merged;
there is no new source merge or model change, no repeated ensemble or
long named checks. Only this requested review clarification is committed
and pushed before the fresh authorized dry. The provisional marker stays
until its actual completed verdict and final records are checked.

## Reviewed-model dry run: green, 2026-11-28

The fresh authorized dry completes **gate exit0** at14:21:14UTC
(10:21:14 Halifax), after a1854-second suite returning3, known reds only.
The original launcher ends0 and its retained artifacts are copied home.
Kept box run: `/home/jerus/cbs/runs/cbs-gate-CBK3-KODIcBIM`.
Home copy: `C:/Users/Jerus/AppData/Local/Temp/cbs-gate-CBK3-KODIcBIM`.
Driver: `target/CBK3-dry-reviewed.txt`. The run's two commits are branch
`24ee53f59ada9012749869d14a063257243053de` and main
`39fcbee71e51de2e6359a305757c07554a17ca20`.

That branch adds only the requested REV-CBK3c rebase qualification to the
reviewed3e938553b; the approved fixture source is1b76fce05. There is no
model/save/UI change after review, no main movement before this dry and no
source change afterward. The accepted final-model ensemble is retained
under the orchestrator's explicit ASH1-merge exception. The dry's suggested
0.9.219 is a publication preview; the branch still leaves VERSION and all
publisher-owned records alone.

The gate names only these14 inherited red harnesses: AutoBuildCheck,
CarCheck, DenominationCheck, FundCheck, GroundSkipCheck, HoldersCheck,
HousingDoorCheck, InvestCheck, OrderDoorCheck, PlanCheck, RailDrawCheck,
RoadCheck, SectorFlowCheck and UiSteadyCheck. This is the same set of main
classes classified in the preceding failed run after excluding the now
mended CreditCrunchCheck; no new CreditCrunch exemption or tolerance is
added. The actual exit is distinguished from the earlier failed VRJmGJS3
run and from an incomplete live snapshot. The completed retained evidence
is checked separately before the reusable-run comment and readiness.

Final read-only verification (helper37, mail20261128-102720-666) confirms
264 registry/pick names,258 selected **all completed**:244 passed and14
named-red harnesses;6 NIGHTLY-tier members were unselected. Both unnamed
and new-red lists are empty. AllChecks itself takes1834 seconds. The
completion marker is present and **all943 manifest entries match**, with
0 missing,0 mismatched and0 malformed. The retained files are not modified
or executed. Full helper verification is in its own target
`CBK3-res3-reviewed-dry-verification.txt/.json`.

CreditCrunchCheck exits0:50 source assertion call sites,49 printed OK and
0 FAIL. The other source site is the fail-only missing-template guard;
all8 added antecedent/transaction assertions actually fire and pass. The
box reproduces the native supported RE quote, complete domestic bank
subscription with acquired shares/basis, residual
-3.637978807091713e-12 (zero cents), positive partial issue116.66384977229106,
and native next-slice capital refusal31.67238469551742 over16.546231509461904.
The original half-band/growth.01, exact door price, offered whole till gap,
Built1-out-of6 capital trim and cleared refusal flag all pass.

Selected BankRulesCheck, BankCheck, CentralBankCheck, MonetaryCheck,
BankFailureCheck, CentralBankStatementCheck, EquityCheck and ExchangeCheck
all exit0; CentralBankCheck takes663.7 seconds. Selected TownGridCheck,
SubstationCheck, PowerPlanCheck, PowerPriceCheck and LabourCheck also exit0,
as do SaveFileCheck, ReadPathCheck, BuildAdviceCheck, BuildCardCheck and
ConstructionControlCheck. The earlier eighteen sequential named merge
checks remain separately recorded, including PowerPlantsCheck.

The3 timing SOFT assertions (SaveSlot1,Map2) and10 path OBSERVED entries
(AutoBuild1,PriceLevel5,Scale2,Housing1,Prize1) are separate unscored evidence,
not concealed hard reds. Ten Monocle display harnesses actually start;
none is skipped. No new known-red row, fixture exemption, money tolerance
or save check is introduced. Only these notes and the ship file change
since24ee53f59; the reusable gate-run comment names KODIcBIM on the first
line. Under the explicit readiness go, the provisional marker is removed
last in its own commit and checked absent on origin before readiness mail.
All native jobs and the verification helper have ended.

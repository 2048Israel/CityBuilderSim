# CBK3 - the window against collateral and the bank's rules as dials

Work in progress; not a ship record. Base b2f50a9ef, 0.9.213, format66.
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

## Check record and integration

Pre-CBK1 physical model6ff36d2d8:

- BankRulesCheck: nine directional controls/defaults, haircuts/market government
  collateral, direct advance cap, no equity collateral, shared fill cash/no
  hot funding, actual save writer/loader and missing keys all green. The120
  copied-city months pass all collateral, desk and cent assertions.
- BankFailureCheck: full24 seeds/120 months,2880 monthly audits and explicitly
  finite equity movements green;5/24 ordinary failures and317 splits versus
  the failed model's3162. The final ensemble still determines the paired
  no-new-failure finding against the retained baseline.
- BankCheck exact, MonetaryCheck, EquityCheck, ExchangeCheck exact and
  MarketCheck exact green. EquityCheck includes109 lines for mature valuation
  and96 for the shared floor/recovery regression; original young rules stay.
- SaveFileCheck: exit3, only registered REV-HC1 P5 populated format62 city.
  New CBK3 save checks pass. Rejudging the saved suite confirmed native3;
  PowerShell otherwise normalised a nonzero child exit to1. Subsequent calls
  explicitly propagate LASTEXITCODE. target/CBK3-save-suite.txt and
  target/CBK3-save-judge.txt retain that evidence.
- CentralBankCheck: NOT green. Three1740s cutoffs, latest6ff run20/24 fixed
  strictness rows complete, with no preceding assertion red. Four rows and
  later rules, saves and section22 par conservation are unexecuted. This is
  incomplete, not the registered HT1-H assertion pair and not yet classified
  as an outside runtime red. Log target/CBK3-central-6ff-timeout.log.

The initial Bank/Central tests contained laws equating all wholesale borrowing
with the window alone, excluding foreign price/share and actual billing.
They now reconcile both actual tranches and the central bank's own portion.
The insolvent deposit-funded bank still freezes new credit. Fixtures, shapes,
24-case/horizon counts, conservation, cent and save bounds are unchanged.
AllChecks uses substring selection: BankCheck also selects CentralBankCheck
and YoungBankCheck, ExchangeCheck selects DebtExchangeCheck, MarketCheck
selects WorldMarketCheck. Exact JVMs avoided unintended repeats, retaining
normal data, soft timing, memory and1740s timeout settings. YoungBank passed.

CBK1 merged without a source conflict. The only conflicts were generated
indexes/maps, regenerated from the merged source. Its central Policy page
now calls the identical BankRulesPane as Policy, with its own redraw callback;
there is one BankRules model. No CBK2 pace/direct-purchase control is built.
CLAUDE's interface count is55. Post-merge --fast: compile green, zero firm
Stale findings,209 informational soft. Post-merge BankRulesCheck reproduces every copied-city total above exactly,
and all three mixed-collateral bit comparisons pass. CentralBankStatementCheck
passes its played statements, read-only twins and247-slot old/new save paths.
The final full CentralBankCheck is running on ef5c40fa7 after the two
explicitly aborted diagnostic runs recorded below.

The helper's immutable6ff source review found owned repeated collateral work:
each bank-held bond's marketValue called curve(), whose key scanned the whole
debt book even on a cache hit. The forecast and curve were already cached;
it was not rebuilding the forecast per paper. Now each collateral operation
lazily reads one domestic curve and passes it into the same price routine,
keeping order, flows, callable treatment, holder fractions and clamps. Nothing
survives the call. strikePrices also reads its unmutated window target once.
The harness compares the aggregate to separate public prices to the bit for
three rates with partial holdings, bills, callable/ordinary and foreign paper.
No AP6 held figure or AP7 engine optimisation is added. The independent
wholesale quote still uses its original YieldCurve formula. No timeout bound
or strictness fixture was shortened to conceal the cost. A measured rerun,
not this source diagnosis, must settle whether the cutoff is mended.

* ★ Design choice: saved statements preserve actual historical advances,
  never substitute today's new collateral target on load. The pre-CBK3
  residual is therefore captioned net funding outside settled wholesale,
  with a tooltip distinguishing the current deposit projection. The supplied
  city's domestic books4798.148 plus hot549.655 are not the same measure as
  its5223.615 allocation; late trades/hot movements and FW2 saved pre-trade
  books prevent reconstructing a missing flow from its ending state. No
  foreign creditor, money issue or retrospective deposit stock is invented.

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

## Publication and remaining work

Gameplay write-up: claude/the-window-against-collateral.md. Eye check: the two
rule pages retain thumb/scroll/nodes through month redraws and set the same
saved values. No separate pending eye-check row is needed.

The initial brief's manual-publication instruction was superseded by
orchestrator mail20261126-193817-304: manual editions belong to the
orchestrator at a milestone/Jerus request. The builder stops that route,
leaves docs/manual.* untouched and provides the player text below. This is
not a readiness block. Before that correction, the browser host was
unavailable; native Edge reached the owned pinned-version6 artifact and
then stopped on URL verification. No artifact was edited/exported/published;
no further browser/app input is being attempted. The task tab's final window
state is unverified.

## For the manual

Use the Bank tab to choose the commercial bank or the central bank. The
central bank's Policy page and Policy > Money > Bank rules show the same
banking rules. Moving one changes the rule at once, on both pages, and the
city saves it. The policy-rate proposal still uses Policy's Apply control.

The window lends against eligible collateral, after a haircut: it counts70%
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
| Countercyclical capital buffer |0%|Adds to the bank's conservation capital target, up to2.5%|
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

Finish final named/integration checks and lawful Central timeout resolution;
review the diff, main/RES3 movement and pick, then push provisional ship/LE1
queue row and mail mends at with the review line. The stranger's money/save
review precedes the final80-run ensemble; an ALL pick needs the orchestrator's
separate dry-run go. No final readiness or gate reuse is claimed here.

The ab69 full Central run was deliberately stopped at19:43 after six of the
24 strictness rows, no preceding assertion red. Only its verified child
PID27760 (parent9928, created19:36:26 under this named runner) was ended;
no other process tree was touched. The wrapper consequently reports exit-1
and no FAIL line. This is an aborted/incomplete run, never a green verdict;
its partial log is target/CBK3-central-ab69-stopped.log.

Source inspection found another owned cost: windowFunding priced all eligible
government holdings even when its final requested amount was zero. It now
computes that unchanged request first, returning only on requested==0; NaN requests
still follow the original minimum, and positive requests still value all
collateral. The harness causes zero and positive requests and counts actual
valuation callbacks. The change introduces no held value or writer, and the
same full harness will be restarted after copied-city/default/save checks.
The integration helper found no accounting or247-slot save conflict, but
flagged the policy-rate card's wording: it now says the policy rate is the
window's base before the chosen spread. No model rate is changed by that text.

Helper20 confirms the zero-request identity for valid finite collateral and
no economic/cache-writer side effect. The comment was narrowed to NaN
requests: an arbitrary callback returning a collateral NaN is no longer
called on zero demand; universal NaN equivalence is not claimed. Actual
positive-demand collateral remains fully priced and capped. No check of
money, saved amounts or valid positive lending is weakened.

Final zero-request build: --fast green (zero firm/209 soft), BankRulesCheck
including both actual callback-count assertions and all120 copied-city
months green. It again reproduces remittance476.704248490,
revenue33741.426003542,1.4128160%, three failures and worst audit
1.95086613530e-10 exactly. The three mixed government-collateral prices are
bit-identical. The skipped zero-demand reader changes no measured outcome.

The3a4 Central run was stopped at19:56 after three strictness rows, no prior
assertion red, to remove the remaining owned repeated collateral reads.
Only verified PID28844 (parent29616, named runner created19:51:15) was ended.
Its partial log/wrapper are target/CBK3-central-3a4-stopped.log and
CBK3-central-3a4-wrapper-stopped.txt. Both aborted runs have wrapper exit-1;
neither supplies a complete harness verdict or a known-red exemption.

Game now requests gross wholesale need; CentralBank independently applies
its existing callback's eligible window cap. Since cap<=gross, this gives
the identical capped target. A positive settle posts the already-capped
difference through the same private posting body; public direct advances
still enforce remaining capacity. One operation prices capacity once.
There is no saved or held snapshot, new public API or intervening writer.
Helper21 checked exact finite arithmetic, callback purity, both allowed
posting callers and old-claim rollover; it recommends this bounded refactor.
Arbitrary stateful callbacks/concurrent mutation/NaN inputs are outside that
identity's premise. The new harness verifies one callback and exact actual
principal issued; both pass. All120 copied-city totals and audits remain
identical to6ff36d2d8. No budget, fixed case, shape or horizon is changed.

SaveFileCheck on3a4 completed native exit3, the sole existing REV-HC1 P5 red;
all new CBK3 and CBK1 save paths pass. The subsequent settlement refactor
adds no saved state or array slot. MonetaryCheck on ef5c40fa7 passes; full Central verification
follows; the final fast build will refresh all indexes before mends.

Additional Part1 observation requested by orchestrator200018-449, from the
desk chat's scratch m143 (not a new model fixture): CB assets Cr10.9M
(window9.5M/vault1.4M), M0 9.7M/M2 8.2M; bank capital6.7% against8%,
trailing12 ROE-124%, NIM-3.73%, loans11.4M/deposits8.2M, savers0%,
funding8.24%, preferred ask335k. Add the Bank page's own margin, capital,
ROE and preferred-offer reads to the original/candidate copied-city measure,
including m143 and m253. Do not reconstruct a monthly flow from an ending
stock or add repeated offers into a fictitious cumulative request.

## Final per-operation callable work and bank health observation

The normal full Central run on ef5c40fa7 ended at1740s with18/24 strictness
rows, no preceding assertion failure. Its six remaining rows and subsequent
rules/save/section22 checks are unexecuted. Native exit4 is INCOMPLETE, not
an outside-red classification. The complete preserved log/wrapper are
 target/CBK3-central-ef5-timeout.log and -timeout-wrapper.txt.

* ★ Design choice: within ONE government collateral valuation, prepare the
  existing tree at the longest eligible bank-held domestic callable horizon
  and read its volatility once. Preserve original price/sum/holder order and
  every scalar/foreign/matured branch. The old cold insertion-order traversal
  could refit progressively longer trees and reread61 history rates per bond.
  No portfolio/sigma snapshot survives the operation; no new cache/writer is
  added. Existing treeOn already permits longer trees to price shorter flows.

Helper23 proved prefix independence from CallTree.fit/price source and the
conditional repeated work, without claiming its share of runtime. Helper24
supplied166 regression lines, reviewed/adopted: two fresh independent cold
books, three increasing partial-held callable maxima, future calls with real
optionality/above-par prices, excluded longer unheld/foreign/matured paper,
holder mutation between operations, exact ordered sum bits and1 versus3
volatility reads. Direct longer/shorter fits agree in18 bit comparisons on
flat/sloped/clipped curves at floor/positive sigma. Every assertion passes;
no timing threshold, existing law or fixture was reduced. The older same-book
three-rate test prewarmed its tree, so it alone did not cause this condition.
All120 copied-city totals and cent/collateral/desk assertions remain EXACTLY
as6ff36d2d8. Final full Central verification is still due on this last mend.

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

Final BankCheck exact run on7d92c82c6 passes. Helper25 source review proves
section22 owns its60-month town, held-line/par branches and scratch saves;
howStrict neither uses nor updates its cumulative audit counters. Its call
now runs before howStrict, retaining every fixture/assertion/month and the
same counters. This moves independent conservation/save coverage earlier;
it does NOT make a cutoff green. Section21 still has mandatory assertions
and saved-rule/default checks after its table and must finish in full.
The call swap adds/removes one line, changing no test budget or predicate.
The attempted baseline population comparison is unavailable: retained842
Ensemble AUTO2seed12 and strict40/3/21 differ in fitted founding, market seed,
player/works and advise cadence. No population drift is attributed to CBK3
from that mismatched comparison, and no FS10 timeout exemption is claimed.

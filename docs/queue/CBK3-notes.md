# CBK3 - the window against collateral and the bank's rules as dials

Work in progress; not a ship record. Base b2f50a9ef, 0.9.213, format66.
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

## Check record so far

- Initial --fast: green; later added UI file required CLAUDE count54, now mended.
- BankRulesCheck: current model and120-month copied-city run green, including
  direct-advance cap, no equity collateral, shared fill budget/no hot-money
  budget, tighter controls, defaults, actual saves and missing-key defaults.
- CentralBankCheck initial pre-final-quote run: two stale assertions equated
  the wholesale blend/bill with the window alone, plus1740s timeout in its fixed
 24-city strictness measurement. No shapes, horizon, shock, cent or save bound
  changed. The stale laws now test both bills and the CB's actual portion at
  original tolerances. The current final-model run is in progress.
- BankCheck: five stale funding expectations diagnosed by the helper: omitted
  foreign term price/share, wholesale blend mistaken for window rate/cost,
  and insolvency mistaken for100% overnight funding. All fixtures and original
  tolerances remain. Actual tranche/reconciliation assertions replace those
  identities; the deposit-funded failed bank still freezes new credit.
  These are not the known HT1-H played-loss premises; those passed in this run.
- AllChecks name matching is substring based: the call BankCheck also selected
  CentralBankCheck and YoungBankCheck. Young passed; Central's current run
  continues. Further narrow runs use serial settings, with exact named JVMs
  where needed to avoid rerunning an unrelated long harness.

## Ensemble observations and explicit exception

The published0.9.212/MR1 baseline is snapshot84234b69c. Its raw CSV/header/logs
on gatebox confirm no remittance/revenue/failure counters; F1=0 measures
findings, not ordinary rescued bank failures. No omitted flow was inferred
from cash, M0 or ending capital.

The orchestrator approved the one supplemental replay exception in
20261126-173158-709: retained baseline classes, identical80 policies/seeds,
getter observations only, proving all eight default trace md5s equal with and
without observers. The job started by mail174132-646 and is running at
/home/jerus/cbs/observations/CBK3-before-20261126. Its copied class manifest must
match retained MR1. Original archive is untouched. The runner uses the box's
ensemble/box locks and yields to gates/nightly.

Ensemble.fundingColumns adds only cb_remitted, budget_revenue, their share,
cumulative bank_failures including rescued episodes, and cb_remitted_cum.
ObserverTrace attaches that exact getter-only function to the supported
LongPlaytest observer, keeping results outside Game. The scorer slice adds
optional informational observations and baseline metadata, leaving every
existing criterion/threshold/verdict and M1/F1 unchanged. Weighted flow sums,
not average monthly ratios, define the revenue share. Results follow when done.

## Remaining before mends

Finish required named checks, merge CBK1 once shipped and wire the SAME
BankRulesPane onto its central policy page, merge main/RES3 as required,
review final diff, refresh maps/fast/pick, and write the provisional ship file.
The final CBK3 ensemble still follows stranger SHIP; this baseline exception
is not approval to skip that review. The manual uses its artifact publication
route; generated docs/manual files are unchanged. Gameplay write-up:
claude/the-window-against-collateral.md. Eye check: both rule pages retain nodes,
thumb and scroll across month redraws and show the same saved values.

Build verification update: all six existing ensemble self-tests pass under Git
Bash (target/CBK3-score-tests.txt), along with the seven bank-observer cases.
The earlier PowerShell invocation could not locate bash for four runner cases;
rerunning through the required shell resolved that environment error.
The baseline proof passed all eight byte comparisons and retained-class
manifests. Its first ensemble start rejected an equivalent explicit-path
settings string; resuming with main-data autodiscovery retained the original
snapshot settings and started all 80 runs. No archive or guard was changed.

## Equity valuation lead, included in this fix

The orchestrator relayed RES3's lead in mail 20261126-174636-318. The helper
confirmed Equity.fairValue returned the last offering quote at zero mature
income/spare value, but capitalised even tiny positive income. Exchange's
fair mark and Bank.markSecurities carry that discontinuity into inventory,
trading income, equity and desk capacity. This is a separate amplifier of the
funding defect; source alone does not establish its share of the observed
city's desk stock.

* ★ Design choice: remove the mature listing's zero-only historical-price
  fallback. Its existing spare-cash plus capitalised-income valuation now
  reaches the existing monetary floor continuously. Keep the under-12-month
  prospects rule, actual income, cash, holdings and mark-loss accounting.
  A stale issue quote is not a guarantee of recoverable value. This preserves
  the existing no-growth forecasting premise rather than inventing a book or
  option-value floor.

[IFRS 13's current-market exit-price definition](https://www.ifrs.org/issued-standards/list-of-standards/ifrs-13-fair-value-measurement/)
supports rejecting a historical issue-price guarantee. Choosing the existing
income model's continuous zero limit is a model inference, not a claim that
all real loss-making companies are worthless. The copied-city measurement
above was rerun on commit 6ff36d2d8 with the shared-floor correction: 1.4128160%, three failures, cent audit
and both funding bounds green. The preceding 1.7921789% run is preserved in
target/CBK3-evidence-before-equity.log and is superseded.

* ★ Design choice: concentration's absolute economic capital add-on remains
  unchanged when the regulatory minimum moves; its equivalent book weight is
  derived from the current minimum immediately, not cached until next month.
  Risk staging retains its calibrated 90% reference: lowering a regulatory
  new-credit ceiling does not itself make an existing borrower riskier.
  The new harness causes a nonzero concentration and changes the dial without
  refreshing the book; required equity rises and the add-on remains exact.

The new EquityCheck regression caught an implementation error before any
valuation commit: identical return text existed in priceOf and fairValue.
The first edit changed the young-company method, leaving the mature fallback
in place. Seven new assertions failed, including the unchanged young-company
law. The helper proved the wrong site from source and the fixture outputs;
the parent restored priceOf and changed only mature fairValue. No expected
value or tolerance was changed. The earlier 1.7981594% replay and 5/24 bank
failure observation used that wrong-site edit and are provisional, preserved
as target/CBK3-evidence-wrong-return.log and target/CBK3-bankfailure-wrong-return.log.
Both are rerun on the corrected source before review.

Corrected mature-only run (commit a32abc1a1): BankRulesCheck and all 120 monthly
collateral/desk/cent assertions pass. Its 4.2055303% remittance share remains
above the cited Fed 2021 comparison; it is measured, not forced to that target.
Five additional failures are one fewer than the same original continuation.
The mature valuation correction books real mark losses; the earlier three-
failure interim funding-only figure is not the final model result.

Broader validation on a32abc1a1 found a real defect: BankFailureCheck seed 10
produced non-finite audit pools and bank equity, and its cent assertion failed.
The copied-city result above is not sufficient to ship this model. The helper
is tracing the first non-finite value, including the mature-floor/split unit
interaction. No assertion, tolerance or fixture is relaxed. Full failed log:
target/CBK3-bankfailure-nan.log. Further model checks wait for the mend.

Central timeout diagnosis (helper report 20261126-182123-744): the preserved
run completed 20/24 fixed strictness rows before 1740s, with no earlier
assertion failure. The 26,400 nominal city-month table/helpers are unchanged
from main. Four rows and subsequent rule/save/section22 par-conservation
assertions were not executed. This is TIMED OUT/INCOMPLETE, not green; main's
latest gate has no Central timeout, and its registered HT1-H assertion pair
is a different red. No budget, shapes, horizon or bounds are changed. A final
named run remains due, and any repeated cutoff needs honest classification.

* ★ Design choice: repair the existing no-worthless-consolidation law with
  one threshold. Equity's presentation floor is anchored to expected prices;
  Exchange's nominal floor was not. Exchange now uses the greater threshold
  consistently to strike fair value, classify atFloor and skip consolidation.
  A presentation epsilon is not positive business value. There is no new saved
  state and no change to cash, income, ownership or legitimate split factors.
  This mends the causal mismatch rather than clipping non-finite balances.

The completed bad run first failed seed 6 at m76, then seed 10 at m113,
13/20 at m76 and22 at m75. It recorded3162 split/consolidation events. The old
log does not identify the first overflowing company/operator, so that is not
claimed. The helper found the source amplification and is building a direct
never-traded floor/recovery regression. It also found an equity-check blind
spot: abs(NaN)>bound is false; explicit finiteness strengthens that assertion
without changing its existing tolerance. MonetaryCheck passes on the shared-
floor repair; SaveFileCheck is running. Full24-seed audit replay remains due.

Final shared-floor replay on6ff36d2d8: BankFailureCheck passes all24seeds,
2880 monthly money audits and explicitly finite equity movements, with5/24
ordinary failures and317 splits/consolidations (the failed run had3162).
BankRulesCheck's120-month copiedcity also passes:1.4128160%,3additional
failuresvs6original. Its full values above are current; the4.2055303% pre-
shared-floor measurement is kept in target/CBK3-evidence-before-shared-floor.log
and is superseded. Monetary/Equity/Exchange aregreen; SaveFile is known-red-
only (registered REV-HC1P5). Rejudging saved suite gives native exit3;
PowerShell otherwise normalizes a nonzero child exit to1, so later calls
explicitly propagate LASTEXITCODE. Bounds/fixtures areunchanged.

The approved baseline replay has finished80/80. All28 stored criterion
values/verdicts and old policy summaries match0.9.212 exactly:24/26 overall,
M1/F1hard10/10, A1/A2 informational FAIL asbefore, H1retired. All102352
recorded rows have complete flow/failure observations;114 monthly ratio
cells with nonpositive monthly revenue remain unavailable and are not
averaged. The new observations count289 cumulative bank failures in78/80
runs (max8), despiteF1=0. Recorded windows are AUTO2m2-2400, ZERO_SLACK2-2040,
ZERO_HOT2-402, SHOCK2-840, HIGH2-720. Candidate comparison must match them.
The new baseline is docs/ensemble/CBK3-before.md. Verdict mailed185017-677;
there is no CBK3 heavy job left on the box. Final candidate ensemble is not
run or certified until strangerSHIP.

Final pre-CBK1 named Central run on6ff36d2d8 also stopped at1740s:20/24
informational rows completed, no earlier assertion red, remaining four rows
and subsequent rule/save/par-conservation assertions unexecuted. Native
wrapper exit4, target/CBK3-central-6ff-timeout.log. It remains INCOMPLETE.
The parent has commissioned a source-only check of new collateral/wholesale
curve work before attributing the cutoff to a fixture/runtime class.

CBK1 shipped at482dbfab2 (0.9.214, stillformat66); orchestrator message
20261126-190438-166 says merge and put the SAME banking rules on its Policy
page. The merge follows this finished run to avoid changing an active check's
source beneath it. RES3 has not yet been merged.

Manual route inspection: the browser connector host was unavailable. Native
Edge reached the owned CityBuilderSim artifact, pinned version6, whose page
still says build0.8.11/format40. No artifact content was changed. The native
input tool then stopped because it could not verify the current Windows
browser URL, so no further UI input is allowed this turn. The minimization
attempt's outcome is not verified; the task tab may remain. Generated manual
files are untouched, and the release copy remains a draft until the required
artifact publication route can be completed. Computer-use SKILL.md and its
guidance were read; its interrupted-turn instruction is to stop app input.

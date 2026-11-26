# CBK3 ? build notes

Base: b2f50a9ef, 0.9.213, format 66. Work in progress.

## Evidence before Part 1

The supplied `C:/Users/Jerus/AppData/Local/Temp/cbs-CBK3-autosave/1257`
was copied by BankRulesCheck into a fresh TempDirs folder and loaded through
package-private GameFiles(dir, legacy). No player save directory was accessed.
The loaded m133 values were desk 9861.906318872, cash -17366.424253986,
window 12142.809656253, trailing treasury revenue 1998.942287585.
Played m134?253 without changing the saved settings: remittance 10134.551266349,
revenue 48341.489897126, share 20.9644992%; six further bank failures.
The first twelve months' share was 46.5852538%. Worst money residual
1.02318153949e-10 save units, within the unchanged cent tolerance.
Kept local logs: target/CBK3-evidence-before.log and target/CBK3-before.txt.

The read-only CBK3-trace helper identified Exchange.postDesk/deskLimits and
Bank.deskCanCarry: capital/name/book caps are rechecked at fills but there is
no stable cash budget. deskPays subtracts cash unconditionally. fundToCover
prices all funding past deposits at the window, and settleWindow has no
collateral cap. The month's equity trading follows the window settlement.
The helper changed no code; the builder keeps the model decision and review.

## Reality checked

- [Fed collateral eligibility](https://www.frbdiscountwindow.org/pages/collateral/collateral_eligibility):
  ordinary window collateral includes eligible loans and debt securities;
  equity is absent, and loans require an enforceable first-priority interest.
- [Fed margins](https://www.frbdiscountwindow.org/pages/collateral/collateral_valuation):
  Treasury lending values are 95?99% of market value by duration. Loan lending
  values depend on risk, maturity and estimated fair value.
- [Bank of Canada SLF](https://www.bankofcanada.ca/2026/09/assets-eligible-collateral-standing-liquidity-facility-310125/):
  non-mortgage loan haircuts include secured business 30%, unsecured business
  40%, secured consumer 65% and unsecured consumer 85%.
- [Bank of Canada ELA](https://www.bankofcanada.ca/markets/market-operations-liquidity-provision/framework-market-operations-liquidity-provision/emergency-lending-assistance/):
  emergency advances still require collateral; mortgages can qualify with
  case-specific valuation and haircuts.
- [Basel NSFR](https://www.bis.org/committees/bcbs/basel-framework/standard/nsf?allChapters=true),
  NSF30.14 and 30.31: short central-bank/interbank funding has 0% stable value;
  non-HQLA listed equity normally needs 85% stable funding.
- [Basel LCR](https://www.bis.org/committees/bcbs/basel-framework/standard/lcr?allChapters=true):
  HQLA must cover net stressed 30-day cash outflows at 100%; collateral
  eligibility alone does not make an asset a stable source of funding.
- [Fed Volcker rule](https://www.federalreserve.gov/supervisionreg/volcker-rule.htm)
  prohibits proprietary trading in general;
  [SEC market-making explanation](https://www.sec.gov/newsroom/speeches-statements/2014-spch020414jr)
  preserves customer market making with inventory tied to expected demand.

## Checks so far

- Initial `./batch-check.sh --fast`: green, no firm stale findings.
- Initial named BankRulesCheck evidence run: zero failures, 120 months.

## Part 1 measured

? Keep the exchange's customer market making, capital/name/book caps and
fair-price sale rule. Add a common core-cash budget to Bank.deskCanCarry,
which posting and every fill already call: net cash plus local deposits,
less the cash that hot depositors can withdraw. A fill spends the same
budget across every company. Existing inventory is refinanced rather than
sold below fair or written off.

? Domestic secured business loans and rental mortgages carry a 30% haircut;
the mortgage choice is the ELA-like secured-property approximation, not an
assertion that unsecuritised mortgages are SLF-eligible. Unsecured household
loans carry 85%; government paper 5%. Values are net of loss allowances;
equity, carry loans and unrated corporate bonds receive zero collateral value.
The window takes at most 100% of the remainder and excludes equity inventory
from its funded tranche. Foreign wholesale covers the rest at
DebtManager.foreignRate(), the world's short rate plus the city's credit spread.
The financing is settled again after equity/bond trading and hot withdrawals,
inside the same audit, without a second interest charge.

Final Part 1 scratch continuation m134?253: remittance 1164.481533619,
revenue 27303.596854126, share **4.2649382%** (before 20.9644992%).
Three bank failures (before six). Worst audit residual 1.73031367012e-10
save units, within the unchanged cent bound. Final window 1468.570822581; the existing
desk remains marked at market and its unsupported funding comes from abroad.
Kept local log: target/CBK3-evidence-part1-nsfr.log. Named BankRulesCheck passes
its direct-advance collateral, no equity collateral, stable budget shared at
fills, no hot-money desk budget, and copied-city audit checks.

The real comparison is a range, not a 3% cap: the Fed remitted $107.4bn in
2021 ([Fed release](https://www.federalreserve.gov/newsevents/pressreleases/other20220114a.htm));
FY2021 federal receipts were $4,046bn
([Treasury statements](https://fiscal.treasury.gov/accounting/us-financial-report/2021/statements-of-change-cash-balance)),
about 2.65% with different calendar/fiscal periods. No remittance ceiling
or hidden transfer was added.

New foreign wholesale stock and flows are saved in DataSave.bankFunding,
restored on load, reset for a new city and redenominated. Missing state is
zero, the actual foreign wholesale liability of a format-66 city before CBK3;
its already-saved window remains intact until the first normal settlement.
No format bump or conversion is needed. The new statement enum lines append
at the end; historical arrays already restore missing appended lines as zero.

## Single-borrower scope answered

The CBK3-trace helper found no borrower/group IDs or defensible firm-size
measure: sector books pool many firms, and neither loan tickets nor building
counts establish control. The Bank notes record Jerus rejecting the old
industry-as-borrower rule after it sent 99% of business borrowing abroad.
The builder asked the orchestrator by bus (20261126-165333-983-CBK3-565f).
Its instruction (20261126-165359-042-orchestrator-423c) is to ship nine working
dials, neither show nor save an inert borrower dial, and queue a persistent
borrower ledger and allocation premise before Basel's cap, exempting sovereigns.
No ticket cap or invented firm count is used. A free queued ID is checked
before the ship file is written.

? The accounting trace also found that loan pricing treated every wholesale
unit as window money. Funding prices now split the lagged window and foreign
shares, with both costs paid exactly once. Foreign creditors continue to earn
their contractual interest in resolution; only the pre-existing central-bank
standstill is interest-free. The new local-currency foreign liability is
included in Game's external holdings, own reserves and world risk exposure;
it is not mislabelled as the treasury's USD debt.

? Collateral alone allows permanent maturity mismatch. The final Part 1
allocation also requires stable funding: 85% of ordinary loans, 65% of insured
low-risk mortgages, 5% of government paper, and 100% of equity inventory.
Core stable funds are equity plus 95% of household deposits and 50% of sector
deposits (Basel NSF30.11, .13, .26, .30, .31). Foreign replacement funds are
term wholesale liabilities denominated in local money; no fictitious USD
principal or exchange gain is created. Overnight advances and hot deposits
have zero stable value. This is an aggregate NSFR approximation; it does not
claim to model a bank's full regulatory liquidity return.

The earlier collateral-only 2.94% observation was provisional: correcting
funding prices made its permanent overnight support visible (8.78%). The
stable-funding law, rather than a tuned remittance cap, gives the final 4.26%.
The first year's inherited-city share is 8.30%, including its inherited
remittance due and treasury flows. No existing stock is erased to force a
historical loaded city instantly into the new steady state.

Part 2 keeps all existing constants as defaults. The new mortgage DTI rule
is off by default: the existing product has a payment-coverage test, not a
principal/annual-income cap. Its separate DSCR remains 1.20. The reserve
requirement and discretionary countercyclical buffer also start at zero.
BankRules owns the nine settings; DataSave writes/reads the array, the Game
load restores it, and both lenders use the same instance. Policy's Bank rules
page uses the existing Kept/Ladder set-at-once pattern. CBK1's page still waits
for CBK1 to ship before it is wired to this same pane.

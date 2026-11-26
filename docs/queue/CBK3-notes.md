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

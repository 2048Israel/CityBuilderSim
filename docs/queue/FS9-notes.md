# FS9 - sweep: the bank and its borrowers (2026-11-22)

The brief is `docs/studies/flaky-suite.md` 6.8 and its table rows. Ten
harnesses (BankCheck, YoungBankCheck, MortgageCheck, SupplierCreditCheck,
CreditCheck, CapitalFlowCheck, CarryTradeCheck, InvestCheck,
DistressBleedCheck, BankFailureCheck) plus CreditCrunchCheck, the other
harness with FS9 rows in `docs/notes/known-reds.txt`. Each stops reading what
a played city happens to hold and causes its premise through the model's
inputs and restore setters, on both sides of each rule, by FS1's order (★2:
setters, then a stood town, then a threshold from both sides against the
model's constant, then a read at the caused month).

The branch was cut from origin/main 827bc03f7 (0.9.181) and merged with main
at 2149ca45c (0.9.183, OG1 and FB16). It is **harness-only**: the diff is the
eleven harnesses and `Observation.java`'s registry, nothing in the model,
the screens, the save or the trace, so `moves_playtest=no` and no ensemble is
owed. The fixture builders other harnesses borrow (`LongPlaytest`,
`ExchangeCheck.step`, `MortgageCheck.landlordCity`, BankCheck's town
builders) are unchanged (★1). Every fixture city comes from
`GameFiles.scratch` or `TempDirs`.

## 1. What each harness read, and what causes it now

- **DistressBleedCheck** (HP2 item 4). It waited for `LIFT_AT`, a timing
  measured on main's town (recovered at a net count of 26). Now a scratch town
  with 8 Contact Centres and no months played: the streaks are set with
  `BusinessInvestment.restoreLossMonths` and `restoreOperatingLossMonths`, the
  desk loan with `issueLoan`, the till with `setSectorCash` (own cash and
  overdrawn), and `planDistressRetirement` is read at `DISTRESS` - 1 and at
  `DISTRESS`, and at the bleed step from both sides. Section 1 writes the
  month's statement and `recordOperatingResult` by hand; section 5 sweeps 600
  states against the old rule; section 7 adds Freight's plant as the control;
  section 8 is new (own cash, not lent). `LIFT_AT`, `RECOVERED_MONTHS`,
  `FLOOR_TIMES` and `MOST_MONTHS` are gone. The four known-red labels are kept
  word for word.
- **YoungBankCheck**. It played BankCheck 19's town 120 months for a sector of
  one plant to weigh a works beyond the bank's equity and default. Now four
  twins of the young town (`FOUNDING_MONTHS` 12, Cement & Concrete held): a
  lone Lime Kiln lent `LOAN_PER_EQUITY` 2 times the bank's equity by
  `issueLoan`, one month played, then retired, settled and written off. The
  loss is sized from both sides of the bank: under the cushion
  (`LOSS_UNDER_CUSHION` .5), between cushion and equity (the midpoint), and
  past equity (`LOSS_PAST_EQUITY` 1.5), with the cushion read from the
  control's close. Run: (a) $353k written off, ratio .3504, no resolution;
  (b) $776k, ratio .0636, under `Bank.CAPITAL_RATIO`, no resolution, a spell
  begun by the write-off; (c) $1,259k, ratio -.2702, in resolution. Section 3
  holds every sector, plays `LABOUR_MONTHS` 2 and places a Wire & Cable Works:
  planned .215 against its own .968 and the .207 it ran at.
- **MortgageCheck**. Sections 2b, 3b, 5c, 6, 8, 8b, 9 and 12 read a played
  city's mortgages. Now they issue them by hand on `landlordCity` (unchanged)
  through `standLandlords`; section 6's bank-equity law now expects the
  residual claim plus what was lent by hand. Section 12 stands 50 Houses with
  the landlords held and two hand mortgages at `PAYOUT_FIXTURE_RATE` .005,
  bounded by `START_UP_MONTHS` 6: paid 0.7309 against 0.7309, and the lean
  month pays 0 where the old rule paid 0.46.
- **CreditCrunchCheck**. Its bank had to happen to stand where leverage binds.
  New `holdInBand` sets the bank's cash and foreign deposits so its leverage
  ratio sits in the band from `Bank.LEVERAGE_RATIO_MIN` to `leverageTarget()`
  and the capital rule binds by `BINDS_BY` 1.25; section 2 now reads 120 of
  120 months on both lines. Section 4 holds the bank in band and strikes the
  desk's rule (`strikeBankBonds`) before the caused order, with the yard's
  materials at 0. The six labels are kept.
- **BankCheck**. Its nine played sections:
  - main and A: a stood Commercial Bank, one booking month, a loan by hand
    (`LOAN_BY_HAND`) and the carry by `CapitalFlows.carryTakeMonth`/`lendCarry`;
  - C (the opening): `planBank()` on both sides of
    `Sector.MIN_STAFFABLE_TO_ORDER`;
  - the savers: two bare Banks, one earning on its book (600k lent, a month at
    6%) and one earning nothing (2M lent), then `fundToCover(.04)`; "it pays
    its savers something" is kept;
  - the desk: stepped `DESK_MONTHS` 6 through `ExchangeCheck.step`;
  - 12: `ALLOWANCE_BY_HAND` through `restoreAllowance`;
  - 13: the tab city plays `TAB_PLAYED` 58 months with its events set by hand:
    a loan at m2, `injectCapital` at m5, a buyback at m9, the bond month
    `TAB_BOND_MONTH` 12, a claim at m20, the treasury's buyback at
    `TAB_TREASURY_BUYBACK_MONTH` 30 and the rescue at `TAB_RESCUE_MONTH` 45;
  - 13b: the rate's parts on a one-month town with the concentration charge
    set by hand, compared to 1e-12 relative;
  - 14: the slices, one month at `SLICE_LEVERAGE` 1.2 and
    `PAST_THE_LINE_LEVERAGE` 2, every month sliced;
  - 15: a lone shop's distress sale, the loss months restored, the builders'
    cash set, families bounded by `FAMILIES_WITHIN_MONTHS` 12;
  - 19: its five Observations made hard against `CHARTER_COVER_UNDER` with the
    template's upkeep set.
- **CreditCheck** 12c: the banned retailer is stood, with no 24 played months,
  and its till held at `HELD_AT` on one side and twice a shop's cost on the
  other. **SupplierCreditCheck**: three Mixed Farms, the shelf topped up,
  bounded by `WARM_AT_MOST` 12. **CarryTradeCheck**: the policy rate pinned at
  `MIN_POLICY_RATE`, read over `AUDITED_MONTHS`.
- **CapitalFlowCheck** 9: the carry is asserted with floorRate - worldRate -
  countryPremium > 0 and the arrival speed law; the twins' rates are pinned a
  4% step apart; the twin's profit is booked through `sellOldMetal` and
  `billForService`. Its laws had been skipped on main whenever the Observation
  was false.
- **BankFailureCheck** 6 and **InvestCheck** (priced19 at 1.25 times the wage
  ladder through `updateWages`) keep their runs; what they counted from a
  played city is printed, and identities replace the counts.
- **Observation.HARNESSES** no longer lists BankCheck or CapitalFlowCheck (★3):
  neither calls `Observation.reached(` now, and ObservationCheck compares the
  registry to the sources.

## 2. The known-reds rows

All 18 FS9 rows go green: DistressBleedCheck 4, CreditCheck 1, BankCheck 1,
YoungBankCheck 4, MortgageCheck 2, CreditCrunchCheck 6. ship.sh's GR2 veto
removes them at the merge; the ship file's `## known-reds` holds no row. Row
75's fragment ("a spell under Bank.CAPITAL_RATIO begins with the write-off of
a sector of one plant") did not match the old label word for word; twin (b)'s
line now contains it and passes. The `docs/notes/gate.txt` lines that go
green are not this batch's to edit: MortgageCheck 6-9, CreditCrunchCheck
10-17, BankCheck 25-27 and YoungBankCheck 45-48.

## 3. The calls

- ★ **`BINDS_BY` 1.25** (CreditCrunchCheck): the rule must bind with room, not
  sit at its edge, or a month's interest moves it out of band. Recommended
  as is.
- ★ **Twins, not one town** (YoungBankCheck): each loss is a fresh twin of
  the same scratch town, so (a), (b) and (c) differ only in the write-off.
- ★ **Bounded waits** where no setter exists: `FAMILIES_WITHIN_MONTHS` 12
  (BankCheck 15), `WARM_AT_MOST` 12 (SupplierCreditCheck), `START_UP_MONTHS` 6
  (MortgageCheck 12). Each fails as a fixture if the bound passes (★2's last
  resort).
- ★ **BankCheck C's order assertion is dropped.** A young town declines the
  branch as unaffordable whatever the staffing, so the order is not the
  opening's law; `planBank()` from both sides of
  `Sector.MIN_STAFFABLE_TO_ORDER` is.
- ★ **BankFailureCheck 6's "pellet mills actually sell in played cities" is
  printed, not asserted**: it read a played city, and the sale law is
  caused in its section 1 ("the pellet mill sells its stock in the following
  month"); section 6 stays played (★4) for its identities.
- ★ **InvestCheck's 360-month counts are printed**, two identities asserted in
  their place.
- **BankCheck 13b's labels** say "to 1e-12 of it" where they said "to the
  bit": the parts' arithmetic can part from the pricing's by an ulp on another path (its javadoc). No known-reds row
  matches them.
- **No fee setter exists** for BankCheck 19's charter; the template's upkeep
  is set instead.

## 4. What the review changed in each slice

The slices ran on Sonnet under `target/slice/RULES.md`; I read every diff.
- YoungBankCheck: (b)'s margin was thin ($31k against a month's $19k income)
  because the claim carries a month's interest; the tills now add
  `interest0`, read at the write-off month's start.
- CreditCheck: the watch compared the till to the planner's `getCostOf`, while
  the ban turns orders away on the door's `orderCostOf`; the two part when the
  yard is short. It now reads `orderCostOf` by reflection (run: 0 orders
  judged, shops covered 480 -> 480).
- A slice ran `./batch-check.sh BankCheck`, which matches YoungBankCheck and
  CentralBankCheck too, and left its tree running; I ended that tree by its
  own process id. The rules now forbid naming harnesses to batch-check in a
  slice.
- The other slices stood as written.

## 5. Observed, not shown to be faults (nothing queued)

- The young town's bank fails on its trading desk on main: 42 months in
  resolution, ratio -7.13 at m79, no write-offs (row 88: equity $2,732k at m50
  to -$500k at m55, traced in PW1's notes section 7, no planner or billing
  error). The harness no longer plays that far; its law is on the caused
  loss.
- SupplierCreditCheck: the credit limit assumes balanced baskets, and a stood
  town fills lopsidedly (186 of 551 baskets).
- CarryTradeCheck: the branches go 4 -> 1 after the first month; not traced.

## 6. The checks

On the merge with main 2149ca45c (0.9.183), 2026-11-22:

- `./batch-check.sh --fast`: compile OK, maps regenerated, Stale 0 firm
  findings, the tree's changes the generated docs.
- The pick (`sh tools/pick.sh origin/main HEAD`): 44 of 239 harnesses for 12
  files, "review not needed - no money, saves or threading in the diff"; not
  ALL, so no dry run is owed. All 44 ran one at a time (hard timing, no
  `cbs.timing=soft`):
  - green, exit 0: every harness of the batch (BankCheck, YoungBankCheck,
    MortgageCheck, CreditCrunchCheck, SupplierCreditCheck, CreditCheck,
    CapitalFlowCheck, CarryTradeCheck, InvestCheck, DistressBleedCheck,
    BankFailureCheck), ObservationCheck, LongPlaytest (381 s) and 28 more;
  - red, main's only: BuildAdviceCheck (gate.txt 18-20, FS12's row),
    DenominationCheck (gate.txt 28-30, DK1's), KeptCheck (five lines, FS5's
    rows 47-50 and 62/72) and MapCheck (FS6's rows 21-23, and three clock
    misses, SIDECAR_MS twice and SCREEN_MS on Rotterdam, under load on the PC:
    `Timing.within` verdicts, SOFT and uncounted in the suite's soft run; the
    batch touches no map code).
- Each of the 18 FS9 rows' label passes in the fresh run, and
  `sh tools/known-reds.sh FS9 docs/queue/FS9-ship.md` exits 0 with the 18
  rows vetoed.
- Before the merge, on 827bc03f7, the batch's harnesses' baseline was
  YoungBankCheck exit 2, CreditCrunchCheck 1, MortgageCheck 1, BankCheck 1;
  the rest exit 0.
- Each rewritten law is asserted on both sides of its rule within the
  harness, so a fixture that misses its side reads FAIL on a line of its own:
  `DISTRESS` - 1 and `DISTRESS` (DistressBleedCheck), the three twins'
  losses (YoungBankCheck), in band and out (CreditCrunchCheck), the held
  retailer's till under and over a shop (CreditCheck).

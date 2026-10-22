# XR2 - notes

*0.8.22, 2026-10-22. The brief is the exchange-rate study,
[spec-exchange-rates.md](../studies/spec-exchange-rates.md) §3.6, §4's board 2
and §5's XR2 row, with Jerus's calls of 2026-10-21: ★3 "all four", ★4 "Vault
or doubt", ★5 "Tied, spread to defend", ★6 "make it a togglable option" with
"Central bank" for a new city, ★13 "Inflation, currency or mix". It builds on
XR1's float (0.8.19, the lean kept as built) and AN1's struck rate (0.8.21),
neither of which it changes.*

## 1. What shipped

**Policy › Money › The currency** chooses how the D$ is held
(`ForeignAccounts`, THE REGIMES):

| regime | the rate | the vault |
|---|---|---|
| **Float** | the float's rule (XR1) | does nothing |
| **Lean against falls** (default) | the rule, a fall damped | sells against the month's new fall, as XR1 built it |
| **Band** | the rule inside centre x (1 ± width), the centre crawling at its slope | at the weak edge sells the month's deficit, at the strong edge buys its surplus |
| **Peg to the US$** | the peg | meets the month's whole balance at the peg |

- **The shadow rate** is the float's rule run beside every regime (under Lean
  and Float, the rate itself). Its gap from the peg is the pressure, shown.
- **A peg breaks** (★4) when paying the month's deficit would take the vault
  under `VAULT_FLOOR` (one month of imports), or by doubt at
  `hazard(gap)` = 0.1% + 3% for each 10% the shadow sits past the peg beyond
  the first 10%, drawn from a stream of its own so the market's path is the
  same pegged or not. The rate lands at the shadow plus `OVERSHOOT` (a quarter)
  of the gap again, which fades at the market's half-life; the regime becomes
  Float; credibility falls `Expectations.PEG_BREAK_DENT` (half) of its way to
  KMIN. The player can devalue or revalue (re-peg).
- **The money that leaves on doubt**: under a peg past its gap, the hot money
  takes the month's break chance of itself home (`CapitalFlows.setDoubt()`), a
  real outflow inside the audit, which the vault then pays as part of the
  balance.
- **The dial under a peg is tied** (★5): the US's rate (`WORLD_BASE_RATE`,
  until XR4 gives the US one) plus a spread the player sets on the page, or,
  with the autopilot aiming at the currency or a mix, `CURRENCY_WEIGHT` (a
  quarter point) for each 1% the shadow sits past the peg. The policy rate's
  own dial is not offered under a peg; its card says so.
- **The autopilot's aim** (★13): Inflation (the rule, the default), the
  Currency (under a band, the US's rate plus the quarter point a 1% from the
  centre) or a Mix (the inflation rule plus it). Under Float and Lean there is
  no target, and the dial follows inflation whatever is chosen (★ below).
- **Who buys at the strong edge** (★6): the vault takes the dollars at the
  reprice; their local price is paid at the top of the next month, inside the
  audit's window (`Game.settleTheEdge()`), by the central bank in new money
  (`CentralBank.buyDollarsForVault()`: M0 up, declared made and paid abroad,
  equity unmoved) or by the treasury out of its cash (a reserve purchase; what
  its room cannot pay the central bank does).
- **Trade › The currency** gains a seventh force, **the hold**: what the peg
  or the band's edge made of the rate beside the shadow, and in a break month
  the peg giving way. Shown only under a band or a peg, so Lean's card is as
  it was. The rate cards name the regime when it is not Lean.
- **The save**: `ForeignAccounts` slots 71-94, `CentralBank` and
  `CapitalFlows` one slot each, `DataSave.policyAim` and `pegSpread`,
  `HistorySave` fxShadow and fxCover. No `SAVE_FORMAT` bump: an older save has
  none of them and loads as Lean, which is what it was.
- **Renders**: `docs/queue/XR2/` (the page under Lean, a band, a peg, a peg
  with the rule's spread, after a break; the policy rate under a peg; Trade ›
  The currency under each), made by `RenderXR2.java` beside them.

## 2. The slices and their review

| slice | by | what | the review changed |
|---|---|---|---|
| 1 the model, its wiring and save | Sonnet 5.5 xhigh | ForeignAccounts' regimes, Game's edge bill, tied dial, doubt and break, DebtManager's aim, CentralBank's purchase, CapitalFlows' doubt, Expectations' dent, the save | BAND_WIDTH's comment named Hong Kong's band (it is ±0.6%); HoldersCheck's fixture asserted the capital flows array ended at slot 14 - restated to "the paper is slots 12-14", its meaning. The slice proved Lean bit-identical by driving main's ForeignAccounts beside the new one through 36,000 months (13,448 of them selling): no difference. |
| 2 the harness | Sonnet 5.5 xhigh | CurrencyRegimeCheck, twelve sections | Nothing in the harness. Found by reading it: the break's dent was not in Expectations' record of the month's move (`takeMonth()`'s step), so NEEDS YOU would not have seen it; fixed (the dent rides into the step, inert under Lean). |
| 3 the screens | Sonnet 5.5 xhigh | Policy › Money › The currency (seven cards), the tied dial card, Trade's seventh force and the regime line, the renders | Every "D$" in the new text through the city's own symbol. |
| mine | Opus | the design, the month's places, the notes, the version | - |

## 3. The checks, the trace

- **The suite** (`./batch-check.sh` on the final tree): 129 run, 128 green;
  the one red is LongPlaytest's map finding "an old works moved", three times
  at m4000, main's own red since AN1 (FX7 is queued for it). Stale: no firm
  finding. The timing harnesses hard: see the commit that ships this.
- **The trace**: the default playtest's log is byte-identical to main's
  (f05fb52d) but for its two run-time lines, after slice 1 and again on the
  final tree. So no ensemble (EN1's rule).
- **CurrencyRegimeCheck** (new, twelve sections, about 7 s), what it counted:
  - Lean against a twin switched to Float and back before its first month:
    60 months equal to the bit, the lean selling in 40 of them.
  - A peg held to the digit in 12 deficit and 12 surplus months, the vault
    moving by exactly the month's balance.
  - 1,000 seeded pegs: the breaks within 0.6 standard deviations of their
    summed hazard (542 against 529 in a 96-month window); median life 7.5
    years, median break 31% (the study's prototype, 2 points over the US: 7.4
    years and 31%); the market's path the same pegged or not.
  - A band 400 months: 92 at the weak edge, 242 at the strong, 66 inside, each
    held or free as it should be.
  - The currency aim over 12,000 months in a band: the shadow inside 43% of
    months against 30% at a neutral dial, the vault spending 12% less.
  - Both payers pay exactly what the edge bought, the audit at $0.00 and the
    central bank's equity unmoved once settled; a mid-peg save plays 12 more
    months to the bit, a break by doubt among them.
  - The default town pegged at founding holds 23 months and breaks by its
    vault in month 24 (★4 below).
- **Restated**: HoldersCheck's fixture "the save carries the world's paper at
  the end of the capital flows array" asserted the array's length (15);
  XR2 appends slot 15, so it reads "in slots 12-14", its meaning.

## 4. ★ For Jerus (XR2-C)

- **★1 Does a band break?** ★4 is about a peg. As built, a band whose vault
  would fall under `VAULT_FLOOR` gives way for the month (the rate floats past
  its edge) and stays a band: no jump, no dent, no chance of a break by doubt.
  The ERM was a band, and sterling left it in 1992 as a peg breaks; the
  alternative is the peg's rule at the weak edge.
- **★2 A float's currency aim.** ★13 defines the Currency aim as holding the
  band or the peg, so under Float and Lean there is no target and the dial
  follows inflation whatever is chosen (the page says so). An open-economy
  Taylor rule under a float reads the real rate against its trend; here that
  would be the D$ against parity's trend. Keep it inert, or aim at parity?
- **★3 The dent at a break**: half of credibility's way to its floor
  (`PEG_BREAK_DENT`, est.), about five years to rebuild on target.
- **★4 A peg chosen at founding breaks by the vault in month 24**
  (CurrencyRegimeCheck 11, the default town): a young city runs deficits, and
  at the peg the vault pays them all. That is the regime working (the median
  real peg lasted two years), and the page shows the floor in months at this
  pace. A warning when a peg is chosen with a thin vault, or nothing?
- **★5 A break with the shadow stronger than the peg** (by chance, at the base
  0.1% a month) lands at the shadow with no overshoot; the Swiss franc
  overshot in 2015. Overshoot both ways?
- **Left undone**: the bank's foreign book (the carry trade) leaving on doubt
  - XR4, beside its unwind in a risk-off; a peg to the euro or the eight - XR3
  in WN4, once their moneys exist; a re-peg (a devaluation the player chooses)
  costs no credibility; board 2's "where the vault's dollars went" table (the
  peg card's cells say it).
- **His eye**: Policy › Money › The currency, the policy rate's card under a
  peg and Trade › The currency's hold, on the PC (renders only, never clicked).

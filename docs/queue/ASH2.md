# ASH2 - the city's rate rule smooths and looks through food

Jerus, desk chat, 2026-11-27, from his Ashbourne findings (see ASH1's head for
his words). The findings are in `docs/queue/ASH-findings.md`; this batch takes
7, 14 and 14b, and re-measures 4 after the change. Ashbourne is his save: copies
on scratch only, and each condition caused on a fixture of the batch's own.

## What he saw

- **What a one-month move does.** A 1% move in the price index in one month
  moves the 12-month figure about a point, and the rate 1.5 points. A year
  later the same month drops out of the window, and the rate jumps back.
- **m192-199 (finding 7).** The rule moved through 2.86, 3.38, 2.85, 3.85,
  3.13, 1.24, 1.95 and 2.65%, while expected inflation was steady (2.4 to
  2.2%): 261 bp in two months.
- **m205-211 (finding 14).**
  - The rate went from 11.7% to 22.4% on 14.7% year-on-year, driven by
    groceries and food, while the last month ran at 7.1% annualised.
  - The bank went from +Cr 313k a month to -Cr 194k, business debt fell
    Cr 2.5M in one month, and unemployment rose to 16%.
- **m226-232 (finding 14b).**
  - Again, 9.7% to 18.6%, while one-month inflation fell from 14.6% to 4.3%
    annualised.
  - The rule's swings are the bank's swings, because the bank funds
    Cr 46-50M overnight at the policy rate.
- **The cause, checked by the desk chat on origin/main.**
  - With the autopilot on, the dial is set each month to `advisedPolicyRate()`
    on the 12-month inflation (`DebtManager.ruleRate`: NEUTRAL_RATE + weight x
    gap), with no smoothing and no step.
  - The world's banks already smooth: in `PolicyRates`, the shadow closes half
    its gap in `RULE_HALF_LIFE` = 9 months, and the rate moves in
    `STEP` = .0025.

## What to build

1. **The rule smooths,** the world's own way (`PolicyRates`' shadow and
   steps) or by an estimated inertia, whichever the real cases support.
   Every Strictness smooths alike.
2. **It reads a measure that looks through food and energy:** a core index,
   if the price index can make one, or recent momentum. Calibrate it from real
   cases (check reality, docs/contract.md: how central banks of the period, and
   today's, answer a food shock).
3. **A saved field for the shadow,** under the old-saves rule: an old save
   starts it at the dial.
4. **Finding 4, re-measured after the change.** A 30-year bond callable at
   100 from 1926 cost 302 bp, where real ten-year calls cost tens of basis
   points. The rule's volatility prices it (`YieldCurve.expectedPath`, the
   call tree). If it is still far from real cases, find why and mend it or
   queue it through the ship file.

## The checks

- A one-month price spike moves the rate by a bounded step, and no jump comes
  back twelve months later.
- A food-and-energy shock moves the rate less than a broad one of the same
  size.
- Every Strictness smooths.
- The shadow saves and loads; an old save starts it at the dial.
- The money audit is exact to the cent.

## Order

- **After CBK3.** CBK3 takes the window's overnight funding of the desk away,
  which is 14b's other half. CBK3 also moves the bank, DebtManager and the
  default trace, so ASH2 is measured on CBK3's main.
- **Beside CBK2.** CBK2 moves the central bank's book and Policy's QE dials;
  ASH2 moves the rate rule. Whichever ships second merges the other.
- **The manual is not this batch's.** An edition comes only at a milestone or
  at Jerus's request (docs/orchestrator.md). The notes carry a "For the
  manual" section, and `docs/manual.*` stays untouched.

Versioned. Default behaviour, money and saves move: an 80-run ensemble against
the newest baseline (its verdict in the notes; no new bank failures), and a
stranger's review at "mends at".

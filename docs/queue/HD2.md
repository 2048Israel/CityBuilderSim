# HD2 - the coupon abroad as profit, kept abroad

Queued 2026-10-29 by the orchestrator, settling [HD1-C](HD1-C.md) on Jerus's
word. Jerus, 2026-10-28: "interest abroad as profit sure"; 2026-10-29, on
HD1-C: "hd1, same" (with WN3's "yes ship, ill look into it later"), and asked
which way, "Kept abroad (Recommended)". HD1-C.md holds the study, the figures
and the three ways; this is way 1.

## What to build

- **Start from HD1's coupon**: commit 3dea900f, reverted in 40cb44c5. `git
  revert 40cb44c5` on main puts it back; EM1 (0.8.74) has since moved
  `Equity` and the holders, so expect conflicts there.
- **The statement as built.** The coupon on a business's dollars abroad (the
  dollars held x the world's 2% over twelve, at the month's rate) is finance
  income below the operating line ("Interest abroad"), inside pre-tax income,
  so the business tax and `Equity.PAYOUT` see it.
- **The cash stays abroad.** The dollars grow by the coupon, as on main, and
  are not paid into the till. The tax and the dividend struck on it are paid
  from the till like any other. `recall()` brings dollars home only when the
  till cannot cover them. A balance of payments books a foreign holding's
  reinvested earnings this way: income in, the same amount out as investment.
- **MoneyAudit** keeps the income credit and the reinvested debit both.
  CapitalFlowCheck section 9 (from 3dea900f) is re-premised to the kept cash:
  the coupon on the statement, the tax at the rate, the dividend at PAYOUT,
  the dollars grown by the coupon, and the till moved only by the tax and
  the dividend.

## What to measure

- **MonetaryCheck, "inflation falls with the rate"** (four policy rates in a
  young city). This is the point of this way: on main inflation is
  10.43/9.68/8.99/8.82% at 3/10/20/40%, and as built it inverted. The
  premise does not move.
- HD1-C's hoard table (dollars abroad at m1200/2400/2852/4000, the tills,
  the D$, the price index) on the new path, beside main's and as-built.
- ScaleCheck's fixture month, which moved as built: rescan for a window.
- The full suite before the ship file; the trace moves, so the ensemble too.

## If the rate check still inverts

Ship it on his word all the same: `--red-ok "hd1, same"` (his "yes ship, ill
look into it later") with a `--red-line` for each of MonetaryCheck's lines.
The ship file then queues the coupon's part in the same study as WN3-R, the
band's monetary study, since both turn the rate dial over.

Money and the month's order: an Opus session with Sonnet slices, not a
Sonnet-only fix batch.

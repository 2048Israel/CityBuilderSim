# Exchange rates that behave like currencies: a design study

*2026-10-21. Work in progress: the PC went off at 08:50; this is what stood
at 08:45. Expands the nations study's WN4 (spec-nations.md §5).*

## 1. What the game does today (draft)

`ForeignAccounts.repriceCurrency()`, each month:

    rate *= 1 + 0.02 x openness x (1 - absorbed) x [BoP / volume - 4 x (real gap + 0.15 ln(rate/parity))]
    rate *= (1 + anchored drift)^(1/12)
    rate += (parity - rate) x 0.004           parity = parityBase x city index / world level

Measured:
- The playtest (`-Dplaytest.fx=true`, 4,000 months): 1.00 to 1,209 D$ a US$,
  parity 1,649; the rate sits 0.70-0.80 of parity for three centuries.
- Jerus's slot 1 (3,001 months): 1 to 2,749 D$ a US$; annualised volatility
  of the monthly change 1.1%; 242 months in a row the same way.
- His new city (slot 3, 476 months): 1.00, 0.74 at m240, 1.30 at m475;
  parity 1.69; the D$ 20-34% dearer than parity from m240 on; volatility
  1.2% a year, runs of 78 months one way.
- The lag is arithmetic: a pull of k = 0.004 a month toward a parity growing
  g a month settles at rate/parity = k / (k + g). The playtest's g, 0.00173,
  gives 0.70; measured 0.73-0.80.

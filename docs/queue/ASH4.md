# ASH4 - four checks from Ashbourne: find each cause, then mend it or record it as real

Jerus, desk chat, 2026-11-27, from his Ashbourne findings (see ASH1's head for
his words). The findings are in `docs/queue/ASH-findings.md`; this batch takes
5, 12, 11 and 16.

Ashbourne is his save. Read it only through copies on scratch
(`tools.SaveDump`, or a headless load of a copy), and cause each condition on a
fixture of the batch's own.

For each finding, find the cause first. Then:

- if it is a bug, mend it with a check;
- if it is real (check reality, docs/contract.md), record it in the notes with
  the evidence, and add a check that holds the real law.

## 5. Households emptied their deposits into one bond (m193-196)

- **What happened.**
  - The city sold a Cr 23.0M 30-year bond, and households took Cr 9.65M of it.
  - Their deposits fell from Cr 10.8M to Cr 0.94M (-91%).
  - The window rose from Cr 40.4M to Cr 47.7M, and business debt fell from
    Cr 25.0M to Cr 21.3M.
- **The cushion.** `Auction.householdsAt` bids out of `householdSpare`, the
  savings past `HouseholdBalance.SHARE_CUSHION_MONTHS` (3 months of disposable
  income). Cr 0.94M for about 2,000 households looks under that cushion.
- **Find what drew past it:** the settle, another path, or the cushion's own
  figure.

## 12. The yen bond bought back at 27.37 per 100 (m205)

- **What happened.** The bond was ¥72M at 3.5%, with 312 months left. A price
  of 27.37 implies a yen yield of about 14%, and the city gained about
  Cr 720k.
- **How it is priced.** `DebtManager.marketValue`:
  `worldCurveAt(JPY, worldRate(JPY) + countryPremium())`, with the term
  premium, in one currency.
- **Check the inputs:**
  - the stand-in century's JPY rate at m205;
  - the country premium on the city's foreign principal;
  - that the flows and the principal convert at the same rate (the crown rose
    about 30% around then).
- **The real yield.** Real 1917 yen yields were about 5-6%; verify that before
  relying on it.

## 11. The power load fell while the town grew (m193-205)

- **What happened.**
  - The peak block load fell from 12,713 kW to 11,072 kW (-13%), while the
    population rose 19% (6,166 to 7,342).
  - The struck C&I price fell 77%, from 0.0487 to 0.0110.
- **To settle:** confirm the two loads are in the same units, or name the cause
  (power-hungry works shrinking under 11.7% rates, for example).

## 16. Households merge by 8% in the month a construction boom ends

- **What happened.** Households fell 8% in a month while the population was
  flat. It happened twice, both in the months the builders were laid off:
  - 2,069 to 1,989 at m216-217 (1,913 by m220), with builders falling from
    593 to 404; it unwound to 2,135 by m226;
  - 2,164 to 1,988 at m231-232, with builders falling from 601 to 156.
- **To settle:** whether the formation rule reads one month's unemployment, and
  its pace.
- **Check reality:** real headship rates move with unemployment over years,
  not within a month.

## Order

- **After RES3.** RES3 moves home choice, boarders and doubling-up, which
  finding 16 reads; it is measured on RES3's main.
- **After AP8.** AP8 moves the households' bond orders, the path finding 5
  reads; it is measured on AP8's main.
- **Finding 5 and CBK3.** Finding 5 also reads the window. If CBK3 has shipped,
  measure on its main.
- **The manual is not this batch's.** An edition comes only at a milestone or
  at Jerus's request (docs/orchestrator.md). The notes carry a "For the
  manual" section, and `docs/manual.*` stays untouched.

Versioned if anything mends. If the default trace moves, an 80-run ensemble
against the newest baseline, its verdict in the notes. If money or saves move,
a stranger's review at "mends at".

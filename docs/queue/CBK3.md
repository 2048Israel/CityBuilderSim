# CBK3 - the window lends against collateral, and the bank's rules become the player's

Jerus, desk chat, 2026-11-26, in order: "also idk, perhaps policy on how much
overnight borrowing banks can do? idk", "as well as some regulatory
controls?", "ive noticed alot of runs the banks are borrowing massively
overnight, like massively, and the central bank remmittance is nearly 60% of
government revenue". He said "Yes, queue CBK3" to the design below as the desk
chat put it to him. Ahead of CBK2; it gates after CBK1, whose central bank page
carries Part 2's dials.

## What he saw (0.9.212)

- **His screenshot.** It shows Government > Overview on 7 January 1911, month 133.
  - The central bank's remittance was 49.2% of the month's revenue (Cr 95k of Cr 193k).
  - The policy rate was 10.14%, inflation 7.7% and GDP Cr 4.9M a year.
- **The autosave of that day.** The desk chat read a copy on scratch, never his saves. It was saved at 12:57, month 133, in save units (a thousandth of the screen's Cr):
  - `CentralBank` advancesToBank (the window): 12,143.
  - M0: 12,378.
  - remittanceDue: 104.
  - Bank deposits: 4,798 (households 1,044, sectors 3,754).
  - bankSecurities (the desk, equity): 9,862.
  - bankCash: -17,366.
  - The treasury's revenue over twelve months (`CentralBank`'s revenue ring): 1,999.
- **What the numbers mean.** The bank holds a stock portfolio twice GDP and funds it overnight at the window: about 2.5 years of GDP at the policy rate plus `WINDOW_PENALTY`. The window's interest, about 105 a month, is nearly all of the remittance, which is about 60% of the treasury's revenue.
- **Slot 9 is different.** At month 6680 it owes the window 0, because its bank funds abroad. The drift depends on the city, and he sees it in many runs.

## The real world, to verify before relying on it (check reality, docs/contract.md)

- The Fed's remittance at its peak (2021) was about 2-3% of federal receipts.
- Lending facilities lend overnight against eligible collateral after haircuts. Equities are mostly ineligible.
- Basel III's NSFR (and the LCR) stop long-dated assets from being funded with overnight money.
- The Volcker rule bars US banks' proprietary trading.

## Part 1 - the fix, first

This part changes default behaviour on purpose, because today's behaviour is wrong.

- **Collateral.** The window lends only up to the bank's eligible collateral after haircuts. Loans and government paper are eligible at set haircuts. The equity desk is not eligible, or only at a heavy haircut, whichever the real cases support.
- **No overnight money for the desk.** Overnight window money must not fund the desk. Find why the desk grows to twice GDP in a young city: a sizing bug, or the carry. Fix the cause, not only the symptom.
- **Past the collateral.** The bank funds abroad at the wholesale rate, or lends and buys less.
- **Measure the change:**
  - the remittance's share of revenue lands near real ranges, both in a city like his autosave's and across the 80-run ensemble against the newest baseline;
  - the money audit stays exact to the cent;
  - no new bank failures come out of it.

  The ensemble's verdict goes in the notes.

## Part 2 - the dials

The central bank's page (CBK1) carries them, and Policy shows them too: one model in two places. Each dial starts at today's value, so only Part 1 moves the defaults.

- **The window:**
  - its spread over the policy rate (`CentralBank.WINDOW_PENALTY`, 0.25 pt now);
  - its limit as a share of eligible collateral, with Part 1's value as the default.
- **Capital:**
  - the minimum (`Bank.CAPITAL_RATIO`, 8%);
  - a countercyclical buffer from 0 to 2.5% (Basel III's range), on top of the conservation buffer (`Bank.CONSERVATION_BUFFER`, dated by L8);
  - the leverage floor (`Bank.LEVERAGE_RATIO_MIN`, 3%; R6i's row dates it).
- **A reserve requirement,** default 0% (the Fed's since 2020).
- **A large-exposure limit on one borrower** (Basel: 25% of capital). Read `Bank`'s concentration notes around `MAX_BUFFER` before setting it.
- **Mortgages:**
  - the loan-to-cost cap (`Mortgage.MORTGAGE_MAX_LOAN_TO_COST`, 85%);
  - a debt-to-income limit.
- **Business loans:** `BusinessDebtManager.MAX_LOAN_TO_ASSETS` (90%).

## The checks

- The window never lends past the bank's collateral.
- The desk is never funded from the window.
- Each dial moves its target the right way: a tighter dial gives less credit and a safer bank.
- The defaults after Part 1 equal Part 1's measured run.
- The money audit is exact to the cent.
- Old saves read under the old-saves rule.

## Order

- **When it starts.** Part 1 needs nothing from CBK1, so CBK3 takes the next free lane.
- **CBK1 first.** It merges CBK1 once CBK1 ships, before Part 2's dials go on its page, and gates after CBK1.
- **AP6.** If AP6 ships first, CBK3 merges it and keeps AP6's held figures exact on any path it adds. If CBK3 ships first, AP6 merges it.
- **CBK2** waits on CBK3: both move the central bank's book and Policy's dials.
- **The manual.** Its banking section is updated by the publish route (the artifact page, then `tools.ManualToMarkdown`), never by editing `docs/manual.*`.

Versioned. Money, saves and the default behaviour move: an 80-run ensemble, and a stranger's review at "mends at".

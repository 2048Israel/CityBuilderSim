# CBK2 - QE and QT at the player's pace, the auction share, and the law on direct purchases

Jerus, desk chat, 2026-11-26: "also im thinking that perhaps the QT and QE could
be like not just 10% and immediately buys 10%, but like 10%, and when you issue
debt the ability to just issue directly to the central bank? if real life is
not like that then no, but do you get what i mean? setting it at 10% doesnt
mean it goes to 10% right away, just means thats teh cap. what do you think?
feel free to add". He said "yes queue both, and preferbly soon" to the design
below as the desk chat put it to him. After CBK1 (the central bank's page,
where these dials are also shown).

## What is there now (0.9.210)

- The holdings dial (`CentralBank.targetShare`, clamped to `MAX_QE_SHARE`) is
  already a target. The book moves toward it by at most `QE_SPEED` (.25) of
  the larger of the dial and the setting before it, as a share of the term
  paper outstanding, each month: 10% is reached in about four months, and the
  pace is not the player's.
- At issue the bank rolls only its own maturing paper, par for par, by a
  non-competitive add-on at the auction's price (`CentralBank`'s "AT ISSUE
  ONLY TO ROLL ITS OWN (0.7.15, round 2)", `buyAtIssue()`; `Debt`'s
  `CENTRAL_BANK_ADD_ON`), as the Federal Reserve rolls the SOMA's. It never
  lends the treasury new money at issue; the treasury's advances (the Ways and
  Means line) are the only direct route.

## What to build

1. **The holdings dial becomes the cap** the book moves toward, and a new
   **pace dial** sets how fast: a share of the term paper per month, both ways.
2. **QT in two modes:**
   - runoff: maturing paper is not replaced, up to the pace each month;
   - sale: paper is sold through the market's order book at market prices,
     and the gain or loss is realised in the bank's income.
3. **An auction share dial:** the bank takes up to X% of each new treasury
   term issue at the auction's price, as a non-competitive bid, always within
   the cap. The money it pays is booked ISSUED in `MoneyAudit`.
4. **A law switch, "direct purchases allowed", default off.**
   - Off: the bank buys at an auction only to replace its own maturing paper
     (main's rule, the Fed's).
   - On: the auction share is open. Leaning on it costs credibility:
     `Expectations`' belief falls, expected inflation rises and the currency
     weakens, as in real episodes of fiscal dominance. Calibrate the size and
     speed from real cases in this batch (check reality, docs/contract.md); it
     is not a question for Jerus.

## The real world, to verify before relying on it

- The Bank of Canada buys Government of Canada bonds at the primary auctions
  on a non-competitive basis.
- The Fed has been barred from buying new Treasuries directly since 1935 and
  only rolls its maturing holdings at auction.
- The EU forbids it outright (TFEU Article 123).
- Japan forbids it except Diet-approved rollovers (Public Finance Act
  Article 5).
- The UK has the Ways and Means overdraft, which the game has as the
  treasury's advances.
- Real QE ran at a pace (purchases per month); the Fed's 2022 QT was runoff
  under monthly caps.

## Saves and defaults

The defaults leave a city behaving exactly as now: the pace dial's default
reproduces `QE_SPEED`'s path, the auction share is 0 and the law is off. An old
save reads the new fields so that it moves exactly as before, or the format is
bumped under the old-saves rule.

## The checks

- The pace is respected both ways; the runoff caps hold.
- Sales are at market, and their gains and losses are realised.
- Auction purchases are at the auction's price and inside the cap.
- The money audit is exact to the cent through each of them.
- With the law off, no new paper is bought at auction beyond the bank's own
  rollover.
- Credibility moves the right way, by a bounded amount, on the real cases'
  calibration.
- The defaults are unchanged: the playtest trace's md5s equal main's, or, if
  the auto-policy moves these dials, the 80-run ensemble against the newest
  baseline.

Versioned. Money and saves move: a stranger's review at "mends at".

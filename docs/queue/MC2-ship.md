<!-- readiness: provisional -->
## changelog
### {next} — MC2, BF1's band share under the leverage target — 2026-11-20, ready, see docs/queue/MC2-notes.md
A bank in its band, between its minimum and its target, lends growth again. BF1 bounded new
buildings' loans and, while the leverage ratio binds, new mortgages by the bank's room to its
target, which is none anywhere under it: a bank a loss put in its band lent no growth at all.
Under the target the room is now the growth limit's share (Bank.lendingGrowthLimit(): 1% a month
halfway, rising toward the target) of each borrower's debt and, as one allowance, of the bank's
book, on the equity the month's known defaults leave, and never past the room to its minimum.
At or over the target it is BF1's room to the target, as before, and none while the month's
default takes the bank under it before its book settles; the carry trade still gets nothing under
it. BankFailureCheck section 9 causes both sides, the cap and the default with hand-built banks,
and 14 of its 17 assertions fail on the old rule. In MC1's played crunch the landlords took 6
mortgages, where none were written before.

## known-reds
KeptCheck | most months make no node at all (12 of 25) | FS5 | 2026-11-21 | Section 55's Bank's Profit page (KBP1's, FS5's range), on the PC under load: MC2's merged tree gave 12 of 25 quiet months, and the same tree with main's Bank.java and BusinessDebtManager.java put back gave 9 of 25, a FAIL too (MC2-notes, REV-MC2 and the mends). KeptCheck's own note at playedCity gives 6 of 25 loaded and 17 quiet; main's kept gate runs give 14 to 25, and FS4's gate on the box passed it. Load, not MC2's code.

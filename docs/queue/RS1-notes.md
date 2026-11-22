# RS1 — the city can sell the bank it rescued

Jerus's four screenshots were read from `cbs-kept-bank-sale` (m1207,
m1208 and the order card). The apparent world bid for exactly 10% at fair
plus 1% is actually `postFund()`'s rule bid: the rescue holding is outside
the market book's cap. `Settle.mayTrade()` correctly refuses a trade between
the fund's two names. Meanwhile the hand's sale arrives after the world and
households have made their decisions; at the next step its ask is withdrawn
before they decide again. Only the bank's earlier buyback bid meets it, and
that buyer cancels shares by design.

★ Use the existing month's ask, posted before the world and households,
rather than introduce a separate offering. Both may buy rescued shares on
their ordinary return, budget and price rules. A hand sale pauses the fund's
rule buying that company for the month, so it neither advertises an
unavailable self-bid nor buys back what the hand just sold. The bank's own
capital-policy buyback remains a buyer too. A -99% limit authorizes a real
sale that cheaply: a buyer arriving at the resting ask pays that ask, and
the fund receives precisely that cash. Unsold shares stay in the rescue book.

★ A genuinely crossed book (self-trade and new-issue restrictions can leave
one) has no meaningful midpoint. Use the last trade, or fair value before
the first trade, and say the book is crossed in the quote caption. Keep the
ordinary midpoint for an uncrossed two-sided book.

Finding 3 has the same arithmetic failure as DK1, but the bank's own
book does not use `deskLimits()`: its buyer is the bank's buyback, whose
`buybackRoom()` is spare equity over its capital requirements. A positive
residue there can likewise buy a dust lot at a cheap resting ask and set
the bank's last price. The bank fixture therefore causes this on a rescued
bank as well as DK1's aggregate desk cap on another company. Reject spare
buyback capital below `MoneyAudit.tolerance(0, larger capital sum)` and
resolve both desk cap differences in money before dividing by fair value.
The normal-scale and HN1-scale fixtures exercise both paths. A cheap price
alone is valid: the positive control gives the desk resolved room and
checks that it records the actual cheap trade price. The screenshots alone
do not establish the historical source of that particular old last print;
these fixtures prove and close the two dust-capacity paths.

No new saved state or field is needed. The existing hand queue, posted
orders, rescue holding, cash and ledger already carry the transaction.
Under the old-saves rule no save-format bump is owed.

The final regression was run with the original `4a6af0baa` Exchange and
TreasuryFund sources and the new ExchangeCheck fixtures: 14 expected
failures, including findings 0, 1, 2, the prior-real-trade crossed quote,
and both desk and bank last-price assertions twice (normal scale and
money multiplied by 1e9, HN1's unit 1e-9). All fixture premises and the
positive controls passed. The positive
room was explicitly above OrderBook.DUST in shares but below the money
sums' resolution. Kept locally: `target/RS1-before-final.txt` and
`target/RS1-before-final-suite.txt`. The rest of ExchangeCheck and its picked
DebtExchangeCheck were green. A first attempt to compile the harness used
a nonexistent indexed getter; corrected to the existing aggregate getter
before these regression results.

The fresh default LongPlaytest on main's `4a6af0baa` model passed all
2,400 months (`target/RS1-main-playtest.txt`, 350s harness time), with the
trace under `target/RS1-main-trace-*`. Only the new ExchangeCheck fixtures
were present in that build; the game sources were main's.

Checks and publication evidence will be recorded here after the runs.

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
the fund receives precisely that cash. Unsold shares stay in the rescue
book. Actual sale receipts can fund the rule's usual purchases of other companies in that step; only the company
being sold is paused.

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
TreasuryFund sources and the final ExchangeCheck fixtures: 15 expected
failures, including findings 0, 1, 2, the household ★, the prior-real-trade
crossed quote,
and both desk and bank last-price assertions twice (normal scale and
money multiplied by 1e9, HN1's unit 1e-9). All fixture premises and the
positive controls passed. The positive room was explicitly above
OrderBook.DUST in shares but below the money sums' resolution. Kept locally: `target/RS1-before-star.txt` and
`target/RS1-before-star-suite.txt`. The rest of ExchangeCheck and its picked
DebtExchangeCheck were green. A first attempt to compile the harness used
a nonexistent indexed getter; corrected to the existing aggregate getter
before these regression results.

The fresh default LongPlaytest on main's `4a6af0baa` model passed all
2,400 months (`target/RS1-main-playtest.txt`, 350s harness time), with the
trace under `target/RS1-main-trace-*`. Only the new ExchangeCheck fixtures
were present in that build; the game sources were main's.

On the mended model, `--fast` passed (compile, regenerated maps, no firm
Stale findings). `ExchangeCheck` and its selected `DebtExchangeCheck`
passed, including every RS1 fixture. `DenominationCheck` passed in full,
including the decade's 1e-9 comparison recommended by DK1.

`sh tools/pick.sh origin/main HEAD` selects 40 harnesses, not ALL. Its
review floor is `needed - money: TreasuryFund.java`; the review must also
read Exchange's fund cash/share settlement timing and buyback capacity.
The picked run uses `-Dcbs.parallel=1`, one active harness at a time, and
keeps the final branch's default trace under `target/RS1-branch-trace-*`.
PriceLevelCheck, ReadPathCheck, SaveFileCheck and SaveSlotCheck are green.

For an eye check, sell Bank from Finances > The city's fund with a rescue
holding: its rule's unavailable bid should disappear during that sale;
actual world/household fills should reduce the city's ownership and appear
in its cash and ledger. Inspect a crossed book's PRICE caption and the
order card's Last after a real fill. No UI source changed.

The picked run's MortgageCheck has its two existing FS9 reds (the missing
lean month, followed by its 0.00/0.00 assertion). CreditCrunchCheck has its
seven existing FS9 reds: the missing sustained leverage/band premises,
the native land shortage watch, and the missing capital-short door
premise and its three dependent assertions. Each is already named in
main's `docs/notes/known-reds.txt`; neither harness source was edited here.
OrderDoorCheck likewise reproduces FS9's missing seed-12 edge at m420.
BankCheck reproduces FS9's existing zero depositor-interest assertion.
AutoBuildCheck reproduces FS12's missing order-placement premise during
its debt-limited replay. CentralBankCheck reproduces its two FS10H native
whole-par/netting premises, and FundCheck reproduces FS13H's missing
limit-ticket room. FundLedgerCheck is green. MapCheck has its existing
FS6 one-for-one building-count red. KeptCheck has its three existing FS5H
reds: Construction cash/debt fresh-page equality, Government node creation
45/401, and the held-family Rankings drawer's two closed cells. These are
existing named reds, not new RS1 rows. CopperRefiningCheck also reproduces
FS8's absent January order-cut premise; its child verdict is recorded below.

The picked run's ShortSellingCheck reached AllChecks' fixed 1,740-second
process watchdog in its second (shorts-off) 1,000-month city. All sections
before that and the enabled run's audit and register assertions passed:
716 sampled months, worst cash residual 0.000001 and register drift
1.22e-14. The final log records the timeout; it is not called a green run.
The historical sweep mapping names FS14, which has shipped, so no timeout
exception is assigned to that shipped row. A complete run of the unchanged
named harness was kept separately, with the same 2 GiB heap and soft timing
and a two-hour process watchdog; no fixture or assertion was edited. It
passed in full, exit 0 (`target/RS1-short-direct.log` and
`target/RS1-short-direct.exit`): 851 seconds with shorts enabled and
1,063 seconds with them off. The enabled run reproduced exactly the first
attempt's 716 samples, residual and register drift; the off run also passed
its audit, register and no-short-order laws. The command is kept in
`target/RS1-short-direct.sh`. No timeout exception or new queue row is needed
for an assertion run that has now completed green.

While this run was active, R6e shipped as 0.9.186 (`7751e845c`).
This run still measures RS1 on `4a6af0baa`; main will be merged after the
stranger's SHIP, in the starter's required order. RS1 adds no saved field
and therefore does not contend with R6e's format 60.

The household fixture was tightened after reviewing the original red log:
with a zero deposit rate it could already rest a bid at fair value on main,
so its positive household-purchase assertion did not prove the answered
★. The household case now offers the deposit rate HURDLE: fair value fails
its deposit-plus-premium-and-band hurdle, while the half-fair ask clears it.
That is an input premise that must be green on both models; the purchase
itself must fail on main and pass with the early ask. The repeated main-red
run gives 15 expected failures, including that household assertion; its hurdle premise is green on main. No game class or
borrowed fixture helper was changed for this stronger regression. The
mended ExchangeCheck and selected DebtExchangeCheck then passed in full
(`target/RS1-final-exchange.txt`); final `--fast` also passed with 0 firm
Stale findings (`target/RS1-final-fast.txt`).

The mended default LongPlaytest completed 2,400 months with its audit
checks green and the already named CL7 industry-distance finding once at
m2400. Its overall verdict is known red, not a full pass; the final auxiliary
"All checks passed" line is not that overall verdict. Its eight CSV
traces differ from the fresh `4a6af0baa` main run beyond runtime: the first
rounded recorded move is at m558 (`bizDebt` in month.csv, `loans` in
bonds.csv). Main's trace has 2,404 rows against RS1's 2,401 as the player's
extra months reach the stopping point differently. Bank exposure first
moves at m589; labour and population at m784. All eight SHA-256 comparisons
are in `target/RS1-trace-comparison.json`, frozen with the branch CSVs in
`target/RS1-pre-review-trace/`. Therefore an ensemble is owed, once on the
final model after merging the new main and after the stranger's SHIP; the
starter's last instruction makes that SHIP the go for it.

The serial `--pick` ran 45 harness mains from the 40 selected names in
5,952 seconds: 32 passed, 12 had only existing named reds, and
ShortSellingCheck hit the process watchdog. Its two new lines are solely
`exit 1` and `FAIL: timed out after 1740 s`; no new model assertion failed.
YoungBankCheck also reproduces its two FS9 bank-resolution premises.
LongPlaytest's named finding is CL7's industry farther out than homes at
m2400; CopperRefiningCheck's child exit is covered by the existing CL7 row.
Raw results: `target/RS1-pick-check.txt` and `target/RS1-pick-suite.txt`.
The timeout is not represented as a green pick or assigned to shipped
FS14. The later complete named run supersedes that incomplete harness
execution; the raw picked-run result remains recorded unchanged above.
The household fixture tightening changed no borrowed helper or game class;
its final ExchangeCheck and DebtExchangeCheck rerun was green.

The provisional ship file is the review handoff. The money review must read
Exchange.java and TreasuryFund.java, especially the early sale, settlement,
self-bid pause, quote age and capital-room guards. After its SHIP: merge
origin/main, run the owed ensemble once on that final pushed model against
the newest baseline, record the score and any named main class, then remove
the provisional marker last in its own commit. The pick is not ALL and no
publisher code changed, so no dry run is owed.

Money review REV-RS1 of `5eb4e034a` returned SHIP on 2026-11-22. The
starter's go named main `abc8fd4f5` (R6e, FS9 and queue cleanup), merged as
`cd01cb63f`. Only docs/harnesses.md and docs/map/README.md conflicted; both
were regenerated. The reviewed Exchange, TreasuryFund and ExchangeCheck
sources are byte-identical after the merge. Merged `--fast`, ExchangeCheck
(and selected DebtExchangeCheck) and FundLedgerCheck are green; logs are
`target/RS1-merged-fast.txt`, `target/RS1-merged-exchange.txt` and
`target/RS1-merged-ledger.txt`. The merged pick names 41 of 241, not ALL.

Queue cleanup consolidated FS10H/FS13H/FS17H as HT1-H and FS5H as FS5. The
older names above describe the historical pre-merge run; any new ship-file
exception will use a current QUEUE.md row. FS9's harness repairs came from
main. No RS1 ship-file known-reds row is presently needed.

The final ensemble uses docs/ensemble/0.9.184.md as explicitly ordered by
the starter. Its only JVM options were the two data paths, so JAVA_OPTS is
empty and JDK_JAVA_OPTIONS is unset for box dispatch. Gate-box preflight
reported zero heavy jobs. The pushed snapshot and result are recorded below.

While that snapshot waited behind HS3's gate, main advanced to `52c79806b`
(0.9.187). Integrated HS3 before the ready handoff: its only model additions
are read-only AdultStage and HouseholdAccounts getters, alongside the money
page and its words harness. HS3 records all eight default trace CSVs as
byte-identical. RS1's three reviewed Java files remain byte-identical to
`5eb4e034a`; the ensemble's `06ff84630` snapshot therefore still measures
this model. The two generated-index conflicts were regenerated, and the
merged `--fast` is green (`target/RS1-HS3-fast.txt`). No new model change or
second ensemble is needed for that trace-neutral integration.

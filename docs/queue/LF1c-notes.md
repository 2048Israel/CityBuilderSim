# LF1c - notes: LF1's review follow-ups, the short's lending fee (2026-11-16)

The brief: [LF1c.md](LF1c.md), queued 2026-11-08 from the stranger's review at LF1's gate (money; SHIP + FOLLOW-UP). Built by a Claude
session (Sonnet 5.5) from main d55f18c3 (0.9.124), after the night's Codex builders ran out of plan; one session, no slices. The brief's
line numbers were from 11-08, so each item was found by its text. None can reach a player's money or save: each needs a short open, which a
real run never opens (EM2's notes).

## 1. The Fund screen's words (`ui/FundScreen.java`)

Three places said less than the model holds, and each is now one pure static a probe reads, used by the screen:

- **A fee's row.** `act()` had no case for `LENDING_FEE`, so the record showed the kind's name. `lendingFeeWords()`: "Lending fees from the
  world on the shares lent to a short in N companies" (one company: "company"), the shape of the dividend's "Dividends from N companies".
- **A share's write-off.** `WRITTEN_DOWN` "gone" read "Written off: X, its face gone" for every key. `writtenOffWords(name, bond)` keeps the
  bond's "its face gone" and says a company's shares "gone with the company".
- **The income that holds the fees.** `FundLedger.lendingFee()` credits the company's MARKET lot's income, so that lot's page said
  "Dividends" over a figure that held the fees. `incomeLabel(kind)`: a share lot "Dividends and lending fees", a bond "Coupons", the rescue
  book's lot and the rest "Dividends". The income card's note (`incomeNote()`, pulled out of `hero()` with its sentence unchanged) says
  "dividends, coupons and lending fees" where it said "dividends and coupons", and two info texts (Holdings' i, the withdrawal's caveat)
  name the fees too. The rescue book's sub-line (`of dividends on ... of cost`) is left: no fee reaches that lot.

The brief's `:431` and `:674` were the rescue sub-line (kept, as above) and the position's income label (now `incomeLabel`); the hero's
"dividends and coupons" (`:400`) was the third spot it had in mind.

**The words are proven in ShortSellingCheck** (`theWords`, in section 3's city: the real city's fund has the fee rows), not in KeptCheck:
`FundScreen` is package-private in `ui/`, so `KeptBench` (public, KeptCheck's own door to the screens) gained static readers, six for the
Fund and two for Trade's valuation words (section 4)
(`fundActivityWords`, `fundPositionLines`, `fundHeroIncomeNote`, `fundIncomeNote`, `fundIncomeLabel`, `fundWrittenOffWords`). The check
reads the Activity page's lines off the city's fund (one fee line per ledger row, none saying LENDING_FEE), the market lot's position
labels, the income card's own note equal to the note the model's two figures give, and a share's write-off. KeptCheck is picked by the diff
(KeptBench and FundScreen changed) and runs in the dry run; this batch adds no section to it, since nothing in it draws a different
node: the same labels carry other text.

## 2. The wipe and the close for nothing, declared (`Exchange`, `MoneyAudit`)

`Exchange.cancelShortHeld(c)` fires in the step's valuation loop for a company with no shares in issue and a short open. It does two
things the audit could not see, and the second was EM2's before LF1 (the zero-shares branch's `shortShares = 0`):

| what | the position it moves | declared now |
|---|---|---|
| the world's own shares in the company (`Equity.cancelHeld`: `foreignShares = 0`), at the month-end quote | `Game.getSharesHeldAbroad()`, "what the world holds here": falls by `foreignShares x quote` with no dollar | `+ desk WorldSharesWiped` (VALUATION, in the city's favour) with `- desk WorldSharesWiped (no cash)` (DOMESTIC), as the bank resolution's `OwnersWipedAbroad` pair |
| the short closed for nothing, the world keeping what it was paid: the shares it owed the buyers, at the month-end quote | none: the position never held the short; the balance of payments' valuation counts it against the city | `- desk ShortClosedForNothing` (VALUATION out, **against** the city) with `+ desk ShortClosedForNothing (no cash)` (DOMESTIC) |

The exchange keeps the two as month tallies (`worldSharesWiped`, `shortClosedForNothing`), taken in `cancelShortHeld()` before it
zeroes anything, at `price(c)`; cleared by `startMonth()`, scaled by `redenominate()`, read by `MoneyAudit` through two getters.
`ForeignAccounts.takeMonth` already does `forgiven += valuationChange()` (valuationIn - valuationOut), so it counts both with no change.
Neither moves a pool, so the cash identity needs the pairs and nothing else.

★ **B's sign.** The brief says "declare both ... as the bank resolution declares its own"; the resolution's is a gain for the city (it owes the
world less). The close for nothing is the other way: the buyers of a short lose a claim on the world, and the world is let off - a
valuation against the city. `MoneyAudit.Result` already has `valuationOut` ("...and against it", read by `valuationChange()`), but no line
used it before. So B is a VALUATION debit and `forgiven` moves by the wipe less the close. If the reviewer wants it a credit ("claims the
world was forgiven"), it is one word in `MoneyAudit` and the check's `ex.getWorldSharesWiped() - ex.getShortClosedForNothing()`; I judged
against it because the screens say "improves what the city owes the world" of a gain and "against it" is what a loss would need.

★ **Not saved.** The two tallies are month figures like `Game.ownersWipedAbroadThisMonth` (also unsaved); the audit that reads them is
struck in the month that made them, and the next `startMonth()` clears them. No `State`, `DataSave` or format change.

★ **Valued at `quoteBefore[c]`,** the quote struck before the step withdrew the books - the month-end mid `getSharesHeldAbroad()` carried
the world's shares at - falling back to `price(c)` only when it is 0. (First draft: `price(c)` after `withdrawAll()`, which is the last
trade of the emptied book: the stranger's review, REV-LF1c-1, note 2.) Both fixtures rest a world bid over the last trade so the two
quotes differ; with the line put back to `price(c)` section 8's Bench shows two failures (144 against 216, 28.8 against 43.2).

**The fixture** (`ShortSellingCheck.theWipe`, the real town of section 3, one more month): a company the town never listed (Heavy Industry:
no shares, no book, so the month's `payDividends` listing leaves it so) is listed by hand against a book of 5,000, a short is sold at it to
a cell (the lenders and the room `shortRoom()` needs exist only while it has shares), every share is retired and bought from the founders'
cell, the buyer sells half of what it bought to the world and half to the desk (no cell holds one to carry abroad, so `followEmigrants` cannot
move the world's count), a world bid rests over the last trade (so the month-end quote is not the last trade), then `city.simulateMonths(1)`. It reads: the exchange's two figures against the shares times the quote; the audit's
four lines equal to them and to each other's halves; the month's residual 0; `valuationIn`/`valuationOut` equal to the exchange's figures
(nothing else was written off); `ForeignAccounts.getForgiven()` moved by the wipe less the short closed. Both figures are over the audit's
cent so a missing line shows. **Control:** with the two VALUATION figures in `MoneyAudit` set to 0 (the "no cash" halves left) the section
shows five failures (the residual 2,477, the four line assertions); restored. A first draft of the fixture held the world's shares at
half the company's and lost them to `followEmigrants`' cap (shares + short): that run's wipe was 23 where it expected 2,500, which is how I
learned the world's holding of a no-share company is bounded by the short.

**The Bench half** (section 8's edge 3, `ShortSellingCheck.theBooks`) reads the same two figures straight off the exchange: 200 world
shares and the 40 short, at the quote before the step, and their clearing a month later.

## 3. Edge 3's fixture in the month's order (`ShortSellingCheck.theBooks`)

It ran retire, step, list. The month runs `payDividends`' `listIfUnlisted` (docs/month-order.md 113) before `tradeShares`' step (114), so a
company with a book is listed again before the step finds it empty, and the wipe fires only for one whose book is at or under 0. The fixture
now follows that order: retire every share, `listIfUnlisted(INDUSTRY, 0, hb)` (a company with no book: it returns false and the company is
still at no shares - asserted), then the step; the later listing, when a book comes back, is unchanged. The other order, which the brief
allowed ("or say which it proves"), is not kept: the fixture proves what the month can do.

**Seen, not changed** (nothing in a real run reaches it): a company WITH a book whose every share is retired is re-listed before the step,
and its founders' new shares then sit beside what the short sold and what the world holds - the "on top of the founders'" state LF1's edge 3
cured for the no-book company. A buyback is capped (`BUYBACK_PACE`) and never takes all of a company, so the state needs a short open at a
company that has been bought out entirely; if it is ever wanted it is a batch of its own with a fixture to cause it.

## 4. Seen on the way

- `ForeignAccounts.getForgiven()` read -0.66 in the 48-month town before any short wipe, so a net-negative valuation was already
  reachable; the Trade screen's words said a gain only ("improves what the city owes the world"). **Fixed here** (review note 3, a few
  lines): `TradeScreen.valuationChip/valuationInfo/valuationNote` and `forgivenChip/forgivenInfo` take the sign - a gain reads as it did
  ("claims written off", "forgiven or settled"), a loss "claims lost abroad ... counts against the city" and "written off against the
  city" - and are read through `KeptBench.tradeValuationWords/tradeForgivenWords`; `theWipe` asserts them after a month whose valuation
  nets under nothing (the wipe .5 S q in, the close S q out), and the gain wording unchanged beside them. No Pending row.
- The per-lot income is one figure: a market share lot's `income()` holds dividends and fees together, so the label names both for every
  market-book share lot, paid a fee or not. Splitting the lot's income would be a saved field and a ledger change for a label.

## 5. Checks

`sh tools/pick.sh origin/main HEAD`: **ALL**, because `MoneyAudit.java` has 65 readers; review "needed - money: ui/FundScreen.java". The
review was REV-LF1c-1 (Claude Opus, on a203e3414): SHIP, with three notes - the audit comment's words (the card holds the wipe, never the
close), the figures at the month-end quote `quoteBefore` and not the last trade of the emptied book, and the Trade screen's words for a
valuation against the city - all three done and accepted at 17218b0fc (orchestrator, no second review). Before the run:
ShortSellingCheck green alone (about 3 minutes; the last full run after the review's changes, exit 0), and FundLedgerCheck, ForeignCheck,
ExchangeCheck, SaveFileCheck, ReadPathCheck by `batch-check.sh` names.

## 6. The dry run (kept) and the trace

`sh tools/gate.sh LF1c --dry-run`, branch `9c77d8187f3e21d5822309e6e37d5ef100ef2899` (LF1c with CD9b's merge, 437e05a2, merged in and its
generated docs conflicts resolved by regenerating) on main `437e05a2a68d03b5fbed022c537ad23f2cbf70fe` (0.9.124). Kept run:
`/tmp/cbs-gate-LF1c-7jBjE1Uz` (`C:/Users/Jerus/AppData/Local/Temp/cbs-gate-LF1c-7jBjE1Uz`). **Gate exit 0**, `unnamed.txt` empty,
`checks.complete` present. Its ship.log: ALL 209 harnesses (MoneyAudit.java, 65 readers), records `0.9.125`=0.9.125, compile OK in 48 s, the
suite finished in 2,256 s with exit 3, **known red only, seven harnesses**: DeathRecordCheck, InvestCheck, KeptCheck, MapCheck,
MoneyDatesCheck, PrizeCheck, ResearchViewCheck - the lines already on record (`known-reds.txt`; KeptCheck's two are KS1-UI's), none of them
this batch's: no line of theirs names a Fund or Trade word, the audit's lines or a short. ShortSellingCheck, FundLedgerCheck, ForeignCheck,
SaveFileCheck and ReadPathCheck are green in the run. Twelve timing assertions soft, four path assertions observed, five starts held for
CPU (none changed a result). Stale: 0 firm findings. The dry run left the tree as it was.

**Trace equality:** the dry run was started with `-Dplaytest.trace`; the eight CSVs it captured
(`target/LF1c-trace-9c77d818-{bank,bonds,borrow,build,house,labour,month,pop}.csv` in the worktree) are **byte-identical** to main's
reference, R0h's run (`C:/Users/Jerus/AppData/Local/Temp/cbs-gate-R0h-NrRv6F33/traces/trace-*.csv`), `cmp` eight of eight. No ensemble: the
trace did not move.

After this kept run only this file and `LF1c-ship.md` advance; the last commit alone removes the provisional marker.

Process lines: none changed.

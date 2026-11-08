# BK2 - notes: each holder's own discount on a line of the city's paper ({next}, 2026-11-08)

The brief: [BK2.md](BK2.md), from BK1's notes (1 and 4) and BD1's (5). Started on `main` (GT2 was already in).

## 1. What changed

A line of the city's paper kept one pool of unaccreted discount (`Debt.discountLeft`), every holder's share of it pro rata by
face. A reopening, or the central bank's par or add-on, added its tranche's discount at that tranche's price, so the pool's
discount per face moved for every holder: the bank's unearned discount moved with no cash and no income. BK1 measured that move at
the settle and booked it to retained (`Game.restrikeBankDiscount()`, "A reopened line's discount, re-struck"), BD1 at the issue and
the trades (`restrikeBankDiscountCarried()`); AutoBuildCheck's cities re-struck up to -$1,641k in a month on about $30,000k of equity.

Since BK2 the line carries each holder's own (`Debt.Holder`, `discountOf(h)`): `bankDiscount`, `householdDiscount`,
`centralBankDiscount`, `firmDiscount`, `worldDiscount`, each at nothing or more, and `unsettledDiscount`, the sales not yet settled
(face less what they raised, signed), nobody's until the settle. `getDiscountLeft()` is their sum. The rules:

- **A sale** (`markIssued`, `reopen`) adds its discount to the unsettled sales' and moves nobody's: a premium no longer eats the
  other holders' discount at the sale (BD1's 13a release).
- **The settle**: each taker (households, firms, world: `takeAtSettle`) adds its face less its cash to its own; the bank, the
  residual buyer, takes what is left of the sale's discount (`settled()`): its face less what it paid, exactly. A premium first eats
  the buyer's own discount on the line, floored at nothing - the rule `reopen()` always stated for the pool, now per holder.
- **The central bank's par** into a line (`reopenForCentralBank`) and its **add-on** (`addOnForCentralBank`) go to its own discount;
  nobody else's moves.
- **Face that changes hands** carries the seller's discount per face (`pass(from, to, face)`, returning it; `moveToX` is a pass
  between the bank and X). The bank's per face is over the face it has paid for (`bankPrincipalPaid()`).
- **Principal repaid** (`repay`) leaves every holder's discount: each holder's face falls in proportion, as the pool's did.
- **Accretion** (`accrete()`): each holder's own, straight-line over the months left; it returns the bank's step, which is what
  the bank books (`DebtManager.processAllDebts()`, no more `bankShare`). The five steps add up to what the pool's step was, so the
  city's accretion in total is unchanged; only who earns it moves. The unsettled sales accrete nothing (in play the settle runs
  before the month's accretion, so nothing is unsettled then).
- **A buyback in part** (`retirePart`) takes each seller's own share off with its face (the central bank keeps its own); a whole
  buyback or a call hands the bank's own (`unaccretedOn(BANK, face)`), and for face beyond what the bank has paid for (a whole
  piece bought back before its settle) the unsettled sales' discount on it, as main handed the pool's.

So the bank's book figure (`unaccretedOnPaid()` = `bankDiscount`) moves exactly by what its trades and its settle book, and BK1's
and BD1's measurements read nothing at every reopening at a discount. They stay, with their cause row, as the check that it is (★2).

## 2. Every place face changes hands (Game)

| where | was | now |
|---|---|---|
| `payDomesticPrincipal()` | `moveToX(-s)` | `repay(X, s)`: no discount moves |
| the desk, three (`desksBuys{Household,Firm,World}Paper`) | `unaccretedOn(off)` (the line's pool) + `moveToX(-off)` | `pass(X, BANK, off)`: the seller's own |
| the settle: households, firms, world | `moveToX(face)` | `takeAtSettle(X, face, cash)` at the cash each paid |
| the settle: the bank | pool | `settled()`: the rest of the sale's discount |
| the holdings dial | `unaccretedOn(f)` + `moveToCentralBank(±f)` | `pass(BANK, CB, f)` / `pass(CB, BANK, f)` |
| the central bank buying from households, firms, world | `moveToX(-f)` + `moveToCentralBank(f)` | `pass(X, CB, f)`: straight across, the bank's untouched |
| `rollCentralBankAtIssue()` | pool | the central bank's own (`reopenForCentralBank`, `addOnForCentralBank`; a new piece's whole discount passes bank -> central bank) |
| `buyBack()`, `callLine()` | `unaccretedOn(bankFace)` | `unaccretedOn(BANK, bankFace)` |

## 3. The save: format 51, the floor with it

The six fields are the piece's, saved by Gson with the debt list; `discountLeft` is gone. A save of format 50 or older carries the
pool and none of the six: every holder would read no discount, the bank's unearned discount would fall to nothing at the first
refresh with no cause, and its accretion stop. So `SAVE_FORMAT` and `OLDEST_LOADABLE_FORMAT` are both 51 (CLAUDE.md, old saves): a
format-50 save is refused, naming the build that still opens it. No conversion is written.

## 4. The calls (each took its recommended way, BP1)

- **★1 A premium eats the buyer's own discount first, floored at nothing.** Taken: what a buyer pays over face comes off its own
  discount on the line (its accretion slows: the premium amortised against it); only past nothing is it the buyer's loss when paid,
  which for the bank BK1's measurement books to retained, as main does for every premium. The other way: carry a premium on the
  bank's books (a negative unearned discount, amortised over the line's life) - `Bank` holds its unearned discount at nothing or
  more in eight places, so that is its own batch if wanted.
- **★2 BK1's and BD1's measurements stay, as the check.** Taken: they book nothing at a reopening at a discount (HoldersCheck 12,
  13), and the premium past a buyer's own discount, and any later mismatch, are named rather than left in the split. The other way:
  delete `restrikeBankDiscount()`, `restrikeBankDiscountCarried()`, the two `Bank` methods and the Bank tab's row.
- **★3 The unsettled sales accrete nothing.** Taken: they are nobody's until the settle, which in play runs before the month's
  accretion. The other way: accrete them to the bank as the pool did (only a harness's month with a sale unsettled would see it).

- **★4 The ensemble's one new red, SHOCK T2, is the seeds' scatter: ship.** Taken (below, 5). The other way: hold BK2 for a
  balance pass on the young city's growth.
- **★5 KeptCheck's 11 new reds are its bench city's path, not BK2's code: pass them by name, KC1 queued** (Jerus, 2026-11-04: a
  red caused outside the batch's own code passes by name, a fix queued). Taken (below, 5). The other way: mend KeptCheck's
  fixtures here, in a money batch, on the UI's files UP8 is in.

## 5. The checks, the trace, the ensemble

- **The harnesses BK2 names, green** (with the main checkout's `data/world-1.bin` named, `-Dcbs.world`): HoldersCheck 470 OK
  (12 and 13 turned: nothing re-struck at a reopening at a discount, the twin that books none closes too, the city's figures the
  twin's bit for bit; 13a's premium $0.0715k comes off the bank's own $0.4084k on the line; 13b's desk is handed the households'
  own $0.117573k, where the pool handed $0.226411k, and the book rises by exactly that; 15 new, the rules one at a time; 6 compares
  the six across a save, exactly), CentralBankCheck 406 OK (22 new, BK1-C: the central bank's par rolled alone into a two-year line
  the households and the bank hold part of, and added on to a market reopening of it; its own discount rises by the par less what
  it paid, $433.59k on $20,000.15k, nobody else's moves, nothing re-struck, the movement and the split close, the audit closes),
  BankCheck, BuybackCheck, DeskCheck, CallCheck, DebtMoneysCheck, SaveFileCheck, ReadPathCheck green; ScaleCheck's reds are main's
  eight, word for word.
- **Without the world file** HoldersCheck 13c is red (its bill's discount, almost all an issue's fixed fee, is over what the
  bank's own discount carries on that path: the clamp of BD1's ★1); the gate names the file, and with it 13c is green unchanged.
- **`--pick` on the merge with main 39c0948d** (63 of 186 harnesses; `Game.java` left to the gate, LongPlaytest in its place):
  **exit 4**, the one NEW red KeptCheck (11 lines; ★5). KeptCheck on main apart (built from 39c0948d, the same world file): green.
  KeptCheck's bench is `LongPlaytest.probeCity` played 600 months; played apart on both models it parts between m205 and m305
  (four pieces of paper against five) and at m600 holds none on main, one of 18,030 face on BK2. Its reds read that city's state:
  "the city has a piece of paper to close" and "a bookRow was found" (section 66 finds no piece), "no month in forty-eight paid a
  holding" (the fund, 65), a sector page redrawing 89.3% of its nodes against 90%, one tooltip, a hidden label's layout in the kept
  header. BK2 touches no screen and nothing a screen reads but `Debt`'s getters, which kept their names.
- **The trace moves** (the bank's accretion, the desk's gains and the premiums are each holder's own), so **the ensemble ran**:
  80 of 80 on 6b90bbd4, against `docs/ensemble/0.9.43.md` (PD1's; 0.9.44-0.9.47 left the trace alone), committed as
  `docs/ensemble/BK2.md`: **23 of 26** against the baseline's 24. A2 and S1 fail on the baseline too. The new red is **SHOCK T2**:
  final population 24,664 [10,424 to 47,198] against the floor of 28,716 (75% of 38,288 [12,279 to 48,064]); SHOCK over its own
  AUTO2 twin is 105% (baseline 101%), so the shock costs no more; the young city is slower in the median (40,000 people at m1,090
  [821 to 1,239] against m862 [780 to 1,485]; AUTO2's final 522,706 against 585,213, with a higher low and a higher high).
  **A four-seed diagnostic** (AUTO2 seeds 0-3 to m900 on main and on BK2, the bank's re-strike, net income and accretion as
  columns): main's re-strike in those cities totals under $0.5k by m900 and the bank's accretion agrees to a tenth, yet the
  cities part (seed 3 at m260, seeds 1 and 2 near m790, seed 0 never) and land both ways: at m900 BK2 is larger in one (16,371
  against 13,564), smaller in one (17,691 against 18,431), the same in two. A perturbation that small moves the city's path, not
  its lean: the T2 red is the seeds' scatter on a criterion whose seeds span four to one (★4).
- The slices went to Sonnet 5.5 at xhigh (A: the model and `Game`'s sites; B: HoldersCheck; C: CentralBankCheck 22), each
  reviewed here; B and C ran side by side under a shared lock on `target/classes` after the first collision.
- Not a process batch: no scripts, checks' machinery or rules changed.

## 6. For the stranger's review (money and saves)

- `Debt.pass()`: the seller's per face is over `heldBy(from)`, the bank's paid face; a seller left within `HOLDING_ROUNDING` of the
  face moved hands over all its discount, so no discount stays on no face. The face moves exactly as `moveToX` moved it.
- `Debt.settled()`: the bank's take is `unsettledDiscount` after the three takings, which is (the sale's face - its proceeds) -
  sum(taker's face - taker's cash) = the bank's face - the bank's cash, whatever the takers' floors did. That is what
  `restrikeBankDiscount()` compares, so it reads nothing unless the bank's own floor binds (a premium past its own discount).
- `householdsTakeTheirShare()`: each piece's cash is its allotted cash times `take / wanted * scale`, the factor its face already
  had, so the households' price per face on each piece is the allotment's.
- The holdings dial now takes the discount moved from the pass itself (second loop), not from a pro rata read before it.
- `unaccretedOn(BANK, face)` beyond the paid face (a whole buyback before its settle) hands the unsettled sales' discount, as main
  handed the pool's; `Bank.sellPaperBack()`'s clamp at what it carries is BD1's ★1 class (main's, the buyback's to mend).
- The save: the fields are plain doubles on `Debt`, Gson's; ScaleCheck's list of scaled debt fields has the six in place of
  `discountLeft`; HoldersCheck 6 compares them across a save and a load, exactly.

## 7. Left for later

- **KC1, queued** ([KC1.md](KC1.md)): KeptCheck's fixtures cause their conditions (★5).
- A premium carried on the bank's books (★1's other way) and the buyback of a whole piece before its settle where the bank's book
  does not cover it (BD1's ★1, the buyback's to mend) are not queued: BD1's notes keep the second; the first only if wanted.

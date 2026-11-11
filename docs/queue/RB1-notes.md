# RB1 - the fund holds to its 10% ({next}, 2026-11-11)

The brief is RB1's row in `QUEUE.md` and [the wealth study](../studies/spec-wealth.md) §2.3 and §3.3, Jerus's
call ★2 (2026-11-02: "Sell the excess", as recommended). After WG2 (0.9.92). A model batch: Opus, with Sonnet
slices for the harnesses and the screens.

## 1. The rule

- **The limit is read on the holding** (`Exchange.postFundOverLimit()`). Each month, for each company whose
  market-book holding is past `TreasuryFund.OWNERSHIP_LIMIT` (.10) of its shares in issue, the fund asks a tenth of
  the excess (`LIMIT_SALE_SHARE`, ★est., spec §3.3; `TreasuryFund.limitSale()`) at fair value. The ask is posted
  after the desk's and the emigrants' and before the world's, the companies' and the households', so a buyback's
  bid (fair x (1 + `BUYBACK_TOLERANCE`)) takes it ahead of the desk's ask. The rule's own sale loop (`postFund()`)
  no longer sells an "over" amount of its own; it counts what already rests, and its rebalance, withdrawal and
  price sales stack on top.
- **Where it settles.** A buyback that retires a fraction b of the shares a month, taking the fund's tenth first,
  leaves the fund at f* = L s / (s - b) (L the limit, s the tenth): 10.2% at b = 0.2% a month, 10.9% at the
  fastest buyback the pace allows (b = `BUYBACK_PACE`/12/(1 - `SPREAD`/2)). A holding under the limit is untouched.
- **The idle cash goes to the budget** (`TreasuryFund.idleCash()`, `dueAt()`). Idle cash is the fund's cash past
  its hand's reserve and past the room its caps leave: what the rule's share bids could still take under the cap
  (`Exchange.fundShareRoom()`, at the rule's bid), and the face of the bonds in issue it does not hold, at their
  value (`BondMarket.fundBondRoom()`). The mix is not read (★b). It is withdrawn at `IDLE_TRANSFER_SHARE` (a 24th
  a month, ★est., spec §3.3) or at the dial when that is more; the rest of the fund at the dial, as before. With
  nothing idle the due is the old one to the bit.
- **Saved:** the month's idle part (`getTransferFromIdle()`, the fund's month array, slot 10) and its lifetime
  (`getTransfersFromIdle()`, the life array, slot 22). No format bump: a missing slot reads 0.
- **Shown:** the Fund screen's AGAINST THE CAP, the ticket's cap line, its (i), the holdings' and THE RULE's
  sentences say a tenth of the excess a month, ahead of the buybacks, and where the idle cash goes; TO THE
  TREASURY and the Government screen's transfer detail carry "...of it from cash its rule cannot invest" for last
  month's and next month's transfer, each shown only when there is one (`Game.fundTransferFromIdleDue()`).

## 2. Why the old sale did not hold (main, measured)

Main asked the excess at fair value, posted last of the fund's orders. At fair value only a company's own buyback
bids, and a buyback that buys from the fund alone cannot lower the fund's share: it retires the shares the fund
sold. The probe (`LongPlaytest.probeCity`, 4,800 months, sampled every 305): the fund's market book held 24-56% of
the whole market's value from month 2,154 to 4,805 (21.5-48.3% of it past the cap), over the cap in 7-9 of 21-22
companies, 100% of Construction in seven of the ten samples from m2154.

## 3. Measured on RB1

The same probe on RB1 (e27ca5ae's model): the fund's market book 7.3-8.9% of the market's value from m3374 (main
24-56%), over the cap in 0-5 of 15-21 companies at any sample, and no company past 10.5% at any sample (the excess
0.0-0.2% of the market's value). Its cash at m4805 is $89.2bn (main $722bn). Of its withdrawals over the 4,805
months, $488bn of $2.97tn (16%) came from idle cash; at every sample the idle cash was 0, paid down between them.
The first version (ff99ef2b, idle read on the mix too) held the book at 9.8-9.9% and drew 31% of the withdrawals
from idle cash, with $40.8bn left at m4805; it was dropped for ★b's reason.

LongPlaytest's own count reads 0, for a reason of its own (section 5).

## 4. The calls, taken as recommended

- ★a **A dial of 0 pays nothing, idle or not.** The player who turns the withdrawal off means off; the idle cash
  then waits. (Recommended; taken.)
- ★b **Idle is struck on the caps' room alone, not the mix**, where the spec says "past what its rule will invest
  in a year": the rule's year is not a figure the model has, its caps' room is. Read on the mix too (ff99ef2b), a
  fund in a city with no bonds found its 30% bond side idle; paid out, that left its shares past `REBALANCE_OVER`,
  and the rebalance (`sharesOver()`) sold them back to 70% for bonds that were not there, idle again the next
  month: the fund sold out at about a percent a month, without end. On the caps alone the cash its mix keeps for
  bonds waits, as on main, and a fund past its caps settles at what they hold. (Recommended; taken.)
- ★c **The idle part goes at the larger of the dial and a 24th.** A dial past a 24th a month (17 steps or more)
  must not withdraw the idle cash slower than the rest. (Recommended; taken.)
- ★d **The early ask stacks with the rule's other sales** rather than taking their maximum. It is posted first;
  the others are struck on what is left. (Recommended; taken.)
- ★e **The ask is at fair value**, not the spec's fair x (1 + BAND) (EM1-U's path). At fair x (1 + BAND) it would
  queue by time with the households' sales at the same price; at fair value it fills first. The emigrants, at the
  desk's bid, still go ahead of it by price. (Recommended; taken.)

## 5. The harnesses

- **New.** ExchangeCheck 7b: one step with the fund at 15% sells the tenth of the excess first, at fair value, the
  desk paid only for the rest of the buyback's money; and 120 months of buybacks from exactly 10% lift the fund past
  the cap (to at most 10.7654%) and never past L s / (s - b) at the run's fastest month (10.8021%, b = 0.7426%).
  FundCheck 9c: the idle cash on numbers written by hand, the due on the constants (the default, a dial of 0, the
  most of the dial, idle above the value), a city paid in past its rooms (the due, its idle part, the budget's
  line), a dial of 0 with cash idle, and both new figures through a save; section 11's round trip carries them
  too. PolicyPreviewCheck 8's idle case: the preview's due and its year on at four settings of the dial, the year
  on against a bare fund's twelve withdrawals. LongPlaytest: a line with the company-months over the cap, the most
  of any company against the fixed point (10.9%), and the months and money of the idle cash.
- **Restated, the premise kept.** FundCheck 9 and 9b's live fixture and PolicyPreviewCheck 8's paid in more than
  the rule could place, so part of the cash was idle and the due was no longer the dial's share alone. Their pay-ins
  are held to what the rule can place, with a fixture check that none of the cash is idle: FundCheck's original
  10,000 x `FACTOR` fits; PolicyPreviewCheck's 100,000 does not, and it pays in half the room. FundCheck 8's last
  block (ff99ef2b): the excess is offered a tenth a month at fair value, read on the ledger's SELL rows.
- **LongPlaytest's line reads 0** (0 of 41,262 company-months over the cap, no idle cash): `GovernmentPlayer` sets
  the playtest's withdrawal to the most of the dial, so the fund sells to pay, never holds a share, and its rooms
  are never full. The probe in section 3 is the count on a fund that invests.
- **`--pick`** (41 of 201 harnesses): all green but KeptCheck, its two recorded reds and the three in section 7.

## 6. The ensemble

`docs/ensemble/RB1.md`, e27ca5ae on 0.9.92, 80 of 80 runs: 23 of 26 against `docs/ensemble/0.9.92.md`'s 22. A2, A3
and HIGH's R3 fail as on main, to the digit. ZERO_HOT's T2 passes now only because main's own 7,704 is the baseline;
its runs are main's. Only AUTO2 moved: A1's median 2.74% to 2.75%, and its lowest seed (AUTO2-1) ends at 44,799
people, 91.3% of its 49,080 peak (main's lowest ended at 53,655, 95.0% of its peak). Every other policy's row is
main's to the digit. The first version's run (ff99ef2b, idle read on the mix) scored the same, row for row.

## 7. The reds (KeptCheck, three NEW)

Word for word:

    KeptCheck | exit 5
    KeptCheck | a month landing starts a business's KEPT and the Income page's waterfall counting exactly at the rungs countsUp() allows, and when paused (10 clocks) FAIL
    KeptCheck | the city has a piece of paper to close                                   FAIL
    KeptCheck | a bookRow was found on a money page of this city, to put the pointer on  FAIL
    KeptCheck | 5 FAILED

Each is a fixture that looks for its condition in KeptCheck's city instead of causing it: section 51 a business
whose figures move within twelve months ("no month in twelve moved the figures"), section 66 a debt to close
(`longestPiece()` finds none), section 67 a bookRow on the Finances hub ("has no bookRow this month"). RB1 moves
the city's path (the fund sells its excess and pays its idle cash to the budget), and on that path none of the
three is there when KeptCheck looks. Each failing line is the fixture's own "found none", not a page's check:
KeptCheck's fault, KS1's class (a page check on another city's path), not RB1's code. Queued as KS2: the three
fixtures cause their conditions (borrow a piece of paper, open a book row, a business whose month moves).

## 8. Left

- A save already past the cap (main's cities at 100% of a company) works its way down at a tenth a month, but
  only into the buyback's bid: at fair value nobody else buys. Saves need not load until the game is published,
  so this is noted, not handled.
- The manual names neither the cap's sale nor the idle cash; nothing in it is now untrue.
- Not done here: the write-up for the project (`claude/<title>.md`; this batch changes how the game plays). This
  session has no way to write to the project; sections 1-4 are its matter.

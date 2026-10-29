# EM1 - an efficient market: one value for a share, every holder trading on it ({next}, 2026-10-29)

The brief: [EM1.md](EM1.md), split out of HD1 (Jerus, 2026-10-28: "perhaps
make it an actual efficient market", "Yes, split it"). Started 2026-10-29 on
HD1's branch, after its item 3; HD1's coupon-as-profit was taken back (HD1-C),
so a business's earnings here do not count the coupon on its dollars abroad,
and EM1's value does not need them to (the orchestrator, 2026-10-29).

**What it does.** A share has one value: the company's money past a working
cushion at face, plus the business on its trailing net income at the required
return. Every holder reads one expected return off it and trades on it past a
band, so a cash-box company's buyback finds its sellers and its cash reaches
the owners that way (his 0.7.12 call, no special dividend, kept). On the
default playtest the hoard is gone (companies' tills 9.17x GDP to 0.39x at
m4,000, dollars abroad 36.59x to 0.01x), every buyback bid fills, P/E sits
about 22 across companies, and the town ends at 656,580 people against
599,734.

## 1. One value for a share

- `Equity.fairValue(c, book, spare, worldRate)`: spare money at face plus
  `max(0, trailing net income) / Equity.requiredReturn(world)`, a share of
  it each. `requiredReturn` is the world rate plus `FOREIGN_PREMIUM` (.03).
  The trailing income is the record's last twelve months (`RECORD_MONTHS`).
  A company with fewer months on the record is valued on its prospects, the
  old `priceOf` (max of book and its dividend capitalised); a value under
  the no-value floor falls back to the last trade.
- **Spare money** (`Exchange.spareOf`): cash, dollars abroad and the city's
  paper it holds (`SectorBooks`), less a cushion of
  `BUYBACK_CUSHION_MONTHS` (6) of its costs and debt service, the cushion
  the buyback already kept. The bank's spare is nought: its money is its
  depositors' (call EM1-K below).
- It replaces `priceOf` wherever fair value is read: the exchange's value,
  the desk's quote (fair x (1 +- SPREAD/2)), the buyback's bid, the issue
  gate (`quoteSupportsIssue`), the fund's orders and the screens.
- The screens say what it is: the sector screen's FAIR VALUE reads "X money
  + Y business" (or "on its prospects, in its first year"), with a RETURN
  figure beside it at the price; the fund screen's caption the same.
- Saved: `Exchange.State` gains the spare money, earnings and money income
  each value was struck on (`DataSave` through the exchange's state; scaled
  by a redenomination). No format bump: a save from before them reads them
  null, and its next month strikes them.

## 2. One yardstick

`Exchange.expectedReturn(c, price)` = (net income + what its money earns) /
(price x shares in issue). What its money earns is a year of its deposit
interest, the coupon on its dollars abroad and its bond coupons, none of
which is in net income (HD1-C keeps the coupon out), so it is counted once
here. A household's alternative is the best of the deposit rate, the world
rate and the households' book yield on the city's paper, plus
`HOUSEHOLD_PREMIUM` (.01), and the other shares on offer to it (slice M).

## 3. Every holder trades on it

The band is `Exchange.BAND` (.05), the pace `OutwardInvestment.HOME_SPEED`
(a tenth of a holding a month), the bonds' speeds.

- **Households** not short of their cushion sell a tenth of a holding at
  fair x (1 + BAND) into a bid over it, or at fair x (1 - BAND) when its
  return at the bid is a band under the best of their alternatives and of
  the other shares a cell could buy now (an ask resting at or under its
  limit). They buy the best return at the ask, up to fair x (1 + SPREAD/2).
  A household short of money still sells as before.
- **The world** buys on the expected return (it read the dividend yield),
  its limit capped at fair x (1 + SPREAD/2), and sells a tenth of its holding
  into a bid over fair x (1 + BAND), posted after the companies' bids so it
  meets the buyback (`postWorldPrice`).
- **The fund** buys by its index weights as before, and sells a tenth into a
  bid over fair x (1 + BAND) (call EM1-U).
- **The desk** quotes fair x (1 +- SPREAD/2), as before, on the new value.

## 4. The buyback finds its sellers

The bid is unchanged: fair x (1 + `BUYBACK_TOLERANCE`), .10 over the value,
more than the band, so every holder past its cushion sells into it. In his
city it filled $0.58B of a $4.4T bid a month. On the default playtest it
filled 0.3% of what it bid a year at m4,000 (1.6B of 542B); now it fills all
of it (18.4B a year, 1.5B a month, nothing left resting). A company raises
the cash by recalling its dollars abroad and selling the city's paper to the
desk (`OutwardInvestment.recall`); the bank counts the paper it buys from
firms (`Bank.paperBoughtFromFirms`) so BankCheck's cash identity holds.

## 5. The playtest, before and after

HD1's tip (main's trace) against the final model, the default playtest,
`LongPlaytest`'s MARKET line (slice P):

| | m600 | m1,200 | m2,400 | m4,000 |
|---|---|---|---|---|
| tills x GDP, before / after | 2.09 / 0.39 | 1.22 / 0.32 | 2.47 / 0.38 | 9.17 / 0.39 |
| dollars abroad x GDP | 2.06 / 0.01 | 2.09 / 0.01 | 9.13 / 0.02 | 36.59 / 0.01 |
| buybacks a year: filled, left resting (before) | 38.5k, 218k | 615k, 27.3M | 6.6M, 3.2B | 1.6B, 540B |
| buybacks a year: filled, left resting (after) | 199k, 0 | 7.5M, 0 | 934M, 0 | 18.4B, 0 |
| P/E low / median / high, before | 10 / 30 / 473 | 6 / 15 / 74 | 8 / 35 / 187 | 5 / 176 / 4,975 |
| P/E, after | 18 / 22 / 23 | 6 / 24 / 552 | 21 / 22 / 23 | 12 / 22 / 23 |
| P/B low / median / high, before | .19 / .98 / 1.6 | .34 / .90 / 798 | .88 / 1.00 / 1.12 | .96 / .97 / 1.11 |
| P/B, after | .07 / 1.31 / 15 | .07 / 1.36 / 15 | .08 / 1.86 / 28 | .00 / .69 / 22 |
| turnover, % of cap a year | .6 / 3.0 | .2 / 1.3 | .0 / 1.9 | .0 / 2.1 |

People at the checkpoints, before / after: 5,595 / 5,237 (m610), 24,166 /
40,730 (m1,314), 156,100 / 230,868 (m2,370), 375,308 / 462,019 (m3,290),
599,734 / 656,580 (m4,005). GDP a month at the end $9.9T / $10.0T. The bank
failed 11 times before and 10 after; the capital the city put back fell from
$1,719T to $90.8T. P/B now spreads (a company's book is no longer its value);
P/E centres on 1 / requiredReturn (20 at a 5% return) and on 22 where the
money it holds adds to it.

## 6. The calls (each took its recommended way, BP1)

- **EM1-G, no growth term.** The brief's "less its growth": not built. A
  trailing growth figure in this model swings with founding and shocks, and
  r - g near zero makes the value explode; the value is trailing net income
  at r. Recommended as built; a growth term needs a smoothed forward figure
  the model can give honestly first.
- **EM1-F, no forward figure.** The brief's "a forward figure if the model
  can give one honestly": it cannot yet (no order book of the next year's
  sales); trailing only.
- **EM1-R, one required return for every sector.** "Sectors may differ on
  growth and risk": every share at the world rate plus FOREIGN_PREMIUM.
  Recommended; a premium by sector is a study of its own.
- **EM1-U, the fund an index.** It sells into a bid past the band, and buys
  by its weights, not by return: an index fund does not pick shares.
- **EM1-K, the bank on its earnings alone.** Its spare money is nought (its
  cash is its depositors') and what its money earns is in its income.
- **EM1-W, the world's price sale after the companies.** The brief's order
  had none; posted after the buybacks, the world's tenth meets the bid that
  the company posts that step.
- **Two harnesses' references, the orchestrator's calls (2026-10-29), on
  this session's recommendation.** EM1's played cities exposed two places
  where a harness had fallen out of step with documented design; neither
  is EM1's code. MapCheck's reference for the far view's runs layer now
  holds the yards' and sea terminals' 2 x 2 dots that `MapTiles` has drawn
  there since 0.8.1 (EM1's city had a quay by m242, main's none).
  PlanCheck's `groundParts` now parts the ground at a building on a site,
  as `DistrictPlan.wallJoinable` and FW11's doc say: its section 6 city
  walled a street piece in behind a farm's building on its field (district
  (0, 0), months 91 and 121), which the join does not cross. Whether a farm
  on its own field should block a street at all is EM1-M, for a map batch.

## 7. The checks, the trace

**MarketCheck** (new, 108 assertions, after ExchangeCheck), each case on a
fixture that causes it: (1) one value - earnings over the required return,
not book; a cash box at its spare money; a company in its first year on its
book, its twelfth month moving it to its record; the parts adding up; (2)
one yardstick - a company with no money returns the required return
exactly, a cash box what its money earns; (3) two companies alike but for
price - the desk open, the prices close ($31.20 and $16.80 to $24.24 both);
the desk closed, the money goes to the cheaper and nobody pays the dearer's
price; the switch, a cell selling A for B on offer a band better, and
nothing with B at A's return; (4) the buyback fills from cells at fair x
1.10, the till and the shares falling by the month's pace, BAND under
BUYBACK_TOLERANCE, and nothing at the equity target; (5) a cash box earning
under the alternatives is sold by the cells and the world, a company at the
required return is not; (6) a quiet market, seven steps with no trade, no
resting sell and no price moved, beside a control a band behind that does
trade; (7) the save and a redenomination. Mutations of the model (the return
sale off, no switch, the old value, no money income, BAND at 0 and .15, the
desk on the last price, the world never selling, and others) were each
caught, in a scratch copy.

What it found: **the desk arbitrages first.** While it has room it bids
fair x (1 - SPREAD/2) for every company, so an ask under that never rests:
a cell's switch from one share to another fires only when the desk is full
(or closed) in the cheaper one; otherwise the desk takes the cheap offer and
the cell's sale of the dear one goes to the desk's bid. The two prices close
either way, as the brief asks.

**The full suite** (`./batch-check.sh`, both data files named, main with
BK1 and HD1 merged in): exit 0, 153 harnesses in 491 s, one timing figure
SOFT (MapCheck, run beside other work; main's gate records MapCheck's
timing too), Stale no firm findings.

**The ensemble** (`tools/ensemble/ensemble.sh em1`, ffe4e607, the final
model with main's BK1 merged; [EM1.md](../ensemble/EM1.md)) against
0.8.68: 23 of 25, as 0.8.68, the same two failing and both milder. Growth
up: AUTO2's final population 261,977 [204,822 to 311,995] against 255,250,
40,000 people at m1,189 against m1,222 (16 of 16), ZERO_SLACK's 180,506
against 165,130; no stall, every other T2 over its baseline. A2 (the longest
spell of yearly inflation over 10% from m240) 11 months [3 to 16] against 10
[3 to 31]; S1 (ZERO_SLACK's window over its twin) 1.78% against 1.11%, its
longest spell 31 months against 80. Inflation m240 to the end 2.15% against
2.20%, the young city 3.18% against 3.26%.

Slices on Sonnet 5.5 at xhigh, each reviewed as a stranger's: P (the
playtest's MARKET line), A (the value, the yardstick, the rules, six
harnesses re-premised), M (the switch between shares, six reds mended), B
(MarketCheck), M2 (the full suite's five reds: three fixtures, the PlanCheck
guard, the two map reds found and left for the call); Opus on the briefs,
the reviews, the diagnosis of the young city's inflation, the screens, the
two harness references, the playtests and the ensemble. Harnesses re-premised, none of their assertions moved:
ExchangeCheck (its fixtures read the new value and band), BankCheck (the
cash identity counts paper bought from firms; section 13's world sells the
bank's shares), FundCheck, SaveFileCheck, ScaleCheck (its fixture month 807
to 1,124: the EM1 town takes off at m1,075; every assertion holds at 1,120,
1,121, 1,124, 1,125 and 1,128 only, five months of nine - EM1-S), FloatLinkCheck
(holders read at the coupon: a business sells the paper it bought at the
settle for a buyback the same month; its sales sized to a third of a year's
tax, as its households now save more), CallCheck (the same sizing),
FundLedgerCheck (a fund at the cap on a company whose step leaves it there;
the hand's order sized to the room the rule's bid leaves). CentralBankCheck
passes as it was: its very loose last year 6.07% against the 7.0% bound (main
4.09%; 7.12% before the switch). The first full suite found five more, three
of them fixtures (slice M2): ForeignCheck holds the wood products (the weaker
city founds a sawmill the parity city does not, and its sawlog imports are
no part of the programme; on main they took the premise to 1.0490 against a
band of .05, on EM1's path to 1.0533); CurrencyRegimeCheck finds a seed whose
peg breaks on doubt after credibility climbed (the default seed's now breaks
at month 47, before it climbs) and plays a candidate until a purchase is
owed; OrderSearchCheck copies at month 500 (at 400 EM1's town sizes no
order past one building). PlanCheck's `HOMES_ENOUGH` guard counts different
homes cells, as its 1 in 59,049 assumes (it counted a cell once a sample,
three cells as ten). The other two were the harness references above.

Not a process batch: no scripts, checks' machinery or rules changed.

# TP1 - trade by partner through a currency reform

Queued 2026-10-30 by the orchestrator, from the stranger's review at WN3's
gate (saves, money and threading; verdict "ship + follow-up"). A small fix
batch: Sonnet whole is enough (`docs/batch.md`, 3).

## The fault

WN3 records trade by partner in thousands of the city's money, and a currency
reform rescales none of it:

- `Game.reformCurrency()` calls no `PartnerTrade.redenominate`, and there is
  none. The month's and the year-to-date value arrays and `yearCells`'
  valueOut/valueIn (all saved in `DataSave.partnerTrade`) keep the old unit.
- `HistorySave.redenominate()` skips `tradeByPartner`, so every finished year
  stays in the old unit for good: the cliff `redenominate` exists to prevent.
- So after a reform the nation card's year to date mixes old and new money
  until January, and last month's lanes are off by the reform's scale until
  the next tally.

It is display and record only: the band reads units (`monthUnits`), and no
pool moves.

## To do

- A `PartnerTrade.redenominate(scale)` that scales the month, the year to date
  and `yearCells`, called from `reformCurrency` beside the others; and
  `HistorySave.redenominate` scaling `tradeByPartner`'s values (not its units).
- `TradeLanes.MIN_LANE` (300) is a threshold in absolute city money: it drifts
  with inflation, and a reform dividing by 100 hides every lane. Put it in real
  terms (the price index, or a share of the month's trade) and say which.
- A DenominationCheck section that causes a reform in a city with trade by
  partner and asserts the card, the history and the lanes on both sides of it.

No save format change: the fields keep their keys, only their values scale.

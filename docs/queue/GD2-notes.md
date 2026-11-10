# GD2 - gold in the vault (0.9.84, 2026-11-10)

The brief is `docs/studies/spec-gold-money.md` §4.2 (GD0, 2026-11-02; Jerus
took its four calls the recommended way the same day, "All four, mines last
(Recommended)"). Gold is the vault's ninth reserve, XAU, at the end of
`ForeignAccounts`' moneys, priced by GD1's `GoldPrice`. `WorldMoneys.EIGHT`
stays eight, so the city's paper, the policy rates and the world's moneys know
no gold. The player buys it by name, or under Choose with a share of it.

## 1. What changed

- **`ForeignAccounts`** (banner THE VAULT IN EIGHT, its new paragraph AND
  GOLD, THE NINTH):
  - `MONEYS` 9, `GOLD` 8, `CODES` (the eight, then "XAU", ISO 4217's troy
    ounce) and `codeOf(k)`. `moneyIndex("XAU")` is `GOLD`.
  - Gold's name "gold", plural "gold" and mark "XAU ". Its balance is in
    thousands of troy ounces and its rate in US$ an ounce.
  - Revalued as the others: a new price moves the month's cross move by the
    ounces times the change, and gold can be the money that moved it most.
  - It earns nothing, whatever rate is handed, and the vault's yield is struck
    on the balances that earned, gold left out.
  - It comes in only by name (`buyReserves(local, "XAU")`) or under `CHOSEN`.
    `ALL_USD` (the default) never touches it. `WORLD_MIX` (COFER counts no
    gold) and `MATCH_TRADE` (no nation trades in it; `setTradeShares` reads
    only the eight) put nothing in it.
  - It leaves largest-first with the rest (`vaultRemove` as it was). The
    study's "gold first under the Gold regime" is GD3's, with `Regime.GOLD`.
  - It anchors no peg: `setPegAnchor` takes only the eight.
- **`Game`**: `handTheMoneysToTheVault` hands gold's price as the ninth rate.
  It uses the month the moneys stand in (`WorldMoneys.month()`, the same
  index as `GoldPrice`'s t). The interest array gives gold NaN.
  - A transient `GoldPrice`, built from the nations, is rebuilt when their
    path or seed differs. It is pure, so it is never saved.
  - `goldUsdPerOunce(month)` and `getGoldMove()` (this month's price over
    last month's, less one) are for the table.
  - `handTheUsRate`'s array is the eight's.
- **`DebtManager`**: its money arrays are the eight's (`WorldMoneys.EIGHT`),
  so a nine-long array handed reads its first eight. The city's paper is never
  written in gold (`offers("XAU")` is false).
- **`VaultView`**: nine rows, the eight then gold. Gold's move is
  `getGoldMove()`: the history keeps no gold series, and the price is pure, so
  a city just loaded shows it. Its policy rate is NaN.

- **Trade › The reserves** (`ui/TradeScreen`, `ui/VaultTable`):
  - The exchange's money picker gets a Gold chip, last, once gold has a
    price. A press buys or sells gold by name with the treasury's money at
    the month's price ("Buy D$182k of gold"). The card shows "of which gold
    19k oz → 27k oz" and "gold's price this month US$20.67 an ounce".
  - Under Choose, gold's row reads "Gold (no interest)".
  - The vault card is "IN THE VAULT, IN EIGHT MONEYS AND GOLD". Gold's row
    is "Gold · US$20.67 an oz", held in ounces and worth in US$. The total
    row is "In all". The card's info adds two sentences on gold. The fold's
    two charts stay the eight's.
  - The tips on the cross move and on the interest mention gold.
- **Policy › The currency**: the peg's anchor picker offers the eight only.
- **Look at** the renders in `docs/queue/GD2/` (`RenderGD2.java` beside
  them): `vault-gold.png`, `vault-gold-years.png` (the fold open),
  `exchange-gold.png`, `sell-gold.png`, `mix-gold.png` and
  `vault-gold-narrow.png`. At 520 wide the table clips after "worth, US$".
  Its column widths are fixed (about 770 px), as they were before gold.

## 2. The save

- `foreignAccounts` gains slots 149-151: gold's balance, its price as last
  handed (0 if never handed) and its chosen share. Slot 148 is the peg's anchor
  since WN4b, so the study's "147 onward" moved up by two.
- `restore` reads the eight's old slots (105-141) for the eight alone. With
  nine, the old loop would have read slot 121, the mix, as a rate. Gold's three
  slots are read only when the array is that long.
- **No format bump**: a save from before GD2 loads with no gold, no gold rate
  until the next month hands one, and a chosen share of 0. Nothing loads
  wrongly, as VaultMoneysCheck 11 asserts.
- SaveFileCheck's fixture now buys gold by name and gives it a share of the
  chosen mix, so its round trip covers 149-151 (it adds D$15k to the town's
  cash first: the euros and pounds had spent it). The length checks read 152
  (SaveFileCheck, InterestAbroadCheck, PegAnchorCheck, CurrencyRegimeCheck 10).

## 3. The checks

- **VaultMoneysCheck 8-12** (new):
  - 8, gold the ninth: a purchase into XAU holds its US$ over the price; the
    world's mix, the trade's and the default put nothing in it; a chosen 30%
    puts 30% in it; no price means the US$.
  - 9: gold earns nothing at any handed rate, and the yield's base leaves it
    out.
  - 10: a new price moves the cross by ounces times the change, with "XAU" the
    driver when gold moved most; a named sale clamps at gold's own.
  - 11: the save round trip, an array cut to 149 loading with no gold, and gold
    refused as a peg's anchor.
  - 12: in a city on the full file, the vault's gold price is
    `GoldPrice`'s for the moneys' month to the bit; a default city holds no
    gold; the debt offers no XAU paper.
  - Its sections 1, 2, 6 and 7 now read the eight where they meant the eight.
- **Changed where the ninth broke them, premises kept**: ReadPathCheck,
  InterestAbroadCheck ("each of the eight", and none in gold), PartnersCheck
  (nine rows; gold's policy rate NaN, its move the price's), PegAnchorCheck
  (gold refused even with a rate), SaveFileCheck, CurrencyRegimeCheck.

- **`--pick`** (the Earth and world files named): exit 3. It ran 48 of 192
  harnesses, and the only red is KeptCheck, main's recorded one
  (`docs/notes/gate.txt`). `Game.java` is left to the gate's suite, with
  LongPlaytest in its place.
- The script asked for two reviews, both done:
  - Money: DebtManager's arrays are the eight's now, the same figures as
    before.
  - Threading: `Game.goldPrice()` is synchronized, as `getNations()` beside
    it is, because the table's draw reads it on the FX thread too.

## 4. The trace, the calls

- **The trace is main's**: the default playtest traced (`-Dplaytest.trace`,
  the Earth and world files named) on main's classes (52408690) and on GD2's
  model (a953e597). The eight files are byte-identical, and the report differs
  only in its run-time lines (75 s and 48 s). The default mix is all US$, so
  nothing buys gold. No ensemble (EN1).
- **No write-up in the project**: gold is a new choice, but the study
  (`spec-gold-money.md`) is its design and the changelog its record.
- **Calls**: none left. Two of the batch's own: gold's mark is ISO's "XAU ", as
  the franc's is "CHF ", and the vault's charts stay the eight's (no gold
  series is kept; the table's "this month" carries gold's move).

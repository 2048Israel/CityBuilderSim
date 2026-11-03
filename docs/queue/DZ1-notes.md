# DZ1 - The trade by partner in the money of the day ({next}, 2026-11-02)

FC1 found it (`docs/queue/DZ1.md`): with its builders' price on, DenominationCheck's reformed twin parted from its town
in month 170, every sector's cash at once, by about 1e-9, through something in absolute money outside FC1. FC1 played
the harness with FC1 off meanwhile.

## 1. What was found

- **Only with the nations' file.** On FC1's tip with FC1 on and no `data/world-1.bin` (a worktree has no `data/`), the
  twins hold to 4e-15 a decade on: green. With the file named (`-Dcbs.world`, LC3's file, as the gate names it) the
  exchange rate is 4e-16 apart through month 168 and 5.6e-8 apart in month 169, a January; the cash parts a month
  later (7e-10), and by month 221 the twins are 5e-7 apart, under the decade's band of 1e-6 (FC1 measured 3.1e-6 on
  an earlier state of its own). The town is FC1's hot one: price index 6.18, 4,057 people at month 221.
- **The instrument** (scratch, not kept): a walk of every number in both Games, field by field, each month, the money
  read through the factor. In month 169 the first figures apart were ForeignAccounts' terms of trade (`totStep`
  5.8e-6), which `stepTermsOfTrade(Game.cityExportPriceLevel())` sets each January.
- **The cause: PartnerTrade's history grid did not follow the unit.** `PartnerTrade.finishYear()` rounds the finished
  year's money to `HISTORY_ROUND_MONEY` places, hundredths of a thousand at today's prices. EP1 made that grid follow
  `PriceLevel.FACTOR` (PriceLevelCheck 9: the same parting, at the factor); nothing made it follow the Denomination's
  unit. After a reform by 100 the grid is a hundred times coarser against the city's money, so the twins' cells
  round apart, and `Game.cityExportPriceLevel()` (and `tradeByNation()`, the reserve mix's and the real rate's
  weights) read them. A scratch patch of the grid alone took the twins at month 221 from 5e-7 to 5e-15.
- **Read on the way: the reform never touched PartnerTrade.** `Game.reformCurrency()` divided every balance but the
  trade by partner: the month's and the year's money by nation, each (good, nation) cell's value, the finished years,
  and HistorySave's copy of them for the save. A reform in mid-year would weigh the year's exports in two units at
  once; the twins reform on a year's turn, so only the grid showed.

## 2. What was built

- **PartnerTrade reads the unit.** `setMoneyUnit(DoubleSupplier)` (the unit at 1 until it is set), and the history's
  grid is `HISTORY_ROUND_MONEY` times the unit: with the unit at 1 it multiplies by exactly 1.0, so no city that never
  reformed moves. `Game.ensureWorldMarket()`, where the one PartnerTrade is made, hands it `denomination::getUnit`;
  a load restores into that same record, and the Denomination is the Game's own for life, so the supplier follows.
- **The reform divides the trade by partner.** `PartnerTrade.redenominate(scale)` divides the month's and the year's
  money by nation, the no-partner figures, each (good, nation) cell and every finished year (`Table.scaled`), and
  republishes the screen's snapshot; `Game.reformCurrency()` calls it beside `historySave.redenominate`, which now
  divides its copy of the finished years too. Units and tonnes are left as they were.
- **DenominationCheck** plays FC1's builders' price again (its `buildersShortOn = false` gone), holds the year and the
  decade to 1e-9 as the month is (★), and has section 7: on bare PartnerTrade objects, a copy on the reformed unit
  rounds as the plain one divided (1e-12), a copy left on the old unit is a step off, and a mid-year `redenominate`
  divides the month, the year to date, the history and HistorySave's copy. Its second part is the Game's half, the
  two lines the bare objects cannot reach: two cities on the repository's twelve-nation fixture (ReadPathCheck's WN3
  city, the property put back as found, so section 2 still plays the gate's file), one reformed in July. Right after
  the reform the record, the finished year and the save's copy are the plain city's divided (0.0 apart); at the next
  January the year finished on the new grid is 2.1e-16 from the plain one's divided, its units bit-equal, where the
  founding grid would put its cells 1.0 apart (they round to nothing); `sameCity` at 1e-9 holds to 2e-15.
- Slices: PartnerTrade, HistorySave, Game's two lines and section 7's first part were one Sonnet 5.5 slice (xhigh);
  the review changed one thing (the fixture's units tallied as the allocations' sums, 4 and 2, as the record's
  contract has them). Section 7's second part was a second slice, taken as written: the slice mutated each of the
  three wiring lines in turn (scratch copies, not the tree) and each turned the part red (Game's `setMoneyUnit`: 2
  FAILs, 1.0 apart; Game's `redenominate`: 4, 99 apart; HistorySave's: 2, 99 apart). Its one counterfactual, the
  founding grid's rounding written out in the harness, is there only to show the band can tell the grids apart.

## 3. The calls (★; each takes the recommended way)

- ★ DenominationCheck's year and decade held to 1e-9, as the month is (recommended), not to a millionth: the band hid
  this parting at 5e-7 for fifty months, and with it mended the twins hold to about 1e-14. The alternative is the
  millionth the file had.
- ★ For the orchestrator, not done here: a worktree's checks run every harness without `data/world-1.bin`, so the
  nations' paths (the terms of trade, the trade by partner, the moneys) are first played at the gate, and FC1's
  finding could not be seen where it was looked for. Recommended: a batch runs its `--pick` with the PC's `data/`
  named in `JDK_JAVA_OPTIONS`, as the gate does (a line in `docs/batch.md` item 4); DZ1's own checks did.

## 4. The checks

- `./batch-check.sh --pick` with the gate's `data/earth-2.bin` and `data/world-1.bin` named in `JDK_JAVA_OPTIONS`
  (on 72b67546, GT1's tip merged): exit 0; 24 of 170 harnesses (Game.java left to the gate, LongPlaytest in its
  place) green in 265 s, one timing assertion SOFT (SaveSlotCheck, not counted; the PC was busy); maps regenerated;
  Stale 0 firm. The review line names HistorySave (saves): no saved field and no format change, only the copy the
  reform now divides, and GT1's snapshot-on-write `settle()` runs first in `redenominate`, the copy keeping lists of
  its own.
- DenominationCheck on the gate's world file: the twins say "trade by partner: yes"; a month, a year and a decade
  after the reform every figure at or under 3e-15 against 1e-9; section 7 green both parts.
- No ensemble: the playtest never reforms, and with the unit at 1 the grid multiplies by exactly 1.0, so its trace
  cannot move.

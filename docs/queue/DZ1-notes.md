# DZ1 - The trade by partner in the money of the day ({next}, 2026-11-02)

> RESUME (paused on the orchestrator's word, 2026-11-02; delete this block before the ship file):
> 1. Found and mended (§1): PartnerTrade's history grid now reads the Denomination's unit; the reform divides PartnerTrade and HistorySave's copy. Local WIP commit on batch/DZ1 (built on GT1's b40db6c8), not pushed.
> 2. DenominationCheck: FC1 back on, year and decade at 1e-9, section 7 on bare PartnerTrade objects: reviewed, green in a worktree (no world file).
> 3. Left: section 7's second part, two cities on the twelve-nation fixture (WorldData.FIXTURE, as ReadPathCheck.onTheWorldMarket) reformed in mid-year; Game's two wiring lines are proved only by it or the gate's world file.
> 4. Then: merge GT1's tip when its ship file lands; `--pick` with JDK_JAVA_OPTIONS naming the PC's data/earth-2.bin and data/world-1.bin; fill §2 and §4; ship file last, once GT1 is on main and merged in.
> 5. No ensemble: the playtest never reforms, and with the unit at 1 the grid multiplies by exactly 1.0, so its trace cannot move.

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

(filled at the review)

## 3. The calls (★; each takes the recommended way)

- ★ DenominationCheck's year and decade held to 1e-9, as the month is (recommended), not to a millionth: the band hid
  this parting at 5e-7 for fifty months, and with it mended the twins hold to about 1e-14. The alternative is the
  millionth the file had.
- ★ For the orchestrator, not done here: a worktree's checks run every harness without `data/world-1.bin`, so the
  nations' paths (the terms of trade, the trade by partner, the moneys) are first played at the gate, and FC1's
  finding could not be seen where it was looked for. Recommended: a batch runs its `--pick` with the PC's `data/`
  named in `JDK_JAVA_OPTIONS`, as the gate does (a line in `docs/batch.md` item 4); DZ1's own checks did.

## 4. The checks

(filled at the end)

# WN1 - the nations on the world's path: Historical or Random ({next}, 2026-10-28)

The batch's record. Jerus took the nations study on 2026-10-20 ("do rec"), and the night's line on 2026-10-28
("you are to continue all night until 7am"). The brief is `docs/studies/spec-nations.md` §9's WN1 row, §7 (the
path) and §3.2, on WN0's world file. It is the nations lane's second batch, built on WN0's branch (RL1) and merged
with main once WN0 landed (6e137f09). Opus wrote two briefs; two Sonnet 5.5 slices at xhigh coded them, one after
the other (1: the path and seed from the founding screen to the save; 2: `Nations` and `NationsCheck`), each
reviewed as a stranger's diff and committed apart.

## 1. What it is

- **`Nations`**: each nation's year (output in US$ of 2015, people, CPI, inflation, its money's rate to the dollar,
  with the file's marks) and each good's real price, as a pure function of the path, the seed and the year (§7.3).
  Worked year by year for all nations at once, eagerly to 2100, on demand after.
  - **Historical** replays the file to 2025, each series to its last published figure; then the game's rule with
    seeded shocks at half Random's size and no crises; past 2100 growth a head eases from its 2091-2100 pace to 1%
    over 50 years and people are held (the leaderboard's D8, the study's D14).
  - **Random** starts from the file's 2000. Growth a head (in logs, so output never goes negative) is a trend plus
    four terms. The trend is 1.5% plus 2% of the gap to the US's output a head, capped at two points. The terms are
    a world factor, a region's factor, its own shock (0.3 carried over) and a crisis now and then. Inflation is
    drawn around the income group's mean. A money follows relative parity plus a shock, or holds its peg.
  - **Goods**: each good's real price from the file's prices layer (WN0 added it for this) to 2025, then mean-reverting
    to 2025's (0.7 carried) at the good's own measured swing.
- **The founding screen's World step**: THE WORLD BEYOND YOUR CITY, two chips (Historical | Random) and the path's
  own seed with its dice, rolled when the page opens; the inflation chips are THE DOLLAR'S OWN INFLATION (§11,
  item 7). The page is about 80 px taller: a generated world's no longer fits a 768-px window whole and scrolls.
- **The save**: `Founding` carries the path and seed; `DataSave` writes `worldPath` and `pathSeed`. No format bump:
  nothing reads them, and a save without them reads Historical on seed 48,213 (WN3 bumps the format, D28).
- **`Game.getNations()`**: builds `Nations` from the game's world file (`WorldData.locate()`) the first time it is
  asked, and again if the founding's path or seed changes; null where the file is not here. Nothing calls it.
- **`LongPlaytest`** takes `-Dplaytest.world=historical|random` and `-Dplaytest.pathSeed=<n>`.

## 2. The calls (each ★ took the recommended way, BP1)

- **★1 The path's seed is its own, not the ground's** (board 4): a city can keep its coast and draw another world.
- **★2 Historical's future shocks are Random's world, region and own terms at half size (`HISTORICAL_SHOCK_SHARE`),
  with no crises.** §7.1 says only "sized as Random's but smaller"; a crisis is a slump, not a small shock.
- **★3 The catch-up is capped at two points a year (`CATCH_UP_MAX`).** 2% of the whole gap to the US (Barro's rate,
  which is conditional on a nation's own steady state) would grow the poorest about 10% a head a year.
- **★4 The frontier is the United States' output a head.**
- **★5 Random keeps each nation's money of 2000**, under its 2025 regime from 2001 (a later euro member keeps its own
  money, floating); Historical uses the 2025 money after 2025. WN4 builds the moneys on the exchange-rate study's rule.
- **★6 Random's slumps run lighter than the real world's.** On the full file over five seeds, 2.7% of nation-years
  fall more than 5% in output, against one in twenty in 2000-2024 [R11]; crises start at 1.67% against Barro's 1.7%.
  Recommended: keep the sourced dials. The gap is in the tails of small volatile nations (oil, conflict), and WN3's
  ensemble, the first to read the paths, judges whether Random's world is too calm. The alternatives are `SD_MAX`
  .045 to .06, or crises over two years instead of three.
- **★7 The tooltip says the path is kept and nothing reads it until the world market comes**, since players on
  itch.io see the step now.
- **★8 `-Dplaytest.pathSeed`, not the spec's `-Dplaytest.worldSeed`**, which would read as the ground's seed.

## 3. For WN2 and WN3

- `Game.getNations()` opens and reads the 8.7 MB world file the first time: call it off the screen's thread (or at
  load), not in a frame. It is `synchronized`, the only such member of `Game`, so it never waits on the month.
- The game finds no world file until WN0-R's release and `get-earth.bat nopause world-1`; until then it returns null.
- Nations are numbered 1..N as `WorldData` numbers them; `Nations.yearOf(gameMonth)` gives the city's year.

## 4. The checks, the trace

- `./batch-check.sh --pick`: exit 0, 14 harnesses (NationsCheck, SaveFileCheck, LongPlaytest in Game's and
  Founding's place, TimingCheck and the rest), Stale no firm finding. Its review line: saves (`DataSave`, above) and
  threading (`synchronized` on `Game.getNations()` and `Nations`' public reads, above).
- **NationsCheck** (new): the fixture always, the full file under `-Dcbs.world` (196 nations, 1.2 s). Against the dials:
  the world factor's SD .00924 (.00935), the own shock's persistence .287 (.30), the frontier's swing .01795 (.018),
  Historical's swings .504 of Random's (.5), Historical's growth a head 2160-2200 .01034 (.01). Twelve planted bugs
  each turned it red.
- **The trace**: the default playtest on main (1a907f71, built apart) and on this branch, traced
  (`-Dplaytest.trace`): all eight CSVs byte-identical, the log identical but for its two run-time lines (66 s, 65 s).
  So no ensemble (EN1's rule).
- **Not checked by eye**: the founding screen's new row on both panels (WN1-C).

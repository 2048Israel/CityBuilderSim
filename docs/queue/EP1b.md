# EP1b - the world from 1900: the world file rebuilt from 1900

Queued 2026-11-01 by EP1, which shipped the first cut its brief allowed (the
calendar and the price level; `docs/queue/EP1-notes.md`). The decisions are
Jerus's of 2026-10-30 (`docs/queue/EP1.md`: "move the start date and history to
1900 ... along with the history"), the study is EP0's
`docs/studies/spec-1900-start.md` (sections 3, 4, 5 and the calls 5 to 9), and
the sources that may ship are LC1's (`docs/studies/licences.md`, and the LC1
note under the study's call 11).

## What EP1 left

The world file (`data/world-1.bin`) starts in 2000. EP1's reader supplies the
century before it by a rule (`WorldData`, THE STAND-IN CENTURY): real series
held at 2000's, every nation's prices on the US CPI, moneys and rates held,
goods at their 2000 real ratios; the eight banks run the game's rule before
their table (`PolicyRates`). A Historical city replays the real world only
from 2000.

## What it is

- **The file rebuilt from 1900** (call 9): paths 1900-2100, prices 1900-2025,
  monthly rates 1900-01 to 2025-12, a new file name and release asset (the
  release is Jerus's), the fixture re-cut. ONE re-bake with LC3's (the IMF's
  figures out) and WD1's (the makers' output), not three: whichever runs first
  carries the others' layers, or they run as one.
- **People and output replayed** (call 5): OWID's people for 195 of 196,
  Maddison's output for 47, the chain rule for the rest (study 3.2-3.3).
- **The moneys before the euro** (call 6, (a)): a place uses its ruler's money
  until it has its own; the euro's members their legacy moneys at published
  rates; the gold pars to 1914, the Federal Reserve Board's Banking and Monetary
  Statistics 1914-1970, PWT 11.0 from 1950, the H.10 from 1971 (LC1's list).
- **The nations under their 1900 ruler** (call 7, (a)): a `sovereign_history`
  column, each row's status from the Factbook, Wikidata and the facts in
  Wikipedia, never the Correlates of War list (LC1).
- **The banks' real rates** (call 8): `policy-rates-eight.csv` re-sourced to
  LC1's owners (BIS, Bank of England, Bank of Canada, RBA, Bundesbank, the Fed;
  Banking and Monetary Statistics 1943 before them), its gaps by the anchor
  rule; the stand-in rule retired where a table reaches.
- **The US CPI 1900-1912** read against a public-domain copy of HSUS E 135
  (EP1-H), and `PriceLevel`'s series and the file's US path one series.

## Checks

The stand-in's harness sections (WorldDataCheck 9, NationsCheck's, InterestAbroadCheck 2's
first stretch) become the replay's; `standInYears()` is 0 on the new file;
PriceLevelCheck's US level within its tolerance or tighter; every trace moves:
the full suite and the ensemble.

## Traps from EP1's gate review

Three things the stranger's review at EP1's ship gate found in the stand-in that
EP1b's file walks into; none shows in a green suite today.

- `WorldMarket.baseYearRead()` (`WorldMarket.java:1184`) on Random is `baseYear -
  standInYears`. Once the file starts in 1900, `standInYears` is 0 and Random's
  base year becomes 2019, 119 years into its path (the trade base year is the
  file's, not the calendar's); WorldMarketCheck stays green, because it reads the
  Historical path. Re-derive Random's base year from the calendar (the year the
  game's month 1 falls in, plus what the path reads), and give WorldMarketCheck a
  Random section that would have failed.
- `pad()` (`WorldData.java:633`) calls `PriceLevel.usCpi()` for the nominal series
  it extends, and `usCpi` throws outside 1900-2025: a nominal series whose first
  figure is after 2025 refuses the whole world file, and the city plays on with no
  nations. A file whose series start where their sources do (EP1b's replay has
  many that start after 1900) must not meet this; clamp or skip, and assert it.
- On Historical, `PolicyRates` drops the rule's state at `TABLE_FIRST_MONTH` (month
  1200), so every bank's rate and shadow snap to the table in one month (the Fed
  to 5.5%, the Bank of Japan to 0.25%) from where a century of the rule left them.
  EP1b's table replaces the rule from 1900 and the snap goes, except for a bank
  whose table starts later (its gap by the anchor rule): there the rule's last
  shadow must hand over to the table's first line, not be dropped, and
  InterestAbroadCheck's stretch asserts that month's step.

## From WN5's gate review (2026-11-01)

WN5 saves the land's sellers and the city's tally of land bought by nation
(`landOffersSellers`, `landBoughtKm2`, `landBoughtUsd`) under world-1.bin's
nation numbers, and `restoreLandBought` checks only the array's length. A
re-bake that renumbers or adds nations would load them against other
nations: key them by ISO code, or raise SAVE_FORMAT and the floor with the
re-bake.

# EP1 - the game starts in 1900: the calendar and the history, before research

Queued 2026-10-30 by the orchestrator, on Jerus's word that night: "what i
do want to do is move the start date and history to 1900 once you finish all
these batches youve scheduled, like a pre research batch, along with the
history, i feel like that will let the historical run right? and prepare the
game fro the research batch, basically 1900 start date with todays tech and
prices lol or do you think better to start with 1900 prices?"

It runs after the night line of 2026-10-30 (WN4, CD7, XR4, CD8 in lane A;
LB1-LB3 and KP1 in lane B) and before R0. R0 keeps the 1900 economy itself
(`docs/studies/research-1900.md`): the real wages, the basket, the build list
and the equipment.

## What it is

- **The calendar.** `CityCalendar.EPOCH_YEAR` is 2000. It becomes 1900, and
  every reader of the date with it: three files outside `CityCalendar` read
  the constant, and more read the year.
- **The history.** Historical replays the real world of 2000-2025: the
  world file's series (`WorldData`), WN4's moneys and XR4's policy rates.
  From 1900 it needs 1900-2000 too, or a rule for the century before its
  data. The moneys' history is mostly fixed rates: the gold standard to the
  1930s, Bretton Woods to 1971, then floating. The study is the batch's
  first step: what each series needs, and from which sources.
- **The tech stays today's** (his words): today's buildings, recipes and
  equipment, until R0-R5 bring 1900's.

## The call: the price level (settled)

Jerus, 2026-10-30, on the recommendation: "1900 price level". So: **today's real economy, priced at 1900's level.** Every nominal
anchor (the world's prices, the founding wages, the building costs, the
opening money) is scaled by one factor, the price index's 1900/2025 ratio
(roughly 1/30 to 1/40 for the US$; the study names the source and the
figure).
- Why: the game's own inflation then compounds from 1900's level and arrives
  near today's prices in the 2020s. From today's level it would arrive at 30
  to 40 times today's (the currency reform trims the digits, not the story).
- The real side (a quarter of today's real wages, a tenth of the income per
  head) is R0's, because that is what the research changes.

## Checks

Every trace moves: the full suite, the ensemble, and the harnesses that
assert a year or a dollar literal re-premised to the constants (README, The
checks), with none of their premises moved. Old saves: by CLAUDE.md's rule, a
save that would now load wrongly is refused (raise `SAVE_FORMAT` and
`OLDEST_LOADABLE_FORMAT` together).

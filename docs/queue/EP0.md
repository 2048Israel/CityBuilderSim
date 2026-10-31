# EP0 - EP1's study: the history and the price level from 1900

Queued and started 2026-10-30, at night, on Jerus's word: "might as well run
another subsession that does the research ep1 will need for its batch? that
way ep1 has less work?". It runs beside the night line, so EP1 starts with
its sources found, its figures gathered and its scope mapped. It changes no
code. [EP1](EP1.md) is the batch it serves; read it first.

## What it finds

1. **The price level factor** (Jerus: "1900 price level", 2026-10-30). Find
   the index's 1900/2025 ratio for the moneys the game prices in: the US$,
   which the world file's prices are in, and Canada's $, since money follows
   Canada's history (`research-1900.md`, the calls). Give each figure with
   its source and how the years before an official CPI are estimated. Then
   list every nominal anchor the factor must reach, as a code survey with
   file:line and no edits: the world's prices, the founding wages, the
   building costs in `buildings.json`, the opening treasury and loans, and
   any dollar literal a dial or harness holds.
2. **Historical from 1900.** List every series Historical replays today:
   `WorldData`'s nation paths and price paths (2000-2025), WN4's moneys and
   rates (`spec-nations.md` §9, `spec-exchange-rates.md` §3.7; WN4 is being
   built tonight), and XR4's policy rates (`spec-exchange-rates.md` §5). For
   each, find 1900-2000 sources: the Jordà-Schularick-Taylor Macrohistory
   Database, the Maddison Project, gold-standard and Bretton Woods parities
   with their devaluation dates, and national statistics. Say which series
   reach 1900, which start later, and the rule for the gap.
3. **Nations that did not exist in 1900.** The world file has 2025's 196
   nations. Propose a rule for the years before a nation existed: its later
   territory under its 1900 ruler, or held at its first year. Give the
   evidence and a recommendation; the call is EP1's.
4. **The calendar.** What reads `CityCalendar.EPOCH_YEAR` or a calendar
   year, and what a 1900 epoch moves: the readers, the save's dates, the
   screens' axes, and the harnesses that assert a year.

## What it leaves

- `docs/studies/spec-1900-start.md`: the findings, each figure with its
  source key, and the decisions EP1 must take, each with a recommendation.
- `docs/studies/1900-start/`: the gathered series as CSV, each with a header
  naming its source and units, ready for EP1 to bake.
- An entry in `docs/studies/README.md`, and EP1's brief pointed at the study.

Docs only: shipped as a study (`--no-tag`).

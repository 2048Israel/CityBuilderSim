# WP1 notes (0.9.79, 2026-11-09)

World prices chained, a study (WP1's row in `QUEUE.md`; CU1's ★1 as Jerus
took it, 2026-11-09). No model change: every changed path ends in `.md`.

## What it found

- The note is `docs/studies/spec-world-prices.md`, its row at the top of
  `docs/studies/README.md`. The prototype and the probes are text in
  `docs/studies/world-prices/probes.md`, what they printed is
  `world-prices/tables.md`, and the refining charge's record with each
  source's terms is `world-prices/tcrc.md`.
- The chained January works: a made good at its inputs' cost in that nation
  plus a conversion. Rod stays on its cathode (1.00 of the literals' ratio
  over thirteen worlds and 330 years; main 2.4 by 2100, 2.7 by 2200), all 60
  goods calibrate as today, and the chain costs no time.
- "The world's price" of an input has to be the cheapest maker's, landed. The
  nation's own index and the world's mean leave the spread open.
- The price file's one-parent table (★2 and ★3 as worded) mends copper only: a
  Blast Furnace ends up paying 1.05-1.27 of its sales for its feed. The works'
  recipes with the conversion on the manufactures index hold every works near
  its literal. That widens ★2 from 27 goods to 40 (call ★1).
- The path anchor (CU1's ★5) is the larger half: main's copper at a city falls
  to 0.38 of its path by 2100; with each root's reference band held on its
  path it stays at 1.01. It costs 11-23 ms a January as prototyped and moves
  the copper nations' terms of trade (DR Congo's -0.44 to +0.08 at 2100).
- Chained, no seed has a copper industry: no smelter, no refinery, one Rod
  Mill at a loss on seed 1, three to five Wire & Cable Works. The city lives
  on fabricated steel and cars (86-98% of GDP exported), as well off as main's
  to month 1800 and at about half its three copper seeds' income a head at
  month 2400. DS1's dead sector is the prices, on every seed.
- No yearly record of the refining charge may ship as a data source; the
  benchmark is published only by firms that sell it. The charge is money
  (12-30 cents a pound, 1994-2024), not a share of the price.
- Main's `solve` has no guard against an iterate that is not a number: on
  drawn worlds 9 and 10 crude's price is not a number from 2165 and from 2191
  on (106 of 4,290 Januaries stop at 2,000 sweeps, each saying so in the
  log). No red recorded for main (`docs/notes/gate.txt`) names it; the world
  probe found it.

## Decisions

- Measured on main at 0.9.75 (b69bc612), the branch's own base. Main at
  0.9.77 was merged before the push; `WorldMarket.java`, `Nations.java`,
  `LongPlaytest.java`, `buildings.json` and `tools/world/` are the same files
  in both.
- The prototype (`chain.py`) writes a copy of `WorldMarket.java`; it ran the
  world probe and `LongPlaytest` only. **No harness was run on the patched
  class**: `WorldMarketCheck`, WN4's checks and the reload are CU2's.
- With no switch set the copy plays as main (the four seeds' yearly rows are
  byte-identical), so main's column is the same code path.
- The anchor's first form, a fixed-point step, ran away on one good
  (fabricated steel's floor to nothing); it was replaced by a bracketed
  root-find and every anchored run was done again. The four cities played on
  the first form were thrown away and are in no table.
- The charge was first prototyped as a share of copper's price and recast as
  money once the research showed it flat in cents; the tables are the money
  form's.
- The solve's guard goes into CU2 (the same method, and CU2 is next), not a
  row of its own.
- **CU2's row in `QUEUE.md` stands and the ship file queues nothing.** Its
  brief is the study's section 9: the orchestrator's edit is the row's link
  (`docs/studies/spec-world-prices.md#9-cu2s-brief`), and, as ★1 and ★3 are
  taken, its words "every good the price file prices off another" (40 goods
  under ★1b) and the anchor.
- `licences.md` gets no row here: no figure reaches the game in WP1. CU2 adds
  its row first if a record ships (★4a). The research note reads the USGS
  chapters' figures as shippable and the study does not (they are credited to
  CRU); `tcrc.md`'s head says so.
- Sources that will ship under any call: the World Bank's Pink Sheet and its
  manufactures index, both already in `licences.md` (CC BY 4.0). Evidence
  only, never shipped: USGS and Bureau of Mines yearbooks, the Canadian
  Minerals Yearbook, Cochilco, Teck's decks, CRU, Fastmarkets, SMM, Reuters.
- Three of the 24 city runs ended on a layout finding, none a price's: a
  district's boxes moved on main's own seed 2 (month 2397) and on seed 3 under
  the price file's table (month 1802); industry farther out than the homes on
  seed 2 under the cycle (month 2400). The first is main's, unpatched.
- No trace moves (no model change): no ensemble. Not a process change (one
  in, one out does not apply). No save was read and no slot written.

## The calls for Jerus (the study's section 8)

| call | recommended |
|---|---|
| ★1 what a made good is made of: the price file's one parent each, or the works' recipes with the conversion on the manufactures index | the works' recipes (40 goods; wire stays 70% rod) |
| ★2 what an input costs a nation: the cheapest maker's landed price, the nation's own index, or the world's mean | the cheapest maker's, landed |
| ★3 the path anchor (CU1's ★5): as now, the reference city's band on the path, or supply grown with the world's use | the reference city's band, in CU2 |
| ★4 the charge's record: the yearly benchmark as facts (LC1's "facts are nobody's"), no real year at all, or ask the publishers | the benchmark as facts if Jerus reads it so; the game's own swing if not. Not the orchestrator's to settle |
| ★5 the charge's form: a share of copper's price, or money on the manufactures index | money |
| ★6 which year's charge the literals stand for: 2024's 5.4%, or 2025's 1.3% | 2024's |
| ★7 Copper Refining with no works that pays on imports: accept it, or widen the works' margins | accept it; CU2's fixture city with a mine proves the chain |
| ★8 what carries the city next: a study of Manufacturing's and Automotive's margins, or leave them | a study, queued once CU2's ensemble shows the same two sectors (MA1 is a free name) |

## Checks

- `./batch-check.sh --fast`: exit 0.
- `git diff --name-only origin/main`: every path ends in `.md`;
  `git ls-files --eol` shows them `i/lf`.
- The scratch folder (`target/wp1/`, git-ignored) is removed; everything it
  held that the study rests on is in `world-prices/`.

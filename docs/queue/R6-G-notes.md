# R6-G notes (2026-11-16)

The catalogue's years 1996-2024, a study (`docs/studies/research-1996-2024.md`,
with the gatherers' fact sheets in `docs/studies/research-1996-2024/`). It
makes no model change and adds no `research.json` row: every changed path
ends in `.md`. The trace is main's, so no ensemble was run.

## Session

- Branch `batch/R6-G`, cut from main at 08c8727d (0.9.128), in the worktree
  `cl-R6-G`. Run headless through MemWatch's launcher on Opus 5.5.
- The web research went to four Sonnet agents at xhigh, one per area: energy;
  industry, mining, farming and transport; medicine; business and law. They
  covered 57 subjects, starting from the near-future study's §6. Each subject
  went to a fact sheet, with its sources and with each figure marked as seen
  on the page or only in a search result. The sheets are committed as
  gathered (134 KB), with one line added under each title.
- The four ran in the background and had all finished before the
  orchestrator's 13:44 rule (foreground subagents only). Nothing ran in the
  background after it.
- A few dates and figures were looked up again from this session ("Checked
  here").

## What the study found

1. **Thirty rows stand**, each with a first commercial use, a measured effect
   and a readable source: twelve engineering (E21-E32), twelve medicine
   (M11-M22), two business (B9, B10) and four law (L6-L9). The other
   twenty-seven subjects are in the study's §4, by why they are out: nothing
   in the game to act on, no measured step, a law Canada never had, or a date
   before 1996.
2. **Thirteen rows have something behind them today**: E21-E24, E26, E27,
   E30 and E32 (R6e), and M12, M19, B9, B10 and L8 (R6f). The other seventeen
   rows are marked for their owners' batches.
3. **The medicine of these years acts on causes the game stops counting.**
   `CausesOfDeath.KNOTS` takes typhoid, diarrhoea, the outbreaks, TB,
   pneumonia and childbirth to 0 by 1980, and there is no cancer. MED1's
   scope (diabetes, kidney failure, heart disease, polio) reaches only M14,
   M21 and L6.
4. **R6b's rows**: FE5 needs E29, FM1 needs M20, and FM2 needs M20 and M17.
5. **Research-design's reserves**: GM crops [T44] becomes E21 and the bigger
   turbines [T49] become E24. Reverse osmosis [T52] takes E33, because E22
   needs it. Slickwater fracturing [T23] stays out, because the Earth file
   has no tight fields.
6. **E27's figure is against the wrong baseline.** The -45% is against
   conventional casting and rolling, and the game already has the thin-slab
   caster (E18). R6e builds the row only if it finds the step against E18.
7. **The work rule holds.** From 1996 to 2024 a building rises from 4,303 to
   18,271 researcher-years.

## The calls (★)

Each call comes with a recommendation. The study's §7 gives the reasons.

1. **★1 Codes**: E21-E32, M11-M22, B9-B10 and L6-L9 in date order, with E33
   for reverse osmosis and any later 1951-1995 row after it. Gaps left for
   1951-1995 would need a guess at how many rows are coming.
2. **★2 The causes past 1980**: MED1 keeps pneumonia, children's diarrhoea
   and TB past 1980 at Canada's rates. Otherwise M11, M15, M18 and M22 have
   nothing to act on.
3. **★3 A cancer cause** in MED1's scope, for M13, M16, M17 and R6b's FM2.
   Cancer is Canada's first cause of death. ★6 rules out a row with nothing
   behind it.
4. **★4 Storage**: a batch after PW3, queued when PW3 ships, for E29 and
   FE5. spec-electricity kept storage out of PW2 and PW3.
5. **★5 M20 enters with FM1**, as a prerequisite that moves nothing itself.
   No study has asked for a COVID cause of death.
6. **★6 Two dates**:
   - The AI assistant takes November 2020, its first measured deployment,
     not ChatGPT's release in November 2022. The row gives the measured
     gain, and only that deployment measured it.
   - The large-rotor turbine takes LBNL's 2004 step. No first installation
     of the class exists, and the step is where the capacity factor moves.

## Checked here

| what | found | so |
|---|---|---|
| PCV7's date | the CDC's MMWR (rr4909a1, page) gives February 2000 only; 17 Feb is a snippet | M11 Feb 2000, H1 |
| Manitoba's ban | 1 Oct 2004 on the province's archived page (Library and Archives Canada's web archive) | L6 on that day, H2; a page mark, no longer a snippet |
| Ipilimumab | 25 Mar 2011 in the Society for Immunotherapy of Cancer's release of the day; median survival 10.0 / 10.1 against 6.4 months, 676 patients | M17 on that day, H3 |
| The work figures | by the rule in awk; the same line returns M7's 1,013, M8's 1,162, B3's 962 and L2's 849 | the study §3.2, §6 |
| The game's handles | `Haulage.DIESEL_LITRES_PER_100_KM` 33, `Bank.CAPITAL_RATIO` .08, the Desalination Plant since E6 (flash, 13.5-25.5 kWh a m³), `OrderBook` with no tick, the buildings named in the rows (`buildings.json`) | the rows' "waits on" |
| The owners | MED1, PW2, PW3, HO4 and HT1 in main's `QUEUE.md`; R6e and R6f free (`tools/batch-id.sh`) | §8 |
| The sources | every key cited in the text is in §9, and §9 lists none that the text does not cite (the FDA's empty Prevnar archive was dropped) | §9 |

Not checked again: every figure marked snippet in the sheets. R6e, R6f and
the owners open a figure's page before the figure goes into `research.json`,
as R6b does.

## The checks

`./batch-check.sh --fast` on the branch, with the data paths in
`JDK_JAVA_OPTIONS`: exit 0. Compile OK, the maps regenerated with none
changed, Stale 0 firm findings (186 soft), and the tree clean apart from the
notes and the ship file. `git diff --name-only origin/main` lists only `.md`
files. No harness reads what changed, so no `--pick`.

## changelog
### {next} — LB1, Rankings: the measures file — 2026-10-31, ready, see docs/queue/LB1-notes.md
The real world's latest figures for the Rankings' Real world 2025 board, baked into the resources: `world-2025.json`
holds 196 nations and 62 city regions on the study's 70 measures, each figure with its year, its mark (● published,
◐ a projection, ○ the game's estimate, with the rule that made it) and its source, and no 0 for a missing one; GDP in
current, constant 2015 and PPP dollars, the cities' shares of their nations, and the CPI-U 2015-2025 that turns any
year's dollar into a 2015 one. From the WDI (49 series, each CC BY 4.0), the OECD, the BIS, the UN's WUP 2025, the EIA
and the BLS, every licence read first; 55 measures have real figures and 15 rank only your cities (nothing a game may
ship publishes them; UNODC's prisoners and police wait on Jerus). `tools/world/measures.py` builds it beside WN0's
pipeline; `WorldMeasures` reads it and `WorldMeasuresCheck` holds it to its contract, 36 one-place breakages each
caught. Nothing reads it yet (LB2), so the trace is main's and no ensemble ran.
## pending
| LB1-U | Prisoners and police on the Rankings: UNODC's figures per 100,000 people (187 and 90 nations since 2015), downloaded, their pointers written in the sources folder's MEASURES-SOURCES.txt ({next}) | Jerus's call: UNODC's data portal now links terms allowing "personal, non-commercial use" only, where its older data terms let anyone copy the figures with credit; recommended: leave them out until UNODC says a game may ship them | [LB1 notes, ★7](docs/queue/LB1-notes.md) |

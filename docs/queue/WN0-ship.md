## changelog
### WN0 — the nations' world file: 196 nations, their borders, trade by pair and good, paths 2000-2100, sea distances, monthly rates — 2026-10-28, ready, no tag (data nothing reads yet), see docs/queue/WN0-notes.md
`tools/world/`, a pipeline in Python's standard library, bakes `world-1.bin` (8.7 MB, not in git: SHA-256 in `tools/world/world-1.sha256`, the release `world-1` as earth-2's, `get-earth.bat nopause world-1` fetches it). It holds the 193 UN members, Taiwan, Hong Kong and Macau, and 15 areas no nation of them holds. Its ten layers:
- the owner of every 15" cell, from Natural Earth's borders, and the border lines;
- each nation's node, label, money and region; people 2000-2100 (WPP 2024), and output, CPI and rate 2000-2025 (WDI; PWT and Maddison fill, marked);
- 158 moneys' monthly rates 2000-2025 (BIS, ★11);
- 2024's trade by pair and good (BACI HS22 through a concordance to the 60 traded goods), kept by Jerus's partner rule (93.6-99.9% of the value), with make, use and the preferences the base year's solve reproduces exactly;
- CERDI's freight closed under the triangle rule, and each good's real price path for WN1.
`WorldData` reads it; `WorldDataCheck` checks it on a twelve-nation fixture and the full files. The playtest's trace is unchanged.
## pending
| WN0-R | WN0 (no tag): the release `world-1` with `world-1.bin` (baked at `C:/Users/Jerus/Documents/world-sources/work/out/world-1.bin` on the PC, SHA-256 in `tools/world/world-1.sha256`), as earth-2's: `gh release create world-1 <that path>`. Until then `WorldDataCheck`'s full-file sections run only where `-Dcbs.world` names the file | Jerus's release, or his yes to the orchestrator | [WN0 notes](docs/queue/WN0-notes.md) |
| WN0-M | WN0 (no tag) estimated what each nation makes from its use, trade and output (★2): FAOSTAT (food, wood), USGS (ores, metals) and EIA (crude, products, coal) were not among its downloads. Their figures in the pipeline and a re-bake, before WN3 prices the world on them. Recommended: yes, before WN3 | the downloads' yes (FAOSTAT CC BY 4.0; USGS and EIA public domain) | [WN0 notes](docs/queue/WN0-notes.md) |

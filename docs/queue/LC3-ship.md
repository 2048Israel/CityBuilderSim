## changelog
### {next} — LC3, the nations' world without the IMF's figures — 2026-11-02, ready, see docs/queue/LC3-notes.md
world-1.bin is baked again with no figure a sold game may not ship, so the nations can reach players (0.9.0 went out
without them, IR1). Each nation's price path is now the World Bank's GDP deflator, and the US's is the BLS's CPI-U, the
dollar the game's own price level is. ILOSTAT's consumer prices were the first choice, but every one of them carries
"Repository: International Monetary Fund". A money's rate in a year the BIS has no month of it is PWT 11.0's, then the
Bank's GDP in local money over GDP in US$, which gives Somalia, Syria and Sudan their published rates. The goods' prices
layer divides by the CPI-U and moves under 0.2%. The new file has its fixture, sha, credits and licence rows; Build
EXE.bat fetches it and puts it beside the exe. The playtest's trace moves, so the ensemble ran: 22 of 26 against 23.
A latent map fault the new trajectory met is mended: houses on a resource field walled a street piece in (MapCheck 8;
MF2's EM1-M), and PlanCheck 18 is its fixture.
## pending
| LC3-R | The nations to players ({next}): copy `C:/Users/Jerus/Documents/world-sources/work/out-lc3/world-1.bin` (SHA-256 fbd11a6a…c1) over `data/world-1.bin`; publish the `world-1` release with the `gh release create` line in LC3-notes §2 (`--latest=false`, so 0.9.0 stays the latest); then IR2 (queued) builds and pushes it to itch.io, `Build EXE.bat` now stopping without the file | The release and the build are the orchestrator's after the gate (WN0-R; Jerus, 2026-10-31: "After LC3 (Recommended)") | [LC3 notes §2](docs/queue/LC3-notes.md) |
| LC3-C | Eye-check LC3 ({next}) in the built exe: data/world-1.bin and data/world-ATTRIBUTION.txt beside it; a Historical city's nations on the globe, Trade › Partners filled, a nation's page with its prices; no "Not yet" in the credits | Not looked at in the running game | [LC3 notes §2-3](docs/queue/LC3-notes.md) |
| LC3-L | LC3's calls ({next}): ★1 the household consumption deflator tracks consumer prices far better than the GDP deflator (Iraq 0.02 against 1.13, Kuwait 0.02 against 0.58) - for WD1's re-bake and LC2; ★2 Zimbabwe Gold's 2025 rate is the Bank's 31.80; ★3 HIGH's R2 in the ensemble (median -1.22 points, at least +0.50 asked; seeds -12.8 to +10.8) | Recommended: ★1 the consumption deflator at WD1's re-bake and in LC2 (its two series read into licences.md first); ★2 as done; ★3 ship, read R2 again at WD1's ensemble | [LC3 notes §4](docs/queue/LC3-notes.md) |

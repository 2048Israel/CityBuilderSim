## changelog
### {next} — WN5, land bought from its nation — 2026-11-01, ready, see docs/queue/WN5-notes.md
Jerus: "Borders baked in; land bought from its nation", "land costs different prices purely by who you are buying it from".
On the real Earth each offer is sold by the nation its ground lies in (world-1.bin's owner layer; an area or no one sells as
the host; a foreign nation's sea only within 12 nautical miles of its land), priced at today's formula times that nation's
land factor, its land against Canada's (exactly 1): 25 published farmland prices (Eurostat, USDA, Statistics Canada, 2024)
and a fit on output a head and people a km2 for the rest (land-2024.json), taken back to 1900 on the file's output a head
and moving with it. An offer across a border is split, each part priced and paid to its own nation, from the vault's
holding of its money first; the receipt and the offer card name them ("Sold by United States 62% · Canada 38%"), and what
the city bought from whom is kept for WN6. A generated world is the host's at factor 1: the default playtest's eight trace
files byte-identical to main's, no ensemble. A Rotterdam city pays about 11 times Halifax's and ends a third smaller.
Suite green (exit 0, 167 harnesses); LandNationsCheck new: Historical meets the published ratios in 2024 to 2.7e-16.
## pending
| WN5-L | Land at its real price ({next}): a Rotterdam city pays about 11 times what a Halifax city pays (the Netherlands' published ratio, 10.9) and over 4,005 months buys 83 km2 of dry ground against main's 135 and ends at 315,000 people against 471,000; Japan's factor is 3.8, Germany's 4.4, the US's 1.35, Brazil's 0.3. ★ Recommended: keep the real ratio undamped (the study's spread, 6.3, is what was asked); a dial on its exponent is a one-line batch if a played European city feels starved of land. | his call | docs/queue/WN5-notes.md §2 call 9, §4 |
| WN5-C | Eye-check WN5 ({next}) on an Earth city at a border (Windsor, 42.30 -83.03): the land office's card line "Sold by United States 62% · Canada 38%" under an offer across the Detroit River and "Sold by United States" under one wholly across it, nothing on Canada's own offers or on a generated world; the receipt's "Sold by ... (62%, US$...) and Canada (38%, C$...)"; the dearer prices across the river. | needs his eye | docs/queue/WN5-notes.md §1 |
| WN5-M | The manual's land office ({next}): land is sold by the nation it lies in, at its land factor against Canada's, an offer across a border split and paid to each nation in its money; the page changed, published and pulled (the docs pass). | docs pass | docs/queue/WN5-notes.md §1 |
| WN5-R | Found for WN6 ({next}): WN4's real effective rate (`Game.realEffectiveRate()`) measures each partner by its money's rate against that money's rate at the founding over its prices since the founding, so a partner that changed money since 1900 (Zimbabwe, Venezuela, the euro members that joined after 2000) is misread: the same fault that made WN5 take the money out of the land factor (call 4). Small in the index (by trade weight), shown by WN6. ★ Recommended: chain each partner's rate across its changes of money (the conversion at the changeover month) in WN6 or a small batch before it. | his call | docs/queue/WN5-notes.md §5 |

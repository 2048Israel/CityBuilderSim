<!-- gate-run: /tmp/cbs-gate-LB5-B-ZgMNnzA6 -->
## changelog
### {next} — LB5-B, a slot's previous city kept whole while its history lands — 2026-11-18, ready, see docs/queue/LB5-B-notes.md
While a saved slot's header month and its history's last month disagree (a save landed before its history, SaveWriter's order, or its
history write failed, LB3-S), the Rankings board keeps that slot's last whole city, header and history together, instead of the new
header's name and land over the old history's figures. A whole slot's two months are equal, so a healthy slot reads as before; a slot
first read with them apart is read as it stands (no whole city of it exists). The held row is read again only when a file moves (the
stamp), so a history that never lands leaves the slot at its last whole city until the next save lands both. The mend is
`SavedCities.keepWhole`; the screen is untouched, nothing new is saved and `SAVE_FORMAT` is unchanged. `RankingsCheck` 21 holds the row
between the save and its history and 22 the failed history, a second save, the landing, a newer build and a deletion; 8 assertions
fail with the mend removed. No model change: the default trace is unmoved, no ensemble.

## known-reds
KeptCheck | Construction, Cash & debt as a statement: the redraw and the next equal a page built new | KRT1 | 2026-11-18 | Section 49 (the Sectors tab) in LB5-B's gate on main 640b7aefb: the kept statement's row 278 is 836x24 against 836x16 built new, the KRT1 class (kept and new layout bounds apart). LB5-B touches only SavedCities and RankingsCheck; the Sectors tab never reads SavedCities, and CPB1's run on the same main kept this page.
KeptCheck | a month's redraw after each choice hands back nine in ten of the page's nodes, or more | KBP1 | 2026-11-18 | Section 57 in LB5-B's gate on main 640b7aefb: after "Profit, the Summary view" one month made 94 nodes and handed back 204 (68.5%), KBP1's finding (93 made in R6b's dry run). LB5-B touches only SavedCities and RankingsCheck; the Bank Overview never reads SavedCities.
KeptCheck | ...the property rate staged: the redraw and the next equal a page built new | KRT1 | 2026-11-18 | Section 77 (the Policy hub) in LB5-B's gate on main 640b7aefb: a hidden label is 0x13 kept against 0x0 built new, the KRT1 class. LB5-B touches only SavedCities and RankingsCheck; the Policy hub never reads SavedCities.

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

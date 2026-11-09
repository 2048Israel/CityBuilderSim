## changelog
### {next} — LB5, the city in play is matched on its founding cash too — 2026-11-09, ready, see docs/queue/LB5-notes.md
LB4's review follow-ups. `SavedCities.Played` took a saved slot for the city in play on its name and world seed alone,
so a re-found on the same name and world (a second try) was marked "this city, as saved" beside the city it replaced;
it now compares the founding cash as well, read by `SaveHeader` from the `foundingCash` key every save already holds
(nothing is added to the save; a save without the key never matches). RankingsCheck 19 gains a re-found and a save with
no founding cash, neither marked. The review's second note, that a slot's stamp misses the header, is not a gap: the
header is the save file parsed, so its size and time are in the stamp already; RankingsCheck 21 proves each file alone,
and a save landing before its history, moves the stamp and the row follows. The window the note describes (the new
header over the old history until the history lands) is SaveWriter's order and stays, as LB5-B. The third, `placeSaved`
per redraw, was not taken (it is behind the cache check; nothing to measure). The default trace is main's: no ensemble.
## pending
| LB5-B | While a slot's header month and its history's last month disagree, keep the slot's previous city whole instead of the new header over the old history (a name over another city's figures until the history lands) ({next}) | a change of what the board shows (a slot whose history write failed, LB3-S, would keep its last whole city, not its new save); recommended, with its fixture (RankingsCheck 21's two steps, the row held between them); a healthy slot's two months are equal | [LB5 notes](docs/queue/LB5-notes.md) |

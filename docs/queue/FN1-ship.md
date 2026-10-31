## changelog
### {next} — FN1, the Found-a-city name starts blank — 2026-10-30, ready, see docs/queue/FN1-notes.md
Jerus: "remove the default name of danzik, have it just blank, or if you search for a city, its that city name (unless
someone already typed something, then it doesnt change the city name)". The Found-a-city page's name field opens blank
with its prompt, "Name the city", showing, at the first open and at every reopening from the main menu, not on Danzik.
A place picked from the Earth search (a hit clicked, or Enter in the search) names the city with that place's name,
cut to 24 characters, while the player has not written a name; the next place searched replaces a name the search
filled, a name the player typed (or changed from the search's) stays, and a field the player clears is filled by the
next place again. Found with the field blank waits: it is the model's refusal, "Name the city.", under the greyed
button, as it was for any cleared field. The rule is `FoundingName`, outside the screen (NewGameCheck 16). Danzik stays
in the model: `Founding.DEFAULT_CITY_NAME`, old saves, the harnesses. A screen change: no save, no money, the trace is main's.
## pending
| FN1-C | Eye-check the Found-a-city page ({next}): open it from the main menu and the name field is blank with "Name the city" showing though it has the focus, Found greyed with that hint under it; on Earth search "Perth" and click the hit: the field reads Perth and Found lights once the card is measured; search "Lima": it reads Lima; type "Arden", then search a place: Arden stays; clear the field, search again: it fills; reopen from the main menu: blank again. No JavaFX run on the PC the night it was built | Try it once on the PC; any miss goes back to FN1 | [FN1-notes](docs/queue/FN1-notes.md) |
| FN1-R | Random spot and a click on the globe do not name the city ({next}): the brief says searched, so a player who rolls a place or clicks one with the field blank sees "Name the city." and types it; Random spot does pick a named place (`spotPlace`) | Recommended: leave them; a place rolled by the dice is not one the player searched for. The other way: Random spot names the city too, on the same rule (a small change in `randomSpot()`) | [FN1-notes](docs/queue/FN1-notes.md) |

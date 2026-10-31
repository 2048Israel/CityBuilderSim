# FN1 - the Found-a-city name starts blank (0.8.79, 2026-10-30)

The record of a small screen fix, run whole on Sonnet 5.5 at xhigh, no subagents
(BP1 item 7). Jerus, 2026-10-30: "remove the default name of danzik, have it just
blank, or if you search for a city, its that city name (unless someone already typed
something, then it doesnt change the city name)". No save change, no money, nothing
the month reads, the trace is main's.

## 1. What was there

`FoundingScreen` opened its name field on `Founding.DEFAULT_CITY_NAME` ("Danzik"), at
the first open and again in `show()`, the reset from the main menu. A place picked
from the Earth search set the spot and nothing else; the city stayed "Danzik".

## 2. What changed

- **`FoundingName`** (new, `ham.citybuildersim`, a pure model so a harness can test
  it; the screen only tells it each change): the text, and whether the player wrote
  it. Blank at first. `edited(now)` is a change in the field: a non-blank text is the
  player's, a cleared or space-only field is nobody's again. `placePicked(name)` puts
  a place's name in unless the player's name stands, and a name it put there is
  replaced by the next place picked. A change to the very text the class last set is
  the field echoing it (`setText` fires the listener), not an edit: that is how the
  screen can fill the field with no flag round the call.
- **`FoundingScreen`**: `cityName` starts "" and `show()` resets the model, so the
  field opens blank with its prompt, "Name the city", showing (the stylesheet's
  `.text-field` gives the prompt its colour, and it overrides Modena's transparent
  prompt on a focused field). `pickPlace()` (a hit clicked, or Enter in the search)
  asks the model and, when it took, sets the field. Random spot and a click on the
  globe leave the name alone: the brief says searched. The model's `cityName` field is
  kept as a mirror so the lines that read it (`currency()`, `generatedChoices()`) are
  untouched; WN4, removing the inflation chips from the same file, edits
  `generatedChoices()`.
- **Left alone**: `Founding.DEFAULT_CITY_NAME`, `Founding.legacy()` and "Danzik" in the
  model, `SaveHeader`'s fallback, `LongPlaytest`'s founding: old saves load as Danzik
  and the harnesses found cities by it.

## 3. The blank-name start (a call Jerus left open)

His words say blank; they do not say what Found does with a blank. Two ways: the
start button waits for a name with a short hint, or the chosen place's name is used.
**The button waits.** `Founding.problem()` already refuses a blank name ("Name the
city."), `FoundingScreen.problem()` already asks it first, and the screen already
greys Found and prints the reason under it, so a blank city never reaches
`ui.foundCity`; the currency, `Currency.fromCityName`, needs a name to derive from.
A silent fallback to the place's name would also name a city the player never looked
at, and with the field blank the player has searched nothing (a place picked fills
the field), so the only way to a blank field with a spot chosen is a click on the
globe or Random spot, where the hint is the honest answer. No new code for it;
section 16 asserts the refusal.

## 4. The check

`NewGameCheck` section 16 (the fixtures cause each case): the page opens blank, not on
Danzik, and a blank name is the model's refusal; a place picked names the blank city,
which then founds with its money named after the place; the field's echo is not an
edit; the next place replaces a name the search filled; a name the player typed, or
changed from the search's, stays through any number of searches; clearing the field
(or leaving spaces) lets the next place fill it again; `reset()` blanks a typed name;
a place name longer than 24 characters is cut, and one with no letter leaves the
field blank; and on EarthCheck's fixture the search finds Rotterdam, whose name makes
a city, as does every 53rd place of the data's 7,000-odd.

## 5. Not checked here

The window itself: no JavaFX run on the PC tonight (memory). FN1-C in the ship file
is the eye-check.
